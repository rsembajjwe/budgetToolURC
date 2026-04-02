package com.methaltech.application.views.budget;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;

import java.io.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DepartmentGeneralPhysicalPerformanceService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.OverallGeneralPhysicalPerformanceService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.BudgetSummary;
import com.methaltech.application.data.entity.bgtool.BudgetSummaryCards;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.RevenueBreakdown;
import com.methaltech.application.data.entity.bgtool.RevenueSource;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.views.budget.Component.QuillEditorField;
import com.methaltech.application.views.budget.charts.DepartmentChartBuilder;
import com.methaltech.application.views.structure.DepartmentOverview;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Budget Management Dashboard")
@SpringComponent
@UIScope
@Route(value = "dashboard", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
@Uses(Icon.class)
public class DashboardView extends VerticalLayout {

    private final BudgetService budgetService;
    private final UserService userService;
    private AuthenticatedUser authenticatedUser;
    private final VerticalLayout contentContainer = new VerticalLayout();
    private ComboBox<Budget> fiscalYear = new ComboBox<>();
    private Budget selectedBudget;
    private Span statusIndicator;
    private Span lastUpdated;
    private User user;
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final BudgetItemsService budgetItemsService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final DepartmentGeneralPhysicalPerformanceService departmentGeneralPhysicalPerformanceService;
    private final OverallGeneralPhysicalPerformanceService overallGeneralPhysicalPerformanceService;

    private static final PageSize REPORT_PAGE_SIZE = PageSize.A4;
    private static final float PAGE_MARGIN_TOP = 48f;
    private static final float PAGE_MARGIN_RIGHT = 36f;
    private static final float PAGE_MARGIN_BOTTOM = 48f;
    private static final float PAGE_MARGIN_LEFT = 36f;

    private static final Color HEADER_BG = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);
    private static final Color GRID = new com.itextpdf.kernel.colors.DeviceRgb(220, 220, 220);
    private static final Color ROW_ALT = new com.itextpdf.kernel.colors.DeviceRgb(250, 250, 250);

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private static final DateTimeFormatter PRINT_TS = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");
    BudgetVisualCards visualCards;
    private final Tab overviewTab = new Tab("Overview");
    private final Tab departmentTab = new Tab("Department Analysis");
    private final Tab revenueTab = new Tab("Revenue Analysis");
    private final OrganisationService organisationService;
    private final Tabs dashboardTabs = new Tabs(overviewTab, departmentTab, revenueTab);
    private final Div tabContent = new Div();

    private final Map<Tab, Component> tabsToPages = new LinkedHashMap<>();
    Set<UrcDeptSectionAnlDimbgt> deptSections = new HashSet<>();

    private ComboBox<Organisation> comboBoxOrganisation = new ComboBox<>();
    private ComboBox<String> comboBoxQuarter = new ComboBox<>();

    private Organisation selectedOrganisation;
    private String selectedQuarter;

    @Autowired
    public DashboardView(BudgetService budgetService, AuthenticatedUser authenticatedUser, UserService userService, CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, SALFLDGService sampleSALFLDGService, DeptSectionMergerService sampleDeptSectionMergerService,
            BudgetItemsService budgetItemsService, Urc_ActivitiesService sampleUrc_ActivitiesService, DepartmentGeneralPhysicalPerformanceService departmentGeneralPhysicalPerformanceService,
            OverallGeneralPhysicalPerformanceService overallGeneralPhysicalPerformanceService, OrganisationService organisationService) {
        this.budgetService = budgetService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budgetItemsService = budgetItemsService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.departmentGeneralPhysicalPerformanceService = departmentGeneralPhysicalPerformanceService;
        this.overallGeneralPhysicalPerformanceService = overallGeneralPhysicalPerformanceService;
        this.organisationService = organisationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        addClassName("dashboard-view");

        loadFiscalYearData();
        createHeader();
        createMainContent();
    }

    private void createHeader() {
        Div header = new Div();
        header.addClassName("dashboard-header");
        header.setWidthFull();
        header.setHeight("72px"); // optional fixed height

        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        headerContent.setPadding(false);
        headerContent.setSpacing(false);
        headerContent.setMargin(false);
        headerContent.setHeight("72px");

        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setSpacing(true);
        leftSide.setPadding(false);
        leftSide.setMargin(false);
        leftSide.setFlexGrow(1);

        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.setSpacing(true);
        logoSection.setPadding(false);
        logoSection.setMargin(false);
        logoSection.addClassName("logo-section");

        Div logo = new Div();
        logo.addClassName("app-logo");
        Span logoText = new Span("B");
        logoText.addClassName("logo-text");
        logo.add(logoText);

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setSpacing(false);
        titleGroup.setPadding(false);
        titleGroup.setMargin(false);

        H1 title = new H1("Dashboard");
        title.addClassName("app-title");

        Span subtitle = new Span("Comprehensive Budget");
        subtitle.addClassName("app-subtitle");

        titleGroup.add(title, subtitle);
        logoSection.add(logo, titleGroup);

        // Fiscal year layout
        HorizontalLayout fiscalYearLayout = new HorizontalLayout();
        fiscalYearLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        fiscalYearLayout.addClassName("fiscal-year");
        fiscalYearLayout.setSpacing(true);
        fiscalYearLayout.setPadding(false);
        fiscalYearLayout.setMargin(false);

        Icon calendarIcon = new Icon(VaadinIcon.CALENDAR);
        calendarIcon.addClassName("calendar-icon");

        VerticalLayout fiscalInfo = new VerticalLayout();
        fiscalInfo.setSpacing(false);
        fiscalInfo.setPadding(false);
        fiscalInfo.setMargin(false);

        Span fiscalLabel = new Span("Fiscal Year");
        fiscalLabel.addClassName("fiscal-label");

        fiscalYear.addClassName("fiscal-year-selector");
        fiscalYear.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedBudget = e.getValue();
                comboBoxOrganisation.clear();
                comboBoxOrganisation.setItems(organisationService.findByBudget(selectedBudget));
                loadDashboardData();
                updateStatusIndicator();
            }
        });

        fiscalInfo.add(fiscalLabel, fiscalYear);
        fiscalYearLayout.add(calendarIcon, fiscalInfo);

        // Organisation layout
        HorizontalLayout organisationLayout = new HorizontalLayout();
        organisationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        organisationLayout.addClassName("fiscal-year");
        organisationLayout.setSpacing(true);
        organisationLayout.setPadding(false);
        organisationLayout.setMargin(false);

        Icon organisationIcon = new Icon(VaadinIcon.OFFICE);
        organisationIcon.addClassName("calendar-icon");

        VerticalLayout organisationInfo = new VerticalLayout();
        organisationInfo.setSpacing(false);
        organisationInfo.setPadding(false);
        organisationInfo.setMargin(false);

        Span organisationLabel = new Span("Fund Source");
        organisationLabel.addClassName("fiscal-label");

        comboBoxOrganisation.addClassName("fiscal-year-selector");
        comboBoxOrganisation.setPlaceholder("Select Fund Source");
        comboBoxOrganisation.setItemLabelGenerator(org
                -> org != null ? org.getName() : ""
        );
        comboBoxOrganisation.setClearButtonVisible(true);

        // Example loading; replace with your actual source
        comboBoxOrganisation.setItems(organisationService.findByBudget(selectedBudget));

        comboBoxOrganisation.addValueChangeListener(e -> {
            selectedOrganisation = e.getValue();
            loadDashboardData();
            updateStatusIndicator();
        });

        organisationInfo.add(organisationLabel, comboBoxOrganisation);
        organisationLayout.add(organisationIcon, organisationInfo);

        // Quarter layout
        HorizontalLayout quarterLayout = new HorizontalLayout();
        quarterLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        quarterLayout.addClassName("fiscal-year");
        quarterLayout.setSpacing(true);
        quarterLayout.setPadding(false);
        quarterLayout.setMargin(false);

        Icon quarterIcon = new Icon(VaadinIcon.CLIPBOARD_CHECK);
        quarterIcon.addClassName("calendar-icon");

        VerticalLayout quarterInfo = new VerticalLayout();
        quarterInfo.setSpacing(false);
        quarterInfo.setPadding(false);
        quarterInfo.setMargin(false);

        Span quarterLabel = new Span("Quarter");
        quarterLabel.addClassName("fiscal-label");

        comboBoxQuarter.addClassName("fiscal-year-selector");
        comboBoxQuarter.setItems("qtr1", "qtr2", "qtr3", "qtr4");
        comboBoxQuarter.setPlaceholder("Select quarter");//comboBoxQuarter.setValue("qtr1");

        comboBoxQuarter.addValueChangeListener(e -> {
            selectedQuarter = e.getValue();
            loadDashboardData();
            updateStatusIndicator();
        });

        quarterInfo.add(quarterLabel, comboBoxQuarter);
        quarterLayout.add(quarterIcon, quarterInfo);

        leftSide.add(logoSection, fiscalYearLayout, organisationLayout, quarterLayout);

        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);
        rightSide.setMargin(false);
        rightSide.addClassName("header-actions");

        Button notificationButton = new Button(new Icon(VaadinIcon.BELL));
        notificationButton.addClassName("notification-button");
        notificationButton.addClickListener(e -> {
            showNotification("No new notifications", NotificationVariant.LUMO_CONTRAST);
        });

        Div notificationBadge = new Div();
        notificationBadge.addClassName("notification-badge");
        notificationBadge.setText("3");
        notificationButton.getElement().appendChild(notificationBadge.getElement());

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClassName("primary-action");
        refreshButton.addClickListener(e -> {
            refreshDashboard();
            showNotification("Dashboard refreshed successfully!", NotificationVariant.LUMO_SUCCESS);
        });

        Button exportButton = new Button("Export Pdf", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addClassName("secondary-action");
        exportButton.addClickListener(e -> generatePDFReport());

        Button exportwordButton = new Button("Export Word", new Icon(VaadinIcon.DOWNLOAD));
        exportwordButton.addClassName("secondary-action");
        exportwordButton.addClickListener(e -> generateWordReport());

        Button settingsButton = new Button(new Icon(VaadinIcon.COG));
        settingsButton.addClassName("tertiary-action");

        Button userButton = new Button(new Icon(VaadinIcon.USER));
        userButton.addClassName("tertiary-action");

        rightSide.add(notificationButton, refreshButton, exportButton, exportwordButton, settingsButton, userButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void createMainContent() {
        contentContainer.setSizeFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.setMargin(false);
        contentContainer.addClassName("content-container");
        contentContainer.getStyle()
                .set("overflow", "hidden")
                .set("min-height", "0");

        dashboardTabs.setWidthFull();
        dashboardTabs.addClassName("dashboard-tabs");
        dashboardTabs.addSelectedChangeListener(event -> showSelectedTab(event.getSelectedTab()));

        tabContent.setSizeFull();
        tabContent.addClassName("dashboard-tab-content");
        tabContent.getStyle()
                .set("overflow", "auto")
                .set("min-height", "0");

        contentContainer.add(dashboardTabs, tabContent);
        contentContainer.expand(tabContent);

        add(contentContainer);
        expand(contentContainer);
    }

    private void showSelectedTab(Tab selectedTab) {
        tabContent.removeAll();

        Component page = tabsToPages.get(selectedTab);
        if (page != null) {
            tabContent.add(page);
        } else {
            tabContent.add(new Span("No content available."));
        }
    }

    private void loadFiscalYearData() {
        fiscalYear.setItems(query -> budgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        fiscalYear.setItemLabelGenerator(Budget::getFinancialYear);
        Optional<Budget> recentBudget = budgetService.getMostRecurrentBudget();
        if (recentBudget.isPresent()) {
            fiscalYear.setValue(recentBudget.get());
            selectedBudget = recentBudget.get();
        }
    }

    private void loadDashboardData() {

        try {
            Budget currentBudget = selectedBudget != null ? selectedBudget : fiscalYear.getValue();
            if (currentBudget == null || selectedOrganisation == null || selectedQuarter == null) {
                showEmptyState();
                showNotification("Please select fiscal year, fund source, and quarter.", NotificationVariant.LUMO_CONTRAST);
                return;
            }

            BudgetSummary budgetSummary = budgetService.getBudgetSummary(currentBudget);

            BudgetSummaryCards summaryCards = new BudgetSummaryCards(
                    budgetSummary,
                    sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService,
                    sampleDeptSectionMergerService,
                    currentBudget
            );
            summaryCards.addClassName("budget-summary-cards");

            BudgetVisualCards visualCards = new BudgetVisualCards(budgetSummary);
            visualCards.addClassName("budget-visual-cards-wrapper");

            DepartmentOverview departmentOverview = new DepartmentOverview(
                    budgetSummary.getDepartmentBudgets(),
                    sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService,
                    sampleDeptSectionMergerService,
                    currentBudget,
                    budgetItemsService,
                    sampleUrc_ActivitiesService,
                    departmentGeneralPhysicalPerformanceService
            );
            departmentOverview.addClassName("department-overview");

            RevenueBreakdown revenueBreakdown = new RevenueBreakdown(
                    budgetSummary.getRevenueSources()
            );
            revenueBreakdown.addClassName("revenue-breakdown");

            java.util.List<DepartmentBudget> departmentBudgets = budgetSummary.getDepartmentBudgets();
            deptSections.clear();
            departmentBudgets.forEach(r -> deptSections.addAll(r.getSections()));

            Tab overviewSummaryTab = new Tab("Summary Expenditure By Account Code");
            Tab generalSummaryPhysicalTab = new Tab("General Budget Physical Performance");

            Tabs tabsSummary = new Tabs(overviewSummaryTab, generalSummaryPhysicalTab);
            tabsSummary.setWidthFull();
            tabsSummary.addClassName("summary-tabs");

            Component summaryExpenditurePage = budgetCoaQtrGeneral(currentBudget, deptSections);
            Component generalPhysicalPage = createOverallGeneralPhysicalPerformanceContent(currentBudget);

            summaryExpenditurePage.setVisible(true);
            generalPhysicalPage.setVisible(false);

            Div summaryTabContent = new Div();
            summaryTabContent.setWidthFull();
            summaryTabContent.addClassName("summary-tab-content");
            summaryTabContent.add(summaryExpenditurePage, generalPhysicalPage);

            tabsSummary.addSelectedChangeListener(event -> {
                Tab selected = event.getSelectedTab();
                summaryExpenditurePage.setVisible(selected == overviewSummaryTab);
                generalPhysicalPage.setVisible(selected == generalSummaryPhysicalTab);
            });

            VerticalLayout overviewPage = new VerticalLayout(summaryCards, visualCards, tabsSummary, summaryTabContent);
            overviewPage.setWidthFull();
            overviewPage.setPadding(false);
            overviewPage.setSpacing(true);
            overviewPage.setMargin(false);
            overviewPage.addClassName("dashboard-page");

            VerticalLayout departmentPage = new VerticalLayout(departmentOverview);
            departmentPage.setWidthFull();
            departmentPage.setPadding(false);
            departmentPage.setSpacing(true);
            departmentPage.setMargin(false);
            departmentPage.addClassName("dashboard-page");

            VerticalLayout revenuePage = new VerticalLayout(revenueBreakdown);
            revenuePage.setWidthFull();
            revenuePage.setPadding(false);
            revenuePage.setSpacing(true);
            revenuePage.setMargin(false);
            revenuePage.addClassName("dashboard-page");

            tabsToPages.clear();
            tabsToPages.put(overviewTab, overviewPage);
            tabsToPages.put(departmentTab, departmentPage);
            tabsToPages.put(revenueTab, revenuePage);

            Tab selectedTab = dashboardTabs.getSelectedTab() != null
                    ? dashboardTabs.getSelectedTab()
                    : overviewTab;

            showSelectedTab(selectedTab);
            updateStatusIndicator();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Error loading dashboard data: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showEmptyState() {
        tabContent.removeAll();

        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setSizeFull();
        emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyState.addClassName("empty-state");

        Icon emptyIcon = new Icon(VaadinIcon.CHART);
        emptyIcon.addClassName("empty-state-icon");

        H2 emptyTitle = new H2("No Budget Data Available");
        emptyTitle.addClassName("empty-state-title");

        Span emptyMessage = new Span("Please select a fiscal year, fund source, and quarter to view dashboard data.");
        emptyMessage.addClassName("empty-state-message");

        emptyState.add(emptyIcon, emptyTitle, emptyMessage);
        tabContent.add(emptyState);
    }

    private void updateStatusIndicator() {
        if (statusIndicator != null && lastUpdated != null) {
            statusIndicator.setText("System Online");
            lastUpdated.setText("Last updated: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
        }
    }

    private void refreshDashboard() {
        tabContent.removeAll();

        Div loadingIndicator = new Div();
        loadingIndicator.addClassName("loading-indicator");
        loadingIndicator.setText("Refreshing dashboard data...");

        tabContent.add(loadingIndicator);

        getUI().ifPresent(ui -> ui.access(() -> {
            try {
                Thread.sleep(500);
                loadDashboardData();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                showNotification("Refresh interrupted", NotificationVariant.LUMO_ERROR);
            }
        }));
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);

        // Enhanced notification styling
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
    }
// Body margins (header/footer are drawn by event handler)
    private static final float MARGIN_TOP = 90f;     // enough room for logo + title + divider
    private static final float MARGIN_RIGHT = 36f;
    private static final float MARGIN_BOTTOM = 55f;
    private static final float MARGIN_LEFT = 36f;

// Style colors (soft, executive)
    private static final Color COLOR_HEADER_BG = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);
    private static final Color COLOR_GRID = new com.itextpdf.kernel.colors.DeviceRgb(220, 220, 220);
    private static final Color COLOR_ALT_ROW = new com.itextpdf.kernel.colors.DeviceRgb(250, 250, 250);
    private static final Color COLOR_NOTE_BG = new com.itextpdf.kernel.colors.DeviceRgb(248, 248, 248);

    private void generatePDFReport() {
        try {
            Budget currentBudget = (selectedBudget != null) ? selectedBudget : fiscalYear.getValue();
            if (currentBudget == null) {
                showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
                return;
            }

            BudgetSummary summary = budgetService.getBudgetSummary(currentBudget);
            if (summary == null) {
                showNotification("No data found for the selected fiscal year.", NotificationVariant.LUMO_ERROR);
                return;
            }

            byte[] pdf = buildBudgetDashboardPdf(currentBudget, summary);

            String fileName = "Budget_Dashboard_Report_"
                    + safe(currentBudget.getFinancialYear()).replace("/", "-")
                    + "_" + LocalDateTime.now().format(FILE_TS)
                    + ".pdf";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(pdf));
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);

            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration reg = ui.getSession().getResourceRegistry().registerResource(resource);
                ui.getPage().open(reg.getResourceUri().toString(), "_blank");
                showNotification("PDF report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Unable to open PDF (no UI context).", NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            showNotification("Error generating PDF report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private byte[] buildBudgetDashboardPdf(Budget budget, BudgetSummary summary) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos, new WriterProperties().setFullCompressionMode(true));
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(REPORT_PAGE_SIZE);

            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            ImageData logo = loadLogo("/META-INF/resources/images/urclogo.png"); // from src/main/resources
            PdfFormXObject totalPagesPlaceholder = new PdfFormXObject(new Rectangle(0, 0, 50, 12));

            String reportTitle = "BUDGET MANAGEMENT DASHBOARD REPORT";
            String fiscalYearLine = "Fiscal Year: " + safe(budget.getFinancialYear());

            pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
                    new ReportHeaderFooterHandler(reportTitle, fiscalYearLine, fontRegular, fontBold, logo, totalPagesPlaceholder));

            try (Document doc = new Document(pdf)) {
                // doc.setMargins(MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);
                doc.setMargins(100f, 36f, 55f, 36f);
                pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new IEventHandler() {
                    @Override
                    public void handleEvent(Event event) {
                        PdfDocumentEvent ev = (PdfDocumentEvent) event;
                        PdfDocument pdfDoc = ev.getDocument();
                        if (pdfDoc.getNumberOfPages() == 2) { // when page 2 starts
                            doc.setMargins(55f, 36f, 55f, 36f);
                        }
                    }
                });

                // Meta line
                doc.add(new Paragraph("Generated on: " + LocalDateTime.now().format(PRINT_TS))
                        .setFont(fontRegular)
                        .setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(14));

                // EXECUTIVE SUMMARY
                doc.add(sectionTitle("EXECUTIVE SUMMARY", fontBold));

                Table summaryTable = baseTable(new float[]{3, 2});
                addHeader(summaryTable, fontBold, "Metric", "Amount");

                addRow(summaryTable, fontRegular, false, "Total Budget", formatUGX(summary.getTotalBudget()), true);
                addRow(summaryTable, fontRegular, true, "Total Spent", formatUGX(summary.getTotalSpent()), true);
                addRow(summaryTable, fontRegular, false, "Remaining Budget", formatUGX(summary.getRemainingBudget()), true);
                addRow(summaryTable, fontRegular, true, "Total Revenue", formatUGX(summary.getTotalRevenue()), true);
                addRow(summaryTable, fontRegular, false, "Projected Revenue", formatUGX(summary.getProjectedRevenue()), true);
                addRow(summaryTable, fontRegular, true, "Budget Utilization", pct(summary.getSpentPercentage()), false);
                addRow(summaryTable, fontRegular, false, "Revenue Achievement", pct(summary.getRevenuePercentage()), false);

                doc.add(summaryTable);

                // DEPARTMENT ANALYSIS
                doc.add(sectionTitle("COST CENTRE BUDGET ANALYSIS", fontBold));

                Table deptTable = baseTable(new float[]{2.8f, 1.5f, 1.5f, 1.5f, 1.0f, 0.9f});
                addHeader(deptTable, fontBold,
                        "Cost Centre", "Total Budget", "Total Spent", "Available", "Utilization", "Status");

                boolean alt = false;
                for (DepartmentBudget d : safeList(summary.getDepartmentBudgets())) {
                    Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
                    alt = !alt;

                    addBodyCell(deptTable, fontRegular, safe(d.getDepartmentName()), bg, TextAlignment.LEFT);
                    addBodyCell(deptTable, fontRegular, formatUGX(d.getTotalBudget()), bg, TextAlignment.RIGHT);
                    addBodyCell(deptTable, fontRegular, formatUGX(d.getTotalSpent()), bg, TextAlignment.RIGHT);
                    addBodyCell(deptTable, fontRegular, formatUGX(d.getAvailableBudget()), bg, TextAlignment.RIGHT);
                    addBodyCell(deptTable, fontRegular, pct(d.getSpentPercentage()), bg, TextAlignment.RIGHT);

                    deptTable.addCell(statusCell(fontRegular, safe(d.getStatusText()), statusColor(d.getSpentPercentage())));
                }
                doc.add(deptTable);

                Image chart = DepartmentChartBuilder.buildDepartmentBarChart(summary.getDepartmentBudgets());
                chart.setAutoScale(true);

                doc.add(chart);

                doc.add(new Paragraph("\n"));
                doc.add(sectionTitle("PERFORMANCE BY ACCOUNT CODE", fontBold));

                int qtr = mapQuarter(selectedQuarter);
                String quarterLabel = getQuarterLabel(qtr);

                java.util.List<BudgetItemsActuals> accountItems
                        = budgetItemsService.findDistinctBudgetItemsesExp(budget, deptSections);

                java.util.List<BudgetItemsActuals> operatingItems = accountItems.stream()
                        .filter(i -> i.getCoacode() != null
                        && i.getCoacode().getCode() != null
                        && i.getCoacode().getCode().startsWith("2"))
                        .toList();

                java.util.List<BudgetItemsActuals> capitalItems = accountItems.stream()
                        .filter(i -> i.getCoacode() != null
                        && i.getCoacode().getCode() != null
                        && i.getCoacode().getCode().startsWith("3"))
                        .toList();

                doc.add(buildAccountPerformanceSectionForQuarter(
                        "OPERATING COST",
                        operatingItems,
                        qtr,
                        quarterLabel,
                        fontBold,
                        fontRegular
                ));

                doc.add(new Paragraph("\n"));

                doc.add(buildAccountPerformanceSectionForQuarter(
                        "CAPITAL EXPENDITURE",
                        capitalItems,
                        qtr,
                        quarterLabel,
                        fontBold,
                        fontRegular
                ));

                doc.add(new Paragraph("\n"));

                doc.add(buildOverallAccountPerformanceTotalForQuarter(
                        accountItems,
                        qtr,
                        quarterLabel,
                        fontBold,
                        fontRegular
                ));

                doc.add(new Paragraph("\n"));
                doc.add(sectionTitle("GENERAL BUDGET PHYSICAL PERFORMANCE", fontBold));

                String quarterKey = selectedQuarter != null ? selectedQuarter : "qtr1";

                String physicalHtml = overallGeneralPhysicalPerformanceService.getQuarterHtml("GENERAL", quarterKey, budget);

                doc.add(new Paragraph("Quarter: " + quarterLabel)
                        .setFont(fontBold)
                        .setFontSize(10)
                        .setMarginBottom(6));

                doc.add(buildPhysicalPerformanceBlock(
                        physicalHtml,
                        fontRegular,
                        fontBold
                ));
                // REVENUE SOURCES
                /*                doc.add(sectionTitle("REVENUE SOURCES ANALYSIS", fontBold));
                
                Table revenueTable = baseTable(new float[]{2.8f, 1.1f, 1.6f, 1.6f, 0.9f});
                addHeader(revenueTable, fontBold, "Revenue Source", "Category", "Collected", "Projected", "Achievement");
                
                alt = false;
                for (RevenueSource r : safeList(summary.getRevenueSources())) {
                Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
                alt = !alt;
                
                addBodyCell(revenueTable, fontRegular, safe(r.getName()), bg, TextAlignment.LEFT);
                addBodyCell(revenueTable, fontRegular, safeUpper(r.getCategory()), bg, TextAlignment.CENTER);
                addBodyCell(revenueTable, fontRegular, formatUGX(r.getAmount()), bg, TextAlignment.RIGHT);
                addBodyCell(revenueTable, fontRegular, formatUGX(r.getProjected()), bg, TextAlignment.RIGHT);
                
                revenueTable.addCell(statusCell(
                fontRegular,
                pct(r.getProjectedPercentage()),
                achievementColor(r.getProjectedPercentage())
                ));
                }
                doc.add(revenueTable);*/

                // KPI
                doc.add(sectionTitle("KEY PERFORMANCE INDICATORS", fontBold));

                Table kpiTable = baseTable(new float[]{3.2f, 1.6f, 1.2f});
                addHeader(kpiTable, fontBold, "Indicator", "Value", "Status");

                double budgetEfficiency = ratioPct(summary.getTotalRevenue(), summary.getTotalBudget());

                addKpi(kpiTable, fontRegular, "Budget Utilization Rate", pct(summary.getSpentPercentage()),
                        statusTextLowerIsBetter(safe(summary.getSpentPercentage()).doubleValue(), 85, 95));

                addKpi(kpiTable, fontRegular, "Revenue Collection Rate", pct(summary.getRevenuePercentage()),
                        statusTextHigherIsBetter(safe(summary.getRevenuePercentage()).doubleValue(), 75, 90));

                addKpi(kpiTable, fontRegular, "Budget Efficiency Ratio", String.format(Locale.US, "%.1f%%", budgetEfficiency),
                        budgetEfficiency >= 100 ? "Excellent" : budgetEfficiency >= 80 ? "Good" : "Below Target");

                addKpi(kpiTable, fontRegular, "Remaining Budget", formatUGX(summary.getRemainingBudget()),
                        safe(summary.getRemainingBudget()).compareTo(BigDecimal.ZERO) > 0 ? "Available" : "Exceeded");

                doc.add(kpiTable);

                // RECOMMENDATIONS
                doc.add(sectionTitle("RECOMMENDATIONS & INSIGHTS", fontBold));
                doc.add(recommendationsBlock(summary, fontRegular));

                // Footer note (content)
                doc.add(new Paragraph("This report was generated automatically by the Budget Management System.")
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(12));

                // Fill total pages placeholder
                writeTotalPages(pdf, totalPagesPlaceholder, fontRegular);
            }

            return baos.toByteArray();
        }
    }

    private void addHtmlSectionToPdf(com.itextpdf.layout.element.Div wrapper, String title, String bodyHtml, PdfFont fontRegular, PdfFont fontBold) {
        wrapper.add(new Paragraph(title)
                .setFont(fontBold)
                .setFontSize(10)
                .setMarginTop(8)
                .setMarginBottom(4));

        String plainText = htmlToPlainText(bodyHtml);

        wrapper.add(new Paragraph(plainText.isBlank() ? "Not provided." : plainText)
                .setFont(fontRegular)
                .setFontSize(9)
                .setMarginTop(0)
                .setMarginBottom(6));
    }

    private String extractHtmlSection(String html, String heading) {
        if (html == null || html.isBlank()) {
            return "";
        }

        String escapedHeading = java.util.regex.Pattern.quote(heading);

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(?is)<h3[^>]*>\\s*(?:<[^>]+>\\s*)*" + escapedHeading + "\\s*(?:</[^>]+>\\s*)*</h3>(.*?)(?=<h3[^>]*>|$)"
        );

        java.util.regex.Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }

    private String htmlToPlainText(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }

        String text = html;

        // remove Quill helper spans
        text = text.replaceAll("(?is)<span[^>]*class=\"ql-ui\"[^>]*>.*?</span>", "");

        // bullets / lists
        text = text.replaceAll("(?i)<li[^>]*>", "• ");
        text = text.replaceAll("(?i)</li>", "\n");
        text = text.replaceAll("(?i)</ol>", "\n");
        text = text.replaceAll("(?i)</ul>", "\n");

        // paragraphs / breaks
        text = text.replaceAll("(?i)<br\\s*/?>", "\n");
        text = text.replaceAll("(?i)</p>", "\n");
        text = text.replaceAll("(?i)<p[^>]*>", "");

        // remove remaining tags
        text = text.replaceAll("(?is)<[^>]+>", "");

        // html entities
        text = text.replace("&nbsp;", " ");
        text = text.replace("&amp;", "&");
        text = text.replace("&lt;", "<");
        text = text.replace("&gt;", ">");

        // whitespace cleanup
        text = text.replaceAll("[ \\t\\x0B\\f\\r]+", " ");
        text = text.replaceAll("\\n\\s*\\n\\s*\\n+", "\n\n");

        return text.trim();
    }

    private com.itextpdf.layout.element.Div buildPhysicalPerformanceBlock(String html, PdfFont fontRegular, PdfFont fontBold) {
        com.itextpdf.layout.element.Div wrapper = new com.itextpdf.layout.element.Div();
        System.out.println(html);
        String content = html == null ? "" : html.trim();
        System.out.println(content);
        if (content.isBlank()) {
            content = """
                <h3>Summary</h3>
                <p>No narrative provided.</p>

                <h3>Key Achievements</h3>
                <p>No achievements recorded.</p>

                <h3>Challenges</h3>
                <p>No challenges recorded.</p>

                <h3>Way Forward</h3>
                <p>No way forward recorded.</p>
                """;
        }

        addHtmlSectionToPdf(wrapper, "Summary", extractHtmlSection(content, "Summary"), fontRegular, fontBold);
        addHtmlSectionToPdf(wrapper, "Key Achievements", extractHtmlSection(content, "Key Achievements"), fontRegular, fontBold);
        addHtmlSectionToPdf(wrapper, "Challenges", extractHtmlSection(content, "Challenges"), fontRegular, fontBold);
        addHtmlSectionToPdf(wrapper, "Way Forward", extractHtmlSection(content, "Way Forward"), fontRegular, fontBold);

        return wrapper;
    }

    private com.itextpdf.layout.element.Div buildAccountPerformanceSectionForQuarter(
            String title,
            java.util.List<BudgetItemsActuals> items,
            int qtr,
            String quarterLabel,
            PdfFont fontBold,
            PdfFont fontNormal
    ) {
        com.itextpdf.layout.element.Div section = new com.itextpdf.layout.element.Div();

        Paragraph heading = new Paragraph(title)
                .setFont(fontBold)
                .setFontSize(11)
                .setMarginBottom(6);

        Table table = new Table(UnitValue.createPercentArray(
                new float[]{50f, 150f, 90f, 90f, 90f, 90f, 90f, 75f}
        )).useAllAvailableWidth().setFixedLayout();

        addHeaderCell(table, "Code", fontBold);
        addHeaderCell(table, "Description", fontBold);
        addHeaderCell(table, "Cum. Budget (" + quarterLabel + ")", fontBold);
        addHeaderCell(table, "Cum. Actual (" + quarterLabel + ")", fontBold);
        addHeaderCell(table, "Total Budget", fontBold);
        addHeaderCell(table, "Total Actual", fontBold);
        addHeaderCell(table, "Variance", fontBold);
        addHeaderCell(table, "Status", fontBold);

        BigDecimal selectedBudgetTotal = BigDecimal.ZERO;
        BigDecimal selectedActualTotal = BigDecimal.ZERO;
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : safeList(items)) {
            COA coa = item.getCoacode();

            BigDecimal selectedBudget = getQuarterBudget(item, qtr);
            BigDecimal selectedActual = getQuarterActual(item, qtr);
            BigDecimal budgetVal = nz(item.getTotal());
            BigDecimal actualVal = nz(item.getTotalA());

            BigDecimal variance = budgetVal.subtract(selectedActual);

            selectedBudgetTotal = selectedBudgetTotal.add(selectedBudget);
            selectedActualTotal = selectedActualTotal.add(selectedActual);
            totalBudget = totalBudget.add(budgetVal);
            totalActual = totalActual.add(actualVal);
            totalVariance = totalVariance.add(variance);

            addBodyCell(table, coa != null ? nvl(coa.getCode(), "") : "", fontNormal);
            addBodyCell(table, nvl(item.getItem(), ""), fontNormal);
            addAmountCell(table, selectedBudget, fontNormal, false);
            addAmountCell(table, selectedActual, fontNormal, true);
            addAmountCell(table, budgetVal, fontNormal, false);
            addAmountCell(table, actualVal, fontNormal, true);
            addVarianceCell(table, variance, fontNormal);
            addStatusCell(table, budgetVal, selectedActual, variance, fontNormal);
        }

        addFooterCell(table, "SECTION TOTAL", fontBold, 2);
        addAmountFooterCell(table, selectedBudgetTotal, fontBold, false);
        addAmountFooterCell(table, selectedActualTotal, fontBold, true);
        addAmountFooterCell(table, totalBudget, fontBold, false);
        addAmountFooterCell(table, totalActual, fontBold, true);
        addVarianceFooterCell(table, totalVariance, fontBold);
        addStatusFooterCell(table, totalBudget, selectedActualTotal, totalVariance, fontBold);

        section.add(heading);
        section.add(table);
        return section;
    }

    private Table buildOverallAccountPerformanceTotalForQuarter2(
            java.util.List<BudgetItemsActuals> items,
            int qtr,
            String quarterLabel,
            PdfFont fontBold,
            PdfFont fontNormal
    ) {
        Table table = new Table(UnitValue.createPercentArray(
                new float[]{55f, 150f, 90f, 90f, 90f, 90f, 75f, 65f}
        )).useAllAvailableWidth().setFixedLayout();

        addHeaderCell(table, "OVERALL TOTAL", fontBold);
        addHeaderCell(table, "Cum. Budget (" + quarterLabel + ")", fontBold);
        addHeaderCell(table, "Cum. Actual (" + quarterLabel + ")", fontBold);
        addHeaderCell(table, "Total Budget", fontBold);
        addHeaderCell(table, "Total Actual", fontBold);
        addHeaderCell(table, "Variance", fontBold);
        addHeaderCell(table, "Status", fontBold);

        BigDecimal selectedBudgetTotal = BigDecimal.ZERO;
        BigDecimal selectedActualTotal = BigDecimal.ZERO;
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : safeList(items)) {
            String code = item.getCoacode() != null ? item.getCoacode().getCode() : null;
            if (code == null || (!code.startsWith("2") && !code.startsWith("3"))) {
                continue;
            }

            BigDecimal selectedBudget = getQuarterBudget(item, qtr);
            BigDecimal selectedActual = getQuarterActual(item, qtr);
            BigDecimal budgetVal = nz(item.getTotal());
            BigDecimal actualVal = nz(item.getTotalA());

            BigDecimal variance = budgetVal.subtract(selectedActual);

            selectedBudgetTotal = selectedBudgetTotal.add(selectedBudget);
            selectedActualTotal = selectedActualTotal.add(selectedActual);
            totalBudget = totalBudget.add(budgetVal);
            totalActual = totalActual.add(actualVal);
            totalVariance = totalVariance.add(variance);
        }

        table.addCell(
                new Cell().add(new Paragraph("Operating Cost + Capital Expenditure")
                        .setFont(fontBold)
                        .setFontSize(9))
                        .setPadding(5)
        );

        addAmountFooterCell(table, selectedBudgetTotal, fontBold, false);
        addAmountFooterCell(table, selectedActualTotal, fontBold, true);
        addAmountFooterCell(table, totalBudget, fontBold, false);
        addAmountFooterCell(table, totalActual, fontBold, true);
        addVarianceFooterCell(table, totalVariance, fontBold);
        addStatusFooterCell(table, totalBudget, selectedActualTotal, totalVariance, fontBold);

        return table;
    }

    private Table buildOverallAccountPerformanceTotalForQuarter(
            java.util.List<BudgetItemsActuals> items,
            int qtr,
            String quarterLabel,
            PdfFont fontBold,
            PdfFont fontNormal
    ) {
        Table table = new Table(UnitValue.createPercentArray(
                new float[]{170f, 85f, 85f, 85f, 85f, 75f, 65f}
        )).useAllAvailableWidth().setFixedLayout();

        addHeaderCell(table, "OVERALL TOTAL", fontBold);
        addHeaderCell(table, "Budget (" + quarterLabel + ")", fontBold);
        addHeaderCell(table, "Actual (" + quarterLabel + ")", fontBold);
        addHeaderCell(table, "Total Budget", fontBold);
        addHeaderCell(table, "Total Actual", fontBold);
        addHeaderCell(table, "Variance", fontBold);
        addHeaderCell(table, "Status", fontBold);

        BigDecimal selectedBudgetTotal = BigDecimal.ZERO;
        BigDecimal selectedActualTotal = BigDecimal.ZERO;
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : safeList(items)) {
            String code = item.getCoacode() != null ? item.getCoacode().getCode() : null;
            if (code == null || (!code.startsWith("2") && !code.startsWith("3"))) {
                continue;
            }

            BigDecimal selectedBudget = getQuarterBudget(item, qtr);
            BigDecimal selectedActual = getQuarterActual(item, qtr);
            BigDecimal budgetVal = nz(item.getTotal());
            BigDecimal actualVal = nz(item.getTotalA());

            BigDecimal variance = budgetVal.subtract(selectedActual);

            selectedBudgetTotal = selectedBudgetTotal.add(selectedBudget);
            selectedActualTotal = selectedActualTotal.add(selectedActual);
            totalBudget = totalBudget.add(budgetVal);
            totalActual = totalActual.add(actualVal);
            totalVariance = totalVariance.add(variance);
        }

        table.addCell(
                new Cell().add(new Paragraph("Operating Cost + Capital Expenditure")
                        .setFont(fontBold)
                        .setFontSize(8))
                        .setPadding(3)
        );

        addAmountFooterCell(table, selectedBudgetTotal, fontBold, false);
        addAmountFooterCell(table, selectedActualTotal, fontBold, true);
        addAmountFooterCell(table, totalBudget, fontBold, false);
        addAmountFooterCell(table, totalActual, fontBold, true);
        addVarianceFooterCell(table, totalVariance, fontBold);
        addStatusFooterCell(table, totalBudget, selectedActualTotal, totalVariance, fontBold);

        return table;
    }

    private com.itextpdf.layout.element.Div buildAccountPerformanceSection(
            String title,
            java.util.List<BudgetItemsActuals> items,
            PdfFont fontBold,
            PdfFont fontNormal
    ) {
        com.itextpdf.layout.element.Div section = new com.itextpdf.layout.element.Div();

        Paragraph heading = new Paragraph(title)
                .setFont(fontBold)
                .setFontSize(11)
                .setMarginBottom(6);

        Table table = new Table(UnitValue.createPercentArray(
                new float[]{50f, 150f, 90f, 90f, 90f, 90f, 90f, 75f}
        )).useAllAvailableWidth().setFixedLayout();

        addHeaderCell(table, "Code", fontBold);
        addHeaderCell(table, "Description", fontBold);
        addHeaderCell(table, "Total Budget", fontBold);
        addHeaderCell(table, "Total Actual", fontBold);
        addHeaderCell(table, "Variance", fontBold);
        addHeaderCell(table, "Status", fontBold);

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : safeList(items)) {
            COA coa = item.getCoacode();

            BigDecimal budget = nz(item.getTotal());
            BigDecimal actual = nz(item.getTotalA());
            BigDecimal variance = budget.subtract(actual);

            totalBudget = totalBudget.add(budget);
            totalActual = totalActual.add(actual);
            totalVariance = totalVariance.add(variance);

            addBodyCell(table, coa != null ? nvl(coa.getCode(), "") : "", fontNormal);
            addBodyCell(table, nvl(item.getItem(), ""), fontNormal);
            addAmountCell(table, budget, fontNormal, false);
            addAmountCell(table, actual, fontNormal, true);
            addVarianceCell(table, variance, fontNormal);
            addStatusCell(table, budget, actual, variance, fontNormal);
        }

        addFooterCell(table, "SECTION TOTAL", fontBold, 2);
        addAmountFooterCell(table, totalBudget, fontBold, false);
        addAmountFooterCell(table, totalActual, fontBold, true);
        addVarianceFooterCell(table, totalVariance, fontBold);
        addStatusFooterCell(table, totalBudget, totalActual, totalVariance, fontBold);

        section.add(heading);
        section.add(table);
        return section;
    }

    private Table buildOverallAccountPerformanceTotal(
            java.util.List<BudgetItemsActuals> items,
            PdfFont fontBold,
            PdfFont fontNormal
    ) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{290f, 90f, 90f, 90f, 80f}))
                .useAllAvailableWidth();

        addHeaderCell(table, "OVERALL TOTAL", fontBold);
        addHeaderCell(table, "Total Budget", fontBold);
        addHeaderCell(table, "Total Actual", fontBold);
        addHeaderCell(table, "Variance", fontBold);
        addHeaderCell(table, "Status", fontBold);

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : safeList(items)) {
            String code = item.getCoacode() != null ? item.getCoacode().getCode() : null;
            if (code == null || (!code.startsWith("2") && !code.startsWith("3"))) {
                continue;
            }

            BigDecimal budget = nz(item.getTotal());
            BigDecimal actual = nz(item.getTotalA());
            BigDecimal variance = budget.subtract(actual);

            totalBudget = totalBudget.add(budget);
            totalActual = totalActual.add(actual);
            totalVariance = totalVariance.add(variance);
        }

        table.addCell(
                new Cell().add(new Paragraph("Operating Cost + Capital Expenditure")
                        .setFont(fontBold)
                        .setFontSize(9))
                        .setPadding(5)
        );

        addAmountFooterCell(table, totalBudget, fontBold, false);
        addAmountFooterCell(table, totalActual, fontBold, true);
        addVarianceFooterCell(table, totalVariance, fontBold);
        addStatusFooterCell(table, totalBudget, totalActual, totalVariance, fontBold);

        return table;
    }

    private Table buildAccountPerformanceTable(
            java.util.List<BudgetItemsActuals> items,
            PdfFont fontBold,
            PdfFont fontNormal
    ) {
        float[] widths = {70f, 220f, 90f, 90f, 90f, 80f};
        Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();

        addHeaderCell(table, "Code", fontBold);
        addHeaderCell(table, "Description", fontBold);
        addHeaderCell(table, "Total Budget", fontBold);
        addHeaderCell(table, "Total Actual", fontBold);
        addHeaderCell(table, "Variance", fontBold);
        addHeaderCell(table, "Status", fontBold);

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : safeList(items)) {
            COA coa = item.getCoacode();

            BigDecimal budget = nz(item.getTotal());
            BigDecimal actual = nz(item.getTotalA());
            BigDecimal variance = budget.subtract(actual);

            totalBudget = totalBudget.add(budget);
            totalActual = totalActual.add(actual);
            totalVariance = totalVariance.add(variance);

            addBodyCell(table, coa != null ? nvl(coa.getCode(), "") : "", fontNormal);
            addBodyCell(table, nvl(item.getItem(), ""), fontNormal);
            addAmountCell(table, budget, fontNormal, false);
            addAmountCell(table, actual, fontNormal, true);
            addVarianceCell(table, variance, fontNormal);
            addStatusCell(table, budget, actual, variance, fontNormal);
        }

        addFooterCell(table, "TOTAL", fontBold, 2);
        addAmountFooterCell(table, totalBudget, fontBold, false);
        addAmountFooterCell(table, totalActual, fontBold, true);
        addVarianceFooterCell(table, totalVariance, fontBold);
        addStatusFooterCell(table, totalBudget, totalActual, totalVariance, fontBold);

        return table;
    }

    private void addVarianceCell(Table table, BigDecimal value, PdfFont font) {
        Paragraph p = new Paragraph(formatCurrencyCompact(value))
                .setFont(font)
                .setFontSize(8);

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            p.setFontColor(new DeviceRgb(200, 0, 0));
        } else {
            p.setFontColor(new DeviceRgb(34, 139, 34));
        }

        table.addCell(
                new Cell()
                        .add(p)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setPadding(4)
        );
    }

    private void addStatusCell(Table table, BigDecimal budget, BigDecimal actual, BigDecimal variance, PdfFont font) {
        String status;
        DeviceRgb color;

        if (budget.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            status = "UNBUDGETED";
            color = new DeviceRgb(200, 0, 0);
        } else if (variance.compareTo(BigDecimal.ZERO) < 0) {
            status = "OVER";
            color = new DeviceRgb(200, 0, 0);
        } else {
            status = "FINE";
            color = new DeviceRgb(34, 139, 34);
        }

        Paragraph p = new Paragraph(status)
                .setFont(font)
                .setFontSize(8)
                .setFontColor(color);

        table.addCell(
                new Cell()
                        .add(p)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(4)
        );
    }

    private void addVarianceFooterCell(Table table, BigDecimal value, PdfFont font) {
        Paragraph p = new Paragraph(formatCurrencyCompact(value))
                .setFont(font)
                .setFontSize(9);

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            p.setFontColor(new DeviceRgb(200, 0, 0));
        } else {
            p.setFontColor(new DeviceRgb(34, 139, 34));
        }

        table.addCell(
                new Cell()
                        .add(p)
                        .setBackgroundColor(new DeviceRgb(245, 245, 245))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setPadding(5)
        );
    }

    private void addStatusFooterCell(Table table, BigDecimal budget, BigDecimal actual, BigDecimal variance, PdfFont font) {
        String status;
        DeviceRgb color;

        if (budget.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            status = "UNBUDGETED";
            color = new DeviceRgb(200, 0, 0);
        } else if (variance.compareTo(BigDecimal.ZERO) < 0) {
            status = "OVER";
            color = new DeviceRgb(200, 0, 0);
        } else {
            status = "FINE";
            color = new DeviceRgb(34, 139, 34);
        }

        Paragraph p = new Paragraph(status)
                .setFont(font)
                .setFontSize(9)
                .setFontColor(color);

        table.addCell(
                new Cell()
                        .add(p)
                        .setBackgroundColor(new DeviceRgb(245, 245, 245))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5)
        );
    }

    private void addHeaderCell(Table table, String text, PdfFont font) {
        table.addHeaderCell(
                new Cell()
                        .add(new Paragraph(text).setFont(font).setFontSize(9))
                        .setBackgroundColor(new DeviceRgb(230, 230, 230))
                        .setPadding(5)
        );
    }

    private void addBodyCell(Table table, String text, PdfFont font) {
        table.addCell(
                new Cell()
                        .add(new Paragraph(text).setFont(font).setFontSize(8))
                        .setPadding(4)
        );
    }

    private void addAmountCell(Table table, BigDecimal value, PdfFont font, boolean actual) {
        Paragraph p = new Paragraph(formatCurrencyCompact(value))
                .setFont(font)
                .setFontSize(8);

        if (actual) {
            p.setFontColor(new DeviceRgb(21, 101, 192));
        }

        table.addCell(
                new Cell()
                        .add(p)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setPadding(4)
        );
    }

    private void addFooterCell(Table table, String text, PdfFont font, int colspan) {
        table.addCell(
                new Cell(1, colspan)
                        .add(new Paragraph(text).setFont(font).setFontSize(9))
                        .setBackgroundColor(new DeviceRgb(245, 245, 245))
                        .setPadding(5)
        );
    }

    private void addAmountFooterCell(Table table, BigDecimal value, PdfFont font, boolean actual) {
        Paragraph p = new Paragraph(formatCurrencyCompact(value))
                .setFont(font)
                .setFontSize(9);

        if (actual) {
            p.setFontColor(new DeviceRgb(21, 101, 192));
        }

        table.addCell(
                new Cell()
                        .add(p)
                        .setBackgroundColor(new DeviceRgb(245, 245, 245))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setPadding(5)
        );
    }

    private String formatCurrencyCompact(BigDecimal value) {
        return value == null ? "0.0" : new DecimalFormat("#,##0.0").format(value);
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private <T> java.util.List<T> safeList(java.util.List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private String nvl(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    /* =========================
   Header / Footer Handler
   ========================= */
    private static class ReportHeaderFooterHandler implements IEventHandler {

        private final String title;
        private final String subtitle;
        private final PdfFont regular;
        private final PdfFont bold;
        private final ImageData logo;
        private final PdfFormXObject totalPages;

        ReportHeaderFooterHandler(String title, String subtitle,
                PdfFont regular, PdfFont bold,
                ImageData logo,
                PdfFormXObject totalPages) {
            this.title = title;
            this.subtitle = subtitle;
            this.regular = regular;
            this.bold = bold;
            this.logo = logo;
            this.totalPages = totalPages;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent ev = (PdfDocumentEvent) event;
            PdfDocument pdf = ev.getDocument();

            PdfPage page = ev.getPage();
            int pageNumber = pdf.getPageNumber(page);

            Rectangle ps = page.getPageSize();
            float left = ps.getLeft() + MARGIN_LEFT;
            float right = ps.getRight() - MARGIN_RIGHT;

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);

            try (Canvas canvas = new Canvas(pdfCanvas, ps)) {

                /* =========================
           HEADER (ONLY PAGE 1)
           ========================= */
                if (pageNumber == 1) {

                    float headerTopY = ps.getTop() - 26f;
                    float dividerY = ps.getTop() - 70f;

                    // LOGO
                    if (logo != null) {
                        Image img = new Image(logo);
                        img.scaleToFit(75, 40);
                        img.setFixedPosition(left, ps.getTop() - 58f);
                        canvas.add(img);
                    }

                    // TITLE
                    canvas.showTextAligned(
                            new Paragraph(title).setFont(bold).setFontSize(12),
                            (left + right) / 2f,
                            headerTopY,
                            TextAlignment.CENTER
                    );

                    // SUBTITLE
                    canvas.showTextAligned(
                            new Paragraph(subtitle)
                                    .setFont(regular)
                                    .setFontSize(9)
                                    .setFontColor(ColorConstants.DARK_GRAY),
                            (left + right) / 2f,
                            headerTopY - 14f,
                            TextAlignment.CENTER
                    );

                    // Divider line
                    pdfCanvas.setStrokeColor(COLOR_GRID).setLineWidth(0.8f);
                    pdfCanvas.moveTo(left, dividerY).lineTo(right, dividerY).stroke();
                }

                /* =========================
           FOOTER (ALL PAGES)
           ========================= */
                float footerY = ps.getBottom() + 28f;

                pdfCanvas.setStrokeColor(COLOR_GRID).setLineWidth(0.8f);
                pdfCanvas.moveTo(left, footerY + 12f).lineTo(right, footerY + 12f).stroke();

                canvas.showTextAligned(
                        new Paragraph("Page " + pageNumber + " of ")
                                .setFont(regular)
                                .setFontSize(8)
                                .setFontColor(ColorConstants.DARK_GRAY),
                        left,
                        footerY,
                        TextAlignment.LEFT
                );

                Image totalImg = new Image(totalPages).scaleToFit(50, 12);
                totalImg.setFixedPosition(left + 52f, footerY - 3f);
                canvas.add(totalImg);

                canvas.showTextAligned(
                        new Paragraph("Confidential")
                                .setFont(regular)
                                .setFontSize(8)
                                .setFontColor(ColorConstants.DARK_GRAY),
                        right,
                        footerY,
                        TextAlignment.RIGHT
                );
            }
        }
    }

    /* =========================
   Styling helpers
   ========================= */
    private Paragraph sectionTitle(String text, PdfFont bold) {
        return new Paragraph(text)
                .setFont(bold)
                .setFontSize(12)
                .setMarginTop(10)
                .setMarginBottom(8);
    }

    private Table baseTable(float[] weights) {
        return new Table(UnitValue.createPercentArray(weights))
                .useAllAvailableWidth()
                .setFixedLayout() // ✅ important: prevents width rebalancing warnings
                .setMarginBottom(14);
    }

    private void addHeader(Table table, PdfFont bold, String... headers) {
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFont(bold).setFontSize(9))
                    .setBackgroundColor(COLOR_HEADER_BG)
                    .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                    .setPadding(4) // ✅ was 7
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    private void addRow(Table table, PdfFont font, boolean altRow, String key, String value, boolean rightAlignValue) {
        Color bg = altRow ? COLOR_ALT_ROW : ColorConstants.WHITE;
        addBodyCell(table, font, key, bg, TextAlignment.LEFT);
        addBodyCell(table, font, value, bg, rightAlignValue ? TextAlignment.RIGHT : TextAlignment.LEFT);
    }

    private void addKpi(Table t, PdfFont font, String indicator, String value, String status) {
        addBodyCell(t, font, indicator, ColorConstants.WHITE, TextAlignment.LEFT);
        addBodyCell(t, font, value, ColorConstants.WHITE, TextAlignment.RIGHT);
        addBodyCell(t, font, status, ColorConstants.WHITE, TextAlignment.CENTER);
    }

    private void addBodyCell(Table t, PdfFont font, String text, Color bg, TextAlignment align) {
        t.addCell(new Cell()
                .add(new Paragraph(safe(text)).setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4) // ✅ was 7
                .setTextAlignment(align)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
    }

    private Cell statusCell(PdfFont font, String text, Color bg) {
        return new Cell()
                .add(new Paragraph(safe(text)).setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(6)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /* =========================
   Business formatting helpers
   ========================= */
    private String formatUGX(BigDecimal amount) {
        BigDecimal a = safe(amount);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return "UGX " + nf.format(a);
    }

    private String pct(BigDecimal v) {
        return String.format(Locale.US, "%.1f%%", safe(v).doubleValue());
    }

    private double ratioPct(BigDecimal num, BigDecimal den) {
        BigDecimal n = safe(num), d = safe(den);
        if (d.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        return (n.doubleValue() / d.doubleValue()) * 100.0;
    }

    private Color statusColor(BigDecimal spentPct) {
        double v = safe(spentPct).doubleValue();
        if (v > 90) {
            return new com.itextpdf.kernel.colors.DeviceRgb(255, 220, 230); // soft pink
        }
        if (v > 75) {
            return new com.itextpdf.kernel.colors.DeviceRgb(255, 245, 210); // soft yellow
        }
        return new com.itextpdf.kernel.colors.DeviceRgb(220, 245, 230);            // soft green
    }

    private Color achievementColor(BigDecimal pct) {
        double v = safe(pct).doubleValue();
        if (v >= 100) {
            return new com.itextpdf.kernel.colors.DeviceRgb(220, 245, 230);
        }
        if (v >= 80) {
            return new com.itextpdf.kernel.colors.DeviceRgb(255, 245, 210);
        }
        return new com.itextpdf.kernel.colors.DeviceRgb(255, 220, 230);
    }

    private String statusTextLowerIsBetter(double value, double goodLimit, double cautionLimit) {
        return value <= goodLimit ? "Good" : value <= cautionLimit ? "Caution" : "Critical";
    }

    private String statusTextHigherIsBetter(double value, double goodLimit, double excellentLimit) {
        return value >= excellentLimit ? "Excellent" : value >= goodLimit ? "Good" : "Needs Improvement";
    }

    private Paragraph recommendationsBlock(BudgetSummary s, PdfFont font) {
        StringBuilder rec = new StringBuilder();

        double spent = safe(s.getSpentPercentage()).doubleValue();
        double rev = safe(s.getRevenuePercentage()).doubleValue();

        if (spent > 95) {
            rec.append("• URGENT: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
        } else if (spent > 85) {
            rec.append("• CAUTION: Budget utilization is above 85%. Monitor spending closely and review commitments.\n");
        } else {
            rec.append("• Budget utilization is within an acceptable range.\n");
        }

        if (rev < 75) {
            rec.append("• Revenue collection is below target. Review billing, enforcement, and collection strategies.\n");
        } else if (rev >= 100) {
            rec.append("• Excellent revenue performance. Consider expanding high-performing revenue streams.\n");
        }

        long overBudget = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() > 100)
                .count();

        if (overBudget > 0) {
            rec.append("• ").append(overBudget)
                    .append(" department(s) have exceeded their budget allocation. Immediate review is required.\n");
        }

        long underUtilized = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() < 40)
                .count();

        if (underUtilized > 0) {
            rec.append("• ").append(underUtilized)
                    .append(" department(s) show low budget utilization. Consider phased reallocation or targeted execution support.\n");
        }

        if (rec.length() == 0) {
            rec.append("• Overall budget performance is satisfactory. Continue monitoring.");
        }

        return new Paragraph(rec.toString())
                .setFont(font)
                .setFontSize(10)
                .setPadding(10)
                .setMarginBottom(12)
                .setBackgroundColor(COLOR_NOTE_BG)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f));
    }

    /* =========================
   Total pages placeholder writing
   ========================= */
    private void writeTotalPages(PdfDocument pdf, PdfFormXObject placeholder, PdfFont font) {
        try (Canvas canvas = new Canvas(placeholder, pdf)) {
            canvas.add(new Paragraph(String.valueOf(pdf.getNumberOfPages()))
                    .setFont(font)
                    .setFontSize(8)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMargin(0)
                    .setBorder(Border.NO_BORDER));
        }
    }

    /* =========================
   Logo loader (classpath resource)
   ========================= */
    private ImageData loadLogo(String classpathResource) {
        try (InputStream is = getClass().getResourceAsStream(classpathResource)) {
            if (is == null) {
                return null;
            }
            return ImageDataFactory.create(is.readAllBytes());
        } catch (Exception e) {
            // If logo fails to load, we still generate report without it.
            return null;
        }
    }

    /* =========================
   Null-safe helpers
   ========================= */
    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String safeUpper(Object o) {
        return o == null ? "" : String.valueOf(o).toUpperCase(Locale.ROOT);
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return "UGX " + formatter.format(amount);
    }

    private void generateWordReport() {
        try {
            Budget currentBudget = selectedBudget != null ? selectedBudget : fiscalYear.getValue();
            if (currentBudget == null) {
                showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
                return;
            }

            BudgetSummary summary = budgetService.getBudgetSummary(currentBudget);
            if (summary == null) {
                showNotification("No data found for the selected fiscal year.", NotificationVariant.LUMO_ERROR);
                return;
            }

            byte[] docxBytes = buildBudgetDashboardDocx(currentBudget, summary);

            String fileName = "Budget_Dashboard_Report_"
                    + safe(currentBudget.getFinancialYear()).replace("/", "-")
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                    + ".docx";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(docxBytes));
            resource.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            resource.setCacheTime(0);

            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration reg = ui.getSession().getResourceRegistry().registerResource(resource);
                ui.getPage().open(reg.getResourceUri().toString(), "_blank");
                showNotification("Word report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Unable to open Word report (no UI context).", NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            showNotification("Error generating Word report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private byte[] buildBudgetDashboardDocx(Budget budget, BudgetSummary summary)
            throws IOException, InvalidFormatException {

        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Page margins (twips: 1440 = 1 inch)
            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar mar = sectPr.addNewPgMar();
            mar.setTop(1100);    // ~0.76"
            mar.setBottom(900);  // ~0.63"
            mar.setLeft(900);    // ~0.63"
            mar.setRight(900);   // ~0.63"

            // Optional: add logo (classpath resource)
            // Put logo at: src/main/resources/images/logo.png
            addLogoIfPresent(doc, "/images/logo.png", 110, 55);

            // Title block
            addCenteredTitle(doc, "BUDGET MANAGEMENT DASHBOARD REPORT", 16, true);
            addCenteredTitle(doc, "Fiscal Year: " + safe(budget.getFinancialYear()), 12, true);

            addRightMeta(doc, "Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm")));

            addSpacer(doc, 1);

            // Executive Summary
            addSectionHeader(doc, "EXECUTIVE SUMMARY");
            XWPFTable summaryTable = doc.createTable(1, 2);
            styleTable(summaryTable);
            setHeaderRow(summaryTable.getRow(0), "Metric", "Amount");

            addRow(summaryTable, "Total Budget", formatUGX(summary.getTotalBudget()));
            addRow(summaryTable, "Total Spent", formatUGX(summary.getTotalSpent()));
            addRow(summaryTable, "Remaining Budget", formatUGX(summary.getRemainingBudget()));
            addRow(summaryTable, "Total Revenue", formatUGX(summary.getTotalRevenue()));
            addRow(summaryTable, "Projected Revenue", formatUGX(summary.getProjectedRevenue()));
            addRow(summaryTable, "Budget Utilization", pct(summary.getSpentPercentage()));
            addRow(summaryTable, "Revenue Achievement", pct(summary.getRevenuePercentage()));

            addSpacer(doc, 1);

            // Department Budget Analysis
            addSectionHeader(doc, "DEPARTMENT BUDGET ANALYSIS");
            XWPFTable deptTable = doc.createTable(1, 6);
            styleTable(deptTable);
            setHeaderRow(deptTable.getRow(0),
                    "Department", "Total Budget", "Total Spent", "Available", "Utilization", "Status");

            for (DepartmentBudget d : safeList(summary.getDepartmentBudgets())) {
                XWPFTableRow r = deptTable.createRow();
                setCell(r.getCell(0), safe(d.getDepartmentName()), false, ParagraphAlignment.LEFT);
                setCell(r.getCell(1), formatUGX(d.getTotalBudget()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(2), formatUGX(d.getTotalSpent()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(3), formatUGX(d.getAvailableBudget()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(4), pct(d.getSpentPercentage()), false, ParagraphAlignment.RIGHT);

                String status = safe(d.getStatusText());
                setCell(r.getCell(5), status, false, ParagraphAlignment.CENTER);

                // Soft status shading
                shadeCell(r.getCell(5), statusColorHex(d.getSpentPercentage()));
            }

            addSpacer(doc, 1);

            // Revenue Sources
            addSectionHeader(doc, "REVENUE SOURCES ANALYSIS");
            XWPFTable revTable = doc.createTable(1, 5);
            styleTable(revTable);
            setHeaderRow(revTable.getRow(0),
                    "Revenue Source", "Category", "Collected", "Projected", "Achievement");

            for (RevenueSource rs : safeList(summary.getRevenueSources())) {
                XWPFTableRow r = revTable.createRow();
                setCell(r.getCell(0), safe(rs.getName()), false, ParagraphAlignment.LEFT);
                setCell(r.getCell(1), safeUpper(rs.getCategory()), false, ParagraphAlignment.CENTER);
                setCell(r.getCell(2), formatUGX(rs.getAmount()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(3), formatUGX(rs.getProjected()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(4), pct(rs.getProjectedPercentage()), false, ParagraphAlignment.CENTER);

                shadeCell(r.getCell(4), achievementColorHex(rs.getProjectedPercentage()));
            }

            addSpacer(doc, 1);

            // KPI
            addSectionHeader(doc, "KEY PERFORMANCE INDICATORS");
            XWPFTable kpiTable = doc.createTable(1, 3);
            styleTable(kpiTable);
            setHeaderRow(kpiTable.getRow(0), "Indicator", "Value", "Status");

            double efficiency = ratioPct(summary.getTotalRevenue(), summary.getTotalBudget());

            addKpiRow(kpiTable, "Budget Utilization Rate", pct(summary.getSpentPercentage()),
                    statusTextLowerIsBetter(safe(summary.getSpentPercentage()).doubleValue(), 85, 95));

            addKpiRow(kpiTable, "Revenue Collection Rate", pct(summary.getRevenuePercentage()),
                    statusTextHigherIsBetter(safe(summary.getRevenuePercentage()).doubleValue(), 75, 90));

            addKpiRow(kpiTable, "Budget Efficiency Ratio",
                    String.format(Locale.US, "%.1f%%", efficiency),
                    efficiency >= 100 ? "Excellent" : efficiency >= 80 ? "Good" : "Below Target");

            addKpiRow(kpiTable, "Remaining Budget", formatUGX(summary.getRemainingBudget()),
                    safe(summary.getRemainingBudget()).compareTo(BigDecimal.ZERO) > 0 ? "Available" : "Exceeded");

            addSpacer(doc, 1);

            // Recommendations
            addSectionHeader(doc, "RECOMMENDATIONS & INSIGHTS");
            XWPFParagraph rec = doc.createParagraph();
            rec.setSpacingBefore(120);
            rec.setSpacingAfter(120);
            XWPFRun rr = rec.createRun();
            rr.setFontFamily("Calibri");
            rr.setFontSize(11);
            rr.setText(buildRecommendations(summary));

            addSpacer(doc, 1);

            // Footer note
            XWPFParagraph foot = doc.createParagraph();
            foot.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun fr = foot.createRun();
            fr.setFontFamily("Calibri");
            fr.setFontSize(9);
            fr.setColor("666666");
            fr.setText("This report was generated automatically by the Budget Management System.");

            doc.write(baos);
            return baos.toByteArray();
        }
    }
    // ===== Paragraph helpers =====

    private void addCenteredTitle(XWPFDocument doc, String text, int size, boolean bold) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.setSpacingAfter(80);
        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(size);
        r.setBold(bold);
        r.setText(text);
    }

    private void addRightMeta(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.RIGHT);
        p.setSpacingAfter(160);
        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(9);
        r.setColor("666666");
        r.setText(text);
    }

    private void addSectionHeader(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(220);
        p.setSpacingAfter(120);
        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(12);
        r.setBold(true);
        r.setText(text);
    }

    private void addSpacer(XWPFDocument doc, int lines) {
        for (int i = 0; i < lines; i++) {
            XWPFParagraph p = doc.createParagraph();
            p.setSpacingAfter(80);
        }
    }

// ===== Table helpers =====
    private void styleTable(XWPFTable table) {
        table.setWidth("100%");
        table.setTableAlignment(TableRowAlign.CENTER);
    }

    private void setHeaderRow(XWPFTableRow row, String... headers) {
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = row.getCell(i);
            setCell(cell, headers[i], true, ParagraphAlignment.CENTER);
            shadeCell(cell, "F2F2F2");
        }
    }

    private void addRow(XWPFTable table, String c1, String c2) {
        XWPFTableRow r = table.createRow();
        setCell(r.getCell(0), c1, false, ParagraphAlignment.LEFT);
        setCell(r.getCell(1), c2, false, ParagraphAlignment.RIGHT);
    }

    private void addKpiRow(XWPFTable table, String indicator, String value, String status) {
        XWPFTableRow r = table.createRow();
        setCell(r.getCell(0), indicator, false, ParagraphAlignment.LEFT);
        setCell(r.getCell(1), value, false, ParagraphAlignment.RIGHT);
        setCell(r.getCell(2), status, false, ParagraphAlignment.CENTER);
    }

    private void setCell(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment align) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setAlignment(align);
        p.setSpacingBefore(60);
        p.setSpacingAfter(60);

        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(10);
        r.setBold(bold);
        r.setText(text == null ? "" : text);
    }

    private void shadeCell(XWPFTableCell cell, String hexColor) {
        // hexColor like "FFFFFF" or "F2F2F2"
        cell.setColor(hexColor);
    }

// ===== Logo =====
    private void addLogoIfPresent(XWPFDocument doc, String classpath, int widthPx, int heightPx)
            throws IOException, InvalidFormatException {
        try (InputStream is = getClass().getResourceAsStream(classpath)) {
            if (is == null) {
                return;
            }

            XWPFParagraph p = doc.createParagraph();
            p.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun r = p.createRun();

            r.addPicture(is,
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "logo.png",
                    Units.toEMU(widthPx),
                    Units.toEMU(heightPx));

            p.setSpacingAfter(60);
        }
    }

    private String buildRecommendations(BudgetSummary s) {
        StringBuilder rec = new StringBuilder();

        double spent = safe(s.getSpentPercentage()).doubleValue();
        double rev = safe(s.getRevenuePercentage()).doubleValue();

        if (spent > 95) {
            rec.append("• URGENT: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
        } else if (spent > 85) {
            rec.append("• CAUTION: Budget utilization is above 85%. Monitor spending closely.\n");
        } else {
            rec.append("• Budget utilization is within an acceptable range.\n");
        }

        if (rev < 75) {
            rec.append("• Revenue collection is below target. Review collection strategies.\n");
        } else if (rev >= 100) {
            rec.append("• Excellent revenue performance. Consider expanding revenue streams.\n");
        }

        long overBudget = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() > 100)
                .count();
        if (overBudget > 0) {
            rec.append("• ").append(overBudget)
                    .append(" department(s) have exceeded their budget allocation. Immediate review required.\n");
        }

        long underUtilized = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() < 40)
                .count();
        if (underUtilized > 0) {
            rec.append("• ").append(underUtilized)
                    .append(" department(s) have low utilization. Consider reallocation.\n");
        }

        if (rec.length() == 0) {
            rec.append("• Overall budget performance is satisfactory. Continue monitoring.\n");
        }
        return rec.toString();
    }

// Status shading (soft)
    private String statusColorHex(BigDecimal spentPct) {
        double v = safe(spentPct).doubleValue();
        if (v > 90) {
            return "FFDCE6"; // soft pink
        }
        if (v > 75) {
            return "FFF5D2"; // soft yellow
        }
        return "DCF5E6";            // soft green
    }

    private String achievementColorHex(BigDecimal pct) {
        double v = safe(pct).doubleValue();
        if (v >= 100) {
            return "DCF5E6";
        }
        if (v >= 80) {
            return "FFF5D2";
        }
        return "FFDCE6";
    }

    private Component budgetCoaQtrGeneral(Budget bdgt, Set<UrcDeptSectionAnlDimbgt> deptSections) {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);

        Grid<BudgetItemsActuals> gridBudgetItemsQuarterlyGrid = new Grid<>(BudgetItemsActuals.class, false);
        gridBudgetItemsQuarterlyGrid.setWidthFull();
        gridBudgetItemsQuarterlyGrid.addThemeVariants(
                GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_ROW_STRIPES
        );

        Grid.Column<BudgetItemsActuals> codeColumn;
        Grid.Column<BudgetItemsActuals> descColumn;
        Grid.Column<BudgetItemsActuals> selectedQtrColumn;
        Grid.Column<BudgetItemsActuals> selectedQtrAColumn;

        Grid.Column<BudgetItemsActuals> totalQtrColumn;
        Grid.Column<BudgetItemsActuals> totalAQtrColumn;
        Grid.Column<BudgetItemsActuals> balanceColumn;
        Grid.Column<BudgetItemsActuals> statusColumn;

        codeColumn = gridBudgetItemsQuarterlyGrid.addColumn(item -> {
            COA coa = item.getCoacode();
            return coa != null ? coa.getCode() : "";
        }).setHeader("Code").setWidth("100px").setFlexGrow(0);

        descColumn = gridBudgetItemsQuarterlyGrid.addColumn(BudgetItemsActuals::getItem)
                .setHeader("Description")
                .setWidth("250px");
        int qtr = mapQuarter(selectedQuarter);

        selectedQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item
                -> createSpan(getQuarterBudget(item, qtr))
        ))
                .setHeader("Cum. Budget (" + getQuarterLabel(qtr) + ")")
                .setWidth("150px");

        selectedQtrAColumn
                = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
                    Span span = createSpan(getQuarterActual(item, qtr));
                    span.getElement().getThemeList().add("badge");
                    return span;
                }))
                        .setHeader("Cum. Actual (" + getQuarterLabel(qtr) + ")")
                        .setWidth("150px");

        /*        qtr1Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> createSpan(item.getQtr1())))
        .setHeader("Qtr1").setWidth("150px");
        
        qtr1AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
        Span span = createSpan(item.getQtr1A());
        span.getElement().getThemeList().add("badge");
        return span;
        })).setHeader("Qtr1 Actual").setWidth("150px");
        
        qtr2Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> createSpan(item.getQtr2().add(item.getQtr1()))))
        .setHeader("Cum. Qtr2").setWidth("150px");
        
        qtr2AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
        Span span = createSpan(item.getQtr2A().add(item.getQtr1A()));
        span.getElement().getThemeList().add("badge");
        return span;
        })).setHeader("Cum. Qtr2 Actual").setWidth("150px");
        
        qtr3Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> createSpan(item.getQtr3().add(item.getQtr2()).add(item.getQtr1()))))
        .setHeader("Cum. Qtr3").setWidth("150px");
        
        qtr3AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
        Span span = createSpan(item.getQtr3A().add(item.getQtr2A()).add(item.getQtr1A()));
        span.getElement().getThemeList().add("badge");
        return span;
        })).setHeader("Cum. Qtr3 Actual").setWidth("150px");
        
        qtr4Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> createSpan(item.getQtr4().add(item.getQtr3()).add(item.getQtr2()).add(item.getQtr1()))))
        .setHeader("Cum. Qtr4").setWidth("150px");
        
        qtr4AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
        Span span = createSpan(item.getQtr4A().add(item.getQtr3A().add(item.getQtr2A()).add(item.getQtr1A())));
        span.getElement().getThemeList().add("badge");
        return span;
        })).setHeader("Cum. Qtr4 Actual").setWidth("150px");*/
        totalQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> createSpan(item.getTotal())))
                .setHeader("Total Budget").setWidth("150px");

        totalAQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            Span span = createSpan(item.getTotalA());
            span.getElement().getThemeList().add("badge");
            return span;
        })).setHeader("Total Actual").setWidth("150px");

        balanceColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            BigDecimal balance = nz(item.getTotal()).subtract(nz(getQuarterActual(item, qtr)));
            Span span = createSpan(balance);
            span.getStyle().set("font-weight", "600");
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                span.getStyle().set("color", "var(--lumo-error-text-color)");
            } else {
                span.getStyle().set("color", "var(--lumo-success-text-color)");
            }
            return span;
        })).setHeader("Variance").setWidth("150px");

        statusColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("130px")
                .setFlexGrow(0);

        codeColumn.setFrozen(true);
        descColumn.setFrozen(true);

        FooterRow footerRow = gridBudgetItemsQuarterlyGrid.appendFooterRow();

        footerRow.getCell(codeColumn).setComponent(footerText(""));
        footerRow.getCell(descColumn).setComponent(footerText("TOTAL"));
        footerRow.getCell(selectedQtrColumn).setComponent(footerText(""));
        footerRow.getCell(selectedQtrAColumn).setComponent(footerActualText(""));
        /*        footerRow.getCell(qtr1Column).setComponent(footerText(""));
        footerRow.getCell(qtr1AColumn).setComponent(footerActualText(""));
        footerRow.getCell(qtr2Column).setComponent(footerText(""));
        footerRow.getCell(qtr2AColumn).setComponent(footerActualText(""));
        footerRow.getCell(qtr3Column).setComponent(footerText(""));
        footerRow.getCell(qtr3AColumn).setComponent(footerActualText(""));
        footerRow.getCell(qtr4Column).setComponent(footerText(""));
        footerRow.getCell(qtr4AColumn).setComponent(footerActualText(""));*/
        footerRow.getCell(totalQtrColumn).setComponent(footerText(""));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(""));
        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(""));
        footerRow.getCell(statusColumn).setComponent(new Span(""));

        Button loadButton = new Button("Load Quarterly Performance", new Icon(VaadinIcon.REFRESH));
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Span info = new Span("Click to load or refresh quarterly figures.");
        info.getStyle().set("color", "var(--lumo-secondary-text-color)");

        HorizontalLayout topBar = new HorizontalLayout(loadButton, info);
        topBar.setWidthFull();
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.setPadding(false);
        topBar.setSpacing(true);

        loadButton.addClickListener(e -> {
            java.util.List<BudgetItemsActuals> items = budgetItemsService.findDistinctBudgetItemsesExp(bdgt, deptSections);
            gridBudgetItemsQuarterlyGrid.setItems(items);
            refreshFooter(
                    footerRow,
                    items,
                    selectedQtrColumn, selectedQtrAColumn,
                    totalQtrColumn, totalAQtrColumn,
                    balanceColumn, statusColumn, qtr
            );
            Notification.show("Quarterly performance loaded");
        });

        wrapper.add(topBar, gridBudgetItemsQuarterlyGrid);
        return wrapper;
    }

    private String getQuarterLabel(int qtr) {
        return switch (qtr) {
            case 1 ->
                "Q1";
            case 2 ->
                "Q2";
            case 3 ->
                "Q3";
            case 4 ->
                "Q4";
            default ->
                "";
        };
    }

    private int mapQuarter(String qtr) {
        if (qtr == null) {
            return 0;
        }

        return switch (qtr.toLowerCase()) {
            case "qtr1" ->
                1;
            case "qtr2" ->
                2;
            case "qtr3" ->
                3;
            case "qtr4" ->
                4;
            default ->
                0;
        };
    }

    private BigDecimal getQuarterBudget(BudgetItemsActuals item, int qtr) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        return switch (qtr) {
            case 1 ->
                nz(item.getQtr1());
            case 2 ->
                nz(item.getQtr1()).add(nz(item.getQtr2()));
            case 3 ->
                nz(item.getQtr1()).add(nz(item.getQtr2())).add(nz(item.getQtr3()));
            case 4 ->
                nz(item.getQtr1()).add(nz(item.getQtr2()))
                .add(nz(item.getQtr3()))
                .add(nz(item.getQtr4()));
            default ->
                BigDecimal.ZERO;
        };
    }

    private BigDecimal getQuarterActual(BudgetItemsActuals item, int qtr) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        return switch (qtr) {
            case 1 ->
                nz(item.getQtr1A());
            case 2 ->
                nz(item.getQtr1A()).add(nz(item.getQtr2A()));
            case 3 ->
                nz(item.getQtr1A()).add(nz(item.getQtr2A())).add(nz(item.getQtr3A()));
            case 4 ->
                nz(item.getQtr1A()).add(nz(item.getQtr2A()))
                .add(nz(item.getQtr3A()))
                .add(nz(item.getQtr4A()));
            default ->
                BigDecimal.ZERO;
        };
    }

    private void refreshQuarterlyFooter(
            FooterRow footerRow,
            java.util.List<BudgetItemsActuals> items,
            Grid.Column<BudgetItemsActuals> qtr1Column,
            Grid.Column<BudgetItemsActuals> qtr1AColumn,
            Grid.Column<BudgetItemsActuals> qtr2Column,
            Grid.Column<BudgetItemsActuals> qtr2AColumn,
            Grid.Column<BudgetItemsActuals> qtr3Column,
            Grid.Column<BudgetItemsActuals> qtr3AColumn,
            Grid.Column<BudgetItemsActuals> qtr4Column,
            Grid.Column<BudgetItemsActuals> qtr4AColumn,
            Grid.Column<BudgetItemsActuals> totalQtrColumn,
            Grid.Column<BudgetItemsActuals> totalAQtrColumn,
            Grid.Column<BudgetItemsActuals> balanceColumn,
            Grid.Column<BudgetItemsActuals> statusColumn
    ) {
        BigDecimal qtr1Total = sum(items, BudgetItemsActuals::getQtr1);
        BigDecimal qtr1ATotal = sum(items, BudgetItemsActuals::getQtr1A);
        BigDecimal qtr2Total = sum(items, BudgetItemsActuals::getQtr2);
        BigDecimal qtr2ATotal = sum(items, BudgetItemsActuals::getQtr2A);
        BigDecimal qtr3Total = sum(items, BudgetItemsActuals::getQtr3);
        BigDecimal qtr3ATotal = sum(items, BudgetItemsActuals::getQtr3A);
        BigDecimal qtr4Total = sum(items, BudgetItemsActuals::getQtr4);
        BigDecimal qtr4ATotal = sum(items, BudgetItemsActuals::getQtr4A);
        BigDecimal grandBudget = sum(items, BudgetItemsActuals::getTotal);
        BigDecimal grandActual = sum(items, BudgetItemsActuals::getTotalA);
        BigDecimal grandBalance = grandBudget.subtract(grandActual);

        footerRow.getCell(qtr1Column).setComponent(footerText(formatAmount(qtr1Total)));
        footerRow.getCell(qtr1AColumn).setComponent(footerActualText(formatAmount(qtr1ATotal)));

        footerRow.getCell(qtr2Column).setComponent(footerText(formatAmount(qtr2Total)));
        footerRow.getCell(qtr2AColumn).setComponent(footerActualText(formatAmount(qtr2ATotal)));

        footerRow.getCell(qtr3Column).setComponent(footerText(formatAmount(qtr3Total)));
        footerRow.getCell(qtr3AColumn).setComponent(footerActualText(formatAmount(qtr3ATotal)));

        footerRow.getCell(qtr4Column).setComponent(footerText(formatAmount(qtr4Total)));
        footerRow.getCell(qtr4AColumn).setComponent(footerActualText(formatAmount(qtr4ATotal)));

        footerRow.getCell(totalQtrColumn).setComponent(footerText(formatAmount(grandBudget)));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(formatAmount(grandActual)));

        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(formatAmount(grandBalance)));
        footerRow.getCell(statusColumn).setComponent(createFooterStatusBadge(grandBudget, grandActual, grandBalance));
    }

    private void refreshFooter(
            FooterRow footerRow,
            java.util.List<BudgetItemsActuals> items,
            Grid.Column<BudgetItemsActuals> selectedQtrColumn,
            Grid.Column<BudgetItemsActuals> selectedQtrAColumn,
            Grid.Column<BudgetItemsActuals> totalQtrColumn,
            Grid.Column<BudgetItemsActuals> totalAQtrColumn,
            Grid.Column<BudgetItemsActuals> balanceColumn,
            Grid.Column<BudgetItemsActuals> statusColumn,
            int qtr
    ) {
        BigDecimal qtr1Total = sum(items, BudgetItemsActuals::getQtr1);
        BigDecimal qtr1ATotal = sum(items, BudgetItemsActuals::getQtr1A);
        BigDecimal qtr2Total = sum(items, BudgetItemsActuals::getQtr2);
        BigDecimal qtr2ATotal = sum(items, BudgetItemsActuals::getQtr2A);
        BigDecimal qtr3Total = sum(items, BudgetItemsActuals::getQtr3);
        BigDecimal qtr3ATotal = sum(items, BudgetItemsActuals::getQtr3A);
        BigDecimal qtr4Total = sum(items, BudgetItemsActuals::getQtr4);
        BigDecimal qtr4ATotal = sum(items, BudgetItemsActuals::getQtr4A);
        BigDecimal grandBudget = sum(items, BudgetItemsActuals::getTotal);
        BigDecimal grandActual = sum(items, BudgetItemsActuals::getTotalA);
        BigDecimal grandBalance = grandBudget.subtract(grandActual);

        BigDecimal selectdQtrTotal = BigDecimal.ZERO;
        BigDecimal selectdQtrATotal = BigDecimal.ZERO;

        switch (qtr) {
            case 1:
                selectdQtrTotal = qtr1Total;
                selectdQtrATotal = qtr1ATotal;
                break;
            case 2:
                selectdQtrTotal = qtr1Total.add(qtr2Total);
                selectdQtrATotal = qtr1ATotal.add(qtr2ATotal);
                break;
            case 3:
                selectdQtrTotal = qtr1Total.add(qtr2Total).add(qtr3Total);
                selectdQtrATotal = qtr1ATotal.add(qtr2ATotal).add(qtr3ATotal);
                break;
            case 4:
                selectdQtrTotal = qtr1Total.add(qtr2Total).add(qtr3Total).add(qtr4Total);
                selectdQtrATotal = qtr1ATotal.add(qtr2ATotal).add(qtr3ATotal).add(qtr4ATotal);
                break;
            default:
                break;
        }

        footerRow.getCell(selectedQtrColumn).setComponent(footerText(formatAmount(selectdQtrTotal)));
        footerRow.getCell(selectedQtrAColumn).setComponent(footerActualText(formatAmount(selectdQtrATotal)));

        footerRow.getCell(totalQtrColumn).setComponent(footerText(formatAmount(grandBudget)));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(formatAmount(grandActual)));

        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(formatAmount(grandBalance)));
        footerRow.getCell(statusColumn).setComponent(createFooterStatusBadge(grandBudget, selectdQtrATotal, grandBalance));
    }

    private Div budgetCoaQtrView(Budget bdgt, Set<UrcDeptSectionAnlDimbgt> deptSections) {
        Div div = new Div();
        div.setSizeFull();
        deptSections.forEach(e -> {
            System.out.println(e.getANL_CODE() + " " + e.getNAME());
        });
        Grid<BudgetItemsActuals> gridBudgetItemsQuarterlyGrid = new Grid<>(BudgetItemsActuals.class, false);
        gridBudgetItemsQuarterlyGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        java.util.List<BudgetItemsActuals> items = budgetItemsService.findDistinctBudgetItemsesExp(bdgt, deptSections);
        BigDecimal grandBudget = sum(items, BudgetItemsActuals::getTotal);
        BigDecimal grandActual = sum(items, BudgetItemsActuals::getTotalA);

        BigDecimal grandBalance = grandBudget.subtract(grandActual);

        Grid.Column<BudgetItemsActuals> codeColumn;
        Grid.Column<BudgetItemsActuals> descColumn;
        Grid.Column<BudgetItemsActuals> qtr1Column;
        Grid.Column<BudgetItemsActuals> qtr1AColumn;
        Grid.Column<BudgetItemsActuals> qtr2Column;
        Grid.Column<BudgetItemsActuals> qtr2AColumn;
        Grid.Column<BudgetItemsActuals> qtr3Column;
        Grid.Column<BudgetItemsActuals> qtr3AColumn;
        Grid.Column<BudgetItemsActuals> qtr4Column;
        Grid.Column<BudgetItemsActuals> qtr4AColumn;
        Grid.Column<BudgetItemsActuals> totalQtrColumn;
        Grid.Column<BudgetItemsActuals> totalAQtrColumn;
        Grid.Column<BudgetItemsActuals> balanceColumn;
        Grid.Column<BudgetItemsActuals> statusColumn;

        codeColumn = gridBudgetItemsQuarterlyGrid.addColumn(budgetItem -> {
            COA coacode = budgetItem.getCoacode();
            com.vaadin.flow.component.Text label = new com.vaadin.flow.component.Text(coacode != null ? coacode.getCode() : "");
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
        descColumn = gridBudgetItemsQuarterlyGrid.addColumn(BudgetItemsActuals::getItem).setHeader("Description").setWidth("250px");
        //gridBudgetItems.addColumn(BudgetItemsActuals::getJul).setHeader("July");

        qtr1Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr1();
            Span span = createSpan(value);

            return span;

        })).setHeader("Qtr1").setWidth("150px");
        qtr1AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr1A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr1 Actual").setWidth("150px");
// Apply the orange-column class to the cells of the "Jul Actuals" column
        qtr1AColumn.setClassNameGenerator(item -> {
            // Check if the value is not null and greater than zero to apply the class
            if (item != null && item.getJulA() != null && item.getJulA().compareTo(BigDecimal.ZERO) > 0) {
                return "orange-column";
            }
            return "orange-column"; // No class applied if condition is not met
        });

        qtr2Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr2();
            Span span = createSpan(value);

            return span;

        })).setHeader("Qtr2").setWidth("150px");

        qtr2AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr2A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr2 Actual").setWidth("150px");
        qtr3Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr3();
            Span span = createSpan(value);

            return span;

        })).setHeader("Qtr3").setWidth("150px");
        qtr3AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr3A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr3 Actual").setWidth("150px");
        qtr4Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr4();
            Span span = createSpan(value);

            return span;

        })).setHeader("Qtr4").setWidth("150px");
        qtr4AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr4A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr4 Actual").setWidth("150px");
        totalQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotal();
            Span span = createSpan(value);

            return span;

        })).setHeader("Total").setWidth("150px");

        totalAQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotalA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Total Actual").setWidth("150px");

        balanceColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> {
            BigDecimal budget = item.getTotal();
            BigDecimal actual = item.getTotalA();
            BigDecimal balance = budget.subtract(actual);

            Span span = createSpan(balance);
            span.getStyle().set("font-weight", "600");

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                span.getStyle().set("color", "var(--lumo-error-text-color)");
            } else {
                span.getStyle().set("color", "var(--lumo-success-text-color)");
            }
            return span;
        })).setHeader("Variance").setWidth("150px");

        statusColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(item -> createStatusBadge(item)))
                .setHeader("Status")
                .setWidth("130px")
                .setFlexGrow(0);

        codeColumn.setFrozen(true);
        descColumn.setFrozen(true);

        gridBudgetItemsQuarterlyGrid.setItems(items);

        gridBudgetItemsQuarterlyGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        //gridBudgetItemsQuarterlyGrid.setItems(budgetItemsService.findDistinctBudgetItemsesExp(budget, deptSections));

        // ===== Footer totals =====
        FooterRow footerRow = gridBudgetItemsQuarterlyGrid.appendFooterRow();

        footerRow.getCell(codeColumn).setComponent(footerText(""));
        footerRow.getCell(descColumn).setComponent(footerText("TOTAL"));

        footerRow.getCell(qtr1Column)
                .setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr1))));
        footerRow.getCell(qtr1AColumn)
                .setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr1A))));

        footerRow.getCell(qtr2Column)
                .setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr2))));
        footerRow.getCell(qtr2AColumn)
                .setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr2A))));

        footerRow.getCell(qtr3Column)
                .setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr3))));
        footerRow.getCell(qtr3AColumn)
                .setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr3A))));

        footerRow.getCell(qtr4Column)
                .setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr4))));
        footerRow.getCell(qtr4AColumn)
                .setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr4A))));

        footerRow.getCell(totalQtrColumn)
                .setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getTotal))));
        footerRow.getCell(totalAQtrColumn)
                .setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getTotalA))));

        footerRow.getCell(codeColumn).setComponent(footerText(""));
        footerRow.getCell(descColumn).setComponent(footerText("TOTAL"));

        footerRow.getCell(qtr1Column).setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr1))));
        footerRow.getCell(qtr1AColumn).setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr1A))));

        footerRow.getCell(qtr2Column).setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr2))));
        footerRow.getCell(qtr2AColumn).setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr2A))));

        footerRow.getCell(qtr3Column).setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr3))));
        footerRow.getCell(qtr3AColumn).setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr3A))));

        footerRow.getCell(qtr4Column).setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getQtr4))));
        footerRow.getCell(qtr4AColumn).setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getQtr4A))));

        footerRow.getCell(totalQtrColumn).setComponent(footerText(formatAmount(sum(items, BudgetItemsActuals::getTotal))));
        footerRow.getCell(totalAQtrColumn).setComponent(footerActualText(formatAmount(sum(items, BudgetItemsActuals::getTotalA))));

        footerRow.getCell(balanceColumn).setComponent(footerBalanceText(formatAmount(grandBalance)));
        footerRow.getCell(statusColumn).setComponent(createFooterStatusBadge(grandBudget, grandActual, grandBalance));

        div.add(gridBudgetItemsQuarterlyGrid);
        return div;
    }

    private Component createOverallGeneralPhysicalPerformanceContent(Budget budget) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        layout.setSpacing(true);

        String scopeCode = "GENERAL";
        String scopeName = "All Departments";

        Span title = new Span("General Budget Physical Performance Narrative");
        title.getStyle()
                .set("font-weight", "700")
                .set("font-size", "1rem");

        Span info = new Span("Scope: All Departments");
        info.getStyle().set("color", "var(--lumo-secondary-text-color)");

        ComboBox<String> quarterBox = new ComboBox<>("Quarter");
        quarterBox.setItems("qtr1", "qtr2", "qtr3", "qtr4");
        quarterBox.setValue("qtr1");
        quarterBox.setWidth("200px");

        QuillEditorField editor = new QuillEditorField();
        editor.setWidth("100%");
        editor.setHeight("300px");
        editor.addClassName("rich-editor");
        editor.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        editor.setPlaceholder("Write the overall general budget physical performance narrative for the selected quarter...");

        String template = """
        <h3>Summary</h3>
        <p></p>

        <h3>Key Achievements</h3>
        <ul><li></li></ul>

        <h3>Challenges</h3>
        <ul><li></li></ul>

        <h3>Way Forward</h3>
        <ul><li></li></ul>
        """;

        String initialHtml = overallGeneralPhysicalPerformanceService.getQuarterHtml(scopeCode, "qtr1", budget);
        String initialContent = (initialHtml == null || initialHtml.isBlank()) ? template : initialHtml;

        editor.setValue(initialContent);

        quarterBox.addValueChangeListener(event -> {
            String quarter = event.getValue();
            String html = overallGeneralPhysicalPerformanceService.getQuarterHtml(scopeCode, quarter, budget);
            String content = (html == null || html.isBlank()) ? template : html;

            editor.setValue(content);
        });

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(e -> {
            editor.getElement().executeJs("return this.value").then(String.class, html -> {
                String content = (html == null || html.isBlank()) ? "" : html;
                String quarter = quarterBox.getValue();

                overallGeneralPhysicalPerformanceService.saveQuarter(
                        scopeCode,
                        scopeName,
                        quarter,
                        content,
                        budget
                );

                Notification.show("Overall narrative saved");
            });
        });

        HorizontalLayout actions = new HorizontalLayout(quarterBox, saveButton);
        actions.setAlignItems(FlexComponent.Alignment.END);
        actions.setSpacing(true);
        actions.setPadding(false);

        layout.add(title, info, actions, editor);
        return layout;
    }

    private Span createSpan(BigDecimal value) {
        Span span;
        if (value != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            span = new Span(decimalFormat.format(value));
        } else {
            span = new Span("-");
        }
        return span;
    }

    private BigDecimal sum(java.util.List<BudgetItemsActuals> items,
            java.util.function.Function<BudgetItemsActuals, BigDecimal> extractor) {
        return items.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    private Component footerText(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "700")
                .set("display", "block")
                .set("text-align", "left")
                .set("width", "100%");
        return span;
    }

    private Component footerActualText(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "700")
                .set("color", "#1565c0")
                .set("display", "block")
                .set("text-align", "left")
                .set("width", "100%");
        return span;
    }

    private Component createStatusBadge(BudgetItemsActuals item) {
        BigDecimal total = nz(item.getTotal());
        BigDecimal actual = nz(item.getTotalA());
        BigDecimal balance = total.subtract(actual);

        Span badge = new Span();
        badge.getStyle()
                .set("font-weight", "700")
                .set("padding", "4px 10px")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("display", "inline-block");

        if (total.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            badge.setText("UNBUDGETED");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            badge.setText("OVER");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else {
            badge.setText("FINE");
            badge.getStyle()
                    .set("background-color", "var(--lumo-success-color-10pct)")
                    .set("color", "var(--lumo-success-text-color)");
        }

        return badge;
    }

    private Component createFooterStatusBadge(BigDecimal total, BigDecimal actual, BigDecimal balance) {
        Span badge = new Span();
        badge.getStyle()
                .set("font-weight", "700")
                .set("padding", "4px 10px")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("display", "inline-block");

        if (total.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            badge.setText("UNBUDGETED");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            badge.setText("OVER");
            badge.getStyle()
                    .set("background-color", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-text-color)");
        } else {
            badge.setText("FINE");
            badge.getStyle()
                    .set("background-color", "var(--lumo-success-color-10pct)")
                    .set("color", "var(--lumo-success-text-color)");
        }

        return badge;
    }

    private Component footerBalanceText(String text) {
        Span span = new Span(text);
        span.getStyle()
                .set("font-weight", "700")
                .set("display", "block")
                .set("text-align", "right")
                .set("width", "100%");
        return span;
    }

}
