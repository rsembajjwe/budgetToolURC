package com.methaltech.application.views.budgetReport;

import com.itextpdf.io.font.constants.StandardFonts;
import com.methaltech.application.data.Display;
import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.Qtr;
import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.bgtool.repository.BudgetRepository;
import com.methaltech.application.data.bgtool.repository.SamplePersonService;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CustomDetailedBudgetReportImpService;
import com.methaltech.application.data.bgtool.service.CustomDetailedBudgetReportService;
import com.methaltech.application.data.bgtool.service.FreightVolumesService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReport;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReportImp;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.SamplePerson;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.entity.livedata.URC_ACNT;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.data.livedata.service.UrcAcntService;
import com.methaltech.application.data.livedata.service.UrcBSalfldgService;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.awt.image.BufferedImage;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.methaltech.application.data.Classification2;
import com.methaltech.application.data.Classification3;
import com.methaltech.application.data.GetPeriods;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.FreightActualVolumesService;
import com.methaltech.application.data.bgtool.service.PassengerActualVolumesService;
import com.methaltech.application.data.bgtool.service.PerformanceContextBuilder;
import com.methaltech.application.data.bgtool.service.PerformanceExcelExportService;
import com.methaltech.application.data.bgtool.service.PerformancePdfExportService;
import com.methaltech.application.data.bgtool.service.QtrReleasesReportService;
import com.methaltech.application.data.bgtool.service.QtrReleasesService;
import com.methaltech.application.data.bgtool.service.QtrReleasesServiceImpl;
import com.methaltech.application.data.bgtool.service.SectionBudgetPerformanceService;
import com.methaltech.application.data.entity.bgtool.FreightQuarterTotalsDTO;
import com.methaltech.application.data.entity.bgtool.PerformanceReportContext;
import com.methaltech.application.data.entity.bgtool.PerformanceRow;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import com.methaltech.application.data.entity.bgtool.QtrReleases;
import com.methaltech.application.data.entity.bgtool.QuarterBudgetSum;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.dto.PassengerQuarterTotalsDTO;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import java.util.Objects;
// iText image classes
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.methaltech.application.data.Classification1;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.bgtool.service.BudgetWordReportService;
import com.methaltech.application.data.bgtool.service.DepartmentWorkplanWordExportService;
import com.methaltech.application.data.bgtool.service.SystemIconService;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.SystemIcon;
import com.methaltech.application.data.entity.bgtool.dto.PerformanceData;
import java.io.IOException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;

@PageTitle("Budget Reports Downloads")
@Route(value = "budgetReport", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO", "USER", "HOD"})
public class BudgetReportsView extends Div {

    PeriodExtractor extActuals = new PeriodExtractor();
    private final ComboBox<Budget> comboBox = new ComboBox<>("Select Budget");
    private final ComboBox<Budget> comboBox2 = new ComboBox<>("Select Budget");
    private final UserService userService;
    private final UrcAcntService urcAcntService;
    private final Coalevel1Service sampleCoalevel1Service;
    private final BudgetWordReportService budgetWordReportService;
    private final CoaService sampleCoaService;
    private final FreightVolumesService sampleFreightVolumesService;
    private final BudgetItemsService sampleBudgetItemsService;
    private final CustomDetailedBudgetReportImpService sampleCustomDetailedBudgetReportImpService;
    private final CustomDetailedBudgetReportService sampleCustomDetailedBudgetReportService;
    private final OrganisationService sampleOrganisationService;
    private final BudgetService budgetService;
    private final SamplePersonService samplePersonService;
    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final SALFLDGService samopleSALFLDGService;
    private final UrcBSalfldgService sampleUrcBSalfldgService;
    private final QtrReleasesService qtrReleasesService;
    private final QtrReleasesReportService qtrReleasesReport;
    private final List<SamplePerson> person = new ArrayList<>();
    private List<URC_Priority_Areas> programmes = new ArrayList<>();
    private List<Urc_Activities> programmesActivities = new ArrayList<>();
    private List<BudgetItems> budgetList = new ArrayList<>();
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sections = new MultiSelectComboBox<>("Sections");
    private MultiSelectComboBox<Organisation> budgetType = new MultiSelectComboBox<>("Budget Type");
    private MultiSelectComboBox<Organisation> budgetType2 = new MultiSelectComboBox<>("Budget Type");
    private User user;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    //List<Report> reportColumns = new ArrayList<>();
    List<CustomDetailedBudgetReportImp> gridCustomDetailedBudgetReportImpItems = new ArrayList<>();
    //private MultiSelectComboBox<Report> reportColumnsCombo = new MultiSelectComboBox<>("Report Columns");
    //private MultiSelectComboBox<Report> reportColumnsCombo2 = new MultiSelectComboBox<>("Report Columns");
    private CustomDetailedBudgetReportImp sampleCustomDetailedBudgetReportImp = new CustomDetailedBudgetReportImp();
    private Grid<CustomDetailedBudgetReportImp> gridCustomDetailedBudgetReportImp = new Grid<>(CustomDetailedBudgetReportImp.class, false);
    private final ComboBox<CustomDetailedBudgetReportImp> CustomDetailedBudgetReportImpcomboBox = new ComboBox<>("Report Name");
    private Grid<CustomDetailedBudgetReport> gridCustomDetailedBudgetReport = new Grid<>(CustomDetailedBudgetReport.class, false);
    private TextField CustomDetailedBudgetReporttextField = new TextField();
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> CustomDetailedBudgetReportcombo = new MultiSelectComboBox<>("Report Columns");
    private final BudgetRepository repository;
    VerticalLayout dataLayout = new VerticalLayout();
    HorizontalLayout datasubLayout = new HorizontalLayout();
    Grid<Organisation> fundSourceGrid = new Grid<>(Organisation.class, false);
    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExp = BigDecimal.ZERO;
    private final SectionBudgetPerformanceService sectionBudgetPerformanceService;
    int qtr = 0;
    int qtr2 = 0;
    BigDecimal cumRealise = BigDecimal.ZERO;
    BigDecimal actualRealise = BigDecimal.ZERO;
    List<PriorityArea> priorityAreas = new ArrayList<>();

    //private final ComboBox<Budget> budgetCombo = new ComboBox<>("Budget");
    private final Accordion accordion = new Accordion();
    Button pdfBtn = new Button("Download PDF");
    Button excelBtn = new Button("Download Excel");

    Button withBgtpdfBtn = new Button("Download PDF");
    Button withBgtexcelBtn = new Button("Download Excel");
    private final Anchor pdfDownloadAnchor = new Anchor();
    RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
    HorizontalLayout topsub = new HorizontalLayout();
    Button refresh = new Button("Reload");
    private final QtrReleasesServiceImpl qtrReleasesServiceImpl;
    GetPeriods periods = new GetPeriods();
    Set<Integer> period = new HashSet<>();

    H2 header1 = new H2("UGANDA RAILWAYS CORPORATION");
    H3 header2 = new H3("FINANCIAL & PHYSICAL PERFORMANCE REPORT");
    H3 header3 = new H3("");
    H3 financialHeader = new H3("1. FINANCIAL PERFORMANCE");
    ComboBox<String> qtrComboBox = new ComboBox<>("Select Quarter");
    Map<String, SectionBudgetPerformance> performanceMap = new HashMap<>();
    private final PerformancePdfExportService performancePdfExportService;
    private final PerformanceExcelExportService performanceExcelExportService;
    private final PerformanceContextBuilder performanceContextBuilder;
    private final FreightActualVolumesService freightActualVolumesService;
    private final PassengerActualVolumesService passengerActualVolumesService;
    Budget prevBudget = null;
    private final SystemIconService systemIconService;
    ComboBox<String> qtrComboBox2 = new ComboBox<>("Select Quarter");
    BigDecimal totalRecurrentIncome = BigDecimal.ZERO;
    BigDecimal totalNonRecurrentIncome = BigDecimal.ZERO;
    BigDecimal totalOperationExpense = BigDecimal.ZERO;
    BigDecimal directExpense = BigDecimal.ZERO;

    BigDecimal otherAdminExpense = BigDecimal.ZERO;

    //Variable expenses
    BigDecimal fuelExpense = BigDecimal.ZERO;
    BigDecimal passengerExpense = BigDecimal.ZERO;
    BigDecimal maintenanceExpense = BigDecimal.ZERO;
    BigDecimal utitlity_PropertyExpense = BigDecimal.ZERO;

    //Admin Expense
    BigDecimal generalExpense = BigDecimal.ZERO;

    //Other Admin Expense
    BigDecimal board_legalExpense = BigDecimal.ZERO;
    BigDecimal communicationsExpense = BigDecimal.ZERO;
    BigDecimal utilitiesExpense = BigDecimal.ZERO;
    BigDecimal supplies_servicesExpense = BigDecimal.ZERO;
    BigDecimal professional_servicesExpense = BigDecimal.ZERO;
    BigDecimal insurancce_licenseExpense = BigDecimal.ZERO;
    BigDecimal travel_transportExpense = BigDecimal.ZERO;
    BigDecimal miscellanous_otherExpense = BigDecimal.ZERO;
    private final URC_Priority_AreasService sampleURC_Priority_Areas;
    private final DepartmentWorkplanWordExportService departmentWorkplanWordExportService;

    public BudgetReportsView(UserService userService, BudgetService budgetService,
            SamplePersonService samplePersonService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, OrganisationService sampleOrganisationService,
            BudgetItemsService sampleBudgetItemsService, Coalevel1Service sampleCoalevel1Service, URC_Priority_AreasService sampleURC_Priority_AreasService,
            Urc_ActivitiesService sampleUrc_ActivitiesService, CustomDetailedBudgetReportImpService sampleCustomDetailedBudgetReportImpService,
            CustomDetailedBudgetReportService sampleCustomDetailedBudgetReportService, UrcAcntService urcAcntService,
            FreightVolumesService sampleFreightVolumesService, CoaService sampleCoaService, SALFLDGService samopleSALFLDGService,
            UrcBSalfldgService sampleUrcBSalfldgService, BudgetRepository repository, SectionBudgetPerformanceService sectionBudgetPerformanceService,
            QtrReleasesService qtrReleasesService, QtrReleasesReportService qtrReleasesReport, QtrReleasesServiceImpl qtrReleasesServiceImpl,
            PerformancePdfExportService performancePdfExportService, PerformanceContextBuilder performanceContextBuilder,
            PerformanceExcelExportService performanceExcelExportService, FreightActualVolumesService freightActualVolumesService,
            PassengerActualVolumesService passengerActualVolumesService, SystemIconService systemIconService,
            URC_Priority_AreasService sampleURC_Priority_Areas, DepartmentWorkplanWordExportService departmentWorkplanWordExportService,
            BudgetWordReportService budgetWordReportService) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.samplePersonService = samplePersonService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleOrganisationService = sampleOrganisationService;
        this.sampleBudgetItemsService = sampleBudgetItemsService;
        this.sampleCoalevel1Service = sampleCoalevel1Service;
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.sampleCustomDetailedBudgetReportImpService = sampleCustomDetailedBudgetReportImpService;
        this.sampleCustomDetailedBudgetReportService = sampleCustomDetailedBudgetReportService;
        this.urcAcntService = urcAcntService;
        this.sampleFreightVolumesService = sampleFreightVolumesService;
        this.sampleCoaService = sampleCoaService;
        this.samopleSALFLDGService = samopleSALFLDGService;
        this.sampleUrcBSalfldgService = sampleUrcBSalfldgService;
        this.repository = repository;
        this.sectionBudgetPerformanceService = sectionBudgetPerformanceService;
        this.qtrReleasesService = qtrReleasesService;
        this.qtrReleasesReport = qtrReleasesReport;
        this.qtrReleasesServiceImpl = qtrReleasesServiceImpl;
        this.performancePdfExportService = performancePdfExportService;
        this.performanceContextBuilder = performanceContextBuilder;
        this.performanceExcelExportService = performanceExcelExportService;
        this.freightActualVolumesService = freightActualVolumesService;
        this.passengerActualVolumesService = passengerActualVolumesService;
        this.systemIconService = systemIconService;
        this.sampleURC_Priority_Areas = sampleURC_Priority_Areas;
        this.departmentWorkplanWordExportService = departmentWorkplanWordExportService;
        this.budgetWordReportService = budgetWordReportService;

        addClassNames("budget-reports-view");

        VerticalLayout sheet1 = new VerticalLayout();
        VerticalLayout sheet2 = new VerticalLayout();
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();

        TabSheet tabSheet2 = new TabSheet();
        tabSheet2.setWidthFull();
        //gridCustomDetailedBudgetReportImpItems = sampleCustomDetailedBudgetReportImpService.getAllReportsImpByUser(user);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        comboBox.setItems(budgetService.getBudgets());
        comboBox.setItemLabelGenerator(Budget::getFinancialYear);

        comboBox2.setItems(budgetService.getBudgets());
        comboBox2.setItemLabelGenerator(Budget::getFinancialYear);

        sections.setItems(user.getDeptsection());
        sections.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        sections.setWidthFull();
        dataLayout.setHeight("90%");
        comboBox.addValueChangeListener(e -> {
            budgetType.setItems(sampleOrganisationService.getOrganisationsByBudget(e.getValue()));
        });

        comboBox2.addValueChangeListener(e -> {
            budgetType2.setItems(sampleOrganisationService.getOrganisationsByBudget(e.getValue()));
        });
        if (!comboBox.isEmpty()) {
            budgetType.setItems(sampleOrganisationService.getOrganisationsByBudget(comboBox.getValue()));
        }

        if (!comboBox2.isEmpty()) {
            budgetType2.setItems(sampleOrganisationService.getOrganisationsByBudget(comboBox.getValue()));
        }
        CustomDetailedBudgetReportcombo.setItems(user.getDeptsection());
        CustomDetailedBudgetReportcombo.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        CustomDetailedBudgetReportImpcomboBox.setItemLabelGenerator(CustomDetailedBudgetReportImp::getReportname);
        refreshCustomDetailedBudgetReportImpItems();
        budgetType.setItemLabelGenerator(Organisation::getName);
        budgetType2.setItemLabelGenerator(Organisation::getName);
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        AccordionPanel panel0 = new AccordionPanel("Programme Budget");
        AccordionPanel panel1 = new AccordionPanel("Activity Budget");
        AccordionPanel panel2 = new AccordionPanel("Budget By Account Code");

        // Add content to the panels
        panel0.addContent(new Button("Budget Activities in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel0.addContent(new Button("Budget Activities in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel1.addContent(new Button("Budget Activities in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel1.addContent(new Button("Budget Activities in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel2.addContent(new Button("Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleAccountCodeDetailClick()));
        panel2.addContent(new Button("Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        // Add the panels to the Accordion
        accordion.add(panel0);
        accordion.add(panel1);
        accordion.add(panel2);

        Accordion accordion2 = new Accordion();
        accordion2.setWidthFull();
        AccordionPanel panel02 = new AccordionPanel("Custom Programme Budget");
        AccordionPanel panel12 = new AccordionPanel("Custom Activity Budget");
        AccordionPanel panel22 = new AccordionPanel("Custom Budget By Account Code");
        AccordionPanel panel33 = new AccordionPanel("Funding Source Envelope");
        AccordionPanel panel44 = new AccordionPanel("Volumes Report");
        AccordionPanel panel55 = new AccordionPanel("Performance Report");
        AccordionPanel panel66 = new AccordionPanel("Procurement Plan");

        // Add content to the panels
        panel02.addContent(new Button("Budget Activities in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel02.addContent(new Button("Budget Activities in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel12.addContent(new Button("Budget Activities with Codes in Detail", VaadinIcon.FILE_PRESENTATION.create(), e -> handleCustomActivityByCOASummaryPDFClick()));
        panel12.addContent(new Button("Budget Activities with Codes in Detail Report", VaadinIcon.FILE_TEXT.create(), e -> handleCustomActivityByCOASummaryWordClick()));
        panel12.addContent(new Button("Budget Activities in Summary", VaadinIcon.FILE_PRESENTATION.create(), e -> handleCustomActivitySummaryPDFClick()));

        panel22.addContent(new Button("Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleCustomAccountCodeDetailClick()));
        panel22.addContent(new Button("Budget By Account Code in Detail Word", new Icon(VaadinIcon.DOWNLOAD), e -> handleCustomAccountCodeDetailWordClick()));
        //panel22.addContent(new Button("Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel22.addContent(new Button("Budget Summary By Account Code", VaadinIcon.FILE_PRESENTATION.create(), e -> handleBudgetSummaryByAccountCodeClick()));
        panel33.addContent(new Button("View By Fund Source", VaadinIcon.FILE_PRESENTATION.create(), e -> setFundSourcedataLayout()));
        panel33.addContent(new Button("Fund Source Vs Expenditure", VaadinIcon.FILE_PRESENTATION.create(), e -> setFundSourceExpdataLayout()));
        panel44.addContent(new Button("Tonnage & Passenger Performance", VaadinIcon.FILE_PRESENTATION.create(), e -> setVolumesdataLayout()));
        panel55.addContent(new Button("Budget Performance", VaadinIcon.FILE_PRESENTATION.create(), e -> setPerformanceLayout()));
        panel55.addContent(new Button("Revenue Performance", VaadinIcon.FILE_PRESENTATION.create(), e -> setRevenuePerformancedataLayout()));

        panel66.addContent(new Button("Departmental Procurement Plan", VaadinIcon.FILE_PRESENTATION.create(), e -> handleDepartmentalProcurementPlan()));

        // Add the panels to the Accordion
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        //accordion2.add(panel02);
        accordion2.add(panel12);
        accordion2.add(panel22);
        accordion2.add(panel33);
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.FREIGHT)) {
            accordion2.add(panel44);
        }

        accordion2.add(panel55);
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) {
            accordion2.add(panel66);
        }

        HorizontalLayout hlay = new HorizontalLayout();
        hlay.setWidthFull();
        hlay.add(comboBox, budgetType, sections);
        hlay.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, comboBox);
        hlay.setAlignItems(FlexComponent.Alignment.BASELINE);
        sheet1.add(hlay, accordion);

        HorizontalLayout hlay2 = new HorizontalLayout();
        hlay2.setWidthFull();
        hlay2.add(comboBox2, budgetType2, CustomDetailedBudgetReportImpcomboBox);
        hlay2.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, comboBox2);
        hlay2.setAlignItems(FlexComponent.Alignment.BASELINE);
        sheet2.add(hlay2, accordion2);
        VerticalLayout sheet2Rpt = new VerticalLayout();
        sheet2Rpt.add(new Div(new Text("My Custom Budget Reports")));

        VerticalLayout sheet2Rpt2 = new VerticalLayout();
        sheet2Rpt2.add(new Div(new Text("Excel Sheet Data")));

        SplitLayout splitLayout2 = new SplitLayout(sheet2Rpt, sheet2Rpt2);
        splitLayout2.setHeightFull();
        splitLayout2.setSplitterPosition(50);
        splitLayout2.setOrientation(SplitLayout.Orientation.VERTICAL);

        //SplitLayout splitLayout = new SplitLayout(sheet2, splitLayout2);
        SplitLayout splitLayout = new SplitLayout(sheet2, tabSheet2);
        tabSheet2.add("Report View", dataLayout);
        tabSheet2.add("Custom Report Settings", splitLayout2);

        tabSheet2.add("Funds Release Settings", setReleaseFundsLayout());

        splitLayout.setHeightFull();
        splitLayout.setSplitterPosition(30);

        gridCustomDetailedBudgetReportImp.addColumn(CustomDetailedBudgetReportImp::getReportname).setHeader("Report Name");

        gridCustomDetailedBudgetReport.addColumn(CustomDetailedBudgetReport::getSheetname).setHeader("Sheet Name").setWidth("250px").setFlexGrow(0);
        gridCustomDetailedBudgetReport.addColumn(report -> formatDeptSection(report)).setHeader("Department Sections");
        gridCustomDetailedBudgetReport.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        TextField CustomDetailedBudgetReportImptextField = new TextField();
        CustomDetailedBudgetReportImptextField.setLabel("Report Name");
        CustomDetailedBudgetReportImptextField.setClearButtonVisible(true);
        CustomDetailedBudgetReportImptextField.setWidthFull();
        //CustomDetailedBudgetReportImptextField.setClearButtonVisible(true);
        //CustomDetailedBudgetReportImptextField.setPrefixComponent(VaadinIcon.CLOSE_CIRCLE_O.create());

        CustomDetailedBudgetReportImpcomboBox.addValueChangeListener(e -> {
            sampleCustomDetailedBudgetReportImp = e.getValue();
        });

        gridCustomDetailedBudgetReportImp.asSingleSelect().addValueChangeListener(e -> {
            gridCustomDetailedBudgetReport.deselectAll();
            if (e.getValue() != null) {
                sampleCustomDetailedBudgetReportImp = e.getValue();
                CustomDetailedBudgetReportImptextField.setValue(e.getValue().getReportname());
                sampleCustomDetailedBudgetReportImp.setReportname(CustomDetailedBudgetReportImptextField.getValue());
                refreshCustomDetailedBudgetReportItems(e.getValue());
                UI.getCurrent().navigate(BudgetReportsView.class);

                //UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getUserId()));
            } else {

                CustomDetailedBudgetReportImptextField.clear();
                sampleCustomDetailedBudgetReportImp = new CustomDetailedBudgetReportImp();
                UI.getCurrent().navigate(BudgetReportsView.class);
            }
            clearForm2();
        });
        gridCustomDetailedBudgetReport.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                CustomDetailedBudgetReporttextField.setValue(e.getValue().getSheetname());
                CustomDetailedBudgetReportcombo.setValue(e.getValue().getDeptsection());
            }

        });
        HorizontalLayout layout = new HorizontalLayout();
        Button save = new Button("Save");
        Button delete = new Button("Delete A");
        save.addClickListener(e -> {
            try {
                sampleCustomDetailedBudgetReportImp = gridCustomDetailedBudgetReportImp.asSingleSelect().getValue();
                if (this.sampleCustomDetailedBudgetReportImp == null) {
                    this.sampleCustomDetailedBudgetReportImp = new CustomDetailedBudgetReportImp();

                    this.sampleCustomDetailedBudgetReportImp.setReportname(CustomDetailedBudgetReportImptextField.getValue());
                    this.sampleCustomDetailedBudgetReportImp.setUser(user);
                } else {
                    this.sampleCustomDetailedBudgetReportImp.setReportname(CustomDetailedBudgetReportImptextField.getValue());
                    this.sampleCustomDetailedBudgetReportImp.setUser(user);

                    //gridCustomDetailedBudgetReportImp.setItems(gridCustomDetailedBudgetReportImpItems);
                }
                sampleCustomDetailedBudgetReportImpService.saveReportImp(sampleCustomDetailedBudgetReportImp);
                CustomDetailedBudgetReportImptextField.clear();
                refreshCustomDetailedBudgetReportImpItems();
                UI.getCurrent().navigate(BudgetReportsView.class);
            } catch (Exception validationException) {
                // Notification.show(" An exception happened while trying to store the User details. ");
                Notificationerror(" An exception happened while trying to store the Report details. " + validationException.getMessage());
            }
        });
        delete.addClickListener(e -> {
            if (!gridCustomDetailedBudgetReportImp.asSingleSelect().isEmpty()) {
                long t = gridCustomDetailedBudgetReportImp.asSingleSelect().getValue().getId();
                sampleCustomDetailedBudgetReportImpService.deleteReportImp(t);
                Notification.show("Print " + t);
                refreshCustomDetailedBudgetReportImpItems();
            } else {
                Notificationerror("Select a report");
            }
        });
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        layout.add(CustomDetailedBudgetReportImptextField, save, delete);

        HorizontalLayout layout2 = new HorizontalLayout();
        Button save2 = new Button("Save");
        Button delete2 = new Button("Delete");
        save2.addClickListener(e -> {
            if (!gridCustomDetailedBudgetReportImp.asSingleSelect().isEmpty()) {
                CustomDetailedBudgetReport report = gridCustomDetailedBudgetReport.asSingleSelect().getValue();
                if (report != null) {
                    report.setSheetname(CustomDetailedBudgetReporttextField.getValue());
                    report.setDeptsection(CustomDetailedBudgetReportcombo.getSelectedItems());
                    report.setBudgetreport(gridCustomDetailedBudgetReportImp.asSingleSelect().getValue());
                } else {
                    report = new CustomDetailedBudgetReport();
                    report.setSheetname(CustomDetailedBudgetReporttextField.getValue());
                    report.setDeptsection(CustomDetailedBudgetReportcombo.getSelectedItems());
                    report.setBudgetreport(gridCustomDetailedBudgetReportImp.asSingleSelect().getValue());
                }
                sampleCustomDetailedBudgetReportService.saveReport(report);
                refreshCustomDetailedBudgetReportItems(gridCustomDetailedBudgetReportImp.asSingleSelect().getValue());
                clearForm2();
            } else {
                Notificationerror("Select a report");
            }
        });
        CustomDetailedBudgetReporttextField.setLabel("Sheet Name");
        CustomDetailedBudgetReporttextField.setClearButtonVisible(true);

        layout2.add(CustomDetailedBudgetReporttextField, CustomDetailedBudgetReportcombo, save2, delete2);
        sheet2Rpt.add(gridCustomDetailedBudgetReportImp, layout);
        sheet2Rpt2.add(gridCustomDetailedBudgetReport, layout2);
        layout2.setAlignItems(FlexComponent.Alignment.BASELINE);

        tabSheet.setHeightFull();
        sheet2.setHeightFull();
        sheet1.setHeightFull();
        setHeightFull();
        tabSheet.add("General Report", sheet1);
        tabSheet.add("Customised Report", splitLayout);
        add(tabSheet);
    }

    public void clearForm2() {
        CustomDetailedBudgetReporttextField.clear();
        CustomDetailedBudgetReportcombo.clear();
    }

    public VerticalLayout setReleaseFundsLayout() {
        VerticalLayout realiseAccodion = new VerticalLayout();
        realiseAccodion.add(new H3("Quarter Releases per Fund Source (by Section)"));
        configureBudgetSelector();
        configureFundSourceView();
        realiseAccodion.add(buildTopBar());

        accordion.setWidthFull();
        realiseAccodion.add(accordion);

        refreshAccordion();
        return realiseAccodion;
    }

    private String formatBigDecimal(BigDecimal value) {
        return value != null ? String.format("%,.2f", value) : "0.00";
    }

    public void setPerformanceLayout() {
        datasubLayout.removeAll();
        dataLayout.removeAll();

        qtrComboBox.setItems("Qtr 1", "Qtr 2", "Qtr 3", "Qtr 4");
        qtrComboBox.setClearButtonVisible(true);

        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
            return;
        }

        List<CustomDetailedBudgetReport> findByBudgetreport = sampleCustomDetailedBudgetReportService.findByBudgetreport(CustomDetailedBudgetReportImpcomboBox.getValue());

        for (CustomDetailedBudgetReport e : findByBudgetreport) {
            header1 = new H2("UGANDA RAILWAYS CORPORATION");
            header2 = new H3("FINANCIAL & PHYSICAL PERFORMANCE REPORT");
            header3 = new H3(comboBox2.getValue().getFinancialYear().toUpperCase());
            financialHeader = new H3("1. FINANCIAL PERFORMANCE");

            H3 header4 = new H3(e.getSheetname().toUpperCase());

            BigDecimal[] computeGrandExpenditureTotals = sampleBudgetItemsService.computeGrandExpenditureTotals(comboBox2.getValue(), e.getDeptsection());
            Grid<PriorityArea> financialGrid = new Grid<>(PriorityArea.class, false);
            financialGrid.removeAllColumns();
            financialGrid.setSizeFull();
            financialGrid.setAllRowsVisible(true);
            financialGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);

            Grid<Urc_Activities> physicalGrid = new Grid<>(Urc_Activities.class, false);
            physicalGrid.removeAllColumns();
            physicalGrid.setSizeFull();
            physicalGrid.setAllRowsVisible(true);
            physicalGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);

            financialGrid.addColumn(PriorityArea::getName).setHeader("NDP Programme");

            financialGrid.addColumn(new ComponentRenderer<>(area -> {
                Span span = new Span();
                span.setText(formatBigDecimal(computeGrandExpenditureTotals[0]));
                Tooltip tooltip = Tooltip.forComponent(span);
                tooltip.setText("Total Budget: " + formatBigDecimal(computeGrandExpenditureTotals[0]));
                tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
                tooltip.setManual(false); // show on hover            
                return span;
            })).setHeader("Planned Output / Budget (UGX)");

            financialGrid.addColumn(new ComponentRenderer<>(area -> {
                Span span = new Span();
                span.setText(formatBigDecimal(computeGrandExpenditureTotals[0]));
                Tooltip tooltip = Tooltip.forComponent(span);
                tooltip.setText("Approved Budget: " + formatBigDecimal(computeGrandExpenditureTotals[0]));
                tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
                tooltip.setManual(false); // show on hover            
                return span;
            })).setHeader("Approved Budget (UGX)");

            financialGrid.addColumn(new ComponentRenderer<>(area -> {
                if (comboBox2.getValue() != null) {
                    switch (qtr) {
                        case 1:
                            actualRealise = samopleSALFLDGService.getTotalAmountByPeriods(periods.getFinancialYearPeriods(comboBox2.getValue(), 1), samopleSALFLDGService.extractTrimmedAnlCodes(e.getDeptsection()));
                            break;
                        case 2:
                            Set<Integer> period1 = periods.getFinancialYearPeriods(comboBox2.getValue(), 1);
                            Set<Integer> period2 = periods.getFinancialYearPeriods(comboBox2.getValue(), 2);
                            period1.addAll(period2);
                            Set<Integer> combinedPeriods = period1;
                            period = combinedPeriods;
                            actualRealise = samopleSALFLDGService.getTotalAmountByPeriods(period, samopleSALFLDGService.extractTrimmedAnlCodes(e.getDeptsection()));
                            break;
                        case 3:
                            period = Stream.concat(periods.getFinancialYearPeriods(comboBox2.getValue(), 1).stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 2).stream()).collect(Collectors.toSet());
                            period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 3).stream()).collect(Collectors.toSet());
                            actualRealise = samopleSALFLDGService.getTotalAmountByPeriods(period, samopleSALFLDGService.extractTrimmedAnlCodes(e.getDeptsection()));
                            break;
                        case 4:
                            period = Stream.concat(periods.getFinancialYearPeriods(comboBox2.getValue(), 1).stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 2).stream()).collect(Collectors.toSet());
                            period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 3).stream()).collect(Collectors.toSet());
                            period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 4).stream()).collect(Collectors.toSet());
                            actualRealise = samopleSALFLDGService.getTotalAmountByPeriods(period, samopleSALFLDGService.extractTrimmedAnlCodes(e.getDeptsection()));
                            break;
                        default:
                            actualRealise = BigDecimal.ZERO;
                            break;
                    }
                }

                Span label = new Span(formatBigDecimal(actualRealise.abs()));
                if (actualRealise.abs().doubleValue() > computeGrandExpenditureTotals[0].abs().doubleValue()) {
                    label.getStyle().set("color", "red");
                    label.getStyle().set("font-weight", "bold");
                    label.getElement().setProperty("title", "⚠ Warning: Over Spent");

                } else if (actualRealise.abs().doubleValue() < computeGrandExpenditureTotals[0].abs().doubleValue()) {
                    label.getStyle().set("color", "green");
                    label.getStyle().set("font-weight", "bold");
                    //label.getElement().setProperty("title", "⚠ Warning: Below You Actual Expenditure");

                }

                label.setText(formatBigDecimal(actualRealise.abs()));
                Tooltip tooltip = Tooltip.forComponent(label);
                tooltip.setText("Release Spent: " + formatBigDecimal(actualRealise.abs()));
                tooltip.setPosition(Tooltip.TooltipPosition.TOP_START);
                tooltip.setManual(false); // show on hover            

                return label;
            })).setHeader("Release Spent (UGX) BNs");

            financialGrid.addColumn(area -> {
                String reason = "";

                reason = calculatePercentage(actualRealise, computeGrandExpenditureTotals[0]);
                return reason.isBlank() ? "—" : reason;
            }).setHeader("% Spent");

            financialGrid.addColumn(area -> {
                if (performanceMap.isEmpty()) {
                    return "—";
                }

                return e.getDeptsection().stream()
                        .map(sect -> performanceMap.get(sect.getANL_CODE()))
                        .filter(Objects::nonNull)
                        .map(p -> getReasonByQuarter(p, qtr))
                        .filter(s -> s != null && !s.isBlank())
                        .collect(Collectors.joining(", "));
            }).setHeader("Reasons for Under / Over Absorption");

            financialGrid.setHeight("150px"); // Adjust as needed   

            qtrComboBox.addValueChangeListener(ex -> {
                String qt = ex.getValue();

                if (qt == null) {
                    qtr = 0;
                } else {
                    switch (qt) {
                        case "Qtr 1" -> {
                            qtr = 1;
                            header3.setText(comboBox2.getValue().getFinancialYear() + ", Quarter 1".toUpperCase());
                        }
                        case "Qtr 2" -> {
                            qtr = 2;
                            header3.setText(comboBox2.getValue().getFinancialYear() + ", Quarter 2".toUpperCase());
                        }
                        case "Qtr 3" -> {
                            qtr = 3;
                            header3.setText(comboBox2.getValue().getFinancialYear() + ", Quarter 3".toUpperCase());
                        }
                        case "Qtr 4" -> {
                            qtr = 4;
                            header3.setText(comboBox2.getValue().getFinancialYear() + ", Quarter 4".toUpperCase());
                        }
                        default ->
                            qtr = 0;
                    }
                }

                if (comboBox2.getValue() != null) {
                    List<SectionBudgetPerformance> performances = sectionBudgetPerformanceService.findByBudget(comboBox2.getValue());

                    performanceMap = performances.stream()
                            .collect(Collectors.toMap(
                                    p -> p.getDeptSection().getANL_CODE(),
                                    Function.identity(),
                                    (existing, replacement) -> existing // keep first
                            ));

                }
                priorityAreas = sampleURC_Priority_AreasService.getDistinctPriorityAreasByBudget(comboBox2.getValue().getStartDate());
                financialGrid.setItems(priorityAreas);
                financialGrid.getDataProvider().refreshAll();
                List<Urc_Activities> acts = new ArrayList();
                acts = sampleUrc_ActivitiesService.findByDeptSectionAndBudget(e.getDeptsection(), comboBox2.getValue());
                physicalGrid.setItems(acts);
                physicalGrid.getDataProvider().refreshAll();
            });

            Button excelButton = new Button(new Icon(VaadinIcon.TABLE));
            excelButton.addThemeVariants(ButtonVariant.LUMO_ICON);
            //excelButton.setAriaLabel("Add item");

            Button pdfButton = new Button(new Icon(VaadinIcon.FILE));
            pdfButton.addThemeVariants(ButtonVariant.LUMO_ICON);
            pdfButton.setAriaLabel("Close");
            pdfButton.setTooltipText("Close the dialog");
            HorizontalLayout menuRight = new HorizontalLayout(excelButton, pdfButton);
            HorizontalLayout menu = new HorizontalLayout(qtrComboBox, menuRight);
            menu.setWidthFull();                 // 👈 take full width
            menu.expand(qtrComboBox);            // 👈 push menuRight to the right
            menu.setAlignItems(FlexComponent.Alignment.CENTER);
            excelButton.setTooltipText("Export to Excel");
            pdfButton.setTooltipText("Export to PDF");
            menu.setPadding(false);
            menu.setSpacing(true);
            menu.setAlignItems(FlexComponent.Alignment.BASELINE);

            pdfButton.addClickListener(et -> {
                try {
                    PerformanceReportContext ctx
                            = performanceContextBuilder.build(
                                    comboBox2.getValue(),
                                    qtr,
                                    performanceMap,
                                    priorityAreas
                            );

                    byte[] file = performancePdfExportService.export(ctx, findByBudgetreport);
                    StreamResource resource = new StreamResource(
                            "performance.pdf",
                            () -> new ByteArrayInputStream(file)
                    );

                    Anchor download = new Anchor(resource, "");
                    download.getElement().setAttribute("download", true);
                    download.add(new Button("Click to download"));
                    download.getElement().callJsFunction("click");
                    datasubLayout.add(download);
                } catch (IOException ex) {
                    Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            excelButton.addClickListener(et -> {
                try {
                    PerformanceReportContext ctx
                            = performanceContextBuilder.build(
                                    comboBox2.getValue(),
                                    qtr,
                                    performanceMap,
                                    priorityAreas
                            );

                    byte[] file = performanceExcelExportService.export(ctx, findByBudgetreport);
                    StreamResource resource = new StreamResource(
                            "performance.xlsx",
                            () -> new ByteArrayInputStream(file)
                    );

                    Anchor download = new Anchor(resource, "");
                    download.getElement().setAttribute("download", true);
                    download.add(new Button("Click to download"));
                    download.getElement().callJsFunction("click");
                    datasubLayout.add(download);
                } catch (IOException ex) {
                    Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
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
                    .setHeader("Annual Target");

            physicalGrid.addColumn(area -> {
                String getCum_achievements = "";
                if (area.getDeptSection() != null && area.getBudget() != null && qtr != 0) {
                    switch (qtr) {
                        case 1:
                            getCum_achievements = area.getCum_achievements_qtr1();
                            break;
                        case 2:
                            getCum_achievements = area.getCum_achievements_qtr2();
                            break;
                        case 3:
                            getCum_achievements = area.getCum_achievements_qtr3();
                            break;
                        case 4:
                            getCum_achievements = area.getCum_achievements_qtr4();
                            break;
                        default:
                            break;
                    }
                }

                return getCum_achievements;
            }).setHeader("Cumulative Achievements");

            physicalGrid.addColumn(area -> {
                String getCum_achievements = "";
                if (area.getDeptSection() != null && area.getBudget() != null && qtr != 0) {
                    switch (qtr) {
                        case 1:
                            getCum_achievements = formatCurrency(qtrReleasesServiceImpl.getTotalAmountByPeriodsAndActivity(periods.getFinancialYearPeriods(comboBox2.getValue(), 1), area).abs());
                            System.out.println(periods.getFinancialYearPeriods(comboBox2.getValue(), 1) + " 1");
                            break;
                        case 2:
                            Set<Integer> period1 = periods.getFinancialYearPeriods(comboBox2.getValue(), 1);
                            Set<Integer> period2 = periods.getFinancialYearPeriods(comboBox2.getValue(), 2);
                            period1.addAll(period2);
                            Set<Integer> combinedPeriods = period1;
                            period = combinedPeriods;
                            getCum_achievements = formatCurrency(qtrReleasesServiceImpl.getTotalAmountByPeriodsAndActivity(period, area).abs());
                            System.out.println(periods.getFinancialYearPeriods(comboBox2.getValue(), 2) + " 2");
                            break;
                        case 3:
                            period = Stream.concat(periods.getFinancialYearPeriods(comboBox2.getValue(), 1).stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 2).stream()).collect(Collectors.toSet());
                            period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 3).stream()).collect(Collectors.toSet());
                            getCum_achievements = formatCurrency(qtrReleasesServiceImpl.getTotalAmountByPeriodsAndActivity(period, area).abs());
                            break;
                        case 4:
                            period = Stream.concat(periods.getFinancialYearPeriods(comboBox2.getValue(), 1).stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 2).stream()).collect(Collectors.toSet());
                            period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 3).stream()).collect(Collectors.toSet());
                            period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(comboBox2.getValue(), 4).stream()).collect(Collectors.toSet());
                            getCum_achievements = formatCurrency(qtrReleasesServiceImpl.getTotalAmountByPeriodsAndActivity(period, area).abs());
                            break;
                        default:
                            break;
                    }
                }

                return getCum_achievements;
            }).setHeader("Actual");

            physicalGrid.addColumn(area -> {
                String getCum_achievements = "";
                if (area.getDeptSection() != null && area.getBudget() != null && qtr != 0) {
                    switch (qtr) {
                        case 1:
                            getCum_achievements = area.getPerc_of_TargetAchieved_qtr1();
                            break;
                        case 2:
                            getCum_achievements = area.getPerc_of_TargetAchieved_qtr2();
                            break;
                        case 3:
                            getCum_achievements = area.getPerc_of_TargetAchieved_qtr3();
                            break;
                        case 4:
                            getCum_achievements = area.getPerc_of_TargetAchieved_qtr4();
                            break;
                        default:
                            break;
                    }
                }

                return getCum_achievements;
            }).setHeader("% Target Achieved");

            physicalGrid.addColumn(new ComponentRenderer<>(area -> {
                String getCum_achievements = "-";
                Span span = new Span();
                if (area.getDeptSection() != null && area.getBudget() != null && qtr != 0) {
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
                return span;
            })).setHeader("Explanation for variations");

            H3 physicalHeader = new H3("2. PHYSICAL PERFORMANCE");
            dataLayout.add(header1, header2, header3, header4, menu, financialHeader, financialGrid, physicalHeader, physicalGrid);
            dataLayout.add(datasubLayout);

        }

    }

    private String getReasonByQuarter(SectionBudgetPerformance p, int quarter) {
        return switch (quarter) {
            case 1 ->
                p.getReasonsForUnderOver1();
            case 2 ->
                p.getReasonsForUnderOver2();
            case 3 ->
                p.getReasonsForUnderOver3();
            case 4 ->
                p.getReasonsForUnderOver4();
            default ->
                null;
        };
    }

    public Set<String> extractAnlCodes(Set<UrcDeptSectionAnlDimbgt> entities) {
        return entities.stream()
                .map(UrcDeptSectionAnlDimbgt::getANL_CODE)
                .filter(code -> code != null && !code.isBlank())
                .collect(Collectors.toSet());
    }

    public void setVolumesdataLayout() {
        datasubLayout.removeAll();
        dataLayout.removeAll();
        if (comboBox2.isEmpty()) {
            warningNotification("Make sure that a Budget is selected");
            return;
        }

        TreeGrid<PerformanceRow> grid = new TreeGrid<>();

        grid.addHierarchyColumn(PerformanceRow::getLabel)
                .setHeader("Details")
                .setAutoWidth(true)
                .setFlexGrow(2)
                .setRenderer(new ComponentRenderer<>(row -> {
                    Span span = new Span(row.getLabel());

                    if ("VOLUMES".equals(row.getLabel())
                            || "Northern Route".equals(row.getLabel())
                            || "Southern Route".equals(row.getLabel())
                            || row.getLabel().startsWith("TOTAL")) {
                        span.getStyle().set("font-weight", "600");
                    }

                    return span;
                }));
        int chosenYear = comboBox2.getValue().getStartDate().getYear();
        Optional<Budget> previousBudgt = repository.findByStartDateYear(chosenYear - 1);
        Budget previousBudget = null;
        if (previousBudgt.isPresent()) {
            previousBudget = previousBudgt.get();
        }

        String prevFy = fyLabel(previousBudget, "_");
        String currFy = fyLabel(comboBox2.getValue(), "Selected FY");

        grid.addColumn(PerformanceRow::getPreviousBudgetApproved)
                .setHeader(prevFy + " Approved (MTs)")
                .setTextAlign(ColumnTextAlign.END);

        grid.addColumn(PerformanceRow::getPreviousBudgeActual)
                .setHeader(prevFy + " Actual (MTs)")
                .setTextAlign(ColumnTextAlign.END);

        grid.addColumn(PerformanceRow::getChosenBudget)
                .setHeader(currFy + " Budget (MTs)")
                .setTextAlign(ColumnTextAlign.END);

        TreeData<PerformanceRow> data = new TreeData<>();

        PerformanceRow volumes = new PerformanceRow("VOLUMES", true);
        PerformanceRow northern = new PerformanceRow("Northern Route", true);
        PerformanceRow southern = new PerformanceRow("Southern Route", true);

        COA northernimportsChosen = sampleCoaService.findByCodeAndBudget("111101", comboBox2.getValue());
        COA northernexportsChosen = sampleCoaService.findByCodeAndBudget("111102", comboBox2.getValue());
        COA northernlocalChosen = sampleCoaService.findByCodeAndBudget("111103", comboBox2.getValue());
        List<COA> northernroutes = new ArrayList<>();
        northernroutes.add(northernlocalChosen);
        northernroutes.add(northernexportsChosen);
        northernroutes.add(northernimportsChosen);

        COA southernimportsChosen = sampleCoaService.findByCodeAndBudget("111104", comboBox2.getValue());
        COA southernexportsChosen = sampleCoaService.findByCodeAndBudget("111105", comboBox2.getValue());
        COA southernlocalChosen = sampleCoaService.findByCodeAndBudget("111106", comboBox2.getValue());

        COA passengerChosen = sampleCoaService.findByCodeAndBudget("111601", comboBox2.getValue());
        COA passengerPrevious = sampleCoaService.findByCodeAndBudget("111601", previousBudget);
        List<COA> southernroutes = new ArrayList<>();
        List<COA> allroutesChosen = new ArrayList<>();
        southernroutes.add(southernlocalChosen);
        southernroutes.add(southernexportsChosen);
        southernroutes.add(southernimportsChosen);

        COA northernimportsPrevious = null;
        COA northernexportsPrevious = null;
        COA northernlocalPrevious = null;
        COA southernimportsPrevious = null;
        COA southernexportsPrevious = null;
        COA southernlocalPrevious = null;
        List<COA> northernroutesPrevious = new ArrayList<>();
        List<COA> southernroutesPrevious = new ArrayList<>();
        List<COA> allroutesPrevious = new ArrayList<>();
        String totalPassengerChosen = "";
        String totalPassengerPrevious = "";
        totalPassengerChosen = formatCurrency(sampleBudgetItemsService.getMonthlyTotalByCoa(comboBox2.getValue(), passengerChosen));

        if (previousBudget != null) {
            northernimportsPrevious = sampleCoaService.findByCodeAndBudget("111101", previousBudget);
            northernexportsPrevious = sampleCoaService.findByCodeAndBudget("111102", previousBudget);
            northernlocalPrevious = sampleCoaService.findByCodeAndBudget("111103", previousBudget);

            northernroutesPrevious.add(northernimportsPrevious);
            northernroutesPrevious.add(northernexportsPrevious);
            northernroutesPrevious.add(northernlocalPrevious);

            southernimportsPrevious = sampleCoaService.findByCodeAndBudget("111104", previousBudget);
            southernexportsPrevious = sampleCoaService.findByCodeAndBudget("111105", previousBudget);
            southernlocalPrevious = sampleCoaService.findByCodeAndBudget("111106", previousBudget);

            southernroutesPrevious.add(southernlocalPrevious);
            southernroutesPrevious.add(southernexportsPrevious);
            southernroutesPrevious.add(southernlocalPrevious);

            allroutesPrevious.addAll(northernroutesPrevious);
            allroutesPrevious.addAll(southernroutesPrevious);

            allroutesChosen.addAll(southernroutes);
            allroutesChosen.addAll(northernroutes);

            totalPassengerPrevious = formatCurrency(sampleBudgetItemsService.getMonthlyTotalByCoa(previousBudget, passengerPrevious));
        } else {
            totalPassengerPrevious = "";
        }

        data.addRootItems(volumes);
        Set<COA> coaIds;
        FreightQuarterTotalsDTO getTotalsNorthernImports = freightActualVolumesService.getTotals(previousBudget, northernimportsPrevious);
        FreightQuarterTotalsDTO getTotalsNorthernExports = freightActualVolumesService.getTotals(previousBudget, northernexportsPrevious);
        FreightQuarterTotalsDTO getTotalsNorthernLocal = freightActualVolumesService.getTotals(previousBudget, northernlocalPrevious);
        coaIds = new HashSet<>(northernroutesPrevious);
        FreightQuarterTotalsDTO getTotalsNorthern = freightActualVolumesService.getTotals(previousBudget, coaIds);
        FreightQuarterTotalsDTO getTotalsSouthernImports = freightActualVolumesService.getTotals(previousBudget, southernimportsPrevious);
        FreightQuarterTotalsDTO getTotalsSouthernExports = freightActualVolumesService.getTotals(previousBudget, southernexportsPrevious);
        FreightQuarterTotalsDTO getTotalsSouthernLocal = freightActualVolumesService.getTotals(previousBudget, southernlocalPrevious);
        coaIds = new HashSet<>(southernroutesPrevious);
        FreightQuarterTotalsDTO getTotalsSouthern = freightActualVolumesService.getTotals(previousBudget, coaIds);

        PassengerQuarterTotalsDTO getPassengerActualTotals = passengerActualVolumesService.getTotals(previousBudget, passengerPrevious);

        data.addItem(volumes, northern);
        data.addItem(northern, new PerformanceRow("Net Tons – Exports", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(previousBudget, northernexportsPrevious)), formatCurrency(getTotalsNorthernExports.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(comboBox2.getValue(), northernexportsChosen))));
        data.addItem(northern, new PerformanceRow("Net Tons – Imports", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(previousBudget, northernimportsPrevious)), formatCurrency(getTotalsNorthernImports.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(comboBox2.getValue(), northernimportsChosen))));
        data.addItem(northern, new PerformanceRow("Local Net Tons", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(previousBudget, northernlocalPrevious)), formatCurrency(getTotalsNorthernLocal.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(comboBox2.getValue(), northernlocalChosen))));
        data.addItem(northern, new PerformanceRow("TOTAL TONS NORTHERN", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(previousBudget, northernroutesPrevious)), formatCurrency(getTotalsNorthern.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(comboBox2.getValue(), northernroutes))));

        data.addItem(volumes, southern);
        data.addItem(southern, new PerformanceRow("Net Tons – Exports", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(previousBudget, southernexportsPrevious)), formatCurrency(getTotalsSouthernExports.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(comboBox2.getValue(), southernexportsChosen))));
        data.addItem(southern, new PerformanceRow("Net Tons – Imports", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(previousBudget, southernimportsPrevious)), formatCurrency(getTotalsSouthernImports.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(comboBox2.getValue(), southernimportsChosen))));
        data.addItem(southern, new PerformanceRow("Local Net Tons", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(previousBudget, southernlocalPrevious)), formatCurrency(getTotalsSouthernLocal.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(comboBox2.getValue(), southernlocalChosen))));
        data.addItem(southern, new PerformanceRow("TOTAL TONS SOUTHERN", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(previousBudget, southernroutesPrevious)), formatCurrency(getTotalsSouthern.getYear()), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(comboBox2.getValue(), southernroutes))));

        BigDecimal totalActuals = getTotalsNorthern.getYear().add(getTotalsSouthern.getYear());
        data.addItem(volumes, new PerformanceRow("TOTAL TONS", formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(previousBudget, allroutesPrevious)), formatCurrency(totalActuals), formatCurrency(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(comboBox2.getValue(), allroutesChosen))));

// Passengers
        PerformanceRow passengers = new PerformanceRow("PASSENGERS", true);
        data.addRootItems(passengers);
        data.addItem(passengers, new PerformanceRow("Passengers Kampala–Namanve Route", totalPassengerPrevious, formatCurrency(getPassengerActualTotals.getYear()), totalPassengerChosen));
        data.addItem(passengers, new PerformanceRow("Passengers Kampala – Other Routes", "-", "-", "-"));
        data.addItem(passengers, new PerformanceRow("TOTAL PASSENGERS", totalPassengerPrevious, formatCurrency(getPassengerActualTotals.getYear()), totalPassengerChosen));

        grid.setTreeData(data);
        grid.expand(volumes, northern, southern, passengers);
        grid.setPartNameGenerator(row -> {
            if (row.getLabel() == null) {
                return null;
            }

            String label = row.getLabel();

            if (label.equals("VOLUMES")
                    || label.equals("Northern Route")
                    || label.equals("Southern Route")) {
                return "section-row";
            }

            if (label.startsWith("TOTAL")) {
                return "total-row";
            }

            return null;
        });

        dataLayout.add(grid);
        Button pdfBtn = new Button("Download PDF");
        Button excelBtn = new Button("Download Excel");
        datasubLayout.add(pdfBtn, excelBtn);
        dataLayout.add(datasubLayout);

        pdfBtn.addClickListener(e -> {
            try {
                List<PerformanceRow> rows = flattenTreeData(data);

                byte[] pdf = generateVolumesPdf(
                        rows,
                        prevFy,
                        currFy
                );

                StreamResource resource = new StreamResource(
                        "Volumes_Report.pdf",
                        () -> new ByteArrayInputStream(pdf)
                );

                Anchor download = new Anchor(resource, "");
                download.getElement().setAttribute("download", true);
                download.add(new Button("Click to download"));
                download.getElement().callJsFunction("click");
                datasubLayout.add(download);

            } catch (Exception ex) {
                ex.printStackTrace();
                warningNotification("Failed to generate PDF");
            }
        });

        excelBtn.addClickListener(e -> {
            try {
                List<PerformanceRow> rows = flattenTreeData(data);
                byte[] excel = generateVolumesExcel(rows, prevFy, currFy);

                StreamResource resource = new StreamResource(
                        "Volumes_Report.xlsx",
                        () -> new ByteArrayInputStream(excel)
                );

                Anchor a = new Anchor(resource, "");
                a.getElement().setAttribute("download", true);
                a.add(new Button("Download"));
                a.getElement().callJsFunction("click");
                datasubLayout.add(a);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    public void setRevenuePerformancedataLayout() {
        datasubLayout.removeAll();
        dataLayout.removeAll();
        qtrComboBox2.setItems("Qtr 1", "Qtr 2", "Qtr 3", "Qtr 4");
        qtrComboBox2.setClearButtonVisible(true);
        HorizontalLayout bas = new HorizontalLayout();

        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
            return;
        }
        Notification.show("Show revenue performance");
        int chosenYear = comboBox2.getValue().getStartDate().getYear();
        Optional<Budget> previousBudgt = repository.findByStartDateYear(chosenYear - 1);

        Budget previousBudget = null;
        if (previousBudgt.isPresent()) {
            previousBudget = previousBudgt.get();
        }

        String prevFy = fyLabel(previousBudget, "_");
        String currFy = fyLabel(comboBox2.getValue(), "Selected FY");
        TreeGrid<PerformanceRow> grid = new TreeGrid<>();
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        grid.setWidthFull();
        grid.addClassName("perf-grid");

// --- Details (Hierarchy) column ---
        TreeGrid.Column<PerformanceRow> detailsCol
                = grid.addComponentHierarchyColumn(row -> {
                    String label = row.getLabel() == null ? "" : row.getLabel().trim();
                    Span span = new Span(label);

                    if (isSectionRow(row)) {
                        span.getStyle()
                                .set("font-weight", "600")
                                .set("display", "block")
                                .set("width", "100%")
                                .set("padding", "6px 0");
                    }
                    return span;
                })
                        .setHeader("Details")
                        .setAutoWidth(true)
                        .setFlexGrow(2);

// --- Budget columns ---
        Grid.Column<PerformanceRow> approvedCol = grid.addColumn(PerformanceRow::getPreviousBudgetApproved).setAutoWidth(true);
        Grid.Column<PerformanceRow> revisedCol = grid.addColumn(PerformanceRow::getPreviousRevised).setAutoWidth(true);
        Grid.Column<PerformanceRow> actualQ2Col = grid.addColumn(PerformanceRow::getPreviousBudgeActual).setAutoWidth(true);
        Grid.Column<PerformanceRow> projectedCol = grid.addColumn(PerformanceRow::getPreviousProjected).setAutoWidth(true);
        Grid.Column<PerformanceRow> plannedCol = grid.addColumn(PerformanceRow::getChosenBudget).setAutoWidth(true);

// optional alignment
        approvedCol.setTextAlign(ColumnTextAlign.END);
        revisedCol.setTextAlign(ColumnTextAlign.END);
        actualQ2Col.setTextAlign(ColumnTextAlign.END);
        projectedCol.setTextAlign(ColumnTextAlign.END);
        plannedCol.setTextAlign(ColumnTextAlign.END);

// =====================================================
// HEADER ROWS (SAFE ORDER)
// =====================================================
// 1) Create the DEFAULT (first-created) header row explicitly
        HeaderRow defaultRow = grid.appendHeaderRow();

// Put normal column headers here (no join on this row)
        defaultRow.getCell(detailsCol).setText("Details");
        defaultRow.getCell(approvedCol).setText("Approved Budget");
        defaultRow.getCell(revisedCol).setText("Revised Budget");
        defaultRow.getCell(actualQ2Col).setText("Actual Q2");
        defaultRow.getCell(projectedCol).setText("Projected Actuals");
        defaultRow.getCell(plannedCol).setText("Planned budget");

// 2) Create GROUP row above default row (join is allowed here)
// 2) Group row above (join allowed here)
        HeaderRow groupRow = grid.prependHeaderRow();
        groupRow.getCell(detailsCol).setText(""); // IMPORTANT: keep blank

// Join FY across the 4 previous-year columns
        HeaderCell fy2425 = groupRow.join(
                groupRow.getCell(approvedCol),
                groupRow.getCell(revisedCol),
                groupRow.getCell(actualQ2Col),
                groupRow.getCell(projectedCol)
        );
        fy2425.setText(previousBudget.getFinancialYear());

// Current FY over planned column
        groupRow.getCell(plannedCol).setText(comboBox2.getValue().getFinancialYear());

// 3) Optional units row below
        HeaderRow unitsRow = grid.appendHeaderRow();
        unitsRow.getCell(detailsCol).setText("");
        unitsRow.getCell(approvedCol).setText("UGX 000");
        unitsRow.getCell(revisedCol).setText("UGX 000");
        unitsRow.getCell(actualQ2Col).setText("UGX 000");
        unitsRow.getCell(projectedCol).setText("UGX 000");
        unitsRow.getCell(plannedCol).setText("UGX 000");

        TreeData<PerformanceRow> data = new TreeData<>();

        PerformanceRow recurrentIncome = new PerformanceRow("RECURRENT INCOME", true);
        PerformanceRow freight_services = new PerformanceRow("FREIGHT SERVICES", true);
        PerformanceRow northern = new PerformanceRow("Northern Route", true);
        PerformanceRow southern = new PerformanceRow("Southern Route", true);

        data.addRootItems(recurrentIncome);
        data.addItem(recurrentIncome, freight_services);
        data.addItem(freight_services, northern);
        List<COA> northencodesCurBdgt = sampleCoaService.findByBudgetAndClass3(comboBox2.getValue(), Classification3.Northern_Route);
        List<COA> northencodesPrevBdgt = sampleCoaService.findByBudgetAndClass3(previousBudget, Classification3.Northern_Route);
        List<COA> listcodesCurBdgt = new ArrayList();
        List<COA> listcodesPrevBdgt = new ArrayList();
        List<COA> freightCodesCurBdgt = new ArrayList();
        List<COA> freightCodesPrevBdgt = new ArrayList();
        freightCodesCurBdgt.addAll(northencodesCurBdgt);
        freightCodesPrevBdgt.addAll(northencodesPrevBdgt);
        Set<String> class1codes = new HashSet<>();
        Set<String> CodesPrevBdgt = new HashSet<>();

        List<COA> listclass1codesCurBdgt = new ArrayList();
        List<COA> listclass1codesPrevBdgt = new ArrayList();

        Set<String> freightCodes = new HashSet<>();
        for (COA coa : northencodesCurBdgt) {
            COA coaPrev = findByCodeFromList(northencodesPrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            freightCodes.add(coa.getCode());
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(northern, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }
        data.addItem(northern, new PerformanceRow("TOTAL NORTHERN ROUTE", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        northencodesCurBdgt = sampleCoaService.findByBudgetAndClass3(comboBox2.getValue(), Classification3.Southern_Route);
        northencodesPrevBdgt = sampleCoaService.findByBudgetAndClass3(previousBudget, Classification3.Southern_Route);
        listcodesCurBdgt.clear();
        listcodesPrevBdgt.clear();
        //class1codes.clear();
        CodesPrevBdgt.clear();
        freightCodesCurBdgt.addAll(northencodesCurBdgt);
        freightCodesPrevBdgt.addAll(northencodesPrevBdgt);
        data.addItem(freight_services, southern);
        for (COA coa : northencodesCurBdgt) {
            COA coaPrev = findByCodeFromList(northencodesPrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(southern, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }
        data.addItem(southern, new PerformanceRow("TOTAL SOUTHERN ROUTE", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));

        data.addItem(freight_services, new PerformanceRow("TOTAL FREIGHT", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, freightCodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), freightCodes)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), freightCodesCurBdgt))));

        PerformanceRow other_fees_charges = new PerformanceRow("OTHER FEES & CHARGES", true);
        data.addItem(recurrentIncome, other_fees_charges);
        List<COA> otherfeesCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Other_Fees_Charges);
        List<COA> otherfeesCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Other_Fees_Charges);
        //class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : otherfeesCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(otherfeesCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(other_fees_charges, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(other_fees_charges, new PerformanceRow("TOTAL OTHER FEES & CHARGES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));

        PerformanceRow rentalIncome = new PerformanceRow("RENT INCOME", true);
        data.addItem(recurrentIncome, rentalIncome);
        List<COA> rentCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Rent_Income);
        List<COA> rentCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Rent_Income);
        //class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : rentCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(rentCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(rentalIncome, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(rentalIncome, new PerformanceRow("TOTAL RENT INCOME", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));

        // Passengers
        PerformanceRow passengers = new PerformanceRow("PASSENGERS TICKET SALES", true);
        data.addItem(recurrentIncome, passengers);
        List<COA> passengersCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Passenger_Ticket_Sales);
        List<COA> passengersCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Passenger_Ticket_Sales);
        //class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : passengersCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(passengersCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(passengers, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(passengers, new PerformanceRow("TOTAL PASSENGER SERVICE INCOME", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));

        PerformanceRow miscellaneous = new PerformanceRow("MISCELLANEOUS INCOME", true);
        data.addItem(recurrentIncome, miscellaneous);
        List<COA> miscellaneousCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Miscellaneous_Income);
        List<COA> miscellaneousCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Miscellaneous_Income);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : miscellaneousCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(miscellaneousCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(miscellaneous, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(miscellaneous, new PerformanceRow("TOTAL MISCELLANEOUS INCOME", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));

        data.addItem(recurrentIncome, new PerformanceRow("TOTAL RECURRENT INCOME", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listclass1codesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), class1codes)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listclass1codesCurBdgt))));
        grid.setTreeData(data);

        PerformanceRow non_recurrentIncome = new PerformanceRow("NON RECURRENT INCOME", true);
        data.addRootItems(non_recurrentIncome);

        List<COA> non_recurrentIncomeCodeCurBdgt = sampleCoaService.findByBudgetAndClass1(comboBox2.getValue(), Classification1.Non_Recurrent_Income);
        List<COA> non_recurrentIncomeCodePrevBdgt = sampleCoaService.findByBudgetAndClass1(previousBudget, Classification1.Non_Recurrent_Income);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : non_recurrentIncomeCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(non_recurrentIncomeCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(non_recurrentIncome, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(non_recurrentIncome, new PerformanceRow("TOTAL NON RECURRENT INCOME", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));

        PerformanceData datas = sampleBudgetItemsService.getTotalIncomeData(comboBox2.getValue());
        data.addRootItems(new PerformanceRow("TOTAL INCOME", formatCurrencyB(datas.getPreviousBudgetApproved()), formatCurrencyB(datas.getPreviousBudgeActual()), formatCurrencyB(datas.getChosenBudget())));
        //data.addItem(non_recurrentIncome, new PerformanceRow("TOTAL INCOME", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt)), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        //grid.expand(recurrentIncome, freight_services, other_fees_charges, rentalIncome, passengers, miscellaneous, northern, southern, passengers, non_recurrentIncome);

        Button pdfBtn = new Button(VaadinIcon.FILE_TEXT.create());
        pdfBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        pdfBtn.getElement().setProperty("title", "Download PDF");

        Button excelBtn = new Button(VaadinIcon.TABLE.create());
        excelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        excelBtn.getElement().setProperty("title", "Download Excel");
        bas.add(qtrComboBox2, pdfBtn, excelBtn);
        bas.setWidthFull();
        bas.setAlignItems(FlexComponent.Alignment.BASELINE);
        qtrComboBox2.setWidthFull();
        H2 head = new H2("REVENUE PERFORMANCE");
        //datasubLayout.add(pdfBtn, excelBtn);
        dataLayout.add(bas, head, grid);
        dataLayout.add(datasubLayout);
        prevBudget = previousBudget;
        pdfBtn.addClickListener(e -> {
            try {
                List<PerformanceRow> rows = flattenTreeData(data);

                byte[] pdf = generateRevenuePerformancePdf(
                        rows,
                        prevFy,
                        currFy,
                        prevBudget.getFinancialYear(),
                        comboBox2.getValue().getFinancialYear()
                );

                StreamResource resource = new StreamResource(
                        "Volumes_Report.pdf",
                        () -> new ByteArrayInputStream(pdf)
                );

                Anchor download = new Anchor(resource, "");
                download.getElement().setAttribute("download", true);
                download.add(new Button("Click to download"));
                download.getElement().callJsFunction("click");
                datasubLayout.add(download);

            } catch (Exception ex) {
                ex.printStackTrace();
                warningNotification("Failed to generate PDF");
            }
        });

        excelBtn.addClickListener(e -> {
            try {
                List<PerformanceRow> rows = flattenTreeData(data);
                byte[] excel = generateVolumesExcel(rows, prevFy, currFy);

                StreamResource resource = new StreamResource(
                        "Volumes_Report.xlsx",
                        () -> new ByteArrayInputStream(excel)
                );

                Anchor a = new Anchor(resource, "");
                a.getElement().setAttribute("download", true);
                a.add(new Button("Download"));
                a.getElement().callJsFunction("click");
                datasubLayout.add(a);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        qtrComboBox2.addValueChangeListener(ex -> {
            String qt = ex.getValue();

            if (qt == null) {
                qtr = 0;
            } else {
                switch (qt) {
                    case "Qtr 1" -> {
                        qtr = 1;
                        defaultRow.getCell(actualQ2Col).setText("Actual Q1");
                        defaultRow.getCell(projectedCol).setText("Projected Actuals Q2");
                    }
                    case "Qtr 2" -> {
                        qtr = 2;
                        defaultRow.getCell(actualQ2Col).setText("Actual Q2");
                        defaultRow.getCell(projectedCol).setText("Projected Actuals Q3");
                    }
                    case "Qtr 3" -> {
                        qtr = 3;
                        defaultRow.getCell(actualQ2Col).setText("Actual Q3");
                        defaultRow.getCell(projectedCol).setText("Projected Actuals Q4");
                    }
                    case "Qtr 4" -> {
                        qtr = 4;
                        defaultRow.getCell(actualQ2Col).setText("Actual Q4");
                        defaultRow.getCell(projectedCol).setText("Projected Actuals Q4");
                    }
                    default ->
                        qtr = 0;
                }
            }

        });
        setExpensePerformancedataLayout();
    }

    public void setExpensePerformancedataLayout() {

        HorizontalLayout bas = new HorizontalLayout();

        int chosenYear = comboBox2.getValue().getStartDate().getYear();
        Optional<Budget> previousBudgt = repository.findByStartDateYear(chosenYear - 1);

        Budget previousBudget = null;
        if (previousBudgt.isPresent()) {
            previousBudget = previousBudgt.get();
        }

        String prevFy = fyLabel(previousBudget, "_");
        String currFy = fyLabel(comboBox2.getValue(), "Selected FY");
        TreeGrid<PerformanceRow> grid = new TreeGrid<>();
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        grid.setWidthFull();
        grid.addClassName("perf-grid");

// --- Details (Hierarchy) column ---
        TreeGrid.Column<PerformanceRow> detailsCol
                = grid.addComponentHierarchyColumn(row -> {
                    String label = row.getLabel() == null ? "" : row.getLabel().trim();
                    Span span = new Span(label);

                    if (isSectionRow(row)) {
                        span.getStyle()
                                .set("font-weight", "600")
                                .set("display", "block")
                                .set("width", "100%")
                                .set("padding", "6px 0");
                    }
                    return span;
                })
                        .setHeader("Details")
                        .setAutoWidth(true)
                        .setFlexGrow(2);

// --- Budget columns ---
        Grid.Column<PerformanceRow> approvedCol = grid.addColumn(PerformanceRow::getPreviousBudgetApproved).setAutoWidth(true);
        Grid.Column<PerformanceRow> revisedCol = grid.addColumn(PerformanceRow::getPreviousRevised).setAutoWidth(true);
        Grid.Column<PerformanceRow> actualQ2Col = grid.addColumn(PerformanceRow::getPreviousBudgeActual).setAutoWidth(true);
        Grid.Column<PerformanceRow> projectedCol = grid.addColumn(PerformanceRow::getPreviousProjected).setAutoWidth(true);
        Grid.Column<PerformanceRow> plannedCol = grid.addColumn(PerformanceRow::getChosenBudget).setAutoWidth(true);

// optional alignment
        approvedCol.setTextAlign(ColumnTextAlign.END);
        revisedCol.setTextAlign(ColumnTextAlign.END);
        actualQ2Col.setTextAlign(ColumnTextAlign.END);
        projectedCol.setTextAlign(ColumnTextAlign.END);
        plannedCol.setTextAlign(ColumnTextAlign.END);

// =====================================================
// HEADER ROWS (SAFE ORDER)
// =====================================================
// 1) Create the DEFAULT (first-created) header row explicitly
        HeaderRow defaultRow = grid.appendHeaderRow();

// Put normal column headers here (no join on this row)
        defaultRow.getCell(detailsCol).setText("Details");
        defaultRow.getCell(approvedCol).setText("Approved Budget");
        defaultRow.getCell(revisedCol).setText("Revised Budget");
        defaultRow.getCell(actualQ2Col).setText("Actual Q2");
        defaultRow.getCell(projectedCol).setText("Projected Actuals");
        defaultRow.getCell(plannedCol).setText("Planned budget");

// 2) Create GROUP row above default row (join is allowed here)
// 2) Group row above (join allowed here)
        HeaderRow groupRow = grid.prependHeaderRow();
        groupRow.getCell(detailsCol).setText(""); // IMPORTANT: keep blank

// Join FY across the 4 previous-year columns
        HeaderCell fy2425 = groupRow.join(
                groupRow.getCell(approvedCol),
                groupRow.getCell(revisedCol),
                groupRow.getCell(actualQ2Col),
                groupRow.getCell(projectedCol)
        );
        fy2425.setText(previousBudget.getFinancialYear());

// Current FY over planned column
        groupRow.getCell(plannedCol).setText(comboBox2.getValue().getFinancialYear());

// 3) Optional units row below
        HeaderRow unitsRow = grid.appendHeaderRow();
        unitsRow.getCell(detailsCol).setText("");
        unitsRow.getCell(approvedCol).setText("UGX 000");
        unitsRow.getCell(revisedCol).setText("UGX 000");
        unitsRow.getCell(actualQ2Col).setText("UGX 000");
        unitsRow.getCell(projectedCol).setText("UGX 000");
        unitsRow.getCell(plannedCol).setText("UGX 000");

        TreeData<PerformanceRow> data = new TreeData<>();

        PerformanceRow directExpense = new PerformanceRow("DIRECT EXPENSES", true);

        data.addRootItems(directExpense);

        List<COA> listcodesCurBdgt = new ArrayList();
        List<COA> listcodesPrevBdgt = new ArrayList();
        List<COA> freightCodesCurBdgt = new ArrayList();
        List<COA> freightCodesPrevBdgt = new ArrayList();
        Set<String> class1codes = new HashSet<>();
        Set<String> CodesPrevBdgt = new HashSet<>();

        List<COA> listclass1codesCurBdgt = new ArrayList();
        List<COA> listclass1codesPrevBdgt = new ArrayList();

        Set<String> freightCodes = new HashSet<>();

        PerformanceRow fuel = new PerformanceRow("Fuel, lubricants & oils", true);
        data.addItem(directExpense, fuel);
        List<COA> fuelCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Fuels_Lubricant_Oil);
        List<COA> fuelCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Fuels_Lubricant_Oil);
        //class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : fuelCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(fuelCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(fuel, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(fuel, new PerformanceRow("TOTAL FUEL", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(fuel, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalFuelData(comboBox2.getValue())));
        PerformanceRow passengerServicesExp = new PerformanceRow("PASSENGER SERVICE EXPENSES", true);
        data.addItem(directExpense, passengerServicesExp);
        List<COA> rentCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Passenger_Service_Expenses);
        List<COA> rentCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Passenger_Service_Expenses);
        //class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : rentCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(rentCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(passengerServicesExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(passengerServicesExp, new PerformanceRow("TOTAL PASSENGER SERVICE EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(passengerServicesExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalPassengerServiceExpData(comboBox2.getValue())));
        // Passengers
        PerformanceRow passengers = new PerformanceRow("MAINTENANCE EXPENSES", true);
        data.addItem(directExpense, passengers);
        List<COA> passengersCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Maintenance);
        List<COA> passengersCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Maintenance);
        //class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : passengersCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(passengersCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(passengers, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(passengers, new PerformanceRow("TOTAL MAINTENANCE EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(passengers, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalMaintenanceData(comboBox2.getValue())));
        PerformanceRow utility_propertyExp = new PerformanceRow("UTILITY & PROPERTY EXPENSES", true);
        data.addItem(directExpense, utility_propertyExp);
        List<COA> miscellaneousCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Utility_Property_Expenses);
        List<COA> miscellaneousCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Utility_Property_Expenses);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : miscellaneousCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(miscellaneousCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(utility_propertyExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(utility_propertyExp, new PerformanceRow("TOTAL UTILITY & PROPERTY EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(utility_propertyExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalUtility_PropertyExpData(comboBox2.getValue())));

        PerformanceData datas = sampleBudgetItemsService.getTotalVariableCost(comboBox2.getValue());
        data.addItem(directExpense, new PerformanceRow("TOTAL VARIABLE COSTS", formatCurrencyB(datas.getPreviousBudgetApproved()), formatCurrencyB(datas.getPreviousBudgeActual().abs()), formatCurrencyB(datas.getChosenBudget())));
        data.addItem(directExpense, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), datas));

        PerformanceRow personnelCost = new PerformanceRow("PERSONNEL COSTS", true);
        data.addItem(directExpense, personnelCost);
        List<COA> personnelCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Personel_Costs);
        List<COA> personnelCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Personel_Costs);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : personnelCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(personnelCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(personnelCost, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(personnelCost, new PerformanceRow("TOTAL PERSONNEL COSTS", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(personnelCost, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalPersonnelExpData(comboBox2.getValue())));

        PerformanceRow adminExpe = new PerformanceRow("ADMINISTRATION EXPENSES", true);

        data.addRootItems(adminExpe);
        PerformanceRow generalExp = new PerformanceRow("GENERAL EXPENSES", true);
        data.addItem(adminExpe, generalExp);
        List<COA> adminCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.General_Expenses);
        List<COA> adminCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.General_Expenses);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : adminCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(adminCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(generalExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(generalExp, new PerformanceRow("TOTAL ADMINISTRATION EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(generalExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalAdminExpData(comboBox2.getValue())));

        PerformanceRow otherAdminExpe = new PerformanceRow("OTHER ADMINISTRATION EXPENSES", true);
        data.addRootItems(otherAdminExpe);

        PerformanceRow board_LegalExp = new PerformanceRow("BOARD & LEGAL EXPENSES", true);
        data.addItem(otherAdminExpe, board_LegalExp);
        List<COA> board_legalCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Board_Legal_Expenses);
        List<COA> board_legaCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Board_Legal_Expenses);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : board_legalCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(board_legaCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(board_LegalExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(board_LegalExp, new PerformanceRow("TOTAL OTHER ADMINISTRATION EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(board_LegalExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalLegal_BoardExpData(comboBox2.getValue())));

        PerformanceRow communicationExp = new PerformanceRow("COMMUNICATION EXPENSES", true);
        data.addItem(otherAdminExpe, communicationExp);
        List<COA> communicationCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Communication_Expenses);
        List<COA> communicationCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Communication_Expenses);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : communicationCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(communicationCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(communicationExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(communicationExp, new PerformanceRow("TOTAL COMMUNICATION EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(communicationExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalCommunicationsExpData(comboBox2.getValue())));

        PerformanceRow utilityExp = new PerformanceRow("UTILITY EXPENSES", true);
        data.addItem(otherAdminExpe, utilityExp);
        List<COA> utilityCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Utilities);
        List<COA> utilityCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Utilities);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : utilityCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(utilityCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(utilityExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(utilityExp, new PerformanceRow("TOTAL UTILITY EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(utilityExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalUtilitiesExpData(comboBox2.getValue())));

        PerformanceRow supplies_ServicesExp = new PerformanceRow("SUPPLIES & SERVICES EXPENSES", true);
        data.addItem(otherAdminExpe, supplies_ServicesExp);
        List<COA> supplies_ServicesCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Supplies_Services);
        List<COA> supplies_ServicesCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Supplies_Services);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : supplies_ServicesCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(supplies_ServicesCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(supplies_ServicesExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(supplies_ServicesExp, new PerformanceRow("TOTAL SUPPLIES & SERVICES EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(supplies_ServicesExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalSupply_ServicesExpData(comboBox2.getValue())));

        PerformanceRow professional_ServicesExp = new PerformanceRow("PROFESSIONAL SERVICES EXPENSES", true);
        data.addItem(otherAdminExpe, professional_ServicesExp);
        List<COA> professional_ServicesCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Professional_Services);
        List<COA> professional_ServicesCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Professional_Services);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : professional_ServicesCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(professional_ServicesCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(professional_ServicesExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(professional_ServicesExp, new PerformanceRow("TOTAL PROFESSIONAL SERVICES EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(professional_ServicesExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalProfessional_ServicesExpData(comboBox2.getValue())));

        PerformanceRow insuranceExp = new PerformanceRow("INSURANCE & LICENSES EXPENSES", true);
        data.addItem(otherAdminExpe, insuranceExp);
        List<COA> insuranceCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Insurance_Licenses);
        List<COA> insuranceCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Insurance_Licenses);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : insuranceCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(insuranceCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(insuranceExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode())), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(insuranceExp, new PerformanceRow("TOTAL INSURANCE & LICENSES EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(insuranceExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalInsurance_LicensesExpData(comboBox2.getValue())));

        PerformanceRow transportExp = new PerformanceRow("TRAVEL & TRANSPORT EXPENSES", true);
        data.addItem(otherAdminExpe, transportExp);
        List<COA> transportCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Insurance_Licenses);
        List<COA> transportCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Insurance_Licenses);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : transportCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(transportCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(transportExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(transportExp, new PerformanceRow("TOTAL TRAVEL & TRANSPORT EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(transportExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalTravel_TransportData(comboBox2.getValue())));

        PerformanceRow miscellanousExp = new PerformanceRow("MISCELLANOUS OTHER EXPENSES", true);
        data.addItem(otherAdminExpe, miscellanousExp);
        List<COA> miscellanousCodeCurBdgt = sampleCoaService.findByBudgetAndClass2(comboBox2.getValue(), Classification2.Miscellaneous_Other_Expenses);
        List<COA> miscellanousCodePrevBdgt = sampleCoaService.findByBudgetAndClass2(previousBudget, Classification2.Miscellaneous_Other_Expenses);
        // class1codes.clear();
        listcodesPrevBdgt.clear();
        listcodesCurBdgt.clear();
        CodesPrevBdgt.clear();
        for (COA coa : miscellanousCodeCurBdgt) {
            COA coaPrev = findByCodeFromList(miscellanousCodePrevBdgt, coa.getCode());
            listcodesCurBdgt.add(coa);
            listcodesPrevBdgt.add(coaPrev);
            class1codes.add(coa.getCode());
            CodesPrevBdgt.add(coaPrev.getCode());
            freightCodes.add(coa.getCode());
            listclass1codesCurBdgt.add(coa);
            listclass1codesPrevBdgt.add(coaPrev);
            data.addItem(miscellanousExp, new PerformanceRow(coa.getName(), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, coaPrev)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCode(previousBudget, coa.getCode()).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), coa))));
        }

        data.addItem(miscellanousExp, new PerformanceRow("TOTAL MISCELLANOUS OTHER EXPENSES", formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(previousBudget, listcodesPrevBdgt)), formatCurrencyB(samopleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(previousBudget), CodesPrevBdgt).abs()), formatCurrencyB(sampleBudgetItemsService.totalBudgetByCode(comboBox2.getValue(), listcodesCurBdgt))));
        data.addItem(miscellanousExp, sampleBudgetItemsService.getExpPercOnOperExpense(comboBox2.getValue(), sampleBudgetItemsService.getTotalMiscellanousOtherExpData(comboBox2.getValue())));

        grid.setTreeData(data);

        //grid.expand(directExpense, fuel, passengerServicesExp, passengers, miscellanousExp, passengers);
        H2 head = new H2("EXPENDITURE PERFORMANCE");

        dataLayout.add(head, grid);

    }

    public COA findByCodeFromList(List<COA> list, String code) {
        return list.stream()
                .filter(coa -> code.equals(coa.getCode()))
                .findFirst()
                .orElse(null);
    }

    private static boolean isSectionRow(PerformanceRow row) {
        String label = row.getLabel();
        if (label == null) {
            return false;
        }
        label = label.trim();
        return "FREIGHT SERVICES".equals(label)
                || "RECURRENT INCOME".equals(label)
                || "Northern Route".equals(label)
                || "Southern Route".equals(label)
                || label.startsWith("Total")
                || label.startsWith("TOTAL");
    }

    private String fyLabel(Budget budget, String fallback) {
        return budget != null && budget.getFinancialYear() != null
                ? budget.getFinancialYear()
                : fallback;
    }

    public void setFundSourcedataLayout() {
        datasubLayout.removeAll();
        dataLayout.removeAll();

        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
            return;
        }

        fundSourceGrid = new Grid<>(Organisation.class, false);

        // Columns
        Grid.Column<Organisation> fundSourceCol
                = fundSourceGrid.addColumn(Organisation::getName)
                        .setHeader("Fund Source")
                        .setWidth("250px")
                        .setFlexGrow(0);

        List<CustomDetailedBudgetReport> reps
                = sampleCustomDetailedBudgetReportService
                        .findByBudgetreport(sampleCustomDetailedBudgetReportImp);

        Set<UrcDeptSectionAnlDimbgt> sections
                = sampleCustomDetailedBudgetReportService.getCombinedDeptSections(reps);

        // TOTAL INCOME (ALL FUND SOURCES)
        BigDecimal grandTotalIncome
                = sampleBudgetItemsService
                        .calculateTotalByBudgetAndBudgetTypesByIncome(
                                comboBox2.getValue(), sections);

        // Amount column
        Grid.Column<Organisation> amountCol
                = fundSourceGrid.addColumn(report -> {
                    BigDecimal amount
                            = sampleBudgetItemsService
                                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                                            comboBox2.getValue(), sections, report);

                    return formatCurrency(amount);
                }).setHeader("Amount / UGX");

        // Percentage column
        Grid.Column<Organisation> percentageCol
                = fundSourceGrid.addColumn(report -> {
                    BigDecimal amount
                            = sampleBudgetItemsService
                                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                                            comboBox2.getValue(), sections, report);

                    return calculatePercentage(amount, grandTotalIncome);
                }).setHeader("%age Contribution");

        // Themes
        fundSourceGrid.addThemeVariants(
                GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_ROW_STRIPES
        );

        List<Organisation> filteredItems = budgetType2.getSelectedItems().stream()
                .filter(org -> {
                    BigDecimal amount = sampleBudgetItemsService
                            .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                                    comboBox2.getValue(), sections, org);
                    return amount != null && amount.compareTo(BigDecimal.ZERO) != 0;
                })
                .toList();

        fundSourceGrid.setItems(filteredItems);

// ================= FOOTER =================
        BigDecimal footerTotalAmount = budgetType2.getSelectedItems().stream()
                .map(org -> sampleBudgetItemsService
                .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                        comboBox2.getValue(), sections, org))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String footerPercentage
                = calculatePercentage(footerTotalAmount, grandTotalIncome);

        FooterRow footerRow = fundSourceGrid.appendFooterRow();
        footerRow.getCell(fundSourceCol).setText("TOTAL");
        footerRow.getCell(amountCol).setText(formatCurrency(footerTotalAmount));
        footerRow.getCell(percentageCol).setText(footerPercentage);

        dataLayout.add(fundSourceGrid);
        try {
            generateFundSourcePdf(filteredItems, sections, grandTotalIncome);
            generateFundSourceIncomeExcel(filteredItems, sections, grandTotalIncome);
            dataLayout.add(datasubLayout);

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setFundSourceExpdataLayout() {
        datasubLayout.removeAll();
        dataLayout.removeAll();
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            fundSourceGrid = new Grid<>(Organisation.class, false);
            Grid.Column<Organisation> fundSourceCol
                    = fundSourceGrid.addColumn(Organisation::getName).setHeader("Fund Source").setWidth("250px").setFlexGrow(0);

            List<CustomDetailedBudgetReport> reps = sampleCustomDetailedBudgetReportService.findByBudgetreport(sampleCustomDetailedBudgetReportImp);
            Set<UrcDeptSectionAnlDimbgt> sections = sampleCustomDetailedBudgetReportService.getCombinedDeptSections(reps);

            BigDecimal totalIncome = sampleBudgetItemsService.calculateTotalByBudgetAndBudgetTypesByIncome(comboBox2.getValue(), sections);
            Grid.Column<Organisation> amountCol = fundSourceGrid.addColumn(report -> {
                this.totalIncome = sampleBudgetItemsService.calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(comboBox2.getValue(), sections, report);
                return formatCurrency(this.totalIncome);
            }).setHeader("Revenue/UGX");
            Grid.Column<Organisation> budgetCol = fundSourceGrid.addColumn(report -> {
                totalExp = sampleBudgetItemsService.calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByExp(comboBox2.getValue(), sections, report);
                return formatCurrency(totalExp);
            }).setHeader("Budget/UGX");
            Grid.Column<Organisation> perCol = fundSourceGrid.addColumn(report -> calculatePercentage(this.totalIncome, totalIncome)).setHeader("%age Contibution");
            fundSourceGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

            BigDecimal footerTotalAmount = budgetType2.getSelectedItems().stream()
                    .map(org -> sampleBudgetItemsService
                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                            comboBox2.getValue(), sections, org))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal footerTotalBudget = budgetType2.getSelectedItems().stream()
                    .map(org -> sampleBudgetItemsService
                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByExp(
                            comboBox2.getValue(), sections, org))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String footerPercentage
                    = calculatePercentage(footerTotalAmount, totalIncome);

            FooterRow footerRow = fundSourceGrid.appendFooterRow();
            footerRow.getCell(fundSourceCol).setText("TOTAL");
            footerRow.getCell(amountCol).setText(formatCurrency(footerTotalAmount));
            footerRow.getCell(budgetCol).setText(formatCurrency(footerTotalBudget));
            footerRow.getCell(perCol).setText(footerPercentage);

            List<Organisation> filteredItems = budgetType2.getSelectedItems().stream()
                    .filter(org -> {
                        BigDecimal amount = sampleBudgetItemsService
                                .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                                        comboBox2.getValue(), sections, org);
                        return amount != null && amount.compareTo(BigDecimal.ZERO) != 0;
                    })
                    .toList();

            fundSourceGrid.setItems(filteredItems);
            dataLayout.add(fundSourceGrid);
            try {
                generateFundSourceExpPdf(filteredItems, sections, totalIncome);
                dataLayout.add(datasubLayout);
            } catch (IOException ex) {
                Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return formatter.format(amount);
    }

    private String formatCurrencyB(BigDecimal amount) {

        if (amount == null) {
            return "0";
        }

        BigDecimal thousands = amount
                .divide(BigDecimal.valueOf(1000), 1, RoundingMode.HALF_UP);

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        formatter.setMinimumFractionDigits(1);

        return formatter.format(thousands);
    }

    public String calculatePercentage(
            BigDecimal value,
            BigDecimal total) {
        DecimalFormat PERCENT_FORMAT
                = new DecimalFormat("#,##0.00'%'");
        if (value == null || total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00%";
        }

        BigDecimal percentage = value
                .multiply(BigDecimal.valueOf(100))
                .divide(total, 4, RoundingMode.HALF_UP);

        return PERCENT_FORMAT.format(percentage.abs());
    }

    private String formatDeptSection(CustomDetailedBudgetReport report) {
        if (report != null) {
            return report.getDeptsection().stream()
                    .map(UrcDeptSectionAnlDimbgt::getNAME)
                    .collect(Collectors.joining(", "));
        }
        return "";
    }

    private List<PerformanceRow> flattenTreeData(TreeData<PerformanceRow> treeData) {
        List<PerformanceRow> rows = new ArrayList<>();
        for (PerformanceRow root : treeData.getRootItems()) {
            collect(rows, treeData, root);
        }
        return rows;
    }

    private void collect(List<PerformanceRow> rows, TreeData<PerformanceRow> treeData, PerformanceRow parent) {
        rows.add(parent);
        for (PerformanceRow child : treeData.getChildren(parent)) {
            collect(rows, treeData, child);
        }
    }

    public byte[] generateVolumesExcel(List<PerformanceRow> rows,
            String prevFy,
            String currFy) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Volumes");

        int rowIdx = 0;

        // Header
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Details");
        header.createCell(1).setCellValue(prevFy + " Approved (MTs)");
        header.createCell(2).setCellValue(prevFy + " Actual (MTs)");
        header.createCell(3).setCellValue(currFy + " Budget (MTs)");

        for (PerformanceRow r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(nullSafe(r.getLabel()));
            row.createCell(1).setCellValue(nullSafe(r.getPreviousBudgetApproved()));
            row.createCell(2).setCellValue(nullSafe(r.getPreviousBudgeActual()));
            row.createCell(3).setCellValue(nullSafe(r.getChosenBudget()));
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    private String nullSafe(String v) {
        return v == null ? "" : v;
    }

    private void refreshCustomDetailedBudgetReportImpItems() {
        gridCustomDetailedBudgetReportImp.deselectAll();
        gridCustomDetailedBudgetReportImp.setItems(sampleCustomDetailedBudgetReportImpService.getAllReportsImpByUser(user));
        CustomDetailedBudgetReportImpcomboBox.setItems(sampleCustomDetailedBudgetReportImpService.getAllReportsImpByUser(user));

    }

    private void refreshCustomDetailedBudgetReportItems(CustomDetailedBudgetReportImp budgetreport) {
        gridCustomDetailedBudgetReport.deselectAll();
        gridCustomDetailedBudgetReport.setItems(sampleCustomDetailedBudgetReportService.findByBudgetreport(budgetreport));
    }

    private void handleSummaryBudgetClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (sections.isEmpty() || comboBox.isEmpty() || budgetType.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            exportAndDownloadSummaryBudget(comboBox.getValue());
        }
    }

    private void handleQtrSummaryBudgetClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (sections.isEmpty() || comboBox.isEmpty() || budgetType.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            exportAndDownloadSummaryBudgetQtr(comboBox.getValue());
        }
    }

    private void handleActualsQtrSummaryBudgetClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (sections.isEmpty() || comboBox.isEmpty() || budgetType.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            exportAndDownloadSummaryBudgetQtrActuals(comboBox.getValue());
        }
    }

    private void handleActivityDetailClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (sections.isEmpty() || comboBox.isEmpty() || budgetType.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            exportAndDownloadActvityBudgetItems2(comboBox.getValue());
        }
    }

    private void handleAccountCodeDetailClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (sections.isEmpty() || comboBox.isEmpty() || budgetType.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            exportAndDownloadAccountcodeBudgetItems2(comboBox.getValue());
        }
    }

    private void handleCustomAccountCodeDetailClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty 444");
        } else {
            exportAndDownloadCustomAccountcodeBudgetItems2(comboBox2.getValue());
        }
    }

    private void handleCustomAccountCodeDetailWordClick() {
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty 444");
        } else {
            exportAndDownloadCustomAccountcodeBudgetWord2(comboBox2.getValue());
        }
    }

    private void handleCustomActivitySummaryPDFClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            CustomDetailedBudgetReportImp rep = CustomDetailedBudgetReportImpcomboBox.getValue();
            createOpenBudgetSummaryPDFReport(comboBox2.getValue(), rep.getReportname());
        }
    }

    private void handleBudgetSummaryByAccountCodeClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            CustomDetailedBudgetReportImp rep = CustomDetailedBudgetReportImpcomboBox.getValue();
            //gridCustomDetailedBudgetReport.
            createOpenBudgetSummaryReport(comboBox2.getValue(), rep.getReportname());
        }
    }

    private void handleCustomActivityByCOASummaryPDFClick() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            CustomDetailedBudgetReportImp rep = CustomDetailedBudgetReportImpcomboBox.getValue();
            createOpenBudgetSummaryByActivityByCOAExcelReport(comboBox2.getValue(), rep.getReportname());
        }
    }

    private void handleDepartmentalProcurementPlan() {
        // Handle the click event here, e.g., show a notification or navigate to another view
        if (comboBox2.isEmpty() || budgetType2.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
        } else {
            createDepartmentalProcurementPlanExcelReport(comboBox2.getValue(), "Procurement Plan");
        }
    }

    private void handleCustomActivityByCOASummaryWordClick() {
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || CustomDetailedBudgetReportImpcomboBox.isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty");
            return;
        }

        try {
            List<CustomDetailedBudgetReport> reports
                    = sampleCustomDetailedBudgetReportService.findByBudgetreport(sampleCustomDetailedBudgetReportImp);

            Set<UrcDeptSectionAnlDimbgt> combinedSections
                    = sampleCustomDetailedBudgetReportService.getCombinedDeptSections(reports);

            List<DepartmentBudget> departmentBudgets
                    = budgetService.getDepartmentBudgets(comboBox2.getValue()).stream()
                            .filter(dept -> dept != null
                            && dept.getSections() != null
                            && !Collections.disjoint(dept.getSections(), combinedSections))
                            .toList();

            byte[] file = departmentWorkplanWordExportService.exportDepartmentWorkplanDocx(
                    comboBox2.getValue(),
                    departmentBudgets,
                    budgetType2.getSelectedItems()
            );

            String name = CustomDetailedBudgetReportImpcomboBox.getValue().getReportname();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            StreamResource resource = new StreamResource(
                    name + "_" + timestamp + "_department-workplans.docx",
                    () -> new ByteArrayInputStream(file)
            );
            resource.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            resource.setCacheTime(0);

            Anchor download = new Anchor(resource, "");
            download.getElement().setAttribute("download", true);
            add(download);
            download.getElement().callJsFunction("click");

            getUI().ifPresent(ui
                    -> ui.getPage().executeJs("setTimeout(() => $0.remove(), 1500)", download.getElement())
            );

        } catch (Exception e) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, e);
            warningNotification("Failed to generate Word file");
        }
    }

    private void exportAndDownloadCustomAccountcodeBudgetWord2(Budget budget) {
        try {
            Set<UrcDeptSectionAnlDimbgt> sect = sampleCustomDetailedBudgetReportService
                    .findByBudgetreport(CustomDetailedBudgetReportImpcomboBox.getValue())
                    .stream()
                    .flatMap(r -> r.getDeptsection().stream())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            StreamResource resource = new StreamResource(
                    "Detailed_Account_Code_Report.docx",
                    () -> new ByteArrayInputStream(
                            budgetWordReportService.generateDetailedAccountCodeQuarterWordReport(
                                    budget,
                                    sect,
                                    budgetType2.getSelectedItems(),
                                    HeaderExcel2("ACCOUNT CODE DETAIL")
                            )
                    )
            );

            Anchor anchor = new Anchor(resource, "");
            anchor.getElement().setAttribute("download", true);
            anchor.getStyle().set("display", "none");
            add(anchor);
            anchor.getElement().executeJs("this.click()");
        } catch (Exception e) {
            e.printStackTrace();
            warningNotification("Failed to generate Word document");
        }
    }

    private void createOpenBudgetSummaryByActivityByCOAExcelReport(Budget budget, String name) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(dtf);

            List<CustomDetailedBudgetReport> reports
                    = sampleCustomDetailedBudgetReportService.findByBudgetreport(sampleCustomDetailedBudgetReportImp);

            Set<UrcDeptSectionAnlDimbgt> combinedSections
                    = sampleCustomDetailedBudgetReportService.getCombinedDeptSections(reports);

            List<DepartmentBudget> departmentBudgets
                    = budgetService.getDepartmentBudgets(budget).stream()
                            .filter(dept -> dept != null
                            && dept.getSections() != null
                            && !Collections.disjoint(dept.getSections(), combinedSections))
                            .toList();

            byte[] file = generateDepartmentWorkplansExcel(
                    budget,
                    budgetType2.getSelectedItems(),
                    departmentBudgets
            );

            StreamResource resource = new StreamResource(
                    name + "_" + timestamp + "_department-workplans.xlsx",
                    () -> new ByteArrayInputStream(file)
            );

            resource.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resource.setCacheTime(0);

            Anchor download = new Anchor(resource, "");
            download.getElement().setAttribute("download", true);
            add(download);

            download.getElement().callJsFunction("click");

            getUI().ifPresent(ui
                    -> ui.getPage().executeJs(
                            "setTimeout(() => $0.remove(), 1500)",
                            download.getElement()
                    )
            );

        } catch (Exception e) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, e);
            warningNotification("Failed to generate Excel file");
        }
    }

    private void createDepartmentalProcurementPlanExcelReport(Budget budget, String name) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(dtf);

            List<DepartmentBudget> departmentBudgets = budgetService.getDepartmentBudgetsWithoutBudget(budget);

            byte[] file = generateProcurementPlansExcel(
                    budget,
                    budgetType2.getSelectedItems(),
                    departmentBudgets
            );

            StreamResource resource = new StreamResource(
                    name + "_" + timestamp + "_procurementPlan.xlsx",
                    () -> new ByteArrayInputStream(file)
            );

            resource.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resource.setCacheTime(0);

            Anchor download = new Anchor(resource, "");
            download.getElement().setAttribute("download", true);
            add(download);

            download.getElement().callJsFunction("click");

            getUI().ifPresent(ui
                    -> ui.getPage().executeJs(
                            "setTimeout(() => $0.remove(), 1500)",
                            download.getElement()
                    )
            );

        } catch (Exception e) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, e);
            warningNotification("Failed to generate Excel file");
        }
    }

    private void createOpenBudgetSummaryByActivityByCOAPDFReport(Budget budget, String name) {
        BudgetReportGeneratorPDF generate = new BudgetReportGeneratorPDF(
                sampleUrc_ActivitiesService,
                sampleCustomDetailedBudgetReportService,
                sampleBudgetItemsService, budgetType2.getValue()
        );
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(dtf);
        StreamResource resource = new StreamResource(name + "_" + timestamp + " budget-coa-report.pdf", () -> {
            PipedInputStream inputStream = new PipedInputStream();
            try {
                PipedOutputStream outputStream = new PipedOutputStream(inputStream);
                new Thread(() -> {
                    try {
                        generate.streamCustomBudgetByActivityByCOAPdfReport(
                                budget,
                                sampleCustomDetailedBudgetReportImp,
                                outputStream
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
                return new ByteArrayInputStream(new byte[0]);
            }

            return inputStream;
        });

        Anchor download = new Anchor(resource, "Download PDF");
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
    }

    private void createOpenBudgetSummaryReport(Budget budget, String name) {
        BudgetReportGeneratorPDF generate = new BudgetReportGeneratorPDF(
                sampleUrc_ActivitiesService,
                sampleCustomDetailedBudgetReportService,
                sampleBudgetItemsService, budgetType2.getValue()
        );

        List<CustomDetailedBudgetReport> summaryBudget
                = sampleCustomDetailedBudgetReportService.findByBudgetreport(sampleCustomDetailedBudgetReportImp);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(dtf);

        StreamResource resource = new StreamResource(
                name + "_" + timestamp + "_Summary_budget-coa-report.pdf",
                () -> {
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        generate.buildBudgetSummaryTable(summaryBudget, budget, budgetType2.getValue(), baos);
                        return new ByteArrayInputStream(baos.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ByteArrayInputStream(new byte[0]);
                    }
                });

        resource.setContentType("application/pdf");
        resource.setCacheTime(0);

        Anchor download = new Anchor(resource, "Download PDF");
        download.getElement().setAttribute("download", true);
        download.getElement().setAttribute("target", "_blank");
        add(download);

        download.getElement().callJsFunction("click");

        // Remove anchor after 2 seconds
        getUI().ifPresent(ui -> ui.getPage().executeJs(
                "setTimeout(() => $0.remove(), 2000)", download.getElement()
        ));
    }

    private void createOpenBudgetSummaryPDFReport(Budget budget, String name) {
        BudgetReportGeneratorPDF generate = new BudgetReportGeneratorPDF(
                sampleUrc_ActivitiesService,
                sampleCustomDetailedBudgetReportService,
                sampleBudgetItemsService, budgetType2.getValue()
        );
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(dtf);
        StreamResource resource = new StreamResource(name + "_" + timestamp + " budget-report.pdf", () -> {
            PipedInputStream inputStream = new PipedInputStream();
            try {
                PipedOutputStream outputStream = new PipedOutputStream(inputStream);
                new Thread(() -> {
                    try {
                        generate.streamCustomBudgetSummaryPdfReport(
                                budget,
                                sampleCustomDetailedBudgetReportImp,
                                outputStream
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
                return new ByteArrayInputStream(new byte[0]);
            }

            return inputStream;
        });

        Anchor download = new Anchor(resource, "Download PDF");
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
    }

    public Notification warningNotification(String warning) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

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
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }

    private void createHeaderAndBodyActivitiesByBudgetRow(Workbook workbook, Sheet sheet) {
        List<Coalevel1> coaList = new ArrayList();
        List<Integer> activityrowIndex = new ArrayList();
        List<Integer> catrowIndex = new ArrayList();
        List<Integer> totalrowIndex = new ArrayList();
        sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
        //sheet.setFitToPage(true);
        //sheet.setHorizontallyCenter(true);
        short rowHeight = 500; // Adjust the height as needed
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);

        // Create a cell style with the specified font
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);

// Create a bold and centered style with a bottom border
        CellStyle boldCenteredStyle = workbook.createCellStyle();
        boldCenteredStyle.cloneStyleFrom(cellStyle);
        boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCenteredStyle.setFont(boldFont);
        boldCenteredStyle.setBorderBottom(BorderStyle.THIN);
        Row headerRow = sheet.createRow(0);
        //int columnWidth = 10000; // Adjust the width as needed
        //sheet.setColumnWidth(0, columnWidth);
        short tr = 2;

        try {
            // Add an image to the header
            // short rowHeight = (short) (getImageHeight("/META-INF/resources/images/urclogo.png") + 50); // Add some padding
            //System.out.println(rowHeight);
            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 24);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        Row header2 = sheet.createRow(1);
        Cell header2Cell = header2.createCell(0);
        header2Cell.setCellValue(HeaderExcel("ACTIVITY").toUpperCase());
        CellRangeAddress cellRange2 = new CellRangeAddress(1, 1, 0, 24);
        sheet.addMergedRegion(cellRange2);
        setBottomBorderForRegion(sheet, cellRange2);
        Row Q2 = sheet.createRow((short) tr);
        Q2.createCell((short) 0).setCellValue("ACTIVITY CODE");
        Q2.createCell((short) 1).setCellValue("COA CODE");
        Q2.createCell((short) 2).setCellValue("ITEM");
        Q2.createCell((short) 3).setCellValue("COST");
        Q2.createCell((short) 4).setCellValue("QTY");
        Q2.createCell((short) 5).setCellValue("Unit Measure");
        Q2.createCell((short) 6).setCellValue("CUR");
        Q2.createCell((short) 7).setCellValue("TOTAL");
        Q2.createCell((short) 8).setCellValue("JUL/UGX");
        Q2.createCell((short) 9).setCellValue("AUG/UGX");
        Q2.createCell((short) 10).setCellValue("SEP/UGX");
        Q2.createCell((short) 11).setCellValue("OCT/UGX");
        Q2.createCell((short) 12).setCellValue("NOV/UGX");
        Q2.createCell((short) 13).setCellValue("DEC/UGX");
        Q2.createCell((short) 14).setCellValue("JAN/UGX");
        Q2.createCell((short) 15).setCellValue("FEB/UGX");
        Q2.createCell((short) 16).setCellValue("MAR/UGX");
        Q2.createCell((short) 17).setCellValue("APR/UGX");
        Q2.createCell((short) 18).setCellValue("MAY/UGX");
        Q2.createCell((short) 19).setCellValue("JUN/UGX");
        Q2.createCell((short) 20).setCellValue("Number of Days");
        Q2.createCell((short) 21).setCellValue("Target Group");
        Q2.createCell((short) 22).setCellValue("Trainer");
        Q2.createCell((short) 23).setCellValue("Notes");
        Q2.createCell((short) 24).setCellValue("Unit");
        Coalevel1 coal = sampleCoalevel1Service.findByCode(1);

        List<UrcDeptSectionAnlDimbgt> selectedSections = sections.getSelectedItems().stream().toList();
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBox.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("INCOME");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRange);
            setBottomBorderForRegion(sheet, cellRange);
            budgetList = sampleBudgetItemsService.findByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems());

            for (BudgetItems tt : budgetList) {

                tr++;
                Row mm = sheet.createRow((short) tr);
                mm.createCell((short) 0).setCellValue("");
                mm.createCell((short) 1).setCellValue(tt.getCoacode().getName() + " (" + tt.getCoacode().getCode().trim() + ")");
                mm.createCell((short) 2).setCellValue(tt.getItem());
                mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQty()));
                mm.createCell((short) 5).setCellValue(tt.getUnitMeasure());
                mm.createCell((short) 6).setCellValue(tt.getCurrency().getData().getCurrencyShort());
                mm.createCell((short) 7).setCellValue(decimalFormat.format(calculateSumOfMonths(tt)));
                mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getJul()));
                mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getAug()));
                mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getSep()));
                mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getOct()));
                mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getNov()));
                mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getDec()));
                mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getJan()));
                mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getFeb()));
                mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getMar()));
                mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getApr()));
                mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getMay()));
                mm.createCell((short) 19).setCellValue(decimalFormat.format(tt.getJun()));
                mm.createCell((short) 20).setCellValue(tt.getNo_of_days());
                mm.createCell((short) 21).setCellValue(tt.getTarget_group());
                mm.createCell((short) 22).setCellValue(tt.getExpected_trainer());
                mm.createCell((short) 23).setCellValue(tt.getNotes());
                mm.createCell((short) 24).setCellValue(tt.getDeptUnit().getNAME());

            }

            tr++;
            Row incometotal2 = sheet.createRow((short) tr);
            incometotal2.createCell(0).setCellValue("TOTAL INCOME");
            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT);
            setBottomTopBorderForRegion(sheet, cellRangeT);
            incometotal2.createCell(7).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems())));
            incometotal2.createCell(8).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul")));
            incometotal2.createCell(9).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "aug")));
            incometotal2.createCell(10).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "sep")));
            incometotal2.createCell(11).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "oct")));
            incometotal2.createCell(12).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "nov")));
            incometotal2.createCell(13).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "dec")));
            incometotal2.createCell(14).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jan")));
            incometotal2.createCell(15).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "feb")));
            incometotal2.createCell(16).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "mar")));
            incometotal2.createCell(17).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "apr")));
            incometotal2.createCell(18).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "may")));
            incometotal2.createCell(19).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun")));
            incometotal2.createCell(20).setCellValue("");
            incometotal2.createCell(21).setCellValue("");
            incometotal2.createCell(22).setCellValue("");
            incometotal2.createCell(23).setCellValue("");
            incometotal2.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);
        }
        coal = sampleCoalevel1Service.findByCode(2);
        coaList.add(coal);
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBox.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("REVENUE EXPENDITURE");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRangeREVENUE = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRangeREVENUE);
            setBottomBorderForRegion(sheet, cellRangeREVENUE);
            programmes = sampleURC_Priority_AreasService.findByBudget(comboBox.getValue());
            for (URC_Priority_Areas prog : programmes) {

                programmesActivities = sampleUrc_ActivitiesService.findActivitiesByPriorityAreas(prog);
                if (sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsAndActivitiesGreaterThanZero(comboBox.getValue(), coal, selectedSections, programmesActivities, budgetType.getSelectedItems()) == true) {

                    for (Urc_Activities d : programmesActivities) {
                        if (sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems()) == true) {

                            tr++;
                            Row rowin = sheet.createRow((short) tr);
                            rowin.createCell(0).setCellValue(("[" + prog.getName() + "] " + d.getName()).toUpperCase());
                            activityrowIndex.add((int) tr);
                            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
                            sheet.addMergedRegion(cellRange);
                            setBottomBorderForRegion(sheet, cellRange);
                            budgetList = sampleBudgetItemsService.findBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems());

                            for (BudgetItems tt : budgetList) {

                                tr++;
                                Row mm = sheet.createRow((short) tr);
                                mm.createCell((short) 0).setCellValue(tt.getActivity().getActivityCode());
                                mm.createCell((short) 1).setCellValue(tt.getCoacode().getName() + " (" + tt.getCoacode().getCode().trim() + ")");
                                mm.createCell((short) 2).setCellValue(tt.getItem());
                                mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                                mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQty()));
                                mm.createCell((short) 5).setCellValue(tt.getUnitMeasure());
                                mm.createCell((short) 6).setCellValue(tt.getCurrency().getData().getCurrencyShort());
                                mm.createCell((short) 7).setCellValue(decimalFormat.format(calculateSumOfMonths(tt)));
                                mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getJul()));
                                mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getAug()));
                                mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getSep()));
                                mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getOct()));
                                mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getNov()));
                                mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getDec()));
                                mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getJan()));
                                mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getFeb()));
                                mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getMar()));
                                mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getApr()));
                                mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getMay()));
                                mm.createCell((short) 19).setCellValue(decimalFormat.format(tt.getJun()));
                                mm.createCell((short) 20).setCellValue(tt.getNo_of_days());
                                mm.createCell((short) 21).setCellValue(tt.getTarget_group());
                                mm.createCell((short) 22).setCellValue(tt.getExpected_trainer());
                                mm.createCell((short) 23).setCellValue(tt.getNotes());
                                mm.createCell((short) 24).setCellValue(tt.getDeptUnit().getNAME());

                            }
                            tr++;
                            Row incometotal = sheet.createRow((short) tr);
                            incometotal.createCell(0).setCellValue("TOTAL");
                            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
                            sheet.addMergedRegion(cellRangeT);
                            setBottomTopBorderForRegion(sheet, cellRangeT);
                            incometotal.createCell(7).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems())));
                            incometotal.createCell(8).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "jul")));
                            incometotal.createCell(9).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "aug")));
                            incometotal.createCell(10).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "sep")));
                            incometotal.createCell(11).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "oct")));
                            incometotal.createCell(12).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "nov")));
                            incometotal.createCell(13).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "dec")));
                            incometotal.createCell(14).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "jan")));
                            incometotal.createCell(15).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "feb")));
                            incometotal.createCell(16).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "mar")));
                            incometotal.createCell(17).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "apr")));
                            incometotal.createCell(18).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "may")));
                            incometotal.createCell(19).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "jun")));
                            incometotal.createCell(20).setCellValue("");
                            incometotal.createCell(21).setCellValue("");
                            incometotal.createCell(22).setCellValue("");
                            incometotal.createCell(23).setCellValue("");
                            incometotal.createCell(24).setCellValue("");
                            totalrowIndex.add((int) tr);
                        }
                    }
                }
            }
            tr++;
            Row incometotal2 = sheet.createRow((short) tr);
            incometotal2.createCell(0).setCellValue("TOTAL REVENUE EXPENDITURE");
            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT);
            setBottomTopBorderForRegion(sheet, cellRangeT);
            incometotal2.createCell(7).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems())));
            incometotal2.createCell(8).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul")));
            incometotal2.createCell(9).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "aug")));
            incometotal2.createCell(10).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "sep")));
            incometotal2.createCell(11).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "oct")));
            incometotal2.createCell(12).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "nov")));
            incometotal2.createCell(13).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "dec")));
            incometotal2.createCell(14).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jan")));
            incometotal2.createCell(15).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "feb")));
            incometotal2.createCell(16).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "mar")));
            incometotal2.createCell(17).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "apr")));
            incometotal2.createCell(18).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "may")));
            incometotal2.createCell(19).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun")));
            incometotal2.createCell(20).setCellValue("");
            incometotal2.createCell(21).setCellValue("");
            incometotal2.createCell(22).setCellValue("");
            incometotal2.createCell(23).setCellValue("");
            incometotal2.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);
        }
        coal = sampleCoalevel1Service.findByCode(3);
        coaList.add(coal);
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBox.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("CAPITAL EXPENDITURE");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRangecaps = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRangecaps);
            setBottomBorderForRegion(sheet, cellRangecaps);
            programmes = sampleURC_Priority_AreasService.findByBudget(comboBox.getValue());
            for (URC_Priority_Areas prog : programmes) {

                programmesActivities = sampleUrc_ActivitiesService.findActivitiesByPriorityAreas(prog);
                if (sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsAndActivitiesGreaterThanZero(comboBox.getValue(), coal, selectedSections, programmesActivities, budgetType.getSelectedItems()) == true) {

                    for (Urc_Activities d : programmesActivities) {
                        if (sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems()) == true) {

                            tr++;
                            Row rowin = sheet.createRow((short) tr);
                            activityrowIndex.add((int) tr);
                            rowin.createCell(0).setCellValue(("[" + prog.getName() + "] " + d.getName()).toUpperCase());
                            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
                            sheet.addMergedRegion(cellRange);
                            setBottomBorderForRegion(sheet, cellRange);

                            budgetList = sampleBudgetItemsService.findBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems());

                            for (BudgetItems tt : budgetList) {

                                tr++;
                                Row mm = sheet.createRow((short) tr);
                                mm.createCell((short) 0).setCellValue(tt.getActivity().getActivityCode());
                                mm.createCell((short) 1).setCellValue(tt.getCoacode().getName() + " (" + tt.getCoacode().getCode().trim() + ")");
                                mm.createCell((short) 2).setCellValue(tt.getItem());
                                mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                                mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQty()));
                                mm.createCell((short) 5).setCellValue(tt.getUnitMeasure());
                                mm.createCell((short) 6).setCellValue(tt.getCurrency().getData().getCurrencyShort());
                                mm.createCell((short) 7).setCellValue(decimalFormat.format(calculateSumOfMonths(tt)));
                                mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getJul()));
                                mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getAug()));
                                mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getSep()));
                                mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getOct()));
                                mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getNov()));
                                mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getDec()));
                                mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getJan()));
                                mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getFeb()));
                                mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getMar()));
                                mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getApr()));
                                mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getMay()));
                                mm.createCell((short) 19).setCellValue(decimalFormat.format(tt.getJun()));
                                mm.createCell((short) 20).setCellValue(tt.getNo_of_days());
                                mm.createCell((short) 21).setCellValue(tt.getTarget_group());
                                mm.createCell((short) 22).setCellValue(tt.getExpected_trainer());
                                mm.createCell((short) 23).setCellValue(tt.getNotes());
                                mm.createCell((short) 24).setCellValue(tt.getDeptUnit().getNAME());

                            }
                            tr++;
                            Row incometotal = sheet.createRow((short) tr);
                            incometotal.createCell(0).setCellValue("TOTAL");
                            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
                            sheet.addMergedRegion(cellRangeT);
                            setBottomTopBorderForRegion(sheet, cellRangeT);
                            incometotal.createCell(7).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems())));
                            incometotal.createCell(8).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "jul")));
                            incometotal.createCell(9).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "aug")));
                            incometotal.createCell(10).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "sep")));
                            incometotal.createCell(11).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "oct")));
                            incometotal.createCell(12).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "nov")));
                            incometotal.createCell(13).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "dec")));
                            incometotal.createCell(14).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "jan")));
                            incometotal.createCell(15).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "feb")));
                            incometotal.createCell(16).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "mar")));
                            incometotal.createCell(17).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "apr")));
                            incometotal.createCell(18).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "may")));
                            incometotal.createCell(19).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(comboBox.getValue(), coal, selectedSections, d, budgetType.getSelectedItems(), "jun")));
                            incometotal.createCell(20).setCellValue("");
                            incometotal.createCell(21).setCellValue("");
                            incometotal.createCell(22).setCellValue("");
                            incometotal.createCell(23).setCellValue("");
                            incometotal.createCell(24).setCellValue("");
                            totalrowIndex.add((int) tr);
                        }
                    }
                }
            }
            tr++;
            Row incometotal2 = sheet.createRow((short) tr);
            incometotal2.createCell(0).setCellValue("TOTAL CAPITAL EXPENDITURE");
            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT);
            setBottomTopBorderForRegion(sheet, cellRangeT);
            incometotal2.createCell(7).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems())));
            incometotal2.createCell(8).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul")));
            incometotal2.createCell(9).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "aug")));
            incometotal2.createCell(10).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "sep")));
            incometotal2.createCell(11).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "oct")));
            incometotal2.createCell(12).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "nov")));
            incometotal2.createCell(13).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "dec")));
            incometotal2.createCell(14).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jan")));
            incometotal2.createCell(15).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "feb")));
            incometotal2.createCell(16).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "mar")));
            incometotal2.createCell(17).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "apr")));
            incometotal2.createCell(18).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "may")));
            incometotal2.createCell(19).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun")));
            incometotal2.createCell(20).setCellValue("");
            incometotal2.createCell(21).setCellValue("");
            incometotal2.createCell(22).setCellValue("");
            incometotal2.createCell(23).setCellValue("");
            incometotal2.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);

            tr++;
            tr++;
            Row incometotal21 = sheet.createRow((short) tr);
            incometotal21.createCell(0).setCellValue("TOTAL EXPENDITURE");
            CellRangeAddress cellRangeT1 = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT1);
            setBottomTopBorderForRegion(sheet, cellRangeT1);
            incometotal21.createCell(7).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems())));
            incometotal21.createCell(8).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul")));
            incometotal21.createCell(9).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "aug")));
            incometotal21.createCell(10).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "sep")));
            incometotal21.createCell(11).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "oct")));
            incometotal21.createCell(12).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "nov")));
            incometotal21.createCell(13).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "dec")));
            incometotal21.createCell(14).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jan")));
            incometotal21.createCell(15).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "feb")));
            incometotal21.createCell(16).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "mar")));
            incometotal21.createCell(17).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "apr")));
            incometotal21.createCell(18).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "may")));
            incometotal21.createCell(19).setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun")));
            incometotal21.createCell(20).setCellValue("");
            incometotal21.createCell(21).setCellValue("");
            incometotal21.createCell(22).setCellValue("");
            incometotal21.createCell(23).setCellValue("");
            incometotal21.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);
        }
        for (Row row : sheet) {
            int i = 0;
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
                i++;

                if (cell.getRow().getRowNum() < 3) {
                    cell.setCellStyle(boldCenteredStyle);
                }

                if (activityrowIndex.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cellStyle);
                    activityStyle.setAlignment(HorizontalAlignment.LEFT);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityStyle.setBorderBottom(BorderStyle.THIN);
                    activityStyle.setFont(activityFont);
                    cell.setCellStyle(activityStyle);
                }
                if (catrowIndex.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cellStyle);
                    activityStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    activityStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    activityStyle.setAlignment(HorizontalAlignment.LEFT);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityStyle.setBorderBottom(BorderStyle.THIN);
                    setCellStyleWithBackground(header2Cell, activityStyle);
                    cell.setCellStyle(activityStyle);
                }
                if (totalrowIndex.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cellStyle);
                    activityStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    activityStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    activityStyle.setAlignment(HorizontalAlignment.LEFT);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityStyle.setBorderBottom(BorderStyle.DOUBLE);
                    activityStyle.setBorderTop(BorderStyle.THICK);
                    setCellStyleWithBackground(header2Cell, activityStyle);
                    cell.setCellStyle(activityStyle);
                }

            }
        }

    }

    private void createHeaderAndBodyAccountCodeDetailByBudgetRow(Workbook workbook, Sheet sheet) {
        List<Coalevel1> coaList = new ArrayList();
        List<Integer> activityrowIndex = new ArrayList();
        List<Integer> catrowIndex = new ArrayList();
        List<Integer> totalrowIndex = new ArrayList();
        List<Integer> totalCoarowIndex = new ArrayList();
        sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);

        //sheet.setFitToPage(true);
        //sheet.setHorizontallyCenter(true);
        short rowHeight = 500; // Adjust the height as needed
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);

        // Create a cell style with the specified font
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

// Create a bold and centered style with a bottom border
        CellStyle boldCenteredStyle = workbook.createCellStyle();
        boldCenteredStyle.cloneStyleFrom(cellStyle);
        boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCenteredStyle.setFont(boldFont);
        boldCenteredStyle.setBorderBottom(BorderStyle.THIN);
        Row headerRow = sheet.createRow(0);
        //int columnWidth = 10000; // Adjust the width as needed
        //sheet.setColumnWidth(0, columnWidth);
        short tr = 2;

        try {
            // Add an image to the header
            // short rowHeight = (short) (getImageHeight("/META-INF/resources/images/urclogo.png") + 50); // Add some padding
            //System.out.println(rowHeight);
            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 24);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        Row header2 = sheet.createRow(1);
        Cell header2Cell = header2.createCell(0);
        header2Cell.setCellValue(HeaderExcel("ACCOUNT CODE").toUpperCase());
        CellRangeAddress cellRange2 = new CellRangeAddress(1, 1, 0, 24);
        sheet.addMergedRegion(cellRange2);
        setBottomBorderForRegion(sheet, cellRange2);
        Row Q2 = sheet.createRow((short) tr);
        Q2.createCell((short) 0).setCellValue("ACTIVITY CODE");
        Q2.createCell((short) 1).setCellValue("COA CODE");
        Q2.createCell((short) 2).setCellValue("ITEM");
        Q2.createCell((short) 3).setCellValue("COST");
        Q2.createCell((short) 4).setCellValue("QTY");
        Q2.createCell((short) 5).setCellValue("Unit Measure");
        Q2.createCell((short) 6).setCellValue("CUR");
        Q2.createCell((short) 7).setCellValue("TOTAL");
        Q2.createCell((short) 8).setCellValue("JUL/UGX");
        Q2.createCell((short) 9).setCellValue("AUG/UGX");
        Q2.createCell((short) 10).setCellValue("SEP/UGX");
        Q2.createCell((short) 11).setCellValue("OCT/UGX");
        Q2.createCell((short) 12).setCellValue("NOV/UGX");
        Q2.createCell((short) 13).setCellValue("DEC/UGX");
        Q2.createCell((short) 14).setCellValue("JAN/UGX");
        Q2.createCell((short) 15).setCellValue("FEB/UGX");
        Q2.createCell((short) 16).setCellValue("MAR/UGX");
        Q2.createCell((short) 17).setCellValue("APR/UGX");
        Q2.createCell((short) 18).setCellValue("MAY/UGX");
        Q2.createCell((short) 19).setCellValue("JUN/UGX");
        Q2.createCell((short) 20).setCellValue("Number of Days");
        Q2.createCell((short) 21).setCellValue("Target Group");
        Q2.createCell((short) 22).setCellValue("Trainer");
        Q2.createCell((short) 23).setCellValue("Notes");
        Q2.createCell((short) 24).setCellValue("Unit");
        Coalevel1 coal = sampleCoalevel1Service.findByCode(1);
        List<UrcDeptSectionAnlDimbgt> selectedSections = sections.getSelectedItems().stream().toList();
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBox.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("INCOME");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRange);
            setBottomBorderForRegion(sheet, cellRange);
            List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems());
            for (COA cao : findDistinctCoacodeByBudgetCoalevel1AndDeptUnits) {
                tr++;
                totalCoarowIndex.add((int) tr);
                Row incometotal2 = sheet.createRow((short) tr);
                incometotal2.createCell(0).setCellValue(cao.getCode());
                CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 1);
                sheet.addMergedRegion(cellRangeT);
                setBottomTopBorderForRegion(sheet, cellRangeT);

                incometotal2.createCell(2).setCellValue(cao.getName());
                CellRangeAddress cellRangeT2 = new CellRangeAddress(tr, tr, 2, 6);
                sheet.addMergedRegion(cellRangeT2);
                setBottomTopBorderForRegion(sheet, cellRangeT2);
                incometotal2.createCell(7).setCellValue(sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems()).doubleValue());

                CellStyle style23 = incometotal2.getSheet().getWorkbook().createCellStyle();
                style23.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

                Cell cell8 = incometotal2.createCell((short) 8);
                cell8.setCellType(CellType.NUMERIC);
                cell8.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jul").doubleValue());
                cell8.setCellStyle(style23);

                Cell cell9 = incometotal2.createCell((short) 9);
                cell9.setCellType(CellType.NUMERIC);
                cell9.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "aug").doubleValue());
                cell9.setCellStyle(style23);

                Cell cell10 = incometotal2.createCell((short) 10);
                cell10.setCellType(CellType.NUMERIC);
                cell10.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "sep").doubleValue());
                cell10.setCellStyle(style23);

                Cell cell11 = incometotal2.createCell((short) 11);
                cell11.setCellType(CellType.NUMERIC);
                cell11.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "oct").doubleValue());
                cell11.setCellStyle(style23);

                Cell cell12 = incometotal2.createCell((short) 12);
                cell12.setCellType(CellType.NUMERIC);
                cell12.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "nov").doubleValue());
                cell12.setCellStyle(style23);

                Cell cell13 = incometotal2.createCell((short) 13);
                cell13.setCellType(CellType.NUMERIC);
                cell13.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "dec").doubleValue());
                cell13.setCellStyle(style23);

                Cell cell14 = incometotal2.createCell((short) 14);
                cell14.setCellType(CellType.NUMERIC);
                cell14.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jan").doubleValue());
                cell14.setCellStyle(style23);

                Cell cell15 = incometotal2.createCell((short) 15);
                cell15.setCellType(CellType.NUMERIC);
                cell15.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "feb").doubleValue());
                cell15.setCellStyle(style23);

                Cell cell16 = incometotal2.createCell((short) 16);
                cell16.setCellType(CellType.NUMERIC);
                cell16.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "mar").doubleValue());
                cell16.setCellStyle(style23);

                Cell cell17 = incometotal2.createCell((short) 17);
                cell17.setCellType(CellType.NUMERIC);
                cell17.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "apr").doubleValue());
                cell17.setCellStyle(style23);

                Cell cell18 = incometotal2.createCell((short) 18);
                cell18.setCellType(CellType.NUMERIC);
                cell18.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "may").doubleValue());
                cell18.setCellStyle(style23);

                Cell cell19 = incometotal2.createCell((short) 19);
                cell19.setCellType(CellType.NUMERIC);
                cell19.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jun").doubleValue());
                cell19.setCellStyle(style23);

                incometotal2.createCell(20).setCellValue("");
                incometotal2.createCell(21).setCellValue("");
                incometotal2.createCell(22).setCellValue("");
                incometotal2.createCell(23).setCellValue("");
                incometotal2.createCell(24).setCellValue("");
                // CellRangeAddress cellRangeT3 = new CellRangeAddress(tr, tr, 7, 24);
                // sheet.addMergedRegion(cellRangeT3);
                budgetList = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems());

                for (BudgetItems tt : budgetList) {

                    tr++;
                    Row mm = sheet.createRow((short) tr);
                    CellStyle style22 = incometotal2.getSheet().getWorkbook().createCellStyle();
                    style22.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    mm.createCell((short) 0).setCellValue("");
                    mm.createCell((short) 1).setCellValue(tt.getCoacode().getName() + " (" + tt.getCoacode().getCode().trim() + ")");
                    mm.createCell((short) 2).setCellValue(tt.getItem());
                    Cell cell3 = mm.createCell((short) 3);
                    cell3.setCellType(CellType.NUMERIC);
                    cell3.setCellStyle(style22);
                    cell3.setCellValue(tt.getCost().doubleValue());

                    Cell cell4 = mm.createCell((short) 4);
                    cell4.setCellType(CellType.NUMERIC);
                    cell4.setCellStyle(style22);
                    cell4.setCellValue(tt.getQty().doubleValue());

                    mm.createCell((short) 5).setCellValue(tt.getUnitMeasure());
                    mm.createCell((short) 6).setCellValue(tt.getCurrency().getData().getCurrencyShort());
                    Cell cell71 = mm.createCell((short) 7);
                    cell71.setCellType(CellType.NUMERIC);
                    cell71.setCellValue(calculateSumOfMonths(tt).doubleValue());
                    cell71.setCellStyle(style22);

                    Cell cell81 = mm.createCell((short) 8);
                    cell81.setCellType(CellType.NUMERIC);
                    cell81.setCellValue(tt.getJul().doubleValue());
                    cell81.setCellStyle(style22);

                    Cell cell91 = mm.createCell((short) 9);
                    cell91.setCellType(CellType.NUMERIC);
                    cell91.setCellValue(tt.getAug().doubleValue());
                    cell91.setCellStyle(style22);

                    Cell cell101 = mm.createCell((short) 10);
                    cell101.setCellType(CellType.NUMERIC);
                    cell101.setCellValue(tt.getSep().doubleValue());
                    cell71.setCellStyle(style22);

                    Cell cell111 = mm.createCell((short) 11);
                    cell111.setCellType(CellType.NUMERIC);
                    cell111.setCellValue(tt.getOct().doubleValue());
                    cell111.setCellStyle(style22);

                    Cell cell121 = mm.createCell((short) 12);
                    cell121.setCellType(CellType.NUMERIC);
                    cell121.setCellValue(tt.getNov().doubleValue());
                    cell121.setCellStyle(style22);

                    Cell cell131 = mm.createCell((short) 13);
                    cell131.setCellType(CellType.NUMERIC);
                    cell131.setCellValue(tt.getDec().doubleValue());
                    cell131.setCellStyle(style22);

                    Cell cell141 = mm.createCell((short) 14);
                    cell141.setCellType(CellType.NUMERIC);
                    cell141.setCellValue(tt.getJan().doubleValue());
                    cell141.setCellStyle(style22);

                    Cell cell151 = mm.createCell((short) 15);
                    cell151.setCellType(CellType.NUMERIC);
                    cell151.setCellValue(tt.getFeb().doubleValue());
                    cell151.setCellStyle(style22);

                    Cell cell161 = mm.createCell((short) 16);
                    cell161.setCellType(CellType.NUMERIC);
                    cell161.setCellValue(tt.getMar().doubleValue());
                    cell161.setCellStyle(style22);

                    Cell cell171 = mm.createCell((short) 17);
                    cell171.setCellType(CellType.NUMERIC);
                    cell171.setCellValue(tt.getApr().doubleValue());
                    cell171.setCellStyle(style22);

                    Cell cell181 = mm.createCell((short) 18);
                    cell181.setCellType(CellType.NUMERIC);
                    cell181.setCellValue(tt.getMay().doubleValue());
                    cell181.setCellStyle(style22);

                    Cell cell191 = mm.createCell((short) 19);
                    cell191.setCellType(CellType.NUMERIC);
                    cell191.setCellValue(tt.getJun().doubleValue());
                    cell191.setCellStyle(style22);

                    mm.createCell((short) 20).setCellValue(tt.getNo_of_days());
                    mm.createCell((short) 21).setCellValue(tt.getTarget_group());
                    mm.createCell((short) 22).setCellValue(tt.getExpected_trainer());
                    mm.createCell((short) 23).setCellValue(tt.getNotes());
                    mm.createCell((short) 24).setCellValue(tt.getDeptUnit().getNAME());

                }
            }

            tr++;
            Row incometotal2 = sheet.createRow((short) tr);
            incometotal2.createCell(0).setCellValue("TOTAL INCOME");
            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT);
            setBottomTopBorderForRegion(sheet, cellRangeT);

            CellStyle style22 = incometotal2.getSheet().getWorkbook().createCellStyle();
            style22.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

            Cell cell7 = incometotal2.createCell((short) 7);
            cell7.setCellType(CellType.NUMERIC);
            cell7.setCellValue(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems()).doubleValue());
            cell7.setCellStyle(style22);

            Cell cell8 = incometotal2.createCell((short) 8);
            cell8.setCellType(CellType.NUMERIC);
            cell8.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul").doubleValue());
            cell8.setCellStyle(style22);

            Cell cell9 = incometotal2.createCell((short) 9);
            cell9.setCellType(CellType.NUMERIC);
            cell9.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "aug").doubleValue());
            cell9.setCellStyle(style22);

            Cell cell10 = incometotal2.createCell((short) 10);
            cell10.setCellType(CellType.NUMERIC);
            cell10.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "sep").doubleValue());
            cell10.setCellStyle(style22);

            Cell cell11 = incometotal2.createCell((short) 11);
            cell11.setCellType(CellType.NUMERIC);
            cell11.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "oct").doubleValue());
            cell11.setCellStyle(style22);

            Cell cell12 = incometotal2.createCell((short) 12);
            cell12.setCellType(CellType.NUMERIC);
            cell12.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "nov").doubleValue());
            cell7.setCellStyle(style22);

            Cell cell13 = incometotal2.createCell((short) 13);
            cell13.setCellType(CellType.NUMERIC);
            cell13.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "dec").doubleValue());
            cell13.setCellStyle(style22);

            Cell cell14 = incometotal2.createCell((short) 14);
            cell14.setCellType(CellType.NUMERIC);
            cell14.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jan").doubleValue());
            cell14.setCellStyle(style22);

            Cell cell15 = incometotal2.createCell((short) 15);
            cell15.setCellType(CellType.NUMERIC);
            cell15.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "feb").doubleValue());
            cell15.setCellStyle(style22);

            Cell cell16 = incometotal2.createCell((short) 16);
            cell16.setCellType(CellType.NUMERIC);
            cell16.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "mar").doubleValue());
            cell16.setCellStyle(style22);

            Cell cell17 = incometotal2.createCell((short) 17);
            cell17.setCellType(CellType.NUMERIC);
            cell17.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "apr").doubleValue());
            cell17.setCellStyle(style22);

            Cell cell18 = incometotal2.createCell((short) 18);
            cell18.setCellType(CellType.NUMERIC);
            cell18.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "may").doubleValue());
            cell18.setCellStyle(style22);

            Cell cell19 = incometotal2.createCell((short) 19);
            cell19.setCellType(CellType.NUMERIC);
            cell19.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun").doubleValue());
            cell19.setCellStyle(style22);

            incometotal2.createCell(20).setCellValue("");
            incometotal2.createCell(21).setCellValue("");
            incometotal2.createCell(22).setCellValue("");
            incometotal2.createCell(23).setCellValue("");
            incometotal2.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);
        }
        tr++;
        coal = sampleCoalevel1Service.findByCode(2);
        coaList.add(coal);
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBox.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("REVENUE EXPENDITURE");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRange);
            setBottomBorderForRegion(sheet, cellRange);

            List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems());
            for (COA cao : findDistinctCoacodeByBudgetCoalevel1AndDeptUnits) {
                tr++;
                totalCoarowIndex.add((int) tr);
                Row incometotal2 = sheet.createRow((short) tr);
                incometotal2.createCell(0).setCellValue(cao.getCode());
                CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 1);
                sheet.addMergedRegion(cellRangeT);
                setBottomTopBorderForRegion(sheet, cellRangeT);

                incometotal2.createCell(2).setCellValue(cao.getName());
                CellRangeAddress cellRangeT2 = new CellRangeAddress(tr, tr, 2, 6);
                sheet.addMergedRegion(cellRangeT2);
                setBottomTopBorderForRegion(sheet, cellRangeT2);
                incometotal2.createCell(7).setCellValue(sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems()).doubleValue());
                CellStyle style23 = incometotal2.getSheet().getWorkbook().createCellStyle();
                style23.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

                Cell cell8 = incometotal2.createCell((short) 8);
                cell8.setCellType(CellType.NUMERIC);
                cell8.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jul").doubleValue());
                cell8.setCellStyle(style23);

                Cell cell9 = incometotal2.createCell((short) 9);
                cell9.setCellType(CellType.NUMERIC);
                cell9.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "aug").doubleValue());
                cell9.setCellStyle(style23);

                Cell cell10 = incometotal2.createCell((short) 10);
                cell10.setCellType(CellType.NUMERIC);
                cell10.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "sep").doubleValue());
                cell10.setCellStyle(style23);

                Cell cell11 = incometotal2.createCell((short) 11);
                cell11.setCellType(CellType.NUMERIC);
                cell11.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "oct").doubleValue());
                cell11.setCellStyle(style23);

                Cell cell12 = incometotal2.createCell((short) 12);
                cell12.setCellType(CellType.NUMERIC);
                cell12.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "nov").doubleValue());
                cell12.setCellStyle(style23);

                Cell cell13 = incometotal2.createCell((short) 13);
                cell13.setCellType(CellType.NUMERIC);
                cell13.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "dec").doubleValue());
                cell13.setCellStyle(style23);

                Cell cell14 = incometotal2.createCell((short) 14);
                cell14.setCellType(CellType.NUMERIC);
                cell14.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jan").doubleValue());
                cell14.setCellStyle(style23);

                Cell cell15 = incometotal2.createCell((short) 15);
                cell15.setCellType(CellType.NUMERIC);
                cell15.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "feb").doubleValue());
                cell15.setCellStyle(style23);

                Cell cell16 = incometotal2.createCell((short) 16);
                cell16.setCellType(CellType.NUMERIC);
                cell16.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "mar").doubleValue());
                cell16.setCellStyle(style23);

                Cell cell17 = incometotal2.createCell((short) 17);
                cell17.setCellType(CellType.NUMERIC);
                cell17.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "apr").doubleValue());
                cell17.setCellStyle(style23);

                Cell cell18 = incometotal2.createCell((short) 18);
                cell18.setCellType(CellType.NUMERIC);
                cell18.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "may").doubleValue());
                cell18.setCellStyle(style23);

                Cell cell19 = incometotal2.createCell((short) 19);
                cell19.setCellType(CellType.NUMERIC);
                cell19.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jun").doubleValue());
                cell19.setCellStyle(style23);

                incometotal2.createCell(20).setCellValue("");
                incometotal2.createCell(21).setCellValue("");
                incometotal2.createCell(22).setCellValue("");
                incometotal2.createCell(23).setCellValue("");
                incometotal2.createCell(24).setCellValue("");
                budgetList = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems());

                for (BudgetItems tt : budgetList) {
                    tr++;
                    Row mm = sheet.createRow((short) tr);
                    CellStyle style21 = incometotal2.getSheet().getWorkbook().createCellStyle();
                    style21.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    mm.createCell((short) 0).setCellValue("");
                    mm.createCell((short) 1).setCellValue(tt.getCoacode().getName() + " (" + tt.getCoacode().getCode().trim() + ")");
                    mm.createCell((short) 2).setCellValue(tt.getItem());
                    Cell cell3 = mm.createCell((short) 3);
                    cell3.setCellType(CellType.NUMERIC);
                    cell3.setCellStyle(style21);
                    cell3.setCellValue(tt.getCost().doubleValue());

                    Cell cell4 = mm.createCell((short) 4);
                    cell4.setCellType(CellType.NUMERIC);
                    cell4.setCellStyle(style21);
                    cell4.setCellValue(tt.getQty().doubleValue());

                    mm.createCell((short) 5).setCellValue(tt.getUnitMeasure());
                    mm.createCell((short) 6).setCellValue(tt.getCurrency().getData().getCurrencyShort());
                    Cell cell71 = mm.createCell((short) 7);
                    cell71.setCellType(CellType.NUMERIC);
                    cell71.setCellValue(calculateSumOfMonths(tt).doubleValue());
                    cell71.setCellStyle(style21);

                    Cell cell81 = mm.createCell((short) 8);
                    cell81.setCellType(CellType.NUMERIC);
                    cell81.setCellValue(tt.getJul().doubleValue());
                    cell81.setCellStyle(style21);

                    Cell cell91 = mm.createCell((short) 9);
                    cell91.setCellType(CellType.NUMERIC);
                    cell91.setCellValue(tt.getAug().doubleValue());
                    cell91.setCellStyle(style21);

                    Cell cell101 = mm.createCell((short) 10);
                    cell101.setCellType(CellType.NUMERIC);
                    cell101.setCellValue(tt.getSep().doubleValue());
                    cell71.setCellStyle(style21);

                    Cell cell111 = mm.createCell((short) 11);
                    cell111.setCellType(CellType.NUMERIC);
                    cell111.setCellValue(tt.getOct().doubleValue());
                    cell111.setCellStyle(style21);

                    Cell cell121 = mm.createCell((short) 12);
                    cell121.setCellType(CellType.NUMERIC);
                    cell121.setCellValue(tt.getNov().doubleValue());
                    cell71.setCellStyle(style21);

                    Cell cell131 = mm.createCell((short) 13);
                    cell131.setCellType(CellType.NUMERIC);
                    cell131.setCellValue(tt.getDec().doubleValue());
                    cell131.setCellStyle(style21);

                    Cell cell141 = mm.createCell((short) 14);
                    cell141.setCellType(CellType.NUMERIC);
                    cell141.setCellValue(tt.getJan().doubleValue());
                    cell141.setCellStyle(style21);

                    Cell cell151 = mm.createCell((short) 15);
                    cell151.setCellType(CellType.NUMERIC);
                    cell151.setCellValue(tt.getFeb().doubleValue());
                    cell151.setCellStyle(style21);

                    Cell cell161 = mm.createCell((short) 16);
                    cell161.setCellType(CellType.NUMERIC);
                    cell161.setCellValue(tt.getMar().doubleValue());
                    cell161.setCellStyle(style21);

                    Cell cell171 = mm.createCell((short) 17);
                    cell171.setCellType(CellType.NUMERIC);
                    cell171.setCellValue(tt.getApr().doubleValue());
                    cell171.setCellStyle(style21);

                    Cell cell181 = mm.createCell((short) 18);
                    cell181.setCellType(CellType.NUMERIC);
                    cell181.setCellValue(tt.getMay().doubleValue());
                    cell181.setCellStyle(style21);

                    Cell cell191 = mm.createCell((short) 19);
                    cell191.setCellType(CellType.NUMERIC);
                    cell191.setCellValue(tt.getJun().doubleValue());
                    cell191.setCellStyle(style21);

                    mm.createCell((short) 20).setCellValue(tt.getNo_of_days());
                    mm.createCell((short) 21).setCellValue(tt.getTarget_group());
                    mm.createCell((short) 22).setCellValue(tt.getExpected_trainer());
                    mm.createCell((short) 23).setCellValue(tt.getNotes());
                    mm.createCell((short) 24).setCellValue(tt.getDeptUnit().getNAME());

                }
            }

            tr++;
            Row incometotal2 = sheet.createRow((short) tr);
            incometotal2.createCell(0).setCellValue("TOTAL REVENUE EXPENDITURE");
            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT);
            setBottomTopBorderForRegion(sheet, cellRangeT);
            Cell cell7 = incometotal2.createCell((short) 7);
            cell7.setCellType(CellType.NUMERIC);
            cell7.setCellValue(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems()).doubleValue());
            CellStyle style = incometotal2.getSheet().getWorkbook().createCellStyle();
            style.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
            cell7.setCellStyle(style);
            Cell cell8 = incometotal2.createCell((short) 8);
            cell8.setCellType(CellType.NUMERIC);
            cell8.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul").doubleValue());
            cell8.setCellStyle(style);

            Cell cell9 = incometotal2.createCell((short) 9);
            cell9.setCellType(CellType.NUMERIC);
            cell9.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "aug").doubleValue());
            cell9.setCellStyle(style);

            Cell cell10 = incometotal2.createCell((short) 10);
            cell10.setCellType(CellType.NUMERIC);
            cell10.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "sep").doubleValue());
            cell10.setCellStyle(style);

            Cell cell11 = incometotal2.createCell((short) 11);
            cell11.setCellType(CellType.NUMERIC);
            cell11.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "oct").doubleValue());
            cell11.setCellStyle(style);

            Cell cell12 = incometotal2.createCell((short) 12);
            cell12.setCellType(CellType.NUMERIC);
            cell12.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "nov").doubleValue());
            cell12.setCellStyle(style);

            Cell cell13 = incometotal2.createCell((short) 13);
            cell13.setCellType(CellType.NUMERIC);
            cell13.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "dec").doubleValue());
            cell13.setCellStyle(style);

            Cell cell14 = incometotal2.createCell((short) 14);
            cell14.setCellType(CellType.NUMERIC);
            cell14.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jan").doubleValue());
            cell14.setCellStyle(style);

            Cell cell15 = incometotal2.createCell((short) 15);
            cell15.setCellType(CellType.NUMERIC);
            cell15.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "feb").doubleValue());
            cell15.setCellStyle(style);

            Cell cell16 = incometotal2.createCell((short) 16);
            cell16.setCellType(CellType.NUMERIC);
            cell16.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "mar").doubleValue());
            cell16.setCellStyle(style);

            Cell cell17 = incometotal2.createCell((short) 17);
            cell17.setCellType(CellType.NUMERIC);
            cell17.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "apr").doubleValue());
            cell17.setCellStyle(style);

            Cell cell18 = incometotal2.createCell((short) 18);
            cell18.setCellType(CellType.NUMERIC);
            cell18.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "may").doubleValue());
            cell18.setCellStyle(style);

            Cell cell19 = incometotal2.createCell((short) 19);
            cell19.setCellType(CellType.NUMERIC);
            cell19.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun").doubleValue());

            incometotal2.createCell(20).setCellValue("");
            incometotal2.createCell(21).setCellValue("");
            incometotal2.createCell(22).setCellValue("");
            incometotal2.createCell(23).setCellValue("");
            incometotal2.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);
        }
        tr++;
        coal = sampleCoalevel1Service.findByCode(3);
        coaList.add(coal);
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBox.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("CAPITAL EXPENDITURE");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRange);
            setBottomBorderForRegion(sheet, cellRange);
            List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems());
            for (COA cao : findDistinctCoacodeByBudgetCoalevel1AndDeptUnits) {
                tr++;

                totalCoarowIndex.add((int) tr);
                Row incometotal2 = sheet.createRow((short) tr);
                incometotal2.createCell(0).setCellValue(cao.getCode());
                CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 1);
                sheet.addMergedRegion(cellRangeT);
                setBottomTopBorderForRegion(sheet, cellRangeT);
                CellStyle style = incometotal2.getSheet().getWorkbook().createCellStyle();
                style.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

                incometotal2.createCell(2).setCellValue(cao.getName());
                CellRangeAddress cellRangeT2 = new CellRangeAddress(tr, tr, 2, 6);
                sheet.addMergedRegion(cellRangeT2);
                setBottomTopBorderForRegion(sheet, cellRangeT2);

                Cell cell7 = incometotal2.createCell((short) 7);
                cell7.setCellType(CellType.NUMERIC);
                cell7.setCellValue(sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems()).doubleValue());
                cell7.setCellStyle(style);
                CellStyle style23 = incometotal2.getSheet().getWorkbook().createCellStyle();
                style23.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

                Cell cell8 = incometotal2.createCell((short) 8);
                cell8.setCellType(CellType.NUMERIC);
                cell8.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jul").doubleValue());
                cell8.setCellStyle(style23);

                Cell cell9 = incometotal2.createCell((short) 9);
                cell9.setCellType(CellType.NUMERIC);
                cell9.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "aug").doubleValue());
                cell9.setCellStyle(style23);

                Cell cell10 = incometotal2.createCell((short) 10);
                cell10.setCellType(CellType.NUMERIC);
                cell10.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "sep").doubleValue());
                cell10.setCellStyle(style23);

                Cell cell11 = incometotal2.createCell((short) 11);
                cell11.setCellType(CellType.NUMERIC);
                cell11.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "oct").doubleValue());
                cell11.setCellStyle(style23);

                Cell cell12 = incometotal2.createCell((short) 12);
                cell12.setCellType(CellType.NUMERIC);
                cell12.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "nov").doubleValue());
                cell12.setCellStyle(style23);

                Cell cell13 = incometotal2.createCell((short) 13);
                cell13.setCellType(CellType.NUMERIC);
                cell13.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "dec").doubleValue());
                cell13.setCellStyle(style23);

                Cell cell14 = incometotal2.createCell((short) 14);
                cell14.setCellType(CellType.NUMERIC);
                cell14.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jan").doubleValue());
                cell14.setCellStyle(style23);

                Cell cell15 = incometotal2.createCell((short) 15);
                cell15.setCellType(CellType.NUMERIC);
                cell15.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "feb").doubleValue());
                cell15.setCellStyle(style23);

                Cell cell16 = incometotal2.createCell((short) 16);
                cell16.setCellType(CellType.NUMERIC);
                cell16.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "mar").doubleValue());
                cell16.setCellStyle(style23);

                Cell cell17 = incometotal2.createCell((short) 17);
                cell17.setCellType(CellType.NUMERIC);
                cell17.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "apr").doubleValue());
                cell17.setCellStyle(style23);

                Cell cell18 = incometotal2.createCell((short) 18);
                cell18.setCellType(CellType.NUMERIC);
                cell18.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "may").doubleValue());
                cell18.setCellStyle(style23);

                Cell cell19 = incometotal2.createCell((short) 19);
                cell19.setCellType(CellType.NUMERIC);
                cell19.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems(), "jun").doubleValue());
                cell19.setCellStyle(style23);

                incometotal2.createCell(20).setCellValue("");
                incometotal2.createCell(21).setCellValue("");
                incometotal2.createCell(22).setCellValue("");
                incometotal2.createCell(23).setCellValue("");
                incometotal2.createCell(24).setCellValue("");
                budgetList = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(comboBox.getValue(), cao, selectedSections, budgetType.getSelectedItems());

                for (BudgetItems tt : budgetList) {
                    tr++;
                    Row mm = sheet.createRow((short) tr);
                    CellStyle style2 = incometotal2.getSheet().getWorkbook().createCellStyle();
                    style2.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    mm.createCell((short) 0).setCellValue("");
                    mm.createCell((short) 1).setCellValue(tt.getCoacode().getName() + " (" + tt.getCoacode().getCode().trim() + ")");
                    mm.createCell((short) 2).setCellValue(tt.getItem());
                    Cell cell3 = mm.createCell((short) 3);
                    cell3.setCellType(CellType.NUMERIC);
                    cell3.setCellStyle(style2);
                    cell3.setCellValue(tt.getCost().doubleValue());

                    Cell cell4 = mm.createCell((short) 4);
                    cell4.setCellType(CellType.NUMERIC);
                    cell4.setCellStyle(style2);
                    cell4.setCellValue(tt.getQty().doubleValue());

                    mm.createCell((short) 5).setCellValue(tt.getUnitMeasure());
                    mm.createCell((short) 6).setCellValue(tt.getCurrency().getData().getCurrencyShort());
                    Cell cell71 = mm.createCell((short) 7);
                    cell71.setCellType(CellType.NUMERIC);
                    cell71.setCellValue(calculateSumOfMonths(tt).doubleValue());
                    cell71.setCellStyle(style2);

                    Cell cell81 = mm.createCell((short) 8);
                    cell81.setCellType(CellType.NUMERIC);
                    cell81.setCellValue(tt.getJul().doubleValue());
                    cell81.setCellStyle(style2);

                    Cell cell91 = mm.createCell((short) 9);
                    cell91.setCellType(CellType.NUMERIC);
                    cell91.setCellValue(tt.getAug().doubleValue());
                    cell91.setCellStyle(style2);

                    Cell cell101 = mm.createCell((short) 10);
                    cell101.setCellType(CellType.NUMERIC);
                    cell101.setCellValue(tt.getSep().doubleValue());
                    cell71.setCellStyle(style2);

                    Cell cell111 = mm.createCell((short) 11);
                    cell111.setCellType(CellType.NUMERIC);
                    cell111.setCellValue(tt.getOct().doubleValue());
                    cell111.setCellStyle(style2);

                    Cell cell121 = mm.createCell((short) 12);
                    cell121.setCellType(CellType.NUMERIC);
                    cell121.setCellValue(tt.getNov().doubleValue());
                    cell71.setCellStyle(style2);

                    Cell cell131 = mm.createCell((short) 13);
                    cell131.setCellType(CellType.NUMERIC);
                    cell131.setCellValue(tt.getDec().doubleValue());
                    cell131.setCellStyle(style2);

                    Cell cell141 = mm.createCell((short) 14);
                    cell141.setCellType(CellType.NUMERIC);
                    cell141.setCellValue(tt.getJan().doubleValue());
                    cell141.setCellStyle(style2);

                    Cell cell151 = mm.createCell((short) 15);
                    cell151.setCellType(CellType.NUMERIC);
                    cell151.setCellValue(tt.getFeb().doubleValue());
                    cell151.setCellStyle(style2);

                    Cell cell161 = mm.createCell((short) 16);
                    cell161.setCellType(CellType.NUMERIC);
                    cell161.setCellValue(tt.getMar().doubleValue());
                    cell161.setCellStyle(style2);

                    Cell cell171 = mm.createCell((short) 17);
                    cell171.setCellType(CellType.NUMERIC);
                    cell171.setCellValue(tt.getApr().doubleValue());
                    cell171.setCellStyle(style2);

                    Cell cell181 = mm.createCell((short) 18);
                    cell181.setCellType(CellType.NUMERIC);
                    cell181.setCellValue(tt.getMay().doubleValue());
                    cell181.setCellStyle(style2);

                    Cell cell191 = mm.createCell((short) 19);
                    cell191.setCellType(CellType.NUMERIC);
                    cell191.setCellValue(tt.getJun().doubleValue());
                    cell191.setCellStyle(style2);

                    mm.createCell((short) 20).setCellValue(tt.getNo_of_days());
                    mm.createCell((short) 21).setCellValue(tt.getTarget_group());
                    mm.createCell((short) 22).setCellValue(tt.getExpected_trainer());
                    mm.createCell((short) 23).setCellValue(tt.getNotes());
                    mm.createCell((short) 24).setCellValue(tt.getDeptUnit().getNAME());

                }
            }

            tr++;
            Row incometotal2 = sheet.createRow((short) tr);
            incometotal2.createCell(0).setCellValue("TOTAL CAPITAL EXPENDITURE");
            CellRangeAddress cellRangeT = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT);
            setBottomTopBorderForRegion(sheet, cellRangeT);
            CellStyle style2 = incometotal2.getSheet().getWorkbook().createCellStyle();
            style2.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
            Cell cell7 = incometotal2.createCell((short) 7);
            cell7.setCellType(CellType.NUMERIC);
            cell7.setCellValue(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems()).doubleValue());
            cell7.setCellStyle(style2);

            Cell cell8 = incometotal2.createCell((short) 8);
            cell8.setCellType(CellType.NUMERIC);
            // cell8.setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul")));
            cell8.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul").doubleValue());
            cell8.setCellStyle(style2);

            Cell cell9 = incometotal2.createCell((short) 9);
            cell9.setCellType(CellType.NUMERIC);
            cell9.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "aug").doubleValue());
            cell9.setCellStyle(style2);

            Cell cell10 = incometotal2.createCell((short) 10);
            cell10.setCellType(CellType.NUMERIC);
            cell10.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "sep").doubleValue());

            Cell cell11 = incometotal2.createCell((short) 11);
            cell11.setCellType(CellType.NUMERIC);
            cell11.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "oct").doubleValue());
            cell11.setCellStyle(style2);

            Cell cell12 = incometotal2.createCell((short) 12);
            cell12.setCellType(CellType.NUMERIC);
            cell12.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "nov").doubleValue());
            cell12.setCellStyle(style2);

            Cell cell13 = incometotal2.createCell((short) 13);
            cell13.setCellType(CellType.NUMERIC);
            cell13.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "dec").doubleValue());
            cell13.setCellStyle(style2);

            Cell cell14 = incometotal2.createCell((short) 14);
            cell14.setCellType(CellType.NUMERIC);
            cell14.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jan").doubleValue());
            cell14.setCellStyle(style2);

            Cell cell15 = incometotal2.createCell((short) 15);
            cell15.setCellType(CellType.NUMERIC);
            cell15.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "feb").doubleValue());
            cell15.setCellStyle(style2);

            Cell cell16 = incometotal2.createCell((short) 16);
            cell16.setCellType(CellType.NUMERIC);
            cell16.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "mar").doubleValue());
            cell16.setCellStyle(style2);

            Cell cell17 = incometotal2.createCell((short) 17);
            cell17.setCellType(CellType.NUMERIC);
            cell17.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "apr").doubleValue());
            cell17.setCellStyle(style2);

            Cell cell18 = incometotal2.createCell((short) 18);
            cell18.setCellType(CellType.NUMERIC);
            cell18.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "may").doubleValue());
            cell18.setCellStyle(style2);

            Cell cell19 = incometotal2.createCell((short) 19);
            cell19.setCellType(CellType.NUMERIC);
            cell19.setCellStyle(style2);
            //cell19.setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun")));
            cell19.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun").doubleValue());
            incometotal2.createCell(20).setCellValue("");
            incometotal2.createCell(21).setCellValue("");
            incometotal2.createCell(22).setCellValue("");
            incometotal2.createCell(23).setCellValue("");
            incometotal2.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);

            tr++;
            tr++;
            Row incometotal21 = sheet.createRow((short) tr);
            incometotal21.setHeight(rowHeight);
            incometotal21.createCell(0).setCellValue("TOTAL EXPENDITURE");
            CellRangeAddress cellRangeT1 = new CellRangeAddress(tr, tr, 0, 6);
            sheet.addMergedRegion(cellRangeT1);
            setBottomTopBorderForRegion(sheet, cellRangeT1);
            CellStyle style21 = incometotal21.getSheet().getWorkbook().createCellStyle();
            style21.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
            Cell cell71 = incometotal21.createCell((short) 7);
            cell71.setCellType(CellType.NUMERIC);
            cell71.setCellValue(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems()).doubleValue());
            cell71.setCellStyle(style21);

            Cell cell81 = incometotal21.createCell((short) 8);
            cell81.setCellType(CellType.NUMERIC);
            // cell8.setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul")));
            cell81.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "jul").doubleValue());
            cell81.setCellStyle(style21);

            Cell cell91 = incometotal21.createCell((short) 9);
            cell91.setCellType(CellType.NUMERIC);
            cell91.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "aug").doubleValue());
            cell91.setCellStyle(style21);

            Cell cell101 = incometotal21.createCell((short) 10);
            cell101.setCellType(CellType.NUMERIC);
            cell101.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "sep").doubleValue());

            Cell cell111 = incometotal21.createCell((short) 11);
            cell111.setCellType(CellType.NUMERIC);
            cell111.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "oct").doubleValue());
            cell111.setCellStyle(style21);

            Cell cell121 = incometotal21.createCell((short) 12);
            cell121.setCellType(CellType.NUMERIC);
            cell121.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "nov").doubleValue());
            cell121.setCellStyle(style21);

            Cell cell131 = incometotal21.createCell((short) 13);
            cell131.setCellType(CellType.NUMERIC);
            cell131.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "dec").doubleValue());
            cell131.setCellStyle(style21);

            Cell cell141 = incometotal21.createCell((short) 14);
            cell141.setCellType(CellType.NUMERIC);
            cell141.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "jan").doubleValue());
            cell141.setCellStyle(style21);

            Cell cell151 = incometotal21.createCell((short) 15);
            cell151.setCellType(CellType.NUMERIC);
            cell151.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "feb").doubleValue());
            cell151.setCellStyle(style21);

            Cell cell161 = incometotal21.createCell((short) 16);
            cell161.setCellType(CellType.NUMERIC);
            cell161.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "mar").doubleValue());
            cell161.setCellStyle(style21);

            Cell cell171 = incometotal21.createCell((short) 17);
            cell171.setCellType(CellType.NUMERIC);
            cell171.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "apr").doubleValue());
            cell171.setCellStyle(style21);

            Cell cell181 = incometotal21.createCell((short) 18);
            cell181.setCellType(CellType.NUMERIC);
            cell181.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "may").doubleValue());
            cell181.setCellStyle(style21);

            Cell cell191 = incometotal21.createCell((short) 19);
            cell191.setCellType(CellType.NUMERIC);
            cell191.setCellStyle(style21);
            //cell19.setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun")));
            cell191.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(comboBox.getValue(), coaList, selectedSections, budgetType.getSelectedItems(), "jun").doubleValue());
            incometotal21.createCell(20).setCellValue("");
            incometotal21.createCell(21).setCellValue("");
            incometotal21.createCell(22).setCellValue("");
            incometotal21.createCell(23).setCellValue("");
            incometotal21.createCell(24).setCellValue("");
            totalrowIndex.add((int) tr);
        }
        for (Row row : sheet) {
            int i = 0;
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
                i++;

                if (cell.getRow().getRowNum() < 3) {
                    cell.setCellStyle(boldCenteredStyle);
                }

                if (activityrowIndex.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cellStyle);
                    activityStyle.setAlignment(HorizontalAlignment.LEFT);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityStyle.setBorderBottom(BorderStyle.THIN);
                    activityStyle.setFont(activityFont);
                    activityStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    cell.setCellStyle(activityStyle);
                }
                if (catrowIndex.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cellStyle);
                    activityStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    activityStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    activityStyle.setAlignment(HorizontalAlignment.LEFT);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityStyle.setBorderBottom(BorderStyle.THIN);
                    setCellStyleWithBackground(header2Cell, activityStyle);
                    activityStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    cell.setCellStyle(activityStyle);
                }
                if (totalrowIndex.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cellStyle);
                    activityStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    activityStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    activityStyle.setAlignment(HorizontalAlignment.LEFT);
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityStyle.setBorderBottom(BorderStyle.DOUBLE);
                    activityStyle.setBorderTop(BorderStyle.THICK);
                    setCellStyleWithBackground(header2Cell, activityStyle);
                    activityStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    cell.setCellStyle(activityStyle);
                }

                if (totalCoarowIndex.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cellStyle);
                    activityStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                    activityStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    activityStyle.setAlignment(HorizontalAlignment.LEFT);
                    activityStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    setCellStyleWithBackground(header2Cell, activityStyle);
                    cell.setCellStyle(activityStyle);

                }

            }
        }

    }

    private void createHeaderAndBodyAccountCodeDetailByBudgetRow2(
            Workbook workbook,
            Sheet sheet,
            Set<UrcDeptSectionAnlDimbgt> sect,
            String title
    ) {
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        printSetup.setLandscape(true);
        printSetup.setFitWidth((short) 1);
        printSetup.setFitHeight((short) 0);

        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        sheet.setMargin(Sheet.LeftMargin, 0.20);
        sheet.setMargin(Sheet.RightMargin, 0.20);
        sheet.setMargin(Sheet.TopMargin, 0.40);
        sheet.setMargin(Sheet.BottomMargin, 0.40);

        List<UrcDeptSectionAnlDimbgt> selectedSections = new ArrayList<>(sect);

        ExcelStyles styles = createStyles(workbook);

        List<Integer> sectionRows = new ArrayList<>();
        List<Integer> categoryRows = new ArrayList<>();
        List<Integer> totalRows = new ArrayList<>();
        List<Integer> coaHeaderRows = new ArrayList<>();

        int rowIdx = 0;

        rowIdx = buildReportHeader(workbook, sheet, styles, rowIdx, title);

        Row headerRow = sheet.createRow(rowIdx++);
        createQuarterHeaderRow(headerRow, styles.header);

        Coalevel1 incomeCoal = sampleCoalevel1Service.findByCode(1);
        SectionBuildResult incomeResult = buildCoaLevelSection(
                workbook,
                sheet,
                rowIdx,
                incomeCoal,
                "INCOME",
                selectedSections,
                styles,
                sectionRows,
                categoryRows,
                totalRows,
                coaHeaderRows,
                false
        );
        rowIdx = incomeResult.nextRowIdx();

        Coalevel1 revenueCoal = sampleCoalevel1Service.findByCode(2);
        SectionBuildResult revenueResult = buildCoaLevelSection(
                workbook,
                sheet,
                rowIdx,
                revenueCoal,
                "REVENUE EXPENDITURE",
                selectedSections,
                styles,
                sectionRows,
                categoryRows,
                totalRows,
                coaHeaderRows,
                true
        );
        rowIdx = revenueResult.nextRowIdx();

        Coalevel1 capitalCoal = sampleCoalevel1Service.findByCode(3);
        SectionBuildResult capitalResult = buildCoaLevelSection(
                workbook,
                sheet,
                rowIdx,
                capitalCoal,
                "CAPITAL EXPENDITURE",
                selectedSections,
                styles,
                sectionRows,
                categoryRows,
                totalRows,
                coaHeaderRows,
                true
        );
        rowIdx = capitalResult.nextRowIdx();

        QuarterValues revenueTotals = revenueResult.totals();
        QuarterValues capitalTotals = capitalResult.totals();

        boolean hasRevenue = revenueTotals.total().compareTo(BigDecimal.ZERO) > 0;
        boolean hasCapital = capitalTotals.total().compareTo(BigDecimal.ZERO) > 0;

        if (hasRevenue || hasCapital) {
            rowIdx++;
            rowIdx = buildGrandExpenditureTotal(
                    sheet,
                    rowIdx,
                    revenueTotals,
                    capitalTotals,
                    styles,
                    totalRows
            );
        }

        applyRowStyles(sheet, styles, sectionRows, categoryRows, totalRows, coaHeaderRows);
        configureColumnWidths(sheet);

        sheet.createFreezePane(0, 3);
    }

    private record SectionBuildResult(
            int nextRowIdx,
            QuarterValues totals) {

    }

    private int buildReportHeader(Workbook workbook, Sheet sheet, ExcelStyles styles, int rowIdx, String title) {
        Row row0 = sheet.createRow(rowIdx++);
        row0.setHeight((short) 650);

        try {
            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");
        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        Cell titleCell = row0.createCell(1);
        titleCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        titleCell.setCellStyle(styles.title);

        CellRangeAddress titleRange = new CellRangeAddress(0, 0, 1, 14);
        sheet.addMergedRegion(titleRange);
        setBottomBorderForRegion(sheet, titleRange);

        Row row1 = sheet.createRow(rowIdx++);
        Cell subtitleCell = row1.createCell(0);
        subtitleCell.setCellValue(HeaderExcel2Title(title).toUpperCase());
        subtitleCell.setCellStyle(styles.subTitle);

        CellRangeAddress subtitleRange = new CellRangeAddress(1, 1, 0, 14);
        sheet.addMergedRegion(subtitleRange);
        setBottomBorderForRegion(sheet, subtitleRange);

        return rowIdx;
    }

    private void createQuarterHeaderRow(Row row, CellStyle style) {
        String[] headers = {
            "COA CODE",
            "ACTIVITY",
            "ITEM",
            "COST",
            "QTY",
            "UNIT",
            "CUR",
            "TOTAL",
            "Q1 (Jul-Sep)",
            "Q2 (Oct-Dec)",
            "Q3 (Jan-Mar)",
            "Q4 (Apr-Jun)",
            "NOTES",
            "FUND SOURCE",
            "COST CENTRE"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }

        row.setHeight((short) 520);
    }

    private SectionBuildResult buildCoaLevelSection(
            Workbook workbook,
            Sheet sheet,
            int rowIdx,
            Coalevel1 coal,
            String sectionTitle,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            ExcelStyles styles,
            List<Integer> sectionRows,
            List<Integer> categoryRows,
            List<Integer> totalRows,
            List<Integer> coaHeaderRows,
            boolean expenditureStyleActivityName
    ) {
        if (!isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero2(comboBox2.getValue(), coal, selectedSections)) {
            return new SectionBuildResult(rowIdx, QuarterValues.zero());
        }

        Row sectionRow = sheet.createRow(rowIdx);
        sectionRow.createCell(0).setCellValue(sectionTitle);

        CellRangeAddress sectionRange = new CellRangeAddress(rowIdx, rowIdx, 0, 14);
        sheet.addMergedRegion(sectionRange);
        setBottomBorderForRegion(sheet, sectionRange);

        sectionRows.add(rowIdx);
        categoryRows.add(rowIdx);
        rowIdx++;

        List<COA> coaCodes = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(
                comboBox2.getValue(),
                coal,
                selectedSections,
                budgetType2.getSelectedItems()
        );

        for (COA coa : coaCodes) {
            rowIdx = buildCoaHeaderRow(
                    sheet,
                    rowIdx,
                    coa,
                    selectedSections,
                    styles,
                    coaHeaderRows
            );

            List<BudgetItems> items = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(
                    comboBox2.getValue(),
                    coa,
                    selectedSections,
                    budgetType2.getSelectedItems()
            );

            for (BudgetItems item : items) {
                Row row = sheet.createRow(rowIdx++);
                buildBudgetItemRow(row, item, styles.money, expenditureStyleActivityName);
            }
        }

        Row totalRow = sheet.createRow(rowIdx);

        String totalLabel = switch (sectionTitle) {
            case "INCOME" ->
                "TOTAL INCOME";
            case "REVENUE EXPENDITURE" ->
                "TOTAL REVENUE EXPENDITURE";
            case "CAPITAL EXPENDITURE" ->
                "TOTAL CAPITAL EXPENDITURE";
            default ->
                "TOTAL " + sectionTitle;
        };

        totalRow.createCell(0).setCellValue(totalLabel);

        CellRangeAddress totalRange = new CellRangeAddress(rowIdx, rowIdx, 0, 6);
        sheet.addMergedRegion(totalRange);
        setBottomTopBorderForRegion(sheet, totalRange);

        BigDecimal total = nz(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                comboBox2.getValue(),
                coal,
                selectedSections,
                budgetType2.getSelectedItems()
        ));

        QuarterValues quarterTotals = getQuarterValuesForCoalevel1(coal, selectedSections);

        QuarterValues resultTotals = new QuarterValues(
                total,
                quarterTotals.q1(),
                quarterTotals.q2(),
                quarterTotals.q3(),
                quarterTotals.q4()
        );

        setNumeric(totalRow.createCell(7), resultTotals.total(), styles.totalMoney);
        setNumeric(totalRow.createCell(8), resultTotals.q1(), styles.totalMoney);
        setNumeric(totalRow.createCell(9), resultTotals.q2(), styles.totalMoney);
        setNumeric(totalRow.createCell(10), resultTotals.q3(), styles.totalMoney);
        setNumeric(totalRow.createCell(11), resultTotals.q4(), styles.totalMoney);

        blankTextCells(totalRow, 12, 14);

        totalRows.add(rowIdx);
        rowIdx++;

        return new SectionBuildResult(rowIdx, resultTotals);
    }

    private int buildCoaHeaderRow(
            Sheet sheet,
            int rowIdx,
            COA coa,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            ExcelStyles styles,
            List<Integer> coaHeaderRows
    ) {
        Row row = sheet.createRow(rowIdx);

        row.createCell(0).setCellValue(coa.getCode());
        row.createCell(2).setCellValue(coa.getName());

        CellRangeAddress leftRange = new CellRangeAddress(rowIdx, rowIdx, 0, 1);
        sheet.addMergedRegion(leftRange);
        setBottomTopBorderForRegion(sheet, leftRange);

        CellRangeAddress nameRange = new CellRangeAddress(rowIdx, rowIdx, 2, 6);
        sheet.addMergedRegion(nameRange);
        setBottomTopBorderForRegion(sheet, nameRange);

        BigDecimal total = sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(
                comboBox2.getValue(), coa, selectedSections, budgetType2.getSelectedItems()
        );

        QuarterValues q = getQuarterValuesForCoa(coa, selectedSections);

        setNumeric(row.createCell(7), total, styles.money);
        setNumeric(row.createCell(8), q.q1(), styles.money);
        setNumeric(row.createCell(9), q.q2(), styles.money);
        setNumeric(row.createCell(10), q.q3(), styles.money);
        setNumeric(row.createCell(11), q.q4(), styles.money);

        blankTextCells(row, 12, 14);

        coaHeaderRows.add(rowIdx);
        return rowIdx + 1;
    }

    private void buildBudgetItemRow(
            Row row,
            BudgetItems item,
            CellStyle moneyStyle,
            boolean expenditureStyleActivityName
    ) {
        boolean incomeRow = !expenditureStyleActivityName;

        row.createCell(0).setCellValue("");

        if (incomeRow) {
            row.createCell(1).setCellValue(safe(item.getItem()));
            row.createCell(2).setCellValue("");

            Sheet sheet = row.getSheet();
            int rowNum = row.getRowNum();
            CellRangeAddress mergedItemRange = new CellRangeAddress(rowNum, rowNum, 1, 2);
            sheet.addMergedRegion(mergedItemRange);
        } else {
            String activityOrCoa = safe(item.getActivity().getName())
                    + " (" + safeTrim(item.getActivity().getActivityCode()) + ")";
            row.createCell(1).setCellValue(activityOrCoa);
            row.createCell(2).setCellValue(safe(item.getItem()));
        }

        setNumeric(row.createCell(3), item.getCost(), moneyStyle);
        setNumeric(row.createCell(4), item.getQty(), moneyStyle);

        row.createCell(5).setCellValue(safe(item.getUnitMeasure()));
        row.createCell(6).setCellValue(
                item.getCurrency() != null && item.getCurrency().getData() != null
                ? safe(item.getCurrency().getData().getCurrencyShort())
                : ""
        );

        setNumeric(row.createCell(7), calculateSumOfMonths(item), moneyStyle);
        setNumeric(row.createCell(8), q1(item), moneyStyle);
        setNumeric(row.createCell(9), q2(item), moneyStyle);
        setNumeric(row.createCell(10), q3(item), moneyStyle);
        setNumeric(row.createCell(11), q4(item), moneyStyle);

        row.createCell(12).setCellValue(safe(item.getNotes()));
        row.createCell(13).setCellValue(
                item.getBudgetType() != null ? safe(item.getBudgetType().getName()) : ""
        );
        row.createCell(14).setCellValue(
                item.getDeptUnit() != null ? safe(item.getDeptUnit().getNAME()) : ""
        );
    }

    private QuarterValues buildCoaLevelTotalRow(
            Sheet sheet,
            int rowIdx,
            Coalevel1 coal,
            String sectionTitle,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            ExcelStyles styles,
            List<Integer> totalRows
    ) {
        Row row = sheet.createRow(rowIdx);

        String label = switch (sectionTitle) {
            case "INCOME" ->
                "TOTAL INCOME";
            case "REVENUE EXPENDITURE" ->
                "TOTAL REVENUE EXPENDITURE";
            case "CAPITAL EXPENDITURE" ->
                "TOTAL CAPITAL EXPENDITURE";
            default ->
                "TOTAL " + sectionTitle;
        };

        row.createCell(0).setCellValue(label);

        CellRangeAddress range = new CellRangeAddress(rowIdx, rowIdx, 0, 6);
        sheet.addMergedRegion(range);
        setBottomTopBorderForRegion(sheet, range);

        BigDecimal total = sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                comboBox2.getValue(),
                coal,
                selectedSections,
                budgetType2.getSelectedItems()
        );

        QuarterValues q = getQuarterValuesForCoalevel1(coal, selectedSections);

        setNumeric(row.createCell(7), total, styles.totalMoney);
        setNumeric(row.createCell(8), q.q1(), styles.totalMoney);
        setNumeric(row.createCell(9), q.q2(), styles.totalMoney);
        setNumeric(row.createCell(10), q.q3(), styles.totalMoney);
        setNumeric(row.createCell(11), q.q4(), styles.totalMoney);

        totalRows.add(rowIdx);

        return new QuarterValues(
                total,
                q.q1(),
                q.q2(),
                q.q3(),
                q.q4()
        );
    }

    private int buildGrandExpenditureTotal(
            Sheet sheet,
            int rowIdx,
            QuarterValues revenue,
            QuarterValues capital,
            ExcelStyles styles,
            List<Integer> totalRows
    ) {
        Row row = sheet.createRow(rowIdx);
        row.setHeight((short) 580);

        row.createCell(0).setCellValue("TOTAL EXPENDITURE");

        CellRangeAddress range = new CellRangeAddress(rowIdx, rowIdx, 0, 6);
        sheet.addMergedRegion(range);
        setBottomTopBorderForRegion(sheet, range);

        BigDecimal total = nz(revenue.q1())
                .add(nz(revenue.q2()))
                .add(nz(revenue.q3()))
                .add(nz(revenue.q4()))
                .add(nz(capital.q1()))
                .add(nz(capital.q2()))
                .add(nz(capital.q3()))
                .add(nz(capital.q4()));

        BigDecimal q1 = nz(revenue.q1()).add(nz(capital.q1()));
        BigDecimal q2 = nz(revenue.q2()).add(nz(capital.q2()));
        BigDecimal q3 = nz(revenue.q3()).add(nz(capital.q3()));
        BigDecimal q4 = nz(revenue.q4()).add(nz(capital.q4()));

        setNumeric(row.createCell(7), total, styles.grandTotalMoney);
        setNumeric(row.createCell(8), q1, styles.grandTotalMoney);
        setNumeric(row.createCell(9), q2, styles.grandTotalMoney);
        setNumeric(row.createCell(10), q3, styles.grandTotalMoney);
        setNumeric(row.createCell(11), q4, styles.grandTotalMoney);

        totalRows.add(rowIdx);
        return rowIdx + 1;
    }

    private QuarterValues getQuarterValuesForCoa(
            COA coa,
            List<UrcDeptSectionAnlDimbgt> selectedSections
    ) {
        BigDecimal q1 = sumCoaMonths(coa, selectedSections, "jul", "aug", "sep");
        BigDecimal q2 = sumCoaMonths(coa, selectedSections, "oct", "nov", "dec");
        BigDecimal q3 = sumCoaMonths(coa, selectedSections, "jan", "feb", "mar");
        BigDecimal q4 = sumCoaMonths(coa, selectedSections, "apr", "may", "jun");

        return new QuarterValues(
                q1.add(q2).add(q3).add(q4),
                q1, q2, q3, q4
        );
    }

    private QuarterValues getQuarterValuesForCoalevel1(
            Coalevel1 coal,
            List<UrcDeptSectionAnlDimbgt> selectedSections
    ) {
        BigDecimal q1 = sumCoalevel1Months(coal, selectedSections, "jul", "aug", "sep");
        BigDecimal q2 = sumCoalevel1Months(coal, selectedSections, "oct", "nov", "dec");
        BigDecimal q3 = sumCoalevel1Months(coal, selectedSections, "jan", "feb", "mar");
        BigDecimal q4 = sumCoalevel1Months(coal, selectedSections, "apr", "may", "jun");

        return new QuarterValues(
                q1.add(q2).add(q3).add(q4),
                q1, q2, q3, q4
        );
    }

    private QuarterValues getQuarterValuesForCoalevel1List(
            List<Coalevel1> coaList,
            List<UrcDeptSectionAnlDimbgt> selectedSections
    ) {
        BigDecimal q1 = sumCoalevel1ListMonths(coaList, selectedSections, "jul", "aug", "sep");
        BigDecimal q2 = sumCoalevel1ListMonths(coaList, selectedSections, "oct", "nov", "dec");
        BigDecimal q3 = sumCoalevel1ListMonths(coaList, selectedSections, "jan", "feb", "mar");
        BigDecimal q4 = sumCoalevel1ListMonths(coaList, selectedSections, "apr", "may", "jun");

        return new QuarterValues(
                q1.add(q2).add(q3).add(q4),
                q1, q2, q3, q4
        );
    }

    private BigDecimal sumCoaMonths(
            COA coa,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            String... months
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (String month : months) {
            total = total.add(nz(
                    sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                            comboBox2.getValue(),
                            coa,
                            selectedSections,
                            budgetType2.getSelectedItems(),
                            month
                    )
            ));
        }

        return total;
    }

    private BigDecimal sumCoalevel1Months(
            Coalevel1 coal,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            String... months
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (String month : months) {
            total = total.add(nz(
                    sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(
                            comboBox2.getValue(),
                            coal,
                            selectedSections,
                            budgetType2.getSelectedItems(),
                            month
                    )
            ));
        }

        return total;
    }

    private BigDecimal sumCoalevel1ListMonths(
            List<Coalevel1> coaList,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            String... months
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (String month : months) {
            total = total.add(nz(
                    sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(
                            comboBox.getValue(),
                            coaList,
                            selectedSections,
                            budgetType.getSelectedItems(),
                            month
                    )
            ));
        }

        return total;
    }

    private BigDecimal monthSumByCoa(COA coa, List<UrcDeptSectionAnlDimbgt> sections, String month) {
        return nz(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                comboBox2.getValue(),
                coa,
                sections,
                budgetType2.getSelectedItems(),
                month
        ));
    }

    private BigDecimal monthSumByCoalevel1(Coalevel1 coal, List<UrcDeptSectionAnlDimbgt> sections, String month) {
        return nz(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(
                comboBox2.getValue(),
                coal,
                sections,
                budgetType2.getSelectedItems(),
                month
        ));
    }

    private BigDecimal monthSumByCoalevel1List(List<Coalevel1> coaList, List<UrcDeptSectionAnlDimbgt> sections, String month) {
        return nz(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(
                comboBox.getValue(),
                coaList,
                sections,
                budgetType.getSelectedItems(),
                month
        ));
    }

    private BigDecimal q1(BudgetItems t) {
        return nz(t.getJul()).add(nz(t.getAug())).add(nz(t.getSep()));
    }

    private BigDecimal q2(BudgetItems t) {
        return nz(t.getOct()).add(nz(t.getNov())).add(nz(t.getDec()));
    }

    private BigDecimal q3(BudgetItems t) {
        return nz(t.getJan()).add(nz(t.getFeb())).add(nz(t.getMar()));
    }

    private BigDecimal q4(BudgetItems t) {
        return nz(t.getApr()).add(nz(t.getMay())).add(nz(t.getJun()));
    }

    private ExcelStyles createStyles(Workbook workbook) {
        DataFormat df = workbook.createDataFormat();

        Font baseFont = workbook.createFont();
        baseFont.setFontName("Arial");
        baseFont.setFontHeightInPoints((short) 10);

        Font boldWhite = workbook.createFont();
        boldWhite.setFontName("Arial");
        boldWhite.setBold(true);
        boldWhite.setColor(IndexedColors.WHITE.getIndex());
        boldWhite.setFontHeightInPoints((short) 10);

        Font boldDark = workbook.createFont();
        boldDark.setFontName("Arial");
        boldDark.setBold(true);
        boldDark.setFontHeightInPoints((short) 10);

        Font titleFont = workbook.createFont();
        titleFont.setFontName("Arial");
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);

        CellStyle base = workbook.createCellStyle();
        base.setFont(baseFont);
        base.setWrapText(true);
        base.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        CellStyle title = workbook.createCellStyle();
        title.cloneStyleFrom(base);
        title.setFont(titleFont);
        title.setAlignment(HorizontalAlignment.CENTER);

        CellStyle subTitle = workbook.createCellStyle();
        subTitle.cloneStyleFrom(base);
        subTitle.setFont(boldDark);
        subTitle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle header = workbook.createCellStyle();
        header.cloneStyleFrom(base);
        header.setFont(boldWhite);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header.setBorderTop(BorderStyle.THIN);
        header.setBorderBottom(BorderStyle.THICK);
        header.setBorderLeft(BorderStyle.THIN);
        header.setBorderRight(BorderStyle.THIN);

        CellStyle money = workbook.createCellStyle();
        money.cloneStyleFrom(base);
        money.setDataFormat(df.getFormat("#,##0;[Red](#,##0);-"));
        money.setAlignment(HorizontalAlignment.RIGHT);

        CellStyle section = workbook.createCellStyle();
        section.cloneStyleFrom(base);
        section.setFont(boldDark);
        section.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        section.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        section.setBorderBottom(BorderStyle.THIN);

        CellStyle coaHeader = workbook.createCellStyle();
        coaHeader.cloneStyleFrom(base);
        coaHeader.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        coaHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        coaHeader.setBorderTop(BorderStyle.THIN);
        coaHeader.setBorderBottom(BorderStyle.THIN);

        CellStyle total = workbook.createCellStyle();
        total.cloneStyleFrom(base);
        total.setFont(boldDark);
        total.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        total.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        total.setBorderTop(BorderStyle.THICK);
        total.setBorderBottom(BorderStyle.DOUBLE);

        CellStyle totalMoney = workbook.createCellStyle();
        totalMoney.cloneStyleFrom(total);
        totalMoney.setDataFormat(df.getFormat("#,##0;[Red](#,##0);-"));
        totalMoney.setAlignment(HorizontalAlignment.RIGHT);

        CellStyle grandTotalMoney = workbook.createCellStyle();
        grandTotalMoney.cloneStyleFrom(totalMoney);

        return new ExcelStyles(base, title, subTitle, header, money, section, coaHeader, total, totalMoney, grandTotalMoney);
    }

    private void applyRowStyles(
            Sheet sheet,
            ExcelStyles styles,
            List<Integer> sectionRows,
            List<Integer> categoryRows,
            List<Integer> totalRows,
            List<Integer> coaHeaderRows
    ) {
        for (Row row : sheet) {
            for (int c = 0; c <= 14; c++) {
                Cell cell = row.getCell(c);
                if (cell == null) {
                    cell = row.createCell(c);
                }

                if (row.getRowNum() <= 1) {
                    if (row.getRowNum() == 0) {
                        cell.setCellStyle(styles.title);
                    } else {
                        cell.setCellStyle(styles.subTitle);
                    }
                    continue;
                }

                if (row.getRowNum() == 2) {
                    cell.setCellStyle(styles.header);
                    continue;
                }

                if (sectionRows.contains(row.getRowNum()) || categoryRows.contains(row.getRowNum())) {
                    cell.setCellStyle(styles.section);
                    continue;
                }

                if (totalRows.contains(row.getRowNum())) {
                    if (c == 3 || c == 4 || (c >= 7 && c <= 11)) {
                        cell.setCellStyle(styles.totalMoney);
                    } else {
                        cell.setCellStyle(styles.total);
                    }
                    continue;
                }

                if (coaHeaderRows.contains(row.getRowNum())) {
                    if (c == 3 || c == 4 || (c >= 7 && c <= 11)) {
                        cell.setCellStyle(styles.money);
                    } else {
                        cell.setCellStyle(styles.coaHeader);
                    }
                    continue;
                }

                if (c == 3 || c == 4 || (c >= 7 && c <= 11)) {
                    cell.setCellStyle(styles.money);
                } else {
                    cell.setCellStyle(styles.base);
                }
            }
        }
    }

    private void configureColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 4200);   // Activity Code
        sheet.setColumnWidth(1, 7000);   // COA Code
        sheet.setColumnWidth(2, 10000);  // Item
        sheet.setColumnWidth(3, 3500);   // Cost
        sheet.setColumnWidth(4, 3000);   // Qty
        sheet.setColumnWidth(5, 4200);   // Unit
        sheet.setColumnWidth(6, 2500);   // Cur
        sheet.setColumnWidth(7, 4500);   // Total
        sheet.setColumnWidth(8, 4200);   // Q1
        sheet.setColumnWidth(9, 4200);   // Q2
        sheet.setColumnWidth(10, 4200);  // Q3
        sheet.setColumnWidth(11, 4200);  // Q4
        sheet.setColumnWidth(12, 6500);  // Notes
        sheet.setColumnWidth(13, 5000);  // Fund Source
        sheet.setColumnWidth(14, 5000);  // Cost Centre
    }

    private void setNumeric(Cell cell, BigDecimal value, CellStyle style) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(nz(value).doubleValue());
        cell.setCellStyle(style);
    }

    private void blankTextCells(Row row, int from, int to) {
        for (int i = from; i <= to; i++) {
            row.createCell(i).setCellValue("");
        }
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private record QuarterValues(
            BigDecimal total,
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4) {

        static QuarterValues zero() {
            return new QuarterValues(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }
    }

    private static class ExcelStyles {

        private final CellStyle base;
        private final CellStyle title;
        private final CellStyle subTitle;
        private final CellStyle header;
        private final CellStyle money;
        private final CellStyle section;
        private final CellStyle coaHeader;
        private final CellStyle total;
        private final CellStyle totalMoney;
        private final CellStyle grandTotalMoney;

        public ExcelStyles(
                CellStyle base,
                CellStyle title,
                CellStyle subTitle,
                CellStyle header,
                CellStyle money,
                CellStyle section,
                CellStyle coaHeader,
                CellStyle total,
                CellStyle totalMoney,
                CellStyle grandTotalMoney
        ) {
            this.base = base;
            this.title = title;
            this.subTitle = subTitle;
            this.header = header;
            this.money = money;
            this.section = section;
            this.coaHeader = coaHeader;
            this.total = total;
            this.totalMoney = totalMoney;
            this.grandTotalMoney = grandTotalMoney;
        }
    }

    private void setBottomBorderForRegion(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
    }

    private void setBottomTopBorderForRegion(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderBottom(BorderStyle.DOUBLE, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.THICK, region, sheet);
    }

    public boolean isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits) {

        return sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(budget, coalevel1, deptUnits, budgetType.getSelectedItems());
    }

    public BigDecimal findSumByBudgetCoalevel1AndDeptUnits(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits) {
        return sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(budget, coalevel1, deptUnits, budgetType.getSelectedItems());
    }

    public boolean isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero2(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits) {

        return sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(budget, coalevel1, deptUnits, budgetType2.getSelectedItems());
    }

    public BigDecimal findSumByBudgetCoalevel1AndDeptUnits2(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits) {
        return sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(budget, coalevel1, deptUnits, budgetType2.getSelectedItems());
    }

    private void exportAndDownloadActvityBudgetItems2(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderAndBodyActivitiesByBudgetRow(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Budget Download " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadSummaryBudget(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderAndBodySummaryBudget(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Budget Download " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadSummaryBudgetQtr(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderAndBodySummaryBudgetQtr(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Qtr Budget Download " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadSummaryBudgetQtrActuals(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderAndBodySummaryBudgetQtrActuals(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Qtr Budget Download " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadAccountcodeBudgetItems2(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            // columns=reportColumnsCombo.select(items)
            createHeaderAndBodyAccountCodeDetailByBudgetRow(workbook, sheet);
            //createDataRows(sheet, people);
            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Budget Download " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadCustomAccountcodeBudgetItems2(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // columns=reportColumnsCombo.select(items)
            if (!CustomDetailedBudgetReportImpcomboBox.isEmpty()) {

                CustomDetailedBudgetReportImp reportImp = CustomDetailedBudgetReportImpcomboBox.getValue();
                if (reportImp != null) {
                    List<CustomDetailedBudgetReport> report = sampleCustomDetailedBudgetReportService.findByBudgetreport(reportImp);
                    for (CustomDetailedBudgetReport w : report) {
                        if (w.getDeptsection() != null) {
                            // Create sheet for each item in the report list
                            Sheet sheet = workbook.createSheet(w.getSheetname() + " Budget " + budget.getFinancialYear());
                            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
                            sheet.getPrintSetup().setLandscape(true);
                            // Uncomment the following line if needed
                            createHeaderAndBodyAccountCodeDetailByBudgetRow2(workbook, sheet, w.getDeptsection(), w.getSheetname());
                        }
                    }
                }

                // Write the workbook to a byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);

                // Create a StreamResource with the Excel data
                StreamResource resource = new StreamResource("Custom Budget Download " + budget.getFinancialYear() + ".xlsx", ()
                        -> new ByteArrayInputStream(outputStream.toByteArray()));

                // Create an Anchor component with the StreamResource
                Anchor downloadLink2 = new Anchor(resource, "");
                downloadLink2.getElement().setAttribute("download", true);
                add(downloadLink2);
                // Programmatically click the download link to initiate the download
                downloadLink2.getElement().callJsFunction("click");
            }
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

    private void setCellStyleWithBackground(Cell cell, CellStyle cellStyle) {
        cell.setCellStyle(cellStyle);
    }

    private String HeaderExcel(String action) {
        StringBuilder selectedsections2 = new StringBuilder();
        for (UrcDeptSectionAnlDimbgt a : sections.getSelectedItems()) {
            selectedsections2.append(a.getNAME()).append(" ,");
        }
        // Check if the StringBuilder is not empty before deleting the last character
        if (!selectedsections2.isEmpty()) {
            selectedsections2.deleteCharAt(selectedsections2.length() - 1);
        }
        if (countCommas(selectedsections2.toString()) > 0) {
            selectedsections2.append("Section(s) | ");
        } else {
            selectedsections2.append("Section | ");
        }
        for (Organisation b : budgetType.getSelectedItems()) {
            selectedsections2.append(b.getName()).append(",");
        }
        if (!selectedsections2.isEmpty()) {
            selectedsections2.deleteCharAt(selectedsections2.length() - 1).append(" | Budget ").append(comboBox.getValue().getFinancialYear()).append(" AS ORGANISED BY ").append(action);
        }
        return selectedsections2.toString();
    }

    private String HeaderExcel3() {
        StringBuilder selectedsections2 = new StringBuilder();
        for (UrcDeptSectionAnlDimbgt a : sections.getSelectedItems()) {
            selectedsections2.append(a.getNAME()).append(" ,");
        }
        // Check if the StringBuilder is not empty before deleting the last character
        if (!selectedsections2.isEmpty()) {
            selectedsections2.deleteCharAt(selectedsections2.length() - 1);
        }
        if (countCommas(selectedsections2.toString()) > 0) {
            selectedsections2.append("Section(s) | ");
        } else {
            selectedsections2.append("Section | ");
        }
        for (Organisation b : budgetType.getSelectedItems()) {
            selectedsections2.append(b.getName()).append(",");
        }
        if (!selectedsections2.isEmpty()) {
            selectedsections2.deleteCharAt(selectedsections2.length() - 1).append(" |Summary Budget ").append(comboBox.getValue().getFinancialYear());
        }
        return selectedsections2.toString();
    }

    private String HeaderExcel2(String action) {
        StringBuilder selectedsections2 = new StringBuilder();

        // Check if the StringBuilder is not empty before deleting the last character
        if (!selectedsections2.isEmpty()) {
            selectedsections2.deleteCharAt(selectedsections2.length() - 1);
        }
        if (countCommas(selectedsections2.toString()) > 0) {
            selectedsections2.append("Section(s) | ");
        } else {
            selectedsections2.append("Section | ");
        }
        for (Organisation b : budgetType2.getSelectedItems()) {
            selectedsections2.append(b.getName()).append(",");
        }
        if (!selectedsections2.isEmpty()) {
            selectedsections2.deleteCharAt(selectedsections2.length() - 1).append(" | Budget ").append(comboBox2.getValue().getFinancialYear()).append(" AS ORGANISED BY ").append(action);
        }
        return selectedsections2.toString();
    }

    private String HeaderExcel2Title(String action) {

        return action.toUpperCase();
    }

    private static int countCommas(String input) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == ',') {
                count++;
            }
        }
        return count;
    }

    public BigDecimal calculateSumOfMonths(BudgetItems e) {
        BigDecimal sum = BigDecimal.ZERO;

        // Add all the months, handling null values
        sum = sum.add(e.getJul() != null ? e.getJul() : BigDecimal.ZERO)
                .add(e.getAug() != null ? e.getAug() : BigDecimal.ZERO)
                .add(e.getSep() != null ? e.getSep() : BigDecimal.ZERO)
                .add(e.getOct() != null ? e.getOct() : BigDecimal.ZERO)
                .add(e.getNov() != null ? e.getNov() : BigDecimal.ZERO)
                .add(e.getDec() != null ? e.getDec() : BigDecimal.ZERO)
                .add(e.getJan() != null ? e.getJan() : BigDecimal.ZERO)
                .add(e.getFeb() != null ? e.getFeb() : BigDecimal.ZERO)
                .add(e.getMar() != null ? e.getMar() : BigDecimal.ZERO)
                .add(e.getApr() != null ? e.getApr() : BigDecimal.ZERO)
                .add(e.getMay() != null ? e.getMay() : BigDecimal.ZERO)
                .add(e.getJun() != null ? e.getJun() : BigDecimal.ZERO);

        return sum;
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

    private Row createHeaderRow(Sheet sheet, int rowNum, List<String> titles) {
        Row row = sheet.createRow((short) rowNum);
        for (int i = 0; i < titles.size(); i++) {
            row.createCell((short) i).setCellValue(titles.get(i));
        }
        return row;
    }

    private Row createHeaderRow(Sheet sheet, int rowNum, String a, String b, double c, double d, double e, double f, double g,
            double h, double i, double j, double k, double l, double m, double n, double o) {
        Row row = sheet.createRow((short) rowNum);
        row.createCell((short) 0).setCellValue(a);
        row.createCell((short) 1).setCellValue(b);
        row.createCell((short) 2).setCellValue(c);
        row.createCell((short) 3).setCellValue(d);
        row.createCell((short) 4).setCellValue(e);
        row.createCell((short) 5).setCellValue(f);
        row.createCell((short) 6).setCellValue(g);
        row.createCell((short) 7).setCellValue(h);
        row.createCell((short) 8).setCellValue(i);
        row.createCell((short) 9).setCellValue(j);
        row.createCell((short) 10).setCellValue(k);
        row.createCell((short) 11).setCellValue(l);
        row.createCell((short) 12).setCellValue(m);
        row.createCell((short) 13).setCellValue(n);
        row.createCell((short) 14).setCellValue(o);

        return row;
    }

    private Row createHeaderRow(Sheet sheet, int rowNum, String a, String b, double c, double d, double e, double f, double g) {
        Row row = sheet.createRow((short) rowNum);
        row.createCell((short) 0).setCellValue(a);
        row.createCell((short) 1).setCellValue(b);
        row.createCell((short) 2).setCellValue(c);
        row.createCell((short) 3).setCellValue(d);
        row.createCell((short) 4).setCellValue(e);
        row.createCell((short) 5).setCellValue(f);
        row.createCell((short) 6).setCellValue(g);

        return row;
    }

    private Row createHeaderRow(Sheet sheet, int rowNum, String a, String b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l) {
        Row row = sheet.createRow((short) rowNum);
        row.createCell((short) 0).setCellValue(a);
        row.createCell((short) 1).setCellValue(b);
        row.createCell((short) 2).setCellValue(c);
        row.createCell((short) 3).setCellValue(d);
        row.createCell((short) 4).setCellValue(e);
        row.createCell((short) 5).setCellValue(f);
        row.createCell((short) 6).setCellValue(g);
        row.createCell((short) 7).setCellValue(h);
        row.createCell((short) 8).setCellValue(i);
        row.createCell((short) 9).setCellValue(j);
        row.createCell((short) 10).setCellValue(k);
        row.createCell((short) 11).setCellValue(l);

        return row;
    }

    private void createHeaderAndBodySummaryBudget(Workbook workbook, Sheet sheet) {
        sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
        List<Integer> title = new ArrayList();
        List<Integer> titleJustBold = new ArrayList();
        List<Integer> titleBoldBlue = new ArrayList();
        List<Integer> titleBoldRed = new ArrayList();
        List<Integer> titleBoldTotal = new ArrayList();
        List<Integer> titleBoldOrange = new ArrayList();
        int startrow100 = 0;

        //sheet.setFitToPage(true);
        //sheet.setHorizontallyCenter(true);
        short rowHeight = 500; // Adjust the height as needed
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        sheet.setFitToPage(true);

        // Create a cell style with the specified font
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

        // Set default style for the sheet
// Create a bold and centered style with a bottom border
        CellStyle boldCenteredStyle = workbook.createCellStyle();
        boldCenteredStyle.cloneStyleFrom(cellStyle);
        boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCenteredStyle.setFont(boldFont);
        boldCenteredStyle.setBorderBottom(BorderStyle.THIN);
        Row headerRow = sheet.createRow(0);
        //int columnWidth = 10000; // Adjust the width as needed
        //sheet.setColumnWidth(0, columnWidth);
        short tr = 2;

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
        title.add(0);
        CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 14);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        Row header2 = sheet.createRow(1);
        title.add(1);
        Cell header2Cell = header2.createCell(0);
        header2Cell.setCellValue(HeaderExcel3().toUpperCase());
        CellRangeAddress cellRange2 = new CellRangeAddress(1, 1, 0, 14);
        sheet.addMergedRegion(cellRange2);
        setBottomBorderForRegion(sheet, cellRange2);

        titleJustBold.add((int) tr);
        List<String> titles = Arrays.asList("COA CODE", "DETAILS", "BUDGET (TOTAL)", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "JAN", "FEB", "MAR", "APR", "MAY", "JUN");
        Row Q2 = createHeaderRow(sheet, tr, titles);
        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "VOLUMES /STATS", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q3 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Northern route", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q4 = createHeaderRow(sheet, tr, titles);
        tr++;

        MonthlySumResponseFreight mon = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));
        Row Q5 = createHeaderRow(sheet, tr, "ZFVNR -EXP", "Net Tons- Exports",
                mon.getTotal().doubleValue(), mon.getJul().doubleValue(), mon.getAug().doubleValue(), mon.getSep().doubleValue(), mon.getOct().doubleValue(),
                mon.getNov().doubleValue(), mon.getDec().doubleValue(), mon.getJan().doubleValue(), mon.getFeb().doubleValue(), mon.getMar().doubleValue(),
                mon.getApr().doubleValue(), mon.getMay().doubleValue(), mon.getJun().doubleValue());

        tr++;
        MonthlySumResponseFreight mon2 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
        Row Q6 = createHeaderRow(sheet, tr, "ZFVNR-IMP", "Net Tons -Imports",
                mon2.getTotal().doubleValue(), mon2.getJul().doubleValue(), mon2.getAug().doubleValue(), mon2.getSep().doubleValue(), mon2.getOct().doubleValue(),
                mon2.getNov().doubleValue(), mon2.getDec().doubleValue(), mon2.getJan().doubleValue(), mon2.getFeb().doubleValue(), mon2.getMar().doubleValue(),
                mon2.getApr().doubleValue(), mon2.getMay().doubleValue(), mon2.getJun().doubleValue());

        tr++;

        MonthlySumResponseFreight mon3 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
        Row Q7 = createHeaderRow(sheet, tr, "ZFVTN-LC", "Local Net Tons",
                mon3.getTotal().doubleValue(), mon3.getJul().doubleValue(), mon3.getAug().doubleValue(), mon3.getSep().doubleValue(), mon3.getOct().doubleValue(),
                mon3.getNov().doubleValue(), mon3.getDec().doubleValue(), mon3.getJan().doubleValue(), mon3.getFeb().doubleValue(), mon3.getMar().doubleValue(),
                mon3.getApr().doubleValue(), mon3.getMay().doubleValue(), mon3.getJun().doubleValue());

        tr++;
        titleBoldBlue.add((int) tr);
        Row Q8 = createHeaderRow(sheet, tr, "", "Total Tons-Northern",
                totalByRoutes(1).getTotal().doubleValue(), totalByRoutes(1).getJul().doubleValue(), totalByRoutes(1).getAug().doubleValue(),
                totalByRoutes(1).getSep().doubleValue(), totalByRoutes(1).getOct().doubleValue(),
                totalByRoutes(1).getNov().doubleValue(), totalByRoutes(1).getDec().doubleValue(), totalByRoutes(1).getJan().doubleValue(), totalByRoutes(1).getFeb().doubleValue(), totalByRoutes(1).getMar().doubleValue(),
                totalByRoutes(1).getApr().doubleValue(), totalByRoutes(1).getMay().doubleValue(), totalByRoutes(1).getJun().doubleValue());

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Southern route", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q9 = createHeaderRow(sheet, tr, titles);

        tr++;

        MonthlySumResponseFreight mon4 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111105", comboBox.getValue()));
        Row Q10 = createHeaderRow(sheet, tr, "ZFVSR -EXP", "Net Tons -Exports",
                mon4.getTotal().doubleValue(), mon4.getJul().doubleValue(), mon4.getAug().doubleValue(), mon4.getSep().doubleValue(), mon4.getOct().doubleValue(),
                mon4.getNov().doubleValue(), mon4.getDec().doubleValue(), mon4.getJan().doubleValue(), mon4.getFeb().doubleValue(), mon4.getMar().doubleValue(),
                mon4.getApr().doubleValue(), mon4.getMay().doubleValue(), mon4.getJun().doubleValue());

        tr++;

        MonthlySumResponseFreight mon5 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111104", comboBox.getValue()));
        Row Q11 = createHeaderRow(sheet, tr, "ZFVSR-IMP", "Net Tons -Imports",
                mon5.getTotal().doubleValue(), mon5.getJul().doubleValue(), mon5.getAug().doubleValue(), mon5.getSep().doubleValue(), mon5.getOct().doubleValue(),
                mon5.getNov().doubleValue(), mon5.getDec().doubleValue(), mon5.getJan().doubleValue(), mon5.getFeb().doubleValue(), mon5.getMar().doubleValue(),
                mon5.getApr().doubleValue(), mon5.getMay().doubleValue(), mon5.getJun().doubleValue());

        tr++;

        MonthlySumResponseFreight mon6 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111106", comboBox.getValue()));
        Row Q12 = createHeaderRow(sheet, tr, "ZFVTSR-LC", "Local Net Tons",
                mon6.getTotal().doubleValue(), mon6.getJul().doubleValue(), mon6.getAug().doubleValue(), mon6.getSep().doubleValue(), mon6.getOct().doubleValue(),
                mon6.getNov().doubleValue(), mon6.getDec().doubleValue(), mon6.getJan().doubleValue(), mon6.getFeb().doubleValue(), mon6.getMar().doubleValue(),
                mon6.getApr().doubleValue(), mon6.getMay().doubleValue(), mon6.getJun().doubleValue());

        tr++;
        titleBoldBlue.add((int) tr);
        Row Q13 = createHeaderRow(sheet, tr, "", "Total Tons-Southern",
                totalByRoutes(2).getTotal().doubleValue(), totalByRoutes(2).getJul().doubleValue(), totalByRoutes(2).getAug().doubleValue(),
                totalByRoutes(2).getSep().doubleValue(), totalByRoutes(2).getOct().doubleValue(),
                totalByRoutes(2).getNov().doubleValue(), totalByRoutes(2).getDec().doubleValue(), totalByRoutes(2).getJan().doubleValue(),
                totalByRoutes(2).getFeb().doubleValue(), totalByRoutes(2).getMar().doubleValue(),
                totalByRoutes(2).getApr().doubleValue(), totalByRoutes(2).getMay().doubleValue(), totalByRoutes(2).getJun().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        Row Q14 = createHeaderRow(sheet, tr, "", "Total",
                totalByRoutes().getTotal().doubleValue(), totalByRoutes().getJul().doubleValue(), totalByRoutes().getAug().doubleValue(),
                totalByRoutes().getSep().doubleValue(), totalByRoutes().getOct().doubleValue(),
                totalByRoutes().getNov().doubleValue(), totalByRoutes().getDec().doubleValue(), totalByRoutes().getJan().doubleValue(),
                totalByRoutes().getFeb().doubleValue(), totalByRoutes().getMar().doubleValue(),
                totalByRoutes().getApr().doubleValue(), totalByRoutes().getMay().doubleValue(), totalByRoutes().getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Passengers", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q15 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZPAS-KNR", "Passengers - Kampala-Namanve route", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q16 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZPAS-KOR", "Passengers - Kampala-Other route", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q17 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total Passengers", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q18 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZTPR-KNV", "Ticket price-Kampala-Namanve", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q19 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZTPR-KPB", "Ticket price-Kampalal-PortBell", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q20 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Southern route Voyages:", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q21 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVK", "MV-Kaawa", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q22 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVP", "MV-Pamba", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q23 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total voyages-URC", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q24 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVU", "MV,Umoja", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q25 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVH", "MV-Uhuru", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q26 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total voyages-other", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q27 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Gross Total voyages", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q28 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Number of trains", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q29 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZNOTR", "NTK ('000)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q30 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZNOTR-GTK", "GTK(000)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q31 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Fuel (Litres)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q32 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-PSER", "Fuel-Passenger services", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q33 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-NR", "Fuel  -Northern route", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q34 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-CR", "Fuel -Central route(Marine)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q35 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("ZNOTR-CRN", "Crane", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q36 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total  freight Fuel-(Ltrs)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q37 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZPRPL-DIS", "Price per litre-Diesel", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q38 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZAVPR-PPS", "Average price per passenger train tkt", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q39 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Efficiency", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q40 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-NTK", "Fuel efficiency-NTK ('M)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q42 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-GTK", "Fuel efficiency-GTK'(M)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q43 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-WTA", "WTA (days)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q44 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-TRA", "Transit days", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q45 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-LOC", "Locomotive Efficiency (Kms per hr)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q46 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-NOE", "No. of employees", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q47 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Employee productivity(Revenue)", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q48 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000");
        Row Q49 = createHeaderRow(sheet, tr, titles);

        List<MonthlySumResponseFreight> listIncomeTotals = new ArrayList<>();
        List<MonthlySumResponseFreight> listIncomeTotals2 = new ArrayList<>();//Total Other Administration Expenses
        List<MonthlySumResponseFreight> listIncomeTotals3 = new ArrayList<>();//Total Administration Expenses
        List<MonthlySumResponseFreight> listIncomeTotals4 = new ArrayList<>();//Total variable costs
        List<MonthlySumResponseFreight> listIncomeTotals5 = new ArrayList<>();//Depreciation
        List<MonthlySumResponseFreight> listVariableCosts = new ArrayList<>();//VariableCosts
        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "INCOME", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q50 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Re-current Income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q51 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Assets Hire", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q52 = createHeaderRow(sheet, tr, titles);
        startrow100 = tr;
        List<URC_ACNT> findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("1112");
        List<COA> listCoas = new ArrayList<>();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            tr++;
            listCoas.add(coa);

            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);

        MonthlySumResponseFreight mon101T = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T);
        Row Q54 = createHeaderRow(sheet, tr, "", "Total Assets hire income",
                mon101T.getTotal().doubleValue(), mon101T.getJul().doubleValue(), mon101T.getAug().doubleValue(), mon101T.getSep().doubleValue(), mon101T.getOct().doubleValue(),
                mon101T.getNov().doubleValue(), mon101T.getDec().doubleValue(), mon101T.getJan().doubleValue(), mon101T.getFeb().doubleValue(), mon101T.getMar().doubleValue(),
                mon101T.getApr().doubleValue(), mon101T.getMay().doubleValue(), mon101T.getJun().doubleValue());

        listCoas.clear();

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Freight Services", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q55 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Northern Route", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q56 = createHeaderRow(sheet, tr, titles);

        listCoas.clear();
        List<String> ss = new ArrayList<>();
        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());
        }
        ss.clear();

        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight mon101T2 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T2);
        Row Q57 = createHeaderRow(sheet, tr, "", "Total freight-Northern Route",
                mon101T2.getTotal().doubleValue(), mon101T2.getJul().doubleValue(), mon101T2.getAug().doubleValue(), mon101T2.getSep().doubleValue(), mon101T2.getOct().doubleValue(),
                mon101T2.getNov().doubleValue(), mon101T2.getDec().doubleValue(), mon101T2.getJan().doubleValue(), mon101T2.getFeb().doubleValue(), mon101T2.getMar().doubleValue(),
                mon101T2.getApr().doubleValue(), mon101T2.getMay().doubleValue(), mon101T2.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Southern Route", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q58 = createHeaderRow(sheet, tr, titles);
        ss.addAll(Arrays.asList("4", "5", "6"));
        listCoas.clear();
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight mon101T3 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T3);
        Row Q59 = createHeaderRow(sheet, tr, "", "Total freight-Southern Route",
                mon101T3.getTotal().doubleValue(), mon101T3.getJul().doubleValue(), mon101T3.getAug().doubleValue(), mon101T3.getSep().doubleValue(), mon101T3.getOct().doubleValue(),
                mon101T3.getNov().doubleValue(), mon101T3.getDec().doubleValue(), mon101T3.getJan().doubleValue(), mon101T3.getFeb().doubleValue(), mon101T3.getMar().doubleValue(),
                mon101T3.getApr().doubleValue(), mon101T3.getMay().doubleValue(), mon101T3.getJun().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        titles = Arrays.asList("", "Total freight Services", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q60 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        /*        ss.addAll(Arrays.asList("10", "09", "11"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("1111", ss);*/
        ss.addAll(Arrays.asList("0", "1"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11111", ss);
        ss.clear();

        ss.addAll(Arrays.asList("9"));
        List<URC_ACNT> findByAcntCodeStartingWith3 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith3);

        ss.addAll(Arrays.asList("1", "2", "3", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith2 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11130", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith2);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight mon101T4 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T4);
        Row Q61 = createHeaderRow(sheet, tr, "", "Total Other fees",
                mon101T4.getTotal().doubleValue(), mon101T4.getJul().doubleValue(), mon101T4.getAug().doubleValue(), mon101T4.getSep().doubleValue(), mon101T4.getOct().doubleValue(),
                mon101T4.getNov().doubleValue(), mon101T4.getDec().doubleValue(), mon101T4.getJan().doubleValue(), mon101T4.getFeb().doubleValue(), mon101T4.getMar().doubleValue(),
                mon101T4.getApr().doubleValue(), mon101T4.getMay().doubleValue(), mon101T4.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Rent Income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q62 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11140", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T5 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T5);

        Row Q64 = createHeaderRow(sheet, tr, "", "Total Rent Income",
                mon101T5.getTotal().doubleValue(), mon101T5.getJul().doubleValue(), mon101T5.getAug().doubleValue(), mon101T5.getSep().doubleValue(), mon101T5.getOct().doubleValue(),
                mon101T5.getNov().doubleValue(), mon101T5.getDec().doubleValue(), mon101T5.getJan().doubleValue(), mon101T5.getFeb().doubleValue(), mon101T5.getMar().doubleValue(),
                mon101T5.getApr().doubleValue(), mon101T5.getMay().doubleValue(), mon101T5.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Passenger Ticket Sales", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q65 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11160", ss);
        ss.clear();
        ss.addAll(Arrays.asList("4", "5"));
        List<URC_ACNT> findByAcntCodeStartingWith34 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11170", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith34);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());
        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T6 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T6);
        Row Q66 = createHeaderRow(sheet, tr, "", "Total Passenger Service Income",
                mon101T6.getTotal().doubleValue(), mon101T6.getJul().doubleValue(), mon101T6.getAug().doubleValue(), mon101T6.getSep().doubleValue(), mon101T6.getOct().doubleValue(),
                mon101T6.getNov().doubleValue(), mon101T6.getDec().doubleValue(), mon101T6.getJan().doubleValue(), mon101T6.getFeb().doubleValue(), mon101T6.getMar().doubleValue(),
                mon101T6.getApr().doubleValue(), mon101T6.getMay().doubleValue(), mon101T6.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Miscellaneous Income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q67 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("2", "3", "7"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11170", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1"));
        List<URC_ACNT> findByAcntCodeStartingWith35 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11180", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith35);
        ss.clear();

        List<URC_ACNT> findByAcntCodeStartingWith36 = urcAcntService.findByAcntCode("145003");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith36);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T7 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T7);
        Row Q68 = createHeaderRow(sheet, tr, "", "Total-Miscellaneous Receipts",
                mon101T7.getTotal().doubleValue(), mon101T7.getJul().doubleValue(), mon101T7.getAug().doubleValue(), mon101T7.getSep().doubleValue(), mon101T7.getOct().doubleValue(),
                mon101T7.getNov().doubleValue(), mon101T7.getDec().doubleValue(), mon101T7.getJan().doubleValue(), mon101T7.getFeb().doubleValue(), mon101T7.getMar().doubleValue(),
                mon101T7.getApr().doubleValue(), mon101T7.getMay().doubleValue(), mon101T7.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Passenger Ticket Sales", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q69 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Re-Current Income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q70 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Non -Recurrent Income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q71 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("111803");

        List<URC_ACNT> findByAcntCodeStartingWith37 = urcAcntService.findByAcntCode("131101");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith37);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }
        tr++;
        titles = Arrays.asList("", "Institutional Support-Development-Trr-Gulu", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q72 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Inst.Support-Dev.:Trr-Gulu-Suppl.", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q73 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("111802");

        List<URC_ACNT> findByAcntCodeStartingWith38 = urcAcntService.findByAcntCode("133202");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith38);
//listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T8 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T8);
        Row Q74 = createHeaderRow(sheet, tr, "", "Total-Non-Recurring Income",
                mon101T8.getTotal().doubleValue(), mon101T8.getJul().doubleValue(), mon101T8.getAug().doubleValue(), mon101T8.getSep().doubleValue(), mon101T8.getOct().doubleValue(),
                mon101T8.getNov().doubleValue(), mon101T8.getDec().doubleValue(), mon101T8.getJan().doubleValue(), mon101T8.getFeb().doubleValue(), mon101T8.getMar().doubleValue(),
                mon101T8.getApr().doubleValue(), mon101T8.getMay().doubleValue(), mon101T8.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Exceptional Income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q75 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Receivable from MoFPED", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q76 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldTotal.add((int) tr);
        titles = Arrays.asList("", "Total exceptional income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q77 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal1 = calculateTotal(listIncomeTotals);
        Row Q78 = createHeaderRow(sheet, tr, "", "Total Income",
                calculateTotal1.getTotal().doubleValue(), calculateTotal1.getJul().doubleValue(), calculateTotal1.getAug().doubleValue(), calculateTotal1.getSep().doubleValue(), calculateTotal1.getOct().doubleValue(),
                calculateTotal1.getNov().doubleValue(), calculateTotal1.getDec().doubleValue(), calculateTotal1.getJan().doubleValue(), calculateTotal1.getFeb().doubleValue(), calculateTotal1.getMar().doubleValue(),
                calculateTotal1.getApr().doubleValue(), calculateTotal1.getMay().doubleValue(), calculateTotal1.getJun().doubleValue());

        listIncomeTotals.clear();
        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "EXPENDITURE", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q79 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Direct Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q80 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Fuel ,Lubricants and Oils", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q81 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        ss.addAll(Arrays.asList("4", "5", "6", "7", "8", "9"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22700", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith39 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22701", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith39);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T9 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T9);
        listIncomeTotals4.add(mon101T9);
        Row Q82 = createHeaderRow(sheet, tr, "", "Total-Fuel",
                mon101T9.getTotal().doubleValue(), mon101T9.getJul().doubleValue(), mon101T9.getAug().doubleValue(), mon101T9.getSep().doubleValue(), mon101T9.getOct().doubleValue(),
                mon101T9.getNov().doubleValue(), mon101T9.getDec().doubleValue(), mon101T9.getJan().doubleValue(), mon101T9.getFeb().doubleValue(), mon101T9.getMar().doubleValue(),
                mon101T9.getApr().doubleValue(), mon101T9.getMay().doubleValue(), mon101T9.getJun().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q83 = createHeaderRow(sheet, tr, titles);

        tr++;

        listCoas.clear();
        COA coa1 = sampleCoaService.findByCodeAndBudget("224002", comboBox.getValue());
        listCoas.add(coa1);
        MonthlySumResponseFreight mon101A = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("224002", comboBox.getValue()));
        Row Q84 = createHeaderRow(sheet, tr, "224002", "Passenger Services Expenses",
                mon101A.getTotal().doubleValue(), mon101A.getJul().doubleValue(), mon101A.getAug().doubleValue(), mon101A.getSep().doubleValue(), mon101A.getOct().doubleValue(),
                mon101A.getNov().doubleValue(), mon101A.getDec().doubleValue(), mon101A.getJan().doubleValue(), mon101A.getFeb().doubleValue(), mon101A.getMar().doubleValue(),
                mon101A.getApr().doubleValue(), mon101A.getMay().doubleValue(), mon101A.getJun().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        listIncomeTotals.add(mon101A);
        listIncomeTotals4.add(mon101A);
        Row Q85 = createHeaderRow(sheet, tr, "", "Total passenger services expenses",
                mon101A.getTotal().doubleValue(), mon101A.getJul().doubleValue(), mon101A.getAug().doubleValue(), mon101A.getSep().doubleValue(), mon101A.getOct().doubleValue(),
                mon101A.getNov().doubleValue(), mon101A.getDec().doubleValue(), mon101A.getJan().doubleValue(), mon101A.getFeb().doubleValue(), mon101A.getMar().doubleValue(),
                mon101A.getApr().doubleValue(), mon101A.getMay().doubleValue(), mon101A.getJun().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q86 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("228", "Maintenance", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q87 = createHeaderRow(sheet, tr, titles);

        listCoas.clear();
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("228");
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

                tr++;
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                        mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                        mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T10 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T10);
        listIncomeTotals4.add(mon101T10);
        Row Q88 = createHeaderRow(sheet, tr, "", "Total Maintenance",
                mon101T10.getTotal().doubleValue(), mon101T10.getJul().doubleValue(), mon101T10.getAug().doubleValue(), mon101T10.getSep().doubleValue(), mon101T10.getOct().doubleValue(),
                mon101T10.getNov().doubleValue(), mon101T10.getDec().doubleValue(), mon101T10.getJan().doubleValue(), mon101T10.getFeb().doubleValue(), mon101T10.getMar().doubleValue(),
                mon101T10.getApr().doubleValue(), mon101T10.getMay().doubleValue(), mon101T10.getJun().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q89 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("223", "Utility And Property Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q891 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "4"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22300", ss);
        ss.clear();
        ss.addAll(Arrays.asList("2", "3", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith40 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("28150", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith40);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T11 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T11);
        listIncomeTotals4.add(mon101T11);

        Row Q90 = createHeaderRow(sheet, tr, "", "Total Property Expenses",
                mon101T11.getTotal().doubleValue(), mon101T11.getJul().doubleValue(), mon101T11.getAug().doubleValue(), mon101T11.getSep().doubleValue(), mon101T11.getOct().doubleValue(),
                mon101T11.getNov().doubleValue(), mon101T11.getDec().doubleValue(), mon101T11.getJan().doubleValue(), mon101T11.getFeb().doubleValue(), mon101T11.getMar().doubleValue(),
                mon101T11.getApr().doubleValue(), mon101T11.getMay().doubleValue(), mon101T11.getJun().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q91 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T11W = calculateTotal(listIncomeTotals4);

        Row Q92 = createHeaderRow(sheet, tr, "", "Total variable costs",
                mon101T11W.getTotal().doubleValue(), mon101T11W.getJul().doubleValue(), mon101T11W.getAug().doubleValue(), mon101T11W.getSep().doubleValue(), mon101T11W.getOct().doubleValue(),
                mon101T11W.getNov().doubleValue(), mon101T11W.getDec().doubleValue(), mon101T11W.getJan().doubleValue(), mon101T11W.getFeb().doubleValue(), mon101T11W.getMar().doubleValue(),
                mon101T11W.getApr().doubleValue(), mon101T11W.getMay().doubleValue(), mon101T11W.getJun().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q93 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Operation Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q931Row = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("211", "Personel Costs", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q931Row1 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21110", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "6"));
        List<URC_ACNT> findByAcntCodeStartingWith41 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21210", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith41);

        List<URC_ACNT> findByAcntCodeStartingWith42 = urcAcntService.findByAcntCode("212201");
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith42);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5"));
        List<URC_ACNT> findByAcntCodeStartingWith43 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21300", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith43);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T12 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T12);
        listIncomeTotals3.add(mon101T12);//Total Personnel Cost

        Row Q941 = createHeaderRow(sheet, tr, "", "Total Personnel Cost",
                mon101T12.getTotal().doubleValue(), mon101T12.getJul().doubleValue(), mon101T12.getAug().doubleValue(), mon101T12.getSep().doubleValue(), mon101T12.getOct().doubleValue(),
                mon101T12.getNov().doubleValue(), mon101T12.getDec().doubleValue(), mon101T12.getJan().doubleValue(), mon101T12.getFeb().doubleValue(), mon101T12.getMar().doubleValue(),
                mon101T12.getApr().doubleValue(), mon101T12.getMay().doubleValue(), mon101T12.getJun().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.exp.", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q942 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "ADMINISTRATION EXPENSES", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q943 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("221", "General Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q94 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22100", ss);
        ss.clear();
        ss.addAll(Arrays.asList("0", "1", "2", "6", "7"));
        List<URC_ACNT> findByAcntCodeStartingWith60 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22101", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith60);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T13 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T13);
        listIncomeTotals3.add(mon101T13);//Total Administration Expenses

        Row Q95 = createHeaderRow(sheet, tr, "", "Total Administration Expenses",
                mon101T13.getTotal().doubleValue(), mon101T13.getJul().doubleValue(), mon101T13.getAug().doubleValue(), mon101T13.getSep().doubleValue(), mon101T13.getOct().doubleValue(),
                mon101T13.getNov().doubleValue(), mon101T13.getDec().doubleValue(), mon101T13.getJan().doubleValue(), mon101T13.getFeb().doubleValue(), mon101T13.getMar().doubleValue(),
                mon101T13.getApr().doubleValue(), mon101T13.getMay().doubleValue(), mon101T13.getJun().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.income", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q96 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "OTHER ADMINISTRATION EXPENSES", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q97 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("221", "Board and Legal Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q98 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("221019");
        ss.clear();
        List<URC_ACNT> findByAcntCodeStartingWith601 = urcAcntService.findByAcntCode("221020");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith601);
        List<URC_ACNT> findByAcntCodeStartingWith602 = urcAcntService.findByAcntCode("221008");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith602);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T14 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T14);
        listIncomeTotals2.add(mon101T14);
        Row Q99 = createHeaderRow(sheet, tr, "", "Total Board & Legal Expenses",
                mon101T14.getTotal().doubleValue(), mon101T14.getJul().doubleValue(), mon101T14.getAug().doubleValue(), mon101T14.getSep().doubleValue(), mon101T14.getOct().doubleValue(),
                mon101T14.getNov().doubleValue(), mon101T14.getDec().doubleValue(), mon101T14.getJan().doubleValue(), mon101T14.getFeb().doubleValue(), mon101T14.getMar().doubleValue(),
                mon101T14.getApr().doubleValue(), mon101T14.getMay().doubleValue(), mon101T14.getJun().doubleValue());

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("222");
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T15 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T15);
        listIncomeTotals2.add(mon101T15);

        Row Q100 = createHeaderRow(sheet, tr, "", "Total Communications",
                mon101T15.getTotal().doubleValue(), mon101T15.getJul().doubleValue(), mon101T15.getAug().doubleValue(), mon101T15.getSep().doubleValue(), mon101T15.getOct().doubleValue(),
                mon101T15.getNov().doubleValue(), mon101T15.getDec().doubleValue(), mon101T15.getJan().doubleValue(), mon101T15.getFeb().doubleValue(), mon101T15.getMar().doubleValue(),
                mon101T15.getApr().doubleValue(), mon101T15.getMay().doubleValue(), mon101T15.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("223", "Utilities", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q101 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("5", "6", "7", "8"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22300", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T16 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T16);
        listIncomeTotals2.add(mon101T16);

        Row Q102 = createHeaderRow(sheet, tr, "", "Total Utility Expenses",
                mon101T16.getTotal().doubleValue(), mon101T16.getJul().doubleValue(), mon101T16.getAug().doubleValue(), mon101T16.getSep().doubleValue(), mon101T16.getOct().doubleValue(),
                mon101T16.getNov().doubleValue(), mon101T16.getDec().doubleValue(), mon101T16.getJan().doubleValue(), mon101T16.getFeb().doubleValue(), mon101T16.getMar().doubleValue(),
                mon101T16.getApr().doubleValue(), mon101T16.getMay().doubleValue(), mon101T16.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("224", "Supplies and Services", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q1021 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("3", "4", "5", "6"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22400", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T17 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T17);
        listIncomeTotals2.add(mon101T17);

        Row Q103 = createHeaderRow(sheet, tr, "", "Total Supplies and Services",
                mon101T17.getTotal().doubleValue(), mon101T17.getJul().doubleValue(), mon101T17.getAug().doubleValue(), mon101T17.getSep().doubleValue(), mon101T17.getOct().doubleValue(),
                mon101T17.getNov().doubleValue(), mon101T17.getDec().doubleValue(), mon101T17.getJan().doubleValue(), mon101T17.getFeb().doubleValue(), mon101T17.getMar().doubleValue(),
                mon101T17.getApr().doubleValue(), mon101T17.getMay().doubleValue(), mon101T17.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Professional Services", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q104 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22500", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T18 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T18);
        listIncomeTotals2.add(mon101T18);
        Row Q105 = createHeaderRow(sheet, tr, "", "Total Professional Services",
                mon101T18.getTotal().doubleValue(), mon101T18.getJul().doubleValue(), mon101T18.getAug().doubleValue(), mon101T18.getSep().doubleValue(), mon101T18.getOct().doubleValue(),
                mon101T18.getNov().doubleValue(), mon101T18.getDec().doubleValue(), mon101T18.getJan().doubleValue(), mon101T18.getFeb().doubleValue(), mon101T18.getMar().doubleValue(),
                mon101T18.getApr().doubleValue(), mon101T18.getMay().doubleValue(), mon101T18.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("226", "Insurances and Licenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q106 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22600", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T19 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T19);
        listIncomeTotals2.add(mon101T19);

        Row Q107 = createHeaderRow(sheet, tr, "", "Total Insurances and Licenses",
                mon101T19.getTotal().doubleValue(), mon101T19.getJul().doubleValue(), mon101T19.getAug().doubleValue(), mon101T19.getSep().doubleValue(), mon101T19.getOct().doubleValue(),
                mon101T19.getNov().doubleValue(), mon101T19.getDec().doubleValue(), mon101T19.getJan().doubleValue(), mon101T19.getFeb().doubleValue(), mon101T19.getMar().doubleValue(),
                mon101T19.getApr().doubleValue(), mon101T19.getMay().doubleValue(), mon101T19.getJun().doubleValue());

        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("227", "Travel and Transport", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q108 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22700", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T191 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T191);
        listIncomeTotals2.add(mon101T191);

        Row Q109 = createHeaderRow(sheet, tr, "", "Total Travel and Transport",
                mon101T191.getTotal().doubleValue(), mon101T191.getJul().doubleValue(), mon101T191.getAug().doubleValue(), mon101T191.getSep().doubleValue(), mon101T191.getOct().doubleValue(),
                mon101T191.getNov().doubleValue(), mon101T191.getDec().doubleValue(), mon101T191.getJan().doubleValue(), mon101T191.getFeb().doubleValue(), mon101T191.getMar().doubleValue(),
                mon101T191.getApr().doubleValue(), mon101T191.getMay().doubleValue(), mon101T191.getJun().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("282", "Miscellaneous Other Expenses", "", "", "", "", "", "", "", "", "", "", "", "", "");
        Row Q110 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("28210", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T1912 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals2.add(mon101T1912);
        listIncomeTotals.add(mon101T1912);
        Row Q111 = createHeaderRow(sheet, tr, "", "Total Miscellaneous Other Expenses",
                mon101T1912.getTotal().doubleValue(), mon101T1912.getJul().doubleValue(), mon101T1912.getAug().doubleValue(), mon101T1912.getSep().doubleValue(), mon101T1912.getOct().doubleValue(),
                mon101T1912.getNov().doubleValue(), mon101T1912.getDec().doubleValue(), mon101T1912.getJan().doubleValue(), mon101T1912.getFeb().doubleValue(), mon101T1912.getMar().doubleValue(),
                mon101T1912.getApr().doubleValue(), mon101T1912.getMay().doubleValue(), mon101T1912.getJun().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal10 = calculateTotal(listIncomeTotals2);
        listIncomeTotals3.add(calculateTotal10);//Total Other Administration Expenses
        Row Q112 = createHeaderRow(sheet, tr, "", "Total Other Administration Expenses",
                calculateTotal10.getTotal().doubleValue(), calculateTotal10.getJul().doubleValue(), calculateTotal10.getAug().doubleValue(), calculateTotal10.getSep().doubleValue(), calculateTotal10.getOct().doubleValue(),
                calculateTotal10.getNov().doubleValue(), calculateTotal10.getDec().doubleValue(), calculateTotal10.getJan().doubleValue(), calculateTotal10.getFeb().doubleValue(), calculateTotal10.getMar().doubleValue(),
                calculateTotal10.getApr().doubleValue(), calculateTotal10.getMay().doubleValue(), calculateTotal10.getJun().doubleValue());
        listIncomeTotals2.clear();
        tr++;
        Row Q113 = sheet.createRow((short) tr);
        Q113.createCell((short) 0).setCellValue("");
        Q113.createCell((short) 1).setCellValue("%age on Oper.exp.");
        Q113.createCell((short) 2).setCellValue("");
        Q113.createCell((short) 3).setCellValue("");
        Q113.createCell((short) 4).setCellValue("");
        Q113.createCell((short) 5).setCellValue("");
        Q113.createCell((short) 6).setCellValue("");
        Q113.createCell((short) 7).setCellValue("");
        Q113.createCell((short) 8).setCellValue("");
        Q113.createCell((short) 9).setCellValue("");
        Q113.createCell((short) 10).setCellValue("");
        Q113.createCell((short) 11).setCellValue("");
        Q113.createCell((short) 12).setCellValue("");
        Q113.createCell((short) 13).setCellValue("");
        Q113.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldTotal.add((int) tr);
        Row Q114 = sheet.createRow((short) tr);
        Q114.createCell((short) 0).setCellValue("");
        Q114.createCell((short) 1).setCellValue("Total Operating Expenses");
        Q114.createCell((short) 2).setCellValue("");
        Q114.createCell((short) 3).setCellValue("");
        Q114.createCell((short) 4).setCellValue("");
        Q114.createCell((short) 5).setCellValue("");
        Q114.createCell((short) 6).setCellValue("");
        Q114.createCell((short) 7).setCellValue("");
        Q114.createCell((short) 8).setCellValue("");
        Q114.createCell((short) 9).setCellValue("");
        Q114.createCell((short) 10).setCellValue("");
        Q114.createCell((short) 11).setCellValue("");
        Q114.createCell((short) 12).setCellValue("");
        Q114.createCell((short) 13).setCellValue("");
        Q114.createCell((short) 14).setCellValue("");
        tr++;
        Row Q115 = sheet.createRow((short) tr);
        Q115.createCell((short) 0).setCellValue("");
        Q115.createCell((short) 1).setCellValue("%age on Oper.income");
        Q115.createCell((short) 2).setCellValue("");
        Q115.createCell((short) 3).setCellValue("");
        Q115.createCell((short) 4).setCellValue("");
        Q115.createCell((short) 5).setCellValue("");
        Q115.createCell((short) 6).setCellValue("");
        Q115.createCell((short) 7).setCellValue("");
        Q115.createCell((short) 8).setCellValue("");
        Q115.createCell((short) 9).setCellValue("");
        Q115.createCell((short) 10).setCellValue("");
        Q115.createCell((short) 11).setCellValue("");
        Q115.createCell((short) 12).setCellValue("");
        Q115.createCell((short) 13).setCellValue("");
        Q115.createCell((short) 14).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q116 = sheet.createRow((short) tr);
        Q116.createCell((short) 0).setCellValue("");
        Q116.createCell((short) 1).setCellValue("Total Non-Wage");
        Q116.createCell((short) 2).setCellValue("");
        Q116.createCell((short) 3).setCellValue("");
        Q116.createCell((short) 4).setCellValue("");
        Q116.createCell((short) 5).setCellValue("");
        Q116.createCell((short) 6).setCellValue("");
        Q116.createCell((short) 7).setCellValue("");
        Q116.createCell((short) 8).setCellValue("");
        Q116.createCell((short) 9).setCellValue("");
        Q116.createCell((short) 10).setCellValue("");
        Q116.createCell((short) 11).setCellValue("");
        Q116.createCell((short) 12).setCellValue("");
        Q116.createCell((short) 13).setCellValue("");
        Q116.createCell((short) 14).setCellValue("");
        tr++;
        Row Q117 = sheet.createRow((short) tr);
        Q117.createCell((short) 0).setCellValue("");
        Q117.createCell((short) 1).setCellValue("%age on Oper.exp");
        Q117.createCell((short) 2).setCellValue("");
        Q117.createCell((short) 3).setCellValue("");
        Q117.createCell((short) 4).setCellValue("");
        Q117.createCell((short) 5).setCellValue("");
        Q117.createCell((short) 6).setCellValue("");
        Q117.createCell((short) 7).setCellValue("");
        Q117.createCell((short) 8).setCellValue("");
        Q117.createCell((short) 9).setCellValue("");
        Q117.createCell((short) 10).setCellValue("");
        Q117.createCell((short) 11).setCellValue("");
        Q117.createCell((short) 12).setCellValue("");
        Q117.createCell((short) 13).setCellValue("");
        Q117.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q118 = sheet.createRow((short) tr);
        Q118.createCell((short) 0).setCellValue("");
        Q118.createCell((short) 1).setCellValue("EBITDA/Operating Suplus/(Deficit) ");
        Q118.createCell((short) 2).setCellValue("");
        Q118.createCell((short) 3).setCellValue("");
        Q118.createCell((short) 4).setCellValue("");
        Q118.createCell((short) 5).setCellValue("");
        Q118.createCell((short) 6).setCellValue("");
        Q118.createCell((short) 7).setCellValue("");
        Q118.createCell((short) 8).setCellValue("");
        Q118.createCell((short) 9).setCellValue("");
        Q118.createCell((short) 10).setCellValue("");
        Q118.createCell((short) 11).setCellValue("");
        Q118.createCell((short) 12).setCellValue("");
        Q118.createCell((short) 13).setCellValue("");
        Q118.createCell((short) 14).setCellValue("");
        tr++;
        Row Q119 = sheet.createRow((short) tr);
        Q119.createCell((short) 0).setCellValue("");
        Q119.createCell((short) 1).setCellValue("%age on Oper.income");
        Q119.createCell((short) 2).setCellValue("");
        Q119.createCell((short) 3).setCellValue("");
        Q119.createCell((short) 4).setCellValue("");
        Q119.createCell((short) 5).setCellValue("");
        Q119.createCell((short) 6).setCellValue("");
        Q119.createCell((short) 7).setCellValue("");
        Q119.createCell((short) 8).setCellValue("");
        Q119.createCell((short) 9).setCellValue("");
        Q119.createCell((short) 10).setCellValue("");
        Q119.createCell((short) 11).setCellValue("");
        Q119.createCell((short) 12).setCellValue("");
        Q119.createCell((short) 13).setCellValue("");
        Q119.createCell((short) 14).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q120 = sheet.createRow((short) tr);
        Q120.createCell((short) 0).setCellValue("");
        Q120.createCell((short) 1).setCellValue("CONSUMPTION OF FIXED ASSETS");
        Q120.createCell((short) 2).setCellValue("");
        Q120.createCell((short) 3).setCellValue("");
        Q120.createCell((short) 4).setCellValue("");
        Q120.createCell((short) 5).setCellValue("");
        Q120.createCell((short) 6).setCellValue("");
        Q120.createCell((short) 7).setCellValue("");
        Q120.createCell((short) 8).setCellValue("");
        Q120.createCell((short) 9).setCellValue("");
        Q120.createCell((short) 10).setCellValue("");
        Q120.createCell((short) 11).setCellValue("");
        Q120.createCell((short) 12).setCellValue("");
        Q120.createCell((short) 13).setCellValue("");
        Q120.createCell((short) 14).setCellValue("");

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("231");
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            listIncomeTotals5.add(mon101);

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }
        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight calculateTotal101 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal101);//Total Other Administration Expenses
        Row Q121 = createHeaderRow(sheet, tr, "", "Total Depreciation",
                calculateTotal101.getTotal().doubleValue(), calculateTotal101.getJul().doubleValue(), calculateTotal101.getAug().doubleValue(), calculateTotal101.getSep().doubleValue(), calculateTotal101.getOct().doubleValue(),
                calculateTotal101.getNov().doubleValue(), calculateTotal101.getDec().doubleValue(), calculateTotal101.getJan().doubleValue(), calculateTotal101.getFeb().doubleValue(), calculateTotal101.getMar().doubleValue(),
                calculateTotal101.getApr().doubleValue(), calculateTotal101.getMay().doubleValue(), calculateTotal101.getJun().doubleValue());
        listIncomeTotals5.clear();

        tr++;
        titleJustBold.add((int) tr);
        Row Q122 = sheet.createRow((short) tr);
        Q122.createCell((short) 0).setCellValue("");
        Q122.createCell((short) 1).setCellValue("Armotisation");
        Q122.createCell((short) 2).setCellValue("");
        Q122.createCell((short) 3).setCellValue("");
        Q122.createCell((short) 4).setCellValue("");
        Q122.createCell((short) 5).setCellValue("");
        Q122.createCell((short) 6).setCellValue("");
        Q122.createCell((short) 7).setCellValue("");
        Q122.createCell((short) 8).setCellValue("");
        Q122.createCell((short) 9).setCellValue("");
        Q122.createCell((short) 10).setCellValue("");
        Q122.createCell((short) 11).setCellValue("");
        Q122.createCell((short) 12).setCellValue("");
        Q122.createCell((short) 13).setCellValue("");
        Q122.createCell((short) 14).setCellValue("");
        tr++;
        Row Q123 = sheet.createRow((short) tr);
        Q123.createCell((short) 0).setCellValue("");
        Q123.createCell((short) 1).setCellValue("Software");
        Q123.createCell((short) 2).setCellValue("");
        Q123.createCell((short) 3).setCellValue("");
        Q123.createCell((short) 4).setCellValue("");
        Q123.createCell((short) 5).setCellValue("");
        Q123.createCell((short) 6).setCellValue("");
        Q123.createCell((short) 7).setCellValue("");
        Q123.createCell((short) 8).setCellValue("");
        Q123.createCell((short) 9).setCellValue("");
        Q123.createCell((short) 10).setCellValue("");
        Q123.createCell((short) 11).setCellValue("");
        Q123.createCell((short) 12).setCellValue("");
        Q123.createCell((short) 13).setCellValue("");
        Q123.createCell((short) 14).setCellValue("");
        tr++;
        Row Q124 = sheet.createRow((short) tr);
        Q124.createCell((short) 0).setCellValue("");
        Q124.createCell((short) 1).setCellValue("Other");
        Q124.createCell((short) 2).setCellValue("");
        Q124.createCell((short) 3).setCellValue("");
        Q124.createCell((short) 4).setCellValue("");
        Q124.createCell((short) 5).setCellValue("");
        Q124.createCell((short) 6).setCellValue("");
        Q124.createCell((short) 7).setCellValue("");
        Q124.createCell((short) 8).setCellValue("");
        Q124.createCell((short) 9).setCellValue("");
        Q124.createCell((short) 10).setCellValue("");
        Q124.createCell((short) 11).setCellValue("");
        Q124.createCell((short) 12).setCellValue("");
        Q124.createCell((short) 13).setCellValue("");
        Q124.createCell((short) 14).setCellValue("");
        tr++;
        Row Q125 = sheet.createRow((short) tr);
        titleJustBold.add((int) tr);
        Q125.createCell((short) 0).setCellValue("");
        Q125.createCell((short) 1).setCellValue("Total Armotisation");
        Q125.createCell((short) 2).setCellValue("");
        Q125.createCell((short) 3).setCellValue("");
        Q125.createCell((short) 4).setCellValue("");
        Q125.createCell((short) 5).setCellValue("");
        Q125.createCell((short) 6).setCellValue("");
        Q125.createCell((short) 7).setCellValue("");
        Q125.createCell((short) 8).setCellValue("");
        Q125.createCell((short) 9).setCellValue("");
        Q125.createCell((short) 10).setCellValue("");
        Q125.createCell((short) 11).setCellValue("");
        Q125.createCell((short) 12).setCellValue("");
        Q125.createCell((short) 13).setCellValue("");
        Q125.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldTotal.add((int) tr);
        Row Q126 = sheet.createRow((short) tr);
        Q126.createCell((short) 0).setCellValue("");
        Q126.createCell((short) 1).setCellValue("Total Depreciation & Armotisation");
        Q126.createCell((short) 2).setCellValue("");
        Q126.createCell((short) 3).setCellValue("");
        Q126.createCell((short) 4).setCellValue("");
        Q126.createCell((short) 5).setCellValue("");
        Q126.createCell((short) 6).setCellValue("");
        Q126.createCell((short) 7).setCellValue("");
        Q126.createCell((short) 8).setCellValue("");
        Q126.createCell((short) 9).setCellValue("");
        Q126.createCell((short) 10).setCellValue("");
        Q126.createCell((short) 11).setCellValue("");
        Q126.createCell((short) 12).setCellValue("");
        Q126.createCell((short) 13).setCellValue("");
        Q126.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q127 = sheet.createRow((short) tr);
        Q127.createCell((short) 0).setCellValue("221");
        Q127.createCell((short) 1).setCellValue("Finance Costs");
        Q127.createCell((short) 2).setCellValue("");
        Q127.createCell((short) 3).setCellValue("");
        Q127.createCell((short) 4).setCellValue("");
        Q127.createCell((short) 5).setCellValue("");
        Q127.createCell((short) 6).setCellValue("");
        Q127.createCell((short) 7).setCellValue("");
        Q127.createCell((short) 8).setCellValue("");
        Q127.createCell((short) 9).setCellValue("");
        Q127.createCell((short) 10).setCellValue("");
        Q127.createCell((short) 11).setCellValue("");
        Q127.createCell((short) 12).setCellValue("");
        Q127.createCell((short) 13).setCellValue("");
        Q127.createCell((short) 14).setCellValue("");

        ss.addAll(Arrays.asList("4", "8"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22101", ss);
        ss.clear();
        listCoas.clear();
        listIncomeTotals5.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            listIncomeTotals5.add(mon101);

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                    mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                    mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal1011 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1011);//Total Other Administration Expenses
        Row Q128 = createHeaderRow(sheet, tr, "", "Total Finance Cost",
                calculateTotal1011.getTotal().doubleValue(), calculateTotal1011.getJul().doubleValue(), calculateTotal1011.getAug().doubleValue(), calculateTotal1011.getSep().doubleValue(), calculateTotal1011.getOct().doubleValue(),
                calculateTotal1011.getNov().doubleValue(), calculateTotal1011.getDec().doubleValue(), calculateTotal1011.getJan().doubleValue(), calculateTotal1011.getFeb().doubleValue(), calculateTotal1011.getMar().doubleValue(),
                calculateTotal1011.getApr().doubleValue(), calculateTotal1011.getMay().doubleValue(), calculateTotal1011.getJun().doubleValue());
        listIncomeTotals5.clear();

        tr++;
        titleBoldTotal.add((int) tr);
        Row Q129 = sheet.createRow((short) tr);
        Q129.createCell((short) 0).setCellValue("");
        Q129.createCell((short) 1).setCellValue("Total Expenditure");
        Q129.createCell((short) 2).setCellValue("");
        Q129.createCell((short) 3).setCellValue("");
        Q129.createCell((short) 4).setCellValue("");
        Q129.createCell((short) 5).setCellValue("");
        Q129.createCell((short) 6).setCellValue("");
        Q129.createCell((short) 7).setCellValue("");
        Q129.createCell((short) 8).setCellValue("");
        Q129.createCell((short) 9).setCellValue("");
        Q129.createCell((short) 10).setCellValue("");
        Q129.createCell((short) 11).setCellValue("");
        Q129.createCell((short) 12).setCellValue("");
        Q129.createCell((short) 13).setCellValue("");
        Q129.createCell((short) 14).setCellValue("");
        tr++;
        Row Q130 = sheet.createRow((short) tr);
        Q130.createCell((short) 0).setCellValue("");
        Q130.createCell((short) 1).setCellValue("");
        Q130.createCell((short) 2).setCellValue("");
        Q130.createCell((short) 3).setCellValue("");
        Q130.createCell((short) 4).setCellValue("");
        Q130.createCell((short) 5).setCellValue("");
        Q130.createCell((short) 6).setCellValue("");
        Q130.createCell((short) 7).setCellValue("");
        Q130.createCell((short) 8).setCellValue("");
        Q130.createCell((short) 9).setCellValue("");
        Q130.createCell((short) 10).setCellValue("");
        Q130.createCell((short) 11).setCellValue("");
        Q130.createCell((short) 12).setCellValue("");
        Q130.createCell((short) 13).setCellValue("");
        Q130.createCell((short) 14).setCellValue("");
        tr++;
        Row Q131 = sheet.createRow((short) tr);
        Q131.createCell((short) 0).setCellValue("");
        Q131.createCell((short) 1).setCellValue("EBT");
        Q131.createCell((short) 2).setCellValue("");
        Q131.createCell((short) 3).setCellValue("");
        Q131.createCell((short) 4).setCellValue("");
        Q131.createCell((short) 5).setCellValue("");
        Q131.createCell((short) 6).setCellValue("");
        Q131.createCell((short) 7).setCellValue("");
        Q131.createCell((short) 8).setCellValue("");
        Q131.createCell((short) 9).setCellValue("");
        Q131.createCell((short) 10).setCellValue("");
        Q131.createCell((short) 11).setCellValue("");
        Q131.createCell((short) 12).setCellValue("");
        Q131.createCell((short) 13).setCellValue("");
        Q131.createCell((short) 14).setCellValue("");
        tr++;
        Row Q132 = sheet.createRow((short) tr);
        Q132.createCell((short) 0).setCellValue("");
        Q132.createCell((short) 1).setCellValue("%age on Oper.income");
        Q132.createCell((short) 2).setCellValue("");
        Q132.createCell((short) 3).setCellValue("");
        Q132.createCell((short) 4).setCellValue("");
        Q132.createCell((short) 5).setCellValue("");
        Q132.createCell((short) 6).setCellValue("");
        Q132.createCell((short) 7).setCellValue("");
        Q132.createCell((short) 8).setCellValue("");
        Q132.createCell((short) 9).setCellValue("");
        Q132.createCell((short) 10).setCellValue("");
        Q132.createCell((short) 11).setCellValue("");
        Q132.createCell((short) 12).setCellValue("");
        Q132.createCell((short) 13).setCellValue("");
        Q132.createCell((short) 14).setCellValue("");
        tr++;
        Row Q133 = sheet.createRow((short) tr);
        Q133.createCell((short) 0).setCellValue("");
        Q133.createCell((short) 1).setCellValue("Tax (Provision)");
        Q133.createCell((short) 2).setCellValue("");
        Q133.createCell((short) 3).setCellValue("");
        Q133.createCell((short) 4).setCellValue("");
        Q133.createCell((short) 5).setCellValue("");
        Q133.createCell((short) 6).setCellValue("");
        Q133.createCell((short) 7).setCellValue("");
        Q133.createCell((short) 8).setCellValue("");
        Q133.createCell((short) 9).setCellValue("");
        Q133.createCell((short) 10).setCellValue("");
        Q133.createCell((short) 11).setCellValue("");
        Q133.createCell((short) 12).setCellValue("");
        Q133.createCell((short) 13).setCellValue("");
        Q133.createCell((short) 14).setCellValue("");
        tr++;
        Row Q134 = sheet.createRow((short) tr);
        Q134.createCell((short) 0).setCellValue("");
        Q134.createCell((short) 1).setCellValue("EAT");
        Q134.createCell((short) 2).setCellValue("");
        Q134.createCell((short) 3).setCellValue("");
        Q134.createCell((short) 4).setCellValue("");
        Q134.createCell((short) 5).setCellValue("");
        Q134.createCell((short) 6).setCellValue("");
        Q134.createCell((short) 7).setCellValue("");
        Q134.createCell((short) 8).setCellValue("");
        Q134.createCell((short) 9).setCellValue("");
        Q134.createCell((short) 10).setCellValue("");
        Q134.createCell((short) 11).setCellValue("");
        Q134.createCell((short) 12).setCellValue("");
        Q134.createCell((short) 13).setCellValue("");
        Q134.createCell((short) 14).setCellValue("");
        tr++;
        Row Q135 = sheet.createRow((short) tr);
        Q135.createCell((short) 0).setCellValue("");
        Q135.createCell((short) 1).setCellValue("Add Non-Operating/non-recurrent Income");
        Q135.createCell((short) 2).setCellValue("");
        Q135.createCell((short) 3).setCellValue("");
        Q135.createCell((short) 4).setCellValue("");
        Q135.createCell((short) 5).setCellValue("");
        Q135.createCell((short) 6).setCellValue("");
        Q135.createCell((short) 7).setCellValue("");
        Q135.createCell((short) 8).setCellValue("");
        Q135.createCell((short) 9).setCellValue("");
        Q135.createCell((short) 10).setCellValue("");
        Q135.createCell((short) 11).setCellValue("");
        Q135.createCell((short) 12).setCellValue("");
        Q135.createCell((short) 13).setCellValue("");
        Q135.createCell((short) 14).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q136 = sheet.createRow((short) tr);
        Q136.createCell((short) 0).setCellValue("");
        Q136.createCell((short) 1).setCellValue("Total Surplus/(Deficit)");
        Q136.createCell((short) 2).setCellValue("");
        Q136.createCell((short) 3).setCellValue("");
        Q136.createCell((short) 4).setCellValue("");
        Q136.createCell((short) 5).setCellValue("");
        Q136.createCell((short) 6).setCellValue("");
        Q136.createCell((short) 7).setCellValue("");
        Q136.createCell((short) 8).setCellValue("");
        Q136.createCell((short) 9).setCellValue("");
        Q136.createCell((short) 10).setCellValue("");
        Q136.createCell((short) 11).setCellValue("");
        Q136.createCell((short) 12).setCellValue("");
        Q136.createCell((short) 13).setCellValue("");
        Q136.createCell((short) 14).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q137 = sheet.createRow((short) tr);
        Q137.createCell((short) 0).setCellValue("");
        Q137.createCell((short) 1).setCellValue("Grand Total Operating Expenses");
        Q137.createCell((short) 2).setCellValue("");
        Q137.createCell((short) 3).setCellValue("");
        Q137.createCell((short) 4).setCellValue("");
        Q137.createCell((short) 5).setCellValue("");
        Q137.createCell((short) 6).setCellValue("");
        Q137.createCell((short) 7).setCellValue("");
        Q137.createCell((short) 8).setCellValue("");
        Q137.createCell((short) 9).setCellValue("");
        Q137.createCell((short) 10).setCellValue("");
        Q137.createCell((short) 11).setCellValue("");
        Q137.createCell((short) 12).setCellValue("");
        Q137.createCell((short) 13).setCellValue("");
        Q137.createCell((short) 14).setCellValue("");
        tr++;
        Row Q138 = sheet.createRow((short) tr);
        Q138.createCell((short) 0).setCellValue("");
        Q138.createCell((short) 1).setCellValue("Operation Ratio");
        Q138.createCell((short) 2).setCellValue("");
        Q138.createCell((short) 3).setCellValue("");
        Q138.createCell((short) 4).setCellValue("");
        Q138.createCell((short) 5).setCellValue("");
        Q138.createCell((short) 6).setCellValue("");
        Q138.createCell((short) 7).setCellValue("");
        Q138.createCell((short) 8).setCellValue("");
        Q138.createCell((short) 9).setCellValue("");
        Q138.createCell((short) 10).setCellValue("");
        Q138.createCell((short) 11).setCellValue("");
        Q138.createCell((short) 12).setCellValue("");
        Q138.createCell((short) 13).setCellValue("");
        Q138.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q139 = sheet.createRow((short) tr);
        Q139.createCell((short) 0).setCellValue("");
        Q139.createCell((short) 1).setCellValue("FIXED ASSETS");
        Q139.createCell((short) 2).setCellValue("");
        Q139.createCell((short) 3).setCellValue("");
        Q139.createCell((short) 4).setCellValue("");
        Q139.createCell((short) 5).setCellValue("");
        Q139.createCell((short) 6).setCellValue("");
        Q139.createCell((short) 7).setCellValue("");
        Q139.createCell((short) 8).setCellValue("");
        Q139.createCell((short) 9).setCellValue("");
        Q139.createCell((short) 10).setCellValue("");
        Q139.createCell((short) 11).setCellValue("");
        Q139.createCell((short) 12).setCellValue("");
        Q139.createCell((short) 13).setCellValue("");
        Q139.createCell((short) 14).setCellValue("");

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("312");
        listCoas.clear();
        listIncomeTotals5.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 4) {

                tr++;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                listIncomeTotals5.add(mon101);

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        mon101.getTotal().doubleValue(), mon101.getJul().doubleValue(), mon101.getAug().doubleValue(), mon101.getSep().doubleValue(), mon101.getOct().doubleValue(),
                        mon101.getNov().doubleValue(), mon101.getDec().doubleValue(), mon101.getJan().doubleValue(), mon101.getFeb().doubleValue(), mon101.getMar().doubleValue(),
                        mon101.getApr().doubleValue(), mon101.getMay().doubleValue(), mon101.getJun().doubleValue());
            }

        }
        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight calculateTotal1012 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1012);//Total Other Administration Expenses
        Row Q140 = createHeaderRow(sheet, tr, "", "Total Fixed Assets",
                calculateTotal1012.getTotal().doubleValue(), calculateTotal1012.getJul().doubleValue(), calculateTotal1012.getAug().doubleValue(), calculateTotal1012.getSep().doubleValue(), calculateTotal1012.getOct().doubleValue(),
                calculateTotal1012.getNov().doubleValue(), calculateTotal1012.getDec().doubleValue(), calculateTotal1012.getJan().doubleValue(), calculateTotal1012.getFeb().doubleValue(), calculateTotal1012.getMar().doubleValue(),
                calculateTotal1012.getApr().doubleValue(), calculateTotal1012.getMay().doubleValue(), calculateTotal1012.getJun().doubleValue());
        listIncomeTotals5.clear();

        tr++;
        titleJustBold.add((int) tr);
        Row Q141 = sheet.createRow((short) tr);
        Q141.createCell((short) 0).setCellValue("");
        Q141.createCell((short) 1).setCellValue("");
        Q141.createCell((short) 2).setCellValue("");
        Q141.createCell((short) 3).setCellValue("");
        Q141.createCell((short) 4).setCellValue("");
        Q141.createCell((short) 5).setCellValue("");
        Q141.createCell((short) 6).setCellValue("");
        Q141.createCell((short) 7).setCellValue("");
        Q141.createCell((short) 8).setCellValue("");
        Q141.createCell((short) 9).setCellValue("");
        Q141.createCell((short) 10).setCellValue("");
        Q141.createCell((short) 11).setCellValue("");
        Q141.createCell((short) 12).setCellValue("");
        Q141.createCell((short) 13).setCellValue("");
        Q141.createCell((short) 14).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q142 = sheet.createRow((short) tr);
        Q142.createCell((short) 0).setCellValue("");
        Q142.createCell((short) 1).setCellValue("SUMMARY");
        Q142.createCell((short) 2).setCellValue("");
        Q142.createCell((short) 3).setCellValue("");
        Q142.createCell((short) 4).setCellValue("");
        Q142.createCell((short) 5).setCellValue("");
        Q142.createCell((short) 6).setCellValue("");
        Q142.createCell((short) 7).setCellValue("");
        Q142.createCell((short) 8).setCellValue("");
        Q142.createCell((short) 9).setCellValue("");
        Q142.createCell((short) 10).setCellValue("");
        Q142.createCell((short) 11).setCellValue("");
        Q142.createCell((short) 12).setCellValue("");
        Q142.createCell((short) 13).setCellValue("");
        Q142.createCell((short) 14).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q143 = sheet.createRow((short) tr);
        Q143.createCell((short) 0).setCellValue("");
        Q143.createCell((short) 1).setCellValue("INCOME");
        Q143.createCell((short) 2).setCellValue("");
        Q143.createCell((short) 3).setCellValue("");
        Q143.createCell((short) 4).setCellValue("");
        Q143.createCell((short) 5).setCellValue("");
        Q143.createCell((short) 6).setCellValue("");
        Q143.createCell((short) 7).setCellValue("");
        Q143.createCell((short) 8).setCellValue("");
        Q143.createCell((short) 9).setCellValue("");
        Q143.createCell((short) 10).setCellValue("");
        Q143.createCell((short) 11).setCellValue("");
        Q143.createCell((short) 12).setCellValue("");
        Q143.createCell((short) 13).setCellValue("");
        Q143.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q144 = sheet.createRow((short) tr);
        Q144.createCell((short) 0).setCellValue("");
        Q144.createCell((short) 1).setCellValue("Operating");
        Q144.createCell((short) 2).setCellValue("");
        Q144.createCell((short) 3).setCellValue("");
        Q144.createCell((short) 4).setCellValue("");
        Q144.createCell((short) 5).setCellValue("");
        Q144.createCell((short) 6).setCellValue("");
        Q144.createCell((short) 7).setCellValue("");
        Q144.createCell((short) 8).setCellValue("");
        Q144.createCell((short) 9).setCellValue("");
        Q144.createCell((short) 10).setCellValue("");
        Q144.createCell((short) 11).setCellValue("");
        Q144.createCell((short) 12).setCellValue("");
        Q144.createCell((short) 13).setCellValue("");
        Q144.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q145 = sheet.createRow((short) tr);
        Q145.createCell((short) 0).setCellValue("");
        Q145.createCell((short) 1).setCellValue("Assets hire");
        Q145.createCell((short) 2).setCellValue("");
        Q145.createCell((short) 3).setCellValue("");
        Q145.createCell((short) 4).setCellValue("");
        Q145.createCell((short) 5).setCellValue("");
        Q145.createCell((short) 6).setCellValue("");
        Q145.createCell((short) 7).setCellValue("");
        Q145.createCell((short) 8).setCellValue("");
        Q145.createCell((short) 9).setCellValue("");
        Q145.createCell((short) 10).setCellValue("");
        Q145.createCell((short) 11).setCellValue("");
        Q145.createCell((short) 12).setCellValue("");
        Q145.createCell((short) 13).setCellValue("");
        Q145.createCell((short) 14).setCellValue("");
        tr++;
        Row Q146 = sheet.createRow((short) tr);
        Q146.createCell((short) 0).setCellValue("");
        Q146.createCell((short) 1).setCellValue("Freight");
        Q146.createCell((short) 2).setCellValue("");
        Q146.createCell((short) 3).setCellValue("");
        Q146.createCell((short) 4).setCellValue("");
        Q146.createCell((short) 5).setCellValue("");
        Q146.createCell((short) 6).setCellValue("");
        Q146.createCell((short) 7).setCellValue("");
        Q146.createCell((short) 8).setCellValue("");
        Q146.createCell((short) 9).setCellValue("");
        Q146.createCell((short) 10).setCellValue("");
        Q146.createCell((short) 11).setCellValue("");
        Q146.createCell((short) 12).setCellValue("");
        Q146.createCell((short) 13).setCellValue("");
        Q146.createCell((short) 14).setCellValue("");
        tr++;
        Row Q147 = sheet.createRow((short) tr);
        Q147.createCell((short) 0).setCellValue("");
        Q147.createCell((short) 1).setCellValue("Rent");
        Q147.createCell((short) 2).setCellValue("");
        Q147.createCell((short) 3).setCellValue("");
        Q147.createCell((short) 4).setCellValue("");
        Q147.createCell((short) 5).setCellValue("");
        Q147.createCell((short) 6).setCellValue("");
        Q147.createCell((short) 7).setCellValue("");
        Q147.createCell((short) 8).setCellValue("");
        Q147.createCell((short) 9).setCellValue("");
        Q147.createCell((short) 10).setCellValue("");
        Q147.createCell((short) 11).setCellValue("");
        Q147.createCell((short) 12).setCellValue("");
        Q147.createCell((short) 13).setCellValue("");
        Q147.createCell((short) 14).setCellValue("");
        tr++;
        Row Q148 = sheet.createRow((short) tr);
        Q148.createCell((short) 0).setCellValue("");
        Q148.createCell((short) 1).setCellValue("Passenger services");
        Q148.createCell((short) 2).setCellValue("");
        Q148.createCell((short) 3).setCellValue("");
        Q148.createCell((short) 4).setCellValue("");
        Q148.createCell((short) 5).setCellValue("");
        Q148.createCell((short) 6).setCellValue("");
        Q148.createCell((short) 7).setCellValue("");
        Q148.createCell((short) 8).setCellValue("");
        Q148.createCell((short) 9).setCellValue("");
        Q148.createCell((short) 10).setCellValue("");
        Q148.createCell((short) 11).setCellValue("");
        Q148.createCell((short) 12).setCellValue("");
        Q148.createCell((short) 13).setCellValue("");
        Q148.createCell((short) 14).setCellValue("");
        tr++;
        Row Q149 = sheet.createRow((short) tr);
        Q149.createCell((short) 0).setCellValue("");
        Q149.createCell((short) 1).setCellValue("Other fees & charges");
        Q149.createCell((short) 2).setCellValue("");
        Q149.createCell((short) 3).setCellValue("");
        Q149.createCell((short) 4).setCellValue("");
        Q149.createCell((short) 5).setCellValue("");
        Q149.createCell((short) 6).setCellValue("");
        Q149.createCell((short) 7).setCellValue("");
        Q149.createCell((short) 8).setCellValue("");
        Q149.createCell((short) 9).setCellValue("");
        Q149.createCell((short) 10).setCellValue("");
        Q149.createCell((short) 11).setCellValue("");
        Q149.createCell((short) 12).setCellValue("");
        Q149.createCell((short) 13).setCellValue("");
        Q149.createCell((short) 14).setCellValue("");
        tr++;
        Row Q150 = sheet.createRow((short) tr);
        Q150.createCell((short) 0).setCellValue("");
        Q150.createCell((short) 1).setCellValue("Miscellaneous income");
        Q150.createCell((short) 2).setCellValue("");
        Q150.createCell((short) 3).setCellValue("");
        Q150.createCell((short) 4).setCellValue("");
        Q150.createCell((short) 5).setCellValue("");
        Q150.createCell((short) 6).setCellValue("");
        Q150.createCell((short) 7).setCellValue("");
        Q150.createCell((short) 8).setCellValue("");
        Q150.createCell((short) 9).setCellValue("");
        Q150.createCell((short) 10).setCellValue("");
        Q150.createCell((short) 11).setCellValue("");
        Q150.createCell((short) 12).setCellValue("");
        Q150.createCell((short) 13).setCellValue("");
        Q150.createCell((short) 14).setCellValue("");
        tr++;
        Row Q151 = sheet.createRow((short) tr);
        Q151.createCell((short) 0).setCellValue("");
        Q151.createCell((short) 1).setCellValue("Income from disposal of obsolete/idle assets");
        Q151.createCell((short) 2).setCellValue("");
        Q151.createCell((short) 3).setCellValue("");
        Q151.createCell((short) 4).setCellValue("");
        Q151.createCell((short) 5).setCellValue("");
        Q151.createCell((short) 6).setCellValue("");
        Q151.createCell((short) 7).setCellValue("");
        Q151.createCell((short) 8).setCellValue("");
        Q151.createCell((short) 9).setCellValue("");
        Q151.createCell((short) 10).setCellValue("");
        Q151.createCell((short) 11).setCellValue("");
        Q151.createCell((short) 12).setCellValue("");
        Q151.createCell((short) 13).setCellValue("");
        Q151.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q152 = sheet.createRow((short) tr);
        Q152.createCell((short) 0).setCellValue("");
        Q152.createCell((short) 1).setCellValue("Total operating Income");
        Q152.createCell((short) 2).setCellValue("");
        Q152.createCell((short) 3).setCellValue("");
        Q152.createCell((short) 4).setCellValue("");
        Q152.createCell((short) 5).setCellValue("");
        Q152.createCell((short) 6).setCellValue("");
        Q152.createCell((short) 7).setCellValue("");
        Q152.createCell((short) 8).setCellValue("");
        Q152.createCell((short) 9).setCellValue("");
        Q152.createCell((short) 10).setCellValue("");
        Q152.createCell((short) 11).setCellValue("");
        Q152.createCell((short) 12).setCellValue("");
        Q152.createCell((short) 13).setCellValue("");
        Q152.createCell((short) 14).setCellValue("");
        tr++;
        Row Q153 = sheet.createRow((short) tr);
        Q153.createCell((short) 0).setCellValue("");
        Q153.createCell((short) 1).setCellValue("Exceptional Income");
        Q153.createCell((short) 2).setCellValue("");
        Q153.createCell((short) 3).setCellValue("");
        Q153.createCell((short) 4).setCellValue("");
        Q153.createCell((short) 5).setCellValue("");
        Q153.createCell((short) 6).setCellValue("");
        Q153.createCell((short) 7).setCellValue("");
        Q153.createCell((short) 8).setCellValue("");
        Q153.createCell((short) 9).setCellValue("");
        Q153.createCell((short) 10).setCellValue("");
        Q153.createCell((short) 11).setCellValue("");
        Q153.createCell((short) 12).setCellValue("");
        Q153.createCell((short) 13).setCellValue("");
        Q153.createCell((short) 14).setCellValue("");
        tr++;
        Row Q154 = sheet.createRow((short) tr);
        Q154.createCell((short) 0).setCellValue("");
        Q154.createCell((short) 1).setCellValue("UNRA");
        Q154.createCell((short) 2).setCellValue("");
        Q154.createCell((short) 3).setCellValue("");
        Q154.createCell((short) 4).setCellValue("");
        Q154.createCell((short) 5).setCellValue("");
        Q154.createCell((short) 6).setCellValue("");
        Q154.createCell((short) 7).setCellValue("");
        Q154.createCell((short) 8).setCellValue("");
        Q154.createCell((short) 9).setCellValue("");
        Q154.createCell((short) 10).setCellValue("");
        Q154.createCell((short) 11).setCellValue("");
        Q154.createCell((short) 12).setCellValue("");
        Q154.createCell((short) 13).setCellValue("");
        Q154.createCell((short) 14).setCellValue("");
        tr++;
        Row Q155 = sheet.createRow((short) tr);
        Q155.createCell((short) 0).setCellValue("");
        Q155.createCell((short) 1).setCellValue("MoFPED");
        Q155.createCell((short) 2).setCellValue("");
        Q155.createCell((short) 3).setCellValue("");
        Q155.createCell((short) 4).setCellValue("");
        Q155.createCell((short) 5).setCellValue("");
        Q155.createCell((short) 6).setCellValue("");
        Q155.createCell((short) 7).setCellValue("");
        Q155.createCell((short) 8).setCellValue("");
        Q155.createCell((short) 9).setCellValue("");
        Q155.createCell((short) 10).setCellValue("");
        Q155.createCell((short) 11).setCellValue("");
        Q155.createCell((short) 12).setCellValue("");
        Q155.createCell((short) 13).setCellValue("");
        Q155.createCell((short) 14).setCellValue("");
        tr++;
        Row Q156 = sheet.createRow((short) tr);
        Q156.createCell((short) 0).setCellValue("");
        Q156.createCell((short) 1).setCellValue("Total exceptional Income");
        Q156.createCell((short) 2).setCellValue("");
        Q156.createCell((short) 3).setCellValue("");
        Q156.createCell((short) 4).setCellValue("");
        Q156.createCell((short) 5).setCellValue("");
        Q156.createCell((short) 6).setCellValue("");
        Q156.createCell((short) 7).setCellValue("");
        Q156.createCell((short) 8).setCellValue("");
        Q156.createCell((short) 9).setCellValue("");
        Q156.createCell((short) 10).setCellValue("");
        Q156.createCell((short) 11).setCellValue("");
        Q156.createCell((short) 12).setCellValue("");
        Q156.createCell((short) 13).setCellValue("");
        Q156.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q157 = sheet.createRow((short) tr);
        Q157.createCell((short) 0).setCellValue("");
        Q157.createCell((short) 1).setCellValue("Non-operating");
        Q157.createCell((short) 2).setCellValue("");
        Q157.createCell((short) 3).setCellValue("");
        Q157.createCell((short) 4).setCellValue("");
        Q157.createCell((short) 5).setCellValue("");
        Q157.createCell((short) 6).setCellValue("");
        Q157.createCell((short) 7).setCellValue("");
        Q157.createCell((short) 8).setCellValue("");
        Q157.createCell((short) 9).setCellValue("");
        Q157.createCell((short) 10).setCellValue("");
        Q157.createCell((short) 11).setCellValue("");
        Q157.createCell((short) 12).setCellValue("");
        Q157.createCell((short) 13).setCellValue("");
        Q157.createCell((short) 14).setCellValue("");
        tr++;

        Row Q158 = sheet.createRow((short) tr);
        Q158.createCell((short) 0).setCellValue("");
        Q158.createCell((short) 1).setCellValue("Institutional Support-Freight Operations");
        Q158.createCell((short) 2).setCellValue("");
        Q158.createCell((short) 3).setCellValue("");
        Q158.createCell((short) 4).setCellValue("");
        Q158.createCell((short) 5).setCellValue("");
        Q158.createCell((short) 6).setCellValue("");
        Q158.createCell((short) 7).setCellValue("");
        Q158.createCell((short) 8).setCellValue("");
        Q158.createCell((short) 9).setCellValue("");
        Q158.createCell((short) 10).setCellValue("");
        Q158.createCell((short) 11).setCellValue("");
        Q158.createCell((short) 12).setCellValue("");
        Q158.createCell((short) 13).setCellValue("");
        Q158.createCell((short) 14).setCellValue("");
        tr++;
        Row Q159 = sheet.createRow((short) tr);
        Q159.createCell((short) 0).setCellValue("");
        Q159.createCell((short) 1).setCellValue("Transfer  by Agencies from Treasury-Counterpart");
        Q159.createCell((short) 2).setCellValue("");
        Q159.createCell((short) 3).setCellValue("");
        Q159.createCell((short) 4).setCellValue("");
        Q159.createCell((short) 5).setCellValue("");
        Q159.createCell((short) 6).setCellValue("");
        Q159.createCell((short) 7).setCellValue("");
        Q159.createCell((short) 8).setCellValue("");
        Q159.createCell((short) 9).setCellValue("");
        Q159.createCell((short) 10).setCellValue("");
        Q159.createCell((short) 11).setCellValue("");
        Q159.createCell((short) 12).setCellValue("");
        Q159.createCell((short) 13).setCellValue("");
        Q159.createCell((short) 14).setCellValue("");
        tr++;
        Row Q160 = sheet.createRow((short) tr);
        Q160.createCell((short) 0).setCellValue("");
        Q160.createCell((short) 1).setCellValue("Institutional Support-Development-Trr-Gulu");
        Q160.createCell((short) 2).setCellValue("");
        Q160.createCell((short) 3).setCellValue("");
        Q160.createCell((short) 4).setCellValue("");
        Q160.createCell((short) 5).setCellValue("");
        Q160.createCell((short) 6).setCellValue("");
        Q160.createCell((short) 7).setCellValue("");
        Q160.createCell((short) 8).setCellValue("");
        Q160.createCell((short) 9).setCellValue("");
        Q160.createCell((short) 10).setCellValue("");
        Q160.createCell((short) 11).setCellValue("");
        Q160.createCell((short) 12).setCellValue("");
        Q160.createCell((short) 13).setCellValue("");
        Q160.createCell((short) 14).setCellValue("");
        tr++;
        Row Q161 = sheet.createRow((short) tr);
        Q161.createCell((short) 0).setCellValue("");
        Q161.createCell((short) 1).setCellValue("Inst.Support-Dev.:Trr-Gulu-Suppl.");
        Q161.createCell((short) 2).setCellValue("");
        Q161.createCell((short) 3).setCellValue("");
        Q161.createCell((short) 4).setCellValue("");
        Q161.createCell((short) 5).setCellValue("");
        Q161.createCell((short) 6).setCellValue("");
        Q161.createCell((short) 7).setCellValue("");
        Q161.createCell((short) 8).setCellValue("");
        Q161.createCell((short) 9).setCellValue("");
        Q161.createCell((short) 10).setCellValue("");
        Q161.createCell((short) 11).setCellValue("");
        Q161.createCell((short) 12).setCellValue("");
        Q161.createCell((short) 13).setCellValue("");
        Q161.createCell((short) 14).setCellValue("");
        tr++;
        Row Q162 = sheet.createRow((short) tr);
        Q162.createCell((short) 0).setCellValue("");
        Q162.createCell((short) 1).setCellValue("Donor Funds - Foreign Governments-Spain");
        Q162.createCell((short) 2).setCellValue("");
        Q162.createCell((short) 3).setCellValue("");
        Q162.createCell((short) 4).setCellValue("");
        Q162.createCell((short) 5).setCellValue("");
        Q162.createCell((short) 6).setCellValue("");
        Q162.createCell((short) 7).setCellValue("");
        Q162.createCell((short) 8).setCellValue("");
        Q162.createCell((short) 9).setCellValue("");
        Q162.createCell((short) 10).setCellValue("");
        Q162.createCell((short) 11).setCellValue("");
        Q162.createCell((short) 12).setCellValue("");
        Q162.createCell((short) 13).setCellValue("");
        Q162.createCell((short) 14).setCellValue("");
        tr++;
        Row Q163 = sheet.createRow((short) tr);
        Q163.createCell((short) 0).setCellValue("");
        Q163.createCell((short) 1).setCellValue("Donor Funds - Foreign Governments-AfDB");
        Q163.createCell((short) 2).setCellValue("");
        Q163.createCell((short) 3).setCellValue("");
        Q163.createCell((short) 4).setCellValue("");
        Q163.createCell((short) 5).setCellValue("");
        Q163.createCell((short) 6).setCellValue("");
        Q163.createCell((short) 7).setCellValue("");
        Q163.createCell((short) 8).setCellValue("");
        Q163.createCell((short) 9).setCellValue("");
        Q163.createCell((short) 10).setCellValue("");
        Q163.createCell((short) 11).setCellValue("");
        Q163.createCell((short) 12).setCellValue("");
        Q163.createCell((short) 13).setCellValue("");
        Q163.createCell((short) 14).setCellValue("");
        tr++;
        Row Q164 = sheet.createRow((short) tr);
        Q164.createCell((short) 0).setCellValue("");
        Q164.createCell((short) 1).setCellValue("Institutional Support-Supplementary");
        Q164.createCell((short) 2).setCellValue("");
        Q164.createCell((short) 3).setCellValue("");
        Q164.createCell((short) 4).setCellValue("");
        Q164.createCell((short) 5).setCellValue("");
        Q164.createCell((short) 6).setCellValue("");
        Q164.createCell((short) 7).setCellValue("");
        Q164.createCell((short) 8).setCellValue("");
        Q164.createCell((short) 9).setCellValue("");
        Q164.createCell((short) 10).setCellValue("");
        Q164.createCell((short) 11).setCellValue("");
        Q164.createCell((short) 12).setCellValue("");
        Q164.createCell((short) 13).setCellValue("");
        Q164.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q1365 = sheet.createRow((short) tr);
        Q1365.createCell((short) 0).setCellValue("");
        Q1365.createCell((short) 1).setCellValue("Total non-operating Income");
        Q1365.createCell((short) 2).setCellValue("");
        Q1365.createCell((short) 3).setCellValue("");
        Q1365.createCell((short) 4).setCellValue("");
        Q1365.createCell((short) 5).setCellValue("");
        Q1365.createCell((short) 6).setCellValue("");
        Q1365.createCell((short) 7).setCellValue("");
        Q1365.createCell((short) 8).setCellValue("");
        Q1365.createCell((short) 9).setCellValue("");
        Q1365.createCell((short) 10).setCellValue("");
        Q1365.createCell((short) 11).setCellValue("");
        Q1365.createCell((short) 12).setCellValue("");
        Q1365.createCell((short) 13).setCellValue("");
        Q1365.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q166 = sheet.createRow((short) tr);
        Q166.createCell((short) 0).setCellValue("");
        Q166.createCell((short) 1).setCellValue("Total Income");
        Q166.createCell((short) 2).setCellValue("");
        Q166.createCell((short) 3).setCellValue("");
        Q166.createCell((short) 4).setCellValue("");
        Q166.createCell((short) 5).setCellValue("");
        Q166.createCell((short) 6).setCellValue("");
        Q166.createCell((short) 7).setCellValue("");
        Q166.createCell((short) 8).setCellValue("");
        Q166.createCell((short) 9).setCellValue("");
        Q166.createCell((short) 10).setCellValue("");
        Q166.createCell((short) 11).setCellValue("");
        Q166.createCell((short) 12).setCellValue("");
        Q166.createCell((short) 13).setCellValue("");
        Q166.createCell((short) 14).setCellValue("");
        tr++;
        Row Q167 = sheet.createRow((short) tr);
        Q167.createCell((short) 0).setCellValue("");
        Q167.createCell((short) 1).setCellValue("EXPENDITURE");
        Q167.createCell((short) 2).setCellValue("");
        Q167.createCell((short) 3).setCellValue("");
        Q167.createCell((short) 4).setCellValue("");
        Q167.createCell((short) 5).setCellValue("");
        Q167.createCell((short) 6).setCellValue("");
        Q167.createCell((short) 7).setCellValue("");
        Q167.createCell((short) 8).setCellValue("");
        Q167.createCell((short) 9).setCellValue("");
        Q167.createCell((short) 10).setCellValue("");
        Q167.createCell((short) 11).setCellValue("");
        Q167.createCell((short) 12).setCellValue("");
        Q167.createCell((short) 13).setCellValue("");
        Q167.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q168 = sheet.createRow((short) tr);
        Q168.createCell((short) 0).setCellValue("");
        Q168.createCell((short) 1).setCellValue("Revenue expenditure");
        Q168.createCell((short) 2).setCellValue("");
        Q168.createCell((short) 3).setCellValue("");
        Q168.createCell((short) 4).setCellValue("");
        Q168.createCell((short) 5).setCellValue("");
        Q168.createCell((short) 6).setCellValue("");
        Q168.createCell((short) 7).setCellValue("");
        Q168.createCell((short) 8).setCellValue("");
        Q168.createCell((short) 9).setCellValue("");
        Q168.createCell((short) 10).setCellValue("");
        Q168.createCell((short) 11).setCellValue("");
        Q168.createCell((short) 12).setCellValue("");
        Q168.createCell((short) 13).setCellValue("");
        Q168.createCell((short) 14).setCellValue("");
        tr++;
        Row Q169 = sheet.createRow((short) tr);
        Q169.createCell((short) 0).setCellValue("");
        Q169.createCell((short) 1).setCellValue("Wage");
        Q169.createCell((short) 2).setCellValue("");
        Q169.createCell((short) 3).setCellValue("");
        Q169.createCell((short) 4).setCellValue("");
        Q169.createCell((short) 5).setCellValue("");
        Q169.createCell((short) 6).setCellValue("");
        Q169.createCell((short) 7).setCellValue("");
        Q169.createCell((short) 8).setCellValue("");
        Q169.createCell((short) 9).setCellValue("");
        Q169.createCell((short) 10).setCellValue("");
        Q169.createCell((short) 11).setCellValue("");
        Q169.createCell((short) 12).setCellValue("");
        Q169.createCell((short) 13).setCellValue("");
        Q169.createCell((short) 14).setCellValue("");
        tr++;
        Row Q170 = sheet.createRow((short) tr);
        Q170.createCell((short) 0).setCellValue("");
        Q170.createCell((short) 1).setCellValue("Total operating expenditure");
        Q170.createCell((short) 2).setCellValue("");
        Q170.createCell((short) 3).setCellValue("");
        Q170.createCell((short) 4).setCellValue("");
        Q170.createCell((short) 5).setCellValue("");
        Q170.createCell((short) 6).setCellValue("");
        Q170.createCell((short) 7).setCellValue("");
        Q170.createCell((short) 8).setCellValue("");
        Q170.createCell((short) 9).setCellValue("");
        Q170.createCell((short) 10).setCellValue("");
        Q170.createCell((short) 11).setCellValue("");
        Q170.createCell((short) 12).setCellValue("");
        Q170.createCell((short) 13).setCellValue("");
        Q170.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q171 = sheet.createRow((short) tr);
        Q171.createCell((short) 0).setCellValue("");
        Q171.createCell((short) 1).setCellValue("EBITDA");
        Q171.createCell((short) 2).setCellValue("");
        Q171.createCell((short) 3).setCellValue("");
        Q171.createCell((short) 4).setCellValue("");
        Q171.createCell((short) 5).setCellValue("");
        Q171.createCell((short) 6).setCellValue("");
        Q171.createCell((short) 7).setCellValue("");
        Q171.createCell((short) 8).setCellValue("");
        Q171.createCell((short) 9).setCellValue("");
        Q171.createCell((short) 10).setCellValue("");
        Q171.createCell((short) 11).setCellValue("");
        Q171.createCell((short) 12).setCellValue("");
        Q171.createCell((short) 13).setCellValue("");
        Q171.createCell((short) 14).setCellValue("");
        tr++;
        Row Q172 = sheet.createRow((short) tr);
        Q172.createCell((short) 0).setCellValue("");
        Q172.createCell((short) 1).setCellValue("Depreciation & Amortisation");
        Q172.createCell((short) 2).setCellValue("");
        Q172.createCell((short) 3).setCellValue("");
        Q172.createCell((short) 4).setCellValue("");
        Q172.createCell((short) 5).setCellValue("");
        Q172.createCell((short) 6).setCellValue("");
        Q172.createCell((short) 7).setCellValue("");
        Q172.createCell((short) 8).setCellValue("");
        Q172.createCell((short) 9).setCellValue("");
        Q172.createCell((short) 10).setCellValue("");
        Q172.createCell((short) 11).setCellValue("");
        Q172.createCell((short) 12).setCellValue("");
        Q172.createCell((short) 13).setCellValue("");
        Q172.createCell((short) 14).setCellValue("");
        tr++;
        Row Q173 = sheet.createRow((short) tr);
        Q173.createCell((short) 0).setCellValue("");
        Q173.createCell((short) 1).setCellValue("Finance charges");
        Q173.createCell((short) 2).setCellValue("");
        Q173.createCell((short) 3).setCellValue("");
        Q173.createCell((short) 4).setCellValue("");
        Q173.createCell((short) 5).setCellValue("");
        Q173.createCell((short) 6).setCellValue("");
        Q173.createCell((short) 7).setCellValue("");
        Q173.createCell((short) 8).setCellValue("");
        Q173.createCell((short) 9).setCellValue("");
        Q173.createCell((short) 10).setCellValue("");
        Q173.createCell((short) 11).setCellValue("");
        Q173.createCell((short) 12).setCellValue("");
        Q173.createCell((short) 13).setCellValue("");
        Q173.createCell((short) 14).setCellValue("");
        tr++;
        Row Q174 = sheet.createRow((short) tr);
        Q174.createCell((short) 0).setCellValue("");
        Q174.createCell((short) 1).setCellValue("EBT");
        Q174.createCell((short) 2).setCellValue("");
        Q174.createCell((short) 3).setCellValue("");
        Q174.createCell((short) 4).setCellValue("");
        Q174.createCell((short) 5).setCellValue("");
        Q174.createCell((short) 6).setCellValue("");
        Q174.createCell((short) 7).setCellValue("");
        Q174.createCell((short) 8).setCellValue("");
        Q174.createCell((short) 9).setCellValue("");
        Q174.createCell((short) 10).setCellValue("");
        Q174.createCell((short) 11).setCellValue("");
        Q174.createCell((short) 12).setCellValue("");
        Q174.createCell((short) 13).setCellValue("");
        Q174.createCell((short) 14).setCellValue("");
        tr++;
        Row Q175 = sheet.createRow((short) tr);
        Q175.createCell((short) 0).setCellValue("");
        Q175.createCell((short) 1).setCellValue("Rental tax(Provn)");
        Q175.createCell((short) 2).setCellValue("");
        Q175.createCell((short) 3).setCellValue("");
        Q175.createCell((short) 4).setCellValue("");
        Q175.createCell((short) 5).setCellValue("");
        Q175.createCell((short) 6).setCellValue("");
        Q175.createCell((short) 7).setCellValue("");
        Q175.createCell((short) 8).setCellValue("");
        Q175.createCell((short) 9).setCellValue("");
        Q175.createCell((short) 10).setCellValue("");
        Q175.createCell((short) 11).setCellValue("");
        Q175.createCell((short) 12).setCellValue("");
        Q175.createCell((short) 13).setCellValue("");
        Q175.createCell((short) 14).setCellValue("");
        tr++;
        Row Q176 = sheet.createRow((short) tr);
        Q176.createCell((short) 0).setCellValue("");
        Q176.createCell((short) 1).setCellValue("EAT");
        Q176.createCell((short) 2).setCellValue("");
        Q176.createCell((short) 3).setCellValue("");
        Q176.createCell((short) 4).setCellValue("");
        Q176.createCell((short) 5).setCellValue("");
        Q176.createCell((short) 6).setCellValue("");
        Q176.createCell((short) 7).setCellValue("");
        Q176.createCell((short) 8).setCellValue("");
        Q176.createCell((short) 9).setCellValue("");
        Q176.createCell((short) 10).setCellValue("");
        Q176.createCell((short) 11).setCellValue("");
        Q176.createCell((short) 12).setCellValue("");
        Q176.createCell((short) 13).setCellValue("");
        Q176.createCell((short) 14).setCellValue("");
        tr++;
        Row Q177 = sheet.createRow((short) tr);
        Q177.createCell((short) 0).setCellValue("");
        Q177.createCell((short) 1).setCellValue("Total revenue expenditure");
        Q177.createCell((short) 2).setCellValue("");
        Q177.createCell((short) 3).setCellValue("");
        Q177.createCell((short) 4).setCellValue("");
        Q177.createCell((short) 5).setCellValue("");
        Q177.createCell((short) 6).setCellValue("");
        Q177.createCell((short) 7).setCellValue("");
        Q177.createCell((short) 8).setCellValue("");
        Q177.createCell((short) 9).setCellValue("");
        Q177.createCell((short) 10).setCellValue("");
        Q177.createCell((short) 11).setCellValue("");
        Q177.createCell((short) 12).setCellValue("");
        Q177.createCell((short) 13).setCellValue("");
        Q177.createCell((short) 14).setCellValue("");
        tr++;
        Row Q178 = sheet.createRow((short) tr);
        Q178.createCell((short) 0).setCellValue("");
        Q178.createCell((short) 1).setCellValue("Operating ratio");
        Q178.createCell((short) 2).setCellValue("");
        Q178.createCell((short) 3).setCellValue("");
        Q178.createCell((short) 4).setCellValue("");
        Q178.createCell((short) 5).setCellValue("");
        Q178.createCell((short) 6).setCellValue("");
        Q178.createCell((short) 7).setCellValue("");
        Q178.createCell((short) 8).setCellValue("");
        Q178.createCell((short) 9).setCellValue("");
        Q178.createCell((short) 10).setCellValue("");
        Q178.createCell((short) 11).setCellValue("");
        Q178.createCell((short) 12).setCellValue("");
        Q178.createCell((short) 13).setCellValue("");
        Q178.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q179 = sheet.createRow((short) tr);
        Q179.createCell((short) 0).setCellValue("");
        Q179.createCell((short) 1).setCellValue("Capital expenditure");
        Q179.createCell((short) 2).setCellValue("");
        Q179.createCell((short) 3).setCellValue("");
        Q179.createCell((short) 4).setCellValue("");
        Q179.createCell((short) 5).setCellValue("");
        Q179.createCell((short) 6).setCellValue("");
        Q179.createCell((short) 7).setCellValue("");
        Q179.createCell((short) 8).setCellValue("");
        Q179.createCell((short) 9).setCellValue("");
        Q179.createCell((short) 10).setCellValue("");
        Q179.createCell((short) 11).setCellValue("");
        Q179.createCell((short) 12).setCellValue("");
        Q179.createCell((short) 13).setCellValue("");
        Q179.createCell((short) 14).setCellValue("");
        tr++;
        Row Q180 = sheet.createRow((short) tr);
        Q180.createCell((short) 0).setCellValue("");
        Q180.createCell((short) 1).setCellValue("Taxes");
        Q180.createCell((short) 2).setCellValue("");
        Q180.createCell((short) 3).setCellValue("");
        Q180.createCell((short) 4).setCellValue("");
        Q180.createCell((short) 5).setCellValue("");
        Q180.createCell((short) 6).setCellValue("");
        Q180.createCell((short) 7).setCellValue("");
        Q180.createCell((short) 8).setCellValue("");
        Q180.createCell((short) 9).setCellValue("");
        Q180.createCell((short) 10).setCellValue("");
        Q180.createCell((short) 11).setCellValue("");
        Q180.createCell((short) 12).setCellValue("");
        Q180.createCell((short) 13).setCellValue("");
        Q180.createCell((short) 14).setCellValue("");
        tr++;
        Row Q181 = sheet.createRow((short) tr);
        Q181.createCell((short) 0).setCellValue("");
        Q181.createCell((short) 1).setCellValue("Exceptional expenditure");
        Q181.createCell((short) 2).setCellValue("");
        Q181.createCell((short) 3).setCellValue("");
        Q181.createCell((short) 4).setCellValue("");
        Q181.createCell((short) 5).setCellValue("");
        Q181.createCell((short) 6).setCellValue("");
        Q181.createCell((short) 7).setCellValue("");
        Q181.createCell((short) 8).setCellValue("");
        Q181.createCell((short) 9).setCellValue("");
        Q181.createCell((short) 10).setCellValue("");
        Q181.createCell((short) 11).setCellValue("");
        Q181.createCell((short) 12).setCellValue("");
        Q181.createCell((short) 13).setCellValue("");
        Q181.createCell((short) 14).setCellValue("");
        tr++;
        Row Q182 = sheet.createRow((short) tr);
        Q182.createCell((short) 0).setCellValue("");
        Q182.createCell((short) 1).setCellValue("Total expenditure (Incl.Depn).");
        Q182.createCell((short) 2).setCellValue("");
        Q182.createCell((short) 3).setCellValue("");
        Q182.createCell((short) 4).setCellValue("");
        Q182.createCell((short) 5).setCellValue("");
        Q182.createCell((short) 6).setCellValue("");
        Q182.createCell((short) 7).setCellValue("");
        Q182.createCell((short) 8).setCellValue("");
        Q182.createCell((short) 9).setCellValue("");
        Q182.createCell((short) 10).setCellValue("");
        Q182.createCell((short) 11).setCellValue("");
        Q182.createCell((short) 12).setCellValue("");
        Q182.createCell((short) 13).setCellValue("");
        Q182.createCell((short) 14).setCellValue("");
        tr++;
        Row Q183 = sheet.createRow((short) tr);
        Q183.createCell((short) 0).setCellValue("");
        Q183.createCell((short) 1).setCellValue("Less Depn.& amortisation");
        Q183.createCell((short) 2).setCellValue("");
        Q183.createCell((short) 3).setCellValue("");
        Q183.createCell((short) 4).setCellValue("");
        Q183.createCell((short) 5).setCellValue("");
        Q183.createCell((short) 6).setCellValue("");
        Q183.createCell((short) 7).setCellValue("");
        Q183.createCell((short) 8).setCellValue("");
        Q183.createCell((short) 9).setCellValue("");
        Q183.createCell((short) 10).setCellValue("");
        Q183.createCell((short) 11).setCellValue("");
        Q183.createCell((short) 12).setCellValue("");
        Q183.createCell((short) 13).setCellValue("");
        Q183.createCell((short) 14).setCellValue("");
        tr++;
        Row Q184 = sheet.createRow((short) tr);
        Q184.createCell((short) 0).setCellValue("");
        Q184.createCell((short) 1).setCellValue("Net total exp.(Excl.depn.& amortisation)");
        Q184.createCell((short) 2).setCellValue("");
        Q184.createCell((short) 3).setCellValue("");
        Q184.createCell((short) 4).setCellValue("");
        Q184.createCell((short) 5).setCellValue("");
        Q184.createCell((short) 6).setCellValue("");
        Q184.createCell((short) 7).setCellValue("");
        Q184.createCell((short) 8).setCellValue("");
        Q184.createCell((short) 9).setCellValue("");
        Q184.createCell((short) 10).setCellValue("");
        Q184.createCell((short) 11).setCellValue("");
        Q184.createCell((short) 12).setCellValue("");
        Q184.createCell((short) 13).setCellValue("");
        Q184.createCell((short) 14).setCellValue("");
        tr++;
        titleBoldBlue.add((int) tr);
        Row Q185 = sheet.createRow((short) tr);
        Q185.createCell((short) 0).setCellValue("");
        Q185.createCell((short) 1).setCellValue("Surplus/(Deficit)");
        Q185.createCell((short) 2).setCellValue("");
        Q185.createCell((short) 3).setCellValue("");
        Q185.createCell((short) 4).setCellValue("");
        Q185.createCell((short) 5).setCellValue("");
        Q185.createCell((short) 6).setCellValue("");
        Q185.createCell((short) 7).setCellValue("");
        Q185.createCell((short) 8).setCellValue("");
        Q185.createCell((short) 9).setCellValue("");
        Q185.createCell((short) 10).setCellValue("");
        Q185.createCell((short) 11).setCellValue("");
        Q185.createCell((short) 12).setCellValue("");
        Q185.createCell((short) 13).setCellValue("");
        Q185.createCell((short) 14).setCellValue("");
        tr++;
        Row Q186 = sheet.createRow((short) tr);
        Q186.createCell((short) 0).setCellValue("");
        Q186.createCell((short) 1).setCellValue("TOP-LEVEL SUMMARY-2");
        Q186.createCell((short) 2).setCellValue("");
        Q186.createCell((short) 3).setCellValue("");
        Q186.createCell((short) 4).setCellValue("");
        Q186.createCell((short) 5).setCellValue("");
        Q186.createCell((short) 6).setCellValue("");
        Q186.createCell((short) 7).setCellValue("");
        Q186.createCell((short) 8).setCellValue("");
        Q186.createCell((short) 9).setCellValue("");
        Q186.createCell((short) 10).setCellValue("");
        Q186.createCell((short) 11).setCellValue("");
        Q186.createCell((short) 12).setCellValue("");
        Q186.createCell((short) 13).setCellValue("");
        Q186.createCell((short) 14).setCellValue("");
        tr++;
        Row Q187 = sheet.createRow((short) tr);
        Q187.createCell((short) 0).setCellValue("");
        Q187.createCell((short) 1).setCellValue(" Total Income ");
        Q187.createCell((short) 2).setCellValue("");
        Q187.createCell((short) 3).setCellValue("");
        Q187.createCell((short) 4).setCellValue("");
        Q187.createCell((short) 5).setCellValue("");
        Q187.createCell((short) 6).setCellValue("");
        Q187.createCell((short) 7).setCellValue("");
        Q187.createCell((short) 8).setCellValue("");
        Q187.createCell((short) 9).setCellValue("");
        Q187.createCell((short) 10).setCellValue("");
        Q187.createCell((short) 11).setCellValue("");
        Q187.createCell((short) 12).setCellValue("");
        Q187.createCell((short) 13).setCellValue("");
        Q187.createCell((short) 14).setCellValue("");
        tr++;
        Row Q188 = sheet.createRow((short) tr);
        Q188.createCell((short) 0).setCellValue("");
        Q188.createCell((short) 1).setCellValue(" Total expenditure ");
        Q188.createCell((short) 2).setCellValue("");
        Q188.createCell((short) 3).setCellValue("");
        Q188.createCell((short) 4).setCellValue("");
        Q188.createCell((short) 5).setCellValue("");
        Q188.createCell((short) 6).setCellValue("");
        Q188.createCell((short) 7).setCellValue("");
        Q188.createCell((short) 8).setCellValue("");
        Q188.createCell((short) 9).setCellValue("");
        Q188.createCell((short) 10).setCellValue("");
        Q188.createCell((short) 11).setCellValue("");
        Q188.createCell((short) 12).setCellValue("");
        Q188.createCell((short) 13).setCellValue("");
        Q188.createCell((short) 14).setCellValue("");
        tr++;
        Row Q189 = sheet.createRow((short) tr);
        Q189.createCell((short) 0).setCellValue("");
        Q189.createCell((short) 1).setCellValue(" Net surplus/(Deficit)");
        Q189.createCell((short) 2).setCellValue("");
        Q189.createCell((short) 3).setCellValue("");
        Q189.createCell((short) 4).setCellValue("");
        Q189.createCell((short) 5).setCellValue("");
        Q189.createCell((short) 6).setCellValue("");
        Q189.createCell((short) 7).setCellValue("");
        Q189.createCell((short) 8).setCellValue("");
        Q189.createCell((short) 9).setCellValue("");
        Q189.createCell((short) 10).setCellValue("");
        Q189.createCell((short) 11).setCellValue("");
        Q189.createCell((short) 12).setCellValue("");
        Q189.createCell((short) 13).setCellValue("");
        Q189.createCell((short) 14).setCellValue("");
        createDefaultStyle(workbook, sheet, startrow100);
        createTitleStyle(workbook, sheet, title);
        createBoldDeafaultStyle(workbook, sheet, titleJustBold);
        createOrangeDeafaultStyle(workbook, sheet);
        createBoldBlueDeafaultStyle(workbook, sheet, titleBoldBlue);
        createBoldRedDeafaultStyle(workbook, sheet, titleBoldRed);
        createBoldTotalDeafaultStyle(workbook, sheet, titleBoldTotal);
        createBoldOrangeHeaderDeafaultStyle(workbook, sheet, titleBoldOrange);

    }

    private void createHeaderAndBodySummaryBudgetQtr(Workbook workbook, Sheet sheet) {
        sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
        List<Integer> title = new ArrayList();
        List<Integer> titleJustBold = new ArrayList();
        List<Integer> titleBoldBlue = new ArrayList();
        List<Integer> titleBoldRed = new ArrayList();
        List<Integer> titleBoldTotal = new ArrayList();
        int startrow100 = 0;

        //sheet.setFitToPage(true);
        //sheet.setHorizontallyCenter(true);
        short rowHeight = 500; // Adjust the height as needed
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        sheet.setFitToPage(true);

        // Create a cell style with the specified font
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

        // Set default style for the sheet
// Create a bold and centered style with a bottom border
        CellStyle boldCenteredStyle = workbook.createCellStyle();
        boldCenteredStyle.cloneStyleFrom(cellStyle);
        boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCenteredStyle.setFont(boldFont);
        boldCenteredStyle.setBorderBottom(BorderStyle.THIN);
        Row headerRow = sheet.createRow(0);
        //int columnWidth = 10000; // Adjust the width as needed
        //sheet.setColumnWidth(0, columnWidth);
        short tr = 2;

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
        title.add(0);
        CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 6);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        Row header2 = sheet.createRow(1);
        title.add(1);
        Cell header2Cell = header2.createCell(0);
        header2Cell.setCellValue(HeaderExcel3().toUpperCase());
        CellRangeAddress cellRange2 = new CellRangeAddress(1, 1, 0, 6);
        sheet.addMergedRegion(cellRange2);
        setBottomBorderForRegion(sheet, cellRange2);

        titleJustBold.add((int) tr);
        List<String> titles = Arrays.asList("COA CODE", "DETAILS", "BUDGET (TOTAL)", "QTR1", "QTR2", "QTR3", "QTR4");
        Row Q2 = createHeaderRow(sheet, tr, titles);
        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "VOLUMES /STATS", "", "", "", "", "");
        Row Q3 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Northern route", "", "", "", "", "");
        Row Q4 = createHeaderRow(sheet, tr, titles);
        tr++;

        MonthlySumResponseFreight mon = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));
        Row Q5 = createHeaderRow(sheet, tr, "ZFVNR -EXP", "Net Tons- Exports",
                mon.getTotal().doubleValue(), mon.getQtr1().doubleValue(), mon.getQtr2().doubleValue(), mon.getQtr3().doubleValue(), mon.getQtr4().doubleValue());

        tr++;
        MonthlySumResponseFreight mon2 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
        Row Q6 = createHeaderRow(sheet, tr, "ZFVNR-IMP", "Net Tons -Imports",
                mon2.getTotal().doubleValue(), mon2.getQtr1().doubleValue(), mon2.getQtr2().doubleValue(), mon2.getQtr3().doubleValue(), mon2.getQtr4().doubleValue());

        tr++;

        MonthlySumResponseFreight mon3 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
        Row Q7 = createHeaderRow(sheet, tr, "ZFVTN-LC", "Local Net Tons",
                mon3.getTotal().doubleValue(), mon3.getQtr1().doubleValue(), mon3.getQtr2().doubleValue(), mon3.getQtr3().doubleValue(), mon3.getQtr4().doubleValue());

        tr++;
        titleBoldBlue.add((int) tr);
        Row Q8 = createHeaderRow(sheet, tr, "", "Total Tons-Northern",
                totalByRoutes(1).getTotal().doubleValue(), totalByRoutes(1).getQtr1().doubleValue(), totalByRoutes(1).getQtr2().doubleValue(),
                totalByRoutes(1).getQtr3().doubleValue(), totalByRoutes(1).getQtr4().doubleValue());

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Southern route", "", "", "", "", "");
        Row Q9 = createHeaderRow(sheet, tr, titles);

        tr++;

        MonthlySumResponseFreight mon4 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111105", comboBox.getValue()));
        Row Q10 = createHeaderRow(sheet, tr, "ZFVSR -EXP", "Net Tons -Exports",
                mon4.getTotal().doubleValue(), mon4.getQtr1().doubleValue(), mon4.getQtr2().doubleValue(), mon4.getQtr3().doubleValue(), mon4.getQtr4().doubleValue());

        tr++;

        MonthlySumResponseFreight mon5 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111104", comboBox.getValue()));
        Row Q11 = createHeaderRow(sheet, tr, "ZFVSR-IMP", "Net Tons -Imports",
                mon5.getTotal().doubleValue(), mon5.getQtr1().doubleValue(), mon5.getQtr2().doubleValue(), mon5.getQtr3().doubleValue(), mon5.getQtr4().doubleValue());

        tr++;

        MonthlySumResponseFreight mon6 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111106", comboBox.getValue()));
        Row Q12 = createHeaderRow(sheet, tr, "ZFVTSR-LC", "Local Net Tons",
                mon6.getTotal().doubleValue(), mon6.getQtr1().doubleValue(), mon6.getQtr2().doubleValue(), mon6.getQtr3().doubleValue(), mon6.getQtr4().doubleValue());

        tr++;
        titleBoldBlue.add((int) tr);
        Row Q13 = createHeaderRow(sheet, tr, "", "Total Tons-Southern",
                totalByRoutes(2).getTotal().doubleValue(), totalByRoutes(2).getQtr1().doubleValue(), totalByRoutes(2).getQtr2().doubleValue(),
                totalByRoutes(2).getQtr3().doubleValue(), totalByRoutes(2).getQtr4().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        Row Q14 = createHeaderRow(sheet, tr, "", "Total",
                totalByRoutes().getTotal().doubleValue(), totalByRoutes().getQtr1().doubleValue(), totalByRoutes().getQtr2().doubleValue(),
                totalByRoutes().getQtr3().doubleValue(), totalByRoutes().getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Passengers", "", "", "", "", "");
        Row Q15 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZPAS-KNR", "Passengers - Kampala-Namanve route", "", "", "", "", "");
        Row Q16 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZPAS-KOR", "Passengers - Kampala-Other route", "", "", "", "", "");
        Row Q17 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total Passengers", "", "", "", "", "");
        Row Q18 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZTPR-KNV", "Ticket price-Kampala-Namanve", "", "", "", "", "");
        Row Q19 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZTPR-KPB", "Ticket price-Kampalal-PortBell", "", "", "", "", "");
        Row Q20 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Southern route Voyages:", "", "", "", "", "");
        Row Q21 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVK", "MV-Kaawa", "", "", "", "", "");
        Row Q22 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVP", "MV-Pamba", "", "", "", "", "");
        Row Q23 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total voyages-URC", "", "", "", "", "");
        Row Q24 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVU", "MV,Umoja", "", "", "", "", "");
        Row Q25 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVH", "MV-Uhuru", "", "", "", "", "");
        Row Q26 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total voyages-other", "", "", "", "", "");
        Row Q27 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Gross Total voyages", "", "", "", "", "");
        Row Q28 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Number of trains", "", "", "", "", "");
        Row Q29 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZNOTR", "NTK ('000)", "", "", "", "", "");
        Row Q30 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZNOTR-GTK", "GTK(000)", "", "", "", "", "");
        Row Q31 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Fuel (Litres)", "", "", "", "", "");
        Row Q32 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-PSER", "Fuel-Passenger services", "", "", "", "", "");
        Row Q33 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-NR", "Fuel  -Northern route", "", "", "", "", "");
        Row Q34 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-CR", "Fuel -Central route(Marine)", "", "", "", "", "");
        Row Q35 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("ZNOTR-CRN", "Crane", "", "", "", "", "");
        Row Q36 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total  freight Fuel-(Ltrs)", "", "", "", "", "");
        Row Q37 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZPRPL-DIS", "Price per litre-Diesel", "", "", "", "", "");
        Row Q38 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZAVPR-PPS", "Average price per passenger train tkt", "", "", "", "", "");
        Row Q39 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Efficiency", "", "", "", "", "");
        Row Q40 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-NTK", "Fuel efficiency-NTK ('M)", "", "", "", "", "");
        Row Q42 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-GTK", "Fuel efficiency-GTK'(M)", "", "", "", "", "");
        Row Q43 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-WTA", "WTA (days)", "", "", "", "", "");
        Row Q44 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-TRA", "Transit days", "", "", "", "", "");
        Row Q45 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-LOC", "Locomotive Efficiency (Kms per hr)", "", "", "", "", "");
        Row Q46 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-NOE", "No. of employees", "", "", "", "", "");
        Row Q47 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Employee productivity(Revenue)", "", "", "", "", "");
        Row Q48 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000");
        Row Q49 = createHeaderRow(sheet, tr, titles);

        List<MonthlySumResponseFreight> listIncomeTotals = new ArrayList<>();
        List<MonthlySumResponseFreight> listIncomeTotals2 = new ArrayList<>();//Total Other Administration Expenses
        List<MonthlySumResponseFreight> listIncomeTotals3 = new ArrayList<>();//Total Administration Expenses
        List<MonthlySumResponseFreight> listIncomeTotals4 = new ArrayList<>();//Total variable costs
        List<MonthlySumResponseFreight> listIncomeTotals5 = new ArrayList<>();//Depreciation
        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "INCOME", "", "", "", "", "");
        Row Q50 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Re-current Income", "", "", "", "", "");
        Row Q51 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Assets Hire", "", "", "", "", "");
        Row Q52 = createHeaderRow(sheet, tr, titles);
        startrow100 = tr;
        List<URC_ACNT> findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("1112");
        List<COA> listCoas = new ArrayList<>();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            tr++;
            listCoas.add(coa);

            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);

        MonthlySumResponseFreight mon101T = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T);
        Row Q54 = createHeaderRow(sheet, tr, "", "Total Assets hire income",
                mon101T.getTotal().doubleValue(), mon101T.getQtr1().doubleValue(), mon101T.getQtr2().doubleValue(),
                mon101T.getQtr3().doubleValue(), mon101T.getQtr4().doubleValue());

        listCoas.clear();

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Freight Services", "", "", "", "", "");
        Row Q55 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Northern Route", "", "", "", "", "");
        Row Q56 = createHeaderRow(sheet, tr, titles);

        listCoas.clear();
        List<String> ss = new ArrayList<>();
        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }
        ss.clear();

        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight mon101T2 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T2);
        Row Q57 = createHeaderRow(sheet, tr, "", "Total freight-Northern Route",
                mon101T2.getTotal().doubleValue(), mon101T2.getQtr1().doubleValue(), mon101T2.getQtr2().doubleValue(),
                mon101T2.getQtr3().doubleValue(), mon101T2.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Southern Route", "", "", "", "", "");
        Row Q58 = createHeaderRow(sheet, tr, titles);
        ss.addAll(Arrays.asList("4", "5", "6"));
        listCoas.clear();
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight mon101T3 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T3);
        Row Q59 = createHeaderRow(sheet, tr, "", "Total freight-Southern Route",
                mon101T3.getTotal().doubleValue(), mon101T3.getQtr1().doubleValue(), mon101T3.getQtr2().doubleValue(),
                mon101T3.getQtr3().doubleValue(), mon101T3.getQtr4().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        titles = Arrays.asList("", "Total freight Services", "", "", "", "", "");
        Row Q60 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        /*        ss.addAll(Arrays.asList("10", "09", "11"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("1111", ss);*/
        ss.addAll(Arrays.asList("0", "1"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11111", ss);
        ss.clear();

        ss.addAll(Arrays.asList("9"));
        List<URC_ACNT> findByAcntCodeStartingWith3 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith3);

        ss.addAll(Arrays.asList("1", "2", "3", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith2 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11130", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith2);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight mon101T4 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T4);
        Row Q61 = createHeaderRow(sheet, tr, "", "Total Other fees",
                mon101T4.getTotal().doubleValue(), mon101T4.getQtr1().doubleValue(), mon101T4.getQtr2().doubleValue(),
                mon101T4.getQtr3().doubleValue(), mon101T4.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Rent Income", "", "", "", "", "");
        Row Q62 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11140", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T5 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T5);

        Row Q64 = createHeaderRow(sheet, tr, "", "Total Rent Income",
                mon101T5.getTotal().doubleValue(), mon101T5.getQtr1().doubleValue(), mon101T5.getQtr2().doubleValue(),
                mon101T5.getQtr3().doubleValue(), mon101T5.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Passenger Ticket Sales", "", "", "", "", "");
        Row Q65 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11160", ss);
        ss.clear();
        ss.addAll(Arrays.asList("4", "5"));
        List<URC_ACNT> findByAcntCodeStartingWith34 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11170", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith34);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T6 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T6);
        Row Q66 = createHeaderRow(sheet, tr, "", "Total Passenger Service Income",
                mon101T6.getTotal().doubleValue(), mon101T6.getQtr1().doubleValue(), mon101T6.getQtr2().doubleValue(),
                mon101T6.getQtr3().doubleValue(), mon101T6.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Miscellaneous Income", "", "", "", "", "");
        Row Q67 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("2", "3", "7"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11170", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1"));
        List<URC_ACNT> findByAcntCodeStartingWith35 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11180", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith35);
        ss.clear();

        List<URC_ACNT> findByAcntCodeStartingWith36 = urcAcntService.findByAcntCode("145003");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith36);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T7 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T7);
        Row Q68 = createHeaderRow(sheet, tr, "", "Total-Miscellaneous Receipts",
                mon101T7.getTotal().doubleValue(), mon101T7.getQtr1().doubleValue(), mon101T7.getQtr2().doubleValue(),
                mon101T7.getQtr3().doubleValue(), mon101T7.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Passenger Ticket Sales", "", "", "", "", "");
        Row Q69 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Re-Current Income", "", "", "", "", "");
        Row Q70 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Non -Recurrent Income", "", "", "", "", "");
        Row Q71 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("111803");

        List<URC_ACNT> findByAcntCodeStartingWith37 = urcAcntService.findByAcntCode("131101");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith37);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        tr++;
        titles = Arrays.asList("", "Institutional Support-Development-Trr-Gulu", "", "", "", "", "");
        Row Q72 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Inst.Support-Dev.:Trr-Gulu-Suppl.", "", "", "", "", "");
        Row Q73 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("111802");

        List<URC_ACNT> findByAcntCodeStartingWith38 = urcAcntService.findByAcntCode("133202");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith38);
//listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T8 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T8);
        Row Q74 = createHeaderRow(sheet, tr, "", "Total-Non-Recurring Income",
                mon101T8.getTotal().doubleValue(), mon101T8.getQtr1().doubleValue(), mon101T8.getQtr2().doubleValue(),
                mon101T8.getQtr3().doubleValue(), mon101T8.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Exceptional Income", "", "", "", "", "");
        Row Q75 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Receivable from MoFPED", "", "", "", "", "");
        Row Q76 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldTotal.add((int) tr);
        titles = Arrays.asList("", "Total exceptional income", "", "", "", "", "");
        Row Q77 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal1 = calculateTotal(listIncomeTotals);
        Row Q78 = createHeaderRow(sheet, tr, "", "Total Income",
                calculateTotal1.getTotal().doubleValue(), calculateTotal1.getQtr1().doubleValue(),
                calculateTotal1.getQtr2().doubleValue(), calculateTotal1.getQtr3().doubleValue(), calculateTotal1.getQtr4().doubleValue());

        listIncomeTotals.clear();
        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "EXPENDITURE", "", "", "", "", "");
        Row Q79 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Direct Expenses", "", "", "", "", "");
        Row Q80 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Fuel ,Lubricants and Oils", "", "", "", "", "");
        Row Q81 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        ss.addAll(Arrays.asList("4", "5", "6", "7", "8", "9"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22700", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith39 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22701", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith39);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T9 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T9);
        listIncomeTotals4.add(mon101T9);
        Row Q82 = createHeaderRow(sheet, tr, "", "Total-Fuel",
                mon101T9.getTotal().doubleValue(), mon101T9.getQtr1().doubleValue(), mon101T9.getQtr2().doubleValue(),
                mon101T9.getQtr3().doubleValue(), mon101T9.getQtr4().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "");
        Row Q83 = createHeaderRow(sheet, tr, titles);

        tr++;

        listCoas.clear();
        COA coa1 = sampleCoaService.findByCodeAndBudget("224002", comboBox.getValue());
        listCoas.add(coa1);
        MonthlySumResponseFreight mon101A = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("224002", comboBox.getValue()));
        Row Q84 = createHeaderRow(sheet, tr, "224002", "Passenger Services Expenses",
                mon101A.getTotal().doubleValue(), mon101A.getQtr1().doubleValue(), mon101A.getQtr2().doubleValue(),
                mon101A.getQtr3().doubleValue(), mon101A.getQtr4().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        listIncomeTotals.add(mon101A);
        listIncomeTotals4.add(mon101A);
        Row Q85 = createHeaderRow(sheet, tr, "", "Total passenger services expenses",
                mon101A.getTotal().doubleValue(), mon101A.getQtr1().doubleValue(), mon101A.getQtr2().doubleValue(),
                mon101A.getQtr3().doubleValue(), mon101A.getQtr4().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "");
        Row Q86 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("228", "Maintenance", "", "", "", "", "");
        Row Q87 = createHeaderRow(sheet, tr, titles);

        listCoas.clear();
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("228");
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

                tr++;
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T10 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T10);
        listIncomeTotals4.add(mon101T10);
        Row Q88 = createHeaderRow(sheet, tr, "", "Total Maintenance",
                mon101T10.getTotal().doubleValue(), mon101T10.getQtr1().doubleValue(), mon101T10.getQtr2().doubleValue(),
                mon101T10.getQtr3().doubleValue(), mon101T10.getQtr4().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "");
        Row Q89 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("223", "Utility And Property Expenses", "", "", "", "", "");
        Row Q891 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "4"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22300", ss);
        ss.clear();
        ss.addAll(Arrays.asList("2", "3", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith40 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("28150", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith40);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T11 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T11);
        listIncomeTotals4.add(mon101T11);

        Row Q90 = createHeaderRow(sheet, tr, "", "Total Property Expenses",
                mon101T11.getTotal().doubleValue(), mon101T11.getQtr1().doubleValue(), mon101T11.getQtr2().doubleValue(),
                mon101T11.getQtr3().doubleValue(), mon101T11.getQtr4().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "");
        Row Q91 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T11W = calculateTotal(listIncomeTotals4);

        Row Q92 = createHeaderRow(sheet, tr, "", "Total variable costs",
                mon101T11W.getTotal().doubleValue(), mon101T11W.getQtr1().doubleValue(), mon101T11W.getQtr2().doubleValue(),
                mon101T11W.getQtr3().doubleValue(), mon101T11W.getQtr4().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "");
        Row Q93 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Operation Expenses", "", "", "", "", "");
        Row Q931Row = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("211", "Personel Costs", "", "", "", "", "");
        Row Q931Row1 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21110", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "6"));
        List<URC_ACNT> findByAcntCodeStartingWith41 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21210", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith41);

        List<URC_ACNT> findByAcntCodeStartingWith42 = urcAcntService.findByAcntCode("212201");
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith42);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5"));
        List<URC_ACNT> findByAcntCodeStartingWith43 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21300", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith43);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T12 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T12);
        listIncomeTotals3.add(mon101T12);//Total Personnel Cost

        Row Q941 = createHeaderRow(sheet, tr, "", "Total Personnel Cost",
                mon101T12.getTotal().doubleValue(), mon101T12.getQtr1().doubleValue(), mon101T12.getQtr2().doubleValue(),
                mon101T12.getQtr3().doubleValue(), mon101T12.getQtr4().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.exp.", "", "", "", "", "");
        Row Q942 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "ADMINISTRATION EXPENSES", "", "", "", "", "");
        Row Q943 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("221", "General Expenses", "", "", "", "", "");
        Row Q94 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22100", ss);
        ss.clear();
        ss.addAll(Arrays.asList("0", "1", "2", "6", "7"));
        List<URC_ACNT> findByAcntCodeStartingWith60 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22101", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith60);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(), mon101.getQtr3().doubleValue(),
                    mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T13 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T13);
        listIncomeTotals3.add(mon101T13);//Total Administration Expenses

        Row Q95 = createHeaderRow(sheet, tr, "", "Total Administration Expenses",
                mon101T13.getTotal().doubleValue(), mon101T13.getQtr1().doubleValue(), mon101T13.getQtr2().doubleValue(),
                mon101T13.getQtr3().doubleValue(), mon101T13.getQtr4().doubleValue());

        tr++;
        titles = Arrays.asList("", "%age on Oper.income", "", "", "", "", "");
        Row Q96 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "OTHER ADMINISTRATION EXPENSES", "", "", "", "", "");
        Row Q97 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("221", "Board and Legal Expenses", "", "", "", "", "");
        Row Q98 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("221019");
        ss.clear();
        List<URC_ACNT> findByAcntCodeStartingWith601 = urcAcntService.findByAcntCode("221020");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith601);
        List<URC_ACNT> findByAcntCodeStartingWith602 = urcAcntService.findByAcntCode("221008");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith602);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T14 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T14);
        listIncomeTotals2.add(mon101T14);
        Row Q99 = createHeaderRow(sheet, tr, "", "Total Board & Legal Expenses",
                mon101T14.getTotal().doubleValue(), mon101T14.getQtr1().doubleValue(),
                mon101T14.getQtr2().doubleValue(), mon101T14.getQtr3().doubleValue(), mon101T14.getQtr4().doubleValue());

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("222");
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(),
                    mon101.getQtr2().doubleValue(), mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T15 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T15);
        listIncomeTotals2.add(mon101T15);

        Row Q100 = createHeaderRow(sheet, tr, "", "Total Communications",
                mon101T15.getTotal().doubleValue(), mon101T15.getQtr1().doubleValue(),
                mon101T15.getQtr2().doubleValue(), mon101T15.getQtr3().doubleValue(), mon101T15.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("223", "Utilities", "", "", "", "", "");
        Row Q101 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("5", "6", "7", "8"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22300", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T16 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T16);
        listIncomeTotals2.add(mon101T16);

        Row Q102 = createHeaderRow(sheet, tr, "", "Total Utility Expenses",
                mon101T16.getTotal().doubleValue(), mon101T16.getQtr1().doubleValue(),
                mon101T16.getQtr2().doubleValue(), mon101T16.getQtr3().doubleValue(), mon101T16.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("224", "Supplies and Services", "", "", "", "", "");
        Row Q1021 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("3", "4", "5", "6"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22400", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T17 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T17);
        listIncomeTotals2.add(mon101T17);

        Row Q103 = createHeaderRow(sheet, tr, "", "Total Supplies and Services",
                mon101T17.getTotal().doubleValue(), mon101T17.getQtr1().doubleValue(), mon101T17.getQtr2().doubleValue(),
                mon101T17.getQtr3().doubleValue(), mon101T17.getQtr4().doubleValue()
        );

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Professional Services", "", "", "", "", "");
        Row Q104 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22500", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T18 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T18);
        listIncomeTotals2.add(mon101T18);
        Row Q105 = createHeaderRow(sheet, tr, "", "Total Professional Services",
                mon101T18.getTotal().doubleValue(), mon101T18.getQtr1().doubleValue(), mon101T18.getQtr2().doubleValue(),
                mon101T18.getQtr3().doubleValue(), mon101T18.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("226", "Insurances and Licenses", "", "", "", "", "");
        Row Q106 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22600", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            tr++;
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T19 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T19);
        listIncomeTotals2.add(mon101T19);

        Row Q107 = createHeaderRow(sheet, tr, "", "Total Insurances and Licenses",
                mon101T19.getTotal().doubleValue(), mon101T19.getQtr1().doubleValue(), mon101T19.getQtr2().doubleValue(),
                mon101T19.getQtr3().doubleValue(), mon101T19.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("227", "Travel and Transport", "", "", "", "", "");
        Row Q108 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22700", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T191 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T191);
        listIncomeTotals2.add(mon101T191);

        Row Q109 = createHeaderRow(sheet, tr, "", "Total Travel and Transport",
                mon101T191.getTotal().doubleValue(), mon101T191.getQtr1().doubleValue(), mon101T191.getQtr2().doubleValue(),
                mon101T191.getQtr3().doubleValue(), mon101T191.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("282", "Miscellaneous Other Expenses", "", "", "", "", "");
        Row Q110 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("28210", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T1912 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals2.add(mon101T1912);
        listIncomeTotals.add(mon101T1912);
        Row Q111 = createHeaderRow(sheet, tr, "", "Total Miscellaneous Other Expenses",
                mon101T1912.getTotal().doubleValue(), mon101T1912.getQtr1().doubleValue(),
                mon101T1912.getQtr2().doubleValue(), mon101T1912.getQtr3().doubleValue(), mon101T1912.getQtr4().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal10 = calculateTotal(listIncomeTotals2);
        listIncomeTotals3.add(calculateTotal10);//Total Other Administration Expenses
        Row Q112 = createHeaderRow(sheet, tr, "", "Total Other Administration Expenses",
                calculateTotal10.getTotal().doubleValue(), calculateTotal10.getQtr1().doubleValue(),
                calculateTotal10.getQtr2().doubleValue(), calculateTotal10.getQtr3().doubleValue(), calculateTotal10.getQtr4().doubleValue());
        listIncomeTotals2.clear();
        tr++;
        Row Q113 = sheet.createRow((short) tr);
        Q113.createCell((short) 0).setCellValue("");
        Q113.createCell((short) 1).setCellValue("%age on Oper.exp.");
        Q113.createCell((short) 2).setCellValue("");
        Q113.createCell((short) 3).setCellValue("");
        Q113.createCell((short) 4).setCellValue("");
        Q113.createCell((short) 5).setCellValue("");
        Q113.createCell((short) 6).setCellValue("");
        tr++;
        titleBoldTotal.add((int) tr);
        Row Q114 = sheet.createRow((short) tr);
        Q114.createCell((short) 0).setCellValue("");
        Q114.createCell((short) 1).setCellValue("Total Operating Expenses");
        Q114.createCell((short) 2).setCellValue("");
        Q114.createCell((short) 3).setCellValue("");
        Q114.createCell((short) 4).setCellValue("");
        Q114.createCell((short) 5).setCellValue("");
        Q114.createCell((short) 6).setCellValue("");
        tr++;
        Row Q115 = sheet.createRow((short) tr);
        Q115.createCell((short) 0).setCellValue("");
        Q115.createCell((short) 1).setCellValue("%age on Oper.income");
        Q115.createCell((short) 2).setCellValue("");
        Q115.createCell((short) 3).setCellValue("");
        Q115.createCell((short) 4).setCellValue("");
        Q115.createCell((short) 5).setCellValue("");
        Q115.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q116 = sheet.createRow((short) tr);
        Q116.createCell((short) 0).setCellValue("");
        Q116.createCell((short) 1).setCellValue("Total Non-Wage");
        Q116.createCell((short) 2).setCellValue("");
        Q116.createCell((short) 3).setCellValue("");
        Q116.createCell((short) 4).setCellValue("");
        Q116.createCell((short) 5).setCellValue("");
        Q116.createCell((short) 6).setCellValue("");
        tr++;
        Row Q117 = sheet.createRow((short) tr);
        Q117.createCell((short) 0).setCellValue("");
        Q117.createCell((short) 1).setCellValue("%age on Oper.exp");
        Q117.createCell((short) 2).setCellValue("");
        Q117.createCell((short) 3).setCellValue("");
        Q117.createCell((short) 4).setCellValue("");
        Q117.createCell((short) 5).setCellValue("");
        Q117.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q118 = sheet.createRow((short) tr);
        Q118.createCell((short) 0).setCellValue("");
        Q118.createCell((short) 1).setCellValue("EBITDA/Operating Suplus/(Deficit) ");
        Q118.createCell((short) 2).setCellValue("");
        Q118.createCell((short) 3).setCellValue("");
        Q118.createCell((short) 4).setCellValue("");
        Q118.createCell((short) 5).setCellValue("");
        Q118.createCell((short) 6).setCellValue("");
        tr++;
        Row Q119 = sheet.createRow((short) tr);
        Q119.createCell((short) 0).setCellValue("");
        Q119.createCell((short) 1).setCellValue("%age on Oper.income");
        Q119.createCell((short) 2).setCellValue("");
        Q119.createCell((short) 3).setCellValue("");
        Q119.createCell((short) 4).setCellValue("");
        Q119.createCell((short) 5).setCellValue("");
        Q119.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q120 = sheet.createRow((short) tr);
        Q120.createCell((short) 0).setCellValue("");
        Q120.createCell((short) 1).setCellValue("CONSUMPTION OF FIXED ASSETS");
        Q120.createCell((short) 2).setCellValue("");
        Q120.createCell((short) 3).setCellValue("");
        Q120.createCell((short) 4).setCellValue("");
        Q120.createCell((short) 5).setCellValue("");
        Q120.createCell((short) 6).setCellValue("");

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("231");
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            listIncomeTotals5.add(mon101);

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal101 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal101);//Total Other Administration Expenses
        Row Q121 = createHeaderRow(sheet, tr, "", "Total Depreciation",
                calculateTotal101.getTotal().doubleValue(), calculateTotal101.getQtr1().doubleValue(),
                calculateTotal101.getQtr2().doubleValue(), calculateTotal101.getQtr3().doubleValue(), calculateTotal101.getQtr4().doubleValue());
        listIncomeTotals5.clear();

        tr++;
        titleJustBold.add((int) tr);
        Row Q122 = sheet.createRow((short) tr);
        Q122.createCell((short) 0).setCellValue("");
        Q122.createCell((short) 1).setCellValue("Armotisation");
        Q122.createCell((short) 2).setCellValue("");
        Q122.createCell((short) 3).setCellValue("");
        Q122.createCell((short) 4).setCellValue("");
        Q122.createCell((short) 5).setCellValue("");
        Q122.createCell((short) 6).setCellValue("");
        tr++;
        Row Q123 = sheet.createRow((short) tr);
        Q123.createCell((short) 0).setCellValue("");
        Q123.createCell((short) 1).setCellValue("Software");
        Q123.createCell((short) 2).setCellValue("");
        Q123.createCell((short) 3).setCellValue("");
        Q123.createCell((short) 4).setCellValue("");
        Q123.createCell((short) 5).setCellValue("");
        Q123.createCell((short) 6).setCellValue("");
        tr++;
        Row Q124 = sheet.createRow((short) tr);
        Q124.createCell((short) 0).setCellValue("");
        Q124.createCell((short) 1).setCellValue("Other");
        Q124.createCell((short) 2).setCellValue("");
        Q124.createCell((short) 3).setCellValue("");
        Q124.createCell((short) 4).setCellValue("");
        Q124.createCell((short) 5).setCellValue("");
        Q124.createCell((short) 6).setCellValue("");
        tr++;
        Row Q125 = sheet.createRow((short) tr);
        titleJustBold.add((int) tr);
        Q125.createCell((short) 0).setCellValue("");
        Q125.createCell((short) 1).setCellValue("Total Armotisation");
        Q125.createCell((short) 2).setCellValue("");
        Q125.createCell((short) 3).setCellValue("");
        Q125.createCell((short) 4).setCellValue("");
        Q125.createCell((short) 5).setCellValue("");
        Q125.createCell((short) 6).setCellValue("");
        tr++;
        titleBoldTotal.add((int) tr);
        Row Q126 = sheet.createRow((short) tr);
        Q126.createCell((short) 0).setCellValue("");
        Q126.createCell((short) 1).setCellValue("Total Depreciation & Armotisation");
        Q126.createCell((short) 2).setCellValue("");
        Q126.createCell((short) 3).setCellValue("");
        Q126.createCell((short) 4).setCellValue("");
        Q126.createCell((short) 5).setCellValue("");
        Q126.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q127 = sheet.createRow((short) tr);
        Q127.createCell((short) 0).setCellValue("221");
        Q127.createCell((short) 1).setCellValue("Finance Costs");
        Q127.createCell((short) 2).setCellValue("");
        Q127.createCell((short) 3).setCellValue("");
        Q127.createCell((short) 4).setCellValue("");
        Q127.createCell((short) 5).setCellValue("");
        Q127.createCell((short) 6).setCellValue("");

        ss.addAll(Arrays.asList("4", "8"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22101", ss);
        ss.clear();
        listCoas.clear();
        listIncomeTotals5.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {

            tr++;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            listIncomeTotals5.add(mon101);

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal1011 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1011);//Total Other Administration Expenses
        Row Q128 = createHeaderRow(sheet, tr, "", "Total Finance Cost",
                calculateTotal1011.getTotal().doubleValue(), calculateTotal1011.getQtr1().doubleValue(),
                calculateTotal1011.getQtr2().doubleValue(), calculateTotal1011.getQtr3().doubleValue(), calculateTotal1011.getQtr4().doubleValue()
        );
        listIncomeTotals5.clear();

        tr++;
        titleBoldTotal.add((int) tr);
        Row Q129 = sheet.createRow((short) tr);
        Q129.createCell((short) 0).setCellValue("");
        Q129.createCell((short) 1).setCellValue("Total Expenditure");
        Q129.createCell((short) 2).setCellValue("");
        Q129.createCell((short) 3).setCellValue("");
        Q129.createCell((short) 4).setCellValue("");
        Q129.createCell((short) 5).setCellValue("");
        Q129.createCell((short) 6).setCellValue("");
        tr++;
        Row Q130 = sheet.createRow((short) tr);
        Q130.createCell((short) 0).setCellValue("");
        Q130.createCell((short) 1).setCellValue("");
        Q130.createCell((short) 2).setCellValue("");
        Q130.createCell((short) 3).setCellValue("");
        Q130.createCell((short) 4).setCellValue("");
        Q130.createCell((short) 5).setCellValue("");
        Q130.createCell((short) 6).setCellValue("");
        tr++;
        Row Q131 = sheet.createRow((short) tr);
        Q131.createCell((short) 0).setCellValue("");
        Q131.createCell((short) 1).setCellValue("EBT");
        Q131.createCell((short) 2).setCellValue("");
        Q131.createCell((short) 3).setCellValue("");
        Q131.createCell((short) 4).setCellValue("");
        Q131.createCell((short) 5).setCellValue("");
        Q131.createCell((short) 6).setCellValue("");
        tr++;
        Row Q132 = sheet.createRow((short) tr);
        Q132.createCell((short) 0).setCellValue("");
        Q132.createCell((short) 1).setCellValue("%age on Oper.income");
        Q132.createCell((short) 2).setCellValue("");
        Q132.createCell((short) 3).setCellValue("");
        Q132.createCell((short) 4).setCellValue("");
        Q132.createCell((short) 5).setCellValue("");
        Q132.createCell((short) 6).setCellValue("");
        tr++;
        Row Q133 = sheet.createRow((short) tr);
        Q133.createCell((short) 0).setCellValue("");
        Q133.createCell((short) 1).setCellValue("Tax (Provision)");
        Q133.createCell((short) 2).setCellValue("");
        Q133.createCell((short) 3).setCellValue("");
        Q133.createCell((short) 4).setCellValue("");
        Q133.createCell((short) 5).setCellValue("");
        Q133.createCell((short) 6).setCellValue("");
        tr++;
        Row Q134 = sheet.createRow((short) tr);
        Q134.createCell((short) 0).setCellValue("");
        Q134.createCell((short) 1).setCellValue("EAT");
        Q134.createCell((short) 2).setCellValue("");
        Q134.createCell((short) 3).setCellValue("");
        Q134.createCell((short) 4).setCellValue("");
        Q134.createCell((short) 5).setCellValue("");
        Q134.createCell((short) 6).setCellValue("");
        tr++;
        Row Q135 = sheet.createRow((short) tr);
        Q135.createCell((short) 0).setCellValue("");
        Q135.createCell((short) 1).setCellValue("Add Non-Operating/non-recurrent Income");
        Q135.createCell((short) 2).setCellValue("");
        Q135.createCell((short) 3).setCellValue("");
        Q135.createCell((short) 4).setCellValue("");
        Q135.createCell((short) 5).setCellValue("");
        Q135.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q136 = sheet.createRow((short) tr);
        Q136.createCell((short) 0).setCellValue("");
        Q136.createCell((short) 1).setCellValue("Total Surplus/(Deficit)");
        Q136.createCell((short) 2).setCellValue("");
        Q136.createCell((short) 3).setCellValue("");
        Q136.createCell((short) 4).setCellValue("");
        Q136.createCell((short) 5).setCellValue("");
        Q136.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q137 = sheet.createRow((short) tr);
        Q137.createCell((short) 0).setCellValue("");
        Q137.createCell((short) 1).setCellValue("Grand Total Operating Expenses");
        Q137.createCell((short) 2).setCellValue("");
        Q137.createCell((short) 3).setCellValue("");
        Q137.createCell((short) 4).setCellValue("");
        Q137.createCell((short) 5).setCellValue("");
        Q137.createCell((short) 6).setCellValue("");
        tr++;
        Row Q138 = sheet.createRow((short) tr);
        Q138.createCell((short) 0).setCellValue("");
        Q138.createCell((short) 1).setCellValue("Operation Ratio");
        Q138.createCell((short) 2).setCellValue("");
        Q138.createCell((short) 3).setCellValue("");
        Q138.createCell((short) 4).setCellValue("");
        Q138.createCell((short) 5).setCellValue("");
        Q138.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q139 = sheet.createRow((short) tr);
        Q139.createCell((short) 0).setCellValue("");
        Q139.createCell((short) 1).setCellValue("FIXED ASSETS");
        Q139.createCell((short) 2).setCellValue("");
        Q139.createCell((short) 3).setCellValue("");
        Q139.createCell((short) 4).setCellValue("");
        Q139.createCell((short) 5).setCellValue("");
        Q139.createCell((short) 6).setCellValue("");

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("312");
        listCoas.clear();
        listIncomeTotals5.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 4) {

                tr++;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                listIncomeTotals5.add(mon101);

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }
        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight calculateTotal1012 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1012);//Total Other Administration Expenses
        Row Q140 = createHeaderRow(sheet, tr, "", "Total Fixed Assets",
                calculateTotal1012.getTotal().doubleValue(), calculateTotal1012.getQtr1().doubleValue(),
                calculateTotal1012.getQtr2().doubleValue(), calculateTotal1012.getQtr3().doubleValue(), calculateTotal1012.getQtr4().doubleValue());
        listIncomeTotals5.clear();

        tr++;
        titleJustBold.add((int) tr);
        Row Q141 = sheet.createRow((short) tr);
        Q141.createCell((short) 0).setCellValue("");
        Q141.createCell((short) 1).setCellValue("");
        Q141.createCell((short) 2).setCellValue("");
        Q141.createCell((short) 3).setCellValue("");
        Q141.createCell((short) 4).setCellValue("");
        Q141.createCell((short) 5).setCellValue("");
        Q141.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q142 = sheet.createRow((short) tr);
        Q142.createCell((short) 0).setCellValue("");
        Q142.createCell((short) 1).setCellValue("SUMMARY");
        Q142.createCell((short) 2).setCellValue("");
        Q142.createCell((short) 3).setCellValue("");
        Q142.createCell((short) 4).setCellValue("");
        Q142.createCell((short) 5).setCellValue("");
        Q142.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q143 = sheet.createRow((short) tr);
        Q143.createCell((short) 0).setCellValue("");
        Q143.createCell((short) 1).setCellValue("INCOME");
        Q143.createCell((short) 2).setCellValue("");
        Q143.createCell((short) 3).setCellValue("");
        Q143.createCell((short) 4).setCellValue("");
        Q143.createCell((short) 5).setCellValue("");
        Q143.createCell((short) 6).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q144 = sheet.createRow((short) tr);
        Q144.createCell((short) 0).setCellValue("");
        Q144.createCell((short) 1).setCellValue("Operating");
        Q144.createCell((short) 2).setCellValue("");
        Q144.createCell((short) 3).setCellValue("");
        Q144.createCell((short) 4).setCellValue("");
        Q144.createCell((short) 5).setCellValue("");
        Q144.createCell((short) 6).setCellValue("");
        tr++;
        Row Q145 = sheet.createRow((short) tr);
        Q145.createCell((short) 0).setCellValue("");
        Q145.createCell((short) 1).setCellValue("Assets hire");
        Q145.createCell((short) 2).setCellValue("");
        Q145.createCell((short) 3).setCellValue("");
        Q145.createCell((short) 4).setCellValue("");
        Q145.createCell((short) 5).setCellValue("");
        Q145.createCell((short) 6).setCellValue("");
        tr++;
        Row Q146 = sheet.createRow((short) tr);
        Q146.createCell((short) 0).setCellValue("");
        Q146.createCell((short) 1).setCellValue("Freight");
        Q146.createCell((short) 2).setCellValue("");
        Q146.createCell((short) 3).setCellValue("");
        Q146.createCell((short) 4).setCellValue("");
        Q146.createCell((short) 5).setCellValue("");
        Q146.createCell((short) 6).setCellValue("");
        tr++;
        Row Q147 = sheet.createRow((short) tr);
        Q147.createCell((short) 0).setCellValue("");
        Q147.createCell((short) 1).setCellValue("Rent");
        Q147.createCell((short) 2).setCellValue("");
        Q147.createCell((short) 3).setCellValue("");
        Q147.createCell((short) 4).setCellValue("");
        Q147.createCell((short) 5).setCellValue("");
        Q147.createCell((short) 6).setCellValue("");
        tr++;
        Row Q148 = sheet.createRow((short) tr);
        Q148.createCell((short) 0).setCellValue("");
        Q148.createCell((short) 1).setCellValue("Passenger services");
        Q148.createCell((short) 2).setCellValue("");
        Q148.createCell((short) 3).setCellValue("");
        Q148.createCell((short) 4).setCellValue("");
        Q148.createCell((short) 5).setCellValue("");
        Q148.createCell((short) 6).setCellValue("");
        tr++;
        Row Q149 = sheet.createRow((short) tr);
        Q149.createCell((short) 0).setCellValue("");
        Q149.createCell((short) 1).setCellValue("Other fees & charges");
        Q149.createCell((short) 2).setCellValue("");
        Q149.createCell((short) 3).setCellValue("");
        Q149.createCell((short) 4).setCellValue("");
        Q149.createCell((short) 5).setCellValue("");
        Q149.createCell((short) 6).setCellValue("");
        tr++;
        Row Q150 = sheet.createRow((short) tr);
        Q150.createCell((short) 0).setCellValue("");
        Q150.createCell((short) 1).setCellValue("Miscellaneous income");
        Q150.createCell((short) 2).setCellValue("");
        Q150.createCell((short) 3).setCellValue("");
        Q150.createCell((short) 4).setCellValue("");
        Q150.createCell((short) 5).setCellValue("");
        Q150.createCell((short) 6).setCellValue("");
        tr++;
        Row Q151 = sheet.createRow((short) tr);
        Q151.createCell((short) 0).setCellValue("");
        Q151.createCell((short) 1).setCellValue("Income from disposal of obsolete/idle assets");
        Q151.createCell((short) 2).setCellValue("");
        Q151.createCell((short) 3).setCellValue("");
        Q151.createCell((short) 4).setCellValue("");
        Q151.createCell((short) 5).setCellValue("");
        Q151.createCell((short) 6).setCellValue("");
        tr++;
        Row Q152 = sheet.createRow((short) tr);
        Q152.createCell((short) 0).setCellValue("");
        Q152.createCell((short) 1).setCellValue("Total operating Income");
        Q152.createCell((short) 2).setCellValue("");
        Q152.createCell((short) 3).setCellValue("");
        Q152.createCell((short) 4).setCellValue("");
        Q152.createCell((short) 5).setCellValue("");
        Q152.createCell((short) 6).setCellValue("");
        tr++;
        Row Q153 = sheet.createRow((short) tr);
        Q153.createCell((short) 0).setCellValue("");
        Q153.createCell((short) 1).setCellValue("Exceptional Income");
        Q153.createCell((short) 2).setCellValue("");
        Q153.createCell((short) 3).setCellValue("");
        Q153.createCell((short) 4).setCellValue("");
        Q153.createCell((short) 5).setCellValue("");
        Q153.createCell((short) 6).setCellValue("");
        tr++;
        Row Q154 = sheet.createRow((short) tr);
        Q154.createCell((short) 0).setCellValue("");
        Q154.createCell((short) 1).setCellValue("UNRA");
        Q154.createCell((short) 2).setCellValue("");
        Q154.createCell((short) 3).setCellValue("");
        Q154.createCell((short) 4).setCellValue("");
        Q154.createCell((short) 5).setCellValue("");
        Q154.createCell((short) 6).setCellValue("");
        tr++;
        Row Q155 = sheet.createRow((short) tr);
        Q155.createCell((short) 0).setCellValue("");
        Q155.createCell((short) 1).setCellValue("MoFPED");
        Q155.createCell((short) 2).setCellValue("");
        Q155.createCell((short) 3).setCellValue("");
        Q155.createCell((short) 4).setCellValue("");
        Q155.createCell((short) 5).setCellValue("");
        Q155.createCell((short) 6).setCellValue("");
        tr++;
        Row Q156 = sheet.createRow((short) tr);
        Q156.createCell((short) 0).setCellValue("");
        Q156.createCell((short) 1).setCellValue("Total exceptional Income");
        Q156.createCell((short) 2).setCellValue("");
        Q156.createCell((short) 3).setCellValue("");
        Q156.createCell((short) 4).setCellValue("");
        Q156.createCell((short) 5).setCellValue("");
        Q156.createCell((short) 6).setCellValue("");
        tr++;
        Row Q157 = sheet.createRow((short) tr);
        Q157.createCell((short) 0).setCellValue("");
        Q157.createCell((short) 1).setCellValue("Non-operating");
        Q157.createCell((short) 2).setCellValue("");
        Q157.createCell((short) 3).setCellValue("");
        Q157.createCell((short) 4).setCellValue("");
        Q157.createCell((short) 5).setCellValue("");
        Q157.createCell((short) 6).setCellValue("");
        tr++;

        Row Q158 = sheet.createRow((short) tr);
        Q158.createCell((short) 0).setCellValue("");
        Q158.createCell((short) 1).setCellValue("Institutional Support-Freight Operations");
        Q158.createCell((short) 2).setCellValue("");
        Q158.createCell((short) 3).setCellValue("");
        Q158.createCell((short) 4).setCellValue("");
        Q158.createCell((short) 5).setCellValue("");
        Q158.createCell((short) 6).setCellValue("");
        tr++;
        Row Q159 = sheet.createRow((short) tr);
        Q159.createCell((short) 0).setCellValue("");
        Q159.createCell((short) 1).setCellValue("Transfer  by Agencies from Treasury-Counterpart");
        Q159.createCell((short) 2).setCellValue("");
        Q159.createCell((short) 3).setCellValue("");
        Q159.createCell((short) 4).setCellValue("");
        Q159.createCell((short) 5).setCellValue("");
        Q159.createCell((short) 6).setCellValue("");
        tr++;
        Row Q160 = sheet.createRow((short) tr);
        Q160.createCell((short) 0).setCellValue("");
        Q160.createCell((short) 1).setCellValue("Institutional Support-Development-Trr-Gulu");
        Q160.createCell((short) 2).setCellValue("");
        Q160.createCell((short) 3).setCellValue("");
        Q160.createCell((short) 4).setCellValue("");
        Q160.createCell((short) 5).setCellValue("");
        Q160.createCell((short) 6).setCellValue("");
        tr++;
        Row Q161 = sheet.createRow((short) tr);
        Q161.createCell((short) 0).setCellValue("");
        Q161.createCell((short) 1).setCellValue("Inst.Support-Dev.:Trr-Gulu-Suppl.");
        Q161.createCell((short) 2).setCellValue("");
        Q161.createCell((short) 3).setCellValue("");
        Q161.createCell((short) 4).setCellValue("");
        Q161.createCell((short) 5).setCellValue("");
        Q161.createCell((short) 6).setCellValue("");
        tr++;
        Row Q162 = sheet.createRow((short) tr);
        Q162.createCell((short) 0).setCellValue("");
        Q162.createCell((short) 1).setCellValue("Donor Funds - Foreign Governments-Spain");
        Q162.createCell((short) 2).setCellValue("");
        Q162.createCell((short) 3).setCellValue("");
        Q162.createCell((short) 4).setCellValue("");
        Q162.createCell((short) 5).setCellValue("");
        Q162.createCell((short) 6).setCellValue("");
        tr++;
        Row Q163 = sheet.createRow((short) tr);
        Q163.createCell((short) 0).setCellValue("");
        Q163.createCell((short) 1).setCellValue("Donor Funds - Foreign Governments-AfDB");
        Q163.createCell((short) 2).setCellValue("");
        Q163.createCell((short) 3).setCellValue("");
        Q163.createCell((short) 4).setCellValue("");
        Q163.createCell((short) 5).setCellValue("");
        Q163.createCell((short) 6).setCellValue("");
        tr++;
        Row Q164 = sheet.createRow((short) tr);
        Q164.createCell((short) 0).setCellValue("");
        Q164.createCell((short) 1).setCellValue("Institutional Support-Supplementary");
        Q164.createCell((short) 2).setCellValue("");
        Q164.createCell((short) 3).setCellValue("");
        Q164.createCell((short) 4).setCellValue("");
        Q164.createCell((short) 5).setCellValue("");
        Q164.createCell((short) 6).setCellValue("");
        tr++;
        Row Q1365 = sheet.createRow((short) tr);
        Q1365.createCell((short) 0).setCellValue("");
        Q1365.createCell((short) 1).setCellValue("Total non-operating Income");
        Q1365.createCell((short) 2).setCellValue("");
        Q1365.createCell((short) 3).setCellValue("");
        Q1365.createCell((short) 4).setCellValue("");
        Q1365.createCell((short) 5).setCellValue("");
        Q1365.createCell((short) 6).setCellValue("");
        tr++;
        Row Q166 = sheet.createRow((short) tr);
        Q166.createCell((short) 0).setCellValue("");
        Q166.createCell((short) 1).setCellValue("Total Income");
        Q166.createCell((short) 2).setCellValue("");
        Q166.createCell((short) 3).setCellValue("");
        Q166.createCell((short) 4).setCellValue("");
        Q166.createCell((short) 5).setCellValue("");
        Q166.createCell((short) 6).setCellValue("");
        tr++;
        Row Q167 = sheet.createRow((short) tr);
        Q167.createCell((short) 0).setCellValue("");
        Q167.createCell((short) 1).setCellValue("EXPENDITURE");
        Q167.createCell((short) 2).setCellValue("");
        Q167.createCell((short) 3).setCellValue("");
        Q167.createCell((short) 4).setCellValue("");
        Q167.createCell((short) 5).setCellValue("");
        Q167.createCell((short) 6).setCellValue("");
        tr++;
        Row Q168 = sheet.createRow((short) tr);
        Q168.createCell((short) 0).setCellValue("");
        Q168.createCell((short) 1).setCellValue("Revenue expenditure");
        Q168.createCell((short) 2).setCellValue("");
        Q168.createCell((short) 3).setCellValue("");
        Q168.createCell((short) 4).setCellValue("");
        Q168.createCell((short) 5).setCellValue("");
        Q168.createCell((short) 6).setCellValue("");
        tr++;
        Row Q169 = sheet.createRow((short) tr);
        Q169.createCell((short) 0).setCellValue("");
        Q169.createCell((short) 1).setCellValue("Wage");
        Q169.createCell((short) 2).setCellValue("");
        Q169.createCell((short) 3).setCellValue("");
        Q169.createCell((short) 4).setCellValue("");
        Q169.createCell((short) 5).setCellValue("");
        Q169.createCell((short) 6).setCellValue("");
        tr++;
        Row Q170 = sheet.createRow((short) tr);
        Q170.createCell((short) 0).setCellValue("");
        Q170.createCell((short) 1).setCellValue("Total operating expenditure");
        Q170.createCell((short) 2).setCellValue("");
        Q170.createCell((short) 3).setCellValue("");
        Q170.createCell((short) 4).setCellValue("");
        Q170.createCell((short) 5).setCellValue("");
        Q170.createCell((short) 6).setCellValue("");
        tr++;
        Row Q171 = sheet.createRow((short) tr);
        Q171.createCell((short) 0).setCellValue("");
        Q171.createCell((short) 1).setCellValue("EBITDA");
        Q171.createCell((short) 2).setCellValue("");
        Q171.createCell((short) 3).setCellValue("");
        Q171.createCell((short) 4).setCellValue("");
        Q171.createCell((short) 5).setCellValue("");
        Q171.createCell((short) 6).setCellValue("");
        tr++;
        Row Q172 = sheet.createRow((short) tr);
        Q172.createCell((short) 0).setCellValue("");
        Q172.createCell((short) 1).setCellValue("Depreciation & Amortisation");
        Q172.createCell((short) 2).setCellValue("");
        Q172.createCell((short) 3).setCellValue("");
        Q172.createCell((short) 4).setCellValue("");
        Q172.createCell((short) 5).setCellValue("");
        Q172.createCell((short) 6).setCellValue("");
        tr++;
        Row Q173 = sheet.createRow((short) tr);
        Q173.createCell((short) 0).setCellValue("");
        Q173.createCell((short) 1).setCellValue("Finance charges");
        Q173.createCell((short) 2).setCellValue("");
        Q173.createCell((short) 3).setCellValue("");
        Q173.createCell((short) 4).setCellValue("");
        Q173.createCell((short) 5).setCellValue("");
        Q173.createCell((short) 6).setCellValue("");
        tr++;
        Row Q174 = sheet.createRow((short) tr);
        Q174.createCell((short) 0).setCellValue("");
        Q174.createCell((short) 1).setCellValue("EBT");
        Q174.createCell((short) 2).setCellValue("");
        Q174.createCell((short) 3).setCellValue("");
        Q174.createCell((short) 4).setCellValue("");
        Q174.createCell((short) 5).setCellValue("");
        Q174.createCell((short) 6).setCellValue("");
        tr++;
        Row Q175 = sheet.createRow((short) tr);
        Q175.createCell((short) 0).setCellValue("");
        Q175.createCell((short) 1).setCellValue("Rental tax(Provn)");
        Q175.createCell((short) 2).setCellValue("");
        Q175.createCell((short) 3).setCellValue("");
        Q175.createCell((short) 4).setCellValue("");
        Q175.createCell((short) 5).setCellValue("");
        Q175.createCell((short) 6).setCellValue("");
        tr++;
        Row Q176 = sheet.createRow((short) tr);
        Q176.createCell((short) 0).setCellValue("");
        Q176.createCell((short) 1).setCellValue("EAT");
        Q176.createCell((short) 2).setCellValue("");
        Q176.createCell((short) 3).setCellValue("");
        Q176.createCell((short) 4).setCellValue("");
        Q176.createCell((short) 5).setCellValue("");
        Q176.createCell((short) 6).setCellValue("");
        tr++;
        Row Q177 = sheet.createRow((short) tr);
        Q177.createCell((short) 0).setCellValue("");
        Q177.createCell((short) 1).setCellValue("Total revenue expenditure");
        Q177.createCell((short) 2).setCellValue("");
        Q177.createCell((short) 3).setCellValue("");
        Q177.createCell((short) 4).setCellValue("");
        Q177.createCell((short) 5).setCellValue("");
        Q177.createCell((short) 6).setCellValue("");
        tr++;
        Row Q178 = sheet.createRow((short) tr);
        Q178.createCell((short) 0).setCellValue("");
        Q178.createCell((short) 1).setCellValue("Operating ratio");
        Q178.createCell((short) 2).setCellValue("");
        Q178.createCell((short) 3).setCellValue("");
        Q178.createCell((short) 4).setCellValue("");
        Q178.createCell((short) 5).setCellValue("");
        Q178.createCell((short) 6).setCellValue("");
        tr++;
        Row Q179 = sheet.createRow((short) tr);
        Q179.createCell((short) 0).setCellValue("");
        Q179.createCell((short) 1).setCellValue("Capital expenditure");
        Q179.createCell((short) 2).setCellValue("");
        Q179.createCell((short) 3).setCellValue("");
        Q179.createCell((short) 4).setCellValue("");
        Q179.createCell((short) 5).setCellValue("");
        Q179.createCell((short) 6).setCellValue("");
        tr++;
        Row Q180 = sheet.createRow((short) tr);
        Q180.createCell((short) 0).setCellValue("");
        Q180.createCell((short) 1).setCellValue("Taxes");
        Q180.createCell((short) 2).setCellValue("");
        Q180.createCell((short) 3).setCellValue("");
        Q180.createCell((short) 4).setCellValue("");
        Q180.createCell((short) 5).setCellValue("");
        Q180.createCell((short) 6).setCellValue("");
        tr++;
        Row Q181 = sheet.createRow((short) tr);
        Q181.createCell((short) 0).setCellValue("");
        Q181.createCell((short) 1).setCellValue("Exceptional expenditure");
        Q181.createCell((short) 2).setCellValue("");
        Q181.createCell((short) 3).setCellValue("");
        Q181.createCell((short) 4).setCellValue("");
        Q181.createCell((short) 5).setCellValue("");
        Q181.createCell((short) 6).setCellValue("");
        tr++;
        Row Q182 = sheet.createRow((short) tr);
        Q182.createCell((short) 0).setCellValue("");
        Q182.createCell((short) 1).setCellValue("Total expenditure (Incl.Depn).");
        Q182.createCell((short) 2).setCellValue("");
        Q182.createCell((short) 3).setCellValue("");
        Q182.createCell((short) 4).setCellValue("");
        Q182.createCell((short) 5).setCellValue("");
        Q182.createCell((short) 6).setCellValue("");
        tr++;
        Row Q183 = sheet.createRow((short) tr);
        Q183.createCell((short) 0).setCellValue("");
        Q183.createCell((short) 1).setCellValue("Less Depn.& amortisation");
        Q183.createCell((short) 2).setCellValue("");
        Q183.createCell((short) 3).setCellValue("");
        Q183.createCell((short) 4).setCellValue("");
        Q183.createCell((short) 5).setCellValue("");
        Q183.createCell((short) 6).setCellValue("");
        tr++;
        Row Q184 = sheet.createRow((short) tr);
        Q184.createCell((short) 0).setCellValue("");
        Q184.createCell((short) 1).setCellValue("Net total exp.(Excl.depn.& amortisation)");
        Q184.createCell((short) 2).setCellValue("");
        Q184.createCell((short) 3).setCellValue("");
        Q184.createCell((short) 4).setCellValue("");
        Q184.createCell((short) 5).setCellValue("");
        Q184.createCell((short) 6).setCellValue("");
        tr++;
        Row Q185 = sheet.createRow((short) tr);
        Q185.createCell((short) 0).setCellValue("");
        Q185.createCell((short) 1).setCellValue("Surplus/(Deficit)");
        Q185.createCell((short) 2).setCellValue("");
        Q185.createCell((short) 3).setCellValue("");
        Q185.createCell((short) 4).setCellValue("");
        Q185.createCell((short) 5).setCellValue("");
        Q185.createCell((short) 6).setCellValue("");
        tr++;
        Row Q186 = sheet.createRow((short) tr);
        Q186.createCell((short) 0).setCellValue("");
        Q186.createCell((short) 1).setCellValue("TOP-LEVEL SUMMARY-2");
        Q186.createCell((short) 2).setCellValue("");
        Q186.createCell((short) 3).setCellValue("");
        Q186.createCell((short) 4).setCellValue("");
        Q186.createCell((short) 5).setCellValue("");
        Q186.createCell((short) 6).setCellValue("");
        tr++;
        Row Q187 = sheet.createRow((short) tr);
        Q187.createCell((short) 0).setCellValue("");
        Q187.createCell((short) 1).setCellValue(" Total Income ");
        Q187.createCell((short) 2).setCellValue("");
        Q187.createCell((short) 3).setCellValue("");
        Q187.createCell((short) 4).setCellValue("");
        Q187.createCell((short) 5).setCellValue("");
        Q187.createCell((short) 6).setCellValue("");
        tr++;
        Row Q188 = sheet.createRow((short) tr);
        Q188.createCell((short) 0).setCellValue("");
        Q188.createCell((short) 1).setCellValue(" Total expenditure ");
        Q188.createCell((short) 2).setCellValue("");
        Q188.createCell((short) 3).setCellValue("");
        Q188.createCell((short) 4).setCellValue("");
        Q188.createCell((short) 5).setCellValue("");
        Q188.createCell((short) 6).setCellValue("");
        tr++;
        Row Q189 = sheet.createRow((short) tr);
        Q189.createCell((short) 0).setCellValue("");
        Q189.createCell((short) 1).setCellValue(" Net surplus/(Deficit)");
        Q189.createCell((short) 2).setCellValue("");
        Q189.createCell((short) 3).setCellValue("");
        Q189.createCell((short) 4).setCellValue("");
        Q189.createCell((short) 5).setCellValue("");
        Q189.createCell((short) 6).setCellValue("");
        createDefaultStyle(workbook, sheet, startrow100);
        createTitleStyle(workbook, sheet, title);
        createBoldDeafaultStyle(workbook, sheet, titleJustBold);
        createOrangeDeafaultStyle(workbook, sheet);
        createBoldBlueDeafaultStyle(workbook, sheet, titleBoldBlue);
        createBoldRedDeafaultStyle(workbook, sheet, titleBoldRed);
        createBoldTotalDeafaultStyle(workbook, sheet, titleBoldTotal);

    }

    private void createHeaderAndBodySummaryBudgetQtrActuals(Workbook workbook, Sheet sheet) {

        sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
        List<Integer> title = new ArrayList();
        List<Integer> titleJustBold = new ArrayList();
        List<Integer> titleBoldBlue = new ArrayList();
        List<Integer> titleBoldRed = new ArrayList();
        List<Integer> titleBoldTotal = new ArrayList();
        List<Integer> titleBoldOrange = new ArrayList();
        List<String> listCOACodes = new ArrayList();
        List<String> listCOACodesT = new ArrayList();
        List<String> listCOACodesTT = new ArrayList();
        int startrow100 = 0;
        //sheet.setFitToPage(true);
        //sheet.setHorizontallyCenter(true);
        short rowHeight = 500; // Adjust the height as needed
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        sheet.setFitToPage(true);

        // Create a cell style with the specified font
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

        // Set default style for the sheet
// Create a bold and centered style with a bottom border
        CellStyle boldCenteredStyle = workbook.createCellStyle();
        boldCenteredStyle.cloneStyleFrom(cellStyle);
        boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCenteredStyle.setFont(boldFont);
        boldCenteredStyle.setBorderBottom(BorderStyle.THIN);
        Row headerRow = sheet.createRow(0);
        //int columnWidth = 10000; // Adjust the width as needed
        //sheet.setColumnWidth(0, columnWidth);
        short tr = 2;

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
        title.add(0);
        CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 11);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        Row header2 = sheet.createRow(1);
        title.add(1);
        Cell header2Cell = header2.createCell(0);
        header2Cell.setCellValue(HeaderExcel3().toUpperCase());
        CellRangeAddress cellRange2 = new CellRangeAddress(1, 1, 0, 11);
        sheet.addMergedRegion(cellRange2);
        setBottomBorderForRegion(sheet, cellRange2);
        titleJustBold.add((int) tr);
        titleBoldOrange.add((int) tr);

        Row Q223 = sheet.createRow((short) tr);
        Q223.createCell((short) 0).setCellValue("");
        Q223.createCell((short) 1).setCellValue("");
        Q223.createCell((short) 2).setCellValue(getPreviousFy(comboBox.getValue().getFinancialYear()));
        Q223.createCell((short) 3).setCellValue("");
        Q223.createCell((short) 4).setCellValue("");
        Q223.createCell((short) 5).setCellValue("");
        Q223.createCell((short) 6).setCellValue("");
        Q223.createCell((short) 7).setCellValue(comboBox.getValue().getFinancialYear());
        Q223.createCell((short) 8).setCellValue("");
        Q223.createCell((short) 9).setCellValue("");
        Q223.createCell((short) 10).setCellValue("");
        Q223.createCell((short) 11).setCellValue("");

        CellRangeAddress cellRange21Address = new CellRangeAddress(tr, tr, 2, 6);
        sheet.addMergedRegion(cellRange21Address);
        CellRangeAddress cellRange21Address2 = new CellRangeAddress(tr, tr, 7, 11);
        sheet.addMergedRegion(cellRange21Address2);
        tr++;
        titleJustBold.add((int) tr);
        List<String> titles = Arrays.asList("COA CODE", "DETAILS", "BUDGET (TOTAL)", "ACTUAL QTR1", "ACTUAL QTR2", "ACTUAL QTR3", "ACTUAL QTR4", "BUDGET (TOTAL)", "BUDGET QTR1", "BUDGET QTR2", "BUDGET QTR3", "BUDGET QTR4");
        Row Q2 = createHeaderRow(sheet, tr, titles);
        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "VOLUMES /STATS", "", "", "", "", "", "", "", "", "", "");
        Row Q3 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Northern route", "", "", "", "", "", "", "", "", "", "");
        Row Q4 = createHeaderRow(sheet, tr, titles);
        tr++;
        Budget bug = getPreviousBudget(comboBox.getValue().getFinancialYear());
        MonthlySumResponseFreight mon = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));

        MonthlySumResponseFreight pmon = sampleFreightVolumesService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));
        Row Q5 = createHeaderRow(sheet, tr, "ZFVNR -EXP", "Net Tons- Exports",
                sampleUrcBSalfldgService.findTotalAmountByAccntCode("ZFVNR -EXP").doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr1, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr2, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr3, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr4, comboBox.getValue().getFinancialYear()).doubleValue(),
                mon.getTotal().doubleValue(), mon.getQtr1().doubleValue(), mon.getQtr2().doubleValue(), mon.getQtr3().doubleValue(), mon.getQtr4().doubleValue());

        tr++;
        MonthlySumResponseFreight mon2 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
        MonthlySumResponseFreight pmon2 = sampleFreightVolumesService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
        Row Q6 = createHeaderRow(sheet, tr, "ZFVNR-IMP", "Net Tons -Imports",
                sampleUrcBSalfldgService.findTotalAmountByAccntCode("ZFVNR-IMP").doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr1, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr2, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr3, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr4, comboBox.getValue().getFinancialYear()).doubleValue(),
                mon2.getTotal().doubleValue(), mon2.getQtr1().doubleValue(), mon2.getQtr2().doubleValue(), mon2.getQtr3().doubleValue(), mon2.getQtr4().doubleValue());

        tr++;

        MonthlySumResponseFreight mon3 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
        MonthlySumResponseFreight pmon3 = sampleFreightVolumesService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
        Row Q7 = createHeaderRow(sheet, tr, "ZFVTN-LC", "Local Net Tons",
                sampleUrcBSalfldgService.findTotalAmountByAccntCode("ZFVTN-LC").doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVTN-LC", Quarters.Qtr1, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVTN-LC", Quarters.Qtr2, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVTN-LC", Quarters.Qtr3, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVTN-LC", Quarters.Qtr4, comboBox.getValue().getFinancialYear()).doubleValue(), mon3.getTotal().doubleValue(), mon3.getQtr1().doubleValue(), mon3.getQtr2().doubleValue(), mon3.getQtr3().doubleValue(), mon3.getQtr4().doubleValue());

        tr++;
        titleBoldBlue.add((int) tr);
        Row Q8 = createHeaderRow(sheet, tr, "", "Total Tons-Northern",
                BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(),
                totalByRoutes(1).getTotal().doubleValue(), totalByRoutes(1).getQtr1().doubleValue(), totalByRoutes(1).getQtr2().doubleValue(),
                totalByRoutes(1).getQtr3().doubleValue(), totalByRoutes(1).getQtr4().doubleValue());

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Southern route", "", "", "", "", "", "", "", "", "", "");
        Row Q9 = createHeaderRow(sheet, tr, titles);

        tr++;

        MonthlySumResponseFreight mon4 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111105", comboBox.getValue()));
        Row Q10 = createHeaderRow(sheet, tr, "ZFVSR -EXP", "Net Tons -Exports",
                BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(),
                mon4.getTotal().doubleValue(), mon4.getQtr1().doubleValue(), mon4.getQtr2().doubleValue(), mon4.getQtr3().doubleValue(), mon4.getQtr4().doubleValue());

        tr++;

        MonthlySumResponseFreight mon5 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111104", comboBox.getValue()));
        Row Q11 = createHeaderRow(sheet, tr, "ZFVSR-IMP", "Net Tons -Imports",
                BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(),
                mon5.getTotal().doubleValue(), mon5.getQtr1().doubleValue(), mon5.getQtr2().doubleValue(), mon5.getQtr3().doubleValue(), mon5.getQtr4().doubleValue());

        tr++;

        MonthlySumResponseFreight mon6 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111106", comboBox.getValue()));
        Row Q12 = createHeaderRow(sheet, tr, "ZFVTSR-LC", "Local Net Tons",
                BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(),
                mon6.getTotal().doubleValue(), mon6.getQtr1().doubleValue(), mon6.getQtr2().doubleValue(), mon6.getQtr3().doubleValue(), mon6.getQtr4().doubleValue());

        tr++;
        titleBoldBlue.add((int) tr);
        Row Q13 = createHeaderRow(sheet, tr, "", "Total Tons-Southern",
                BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(),
                totalByRoutes(2).getTotal().doubleValue(), totalByRoutes(2).getQtr1().doubleValue(), totalByRoutes(2).getQtr2().doubleValue(),
                totalByRoutes(2).getQtr3().doubleValue(), totalByRoutes(2).getQtr4().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        Row Q14 = createHeaderRow(sheet, tr, "", "Total",
                BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(), BigDecimal.ZERO.doubleValue(),
                totalByRoutes().getTotal().doubleValue(), totalByRoutes().getQtr1().doubleValue(), totalByRoutes().getQtr2().doubleValue(),
                totalByRoutes().getQtr3().doubleValue(), totalByRoutes().getQtr4().doubleValue());

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Passengers", "", "", "", "", "", "", "", "", "", "");
        Row Q15 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZPAS-KNR", "Passengers - Kampala-Namanve route", "", "", "", "", "", "", "", "", "", "");
        Row Q16 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZPAS-KOR", "Passengers - Kampala-Other route", "", "", "", "", "", "", "", "", "", "");
        Row Q17 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total Passengers", "", "", "", "", "", "", "", "", "", "");
        Row Q18 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZTPR-KNV", "Ticket price-Kampala-Namanve", "", "", "", "", "", "", "", "", "", "");
        Row Q19 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZTPR-KPB", "Ticket price-Kampalal-PortBell", "", "", "", "", "", "", "", "", "", "");
        Row Q20 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Southern route Voyages:", "", "", "", "", "", "", "", "", "", "");
        Row Q21 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVK", "MV-Kaawa", "", "", "", "", "", "", "", "", "", "");
        Row Q22 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVP", "MV-Pamba", "", "", "", "", "", "", "", "", "", "");
        Row Q23 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total voyages-URC", "", "", "", "", "", "", "", "", "", "");
        Row Q24 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVU", "MV,Umoja", "", "", "", "", "", "", "", "", "", "");
        Row Q25 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZSRVO-MVH", "MV-Uhuru", "", "", "", "", "", "", "", "", "", "");
        Row Q26 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total voyages-other", "", "", "", "", "", "", "", "", "", "");
        Row Q27 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Gross Total voyages", "", "", "", "", "", "", "", "", "", "");
        Row Q28 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Number of trains", "", "", "", "", "", "", "", "", "", "");
        Row Q29 = createHeaderRow(sheet, tr, titles);

        tr++;

        titles = Arrays.asList("ZNOTR", "NTK ('000)", "", "", "", "", "", "", "", "", "", "");
        Row Q30 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZNOTR-GTK", "GTK(000)", "", "", "", "", "", "", "", "", "", "");
        Row Q31 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Fuel (Litres)", "", "", "", "", "", "", "", "", "", "");
        Row Q32 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-PSER", "Fuel-Passenger services", "", "", "", "", "", "", "", "", "", "");
        Row Q33 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-NR", "Fuel  -Northern route", "", "", "", "", "", "", "", "", "", "");
        Row Q34 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZFUEL-CR", "Fuel -Central route(Marine)", "", "", "", "", "", "", "", "", "", "");
        Row Q35 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("ZNOTR-CRN", "Crane", "", "", "", "", "", "", "", "", "", "");
        Row Q36 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Total  freight Fuel-(Ltrs)", "", "", "", "", "", "", "", "", "", "");
        Row Q37 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZPRPL-DIS", "Price per litre-Diesel", "", "", "", "", "", "", "", "", "", "");
        Row Q38 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZAVPR-PPS", "Average price per passenger train tkt", "", "", "", "", "", "", "", "", "", "");
        Row Q39 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Efficiency", "", "", "", "", "", "", "", "", "", "");
        Row Q40 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-NTK", "Fuel efficiency-NTK ('M)", "", "", "", "", "", "", "", "", "", "");
        Row Q42 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-GTK", "Fuel efficiency-GTK'(M)", "", "", "", "", "", "", "", "", "", "");
        Row Q43 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-WTA", "WTA (days)", "", "", "", "", "", "", "", "", "", "");
        Row Q44 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-TRA", "Transit days", "", "", "", "", "", "", "", "", "", "");
        Row Q45 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-LOC", "Locomotive Efficiency (Kms per hr)", "", "", "", "", "", "", "", "", "", "");
        Row Q46 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("ZEFFI-NOE", "No. of employees", "", "", "", "", "", "", "", "", "", "");
        Row Q47 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Employee productivity(Revenue)", "", "", "", "", "", "", "", "", "", "");
        Row Q48 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000", "Ugx'000");
        Row Q49 = createHeaderRow(sheet, tr, titles);

        List<MonthlySumResponseFreight> listIncomeTotals = new ArrayList<>();
        List<MonthlySumResponseFreight> listIncomeTotals2 = new ArrayList<>();//Total Other Administration Expenses
        List<MonthlySumResponseFreight> listIncomeTotals3 = new ArrayList<>();//Total Administration Expenses
        List<MonthlySumResponseFreight> listIncomeTotals4 = new ArrayList<>();//Total variable costs
        List<MonthlySumResponseFreight> listIncomeTotals5 = new ArrayList<>();//Depreciation
        List<MonthlySumResponseFreight> listVariableCosts = new ArrayList<>();//VariableCosts

        BigDecimal ptotal = BigDecimal.ZERO;
        BigDecimal suntotal = BigDecimal.ZERO;
        BigDecimal suntotalT = BigDecimal.ZERO;
        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "INCOME", "", "", "", "", "", "", "", "", "", "");
        Row Q50 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "Re-current Income", "", "", "", "", "", "", "", "", "", "");
        Row Q51 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Assets Hire", "", "", "", "", "", "", "", "", "", "");
        Row Q52 = createHeaderRow(sheet, tr, titles);

        List<URC_ACNT> findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("1112");
        List<COA> listCoas = new ArrayList<>();
        List<COA> listCoasT = new ArrayList<>();
        listCOACodes.clear();
        startrow100 = tr;
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);

        MonthlySumResponseFreight mon101T = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q54 = createHeaderRow(sheet, tr, "", "Total Assets hire income",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T.getTotal().doubleValue(), mon101T.getQtr1().doubleValue(), mon101T.getQtr2().doubleValue(),
                mon101T.getQtr3().doubleValue(), mon101T.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        listCoas.clear();

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Freight Services", "", "", "", "", "", "", "", "", "", "");
        Row Q55 = createHeaderRow(sheet, tr, titles);
        BigDecimal tt = BigDecimal.ZERO;
        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Northern Route", "", "", "", "", "", "", "", "", "", "");
        Row Q56 = createHeaderRow(sheet, tr, titles);

        listCoas.clear();
        List<String> ss = new ArrayList<>();
        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        int i = 0;
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesT.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        ss.clear();

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T2 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T2);

        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q57 = createHeaderRow(sheet, tr, "", "Total freight-Northern Route",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T2.getTotal().doubleValue(), mon101T2.getQtr1().doubleValue(), mon101T2.getQtr2().doubleValue(),
                mon101T2.getQtr3().doubleValue(), mon101T2.getQtr4().doubleValue());
        listCOACodes.clear();
        suntotal = BigDecimal.ZERO;
        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Southern Route", "", "", "", "", "", "", "", "", "", "");
        Row Q58 = createHeaderRow(sheet, tr, titles);
        ss.addAll(Arrays.asList("4", "5", "6"));
        listCoas.clear();
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            listCOACodes.add(coa.getCode().trim());
            listCOACodesT.add(coa.getCode().trim());
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T3 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T3);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q59 = createHeaderRow(sheet, tr, "", "Total freight-Southern Route",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T3.getTotal().doubleValue(), mon101T3.getQtr1().doubleValue(), mon101T3.getQtr2().doubleValue(),
                mon101T3.getQtr3().doubleValue(), mon101T3.getQtr4().doubleValue());
        listCOACodes.clear();
        suntotal = BigDecimal.ZERO;

        tr++;
        titleBoldTotal.add((int) tr);

        Row Q60 = createHeaderRow(sheet, tr, "", "Total freight Services",
                tt.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetAndCoacodeFreight(comboBox.getValue(), Display.FREIGHT, "total").doubleValue(),
                sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetAndCoacodeFreight(comboBox.getValue(), Display.FREIGHT, "qtr1").doubleValue(),
                sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetAndCoacodeFreight(comboBox.getValue(), Display.FREIGHT, "qtr2").doubleValue(),
                sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetAndCoacodeFreight(comboBox.getValue(), Display.FREIGHT, "qtr3").doubleValue(),
                sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetAndCoacodeFreight(comboBox.getValue(), Display.FREIGHT, "qtr4").doubleValue());

        ss.clear();
        listCOACodesT.clear();
        /*        ss.addAll(Arrays.asList("10", "09", "11"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("1111", ss);*/
        ss.addAll(Arrays.asList("0", "1"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11111", ss);
        ss.clear();

        ss.addAll(Arrays.asList("9"));
        List<URC_ACNT> findByAcntCodeStartingWith3 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11110", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith3);

        ss.addAll(Arrays.asList("1", "2", "3", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith2 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11130", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith2);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T4 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T4);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q61 = createHeaderRow(sheet, tr, "", "Total Other fees",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T4.getTotal().doubleValue(), mon101T4.getQtr1().doubleValue(), mon101T4.getQtr2().doubleValue(),
                mon101T4.getQtr3().doubleValue(), mon101T4.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Rent Income", "", "", "", "", "", "", "", "", "", "");
        Row Q62 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11140", ss);
        ss.clear();
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T5 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T5);;
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q64 = createHeaderRow(sheet, tr, "", "Total Rent Income",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T5.getTotal().doubleValue(), mon101T5.getQtr1().doubleValue(), mon101T5.getQtr2().doubleValue(),
                mon101T5.getQtr3().doubleValue(), mon101T5.getQtr4().doubleValue());

        suntotal = BigDecimal.ZERO;

        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "Passenger Ticket Sales", "", "", "", "", "", "", "", "", "", "");
        Row Q65 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11160", ss);
        ss.clear();
        ss.addAll(Arrays.asList("4", "5"));
        List<URC_ACNT> findByAcntCodeStartingWith34 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11170", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith34);
        ss.clear();
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T6 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T6);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q66 = createHeaderRow(sheet, tr, "", "Total Passenger Service Income",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T6.getTotal().doubleValue(), mon101T6.getQtr1().doubleValue(), mon101T6.getQtr2().doubleValue(),
                mon101T6.getQtr3().doubleValue(), mon101T6.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Miscellaneous Income", "", "", "", "", "", "", "", "", "", "");
        Row Q67 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("2", "3", "7"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11170", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1"));
        List<URC_ACNT> findByAcntCodeStartingWith35 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("11180", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith35);
        ss.clear();

        List<URC_ACNT> findByAcntCodeStartingWith36 = urcAcntService.findByAcntCode("145003");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith36);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T7 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T7);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q68 = createHeaderRow(sheet, tr, "", "Total-Miscellaneous Receipts",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T7.getTotal().doubleValue(), mon101T7.getQtr1().doubleValue(), mon101T7.getQtr2().doubleValue(),
                mon101T7.getQtr3().doubleValue(), mon101T7.getQtr4().doubleValue());

        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Passenger Ticket Sales", "", "", "", "", "", "", "", "", "", "");
        Row Q69 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Re-Current Income", "", "", "", "", "", "", "", "", "", "");
        Row Q70 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "Non -Recurrent Income", "", "", "", "", "", "", "", "", "", "");
        Row Q71 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("111803");

        List<URC_ACNT> findByAcntCodeStartingWith37 = urcAcntService.findByAcntCode("131101");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith37);
        listCoas.clear();
        listCOACodes.clear();
        listCOACodesT.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesT.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        tr++;
        titles = Arrays.asList("", "Institutional Support-Development-Trr-Gulu", "", "", "", "", "", "", "", "", "", "");
        Row Q72 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Inst.Support-Dev.:Trr-Gulu-Suppl.", "", "", "", "", "", "", "", "", "", "");
        Row Q73 = createHeaderRow(sheet, tr, titles);

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("111802");

        List<URC_ACNT> findByAcntCodeStartingWith38 = urcAcntService.findByAcntCode("133202");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith38);
//listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCoasT.add(coa);
            listCOACodes.add(coa.getCode().trim());
            listCOACodesT.add(coa.getCode().trim());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T8 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T8);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoasT);
            ptotal = pmon101.getTotal();
        }
        Row Q74 = createHeaderRow(sheet, tr, "", "Total-Non-Recurring Income",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T8.getTotal().doubleValue(), mon101T8.getQtr1().doubleValue(), mon101T8.getQtr2().doubleValue(),
                mon101T8.getQtr3().doubleValue(), mon101T8.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "Exceptional Income", "", "", "", "", "", "", "", "", "", "");
        Row Q75 = createHeaderRow(sheet, tr, titles);

        tr++;
        titles = Arrays.asList("", "Receivable from MoFPED", "", "", "", "", "", "", "", "", "", "");
        Row Q76 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "Total exceptional income", "", "", "", "", "", "", "", "", "", "");
        Row Q77 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldBlue.add((int) tr);
        titleBoldBlue.add((int) tr);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoasT);
            ptotal = pmon101.getTotal();
        }
        MonthlySumResponseFreight calculateTotal1 = calculateTotal(listIncomeTotals);
        Row Q78 = createHeaderRow(sheet, tr, "", "Total Income",
                suntotalT.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                calculateTotal1.getTotal().doubleValue(), calculateTotal1.getQtr1().doubleValue(),
                calculateTotal1.getQtr2().doubleValue(), calculateTotal1.getQtr3().doubleValue(), calculateTotal1.getQtr4().doubleValue());
        MonthlySumResponseFreight totalIncome = new MonthlySumResponseFreight();
        totalIncome.setTotal(calculateTotal1.getTotal());
        totalIncome.setQtr1(calculateTotal1.getQtr1());
        totalIncome.setQtr2(calculateTotal1.getQtr2());
        totalIncome.setQtr3(calculateTotal1.getQtr3());
        totalIncome.setQtr4(calculateTotal1.getQtr4());
        MonthlySumResponseFreight totalIncomeActuals = new MonthlySumResponseFreight();
        totalIncomeActuals.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setTotal(calculateTotal1.getTotal());
        listIncomeTotals.clear();
        suntotalT = BigDecimal.ZERO;
        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "EXPENDITURE", "", "", "", "", "", "", "", "", "", "");
        Row Q79 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Direct Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q80 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Fuel ,Lubricants and Oils", "", "", "", "", "", "", "", "", "", "");
        Row Q81 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        ss.addAll(Arrays.asList("4", "5", "6", "7", "8", "9"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22700", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith39 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22701", ss);
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith39);
        ss.clear();
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCOACodes.add(coa.getCode());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T9 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T9);
        listIncomeTotals4.add(mon101T9);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q82 = createHeaderRow(sheet, tr, "", "Total-Fuel",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T9.getTotal().doubleValue(), mon101T9.getQtr1().doubleValue(), mon101T9.getQtr2().doubleValue(),
                mon101T9.getQtr3().doubleValue(), mon101T9.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts1 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts1.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts1.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts1.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts1.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        listVariableCosts.add(VariableCosts1);
        suntotal = BigDecimal.ZERO;
        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q83 = createHeaderRow(sheet, tr, titles);

        tr++;

        listCoas.clear();
        COA coa1 = sampleCoaService.findByCodeAndBudget("224002", comboBox.getValue());
        listCoas.add(coa1);
        MonthlySumResponseFreight mon101A = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("224002", comboBox.getValue()));
        Row Q84 = createHeaderRow(sheet, tr, "224002", "Passenger Services Expenses",
                sampleUrcBSalfldgService.findTotalAmountByAccntCode("224002").doubleValue(),
                getQtrs("224002", Quarters.Qtr1).doubleValue(),
                getQtrs("224002", Quarters.Qtr2).doubleValue(),
                getQtrs("224002", Quarters.Qtr3).doubleValue(),
                getQtrs("224002", Quarters.Qtr4).doubleValue(),
                mon101A.getTotal().doubleValue(), mon101A.getQtr1().doubleValue(), mon101A.getQtr2().doubleValue(),
                mon101A.getQtr3().doubleValue(), mon101A.getQtr4().doubleValue());
        suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode("224002"));
        suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode("224002"));

        tr++;
        titleBoldTotal.add((int) tr);
        listIncomeTotals.add(mon101A);
        listIncomeTotals4.add(mon101A);
        Row Q85 = createHeaderRow(sheet, tr, "", "Total passenger services expenses",
                suntotal.doubleValue(),
                getQtrs("224002", Quarters.Qtr1).doubleValue(),
                getQtrs("224002", Quarters.Qtr2).doubleValue(),
                getQtrs("224002", Quarters.Qtr3).doubleValue(),
                getQtrs("224002", Quarters.Qtr4).doubleValue(),
                mon101A.getTotal().doubleValue(), mon101A.getQtr1().doubleValue(), mon101A.getQtr2().doubleValue(),
                mon101A.getQtr3().doubleValue(), mon101A.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts2 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts2.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts2.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts2.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts2.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        listVariableCosts.add(VariableCosts2);
        suntotal = BigDecimal.ZERO;
        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q86 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("228", "Maintenance", "", "", "", "", "", "", "", "", "", "");
        Row Q87 = createHeaderRow(sheet, tr, titles);

        listCoas.clear();
        listCOACodes.clear();
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("228");
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
                suntotal = BigDecimal.ZERO;

            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T10 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T10);
        listIncomeTotals4.add(mon101T10);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q88 = createHeaderRow(sheet, tr, "", "Total Maintenance",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T10.getTotal().doubleValue(), mon101T10.getQtr1().doubleValue(), mon101T10.getQtr2().doubleValue(),
                mon101T10.getQtr3().doubleValue(), mon101T10.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts3 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts3.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts3.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts3.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts3.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        listVariableCosts.add(VariableCosts3);
        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q89 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("223", "Utility And Property Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q891 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "4"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22300", ss);
        ss.clear();
        ss.addAll(Arrays.asList("2", "3", "4"));
        List<URC_ACNT> findByAcntCodeStartingWith40 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("28150", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith40);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCOACodes.add(coa.getCode());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        MonthlySumResponseFreight mon101T11 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T11);
        listIncomeTotals4.add(mon101T11);

        Row Q90 = createHeaderRow(sheet, tr, "", "Total Property Expenses",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T11.getTotal().doubleValue(), mon101T11.getQtr1().doubleValue(), mon101T11.getQtr2().doubleValue(),
                mon101T11.getQtr3().doubleValue(), mon101T11.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts4 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts4.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts4.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts4.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts4.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        listVariableCosts.add(VariableCosts4);
        suntotal = BigDecimal.ZERO;
        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q91 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T11W = calculateTotal(listIncomeTotals4);
        MonthlySumResponseFreight varcosts = calculateTotal(listVariableCosts);
        List<Qtr> listqtr = new ArrayList<>();
        for (MonthlySumResponseFreight b : listVariableCosts) {
            Qtr aa = new Qtr(null, null, null, null, null, null, null, null, null, null);
            aa.setQtr1(b.getQtr1());
            aa.setQtr2(b.getQtr2());
            aa.setQtr3(b.getQtr3());
            aa.setQtr4(b.getQtr4());
            listqtr.add(aa);
        }
        Qtr thisQtr = sumQuarterTotals(listqtr);
        Row Q92 = createHeaderRow(sheet, tr, "", "Total variable costs",
                suntotalT.doubleValue(),
                thisQtr.getQtr1().doubleValue(),
                thisQtr.getQtr2().doubleValue(),
                thisQtr.getQtr3().doubleValue(),
                thisQtr.getQtr4().doubleValue(),
                mon101T11W.getTotal().doubleValue(), mon101T11W.getQtr1().doubleValue(), mon101T11W.getQtr2().doubleValue(),
                mon101T11W.getQtr3().doubleValue(), mon101T11W.getQtr4().doubleValue());
        thisQtr.setBqtr1(mon101T11W.getQtr1());
        thisQtr.setBqtr2(mon101T11W.getQtr2());
        thisQtr.setBqtr3(mon101T11W.getQtr3());
        thisQtr.setBqtr4(mon101T11W.getQtr4());
        thisQtr.setCbudget(mon101T11W.getTotal());
        thisQtr.setPbudget(BigDecimal.ZERO);
        List<Qtr> totalExpenditure = new ArrayList<>();
        totalExpenditure.add(thisQtr);
        listVariableCosts.clear();
        listVariableCosts.add(varcosts);
        BigDecimal VariableCost = suntotalT;

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "%age on Oper.Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q93 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldBlue.add((int) tr);
        titles = Arrays.asList("", "Operation Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q931Row = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("211", "Personel Costs", "", "", "", "", "", "", "", "", "", "");
        Row Q931Row1 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21110", ss);
        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "6"));
        List<URC_ACNT> findByAcntCodeStartingWith41 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21210", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith41);

        List<URC_ACNT> findByAcntCodeStartingWith42 = urcAcntService.findByAcntCode("212201");
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith42);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5"));
        List<URC_ACNT> findByAcntCodeStartingWith43 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("21300", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith43);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCOACodes.add(coa.getCode());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T12 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T12);
        listIncomeTotals3.add(mon101T12);//Total Personnel Cost
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q941 = createHeaderRow(sheet, tr, "", "Total Personnel Cost",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T12.getTotal().doubleValue(), mon101T12.getQtr1().doubleValue(), mon101T12.getQtr2().doubleValue(),
                mon101T12.getQtr3().doubleValue(), mon101T12.getQtr4().doubleValue());
        Qtr thisQtr2 = new Qtr(null, null, null, null, null, null, null, null, null, null);
        thisQtr2.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr2.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr2.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr2.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr2.setBqtr1(mon101T11W.getQtr1());
        thisQtr2.setBqtr2(mon101T11W.getQtr2());
        thisQtr2.setBqtr3(mon101T11W.getQtr3());
        thisQtr2.setBqtr4(mon101T11W.getQtr4());
        thisQtr2.setCbudget(mon101T11W.getTotal());
        thisQtr2.setPbudget(ptotal);
        totalExpenditure.add(thisQtr2);
        suntotal = BigDecimal.ZERO;
        BigDecimal totalPersonalCost = suntotal;
        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "%age on Oper.exp.", "", "", "", "", "", "", "", "", "", "");
        Row Q942 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "ADMINISTRATION EXPENSES", "", "", "", "", "", "", "", "", "", "");
        Row Q943 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("221", "General Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q94 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22100", ss);
        ss.clear();
        ss.addAll(Arrays.asList("0", "1", "2", "6", "7"));
        List<URC_ACNT> findByAcntCodeStartingWith60 = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22101", ss);
        ss.clear();
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith60);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCOACodes.add(coa.getCode());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T13 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T13);
        listIncomeTotals3.add(mon101T13);//Total Administration Expenses
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q95 = createHeaderRow(sheet, tr, "", "Total Administration Expenses",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T13.getTotal().doubleValue(), mon101T13.getQtr1().doubleValue(), mon101T13.getQtr2().doubleValue(),
                mon101T13.getQtr3().doubleValue(), mon101T13.getQtr4().doubleValue());

        Qtr thisQtr3 = new Qtr(null, null, null, null, null, null, null, null, null, null);
        thisQtr3.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr3.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr3.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr3.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr3.setBqtr1(mon101T13.getQtr1());
        thisQtr3.setBqtr2(mon101T13.getQtr2());
        thisQtr3.setBqtr3(mon101T13.getQtr3());
        thisQtr3.setBqtr4(mon101T13.getQtr4());
        thisQtr3.setCbudget(mon101T13.getTotal());
        thisQtr3.setPbudget(ptotal);
        totalExpenditure.add(thisQtr3);
        suntotal = BigDecimal.ZERO;
        BigDecimal totalAminCost = suntotal;

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("", "%age on Oper.income", "", "", "", "", "", "", "", "", "", "");
        Row Q96 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "OTHER ADMINISTRATION EXPENSES", "", "", "", "", "", "", "", "", "", "");
        Row Q97 = createHeaderRow(sheet, tr, titles);

        tr++;
        titleBoldRed.add((int) tr);
        titles = Arrays.asList("221", "Board and Legal Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q98 = createHeaderRow(sheet, tr, titles);

        tt = BigDecimal.ZERO;
        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("221019");
        ss.clear();
        List<URC_ACNT> findByAcntCodeStartingWith601 = urcAcntService.findByAcntCode("221020");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith601);
        List<URC_ACNT> findByAcntCodeStartingWith602 = urcAcntService.findByAcntCode("221008");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith602);
        listCoas.clear();
        listCOACodes.clear();
        listCOACodesTT.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            listCOACodes.add(coa.getCode());
            listCOACodesTT.add(coa.getCode().trim());
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }
            suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                    getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T14 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T14);
        listIncomeTotals2.add(mon101T14);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q99 = createHeaderRow(sheet, tr, "", "Total Board & Legal Expenses",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T14.getTotal().doubleValue(), mon101T14.getQtr1().doubleValue(),
                mon101T14.getQtr2().doubleValue(), mon101T14.getQtr3().doubleValue(), mon101T14.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("222");
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                listCOACodesTT.add(coa.getCode().trim());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T15 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T15);
        listIncomeTotals2.add(mon101T15);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q100 = createHeaderRow(sheet, tr, "", "Total Communications",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T15.getTotal().doubleValue(), mon101T15.getQtr1().doubleValue(),
                mon101T15.getQtr2().doubleValue(), mon101T15.getQtr3().doubleValue(), mon101T15.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("223", "Utilities", "", "", "", "", "", "", "", "", "", "");
        Row Q101 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("5", "6", "7", "8"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22300", ss);
        ss.clear();
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                listCOACodesTT.add(coa.getCode().trim());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T16 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T16);
        listIncomeTotals2.add(mon101T16);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q102 = createHeaderRow(sheet, tr, "", "Total Utility Expenses",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T16.getTotal().doubleValue(), mon101T16.getQtr1().doubleValue(),
                mon101T16.getQtr2().doubleValue(), mon101T16.getQtr3().doubleValue(), mon101T16.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("224", "Supplies and Services", "", "", "", "", "", "", "", "", "", "");
        Row Q1021 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("3", "4", "5", "6"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22400", ss);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                listCOACodesTT.add(coa.getCode().trim());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T17 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T17);
        listIncomeTotals2.add(mon101T17);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q103 = createHeaderRow(sheet, tr, "", "Total Supplies and Services",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T17.getTotal().doubleValue(), mon101T17.getQtr1().doubleValue(), mon101T17.getQtr2().doubleValue(),
                mon101T17.getQtr3().doubleValue(), mon101T17.getQtr4().doubleValue()
        );
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Professional Services", "", "", "", "", "", "", "", "", "", "");
        Row Q104 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22500", ss);
        ss.clear();
        ss.add("3");
        findByAcntCodeStartingWith.addAll(urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("24200", ss));
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                listCOACodesTT.add(coa.getCode().trim());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T18 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T18);
        listIncomeTotals2.add(mon101T18);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }

        Row Q105 = createHeaderRow(sheet, tr, "", "Total Professional Services",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T18.getTotal().doubleValue(), mon101T18.getQtr1().doubleValue(), mon101T18.getQtr2().doubleValue(),
                mon101T18.getQtr3().doubleValue(), mon101T18.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("226", "Insurances and Licenses", "", "", "", "", "", "", "", "", "", "");
        Row Q106 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22600", ss);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                listCOACodesTT.add(coa.getCode().trim());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T19 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T19);
        listIncomeTotals2.add(mon101T19);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q107 = createHeaderRow(sheet, tr, "", "Total Insurances and Licenses",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T19.getTotal().doubleValue(), mon101T19.getQtr1().doubleValue(), mon101T19.getQtr2().doubleValue(),
                mon101T19.getQtr3().doubleValue(), mon101T19.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("227", "Travel and Transport", "", "", "", "", "", "", "", "", "", "");
        Row Q108 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22700", ss);
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                listCOACodesTT.add(coa.getCode().trim());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T191 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T191);
        listIncomeTotals2.add(mon101T191);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q109 = createHeaderRow(sheet, tr, "", "Total Travel and Transport",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T191.getTotal().doubleValue(), mon101T191.getQtr1().doubleValue(), mon101T191.getQtr2().doubleValue(),
                mon101T191.getQtr3().doubleValue(), mon101T191.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("282", "Miscellaneous Other Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q110 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("28210", ss);
        ss.clear();
        listCoas.clear();
        listCOACodes.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                listCOACodes.add(coa.getCode());
                listCOACodesTT.add(coa.getCode().trim());
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                tt = tt.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T1912 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals2.add(mon101T1912);
        listIncomeTotals.add(mon101T1912);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q111 = createHeaderRow(sheet, tr, "", "Total Miscellaneous Other Expenses",
                suntotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T1912.getTotal().doubleValue(), mon101T1912.getQtr1().doubleValue(),
                mon101T1912.getQtr2().doubleValue(), mon101T1912.getQtr3().doubleValue(), mon101T1912.getQtr4().doubleValue());
        suntotal = BigDecimal.ZERO;

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal10 = calculateTotal(listIncomeTotals2);
        listIncomeTotals3.add(calculateTotal10);//Total Other Administration Expenses

        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        BigDecimal totalOtherAminCost = suntotalT.subtract(VariableCost)
                .subtract(totalPersonalCost)
                .subtract(totalAminCost);
        Row Q112 = createHeaderRow(sheet, tr, "", "Total Other Administration Expenses",
                tt.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                calculateTotal10.getTotal().doubleValue(), calculateTotal10.getQtr1().doubleValue(),
                calculateTotal10.getQtr2().doubleValue(), calculateTotal10.getQtr3().doubleValue(), calculateTotal10.getQtr4().doubleValue());

        Qtr thisQtr4 = new Qtr(null, null, null, null, null, null, null, null, null, null);
        thisQtr4.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr1, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr4.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr2, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr4.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr3, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr4.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr4, getPreviousFy(comboBox.getValue().getFinancialYear())));
        thisQtr4.setBqtr1(calculateTotal10.getQtr1());
        thisQtr4.setBqtr2(calculateTotal10.getQtr2());
        thisQtr4.setBqtr3(calculateTotal10.getQtr3());
        thisQtr4.setBqtr4(calculateTotal10.getQtr4());
        thisQtr4.setCbudget(calculateTotal10.getTotal());
        thisQtr4.setPbudget(ptotal);
        totalExpenditure.add(thisQtr4);
        listIncomeTotals2.clear();
        tr++;
        titleBoldRed.add((int) tr);
        Row Q113 = sheet.createRow((short) tr);
        Q113.createCell((short) 0).setCellValue("");
        Q113.createCell((short) 1).setCellValue("%age on Oper.exp.");
        Q113.createCell((short) 2).setCellValue("");
        Q113.createCell((short) 3).setCellValue("");
        Q113.createCell((short) 4).setCellValue("");
        Q113.createCell((short) 5).setCellValue("");
        Q113.createCell((short) 6).setCellValue("");
        Q113.createCell((short) 7).setCellValue("");
        Q113.createCell((short) 8).setCellValue("");
        Q113.createCell((short) 9).setCellValue("");
        Q113.createCell((short) 10).setCellValue("");
        Q113.createCell((short) 11).setCellValue("");
        tr++;
        titleBoldTotal.add((int) tr);
        Row Q114 = sheet.createRow((short) tr);
        Qtr totalE = sumQuarterTotals(totalExpenditure);
        Q114.createCell((short) 0).setCellValue("");
        Q114.createCell((short) 1).setCellValue("Total Operating Expenses");
        Q114.createCell((short) 2).setCellValue(suntotalT.doubleValue());
        Q114.createCell((short) 3).setCellValue(totalE.getQtr1().doubleValue());
        Q114.createCell((short) 4).setCellValue(totalE.getQtr2().doubleValue());
        Q114.createCell((short) 5).setCellValue(totalE.getQtr3().doubleValue());
        Q114.createCell((short) 6).setCellValue(totalE.getQtr4().doubleValue());
        Q114.createCell((short) 7).setCellValue(totalE.getCbudget().doubleValue());
        Q114.createCell((short) 8).setCellValue(totalE.getBqtr1().doubleValue());
        Q114.createCell((short) 9).setCellValue(totalE.getBqtr2().doubleValue());
        Q114.createCell((short) 10).setCellValue(totalE.getBqtr3().doubleValue());
        Q114.createCell((short) 11).setCellValue(totalE.getBqtr4().doubleValue());
        tr++;
        titleBoldRed.add((int) tr);
        Row Q115 = sheet.createRow((short) tr);
        Q115.createCell((short) 0).setCellValue("");
        Q115.createCell((short) 1).setCellValue("%age on Oper.income");
        Q115.createCell((short) 2).setCellValue("");
        Q115.createCell((short) 3).setCellValue("");
        Q115.createCell((short) 4).setCellValue("");
        Q115.createCell((short) 5).setCellValue("");
        Q115.createCell((short) 6).setCellValue("");
        Q115.createCell((short) 7).setCellValue("");
        Q115.createCell((short) 8).setCellValue("");
        Q115.createCell((short) 9).setCellValue("");
        Q115.createCell((short) 10).setCellValue("");
        Q115.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q116 = sheet.createRow((short) tr);
        Q116.createCell((short) 0).setCellValue("");
        Q116.createCell((short) 1).setCellValue("Total Non-Wage");
        Q116.createCell((short) 2).setCellValue("");
        Q116.createCell((short) 3).setCellValue("");
        Q116.createCell((short) 4).setCellValue("");
        Q116.createCell((short) 5).setCellValue("");
        Q116.createCell((short) 6).setCellValue("");
        Q116.createCell((short) 7).setCellValue("");
        Q116.createCell((short) 8).setCellValue("");
        Q116.createCell((short) 9).setCellValue("");
        Q116.createCell((short) 10).setCellValue("");
        Q116.createCell((short) 11).setCellValue("");
        tr++;
        Row Q117 = sheet.createRow((short) tr);
        Q117.createCell((short) 0).setCellValue("");
        Q117.createCell((short) 1).setCellValue("%age on Oper.exp");
        Q117.createCell((short) 2).setCellValue("");
        Q117.createCell((short) 3).setCellValue("");
        Q117.createCell((short) 4).setCellValue("");
        Q117.createCell((short) 5).setCellValue("");
        Q117.createCell((short) 6).setCellValue("");
        Q117.createCell((short) 7).setCellValue("");
        Q117.createCell((short) 8).setCellValue("");
        Q117.createCell((short) 9).setCellValue("");
        Q117.createCell((short) 10).setCellValue("");
        Q117.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q118 = sheet.createRow((short) tr);
        Q118.createCell((short) 0).setCellValue("");
        Q118.createCell((short) 1).setCellValue("EBITDA/Operating Suplus/(Deficit) ");
        Q118.createCell((short) 2).setCellValue("");
        Q118.createCell((short) 3).setCellValue("");
        Q118.createCell((short) 4).setCellValue("");
        Q118.createCell((short) 5).setCellValue("");
        Q118.createCell((short) 6).setCellValue("");
        Q118.createCell((short) 7).setCellValue("");
        Q118.createCell((short) 8).setCellValue("");
        Q118.createCell((short) 9).setCellValue("");
        Q118.createCell((short) 10).setCellValue("");
        Q118.createCell((short) 11).setCellValue("");
        tr++;
        Row Q119 = sheet.createRow((short) tr);
        Q119.createCell((short) 0).setCellValue("");
        Q119.createCell((short) 1).setCellValue("%age on Oper.income");
        Q119.createCell((short) 2).setCellValue("");
        Q119.createCell((short) 3).setCellValue("");
        Q119.createCell((short) 4).setCellValue("");
        Q119.createCell((short) 5).setCellValue("");
        Q119.createCell((short) 6).setCellValue("");
        Q119.createCell((short) 7).setCellValue("");
        Q119.createCell((short) 8).setCellValue("");
        Q119.createCell((short) 9).setCellValue("");
        Q119.createCell((short) 10).setCellValue("");
        Q119.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q120 = sheet.createRow((short) tr);
        Q120.createCell((short) 0).setCellValue("");
        Q120.createCell((short) 1).setCellValue("CONSUMPTION OF FIXED ASSETS");
        Q120.createCell((short) 2).setCellValue("");
        Q120.createCell((short) 3).setCellValue("");
        Q120.createCell((short) 4).setCellValue("");
        Q120.createCell((short) 5).setCellValue("");
        Q120.createCell((short) 6).setCellValue("");
        Q120.createCell((short) 7).setCellValue("");
        Q120.createCell((short) 8).setCellValue("");
        Q120.createCell((short) 9).setCellValue("");
        Q120.createCell((short) 10).setCellValue("");
        Q120.createCell((short) 11).setCellValue("");

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("231");
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal101 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal101);//Total Other Administration Expenses
        Row Q121 = createHeaderRow(sheet, tr, "", "Total Depreciation",
                suntotal.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                calculateTotal101.getTotal().doubleValue(), calculateTotal101.getQtr1().doubleValue(),
                calculateTotal101.getQtr2().doubleValue(), calculateTotal101.getQtr3().doubleValue(), calculateTotal101.getQtr4().doubleValue());
        listIncomeTotals5.clear();
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        Row Q122 = sheet.createRow((short) tr);
        Q122.createCell((short) 0).setCellValue("");
        Q122.createCell((short) 1).setCellValue("Armotisation");
        Q122.createCell((short) 2).setCellValue("");
        Q122.createCell((short) 3).setCellValue("");
        Q122.createCell((short) 4).setCellValue("");
        Q122.createCell((short) 5).setCellValue("");
        Q122.createCell((short) 6).setCellValue("");
        Q122.createCell((short) 7).setCellValue("");
        Q122.createCell((short) 8).setCellValue("");
        Q122.createCell((short) 9).setCellValue("");
        Q122.createCell((short) 10).setCellValue("");
        Q122.createCell((short) 11).setCellValue("");
        tr++;
        Row Q123 = sheet.createRow((short) tr);
        Q123.createCell((short) 0).setCellValue("");
        Q123.createCell((short) 1).setCellValue("Software");
        Q123.createCell((short) 2).setCellValue("");
        Q123.createCell((short) 3).setCellValue("");
        Q123.createCell((short) 4).setCellValue("");
        Q123.createCell((short) 5).setCellValue("");
        Q123.createCell((short) 6).setCellValue("");
        Q123.createCell((short) 7).setCellValue("");
        Q123.createCell((short) 8).setCellValue("");
        Q123.createCell((short) 9).setCellValue("");
        Q123.createCell((short) 10).setCellValue("");
        Q123.createCell((short) 11).setCellValue("");
        tr++;
        Row Q124 = sheet.createRow((short) tr);
        Q124.createCell((short) 0).setCellValue("");
        Q124.createCell((short) 1).setCellValue("Other");
        Q124.createCell((short) 2).setCellValue("");
        Q124.createCell((short) 3).setCellValue("");
        Q124.createCell((short) 4).setCellValue("");
        Q124.createCell((short) 5).setCellValue("");
        Q124.createCell((short) 6).setCellValue("");
        Q124.createCell((short) 7).setCellValue("");
        Q124.createCell((short) 8).setCellValue("");
        Q124.createCell((short) 9).setCellValue("");
        Q124.createCell((short) 10).setCellValue("");
        Q124.createCell((short) 11).setCellValue("");
        tr++;
        Row Q125 = sheet.createRow((short) tr);
        titleJustBold.add((int) tr);
        Q125.createCell((short) 0).setCellValue("");
        Q125.createCell((short) 1).setCellValue("Total Armotisation");
        Q125.createCell((short) 2).setCellValue("");
        Q125.createCell((short) 3).setCellValue("");
        Q125.createCell((short) 4).setCellValue("");
        Q125.createCell((short) 5).setCellValue("");
        Q125.createCell((short) 6).setCellValue("");
        Q125.createCell((short) 7).setCellValue("");
        Q125.createCell((short) 8).setCellValue("");
        Q125.createCell((short) 9).setCellValue("");
        Q125.createCell((short) 10).setCellValue("");
        Q125.createCell((short) 11).setCellValue("");
        tr++;
        titleBoldTotal.add((int) tr);
        Row Q126 = sheet.createRow((short) tr);
        Q126.createCell((short) 0).setCellValue("");
        Q126.createCell((short) 1).setCellValue("Total Depreciation & Armotisation");
        Q126.createCell((short) 2).setCellValue("");
        Q126.createCell((short) 3).setCellValue("");
        Q126.createCell((short) 4).setCellValue("");
        Q126.createCell((short) 5).setCellValue("");
        Q126.createCell((short) 6).setCellValue("");
        Q126.createCell((short) 7).setCellValue("");
        Q126.createCell((short) 8).setCellValue("");
        Q126.createCell((short) 9).setCellValue("");
        Q126.createCell((short) 10).setCellValue("");
        Q126.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q127 = sheet.createRow((short) tr);
        Q127.createCell((short) 0).setCellValue("221");
        Q127.createCell((short) 1).setCellValue("Finance Costs");
        Q127.createCell((short) 2).setCellValue("");
        Q127.createCell((short) 3).setCellValue("");
        Q127.createCell((short) 4).setCellValue("");
        Q127.createCell((short) 5).setCellValue("");
        Q127.createCell((short) 6).setCellValue("");
        Q127.createCell((short) 7).setCellValue("");
        Q127.createCell((short) 8).setCellValue("");
        Q127.createCell((short) 9).setCellValue("");
        Q127.createCell((short) 10).setCellValue("");
        Q127.createCell((short) 11).setCellValue("");

        ss.addAll(Arrays.asList("4", "8"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22101", ss);
        ss.clear();
        listCoas.clear();
        listIncomeTotals5.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal1011 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1011);//Total Other Administration Expenses
        Row Q128 = createHeaderRow(sheet, tr, "", "Total Finance Cost",
                suntotal.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                calculateTotal1011.getTotal().doubleValue(), calculateTotal1011.getQtr1().doubleValue(),
                calculateTotal1011.getQtr2().doubleValue(), calculateTotal1011.getQtr3().doubleValue(), calculateTotal1011.getQtr4().doubleValue()
        );
        listIncomeTotals5.clear();
        suntotal = BigDecimal.ZERO;

        tr++;
        titleBoldTotal.add((int) tr);
        Row Q129 = sheet.createRow((short) tr);
        Q129.createCell((short) 0).setCellValue("");
        Q129.createCell((short) 1).setCellValue("Total Expenditure");
        Q129.createCell((short) 2).setCellValue(suntotalT.doubleValue());
        Q129.createCell((short) 3).setCellValue("");
        Q129.createCell((short) 4).setCellValue("");
        Q129.createCell((short) 5).setCellValue("");
        Q129.createCell((short) 6).setCellValue("");
        Q129.createCell((short) 7).setCellValue("");
        Q129.createCell((short) 8).setCellValue("");
        Q129.createCell((short) 9).setCellValue("");
        Q129.createCell((short) 10).setCellValue("");
        Q129.createCell((short) 11).setCellValue("");
        tr++;
        Row Q130 = sheet.createRow((short) tr);
        Q130.createCell((short) 0).setCellValue("");
        Q130.createCell((short) 1).setCellValue("");
        Q130.createCell((short) 2).setCellValue("");
        Q130.createCell((short) 3).setCellValue("");
        Q130.createCell((short) 4).setCellValue("");
        Q130.createCell((short) 5).setCellValue("");
        Q130.createCell((short) 6).setCellValue("");
        Q130.createCell((short) 7).setCellValue("");
        Q130.createCell((short) 8).setCellValue("");
        Q130.createCell((short) 9).setCellValue("");
        Q130.createCell((short) 10).setCellValue("");
        Q130.createCell((short) 11).setCellValue("");
        tr++;
        Row Q131 = sheet.createRow((short) tr);
        Q131.createCell((short) 0).setCellValue("");
        Q131.createCell((short) 1).setCellValue("EBT");
        Q131.createCell((short) 2).setCellValue("");
        Q131.createCell((short) 3).setCellValue("");
        Q131.createCell((short) 4).setCellValue("");
        Q131.createCell((short) 5).setCellValue("");
        Q131.createCell((short) 6).setCellValue("");
        Q131.createCell((short) 7).setCellValue("");
        Q131.createCell((short) 8).setCellValue("");
        Q131.createCell((short) 9).setCellValue("");
        Q131.createCell((short) 10).setCellValue("");
        Q131.createCell((short) 11).setCellValue("");
        tr++;
        titleBoldRed.add((int) tr);
        Row Q132 = sheet.createRow((short) tr);
        Q132.createCell((short) 0).setCellValue("");
        Q132.createCell((short) 1).setCellValue("%age on Oper.income");
        Q132.createCell((short) 2).setCellValue("");
        Q132.createCell((short) 3).setCellValue("");
        Q132.createCell((short) 4).setCellValue("");
        Q132.createCell((short) 5).setCellValue("");
        Q132.createCell((short) 6).setCellValue("");
        Q132.createCell((short) 7).setCellValue("");
        Q132.createCell((short) 8).setCellValue("");
        Q132.createCell((short) 9).setCellValue("");
        Q132.createCell((short) 10).setCellValue("");
        Q132.createCell((short) 11).setCellValue("");
        tr++;
        Row Q133 = sheet.createRow((short) tr);
        Q133.createCell((short) 0).setCellValue("");
        Q133.createCell((short) 1).setCellValue("Tax (Provision)");
        Q133.createCell((short) 2).setCellValue("");
        Q133.createCell((short) 3).setCellValue("");
        Q133.createCell((short) 4).setCellValue("");
        Q133.createCell((short) 5).setCellValue("");
        Q133.createCell((short) 6).setCellValue("");
        Q133.createCell((short) 7).setCellValue("");
        Q133.createCell((short) 8).setCellValue("");
        Q133.createCell((short) 9).setCellValue("");
        Q133.createCell((short) 10).setCellValue("");
        Q133.createCell((short) 11).setCellValue("");
        tr++;
        Row Q134 = sheet.createRow((short) tr);
        Q134.createCell((short) 0).setCellValue("");
        Q134.createCell((short) 1).setCellValue("EAT");
        Q134.createCell((short) 2).setCellValue("");
        Q134.createCell((short) 3).setCellValue("");
        Q134.createCell((short) 4).setCellValue("");
        Q134.createCell((short) 5).setCellValue("");
        Q134.createCell((short) 6).setCellValue("");
        Q134.createCell((short) 7).setCellValue("");
        Q134.createCell((short) 8).setCellValue("");
        Q134.createCell((short) 9).setCellValue("");
        Q134.createCell((short) 10).setCellValue("");
        Q134.createCell((short) 11).setCellValue("");
        tr++;
        Row Q135 = sheet.createRow((short) tr);
        Q135.createCell((short) 0).setCellValue("");
        Q135.createCell((short) 1).setCellValue("Add Non-Operating/non-recurrent Income");
        Q135.createCell((short) 2).setCellValue("");
        Q135.createCell((short) 3).setCellValue("");
        Q135.createCell((short) 4).setCellValue("");
        Q135.createCell((short) 5).setCellValue("");
        Q135.createCell((short) 6).setCellValue("");
        Q135.createCell((short) 7).setCellValue("");
        Q135.createCell((short) 8).setCellValue("");
        Q135.createCell((short) 9).setCellValue("");
        Q135.createCell((short) 10).setCellValue("");
        Q135.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q136 = sheet.createRow((short) tr);
        Q136.createCell((short) 0).setCellValue("");
        Q136.createCell((short) 1).setCellValue("Total Surplus/(Deficit)");
        Q136.createCell((short) 2).setCellValue("");
        Q136.createCell((short) 3).setCellValue("");
        Q136.createCell((short) 4).setCellValue("");
        Q136.createCell((short) 5).setCellValue("");
        Q136.createCell((short) 6).setCellValue("");
        Q136.createCell((short) 7).setCellValue("");
        Q136.createCell((short) 8).setCellValue("");
        Q136.createCell((short) 9).setCellValue("");
        Q136.createCell((short) 10).setCellValue("");
        Q136.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q137 = sheet.createRow((short) tr);
        Q137.createCell((short) 0).setCellValue("");
        Q137.createCell((short) 1).setCellValue("Grand Total Operating Expenses");
        Q137.createCell((short) 2).setCellValue("");
        Q137.createCell((short) 3).setCellValue("");
        Q137.createCell((short) 4).setCellValue("");
        Q137.createCell((short) 5).setCellValue("");
        Q137.createCell((short) 6).setCellValue("");
        Q137.createCell((short) 7).setCellValue("");
        Q137.createCell((short) 8).setCellValue("");
        Q137.createCell((short) 9).setCellValue("");
        Q137.createCell((short) 10).setCellValue("");
        Q137.createCell((short) 11).setCellValue("");
        tr++;
        Row Q138 = sheet.createRow((short) tr);
        Q138.createCell((short) 0).setCellValue("");
        Q138.createCell((short) 1).setCellValue("Operation Ratio");
        Q138.createCell((short) 2).setCellValue("");
        Q138.createCell((short) 3).setCellValue("");
        Q138.createCell((short) 4).setCellValue("");
        Q138.createCell((short) 5).setCellValue("");
        Q138.createCell((short) 6).setCellValue("");
        Q138.createCell((short) 7).setCellValue("");
        Q138.createCell((short) 8).setCellValue("");
        Q138.createCell((short) 9).setCellValue("");
        Q138.createCell((short) 10).setCellValue("");
        Q138.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q139 = sheet.createRow((short) tr);
        Q139.createCell((short) 0).setCellValue("");
        Q139.createCell((short) 1).setCellValue("FIXED ASSETS");
        Q139.createCell((short) 2).setCellValue("");
        Q139.createCell((short) 3).setCellValue("");
        Q139.createCell((short) 4).setCellValue("");
        Q139.createCell((short) 5).setCellValue("");
        Q139.createCell((short) 6).setCellValue("");
        Q139.createCell((short) 7).setCellValue("");
        Q139.createCell((short) 8).setCellValue("");
        Q139.createCell((short) 9).setCellValue("");
        Q139.createCell((short) 10).setCellValue("");
        Q139.createCell((short) 11).setCellValue("");

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("311");
        findByAcntCodeStartingWith.addAll(urcAcntService.findByAcntCodeStartingWith("312"));
        listCoas.clear();
        listIncomeTotals5.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }
                suntotal = suntotal.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                suntotalT = suntotalT.add(sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()));
                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        sampleUrcBSalfldgService.findTotalAmountByAccntCode(k.getAcntCode().trim()).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr1).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr2).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr3).doubleValue(),
                        getQtrs(coa.getCode(), Quarters.Qtr4).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }
        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight calculateTotal1012 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1012);//Total Other Administration Expenses
        Row Q140 = createHeaderRow(sheet, tr, "", "Total Fixed Assets",
                suntotal.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                calculateTotal1012.getTotal().doubleValue(), calculateTotal1012.getQtr1().doubleValue(),
                calculateTotal1012.getQtr2().doubleValue(), calculateTotal1012.getQtr3().doubleValue(), calculateTotal1012.getQtr4().doubleValue());
        listIncomeTotals5.clear();
        suntotal = BigDecimal.ZERO;

        tr++;
        titleJustBold.add((int) tr);
        Row Q141 = sheet.createRow((short) tr);
        Q141.createCell((short) 0).setCellValue("");
        Q141.createCell((short) 1).setCellValue("");
        Q141.createCell((short) 2).setCellValue("");
        Q141.createCell((short) 3).setCellValue("");
        Q141.createCell((short) 4).setCellValue("");
        Q141.createCell((short) 5).setCellValue("");
        Q141.createCell((short) 6).setCellValue("");
        Q141.createCell((short) 7).setCellValue("");
        Q141.createCell((short) 8).setCellValue("");
        Q141.createCell((short) 9).setCellValue("");
        Q141.createCell((short) 10).setCellValue("");
        Q141.createCell((short) 11).setCellValue("");

        tr++;
        titleJustBold.add((int) tr);
        Row Q142 = sheet.createRow((short) tr);
        Q142.createCell((short) 0).setCellValue("");
        Q142.createCell((short) 1).setCellValue("SUMMARY");
        Q142.createCell((short) 2).setCellValue("");
        Q142.createCell((short) 3).setCellValue("");
        Q142.createCell((short) 4).setCellValue("");
        Q142.createCell((short) 5).setCellValue("");
        Q142.createCell((short) 6).setCellValue("");
        Q142.createCell((short) 7).setCellValue("");
        Q142.createCell((short) 8).setCellValue("");
        Q142.createCell((short) 9).setCellValue("");
        Q142.createCell((short) 10).setCellValue("");
        Q142.createCell((short) 11).setCellValue("");
        tr++;
        titleBoldRed.add((int) tr);
        Row Q143 = sheet.createRow((short) tr);
        Q143.createCell((short) 0).setCellValue("");
        Q143.createCell((short) 1).setCellValue("INCOME");
        Q143.createCell((short) 2).setCellValue("");
        Q143.createCell((short) 3).setCellValue("");
        Q143.createCell((short) 4).setCellValue("");
        Q143.createCell((short) 5).setCellValue("");
        Q143.createCell((short) 6).setCellValue("");
        Q143.createCell((short) 7).setCellValue("");
        Q143.createCell((short) 8).setCellValue("");
        Q143.createCell((short) 9).setCellValue("");
        Q143.createCell((short) 10).setCellValue("");
        Q143.createCell((short) 11).setCellValue("");
        tr++;
        titleJustBold.add((int) tr);
        Row Q144 = sheet.createRow((short) tr);
        Q144.createCell((short) 0).setCellValue("");
        Q144.createCell((short) 1).setCellValue("Operating");
        Q144.createCell((short) 2).setCellValue("");
        Q144.createCell((short) 3).setCellValue("");
        Q144.createCell((short) 4).setCellValue("");
        Q144.createCell((short) 5).setCellValue("");
        Q144.createCell((short) 6).setCellValue("");
        Q144.createCell((short) 7).setCellValue("");
        Q144.createCell((short) 8).setCellValue("");
        Q144.createCell((short) 9).setCellValue("");
        Q144.createCell((short) 10).setCellValue("");
        Q144.createCell((short) 11).setCellValue("");
        tr++;
        Row Q145 = sheet.createRow((short) tr);
        Q145.createCell((short) 0).setCellValue("");
        Q145.createCell((short) 1).setCellValue("Assets hire");
        Q145.createCell((short) 2).setCellValue("");
        Q145.createCell((short) 3).setCellValue("");
        Q145.createCell((short) 4).setCellValue("");
        Q145.createCell((short) 5).setCellValue("");
        Q145.createCell((short) 6).setCellValue("");
        Q145.createCell((short) 7).setCellValue("");
        Q145.createCell((short) 8).setCellValue("");
        Q145.createCell((short) 9).setCellValue("");
        Q145.createCell((short) 10).setCellValue("");
        Q145.createCell((short) 11).setCellValue("");
        tr++;
        Row Q146 = sheet.createRow((short) tr);
        Q146.createCell((short) 0).setCellValue("");
        Q146.createCell((short) 1).setCellValue("Freight");
        Q146.createCell((short) 2).setCellValue("");
        Q146.createCell((short) 3).setCellValue("");
        Q146.createCell((short) 4).setCellValue("");
        Q146.createCell((short) 5).setCellValue("");
        Q146.createCell((short) 6).setCellValue("");
        Q146.createCell((short) 7).setCellValue("");
        Q146.createCell((short) 8).setCellValue("");
        Q146.createCell((short) 9).setCellValue("");
        Q146.createCell((short) 10).setCellValue("");
        Q146.createCell((short) 11).setCellValue("");
        tr++;
        Row Q147 = sheet.createRow((short) tr);
        Q147.createCell((short) 0).setCellValue("");
        Q147.createCell((short) 1).setCellValue("Rent");
        Q147.createCell((short) 2).setCellValue("");
        Q147.createCell((short) 3).setCellValue("");
        Q147.createCell((short) 4).setCellValue("");
        Q147.createCell((short) 5).setCellValue("");
        Q147.createCell((short) 6).setCellValue("");
        Q147.createCell((short) 7).setCellValue("");
        Q147.createCell((short) 8).setCellValue("");
        Q147.createCell((short) 9).setCellValue("");
        Q147.createCell((short) 10).setCellValue("");
        Q147.createCell((short) 11).setCellValue("");
        tr++;
        Row Q148 = sheet.createRow((short) tr);
        Q148.createCell((short) 0).setCellValue("");
        Q148.createCell((short) 1).setCellValue("Passenger services");
        Q148.createCell((short) 2).setCellValue("");
        Q148.createCell((short) 3).setCellValue("");
        Q148.createCell((short) 4).setCellValue("");
        Q148.createCell((short) 5).setCellValue("");
        Q148.createCell((short) 6).setCellValue("");
        Q148.createCell((short) 7).setCellValue("");
        Q148.createCell((short) 8).setCellValue("");
        Q148.createCell((short) 9).setCellValue("");
        Q148.createCell((short) 10).setCellValue("");
        Q148.createCell((short) 11).setCellValue("");
        tr++;
        Row Q149 = sheet.createRow((short) tr);
        Q149.createCell((short) 0).setCellValue("");
        Q149.createCell((short) 1).setCellValue("Other fees & charges");
        Q149.createCell((short) 2).setCellValue("");
        Q149.createCell((short) 3).setCellValue("");
        Q149.createCell((short) 4).setCellValue("");
        Q149.createCell((short) 5).setCellValue("");
        Q149.createCell((short) 6).setCellValue("");
        Q149.createCell((short) 7).setCellValue("");
        Q149.createCell((short) 8).setCellValue("");
        Q149.createCell((short) 9).setCellValue("");
        Q149.createCell((short) 10).setCellValue("");
        Q149.createCell((short) 11).setCellValue("");
        tr++;
        Row Q150 = sheet.createRow((short) tr);
        Q150.createCell((short) 0).setCellValue("");
        Q150.createCell((short) 1).setCellValue("Miscellaneous income");
        Q150.createCell((short) 2).setCellValue("");
        Q150.createCell((short) 3).setCellValue("");
        Q150.createCell((short) 4).setCellValue("");
        Q150.createCell((short) 5).setCellValue("");
        Q150.createCell((short) 6).setCellValue("");
        Q150.createCell((short) 7).setCellValue("");
        Q150.createCell((short) 8).setCellValue("");
        Q150.createCell((short) 9).setCellValue("");
        Q150.createCell((short) 10).setCellValue("");
        Q150.createCell((short) 11).setCellValue("");
        tr++;
        Row Q151 = sheet.createRow((short) tr);
        Q151.createCell((short) 0).setCellValue("");
        Q151.createCell((short) 1).setCellValue("Income from disposal of obsolete/idle assets");
        Q151.createCell((short) 2).setCellValue("");
        Q151.createCell((short) 3).setCellValue("");
        Q151.createCell((short) 4).setCellValue("");
        Q151.createCell((short) 5).setCellValue("");
        Q151.createCell((short) 6).setCellValue("");
        Q151.createCell((short) 7).setCellValue("");
        Q151.createCell((short) 8).setCellValue("");
        Q151.createCell((short) 9).setCellValue("");
        Q151.createCell((short) 10).setCellValue("");
        Q151.createCell((short) 11).setCellValue("");
        tr++;
        Row Q152 = sheet.createRow((short) tr);
        Q152.createCell((short) 0).setCellValue("");
        Q152.createCell((short) 1).setCellValue("Total operating Income");
        Q152.createCell((short) 2).setCellValue("");
        Q152.createCell((short) 3).setCellValue("");
        Q152.createCell((short) 4).setCellValue("");
        Q152.createCell((short) 5).setCellValue("");
        Q152.createCell((short) 6).setCellValue("");
        Q152.createCell((short) 7).setCellValue("");
        Q152.createCell((short) 8).setCellValue("");
        Q152.createCell((short) 9).setCellValue("");
        Q152.createCell((short) 10).setCellValue("");
        Q152.createCell((short) 11).setCellValue("");
        tr++;
        Row Q153 = sheet.createRow((short) tr);
        Q153.createCell((short) 0).setCellValue("");
        Q153.createCell((short) 1).setCellValue("Exceptional Income");
        Q153.createCell((short) 2).setCellValue("");
        Q153.createCell((short) 3).setCellValue("");
        Q153.createCell((short) 4).setCellValue("");
        Q153.createCell((short) 5).setCellValue("");
        Q153.createCell((short) 6).setCellValue("");
        Q153.createCell((short) 7).setCellValue("");
        Q153.createCell((short) 8).setCellValue("");
        Q153.createCell((short) 9).setCellValue("");
        Q153.createCell((short) 10).setCellValue("");
        Q153.createCell((short) 11).setCellValue("");
        tr++;
        Row Q154 = sheet.createRow((short) tr);
        Q154.createCell((short) 0).setCellValue("");
        Q154.createCell((short) 1).setCellValue("UNRA");
        Q154.createCell((short) 2).setCellValue("");
        Q154.createCell((short) 3).setCellValue("");
        Q154.createCell((short) 4).setCellValue("");
        Q154.createCell((short) 5).setCellValue("");
        Q154.createCell((short) 6).setCellValue("");
        Q154.createCell((short) 7).setCellValue("");
        Q154.createCell((short) 8).setCellValue("");
        Q154.createCell((short) 9).setCellValue("");
        Q154.createCell((short) 10).setCellValue("");
        Q154.createCell((short) 11).setCellValue("");
        tr++;
        Row Q155 = sheet.createRow((short) tr);
        Q155.createCell((short) 0).setCellValue("");
        Q155.createCell((short) 1).setCellValue("MoFPED");
        Q155.createCell((short) 2).setCellValue("");
        Q155.createCell((short) 3).setCellValue("");
        Q155.createCell((short) 4).setCellValue("");
        Q155.createCell((short) 5).setCellValue("");
        Q155.createCell((short) 6).setCellValue("");
        Q155.createCell((short) 7).setCellValue("");
        Q155.createCell((short) 8).setCellValue("");
        Q155.createCell((short) 9).setCellValue("");
        Q155.createCell((short) 10).setCellValue("");
        Q155.createCell((short) 11).setCellValue("");
        tr++;
        Row Q156 = sheet.createRow((short) tr);
        Q156.createCell((short) 0).setCellValue("");
        Q156.createCell((short) 1).setCellValue("Total exceptional Income");
        Q156.createCell((short) 2).setCellValue("");
        Q156.createCell((short) 3).setCellValue("");
        Q156.createCell((short) 4).setCellValue("");
        Q156.createCell((short) 5).setCellValue("");
        Q156.createCell((short) 6).setCellValue("");
        Q156.createCell((short) 7).setCellValue("");
        Q156.createCell((short) 8).setCellValue("");
        Q156.createCell((short) 9).setCellValue("");
        Q156.createCell((short) 10).setCellValue("");
        Q156.createCell((short) 11).setCellValue("");
        tr++;
        Row Q157 = sheet.createRow((short) tr);
        Q157.createCell((short) 0).setCellValue("");
        Q157.createCell((short) 1).setCellValue("Non-operating");
        Q157.createCell((short) 2).setCellValue("");
        Q157.createCell((short) 3).setCellValue("");
        Q157.createCell((short) 4).setCellValue("");
        Q157.createCell((short) 5).setCellValue("");
        Q157.createCell((short) 6).setCellValue("");
        Q157.createCell((short) 7).setCellValue("");
        Q157.createCell((short) 8).setCellValue("");
        Q157.createCell((short) 9).setCellValue("");
        Q157.createCell((short) 10).setCellValue("");
        Q157.createCell((short) 11).setCellValue("");
        tr++;

        Row Q158 = sheet.createRow((short) tr);
        Q158.createCell((short) 0).setCellValue("");
        Q158.createCell((short) 1).setCellValue("Institutional Support-Freight Operations");
        Q158.createCell((short) 2).setCellValue("");
        Q158.createCell((short) 3).setCellValue("");
        Q158.createCell((short) 4).setCellValue("");
        Q158.createCell((short) 5).setCellValue("");
        Q158.createCell((short) 6).setCellValue("");
        Q158.createCell((short) 7).setCellValue("");
        Q158.createCell((short) 8).setCellValue("");
        Q158.createCell((short) 9).setCellValue("");
        Q158.createCell((short) 10).setCellValue("");
        Q158.createCell((short) 11).setCellValue("");
        tr++;
        Row Q159 = sheet.createRow((short) tr);
        Q159.createCell((short) 0).setCellValue("");
        Q159.createCell((short) 1).setCellValue("Transfer  by Agencies from Treasury-Counterpart");
        Q159.createCell((short) 2).setCellValue("");
        Q159.createCell((short) 3).setCellValue("");
        Q159.createCell((short) 4).setCellValue("");
        Q159.createCell((short) 5).setCellValue("");
        Q159.createCell((short) 6).setCellValue("");
        Q159.createCell((short) 7).setCellValue("");
        Q159.createCell((short) 8).setCellValue("");
        Q159.createCell((short) 9).setCellValue("");
        Q159.createCell((short) 10).setCellValue("");
        Q159.createCell((short) 11).setCellValue("");
        tr++;
        Row Q160 = sheet.createRow((short) tr);
        Q160.createCell((short) 0).setCellValue("");
        Q160.createCell((short) 1).setCellValue("Institutional Support-Development-Trr-Gulu");
        Q160.createCell((short) 2).setCellValue("");
        Q160.createCell((short) 3).setCellValue("");
        Q160.createCell((short) 4).setCellValue("");
        Q160.createCell((short) 5).setCellValue("");
        Q160.createCell((short) 6).setCellValue("");
        Q160.createCell((short) 7).setCellValue("");
        Q160.createCell((short) 8).setCellValue("");
        Q160.createCell((short) 9).setCellValue("");
        Q160.createCell((short) 10).setCellValue("");
        Q160.createCell((short) 11).setCellValue("");
        tr++;
        Row Q161 = sheet.createRow((short) tr);
        Q161.createCell((short) 0).setCellValue("");
        Q161.createCell((short) 1).setCellValue("Inst.Support-Dev.:Trr-Gulu-Suppl.");
        Q161.createCell((short) 2).setCellValue("");
        Q161.createCell((short) 3).setCellValue("");
        Q161.createCell((short) 4).setCellValue("");
        Q161.createCell((short) 5).setCellValue("");
        Q161.createCell((short) 6).setCellValue("");
        Q161.createCell((short) 7).setCellValue("");
        Q161.createCell((short) 8).setCellValue("");
        Q161.createCell((short) 9).setCellValue("");
        Q161.createCell((short) 10).setCellValue("");
        Q161.createCell((short) 11).setCellValue("");
        tr++;
        Row Q162 = sheet.createRow((short) tr);
        Q162.createCell((short) 0).setCellValue("");
        Q162.createCell((short) 1).setCellValue("Donor Funds - Foreign Governments-Spain");
        Q162.createCell((short) 2).setCellValue("");
        Q162.createCell((short) 3).setCellValue("");
        Q162.createCell((short) 4).setCellValue("");
        Q162.createCell((short) 5).setCellValue("");
        Q162.createCell((short) 6).setCellValue("");
        Q162.createCell((short) 7).setCellValue("");
        Q162.createCell((short) 8).setCellValue("");
        Q162.createCell((short) 9).setCellValue("");
        Q162.createCell((short) 10).setCellValue("");
        Q162.createCell((short) 11).setCellValue("");
        tr++;
        Row Q163 = sheet.createRow((short) tr);
        Q163.createCell((short) 0).setCellValue("");
        Q163.createCell((short) 1).setCellValue("Donor Funds - Foreign Governments-AfDB");
        Q163.createCell((short) 2).setCellValue("");
        Q163.createCell((short) 3).setCellValue("");
        Q163.createCell((short) 4).setCellValue("");
        Q163.createCell((short) 5).setCellValue("");
        Q163.createCell((short) 6).setCellValue("");
        Q163.createCell((short) 7).setCellValue("");
        Q163.createCell((short) 8).setCellValue("");
        Q163.createCell((short) 9).setCellValue("");
        Q163.createCell((short) 10).setCellValue("");
        Q163.createCell((short) 11).setCellValue("");
        tr++;
        Row Q164 = sheet.createRow((short) tr);
        Q164.createCell((short) 0).setCellValue("");
        Q164.createCell((short) 1).setCellValue("Institutional Support-Supplementary");
        Q164.createCell((short) 2).setCellValue("");
        Q164.createCell((short) 3).setCellValue("");
        Q164.createCell((short) 4).setCellValue("");
        Q164.createCell((short) 5).setCellValue("");
        Q164.createCell((short) 6).setCellValue("");
        Q164.createCell((short) 7).setCellValue("");
        Q164.createCell((short) 8).setCellValue("");
        Q164.createCell((short) 9).setCellValue("");
        Q164.createCell((short) 10).setCellValue("");
        Q164.createCell((short) 11).setCellValue("");
        tr++;
        Row Q1365 = sheet.createRow((short) tr);
        Q1365.createCell((short) 0).setCellValue("");
        Q1365.createCell((short) 1).setCellValue("Total non-operating Income");
        Q1365.createCell((short) 2).setCellValue("");
        Q1365.createCell((short) 3).setCellValue("");
        Q1365.createCell((short) 4).setCellValue("");
        Q1365.createCell((short) 5).setCellValue("");
        Q1365.createCell((short) 6).setCellValue("");
        Q1365.createCell((short) 7).setCellValue("");
        Q1365.createCell((short) 8).setCellValue("");
        Q1365.createCell((short) 9).setCellValue("");
        Q1365.createCell((short) 10).setCellValue("");
        Q1365.createCell((short) 11).setCellValue("");
        tr++;
        Row Q166 = sheet.createRow((short) tr);
        Q166.createCell((short) 0).setCellValue("");
        Q166.createCell((short) 1).setCellValue("Total Income");
// Check for null values and set default value if necessary
        double totalIncomeValueActuals = totalIncomeActuals.getTotal() != null ? totalIncomeActuals.getTotal().doubleValue() : 0.0;
        double qtr1ValueActuals = totalIncomeActuals.getQtr1() != null ? totalIncomeActuals.getQtr1().doubleValue() : 0.0;
        double qtr2ValueActuals = totalIncomeActuals.getQtr2() != null ? totalIncomeActuals.getQtr2().doubleValue() : 0.0;
        double qtr3ValueActuals = totalIncomeActuals.getQtr3() != null ? totalIncomeActuals.getQtr3().doubleValue() : 0.0;
        double qtr4ValueActuals = totalIncomeActuals.getQtr4() != null ? totalIncomeActuals.getQtr4().doubleValue() : 0.0;

        double totalIncomeValue = totalIncome.getTotal() != null ? totalIncome.getTotal().doubleValue() : 0.0;
        double qtr1Value = totalIncome.getQtr1() != null ? totalIncome.getQtr1().doubleValue() : 0.0;
        double qtr2Value = totalIncome.getQtr2() != null ? totalIncome.getQtr2().doubleValue() : 0.0;
        double qtr3Value = totalIncome.getQtr3() != null ? totalIncome.getQtr3().doubleValue() : 0.0;
        double qtr4Value = totalIncome.getQtr4() != null ? totalIncome.getQtr4().doubleValue() : 0.0;
        // Q166.createCell((short) 2).setCellValue(totalIncomeValueActuals);
        Q166.createCell((short) 2).setCellValue("");
        Q166.createCell((short) 3).setCellValue(qtr1ValueActuals);
        Q166.createCell((short) 4).setCellValue(qtr2ValueActuals);
        Q166.createCell((short) 5).setCellValue(qtr3ValueActuals);
        Q166.createCell((short) 6).setCellValue(qtr4ValueActuals);
        Q166.createCell((short) 7).setCellValue(totalIncomeValue);
        Q166.createCell((short) 8).setCellValue(qtr1Value);
        Q166.createCell((short) 9).setCellValue(qtr2Value);
        Q166.createCell((short) 10).setCellValue(qtr3Value);
        Q166.createCell((short) 11).setCellValue(qtr4Value);
        tr++;
        titleBoldRed.add((int) tr);
        Row Q167 = sheet.createRow((short) tr);
        Q167.createCell((short) 0).setCellValue("");
        Q167.createCell((short) 1).setCellValue("EXPENDITURE");
        Q167.createCell((short) 2).setCellValue("");
        Q167.createCell((short) 3).setCellValue("");
        Q167.createCell((short) 4).setCellValue("");
        Q167.createCell((short) 5).setCellValue("");
        Q167.createCell((short) 6).setCellValue("");
        Q167.createCell((short) 7).setCellValue("");
        Q167.createCell((short) 8).setCellValue("");
        Q167.createCell((short) 9).setCellValue("");
        Q167.createCell((short) 10).setCellValue("");
        Q167.createCell((short) 11).setCellValue("");

        tr++;
        Row Q168 = sheet.createRow((short) tr);
        Q168.createCell((short) 0).setCellValue("");
        Q168.createCell((short) 1).setCellValue("Revenue expenditure");
        Q168.createCell((short) 2).setCellValue("");
        Q168.createCell((short) 3).setCellValue("");
        Q168.createCell((short) 4).setCellValue("");
        Q168.createCell((short) 5).setCellValue("");
        Q168.createCell((short) 6).setCellValue("");
        Q168.createCell((short) 7).setCellValue("");
        Q168.createCell((short) 8).setCellValue("");
        Q168.createCell((short) 9).setCellValue("");
        Q168.createCell((short) 10).setCellValue("");
        Q168.createCell((short) 11).setCellValue("");
        tr++;
        Row Q169 = sheet.createRow((short) tr);
        Q169.createCell((short) 0).setCellValue("");
        Q169.createCell((short) 1).setCellValue("Wage");
        Q169.createCell((short) 2).setCellValue("");
        Q169.createCell((short) 3).setCellValue("");
        Q169.createCell((short) 4).setCellValue("");
        Q169.createCell((short) 5).setCellValue("");
        Q169.createCell((short) 6).setCellValue("");
        Q169.createCell((short) 7).setCellValue("");
        Q169.createCell((short) 8).setCellValue("");
        Q169.createCell((short) 9).setCellValue("");
        Q169.createCell((short) 10).setCellValue("");
        Q169.createCell((short) 11).setCellValue("");
        tr++;
        Row Q170 = sheet.createRow((short) tr);
        Q170.createCell((short) 0).setCellValue("");
        Q170.createCell((short) 1).setCellValue("Total operating expenditure");
        Q170.createCell((short) 2).setCellValue("");
        Q170.createCell((short) 3).setCellValue("");
        Q170.createCell((short) 4).setCellValue("");
        Q170.createCell((short) 5).setCellValue("");
        Q170.createCell((short) 6).setCellValue("");
        Q170.createCell((short) 7).setCellValue("");
        Q170.createCell((short) 8).setCellValue("");
        Q170.createCell((short) 9).setCellValue("");
        Q170.createCell((short) 10).setCellValue("");
        Q170.createCell((short) 11).setCellValue("");
        tr++;
        Row Q171 = sheet.createRow((short) tr);
        Q171.createCell((short) 0).setCellValue("");
        Q171.createCell((short) 1).setCellValue("EBITDA");
        Q171.createCell((short) 2).setCellValue("");
        Q171.createCell((short) 3).setCellValue("");
        Q171.createCell((short) 4).setCellValue("");
        Q171.createCell((short) 5).setCellValue("");
        Q171.createCell((short) 6).setCellValue("");
        Q171.createCell((short) 7).setCellValue("");
        Q171.createCell((short) 8).setCellValue("");
        Q171.createCell((short) 9).setCellValue("");
        Q171.createCell((short) 10).setCellValue("");
        Q171.createCell((short) 11).setCellValue("");
        tr++;
        Row Q172 = sheet.createRow((short) tr);
        Q172.createCell((short) 0).setCellValue("");
        Q172.createCell((short) 1).setCellValue("Depreciation & Amortisation");
        Q172.createCell((short) 2).setCellValue("");
        Q172.createCell((short) 3).setCellValue("");
        Q172.createCell((short) 4).setCellValue("");
        Q172.createCell((short) 5).setCellValue("");
        Q172.createCell((short) 6).setCellValue("");
        Q172.createCell((short) 7).setCellValue("");
        Q172.createCell((short) 8).setCellValue("");
        Q172.createCell((short) 9).setCellValue("");
        Q172.createCell((short) 10).setCellValue("");
        Q172.createCell((short) 11).setCellValue("");
        tr++;
        Row Q173 = sheet.createRow((short) tr);
        Q173.createCell((short) 0).setCellValue("");
        Q173.createCell((short) 1).setCellValue("Finance charges");
        Q173.createCell((short) 2).setCellValue("");
        Q173.createCell((short) 3).setCellValue("");
        Q173.createCell((short) 4).setCellValue("");
        Q173.createCell((short) 5).setCellValue("");
        Q173.createCell((short) 6).setCellValue("");
        Q173.createCell((short) 7).setCellValue("");
        Q173.createCell((short) 8).setCellValue("");
        Q173.createCell((short) 9).setCellValue("");
        Q173.createCell((short) 10).setCellValue("");
        Q173.createCell((short) 11).setCellValue("");
        tr++;
        Row Q174 = sheet.createRow((short) tr);
        Q174.createCell((short) 0).setCellValue("");
        Q174.createCell((short) 1).setCellValue("EBT");
        Q174.createCell((short) 2).setCellValue("");
        Q174.createCell((short) 3).setCellValue("");
        Q174.createCell((short) 4).setCellValue("");
        Q174.createCell((short) 5).setCellValue("");
        Q174.createCell((short) 6).setCellValue("");
        Q174.createCell((short) 7).setCellValue("");
        Q174.createCell((short) 8).setCellValue("");
        Q174.createCell((short) 9).setCellValue("");
        Q174.createCell((short) 10).setCellValue("");
        Q174.createCell((short) 11).setCellValue("");
        tr++;
        Row Q175 = sheet.createRow((short) tr);
        Q175.createCell((short) 0).setCellValue("");
        Q175.createCell((short) 1).setCellValue("Rental tax(Provn)");
        Q175.createCell((short) 2).setCellValue("");
        Q175.createCell((short) 3).setCellValue("");
        Q175.createCell((short) 4).setCellValue("");
        Q175.createCell((short) 5).setCellValue("");
        Q175.createCell((short) 6).setCellValue("");
        Q175.createCell((short) 7).setCellValue("");
        Q175.createCell((short) 8).setCellValue("");
        Q175.createCell((short) 9).setCellValue("");
        Q175.createCell((short) 10).setCellValue("");
        Q175.createCell((short) 11).setCellValue("");
        tr++;
        Row Q176 = sheet.createRow((short) tr);
        Q176.createCell((short) 0).setCellValue("");
        Q176.createCell((short) 1).setCellValue("EAT");
        Q176.createCell((short) 2).setCellValue("");
        Q176.createCell((short) 3).setCellValue("");
        Q176.createCell((short) 4).setCellValue("");
        Q176.createCell((short) 5).setCellValue("");
        Q176.createCell((short) 6).setCellValue("");
        Q176.createCell((short) 7).setCellValue("");
        Q176.createCell((short) 8).setCellValue("");
        Q176.createCell((short) 9).setCellValue("");
        Q176.createCell((short) 10).setCellValue("");
        Q176.createCell((short) 11).setCellValue("");
        tr++;
        Row Q177 = sheet.createRow((short) tr);
        Q177.createCell((short) 0).setCellValue("");
        Q177.createCell((short) 1).setCellValue("Total revenue expenditure");
        Q177.createCell((short) 2).setCellValue("");
        Q177.createCell((short) 3).setCellValue("");
        Q177.createCell((short) 4).setCellValue("");
        Q177.createCell((short) 5).setCellValue("");
        Q177.createCell((short) 6).setCellValue("");
        Q177.createCell((short) 7).setCellValue("");
        Q177.createCell((short) 8).setCellValue("");
        Q177.createCell((short) 9).setCellValue("");
        Q177.createCell((short) 10).setCellValue("");
        Q177.createCell((short) 11).setCellValue("");
        tr++;
        titleBoldRed.add((int) tr);
        Row Q178 = sheet.createRow((short) tr);
        Q178.createCell((short) 0).setCellValue("");
        Q178.createCell((short) 1).setCellValue("Operating ratio");
        Q178.createCell((short) 2).setCellValue("");
        Q178.createCell((short) 3).setCellValue("");
        Q178.createCell((short) 4).setCellValue("");
        Q178.createCell((short) 5).setCellValue("");
        Q178.createCell((short) 6).setCellValue("");
        Q178.createCell((short) 7).setCellValue("");
        Q178.createCell((short) 8).setCellValue("");
        Q178.createCell((short) 9).setCellValue("");
        Q178.createCell((short) 10).setCellValue("");
        Q178.createCell((short) 11).setCellValue("");
        tr++;
        Row Q179 = sheet.createRow((short) tr);
        Q179.createCell((short) 0).setCellValue("");
        Q179.createCell((short) 1).setCellValue("Capital expenditure");
        Q179.createCell((short) 2).setCellValue("");
        Q179.createCell((short) 3).setCellValue("");
        Q179.createCell((short) 4).setCellValue("");
        Q179.createCell((short) 5).setCellValue("");
        Q179.createCell((short) 6).setCellValue("");
        Q179.createCell((short) 7).setCellValue("");
        Q179.createCell((short) 8).setCellValue("");
        Q179.createCell((short) 9).setCellValue("");
        Q179.createCell((short) 10).setCellValue("");
        Q179.createCell((short) 11).setCellValue("");
        tr++;
        Row Q180 = sheet.createRow((short) tr);
        Q180.createCell((short) 0).setCellValue("");
        Q180.createCell((short) 1).setCellValue("Taxes");
        Q180.createCell((short) 2).setCellValue("");
        Q180.createCell((short) 3).setCellValue("");
        Q180.createCell((short) 4).setCellValue("");
        Q180.createCell((short) 5).setCellValue("");
        Q180.createCell((short) 6).setCellValue("");
        Q180.createCell((short) 7).setCellValue("");
        Q180.createCell((short) 8).setCellValue("");
        Q180.createCell((short) 9).setCellValue("");
        Q180.createCell((short) 10).setCellValue("");
        Q180.createCell((short) 11).setCellValue("");
        tr++;
        Row Q181 = sheet.createRow((short) tr);
        Q181.createCell((short) 0).setCellValue("");
        Q181.createCell((short) 1).setCellValue("Exceptional expenditure");
        Q181.createCell((short) 2).setCellValue("");
        Q181.createCell((short) 3).setCellValue("");
        Q181.createCell((short) 4).setCellValue("");
        Q181.createCell((short) 5).setCellValue("");
        Q181.createCell((short) 6).setCellValue("");
        Q181.createCell((short) 7).setCellValue("");
        Q181.createCell((short) 8).setCellValue("");
        Q181.createCell((short) 9).setCellValue("");
        Q181.createCell((short) 10).setCellValue("");
        Q181.createCell((short) 11).setCellValue("");
        tr++;
        Row Q182 = sheet.createRow((short) tr);
        Q182.createCell((short) 0).setCellValue("");
        Q182.createCell((short) 1).setCellValue("Total expenditure (Incl.Depn).");
        Q182.createCell((short) 2).setCellValue("");
        Q182.createCell((short) 3).setCellValue("");
        Q182.createCell((short) 4).setCellValue("");
        Q182.createCell((short) 5).setCellValue("");
        Q182.createCell((short) 6).setCellValue("");
        Q182.createCell((short) 7).setCellValue("");
        Q182.createCell((short) 8).setCellValue("");
        Q182.createCell((short) 9).setCellValue("");
        Q182.createCell((short) 10).setCellValue("");
        Q182.createCell((short) 11).setCellValue("");
        tr++;
        Row Q183 = sheet.createRow((short) tr);
        Q183.createCell((short) 0).setCellValue("");
        Q183.createCell((short) 1).setCellValue("Less Depn.& amortisation");
        Q183.createCell((short) 2).setCellValue("");
        Q183.createCell((short) 3).setCellValue("");
        Q183.createCell((short) 4).setCellValue("");
        Q183.createCell((short) 5).setCellValue("");
        Q183.createCell((short) 6).setCellValue("");
        Q183.createCell((short) 7).setCellValue("");
        Q183.createCell((short) 8).setCellValue("");
        Q183.createCell((short) 9).setCellValue("");
        Q183.createCell((short) 10).setCellValue("");
        Q183.createCell((short) 11).setCellValue("");
        tr++;
        Row Q184 = sheet.createRow((short) tr);
        Q184.createCell((short) 0).setCellValue("");
        Q184.createCell((short) 1).setCellValue("Net total exp.(Excl.depn.& amortisation)");
        Q184.createCell((short) 2).setCellValue("");
        Q184.createCell((short) 3).setCellValue("");
        Q184.createCell((short) 4).setCellValue("");
        Q184.createCell((short) 5).setCellValue("");
        Q184.createCell((short) 6).setCellValue("");
        Q184.createCell((short) 7).setCellValue("");
        Q184.createCell((short) 8).setCellValue("");
        Q184.createCell((short) 9).setCellValue("");
        Q184.createCell((short) 10).setCellValue("");
        Q184.createCell((short) 11).setCellValue("");
        tr++;
        Row Q185 = sheet.createRow((short) tr);
        Q185.createCell((short) 0).setCellValue("");
        Q185.createCell((short) 1).setCellValue("Surplus/(Deficit)");
        Q185.createCell((short) 2).setCellValue("");
        Q185.createCell((short) 3).setCellValue("");
        Q185.createCell((short) 4).setCellValue("");
        Q185.createCell((short) 5).setCellValue("");
        Q185.createCell((short) 6).setCellValue("");
        Q185.createCell((short) 7).setCellValue("");
        Q185.createCell((short) 8).setCellValue("");
        Q185.createCell((short) 9).setCellValue("");
        Q185.createCell((short) 10).setCellValue("");
        Q185.createCell((short) 11).setCellValue("");
        tr++;
        Row Q186 = sheet.createRow((short) tr);
        Q186.createCell((short) 0).setCellValue("");
        Q186.createCell((short) 1).setCellValue("TOP-LEVEL SUMMARY-2");
        Q186.createCell((short) 2).setCellValue("");
        Q186.createCell((short) 3).setCellValue("");
        Q186.createCell((short) 4).setCellValue("");
        Q186.createCell((short) 5).setCellValue("");
        Q186.createCell((short) 6).setCellValue("");
        Q186.createCell((short) 7).setCellValue("");
        Q186.createCell((short) 8).setCellValue("");
        Q186.createCell((short) 9).setCellValue("");
        Q186.createCell((short) 10).setCellValue("");
        Q186.createCell((short) 11).setCellValue("");
        tr++;
        Row Q187 = sheet.createRow((short) tr);
        Q187.createCell((short) 0).setCellValue("");
        Q187.createCell((short) 1).setCellValue(" Total Income ");
        Q187.createCell((short) 2).setCellValue("");
        Q187.createCell((short) 3).setCellValue("");
        Q187.createCell((short) 4).setCellValue("");
        Q187.createCell((short) 5).setCellValue("");
        Q187.createCell((short) 6).setCellValue("");
        Q187.createCell((short) 7).setCellValue("");
        Q187.createCell((short) 8).setCellValue("");
        Q187.createCell((short) 9).setCellValue("");
        Q187.createCell((short) 10).setCellValue("");
        Q187.createCell((short) 11).setCellValue("");
        tr++;
        Row Q188 = sheet.createRow((short) tr);
        Q188.createCell((short) 0).setCellValue("");
        Q188.createCell((short) 1).setCellValue(" Total expenditure ");
        Q188.createCell((short) 2).setCellValue("");
        Q188.createCell((short) 3).setCellValue("");
        Q188.createCell((short) 4).setCellValue("");
        Q188.createCell((short) 5).setCellValue("");
        Q188.createCell((short) 6).setCellValue("");
        Q188.createCell((short) 7).setCellValue("");
        Q188.createCell((short) 8).setCellValue("");
        Q188.createCell((short) 9).setCellValue("");
        Q188.createCell((short) 10).setCellValue("");
        Q188.createCell((short) 11).setCellValue("");
        tr++;
        Row Q189 = sheet.createRow((short) tr);
        Q189.createCell((short) 0).setCellValue("");
        Q189.createCell((short) 1).setCellValue(" Net surplus/(Deficit)");
        Q189.createCell((short) 2).setCellValue("");
        Q189.createCell((short) 3).setCellValue("");
        Q189.createCell((short) 4).setCellValue("");
        Q189.createCell((short) 5).setCellValue("");
        Q189.createCell((short) 6).setCellValue("");
        Q189.createCell((short) 7).setCellValue("");
        Q189.createCell((short) 8).setCellValue("");
        Q189.createCell((short) 9).setCellValue("");
        Q189.createCell((short) 10).setCellValue("");
        Q189.createCell((short) 11).setCellValue("");

        tr++;
        Row Q190 = sheet.createRow((short) tr);
        Q190.createCell((short) 0).setCellValue("");
        Q190.createCell((short) 1).setCellValue("TOP-LEVEL SUMMARY- 2");
        Q190.createCell((short) 2).setCellValue("");
        Q190.createCell((short) 3).setCellValue("");
        Q190.createCell((short) 4).setCellValue("");
        Q190.createCell((short) 5).setCellValue("");
        Q190.createCell((short) 6).setCellValue("");
        Q190.createCell((short) 7).setCellValue("");
        Q190.createCell((short) 8).setCellValue("");
        Q190.createCell((short) 9).setCellValue("");
        Q190.createCell((short) 10).setCellValue("");
        Q190.createCell((short) 11).setCellValue("");
        tr++;
        Row Q191 = sheet.createRow((short) tr);
        Q191.createCell((short) 0).setCellValue("");
        Q191.createCell((short) 1).setCellValue("Total Income");
        Q191.createCell((short) 2).setCellValue("");
        Q191.createCell((short) 3).setCellValue("");
        Q191.createCell((short) 4).setCellValue("");
        Q191.createCell((short) 5).setCellValue("");
        Q191.createCell((short) 6).setCellValue("");
        Q191.createCell((short) 7).setCellValue("");
        Q191.createCell((short) 8).setCellValue("");
        Q191.createCell((short) 9).setCellValue("");
        Q191.createCell((short) 10).setCellValue("");
        Q191.createCell((short) 11).setCellValue("");
        tr++;
        Row Q192 = sheet.createRow((short) tr);
        Q192.createCell((short) 0).setCellValue("");
        Q192.createCell((short) 1).setCellValue("Total Expenditure");
        Q192.createCell((short) 2).setCellValue("");
        Q192.createCell((short) 3).setCellValue("");
        Q192.createCell((short) 4).setCellValue("");
        Q192.createCell((short) 5).setCellValue("");
        Q192.createCell((short) 6).setCellValue("");
        Q192.createCell((short) 7).setCellValue("");
        Q192.createCell((short) 8).setCellValue("");
        Q192.createCell((short) 9).setCellValue("");
        Q192.createCell((short) 10).setCellValue("");
        Q192.createCell((short) 11).setCellValue("");
        tr++;
        Row Q193 = sheet.createRow((short) tr);
        Q193.createCell((short) 0).setCellValue("");
        Q193.createCell((short) 1).setCellValue("Net surplus/(Deficit)");
        Q193.createCell((short) 2).setCellValue("");
        Q193.createCell((short) 3).setCellValue("");
        Q193.createCell((short) 4).setCellValue("");
        Q193.createCell((short) 5).setCellValue("");
        Q193.createCell((short) 6).setCellValue("");
        Q193.createCell((short) 7).setCellValue("");
        Q193.createCell((short) 8).setCellValue("");
        Q193.createCell((short) 9).setCellValue("");
        Q193.createCell((short) 10).setCellValue("");
        Q193.createCell((short) 11).setCellValue("");
        createDefaultStyle(workbook, sheet, startrow100);
        createTitleStyle(workbook, sheet, title);
        createBoldDeafaultStyle(workbook, sheet, titleJustBold);
        createOrangeDeafaultStyle(workbook, sheet);
        createBoldBlueDeafaultStyle(workbook, sheet, titleBoldBlue);
        createBoldRedDeafaultStyle(workbook, sheet, titleBoldRed);
        createBoldTotalDeafaultStyle(workbook, sheet, titleBoldTotal);
        createBoldOrangeHeaderDeafaultStyle(workbook, sheet, titleBoldOrange);

    }

    public BigDecimal totalByRoute(int y) {
        BigDecimal tot = BigDecimal.ZERO;
        if (y == 1) {
            List<COA> list = new ArrayList<>();
            list.add(sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
            tot = sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(comboBox.getValue(), list);

        } else if (y == 2) {
            List<COA> list = new ArrayList<>();
            list.add(sampleCoaService.findByCodeAndBudget("111104", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111105", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111106", comboBox.getValue()));
            tot = sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacodes(comboBox.getValue(), list);
        }
        return tot;
    }

    public MonthlySumResponseFreight totalByRoutes(int y) {
        BigDecimal tot = BigDecimal.ZERO;
        if (y == 1) {
            List<COA> list = new ArrayList<>();
            list.add(sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
            return sampleFreightVolumesService.getTotals(comboBox.getValue(), list);

        } else if (y == 2) {
            List<COA> list = new ArrayList<>();
            list.add(sampleCoaService.findByCodeAndBudget("111104", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111105", comboBox.getValue()));
            list.add(sampleCoaService.findByCodeAndBudget("111106", comboBox.getValue()));
            return sampleFreightVolumesService.getTotals(comboBox.getValue(), list);
        } else {
            return null;
        }

    }

    public MonthlySumResponseFreight totalByRoutes() {

        List<COA> list = new ArrayList<>();
        list.add(sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
        list.add(sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));
        list.add(sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));

        list.add(sampleCoaService.findByCodeAndBudget("111104", comboBox.getValue()));
        list.add(sampleCoaService.findByCodeAndBudget("111105", comboBox.getValue()));
        list.add(sampleCoaService.findByCodeAndBudget("111106", comboBox.getValue()));
        return sampleFreightVolumesService.getTotals(comboBox.getValue(), list);

    }

    public MonthlySumResponseFreight calculateTotal(List<MonthlySumResponseFreight> monthlySums) {
        BigDecimal julTotal = BigDecimal.ZERO;
        BigDecimal augTotal = BigDecimal.ZERO;
        BigDecimal sepTotal = BigDecimal.ZERO;
        BigDecimal octTotal = BigDecimal.ZERO;
        BigDecimal novTotal = BigDecimal.ZERO;
        BigDecimal decTotal = BigDecimal.ZERO;
        BigDecimal janTotal = BigDecimal.ZERO;
        BigDecimal febTotal = BigDecimal.ZERO;
        BigDecimal marTotal = BigDecimal.ZERO;
        BigDecimal aprTotal = BigDecimal.ZERO;
        BigDecimal mayTotal = BigDecimal.ZERO;
        BigDecimal junTotal = BigDecimal.ZERO;
        BigDecimal qtr1 = BigDecimal.ZERO;
        BigDecimal qtr2 = BigDecimal.ZERO;
        BigDecimal qtr3 = BigDecimal.ZERO;
        BigDecimal qtr4 = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (MonthlySumResponseFreight freight : monthlySums) {
            julTotal = addIfNotNull(julTotal, freight.getJul());
            augTotal = addIfNotNull(augTotal, freight.getAug());
            sepTotal = addIfNotNull(sepTotal, freight.getSep());
            octTotal = addIfNotNull(octTotal, freight.getOct());
            novTotal = addIfNotNull(novTotal, freight.getNov());
            decTotal = addIfNotNull(decTotal, freight.getDec());
            janTotal = addIfNotNull(janTotal, freight.getJan());
            febTotal = addIfNotNull(febTotal, freight.getFeb());
            marTotal = addIfNotNull(marTotal, freight.getMar());
            aprTotal = addIfNotNull(aprTotal, freight.getApr());
            mayTotal = addIfNotNull(mayTotal, freight.getMay());
            junTotal = addIfNotNull(junTotal, freight.getJun());

            total = addIfNotNull(total, freight.getTotal());
        }
// Calculate quarterly totals, handling potential null values
        qtr1 = addIfNotNull2(addIfNotNull(julTotal, augTotal), sepTotal);
        qtr2 = addIfNotNull2(addIfNotNull(octTotal, novTotal), decTotal);
        qtr3 = addIfNotNull2(addIfNotNull(janTotal, febTotal), marTotal);
        qtr4 = addIfNotNull2(addIfNotNull(aprTotal, mayTotal), junTotal);
        return new MonthlySumResponseFreight(julTotal, augTotal, sepTotal, octTotal, novTotal, decTotal, janTotal,
                febTotal, marTotal, aprTotal, mayTotal, junTotal, total, qtr1, qtr2, qtr3, qtr4);
    }

    private BigDecimal addIfNotNull(BigDecimal sum, BigDecimal value) {
        return (value != null) ? sum.add(value) : sum;
    }

    private BigDecimal addIfNotNull2(BigDecimal a, BigDecimal b) {
        if (a != null && b != null) {
            return a.add(b);
        } else if (a != null) {
            return a;
        } else if (b != null) {
            return b;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Qtr sumQuarterTotals(List<Qtr> qtrList) {
        BigDecimal qtr1Total = BigDecimal.ZERO;
        BigDecimal qtr2Total = BigDecimal.ZERO;
        BigDecimal qtr3Total = BigDecimal.ZERO;
        BigDecimal qtr4Total = BigDecimal.ZERO;

        BigDecimal bqtr1Total = BigDecimal.ZERO;
        BigDecimal bqtr2Total = BigDecimal.ZERO;
        BigDecimal bqtr3Total = BigDecimal.ZERO;
        BigDecimal bqtr4Total = BigDecimal.ZERO;

        BigDecimal pbudgetTotal = BigDecimal.ZERO;
        BigDecimal cbudgetTotal = BigDecimal.ZERO;

        for (Qtr qtr : qtrList) {
            qtr1Total = addIfNotNull(qtr1Total, qtr.getQtr1());
            qtr2Total = addIfNotNull(qtr2Total, qtr.getQtr2());
            qtr3Total = addIfNotNull(qtr3Total, qtr.getQtr3());
            qtr4Total = addIfNotNull(qtr4Total, qtr.getQtr4());

            bqtr1Total = addIfNotNull(bqtr1Total, qtr.getBqtr1());
            bqtr2Total = addIfNotNull(bqtr2Total, qtr.getBqtr2());
            bqtr3Total = addIfNotNull(bqtr3Total, qtr.getBqtr3());
            bqtr4Total = addIfNotNull(bqtr4Total, qtr.getBqtr4());

            pbudgetTotal = addIfNotNull(pbudgetTotal, qtr.getPbudget());
            cbudgetTotal = addIfNotNull(cbudgetTotal, qtr.getCbudget());
        }

        return new Qtr(qtr1Total, qtr2Total, qtr3Total, qtr4Total,
                bqtr1Total,
                bqtr2Total,
                bqtr3Total,
                bqtr4Total,
                pbudgetTotal,
                cbudgetTotal);
    }

    public void createDefaultStyle(Workbook workbook, Sheet sheet, int startrow100) {
        // Create font
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.BLACK.getIndex());

        // Create cell style
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setDataFormat((short) 0x29); // Apply the desired data format
        // Set border properties
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        // Apply the style to all cells in the sheet
        for (Row row : sheet) {
            for (Cell cell : row) {
                cell.setCellStyle(style);

                if ((cell.getCellType() == CellType.NUMERIC) && row.getRowNum() >= startrow100) {
                    double originalValue = cell.getNumericCellValue();
                    // Divide by 1000 and handle zero values
                    double newValue = originalValue != 0.0 ? originalValue / 1000 : 0.0;
                    cell.setCellValue(newValue);
                }
            }
        }

        // Auto-fit column width for all columns
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

    }

    public void createTitleStyle(Workbook workbook, Sheet sheet, List<Integer> title) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (title.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cell.getCellStyle());
                    activityStyle.setAlignment(HorizontalAlignment.CENTER);

                    // Setting font properties
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityFont.setColor(IndexedColors.WHITE.getIndex()); // Setting font color to white
                    activityStyle.setFont(activityFont);

                    // Setting background color
                    activityStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
                    activityStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    activityStyle.setBorderBottom(BorderStyle.THIN);

                    //activityStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
                    activityStyle.setDataFormat((short) 0x29);

                    cell.setCellStyle(activityStyle);
                }
            }
        }
    }

    public void createBoldDeafaultStyle(Workbook workbook, Sheet sheet, List<Integer> title) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (title.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cell.getCellStyle());

                    // Setting font properties
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityFont.setColor(IndexedColors.BLACK.getIndex()); // Setting font color to white
                    activityStyle.setFont(activityFont);

                    cell.setCellStyle(activityStyle);
                }
            }
        }
    }

    public void createBoldTotalDeafaultStyle(Workbook workbook, Sheet sheet, List<Integer> title) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (title.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cell.getCellStyle());

                    // Setting font properties
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityFont.setColor(IndexedColors.BLACK.getIndex()); // Setting font color to white
                    activityStyle.setFont(activityFont);
                    activityStyle.setBorderTop(BorderStyle.THIN);
                    activityStyle.setBorderBottom(BorderStyle.DOUBLE);
                    activityStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                    activityStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    cell.setCellStyle(activityStyle);
                }
            }
        }
    }

    public void createBoldBlueDeafaultStyle(Workbook workbook, Sheet sheet, List<Integer> title) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (title.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cell.getCellStyle());

                    // Setting font properties
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityFont.setColor(IndexedColors.BLUE.getIndex()); // Setting font color to white
                    activityStyle.setFont(activityFont);

                    cell.setCellStyle(activityStyle);
                }
            }
        }
    }

    public void createBoldRedDeafaultStyle(Workbook workbook, Sheet sheet, List<Integer> title) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (title.contains(cell.getRow().getRowNum())) {
                    CellStyle activityStyle = workbook.createCellStyle();
                    activityStyle.cloneStyleFrom(cell.getCellStyle());

                    // Setting font properties
                    Font activityFont = workbook.createFont();
                    activityFont.setBold(true);
                    activityFont.setColor(IndexedColors.RED.getIndex()); // Setting font color to white
                    activityStyle.setFont(activityFont);

                    cell.setCellStyle(activityStyle);
                }
            }
        }
    }

    public void createBoldOrangeHeaderDeafaultStyle(Workbook workbook, Sheet sheet, List<Integer> title) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (title.contains(cell.getRow().getRowNum())) {
                    CellStyle orangeStyle = workbook.createCellStyle();
                    orangeStyle.cloneStyleFrom(cell.getCellStyle());
                    orangeStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                    orangeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    cell.setCellStyle(orangeStyle);
                }
            }
        }
    }

    public void createOrangeDeafaultStyle(Workbook workbook, Sheet sheet) {
        for (Row row : sheet) {
            for (Cell cell : row) {

                int rowIndex = cell.getRowIndex();
                int cellIndex = cell.getColumnIndex();

                // Set background color to orange for cell index 2 and starting from row index 2
                if (rowIndex >= 2 && (cellIndex == 2 || cellIndex == 7)) {
                    CellStyle orangeStyle = workbook.createCellStyle();
                    orangeStyle.cloneStyleFrom(cell.getCellStyle());
                    orangeStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                    orangeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    cell.setCellStyle(orangeStyle);
                }
            }
        }
    }

    private BigDecimal getQtrs(String coa, Quarters quarters) {
        BigDecimal qtrTotal = BigDecimal.ZERO;
        if (quarters.equals(Quarters.Qtr1)) {
            qtrTotal = samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Jul"))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Aug")))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Sep")));
            return qtrTotal;
        } else if (quarters.equals(Quarters.Qtr2)) {
            qtrTotal = samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Oct"))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Nov")))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Dec")));
            return qtrTotal;
        } else if (quarters.equals(Quarters.Qtr3)) {
            qtrTotal = samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Jan"))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Feb")))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Mar")));
            return qtrTotal;
        } else if (quarters.equals(Quarters.Qtr4)) {
            qtrTotal = samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Apr"))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "May")))
                    .add(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriod(coa, extActuals.generatePreviousPeriod(comboBox.getValue().getFinancialYear(), "Jun")));
            return qtrTotal;
        } else {
            return BigDecimal.ZERO;
        }

    }

    public List<String> periods(String fy, Quarters qtr) {
        int[] years = extractYears(fy);
        List<String> periods = new ArrayList<>();

        if (qtr.equals(Quarters.Qtr1)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d007", y), String.format("%d008", y), String.format("%d009", y)));
        } else if (qtr.equals(Quarters.Qtr2)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)));
        } else if (qtr.equals(Quarters.Qtr3)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d001", y), String.format("%d002", y), String.format("%d003", y)));
        } else if (qtr.equals(Quarters.Qtr4)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d004", y), String.format("%d005", y), String.format("%d006", y)));
        } else if (qtr.equals(Quarters.Total)) {
            for (int y : years) {
                periods.addAll(Arrays.asList(
                        String.format("%d001", y), String.format("%d002", y), String.format("%d003", y),
                        String.format("%d004", y), String.format("%d005", y), String.format("%d006", y),
                        String.format("%d007", y), String.format("%d008", y), String.format("%d009", y),
                        String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)
                ));
            }
        }

        return periods;
    }

    public List<String> previousPeriods(String fy, Quarters qtr) {
        int[] years = extractYears(fy);
        List<String> periods = new ArrayList<>();
        int y = years[1];
        if (qtr.equals(Quarters.Qtr1)) {

            periods.addAll(Arrays.asList(String.format("%d001", y), String.format("%d002", y), String.format("%d003", y)));

        } else if (qtr.equals(Quarters.Qtr2)) {

            periods.addAll(Arrays.asList(String.format("%d004", y), String.format("%d005", y), String.format("%d006", y)));
        } else if (qtr.equals(Quarters.Qtr3)) {

            periods.addAll(Arrays.asList(String.format("%d007", y), String.format("%d008", y), String.format("%d009", y)));
        } else if (qtr.equals(Quarters.Qtr4)) {

            periods.addAll(Arrays.asList(String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)));
        } else if (qtr.equals(Quarters.Total)) {
            for (int x : years) {
                periods.addAll(Arrays.asList(
                        String.format("%d001", y), String.format("%d002", y), String.format("%d003", y),
                        String.format("%d004", y), String.format("%d005", y), String.format("%d006", y),
                        String.format("%d007", y), String.format("%d008", y), String.format("%d009", y),
                        String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)
                ));
            }
        }

        return periods;
    }

    public String getPreviousFy(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        return String.format("FY%d-%d", newfy1, fy1);
    }

    public Budget getPreviousBudget(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        // return String.format("FY%d-%d", newfy1, fy1);
        Optional<Budget> bug = repository.findByFY(String.format("FY%d-%d", newfy1, fy1));
        if (bug.isPresent()) {
            return bug.get();
        } else {
            return null;
        }
    }

    public int[] extractYears(String input) {
        int[] years = new int[2];
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        int i = 0;
        while (matcher.find() && i < 2) {
            years[i++] = Integer.parseInt(matcher.group());
        }

        return years;
    }

    private void generateFundSourcePdf(
            List<Organisation> organisations,
            Set<UrcDeptSectionAnlDimbgt> sections,
            BigDecimal grandTotalIncome) throws IOException {

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont itallic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // ================= TITLE =================
        Paragraph title = new Paragraph("URC Funding Sources")
                .setFont(bold)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15);

        document.add(title);

        // ================= TABLE =================
        Table table = new Table(new float[]{4, 3, 3});
        table.setWidth(UnitValue.createPercentValue(100));

        // Header styling
        Style headerStyle = new Style()
                .setBackgroundColor(ColorConstants.YELLOW)
                .setFont(bold)
                .setTextAlignment(TextAlignment.CENTER);

        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Fund Source")).addStyle(headerStyle));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Amount (UGX)")).addStyle(headerStyle));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("% Contribution")).addStyle(headerStyle));

        // ================= DATA ROWS =================
        BigDecimal footerTotalAmount = BigDecimal.ZERO;

        for (Organisation org : organisations) {

            BigDecimal amount
                    = sampleBudgetItemsService
                            .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                                    comboBox2.getValue(), sections, org);

            footerTotalAmount = footerTotalAmount.add(amount);

            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(org.getName())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(formatCurrency(amount))));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(calculatePercentage(amount, grandTotalIncome))));
        }

        // ================= FOOTER =================
        Style footerStyle = new Style()
                .setBackgroundColor(ColorConstants.YELLOW)
                .setFont(bold);

        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("TOTAL")).addStyle(footerStyle));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(formatCurrency(footerTotalAmount))).addStyle(footerStyle));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(
                calculatePercentage(footerTotalAmount, grandTotalIncome)
        )).addStyle(footerStyle));

        document.add(table);
        document.close();

        // ================= DOWNLOAD =================
        StreamResource resource = new StreamResource(
                "URC_Funding_Sources.pdf",
                () -> new ByteArrayInputStream(baos.toByteArray())
        );

        Anchor downloadLink = new Anchor(resource, "");
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.add(new Button("Download PDF"));
        datasubLayout.add(downloadLink);
        // dataLayout.add(downloadLink);
    }

    public byte[] generateVolumesPdf(List<PerformanceRow> rows,
            String prevFy,
            String currFy) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        Document document = new Document(pdfDoc);

        PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

        // Title
        document.add(
                new Paragraph("VOLUMES & PASSENGER PERFORMANCE REPORT")
                        .setFont(bold)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
        );

        document.add(new Paragraph("\n"));

        // 4-column table
        Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
                .useAllAvailableWidth();

        // Header row
        addHeader(table, "Details", bold);
        addHeader(table, prevFy + " Approved (MTs)", bold);
        addHeader(table, prevFy + " Actual (MTs)", bold);
        addHeader(table, currFy + " Budget (MTs)", bold);

        // Data rows
        for (PerformanceRow row : rows) {
            boolean isTotal = row.getLabel() != null && row.getLabel().startsWith("TOTAL");
            PdfFont rowFont = isTotal ? bold : normal;

            addCell(table, row.getLabel(), rowFont, TextAlignment.LEFT);
            addCell(table, row.getPreviousBudgetApproved(), rowFont, TextAlignment.RIGHT);
            addCell(table, row.getPreviousBudgeActual(), rowFont, TextAlignment.RIGHT);
            addCell(table, row.getChosenBudget(), rowFont, TextAlignment.RIGHT);
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private void addHeader(Table table, String text, PdfFont font) {
        table.addHeaderCell(
                new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(text).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER)
        );
    }

    private void addCell(Table table, String text, PdfFont font, TextAlignment align) {
        table.addCell(
                new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(text == null ? "" : text).setFont(font))
                        .setTextAlignment(align)
        );
    }

    public byte[] generateVolumesPdf2(List<PerformanceRow> rows, String prevFy, String currFy) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        Document document = new Document(pdfDoc);
        document.setMargins(20, 20, 20, 20);
        addCenteredLogo(document);
        PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

        // ===== Report Header =====
        document.add(buildTitleBlock(bold, normal, prevFy, currFy));

        // ===== Table =====
        // widths: Details wider; numbers equal width
        float[] widths = new float[]{52, 16, 16, 16};
        Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();

        // Header cells (repeat on each page)
        addHeaderCell(table, "Details", bold);
        addHeaderCell(table, prevFy + " Approved (UGX '000)", bold);
        addHeaderCell(table, prevFy + " Actual (UGX '000)", bold);
        addHeaderCell(table, currFy + " Budget (UGX '000)", bold);

        boolean zebra = false;

        for (PerformanceRow row : rows) {
            String label = safe(row.getLabel());

            boolean isTotal = label.toUpperCase().startsWith("TOTAL");
            boolean isSection = isSectionLabel(label);  // section headers like RECURRENT INCOME, FREIGHT SERVICES, etc.

            // choose font
            PdfFont rowFont = (isTotal || isSection) ? bold : normal;

            // row background style
            com.itextpdf.kernel.colors.Color bg = null;
            if (isSection) {
                bg = com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY;
            } else if (isTotal) {
                bg = com.itextpdf.kernel.colors.ColorConstants.WHITE;
            } else if (zebra) {
                bg = com.itextpdf.kernel.colors.ColorConstants.WHITE;
            }

            // Details cell: indent children if you have level info; otherwise simple rule by label
            String detailsText = formatDetailsLabel(label, row);

            addBodyCell(table, detailsText, rowFont, TextAlignment.LEFT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getPreviousBudgetApproved()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getPreviousBudgeActual()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getChosenBudget()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);

            // zebra toggles for normal rows only
            if (!isSection) {
                zebra = !zebra;
            }
        }

        document.add(table);

        // Footer (optional)
        document.add(new Paragraph("\n")
                .setFont(normal)
                .setFontSize(8));

        document.close();
        return baos.toByteArray();
    }

    public byte[] generateRevenuePerformancePdf(List<PerformanceRow> rows,
            String prevFy,
            String currFy,
            String prevFinancialYearLabel,
            String currFinancialYearLabel) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        Document document = new Document(pdfDoc);
        document.setMargins(20, 20, 20, 20);
        addCenteredLogo(document);

        PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

        // ===== Title block =====
        document.add(buildTitleBlock(bold, normal, prevFy, currFy));

        // ===== 6-column table =====
        float[] widths = new float[]{34, 13.2f, 13.2f, 13.2f, 13.2f, 13.2f};
        Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();

        // ---- Group header row (Prev FY spans 4 cols) ----
        table.addHeaderCell(groupHeaderCell("", bold, 1)); // Details col placeholder

        table.addHeaderCell(groupHeaderCell(prevFinancialYearLabel, bold, 4)); // Approved..Projected
        table.addHeaderCell(groupHeaderCell(currFinancialYearLabel, bold, 1)); // Planned

        // ---- Column header row ----
        addHeaderCell(table, "Details", bold);
        addHeaderCell(table, "Approved Budget", bold);
        addHeaderCell(table, "Revised Budget", bold);
        addHeaderCell(table, "Actual Q2", bold);
        addHeaderCell(table, "Projected Actuals", bold);
        addHeaderCell(table, "Planned Budget", bold);

        // ---- Units row ----
        addUnitsCell(table, "", normal);
        addUnitsCell(table, "UGX '000", normal);
        addUnitsCell(table, "UGX '000", normal);
        addUnitsCell(table, "UGX '000", normal);
        addUnitsCell(table, "UGX '000", normal);
        addUnitsCell(table, "UGX '000", normal);

        boolean zebra = false;

        for (PerformanceRow row : rows) {
            String label = safe(row.getLabel());

            boolean isTotal = label.toUpperCase().startsWith("TOTAL");
            boolean isSection = isSectionLabel(label);

            PdfFont rowFont = (isTotal || isSection) ? bold : normal;

            com.itextpdf.kernel.colors.Color bg = null;
            if (isSection) {
                bg = com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY;
            } else if (zebra) {
                bg = com.itextpdf.kernel.colors.ColorConstants.WHITE;
            }

            String detailsText = formatDetailsLabel(label);

            addBodyCell(table, detailsText, rowFont, TextAlignment.LEFT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getPreviousBudgetApproved()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getPreviousRevised()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getPreviousBudgeActual()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getPreviousProjected()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);
            addBodyCell(table, safe(row.getChosenBudget()), rowFont, TextAlignment.RIGHT, bg, isTotal, isSection);

            if (!isSection) {
                zebra = !zebra;
            }
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    private com.itextpdf.layout.element.Div buildTitleBlock(
            PdfFont bold,
            PdfFont normal,
            String prevFy,
            String currFy) {

        com.itextpdf.layout.element.Div container
                = new com.itextpdf.layout.element.Div();

        container.setTextAlignment(TextAlignment.CENTER);

        // ===============================
        // 1️⃣ ADD LOGO (FROM DB)
        // ===============================
        try {
            SystemIcon logoIcon = systemIconService.getRequired("URC_LOGO");

            ImageData imageData = ImageDataFactory.create(logoIcon.getData());
            com.itextpdf.layout.element.Image logo
                    = new com.itextpdf.layout.element.Image(imageData);

            logo.scaleToFit(110, 60);   // Adjust size if needed
            logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            logo.setMarginBottom(8);

            container.add(logo);

        } catch (Exception ex) {
            System.out.println("URC_LOGO not found for PDF header.");
        }

        // ===============================
        // 2️⃣ REPORT TITLE
        // ===============================
        container.add(new Paragraph("REVENUE PERFORMANCE REPORT")
                .setFont(bold)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2));

        container.add(new Paragraph("FY Comparison: " + prevFy + " vs " + currFy)
                .setFont(normal)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2));

        container.add(new Paragraph("Generated on: " + java.time.LocalDate.now())
                .setFont(normal)
                .setFontSize(9)
                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(12));

        return container;
    }

    private com.itextpdf.layout.element.Cell groupHeaderCell(String text, PdfFont font, int colSpan) {
        return new com.itextpdf.layout.element.Cell(1, colSpan)
                .add(new Paragraph(text == null ? "" : text).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(6)
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.YELLOW)
                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.BLACK)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(0.6f));
    }

    private void addHeaderCell(Table table, String text, PdfFont font) {
        table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(6)
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.BLACK)
                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(0.5f)));
    }

    private void addUnitsCell(Table table, String text, PdfFont font) {
        table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(text == null ? "" : text).setFont(font).setFontSize(8))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(4)
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(0.5f)));
    }

    private void addBodyCell(Table table,
            String text,
            PdfFont font,
            TextAlignment align,
            com.itextpdf.kernel.colors.Color bg,
            boolean isTotal,
            boolean isSection) {

        Paragraph p = new Paragraph(text == null ? "" : text)
                .setFont(font)
                .setFontSize(8) // smaller font
                .setMargin(0) // remove paragraph margins
                .setMultipliedLeading(1); // minimal line spacing

        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                .add(p)
                .setTextAlignment(align)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPaddingTop(2) // 🔥 minimal padding
                .setPaddingBottom(2)
                .setPaddingLeft(4)
                .setPaddingRight(4)
                .setBorder(new com.itextpdf.layout.borders.SolidBorder(0.3f));

        if (bg != null) {
            cell.setBackgroundColor(bg);
        }

        if (isTotal) {
            cell.setBorder(new com.itextpdf.layout.borders.SolidBorder(0.8f));
        }
        if (isSection) {
            cell.setBorder(new com.itextpdf.layout.borders.SolidBorder(0.5f));
        }

        table.addCell(cell);
    }

    private void addCenteredLogo(Document document) throws IOException {

        InputStream is = getClass().getResourceAsStream("/images/urclogo2.png");
        if (is == null) {
            System.out.println("Its null");
            return;
        }

        byte[] imageBytes = is.readAllBytes();

        ImageData imageData = ImageDataFactory.create(imageBytes);
        com.itextpdf.layout.element.Image logo
                = new com.itextpdf.layout.element.Image(imageData);

        logo.scaleToFit(120, 60);
        logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        logo.setMarginBottom(8);
        System.out.println("Its not null");
        document.add(logo);
    }

    /*    private String safe(String s) {
    return s == null ? "" : s.trim();
    }*/
    private boolean isSectionLabel(String label) {
        String u = label.toUpperCase();
        return u.equals("RECURRENT INCOME")
                || u.equals("FREIGHT SERVICES")
                || u.equals("NORTHERN ROUTE")
                || u.equals("SOUTHERN ROUTE")
                || u.equals("OTHER FEES & CHARGES")
                || u.equals("RENT INCOME")
                || u.equals("NON RECURRENT INCOME")
                || u.equals("TOTAL NON RECURRENT INCOME")
                || u.equals("PASSENGERS TICKET SALES")
                || u.equals("MISCELLANEOUS INCOME");
    }

    private String formatDetailsLabel(String label) {
        // Simple indentation by known hierarchy (tweak if you have a true level field)
        String u = label.toUpperCase();
        if (u.equals("FREIGHT SERVICES")
                || u.equals("OTHER FEES & CHARGES")
                || u.equals("RENT INCOME")
                || u.equals("PASSENGERS TICKET SALES")
                || u.equals("NON RECURRENT INCOME")
                || u.equals("TOTAL NON RECURRENT INCOME")
                || u.equals("MISCELLANEOUS INCOME")) {
            return "  " + label;
        }
        if (u.equals("NORTHERN ROUTE") || u.equals("SOUTHERN ROUTE")) {
            return "    " + label;
        }
        if (u.startsWith("TOTAL")) {
            return label;
        }
        // leaf rows
        return "      " + label;
    }

    /**
     * Optional: indent based on known label hierarchy, since PerformanceRow
     * doesn't show level here. If you have a "level" field, use that instead.
     */
    private String formatDetailsLabel(String label, PerformanceRow row) {
        String u = label.toUpperCase();
        if (u.equals("NORTHERN ROUTE") || u.equals("SOUTHERN ROUTE")) {
            return "   " + label;
        }
        if (u.startsWith("TOTAL")) {
            return label;
        }
        // default
        return label;
    }

    private void generateFundSourceExpPdf(
            List<Organisation> organisations,
            Set<UrcDeptSectionAnlDimbgt> sections,
            BigDecimal totalIncome) throws IOException {

        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // ================= TITLE =================
        document.add(
                new Paragraph("URC Funding Sources – Income vs Expenditure")
                        .setFont(bold)
                        .setFontSize(16)
                        .setMarginBottom(15)
        );

        // ================= TABLE =================
        Table table = new Table(new float[]{4, 3, 3, 2});
        table.setWidth(UnitValue.createPercentValue(100));

        Style headerStyle = new Style()
                .setBackgroundColor(ColorConstants.YELLOW)
                .setFont(bold)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);

        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Fund Source")).addStyle(headerStyle));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Revenue (UGX)")).addStyle(headerStyle));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Budget (UGX)")).addStyle(headerStyle));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("% Contribution")).addStyle(headerStyle));

        // ================= DATA =================
        BigDecimal footerTotalRevenue = BigDecimal.ZERO;
        BigDecimal footerTotalBudget = BigDecimal.ZERO;

        for (Organisation org : organisations) {

            BigDecimal revenue = sampleBudgetItemsService
                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                            comboBox2.getValue(), sections, org);

            if (revenue == null || revenue.compareTo(BigDecimal.ZERO) == 0) {
                continue; // keep PDF consistent with grid
            }

            BigDecimal budget = sampleBudgetItemsService
                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByExp(
                            comboBox2.getValue(), sections, org);

            footerTotalRevenue = footerTotalRevenue.add(revenue);
            footerTotalBudget = footerTotalBudget.add(budget);

            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(org.getName()).setFont(normal)));
            table.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(formatCurrency(revenue)).setFont(normal))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(formatCurrency(budget)).setFont(normal))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(calculatePercentage(revenue, totalIncome)).setFont(normal))
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        // ================= FOOTER =================
        Style footerStyle = new Style()
                .setBackgroundColor(ColorConstants.YELLOW)
                .setFont(bold)
                .setPadding(5);

        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("TOTAL")).addStyle(footerStyle));
        table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(formatCurrency(footerTotalRevenue)))
                .addStyle(footerStyle)
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(formatCurrency(footerTotalBudget)))
                .addStyle(footerStyle)
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(
                        calculatePercentage(footerTotalRevenue, totalIncome)))
                .addStyle(footerStyle)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(table);
        document.close();

        // ================= DOWNLOAD =================
        StreamResource resource = new StreamResource(
                "URC_Funding_Sources_Income_vs_Expenditure.pdf",
                () -> new ByteArrayInputStream(baos.toByteArray())
        );

        Anchor downloadLink = new Anchor(resource, "");
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.add(new Button("Download PDF"));
        datasubLayout.add(downloadLink);
        // dataLayout.add(downloadLink);
    }

    private void generateFundSourceIncomeExcel(
            List<Organisation> organisations,
            Set<UrcDeptSectionAnlDimbgt> sections,
            BigDecimal grandTotalIncome) throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Funding Sources");

        CellStyle headerStyle = headerFooterStyle(wb);

        int rowIdx = 0;

        // ===== TITLE =====
        Row titleRow = sheet.createRow(rowIdx++);
        titleRow.createCell(0).setCellValue("URC Funding Sources");
        titleRow.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        rowIdx++;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Fund Source");
        header.createCell(1).setCellValue("Amount (UGX)");
        header.createCell(2).setCellValue("% Contribution");

        header.forEach(c -> c.setCellStyle(headerStyle));

        BigDecimal totalAmount = BigDecimal.ZERO;

        // ===== DATA =====
        for (Organisation org : organisations) {

            BigDecimal amount = sampleBudgetItemsService
                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                            comboBox2.getValue(), sections, org);

            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            totalAmount = totalAmount.add(amount);

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(org.getName());
            row.createCell(1).setCellValue(amount.doubleValue());
            row.createCell(2).setCellValue(
                    calculatePercentage(amount, grandTotalIncome)
            );
        }

        // ===== FOOTER =====
        Row footer = sheet.createRow(rowIdx);
        footer.createCell(0).setCellValue("TOTAL");
        footer.createCell(1).setCellValue(totalAmount.doubleValue());
        footer.createCell(2).setCellValue(
                calculatePercentage(totalAmount, grandTotalIncome)
        );

        footer.forEach(c -> c.setCellStyle(headerStyle));

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        // ===== DOWNLOAD =====
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        StreamResource resource = new StreamResource(
                "URC_Funding_Sources.xlsx",
                () -> new ByteArrayInputStream(baos.toByteArray())
        );

        Anchor download = new Anchor(resource, "Download Excel");
        download.getElement().setAttribute("download", true);
        datasubLayout.add(download);
        // dataLayout.add(download);
    }

    private void generateFundSourceIncomeExpExcel(
            List<Organisation> organisations,
            Set<UrcDeptSectionAnlDimbgt> sections,
            BigDecimal totalIncome) throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Income vs Expenditure");

        CellStyle headerStyle = headerFooterStyle(wb);

        int rowIdx = 0;

        // ===== TITLE =====
        Row titleRow = sheet.createRow(rowIdx++);
        titleRow.createCell(0).setCellValue("URC Funding Sources – Income vs Expenditure");
        titleRow.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        rowIdx++;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Fund Source");
        header.createCell(1).setCellValue("Revenue (UGX)");
        header.createCell(2).setCellValue("Budget (UGX)");
        header.createCell(3).setCellValue("% Contribution");

        header.forEach(c -> c.setCellStyle(headerStyle));

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalBudget = BigDecimal.ZERO;

        // ===== DATA =====
        for (Organisation org : organisations) {

            BigDecimal revenue = sampleBudgetItemsService
                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
                            comboBox2.getValue(), sections, org);

            if (revenue == null || revenue.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            BigDecimal budget = sampleBudgetItemsService
                    .calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByExp(
                            comboBox2.getValue(), sections, org);

            totalRevenue = totalRevenue.add(revenue);
            totalBudget = totalBudget.add(budget);

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(org.getName());
            row.createCell(1).setCellValue(revenue.doubleValue());
            row.createCell(2).setCellValue(budget.doubleValue());
            row.createCell(3).setCellValue(
                    calculatePercentage(revenue, totalIncome)
            );
        }

        // ===== FOOTER =====
        Row footer = sheet.createRow(rowIdx);
        footer.createCell(0).setCellValue("TOTAL");
        footer.createCell(1).setCellValue(totalRevenue.doubleValue());
        footer.createCell(2).setCellValue(totalBudget.doubleValue());
        footer.createCell(3).setCellValue(
                calculatePercentage(totalRevenue, totalIncome)
        );

        footer.forEach(c -> c.setCellStyle(headerStyle));

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        // ===== DOWNLOAD =====
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        StreamResource resource = new StreamResource(
                "URC_Funding_Sources_Income_vs_Expenditure.xlsx",
                () -> new ByteArrayInputStream(baos.toByteArray())
        );

        Anchor download = new Anchor(resource, "Download Excel");
        download.getElement().setAttribute("download", true);
        dataLayout.add(download);
    }

    private CellStyle headerFooterStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void configureFundSourceView() {
        radioGroup.setLabel("Status");
        radioGroup.setItems("View With Budget", "View Without Budget");
        radioGroup.setValue("View Without Budget");

        radioGroup.addValueChangeListener(e -> {
            topsub.removeAll();
            if (e.getValue().equals("View With Budget")) {
                topsub.add(withBgtpdfBtn, withBgtexcelBtn);
                refreshAccordionWithBudget();
            } else {
                topsub.add(refresh, pdfBtn, excelBtn);
                refreshAccordion();
            }
        });
    }

    private void configureBudgetSelector() {

        pdfBtn.setEnabled(false);
        excelBtn.setEnabled(false);
        comboBox2.addValueChangeListener(e -> {
            refreshAccordion();
            if (e.getValue() == null) {
                pdfBtn.setEnabled(false);
                excelBtn.setEnabled(false);
                withBgtpdfBtn.setEnabled(false);
                withBgtexcelBtn.setEnabled(false);
            } else {
                pdfBtn.setEnabled(true);
                excelBtn.setEnabled(true);
                withBgtpdfBtn.setEnabled(true);
                withBgtexcelBtn.setEnabled(true);
            }
        });
    }

    private HorizontalLayout buildTopBar() {
        refresh.addClickListener(e -> refreshAccordion());

        pdfDownloadAnchor.setText(""); // no visible text
        pdfDownloadAnchor.getStyle().set("display", "none"); // hide it

        pdfBtn.addClickListener(e -> {
            StreamResource pdf = new StreamResource("Qtr-Releases.pdf", () -> {
                byte[] bytes = qtrReleasesReport.buildPdfItext7ByFundSourceByBudget(
                        comboBox2.getValue(),
                        budgetType2.getSelectedItems(), user
                );
                return new ByteArrayInputStream(bytes);
            });

            pdfDownloadAnchor.setHref(pdf);
            pdfDownloadAnchor.getElement().setAttribute("download", true);

            // trigger click
            pdfDownloadAnchor.getElement().callJsFunction("click");
        });

        withBgtpdfBtn.addClickListener(e -> {
            StreamResource pdf = new StreamResource("Qtr-Releases.pdf", () -> {
                byte[] bytes = qtrReleasesReport.buildPdfItext7ByFundSourceByBudgetwithBgt(
                        comboBox2.getValue(),
                        budgetType2.getSelectedItems(), user
                );
                return new ByteArrayInputStream(bytes);
            });

            pdfDownloadAnchor.setHref(pdf);
            pdfDownloadAnchor.getElement().setAttribute("download", true);

            // trigger click
            pdfDownloadAnchor.getElement().callJsFunction("click");
        });

        excelBtn.addClickListener(e -> {
            StreamResource pdf = new StreamResource("Qtrly-Releases.xlsx", () -> {
                byte[] bytes = qtrReleasesReport.buildExcelByFundSourceByBudget(
                        comboBox2.getValue(),
                        budgetType2.getSelectedItems(), user
                );
                return new ByteArrayInputStream(bytes);
            });

            pdfDownloadAnchor.setHref(pdf);
            pdfDownloadAnchor.getElement().setAttribute("download", true);

            // trigger click
            pdfDownloadAnchor.getElement().callJsFunction("click");
        });

        withBgtexcelBtn.addClickListener(e -> {
            StreamResource pdf = new StreamResource("Qtrly-Releases.xlsx", () -> {
                byte[] bytes = qtrReleasesReport.buildExcelByFundSourceByBudgetwithBgt(
                        comboBox2.getValue(),
                        budgetType2.getSelectedItems(), user
                );
                return new ByteArrayInputStream(bytes);
            });

            pdfDownloadAnchor.setHref(pdf);
            pdfDownloadAnchor.getElement().setAttribute("download", true);

            // trigger click
            pdfDownloadAnchor.getElement().callJsFunction("click");
        });
        topsub.add(refresh, pdfBtn, excelBtn);
        HorizontalLayout top = new HorizontalLayout(radioGroup, pdfDownloadAnchor, topsub);
        top.setWidthFull();
        top.setAlignItems(FlexComponent.Alignment.END);
        return top;
    }

    private void refreshAccordion() {
        accordion.getChildren().forEach(component -> component.removeFromParent());

        Budget budget = comboBox2.getValue();
        if (budget == null) {
            Notification.show("Select a Budget first.");
            return;
        }

        List<Organisation> organisations = sampleOrganisationService.getOrganisationsByBudget(budget);
        if (organisations.isEmpty()) {
            Notification.show("No organisations found.");
            return;
        }

        for (Organisation org : organisations) {
            VerticalLayout content = new VerticalLayout();
            content.setPadding(false);
            content.setSpacing(false);
            content.setWidthFull();

            Grid<QtrReleaseRow> grid = buildDeptSectionGrid(org, budget);
            content.add(grid);

            AccordionPanel panel = accordion.add(org.getName() + " (" + org.getCode() + ")", content);
            panel.addOpenedChangeListener(event -> {
                if (event.isOpened()) {
                    // refresh grid data when opened (latest DB values)

                    List<QtrReleaseRow> rows = loadRows(org, budget);

                    BigDecimal q1Sum = rows.stream().map(QtrReleaseRow::getQtr1Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q2Sum = rows.stream().map(QtrReleaseRow::getQtr2Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q3Sum = rows.stream().map(QtrReleaseRow::getQtr3Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q4Sum = rows.stream().map(QtrReleaseRow::getQtr4Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalAll = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);

// --- Create a “footer” row ---
                    UrcDeptSectionAnlDimbgt tot = new UrcDeptSectionAnlDimbgt();
                    tot.setNAME("TOTAL");
                    QtrReleaseRow footerRow = new QtrReleaseRow(tot);
                    //footerRow.setDeptSectionName("Total");   // or blank
                    footerRow.setQtr1Release(q1Sum);
                    footerRow.setQtr2Release(q2Sum);
                    footerRow.setQtr3Release(q3Sum);
                    footerRow.setQtr4Release(q4Sum);
                    footerRow.setTotalRow(true);
                    //footerRow.setTotalReleased(totalAll);

                    rows.add(footerRow);
                    grid.setItems(rows);
                }
            });

            // panel.close();
        }
    }

    private void refreshAccordionWithBudget() {
        accordion.getChildren().forEach(component -> component.removeFromParent());

        Budget budget = comboBox2.getValue();
        if (budget == null) {
            Notification.show("Select a Budget first.");
            return;
        }

        List<Organisation> organisations = sampleOrganisationService.getOrganisationsByBudget(budget);
        if (organisations.isEmpty()) {
            Notification.show("No organisations found.");
            return;
        }

        for (Organisation org : organisations) {
            VerticalLayout content = new VerticalLayout();
            content.setPadding(false);
            content.setSpacing(false);
            content.setWidthFull();

            Grid<QtrReleaseRow> grid = buildBgtDeptSectionGrid(org, budget);
            content.add(grid);

            AccordionPanel panel = accordion.add(org.getName() + " (" + org.getCode() + ")", content);
            panel.addOpenedChangeListener(event -> {
                if (event.isOpened()) {
                    // refresh grid data when opened (latest DB values)

                    List<QtrReleaseRow> rows = loadRows(org, budget);
                    for (QtrReleaseRow row : rows) {
                        fillQuarterBudgets(row, org, budget); // sets q1Budget..q4Budget on each row
                    }
                    BigDecimal q1Sum = rows.stream().map(QtrReleaseRow::getQtr1Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q2Sum = rows.stream().map(QtrReleaseRow::getQtr2Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q3Sum = rows.stream().map(QtrReleaseRow::getQtr3Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q4Sum = rows.stream().map(QtrReleaseRow::getQtr4Release).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalAll = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);

                    BigDecimal q1BgtSum = rows.stream().map(QtrReleaseRow::getQ1Budget).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q2BgtSum = rows.stream().map(QtrReleaseRow::getQ2Budget).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q3BgtSum = rows.stream().map(QtrReleaseRow::getQ3Budget).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal q4BgtSum = rows.stream().map(QtrReleaseRow::getQ4Budget).reduce(BigDecimal.ZERO, BigDecimal::add);

// --- Create a “footer” row ---
                    UrcDeptSectionAnlDimbgt tot = new UrcDeptSectionAnlDimbgt();
                    tot.setNAME("TOTAL");
                    QtrReleaseRow footerRow = new QtrReleaseRow(tot);
                    //footerRow.setDeptSectionName("Total");   // or blank
                    footerRow.setQtr1Release(q1Sum);
                    footerRow.setQtr2Release(q2Sum);
                    footerRow.setQtr3Release(q3Sum);
                    footerRow.setQtr4Release(q4Sum);
                    footerRow.setTotalRow(true);

                    footerRow.setQ1Budget(q1BgtSum);
                    footerRow.setQ2Budget(q2BgtSum);
                    footerRow.setQ3Budget(q3BgtSum);
                    footerRow.setQ4Budget(q4BgtSum);
                    //footerRow.setTotalReleased(totalAll);

                    rows.add(footerRow);
                    grid.setItems(rows);
                }
            });

            // panel.close();
        }
    }

    private Grid<QtrReleaseRow> buildDeptSectionGrid(Organisation org, Budget budget) {
        Grid<QtrReleaseRow> grid = new Grid<>(QtrReleaseRow.class, false);
        grid.setWidthFull();
        grid.setHeight("450px");
        grid.setAllRowsVisible(false);

        // Load data initially
        List<QtrReleaseRow> rows = loadRows(org, budget);

        BigDecimal q1Sum = rows.stream().map(QtrReleaseRow::getQtr1Release).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal q2Sum = rows.stream().map(QtrReleaseRow::getQtr2Release).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal q3Sum = rows.stream().map(QtrReleaseRow::getQtr3Release).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal q4Sum = rows.stream().map(QtrReleaseRow::getQtr4Release).reduce(BigDecimal.ZERO, BigDecimal::add);

// --- Create a “footer” row ---
        UrcDeptSectionAnlDimbgt tot = new UrcDeptSectionAnlDimbgt();
        tot.setNAME("TOTAL");
        QtrReleaseRow footerRow = new QtrReleaseRow(tot);
        //footerRow.setDeptSectionName("Total");   // or blank
        footerRow.setQtr1Release(q1Sum);
        footerRow.setQtr2Release(q2Sum);
        footerRow.setQtr3Release(q3Sum);
        footerRow.setQtr4Release(q4Sum);
        footerRow.setTotalRow(true);
        rows.add(footerRow);
        grid.setItems(rows);
        // Binder + Editor (inline edit)
        Binder<QtrReleaseRow> binder = new Binder<>(QtrReleaseRow.class);
        Editor<QtrReleaseRow> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        // Columns
        grid.addColumn(row -> row.getDeptSectionName())
                .setHeader("Dept/Section")
                .setAutoWidth(true)
                .setFlexGrow(1);

        // Editable numeric fields
        BigDecimalField q1Field = moneyEditorField();
        BigDecimalField q2Field = moneyEditorField();
        BigDecimalField q3Field = moneyEditorField();
        BigDecimalField q4Field = moneyEditorField();

        Grid.Column<QtrReleaseRow> q1Col = grid.addColumn(row -> formatBigDecimal(row.getQtr1Release()))
                .setHeader("Q1 Release")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setEditorComponent(q1Field);

        Grid.Column<QtrReleaseRow> q2Col = grid.addColumn(row -> formatBigDecimal(row.getQtr2Release()))
                .setHeader("Q2 Release")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setEditorComponent(q2Field);

        Grid.Column<QtrReleaseRow> q3Col = grid.addColumn(row -> formatBigDecimal(row.getQtr3Release()))
                .setHeader("Q3 Release")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setEditorComponent(q3Field);

        grid.addColumn(QtrReleaseRow::getQtr4Release)
                .setHeader("Q4 Release")
                .setAutoWidth(true)
                .setEditorComponent(q4Field);

        // Editable reasons
        TextArea r1Field = reasonEditorField();
        TextArea r2Field = reasonEditorField();
        TextArea r3Field = reasonEditorField();
        TextArea r4Field = reasonEditorField();

        // Convenience total column per row
        Grid.Column<QtrReleaseRow> totalCol = grid.addColumn(new ComponentRenderer<>(row -> {
            Span s = new Span(formatBigDecimal(
                    nvl(row.getQtr1Release())
                            .add(nvl(row.getQtr2Release()))
                            .add(nvl(row.getQtr3Release()))
                            .add(nvl(row.getQtr4Release()))
            ));
            s.getStyle().set("font-weight", "600"); // bold
            return s;
        }))
                .setHeader("Total Released")
                .setAutoWidth(true);

        // Bind editor fields
        binder.forField(q1Field).bind(QtrReleaseRow::getQtr1Release, QtrReleaseRow::setQtr1Release);
        binder.forField(q2Field).bind(QtrReleaseRow::getQtr2Release, QtrReleaseRow::setQtr2Release);
        binder.forField(q3Field).bind(QtrReleaseRow::getQtr3Release, QtrReleaseRow::setQtr3Release);
        binder.forField(q4Field).bind(QtrReleaseRow::getQtr4Release, QtrReleaseRow::setQtr4Release);

        binder.forField(r1Field).bind(QtrReleaseRow::getReasonsForUnderOver1, QtrReleaseRow::setReasonsForUnderOver1);
        binder.forField(r2Field).bind(QtrReleaseRow::getReasonsForUnderOver2, QtrReleaseRow::setReasonsForUnderOver2);
        binder.forField(r3Field).bind(QtrReleaseRow::getReasonsForUnderOver3, QtrReleaseRow::setReasonsForUnderOver3);
        binder.forField(r4Field).bind(QtrReleaseRow::getReasonsForUnderOver4, QtrReleaseRow::setReasonsForUnderOver4);

        // Set items
        // grid.setItems(displayRows);
// --- Optional: style footer differently ---
        grid.setClassNameGenerator(row -> {
            if ("Total".equals(row.getDeptSectionName())) {
                return "totals-row"; // define CSS class
            }
            return null;
        });

        // Action buttons column
        grid.addColumn(new ComponentRenderer<>(row -> {
            Button edit = new Button("Edit");
            Button save = new Button("Save");
            Button cancel = new Button("Cancel");

            boolean isFooter = row.isTotalRow();
            boolean isAdmin = user.getRoles().contains(Role.ADMIN);
            boolean isoff = true;
            if (isFooter == true || isAdmin == false) {
                isoff = false;
            }

            edit.setEnabled(isoff);
            save.setEnabled(isoff);
            cancel.setEnabled(isoff);

            edit.addClickListener(e -> {
                if (editor.isOpen()) {
                    editor.cancel();
                }
                editor.editItem(row);
            });

            save.addClickListener(e -> {
                try {
                    editor.save();

                    // Persist the row -> QtrReleases
                    QtrReleases entity = row.toEntity(org, budget);
                    qtrReleasesService.save(entity);
                    refreshAccordion();

                    Notification n = Notification.show(
                            "Saved: " + row.getDeptSectionName(),
                            2500,
                            Notification.Position.TOP_END
                    );
                    n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                } catch (Exception ex) {
                    Notification n = Notification.show(
                            "Save failed: " + ex.getMessage(),
                            4000,
                            Notification.Position.TOP_END
                    );
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });

            cancel.addClickListener(e -> editor.cancel());

            HorizontalLayout actions = new HorizontalLayout(edit, save, cancel);
            actions.setPadding(false);
            actions.setSpacing(true);
            return actions;
        }))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        return grid;
    }

    private Grid<QtrReleaseRow> buildBgtDeptSectionGrid(Organisation org, Budget budget) {

        Grid<QtrReleaseRow> grid = new Grid<>(QtrReleaseRow.class, false);
        grid.setWidthFull();
        grid.setHeight("520px");
        grid.setAllRowsVisible(false);

        // -----------------------------
        // Load rows + fill quarter budgets
        // -----------------------------
        List<QtrReleaseRow> rows = loadRows(org, budget);
        for (QtrReleaseRow row : rows) {
            fillQuarterBudgets(row, org, budget); // sets q1Budget..q4Budget on each row
        }

        // -----------------------------
        // Compute totals (Releases + Budgets)
        // -----------------------------
        BigDecimal q1RelSum = BigDecimal.ZERO;
        BigDecimal q2RelSum = BigDecimal.ZERO;
        BigDecimal q3RelSum = BigDecimal.ZERO;
        BigDecimal q4RelSum = BigDecimal.ZERO;

        BigDecimal q1BudSum = BigDecimal.ZERO;
        BigDecimal q2BudSum = BigDecimal.ZERO;
        BigDecimal q3BudSum = BigDecimal.ZERO;
        BigDecimal q4BudSum = BigDecimal.ZERO;

        for (QtrReleaseRow r : rows) {
            q1RelSum = q1RelSum.add(nvl(r.getQtr1Release()));
            q2RelSum = q2RelSum.add(nvl(r.getQtr2Release()));
            q3RelSum = q3RelSum.add(nvl(r.getQtr3Release()));
            q4RelSum = q4RelSum.add(nvl(r.getQtr4Release()));

            q1BudSum = q1BudSum.add(nvl(r.getQ1Budget()));
            q2BudSum = q2BudSum.add(nvl(r.getQ2Budget()));
            q3BudSum = q3BudSum.add(nvl(r.getQ3Budget()));
            q4BudSum = q4BudSum.add(nvl(r.getQ4Budget()));
        }

        // -----------------------------
        // Footer (TOTAL) row
        // -----------------------------
        UrcDeptSectionAnlDimbgt tot = new UrcDeptSectionAnlDimbgt();
        tot.setNAME("TOTAL");

        QtrReleaseRow footerRow = new QtrReleaseRow(tot);
        footerRow.setTotalRow(true);

        // totals for releases
        footerRow.setQtr1Release(q1RelSum);
        footerRow.setQtr2Release(q2RelSum);
        footerRow.setQtr3Release(q3RelSum);
        footerRow.setQtr4Release(q4RelSum);

        // totals for budgets
        footerRow.setQ1Budget(q1BudSum);
        footerRow.setQ2Budget(q2BudSum);
        footerRow.setQ3Budget(q3BudSum);
        footerRow.setQ4Budget(q4BudSum);

        rows.add(footerRow);
        grid.setItems(rows);

        // -----------------------------
        // Binder + Editor (inline edit)
        // -----------------------------
        Binder<QtrReleaseRow> binder = new Binder<>(QtrReleaseRow.class);
        Editor<QtrReleaseRow> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);

        // prevent editing TOTAL row
        /*        editor.addOpenListener(e -> {
        e.getItem().setRowUser(isAdmin);
        if ((e.getItem() != null && e.getItem().isTotalRow()) || isAdmin == true) {
        editor.cancel();
        }
        });*/
        // -----------------------------
        // Columns
        // -----------------------------
        grid.addColumn(QtrReleaseRow::getDeptSectionName)
                .setHeader("Dept/Section")
                .setAutoWidth(true)
                .setFlexGrow(1);

        // Quarter budgets (read-only)
        grid.addColumn(row -> formatBigDecimal(row.getQ1Budget()))
                .setHeader("Q1 Budget")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.addColumn(row -> formatBigDecimal(row.getQ2Budget()))
                .setHeader("Q2 Budget")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.addColumn(row -> formatBigDecimal(row.getQ3Budget()))
                .setHeader("Q3 Budget")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.addColumn(row -> formatBigDecimal(row.getQ4Budget()))
                .setHeader("Q4 Budget")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        // Editable release fields
        BigDecimalField q1Field = moneyEditorField();
        BigDecimalField q2Field = moneyEditorField();
        BigDecimalField q3Field = moneyEditorField();
        BigDecimalField q4Field = moneyEditorField();

        Grid.Column<QtrReleaseRow> q1RelCol = grid.addColumn(row -> formatBigDecimal(row.getQtr1Release()))
                .setHeader("Q1 Release")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setEditorComponent(q1Field);

        Grid.Column<QtrReleaseRow> q2RelCol = grid.addColumn(row -> formatBigDecimal(row.getQtr2Release()))
                .setHeader("Q2 Release")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setEditorComponent(q2Field);

        Grid.Column<QtrReleaseRow> q3RelCol = grid.addColumn(row -> formatBigDecimal(row.getQtr3Release()))
                .setHeader("Q3 Release")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setEditorComponent(q3Field);

        Grid.Column<QtrReleaseRow> q4RelCol = grid.addColumn(row -> formatBigDecimal(row.getQtr4Release()))
                .setHeader("Q4 Release")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setEditorComponent(q4Field);

        // Total Released per row
        grid.addColumn(new ComponentRenderer<>(row -> {
            BigDecimal totalReleased
                    = nvl(row.getQtr1Release())
                            .add(nvl(row.getQtr2Release()))
                            .add(nvl(row.getQtr3Release()))
                            .add(nvl(row.getQtr4Release()));

            Span s = new Span(formatBigDecimal(totalReleased));
            s.getStyle().set("font-weight", "600");
            return s;
        }))
                .setHeader("Total Released")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        // Optional: Total Budget per row
        grid.addColumn(new ComponentRenderer<>(row -> {
            BigDecimal totalBudget
                    = nvl(row.getQ1Budget())
                            .add(nvl(row.getQ2Budget()))
                            .add(nvl(row.getQ3Budget()))
                            .add(nvl(row.getQ4Budget()));

            Span s = new Span(formatBigDecimal(totalBudget));
            s.getStyle().set("font-weight", "600");
            return s;
        }))
                .setHeader("Total Budget")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        // Optional: Variance (Released - Budget)
        grid.addColumn(new ComponentRenderer<>(row -> {
            BigDecimal released
                    = nvl(row.getQtr1Release())
                            .add(nvl(row.getQtr2Release()))
                            .add(nvl(row.getQtr3Release()))
                            .add(nvl(row.getQtr4Release()));

            BigDecimal budgeted
                    = nvl(row.getQ1Budget())
                            .add(nvl(row.getQ2Budget()))
                            .add(nvl(row.getQ3Budget()))
                            .add(nvl(row.getQ4Budget()));

            BigDecimal variance = released.subtract(budgeted);

            Span s = new Span(formatBigDecimal(variance));
            s.getStyle().set("font-weight", "600");
            return s;
        }))
                .setHeader("Variance")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        /*        grid.addItemDoubleClickListener(e -> {
        QtrReleaseRow row = e.getItem();
        if (!isAdmin && !row.isTotalRow()) {
        editor.editItem(row);
        }
        });*/
        // -----------------------------
        // Bind editor fields (releases only)
        // -----------------------------
        binder.forField(q1Field).bind(QtrReleaseRow::getQtr1Release, QtrReleaseRow::setQtr1Release);
        binder.forField(q2Field).bind(QtrReleaseRow::getQtr2Release, QtrReleaseRow::setQtr2Release);
        binder.forField(q3Field).bind(QtrReleaseRow::getQtr3Release, QtrReleaseRow::setQtr3Release);
        binder.forField(q4Field).bind(QtrReleaseRow::getQtr4Release, QtrReleaseRow::setQtr4Release);

        // -----------------------------
        // Style TOTAL row
        // -----------------------------
        grid.setClassNameGenerator(row -> row.isTotalRow() ? "totals-row" : null);
        return grid;
    }

    private List<QtrReleaseRow> loadRows(Organisation organisation, Budget budget) {
        //List<UrcDeptSectionAnlDimbgt> sections = sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims();
        List<UrcDeptSectionAnlDimbgt> sections = new ArrayList();
        if (user.getRoles().contains(Role.ADMIN)) {
            sections = sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims();
        } else {
            sections = user.getDeptsection().stream().toList();
        }

        // Load existing QtrReleases for this organisation+budget (for all deptSections)
        List<QtrReleases> existing = qtrReleasesService.findByOrganisationAndBudget(organisation, budget);

        Map<String, QtrReleases> bySectionId = existing.stream()
                .filter(x -> x.getDeptSection() != null && x.getDeptSection().getANL_CODE() != null)
                .collect(Collectors.toMap(
                        x -> x.getDeptSection().getANL_CODE(),
                        x -> x,
                        (a, b) -> a // keep first if duplicates exist
                ));

        List<QtrReleaseRow> rows = new ArrayList<>();

        for (UrcDeptSectionAnlDimbgt section : sections) {
            QtrReleases qtr = bySectionId.get(section.getANL_CODE());

            QtrReleaseRow row = new QtrReleaseRow(section);

            if (qtr != null) {
                row.setId(qtr.getId());
                row.setQtr1Release(nvl(qtr.getQtr1Release()));
                row.setQtr2Release(nvl(qtr.getQtr2Release()));
                row.setQtr3Release(nvl(qtr.getQtr3Release()));
                row.setQtr4Release(nvl(qtr.getQtr4Release()));

                row.setReasonsForUnderOver1(nullToEmpty(qtr.getReasonsForUnderOver1()));
                row.setReasonsForUnderOver2(nullToEmpty(qtr.getReasonsForUnderOver2()));
                row.setReasonsForUnderOver3(nullToEmpty(qtr.getReasonsForUnderOver3()));
                row.setReasonsForUnderOver4(nullToEmpty(qtr.getReasonsForUnderOver4()));
            }

            rows.add(row);
        }

        return rows;
    }

    private BigDecimalField moneyEditorField() {
        BigDecimalField f = new BigDecimalField();
        f.setWidthFull();
        f.setClearButtonVisible(true);

// Set minimum value using a validator
        f.setValue(BigDecimal.ZERO);
        f.addValueChangeListener(event -> {
            BigDecimal value = event.getValue();
            if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
                f.setValue(BigDecimal.ZERO); // enforce min
            }
        });
        return f;
    }

    private TextArea reasonEditorField() {
        TextArea t = new TextArea();
        t.setWidthFull();
        t.setMaxLength(500);
        t.getStyle().set("min-width", "220px");
        return t;
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    // ---------------------------
    // Row DTO used by the Grid
    // ---------------------------
    @Getter
    public static class QtrReleaseRow {

        private Long id;
        private boolean rowUser;

        private boolean totalRow;

        public boolean isTotalRow() {
            return totalRow;
        }

        public void setTotalRow(boolean totalRow) {
            this.totalRow = totalRow;
        }

        public void setRowUser(boolean rowUser) {
            this.rowUser = rowUser;
        }

        private final UrcDeptSectionAnlDimbgt deptSection;

        private BigDecimal qtr1Release = BigDecimal.ZERO;
        private BigDecimal qtr2Release = BigDecimal.ZERO;
        private BigDecimal qtr3Release = BigDecimal.ZERO;
        private BigDecimal qtr4Release = BigDecimal.ZERO;

        private BigDecimal q1Budget = BigDecimal.ZERO;
        private BigDecimal q2Budget = BigDecimal.ZERO;
        private BigDecimal q3Budget = BigDecimal.ZERO;
        private BigDecimal q4Budget = BigDecimal.ZERO;

        private BigDecimal q1Act = BigDecimal.ZERO;
        private BigDecimal q2Act = BigDecimal.ZERO;
        private BigDecimal q3Act = BigDecimal.ZERO;
        private BigDecimal q4Act = BigDecimal.ZERO;

        private String reasonsForUnderOver1 = "";
        private String reasonsForUnderOver2 = "";
        private String reasonsForUnderOver3 = "";
        private String reasonsForUnderOver4 = "";

        public QtrReleaseRow(UrcDeptSectionAnlDimbgt deptSection) {
            this.deptSection = deptSection;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDeptSectionName() {
            // adjust based on your entity fields
            if (deptSection == null) {
                return "";
            }
            if (deptSection.getNAME() != null) {
                return deptSection.getNAME();
            }
            return "Section #" + deptSection.getANL_CODE();
        }

        public BigDecimal getQtr1Release() {
            return qtr1Release;
        }

        public BigDecimal getQtr2Release() {
            return qtr2Release;
        }

        public BigDecimal getQtr3Release() {
            return qtr3Release;
        }

        public BigDecimal getQtr4Release() {
            return qtr4Release;
        }

        public void setQtr1Release(BigDecimal v) {
            this.qtr1Release = nvl(v);
        }

        public void setQtr2Release(BigDecimal v) {
            this.qtr2Release = nvl(v);
        }

        public void setQtr3Release(BigDecimal v) {
            this.qtr3Release = nvl(v);
        }

        public void setQtr4Release(BigDecimal v) {
            this.qtr4Release = nvl(v);
        }

        public String getReasonsForUnderOver1() {
            return reasonsForUnderOver1;
        }

        public String getReasonsForUnderOver2() {
            return reasonsForUnderOver2;
        }

        public String getReasonsForUnderOver3() {
            return reasonsForUnderOver3;
        }

        public String getReasonsForUnderOver4() {
            return reasonsForUnderOver4;
        }

        public void setReasonsForUnderOver1(String s) {
            this.reasonsForUnderOver1 = emptyToEmpty(s);
        }

        public void setReasonsForUnderOver2(String s) {
            this.reasonsForUnderOver2 = emptyToEmpty(s);
        }

        public void setReasonsForUnderOver3(String s) {
            this.reasonsForUnderOver3 = emptyToEmpty(s);
        }

        public void setReasonsForUnderOver4(String s) {
            this.reasonsForUnderOver4 = emptyToEmpty(s);
        }

        public BigDecimal getQ1Budget() {
            return q1Budget;
        }

        public BigDecimal getQ2Budget() {
            return q2Budget;
        }

        public BigDecimal getQ3Budget() {
            return q3Budget;
        }

        public BigDecimal getQ4Budget() {
            return q4Budget;
        }

        public void setQ1Budget(BigDecimal v) {
            this.q1Budget = nvl(v);
        }

        public void setQ2Budget(BigDecimal v) {
            this.q2Budget = nvl(v);
        }

        public void setQ3Budget(BigDecimal v) {
            this.q3Budget = nvl(v);
        }

        public void setQ4Budget(BigDecimal v) {
            this.q4Budget = nvl(v);
        }

        public void setQ1Act(BigDecimal q1Act) {
            this.q1Act = q1Act;
        }

        public void setQ2Act(BigDecimal q2Act) {
            this.q2Act = q2Act;
        }

        public void setQ3Act(BigDecimal q3Act) {
            this.q3Act = q3Act;
        }

        public void setQ4Act(BigDecimal q4Act) {
            this.q4Act = q4Act;
        }

        public QtrReleases toEntity(Organisation organisation, Budget budget) {
            return QtrReleases.builder()
                    .id(this.id) // important for update
                    .organisation(organisation)
                    .budget(budget)
                    .deptSection(this.deptSection)
                    .qtr1Release(nvl(qtr1Release))
                    .qtr2Release(nvl(qtr2Release))
                    .qtr3Release(nvl(qtr3Release))
                    .qtr4Release(nvl(qtr4Release))
                    .reasonsForUnderOver1(emptyToNull(reasonsForUnderOver1))
                    .reasonsForUnderOver2(emptyToNull(reasonsForUnderOver2))
                    .reasonsForUnderOver3(emptyToNull(reasonsForUnderOver3))
                    .reasonsForUnderOver4(emptyToNull(reasonsForUnderOver4))
                    .build();
        }

        private static BigDecimal nvl(BigDecimal v) {
            return v == null ? BigDecimal.ZERO : v;
        }

        private static String emptyToEmpty(String s) {
            return s == null ? "" : s;
        }

        private static String emptyToNull(String s) {
            if (s == null) {
                return null;
            }
            String t = s.trim();
            return t.isEmpty() ? null : t;
        }
    }

    private void fillQuarterBudgets(QtrReleaseRow row, Organisation org, Budget budget) {

        if (row == null || row.getDeptSection() == null) {
            return;
        }

        List<QuarterBudgetSum> sum
                = sampleBudgetItemsService.sumQuarterBudgetsByDept(
                        budget,
                        org,
                        row.getDeptSection()
                );
        for (QuarterBudgetSum val : sum) {
            row.setQ1Budget(val.q1());
            row.setQ2Budget(val.q2());
            row.setQ3Budget(val.q3());
            row.setQ4Budget(val.q4());
            System.out.println(val.q1() + " .. " + val.q2() + " .. " + val.q3() + " .. " + val.q4() + " .. ");
        }

    }

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) {
            return BigDecimal.ZERO;
        }
        if (o instanceof BigDecimal bd) {
            return bd;
        }
        if (o instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return new BigDecimal(o.toString());
    }

    public Set<Integer> getFinancialYearPeriods(Budget budget) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        for (int i = 1; i <= 12; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

    public Set<Integer> getFinancialYearPeriods(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        int start = 0;
        switch (quarter) {
            case 1:
                start = 1;
                break;
            case 2:
                start = 4;
                break;
            case 3:
                start = 7;
                break;
            case 4:
                start = 10;
                break;
            default:
                break;
        }
        for (int i = start; i <= 12; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

    public Set<Integer> getFinancialYearPeriodsCummulative(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        int stop = 0;
        switch (quarter) {
            case 1:
                stop = 3;
                break;
            case 2:
                stop = 6;
                break;
            case 3:
                stop = 9;
                break;
            case 4:
                stop = 12;
                break;
            default:
                break;
        }
        for (int i = 1; i <= stop; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

    private byte[] generateDepartmentWorkplansExcel(
            Budget budget,
            Set<Organisation> selectedOrganisations,
            List<DepartmentBudget> departmentBudgets
    ) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            WorkplanStyles styles = createWorkplanStyles(workbook);

            List<DepartmentBudget> validDepartments = departmentBudgets == null
                    ? Collections.emptyList()
                    : departmentBudgets.stream()
                            .filter(dept -> dept != null
                            && dept.getSections() != null
                            && !dept.getSections().isEmpty())
                            .toList();

            for (DepartmentBudget department : validDepartments) {
                String rawSheetName = department.getDepartmentName() != null
                        ? department.getDepartmentName()
                        : "Department";

                String sheetName = WorkbookUtil.createSafeSheetName(rawSheetName);
                Sheet sheet = workbook.createSheet(sheetName);

                createHeaderRowWorkplanQtr2ForDepartmentCombined(
                        workbook,
                        sheet,
                        budget,
                        selectedOrganisations,
                        department,
                        0,
                        styles
                );
            }

            if (!validDepartments.isEmpty()) {
                Sheet combinedSheet = workbook.createSheet(
                        WorkbookUtil.createSafeSheetName("All Departments")
                );

                createCombinedDepartmentWorkplanSheet(
                        workbook,
                        combinedSheet,
                        budget,
                        selectedOrganisations,
                        validDepartments,
                        styles
                );
            }

            if (workbook.getNumberOfSheets() == 0) {
                Sheet sheet = workbook.createSheet("Workplan");
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("No department workplans available");
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Failed to generate department workplans Excel", ex);
        }
    }

    private byte[] generateProcurementPlansExcel(
            Budget budget,
            Set<Organisation> selectedOrganisations,
            List<DepartmentBudget> departmentBudgets
    ) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            WorkplanStyles styles = createWorkplanStyles(workbook);

            List<DepartmentBudget> validDepartments = departmentBudgets == null
                    ? Collections.emptyList()
                    : departmentBudgets.stream()
                            .filter(dept -> dept != null
                            && dept.getSections() != null
                            && !dept.getSections().isEmpty())
                            .toList();

            // =========================
            // 1. DEPARTMENT SHEETS
            // =========================
            for (DepartmentBudget department : validDepartments) {

                String rawSheetName = department.getDepartmentName() != null
                        ? department.getDepartmentName()
                        : "Department";

                String sheetName = WorkbookUtil.createSafeSheetName(rawSheetName);
                Sheet sheet = workbook.createSheet(sheetName);

                int rowPointer = 0;
                boolean hasAnyTable = false;

                for (ProcClass procClass : ProcClass.values()) {

                    List<BudgetItems> procItems
                            = sampleBudgetItemsService.findByBudgetAndBudgetTypesAndDeptUnitsAndProcClass(
                                    budget,
                                    selectedOrganisations,
                                    department.getSections(),
                                    procClass
                            );

                    if (procItems == null || procItems.isEmpty()) {
                        continue;
                    }

                    hasAnyTable = true;

                    DepartmentTotals totals = createDepartmentalProcurementPlanDetailed(
                            workbook,
                            sheet,
                            budget,
                            selectedOrganisations,
                            department,
                            rowPointer,
                            styles,
                            procClass
                    );

                    rowPointer = totals.nextRowPointer() + 3;
                }

                if (!hasAnyTable) {
                    Row row = sheet.createRow(0);
                    Cell cell = row.createCell(0);
                    cell.setCellValue("No procurement items available for " + rawSheetName);
                    cell.setCellStyle(styles.titleStyle);
                }

                applyProcurementSheetPrintSetup(sheet);
            }

            // =========================
            // 2. ALL DEPARTMENTS SUMMARY SHEET
            // =========================
            if (!validDepartments.isEmpty()) {
                Sheet summarySheet = workbook.createSheet(
                        WorkbookUtil.createSafeSheetName("All Departments")
                );

                createAllDepartmentsProcurementPlanSheet(
                        workbook,
                        summarySheet,
                        budget,
                        selectedOrganisations,
                        styles
                );

                applyProcurementSheetPrintSetup(summarySheet);
            }

            // =========================
            // 3. FALLBACK
            // =========================
            if (workbook.getNumberOfSheets() == 0) {
                Sheet sheet = workbook.createSheet("Procurement Plan");
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("No departmental procurement plans available");
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException(
                    "Failed to generate departmental procurement plans Excel",
                    ex
            );
        }
    }

    private void createAllDepartmentsProcurementPlanSheet(
            Workbook workbook,
            Sheet sheet,
            Budget budget,
            Set<Organisation> selectedOrganisations,
            WorkplanStyles styles
    ) {
        int rowPointer = 0;

        BigDecimal grandTotal = BigDecimal.ZERO;
        BigDecimal grandQ1 = BigDecimal.ZERO;
        BigDecimal grandQ2 = BigDecimal.ZERO;
        BigDecimal grandQ3 = BigDecimal.ZERO;
        BigDecimal grandQ4 = BigDecimal.ZERO;

        for (ProcClass procClass : ProcClass.values()) {

            List<BudgetItems> procItems
                    = sampleBudgetItemsService.findByBudgetAndBudgetTypesAndProcClassFiltered(
                            budget,
                            selectedOrganisations,
                            procClass
                    );

            if (procItems == null || procItems.isEmpty()) {
                continue;
            }

            String procClassLabel
                    = procClass.name().replace("_", " ").toUpperCase();

            // =========================
            // TITLE
            // =========================
            Row titleRow = sheet.createRow(rowPointer++);
            titleRow.setHeight((short) 700);

            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(
                    procClassLabel
                    + " PROCUREMENT PLAN - ALL DEPARTMENTS"
            );
            titleCell.setCellStyle(styles.titleStyle);

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            rowPointer - 1,
                            rowPointer - 1,
                            0,
                            12
                    )
            );

            // =========================
            // HEADER
            // =========================
            Row headerRow = sheet.createRow(rowPointer++);
            headerRow.setHeight((short) 650);

            createCell(headerRow, 0, "Programme", styles.headerStyle);
            createCell(headerRow, 1, "Procurement Item", styles.headerStyle);
            createCell(headerRow, 2, "Budget", styles.headerStyle);
            createCell(headerRow, 3, "Account", styles.headerStyle);
            createCell(headerRow, 4, "Funding", styles.headerStyle);
            createCell(headerRow, 5, "Q1", styles.headerStyle);
            createCell(headerRow, 6, "Q2", styles.headerStyle);
            createCell(headerRow, 7, "Q3", styles.headerStyle);
            createCell(headerRow, 8, "Q4", styles.headerStyle);
            createCell(headerRow, 9, "Code Description", styles.headerStyle);
            createCell(headerRow, 10, "Section", styles.headerStyle);
            createCell(headerRow, 11, "Activity", styles.headerStyle);
            createCell(headerRow, 12, "Department", styles.headerStyle);

            BigDecimal classTotal = BigDecimal.ZERO;
            BigDecimal classQ1 = BigDecimal.ZERO;
            BigDecimal classQ2 = BigDecimal.ZERO;
            BigDecimal classQ3 = BigDecimal.ZERO;
            BigDecimal classQ4 = BigDecimal.ZERO;

            // =========================
            // BODY
            // =========================
            for (BudgetItems item : procItems) {

                Row row = sheet.createRow(rowPointer++);
                row.setHeight((short) 580);

                BigDecimal amount = nz(item.getYearTotalFromQuarters());
                BigDecimal q1 = nz(item.getQ1Total());
                BigDecimal q2 = nz(item.getQ2Total());
                BigDecimal q3 = nz(item.getQ3Total());
                BigDecimal q4 = nz(item.getQ4Total());

                classTotal = classTotal.add(amount);
                classQ1 = classQ1.add(q1);
                classQ2 = classQ2.add(q2);
                classQ3 = classQ3.add(q3);
                classQ4 = classQ4.add(q4);

                String programme = "";
                String activity = "";

                if (item.getActivity() != null) {
                    activity = nzs(item.getActivity().getName());

                    if (item.getActivity().getUrcPriorityAreas() != null) {
                        programme = nzs(
                                item.getActivity()
                                        .getUrcPriorityAreas()
                                        .getName()
                        );
                    }
                }

                createCell(row, 0, programme, styles.textStyle);
                createCell(row, 1, nzs(item.getItem()), styles.textStyle);

                createNumericCell(row, 2, amount, styles.amountStyle);

                createCell(
                        row,
                        3,
                        nzs(item.getCoacode() != null
                                ? item.getCoacode().getCode()
                                : ""),
                        styles.textStyle
                );

                createCell(
                        row,
                        4,
                        nzs(item.getBudgetType() != null
                                ? item.getBudgetType().getName()
                                : ""),
                        styles.textStyle
                );

                createNumericCell(row, 5, q1, styles.amountStyle);
                createNumericCell(row, 6, q2, styles.amountStyle);
                createNumericCell(row, 7, q3, styles.amountStyle);
                createNumericCell(row, 8, q4, styles.amountStyle);

                createCell(
                        row,
                        9,
                        nzs(item.getCoacode() != null
                                ? item.getCoacode().getName()
                                : ""),
                        styles.textStyle
                );

                createCell(
                        row,
                        10,
                        nzs(item.getDeptUnit() != null
                                ? item.getDeptUnit().getNAME()
                                : ""),
                        styles.textStyle
                );

                createCell(row, 11, activity, styles.textStyle);

                createCell(
                        row,
                        12,
                        nzs(item.getDeptUnit() != null
                                && item.getDeptUnit().getNAME() != null
                                ? item.getDeptUnit().getNAME()
                                : ""),
                        styles.textStyle
                );
            }

            // =========================
            // CLASS TOTAL
            // =========================
            Row totalRow = sheet.createRow(rowPointer++);
            totalRow.setHeight((short) 650);

            createCell(
                    totalRow,
                    0,
                    "TOTAL " + procClassLabel,
                    styles.totalLabelStyle
            );

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            rowPointer - 1,
                            rowPointer - 1,
                            0,
                            1
                    )
            );

            createNumericCell(
                    totalRow,
                    2,
                    classTotal,
                    styles.totalAmountStyle
            );

            createNumericCell(
                    totalRow,
                    5,
                    classQ1,
                    styles.totalAmountStyle
            );

            createNumericCell(
                    totalRow,
                    6,
                    classQ2,
                    styles.totalAmountStyle
            );

            createNumericCell(
                    totalRow,
                    7,
                    classQ3,
                    styles.totalAmountStyle
            );

            createNumericCell(
                    totalRow,
                    8,
                    classQ4,
                    styles.totalAmountStyle
            );

            grandTotal = grandTotal.add(classTotal);
            grandQ1 = grandQ1.add(classQ1);
            grandQ2 = grandQ2.add(classQ2);
            grandQ3 = grandQ3.add(classQ3);
            grandQ4 = grandQ4.add(classQ4);

            rowPointer += 3;
        }

        // =========================
        // GRAND TOTAL
        // =========================
        Row grandRow = sheet.createRow(rowPointer);
        grandRow.setHeight((short) 750);

        createCell(
                grandRow,
                0,
                "GRAND TOTAL - ALL PROCUREMENT CLASSES",
                styles.totalLabelStyle
        );

        sheet.addMergedRegion(
                new CellRangeAddress(
                        rowPointer,
                        rowPointer,
                        0,
                        1
                )
        );

        createNumericCell(
                grandRow,
                2,
                grandTotal,
                styles.totalAmountStyle
        );

        createNumericCell(
                grandRow,
                5,
                grandQ1,
                styles.totalAmountStyle
        );

        createNumericCell(
                grandRow,
                6,
                grandQ2,
                styles.totalAmountStyle
        );

        createNumericCell(
                grandRow,
                7,
                grandQ3,
                styles.totalAmountStyle
        );

        createNumericCell(
                grandRow,
                8,
                grandQ4,
                styles.totalAmountStyle
        );

        applyStandardWorkplanColumnWidths(sheet);
    }

    private DepartmentTotals createHeaderRowWorkplanQtr2ForDepartmentCombined(
            Workbook workbook,
            Sheet sheet,
            Budget budget,
            Set<Organisation> selectedOrganisations,
            DepartmentBudget department,
            int startRow,
            WorkplanStyles styles
    ) {
        int tr = startRow;
        final short defaultRowHeight = 500;
        final short sectionCardBorderColor = IndexedColors.GREY_50_PERCENT.getIndex();

        Set<UrcDeptSectionAnlDimbgt> selectedSections
                = department.getSections() != null ? department.getSections() : Collections.emptySet();

        String deptName = nzs(department.getDepartmentName());
        String workplanTitle = "WORKPLAN - " + deptName + " " + budget.getFinancialYear();

        List<SectionCard> sectionCards = new ArrayList<>();

        Map<Long, ActivityRenderData> activityDataMap = preloadActivityRenderData(
                budget,
                selectedOrganisations,
                selectedSections
        );

        Row deptRow = sheet.createRow(tr);
        deptRow.setHeight((short) 600);
        Cell deptCell = deptRow.createCell(0);
        deptCell.setCellValue("DEPARTMENT: " + (deptName.isBlank() ? "Department" : deptName));
        deptCell.setCellStyle(styles.deptBannerStyle);
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 12));
        tr++;

        Row titleRow = sheet.createRow(tr);
        titleRow.setHeight((short) 650);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        titleCell.setCellStyle(styles.titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 1, 12));
        tr++;

        Row reportRow = sheet.createRow(tr);
        reportRow.setHeight((short) 900);
        Cell reportCell = reportRow.createCell(0);
        reportCell.setCellValue(workplanTitle);
        reportCell.setCellStyle(styles.subtitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 12));
        tr++;

        Row headerRow = sheet.createRow(tr);
        headerRow.setHeight((short) 950);

        createCell(headerRow, 0, "Programme", styles.headerStyle);
        createCell(headerRow, 1, "Activities / Budget Items", styles.headerStyle);
        createCell(headerRow, 2, "Budget", styles.headerStyle);
        createCell(headerRow, 3, "Account", styles.headerStyle);
        createCell(headerRow, 4, "Funding", styles.headerStyle);
        createCell(headerRow, 5, "Time Line Quarterly", styles.headerStyle);
        createCell(headerRow, 9, "Output / Code Description", styles.headerStyle);
        createCell(headerRow, 10, "Performance Indicator / Section", styles.headerStyle);
        createCell(headerRow, 11, "Outcome", styles.headerStyle);
        createCell(headerRow, 12, "Strategic Objective", styles.headerStyle);

        int headerTopRow = tr;
        tr++;

        Row qtrRow = sheet.createRow(tr);
        qtrRow.setHeight(defaultRowHeight);
        createCell(qtrRow, 5, "Q1", styles.quarterHeaderStyle);
        createCell(qtrRow, 6, "Q2", styles.quarterHeaderStyle);
        createCell(qtrRow, 7, "Q3", styles.quarterHeaderStyle);
        createCell(qtrRow, 8, "Q4", styles.quarterHeaderStyle);

        int headerBottomRow = tr;

        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerTopRow, 5, 8));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 9, 9));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 10, 10));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 11, 11));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 12, 12));

        short rownum = (short) tr;

        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal totalQtr1 = BigDecimal.ZERO;
        BigDecimal totalQtr2 = BigDecimal.ZERO;
        BigDecimal totalQtr3 = BigDecimal.ZERO;
        BigDecimal totalQtr4 = BigDecimal.ZERO;

        if (sheet instanceof XSSFSheet xssfSheet) {
            xssfSheet.setRowSumsBelow(false);
        }

        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(budget, new ArrayList<>(selectedSections))) {
            programmes = sampleURC_Priority_Areas.getAreasByDate(budget.getCloseDate());

            for (URC_Priority_Areas prog : programmes) {
                programmesActivities = sampleUrc_ActivitiesService.findActivitiesByBudgetAndPriorityAndDeptUnits(
                        budget,
                        prog,
                        new ArrayList<>(selectedSections)
                );

                if (programmesActivities == null || programmesActivities.isEmpty()) {
                    continue;
                }

                int currentGroupStart = -1;
                int currentGroupEnd = -1;

                for (Urc_Activities activity : programmesActivities) {
                    if (activity == null || activity.getId() == null) {
                        continue;
                    }

                    ActivityRenderData renderData = activityDataMap.get(activity.getId());
                    if (renderData == null || renderData.items().isEmpty() || renderData.total().compareTo(BigDecimal.ZERO) == 0) {
                        continue;
                    }

                    rownum++;
                    Row dataRow = sheet.createRow(rownum);
                    dataRow.setHeight((short) 900);

                    boolean zebra = rownum % 2 == 0;
                    CellStyle rowTextStyle = zebra ? styles.zebraActivityTextStyle : styles.activityTextStyle;
                    CellStyle rowAmountStyle = zebra ? styles.zebraActivityAmountStyle : styles.activityAmountStyle;
                    CellStyle rowQuarterStyle = zebra ? styles.zebraActivityQuarterStyle : styles.activityQuarterStyle;

                    int col = 0;

                    createCell(dataRow, col++, nzs(prog.getName()), rowTextStyle);
                    createCell(dataRow, col++, nzs(activity.getName()), rowTextStyle);

                    totalExpense = totalExpense.add(renderData.total());
                    totalQtr1 = totalQtr1.add(renderData.q1());
                    totalQtr2 = totalQtr2.add(renderData.q2());
                    totalQtr3 = totalQtr3.add(renderData.q3());
                    totalQtr4 = totalQtr4.add(renderData.q4());

                    createNumericCell(dataRow, col++, renderData.total(), rowAmountStyle);
                    createCell(dataRow, col++, renderData.accounts(), rowTextStyle);
                    createCell(dataRow, col++, renderData.funding(), rowTextStyle);

                    createNumericCell(dataRow, col++, renderData.q1(), renderData.q1().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);
                    createNumericCell(dataRow, col++, renderData.q2(), renderData.q2().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);
                    createNumericCell(dataRow, col++, renderData.q3(), renderData.q3().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);
                    createNumericCell(dataRow, col++, renderData.q4(), renderData.q4().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);

                    createCell(dataRow, col++, nzs(activity.getOutput()), rowTextStyle);
                    createCell(dataRow, col++, nzs(activity.getPerformanceIndicator()), rowTextStyle);
                    createCell(dataRow, col++, nzs(activity.getOutcome()), rowTextStyle);
                    createCell(dataRow, col++, nzs(activity.getObjective()), rowTextStyle);

                    if (currentGroupStart == -1) {
                        currentGroupStart = rownum;
                    }
                    currentGroupEnd = rownum;

                    int cardStartRow;
                    int detailGroupStart;

                    rownum++;
                    Row bandRow = sheet.createRow(rownum);
                    bandRow.setHeight((short) 430);
                    cardStartRow = rownum;
                    detailGroupStart = rownum;

                    createCell(bandRow, 0, "", styles.detailBandStyle);
                    createCell(bandRow, 1, "Budget Item Breakdown", styles.detailBandStyle);
                    for (int i = 2; i <= 12; i++) {
                        createCell(bandRow, i, "", styles.detailBandStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 12));

                    currentGroupEnd = rownum;

                    rownum++;
                    Row itemHeaderRow = sheet.createRow(rownum);
                    itemHeaderRow.setHeight((short) 500);
                    createBudgetItemSubHeaderWithoutOrganisation(itemHeaderRow, styles.detailHeaderStyle);

                    currentGroupEnd = rownum;

                    for (BudgetItems item : renderData.items()) {
                        rownum++;

                        Row itemRow = sheet.createRow(rownum);
                        itemRow.setHeight((short) 680);

                        boolean zebraItem = rownum % 2 == 0;
                        CellStyle itemTextStyle = zebraItem ? styles.zebraDetailTextStyle : styles.detailTextStyle;
                        CellStyle itemAmountStyle = zebraItem ? styles.zebraDetailAmountStyle : styles.detailAmountStyle;
                        CellStyle itemQuarterStyle = zebraItem ? styles.zebraDetailQuarterStyle : styles.detailQuarterStyle;

                        BigDecimal itemAmount = nz(item.getYearTotalFromQuarters());
                        BigDecimal itemQ1 = nz(item.getQ1Total());
                        BigDecimal itemQ2 = nz(item.getQ2Total());
                        BigDecimal itemQ3 = nz(item.getQ3Total());
                        BigDecimal itemQ4 = nz(item.getQ4Total());

                        createCell(itemRow, 0, "", itemTextStyle);
                        createCell(itemRow, 1, "• " + nzs(item.getItem()), itemTextStyle);
                        createNumericCell(itemRow, 2, itemAmount, itemAmountStyle);
                        createCell(itemRow, 3, nzs(item.getCoacode() != null ? item.getCoacode().getCode() : ""), itemTextStyle);
                        createCell(itemRow, 4, nzs(item.getBudgetType() != null ? item.getBudgetType().getName() : ""), itemTextStyle);

                        createNumericCell(itemRow, 5, itemQ1, itemQ1.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);
                        createNumericCell(itemRow, 6, itemQ2, itemQ2.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);
                        createNumericCell(itemRow, 7, itemQ3, itemQ3.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);
                        createNumericCell(itemRow, 8, itemQ4, itemQ4.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);

                        createCell(itemRow, 9, nzs(item.getCoacode() != null ? item.getCoacode().getName() : ""), itemTextStyle);
                        createCell(itemRow, 10, nzs(item.getDeptUnit() != null ? item.getDeptUnit().getNAME() : ""), itemTextStyle);
                        createCell(itemRow, 11, "", itemTextStyle);
                        createCell(itemRow, 12, "", itemTextStyle);

                        currentGroupEnd = rownum;
                    }

                    // =========================
// ACTIVITY SEPARATOR ROW
// =========================
                    rownum++;

                    Row separatorRow = sheet.createRow(rownum);
                    separatorRow.setHeight((short) 260);

                    CellStyle separatorStyle = workbook.createCellStyle();
                    separatorStyle.cloneStyleFrom(styles.detailTextStyle);

                    separatorStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    separatorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    separatorStyle.setBorderTop(BorderStyle.NONE);
                    separatorStyle.setBorderBottom(BorderStyle.NONE);
                    separatorStyle.setBorderLeft(BorderStyle.NONE);
                    separatorStyle.setBorderRight(BorderStyle.NONE);

                    for (int c = 0; c <= 12; c++) {
                        Cell cell = separatorRow.createCell(c);
                        cell.setCellStyle(separatorStyle);

                        if (c == 0) {
                            cell.setCellValue("");
                        }
                    }

                    CellRangeAddress separatorRange
                            = new CellRangeAddress(rownum, rownum, 0, 12);

                    sheet.addMergedRegion(separatorRange);

                    currentGroupEnd = rownum;

                    sectionCards.add(new SectionCard(cardStartRow, rownum, 0, 12));
                    sheet.groupRow(detailGroupStart, rownum);
                    sheet.setRowGroupCollapsed(detailGroupStart, true);
                }

                if (currentGroupStart != -1 && currentGroupEnd != -1 && currentGroupEnd > currentGroupStart) {
                    CellRangeAddress merged = new CellRangeAddress(currentGroupStart, currentGroupEnd, 0, 0);
                    if (!isOverlappingWithExistingRegions(sheet, merged)) {
                        addMergedRegion(sheet, merged);
                    }
                }
            }
        }

        rownum++;
        Row totalRow = sheet.createRow(rownum);
        totalRow.setHeight(defaultRowHeight);

        createCell(totalRow, 0, "TOTAL", styles.totalLabelStyle);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));

        createNumericCell(totalRow, 2, totalExpense, styles.totalAmountStyle);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 4));

        createNumericCell(totalRow, 5, totalQtr1, styles.totalAmountStyle);
        createNumericCell(totalRow, 6, totalQtr2, styles.totalAmountStyle);
        createNumericCell(totalRow, 7, totalQtr3, styles.totalAmountStyle);
        createNumericCell(totalRow, 8, totalQtr4, styles.totalAmountStyle);

        for (int c = 3; c <= 4; c++) {
            Cell cell = totalRow.getCell(c);
            if (cell == null) {
                cell = totalRow.createCell(c);
            }
            cell.setCellStyle(styles.totalAmountStyle);
        }

        for (int c = 9; c <= 12; c++) {
            Cell cell = totalRow.getCell(c);
            if (cell == null) {
                cell = totalRow.createCell(c);
            }
            cell.setCellStyle(styles.totalLabelStyle);
        }

        applySectionCards(sheet, sectionCards, sectionCardBorderColor);

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            applyRegionBorder(sheet, sheet.getMergedRegion(i));
        }

        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 11000);
        sheet.setColumnWidth(2, 4500);
        sheet.setColumnWidth(3, 4500);
        sheet.setColumnWidth(4, 5500);
        sheet.setColumnWidth(5, 3400);
        sheet.setColumnWidth(6, 3400);
        sheet.setColumnWidth(7, 3400);
        sheet.setColumnWidth(8, 3400);
        sheet.setColumnWidth(9, 7000);
        sheet.setColumnWidth(10, 5500);
        sheet.setColumnWidth(11, 5000);
        sheet.setColumnWidth(12, 5000);

        return new DepartmentTotals(
                deptName,
                rownum + 1,
                totalExpense,
                totalQtr1,
                totalQtr2,
                totalQtr3,
                totalQtr4
        );
    }

    private DepartmentTotals createDepartmentalProcurementPlanDetailed(
            Workbook workbook,
            Sheet sheet,
            Budget budget,
            Set<Organisation> selectedOrganisations,
            DepartmentBudget department,
            int startRow,
            WorkplanStyles styles,
            ProcClass procClass
    ) {
        int tr = startRow;
        final short defaultRowHeight = 500;
        final short sectionCardBorderColor = IndexedColors.GREY_50_PERCENT.getIndex();

        Set<UrcDeptSectionAnlDimbgt> selectedSections
                = department.getSections() != null ? department.getSections() : Collections.emptySet();

        String deptName = nzs(department.getDepartmentName());
        String procClassLabel = procClass != null
                ? procClass.name().replace("_", " ").toUpperCase()
                : "UNSPECIFIED";

        String workplanTitle = procClassLabel
                + " PROCUREMENT PLAN - "
                + deptName + " "
                + budget.getFinancialYear();

        List<SectionCard> sectionCards = new ArrayList<>();

        Map<Long, ActivityRenderData> activityDataMap
                = preloadActivityRenderDataByProcClass(
                        budget,
                        selectedOrganisations,
                        selectedSections,
                        procClass
                );

        // =========================
        // SECTION / TABLE BANNER
        // =========================
        Row deptRow = sheet.createRow(tr);
        deptRow.setHeight((short) 600);

        Cell deptCell = deptRow.createCell(0);
        String subTitle = "DEPARTMENT: " + (deptName.isBlank() ? "Department" : deptName)
                + " | PROCUREMENT CLASS: " + procClassLabel;
        deptCell.setCellValue(subTitle.toUpperCase());
        deptCell.setCellStyle(styles.deptBannerStyle);

        CellRangeAddress deptRange = new CellRangeAddress(tr, tr, 0, 12);
        sheet.addMergedRegion(deptRange);
        setBottomBorderForRegion(sheet, deptRange);
        tr++;

        Row titleRow = sheet.createRow(tr);
        titleRow.setHeight((short) 650);

        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        titleCell.setCellStyle(styles.titleStyle);

        CellRangeAddress titleRange = new CellRangeAddress(tr, tr, 1, 12);
        sheet.addMergedRegion(titleRange);
        tr++;

        Row reportRow = sheet.createRow(tr);
        reportRow.setHeight((short) 900);

        Cell reportCell = reportRow.createCell(0);
        reportCell.setCellValue(workplanTitle.toUpperCase());
        reportCell.setCellStyle(styles.subtitleStyle);

        CellRangeAddress reportRange = new CellRangeAddress(tr, tr, 0, 12);
        sheet.addMergedRegion(reportRange);
        tr++;

        // =========================
        // HEADER
        // =========================
        Row headerRow = sheet.createRow(tr);
        headerRow.setHeight((short) 950);

        createCell(headerRow, 0, "Programme", styles.headerStyle);
        createCell(headerRow, 1, "Activities / Budget Items", styles.headerStyle);
        createCell(headerRow, 2, "Budget", styles.headerStyle);
        createCell(headerRow, 3, "Account", styles.headerStyle);
        createCell(headerRow, 4, "Funding", styles.headerStyle);
        createCell(headerRow, 5, "Time Line Quarterly", styles.headerStyle);
        createCell(headerRow, 9, "Output / Code Description", styles.headerStyle);
        createCell(headerRow, 10, "Performance Indicator / Section", styles.headerStyle);
        createCell(headerRow, 11, "Outcome", styles.headerStyle);
        createCell(headerRow, 12, "Strategic Objective", styles.headerStyle);

        int headerTopRow = tr;
        tr++;

        Row qtrRow = sheet.createRow(tr);
        qtrRow.setHeight(defaultRowHeight);

        createCell(qtrRow, 5, "Q1", styles.quarterHeaderStyle);
        createCell(qtrRow, 6, "Q2", styles.quarterHeaderStyle);
        createCell(qtrRow, 7, "Q3", styles.quarterHeaderStyle);
        createCell(qtrRow, 8, "Q4", styles.quarterHeaderStyle);

        int headerBottomRow = tr;

        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerTopRow, 5, 8));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 9, 9));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 10, 10));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 11, 11));
        sheet.addMergedRegion(new CellRangeAddress(headerTopRow, headerBottomRow, 12, 12));

        short rownum = (short) tr;

        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal totalQtr1 = BigDecimal.ZERO;
        BigDecimal totalQtr2 = BigDecimal.ZERO;
        BigDecimal totalQtr3 = BigDecimal.ZERO;
        BigDecimal totalQtr4 = BigDecimal.ZERO;

        if (sheet instanceof XSSFSheet xssfSheet) {
            xssfSheet.setRowSumsBelow(false);
        }

        // =========================
        // EMPTY PROCUREMENT CLASS TABLE
        // =========================
        if (activityDataMap.isEmpty()) {
            rownum++;

            Row emptyRow = sheet.createRow(rownum);
            emptyRow.setHeight((short) 600);

            createCell(
                    emptyRow,
                    0,
                    "No procurement items found for " + procClassLabel,
                    styles.textStyle
            );

            for (int c = 1; c <= 12; c++) {
                createCell(emptyRow, c, "", styles.textStyle);
            }

            CellRangeAddress emptyRange = new CellRangeAddress(rownum, rownum, 0, 12);
            sheet.addMergedRegion(emptyRange);
            setBottomBorderForRegion(sheet, emptyRange);

            rownum++;

            applyStandardWorkplanColumnWidths(sheet);

            return new DepartmentTotals(
                    procClassLabel,
                    rownum + 1,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        // =========================
        // BODY
        // =========================
        programmes = sampleURC_Priority_Areas.getAreasByDate(budget.getCloseDate());

        for (URC_Priority_Areas prog : programmes) {
            programmesActivities = sampleUrc_ActivitiesService.findActivitiesByBudgetAndPriorityAndDeptUnits(
                    budget,
                    prog,
                    new ArrayList<>(selectedSections)
            );

            if (programmesActivities == null || programmesActivities.isEmpty()) {
                continue;
            }

            int currentGroupStart = -1;
            int currentGroupEnd = -1;

            for (Urc_Activities activity : programmesActivities) {
                if (activity == null || activity.getId() == null) {
                    continue;
                }

                ActivityRenderData renderData = activityDataMap.get(activity.getId());

                if (renderData == null
                        || renderData.items() == null
                        || renderData.items().isEmpty()
                        || renderData.total().compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                rownum++;

                Row dataRow = sheet.createRow(rownum);
                dataRow.setHeight((short) 900);

                boolean zebra = rownum % 2 == 0;
                CellStyle rowTextStyle = zebra ? styles.zebraActivityTextStyle : styles.activityTextStyle;
                CellStyle rowAmountStyle = zebra ? styles.zebraActivityAmountStyle : styles.activityAmountStyle;
                CellStyle rowQuarterStyle = zebra ? styles.zebraActivityQuarterStyle : styles.activityQuarterStyle;

                int col = 0;

                createCell(dataRow, col++, nzs(prog.getName()), rowTextStyle);
                createCell(dataRow, col++, nzs(activity.getName()), rowTextStyle);

                totalExpense = totalExpense.add(renderData.total());
                totalQtr1 = totalQtr1.add(renderData.q1());
                totalQtr2 = totalQtr2.add(renderData.q2());
                totalQtr3 = totalQtr3.add(renderData.q3());
                totalQtr4 = totalQtr4.add(renderData.q4());

                createNumericCell(dataRow, col++, renderData.total(), rowAmountStyle);
                createCell(dataRow, col++, renderData.accounts(), rowTextStyle);
                createCell(dataRow, col++, renderData.funding(), rowTextStyle);

                createNumericCell(dataRow, col++, renderData.q1(),
                        renderData.q1().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);
                createNumericCell(dataRow, col++, renderData.q2(),
                        renderData.q2().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);
                createNumericCell(dataRow, col++, renderData.q3(),
                        renderData.q3().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);
                createNumericCell(dataRow, col++, renderData.q4(),
                        renderData.q4().compareTo(BigDecimal.ZERO) > 0 ? rowQuarterStyle : rowAmountStyle);

                createCell(dataRow, col++, nzs(activity.getOutput()), rowTextStyle);
                createCell(dataRow, col++, nzs(activity.getPerformanceIndicator()), rowTextStyle);
                createCell(dataRow, col++, nzs(activity.getOutcome()), rowTextStyle);
                createCell(dataRow, col++, nzs(activity.getObjective()), rowTextStyle);

                if (currentGroupStart == -1) {
                    currentGroupStart = rownum;
                }
                currentGroupEnd = rownum;

                // =========================
                // PROCUREMENT ITEMS BLOCK
                // =========================
                rownum++;

                Row bandRow = sheet.createRow(rownum);
                bandRow.setHeight((short) 430);

                int cardStartRow = rownum;
                int detailGroupStart = rownum;

                createCell(bandRow, 0, "", styles.detailBandStyle);
                createCell(bandRow, 1, procClassLabel + " Procurement Item Breakdown", styles.detailBandStyle);

                for (int i = 2; i <= 12; i++) {
                    createCell(bandRow, i, "", styles.detailBandStyle);
                }

                sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 12));
                currentGroupEnd = rownum;

                rownum++;

                Row itemHeaderRow = sheet.createRow(rownum);
                itemHeaderRow.setHeight((short) 500);
                createBudgetItemSubHeaderWithoutOrganisation(itemHeaderRow, styles.detailHeaderStyle);

                currentGroupEnd = rownum;

                for (BudgetItems item : renderData.items()) {
                    rownum++;

                    Row itemRow = sheet.createRow(rownum);
                    itemRow.setHeight((short) 680);

                    boolean zebraItem = rownum % 2 == 0;
                    CellStyle itemTextStyle = zebraItem ? styles.zebraDetailTextStyle : styles.detailTextStyle;
                    CellStyle itemAmountStyle = zebraItem ? styles.zebraDetailAmountStyle : styles.detailAmountStyle;
                    CellStyle itemQuarterStyle = zebraItem ? styles.zebraDetailQuarterStyle : styles.detailQuarterStyle;

                    BigDecimal itemAmount = nz(item.getYearTotalFromQuarters());
                    BigDecimal itemQ1 = nz(item.getQ1Total());
                    BigDecimal itemQ2 = nz(item.getQ2Total());
                    BigDecimal itemQ3 = nz(item.getQ3Total());
                    BigDecimal itemQ4 = nz(item.getQ4Total());

                    createCell(itemRow, 0, "", itemTextStyle);
                    createCell(itemRow, 1, "• " + nzs(item.getItem()), itemTextStyle);
                    createNumericCell(itemRow, 2, itemAmount, itemAmountStyle);
                    createCell(itemRow, 3, nzs(item.getCoacode() != null ? item.getCoacode().getCode() : ""), itemTextStyle);
                    createCell(itemRow, 4, nzs(item.getBudgetType() != null ? item.getBudgetType().getName() : ""), itemTextStyle);

                    createNumericCell(itemRow, 5, itemQ1,
                            itemQ1.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);
                    createNumericCell(itemRow, 6, itemQ2,
                            itemQ2.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);
                    createNumericCell(itemRow, 7, itemQ3,
                            itemQ3.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);
                    createNumericCell(itemRow, 8, itemQ4,
                            itemQ4.compareTo(BigDecimal.ZERO) > 0 ? itemQuarterStyle : itemAmountStyle);

                    createCell(itemRow, 9, nzs(item.getCoacode() != null ? item.getCoacode().getName() : ""), itemTextStyle);
                    createCell(itemRow, 10, nzs(item.getDeptUnit() != null ? item.getDeptUnit().getNAME() : ""), itemTextStyle);
                    createCell(itemRow, 11, nzs(item.getProcClass() != null ? item.getProcClass().name().replace("_", " ") : ""), itemTextStyle);
                    createCell(itemRow, 12, "", itemTextStyle);

                    currentGroupEnd = rownum;
                }

                // =========================
// ACTIVITY SEPARATOR ROW
// =========================
                rownum++;

                Row separatorRow = sheet.createRow(rownum);
                separatorRow.setHeight((short) 260);

                CellStyle separatorStyle = workbook.createCellStyle();
                separatorStyle.cloneStyleFrom(styles.detailTextStyle);

                separatorStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                separatorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                separatorStyle.setBorderTop(BorderStyle.NONE);
                separatorStyle.setBorderBottom(BorderStyle.NONE);
                separatorStyle.setBorderLeft(BorderStyle.NONE);
                separatorStyle.setBorderRight(BorderStyle.NONE);

                for (int c = 0; c <= 12; c++) {
                    Cell cell = separatorRow.createCell(c);
                    cell.setCellStyle(separatorStyle);

                    if (c == 0) {
                        cell.setCellValue("");
                    }
                }

                CellRangeAddress separatorRange
                        = new CellRangeAddress(rownum, rownum, 0, 12);

                sheet.addMergedRegion(separatorRange);

                currentGroupEnd = rownum;

                sectionCards.add(new SectionCard(cardStartRow, rownum, 0, 12));

                sheet.groupRow(detailGroupStart, rownum);
                sheet.setRowGroupCollapsed(detailGroupStart, true);
            }

            if (currentGroupStart != -1 && currentGroupEnd != -1 && currentGroupEnd > currentGroupStart) {
                CellRangeAddress merged = new CellRangeAddress(currentGroupStart, currentGroupEnd, 0, 0);
                if (!isOverlappingWithExistingRegions(sheet, merged)) {
                    addMergedRegion(sheet, merged);
                }
            }
        }

        // =========================
        // TOTAL ROW
        // =========================
        rownum++;

        Row totalRow = sheet.createRow(rownum);
        totalRow.setHeight(defaultRowHeight);

        createCell(totalRow, 0, "TOTAL " + procClassLabel, styles.totalLabelStyle);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));

        createNumericCell(totalRow, 2, totalExpense, styles.totalAmountStyle);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 4));

        createNumericCell(totalRow, 5, totalQtr1, styles.totalAmountStyle);
        createNumericCell(totalRow, 6, totalQtr2, styles.totalAmountStyle);
        createNumericCell(totalRow, 7, totalQtr3, styles.totalAmountStyle);
        createNumericCell(totalRow, 8, totalQtr4, styles.totalAmountStyle);

        for (int c = 3; c <= 4; c++) {
            Cell cell = totalRow.getCell(c);
            if (cell == null) {
                cell = totalRow.createCell(c);
            }
            cell.setCellStyle(styles.totalAmountStyle);
        }

        for (int c = 9; c <= 12; c++) {
            Cell cell = totalRow.getCell(c);
            if (cell == null) {
                cell = totalRow.createCell(c);
            }
            cell.setCellStyle(styles.totalLabelStyle);
        }

        applySectionCardsFast(sheet, sectionCards, sectionCardBorderColor);

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            applyRegionBorder(sheet, sheet.getMergedRegion(i));
        }

        applyStandardWorkplanColumnWidths(sheet);

        return new DepartmentTotals(
                procClassLabel,
                rownum + 1,
                totalExpense,
                totalQtr1,
                totalQtr2,
                totalQtr3,
                totalQtr4
        );
    }

    private void applySectionCardsFast(
            Sheet sheet,
            List<SectionCard> cards,
            short borderColor
    ) {
        if (sheet == null || cards == null || cards.isEmpty()) {
            return;
        }

        for (SectionCard card : cards) {
            if (card == null) {
                continue;
            }

            int firstRow = Math.max(0, card.startRow());
            int lastRow = Math.max(firstRow, card.endRow());
            int firstCol = Math.max(0, card.startCol());
            int lastCol = Math.max(firstCol, card.endCol());

            CellRangeAddress region = new CellRangeAddress(
                    firstRow,
                    lastRow,
                    firstCol,
                    lastCol
            );

            RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
            RegionUtil.setTopBorderColor(borderColor, region, sheet);

            RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
            RegionUtil.setBottomBorderColor(borderColor, region, sheet);

            RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
            RegionUtil.setLeftBorderColor(borderColor, region, sheet);

            RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
            RegionUtil.setRightBorderColor(borderColor, region, sheet);
        }
    }

    private void applyProcurementSheetPrintSetup(Sheet sheet) {
        PrintSetup ps = sheet.getPrintSetup();
        ps.setLandscape(true);
        ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);

        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        sheet.setAutobreaks(true);

        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.3);
        sheet.setMargin(Sheet.RightMargin, 0.3);

        sheet.setZoom(85);
    }

    private void applyStandardWorkplanColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 11000);
        sheet.setColumnWidth(2, 4500);
        sheet.setColumnWidth(3, 4500);
        sheet.setColumnWidth(4, 5500);
        sheet.setColumnWidth(5, 3400);
        sheet.setColumnWidth(6, 3400);
        sheet.setColumnWidth(7, 3400);
        sheet.setColumnWidth(8, 3400);
        sheet.setColumnWidth(9, 7000);
        sheet.setColumnWidth(10, 5500);
        sheet.setColumnWidth(11, 5000);
        sheet.setColumnWidth(12, 5000);
    }

    private byte[] generateDepartmentalProcurementPlanByProcClass(
            Budget budget,
            Set<Organisation> selectedOrganisations,
            DepartmentBudget department
    ) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            WorkplanStyles styles = createWorkplanStyles(workbook);

            Sheet sheet = workbook.createSheet(
                    WorkbookUtil.createSafeSheetName("Procurement Plan")
            );

            int rowPointer = 0;

            for (ProcClass procClass : ProcClass.values()) {
                DepartmentTotals totals = createDepartmentalProcurementPlanDetailed(
                        workbook,
                        sheet,
                        budget,
                        selectedOrganisations,
                        department,
                        rowPointer,
                        styles,
                        procClass
                );

                rowPointer = totals.nextRowPointer() + 3;
            }

            PrintSetup ps = sheet.getPrintSetup();
            ps.setLandscape(true);
            ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
            ps.setFitWidth((short) 1);
            ps.setFitHeight((short) 0);

            sheet.setFitToPage(true);
            sheet.setHorizontallyCenter(true);
            sheet.setAutobreaks(true);
            sheet.createFreezePane(0, 4);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Failed to generate procurement plan Excel", ex);
        }
    }

    private Map<Long, ActivityRenderData> preloadActivityRenderDataByProcClass(
            Budget budget,
            Set<Organisation> selectedOrganisations,
            Set<UrcDeptSectionAnlDimbgt> selectedSections,
            ProcClass procClass
    ) {
        List<BudgetItems> allItems
                = sampleBudgetItemsService.findByBudgetAndBudgetTypesAndDeptUnitsAndProcClass(
                        budget,
                        selectedOrganisations,
                        selectedSections,
                        procClass
                );

        Map<Long, List<BudgetItems>> itemsByActivity = allItems.stream()
                .filter(item -> item.getActivity() != null && item.getActivity().getId() != null)
                .collect(Collectors.groupingBy(item -> item.getActivity().getId()));

        Map<Long, ActivityRenderData> result = new HashMap<>();

        for (Map.Entry<Long, List<BudgetItems>> entry : itemsByActivity.entrySet()) {
            List<BudgetItems> items = entry.getValue();
            Urc_Activities activity = items.get(0).getActivity();

            BigDecimal total = items.stream()
                    .map(item -> nz(item.getYearTotalFromQuarters()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q1 = items.stream()
                    .map(item -> nz(item.getQ1Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q2 = items.stream()
                    .map(item -> nz(item.getQ2Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q3 = items.stream()
                    .map(item -> nz(item.getQ3Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q4 = items.stream()
                    .map(item -> nz(item.getQ4Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String accounts = items.stream()
                    .map(item -> item.getCoacode() != null ? nzs(item.getCoacode().getCode()) : "")
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            String funding = items.stream()
                    .map(item -> item.getBudgetType() != null ? nzs(item.getBudgetType().getName()) : "")
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            result.put(entry.getKey(), new ActivityRenderData(
                    activity,
                    items,
                    total,
                    q1,
                    q2,
                    q3,
                    q4,
                    accounts,
                    funding
            ));
        }

        return result;
    }

    private record DepartmentOrganisationBudgetRow(
            String departmentName,
            Map<String, BigDecimal> organisationBudgets) {

    }

    private List<DepartmentOrganisationBudgetRow> buildDepartmentOrganisationBudgetRows(
            Budget budget,
            Set<Organisation> selectedOrganisations,
            List<DepartmentBudget> departments
    ) {
        List<DepartmentOrganisationBudgetRow> rows = new ArrayList<>();

        for (DepartmentBudget department : departments) {
            if (department == null || department.getSections() == null || department.getSections().isEmpty()) {
                continue;
            }

            Set<UrcDeptSectionAnlDimbgt> sections = department.getSections();

            List<BudgetItems> items = sampleBudgetItemsService.findExpenseItemsByBudgetAndBudgetTypesAndDeptUnits(
                    budget,
                    selectedOrganisations,
                    sections
            );

            Map<String, BigDecimal> orgTotals = new LinkedHashMap<>();

            for (Organisation org : selectedOrganisations) {
                String orgName = org != null ? nzs(org.getName()) : "";
                if (!orgName.isBlank()) {
                    orgTotals.put(orgName, BigDecimal.ZERO);
                }
            }

            for (BudgetItems item : items) {
                if (item.getBudgetType() == null) {
                    continue;
                }

                String orgName = nzs(item.getBudgetType().getName());
                if (orgName.isBlank()) {
                    continue;
                }

                BigDecimal amount = nz(item.getYearTotalFromQuarters());
                orgTotals.merge(orgName, amount, BigDecimal::add);
            }

            rows.add(new DepartmentOrganisationBudgetRow(
                    nzs(department.getDepartmentName()),
                    orgTotals
            ));
        }

        return rows;
    }

    private void createDashboardSummarySheet(
            Workbook workbook,
            Sheet sheet,
            List<DepartmentTotals> departmentTotals,
            List<DepartmentOrganisationBudgetRow> departmentOrganisationRows,
            Set<Organisation> selectedOrganisations,
            WorkplanStyles styles
    ) {
        int rowIdx = 0;

        // =========================
        // TABLE 1: Department Summary
        // =========================
        Row titleRow = sheet.createRow(rowIdx++);
        titleRow.setHeight((short) 650);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DEPARTMENT EXPENDITURE DASHBOARD");
        titleCell.setCellStyle(styles.titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(
                titleRow.getRowNum(),
                titleRow.getRowNum(),
                0,
                7
        ));

        Row headerRow = sheet.createRow(rowIdx++);
        createCell(headerRow, 0, "Department", styles.headerStyle);
        createCell(headerRow, 1, "Total Expenditure", styles.headerStyle);
        createCell(headerRow, 2, "Q1", styles.headerStyle);
        createCell(headerRow, 3, "Q2", styles.headerStyle);
        createCell(headerRow, 4, "Q3", styles.headerStyle);
        createCell(headerRow, 5, "Q4", styles.headerStyle);
        createCell(headerRow, 6, "% Contribution", styles.headerStyle);
        createCell(headerRow, 7, "Variance from Avg", styles.headerStyle);

        BigDecimal grandTotal = departmentTotals.stream()
                .map(d -> nz(d.total()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandQ1 = departmentTotals.stream()
                .map(d -> nz(d.q1()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandQ2 = departmentTotals.stream()
                .map(d -> nz(d.q2()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandQ3 = departmentTotals.stream()
                .map(d -> nz(d.q3()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandQ4 = departmentTotals.stream()
                .map(d -> nz(d.q4()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = departmentTotals.isEmpty()
                ? BigDecimal.ZERO
                : grandTotal.divide(BigDecimal.valueOf(departmentTotals.size()), 2, RoundingMode.HALF_UP);

        for (DepartmentTotals d : departmentTotals) {
            Row row = sheet.createRow(rowIdx++);

            BigDecimal percent = grandTotal.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : nz(d.total())
                            .multiply(BigDecimal.valueOf(100))
                            .divide(grandTotal, 2, RoundingMode.HALF_UP);

            BigDecimal variance = nz(d.total()).subtract(average);

            createCell(row, 0, nzs(d.departmentName()), styles.textStyle);
            createNumericCell(row, 1, nz(d.total()), styles.amountStyle);
            createNumericCell(row, 2, nz(d.q1()), styles.amountStyle);
            createNumericCell(row, 3, nz(d.q2()), styles.amountStyle);
            createNumericCell(row, 4, nz(d.q3()), styles.amountStyle);
            createNumericCell(row, 5, nz(d.q4()), styles.amountStyle);
            createNumericCell(row, 6, percent, styles.amountStyle);
            createNumericCell(row, 7, variance, styles.amountStyle);
        }

        Row totalRow = sheet.createRow(rowIdx++);
        createCell(totalRow, 0, "GRAND TOTAL", styles.totalLabelStyle);
        createNumericCell(totalRow, 1, grandTotal, styles.totalAmountStyle);
        createNumericCell(totalRow, 2, grandQ1, styles.totalAmountStyle);
        createNumericCell(totalRow, 3, grandQ2, styles.totalAmountStyle);
        createNumericCell(totalRow, 4, grandQ3, styles.totalAmountStyle);
        createNumericCell(totalRow, 5, grandQ4, styles.totalAmountStyle);
        createNumericCell(totalRow, 6, BigDecimal.valueOf(100), styles.totalAmountStyle);
        createCell(totalRow, 7, "-", styles.totalLabelStyle);

        // spacing
        rowIdx += 2;

        // =========================
        // TABLE 2: Department x Organisation Budgets
        // =========================
        Map<String, String> aliasToFullName = buildOrganisationAliases(selectedOrganisations);
        List<String> aliases = new ArrayList<>(aliasToFullName.keySet());

        Row secondTitleRow = sheet.createRow(rowIdx++);
        secondTitleRow.setHeight((short) 600);
        Cell secondTitleCell = secondTitleRow.createCell(0);
        secondTitleCell.setCellValue("DEPARTMENT BUDGET BY ORGANISATION");
        secondTitleCell.setCellStyle(styles.titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(
                secondTitleRow.getRowNum(),
                secondTitleRow.getRowNum(),
                0,
                Math.max(aliases.size() + 1, 2)
        ));

        Row orgHeaderRow = sheet.createRow(rowIdx++);
        createCell(orgHeaderRow, 0, "Department", styles.headerStyle);

        int col = 1;
        for (String alias : aliases) {
            createCell(orgHeaderRow, col++, alias, styles.headerStyle);
        }
        createCell(orgHeaderRow, col, "Total", styles.headerStyle);

        Map<String, BigDecimal> columnTotals = new LinkedHashMap<>();
        for (String alias : aliases) {
            columnTotals.put(alias, BigDecimal.ZERO);
        }
        BigDecimal overallOrgGrandTotal = BigDecimal.ZERO;

        for (DepartmentOrganisationBudgetRow deptRow : departmentOrganisationRows) {
            Row row = sheet.createRow(rowIdx++);

            createCell(row, 0, nzs(deptRow.departmentName()), styles.textStyle);

            int c = 1;
            BigDecimal rowTotal = BigDecimal.ZERO;

            for (String alias : aliases) {
                String fullName = aliasToFullName.get(alias);
                BigDecimal value = nz(deptRow.organisationBudgets().get(fullName));

                createNumericCell(row, c++, value, styles.amountStyle);

                rowTotal = rowTotal.add(value);
                columnTotals.merge(alias, value, BigDecimal::add);
            }

            createNumericCell(row, c, rowTotal, styles.totalAmountStyle);
            overallOrgGrandTotal = overallOrgGrandTotal.add(rowTotal);
        }

        Row orgTotalRow = sheet.createRow(rowIdx++);
        createCell(orgTotalRow, 0, "TOTAL", styles.totalLabelStyle);

        int totalCol = 1;
        for (String alias : aliases) {
            createNumericCell(orgTotalRow, totalCol++, nz(columnTotals.get(alias)), styles.totalAmountStyle);
        }
        createNumericCell(orgTotalRow, totalCol, overallOrgGrandTotal, styles.totalAmountStyle);

        // spacing
        rowIdx += 2;

        // =========================
        // TABLE 3: Alias Legend
        // =========================
        Row legendTitleRow = sheet.createRow(rowIdx++);
        legendTitleRow.setHeight((short) 550);
        Cell legendTitleCell = legendTitleRow.createCell(0);
        legendTitleCell.setCellValue("ORGANISATION ALIAS LEGEND");
        legendTitleCell.setCellStyle(styles.headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(
                legendTitleRow.getRowNum(),
                legendTitleRow.getRowNum(),
                0,
                1
        ));

        Row legendHeaderRow = sheet.createRow(rowIdx++);
        createCell(legendHeaderRow, 0, "Alias", styles.headerStyle);
        createCell(legendHeaderRow, 1, "Full Organisation Name", styles.headerStyle);

        for (Map.Entry<String, String> entry : aliasToFullName.entrySet()) {
            Row legendRow = sheet.createRow(rowIdx++);
            createCell(legendRow, 0, entry.getKey(), styles.textStyle);
            createCell(legendRow, 1, entry.getValue(), styles.textStyle);
        }

        // =========================
        // Widths
        // =========================
        sheet.setColumnWidth(0, 9000);  // Department / Alias
        sheet.setColumnWidth(1, 12000); // Works for summary and legend

        // summary table widths
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 3500);
        sheet.setColumnWidth(4, 3500);
        sheet.setColumnWidth(5, 3500);
        sheet.setColumnWidth(6, 4500);
        sheet.setColumnWidth(7, 5000);

        // organisation matrix widths
        for (int i = 1; i <= aliases.size(); i++) {
            sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i), 3800));
            if (sheet.getColumnWidth(i) < 3200) {
                sheet.setColumnWidth(i, 3200);
            }
        }

        int orgTotalColumnIndex = aliases.size() + 1;
        if (orgTotalColumnIndex <= 20) {
            sheet.setColumnWidth(orgTotalColumnIndex, 4500);
        }

        // print / freeze
        sheet.createFreezePane(0, 2);

        PrintSetup ps = sheet.getPrintSetup();
        ps.setLandscape(true);
        ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);

        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        sheet.setAutobreaks(true);
    }

    private record ActivityRenderData(
            Urc_Activities activity,
            List<BudgetItems> items,
            BigDecimal total,
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4,
            String accounts,
            String funding) {

    }

    private static class WorkplanStyles {

        CellStyle titleStyle;
        CellStyle subtitleStyle;
        CellStyle deptBannerStyle;
        CellStyle headerStyle;
        CellStyle quarterHeaderStyle;
        CellStyle textStyle;
        CellStyle amountStyle;
        CellStyle quarterValueStyle;
        CellStyle activityTextStyle;
        CellStyle activityAmountStyle;
        CellStyle activityQuarterStyle;
        CellStyle totalLabelStyle;
        CellStyle totalAmountStyle;
        CellStyle zebraTextStyle;
        CellStyle zebraAmountStyle;
        CellStyle zebraQuarterValueStyle;
        CellStyle zebraActivityTextStyle;
        CellStyle zebraActivityAmountStyle;
        CellStyle zebraActivityQuarterStyle;
        CellStyle detailBandStyle;
        CellStyle detailHeaderStyle;
        CellStyle detailTextStyle;
        CellStyle detailAmountStyle;
        CellStyle detailQuarterStyle;
        CellStyle zebraDetailTextStyle;
        CellStyle zebraDetailAmountStyle;
        CellStyle zebraDetailQuarterStyle;
    }

    private WorkplanStyles createWorkplanStyles(Workbook workbook) {
        WorkplanStyles s = new WorkplanStyles();

        DataFormat dataFormat = workbook.getCreationHelper().createDataFormat();

        Font titleFont = workbook.createFont();
        titleFont.setFontName("Calibri");
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);

        Font sectionFont = workbook.createFont();
        sectionFont.setFontName("Calibri");
        sectionFont.setBold(true);
        sectionFont.setFontHeightInPoints((short) 11);

        Font headerFont = workbook.createFont();
        headerFont.setFontName("Calibri");
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);

        Font normalFont = workbook.createFont();
        normalFont.setFontName("Calibri");
        normalFont.setFontHeightInPoints((short) 10);

        Font detailFont = workbook.createFont();
        detailFont.setFontName("Calibri");
        detailFont.setFontHeightInPoints((short) 9);

        Font detailHeaderFont = workbook.createFont();
        detailHeaderFont.setFontName("Calibri");
        detailHeaderFont.setBold(true);
        detailHeaderFont.setFontHeightInPoints((short) 9);

        Font activityBoldFont = workbook.createFont();
        activityBoldFont.setFontName("Calibri");
        activityBoldFont.setBold(true);
        activityBoldFont.setFontHeightInPoints((short) 10);

        s.titleStyle = workbook.createCellStyle();
        s.titleStyle.setFont(titleFont);
        s.titleStyle.setAlignment(HorizontalAlignment.CENTER);
        s.titleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        s.titleStyle.setWrapText(true);

        s.subtitleStyle = workbook.createCellStyle();
        s.subtitleStyle.setFont(sectionFont);
        s.subtitleStyle.setAlignment(HorizontalAlignment.CENTER);
        s.subtitleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        s.subtitleStyle.setWrapText(true);

        s.deptBannerStyle = workbook.createCellStyle();
        s.deptBannerStyle.setFont(sectionFont);
        s.deptBannerStyle.setAlignment(HorizontalAlignment.LEFT);
        s.deptBannerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        s.deptBannerStyle.setWrapText(true);
        s.deptBannerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.deptBannerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setAllBorders(s.deptBannerStyle, IndexedColors.BLACK.getIndex());

        s.headerStyle = workbook.createCellStyle();
        s.headerStyle.setFont(headerFont);
        s.headerStyle.setAlignment(HorizontalAlignment.CENTER);
        s.headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        s.headerStyle.setWrapText(true);
        s.headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setAllBorders(s.headerStyle, IndexedColors.BLACK.getIndex());

        s.quarterHeaderStyle = workbook.createCellStyle();
        s.quarterHeaderStyle.cloneStyleFrom(s.headerStyle);
        s.quarterHeaderStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        s.quarterHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.textStyle = workbook.createCellStyle();
        s.textStyle.setFont(normalFont);
        s.textStyle.setAlignment(HorizontalAlignment.LEFT);
        s.textStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.TOP);
        s.textStyle.setWrapText(true);
        setAllBorders(s.textStyle, IndexedColors.BLACK.getIndex());

        s.amountStyle = workbook.createCellStyle();
        s.amountStyle.cloneStyleFrom(s.textStyle);
        s.amountStyle.setAlignment(HorizontalAlignment.RIGHT);
        s.amountStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

        s.quarterValueStyle = workbook.createCellStyle();
        s.quarterValueStyle.cloneStyleFrom(s.amountStyle);
        s.quarterValueStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        s.quarterValueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.activityTextStyle = workbook.createCellStyle();
        s.activityTextStyle.cloneStyleFrom(s.textStyle);
        s.activityTextStyle.setFont(activityBoldFont);

        s.activityAmountStyle = workbook.createCellStyle();
        s.activityAmountStyle.cloneStyleFrom(s.amountStyle);
        s.activityAmountStyle.setFont(activityBoldFont);

        s.activityQuarterStyle = workbook.createCellStyle();
        s.activityQuarterStyle.cloneStyleFrom(s.quarterValueStyle);
        s.activityQuarterStyle.setFont(activityBoldFont);

        s.totalLabelStyle = workbook.createCellStyle();
        s.totalLabelStyle.cloneStyleFrom(s.headerStyle);

        s.totalAmountStyle = workbook.createCellStyle();
        s.totalAmountStyle.cloneStyleFrom(s.headerStyle);
        s.totalAmountStyle.setAlignment(HorizontalAlignment.RIGHT);
        s.totalAmountStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

        s.zebraTextStyle = workbook.createCellStyle();
        s.zebraTextStyle.cloneStyleFrom(s.textStyle);
        s.zebraTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.zebraTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.zebraAmountStyle = workbook.createCellStyle();
        s.zebraAmountStyle.cloneStyleFrom(s.amountStyle);
        s.zebraAmountStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.zebraAmountStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.zebraQuarterValueStyle = workbook.createCellStyle();
        s.zebraQuarterValueStyle.cloneStyleFrom(s.quarterValueStyle);
        s.zebraQuarterValueStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        s.zebraQuarterValueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.zebraActivityTextStyle = workbook.createCellStyle();
        s.zebraActivityTextStyle.cloneStyleFrom(s.zebraTextStyle);
        s.zebraActivityTextStyle.setFont(activityBoldFont);

        s.zebraActivityAmountStyle = workbook.createCellStyle();
        s.zebraActivityAmountStyle.cloneStyleFrom(s.zebraAmountStyle);
        s.zebraActivityAmountStyle.setFont(activityBoldFont);

        s.zebraActivityQuarterStyle = workbook.createCellStyle();
        s.zebraActivityQuarterStyle.cloneStyleFrom(s.zebraQuarterValueStyle);
        s.zebraActivityQuarterStyle.setFont(activityBoldFont);

        s.detailBandStyle = workbook.createCellStyle();
        s.detailBandStyle.cloneStyleFrom(s.textStyle);
        s.detailBandStyle.setFont(detailHeaderFont);
        s.detailBandStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.detailBandStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.detailHeaderStyle = workbook.createCellStyle();
        s.detailHeaderStyle.cloneStyleFrom(s.headerStyle);
        s.detailHeaderStyle.setFont(detailHeaderFont);
        s.detailHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        s.detailHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.detailTextStyle = workbook.createCellStyle();
        s.detailTextStyle.cloneStyleFrom(s.textStyle);
        s.detailTextStyle.setFont(detailFont);
        s.detailTextStyle.setIndention((short) 1);

        s.detailAmountStyle = workbook.createCellStyle();
        s.detailAmountStyle.cloneStyleFrom(s.amountStyle);
        s.detailAmountStyle.setFont(detailFont);

        s.detailQuarterStyle = workbook.createCellStyle();
        s.detailQuarterStyle.cloneStyleFrom(s.quarterValueStyle);
        s.detailQuarterStyle.setFont(detailFont);

        s.zebraDetailTextStyle = workbook.createCellStyle();
        s.zebraDetailTextStyle.cloneStyleFrom(s.detailTextStyle);
        s.zebraDetailTextStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.zebraDetailTextStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.zebraDetailAmountStyle = workbook.createCellStyle();
        s.zebraDetailAmountStyle.cloneStyleFrom(s.detailAmountStyle);
        s.zebraDetailAmountStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.zebraDetailAmountStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        s.zebraDetailQuarterStyle = workbook.createCellStyle();
        s.zebraDetailQuarterStyle.cloneStyleFrom(s.detailQuarterStyle);
        s.zebraDetailQuarterStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        s.zebraDetailQuarterStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return s;
    }

    private Map<Long, ActivityRenderData> preloadActivityRenderData(
            Budget budget,
            Set<Organisation> selectedOrganisations,
            Set<UrcDeptSectionAnlDimbgt> selectedSections
    ) {
        List<BudgetItems> allItems = sampleBudgetItemsService.findExpenseItemsByBudgetAndBudgetTypesAndDeptUnits(
                budget,
                selectedOrganisations,
                selectedSections
        );

        Map<Long, List<BudgetItems>> itemsByActivity = allItems.stream()
                .filter(item -> item.getActivity() != null && item.getActivity().getId() != null)
                .collect(Collectors.groupingBy(item -> item.getActivity().getId()));

        Map<Long, ActivityRenderData> result = new HashMap<>();

        for (Map.Entry<Long, List<BudgetItems>> entry : itemsByActivity.entrySet()) {
            List<BudgetItems> items = entry.getValue();
            Urc_Activities activity = items.get(0).getActivity();

            BigDecimal total = items.stream()
                    .map(item -> nz(item.getYearTotalFromQuarters()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q1 = items.stream()
                    .map(item -> nz(item.getQ1Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q2 = items.stream()
                    .map(item -> nz(item.getQ2Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q3 = items.stream()
                    .map(item -> nz(item.getQ3Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q4 = items.stream()
                    .map(item -> nz(item.getQ4Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String accounts = items.stream()
                    .map(item -> item.getCoacode() != null ? nzs(item.getCoacode().getCode()) : "")
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            String funding = items.stream()
                    .map(item -> item.getBudgetType() != null ? nzs(item.getBudgetType().getName()) : "")
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            result.put(entry.getKey(), new ActivityRenderData(
                    activity, items, total, q1, q2, q3, q4, accounts, funding
            ));
        }

        return result;
    }

    private record DepartmentTotals(
            String departmentName,
            int nextRowPointer,
            BigDecimal total,
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4) {

    }

    private record DepartmentSummaryRow(
            String departmentName,
            BigDecimal total,
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4,
            BigDecimal percentageContribution,
            BigDecimal varianceFromAverage) {

    }

    private void createHeaderRowWorkplanQtr2ForDepartment(
            Workbook workbook,
            Sheet sheet,
            Budget budget,
            Set<Organisation> selectedOrganisations,
            DepartmentBudget department,
            WorkplanStyles styles
    ) {
        createHeaderRowWorkplanQtr2ForDepartmentCombined(
                workbook,
                sheet,
                budget,
                selectedOrganisations,
                department,
                0,
                styles
        );
    }

    private void createCombinedDepartmentWorkplanSheet(
            Workbook workbook,
            Sheet sheet,
            Budget budget,
            Set<Organisation> selectedOrganisations,
            List<DepartmentBudget> departmentBudgets,
            WorkplanStyles styles
    ) {
        int rowPointer = 0;

        BigDecimal grandTotal = BigDecimal.ZERO;
        BigDecimal grandQ1 = BigDecimal.ZERO;
        BigDecimal grandQ2 = BigDecimal.ZERO;
        BigDecimal grandQ3 = BigDecimal.ZERO;
        BigDecimal grandQ4 = BigDecimal.ZERO;

        List<DepartmentTotals> collectedTotals = new ArrayList<>();

        for (DepartmentBudget department : departmentBudgets) {
            DepartmentTotals totals = createHeaderRowWorkplanQtr2ForDepartmentCombined(
                    workbook,
                    sheet,
                    budget,
                    selectedOrganisations,
                    department,
                    rowPointer,
                    styles
            );

            collectedTotals.add(totals);

            grandTotal = grandTotal.add(nz(totals.total()));
            grandQ1 = grandQ1.add(nz(totals.q1()));
            grandQ2 = grandQ2.add(nz(totals.q2()));
            grandQ3 = grandQ3.add(nz(totals.q3()));
            grandQ4 = grandQ4.add(nz(totals.q4()));

            rowPointer = totals.nextRowPointer() + 2;
        }

        Row totalRow = sheet.createRow(rowPointer);
        totalRow.setHeight((short) 600);

        Cell labelCell = totalRow.createCell(0);
        labelCell.setCellValue("GRAND TOTAL - ALL DEPARTMENTS");
        labelCell.setCellStyle(styles.totalLabelStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowPointer, rowPointer, 0, 1));
        createNumericCell(totalRow, 2, grandTotal, styles.totalAmountStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowPointer, rowPointer, 2, 4));

        createNumericCell(totalRow, 5, grandQ1, styles.totalAmountStyle);
        createNumericCell(totalRow, 6, grandQ2, styles.totalAmountStyle);
        createNumericCell(totalRow, 7, grandQ3, styles.totalAmountStyle);
        createNumericCell(totalRow, 8, grandQ4, styles.totalAmountStyle);

        for (int c = 9; c <= 12; c++) {
            Cell cell = totalRow.createCell(c);
            cell.setCellStyle(styles.totalLabelStyle);
        }

        PrintSetup ps = sheet.getPrintSetup();
        ps.setLandscape(true);
        ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);

        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        sheet.createFreezePane(0, 4);

        // dashboard data
        List<DepartmentOrganisationBudgetRow> departmentOrganisationRows
                = buildDepartmentOrganisationBudgetRows(
                        budget,
                        selectedOrganisations,
                        departmentBudgets
                );

        Sheet dashboardSheet = workbook.createSheet(
                WorkbookUtil.createSafeSheetName("Dashboard Summary")
        );
        createDashboardSummarySheet(
                workbook,
                dashboardSheet,
                collectedTotals,
                departmentOrganisationRows,
                selectedOrganisations,
                styles
        );
    }

    private Map<String, String> buildOrganisationAliases(Set<Organisation> selectedOrganisations) {
        Map<String, String> aliasToFullName = new LinkedHashMap<>();
        Set<String> usedAliases = new HashSet<>();

        List<String> fullNames = selectedOrganisations.stream()
                .filter(Objects::nonNull)
                .map(org -> nzs(org.getName()).trim())
                .filter(name -> !name.isBlank())
                .distinct()
                .sorted()
                .toList();

        for (String fullName : fullNames) {
            String alias = createOrganisationAlias(fullName);

            String baseAlias = alias;
            int counter = 2;
            while (usedAliases.contains(alias)) {
                alias = baseAlias + counter;
                counter++;
            }

            usedAliases.add(alias);
            aliasToFullName.put(alias, fullName);
        }

        return aliasToFullName;
    }

    private String createOrganisationAlias(String fullName) {
        String cleaned = nzs(fullName).trim();
        if (cleaned.isBlank()) {
            return "";
        }

        // If already short enough, use as-is
        if (cleaned.length() <= 12 && !cleaned.contains(" ")) {
            return cleaned;
        }

        String[] parts = cleaned.split("\\s+");

        // Build initials from words
        String initials = Arrays.stream(parts)
                .filter(p -> !p.isBlank())
                .map(p -> String.valueOf(Character.toUpperCase(p.charAt(0))))
                .collect(Collectors.joining());

        if (!initials.isBlank() && initials.length() <= 8) {
            return initials;
        }

        // fallback: first 10 chars, compacted
        String compact = cleaned.replaceAll("[^A-Za-z0-9]", "");
        if (compact.length() > 10) {
            compact = compact.substring(0, 10);
        }

        return compact;
    }

    private void applySectionCards(
            Sheet sheet,
            List<SectionCard> cards,
            short borderColor
    ) {
        for (SectionCard card : cards) {
            applySectionCardBorder(
                    sheet,
                    card.startRow(),
                    card.endRow(),
                    card.startCol(),
                    card.endCol(),
                    BorderStyle.MEDIUM,
                    borderColor
            );
        }
    }

    private void applySectionCardBorder(
            Sheet sheet,
            int firstRow,
            int lastRow,
            int firstCol,
            int lastCol,
            BorderStyle borderStyle,
            short borderColor
    ) {
        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);

        RegionUtil.setBorderTop(borderStyle, region, sheet);
        RegionUtil.setTopBorderColor(borderColor, region, sheet);

        RegionUtil.setBorderBottom(borderStyle, region, sheet);
        RegionUtil.setBottomBorderColor(borderColor, region, sheet);

        RegionUtil.setBorderLeft(borderStyle, region, sheet);
        RegionUtil.setLeftBorderColor(borderColor, region, sheet);

        RegionUtil.setBorderRight(borderStyle, region, sheet);
        RegionUtil.setRightBorderColor(borderColor, region, sheet);
    }

    private void createBudgetItemSubHeaderWithoutOrganisation(Row row, CellStyle detailHeaderStyle) {
        createCell(row, 0, "", detailHeaderStyle);
        createCell(row, 1, "Item", detailHeaderStyle);
        createCell(row, 2, "Amount", detailHeaderStyle);
        createCell(row, 3, "Code", detailHeaderStyle);
        createCell(row, 4, "Funding", detailHeaderStyle);
        createCell(row, 5, "Q1", detailHeaderStyle);
        createCell(row, 6, "Q2", detailHeaderStyle);
        createCell(row, 7, "Q3", detailHeaderStyle);
        createCell(row, 8, "Q4", detailHeaderStyle);
        createCell(row, 9, "Code Description", detailHeaderStyle);
        createCell(row, 10, "Section", detailHeaderStyle);
        createCell(row, 11, "", detailHeaderStyle);
        createCell(row, 12, "", detailHeaderStyle);
    }

    private record SectionCard(int startRow, int endRow, int startCol, int endCol) {

    }

    private String nzs(String value) {
        return value == null ? "" : value;
    }

    private void setAllBorders(CellStyle style, short colorIndex) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(colorIndex);
        style.setBottomBorderColor(colorIndex);
        style.setLeftBorderColor(colorIndex);
        style.setRightBorderColor(colorIndex);
    }

    private void applyRegionBorder(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);

        RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
        RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
    }

    private void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int columnIndex, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value.doubleValue() : 0.0);
        cell.setCellStyle(style);
    }

    private static boolean isOverlappingWithExistingRegions(Sheet sheet, CellRangeAddress newRegion) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress existingRegion = sheet.getMergedRegion(i);
            if (newRegion.isInRange(existingRegion.getFirstRow(), existingRegion.getFirstColumn())
                    || newRegion.isInRange(existingRegion.getLastRow(), existingRegion.getLastColumn())) {
                return true; // Overlapping
            }
        }
        return false; // Not overlapping
    }

// Method to add a merged region based on the sheet type
    private static void addMergedRegion(Sheet sheet, CellRangeAddress region) {
        if (sheet instanceof XSSFSheet) {
            ((XSSFSheet) sheet).addMergedRegion(region);
        } else if (sheet instanceof HSSFSheet) {
            ((HSSFSheet) sheet).addMergedRegion(region);
        }
    }

    public boolean isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(Budget budget, List<UrcDeptSectionAnlDimbgt> deptUnits) {

        return sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(budget, deptUnits, budgetType2.getSelectedItems());
    }

    public boolean isSumBudgetDeptUnitsGreaterThanZero(Budget budget, List<UrcDeptSectionAnlDimbgt> deptUnits) {

        return sampleBudgetItemsService.isSumBudgetDeptUnitsGreaterThanZero(budget, deptUnits, budgetType2.getSelectedItems());
    }
}
