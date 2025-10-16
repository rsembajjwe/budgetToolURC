package com.methaltech.application.views.budget;

import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SectionBudgetChart extends VerticalLayout {

    private final NumberFormat currencyFormat;
    private final DepartmentBudget department;

    public SectionBudgetChart(DepartmentBudget department) {
        this.department = department;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("section-budget-chart");

        createOverviewMetrics();
        createHeader();
       // createChart();
        createInsights();
        createTrendAnalysis();
    }

    private void createOverviewMetrics() {
        Div metricsCard = new Div();
        metricsCard.addClassName("chart-metrics-card");

        HorizontalLayout metricsGrid = new HorizontalLayout();
        metricsGrid.setWidthFull();
        metricsGrid.setSpacing(true);
        metricsGrid.setPadding(false);
        metricsGrid.addClassName("chart-metrics-grid");

        // Calculate metrics
        double spentPercentage = department.getSpentPercentage();
        double availablePercentage = 100 - spentPercentage;
        double efficiency = spentPercentage > 0 ? (department.getTotalBudget() / department.getTotalSpent()) * 100 : 0;

        metricsGrid.add(
                createMetricItem("Budget Utilization", String.format("%.1f%%", spentPercentage),
                        VaadinIcon.CHART_LINE, getUtilizationColor(spentPercentage), "metric-utilization"),
                createMetricItem("Available Budget", String.format("%.1f%%", availablePercentage),
                        VaadinIcon.MONEY_DEPOSIT, getAvailabilityColor(availablePercentage), "metric-available"),
                createMetricItem("Sections Active", String.valueOf(department.getSectionCount()),
                        VaadinIcon.BUILDING, "#3b82f6", "metric-sections"),
                createMetricItem("Budget Status", department.getStatusText(),
                        VaadinIcon.INFO_CIRCLE, getStatusColor(department.getStatusText()), "metric-status")
        );

        metricsCard.add(metricsGrid);
        add(metricsCard);
    }

    private VerticalLayout createMetricItem(String label, String value, VaadinIcon iconType, String color, String className) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName("chart-metric-item");
        item.addClassName(className);
        item.setAlignItems(FlexComponent.Alignment.CENTER);
        item.setWidthFull();

        // Icon container
        Div iconContainer = new Div();
        iconContainer.addClassName("metric-icon-container");
        iconContainer.getStyle().set("background-color", color + "20");
        iconContainer.getStyle().set("border", "1px solid " + color + "40");

        Icon icon = new Icon(iconType);
        icon.addClassName("metric-icon");
        icon.getStyle().set("color", color);
        iconContainer.add(icon);

        // Value
        Span valueSpan = new Span(value);
        valueSpan.addClassName("metric-value");
        valueSpan.getStyle().set("color", color);

        // Label
        Span labelSpan = new Span(label);
        labelSpan.addClassName("metric-label");

        item.add(iconContainer, valueSpan, labelSpan);
        return item;
    }

    private String getUtilizationColor(double percentage) {
        if (percentage > 90) {
            return "#dc2626";
        }
        if (percentage > 75) {
            return "#f59e0b";
        }
        if (percentage > 50) {
            return "#10b981";
        }
        return "#3b82f6";
    }

    private String getAvailabilityColor(double percentage) {
        if (percentage < 10) {
            return "#dc2626";
        }
        if (percentage < 25) {
            return "#f59e0b";
        }
        return "#10b981";
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "Over Budget":
                return "#dc2626";
            case "Critical":
                return "#f59e0b";
            case "Near Limit":
                return "#f97316";
            default:
                return "#10b981";
        }
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);
        header.addClassName("chart-header");

        VerticalLayout headerText = new VerticalLayout();
        headerText.setSpacing(false);
        headerText.setPadding(false);

        H3 title = new H3("Section Budget Analysis");
        title.addClassName("chart-title");

        Span subtitle = new Span("Comprehensive section-wise budget breakdown and performance metrics");
        subtitle.addClassName("chart-subtitle");

        headerText.add(title, subtitle);

        HorizontalLayout headerActions = new HorizontalLayout();
        headerActions.setAlignItems(FlexComponent.Alignment.CENTER);
        headerActions.setSpacing(true);
        headerActions.setPadding(false);
        headerActions.addClassName("chart-header-actions");

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        refreshButton.addClassName("chart-refresh-button");
        refreshButton.addClickListener(e -> {
            showNotification("Chart data refreshed", NotificationVariant.LUMO_SUCCESS);
        });

        Button exportButton = new Button("Export", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        exportButton.addClassName("chart-export-button");
        exportButton.addClickListener(e -> {
            showNotification("Chart export coming soon", NotificationVariant.LUMO_PRIMARY);
        });

        Icon chartIcon = new Icon(VaadinIcon.CHART);
        chartIcon.addClassName("chart-icon");

        headerActions.add(refreshButton, exportButton, chartIcon);
        header.add(headerText, headerActions);
        add(header);
    }

    private void createChart() {
        Div chartCard = new Div();
        chartCard.addClassName("chart-card");

        // Chart header with legend
        HorizontalLayout chartHeader = new HorizontalLayout();
        chartHeader.setWidthFull();
        chartHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        chartHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        chartHeader.setPadding(false);
        chartHeader.setSpacing(false);
        chartHeader.addClassName("chart-card-header");

        H4 chartTitle = new H4("Section Performance Breakdown");
        chartTitle.addClassName("chart-card-title");

        HorizontalLayout legend = new HorizontalLayout();
        legend.setAlignItems(FlexComponent.Alignment.CENTER);
        legend.setSpacing(true);
        legend.setPadding(false);
        legend.addClassName("chart-legend");

        legend.add(
                createLegendItem("Budget", "#e2e8f0", "legend-budget"),
                createLegendItem("Spent", "#3b82f6", "legend-spent"),
                createLegendItem("Committed", "#f59e0b", "legend-committed")
        );

        chartHeader.add(chartTitle, legend);

        VerticalLayout chartContent = new VerticalLayout();
        chartContent.setSpacing(true);
        chartContent.setPadding(false);
        chartContent.setWidthFull();
        chartContent.addClassName("chart-content");

        // Mock section data for visualization
        List<ChartData> chartData = generateChartData();
        double maxBudget = chartData.stream().mapToDouble(ChartData::getBudget).max().orElse(1);

        for (ChartData data : chartData) {
            chartContent.add(createChartBar(data, maxBudget));
        }

        //chartCard.add(chartHeader);
        chartCard.add(chartHeader, chartContent);
        add(chartCard);
    }

    private HorizontalLayout createLegendItem(String label, String color, String className) {
        HorizontalLayout item = new HorizontalLayout();
        item.setAlignItems(FlexComponent.Alignment.CENTER);
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName(className);

        Div colorDot = new Div();
        colorDot.addClassName("legend-color-dot");
        colorDot.getStyle().set("background-color", color);

        Span labelSpan = new Span(label);
        labelSpan.addClassName("legend-label");

        item.add(colorDot, labelSpan);
        return item;
    }

    private Div createChartBar(ChartData data, double maxBudget) {
        Div barContainer = new Div();
        barContainer.addClassName("chart-bar-container");
        barContainer.setWidthFull();

        // Calculate utilization percentage from chart data
        double utilization = (data.getSpent() / data.getBudget()) * 100;

        // Enhanced bar header
        HorizontalLayout barHeader = new HorizontalLayout();
        barHeader.setWidthFull();
        barHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        barHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        barHeader.setPadding(false);
        barHeader.setSpacing(false);
        barHeader.addClassName("chart-bar-header");

        // Left side - Section info
        HorizontalLayout sectionInfo = new HorizontalLayout();
        sectionInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionInfo.setSpacing(true);
        sectionInfo.setPadding(false);

        Icon sectionIcon = getSectionIcon(data.getName());
        sectionIcon.addClassName("chart-section-icon");

        VerticalLayout sectionDetails = new VerticalLayout();
        sectionDetails.setSpacing(false);
        sectionDetails.setPadding(false);

        Span sectionName = new Span(data.getName());
        sectionName.addClassName("chart-section-name");

        Span utilizationText = new Span(String.format("%.1f%% utilized", utilization));
        utilizationText.addClassName("chart-section-utilization");
        utilizationText.getStyle().set("color", getUtilizationColor(utilization));

        sectionDetails.add(sectionName, utilizationText);
        sectionInfo.add(sectionIcon, sectionDetails);

        // Right side - Amount details
        HorizontalLayout amounts = new HorizontalLayout();
        amounts.setAlignItems(FlexComponent.Alignment.CENTER);
        amounts.setSpacing(true);
        amounts.setPadding(false);
        amounts.addClassName("chart-amounts");

        VerticalLayout budgetInfo = new VerticalLayout();
        budgetInfo.setSpacing(false);
        budgetInfo.setPadding(false);
        budgetInfo.setAlignItems(FlexComponent.Alignment.END);

        Span budgetLabel = new Span("Budget");
        budgetLabel.addClassName("amount-label");

        Span budgetAmount = new Span(formatCurrency(data.getBudget()));
        budgetAmount.addClassName("amount-value");
        budgetAmount.addClassName("budget-amount");

        budgetInfo.add(budgetLabel, budgetAmount);

        VerticalLayout spentInfo = new VerticalLayout();
        spentInfo.setSpacing(false);
        spentInfo.setPadding(false);
        spentInfo.setAlignItems(FlexComponent.Alignment.END);

        Span spentLabel = new Span("Spent");
        spentLabel.addClassName("amount-label");

        Span spentAmount = new Span(formatCurrency(data.getSpent()));
        spentAmount.addClassName("amount-value");
        spentAmount.addClassName("spent-amount");

        spentInfo.add(spentLabel, spentAmount);

        VerticalLayout remainingInfo = new VerticalLayout();
        remainingInfo.setSpacing(false);
        remainingInfo.setPadding(false);
        remainingInfo.setAlignItems(FlexComponent.Alignment.END);

        Span remainingLabel = new Span("Remaining");
        remainingLabel.addClassName("amount-label");

        Span remainingAmount = new Span(formatCurrency(data.getBudget() - data.getSpent()));
        remainingAmount.addClassName("amount-value");
        remainingAmount.addClassName("remaining-amount");

        remainingInfo.add(remainingLabel, remainingAmount);

        amounts.add(budgetInfo, spentInfo, remainingInfo);
        barHeader.add(sectionInfo, amounts);

        // Enhanced progress bars with multiple layers
        Div barsContainer = new Div();
        barsContainer.addClassName("chart-bars-container");
        barsContainer.setWidthFull();

        // Background track
        Div trackContainer = new Div();
        trackContainer.addClassName("chart-track-container");
        trackContainer.setWidthFull();

        // Budget bar (background)
        Div budgetBar = new Div();
        budgetBar.addClassName("chart-budget-bar");
        double budgetWidth = (data.getBudget() / maxBudget) * 100;
        budgetBar.getStyle().set("width", budgetWidth + "%");

        // Spent bar (foreground)
        Div spentBar = new Div();
        spentBar.addClassName("chart-spent-bar");
        double spentWidth = (data.getSpent() / maxBudget) * 100;
        spentBar.getStyle().set("width", spentWidth + "%");

        // Committed bar (overlay)
        double committedAmount = data.getBudget() * 0.1; // Mock committed amount
        Div committedBar = new Div();
        committedBar.addClassName("chart-committed-bar");
        double committedWidth = (committedAmount / maxBudget) * 100;
        committedBar.getStyle().set("width", committedWidth + "%");

        // Add status class based on utilization
        String statusClass = getBarStatusClass(utilization);
        spentBar.addClassName(statusClass);

        trackContainer.add(budgetBar);
        barsContainer.add(trackContainer, spentBar, committedBar);

        // Performance indicator
        HorizontalLayout performanceRow = new HorizontalLayout();
        performanceRow.setWidthFull();
        performanceRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        performanceRow.setAlignItems(FlexComponent.Alignment.CENTER);
        performanceRow.setPadding(false);
        performanceRow.setSpacing(false);
        performanceRow.addClassName("chart-performance-row");

        HorizontalLayout performanceLeft = new HorizontalLayout();
        performanceLeft.setAlignItems(FlexComponent.Alignment.CENTER);
        performanceLeft.setSpacing(false);
        performanceLeft.setPadding(false);

        Icon performanceIcon = getPerformanceIcon(utilization);
        performanceIcon.addClassName("performance-icon");

        Span performanceText = new Span(getPerformanceText(utilization));
        performanceText.addClassName("performance-text");

        performanceLeft.add(performanceIcon, performanceText);

        double efficiency = utilization > 0 ? (data.getBudget() / data.getSpent()) * 100 : 0;
        Span efficiencyText = new Span(String.format("Efficiency: %.1f%%", Math.min(efficiency, 100)));
        efficiencyText.addClassName("efficiency-text");

        performanceRow.add(performanceLeft, efficiencyText);

        barContainer.add(barHeader, barsContainer, performanceRow);
        return barContainer;
    }

    private String getBarStatusClass(double utilization) {
        if (utilization > 100) {
            return "bar-over";
        }
        if (utilization > 90) {
            return "bar-critical";
        }
        if (utilization > 75) {
            return "bar-warning";
        }
        return "bar-good";
    }

    private Icon getPerformanceIcon(double utilization) {
        if (utilization > 100) {
            return new Icon(VaadinIcon.ARROW_DOWN);
        }
        if (utilization > 90) {
            return new Icon(VaadinIcon.WARNING);
        }
        if (utilization > 75) {
            return new Icon(VaadinIcon.MINUS);
        }
        return new Icon(VaadinIcon.ARROW_UP);
    }

    private String getPerformanceText(double utilization) {
        if (utilization > 100) {
            return "Over Budget";
        }
        if (utilization > 90) {
            return "Critical Level";
        }
        if (utilization > 75) {
            return "High Utilization";
        }
        if (utilization > 50) {
            return "Good Progress";
        }
        return "Low Utilization";
    }

    private Icon getSectionIcon(String sectionName) {
        switch (sectionName) {
            case "Recruitment":
                return new Icon(VaadinIcon.USERS);
            case "Training":
                return new Icon(VaadinIcon.ACADEMY_CAP);
            case "Benefits":
                return new Icon(VaadinIcon.HEART);
            case "Performance":
                return new Icon(VaadinIcon.TROPHY);
            case "Infrastructure":
                return new Icon(VaadinIcon.SERVER);
            case "Development":
                return new Icon(VaadinIcon.CODE);
            case "Security":
                return new Icon(VaadinIcon.SHIELD);
            case "Support":
                return new Icon(VaadinIcon.HEADSET);
            case "Operations":
                return new Icon(VaadinIcon.COG);
            case "Administration":
                return new Icon(VaadinIcon.OFFICE);
            case "Projects":
                return new Icon(VaadinIcon.TASKS);
            case "Maintenance":
                return new Icon(VaadinIcon.WRENCH);
            default:
                return new Icon(VaadinIcon.FOLDER);
        }
    }

    private void createTrendAnalysis() {
        Div trendCard = new Div();
        trendCard.addClassName("trend-analysis-card");

        VerticalLayout trendContent = new VerticalLayout();
        trendContent.setSpacing(true);
        trendContent.setPadding(false);
        trendContent.setWidthFull();

        HorizontalLayout trendHeader = new HorizontalLayout();
        trendHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        trendHeader.setSpacing(true);
        trendHeader.setPadding(false);

        Icon trendIcon = new Icon(VaadinIcon.TRENDING_UP);
        trendIcon.addClassName("trend-icon");

        H4 trendTitle = new H4("Budget Trend Analysis");
        trendTitle.addClassName("trend-title");

        trendHeader.add(trendIcon, trendTitle);

        // Trend metrics
        HorizontalLayout trendMetrics = new HorizontalLayout();
        trendMetrics.setWidthFull();
        trendMetrics.setSpacing(true);
        trendMetrics.setPadding(false);
        trendMetrics.addClassName("trend-metrics");

        double monthlyBurn = department.getTotalSpent() / 12; // Mock monthly burn rate
        double projectedSpend = monthlyBurn * 12;
        double variance = ((projectedSpend - department.getTotalBudget()) / department.getTotalBudget()) * 100;

        trendMetrics.add(
                createTrendMetric("Monthly Burn Rate", formatCurrency(monthlyBurn), VaadinIcon.FIRE, "#f59e0b"),
                createTrendMetric("Projected Annual", formatCurrency(projectedSpend), VaadinIcon.TRENDING_UP, "#3b82f6"),
                createTrendMetric("Budget Variance", String.format("%.1f%%", Math.abs(variance)),
                        variance > 0 ? VaadinIcon.ARROW_UP : VaadinIcon.ARROW_DOWN,
                        variance > 0 ? "#ef4444" : "#10b981")
        );

        trendContent.add(trendHeader, trendMetrics);
        trendCard.add(trendContent);
        add(trendCard);
    }

    private VerticalLayout createTrendMetric(String label, String value, VaadinIcon iconType, String color) {
        VerticalLayout metric = new VerticalLayout();
        metric.setSpacing(false);
        metric.setPadding(false);
        metric.addClassName("trend-metric");
        metric.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon icon = new Icon(iconType);
        icon.addClassName("trend-metric-icon");
        icon.getStyle().set("color", color);

        Span valueSpan = new Span(value);
        valueSpan.addClassName("trend-metric-value");
        valueSpan.getStyle().set("color", color);

        Span labelSpan = new Span(label);
        labelSpan.addClassName("trend-metric-label");

        metric.add(icon, valueSpan, labelSpan);
        return metric;
    }

    private void createInsights() {
        Div insightsCard = new Div();
        insightsCard.addClassName("insights-card");

        VerticalLayout insightsContent = new VerticalLayout();
        insightsContent.setSpacing(true);
        insightsContent.setPadding(false);
        insightsContent.setWidthFull();

        HorizontalLayout insightsHeader = new HorizontalLayout();
        insightsHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        insightsHeader.setSpacing(true);
        insightsHeader.setPadding(false);
        insightsHeader.addClassName("insights-header");

        Icon insightIcon = new Icon(VaadinIcon.LIGHTBULB);
        insightIcon.addClassName("insight-icon");

        H4 insightsTitle = new H4("System Powered Budget Insights");
        insightsTitle.addClassName("insights-title");

        Span insightsSubtitle = new Span("Smart recommendations based on spending patterns");
        insightsSubtitle.addClassName("insights-subtitle");

        VerticalLayout insightsTitleGroup = new VerticalLayout();
        insightsTitleGroup.setSpacing(false);
        insightsTitleGroup.setPadding(false);
        insightsTitleGroup.add(insightsTitle, insightsSubtitle);

        insightsHeader.add(insightIcon, insightsTitleGroup);

        // Enhanced insights with priority levels
        VerticalLayout insightsList = new VerticalLayout();
        insightsList.setSpacing(false);
        insightsList.setPadding(false);
        insightsList.setWidthFull();
        insightsList.addClassName("insights-list");

        double spentPercentage = department.getSpentPercentage();
        List<InsightItem> insights = generateInsights(spentPercentage);

        for (InsightItem insight : insights) {
            insightsList.add(createEnhancedInsightItem(insight));
        }

        insightsContent.add(insightsHeader, insightsList);
        insightsCard.add(insightsContent);
        add(insightsCard);
    }

    private List<InsightItem> generateInsights(double spentPercentage) {
        List<InsightItem> insights = new ArrayList<>();

        if (spentPercentage > 95) {
            insights.add(new InsightItem("critical", "high",
                    "Critical Budget Alert",
                    "Department has exceeded 95% budget utilization. Immediate action required.",
                    "Consider budget reallocation or spending freeze"));
        } else if (spentPercentage > 85) {
            insights.add(new InsightItem("warning", "high",
                    "High Utilization Warning",
                    "Budget utilization is above 85%. Monitor spending closely.",
                    "Review upcoming expenses and consider adjustments"));
        } else if (spentPercentage > 75) {
            insights.add(new InsightItem("info", "medium",
                    "Good Budget Progress",
                    "Budget utilization is on track at " + String.format("%.1f%%", spentPercentage) + ".",
                    "Continue monitoring for optimal performance"));
        } else if (spentPercentage < 40) {
            insights.add(new InsightItem("warning", "medium",
                    "Low Utilization Alert",
                    "Budget utilization is below 40%. Consider accelerating activities.",
                    "Review planned activities and timeline"));
        }

        if (department.getAvailableBudget() < 50_000_000) {
            insights.add(new InsightItem("error", "high",
                    "Low Available Budget",
                    "Available budget is below UGX 50M threshold.",
                    "Plan carefully for remaining fiscal period"));
        }

        insights.add(new InsightItem("success", "low",
                "Monitoring Recommendation",
                "Regular budget reviews ensure optimal financial management.",
                "Schedule weekly budget review meetings"));

        return insights;
    }

    private Div createEnhancedInsightItem(InsightItem insight) {
        Div item = new Div();
        item.addClassName("enhanced-insight-item");
        item.addClassName("insight-" + insight.getType());
        item.addClassName("priority-" + insight.getPriority());

        HorizontalLayout itemContent = new HorizontalLayout();
        itemContent.setWidthFull();
        itemContent.setAlignItems(FlexComponent.Alignment.START);
        itemContent.setSpacing(true);
        itemContent.setPadding(false);

        // Priority indicator
        Div priorityIndicator = new Div();
        priorityIndicator.addClassName("insight-priority-indicator");
        priorityIndicator.addClassName("priority-" + insight.getPriority());

        // Icon
        Icon icon = getInsightIcon(insight.getType());
        icon.addClassName("enhanced-insight-icon");

        // Content
        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);
        textContent.setFlexGrow(1);

        H5 title = new H5(insight.getTitle());
        title.addClassName("insight-item-title");

        Span message = new Span(insight.getMessage());
        message.addClassName("insight-item-message");

        if (insight.getRecommendation() != null) {
            Span recommendation = new Span("💡 " + insight.getRecommendation());
            recommendation.addClassName("insight-recommendation");
            textContent.add(title, message, recommendation);
        } else {
            textContent.add(title, message);
        }

        itemContent.add(priorityIndicator, icon, textContent);
        item.add(itemContent);

        return item;
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
    }

    private Icon getInsightIcon(String type) {
        switch (type) {
            case "warning":
                return new Icon(VaadinIcon.WARNING);
            case "error":
                return new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
            case "critical":
                return new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
            case "info":
                return new Icon(VaadinIcon.INFO_CIRCLE);
            case "success":
                return new Icon(VaadinIcon.CHECK_CIRCLE);
            default:
                return new Icon(VaadinIcon.INFO);
        }
    }

    private List<ChartData> generateChartData() {
        // Mock data based on department type
        if ("HR".equals(department.getDepartmentCode())) {
            return Arrays.asList(
                    new ChartData("Recruitment", 80_000_000, 65_000_000),
                    new ChartData("Training", 120_000_000, 89_000_000),
                    new ChartData("Benefits", 60_000_000, 42_000_000),
                    new ChartData("Performance", 40_000_000, 28_000_000)
            );
        } else if ("IT".equals(department.getDepartmentCode())) {
            return Arrays.asList(
                    new ChartData("Infrastructure", 200_000_000, 178_000_000),
                    new ChartData("Development", 150_000_000, 125_000_000),
                    new ChartData("Security", 80_000_000, 67_000_000),
                    new ChartData("Support", 50_000_000, 35_000_000)
            );
        } else {
            return Arrays.asList(
                    new ChartData("Operations", 100_000_000, 75_000_000),
                    new ChartData("Administration", 80_000_000, 60_000_000),
                    new ChartData("Projects", 120_000_000, 95_000_000),
                    new ChartData("Maintenance", 60_000_000, 45_000_000)
            );
        }
    }

    private String formatCurrency(double amount) {
        if (Math.abs(amount) >= 1_000_000_000) {
            return String.format("UGX %.1fB", amount / 1_000_000_000);
        } else if (Math.abs(amount) >= 1_000_000) {
            return String.format("UGX %.1fM", amount / 1_000_000);
        } else if (Math.abs(amount) >= 1_000) {
            return String.format("UGX %.0fK", amount / 1_000);
        } else {
            return String.format("UGX %.0f", amount);
        }
    }

    private static class ChartData {

        private final String name;
        private final double budget;
        private final double spent;

        public ChartData(String name, double budget, double spent) {
            this.name = name;
            this.budget = budget;
            this.spent = spent;
        }

        public String getName() {
            return name;
        }

        public double getBudget() {
            return budget;
        }

        public double getSpent() {
            return spent;
        }
    }

    private static class InsightItem {

        private final String type;
        private final String priority;
        private final String title;
        private final String message;
        private final String recommendation;

        public InsightItem(String type, String priority, String title, String message, String recommendation) {
            this.type = type;
            this.priority = priority;
            this.title = title;
            this.message = message;
            this.recommendation = recommendation;
        }

        public String getType() {
            return type;
        }

        public String getPriority() {
            return priority;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getRecommendation() {
            return recommendation;
        }
    }
}
