package com.methaltech.application.views.budgetReport;

import com.methaltech.application.data.Report;
import com.methaltech.application.data.bgtool.repository.SamplePersonService;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CustomDetailedBudgetReportImpService;
import com.methaltech.application.data.bgtool.service.CustomDetailedBudgetReportService;
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
import com.methaltech.application.data.entity.oldbgtool.BudgetSubItem;
import com.methaltech.application.data.entity.oldbgtool.DepartmentSection;
import com.methaltech.application.data.entity.oldbgtool.DepartmentUnit;
import com.methaltech.application.data.entity.oldbgtool.ProgramActivity;
import com.methaltech.application.data.entity.oldbgtool.Programme;
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
import com.vaadin.flow.component.formlayout.FormLayout;
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
import com.vaadin.flow.component.orderedlayout.Scroller;
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.poi.ss.usermodel.Picture;
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
    private final Coalevel1Service sampleCoalevel1Service;
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

    public BudgetReportsView(UserService userService, BudgetService budgetService,
            DepartmentSectionService departmentsectionService, DepartmentUnitService departmentUnitService,
            SamplePersonService samplePersonService, ProgrammeService programmeService,
            ProgramActivityService programmeActivityService, BudgetSubItemService budgetSubItemService,
            ItemService2 itemService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, OrganisationService sampleOrganisationService,
            BudgetItemsService sampleBudgetItemsService, Coalevel1Service sampleCoalevel1Service, URC_Priority_AreasService sampleURC_Priority_AreasService,
            Urc_ActivitiesService sampleUrc_ActivitiesService, CustomDetailedBudgetReportImpService sampleCustomDetailedBudgetReportImpService,
            CustomDetailedBudgetReportService sampleCustomDetailedBudgetReportService) {
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
        
        //setSpacing(false);
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
        // Add the panels to the Accordion
        accordion.add(panel0);
        accordion.add(panel1);
        accordion.add(panel2);
        accordion.add(panel3);
        accordion.add(panel4);
        accordion.add(panel5);

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
            
            tr++;tr++;
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
                System.out.println(cao.getCode() + " 1");
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
                    System.out.println("    " + tt.getCoacode().getCode());
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
                System.out.println(cao.getCode() + " 2");
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
                    System.out.println("    " + tt.getCoacode().getCode());
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
                System.out.println(cao.getCode() + " 3");
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
                    System.out.println("    " + tt.getCoacode().getCode());
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
            
            tr++;tr++;
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
                System.out.println(cao.getCode() + " 1");
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
                    System.out.println("    " + tt.getCoacode().getCode());
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
        System.out.println(isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBox2.getValue(), coal, selectedSections));
        System.out.println(comboBox2.getValue().getFinancialYear() + " " + coal.getName() + " " + selectedSections.size());
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero2(comboBox2.getValue(), coal, selectedSections) == true) {
            System.out.println("Revenue Exp exists");
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
                System.out.println(cao.getCode() + " 2");
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
                    System.out.println("    " + tt.getCoacode().getCode());
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
                System.out.println(cao.getCode() + " 3");
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
                    System.out.println("    " + tt.getCoacode().getCode());
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
            
            tr++;tr++;
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
            System.out.println("hello3: " + e.getMessage());
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
}
