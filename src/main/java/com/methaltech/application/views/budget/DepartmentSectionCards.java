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
import java.math.RoundingMode;

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
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;

    private utilityActuals utils;

    @Autowired
    public DepartmentSectionCards(
            DepartmentBudget department,
            BudgetService budgetService,
            Budget budget,
            CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            SALFLDGService sampleSALFLDGService
    ) {
        this.department = department;
        this.budgetService = budgetService;
        this.budget = budget;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;

        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("department-section-cards");

        List<SectionBudget> sections = budgetService.getDepartmentSections(
                department.getDepartmentCode(),
                budget
        );

        createHeader();
        createOverviewCard(sections);
        createSectionCharts(sections);
        createSectionCards(sections);
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

        Span subtitle = new Span(
                "Detailed Cost centre breakdown for " + nvl(department.getDepartmentName(), "Department")
        );
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
        refreshButton.addClickListener(e
                -> showNotification("Section data refreshed", NotificationVariant.LUMO_SUCCESS)
        );

        Icon sectionsIcon = new Icon(VaadinIcon.GRID_BIG);
        sectionsIcon.addClassName("section-cards-icon");

        headerActions.add(refreshButton, sectionsIcon);
        header.add(headerText, headerActions);
        add(header);
    }

private void createOverviewCard(List<SectionBudget> sections) {
    Div overviewCard = new Div();
    overviewCard.addClassName("section-overview-card");
    overviewCard.setWidthFull();

    VerticalLayout overviewContent = new VerticalLayout();
    overviewContent.setSpacing(true);
    overviewContent.setPadding(false);
    overviewContent.setWidthFull();
    overviewContent.addClassName("section-overview-content");

    H4 overviewTitle = new H4("Department Overview");
    overviewTitle.addClassName("overview-title");

    BigDecimal totalAllocated = BigDecimal.ZERO;
    BigDecimal totalSpent = BigDecimal.ZERO;
    BigDecimal totalAvailable = BigDecimal.ZERO;
    int overBudgetCount = 0;

    for (SectionBudget section : sections) {
        totalAllocated = totalAllocated.add(nz(section.getAllocatedBudget()));
        totalSpent = totalSpent.add(nz(section.getSpentAmount())).abs();
        totalAvailable = totalAvailable.add(nz(section.getAvailableAmount()));

        if (section.isOverBudget()) {
            overBudgetCount++;
        }
    }

    BigDecimal utilization = percentage(totalSpent, totalAllocated);

    HorizontalLayout metricsGrid = new HorizontalLayout();
    metricsGrid.setWidthFull();
    metricsGrid.setSpacing(true);
    metricsGrid.setPadding(false);
    metricsGrid.setAlignItems(FlexComponent.Alignment.STRETCH);
    metricsGrid.addClassName("overview-metrics");

    VerticalLayout sectionsMetric = createMetricItem(
            "Total Sections", String.valueOf(sections.size()), VaadinIcon.CLUSTER, "metric-sections");
    VerticalLayout allocatedMetric = createMetricItem(
            "Allocated Budget", formatCurrency(totalAllocated), VaadinIcon.WALLET, "metric-allocated");
    VerticalLayout spentMetric = createMetricItem(
            "Total Spent", formatCurrency(totalSpent), VaadinIcon.TRENDING_DOWN, "metric-spent");
    VerticalLayout utilizationMetric = createMetricItem(
            "Utilization", utilization.setScale(1, RoundingMode.HALF_UP) + "%", VaadinIcon.CHART_LINE, "metric-utilization");
    VerticalLayout overBudgetMetric = createMetricItem(
            "Over Budget", String.valueOf(overBudgetCount), VaadinIcon.WARNING, "metric-status");

    metricsGrid.add(sectionsMetric, allocatedMetric, spentMetric, utilizationMetric, overBudgetMetric);
    metricsGrid.setFlexGrow(1, sectionsMetric);
    metricsGrid.setFlexGrow(1, allocatedMetric);
    metricsGrid.setFlexGrow(1, spentMetric);
    metricsGrid.setFlexGrow(1, utilizationMetric);
    metricsGrid.setFlexGrow(1, overBudgetMetric);

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

    Span progressValue = new Span(String.format(Locale.US, "%.1f%% utilized", utilization.doubleValue()));
    progressValue.addClassName("progress-value");

    progressHeader.add(progressLabel, progressValue);

    ProgressBar departmentProgress = new ProgressBar();
    departmentProgress.setWidthFull();
    departmentProgress.setMin(0);
    departmentProgress.setMax(1);
    departmentProgress.setValue(pctToFraction(utilization));
    departmentProgress.addClassName("department-progress-bar");
    departmentProgress.addClassName(getDepartmentProgressClass(utilization.doubleValue()));

    progressSection.add(progressHeader, departmentProgress);
    overviewContent.add(overviewTitle, metricsGrid, progressSection);
    overviewCard.add(overviewContent);
    add(overviewCard);
}

private VerticalLayout createMetricItem(String label, String value, VaadinIcon iconType, String className) {
    VerticalLayout item = new VerticalLayout();
    item.setSpacing(false);
    item.setPadding(false);
    item.setWidthFull();
    item.addClassNames("metric-item", className);
    item.setAlignItems(FlexComponent.Alignment.CENTER);
    item.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

    Icon icon = new Icon(iconType);
    icon.addClassName("metric-icon");

    Span valueSpan = new Span(value);
    valueSpan.addClassName("metric-value");

    Span labelSpan = new Span(label);
    labelSpan.addClassName("metric-label");

    item.add(icon, valueSpan, labelSpan);
    return item;
}

    private void createSectionCharts(List<SectionBudget> sections) {
        Div chartsRow = new Div();
        chartsRow.addClassName("section-charts-row");
        chartsRow.setWidthFull();

        GraphCard budgetVsSpentChart = new GraphCard(
                "Allocated vs Spent",
                "Section budget against expenditure",
                "bar",
                createBudgetVsSpentConfig(sections)
        );

        GraphCard utilizationChart = new GraphCard(
                "Section Utilization",
                "Percentage spent by section",
                "bar",
                createUtilizationConfig(sections)
        );

        GraphCard statusChart = new GraphCard(
                "Status Distribution",
                "Section budget health",
                "donut",
                createStatusConfig(sections)
        );

        chartsRow.add(budgetVsSpentChart, utilizationChart, statusChart);
        add(chartsRow);
    }

    private void createSectionCards(List<SectionBudget> sections) {
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

    VerticalLayout cardContent = new VerticalLayout();
    cardContent.setWidthFull();
    cardContent.setPadding(false);
    cardContent.setSpacing(true);
    cardContent.addClassName("section-card-content");

    HorizontalLayout cardHeader = new HorizontalLayout();
    cardHeader.setWidthFull();
    cardHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    cardHeader.setAlignItems(FlexComponent.Alignment.START);
    cardHeader.setPadding(false);
    cardHeader.setSpacing(true);
    cardHeader.addClassName("section-card-header");

    HorizontalLayout sectionInfo = new HorizontalLayout();
    sectionInfo.setAlignItems(FlexComponent.Alignment.START);
    sectionInfo.setSpacing(true);
    sectionInfo.setPadding(false);
    sectionInfo.setFlexGrow(1);
    sectionInfo.addClassName("section-info");

    Icon sectionIcon = getSectionIcon(nvl(section.getSectionName(), ""));
    sectionIcon.addClassName("section-icon");

    VerticalLayout sectionDetails = new VerticalLayout();
    sectionDetails.setSpacing(false);
    sectionDetails.setPadding(false);
    sectionDetails.addClassName("section-details");

    H5 sectionName = new H5(nvl(section.getSectionName(), "Unknown Section"));
    sectionName.addClassName("section-name");

    HorizontalLayout sectionMeta = new HorizontalLayout();
    sectionMeta.setAlignItems(FlexComponent.Alignment.CENTER);
    sectionMeta.setSpacing(true);
    sectionMeta.setPadding(false);
    sectionMeta.addClassName("section-meta");

    Span sectionCode = new Span(nvl(section.getSectionCode(), "N/A"));
    sectionCode.addClassName("section-code");

    Div separator = new Div();
    separator.addClassName("meta-separator");

    Span categoryInfo = new Span("Category: " + nvl(section.getCategoryId(), "N/A"));
    categoryInfo.addClassName("category-info");

    sectionMeta.add(sectionCode, separator, categoryInfo);

    sectionDetails.add(sectionName, sectionMeta);

    if (section.getDescription() != null && !section.getDescription().isBlank()) {
        Span sectionDesc = new Span(section.getDescription());
        sectionDesc.addClassName("section-description");
        sectionDetails.add(sectionDesc);
    }

    sectionInfo.add(sectionIcon, sectionDetails);

    Div statusIndicator = createEnhancedStatusIndicator(section);
    cardHeader.add(sectionInfo, statusIndicator);

    HorizontalLayout budgetGrid = new HorizontalLayout();
    budgetGrid.setWidthFull();
    budgetGrid.setSpacing(true);
    budgetGrid.setPadding(false);
    budgetGrid.setAlignItems(FlexComponent.Alignment.STRETCH);
    budgetGrid.addClassName("section-budget-grid");

    VerticalLayout allocatedCard = createEnhancedBudgetItem(
            "Allocated", nz(section.getAllocatedBudget()), VaadinIcon.WALLET, "budget-allocated");

    VerticalLayout spentCard = createEnhancedBudgetItem(
            "Spent", nz(section.getSpentAmount()), VaadinIcon.TRENDING_DOWN, "budget-spent");

    VerticalLayout availableCard = createEnhancedBudgetItem(
            "Available", nz(section.getAvailableAmount()), VaadinIcon.MONEY_DEPOSIT, "budget-available");

    allocatedCard.setWidthFull();
    spentCard.setWidthFull();
    availableCard.setWidthFull();

    budgetGrid.add(allocatedCard, spentCard, availableCard);
    budgetGrid.setFlexGrow(1, allocatedCard);
    budgetGrid.setFlexGrow(1, spentCard);
    budgetGrid.setFlexGrow(1, availableCard);

    spentCard.addDoubleClickListener(e -> {
        String sectcode = section.getSectionCode();
        Set<String> sectionCodes = new HashSet<>();
        sectionCodes.add(sectcode);

        utils = new utilityActuals(
                budget,
                sampleCoaService,
                sampleUrcDeptSectionAnlDimbgtService,
                sampleSALFLDGService,
                sectionCodes
        );
        utils.createTransactionsDialog2(budgetGrid);
    });

    VerticalLayout progressSection = createEnhancedProgressSection(section);
    HorizontalLayout actionButtons = createSectionActions(section);

    cardContent.add(cardHeader, budgetGrid, progressSection, actionButtons);
    card.add(cardContent);
    return card;
}

    private Icon getSectionIcon(String sectionName) {
        String name = sectionName.toLowerCase(Locale.ROOT);

        if (name.contains("admin") || name.contains("administration")) {
            return new Icon(VaadinIcon.OFFICE);
        }
        if (name.contains("operation") || name.contains("ops")) {
            return new Icon(VaadinIcon.COG);
        }
        if (name.contains("project") || name.contains("development")) {
            return new Icon(VaadinIcon.ROCKET);
        }
        if (name.contains("maintenance") || name.contains("support")) {
            return new Icon(VaadinIcon.TOOLS);
        }
        if (name.contains("training") || name.contains("capacity")) {
            return new Icon(VaadinIcon.ACADEMY_CAP);
        }
        if (name.contains("procurement") || name.contains("supply")) {
            return new Icon(VaadinIcon.PACKAGE);
        }
        if (name.contains("research") || name.contains("innovation")) {
            return new Icon(VaadinIcon.LIGHTBULB);
        }
        if (name.contains("quality") || name.contains("assurance")) {
            return new Icon(VaadinIcon.CHECK_SQUARE);
        }

        return new Icon(VaadinIcon.FOLDER);
    }

    private Div createEnhancedStatusIndicator(SectionBudget section) {
        Div statusContainer = new Div();
        statusContainer.addClassName("enhanced-status-container");

        BigDecimal spentPercentage = nz(section.getSpentPercentage());
        String statusText = nvl(section.getStatus(), "Unknown");
        String statusClass = nvl(section.getStatusClass(), "status-good");

        VerticalLayout statusContent = new VerticalLayout();
        statusContent.setSpacing(false);
        statusContent.setPadding(false);
        statusContent.setAlignItems(FlexComponent.Alignment.CENTER);

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

        Span utilizationText = new Span(String.format(Locale.US, "%.1f%%", spentPercentage.doubleValue()));
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

        if ("Available".equals(label)) {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                amountSpan.addClassName("amount-negative");
                icon.addClassName("icon-negative");
            } else if (amount.compareTo(BigDecimal.valueOf(10_000_000)) < 0) {
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

        BigDecimal spentPercentage = nz(section.getSpentPercentage());

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

        Span progressPercentage = new Span(String.format(Locale.US, "%.1f%%", spentPercentage.doubleValue()));
        progressPercentage.addClassName("enhanced-progress-percentage");

        Span progressAmount = new Span(
                formatCurrency(nz(section.getSpentAmount())) + " / " + formatCurrency(nz(section.getAllocatedBudget()))
        );
        progressAmount.addClassName("enhanced-progress-amount");

        rightProgress.add(progressPercentage, progressAmount);
        progressHeader.add(leftProgress, rightProgress);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setMin(0);
        progressBar.setMax(1);
        progressBar.setValue(pctToFraction(spentPercentage));
        progressBar.addClassName("enhanced-progress-bar");
        progressBar.addClassName(nvl(section.getProgressBarClass(), "progress-good"));
        progressSection.add(progressHeader, progressBar);
        /*        if (nz(section.getCommittedAmount()).compareTo(BigDecimal.ZERO) > 0) {
HorizontalLayout insightsRow = new HorizontalLayout();
insightsRow.setWidthFull();
insightsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
insightsRow.setAlignItems(FlexComponent.Alignment.CENTER);
insightsRow.setPadding(false);
insightsRow.setSpacing(false);
insightsRow.addClassName("progress-insights");

Span committedLabel = new Span("Committed: " + formatCurrency(nz(section.getCommittedAmount())));
committedLabel.addClassName("committed-label");

Span remainingLabel = new Span("Remaining: " + formatCurrency(nz(section.getRemainingBudget())));
remainingLabel.addClassName("remaining-label");

insightsRow.add(committedLabel, remainingLabel);
progressSection.add(progressHeader, progressBar, insightsRow);
} else {
progressSection.add(progressHeader, progressBar);
}*/

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
        viewDetailsButton.addClickListener(e
                -> showNotification("Opening details for: " + nvl(section.getSectionName(), "Section"), NotificationVariant.LUMO_PRIMARY)
        );

        Button manageButton = new Button("Manage", new Icon(VaadinIcon.COG));
        manageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        manageButton.addClassName("manage-section-button");
        manageButton.addClickListener(e
                -> showNotification("Managing section: " + nvl(section.getSectionName(), "Section"), NotificationVariant.LUMO_SUCCESS)
        );

        actions.add(viewDetailsButton, manageButton);
        return actions;
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

    private String createBudgetVsSpentConfig(List<SectionBudget> sections) {
        String[] categories = sections.stream()
                .map(s -> nvl(s.getSectionCode(), "N/A"))
                .toArray(String[]::new);

        String allocatedSeries = sections.stream()
                .map(s -> nz(s.getAllocatedBudget()).abs().toPlainString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        String spentSeries = sections.stream()
                .map(s -> nz(s.getSpentAmount()).abs().toPlainString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return """
        {
          "series": [
            { "name": "Allocated", "data": [%s] },
            { "name": "Spent", "data": [%s] }
          ],
          "xaxis": {
            "categories": [%s]
          },
          "height": 260,
          "plotOptions": {
            "bar": {
              "borderRadius": 6,
              "columnWidth": "45%%"
            }
          }
        }
        """.formatted(allocatedSeries, spentSeries, joinQuoted(categories));
    }

    private String createUtilizationConfig(List<SectionBudget> sections) {
        String[] categories = sections.stream()
                .map(s -> nvl(s.getSectionCode(), "N/A"))
                .toArray(String[]::new);

        String values = sections.stream()
                .map(s -> nz(s.getSpentPercentage()).toPlainString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return """
        {
          "series": [{
            "name": "Utilization",
            "data": [%s]
          }],
          "xaxis": {
            "categories": [%s]
          },
          "height": 260,
          "plotOptions": {
            "bar": {
              "borderRadius": 6,
              "columnWidth": "45%%"
            }
          }
        }
        """.formatted(values, joinQuoted(categories));
    }

    private String createStatusConfig(List<SectionBudget> sections) {
        long onTrack = sections.stream().filter(s -> "On Track".equalsIgnoreCase(nvl(s.getStatus(), ""))).count();
        long near = sections.stream().filter(s -> "Near Limit".equalsIgnoreCase(nvl(s.getStatus(), ""))).count();
        long critical = sections.stream().filter(s -> "Critical".equalsIgnoreCase(nvl(s.getStatus(), ""))).count();
        long over = sections.stream().filter(s -> "Over Budget".equalsIgnoreCase(nvl(s.getStatus(), ""))).count();

        if (onTrack == 0 && near == 0 && critical == 0 && over == 0) {
            onTrack = 1;
            near = 1;
        }

        return """
        {
          "series": [%d, %d, %d, %d],
          "labels": ["On Track", "Near Limit", "Critical", "Over Budget"],
          "colors": ["#10b981", "#f59e0b", "#f97316", "#dc2626"],
          "height": 260,
          "legend": { "position": "bottom" },
          "plotOptions": {
            "pie": {
              "donut": { "size": "62%%" }
            }
          }
        }
        """.formatted(onTrack, near, critical, over);
    }

    private String joinQuoted(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("\"").append(values[i].replace("\"", "\\\"")).append("\"");
        }
        return sb.toString();
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        formatter.setMinimumFractionDigits(0);
        return "UGX " + formatter.format(nz(amount));
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String nvl(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private BigDecimal percentage(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return nz(part)
                .divide(total, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
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
