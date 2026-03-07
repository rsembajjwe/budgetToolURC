package com.methaltech.application.views.structure;

import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.views.actual.utilityActuals;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import java.math.BigDecimal;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

public class DepartmentOverview extends VerticalLayout {

    private final NumberFormat currencyFormat;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    utilityActuals utils;
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final Budget budget;

    @Autowired
    public DepartmentOverview(List<DepartmentBudget> departmentBudgets, CoaService sampleCoaService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, SALFLDGService sampleSALFLDGService, DeptSectionMergerService sampleDeptSectionMergerService, Budget budget) {
        // Create UGX currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budget = budget;

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("department-overview");

        createHeader();
        createDepartmentCards(departmentBudgets);
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);

        VerticalLayout headerText = new VerticalLayout();
        headerText.setSpacing(false);
        headerText.setPadding(false);

        H2 title = new H2("Department Overview");
        title.addClassName("section-title");

        Span subtitle = new Span("Budget allocation and spending by department");
        subtitle.addClassName("section-subtitle");

        headerText.add(title, subtitle);

        Icon usersIcon = new Icon(VaadinIcon.USERS);
        usersIcon.addClassName("section-icon");

        header.add(headerText, usersIcon);
        add(header);
    }

    private void createDepartmentCards(List<DepartmentBudget> departmentBudgets) {
        VerticalLayout cardsContainer = new VerticalLayout();
        cardsContainer.setWidthFull();
        cardsContainer.setPadding(false);
        cardsContainer.setSpacing(true);

        for (DepartmentBudget department : departmentBudgets) {
            cardsContainer.add(createDepartmentCard(department));
        }

        add(cardsContainer);
    }

    private Div createDepartmentCard(DepartmentBudget department) {
        Div card = new Div();
        card.addClassName("department-card");
        card.setWidth("95%");

        // Add click interaction
        card.getElement().addEventListener("click", e -> {
            // Future: Navigate to department details
        });

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.setAlignItems(FlexComponent.Alignment.START);
        content.setPadding(false);
        content.setSpacing(false);

        VerticalLayout leftContent = new VerticalLayout();
        leftContent.setSpacing(false);
        leftContent.setPadding(false);
        leftContent.setFlexGrow(1);

        // Department header with color indicator
        HorizontalLayout deptHeader = new HorizontalLayout();
        deptHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        deptHeader.setPadding(false);
        deptHeader.setSpacing(false);

        Div colorIndicator = new Div();
        colorIndicator.addClassName("color-indicator");
        colorIndicator.getStyle().set("background-color", department.getColor());

        VerticalLayout deptInfo = new VerticalLayout();
        deptInfo.setSpacing(false);
        deptInfo.setPadding(false);

        H3 deptName = new H3(department.getDepartmentName());
        deptName.addClassName("dept-name");

        deptName.addSingleClickListener(e -> {
            Notification.show("Gooood");
        });

        Span deptDetails = new Span(buildDepartmentDetails(department));
        deptDetails.addClassName("dept-details");

        deptInfo.add(deptName, deptDetails);
        deptHeader.add(colorIndicator, deptInfo);

        // Budget information grid
        HorizontalLayout budgetInfo = new HorizontalLayout();
        budgetInfo.setSpacing(true);
        budgetInfo.setPadding(false);
        budgetInfo.setWidthFull();
        budgetInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        VerticalLayout createBudgetItemSpent = createBudgetItem("Spent", department.getTotalSpent());
        createBudgetItemSpent.addDoubleClickListener(e -> {
            String deptcode = department.getDepartmentCode();
            Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
            //Set<UrcDeptSectionAnlDimbgt> sections = sampleDeptSectionMergerService.getSectionsByDeptCode(deptcode);
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sectionCodes);
            utils.createTransactionsDialog2(budgetInfo);
        });
        budgetInfo.add(
                createBudgetItem("Budget", department.getTotalBudget()),
                createBudgetItemSpent,
                //createBudgetItem("Committed", department.getTotalCommitted()),
                createBudgetItem("Available", department.getAvailableBudget())
        );

        leftContent.add(deptHeader, budgetInfo);

        // Budget controls indicators
        if (hasBudgetControls(department)) {
            HorizontalLayout controlsInfo = createBudgetControls(department);
            leftContent.add(controlsInfo);
        }

        // Progress section
        VerticalLayout progressSection = createProgressSection(department);
        leftContent.add(progressSection);

        // Chevron icon
        Icon chevronIcon = new Icon(VaadinIcon.CHEVRON_RIGHT);
        chevronIcon.addClassName("chevron-icon");

        content.add(leftContent, chevronIcon);
        card.add(content);

        return card;
    }

    private String buildDepartmentDetails(DepartmentBudget department) {
        StringBuilder details = new StringBuilder();
        details.append(department.getDepartmentCode());

        /*if (department.getCategoryId() != null && !department.getCategoryId().isEmpty()) {
            details.append(" • ").append(department.getCategoryId());
        }*/
        details.append(" • ").append(department.getSectionCount()).append(" sections");

        return details.toString();
    }

    private VerticalLayout createBudgetItem(String label, BigDecimal amount) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.setMinWidth("120px");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("budget-label");

        Span amountSpan = new Span(formatCurrency(amount));
        amountSpan.addClassName("budget-amount");

        // Enhanced color coding
        if ("Available".equals(label)) {
            if (amount.doubleValue() < 0) {
                amountSpan.addClassName("amount-negative");
            } else if (amount.doubleValue() < 50000) {
                amountSpan.addClassName("amount-low");
            }
        }

        item.add(labelSpan, amountSpan);
        return item;
    }

    private boolean hasBudgetControls(DepartmentBudget department) {
        return department.isBudgetCheckEnabled()
                || department.isBudgetStopEnabled()
                || department.isPostingProhibited();
    }

    private HorizontalLayout createBudgetControls(DepartmentBudget department) {
        HorizontalLayout controlsInfo = new HorizontalLayout();
        controlsInfo.setSpacing(true);
        controlsInfo.setPadding(false);
        controlsInfo.addClassName("budget-controls");

        if (department.isBudgetCheckEnabled()) {
            Span budgetCheck = new Span("Budget Check");
            budgetCheck.addClassName("control-indicator");
            budgetCheck.addClassName("control-check");
            controlsInfo.add(budgetCheck);
        }

        if (department.isBudgetStopEnabled()) {
            Span budgetStop = new Span("Budget Stop");
            budgetStop.addClassName("control-indicator");
            budgetStop.addClassName("control-stop");
            controlsInfo.add(budgetStop);
        }

        if (department.isPostingProhibited()) {
            Span postingProhibited = new Span("Posting Prohibited");
            postingProhibited.addClassName("control-indicator");
            postingProhibited.addClassName("control-prohibited");
            controlsInfo.add(postingProhibited);
        }

        return controlsInfo;
    }

    private VerticalLayout createProgressSection(DepartmentBudget department) {
        VerticalLayout progressSection = new VerticalLayout();
        progressSection.setSpacing(false);
        progressSection.setPadding(false);
        progressSection.setWidthFull();

        HorizontalLayout progressHeader = new HorizontalLayout();
        progressHeader.setWidthFull();
        progressHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        progressHeader.setPadding(false);
        progressHeader.setSpacing(false);

        HorizontalLayout statusContainer = new HorizontalLayout();
        statusContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        statusContainer.setPadding(false);
        statusContainer.setSpacing(false);

        Span statusLabel = new Span(department.getStatusText());
        statusLabel.addClassName(department.getStatusClass());

        if (!department.getStatusText().equals("On Track")) {
            Icon alertIcon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
            alertIcon.setSize("0.875rem");
            statusContainer.add(alertIcon);
        }

        BigDecimal spentPct = department.getSpentPercentage(); // can be null
        double progress = pctToFraction(spentPct);            // clamps 0..1

        ProgressBar progressBar = new ProgressBar(0, 1, progress);
        progressBar.setWidthFull();
        progressBar.addClassName(getProgressBarClass(spentPct == null ? 0.0 : spentPct.doubleValue()));

        double spentPctVal = spentPct == null ? 0.0 : spentPct.doubleValue();
        Span progressLabel = new Span(String.format(Locale.US, "%.1f%% spent", spentPctVal));
        progressLabel.addClassName("progress-label");
        statusContainer.add(statusLabel);
        progressHeader.add(progressLabel, statusContainer);

        progressSection.add(progressHeader, progressBar);

        return progressSection;
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return "UGX " + formatter.format(amount);
    }

    private String getProgressBarClass(double spentPercentage) {
        if (spentPercentage > 100) {
            return "progress-over";
        } else if (spentPercentage > 90) {
            return "progress-critical";
        } else if (spentPercentage > 75) {
            return "progress-near";
        } else {
            return "progress-good";
        }
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
