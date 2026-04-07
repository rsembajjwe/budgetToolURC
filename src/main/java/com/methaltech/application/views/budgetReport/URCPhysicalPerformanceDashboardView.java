package com.methaltech.application.views.budgetReport;

import com.methaltech.application.data.bgtool.repository.URCProgrammeAnnualBudgetRepository;
import com.methaltech.application.data.bgtool.service.ConsolidatedPhysicalPerformanceService;
import com.methaltech.application.data.entity.bgtool.URC_Programme_Annual_Budget;
import com.methaltech.application.data.entity.bgtool.dto.ConsolidatedPhysicalPerformanceRow;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Route(value = "urc/physical-performance-dashboard", layout = MainLayout.class)
@PageTitle("Physical Performance Dashboard")
@PermitAll
public class URCPhysicalPerformanceDashboardView extends VerticalLayout {

    private final URCProgrammeAnnualBudgetRepository annualBudgetRepository;
    private final ConsolidatedPhysicalPerformanceService consolidatedService;

    private final ComboBox<URC_Programme_Annual_Budget> budgetCombo = new ComboBox<>("Budget / Programme");
    private final Grid<ConsolidatedPhysicalPerformanceRow> grid = new Grid<>(ConsolidatedPhysicalPerformanceRow.class, false);

    private final Span totalTarget = metricValue();
    private final Span totalActual = metricValue();
    private final Span avgPerformance = metricValue();
    private final Span offTrackCount = metricValue();

    public URCPhysicalPerformanceDashboardView(
            URCProgrammeAnnualBudgetRepository annualBudgetRepository,
            ConsolidatedPhysicalPerformanceService consolidatedService
    ) {
        this.annualBudgetRepository = annualBudgetRepository;
        this.consolidatedService = consolidatedService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(buildHeader(), buildToolbar(), buildMetricCards(), buildGrid());
        configure();
        loadBudgets();
    }

    private Component buildHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.add(
                new H2("URC Physical Performance Dashboard"),
                new Span("Budget-based consolidated physical performance for programmes, outputs, activities, departments, and sections.")
        );
        return header;
    }

    private Component buildToolbar() {
        budgetCombo.setWidth("460px");

        Button refresh = new Button("Refresh", VaadinIcon.REFRESH.create(), e -> loadDashboard());
        refresh.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout row = new HorizontalLayout(budgetCombo, refresh);
        row.setAlignItems(FlexComponent.Alignment.END);
        return row;
    }

    private Component buildMetricCards() {
        HorizontalLayout cards = new HorizontalLayout(
                metricCard("Total Target", totalTarget),
                metricCard("Total Actual", totalActual),
                metricCard("Average Performance %", avgPerformance),
                metricCard("Off Track / At Risk", offTrackCount)
        );
        cards.setWidthFull();
        cards.setFlexGrow(1);
        return cards;
    }

    private Component metricCard(String label, Span value) {
        VerticalLayout card = new VerticalLayout(new Span(label), value);
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "16px")
                .set("padding", "16px")
                .set("min-width", "220px");
        card.setSpacing(false);
        return card;
    }

    private Span metricValue() {
        Span span = new Span("0");
        span.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "700");
        return span;
    }

    private Component buildGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setSizeFull();

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getProgrammeName)
                .setHeader("Programme")
                .setAutoWidth(true);

        grid.addColumn(this::safeOutput)
                .setHeader("Output")
                .setAutoWidth(true);

        grid.addColumn(this::safeActivity)
                .setHeader("Activity")
                .setAutoWidth(true);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getDepartmentName)
                .setHeader("Department")
                .setAutoWidth(true);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getSectionName)
                .setHeader("Section")
                .setAutoWidth(true);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getKpiName)
                .setHeader("KPI")
                .setAutoWidth(true);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getReportingPeriod)
                .setHeader("Period")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getTargetValue)
                .setHeader("Target")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getActualValue)
                .setHeader("Actual")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getVarianceValue)
                .setHeader("Variance")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getPerformancePercent)
                .setHeader("Performance %")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(this::buildStatusBadge))
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getPhysicalPerformance)
                .setHeader("Physical Performance")
                .setFlexGrow(1);

        grid.addColumn(ConsolidatedPhysicalPerformanceRow::getChallenges)
                .setHeader("Challenges")
                .setFlexGrow(1);

        return grid;
    }

    private Component buildStatusBadge(ConsolidatedPhysicalPerformanceRow row) {
        String status = row.getStatus() == null ? "NOT_STARTED" : row.getStatus();

        Span badge = new Span(status);
        badge.getStyle()
                .set("padding", "4px 10px")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("background", switch (status.toUpperCase()) {
                    case "COMPLETED" -> "var(--lumo-success-color-10pct)";
                    case "ON_TRACK" -> "var(--lumo-primary-color-10pct)";
                    case "AT_RISK" -> "var(--lumo-contrast-10pct)";
                    case "OFF_TRACK" -> "var(--lumo-error-color-10pct)";
                    default -> "var(--lumo-contrast-5pct)";
                });

        return badge;
    }

    private void configure() {
        budgetCombo.setItemLabelGenerator(b -> {
            String budgetLabel = b.getBudget() != null ? b.getBudget().toString() : "Budget";
            String programmeLabel = b.getProgramme() != null ? b.getProgramme().getName() : "Programme";
            return budgetLabel + " - " + programmeLabel;
        });

        budgetCombo.addValueChangeListener(e -> loadDashboard());
    }

    private void loadBudgets() {
        List<URC_Programme_Annual_Budget> items = annualBudgetRepository.findAll();
        items.sort(Comparator.comparing(a ->
                Optional.ofNullable(a.getProgramme()).map(p -> p.getName()).orElse("")));
        budgetCombo.setItems(items);
    }

    private void loadDashboard() {
        if (budgetCombo.getValue() == null || budgetCombo.getValue().getBudget() == null) {
            grid.setItems(List.of());
            resetMetrics();
            return;
        }

        Long budgetId = budgetCombo.getValue().getBudget().getId();
        List<ConsolidatedPhysicalPerformanceRow> rows = consolidatedService.getReportByBudget(budgetId);

        grid.setItems(rows);
        updateMetrics(rows);
    }

    private void updateMetrics(List<ConsolidatedPhysicalPerformanceRow> rows) {
        BigDecimal target = rows.stream()
                .map(ConsolidatedPhysicalPerformanceRow::getTargetValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal actual = rows.stream()
                .map(ConsolidatedPhysicalPerformanceRow::getActualValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgPerf = rows.stream()
                .map(ConsolidatedPhysicalPerformanceRow::getPerformancePercent)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!rows.isEmpty()) {
            avgPerf = avgPerf.divide(BigDecimal.valueOf(rows.size()), 2, RoundingMode.HALF_UP);
        }

        long offTrack = rows.stream()
                .map(ConsolidatedPhysicalPerformanceRow::getStatus)
                .filter(Objects::nonNull)
                .filter(status -> "OFF_TRACK".equalsIgnoreCase(status) || "AT_RISK".equalsIgnoreCase(status))
                .count();

        totalTarget.setText(target.setScale(2, RoundingMode.HALF_UP).toPlainString());
        totalActual.setText(actual.setScale(2, RoundingMode.HALF_UP).toPlainString());
        avgPerformance.setText(avgPerf.setScale(2, RoundingMode.HALF_UP).toPlainString());
        offTrackCount.setText(String.valueOf(offTrack));
    }

    private void resetMetrics() {
        totalTarget.setText("0");
        totalActual.setText("0");
        avgPerformance.setText("0");
        offTrackCount.setText("0");
    }

    private String safeOutput(ConsolidatedPhysicalPerformanceRow row) {
        return row.getLogframeDescription() != null ? row.getLogframeDescription() : "-";
    }

    private String safeActivity(ConsolidatedPhysicalPerformanceRow row) {
        return row.getActivityName() != null ? row.getActivityName() : "-";
    }
}
