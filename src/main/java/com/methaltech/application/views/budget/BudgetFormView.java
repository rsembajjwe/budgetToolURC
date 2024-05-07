package com.methaltech.application.views.budget;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.COAReconcileService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CurrencyDataService;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.FundsourceService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.ProcurementPlanService;
import com.methaltech.application.data.bgtool.service.SectionService;
import com.methaltech.application.data.bgtool.service.StaffSalaryService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.UnitService;
import com.methaltech.application.data.bgtool.service.UnitsBudgetService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.entity.bgtool.*;
import com.methaltech.application.data.entity.livedata.UR5_ACNT;
import com.methaltech.application.data.entity.oldbgtool.OldBudget;
import com.methaltech.application.data.entity.oldbgtool.Staff;
import com.methaltech.application.data.errorMessages;
import com.methaltech.application.data.livedata.service.UR5_ACNTService;
import com.methaltech.application.data.oldbgtool.service.StaffService;
import com.methaltech.application.data.salaryScale;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
//import com.methaltech.application.data;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.io.InputStream;
import java.math.MathContext;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@PageTitle("Budget-Form-View")
@Route(value = "budget-form", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
//@RolesAllowed("USER")
@Uses(Icon.class)
//@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO", "USER"})

public class BudgetFormView extends Div {

    private Budget chosenBudget;
    private UnitsBudget sampleUnitsBudget;
    private UrcDeptSectionAnlDimbgt chosenDsection;
    private COA chosenCOA;
    private List<D_Unit> userUnits = new ArrayList<>();
    private ComboBox<Budget> comboBoxBudget;
    private ComboBox<Organisation> comboBoxOrganisation;
    private ComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section;
    private ComboBox<Coalevel1> comboBoxCoalevel1;
    private ComboBox<Coalevel1> comboBoxCoalevel1Two;
    private Coalevel1 chosenCoalevel1;
    private ComboBox<URC_Priority_Areas> comboBoxUrc_Priority_Areas;
    private Grid<Urc_Activities> gridUrc_Activities = new Grid<>(Urc_Activities.class, false);
    private Grid<Urc_Activities> gridUrc_ActivityDeatail = new Grid<>(Urc_Activities.class, false);
    private final BudgetService chosenBudgetService;
    private final BudgetItemsService budgetItemsService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    private final UserService userService;
    private final UnitService unitService;
    private final UnitsBudgetService unitsbudgetService;
    private final OrganisationService organisationService;
    private final CoaService coaService;
    private final Coalevel1Service coalevel1Service;
    private final SectionService sampleSectionService;
    private final CurrencyService sampleCurrencyService;
    private final CurrencyDataService sampleCurrencyDataService;
    private final StockUnitMeasureService sampleStockUnitMeasureService;
    private final UR5_ACNTService sampleUR5_ACNTService;
    private final FundsourceService sampleFundsourceService;
    private final ProcurementPlanService sampleProcurementPlanService;

    private TextField searchCoa;

    private Grid<COA> gridCOA = new Grid<>(COA.class, false);
    private TextField Item = new TextField("Item");
    private BigDecimalField cost = new BigDecimalField("Rate");
    private BigDecimalField qty = new BigDecimalField("Qty");
    private ComboBox<Currency> currency = new ComboBox<>("Currency");
    private ComboBox<Fundsource> budgetItemfundSource = new ComboBox<>("Fund Source");
    private ComboBox<String> unitMeasure = new ComboBox<>("Unit Measure");
    private BigDecimalField total = new BigDecimalField("Total");

    private BigDecimalField jan = new BigDecimalField("Jan Amount");
    private BigDecimalField feb = new BigDecimalField("Feb Amount");
    private BigDecimalField mar = new BigDecimalField("Mar Amount");
    private BigDecimalField apr = new BigDecimalField("Apr Amount");
    private BigDecimalField may = new BigDecimalField("May Amount");
    private BigDecimalField jun = new BigDecimalField("Jun Amount");
    private BigDecimalField jul = new BigDecimalField("Jul Amount");
    private BigDecimalField aug = new BigDecimalField("Aug Amount");
    private BigDecimalField sep = new BigDecimalField("Sep Amount");
    private BigDecimalField oct = new BigDecimalField("Oct Amount");
    private BigDecimalField nov = new BigDecimalField("Nov Amount");
    private BigDecimalField dec = new BigDecimalField("Dec Amount");
    private TextArea notes = new TextArea("Notes");

    private Button saveBudgetItem;
    private Button deleteBudgetItem;
    private Button distrWorkplan;
    private Button quarterWorkplan;
    private Button clearWorkplan;
    private Button templateDownload;
    private Upload uploadBudget;
    Button staff = new Button("Import staff");
    Button rectify = new Button("Rectify");

    private TextArea name = new TextArea("Urc Activity");
    private TextArea fundsource = new TextArea("Fund Source");
    private TextArea performanceIndicator = new TextArea("Performance Indicator");
    private TextArea outcome = new TextArea("Outcome");
    private TextArea objective = new TextArea("Objective");

    private Grid<BudgetItems> gridBudgetItems = new Grid<>(BudgetItems.class, false);
    private Grid<BudgetItems> gridBudgetCoa = new Grid<>(BudgetItems.class, false);

    private BeanValidationBinder<Urc_Activities> binder = new BeanValidationBinder<>(Urc_Activities.class);
    private BeanValidationBinder<BudgetItems> binderbudgetItem = new BeanValidationBinder<>(BudgetItems.class);
    private User user;

    GridListDataView<Urc_Activities> dataView = null;
    GridListDataView<Urc_Activities> dataViewUrc_ActivityDeatail = null;

    private AuthenticatedUser authenticatedUser;
    private Span fy;
    private Span fyType;
    private Span deptUnit;
    private Span accCode;
    private Span activity;
    private Urc_Activities chosenUrc_Activities;
    private Organisation chosenOrganisation;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    private BigDecimal calculateSumOfAllMonthstotal = BigDecimal.ZERO;
    private Span calculateSumOfAllMonthstotalText = new Span();
    private Span calculateSumOfAllMonthstotalText2 = new Span();
    private Span sectionBudgetText = new Span();
    private Span status;
    String viewSetting = "";
    DecimalFormat decimalFormat2 = new DecimalFormat("#");
    private UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService;
    private final COAReconcileService coaReconcileService;
    private final StaffService staffService;
    private final StaffSalaryService staffSalaryService;
    private ComboBox<ProcClass> procClassCombo = new ComboBox("Procurement Class");

    public BudgetFormView(AuthenticatedUser authenticatedUser, BudgetService chosenBudgetService, Urc_ActivitiesService sampleUrc_ActivitiesService,
            UserService userService, UnitService unitService, UnitsBudgetService unitsbudgetService,
            OrganisationService organisationService, CoaService coaService, Coalevel1Service coalevel1Service,
            URC_Priority_AreasService sampleURC_Priority_AreasService, SectionService sampleSectionService,
            CurrencyService sampleCurrencyService, BudgetItemsService budgetItemsService, StockUnitMeasureService sampleStockUnitMeasureService,
            UR5_ACNTService sampleUR5_ACNTService, UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService,
            CurrencyDataService sampleCurrencyDataService, COAReconcileService coaReconcileService, StaffService staffService,
            StaffSalaryService staffSalaryService, FundsourceService sampleFundsourceService, ProcurementPlanService sampleProcurementPlanService) {
        this.chosenBudgetService = chosenBudgetService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.userService = userService;
        this.unitService = unitService;
        this.unitsbudgetService = unitsbudgetService;
        this.organisationService = organisationService;
        this.coaService = coaService;
        this.coalevel1Service = coalevel1Service;
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
        this.sampleSectionService = sampleSectionService;
        this.sampleCurrencyService = sampleCurrencyService;
        this.budgetItemsService = budgetItemsService;
        this.sampleStockUnitMeasureService = sampleStockUnitMeasureService;
        this.authenticatedUser = authenticatedUser;
        this.sampleUR5_ACNTService = sampleUR5_ACNTService;
        this.urcDeptSectionAnlDimbgtService = urcDeptSectionAnlDimbgtService;
        this.sampleCurrencyDataService = sampleCurrencyDataService;
        this.coaReconcileService = coaReconcileService;
        this.staffService = staffService;
        this.staffSalaryService = staffSalaryService;
        this.sampleFundsourceService = sampleFundsourceService;
        this.sampleProcurementPlanService = sampleProcurementPlanService;
        this.setHeight("100%");
        gridUrc_Activities.setHeight("100%");

        addClassName("budget-form-view");
        decimalFormat.setGroupingUsed(true);
        procClassCombo.setItems(ProcClass.Supplies, ProcClass.Works, ProcClass.Consultancy, ProcClass.Non_Consultancy);

        //add(settingView(), new Hr(), masterSplitPane());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        saveBudgetItem = new Button("Save", new Icon(VaadinIcon.PLUS));
        saveBudgetItem.addThemeVariants(ButtonVariant.LUMO_ICON);
        saveBudgetItem.setAriaLabel("Save");
        saveBudgetItem.setTooltipText("Save or Update");

        deleteBudgetItem = new Button("Delete", new Icon(VaadinIcon.TRASH));
        deleteBudgetItem.addThemeVariants(ButtonVariant.LUMO_ICON);
        deleteBudgetItem.setAriaLabel("Delete");
        deleteBudgetItem.setTooltipText("Delete");

        distrWorkplan = new Button("Evenly Distribute", new Icon(VaadinIcon.SPLIT));
        distrWorkplan.addThemeVariants(ButtonVariant.LUMO_ICON);
        distrWorkplan.setAriaLabel("Evenly Distribute");
        distrWorkplan.setTooltipText("Evenly Distribute");

        quarterWorkplan = new Button("Quarterly", new Icon(VaadinIcon.SPLIT_H));
        quarterWorkplan.addThemeVariants(ButtonVariant.LUMO_ICON);
        quarterWorkplan.setAriaLabel("Quarterly");
        quarterWorkplan.setTooltipText("Quarterly");

        clearWorkplan = new Button("Clear", new Icon(VaadinIcon.STAR));
        clearWorkplan.addThemeVariants(ButtonVariant.LUMO_ICON);
        clearWorkplan.setAriaLabel("Save");
        clearWorkplan.setTooltipText("Save or Update");

        templateDownload = new Button("Download Budget Template", new Icon(VaadinIcon.DOWNLOAD));
        templateDownload.addThemeVariants(ButtonVariant.LUMO_ICON);
        templateDownload.setAriaLabel("Download Budget Template");

        MultiFileMemoryBuffer buffer2 = new MultiFileMemoryBuffer();
        uploadBudget = new Upload(buffer2);
        Button uploadButtonButton = new Button("Upload Budget...");
        uploadButtonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        rectify.addClickListener(e -> {
            fixCOA();
        });
        uploadBudget.setUploadButton(uploadButtonButton);
        uploadBudget.addSucceededListener(event -> {
            List<errorMessages> mes = new ArrayList<>();
            errorMessages m = new errorMessages();
            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty()) {
                String fileName = event.getFileName();
                InputStream inputStream = buffer2.getInputStream(fileName);

                try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                    Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
                    int i = 0;
                    StringBuilder error = new StringBuilder();
                    UrcDeptSectionAnlDimbgt deptSection = null;

                    for (Row row : sheet) {
                        i++;

                        if (i > 1) {
                            // Skip the first row

                            if (row != null) {
                                BigDecimal jan2 = BigDecimal.ZERO;
                                BigDecimal may2 = BigDecimal.ZERO;
                                BigDecimal sep2 = BigDecimal.ZERO;
                                BigDecimal feb2 = BigDecimal.ZERO;
                                BigDecimal jun2 = BigDecimal.ZERO;
                                BigDecimal oct2 = BigDecimal.ZERO;
                                BigDecimal mar2 = BigDecimal.ZERO;
                                BigDecimal jul2 = BigDecimal.ZERO;
                                BigDecimal nov2 = BigDecimal.ZERO;
                                BigDecimal apr2 = BigDecimal.ZERO;
                                BigDecimal aug2 = BigDecimal.ZERO;
                                BigDecimal dec2 = BigDecimal.ZERO;
                                BigDecimal cost2 = BigDecimal.ZERO;
                                BigDecimal qty2 = BigDecimal.ZERO;
                                Currency cur2 = null;
                                Cell cell = row.getCell(0);
                                Cell cel2 = row.getCell(1);

                                if (cell != null && cell.getCellType() == CellType.STRING) {
                                    String stringValue = cell.getStringCellValue();
                                    Urc_Activities urcPriorityArea = sampleUrc_ActivitiesService.findByActivityCodeAndBudget(stringValue, chosenBudget);
                                    if (urcPriorityArea == null && !row.getCell(1).getStringCellValue().startsWith("1")) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 0);
                                        m.setMessage(stringValue + ":     Invalid Activity");
                                        mes.add(m);
                                    } else if (urcPriorityArea != null) {
                                        deptSection = urcDeptSectionAnlDimbgtService.findByANL_CODE(urcPriorityArea.getActivityCode().substring(0, 4));

                                        if (!user.getDeptsection().contains(urcDeptSectionAnlDimbgtService.findByANL_CODE(urcPriorityArea.getActivityCode().substring(0, 4)))) {
                                            m = new errorMessages();
                                            m.setRow("Row: " + i + " Column: " + 0);
                                            m.setMessage(stringValue + ":     No access to the Activity " + stringValue.substring(0, 4));
                                            mes.add(m);
                                        }
                                    } else {

                                    }

                                } else {
                                    /*                                    m = new errorMessages();
                                    m.setRow("Row: " + i);
                                    m.setMessage("Invalid Activity");
                                    mes.add(m);*/
                                }

                                if (deptSection != null) {
                                    if (cel2 != null && cel2.getCellType() == CellType.STRING) {
                                        String stringValue = cel2.getStringCellValue();
                                        COA coa = coaService.findByCodeAndBudget(stringValue, chosenBudget);
                                        if (coa == null) {
                                            m = new errorMessages();
                                            m.setRow("Row: " + i + " Column: " + 1);
                                            m.setMessage(stringValue + ":     Invalid Chart of Account Code");
                                            mes.add(m);
                                        } else {

                                            if (!doesUrcDeptSectionAnlDimbgtExistInUser(user.getDeptsection(), deptSection) == true) {
                                                m = new errorMessages();
                                                m.setRow("Row: " + i + " Column: " + 1);
                                                m.setMessage(deptSection.getNAME() + ":    Your not allowed to Budget for that Section ");
                                                mes.add(m);
                                            }
                                            if (!doesUrcDeptSectionAnlDimbgtExistInUser(coa.getDeptsection(), deptSection) == true) {
                                                m = new errorMessages();
                                                m.setRow("Row: " + i + " Column: " + 1);
                                                m.setMessage(coa.getCode() + ":    Your not allowed to Budget for Under this acoount code ");
                                                mes.add(m);
                                            }
                                            if (coa.getDisplay().equals(Display.FREIGHT) || coa.getDisplay().equals(Display.SALARIES)) {
                                                m = new errorMessages();
                                                m.setRow("Row: " + i + " Column: " + 1);
                                                m.setMessage(coa.getCode() + ":    Your not allowed to Budget for Under this acoount code under this interface");
                                                mes.add(m);
                                            }
                                        }

                                    } else if (cel2 != null && cel2.getCellType() == CellType.NUMERIC) {
                                        String stringValue = String.valueOf((int) cel2.getNumericCellValue());
                                        COA coa = coaService.findByCodeAndBudget(stringValue, chosenBudget);

                                        if (coa == null) {
                                            m = new errorMessages();
                                            m.setRow("Row: " + i + " Column: " + 2);
                                            m.setMessage(stringValue + ":     Invalid Chart of Account Code");
                                            mes.add(m);
                                        } else {

                                            if (!doesUrcDeptSectionAnlDimbgtExistInUser(user.getDeptsection(), deptSection) == true) {
                                                m = new errorMessages();
                                                m.setRow("Row: " + i + " Column: " + 2);
                                                m.setMessage(deptSection.getNAME() + ":    Your not allowed to Budget for that Section ");
                                                mes.add(m);
                                            }
                                            if (!doesUrcDeptSectionAnlDimbgtExistInUser(coa.getDeptsection(), deptSection) == true) {
                                                m = new errorMessages();
                                                m.setRow("Row: " + i + " Column: " + 2);
                                                m.setMessage(coa.getCode() + ":    Your not allowed to Budget for Under this acoount code ");
                                                mes.add(m);
                                            }
                                            if (coa.getDisplay().equals(Display.FREIGHT) || coa.getDisplay().equals(Display.SALARIES)) {
                                                m = new errorMessages();
                                                m.setRow("Row: " + i + " Column: " + 2);
                                                m.setMessage(coa.getCode() + ":    Your not allowed to Budget for Under this acoount code under this interface");
                                                mes.add(m);
                                            }
                                        }

                                    } else {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 2);
                                        m.setMessage("Invalid Chart of Account Code");
                                        mes.add(m);
                                    }
                                }

                                Cell cel3 = row.getCell(3);

                                if (cel3 != null && cel3.getCellType() == CellType.STRING) {
                                    String stringValue = cel3.getStringCellValue().replace(",", "");
                                    try {
                                        cost2 = new BigDecimal(stringValue);
                                        // Your code here
                                    } catch (NumberFormatException e) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 3);
                                        m.setMessage(stringValue + ":     Invalid Value");
                                        mes.add(m);
                                    }

                                } else if (cel3 != null && cel3.getCellType() == CellType.NUMERIC) {
                                    String stringValue = decimalFormat.format(cel3.getNumericCellValue()).replace(",", "");
                                    try {
                                        cost2 = new BigDecimal(stringValue);
                                        // Your code here
                                    } catch (NumberFormatException e) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 3);
                                        m.setMessage(stringValue + ":     Invalid Value");
                                        mes.add(m);
                                    }

                                } else {
                                    m = new errorMessages();
                                    m.setRow("Row: " + i);
                                    m.setMessage("Invalid Value");
                                    mes.add(m);
                                }

                                Cell cel5 = row.getCell(5);

                                if (cel5 != null && cel5.getCellType() == CellType.STRING) {
                                    String stringValue = cel5.getStringCellValue().replace(",", "");
                                    try {
                                        qty2 = new BigDecimal(stringValue);
                                        // Your code here
                                    } catch (NumberFormatException e) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 5);
                                        m.setMessage(stringValue + ":     Invalid Value");
                                        mes.add(m);
                                    }

                                } else if (cel5 != null && cel5.getCellType() == CellType.NUMERIC) {
                                    String stringValue = decimalFormat.format(cel5.getNumericCellValue()).replace(",", "");
                                    try {
                                        qty2 = new BigDecimal(stringValue);
                                        // Your code here
                                    } catch (NumberFormatException e) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 5);
                                        m.setMessage(stringValue + "X:     Invalid Value");
                                        mes.add(m);
                                    }

                                } else {
                                    m = new errorMessages();
                                    m.setRow("Row: " + i);
                                    m.setMessage("Invalid Value");
                                    mes.add(m);
                                }

                                Cell cel4 = row.getCell(4);

                                if (cel4 != null && cel4.getCellType() == CellType.STRING) {
                                    String unitmeasure = cel4.getStringCellValue();
                                    List<StockUnitMeasure> getStockUnitMeasureByUnit = sampleStockUnitMeasureService.getStockUnitMeasureByUnit(unitmeasure);

                                    if (getStockUnitMeasureByUnit.isEmpty()) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 4);
                                        m.setMessage(unitmeasure + ":     Invalid Stock Unit");
                                        mes.add(m);
                                    }

                                } else if (cel4 != null && cel4.getCellType() == CellType.NUMERIC) {
                                    int unitmeasure2 = (int) cel4.getNumericCellValue();
                                    String unitmeasure = Integer.toString(unitmeasure2);
                                    List<StockUnitMeasure> getStockUnitMeasureByUnit = sampleStockUnitMeasureService.getStockUnitMeasureByUnit(unitmeasure);

                                    if (getStockUnitMeasureByUnit.isEmpty()) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 4);
                                        m.setMessage(unitmeasure + ":     Invalid Stock Unit");
                                        mes.add(m);
                                    }

                                } else {
                                    m = new errorMessages();
                                    m.setRow("Row: " + i + " Column: " + 4);
                                    m.setMessage("X Invalid Value");
                                    mes.add(m);
                                }
                                Cell cel6 = row.getCell(6);

                                if (cel6 != null && cel6.getCellType() == CellType.STRING) {
                                    String currency = cel6.getStringCellValue();
                                    cur2 = sampleCurrencyService.findByDataCurrencyAndBudget(currency, chosenBudget);

                                    if (sampleCurrencyDataService.getCurrencyData(currency) == false) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i + " Column: " + 6);
                                        m.setMessage(currency + ":     Invalid Currency");
                                        mes.add(m);
                                    }

                                } else {
                                    m = new errorMessages();
                                    m.setRow("Row: " + i + " Column: " + 6);
                                    m.setMessage("Invalid Value");
                                    mes.add(m);
                                }

                                if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null || row.getCell(3) == null || row.getCell(4) == null || row.getCell(5) == null || row.getCell(6) == null) {
                                    m = new errorMessages();
                                    m.setRow("Row: " + i);
                                    m.setMessage("Empty columns an not accepted");
                                    mes.add(m);
                                }
                            }
                        }
                    }

                    if (!mes.isEmpty()) {
                        warningNotification(mes);
                        System.out.println(error.toString());
                    } else {
                        System.out.println("Start uploading budget");
                        String fileName2 = event.getFileName();
                        InputStream inputStream2 = buffer2.getInputStream(fileName2);
                        uploadSectionsBudget(inputStream2);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                m = new errorMessages();
                m.setRow("Error:");
                m.setMessage("No Budget Selected");
                mes.add(m);

                warningNotification(mes);
            }

        });

        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2, budgetTabs());
    }

    private TabSheet budgetTabs() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setHeight("100%");
        SplitLayout lay = new SplitLayout(settingView(), coaView());
        lay.setHeight("100%");
        lay.setSplitterPosition(50);

        tabSheet.add("Budget Details", lay);

        VerticalLayout vert = new VerticalLayout();
        vert.setHeight("100%");
        vert.add(BudgetSemiDetailDiv());

        SplitLayout vert2 = new SplitLayout(budgetDetailView(), budgetWorkplanView());
        vert2.setHeight("100%");
        vert2.setOrientation(SplitLayout.Orientation.VERTICAL);
        vert2.setSplitterPosition(50);
        //vert2.add(budgetDetailView(), budgetWorkplanView());

        SplitLayout vert3 = new SplitLayout(budgetView(), budgetCoaView());
        vert3.setHeight("100%");
        vert3.setOrientation(SplitLayout.Orientation.VERTICAL);
        vert3.setSplitterPosition(50);
        //vert3.add(budgetView(), budgetCoaView());

        SplitLayout layy = new SplitLayout(vert2, vert3);
        layy.setSplitterPosition(40);
        layy.setHeight("100%");
        vert.add(layy);

        tabSheet.add("Budget Form", vert);
        return tabSheet;
    }

    private FormLayout BudgetSemiDetailDiv() {
        FormLayout div = new FormLayout();
        fy = new Span("");
        fy.getElement().getThemeList().add("badge");

        fyType = new Span("");
        fyType.getElement().getThemeList().add("badge success");
        deptUnit = new Span("");
        deptUnit.getElement().getThemeList().add("badge error");
        accCode = new Span("");
        accCode.getElement().getThemeList().add("badge contrast");
        activity = new Span("");
        activity.getElement().getThemeList().add("badge");
        div.add(fy, fyType, deptUnit, accCode, activity);
        div.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 2),
                // Use two columns, if the layout's width exceeds 320px
                new ResponsiveStep("320px", 3),
                // Use three columns, if the layout's width exceeds 500px
                new ResponsiveStep("500px", 4),
                new ResponsiveStep("700px", 5));
        return div;
    }

    private void setBudgetDetails() {
        fy.setTitle("FInacial Year");
        if (!comboBoxBudget.isEmpty()) {
            fy.setText("Budget: " + chosenBudget.getFinancialYear());
        } else {
            fy.setText("");
        }
        if (!comboBoxOrganisation.isEmpty()) {
            fyType.setText("Budget Type: " + chosenOrganisation.getName());
        } else {
            fyType.setText("");
        }
        if (!comboBoxD_Section.isEmpty()) {
            deptUnit.setText("Section: " + chosenDsection.getNAME());
        } else {
            deptUnit.setText("");
        }
        if (!comboBoxCoalevel1.isEmpty() && !gridCOA.asSingleSelect().isEmpty()) {
            accCode.setText("Chart Of Accounts: " + chosenCOA.getCode() + ": " + chosenCOA.getName());
            if (!gridUrc_Activities.asSingleSelect().isEmpty() && chosenCOA.getCoalevel1().getCode() != 1) {

                activity.setText("Activities: " + chosenUrc_Activities.getName() + " (" + chosenUrc_Activities.getActivityCode() + ")");
            } else {
                activity.setText("");
            }
        } else {
            accCode.setText("");
        }

    }

    private VerticalLayout settingView() {
        VerticalLayout contain = new VerticalLayout();
        contain.setAlignItems(FlexComponent.Alignment.STRETCH);

        contain.setHeight("100%");
        contain.setMaxWidth("100%");
        contain.setPadding(false);
        contain.setSpacing(false);
        contain.setWidthFull();
        //contain.getStyle().set("border-top", "1px solid var(--lumo-contrast-20pct)");

        FormLayout hor = new FormLayout();

        // Header
        Header header = new Header();
        Footer footer = new Footer();

        footer.getStyle().set("height", "30px")
                .set("margin", "1px");

        /*        header.getStyle().set("align-items", "center").set("height", "50px")
        .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
        .set("display", "flex").set("padding", "var(--lumo-space-m)");*/
        H5 editEmployee = new H5("Budget Details");
        editEmployee.getStyle().set("margin", "0");

        Icon arrowLeft = VaadinIcon.ARROW_LEFT.create();
        arrowLeft.setSize("var(--lumo-icon-size-m)");
        arrowLeft.getElement().setAttribute("aria-hidden", "true");
        arrowLeft.getStyle().set("box-sizing", "border-box")
                .set("margin-right", "var(--lumo-space-m)")
                .set("padding", "calc(var(--lumo-space-xs) / 2)");

        Anchor goBack = new Anchor("#", arrowLeft);

        header.add(goBack, editEmployee);

        //contain.add(header);
        comboBoxBudget = new ComboBox<>("Budget");

        comboBoxD_Section = new ComboBox<>("Cost Centre");
        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);

        budgetItemfundSource.setItemLabelGenerator(Fundsource::getFundsource);

        comboBoxD_Section.setWidthFull();
        comboBoxD_Section.setEnabled(false);

        comboBoxOrganisation = new ComboBox<>("Budget Type");
        comboBoxOrganisation.addValueChangeListener(e -> {
            chosenOrganisation = e.getValue();
            setBudgetDetails();
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null && chosenUrc_Activities != null) {
                refreshgridBudgetItems();
            }
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null) {

                refreshgridBudgetItemCOA2();
            }
            refreshActivitiesSetingGrid2("");
        });

        comboBoxOrganisation.setEnabled(false);
        gridUrc_Activities.setEnabled(false);

        // Configure Grid
        gridUrc_Activities = new Grid<>(Urc_Activities.class, false);
        Grid.Column<Urc_Activities> name = gridUrc_Activities.addColumn("name").setHeader("URC Activities").setAutoWidth(true);
        gridUrc_Activities.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(urcActivity.getDeptSection().getNAME());

            if (isUserCreator(urcActivity.getDeptSection())) {
                span.getElement().getThemeList().add("badge success");
            } else {
                span.getElement().getThemeList().add("badge error");
            }
            return span;
        })).setHeader("Section Creator").setFlexGrow(0).setWidth("150px");
        gridUrc_Activities.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(urcActivity.getActivityCode());
            span.getElement().getThemeList().add("badge success");

            return span;
        })).setHeader("Code").setFlexGrow(0).setWidth("150px");
        //gridUrc_Activities.addColumn("budget").setHeader("Budget").setAutoWidth(true).setFlexGrow(0).setWidth("150px");
        BigDecimal Total = budgetItemsService.sumMonthsBySection4(chosenCoalevel1, chosenOrganisation, chosenBudget, chosenDsection);
        gridUrc_Activities.addColumn(new ComponentRenderer<>(urcActivity -> {

            Span span = new Span("");
            if (urcActivity.getActivity_budget() == null) {
                BigDecimal tot = budgetItemsService.sumMonthsByActivityBySection4(chosenCoalevel1, chosenOrganisation, chosenBudget, urcActivity, chosenDsection);
                span = new Span(decimalFormat.format(tot));
            } else {
                span = new Span(urcActivity.getActivity_budget().toString());
            }

            return span;
        })).setHeader("Budget").setFlexGrow(0).setWidth("150px");
        gridUrc_Activities.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        TextField searchField = new TextField();
        searchField.setWidth("50%");

        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            //gridUrc_Activities.setItems(sampleUrc_ActivitiesService.findByBudgetAndSearch(chosenBudget, e.getValue()));
            if (!comboBoxD_Section.isEmpty()) {
                // gridUrc_Activities.setItems(sampleUrc_ActivitiesService.findByDeptUnitsAndName(unitService.findBySection(comboBoxD_Unit.getValue().getSection()), e.getValue()));
            }

        });

        comboBoxBudget.setItems(query -> chosenBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        comboBoxBudget.setItemLabelGenerator(Budget::getFinancialYear);

        gridUrc_Activities.asSingleSelect().addValueChangeListener(vl -> {
            chosenUrc_Activities = vl.getValue();
            setBudgetDetails();
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null && chosenUrc_Activities != null) {
                refreshgridBudgetItems();
            }
            refreshgridBudgetItemCOA2();
        });
        comboBoxBudget.addValueChangeListener(e -> {
            if (e.getValue().isActive() == false) {
                disabledBudget(false);
            }
            gridCOA.setItems(new ArrayList<>());
            budgetItemfundSource.setItems(sampleFundsourceService.findFundsourcesByBudget(e.getValue()));
            chosenBudget = e.getValue();
            editEmployee.setText("Budget Details " + chosenBudget.getFinancialYear());
            comboBoxD_Section.setEnabled(true);
            comboBoxOrganisation.setEnabled(true);
            gridUrc_Activities.setEnabled(true);

            //refreshActivitiesSetingGrid2();
            comboBoxOrganisation.setItems(organisationService.findByBudgetList(chosenBudget));
            comboBoxOrganisation.setItemLabelGenerator(Organisation::getName);
            comboBoxD_Section.setItems(user.getDeptsection());
            setBudgetDetails();
            if (!comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                comboBoxCoalevel1.setItems(coalevel1Service.findByBudget());
                gridCOA.setEnabled(true);
                gridCOA.getSelectedItems().clear();
            } else {
                comboBoxCoalevel1.clear();
                gridCOA.setEnabled(false);
                gridCOA.getSelectedItems().clear();

            }
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null && chosenUrc_Activities != null) {
                refreshgridBudgetItems();
            }
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null) {

                refreshgridBudgetItemCOA2();
            }
            refreshActivitiesSetingGrid2("");
        });
        comboBoxD_Section.addValueChangeListener(ed -> {
            gridCOA.setItems(new ArrayList<>());
            chosenDsection = ed.getValue();

            setBudgetDetails();
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty()) {
                comboBoxCoalevel1.setItems(coalevel1Service.findByBudget());
                gridCOA.setEnabled(true);
                gridCOA.getSelectedItems().clear();

            } else {
                comboBoxCoalevel1.clear();
                gridCOA.setEnabled(false);
                gridCOA.getSelectedItems().clear();
            }
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null && chosenUrc_Activities != null) {
                refreshgridBudgetItems();
            }
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null) {

                refreshgridBudgetItemCOA2();
            }
            refreshActivitiesSetingGrid2("");
        });

        hor.add(comboBoxBudget, comboBoxOrganisation, comboBoxD_Section, searchField);
        //hor.setAlignItems(Alignment.BASELINE);
        VerticalLayout vert = new VerticalLayout();
        //hor.setHeightFull();
        vert.setHeight("100%");
        vert.add(hor, gridUrc_Activities);
        Footer ft = new Footer();
        ft.getElement().getStyle().set("margin-left", "auto");
        calculateSumOfAllMonthstotalText2.setWidthFull();
        ft.add(calculateSumOfAllMonthstotalText2);
        ft.getElement().getThemeList().add("badge success");
        vert.add(ft);
        Scroller scroller = new Scroller(vert);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "var(--lumo-space-m)");
        scroller.setHeight("100%");
        contain.add(scroller, footer);
        //contain.add(hor);

        return contain;
    }

    private boolean isUserCreator(UrcDeptSectionAnlDimbgt unit) {
        Optional<User> maybeUser = authenticatedUser.get();

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            return user.getDeptsection().contains(unit);
        }

        return false;
    }

    private VerticalLayout budgetDetailView() {
        Button button = new Button("temp");
        VerticalLayout contain = new VerticalLayout();
        contain.setAlignItems(FlexComponent.Alignment.STRETCH);

        Header header = new Header();
        Footer footer = new Footer();

        H5 editEmployee = new H5("Budget Item Details");
        editEmployee.getStyle().set("margin", "0");

        Icon arrowLeft = VaadinIcon.ARROW_LEFT.create();
        arrowLeft.setSize("var(--lumo-icon-size-m)");
        arrowLeft.getElement().setAttribute("aria-hidden", "true");
        arrowLeft.getStyle().set("box-sizing", "border-box")
                .set("margin-right", "var(--lumo-space-m)")
                .set("padding", "calc(var(--lumo-space-xs) / 2)");

        FormLayout form = new FormLayout();
        currency.setItems(query -> sampleCurrencyService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        currency.setItemLabelGenerator(item -> item.getData().getCurrencyShort());
        unitMeasure.setItems(sampleStockUnitMeasureService.findAllUnitValues());
        //unitMeasure.setItemLabelGenerator(StockUnitMeasure::getUnit);
        total.setEnabled(true);
        cost.addValueChangeListener(v -> {
            if (!currency.isEmpty() && !qty.isEmpty()) {
                total.setValue(calculateTotal());
            }
        });
        qty.addValueChangeListener(v -> {
            if (!currency.isEmpty() && !cost.isEmpty()) {
                total.setValue(calculateTotal());
            }
        });
        currency.addValueChangeListener(v -> {
            if (!cost.isEmpty() && !qty.isEmpty()) {
                total.setValue(calculateTotal());
            }
        });
        form.add(Item, cost, qty, unitMeasure, currency, budgetItemfundSource, procClassCombo, total, notes);
        binderbudgetItem.forField(Item).bind("item");
        binderbudgetItem.forField(cost).bind("cost");
        binderbudgetItem.forField(qty).bind("qty");
        binderbudgetItem.forField(currency).bind("currency");
        binderbudgetItem.forField(budgetItemfundSource).bind("fundsource");
        binderbudgetItem.forField(jan).bind("jan");
        binderbudgetItem.forField(feb).bind("feb");
        binderbudgetItem.forField(mar).bind("mar");
        binderbudgetItem.forField(apr).bind("apr");
        binderbudgetItem.forField(may).bind("may");
        binderbudgetItem.forField(jun).bind("jun");
        binderbudgetItem.forField(jul).bind("jul");
        binderbudgetItem.forField(aug).bind("aug");
        binderbudgetItem.forField(sep).bind("sep");
        binderbudgetItem.forField(oct).bind("oct");
        binderbudgetItem.forField(nov).bind("nov");
        binderbudgetItem.forField(dec).bind("dec");
        binderbudgetItem.forField(notes).bind("notes");
        binderbudgetItem.forField(unitMeasure).bind("unitMeasure");
        total.setEnabled(false);

        form.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new ResponsiveStep("320px", 2),
                // Use three columns, if the layout's width exceeds 500px
                new ResponsiveStep("500px", 3),
                new ResponsiveStep("700px", 4));
        form.setColspan(Item, 4);
        Scroller scroller = new Scroller(
                new Div(editEmployee, form));
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "var(--lumo-space-m)");
        deleteBudgetItem.addClickListener(e -> {

            //BudgetItems budg = gridBudgetItems.asSingleSelect().getValue();
            try {
                if (!gridBudgetItems.asSingleSelect().isEmpty()) {
                    BudgetItems budg = gridBudgetItems.asSingleSelect().getValue();
                    // Notificationwarning("Testing speed"+budg.getItem());
                    status = new Span();
                    status.setVisible(false);

                    ConfirmDialog dialog = new ConfirmDialog();
                    dialog.setHeader("Delete Budget Item");
                    dialog.setText(
                            "You are trying to Delete an Item. Are you Sure you want to Delete?");

                    dialog.setCancelable(true);
                    dialog.addCancelListener(event -> setStatus("Canceled"));

                    dialog.setRejectable(true);
                    dialog.setRejectText("Discard");
                    dialog.addRejectListener(event -> setStatus("Discarded"));

                    dialog.setConfirmText("Delete");
                    dialog.addConfirmListener(event -> {

                        budgetItemsService.deleteBudgetItem(budg);
                        clearWorkplan();
                        clearBudget();
                        refreshgridBudgetItems();
                        UI.getCurrent().navigate(BudgetFormView.class);
                    });

                    dialog.open();

                } else {
                    Notificationwarning("Null Selection");
                }

            } catch (Exception validationException) {
                NotificationError(" An exception happened while trying to delete. " + validationException.getMessage());
            }
        });
        saveBudgetItem.addSingleClickListener(v -> {
            if (ValidateBudgetFigures() == true) {
                BudgetItems budg = gridBudgetItems.asSingleSelect().getValue();

                try {

                    if (budg == null) {
                        budg = new BudgetItems();

                    }
                    budg.setItem(Item.getValue());
                    budg.setCost(getNonNullValue(cost).stripTrailingZeros());
                    budg.setQty(getNonNullValue(qty).stripTrailingZeros());
                    budg.setUnitMeasure(unitMeasure.getValue());
                    budg.setCurrency(currency.getValue());
                    budg.setNotes(notes.getValue());
                    budg.setBudget(chosenBudget);
                    // budg.setFundsource(budgetItemfundSource.getValue());
                    Fundsource selectedFundsource = budgetItemfundSource.getValue();

                    if ((chosenCOA.getCoalevel1().getCode() == 2 || chosenCOA.getCoalevel1().getCode() == 3) && selectedFundsource != null && selectedFundsource.getId() != null) {
                        selectedFundsource = sampleFundsourceService.findById(selectedFundsource.getId());

                        budg.setFundsource(selectedFundsource);
                    } else if ((chosenCOA.getCoalevel1().getCode() == 1) && selectedFundsource != null && selectedFundsource.getId() != null) {

                    } else {
                        //System.out.println("Not An Expenditure proper");
                        //throw new IllegalArgumentException("No Fundsource selected nnn");
                    }

                    budg.setBudgetType(chosenOrganisation);
                    budg.setCoacode(chosenCOA);
                    budg.setProcClass(procClassCombo.getValue());
                    if (budg.getCoacode().getCoalevel1().getCode() != 1) {
                        budg.setActivity(chosenUrc_Activities);
                    }

                    budg.setDeptUnit(chosenDsection);
                    budg.setCoalevel1(chosenCoalevel1);

                    budg.setJan(getNonNullValue(jan).stripTrailingZeros());
                    budg.setFeb(getNonNullValue(feb).stripTrailingZeros());
                    budg.setMar(getNonNullValue(mar).stripTrailingZeros());
                    budg.setApr(getNonNullValue(apr).stripTrailingZeros());
                    budg.setMay(getNonNullValue(may).stripTrailingZeros());
                    budg.setJun(getNonNullValue(jun).stripTrailingZeros());
                    budg.setJul(getNonNullValue(jul).stripTrailingZeros());
                    budg.setAug(getNonNullValue(aug).stripTrailingZeros());
                    budg.setSep(getNonNullValue(sep).stripTrailingZeros());
                    budg.setOct(getNonNullValue(oct).stripTrailingZeros());
                    budg.setNov(getNonNullValue(nov).stripTrailingZeros());
                    budg.setDec(getNonNullValue(dec).stripTrailingZeros());
                    budgetItemsService.update(budg);
                    ProcurementPlan getAllProcurementPlans = sampleProcurementPlanService.findFirstByBudgetAndProcClassAndCoa(budg.getBudget(), budg.getProcClass(), budg.getCoacode());
                    if (getAllProcurementPlans != null) {
                        // pp.setCost(budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(budg.getBudget(), budg.getProcClass(), budg.getCoacode()));
                        BigDecimal tDecimal = budgetItemsService.sumOfAllMonthsByBudgetAndProcClassAndCoa(getAllProcurementPlans.getBudget(), getAllProcurementPlans.getProcClass(), getAllProcurementPlans.getCoa());
                        getAllProcurementPlans.setProcurementMethod(sampleProcurementPlanService.getProcurementMethodList2(budg.getProcClass(), tDecimal));
                        sampleProcurementPlanService.save(getAllProcurementPlans);
                    } else {
                        ProcurementPlan pr = new ProcurementPlan();
                        pr.setSubject(budg.getCoacode().getName());
                        pr.setBudget(budg.getBudget()); // Assuming all selected plans have the same budget
                        pr.setCoa(budg.getCoacode());
                        Set<Fundsource> fundsourceSet = new HashSet<>();
                        fundsourceSet.add(budg.getFundsource());
                        //pr.setFundsource(fundsourceSet); // Assuming all selected plans have the same fund source
                        pr.setCost(calculateMonthSum(budg));
                        pr.setProcClass(procClassCombo.getValue());
                        pr.setProcurementMethod(sampleProcurementPlanService.getProcurementMethodList2(budg.getProcClass(), pr.getCost()));

                        sampleProcurementPlanService.save(pr);
                    }

                    refreshgridBudgetItems();
                    refreshgridBudgetItemCOA2();
                    clearWorkplan();
                    clearBudget();
                    UI.getCurrent().navigate(BudgetFormView.class);
                } catch (Exception validationException) {
                    NotificationError(" An exception happened while trying to saving. " + validationException.getMessage());
                    System.out.println(validationException.getMessage());

                }
            } else {
                Notification note = Notification.show("Check your settings");
                note.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        });
        staff.addClickListener(e -> {

            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && chosenUrc_Activities != null) {
                // OldBudget bugt = oldBudgetBox.getValue();
                List<Staff> staffs = staffService.getStaffByFinancialYear(comboBoxBudget.getValue().getFinancialYear());

                if (!staffs.isEmpty()) {
                    Budget bgt = comboBoxBudget.getValue();
                    COA salary = coaService.findByCodeAndBudget("211101", bgt);

                    COA nssf = coaService.findByCodeAndBudget("212101", bgt);

                    COA gratuity = coaService.findByCodeAndBudget("213004", bgt);

                    COA workmanscompensation = coaService.findByCodeAndBudget("213005", bgt);
                    Currency cur = sampleCurrencyService.findCurrenciesByCurrencyShortAndBudget("UGX", bgt);

                    if (budgetItemsService.isItemWithCoacodeExists(nssf, bgt) == true) {

                        budgetItemsService.deleteByCoacode(nssf, bgt);

                    }
                    if (budgetItemsService.isItemWithCoacodeExists(gratuity, bgt) == true) {
                        budgetItemsService.deleteByCoacode(gratuity, bgt);
                    }
                    if (budgetItemsService.isItemWithCoacodeExists(workmanscompensation, bgt) == true) {

                        budgetItemsService.deleteByCoacode(workmanscompensation, bgt);

                    }
                    if (budgetItemsService.isItemWithCoacodeExists(salary, bgt) == true) {

                        budgetItemsService.deleteByCoacode(salary, bgt);

                    }
                    for (Staff st : staffs) {
                        StaffSalary staf = new StaffSalary();
                        staf.setFname(st.getFname());
                        staf.setLname(st.getLname());
                        staf.setAddress(st.getAddress());
                        staf.setAddress2(st.getAddress2());
                        staf.setBudget(bgt);
                        staf.setCode(st.getCode());
                        staf.setEmail(st.getEmail());
                        staf.setTel(st.getTel());
                        staf.setPosition(st.getPosition());
                        staf.setGrade(GetScale(st.getGrade()));
                        staf.setSalary(st.getSalary());
                        staffSalaryService.saveStaffSalary(staf);

                        BudgetItems salaryItem = new BudgetItems();
                        salaryItem.setItem(staf.getFname() + " " + staf.getLname());
                        salaryItem.setCost(staf.getSalary());
                        salaryItem.setQty(new BigDecimal("12"));

                        salaryItem.setUnitMeasure("MONTH");

                        salaryItem.setCurrency(cur);
                        salaryItem.setBudget(staf.getBudget());
                        salaryItem.setBudgetType(comboBoxOrganisation.getValue());
                        salaryItem.setCoacode(salary);
                        salaryItem.setDeptUnit(comboBoxD_Section.getValue());
                        salaryItem.setAnalcode(staffSalaryService.getLastSavedItem().getId());
                        salaryItem.setBcategory(salary.getCode());
                        salaryItem.setActivity(chosenUrc_Activities);
                        salaryItem.setCoalevel1(chosenCoalevel1);
                        salaryItem.setJan(staf.getSalary());
                        salaryItem.setFeb(staf.getSalary());
                        salaryItem.setMar(staf.getSalary());
                        salaryItem.setApr(staf.getSalary());
                        salaryItem.setMay(staf.getSalary());
                        salaryItem.setJun(staf.getSalary());
                        salaryItem.setJul(staf.getSalary());
                        salaryItem.setAug(staf.getSalary());
                        salaryItem.setSep(staf.getSalary());
                        salaryItem.setOct(staf.getSalary());
                        salaryItem.setNov(staf.getSalary());
                        salaryItem.setDec(staf.getSalary());
                        budgetItemsService.update(salaryItem);
                    }
                    BudgetItems bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    //bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    BudgetItems nssfBudget = new BudgetItems();
                    nssfBudget.setActivity(bug.getActivity());
                    nssfBudget.setCoacode(nssf);
                    nssfBudget.setProcClass(nssf.getProcclass());
                    nssfBudget.setItem(nssf.getName());
                    nssfBudget.setBudget(bgt);
                    nssfBudget.setBudgetType(bug.getBudgetType());
                    nssfBudget.setCoalevel1(bug.getCoalevel1());
                    nssfBudget.setCurrency(bug.getCurrency());
                    nssfBudget.setDeptUnit(bug.getDeptUnit());
                    nssfBudget.setBcategory(nssf.getCode());
                    nssfBudget.setCost(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setQty(new BigDecimal("12"));
                    nssfBudget.setUnitMeasure("MONTH");
                    nssfBudget.setJul(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setAug(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setSep(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setOct(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setNov(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setDec(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setJan(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setFeb(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setMar(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setApr(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setMay(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setJun(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    budgetItemsService.update(nssfBudget);

                    //                   bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    BudgetItems gratuityBudget = new BudgetItems();
                    gratuityBudget.setActivity(bug.getActivity());
                    gratuityBudget.setCoacode(gratuity);
                    gratuityBudget.setProcClass(gratuity.getProcclass());
                    gratuityBudget.setItem(gratuity.getName());
                    gratuityBudget.setBudget(bgt);
                    gratuityBudget.setBudgetType(bug.getBudgetType());
                    gratuityBudget.setCoalevel1(bug.getCoalevel1());
                    gratuityBudget.setCurrency(bug.getCurrency());
                    gratuityBudget.setDeptUnit(bug.getDeptUnit());
                    gratuityBudget.setBcategory(gratuity.getCode());
                    System.out.println(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt) + " Summation");
                    gratuityBudget.setCost(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setQty(new BigDecimal("12"));
                    gratuityBudget.setJul(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setAug(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setSep(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setOct(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setNov(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setDec(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setJan(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setFeb(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setMar(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setApr(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setMay(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setJun(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setUnitMeasure("MONTH");
                    budgetItemsService.update(gratuityBudget);

                    //                    bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    BudgetItems workmanscompensationBudget = new BudgetItems();
                    workmanscompensationBudget.setActivity(bug.getActivity());
                    workmanscompensationBudget.setCoacode(workmanscompensation);
                    workmanscompensationBudget.setProcClass(workmanscompensation.getProcclass());
                    workmanscompensationBudget.setItem(workmanscompensation.getName());
                    workmanscompensationBudget.setBudget(bgt);
                    workmanscompensationBudget.setBudgetType(bug.getBudgetType());
                    workmanscompensationBudget.setCoalevel1(bug.getCoalevel1());
                    workmanscompensationBudget.setCurrency(bug.getCurrency());
                    workmanscompensationBudget.setDeptUnit(bug.getDeptUnit());
                    workmanscompensationBudget.setBcategory(workmanscompensation.getCode());
                    workmanscompensationBudget.setCost(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setQty(new BigDecimal("12"));
                    workmanscompensationBudget.setJul(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setAug(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setSep(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setOct(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setNov(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setDec(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setJan(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setFeb(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setMar(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setApr(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setMay(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setJun(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setUnitMeasure("MONTH");
                    budgetItemsService.update(workmanscompensationBudget);
                }
            } else {
                Notification.show("Something is empty");
            }

        });
        button.addClickListener(e -> {
            fixCOA();
            //fixFundsource();
            /*            List<BudgetItems> findByDeptUnitAndBudget = budgetItemsService.findByCoacodeCodeStartingWith2Or3();
            for (BudgetItems y : findByDeptUnitAndBudget) {
            y.setProcClass(y.getCoacode().getProcclass());
            budgetItemsService.update(y);
            }*/
        });
        footer.add(saveBudgetItem, deleteBudgetItem, distrWorkplan, quarterWorkplan, clearWorkplan, templateDownload, uploadBudget);
        if (user.getRoles().contains(Role.ADMIN)) {
            footer.add(rectify, button);
        }
        contain.add(scroller, footer);

        return contain;
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

    private void fixCOA() {
        List<BudgetItems> findByDeptUnitAndBudget = budgetItemsService.findByAll();
        for (BudgetItems z : findByDeptUnitAndBudget) {

            z.setBcategory(z.getCoacode().getCode());
            if (!z.getBcategory().trim().equals(z.getCoacode().getCode().trim())) {
                System.out.println(z.getBcategory() + "   " + z.getCoacode().getCode() + " 1");
            }
            //System.out.println(z.getBcategory() + "   " + z.getCoacode().getCode() + " 1");

            int p = Integer.parseInt(z.getCoacode().getCode().substring(0, 1));
            z.setCoalevel1(coalevel1Service.findByCode(p));

            budgetItemsService.update(z);
            /*            if (z.getCost() == null || z.getCost() == BigDecimal.ZERO || z.getQty() == null || z.getQty() == BigDecimal.ZERO) {
            budgetItemsService.deleteBudgetItem(z);
            }*/
        }
        //fixFundsource();
    }

    private void fixFundsource() {
        List<BudgetItems> findByDeptUnitAndBudget = budgetItemsService.findByAll();
        for (BudgetItems z : findByDeptUnitAndBudget) {

            //z.setBcategory(z.getCoacode().getCode());
            int p = Integer.parseInt(z.getCoacode().getCode().substring(0, 1));
            //z.setCoalevel1(coalevel1Service.findByCode(p));
            Fundsource source = sampleFundsourceService.findByFundsourceAndBudget("IGR", z.getBudget());
            if (source != null && p != 1 && z.getFundsource() == null) {
                z.setFundsource(source);
                budgetItemsService.update(z);
            }

        }
    }

    private BigDecimal currentExchangeRate(BudgetItems z) {
        BigDecimal costValue = z.getCost();
        BigDecimal qtyValue = z.getQty();
        BigDecimal total = calculateMonthSum(z);
        return total.divide(costValue).divide(qtyValue);
    }

    private BigDecimal calculateTotal() {
        BigDecimal costValue = getNonNullValue(cost).stripTrailingZeros();
        BigDecimal qtyValue = getNonNullValue(qty).stripTrailingZeros();
        Currency cur = currency.getValue();
        BigDecimal totalValues = costValue.multiply(qtyValue).multiply(cur.getRate()).stripTrailingZeros();
        return totalValues;
    }

    private BigDecimal calculateTotal(BigDecimal costValue, BigDecimal qtyValue, Currency cur) {

        costValue = getNonNullValue(cost).stripTrailingZeros();
        qtyValue = getNonNullValue(qty).stripTrailingZeros();
        cur = currency.getValue();
        BigDecimal totalValues = costValue.multiply(qtyValue).multiply(cur.getRate()).stripTrailingZeros();
        return totalValues;
    }

    private BigDecimal calculateSumOfAllMonths() {
        BigDecimal costValue = BigDecimal.ZERO;
        if (!comboBoxCoalevel1.isEmpty() && !gridUrc_Activities.asSingleSelect().isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty()) {
            costValue = budgetItemsService.sumMonthsByParameters(comboBoxCoalevel1.getValue(), comboBoxOrganisation.getValue(), comboBoxBudget.getValue(), gridUrc_Activities.asSingleSelect().getValue(), comboBoxD_Section.getValue(), chosenCOA);
        }

        return costValue;
    }

    private BigDecimal calculateSumOfAllMonths3(Coalevel1 coalevel1, COA coa) {
        BigDecimal costValue = BigDecimal.ZERO;
        if (!gridUrc_Activities.asSingleSelect().isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty()) {
            costValue = budgetItemsService.sumMonthsByParameters(coalevel1, comboBoxOrganisation.getValue(), comboBoxBudget.getValue(), gridUrc_Activities.asSingleSelect().getValue(), comboBoxD_Section.getValue(), coa);
        }

        return costValue;
    }

    private BigDecimal calculateSumOfAllMonths2(Coalevel1 coalevel1, COA coa) {
        BigDecimal costValue = BigDecimal.ZERO;
        if (!comboBoxOrganisation.isEmpty() && !comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty()) {
            costValue = budgetItemsService.sumMonthsByParameters2(coalevel1, comboBoxOrganisation.getValue(), comboBoxBudget.getValue(), comboBoxD_Section.getValue(), coa);
        }

        return costValue;
    }

    private BigDecimal calculateSumOfAllMonthsSectionActivity() {
        BigDecimal costValue = BigDecimal.ZERO;
        if (!comboBoxCoalevel1.isEmpty() && !gridUrc_Activities.asSingleSelect().isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty()) {
            costValue = budgetItemsService.sumMonthsByActivityBySection(comboBoxCoalevel1.getValue(), comboBoxOrganisation.getValue(), comboBoxBudget.getValue(), gridUrc_Activities.asSingleSelect().getValue(), comboBoxD_Section.getValue());
        }

        return costValue;
    }

    private boolean ValidateBudgetFigures() {
        boolean result = true;
        if (checkPreSettings() == false) {
            Item.setErrorMessage("Make sure all presettings are okay");
            Item.setInvalid(true);
            result = false;
        }
        if (Item.isEmpty()) {
            Item.setErrorMessage("Budget Item should not be empty");
            Item.setInvalid(true);
            result = false;

        }
        if (cost.isEmpty()) {
            cost.setErrorMessage("Cost of Budget Item should not be empty");
            cost.setInvalid(true);
            result = false;
        }
        if (currency.isEmpty()) {
            currency.setErrorMessage("Currency should not be empty");
            currency.setInvalid(true);
            result = false;
        }
        if (qty.isEmpty()) {
            qty.setErrorMessage("Cost of Budget Item should not be empty");
            qty.setInvalid(true);
            result = false;
        }
        if (unitMeasure.isEmpty()) {
            unitMeasure.setErrorMessage("Unit Measure should not be empty");
            unitMeasure.setInvalid(true);
            result = false;
        }
        if (workplan_total_check() == false) {
            total.setErrorMessage("Total Should Match the work plan alignment");
            total.setInvalid(true);
            result = false;
        }
        if (budgetItemfundSource.isEmpty() && (chosenCOA.getCoalevel1().getCode() == 2 || chosenCOA.getCoalevel1().getCode() == 3)) {
            budgetItemfundSource.setErrorMessage("Set the Fund Source");
            budgetItemfundSource.setInvalid(true);
            result = false;
        }
        if (procClassCombo.isEmpty() && (chosenCOA.getCoalevel1().getCode() == 2 || chosenCOA.getCoalevel1().getCode() == 3)) {
            procClassCombo.setErrorMessage("Set the Procurement class");
            procClassCombo.setInvalid(true);
            result = false;
        }
        return result;
    }

    private boolean checkPreSettings() {
        if (fy.getText().equals("") || fy.getText() == null) {
            return false;
        }
        if (fyType.getText().equals("") || fyType.getText() == null) {
            return false;
        }
        if (deptUnit.getText().equals("") || deptUnit.getText() == null) {
            return false;
        }
        if (accCode.getText().equals("") || accCode.getText() == null) {
            return false;
        }
        if (chosenCOA.getCoalevel1().getCode() != 1 && (activity.getText().equals("") || activity.getText() == null)) {
            return false;
        } else {
            return true;
        }

    }

    private BigDecimal workplan_total() {
        BigDecimal totalValue = getNonNullValue(total).stripTrailingZeros();

        // Initialize totalWorkplan to zero
        BigDecimal totalWorkplan = BigDecimal.ZERO;

        // Add non-null values of months to totalWorkplan
        totalWorkplan = totalWorkplan.add(getNonNullValue(jan)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(feb)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(mar)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(apr)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(may)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(jun)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(jul)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(aug)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(sep)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(oct)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(nov)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(dec)).stripTrailingZeros();

        return totalWorkplan;
    }

    private boolean workplan_total_check() {
        BigDecimal totalValue = getNonNullValue(total).stripTrailingZeros();

        // Initialize totalWorkplan to zero
        BigDecimal totalWorkplan = BigDecimal.ZERO;

        // Add non-null values of months to totalWorkplan
        totalWorkplan = totalWorkplan.add(getNonNullValue(jan)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(feb)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(mar)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(apr)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(may)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(jun)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(jul)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(aug)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(sep)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(oct)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(nov)).stripTrailingZeros();
        totalWorkplan = totalWorkplan.add(getNonNullValue(dec)).stripTrailingZeros();
        return totalValue.compareTo(totalWorkplan) == 0;

    }

    private BigDecimal getNonNullValue(BigDecimalField field) {
        return field.getValue() != null ? field.getValue() : BigDecimal.ZERO;
    }

    private VerticalLayout budgetWorkplanView() {
        VerticalLayout contain = new VerticalLayout();
        contain.setAlignItems(FlexComponent.Alignment.STRETCH);
        /*        contain.setHeightFull();
        contain.setMaxWidth("100%");
        contain.setPadding(true);
        contain.setSpacing(true);
        contain.setWidthFull();*/
        //contain.getStyle().set("border-top", "1px solid var(--lumo-contrast-20pct)");

        // Header
        Header header = new Header();
        Footer footer = new Footer();
        /*        header.getStyle().set("align-items", "center").set("height", "50px")
        .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
        .set("display", "flex").set("padding", "var(--lumo-space-m)");*/

        H5 editEmployee = new H5("Budget Item Execution Month(s)");
        editEmployee.getStyle().set("margin", "0");

        Icon arrowLeft = VaadinIcon.ARROW_LEFT.create();
        arrowLeft.setSize("var(--lumo-icon-size-m)");
        arrowLeft.getElement().setAttribute("aria-hidden", "true");
        arrowLeft.getStyle().set("box-sizing", "border-box")
                .set("margin-right", "var(--lumo-space-m)")
                .set("padding", "calc(var(--lumo-space-xs) / 2)");

        //Anchor goBack = new Anchor("#", arrowLeft);
        // header.add(goBack, editEmployee);
        //contain.add(header);
        FormLayout form = new FormLayout();
        form.add(jul, aug, sep,
                oct, nov, dec, jan,
                feb, mar,
                apr, may, jun);
        form.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new ResponsiveStep("320px", 2),
                // Use three columns, if the layout's width exceeds 500px
                new ResponsiveStep("500px", 3),
                new ResponsiveStep("700px", 4));

        form.getStyle().set("margin-bottom", "0px");
        Scroller scroller = new Scroller(
                new Div(editEmployee, form));
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "var(--lumo-space-m)");

        distrWorkplan.addSingleClickListener(v -> {
            clearWorkplan();

            //BigDecimal totalValueDstr = getNonNullValue(total).divide(BigDecimal.valueOf(12));
            BigDecimal totalValueDstr = getNonNullValue(total).divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP).stripTrailingZeros();
            BigDecimal sum = totalValueDstr.multiply(BigDecimal.valueOf(11)).stripTrailingZeros(); // Sum of first 11 divisions
            BigDecimal discrepancy = getNonNullValue(total).subtract(sum).stripTrailingZeros();
            //BigDecimal ch = totalValueDstr.multiply(BigDecimal.valueOf(12)).subtract(getNonNullValue(total)).abs();
            jan.setValue(totalValueDstr);
            feb.setValue(totalValueDstr);
            mar.setValue(totalValueDstr);
            apr.setValue(totalValueDstr);
            may.setValue(totalValueDstr);
            jun.setValue(discrepancy);
            jul.setValue(totalValueDstr);
            aug.setValue(totalValueDstr);
            sep.setValue(totalValueDstr);
            oct.setValue(totalValueDstr);
            nov.setValue(totalValueDstr);
            dec.setValue(totalValueDstr);
        });
        quarterWorkplan.addSingleClickListener(v -> {
            clearWorkplan();
            BigDecimal totalValueDstr = getNonNullValue(total).divide(BigDecimal.valueOf(4), 6, RoundingMode.HALF_UP).stripTrailingZeros();
            BigDecimal sum = totalValueDstr.multiply(BigDecimal.valueOf(3)).stripTrailingZeros();
            BigDecimal discrepancy = getNonNullValue(total).subtract(sum).stripTrailingZeros();
            mar.setValue(totalValueDstr);
            jun.setValue(discrepancy);
            sep.setValue(totalValueDstr);
            dec.setValue(totalValueDstr);
        });
        clearWorkplan.addSingleClickListener(v -> {

            clearWorkplan();
        });
        templateDownload.addSingleClickListener(v -> {

            exportAndDownloadUploadBudgetFileExcel();
        });
        // footer.add(distrWorkplan, quarterWorkplan, clearWorkplan);
        contain.add(scroller);
        return contain;
    }

    private void clearWorkplan() {
        jan.clear();
        feb.clear();
        mar.clear();
        apr.clear();
        may.clear();
        jun.clear();
        jul.clear();
        aug.clear();
        sep.clear();
        oct.clear();
        nov.clear();
        dec.clear();
    }

    private void clearBudget() {
        Item.clear();
        cost.clear();
        qty.clear();
        currency.clear();
        unitMeasure.clear();
        notes.clear();
        budgetItemfundSource.clear();
    }

    private void disabledBudget(boolean status) {
        Item.setEnabled(status);
        cost.setEnabled(status);
        qty.setEnabled(status);
        currency.setEnabled(status);
        unitMeasure.setEnabled(status);
        notes.setEnabled(status);
        jan.setEnabled(status);
        feb.setEnabled(status);
        mar.setEnabled(status);
        apr.setEnabled(status);
        may.setEnabled(status);
        jun.setEnabled(status);
        jul.setEnabled(status);
        aug.setEnabled(status);
        sep.setEnabled(status);
        oct.setEnabled(status);
        nov.setEnabled(status);
        dec.setEnabled(status);
        saveBudgetItem.setEnabled(status);
        deleteBudgetItem.setEnabled(status);
        distrWorkplan.setEnabled(status);
        quarterWorkplan.setEnabled(status);
        clearWorkplan.setEnabled(status);
        procClassCombo.setEnabled(status);
    }

    private VerticalLayout coaView() {
        VerticalLayout contain = new VerticalLayout();
        // Header
        Header header = new Header();
        header.getStyle().set("align-items", "center").set("height", "50px")
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("display", "flex").set("padding", "var(--lumo-space-m)");

        H5 editEmployee = new H5("Chart Of Accounts");
        editEmployee.getStyle().set("margin", "0");

        Icon arrowLeft = VaadinIcon.ARROW_LEFT.create();
        arrowLeft.setSize("var(--lumo-icon-size-m)");
        arrowLeft.getElement().setAttribute("aria-hidden", "true");
        arrowLeft.getStyle().set("box-sizing", "border-box")
                .set("margin-right", "var(--lumo-space-m)")
                .set("padding", "calc(var(--lumo-space-xs) / 2)");

        Anchor goBack = new Anchor("#", arrowLeft);

        header.add(goBack, editEmployee);
        //contain.add(header);
        contain.setAlignItems(FlexComponent.Alignment.STRETCH);
        contain.setHeight("100%");
        contain.setMaxWidth("100%");
        contain.setPadding(false);
        contain.setSpacing(false);
        contain.setWidthFull();

        searchCoa = new TextField();
        searchCoa.setPlaceholder("Search");
        searchCoa.setClearButtonVisible(true);
        searchCoa.setValueChangeMode(ValueChangeMode.EAGER);

        comboBoxCoalevel1 = new ComboBox<>();
        comboBoxCoalevel1Two = new ComboBox<>();
        searchCoa.addValueChangeListener(e -> {
            refreshgridCOA(e.getValue());
        });

        HorizontalLayout lay1 = new HorizontalLayout();
        lay1.setHeight("40px");
        lay1.add(comboBoxCoalevel1, searchCoa);

        comboBoxCoalevel1.setPlaceholder("Select COA Category");
        comboBoxCoalevel1.setItemLabelGenerator(Coalevel1::getName);

        comboBoxCoalevel1.addValueChangeListener(ev -> {
            chosenCoalevel1 = ev.getValue();
            //comboBoxCoalevel1Two.setValue(chosenCoalevel1);
            setBudgetDetails();
            refreshcomboCoalevel1Two();
            comboBoxCoalevel1Two.setValue(ev.getValue());
            if (comboBoxD_Section.isEmpty()) {
                Notificationwarning("No Section attached");

            } else {

                if (!comboBoxBudget.isEmpty()) {
                    gridCOA.setItems(coaService.findByDeptSectionAndCodeStartingWith(comboBoxD_Section.getValue(), Coalevel1String(ev.getValue()), comboBoxBudget.getValue()));
                    searchCoa.setValue(Coalevel1String(ev.getValue()));
                }

            }
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null && chosenUrc_Activities != null) {
                refreshgridBudgetItems();
            }
            if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null) {

                refreshgridBudgetItemCOA2();
            }
            refreshActivitiesSetingGrid2("");

            if (ev.getValue() != null) {
                if (ev.getValue().getCode() == 1) {
                    budgetItemfundSource.setEnabled(false);
                } else {
                    budgetItemfundSource.setEnabled(true);
                }
            }
        });
        gridCOA = new Grid<>(COA.class, false);
        // Configure Grid
        gridCOA.addColumn("code").setAutoWidth(true);
        gridCOA.addColumn("name").setAutoWidth(true);
        gridCOA.setHeightFull();
        gridCOA.getStyle().set("flex-grow", "1");
        gridCOA.asSingleSelect().addValueChangeListener(vl -> {
            if (vl.getValue() != null) {
                if (vl.getValue().getCode().trim().startsWith("2") || vl.getValue().getCode().trim().startsWith("3")) {
                    procClassCombo.setValue(vl.getValue().getProcclass());
                } else {
                    procClassCombo.setValue(ProcClass.Other);
                }
            }

            if (vl.getValue() != null) {
                if (vl.getValue().getDisplay() == Display.FREIGHT || vl.getValue().getDisplay() == Display.SALARIES) {
                    disabledBudget(false);
                } else {
                    disabledBudget(true);
                }
                chosenCOA = vl.getValue();
                setBudgetDetails();

                if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null) {
                    refreshgridBudgetItems();
                }
                refreshgridBudgetItemCOA2();
            }

        });

        VerticalLayout div = new VerticalLayout();
        div.setHeight("100%");
        div.add(lay1, gridCOA);
        Scroller scroller = new Scroller(div);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "var(--lumo-space-m)");
        scroller.setHeight("100%");
        contain.add(scroller);
        return contain;
    }

    private VerticalLayout budgetView() {
        MenuBar menuBar = new MenuBar();
        VerticalLayout contain = new VerticalLayout();
        contain.setAlignItems(FlexComponent.Alignment.STRETCH);
        /*        contain.setHeightFull();
        contain.setMaxWidth("100%");
        contain.setPadding(true);
        contain.setSpacing(true);
        contain.setWidthFull();*/
        //contain.getStyle().set("border-top", "1px solid var(--lumo-contrast-20pct)");

        // Header
        Header header = new Header();
        Footer footer = new Footer();
        header.getStyle().set("align-items", "center").set("height", "50px")
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("display", "flex").set("padding", "var(--lumo-space-m)");

        H5 editEmployee = new H5("Table of Budget Items");
        editEmployee.getStyle().set("margin", "0");

        Icon arrowLeft = VaadinIcon.ARROW_LEFT.create();
        arrowLeft.setSize("var(--lumo-icon-size-m)");
        arrowLeft.getElement().setAttribute("aria-hidden", "true");
        arrowLeft.getStyle().set("box-sizing", "border-box")
                .set("margin-right", "var(--lumo-space-m)")
                .set("padding", "calc(var(--lumo-space-xs) / 2)");

        Anchor goBack = new Anchor("#", arrowLeft);

        header.add(goBack, editEmployee);

        //contain.add(header);
        footer.getElement().getStyle().set("margin-left", "auto");
        calculateSumOfAllMonthstotalText.setWidth("200px");
        footer.add(calculateSumOfAllMonthstotalText);
        footer.getElement().getThemeList().add("badge success");
        Grid.Column<BudgetItems> codeColumn = gridBudgetItems
                .addColumn(budgetItem -> {
                    COA coacode = budgetItem.getCoacode();
                    Text label = new Text(coacode != null ? coacode.getCode() : "");
                    return label.getText(); // Get the text content
                })
                .setHeader("Code").setWidth("80px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getCoacode() != null ? budgetItem1.getCoacode().getName() : "";
                    String name2 = budgetItem2.getCoacode() != null ? budgetItem2.getCoacode().getName() : "";
                    return name1.compareTo(name2);
                });
        Grid.Column<BudgetItems> itemColumn = gridBudgetItems.addColumn(BudgetItems::getItem).setHeader("Description").setKey("Item");
        Grid.Column<BudgetItems> actcodeColumn = gridBudgetItems
                .addColumn(budgetItem -> {
                    //COA coacode = budgetItem.getCoacode();
                    Urc_Activities act = budgetItem.getActivity();
                    Text label = new Text(act != null ? act.getActivityCode() : "");
                    return label.getText(); // Get the text content
                })
                .setHeader("Activity Code").setWidth("150px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getActivity() != null ? budgetItem1.getActivity().getActivityCode() : "";
                    String name2 = budgetItem2.getActivity() != null ? budgetItem2.getActivity().getActivityCode() : "";
                    return name1.compareTo(name2);
                });

        Grid.Column<BudgetItems> totalColumn = gridBudgetItems.addColumn(budgetItem -> {
            BigDecimal total = generatesumofMonths(budgetItem);
            // Pattern to remove extra zeros
            return decimalFormat.format(total);
        }).setHeader("Total")
                .setKey("totalz")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = generatesumofMonths(budgetItem1);
                    BigDecimal total2 = generatesumofMonths(budgetItem2);
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);

        gridBudgetItems.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        gridBudgetItems.setWidthFull();
        BudgetItemsGridContextMenu contextMenu = new BudgetItemsGridContextMenu(gridBudgetItems);

        gridBudgetItems.asSingleSelect().addValueChangeListener(vl -> {
            if (vl.getValue() != null) {
                chosenCOA = vl.getValue().getCoacode();
                chosenUrc_Activities = vl.getValue().getActivity();
                chosenOrganisation = vl.getValue().getBudgetType();
                chosenCoalevel1 = vl.getValue().getCoalevel1();
                chosenBudget = vl.getValue().getBudget();
                chosenDsection = vl.getValue().getDeptUnit();
                populateBudgetItemForm(vl.getValue());
                setBudgetDetails();
            } else {
                // Handle the case when no item is selected (vl.getValue() is null)
                // You can clear/reset any fields or take appropriate action here.
            }
        });
        contain.add(editEmployee, gridBudgetItems, footer);
        return contain;
    }

    private static BigDecimal generatesumofMonths(BudgetItems budget) {
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

    private VerticalLayout budgetCoaView() {
        VerticalLayout contain = new VerticalLayout();
        contain.setAlignItems(FlexComponent.Alignment.STRETCH);

        comboBoxCoalevel1Two.setPlaceholder("Select COA Category");
        comboBoxCoalevel1Two.setItemLabelGenerator(Coalevel1::getName);

        Footer footer = new Footer();

        gridBudgetCoa.addColumn(budgetItem -> {
            COA coacode = budgetItem.getCoacode();
            Text label = new Text(coacode != null ? coacode.getCode() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Code").setWidth("80px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getCoacode() != null ? budgetItem1.getCoacode().getName() : "";
                    String name2 = budgetItem2.getCoacode() != null ? budgetItem2.getCoacode().getName() : "";
                    return name1.compareTo(name2);
                });
        gridBudgetCoa.addColumn(BudgetItems::getItem).setHeader("Description");
        gridBudgetCoa.addColumn(new ComponentRenderer<>(urcActivity -> {

            Span span = new Span(decimalFormat.format(urcActivity.getTotal()));
            span.getElement().getThemeList().add("badge success");

            return span;

        })).setHeader("Total").setFlexGrow(0).setWidth("150px");

        gridBudgetCoa.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        //sectionBudgetText.getElement().getThemeList().add("badge success");
        footer.getElement().getStyle().set("margin-right", "auto");
        sectionBudgetText.getStyle().set("margin-left", "auto");
        sectionBudgetText.setWidth("200px");
        footer.add(sectionBudgetText);
        sectionBudgetText.getElement().getThemeList().add("badge success");
        HorizontalLayout out = new HorizontalLayout();
        ComboBox<String> sett = new ComboBox();

        sett.setItems("View By Section", "View By Activity");
        sett.addValueChangeListener(e -> {
            viewSetting = e.getValue();

        });
        comboBoxCoalevel1Two.addValueChangeListener(e -> {
            refreshgridBudgetItemCOA2();
            if (e.getValue() != null) {
                if (e.getValue().getCode() == 1) {
                    budgetItemfundSource.setEnabled(false);
                } else {
                    budgetItemfundSource.setEnabled(true);
                }
            }

        });
        gridBudgetCoa.asSingleSelect().addValueChangeListener(e -> {
            // refreshgridBudgetItemCOA2();
            if (e.getValue() != null) {
                chosenCOA = e.getValue().getCoacode();
                BudgetItems bcoa = e.getValue();

                if (bcoa != null) {
                    COA coacode = bcoa.getCoacode();

                    if (coacode != null) {
                        if (coacode.getDisplay() == Display.FREIGHT || coacode.getDisplay() == Display.SALARIES) {
                            disabledBudget(false);
                        } else {
                            disabledBudget(true);
                        }
                        String code = coacode.getCode();
                        COA coa = coaService.findByCodeAndBudget(code, chosenBudget);

                        Coalevel1 coalnew = findCoalevel1(coa.getCode(), chosenBudget);
                        if (!sett.isEmpty() && !comboBoxCoalevel1Two.isEmpty()) {
                            if (sett.getValue().equals("View By Section") && !comboBoxCoalevel1Two.isEmpty()) {
                                refreshgridBudgetItems2(coalnew, coa);
                            } else if (sett.getValue().equals("View By Activity") && !comboBoxCoalevel1Two.isEmpty()) {
                                refreshgridBudgetItems3(coalnew, coa);
                            }
                        }

                    }
                }
            } else {

            }

        });

        out.add(comboBoxCoalevel1Two, sett);
        contain.add(new H5("Chat of Accounts"), out, gridBudgetCoa, sectionBudgetText);
        return contain;
    }

    private Dialog openActivityDialogue() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Urc Activities " + comboBoxBudget.getValue().getFinancialYear() + ", Budget:" + comboBoxOrganisation.getValue().getName());
        dialog.setWidth("90%");
        dialog.setHeight("90%");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        dialog.add(createDialogLayout());
        dialog.open();
        return dialog;
    }

    private Dialog createActivityDialogue(Urc_Activities act) {
        FormLayout detail = new FormLayout();
        Button saveButton = new Button("Save");
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Activity");
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        saveButton.addSingleClickListener(e -> {
            if (validEditorTextFieldsActivities().toString().length() > 0) {
                NotificationError(validEditorTextFieldsActivities().toString());
            } else {
                if (act != null) {
                    Urc_Activities data = act;
                    try {

                        if (data == null) {
                            data = new Urc_Activities();
                            data.setName(name.getValue());
                            data.setFundsource(fundsource.getValue());
                            data.setPerformanceIndicator(performanceIndicator.getValue());
                            data.setOutcome(outcome.getValue());
                            data.setObjective(objective.getValue());
                            data.setUrcPriorityAreas(comboBoxUrc_Priority_Areas.getValue());
                            data.setDeptSection(comboBoxD_Section.getValue());
                            data.setBudget(chosenBudget);
                            String maxActivityCode = sampleUrc_ActivitiesService.maxActivityCode(data.getDeptSection().getANL_CODE());
                            String nextActivityCode = "";
                            if (maxActivityCode != null) {
                                int nextActivityCodeNumber = Integer.parseInt(maxActivityCode.substring(4)) + 1;
                                nextActivityCode = data.getDeptSection().getANL_CODE().trim() + String.format("%07d", nextActivityCodeNumber);
                            } else {
                                nextActivityCode = data.getDeptSection().getANL_CODE().trim() + "0000001";
                            }

                            data.setActivityCode(nextActivityCode);

                            // data.setSections(sect);
                        } else {
                            data.setName(name.getValue());
                            data.setFundsource(fundsource.getValue());
                            data.setPerformanceIndicator(performanceIndicator.getValue());
                            data.setOutcome(outcome.getValue());
                            data.setObjective(objective.getValue());
                            data.setUrcPriorityAreas(comboBoxUrc_Priority_Areas.getValue());
                            data.setDeptSection(comboBoxD_Section.getValue());
                            data.setBudget(chosenBudget);

                        }
                    } catch (Exception validationException) {
                        NotificationError(" An exception happened while trying to saving. " + validationException.getMessage());
                    }
                    sampleUrc_ActivitiesService.update(data);
                    //sampleUrc_ActivitiesService.createUrcActivity(data, comboBoxD_Section.getValue().getANL_CODE());
                    refreshActivitiesGrid();
                    refreshActivitiesSetingGrid2("");
                    clearForm();
                    dialog.close();
                    UI.getCurrent().navigate(BudgetFormView.class);
                } else {
                    Notificationwarning("Select a Priority Area");
                }

            }
        });
        detail.add(name, fundsource, performanceIndicator, outcome, objective);
        dialog.open();
        return null;
    }

    private SplitLayout createDialogLayout() {
        VerticalLayout master = new VerticalLayout();
        FormLayout detail = new FormLayout();
        detail.setHeight("100%");
        master.setHeight("100%");

        gridUrc_ActivityDeatail = new Grid<>(Urc_Activities.class, false);
        gridUrc_ActivityDeatail.addColumn("name").setHeader("URC Activities").setAutoWidth(true);
        gridUrc_ActivityDeatail.addColumn("fundsource").setHeader("Fund Source").setAutoWidth(true);
        gridUrc_ActivityDeatail.addColumn("performanceIndicator").setHeader("Performance Indicator").setAutoWidth(true);
        gridUrc_ActivityDeatail.addColumn("outcome").setHeader("Outcome").setAutoWidth(true);
        gridUrc_ActivityDeatail.addColumn("objective").setHeader("Objective").setAutoWidth(true);
        gridUrc_ActivityDeatail.setHeight("100%");
        gridUrc_ActivityDeatail.addColumn(new ComponentRenderer<>(urcActivity -> {
            if (urcActivity.getDeptSection() != null) {
                Span span = new Span(urcActivity.getDeptSection().getNAME());
                if (isUserCreator(urcActivity.getDeptSection())) {
                    //span.getElement().setAttribute("theme", "success");
                    span.getElement().getThemeList().add("badge success");
                } else {
                    span.getElement().getThemeList().add("badge error");
                }
                return span;
            } else {
                return new Span(); // Return an empty Span if deptSection is null
            }
        })).setHeader("Section Creator").setFlexGrow(0).setWidth("150px");

        gridUrc_ActivityDeatail.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        comboBoxUrc_Priority_Areas = new ComboBox<>("Priority Areas");
        comboBoxUrc_Priority_Areas.setItemLabelGenerator(URC_Priority_Areas::getName);
        comboBoxUrc_Priority_Areas.setWidthFull();
        comboBoxUrc_Priority_Areas.setItems(sampleURC_Priority_AreasService.findByBudget(chosenBudget));

        binder.forField(name).asRequired("Urc Activity is Required").bind("name");
        binder.forField(fundsource).asRequired("Fund Source is Required").bind("fundsource");
        binder.forField(performanceIndicator).asRequired("Performance Indicator is Required").bind("performanceIndicator");
        binder.forField(outcome).asRequired("outcome is Required").bind("outcome");
        binder.forField(objective).asRequired("objective is Required").bind("objective");
        comboBoxUrc_Priority_Areas.addValueChangeListener(e -> {
            refreshActivitiesGrid();
            // gridUrc_ActivityDeatail.setItems(sampleUrc_ActivitiesService.findByUrcPriorityAreasAndDeptUnitIn(e.getValue(), user.getUnits().stream().toList()));
        });

        Footer footer = new Footer();
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel", e -> {
            saveButton.setEnabled(true);
            clearForm();
            gridUrc_ActivityDeatail.getSelectionModel().deselectAll();
        });

        gridUrc_ActivityDeatail.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                populateForm(e.getValue());
                if (isUserCreator(e.getValue().getDeptSection()) == true) {
                    saveButton.setEnabled(true);
                } else {
                    saveButton.setEnabled(false);
                }
            } else {
                populateForm(null);
            }

        });

        saveButton.addSingleClickListener(e -> {
            if (validEditorTextFieldsActivities().toString().length() > 0) {
                NotificationError(validEditorTextFieldsActivities().toString());
            } else {
                if (!comboBoxUrc_Priority_Areas.isEmpty()) {
                    Urc_Activities data = gridUrc_ActivityDeatail.asSingleSelect().getValue();
                    try {

                        if (data == null) {
                            data = new Urc_Activities();
                            data.setName(name.getValue());
                            data.setFundsource(fundsource.getValue());
                            data.setPerformanceIndicator(performanceIndicator.getValue());
                            data.setOutcome(outcome.getValue());
                            data.setObjective(objective.getValue());
                            data.setUrcPriorityAreas(comboBoxUrc_Priority_Areas.getValue());
                            data.setDeptSection(comboBoxD_Section.getValue());
                            data.setBudget(chosenBudget);

                            // data.setSections(sect);
                        } else {
                            data.setName(name.getValue());
                            data.setFundsource(fundsource.getValue());
                            data.setPerformanceIndicator(performanceIndicator.getValue());
                            data.setOutcome(outcome.getValue());
                            data.setObjective(objective.getValue());
                            data.setUrcPriorityAreas(comboBoxUrc_Priority_Areas.getValue());
                            data.setDeptSection(comboBoxD_Section.getValue());
                            data.setBudget(chosenBudget);

                        }
                    } catch (Exception validationException) {
                        NotificationError(" An exception happened while trying to saving. " + validationException.getMessage());
                    }
                    sampleUrc_ActivitiesService.createUrcActivity(data, comboBoxD_Section.getValue().getANL_CODE());
                    refreshActivitiesGrid();
                    refreshActivitiesSetingGrid2("");
                    clearForm();
                    UI.getCurrent().navigate(BudgetFormView.class);
                } else {
                    Notificationwarning("Select a Priority Area");
                }

            }
        });

        footer.add(saveButton, cancelButton);

        detail.add(name, fundsource, performanceIndicator, outcome, objective, footer);
        detail.setHeight("100%");
        gridUrc_ActivityDeatail.setHeight("100%");

        master.add(comboBoxUrc_Priority_Areas, gridUrc_ActivityDeatail);
        //master.add(comboBoxUrc_Priority_Areas);

        SplitLayout dialogLayout = new SplitLayout(master, detail);
        dialogLayout.setHeight("100%");

        //dialogLayout.getStyle().set("width", "90%").set("height", "90%");
        return dialogLayout;
    }

    private StringBuilder validEditorTextFieldsActivities() {
        StringBuilder build = new StringBuilder();
        if (name.isEmpty()) {
            build.append("Name field 1 is empty \n");

        }
        if (fundsource.isEmpty()) {
            build.append("Fund Source field 1 is empty \n");

        }
        if (performanceIndicator.isEmpty()) {
            build.append("Performance Indicator is empty \n");

        }
        if (outcome.isEmpty()) {
            build.append("Outcome is empty \n");

        }
        if (objective.isEmpty()) {
            build.append("Objective is empty \n");

        }
        return build;
    }

    private void refreshgridCOA(String search) {
        if (!comboBoxCoalevel1.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
            Set<UrcDeptSectionAnlDimbgt> sectionSet = new HashSet<>();
            sectionSet.add(comboBoxD_Section.getValue());

            gridCOA.setItems(coaService.findByDeptsectionAndCoalevel1AndBudgetAndSearchTerm(sectionSet, comboBoxCoalevel1.getValue(), comboBoxBudget.getValue(), search));
        }

    }

    private void refreshgridBudgetItemCOA() {

        if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty() && chosenCOA != null && chosenUrc_Activities != null) {
            gridBudgetCoa.setItems(budgetItemsService.findDistictCodeAndNames(chosenOrganisation, chosenBudget, chosenDsection, chosenCoalevel1, chosenUrc_Activities));
            //sectionBudgetText.setText(budgetItemsService.sumMonthsByActivityBySection(chosenCoalevel1, chosenOrganisation, chosenBudget, chosenUrc_Activities, chosenDsection)+"");
            sectionBudgetText.setText("Total:   " + decimalFormat.format(budgetItemsService.sumMonthsByActivityBySection(chosenCoalevel1, chosenOrganisation, chosenBudget, chosenUrc_Activities, chosenDsection)));
        }
    }

    private void refreshgridBudgetItemCOA2() {

        if (!comboBoxBudget.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1Two.isEmpty() && chosenCOA != null) {
            gridBudgetCoa.setItems(budgetItemsService.findDistictCodeAndNames2(chosenOrganisation, chosenBudget, chosenDsection, comboBoxCoalevel1Two.getValue()));
            //sectionBudgetText.setText(budgetItemsService.sumMonthsByActivityBySection(chosenCoalevel1, chosenOrganisation, chosenBudget, chosenUrc_Activities, chosenDsection)+"");
            sectionBudgetText.setText("Total:   " + decimalFormat.format(budgetItemsService.sumMonthsByActivityBySection2(comboBoxCoalevel1Two.getValue(), chosenOrganisation, chosenBudget, chosenDsection)));
        }
    }

    private void refreshgridBudgetItems() {
        if (chosenCoalevel1.getCode() == 1) {
            gridBudgetItems.setItems(budgetItemsService.findByCriteria(chosenCoalevel1, chosenOrganisation, chosenBudget, null, comboBoxD_Section.getValue(), gridCOA.asSingleSelect().getValue()));
        } else {
            gridBudgetItems.setItems(budgetItemsService.findByCriteria(chosenCoalevel1, chosenOrganisation, chosenBudget, gridUrc_Activities.asSingleSelect().getValue(), comboBoxD_Section.getValue(), gridCOA.asSingleSelect().getValue()));
        }

        calculateSumOfAllMonthstotal = calculateSumOfAllMonths();

        calculateSumOfAllMonthstotalText.setText("Total:   " + decimalFormat.format(calculateSumOfAllMonthstotal) + "");
    }

    private void refreshgridBudgetItems3(Coalevel1 coalevel1, COA coa) {

        gridBudgetItems.setItems(budgetItemsService.findByCriteria(coalevel1, chosenOrganisation, chosenBudget, gridUrc_Activities.asSingleSelect().getValue(), comboBoxD_Section.getValue(), coa));

        calculateSumOfAllMonthstotal = calculateSumOfAllMonths3(coalevel1, coa);

        //gridBudgetItems.prependFooterRow().getCell(gridBudgetItems.getColumnByKey("totalz")).setText(calculateSumOfAllMonthstotal+"");
        //gridBudgetItems.prependFooterRow().getCell(gridBudgetItems.getColumnByKey("Item")).setText("Total");
        //gridBudgetItems.prependFooterRow().getCell(gridBudgetItems.getColumnByKey("totalz")).setText(calculateSumOfAllMonthstotal+"");
        calculateSumOfAllMonthstotalText.setText("Total:   " + decimalFormat.format(calculateSumOfAllMonthstotal) + "");
    }

    private void refreshgridBudgetItems2(Coalevel1 coalevel1, COA coa) {

        gridBudgetItems.setItems(budgetItemsService.findByCriteria2(coalevel1, chosenOrganisation, chosenBudget, comboBoxD_Section.getValue(), coa));

        calculateSumOfAllMonthstotal = calculateSumOfAllMonths2(coalevel1, coa);

        //gridBudgetItems.prependFooterRow().getCell(gridBudgetItems.getColumnByKey("totalz")).setText(calculateSumOfAllMonthstotal+"");
        //gridBudgetItems.prependFooterRow().getCell(gridBudgetItems.getColumnByKey("Item")).setText("Total");
        //gridBudgetItems.prependFooterRow().getCell(gridBudgetItems.getColumnByKey("totalz")).setText(calculateSumOfAllMonthstotal+"");
        calculateSumOfAllMonthstotalText.setText("Total:   " + decimalFormat.format(calculateSumOfAllMonthstotal) + "");
    }

    private void refreshcomboCoalevel1() {
        if (!comboBoxBudget.isEmpty()) {
            comboBoxCoalevel1.setItems(coalevel1Service.findByBudget());
        }
    }

    private void refreshcomboCoalevel1Two() {
        if (!comboBoxBudget.isEmpty()) {
            comboBoxCoalevel1Two.setItems(coalevel1Service.findByBudget());
        }
    }

    private void refreshActivitiesGrid() {
        if (!comboBoxUrc_Priority_Areas.isEmpty()) {
            //dataViewUrc_ActivityDeatail = 
            gridUrc_ActivityDeatail.setItems(sampleUrc_ActivitiesService.findByUrcPriorityAreasAndBudgetAndDeptSectionIn(comboBoxUrc_Priority_Areas.getValue(), chosenBudget, user.getDeptsection().stream().toList()));
            Notification.show(sampleUrc_ActivitiesService.findByUrcPriorityAreasAndBudgetAndDeptSectionIn(comboBoxUrc_Priority_Areas.getValue(), chosenBudget, user.getDeptsection().stream().toList()).size() + " Items");
        }
    }

    private GridListDataView<Urc_Activities> refreshActivitiesSetingGrid() {
        if (!comboBoxBudget.isEmpty()) {
            // dataView = gridUrc_Activities.setItems(sampleUrc_ActivitiesService.findByBudgetSortedByDepartment(chosenBudget));
            //gridUrc_Activities.setItems(sampleUrc_ActivitiesService.findByDeptUnitsAndName(unitService.findBySection(comboBoxD_Unit.getValue().getSection()), ""));
        }
        return dataView;
    }

    private void refreshActivitiesSetingGrid2(String search) {

        if (!comboBoxBudget.isEmpty()) {

            gridUrc_Activities.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudgetAndSearch(comboBoxD_Section.getValue(), chosenBudget, search));
            if (chosenCoalevel1 != null) {
                calculateSumOfAllMonthstotalText2.setText("Total " + chosenCoalevel1.getName() + ": " + decimalFormat.format(budgetItemsService.sumMonthsBySection4(chosenCoalevel1, chosenOrganisation, chosenBudget, chosenDsection)) + "    |   " + "Total Expenditure: " + decimalFormat.format(budgetItemsService.sumMonthsBySectionTotalBudget(chosenOrganisation, chosenBudget, chosenDsection)));
            } else {
                calculateSumOfAllMonthstotalText2.setText("Total: " + decimalFormat.format(budgetItemsService.sumMonthsBySection4(chosenCoalevel1, chosenOrganisation, chosenBudget, chosenDsection)) + "    |   " + "Total Expenditure: " + decimalFormat.format(budgetItemsService.sumMonthsBySectionTotalBudget(chosenOrganisation, chosenBudget, chosenDsection)));
            }

        }

    }

    private void clearForm() {
        populateForm(null);
        //autogen.setEnabled(false);
    }

    private void populateForm(Urc_Activities object) {
        binder.readBean(object);

    }

    private void populateBudgetItemForm(BudgetItems object) {
        binderbudgetItem.readBean(object);

    }

    public Notification NotificationError(String error) {

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
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();

        notification.setPosition(Notification.Position.MIDDLE);

        return notification;
    }

    public Notification Notificationwarning(String warning) {

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                // new Text("Your session will expire in 5 minutes due to inactivity."),
                // new HtmlComponent("br"),
                new Text(warning)
        );

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

        notification.setPosition(Notification.Position.TOP_CENTER);

        return notification;
    }

    private static class ColumnToggleContextMenu extends ContextMenu {

        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<BudgetItems> column) {
            MenuItem menuItem = this.addItem(label, e -> {
                column.setVisible(e.getSource().isChecked());
            });
            menuItem.setCheckable(true);
            menuItem.setChecked(!column.isVisible());
        }
    }

    public String Coalevel1String(Coalevel1 coal) {
        if (coal != null) {
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
        } else {
            return "1";
        }

    }

    private class BudgetItemsGridContextMenu extends GridContextMenu<BudgetItems> {

        public BudgetItemsGridContextMenu(Grid<BudgetItems> target) {
            super(target);

            addItem("Change Account Code", e -> e.getItem().ifPresent(person -> {
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle("Change Account");
                Span span = new Span();
                span.setText("Change Budget Item " + person.getCoacode().getCode() + " To:");
                span.getElement().getThemeList().add("badge success");
                VerticalLayout dialogLayout = new VerticalLayout();
                ComboBox<Coalevel1> comboBoxCoalevel1Three = new ComboBox<>();
                comboBoxCoalevel1Three.setPlaceholder("Select COA Category this");
                comboBoxCoalevel1Three.setItemLabelGenerator(Coalevel1::getName);
                if (!comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                    comboBoxCoalevel1Three.setItems(coalevel1Service.findByBudget());

                }

                Grid<COA> gridCOA2 = new Grid<>(COA.class, false);
                gridCOA2.addColumn("code").setAutoWidth(true);
                gridCOA2.addColumn("name").setAutoWidth(true);
                gridCOA2.asSingleSelect().addValueChangeListener(vl -> {
                    span.setText("Change Budget Item " + person.getCoacode().getCode() + " To:" + vl.getValue().getCode());
                });
                comboBoxCoalevel1Three.addValueChangeListener(ev -> {
                    if (!comboBoxCoalevel1.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                        Set<UrcDeptSectionAnlDimbgt> sectionSet = new HashSet<>();
                        sectionSet.add(comboBoxD_Section.getValue());

                        gridCOA2.setItems(coaService.findBySectionsAndCodePrefixSearch(sectionSet, Coalevel1String(comboBoxCoalevel1Three.getValue()), searchCoa.getValue(), comboBoxBudget.getValue()));
                    }
                });
                FormLayout formLayout = new FormLayout();
                TextField searchCoa = new TextField();
                searchCoa.setPlaceholder("Search");
                searchCoa.setClearButtonVisible(true);
                searchCoa.setValueChangeMode(ValueChangeMode.EAGER);
                searchCoa.addValueChangeListener(ev -> {
                    if (!comboBoxCoalevel1.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                        Set<UrcDeptSectionAnlDimbgt> sectionSet = new HashSet<>();
                        sectionSet.add(comboBoxD_Section.getValue());
                        gridCOA2.setItems(coaService.findBySectionsAndCodePrefixSearch(sectionSet, Coalevel1String(comboBoxCoalevel1Three.getValue()), searchCoa.getValue(), comboBoxBudget.getValue()));
                        //gridCOA2.setItems(coaService.findBySectionsAndCodePrefixSearch2(sectionSet, searchCoa.getValue(), Coalevel1String(comboBoxCoalevel1Three.getValue())));
                    }
                });
                if (!comboBoxCoalevel1.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                    Set<UrcDeptSectionAnlDimbgt> sectionSet = new HashSet<>();
                    sectionSet.add(comboBoxD_Section.getValue());
                    gridCOA2.setItems(coaService.findBySectionsAndCodePrefixSearch(sectionSet, Coalevel1String(comboBoxCoalevel1Three.getValue()), searchCoa.getValue(), comboBoxBudget.getValue()));
                    // gridCOA2.setItems(coaService.findBySectionsAndCodePrefixSearch2(sectionSet, searchCoa.getValue(), Coalevel1String(comboBoxCoalevel1.getValue())));
                }
                formLayout.add(span, comboBoxCoalevel1Three, searchCoa);
                dialogLayout.add(formLayout, gridCOA2);
                Button saveButton = new Button("Change Account");
                saveButton.addClickListener(ev -> {
                    if (!gridCOA2.asSingleSelect().isEmpty()) {
                        person.setCoacode(gridCOA2.asSingleSelect().getValue());
                        budgetItemsService.update(person);
                        refreshgridBudgetItems();
                        dialog.close();
                    } else {
                        Notificationwarning("Select an Account");
                    }
                });
                Button cancelButton = new Button("Cancel", ev -> dialog.close());
                dialog.getFooter().add(cancelButton);
                dialog.getFooter().add(saveButton);
                dialog.add(dialogLayout);
                dialog.open();
            }));
            addItem("Change Budget Item Activity", e -> e.getItem().ifPresent(person -> {
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle("Change Activity");
                Span span = new Span();
                span.setText("Change Actvity " + person.getActivity().getName() + " To:");
                span.getElement().getThemeList().add("badge success");
                VerticalLayout dialogLayout = new VerticalLayout();
                Grid<Urc_Activities> gridUrc_Activities2 = new Grid<>(Urc_Activities.class, false);

                Grid.Column<Urc_Activities> name = gridUrc_Activities2.addColumn("name").setHeader("URC Activities").setAutoWidth(true);
                gridUrc_Activities2.addColumn(new ComponentRenderer<>(urcActivity -> {
                    Span span2 = new Span(urcActivity.getDeptSection().getNAME());

                    if (isUserCreator(urcActivity.getDeptSection())) {
                        span2.getElement().getThemeList().add("badge success");
                    } else {
                        span2.getElement().getThemeList().add("badge error");
                    }
                    return span2;
                })).setHeader("D_Unit Creator").setFlexGrow(0).setWidth("150px").setFooter("Footer");

                gridUrc_Activities2.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

                gridUrc_Activities2.asSingleSelect().addValueChangeListener(vl -> {
                    span.setText("Change Actvity " + person.getActivity().getName() + " To: " + vl.getValue().getName());
                });
                gridUrc_Activities2.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudgetAndSearch(comboBoxD_Section.getValue(), chosenBudget, ""));
                //gridUrc_Activities2.setItems(sampleUrc_ActivitiesService.findByDeptUnits(unitService.findBySection(comboBoxD_Unit.getValue().getSection())));
                FormLayout formLayout = new FormLayout();
                TextField searchCoa = new TextField();
                searchCoa.setPlaceholder("Search");
                searchCoa.setClearButtonVisible(true);
                searchCoa.setValueChangeMode(ValueChangeMode.EAGER);
                searchCoa.addValueChangeListener(ev -> {
                    gridUrc_Activities2.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudgetAndSearch(comboBoxD_Section.getValue(), chosenBudget, ev.getValue()));
                    //gridUrc_Activities2.setItems(sampleUrc_ActivitiesService.findByDeptUnitsAndName(unitService.findBySection(comboBoxD_Unit.getValue().getSection()), ev.getValue()));
                });

                formLayout.add(span, searchCoa);
                dialogLayout.add(formLayout, gridUrc_Activities2);
                Button saveButton = new Button("Change Activity");
                saveButton.addClickListener(ev -> {
                    if (!gridUrc_Activities2.asSingleSelect().isEmpty()) {
                        person.setActivity(gridUrc_Activities2.asSingleSelect().getValue());
                        budgetItemsService.update(person);
                        refreshgridBudgetItems();
                        dialog.close();
                    } else {
                        Notificationwarning("Select an Account");
                    }
                });
                Button cancelButton = new Button("Cancel", ev -> dialog.close());
                dialog.getFooter().add(cancelButton);
                dialog.getFooter().add(saveButton);
                dialog.add(dialogLayout);
                dialog.open();
            }));

            //add(new Hr());
            //GridMenuItem<BudgetItems> emailItem = 
            addItem("Change Budget Item Section",
                    e -> e.getItem().ifPresent(person -> {
                        Dialog dialog = new Dialog();
                        dialog.setHeaderTitle("Change Budget Item Section");
                        Span span = new Span();
                        span.setText("Change Budget Item Section " + person.getDeptUnit().getNAME() + " To:");
                        span.getElement().getThemeList().add("badge success");
                        VerticalLayout dialogLayout = new VerticalLayout();
                        Grid<UrcDeptSectionAnlDimbgt> gridD_Unit = new Grid<>(UrcDeptSectionAnlDimbgt.class, false);

                        Grid.Column<UrcDeptSectionAnlDimbgt> name = gridD_Unit.addColumn("NAME").setHeader("Section").setAutoWidth(true);

                        gridD_Unit.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

                        gridD_Unit.setItems(user.getDeptsection());

                        Grid<Urc_Activities> gridUrc_ActivitiesForChangeSection = new Grid<>(Urc_Activities.class, false);

                        Grid.Column<Urc_Activities> name2 = gridUrc_ActivitiesForChangeSection.addColumn("name").setHeader("URC Activities").setAutoWidth(true);

                        gridUrc_ActivitiesForChangeSection.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

                        gridUrc_ActivitiesForChangeSection.asSingleSelect().addValueChangeListener(vl -> {
                            // span.setText("Change Actvity " + person.getActivity().getName() + " To: " + vl.getValue().getName());

                        });
                        gridD_Unit.asSingleSelect().addValueChangeListener(vl -> {
                            span.setText("Change Budget Item Section " + person.getDeptUnit().getNAME() + " To: " + vl.getValue().getNAME());

                            if (!gridD_Unit.asSingleSelect().isEmpty()) {
                                //Notification.show("Changed: " + sampleUrc_ActivitiesService.findByDeptSectionAndBudgetAndSearch(gridD_Unit.asSingleSelect().getValue(), chosenBudget, "").size());
                                gridUrc_ActivitiesForChangeSection.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudgetAndSearch(gridD_Unit.asSingleSelect().getValue(), chosenBudget, ""));
                            }
                        });
                        // gridUrc_Activities2.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudgetAndSearch(gridD_Unit.asSingleSelect().getValue(), chosenBudget, ""));
                        FormLayout formLayout = new FormLayout();
                        // SplitLayout lay = new SplitLayout(gridD_Unit, null);
                        formLayout.add(span);
                        dialogLayout.add(formLayout, gridD_Unit, gridUrc_ActivitiesForChangeSection);
                        Button saveButton = new Button("Change Section");
                        saveButton.addClickListener(ev -> {
                            if (!gridD_Unit.asSingleSelect().isEmpty() && !gridUrc_ActivitiesForChangeSection.asSingleSelect().isEmpty()) {
                                person.setDeptUnit(gridD_Unit.asSingleSelect().getValue());
                                person.setActivity(gridUrc_ActivitiesForChangeSection.asSingleSelect().getValue());
                                budgetItemsService.update(person);
                                refreshgridBudgetItems();
                                dialog.close();
                            } else {
                                Notificationwarning("Select a Section and An Activity");
                            }
                        });
                        Button cancelButton = new Button("Cancel", ev -> dialog.close());
                        dialog.getFooter().add(cancelButton);
                        dialog.getFooter().add(saveButton);
                        dialog.add(dialogLayout);
                        dialog.open();
                    }));
            //GridMenuItem<BudgetItems> phoneItem = 
            addItem("Change Budget Item -Budget Type",
                    e -> e.getItem().ifPresent(person -> {
                        Dialog dialog = new Dialog();
                        dialog.setHeaderTitle("Change Budget Item -Budget Type");
                        Span span = new Span();
                        span.setText("Change Budget Item -Budget Type " + person.getDeptUnit().getNAME() + " To:");
                        span.getElement().getThemeList().add("badge success");
                        VerticalLayout dialogLayout = new VerticalLayout();
                        ComboBox<Organisation> comboBoxOrganisation = new ComboBox<>();
                        comboBoxOrganisation.setPlaceholder("Select Budget Type");
                        comboBoxOrganisation.setItemLabelGenerator(Organisation::getName);
                        if (!comboBoxBudget.isEmpty()) {
                            comboBoxOrganisation.setItems(organisationService.findByBudgetList(chosenBudget));

                        }
                        FormLayout formLayout = new FormLayout();

                        formLayout.add(span);
                        dialogLayout.add(span, comboBoxOrganisation);
                        Button saveButton = new Button("Change Budget Type");
                        saveButton.addClickListener(ev -> {
                            if (!comboBoxOrganisation.isEmpty()) {
                                person.setBudgetType(comboBoxOrganisation.getValue());
                                budgetItemsService.update(person);
                                refreshgridBudgetItems();
                                dialog.close();
                            } else {
                                Notificationwarning("Select an Account");
                            }
                        });
                        Button cancelButton = new Button("Cancel", ev -> dialog.close());
                        dialog.getFooter().add(cancelButton);
                        dialog.getFooter().add(saveButton);
                        dialog.add(dialogLayout);
                        dialog.open();
                    }));
            //add(new Hr());

            //GridMenuItem<BudgetItems> emailItem = 
            addItem("Extract Selected Unit Budget to Excel By Activity",
                    e -> e.getItem().ifPresent(person -> {
                        // List<BudgetItems> budgetItemsList = budgetItemsService.findBudgetItemsByCriteria2(chosenCoalevel1, chosenOrganisation, chosenBudget, chosenUrc_Activities, chosenDunit);
                        List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeLengthRange = sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();
                        // extractToExcelBudgetItems(budgetItemsList, findByAcntCodeStartingWithAndAcntCodeLengthRange);
                    }));
            addItem("Extract Full Unit Budget to Excel By Activity",
                    e -> e.getItem().ifPresent(person -> {
                        // List<BudgetItems> budgetItemsList = budgetItemsService.findBudgetItemsByCriteria3(chosenOrganisation, chosenBudget, chosenUrc_Activities, chosenDunit);
                        List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeLengthRange = sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();
                        // extractToExcelBudgetItemsByActivity(budgetItemsList, findByAcntCodeStartingWithAndAcntCodeLengthRange);

                    }));
        }
    }

    private void setStatus(String value) {
        status.setText("Status: " + value);
        status.setVisible(true);
    }

    private ConfirmDialog ConfirmDialogBasic() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        status = new Span();
        status.setVisible(false);

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Unsaved changes");
        dialog.setText(
                "There are unsaved changes. Do you want to discard or save them?");

        dialog.setCancelable(true);
        dialog.addCancelListener(event -> setStatus("Canceled"));

        dialog.setRejectable(true);
        dialog.setRejectText("Discard");
        dialog.addRejectListener(event -> setStatus("Discarded"));

        dialog.setConfirmText("Save");
        dialog.addConfirmListener(event -> setStatus("Saved"));

        Button button = new Button("Open confirm dialog");
        button.addClickListener(event -> {
            dialog.open();
            status.setVisible(false);
        });
        dialog.open();
        return dialog;
    }

    private void extractToExcelBudgetItems(List<BudgetItems> budgetItemsList, List<UR5_ACNT> ur5AcntList) {
        List<Integer> cols = new ArrayList();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(comboBoxD_Section.getValue().getNAME() + " " + comboBoxCoalevel1.getValue().getName() + " Activity Budget");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(false);
            // Create cell style for header row
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER); // Adjust alignment if needed
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Adjust alignment if needed            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            CellStyle accountingStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            accountingStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

// Create a CellStyle for cell(0) to fit content
            CellStyle fitContentStyle = workbook.createCellStyle();
            fitContentStyle.setAlignment(HorizontalAlignment.LEFT); // Adjust alignment if needed
            fitContentStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Adjust alignment if needed

// Create a CellStyle for cell(1) to wrap text
            CellStyle wrapTextStyle = workbook.createCellStyle();
            wrapTextStyle.setWrapText(true);

// Create a title row with a title cell
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(40); // Adjust the height as needed

            try {
// Add a logo
                FileInputStream fis = new FileInputStream("C:\\Users\\Methaltech\\Documents\\NetBeansProjects\\abc2\\src\\main\\resources\\META-INF\\resources\\images\\urclogo2.png");
                byte[] imageBytes = IOUtils.toByteArray(fis);
                int logoIndex = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG); // Replace 'yourLogoByteArray' with your logo image data
                CreationHelper helper = workbook.getCreationHelper();
                Drawing drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0); // Adjust the column number where you want to place the logo
                anchor.setRow1(0); // Adjust the row number where you want to place the logo
                Picture logo = drawing.createPicture(anchor, logoIndex);
                logo.resize();
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Image file not found: " + e.getMessage());
            }
// Create a cell for the title
            Cell titleCell = titleRow.createCell(1);
            //titleCell.setCellValue(new XSSFRichTextString("UGANDA RAILWAYS COOPERATION")); // Set your title text
            titleCell.setCellValue("UGANDA RAILWAYS COOPERATION");
            titleCell.setCellStyle(headerStyle); // Apply the header style to the title cell

// Create a title row with a title cell
            Row titleRow2 = sheet.createRow(1);
            titleRow2.setHeightInPoints(40); // Adjust the height as needed
            Cell titleCell2 = titleRow2.createCell(0);
            //titleCell.setCellValue(new XSSFRichTextString("UGANDA RAILWAYS COOPERATION")); // Set your title text

            //titleRow2.createCell(0).setCellValue(new XSSFRichTextString("UGANDA RAILWAYS COOPERATION"));
            titleRow2.createCell(0).setCellValue(titleSetup(fy.getText(), deptUnit.getText(), activity.getText(), fyType.getText()));
            titleCell2.setCellStyle(headerStyle); // Apply the header style to the title cell 
            titleCell2.setCellStyle(wrapTextStyle);

            // Create the header row
            Row headerRow = sheet.createRow(2);
            headerRow.createCell(0).setCellValue("Code");
            headerRow.createCell(1).setCellValue("Item");
            headerRow.createCell(2).setCellValue("Cost");
            headerRow.createCell(3).setCellValue("Qty");
            headerRow.createCell(4).setCellValue("Total");

            // Apply header style
            for (Cell cell : headerRow) {
                cell.setCellStyle(headerStyle);
            }

            // Filter and group budgetItems by COA
            Map<String, List<BudgetItems>> budgetItemsByCOA = budgetItemsList.stream().collect(Collectors.groupingBy(item -> item.getCoacode().getCode()));

            int rowNum = 3;

            for (UR5_ACNT ur5Acnt : ur5AcntList) {
                String acntCode = ur5Acnt.getAcntCode();
                String descr = ur5Acnt.getDescr();

                if (budgetItemsByCOA.containsKey(acntCode)) {
                    // Create subheading for each unique acntCode and descr combination
                    Row subheadingRow = sheet.createRow(rowNum++);
                    // subheadingRow.createCell(0).setCellValue(acntCode);
                    Cell cell0 = subheadingRow.createCell(0);
                    cell0.setCellValue(acntCode); // You can set the content of cell(0) as needed
                    cell0.setCellStyle(fitContentStyle);
                    subheadingRow.createCell(1).setCellValue(descr);

                    cols.add(rowNum - 1);
                    //sheet.addMergedRegion(cellRange);

                    subheadingRow.createCell(2);
                    subheadingRow.createCell(3);
                    subheadingRow.createCell(4);

                    // Apply style to the subheading row
                    CellStyle subheadingStyle = workbook.createCellStyle();
                    subheadingStyle.setAlignment(HorizontalAlignment.LEFT);
                    Font subheadingFont = workbook.createFont();
                    subheadingFont.setBold(true);
                    subheadingStyle.setFont(subheadingFont);
                    subheadingRow.getCell(0).setCellStyle(subheadingStyle);
                    subheadingRow.getCell(1).setCellStyle(subheadingStyle);

                    // Add the budget items under the subheading
                    List<BudgetItems> budgetItems = budgetItemsByCOA.get(acntCode);

                    for (BudgetItems budgetItem : budgetItems) {
                        Row row = sheet.createRow(rowNum++);

                        row.createCell(0);

                        Cell cell1 = row.createCell(1);
                        cell1.setCellValue(budgetItem.getItem());
                        cell1.setCellStyle(wrapTextStyle);

                        Cell formulaCell = row.createCell(2);
                        formulaCell.setCellValue(budgetItem.getCost().doubleValue());
                        formulaCell.setCellStyle(accountingStyle);

                        Cell formulaCel2 = row.createCell(3);
                        formulaCel2.setCellValue(budgetItem.getQty().doubleValue());
                        formulaCel2.setCellStyle(accountingStyle);

                        Cell formulaCel3 = row.createCell(4);
                        formulaCel3.setCellFormula("C" + (rowNum) + "*D" + (rowNum));
                        formulaCel3.setCellStyle(accountingStyle);
                    }
                }
            }

            CellRangeAddress cellRange2 = new CellRangeAddress(0, 0, 0, 4);
            CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 4);
            CellRangeAddress cellRange4 = new CellRangeAddress(1, 1, 0, 4);

            RegionUtil.setBorderBottom(BorderStyle.DASHED, cellRange2, sheet);
            sheet.addMergedRegion(cellRange3);
            sheet.addMergedRegion(cellRange4);
            for (Integer i : cols) {
                CellRangeAddress cellRange = new CellRangeAddress(i, i, 0, 4);
                RegionUtil.setBorderBottom(BorderStyle.THICK, cellRange, sheet);
            }
            // Auto-size columns
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(comboBoxD_Section.getValue().getNAME() + " " + comboBoxCoalevel1.getValue().getName() + " Activity Budget.xlsx", ()
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

    private void extractToExcelBudgetItemsByActivity(List<BudgetItems> budgetItemsList, List<UR5_ACNT> ur5AcntList) {
        try (Workbook workbook = new XSSFWorkbook()) {
            List<Integer> cols = new ArrayList();
            List<Integer> cols2 = new ArrayList();
            List<Integer> cols3 = new ArrayList();
            List<Integer> cols4 = new ArrayList();
            List<Integer> cols5 = new ArrayList();
            List<Integer> cols6 = new ArrayList();
            List<Integer> cols7 = new ArrayList();
            Sheet sheet = workbook.createSheet(comboBoxD_Section.getValue().getNAME() + " " + comboBoxCoalevel1.getValue().getName() + " Activity Budget");
            // Set the paper size to A4 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(false);

            // Create cell style for header row
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle accountingStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            accountingStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

            // Create a CellStyle for cell(0) to fit content
            CellStyle fitContentStyle = workbook.createCellStyle();
            fitContentStyle.setAlignment(HorizontalAlignment.LEFT);
            fitContentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle accountingStyle2 = workbook.createCellStyle();
            DataFormat dataFormat2 = workbook.createDataFormat();
            accountingStyle2.setDataFormat(dataFormat2.getFormat("#,##0.00;[Red]#,##0.00"));
            accountingStyle2.setFont(headerFont);

            // Create a CellStyle for cell(1) to wrap text
            CellStyle wrapTextStyle = workbook.createCellStyle();
            wrapTextStyle.setWrapText(true);

            int rowNum = 0; // Initialize row number

            // Add a logo
            try {
                FileInputStream fis = new FileInputStream("C:\\Users\\Methaltech\\Documents\\NetBeansProjects\\abc2\\src\\main\\resources\\META-INF\\resources\\images\\urclogo2.png");
                byte[] imageBytes = IOUtils.toByteArray(fis);
                int logoIndex = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
                CreationHelper helper = workbook.getCreationHelper();
                Drawing drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(0);

                // Calculate the aspect ratio to fit within the cell (adjust cell width and height as needed)
                double cellWidth = sheet.getColumnWidthInPixels(0); // Adjust the column as needed
                double cellHeight = 40.0; // Adjust the row height as needed
                Picture logo = drawing.createPicture(anchor, logoIndex);
                logo.resize();

                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Image file not found: " + e.getMessage());
            }

            // Create the title row
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(40);
            Cell titleCell = titleRow.createCell(1);
            titleCell.setCellValue("UGANDA RAILWAYS COOPERATION");
            titleCell.setCellStyle(headerStyle);
            // Create the header text row
            Row headerTextRow = sheet.createRow(rowNum++);
            headerTextRow.setHeightInPoints(40);
            Cell headerTextCell = headerTextRow.createCell(0);
            headerTextCell.setCellValue(new XSSFRichTextString(titleSetup(fy.getText(), deptUnit.getText(), activity.getText(), fyType.getText())));
            headerTextCell.setCellStyle(headerStyle);
            headerTextCell.setCellStyle(wrapTextStyle);

            // Create the header row
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Code");
            headerRow.createCell(1).setCellValue("Item");
            headerRow.createCell(2).setCellValue("Cost");
            headerRow.createCell(3).setCellValue("Qty");
            headerRow.createCell(4).setCellValue("Currency");
            headerRow.createCell(5).setCellValue("Total");

            // Apply header style
            for (Cell cell : headerRow) {
                cell.setCellStyle(headerStyle);
            }

            Map<String, List<BudgetItems>> budgetItemsByCOA = budgetItemsList.stream()
                    .collect(Collectors.groupingBy(item -> item.getCoacode().getCode().substring(0, 1)));

            for (String coaPrefix : budgetItemsByCOA.keySet()) {
                // Create a new header row for COA prefix
                Row coaHeaderRow = sheet.createRow(rowNum++);
                Cell coaHeaderCell = coaHeaderRow.createCell(0);

                // Determine the header text based on the prefix
                String headerText = "";
                if (coaPrefix.equals("1")) {
                    headerText = "Income";
                } else if (coaPrefix.equals("2")) {
                    headerText = "Revenue Expenses";
                } else if (coaPrefix.equals("3")) {
                    headerText = "Capital Expenditure";
                }
                coaHeaderCell.setCellValue(headerText);
                coaHeaderCell.setCellStyle(fitContentStyle);
                coaHeaderRow.createCell(1);
                coaHeaderRow.createCell(2);
                coaHeaderRow.createCell(3);
                coaHeaderRow.createCell(4);
                coaHeaderRow.createCell(5);
                cols3.add(rowNum - 1);
                // Apply style to the COA header row
                CellStyle coaHeaderStyle = workbook.createCellStyle();
                coaHeaderStyle.setAlignment(HorizontalAlignment.LEFT);
                Font coaHeaderFont = workbook.createFont();
                coaHeaderFont.setBold(true);
                coaHeaderStyle.setFont(coaHeaderFont);
                coaHeaderCell.setCellStyle(coaHeaderStyle);

                List<BudgetItems> budgetItems = budgetItemsByCOA.get(coaPrefix);

                // Group budgetItems by COA.code
                Map<String, List<BudgetItems>> budgetItemsByCOACode = budgetItems.stream()
                        .collect(Collectors.groupingBy(item -> item.getCoacode().getCode()));
                BigDecimal sums2 = BigDecimal.ZERO;
                for (String coaCode : budgetItemsByCOACode.keySet()) {
                    // Add a row for each COA.code group
                    Row coaCodeRow = sheet.createRow(rowNum++);
                    Cell coaCodeCell = coaCodeRow.createCell(0);
                    coaCodeCell.setCellValue(coaCode);
                    Cell coaCodeCell2 = coaCodeRow.createCell(1);
                    coaCodeCell2.setCellValue(sampleUR5_ACNTService.findByAcntCode(coaCode).getDescr());
                    // Set appropriate cell styles for the COA.code row
                    coaCodeRow.createCell(2);
                    coaCodeRow.createCell(3);
                    //coaCodeRow.createCell(4);
                    Cell coaCodeCel4 = coaCodeRow.createCell(4);
                    coaCodeCel4.setCellStyle(accountingStyle2);
                    coaCodeRow.createCell(5);
                    // ...
                    cols.add(rowNum - 1);
                    cols4.add(rowNum - 1);
                    cols5.add(rowNum - 1);
                    List<BudgetItems> budgetItemsGroup = budgetItemsByCOACode.get(coaCode);
                    BigDecimal sums = BigDecimal.ZERO;
                    for (BudgetItems budgetItem : budgetItemsGroup) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0);
                        Cell cell1 = row.createCell(1);
                        cell1.setCellValue(budgetItem.getItem());
                        cell1.setCellStyle(wrapTextStyle);

                        Cell formulaCell = row.createCell(2);
                        formulaCell.setCellValue(budgetItem.getCost().doubleValue());
                        formulaCell.setCellStyle(accountingStyle);

                        Cell formulaCel2 = row.createCell(3);
                        formulaCel2.setCellValue(budgetItem.getQty().doubleValue());
                        formulaCel2.setCellStyle(accountingStyle);

                        Cell formulaCel21 = row.createCell(4);
                        formulaCel21.setCellValue(budgetItem.getCurrency().getData().getCurrencyShort());
                        formulaCel21.setCellStyle(accountingStyle);

                        Cell formulaCel3 = row.createCell(5);
                        //formulaCel3.setCellFormula("C" + (rowNum) + "*D" + (rowNum));
                        formulaCel3.setCellValue(calculateMonthSum(budgetItem).doubleValue());
                        formulaCel3.setCellStyle(accountingStyle);
                        cols2.add(rowNum - 1);
                        sums = sums.add(calculateMonthSum(budgetItem));
                        sums2 = sums2.add(calculateMonthSum(budgetItem));
                        coaCodeCel4.setCellValue(sums.doubleValue());
                    }

                }
                Row row = sheet.createRow(rowNum++);
                Cell formulaCe0 = row.createCell(0);
                formulaCe0.setCellValue("TOTAL " + headerText.toUpperCase());
                formulaCe0.setCellStyle(headerStyle);
                row.createCell(1);
                row.createCell(2);
                row.createCell(3);
                // row.createCell(4).setCellValue(sums2.doubleValue());
                Cell formulaCe4 = row.createCell(4);
                formulaCe4.setCellValue(sums2.doubleValue());
                formulaCe4.setCellStyle(accountingStyle2);
                row.createCell(5);
                cols6.add(rowNum - 1);
                //row.setRowStyle(headerStyle);

                Row rownext = sheet.createRow(rowNum++);
                rownext.createCell(0);
                rownext.createCell(1);
                rownext.createCell(2);
                rownext.createCell(3);
                rownext.createCell(4);
                rownext.createCell(5);
            }

            CellRangeAddress cellRange2 = new CellRangeAddress(0, 0, 0, 5);
            CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 5);
            CellRangeAddress cellRange4 = new CellRangeAddress(1, 1, 0, 5);

            RegionUtil.setBorderBottom(BorderStyle.DASHED, cellRange2, sheet);
            sheet.addMergedRegion(cellRange3);
            sheet.addMergedRegion(cellRange4);

            for (Integer i : cols) {
                CellRangeAddress cellRange = new CellRangeAddress(i, i, 0, 5);
                RegionUtil.setBorderBottom(BorderStyle.THICK, cellRange, sheet);
            }
            for (Integer i : cols2) {
                CellRangeAddress cellRange = new CellRangeAddress(i, i, 0, 5);
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellRange, sheet);
            }
            for (Integer i : cols3) {
                CellRangeAddress cellRange = new CellRangeAddress(i, i, 0, 5);
                sheet.addMergedRegion(cellRange);
            }
            for (Integer i : cols4) {
                CellRangeAddress cellRange = new CellRangeAddress(i, i, 1, 3);
                sheet.addMergedRegion(cellRange);
            }
            for (Integer i : cols5) {
                CellRangeAddress cellRange = new CellRangeAddress(i, i, 4, 5);
                sheet.addMergedRegion(cellRange);
            }
            for (Integer i : cols6) {
                CellRangeAddress cellRange = new CellRangeAddress(i, i, 4, 5);
                sheet.addMergedRegion(cellRange);
                RegionUtil.setBorderBottom(BorderStyle.DOUBLE, cellRange, sheet);
                CellRangeAddress cellRange22 = new CellRangeAddress(i, i, 0, 3);
                sheet.addMergedRegion(cellRange22);
                RegionUtil.setBorderBottom(BorderStyle.DOUBLE, cellRange22, sheet);

            }

            // Auto-size columns
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            //sheet.setColumnWidth(0, 50);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(
                    comboBoxD_Section.getValue().getNAME() + " " + comboBoxCoalevel1.getValue().getName() + " Activity Budget.xlsx",
                    () -> new ByteArrayInputStream(outputStream.toByteArray())
            );

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

    private String titleSetup(String fy, String dunit, String activity, String budgetType) {

        return dunit + " " + fy + " " + activity + " " + budgetType;
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

    public Coalevel1 findCoalevel1(String code, Budget budget) {
        String income = "Income";
        String opex = "Operation Expenditure";
        String capex = "Capital Expenditure";
        String cats = "";

        List<Coalevel1> findByBudget = coalevel1Service.findByBudget();
        //System.out.println(findByBudget.size());
        Coalevel1 coalnew = null;
        for (Coalevel1 coal : findByBudget) {
            //System.out.println(code+"       "+coal.getName().trim()+"       "+budget.getFinancialYear());
            if (code.startsWith("1") && coal.getName().trim().equalsIgnoreCase(income)) {

                coalnew = coal;

            } else if (code.startsWith("2") && coal.getName().trim().equalsIgnoreCase(opex)) {

                coalnew = coal;
            } else if (code.startsWith("3") && coal.getName().trim().equalsIgnoreCase(capex)) {

                coalnew = coal;
            }
        }
        // System.out.println(code+"  "+coalnew.getName());
        return coalnew;
    }

    private void exportAndDownloadUploadBudgetFileExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget Upload Template");
            Sheet sheet2 = workbook.createSheet("Currencies & Unit Measures");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            Row headerRow = sheet.createRow(0);
            Row headerRow2 = sheet2.createRow(0);
            String[] headers = {
                "ACTIVITY CODE", "COA CODE", "BUDGET ITEM", "COST", "UNIT MEASURE",
                "QTY", "CUR", "NOTES", "EXPECTED TRAINER", "NO OF DAYS", "TARGET GROUP"
            };
            String[] headers2 = {
                "CURRENCIES", "UNIT MEASURES"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            String[] currencies = {"CURRENCIES", "UGX", "GBP", "EUR", "KES", "TZS", "USD"};
            String[] unitMeasures = {"UNIT MEASURES", "piece", "Len", "ton", "Box", "Bottle", "Can", "Each", "Gram",
                "Kilogram", "Litre", "Metre", "n/a", "Pad", "Packet", "Pair",
                "Ream", "Roll", "Set", "Sq Metre", "Tin", "x-x", "Cells??ea",
                "Stick", "Drum", "ctn", "dozen", "Jerrycan", "Ft", "Sheet",
                "Cubic metre", "tube", "inch", "sacket", "Wagon", "DAY", "EACH",
                "MONTH", "NIGHT", "Lumpsum", "Hours", "TONNE", "ACTIVITY UNIT", "SET",
                "LITRES", "WAGON", "CONTAINER", "TONNAGE", "SHIP", "DOCKING"};

// Create a single row for currencies
            Row currencyRow = sheet2.createRow(1); // Starting from row 2

            for (int i = 0; i < currencies.length; i++) {
                Cell cell = currencyRow.createCell(i); // Starting from column 26 (Position AA)
                cell.setCellValue(currencies[i]);
            }

// Create a single row for unit measures
            Row unitMeasureRow = sheet2.createRow(2); // Starting from row 3

            for (int i = 0; i < unitMeasures.length; i++) {
                Cell cell = unitMeasureRow.createCell(i); // Starting from column 27 (Position AB)
                cell.setCellValue(unitMeasures[i]);
            }

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Budget Upload Template.xlsx", ()
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

    //this form has the entire budget parameters
    private void exportAndDownloadUploadBudgetFileExcel2() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget Upload Template");
            Sheet sheet2 = workbook.createSheet("Currencies & Unit Measures");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            Row headerRow = sheet.createRow(0);
            Row headerRow2 = sheet2.createRow(0);
            String[] headers = {
                "ACTIVITY CODE", "COA CODE", "BUDGET ITEM", "COST", "UNIT MEASURE",
                "QTY", "CUR", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "JAN", "FEB", "MAR",
                "APR", "MAY", "JUN", "NOTES", "EXPECTED TRAINER", "NO OF DAYS", "TARGET GROUP"
            };
            String[] headers2 = {
                "CURRENCIES", "UNIT MEASURES"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            String[] currencies = {"CURRENCIES", "UGX", "GBP", "EUR", "KES", "TZS", "USD"};
            String[] unitMeasures = {"UNIT MEASURES", "piece", "Len", "ton", "Box", "Bottle", "Can", "Each", "Gram",
                "Kilogram", "Litre", "Metre", "n/a", "Pad", "Packet", "Pair",
                "Ream", "Roll", "Set", "Sq Metre", "Tin", "x-x", "Cells??ea",
                "Stick", "Drum", "ctn", "dozen", "Jerrycan", "Ft", "Sheet",
                "Cubic metre", "tube", "inch", "sacket", "Wagon", "DAY", "EACH",
                "MONTH", "NIGHT", "Lumpsum", "Hours", "TONNE", "ACTIVITY UNIT", "SET",
                "LITRES", "WAGON", "CONTAINER", "TONNAGE", "SHIP", "DOCKING"};

// Create a single row for currencies
            Row currencyRow = sheet2.createRow(1); // Starting from row 2

            for (int i = 0; i < currencies.length; i++) {
                Cell cell = currencyRow.createCell(i); // Starting from column 26 (Position AA)
                cell.setCellValue(currencies[i]);
            }

// Create a single row for unit measures
            Row unitMeasureRow = sheet2.createRow(2); // Starting from row 3

            for (int i = 0; i < unitMeasures.length; i++) {
                Cell cell = unitMeasureRow.createCell(i); // Starting from column 27 (Position AB)
                cell.setCellValue(unitMeasures[i]);
            }

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Budget Upload Template.xlsx", ()
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

    public Notification warningNotification(List<errorMessages> messages) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        Grid<errorMessages> errors = new Grid<>(errorMessages.class, false);
        errors.addColumn(errorMessages::getRow).setHeader("Row");
        errors.addColumn(errorMessages::getMessage).setHeader("Warning");
        errors.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        errors.getStyle().set("background-color", "#ffcc00");

        errors.setItems(messages);
        Div text = new Div(
                errors,
                new HtmlComponent("br"),
                new Text("Close this warning to continue working.")
        );

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }

    public boolean doesUrcDeptSectionAnlDimbgtExistInUser(
            Set<UrcDeptSectionAnlDimbgt> deptsectionUser,
            UrcDeptSectionAnlDimbgt urcDeptSectionAnlDimbgtToCheck) {

        if (deptsectionUser == null || urcDeptSectionAnlDimbgtToCheck == null) {
            return false;
        }

        for (UrcDeptSectionAnlDimbgt urcDeptSectionAnlDimbgt : deptsectionUser) {
            if (urcDeptSectionAnlDimbgt.getANL_CODE().equals(urcDeptSectionAnlDimbgtToCheck.getANL_CODE())) {
                return true;
            }
        }

        return false;
    }

    public void uploadSectionsBudget(InputStream inputStream) {
        if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty()) {

            try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
                int i = 0;
                for (Row row : sheet) {
                    i++;
                    BudgetItems budget = new BudgetItems();
                    budget.setBudget(comboBoxBudget.getValue());
                    budget.setBudgetType(comboBoxOrganisation.getValue());
                    if (i > 1) {
                        // Skip the first row
                        BigDecimal cost = BigDecimal.ZERO;
                        BigDecimal qty = BigDecimal.ZERO;
                        if (row != null) {

                            Currency cur = null;
                            Cell item = row.getCell(2);
                            budget.setDeptUnit(comboBoxD_Section.getValue());

                            if (item != null && item.getCellType() == CellType.STRING) {
                                String stringValue = item.getStringCellValue();
                                budget.setItem(stringValue);

                            }
                            Cell cell = row.getCell(0);

                            if (cell != null && cell.getCellType() == CellType.STRING) {
                                String stringValue = cell.getStringCellValue();
                                Urc_Activities urcPriorityArea = sampleUrc_ActivitiesService.findByActivityCodeAndBudget(stringValue, chosenBudget);
                                if (urcPriorityArea != null) {
                                    budget.setActivity(urcPriorityArea);
                                    // budget.setDeptUnit(urcPriorityArea.getDeptSection());
                                }

                            } else {
                                //budget.setDeptUnit(comboBoxD_Section.getValue()); 
                                // System.out.println(comboBoxD_Section.getValue().toString());
                            }

                            Cell cel2 = row.getCell(1);

                            if (cel2 != null && cel2.getCellType() == CellType.STRING) {
                                String stringValue = cel2.getStringCellValue();
                                COA coa = coaService.findByCodeAndBudget(stringValue, chosenBudget);
                                //COA coa = setCOA(stringValue, comboBoxBudget.getValue());

                                if (coa != null) {

                                    budget.setCoacode(coa);
                                    budget.setBcategory(coa.getCode());
                                    budget.setCoalevel1(coa.getCoalevel1());
                                }

                            } else if (cel2 != null && cel2.getCellType() == CellType.NUMERIC) {
                                String stringValue = String.valueOf((int) cel2.getNumericCellValue());
                                COA coa = coaService.findByCodeAndBudget(stringValue, chosenBudget);
                                //COA coa = setCOA(stringValue, comboBoxBudget.getValue());

                                if (coa != null) {
                                    budget.setCoacode(coa);
                                    budget.setBcategory(coa.getCode());
                                    budget.setCoalevel1(coa.getCoalevel1());
                                }

                            }
                            Cell cel3 = row.getCell(3);

                            if (cel3 != null && cel3.getCellType() == CellType.STRING) {
                                String stringValue = cel3.getStringCellValue().replace(",", "");
                                try {
                                    cost = new BigDecimal(stringValue);
                                    budget.setCost(cost);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            } else if (cel3 != null && cel3.getCellType() == CellType.NUMERIC) {
                                String stringValue = String.valueOf(cel3.getNumericCellValue()).replace(",", "");
                                try {
                                    cost = new BigDecimal(stringValue);
                                    budget.setCost(cost);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            }

                            Cell cel5 = row.getCell(5);

                            if (cel5 != null && cel5.getCellType() == CellType.STRING) {
                                String stringValue = cel5.getStringCellValue().replace(",", "");
                                try {
                                    qty = new BigDecimal(stringValue);
                                    budget.setQty(qty);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            } else if (cel5 != null && cel5.getCellType() == CellType.NUMERIC) {
                                String stringValue = String.valueOf(cel5.getNumericCellValue()).replace(",", "");
                                try {
                                    qty = new BigDecimal(stringValue);
                                    budget.setQty(qty);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            }

                            Cell cel4 = row.getCell(4);

                            if (cel4 != null && cel4.getCellType() == CellType.STRING) {
                                String unitmeasure = cel4.getStringCellValue();
                                List<StockUnitMeasure> getStockUnitMeasureByUnit = sampleStockUnitMeasureService.getStockUnitMeasureByUnit(unitmeasure);

                                if (!getStockUnitMeasureByUnit.isEmpty()) {
                                    budget.setUnitMeasure(unitmeasure);
                                }

                            }
                            Cell cel6 = row.getCell(6);

                            if (cel6 != null && cel6.getCellType() == CellType.STRING) {
                                String currency = cel6.getStringCellValue();
                                cur = sampleCurrencyService.findByDataCurrencyAndBudget(currency, chosenBudget);
                                budget.setCurrency(cur);

                                calculateAndSetMonthlyValues(budget, cur);

                                Cell dayno = row.getCell(7);

                                if (dayno != null && dayno.getCellType() == CellType.STRING) {
                                    String stringValue = dayno.getStringCellValue();
                                    budget.setNo_of_days(stringValue);

                                } else if (dayno != null && dayno.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(dayno.getNumericCellValue());
                                    budget.setNo_of_days(stringValue);

                                }
                                Cell targetGroup = row.getCell(8);

                                if (targetGroup != null && targetGroup.getCellType() == CellType.STRING) {
                                    String stringValue = targetGroup.getStringCellValue();
                                    budget.setTarget_group(stringValue);

                                } else if (targetGroup != null && targetGroup.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(targetGroup.getNumericCellValue());
                                    budget.setTarget_group(stringValue);

                                }
                                Cell trainer = row.getCell(9);

                                if (trainer != null && trainer.getCellType() == CellType.STRING) {
                                    String stringValue = trainer.getStringCellValue();
                                    budget.setExpected_trainer(stringValue);

                                } else if (trainer != null && trainer.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(trainer.getNumericCellValue());
                                    budget.setExpected_trainer(stringValue);

                                }
                                Cell notes = row.getCell(10);

                                if (notes != null && notes.getCellType() == CellType.STRING) {
                                    String stringValue = notes.getStringCellValue();
                                    budget.setNotes(stringValue);

                                } else if (notes != null && notes.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(notes.getNumericCellValue());
                                    budget.setNotes(stringValue);

                                }
                                budgetItemsService.update(budget);
                            }
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadSectionsBudget2(InputStream inputStream) {
        if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty()) {

            try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
                int i = 0;
                for (Row row : sheet) {
                    i++;
                    BudgetItems budget = new BudgetItems();
                    budget.setBudget(comboBoxBudget.getValue());
                    budget.setBudgetType(comboBoxOrganisation.getValue());
                    if (i > 1) {
                        // Skip the first row

                        if (row != null) {
                            BigDecimal jan = BigDecimal.ZERO;
                            BigDecimal may = BigDecimal.ZERO;
                            BigDecimal sep = BigDecimal.ZERO;
                            BigDecimal feb = BigDecimal.ZERO;
                            BigDecimal jun = BigDecimal.ZERO;
                            BigDecimal oct = BigDecimal.ZERO;
                            BigDecimal mar = BigDecimal.ZERO;
                            BigDecimal jul = BigDecimal.ZERO;
                            BigDecimal nov = BigDecimal.ZERO;
                            BigDecimal apr = BigDecimal.ZERO;
                            BigDecimal aug = BigDecimal.ZERO;
                            BigDecimal dec = BigDecimal.ZERO;
                            BigDecimal cost = BigDecimal.ZERO;
                            BigDecimal qty = BigDecimal.ZERO;
                            Currency cur = null;
                            Cell item = row.getCell(2);
                            budget.setDeptUnit(comboBoxD_Section.getValue());

                            if (item != null && item.getCellType() == CellType.STRING) {
                                String stringValue = item.getStringCellValue();
                                budget.setItem(stringValue);

                            }
                            Cell cell = row.getCell(0);

                            if (cell != null && cell.getCellType() == CellType.STRING) {
                                String stringValue = cell.getStringCellValue();
                                Urc_Activities urcPriorityArea = sampleUrc_ActivitiesService.findByActivityCodeAndBudget(stringValue, chosenBudget);
                                if (urcPriorityArea != null) {
                                    budget.setActivity(urcPriorityArea);
                                    // budget.setDeptUnit(urcPriorityArea.getDeptSection());
                                }

                            } else {
                                //budget.setDeptUnit(comboBoxD_Section.getValue()); 
                                // System.out.println(comboBoxD_Section.getValue().toString());
                            }

                            Cell cel2 = row.getCell(1);

                            if (cel2 != null && cel2.getCellType() == CellType.STRING) {
                                String stringValue = cel2.getStringCellValue();
                                COA coa = coaService.findByCodeAndBudget(stringValue, chosenBudget);
                                //COA coa = setCOA(stringValue, comboBoxBudget.getValue());

                                if (coa != null) {

                                    budget.setCoacode(coa);
                                    budget.setBcategory(coa.getCode());
                                    budget.setCoalevel1(coa.getCoalevel1());
                                }

                            } else if (cel2 != null && cel2.getCellType() == CellType.NUMERIC) {
                                String stringValue = String.valueOf((int) cel2.getNumericCellValue());
                                COA coa = coaService.findByCodeAndBudget(stringValue, chosenBudget);
                                //COA coa = setCOA(stringValue, comboBoxBudget.getValue());

                                if (coa != null) {
                                    budget.setCoacode(coa);
                                    budget.setBcategory(coa.getCode());
                                    budget.setCoalevel1(coa.getCoalevel1());
                                }

                            }
                            Cell cel3 = row.getCell(3);

                            if (cel3 != null && cel3.getCellType() == CellType.STRING) {
                                String stringValue = cel3.getStringCellValue().replace(",", "");
                                try {
                                    cost = new BigDecimal(stringValue);
                                    budget.setCost(cost);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            } else if (cel3 != null && cel3.getCellType() == CellType.NUMERIC) {
                                String stringValue = String.valueOf(cel3.getNumericCellValue()).replace(",", "");
                                try {
                                    cost = new BigDecimal(stringValue);
                                    budget.setCost(cost);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            }

                            Cell cel4 = row.getCell(4);

                            if (cel4 != null && cel4.getCellType() == CellType.STRING) {
                                String stringValue = cel4.getStringCellValue().replace(",", "");
                                try {
                                    qty = new BigDecimal(stringValue);
                                    budget.setQty(qty);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            } else if (cel4 != null && cel4.getCellType() == CellType.NUMERIC) {
                                String stringValue = String.valueOf(cel4.getNumericCellValue()).replace(",", "");
                                try {
                                    qty = new BigDecimal(stringValue);
                                    budget.setQty(qty);
                                    // Your code here
                                } catch (NumberFormatException e) {

                                }

                            }

                            Cell cel5 = row.getCell(5);

                            if (cel5 != null && cel5.getCellType() == CellType.STRING) {
                                String unitmeasure = cel5.getStringCellValue();
                                List<StockUnitMeasure> getStockUnitMeasureByUnit = sampleStockUnitMeasureService.getStockUnitMeasureByUnit(unitmeasure);

                                if (!getStockUnitMeasureByUnit.isEmpty()) {
                                    budget.setUnitMeasure(unitmeasure);
                                }

                            }
                            Cell cel6 = row.getCell(6);

                            if (cel6 != null && cel6.getCellType() == CellType.STRING) {
                                String currency = cel6.getStringCellValue();
                                cur = sampleCurrencyService.findByDataCurrencyAndBudget(currency, chosenBudget);
                                budget.setCurrency(cur);

                                //calculateAndSetMonthlyValues(budget);                              
                                Cell cel7 = row.getCell(7);

                                if (cel7 != null && cel7.getCellType() == CellType.STRING) {
                                    String stringValue = cel7.getStringCellValue().replace(",", "");
                                    try {
                                        jul = new BigDecimal(stringValue);
                                        budget.setJul(jul);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel7 != null && cel7.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel7.getNumericCellValue()).replace(",", "");
                                    try {
                                        jul = new BigDecimal(stringValue);
                                        budget.setJul(jul);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setJul(jul);
                                }
                                Cell cel8 = row.getCell(8);

                                if (cel8 != null && cel8.getCellType() == CellType.STRING) {
                                    String stringValue = cel8.getStringCellValue().replace(",", "");
                                    try {

                                        aug = new BigDecimal(stringValue);
                                        budget.setAug(aug);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel8 != null && cel8.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel8.getNumericCellValue()).replace(",", "");
                                    try {

                                        aug = new BigDecimal(stringValue);
                                        budget.setAug(aug);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setAug(aug);
                                }
                                Cell cel9 = row.getCell(9);

                                if (cel9 != null && cel9.getCellType() == CellType.STRING) {
                                    String stringValue = cel9.getStringCellValue().replace(",", "");
                                    try {
                                        sep = new BigDecimal(stringValue);
                                        budget.setSep(sep);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel9 != null && cel9.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel9.getNumericCellValue()).replace(",", "");
                                    try {
                                        sep = new BigDecimal(stringValue);
                                        budget.setSep(sep);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setSep(sep);
                                }
                                Cell cel10 = row.getCell(10);

                                if (cel10 != null && cel10.getCellType() == CellType.STRING) {
                                    String stringValue = cel10.getStringCellValue().replace(",", "");
                                    try {
                                        oct = new BigDecimal(stringValue);
                                        budget.setOct(oct);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel10 != null && cel10.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel10.getNumericCellValue()).replace(",", "");
                                    try {
                                        oct = new BigDecimal(stringValue);
                                        budget.setOct(oct);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setOct(oct);
                                }
                                Cell cel11 = row.getCell(11);

                                if (cel11 != null && cel11.getCellType() == CellType.STRING) {
                                    String stringValue = cel11.getStringCellValue().replace(",", "");
                                    try {
                                        nov = new BigDecimal(stringValue);
                                        budget.setNov(nov);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel11 != null && cel11.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel11.getNumericCellValue()).replace(",", "");
                                    try {
                                        nov = new BigDecimal(stringValue);
                                        budget.setNov(nov);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setNov(nov);
                                }

                                Cell cel12 = row.getCell(12);

                                if (cel12 != null && cel12.getCellType() == CellType.STRING) {
                                    String stringValue = cel12.getStringCellValue().replace(",", "");
                                    try {
                                        dec = new BigDecimal(stringValue);
                                        budget.setDec(dec);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel12 != null && cel12.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel12.getNumericCellValue()).replace(",", "");
                                    try {
                                        dec = new BigDecimal(stringValue);
                                        budget.setDec(dec);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setDec(dec);
                                }
                                Cell cel13 = row.getCell(13);

                                if (cel13 != null && cel13.getCellType() == CellType.STRING) {
                                    String stringValue = cel13.getStringCellValue().replace(",", "");
                                    try {
                                        jan = new BigDecimal(stringValue);
                                        budget.setJan(jan);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel13 != null && cel13.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel13.getNumericCellValue()).replace(",", "");
                                    try {
                                        jan = new BigDecimal(stringValue);
                                        budget.setJan(jan);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setJan(jan);
                                }
                                Cell cel14 = row.getCell(14);

                                if (cel14 != null && cel14.getCellType() == CellType.STRING) {
                                    String stringValue = cel14.getStringCellValue().replace(",", "");
                                    try {
                                        feb = new BigDecimal(stringValue);
                                        budget.setFeb(feb);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel14 != null && cel14.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel14.getNumericCellValue()).replace(",", "");
                                    try {
                                        feb = new BigDecimal(stringValue);
                                        budget.setFeb(feb);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setFeb(feb);
                                }
                                Cell cel15 = row.getCell(15);

                                if (cel15 != null && cel15.getCellType() == CellType.STRING) {
                                    String stringValue = cel15.getStringCellValue().replace(",", "");
                                    try {
                                        mar = new BigDecimal(stringValue);
                                        budget.setMar(mar);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel15 != null && cel15.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel15.getNumericCellValue()).replace(",", "");
                                    try {
                                        mar = new BigDecimal(stringValue);
                                        budget.setMar(mar);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setMar(mar);
                                }
                                Cell cel16 = row.getCell(16);

                                if (cel16 != null && cel16.getCellType() == CellType.STRING) {
                                    String stringValue = cel16.getStringCellValue().replace(",", "");
                                    try {
                                        apr = new BigDecimal(stringValue);
                                        budget.setApr(apr);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel16 != null && cel16.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel16.getNumericCellValue()).replace(",", "");
                                    try {
                                        apr = new BigDecimal(stringValue);
                                        budget.setApr(apr);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setApr(apr);
                                }
                                Cell cel17 = row.getCell(17);

                                if (cel17 != null && cel17.getCellType() == CellType.STRING) {
                                    String stringValue = cel17.getStringCellValue().replace(",", "");
                                    try {
                                        may = new BigDecimal(stringValue);
                                        budget.setMay(may);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel17 != null && cel17.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel17.getNumericCellValue()).replace(",", "");
                                    try {
                                        may = new BigDecimal(stringValue);
                                        budget.setMay(may);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setMay(may);
                                }
                                Cell cel18 = row.getCell(18);

                                if (cel18 != null && cel18.getCellType() == CellType.STRING) {
                                    String stringValue = cel18.getStringCellValue().replace(",", "");
                                    try {
                                        jun = new BigDecimal(stringValue);
                                        budget.setJun(jun);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else if (cel18 != null && cel18.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(cel18.getNumericCellValue()).replace(",", "");
                                    try {
                                        jun = new BigDecimal(stringValue);
                                        budget.setJun(jun);
                                        // Your code here
                                    } catch (NumberFormatException e) {

                                    }

                                } else {
                                    budget.setJun(jun);
                                }
                                Cell dayno = row.getCell(19);

                                if (dayno != null && dayno.getCellType() == CellType.STRING) {
                                    String stringValue = dayno.getStringCellValue();
                                    budget.setNo_of_days(stringValue);

                                } else if (dayno != null && dayno.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(dayno.getNumericCellValue());
                                    budget.setNo_of_days(stringValue);

                                }
                                Cell targetGroup = row.getCell(20);

                                if (targetGroup != null && targetGroup.getCellType() == CellType.STRING) {
                                    String stringValue = targetGroup.getStringCellValue();
                                    budget.setTarget_group(stringValue);

                                } else if (targetGroup != null && targetGroup.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(targetGroup.getNumericCellValue());
                                    budget.setTarget_group(stringValue);

                                }
                                Cell trainer = row.getCell(20);

                                if (trainer != null && trainer.getCellType() == CellType.STRING) {
                                    String stringValue = trainer.getStringCellValue();
                                    budget.setExpected_trainer(stringValue);

                                } else if (trainer != null && trainer.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(trainer.getNumericCellValue());
                                    budget.setExpected_trainer(stringValue);

                                }
                                Cell notes = row.getCell(21);

                                if (notes != null && notes.getCellType() == CellType.STRING) {
                                    String stringValue = notes.getStringCellValue();
                                    budget.setNotes(stringValue);

                                } else if (notes != null && notes.getCellType() == CellType.NUMERIC) {
                                    String stringValue = String.valueOf(notes.getNumericCellValue());
                                    budget.setNotes(stringValue);

                                }
                                budgetItemsService.update(budget);
                            }
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public COA setCOA(String codez, Budget budget) {
        COA coa = null;
        List<COAReconcile> code = coaReconcileService.findCOAReconcileByOldcoa(codez);
        for (COAReconcile b : code) {
            //System.out.println(b.getNewcoa() + ": Item new " + b.getOldcoa() + ": Old Item");
            coa = coaService.findByCodeAndBudget(b.getNewcoa(), budget);
            // System.out.println(coa.getCode() + " Item new");
        }

        return coa;
    }

    public salaryScale GetScale(String s) {
        salaryScale ba = null;

        if (s.equals("RG 1")) {
            ba = salaryScale.RG_1;
        } else if (s.equals("RG 2")) {
            ba = salaryScale.RG_2;
        } else if (s.equals("RG 3")) {
            ba = salaryScale.RG_3;
        } else if (s.equals("RG 4")) {
            ba = salaryScale.RG_4;
        } else if (s.equals("RG 5")) {
            ba = salaryScale.RG_5;
        } else if (s.equals("RG 6")) {
            ba = salaryScale.RG_6;
        } else if (s.equals("RG 7")) {
            ba = salaryScale.RG_7;
        } else if (s.equals("RG 8")) {
            ba = salaryScale.RG_8;
        } else if (s.equals("RG 9")) {
            ba = salaryScale.RG_9;
        } else if (s.equals("EXEC 1")) {
            ba = salaryScale.EXEC_1;
        } else if (s.equals("EXEC 2")) {
            ba = salaryScale.EXEC_2;
        }

        return ba;
    }

    public void calculateAndSetMonthlyValues(BudgetItems budget, Currency cur) {
        // Check if cost and qty are not null
        if (budget.getCost() != null && budget.getQty() != null) {
            // Calculate total by multiplying cost and qty
            BigDecimal total = budget.getCost().multiply(budget.getQty().multiply(cur.getRate()));

            // Define the scale and rounding mode
            int scale = 6; // Set the desired scale
            RoundingMode roundingMode = RoundingMode.HALF_UP; // Choose a rounding mode that suits your needs

            // Divide the total by 12 to get the monthly value with consistent scale and rounding
            BigDecimal monthlyValue = total.divide(BigDecimal.valueOf(12), scale, roundingMode);

            // Set monthly values for each month
            budget.setJan(monthlyValue);
            budget.setFeb(monthlyValue);
            budget.setMar(monthlyValue);
            budget.setApr(monthlyValue);
            budget.setMay(monthlyValue);

            budget.setJul(monthlyValue);
            budget.setAug(monthlyValue);
            budget.setSep(monthlyValue);
            budget.setOct(monthlyValue);
            budget.setNov(monthlyValue);
            budget.setDec(monthlyValue);
            // Set June as the sum of monthlyValue and the difference between monthlyValue * 12 and total
            BigDecimal juneValue = total.subtract(monthlyValue.multiply(BigDecimal.valueOf(11)));
            budget.setJun(juneValue);

            // Set the total value with consistent scale and rounding
            // budget.setTotal(total.setScale(scale, roundingMode));
        } else {
            // Handle the case where cost or qty is null
            System.out.println("Cannot calculate monthly values. Cost or qty is null.");
        }
    }

}
