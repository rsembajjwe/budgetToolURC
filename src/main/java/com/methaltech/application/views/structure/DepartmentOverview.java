package com.methaltech.application.views.structure;

import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DepartmentGeneralPhysicalPerformanceService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.DepartmentGeneralPhysicalPerformance;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.views.actual.utilityActuals;
import com.methaltech.application.views.budget.Component.QuillEditorField;
import com.methaltech.application.views.budget.GraphCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class DepartmentOverview extends VerticalLayout {

    private final NumberFormat currencyFormat;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final Budget budget;
    private final BudgetItemsService budgetItemsService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final DepartmentGeneralPhysicalPerformanceService departmentGeneralPhysicalPerformanceService;

    private utilityActuals utils;
    private final String selectedQuarter;

    @Autowired
    public DepartmentOverview(
            List<DepartmentBudget> departmentBudgets,
            CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            SALFLDGService sampleSALFLDGService,
            DeptSectionMergerService sampleDeptSectionMergerService,
            Budget budget,
            BudgetItemsService budgetItemsService,
            Urc_ActivitiesService sampleUrc_ActivitiesService,
            DepartmentGeneralPhysicalPerformanceService departmentGeneralPhysicalPerformanceService,
            String selectedQuarter
    ) {
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budget = budget;
        this.budgetItemsService = budgetItemsService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.departmentGeneralPhysicalPerformanceService = departmentGeneralPhysicalPerformanceService;
        this.selectedQuarter = selectedQuarter;

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

        Span subtitle = new Span(
                "Budget allocation, utilization, and department performance analysis for " + getQuarterLabel() + "."
        );
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

        for (DepartmentBudget dept : departmentBudgets) {
            BigDecimal deptBudget = nz(dept.getTotalBudget());
            BigDecimal deptSpent = getSelectedDepartmentSpent(dept);
            BigDecimal deptAvailable = getSelectedDepartmentAvailable(dept);
            BigDecimal deptPct = getSelectedDepartmentSpentPercentage(dept);

            totalBudget = totalBudget.add(deptBudget);
            totalSpent = totalSpent.add(deptSpent);
            totalAvailable = totalAvailable.add(deptAvailable);

            if (deptPct.compareTo(BigDecimal.valueOf(100)) > 0) {
                overBudgetCount++;
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
        budgetInfo.setWidth(20, Unit.PERCENTAGE);
        budgetInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        budgetInfo.addClassName("budget-info-row");

        VerticalLayout spentItem = createBudgetItem("Spent", getSelectedDepartmentSpent(department));
        spentItem.getStyle().set("cursor", "pointer");
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
                createBudgetItem("Available", getSelectedDepartmentAvailable(department))
        );

        HorizontalLayout financialLayout = new HorizontalLayout();
        financialLayout.setWidthFull();
        financialLayout.setSpacing(true);
        financialLayout.setPadding(false);

        financialLayout.add(budgetInfo, budgetCoaQtrGeneral(budget, department.getSections()));
        financialLayout.setFlexGrow(0, budgetInfo);
        financialLayout.setFlexGrow(1, budgetCoaQtrGeneral(budget, department.getSections()));

        if (hasBudgetControls(department)) {
            HorizontalLayout controlsInfo = createBudgetControls(department);
            financialLayout.add(controlsInfo);
        }

        VerticalLayout progressSection = createProgressSection(department);

        VerticalLayout financialTabContent = new VerticalLayout();
        financialTabContent.setWidthFull();
        financialTabContent.setPadding(false);
        financialTabContent.setSpacing(true);
        financialTabContent.add(financialLayout, progressSection);

        VerticalLayout physicalTabContent = createPhysicalBudgetPerformanceContent(department);
        Component generalPhysicalTabContent = createGeneralPhysicalPerformanceContent(department);

        Tab financialTab = new Tab("Financial Budget Performance");
        Tab physicalTab = new Tab("Physical Budget Performance");
        Tab generalPhysicalTab = new Tab("General Physical Performance");

        Tabs tabs = new Tabs(financialTab, physicalTab, generalPhysicalTab);
        tabs.setWidthFull();

        Div pages = new Div();
        pages.setWidthFull();

        financialTabContent.setVisible(true);
        physicalTabContent.setVisible(false);
        generalPhysicalTabContent.setVisible(false);

        pages.add(financialTabContent, physicalTabContent, generalPhysicalTabContent);

        tabs.addSelectedChangeListener(event -> {
            Tab selected = event.getSelectedTab();
            financialTabContent.setVisible(selected == financialTab);
            physicalTabContent.setVisible(selected == physicalTab);
            generalPhysicalTabContent.setVisible(selected == generalPhysicalTab);
        });

        leftContent.add(deptHeader, tabs, pages);

        Icon chevronIcon = new Icon(VaadinIcon.CHEVRON_RIGHT);
        chevronIcon.addClassName("chevron-icon");

        content.add(leftContent, chevronIcon);
        content.setFlexGrow(1, leftContent);

        card.add(content);
        return card;
    }

    private VerticalLayout createPhysicalBudgetPerformanceContent(DepartmentBudget department) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.addClassName("physical-budget-performance");

        List<Urc_Activities> activities = sampleUrc_ActivitiesService.findByDeptSectionAndBudget(
                department.getSections(), budget
        );

        Span title = new Span("Physical Budget Performance - " + getQuarterLabel());
        title.getStyle()
                .set("font-weight", "700")
                .set("font-size", "1rem");

        Component summary = createPhysicalSummary(activities);
        Grid<Urc_Activities> grid = createPhysicalActivitiesGrid(activities);

        layout.add(title, summary, grid);
        return layout;
    }

    private Grid<Urc_Activities> createPhysicalActivitiesGrid(List<Urc_Activities> activities) {
        Grid<Urc_Activities> grid = new Grid<>(Urc_Activities.class, false);
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        Grid.Column<Urc_Activities> nameColumn = grid.addColumn(a -> nvl(a.getName(), ""))
                .setHeader("Activity")
                .setWidth("260px")
                .setFlexGrow(0)
                .setFrozen(true)
                .setSortable(true);

        grid.addColumn(a -> nvl(a.getOutput(), ""))
                .setHeader("Output")
                .setWidth("220px");

        grid.addColumn(a -> nvl(a.getPerformanceIndicator(), ""))
                .setHeader("Performance Indicator")
                .setWidth("220px");

        grid.addColumn(a -> nvl(a.getAnnualTarget(), ""))
                .setHeader("Annual Target")
                .setWidth("160px");

        grid.addColumn(a -> nvl(getAchievementForSelectedQuarter(a), ""))
                .setHeader(getQuarterLabel() + " Achievement")
                .setWidth("180px");

        grid.addColumn(a -> formatPercentage(getPhysicalPctForSelectedQuarter(a)))
                .setHeader(getQuarterLabel() + " % Achieved")
                .setWidth("130px");

        grid.addColumn(new ComponentRenderer<>(this::createPhysicalStatusBadge))
                .setHeader("Status")
                .setWidth("130px")
                .setFlexGrow(0);

        grid.addColumn(a -> formatCurrency(nz(a.getActivity_budget())))
                .setHeader("Budget")
                .setWidth("140px");

        grid.addColumn(a -> formatCurrency(getActualForSelectedQuarter(a)))
                .setHeader(getQuarterLabel() + " Actual")
                .setWidth("140px");

        grid.addColumn(a -> {
            BigDecimal variance = nz(a.getActivity_budget()).subtract(getActualForSelectedQuarter(a));
            return formatCurrency(variance);
        }).setHeader("Variance").setWidth("140px");

        grid.addColumn(a -> nvl(getVariationExplanationForSelectedQuarter(a), ""))
                .setHeader(getQuarterLabel() + " Variation Explanation")
                .setWidth("260px");

        grid.addComponentColumn(activity -> {
            Button btn = new Button("Details");
            btn.addClickListener(e -> grid.setDetailsVisible(activity, !grid.isDetailsVisible(activity)));
            return btn;
        }).setHeader("More").setWidth("100px").setFlexGrow(0);

        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::createPhysicalActivityDetails));

        grid.setItems(activities);

        FooterRow footerRow = grid.appendFooterRow();

        BigDecimal totalBudget = sumActivityBudget(activities);
        BigDecimal totalActual = sumActivityActualForSelectedQuarter(activities);
        BigDecimal totalVariance = totalBudget.subtract(totalActual);

        footerRow.getCell(nameColumn).setComponent(footerText("TOTAL"));

        Grid.Column<Urc_Activities> budgetColumn = grid.getColumns().get(7);
        Grid.Column<Urc_Activities> actualColumn = grid.getColumns().get(8);
        Grid.Column<Urc_Activities> varianceColumn = grid.getColumns().get(9);

        footerRow.getCell(budgetColumn).setComponent(footerText(formatCurrency(totalBudget)));
        footerRow.getCell(actualColumn).setComponent(footerActualText(formatCurrency(totalActual)));
        footerRow.getCell(varianceColumn).setComponent(footerBalanceText(formatCurrency(totalVariance)));

        return grid;
    }

    private Component createPhysicalSummary(List<Urc_Activities> activities) {
        long achieved = activities.stream()
                .filter(a -> getPhysicalPctForSelectedQuarter(a) >= 100.0)
                .count();

        long onTrack = activities.stream()
                .filter(a -> {
                    double pct = getPhysicalPctForSelectedQuarter(a);
                    return pct >= 50.0 && pct < 100.0;
                })
                .count();

        long delayed = activities.stream()
                .filter(a -> {
                    double pct = getPhysicalPctForSelectedQuarter(a);
                    return pct > 0.0 && pct < 50.0;
                })
                .count();

        long notStarted = activities.stream()
                .filter(a -> getPhysicalPctForSelectedQuarter(a) <= 0.0)
                .count();

        BigDecimal totalBudget = sumActivityBudget(activities);
        BigDecimal totalActual = sumActivityActualForSelectedQuarter(activities);

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.setPadding(false);
        row.addClassName("physical-summary-row");

        row.add(
                createPhysicalMetricCard("Activities", String.valueOf(activities.size())),
                createPhysicalMetricCard("Achieved", String.valueOf(achieved)),
                createPhysicalMetricCard("On Track", String.valueOf(onTrack)),
                createPhysicalMetricCard("Delayed", String.valueOf(delayed)),
                createPhysicalMetricCard("Not Started", String.valueOf(notStarted)),
                createPhysicalMetricCard("Budget", formatCurrency(totalBudget)),
                createPhysicalMetricCard(getQuarterLabel() + " Actual", formatCurrency(totalActual))
        );

        return row;
    }

    private Div createPhysicalMetricCard(String label, String value) {
        Div card = new Div();
        card.addClassName("physical-metric-card");
        card.getStyle()
                .set("padding", "12px 16px")
                .set("border-radius", "12px")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("min-width", "140px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("display", "block")
                .set("font-size", "1.1rem")
                .set("font-weight", "700");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("display", "block")
                .set("font-size", "0.85rem")
                .set("color", "var(--lumo-secondary-text-color)");

        card.add(valueSpan, labelSpan);
        return card;
    }

    private Component createPhysicalStatusBadge(Urc_Activities activity) {
        double pct = getPhysicalPctForSelectedQuarter(activity);

        Span badge = new Span();
        badge.getStyle()
                .set("font-weight", "700")
                .set("padding", "4px 10px")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("display", "inline-block");

        if (pct >= 100.0) {
            badge.setText("ACHIEVED");
            badge.getStyle()
                    .set("background-color", "var(--lumo-success-color-10pct)")
                    .set("color", "var(--lumo-success-text-color)");
        } else if (pct >= 75.0) {
            badge.setText("ON TRACK");
            badge.getStyle()
                    .set("background-color", "var(--lumo-primary-color-10pct)")
                    .set("color", "var(--lumo-primary-text-color)");
        } else if (pct > 0.0) {
            badge.setText("DELAYED");
            badge.getStyle()
                    .set("background-color", "var(--lumo-warning-color-10pct)")
                    .set("color", "var(--lumo-warning-text-color)");
        } else {
            badge.setText("NOT STARTED");
            badge.getStyle()
                    .set("background-color", "var(--lumo-contrast-10pct)")
                    .set("color", "var(--lumo-secondary-text-color)");
        }

        return badge;
    }

    private Component createGeneralPhysicalPerformanceContent(DepartmentBudget department) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        layout.setSpacing(true);

        String deptCode = nvl(department.getDepartmentCode(), "");
        String deptName = nvl(department.getDepartmentName(), "");

        Span title = new Span("General Budget Physical Performance Narrative");
        title.getStyle()
                .set("font-weight", "700")
                .set("font-size", "1rem");

        Span deptInfo = new Span("Department: " + deptCode + " - " + deptName);
        deptInfo.getStyle().set("color", "var(--lumo-secondary-text-color)");

        ComboBox<String> quarterBox = new ComboBox<>("Quarter");
        quarterBox.setItems("qtr1", "qtr2", "qtr3", "qtr4");
        quarterBox.setValue("qtr1");
        quarterBox.setWidth("200px");

        QuillEditorField editor = new QuillEditorField();
        editor.setWidth("100%");
        editor.setHeight("300px");
        editor.addClassName("rich-editor");
        editor.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        editor.setPlaceholder("Write the general budget physical performance narrative for the selected quarter...");

        /*        Div preview = new Div();
        preview.setWidthFull();
        preview.getStyle()
        .set("border", "1px solid var(--lumo-contrast-20pct)")
        .set("border-radius", "8px")
        .set("padding", "12px")
        .set("min-height", "120px")
        .set("background", "var(--lumo-base-color)");*/
        String template = """
        <h3>Summary</h3>
        <p></p>

        <h3>Key Achievements</h3>
        <ul><li></li></ul>

        <h3>Challenges</h3>
        <ul><li></li></ul>

        <h3>Way Forward</h3>
        <ul><li></li></ul>
        """;

        String initialHtml = departmentGeneralPhysicalPerformanceService.getQuarterHtml(deptCode, "qtr1", budget);
        String initialContent = (initialHtml == null || initialHtml.isBlank()) ? template : initialHtml;

        editor.setValue(initialContent);
        //preview.getElement().setProperty("innerHTML", initialContent);

        quarterBox.addValueChangeListener(event -> {
            String quarter = event.getValue();
            String html = departmentGeneralPhysicalPerformanceService.getQuarterHtml(deptCode, quarter, budget);
            String content = (html == null || html.isBlank()) ? template : html;

            editor.setValue(content);
            // preview.getElement().setProperty("innerHTML", content);
        });

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button previewButton = new Button("Preview");

        saveButton.addClickListener(e -> {
            editor.getElement().executeJs("return this.value").then(String.class, html -> {
                String content = (html == null || html.isBlank()) ? "" : html;
                String quarter = quarterBox.getValue();

                departmentGeneralPhysicalPerformanceService.saveQuarter(
                        deptCode,
                        deptName,
                        quarter,
                        content,
                        budget
                );

                //preview.getElement().setProperty("innerHTML", content);
                Notification.show("Narrative saved");
            });
        });

        previewButton.addClickListener(e -> {
            editor.getElement().executeJs("return this.value").then(String.class, html -> {
                String content = html == null ? "" : html;
                // preview.getElement().setProperty("innerHTML", content);
            });
        });

        HorizontalLayout actions = new HorizontalLayout(quarterBox, saveButton, previewButton);
        actions.setAlignItems(FlexComponent.Alignment.END);
        actions.setSpacing(true);
        actions.setPadding(false);

        layout.add(title, deptInfo, actions, editor);
        return layout;
    }

    private double nz(Double value) {
        return value == null ? 0.0 : value;
    }

    private double pct(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private Component createPhysicalActivityDetails(Urc_Activities activity) {
        VerticalLayout details = new VerticalLayout();
        details.setWidthFull();
        details.setPadding(true);
        details.setSpacing(true);
        details.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "8px");

        details.add(
                createDetailLine("Selected Quarter", getQuarterLabel()),
                createDetailLine("Achievement", nvl(getAchievementForSelectedQuarter(activity), "-")),
                createDetailLine("% Achieved", formatPercentage(getPhysicalPctForSelectedQuarter(activity))),
                createDetailLine("Variation", nvl(getVariationExplanationForSelectedQuarter(activity), "-")),
                createDetailLine("Q1 Achievement", nvl(activity.getCum_achievements_qtr1(), "-")),
                createDetailLine("Q1 % Achieved", nvl(activity.getPerc_of_TargetAchieved_qtr1(), "-")),
                createDetailLine("Q2 Achievement", nvl(activity.getCum_achievements_qtr2(), "-")),
                createDetailLine("Q2 % Achieved", nvl(activity.getPerc_of_TargetAchieved_qtr2(), "-")),
                createDetailLine("Q3 Achievement", nvl(activity.getCum_achievements_qtr3(), "-")),
                createDetailLine("Q3 % Achieved", nvl(activity.getPerc_of_TargetAchieved_qtr3(), "-")),
                createDetailLine("Q4 Achievement", nvl(activity.getCum_achievements_qtr4(), "-")),
                createDetailLine("Q4 % Achieved", nvl(activity.getPerc_of_TargetAchieved_qtr4(), "-")),
                createDetailLine("Objective", nvl(activity.getObjective(), "-")),
                createDetailLine("Outcome", nvl(activity.getOutcome(), "-")),
                createDetailLine("Funding Source", nvl(activity.getFundsource(), "-"))
        );

        return details;
    }

    private String getAchievementForSelectedQuarter(Urc_Activities activity) {
        if (activity == null) {
            return "";
        }

        return switch (selectedQuarter != null ? selectedQuarter.toLowerCase() : "") {
            case "qtr1" ->
                nvl(activity.getCum_achievements_qtr1(), "");
            case "qtr2" ->
                nvl(activity.getCum_achievements_qtr2(), "");
            case "qtr3" ->
                nvl(activity.getCum_achievements_qtr3(), "");
            case "qtr4" ->
                nvl(activity.getCum_achievements_qtr4(), "");
            default ->
                nvl(activity.getCum_achievements_qtr1(), "");
        };
    }

    private String getVariationExplanationForSelectedQuarter(Urc_Activities activity) {
        if (activity == null) {
            return "";
        }

        return switch (selectedQuarter != null ? selectedQuarter.toLowerCase() : "") {
            case "qtr1" ->
                nvl(activity.getExpl_of_variations_qtr1(), "");
            case "qtr2" ->
                nvl(activity.getExpl_of_variations_qtr2(), "");
            case "qtr3" ->
                nvl(activity.getExpl_of_variations_qtr3(), "");
            case "qtr4" ->
                nvl(activity.getExpl_of_variations_qtr4(), "");
            default ->
                nvl(activity.getExpl_of_variations_qtr1(), "");
        };
    }

    private double getPhysicalPctForSelectedQuarter(Urc_Activities activity) {
        if (activity == null) {
            return 0.0;
        }

        return switch (selectedQuarter != null ? selectedQuarter.toLowerCase() : "") {
            case "qtr1" ->
                nz(activity.getPerc_of_TargetAchieved_qtr11());
            case "qtr2" ->
                nz(activity.getPerc_of_TargetAchieved_qtr21());
            case "qtr3" ->
                nz(activity.getPerc_of_TargetAchieved_qtr31());
            case "qtr4" ->
                nz(activity.getPerc_of_TargetAchieved_qtr41());
            default ->
                nz(activity.getPerc_of_TargetAchieved_qtr11());
        };
    }

    private BigDecimal getActualForSelectedQuarter(Urc_Activities activity) {
        if (activity == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal value = switch (selectedQuarter != null ? selectedQuarter.toLowerCase() : "") {
            case "qtr1" ->
                nz(activity.getQtr1A());
            case "qtr2" ->
                nz(activity.getQtr1A()).add(nz(activity.getQtr2A()));
            case "qtr3" ->
                nz(activity.getQtr1A()).add(nz(activity.getQtr2A())).add(nz(activity.getQtr3A()));
            case "qtr4" ->
                nz(activity.getQtr1A()).add(nz(activity.getQtr2A())).add(nz(activity.getQtr3A())).add(nz(activity.getQtr4A()));
            default ->
                nz(activity.getQtr1A());
        };

        return invert(value);
    }

    private BigDecimal sumActivityActualForSelectedQuarter(List<Urc_Activities> activities) {
        return safeListActivities(activities).stream()
                .map(this::getActualForSelectedQuarter)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Urc_Activities> safeListActivities(List<Urc_Activities> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private String getLatestAchievement(Urc_Activities activity) {
        if (!isBlank(activity.getCum_achievements_qtr4())) {
            return activity.getCum_achievements_qtr4();
        }
        if (!isBlank(activity.getCum_achievements_qtr3())) {
            return activity.getCum_achievements_qtr3();
        }
        if (!isBlank(activity.getCum_achievements_qtr2())) {
            return activity.getCum_achievements_qtr2();
        }
        return activity.getCum_achievements_qtr1();
    }

    private String getLatestVariationExplanation(Urc_Activities activity) {
        if (!isBlank(activity.getExpl_of_variations_qtr4())) {
            return activity.getExpl_of_variations_qtr4();
        }
        if (!isBlank(activity.getExpl_of_variations_qtr3())) {
            return activity.getExpl_of_variations_qtr3();
        }
        if (!isBlank(activity.getExpl_of_variations_qtr2())) {
            return activity.getExpl_of_variations_qtr2();
        }
        return activity.getExpl_of_variations_qtr1();
    }

    private double getLatestPhysicalPct(Urc_Activities activity) {
        double q4 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr4());
        double q3 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr3());
        double q2 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr2());
        double q1 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr1());

        if (q4 > 0) {
            return q4;
        }
        if (q3 > 0) {
            return q3;
        }
        if (q2 > 0) {
            return q2;
        }
        return q1;
    }

    private Component createDetailLine(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.setPadding(false);

        Span labelSpan = new Span(label + ":");
        labelSpan.getStyle()
                .set("font-weight", "700")
                .set("min-width", "180px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("flex", "1");

        row.add(labelSpan, valueSpan);
        row.expand(valueSpan);
        return row;
    }

    private double parsePercentage(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }

        try {
            String cleaned = value.replace("%", "").replace(",", "").trim();
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private BigDecimal sumActivityBudget(List<Urc_Activities> activities) {
        return activities.stream()
                .map(Urc_Activities::getActivity_budget)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumActivityActual(List<Urc_Activities> activities) {
        return activities.stream()
                .map(Urc_Activities::getTotalA)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatPercentage(double value) {
        return String.format(Locale.US, "%.1f%%", value);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
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

        String statusText = getSelectedDepartmentStatusText(department);
        String statusClass = getSelectedDepartmentStatusClass(department);

        Span statusLabel = new Span(statusText);
        statusLabel.addClassName(statusClass);

        if (!"On Track".equalsIgnoreCase(statusText)) {
            Icon alertIcon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
            alertIcon.setSize("0.875rem");
            statusContainer.add(alertIcon);
        }

        BigDecimal spentPct = getSelectedDepartmentSpentPercentage(department);
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
                .map(d -> toMillions(getSelectedDepartmentSpent(d)))
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
                .map(d -> getSelectedDepartmentSpentPercentage(d).toPlainString())
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
        if (name == null) {
            return "N/A";
        }
        return name.length() > 14 ? name.substring(0, 14) + "..." : name;
    }

    private String createDepartmentStatusConfig(List<DepartmentBudget> departments) {
        long onTrack = departments.stream()
                .filter(d -> "On Track".equalsIgnoreCase(getSelectedDepartmentStatusText(d)))
                .count();

        long near = departments.stream()
                .filter(d -> "Near Limit".equalsIgnoreCase(getSelectedDepartmentStatusText(d)))
                .count();

        long critical = departments.stream()
                .filter(d -> "Critical".equalsIgnoreCase(getSelectedDepartmentStatusText(d)))
                .count();

        long over = departments.stream()
                .filter(d -> "Over Budget".equalsIgnoreCase(getSelectedDepartmentStatusText(d)))
                .count();

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

    private Component budgetCoaQtrView(Set<UrcDeptSectionAnlDimbgt> deptSections) {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);

        Grid<BudgetItemsActuals> gridBudgetItemsQuarterlyGrid = new Grid<>(BudgetItemsActuals.class, false);
        gridBudgetItemsQuarterlyGrid.setWidthFull();
        gridBudgetItemsQuarterlyGrid.addThemeVariants(
                GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_ROW_STRIPES
        );

        Grid.Column<BudgetItemsActuals> codeColumn;
        Grid.Column<BudgetItemsActuals> descColumn;
        Grid.Column<BudgetItemsActuals> qtr1Column;
        Grid.Column<BudgetItemsActuals> qtr1AColumn;
        Grid.Column<BudgetItemsActuals> qtr2Column;
        Grid.Column<BudgetItemsActuals> qtr2AColumn;
        Grid.Column<BudgetItemsActuals> qtr3Column;
        Grid.Column<BudgetItemsActuals> qtr3AColumn;
        Grid.Column<BudgetItemsActuals> qtr4Column;
        Grid.Column<BudgetItemsActuals> qtr4AColumn;
        Grid.Column<BudgetItemsActuals> totalQtrColumn;
        Grid.Column<BudgetItemsActuals> totalAQtrColumn;
        Grid.Column<BudgetItemsActuals> balanceColumn;
        Grid.Column<BudgetItemsActuals> statusColumn;

        codeColumn = gridBudgetItemsQuarterlyGrid.addColumn(item -> {
            COA coacode = item.getCoacode();
            return coacode != null ? coacode.getCode() : "";
        })
                .setHeader("Code")
                .setWidth("80px")
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator((a, b) -> {
                    String code1 = a.getCoacode() != null ? a.getCoacode().getCode() : "";
                    String code2 = b.getCoacode() != null ? b.getCoacode().getCode() : "";
                    return code1.compareTo(code2);
                });

        descColumn = gridBudgetItemsQuarterlyGrid.addColumn(BudgetItemsActuals::getItem)
                .setHeader("Description")
                .setWidth("250px");

        qtr1Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item
                -> createSpan(item.getQtr1())
        )).setHeader("Qtr1").setWidth("150px");

        qtr1AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(item.getQtr1A());
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Qtr1 Actual").setWidth("150px");

        qtr2Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item
                -> createSpan(item.getQtr2())
        )).setHeader("Qtr2").setWidth("150px");

        qtr2AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(item.getQtr2A());
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Qtr2 Actual").setWidth("150px");

        qtr3Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item
                -> createSpan(item.getQtr3())
        )).setHeader("Qtr3").setWidth("150px");

        qtr3AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(item.getQtr3A());
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Qtr3 Actual").setWidth("150px");

        qtr4Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item
                -> createSpan(item.getQtr4())
        )).setHeader("Qtr4").setWidth("150px");

        qtr4AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(item.getQtr4A());
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Qtr4 Actual").setWidth("150px");

        totalQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item
                -> createSpan(item.getTotal())
        )).setHeader("Total").setWidth("150px");

        totalAQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(item.getTotalA());
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Total Actual").setWidth("150px");

        balanceColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            BigDecimal budgetVal = nz(item.getTotal());
            BigDecimal actualVal = nz(item.getTotalA());
            BigDecimal balance = budgetVal.subtract(actualVal);

            Span span = createSpan(balance);
            span.getStyle().set("font-weight", "600");

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                span.getStyle().set("color", "var(--lumo-error-text-color)");
            } else {
                span.getStyle().set("color", "var(--lumo-success-text-color)");
            }
            return span;
        })).setHeader("Variance").setWidth("150px");

        statusColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("130px")
                .setFlexGrow(0);

        codeColumn.setFrozen(true);
        descColumn.setFrozen(true);

        gridBudgetItemsQuarterlyGrid.setItems(Collections.emptyList());

        FooterRow footerRow = gridBudgetItemsQuarterlyGrid.appendFooterRow();
        footerRow.getCell(codeColumn).setComponent(footerText(""));
        footerRow.getCell(descColumn).setComponent(footerText("TOTAL"));
        footerRow.getCell(qtr1Column).setComponent(footerText(""));
        footerRow.getCell(qtr1AColumn).setComponent(footerActualText(""));
        footerRow.getCell(qtr2Column).setComponent(footerText(""));
        footerRow.getCell(qtr2AColumn).setComponent(footerActualText(""));
        footerRow.getCell(qtr3Column).setComponent(footerText(""));
        footerRow.getCell(qtr3AColumn).setComponent(footerActualText(""));
        footerRow.getCell(qtr4Column).setComponent(footerText(""));
        footerRow.getCell(qtr4AColumn).setComponent(footerActualText(""));
        footerRow.getCell(totalQtrColumn).setComponent(footerText(""));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(""));
        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(""));
        footerRow.getCell(statusColumn).setComponent(new Span(""));

        Button loadButton = new Button("Load Quarterly Performance", new Icon(VaadinIcon.REFRESH));
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Span hint = new Span("Loads or refreshes the grid and totals.");
        hint.getStyle().set("color", "var(--lumo-secondary-text-color)");

        HorizontalLayout topBar = new HorizontalLayout(loadButton, hint);
        topBar.setWidthFull();
        topBar.setPadding(false);
        topBar.setSpacing(true);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);

        loadButton.addClickListener(e -> {
            List<BudgetItemsActuals> items = budgetItemsService.findDistinctBudgetItemsesExp(budget, deptSections);
            gridBudgetItemsQuarterlyGrid.setItems(items);

            refreshQuarterlyFooter(
                    footerRow,
                    items,
                    qtr1Column, qtr1AColumn,
                    qtr2Column, qtr2AColumn,
                    qtr3Column, qtr3AColumn,
                    qtr4Column, qtr4AColumn,
                    totalQtrColumn, totalAQtrColumn,
                    balanceColumn, statusColumn
            );

            loadButton.setText("Refresh Quarterly Performance");
        });

        wrapper.add(topBar, gridBudgetItemsQuarterlyGrid);
        return wrapper;
    }

    private Component budgetCoaQtrGeneral(Budget bdgt, Set<UrcDeptSectionAnlDimbgt> deptSections) {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        HorizontalLayout overviewContent = new HorizontalLayout();

        Grid<BudgetItemsActuals> gridBudgetItemsQuarterlyGrid = new Grid<>(BudgetItemsActuals.class, false);
        gridBudgetItemsQuarterlyGrid.setWidthFull();
        gridBudgetItemsQuarterlyGrid.addThemeVariants(
                GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_ROW_STRIPES
        );

        Grid.Column<BudgetItemsActuals> codeColumn;
        Grid.Column<BudgetItemsActuals> descColumn;
        Grid.Column<BudgetItemsActuals> selectedQtrColumn;
        Grid.Column<BudgetItemsActuals> selectedQtrAColumn;

        Grid.Column<BudgetItemsActuals> totalQtrColumn;
        Grid.Column<BudgetItemsActuals> totalAQtrColumn;
        Grid.Column<BudgetItemsActuals> balanceColumn;
        Grid.Column<BudgetItemsActuals> statusColumn;

        codeColumn = gridBudgetItemsQuarterlyGrid.addColumn(item -> {
            COA coa = item.getCoacode();
            return coa != null ? coa.getCode() : "";
        }).setHeader("Code").setWidth("100px").setFlexGrow(0);

        descColumn = gridBudgetItemsQuarterlyGrid.addColumn(BudgetItemsActuals::getItem)
                .setHeader("Description")
                .setWidth("250px");
        int qtr = mapQuarter(selectedQuarter);

        selectedQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item
                -> createSpan(getQuarterBudget(item, qtr))
        ))
                .setHeader("Cum. Budget (" + getQuarterLabel(qtr) + ")")
                .setWidth("150px");

        selectedQtrAColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(getQuarterActual(item, qtr));
            span.getElement().getThemeList().add("badge");

            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.setOpenOnClick(false);

            contextMenu.addItem("Download Qtr " + qtr + " Expenses", event -> {
                Set<String> sections = extractSectionCodes(deptSections);
                utils = new utilityActuals(
                        budget, sampleCoaService,
                        sampleUrcDeptSectionAnlDimbgtService,
                        sampleSALFLDGService, sections
                );
                utils.createTransactionsDialog3(overviewContent, qtr);
            });

            contextMenu.addItem("Download Qtr " + qtr + " " + item.getCoacode().getCode() + " Expenses", event -> {
                Set<String> sections = extractSectionCodes(deptSections);
                utils = new utilityActuals(
                        budget, sampleCoaService,
                        sampleUrcDeptSectionAnlDimbgtService,
                        sampleSALFLDGService, sections
                );
                utils.createTransactionsDialog4(overviewContent, qtr, item.getCoacode().getCode());
            });
            return span;
        }))
                .setHeader("Cum. Actual (" + getQuarterLabel(qtr) + ")")
                .setWidth("150px");

        totalQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> createSpan(item.getTotal())))
                .setHeader("Total Budget").setWidth("150px");

        totalAQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(item.getTotalA());
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Total Actual").setWidth("150px");

        balanceColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            BigDecimal balance = nz(item.getTotal()).subtract(nz(getQuarterActual(item, qtr)));
            Span span = createSpan(balance);
            span.getStyle().set("font-weight", "600");
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                span.getStyle().set("color", "var(--lumo-error-text-color)");
            } else {
                span.getStyle().set("color", "var(--lumo-success-text-color)");
            }
            return span;
        })).setHeader("Variance").setWidth("150px");

        statusColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("130px")
                .setFlexGrow(0);

        codeColumn.setFrozen(true);
        descColumn.setFrozen(true);

        FooterRow footerRow = gridBudgetItemsQuarterlyGrid.appendFooterRow();

        footerRow.getCell(codeColumn).setComponent(footerText(""));
        footerRow.getCell(descColumn).setComponent(footerText("TOTAL"));
        footerRow.getCell(selectedQtrColumn).setComponent(footerText(""));
        footerRow.getCell(selectedQtrAColumn).setComponent(footerActualText(""));

        footerRow.getCell(totalQtrColumn).setComponent(footerText(""));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(""));
        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(""));
        footerRow.getCell(statusColumn).setComponent(new Span(""));

        Button loadButton = new Button("Load Quarterly Performance", new Icon(VaadinIcon.REFRESH));
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Span info = new Span("Click to load or refresh quarterly figures.");
        info.getStyle().set("color", "var(--lumo-secondary-text-color)");

        HorizontalLayout topBar = new HorizontalLayout(loadButton, info);
        topBar.setWidthFull();
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.setPadding(false);
        topBar.setSpacing(true);

        loadButton.addClickListener(e -> {
            java.util.List<BudgetItemsActuals> items = budgetItemsService.findDistinctBudgetItemsesExp(bdgt, deptSections);
            gridBudgetItemsQuarterlyGrid.setItems(items);
            refreshFooter(
                    footerRow,
                    items,
                    selectedQtrColumn, selectedQtrAColumn,
                    totalQtrColumn, totalAQtrColumn,
                    balanceColumn, statusColumn, qtr
            );
            Notification.show("Quarterly performance loaded");
        });

        wrapper.add(topBar, gridBudgetItemsQuarterlyGrid, overviewContent);
        return wrapper;
    }

    private Set<String> extractSectionCodes(Set<UrcDeptSectionAnlDimbgt> sections) {
        if (sections == null) {
            return Collections.emptySet();
        }

        return sections.stream()
                .map(UrcDeptSectionAnlDimbgt::getANL_CODE)
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    private BigDecimal getQuarterBudget(BudgetItemsActuals item, int qtr) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        return switch (qtr) {
            case 1 ->
                nz(item.getQtr1());
            case 2 ->
                nz(item.getQtr1()).add(nz(item.getQtr2()));
            case 3 ->
                nz(item.getQtr1()).add(nz(item.getQtr2())).add(nz(item.getQtr3()));
            case 4 ->
                nz(item.getQtr1()).add(nz(item.getQtr2()))
                .add(nz(item.getQtr3()))
                .add(nz(item.getQtr4()));
            default ->
                BigDecimal.ZERO;
        };
    }

    private BigDecimal getQuarterActual(BudgetItemsActuals item, int qtr) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal value = switch (qtr) {
            case 1 ->
                nz(item.getQtr1A());
            case 2 ->
                nz(item.getQtr1A()).add(nz(item.getQtr2A()));
            case 3 ->
                nz(item.getQtr1A()).add(nz(item.getQtr2A())).add(nz(item.getQtr3A()));
            case 4 ->
                nz(item.getQtr1A()).add(nz(item.getQtr2A()))
                .add(nz(item.getQtr3A()))
                .add(nz(item.getQtr4A()));
            default ->
                BigDecimal.ZERO;
        };

        return value;
    }

    private BigDecimal invert(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.negate();
    }

    private String getQuarterLabel(int qtr) {
        return switch (qtr) {
            case 1 ->
                "Q1";
            case 2 ->
                "Q2";
            case 3 ->
                "Q3";
            case 4 ->
                "Q4";
            default ->
                "";
        };
    }

    private int mapQuarter(String qtr) {
        if (qtr == null) {
            return 0;
        }

        return switch (qtr.toLowerCase()) {
            case "qtr1" ->
                1;
            case "qtr2" ->
                2;
            case "qtr3" ->
                3;
            case "qtr4" ->
                4;
            default ->
                0;
        };
    }

    private void refreshFooter(
            FooterRow footerRow,
            java.util.List<BudgetItemsActuals> items,
            Grid.Column<BudgetItemsActuals> selectedQtrColumn,
            Grid.Column<BudgetItemsActuals> selectedQtrAColumn,
            Grid.Column<BudgetItemsActuals> totalQtrColumn,
            Grid.Column<BudgetItemsActuals> totalAQtrColumn,
            Grid.Column<BudgetItemsActuals> balanceColumn,
            Grid.Column<BudgetItemsActuals> statusColumn,
            int qtr
    ) {
        BigDecimal qtr1Total = sum(items, BudgetItemsActuals::getQtr1);
        BigDecimal qtr1ATotal = sum(items, BudgetItemsActuals::getQtr1A);
        BigDecimal qtr2Total = sum(items, BudgetItemsActuals::getQtr2);
        BigDecimal qtr2ATotal = sum(items, BudgetItemsActuals::getQtr2A);
        BigDecimal qtr3Total = sum(items, BudgetItemsActuals::getQtr3);
        BigDecimal qtr3ATotal = sum(items, BudgetItemsActuals::getQtr3A);
        BigDecimal qtr4Total = sum(items, BudgetItemsActuals::getQtr4);
        BigDecimal qtr4ATotal = sum(items, BudgetItemsActuals::getQtr4A);
        BigDecimal grandBudget = sum(items, BudgetItemsActuals::getTotal);
        BigDecimal grandActual = sum(items, BudgetItemsActuals::getTotalA);
        BigDecimal grandBalance = grandBudget.subtract(grandActual);

        BigDecimal selectdQtrTotal = BigDecimal.ZERO;
        BigDecimal selectdQtrATotal = BigDecimal.ZERO;

        switch (qtr) {
            case 1:
                selectdQtrTotal = qtr1Total;
                selectdQtrATotal = qtr1ATotal;
                break;
            case 2:
                selectdQtrTotal = qtr1Total.add(qtr2Total);
                selectdQtrATotal = qtr1ATotal.add(qtr2ATotal);
                break;
            case 3:
                selectdQtrTotal = qtr1Total.add(qtr2Total).add(qtr3Total);
                selectdQtrATotal = qtr1ATotal.add(qtr2ATotal).add(qtr3ATotal);
                break;
            case 4:
                selectdQtrTotal = qtr1Total.add(qtr2Total).add(qtr3Total).add(qtr4Total);
                selectdQtrATotal = qtr1ATotal.add(qtr2ATotal).add(qtr3ATotal).add(qtr4ATotal);
                break;
            default:
                break;
        }

        footerRow.getCell(selectedQtrColumn).setComponent(footerText(formatAmount(selectdQtrTotal)));
        footerRow.getCell(selectedQtrAColumn).setComponent(footerActualText(formatAmount(selectdQtrATotal)));

        footerRow.getCell(totalQtrColumn).setComponent(footerText(formatAmount(grandBudget)));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(formatAmount(grandActual)));

        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(formatAmount(grandBalance)));
        footerRow.getCell(statusColumn).setComponent(createFooterStatusBadge(grandBudget, selectdQtrATotal, grandBalance));
    }

    private void refreshQuarterlyFooter(
            FooterRow footerRow,
            List<BudgetItemsActuals> items,
            Grid.Column<BudgetItemsActuals> qtr1Column,
            Grid.Column<BudgetItemsActuals> qtr1AColumn,
            Grid.Column<BudgetItemsActuals> qtr2Column,
            Grid.Column<BudgetItemsActuals> qtr2AColumn,
            Grid.Column<BudgetItemsActuals> qtr3Column,
            Grid.Column<BudgetItemsActuals> qtr3AColumn,
            Grid.Column<BudgetItemsActuals> qtr4Column,
            Grid.Column<BudgetItemsActuals> qtr4AColumn,
            Grid.Column<BudgetItemsActuals> totalQtrColumn,
            Grid.Column<BudgetItemsActuals> totalAQtrColumn,
            Grid.Column<BudgetItemsActuals> balanceColumn,
            Grid.Column<BudgetItemsActuals> statusColumn
    ) {
        BigDecimal qtr1Total = sum(items, BudgetItemsActuals::getQtr1);
        BigDecimal qtr1ATotal = sum(items, BudgetItemsActuals::getQtr1A);
        BigDecimal qtr2Total = sum(items, BudgetItemsActuals::getQtr2);
        BigDecimal qtr2ATotal = sum(items, BudgetItemsActuals::getQtr2A);
        BigDecimal qtr3Total = sum(items, BudgetItemsActuals::getQtr3);
        BigDecimal qtr3ATotal = sum(items, BudgetItemsActuals::getQtr3A);
        BigDecimal qtr4Total = sum(items, BudgetItemsActuals::getQtr4);
        BigDecimal qtr4ATotal = sum(items, BudgetItemsActuals::getQtr4A);
        BigDecimal grandBudget = sum(items, BudgetItemsActuals::getTotal);
        BigDecimal grandActual = sum(items, BudgetItemsActuals::getTotalA);
        BigDecimal grandBalance = grandBudget.subtract(grandActual);

        footerRow.getCell(qtr1Column).setComponent(footerText(formatAmount(qtr1Total)));
        footerRow.getCell(qtr1AColumn).setComponent(footerActualText(formatAmount(qtr1ATotal)));

        footerRow.getCell(qtr2Column).setComponent(footerText(formatAmount(qtr2Total)));
        footerRow.getCell(qtr2AColumn).setComponent(footerActualText(formatAmount(qtr2ATotal)));

        footerRow.getCell(qtr3Column).setComponent(footerText(formatAmount(qtr3Total)));
        footerRow.getCell(qtr3AColumn).setComponent(footerActualText(formatAmount(qtr3ATotal)));

        footerRow.getCell(qtr4Column).setComponent(footerText(formatAmount(qtr4Total)));
        footerRow.getCell(qtr4AColumn).setComponent(footerActualText(formatAmount(qtr4ATotal)));

        footerRow.getCell(totalQtrColumn).setComponent(footerText(formatAmount(grandBudget)));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(formatAmount(grandActual)));
        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(formatAmount(grandBalance)));
        footerRow.getCell(statusColumn).setComponent(createFooterStatusBadge(grandBudget, grandActual, grandBalance));
    }

    private Component footerBalanceText(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "700")
                .set("display", "block")
                .set("text-align", "right")
                .set("width", "100%");
        return span;
    }

    private Component createStatusBadge(BudgetItemsActuals item) {
        BigDecimal total = nz(item.getTotal());
        BigDecimal actual = nz(item.getTotalA());
        BigDecimal balance = total.subtract(actual);

        Span badge = new Span();
        badge.getStyle()
                .set("font-weight", "700")
                .set("padding", "4px 10px")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("display", "inline-block");

        if (total.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            badge.setText("UNBUDGETED");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            badge.setText("OVER");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else {
            badge.setText("FINE");
            badge.getStyle()
                    .set("background-color", "var(--lumo-success-color-10pct)")
                    .set("color", "var(--lumo-success-text-color)");
        }

        return badge;
    }

    private Component createFooterStatusBadge(BigDecimal total, BigDecimal actual, BigDecimal balance) {
        Span badge = new Span();
        badge.getStyle()
                .set("font-weight", "700")
                .set("padding", "4px 10px")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("display", "inline-block");

        if (total.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            badge.setText("UNBUDGETED");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            badge.setText("OVER");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else {
            badge.setText("FINE");
            badge.getStyle()
                    .set("background-color", "var(--lumo-success-color-10pct)")
                    .set("color", "var(--lumo-success-text-color)");
        }

        return badge;
    }

    private Component footerText(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "700")
                .set("display", "block")
                .set("text-align", "left")
                .set("width", "100%");
        return span;
    }

    private Component footerActualText(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "700")
                .set("color", "#1565c0")
                .set("display", "block")
                .set("text-align", "left")
                .set("width", "100%");
        return span;
    }

    private Component footerLabel(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "700");
        return span;
    }

    private BigDecimal sum(List<BudgetItemsActuals> items,
            java.util.function.Function<BudgetItemsActuals, BigDecimal> extractor) {
        return items.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    private Span createSpan(BigDecimal value) {
        Span span;
        if (value != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            span = new Span(decimalFormat.format(value));
        } else {
            span = new Span("-");
        }
        return span;
    }

    private BigDecimal getSelectedDepartmentSpent(DepartmentBudget department) {
        if (department == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal value;

        if (selectedQuarter == null || selectedQuarter.isBlank()) {
            value = nz(department.getTotalSpent());
        } else {
            value = switch (selectedQuarter.toLowerCase()) {
                case "qtr1" ->
                    nz(department.getCumQtr1Actual());
                case "qtr2" ->
                    nz(department.getCumQtr2Actual());
                case "qtr3" ->
                    nz(department.getCumQtr3Actual());
                case "qtr4" ->
                    nz(department.getCumQtr4Actual());
                default ->
                    nz(department.getTotalSpent());
            };
        }

        return invert(value);
    }

    private BigDecimal getSelectedDepartmentAvailable(DepartmentBudget department) {
        if (department == null) {
            return BigDecimal.ZERO;
        }
        return nz(department.getTotalBudget()).subtract(getSelectedDepartmentSpent(department));
    }

    private BigDecimal getSelectedDepartmentSpentPercentage(DepartmentBudget department) {
        BigDecimal totalBudget = nz(department != null ? department.getTotalBudget() : BigDecimal.ZERO);
        if (totalBudget.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return getSelectedDepartmentSpent(department)
                .divide(totalBudget, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String getSelectedDepartmentStatusText(DepartmentBudget department) {
        BigDecimal pct = getSelectedDepartmentSpentPercentage(department);

        if (pct.compareTo(BigDecimal.valueOf(100)) > 0) {
            return "Over Budget";
        } else if (pct.compareTo(BigDecimal.valueOf(90)) > 0) {
            return "Critical";
        } else if (pct.compareTo(BigDecimal.valueOf(75)) > 0) {
            return "Near Limit";
        }
        return "On Track";
    }

    private String getSelectedDepartmentStatusClass(DepartmentBudget department) {
        return switch (getSelectedDepartmentStatusText(department)) {
            case "Over Budget" ->
                "status-over";
            case "Critical" ->
                "status-critical";
            case "Near Limit" ->
                "status-near";
            default ->
                "status-good";
        };
    }

    private String getQuarterLabel() {
        return switch (selectedQuarter != null ? selectedQuarter.toLowerCase() : "") {
            case "qtr1" ->
                "Quarter 1";
            case "qtr2" ->
                "Quarter 2";
            case "qtr3" ->
                "Quarter 3";
            case "qtr4" ->
                "Quarter 4";
            default ->
                "Full Year";
        };
    }
}
