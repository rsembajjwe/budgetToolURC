package com.methaltech.application.views.structure;

import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.views.actual.utilityActuals;
import com.methaltech.application.views.budget.GraphCard;
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
import java.math.RoundingMode;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

public class DepartmentOverview extends VerticalLayout {

    private final NumberFormat currencyFormat;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final Budget budget;

    private utilityActuals utils;

    @Autowired
    public DepartmentOverview(
            List<DepartmentBudget> departmentBudgets,
            CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            SALFLDGService sampleSALFLDGService,
            DeptSectionMergerService sampleDeptSectionMergerService,
            Budget budget
    ) {
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
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
        createSummaryMetrics(departmentBudgets);
        createDepartmentCharts(departmentBudgets);
        createDepartmentCards(departmentBudgets);
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);
        header.addClassName("department-overview-header");

        VerticalLayout headerText = new VerticalLayout();
        headerText.setSpacing(false);
        headerText.setPadding(false);
        headerText.addClassName("department-overview-header-text");

        H2 title = new H2("Department Overview");
        title.addClassName("section-title");

        Span subtitle = new Span("Budget allocation, utilization, and department performance analysis.");
        subtitle.addClassName("section-subtitle");

        headerText.add(title, subtitle);

        Icon usersIcon = new Icon(VaadinIcon.USERS);
        usersIcon.addClassName("section-icon");

        header.add(headerText, usersIcon);
        add(header);
    }

    private void createSummaryMetrics(List<DepartmentBudget> departmentBudgets) {
        HorizontalLayout metricsRow = new HorizontalLayout();
        metricsRow.setWidthFull();
        metricsRow.setSpacing(true);
        metricsRow.setPadding(false);
        metricsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        metricsRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);
        metricsRow.addClassName("department-summary-metrics");
        metricsRow.getStyle().set("display", "flex");
        metricsRow.getStyle().set("gap", "1rem");

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal totalAvailable = BigDecimal.ZERO;
        int overBudgetCount = 0;
        int criticalCount = 0;

        for (DepartmentBudget dept : departmentBudgets) {
            totalBudget = totalBudget.add(nz(dept.getTotalBudget()));
            totalSpent = totalSpent.add(nz(dept.getTotalSpent()));
            totalAvailable = totalAvailable.add(nz(dept.getAvailableBudget()));

            double pct = pctValue(dept.getSpentPercentage());
            if (pct > 100.0) {
                overBudgetCount++;
            } else if (pct > 90.0) {
                criticalCount++;
            }
        }

        BigDecimal avgUtilization = percentage(totalSpent, totalBudget);

        metricsRow.add(
                createOverviewMetric("Cost Centres", String.valueOf(departmentBudgets.size()), VaadinIcon.USERS),
                createOverviewMetric("Allocated Budget", formatCurrency(totalBudget), VaadinIcon.WALLET),
                createOverviewMetric("Total Spent", formatCurrency(totalSpent), VaadinIcon.TRENDING_DOWN),
                createOverviewMetric("Available Budget", formatCurrency(totalAvailable), VaadinIcon.MONEY_DEPOSIT),
                createOverviewMetric("Avg Utilization", avgUtilization.setScale(1, RoundingMode.HALF_UP) + "%", VaadinIcon.CHART_LINE),
                createOverviewMetric("Over Budget", String.valueOf(overBudgetCount), VaadinIcon.WARNING)
        );

        add(metricsRow);
    }

    private Div createOverviewMetric(String label, String value, VaadinIcon iconType) {
        Div card = new Div();
        card.addClassName("department-overview-metric");

        Icon icon = new Icon(iconType);
        icon.addClassName("department-overview-metric-icon");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("department-overview-metric-value");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("department-overview-metric-label");

        card.add(icon, valueSpan, labelSpan);
        return card;
    }

    private void createDepartmentCharts(List<DepartmentBudget> departmentBudgets) {
        Div chartsRow = new Div();
        chartsRow.addClassName("department-charts-row");
        chartsRow.setWidthFull();

        GraphCard budgetVsSpentChart = new GraphCard(
                "Budget vs Spent",
                "Allocated budget against expenditure by department",
                "bar",
                createBudgetVsSpentConfig(departmentBudgets)
        );

        GraphCard utilizationChart = new GraphCard(
                "Department Utilization",
                "Percentage of budget spent by department",
                "bar",
                createDepartmentUtilizationConfig(departmentBudgets)
        );

        GraphCard statusChart = new GraphCard(
                "Status Distribution",
                "Department budget health overview",
                "donut",
                createDepartmentStatusConfig(departmentBudgets)
        );

        chartsRow.add(budgetVsSpentChart, utilizationChart, statusChart);
        add(chartsRow);
    }

    private void createDepartmentCards(List<DepartmentBudget> departmentBudgets) {
        VerticalLayout cardsContainer = new VerticalLayout();
        cardsContainer.setWidthFull();
        cardsContainer.setPadding(false);
        cardsContainer.setSpacing(true);
        cardsContainer.addClassName("department-cards-container");

        for (DepartmentBudget department : departmentBudgets) {
            cardsContainer.add(createDepartmentCard(department));
        }

        add(cardsContainer);
    }

    private Div createDepartmentCard(DepartmentBudget department) {
        Div card = new Div();
        card.addClassName("department-card");
        card.setWidthFull();

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.setAlignItems(FlexComponent.Alignment.START);
        content.setPadding(false);
        content.setSpacing(true);

        VerticalLayout leftContent = new VerticalLayout();
        leftContent.setSpacing(false);
        leftContent.setPadding(false);
        leftContent.setWidthFull();
        leftContent.setFlexGrow(1);

        HorizontalLayout deptHeader = new HorizontalLayout();
        deptHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        deptHeader.setPadding(false);
        deptHeader.setSpacing(false);
        deptHeader.addClassName("department-card-header");

        Div colorIndicator = new Div();
        colorIndicator.addClassName("color-indicator");
        colorIndicator.getStyle().set("background-color", safeColor(department.getColor()));

        VerticalLayout deptInfo = new VerticalLayout();
        deptInfo.setSpacing(false);
        deptInfo.setPadding(false);

        H3 deptName = new H3(nvl(department.getDepartmentName(), "Unknown Department"));
        deptName.addClassName("dept-name");

        Span deptDetails = new Span(buildDepartmentDetails(department));
        deptDetails.addClassName("dept-details");

        deptInfo.add(deptName, deptDetails);
        deptHeader.add(colorIndicator, deptInfo);

        HorizontalLayout budgetInfo = new HorizontalLayout();
        budgetInfo.setSpacing(true);
        budgetInfo.setPadding(false);
        budgetInfo.setWidthFull();
        budgetInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        // budgetInfo.setWrap(true);
        budgetInfo.addClassName("budget-info-row");

        VerticalLayout spentItem = createBudgetItem("Spent", nz(department.getTotalSpent()));
        spentItem.addDoubleClickListener(e -> {
            String deptcode = department.getDepartmentCode();
            Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
            utils = new utilityActuals(
                    budget,
                    sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService,
                    sectionCodes
            );
            utils.createTransactionsDialog2(budgetInfo);
        });

        budgetInfo.add(
                createBudgetItem("Budget", nz(department.getTotalBudget())),
                spentItem,
                createBudgetItem("Available", nz(department.getAvailableBudget()))
        );

        leftContent.add(deptHeader, budgetInfo);

        if (hasBudgetControls(department)) {
            HorizontalLayout controlsInfo = createBudgetControls(department);
            leftContent.add(controlsInfo);
        }

        VerticalLayout progressSection = createProgressSection(department);
        leftContent.add(progressSection);

        Icon chevronIcon = new Icon(VaadinIcon.CHEVRON_RIGHT);
        chevronIcon.addClassName("chevron-icon");

        content.add(leftContent, chevronIcon);
        card.add(content);

        return card;
    }

    private String buildDepartmentDetails(DepartmentBudget department) {
        StringBuilder details = new StringBuilder();
        details.append(nvl(department.getDepartmentCode(), "N/A"));
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

        if ("Available".equals(label)) {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                amountSpan.addClassName("amount-negative");
            } else if (amount.compareTo(BigDecimal.valueOf(50000)) < 0) {
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

        Span statusLabel = new Span(nvl(department.getStatusText(), "Unknown"));
        statusLabel.addClassName(nvl(department.getStatusClass(), "status-good"));

        if (!"On Track".equalsIgnoreCase(nvl(department.getStatusText(), ""))) {
            Icon alertIcon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
            alertIcon.setSize("0.875rem");
            statusContainer.add(alertIcon);
        }

        BigDecimal spentPct = nz(department.getSpentPercentage());
        double progress = pctToFraction(spentPct);

        ProgressBar progressBar = new ProgressBar(0, 1, progress);
        progressBar.setWidthFull();
        progressBar.addClassName(getProgressBarClass(spentPct.doubleValue()));

        Span progressLabel = new Span(String.format(Locale.US, "%.1f%% spent", spentPct.doubleValue()));
        progressLabel.addClassName("progress-label");

        statusContainer.add(statusLabel);
        progressHeader.add(progressLabel, statusContainer);

        progressSection.add(progressHeader, progressBar);
        return progressSection;
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

    private String createBudgetVsSpentConfig(List<DepartmentBudget> departments) {

        String[] categories = departments.stream()
                .map(d -> nvl(shortDeptName(d.getDepartmentName()), "N/A"))
                .toArray(String[]::new);

        String budgetSeries = departments.stream()
                .map(d -> toMillions(d.getTotalBudget()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        String spentSeries = departments.stream()
                .map(d -> toMillions(d.getTotalSpent()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return """
    {
      "series": [
        { "name": "Budget (UGX M)", "data": [%s] },
        { "name": "Spent (UGX M)", "data": [%s] }
      ],
      "xaxis": {
        "categories": [%s],
        "title": { "text": "Departments" }
      },
      "yaxis": {
        "title": { "text": "UGX (Millions)" }
      },
      "height": 260,
      "plotOptions": {
        "bar": {
          "borderRadius": 6,
          "columnWidth": "45%%"
        }
      },
      "dataLabels": {
        "enabled": true
      }
    }
    """.formatted(
                budgetSeries,
                spentSeries,
                joinQuoted(categories)
        );
    }

    private String toMillions(BigDecimal value) {
        return nz(value)
                .divide(BigDecimal.valueOf(1_000_000), 2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String createDepartmentUtilizationConfig(List<DepartmentBudget> departments) {

        String[] categories = departments.stream()
                .map(d -> nvl(shortDeptName(d.getDepartmentName()), "N/A"))
                .toArray(String[]::new);

        String utilizationSeries = departments.stream()
                .map(d -> nz(d.getSpentPercentage()).toPlainString())
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
      "chartUnit": "percent",
      "yaxis": {
        "title": {
          "text": "Budget Utilization (%%)"
        }
      },
      "height": 260,
      "plotOptions": {
        "bar": {
          "borderRadius": 6,
          "columnWidth": "45%%"
        }
      }
               
    }
    """.formatted(
                utilizationSeries,
                joinQuoted(categories)
        );
    }
private String shortDeptName(String name) {
    if (name == null) return "N/A";
    return name.length() > 14 ? name.substring(0, 14) + "..." : name;
}
    private String createDepartmentStatusConfig(List<DepartmentBudget> departments) {
        long onTrack = departments.stream().filter(d -> "On Track".equalsIgnoreCase(nvl(d.getStatusText(), ""))).count();
        long near = departments.stream().filter(d -> "Near Limit".equalsIgnoreCase(nvl(d.getStatusText(), ""))).count();
        long critical = departments.stream().filter(d -> "Critical".equalsIgnoreCase(nvl(d.getStatusText(), ""))).count();
        long over = departments.stream().filter(d -> "Over Budget".equalsIgnoreCase(nvl(d.getStatusText(), ""))).count();

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

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String nvl(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String safeColor(String value) {
        return (value == null || value.isBlank()) ? "#7c3aed" : value;
    }

    private double pctValue(BigDecimal value) {
        return nz(value).doubleValue();
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
