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
    private UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService;
    private final PriorityAreaService PriorityAreaService;
    private final URC_Priority_AreasService URC_Priority_AreasService;
    private final QuarterlyActualsService QuarterlyActualsService;
    private final SectionBudgetPerformanceService sectionBudgetPerformanceService;
    private final QtrReleasesServiceImpl qtrReleasesServiceImpl;
    private AuthenticatedUser authenticatedUser;

    private final ComboBox<Budget> budgetComboBox = new ComboBox<>("Select Budget");
    private final ComboBox<String> qtrComboBox = new ComboBox<>("Select Quarter");
    int qtr = 0;
    private final ComboBox<UrcDeptSectionAnlDimbgt> sectionComboBox = new ComboBox<>("Select Section");
    private final MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sectionMultiComboBox = new MultiSelectComboBox<>("Combine Section Reports");
    private final Button submitButton = new Button("Submit");
    private final Button downloadButton = new Button("Download");
    Button importPhyPerf = new Button("Import");

    private final Grid<PriorityArea> financialGrid = new Grid<>(PriorityArea.class, false);
    GridContextMenu<PriorityArea> contextMenuPriorityArea = new GridContextMenu<>(financialGrid);

    private final Grid<Urc_Activities> physicalGrid = new Grid<>(Urc_Activities.class, false);
    GridContextMenu<Urc_Activities> contextMenuPhysicalGrid = new GridContextMenu<>(physicalGrid);

    GetPeriods periods = new GetPeriods();
    Set<Integer> period = new HashSet<>();
    BigDecimal cumRealiseSpent = BigDecimal.ZERO;
    BigDecimal totalBudget = BigDecimal.ZERO;
    BigDecimal totalActualExpenditure = BigDecimal.ZERO;
    BigDecimal cumReleasedFund = BigDecimal.ZERO;

    private QuarterlyActuals draggedItem = null;
    Binder<SectionBudgetPerformance> binderSectionBudgetPerformance = new Binder<>(SectionBudgetPerformance.class);
    SectionBudgetPerformance budgetPer = null;
    boolean submit = false;
    List<Urc_Activities> acts = new ArrayList();
    SectionBudgetPerformance perfo = null;

    public PhysicalFinancialPerformanceView(List<Urc_Activities> activities, BudgetService chosenBudgetService, BudgetItemsService budgetItemsService, Urc_ActivitiesService sampleUrc_ActivitiesService,
            UserService userService, AuthenticatedUser authenticatedUser, UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService, URC_Priority_AreasService sampleURC_Priority_AreasService,
            SALFLDGService SALFLDGService, PriorityAreaService PriorityAreaService, URC_Priority_AreasService URC_Priority_AreasService, QuarterlyActualsService QuarterlyActualsService,
            SectionBudgetPerformanceService sectionBudgetPerformanceService, QtrReleasesServiceImpl qtrReleasesServiceImpl) {
        this.chosenBudgetService = chosenBudgetService;
        this.budgetItemsService = budgetItemsService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.urcDeptSectionAnlDimbgtService = urcDeptSectionAnlDimbgtService;
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
        this.SALFLDGService = SALFLDGService;
        this.PriorityAreaService = PriorityAreaService;
        this.URC_Priority_AreasService = URC_Priority_AreasService;
        this.QuarterlyActualsService = QuarterlyActualsService;
        this.sectionBudgetPerformanceService = sectionBudgetPerformanceService;
        this.qtrReleasesServiceImpl = qtrReleasesServiceImpl;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        configureUser();
        loadFiscalYearData();
        setSections();
        configureQuarter();
        configureSubmit();

        HorizontalLayout headMenu = new HorizontalLayout();
        contextMenuPriorityArea.setOpenOnClick(true);
        contextMenuPhysicalGrid.setOpenOnClick(true);

        headMenu.add(budgetComboBox, sectionComboBox, qtrComboBox, importPhyPerf, submitButton, downloadButton, sectionMultiComboBox);
        headMenu.setAlignItems(Alignment.BASELINE);

        add(headMenu);

        // Header
        H3 header = new H3("FINANCIAL & PHYSICAL PERFORMANCE REPORT");
        add(header);

        // Financial Performance Section
        H3 financialHeader = new H3("1. FINANCIAL PERFORMANCE");
        add(financialHeader);

        configureFinancialGridByPriorityArea();
        add(financialGrid);

        // Physical Performance Section
        H3 physicalHeader = new H3("2. PHYSICAL PERFORMANCE");
        add(physicalHeader);
        configurePhysicalGrid(activities);
        add(physicalGrid);
    }

    private void configureUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }
    }

    private void loadFiscalYearData() {
        budgetComboBox.setItems(query -> chosenBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        budgetComboBox.setItemLabelGenerator(Budget::getFinancialYear);
        budgetComboBox.setClearButtonVisible(true);
        Optional<Budget> recentBudget = chosenBudgetService.getMostRecurrentBudget();
        if (recentBudget.isPresent()) {
            budgetComboBox.setValue(recentBudget.get());
            chosenBudget = recentBudget.get();
        }
        budgetComboBox.addValueChangeListener(e -> {
            chosenBudget = e.getValue();
            refreshFinancialGrid();
            configureSubmit();
            disableElements();
            financialGrid.getDataProvider().refreshAll();
            physicalGrid.getDataProvider().refreshAll();

        });
        contextMenuPriorityArea.addItem("Edit Quarter Financial Data", e -> {
            openFinancialDialog();
        });

    }

    private void disableElements() {
        if (chosenDsection == null || chosenBudget == null || qtr == 0) {
            submitButton.setEnabled(false);
            downloadButton.setEnabled(false);
            sectionMultiComboBox.setEnabled(false);
        }
    }

    private void configureSubmit() {
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        if (chosenDsection == null || chosenBudget == null || qtr == 0) {
            submitButton.setEnabled(false);
            downloadButton.setEnabled(false);
            sectionMultiComboBox.setEnabled(false);
        } else {
            sectionMultiComboBox.setEnabled(true);
            Optional<SectionBudgetPerformance> budgetchosen = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
            if (budgetchosen.isPresent()) {
                perfo = budgetchosen.get();
                if (perfo != null) {
                    switch (qtr) {
                        case 1 -> {
                            if (perfo.getSubmitQtr1() != null) {
                                submitButton.setEnabled(perfo.getSubmitQtr1());
                                submit = perfo.getSubmitQtr1();
                            } else {
                                submit = false;
                            }

                        }
                        case 2 -> {
                            if (perfo.getSubmitQtr2() != null) {
                                submitButton.setEnabled(perfo.getSubmitQtr2());
                                submit = perfo.getSubmitQtr2();
                            } else {
                                submit = false;
                            }
                        }
                        case 3 -> {
                            if (perfo.getSubmitQtr3() != null) {
                                submitButton.setEnabled(perfo.getSubmitQtr3());
                                submit = perfo.getSubmitQtr3();
                            } else {
                                submit = false;
                            }
                        }
                        case 4 -> {
                            if (perfo.getSubmitQtr4() != null) {
                                submitButton.setEnabled(perfo.getSubmitQtr4());
                                submit = perfo.getSubmitQtr4();
                            } else {
                                submit = false;
                            }
                        }
                        default ->
                            submit = false;
                    }
                } else {
                    submit = false;
                }

                if (submit == true) {
                    downloadButton.setEnabled(true);
                    submitButton.setText("Submitted");
                    submitButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    //System.out.println("Download yes");
                } else {
                    submitButton.setText("Submit");
                    downloadButton.setEnabled(false);
                    submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                }
                if (((isQuarterDataComplete(perfo, qtr) == true) || areActivitiesCompleteForQuarter(acts, qtr) == true) && submit == false) {
                    submitButton.setEnabled(true);
                } else {
                    submitButton.setEnabled(false);
                }
                submitButton.addClickListener(e -> {

                    switch (qtr) {
                        case 1:
                            perfo.setSubmitQtr1(true);
                            break;
                        case 2:
                            perfo.setSubmitQtr2(true);
                            break;
                        case 3:
                            perfo.setSubmitQtr3(true);
                            break;
                        case 4:
                            perfo.setSubmitQtr4(true);
                            break;
                        default:
                            break;
                    }
                    if (totalActualExpenditure.abs().doubleValue() >= cumRealiseSpent.abs().doubleValue()) {
                        sectionBudgetPerformanceService.save(perfo);
                        configureSubmit();
                        showNotification("Successifully Sumitted", NotificationVariant.LUMO_SUCCESS);
                    } else {
                        showNotification("Check your Expenditure", NotificationVariant.LUMO_WARNING);
                    }

                });
                downloadButton.addClickListener(e -> {
                    AnnualPerformancePdfGenerator pdfgen = new AnnualPerformancePdfGenerator(sampleURC_Priority_AreasService, sampleUrc_ActivitiesService,
                            chosenDsection, SALFLDGService, budgetItemsService, sectionBudgetPerformanceService, qtrComboBox.getValue(), chosenBudget);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                    String timestamp = LocalDateTime.now().format(dtf);
                    String fileName = "Financial_Physical_Report_" + chosenDsection.getNAME().toUpperCase() + "_" + chosenBudget.getFinancialYear() + "_" + qtrComboBox.getValue() + "_" + timestamp + ".pdf";
                    StreamResource pdfResource = new StreamResource(fileName, () -> {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            pdfgen.generateAnnualReportPdf(baos);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return new ByteArrayInputStream(baos.toByteArray());
                    });
                    // Anchor must be added to the UI for the browser to allow download/open
                    /*                    Anchor download = new Anchor(pdfResource, "");
                    download.getElement().setAttribute("target", "_blank");
                    download.getElement().callJsFunction("click");
                    add(download);*/

                    Anchor download = new Anchor(pdfResource, "Download PDF");
                    download.getElement().setAttribute("download", true);
                    download.getElement().setAttribute("target", "_blank"); // optional, open in new tab
                    add(download);

                    download.getElement().callJsFunction("click");

// Remove the anchor after 2 seconds
                    getUI().ifPresent(ui
                            -> ui.getPage().executeJs(
                                    "setTimeout(() => $0.remove(), 2000)", download.getElement()
                            )
                    );

                });

            }
            List<UrcDeptSectionAnlDimbgt> getSubmittedDeptSectionsQtr = null;

            switch (qtr) {
                case 1:
                    getSubmittedDeptSectionsQtr = sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr1(chosenBudget);
                    break;
                case 2:
                    getSubmittedDeptSectionsQtr = sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr2(chosenBudget);
                    break;
                case 3:
                    getSubmittedDeptSectionsQtr = sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr3(chosenBudget);
                    break;
                case 4:
                    getSubmittedDeptSectionsQtr = sectionBudgetPerformanceService.getSubmittedDeptSectionsQtr4(chosenBudget);
                    break;
                default:
                    break;
            }
            if (user.getRoles().contains(Role.ADMIN)) {
                sectionMultiComboBox.setItems(getSubmittedDeptSectionsQtr);
            } else {
                sectionMultiComboBox.setItems(retainCommonDeptSections(getSubmittedDeptSectionsQtr, user.getDeptsection().stream().toList()));
            }

        }

    }

    public List<UrcDeptSectionAnlDimbgt> retainCommonDeptSections(
            List<UrcDeptSectionAnlDimbgt> list1,
            List<UrcDeptSectionAnlDimbgt> list2) {

        // Defensive null checks
        if (list1 == null || list2 == null) {
            return Collections.emptyList();
        }

        // Use equals() and hashCode() from UrcDeptSectionAnlDimbgt for comparison
        return list2.stream()
                .filter(list1::contains)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean isQuarterDataComplete(SectionBudgetPerformance perf, int quarter) {
        if (perf == null) {
            return false;
        }

        switch (quarter) {
            case 1:
                return perf.getCumulativeFundsReleased1() != null
                        && perf.getCumulativeFundsSpent1() != null
                        && perf.getPercentageSpent1() != null && !perf.getPercentageSpent1().isBlank()
                        && perf.getReasonsForUnderOver1() != null && !perf.getReasonsForUnderOver1().isBlank();

            case 2:
                return perf.getCumulativeFundsReleased2() != null
                        && perf.getCumulativeFundsSpent2() != null
                        && perf.getPercentageSpent2() != null && !perf.getPercentageSpent2().isBlank()
                        && perf.getReasonsForUnderOver2() != null && !perf.getReasonsForUnderOver2().isBlank();

            case 3:
                return perf.getCumulativeFundsReleased3() != null
                        && perf.getCumulativeFundsSpent3() != null
                        && perf.getPercentageSpent3() != null && !perf.getPercentageSpent3().isBlank()
                        && perf.getReasonsForUnderOver3() != null && !perf.getReasonsForUnderOver3().isBlank();

            case 4:
                return perf.getCumulativeFundsReleased4() != null
                        && perf.getCumulativeFundsSpent4() != null
                        && perf.getPercentageSpent4() != null && !perf.getPercentageSpent4().isBlank()
                        && perf.getReasonsForUnderOver4() != null && !perf.getReasonsForUnderOver4().isBlank();

            default:
                throw new IllegalArgumentException("Quarter must be between 1 and 4");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Checks whether all required fields for a given quarter are filled for
     * each activity in the list.
     *
     * @param activities list of Urc_Activities to check
     * @param quarter the quarter number (1-4)
     * @return true if all activities have the required fields filled, false
     * otherwise
     */
    public boolean areActivitiesCompleteForQuarter(List<Urc_Activities> activities, int quarter) {
        if (activities == null || activities.isEmpty()) {
            return false;
        }

        for (Urc_Activities activity : activities) {
            if (activity == null) {
                return false;
            }

            // --- Validate common fields ---
            if (isBlank(activity.getName())
                    || isBlank(activity.getPerformanceIndicator())
                    || isBlank(activity.getAnnualTarget())) {
                return false;
            }

            // --- Validate quarter-specific fields ---
            switch (quarter) {
                case 1:
                    if (isBlank(activity.getCum_achievements_qtr1())
                            || isBlank(activity.getExpl_of_variations_qtr1())) {
                        return false;
                    }
                    break;

                case 2:
                    if (isBlank(activity.getCum_achievements_qtr2())
                            || isBlank(activity.getExpl_of_variations_qtr2())) {
                        return false;
                    }
                    break;

                case 3:
                    if (isBlank(activity.getCum_achievements_qtr3())
                            || isBlank(activity.getExpl_of_variations_qtr3())) {
                        return false;
                    }
                    break;

                case 4:
                    if (isBlank(activity.getCum_achievements_qtr4())
                            || isBlank(activity.getExpl_of_variations_qtr4())) {
                        return false;
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Quarter must be between 1 and 4");
            }
        }

        return true;
    }

    private void configureQuarter() {
        qtrComboBox.setItems("Qtr 1", "Qtr 2", "Qtr 3", "Qtr 4");
        qtrComboBox.setClearButtonVisible(true);

        qtrComboBox.addValueChangeListener(e -> {
            String qt = e.getValue();
            importPhyPerf.setEnabled(false);
            if (qt == null) {
                qtr = 0;
            } else {
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
            refreshFinancialGrid();
            configureSubmit();
            financialGrid.getDataProvider().refreshAll();
            physicalGrid.getDataProvider().refreshAll();
            // ✅ Always check after update
            disableElements();
        });
        importPhyPerf.addSingleClickListener(e -> {

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Confirm import");
            dialog.setText("This will copy the previous quarter's physical performance into Quarter " + qtr + " for " + acts.size() + " activities. Continue?");

            dialog.setCancelable(true);
            dialog.setCancelText("No");

            dialog.setConfirmText("Yes, import");
            dialog.addConfirmListener(ev -> {

                List<Urc_Activities> tempActs = new ArrayList<>();
                for (Urc_Activities activ : acts) {
                    if (qtr == 2) {
                        //activ.setPerc_of_TargetAchieved_qtr2(activ.getPerc_of_TargetAchieved_qtr1());
                        activ.setCum_achievements_qtr2(activ.getCum_achievements_qtr1());
                        activ.setExpl_of_variations_qtr2(activ.getExpl_of_variations_qtr1());
                        activ.setPerc_of_TargetAchieved_qtr21(activ.getPerc_of_TargetAchieved_qtr11());
                    } else if (qtr == 3) {
                        //activ.setPerc_of_TargetAchieved_qtr3(activ.getPerc_of_TargetAchieved_qtr2());
                        activ.setCum_achievements_qtr3(activ.getCum_achievements_qtr2());
                        activ.setExpl_of_variations_qtr3(activ.getExpl_of_variations_qtr2());
                        activ.setPerc_of_TargetAchieved_qtr31(activ.getPerc_of_TargetAchieved_qtr21());
                    } else if (qtr == 4) {
                        //activ.setPerc_of_TargetAchieved_qtr4(activ.getPerc_of_TargetAchieved_qtr3());
                        activ.setCum_achievements_qtr4(activ.getCum_achievements_qtr3());
                        activ.setExpl_of_variations_qtr4(activ.getExpl_of_variations_qtr3());
                        activ.setPerc_of_TargetAchieved_qtr41(activ.getPerc_of_TargetAchieved_qtr31());
                    }
                    tempActs.add(activ);
                }

                sampleUrc_ActivitiesService.saveAllActivities(tempActs);
                refreshFinancialGrid();
            });

            dialog.open();
        });
    }

    private void refreshFinancialGrid() {
        financialGrid.setItems(Collections.emptyList());
        if (chosenDsection == null || chosenBudget == null || qtr == 0) {
            financialGrid.setItems(Collections.emptyList());
            return;
        }
        switch (qtr) {
            case 1:
                period = periods.getFinancialYearPeriods(chosenBudget, 1);
                break;
            case 2:
                Set<Integer> period1 = periods.getFinancialYearPeriods(chosenBudget, 1);
                Set<Integer> period2 = periods.getFinancialYearPeriods(chosenBudget, 2);
                period1.addAll(period2);
                Set<Integer> combinedPeriods = period1;
                period = combinedPeriods;
                // System.out.println(period1+" Period 1 "+period2+" Period 2  "+period+" -Combined "+qtr);
                break;
            case 3:
                period = Stream.concat(periods.getFinancialYearPeriods(chosenBudget, 1).stream(), periods.getFinancialYearPeriods(chosenBudget, 2).stream()).collect(Collectors.toSet());
                period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(chosenBudget, 3).stream()).collect(Collectors.toSet());
                break;
            case 4:
                period = Stream.concat(periods.getFinancialYearPeriods(chosenBudget, 1).stream(), periods.getFinancialYearPeriods(chosenBudget, 2).stream()).collect(Collectors.toSet());
                period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(chosenBudget, 3).stream()).collect(Collectors.toSet());
                period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(chosenBudget, 4).stream()).collect(Collectors.toSet());
                break;
            default:
                break;
        }
        acts = sampleUrc_ActivitiesService.findByDeptSectionAndBudget(chosenDsection, chosenBudget);
        //totalActualExpenditure = getTotalActualsForActivities(acts, period);

        totalActualExpenditure = SALFLDGService.findSumOfAmountByAnalT1AndPeriodIn(chosenDsection.getANL_CODE(), period.stream().toList());
        totalActualExpenditure = SALFLDGService.getTotalAmountByPeriods2(period, chosenDsection.getANL_CODE());
        // Recalculate key values
        cumRealiseSpent = SALFLDGService.getTotalAmountByPeriods2(period, chosenDsection.getANL_CODE());
        totalBudget = budgetItemsService.calculateTotalDeptExpenditure2(chosenBudget, chosenDsection);

        cumReleasedFund = cumFundsReleased();

        // Refresh data
        List<PriorityArea> priorityAreas = URC_Priority_AreasService.getDistinctPriorityAreasByBudget(chosenBudget.getStartDate());
        financialGrid.setItems(priorityAreas);
        financialGrid.getDataProvider().refreshAll();

        physicalGrid.setItems(acts);
        physicalGrid.getDataProvider().refreshAll();

    }

    private BigDecimal cumFundsReleased() {
        BigDecimal cumRealise = BigDecimal.ZERO;
        SectionBudgetPerformance perf = null;
        Optional<SectionBudgetPerformance> budgetchosen = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
        if (budgetchosen.isPresent()) {
            perf = budgetchosen.get();
            if (perf != null) {
                switch (qtr) {
                    case 1 ->
                        cumRealise = Optional.ofNullable(perf.getCumulativeFundsReleased1()).orElse(BigDecimal.ZERO);
                    case 2 ->
                        cumRealise = Optional.ofNullable(perf.getCumulativeFundsReleased2()).orElse(BigDecimal.ZERO);
                    case 3 ->
                        cumRealise = Optional.ofNullable(perf.getCumulativeFundsReleased3()).orElse(BigDecimal.ZERO);
                    case 4 ->
                        cumRealise = Optional.ofNullable(perf.getCumulativeFundsReleased4()).orElse(BigDecimal.ZERO);
                    default ->
                        cumRealise = BigDecimal.ZERO;
                }
            } else {
                cumRealise = BigDecimal.ZERO;
            }

        }
        return cumRealise;
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
            // No period filter provided — return total of all periods
            return activity.getQuarterlyActuals().stream()
                    .map(QuarterlyActuals::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return activity.getQuarterlyActuals().stream()
                .filter(q -> q.getPeriod() != null && periods.contains(q.getPeriod())) // filter by allowed periods
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
            sectionComboBox.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);

            sectionMultiComboBox.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        } else {
            sectionComboBox.setItems(user.getDeptsection());
            sectionMultiComboBox.setItems(user.getDeptsection());
        }
        sectionComboBox.addValueChangeListener(e -> {
            chosenDsection = e.getValue();

            refreshFinancialGrid();
            configureSubmit();
            disableElements();
            financialGrid.getDataProvider().refreshAll();
            physicalGrid.getDataProvider().refreshAll();
        });

        sectionMultiComboBox.addValueChangeListener(e -> {

        });

    }

    private void configureFinancialGridByPriorityArea() {
        financialGrid.removeAllColumns();
        financialGrid.setSizeFull();
        financialGrid.setAllRowsVisible(true);
        financialGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);

        financialGrid.addColumn(PriorityArea::getName).setHeader("NDP Programme");

        financialGrid.addColumn(new ComponentRenderer<>(area -> {
            Span span = new Span();
            span.setText(formatBigDecimal(totalBudget));
            Tooltip tooltip = Tooltip.forComponent(span);
            tooltip.setText("Total Budget: " + formatBigDecimal(totalBudget));
            tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
            tooltip.setManual(false); // show on hover            
            return span;
        })).setHeader("Planned Output / Budget (UGX)");

        financialGrid.addColumn(new ComponentRenderer<>(area -> {
            Span span = new Span();
            span.setText(formatBigDecimal(totalBudget));
            Tooltip tooltip = Tooltip.forComponent(span);
            tooltip.setText("Approved Budget: " + formatBigDecimal(totalBudget));
            tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
            tooltip.setManual(false); // show on hover            
            return span;
        })).setHeader("Approved Budget (UGX)");

        financialGrid.addColumn(new ComponentRenderer<>(area -> {
            BigDecimal cumRealise = BigDecimal.ZERO;
            Tuple result = qtrReleasesServiceImpl.getCumulativeQuarterReleases(chosenBudget.getId(), chosenDsection);
            cumReleasedFund = switch (qtr) {
                case 1 ->
                    //result.get("q1Total", BigDecimal.class);
                    nvl(result.get("q1Total", BigDecimal.class));
                case 2 ->
                    nvl(result.get("q2Total", BigDecimal.class));
                case 3 ->
                    nvl(result.get("q3Total", BigDecimal.class));
                case 4 ->
                    nvl(result.get("q4Total", BigDecimal.class));
                default ->
                    BigDecimal.ZERO;
            };
            Span span = new Span(formatBigDecimal(cumReleasedFund));
            span.getStyle()
                    .set("font-weight", "500")
                    .set("color", "#2E3A59")
                    .set("font-size", "var(--lumo-font-size-s)");

            Tooltip tooltip = Tooltip.forComponent(span);
            tooltip.setText("Cumulative Funds Released: " + formatBigDecimal(cumReleasedFund));
            tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
            tooltip.setManual(false); // show on hover

            return span;
        }))
                .setHeader("Cumulative Funds Released (UGX)");

        financialGrid.addColumn(new ComponentRenderer<>(area -> {
            Span label = new Span(formatBigDecimal(totalActualExpenditure.abs()));
            // System.out.println(totalActualExpenditure.abs()+" .........");
            if (totalActualExpenditure.abs().doubleValue() <= cumReleasedFund.abs().doubleValue()) {
                label.getStyle().set("color", "green");
                label.getStyle().set("font-weight", "bold");

            } else if (totalActualExpenditure.abs().doubleValue() > cumReleasedFund.abs().doubleValue()) {
                label.getStyle().set("color", "red");
                label.getStyle().set("font-weight", "bold");
                label.getElement().setProperty("title", "⚠ Warning: Below You Actual Expenditure");

            }

            label.setText(formatBigDecimal(totalActualExpenditure.abs()));
            Tooltip tooltip = Tooltip.forComponent(label);
            tooltip.setText("Release Spent: " + formatBigDecimal(totalActualExpenditure.abs()));
            tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
            tooltip.setManual(false); // show on hover            

            return label;
        })).setHeader("Release Spent (UGX) BNs");

        financialGrid.addColumn(area -> {
            String reason = formatPercentage(totalActualExpenditure, cumReleasedFund);

            return reason.isBlank() ? "—" : reason;
        }).setHeader("% of Release Spent");

        financialGrid.addColumn(area -> {
            String reason = "";
            SectionBudgetPerformance perf = null;
            Optional<SectionBudgetPerformance> budgetchosen = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
            if (budgetchosen.isPresent()) {
                perf = budgetchosen.get();
                if (perf != null) {
                    switch (qtr) {
                        case 1 ->
                            reason = Optional.ofNullable(perf.getReasonsForUnderOver1()).orElse("");
                        case 2 ->
                            reason = Optional.ofNullable(perf.getReasonsForUnderOver2()).orElse("");
                        case 3 ->
                            reason = Optional.ofNullable(perf.getReasonsForUnderOver3()).orElse("");
                        case 4 ->
                            reason = Optional.ofNullable(perf.getReasonsForUnderOver4()).orElse("");
                        default ->
                            reason = "";
                    }
                }

            }
            // }

            return reason.isBlank() ? "—" : reason;
        })
                .setHeader("Reasons for Under / Over Absorption");

        financialGrid.setHeight("180px"); // Adjust as needed
    }

    public String formatInBillions(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        BigDecimal inBillions = amount.divide(BigDecimal.valueOf(1_000_000_000));
        DecimalFormat df = new DecimalFormat("#,##0.##"); // 2 decimal places
        return df.format(inBillions);
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
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

    private void configurePhysicalGrid(List<Urc_Activities> activities) {
        physicalGrid.setSizeFull();
        physicalGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
        physicalGrid.addColumn(activity -> {
            URC_Priority_Areas area = activity.getUrcPriorityAreas();
            return (area != null && area.getName() != null) ? area.getName() : "—";
        })
                .setHeader("URC Programme");

        physicalGrid.addColumn(Urc_Activities::getName)
                .setHeader("Workplan- Activity");

        physicalGrid.addColumn(Urc_Activities::getPerformanceIndicator)
                .setHeader("Key Performance Indicator");

        physicalGrid.addColumn(Urc_Activities::getAnnualTarget)
                .setHeader("Annual Target").setWidth("30px");

        physicalGrid.addColumn(new ComponentRenderer<>(activity -> {
            BigDecimalField field = new BigDecimalField();
            field.setWidthFull();
            field.setPlaceholder("0");
            field.setSuffixComponent(new Span("%"));

            Double pct = getPct(activity);
            field.setValue(pct != null ? BigDecimal.valueOf(pct.doubleValue()) : null);

            field.addValueChangeListener(e -> {
                if (!e.isFromClient()) {
                    return;
                }

                BigDecimal value = e.getValue();

                if (value != null
                        && (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("100")) > 0)) {
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

            field.addValueChangeListener(e -> {
                if (!e.isFromClient()) {
                    return;
                }
                setCumAchievements(activity, e.getValue());
                savePhysicalRow(activity);
            });

            return field;
        })).setHeader("Cumulative Achievements").setFlexGrow(2);

        /*        physicalGrid.addColumn(new ComponentRenderer<>(area -> {
        String getCum_achievements = "-";
        Span span = new Span();
        if (chosenDsection != null && chosenBudget != null && qtr != 0) {
        switch (qtr) {
        case 1:
        getCum_achievements = area.getExpl_of_variations_qtr1();
        break;
        case 2:
        getCum_achievements = area.getExpl_of_variations_qtr2();
        break;
        case 3:
        getCum_achievements = area.getExpl_of_variations_qtr3();
        break;
        case 4:
        getCum_achievements = area.getExpl_of_variations_qtr4();
        break;
        default:
        break;
        }
        }
        if (getCum_achievements == null) {
        getCum_achievements = "-";
        }
        span.setText(getCum_achievements);
        ContextMenu contextMenu = new ContextMenu(span);
        contextMenu.setOpenOnClick(true); //
        contextMenu.addItem("Edit Quarter Physical Data", e -> {
        openPerformanceDialog(physicalGrid, activities, area, qtr);
        }
        );
        
        return span;
        })).setHeader("Explanation for variations");*/
        physicalGrid.addColumn(new ComponentRenderer<>(activity -> {
            TextArea area = new TextArea();
            area.setWidthFull();
            area.setValue(getVariation(activity));
            area.setPlaceholder("Enter explanation for variations");
            area.setMaxLength(1000);
            area.setMinHeight("120px");

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
        sampleUrc_ActivitiesService.update(activity);
        physicalGrid.getDataProvider().refreshItem(activity);
    }

    private String calculateReleaseSpent(BigDecimal totalReleased, BigDecimal releaseSpent) {
        if (totalReleased == null || totalReleased.compareTo(BigDecimal.ZERO) == 0
                || releaseSpent == null) {
            return "0%";
        }

        // Calculate (releaseSpent / totalReleased) * 100
        BigDecimal percentage = releaseSpent
                .divide(totalReleased, 4, RoundingMode.HALF_UP) // 4 decimal precision
                .multiply(BigDecimal.valueOf(100));

        return percentage.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
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

        if (value == null) {
            return "";
        }

        return String.format("%,.2f", value);
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

    private String nvl(String value, String defaultValue) {
        return value != null ? value : defaultValue;
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
        }
    }

    // Method to open dialog
    private void openFinancialDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("450px");
        dialog.setHeight("250px");

        // Header
        H3 header = new H3("Edit Quarter Financial Data");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMaximumFractionDigits(2);
        // Fields
        BigDecimalField cumulativeReleased = new BigDecimalField("Cumulative Funds Released");
        cumulativeReleased.setValue(totalActualExpenditure.abs());
        cumulativeReleased.addValueChangeListener(event -> {
            BigDecimal val = event.getValue();
            if (val != null) {
                cumulativeReleased.setHelperText(numberFormat.format(val));
            } else {
                cumulativeReleased.setHelperText("");
            }
        });
        TextField reason_for_over = new TextField("Reasons for under / over absorption");

        cumulativeReleased.setWidthFull();
        reason_for_over.setWidthFull();
        if (user.getRoles().contains(Role.ADMIN)) {
            cumulativeReleased.setEnabled(true);
        } else {
            cumulativeReleased.setEnabled(false);
        }

        Optional<SectionBudgetPerformance> isbudgetPerExist = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
        if (isbudgetPerExist.isPresent()) {
            budgetPer = isbudgetPerExist.get();
            reason_for_over.setValue(budgetPer.getReasonsForUnderOver1());
        } else {
            budgetPer = new SectionBudgetPerformance();

        }

        // Binder to bind data if needed
        // Bind depending on quarter
        switch (qtr) {

            case 1 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased).bind(SectionBudgetPerformance::getCumulativeFundsReleased1, SectionBudgetPerformance::setCumulativeFundsReleased1);
                binderSectionBudgetPerformance.forField(reason_for_over).bind(SectionBudgetPerformance::getReasonsForUnderOver1, SectionBudgetPerformance::setReasonsForUnderOver1);

            }
            case 2 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased).bind(SectionBudgetPerformance::getCumulativeFundsReleased2, SectionBudgetPerformance::setCumulativeFundsReleased2);
                binderSectionBudgetPerformance.forField(reason_for_over).bind(SectionBudgetPerformance::getReasonsForUnderOver2, SectionBudgetPerformance::setReasonsForUnderOver2);
            }
            case 3 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased).bind(SectionBudgetPerformance::getCumulativeFundsReleased3, SectionBudgetPerformance::setCumulativeFundsReleased3);
                binderSectionBudgetPerformance.forField(reason_for_over).bind(SectionBudgetPerformance::getReasonsForUnderOver3, SectionBudgetPerformance::setReasonsForUnderOver3);
            }
            case 4 -> {
                binderSectionBudgetPerformance.forField(cumulativeReleased).bind(SectionBudgetPerformance::getCumulativeFundsReleased4, SectionBudgetPerformance::setCumulativeFundsReleased4);
                binderSectionBudgetPerformance.forField(reason_for_over).bind(SectionBudgetPerformance::getReasonsForUnderOver4, SectionBudgetPerformance::setReasonsForUnderOver4);
            }
        }

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        if (user.getRoles().contains(Role.ADMIN)) {
            cumulativeReleased.setEnabled(true);
            formLayout.add(cumulativeReleased, reason_for_over);
        } else {
            cumulativeReleased.setEnabled(false);
            formLayout.add(reason_for_over);
        }

        // Buttons
        Button saveButton = new Button("Save", event -> {
            try {
                if (chosenDsection != null && chosenBudget != null && qtr != 0) {

                    Optional<SectionBudgetPerformance> existing = sectionBudgetPerformanceService.findByBudgetAndDeptSection(chosenBudget, chosenDsection);
                    if (existing.isPresent()) {
                        budgetPer = existing.get();
                    } else {
                        budgetPer = new SectionBudgetPerformance();
                    }
                    binderSectionBudgetPerformance.writeBean(budgetPer);
                    budgetPer.setBudget(chosenBudget);
                    budgetPer.setDeptSection(chosenDsection);
                    budgetPer.setPlannedBudget(totalBudget);
                    budgetPer.setApprovedBudget(totalBudget);

                    switch (qtr) {
                        case 1 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased1(cumulativeReleased.getValue());
                                budgetPer.setPercentageSpent1(calculateReleaseSpent(budgetPer.getCumulativeFundsReleased1(), totalActualExpenditure.abs()));
                            }
                            budgetPer.setCumulativeFundsSpent1(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver1(reason_for_over.getValue());

                        }
                        case 2 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased2(cumulativeReleased.getValue());
                                BigDecimal tot = (budgetPer.getCumulativeFundsReleased1() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased1())
                                        .add(budgetPer.getCumulativeFundsReleased2() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased2());
                                budgetPer.setPercentageSpent2(calculateReleaseSpent(tot, totalActualExpenditure.abs()));
                            }
                            budgetPer.setCumulativeFundsSpent2(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver2(reason_for_over.getValue());
                        }
                        case 3 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased3(cumulativeReleased.getValue());
                                BigDecimal tot = (budgetPer.getCumulativeFundsReleased1() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased1())
                                        .add(budgetPer.getCumulativeFundsReleased2() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased2())
                                        .add(budgetPer.getCumulativeFundsReleased3() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased3());
                                budgetPer.setPercentageSpent3(calculateReleaseSpent(tot, totalActualExpenditure.abs()));
                            }

                            budgetPer.setCumulativeFundsSpent3(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver3(reason_for_over.getValue());
                        }
                        case 4 -> {
                            if (user.getRoles().contains(Role.ADMIN)) {
                                budgetPer.setCumulativeFundsReleased4(cumulativeReleased.getValue());
                                BigDecimal tot = (budgetPer.getCumulativeFundsReleased1() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased1())
                                        .add(budgetPer.getCumulativeFundsReleased2() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased2())
                                        .add(budgetPer.getCumulativeFundsReleased3() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased3())
                                        .add(budgetPer.getCumulativeFundsReleased4() == null ? BigDecimal.ZERO : budgetPer.getCumulativeFundsReleased4());
                                budgetPer.setPercentageSpent4(calculateReleaseSpent(tot, totalActualExpenditure.abs()));
                            }

                            budgetPer.setCumulativeFundsSpent4(totalActualExpenditure.abs());
                            budgetPer.setReasonsForUnderOver4(reason_for_over.getValue());
                        }
                    }
                }

                if (totalActualExpenditure.abs().doubleValue() >= cumRealiseSpent.abs().doubleValue()) {

                    sectionBudgetPerformanceService.save(budgetPer);
                    refreshFinancialGrid();
                    showNotification("Saved successfully!", NotificationVariant.LUMO_SUCCESS);
                } else {
                    showNotification("Check you expenditures to tally", NotificationVariant.LUMO_WARNING);
                }

                //  chosenBudgetService.update(budget);
                dialog.close();
            } catch (ValidationException ex) {
                showNotification("Validation error: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        // Style buttons
        saveButton.getStyle().set("background-color", "#007bff");
        saveButton.getStyle().set("color", "white");
        saveButton.getStyle().set("margin-right", "10px");

        cancelButton.getStyle().set("background-color", "#6c757d");
        cancelButton.getStyle().set("color", "white");

        // Add everything to dialog
        dialog.add(header, formLayout, saveButton, cancelButton);
        dialog.open();
    }

    private void openPerformanceDialog(Grid grid, List<Urc_Activities> activities, Urc_Activities activity, int qtr) {
        Dialog dialog = new Dialog();
        dialog.setWidth("1100px");
        dialog.setHeight("650px");
        dialog.setMinWidth("550px");
        dialog.setHeaderTitle("Manage Deliverables & Quarterly Actuals - " + activity.getActivityCode() + ": " + activity.getName());
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.setResizable(true);

        // === Data Providers ===
        List<QuarterlyActuals> sourceItems = QuarterlyActualsService.getQuarterlyActuals(activity.getDeptSection().getANL_CODE(), periods.getFinancialYearPeriods(activity.getBudget(), qtr), activity);
        List<QuarterlyActuals> existingItems = QuarterlyActualsService.findByPeriods(periods.getFinancialYearPeriods(activity.getBudget(), qtr));

        // Build a Set of unique composite keys for fast lookup
        Set<String> existingKeys = existingItems.stream()
                .map(item -> item.getAccountCode() + "|"
                + item.getPeriod() + "|"
                + item.getTransactionDateTime() + "|"
                + item.getJournalNo() + "|"
                + item.getJournalLine())
                .collect(Collectors.toSet());

// Filter sourceItems to remove any that already exist
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

        // === Source Grid (Available QuarterlyActuals) ===
        Grid<QuarterlyActuals> sourceGrid = new Grid<>(QuarterlyActuals.class, false);
        //sourceGrid.addColumn(QuarterlyActuals::getAccountCode).setHeader("Account Code").setWidth("60px").setSortable(true);

        sourceGrid.addColumn(new ComponentRenderer<>(item -> {
            if (item.getAccountCode() == null) {
                return new Span(""); // Avoid returning an empty string
            }

            Span span = new Span(item.getAccountCode());
            span.getStyle()
                    .set("display", "inline-block")
                    .set("padding", "4px 10px")
                    .set("background-color", "#1976d2") // Blue button color
                    .set("color", "white")
                    .set("border-radius", "4px")
                    .set("font-weight", "bold")
                    .set("cursor", "pointer")
                    .set("text-align", "center")
                    .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.2)");

            // Create right-click or left-click context menu
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.setOpenOnClick(true); // Open menu on left click

            // Add context menu action
            contextMenu.addItem("Select All By Account Code", e -> {
                String acc = item.getAccountCode();
                if (acc != null) {
                    Set<QuarterlyActuals> sameAccountItems = filteredItems.stream()
                            .filter(i -> acc.equals(i.getAccountCode()))
                            .collect(Collectors.toSet());
                    sameAccountItems.forEach(sourceGrid::select);
                }
            });
            contextMenu.addItem("Align/Transfer to Activity", e -> {
                // Get selected items from the source grid
                Set<QuarterlyActuals> selectedItems = new HashSet<>(sourceGrid.getSelectedItems());
                if (selectedItems.isEmpty()) {
                    Notification.show("No items selected.", 2000, Notification.Position.MIDDLE);
                    return;
                }

                // Move selected items to the target grid
                selectedItems.forEach(itemThis -> {
                    sourceProvider.getItems().remove(itemThis);
                    targetProvider.getItems().add(itemThis);
                });

                // Refresh both grids
                sourceProvider.refreshAll();
                targetProvider.refreshAll();

                // Clear selection after transfer
                sourceGrid.deselectAll();

                Notification.show(selectedItems.size() + " item(s) transferred to activity.", 2000, Notification.Position.BOTTOM_CENTER);
            });
            return span;
        }))
                .setHeader("Account Code")
                .setAutoWidth(true)
                .setSortable(true)
                .setFlexGrow(0);

        sourceGrid.addColumn(QuarterlyActuals::getDescription).setHeader("Description").setSortable(true);
        sourceGrid.addColumn(qa -> formatBigDecimal(qa.getAmount())).setHeader("Amount").setSortable(true);
        sourceGrid.addColumn(item -> {

            if (item.getTransactionDateTime() == null) {
                return "";
            }
            return item.getTransactionDateTime()
                    .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        }).setHeader("Trans Date").setSortable(true);
        sourceGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        sourceGrid.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        sourceGrid.setHeight("400px");
        sourceGrid.setWidth("48%");
        sourceGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        // === Target Grid (Activity's QuarterlyActuals) ===
        Grid<QuarterlyActuals> targetGrid = new Grid<>(QuarterlyActuals.class, false);
        // targetGrid.addColumn(QuarterlyActuals::getAccountCode).setHeader("Account Code").setWidth("60px").setSortable(true);

        targetGrid.addColumn(new ComponentRenderer<>(item -> {
            if (item.getAccountCode() == null) {
                return new Span(""); // Avoid returning an empty string
            }

            Span span = new Span(item.getAccountCode());
            span.getStyle()
                    .set("display", "inline-block")
                    .set("padding", "4px 10px")
                    .set("background-color", "#1976d2") // Blue button color
                    .set("color", "white")
                    .set("border-radius", "4px")
                    .set("font-weight", "bold")
                    .set("cursor", "pointer")
                    .set("text-align", "center")
                    .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.2)");

            // Create right-click or left-click context menu
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.setOpenOnClick(true); // Open menu on left click

            // Add context menu action
            contextMenu.addItem("Select All By Account Code", e -> {
                String acc = item.getAccountCode();
                if (acc != null) {
                    Set<QuarterlyActuals> sameAccountItems = existingItems.stream()
                            .filter(i -> acc.equals(i.getAccountCode()))
                            .collect(Collectors.toSet());
                    sameAccountItems.forEach(targetGrid::select);
                }
            });

            contextMenu.addItem("Remove From Activity", e -> {
                // Get selected items from the source grid
                Set<QuarterlyActuals> selectedItems = new HashSet<>(targetGrid.getSelectedItems());
                if (selectedItems.isEmpty()) {
                    Notification.show("No items selected.", 2000, Notification.Position.MIDDLE);
                    return;
                }

                // Move selected items to the target grid
                selectedItems.forEach(itemThis -> {

                    targetProvider.getItems().remove(itemThis);
                    sourceProvider.getItems().add(itemThis);
                });

                // Refresh both grids
                sourceProvider.refreshAll();
                targetProvider.refreshAll();

                // Clear selection after transfer
                targetGrid.deselectAll();

                Notification.show(selectedItems.size() + " item(s) removed from activity.", 2000, Notification.Position.BOTTOM_CENTER);
            });

            return span;
        }))
                .setHeader("Account Code")
                .setAutoWidth(true)
                .setSortable(true)
                .setFlexGrow(0);
        targetGrid.addColumn(QuarterlyActuals::getDescription).setHeader("Description").setSortable(true);
        Grid.Column<QuarterlyActuals> amountColumn = targetGrid.addColumn(qa -> formatBigDecimal(qa.getAmount())).setHeader("Amount").setSortable(true);
        targetGrid.addColumn(QuarterlyActuals::getTransactionDateTime).setHeader("Trans Date").setSortable(true);
        targetGrid.addColumn(item -> {
            if (item.getTransactionDateTime() == null) {
                return "";
            }
            return item.getTransactionDateTime()
                    .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        }).setHeader("Trans Date");
        targetGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        targetGrid.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        targetGrid.setHeight("400px");
        targetGrid.setWidth("48%");
        targetGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        targetGrid.setItems(activity.getQuarterlyActuals());

        sourceGrid.setDataProvider(sourceProvider);
        targetGrid.setDataProvider(targetProvider);

        BigDecimal total = targetProvider.getItems().stream()
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        amountColumn.setFooter("Total: " + formatBigDecimal(total));

        targetProvider.addDataProviderListener(event -> {
            updateAmountFooter(amountColumn, targetProvider);
        });

        sourceGrid.setRowsDraggable(true);
        targetGrid.setRowsDraggable(true);
        sourceGrid.setDropMode(GridDropMode.ON_GRID);
        targetGrid.setDropMode(GridDropMode.ON_GRID);

// Drag start
        sourceGrid.addDragStartListener(e -> draggedItem = e.getDraggedItems().get(0));
        targetGrid.addDragStartListener(e -> draggedItem = e.getDraggedItems().get(0));

// Drag end — reset
        sourceGrid.addDragEndListener(e -> draggedItem = null);
        targetGrid.addDragEndListener(e -> draggedItem = null);

// Drop logic
        targetGrid.addDropListener(e -> {
            if (draggedItem != null) {
                // Remove from source provider's data set
                sourceProvider.getItems().remove(draggedItem);

                // Add to target provider's data set
                targetProvider.getItems().add(draggedItem);

                // Refresh both
                sourceProvider.refreshAll();
                targetProvider.refreshAll();

                draggedItem = null;
            }
        });

        sourceGrid.addDropListener(e -> {
            if (draggedItem != null) {
                targetProvider.getItems().remove(draggedItem);
                sourceProvider.getItems().add(draggedItem);

                sourceProvider.refreshAll();
                targetProvider.refreshAll();

                draggedItem = null;
            }
        });

// === Filters (Search Fields) ===
        TextField sourceSearch = new TextField();
        sourceSearch.setPlaceholder("Search available actuals...");
        sourceSearch.setWidth("48%");
        sourceSearch.setPrefixComponent(VaadinIcon.SEARCH.create());

        TextField targetSearch = new TextField();
        targetSearch.setPlaceholder("Search assigned actuals...");
        targetSearch.setWidth("48%");
        targetSearch.setPrefixComponent(VaadinIcon.SEARCH.create());

// === Apply filtering logic ===
        sourceSearch.addValueChangeListener(e -> {
            String filterText = e.getValue().trim().toLowerCase();
            sourceProvider.setFilter(qa
                    -> (qa.getAccountCode() != null && qa.getAccountCode().toLowerCase().contains(filterText))
                    || (qa.getDescription() != null && qa.getDescription().toLowerCase().contains(filterText))
                    || (qa.getTransactionDateTime() != null
                    && qa.getTransactionDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toLowerCase().contains(filterText))
            );
        });

        targetSearch.addValueChangeListener(e -> {
            String filterText = e.getValue().trim().toLowerCase();
            targetProvider.setFilter(qa
                    -> (qa.getAccountCode() != null && qa.getAccountCode().toLowerCase().contains(filterText))
                    || (qa.getDescription() != null && qa.getDescription().toLowerCase().contains(filterText))
                    || (qa.getTransactionDateTime() != null
                    && qa.getTransactionDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toLowerCase().contains(filterText))
            );
        });

        // === Deliverables Section ===
        FormLayout deliverableLayout = new FormLayout();
        HorizontalLayout lay = new HorizontalLayout();
        deliverableLayout.setWidthFull();
        deliverableLayout.getStyle()
                .set("margin-top", "10px")
                .set("padding", "6px")
                .set("background-color", "#f9fafc")
                .set("border-radius", "6px")
                .set("box-shadow", "inset 0 1px 3px rgba(0,0,0,0.1)")
                // 👇 Force columns to stretch evenly and fill 100%
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))")
                .set("gap", "12px");

        TextArea kpiField = new TextArea("KPI");
        kpiField.setValue(activity.getPerformanceIndicator());
        kpiField.setEnabled(false);
        kpiField.setPlaceholder("% of ICT equipment procured and installed");
        // kpiField.setWidth("400px");
        TextArea outputField = new TextArea("Planned Activity Output");
        outputField.setValue(activity.getOutput());
        outputField.setEnabled(false);
        outputField.setPlaceholder("% of ICT equipment procured and installed");

        // outputField.setWidth("400px");
        TextArea outcomeField = new TextArea("Planned Activity Outcome");
        outcomeField.setValue(activity.getOutput());
        outcomeField.setEnabled(false);
        kpiField.setPlaceholder("% of ICT equipment procured and installed");
        // outcomeField.setWidth("400px");

        TextField annualTargetSpan = new TextField("Planned Annual Target");
        annualTargetSpan.setEnabled(false);
        String annualTargetValue = activity.getAnnualTarget() != null ? activity.getAnnualTarget().toString() : "";
        annualTargetSpan.setValue(annualTargetValue);
        annualTargetSpan.setPlaceholder("100%");

        TextArea cumAchievementsSpan = new TextArea("Cumulative Achievements");
        cumAchievementsSpan.setPlaceholder("Enhanced modern technology use through acquiring new ICT equipments");
        TextField perc_of_release_SpentSpan = new TextField("% Target Achieved");
        perc_of_release_SpentSpan.setPlaceholder("90%");

        NumberField perc_of_release_SpentSpan2 = new NumberField("% Target Achieved");
        Span suffix = new Span("%");
        perc_of_release_SpentSpan2.setSuffixComponent(suffix);
        perc_of_release_SpentSpan2.setPlaceholder("90");
        perc_of_release_SpentSpan2.setSuffixComponent(new Span("%"));
        perc_of_release_SpentSpan2.setMin(0);
        perc_of_release_SpentSpan2.setMax(100);
        perc_of_release_SpentSpan2.setStep(0.1);

        TextArea expl_of_variationField = new TextArea("Explanation for Variations");
        expl_of_variationField.setPlaceholder("Delayed supplier delivery");
        //expl_of_variationField.setWidth("400px");

        switch (qtr) {
            case 1:
                cumAchievementsSpan.setValue(activity.getCum_achievements_qtr1() != null ? activity.getCum_achievements_qtr1() : "");
                perc_of_release_SpentSpan.setValue(activity.getPerc_of_TargetAchieved_qtr1() != null ? activity.getPerc_of_TargetAchieved_qtr1() : "");
                Double pct = activity.getPerc_of_TargetAchieved_qtr11();
                perc_of_release_SpentSpan2.setValue(pct != null ? pct : null);
                expl_of_variationField.setValue(activity.getExpl_of_variations_qtr1() != null ? activity.getExpl_of_variations_qtr1() : "");
                break;

            case 2:
                cumAchievementsSpan.setValue(activity.getCum_achievements_qtr2() != null ? activity.getCum_achievements_qtr2() : "");
                perc_of_release_SpentSpan.setValue(activity.getPerc_of_TargetAchieved_qtr2() != null ? activity.getPerc_of_TargetAchieved_qtr2() : "");
                perc_of_release_SpentSpan2.setValue(activity.getPerc_of_TargetAchieved_qtr21() != null ? activity.getPerc_of_TargetAchieved_qtr21() : 0.0);
                expl_of_variationField.setValue(activity.getExpl_of_variations_qtr2() != null ? activity.getExpl_of_variations_qtr2() : "");
                break;

            case 3:
                cumAchievementsSpan.setValue(activity.getCum_achievements_qtr3() != null ? activity.getCum_achievements_qtr3() : "");
                perc_of_release_SpentSpan.setValue(activity.getPerc_of_TargetAchieved_qtr3() != null ? activity.getPerc_of_TargetAchieved_qtr3() : "");
                perc_of_release_SpentSpan2.setValue(activity.getPerc_of_TargetAchieved_qtr31() != null ? activity.getPerc_of_TargetAchieved_qtr31() : 0.0);
                expl_of_variationField.setValue(activity.getExpl_of_variations_qtr3() != null ? activity.getExpl_of_variations_qtr3() : "");
                break;

            case 4:
                cumAchievementsSpan.setValue(activity.getCum_achievements_qtr4() != null ? activity.getCum_achievements_qtr4() : "");
                perc_of_release_SpentSpan.setValue(activity.getPerc_of_TargetAchieved_qtr4() != null ? activity.getPerc_of_TargetAchieved_qtr4() : "");
                perc_of_release_SpentSpan2.setValue(activity.getPerc_of_TargetAchieved_qtr41() != null ? activity.getPerc_of_TargetAchieved_qtr41() : 0.0);
                expl_of_variationField.setValue(activity.getExpl_of_variations_qtr4() != null ? activity.getExpl_of_variations_qtr4() : "");
                break;

            default:
                cumAchievementsSpan.setValue("");
                perc_of_release_SpentSpan.setValue("");
                perc_of_release_SpentSpan2.setValue(0.0);
                expl_of_variationField.setValue("");
                break;
        }

        lay.add(kpiField, outputField, outcomeField);
        // Set all text areas to the same nice style
        Stream.of(kpiField, outputField, outcomeField, expl_of_variationField, cumAchievementsSpan)
                .forEach(area -> {
                    area.setWidthFull();
                    area.setRequiredIndicatorVisible(true);
                    area.getStyle()
                            .set("min-height", "90px")
                            .set("border-radius", "6px")
                            .set("background-color", "white")
                            .set("padding", "1px")
                            .set("box-shadow", "inset 0 1px 3px rgba(0,0,0,0.1)");
                });

// Same for text fields
        Stream.of(annualTargetSpan, perc_of_release_SpentSpan)
                .forEach(field -> {
                    field.setWidthFull();
                    field.setRequiredIndicatorVisible(true);

                });
        deliverableLayout.add(lay, annualTargetSpan, perc_of_release_SpentSpan, perc_of_release_SpentSpan2, cumAchievementsSpan, expl_of_variationField);
        deliverableLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE),
                new FormLayout.ResponsiveStep("100px", 3, FormLayout.ResponsiveStep.LabelsPosition.ASIDE)
        );
        deliverableLayout.setWidthFull(); // 👈 ensures full parent width
        deliverableLayout.getStyle().set("flex", "1");

        // Make all components share equal width across the row
        /*        deliverableLayout.setColspan(kpiField, 1);
        deliverableLayout.setColspan(outputField, 1);
        deliverableLayout.setColspan(outcomeField, 1);
        deliverableLayout.setColspan(annualTargetSpan, 1);
        deliverableLayout.setColspan(cumAchievementsSpan, 1);
        deliverableLayout.setColspan(perc_of_release_SpentSpan, 1);*/
// Explanation field should take full width across the 3 columns
        deliverableLayout.setColspan(lay, 3);
        deliverableLayout.setColspan(annualTargetSpan, 2);
        deliverableLayout.setColspan(cumAchievementsSpan, 3);
        deliverableLayout.setColspan(expl_of_variationField, 3);
        // === Buttons (Footer) ===
        Button saveBtn = new Button("Save", e -> {

            List<Component> requiredFields = List.of(
                    kpiField, outputField, outcomeField,
                    annualTargetSpan, cumAchievementsSpan, perc_of_release_SpentSpan,
                    expl_of_variationField
            );

            boolean hasError = false;
            for (Component c : requiredFields) {
                if (c instanceof TextArea ta && ta.getValue().trim().isEmpty()) {
                    ta.getStyle().set("border-color", "var(--lumo-error-color)");
                    hasError = true;
                } else if (c instanceof TextField tf && tf.getValue().trim().isEmpty()) {
                    tf.getStyle().set("border-color", "var(--lumo-error-color)");
                    hasError = true;
                } else {
                    c.getElement().getStyle().remove("border-color");
                }
            }

            if (hasError) {
                Notification.show("Please fill in all required fields before saving.",
                        3000, Notification.Position.MIDDLE);
                return;
            }
            // Clear existing actuals before re-assigning
            activity.getQuarterlyActuals().clear();

            // Reassign new target items and set the relationship
            for (QuarterlyActuals qa : targetItems) {
                qa.setActivity(activity); // important for JPA consistency
                activity.getQuarterlyActuals().add(qa);
            }
            switch (qtr) {
                case 1:
                    activity.setQtr1A(sumOfQtr(activity.getQuarterlyActuals()));
                    // activity.setCum_achievements_qtr1(cumAchievementsSpan.getValue());
                    // activity.setPerc_of_TargetAchieved_qtr1(perc_of_release_SpentSpan.getValue());
                    // activity.setPerc_of_TargetAchieved_qtr11(perc_of_release_SpentSpan2.getValue());
                    // activity.setExpl_of_variations_qtr1(expl_of_variationField.getValue());
                    break;
                case 2:
                    activity.setQtr2A(sumOfQtr(activity.getQuarterlyActuals()));
                    // activity.setCum_achievements_qtr2(cumAchievementsSpan.getValue());
                    // activity.setPerc_of_TargetAchieved_qtr2(perc_of_release_SpentSpan.getValue());
                    // activity.setExpl_of_variations_qtr2(expl_of_variationField.getValue());
                    break;
                case 3:
                    activity.setQtr3A(sumOfQtr(activity.getQuarterlyActuals()));
                    // activity.setCum_achievements_qtr3(cumAchievementsSpan.getValue());
                    // activity.setPerc_of_TargetAchieved_qtr3(perc_of_release_SpentSpan.getValue());
                    //activity.setExpl_of_variations_qtr2(expl_of_variationField.getValue());
                    break;
                case 4:
                    activity.setQtr4A(sumOfQtr(activity.getQuarterlyActuals()));
                    // activity.setCum_achievements_qtr4(cumAchievementsSpan.getValue());
                    // activity.setPerc_of_TargetAchieved_qtr4(perc_of_release_SpentSpan.getValue());
                    // activity.setExpl_of_variations_qtr2(expl_of_variationField.getValue());
                    break;
                default:
                    break;
            }

            sampleUrc_ActivitiesService.saveActivity(activity);
            grid.setItems(activities);
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

        // === Main Layout ===
        HorizontalLayout searchLayout = new HorizontalLayout(sourceSearch, targetSearch);
        searchLayout.setWidthFull();
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout gridsLayout = new HorizontalLayout(sourceGrid, targetGrid);
        gridsLayout.setWidthFull();
        gridsLayout.setSpacing(true);
        gridsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        /*        VerticalLayout content = new VerticalLayout(
        new H3("Quarterly Financial Performance for QTR " + qtr),
        new Span("Drag items between tables to assign or unassign quarterly actuals."),
        searchLayout, // 👈 Add this line
        gridsLayout,
        new Hr(),
        new H4("Quarterly Physical Performance for QTR " + qtr),
        deliverableLayout,
        footer
        );*/
        VerticalLayout content = new VerticalLayout(
                new H3("Quarterly Financial Performance for QTR " + qtr),
                new Span("Drag items between tables to assign or unassign quarterly actuals."),
                searchLayout, // 👈 Add this line
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

    private String formatBigDecimal(BigDecimal value) {
        return value != null ? String.format("%,.2f", value) : "0.00";
    }

    private String formatBigDecimal2(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }

        BigDecimal absValue = value.abs(); // Always positive for display
        String formatted = String.format("%,.2f", absValue);

        // If original value was negative, wrap in brackets
        return value.signum() < 0 ? "(" + formatted + ")" : formatted;
    }

    private void updateAmountFooter(Grid.Column<QuarterlyActuals> amountColumn,
            ListDataProvider<QuarterlyActuals> targetProvider) {
        BigDecimal total = targetProvider.getItems().stream()
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        amountColumn.setFooter("Total: " + formatBigDecimal(total));
    }

    public BigDecimal sumOfQtr(Set<QuarterlyActuals> actualsList) {
        if (actualsList == null || actualsList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return actualsList.stream()
                .filter(a -> a.getPeriod() != null) // Qtr1 = period 1
                .map(QuarterlyActuals::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
