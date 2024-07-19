package com.methaltech.application.views.procurementplan;

import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.UnitsBudget;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.DataDuplicationService;
import com.methaltech.application.data.bgtool.service.FundsourceService;
import com.methaltech.application.data.bgtool.service.ProcurementMethodService;
import com.methaltech.application.data.bgtool.service.ProcurementPlanService;
import com.methaltech.application.data.bgtool.service.ProcurementTypeService;
import com.methaltech.application.data.bgtool.service.UnitService;
import com.methaltech.application.data.bgtool.service.UnitsBudgetService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.ProcurementMethod;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import com.methaltech.application.data.entity.bgtool.ProcurementType;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.oldbgtool.service.DepartmentUnitService;
import com.methaltech.application.data.oldbgtool.service.UrcUserService;
import com.methaltech.application.data.oldbgtool.service.UserUnitsService;
import com.methaltech.application.data.livedata.service.UrcDeptSectionAnlDimService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.views.budgetReport.BudgetReportsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Procurement Plan")
@Route(value = "procurementplan", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "PROCUREMENT", "BLO", "HOD"})
@Uses(Icon.class)
public class ProcurementPlanView extends Div {

    private static final Random random = new Random();
    private static final char[] symbols;

    private Grid<ProcurementPlan> grid = new Grid<>(ProcurementPlan.class, false);
    private Grid<ProcurementPlan> grid2 = new Grid<>(ProcurementPlan.class, false);

    private Grid<BudgetItems> gridBudgetItems = new Grid<>(BudgetItems.class, false);
    private Grid<BudgetItems> gridBudgetItems2 = new Grid<>(BudgetItems.class, false);

    private TextField subject = new TextField("Subject of Procurement");
    private ComboBox<Currency> currency = new ComboBox("Currency");
    private ComboBox<Fundsource> fundsource = new ComboBox("Source of Funds");
    private ComboBox<ProcurementMethod> procurementMethod = new ComboBox("Procurement Method");
    private ComboBox<ProcurementType> contractType = new ComboBox("Contract Type");
    private NumberField cost = new NumberField("Cost");

    private TextField subject2 = new TextField("Subject of Procurement");
    private ComboBox<Currency> currency2 = new ComboBox("Currency");
    private ComboBox<Fundsource> fundsource2 = new ComboBox("Source of Funds");
    private ComboBox<ProcurementMethod> procurementMethod2 = new ComboBox("Procurement Method");
    private ComboBox<ProcurementType> contractType2 = new ComboBox("Contract Type");
    private NumberField cost2 = new NumberField("Cost");
    private ComboBox<Budget> budget2 = new ComboBox("Budget");
    private ComboBox<ProcClass> procClassCombo2 = new ComboBox("Procurement Class");

    private ComboBox<Budget> budget = new ComboBox("Budget");
    private ComboBox<ProcClass> procClassCombo = new ComboBox("Procurement Class");
    private Checkbox preq = new Checkbox("Prequalification");
    private Checkbox reserv = new Checkbox("Application of Reserve Scheme");
    DatePicker ReqInviofExpofInterestdate = new DatePicker("Bid Invitation Date");
    DatePicker ReqClosingOpeningdate = new DatePicker("Bid Opening Date");
    DatePicker ReqApprovalOfShortlist = new DatePicker("Evaluation Report Date");
    DatePicker ReqNotificationdate = new DatePicker("Award Notification Date");
    DatePicker InvitationofProposalsdate = new DatePicker("Invitation of Proposals Date");
    DatePicker SubmissionOpeningdate = new DatePicker("Submission / Opening Date");
    DatePicker Approvaloffinalevaluationreport = new DatePicker("Approval of Final Evaluation Report Date");
    DatePicker InvNotificationdate = new DatePicker("Notification Date");
    DatePicker contract_sign = new DatePicker("Contract Signing Date");
    DatePicker completion = new DatePicker("Completion Date");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<ProcurementPlan> binder;

    DatePicker bid_inv2 = new DatePicker("Bid Invitation Date");
    DatePicker ReqClosingOpeningdate2 = new DatePicker("Bid Opening Date");
    DatePicker Approvaloffinalevaluationreport2 = new DatePicker("Approval of Final Evaluation Report Date");

    DatePicker award_not2 = new DatePicker("Award Notification Date");
    DatePicker contract_sign2 = new DatePicker("Contract Signing Date");
    DatePicker completion2 = new DatePicker("Completion Date");
    private Checkbox preq2 = new Checkbox("Prequalification");
    private Checkbox reserv2 = new Checkbox("Application of Reserve Scheme");

    private final Button cancel2 = new Button("Cancel");
    private final Button save2 = new Button("Save");

    private final BeanValidationBinder<ProcurementPlan> binder2;

    private ProcurementPlan sampleProcurementPlan;
    private ProcurementPlan sampleProcurementPlan2;
    private Budget sampleBudget;
    private Budget sampleBudget2;
    private UnitsBudget sampleUnitsBudget;
    private AuthenticatedUser authenticatedUser;

    private final UserService samplePersonService;
    private final BudgetService sampleBudgetService;
    private final UnitService sampleUnitService;
    private final UnitsBudgetService sampleUnitsBudgetService;
    private final DataDuplicationService sampleDataDuplicationService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final UrcUserService urcUserService;
    private final DepartmentUnitService departmentUnitService;

    private final ProcurementPlanService ProcurementPlanService;
    private final ProcurementMethodService ProcurementMethodService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private final EmailValidator emailValidator;
    public MultiSelectComboBox<D_Unit> unitsList;
    private final UrcDeptSectionAnlDimService sampleSectionService;
    private final UserUnitsService UserUnitsService;
    private final BudgetItemsService budgetItemsService;
    private final CoaService coaService;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    Span span11 = new Span("");
    Span spanTotal = new Span("");
    Span spanT1 = new Span("TOTAL");
    Span spanT2 = new Span("TOTAL");
    Dialog dialogA = new Dialog();
    Dialog dialogB = new Dialog();

    Span span2 = new Span("");
    Span spanTotal2 = new Span("");
    Span spanT12 = new Span("TOTAL");
    Span spanT22 = new Span("TOTAL");
    Dialog dialogA2 = new Dialog();
    Dialog dialogB2 = new Dialog();
    private final CurrencyService sampleCurrencyService;
    private final FundsourceService sampleFundsourceService;
    private final ProcurementTypeService sampleProcurementTypeService;
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section = new MultiSelectComboBox<>("Cost Centres");
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section2 = new MultiSelectComboBox<>("Cost Centres");
    private MultiSelectComboBox<Fundsource> funds = new MultiSelectComboBox("Source of Funds");
    private MultiSelectComboBox<Fundsource> funds2 = new MultiSelectComboBox("Source of Funds");
    private User user;
    private Set<ProcurementPlan> selectedProcurementPlan = null;
    PersonContextMenuGrid2 contextMenu2=null;
    ProcuremetPlanGridContextMenu contextMenu=null;
    BudgetItemsGridContextMenu budgetItemsGridContextMenu=null;
    BudgetItemsGridContextMenu2 budgetItemsGridContextMenu2=null;

    @Autowired
    public ProcurementPlanView(UserService samplePersonService, EmailValidator emailValidator,
            BudgetService sampleBudgetService, UnitService sampleUnitService, UnitsBudgetService sampleUnitsBudgetService,
            UrcDeptSectionAnlDimService sampleSectionService, DataDuplicationService sampleDataDuplicationService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, AuthenticatedUser authenticatedUser,
            UrcUserService urcUserService, UserUnitsService UserUnitsService, DepartmentUnitService departmentUnitService,
            ProcurementPlanService ProcurementPlanService, BudgetItemsService budgetItemsService,
            CoaService coaService, CurrencyService sampleCurrencyService, FundsourceService sampleFundsourceService,
            ProcurementMethodService ProcurementMethodService, ProcurementTypeService sampleProcurementTypeService) {
        this.samplePersonService = samplePersonService;
        this.emailValidator = emailValidator;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleUnitService = sampleUnitService;
        this.sampleUnitsBudgetService = sampleUnitsBudgetService;
        this.sampleSectionService = sampleSectionService;
        this.sampleDataDuplicationService = sampleDataDuplicationService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.urcUserService = urcUserService;
        this.UserUnitsService = UserUnitsService;
        this.departmentUnitService = departmentUnitService;
        this.authenticatedUser = authenticatedUser;
        this.ProcurementPlanService = ProcurementPlanService;
        this.budgetItemsService = budgetItemsService;
        this.coaService = coaService;
        this.sampleCurrencyService = sampleCurrencyService;
        this.sampleFundsourceService = sampleFundsourceService;
        this.ProcurementMethodService = ProcurementMethodService;
        this.sampleProcurementTypeService = sampleProcurementTypeService;
        addClassNames("procurement-plan-view");
        this.setHeight("100%");
        spanTotal.getStyle().set("font-weight", "bold");
        span11.getStyle().set("font-weight", "bold");
        spanT1.getStyle().set("font-weight", "bold");
        spanT2.getStyle().set("font-weight", "bold");

        spanTotal2.getStyle().set("font-weight", "bold");
        span2.getStyle().set("font-weight", "bold");
        spanT12.getStyle().set("font-weight", "bold");
        spanT22.getStyle().set("font-weight", "bold");
        // Create UI
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setHeight("100%");
        splitLayout.setSplitterPosition(65);

        SplitLayout splitLayout2 = new SplitLayout();
        splitLayout2.setHeight("100%");
        splitLayout2.setSizeFull();
        splitLayout2.setSplitterPosition(65);
        Div div = new Div();
        div.setSizeFull();

        Div div2 = new Div();
        div2.setSizeFull();

        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        gridForBudgetItems();
        gridForBudgetItems2();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        createGridLayout2(splitLayout2);
        createEditorLayout2(splitLayout2);
        // add(createBudgetSelectLayout());
        div.add(createBudgetSelectLayout(), splitLayout);
        div2.add(createBudgetSelectLayout2(), splitLayout2);
        tabSheet.add("Consultancy", div);
        tabSheet.add("Supplies, Works,Non_Consultancy, Disposal", div2);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = samplePersonService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }

        funds.setItemLabelGenerator(Fundsource::getFundsource);
        funds2.setItemLabelGenerator(Fundsource::getFundsource);

        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) {
            comboBoxD_Section.setItems(sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims());
            comboBoxD_Section2.setItems(sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims());
        } else {
            comboBoxD_Section.setItems(user.getDeptsection());
            comboBoxD_Section2.setItems(user.getDeptsection());
        }

        comboBoxD_Section2.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);

        add(tabSheet);
        span11.getElement().getThemeList().add("badge success");

        contractType.setItems(sampleProcurementTypeService.getAllProcurementTypes());
        contractType2.setItems(sampleProcurementTypeService.getAllProcurementTypes());
        contractType.setItemLabelGenerator(ProcurementType::getProcuremntType);
        contractType2.setItemLabelGenerator(ProcurementType::getProcuremntType);
        procurementMethod2.setItems(ProcurementMethodService.getAllProcurementMethods());
        procurementMethod2.setItemLabelGenerator(ProcurementMethod::getProcuremntMethod);

        currency.setItems(query -> sampleCurrencyService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        // Configure Grid
        procClassCombo.setItems(ProcClass.Consultancy);
        procClassCombo.setValue(ProcClass.Consultancy);
        grid.addColumn("subject").setWidth("300px").setFrozen(true);
        grid.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(urcActivity.getCoa().getCode());
            span.getElement().getThemeList().add("badge success");

            return span;
        })).setHeader("Code").setFlexGrow(0).setWidth("70px").setFooter(spanT22).setFrozen(true).setTooltipGenerator(urcActivity -> "Code Name: " + urcActivity.getCoa().getName());
        grid.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");
            if (urcActivity.getCurrency().getData().getCurrencyShort() != null) {
                span = new Span(urcActivity.getCurrency().getData().getCurrencyShort());
            }

            return span;
        })).setHeader("Currency").setFlexGrow(0).setWidth("100px");
        grid.getElement().getStyle().set("--vaadin-grid-cell-background", "#cccccc");

        grid.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");

            if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {

                if (funds.isEmpty()) {
                    span = new Span(decimalFormat.format(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa())) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa(), funds.getSelectedItems()))) + "");
                }

            } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {

                if (funds.isEmpty()) {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa(), user.getDeptsection()))) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa(), user.getDeptsection(), funds.getSelectedItems()))) + "");
                }
            } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                if (funds.isEmpty()) {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems()))) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems()))) + "");
                }
            } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                if (funds.isEmpty()) {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems()))) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(urcActivity.getBudget(), procClassCombo.getValue(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems()))) + "");
                }

            }
            return span;
        })).setHeader("Estimate Cost").setFrozen(true).setWidth("150px").setFooter(span11);

        // grid.addColumn("fundsource").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");
            if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                if (funds.isEmpty()) {
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacodeF(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), funds.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }

            } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), user.getDeptsection());
                if (funds.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), user.getDeptsection(), funds.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems());
                if (funds.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems());
                if (funds.isEmpty()) {
                    // fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }

            }

            return span;
        })).setHeader("Fund Source").setFlexGrow(0).setWidth("100px");
        //grid.addColumn("procurementMethod").setWidth("150px").setHeader("Procurement method");
        grid.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");
            if (urcActivity.getProcurementMethod() != null) {
                span = new Span(urcActivity.getProcurementMethod().getProcuremntMethod());
            }

            return span;
        })).setHeader("Procurement method").setFlexGrow(0).setWidth("150px");
        grid.addColumn("reserve").setWidth("100px").setHeader("Apply Reservation Scheme (Yes/No)");
        grid.addColumn("procurementtype").setWidth("100px").setHeader("Contract Type");

        grid.addColumn("reqInviofExpofInterestdate").setWidth("100px").setHeader("Invitation of Expressions of Interest Date");
        grid.addColumn("reqClosingOpeningdate").setWidth("100px").setHeader("Closing - Opening Date");
        grid.addColumn("reqApprovalOfShortlist").setWidth("100px").setHeader("Approval of Shortlist");
        grid.addColumn("reqNotificationdate").setWidth("100px").setHeader("Notification Date");

        grid.addColumn("invitationofProposalsdate").setWidth("100px").setHeader("Invitation of Proposals Date");
        grid.addColumn("submissionOpeningdate").setWidth("100px").setHeader("Submission/ Opening Date");

        grid.addColumn("approvaloffinalevaluationreport").setWidth("100px").setHeader("Approval of Final Evaluation Report Date");
        grid.addColumn("invNotificationdate").setWidth("100px").setHeader("Notification Date");

        grid.addColumn("contractsigningdate").setWidth("100px").setHeader("Contract signing date");
        grid.addColumn("bcompletion").setWidth("100px").setHeader("Completion date");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addAttachListener(event -> {

        });
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) {
            contextMenu = new ProcuremetPlanGridContextMenu(grid);
        }

        //refreshgridProcurementPlan(procClassCombo.getValue());
        procClassCombo.addValueChangeListener(e -> {
            if (sampleBudget != null) {
                refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                tabSheet.getSelectedTab().setLabel(e.getValue().name());
            }

        });

        budget.addValueChangeListener(e -> {
            if(!e.getValue().isActive()){
                
                contextMenu.setEnabled(false);
                budgetItemsGridContextMenu.setEnabled(false);
            }
            if (!procClassCombo.isEmpty()) {
                refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                tabSheet.getSelectedTab().setLabel(procClassCombo.getValue().name());
            }

        });
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // when a row is selected or deselected, populate form
        grid.asMultiSelect().addValueChangeListener(event -> {
            gridBudgetItems.deselectAll();
            if (event.getValue() != null) {
                setgridBudgetItemsData(event.getValue());

                UI.getCurrent().navigate(ProcurementPlanView.class);
            } else {
                UI.getCurrent().navigate(ProcurementPlanView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(ProcurementPlan.class);
        //binder.bindInstanceFields(this);
        // binder.forField(subject).bind("subject");
        binder.forField(procurementMethod).bind("procurementMethod");
        // binder.forField(contractType).bind("procurementtype");
        //binder.forField(reserv).bind("reserve");
        //binder.forField(ReqInviofExpofInterestdate).bind("ReqInviofExpofInterestdate");
        //binder.forField(ReqClosingOpeningdate).bind("ReqClosingOpeningdate");
        //binder.forField(ReqApprovalOfShortlist).bind("subject");
        //binder.forField(ReqNotificationdate).bind("ReqNotificationdate");

        //binder.forField(InvitationofProposalsdate).bind("InvitationofProposalsdate");
        //binder.forField(SubmissionOpeningdate).bind("SubmissionOpeningdate");
        //binder.forField(Approvaloffinalevaluationreport).bind("Approvaloffinalevaluationreport");
        // binder.forField(InvNotificationdate).bind("InvNotificationdate");
        //binder.forField(contract_sign).bind("contract_sign");
        //binder.forField(completion).bind("completion");
        // Bind form fields to User properties
        //-----------------------------------------------
        span2.getElement().getThemeList().add("badge success");

        currency2.setItems(query -> sampleCurrencyService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        // Configure Grid
        procClassCombo2.setItems(ProcClass.Supplies, ProcClass.Works, ProcClass.Non_Consultancy, ProcClass.Disposal);
        grid2.addColumn("subject").setWidth("300px").setFrozen(true);
        grid2.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(urcActivity.getCoa().getCode());
            span.getElement().getThemeList().add("badge success");

            return span;
        })).setHeader("Code").setFlexGrow(0).setWidth("70px").setFooter(spanT1).setFrozen(true).setTooltipGenerator(urcActivity -> "Code Name: " + urcActivity.getCoa().getName());

        //grid2.addColumn("currency").setWidth("100px");
        grid2.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");
            if (urcActivity.getCurrency() != null) {
                span = new Span(urcActivity.getCurrency().getData().getCurrencyShort());
            }

            return span;
        })).setHeader("Currency").setFlexGrow(0).setWidth("100px");

        grid2.getElement().getStyle().set("--vaadin-grid-cell-background", "#cccccc");
        grid2.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");

            if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {

                if (funds2.isEmpty()) {
                    span = new Span(decimalFormat.format(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa())) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa(), funds2.getSelectedItems()))) + "");
                }

            } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {

                if (funds2.isEmpty()) {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa(), user.getDeptsection()))) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa(), user.getDeptsection(), funds2.getSelectedItems()))) + "");
                }
            } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                if (funds2.isEmpty()) {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems()))) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems()))) + "");
                }
            } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                if (funds2.isEmpty()) {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems()))) + "");
                } else {
                    span = new Span(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(urcActivity.getBudget(), procClassCombo2.getValue(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems()))) + "");
                }

            }
            return span;
        })).setHeader("Estimate Cost").setFrozen(true).setWidth("150px").setFooter(span2);

        // grid.addColumn("fundsource").setAutoWidth(true);
        grid2.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");
            if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                if (funds2.isEmpty()) {
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacodeF(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), funds2.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }

            } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), user.getDeptsection());
                if (funds2.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), user.getDeptsection(), funds2.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems());
                if (funds2.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems());
                if (funds2.isEmpty()) {
                    // fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems());
                    span = new Span(getCommaSeparatedFundsourceList(fundString));
                }

            }

            return span;
        })).setHeader("Fund Source").setFlexGrow(0).setWidth("100px");
        grid2.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");
            if (urcActivity.getProcurementMethod() != null) {
                span = new Span(urcActivity.getProcurementMethod().getProcuremntMethod());
            }

            return span;
        })).setHeader("Procurement method").setFlexGrow(0).setWidth("150px");
        grid2.addColumn("procurementtype").setWidth("100px").setHeader("Procurement Type");
        grid2.addColumn("prequal").setWidth("100px").setHeader("PRE- QUALIFICATION (Yes or No)");
        grid2.addColumn("reserve").setWidth("100px").setHeader("Apply Reservation Scheme (Yes/No)");
        grid2.addColumn("binvite").setWidth("100px").setHeader("Bid Invitation Date");
        grid2.addColumn("reqClosingOpeningdate").setWidth("100px").setHeader("Bid closing/ Opening Date");
        grid2.addColumn("approvaloffinalevaluationreport").setWidth("100px").setHeader("Approval Evaluation Report Date");
        grid2.addColumn("reqNotificationdate").setWidth("100px").setHeader("Award Notification Date");
        grid2.addColumn("contractsigningdate").setWidth("100px").setHeader("Contract Signing Date");
        grid2.addColumn("bcompletion").setWidth("100px").setHeader("Completion Date");

        grid2.setSelectionMode(Grid.SelectionMode.MULTI);
        grid2.addAttachListener(event -> {

            List<Column<ProcurementPlan>> columns = grid2.getColumns();
            for (Column<ProcurementPlan> column : columns) {
                ProcClass selectedProcClass = procClassCombo2.getValue();
                if (selectedProcClass != null && selectedProcClass.equals(ProcClass.Consultancy)) {
                    Notificationwarning("Atached listener");
                    column.setVisible(false);
                }
            }
        });
        //grid.addColumn("roles").setAutoWidth(true);
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) {
            contextMenu2 = new PersonContextMenuGrid2(grid2);
            
            
        }

        refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
        procClassCombo2.addValueChangeListener(e -> {
            //refreshgridProcurementPlan2(e.getValue(), null);
            refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
            //tabSheet.getSelectedTab().setLabel(e.getValue().name());
        });
        grid2.setHeight("100%");
        grid2.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // when a row is selected or deselected, populate form
        grid2.asMultiSelect().addValueChangeListener(event -> {
            gridBudgetItems2.deselectAll();
            if (event.getValue() != null) {
                gridBudgetItems2.deselectAll();
                selectedProcurementPlan = event.getValue();
                setgridBudgetItemsData2(event.getValue());
                UI.getCurrent().navigate(ProcurementPlanView.class);
            } else {
                UI.getCurrent().navigate(ProcurementPlanView.class);
            }
        });

        // Configure Form
        binder2 = new BeanValidationBinder<>(ProcurementPlan.class);

        //binder.bindInstanceFields(this);
        // Bind form fields to User properties
        /*        cancel2.addClickListener(e -> {
        clearForm();
        
        });*/
    }

    private Div createBudgetSelectLayout() {
        funds.onEnabledStateChanged(true);
        budget.setItemLabelGenerator(Budget::getFinancialYear);

        fundsource.setItemLabelGenerator(Fundsource::getFundsource);
        currency.setItemLabelGenerator(item -> item.getData().getCurrencyShort());
        //procClassCombo.setItemLabelGenerator(ProcClass::getFinancialYear);c
        budget.addValueChangeListener(e -> {
            fundsource.setItems(sampleFundsourceService.findFundsourcesByBudget(e.getValue()));
            funds.setItems(sampleFundsourceService.findFundsourcesByBudget(e.getValue()));
            //refreshgridProcurementPlan(procClassCombo.getValue(), null);
            refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());

        });

        funds.addValueChangeListener(y -> {
            refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());

        });
        Button refresh = new Button("Refresh");
        Button downloadProcurementReportButton = new Button("Download By Procurement Category", new Icon(VaadinIcon.DOWNLOAD));
        refresh.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button updateButton = new Button("Update Procurement Plan");
        updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        downloadProcurementReportButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        downloadProcurementReportButton.setAriaLabel("Download");
        downloadProcurementReportButton.setEnabled(true);

        Button downloadProcurementReportButtonSheets = new Button("Download Procurement Plan", new Icon(VaadinIcon.DOWNLOAD));
        downloadProcurementReportButtonSheets.addThemeVariants(ButtonVariant.LUMO_ICON);
        downloadProcurementReportButtonSheets.setAriaLabel("Download");
        downloadProcurementReportButtonSheets.setEnabled(true);

        Div div = new Div();
        div.add(budget, procClassCombo, comboBoxD_Section, funds, downloadProcurementReportButton, downloadProcurementReportButtonSheets);
        // Set margin-right style to create space between components
        budget.getStyle().set("marginRight", "10px");
        procClassCombo.getStyle().set("marginRight", "10px");
        comboBoxD_Section.getStyle().set("marginRight", "10px");
        funds.getStyle().set("marginRight", "10px");
        downloadProcurementReportButton.addClickListener(e -> {
            if(!budget.isEmpty()&&!procClassCombo.isEmpty()){
               exportAndDownloadExcelWorkplan(procClassCombo.getValue()); 
            }else{
                Notificationwarning("Select a Financial Year && procurement Class");
            }
            
        });
        downloadProcurementReportButtonSheets.addClickListener(e -> {
            if (!budget.isEmpty()&&!procClassCombo.isEmpty()) {
                budget2.setValue(budget.getValue());

                exportAndDownloadExcelProcurementPlanSheets();
            } else {
                Notificationwarning("Select a Financial Year && procurement Class");
            }

        });
        updateButton.addClickListener(f -> {
            if (!budget.isEmpty()) {
                updateProcurementPlan(budget.getValue());
            }

        });

        refresh.addClickListener(e -> {
            List<ProcClass> proc = new ArrayList();
            proc.add(ProcClass.Supplies);
            proc.add(ProcClass.Works);
            proc.add(ProcClass.Consultancy);
            proc.add(ProcClass.Non_Consultancy);
            proc.add(ProcClass.Disposal);

            if (!budget.isEmpty()) {
                //List<BudgetItems> listBudgetItems=budgetItemsService.
                ProcurementPlanService.deleteProcurementPlanByBudget(budget.getValue());
                List<ProcurementPlan> listProc = ProcurementPlanService.findProcurementPlansByBudget(budget.getValue());

                if (listProc == null || listProc.isEmpty()) {
                    List<COA> listCOA = coaService.findByBudgetAndProcclassIn(budget.getValue(), proc);
                    for (COA p : listCOA) {

                        List<BudgetItems> listBudgetItems = budgetItemsService.getBudgetItemsByBudgetAndCoacode(budget.getValue(), p);
                        Set<BudgetItems> budgetItemsSet = new HashSet<>();
                        Fundsource fundsourcex = null;
                        for (BudgetItems x : listBudgetItems) {

                            budgetItemsSet.add(x);
                            fundsourcex = x.getFundsource();

                        }
                        ProcurementPlan pnew = new ProcurementPlan();
                        pnew.setBudget(budget.getValue());

                        // pnew.setProcPlanBudgetItems(budgetItemsSet);
                        // pnew.setCost(generatesumofMonthsFromList(budgetItemsSet.stream().toList()));
                        BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(p.getBudget(), p.getProcclass(), p);
                        // pnew.setCost(x.getCost());
                        Currency cur = sampleCurrencyService.findCurrenciesByCurrencyShortAndBudget("UGX", budget.getValue());
                        pnew.setCurrency(cur);
                        Set<Fundsource> fundsourceSet = new HashSet<>();
                        fundsourceSet.add(fundsourcex);
                        // pnew.setFundsource(fundsourceSet);
                        pnew.setSubject(p.getName());
                        pnew.setProcClass(p.getProcclass());
                        pnew.setCoa(p);

                        pnew.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(pnew.getProcClass(), tDecimal));

                        if (tDecimal.doubleValue() > 0) {

                            ProcurementPlanService.save(pnew);
                        }

                    }

                } else {

                }
                refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
            }

        });

        budget.setItems(query -> sampleBudgetService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        budget.addValueChangeListener(ev -> {

            refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
            sampleBudget = ev.getValue();
        });
        comboBoxD_Section.addValueChangeListener(v -> {
            refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
        });
        return div;
    }

    private Div createBudgetSelectLayout2() {
        funds2.onEnabledStateChanged(true);
        budget2 = new ComboBox("Select Budget");
        budget2.setItemLabelGenerator(Budget::getFinancialYear);

        fundsource2.setItemLabelGenerator(Fundsource::getFundsource);
        currency2.setItemLabelGenerator(item -> item.getData().getCurrencyShort());
        //procClassCombo.setItemLabelGenerator(ProcClass::getFinancialYear);c
        budget2.addValueChangeListener(e -> {

            if(!e.getValue().isActive()){
                budgetItemsGridContextMenu2.setEnabled(false);
                contextMenu2.setEnabled(false);
                
            }            
            fundsource2.setItems(sampleFundsourceService.findFundsourcesByBudget(e.getValue()));
            funds2.setItems(sampleFundsourceService.findFundsourcesByBudget(e.getValue()));
        });

        funds2.addValueChangeListener(y -> {

            refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());

        });

        Button refresh = new Button("Refresh");
        Button downloadProcurementReportButton = new Button(new Icon(VaadinIcon.DOWNLOAD));
        refresh.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        downloadProcurementReportButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        downloadProcurementReportButton.setAriaLabel("Download");
        downloadProcurementReportButton.setEnabled(true);

        Div div = new Div();
        div.add(budget2, procClassCombo2, comboBoxD_Section2, funds2, downloadProcurementReportButton);
// Set margin-right style to create space between components
        budget2.getStyle().set("marginRight", "10px");
        procClassCombo2.getStyle().set("marginRight", "10px");
        comboBoxD_Section2.getStyle().set("marginRight", "10px");
        funds2.getStyle().set("marginRight", "10px");
        downloadProcurementReportButton.addClickListener(e -> {
            exportAndDownloadExcelWorkplan(procClassCombo2.getValue());
        });

        budget2.setItems(query -> sampleBudgetService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        budget2.addValueChangeListener(ev -> {

            refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
            sampleBudget2 = ev.getValue();
        });

        comboBoxD_Section2.addValueChangeListener(v -> {
            refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
        });
        return div;
    }

    private void refreshgridProcurementPlan(ProcClass p, Set<UrcDeptSectionAnlDimbgt> depts) {
        //Notification.show("Refreshed");f

        if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            List<ProcurementPlan> plan = new ArrayList<>();
            List<ProcurementPlan> plan1 = new ArrayList<>();
            if (!funds.isEmpty()) {
                plan1 = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), p);
                plan = ProcurementPlanService.findByBudgetAndProcClassAndFundsource(budget.getValue(), p, plan1, funds.getValue());
            } else {
                plan = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), p);
            }
            grid.setItems(plan);
            if (plan != null) {
                span11.setText(decimalFormat.format(generatesumofProc(plan)) + "/= ");
            }

            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, user.getDeptsection());
            if (!funds.isEmpty()) {
                plan = ProcurementPlanService.findByBudgetAndProcClassAndSectsAndFundsource(budget.getValue(), p, plan, funds.getValue(), user.getDeptsection());
            } else {
                plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, user.getDeptsection());
            }
            grid.setItems(plan);
            if (plan != null) {
                span11.setText(decimalFormat.format(generatesumofProc(plan)) + "/= ");
            }

            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), procClassCombo.getValue());
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();
            if (plan != null) {
                currentGridplan = grid.getGenericDataView().getItems().toList();
                for (ProcurementPlan k : plan) {

                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), depts);
                    if (funds.isEmpty()) {

                    } else {

                        listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(k.getBudget(), k.getProcClass(), k.getCoa(), depts, funds.getValue());
                    }
                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            grid.setItems(avGridplan);

            if (avGridplan != null) {
                span11.setText(decimalFormat.format(generatesumofProc(avGridplan)) + "/= ");
            }

            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, user.getDeptsection());
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();
            if (plan != null) {
                currentGridplan = grid.getGenericDataView().getItems().toList();
                for (ProcurementPlan k : plan) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), depts);

                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            grid.setItems(avGridplan);

            if (avGridplan != null) {
                span11.setText(decimalFormat.format(generatesumofProc(avGridplan)) + "/= ");
            }

            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        }
    }

    private void refreshgridProcurementPlan2(ProcClass p, Set<UrcDeptSectionAnlDimbgt> depts) {

        // grid2.deselectAll();
        if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            List<ProcurementPlan> plan = new ArrayList<>();
            List<ProcurementPlan> plan1 = new ArrayList<>();
            if (!funds2.isEmpty()) {
                plan1 = ProcurementPlanService.findByBudgetAndProcClass(budget2.getValue(), p);
                plan = ProcurementPlanService.findByBudgetAndProcClassAndFundsource(budget2.getValue(), p, plan1, funds2.getValue());
            } else {
                plan = ProcurementPlanService.findByBudgetAndProcClass(budget2.getValue(), p);
            }
            grid2.setItems(plan);
            if (plan != null) {
                span2.setText(decimalFormat.format(generatesumofProc(plan)) + "/= ");
            }

        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget2.getValue(), p, user.getDeptsection());
            if (!funds.isEmpty()) {
                plan = ProcurementPlanService.findByBudgetAndProcClassAndSectsAndFundsource(budget2.getValue(), p, plan, funds2.getValue(), user.getDeptsection());
            } else {
                plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget2.getValue(), p, user.getDeptsection());
            }
            grid2.setItems(plan);
            if (plan != null) {
                span2.setText(decimalFormat.format(generatesumofProc(plan)) + "/= ");
            }
            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClass(budget2.getValue(), procClassCombo2.getValue());
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();
            if (plan != null) {
                currentGridplan = grid2.getGenericDataView().getItems().toList();
                for (ProcurementPlan k : plan) {

                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), depts);
                    if (funds2.isEmpty()) {

                    } else {

                        listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(k.getBudget(), k.getProcClass(), k.getCoa(), depts, funds2.getValue());
                    }
                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            grid2.setItems(avGridplan);

            if (avGridplan != null) {
                span2.setText(decimalFormat.format(generatesumofProc(avGridplan)) + "/= ");
            }

        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget2.getValue(), p, user.getDeptsection());
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();
            if (plan != null) {
                currentGridplan = grid2.getGenericDataView().getItems().toList();
                for (ProcurementPlan k : plan) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), depts);

                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            grid2.setItems(avGridplan);

            if (avGridplan != null) {
                span2.setText(decimalFormat.format(generatesumofProc(avGridplan)) + "/= ");
            }

        }

    }

    private void createEditorLayout(SplitLayout splitLayout) {
        VerticalLayout lay = new VerticalLayout();

        //splitLayout.addToSecondary(gridBudgetItems);
        splitLayout.addToSecondary(gridBudgetItems);
    }

    private void createEditorLayout2(SplitLayout splitLayout) {
        VerticalLayout lay = new VerticalLayout();

        //splitLayout.addToSecondary(gridBudgetItems);
        splitLayout.addToSecondary(gridBudgetItems2);
    }

    private Dialog editProPlan(ProcurementPlan ProcurementPlanSelected, Grid<BudgetItems> gridPlan, Grid<ProcurementPlan> target) {

        Dialog dia = new Dialog();
        dia.setModal(false);
        dia.setDraggable(true);
        dia.setResizable(true);
        dia.setWidth("800px");
        Div editorLayoutDiv = new Div();

        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> {
                    grid.deselectAll();
                    dia.close();
                });
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dia.getHeader().add(closeButton);
        editorLayoutDiv.setClassName("user-editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("user-editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        subject.setRequired(true);
        subject.setRequiredIndicatorVisible(true);
        //subject.setErrorMessage("Subject is Required");
        subject.setClearButtonVisible(true);
        subject.setEnabled(false);

        /*        currency.setRequired(true);
        currency.setRequiredIndicatorVisible(true);
        currency.setErrorMessage("Currency is Required");
        currency.setClearButtonVisible(true);*/
        cost.setRequired(true);
        cost.setRequiredIndicatorVisible(true);
        //cost.setErrorMessage("Fund Source is Required");
        cost.setClearButtonVisible(true);

        /*        fundsource.setRequired(true);
        fundsource.setRequiredIndicatorVisible(true);
        fundsource.setErrorMessage("Fund Source is Required");
        fundsource.setClearButtonVisible(true);*/
        procurementMethod.setClearButtonVisible(true);
        procurementMethod.setRequired(true);
        procurementMethod.setRequiredIndicatorVisible(true);
        // procMethod.setErrorMessage("Procurement Method is Required");

        contractType.setRequired(true);
        contractType.setRequiredIndicatorVisible(true);
        //contractType.setErrorMessage("Contact Type is Required");

        // Populate combo with roles
        procurementMethod.setItems(ProcurementMethodService.getAllProcurementMethods());
        procurementMethod.setItemLabelGenerator(ProcurementMethod::getProcuremntMethod);
        Hr hr1 = new Hr();
        hr1.setWidthFull();
        Hr hr2 = new Hr();
        hr2.setWidthFull();
        Hr hr3 = new Hr();
        hr3.setWidthFull();

        Html html1 = new Html("<b>Request for Expression of Interest</b>");
        Html html2 = new Html("<b>Invitation of proposals and approval for award</b>");
        formLayout.add(subject,
                procurementMethod,
                reserv,
                contractType,
                new Html("<br>"),
                html1,
                hr1,
                ReqInviofExpofInterestdate,
                ReqClosingOpeningdate,
                ReqApprovalOfShortlist,
                ReqNotificationdate,
                html2,
                hr2,
                InvitationofProposalsdate,
                SubmissionOpeningdate,
                Approvaloffinalevaluationreport,
                InvNotificationdate,
                hr3,
                contract_sign,
                completion);
        formLayout.setColspan(hr2, 3);
        formLayout.setColspan(hr1, 3);
        formLayout.setColspan(hr3, 3);
        formLayout.setColspan(html1, 3);
        formLayout.setColspan(html2, 3);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3)
        );
        HorizontalLayout lay = new HorizontalLayout();
        HorizontalLayout lay2 = new HorizontalLayout();
        gridPlan = new Grid<>(BudgetItems.class, false);
        //Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(BudgetItems::getJul).setHeader("Jul").setWidth("30px");
        Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getJul()) + "");
            span.setSizeFull();
            if (urcActivity.getJul() != null && !urcActivity.getJul().equals(BigDecimal.ZERO)) {

                if (urcActivity.getJul().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Jul").setWidth("30px");
        // Set the background color based on the value using CSS classes
        julColumn.setClassNameGenerator(item -> {
            if (item.getJul() != null && item.getJul().doubleValue() > 0) {
                return "positive-value";
            } else {
                return "negative-value";
            }
        });
        //Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(BudgetItems::getAug).setHeader("Aug").setWidth("30px");
        Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getAug()) + "");
            span.setSizeFull();
            if (urcActivity.getAug() != null && !urcActivity.getAug().equals(BigDecimal.ZERO)) {

                if (urcActivity.getAug().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Aug").setWidth("30px");
        Grid.Column<BudgetItems> sepColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getSep()) + "");
            span.setSizeFull();
            if (urcActivity.getSep() != null && !urcActivity.getSep().equals(BigDecimal.ZERO)) {

                if (urcActivity.getSep().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Sep").setWidth("30px");
        Grid.Column<BudgetItems> octColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getOct()) + "");
            span.setSizeFull();
            if (urcActivity.getOct() != null && !urcActivity.getOct().equals(BigDecimal.ZERO)) {

                if (urcActivity.getOct().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Oct").setWidth("30px");
        Grid.Column<BudgetItems> novColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getNov()) + "");
            span.setSizeFull();
            if (urcActivity.getNov() != null && !urcActivity.getNov().equals(BigDecimal.ZERO)) {

                if (urcActivity.getNov().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Nov").setWidth("30px");
        Grid.Column<BudgetItems> decColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getDec()) + "");
            span.setSizeFull();
            if (urcActivity.getDec() != null && !urcActivity.getDec().equals(BigDecimal.ZERO)) {

                if (urcActivity.getDec().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Dec").setWidth("30px");
        Grid.Column<BudgetItems> janColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getJan()) + "");
            span.setSizeFull();
            if (urcActivity.getJan() != null && !urcActivity.getJan().equals(BigDecimal.ZERO)) {

                if (urcActivity.getJan().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Jan").setWidth("30px");
        Grid.Column<BudgetItems> febColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getFeb()) + "");
            span.setSizeFull();
            if (urcActivity.getFeb() != null && !urcActivity.getFeb().equals(BigDecimal.ZERO)) {

                if (urcActivity.getFeb().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Feb").setWidth("30px");
        Grid.Column<BudgetItems> marColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getMar()) + "");
            span.setSizeFull();
            if (urcActivity.getMar() != null && !urcActivity.getMar().equals(BigDecimal.ZERO)) {

                if (urcActivity.getMar().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Mar").setWidth("30px");
        Grid.Column<BudgetItems> aprColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getApr()) + "");
            span.setSizeFull();
            if (urcActivity.getApr() != null && !urcActivity.getApr().equals(BigDecimal.ZERO)) {

                if (urcActivity.getApr().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Apr").setWidth("30px");
        Grid.Column<BudgetItems> mayColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getMay()) + "");
            span.setSizeFull();
            if (urcActivity.getMay() != null && !urcActivity.getMay().equals(BigDecimal.ZERO)) {

                if (urcActivity.getMay().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("May").setWidth("30px");
        Grid.Column<BudgetItems> junColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getJun()) + "");
            span.setSizeFull();
            if (urcActivity.getJun() != null && !urcActivity.getJun().equals(BigDecimal.ZERO)) {

                if (urcActivity.getJun().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Jun").setWidth("30px");

        HeaderRow headerRow = gridPlan.prependHeaderRow();
        headerRow.join(julColumn, augColumn, sepColumn).setText("QTR 1");
        headerRow.join(octColumn, novColumn, decColumn).setText("QTR 2");
        headerRow.join(janColumn, febColumn, marColumn).setText("QTR 3");
        headerRow.join(aprColumn, mayColumn, junColumn).setText("QTR 4");
        gridPlan.setHeight("100px");
        gridPlan.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
        List<BudgetItems> listBudgetItems = new ArrayList<>();
        Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
        ProcurementPlan mm = p.stream().toList().get(0);
        for (ProcurementPlan pp : p) {

            listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));

        }
        gridPlan.setItems(generatesumofMonthly(listBudgetItems));
        lay.add(gridPlan);
        lay.setWidthFull();
        editorDiv.add(formLayout, lay, lay2);

        formLayout.setColspan(lay, 3);
        formLayout.setColspan(lay2, 3);
        editorDiv.add(formLayout);
        Footer buttonLayout = new Footer();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(save, cancel);
        Set<ProcurementPlan> ProcurementPlanSelectedSet = grid.getSelectedItems();
        ProcurementPlanSelected = ProcurementPlanSelectedSet.stream().toList().get(0);
        if (ProcurementPlanSelected != null) {
            //setProcurementPlan(ProcurementPlanSelected);
            populateForm(ProcurementPlanSelected);
        }
        save.addClickListener(e -> {

            CharSequence editorTextFields = validEditorTextFields();

            if (editorTextFields != null && editorTextFields.length() > 0) {
                //Notification.show("Fill in the Empty Field (s)");
                Notificationwarning(" Either " + validEditorTextFields().toString() + "Or Select Budget");
                // Notification not = Notification.show("Fill in the Empty Field (s).");
                // not.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                Set<ProcurementPlan> ProcurementPlanSelectedSet2 = grid.getSelectedItems();
                ProcurementPlan ProcurementPlanSelected2 = ProcurementPlanSelectedSet2.stream().toList().get(0);
                ProcurementPlanSelected2.setSubject(subject.getValue());
                ProcurementPlanSelected2.setProcurementMethod(procurementMethod.getValue());
                ProcurementPlanSelected2.setProcurementtype(contractType.getValue());
                ProcurementPlanSelected2.setReserve(reserv.getValue());
                ProcurementPlanSelected2.setReqInviofExpofInterestdate(ReqInviofExpofInterestdate.getValue());
                ProcurementPlanSelected2.setReqClosingOpeningdate(ReqClosingOpeningdate.getValue());
                ProcurementPlanSelected2.setReqApprovalOfShortlist(ReqApprovalOfShortlist.getValue());
                ProcurementPlanSelected2.setReqNotificationdate(ReqNotificationdate.getValue());
                ProcurementPlanSelected2.setInvitationofProposalsdate(InvitationofProposalsdate.getValue());
                ProcurementPlanSelected2.setSubmissionOpeningdate(SubmissionOpeningdate.getValue());
                ProcurementPlanSelected2.setApprovaloffinalevaluationreport(Approvaloffinalevaluationreport.getValue());
                ProcurementPlanSelected2.setInvNotificationdate(InvNotificationdate.getValue());
                ProcurementPlanSelected2.setContractsigningdate(contract_sign.getValue());
                ProcurementPlanSelected2.setBcompletion(completion.getValue());
                ProcurementPlanService.save(ProcurementPlanSelected2);
                refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());

                UI.getCurrent().navigate(ProcurementPlanView.class);

            }
            dia.close();

        });
        dia.add(editorLayoutDiv, buttonLayout);
        cancel.addClickListener(er -> {
            grid.deselectAll();
            dia.close();
        });
        dia.open();
        return dia;
    }

    private Dialog editProPlan2(Grid<BudgetItems> gridPlan, Grid<ProcurementPlan> target) {

        Dialog dia = new Dialog();
        dia.setModal(false);
        dia.setDraggable(true);
        dia.setResizable(true);
        dia.setWidth("800px");
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("user-editor-layout");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> {
                    grid2.deselectAll();
                    dia.close();
                });
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dia.getHeader().add(closeButton);
        Div editorDiv = new Div();
        editorDiv.setClassName("user-editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        subject2.setRequired(true);
        subject2.setRequiredIndicatorVisible(true);
        //subject.setErrorMessage("Subject is Required");
        subject2.setClearButtonVisible(true);
        subject2.setEnabled(false);

        procurementMethod2.setClearButtonVisible(true);
        procurementMethod2.setRequired(true);
        procurementMethod2.setRequiredIndicatorVisible(true);
        //procMethod.setErrorMessage("Procurement Method is Required");

        contractType2.setRequired(true);
        contractType2.setRequiredIndicatorVisible(true);
        //contractType.setErrorMessage("Contact Type is Required");
        Hr hr1 = new Hr();
        hr1.setWidthFull();
        Hr hr2 = new Hr();
        hr2.setWidthFull();
        Html html1 = new Html("<b>INVITATION AND AWARD OF BIDS</b>");
        formLayout.add(subject2,
                procurementMethod2,
                contractType2,
                preq2,
                reserv2,
                hr1,
                html1,
                bid_inv2,
                ReqClosingOpeningdate,
                Approvaloffinalevaluationreport2,
                hr2,
                award_not2, contract_sign2,
                completion2);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3)
        );

        formLayout.setColspan(hr1, 3);
        formLayout.setColspan(hr2, 3);
        formLayout.setColspan(html1, 3);
        editorDiv.add(formLayout);

        HorizontalLayout lay = new HorizontalLayout();
        HorizontalLayout lay2 = new HorizontalLayout();
        gridPlan = new Grid<>(BudgetItems.class, false);
        //Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(BudgetItems::getJul).setHeader("Jul").setWidth("30px");
        Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getJul()) + "");
            span.setSizeFull();
            if (urcActivity.getJul() != null && !urcActivity.getJul().equals(BigDecimal.ZERO)) {

                if (urcActivity.getJul().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Jul").setWidth("30px");
        // Set the background color based on the value using CSS classes
        julColumn.setClassNameGenerator(item -> {
            if (item.getJul() != null && item.getJul().doubleValue() > 0) {
                return "positive-value";
            } else {
                return "negative-value";
            }
        });
        //Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(BudgetItems::getAug).setHeader("Aug").setWidth("30px");
        Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getAug()) + "");
            span.setSizeFull();
            if (urcActivity.getAug() != null && !urcActivity.getAug().equals(BigDecimal.ZERO)) {

                if (urcActivity.getAug().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Aug").setWidth("30px");
        Grid.Column<BudgetItems> sepColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getSep()) + "");
            span.setSizeFull();
            if (urcActivity.getSep() != null && !urcActivity.getSep().equals(BigDecimal.ZERO)) {

                if (urcActivity.getSep().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Sep").setWidth("30px");
        Grid.Column<BudgetItems> octColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getOct()) + "");
            span.setSizeFull();
            if (urcActivity.getOct() != null && !urcActivity.getOct().equals(BigDecimal.ZERO)) {

                if (urcActivity.getOct().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Oct").setWidth("30px");
        Grid.Column<BudgetItems> novColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getNov()) + "");
            span.setSizeFull();
            if (urcActivity.getNov() != null && !urcActivity.getNov().equals(BigDecimal.ZERO)) {

                if (urcActivity.getNov().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Nov").setWidth("30px");
        Grid.Column<BudgetItems> decColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getDec()) + "");
            span.setSizeFull();
            if (urcActivity.getDec() != null && !urcActivity.getDec().equals(BigDecimal.ZERO)) {

                if (urcActivity.getDec().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Dec").setWidth("30px");
        Grid.Column<BudgetItems> janColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getJan()) + "");
            span.setSizeFull();
            if (urcActivity.getJan() != null && !urcActivity.getJan().equals(BigDecimal.ZERO)) {

                if (urcActivity.getJan().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Jan").setWidth("30px");
        Grid.Column<BudgetItems> febColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getFeb()) + "");
            span.setSizeFull();
            if (urcActivity.getFeb() != null && !urcActivity.getFeb().equals(BigDecimal.ZERO)) {

                if (urcActivity.getFeb().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Feb").setWidth("30px");
        Grid.Column<BudgetItems> marColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getMar()) + "");
            span.setSizeFull();
            if (urcActivity.getMar() != null && !urcActivity.getMar().equals(BigDecimal.ZERO)) {

                if (urcActivity.getMar().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Mar").setWidth("30px");
        Grid.Column<BudgetItems> aprColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getApr()) + "");
            span.setSizeFull();
            if (urcActivity.getApr() != null && !urcActivity.getApr().equals(BigDecimal.ZERO)) {

                if (urcActivity.getApr().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Apr").setWidth("30px");
        Grid.Column<BudgetItems> mayColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getMay()) + "");
            span.setSizeFull();
            if (urcActivity.getMay() != null && !urcActivity.getMay().equals(BigDecimal.ZERO)) {

                if (urcActivity.getMay().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("May").setWidth("30px");
        Grid.Column<BudgetItems> junColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(decimalFormat.format(urcActivity.getJun()) + "");
            span.setSizeFull();
            if (urcActivity.getJun() != null && !urcActivity.getJun().equals(BigDecimal.ZERO)) {

                if (urcActivity.getJun().doubleValue() > 0) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                    //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                span.getElement().getThemeList().add("badge error");
                return span; // Return an empty Span if deptSection is null
            }
        })).setHeader("Jun").setWidth("30px");

        HeaderRow headerRow = gridPlan.prependHeaderRow();
        headerRow.join(julColumn, augColumn, sepColumn).setText("QTR 1");
        headerRow.join(octColumn, novColumn, decColumn).setText("QTR 2");
        headerRow.join(janColumn, febColumn, marColumn).setText("QTR 3");
        headerRow.join(aprColumn, mayColumn, junColumn).setText("QTR 4");
        gridPlan.setHeight("100px");
        gridPlan.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
        List<BudgetItems> listBudgetItems = new ArrayList<>();
        Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
        ProcurementPlan mm = p.stream().toList().get(0);
        for (ProcurementPlan pp : p) {

            listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));

        }
        gridPlan.setItems(generatesumofMonthly(listBudgetItems));
        lay.add(gridPlan);
        lay.setWidthFull();
        editorDiv.add(formLayout, lay, lay2);

        formLayout.setColspan(lay, 3);
        formLayout.setColspan(lay2, 3);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel2.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(save2, cancel2);
        cancel2.addClickListener(e -> {
            grid2.deselectAll();
            dia.close();
        });

        save2.addClickListener(e -> {
            CharSequence editorTextFields = validEditorTextFields2();

            if (editorTextFields != null && editorTextFields.length() > 0) {
                //Notification.show("Fill in the Empty Field (s)");
                NotificationDialogue(" Either " + validEditorTextFields2().toString() + "Or Select Budget");
            } else {
                try {
                    Set<ProcurementPlan> ProcurementPlanSelectedSet2 = grid2.getSelectedItems();
                    ProcurementPlan ProcurementPlanSelected2 = ProcurementPlanSelectedSet2.stream().toList().get(0);
                    ProcurementPlanSelected2.setSubject(subject2.getValue());
                    ProcurementPlanSelected2.setProcurementMethod(procurementMethod2.getValue());
                    ProcurementPlanSelected2.setProcurementtype(contractType2.getValue());
                    ProcurementPlanSelected2.setReserve(reserv2.getValue());
                    ProcurementPlanSelected2.setPrequal(preq2.getValue());
                    ProcurementPlanSelected2.setBinvite(bid_inv2.getValue());
                    ProcurementPlanSelected2.setReqClosingOpeningdate(ReqClosingOpeningdate.getValue());
                    ProcurementPlanSelected2.setApprovaloffinalevaluationreport(Approvaloffinalevaluationreport2.getValue());
                    ProcurementPlanSelected2.setAwardnotificationdate(award_not2.getValue());

                    ProcurementPlanSelected2.setContractsigningdate(contract_sign2.getValue());
                    ProcurementPlanSelected2.setBcompletion(completion2.getValue());
                    ProcurementPlanService.save(ProcurementPlanSelected2);
                    refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                    grid2.deselectAll();
                    dia.close();
                    UI.getCurrent().navigate(ProcurementPlanView.class);
                } catch (Exception validationException) {
                    Notificationwarning(" An exception happened while trying to store the User details. " + validationException.getMessage());
                }
            }

        });
        //editorLayoutDiv.add(buttonLayout);        
        // createButtonLayout(editorLayoutDiv);
        dia.add(editorLayoutDiv, buttonLayout);
        dia.open();
        return dia;
    }

    private StringBuilder validEditorTextFields() {
        StringBuilder build = new StringBuilder();
        if (subject.isEmpty()) {
            build.append("Subject field is empty \n");

        }
        if (procurementMethod.isEmpty()) {
            build.append("Procurement Method field is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFields2() {
        StringBuilder build = new StringBuilder();
        if (subject2.isEmpty()) {
            build.append("Subject field is empty \n");

        }
        if (procurementMethod2.isEmpty()) {
            build.append("Procurement Method field is empty \n");

        }
        return build;
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(save, cancel);

        editorLayoutDiv.add(buttonLayout);

    }

    private void createGridLayout(SplitLayout splitLayout) {

        VerticalLayout wrapper = new VerticalLayout();
        VerticalLayout wrapper2 = new VerticalLayout();
        // wrapper.setClassName("grid-wrapper");
        //wrapper2.setClassName("grid-wrapper");

        wrapper.add(grid);
        //Scroller scroller = new Scroller(new Div(grid));
        //wrapper2.add(scroller);
        splitLayout.addToPrimary(wrapper);
    }

    private void createGridLayout2(SplitLayout splitLayout) {

        VerticalLayout wrapper = new VerticalLayout();
        VerticalLayout wrapper2 = new VerticalLayout();
        // wrapper.setClassName("grid-wrapper");
        //wrapper2.setClassName("grid-wrapper");

        wrapper.add(grid2);
        //Scroller scroller = new Scroller(new Div(grid));
        //wrapper2.add(scroller);
        splitLayout.addToPrimary(wrapper);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        preq.clear();
        populateForm(null);
        budget.setValue(sampleBudget);
        refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
        //unitsList.setEnabled(false);
    }

    private void clearForm2() {
        preq2.clear();
        populateForm(null);
        budget.setValue(sampleBudget);
        refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
        //unitsList.setEnabled(false);
    }

    private void populateForm(ProcurementPlan value) {
        this.sampleProcurementPlan = value;
        binder.readBean(this.sampleProcurementPlan);
        //subject.setValue(value.getSubject());
        //procMethod.setValue(value.getProcurementMethod());
        //contractType.setValue(value.getProcurementtype());

    }

    private void populateForm2(ProcurementPlan value) {
        this.sampleProcurementPlan2 = value;
        binder2.readBean(this.sampleProcurementPlan2);
        subject.setValue(value.getSubject());
        procurementMethod2.setValue(value.getProcurementMethod());
        contractType2.setValue(value.getProcurementtype());
        bid_inv2.setValue(value.getBinvite());
        ReqClosingOpeningdate.setValue(value.getReqClosingOpeningdate());
        Approvaloffinalevaluationreport2.setValue(value.getApprovaloffinalevaluationreport());
        award_not2.setValue(value.getAwardnotificationdate());;
        contract_sign2.setValue(value.getContractsigningdate());
        completion2.setValue(value.getBcompletion());

    }

    private void setProcurementPlan(ProcurementPlan value) {

        procurementMethod.setItems(ProcurementMethodService.getAllProcurementMethods());
        if (value != null) {
            // Set values only if the 'value' object is not null
            subject.setValue(value.getSubject());
            if (value.getProcurementMethod() != null) {
                procurementMethod.setValue(value.getProcurementMethod());
            }

            contractType.setValue(value.getProcurementtype());
            if (value.getReserve() != null) {
                reserv.setValue(value.getReserve());
            }

            ReqInviofExpofInterestdate.setValue(value.getReqInviofExpofInterestdate());
            ReqClosingOpeningdate.setValue(value.getReqClosingOpeningdate());
            ReqApprovalOfShortlist.setValue(value.getReqApprovalOfShortlist());
            ReqNotificationdate.setValue(value.getReqNotificationdate());
            InvitationofProposalsdate.setValue(value.getInvitationofProposalsdate());
            SubmissionOpeningdate.setValue(value.getSubmissionOpeningdate());
            Approvaloffinalevaluationreport.setValue(value.getApprovaloffinalevaluationreport());
            InvNotificationdate.setValue(value.getInvNotificationdate());
            contract_sign.setValue(value.getContractsigningdate());
            completion.setValue(value.getBcompletion());
        } else {

        }
    }

    private void setProcurementPlan2(ProcurementPlan value) {

        procurementMethod2.setItems(ProcurementMethodService.getAllProcurementMethods());
        if (value != null) {
            // Set values only if the 'value' object is not null
            subject2.setValue(value.getSubject());
            if (value.getProcurementMethod() != null) {
                procurementMethod2.setValue(value.getProcurementMethod());
            }

            contractType2.setValue(value.getProcurementtype());
            if (value.getReserve() != null) {
                reserv2.setValue(value.getReserve());
            }
            if (value.getPrequal() != null) {
                preq2.setValue(value.getPrequal());
            }

            bid_inv2.setValue(value.getBinvite());
            ReqClosingOpeningdate2.setValue(value.getReqClosingOpeningdate());
            Approvaloffinalevaluationreport2.setValue(value.getApprovaloffinalevaluationreport());
            award_not2.setValue(value.getAwardnotificationdate());;
            contract_sign2.setValue(value.getContractsigningdate());
            completion2.setValue(value.getBcompletion());

        } else {

        }
    }

    public static String prepareRandomString(int len) {
        char[] buf = new char[len];

        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }

    static {
        StringBuilder tmp = new StringBuilder();

        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }

        for (char ch = 'a'; ch <= 'z'; ++ch) {
            tmp.append(ch);
        }

        symbols = tmp.toString().toCharArray();
    }

    public Notification Notificationwarning(String warning) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                new Text(warning)
        );

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();

        notification.setPosition(Notification.Position.TOP_CENTER);

        return notification;
    }

    private Component NotificationDialogue(String errorMessage) {
        Dialog dialog = new Dialog();
        dialog.setModal(false);

        dialog.setHeaderTitle("Fill in the Empty Fields");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        TextArea label = new TextArea();
        label.setEnabled(false);
        label.setValue(errorMessage);
        label.setHeightFull();
        label.setWidthFull();
        label.getStyle().set("background", "red");

        VerticalLayout dialogLayout = new VerticalLayout(label);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        //dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(dialogLayout);
        dialog.open();
        return dialog;
    }

    public Dialog unitListEditor(MultiSelectComboBox<D_Unit> unitsList) {

        Dialog dialog = new Dialog();
        dialog.setModal(false);
        VerticalLayout div = new VerticalLayout();

        dialog.setHeaderTitle("User Units");

        Button saveButton = new Button("Save", e -> {

            dialog.close();

        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        /*            // Center the button within the example
            dialog.getStyle().set("position", "fixed").set("top", "0").set("right", "0")
            .set("bottom", "0").set("left", "0").set("display", "flex")
            .set("align-items", "center").set("justify-content", "center");*/
        div.setPadding(false);
        div.setSpacing(false);
        div.setAlignItems(FlexComponent.Alignment.STRETCH);
        div.getStyle().set("width", "500px").set("max-width", "100%");

        div.add(unitsList);
        dialog.add(div);

        dialog.open();

        return dialog;
    }

    private class PersonContextMenuGrid2 extends GridContextMenu<ProcurementPlan> {

        public PersonContextMenuGrid2(Grid<ProcurementPlan> target) {
            super(target);

            addItem("Edit", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (!target.asMultiSelect().isEmpty()) {
                    if (target.asMultiSelect().getSelectedItems().size() > 1) {
                        Notificationwarning("You should select one Procurement plan Item to Edit");
                    } else {

                        Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                        ProcurementPlan mm = p.stream().toList().get(0);
                        setProcurementPlan2(mm);
                        populateForm2(mm);
                        Grid<BudgetItems> gridPlan = new Grid<>(BudgetItems.class, false);
                        Dialog dia = editProPlan2(gridPlan, target);
                        dia.setHeaderTitle("Edit Procurement Plan Item");

                        cancel.addClickListener(ep -> {
                            grid2.deselectAll();
                            dia.close();
                        });
                    }

                } else {
                    Notificationwarning("You should select a Procurement Plan Item to Edit");
                }

            }));
            add(new Hr());
            addItem("Combine Items", e -> e.getItem().ifPresent(person -> {

                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().size() < 2) {
                    Notificationwarning("You should select more than one Procurement plan Item to Combine");
                } else {
                    if (!target.asMultiSelect().isEmpty()) {
                        if (!procClassCombo2.isEmpty()) {
                            if (procClassCombo2.getValue() == ProcClass.Supplies || procClassCombo2.getValue() == ProcClass.Works || procClassCombo2.getValue() == ProcClass.Non_Consultancy) {
                                Dialog dialogA = new Dialog();
                                dialogA.setModal(false);
                                dialogA.setDraggable(true);
                                dialogA.setResizable(true);
                                dialogA.setWidth("800px");
                                Div editorLayoutDiv = new Div();
                                editorLayoutDiv.setClassName("user-editor-layout");

                                Button closeButton = new Button(new Icon("lumo", "cross"), (ex) -> {
                                    dialogA.close();
                                });
                                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                                dialogA.getHeader().add(closeButton);

                                dialogA.setHeaderTitle("Combined Procurement Item Details");

                                Div editorDiv = new Div();
                                editorDiv.setClassName("user-editor");
                                editorLayoutDiv.add(editorDiv);

                                FormLayout formLayout = new FormLayout();

                                TextField subject2 = new TextField("Subject of Procurement");

                                ComboBox<ProcurementType> contractType2 = new ComboBox("Contract Type");
                                contractType2.setItemLabelGenerator(ProcurementType::getProcuremntType);
                                contractType2.setItems(sampleProcurementTypeService.getAllProcurementTypes());
                                BigDecimalField cost2 = new BigDecimalField("Threshold");
                                Checkbox preq2 = new Checkbox("Prequalification");
                                Checkbox reserv2 = new Checkbox("Apply Reservation Scheme");

                                DatePicker bid_inv2 = new DatePicker("Bid Invitation Date");
                                DatePicker bid_close_open = new DatePicker("Bid closing - Opening date");
                                DatePicker bid_Approval_evaluation_report = new DatePicker("Approval Evaluation Report Date");
                                DatePicker award_notification = new DatePicker("Award Notification Date");
                                DatePicker contract_sign2 = new DatePicker("Contract Signing Date");
                                DatePicker completion2 = new DatePicker("Completion Date");

                                subject2.setRequired(true);
                                subject2.setRequiredIndicatorVisible(true);
                                subject2.setErrorMessage("Subject is Required");
                                subject2.setClearButtonVisible(true);

                                cost2.setRequired(true);
                                cost2.setRequiredIndicatorVisible(true);
                                cost2.setErrorMessage("Fund Source is Required");
                                cost2.setClearButtonVisible(true);
                                /*                         Checkbox checkbox2 = new Checkbox();
                                checkbox2.setLabel("Apply Reservation Scheme");*/

                                ComboBox<ProcurementMethod> procMethod12Box = new ComboBox("Procurement Method");
                                procMethod12Box.setItemLabelGenerator(ProcurementMethod::getProcuremntMethod);
                                procMethod12Box.setClearButtonVisible(true);
                                procMethod12Box.setRequired(true);
                                procMethod12Box.setRequiredIndicatorVisible(true);
                                procMethod12Box.setErrorMessage("Procurement Method is Required");
                                procMethod12Box.setItems(ProcurementMethodService.getAllProcurementMethods());

                                contractType2.setRequired(true);
                                contractType2.setRequiredIndicatorVisible(true);
                                contractType2.setErrorMessage("Contact Type is Required");

                                HorizontalLayout lay = new HorizontalLayout();
                                HorizontalLayout lay2 = new HorizontalLayout();
                                Grid<BudgetItems> gridPlan = new Grid<>(BudgetItems.class, false);
                                //Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(BudgetItems::getJul).setHeader("Jul").setWidth("30px");
                                Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJul()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJul() != null && !urcActivity.getJul().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJul().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jul").setWidth("30px");
                                // Set the background color based on the value using CSS classes
                                julColumn.setClassNameGenerator(item -> {
                                    if (item.getJul() != null && item.getJul().doubleValue() > 0) {
                                        return "positive-value";
                                    } else {
                                        return "negative-value";
                                    }
                                });
                                //Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(BudgetItems::getAug).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getAug()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getAug() != null && !urcActivity.getAug().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getAug().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> sepColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getSep()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getSep() != null && !urcActivity.getSep().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getSep().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Sep").setWidth("30px");
                                Grid.Column<BudgetItems> octColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getOct()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getOct() != null && !urcActivity.getOct().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getOct().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Oct").setWidth("30px");
                                Grid.Column<BudgetItems> novColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getNov()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getNov() != null && !urcActivity.getNov().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getNov().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Nov").setWidth("30px");
                                Grid.Column<BudgetItems> decColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getDec()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getDec() != null && !urcActivity.getDec().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getDec().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Dec").setWidth("30px");
                                Grid.Column<BudgetItems> janColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJan()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJan() != null && !urcActivity.getJan().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJan().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jan").setWidth("30px");
                                Grid.Column<BudgetItems> febColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getFeb()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getFeb() != null && !urcActivity.getFeb().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getFeb().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Feb").setWidth("30px");
                                Grid.Column<BudgetItems> marColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMar()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMar() != null && !urcActivity.getMar().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMar().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Mar").setWidth("30px");
                                Grid.Column<BudgetItems> aprColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getApr()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getApr() != null && !urcActivity.getApr().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getApr().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Apr").setWidth("30px");
                                Grid.Column<BudgetItems> mayColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMay()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMay() != null && !urcActivity.getMay().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMay().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("May").setWidth("30px");
                                Grid.Column<BudgetItems> junColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJun()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJun() != null && !urcActivity.getJun().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJun().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jun").setWidth("30px");

                                HeaderRow headerRow = gridPlan.prependHeaderRow();
                                headerRow.join(julColumn, augColumn, sepColumn).setText("QTR 1");
                                headerRow.join(octColumn, novColumn, decColumn).setText("QTR 2");
                                headerRow.join(janColumn, febColumn, marColumn).setText("QTR 3");
                                headerRow.join(aprColumn, mayColumn, junColumn).setText("QTR 4");
                                gridPlan.setHeight("100px");
                                gridPlan.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
                                lay.add(gridPlan);
                                lay.setWidthFull();

                                formLayout.add(subject2,
                                        cost2,
                                        procMethod12Box,
                                        contractType2,
                                        preq2,
                                        reserv2,
                                        bid_inv2,
                                        bid_close_open,
                                        bid_Approval_evaluation_report,
                                        award_notification,
                                        contract_sign2,
                                        completion2);
                                formLayout.setResponsiveSteps(
                                        new FormLayout.ResponsiveStep("0px", 1),
                                        new FormLayout.ResponsiveStep("600px", 2),
                                        new FormLayout.ResponsiveStep("900px", 3)
                                );

                                editorDiv.add(formLayout, lay, lay2);

                                formLayout.setColspan(lay, 3);
                                formLayout.setColspan(lay2, 3);

                                dialogA.add(editorLayoutDiv);
                                Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                                for (ProcurementPlan pp : p) {

                                    listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));

                                }
                                gridPlan.setItems(generatesumofMonthly(listBudgetItems));
                                Button savep = new Button("Combine");
                                Button cancelButton = new Button("Cancel", ex -> dialogA.close());
                                //savep.setDisableOnClick(true);
                                Set<ProcurementPlan> selectedPlans2 = target.asMultiSelect().getSelectedItems();

                                if (!selectedPlans2.isEmpty()) {
                                    // Get the COA of the first procurement plan
                                    COA firstCOA = selectedPlans2.iterator().next().getCoa();
                                    subject2.setValue(firstCOA.getName());
                                }
                                savep.addClickListener(ex -> {
                                    if (!subject2.getValue().isEmpty()) { // This check should be subject2.getValue().isEmpty() instead of subject2.isEmpty()
                                        Set<ProcurementPlan> selectedPlans = target.asMultiSelect().getSelectedItems();

                                        if (!selectedPlans.isEmpty()) {
                                            // Get the COA of the first procurement plan
                                            COA firstCOA = selectedPlans.iterator().next().getCoa();

                                            // Check if all selected plans have the same COA
                                            boolean allSameCOA = selectedPlans.stream().allMatch(plan -> plan.getCoa().equals(firstCOA));
                                            if (allSameCOA) {
                                                List<BudgetItems> allBudgetItems = new ArrayList<>();
                                                BigDecimal totalCost = BigDecimal.ZERO;

                                                for (ProcurementPlan pp : selectedPlans) {
                                                    allBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));
                                                    totalCost = totalCost.add(pp.getCost());
                                                    ProcurementPlanService.deleteProcurementPlan(pp);
                                                }

                                                ProcurementPlan pr = new ProcurementPlan();
                                                pr.setSubject(subject2.getValue());
                                                pr.setBudget(selectedPlans.iterator().next().getBudget()); // Assuming all selected plans have the same budget
                                                pr.setCoa(selectedPlans.iterator().next().getCoa()); // Assuming all selected plans have the same coa
                                                //pr.setProcPlanBudgetItems(new HashSet<>(allBudgetItems));
                                                pr.setCurrency(selectedPlans.iterator().next().getCurrency()); // Assuming all selected plans have the same currency
                                                // pr.setFundsource(selectedPlans.iterator().next().getFundsource()); // Assuming all selected plans have the same fund source
                                                pr.setProcurementMethod(procMethod12Box.getValue());
                                                pr.setProcurementtype(contractType2.getValue());
                                                pr.setBinvite(bid_inv2.getValue());
                                                pr.setReqClosingOpeningdate(bid_close_open.getValue());
                                                pr.setApprovaloffinalevaluationreport(bid_Approval_evaluation_report.getValue());
                                                pr.setAwardnotificationdate(award_notification.getValue());
                                                pr.setContractsigningdate(contract_sign2.getValue());
                                                pr.setBcompletion(completion2.getValue());
                                                pr.setCost(totalCost); // Set total cost calculated from selected plans
                                                pr.setProcClass(procClassCombo2.getValue());
                                                pr.setPrequal(preq2.getValue());
                                                pr.setReserve(reserv2.getValue());

                                                ProcurementPlan savedPlan = ProcurementPlanService.save(pr);
                                                //refreshgridProcurementPlan2(procClassCombo2.getValue(), null);
                                                refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                                                //dialogA.setEnabled(true); // Disabling the dialog here
                                                dialogA.close();
                                            } else {
                                                Notificationwarning("Selected Items should have the same Account Code");
                                            }

                                        }
                                    } else {
                                        Notificationwarning("Ensure that the description is not empty");
                                    }
                                });

                                procMethod12Box.setValue(ProcurementPlanService.getProcurementMethodList2(procClassCombo2.getValue(), generatesumofMonthsFromList(listBudgetItems)));
                                savep.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                cost2.setValue(generatesumofMonthsFromList(listBudgetItems));

                                dialogA.getFooter().add(cancelButton);
                                dialogA.getFooter().add(savep);
                                dialogA.open();
                            }
                        }

                    }
                }

            }));

            add(new Hr());
            addItem("Ungroup Combination", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().size() != 1) {
                    Notificationwarning("You should select one Procurement plan Item");
                } else {
                    if (!target.asMultiSelect().isEmpty()) {
                        if (!procClassCombo2.isEmpty() && !budget2.isEmpty()) {
                            Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                            ProcurementPlan vb = p.stream().toList().get(0);

                            if (vb != null) {

                                List<BudgetItems> lists = budgetItemsService.findByBudgetAndProcClassAndCoa(vb.getBudget(), vb.getProcClass(), vb.getCoa());
                                for (BudgetItems pp : lists) {
                                    ProcurementPlan pr = new ProcurementPlan();
                                    pr.setSubject(pp.getItem());
                                    pr.setBudget(pp.getBudget()); // Assuming all selected plans have the same budget
                                    pr.setCoa(pp.getCoacode()); // Assuming all selected plans have the same coa
                                    listBudgetItems.add(pp);
                                    //pr.setProcPlanBudgetItems(new HashSet<>(listBudgetItems));
                                    pr.setCurrency(pp.getCurrency()); // Assuming all selected plans have the same currency
                                    Set<Fundsource> fundsourceSet = new HashSet<>();
                                    fundsourceSet.add(pp.getFundsource());// Assuming all selected plans have the same fund source
                                    pr.setCost(generatesumofMonths(pp));
                                    pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(procClassCombo2.getValue(), pr.getCost()));
                                    pr.setProcClass(procClassCombo2.getValue());

                                    ProcurementPlan savedPlan = ProcurementPlanService.save(pr);
                                    if (savedPlan != null) {
                                    }

                                }
                            }
                            ProcurementPlanService.deleteProcurementPlan(vb);
                            //refreshgridProcurementPlan2(procClassCombo2.getValue(), null);
                            refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                        }
                    }
                }
            }));

            add(new Hr());
            add(new Hr());
            addItem("Change Procurement Class", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().isEmpty()) {
                    Notificationwarning("You should select a Procurement plan Item");
                } else {
                    Set<ProcurementPlan> ps = target.asMultiSelect().getSelectedItems();
                    ProcurementPlan vb = ps.stream().toList().get(0);
                    Dialog dialog = new Dialog();

                    dialog.setHeaderTitle("Edit");

                    VerticalLayout dialogLayout = new VerticalLayout();

                    ComboBox<ProcClass> procClassCombox = new ComboBox("Procurement Class");
                    procClassCombox.setItems(ProcClass.Supplies, ProcClass.Works, ProcClass.Non_Consultancy, ProcClass.Consultancy, ProcClass.Other);
                    procClassCombox.setValue(person.getProcClass());
                    dialogLayout.add(procClassCombox);
                    dialog.add(dialogLayout);

                    Button saveButton = new Button("Edit", ep -> {
                        if (!target.asMultiSelect().isEmpty()) {
                            if (!procClassCombox.isEmpty() && !budget2.isEmpty()) {
                                Set<ProcurementPlan> selectedPlans = target.asMultiSelect().getSelectedItems();
                                if (!selectedPlans.isEmpty()) {
                                    for (ProcurementPlan p : selectedPlans) {
                                        p.setProcClass(procClassCombox.getValue());
                                        ProcurementPlanService.save(p);
                                        procClassCombox.clear();
                                    }

                                }
                            }
                        }
                        //refreshgridProcurementPlan2(procClassCombo2.getValue(), null);
                        refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                        dialog.close();
                    });
                    Button cancelButton = new Button("Cancel", ek -> dialog.close());
                    dialog.getFooter().add(cancelButton);
                    dialog.getFooter().add(saveButton);
                    dialog.open();

                }
            }));
            add(new Hr());
        }

    }

    private class ProcuremetPlanGridContextMenu extends GridContextMenu<ProcurementPlan> {

        public ProcuremetPlanGridContextMenu(Grid<ProcurementPlan> target) {
            super(target);

            addItem("Edit", e -> e.getItem().ifPresent(person -> {
                if (!target.asMultiSelect().isEmpty()) {
                    if (target.asMultiSelect().getSelectedItems().size() > 1) {
                        Notificationwarning("You should select one Procurement plan Item to Edit");
                    } else {
                        Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                        ProcurementPlan mm = p.stream().toList().get(0);
                        setProcurementPlan(mm);
                        populateForm(mm);
                        Dialog dia = new Dialog();
                        dia.setHeaderTitle("Edit Procurement Item Details");
                        Grid<BudgetItems> gridPlan = new Grid<>(BudgetItems.class, false);
                        editProPlan(mm, gridPlan, target);
                        cancel.addClickListener(ep -> {
                            dia.close();
                        });
                    }

                } else {
                    Notificationwarning("You should select a Procurement Plan Item to Edit");
                }

            }));
            add(new Hr());
            addItem("Combine Items", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().size() < 2) {
                    Notificationwarning("You should select more than one Procurement plan Item to Combine");
                } else {
                    if (!target.asMultiSelect().isEmpty()) {
                        if (!procClassCombo.isEmpty() && !budget.isEmpty()) {

                            if (procClassCombo.getValue() == ProcClass.Consultancy) {
                                Dialog dialogB = new Dialog();
                                dialogB.setModal(false);
                                dialogB.setDraggable(true);
                                dialogB.setResizable(true);
                                dialogB.setWidth("800px");
                                Div editorLayoutDiv = new Div();
                                editorLayoutDiv.setClassName("user-editor-layout");
                                Button closeButton = new Button(new Icon("lumo", "cross"), (ex) -> {
                                    dialogB.close();
                                });
                                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                                dialogB.getHeader().add(closeButton);
                                dialogB.setHeaderTitle("Combined Procurement Item Details");

                                Div editorDiv = new Div();
                                editorDiv.setClassName("user-editor");
                                editorLayoutDiv.add(editorDiv);

                                FormLayout formLayout = new FormLayout();

                                TextField subject2 = new TextField("Subject of Procurement");
                                ComboBox<Currency> currency2 = new ComboBox("Currency");
                                ComboBox<Fundsource> fundsource2 = new ComboBox("Source of Funds");
                                ComboBox<ProcurementMethod> procMethod10Box = new ComboBox("Procurement Method");
                                ComboBox<ProcurementType> contractType2 = new ComboBox("Contract Type");
                                contractType2.setItemLabelGenerator(ProcurementType::getProcuremntType);
                                contractType2.setItems(sampleProcurementTypeService.getAllProcurementTypes());
                                BigDecimalField cost2 = new BigDecimalField("Threshold");
                                ComboBox<Budget> budget2 = new ComboBox("Budget");
                                ComboBox<ProcClass> procClassCombo2 = new ComboBox("Procurement Class");
                                //Checkbox preq2 = new Checkbox("Prequalification");
                                Checkbox reserv2 = new Checkbox("Apply Reservation Scheme ");
                                DatePicker bid_inv2 = new DatePicker("Invitation of Expressions of Interest date");
                                DatePicker req_close_open_date = new DatePicker("Closing - Opening date");
                                DatePicker req_aprroval_of__shortlist = new DatePicker("Approval of shortlist");
                                DatePicker req_notification_date = new DatePicker("Notification Date");
                                DatePicker inv_proposal_date = new DatePicker("Invitation of proposals date");
                                DatePicker submission_open_date = new DatePicker("Contract Signing Date");
                                DatePicker approval_of_final_evaluation_report_date = new DatePicker("Approval Of Final Evaluation Report Date");
                                DatePicker inv_notification_date = new DatePicker("Notification Date");
                                DatePicker contract_signing_date = new DatePicker("Contract signing date");
                                DatePicker completion_date = new DatePicker("Completion Date");

                                subject2.setRequired(true);
                                subject2.setRequiredIndicatorVisible(true);
                                subject2.setErrorMessage("Subject is Required");
                                subject2.setClearButtonVisible(true);

                                cost2.setRequired(true);
                                cost2.setRequiredIndicatorVisible(true);
                                cost2.setErrorMessage("Fund Source is Required");
                                cost2.setClearButtonVisible(true);

                                procMethod10Box.setClearButtonVisible(true);
                                procMethod10Box.setRequired(true);
                                procMethod10Box.setRequiredIndicatorVisible(true);
                                procMethod10Box.setErrorMessage("Procurement Method is Required");
                                procMethod10Box.setItems(ProcurementMethodService.getAllProcurementMethods());

                                contractType2.setRequired(true);
                                contractType2.setRequiredIndicatorVisible(true);
                                contractType2.setErrorMessage("Contact Type is Required");

                                Html span1 = new Html("<b>Request for Expression of Interest</b>");
                                Hr hr1 = new Hr();

                                Html span2 = new Html("<b>Invitation of proposals and approval for award</b>");
                                Hr hr2 = new Hr();
                                Hr hr3 = new Hr();

                                formLayout.add(subject2,
                                        cost2,
                                        procMethod10Box,
                                        reserv2,
                                        contractType2,
                                        hr1,
                                        span1,
                                        bid_inv2,
                                        req_close_open_date,
                                        req_aprroval_of__shortlist,
                                        req_notification_date,
                                        hr2,
                                        span2,
                                        inv_proposal_date,
                                        submission_open_date,
                                        approval_of_final_evaluation_report_date,
                                        inv_notification_date,
                                        contract_signing_date,
                                        hr3,
                                        completion_date);
                                formLayout.setResponsiveSteps(
                                        new FormLayout.ResponsiveStep("0px", 1),
                                        new FormLayout.ResponsiveStep("600px", 2),
                                        new FormLayout.ResponsiveStep("900px", 3)
                                );
                                formLayout.setColspan(span1, 3);
                                formLayout.setColspan(span2, 3);
                                formLayout.setColspan(hr1, 3);
                                formLayout.setColspan(hr2, 3);
                                formLayout.setColspan(hr3, 3);

                                HorizontalLayout lay = new HorizontalLayout();
                                HorizontalLayout lay2 = new HorizontalLayout();
                                Grid<BudgetItems> gridPlan = new Grid<>(BudgetItems.class, false);
                                //Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(BudgetItems::getJul).setHeader("Jul").setWidth("30px");
                                Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJul()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJul() != null && !urcActivity.getJul().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJul().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jul").setWidth("30px");
                                // Set the background color based on the value using CSS classes
                                julColumn.setClassNameGenerator(item -> {
                                    if (item.getJul() != null && item.getJul().doubleValue() > 0) {
                                        return "positive-value";
                                    } else {
                                        return "negative-value";
                                    }
                                });
                                //Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(BudgetItems::getAug).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getAug()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getAug() != null && !urcActivity.getAug().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getAug().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> sepColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getSep()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getSep() != null && !urcActivity.getSep().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getSep().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Sep").setWidth("30px");
                                Grid.Column<BudgetItems> octColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getOct()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getOct() != null && !urcActivity.getOct().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getOct().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Oct").setWidth("30px");
                                Grid.Column<BudgetItems> novColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getNov()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getNov() != null && !urcActivity.getNov().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getNov().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Nov").setWidth("30px");
                                Grid.Column<BudgetItems> decColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getDec()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getDec() != null && !urcActivity.getDec().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getDec().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Dec").setWidth("30px");
                                Grid.Column<BudgetItems> janColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJan()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJan() != null && !urcActivity.getJan().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJan().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jan").setWidth("30px");
                                Grid.Column<BudgetItems> febColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getFeb()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getFeb() != null && !urcActivity.getFeb().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getFeb().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Feb").setWidth("30px");
                                Grid.Column<BudgetItems> marColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMar()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMar() != null && !urcActivity.getMar().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMar().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Mar").setWidth("30px");
                                Grid.Column<BudgetItems> aprColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getApr()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getApr() != null && !urcActivity.getApr().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getApr().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Apr").setWidth("30px");
                                Grid.Column<BudgetItems> mayColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMay()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMay() != null && !urcActivity.getMay().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMay().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("May").setWidth("30px");
                                Grid.Column<BudgetItems> junColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJun()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJun() != null && !urcActivity.getJun().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJun().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jun").setWidth("30px");

                                HeaderRow headerRow = gridPlan.prependHeaderRow();
                                headerRow.join(julColumn, augColumn, sepColumn).setText("QTR 1");
                                headerRow.join(octColumn, novColumn, decColumn).setText("QTR 2");
                                headerRow.join(janColumn, febColumn, marColumn).setText("QTR 3");
                                headerRow.join(aprColumn, mayColumn, junColumn).setText("QTR 4");
                                gridPlan.setHeight("100px");
                                gridPlan.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
                                lay.add(gridPlan);
                                lay.setWidthFull();
                                editorDiv.add(formLayout, lay, lay2);

                                formLayout.setColspan(lay, 3);
                                formLayout.setColspan(lay2, 3);

                                dialogB.add(editorLayoutDiv);
                                Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                                for (ProcurementPlan pp : p) {

                                    listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));

                                }
                                gridPlan.setItems(generatesumofMonthly(listBudgetItems));
                                Button savep = new Button("Combine");
                                Button cancelButton = new Button("Cancel", ex -> dialogB.close());
                                //savep.setDisableOnClick(true);
                                Set<ProcurementPlan> selectedPlans2 = target.asMultiSelect().getSelectedItems();

                                if (!selectedPlans2.isEmpty()) {
                                    // Get the COA of the first procurement plan
                                    COA firstCOA = selectedPlans2.iterator().next().getCoa();
                                    subject2.setValue(firstCOA.getName());
                                }
                                savep.addClickListener(ex -> {
                                    cancelButton.setVisible(false);
                                    //dialogB.setEnabled(false);
                                    if (!subject2.getValue().isEmpty()) { // This check should be subject2.getValue().isEmpty() instead of subject2.isEmpty()
                                        Set<ProcurementPlan> selectedPlans = target.asMultiSelect().getSelectedItems();

                                        if (!selectedPlans.isEmpty()) {
                                            // Get the COA of the first procurement plan
                                            COA firstCOA = selectedPlans.iterator().next().getCoa();

                                            // Check if all selected plans have the same COA
                                            boolean allSameCOA = selectedPlans.stream().allMatch(plan -> plan.getCoa().equals(firstCOA));
                                            if (allSameCOA) {
                                                List<BudgetItems> allBudgetItems = new ArrayList<>();
                                                BigDecimal totalCost = BigDecimal.ZERO;

                                                for (ProcurementPlan pp : selectedPlans) {

                                                    allBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));
                                                    totalCost = totalCost.add(pp.getCost());
                                                    ProcurementPlanService.deleteProcurementPlan(pp);
                                                }

                                                ProcurementPlan pr = new ProcurementPlan();
                                                pr.setSubject(subject2.getValue());
                                                pr.setBudget(selectedPlans.iterator().next().getBudget()); // Assuming all selected plans have the same budget
                                                pr.setCoa(selectedPlans.iterator().next().getCoa()); // Assuming all selected plans have the same coa
                                                //pr.setProcPlanBudgetItems(new HashSet<>(allBudgetItems));
                                                pr.setCurrency(selectedPlans.iterator().next().getCurrency()); // Assuming all selected plans have the same currency
                                                //pr.setFundsource(selectedPlans.iterator().next().getFundsource()); // Assuming all selected plans have the same fund source
                                                pr.setProcurementMethod(procMethod10Box.getValue());
                                                pr.setProcurementtype(contractType2.getValue());
                                                pr.setReqInviofExpofInterestdate(bid_inv2.getValue());
                                                pr.setReqClosingOpeningdate(req_close_open_date.getValue());
                                                pr.setReqApprovalOfShortlist(req_aprroval_of__shortlist.getValue());
                                                pr.setReqNotificationdate(req_notification_date.getValue());
                                                pr.setInvitationofProposalsdate(inv_proposal_date.getValue());
                                                pr.setSubmissionOpeningdate(submission_open_date.getValue());
                                                pr.setApprovaloffinalevaluationreport(approval_of_final_evaluation_report_date.getValue());
                                                pr.setInvNotificationdate(inv_notification_date.getValue());
                                                pr.setContractsigningdate(contract_signing_date.getValue());
                                                pr.setBcompletion(completion_date.getValue());

                                                pr.setCost(totalCost); // Set total cost calculated from selected plans
                                                pr.setProcClass(procClassCombo.getValue());
                                                //pr.setPrequal(preq2.getValue());
                                                pr.setReserve(reserv2.getValue());

                                                ProcurementPlan savedPlan = ProcurementPlanService.save(pr);
                                                refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                                                //dialogB.setEnabled(true); // Disabling the dialog here
                                                dialogB.close();
                                            } else {
                                                Notificationwarning("Selected Items should have the same Account Code");
                                            }

                                        }
                                    } else {
                                        Notificationwarning("Ensure that the description is not empty");
                                    }
                                });

                                procMethod10Box.setValue(ProcurementPlanService.getProcurementMethodList2(procClassCombo.getValue(), generatesumofMonthsFromList(listBudgetItems)));
                                savep.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                cost2.setValue(generatesumofMonthsFromList(listBudgetItems));

                                dialogB.getFooter().add(cancelButton);
                                dialogB.getFooter().add(savep);
                                dialogB.open();
                            }
                        }

                    }
                }

            }));

            add(new Hr());
            addItem("Ungroup Combination", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().size() != 1) {
                    Notificationwarning("You should select one Procurement plan Item");
                } else {
                    if (!target.asMultiSelect().isEmpty()) {
                        if (!procClassCombo.isEmpty() && !budget.isEmpty()) {
                            Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                            ProcurementPlan vb = p.stream().toList().get(0);
                            if (vb != null) {
                                List<BudgetItems> lists = budgetItemsService.findByBudgetAndProcClassAndCoa(vb.getBudget(), vb.getProcClass(), vb.getCoa());
                                for (BudgetItems pp : lists) {
                                    ProcurementPlan pr = new ProcurementPlan();
                                    pr.setSubject(pp.getItem());
                                    pr.setBudget(pp.getBudget()); // Assuming all selected plans have the same budget
                                    pr.setCoa(pp.getCoacode()); // Assuming all selected plans have the same coa
                                    listBudgetItems.add(pp);
                                    //pr.setProcPlanBudgetItems(new HashSet<>(listBudgetItems));
                                    pr.setCurrency(pp.getCurrency()); // Assuming all selected plans have the same currency
                                    Set<Fundsource> fundsourceSet = new HashSet<>();
                                    fundsourceSet.add(pp.getFundsource());
                                    //pr.setFundsource(fundsourceSet); // Assuming all selected plans have the same fund source
                                    pr.setCost(generatesumofMonths(pp));
                                    pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(procClassCombo.getValue(), pr.getCost()));
                                    pr.setProcClass(procClassCombo.getValue());

                                    ProcurementPlan savedPlan = ProcurementPlanService.save(pr);
                                    if (savedPlan != null) {

                                    }

                                }
                            }
                            ProcurementPlanService.deleteProcurementPlan(vb);
                            refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                        }
                    }
                }
            }));
            add(new Hr());
            addItem("Change Procurement Class", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().isEmpty()) {
                    Notificationwarning("You should select a Procurement plan Item");
                } else {
                    Set<ProcurementPlan> px = target.asMultiSelect().getSelectedItems();
                    ProcurementPlan vb = px.stream().toList().get(0);

                    Dialog dialog = new Dialog();

                    dialog.setHeaderTitle("Edit");

                    VerticalLayout dialogLayout = new VerticalLayout();

                    ComboBox<ProcClass> procClassCombox = new ComboBox("Procurement Class");

                    procClassCombox.setItems(ProcClass.Supplies, ProcClass.Works, ProcClass.Non_Consultancy, ProcClass.Consultancy, ProcClass.Other);
                    procClassCombox.setValue(person.getProcClass());
                    dialogLayout.add(procClassCombox);
                    dialog.add(dialogLayout);

                    Button saveButton = new Button("Edit", ep -> {
                        if (!target.asMultiSelect().isEmpty()) {
                            if (!procClassCombox.isEmpty() && !budget.isEmpty()) {
                                Set<ProcurementPlan> selectedPlans = target.asMultiSelect().getSelectedItems();
                                if (!selectedPlans.isEmpty()) {
                                    for (ProcurementPlan p : selectedPlans) {
                                        p.setProcClass(procClassCombox.getValue());
                                        ProcurementPlanService.save(p);
                                        procClassCombox.clear();
                                    }

                                }
                            }
                        }
                        refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                        dialog.close();
                    });
                    Button cancelButton = new Button("Cancel", ek -> dialog.close());
                    dialog.getFooter().add(cancelButton);
                    dialog.getFooter().add(saveButton);
                    dialog.open();

                }
            }));
            add(new Hr());

        }

    }

    private class BudgetItemsGridContextMenu extends GridContextMenu<BudgetItems> {

        public BudgetItemsGridContextMenu(Grid<BudgetItems> target) {
            super(target);

            add(new Hr());
            addItem("Transfer to Non Consultancy", e -> e.getItem().ifPresent(person -> {
                if (target.asMultiSelect().isEmpty()) {
                    Notificationwarning("You should select a Budget Item to Transfer");
                } else {

                    Set<BudgetItems> selectedBudgetItems = target.getSelectedItems();
                    for (BudgetItems plan : selectedBudgetItems) {
                        ProcClass old = plan.getProcClass();
                        plan.setProcClass(ProcClass.Non_Consultancy);
                        budgetItemsService.update(plan);
//Update the old procurement plan
                        ProcurementPlan ps = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), old, plan.getCoacode());
                        if (ps != null) {
                            BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(ps.getBudget(), ps.getProcClass(), ps.getCoa());
                            if (tDecimal != null && tDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                ps.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(old, tDecimal));
                                ProcurementPlanService.save(ps);
                            } else {
                                ProcurementPlanService.deleteProcurementPlan(ps);
                            }
                        }
                        //update transferred
                        ProcurementPlan ps2 = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), ProcClass.Non_Consultancy, plan.getCoacode());
                        BigDecimal tDecimal = BigDecimal.ZERO;
                        if (ps2 != null) {
                            tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(plan.getBudget(), ps2.getProcClass(), ps2.getCoa());
                        }
                        if (ps2 != null) {
                            ps2.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Non_Consultancy, tDecimal));
                            ProcurementPlanService.save(ps2);
                        } else {
                            tDecimal = generatesumofMonths(plan);
                            ProcurementPlan pr = new ProcurementPlan();
                            pr.setSubject(plan.getCoacode().getName());
                            pr.setBudget(plan.getBudget()); // Assuming all selected plans have the same budget
                            pr.setCoa(plan.getCoacode()); // Assuming all selected plans have the same coa
                            // pr.setProcPlanBudgetItems(new HashSet<>(commonItems));
                            pr.setCurrency(plan.getCurrency()); // Assuming all selected plans have the same currency
                            // pr.setFundsource(plan.getFundsource()); // Assuming all selected plans have the same fund source
                            pr.setProcClass(ProcClass.Non_Consultancy);
                            pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Non_Consultancy, tDecimal));

                            ProcurementPlanService.save(pr);
                        }

                    }

                    refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                    gridBudgetItems.deselectAll();
                    Set<ProcurementPlan> selected2 = grid.asMultiSelect().getSelectedItems();
                    setgridBudgetItemsData(selected2);
                }
            }));
            add(new Hr());

            addItem("Transfer to Supplies", e -> e.getItem().ifPresent(person -> {
                if (target.asMultiSelect().isEmpty()) {
                    Notificationwarning("You should select a Budget Item to Transfer");
                } else {

                    Set<BudgetItems> selectedBudgetItems = target.getSelectedItems();
                    for (BudgetItems plan : selectedBudgetItems) {
                        ProcClass old = plan.getProcClass();
                        plan.setProcClass(ProcClass.Supplies);
                        budgetItemsService.update(plan);
//Update the old procurement plan
                        ProcurementPlan ps = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), old, plan.getCoacode());
                        if (ps != null) {
                            BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(ps.getBudget(), ps.getProcClass(), ps.getCoa());
                            if (tDecimal != null && tDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                ps.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(old, tDecimal));
                                ProcurementPlanService.save(ps);
                            } else {
                                ProcurementPlanService.deleteProcurementPlan(ps);
                            }
                        }
                        //update transferred
                        ProcurementPlan ps2 = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), ProcClass.Supplies, plan.getCoacode());
                        BigDecimal tDecimal = BigDecimal.ZERO;
                        if (ps2 != null) {
                            tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(plan.getBudget(), ps2.getProcClass(), ps2.getCoa());
                        }
                        if (ps2 != null) {
                            ps2.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Supplies, tDecimal));
                            ProcurementPlanService.save(ps2);
                        } else {
                            tDecimal = generatesumofMonths(plan);
                            ProcurementPlan pr = new ProcurementPlan();
                            pr.setSubject(plan.getCoacode().getName());
                            pr.setBudget(plan.getBudget()); // Assuming all selected plans have the same budget
                            pr.setCoa(plan.getCoacode()); // Assuming all selected plans have the same coa
                            // pr.setProcPlanBudgetItems(new HashSet<>(commonItems));
                            pr.setCurrency(plan.getCurrency()); // Assuming all selected plans have the same currency
                            // pr.setFundsource(plan.getFundsource()); // Assuming all selected plans have the same fund source
                            pr.setProcClass(ProcClass.Supplies);
                            pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Supplies, tDecimal));

                            ProcurementPlanService.save(pr);
                        }

                    }

                    refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                    gridBudgetItems.deselectAll();
                    Set<ProcurementPlan> selected2 = grid.asMultiSelect().getSelectedItems();
                    setgridBudgetItemsData(selected2);
                }
            }));
            add(new Hr());
            addItem("Transfer to Works", e -> e.getItem().ifPresent(person -> {
                if (target.asMultiSelect().isEmpty()) {
                    Notificationwarning("You should select a Budget Item to Transfer");
                } else {

                    Set<BudgetItems> selectedBudgetItems = target.getSelectedItems();
                    for (BudgetItems plan : selectedBudgetItems) {
                        ProcClass old = plan.getProcClass();
                        plan.setProcClass(ProcClass.Works);
                        budgetItemsService.update(plan);
//Update the old procurement plan
                        ProcurementPlan ps = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), old, plan.getCoacode());
                        if (ps != null) {
                            BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(ps.getBudget(), ps.getProcClass(), ps.getCoa());
                            if (tDecimal != null && tDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                ps.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(old, tDecimal));
                                ProcurementPlanService.save(ps);
                            } else {
                                ProcurementPlanService.deleteProcurementPlan(ps);
                            }
                        }
                        //update transferred
                        ProcurementPlan ps2 = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), ProcClass.Works, plan.getCoacode());
                        BigDecimal tDecimal = BigDecimal.ZERO;
                        if (ps2 != null) {
                            tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(plan.getBudget(), ps2.getProcClass(), ps2.getCoa());
                        }
                        if (ps2 != null) {
                            ps2.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Works, tDecimal));
                            ProcurementPlanService.save(ps2);
                        } else {
                            tDecimal = generatesumofMonths(plan);
                            ProcurementPlan pr = new ProcurementPlan();
                            pr.setSubject(plan.getCoacode().getName());
                            pr.setBudget(plan.getBudget()); // Assuming all selected plans have the same budget
                            pr.setCoa(plan.getCoacode()); // Assuming all selected plans have the same coa
                            // pr.setProcPlanBudgetItems(new HashSet<>(commonItems));
                            pr.setCurrency(plan.getCurrency()); // Assuming all selected plans have the same currency
                            // pr.setFundsource(plan.getFundsource()); // Assuming all selected plans have the same fund source
                            pr.setProcClass(ProcClass.Works);
                            pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Works, tDecimal));

                            ProcurementPlanService.save(pr);
                        }

                    }

                    refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                    gridBudgetItems.deselectAll();
                    Set<ProcurementPlan> selected2 = grid.asMultiSelect().getSelectedItems();
                    setgridBudgetItemsData(selected2);
                }
            }));
            add(new Hr());
            addItem("Combine Items", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().size() < 2) {
                    Notificationwarning("You should select more than one Procurement plan Item to Combine");
                } else {
                    if (!target.asMultiSelect().isEmpty()) {
                        if (!procClassCombo.isEmpty() && !budget.isEmpty()) {

                            if (procClassCombo.getValue() == ProcClass.Consultancy) {
                                Dialog dialogB = new Dialog();
                                dialogB.setModal(false);
                                dialogB.setDraggable(true);
                                dialogB.setResizable(true);
                                dialogB.setWidth("800px");
                                Div editorLayoutDiv = new Div();
                                editorLayoutDiv.setClassName("user-editor-layout");
                                Button closeButton = new Button(new Icon("lumo", "cross"), (ex) -> {
                                    dialogB.close();
                                });
                                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                                dialogB.getHeader().add(closeButton);
                                dialogB.setHeaderTitle("Combined Procurement Item Details");

                                Div editorDiv = new Div();
                                editorDiv.setClassName("user-editor");
                                editorLayoutDiv.add(editorDiv);

                                FormLayout formLayout = new FormLayout();

                                TextField subject2 = new TextField("Subject of Procurement");
                                ComboBox<Currency> currency2 = new ComboBox("Currency");
                                ComboBox<Fundsource> fundsource2 = new ComboBox("Source of Funds");
                                ComboBox<ProcurementMethod> procMethod10Box = new ComboBox("Procurement Method");
                                ComboBox<ProcurementType> contractType2 = new ComboBox("Contract Type");
                                contractType2.setItemLabelGenerator(ProcurementType::getProcuremntType);
                                contractType2.setItems(sampleProcurementTypeService.getAllProcurementTypes());
                                BigDecimalField cost2 = new BigDecimalField("Threshold");
                                ComboBox<Budget> budget2 = new ComboBox("Budget");
                                ComboBox<ProcClass> procClassCombo2 = new ComboBox("Procurement Class");
                                //Checkbox preq2 = new Checkbox("Prequalification");
                                Checkbox reserv2 = new Checkbox("Apply Reservation Scheme ");
                                DatePicker bid_inv2 = new DatePicker("Invitation of Expressions of Interest date");
                                DatePicker req_close_open_date = new DatePicker("Closing - Opening date");
                                DatePicker req_aprroval_of__shortlist = new DatePicker("Approval of shortlist");
                                DatePicker req_notification_date = new DatePicker("Notification Date");
                                DatePicker inv_proposal_date = new DatePicker("Invitation of proposals date");
                                DatePicker submission_open_date = new DatePicker("Contract Signing Date");
                                DatePicker approval_of_final_evaluation_report_date = new DatePicker("Approval Of Final Evaluation Report Date");
                                DatePicker inv_notification_date = new DatePicker("Notification Date");
                                DatePicker contract_signing_date = new DatePicker("Contract signing date");
                                DatePicker completion_date = new DatePicker("Completion Date");

                                subject2.setRequired(true);
                                subject2.setRequiredIndicatorVisible(true);
                                subject2.setErrorMessage("Subject is Required");
                                subject2.setClearButtonVisible(true);

                                cost2.setRequired(true);
                                cost2.setRequiredIndicatorVisible(true);
                                cost2.setErrorMessage("Fund Source is Required");
                                cost2.setClearButtonVisible(true);

                                procMethod10Box.setClearButtonVisible(true);
                                procMethod10Box.setRequired(true);
                                procMethod10Box.setRequiredIndicatorVisible(true);
                                procMethod10Box.setErrorMessage("Procurement Method is Required");
                                procMethod10Box.setItems(ProcurementMethodService.getAllProcurementMethods());

                                contractType2.setRequired(true);
                                contractType2.setRequiredIndicatorVisible(true);
                                contractType2.setErrorMessage("Contact Type is Required");

                                Html span1 = new Html("<b>Request for Expression of Interest</b>");
                                Hr hr1 = new Hr();

                                Html span2 = new Html("<b>Invitation of proposals and approval for award</b>");
                                Hr hr2 = new Hr();
                                Hr hr3 = new Hr();

                                formLayout.add(subject2,
                                        cost2,
                                        procMethod10Box,
                                        reserv2,
                                        contractType2,
                                        hr1,
                                        span1,
                                        bid_inv2,
                                        req_close_open_date,
                                        req_aprroval_of__shortlist,
                                        req_notification_date,
                                        hr2,
                                        span2,
                                        inv_proposal_date,
                                        submission_open_date,
                                        approval_of_final_evaluation_report_date,
                                        inv_notification_date,
                                        contract_signing_date,
                                        hr3,
                                        completion_date);
                                formLayout.setResponsiveSteps(
                                        new FormLayout.ResponsiveStep("0px", 1),
                                        new FormLayout.ResponsiveStep("600px", 2),
                                        new FormLayout.ResponsiveStep("900px", 3)
                                );
                                formLayout.setColspan(span1, 3);
                                formLayout.setColspan(span2, 3);
                                formLayout.setColspan(hr1, 3);
                                formLayout.setColspan(hr2, 3);
                                formLayout.setColspan(hr3, 3);

                                HorizontalLayout lay = new HorizontalLayout();
                                HorizontalLayout lay2 = new HorizontalLayout();
                                Grid<BudgetItems> gridPlan = new Grid<>(BudgetItems.class, false);
                                //Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(BudgetItems::getJul).setHeader("Jul").setWidth("30px");
                                Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJul()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJul() != null && !urcActivity.getJul().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJul().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jul").setWidth("30px");
                                // Set the background color based on the value using CSS classes
                                julColumn.setClassNameGenerator(item -> {
                                    if (item.getJul() != null && item.getJul().doubleValue() > 0) {
                                        return "positive-value";
                                    } else {
                                        return "negative-value";
                                    }
                                });
                                //Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(BudgetItems::getAug).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getAug()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getAug() != null && !urcActivity.getAug().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getAug().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> sepColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getSep()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getSep() != null && !urcActivity.getSep().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getSep().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Sep").setWidth("30px");
                                Grid.Column<BudgetItems> octColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getOct()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getOct() != null && !urcActivity.getOct().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getOct().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Oct").setWidth("30px");
                                Grid.Column<BudgetItems> novColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getNov()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getNov() != null && !urcActivity.getNov().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getNov().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Nov").setWidth("30px");
                                Grid.Column<BudgetItems> decColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getDec()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getDec() != null && !urcActivity.getDec().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getDec().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Dec").setWidth("30px");
                                Grid.Column<BudgetItems> janColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJan()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJan() != null && !urcActivity.getJan().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJan().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jan").setWidth("30px");
                                Grid.Column<BudgetItems> febColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getFeb()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getFeb() != null && !urcActivity.getFeb().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getFeb().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Feb").setWidth("30px");
                                Grid.Column<BudgetItems> marColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMar()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMar() != null && !urcActivity.getMar().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMar().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Mar").setWidth("30px");
                                Grid.Column<BudgetItems> aprColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getApr()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getApr() != null && !urcActivity.getApr().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getApr().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Apr").setWidth("30px");
                                Grid.Column<BudgetItems> mayColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMay()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMay() != null && !urcActivity.getMay().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMay().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("May").setWidth("30px");
                                Grid.Column<BudgetItems> junColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJun()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJun() != null && !urcActivity.getJun().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJun().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jun").setWidth("30px");

                                HeaderRow headerRow = gridPlan.prependHeaderRow();
                                headerRow.join(julColumn, augColumn, sepColumn).setText("QTR 1");
                                headerRow.join(octColumn, novColumn, decColumn).setText("QTR 2");
                                headerRow.join(janColumn, febColumn, marColumn).setText("QTR 3");
                                headerRow.join(aprColumn, mayColumn, junColumn).setText("QTR 4");
                                gridPlan.setHeight("100px");
                                gridPlan.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
                                lay.add(gridPlan);
                                lay.setWidthFull();
                                editorDiv.add(formLayout, lay, lay2);

                                formLayout.setColspan(lay, 3);
                                formLayout.setColspan(lay2, 3);

                                dialogB.add(editorLayoutDiv);
                                /*                                Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                                for (ProcurementPlan pp : p) {
                                
                                listBudgetItems.addAll(pp.getProcPlanBudgetItems().stream().toList());
                                
                                }*/
                                gridPlan.setItems(generatesumofMonthly(listBudgetItems));
                                Button savep = new Button("Combine");
                                Button cancelButton = new Button("Cancel", ex -> dialogB.close());
                                //savep.setDisableOnClick(true);
                                /*                                Set<ProcurementPlan> selectedPlans2 = target.asMultiSelect().getSelectedItems();
                                
                                if (!selectedPlans2.isEmpty()) {
                                // Get the COA of the first procurement plan
                                COA firstCOA = selectedPlans2.iterator().next().getCoa();
                                subject2.setValue(firstCOA.getName());
                                }*/
                                savep.addClickListener(ex -> {
                                    cancelButton.setVisible(false);
                                    //dialogB.setEnabled(false);
                                    if (!subject2.getValue().isEmpty()) { // This check should be subject2.getValue().isEmpty() instead of subject2.isEmpty()
                                        Set<ProcurementPlan> selectedPlans = null;// target.asMultiSelect().getSelectedItems();

                                        if (!selectedPlans.isEmpty()) {
                                            // Get the COA of the first procurement plan
                                            COA firstCOA = selectedPlans.iterator().next().getCoa();

                                            // Check if all selected plans have the same COA
                                            boolean allSameCOA = selectedPlans.stream().allMatch(plan -> plan.getCoa().equals(firstCOA));
                                            if (allSameCOA) {
                                                List<BudgetItems> allBudgetItems = new ArrayList<>();
                                                BigDecimal totalCost = BigDecimal.ZERO;

                                                for (ProcurementPlan pp : selectedPlans) {
                                                    //allBudgetItems.addAll(pp.getProcPlanBudgetItems());
                                                    totalCost = totalCost.add(pp.getCost());
                                                    ProcurementPlanService.deleteProcurementPlan(pp);
                                                }

                                                ProcurementPlan pr = new ProcurementPlan();
                                                pr.setSubject(subject2.getValue());
                                                pr.setBudget(selectedPlans.iterator().next().getBudget()); // Assuming all selected plans have the same budget
                                                pr.setCoa(selectedPlans.iterator().next().getCoa()); // Assuming all selected plans have the same coa
                                                //pr.setProcPlanBudgetItems(new HashSet<>(allBudgetItems));
                                                pr.setCurrency(selectedPlans.iterator().next().getCurrency()); // Assuming all selected plans have the same currency
                                                //pr.setFundsource(selectedPlans.iterator().next().getFundsource()); // Assuming all selected plans have the same fund source
                                                pr.setProcurementMethod(procMethod10Box.getValue());
                                                pr.setProcurementtype(contractType2.getValue());
                                                pr.setReqInviofExpofInterestdate(bid_inv2.getValue());
                                                pr.setReqClosingOpeningdate(req_close_open_date.getValue());
                                                pr.setReqApprovalOfShortlist(req_aprroval_of__shortlist.getValue());
                                                pr.setReqNotificationdate(req_notification_date.getValue());
                                                pr.setInvitationofProposalsdate(inv_proposal_date.getValue());
                                                pr.setSubmissionOpeningdate(submission_open_date.getValue());
                                                pr.setApprovaloffinalevaluationreport(approval_of_final_evaluation_report_date.getValue());
                                                pr.setInvNotificationdate(inv_notification_date.getValue());
                                                pr.setContractsigningdate(contract_signing_date.getValue());
                                                pr.setBcompletion(completion_date.getValue());

                                                pr.setCost(totalCost); // Set total cost calculated from selected plans
                                                pr.setProcClass(procClassCombo.getValue());
                                                //pr.setPrequal(preq2.getValue());
                                                pr.setReserve(reserv2.getValue());

                                                ProcurementPlan savedPlan = ProcurementPlanService.save(pr);
                                                refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                                                //dialogB.setEnabled(true); // Disabling the dialog here
                                                dialogB.close();
                                            } else {
                                                Notificationwarning("Selected Items should have the same Account Code");
                                            }

                                        }
                                    } else {
                                        Notificationwarning("Ensure that the description is not empty");
                                    }
                                });

                                procMethod10Box.setValue(ProcurementPlanService.getProcurementMethodList2(procClassCombo.getValue(), generatesumofMonthsFromList(listBudgetItems)));
                                savep.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                cost2.setValue(generatesumofMonthsFromList(listBudgetItems));

                                dialogB.getFooter().add(cancelButton);
                                dialogB.getFooter().add(savep);
                                dialogB.open();
                            }
                        }

                    }
                }

            }));

            add(new Hr());

        }

    }

    private class BudgetItemsGridContextMenu2 extends GridContextMenu<BudgetItems> {

        public BudgetItemsGridContextMenu2(Grid<BudgetItems> target) {
            super(target);

            add(new Hr());
            addItem("Transfer to Consultancy", e -> e.getItem().ifPresent(person -> {
                if (target.asMultiSelect().isEmpty()) {
                    Notificationwarning("You should select a Budget Item to Transfer");
                } else {

                    Set<BudgetItems> selectedBudgetItems = target.getSelectedItems();
                    for (BudgetItems plan : selectedBudgetItems) {
                        ProcClass old = plan.getProcClass();
                        plan.setProcClass(ProcClass.Consultancy);
                        budgetItemsService.update(plan);
//Update the old procurement plan
                        ProcurementPlan ps = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), old, plan.getCoacode());
                        if (ps != null) {
                            BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(ps.getBudget(), ps.getProcClass(), ps.getCoa());
                            if (tDecimal != null && tDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                ps.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(old, tDecimal));
                                ProcurementPlanService.save(ps);
                            } else {
                                ProcurementPlanService.deleteProcurementPlan(ps);
                            }
                        }
                        //update transferred
                        ProcurementPlan ps2 = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), ProcClass.Consultancy, plan.getCoacode());
                        BigDecimal tDecimal = BigDecimal.ZERO;
                        if (ps2 != null) {
                            tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(plan.getBudget(), ps2.getProcClass(), ps2.getCoa());
                        }
                        if (ps2 != null) {
                            ps2.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Consultancy, tDecimal));
                            ProcurementPlanService.save(ps2);
                        } else {
                            tDecimal = generatesumofMonths(plan);
                            ProcurementPlan pr = new ProcurementPlan();
                            pr.setSubject(plan.getCoacode().getName());
                            pr.setBudget(plan.getBudget()); // Assuming all selected plans have the same budget
                            pr.setCoa(plan.getCoacode()); // Assuming all selected plans have the same coa
                            // pr.setProcPlanBudgetItems(new HashSet<>(commonItems));
                            pr.setCurrency(plan.getCurrency()); // Assuming all selected plans have the same currency
                            // pr.setFundsource(plan.getFundsource()); // Assuming all selected plans have the same fund source
                            pr.setProcClass(ProcClass.Consultancy);
                            pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Consultancy, tDecimal));

                            ProcurementPlanService.save(pr);
                        }

                    }

                    refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                    gridBudgetItems2.deselectAll();
                    Set<ProcurementPlan> selected2 = grid2.asMultiSelect().getSelectedItems();
                    setgridBudgetItemsData2(selected2);
                }
            }));

            add(new Hr());
            addItem("Transfer to Non Consultancy", e -> e.getItem().ifPresent(person -> {
                if (target.asMultiSelect().isEmpty()) {
                    Notificationwarning("You should select a Budget Item to Transfer");
                } else {

                    Set<BudgetItems> selectedBudgetItems = target.getSelectedItems();
                    for (BudgetItems plan : selectedBudgetItems) {
                        ProcClass old = plan.getProcClass();
                        plan.setProcClass(ProcClass.Non_Consultancy);
                        budgetItemsService.update(plan);
//Update the old procurement plan
                        ProcurementPlan ps = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), old, plan.getCoacode());
                        if (ps != null) {
                            BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(ps.getBudget(), ps.getProcClass(), ps.getCoa());
                            if (tDecimal != null && tDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                ps.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(old, tDecimal));
                                ProcurementPlanService.save(ps);
                            } else {
                                ProcurementPlanService.deleteProcurementPlan(ps);
                            }
                        }
                        //update transferred
                        ProcurementPlan ps2 = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), ProcClass.Non_Consultancy, plan.getCoacode());
                        BigDecimal tDecimal = BigDecimal.ZERO;
                        if (ps2 != null) {
                            tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(plan.getBudget(), ps2.getProcClass(), ps2.getCoa());
                        }
                        if (ps2 != null) {
                            ps2.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Non_Consultancy, tDecimal));
                            ProcurementPlanService.save(ps2);
                        } else {
                            tDecimal = generatesumofMonths(plan);
                            ProcurementPlan pr = new ProcurementPlan();
                            pr.setSubject(plan.getCoacode().getName());
                            pr.setBudget(plan.getBudget()); // Assuming all selected plans have the same budget
                            pr.setCoa(plan.getCoacode()); // Assuming all selected plans have the same coa
                            // pr.setProcPlanBudgetItems(new HashSet<>(commonItems));
                            pr.setCurrency(plan.getCurrency()); // Assuming all selected plans have the same currency
                            // pr.setFundsource(plan.getFundsource()); // Assuming all selected plans have the same fund source
                            pr.setProcClass(ProcClass.Non_Consultancy);
                            pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Non_Consultancy, tDecimal));

                            ProcurementPlanService.save(pr);
                        }

                    }

                    refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                    gridBudgetItems2.deselectAll();
                    Set<ProcurementPlan> selected2 = grid2.asMultiSelect().getSelectedItems();
                    setgridBudgetItemsData2(selected2);
                }
            }));
            add(new Hr());
            addItem("Transfer to Supplies", e -> e.getItem().ifPresent(person -> {
                if (target.asMultiSelect().isEmpty()) {
                    Notificationwarning("You should select a Budget Item to Transfer");
                } else {

                    Set<BudgetItems> selectedBudgetItems = target.getSelectedItems();
                    for (BudgetItems plan : selectedBudgetItems) {
                        ProcClass old = plan.getProcClass();
                        plan.setProcClass(ProcClass.Supplies);
                        budgetItemsService.update(plan);
//Update the old procurement plan
                        ProcurementPlan ps = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), old, plan.getCoacode());
                        if (ps != null) {
                            BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(ps.getBudget(), ps.getProcClass(), ps.getCoa());
                            if (tDecimal != null && tDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                ps.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(old, tDecimal));
                                ProcurementPlanService.save(ps);
                            } else {
                                ProcurementPlanService.deleteProcurementPlan(ps);
                            }
                        }
                        //update transferred
                        ProcurementPlan ps2 = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), ProcClass.Supplies, plan.getCoacode());
                        BigDecimal tDecimal = BigDecimal.ZERO;
                        if (ps2 != null) {
                            tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(plan.getBudget(), ps2.getProcClass(), ps2.getCoa());
                        }
                        if (ps2 != null) {
                            ps2.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Supplies, tDecimal));
                            ProcurementPlanService.save(ps2);
                        } else {
                            tDecimal = generatesumofMonths(plan);
                            ProcurementPlan pr = new ProcurementPlan();
                            pr.setSubject(plan.getCoacode().getName());
                            pr.setBudget(plan.getBudget()); // Assuming all selected plans have the same budget
                            pr.setCoa(plan.getCoacode()); // Assuming all selected plans have the same coa
                            // pr.setProcPlanBudgetItems(new HashSet<>(commonItems));
                            pr.setCurrency(plan.getCurrency()); // Assuming all selected plans have the same currency
                            // pr.setFundsource(plan.getFundsource()); // Assuming all selected plans have the same fund source
                            pr.setProcClass(ProcClass.Supplies);
                            pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Supplies, tDecimal));

                            ProcurementPlanService.save(pr);
                        }

                    }

                    refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                    gridBudgetItems2.deselectAll();
                    Set<ProcurementPlan> selected2 = grid2.asMultiSelect().getSelectedItems();
                    setgridBudgetItemsData2(selected2);
                }
            }));
            add(new Hr());
            addItem("Transfer to Works", e -> e.getItem().ifPresent(person -> {
                if (target.asMultiSelect().isEmpty()) {
                    Notificationwarning("You should select a Budget Item to Transfer");
                } else {

                    Set<BudgetItems> selectedBudgetItems = target.getSelectedItems();
                    for (BudgetItems plan : selectedBudgetItems) {
                        ProcClass old = plan.getProcClass();
                        plan.setProcClass(ProcClass.Works);
                        budgetItemsService.update(plan);
//Update the old procurement plan
                        ProcurementPlan ps = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), old, plan.getCoacode());
                        if (ps != null) {
                            BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(ps.getBudget(), ps.getProcClass(), ps.getCoa());
                            if (tDecimal != null && tDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                ps.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(old, tDecimal));
                                ProcurementPlanService.save(ps);
                            } else {
                                ProcurementPlanService.deleteProcurementPlan(ps);
                            }
                        }
                        //update transferred
                        ProcurementPlan ps2 = ProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(plan.getBudget(), ProcClass.Works, plan.getCoacode());
                        BigDecimal tDecimal = BigDecimal.ZERO;
                        if (ps2 != null) {
                            tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(plan.getBudget(), ps2.getProcClass(), ps2.getCoa());
                        }
                        if (ps2 != null) {
                            ps2.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Works, tDecimal));
                            ProcurementPlanService.save(ps2);
                        } else {
                            tDecimal = generatesumofMonths(plan);
                            ProcurementPlan pr = new ProcurementPlan();
                            pr.setSubject(plan.getCoacode().getName());
                            pr.setBudget(plan.getBudget()); // Assuming all selected plans have the same budget
                            pr.setCoa(plan.getCoacode()); // Assuming all selected plans have the same coa
                            // pr.setProcPlanBudgetItems(new HashSet<>(commonItems));
                            pr.setCurrency(plan.getCurrency()); // Assuming all selected plans have the same currency
                            // pr.setFundsource(plan.getFundsource()); // Assuming all selected plans have the same fund source
                            pr.setProcClass(ProcClass.Works);
                            pr.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(ProcClass.Works, tDecimal));

                            ProcurementPlanService.save(pr);
                        }

                    }

                    refreshgridProcurementPlan2(procClassCombo2.getValue(), comboBoxD_Section2.getSelectedItems());
                    gridBudgetItems2.deselectAll();
                    Set<ProcurementPlan> selected2 = grid2.asMultiSelect().getSelectedItems();
                    setgridBudgetItemsData2(selected2);
                }
            }));
            add(new Hr());
            addItem("Combine Items", e -> e.getItem().ifPresent(person -> {
                List<BudgetItems> listBudgetItems = new ArrayList<>();
                if (target.asMultiSelect().getSelectedItems().size() < 2) {
                    Notificationwarning("You should select more than one Procurement plan Item to Combine");
                } else {
                    if (!target.asMultiSelect().isEmpty()) {
                        if (!procClassCombo.isEmpty() && !budget.isEmpty()) {

                            if (procClassCombo.getValue() == ProcClass.Consultancy) {
                                Dialog dialogB = new Dialog();
                                dialogB.setModal(false);
                                dialogB.setDraggable(true);
                                dialogB.setResizable(true);
                                dialogB.setWidth("800px");
                                Div editorLayoutDiv = new Div();
                                editorLayoutDiv.setClassName("user-editor-layout");
                                Button closeButton = new Button(new Icon("lumo", "cross"), (ex) -> {
                                    dialogB.close();
                                });
                                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                                dialogB.getHeader().add(closeButton);
                                dialogB.setHeaderTitle("Combined Procurement Item Details");

                                Div editorDiv = new Div();
                                editorDiv.setClassName("user-editor");
                                editorLayoutDiv.add(editorDiv);

                                FormLayout formLayout = new FormLayout();

                                TextField subject2 = new TextField("Subject of Procurement");
                                ComboBox<Currency> currency2 = new ComboBox("Currency");
                                ComboBox<Fundsource> fundsource2 = new ComboBox("Source of Funds");
                                ComboBox<ProcurementMethod> procMethod10Box = new ComboBox("Procurement Method");
                                ComboBox<ProcurementType> contractType2 = new ComboBox("Contract Type");
                                contractType2.setItemLabelGenerator(ProcurementType::getProcuremntType);
                                contractType2.setItems(sampleProcurementTypeService.getAllProcurementTypes());
                                BigDecimalField cost2 = new BigDecimalField("Threshold");
                                ComboBox<Budget> budget2 = new ComboBox("Budget");
                                ComboBox<ProcClass> procClassCombo2 = new ComboBox("Procurement Class");
                                //Checkbox preq2 = new Checkbox("Prequalification");
                                Checkbox reserv2 = new Checkbox("Apply Reservation Scheme ");
                                DatePicker bid_inv2 = new DatePicker("Invitation of Expressions of Interest date");
                                DatePicker req_close_open_date = new DatePicker("Closing - Opening date");
                                DatePicker req_aprroval_of__shortlist = new DatePicker("Approval of shortlist");
                                DatePicker req_notification_date = new DatePicker("Notification Date");
                                DatePicker inv_proposal_date = new DatePicker("Invitation of proposals date");
                                DatePicker submission_open_date = new DatePicker("Contract Signing Date");
                                DatePicker approval_of_final_evaluation_report_date = new DatePicker("Approval Of Final Evaluation Report Date");
                                DatePicker inv_notification_date = new DatePicker("Notification Date");
                                DatePicker contract_signing_date = new DatePicker("Contract signing date");
                                DatePicker completion_date = new DatePicker("Completion Date");

                                subject2.setRequired(true);
                                subject2.setRequiredIndicatorVisible(true);
                                subject2.setErrorMessage("Subject is Required");
                                subject2.setClearButtonVisible(true);

                                cost2.setRequired(true);
                                cost2.setRequiredIndicatorVisible(true);
                                cost2.setErrorMessage("Fund Source is Required");
                                cost2.setClearButtonVisible(true);

                                procMethod10Box.setClearButtonVisible(true);
                                procMethod10Box.setRequired(true);
                                procMethod10Box.setRequiredIndicatorVisible(true);
                                procMethod10Box.setErrorMessage("Procurement Method is Required");
                                procMethod10Box.setItems(ProcurementMethodService.getAllProcurementMethods());

                                contractType2.setRequired(true);
                                contractType2.setRequiredIndicatorVisible(true);
                                contractType2.setErrorMessage("Contact Type is Required");

                                Html span1 = new Html("<b>Request for Expression of Interest</b>");
                                Hr hr1 = new Hr();

                                Html span2 = new Html("<b>Invitation of proposals and approval for award</b>");
                                Hr hr2 = new Hr();
                                Hr hr3 = new Hr();

                                formLayout.add(subject2,
                                        cost2,
                                        procMethod10Box,
                                        reserv2,
                                        contractType2,
                                        hr1,
                                        span1,
                                        bid_inv2,
                                        req_close_open_date,
                                        req_aprroval_of__shortlist,
                                        req_notification_date,
                                        hr2,
                                        span2,
                                        inv_proposal_date,
                                        submission_open_date,
                                        approval_of_final_evaluation_report_date,
                                        inv_notification_date,
                                        contract_signing_date,
                                        hr3,
                                        completion_date);
                                formLayout.setResponsiveSteps(
                                        new FormLayout.ResponsiveStep("0px", 1),
                                        new FormLayout.ResponsiveStep("600px", 2),
                                        new FormLayout.ResponsiveStep("900px", 3)
                                );
                                formLayout.setColspan(span1, 3);
                                formLayout.setColspan(span2, 3);
                                formLayout.setColspan(hr1, 3);
                                formLayout.setColspan(hr2, 3);
                                formLayout.setColspan(hr3, 3);

                                HorizontalLayout lay = new HorizontalLayout();
                                HorizontalLayout lay2 = new HorizontalLayout();
                                Grid<BudgetItems> gridPlan = new Grid<>(BudgetItems.class, false);
                                //Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(BudgetItems::getJul).setHeader("Jul").setWidth("30px");
                                Grid.Column<BudgetItems> julColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJul()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJul() != null && !urcActivity.getJul().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJul().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jul").setWidth("30px");
                                // Set the background color based on the value using CSS classes
                                julColumn.setClassNameGenerator(item -> {
                                    if (item.getJul() != null && item.getJul().doubleValue() > 0) {
                                        return "positive-value";
                                    } else {
                                        return "negative-value";
                                    }
                                });
                                //Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(BudgetItems::getAug).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> augColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getAug()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getAug() != null && !urcActivity.getAug().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getAug().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Aug").setWidth("30px");
                                Grid.Column<BudgetItems> sepColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getSep()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getSep() != null && !urcActivity.getSep().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getSep().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Sep").setWidth("30px");
                                Grid.Column<BudgetItems> octColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getOct()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getOct() != null && !urcActivity.getOct().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getOct().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Oct").setWidth("30px");
                                Grid.Column<BudgetItems> novColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getNov()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getNov() != null && !urcActivity.getNov().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getNov().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Nov").setWidth("30px");
                                Grid.Column<BudgetItems> decColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getDec()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getDec() != null && !urcActivity.getDec().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getDec().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Dec").setWidth("30px");
                                Grid.Column<BudgetItems> janColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJan()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJan() != null && !urcActivity.getJan().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJan().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jan").setWidth("30px");
                                Grid.Column<BudgetItems> febColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getFeb()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getFeb() != null && !urcActivity.getFeb().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getFeb().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Feb").setWidth("30px");
                                Grid.Column<BudgetItems> marColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMar()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMar() != null && !urcActivity.getMar().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMar().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Mar").setWidth("30px");
                                Grid.Column<BudgetItems> aprColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getApr()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getApr() != null && !urcActivity.getApr().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getApr().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Apr").setWidth("30px");
                                Grid.Column<BudgetItems> mayColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getMay()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getMay() != null && !urcActivity.getMay().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getMay().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("May").setWidth("30px");
                                Grid.Column<BudgetItems> junColumn = gridPlan.addColumn(new ComponentRenderer<>(urcActivity -> {
                                    Span span = new Span(decimalFormat.format(urcActivity.getJun()) + "");
                                    span.setSizeFull();
                                    if (urcActivity.getJun() != null && !urcActivity.getJun().equals(BigDecimal.ZERO)) {

                                        if (urcActivity.getJun().doubleValue() > 0) {
                                            //span.getElement().setAttribute("theme", "success");
                                            span.getElement().getThemeList().add("badge success");
                                            //julColumn.getStyle().set("--vaadin-grid-cell-background", "");
                                        } else {
                                            span.getElement().getThemeList().add("badge error");
                                        }
                                        return span;
                                    } else {
                                        span.getElement().getThemeList().add("badge error");
                                        return span; // Return an empty Span if deptSection is null
                                    }
                                })).setHeader("Jun").setWidth("30px");

                                HeaderRow headerRow = gridPlan.prependHeaderRow();
                                headerRow.join(julColumn, augColumn, sepColumn).setText("QTR 1");
                                headerRow.join(octColumn, novColumn, decColumn).setText("QTR 2");
                                headerRow.join(janColumn, febColumn, marColumn).setText("QTR 3");
                                headerRow.join(aprColumn, mayColumn, junColumn).setText("QTR 4");
                                gridPlan.setHeight("100px");
                                gridPlan.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
                                lay.add(gridPlan);
                                lay.setWidthFull();
                                editorDiv.add(formLayout, lay, lay2);

                                formLayout.setColspan(lay, 3);
                                formLayout.setColspan(lay2, 3);

                                dialogB.add(editorLayoutDiv);
                                /*                                Set<ProcurementPlan> p = target.asMultiSelect().getSelectedItems();
                                for (ProcurementPlan pp : p) {
                                
                                listBudgetItems.addAll(pp.getProcPlanBudgetItems().stream().toList());
                                
                                }*/
                                gridPlan.setItems(generatesumofMonthly(listBudgetItems));
                                Button savep = new Button("Combine");
                                Button cancelButton = new Button("Cancel", ex -> dialogB.close());
                                //savep.setDisableOnClick(true);
                                /*                                Set<ProcurementPlan> selectedPlans2 = target.asMultiSelect().getSelectedItems();
                                
                                if (!selectedPlans2.isEmpty()) {
                                // Get the COA of the first procurement plan
                                COA firstCOA = selectedPlans2.iterator().next().getCoa();
                                subject2.setValue(firstCOA.getName());
                                }*/
                                savep.addClickListener(ex -> {
                                    cancelButton.setVisible(false);
                                    //dialogB.setEnabled(false);
                                    if (!subject2.getValue().isEmpty()) { // This check should be subject2.getValue().isEmpty() instead of subject2.isEmpty()
                                        Set<ProcurementPlan> selectedPlans = null;// target.asMultiSelect().getSelectedItems();

                                        if (!selectedPlans.isEmpty()) {
                                            // Get the COA of the first procurement plan
                                            COA firstCOA = selectedPlans.iterator().next().getCoa();

                                            // Check if all selected plans have the same COA
                                            boolean allSameCOA = selectedPlans.stream().allMatch(plan -> plan.getCoa().equals(firstCOA));
                                            if (allSameCOA) {
                                                List<BudgetItems> allBudgetItems = new ArrayList<>();
                                                BigDecimal totalCost = BigDecimal.ZERO;

                                                for (ProcurementPlan pp : selectedPlans) {
                                                    // allBudgetItems.addAll(pp.getProcPlanBudgetItems());
                                                    totalCost = totalCost.add(pp.getCost());
                                                    ProcurementPlanService.deleteProcurementPlan(pp);
                                                }

                                                ProcurementPlan pr = new ProcurementPlan();
                                                pr.setSubject(subject2.getValue());
                                                pr.setBudget(selectedPlans.iterator().next().getBudget()); // Assuming all selected plans have the same budget
                                                pr.setCoa(selectedPlans.iterator().next().getCoa()); // Assuming all selected plans have the same coa
                                                // pr.setProcPlanBudgetItems(new HashSet<>(allBudgetItems));
                                                pr.setCurrency(selectedPlans.iterator().next().getCurrency()); // Assuming all selected plans have the same currency
                                                //pr.setFundsource(selectedPlans.iterator().next().getFundsource()); // Assuming all selected plans have the same fund source
                                                pr.setProcurementMethod(procMethod10Box.getValue());
                                                pr.setProcurementtype(contractType2.getValue());
                                                pr.setReqInviofExpofInterestdate(bid_inv2.getValue());
                                                pr.setReqClosingOpeningdate(req_close_open_date.getValue());
                                                pr.setReqApprovalOfShortlist(req_aprroval_of__shortlist.getValue());
                                                pr.setReqNotificationdate(req_notification_date.getValue());
                                                pr.setInvitationofProposalsdate(inv_proposal_date.getValue());
                                                pr.setSubmissionOpeningdate(submission_open_date.getValue());
                                                pr.setApprovaloffinalevaluationreport(approval_of_final_evaluation_report_date.getValue());
                                                pr.setInvNotificationdate(inv_notification_date.getValue());
                                                pr.setContractsigningdate(contract_signing_date.getValue());
                                                pr.setBcompletion(completion_date.getValue());

                                                pr.setCost(totalCost); // Set total cost calculated from selected plans
                                                pr.setProcClass(procClassCombo.getValue());
                                                //pr.setPrequal(preq2.getValue());
                                                pr.setReserve(reserv2.getValue());

                                                ProcurementPlan savedPlan = ProcurementPlanService.save(pr);
                                                refreshgridProcurementPlan(procClassCombo.getValue(), comboBoxD_Section.getSelectedItems());
                                                //dialogB.setEnabled(true); // Disabling the dialog here
                                                dialogB.close();
                                            } else {
                                                Notificationwarning("Selected Items should have the same Account Code");
                                            }

                                        }
                                    } else {
                                        Notificationwarning("Ensure that the description is not empty");
                                    }
                                });

                                procMethod10Box.setValue(ProcurementPlanService.getProcurementMethodList2(procClassCombo2.getValue(), generatesumofMonthsFromList(listBudgetItems)));
                                savep.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                                cost2.setValue(generatesumofMonthsFromList(listBudgetItems));

                                dialogB.getFooter().add(cancelButton);
                                dialogB.getFooter().add(savep);
                                dialogB.open();
                            }
                        }

                    }
                }

            }));

            add(new Hr());

        }

    }

    private BigDecimal generatesumofProc(List<ProcurementPlan> budgetList) {
        BigDecimal sumofMonths = BigDecimal.ZERO;
        /*        if (comboBoxD_Section.isEmpty()) {
        for (ProcurementPlan bList : budgetList) {
        List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoa(bList.getBudget(), bList.getProcClass(), bList.getCoa());
        sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));
        
        }
        } else {
        for (ProcurementPlan bList : budgetList) {
        List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section.getValue());
        sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));
        
        }
        }*/

        if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            List<ProcurementPlan> plan = new ArrayList<>();
            List<ProcurementPlan> plan1 = new ArrayList<>();
            if (funds.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoa(bList.getBudget(), bList.getProcClass(), bList.getCoa());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), funds.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }

            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget2.getValue(), urcActivity.getProcClass(), user.getDeptsection());
            if (funds.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), user.getDeptsection());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), user.getDeptsection(), funds.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }

            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            if (funds.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }
            // Notification.show(budget.getValue()+" Refreshed "+ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Supplies));
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            if (funds.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }
        }

        return sumofMonths;
    }

    private BigDecimal generatesumofProc2(List<ProcurementPlan> budgetList) {
        BigDecimal sumofMonths = BigDecimal.ZERO;
        /*        if (comboBoxD_Section2.isEmpty()) {
        for (ProcurementPlan bList : budgetList) {
        List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoa(bList.getBudget(), bList.getProcClass(), bList.getCoa());
        sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));
        
        }
        } else {
        for (ProcurementPlan bList : budgetList) {
        List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section.getValue());
        sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));
        
        }
        }*/

        if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            List<ProcurementPlan> plan = new ArrayList<>();
            List<ProcurementPlan> plan1 = new ArrayList<>();
            if (funds2.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoa(bList.getBudget(), bList.getProcClass(), bList.getCoa());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), funds2.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }

        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget2.getValue(), urcActivity.getProcClass(), user.getDeptsection());
            if (funds2.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), user.getDeptsection());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), user.getDeptsection(), funds2.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }

        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            if (funds2.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section2.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }

        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            if (funds2.isEmpty()) {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section2.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            } else {
                for (ProcurementPlan bList : budgetList) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(bList.getBudget(), bList.getProcClass(), bList.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems());
                    sumofMonths = sumofMonths.add(generatesumofMonthsFromList(listBudgetItems));

                }
            }
        }

        return sumofMonths;
    }

    private BigDecimal generatesumofMonths(BudgetItems budget) {
        BigDecimal sumofMonths = BigDecimal.ZERO;

        if (budget.getJan() != null) {
            sumofMonths = sumofMonths.add(budget.getJan());
        }
        if (budget.getFeb() != null) {
            sumofMonths = sumofMonths.add(budget.getFeb());
        }
        if (budget.getMar() != null) {
            sumofMonths = sumofMonths.add(budget.getMar());
        }
        if (budget.getApr() != null) {
            sumofMonths = sumofMonths.add(budget.getApr());
        }
        if (budget.getMay() != null) {
            sumofMonths = sumofMonths.add(budget.getMay());
        }
        if (budget.getJun() != null) {
            sumofMonths = sumofMonths.add(budget.getJun());
        }
        if (budget.getJul() != null) {
            sumofMonths = sumofMonths.add(budget.getJul());
        }
        if (budget.getAug() != null) {
            sumofMonths = sumofMonths.add(budget.getAug());
        }
        if (budget.getSep() != null) {
            sumofMonths = sumofMonths.add(budget.getSep());
        }
        if (budget.getOct() != null) {
            sumofMonths = sumofMonths.add(budget.getOct());
        }
        if (budget.getNov() != null) {
            sumofMonths = sumofMonths.add(budget.getNov());
        }
        if (budget.getDec() != null) {
            sumofMonths = sumofMonths.add(budget.getDec());
        }
        return sumofMonths;
    }

    private BudgetItems generatesumofMonthly(List<BudgetItems> budgetList) {
        BudgetItems budget = new BudgetItems();
        budget.setJan(BigDecimal.ZERO);
        budget.setMay(BigDecimal.ZERO);
        budget.setSep(BigDecimal.ZERO);
        budget.setFeb(BigDecimal.ZERO);
        budget.setJun(BigDecimal.ZERO);
        budget.setOct(BigDecimal.ZERO);
        budget.setMar(BigDecimal.ZERO);
        budget.setJul(BigDecimal.ZERO);
        budget.setNov(BigDecimal.ZERO);
        budget.setApr(BigDecimal.ZERO);
        budget.setAug(BigDecimal.ZERO);
        budget.setDec(BigDecimal.ZERO);
        BigDecimal sumofMonths = BigDecimal.ZERO;
        for (BudgetItems bList : budgetList) {
            if (bList.getJan() != null) {
                sumofMonths = budget.getJan().add(bList.getJan());
                budget.setJan(sumofMonths);
            }
            if (bList.getFeb() != null) {
                sumofMonths = budget.getFeb().add(bList.getFeb());
                budget.setFeb(sumofMonths);
            }
            if (bList.getMar() != null) {
                sumofMonths = budget.getMar().add(bList.getMar());
                budget.setMar(sumofMonths);
            }
            if (bList.getApr() != null) {
                sumofMonths = budget.getApr().add(bList.getApr());
                budget.setApr(sumofMonths);
            }
            if (bList.getMay() != null) {
                sumofMonths = budget.getApr().add(bList.getMay());
                budget.setMay(sumofMonths);
            }
            if (bList.getJun() != null) {
                sumofMonths = budget.getJun().add(bList.getJun());
                budget.setJun(sumofMonths);
            }
            if (bList.getJul() != null) {
                sumofMonths = budget.getJul().add(bList.getJul());
                budget.setJul(sumofMonths);
            }
            if (bList.getAug() != null) {
                sumofMonths = budget.getAug().add(bList.getAug());
                budget.setAug(sumofMonths);
            }
            if (bList.getSep() != null) {
                sumofMonths = budget.getSep().add(bList.getSep());
                budget.setSep(sumofMonths);
            }
            if (bList.getOct() != null) {
                sumofMonths = budget.getOct().add(bList.getOct());
                budget.setOct(sumofMonths);
            }
            if (bList.getNov() != null) {
                sumofMonths = budget.getNov().add(bList.getNov());
                budget.setNov(sumofMonths);
            }
            if (bList.getDec() != null) {
                sumofMonths = budget.getDec().add(bList.getDec());
                budget.setDec(sumofMonths);
            }
        }

        return budget;
    }

    private BigDecimal generatesumofMonthsFromList(List<BudgetItems> budget) {
        BigDecimal sumofMonths = BigDecimal.ZERO;
        for (BudgetItems lis : budget) {

            sumofMonths = sumofMonths.add(generatesumofMonths(lis));
        }

        return sumofMonths;
    }

    private void updateProcurementPlan(Budget budget) {
        List<ProcurementPlan> listP = ProcurementPlanService.findProcurementPlansByBudget(budget);
        for (ProcurementPlan m : listP) {
            List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoa(m.getBudget(), m.getProcClass(), m.getCoa());
            // m.setCost(generatesumofMonthsFromList(listBudgetItems));
            m.setProcurementMethod(ProcurementPlanService.getProcurementMethodList2(m.getProcClass(), generatesumofMonthsFromList(listBudgetItems)));
            Currency cur = sampleCurrencyService.findCurrenciesByCurrencyShortAndBudget("UGX", budget);
            m.setCurrency(cur);
            ProcurementPlanService.save(m);
        }
    }

    private void gridForBudgetItems() {
        gridBudgetItems.setSizeFull();
        gridBudgetItems.addColumn(BudgetItems::getItem).setHeader("Description").setWidth("300px").setFrozen(true).setFooter("TOTAL");

        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {

            BigDecimal total = generatesumofMonths(budget);
            Span span = new Span(decimalFormat.format(total));
            span.getElement().getThemeList().add("badge success");
            return span;
        })).setHeader("Total").setHeader("Total")
                .setKey("totalz")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = generatesumofMonths(budgetItem1);
                    BigDecimal total2 = generatesumofMonths(budgetItem2);
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0).setFrozen(true).setFooter(spanTotal);//.setFooter(spanTotal);
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span(decimalFormat.format(budget.getCost()));

            return span;
        })).setHeader("Rate").setFlexGrow(0).setWidth("100px");

        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span(decimalFormat.format(budget.getQty()));

            return span;
        })).setHeader("Qty").setFlexGrow(0).setWidth("100px");

        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getJul() != null && budget.getJul().intValue() != 0) ? decimalFormat.format(budget.getJul()) : "-");
            if (budget.getJul() != null && budget.getJul().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Jul").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getAug() != null && budget.getAug().intValue() != 0) ? decimalFormat.format(budget.getAug()) : "-");
            if (budget.getAug() != null && budget.getAug().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Aug").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getSep() != null && budget.getSep().intValue() != 0) ? decimalFormat.format(budget.getSep()) : "-");
            if (budget.getSep() != null && budget.getSep().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Sep").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getOct() != null && budget.getOct().intValue() != 0) ? decimalFormat.format(budget.getOct()) : "-");
            if (budget.getOct() != null && budget.getOct().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Oct").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getNov() != null && budget.getNov().intValue() != 0) ? decimalFormat.format(budget.getNov()) : "-");
            if (budget.getNov() != null && budget.getNov().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Nov").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getDec() != null && budget.getDec().intValue() != 0) ? decimalFormat.format(budget.getDec()) : "-");
            if (budget.getDec() != null && budget.getDec().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Dec").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getJan() != null && budget.getJan().intValue() != 0) ? decimalFormat.format(budget.getJan()) : "-");
            if (budget.getJan() != null && budget.getJan().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Jan").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getFeb() != null && budget.getFeb().intValue() != 0) ? decimalFormat.format(budget.getFeb()) : "-");
            if (budget.getFeb() != null && budget.getFeb().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Feb").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getMar() != null && budget.getMar().intValue() != 0) ? decimalFormat.format(budget.getMar()) : "-");
            if (budget.getMar() != null && budget.getMar().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Mar").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getApr() != null && budget.getApr().intValue() != 0) ? decimalFormat.format(budget.getApr()) : "-");
            if (budget.getApr() != null && budget.getApr().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Apr").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getMay() != null && budget.getMay().intValue() != 0) ? decimalFormat.format(budget.getMay()) : "-");
            if (budget.getMay() != null && budget.getMay().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("May").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getJun() != null && budget.getJun().intValue() != 0) ? decimalFormat.format(budget.getJun()) : "-");
            if (budget.getJun() != null && budget.getJun().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Jun").setFlexGrow(0).setWidth("100px");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {

            Span span = new Span(budget.getFundsource().getFundsource());

            return span;
        })).setHeader("Fund Source");
        gridBudgetItems.addColumn(new ComponentRenderer<>(budget -> {

            Span span = new Span(budget.getDeptUnit().getNAME());

            return span;
        })).setHeader("DEPT SECTION").setFlexGrow(0).setWidth("100px");

        gridBudgetItems.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        gridBudgetItems.setWidthFull();
        gridBudgetItems.setSelectionMode(Grid.SelectionMode.MULTI);
        budgetItemsGridContextMenu = new BudgetItemsGridContextMenu(gridBudgetItems);

    }

    private void gridForBudgetItems2() {
        gridBudgetItems2.setSizeFull();
        gridBudgetItems2.addColumn(BudgetItems::getItem).setHeader("Description").setWidth("300px").setFrozen(true).setFooter(spanT2);

        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            BigDecimal total = generatesumofMonths(budget);
            Span span = new Span(decimalFormat.format(total));
            span.getElement().getThemeList().add("badge success");
            return span;
        })).setHeader("Total").setHeader("Total")
                .setKey("totalz")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = generatesumofMonths(budgetItem1);
                    BigDecimal total2 = generatesumofMonths(budgetItem2);
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0).setFrozen(true).setFooter(spanTotal2);
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span(decimalFormat.format(budget.getCost()));

            return span;
        })).setHeader("Rate").setFlexGrow(0).setWidth("100px");

        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span(decimalFormat.format(budget.getQty()));

            return span;
        })).setHeader("Qty").setFlexGrow(0).setWidth("100px");

        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getJul() != null && budget.getJul().intValue() != 0) ? decimalFormat.format(budget.getJul()) : "-");
            if (budget.getJul() != null && budget.getJul().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Jul").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getAug() != null && budget.getAug().intValue() != 0) ? decimalFormat.format(budget.getAug()) : "-");
            if (budget.getAug() != null && budget.getAug().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Aug").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getSep() != null && budget.getSep().intValue() != 0) ? decimalFormat.format(budget.getSep()) : "-");
            if (budget.getSep() != null && budget.getSep().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Sep").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getOct() != null && budget.getOct().intValue() != 0) ? decimalFormat.format(budget.getOct()) : "-");
            if (budget.getOct() != null && budget.getOct().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Oct").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getNov() != null && budget.getNov().intValue() != 0) ? decimalFormat.format(budget.getNov()) : "-");
            if (budget.getNov() != null && budget.getNov().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Nov").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getDec() != null && budget.getDec().intValue() != 0) ? decimalFormat.format(budget.getDec()) : "-");
            if (budget.getDec() != null && budget.getDec().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Dec").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getJan() != null && budget.getJan().intValue() != 0) ? decimalFormat.format(budget.getJan()) : "-");
            if (budget.getJan() != null && budget.getJan().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Jan").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getFeb() != null && budget.getFeb().intValue() != 0) ? decimalFormat.format(budget.getFeb()) : "-");
            if (budget.getFeb() != null && budget.getFeb().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Feb").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getMar() != null && budget.getMar().intValue() != 0) ? decimalFormat.format(budget.getMar()) : "-");
            if (budget.getMar() != null && budget.getMar().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Mar").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getApr() != null && budget.getApr().intValue() != 0) ? decimalFormat.format(budget.getApr()) : "-");
            if (budget.getApr() != null && budget.getApr().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Apr").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getMay() != null && budget.getMay().intValue() != 0) ? decimalFormat.format(budget.getMay()) : "-");
            if (budget.getMay() != null && budget.getMay().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("May").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span((budget.getJun() != null && budget.getJun().intValue() != 0) ? decimalFormat.format(budget.getJun()) : "-");
            if (budget.getJun() != null && budget.getJun().intValue() > 0) {
                span.getElement().getThemeList().add("badge success");
            }

            return span;
        })).setHeader("Jun").setFlexGrow(0).setWidth("100px");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {

            Span span = new Span(budget.getFundsource().getFundsource());

            return span;
        })).setHeader("Fund Source");
        gridBudgetItems2.addColumn(new ComponentRenderer<>(budget -> {
            Span span = new Span(budget.getDeptUnit().getNAME());

            return span;
        })).setHeader("DEPT SECTION").setFlexGrow(0).setWidth("100px");

        gridBudgetItems2.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        gridBudgetItems2.setWidthFull();
        gridBudgetItems2.setSelectionMode(Grid.SelectionMode.MULTI);
        budgetItemsGridContextMenu2 = new BudgetItemsGridContextMenu2(gridBudgetItems2);

    }

    private void setgridBudgetItemsData_1(Set<ProcurementPlan> list) {
        gridBudgetItems.deselectAll();
        List<BudgetItems> listBudgetItems = new ArrayList<>();
        for (ProcurementPlan pp : list) {
            listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));

        }
        gridBudgetItems.setItems(listBudgetItems);
        spanTotal.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
    }

    private void setgridBudgetItemsData2_1(Set<ProcurementPlan> list) {
        gridBudgetItems2.deselectAll();
        List<BudgetItems> listBudgetItems = new ArrayList<>();
        for (ProcurementPlan pp : list) {
            listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));
        }
        gridBudgetItems2.setItems(listBudgetItems);
        spanTotal2.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
    }

    private void setgridBudgetItemsData(Set<ProcurementPlan> list) {

        List<BudgetItems> listBudgetItems = new ArrayList<>();
        gridBudgetItems.deselectAll();
        if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            if (funds.isEmpty()) {
                for (ProcurementPlan pp : list) {
                    listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));
                }
            } else {
                for (ProcurementPlan pp : list) {
                    listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), funds.getValue()));
                }
            }

            gridBudgetItems.setItems(listBudgetItems);
            spanTotal.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            for (ProcurementPlan pp : list) {
                listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), user.getDeptsection()));

            }
            gridBudgetItems.setItems(listBudgetItems);
            spanTotal.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            for (ProcurementPlan pp : list) {
                listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), comboBoxD_Section.getValue()));

            }
            gridBudgetItems.setItems(listBudgetItems);
            spanTotal.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        } else if (budget != null && !budget.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            for (ProcurementPlan pp : list) {
                listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), comboBoxD_Section.getValue()));

            }
            gridBudgetItems.setItems(listBudgetItems);
            spanTotal.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        }

    }

    private void setgridBudgetItemsData2(Set<ProcurementPlan> list) {
        gridBudgetItems2.deselectAll();

        List<BudgetItems> listBudgetItems = new ArrayList<>();
        gridBudgetItems2.deselectAll();
        if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            if (funds2.isEmpty()) {
                for (ProcurementPlan pp : list) {
                    listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(pp.getBudget(), pp.getProcClass(), pp.getCoa()));
                }
            } else {
                for (ProcurementPlan pp : list) {
                    listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), funds2.getValue()));
                }
            }

            gridBudgetItems2.setItems(listBudgetItems);
            spanTotal2.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        } else if (budget != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            for (ProcurementPlan pp : list) {
                listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), user.getDeptsection()));

            }
            gridBudgetItems2.setItems(listBudgetItems);
            spanTotal2.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo2.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            for (ProcurementPlan pp : list) {
                listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), comboBoxD_Section2.getValue()));

            }
            gridBudgetItems2.setItems(listBudgetItems);
            spanTotal2.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        } else if (budget2 != null && !budget2.isEmpty() && !procClassCombo.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            for (ProcurementPlan pp : list) {
                listBudgetItems.addAll(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(pp.getBudget(), pp.getProcClass(), pp.getCoa(), comboBoxD_Section2.getValue()));

            }
            gridBudgetItems2.setItems(listBudgetItems);
            spanTotal2.setText(decimalFormat.format(generatesumofMonthsFromList(listBudgetItems)) + "/=");
        }

    }

    private void createHeaderRowWorkplanConsultancy(Workbook workbook, Sheet sheet) {
        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq.setWrapText(true);
        styleq.setFont(fontBold);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.CENTER);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowDatecount = new ArrayList();
        Row headerRow = sheet.createRow(tr);
        List<Integer> unbold = new ArrayList();

// Assuming sheet is of type Sheet and workbook is of type Workbook
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 17);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("PROCUREMENT PLAN FOR CONSULTANCY SERVICES - FOR SUBMISSION TO THE SECRETARY TO TREASURY, PPDA AND PUBLICATION");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 17));
        rowDatecount.add((int) 0);
        tr++;

        Row row01 = sheet.createRow(tr);
        Cell cellq1 = row01.createCell((short) 0);
        row01.getCell(0).setCellStyle(styleq31);
        cellq1.setCellValue("Procuring and Disposing Entity: UGANDA RAILWAYS CORPORATION");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 17));
        rowDatecount.add((int) 1);
        tr++;

        Row row02 = sheet.createRow(tr);
        Cell cellq2 = row02.createCell((short) 0);
        row02.getCell(0).setCellStyle(styleq31);
        cellq2.setCellValue("Financial Year: " + budget.getValue().getFinancialYear());
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 17));
        rowDatecount.add((int) 2);
        tr++;

        short rownum = tr;
        int rowstart = tr + 1;

        int rowend = 0;
        int rowstart2 = 0;
        // sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 17));

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        //row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("S/No");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 0, 0));
        Cell cell2 = row.createCell((short) 1);
        //row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Subject of procurement");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 1, 1));
        Cell cell3 = row.createCell((short) 2);
        //row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Currency");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 2, 2));
        Cell cell4 = row.createCell((short) 3);
        //row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Estimated cost");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 3, 3));
        Cell cell5 = row.createCell((short) 4);
        //row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Source of funding");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 4, 4));

        Cell cell6 = row.createCell((short) 5);
        //row.getCell(16).setCellStyle(styleq31);
        cell6.setCellValue("Procurement method");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 5, 5));

        Cell cell6a = row.createCell((short) 6);
        //row.getCell(16).setCellStyle(styleq31);
        cell6a.setCellValue("Apply Reservation Scheme (Yes/No)");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 6, 6));

        Cell cell8 = row.createCell((short) 7);
        //row.getCell(17).setCellStyle(styleq31);

        cell8.setCellValue("Contract type");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 7, 7));

        Cell cell9 = row.createCell((short) 8);
        //row.getCell(18).setCellStyle(styleq31);
        cell9.setCellValue("Request for Expression of Interest");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 8, 11));
        Cell cell10 = row.createCell((short) 12);
        // row.getCell(19).setCellStyle(styleq31);
        cell10.setCellValue("Invitation of proposals and approval for award");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 12, 16));
        Cell cell11 = row.createCell((short) 17);
        //row.getCell(20).setCellStyle(styleq31);
        cell11.setCellValue("Completion date");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 17, 17));

        tr++;
        Row row1 = sheet.createRow(tr);
        Cell cella = row1.createCell((short) 8);
        cella.setCellValue("Invitation of Expressions of Interest date");

        Cell cellb = row1.createCell((short) 9);
        cellb.setCellValue("Closing- Opening date");

        Cell cellc = row1.createCell((short) 10);
        cellc.setCellValue("Approval of shortlist");

        Cell celld = row1.createCell((short) 11);
        celld.setCellValue("Notification date");

        Cell celld1 = row1.createCell((short) 12);
        celld1.setCellValue("Invitation of proposals date");

        Cell celld2 = row1.createCell((short) 13);
        celld2.setCellValue("Submission/ opening date");

        Cell celld3 = row1.createCell((short) 14);
        celld3.setCellValue("Approval of final evaluation report");

        Cell celld4 = row1.createCell((short) 15);
        celld4.setCellValue("Notification date");

        Cell celld5 = row1.createCell((short) 16);
        celld5.setCellValue("Contract signing date");

        Cell celld6 = row1.createCell((short) 17);
        //row.getCell(20).setCellStyle(styleq31);
        celld6.setCellValue("Completion date");
        List<ProcurementPlan> listProcurementPlan = new ArrayList<>();
        List<ProcurementPlan> plan1 = new ArrayList<>();
        plan1 = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Consultancy);
        if (budget != null && !budget.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            List<ProcurementPlan> plan = new ArrayList<>();
            
            if (!funds.isEmpty()) {
                
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndFundsource(budget.getValue(), ProcClass.Consultancy, plan1, funds.getValue());
            } else {
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Consultancy);
            }

        } else if (budget != null && !budget.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), ProcClass.Consultancy, user.getDeptsection());
            if (!funds.isEmpty()) {
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndSectsAndFundsource(budget.getValue(), ProcClass.Consultancy, plan1, funds.getValue(), user.getDeptsection());
            } else {
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), ProcClass.Consultancy, user.getDeptsection());
            }

        } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), ProcClass.Consultancy);
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();

            if (plan != null) {
                currentGridplan = grid.getGenericDataView().getItems().toList();
                for (ProcurementPlan k : plan) {

                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), comboBoxD_Section.getSelectedItems());
                    if (funds.isEmpty()) {

                    } else {

                        listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(k.getBudget(), k.getProcClass(), k.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getValue());
                    }
                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            listProcurementPlan = avGridplan;

        } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), ProcClass.Consultancy, user.getDeptsection());
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();
            if (plan != null) {
                currentGridplan = grid.getGenericDataView().getItems().toList();
                for (ProcurementPlan k : plan) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), comboBoxD_Section.getSelectedItems());

                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            listProcurementPlan = avGridplan;
        }

        int count = 0;
        BigDecimal totBigDecimal = BigDecimal.ZERO;
        for (ProcurementPlan r : listProcurementPlan) {

            count++;
            tr++;
            unbold.add((int) tr);
            rowDatecount.add((int) tr);
            Row rowm = sheet.createRow(tr);
            Cell cellm = rowm.createCell((short) 0);
            //row.getCell(0).setCellStyle(styleq31);
            cellm.setCellValue(count);
            Cell cell2m = rowm.createCell((short) 1);
            //row.getCell(1).setCellStyle(styleq31);
            cell2m.setCellValue(r.getSubject());
            Cell cell3m = rowm.createCell((short) 2);
            //row.getCell(2).setCellStyle(styleq31);
            cell3m.setCellValue(r.getCurrency().getData().getCurrencyShort());
            Cell cell4m = rowm.createCell((short) 3);
            //row.getCell(3).setCellStyle(styleq31);
         
            /*            if (comboBoxD_Section.isEmpty() & funds.isEmpty()) {
            
            cell4m.setCellValue(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(r.getBudget(), r.getProcClass(), r.getCoa()).doubleValue());
            totBigDecimal = totBigDecimal.add(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(r.getBudget(), r.getProcClass(), r.getCoa()));
            } else if (!comboBoxD_Section.isEmpty() & funds.isEmpty()) {
            
            cell4m.setCellValue(ProcurementPlanService.findByBudgetAndProcClassAndSectionIn(r.getBudget(), r.getProcClass(), r, comboBoxD_Section.getSelectedItems()).doubleValue());
            totBigDecimal = totBigDecimal.add(ProcurementPlanService.findByBudgetAndProcClassAndSectionIn(r.getBudget(), r.getProcClass(), r, comboBoxD_Section.getSelectedItems()));
            } else if (comboBoxD_Section.isEmpty() & !funds.isEmpty()) {
            cell4m.setCellValue(ProcurementPlanService.findByBudgetAndProcClassAndFundsIn(r.getBudget(), r.getProcClass(), r, funds.getSelectedItems()).doubleValue());
            totBigDecimal = totBigDecimal.add(ProcurementPlanService.findByBudgetAndProcClassAndFundsIn(r.getBudget(), r.getProcClass(), r, funds.getSelectedItems()));
            } else if (!comboBoxD_Section.isEmpty() & !funds.isEmpty()) {
            cell4m.setCellValue(ProcurementPlanService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), r.getProcClass(), r, comboBoxD_Section.getSelectedItems(), funds.getSelectedItems()).doubleValue());
            totBigDecimal = totBigDecimal.add(ProcurementPlanService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), r.getProcClass(), r, comboBoxD_Section.getSelectedItems(), funds.getSelectedItems()));
            }*/
            
            if (budget != null && !budget.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {

                if (funds.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(r.getBudget(), ProcClass.Consultancy, r.getCoa())) + "");
                    totBigDecimal = totBigDecimal.add(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(r.getBudget(), ProcClass.Consultancy, r.getCoa()));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), funds.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), funds.getSelectedItems())));
                }

            } else if (budget != null && !budget.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {

                if (funds.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), user.getDeptsection()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), user.getDeptsection())));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), user.getDeptsection(), funds.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), user.getDeptsection(), funds.getSelectedItems())));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                if (funds.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems())));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems())));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                if (funds.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems())));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), ProcClass.Consultancy, r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems())));
                }

            }            

            Cell cell5m = rowm.createCell((short) 4);
            //row.getCell(4).setCellStyle(styleq31);
            /*            if (r.getFundsource() != null) {
            cell5m.setCellValue(getCommaSeparatedFundsourceList(r.getFundsource()));
            }*/
            cell5m.setCellValue("");
            /*            Set<String> funds = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa());
            if (!funds.isEmpty()) {
            cell5m.setCellValue(getCommaSeparatedFundsourceList(funds));
            }
            Span span = new Span("");*/
            if (budget != null && !budget.isEmpty()  && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa());
                if (funds.isEmpty()) {
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacodeF(r.getBudget(), r.getProcClass(), r.getCoa(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }

            } else if (budget != null && !budget.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), user.getDeptsection());
                if (funds.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), user.getDeptsection(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems());
                if (funds.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems());
                if (funds.isEmpty()) {
                    // fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }

            }
            Cell cell6m = rowm.createCell((short) 5);
            //row.getCell(16).setCellStyle(styleq31);
            String procurementMethodName = (r.getProcurementMethod() != null) ? r.getProcurementMethod().getProcuremntMethod() : "";
            cell6m.setCellValue(procurementMethodName);

            Cell cell5ma = rowm.createCell((short) 6);
            //row.getCell(4).setCellStyle(styleq31);
            if (r.getReserve() != null) {
                cell5ma.setCellValue(r.getReserve());
            }

            Cell cell8m = rowm.createCell((short) 7);
            //row.getCell(17).setCellStyle(styleq31);
            String procurementTypeName = (r.getProcurementtype() != null) ? r.getProcurementtype().getProcuremntType() : "";
            cell8m.setCellValue(procurementTypeName);

            Cell cell9m = rowm.createCell((short) 8);
            //row.getCell(18).setCellStyle(styleq31);

            if (r.getReqInviofExpofInterestdate() != null) {
                cell9m.setCellValue(convertToLocalDate(r.getReqInviofExpofInterestdate()));
            } else {
                cell9m.setCellValue("");
            }
            cell9m.setCellStyle(dateCellStyle);
            Cell cell10m = rowm.createCell((short) 9);
            // row.getCell(19).setCellStyle(styleq31);
            if (r.getReqClosingOpeningdate() != null) {
                cell10m.setCellValue(convertToLocalDate(r.getReqClosingOpeningdate()));
            } else {
                cell10m.setCellValue("");
            }

            cell10m.setCellStyle(dateCellStyle);

            Cell cell11m = rowm.createCell((short) 10);
            if (r.getReqApprovalOfShortlist() != null) {
                cell11m.setCellValue(convertToLocalDate(r.getReqApprovalOfShortlist()));
            } else {
                cell11m.setCellValue("");
            }
            cell11m.setCellStyle(dateCellStyle);

            Cell cell10m1 = rowm.createCell((short) 11);
            //row.getCell(20).setCellStyle(styleq31);

            if (r.getReqNotificationdate() != null) {
                cell10m1.setCellValue(convertToLocalDate(r.getReqNotificationdate()));
            } else {
                cell10m1.setCellValue("");
            }
            cell10m1.setCellStyle(dateCellStyle);

            Cell cell11m1 = rowm.createCell((short) 12);
            //row.getCell(20).setCellStyle(styleq31);

            if (r.getInvitationofProposalsdate() != null) {
                cell11m1.setCellValue(convertToLocalDate(r.getInvitationofProposalsdate()));
            } else {
                cell11m1.setCellValue("");
            }
            cell11m1.setCellStyle(dateCellStyle);

            Cell cell12m = rowm.createCell((short) 13);
            //row.getCell(20).setCellStyle(styleq31);

            if (r.getSubmissionOpeningdate() != null) {
                cell12m.setCellValue(convertToLocalDate(r.getSubmissionOpeningdate()));
            } else {
                cell12m.setCellValue("");
            }
            cell12m.setCellStyle(dateCellStyle);

            Cell cell13m = rowm.createCell((short) 14);

            if (r.getApprovaloffinalevaluationreport() != null) {
                cell13m.setCellValue(convertToLocalDate(r.getApprovaloffinalevaluationreport()));
            } else {
                cell13m.setCellValue("");
            }
            cell13m.setCellStyle(dateCellStyle);

            Cell cell14m = rowm.createCell((short) 15);
            //row.getCell(20).setCellStyle(styleq31);

            if (r.getInvNotificationdate() != null) {
                cell14m.setCellStyle(dateCellStyle);
                cell14m.setCellValue(convertToLocalDate(r.getInvNotificationdate()));
            } else {
                cell14m.setCellValue("");
            }

            Cell cell15m = rowm.createCell((short) 16);
            cell15m.setCellStyle(dateCellStyle);
            //row.getCell(20).setCellStyle(styleq31);

            if (r.getContractsigningdate() != null) {
                cell15m.setCellValue(convertToLocalDate(r.getContractsigningdate()));
            } else {
                cell15m.setCellValue("");
            }
            Cell cell16m = rowm.createCell((short) 17);
            //row.getCell(20).setCellStyle(styleq31);

            if (r.getBcompletion() != null) {
                Date bCompletionDate = convertToLocalDate(r.getBcompletion());
                cell16m.setCellStyle(dateCellStyle);
                //cell16m.setCellValue(bCompletionDate);
                cell16m.setCellValue(r.getBcompletion());

            } else {
                cell16m.setCellValue("");
            }

        }
        tr++;
        Row rowft = sheet.createRow(tr);
        Cell rowf1cell0t = rowft.createCell((short) 0);
        rowf1cell0t.setCellValue("");
        Cell rowf1cell1t = rowft.createCell((short) 1);
        rowf1cell1t.setCellValue("");
        Cell rowf1cell2t = rowft.createCell((short) 2);
        rowf1cell2t.setCellValue("TOTAL");
        Cell rowf1cell3t = rowft.createCell((short) 3);
        rowf1cell3t.setCellValue(totBigDecimal.doubleValue());
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 3, 17));

        tr++;
        Row rowf1 = sheet.createRow(tr);
        Cell rowf1cell0 = rowf1.createCell((short) 0);
        rowf1cell0.setCellValue("");
        Cell rowf1cell1 = rowf1.createCell((short) 1);
        rowf1cell1.setCellValue("Prepared By:");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 3, 1, 2));

        Cell rowf1cell3 = rowf1.createCell((short) 3);
        rowf1cell3.setCellValue("Name:");

        Cell rowf1cell4 = rowf1.createCell((short) 4);
        rowf1cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf1cell8 = rowf1.createCell((short) 8);
        rowf1cell8.setCellValue("Approved By:");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 3, 8, 8));

        Cell rowf1cell9 = rowf1.createCell((short) 9);
        rowf1cell9.setCellValue("Name:");

        Cell rowf1cell10 = rowf1.createCell((short) 10);
        rowf1cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 17));

        tr++;
        Row rowf2 = sheet.createRow(tr);
        Cell rowf2cell0 = rowf2.createCell((short) 0);
        rowf2cell0.setCellValue("");

        Cell rowf2cell3 = rowf2.createCell((short) 3);
        rowf2cell3.setCellValue("Signature:");

        Cell rowf2cell4 = rowf2.createCell((short) 4);
        rowf2cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf2cell9 = rowf2.createCell((short) 9);
        rowf2cell9.setCellValue("Signature:");

        Cell rowf2cell10 = rowf2.createCell((short) 10);
        rowf2cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 17));

        tr++;
        Row rowf3 = sheet.createRow(tr);
        Cell rowf3cell0 = rowf3.createCell((short) 0);
        rowf3cell0.setCellValue("");

        Cell rowf3cell3 = rowf3.createCell((short) 3);
        rowf3cell3.setCellValue("Designation:");

        Cell rowf3cell4 = rowf3.createCell((short) 4);
        rowf3cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf3cell9 = rowf3.createCell((short) 9);
        rowf3cell9.setCellValue("Designation:");

        Cell rowf3cell10 = rowf3.createCell((short) 10);
        rowf3cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 17));

        tr++;
        Row rowf4 = sheet.createRow(tr);
        Cell rowf4cell0 = rowf4.createCell((short) 0);
        rowf4cell0.setCellValue("");

        Cell rowf4cell3 = rowf4.createCell((short) 3);
        rowf4cell3.setCellValue("Date:");

        Cell rowf4cell4 = rowf4.createCell((short) 4);
        rowf4cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf4cell9 = rowf4.createCell((short) 9);
        rowf4cell9.setCellValue("Date:");

        Cell rowf4cell10 = rowf4.createCell((short) 10);
        rowf4cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 17));
        for (Row rowDate : sheet) {

            for (Integer a : rowDatecount) {
                if (rowDate.getRowNum() == a) {
                    for (Cell currentCell : rowDate) {
                        CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                        CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                        newStyle.cloneStyleFrom(existingStyle);
                        CreationHelper createHelper2 = workbook.getCreationHelper();
                        CellStyle dateCellStyle2 = workbook.createCellStyle();
                        dateCellStyle2.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
                        dateCellStyle2.setFillForegroundColor(IndexedColors.RED.index);
                        dateCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        currentCell.setCellStyle(newStyle);
                    }
                }
            }
        }
        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    newStyle.setWrapText(true);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(false);
                    activityFont.setFontName("Arial");
                    activityFont.setFontHeightInPoints((short) 10);
                    if (unbold.contains(currentRow.getRowNum())) {
                        newStyle.setFont(activityFont);
                    } else {
                        activityFont.setBold(true);
                        newStyle.setFont(activityFont);
                    }

                    currentCell.setCellStyle(newStyle);
                }

            }
        }

        // Iterate through each merged region and apply the border
        for (int i = 1; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            for (int rowz = mergedRegion.getFirstRow(); rowz <= mergedRegion.getLastRow(); rowz++) {
                Row currentRow = sheet.getRow(rowz);
                if (currentRow == null) {
                    currentRow = sheet.createRow(rowz);
                }

                for (int col = mergedRegion.getFirstColumn(); col <= mergedRegion.getLastColumn(); col++) {
                    Cell currentCell = currentRow.getCell(col);
                    if (currentCell == null) {
                        currentCell = currentRow.createCell(col);
                    }
                    CellStyle existingStyle = currentCell.getCellStyle();

                    // Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    newStyle.setWrapText(true);
                    Font activityFont = workbook.createFont();
                    activityFont.setFontName("Arial");
                    activityFont.setFontHeightInPoints((short) 10);
                    activityFont.setBold(false);

                    if (unbold.contains(currentRow.getRowNum())) {
                        newStyle.setFont(activityFont);
                    } else {
                        activityFont.setBold(true);
                        newStyle.setFont(activityFont);
                    }

                    // Set the new style to the cell
                    currentCell.setCellStyle(newStyle);

                }
            }
        }

    }

    private void createHeaderRowWorkplanConsultancyelse(Workbook workbook, Sheet sheet) {
        if(!funds.isEmpty()){
            funds2.select(funds.getSelectedItems());
        }
        if(!comboBoxD_Section.isEmpty()){
            comboBoxD_Section2.select(comboBoxD_Section.getSelectedItems());
        }        
        
        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq.setWrapText(true);
        styleq.setFont(fontBold);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.CENTER);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowBoldcount = new ArrayList();
        Row headerRow = sheet.createRow(tr);
        List<Integer> unbold = new ArrayList();

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 14);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("PROCUREMENT PLAN FOR " + procClassCombo2.getValue().name().toUpperCase() + " - FOR SUBMISSION TO TREASURY, PPDA and PUBLICATION");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 14));
        rowBoldcount.add((int) 0);
        tr++;

        Row row01 = sheet.createRow(tr);
        Cell cellq1 = row01.createCell((short) 0);
        row01.getCell(0).setCellStyle(styleq31);
        cellq1.setCellValue("Procuring and Disposing Entity: UGANDA RAILWAYS CORPORATION");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 14));
        rowBoldcount.add((int) 1);
        tr++;

        Row row02 = sheet.createRow(tr);
        Cell cellq2 = row02.createCell((short) 0);
        row02.getCell(0).setCellStyle(styleq31);
        cellq2.setCellValue("Financial Year: " + budget2.getValue().getFinancialYear());
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 14));
        rowBoldcount.add((int) 2);
        tr++;

        short rownum = tr;
        int rowstart = tr + 1;

        int rowend = 0;
        int rowstart2 = 0;
        // sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 17));

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        //row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("S/No");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 0, 0));
        Cell cell2 = row.createCell((short) 1);
        //row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Subject of procurement");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 1, 1));
        Cell cell3 = row.createCell((short) 2);
        //row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Currency");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 2, 2));
        Cell cell4 = row.createCell((short) 3);
        //row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Estimated cost");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 3, 3));
        Cell cell5 = row.createCell((short) 4);
        //row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Source of funding");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 4, 4));

        Cell cell6 = row.createCell((short) 5);
        //row.getCell(16).setCellStyle(styleq31);
        cell6.setCellValue("Procurement method");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 5, 5));

        Cell cell8 = row.createCell((short) 6);
        //row.getCell(17).setCellStyle(styleq31);

        cell8.setCellValue("Contract type");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 6, 6));

        Cell cell81 = row.createCell((short) 7);
        //row.getCell(17).setCellStyle(styleq31);

        cell81.setCellValue("PRE- QUALIFICATION (Yes or No)");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 7, 7));

        Cell cell6a = row.createCell((short) 8);
        //row.getCell(16).setCellStyle(styleq31);
        cell6a.setCellValue("Apply Reservation Scheme (Yes/No)");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 8, 8));

        Cell cell9 = row.createCell((short) 9);
        //row.getCell(18).setCellStyle(styleq31);
        cell9.setCellValue("INVITATION AND AWARD OF BIDS");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 9, 12));

        Cell cell10 = row.createCell((short) 13);
        // row.getCell(19).setCellStyle(styleq31);
        cell10.setCellValue("Contract signing date");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 13, 13));
        Cell cell11 = row.createCell((short) 14);
        //row.getCell(20).setCellStyle(styleq31);
        cell11.setCellValue("Completion date");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 1, 14, 14));

        tr++;
        Row row1 = sheet.createRow(tr);
        Cell cella = row1.createCell((short) 9);
        cella.setCellValue("Bid invitation date");

        Cell cellb = row1.createCell((short) 10);
        cellb.setCellValue("Closing- Opening date");

        Cell cellc = row1.createCell((short) 11);
        cellc.setCellValue("Approval evaluati on report date");

        Cell celld = row1.createCell((short) 12);
        celld.setCellValue("Award notification date");
        List<ProcurementPlan> listProcurementPlan = new ArrayList<>();
        List<ProcurementPlan> plan1 = new ArrayList<>();
        plan1 = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), procClassCombo2.getValue());
        if (budget != null && !budget.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            List<ProcurementPlan> plan = new ArrayList<>();
            
            if (!funds2.isEmpty()) {
                
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndFundsource(budget.getValue(), procClassCombo2.getValue(), plan1, funds2.getValue());
            } else {
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), procClassCombo2.getValue());
            }

        } else if (budget != null && !budget.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), procClassCombo2.getValue(), user.getDeptsection());
            if (!funds2.isEmpty()) {
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndSectsAndFundsource(budget.getValue(), procClassCombo2.getValue(), plan1, funds2.getValue(), user.getDeptsection());
            } else {
                listProcurementPlan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), procClassCombo2.getValue(), user.getDeptsection());
            }

        } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClass(budget.getValue(), procClassCombo2.getValue());
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();

            if (plan != null) {
               
                for (ProcurementPlan k : plan) {

                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), comboBoxD_Section2.getSelectedItems());
                    if (funds2.isEmpty()) {

                    } else {

                        listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(k.getBudget(), k.getProcClass(), k.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getValue());
                    }
                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            listProcurementPlan = avGridplan;

        } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
            //List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), p, comboBoxD_Section.getValue());
            List<ProcurementPlan> plan = ProcurementPlanService.findByBudgetAndProcClassAndDeptUnits(budget.getValue(), procClassCombo2.getValue(), user.getDeptsection());
            List<ProcurementPlan> currentGridplan = new ArrayList<>();
            List<ProcurementPlan> avGridplan = new ArrayList<>();
            if (plan != null) {
                
                for (ProcurementPlan k : plan) {
                    List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(k.getBudget(), k.getProcClass(), k.getCoa(), comboBoxD_Section2.getSelectedItems());

                    if (!listBudgetItems.isEmpty()) {
                        avGridplan.add(k);
                    }
                }

            }
            listProcurementPlan = avGridplan;
        }
        int count = 0;
        BigDecimal totBigDecimal = BigDecimal.ZERO;
        for (ProcurementPlan r : listProcurementPlan) {
            count++;
            tr++;
            unbold.add((int) tr);
            Row rowm = sheet.createRow(tr);
            Cell cellm = rowm.createCell((short) 0);
            //row.getCell(0).setCellStyle(styleq31);
            cellm.setCellValue(count);
            Cell cell2m = rowm.createCell((short) 1);
            //row.getCell(1).setCellStyle(styleq31);
            cell2m.setCellValue(r.getSubject());
            Cell cell3m = rowm.createCell((short) 2);
            //row.getCell(2).setCellStyle(styleq31);
            if (r.getCurrency() != null) {
                cell3m.setCellValue(r.getCurrency().getData().getCurrencyShort());
            }

            Cell cell4m = rowm.createCell((short) 3);
            if (budget != null && !budget.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {

                if (funds.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(r.getBudget(), procClassCombo2.getValue(), r.getCoa())) + "");
                    totBigDecimal = totBigDecimal.add(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(r.getBudget(), procClassCombo2.getValue(), r.getCoa()));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), funds2.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndFundsourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), funds2.getSelectedItems())));
                }

            } else if (budget != null && !budget.isEmpty() && comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {

                if (funds2.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), user.getDeptsection()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), user.getDeptsection())));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), user.getDeptsection(), funds.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), user.getDeptsection(), funds.getSelectedItems())));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                if (funds2.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems())));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems())));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section2.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                if (funds2.isEmpty()) {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems())));
                } else {
                    cell4m.setCellValue(decimalFormat.format(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems()))) + "");
                    totBigDecimal = totBigDecimal.add(generatesumofMonthsFromList(budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(r.getBudget(), procClassCombo2.getValue(), r.getCoa(), comboBoxD_Section2.getSelectedItems(), funds2.getSelectedItems())));
                }

            }
            Cell cell5m = rowm.createCell((short) 4);
            //row.getCell(4).setCellStyle(styleq31);
            /*            if (r.getFundsource() != null) {
            cell5m.setCellValue(getCommaSeparatedFundsourceList(r.getFundsource()));
            }*/
            cell5m.setCellValue("");
            if (budget != null && !budget.isEmpty()  && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa());
                if (funds.isEmpty()) {
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacodeF(r.getBudget(), r.getProcClass(), r.getCoa(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }

            } else if (budget != null && !budget.isEmpty() && comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), user.getDeptsection());
                if (funds.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), user.getDeptsection(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems());
                if (funds.isEmpty()) {
                    //fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }
            } else if (budget != null && !budget.isEmpty() && !comboBoxD_Section.isEmpty() && (user.getRoles().contains(Role.BLO) || user.getRoles().contains(Role.HOD))) {
                Set<String> fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems());
                if (funds.isEmpty()) {
                    // fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(urcActivity.getBudget(), urcActivity.getProcClass(), urcActivity.getCoa());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                } else {
                    fundString = budgetItemsService.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(r.getBudget(), r.getProcClass(), r.getCoa(), comboBoxD_Section.getSelectedItems(), funds.getSelectedItems());
                    cell5m.setCellValue(getCommaSeparatedFundsourceList(fundString));
                }

            }
            Cell cell6m = rowm.createCell((short) 5);
            //row.getCell(16).setCellStyle(styleq31);
            String procurementMethodName = (r.getProcurementMethod() != null) ? r.getProcurementMethod().getProcuremntMethod() : "";
            cell6m.setCellValue(procurementMethodName);

            Cell cell8m = rowm.createCell((short) 6);
            //row.getCell(17).setCellStyle(styleq31);
            String procurementTypeName = (r.getProcurementtype() != null) ? r.getProcurementtype().getProcuremntType() : "";
            cell8m.setCellValue(procurementTypeName);

            Cell cell5ma = rowm.createCell((short) 7);
            //row.getCell(4).setCellStyle(styleq31);
            if (r.getPrequal() != null) {
                cell5ma.setCellValue(r.getPrequal());
            }

            Cell cell5ma1 = rowm.createCell((short) 8);
            //row.getCell(4).setCellStyle(styleq31);
            if (r.getReserve() != null) {
                cell5ma1.setCellValue(r.getReserve());
            }

            Cell cell9m = rowm.createCell((short) 9);
            //row.getCell(18).setCellStyle(styleq31);
            cell9m.setCellValue(r.getBinvite());
            Cell cell10m = rowm.createCell((short) 10);
            // row.getCell(19).setCellStyle(styleq31);
            cell10m.setCellValue(r.getReqClosingOpeningdate());

            Cell cell11m = rowm.createCell((short) 11);
            cell11m.setCellValue(r.getApprovaloffinalevaluationreport());

            Cell cell10m1 = rowm.createCell((short) 12);
            //row.getCell(20).setCellStyle(styleq31);
            cell10m1.setCellValue(r.getAwardnotificationdate());
            Cell cell11m1 = rowm.createCell((short) 13);
            //row.getCell(20).setCellStyle(styleq31);
            cell11m1.setCellValue(r.getContractsigningdate());
            Cell cell12m = rowm.createCell((short) 14);
            //row.getCell(20).setCellStyle(styleq31);
            cell12m.setCellValue(r.getBcompletion());
        }
        tr++;
        Row rowft = sheet.createRow(tr);
        Cell rowf1cell0t = rowft.createCell((short) 0);
        rowf1cell0t.setCellValue("");
        Cell rowf1cell1t = rowft.createCell((short) 1);
        rowf1cell1t.setCellValue("");
        Cell rowf1cell2t = rowft.createCell((short) 2);
        rowf1cell2t.setCellValue("TOTAL");
        Cell rowf1cell3t = rowft.createCell((short) 3);
        rowf1cell3t.setCellValue(totBigDecimal.doubleValue());
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 3, 14));

        tr++;
        Row rowf1 = sheet.createRow(tr);
        Cell rowf1cell0 = rowf1.createCell((short) 0);
        rowf1cell0.setCellValue("");
        Cell rowf1cell1 = rowf1.createCell((short) 1);
        rowf1cell1.setCellValue("Prepared By:");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 3, 1, 2));

        Cell rowf1cell3 = rowf1.createCell((short) 3);
        rowf1cell3.setCellValue("Name:");

        Cell rowf1cell4 = rowf1.createCell((short) 4);
        rowf1cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf1cell8 = rowf1.createCell((short) 8);
        rowf1cell8.setCellValue("Approved By:");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr + 3, 8, 8));

        Cell rowf1cell9 = rowf1.createCell((short) 9);
        rowf1cell9.setCellValue("Name:");

        Cell rowf1cell10 = rowf1.createCell((short) 10);
        rowf1cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 14));

        tr++;
        Row rowf2 = sheet.createRow(tr);
        Cell rowf2cell0 = rowf2.createCell((short) 0);
        rowf2cell0.setCellValue("");

        Cell rowf2cell3 = rowf2.createCell((short) 3);
        rowf2cell3.setCellValue("Signature:");

        Cell rowf2cell4 = rowf2.createCell((short) 4);
        rowf2cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf2cell9 = rowf2.createCell((short) 9);
        rowf2cell9.setCellValue("Signature:");

        Cell rowf2cell10 = rowf2.createCell((short) 10);
        rowf2cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 14));

        tr++;
        Row rowf3 = sheet.createRow(tr);
        Cell rowf3cell0 = rowf3.createCell((short) 0);
        rowf3cell0.setCellValue("");

        Cell rowf3cell3 = rowf3.createCell((short) 3);
        rowf3cell3.setCellValue("Designation:");

        Cell rowf3cell4 = rowf3.createCell((short) 4);
        rowf3cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf3cell9 = rowf3.createCell((short) 9);
        rowf3cell9.setCellValue("Designation:");

        Cell rowf3cell10 = rowf3.createCell((short) 10);
        rowf3cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 14));

        tr++;
        Row rowf4 = sheet.createRow(tr);
        Cell rowf4cell0 = rowf4.createCell((short) 0);
        rowf4cell0.setCellValue("");

        Cell rowf4cell3 = rowf4.createCell((short) 3);
        rowf4cell3.setCellValue("Date:");

        Cell rowf4cell4 = rowf4.createCell((short) 4);
        rowf4cell4.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 4, 7));

        Cell rowf4cell9 = rowf4.createCell((short) 9);
        rowf4cell9.setCellValue("Date:");

        Cell rowf4cell10 = rowf4.createCell((short) 10);
        rowf4cell10.setCellValue("");
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 10, 14));

        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    newStyle.setWrapText(true);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(false);
                    activityFont.setFontName("Arial");
                    activityFont.setFontHeightInPoints((short) 10);
                    if (unbold.contains(currentRow.getRowNum())) {
                        newStyle.setFont(activityFont);
                    } else {
                        activityFont.setBold(true);
                        newStyle.setFont(activityFont);
                    }

                    currentCell.setCellStyle(newStyle);
                }

            }
        }

        // Iterate through each merged region and apply the border
        for (int i = 1; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            for (int rowz = mergedRegion.getFirstRow(); rowz <= mergedRegion.getLastRow(); rowz++) {
                Row currentRow = sheet.getRow(rowz);
                if (currentRow == null) {
                    currentRow = sheet.createRow(rowz);
                }

                for (int col = mergedRegion.getFirstColumn(); col <= mergedRegion.getLastColumn(); col++) {
                    Cell currentCell = currentRow.getCell(col);
                    if (currentCell == null) {
                        currentCell = currentRow.createCell(col);
                    }
                    CellStyle existingStyle = currentCell.getCellStyle();

                    // Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    newStyle.setWrapText(true);
                    Font activityFont = workbook.createFont();
                    activityFont.setFontName("Arial");
                    activityFont.setFontHeightInPoints((short) 10);
                    activityFont.setBold(false);

                    if (unbold.contains(currentRow.getRowNum())) {
                        newStyle.setFont(activityFont);
                    } else {
                        activityFont.setBold(true);
                        newStyle.setFont(activityFont);
                    }

                    // Set the new style to the cell
                    currentCell.setCellStyle(newStyle);

                }
            }
        }

    }

    private void exportAndDownloadExcelWorkplan(ProcClass proc) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(proc.name());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            if (proc.equals(ProcClass.Consultancy)) {
                createHeaderRowWorkplanConsultancy(workbook, sheet);
            } else if (proc.equals(ProcClass.Disposal)) {
                //createHeaderRowWorkplanConsultancyelse(workbook, sheet);
            } else {
                procClassCombo2.setValue(proc);
                createHeaderRowWorkplanConsultancyelse(workbook, sheet);
            }

            //createDataRows(sheet, people);
            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Procurement plan.xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadExcelProcurementPlanSheets() {
        try (Workbook workbook = new XSSFWorkbook()) {

            List<ProcClass> listProcClass = new ArrayList();
            listProcClass.add(ProcClass.Consultancy);
            listProcClass.add(ProcClass.Non_Consultancy);
            listProcClass.add(ProcClass.Supplies);
            listProcClass.add(ProcClass.Works);
            for (ProcClass newProcClass : listProcClass) {
                Sheet sheet = workbook.createSheet(newProcClass.name());
                // Set the paper size to A3 Landscape
                sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
                sheet.getPrintSetup().setLandscape(true);
                if (newProcClass.equals(ProcClass.Consultancy)) {
                    createHeaderRowWorkplanConsultancy(workbook, sheet);
                } else {
                    procClassCombo2.setValue(newProcClass);
                    createHeaderRowWorkplanConsultancyelse(workbook, sheet);
                }
            }

            //createDataRows(sheet, people);
            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Procurement plan.xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addImageToHeader(Sheet sheet, String imagePath) throws IOException {
        // Load the image
        BufferedImage bufferedImage = ImageIO.read(BudgetReportsView.class.getResourceAsStream(imagePath));

        // Convert the image to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Add the image to the header
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 0, 1, 1);

        int pictureIndex = sheet.getWorkbook().addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        drawing.createPicture(anchor, pictureIndex);
    }

    private void setBottomBorderForRegion(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
    }

    public Date convertToLocalDate(LocalDate localDate) {

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public List<BudgetItems> extractBudgetItemsFromProcurementPlans(List<ProcurementPlan> procurementPlans) {
        List<BudgetItems> budgetItemsList = new ArrayList<>();

        for (ProcurementPlan plan : procurementPlans) {

            budgetItemsList.addAll(budgetItemsService.findByBudgetAndProcClassAndCoa(plan.getBudget(), plan.getProcClass(), plan.getCoa()));
        }

        return budgetItemsList;
    }

    /*    public String getCommaSeparatedFundsourceList(Set<Fundsource> fundsourceSet) {
    // Convert Set<Fundsource> to a comma-separated string of fundsource names
    String commaSeparatedList = fundsourceSet.stream()
    .map(Fundsource::getFundsource)
    .collect(Collectors.joining(", "));
    
    return commaSeparatedList;
    }*/
    public String getCommaSeparatedFundsourceList(Set<String> fundsourceSet) {
        // Convert Set<String> to a comma-separated string of fundsource names
        String commaSeparatedList = String.join(", ", fundsourceSet);

        return commaSeparatedList;
    }

    public Notification note(List<ProcurementPlan> avGridplan, Set<UrcDeptSectionAnlDimbgt> depts) {
        int count = 0; // Renamed i to count for clarity

        for (ProcurementPlan plan : avGridplan) {
            List<BudgetItems> listBudgetItems = budgetItemsService.findByBudgetAndProcClassAndCoaAndDeptUnitIn(
                    plan.getBudget(), plan.getProcClass(), plan.getCoa(), depts);
            if (!listBudgetItems.isEmpty()) {
                count++;

            } else {

            }
        }

        Notification notification = new Notification(count + " Item(s) found", 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR); // Adding an error theme to indicate it's a warning
        notification.open();
        return notification;
    }
}
