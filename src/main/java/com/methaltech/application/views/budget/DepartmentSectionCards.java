package com.methaltech.application.views.budget;

import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.SectionBudget;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.views.actual.utilityActuals;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import java.math.BigDecimal;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

public class DepartmentSectionCards extends VerticalLayout {

    private final NumberFormat currencyFormat;
    private final DepartmentBudget department;
    private final BudgetService budgetService;
    private final Budget budget;
    utilityActuals utils;

    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;

    @Autowired
    public DepartmentSectionCards(DepartmentBudget department, BudgetService budgetService, Budget budget, CoaService sampleCoaService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, SALFLDGService sampleSALFLDGService) {
        this.department = department;
        this.budgetService = budgetService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.budget = budget;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("department-section-cards");

        createHeader();
        createOverviewCard();
        createSectionCards(budget);
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);
        header.addClassName("section-cards-header");

        VerticalLayout headerText = new VerticalLayout();
        headerText.setSpacing(false);
        headerText.setPadding(false);

        H3 title = new H3("Section Budget Analysis");
        title.addClassName("section-cards-title");

        Span subtitle = new Span("Comprehensive breakdown of budget allocation across sections");
        subtitle.addClassName("section-cards-subtitle");

        headerText.add(title, subtitle);

        HorizontalLayout headerActions = new HorizontalLayout();
        headerActions.setAlignItems(FlexComponent.Alignment.CENTER);
        headerActions.setSpacing(true);
        headerActions.setPadding(false);
        headerActions.addClassName("header-actions");

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        refreshButton.addClassName("header-refresh-button");
        refreshButton.addClickListener(e -> {
            showNotification("Section data refreshed", NotificationVariant.LUMO_SUCCESS);
        });

        Icon sectionsIcon = new Icon(VaadinIcon.GRID_BIG);
        sectionsIcon.addClassName("section-cards-icon");

        headerActions.add(refreshButton, sectionsIcon);
        header.add(headerText, headerActions);
        add(header);
    }

    private void createOverviewCard() {
        Div overviewCard = new Div();
        overviewCard.addClassName("section-overview-card");
        overviewCard.setWidthFull();

        VerticalLayout overviewContent = new VerticalLayout();
        overviewContent.setSpacing(true);
        overviewContent.setPadding(false);
        overviewContent.setWidthFull();

        H4 overviewTitle = new H4("Department Overview");
        overviewTitle.addClassName("overview-title");

        // Metrics grid
        HorizontalLayout metricsGrid = new HorizontalLayout();
        metricsGrid.setWidthFull();
        metricsGrid.setSpacing(true);
        metricsGrid.setPadding(false);
        metricsGrid.addClassName("overview-metrics");

        metricsGrid.add(
                createMetricItem("Total Sections", String.valueOf(department.getSectionCount()), VaadinIcon.BUILDING, "metric-sections"),
                createMetricItem("Budget Utilization", String.format("%.1f%%", department.getSpentPercentage()), VaadinIcon.CHART_LINE, "metric-utilization"),
                createMetricItem("Available Budget B", formatCurrency(department.getAvailableBudget()), VaadinIcon.MONEY_DEPOSIT, "metric-available"),
                createMetricItem("Status", department.getStatusText(), VaadinIcon.INFO_CIRCLE, "metric-status")
        );

        // Department progress
        VerticalLayout progressSection = new VerticalLayout();
        progressSection.setSpacing(false);
        progressSection.setPadding(false);
        progressSection.setWidthFull();
        progressSection.addClassName("overview-progress");

        HorizontalLayout progressHeader = new HorizontalLayout();
        progressHeader.setWidthFull();
        progressHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        progressHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        progressHeader.setPadding(false);
        progressHeader.setSpacing(false);

        Span progressLabel = new Span("Department Budget Progress");
        progressLabel.addClassName("progress-label");

        Span progressValue = new Span(String.format("%.1f%% utilized", department.getSpentPercentage()));
        progressValue.addClassName("progress-value");

        progressHeader.add(progressLabel, progressValue);

        ProgressBar departmentProgress = new ProgressBar();
        departmentProgress.setWidthFull();
        departmentProgress.setValue(Math.min(department.getSpentPercentage().doubleValue() / 100.0, 1.0));
        departmentProgress.addClassName("department-progress-bar");
        departmentProgress.addClassName(getDepartmentProgressClass(department.getSpentPercentage().doubleValue()));

        progressSection.add(progressHeader, departmentProgress);

        overviewContent.add(overviewTitle, metricsGrid, progressSection);
        overviewCard.add(overviewContent);
        add(overviewCard);
    }

    private VerticalLayout createMetricItem(String label, String value, VaadinIcon iconType, String className) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName("metric-item");
        item.addClassName(className);
        item.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon icon = new Icon(iconType);
        icon.addClassName("metric-icon");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("metric-value");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("metric-label");

        item.add(icon, valueSpan, labelSpan);
        return item;
    }

    private String getDepartmentProgressClass(double percentage) {
        if (percentage > 100) {
            return "progress-over";
        }
        if (percentage > 90) {
            return "progress-critical";
        }
        if (percentage > 75) {
            return "progress-warning";
        }
        return "progress-good";
    }

    private void createSectionCards(Budget budget) {
        // Get real sections for the department
        List<SectionBudget> sections = budgetService.getDepartmentSections(department.getDepartmentCode(), budget);

        H4 sectionsTitle = new H4("Section Details");
        sectionsTitle.addClassName("sections-title");
        add(sectionsTitle);

        VerticalLayout cardsContainer = new VerticalLayout();
        cardsContainer.setWidthFull();
        cardsContainer.setPadding(false);
        cardsContainer.setSpacing(true);
        cardsContainer.addClassName("section-cards-container");

        for (SectionBudget section : sections) {
            cardsContainer.add(createSectionCard(section));
        }

        add(cardsContainer);
    }

    private Div createSectionCard(SectionBudget section) {
        Div card = new Div();
        card.addClassName("section-card");
        card.setWidthFull();

        // Enhanced card header with status indicator
        HorizontalLayout cardHeader = new HorizontalLayout();
        cardHeader.setWidthFull();
        cardHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        cardHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        cardHeader.setPadding(false);
        cardHeader.setSpacing(false);
        cardHeader.addClassName("section-card-header");

        // Section info
        HorizontalLayout sectionInfo = new HorizontalLayout();
        sectionInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionInfo.setSpacing(false);
        sectionInfo.setPadding(false);
        sectionInfo.setFlexGrow(1);
        sectionInfo.addClassName("section-info");

        // Section icon based on name
        Icon sectionIcon = getSectionIcon(section.getSectionName());
        sectionIcon.addClassName("section-icon");

        VerticalLayout sectionDetails = new VerticalLayout();
        sectionDetails.setSpacing(false);
        sectionDetails.setPadding(false);

        H5 sectionName = new H5(section.getSectionName());
        sectionName.addClassName("section-name");

        HorizontalLayout sectionMeta = new HorizontalLayout();
        sectionMeta.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionMeta.setSpacing(true);
        sectionMeta.setPadding(false);
        sectionMeta.addClassName("section-meta");

        Span sectionCode = new Span(section.getSectionCode());
        sectionCode.addClassName("section-code");

        Div separator = new Div();
        separator.addClassName("meta-separator");

        Span categoryInfo = new Span("Category: " + section.getCategoryId());
        categoryInfo.addClassName("category-info");

        sectionMeta.add(sectionCode, separator, categoryInfo);

        if (section.getDescription() != null && !section.getDescription().isEmpty()) {
            Span sectionDesc = new Span(section.getDescription());
            sectionDesc.addClassName("section-description");
            sectionDetails.add(sectionName, sectionMeta, sectionDesc);
        } else {
            sectionDetails.add(sectionName, sectionMeta);
        }

        sectionInfo.add(sectionIcon, sectionDetails);

        // Status indicator
        Div statusIndicator = createEnhancedStatusIndicator(section);

        cardHeader.add(sectionInfo, statusIndicator);

        // Enhanced budget metrics grid
        HorizontalLayout budgetGrid = new HorizontalLayout();
        budgetGrid.setWidthFull();
        budgetGrid.setSpacing(true);
        budgetGrid.setPadding(false);
        budgetGrid.addClassName("section-budget-grid");

        VerticalLayout spentLayoutCard = createEnhancedBudgetItem("Spent", section.getSpentAmount(), VaadinIcon.TRENDING_DOWN, "budget-spent");

        spentLayoutCard.addDoubleClickListener(e -> {
            String sectcode = section.getSectionCode();
            Set<String> sectionCodes = new HashSet<>();
            sectionCodes.add(sectcode);
            //Set<UrcDeptSectionAnlDimbgt> sections = sampleDeptSectionMergerService.getSectionsByDeptCode(deptcode);
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sectionCodes);
            utils.createTransactionsDialog2(budgetGrid);
        });
        budgetGrid.add(
                createEnhancedBudgetItem("Allocated", section.getAllocatedBudget(), VaadinIcon.TERMINAL, "budget-allocated"),
                spentLayoutCard,
                createEnhancedBudgetItem("Committed", section.getCommittedAmount(), VaadinIcon.CLOCK, "budget-committed"),
                createEnhancedBudgetItem("Available A", section.getAvailableAmount(), VaadinIcon.MONEY_DEPOSIT, "budget-available")
        );

        // Enhanced progress section with insights
        VerticalLayout progressSection = createEnhancedProgressSection(section);

        // Action buttons
        HorizontalLayout actionButtons = createSectionActions(section);

        card.add(cardHeader, budgetGrid, progressSection, actionButtons);

        // Add click interaction
        card.getElement().addEventListener("click", e -> {
            //  showNotification("Section details for: " + section.getSectionName(), NotificationVariant.LUMO_PRIMARY);
        });

        return card;
    }

    private Icon getSectionIcon(String sectionName) {
        String name = sectionName.toLowerCase();

        if (name.contains("admin") || name.contains("administration")) {
            return new Icon(VaadinIcon.OFFICE);
        } else if (name.contains("operation") || name.contains("ops")) {
            return new Icon(VaadinIcon.COG);
        } else if (name.contains("project") || name.contains("development")) {
            return new Icon(VaadinIcon.ROCKET);
        } else if (name.contains("maintenance") || name.contains("support")) {
            return new Icon(VaadinIcon.TOOLS);
        } else if (name.contains("training") || name.contains("capacity")) {
            return new Icon(VaadinIcon.ACADEMY_CAP);
        } else if (name.contains("procurement") || name.contains("supply")) {
            return new Icon(VaadinIcon.PACKAGE);
        } else if (name.contains("research") || name.contains("innovation")) {
            return new Icon(VaadinIcon.LIGHTBULB);
        } else if (name.contains("quality") || name.contains("assurance")) {
            return new Icon(VaadinIcon.CHECK_SQUARE);
        } else {
            return new Icon(VaadinIcon.FOLDER);
        }
    }

    private Div createEnhancedStatusIndicator(SectionBudget section) {
        Div statusContainer = new Div();
        statusContainer.addClassName("enhanced-status-container");

        BigDecimal spentPercentage = section.getSpentPercentage();
        String statusText = section.getStatus();
        String statusClass = section.getStatusClass();

        VerticalLayout statusContent = new VerticalLayout();
        statusContent.setSpacing(false);
        statusContent.setPadding(false);
        statusContent.setAlignItems(FlexComponent.Alignment.CENTER);

        // Status badge
        Div statusBadge = new Div();
        statusBadge.addClassName("status-badge");
        statusBadge.addClassName(statusClass);

        HorizontalLayout badgeContent = new HorizontalLayout();
        badgeContent.setAlignItems(FlexComponent.Alignment.CENTER);
        badgeContent.setSpacing(false);
        badgeContent.setPadding(false);

        VaadinIcon statusIcon;

        if (section.isOverBudget()) {
            statusIcon = VaadinIcon.EXCLAMATION_CIRCLE;
        } else if (section.isCritical()) {
            statusIcon = VaadinIcon.WARNING;
        } else if (section.isNearLimit()) {
            statusIcon = VaadinIcon.INFO_CIRCLE;
        } else {
            statusIcon = VaadinIcon.CHECK_CIRCLE;
        }

        Icon icon = new Icon(statusIcon);
        icon.addClassName("status-badge-icon");

        Span status = new Span(statusText);
        status.addClassName("status-badge-text");

        badgeContent.add(icon, status);
        statusBadge.add(badgeContent);

        // Utilization percentage
        Span utilizationText = new Span(String.format("%.1f%%", spentPercentage));
        utilizationText.addClassName("utilization-text");

        statusContent.add(statusBadge, utilizationText);
        statusContainer.add(statusContent);

        return statusContainer;
    }

    private VerticalLayout createEnhancedBudgetItem(String label, BigDecimal amount, VaadinIcon iconType, String className) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName(className);
        item.addClassName("enhanced-budget-item");
        item.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon icon = new Icon(iconType);
        icon.addClassName("budget-item-icon");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("enhanced-budget-label");

        Span amountSpan = new Span(formatCurrency(amount));
        amountSpan.addClassName("enhanced-budget-amount");

        // Enhanced color coding
        if ("Available".equals(label)) {
            if (amount.doubleValue() < 0) {
                amountSpan.addClassName("amount-negative");
                icon.addClassName("icon-negative");
            } else if (amount.doubleValue() < 10_000_000) { // Less than 10M UGX
                amountSpan.addClassName("amount-low");
                icon.addClassName("icon-warning");
            } else {
                icon.addClassName("icon-positive");
            }
        } else if ("Spent".equals(label)) {
            icon.addClassName("icon-spent");
        } else if ("Committed".equals(label)) {
            icon.addClassName("icon-committed");
        } else {
            icon.addClassName("icon-allocated");
        }

        item.add(icon, amountSpan, labelSpan);
        return item;
    }

    private VerticalLayout createEnhancedProgressSection(SectionBudget section) {
        VerticalLayout progressSection = new VerticalLayout();
        progressSection.setSpacing(false);
        progressSection.setPadding(false);
        progressSection.setWidthFull();
        progressSection.addClassName("enhanced-section-progress");

        BigDecimal spentPercentage = section.getSpentPercentage();
        BigDecimal utilizationPercentage = section.getUtilizationPercentage();

        HorizontalLayout progressHeader = new HorizontalLayout();
        progressHeader.setWidthFull();
        progressHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        progressHeader.setPadding(false);
        progressHeader.setSpacing(false);
        progressHeader.addClassName("progress-header");

        HorizontalLayout leftProgress = new HorizontalLayout();
        leftProgress.setAlignItems(FlexComponent.Alignment.CENTER);
        leftProgress.setSpacing(true);
        leftProgress.setPadding(false);

        Icon progressIcon = new Icon(VaadinIcon.CHART_LINE);
        progressIcon.addClassName("progress-icon");

        Span progressLabel = new Span("Budget Utilization");
        progressLabel.addClassName("enhanced-progress-label");

        leftProgress.add(progressIcon, progressLabel);

        VerticalLayout rightProgress = new VerticalLayout();
        rightProgress.setSpacing(false);
        rightProgress.setPadding(false);
        rightProgress.setAlignItems(FlexComponent.Alignment.END);

        Span progressPercentage = new Span(String.format("%.1f%%", spentPercentage));
        progressPercentage.addClassName("enhanced-progress-percentage");

        Span progressAmount = new Span(formatCurrency(section.getSpentAmount()) + " / " + formatCurrency(section.getAllocatedBudget()));
        progressAmount.addClassName("enhanced-progress-amount");

        rightProgress.add(progressPercentage, progressAmount);

        progressHeader.add(leftProgress, rightProgress);

        // Enhanced progress bar with multiple indicators
        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        // progressBar.setValue(Math.min(spentPercentage.doubleValue() / 100.0, 1.0));
        progressBar.addClassName("enhanced-progress-bar");
        progressBar.addClassName(section.getProgressBarClass());

        BigDecimal spentPct = section.getSpentPercentage(); // whatever your variable is
        progressBar.setMin(0);
        progressBar.setMax(1);
        progressBar.setValue(pctToFraction(spentPct)); // ✅ always 0..1

        // Progress insights
        if (section.getCommittedAmount().doubleValue() > 0) {
            HorizontalLayout insightsRow = new HorizontalLayout();
            insightsRow.setWidthFull();
            insightsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            insightsRow.setAlignItems(FlexComponent.Alignment.CENTER);
            insightsRow.setPadding(false);
            insightsRow.setSpacing(false);
            insightsRow.addClassName("progress-insights");

            Span committedLabel = new Span("Committed: " + formatCurrency(section.getCommittedAmount()));
            committedLabel.addClassName("committed-label");

            Span remainingLabel = new Span("Remaining: " + formatCurrency(section.getRemainingBudget()));
            remainingLabel.addClassName("remaining-label");

            insightsRow.add(committedLabel, remainingLabel);
            progressSection.add(progressHeader, progressBar, insightsRow);
        } else {
            progressSection.add(progressHeader, progressBar);
        }

        return progressSection;
    }

    private HorizontalLayout createSectionActions(SectionBudget section) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.setSpacing(true);
        actions.setPadding(false);
        actions.addClassName("section-actions");

        Button viewDetailsButton = new Button("View Details", new Icon(VaadinIcon.EYE));
        viewDetailsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        viewDetailsButton.addClassName("view-details-button");
        viewDetailsButton.addClickListener(e -> {
            showNotification("Opening details for: " + section.getSectionName(), NotificationVariant.LUMO_PRIMARY);
        });

        Button manageButton = new Button("Manage", new Icon(VaadinIcon.COG));
        manageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        manageButton.addClassName("manage-section-button");
        manageButton.addClickListener(e -> {
            showNotification("Managing section: " + section.getSectionName(), NotificationVariant.LUMO_SUCCESS);
        });

        actions.add(viewDetailsButton, manageButton);
        return actions;
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return "UGX " + formatter.format(amount);
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
    }

    private static double clamp01(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, v));
    }

    private static double pctToFraction(BigDecimal pct) {
        if (pct == null) {
            return 0.0;
        }
        return clamp01(pct.doubleValue() / 100.0);
    }
}
