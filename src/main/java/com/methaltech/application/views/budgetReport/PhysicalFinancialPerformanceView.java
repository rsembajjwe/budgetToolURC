package com.methaltech.application.views.budgetReport;

import com.methaltech.application.data.GetPeriods;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.PriorityAreaService;
import com.methaltech.application.data.bgtool.service.QtrReleasesServiceImpl;
import com.methaltech.application.data.bgtool.service.QuarterlyActualsService;
import com.methaltech.application.data.bgtool.service.SectionBudgetPerformanceService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import com.methaltech.application.data.entity.bgtool.QuarterlyActuals;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.Tuple;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Physical & Financial")
@Route(value = "physical_financial", layout = MainLayout.class)
@Uses(Icon.class)
@RolesAllowed({"ADMIN", "BLO", "USER", "HOD"})
public class PhysicalFinancialPerformanceView extends VerticalLayout {

    private Budget chosenBudget = null;
    private User user = null;
    private UrcDeptSectionAnlDimbgt chosenDsection = null;

    private final BudgetService chosenBudgetService;
    private final BudgetItemsService budgetItemsService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    private final UserService userService;
    private final SALFLDGService SALFLDGService;
    private final UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService;
    private final PriorityAreaService priorityAreaService;
    private final URC_Priority_AreasService URC_Priority_AreasService;
    private final QuarterlyActualsService quarterlyActualsService;
    private final SectionBudgetPerformanceService sectionBudgetPerformanceService;
    private final QtrReleasesServiceImpl qtrReleasesServiceImpl;
    private final AuthenticatedUser authenticatedUser;

    private final ComboBox<Budget> budgetComboBox = new ComboBox<>("Select Budget");
    private final ComboBox<String> qtrComboBox = new ComboBox<>("Select Quarter");
    private final ComboBox<UrcDeptSectionAnlDimbgt> sectionComboBox = new ComboBox<>("Select Section");
    private final MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sectionMultiComboBox
            = new MultiSelectComboBox<>("Combine Section Reports");
    private final Button submitButton = new Button("Submit");
    private final Button downloadButton = new Button("Download");
    private final Button importPhyPerf = new Button("Import");

    private final Grid<PriorityArea> financialGrid = new Grid<>(PriorityArea.class, false);
    private final GridContextMenu<PriorityArea> contextMenuPriorityArea = new GridContextMenu<>(financialGrid);

    private final Grid<Urc_Activities> physicalGrid = new Grid<>(Urc_Activities.class, false);
    private final GridContextMenu<Urc_Activities> contextMenuPhysicalGrid = new GridContextMenu<>(physicalGrid);

    private final GetPeriods periods = new GetPeriods();
    private Set<Integer> period = new HashSet<>();

    private BigDecimal cumRealiseSpent = BigDecimal.ZERO;
    private BigDecimal totalBudget = BigDecimal.ZERO;
    private BigDecimal totalActualExpenditure = BigDecimal.ZERO;
    private BigDecimal cumReleasedFund = BigDecimal.ZERO;

    private QuarterlyActuals draggedItem = null;
    private final Binder<SectionBudgetPerformance> binderSectionBudgetPerformance
            = new Binder<>(SectionBudgetPerformance.class);

    private SectionBudgetPerformance budgetPer = null;
    private SectionBudgetPerformance perfo = null;
    private boolean submit = false;
    private int qtr = 0;
    private List<Urc_Activities> acts = new ArrayList<>();

    public PhysicalFinancialPerformanceView(
            List<Urc_Activities> activities,
            BudgetService chosenBudgetService,
            BudgetItemsService budgetItemsService,
            Urc_ActivitiesService sampleUrc_ActivitiesService,
            UserService userService,
            AuthenticatedUser authenticatedUser,
            UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService,
            URC_Priority_AreasService sampleURC_Priority_AreasService,
            SALFLDGService SALFLDGService,
            PriorityAreaService priorityAreaService,
            URC_Priority_AreasService URC_Priority_AreasService,
            QuarterlyActualsService quarterlyActualsService,
            SectionBudgetPerformanceService sectionBudgetPerformanceService,
            QtrReleasesServiceImpl qtrReleasesServiceImpl
    ) {
        this.chosenBudgetService = chosenBudgetService;
        this.budgetItemsService = budgetItemsService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.urcDeptSectionAnlDimbgtService = urcDeptSectionAnlDimbgtService;
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
        this.SALFLDGService = SALFLDGService;
        this.priorityAreaService = priorityAreaService;
        this.URC_Priority_AreasService = URC_Priority_AreasService;
        this.quarterlyActualsService = quarterlyActualsService;
        this.sectionBudgetPerformanceService = sectionBudgetPerformanceService;
        this.qtrReleasesServiceImpl = qtrReleasesServiceImpl;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        addClassName("physical-financial-view");

        configureUser();
        loadFiscalYearData();
        setSections();
        configureQuarter();
        configureButtonActions();
        configureSubmit();

        HorizontalLayout headMenu = new HorizontalLayout(
                budgetComboBox, sectionComboBox, qtrComboBox, importPhyPerf,
                submitButton, downloadButton, sectionMultiComboBox
        );
        headMenu.setAlignItems(Alignment.BASELINE);

        contextMenuPriorityArea.setOpenOnClick(true);
        contextMenuPhysicalGrid.setOpenOnClick(true);

        add(headMenu);
        add(new H3("FINANCIAL & PHYSICAL PERFORMANCE REPORT"));
        add(new H3("1. FINANCIAL PERFORMANCE"));

        configureFinancialGridByPriorityArea();
        add(financialGrid);

        add(new H3("2. PHYSICAL PERFORMANCE"));
        configurePhysicalGrid(activities);
        add(physicalGrid);
    }

    private void configureUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        authenticatedUser.get().ifPresent(value -> user = value);
    }

    private void loadFiscalYearData() {
        budgetComboBox.setItems(query -> chosenBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))
        ).stream());
        budgetComboBox.setItemLabelGenerator(Budget::getFinancialYear);
        budgetComboBox.setClearButtonVisible(true);

        chosenBudgetService.getMostRecurrentBudget().ifPresent(recentBudget -> {
            budgetComboBox.setValue(recentBudget);
            chosenBudget = recentBudget;
        });

        budgetComboBox.addValueChangeListener(e -> {
            chosenBudget = e.getValue();
            refreshKeepingSelections();
            configureSubmit();
            disableElements();
        });

        contextMenuPriorityArea.addItem("Edit Quarter Financial Data", e -> openFinancialDialog());
    }

    private void setSections() {
        sectionComboBox.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        sectionMultiComboBox.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        sectionComboBox.setClearButtonVisible(true);
        sectionMultiComboBox.setClearButtonVisible(true);

        if (user.getRoles().contains(Role.ADMIN)) {
            List<UrcDeptSectionAnlDimbgt> allSections = urcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims2();
            List<UrcDeptSectionAnlDimbgt> filteredSections = allSections.stream()
                    .filter(section -> section.getANL_CODE() != null && !section.getANL_CODE().contains("#"))
                    .collect(Collectors.toList());
            sectionComboBox.setItems(filteredSections);
        } else {
            sectionComboBox.setItems(user.getDeptsection());
            sectionMultiComboBox.setItems(user.getDeptsection());
        }

        sectionComboBox.addValueChangeListener(e -> {
            chosenDsection = e.getValue();
            refreshKeepingSelections();
            configureSubmit();
            disableElements();
        });
    }

    private void configureQuarter() {
        qtrComboBox.setItems("Qtr 1", "Qtr 2", "Qtr 3", "Qtr 4");
        qtrComboBox.setClearButtonVisible(true);

        qtrComboBox.addValueChangeListener(e -> {
            applyQuarterFromCombo(e.getValue());
            refreshKeepingSelections();
            configureSubmit();
            disableElements();
        });

        importPhyPerf.addSingleClickListener(e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Confirm import");
            dialog.setText("This will copy the previous quarter's physical performance into Quarter "
                    + qtr + " for " + acts.size() + " activities. Continue?");
            dialog.setCancelable(true);
            dialog.setCancelText("No");
            dialog.setConfirmText("Yes, import");
            dialog.addConfirmListener(ev -> importPreviousQuarterPhysicalPerformance());
            dialog.open();
        });
    }

    private void configureButtonActions() {
        submitButton.addClickListener(e -> handleSubmit());
        downloadButton.addClickListener(e -> handleDownload());
    }

    private void handleSubmit() {
        if (perfo == null || chosenBudget == null || chosenDsection == null || qtr == 0) {
            showNotification("Missing budget, section or quarter.", NotificationVariant.LUMO_WARNING);
            return;
        }

        switch (qtr) {
            case 1 ->
                perfo.setSubmitQtr1(true);
            case 2 ->
                perfo.setSubmitQtr2(true);
            case 3 ->
                perfo.setSubmitQtr3(true);
            case 4 ->
                perfo.setSubmitQtr4(true);
            default -> {
                return;
            }
        }

        if (totalActualExpenditure.abs().doubleValue() >= cumRealiseSpent.abs().doubleValue()) {
            sectionBudgetPerformanceService.save(perfo);
            refreshKeepingSelections();
            configureSubmit();
            showNotification("Successfully Submitted", NotificationVariant.LUMO_SUCCESS);
        } else {
            showNotification("Check your Expenditure", NotificationVariant.LUMO_WARNING);
        }
    }

    private void handleDownload() {
        if (chosenDsection == null || chosenBudget == null || qtrComboBox.getValue() == null) {
            showNotification("Select budget, section and quarter first.", NotificationVariant.LUMO_WARNING);
            return;
        }

        AnnualPerformancePdfGenerator pdfgen = new AnnualPerformancePdfGenerator(
                sampleURC_Priority_AreasService,
                sampleUrc_ActivitiesService,
                chosenDsection,
                SALFLDGService,
                budgetItemsService,
                sectionBudgetPerformanceService,
                qtrComboBox.getValue(),
                chosenBudget
        );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(dtf);
        String fileName = "Financial_Physical_Report_" + chosenDsection.getNAME().toUpperCase()
                + "_" + chosenBudget.getFinancialYear()
                + "_" + qtrComboBox.getValue()
                + "_" + timestamp + ".pdf";

        StreamResource pdfResource = new StreamResource(fileName, () -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                pdfgen.generateAnnualReportPdf(baos);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return new ByteArrayInputStream(baos.toByteArray());
        });

        Anchor download = new Anchor(pdfResource, "Download PDF");
        download.getElement().setAttribute("download", true);
        download.getElement().setAttribute("target", "_blank");
        add(download);
        download.getElement().callJsFunction("click");

        getUI().ifPresent(ui
                -> ui.getPage().executeJs("setTimeout(() => $0.remove(), 2000)", download.getElement())
        );
    }

    private void configureSubmit() {
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        if (chosenDsection == null || chosenBudget == null || qtr == 0) {
            submitButton.setEnabled(false);
            downloadButton.setEnabled(false);
            sectionMultiComboBox.setEnabled(false);
            return;
        }

        sectionMultiComboBox.setEnabled(true);

        Optional<SectionBudgetPerformance> budgetchosen
                = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);

        submit = false;
        perfo = null;

        if (budgetchosen.isPresent()) {
            perfo = budgetchosen.get();
            switch (qtr) {
                case 1 ->
                    submit = Boolean.TRUE.equals(perfo.getSubmitQtr1());
                case 2 ->
                    submit = Boolean.TRUE.equals(perfo.getSubmitQtr2());
                case 3 ->
                    submit = Boolean.TRUE.equals(perfo.getSubmitQtr3());
                case 4 ->
                    submit = Boolean.TRUE.equals(perfo.getSubmitQtr4());
                default ->
                    submit = false;
            }
        }

        if (submit) {
            downloadButton.setEnabled(true);
            submitButton.setText("Submitted");
            submitButton.setEnabled(false);
        } else {
            submitButton.setText("Submit");
            downloadButton.setEnabled(false);
            submitButton.setEnabled(isQuarterDataComplete(perfo, qtr) || areActivitiesCompleteForQuarter(acts, qtr));
        }

        List<UrcDeptSectionAnlDimbgt> submittedSections = switch (qtr) {
            case 1 ->
                sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr1(chosenBudget);
            case 2 ->
                sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr2(chosenBudget);
            case 3 ->
                sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr3(chosenBudget);
            case 4 ->
                sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr4(chosenBudget);
            default ->
                Collections.emptyList();
        };

        if (user.getRoles().contains(Role.ADMIN)) {
            sectionMultiComboBox.setItems(submittedSections);
        } else if (submittedSections != null && user.getDeptsection() != null) {
            sectionMultiComboBox.setItems(
                    retainCommonDeptSections(submittedSections, user.getDeptsection().stream().toList())
            );
        } else {
            sectionMultiComboBox.clear();
        }
    }

    private void disableElements() {
        boolean enabled = !(chosenDsection == null || chosenBudget == null || qtr == 0);
        if (!enabled) {
            submitButton.setEnabled(false);
            downloadButton.setEnabled(false);
            sectionMultiComboBox.setEnabled(false);
        }
    }

    private void applyQuarterFromCombo(String qt) {
        importPhyPerf.setEnabled(false);
        if (qt == null) {
            qtr = 0;
            return;
        }

        switch (qt) {
            case "Qtr 1" -> {
                qtr = 1;
                importPhyPerf.setEnabled(false);
            }
            case "Qtr 2" -> {
                qtr = 2;
                importPhyPerf.setEnabled(true);
            }
            case "Qtr 3" -> {
                qtr = 3;
                importPhyPerf.setEnabled(true);
            }
            case "Qtr 4" -> {
                qtr = 4;
                importPhyPerf.setEnabled(true);
            }
            default -> {
                qtr = 0;
                importPhyPerf.setEnabled(false);
            }
        }
    }

    private void refreshKeepingSelections() {
        Budget selectedBudget = budgetComboBox.getValue();
        UrcDeptSectionAnlDimbgt selectedSection = sectionComboBox.getValue();
        String selectedQuarter = qtrComboBox.getValue();

        chosenBudget = selectedBudget;
        chosenDsection = selectedSection;
        applyQuarterFromCombo(selectedQuarter);

        refreshFinancialGrid();

        if (financialGrid.getDataProvider() != null) {
            financialGrid.getDataProvider().refreshAll();
        }
        if (physicalGrid.getDataProvider() != null) {
            physicalGrid.getDataProvider().refreshAll();
        }

        if (!Objects.equals(budgetComboBox.getValue(), selectedBudget)) {
            budgetComboBox.setValue(selectedBudget);
        }
        if (!Objects.equals(sectionComboBox.getValue(), selectedSection)) {
            sectionComboBox.setValue(selectedSection);
        }
        if (!Objects.equals(qtrComboBox.getValue(), selectedQuarter)) {
            qtrComboBox.setValue(selectedQuarter);
        }
    }

    private void refreshFinancialGrid() {
        chosenDsection = sectionComboBox.getValue();

        if (chosenDsection == null || chosenBudget == null || qtr == 0) {
            financialGrid.setItems(Collections.emptyList());
            physicalGrid.setItems(Collections.emptyList());
            return;
        }

        period = getPeriodsForQuarter(chosenBudget, qtr);
        acts = sampleUrc_ActivitiesService.findByDeptSectionAndBudget(chosenDsection, chosenBudget);

        totalActualExpenditure = SALFLDGService.getTotalAmountByPeriods2(period, chosenDsection.getANL_CODE());
        cumRealiseSpent = totalActualExpenditure;
        totalBudget = budgetItemsService.calculateTotalDeptExpenditure2(chosenBudget, chosenDsection);
        cumReleasedFund = cumFundsReleased();

        List<PriorityArea> priorityAreas
                = URC_Priority_AreasService.getDistinctPriorityAreasByBudget(chosenBudget.getStartDate());

        financialGrid.setItems(priorityAreas);
        physicalGrid.setItems(acts);
    }

    private Set<Integer> getPeriodsForQuarter(Budget budget, int quarter) {
        Set<Integer> combined = new HashSet<>();
        for (int i = 1; i <= quarter; i++) {
            combined.addAll(periods.getFinancialYearPeriods(budget, i));
        }
        return combined;
    }

    private BigDecimal cumFundsReleased() {
        Optional<SectionBudgetPerformance> budgetchosen
                = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);

        if (budgetchosen.isEmpty()) {
            return BigDecimal.ZERO;
        }

        SectionBudgetPerformance perf = budgetchosen.get();
        return switch (qtr) {
            case 1 ->
                Optional.ofNullable(perf.getCumulativeFundsReleased1()).orElse(BigDecimal.ZERO);
            case 2 ->
                Optional.ofNullable(perf.getCumulativeFundsReleased2()).orElse(BigDecimal.ZERO);
            case 3 ->
                Optional.ofNullable(perf.getCumulativeFundsReleased3()).orElse(BigDecimal.ZERO);
            case 4 ->
                Optional.ofNullable(perf.getCumulativeFundsReleased4()).orElse(BigDecimal.ZERO);
            default ->
                BigDecimal.ZERO;
        };
    }

    public List<UrcDeptSectionAnlDimbgt> retainCommonDeptSections(
            List<UrcDeptSectionAnlDimbgt> list1,
            List<UrcDeptSectionAnlDimbgt> list2
    ) {
        if (list1 == null || list2 == null) {
            return Collections.emptyList();
        }
        return list2.stream().filter(list1::contains).distinct().collect(Collectors.toList());
    }

    public boolean isQuarterDataComplete(SectionBudgetPerformance perf, int quarter) {
        if (perf == null) {
            return false;
        }

        return switch (quarter) {
            case 1 ->
                perf.getCumulativeFundsReleased1() != null
                && perf.getCumulativeFundsSpent1() != null
                && !isBlank(perf.getPercentageSpent1())
                && !isBlank(perf.getReasonsForUnderOver1());
            case 2 ->
                perf.getCumulativeFundsReleased2() != null
                && perf.getCumulativeFundsSpent2() != null
                && !isBlank(perf.getPercentageSpent2())
                && !isBlank(perf.getReasonsForUnderOver2());
            case 3 ->
                perf.getCumulativeFundsReleased3() != null
                && perf.getCumulativeFundsSpent3() != null
                && !isBlank(perf.getPercentageSpent3())
                && !isBlank(perf.getReasonsForUnderOver3());
            case 4 ->
                perf.getCumulativeFundsReleased4() != null
                && perf.getCumulativeFundsSpent4() != null
                && !isBlank(perf.getPercentageSpent4())
                && !isBlank(perf.getReasonsForUnderOver4());
            default ->
                false;
        };
    }

    public boolean areActivitiesCompleteForQuarter(List<Urc_Activities> activities, int quarter) {
        if (activities == null || activities.isEmpty()) {
            return false;
        }

        for (Urc_Activities activity : activities) {
            if (activity == null) {
                return false;
            }
            if (isBlank(activity.getName()) || isBlank(activity.getPerformanceIndicator()) || isBlank(activity.getAnnualTarget())) {
                return false;
            }

            switch (quarter) {
                case 1 -> {
                    if (isBlank(activity.getCum_achievements_qtr1()) || isBlank(activity.getExpl_of_variations_qtr1())) {
                        return false;
                    }
                }
                case 2 -> {
                    if (isBlank(activity.getCum_achievements_qtr2()) || isBlank(activity.getExpl_of_variations_qtr2())) {
                        return false;
                    }
                }
                case 3 -> {
                    if (isBlank(activity.getCum_achievements_qtr3()) || isBlank(activity.getExpl_of_variations_qtr3())) {
                        return false;
                    }
                }
                case 4 -> {
                    if (isBlank(activity.getCum_achievements_qtr4()) || isBlank(activity.getExpl_of_variations_qtr4())) {
                        return false;
                    }
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void importPreviousQuarterPhysicalPerformance() {
        List<Urc_Activities> tempActs = new ArrayList<>();
        for (Urc_Activities activ : acts) {
            if (qtr == 2) {
                activ.setCum_achievements_qtr2(activ.getCum_achievements_qtr1());
                activ.setExpl_of_variations_qtr2(activ.getExpl_of_variations_qtr1());
                activ.setPerc_of_TargetAchieved_qtr21(activ.getPerc_of_TargetAchieved_qtr11());
            } else if (qtr == 3) {
                activ.setCum_achievements_qtr3(activ.getCum_achievements_qtr2());
                activ.setExpl_of_variations_qtr3(activ.getExpl_of_variations_qtr2());
                activ.setPerc_of_TargetAchieved_qtr31(activ.getPerc_of_TargetAchieved_qtr21());
            } else if (qtr == 4) {
                activ.setCum_achievements_qtr4(activ.getCum_achievements_qtr3());
                activ.setExpl_of_variations_qtr4(activ.getExpl_of_variations_qtr3());
                activ.setPerc_of_TargetAchieved_qtr41(activ.getPerc_of_TargetAchieved_qtr31());
            }
            tempActs.add(activ);
        }

        sampleUrc_ActivitiesService.saveAllActivities(tempActs);
        refreshKeepingSelections();
        configureSubmit();
    }

    private void configureFinancialGridByPriorityArea() {
        financialGrid.removeAllColumns();
        financialGrid.setSizeFull();
        financialGrid.setAllRowsVisible(true);
        financialGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );

        financialGrid.addColumn(PriorityArea::getName).setHeader("NDP Programme");

        financialGrid.addColumn(new ComponentRenderer<>(area -> {
            Span span = new Span(formatBigDecimal(totalBudget));
            Tooltip tooltip = Tooltip.forComponent(span);
            tooltip.setText("Total Budget: " + formatBigDecimal(totalBudget));
            tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
            return span;
        })).setHeader("Planned Output / Budget (UGX)");

        financialGrid.addColumn(new ComponentRenderer<>(area -> {
            Span span = new Span(formatBigDecimal(totalBudget));
            Tooltip tooltip = Tooltip.forComponent(span);
            tooltip.setText("Approved Budget: " + formatBigDecimal(totalBudget));
            tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
            return span;
        })).setHeader("Approved Budget (UGX)");

        financialGrid.addColumn(new ComponentRenderer<>(area -> {
            Span label = new Span(formatBigDecimal(totalActualExpenditure.abs()));
            if (totalActualExpenditure.abs().doubleValue() <= cumReleasedFund.abs().doubleValue()) {
                label.getStyle().set("color", "green").set("font-weight", "bold");
            } else {
                label.getStyle().set("color", "red").set("font-weight", "bold");
                label.getElement().setProperty("title", "⚠ Warning: Below You Actual Expenditure");
            }
            Tooltip tooltip = Tooltip.forComponent(label);
            tooltip.setText("Release Spent: " + formatBigDecimal(totalActualExpenditure.abs()));
            tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
            return label;
        })).setHeader("Release Spent (UGX) BNs");

        financialGrid.addColumn(area -> formatPercentage(totalActualExpenditure, cumReleasedFund))
                .setHeader("% of Budget Spent");

        financialGrid.addColumn(area -> {
            Optional<SectionBudgetPerformance> budgetchosen
                    = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
            if (budgetchosen.isEmpty()) {
                return "—";
            }
            SectionBudgetPerformance perf = budgetchosen.get();
            String reason = switch (qtr) {
                case 1 ->
                    Optional.ofNullable(perf.getReasonsForUnderOver1()).orElse("");
                case 2 ->
                    Optional.ofNullable(perf.getReasonsForUnderOver2()).orElse("");
                case 3 ->
                    Optional.ofNullable(perf.getReasonsForUnderOver3()).orElse("");
                case 4 ->
                    Optional.ofNullable(perf.getReasonsForUnderOver4()).orElse("");
                default ->
                    "";
            };
            return reason.isBlank() ? "—" : reason;
        }).setHeader("Reasons for Under / Over Absorption");

        financialGrid.setHeight("180px");
    }

    private void configurePhysicalGrid(List<Urc_Activities> activities) {
        physicalGrid.setSizeFull();
        physicalGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
        physicalGrid.setSelectionMode(Grid.SelectionMode.NONE);

        physicalGrid.addColumn(activity -> {
            URC_Priority_Areas area = activity.getUrcPriorityAreas();
            return (area != null && area.getName() != null) ? area.getName() : "—";
        }).setHeader("URC Programme");

        physicalGrid.addColumn(Urc_Activities::getName).setHeader("Workplan- Activity");
        physicalGrid.addColumn(Urc_Activities::getPerformanceIndicator).setHeader("Key Performance Indicator");
        physicalGrid.addColumn(Urc_Activities::getAnnualTarget).setHeader("Annual Target").setWidth("30px");

        physicalGrid.addColumn(new ComponentRenderer<>(activity -> {
            BigDecimalField field = new BigDecimalField();
            field.setWidthFull();
            field.setPlaceholder("0");
            field.setSuffixComponent(new Span("%"));

            Double pct = getPct(activity);
            field.setValue(pct != null ? BigDecimal.valueOf(pct) : null);
            field.getElement().addEventListener("click", e -> field.focus());
            field.addValueChangeListener(e -> {
                if (!e.isFromClient()) {
                    return;
                }

                BigDecimal value = e.getValue();
                if (value != null && (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("100")) > 0)) {
                    field.setInvalid(true);
                    field.setErrorMessage("Enter a value between 0 and 100");
                    return;
                }

                field.setInvalid(false);
                setPct(activity, value != null ? value.doubleValue() : null);
                savePhysicalRow(activity);
            });

            return field;
        })).setHeader("% Target Achieved").setWidth("60px");

        physicalGrid.addColumn(new ComponentRenderer<>(activity -> {
            TextArea field = new TextArea();
            field.setWidthFull();
            field.setMaxLength(1000);
            field.setMinHeight("120px");
            field.setValue(getCumAchievements(activity));
            field.setPlaceholder("Enter cumulative achievements");
            field.getElement().addEventListener("click", e -> field.focus());
            field.addValueChangeListener(e -> {
                if (!e.isFromClient()) {
                    return;
                }
                setCumAchievements(activity, e.getValue());
                savePhysicalRow(activity);
            });
            return field;
        })).setHeader("Cumulative Achievements").setFlexGrow(2);

        physicalGrid.addColumn(new ComponentRenderer<>(activity -> {
            TextArea area = new TextArea();
            area.setWidthFull();
            area.setValue(getVariation(activity));
            area.setPlaceholder("Enter explanation for variations");
            area.setMaxLength(1000);
            area.setMinHeight("120px");
            area.getElement().addEventListener("click", e -> area.focus());
            area.addValueChangeListener(e -> {
                if (!e.isFromClient()) {
                    return;
                }
                setVariation(activity, e.getValue());
                savePhysicalRow(activity);
            });
            return area;
        })).setHeader("Explanation for variations").setFlexGrow(2);

        physicalGrid.addComponentColumn(activity -> {
            Button editButton = new Button(getPctFormatted(activity), new Icon(VaadinIcon.PENCIL));
            editButton.addThemeVariants(
                    ButtonVariant.LUMO_TERTIARY_INLINE,
                    ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_SMALL
            );
            editButton.getElement().setProperty("title", "Align Expenditure to this Activity");
            editButton.getStyle().set("border-radius", "999px");
            editButton.addClickListener(e -> {
                if (activity != null && qtr != 0) {
                    openPerformanceDialog(physicalGrid, activities, activity, qtr);
                }
            });
            return editButton;
        }).setHeader("Actions").setWidth("120px").setFlexGrow(0);
    }

    private void savePhysicalRow(Urc_Activities activity) {
        String selectedQuarter = qtrComboBox.getValue();
        sampleUrc_ActivitiesService.update(activity);
        physicalGrid.getDataProvider().refreshItem(activity);
        if (!Objects.equals(qtrComboBox.getValue(), selectedQuarter)) {
            qtrComboBox.setValue(selectedQuarter);
        }
        configureSubmit();
    }

    private void openFinancialDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("450px");
        dialog.setHeight("250px");

        H3 header = new H3("Edit Quarter Financial Data");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMaximumFractionDigits(2);

        BigDecimalField cumulativeReleased = new BigDecimalField("Cumulative Funds Released");
        cumulativeReleased.setValue(totalActualExpenditure.abs());
        cumulativeReleased.addValueChangeListener(event -> {
            BigDecimal val = event.getValue();
            cumulativeReleased.setHelperText(val != null ? numberFormat.format(val) : "");
        });

        TextField reasonForOver = new TextField("Reasons for under / over absorption");
        cumulativeReleased.setWidthFull();
        reasonForOver.setWidthFull();
        cumulativeReleased.setEnabled(user.getRoles().contains(Role.ADMIN));

        Optional<SectionBudgetPerformance> existing
                = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
        budgetPer = existing.orElseGet(SectionBudgetPerformance::new);

        binderSectionBudgetPerformance.removeBean();
        switch (qtr) {
            case 1 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased)
                        .bind(SectionBudgetPerformance::getCumulativeFundsReleased1, SectionBudgetPerformance::setCumulativeFundsReleased1);
                binderSectionBudgetPerformance.forField(reasonForOver)
                        .bind(SectionBudgetPerformance::getReasonsForUnderOver1, SectionBudgetPerformance::setReasonsForUnderOver1);
            }
            case 2 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased)
                        .bind(SectionBudgetPerformance::getCumulativeFundsReleased2, SectionBudgetPerformance::setCumulativeFundsReleased2);
                binderSectionBudgetPerformance.forField(reasonForOver)
                        .bind(SectionBudgetPerformance::getReasonsForUnderOver2, SectionBudgetPerformance::setReasonsForUnderOver2);
            }
            case 3 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased)
                        .bind(SectionBudgetPerformance::getCumulativeFundsReleased3, SectionBudgetPerformance::setCumulativeFundsReleased3);
                binderSectionBudgetPerformance.forField(reasonForOver)
                        .bind(SectionBudgetPerformance::getReasonsForUnderOver3, SectionBudgetPerformance::setReasonsForUnderOver3);
            }
            case 4 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased)
                        .bind(SectionBudgetPerformance::getCumulativeFundsReleased4, SectionBudgetPerformance::setCumulativeFundsReleased4);
                binderSectionBudgetPerformance.forField(reasonForOver)
                        .bind(SectionBudgetPerformance::getReasonsForUnderOver4, SectionBudgetPerformance::setReasonsForUnderOver4);
            }
            default -> {
            }
        }

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        if (user.getRoles().contains(Role.ADMIN)) {
            formLayout.add(cumulativeReleased, reasonForOver);
        } else {
            formLayout.add(reasonForOver);
        }

        Button saveButton = new Button("Save", event -> {
            try {
                String selectedQuarter = qtrComboBox.getValue();

                if (chosenDsection != null && chosenBudget != null && qtr != 0) {
                    Optional<SectionBudgetPerformance> current
                            = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
                    budgetPer = current.orElseGet(SectionBudgetPerformance::new);

                    binderSectionBudgetPerformance.writeBean(budgetPer);
                    budgetPer.setBudget(chosenBudget);
                    budgetPer.setDeptSection(chosenDsection);
                    budgetPer.setPlannedBudget(totalBudget);
                    budgetPer.setApprovedBudget(totalBudget);

                    switch (qtr) {
                        case 1 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased1(cumulativeReleased.getValue());
                                budgetPer.setPercentageSpent1(
                                        calculateReleaseSpent(budgetPer.getCumulativeFundsReleased1(), totalActualExpenditure.abs())
                                );
                            }
                            budgetPer.setCumulativeFundsSpent1(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver1(reasonForOver.getValue());
                        }
                        case 2 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased2(cumulativeReleased.getValue());
                                BigDecimal tot = nvl(budgetPer.getCumulativeFundsReleased1())
                                        .add(nvl(budgetPer.getCumulativeFundsReleased2()));
                                budgetPer.setPercentageSpent2(calculateReleaseSpent(tot, totalActualExpenditure.abs()));
                            }
                            budgetPer.setCumulativeFundsSpent2(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver2(reasonForOver.getValue());
                        }
                        case 3 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased3(cumulativeReleased.getValue());
                                BigDecimal tot = nvl(budgetPer.getCumulativeFundsReleased1())
                                        .add(nvl(budgetPer.getCumulativeFundsReleased2()))
                                        .add(nvl(budgetPer.getCumulativeFundsReleased3()));
                                budgetPer.setPercentageSpent3(calculateReleaseSpent(tot, totalActualExpenditure.abs()));
                            }
                            budgetPer.setCumulativeFundsSpent3(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver3(reasonForOver.getValue());
                        }
                        case 4 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased4(cumulativeReleased.getValue());
                                BigDecimal tot = nvl(budgetPer.getCumulativeFundsReleased1())
                                        .add(nvl(budgetPer.getCumulativeFundsReleased2()))
                                        .add(nvl(budgetPer.getCumulativeFundsReleased3()))
                                        .add(nvl(budgetPer.getCumulativeFundsReleased4()));
                                budgetPer.setPercentageSpent4(calculateReleaseSpent(tot, totalActualExpenditure.abs()));
                            }
                            budgetPer.setCumulativeFundsSpent4(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver4(reasonForOver.getValue());
                        }
                        default -> {
                        }
                    }
                }

                if (totalActualExpenditure.abs().doubleValue() >= cumRealiseSpent.abs().doubleValue()) {
                    sectionBudgetPerformanceService.save(budgetPer);
                    refreshKeepingSelections();
                    configureSubmit();

                    if (!Objects.equals(qtrComboBox.getValue(), selectedQuarter)) {
                        qtrComboBox.setValue(selectedQuarter);
                    }

                    showNotification("Saved successfully!", NotificationVariant.LUMO_SUCCESS);
                } else {
                    showNotification("Check your expenditures to tally", NotificationVariant.LUMO_WARNING);
                }

                dialog.close();
            } catch (ValidationException ex) {
                showNotification("Validation error: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        saveButton.getStyle().set("background-color", "#007bff").set("color", "white").set("margin-right", "10px");
        cancelButton.getStyle().set("background-color", "#6c757d").set("color", "white");

        dialog.add(header, formLayout, saveButton, cancelButton);
        dialog.open();
    }

    private void openPerformanceDialog(
            Grid<Urc_Activities> grid,
            List<Urc_Activities> activities,
            Urc_Activities activity,
            int qtr
    ) {
        Dialog dialog = new Dialog();
        dialog.setWidth("1100px");
        dialog.setHeight("650px");
        dialog.setMinWidth("550px");
        dialog.setHeaderTitle("Manage Deliverables & Quarterly Actuals - "
                + activity.getActivityCode() + ": " + activity.getName());
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.setResizable(true);

        List<QuarterlyActuals> sourceItems = quarterlyActualsService.getQuarterlyActuals(
                activity.getDeptSection().getANL_CODE(),
                periods.getFinancialYearPeriods(activity.getBudget(), qtr),
                activity
        );

        List<QuarterlyActuals> existingItems = quarterlyActualsService.findByPeriods(
                periods.getFinancialYearPeriods(activity.getBudget(), qtr)
        );

        Set<String> existingKeys = existingItems.stream()
                .map(item -> item.getAccountCode() + "|"
                + item.getPeriod() + "|"
                + item.getTransactionDateTime() + "|"
                + item.getJournalNo() + "|"
                + item.getJournalLine())
                .collect(Collectors.toSet());

        List<QuarterlyActuals> filteredItems = sourceItems.stream()
                .filter(item -> !existingKeys.contains(
                item.getAccountCode() + "|"
                + item.getPeriod() + "|"
                + item.getTransactionDateTime() + "|"
                + item.getJournalNo() + "|"
                + item.getJournalLine()))
                .collect(Collectors.toList());

        List<QuarterlyActuals> targetItems = new ArrayList<>(activity.getQuarterlyActuals());

        ListDataProvider<QuarterlyActuals> sourceProvider = new ListDataProvider<>(filteredItems);
        ListDataProvider<QuarterlyActuals> targetProvider = new ListDataProvider<>(targetItems);

        Grid<QuarterlyActuals> sourceGrid = buildActualsGrid(sourceProvider, targetProvider, true);
        Grid<QuarterlyActuals> targetGrid = buildActualsGrid(targetProvider, sourceProvider, false);

        sourceGrid.setDataProvider(sourceProvider);
        targetGrid.setDataProvider(targetProvider);

        Grid.Column<QuarterlyActuals> amountColumn = targetGrid.getColumns().stream()
                .filter(col -> "Amount".equals(col.getHeaderText()))
                .findFirst()
                .orElse(null);

        if (amountColumn != null) {
            updateAmountFooter(amountColumn, targetProvider);
            targetProvider.addDataProviderListener(event -> updateAmountFooter(amountColumn, targetProvider));
        }

        setupDragAndDrop(sourceGrid, targetGrid, sourceProvider, targetProvider);

        TextField sourceSearch = buildSearchField("Search available actuals...", sourceProvider);
        TextField targetSearch = buildSearchField("Search assigned actuals...", targetProvider);

        HorizontalLayout searchLayout = new HorizontalLayout(sourceSearch, targetSearch);
        searchLayout.setWidthFull();
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout gridsLayout = new HorizontalLayout(sourceGrid, targetGrid);
        gridsLayout.setWidthFull();
        gridsLayout.setSpacing(true);
        gridsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Button saveBtn = new Button("Save", e -> {
            String selectedQuarter = qtrComboBox.getValue();

            activity.getQuarterlyActuals().clear();
            for (QuarterlyActuals qa : targetItems) {
                qa.setActivity(activity);
                activity.getQuarterlyActuals().add(qa);
            }

            switch (qtr) {
                case 1 ->
                    activity.setQtr1A(sumOfQtr(activity.getQuarterlyActuals()));
                case 2 ->
                    activity.setQtr2A(sumOfQtr(activity.getQuarterlyActuals()));
                case 3 ->
                    activity.setQtr3A(sumOfQtr(activity.getQuarterlyActuals()));
                case 4 ->
                    activity.setQtr4A(sumOfQtr(activity.getQuarterlyActuals()));
                default -> {
                }
            }

            sampleUrc_ActivitiesService.saveActivity(activity);
            refreshKeepingSelections();
            configureSubmit();

            if (!Objects.equals(qtrComboBox.getValue(), selectedQuarter)) {
                qtrComboBox.setValue(selectedQuarter);
            }

            Notification.show("Changes saved successfully.", 3000, Notification.Position.BOTTOM_CENTER);
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout footer = new HorizontalLayout(saveBtn, cancelBtn);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setWidthFull();
        footer.getStyle().set("margin-top", "15px");

        VerticalLayout content = new VerticalLayout(
                new H3("Quarterly Financial Performance for QTR " + qtr),
                new Span("Drag items between tables to assign or unassign quarterly actuals."),
                searchLayout,
                gridsLayout,
                new Hr(),
                footer
        );
        content.setPadding(true);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialog.add(content);
        dialog.open();
    }

    private Grid<QuarterlyActuals> buildActualsGrid(
            ListDataProvider<QuarterlyActuals> thisProvider,
            ListDataProvider<QuarterlyActuals> otherProvider,
            boolean source
    ) {
        Grid<QuarterlyActuals> grid = new Grid<>(QuarterlyActuals.class, false);

        grid.addColumn(new ComponentRenderer<>(item -> {
            if (item.getAccountCode() == null) {
                return new Span("");
            }

            Span span = new Span(item.getAccountCode());
            span.getStyle()
                    .set("display", "inline-block")
                    .set("padding", "4px 10px")
                    .set("background-color", "#1976d2")
                    .set("color", "white")
                    .set("border-radius", "4px")
                    .set("font-weight", "bold")
                    .set("cursor", "pointer")
                    .set("text-align", "center")
                    .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.2)");

            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.setOpenOnClick(true);

            contextMenu.addItem("Select All By Account Code", e -> {
                String acc = item.getAccountCode();
                if (acc == null) {
                    Notification.show("Account code is missing.", 2000, Notification.Position.MIDDLE);
                    return;
                }

                grid.deselectAll();

                Set<QuarterlyActuals> sameAccountItems = thisProvider.getItems().stream()
                        .filter(i -> acc.equals(i.getAccountCode()))
                        .collect(Collectors.toSet());

                sameAccountItems.forEach(grid::select);

                if (sameAccountItems.isEmpty()) {
                    Notification.show("No matching items found.", 2000, Notification.Position.MIDDLE);
                }
            });

            if (source) {
                contextMenu.addItem("Align/Transfer to Activity", e -> {
                    Set<QuarterlyActuals> selectedItems = new HashSet<>(grid.getSelectedItems());

                    if (selectedItems.isEmpty()) {
                        selectedItems.add(item);
                    }

                    if (selectedItems.isEmpty()) {
                        Notification.show("No items selected.", 2000, Notification.Position.MIDDLE);
                        return;
                    }

                    int moved = 0;
                    for (QuarterlyActuals qa : selectedItems) {
                        if (!otherProvider.getItems().contains(qa)) {
                            thisProvider.getItems().remove(qa);
                            otherProvider.getItems().add(qa);
                            moved++;
                        }
                    }

                    thisProvider.refreshAll();
                    otherProvider.refreshAll();
                    grid.deselectAll();

                    Notification.show(
                            moved + " item(s) transferred to activity.",
                            2000,
                            Notification.Position.BOTTOM_CENTER
                    );
                });
            } else {
                contextMenu.addItem("Remove From Activity", e -> {
                    Set<QuarterlyActuals> selectedItems = new HashSet<>(grid.getSelectedItems());

                    if (selectedItems.isEmpty()) {
                        selectedItems.add(item);
                    }

                    if (selectedItems.isEmpty()) {
                        Notification.show("No items selected.", 2000, Notification.Position.MIDDLE);
                        return;
                    }

                    int moved = 0;
                    for (QuarterlyActuals qa : selectedItems) {
                        if (!otherProvider.getItems().contains(qa)) {
                            thisProvider.getItems().remove(qa);
                            otherProvider.getItems().add(qa);
                            moved++;
                        }
                    }

                    thisProvider.refreshAll();
                    otherProvider.refreshAll();
                    grid.deselectAll();

                    Notification.show(
                            moved + " item(s) removed from activity.",
                            2000,
                            Notification.Position.BOTTOM_CENTER
                    );
                });
            }

            return span;
        })).setHeader("Account Code").setAutoWidth(true).setSortable(true).setFlexGrow(0);

        grid.addColumn(QuarterlyActuals::getDescription)
                .setHeader("Description")
                .setSortable(true);

        grid.addColumn(qa -> formatBigDecimal(qa.getAmount()))
                .setHeader("Amount")
                .setSortable(true);

        grid.addColumn(item -> item.getTransactionDateTime() == null
                ? ""
                : item.getTransactionDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")))
                .setHeader("Trans Date")
                .setSortable(true);

        grid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
        grid.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        grid.setHeight("400px");
        grid.setWidth("48%");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        return grid;
    }

    private void setupDragAndDrop(
            Grid<QuarterlyActuals> sourceGrid,
            Grid<QuarterlyActuals> targetGrid,
            ListDataProvider<QuarterlyActuals> sourceProvider,
            ListDataProvider<QuarterlyActuals> targetProvider
    ) {
        sourceGrid.setRowsDraggable(true);
        targetGrid.setRowsDraggable(true);

        sourceGrid.setDropMode(GridDropMode.ON_GRID);
        targetGrid.setDropMode(GridDropMode.ON_GRID);

        sourceGrid.addDragStartListener(e -> {
            if (!e.getDraggedItems().isEmpty()) {
                draggedItem = e.getDraggedItems().get(0);
            }
        });

        targetGrid.addDragStartListener(e -> {
            if (!e.getDraggedItems().isEmpty()) {
                draggedItem = e.getDraggedItems().get(0);
            }
        });

        sourceGrid.addDragEndListener(e -> draggedItem = null);
        targetGrid.addDragEndListener(e -> draggedItem = null);

        targetGrid.addDropListener(e -> {
            if (draggedItem == null) {
                return;
            }

            QuarterlyActuals itemToMove = draggedItem;
            draggedItem = null;

            if (targetProvider.getItems().contains(itemToMove)) {
                return;
            }

            sourceProvider.getItems().remove(itemToMove);
            targetProvider.getItems().add(itemToMove);

            sourceProvider.refreshAll();
            targetProvider.refreshAll();

            sourceGrid.deselectAll();
            targetGrid.deselectAll();
        });

        sourceGrid.addDropListener(e -> {
            if (draggedItem == null) {
                return;
            }

            QuarterlyActuals itemToMove = draggedItem;
            draggedItem = null;

            if (sourceProvider.getItems().contains(itemToMove)) {
                return;
            }

            targetProvider.getItems().remove(itemToMove);
            sourceProvider.getItems().add(itemToMove);

            sourceProvider.refreshAll();
            targetProvider.refreshAll();

            sourceGrid.deselectAll();
            targetGrid.deselectAll();
        });
    }

    private TextField buildSearchField(String placeholder, ListDataProvider<QuarterlyActuals> provider) {
        TextField search = new TextField();
        search.setPlaceholder(placeholder);
        search.setWidth("48%");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());

        search.addValueChangeListener(e -> {
            String filterText = e.getValue().trim().toLowerCase();
            provider.setFilter(qa
                    -> (qa.getAccountCode() != null && qa.getAccountCode().toLowerCase().contains(filterText))
                    || (qa.getDescription() != null && qa.getDescription().toLowerCase().contains(filterText))
                    || (qa.getTransactionDateTime() != null
                    && qa.getTransactionDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
                            .toLowerCase().contains(filterText))
            );
        });

        return search;
    }

    private Double getPct(Urc_Activities a) {
        if (a == null || chosenDsection == null || chosenBudget == null || qtr == 0) {
            return null;
        }
        return switch (qtr) {
            case 1 ->
                a.getPerc_of_TargetAchieved_qtr11() != null ? Math.abs(a.getPerc_of_TargetAchieved_qtr11()) : 0.0;
            case 2 ->
                a.getPerc_of_TargetAchieved_qtr21() != null ? Math.abs(a.getPerc_of_TargetAchieved_qtr21()) : 0.0;
            case 3 ->
                a.getPerc_of_TargetAchieved_qtr31() != null ? Math.abs(a.getPerc_of_TargetAchieved_qtr31()) : 0.0;
            case 4 ->
                a.getPerc_of_TargetAchieved_qtr41() != null ? Math.abs(a.getPerc_of_TargetAchieved_qtr41()) : 0.0;
            default ->
                null;
        };
    }

    private BigDecimal getActivityQuarterExpenditure(Urc_Activities a) {
        if (a == null || chosenDsection == null || chosenBudget == null || qtr == 0) {
            return BigDecimal.ZERO;
        }
        return switch (qtr) {
            case 1 ->
                a.getQtr1A() != null ? a.getQtr1A().abs() : BigDecimal.ZERO;
            case 2 ->
                a.getQtr2A() != null ? a.getQtr2A().abs() : BigDecimal.ZERO;
            case 3 ->
                a.getQtr3A() != null ? a.getQtr3A().abs() : BigDecimal.ZERO;
            case 4 ->
                a.getQtr4A() != null ? a.getQtr4A().abs() : BigDecimal.ZERO;
            default ->
                BigDecimal.ZERO;
        };
    }

    private String getPctFormatted(Urc_Activities a) {
        BigDecimal value = getActivityQuarterExpenditure(a);
        return value == null ? "" : String.format("%,.2f", value);
    }

    private void setPct(Urc_Activities a, Double value) {
        if (a == null) {
            return;
        }
        switch (qtr) {
            case 1 ->
                a.setPerc_of_TargetAchieved_qtr11(value);
            case 2 ->
                a.setPerc_of_TargetAchieved_qtr21(value);
            case 3 ->
                a.setPerc_of_TargetAchieved_qtr31(value);
            case 4 ->
                a.setPerc_of_TargetAchieved_qtr41(value);
            default -> {
            }
        }
    }

    private String getCumAchievements(Urc_Activities a) {
        if (a == null || chosenDsection == null || chosenBudget == null || qtr == 0) {
            return "";
        }
        return switch (qtr) {
            case 1 ->
                nvl(a.getCum_achievements_qtr1(), "");
            case 2 ->
                nvl(a.getCum_achievements_qtr2(), "");
            case 3 ->
                nvl(a.getCum_achievements_qtr3(), "");
            case 4 ->
                nvl(a.getCum_achievements_qtr4(), "");
            default ->
                "";
        };
    }

    private void setCumAchievements(Urc_Activities a, String value) {
        String v = value == null ? "" : value;
        switch (qtr) {
            case 1 ->
                a.setCum_achievements_qtr1(v);
            case 2 ->
                a.setCum_achievements_qtr2(v);
            case 3 ->
                a.setCum_achievements_qtr3(v);
            case 4 ->
                a.setCum_achievements_qtr4(v);
            default -> {
            }
        }
    }

    private String getVariation(Urc_Activities a) {
        if (a == null || chosenDsection == null || chosenBudget == null || qtr == 0) {
            return "";
        }
        return switch (qtr) {
            case 1 ->
                nvl(a.getExpl_of_variations_qtr1(), "");
            case 2 ->
                nvl(a.getExpl_of_variations_qtr2(), "");
            case 3 ->
                nvl(a.getExpl_of_variations_qtr3(), "");
            case 4 ->
                nvl(a.getExpl_of_variations_qtr4(), "");
            default ->
                "";
        };
    }

    private void setVariation(Urc_Activities a, String value) {
        String v = value == null ? "" : value;
        switch (qtr) {
            case 1 ->
                a.setExpl_of_variations_qtr1(v);
            case 2 ->
                a.setExpl_of_variations_qtr2(v);
            case 3 ->
                a.setExpl_of_variations_qtr3(v);
            case 4 ->
                a.setExpl_of_variations_qtr4(v);
            default -> {
            }
        }
    }

    public BigDecimal calculateTotalActivityActuals(Urc_Activities activity) {
        if (activity.getQuarterlyActuals() == null || activity.getQuarterlyActuals().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return activity.getQuarterlyActuals().stream()
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalActivityActuals(Urc_Activities activity, Set<Integer> periods) {
        if (activity.getQuarterlyActuals() == null || activity.getQuarterlyActuals().isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (periods == null || periods.isEmpty()) {
            return activity.getQuarterlyActuals().stream()
                    .map(QuarterlyActuals::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return activity.getQuarterlyActuals().stream()
                .filter(q -> q.getPeriod() != null && periods.contains(q.getPeriod()))
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalActualsForActivities(List<Urc_Activities> activities, Set<Integer> periods) {
        if (activities == null || activities.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return activities.stream()
                .map(a -> calculateTotalActivityActuals(a, periods))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String nvl(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String calculateReleaseSpent(BigDecimal totalReleased, BigDecimal releaseSpent) {
        if (totalReleased == null || totalReleased.compareTo(BigDecimal.ZERO) == 0 || releaseSpent == null) {
            return "0%";
        }
        BigDecimal percentage = releaseSpent
                .divide(totalReleased, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return percentage.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
    }

    public String formatPercentage(BigDecimal value, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00%";
        }
        BigDecimal percentage = value.abs()
                .multiply(BigDecimal.valueOf(100))
                .divide(total.abs(), 2, RoundingMode.HALF_UP);
        return percentage.abs().toPlainString() + "%";
    }

    public String formatInBillions(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        BigDecimal inBillions = amount.divide(BigDecimal.valueOf(1_000_000_000));
        DecimalFormat df = new DecimalFormat("#,##0.##");
        return df.format(inBillions);
    }

    public BigDecimal sumOfQtr(Set<QuarterlyActuals> actualsList) {
        if (actualsList == null || actualsList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return actualsList.stream()
                .filter(a -> a.getPeriod() != null)
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateAmountFooter(Grid.Column<QuarterlyActuals> amountColumn,
            ListDataProvider<QuarterlyActuals> targetProvider) {
        BigDecimal total = targetProvider.getItems().stream()
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        amountColumn.setFooter("Total: " + formatBigDecimal(total));
    }

    private String formatBigDecimal(BigDecimal value) {
        return value != null ? String.format("%,.2f", value) : "0.00";
    }

    private String formatBigDecimal2(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        BigDecimal absValue = value.abs();
        String formatted = String.format("%,.2f", absValue);
        return value.signum() < 0 ? "(" + formatted + ")" : formatted;
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
    }
}
