package com.methaltech.application.views.budget;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.bgtool.service.*;
import com.methaltech.application.data.entity.bgtool.*;
import com.methaltech.application.data.entity.livedata.UR5_ACNT;
import com.methaltech.application.data.livedata.service.UR5_ACNTService;
import com.methaltech.application.data.livedata.service.UrcDepartmentAnlDimService;
import static com.methaltech.application.test.getYears;
import static com.methaltech.application.test.strings;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.time.LocalDate;
import java.util.Optional;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@PageTitle("Budget")
@Route(value = "budget-detail/:budgetID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
@Uses(Icon.class)
public class BudgetView extends Div implements BeforeEnterObserver {

    private final String BUDGET_ID = "budgetID";
    private final String BUDGET_EDIT_ROUTE_TEMPLATE = "budget-detail/%s/edit";

    private final Grid<Budget> grid = new Grid<>(Budget.class, false);

    private Grid<Currency> gridCurrency = new Grid<>(Currency.class, false);

    private Grid<CurrencyData> gridCurrencyData = new Grid<>(CurrencyData.class, false);

    private Grid<D_Unit> gridUnit = new Grid<>(D_Unit.class, false);

    private Grid<Section> gridSection = new Grid<>(Section.class, false);

    private Grid<Department> gridDepartment = new Grid<>(Department.class, false);
    private Grid<Coalevel1> gridCoalevel1 = new Grid<>(Coalevel1.class, false);
    private Grid<Coalevel11> gridCoalevel11 = new Grid<>(Coalevel11.class, false);
    private Grid<Coalevel12> gridCoalevel12 = new Grid<>(Coalevel12.class, false);
    private Grid<Coalevel13> gridCoalevel13 = new Grid<>(Coalevel13.class, false);
    private Grid<NDPIII_Objective> ndpGrid = new Grid<>(NDPIII_Objective.class, false);
    private Grid<National_Transport_Master_Plan> ntmpGrid = new Grid<>(National_Transport_Master_Plan.class, false);
    private Grid<URC_Strategic_Plan> URC_Strategic_PlanGrid = new Grid<>(URC_Strategic_Plan.class, false);

    private Grid<National_Budget_Focus_Areas> nbfaGrid = new Grid<>(National_Budget_Focus_Areas.class, false);
    private Grid<URC_Priority_Areas> URC_Priority_AreasGrid = new Grid<>(URC_Priority_Areas.class, false);

    private Grid<Urc_Activities> Urc_ActivitiesGrid = new Grid<>(Urc_Activities.class, false);
    private Grid<Organisation> gridOrganisation = new Grid<>(Organisation.class, false);

    //private Grid<ProcurementMethod> gridProcurementMethod = new Grid<>(ProcurementMethod.class, false);
    private Grid<ProcurementType> gridProcurementType = new Grid<>(ProcurementType.class, false);
    private Grid<Fundsource> gridFundsource = new Grid<>(Fundsource.class, false);

    private Grid<COA> gridCOA = new Grid<>(COA.class, false);
    private Grid<UR5_ACNT> gridCOASetting = new Grid<>(UR5_ACNT.class, false);

    private DatePicker startDate;
    private DatePicker closeDate;
    private TextField financialYear;
    private TextArea description;
    private Checkbox important;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final Button cancelCurrencyData = new Button("Cancel");
    private final Button saveCurrencyData = new Button("Save");

    private final Button cancelCurrency = new Button("Cancel");
    private final Button saveCurrency = new Button("Save");

    private final Button cancelLevel1COA = new Button("Cancel");
    private final Button saveLevel1COA = new Button("Save");

    private final Button cancelLevel2COA = new Button("Cancel");
    private final Button saveLevel2COA = new Button("Save");

    private final Button cancelLevel3COA = new Button("Cancel");
    private final Button saveLevel3COA = new Button("Save");

    private final Button cancelUnit = new Button("Cancel");
    private final Button saveUnit = new Button("Save");

    private final Button cancelSection = new Button("Cancel");
    private final Button saveSection = new Button("Save");

    private final Button cancelDepartment = new Button("Cancel");
    private final Button saveDepartment = new Button("Save");

    private final Button cancelCoalevel1 = new Button("Cancel");
    private final Button saveCoalevel1 = new Button("Save");

    private final Button cancelCoalevel11 = new Button("Cancel");
    private final Button saveCoalevel11 = new Button("Save");

    private final Button cancelCoalevel12 = new Button("Cancel");
    private final Button saveCoalevel12 = new Button("Save");

    private final Button cancelCoalevel13 = new Button("Cancel");
    private final Button saveCoalevel13 = new Button("Save");

    private final Button cancelCOA = new Button("Cancel");
    private final Button saveCOA = new Button("Save");

    private Button autogen = new Button("Set");

    private MenuBar menuBar = new MenuBar();

    private MenuItem currency;
    private SubMenu edit_currency;
    private SubMenu currencyFy;

    private MenuItem chart_of_accounts;
    private SubMenu level1COA;
    private SubMenu level2COA;
    private SubMenu level3COA;
    private SubMenu level4COA;

    private MenuItem structure;
    private SubMenu department;
    private SubMenu section;
    private SubMenu unit;
    private SubMenu budgetStructure;

    private MenuItem workplan;
    private SubMenu workplanView;

    private MenuItem procurementMethods;
    private SubMenu procurementMethodsView;

    private MenuItem procurementType;
    private SubMenu procurementTypeView;

    private MenuItem Fundsource;
    private SubMenu fundsourceView;

    private MenuItem SunFile;
    private SubMenu sunFileJul;
    private SubMenu sunFileJan;
    private SubMenu sunStatFileJul;
    private SubMenu sunStatFileJan;
    private SubMenu parameterFile;

    private BeanValidationBinder<Budget> binder = new BeanValidationBinder<>(Budget.class);

    private BeanValidationBinder<COA> binderCOA = new BeanValidationBinder<>(COA.class);

    private Budget sampleBudget;

    private CurrencyData sampleCurrencyData;
    private Currency sampleCurrency;

    private Unit sampleUnitDept;
    private Section sampleSection;
    private Department sampleDepartment;
    private Coalevel1 sampleCoalevel1;
    private Coalevel11 sampleCoalevel11;
    private Coalevel12 sampleCoalevel12;
    private Coalevel13 sampleCoalevel13;

    private Coalevel1 sampleCoalevel1COA = new Coalevel1();

    private Organisation sampleOrganisation;

    // private ProcurementMethod sampleProcurementMethod;
    private ProcurementType sampleProcurementType;
    private Fundsource sampleFundsource;

    private COA sampleCOA;
    //private BeanValidationBinder<CurrencyData> binderCurrencyData;
    private final BudgetService sampleBudgetService;
    private NDPIII_Objective sampleNDPIII_Objective;
    private National_Transport_Master_Plan sampleNational_Transport_Master_Plan;
    private National_Budget_Focus_Areas sampleNational_Budget_Focus_Areas;
    private URC_Priority_Areas sampleURC_Priority_Areas;
    private URC_Strategic_Plan sampleURC_Strategic_Plan;

    private Urc_Activities sampleUrc_Activities;

    // private final ProcurementMethodService sampleProcurementMethodService;
    private final ProcurementTypeService sampleProcurementTypeService;

    private final CurrencyDataService sampleCurrencyDataService;
    private final CurrencyService sampleCurrencyService;

    private final UnitService sampleUnitService;
    private final SectionService sampleSectionService;
    private final DepartmentService sampleDepartmentService;
    private final OrganisationService sampleOrganisationService;
    private final Coalevel1Service sampleCoalevel1Service;
    private final Coalevel11Service sampleCoalevel11Service;
    private final Coalevel12Service sampleCoalevel12Service;
    private final CoaService sampleCoaService;
    private final Coalevel13Service sampleCoalevel13Service;
    private final UR5_ACNTService sampleUR5_ACNTService;
    private final NDPIII_ObjectiveService sampleNDPIII_ObjectiveService;
    private final National_Transport_Master_PlanService sampleNational_Transport_Master_PlanService;
    private final National_Budget_Focus_AreasService sampleNational_Budget_Focus_AreasService;
    private final URC_Strategic_PlanService sampleURC_Strategic_PlanService;
    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final FreightVolumesService sampleFreightVolumesService;

    private TextField currencyDataNameField = new TextField("Currency");
    private TextField currencyAbrField = new TextField("Currency Abr");

    private TextField UnitNameField = new TextField("Unit");

    private TextField SectionNameField = new TextField("Section");

    private ComboBox<CurrencyData> currencyName = new ComboBox<>("Currency");
    private BigDecimalField currencyRateField = new BigDecimalField("Rate");
    private Checkbox currencyEnabledField = new Checkbox("Enable");
    private String currentfy;
    private Long level1ID = null;
    Long level1ID2 = null;
    private int deptselect;
    private int sectselect;

    private TextField BudgetTypeNameField = new TextField("BudgetName");
    private TextField ProcurementMethodNameField = new TextField("Procurement Method");
    private TextField ProcurementTypeNameField = new TextField("Procurement Type");
    private TextField FundsourceNameField = new TextField("Fund source");

    private TextField DepartmentNameField = new TextField("Department");

    private TextField Coalevel1NameField = new TextField("Name");
    private TextField Coalevel11NameField = new TextField("Name");
    private TextField Coalevel12NameField = new TextField("Name");
    private TextField Coalevel13NameField = new TextField("Name");

    private TextField COALevel11NameField = new TextField("Name");

    private ComboBox<Coalevel1> Coalevel1Box = new ComboBox<>("Class 1");
    private ComboBox<Display> displayBox = new ComboBox<>("Display");
    private ComboBox<Coalevel1> Coalevel1Box1 = new ComboBox<>("Class 1");
    private TextField COANameField = new TextField("Name");
    private TextField CodeField = new TextField("Code");
    //private ComboBox<Coalevel11> Coalevel11Box = new ComboBox<>("COA Sub Category 1");
    private MultiSelectComboBox<Section> coaunits = new MultiSelectComboBox<>();
    private ComboBox<ProcClass> procclass = new ComboBox<>("Procurement Category");
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sections = new MultiSelectComboBox("Attach Sections");
    private Span status;
    private TextField COASearchField = new TextField("Search");
    private TextField COASearchField1 = new TextField("Search");

    private int currentPage;
    private int pageSize;
    private String filter;
    private COA coaSAVE;
    private Budget sourceBudget;
    private TreeGrid<Department> UnitstreeGrid;
    private int ndp3objSelect;
    private int ntmpSelect;
    private int nbfaSelect;
    private int Urc_ActivitiesSelect;

    private TextArea activity = new TextArea("Activity");
    private TextField fundsource = new TextField("Fund Source");
    private TextField output = new TextField("Out Put");
    private TextField performanceIndicator = new TextField("Performance Indicator");
    private TextField outcome = new TextField("Out Come");
    private TextArea objective = new TextArea("Objective");

    private TextArea nameNdp1 = new TextArea("Objective");
    private TextArea nameNdp2 = new TextArea("Objective");
    private TextArea nameNdp3 = new TextArea("Objective");
    private TextArea nameNdp4 = new TextArea("Objective");
    private TextArea nameNdp5 = new TextArea("Objective");
    private TextArea nameNdp6 = new TextArea("Objective");

    Checkbox checkbox = new Checkbox("Active");
    Button saveCategory = new Button("Save");
    private final BudgetItemsService budgetItemsService;
    private final FundsourceService fundsourceService;
    PeriodExtractor periodExtractor = new PeriodExtractor();

    private final UrcDepartmentAnlDimService sampleUrcAnlCodeService;

    List<StreamResource> downloadQueue = new ArrayList<>();

    @Autowired
    public BudgetView(BudgetService sampleBudgetService, CurrencyDataService sampleCurrencyDataService,
            CurrencyService sampleCurrencyService, UnitService sampleUnitService,
            SectionService sampleSectionService, DepartmentService sampleDepartmentService,
            OrganisationService sampleOrganisationService, Coalevel1Service sampleCoalevel1Service,
            Coalevel11Service sampleCoalevel11Service, Coalevel12Service sampleCoalevel12Service,
            CoaService sampleCoaService, Coalevel13Service sampleCoalevel13Service,
            UR5_ACNTService sampleUR5_ACNTService, NDPIII_ObjectiveService sampleNDPIII_ObjectiveService,
            National_Transport_Master_PlanService sampleNational_Transport_Master_PlanService,
            National_Budget_Focus_AreasService sampleNational_Budget_Focus_AreasService,
            Urc_ActivitiesService sampleUrc_ActivitiesService, URC_Strategic_PlanService sampleURC_Strategic_PlanService,
            URC_Priority_AreasService sampleURC_Priority_AreasService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
             BudgetItemsService budgetItemsService,
            ProcurementTypeService sampleProcurementTypeService, FundsourceService fundsourceService,
            UrcDepartmentAnlDimService sampleUrcAnlCodeService, DeptSectionMergerService sampleDeptSectionMergerService,
            FreightVolumesService sampleFreightVolumesService) {
        this.sampleBudgetService = sampleBudgetService;
        this.sampleCurrencyDataService = sampleCurrencyDataService;
        this.sampleCurrencyService = sampleCurrencyService;
        this.sampleUnitService = sampleUnitService;
        this.sampleSectionService = sampleSectionService;
        this.sampleDepartmentService = sampleDepartmentService;
        this.sampleOrganisationService = sampleOrganisationService;
        this.sampleCoalevel1Service = sampleCoalevel1Service;
        this.sampleCoalevel11Service = sampleCoalevel11Service;
        this.sampleCoalevel12Service = sampleCoalevel12Service;
        this.sampleCoaService = sampleCoaService;
        this.sampleCoalevel13Service = sampleCoalevel13Service;
        this.sampleUR5_ACNTService = sampleUR5_ACNTService;
        this.sampleNDPIII_ObjectiveService = sampleNDPIII_ObjectiveService;
        this.sampleNational_Transport_Master_PlanService = sampleNational_Transport_Master_PlanService;
        this.sampleNational_Budget_Focus_AreasService = sampleNational_Budget_Focus_AreasService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.sampleURC_Strategic_PlanService = sampleURC_Strategic_PlanService;
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleFreightVolumesService = sampleFreightVolumesService;
        this.currentPage = 0;
        this.pageSize = 10;
        this.filter = "";
        this.budgetItemsService = budgetItemsService;
        // this.sampleProcurementMethodService = sampleProcurementMethodService;
        this.sampleProcurementTypeService = sampleProcurementTypeService;
        this.fundsourceService = fundsourceService;
        this.sampleUrcAnlCodeService = sampleUrcAnlCodeService;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        addClassNames("budget-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        SplitLayout splitLayout2 = new SplitLayout();
        splitLayout.setSplitterPosition(75);
        splitLayout2.setSplitterPosition(50);
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
        procclass.setItems(ProcClass.Works, ProcClass.Supplies, ProcClass.Consultancy, ProcClass.Non_Consultancy, ProcClass.Disposal, ProcClass.Other);
        Coalevel1Box1 = new ComboBox<>("Class 1");
        displayBox.setItems(Display.GENERAL, Display.FREIGHT, Display.SALARIES);
        COASearchField1.setClearButtonVisible(true);
        COASearchField1.setPlaceholder("Filter by name or code");
        COASearchField1.setPrefixComponent(VaadinIcon.SEARCH.create());
        COASearchField1.setValueChangeMode(ValueChangeMode.EAGER);

        Coalevel1Box1.addValueChangeListener(er -> {
            if (!Coalevel1Box1.isEmpty()) {
                gridCOASetting.setItems(sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeLengthRange("", Coalevel1String(Coalevel1Box1.getValue())));
            }
        });
        COASearchField1.addValueChangeListener(er -> {
            //updateGridCoaSetting(er.getValue().trim());
            if (!Coalevel1Box1.isEmpty()) {
                gridCOASetting.setItems(sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeLengthRange(er.getValue().trim(), Coalevel1String(Coalevel1Box1.getValue())));
            }

        });

        // Configure Grid
        grid.addColumn("financialYear").setAutoWidth(true);
        grid.addColumn("startDate").setAutoWidth(true);
        grid.addColumn("closeDate").setAutoWidth(true);
        LitRenderer<Budget> importantRenderer = LitRenderer.<Budget>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", active -> active.isActive() ? "check" : "minus").withProperty("color",
                active -> active.isActive()
                ? "var(--lumo-primary-text-color)"
                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Open/Closed")
                .setFlexGrow(0)
                .setWidth("100px")
                .setResizable(false);

        grid.setItems(query -> sampleBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.setClassNameGenerator(budget -> {
            if (!budget.isActive()) {
                return "high-rating";
            }
            if (!budget.isActive()) {
                return "low-rating";
            }
            return null;
        });

        gridCurrencyData.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {

                sampleBudget = event.getValue();

                autogen.setEnabled(false);
                menuBar.setEnabled(true);
                currentfy = sampleBudget.getFinancialYear();
                populateForm(event.getValue());
                if (event.getValue() != null) {
                    if (event.getValue().isActive()) {
                        saveCurrency.setEnabled(true);
                        save.setEnabled(true);
                        saveCategory.setEnabled(true);
                    } else {
                        saveCurrency.setEnabled(false);
                        save.setEnabled(false);
                        saveCategory.setEnabled(false);
                    }

                }
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                sampleBudget = new Budget();
                clearForm();
                UI.getCurrent().navigate(BudgetView.class);
                autogen.setEnabled(true);
                menuBar.setEnabled(false);
            }
        });

        menuBar.setEnabled(false);
        autogen.setEnabled(true);

        Coalevel1Box.setItems(sampleCoalevel1);
        Coalevel1Box.setItemLabelGenerator(Coalevel1::getName);
        // Configure Form
        binder = new BeanValidationBinder<>(Budget.class);
        binder.bindInstanceFields(this);
        binder.forField(startDate).asRequired("Start Date is Required").bind("startDate");
        binder.forField(closeDate).asRequired("Close Date is Required").bind("closeDate");
        binder.forField(financialYear).asRequired("Financial Year is Required").bind("financialYear");
        binder.forField(important).bind("active");
        binder.forField(description).bind("description");

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        autogen.addClickListener(e -> {
            if (!startDate.isEmpty()) {
                LocalDate selectedDate = startDate.getValue().plusMonths(12).minusDays(1);
                closeDate.setValue(selectedDate);
                financialYear.setValue("FY" + startDate.getValue().getYear() + "-" + closeDate.getValue().getYear());
            } else {
                Notification not = Notification.show("Start Date Field is Empty");
                not.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        });

        Coalevel1Box1.addValueChangeListener(event -> {
            sampleCoalevel1COA = event.getValue();
        });

        createIconItem(edit_currency, VaadinIcon.MONEY_EXCHANGE, "Edit Currencies", null, true).addClickListener(e -> {

            Dialog g = new Dialog();
            CurrencyDataDialog(g).open();
            gridCurrencyData.setItems(query -> sampleCurrencyDataService.list(
                    PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());

        });
        createIconItem(currencyFy, VaadinIcon.AUTOMATION, "Edit Currencies by Budget", null, true).addClickListener(e -> {
            if (grid.asSingleSelect().getValue() != null) {
                Dialog g = new Dialog();
                CurrencyDialog(g).open();
                // gridCurrency.setItems(sampleCurrencyService.getfy2(currentfy));
            }
            gridCurrency.setItems(sampleCurrencyService.findCurrencyByBudget(sampleBudget));

        });
        createIconItem(level1COA, VaadinIcon.AUTOMATION, "Chart Of Accounts Configurations", null, true).addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.setWidth(90, Unit.PERCENTAGE);
            dialog.setHeight(100, Unit.PERCENTAGE);
            dialog.setHeaderTitle("CHART OF ACCOUNTS CONFIGURATIONS " + sampleBudget.getFinancialYear().toUpperCase());
            Button closeButton = new Button(new Icon("lumo", "cross"),
                    (ex) -> dialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getHeader().add(closeButton);
            dialog.setCloseOnOutsideClick(false);
            dialog.setModal(true);
            dialog.setDraggable(true);
            dialog.setResizable(true);
            dialog.add(createCOAConfigPane(sampleCoalevel1));
            dialog.open();
        });

        createIconItem(level4COA, VaadinIcon.AUTOMATION, "Chart Of Accounts Settings", null, true).addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.setWidth(90, Unit.PERCENTAGE);
            dialog.setHeight(100, Unit.PERCENTAGE);
            dialog.setHeaderTitle("CHART OF ACCOUNTS " + sampleBudget.getFinancialYear().toUpperCase());
            Button closeButton = new Button(new Icon("lumo", "cross"),
                    (ex) -> dialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getHeader().add(closeButton);
            dialog.setCloseOnOutsideClick(false);
            dialog.setModal(true);
            dialog.setDraggable(true);
            dialog.setResizable(true);
            COADialogSetting(dialog).open();
        });
        /*        createIconItem(level3COA, VaadinIcon.AUTOMATION, "Import Chart of Accounts", null, true).addClickListener(e -> {
        saveCoalevel1();
        refreshGridCOA();
        });*/

        createIconItem(budgetStructure, VaadinIcon.BUILDING, "Budget Categories", null, true).addClickListener(e -> {

            Dialog dialog = new Dialog();
            dialog.setWidth("500px");
            dialog.setHeight(70, Unit.PERCENTAGE);
            dialog.setHeaderTitle("Budget Category");
            Button closeButton = new Button(new Icon("lumo", "cross"),
                    (ex) -> dialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getHeader().add(closeButton);
            dialog.setCloseOnOutsideClick(false);
            dialog.setModal(true);
            dialog.setDraggable(true);
            dialog.setResizable(true);
            dialog.add(createOrganisationGridDialogLayout());
            dialog.open();
        });

        createIconItem(workplanView, VaadinIcon.BRIEFCASE, "Budget NPDIIII Alignment", null, true).addClickListener(e -> {
            Dialog workplanDialog = new Dialog();
            workplanDialog.getElement().setAttribute("aria-label", "Add note");
            workplanDialog.setHeaderTitle("ALIGNENT TO THE NDP111,NTMP,URC STRATEGIC PLAN");
            Button closeButton = new Button(new Icon("lumo", "cross"),
                    (er) -> workplanDialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            workplanDialog.getHeader().add(closeButton);
            workplanDialog.add(workplanLayOut());
            workplanDialog.setHeightFull();
            workplanDialog.setResizable(true);
            workplanDialog.setSizeFull();
            workplanDialog.open();

        });

        createIconItem(procurementMethodsView, VaadinIcon.BRIEFCASE, "Procurement Types", null, true).addClickListener(e -> {

            Dialog dialog = new Dialog();
            dialog.setWidth("450px");
            dialog.setHeight(70, Unit.PERCENTAGE);
            dialog.setHeaderTitle("Procurement Methods");
            Button closeButton = new Button(new Icon("lumo", "cross"),
                    (ex) -> dialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getHeader().add(closeButton);
            dialog.setCloseOnOutsideClick(false);
            dialog.setModal(true);
            dialog.setDraggable(true);
            dialog.setResizable(true);
            dialog.add(createProcurementTypeGridDialogLayout());
            dialog.open();

        });
        createIconItem(fundsourceView, VaadinIcon.BRIEFCASE, "Source of Funds", null, true).addClickListener(e -> {

            Dialog dialog = new Dialog();
            dialog.setWidth("450px");
            dialog.setHeight(70, Unit.PERCENTAGE);
            dialog.setHeaderTitle("Source of Funds");
            Button closeButton = new Button(new Icon("lumo", "cross"),
                    (ex) -> dialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getHeader().add(closeButton);
            dialog.setCloseOnOutsideClick(false);
            dialog.setModal(true);
            dialog.setDraggable(true);
            dialog.setResizable(true);
            dialog.add(createFundsourceGridDialogLayout());
            dialog.open();

        });
        createIconItem(sunFileJul, VaadinIcon.BRIEFCASE, "Extract Sun Budget (JUL-DEC) File", null, true).addClickListener(e -> {
            exportAndDownloadSunFile3();
        });
        createIconItem(sunFileJan, VaadinIcon.BRIEFCASE, "Extract Sun Budget (JAN-JUN) File", null, true).addClickListener(e -> {
            exportAndDownloadSunFile5();
        });
        createIconItem(sunStatFileJul, VaadinIcon.BRIEFCASE, "Extract Sun Statistics (JUL-DEC) File", null, true).addClickListener(e -> {
            exportAndDownloadSunFile3StatisticsJD();
        });
        createIconItem(sunStatFileJan, VaadinIcon.BRIEFCASE, "Extract Sun Statistics (JAN-JUN) File", null, true).addClickListener(e -> {
            exportAndDownloadSunFile5StatisticsJJ();
        });
        createIconItem(parameterFile, VaadinIcon.BRIEFCASE, "Extract Budget Parameters", null, true).addClickListener(e -> {
            extractFundsourcesAndActvities();

        });

        COASearchField.addValueChangeListener(event -> {
            String search = event.getValue().trim();
            updateGridCOA(search);
        });
        save.addClickListener((ClickEvent<Button> e) -> {
            if (validEditorTextFields().toString().length() > 0) {
                //Notification.show("Fill in the Empty Field (s)");
                NotificationDialogue(validEditorTextFields().toString());
                // Notification not = Notification.show("Fill in the Empty Field (s).");
                // not.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                int newdeterminant = 0;
                try {
                    if (BudgetView.this.sampleBudget == null) {
                        BudgetView.this.sampleBudget = new Budget();
                        //this.sampleBudget.setCurrency(null);
                        newdeterminant++;
                    }
                    if (sampleBudgetService.getBudget(financialYear.getValue()) == true && newdeterminant > 0) {
                        clearForm();
                        //refreshGrid();
                        Notification not = Notification.show("Budget Exists.");
                        not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } else {
                        binder.writeBean(BudgetView.this.sampleBudget);
                        if (newdeterminant > 0) {
                            createDialogSaveBudgetListLayout(BudgetView.this.sampleBudget).open();

                            //if(sourceBudget!=null){
                            //sampleBudgetService.savenewBudget(sourceBudget, BudgetView.this.sampleBudget);
                            //}
                        } else {
                            sampleBudgetService.update(BudgetView.this.sampleBudget);

                        }
                        clearForm();
                        refreshGrid();
                        UI.getCurrent().navigate(BudgetView.class);

                    }
                } catch (Exception validationException) {
                    // Notification.show(" An exception happened while trying to store the User details. ");
                    Notificationerror(" An exception happened while trying to store the Budget details. " + validationException.getMessage());
                }
            }
        });
        saveCurrencyData.addClickListener(e -> {
            if (validEditorTextFieldsCurrencyData().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsCurrencyData().toString());
            } else {

                CurrencyData data = gridCurrencyData.asSingleSelect().getValue();
                try {
                    if (data == null) {
                        data = new CurrencyData();
                        data.setCurrency(currencyDataNameField.getValue());
                        data.setCurrencyShort(currencyAbrField.getValue());
                        sampleCurrencyDataService.update(data);
                        clearFormCurrencyData();
                        refreshGridCurrencyData();
                        UI.getCurrent().navigate(BudgetView.class);
                    } else {
                        data.setCurrency(currencyDataNameField.getValue());
                        data.setCurrencyShort(currencyAbrField.getValue());
                        sampleCurrencyDataService.update(data);
                        clearFormCurrencyData();
                        refreshGridCurrencyData();
                        UI.getCurrent().navigate(BudgetView.class);
                    }

                } catch (Exception validationException) {
                    Notificationerror(" An exception happened while trying to store the Currency details. " + validationException.getMessage());
                    System.out.println(validationException.toString());
                }

            }

        });

        saveCurrency.addClickListener(e -> {
            int i = 0;
            if (validEditorTextFieldsCurrency().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsCurrency().toString());
            } else {

                Currency data = gridCurrency.asSingleSelect().getValue();
                try {
                    if (data == null) {
                        data = new Currency();
                        data.setBudget(sampleBudget);
                        data.setRate(getNonNullValue(currencyRateField));
                        data.setEnabled(currencyEnabledField.getValue());
                        data.setData(currencyName.getValue());

                    } else {
                        data.setBudget(sampleBudget);
                        data.setRate(getNonNullValue(currencyRateField));
                        data.setEnabled(currencyEnabledField.getValue());
                        data.setData(currencyName.getValue());
                        i = 1;
                    }

                } catch (Exception validationException) {
                    Notificationerror(" An exception happened while trying to store the Currency details. " + validationException.getMessage());
                    System.out.println(validationException.toString());
                }
                sampleCurrencyService.update(data);
                refreshGridCurrency();
                clearFormCurrency();
                if (i == 1) {
                    changeRate(data);
                }
                UI.getCurrent().navigate(BudgetView.class);

            }

        });

        saveUnit.addClickListener(e -> {
            Section sect = gridSection.asSingleSelect().getValue();
            if (sectselect == 1) {
                if (validEditorTextFieldsUnit().toString().length() > 0) {
                    NotificationDialogue2("Error", validEditorTextFieldsUnit().toString());
                } else {

                    D_Unit data = gridUnit.asSingleSelect().getValue();
                    try {
                        if (data == null) {
                            data = new D_Unit();
                            data.setUnit(UnitNameField.getValue());
                            data.setSection(sect);
                            data.setDepartment(sampleDepartment);
                            //data.setBudget(sampleBudget);

                        } else {
                            data.setUnit(UnitNameField.getValue());
                            data.setSection(sect);
                            data.setDepartment(sampleDepartment);
                            //data.setBudget(sampleBudget);
                        }

                    } catch (Exception validationException) {
                        Notificationerror(" An exception happened while trying to store the Currency details. " + validationException.getMessage());
                        System.out.println(validationException.toString());
                    }
                    sampleUnitService.update(data);
                    refreshGridUnit();
                    clearFormUnit();
                    UI.getCurrent().navigate(BudgetView.class);

                }
            } else {
                NotificationDialogue2("Null Selection", "Select a Section");
            }

        });

        saveSection.addClickListener(e -> {
            Department dept = gridDepartment.asSingleSelect().getValue();
            if (deptselect == 1) {
                if (validEditorTextFieldsSection().toString().length() > 0) {
                    NotificationDialogue(validEditorTextFieldsSection().toString());
                } else {

                    Section data = gridSection.asSingleSelect().getValue();
                    try {
                        if (data == null) {
                            data = new Section();
                            data.setSection(SectionNameField.getValue());
                            data.setDepartment(dept);
                            // data.setBudget(sampleBudget);
                        } else {
                            data.setSection(SectionNameField.getValue());
                            data.setDepartment(dept);
                            // data.setBudget(sampleBudget);

                        }

                    } catch (Exception validationException) {
                        Notificationerror(" An exception happened while trying to store the Currency details. " + validationException.getMessage());
                        Notification.show(data.toString());
                        System.out.println(validationException.toString());
                    }
                    sampleSectionService.update(data);
                    refreshGridSection();
                    clearFormSection();
                    UI.getCurrent().navigate(BudgetView.class);

                }
            } else {
                NotificationDialogue2("Null Selection", "Select a Department");
            }

        });
        saveDepartment.addClickListener(e -> {
            if (validEditorTextFieldsDepartment().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsDepartment().toString());
            } else {

                Department data = gridDepartment.asSingleSelect().getValue();
                try {
                    if (data == null) {
                        data = new Department();
                        data.setDepartment(DepartmentNameField.getValue());
                        data.setBudget(sampleBudget);
                        sampleBudgetService.update(sampleBudget);
                    } else {
                        data.setDepartment(DepartmentNameField.getValue());
                        data.setBudget(sampleBudget);
                        sampleBudgetService.update(sampleBudget);

                    }

                } catch (Exception validationException) {
                    Notificationerror(" An exception happened while trying to saving. " + validationException.getMessage());
                }
                sampleDepartmentService.update(data);
                sampleBudgetService.update(sampleBudget);
                refreshGridDepartment();
                clearFormDepartment();
                UI.getCurrent().navigate(BudgetView.class);

            }

        });

        saveCoalevel1.addClickListener(e -> {
            if (validEditorTextFieldsCoalevel1().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsCoalevel1().toString());
            } else {

                Coalevel1 data = gridCoalevel1.asSingleSelect().getValue();
                //List<Section> sect = sectionListNameField.asMultiSelect().getSelectedItems().stream().toList();
                try {
                    if (data == null) {
                        data = new Coalevel1();
                        data.setName(Coalevel1NameField.getValue());
                        //data.setBudget(sampleBudget);

                        // data.setSections(sect);
                    } else {
                        data.setName(Coalevel1NameField.getValue());
                        //data.setBudget(sampleBudget);

                    }

                } catch (Exception validationException) {
                    Notificationerror(" An exception happened while trying to saving. " + validationException.getMessage());
                }
                sampleCoalevel1Service.save(data);
                refreshGridCoalevel1();
                clearFormCoalevel1();
                UI.getCurrent().navigate(BudgetView.class);

            }

        });

        saveCoalevel13.addClickListener(e -> {
            if (validEditorTextFieldsCoalevel13().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsCoalevel13().toString());
            }
            if (gridCoalevel11.asSingleSelect().isEmpty()) {
                NotificationDialogue("Select a class");
            } else {
                Coalevel11 coa = gridCoalevel11.asSingleSelect().getValue();
                Coalevel13 data = gridCoalevel13.asSingleSelect().getValue();
                //List<Section> sect = sectionListNameField.asMultiSelect().getSelectedItems().stream().toList();
                try {
                    if (data == null) {
                        data = new Coalevel13();
                        data.setName(Coalevel13NameField.getValue());
                        data.setCoalevel11(coa);

                        // data.setSections(sect);
                    } else {
                        data.setName(Coalevel13NameField.getValue());
                        data.setCoalevel11(coa);

                    }

                } catch (Exception validationException) {
                    Notificationerror(" An exception happened while trying to saving. " + validationException.getMessage());
                }
                sampleCoalevel13Service.save(data);
                refreshGridCoalevel13(coa);
                clearFormCoalevel13();
                UI.getCurrent().navigate(BudgetView.class);

            }

        });
        saveCOA.addClickListener(q -> {
            if (validEditorTextFieldsCOA().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsCOA().toString());
            } else {
                if (!gridCOASetting.asSingleSelect().isEmpty()) {
                    UR5_ACNT acnt = gridCOASetting.asSingleSelect().getValue();
                    sampleCOA = sampleCoaService.findByCodeAndBudget(acnt.getAcntCode().trim(), sampleBudget);
                    try {
                        if (sampleCOA == null) {
                            sampleCOA = new COA();
                            sampleCOA.setName(COANameField.getValue());
                            sampleCOA.setCode(CodeField.getValue());
                            sampleCOA.setBudget(sampleBudget);
                            // sampleCOA.setCoalevel13(Coalevel13Box.getValue());
                            sampleCOA.setCoalevel1(Coalevel1Box1.getValue());
                            //sampleCOA.setCoalevel12(Coalevel12Box.getValue());
                            sampleCOA.setDeptsection(sections.getValue());
                            //sampleCOA.setCoalevel11(Coalevel11Box.getValue());
                            /*                            Set<Section> selectedSections = new HashSet<>(coaunits.getValue());
                            sampleCOA.setDsections(selectedSections);*/
                            sampleCOA.setProcclass(procclass.getValue());
                            sampleCOA.setDisplay(displayBox.getValue());
                            sampleCOA.setStateOpen(checkbox.getValue());

                        } else {
                            sampleCOA.setName(COANameField.getValue());
                            sampleCOA.setCode(CodeField.getValue());
                            sampleCOA.setBudget(sampleBudget);
                            //sampleCOA.setCoalevel13(Coalevel13Box.getValue());
                            sampleCOA.setCoalevel1(Coalevel1Box1.getValue());
                            //sampleCOA.setCoalevel12(Coalevel12Box.getValue());
                            sampleCOA.setDeptsection(sections.getValue());
                            sampleCOA.setProcclass(procclass.getValue());
                            sampleCOA.setDisplay(displayBox.getValue());
                            sampleCOA.setStateOpen(checkbox.getValue());

                        }

                    } catch (Exception validationException) {
                        Notificationerror(" An exception happened while trying to saving. " + validationException.getMessage());
                    }
                    sampleCoaService.save(sampleCOA);
                    clearFormCOA();

                } else {
                    Notificationwarning("Select an Item");
                }

            }

            UI.getCurrent().navigate(BudgetView.class);

        });
    }

    private void changeRate(Currency currency) {
        List<BudgetItems> findByDeptUnitAndBudget = budgetItemsService.findByAll();
        for (BudgetItems z : findByDeptUnitAndBudget) {
            BigDecimal curRate = currentExchangeRate(z);
            if (z.getCurrency().getData().getCurrencyShort().equals(currency.getData().getCurrencyShort())) {
                if (z.getJul() != null && z.getJul().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal jul = z.getJul().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    //BigDecimal jul2 = z.getJul().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setJul(jul);
                }
                if (z.getAug() != null && z.getAug().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal aug = z.getAug().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setAug(aug);
                }
                if (z.getSep() != null && z.getSep().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal sep = z.getSep().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setSep(sep);
                }
                if (z.getOct() != null && z.getOct().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal oct = z.getOct().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setOct(oct);
                }
                if (z.getNov() != null && z.getNov().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal nov = z.getNov().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setNov(nov);
                }
                if (z.getDec() != null && z.getDec().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal dec = z.getDec().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setDec(dec);
                }
                if (z.getJan() != null && z.getJan().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal jan = z.getJan().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setJan(jan);
                }
                if (z.getFeb() != null && z.getFeb().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal feb = z.getFeb().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setFeb(feb);
                }
                if (z.getMar() != null && z.getMar().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal jul = z.getMar().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setMar(jul);
                }
                if (z.getApr() != null && z.getApr().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal jul = z.getApr().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setApr(jul);
                }
                if (z.getMay() != null && z.getMay().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal jul = z.getMay().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setMay(jul);
                }
                if (z.getJun() != null && z.getJun().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal jul = z.getJun().divide(currentExchangeRate(z), MathContext.DECIMAL128).multiply(currency.getRate());
                    z.setJun(jul);
                }
                budgetItemsService.update(z);
            }
        }
    }

    public BigDecimal currentExchangeRate(BudgetItems z) {
        // Ensure that cost and quantity values are not zero
        BigDecimal costValue = z.getCost();
        BigDecimal qtyValue = z.getQty();

        if (costValue.compareTo(BigDecimal.ZERO) == 0 || qtyValue.compareTo(BigDecimal.ZERO) == 0) {
            // Handle the case where costValue or qtyValue is zero
            return BigDecimal.ZERO; // Or another appropriate value
        }

        // Calculate the total value
        BigDecimal total = calculateMonthSum(z);

        // Perform division with rounding mode
        return total.divide(costValue, MathContext.DECIMAL128)
                .divide(qtyValue, MathContext.DECIMAL128)
                .setScale(6, RoundingMode.HALF_UP); // Adjust scale and rounding mode as needed
    }

    private BigDecimal getNonNullValue(BigDecimalField field) {
        return field.getValue() != null ? field.getValue() : BigDecimal.ZERO;
    }

    public BigDecimal calculateMonthSum(BudgetItems budget) {
        BigDecimal sum = BigDecimal.ZERO; // Initialize sum to zero

        // Add up the values of all the month fields
        sum = sum.add(budget.getJan());
        sum = sum.add(budget.getFeb());
        sum = sum.add(budget.getMar());
        sum = sum.add(budget.getApr());
        sum = sum.add(budget.getMay());
        sum = sum.add(budget.getJun());
        sum = sum.add(budget.getJul());
        sum = sum.add(budget.getAug());
        sum = sum.add(budget.getSep());
        sum = sum.add(budget.getOct());
        sum = sum.add(budget.getNov());
        sum = sum.add(budget.getDec());

        return sum;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //Optional<UUID> samplePersonId = event.getRouteParameters().get(BUDGET_ID).map(UUID::fromString);
        Optional<Integer> samplePersonId = event.getRouteParameters().get(BUDGET_ID).map(Integer::parseInt);
        if (samplePersonId.isPresent()) {
            Optional<Budget> samplePersonFromBackend = sampleBudgetService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested budget was not found, ID = %s", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(BudgetView.class);
            }
        }

    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("budget-editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("budget-editor");
        editorLayoutDiv.add(editorDiv);
        HorizontalLayout h = new HorizontalLayout();

        FormLayout formLayout = new FormLayout();
        startDate = new DatePicker("Start Date");
        startDate.setHelperText("Date is Required");
        startDate.setRequired(true);
        startDate.setRequiredIndicatorVisible(true);
        startDate.setErrorMessage("Required");
        startDate.setClearButtonVisible(true);
        startDate.setWidthFull();
        binder.forField(startDate).bind("startDate");

        autogen = new Button("Set");
        //autogen.setDisableOnClick(true);
        h.add(startDate, autogen);
        h.setAlignItems(FlexComponent.Alignment.BASELINE);
        h.setSizeFull();
        h.setSpacing(false);
        h.setWidthFull();

        closeDate = new DatePicker("Close Date");
        closeDate.setHelperText("Date Required");
        closeDate.setRequired(true);
        closeDate.setRequiredIndicatorVisible(true);
        closeDate.setErrorMessage("Required");
        closeDate.setClearButtonVisible(true);
        closeDate.setEnabled(false);
        binder.forField(closeDate).bind("closeDate");

        financialYear = new TextField("Financial Year");
        financialYear.setHelperText("Financial Year is Required");
        financialYear.setRequiredIndicatorVisible(true);
        financialYear.setErrorMessage("Required");
        financialYear.setClearButtonVisible(true);
        financialYear.setEnabled(false);
        binder.forField(financialYear).bind("financialYear");

        important = new Checkbox("Active");
        binder.forField(important).bind("active");

        description = new TextArea("Description");
        description.setHelperText("Rate is Required");
        description.setClearButtonVisible(true);
        description.addClassName("descType");
        binder.forField(description).bind("description");
        formLayout.add(h, closeDate, financialYear, important, description);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private StringBuilder validEditorTextFields() {
        StringBuilder build = new StringBuilder();
        if (startDate.isEmpty()) {
            build.append("Starting Date field is empty \n");

        }
        if (isValidDate(startDate.getValue().toString()) == false) {
            build.append("Starting Date Invalid \n");
        }

        if (closeDate.isEmpty()) {
            build.append("Closing Date field is empty \n");

        }
        if (financialYear.isEmpty()) {
            build.append("Financial Year field is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsCurrencyData() {
        StringBuilder build = new StringBuilder();
        if (currencyDataNameField.isEmpty()) {
            build.append("Curreny field is empty \n");

        }

        if (currencyAbrField.isEmpty()) {
            build.append("Currency Abriviation Field is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsCurrency() {
        StringBuilder build = new StringBuilder();
        if (currencyRateField.isEmpty()) {
            build.append("Curreny Rate field is empty \n");

        }
        if (currencyName.isEmpty()) {
            build.append("Curreny field is empty \n");

        }

        return build;
    }

    private StringBuilder validEditorTextFieldsUnit() {
        StringBuilder build = new StringBuilder();
        if (UnitNameField.isEmpty()) {
            build.append("Unit field is empty \n");

        }

        return build;
    }

    private StringBuilder validEditorTextFieldsSection() {
        StringBuilder build = new StringBuilder();
        if (SectionNameField.isEmpty()) {
            build.append("Section field is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsDepartment() {
        StringBuilder build = new StringBuilder();
        if (DepartmentNameField.isEmpty()) {
            build.append("Department field is empty \n");

        }

        return build;
    }

    private StringBuilder validEditorTextFieldsCoalevel1() {
        StringBuilder build = new StringBuilder();
        if (Coalevel1NameField.isEmpty()) {
            build.append("Name field 1 is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsOrganisation() {
        StringBuilder build = new StringBuilder();
        if (BudgetTypeNameField.isEmpty()) {
            build.append("Name field 1 is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsProcurementMethod() {
        StringBuilder build = new StringBuilder();
        if (ProcurementMethodNameField.isEmpty()) {
            build.append("Name field 1 is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsProcurementType() {
        StringBuilder build = new StringBuilder();
        if (ProcurementTypeNameField.isEmpty()) {
            build.append("Name field 1 is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsFundsource() {
        StringBuilder build = new StringBuilder();
        if (FundsourceNameField.isEmpty()) {
            build.append("Name field 1 is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsCOA() {
        StringBuilder build = new StringBuilder();
        if (Coalevel1Box1.isEmpty()) {
            build.append("Select code Category \n");

        }
        if (CodeField.isEmpty()) {
            build.append("Code field 1 is empty \n");

        }
        if (COANameField.isEmpty()) {
            build.append("Code Name field is empty \n");

        }
        /*        if (Coalevel12Box.isEmpty()) {
        build.append("Screen View Category is empty \n");
        
        }*/

        return build;
    }

    private StringBuilder validEditorTextFieldsCoalevel11() {
        StringBuilder build = new StringBuilder();
        if (Coalevel11NameField.isEmpty()) {
            build.append("Name field 2 is empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsCoalevel12() {
        StringBuilder build = new StringBuilder();
        if (Coalevel12NameField.isEmpty()) {
            build.append("Name field is 3 empty \n");

        }
        return build;
    }

    private StringBuilder validEditorTextFieldsCoalevel13() {
        StringBuilder build = new StringBuilder();
        if (Coalevel13NameField.isEmpty()) {
            build.append("Name field is 3 empty \n");

        }
        return build;
    }

    public static boolean isValidDate(final String date) {

        boolean valid = false;

        try {

            // ResolverStyle.STRICT for 30, 31 days checking, and also leap year.
            LocalDate.parse(date,
                    DateTimeFormatter.ofPattern("uuuu-M-d")
                            .withResolverStyle(ResolverStyle.STRICT)
            );

            valid = true;

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            valid = false;
        }

        return valid;
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
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(addMeunuBar(), grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        grid.setItems(query -> sampleBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    private void refreshGridCurrencyData() {
        gridCurrencyData.select(null);
        gridCurrencyData.getDataProvider().refreshAll();
        gridCurrencyData.setItems(query -> sampleCurrencyDataService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    private void refreshGridCurrency() {
        gridCurrency.select(null);
        gridCurrency.getDataProvider().refreshAll();
        gridCurrency.setItems(sampleCurrencyService.findCurrencyByBudget(sampleBudget));
    }

    private void refreshGridUnit() {
        gridUnit.select(null);
        gridUnit.getDataProvider().refreshAll();
        /*        gridUnit.setItems(query -> sampleUnitService.listAllCurrencyData(
        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
        .stream());*/
        gridUnit.setItems(sampleUnitService.findBySection(sampleSection));
    }

    private void refreshGridSection() {
        gridSection.select(null);
        gridSection.getDataProvider().refreshAll();
        /*        gridSection.setItems(query -> sampleSectionService.listAllCurrencyData(
        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
        .stream());*/
        gridSection.setItems(sampleSectionService.findByDepartment(sampleDepartment));
    }

    private void refreshGridOrganisation() {
        gridOrganisation.select(null);
        gridOrganisation.getDataProvider().refreshAll();
        gridOrganisation.setItems(query -> sampleOrganisationService.findByBudget(sampleBudget,
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    /*    private void refreshGridProcurementMethod() {
    gridProcurementMethod.select(null);
    gridProcurementMethod.getDataProvider().refreshAll();
    gridProcurementMethod.setItems(sampleProcurementMethodService.getAllProcurementMethods());
    }*/
    private void refreshGridProcurementType() {
        gridProcurementType.select(null);
        gridProcurementType.getDataProvider().refreshAll();
        gridProcurementType.setItems(sampleProcurementTypeService.getAllProcurementTypes());
    }

    private void refreshGridFundsource() {
        gridFundsource.select(null);
        gridFundsource.getDataProvider().refreshAll();
        gridFundsource.setItems(fundsourceService.findFundsourcesByBudget(sampleBudget));
    }

    private void refreshGridDepartment() {
        gridDepartment.select(null);
        gridDepartment.getDataProvider().refreshAll();
        /*        gridDepartment.setItems(query -> sampleDepartmentService.listAllCurrencyData(
        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
        .stream());*/
        gridDepartment.setItems(sampleDepartmentService.findDepartmentByBudget(sampleBudget));
    }

    private void refreshGridCoalevel1() {
        gridCoalevel1.select(null);
        gridCoalevel1.getDataProvider().refreshAll();
        gridCoalevel1.setItems(sampleCoalevel1Service.findCoalevel1ByBudgetId());
    }

    private void refreshGridCOA() {
        gridCOA.select(null);
        gridCOA.getDataProvider().refreshAll();
        gridCOA.setItems(sampleCoaService.findByBudgetAndCoalevel1(sampleBudget, sampleCoalevel1COA));

    }

    private void refreshGridCoalevel11(Coalevel1 coa) {
        gridCoalevel11.select(null);
        gridCoalevel11.getDataProvider().refreshAll();
        gridCoalevel11.setItems(sampleCoalevel11Service.findCoalevel11ByClass1Id(coa));
    }

    private void refreshGridCoalevel12(Coalevel1 coa) {
        gridCoalevel12.select(null);
        gridCoalevel12.getDataProvider().refreshAll();
        gridCoalevel12.setItems(sampleCoalevel12Service.findCoalevel12ByClass1Id(coa));
    }

    private void refreshGridCoalevel13(Coalevel11 coa) {
        gridCoalevel13.select(null);
        gridCoalevel13.getDataProvider().refreshAll();
        gridCoalevel13.setItems(sampleCoalevel13Service.findCoalevel13ByClass1Id(coa));
    }

    private void refreshGridCurrency2() {
        gridCurrency.select(null);
        gridCurrency.getDataProvider().refreshAll();
        gridCurrency.setItems(sampleCurrencyService.findCurrencyByBudget(sampleBudget));
    }

    private void clearForm() {
        populateForm(null);
        //autogen.setEnabled(false);
    }

    private void clearFormCurrencyData() {
        currencyDataNameField.clear();
        currencyAbrField.clear();
    }

    private void clearFormCOA() {
        // Coalevel13Box.clear();
        CodeField.clear();
        COANameField.clear();
        // Coalevel11Box.clear();
        //Coalevel12Box.clear();
        //coaunits.clear();
        sections.clear();
        procclass.clear();
        displayBox.clear();
        checkbox.clear();
    }

    private void clearFormCurrency() {
        currencyRateField.clear();
        currencyRateField.clear();
        currencyName.clear();
        currencyEnabledField.clear();
    }

    private void clearFormUnit() {
        UnitNameField.clear();
    }

    private void clearFormSection() {
        SectionNameField.clear();
        //unitListNameField.asMultiSelect().deselectAll();
    }

    private void clearFormDepartment() {
        DepartmentNameField.clear();
    }

    private void clearFormCoalevel1() {
        Coalevel1NameField.clear();
    }

    private void clearFormCoalevel11() {
        Coalevel11NameField.clear();
    }

    private void clearFormCoalevel12() {
        Coalevel12NameField.clear();
    }

    private void clearFormCoalevel13() {
        Coalevel13NameField.clear();
    }

    private void populateForm(Budget value) {
        this.sampleBudget = value;
        binder.readBean(this.sampleBudget);

    }

    private void populateFormCurrencyData(CurrencyData value) {
        this.sampleCurrencyData = value;
        // binderCurrencyData.readBean(this.sampleCurrencyData);
    }

    private Component NotificationDialogue(String errorMessage) {
        Dialog dialog = new Dialog();

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

    private Component NotificationDialogue2(String title, String errorMessage) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(false);
        dialog.setCloseOnOutsideClick(false);

        dialog.setHeaderTitle(title);
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

    private Component Notificationerror(String errorMessage) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(errorMessage));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);

        notification.open();
        return notification;
    }

    private MenuBar addMeunuBar() {
        menuBar = new MenuBar();
        currency = menuBar.addItem("Currency");
        edit_currency = currency.getSubMenu();
        currencyFy = currency.getSubMenu();

        chart_of_accounts = menuBar.addItem("COA Configurations");
        level1COA = chart_of_accounts.getSubMenu();
        level2COA = chart_of_accounts.getSubMenu();
        level3COA = chart_of_accounts.getSubMenu();
        level4COA = chart_of_accounts.getSubMenu();

        structure = menuBar.addItem("Budget Categories");
        department = structure.getSubMenu();
        section = structure.getSubMenu();
        unit = structure.getSubMenu();
        budgetStructure = structure.getSubMenu();

        workplan = menuBar.addItem("NDP III Alignment");
        workplanView = workplan.getSubMenu();

        procurementMethods = menuBar.addItem("Procurement Settings");
        procurementMethodsView = procurementMethods.getSubMenu();

        Fundsource = menuBar.addItem("Source of Funds");
        fundsourceView = Fundsource.getSubMenu();

        SunFile = menuBar.addItem("Sun Setting");
        sunFileJul = SunFile.getSubMenu();
        sunFileJan = SunFile.getSubMenu();
        sunStatFileJul = SunFile.getSubMenu();
        sunStatFileJan = SunFile.getSubMenu();
        parameterFile = SunFile.getSubMenu();
        return menuBar;
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
            String label, String ariaLabel, String fy) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
            String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private VerticalLayout createCurrencyDataDialogLayout(Dialog g) {
        Text label = new Text("EDIT CURRENCY DATA");

        currencyDataNameField = new TextField("Currency");
        currencyAbrField = new TextField("Currency Abr");

        VerticalLayout dialogLayout = new VerticalLayout(label, currencyDataNameField,
                currencyAbrField);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        Button but = new Button("Import From Sun");
        but.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        but.addSingleClickListener(e -> {
            if (sampleCurrencyDataService.count() < 1) {
                sampleCurrencyDataService.sunCurr();
                refreshGridCurrencyData();
            } else {
                Notification notes = Notification.show("Already Imported");
                notes.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        });
        Button but2 = new Button("Import From BgTool");
        but2.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        but2.addSingleClickListener(e -> {
            sampleCurrencyDataService.fetchFromBdgt();
            refreshGridCurrencyData();
        });
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancelCurrencyData.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCurrencyData.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelCurrencyData.addClickListener(e -> {
            gridCurrencyData.deselectAll();
            currencyDataNameField.setValue("");
            currencyAbrField.setValue("");
        });

        Div editorLayoutDiv = new Div();
        buttonLayout.add(saveCurrencyData, cancelCurrencyData, but, but2);
        editorLayoutDiv.add(buttonLayout);
        dialogLayout.add(editorLayoutDiv);

        return dialogLayout;
    }

    private VerticalLayout createCurrencyDialogLayout(Dialog g) {
        Text label = new Text("EDIT CURRENCY DETAIL");
        currencyName = new ComboBox("Currency");
        currencyRateField = new BigDecimalField("Rate");
        currencyEnabledField = new Checkbox("Enable");
        Button refreshCurrencyTable = new Button("Refresh");

        VerticalLayout dialogLayout = new VerticalLayout(label, currencyName, currencyRateField,
                currencyEnabledField);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        HorizontalLayout buttonLayout2 = new HorizontalLayout();
        cancelCurrency.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCurrency.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshCurrencyTable.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelCurrency.addClickListener(e -> {
            gridCurrency.deselectAll();
            currencyRateField.clear();
            currencyEnabledField.clear();
            currencyName.clear();
        });
        refreshCurrencyTable.addClickListener(e -> {
            refreshGridCurrency2();
        });
        Div editorLayoutDiv = new Div();
        buttonLayout.add(saveCurrency, cancelCurrency);
        buttonLayout2.add(refreshCurrencyTable);
        editorLayoutDiv.add(buttonLayout);
        dialogLayout.add(editorLayoutDiv);

        currencyName.setItems(sampleCurrencyDataService.listAllCurrencyData());
        currencyName.setItemLabelGenerator(CurrencyData::getCurrency);

        return dialogLayout;
    }

    private VerticalLayout createCurrencyDataGridDialogLayout() {
        gridCurrencyData = new Grid<>(CurrencyData.class, false);
        // Configure Grid
        gridCurrencyData.addColumn("currency").setAutoWidth(true);
        gridCurrencyData.addColumn("currencyShort").setAutoWidth(true);
        // Configure Form

        gridCurrencyData.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                currencyDataNameField.setValue(event.getValue().getCurrency());
                currencyAbrField.setValue(event.getValue().getCurrencyShort());
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                clearFormCurrencyData();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });
        VerticalLayout dialogLayout = new VerticalLayout(gridCurrencyData);

        return dialogLayout;
    }

    private VerticalLayout createCoalevel1GridDialogLayout() {
        Coalevel1NameField = new TextField("Name");
        Coalevel1NameField.setPlaceholder("Enter Class Name");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button refresh = new Button("Refresh");
        buttonLayout.setClassName("button-layout");
        cancelCoalevel1.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCoalevel1.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        cancelCoalevel1.addClickListener(e -> {
            sampleCoalevel1 = new Coalevel1();
            gridCoalevel1.deselectAll();
            Coalevel1NameField.clear();
        });

        refresh.addClickListener(e -> {
            refreshGridCoalevel1();
        });
        Div editorLayoutDiv = new Div();
        buttonLayout.add(Coalevel1NameField, saveCoalevel1, cancelCoalevel1, refresh);
        editorLayoutDiv.add(buttonLayout);
        gridCoalevel1 = new Grid<>(Coalevel1.class, false);
        gridCoalevel1.setHeight(200, Unit.PIXELS);
        gridCoalevel1.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Configure Grid
        gridCoalevel1.addColumn("name").setAutoWidth(true);

        ComponentRenderer<Button, Coalevel1> deleteRenderer = new ComponentRenderer<>(person -> {
            Icon deleteIcon = VaadinIcon.TRASH.create();
            Button deleteButton = new Button(deleteIcon);
            deleteButton.addClickListener(event -> {
                // Perform delete operation here
                Caolevel1ConfirmDialog(person.getId()).open();
                refreshGridCoalevel1();
                refreshGridCoalevel11(new Coalevel1());
                refreshGridCoalevel12(new Coalevel1());
                // Notification.show("Deletiiiiii");

            });
            return deleteButton;
        });
        gridCoalevel1.addColumn(deleteRenderer).setHeader("Delete").setWidth("80px").setFlexGrow(0);

        refreshGridCoalevel1();
        // Configure Form
        gridCoalevel1.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Coalevel1NameField.setValue(event.getValue().getName());
                refreshGridCoalevel11(event.getValue());
                refreshGridCoalevel12(event.getValue());
                sampleCoalevel1 = event.getValue();
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                sampleCoalevel1 = new Coalevel1();
                clearFormCoalevel1();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });
        H1 title = new H1("CHART OF ACCOUNT LEVEL 1");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        VerticalLayout dialogLayout = new VerticalLayout(title, new Hr(), buttonLayout, gridCoalevel1);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private VerticalLayout createCoalevel11GridDialogLayout(Coalevel1 coa) {
        Coalevel11NameField = new TextField("Name");
        Coalevel11NameField.setPlaceholder("Enter Class Name");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button refresh = new Button("Refresh");
        buttonLayout.setClassName("button-layout");
        cancelCoalevel11.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCoalevel11.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        cancelCoalevel11.addClickListener(e -> {
            gridCoalevel11.deselectAll();
            Coalevel11NameField.clear();

        });

        refresh.addClickListener(e -> {
            refreshGridCoalevel11(coa);
        });
        Div editorLayoutDiv = new Div();
        buttonLayout.add(Coalevel11NameField, saveCoalevel11, cancelCoalevel11, refresh);
        editorLayoutDiv.add(buttonLayout);
        gridCoalevel11 = new Grid<>(Coalevel11.class, false);
        gridCoalevel11.setHeight(200, Unit.PIXELS);
        gridCoalevel11.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Configure Grid
        gridCoalevel11.addColumn("name").setAutoWidth(true);
        ComponentRenderer<Button, Coalevel11> deleteRenderer = new ComponentRenderer<>(person -> {
            Icon deleteIcon = VaadinIcon.TRASH.create();
            Button deleteButton = new Button(deleteIcon);
            deleteButton.addClickListener(event -> {
                // Perform delete operation here
                Caolevel11ConfirmDialog(person.getId()).open();

            });
            return deleteButton;
        });
        gridCoalevel11.addColumn(deleteRenderer).setHeader("Delete").setWidth("80px").setFlexGrow(0);

        // Configure Form
        gridCoalevel11.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Coalevel11NameField.setValue(event.getValue().getName());
                sampleCoalevel11 = event.getValue();
                refreshGridCoalevel12(sampleCoalevel1);
                refreshGridCoalevel13(sampleCoalevel11);
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                sampleCoalevel11 = new Coalevel11();
                clearFormCoalevel11();
                clearFormCoalevel13();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });
        saveCoalevel11.addSingleClickListener(e -> {
            Coalevel11 coalevel11 = gridCoalevel11.asSingleSelect().getValue();
            if (coalevel11 == null) {
                coalevel11 = new Coalevel11();
            }

            coalevel11.setName(Coalevel11NameField.getValue());
            sampleCoalevel11Service.save(coalevel11);

        });
        H1 title = new H1("CHART OF ACCOUNT LEVEL 2");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        VerticalLayout dialogLayout = new VerticalLayout(title, new Hr(), buttonLayout, gridCoalevel11);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private VerticalLayout createCoalevel13GridDialogLayout(Coalevel11 coa) {
        Coalevel13NameField = new TextField("Name");
        Coalevel13NameField.setPlaceholder("Enter Class Name");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button refresh = new Button("Refresh");
        buttonLayout.setClassName("button-layout");
        cancelCoalevel13.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCoalevel13.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        cancelCoalevel13.addClickListener(e -> {
            gridCoalevel13.deselectAll();
            Coalevel13NameField.clear();
        });

        refresh.addClickListener(e -> {
            refreshGridCoalevel13(coa);
        });
        Div editorLayoutDiv = new Div();
        buttonLayout.add(Coalevel13NameField, saveCoalevel13, cancelCoalevel13, refresh);
        editorLayoutDiv.add(buttonLayout);
        gridCoalevel13 = new Grid<>(Coalevel13.class, false);
        // gridCoalevel12.setHeight(200, Unit.PIXELS);
        gridCoalevel13.setSizeFull();
        gridCoalevel13.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Configure Grid
        gridCoalevel13.addColumn("name").setAutoWidth(true);
        ComponentRenderer<Button, Coalevel13> deleteRenderer = new ComponentRenderer<>(person -> {
            Icon deleteIcon = VaadinIcon.TRASH.create();
            Button deleteButton = new Button(deleteIcon);
            deleteButton.addClickListener(event -> {
                // Perform delete operation here
                Caolevel13ConfirmDialog(person.getId()).open();

            });
            return deleteButton;
        });
        gridCoalevel13.addColumn(deleteRenderer).setHeader("Delete").setWidth("80px").setFlexGrow(0);
        // Configure Form
        gridCoalevel13.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Coalevel13NameField.setValue(event.getValue().getName());

                UI.getCurrent().navigate(BudgetView.class);

            } else {

                clearFormCoalevel13();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });
        H1 title = new H1("CHART OF ACCOUNT LEVEL 3");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        VerticalLayout dialogLayout = new VerticalLayout(title, new Hr(), buttonLayout, gridCoalevel13);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogLayout.setSizeFull();
        // dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private VerticalLayout createCoalevel12GridDialogLayout(Coalevel1 coa) {
        Coalevel12NameField = new TextField("Name");
        Coalevel12NameField.setPlaceholder("Enter Class Name");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button refresh = new Button("Refresh");
        buttonLayout.setClassName("button-layout");
        cancelCoalevel12.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCoalevel12.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        cancelCoalevel12.addClickListener(e -> {
            gridCoalevel12.deselectAll();
            Coalevel12NameField.clear();
        });

        refresh.addClickListener(e -> {
            refreshGridCoalevel12(coa);
        });
        Div editorLayoutDiv = new Div();
        buttonLayout.add(Coalevel12NameField, saveCoalevel12, cancelCoalevel12, refresh);
        editorLayoutDiv.add(buttonLayout);
        gridCoalevel12 = new Grid<>(Coalevel12.class, false);
        // gridCoalevel12.setHeight(200, Unit.PIXELS);
        gridCoalevel12.setSizeFull();
        gridCoalevel12.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Configure Grid
        gridCoalevel12.addColumn("name").setAutoWidth(true);
        ComponentRenderer<Button, Coalevel12> deleteRenderer = new ComponentRenderer<>(person -> {
            Icon deleteIcon = VaadinIcon.TRASH.create();
            Button deleteButton = new Button(deleteIcon);
            deleteButton.addClickListener(event -> {
                // Perform delete operation here
                Caolevel12ConfirmDialog(person.getId()).open();

            });
            return deleteButton;
        });
        gridCoalevel12.addColumn(deleteRenderer).setHeader("Delete").setWidth("80px").setFlexGrow(0);
        // Configure Form
        Coalevel12NameField.setEnabled(false);
        cancelCoalevel12.setEnabled(false);
        saveCoalevel12.setEnabled(false);
        gridCoalevel12.setEnabled(false);
        refresh.setEnabled(false);

        gridCoalevel12.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Coalevel12NameField.setValue(event.getValue().getName());

                UI.getCurrent().navigate(BudgetView.class);

            } else {

                clearFormCoalevel12();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });
        H1 title = new H1("SYSTEM BUDGET DISPLAY CLASSIFICATION");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        VerticalLayout dialogLayout = new VerticalLayout(title, new Hr(), buttonLayout, gridCoalevel12);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogLayout.setSizeFull();
        // dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private VerticalLayout createOrganisationGridDialogLayout() {
        BudgetTypeNameField = new TextField("Budget Category");
        BudgetTypeNameField.setPlaceholder("Budget Category");

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button cancel = new Button("Cancel");
        buttonLayout.setClassName("button-layout");
        saveCategory.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(BudgetTypeNameField, saveCategory);
        BudgetTypeNameField.setWidthFull();
        BudgetTypeNameField.setClearButtonVisible(true);
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        gridOrganisation = new Grid<>(Organisation.class, false);
        gridOrganisation.setHeight(200, Unit.PIXELS);
        gridOrganisation.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Configure Grid
        gridOrganisation.addColumn("name").setAutoWidth(true);
        gridOrganisation.addColumn("code").setAutoWidth(true);
        gridOrganisation.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        gridOrganisation.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                BudgetTypeNameField.setValue(event.getValue().getName());
                sampleOrganisation = event.getValue();
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                sampleOrganisation = new Organisation();
                BudgetTypeNameField.clear();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });

        saveCategory.addClickListener(e -> {
            if (validEditorTextFieldsOrganisation().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsOrganisation().toString());
            } else {
                sampleOrganisation = gridOrganisation.asSingleSelect().getValue();
                if (sampleOrganisation == null) {
                    Organisation org = new Organisation();
                    org.setBudget(sampleBudget);
                    org.setName(BudgetTypeNameField.getValue());
                    org.setCode(generateBudgetTypeCode());
                    sampleOrganisation = sampleOrganisationService.create(org);  // Assign the created organisation to sampleOrganisation
                } else {
                    sampleOrganisation.setName(BudgetTypeNameField.getValue());
                    sampleOrganisationService.update(sampleOrganisation);
                }
            }

            BudgetTypeNameField.clear();
            refreshGridOrganisation();
        });

        refreshGridOrganisation();
        // gridOrganisation.select(sampleOrganisationService.findById(Long.valueOf("1")));
        //gridOrganisation.setEnabled(false);
        //System.out.println(sampleOrganisationService.findById(Long.valueOf("1")).toString());
        H1 title = new H1("BUDGETS");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        VerticalLayout dialogLayout = new VerticalLayout(buttonLayout, gridOrganisation);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogLayout.setWidth("100%");
        dialogLayout.setHeight("100%");
        //dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private VerticalLayout createProcurementTypeGridDialogLayout() {
        ProcurementTypeNameField = new TextField("Procurement Type");
        ProcurementTypeNameField.setPlaceholder("Procurement Type");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button save = new Button("Save");
        Button cancel = new Button("Delete");
        buttonLayout.setClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(ProcurementTypeNameField, save, cancel);
        ProcurementTypeNameField.setWidthFull();
        ProcurementTypeNameField.setClearButtonVisible(true);
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        gridProcurementType = new Grid<>(ProcurementType.class, false);
        gridProcurementType.setHeight(200, Unit.PIXELS);
        gridProcurementType.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Configure Grid
        gridProcurementType.addColumn("procuremntType").setAutoWidth(true);
        gridProcurementType.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        gridProcurementType.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                ProcurementTypeNameField.setValue(event.getValue().getProcuremntType());
                sampleProcurementType = event.getValue();
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                sampleProcurementType = new ProcurementType();
                ProcurementTypeNameField.clear();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });

        save.addClickListener(e -> {
            if (validEditorTextFieldsProcurementType().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsProcurementType().toString());
            } else {
                sampleProcurementType = gridProcurementType.asSingleSelect().getValue();
                if (sampleProcurementType == null) {
                    ProcurementType org = new ProcurementType();
                    org.setProcuremntType(ProcurementTypeNameField.getValue());
                    sampleProcurementType = sampleProcurementTypeService.save(org);  // Assign the created organisation to sampleOrganisation
                } else {
                    sampleProcurementType.setProcuremntType(ProcurementTypeNameField.getValue());
                    sampleProcurementTypeService.save(sampleProcurementType);
                }
            }

            ProcurementTypeNameField.clear();
            refreshGridProcurementType();
        });

        cancel.addClickListener(e -> {
            if (!gridProcurementType.asSingleSelect().isEmpty()) {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Delete Item");
                dialog.setText(
                        "An Item has been selected. Do you want to delete it?");

                dialog.setCancelable(true);
                dialog.addCancelListener(event -> setStatus("Canceled"));
                dialog.setRejectable(true);
                dialog.setRejectText("Discard");
                dialog.addRejectListener(event -> setStatus("Discarded"));

                dialog.setConfirmText("Delete");
                dialog.addConfirmListener(event -> {
                    sampleProcurementTypeService.deleteProcurementType(gridProcurementType.asSingleSelect().getValue());
                    refreshGridProcurementType();
                });

                dialog.open();
            } else {
                NotificationDialogue("Select a Procurement Method");
            }
        });

        refreshGridProcurementType();
        // gridOrganisation.select(sampleOrganisationService.findById(Long.valueOf("1")));
        //gridOrganisation.setEnabled(false);
        //System.out.println(sampleOrganisationService.findById(Long.valueOf("1")).toString());
        H1 title = new H1("BUDGETS");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        VerticalLayout dialogLayout = new VerticalLayout(buttonLayout, gridProcurementType);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogLayout.setWidth("100%");
        dialogLayout.setHeight("100%");
        //dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private VerticalLayout createFundsourceGridDialogLayout() {
        FundsourceNameField = new TextField("Fund Source");
        FundsourceNameField.setPlaceholder("Fund Source");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button save = new Button("Save");
        Button cancel = new Button("Delete");
        buttonLayout.setClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(FundsourceNameField, save, cancel);
        FundsourceNameField.setWidthFull();
        FundsourceNameField.setClearButtonVisible(true);
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        gridFundsource = new Grid<>(Fundsource.class, false);
        gridFundsource.setHeight(200, Unit.PIXELS);
        gridFundsource.setSelectionMode(Grid.SelectionMode.SINGLE);
        // Configure Grid
        gridFundsource.addColumn("fundsource").setAutoWidth(true);
        gridFundsource.addColumn("code").setAutoWidth(true);
        gridFundsource.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        refreshGridFundsource();
        gridFundsource.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                FundsourceNameField.setValue(event.getValue().getFundsource());
                sampleFundsource = event.getValue();
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                sampleFundsource = new Fundsource();
                FundsourceNameField.clear();
                UI.getCurrent().navigate(BudgetView.class);

            }
        });

        save.addClickListener(e -> {

            if (validEditorTextFieldsFundsource().toString().length() > 0) {
                NotificationDialogue(validEditorTextFieldsFundsource().toString());

            } else {

                sampleFundsource = gridFundsource.asSingleSelect().getValue();
                if (sampleFundsource == null) {
                    Fundsource org = new Fundsource();
                    org.setFundsource(FundsourceNameField.getValue());
                    org.setBudget(sampleBudget);
                    org.setCode(generateFundSourceCode());
                    sampleFundsource = fundsourceService.save(org);
                } else {
                    sampleFundsource.setFundsource(FundsourceNameField.getValue());
                    fundsourceService.save(sampleFundsource);
                }
            }

            FundsourceNameField.clear();
            refreshGridFundsource();
        });

        cancel.addClickListener(e -> {
            if (!gridFundsource.asSingleSelect().isEmpty()) {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Delete Item");
                dialog.setText(
                        "An Item has been selected. Do you want to delete it?");

                dialog.setCancelable(true);
                dialog.addCancelListener(event -> setStatus("Canceled"));
                dialog.setRejectable(true);
                dialog.setRejectText("Discard");
                dialog.addRejectListener(event -> setStatus("Discarded"));

                dialog.setConfirmText("Delete");
                dialog.addConfirmListener(event -> {
                    fundsourceService.deleteFundsource(gridFundsource.asSingleSelect().getValue());
                    refreshGridFundsource();
                });

                dialog.open();
            } else {
                NotificationDialogue("Select a Fund source");
            }
        });

        refreshGridFundsource();
        // gridOrganisation.select(sampleOrganisationService.findById(Long.valueOf("1")));
        //gridOrganisation.setEnabled(false);
        //System.out.println(sampleOrganisationService.findById(Long.valueOf("1")).toString());
        H1 title = new H1("BUDGETS");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        VerticalLayout dialogLayout = new VerticalLayout(buttonLayout, gridFundsource);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        dialogLayout.setWidth("100%");
        dialogLayout.setHeight("100%");
        //dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private VerticalLayout createCurrencyGridDialogLayout() {
        gridCurrency = new Grid<>(Currency.class, false);
        // Configure Grid

        gridCurrency.addColumn(new ComponentRenderer<>(Currency -> {
            Text textField = new Text("");
            if (Optional.ofNullable(Currency.getBudget()).isPresent()) {

                //Optional<CurrencyData> curr = sampleCurrencyDataService.get(Currency.getCurrencyid());
                //Currency curr2=Currency.getData().getCurrencyShort()
                if (Currency.getData() != null) {
                    textField.setText(Currency.getData().getCurrencyShort());
                } else {
                    textField.setText("Not Found");
                }

                return textField;
            } else {
                textField.setText("Empty");
                return textField;
            }
        })).setHeader("Name");
        gridCurrency.addColumn("rate").setAutoWidth(true);
        LitRenderer<Currency> importantRenderer = LitRenderer.<Currency>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isEnabled() ? "check" : "minus").withProperty("color",
                important -> important.isEnabled()
                ? "var(--lumo-primary-text-color)"
                : "var(--lumo-disabled-text-color)");

        gridCurrency.addColumn(importantRenderer).setHeader("Enable/Disable").setAutoWidth(true);

        gridCurrency.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                currencyRateField.setValue(event.getValue().getRate());
                currencyEnabledField.setValue(event.getValue().isEnabled());
                //currencyName.setValue(sampleCurrencyDataService.get(event.getValue().getCurrencyid()).get());
                currencyName.setValue(event.getValue().getData());
                //UI.getCurrent().navigate(BudgetView.class);

            } else {
                clearFormCurrency();
                //UI.getCurrent().navigate(BudgetView.class);

            }
        });
        VerticalLayout dialogLayout = new VerticalLayout(gridCurrency);

        return dialogLayout;
    }

    private Dialog CurrencyDataDialog(Dialog g) {
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(65);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(createCurrencyDataGridDialogLayout());
        splitLayout.addToSecondary(createCurrencyDataDialogLayout(g));

        Dialog dialog = new Dialog();
        dialog.setClassName("dialogCurrencyData");
        //dialog.setSizeFull();
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        //dialog.setHeight("400px");
        dialog.setWidth("80%");
        dialog.setHeaderTitle("Currencies");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        dialog.setCloseOnOutsideClick(false);
        dialog.add(splitLayout);
        dialog.setHeight(400, Unit.PIXELS);
        dialog.setWidth("80%");

        return dialog;
    }

    private Dialog CurrencyDialog(Dialog g) {
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(65);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(createCurrencyGridDialogLayout());
        splitLayout.addToSecondary(createCurrencyDialogLayout(g));

        Dialog dialog = new Dialog();
        dialog.setClassName("dialogCurrencyData");
        dialog.setSizeFull();
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        //dialog.setHeight("400px");
        //dialog.setWidth("600px");
        dialog.setHeaderTitle("Currency Details");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        dialog.setCloseOnOutsideClick(false);
        dialog.add(splitLayout);
        dialog.setHeight(400, Unit.PIXELS);
        dialog.setWidth(700, Unit.PIXELS);

        return dialog;
    }

    private VerticalLayout createbudgetStructurePane() {
        VerticalLayout master0 = new VerticalLayout();
        master0.add(createOrganisationGridDialogLayout());
        master0.setWidthFull();
        master0.setHeightFull();

        return master0;
    }

    private VerticalLayout createCOAConfigPane(Coalevel1 coa2) {
        VerticalLayout master0 = new VerticalLayout();
        master0.add(new Hr());

        SplitLayout splitLayout = new SplitLayout(createCoalevel1GridDialogLayout(), createCoalevel11GridDialogLayout(coa2));
        splitLayout.setSplitterPosition(50);
        splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        master0.add(splitLayout);
        splitLayout.setWidth(100, Unit.PERCENTAGE);
        splitLayout.setHeight(100, Unit.PERCENTAGE);
        master0.add(new Hr());

        SplitLayout splitLayout1 = new SplitLayout(createCoalevel12GridDialogLayout(coa2), createCoalevel13GridDialogLayout(sampleCoalevel11));
        splitLayout1.setOrientation(SplitLayout.Orientation.HORIZONTAL);//splitLayout1.setMaxHeight("350px");
        splitLayout1.setSplitterPosition(50);
        master0.add(splitLayout1);
        splitLayout1.setWidth(100, Unit.PERCENTAGE);
        splitLayout1.setHeight(100, Unit.PERCENTAGE);
        master0.setWidthFull();
        master0.setHeightFull();

        return master0;
    }

    private VerticalLayout createCOAPane() {
        VerticalLayout master0 = new VerticalLayout();
        master0.add(new Hr());

        // master0.add(createCOAGridDialogLayout());
        master0.setWidthFull();
        master0.setHeightFull();

        return master0;
    }

    private MenuBar COAMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_PRIMARY);
        menuBar.addClassNames("coamenubar");
        menuBar.addItem("View Chart Of Accounts");
        MenuItem item = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        SubMenu subItems = item.getSubMenu();
        /*        subItems.addItem("From Sun System Info").addClickListener(e -> {
        
        if (!sampleCoaService.isCoaTableEmpty(sampleBudget)) {
        
        Notification notification = Notification.show("Chart of Accounts Already has values");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        
        } else {
        sampleCoaService.saveFromSunDb(sampleBudget);
        sampleCoalevel1 = sampleCoalevel1Service.findByNameAndBudget("Income", sampleBudget);
        Coalevel1Box.setValue(sampleCoalevel1);
        refreshGridCOA();
        Notification notification = Notification.show("Chart of Accounts Has Successfully been Imported");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        
        });*/

        subItems.addItem("From Previous Budget").addClickListener(e -> {

            /*            if (!sampleCoaService.isCoaTableEmpty(sampleBudget)) {
            
            Notification notification = Notification.show("Chart of Accounts Already has values");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            
            } else {
            createDialogBudgetListLayout().open();
            }*/
        });
        subItems.addItem("View Chart of Accounts By Unit").addClickListener(e -> {

        });
        subItems.addItem("View Chart of Accounts By Section").addClickListener(e -> {

        });
        subItems.addItem("View Chart of Accounts By Department").addClickListener(e -> {

        });
        menuBar.addItem("Update Chart Of Account");
        MenuItem item2 = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        SubMenu subItems2 = item2.getSubMenu();
        subItems2.addItem("Update").addClickListener(e -> {

        });
        return menuBar;
    }

    private FormLayout createCOADialogLayout(Dialog g) {
        List<Section> units = sampleSectionService.findAllSectionsByBudget(sampleBudget);

        coaunits.setLabel("Choose Section Units");

        coaunits.setItems(units);
        coaunits.setItemLabelGenerator(Section::getSection);
        coaunits.addThemeVariants(MultiSelectComboBoxVariant.LUMO_ALIGN_LEFT);

        Text label = new Text("CODE DETAILS");
        CodeField = new TextField("Code");
        CodeField.setPlaceholder("Account Code");
        COANameField = new TextField("Name");
        COANameField.setPlaceholder("Account Name");
        Button refreshCurrencyTable = new Button("Refresh");

        Button selectallUnits = new Button("Select all");

        HorizontalLayout layContainer = new HorizontalLayout();
        layContainer.setPadding(true);
        layContainer.setSpacing(false);
        layContainer.setAlignItems(FlexComponent.Alignment.BASELINE);
        layContainer.setWidthFull();
        layContainer.getStyle().set("width", "18rem").set("max-width", "100%");

        //lay.add(coaunits, selectallUnits);
        Set<Section> allItems = new HashSet<>(sampleSectionService.findAllSectionsByBudget(sampleBudget));
        selectallUnits.addClickListener(e -> {
            // coaunits.clear();
            // coaunits.select(allItems);

        });

        COANameField = new TextField("Name");
        COANameField.setPlaceholder("Enter Class Name ");
        // Coalevel1Box = new ComboBox("Class 1");
        Coalevel1Box.setPlaceholder("Select COA Category");

        //Coalevel12Box = new ComboBox("Screen View Category");
        //Coalevel12Box.setPlaceholder("Select Screen View Category");
        //Coalevel13Box = new ComboBox("COA Sub Category 2");
        //Coalevel13Box.setPlaceholder("Select COA Sub Category 2");
        sections.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        sections.setItems(query -> sampleUrcDeptSectionAnlDimbgtService.findByANL_CODEStartingWithD2(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        HorizontalLayout buttonLayoutrefresh = new HorizontalLayout();
        Button sellectAll = new Button("Select All");
        Button clearAll = new Button("Clear");
        buttonLayoutrefresh.add(sections, sellectAll, clearAll);
        clearAll.addSingleClickListener(e -> {
            sections.clear();
        });
        sellectAll.addSingleClickListener(e -> {
            List<UrcDeptSectionAnlDimbgt> findByANL_CODEStartingWithS = sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims();
            sections.select(findByANL_CODEStartingWithS);

        });
        sections.setWidthFull();
        buttonLayoutrefresh.setWidth("100%");
        buttonLayoutrefresh.setAlignItems(Alignment.BASELINE);
        FormLayout dialogLayout = new FormLayout(label, new Hr(), CodeField, COANameField, procclass, displayBox, checkbox, buttonLayoutrefresh);
        /*        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);*/
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        dialogLayout.setSizeFull();
        dialogLayout.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        HorizontalLayout buttonLayout2 = new HorizontalLayout();
        cancelCOA.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCOA.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshCurrencyTable.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelCOA.addClickListener(e -> {
            // Coalevel11Box.clear();
            // Coalevel12Box.clear();
            CodeField.clear();
            COANameField.clear();

            coaSAVE = new COA();
            gridCOA.deselectAll();
        });
        refreshCurrencyTable.addClickListener(e -> {
            refreshGridCurrency2();
        });
        Div editorLayoutDiv = new Div();
        buttonLayout.add(saveCOA, cancelCOA);
        buttonLayout2.add(refreshCurrencyTable);
        editorLayoutDiv.add(buttonLayout);
        dialogLayout.add(editorLayoutDiv);

        return dialogLayout;
    }

    private VerticalLayout createCOAASettingGridDialogLayout() {

        FormLayout lay = new FormLayout();
        lay.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2),
                new ResponsiveStep("800px", 3),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("1000px", 4));
        // layContainer.setAlignItems(FlexComponent.Alignment.BASELINE);
        gridCOASetting = new Grid<>(UR5_ACNT.class, false);
        gridCOASetting.addColumn(UR5_ACNT::getAcntCode)
                .setHeader("Code")
                .setAutoWidth(true);
        gridCOASetting.addColumn("descr").setAutoWidth(true);

        Coalevel1Box1.setItems(sampleCoalevel1Service.findCoalevel1ByBudgetId());
        Coalevel1Box1.setItemLabelGenerator(Coalevel1::getName);

        gridCOASetting.asSingleSelect().addValueChangeListener(event -> {
            clearFormCOA();
            if (event.getValue() != null) {
                sampleCOA = sampleCoaService.findByCodeAndBudgetWithDSections(event.getValue().getAcntCode(), sampleBudget);
                
               // sampleCOA = sampleCoaService.findByCodeAndBudget(event.getValue().getAcntCode(), sampleBudget);

                if (sampleCOA != null) {
                 

                    COANameField.setValue(event.getValue().getDescr());
                    CodeField.setValue(event.getValue().getAcntCode());
                    //      Coalevel12Box.setValue(sampleCOA.getCoalevel12());
                    sections.setValue(sampleCOA.getDeptsection());
                    displayBox.setValue(sampleCOA.getDisplay());
                    checkbox.setValue(sampleCOA.isStateOpen());
                    if (sampleCOA.getProcclass() != null) {
                        procclass.setValue(sampleCOA.getProcclass());
                    }

                    coaunits.clear();
                    if (!sampleCOA.getDsections().isEmpty()) {
                        coaunits.select(new HashSet<>(sampleCOA.getDsections()));
                    }

                } else {
                    sampleCOA = new COA();
                    COANameField.setValue(event.getValue().getDescr());
                    CodeField.setValue(event.getValue().getAcntCode());
                    sections.setValue(sampleCOA.getDeptsection());
                    displayBox.setValue(sampleCOA.getDisplay());
                    checkbox.setValue(sampleCOA.isStateOpen());
                    if (sampleCOA.getProcclass() != null) {
                        procclass.setValue(sampleCOA.getProcclass());
                    }

                    coaunits.clear();
                    if (!sampleCOA.getDsections().isEmpty()) {
                        coaunits.select(new HashSet<>(sampleCOA.getDsections()));
                    }

                }

            }

        });

        lay.add(Coalevel1Box1, COASearchField1);
        VerticalLayout dialogLayout = new VerticalLayout(lay, gridCOASetting);

        return dialogLayout;
    }

    private Dialog COADialog(Dialog g) {
        VerticalLayout dialogLayout = new VerticalLayout();
        //dialogLayout.add(new Hr());
        //dialogLayout.setPadding(false);
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(65);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(new Text(""));
        splitLayout.addToSecondary(createCOADialogLayout(g));

        Dialog dialog = new Dialog();
        dialog.setClassName("dialogCurrencyData");
        dialog.setSizeFull();
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        //dialog.setHeight("400px");
        //dialog.setWidth("600px");
        dialog.setHeaderTitle("Chart Of Accounts");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        dialog.setCloseOnOutsideClick(false);
        dialogLayout.add(COAMenuBar(), new Hr(), splitLayout);
        dialog.add(dialogLayout);
        dialog.setHeight(100, Unit.PERCENTAGE);
        dialog.setWidth(80, Unit.PERCENTAGE);

        return dialog;
    }

    private Dialog COADialogSetting(Dialog g) {
        VerticalLayout dialogLayout = new VerticalLayout();
        //dialogLayout.add(new Hr());
        //dialogLayout.setPadding(false);
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(65);
        splitLayout.setHeightFull();
        //splitLayout.setSizeFull();
        splitLayout.addToPrimary(createCOAASettingGridDialogLayout());
        splitLayout.addToSecondary(createCOADialogLayout(g));

        Dialog dialog = new Dialog();
        dialog.setClassName("dialogCurrencyData");
        dialog.setSizeFull();
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        //dialog.setHeight("400px");
        //dialog.setWidth("600px");
        dialog.setHeaderTitle("Section Chart Of Accounts Setting");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        dialog.setCloseOnOutsideClick(false);
        dialogLayout.setHeightFull();
        dialogLayout.add(COAMenuBar(), new Hr(), splitLayout);
        dialog.add(dialogLayout);
        dialog.setHeight(100, Unit.PERCENTAGE);
        dialog.setWidth(80, Unit.PERCENTAGE);

        return dialog;
    }

    private ConfirmDialog Caolevel11ConfirmDialog(Long id) {

        status = new Span();
        status.setVisible(false);
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete");
        dialog.setText(
                "There are unsaved changes. Do you want to discard them?");

        dialog.setCancelable(true);
        dialog.addCancelListener(event -> setStatus("Canceled"));

        dialog.setRejectable(true);
        dialog.setRejectText("Discard");
        dialog.addRejectListener(event -> setStatus("Discarded"));

        dialog.setConfirmText("Delete");
        dialog.addConfirmListener(event -> {
            if (gridCoalevel1.asSingleSelect().isEmpty()) {
                gridCoalevel1.getSelectionModel().select(sampleCoalevel11Service.findById(id).getCoalevel1());
            }
            sampleCoalevel11Service.deleteById(id);
            refreshGridCoalevel11(sampleCoalevel1);
            Notification.show(
                    String.format("The requested samplePerson was not found, ID = %s", sampleCoalevel11Service.findById(id).getId()), 30000,
                    Notification.Position.BOTTOM_START);
        }
        );
        return dialog;
    }

    private ConfirmDialog Caolevel1ConfirmDialog(Long id) {

        status = new Span();
        status.setVisible(false);
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete");
        dialog.setText(
                "If you Delete, even data associated to this class will be deleted");

        dialog.setCancelable(true);
        dialog.addCancelListener(event -> setStatus("Canceled"));

        dialog.setRejectable(true);
        dialog.setRejectText("Discard");
        dialog.addRejectListener(event -> setStatus("Discarded"));

        dialog.setConfirmText("Delete");
        dialog.addConfirmListener(event -> {

            sampleCoalevel11Service.deleteByCoalevel11Id(id);
            sampleCoalevel12Service.deleteByCoalevel12Id(id);
            sampleCoalevel1Service.deleteById(id);

            refreshGridCoalevel1();
        }
        );
        return dialog;
    }

    private ConfirmDialog Caolevel12ConfirmDialog(Long id) {

        status = new Span();
        status.setVisible(false);
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Record");
        dialog.setText(
                "There are unsaved changes. Do you want to discard them?");

        dialog.setCancelable(true);
        dialog.addCancelListener(event -> setStatus("Canceled"));

        dialog.setRejectable(true);
        dialog.setRejectText("Discard");
        dialog.addRejectListener(event -> setStatus("Discarded"));

        dialog.setConfirmText("Delete");
        dialog.addConfirmListener(event -> {
            if (gridCoalevel1.asSingleSelect().isEmpty()) {
                gridCoalevel1.getSelectionModel().select(sampleCoalevel12Service.findById(id).getCoalevel1());
            }
            sampleCoalevel12Service.deleteById(id);
            refreshGridCoalevel12(sampleCoalevel1);
            /*                Notification.show(
              String.format("The requested samplePerson was not found, ID = %s", sampleCoalevel12Service.findById(id).getId()), 30000,
              Notification.Position.BOTTOM_START);*/
        }
        );
        return dialog;
    }

    private ConfirmDialog Caolevel13ConfirmDialog(Long id) {

        status = new Span();
        status.setVisible(false);
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Record");
        dialog.setText(
                "There are unsaved changes. Do you want to discard them?");

        dialog.setCancelable(true);
        dialog.addCancelListener(event -> setStatus("Canceled"));

        dialog.setRejectable(true);
        dialog.setRejectText("Discard");
        dialog.addRejectListener(event -> setStatus("Discarded"));

        dialog.setConfirmText("Delete");
        dialog.addConfirmListener(event -> {
            if (gridCoalevel11.asSingleSelect().isEmpty()) {
                gridCoalevel11.getSelectionModel().select(sampleCoalevel13Service.findById(id).getCoalevel11());
            }
            sampleCoalevel13Service.deleteById(id);
            refreshGridCoalevel13(sampleCoalevel11);
            /*                Notification.show(
              String.format("The requested samplePerson was not found, ID = %s", sampleCoalevel12Service.findById(id).getId()), 30000,
              Notification.Position.BOTTOM_START);*/
        }
        );
        return dialog;
    }

    private void setStatus(String value) {
        status.setText("Status: " + value);
        status.setVisible(true);
    }

    private void saveCoalevel1() {
        Coalevel1 coa1 = new Coalevel1();
        coa1.setName("Income");

        if (sampleCoalevel1Service.existsByBudgetAndName(coa1.getName()) == false) {
            //coa1.setBudget(budget);
            coa1.setCode(1);
            sampleCoalevel1Service.save(coa1);
        }

        Coalevel1 coa2 = new Coalevel1();
        coa2.setName("Operation Expenditure");
        if (sampleCoalevel1Service.existsByBudgetAndName(coa2.getName()) == false) {
            //coa2.setBudget(budget);
            coa2.setCode(2);
            sampleCoalevel1Service.save(coa2);
        }

        Coalevel1 coa3 = new Coalevel1();
        coa3.setName("Capital Expenditure");
        if (sampleCoalevel1Service.existsByBudgetAndName(coa3.getName()) == false) {
            //coa3.setBudget(budget);
            coa3.setCode(3);
            sampleCoalevel1Service.save(coa3);
        }
        List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan = sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();

        for (UR5_ACNT b : findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan) {
            COA coa = new COA();
            //coa.setBudget(budget);
            coa.setCode(b.getAcntCode());
            coa.setName(b.getDescr());
            coa.setStateOpen(true);
            coa.setDisplay(Display.GENERAL);
            sampleCoaService.save(coa);
        }
    }

    public void updateGridCoa(String search) {

        gridCOA.setItems(sampleCoaService.getCOAList(sampleBudget, Coalevel1Box.getValue(), search));
    }

    public void updateGridCoaSetting(String search, String code) {

        gridCOASetting.setItems(sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeLengthRange(search, code));
        //gridCOASetting.setItems(sampleUR5_ACNTService.findAll());

    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    public void updateGridCOA(String search) {
        if (!search.isEmpty()) {

        }

    }

    private Dialog createDialogBudgetListLayout() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Previous Budgets");

        RadioButtonGroup<Budget> budgetList = new RadioButtonGroup<>();
        budgetList.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        budgetList.setItemLabelGenerator(Budget::getFinancialYear);
        budgetList.setLabel("Budgets Financial Periods");
        budgetList.setItems(sampleBudgetService.findAllExcept(sampleBudget.getFinancialYear()));

        dialog.add(budgetList);
        Button selectButton = new Button("Select", e -> {
            if (!budgetList.isEmpty()) {
                int num2 = sampleCoaService.saveFromPreviousBudget(sampleBudget, budgetList.getValue());
                if (num2 > 0) {
                    Notification notification = Notification.show("Chart of Accounts Imported successifully " + budgetList.getValue().toString());
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    Notification notification = Notification.show("Nothing imported ");
                    notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                }

            } else {
                Notification notification = Notification.show("Select a Budget " + "Null");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        });
        selectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(selectButton);

        //dialog.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialog;
    }

    private Dialog createDialogSaveBudgetListLayout(Budget targetbudget) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Previous Budgets");

        RadioButtonGroup<Budget> budgetList = new RadioButtonGroup<>();
        budgetList.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        budgetList.setItemLabelGenerator(Budget::getFinancialYear);
        budgetList.setLabel("Budgets Financial Periods");
        budgetList.setItems(sampleBudgetService.findAllExcept(targetbudget.getFinancialYear()));

        dialog.add(budgetList);
        Button selectButton = new Button("Select", e -> {
            if (!budgetList.isEmpty()) {
                sampleBudgetService.savenewBudget(budgetList.getValue(), targetbudget);
                refreshGrid();
                UI.getCurrent().navigate(BudgetView.class);

            } else {
                Notification notification = Notification.show("Select a Budget " + "Null");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            dialog.close();

        });
        selectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(selectButton);
        UI.getCurrent().navigate(BudgetView.class);
        //dialog.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialog;
    }

    private FormLayout workplanLayOut() {
        FormLayout form = new FormLayout();
        form.setSizeFull();
        VerticalLayout ndp3 = ndp3Layout();
        //ndp3.setWidthFull();

        VerticalLayout ntmp = NTMPLayout();
        //ntmp.setWidthFull();
        VerticalLayout nbfa = NBFALayout();
        //nbfa.setWidthFull();
        VerticalLayout urcsp = URC_Strategic_PlanLayout();
        //urcsp.setWidthFull();
        VerticalLayout urcpa = URC_Priority_AreaLayout();
        //urcpa.setWidthFull();
        VerticalLayout urca = Urc_ActivitiesLayout();
        //urca.setWidthFull();

        form.add(ndp3, ntmp, nbfa, urcsp, urcpa, urca);
        form.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2),
                new ResponsiveStep("800px", 3),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("1000px", 4));
        //form.setColspan(username, 2);
        return form;
    }

    private VerticalLayout ndp3Layout() {
        VerticalLayout ndp3 = new VerticalLayout();
        ndp3.setSpacing(false);
        HorizontalLayout lay = new HorizontalLayout();
        lay.setWidthFull();
        Text title = new Text("NDPIII OBJECTIVE");

        // Apply a style to make the title bold
        //title.getElement().getStyle().set("font-weight", "bold");
        nameNdp1 = new TextArea();
        nameNdp1.setWidthFull();
        // nameNdp1.setMinHeight("100px");
        nameNdp1.setMaxHeight("100px");
        nameNdp1.setMaxLength(950);
        nameNdp1.setValueChangeMode(ValueChangeMode.EAGER);
        nameNdp1.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + 950);
        });
        Button save = new Button("Save");
        lay.add(nameNdp1);
        lay.setAlignItems(Alignment.BASELINE);
        ndpGrid = new Grid<>(NDPIII_Objective.class, false);
        ndpGrid.addColumn(NDPIII_Objective::getObjective).setHeader("Objective");
        ndpGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        ndpGrid.setHeight("200px");
        ndpGrid.setItems(query -> sampleNDPIII_ObjectiveService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        ndpGrid.asSingleSelect().addValueChangeListener(eq -> {
            ntmpGrid.select(null);
            nbfaGrid.select(null);
            URC_Strategic_PlanGrid.select(null);
            URC_Priority_AreasGrid.select(null);
            Urc_ActivitiesGrid.select(null);
            if (eq.getValue() != null) {
                sampleNDPIII_Objective = eq.getValue();
                nameNdp1.setValue(eq.getValue().getObjective());

                ndp3objSelect = 1;
            } else {
                //sampleNDPIII_Objective = new NDPIII_Objective();
                ndp3objSelect = 0;
            }

            refreshNTMPGrid(ntmpGrid, sampleNDPIII_Objective);
            // refreshNBFAGrid(nbfaGrid, sampleNational_Transport_Master_Plan);
            // refreshNBFAGrid(nbfaGrid, null);
            nbfaGrid.setItems(new National_Budget_Focus_Areas());
            URC_Strategic_PlanGrid.setItems(new URC_Strategic_Plan());
            URC_Priority_AreasGrid.setItems(new URC_Priority_Areas());
            Urc_ActivitiesGrid.setItems(new Urc_Activities());
            nameNdp2.clear();
            nameNdp3.clear();
            nameNdp4.clear();
            nameNdp5.clear();
            nameNdp6.clear();
            //refreshURCSPGrid(URC_Strategic_PlanGrid);
            //refreshURCPAGrid(URC_Priority_AreasGrid);
            //refreshURCAGrid(Urc_ActivitiesGrid);
        });

        save.addClickListener(s -> {
            if (!nameNdp1.isEmpty()) {
                NDPIII_Objective selected = ndpGrid.asSingleSelect().getValue();
                if (selected == null) {
                    selected = new NDPIII_Objective();

                }
                selected.setBudget(sampleBudget);
                selected.setObjective(nameNdp1.getValue());
                sampleNDPIII_ObjectiveService.update(selected);
                refreshNDP3ObjGrid(ndpGrid);

            } else {
                warningDilogue("Fill the required fields");
            }
            nameNdp1.clear();
            UI.getCurrent().navigate(BudgetView.class);
        });
        ndp3.add(title, lay, save, ndpGrid);
        return ndp3;
    }

    private void refreshNDPGrid(Grid<NDPIII_Objective> grid) {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        grid.setItems(query -> sampleNDPIII_ObjectiveService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    private VerticalLayout NTMPLayout() {
        VerticalLayout ndp3 = new VerticalLayout();
        ndp3.setSpacing(false);
        HorizontalLayout lay = new HorizontalLayout();
        lay.setWidthFull();
        Text title = new Text("NATIONAL TRANSPORT MASTER PLAN");

        // Apply a style to make the title bold
        //title.getElement().getStyle().set("font-weight", "bold");
        nameNdp2 = new TextArea();
        nameNdp2.setWidthFull();
        //nameNdp2.setMinHeight("100px");
        nameNdp2.setMaxHeight("100px");
        nameNdp2.setMaxLength(950);
        nameNdp2.setValueChangeMode(ValueChangeMode.EAGER);
        nameNdp2.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + 950);
        });
        Button save = new Button("Save");
        lay.add(nameNdp2);
        lay.setAlignItems(Alignment.BASELINE);
        ntmpGrid = new Grid<>(National_Transport_Master_Plan.class, false);
        ntmpGrid.addColumn(National_Transport_Master_Plan::getName).setHeader("National Transport Master Plan");
        ntmpGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        ntmpGrid.setHeight("200px");
        ntmpGrid.asSingleSelect().addValueChangeListener(eq -> {
            //nbfaGrid.select(null);
            URC_Strategic_PlanGrid.select(null);
            URC_Priority_AreasGrid.select(null);
            Urc_ActivitiesGrid.select(null);
            if (eq.getValue() != null) {
                sampleNational_Transport_Master_Plan = eq.getValue();
                nameNdp2.setValue(eq.getValue().getName());
                ntmpSelect = 1;
                refreshNBFAGrid(nbfaGrid, sampleNational_Transport_Master_Plan);
                //refreshURCSPGrid(URC_Strategic_PlanGrid);

                //refreshURCPAGrid(URC_Priority_AreasGrid);
                //refreshURCAGrid(Urc_ActivitiesGrid);
                URC_Strategic_PlanGrid.setItems(new URC_Strategic_Plan());
                URC_Priority_AreasGrid.setItems(new URC_Priority_Areas());
                Urc_ActivitiesGrid.setItems(new Urc_Activities());
                nameNdp3.clear();
                nameNdp4.clear();
                nameNdp5.clear();
                nameNdp6.clear();
            } else {
                ntmpSelect = 0;
            }

        });
        save.addClickListener(s -> {
            if (!nameNdp2.isEmpty() || !ndpGrid.asSingleSelect().isEmpty()) {
                National_Transport_Master_Plan selected = ntmpGrid.asSingleSelect().getValue();
                if (selected == null) {
                    selected = new National_Transport_Master_Plan();

                }
                selected.setNdp111Objective(ndpGrid.asSingleSelect().getValue());
                selected.setName(nameNdp2.getValue());
                sampleNational_Transport_Master_PlanService.update(selected);

            } else {
                warningDilogue("Select an objective or else fill the required fields");
            }
            refreshNTMPGrid(ntmpGrid, sampleNDPIII_Objective);
            nameNdp2.clear();
        });
        ndp3.add(title, lay, save, ntmpGrid);
        return ndp3;
    }

    private VerticalLayout NBFALayout() {
        VerticalLayout ndp3 = new VerticalLayout();
        ndp3.setSpacing(false);
        HorizontalLayout lay = new HorizontalLayout();
        lay.setWidthFull();
        Text title = new Text("NATIONAL BUDGET FOCUS AREAS");

        // Apply a style to make the title bold
        //title.getElement().getStyle().set("font-weight", "bold");
        nameNdp3 = new TextArea();
        nameNdp3.setWidthFull();
        //nameNdp3.setMinHeight("100px");
        nameNdp3.setMaxHeight("100px");
        nameNdp3.setMaxLength(950);
        nameNdp3.setValueChangeMode(ValueChangeMode.EAGER);
        nameNdp3.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + 950);
        });
        Button save = new Button("Save");
        lay.add(nameNdp3);
        lay.setAlignItems(Alignment.BASELINE);
        nbfaGrid = new Grid<>(National_Budget_Focus_Areas.class, false);

        nbfaGrid.addColumn(National_Budget_Focus_Areas::getName).setHeader("National Budget Focus Areas");
        nbfaGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        nbfaGrid.setHeight("200px");
        nbfaGrid.setItems(query -> sampleNational_Budget_Focus_AreasService.listByNationalTransportMasterPlan(ntmpGrid.asSingleSelect().getValue(),
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        nbfaGrid.asSingleSelect().addValueChangeListener(eq -> {

            if (eq.getValue() != null) {
                sampleNational_Budget_Focus_Areas = eq.getValue();
                nameNdp3.setValue(eq.getValue().getName());
                nbfaSelect = 1;
                refreshURCSPGrid(URC_Strategic_PlanGrid);
                // refreshURCPAGrid(URC_Priority_AreasGrid);
                //refreshURCAGrid(Urc_ActivitiesGrid);
                URC_Priority_AreasGrid.setItems(new URC_Priority_Areas());
                Urc_ActivitiesGrid.setItems(new Urc_Activities());
                nameNdp4.clear();
                nameNdp5.clear();
                nameNdp6.clear();
            } else {
                nbfaSelect = 0;

            }
        });
        save.addClickListener(s -> {
            if (!nameNdp3.isEmpty() || !ntmpGrid.asSingleSelect().isEmpty()) {
                National_Budget_Focus_Areas selected = nbfaGrid.asSingleSelect().getValue();
                if (selected == null) {
                    selected = new National_Budget_Focus_Areas();

                }
                selected.setNationalTransportMasterPlan(ntmpGrid.asSingleSelect().getValue());
                selected.setName(nameNdp3.getValue());
                sampleNational_Budget_Focus_AreasService.update(selected);
                refreshNBFAGrid(nbfaGrid, sampleNational_Transport_Master_Plan);
            } else {
                warningDilogue("Select an objective or else fill the required fields");
            }
            nameNdp3.clear();
        });
        ndp3.add(title, lay, save, nbfaGrid);
        return ndp3;
    }

    private VerticalLayout URC_Strategic_PlanLayout() {
        VerticalLayout ndp3 = new VerticalLayout();
        ndp3.setSpacing(false);
        HorizontalLayout lay = new HorizontalLayout();
        lay.setWidthFull();
        Text title = new Text("URC STRATEGIC PLAN");

        // Apply a style to make the title bold
        //title.getElement().getStyle().set("font-weight", "bold");
        nameNdp4 = new TextArea();
        nameNdp4.setWidthFull();
        //nameNdp4.setMinHeight("100px");
        nameNdp4.setMaxHeight("100px");
        nameNdp4.setMaxLength(950);
        nameNdp4.setValueChangeMode(ValueChangeMode.EAGER);
        nameNdp4.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + 950);
        });
        Button save = new Button("Save");
        lay.add(nameNdp4);
        lay.setAlignItems(Alignment.BASELINE);
        URC_Strategic_PlanGrid = new Grid<>(URC_Strategic_Plan.class, false);

        URC_Strategic_PlanGrid.addColumn(URC_Strategic_Plan::getName).setHeader("URC Strategic Plan");
        URC_Strategic_PlanGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        URC_Strategic_PlanGrid.setHeight("200px");
        URC_Strategic_PlanGrid.setItems(query -> sampleURC_Strategic_PlanService.listByNational_Budget_Focus_Areas(nbfaGrid.asSingleSelect().getValue(),
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        URC_Strategic_PlanGrid.asSingleSelect().addValueChangeListener(eq -> {

            if (eq.getValue() != null) {
                sampleURC_Strategic_Plan = eq.getValue();
                nameNdp4.setValue(eq.getValue().getName());
                //refreshURCAGrid(Urc_ActivitiesGrid);/
                refreshURCPAGrid(URC_Priority_AreasGrid);
                Urc_ActivitiesGrid.setItems(new Urc_Activities());
                nameNdp5.clear();
                nameNdp6.clear();
                //nbfaSelect = 1;
            } else {
                // nbfaSelect = 0;

            }
        });
        save.addClickListener(s -> {
            if (!nameNdp4.isEmpty() || !nbfaGrid.asSingleSelect().isEmpty()) {
                URC_Strategic_Plan selected = URC_Strategic_PlanGrid.asSingleSelect().getValue();
                if (selected == null) {
                    selected = new URC_Strategic_Plan();

                }
                selected.setNationalBudgetFocusArea(nbfaGrid.asSingleSelect().getValue());
                selected.setName(nameNdp4.getValue());
                sampleURC_Strategic_PlanService.update(selected);
                refreshURCSPGrid(URC_Strategic_PlanGrid);
            } else {
                warningDilogue("Select an objective or else fill the required fields");
            }
            nameNdp4.clear();
        });
        ndp3.add(title, lay, save, URC_Strategic_PlanGrid);
        return ndp3;
    }

    private VerticalLayout URC_Priority_AreaLayout() {
        VerticalLayout ndp3 = new VerticalLayout();
        ndp3.setSpacing(false);
        HorizontalLayout lay = new HorizontalLayout();
        lay.setWidthFull();
        Text title = new Text("URC PRIORITY AREAS");

        // Apply a style to make the title bold
        //title.getElement().getStyle().set("font-weight", "bold");
        nameNdp5 = new TextArea();
        nameNdp5.setWidthFull();
        // nameNdp5.setMinHeight("100px");
        nameNdp5.setMaxHeight("100px");
        nameNdp5.setMaxLength(950);
        nameNdp5.setValueChangeMode(ValueChangeMode.EAGER);
        nameNdp5.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + 950);
        });
        Button save = new Button("Save");
        lay.add(nameNdp5);
        lay.setAlignItems(Alignment.BASELINE);
        URC_Priority_AreasGrid = new Grid<>(URC_Priority_Areas.class, false);

        URC_Priority_AreasGrid.addColumn(URC_Priority_Areas::getName).setHeader("URC Priority Areas");
        URC_Priority_AreasGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        URC_Priority_AreasGrid.setHeight("200px");
        URC_Priority_AreasGrid.setItems(query -> sampleURC_Priority_AreasService.findByUrcStrategicPlan(URC_Strategic_PlanGrid.asSingleSelect().getValue(),
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        URC_Priority_AreasGrid.asSingleSelect().addValueChangeListener(eq -> {

            if (eq.getValue() != null) {
                sampleURC_Priority_Areas = eq.getValue();
                nameNdp5.setValue(eq.getValue().getName());
                refreshURCAGrid(Urc_ActivitiesGrid);
                nameNdp6.clear();
                //nbfaSelect = 1;
            } else {
                // nbfaSelect = 0;

            }
        });
        save.addClickListener(s -> {
            if (!nameNdp5.isEmpty() || !URC_Strategic_PlanGrid.asSingleSelect().isEmpty()) {
                URC_Priority_Areas selected = URC_Priority_AreasGrid.asSingleSelect().getValue();
                if (selected == null) {
                    selected = new URC_Priority_Areas();

                }
                selected.setUrcStrategicPlan(URC_Strategic_PlanGrid.asSingleSelect().getValue());
                selected.setName(nameNdp5.getValue());
                sampleURC_Priority_AreasService.update(selected);
                refreshURCPAGrid(URC_Priority_AreasGrid);
            } else {
                warningDilogue("Select an objective or else fill the required fields");
            }
            nameNdp5.clear();
        });
        ndp3.add(title, lay, save, URC_Priority_AreasGrid);
        return ndp3;
    }

    private VerticalLayout Urc_ActivitiesLayout() {
        VerticalLayout ndp3 = new VerticalLayout();
        ndp3.setSpacing(false);
        HorizontalLayout lay = new HorizontalLayout();
        lay.setWidthFull();
        Text title = new Text("URC ACTIVITIES");

        // Apply a style to make the title bold
        //title.getElement().getStyle().set("font-weight", "bold");
        nameNdp6 = new TextArea();
        nameNdp6.setWidthFull();
        //nameNdp6.setMinHeight("100px");
        nameNdp6.setMaxHeight("100px");
        nameNdp6.setMaxLength(950);
        nameNdp6.setValueChangeMode(ValueChangeMode.EAGER);
        nameNdp6.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + 950);
        });
        Button save = new Button("Save");
        lay.add(nameNdp6);
        lay.setAlignItems(Alignment.BASELINE);
        Urc_ActivitiesGrid = new Grid<>(Urc_Activities.class, false);

        Urc_ActivitiesGrid.addColumn(Urc_Activities::getName).setHeader("URC Activities");

        Urc_ActivitiesGrid.addColumn(
                new ComponentRenderer<>(Button::new, (button, urc_Activities) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> {
                        Dialog dia = new Dialog();

                        // dia.setWidth(90, Unit.PERCENTAGE);
                        // dia.setHeight(100, Unit.PERCENTAGE);
                        dia.setSizeUndefined();
                        dia.setHeaderTitle("ACTIVITY DETAILS ");
                        Button closeButton = new Button(new Icon("lumo", "cross"),
                                (ex) -> dia.close());
                        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                        dia.getHeader().add(closeButton);
                        dia.setCloseOnOutsideClick(false);
                        dia.setModal(true);
                        dia.setDraggable(true);
                        dia.setResizable(true);
                        dia.add(ActivityDetails(urc_Activities));
                        // dia.add(new Label(urc_Activities.getName() + " This is the activity"));
                        dia.open();
                        Notification.show("Edit");
                    });

                    button.setIcon(new Icon(VaadinIcon.EDIT));
                })).setFrozenToEnd(true)
                .setAutoWidth(true).setFlexGrow(0).setHeader("Edit");
        Urc_ActivitiesGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        Urc_ActivitiesGrid.setHeight("200px");
        Urc_ActivitiesGrid.setItems(query -> sampleUrc_ActivitiesService.listByUrcPriorityAreas(URC_Priority_AreasGrid.asSingleSelect().getValue(),
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        Urc_ActivitiesGrid.asSingleSelect().addValueChangeListener(eq -> {

            if (eq.getValue() != null) {
                sampleUrc_Activities = eq.getValue();
                nameNdp6.setValue(eq.getValue().getName());
                //refreshURCAGrid(Urc_ActivitiesGrid);
                //nbfaSelect = 1;
            } else {
                //nbfaSelect = 0;

            }
        });
        save.addClickListener(s -> {
            if (!nameNdp6.isEmpty() || !URC_Priority_AreasGrid.asSingleSelect().isEmpty()) {
                Urc_Activities selected = Urc_ActivitiesGrid.asSingleSelect().getValue();
                if (selected == null) {
                    selected = new Urc_Activities();

                }
                selected.setUrcPriorityAreas(URC_Priority_AreasGrid.asSingleSelect().getValue());
                selected.setName(nameNdp6.getValue());
                sampleUrc_ActivitiesService.update(selected);
                refreshURCAGrid(Urc_ActivitiesGrid);
            } else {
                warningDilogue("Select an objective or else fill the required fields");
            }
            nameNdp6.clear();
        });
        ndp3.add(title, lay, save, Urc_ActivitiesGrid);
        return ndp3;
    }

    private void refreshNDP3ObjGrid(Grid<NDPIII_Objective> grid) {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        grid.setItems(query -> sampleNDPIII_ObjectiveService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    private void refreshNTMPGrid(Grid<National_Transport_Master_Plan> grid, NDPIII_Objective entity) {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        grid.setItems(query -> sampleNational_Transport_Master_PlanService.listByNDP3Objective(entity,
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    private void refreshNBFAGrid(Grid<National_Budget_Focus_Areas> grid, National_Transport_Master_Plan sampleNational_Transport_Master_Plan) {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        if (sampleNational_Transport_Master_Plan != null) {

            grid.setItems(query -> sampleNational_Budget_Focus_AreasService.listByNationalTransportMasterPlan(sampleNational_Transport_Master_Plan,
                    PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());

        }
    }

    private void refreshURCSPGrid(Grid<URC_Strategic_Plan> grid) {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        if (sampleNational_Budget_Focus_Areas != null) {

            grid.setItems(query -> sampleURC_Strategic_PlanService.listByNational_Budget_Focus_Areas(sampleNational_Budget_Focus_Areas,
                    PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());

        }
    }

    private void refreshURCPAGrid(Grid<URC_Priority_Areas> grid) {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        if (sampleURC_Strategic_Plan != null) {

            grid.setItems(query -> sampleURC_Priority_AreasService.findByUrcStrategicPlan(sampleURC_Strategic_Plan,
                    PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());

        }
    }

    private void refreshURCAGrid(Grid<Urc_Activities> grid) {
        // grid.select(null);
        grid.getDataProvider().refreshAll();
        if (sampleURC_Priority_Areas != null) {

            grid.setItems(query -> sampleUrc_ActivitiesService.listByUrcPriorityAreas(sampleURC_Priority_Areas,
                    PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());

        }
    }

    private Notification warningDilogue(String warning) {
        Notification notification = new Notification();
        // notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                new Text(warning),
                new HtmlComponent("br"),
                new Text("Close this warning to continue working.")
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
        return notification;
    }

    private FormLayout ActivityDetails(Urc_Activities act) {
        FormLayout div = new FormLayout();
        activity = new TextArea("Activity");
        activity.setEnabled(false);
        fundsource = new TextField("Fund Source");
        output = new TextField("Out Put");
        performanceIndicator = new TextField("Performance Indicator");
        outcome = new TextField("Out Come");
        objective = new TextArea("Objective");
        setActivitiesDeatils(act);
        div.add(activity, fundsource, output, performanceIndicator, outcome, objective);
        div.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2));
// Stretch the username field over 2 columns
        div.setColspan(activity, 2);
        return div;
    }

    private void setActivitiesDeatils(Urc_Activities act) {
        activity.setValue(act.getName());

        if (!fundsource.isEmpty()) {
            fundsource.setValue(act.getFundsource());
        }
        if (!output.isEmpty()) {
            output.setValue(act.getOutput());
        }
        if (!performanceIndicator.isEmpty()) {
            performanceIndicator.setValue(act.getPerformanceIndicator());
        }
        if (!outcome.isEmpty()) {
            outcome.setValue(act.getOutcome());
        }
        if (!objective.isEmpty()) {
            objective.setValue(act.getObjective());
        }

    }

    private void cleartActivitiesDeatils() {
        fundsource.clear();
        output.clear();
        performanceIndicator.clear();
        outcome.clear();
        objective.clear();
    }

    public Notification NotificationError(String error) {
        // When creating a notification using the constructor,
        // the duration is 0-sec by default which means that
        // the notification does not close automatically.
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(error));

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

        notification.setPosition(Notification.Position.MIDDLE);

        return notification;
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

    private void exportAndDownloadExcelSectionCOA() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Setion Chart Of Accounts");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(false);
            createHeaderRow(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Setion Chart Of Accounts.xlsx", ()
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

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        short tr = 0;
        short cl = 0;
        Row rowhead01 = sheet.createRow((short) tr);
        rowhead01.createCell((short) tr).setCellValue("UGANDA RAILWAYS CORPORATION BUDGET BY ACTIVITY");
        tr++;
        Row rowhead0a = sheet.createRow((short) tr);
        Cell cell = rowhead0a.createCell((short) 0);
        cell.setCellValue("Account Code");
        Cell cell2 = rowhead0a.createCell((short) 1);
        cell2.setCellValue("Account Description");
        //List<String> elements = List.of("Projects", "Property Management", "Civil Engineering", "Operations","Marine", "Board Affairs", "Commercial Affairs", "MA","Reporting", "ICT", "STORES", "Procurement","Corporate Planning", "Security", "HR", "MD","Internal audit");
        List<UR5_ACNT> ant = sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();
        for (UR5_ACNT ants : ant) {
            tr++;
            Row rowhead = sheet.createRow((short) tr);
            Cell cela = rowhead.createCell((short) 0);
            cela.setCellValue(ants.getAcntCode());
            Cell cellb = rowhead.createCell((short) 1);
            cellb.setCellValue(ants.getDescr());
        }

    }

    public Coalevel1 findCoalevel1(String code, Budget budget) {
        String income = "Income";
        String opex = "Operation Expenditure";
        String capex = "Capital Expenditure";
        String cats = "";

        List<Coalevel1> findByBudget = sampleCoalevel1Service.findByBudget();
        for (Coalevel1 coal : findByBudget) {
            if (code.startsWith("1") && coal.getName().equalsIgnoreCase(income)) {
                cats = income;
            } else if (code.startsWith("2") && coal.getName().equalsIgnoreCase(opex)) {
                cats = opex;
            } else if (code.startsWith("3") && coal.getName().equalsIgnoreCase(capex)) {
                cats = capex;
            } else {
                cats = "";
            }
        }
        return sampleCoalevel1Service.findByNameAndBudget(cats);
    }

    public String Coalevel1String(Coalevel1 coal) {
        String income = "Income";
        String opex = "Operation Expenditure";
        String capex = "Capital Expenditure";
        String string;
        if (coal.getName().equalsIgnoreCase(income)) {
            string = "1";
        } else if (coal.getName().equalsIgnoreCase(opex)) {
            string = "2";
        } else if (coal.getName().equalsIgnoreCase(capex)) {
            string = "3";
        } else {
            string = "";
        }
        return string;
    }

    private void extractFundsourcesAndActvities() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Activities " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            String[] headers = {
                "ACTIVITY CODE",
                "ACTIVITY DESCRIPTION"
            };
            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Urc_Activities> acts = sampleUrc_ActivitiesService.listByBudget2(sampleBudget);
            for (Urc_Activities a : acts) {
                tr++;
                Row row = sheet.createRow(tr);
                Cell cell0 = row.createCell((short) 0);
                cell0.setCellValue(a.getActivityCode());
                Cell cell1 = row.createCell((short) 1);

                cell1.setCellValue(getFirstCharacters(a.getName()));

            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            tr = 0;
            Sheet sheet2 = workbook.createSheet("Budget Type " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet2.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet2.getPrintSetup().setLandscape(true);
            String[] headers2 = {
                "BUDGET TYPE CODE",
                "BUDGET TYPE DESCRIPTION"
            };

            Row headerRow2 = sheet2.createRow(0);

            for (int i = 0; i < headers2.length; i++) {
                Cell cell = headerRow2.createCell(i);
                cell.setCellValue(headers2[i]);
            }
            List<Organisation> organisations = sampleOrganisationService.findByBudgetList(sampleBudget);
            for (Organisation a : organisations) {
                tr++;
                Row row = sheet2.createRow(tr);
                Cell cell0 = row.createCell((short) 0);
                cell0.setCellValue(a.getCode());
                Cell cell1 = row.createCell((short) 1);

                cell1.setCellValue(getFirstCharacters(a.getName()));

            }
            for (int i = 0; i < headers2.length; i++) {
                sheet2.autoSizeColumn(i);
            }

            tr = 0;
            Sheet sheet3 = workbook.createSheet("Budget Funds Sources " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet3.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet3.getPrintSetup().setLandscape(true);
            String[] headers3 = {
                "BUDGET FUND SOURCE CODE",
                "BUDGET FUND SOURCE DESCRIPTION"
            };

            Row headerRow3 = sheet3.createRow(0);

            for (int i = 0; i < headers3.length; i++) {
                Cell cell = headerRow3.createCell(i);
                cell.setCellValue(headers3[i]);
            }
            List<Fundsource> fundsources = fundsourceService.findFundsourcesByBudget(sampleBudget);
            for (Fundsource a : fundsources) {
                tr++;
                Row row = sheet3.createRow(tr);
                Cell cell0 = row.createCell((short) 0);
                cell0.setCellValue(a.getCode());
                Cell cell1 = row.createCell((short) 1);

                cell1.setCellValue(getFirstCharacters(a.getFundsource()));

            }
            for (int i = 0; i < headers3.length; i++) {
                sheet3.autoSizeColumn(i);
            }
            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Budget Parameters " + sampleBudget.getFinancialYear() + ".xlsx", ()
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

    private void createSunBudgetFile(Workbook workbook, Sheet sheet) {

        String[] headers = {
            "ACCOUNTCODE",
            "DEBIT/ CREDIT",
            "DESCRIPTION",
            "ACCT PERIOD",
            "TRANSACTIONAL REFERENCE",
            "BASE AMOUNT",
            "CURR CODE",
            "DEPARTMENT",
            "SECTION",
            "ACTIVITY CODE",
            "BUDGET TYPE",
            "BUDGET FUND SOURCE"
        };
        int tr = 0;
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
        for (Coalevel1 list : Coalevel1List) {
            List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
            for (BudgetItems k : budgetItems) {
                String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                if (k.getJul().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jul"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jul"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getJul().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getJul().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }

                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }

                }
                if (k.getAug().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Aug"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Aug"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getAug().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getAug().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }

                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getSep().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Sep"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Sep"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getSep().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getSep().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }

                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getOct().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Oct"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Oct"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getOct().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getOct().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getNov().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Nov"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Nov"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getNov().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getNov().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getDec().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Dec"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Dec"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getDec().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getDec().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getJan().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jan"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jan"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getJan().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getJan().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getFeb().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Feb"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Feb"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getFeb().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getFeb().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getMar().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Mar"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Mar"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getMar().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getMar().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getApr().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Apr"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Apr"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getApr().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getApr().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getMay().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "May"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "May"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getMay().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getMay().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getJun().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jun"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jun"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getJun().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getJun().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");

                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createSunBudgetFile2(Workbook workbook, Sheet sheet) {

        String[] headers = {
            "ACCOUNTCODE",
            "DEBIT/ CREDIT",
            "DESCRIPTION",
            "ACCT PERIOD",
            "TRANSACTIONAL REFERENCE",
            "BASE AMOUNT",
            "CURR CODE",
            "DEPARTMENT",
            "SECTION",
            "ACTIVITY CODE",
            "BUDGET TYPE",
            "BUDGET FUND SOURCE"
        };
        int tr = 0;
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
        for (Coalevel1 list : Coalevel1List) {
            List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
            for (BudgetItems k : budgetItems) {
                String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                if (k.getJul().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jul"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jul"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getJul().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getJul().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }

                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }

                }
                if (k.getAug().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Aug"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Aug"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getAug().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getAug().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }

                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getSep().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Sep"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Sep"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getSep().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getSep().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }

                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getOct().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Oct"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Oct"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getOct().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getOct().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getNov().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Nov"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Nov"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getNov().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getNov().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());
                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getDec().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Dec"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Dec"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getDec().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getDec().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getJan().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jan"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jan"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getJan().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getJan().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getFeb().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Feb"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Feb"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getFeb().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getFeb().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getMar().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Mar"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Mar"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getMar().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getMar().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getApr().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Apr"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Apr"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getApr().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getApr().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getMay().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "May"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "May"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getMay().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getMay().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");
                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
                if (k.getJun().doubleValue() > 0) {
                    tr++;
                    Row row = sheet.createRow(tr);
                    Cell cell0 = row.createCell((short) 0);
                    cell0.setCellValue(k.getCoacode().getCode());
                    Cell cell1 = row.createCell((short) 1);
                    if (list.getCode() == 1) {
                        cell1.setCellValue("C");
                    } else if (list.getCode() == 2) {
                        cell1.setCellValue("D");
                    } else if (list.getCode() == 3) {
                        cell1.setCellValue("D");
                    }

                    Cell cell2 = row.createCell((short) 2);
                    cell2.setCellValue(getFirstCharacters(k.getItem()));
                    Cell cell3 = row.createCell((short) 3);
                    cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jun"));
                    Cell cell4 = row.createCell((short) 4);
                    cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jun"));

                    Cell cell5 = row.createCell((short) 5);
                    if (list.getCode() > 1) {
                        cell5.setCellValue(k.getJun().negate().doubleValue());
                    } else {
                        cell5.setCellValue(k.getJun().doubleValue());
                    }

                    Cell cell6 = row.createCell((short) 6);
                    cell6.setCellValue("UGX");

                    Cell cell7 = row.createCell((short) 7);
                    cell7.setCellValue(deptCode);

                    Cell cell8 = row.createCell((short) 8);
                    cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                    Cell cell9 = row.createCell((short) 9);
                    if (k.getActivity() != null) {
                        cell9.setCellValue(k.getActivity().getActivityCode());
                    }

                    Cell cell10 = row.createCell((short) 10);
                    if (k.getBudgetType() != null) {
                        cell10.setCellValue(k.getBudgetType().getId());
                    }
                    Cell cell11 = row.createCell((short) 11);
                    if (k.getFundsource() != null) {
                        cell11.setCellValue(k.getFundsource().getId());
                    }
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void exportAndDownloadSunFile(String fy) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sun File " + fy);
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createSunBudgetFile(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("sun file.xlsx", ()
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

    private void exportAndDownloadSunFile2() {
        String[] headers = {
            "ACCOUNTCODE",
            "DEBIT/ CREDIT",
            "DESCRIPTION",
            "ACCT PERIOD",
            "TRANSACTIONAL REFERENCE",
            "BASE AMOUNT",
            "CURR CODE",
            "DEPARTMENT",
            "SECTION",
            "ACTIVITY CODE",
            "BUDGET TYPE",
            "BUDGET FUND SOURCE"
        };
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("July " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getJul().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jul"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jul"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getJul().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getJul().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jul").replace("/", "") + ".xlsx", ()
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

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("August " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getAug().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Aug"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Aug"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getAug().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getAug().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Aug").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("September " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getSep().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Sep"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Sep"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getSep().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getSep().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Sep").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("October " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getOct().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Oct"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Oct"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getOct().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getOct().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Oct").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("November " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getNov().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Nov"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Nov"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getNov().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getNov().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Nov").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("December " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getDec().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Dec"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Dec"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getDec().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getDec().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Dec").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("January " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getJul().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jan"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jan"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getJan().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getJan().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jan").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("February " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getFeb().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Feb"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Feb"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getFeb().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getFeb().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Feb").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("March " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getMar().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Mar"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Mar"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getMar().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getMar().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Mar").replace("/", "") + ".xlsx", ()
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("April " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getApr().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Apr"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Apr"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getApr().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getApr().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Apr").replace("/", "") + ".xlsx", ()
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
        System.out.println("Tenth");
        try (Workbook workbook = new XSSFWorkbook()) {
            System.out.println("Tenth processes started");
            Sheet sheet = workbook.createSheet("May " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getMay().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "May"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "May"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getMay().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getMay().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "May").replace("/", "") + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
            System.out.println("Tenth processes started");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("June " + sampleBudget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            int tr = 0;
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
            for (Coalevel1 list : Coalevel1List) {
                List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);
                for (BudgetItems k : budgetItems) {
                    String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());
                    if (k.getJun().doubleValue() > 0) {
                        tr++;
                        Row row = sheet.createRow(tr);
                        Cell cell0 = row.createCell((short) 0);
                        cell0.setCellValue(k.getCoacode().getCode());
                        Cell cell1 = row.createCell((short) 1);
                        if (list.getCode() == 1) {
                            cell1.setCellValue("C");
                        } else if (list.getCode() == 2) {
                            cell1.setCellValue("D");
                        } else if (list.getCode() == 3) {
                            cell1.setCellValue("D");
                        }

                        Cell cell2 = row.createCell((short) 2);
                        cell2.setCellValue(getFirstCharacters(k.getItem()));
                        Cell cell3 = row.createCell((short) 3);
                        cell3.setCellValue(generateAccPeriod(sampleBudget.getFinancialYear(), "Jun"));
                        Cell cell4 = row.createCell((short) 4);
                        cell4.setCellValue(generateTReference(sampleBudget.getFinancialYear(), "Jun"));

                        Cell cell5 = row.createCell((short) 5);
                        if (list.getCode() > 1) {
                            cell5.setCellValue(k.getJun().negate().doubleValue());
                        } else {
                            cell5.setCellValue(k.getJun().doubleValue());
                        }

                        Cell cell6 = row.createCell((short) 6);
                        cell6.setCellValue("UGX");
                        Cell cell7 = row.createCell((short) 7);
                        cell7.setCellValue(deptCode);

                        Cell cell8 = row.createCell((short) 8);
                        cell8.setCellValue(k.getDeptUnit().getANL_CODE());

                        Cell cell9 = row.createCell((short) 9);
                        if (k.getActivity() != null) {
                            cell9.setCellValue(k.getActivity().getActivityCode());
                        }

                        Cell cell10 = row.createCell((short) 10);
                        if (k.getBudgetType() != null) {
                            cell10.setCellValue(k.getBudgetType().getId());
                        }

                        Cell cell11 = row.createCell((short) 11);
                        if (k.getFundsource() != null) {
                            cell11.setCellValue(k.getFundsource().getId());
                        }

                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jun").replace("/", "") + ".xlsx", ()
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

    private void exportAndDownloadSunFile4() {
        // Download text file
        StreamResource resourceJul = new StreamResource("100.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul.setContentType("text/plain");
        resourceJul.setCacheTime(0);
        Anchor downloadLinkJul = new Anchor(resourceJul, "");
        downloadLinkJul.getElement().setAttribute("download", true);
        add(downloadLinkJul);
        //downloadLinkJul.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul);

        StreamResource resource2 = new StreamResource("102.txt", () -> new ByteArrayInputStream("2".getBytes(StandardCharsets.UTF_8)));
        resource2.setContentType("text/plain");
        resource2.setCacheTime(0);
        Anchor downloadLinkJul2 = new Anchor(resource2, "");
        downloadLinkJul2.getElement().setAttribute("download", true);
        add(downloadLinkJul2);
        //downloadLinkJul2.getElement().callJsFunction("click");
        downloadQueue.add(resource2);

        StreamResource resourceJul3 = new StreamResource("103.txt", () -> new ByteArrayInputStream("3".getBytes(StandardCharsets.UTF_8)));
        resourceJul3.setContentType("text/plain");
        resourceJul3.setCacheTime(0);
        Anchor downloadLinkJul3 = new Anchor(resourceJul3, "");
        downloadLinkJul3.getElement().setAttribute("download", true);
        add(downloadLinkJul3);
        //downloadLinkJul3.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul3);

        StreamResource resourceJul4 = new StreamResource("104.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul4.setContentType("text/plain");
        resourceJul4.setCacheTime(0);
        Anchor downloadLinkJul4 = new Anchor(resourceJul4, "");
        downloadLinkJul4.getElement().setAttribute("download", true);
        add(downloadLinkJul4);
        //downloadLinkJul4.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul4);

        StreamResource resourceJul5 = new StreamResource("105.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul5.setContentType("text/plain");
        resourceJul5.setCacheTime(0);
        Anchor downloadLinkJul5 = new Anchor(resourceJul5, "");
        downloadLinkJul5.getElement().setAttribute("download", true);
        add(downloadLinkJul5);
        //downloadLinkJul5.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul5);

        StreamResource resourceJul6 = new StreamResource("106.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul6.setContentType("text/plain");
        resourceJul6.setCacheTime(0);
        Anchor downloadLinkJul6 = new Anchor(resourceJul6, "");
        // downloadLinkJul6.getElement().setAttribute("download", true);
        add(downloadLinkJul6);
        downloadLinkJul6.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul6);

        StreamResource resourceJul7 = new StreamResource("107.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul7.setContentType("text/plain");
        resourceJul7.setCacheTime(0);
        Anchor downloadLinkJul7 = new Anchor(resourceJul7, "");
        downloadLinkJul7.getElement().setAttribute("download", true);
        add(downloadLinkJul7);
        //downloadLinkJul7.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul7);

        StreamResource resourceJul8 = new StreamResource("108.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul8.setContentType("text/plain");
        resourceJul8.setCacheTime(0);
        Anchor downloadLinkJul8 = new Anchor(resourceJul8, "");
        downloadLinkJul8.getElement().setAttribute("download", true);
        add(downloadLinkJul8);
        //downloadLinkJul8.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul8);

        StreamResource resourceJul9 = new StreamResource("109.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul9.setContentType("text/plain");
        resourceJul9.setCacheTime(0);
        Anchor downloadLinkJul9 = new Anchor(resourceJul9, "");
        downloadLinkJul9.getElement().setAttribute("download", true);
        add(downloadLinkJul9);
        //downloadLinkJul9.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul9);

        StreamResource resourceJul10 = new StreamResource("110.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul10.setContentType("text/plain");
        resourceJul10.setCacheTime(0);
        Anchor downloadLinkJul10 = new Anchor(resourceJul10, "");
        downloadLinkJul10.getElement().setAttribute("download", true);
        add(downloadLinkJul10);
        //downloadLinkJul10.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul10);

        StreamResource resourceJul11 = new StreamResource("111.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul11.setContentType("text/plain");
        resourceJul11.setCacheTime(0);
        Anchor downloadLinkJul11 = new Anchor(resourceJul11, "");
        downloadLinkJul11.getElement().setAttribute("download", true);
        add(downloadLinkJul11);
        //downloadLinkJul11.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul11);

        StreamResource resourceJul12 = new StreamResource("112.txt", () -> new ByteArrayInputStream("1".getBytes(StandardCharsets.UTF_8)));
        resourceJul12.setContentType("text/plain");
        resourceJul12.setCacheTime(0);
        Anchor downloadLinkJul12 = new Anchor(resourceJul12, "");
        downloadLinkJul12.getElement().setAttribute("download", true);
        add(downloadLinkJul12);
        //downloadLinkJul12.getElement().callJsFunction("click");
        downloadQueue.add(resourceJul12);
    }

    private void exportAndDownloadSunFile3() {
        List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();
        List<String> recordsJul = new ArrayList<>();
        List<String> recordsAug = new ArrayList<>();
        List<String> recordsSep = new ArrayList<>();
        List<String> recordsOct = new ArrayList<>();
        List<String> recordsNov = new ArrayList<>();
        List<String> recordsDec = new ArrayList<>();

        int i = 0;
        for (Coalevel1 list : Coalevel1List) {
            List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);

            for (BudgetItems k : budgetItems) {
                String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());

                String d_c = "";

                if (list.getCode() == 1) {
                    d_c = "C";
                } else if (list.getCode() == 2) {
                    d_c = "D";
                } else if (list.getCode() == 3) {
                    d_c = "D";
                }
                String fundsource = "";
                if (k.getFundsource() != null) {
                    fundsource = k.getFundsource().getCode();
                }

                String activity = "";
                if (k.getActivity() != null) {
                    activity = k.getActivity().getActivityCode();
                }

                String budgetType = "";
                if (k.getBudgetType() != null) {
                    budgetType = k.getBudgetType().getCode();
                }
                BigDecimal jul = BigDecimal.ZERO;
                BigDecimal aug = BigDecimal.ZERO;
                BigDecimal sep = BigDecimal.ZERO;
                BigDecimal oct = BigDecimal.ZERO;
                BigDecimal nov = BigDecimal.ZERO;
                BigDecimal dec = BigDecimal.ZERO;
                BigDecimal jan = BigDecimal.ZERO;
                BigDecimal feb = BigDecimal.ZERO;
                BigDecimal mar = BigDecimal.ZERO;
                BigDecimal apr = BigDecimal.ZERO;
                BigDecimal may = BigDecimal.ZERO;
                BigDecimal jun = BigDecimal.ZERO;

                if (k.getJul().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        jul = k.getJul();
                    } else {
                        jul = k.getJul();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Jul"),
                            "0107" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), jul, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Jul"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsJul.add(record);
                }
                if (k.getAug().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        aug = k.getAug();
                    } else {
                        aug = k.getAug();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Aug"),
                            "0108" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), aug, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Aug"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsAug.add(record);
                }

                if (k.getSep().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        sep = k.getSep();
                    } else {
                        sep = k.getSep();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Sep"),
                            "0109" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), sep, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Sep"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsSep.add(record);
                }

                if (k.getOct().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        oct = k.getOct();
                    } else {
                        oct = k.getOct();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Oct"),
                            "0110" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), oct, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Oct"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsOct.add(record);
                }

                if (k.getNov().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        nov = k.getNov();
                    } else {
                        nov = k.getNov();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Nov"),
                            "0111" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), nov, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Nov"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsNov.add(record);
                }

                if (k.getDec().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        dec = k.getDec();
                    } else {
                        dec = k.getDec();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Dec"),
                            "0112" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), dec, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Dec"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsDec.add(record);
                }

            }
        }
        // Generate fixed-width text
        StringBuilder fixedWidthTextJul = new StringBuilder();

        for (String record : recordsJul) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextJul.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextAug = new StringBuilder();

        for (String record : recordsAug) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextAug.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextSep = new StringBuilder();

        for (String record : recordsSep) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextSep.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextOct = new StringBuilder();

        for (String record : recordsOct) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextOct.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextNov = new StringBuilder();

        for (String record : recordsNov) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextNov.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextDec = new StringBuilder();

        for (String record : recordsDec) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextDec.append(record).append("\n");
            }

        }

        int delayBetweenDownloadsMillis = 1000;
        // Download text file
        StreamResource resourceJul = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jul").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextJul.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceJul.setContentType("text/plain");
        resourceJul.setCacheTime(0);

        Anchor downloadLinkJul = new Anchor(resourceJul, "");
        downloadLinkJul.getElement().setAttribute("download", true);
        add(downloadLinkJul);

// Staggered download requests with a delay between each download
        // Adjust as needed
        //downloadLinkJul.getElement().executeJs("setTimeout(function() { this.click(); }, " + delayBetweenDownloadsMillis + ")");
        //downloadLinkJul.getElement().callJsFunction("click");
        downloadLinkJul.getElement().executeJs(
                "setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")"
        );

        StreamResource resourceAug = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Aug").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextAug.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceAug.setContentType("text/plain");
        resourceAug.setCacheTime(0);

        Anchor downloadLinkAug = new Anchor(resourceAug, "");
        downloadLinkAug.getElement().setAttribute("download", true);
        add(downloadLinkAug);
        // Programmatically click the download link to initiate the download
        downloadLinkAug.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceSep = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Sep").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextSep.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceSep.setContentType("text/plain");
        resourceSep.setCacheTime(0);

        Anchor downloadLinkSep = new Anchor(resourceSep, "");
        downloadLinkSep.getElement().setAttribute("download", true);
        add(downloadLinkSep);
        // Programmatically click the download link to initiate the download
        downloadLinkSep.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");
        // Download text file
        StreamResource resourceOct = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Oct").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextOct.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceOct.setContentType("text/plain");
        resourceOct.setCacheTime(0);

        Anchor downloadLinkOct = new Anchor(resourceOct, "");
        downloadLinkOct.getElement().setAttribute("download", true);
        add(downloadLinkOct);

        downloadLinkOct.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceNov = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Nov").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextNov.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceNov.setContentType("text/plain");
        resourceNov.setCacheTime(0);

        Anchor downloadLinkNov = new Anchor(resourceNov, "");
        downloadLinkNov.getElement().setAttribute("download", true);
        add(downloadLinkNov);

        downloadLinkNov.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceDec = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Dec").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextDec.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceDec.setContentType("text/plain");
        resourceDec.setCacheTime(0);

        Anchor downloadLinkDec = new Anchor(resourceDec, "");
        downloadLinkDec.getElement().setAttribute("download", true);
        add(downloadLinkDec);

        downloadLinkDec.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

    }

    private void exportAndDownloadSunFile3StatisticsJD() {
        List<String> recordsJul = new ArrayList<>();
        List<String> recordsAug = new ArrayList<>();
        List<String> recordsSep = new ArrayList<>();
        List<String> recordsOct = new ArrayList<>();
        List<String> recordsNov = new ArrayList<>();
        List<String> recordsDec = new ArrayList<>();

        int i = 0;

        List<FreightVolumes> budgetItems = sampleFreightVolumesService.getAllFreightVolumesByBudget(sampleBudget);

        for (FreightVolumes k : budgetItems) {
            String deptCode = "D0001";

            String d_c = "D";

            /*                if (list.getCode() == 1) {
                d_c = "C";
                } else if (list.getCode() == 2) {
                d_c = "D";
                } else if (list.getCode() == 3) {
                d_c = "D";
                }*/
            String fundsource = "";

            String activity = "";

            String budgetType = "";
            BigDecimal jul = BigDecimal.ZERO;
            BigDecimal aug = BigDecimal.ZERO;
            BigDecimal sep = BigDecimal.ZERO;
            BigDecimal oct = BigDecimal.ZERO;
            BigDecimal nov = BigDecimal.ZERO;
            BigDecimal dec = BigDecimal.ZERO;
            BigDecimal jan = BigDecimal.ZERO;
            BigDecimal feb = BigDecimal.ZERO;
            BigDecimal mar = BigDecimal.ZERO;
            BigDecimal apr = BigDecimal.ZERO;
            BigDecimal may = BigDecimal.ZERO;
            BigDecimal jun = BigDecimal.ZERO;

            if (k.getJul().doubleValue() > 0) {

                jul = k.getJul();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Jul"),
                        "0107" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), jul, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Jul"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsJul.add(record);
            }
            if (k.getAug().doubleValue() > 0) {

                aug = k.getAug();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Aug"),
                        "0108" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), aug, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Aug"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsAug.add(record);
            }

            if (k.getSep().doubleValue() > 0) {

                sep = k.getSep();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Sep"),
                        "0109" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), sep, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Sep"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsSep.add(record);
            }

            if (k.getOct().doubleValue() > 0) {

                oct = k.getOct();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Oct"),
                        "0110" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), oct, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Oct"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsOct.add(record);
            }

            if (k.getNov().doubleValue() > 0) {

                nov = k.getNov();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Nov"),
                        "0111" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), nov, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Nov"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsNov.add(record);
            }

            if (k.getDec().doubleValue() > 0) {

                dec = k.getDec();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Dec"),
                        "0112" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), dec, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Dec"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsDec.add(record);
            }

        }

        // Generate fixed-width text
        StringBuilder fixedWidthTextJul = new StringBuilder();

        for (String record : recordsJul) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextJul.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextAug = new StringBuilder();

        for (String record : recordsAug) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextAug.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextSep = new StringBuilder();

        for (String record : recordsSep) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextSep.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextOct = new StringBuilder();

        for (String record : recordsOct) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextOct.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextNov = new StringBuilder();

        for (String record : recordsNov) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextNov.append(record).append("\n");
            }

        }

        StringBuilder fixedWidthTextDec = new StringBuilder();

        for (String record : recordsDec) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextDec.append(record).append("\n");
            }

        }

        int delayBetweenDownloadsMillis = 1000;
        // Download text file
        StreamResource resourceJul = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jul").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextJul.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceJul.setContentType("text/plain");
        resourceJul.setCacheTime(0);

        Anchor downloadLinkJul = new Anchor(resourceJul, "");
        downloadLinkJul.getElement().setAttribute("download", true);
        add(downloadLinkJul);

        downloadLinkJul.getElement().executeJs(
                "setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")"
        );

        StreamResource resourceAug = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Aug").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextAug.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceAug.setContentType("text/plain");
        resourceAug.setCacheTime(0);

        Anchor downloadLinkAug = new Anchor(resourceAug, "");
        downloadLinkAug.getElement().setAttribute("download", true);
        add(downloadLinkAug);
        // Programmatically click the download link to initiate the download
        downloadLinkAug.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceSep = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Sep").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextSep.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceSep.setContentType("text/plain");
        resourceSep.setCacheTime(0);

        Anchor downloadLinkSep = new Anchor(resourceSep, "");
        downloadLinkSep.getElement().setAttribute("download", true);
        add(downloadLinkSep);
        // Programmatically click the download link to initiate the download
        downloadLinkSep.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");
        // Download text file
        StreamResource resourceOct = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Oct").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextOct.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceOct.setContentType("text/plain");
        resourceOct.setCacheTime(0);

        Anchor downloadLinkOct = new Anchor(resourceOct, "");
        downloadLinkOct.getElement().setAttribute("download", true);
        add(downloadLinkOct);

        downloadLinkOct.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceNov = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Nov").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextNov.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceNov.setContentType("text/plain");
        resourceNov.setCacheTime(0);

        Anchor downloadLinkNov = new Anchor(resourceNov, "");
        downloadLinkNov.getElement().setAttribute("download", true);
        add(downloadLinkNov);

        downloadLinkNov.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceDec = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Dec").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextDec.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceDec.setContentType("text/plain");
        resourceDec.setCacheTime(0);

        Anchor downloadLinkDec = new Anchor(resourceDec, "");
        downloadLinkDec.getElement().setAttribute("download", true);
        add(downloadLinkDec);

        downloadLinkDec.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

    }

    private void exportAndDownloadSunFile5() {
        List<Coalevel1> Coalevel1List = sampleCoalevel1Service.findByBudget();

        List<String> recordsJan = new ArrayList<>();
        List<String> recordsFeb = new ArrayList<>();
        List<String> recordsMar = new ArrayList<>();
        List<String> recordsApr = new ArrayList<>();
        List<String> recordsMay = new ArrayList<>();
        List<String> recordsJun = new ArrayList<>();
        int i = 0;
        for (Coalevel1 list : Coalevel1List) {
            List<BudgetItems> budgetItems = budgetItemsService.findByBudgetAndCoalevel1(sampleBudget, list);

            for (BudgetItems k : budgetItems) {
                String deptCode = sampleDeptSectionMergerService.getDeptCode(k.getDeptUnit().getANL_CODE());

                String d_c = "";

                if (list.getCode() == 1) {
                    d_c = "C";
                } else if (list.getCode() == 2) {
                    d_c = "D";
                } else if (list.getCode() == 3) {
                    d_c = "D";
                }
                String fundsource = "";
                if (k.getFundsource() != null) {
                    fundsource = k.getFundsource().getCode();
                }

                String activity = "";
                if (k.getActivity() != null) {
                    activity = k.getActivity().getActivityCode();
                }

                String budgetType = "";
                if (k.getBudgetType() != null) {
                    budgetType = k.getBudgetType().getCode();
                }
                BigDecimal jul = BigDecimal.ZERO;
                BigDecimal aug = BigDecimal.ZERO;
                BigDecimal sep = BigDecimal.ZERO;
                BigDecimal oct = BigDecimal.ZERO;
                BigDecimal nov = BigDecimal.ZERO;
                BigDecimal dec = BigDecimal.ZERO;
                BigDecimal jan = BigDecimal.ZERO;
                BigDecimal feb = BigDecimal.ZERO;
                BigDecimal mar = BigDecimal.ZERO;
                BigDecimal apr = BigDecimal.ZERO;
                BigDecimal may = BigDecimal.ZERO;
                BigDecimal jun = BigDecimal.ZERO;

                if (k.getJan().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        jan = k.getJan();
                    } else {
                        jan = k.getJan();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Jan"),
                            "0101" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), jan, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Jan"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsJan.add(record);
                }

                if (k.getFeb().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        feb = k.getFeb();
                    } else {
                        feb = k.getFeb();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Feb"),
                            "0102" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), feb, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Feb"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsFeb.add(record);
                }
                if (k.getMar().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        mar = k.getMar();
                    } else {
                        mar = k.getMar();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Mar"),
                            "0103" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), mar, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Mar"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsMar.add(record);
                }

                if (k.getApr().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        apr = k.getApr();
                    } else {
                        apr = k.getApr();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Apr"),
                            "0104" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), apr, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Apr"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsApr.add(record);
                }

                if (k.getMay().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        may = k.getMay();
                    } else {
                        may = k.getMay();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "May"),
                            "0105" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), may, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "May"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsMay.add(record);
                }

                if (k.getJun().doubleValue() > 0) {

                    if (list.getCode() > 1) {
                        jun = k.getJun();
                    } else {
                        jun = k.getJun();
                    }
                    String record = generateFixedWidthText(k.getCoacode().getCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Jun"),
                            "0106" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), jun, d_c, "BUD01",
                            generateTReference(sampleBudget.getFinancialYear(), "Jun"),
                            getFirstCharacters(k.getItem()), "UGX", deptCode, k.getDeptUnit().getANL_CODE(),
                            "", "", "", "",
                            budgetType, fundsource, activity, "");
                    recordsJun.add(record);
                }
            }
        }

        StringBuilder fixedWidthTextJan = new StringBuilder();

        for (String record : recordsJan) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextJan.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextFeb = new StringBuilder();

        for (String record : recordsFeb) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextFeb.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextMar = new StringBuilder();

        for (String record : recordsMar) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextMar.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextApr = new StringBuilder();

        for (String record : recordsApr) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextApr.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextMay = new StringBuilder();

        for (String record : recordsMay) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextMay.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextJun = new StringBuilder();

        for (String record : recordsJun) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextJun.append(record).append("\n");
            }

        }

        int delayBetweenDownloadsMillis = 1000;

        StreamResource resourceJan = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jan").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextJan.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceJan.setContentType("text/plain");
        resourceJan.setCacheTime(0);

        Anchor downloadLinkJan = new Anchor(resourceJan, "");
        downloadLinkJan.getElement().setAttribute("download", true);
        add(downloadLinkJan);

        downloadLinkJan.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceFeb = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Feb").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextFeb.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceFeb.setContentType("text/plain");
        resourceFeb.setCacheTime(0);

        Anchor downloadLinkFeb = new Anchor(resourceFeb, "");
        downloadLinkFeb.getElement().setAttribute("download", true);
        add(downloadLinkFeb);

        downloadLinkFeb.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceMar = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Mar").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextMar.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceMar.setContentType("text/plain");
        resourceMar.setCacheTime(0);

        Anchor downloadLinkMar = new Anchor(resourceMar, "");
        downloadLinkMar.getElement().setAttribute("download", true);
        add(downloadLinkMar);

        downloadLinkMar.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceApr = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Apr").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextApr.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceApr.setContentType("text/plain");
        resourceApr.setCacheTime(0);

        Anchor downloadLinkApr = new Anchor(resourceApr, "");
        downloadLinkApr.getElement().setAttribute("download", true);
        add(downloadLinkApr);

        downloadLinkApr.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceMay = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "May").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextMay.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceMay.setContentType("text/plain");
        resourceMay.setCacheTime(0);

        Anchor downloadLinkMay = new Anchor(resourceMay, "");
        downloadLinkMay.getElement().setAttribute("download", true);
        add(downloadLinkMay);

        downloadLinkMay.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceJun = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jun").replace("/", "") + ".txt",
                () -> new ByteArrayInputStream(fixedWidthTextJun.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceJun.setContentType("text/plain");
        resourceJun.setCacheTime(0);

        Anchor downloadLinkJun = new Anchor(resourceJun, "");
        downloadLinkJun.getElement().setAttribute("download", true);
        add(downloadLinkJun);
        downloadLinkJun.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

    }

    private void exportAndDownloadSunFile5StatisticsJJ() {

        List<String> recordsJan = new ArrayList<>();
        List<String> recordsFeb = new ArrayList<>();
        List<String> recordsMar = new ArrayList<>();
        List<String> recordsApr = new ArrayList<>();
        List<String> recordsMay = new ArrayList<>();
        List<String> recordsJun = new ArrayList<>();
        int i = 0;

        List<FreightVolumes> budgetItems = sampleFreightVolumesService.getAllFreightVolumesByBudget(sampleBudget);

        for (FreightVolumes k : budgetItems) {
            String deptCode = "D0001";

            String d_c = "D";
            String fundsource = "";

            String activity = "";

            String budgetType = "";
            BigDecimal jul = BigDecimal.ZERO;
            BigDecimal aug = BigDecimal.ZERO;
            BigDecimal sep = BigDecimal.ZERO;
            BigDecimal oct = BigDecimal.ZERO;
            BigDecimal nov = BigDecimal.ZERO;
            BigDecimal dec = BigDecimal.ZERO;
            BigDecimal jan = BigDecimal.ZERO;
            BigDecimal feb = BigDecimal.ZERO;
            BigDecimal mar = BigDecimal.ZERO;
            BigDecimal apr = BigDecimal.ZERO;
            BigDecimal may = BigDecimal.ZERO;
            BigDecimal jun = BigDecimal.ZERO;

            if (k.getJan().doubleValue() > 0) {

                jan = k.getJan();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Jan"),
                        "0101" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), jan, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Jan"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsJan.add(record);
            }

            if (k.getFeb().doubleValue() > 0) {

                feb = k.getFeb();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Feb"),
                        "0102" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), feb, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Feb"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsFeb.add(record);
            }
            if (k.getMar().doubleValue() > 0) {

                mar = k.getMar();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Mar"),
                        "0103" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), mar, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Mar"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsMar.add(record);
            }

            if (k.getApr().doubleValue() > 0) {

                apr = k.getApr();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Apr"),
                        "0104" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), apr, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Apr"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsApr.add(record);
            }

            if (k.getMay().doubleValue() > 0) {

                may = k.getMay();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "May"),
                        "0105" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), may, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "May"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsMay.add(record);
            }

            if (k.getJun().doubleValue() > 0) {

                jun = k.getJun();
                String record = generateFixedWidthText(k.getCoacode().getStatCode(), generateAccPeriod2(sampleBudget.getFinancialYear(), "Jun"),
                        "0106" + periodExtractor.extYears(sampleBudget.getFinancialYear()).get(0), jun, d_c, "BUD01",
                        generateTReference(sampleBudget.getFinancialYear(), "Jun"),
                        getFirstCharacters(k.getName()), "UGX", deptCode, "S020",
                        "", "", "", "",
                        budgetType, fundsource, activity, "");
                recordsJun.add(record);
            }
        }

        StringBuilder fixedWidthTextJan = new StringBuilder();

        for (String record : recordsJan) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextJan.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextFeb = new StringBuilder();

        for (String record : recordsFeb) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextFeb.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextMar = new StringBuilder();

        for (String record : recordsMar) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextMar.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextApr = new StringBuilder();

        for (String record : recordsApr) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextApr.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextMay = new StringBuilder();

        for (String record : recordsMay) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextMay.append(record).append("\n");
            }

        }
        StringBuilder fixedWidthTextJun = new StringBuilder();

        for (String record : recordsJun) {
            i++;
            if (!record.isBlank()) {
                fixedWidthTextJun.append(record).append("\n");
            }

        }

        int delayBetweenDownloadsMillis = 1000;

        StreamResource resourceJan = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jan").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextJan.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceJan.setContentType("text/plain");
        resourceJan.setCacheTime(0);

        Anchor downloadLinkJan = new Anchor(resourceJan, "");
        downloadLinkJan.getElement().setAttribute("download", true);
        add(downloadLinkJan);

        downloadLinkJan.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceFeb = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Feb").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextFeb.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceFeb.setContentType("text/plain");
        resourceFeb.setCacheTime(0);

        Anchor downloadLinkFeb = new Anchor(resourceFeb, "");
        downloadLinkFeb.getElement().setAttribute("download", true);
        add(downloadLinkFeb);

        downloadLinkFeb.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceMar = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Mar").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextMar.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceMar.setContentType("text/plain");
        resourceMar.setCacheTime(0);

        Anchor downloadLinkMar = new Anchor(resourceMar, "");
        downloadLinkMar.getElement().setAttribute("download", true);
        add(downloadLinkMar);

        downloadLinkMar.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceApr = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Apr").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextApr.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceApr.setContentType("text/plain");
        resourceApr.setCacheTime(0);

        Anchor downloadLinkApr = new Anchor(resourceApr, "");
        downloadLinkApr.getElement().setAttribute("download", true);
        add(downloadLinkApr);

        downloadLinkApr.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceMay = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "May").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextMay.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceMay.setContentType("text/plain");
        resourceMay.setCacheTime(0);

        Anchor downloadLinkMay = new Anchor(resourceMay, "");
        downloadLinkMay.getElement().setAttribute("download", true);
        add(downloadLinkMay);

        downloadLinkMay.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

        StreamResource resourceJun = new StreamResource(generateAccPeriod(sampleBudget.getFinancialYear(), "Jun").replace("/", "") + " Stat.txt",
                () -> new ByteArrayInputStream(fixedWidthTextJun.toString().replace("\n", System.lineSeparator()).getBytes(StandardCharsets.UTF_8)));
        resourceJun.setContentType("text/plain");
        resourceJun.setCacheTime(0);

        Anchor downloadLinkJun = new Anchor(resourceJun, "");
        downloadLinkJun.getElement().setAttribute("download", true);
        add(downloadLinkJun);
        downloadLinkJun.getElement().executeJs("setTimeout(() => { this.click(); }, " + delayBetweenDownloadsMillis + ")");

    }

    private String generateFixedWidthText(String accCode, String period, String date,
            BigDecimal amount, String d_c, String jnrl_type,
            String trans_ref, String desc, String curr, String anal_t1, String anal_t2,
            String anal_t3, String anal_t4, String anal_t5, String anal_t6,
            String anal_t7, String anal_t8, String anal_t9, String anal_t10) {
        // Define start positions and lengths

        if (anal_t7 == null || anal_t7.isBlank() || anal_t7.isEmpty()) {
            anal_t7 = "ZBT00";
        }
        if (anal_t8 == null || anal_t8.isBlank() || anal_t8.isEmpty()) {
            anal_t8 = "ZBFS00";
        }
        if (anal_t9 == null || anal_t9.isBlank() || anal_t9.isEmpty()) {
            anal_t9 = "ZBAS0200000000";
        }
        int accCodeLength = 15;

        int periodLength = 7;

        int dateLength = 8;
        int amountLength = 18;
        //BigDecimal amount=BigDecimal.ZERO;
        //String amountS = amount.multiply(new BigDecimal("1000")).stripTrailingZeros().toPlainString();
        String amountS = amount.multiply(new BigDecimal("1000")).setScale(0, RoundingMode.DOWN).toPlainString();
        int d_cLength = 1;
        int jnrl_typeLength = 5;
        int trans_refLength = 30;
        int descLength = 50;
        int currLength = 3;
        int analLength = 15;

        // Format data into fixed-width format
        StringBuilder fixedWidthTextBuilder = new StringBuilder();
        fixedWidthTextBuilder.append(String.format("%-" + accCodeLength + "s", accCode).substring(0, accCodeLength));
        fixedWidthTextBuilder.append(String.format("%-" + periodLength + "s", period).substring(0, periodLength));
        fixedWidthTextBuilder.append(String.format("%-" + dateLength + "s", date).substring(0, dateLength));
        fixedWidthTextBuilder.append(String.format("%-" + amountLength + "s", amountS).substring(0, amountLength));
        fixedWidthTextBuilder.append(String.format("%-" + d_cLength + "s", d_c).substring(0, d_cLength));
        fixedWidthTextBuilder.append(String.format("%-" + jnrl_typeLength + "s", jnrl_type).substring(0, jnrl_typeLength));
        fixedWidthTextBuilder.append(String.format("%-" + trans_refLength + "s", trans_ref).substring(0, trans_refLength));
        fixedWidthTextBuilder.append(String.format("%-" + descLength + "s", desc.trim()).substring(0, descLength));
        fixedWidthTextBuilder.append(String.format("%-" + currLength + "s", curr).substring(0, currLength));
        fixedWidthTextBuilder.append(String.format("%-" + amountLength + "s", amountS).substring(0, amountLength));

        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t1).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t2).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t3).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t4).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t5).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t6).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t7).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t8).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t9).substring(0, analLength));
        fixedWidthTextBuilder.append(String.format("%-" + analLength + "s", anal_t10).substring(0, analLength));
        if (accCode.startsWith("1")) {
            return fixedWidthTextBuilder.toString().trim() + " ";
        } else {
            return fixedWidthTextBuilder.toString().trim() + " ";
        }

    }

    public String getFirstCharacters(String text) {
        int maxLength = 50;
        // Return null if the input text is null or empty
        if (text == null || text.isEmpty()) {
            return null;
        }

        // Return the text itself if its length is less than or equal to the maxLength
        if (text.length() <= maxLength) {
            return text;
        }

        // Otherwise, return the substring containing the first maxLength characters
        return text.substring(0, maxLength);
    }

    private String generateAccPeriod(String yearS, String month) {
        strings(yearS); // Extract years from the input string
        int year = getYears().get(1); // Get the first year extracted

        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);

        int monthValue = monthMap.get(month);
        if (monthValue >= 1 && monthValue <= 6) {
            return year + "/" + String.format("%03d", monthValue); // Format for "Jul" to "Jun"
        } else if (monthValue >= 7 && monthValue <= 12) {
            return year + "/" + String.format("%03d", monthValue); // Format for other months
        } else {
            return ""; // Return empty string for invalid input
        }
    }

    private String generateAccPeriod2(String yearS, String month) {
        strings(yearS); // Extract years from the input string
        int year = getYears().get(1); // Get the first year extracted

        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);

        int monthValue = monthMap.get(month);
        if (monthValue >= 1 && monthValue <= 6) {
            return String.format("%03d", monthValue) + year; // Format for "Jul" to "Jun"
        } else if (monthValue >= 7 && monthValue <= 12) {
            return String.format("%03d", monthValue) + year; // Format for other months
        } else {
            return ""; // Return empty string for invalid input
        }
    }

    private static String generateTReference(String yearS, String month) {
        strings(yearS); // Extract years from the input string
        int year = getYears().get(0); // Get the first year extracted

        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);

        int monthValue = monthMap.get(month);
        if (monthValue >= 1 && monthValue <= 6) {
            return "Budget Upload " + month + "-" + Integer.toString(year).substring(2); // Format for "Jul" to "Jun"
        } else if (monthValue >= 7 && monthValue <= 12) {
            year = getYears().get(1); // Get the second year extracted
            return "Budget Upload " + month + "-" + Integer.toString(year).substring(2); // Format for other months
        } else {
            return ""; // Return empty string for invalid input
        }
    }

    private String generateBudgetTypeCode() {
        // Assuming you have a way to retrieve the last code used in your database
        // For demonstration purposes, let's assume it's stored in a variable called lastCode
        // You can replace it with your actual logic to fetch the last used code
        String lastCode = ""; // Replace this with actaul logic to retrieve last used code

        //Organisation org = sampleOrganisationService.getLastSavedOrganisationByBudget(sampleBudget);
        Organisation org = sampleOrganisationService.getLastSavedOrganisationByBudget();
        System.out.println(org.getCode());
        // If lastCode is null or empty, start with ZBT01
        if (org == null) {
            return "ZBT01";
        }
        lastCode = org.getCode();
        // Extract the numeric part of the last code
        String numericPart = lastCode.substring(3); // Assuming "ZBT" is always the prefix
        int index = Integer.parseInt(numericPart);

        // Increment index and format it with leading zeros
        index++;
        String newIndex = String.format("%02d", index);

        // Construct the new code
        return "ZBT" + newIndex;
    }

    private String generateFundSourceCode() {
        // Assuming you have a way to retrieve the last code used in your database
        // For demonstration purposes, let's assume it's stored in a variable called lastCode
        // You can replace it with your actual logic to fetch the last used code
        String lastCode = ""; // Replace this with actaul logic to retrieve last used code

        //Fundsource org = fundsourceService.getLastSavedFundsourceByBudget(sampleBudget);
        Fundsource org = fundsourceService.getLastSavedFundsourceByBudget();
        System.out.println(org.getCode());
        // If lastCode is null or empty, start with ZBT01
        if (org == null) {
            return "ZBFS01";
        }
        lastCode = org.getCode();
        // Extract the numeric part of the last code
        String numericPart = lastCode.substring(4); // Assuming "ZBT" is always the prefix
        int index = Integer.parseInt(numericPart);

        // Increment index and format it with leading zeros
        index++;
        String newIndex = String.format("%02d", index);

        // Construct the new code
        return "ZBFS" + newIndex;
    }
    // Function to process the download queue

    private void processDownloadQueue() {
        if (!downloadQueue.isEmpty()) {
            StreamResource nextResource = downloadQueue.remove(0);
            Anchor downloadLink = new Anchor(nextResource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            downloadLink.getElement().callJsFunction("click");

            // Schedule the next download if there are more resources in the queue
            if (!downloadQueue.isEmpty()) {
                int delayBetweenDownloadsMillis = 1000; // Example delay of 1 second (1000 milliseconds)
                executeDelayedDownload(delayBetweenDownloadsMillis);
            }
        }
    }

// Function to execute the next download after a delay
    private void executeDelayedDownload(int delayMillis) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
            @Override
            public void run() {
                processDownloadQueue(); // Call the function to process the next download
            }
        },
                delayMillis
        );
    }

    // Call this method to start processing the download queue
    private void startDownloadProcess() {
        processDownloadQueue();
    }

}
