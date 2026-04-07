
package com.methaltech.application.views.budgetReport;

import com.methaltech.application.data.bgtool.repository.URCKPIPerformanceRecordRepository;
import com.methaltech.application.data.bgtool.repository.URCProgrammeAnnualBudgetRepository;
import com.methaltech.application.data.bgtool.repository.URCProgrammeKPIRepository;
import com.methaltech.application.data.entity.bgtool.MonitoringStatus;
import static com.methaltech.application.data.entity.bgtool.MonitoringStatus.AT_RISK;
import static com.methaltech.application.data.entity.bgtool.MonitoringStatus.COMPLETED;
import static com.methaltech.application.data.entity.bgtool.MonitoringStatus.OFF_TRACK;
import static com.methaltech.application.data.entity.bgtool.MonitoringStatus.ON_TRACK;
import com.methaltech.application.data.entity.bgtool.ReportingPeriodType;
import com.methaltech.application.data.entity.bgtool.URC_KPI_Performance_Record;
import com.methaltech.application.data.entity.bgtool.URC_Programme_Annual_Budget;
import com.methaltech.application.data.entity.bgtool.URC_Programme_KPI;
import com.methaltech.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data-entry view for quarterly/annual physical performance.
 */
@Route(value = "urc/physical-performance-entry", layout = MainLayout.class)
@PageTitle("Physical Performance Entry")
@PermitAll
public class URCPhysicalPerformanceEntryView extends VerticalLayout {

    private final URCProgrammeAnnualBudgetRepository annualBudgetRepository;
    private final URCProgrammeKPIRepository kpiRepository;
    private final URCKPIPerformanceRecordRepository performanceRepository;

    private final ComboBox<URC_Programme_Annual_Budget> budgetCombo = new ComboBox<>("Budget / Programme");
    private final ComboBox<URC_Programme_KPI> kpiCombo = new ComboBox<>("KPI");
    private final ComboBox<ReportingPeriodType> periodCombo = new ComboBox<>("Reporting Period");
    private final ComboBox<MonitoringStatus> statusCombo = new ComboBox<>("Status");

    private final BigDecimalField targetField = new BigDecimalField("Target");
    private final BigDecimalField actualField = new BigDecimalField("Actual");
    private final TextField varianceField = new TextField("Variance");
    private final TextField performanceField = new TextField("Performance %");

    private final TextArea physicalPerformanceField = new TextArea("Physical Performance");
    private final TextArea achievementsField = new TextArea("Key Achievements");
    private final TextArea challengesField = new TextArea("Challenges");
    private final TextArea correctiveActionsField = new TextArea("Corrective Actions");
    private final TextArea verificationField = new TextArea("Means of Verification");

    private final Grid<URC_KPI_Performance_Record> recordsGrid = new Grid<>(URC_KPI_Performance_Record.class, false);

    private URC_KPI_Performance_Record currentRecord;

    public URCPhysicalPerformanceEntryView(
            URCProgrammeAnnualBudgetRepository annualBudgetRepository,
            URCProgrammeKPIRepository kpiRepository,
            URCKPIPerformanceRecordRepository performanceRepository
    ) {
        this.annualBudgetRepository = annualBudgetRepository;
        this.kpiRepository = kpiRepository;
        this.performanceRepository = performanceRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(buildHeader(), buildFilters(), buildEditor(), buildGrid());
        configureComponents();
        loadBudgets();
    }

    private Component buildHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.add(new H2("URC Physical Performance Entry"),
                new Span("Capture quarterly or annual physical performance against an annual programme budget."));
        return header;
    }

    private Component buildFilters() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.END);

        budgetCombo.setWidth("420px");
        kpiCombo.setWidth("420px");
        periodCombo.setItems(ReportingPeriodType.values());
        periodCombo.setWidth("220px");
        periodCombo.setValue(ReportingPeriodType.Q1);

        Button loadButton = new Button("Load", VaadinIcon.SEARCH.create(), e -> loadOrCreateRecord());
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        row.add(budgetCombo, kpiCombo, periodCombo, loadButton);
        return row;
    }

    private Component buildEditor() {
        FormLayout form = new FormLayout();
        form.setWidthFull();

        varianceField.setReadOnly(true);
        performanceField.setReadOnly(true);


        statusCombo.setItems(MonitoringStatus.values());

        physicalPerformanceField.setMinHeight("120px");
        achievementsField.setMinHeight("100px");
        challengesField.setMinHeight("100px");
        correctiveActionsField.setMinHeight("100px");
        verificationField.setMinHeight("100px");

        targetField.addValueChangeListener(e -> recalculatePreview());
        actualField.addValueChangeListener(e -> recalculatePreview());

        form.add(budgetCombo, 2);
        form.add(kpiCombo, 2);
        form.add(periodCombo, statusCombo);
        form.add(targetField, actualField, varianceField, performanceField);
        form.add(physicalPerformanceField, 4);
        form.add(achievementsField, 2);
        form.add(challengesField, 2);
        form.add(correctiveActionsField, 2);
        form.add(verificationField, 2);

        Button save = new Button("Save Record", VaadinIcon.CHECK.create(), e -> saveRecord());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button clear = new Button("Clear", VaadinIcon.ERASER.create(), e -> clearEditor());

        HorizontalLayout actions = new HorizontalLayout(save, clear);
        actions.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout wrapper = new VerticalLayout(new H3("Performance Record"), form, actions);
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        return new Scroller(wrapper);
    }

    private Component buildGrid() {
        recordsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        recordsGrid.setHeight("360px");
        recordsGrid.addColumn(r -> Optional.ofNullable(r.getProgrammeAnnualBudget())
                        .map(URC_Programme_Annual_Budget::getBudget)
                        .map(Object::toString)
                        .orElse("-"))
                .setHeader("Budget")
                .setAutoWidth(true)
                .setFlexGrow(0);
        recordsGrid.addColumn(r -> Optional.ofNullable(r.getKpi()).map(URC_Programme_KPI::getKpiName).orElse("-"))
                .setHeader("KPI")
                .setAutoWidth(true);
        recordsGrid.addColumn(URC_KPI_Performance_Record::getReportingPeriod)
                .setHeader("Period")
                .setAutoWidth(true)
                .setFlexGrow(0);
        recordsGrid.addColumn(URC_KPI_Performance_Record::getTargetValue)
                .setHeader("Target")
                .setAutoWidth(true)
                .setFlexGrow(0);
        recordsGrid.addColumn(URC_KPI_Performance_Record::getActualValue)
                .setHeader("Actual")
                .setAutoWidth(true)
                .setFlexGrow(0);
        recordsGrid.addColumn(URC_KPI_Performance_Record::getVarianceValue)
                .setHeader("Variance")
                .setAutoWidth(true)
                .setFlexGrow(0);
        recordsGrid.addColumn(URC_KPI_Performance_Record::getPerformancePercent)
                .setHeader("Performance %")
                .setAutoWidth(true)
                .setFlexGrow(0);
        recordsGrid.addColumn(new ComponentRenderer<>(this::buildStatusBadge))
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);
        recordsGrid.addColumn(URC_KPI_Performance_Record::getPhysicalPerformance)
                .setHeader("Physical Performance")
                .setFlexGrow(1);

        recordsGrid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                editRecord(e.getValue());
            }
        });

        VerticalLayout wrapper = new VerticalLayout(new H3("Saved Records"), recordsGrid);
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.setSizeFull();
        return wrapper;
    }

    private Component buildStatusBadge(URC_KPI_Performance_Record record) {
        Span badge = new Span(record.getStatus() != null ? record.getStatus().name() : "NOT_STARTED");
        badge.getStyle()
                .set("padding", "4px 10px")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("background", switch (record.getStatus()) {
                    case COMPLETED -> "var(--lumo-success-color-10pct)";
                    case ON_TRACK -> "var(--lumo-primary-color-10pct)";
                    case AT_RISK -> "var(--lumo-contrast-10pct)";
                    case OFF_TRACK -> "var(--lumo-error-color-10pct)";
                    default -> "var(--lumo-contrast-5pct)";
                });
        return badge;
    }

    private void configureComponents() {
        budgetCombo.setItemLabelGenerator(b -> {
            String budgetLabel = b.getBudget() != null ? b.getBudget().toString() : "Budget";
            String programmeLabel = b.getProgramme() != null ? b.getProgramme().getName() : "Programme";
            return budgetLabel + " - " + programmeLabel;
        });

        kpiCombo.setItemLabelGenerator(URC_Programme_KPI::getKpiName);

        budgetCombo.addValueChangeListener(e -> loadKpisForBudget(e.getValue()));
    }

    private void loadBudgets() {
        List<URC_Programme_Annual_Budget> items = annualBudgetRepository.findAll();
        items.sort(Comparator.comparing(a -> Optional.ofNullable(a.getProgramme()).map(p -> p.getName()).orElse("")));
        budgetCombo.setItems(items);
    }

    private void loadKpisForBudget(URC_Programme_Annual_Budget selectedBudget) {
        kpiCombo.clear();
        if (selectedBudget == null || selectedBudget.getProgramme() == null) {
            kpiCombo.setItems(List.of());
            return;
        }
        List<URC_Programme_KPI> kpis = kpiRepository.findByProgrammeId(selectedBudget.getProgramme().getId());
        kpiCombo.setItems(kpis);
        refreshGrid();
    }

    private void loadOrCreateRecord() {
        if (budgetCombo.getValue() == null || kpiCombo.getValue() == null || periodCombo.getValue() == null) {
            Notification.show("Select budget, KPI and reporting period.");
            return;
        }

        URC_Programme_Annual_Budget budget = budgetCombo.getValue();
        URC_Programme_KPI kpi = kpiCombo.getValue();
        ReportingPeriodType period = periodCombo.getValue();

        Optional<URC_KPI_Performance_Record> existing = performanceRepository.findAll().stream()
                .filter(r -> Objects.equals(r.getProgrammeAnnualBudget().getId(), budget.getId()))
                .filter(r -> Objects.equals(r.getKpi().getId(), kpi.getId()))
                .filter(r -> r.getReportingPeriod() == period)
                .findFirst();

        currentRecord = existing.orElseGet(() -> {
            URC_KPI_Performance_Record record = new URC_KPI_Performance_Record();
            record.setProgrammeAnnualBudget(budget);
            record.setKpi(kpi);
            record.setReportingPeriod(period);
            record.setReportingDate(LocalDate.now());
            record.setStatus(MonitoringStatus.NOT_STARTED);
            record.setTargetValue(BigDecimal.ZERO);
            return record;
        });

        bindToEditor(currentRecord);
    }

    private void bindToEditor(URC_KPI_Performance_Record record) {
        budgetCombo.setValue(record.getProgrammeAnnualBudget());
        if (record.getProgrammeAnnualBudget() != null && record.getProgrammeAnnualBudget().getProgramme() != null) {
            kpiCombo.setItems(kpiRepository.findByProgrammeId(record.getProgrammeAnnualBudget().getProgramme().getId()));
        }
        kpiCombo.setValue(record.getKpi());
        periodCombo.setValue(record.getReportingPeriod());
        targetField.setValue(record.getTargetValue());
        actualField.setValue(record.getActualValue());
        statusCombo.setValue(record.getStatus());
        physicalPerformanceField.setValue(nullSafe(record.getPhysicalPerformance()));
        achievementsField.setValue(nullSafe(record.getKeyAchievements()));
        challengesField.setValue(nullSafe(record.getChallenges()));
        correctiveActionsField.setValue(nullSafe(record.getCorrectiveActions()));
        verificationField.setValue(nullSafe(record.getMeansOfVerification()));
        recalculatePreview();
    }

    private void editRecord(URC_KPI_Performance_Record record) {
        this.currentRecord = record;
        bindToEditor(record);
    }

    private void recalculatePreview() {
        BigDecimal target = targetField.getValue() != null ? targetField.getValue() : BigDecimal.ZERO;
        BigDecimal actual = actualField.getValue() != null ? actualField.getValue() : BigDecimal.ZERO;
        BigDecimal variance = actual.subtract(target);
        varianceField.setValue(variance.setScale(2, RoundingMode.HALF_UP).toPlainString());

        if (target.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal perf = actual.divide(target, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            performanceField.setValue(perf.toPlainString());
        } else {
            performanceField.setValue("0.00");
        }
    }

    private void saveRecord() {
        if (currentRecord == null) {
            loadOrCreateRecord();
            if (currentRecord == null) {
                return;
            }
        }

        currentRecord.setProgrammeAnnualBudget(budgetCombo.getValue());
        currentRecord.setKpi(kpiCombo.getValue());
        currentRecord.setReportingPeriod(periodCombo.getValue());
        currentRecord.setTargetValue(targetField.getValue() != null ? targetField.getValue() : BigDecimal.ZERO);
        currentRecord.setActualValue(actualField.getValue());
        currentRecord.setStatus(statusCombo.getValue() != null ? statusCombo.getValue() : MonitoringStatus.NOT_STARTED);
        currentRecord.setPhysicalPerformance(trimToNull(physicalPerformanceField.getValue()));
        currentRecord.setKeyAchievements(trimToNull(achievementsField.getValue()));
        currentRecord.setChallenges(trimToNull(challengesField.getValue()));
        currentRecord.setCorrectiveActions(trimToNull(correctiveActionsField.getValue()));
        currentRecord.setMeansOfVerification(trimToNull(verificationField.getValue()));
        currentRecord.setReportingDate(LocalDate.now());

        performanceRepository.save(currentRecord);
        Notification.show("Performance record saved.");
        refreshGrid();
    }

    private void refreshGrid() {
        List<URC_KPI_Performance_Record> rows = new ArrayList<>();
        if (budgetCombo.getValue() != null) {
            rows = performanceRepository.findByProgrammeAnnualBudgetId(budgetCombo.getValue().getId());
        }
        recordsGrid.setItems(rows);
    }

    private void clearEditor() {
        currentRecord = null;
        targetField.clear();
        actualField.clear();
        varianceField.clear();
        performanceField.clear();
        statusCombo.clear();
        physicalPerformanceField.clear();
        achievementsField.clear();
        challengesField.clear();
        correctiveActionsField.clear();
        verificationField.clear();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

