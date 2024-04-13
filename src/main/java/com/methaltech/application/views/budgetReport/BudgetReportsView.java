package com.methaltech.application.views.budgetReport;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.GenerateQtrFromFy;
import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.Qtr;
import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.Report;
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
import com.methaltech.application.data.entity.oldbgtool.DepartmentSection;
import com.methaltech.application.data.entity.oldbgtool.DepartmentUnit;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.data.livedata.service.UrcAcntService;
import com.methaltech.application.data.oldbgtool.service.BudgetSubItemService;
import com.methaltech.application.data.oldbgtool.service.DepartmentSectionService;
import com.methaltech.application.data.oldbgtool.service.DepartmentUnitService;
import com.methaltech.application.data.oldbgtool.service.ItemService2;
import com.methaltech.application.data.oldbgtool.service.ProgramActivityService;
import com.methaltech.application.data.oldbgtool.service.ProgrammeService;
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
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
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

@PageTitle("Budget Reports Downloads")
@Route(value = "budgetReport", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO", "USER", "HOD"})
public class BudgetReportsView extends Div {

    private final ComboBox<Budget> comboBox = new ComboBox<>("Select Budget");
    private final ComboBox<Budget> comboBox2 = new ComboBox<>("Select Budget");
    private final UserService userService;
    private final UrcAcntService urcAcntService;
    private final Coalevel1Service sampleCoalevel1Service;
    private final CoaService sampleCoaService;
    private final FreightVolumesService sampleFreightVolumesService;
    private final BudgetItemsService sampleBudgetItemsService;
    private final CustomDetailedBudgetReportImpService sampleCustomDetailedBudgetReportImpService;
    private final CustomDetailedBudgetReportService sampleCustomDetailedBudgetReportService;
    private final OrganisationService sampleOrganisationService;
    private final BudgetService budgetService;
    private final DepartmentSectionService departmentsectionService;
    private final DepartmentUnitService departmentUnitService;
    private final SamplePersonService samplePersonService;
    private final ProgrammeService programmeService;
    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final ProgramActivityService programmeActivityService;
    private final BudgetSubItemService budgetSubItemService;
    private final ItemService2 itemService;
    private final SALFLDGService samopleSALFLDGService;
    private Grid<DepartmentUnit> select = new Grid<>(DepartmentUnit.class, false);
    private final List<DepartmentUnit> selected = new ArrayList<>();
    private final List<SamplePerson> person = new ArrayList<>();
    private List<DepartmentSection> selectedSections = new ArrayList<>();
    private List<URC_Priority_Areas> programmes = new ArrayList<>();
    private List<Urc_Activities> programmesActivities = new ArrayList<>();
    private List<BudgetItems> budgetList = new ArrayList<>();
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sections = new MultiSelectComboBox<>("Sections");
    private MultiSelectComboBox<Organisation> budgetType = new MultiSelectComboBox<>("Budget Type");
    private MultiSelectComboBox<Organisation> budgetType2 = new MultiSelectComboBox<>("Budget Type");
    private User user;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    List<Report> reportColumns = new ArrayList<>();
    List<CustomDetailedBudgetReportImp> gridCustomDetailedBudgetReportImpItems = new ArrayList<>();
    private MultiSelectComboBox<Report> reportColumnsCombo = new MultiSelectComboBox<>("Report Columns");
    private MultiSelectComboBox<Report> reportColumnsCombo2 = new MultiSelectComboBox<>("Report Columns");
    private CustomDetailedBudgetReportImp sampleCustomDetailedBudgetReportImp = new CustomDetailedBudgetReportImp();
    private Grid<CustomDetailedBudgetReportImp> gridCustomDetailedBudgetReportImp = new Grid<>(CustomDetailedBudgetReportImp.class, false);
    private Grid<CustomDetailedBudgetReport> gridCustomDetailedBudgetReport = new Grid<>(CustomDetailedBudgetReport.class, false);
    private TextField CustomDetailedBudgetReporttextField = new TextField();
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> CustomDetailedBudgetReportcombo = new MultiSelectComboBox<>("Report Columns");
    private GenerateQtrFromFy gen;

    public BudgetReportsView(UserService userService, BudgetService budgetService,
            DepartmentSectionService departmentsectionService, DepartmentUnitService departmentUnitService,
            SamplePersonService samplePersonService, ProgrammeService programmeService,
            ProgramActivityService programmeActivityService, BudgetSubItemService budgetSubItemService,
            ItemService2 itemService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, OrganisationService sampleOrganisationService,
            BudgetItemsService sampleBudgetItemsService, Coalevel1Service sampleCoalevel1Service, URC_Priority_AreasService sampleURC_Priority_AreasService,
            Urc_ActivitiesService sampleUrc_ActivitiesService, CustomDetailedBudgetReportImpService sampleCustomDetailedBudgetReportImpService,
            CustomDetailedBudgetReportService sampleCustomDetailedBudgetReportService, UrcAcntService urcAcntService,
            FreightVolumesService sampleFreightVolumesService, CoaService sampleCoaService, SALFLDGService samopleSALFLDGService,
            GenerateQtrFromFy gen) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.departmentsectionService = departmentsectionService;
        this.departmentUnitService = departmentUnitService;
        this.samplePersonService = samplePersonService;
        this.programmeService = programmeService;
        this.programmeActivityService = programmeActivityService;
        this.budgetSubItemService = budgetSubItemService;
        this.itemService = itemService;
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
        this.gen = gen;

        reportColumns.add(Report.BASIC);
        reportColumns.add(Report.QTR1);
        reportColumns.add(Report.QTR2);
        reportColumns.add(Report.QTR3);
        reportColumns.add(Report.QTR4);
        reportColumns.add(Report.SECTION);
        reportColumns.add(Report.TRAINING_DETAILS);
        reportColumnsCombo.setItems(reportColumns);
        reportColumnsCombo.setValue(Report.BASIC);

        reportColumnsCombo2.setItems(reportColumns);
        reportColumnsCombo2.setValue(Report.BASIC);

        VerticalLayout sheet1 = new VerticalLayout();
        VerticalLayout sheet2 = new VerticalLayout();
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
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
        comboBox.addValueChangeListener(e -> {
            budgetType.setItems(sampleOrganisationService.findByBudgetList(e.getValue()));
        });

        comboBox2.addValueChangeListener(e -> {
            budgetType2.setItems(sampleOrganisationService.findByBudgetList(e.getValue()));
        });
        if (!comboBox.isEmpty()) {
            budgetType.setItems(sampleOrganisationService.findByBudgetList(comboBox.getValue()));
        }

        if (!comboBox2.isEmpty()) {
            budgetType2.setItems(sampleOrganisationService.findByBudgetList(comboBox.getValue()));
        }
        CustomDetailedBudgetReportcombo.setItems(user.getDeptsection());
        CustomDetailedBudgetReportcombo.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        refreshCustomDetailedBudgetReportImpItems();
        budgetType.setItemLabelGenerator(Organisation::getName);
        budgetType2.setItemLabelGenerator(Organisation::getName);
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        AccordionPanel panel0 = new AccordionPanel("Programme Budget");
        AccordionPanel panel1 = new AccordionPanel("Activity Budget");
        AccordionPanel panel2 = new AccordionPanel("Budget By Account Code");
        AccordionPanel panel3 = new AccordionPanel("Income Budget");
        AccordionPanel panel4 = new AccordionPanel("Revenue Expenditure Budget");
        AccordionPanel panel5 = new AccordionPanel("Capital Expenditure Budget");
        AccordionPanel panel6 = new AccordionPanel("Summary Budget");
        // Add content to the panels
        panel0.addContent(new Button("Budget Activities in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel0.addContent(new Button("Budget Activities in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel1.addContent(new Button("Budget Activities in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel1.addContent(new Button("Budget Activities in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel2.addContent(new Button("Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleAccountCodeDetailClick()));
        panel2.addContent(new Button("Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel3.addContent(new Button("Income Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel3.addContent(new Button("Income Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel4.addContent(new Button("Revenue Expenditure Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel4.addContent(new Button("Revenue Expenditure Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel4.addContent(new Button("Revenue Expenditure Budget By Account Code in Detail By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel4.addContent(new Button("Revenue Expenditure Budget By Account Code in Summary  By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel5.addContent(new Button("Capital Expenditure Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel5.addContent(new Button("Capital Expenditure Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel5.addContent(new Button("Capital Expenditure Budget By Account Code in Detail By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel5.addContent(new Button("Capital Expenditure Budget By Account Code in Summary  By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel6.addContent(new Button("Summary Budget", new Icon(VaadinIcon.DOWNLOAD), e -> handleSummaryBudgetClick()));
        panel6.addContent(new Button("Quarterly Summary Budget", new Icon(VaadinIcon.DOWNLOAD), e -> handleQtrSummaryBudgetClick()));
        panel6.addContent(new Button("Quarterly Summary Performance Budget", new Icon(VaadinIcon.DOWNLOAD), e -> handleActualsQtrSummaryBudgetClick()));
        panel6.addContent(new Button("Summary Analysis Budget", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        // Add the panels to the Accordion
        accordion.add(panel0);
        accordion.add(panel1);
        accordion.add(panel2);
        accordion.add(panel3);
        accordion.add(panel4);
        accordion.add(panel5);
        accordion.add(panel6);

        Accordion accordion2 = new Accordion();
        accordion2.setWidthFull();
        AccordionPanel panel02 = new AccordionPanel("Programme Budget");
        AccordionPanel panel12 = new AccordionPanel("Activity Budget");
        AccordionPanel panel22 = new AccordionPanel("Budget By Account Code");
        AccordionPanel panel32 = new AccordionPanel("Income Budget");
        AccordionPanel panel42 = new AccordionPanel("Revenue Expenditure Budget");
        AccordionPanel panel52 = new AccordionPanel("Capital Expenditure Budget");
        // Add content to the panels
        panel02.addContent(new Button("Budget Activities in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel02.addContent(new Button("Budget Activities in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel12.addContent(new Button("Budget Activities in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel12.addContent(new Button("Budget Activities in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel22.addContent(new Button("Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleCustomAccountCodeDetailClick()));
        panel22.addContent(new Button("Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel32.addContent(new Button("Income Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel32.addContent(new Button("Income Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel42.addContent(new Button("Revenue Expenditure Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel42.addContent(new Button("Revenue Expenditure Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel42.addContent(new Button("Revenue Expenditure Budget By Account Code in Detail By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel42.addContent(new Button("Revenue Expenditure Budget By Account Code in Summary  By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));

        panel52.addContent(new Button("Capital Expenditure Budget By Account Code in Detail", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel52.addContent(new Button("Capital Expenditure Budget By Account Code in Summary", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel52.addContent(new Button("Capital Expenditure Budget By Account Code in Detail By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        panel52.addContent(new Button("Capital Expenditure Budget By Account Code in Summary  By Activity", new Icon(VaadinIcon.DOWNLOAD), e -> handleActivityDetailClick()));
        // Add the panels to the Accordion
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        accordion2.add(panel02);
        accordion2.add(panel12);
        accordion2.add(panel22);
        accordion2.add(panel32);
        accordion2.add(panel42);
        accordion2.add(panel52);

        HorizontalLayout hlay = new HorizontalLayout();
        hlay.setWidthFull();
        hlay.add(comboBox, budgetType, sections, reportColumnsCombo);
        hlay.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, comboBox);
        hlay.setAlignItems(FlexComponent.Alignment.BASELINE);
        sheet1.add(hlay, accordion);

        HorizontalLayout hlay2 = new HorizontalLayout();
        hlay2.setWidthFull();
        hlay2.add(comboBox2, budgetType2, reportColumnsCombo2);
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

        SplitLayout splitLayout = new SplitLayout(sheet2, splitLayout2);
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
        Button delete = new Button("Delete");
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
                sampleCustomDetailedBudgetReportImpService.deleteReportImp(gridCustomDetailedBudgetReportImp.asSingleSelect().getValue().getId());
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

    private String formatDeptSection(CustomDetailedBudgetReport report) {
        if (report != null) {
            return report.getDeptsection().stream()
                    .map(UrcDeptSectionAnlDimbgt::getNAME)
                    .collect(Collectors.joining(", "));
        }
        return "";
    }

    private void refreshCustomDetailedBudgetReportImpItems() {
        gridCustomDetailedBudgetReportImp.deselectAll();
        gridCustomDetailedBudgetReportImp.setItems(sampleCustomDetailedBudgetReportImpService.getAllReportsImpByUser(user));
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
        if (comboBox2.isEmpty() || budgetType2.isEmpty() || gridCustomDetailedBudgetReportImp.asSingleSelect().isEmpty()) {
            warningNotification("Make sure that Neither Section nor Budget nor Budget Type is empty 444");
        } else {
            exportAndDownloadCustomAccountcodeBudgetItems2(comboBox2.getValue());
        }
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

    private void createHeaderAndBodyAccountCodeDetailByBudgetRow2(Workbook workbook, Sheet sheet, Set<UrcDeptSectionAnlDimbgt> sect) {
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
        header2Cell.setCellValue(HeaderExcel2("ACCOUNT CODE").toUpperCase());
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
        List<UrcDeptSectionAnlDimbgt> selectedSections = sect.stream().toList();
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero2(comboBox2.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("INCOME");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRange);
            setBottomBorderForRegion(sheet, cellRange);
            List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems());
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
                incometotal2.createCell(7).setCellValue(sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems()).doubleValue());

                CellStyle style23 = incometotal2.getSheet().getWorkbook().createCellStyle();
                style23.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

                Cell cell8 = incometotal2.createCell((short) 8);
                cell8.setCellType(CellType.NUMERIC);
                cell8.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jul").doubleValue());
                cell8.setCellStyle(style23);

                Cell cell9 = incometotal2.createCell((short) 9);
                cell9.setCellType(CellType.NUMERIC);
                cell9.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "aug").doubleValue());
                cell9.setCellStyle(style23);

                Cell cell10 = incometotal2.createCell((short) 10);
                cell10.setCellType(CellType.NUMERIC);
                cell10.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "sep").doubleValue());
                cell10.setCellStyle(style23);

                Cell cell11 = incometotal2.createCell((short) 11);
                cell11.setCellType(CellType.NUMERIC);
                cell11.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "oct").doubleValue());
                cell11.setCellStyle(style23);

                Cell cell12 = incometotal2.createCell((short) 12);
                cell12.setCellType(CellType.NUMERIC);
                cell12.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "nov").doubleValue());
                cell12.setCellStyle(style23);

                Cell cell13 = incometotal2.createCell((short) 13);
                cell13.setCellType(CellType.NUMERIC);
                cell13.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "dec").doubleValue());
                cell13.setCellStyle(style23);

                Cell cell14 = incometotal2.createCell((short) 14);
                cell14.setCellType(CellType.NUMERIC);
                cell14.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jan").doubleValue());
                cell14.setCellStyle(style23);

                Cell cell15 = incometotal2.createCell((short) 15);
                cell15.setCellType(CellType.NUMERIC);
                cell15.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "feb").doubleValue());
                cell15.setCellStyle(style23);

                Cell cell16 = incometotal2.createCell((short) 16);
                cell16.setCellType(CellType.NUMERIC);
                cell16.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "mar").doubleValue());
                cell16.setCellStyle(style23);

                Cell cell17 = incometotal2.createCell((short) 17);
                cell17.setCellType(CellType.NUMERIC);
                cell17.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "apr").doubleValue());
                cell17.setCellStyle(style23);

                Cell cell18 = incometotal2.createCell((short) 18);
                cell18.setCellType(CellType.NUMERIC);
                cell18.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "may").doubleValue());
                cell18.setCellStyle(style23);

                Cell cell19 = incometotal2.createCell((short) 19);
                cell19.setCellType(CellType.NUMERIC);
                cell19.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jun").doubleValue());
                cell19.setCellStyle(style23);

                incometotal2.createCell(20).setCellValue("");
                incometotal2.createCell(21).setCellValue("");
                incometotal2.createCell(22).setCellValue("");
                incometotal2.createCell(23).setCellValue("");
                incometotal2.createCell(24).setCellValue("");
                // CellRangeAddress cellRangeT3 = new CellRangeAddress(tr, tr, 7, 24);
                // sheet.addMergedRegion(cellRangeT3);
                budgetList = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems());

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
            cell7.setCellValue(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems()).doubleValue());
            cell7.setCellStyle(style22);

            Cell cell8 = incometotal2.createCell((short) 8);
            cell8.setCellType(CellType.NUMERIC);
            cell8.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jul").doubleValue());
            cell8.setCellStyle(style22);

            Cell cell9 = incometotal2.createCell((short) 9);
            cell9.setCellType(CellType.NUMERIC);
            cell9.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "aug").doubleValue());
            cell9.setCellStyle(style22);

            Cell cell10 = incometotal2.createCell((short) 10);
            cell10.setCellType(CellType.NUMERIC);
            cell10.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "sep").doubleValue());
            cell10.setCellStyle(style22);

            Cell cell11 = incometotal2.createCell((short) 11);
            cell11.setCellType(CellType.NUMERIC);
            cell11.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "oct").doubleValue());
            cell11.setCellStyle(style22);

            Cell cell12 = incometotal2.createCell((short) 12);
            cell12.setCellType(CellType.NUMERIC);
            cell12.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "nov").doubleValue());
            cell7.setCellStyle(style22);

            Cell cell13 = incometotal2.createCell((short) 13);
            cell13.setCellType(CellType.NUMERIC);
            cell13.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "dec").doubleValue());
            cell13.setCellStyle(style22);

            Cell cell14 = incometotal2.createCell((short) 14);
            cell14.setCellType(CellType.NUMERIC);
            cell14.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jan").doubleValue());
            cell14.setCellStyle(style22);

            Cell cell15 = incometotal2.createCell((short) 15);
            cell15.setCellType(CellType.NUMERIC);
            cell15.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "feb").doubleValue());
            cell15.setCellStyle(style22);

            Cell cell16 = incometotal2.createCell((short) 16);
            cell16.setCellType(CellType.NUMERIC);
            cell16.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "mar").doubleValue());
            cell16.setCellStyle(style22);

            Cell cell17 = incometotal2.createCell((short) 17);
            cell17.setCellType(CellType.NUMERIC);
            cell17.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "apr").doubleValue());
            cell17.setCellStyle(style22);

            Cell cell18 = incometotal2.createCell((short) 18);
            cell18.setCellType(CellType.NUMERIC);
            cell18.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "may").doubleValue());
            cell18.setCellStyle(style22);

            Cell cell19 = incometotal2.createCell((short) 19);
            cell19.setCellType(CellType.NUMERIC);
            cell19.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jun").doubleValue());
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
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero2(comboBox2.getValue(), coal, selectedSections) == true) {

            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("REVENUE EXPENDITURE");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRange);
            setBottomBorderForRegion(sheet, cellRange);

            List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems());
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
                incometotal2.createCell(7).setCellValue(sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems()).doubleValue());
                CellStyle style23 = incometotal2.getSheet().getWorkbook().createCellStyle();
                style23.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

                Cell cell8 = incometotal2.createCell((short) 8);
                cell8.setCellType(CellType.NUMERIC);
                cell8.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jul").doubleValue());
                cell8.setCellStyle(style23);

                Cell cell9 = incometotal2.createCell((short) 9);
                cell9.setCellType(CellType.NUMERIC);
                cell9.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "aug").doubleValue());
                cell9.setCellStyle(style23);

                Cell cell10 = incometotal2.createCell((short) 10);
                cell10.setCellType(CellType.NUMERIC);
                cell10.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "sep").doubleValue());
                cell10.setCellStyle(style23);

                Cell cell11 = incometotal2.createCell((short) 11);
                cell11.setCellType(CellType.NUMERIC);
                cell11.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "oct").doubleValue());
                cell11.setCellStyle(style23);

                Cell cell12 = incometotal2.createCell((short) 12);
                cell12.setCellType(CellType.NUMERIC);
                cell12.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "nov").doubleValue());
                cell12.setCellStyle(style23);

                Cell cell13 = incometotal2.createCell((short) 13);
                cell13.setCellType(CellType.NUMERIC);
                cell13.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "dec").doubleValue());
                cell13.setCellStyle(style23);

                Cell cell14 = incometotal2.createCell((short) 14);
                cell14.setCellType(CellType.NUMERIC);
                cell14.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jan").doubleValue());
                cell14.setCellStyle(style23);

                Cell cell15 = incometotal2.createCell((short) 15);
                cell15.setCellType(CellType.NUMERIC);
                cell15.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "feb").doubleValue());
                cell15.setCellStyle(style23);

                Cell cell16 = incometotal2.createCell((short) 16);
                cell16.setCellType(CellType.NUMERIC);
                cell16.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "mar").doubleValue());
                cell16.setCellStyle(style23);

                Cell cell17 = incometotal2.createCell((short) 17);
                cell17.setCellType(CellType.NUMERIC);
                cell17.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "apr").doubleValue());
                cell17.setCellStyle(style23);

                Cell cell18 = incometotal2.createCell((short) 18);
                cell18.setCellType(CellType.NUMERIC);
                cell18.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "may").doubleValue());
                cell18.setCellStyle(style23);

                Cell cell19 = incometotal2.createCell((short) 19);
                cell19.setCellType(CellType.NUMERIC);
                cell19.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jun").doubleValue());
                cell19.setCellStyle(style23);

                incometotal2.createCell(20).setCellValue("");
                incometotal2.createCell(21).setCellValue("");
                incometotal2.createCell(22).setCellValue("");
                incometotal2.createCell(23).setCellValue("");
                incometotal2.createCell(24).setCellValue("");
                budgetList = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems());

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
            cell7.setCellValue(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems()).doubleValue());
            CellStyle style = incometotal2.getSheet().getWorkbook().createCellStyle();
            style.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
            cell7.setCellStyle(style);
            Cell cell8 = incometotal2.createCell((short) 8);
            cell8.setCellType(CellType.NUMERIC);
            cell8.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jul").doubleValue());
            cell8.setCellStyle(style);

            Cell cell9 = incometotal2.createCell((short) 9);
            cell9.setCellType(CellType.NUMERIC);
            cell9.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "aug").doubleValue());
            cell9.setCellStyle(style);

            Cell cell10 = incometotal2.createCell((short) 10);
            cell10.setCellType(CellType.NUMERIC);
            cell10.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "sep").doubleValue());
            cell10.setCellStyle(style);

            Cell cell11 = incometotal2.createCell((short) 11);
            cell11.setCellType(CellType.NUMERIC);
            cell11.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "oct").doubleValue());
            cell11.setCellStyle(style);

            Cell cell12 = incometotal2.createCell((short) 12);
            cell12.setCellType(CellType.NUMERIC);
            cell12.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "nov").doubleValue());
            cell12.setCellStyle(style);

            Cell cell13 = incometotal2.createCell((short) 13);
            cell13.setCellType(CellType.NUMERIC);
            cell13.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "dec").doubleValue());
            cell13.setCellStyle(style);

            Cell cell14 = incometotal2.createCell((short) 14);
            cell14.setCellType(CellType.NUMERIC);
            cell14.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jan").doubleValue());
            cell14.setCellStyle(style);

            Cell cell15 = incometotal2.createCell((short) 15);
            cell15.setCellType(CellType.NUMERIC);
            cell15.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "feb").doubleValue());
            cell15.setCellStyle(style);

            Cell cell16 = incometotal2.createCell((short) 16);
            cell16.setCellType(CellType.NUMERIC);
            cell16.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "mar").doubleValue());
            cell16.setCellStyle(style);

            Cell cell17 = incometotal2.createCell((short) 17);
            cell17.setCellType(CellType.NUMERIC);
            cell17.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "apr").doubleValue());
            cell17.setCellStyle(style);

            Cell cell18 = incometotal2.createCell((short) 18);
            cell18.setCellType(CellType.NUMERIC);
            cell18.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "may").doubleValue());
            cell18.setCellStyle(style);

            Cell cell19 = incometotal2.createCell((short) 19);
            cell19.setCellType(CellType.NUMERIC);
            cell19.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jun").doubleValue());

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
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero2(comboBox2.getValue(), coal, selectedSections) == true) {
            tr++;
            Row income = sheet.createRow((short) tr);
            income.createCell(0).setCellValue("CAPITAL EXPENDITURE");
            activityrowIndex.add((int) tr);
            catrowIndex.add((int) tr);
            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            CellRangeAddress cellRange = new CellRangeAddress(tr, tr, 0, 24);
            sheet.addMergedRegion(cellRange);
            setBottomBorderForRegion(sheet, cellRange);
            List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems());
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
                cell7.setCellValue(sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems()).doubleValue());
                cell7.setCellStyle(style);
                CellStyle style23 = incometotal2.getSheet().getWorkbook().createCellStyle();
                style23.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));

                Cell cell8 = incometotal2.createCell((short) 8);
                cell8.setCellType(CellType.NUMERIC);
                cell8.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jul").doubleValue());
                cell8.setCellStyle(style23);

                Cell cell9 = incometotal2.createCell((short) 9);
                cell9.setCellType(CellType.NUMERIC);
                cell9.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "aug").doubleValue());
                cell9.setCellStyle(style23);

                Cell cell10 = incometotal2.createCell((short) 10);
                cell10.setCellType(CellType.NUMERIC);
                cell10.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "sep").doubleValue());
                cell10.setCellStyle(style23);

                Cell cell11 = incometotal2.createCell((short) 11);
                cell11.setCellType(CellType.NUMERIC);
                cell11.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "oct").doubleValue());
                cell11.setCellStyle(style23);

                Cell cell12 = incometotal2.createCell((short) 12);
                cell12.setCellType(CellType.NUMERIC);
                cell12.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "nov").doubleValue());
                cell12.setCellStyle(style23);

                Cell cell13 = incometotal2.createCell((short) 13);
                cell13.setCellType(CellType.NUMERIC);
                cell13.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "dec").doubleValue());
                cell13.setCellStyle(style23);

                Cell cell14 = incometotal2.createCell((short) 14);
                cell14.setCellType(CellType.NUMERIC);
                cell14.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jan").doubleValue());
                cell14.setCellStyle(style23);

                Cell cell15 = incometotal2.createCell((short) 15);
                cell15.setCellType(CellType.NUMERIC);
                cell15.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "feb").doubleValue());
                cell15.setCellStyle(style23);

                Cell cell16 = incometotal2.createCell((short) 16);
                cell16.setCellType(CellType.NUMERIC);
                cell16.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "mar").doubleValue());
                cell16.setCellStyle(style23);

                Cell cell17 = incometotal2.createCell((short) 17);
                cell17.setCellType(CellType.NUMERIC);
                cell17.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "apr").doubleValue());
                cell17.setCellStyle(style23);

                Cell cell18 = incometotal2.createCell((short) 18);
                cell18.setCellType(CellType.NUMERIC);
                cell18.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "may").doubleValue());
                cell18.setCellStyle(style23);

                Cell cell19 = incometotal2.createCell((short) 19);
                cell19.setCellType(CellType.NUMERIC);
                cell19.setCellValue(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems(), "jun").doubleValue());
                cell19.setCellStyle(style23);

                incometotal2.createCell(20).setCellValue("");
                incometotal2.createCell(21).setCellValue("");
                incometotal2.createCell(22).setCellValue("");
                incometotal2.createCell(23).setCellValue("");
                incometotal2.createCell(24).setCellValue("");
                budgetList = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(comboBox2.getValue(), cao, selectedSections, budgetType2.getSelectedItems());

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
            cell7.setCellValue(sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems()).doubleValue());
            cell7.setCellStyle(style2);

            Cell cell8 = incometotal2.createCell((short) 8);
            cell8.setCellType(CellType.NUMERIC);
            // cell8.setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jul")));
            cell8.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jul").doubleValue());
            cell8.setCellStyle(style2);

            Cell cell9 = incometotal2.createCell((short) 9);
            cell9.setCellType(CellType.NUMERIC);
            cell9.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "aug").doubleValue());
            cell9.setCellStyle(style2);

            Cell cell10 = incometotal2.createCell((short) 10);
            cell10.setCellType(CellType.NUMERIC);
            cell10.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "sep").doubleValue());

            Cell cell11 = incometotal2.createCell((short) 11);
            cell11.setCellType(CellType.NUMERIC);
            cell11.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "oct").doubleValue());
            cell11.setCellStyle(style2);

            Cell cell12 = incometotal2.createCell((short) 12);
            cell12.setCellType(CellType.NUMERIC);
            cell12.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "nov").doubleValue());
            cell12.setCellStyle(style2);

            Cell cell13 = incometotal2.createCell((short) 13);
            cell13.setCellType(CellType.NUMERIC);
            cell13.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "dec").doubleValue());
            cell13.setCellStyle(style2);

            Cell cell14 = incometotal2.createCell((short) 14);
            cell14.setCellType(CellType.NUMERIC);
            cell14.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jan").doubleValue());
            cell14.setCellStyle(style2);

            Cell cell15 = incometotal2.createCell((short) 15);
            cell15.setCellType(CellType.NUMERIC);
            cell15.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "feb").doubleValue());
            cell15.setCellStyle(style2);

            Cell cell16 = incometotal2.createCell((short) 16);
            cell16.setCellType(CellType.NUMERIC);
            cell16.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "mar").doubleValue());
            cell16.setCellStyle(style2);

            Cell cell17 = incometotal2.createCell((short) 17);
            cell17.setCellType(CellType.NUMERIC);
            cell17.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "apr").doubleValue());
            cell17.setCellStyle(style2);

            Cell cell18 = incometotal2.createCell((short) 18);
            cell18.setCellType(CellType.NUMERIC);
            cell18.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "may").doubleValue());
            cell18.setCellStyle(style2);

            Cell cell19 = incometotal2.createCell((short) 19);
            cell19.setCellType(CellType.NUMERIC);
            cell19.setCellStyle(style2);
            //cell19.setCellValue(decimalFormat.format(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox.getValue(), coal, selectedSections, budgetType.getSelectedItems(), "jun")));
            cell19.setCellValue(sampleBudgetItemsService.findSumOfMonthByBudgetCoalevel1AndDeptUnits(comboBox2.getValue(), coal, selectedSections, budgetType2.getSelectedItems(), "jun").doubleValue());
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
            if (reportColumnsCombo.isSelected(Report.QTR1) && reportColumnsCombo.isSelected(Report.BASIC) && reportColumnsCombo.isSelected(Report.QTR1) && reportColumnsCombo.isSelected(Report.QTR2) && reportColumnsCombo.isSelected(Report.QTR3) && reportColumnsCombo.isSelected(Report.QTR4) && reportColumnsCombo.isSelected(Report.SECTION) && reportColumnsCombo.isSelected(Report.TRAINING_DETAILS)) {
                createHeaderAndBodyAccountCodeDetailByBudgetRow(workbook, sheet);
            }

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
            if (reportColumnsCombo2.isSelected(Report.QTR1) && reportColumnsCombo2.isSelected(Report.BASIC) && reportColumnsCombo2.isSelected(Report.QTR1) && reportColumnsCombo2.isSelected(Report.QTR2) && reportColumnsCombo2.isSelected(Report.QTR3) && reportColumnsCombo2.isSelected(Report.QTR4) && reportColumnsCombo2.isSelected(Report.SECTION) && reportColumnsCombo2.isSelected(Report.TRAINING_DETAILS) && !gridCustomDetailedBudgetReportImp.asSingleSelect().isEmpty()) {

                CustomDetailedBudgetReportImp reportImp = gridCustomDetailedBudgetReportImp.asSingleSelect().getValue();
                if (reportImp != null) {
                    List<CustomDetailedBudgetReport> report = sampleCustomDetailedBudgetReportService.findByBudgetreport(reportImp);
                    for (CustomDetailedBudgetReport w : report) {
                        if (w.getDeptsection() != null) {
                            // Create sheet for each item in the report list
                            Sheet sheet = workbook.createSheet(w.getSheetname() + " Budget " + budget.getFinancialYear());
                            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
                            sheet.getPrintSetup().setLandscape(true);
                            // Uncomment the following line if needed
                            createHeaderAndBodyAccountCodeDetailByBudgetRow2(workbook, sheet, w.getDeptsection());
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
        Q223.createCell((short) 2).setCellValue(gen.getPreviousFy(comboBox.getValue().getFinancialYear()));
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
        Budget bug = gen.getPreviousBudget(comboBox.getValue().getFinancialYear());
        MonthlySumResponseFreight mon = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));

        MonthlySumResponseFreight pmon = sampleFreightVolumesService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget("111102", comboBox.getValue()));
        Row Q5 = createHeaderRow(sheet, tr, "ZFVNR -EXP", "Net Tons- Exports",
                pmon.getTotal().doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr1, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr2, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr3, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR -EXP", Quarters.Qtr4, comboBox.getValue().getFinancialYear()).doubleValue(),
                mon.getTotal().doubleValue(), mon.getQtr1().doubleValue(), mon.getQtr2().doubleValue(), mon.getQtr3().doubleValue(), mon.getQtr4().doubleValue());

        tr++;
        MonthlySumResponseFreight mon2 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
        MonthlySumResponseFreight pmon2 = sampleFreightVolumesService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget("111101", comboBox.getValue()));
        Row Q6 = createHeaderRow(sheet, tr, "ZFVNR-IMP", "Net Tons -Imports",
                pmon2.getTotal().doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr1, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr2, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr3, comboBox.getValue().getFinancialYear()).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("ZFVNR-IMP", Quarters.Qtr4, comboBox.getValue().getFinancialYear()).doubleValue(),
                mon2.getTotal().doubleValue(), mon2.getQtr1().doubleValue(), mon2.getQtr2().doubleValue(), mon2.getQtr3().doubleValue(), mon2.getQtr4().doubleValue());

        tr++;

        MonthlySumResponseFreight mon3 = sampleFreightVolumesService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
        MonthlySumResponseFreight pmon3 = sampleFreightVolumesService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget("111103", comboBox.getValue()));
        Row Q7 = createHeaderRow(sheet, tr, "ZFVTN-LC", "Local Net Tons",
                pmon3.getTotal().doubleValue(),
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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);

        MonthlySumResponseFreight mon101T = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q54 = createHeaderRow(sheet, tr, "", "Total Assets hire income",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T.getTotal().doubleValue(), mon101T.getQtr1().doubleValue(), mon101T.getQtr2().doubleValue(),
                mon101T.getQtr3().doubleValue(), mon101T.getQtr4().doubleValue());

        listCoas.clear();

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Freight Services", "", "", "", "", "", "", "", "", "", "");
        Row Q55 = createHeaderRow(sheet, tr, titles);

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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        ss.clear();

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T2 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T2);

        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q57 = createHeaderRow(sheet, tr, "", "Total freight-Northern Route",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T2.getTotal().doubleValue(), mon101T2.getQtr1().doubleValue(), mon101T2.getQtr2().doubleValue(),
                mon101T2.getQtr3().doubleValue(), mon101T2.getQtr4().doubleValue());
        listCOACodes.clear();
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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T3 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T3);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q59 = createHeaderRow(sheet, tr, "", "Total freight-Southern Route",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T3.getTotal().doubleValue(), mon101T3.getQtr1().doubleValue(), mon101T3.getQtr2().doubleValue(),
                mon101T3.getQtr3().doubleValue(), mon101T3.getQtr4().doubleValue());
        listCOACodes.clear();

        tr++;
        titleBoldTotal.add((int) tr);

        Row Q60 = createHeaderRow(sheet, tr, "", "Total freight Services",
                BigDecimal.ZERO.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T4 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T4);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q61 = createHeaderRow(sheet, tr, "", "Total Other fees",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T4.getTotal().doubleValue(), mon101T4.getQtr1().doubleValue(), mon101T4.getQtr2().doubleValue(),
                mon101T4.getQtr3().doubleValue(), mon101T4.getQtr4().doubleValue());

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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T5 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T5);;
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q64 = createHeaderRow(sheet, tr, "", "Total Rent Income",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T5.getTotal().doubleValue(), mon101T5.getQtr1().doubleValue(), mon101T5.getQtr2().doubleValue(),
                mon101T5.getQtr3().doubleValue(), mon101T5.getQtr4().doubleValue());

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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T6 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T6);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q66 = createHeaderRow(sheet, tr, "", "Total Passenger Service Income",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T6.getTotal().doubleValue(), mon101T6.getQtr1().doubleValue(), mon101T6.getQtr2().doubleValue(),
                mon101T6.getQtr3().doubleValue(), mon101T6.getQtr4().doubleValue());

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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T7 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T7);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q68 = createHeaderRow(sheet, tr, "", "Total-Miscellaneous Receipts",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T7.getTotal().doubleValue(), mon101T7.getQtr1().doubleValue(), mon101T7.getQtr2().doubleValue(),
                mon101T7.getQtr3().doubleValue(), mon101T7.getQtr4().doubleValue());

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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }
        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T8 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T8);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoasT);
            ptotal = pmon101.getTotal();
        }
        Row Q74 = createHeaderRow(sheet, tr, "", "Total-Non-Recurring Income",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesT, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T8.getTotal().doubleValue(), mon101T8.getQtr1().doubleValue(), mon101T8.getQtr2().doubleValue(),
                mon101T8.getQtr3().doubleValue(), mon101T8.getQtr4().doubleValue());

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
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoasT);
            ptotal = pmon101.getTotal();
        }
        MonthlySumResponseFreight calculateTotal1 = calculateTotal(listIncomeTotals);
        Row Q78 = createHeaderRow(sheet, tr, "", "Total Income",
                BigDecimal.ZERO.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                calculateTotal1.getTotal().doubleValue(), calculateTotal1.getQtr1().doubleValue(),
                calculateTotal1.getQtr2().doubleValue(), calculateTotal1.getQtr3().doubleValue(), calculateTotal1.getQtr4().doubleValue());
        MonthlySumResponseFreight totalIncome = new MonthlySumResponseFreight();
        totalIncome.setTotal(calculateTotal1.getTotal());
        totalIncome.setQtr1(calculateTotal1.getQtr1());
        totalIncome.setQtr2(calculateTotal1.getQtr2());
        totalIncome.setQtr3(calculateTotal1.getQtr3());
        totalIncome.setQtr4(calculateTotal1.getQtr4());
        MonthlySumResponseFreight totalIncomeActuals = new MonthlySumResponseFreight();
        totalIncomeActuals.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodesTT, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        totalIncomeActuals.setTotal(calculateTotal1.getTotal());
        listIncomeTotals.clear();
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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T9 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T9);
        listIncomeTotals4.add(mon101T9);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q82 = createHeaderRow(sheet, tr, "", "Total-Fuel",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T9.getTotal().doubleValue(), mon101T9.getQtr1().doubleValue(), mon101T9.getQtr2().doubleValue(),
                mon101T9.getQtr3().doubleValue(), mon101T9.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts1 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts1.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts1.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts1.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts1.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        listVariableCosts.add(VariableCosts1);
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
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101A.getTotal().doubleValue(), mon101A.getQtr1().doubleValue(), mon101A.getQtr2().doubleValue(),
                mon101A.getQtr3().doubleValue(), mon101A.getQtr4().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        listIncomeTotals.add(mon101A);
        listIncomeTotals4.add(mon101A);
        Row Q85 = createHeaderRow(sheet, tr, "", "Total passenger services expenses",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101A.getTotal().doubleValue(), mon101A.getQtr1().doubleValue(), mon101A.getQtr2().doubleValue(),
                mon101A.getQtr3().doubleValue(), mon101A.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts2 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts2.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts2.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts2.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts2.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods("224002", Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        listVariableCosts.add(VariableCosts2);
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
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T10 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T10);
        listIncomeTotals4.add(mon101T10);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        Row Q88 = createHeaderRow(sheet, tr, "", "Total Maintenance",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T10.getTotal().doubleValue(), mon101T10.getQtr1().doubleValue(), mon101T10.getQtr2().doubleValue(),
                mon101T10.getQtr3().doubleValue(), mon101T10.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts3 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts3.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts3.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts3.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts3.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
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
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldTotal.add((int) tr);
        if (bug != null) {
            MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), listCoas);
            ptotal = pmon101.getTotal();
        }
        MonthlySumResponseFreight mon101T11 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T11);
        listIncomeTotals4.add(mon101T11);

        Row Q90 = createHeaderRow(sheet, tr, "", "Total Property Expenses",
                ptotal.doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                mon101T11.getTotal().doubleValue(), mon101T11.getQtr1().doubleValue(), mon101T11.getQtr2().doubleValue(),
                mon101T11.getQtr3().doubleValue(), mon101T11.getQtr4().doubleValue());
        MonthlySumResponseFreight VariableCosts4 = new MonthlySumResponseFreight();//VariableCosts
        VariableCosts4.setQtr1(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts4.setQtr2(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts4.setQtr3(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        VariableCosts4.setQtr4(samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(listCOACodes, Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())));
        listVariableCosts.add(VariableCosts4);
        System.out.print(VariableCosts4.getQtr1());
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
            Qtr aa = new Qtr(null, null, null, null);
            aa.setQtr1(b.getQtr1());
            aa.setQtr2(b.getQtr2());
            aa.setQtr3(b.getQtr3());
            aa.setQtr4(b.getQtr4());
            listqtr.add(aa);
            System.out.println(b.toString() + " QQ2");
        }
        Qtr thisQtr = sumQuarterTotals(listqtr);
        Row Q92 = createHeaderRow(sheet, tr, "", "Total variable costs",
                BigDecimal.ZERO.doubleValue(),
                thisQtr.getQtr1().doubleValue(),
                thisQtr.getQtr2().doubleValue(),
                thisQtr.getQtr3().doubleValue(),
                thisQtr.getQtr4().doubleValue(),
                mon101T11W.getTotal().doubleValue(), mon101T11W.getQtr1().doubleValue(), mon101T11W.getQtr2().doubleValue(),
                mon101T11W.getQtr3().doubleValue(), mon101T11W.getQtr4().doubleValue());
        //System.out.print(varcosts.getQtr1() + " |1 ");
        //System.out.print(varcosts.getQtr2() + " |2 ");
        //System.out.print(varcosts.getQtr3() + " |3 ");
        //System.out.print(varcosts.getQtr4() + " |4 ");
        //System.out.println(listVariableCosts.size());

        System.out.println(thisQtr.getQtr1() + " QQ");

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
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T12 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T12);
        listIncomeTotals3.add(mon101T12);//Total Personnel Cost

        Row Q941 = createHeaderRow(sheet, tr, "", "Total Personnel Cost",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T12.getTotal().doubleValue(), mon101T12.getQtr1().doubleValue(), mon101T12.getQtr2().doubleValue(),
                mon101T12.getQtr3().doubleValue(), mon101T12.getQtr4().doubleValue());

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
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T13 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T13);
        listIncomeTotals3.add(mon101T13);//Total Administration Expenses

        Row Q95 = createHeaderRow(sheet, tr, "", "Total Administration Expenses",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T13.getTotal().doubleValue(), mon101T13.getQtr1().doubleValue(), mon101T13.getQtr2().doubleValue(),
                mon101T13.getQtr3().doubleValue(), mon101T13.getQtr4().doubleValue());

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

        findByAcntCodeStartingWith = urcAcntService.findByAcntCode("221019");
        ss.clear();
        List<URC_ACNT> findByAcntCodeStartingWith601 = urcAcntService.findByAcntCode("221020");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith601);
        List<URC_ACNT> findByAcntCodeStartingWith602 = urcAcntService.findByAcntCode("221008");
        findByAcntCodeStartingWith.addAll(findByAcntCodeStartingWith602);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            tr++;

            BigDecimal ptot = BigDecimal.ZERO;
            COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
            listCoas.add(coa);
            MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
            if (bug != null) {
                MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                ptot = pmon101.getTotal();
            }

            Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                    ptot.doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                    mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                    mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T14 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T14);
        listIncomeTotals2.add(mon101T14);
        Row Q99 = createHeaderRow(sheet, tr, "", "Total Board & Legal Expenses",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T14.getTotal().doubleValue(), mon101T14.getQtr1().doubleValue(),
                mon101T14.getQtr2().doubleValue(), mon101T14.getQtr3().doubleValue(), mon101T14.getQtr4().doubleValue());

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("222");
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldBlue.add((int) tr);
        MonthlySumResponseFreight mon101T15 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T15);
        listIncomeTotals2.add(mon101T15);

        Row Q100 = createHeaderRow(sheet, tr, "", "Total Communications",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T15.getTotal().doubleValue(), mon101T15.getQtr1().doubleValue(),
                mon101T15.getQtr2().doubleValue(), mon101T15.getQtr3().doubleValue(), mon101T15.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("223", "Utilities", "", "", "", "", "", "", "", "", "", "");
        Row Q101 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("5", "6", "7", "8"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22300", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T16 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T16);
        listIncomeTotals2.add(mon101T16);

        Row Q102 = createHeaderRow(sheet, tr, "", "Total Utility Expenses",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T16.getTotal().doubleValue(), mon101T16.getQtr1().doubleValue(),
                mon101T16.getQtr2().doubleValue(), mon101T16.getQtr3().doubleValue(), mon101T16.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("224", "Supplies and Services", "", "", "", "", "", "", "", "", "", "");
        Row Q1021 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("3", "4", "5", "6"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22400", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T17 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T17);
        listIncomeTotals2.add(mon101T17);

        Row Q103 = createHeaderRow(sheet, tr, "", "Total Supplies and Services",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T17.getTotal().doubleValue(), mon101T17.getQtr1().doubleValue(), mon101T17.getQtr2().doubleValue(),
                mon101T17.getQtr3().doubleValue(), mon101T17.getQtr4().doubleValue()
        );

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("", "Professional Services", "", "", "", "", "", "", "", "", "", "");
        Row Q104 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22500", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }
        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T18 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T18);
        listIncomeTotals2.add(mon101T18);
        Row Q105 = createHeaderRow(sheet, tr, "", "Total Professional Services",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T18.getTotal().doubleValue(), mon101T18.getQtr1().doubleValue(), mon101T18.getQtr2().doubleValue(),
                mon101T18.getQtr3().doubleValue(), mon101T18.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("226", "Insurances and Licenses", "", "", "", "", "", "", "", "", "", "");
        Row Q106 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22600", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T19 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T19);
        listIncomeTotals2.add(mon101T19);

        Row Q107 = createHeaderRow(sheet, tr, "", "Total Insurances and Licenses",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T19.getTotal().doubleValue(), mon101T19.getQtr1().doubleValue(), mon101T19.getQtr2().doubleValue(),
                mon101T19.getQtr3().doubleValue(), mon101T19.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("227", "Travel and Transport", "", "", "", "", "", "", "", "", "", "");
        Row Q108 = createHeaderRow(sheet, tr, titles);

        ss.clear();
        ss.addAll(Arrays.asList("1", "2", "3"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("22700", ss);
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T191 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals.add(mon101T191);
        listIncomeTotals2.add(mon101T191);

        Row Q109 = createHeaderRow(sheet, tr, "", "Total Travel and Transport",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T191.getTotal().doubleValue(), mon101T191.getQtr1().doubleValue(), mon101T191.getQtr2().doubleValue(),
                mon101T191.getQtr3().doubleValue(), mon101T191.getQtr4().doubleValue());

        tr++;
        titleJustBold.add((int) tr);
        titles = Arrays.asList("282", "Miscellaneous Other Expenses", "", "", "", "", "", "", "", "", "", "");
        Row Q110 = createHeaderRow(sheet, tr, titles);

        ss.addAll(Arrays.asList("1", "2"));
        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith11110AndNextDigitIn123("28210", ss);
        ss.clear();
        listCoas.clear();
        for (URC_ACNT k : findByAcntCodeStartingWith) {
            if (k.getAcntCode().trim().length() > 5) {
                tr++;

                BigDecimal ptot = BigDecimal.ZERO;
                COA coa = sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue());
                listCoas.add(coa);
                MonthlySumResponseFreight mon101 = sampleBudgetItemsService.getTotals(comboBox.getValue(), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                if (bug != null) {
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight mon101T1912 = sampleBudgetItemsService.getTotals(comboBox.getValue(), listCoas);
        listIncomeTotals2.add(mon101T1912);
        listIncomeTotals.add(mon101T1912);
        Row Q111 = createHeaderRow(sheet, tr, "", "Total Miscellaneous Other Expenses",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                mon101T1912.getTotal().doubleValue(), mon101T1912.getQtr1().doubleValue(),
                mon101T1912.getQtr2().doubleValue(), mon101T1912.getQtr3().doubleValue(), mon101T1912.getQtr4().doubleValue());

        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal10 = calculateTotal(listIncomeTotals2);
        listIncomeTotals3.add(calculateTotal10);//Total Other Administration Expenses
        Row Q112 = createHeaderRow(sheet, tr, "", "Total Other Administration Expenses",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                calculateTotal10.getTotal().doubleValue(), calculateTotal10.getQtr1().doubleValue(),
                calculateTotal10.getQtr2().doubleValue(), calculateTotal10.getQtr3().doubleValue(), calculateTotal10.getQtr4().doubleValue());
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
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal101 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal101);//Total Other Administration Expenses
        Row Q121 = createHeaderRow(sheet, tr, "", "Total Depreciation",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
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
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }
        tr++;
        titleBoldTotal.add((int) tr);
        MonthlySumResponseFreight calculateTotal1011 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1011);//Total Other Administration Expenses
        Row Q128 = createHeaderRow(sheet, tr, "", "Total Finance Cost",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
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

        findByAcntCodeStartingWith = urcAcntService.findByAcntCodeStartingWith("312");
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
                    MonthlySumResponseFreight pmon101 = sampleBudgetItemsService.getTotals(gen.getPreviousBudget(comboBox.getValue().getFinancialYear()), sampleCoaService.findByCodeAndBudget(k.getAcntCode().trim(), comboBox.getValue()));
                    ptot = pmon101.getTotal();
                }

                Row Q53 = createHeaderRow(sheet, tr, k.getAcntCode().trim(), k.getDescr(),
                        ptot.doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr1, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr2, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr3, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        samopleSALFLDGService.findSumOfAmountByAccntCodeAndPeriods(coa.getCode(), Quarters.Qtr4, gen.getPreviousFy(comboBox.getValue().getFinancialYear())).doubleValue(),
                        mon101.getTotal().doubleValue(), mon101.getQtr1().doubleValue(), mon101.getQtr2().doubleValue(),
                        mon101.getQtr3().doubleValue(), mon101.getQtr4().doubleValue());
            }

        }
        tr++;
        titleJustBold.add((int) tr);
        MonthlySumResponseFreight calculateTotal1012 = calculateTotal(listIncomeTotals5);
        listIncomeTotals5.add(calculateTotal1012);//Total Other Administration Expenses
        Row Q140 = createHeaderRow(sheet, tr, "", "Total Fixed Assets",
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
                BigDecimal.ZERO.doubleValue(),
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
        Q166.createCell((short) 2).setCellValue(totalIncomeValueActuals);
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

        for (Qtr qtr : qtrList) {
            qtr1Total = addIfNotNull(qtr1Total, qtr.getQtr1());
            qtr2Total = addIfNotNull(qtr2Total, qtr.getQtr2());
            qtr3Total = addIfNotNull(qtr3Total, qtr.getQtr3());
            qtr4Total = addIfNotNull(qtr4Total, qtr.getQtr4());
        }

        return new Qtr(qtr1Total, qtr2Total, qtr3Total, qtr4Total);
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
}
