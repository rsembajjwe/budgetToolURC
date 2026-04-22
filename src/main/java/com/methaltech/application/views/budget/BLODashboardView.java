package com.methaltech.application.views.budget;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetSummary;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.data.bgtool.service.BudgetApprovalsService;
import com.methaltech.application.data.bgtool.service.BudgetControlService;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.BudgetApprovals;
import com.methaltech.application.data.entity.bgtool.SectionBudget;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.actual.utilityActuals;
import com.methaltech.application.views.approvals.ApprovalDashboardCards;
import com.methaltech.application.views.approvals.ApprovalRequestForm;
import com.methaltech.application.views.approvals.ApprovalWorkflowGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.methaltech.application.data.bgtool.service.DepartmentGeneralPhysicalPerformanceService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.views.structure.DepartmentOverview;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;

@Route(value = "blo-dashboard", layout = MainLayout.class)
@PageTitle("Budget Liaison Officer Dashboard")
@RouteAlias(value = "", layout = MainLayout.class)
@SpringComponent
@UIScope
@RolesAllowed({"BLO", "ADMIN"})
public class BLODashboardView extends VerticalLayout {

    private final BudgetService budgetService;
    private final BudgetApprovalsService approvalsService;
    private final BudgetControlService controlService;
    private final UserService userService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final BudgetItemsService budgetItemsService;
    private AuthenticatedUser authenticatedUser;
    private final NumberFormat currencyFormat;

    private ComboBox<DepartmentBudget> departmentSelector;
    private VerticalLayout contentContainer;
    private DepartmentBudget selectedDepartment;
    private ComboBox<Budget> fiscalYear = new ComboBox<>();
    private Budget selectedBudget;

    // Navigation tabs
    private Tabs navigationTabs;
    private Tab budgetTab;
    private Tab financialPerformanceTab;

    // Approval components
    private ApprovalDashboardCards approvalCards;
    private ApprovalWorkflowGrid approvalGrid;
    private ApprovalRequestForm requestForm;

    // Current user context
    private String currentUserRole = "BLO";
    private String currentUserName = "BLO User";
    private User user;
    utilityActuals utils;
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final DepartmentGeneralPhysicalPerformanceService departmentGeneralPhysicalPerformanceService;

    private ComboBox<String> quarterSelector;
    private String selectedQuarter;
    Span subtitlePerf = new Span();

    @Autowired
    public BLODashboardView(BudgetService budgetService, BudgetApprovalsService approvalsService, BudgetControlService controlService,
            AuthenticatedUser authenticatedUser, UserService userService, DeptSectionMergerService sampleDeptSectionMergerService,
            BudgetItemsService budgetItemsService, CoaService sampleCoaService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            SALFLDGService sampleSALFLDGService, Urc_ActivitiesService sampleUrc_ActivitiesService, DepartmentGeneralPhysicalPerformanceService departmentGeneralPhysicalPerformanceService) {
        this.budgetService = budgetService;
        this.approvalsService = approvalsService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.controlService = controlService;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.budgetItemsService = budgetItemsService;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.departmentGeneralPhysicalPerformanceService = departmentGeneralPhysicalPerformanceService;

        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        addClassName("blo-dashboard");
        setUser();

        loadFiscalYearData();
        createHeader();
        createNavigationTabs();
        createMainContent();
        loadInitialData();
    }

    private void setUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
            currentUserName = user.getLastName() + " " + user.getFirstName();
            currentUserRole = user.getRoles().toString();
        }
    }

    private void createHeader() {
        Div header = new Div();
        header.addClassName("blo-dashboard-header");
        header.setWidthFull();

        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        headerContent.setPadding(false);
        headerContent.setSpacing(false);

        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSpacing(false);
        leftSide.setPadding(false);
        leftSide.setFlexGrow(1);

        H1 title = new H1("Budget Liaison Officer Dashboard");
        title.addClassName("blo-title");

        Span subtitle = new Span("Comprehensive Budget, Approval & Performance Management");
        subtitle.addClassName("blo-subtitle");

        HorizontalLayout selectorsLayout = new HorizontalLayout();
        selectorsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        selectorsLayout.setSpacing(true);
        selectorsLayout.setPadding(false);
        selectorsLayout.addClassName("blo-selectors");

        Span selectorYearLabel = new Span("Fiscal Year:");
        selectorYearLabel.addClassName("selector-label");
        fiscalYear.addClassName("fiscal-year-selector");
        fiscalYear.setPlaceholder("Fiscal Year");

        fiscalYear.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedBudget = e.getValue();
                loadInitialData();
                refreshCurrentTab();
            }
        });

        Span selectorLabel = new Span("Department:");
        selectorLabel.addClassName("selector-label");

        departmentSelector = new ComboBox<>();
        departmentSelector.setPlaceholder("Select your department...");
        departmentSelector.setItemLabelGenerator(DepartmentBudget::getDepartmentName);
        departmentSelector.addClassName("department-selector");
        departmentSelector.setWidthFull();
        departmentSelector.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedDepartment = e.getValue();
                refreshCurrentTab();
            }
        });

        Span quarterLabel = new Span("Quarter:");
        quarterLabel.addClassName("selector-label");

        quarterSelector = new ComboBox<>();
        quarterSelector.setItems("qtr1", "qtr2", "qtr3", "qtr4");
        quarterSelector.setPlaceholder("Select Quarter");
        quarterSelector.setClearButtonVisible(true);
        quarterSelector.addClassName("quarter-selector");
        quarterSelector.addValueChangeListener(e -> {
            selectedQuarter = e.getValue();
            refreshCurrentTab();
        });

        selectorsLayout.add(
                selectorYearLabel, fiscalYear,
                selectorLabel, departmentSelector,
                quarterLabel, quarterSelector
        );
        selectorsLayout.setFlexGrow(1, departmentSelector);

        leftSide.add(title, subtitle, selectorsLayout);

        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);
        rightSide.addClassName("blo-actions");

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClassName("blo-refresh-button");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        refreshButton.addClickListener(e -> {
            refreshCurrentTab();
            showNotification("Data refreshed successfully", NotificationVariant.LUMO_SUCCESS);
        });

        Button exportButton = new Button("Export PDF", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addClassName("blo-export-button");
        exportButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportButton.addClickListener(e -> generateBLOPDFReport());

        Button exportWordButton = new Button("Export Word", new Icon(VaadinIcon.DOWNLOAD));
        exportWordButton.addClassName("blo-export-button");
        exportWordButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportWordButton.addClickListener(e -> generateBLOWordReport());

        Button alertsButton = new Button("Alerts", new Icon(VaadinIcon.BELL));
        alertsButton.addClassName("blo-alerts-button");
        alertsButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        alertsButton.addClickListener(e
                -> showNotification("Budget alerts panel coming soon", NotificationVariant.LUMO_CONTRAST)
        );

        rightSide.add(refreshButton, exportButton, exportWordButton, alertsButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void createNavigationTabs() {
        navigationTabs = new Tabs();
        navigationTabs.addClassName("blo-navigation-tabs");
        navigationTabs.setWidthFull();

        budgetTab = new Tab(new Icon(VaadinIcon.DASHBOARD), new Span("Budget Overview"));
        //physicalPerformanceTab = new Tab(new Icon(VaadinIcon.CLIPBOARD_TEXT), new Span("Physical Performance"));
        financialPerformanceTab = new Tab(new Icon(VaadinIcon.CHART_LINE), new Span("Financial Performance"));
        //generalPhysicalPerformanceTab = new Tab(new Icon(VaadinIcon.CHART_GRID), new Span("General Physical Performance"));

        budgetTab.addClassName("nav-tab");
        //physicalPerformanceTab.addClassName("nav-tab");
        financialPerformanceTab.addClassName("nav-tab");
        //generalPhysicalPerformanceTab.addClassName("nav-tab");

        navigationTabs.add(
                budgetTab,
                financialPerformanceTab
        );
        navigationTabs.setSelectedTab(budgetTab);

        navigationTabs.addSelectedChangeListener(e -> {
            Tab selectedTab = e.getSelectedTab();

            if (selectedTab == budgetTab) {
                showBudgetOverview();
            } else if (selectedTab == financialPerformanceTab) {
                showFinancialPerformanceView();
            }
        });

        add(navigationTabs);
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

    private void createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("blo-main-content");

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.addClassName("blo-content-container");

        // Initial empty state
        createEmptyState();

        mainContent.add(contentContainer);
        add(mainContent);
    }

    private void createEmptyState() {
        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyState.addClassName("blo-empty-state");
        emptyState.setHeightFull();

        Icon departmentIcon = new Icon(VaadinIcon.BUILDING);
        departmentIcon.addClassName("empty-state-icon");

        H2 emptyTitle = new H2("Select Your Department");
        emptyTitle.addClassName("empty-state-title");

        Span emptyMessage = new Span("Choose a department from the dropdown above to view detailed budget information and manage approvals.");
        emptyMessage.addClassName("empty-state-message");

        emptyState.add(departmentIcon, emptyTitle, emptyMessage);
        contentContainer.add(emptyState);
    }

    private void loadInitialData() {
        if (selectedBudget == null) {
            return;
        }

        try {
            BudgetSummary budgetSummary = budgetService.getBudgetSummary(selectedBudget);

            List<DepartmentBudget> departments = budgetSummary.getDepartmentBudgets();
            List<DepartmentBudget> departmentsFiltered = new ArrayList();
            //System.out.println(departments);
            if (user.getRoles().contains(Role.BLO) && !user.getRoles().contains(Role.ADMIN)) {

                Set<UrcDeptSectionAnlDimbgt> deptsection = user.getDeptsection();
                List<String> deptcodes = new ArrayList();
                for (UrcDeptSectionAnlDimbgt dep : deptsection) {
                    // String getDeptCodeBySectionCode = sampleDeptSectionMergerService.getDeptCodeBySectionCode(dep.getANL_CODE());
                    //deptcodes.add(getDeptCodeBySectionCode);

                    departments.forEach(r -> {
                        if (r.getDepartmentCode().trim().equalsIgnoreCase(dep.getANL_CODE().trim()) || r.getSections().contains(dep)) {
                            departmentsFiltered.add(r);
                        }
                    });

                }
                departments = departmentsFiltered;
                departments = departments.stream().distinct().collect(Collectors.toList());

            } else if (user.getRoles().contains(Role.ADMIN)) {
                departments = budgetSummary.getDepartmentBudgets();
            }

            departmentSelector.setItems(departments);

            // Auto-select first department if available
            if (!departments.isEmpty()) {
                departmentSelector.setValue(departments.get(0));
            }
        } catch (Exception e) {
            showNotification("Error loading departments: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showBudgetOverview() {
        if (selectedDepartment == null) {
            createEmptyState();
            return;
        }

        try {
            contentContainer.removeAll();
            //createDepartmentOverview();

            HorizontalLayout dashboardGrid = new HorizontalLayout();
            dashboardGrid.setWidthFull();
            dashboardGrid.setSpacing(true);
            dashboardGrid.setPadding(false);
            dashboardGrid.addClassName("blo-dashboard-grid");

            VerticalLayout leftColumn = new VerticalLayout();
            leftColumn.setSpacing(true);
            leftColumn.setPadding(false);
            leftColumn.addClassName("blo-left-column");

            DepartmentSectionCards sectionCards
                    = new DepartmentSectionCards(
                            selectedDepartment,
                            budgetService,
                            selectedBudget,
                            sampleCoaService,
                            sampleUrcDeptSectionAnlDimbgtService,
                            sampleSALFLDGService,
                            selectedQuarter
                    );
            sectionCards.addClassName("blo-section-cards");

            BudgetControlPanel controlPanel
                    = new BudgetControlPanel(selectedDepartment, controlService, selectedBudget, user);
            controlPanel.addClassName("blo-control-panel");

            leftColumn.add(sectionCards, controlPanel);

            VerticalLayout rightColumn = new VerticalLayout();
            rightColumn.setSpacing(true);
            rightColumn.setPadding(false);
            rightColumn.addClassName("blo-right-column");

            SectionBudgetChart budgetChart = new SectionBudgetChart(selectedDepartment);
            budgetChart.addClassName("blo-budget-chart");

            BudgetAlerts budgetAlerts = new BudgetAlerts(selectedDepartment);
            budgetAlerts.addClassName("blo-budget-alerts");

            rightColumn.add(budgetChart, budgetAlerts);

            dashboardGrid.add(leftColumn, rightColumn);
            dashboardGrid.setFlexGrow(2, leftColumn);
            dashboardGrid.setFlexGrow(1, rightColumn);

            contentContainer.add(dashboardGrid);

        } catch (Exception e) {
            e.printStackTrace(); // ✅ find exact offender
            showNotification("Error loading department data: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showNewRequestForm() {
        if (selectedBudget == null) {
            showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            BudgetSummary budgetSummary = budgetService.getBudgetSummary(selectedBudget);
            List<DepartmentBudget> departments = budgetSummary.getDepartmentBudgets();

            requestForm = new ApprovalRequestForm(departments, currentUserName, approvalsService);
            requestForm.addClassName("blo-request-form");

            requestForm.addSaveListener(e -> {
                showNotification("Budget request submitted successfully", NotificationVariant.LUMO_SUCCESS);
                refreshCurrentTab();
            });

            requestForm.open();
        } catch (Exception e) {
            showNotification("Error loading form: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshCurrentTab() {
        Tab selectedTab = navigationTabs.getSelectedTab();

        if (selectedTab == budgetTab) {
            showBudgetOverview();
        } else if (selectedTab == financialPerformanceTab) {
            showFinancialPerformanceView();
        }
    }

    private void showPhysicalPerformanceView() {
        if (selectedDepartment == null) {
            createEmptyState();
            return;
        }

        contentContainer.removeAll();
        createDepartmentOverview();

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.addClassName("blo-tab-view");

        H3 title = new H3("Physical Performance");
        title.addClassName("section-title");

        Span subtitle = new Span(
                "Department: " + selectedDepartment.getDepartmentName()
                + " | Fiscal Year: " + (selectedBudget != null ? selectedBudget.getFinancialYear() : "N/A")
        );
        subtitle.addClassName("blo-tab-subtitle");

        Grid<SectionBudget> grid = new Grid<>(SectionBudget.class, false);
        grid.setWidthFull();
        grid.addClassName("blo-performance-grid");

        List<SectionBudget> sections = budgetService.getDepartmentSections(
                selectedDepartment.getDepartmentCode(),
                selectedBudget
        );

        grid.addColumn(SectionBudget::getSectionCode).setHeader("Section Code").setAutoWidth(true);
        grid.addColumn(SectionBudget::getSectionName).setHeader("Section Name").setFlexGrow(1);
        grid.addColumn(s -> derivePhysicalStatus(s)).setHeader("Physical Status").setAutoWidth(true);
        grid.addColumn(s -> derivePhysicalRemarks(s)).setHeader("Remarks").setFlexGrow(1);

        grid.setItems(sections);

        wrapper.add(title, subtitle, grid);
        contentContainer.add(wrapper);
    }

    private void showFinancialPerformanceView() {
        if (selectedDepartment == null) {
            createEmptyState();
            return;
        }

        contentContainer.removeAll();

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.addClassName("blo-tab-view");

        H3 title = new H3("Financial Performance");
        title.addClassName("section-title");

        subtitlePerf = new Span(
                "Department: " + selectedDepartment.getDepartmentName()
                + " | Fiscal Year: " + (selectedBudget != null ? selectedBudget.getFinancialYear() : "N/A")
                + " | Quarter: " + (selectedQuarter != null ? selectedQuarter.toUpperCase() : "FULL YEAR")
        );
        subtitlePerf.addClassName("blo-tab-subtitle");

        List<DepartmentBudget> departmentBudgets = new ArrayList();
        departmentBudgets.add(selectedDepartment);

        DepartmentOverview departmentOverview = new DepartmentOverview(
                departmentBudgets,
                sampleCoaService,
                sampleUrcDeptSectionAnlDimbgtService,
                sampleSALFLDGService,
                sampleDeptSectionMergerService,
                selectedBudget,
                budgetItemsService,
                sampleUrc_ActivitiesService,
                departmentGeneralPhysicalPerformanceService,
                selectedQuarter
        );

        wrapper.add(departmentOverview);
        contentContainer.add(wrapper);
    }

    private void showGeneralPhysicalPerformanceView() {
        if (selectedDepartment == null) {
            createEmptyState();
            return;
        }

        contentContainer.removeAll();
        createDepartmentOverview();

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.addClassName("blo-tab-view");

        H3 title = new H3("General Physical Performance");
        title.addClassName("section-title");

        Span subtitle = new Span(
                "Department-wide physical progress summary for "
                + selectedDepartment.getDepartmentName()
        );
        subtitle.addClassName("blo-tab-subtitle");

        List<SectionBudget> sections = budgetService.getDepartmentSections(
                selectedDepartment.getDepartmentCode(),
                selectedBudget
        );

        BigDecimal totalAllocated = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;
        int completedLike = 0;
        int inProgress = 0;
        int notStarted = 0;

        for (SectionBudget section : sections) {
            totalAllocated = totalAllocated.add(nvl(section.getAllocatedBudget()));
            totalSpent = totalSpent.add(nvl(section.getSpentAmount()).abs());

            String status = derivePhysicalStatus(section);
            if ("Completed / Overrun".equals(status) || "Advanced".equals(status)) {
                completedLike++;
            } else if ("In Progress".equals(status) || "Started".equals(status)) {
                inProgress++;
            } else {
                notStarted++;
            }
        }

        HorizontalLayout metrics = new HorizontalLayout();
        metrics.setWidthFull();
        metrics.setSpacing(true);
        metrics.setPadding(false);
        metrics.addClassName("blo-general-performance-metrics");

        VerticalLayout allocatedCard = createBudgetSummaryItem("Total Budget", totalAllocated, "summary-budget");
        VerticalLayout spentCard = createBudgetSummaryItem("Total Spent", totalSpent, "summary-spent");
        VerticalLayout progressCard = createTextMetricCard("Sections In Progress", String.valueOf(inProgress), "summary-available");
        VerticalLayout completedCard = createTextMetricCard("Advanced / Completed", String.valueOf(completedLike), "summary-budget");
        VerticalLayout pendingCard = createTextMetricCard("Not Started", String.valueOf(notStarted), "summary-spent");

        metrics.add(allocatedCard, spentCard, progressCard, completedCard, pendingCard);
        metrics.setFlexGrow(1, allocatedCard);
        metrics.setFlexGrow(1, spentCard);
        metrics.setFlexGrow(1, progressCard);
        metrics.setFlexGrow(1, completedCard);
        metrics.setFlexGrow(1, pendingCard);

        wrapper.add(title, subtitle, metrics);
        contentContainer.add(wrapper);
    }

    private void createDepartmentOverview() {
        Div overviewCard = new Div();
        overviewCard.addClassName("blo-department-overview");
        overviewCard.setWidthFull();

        HorizontalLayout overviewContent = new HorizontalLayout();
        overviewContent.setWidthFull();
        overviewContent.setPadding(false);
        overviewContent.setSpacing(true);
        overviewContent.setAlignItems(FlexComponent.Alignment.CENTER);
        overviewContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        overviewContent.addClassName("blo-department-overview-content");

        // LEFT SIDE: Department info
        HorizontalLayout deptInfo = new HorizontalLayout();
        deptInfo.setPadding(false);
        deptInfo.setSpacing(true);
        deptInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        deptInfo.addClassName("blo-dept-info");
        deptInfo.getStyle().set("min-width", "0");

        Div colorIndicator = new Div();
        colorIndicator.addClassName("dept-color-indicator");
        colorIndicator.getStyle().set("background-color",
                selectedDepartment.getColor() != null ? selectedDepartment.getColor() : "#7c3aed");

        VerticalLayout deptDetails = new VerticalLayout();
        deptDetails.setSpacing(false);
        deptDetails.setPadding(false);
        deptDetails.addClassName("blo-dept-details");

        H2 deptName = new H2(selectedDepartment.getDepartmentName());
        deptName.addClassName("dept-overview-name");

        Span deptCode = new Span(
                "Code: " + selectedDepartment.getDepartmentCode()
                + " | Sections: " + selectedDepartment.getSectionCount()
        );
        deptCode.addClassName("dept-overview-details");

        deptDetails.add(deptName, deptCode);
        deptInfo.add(colorIndicator, deptDetails);

        // RIGHT SIDE: Budget summary cards
        HorizontalLayout budgetSummary = new HorizontalLayout();
        budgetSummary.setWidthFull(); // 🔥 critical
        budgetSummary.setSpacing(true);
        budgetSummary.setPadding(false);
        budgetSummary.setAlignItems(FlexComponent.Alignment.STRETCH);
        budgetSummary.addClassName("dept-budget-summary");
        budgetSummary.getStyle().set("min-width", "0");

        VerticalLayout totalSpan = createBudgetSummaryItem(
                "Total Budget",
                selectedDepartment.getTotalBudget(),
                "summary-budget"
        );

        VerticalLayout totalSpentSpan = createBudgetSummaryItem(
                "Total Spent",
                getSelectedSpentAmount(selectedDepartment),
                "summary-spent"
        );
        VerticalLayout availableSpan = createBudgetSummaryItem(
                "Available",
                getSelectedAvailableAmount(selectedDepartment),
                "summary-available"
        );

        totalSpan.addClassName("summary-card");
        totalSpentSpan.addClassName("summary-card");
        availableSpan.addClassName("summary-card");

        totalSpentSpan.addDoubleClickListener(e -> {
            String deptcode = selectedDepartment.getDepartmentCode();
            Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);

            utils = new utilityActuals(
                    selectedBudget,
                    sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService,
                    sectionCodes
            );
            utils.createTransactionsDialog2(overviewContent);
        });

        budgetSummary.add(totalSpan, totalSpentSpan, availableSpan);

// 🔥 make them equally stretch
        budgetSummary.setFlexGrow(1, totalSpan);
        budgetSummary.setFlexGrow(1, totalSpentSpan);
        budgetSummary.setFlexGrow(1, availableSpan);

// optional but safer
        budgetSummary.setWidthFull();
        totalSpan.setWidthFull();
        totalSpentSpan.setWidthFull();
        availableSpan.setWidthFull();

        overviewContent.add(deptInfo, budgetSummary);
        overviewContent.setFlexGrow(0, deptInfo);
        overviewContent.setFlexGrow(2, budgetSummary);

        overviewCard.add(overviewContent);
        contentContainer.add(overviewCard);
    }

    private BigDecimal getSelectedSpentAmount(DepartmentBudget department) {
        if (department == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal q1 = nvl(department.getCumQtr1Actual()).abs();
        BigDecimal q2 = nvl(department.getCumQtr2Actual()).abs();
        BigDecimal q3 = nvl(department.getCumQtr3Actual()).abs();
        BigDecimal q4 = nvl(department.getCumQtr4Actual()).abs();

        if (selectedQuarter == null || selectedQuarter.isBlank()) {
            return nvl(department.getTotalSpent()).abs();
        }

        return switch (selectedQuarter.toLowerCase()) {
            case "qtr1" ->
                q1;
            case "qtr2" ->
                q1.add(q2);
            case "qtr3" ->
                q1.add(q2).add(q3);
            case "qtr4" ->
                q1.add(q2).add(q3).add(q4);
            default ->
                nvl(department.getTotalSpent()).abs();
        };
    }

    private BigDecimal getSelectedAvailableAmount(DepartmentBudget department) {
        if (department == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalBudget = nvl(department.getTotalBudget());
        BigDecimal spent = getSelectedSpentAmount(department);

        return totalBudget.subtract(spent);
    }

    private VerticalLayout createBudgetSummaryItem(String label, BigDecimal amount, String className) {
        BigDecimal safeAmount = amount != null ? amount : BigDecimal.ZERO;

        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassNames("blo-summary-item", className);
        item.setAlignItems(FlexComponent.Alignment.CENTER);
        item.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        item.setWidthFull();

        Span labelSpan = new Span(label);
        labelSpan.addClassName("summary-label");

        Span amountSpan = new Span(formatCurrency(safeAmount));
        amountSpan.addClassName("summary-amount");

        if ("Available".equalsIgnoreCase(label)) {
            if (safeAmount.compareTo(BigDecimal.ZERO) < 0) {
                amountSpan.addClassName("amount-negative");

                Icon warningIcon = new Icon(VaadinIcon.WARNING);
                warningIcon.addClassName("amount-warning-icon");

                item.add(labelSpan, amountSpan, warningIcon);
                return item;
            } else if (safeAmount.compareTo(BigDecimal.valueOf(50_000_000)) < 0) {
                amountSpan.addClassName("amount-low");
            }
        }

        item.add(labelSpan, amountSpan);
        return item;
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return "UGX " + formatter.format(amount);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String derivePhysicalStatus(SectionBudget section) {
        BigDecimal pct = nvl(section.getSpentPercentage());

        if (pct.compareTo(BigDecimal.valueOf(100)) > 0) {
            return "Completed / Overrun";
        } else if (pct.compareTo(BigDecimal.valueOf(75)) >= 0) {
            return "Advanced";
        } else if (pct.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "In Progress";
        } else if (pct.compareTo(BigDecimal.ZERO) > 0) {
            return "Started";
        }
        return "Not Started";
    }

    private VerticalLayout createTextMetricCard(String label, String value, String className) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassNames("blo-summary-item", className);
        item.setAlignItems(FlexComponent.Alignment.CENTER);
        item.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        item.setWidthFull();

        Span labelSpan = new Span(label);
        labelSpan.addClassName("summary-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("summary-amount");

        item.add(labelSpan, valueSpan);
        return item;
    }

    private String derivePhysicalRemarks(SectionBudget section) {
        BigDecimal pct = nvl(section.getSpentPercentage());

        if (section.isOverBudget()) {
            return "Review expenditure and scope alignment";
        } else if (pct.compareTo(BigDecimal.valueOf(75)) >= 0) {
            return "Near completion";
        } else if (pct.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "Progressing normally";
        } else if (pct.compareTo(BigDecimal.ZERO) > 0) {
            return "Early implementation stage";
        }
        return "No notable physical progress recorded";
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
    }

    private static final PageSize REPORT_PAGE_SIZE = PageSize.A4;
    private static final float MARGIN_TOP = 85f;     // page 1 header space
    private static final float MARGIN_OTHER_TOP = 55f;
    private static final float MARGIN_RIGHT = 36f;
    private static final float MARGIN_BOTTOM = 55f;
    private static final float MARGIN_LEFT = 36f;

    private static final Color COLOR_HEADER_BG = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);
    private static final Color COLOR_GRID = new com.itextpdf.kernel.colors.DeviceRgb(220, 220, 220);
    private static final Color COLOR_ALT_ROW = new com.itextpdf.kernel.colors.DeviceRgb(250, 250, 250);
    private static final Color COLOR_NOTE_BG = new com.itextpdf.kernel.colors.DeviceRgb(248, 248, 248);

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private static final DateTimeFormatter PRINT_TS = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");

    private void generateBLOPDFReport() {
        try {
            if (selectedBudget == null) {
                showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (selectedDepartment == null) {
                showNotification("Please select a department first", NotificationVariant.LUMO_ERROR);
                return;
            }

            byte[] pdfBytes = buildBloDashboardPdf(selectedBudget, selectedDepartment);

            String fileName = "BLO_Dashboard_Report_"
                    + safe(selectedDepartment.getDepartmentCode()) + "_"
                    + safe(selectedBudget.getFinancialYear()).replace("/", "-")
                    + "_" + LocalDateTime.now().format(FILE_TS)
                    + ".pdf";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(pdfBytes));
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);

            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration reg = ui.getSession().getResourceRegistry().registerResource(resource);
                ui.getPage().open(reg.getResourceUri().toString(), "_blank");
                showNotification("BLO Dashboard PDF report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Unable to open PDF (no UI context).", NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Error generating PDF report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private byte[] buildBloDashboardPdf(Budget budget, DepartmentBudget dept) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos, new WriterProperties().setFullCompressionMode(true));
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(REPORT_PAGE_SIZE);

            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            PdfFormXObject totalPagesPlaceholder = new PdfFormXObject(
                    new com.itextpdf.kernel.geom.Rectangle(0, 0, 50, 12)
            );

            int qtr = mapQuarter(selectedQuarter);
            String quarterLabel = getQuarterLabel(qtr);

            String title = "BUDGET LIAISON OFFICER DASHBOARD REPORT";
            String subtitle = "Fiscal Year: " + safe(budget.getFinancialYear())
                    + "  |  Department: " + safe(dept.getDepartmentName())
                    + "  |  Quarter: " + quarterLabel;

            ImageData logo = loadLogo("/META-INF/resources/images/urclogo.png");

            pdf.addEventHandler(
                    PdfDocumentEvent.END_PAGE,
                    new BloHeaderFooterHandler(title, subtitle, fontRegular, fontBold, logo, totalPagesPlaceholder)
            );

            try (Document doc = new Document(pdf)) {
                doc.setMargins(100f, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);

                BigDecimal selectedSpent = getSelectedSpentAmount(dept);
                BigDecimal selectedAvailable = getSelectedAvailableAmount(dept);
                BigDecimal selectedSpentPct = percentage(selectedSpent, nvl(dept.getTotalBudget()));

                doc.add(new Paragraph("Generated on: " + LocalDateTime.now().format(PRINT_TS)
                        + "  |  Generated by: " + safe(currentUserName))
                        .setFont(fontRegular)
                        .setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(12));

                // EXEC SUMMARY
                doc.add(sectionTitle("Department Executive Summary", fontBold));

                Table summary = baseTable(new float[]{3, 2});
                addHeader(summary, fontBold, "Metric", "Amount / Value");

                addRow(summary, fontRegular, false, "Department Name", safe(dept.getDepartmentName()), false);
                addRow(summary, fontRegular, true, "Department Code", safe(dept.getDepartmentCode()), false);
                addRow(summary, fontRegular, false, "Quarter", quarterLabel, false);
                addRow(summary, fontRegular, true, "Total Budget", formatUGX(dept.getTotalBudget()), true);
                addRow(summary, fontRegular, false, "Cumulative Spent (" + quarterLabel + ")", formatUGX(selectedSpent), true);
                addRow(summary, fontRegular, true, "Available Budget", formatUGX(selectedAvailable), true);
                addRow(summary, fontRegular, false, "Budget Utilization", pct(selectedSpentPct), true);
                addRow(summary, fontRegular, true, "Department Status", safe(dept.getStatusText()), false);
                addRow(summary, fontRegular, false, "Number of Sections", String.valueOf(dept.getSectionCount()), false);

                doc.add(summary);

                doc.setMargins(MARGIN_OTHER_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);

                // SECTION ANALYSIS
                doc.add(sectionTitle("Section Budget Analysis", fontBold));
                List<SectionBudget> sections = safeList(
                        budgetService.getDepartmentSections(dept.getDepartmentCode(), budget)
                );

                if (!sections.isEmpty()) {
                    Table sectionTable = baseTable(new float[]{2.6f, 1.2f, 1.6f, 1.6f, 1.0f, 1.0f});
                    addHeader(sectionTable, fontBold,
                            "Section Name",
                            "Code",
                            "Allocated",
                            "Cum. Spent (" + quarterLabel + ")",
                            "Utilization",
                            "Status");

                    boolean alt = false;
                    for (SectionBudget s : sections) {
                        Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
                        alt = !alt;

                        BigDecimal sectionSpent = nvl(s.getSpentAmountByQuarter(selectedQuarter));
                        BigDecimal sectionPct = nvl(s.getSpentPercentageByQuarter(selectedQuarter));
                        String sectionStatus = safe(s.getStatusByQuarter(selectedQuarter));

                        addBodyCell(sectionTable, fontRegular, safe(s.getSectionName()), bg, TextAlignment.LEFT);
                        addBodyCell(sectionTable, fontRegular, safe(s.getSectionCode()), bg, TextAlignment.CENTER);
                        addBodyCell(sectionTable, fontRegular, formatUGX(s.getAllocatedBudget()), bg, TextAlignment.RIGHT);
                        addBodyCell(sectionTable, fontRegular, formatUGX(sectionSpent), bg, TextAlignment.RIGHT);
                        addBodyCell(sectionTable, fontRegular, pct(sectionPct), bg, TextAlignment.RIGHT);

                        sectionTable.addCell(
                                statusCell(fontRegular, sectionStatus, sectionStatusColorByQuarter(s, selectedQuarter))
                        );
                    }

                    doc.add(sectionTable);
                } else {
                    doc.add(new Paragraph("No section data available for this department.")
                            .setFont(fontRegular)
                            .setFontSize(10)
                            .setFontColor(ColorConstants.DARK_GRAY)
                            .setMarginBottom(10));
                }

                // Quarter-aware detail sections
                addFinancialBudgetPerformanceSectionByQuarter(doc, fontBold, fontRegular, budget, dept, selectedQuarter);
                addPhysicalBudgetPerformanceSectionByQuarter(doc, fontBold, fontRegular, budget, dept, selectedQuarter);
                addGeneralBudgetPhysicalPerformanceSectionByQuarter(doc, fontBold, fontRegular, budget, dept, selectedQuarter);

                // KPI
                doc.add(sectionTitle("Department Key Performance Indicators", fontBold));

                BigDecimal availablePct = percentage(selectedAvailable, nvl(dept.getTotalBudget()));
                BigDecimal efficiency = selectedSpent.compareTo(BigDecimal.ZERO) > 0
                        ? percentage(nvl(dept.getTotalBudget()), selectedSpent)
                        : BigDecimal.ZERO;

                Table kpi = baseTable(new float[]{3, 1.6f, 1.4f});
                addHeader(kpi, fontBold, "Indicator", "Value", "Status");

                addKpiRow(kpi, fontRegular,
                        "Budget Utilization Rate (" + quarterLabel + ")",
                        pct(selectedSpentPct),
                        selectedSpentPct.doubleValue() <= 85 ? "Good"
                        : selectedSpentPct.doubleValue() <= 95 ? "Caution" : "Critical");

                addKpiRow(kpi, fontRegular,
                        "Available Budget Ratio",
                        pct(availablePct),
                        availablePct.doubleValue() >= 15 ? "Healthy"
                        : availablePct.doubleValue() >= 5 ? "Low" : "Critical");

                addKpiRow(kpi, fontRegular,
                        "Budget Efficiency",
                        String.format(Locale.US, "%.1f%%", Math.min(efficiency.doubleValue(), 200)),
                        efficiency.doubleValue() >= 100 ? "Excellent"
                        : efficiency.doubleValue() >= 80 ? "Good" : "Below Target");

                addBodyCell(kpi, fontRegular, "Department Status", ColorConstants.WHITE, TextAlignment.LEFT);
                addBodyCell(kpi, fontRegular, safe(dept.getStatusText()), ColorConstants.WHITE, TextAlignment.LEFT);
                kpi.addCell(statusCell(fontRegular, safe(getStatusDescription(dept.getStatusText())), deptStatusColor(dept)));

                doc.add(kpi);

                // RECOMMENDATIONS
                doc.add(sectionTitle("Recommendations & Action Items", fontBold));
                doc.add(recommendationsBlock(dept, selectedSpentPct, fontRegular));

                // PENDING APPROVALS
                addPendingApprovalsSection(doc, pdf, fontBold, fontRegular, budget);

                doc.add(new Paragraph("This report was generated automatically by the Budget Liaison Officer Dashboard. "
                        + "For questions or clarifications, please contact the Budget Management Team.")
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(14));

                writeTotalPages(pdf, totalPagesPlaceholder, fontRegular);
            }

            return baos.toByteArray();
        }
    }

    private String getAchievementForQuarter(Urc_Activities activity, String quarterKey) {
        if (activity == null) {
            return "";
        }

        return switch (quarterKey != null ? quarterKey.toLowerCase() : "") {
            case "qtr1" ->
                safe(activity.getCum_achievements_qtr1());
            case "qtr2" ->
                safe(activity.getCum_achievements_qtr2());
            case "qtr3" ->
                safe(activity.getCum_achievements_qtr3());
            case "qtr4" ->
                safe(activity.getCum_achievements_qtr4());
            default ->
                safe(activity.getCum_achievements_qtr1());
        };
    }

    private double getPhysicalPctForQuarter(Urc_Activities activity, String quarterKey) {
        if (activity == null) {
            return 0.0;
        }

        return switch (quarterKey != null ? quarterKey.toLowerCase() : "") {
            case "qtr1" ->
                nz(activity.getPerc_of_TargetAchieved_qtr11());
            case "qtr2" ->
                nz(activity.getPerc_of_TargetAchieved_qtr21());
            case "qtr3" ->
                nz(activity.getPerc_of_TargetAchieved_qtr31());
            case "qtr4" ->
                nz(activity.getPerc_of_TargetAchieved_qtr41());
            default ->
                nz(activity.getPerc_of_TargetAchieved_qtr11());
        };
    }

    private double nz(Double value) {
        return value == null ? 0.0 : value;
    }

    private BigDecimal getQuarterBudget(BudgetItemsActuals item, int qtr) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        return switch (qtr) {
            case 1 ->
                nvl(item.getQtr1());
            case 2 ->
                nvl(item.getQtr1()).add(nvl(item.getQtr2()));
            case 3 ->
                nvl(item.getQtr1()).add(nvl(item.getQtr2())).add(nvl(item.getQtr3()));
            case 4 ->
                nvl(item.getQtr1()).add(nvl(item.getQtr2())).add(nvl(item.getQtr3())).add(nvl(item.getQtr4()));
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
                nvl(item.getQtr1A()).abs();
            case 2 ->
                nvl(item.getQtr1A()).add(nvl(item.getQtr2A())).abs();
            case 3 ->
                nvl(item.getQtr1A()).add(nvl(item.getQtr2A())).add(nvl(item.getQtr3A())).abs();
            case 4 ->
                nvl(item.getQtr1A()).add(nvl(item.getQtr2A())).add(nvl(item.getQtr3A())).add(nvl(item.getQtr4A())).abs();
            default ->
                BigDecimal.ZERO;
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

    private void addFinancialBudgetPerformanceSectionByQuarter(
            Document doc,
            PdfFont fontBold,
            PdfFont fontRegular,
            Budget budget,
            DepartmentBudget dept,
            String quarterKey
    ) {
        doc.add(sectionTitle("Financial Budget Performance", fontBold));

        List<BudgetItemsActuals> items = safeList(
                budgetItemsService.findDistinctBudgetItemsesExp(budget, dept.getSections())
        );

        if (items.isEmpty()) {
            doc.add(new Paragraph("No financial budget performance data available.")
                    .setFont(fontRegular)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10));
            return;
        }

        int qtr = mapQuarter(quarterKey);
        String quarterLabel = getQuarterLabel(qtr);

        Table t = baseTable(new float[]{1.0f, 2.3f, 1.3f, 1.3f, 1.3f, 1.1f});
        addHeader(t, fontBold,
                "Code",
                "Description",
                "Cum. Budget (" + quarterLabel + ")",
                "Cum. Actual (" + quarterLabel + ")",
                "Variance",
                "Status"
        );

        boolean alt = false;
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : items) {
            Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
            alt = !alt;

            BigDecimal budgetVal = getQuarterBudget(item, qtr);
            BigDecimal actualVal = getQuarterActual(item, qtr);
            BigDecimal variance = budgetVal.subtract(actualVal);

            totalBudget = totalBudget.add(budgetVal);
            totalActual = totalActual.add(actualVal);
            totalVariance = totalVariance.add(variance);

            addBodyCell(t, fontRegular,
                    item.getCoacode() != null ? safe(item.getCoacode().getCode()) : "",
                    bg, TextAlignment.LEFT);

            addBodyCell(t, fontRegular, safe(item.getItem()), bg, TextAlignment.LEFT);
            addBodyCell(t, fontRegular, formatUGX(budgetVal), bg, TextAlignment.RIGHT);
            addBodyCell(t, fontRegular, formatUGX(actualVal), bg, TextAlignment.RIGHT);
            addBodyCell(t, fontRegular, formatUGX(variance), bg, TextAlignment.RIGHT);

            t.addCell(statusCell(
                    fontRegular,
                    financialStatusText(budgetVal, actualVal, variance),
                    financialStatusColor(budgetVal, actualVal, variance)
            ));
        }

        addFinancialFooterRow(t, fontBold, totalBudget, totalActual, totalVariance);
        doc.add(t);
    }

    private void addPhysicalBudgetPerformanceSectionByQuarter(
            Document doc,
            PdfFont fontBold,
            PdfFont fontRegular,
            Budget budget,
            DepartmentBudget dept,
            String quarterKey
    ) {
        doc.add(sectionTitle("Physical Budget Performance", fontBold));

        List<Urc_Activities> activities = safeList(
                sampleUrc_ActivitiesService.findByDeptSectionAndBudget(dept.getSections(), budget)
        );

        if (activities.isEmpty()) {
            doc.add(new Paragraph("No physical budget performance data available.")
                    .setFont(fontRegular)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10));
            return;
        }

        String quarterLabel = getQuarterLabel(mapQuarter(quarterKey));

        Table t = baseTable(new float[]{2.8f, 2.2f, 1.4f, 1.4f});
        addHeader(t, fontBold,
                "Activity",
                quarterLabel + " Achievement",
                quarterLabel + " % Achieved",
                "Status"
        );

        boolean alt = false;

        for (Urc_Activities a : activities) {
            Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
            alt = !alt;

            double pct = getPhysicalPctForQuarter(a, quarterKey);

            addBodyCell(t, fontRegular, safe(a.getName()), bg, TextAlignment.LEFT);
            addBodyCell(t, fontRegular, safe(getAchievementForQuarter(a, quarterKey)), bg, TextAlignment.LEFT);
            addBodyCell(t, fontRegular, formatPercentage(pct), bg, TextAlignment.RIGHT);

            t.addCell(statusCell(
                    fontRegular,
                    physicalStatusText(pct),
                    physicalStatusColor(pct)
            ));
        }

        doc.add(t);
    }

    private void addGeneralBudgetPhysicalPerformanceSectionByQuarter(
            Document doc,
            PdfFont fontBold,
            PdfFont fontRegular,
            Budget budget,
            DepartmentBudget dept,
            String quarterKey
    ) {
        doc.add(sectionTitle("General Budget Physical Performance", fontBold));

        String deptCode = safe(dept.getDepartmentCode());
        String html = departmentGeneralPhysicalPerformanceService.getQuarterHtml(deptCode, quarterKey, budget);

        if (html == null || html.isBlank()) {
            doc.add(new Paragraph("No general budget physical performance narrative has been recorded.")
                    .setFont(fontRegular)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10));
            return;
        }

        doc.add(new Paragraph("Quarter: " + getQuarterLabel(mapQuarter(quarterKey)))
                .setFont(fontRegular)
                .setFontSize(9)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(6));

        doc.add(buildPhysicalNarrativeBlock(html, fontBold, fontRegular));
    }

    private Color sectionStatusColorByQuarter(SectionBudget s, String quarterKey) {
        if (s == null) {
            return COLOR_HEADER_BG;
        }
        String status = safe(s.getStatusByQuarter(quarterKey));
        if ("Over Budget".equalsIgnoreCase(status)) {
            return softPink();
        }
        if ("Critical".equalsIgnoreCase(status)) {
            return softYellow();
        }
        if ("Near Limit".equalsIgnoreCase(status)) {
            return new DeviceRgb(230, 220, 255);
        }
        return softGreen();
    }

    private void addFinancialBudgetPerformanceSection(
            Document doc,
            PdfFont fontBold,
            PdfFont fontRegular,
            Budget budget,
            DepartmentBudget dept
    ) {
        doc.add(sectionTitle("Financial Budget Performance", fontBold));

        List<BudgetItemsActuals> items = safeList(
                budgetItemsService.findDistinctBudgetItemsesExp(budget, dept.getSections())
        );

        if (items.isEmpty()) {
            doc.add(new Paragraph("No financial budget performance data available.")
                    .setFont(fontRegular)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10));
            return;
        }

        Table t = baseTable(new float[]{1.0f, 2.6f, 1.3f, 1.3f, 1.3f, 1.1f});
        addHeader(t, fontBold, "Code", "Description", "Budget", "Actual", "Variance", "Status");

        boolean alt = false;
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : items) {
            Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
            alt = !alt;

            BigDecimal budgetVal = nz(item.getTotal());
            BigDecimal actualVal = nz(item.getTotalA());
            BigDecimal variance = budgetVal.subtract(actualVal);

            totalBudget = totalBudget.add(budgetVal);
            totalActual = totalActual.add(actualVal);
            totalVariance = totalVariance.add(variance);

            addBodyCell(t, fontRegular,
                    item.getCoacode() != null ? safe(item.getCoacode().getCode()) : "",
                    bg, TextAlignment.LEFT);

            addBodyCell(t, fontRegular, safe(item.getItem()), bg, TextAlignment.LEFT);
            addBodyCell(t, fontRegular, formatUGX(budgetVal), bg, TextAlignment.RIGHT);
            addBodyCell(t, fontRegular, formatUGX(actualVal), bg, TextAlignment.RIGHT);
            addBodyCell(t, fontRegular, formatUGX(variance), bg, TextAlignment.RIGHT);

            t.addCell(statusCell(
                    fontRegular,
                    financialStatusText(budgetVal, actualVal, variance),
                    financialStatusColor(budgetVal, actualVal, variance)
            ));
        }

        addFinancialFooterRow(t, fontBold, totalBudget, totalActual, totalVariance);
        doc.add(t);
    }

    private void addPhysicalBudgetPerformanceSection(
            Document doc,
            PdfFont fontBold,
            PdfFont fontRegular,
            Budget budget,
            DepartmentBudget dept
    ) {
        doc.add(sectionTitle("Physical Budget Performance", fontBold));

        List<Urc_Activities> activities = safeList(
                sampleUrc_ActivitiesService.findByDeptSectionAndBudget(dept.getSections(), budget)
        );

        if (activities.isEmpty()) {
            doc.add(new Paragraph("No physical budget performance data available.")
                    .setFont(fontRegular)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10));
            return;
        }

        // UPDATED TABLE STRUCTURE (removed Code, Budget, Actual)
        Table t = baseTable(new float[]{2.8f, 2.2f, 1.4f, 1.4f});
        addHeader(t, fontBold,
                "Activity",
                "Latest Achievement",
                "% Achieved",
                "Status"
        );

        boolean alt = false;

        for (Urc_Activities a : activities) {
            Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
            alt = !alt;

            double pct = getLatestPhysicalPct(a);

            addBodyCell(t, fontRegular, safe(a.getName()), bg, TextAlignment.LEFT);
            addBodyCell(t, fontRegular, safe(getLatestAchievement(a)), bg, TextAlignment.LEFT);
            addBodyCell(t, fontRegular, formatPercentage(pct), bg, TextAlignment.RIGHT);

            t.addCell(statusCell(
                    fontRegular,
                    physicalStatusText(pct),
                    physicalStatusColor(pct)
            ));
        }

        doc.add(t);
    }

    private String physicalStatusText(double pct) {
        if (pct >= 100.0) {
            return "ACHIEVED";
        }
        if (pct >= 75.0) {
            return "ON TRACK";
        }
        if (pct > 0.0) {
            return "DELAYED";
        }
        return "NOT STARTED";
    }

    private Color physicalStatusColor(double pct) {
        if (pct >= 100.0) {
            return softGreen();
        }
        if (pct >= 75.0) {
            return softBlue();
        }
        if (pct > 0.0) {
            return softOrange();
        }
        return ColorConstants.LIGHT_GRAY;
    }

    private Color softBlue() {
        return new DeviceRgb(219, 234, 254); // light blue
    }

    private Color softOrange() {
        return new DeviceRgb(255, 237, 213); // light orange
    }

    private Color textBlue() {
        return new DeviceRgb(30, 64, 175);
    }

    private Color textOrange() {
        return new DeviceRgb(180, 83, 9);
    }

    private String formatPercentage(BigDecimal value) {
        if (value == null) {
            return "0.0%";
        }
        return String.format(Locale.US, "%.1f%%", value.doubleValue());
    }

    private String formatPercentage(double value) {
        return String.format(Locale.US, "%.1f%%", value);
    }

    private String getLatestAchievement(Urc_Activities activity) {
        if (!isBlank(activity.getCum_achievements_qtr4())) {
            return activity.getCum_achievements_qtr4();
        }
        if (!isBlank(activity.getCum_achievements_qtr3())) {
            return activity.getCum_achievements_qtr3();
        }
        if (!isBlank(activity.getCum_achievements_qtr2())) {
            return activity.getCum_achievements_qtr2();
        }
        return activity.getCum_achievements_qtr1();
    }

    private double getLatestPhysicalPct(Urc_Activities activity) {
        double q4 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr4());
        double q3 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr3());
        double q2 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr2());
        double q1 = parsePercentage(activity.getPerc_of_TargetAchieved_qtr1());

        if (q4 > 0) {
            return q4;
        }
        if (q3 > 0) {
            return q3;
        }
        if (q2 > 0) {
            return q2;
        }
        return q1;
    }

    private double parsePercentage(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }

        try {
            String cleaned = value.replace("%", "").replace(",", "").trim();
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void addGeneralBudgetPhysicalPerformanceSection(
            Document doc,
            PdfFont fontBold,
            PdfFont fontRegular,
            Budget budget,
            DepartmentBudget dept
    ) {
        doc.add(sectionTitle("General Budget Physical Performance", fontBold));

        String deptCode = safe(dept.getDepartmentCode());
        String quarterKey = "qtr1"; // use selected quarter later if you add it in BLO view

        String html = departmentGeneralPhysicalPerformanceService.getQuarterHtml(deptCode, quarterKey, budget);

        if (html == null || html.isBlank()) {
            doc.add(new Paragraph("No general budget physical performance narrative has been recorded.")
                    .setFont(fontRegular)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(10));
            return;
        }

        doc.add(buildPhysicalNarrativeBlock(html, fontBold, fontRegular));
    }

    private com.itextpdf.layout.element.Div buildPhysicalNarrativeBlock(String html, PdfFont fontBold, PdfFont fontRegular) {
        com.itextpdf.layout.element.Div wrapper = new com.itextpdf.layout.element.Div();

        addNarrativeSection(wrapper, "Summary", extractHtmlSection(html, "Summary"), fontBold, fontRegular);
        addNarrativeSection(wrapper, "Key Achievements", extractHtmlSection(html, "Key Achievements"), fontBold, fontRegular);
        addNarrativeSection(wrapper, "Challenges", extractHtmlSection(html, "Challenges"), fontBold, fontRegular);
        addNarrativeSection(wrapper, "Way Forward", extractHtmlSection(html, "Way Forward"), fontBold, fontRegular);

        return wrapper;
    }

    private void addNarrativeSection(
            com.itextpdf.layout.element.Div wrapper,
            String heading,
            String bodyHtml,
            PdfFont fontBold,
            PdfFont fontRegular
    ) {
        wrapper.add(new Paragraph(heading)
                .setFont(fontBold)
                .setFontSize(10)
                .setMarginTop(8)
                .setMarginBottom(4));

        String plain = htmlToPlainText(bodyHtml);
        wrapper.add(new Paragraph(plain.isBlank() ? "Not provided." : plain)
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
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private String htmlToPlainText(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }

        String text = html;
        text = text.replaceAll("(?is)<span[^>]*class=\"ql-ui\"[^>]*>.*?</span>", "");
        text = text.replaceAll("(?i)<li[^>]*>", "• ");
        text = text.replaceAll("(?i)</li>", "\n");
        text = text.replaceAll("(?i)</ol>", "\n");
        text = text.replaceAll("(?i)</ul>", "\n");
        text = text.replaceAll("(?i)<br\\s*/?>", "\n");
        text = text.replaceAll("(?i)</p>", "\n");
        text = text.replaceAll("(?i)<p[^>]*>", "");
        text = text.replaceAll("(?is)<[^>]+>", "");
        text = text.replace("&nbsp;", " ");
        text = text.replace("&amp;", "&");
        text = text.replace("&lt;", "<");
        text = text.replace("&gt;", ">");
        text = text.replaceAll("[ \\t\\x0B\\f\\r]+", " ");
        text = text.replaceAll("\\n\\s*\\n\\s*\\n+", "\n\n");
        return text.trim();
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String financialStatusText(BigDecimal budget, BigDecimal actual, BigDecimal variance) {
        if (budget.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            return "UNBUDGETED";
        }
        if (variance.compareTo(BigDecimal.ZERO) < 0) {
            return "OVER";
        }
        return "FINE";
    }

    private Color financialStatusColor(BigDecimal budget, BigDecimal actual, BigDecimal variance) {
        if (budget.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            return softPink();
        }
        if (variance.compareTo(BigDecimal.ZERO) < 0) {
            return softPink();
        }
        return softGreen();
    }

    private void addFinancialFooterRow(
            Table t,
            PdfFont fontBold,
            BigDecimal totalBudget,
            BigDecimal totalActual,
            BigDecimal totalVariance
    ) {
        t.addCell(new Cell(1, 2)
                .add(new Paragraph("TOTAL").setFont(fontBold).setFontSize(9))
                .setBackgroundColor(COLOR_HEADER_BG)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4)
                .setTextAlignment(TextAlignment.LEFT));

        t.addCell(new Cell()
                .add(new Paragraph(formatUGX(totalBudget)).setFont(fontBold).setFontSize(9))
                .setBackgroundColor(COLOR_HEADER_BG)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4)
                .setTextAlignment(TextAlignment.RIGHT));

        t.addCell(new Cell()
                .add(new Paragraph(formatUGX(totalActual)).setFont(fontBold).setFontSize(9))
                .setBackgroundColor(COLOR_HEADER_BG)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4)
                .setTextAlignment(TextAlignment.RIGHT));

        t.addCell(new Cell()
                .add(new Paragraph(formatUGX(totalVariance)).setFont(fontBold).setFontSize(9))
                .setBackgroundColor(COLOR_HEADER_BG)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4)
                .setTextAlignment(TextAlignment.RIGHT));

        t.addCell(statusCell(
                fontBold,
                financialStatusText(totalBudget, totalActual, totalVariance),
                financialStatusColor(totalBudget, totalActual, totalVariance)
        ));
    }

    /* =========================
   Header / Footer
   ========================= */
    private void writeTotalPages(PdfDocument pdf, PdfFormXObject placeholder, PdfFont font) {
        try (Canvas canvas = new Canvas(placeholder, pdf)) {
            canvas.add(new Paragraph(String.valueOf(pdf.getNumberOfPages()))
                    .setFont(font)
                    .setFontSize(8)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMargin(0));
        }
    }

    private static class BloHeaderFooterHandler implements com.itextpdf.kernel.events.IEventHandler {

        private final String title;
        private final String subtitle;
        private final PdfFont regular;
        private final PdfFont bold;
        private final ImageData logo;
        private final PdfFormXObject totalPages;

        BloHeaderFooterHandler(String title, String subtitle,
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
        public void handleEvent(com.itextpdf.kernel.events.Event event) {
            PdfDocumentEvent ev = (PdfDocumentEvent) event;
            PdfDocument pdf = ev.getDocument();
            PdfPage page = ev.getPage();
            Rectangle ps = page.getPageSize();

            float left = ps.getLeft() + MARGIN_LEFT;
            float right = ps.getRight() - MARGIN_RIGHT;

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);

            try (Canvas canvas = new Canvas(pdfCanvas, ps)) {
                int pageNo = pdf.getPageNumber(page);

                // HEADER only on page 1
                if (pageNo == 1) {
                    float top = ps.getTop();

                    // Header geometry (tweak once, everything else follows)
                    float headerTopPadding = 18f;
                    float lineGap = 12f;
                    float titleGap = 14f;

                    float y = top - headerTopPadding;

                    // Logo box
                    float logoW = 62f;
                    float logoH = 32f;
                    float logoY = top - 60f; // fixed logo anchor
                    if (logo != null) {
                        Image img = new Image(logo);
                        img.scaleToFit(logoW, logoH);
                        img.setFixedPosition(left, logoY);
                        canvas.add(img);
                    }

                    // Centered text block starts higher than logo bottom
                    // (ensures text doesn't collide with logo)
                    float textTop = top - 28f;

                    // 1) Organization name
                    canvas.showTextAligned(
                            new Paragraph("UGANDA RAILWAYS CORPORATION")
                                    .setFont(bold)
                                    .setFontSize(10)
                                    .setFontColor(ColorConstants.DARK_GRAY),
                            (left + right) / 2f,
                            textTop,
                            TextAlignment.CENTER
                    );

                    // 2) Report title
                    canvas.showTextAligned(
                            new Paragraph(title)
                                    .setFont(bold)
                                    .setFontSize(12),
                            (left + right) / 2f,
                            textTop - titleGap,
                            TextAlignment.CENTER
                    );

                    // 3) Subtitle
                    canvas.showTextAligned(
                            new Paragraph(subtitle)
                                    .setFont(regular)
                                    .setFontSize(9)
                                    .setFontColor(ColorConstants.DARK_GRAY),
                            (left + right) / 2f,
                            textTop - titleGap - lineGap,
                            TextAlignment.CENTER
                    );

                    // Divider placed clearly below subtitle
                    float dividerY = textTop - titleGap - lineGap - 12f;
                    pdfCanvas.setStrokeColor(COLOR_GRID).setLineWidth(0.8f);
                    pdfCanvas.moveTo(left, dividerY).lineTo(right, dividerY).stroke();
                }
                // FOOTER all pages
                float footerY = ps.getBottom() + 28f;

                pdfCanvas.setStrokeColor(COLOR_GRID).setLineWidth(0.8f);
                pdfCanvas.moveTo(left, footerY + 12f).lineTo(right, footerY + 12f).stroke();

                canvas.showTextAligned(
                        new Paragraph("Page " + pageNo + " of ")
                                .setFont(regular).setFontSize(8).setFontColor(ColorConstants.DARK_GRAY),
                        left, footerY, TextAlignment.LEFT
                );

                Image total = new Image(totalPages).scaleToFit(50, 12);
                total.setFixedPosition(left + 52f, footerY - 3f);
                canvas.add(total);

                canvas.showTextAligned(
                        new Paragraph("Confidential")
                                .setFont(regular).setFontSize(8).setFontColor(ColorConstants.DARK_GRAY),
                        right, footerY, TextAlignment.RIGHT
                );
            }
        }
    }

    /* =========================
   Sections
   ========================= */
    private void addPendingApprovalsSection(Document doc, PdfDocument pdf, PdfFont bold, PdfFont regular, Budget budget) {
        try {
            List<BudgetApprovals> pending = approvalsService.getPendingApprovalsByRole(currentUserRole)
                    .stream()
                    .filter(a -> a.getBudget() != null && a.getBudget().getId().equals(budget.getId()))
                    .toList();

            if (pending.isEmpty()) {
                return;
            }

            doc.add(sectionTitle("Pending Approval Requests", bold));

            Table t = baseTable(new float[]{1.4f, 2.2f, 2.0f, 1.2f, 1.0f});
            addHeader(t, bold, "Request ID", "Department", "Request Type", "Amount", "Priority");

            boolean alt = false;
            for (BudgetApprovals a : pending.stream().limit(10).toList()) {
                Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
                alt = !alt;

                addBodyCell(t, regular, safe(a.getRequestId()), bg, TextAlignment.LEFT);
                addBodyCell(t, regular, safe(a.getDepartmentName()), bg, TextAlignment.LEFT);
                addBodyCell(t, regular, a.getRequestType() == null ? "" : safe(a.getRequestType().getDisplayName()), bg, TextAlignment.LEFT);
                addBodyCell(t, regular, formatUGX(a.getRequestedAmount()), bg, TextAlignment.RIGHT);

                String pri = a.getPriorityLevel() == null ? "" : safe(a.getPriorityLevel().getDisplayName());
                t.addCell(statusCell(regular, pri, priorityColor(a)));
            }

            doc.add(t);

        } catch (Exception ignored) {
            doc.add(new Paragraph("Approval workflow information not available.")
                    .setFont(regular)
                    .setFontSize(9)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(8));
        }
    }

    /* =========================
   Styling helpers
   ========================= */
    private Paragraph sectionTitle(String text, PdfFont bold) {
        return new Paragraph(text.toUpperCase(Locale.ROOT))
                .setFont(bold)
                .setFontSize(12)
                .setMarginTop(10)
                .setMarginBottom(8);
    }

    private Table baseTable(float[] weights) {
        return new Table(UnitValue.createPercentArray(weights))
                .useAllAvailableWidth()
                .setFixedLayout()
                .setMarginBottom(14);
    }

    private void addHeader(Table table, PdfFont bold, String... headers) {
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFont(bold).setFontSize(9))
                    .setBackgroundColor(COLOR_HEADER_BG)
                    .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                    .setPadding(4)
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    private void addRow(Table table, PdfFont font, boolean altRow, String key, String value, boolean rightAlignValue) {
        Color bg = altRow ? COLOR_ALT_ROW : ColorConstants.WHITE;
        addBodyCell(table, font, key, bg, TextAlignment.LEFT);
        addBodyCell(table, font, value, bg, rightAlignValue ? TextAlignment.RIGHT : TextAlignment.LEFT);
    }

    private void addKpiRow(Table t, PdfFont font, String indicator, String value, String status) {
        addBodyCell(t, font, indicator, ColorConstants.WHITE, TextAlignment.LEFT);
        addBodyCell(t, font, value, ColorConstants.WHITE, TextAlignment.RIGHT);
        addBodyCell(t, font, status, ColorConstants.WHITE, TextAlignment.CENTER);
    }

    private void addControlStatusRow(Table t, PdfFont font, String type, boolean enabled, String desc, Color statusBg) {
        addBodyCell(t, font, type, ColorConstants.WHITE, TextAlignment.LEFT);
        t.addCell(statusCell(font, enabled ? "ENABLED" : "DISABLED", statusBg));
        addBodyCell(t, font, desc, ColorConstants.WHITE, TextAlignment.LEFT);
    }

    private void addBodyCell(Table t, PdfFont font, String text, Color bg, TextAlignment align) {
        t.addCell(new Cell()
                .add(new Paragraph(safe(text)).setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4)
                .setTextAlignment(align)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
    }

    private Cell statusCell(PdfFont font, String text, Color bg) {
        return new Cell()
                .add(new Paragraph(safe(text)).setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /* =========================
   Business helpers
   ========================= */
    private Paragraph recommendationsBlock(DepartmentBudget dept, BigDecimal spentPct, PdfFont font) {
        StringBuilder rec = new StringBuilder();
        double spent = safe(spentPct).doubleValue();

        if (spent > 100) {
            rec.append("• URGENT: Department has exceeded budget allocation by ")
                    .append(String.format(Locale.US, "%.1f%%", spent - 100))
                    .append(". Immediate corrective action required.\n");
        } else if (spent > 95) {
            rec.append("• CRITICAL: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
        } else if (spent > 85) {
            rec.append("• CAUTION: Budget utilization above 85%. Monitor spending closely.\n");
        } else if (spent < 40) {
            rec.append("• LOW UTILIZATION: Budget utilization below 40%. Consider accelerating planned activities.\n");
        } else {
            rec.append("• Budget utilization is within acceptable range (40–85%).\n");
        }

        double available = safe(dept.getAvailableBudget()).doubleValue();
        if (available < 0) {
            rec.append("• CRITICAL: Available budget is negative. Budget reallocation required.\n");
        } else if (available < 50_000_000) {
            rec.append("• WARNING: Available budget below UGX 50M threshold. Plan remaining expenditures carefully.\n");
        }

        if (safe(dept.getTotalCommitted()).doubleValue() > available) {
            rec.append("• ALERT: Committed amounts exceed available budget. Review commitments.\n");
        }

        if (!dept.isBudgetCheckEnabled() && spent > 80) {
            rec.append("• RECOMMENDATION: Enable Budget Check control for better spending monitoring.\n");
        }
        if (!dept.isBudgetStopEnabled() && spent > 90) {
            rec.append("• RECOMMENDATION: Consider enabling Budget Stop control to prevent overspending.\n");
        }
        if (dept.isPostingProhibited()) {
            rec.append("• ACTION REQUIRED: Posting is currently prohibited. Review and lift restriction if appropriate.\n");
        }

        if (rec.length() == 0) {
            rec.append("• Department budget performance is satisfactory. Continue monitoring and maintain current practices.\n");
        }

        return new Paragraph(rec.toString())
                .setFont(font)
                .setFontSize(10)
                .setPadding(10)
                .setBackgroundColor(COLOR_NOTE_BG)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setMarginBottom(10);
    }

    private Color sectionStatusColor(SectionBudget s) {
        if (s == null) {
            return COLOR_HEADER_BG;
        }
        if (s.isOverBudget()) {
            return softPink();
        }
        if (s.isCritical()) {
            return softYellow();
        }
        if (s.isNearLimit()) {
            return new com.itextpdf.kernel.colors.DeviceRgb(230, 220, 255); // soft purple
        }
        return softGreen();
    }

    private Color deptStatusColor(DepartmentBudget d) {
        String st = safe(d.getStatusText());
        if ("Over Budget".equalsIgnoreCase(st)) {
            return softPink();
        }
        if ("Critical".equalsIgnoreCase(st)) {
            return softYellow();
        }
        if ("Near Limit".equalsIgnoreCase(st)) {
            return softYellow();
        }
        return softGreen();
    }

    private Color priorityColor(BudgetApprovals a) {
        if (a == null || a.getPriorityLevel() == null) {
            return COLOR_HEADER_BG;
        }
        String name = a.getPriorityLevel().name();
        if ("HIGH".equalsIgnoreCase(name) || "URGENT".equalsIgnoreCase(name)) {
            return softPink();
        }
        if ("MEDIUM".equalsIgnoreCase(name)) {
            return softYellow();
        }
        return softGreen();
    }

    private Color softGreen() {
        return new com.itextpdf.kernel.colors.DeviceRgb(220, 245, 230);
    }

    private Color softYellow() {
        return new com.itextpdf.kernel.colors.DeviceRgb(255, 245, 210);
    }

    private Color softPink() {
        return new com.itextpdf.kernel.colors.DeviceRgb(255, 220, 230);
    }

    private String formatUGX(BigDecimal amount) {
        BigDecimal a = safe(amount);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(a);
    }

    private String pct(BigDecimal v) {
        return String.format(Locale.US, "%.1f%%", safe(v).doubleValue());
    }

    private BigDecimal percentage(BigDecimal numerator, BigDecimal denominator) {
        BigDecimal den = safe(denominator);
        if (den.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return safe(numerator).multiply(BigDecimal.valueOf(100))
                .divide(den, 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private ImageData loadLogo(String classpathPath) {
        try (InputStream is = getClass().getResourceAsStream(classpathPath)) {
            if (is == null) {
                return null;
            }
            return ImageDataFactory.create(is.readAllBytes());
        } catch (Exception e) {
            return null; // generate report even if logo fails
        }
    }

    private String getStatusDescription(String status) {
        switch (status) {
            case "Over Budget":
                return "Immediate action required";
            case "Critical":
                return "Close monitoring needed";
            case "Near Limit":
                return "Caution advised";
            default:
                return "Performance satisfactory";
        }
    }

    public List<DepartmentBudget> filterBudgets(
            List<DepartmentBudget> budgets,
            List<String> filterStrings
    ) {
        if (filterStrings == null || filterStrings.isEmpty()) {
            return budgets; // No filtering applied
        }

        return budgets.stream()
                .filter(budget -> filterStrings.stream()
                .anyMatch(filter
                        -> budget.getDepartmentCode().trim().equalsIgnoreCase(filter.trim())
                || budget.getDepartmentName().equalsIgnoreCase(filter)
                )
                )
                .collect(Collectors.toList());
    }

    private static double clamp01(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, v));
    }

    private static double pctToFraction(BigDecimal pct) {
        if (pct == null) {
            return 0.0;
        }
        return clamp01(pct.doubleValue() / 100.0);
    }

    private void generateBLOWordReport() {
        try {
            if (selectedBudget == null) {
                showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (selectedDepartment == null) {
                showNotification("Please select a department first", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (selectedQuarter == null || selectedQuarter.isBlank()) {
                showNotification("Please select a quarter first", NotificationVariant.LUMO_ERROR);
                return;
            }

            byte[] docxBytes = buildBloDashboardQuarterDocx(selectedBudget, selectedDepartment, selectedQuarter);

            String fileName = "BLO_Dashboard_Report_"
                    + safe(selectedDepartment.getDepartmentCode()) + "_"
                    + safe(selectedBudget.getFinancialYear()).replace("/", "-") + "_"
                    + selectedQuarter.toUpperCase(Locale.ROOT) + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                    + ".docx";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(docxBytes));
            resource.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            resource.setCacheTime(0);

            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration reg = ui.getSession().getResourceRegistry().registerResource(resource);
                ui.getPage().open(reg.getResourceUri().toString(), "_blank");
                showNotification("BLO quarter Word report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Unable to open Word report (no UI context).", NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Error generating Word report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private byte[] buildBloDashboardQuarterDocx(Budget budget, DepartmentBudget dept, String quarterKey)
            throws IOException, InvalidFormatException {

        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar mar = sectPr.addNewPgMar();
            mar.setTop(BigInteger.valueOf(1100));
            mar.setBottom(BigInteger.valueOf(900));
            mar.setLeft(BigInteger.valueOf(900));
            mar.setRight(BigInteger.valueOf(900));

            int qtr = mapQuarter(quarterKey);
            String quarterLabel = getQuarterLabel(qtr);

            addFirstPageHeader(
                    doc,
                    "/META-INF/resources/images/urclogo.png",
                    "BUDGET LIAISON OFFICER DASHBOARD REPORT",
                    "Fiscal Year: " + safe(budget.getFinancialYear())
                    + "  |  Department: " + safe(dept.getDepartmentName())
                    + "  |  Quarter: " + quarterLabel
            );

            addRightMeta(doc, "Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"))
                    + "  |  Generated by: " + safe(currentUserName));

            addSectionHeader(doc, "DEPARTMENT EXECUTIVE SUMMARY");
            XWPFTable summary = doc.createTable(1, 2);
            styleTable(summary);
            setHeaderRow(summary.getRow(0), "Metric", "Amount / Value");

            BigDecimal selectedSpent = getSelectedSpentAmount(dept);
            BigDecimal selectedAvailable = getSelectedAvailableAmount(dept);
            BigDecimal selectedPct = percentage(selectedSpent, nvl(dept.getTotalBudget()));

            addRow(summary, "Department Name", safe(dept.getDepartmentName()));
            addRow(summary, "Department Code", safe(dept.getDepartmentCode()));
            addRow(summary, "Quarter", quarterLabel);
            addRow(summary, "Total Budget", formatUGX(nvl(dept.getTotalBudget())));
            addRow(summary, "Cumulative Spent (" + quarterLabel + ")", formatUGX(selectedSpent));
            addRow(summary, "Available Budget", formatUGX(selectedAvailable));
            addRow(summary, "Budget Utilization", pct(selectedPct));
            addRow(summary, "Department Status", safe(dept.getStatusText()));
            addRow(summary, "Number of Sections", String.valueOf(dept.getSectionCount()));

            addSpacer(doc, 1);

            addSectionHeader(doc, "SECTION BUDGET ANALYSIS");
            List<SectionBudget> sections = safeList(budgetService.getDepartmentSections(dept.getDepartmentCode(), budget));

            if (!sections.isEmpty()) {
                XWPFTable sectionTable = doc.createTable(1, 6);
                styleTable(sectionTable);
                setHeaderRow(sectionTable.getRow(0),
                        "Section Name",
                        "Code",
                        "Allocated",
                        "Cum. Spent (" + quarterLabel + ")",
                        "Utilization",
                        "Status"
                );

                for (SectionBudget s : sections) {
                    BigDecimal sectionSpent = nvl(s.getSpentAmountByQuarter(quarterKey));
                    BigDecimal sectionPct = nvl(s.getSpentPercentageByQuarter(quarterKey));
                    String status = s.getStatusByQuarter(quarterKey);

                    XWPFTableRow r = sectionTable.createRow();
                    setCell(r.getCell(0), safe(s.getSectionName()), false, ParagraphAlignment.LEFT);
                    setCell(r.getCell(1), safe(s.getSectionCode()), false, ParagraphAlignment.CENTER);
                    setCell(r.getCell(2), formatUGX(nvl(s.getAllocatedBudget())), false, ParagraphAlignment.RIGHT);
                    setCell(r.getCell(3), formatUGX(sectionSpent), false, ParagraphAlignment.RIGHT);
                    setCell(r.getCell(4), pct(sectionPct), false, ParagraphAlignment.RIGHT);
                    setCell(r.getCell(5), safe(status), true, ParagraphAlignment.CENTER);
                    shadeCell(r.getCell(5), sectionStatusHex(status));
                }
            } else {
                addMutedParagraph(doc, "No section data available for this department.");
            }

            addSpacer(doc, 1);

            addFinancialBudgetPerformanceQuarterDocx(doc, budget, dept, quarterKey);
            addPhysicalBudgetPerformanceQuarterDocx(doc, budget, dept, quarterKey);
            addGeneralBudgetPhysicalPerformanceQuarterDocx(doc, budget, dept, quarterKey);

            addSectionHeader(doc, "DEPARTMENT KEY PERFORMANCE INDICATORS");

            XWPFTable kpiTable = doc.createTable(1, 3);
            styleTable(kpiTable);
            setHeaderRow(kpiTable.getRow(0), "Indicator", "Value", "Status");

            BigDecimal availablePct = percentage(selectedAvailable, nvl(dept.getTotalBudget()));
            BigDecimal efficiency = selectedSpent.compareTo(BigDecimal.ZERO) > 0
                    ? percentage(nvl(dept.getTotalBudget()), selectedSpent)
                    : BigDecimal.ZERO;

            addKpiRow(kpiTable,
                    "Budget Utilization Rate (" + quarterLabel + ")",
                    pct(selectedPct),
                    selectedPct.doubleValue() <= 85 ? "Good"
                    : selectedPct.doubleValue() <= 95 ? "Caution" : "Critical");

            addKpiRow(kpiTable,
                    "Available Budget Ratio",
                    pct(availablePct),
                    availablePct.doubleValue() >= 15 ? "Healthy"
                    : availablePct.doubleValue() >= 5 ? "Low" : "Critical");

            addKpiRow(kpiTable,
                    "Budget Efficiency",
                    String.format(Locale.US, "%.1f%%", Math.min(efficiency.doubleValue(), 200)),
                    efficiency.doubleValue() >= 100 ? "Excellent"
                    : efficiency.doubleValue() >= 80 ? "Good" : "Below Target");

            XWPFTable st = doc.createTable(1, 3);
            styleTable(st);
            setHeaderRow(st.getRow(0), "Department Status", "Current", "Comment");
            XWPFTableRow stRow = st.createRow();
            setCell(stRow.getCell(0), "Department Status", false, ParagraphAlignment.LEFT);
            setCell(stRow.getCell(1), safe(dept.getStatusText()), false, ParagraphAlignment.LEFT);
            setCell(stRow.getCell(2), safe(getStatusDescription(dept.getStatusText())), false, ParagraphAlignment.LEFT);
            shadeCell(stRow.getCell(2), deptStatusHex(dept));

            addSpacer(doc, 1);

            addSectionHeader(doc, "RECOMMENDATIONS & ACTION ITEMS");
            addRecommendationsBox(doc, buildDeptRecommendations(dept, selectedPct));

            addSpacer(doc, 1);
            addPendingApprovalsDocx(doc, budget);

            XWPFParagraph foot = doc.createParagraph();
            foot.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun fr = foot.createRun();
            fr.setFontFamily("Calibri");
            fr.setFontSize(9);
            fr.setColor("666666");
            fr.setText("This report was generated automatically by the Budget Liaison Officer Dashboard. "
                    + "For questions or clarifications, please contact the Budget Management Team.");

            doc.write(baos);
            return baos.toByteArray();
        }
    }
// ---------- First page header only ----------

    private void addFinancialBudgetPerformanceQuarterDocx(XWPFDocument doc, Budget budget, DepartmentBudget dept, String quarterKey) {
        addSectionHeader(doc, "FINANCIAL BUDGET PERFORMANCE");

        List<BudgetItemsActuals> items = safeList(
                budgetItemsService.findDistinctBudgetItemsesExp(budget, dept.getSections())
        );

        if (items.isEmpty()) {
            addMutedParagraph(doc, "No financial budget performance data available.");
            return;
        }

        int qtr = mapQuarter(quarterKey);
        String quarterLabel = getQuarterLabel(qtr);

        XWPFTable table = doc.createTable(1, 8);
        styleTable(table);
        setHeaderRow(table.getRow(0),
                "Code",
                "Description",
                "Cum. Budget (" + quarterLabel + ")",
                "Cum. Actual (" + quarterLabel + ")",
                "Total Budget",
                "Total Actual",
                "Variance",
                "Status"
        );

        BigDecimal totalSelectedBudget = BigDecimal.ZERO;
        BigDecimal totalSelectedActual = BigDecimal.ZERO;
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        for (BudgetItemsActuals item : items) {
            BigDecimal selectedBudget = getQuarterBudget(item, qtr);
            BigDecimal selectedActual = getQuarterActual(item, qtr);
            BigDecimal budgetVal = nvl(item.getTotal());
            BigDecimal actualVal = nvl(item.getTotalA());
            BigDecimal variance = selectedBudget.subtract(selectedActual);

            totalSelectedBudget = totalSelectedBudget.add(selectedBudget);
            totalSelectedActual = totalSelectedActual.add(selectedActual);
            totalBudget = totalBudget.add(budgetVal);
            totalActual = totalActual.add(actualVal);
            totalVariance = totalVariance.add(variance);

            XWPFTableRow r = table.createRow();
            ensureCells(r, 8);

            setCell(r.getCell(0), item.getCoacode() != null ? safe(item.getCoacode().getCode()) : "", false, ParagraphAlignment.LEFT);
            setCell(r.getCell(1), safe(item.getItem()), false, ParagraphAlignment.LEFT);
            setCell(r.getCell(2), formatUGX(selectedBudget), false, ParagraphAlignment.RIGHT);
            setCell(r.getCell(3), formatUGX(selectedActual), false, ParagraphAlignment.RIGHT);
            setCell(r.getCell(4), formatUGX(budgetVal), false, ParagraphAlignment.RIGHT);
            setCell(r.getCell(5), formatUGX(actualVal), false, ParagraphAlignment.RIGHT);
            setCell(r.getCell(6), formatUGX(variance), false, ParagraphAlignment.RIGHT);

            String status = financialStatusText(selectedBudget, selectedActual, variance);
            setCell(r.getCell(7), status, true, ParagraphAlignment.CENTER);
            shadeCell(r.getCell(7), financialStatusHex(selectedBudget, selectedActual, variance));
        }

        XWPFTableRow totalRow = table.createRow();
        ensureCells(totalRow, 8);
        setCell(totalRow.getCell(0), "TOTAL", true, ParagraphAlignment.LEFT);
        setCell(totalRow.getCell(1), "", true, ParagraphAlignment.LEFT);
        setCell(totalRow.getCell(2), formatUGX(totalSelectedBudget), true, ParagraphAlignment.RIGHT);
        setCell(totalRow.getCell(3), formatUGX(totalSelectedActual), true, ParagraphAlignment.RIGHT);
        setCell(totalRow.getCell(4), formatUGX(totalBudget), true, ParagraphAlignment.RIGHT);
        setCell(totalRow.getCell(5), formatUGX(totalActual), true, ParagraphAlignment.RIGHT);
        setCell(totalRow.getCell(6), formatUGX(totalVariance), true, ParagraphAlignment.RIGHT);
        setCell(totalRow.getCell(7),
                financialStatusText(totalSelectedBudget, totalSelectedActual, totalVariance),
                true,
                ParagraphAlignment.CENTER);
        shadeCell(totalRow.getCell(7), financialStatusHex(totalSelectedBudget, totalSelectedActual, totalVariance));

        addSpacer(doc, 1);
    }

    private void addPhysicalBudgetPerformanceQuarterDocx(XWPFDocument doc, Budget budget, DepartmentBudget dept, String quarterKey) {
        addSectionHeader(doc, "PHYSICAL BUDGET PERFORMANCE");

        List<Urc_Activities> activities = safeList(
                sampleUrc_ActivitiesService.findByDeptSectionAndBudget(dept.getSections(), budget)
        );

        if (activities.isEmpty()) {
            addMutedParagraph(doc, "No physical budget performance data available.");
            return;
        }

        String quarterLabel = getQuarterLabel(mapQuarter(quarterKey));

        XWPFTable table = doc.createTable(1, 4);
        styleTable(table);
        setHeaderRow(table.getRow(0),
                "Activity",
                quarterLabel + " Achievement",
                quarterLabel + " % Achieved",
                "Status"
        );

        for (Urc_Activities a : activities) {
            double pct = getPhysicalPctForQuarter(a, quarterKey);
            String status = physicalStatusText(pct);

            XWPFTableRow r = table.createRow();
            ensureCells(r, 4);
            setCell(r.getCell(0), safe(a.getName()), false, ParagraphAlignment.LEFT);
            setCell(r.getCell(1), safe(getAchievementForQuarter(a, quarterKey)), false, ParagraphAlignment.LEFT);
            setCell(r.getCell(2), formatPercentage(pct), false, ParagraphAlignment.RIGHT);
            setCell(r.getCell(3), status, true, ParagraphAlignment.CENTER);
            shadeCell(r.getCell(3), physicalStatusHex(pct));
        }

        addSpacer(doc, 1);
    }

    private String financialStatusHex(BigDecimal budget, BigDecimal actual, BigDecimal variance) {
        if (budget.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) > 0) {
            return "FFDCE6";
        }
        if (variance.compareTo(BigDecimal.ZERO) < 0) {
            return "FFDCE6";
        }
        return "DCF5E6";
    }

    private String physicalStatusHex(double pct) {
        if (pct >= 100.0) {
            return "DCF5E6";
        }
        if (pct >= 75.0) {
            return "DBEAFE";
        }
        if (pct > 0.0) {
            return "FFEDD5";
        }
        return "F2F2F2";
    }

    private void addGeneralBudgetPhysicalPerformanceQuarterDocx(XWPFDocument doc, Budget budget, DepartmentBudget dept, String quarterKey) {
        addSectionHeader(doc, "GENERAL BUDGET PHYSICAL PERFORMANCE");

        String html = departmentGeneralPhysicalPerformanceService.getQuarterHtml(
                safe(dept.getDepartmentCode()),
                quarterKey,
                budget
        );

        if (html == null || html.isBlank()) {
            addMutedParagraph(doc, "No general budget physical performance narrative has been recorded.");
            return;
        }

        addParagraph(doc, "Quarter: " + getQuarterLabel(mapQuarter(quarterKey)), false, ParagraphAlignment.LEFT);
        addNarrativeParagraphDocx(doc, "Summary", extractHtmlSection(html, "Summary"));
        addNarrativeParagraphDocx(doc, "Key Achievements", extractHtmlSection(html, "Key Achievements"));
        addNarrativeParagraphDocx(doc, "Challenges", extractHtmlSection(html, "Challenges"));
        addNarrativeParagraphDocx(doc, "Way Forward", extractHtmlSection(html, "Way Forward"));
    }

    private void addParagraph(
            XWPFDocument doc,
            String text,
            boolean bold,
            ParagraphAlignment alignment
    ) {
        XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(alignment);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontFamily("Calibri");
        run.setFontSize(11);
    }

    private void ensureCells(XWPFTableRow row, int count) {
        while (row.getTableCells().size() < count) {
            row.addNewTableCell();
        }
    }

    private String sectionStatusHex(String status) {
        if ("Over Budget".equalsIgnoreCase(status)) {
            return "FFDCE6";
        }
        if ("Critical".equalsIgnoreCase(status)) {
            return "FFF5D2";
        }
        if ("Near Limit".equalsIgnoreCase(status)) {
            return "E6DCFF";
        }
        return "DCF5E6";
    }

    private void setHeaderRow(XWPFTableRow row, String... headers) {
        ensureCells(row, headers.length);

        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = row.getCell(i);

            // Clear existing content
            cell.removeParagraph(0);

            XWPFParagraph p = cell.addParagraph();
            p.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun run = p.createRun();
            run.setText(headers[i]);
            run.setBold(true);
            run.setFontFamily("Calibri");
            run.setFontSize(10);

            // Background color (light blue/grey)
            cell.setColor("D9E2F3");

            // Vertical alignment
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        }
    }

    private void styleTable(XWPFTable table) {
        table.setWidth("100%");

        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTTblBorders borders = tblPr.addNewTblBorders();

        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }

    private void addNarrativeParagraphDocx(XWPFDocument doc, String heading, String bodyHtml) {
        XWPFParagraph headingP = doc.createParagraph();
        headingP.setSpacingBefore(80);
        headingP.setSpacingAfter(40);

        XWPFRun hr = headingP.createRun();
        hr.setFontFamily("Calibri");
        hr.setFontSize(11);
        hr.setBold(true);
        hr.setText(heading);

        XWPFParagraph bodyP = doc.createParagraph();
        bodyP.setSpacingAfter(100);

        XWPFRun br = bodyP.createRun();
        br.setFontFamily("Calibri");
        br.setFontSize(10);
        br.setText(htmlToPlainText(bodyHtml).isBlank() ? "Not provided." : htmlToPlainText(bodyHtml));
    }

    private void addFirstPageHeader(XWPFDocument doc,
            String logoClasspath,
            String title,
            String subtitle) throws IOException, InvalidFormatException {

        // 1) Ensure sectPr exists
        CTBody body = doc.getDocument().getBody();
        CTSectPr sectPr = body.isSetSectPr() ? body.getSectPr() : body.addNewSectPr();

        // 2) Enable "Different first page" header/footer
        // Equivalent to checking "Different First Page" in Word header settings
        if (!sectPr.isSetTitlePg()) {
            sectPr.addNewTitlePg();
        }

        // 3) Create header policy bound to this section
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc, sectPr);

        // Create FIRST header (page 1 only) and DEFAULT header (pages 2+)
        XWPFHeader firstHeader = policy.createHeader(XWPFHeaderFooterPolicy.FIRST);
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT); // keep empty; prevents some viewers from copying FIRST

        // ---- Logo paragraph (left)
// ---- Logo paragraph (left)
        XWPFParagraph logoP = firstHeader.createParagraph();
        logoP.setAlignment(ParagraphAlignment.LEFT);
        logoP.setSpacingAfter(40);

        try (InputStream is = getClass().getResourceAsStream(logoClasspath)) {
            if (is != null) {
                XWPFRun lr = logoP.createRun();
                lr.addPicture(
                        is,
                        pictureTypeFromPath(logoClasspath),
                        "urc-logo",
                        Units.toEMU(70), // ✅ smaller width
                        Units.toEMU(35) // ✅ smaller height
                );
            }
        }
// ✅ Organization name
        XWPFParagraph orgP = firstHeader.createParagraph();
        orgP.setAlignment(ParagraphAlignment.CENTER);
        orgP.setSpacingAfter(20);

        XWPFRun or = orgP.createRun();
        or.setFontFamily("Calibri");
        or.setFontSize(11);
        or.setBold(true);
        or.setColor("444444");
        or.setText("UGANDA RAILWAYS CORPORATION");

        // ---- Title (center)
        XWPFParagraph titleP = firstHeader.createParagraph();
        titleP.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun tr = titleP.createRun();
        tr.setFontFamily("Calibri");
        tr.setFontSize(14);
        tr.setBold(true);
        tr.setText(title);

        // ---- Subtitle (center + divider line)
        XWPFParagraph subP = firstHeader.createParagraph();
        subP.setAlignment(ParagraphAlignment.CENTER);
        subP.setBorderBottom(Borders.SINGLE);

        XWPFRun sr = subP.createRun();
        sr.setFontFamily("Calibri");
        sr.setFontSize(10);
        sr.setBold(true);
        sr.setColor("444444");
        sr.setText(subtitle);
    }

    private int pictureTypeFromPath(String path) {
        String p = path == null ? "" : path.toLowerCase();
        if (p.endsWith(".jpg") || p.endsWith(".jpeg")) {
            return XWPFDocument.PICTURE_TYPE_JPEG;
        }
        if (p.endsWith(".gif")) {
            return XWPFDocument.PICTURE_TYPE_GIF;
        }
        if (p.endsWith(".bmp")) {
            return XWPFDocument.PICTURE_TYPE_BMP;
        }
        return XWPFDocument.PICTURE_TYPE_PNG; // default
    }

// ---------- Paragraph helpers ----------
    private void addRightMeta(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.RIGHT);
        p.setSpacingAfter(180);
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

    private void addMutedParagraph(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(120);
        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(10);
        r.setColor("666666");
        r.setText(text);
    }

    private void addSpacer(XWPFDocument doc, int lines) {
        for (int i = 0; i < lines; i++) {
            XWPFParagraph p = doc.createParagraph();
            p.setSpacingAfter(120);
        }
    }

    private void addRow(XWPFTable table, String c1, String c2) {
        XWPFTableRow r = table.createRow();
        setCell(r.getCell(0), c1, false, ParagraphAlignment.LEFT);
        setCell(r.getCell(1), c2, false, ParagraphAlignment.LEFT);
    }

    private void addKpiRow(XWPFTable table, String indicator, String value, String status) {
        XWPFTableRow r = table.createRow();
        setCell(r.getCell(0), indicator, false, ParagraphAlignment.LEFT);
        setCell(r.getCell(1), value, false, ParagraphAlignment.RIGHT);
        setCell(r.getCell(2), status, false, ParagraphAlignment.CENTER);
    }

    private void addControlStatusRow(XWPFTable table, String type, boolean enabled, String desc, String statusShadeHex) {
        XWPFTableRow r = table.createRow();
        setCell(r.getCell(0), type, false, ParagraphAlignment.LEFT);

        setCell(r.getCell(1), enabled ? "ENABLED" : "DISABLED", true, ParagraphAlignment.CENTER);
        shadeCell(r.getCell(1), statusShadeHex);

        setCell(r.getCell(2), desc, false, ParagraphAlignment.LEFT);
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
        cell.setColor(hexColor); // e.g. "DCF5E6"
    }

// ---------- Recommendations "box" ----------
    private void addRecommendationsBox(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(80);
        p.setSpacingAfter(120);

        // light shading by using a 1x1 table (most reliable in Word)
        XWPFTable box = doc.createTable(1, 1);
        box.setWidth("100%");
        XWPFTableCell c = box.getRow(0).getCell(0);
        c.setColor("F8F8F8");

        c.removeParagraph(0);
        XWPFParagraph cp = c.addParagraph();
        XWPFRun r = cp.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(10);
        r.setText(text == null ? "" : text);
    }

// ---------- Pending Approvals (best-effort) ----------
    private void addPendingApprovalsDocx(XWPFDocument doc, Budget budget) {
        try {
            List<BudgetApprovals> pending = approvalsService.getPendingApprovalsByRole(currentUserRole)
                    .stream()
                    .filter(a -> a.getBudget() != null && a.getBudget().getId().equals(budget.getId()))
                    .toList();

            if (pending.isEmpty()) {
                return;
            }

            addSectionHeader(doc, "PENDING APPROVAL REQUESTS");

            XWPFTable t = doc.createTable(1, 5);
            styleTable(t);
            setHeaderRow(t.getRow(0), "Request ID", "Department", "Request Type", "Amount", "Priority");

            for (BudgetApprovals a : pending.stream().limit(10).toList()) {
                XWPFTableRow r = t.createRow();
                setCell(r.getCell(0), safe(a.getRequestId()), false, ParagraphAlignment.LEFT);
                setCell(r.getCell(1), safe(a.getDepartmentName()), false, ParagraphAlignment.LEFT);
                setCell(r.getCell(2), a.getRequestType() == null ? "" : safe(a.getRequestType().getDisplayName()), false, ParagraphAlignment.LEFT);
                setCell(r.getCell(3), formatUGX(a.getRequestedAmount()), false, ParagraphAlignment.RIGHT);

                String pri = a.getPriorityLevel() == null ? "" : safe(a.getPriorityLevel().getDisplayName());
                setCell(r.getCell(4), pri, true, ParagraphAlignment.CENTER);
                shadeCell(r.getCell(4), priorityHex(a));
            }

            addSpacer(doc, 1);
        } catch (Exception e) {
            addMutedParagraph(doc, "Approval workflow information not available.");
        }
    }

// ---------- Coloring logic ----------
    private String sectionStatusHex(SectionBudget s) {
        if (s == null) {
            return "F2F2F2";
        }
        if (s.isOverBudget()) {
            return "FFDCE6"; // soft pink
        }
        if (s.isCritical()) {
            return "FFF5D2";   // soft yellow
        }
        if (s.isNearLimit()) {
            return "E6DCFF";  // soft purple
        }
        return "DCF5E6";                       // soft green
    }

    private String deptStatusHex(DepartmentBudget d) {
        String st = safe(d.getStatusText());
        if ("Over Budget".equalsIgnoreCase(st)) {
            return "FFDCE6";
        }
        if ("Critical".equalsIgnoreCase(st)) {
            return "FFF5D2";
        }
        if ("Near Limit".equalsIgnoreCase(st)) {
            return "FFF5D2";
        }
        return "DCF5E6";
    }

    private String priorityHex(BudgetApprovals a) {
        if (a == null || a.getPriorityLevel() == null) {
            return "F2F2F2";
        }
        String name = a.getPriorityLevel().name();
        if ("HIGH".equalsIgnoreCase(name) || "URGENT".equalsIgnoreCase(name)) {
            return "FFDCE6";
        }
        if ("MEDIUM".equalsIgnoreCase(name)) {
            return "FFF5D2";
        }
        return "DCF5E6";
    }

    private String buildDeptRecommendations(DepartmentBudget dept, BigDecimal spentPct) {
        StringBuilder rec = new StringBuilder();
        double spent = safe(spentPct).doubleValue();

        if (spent > 100) {
            rec.append("• URGENT: Department has exceeded budget allocation by ")
                    .append(String.format(Locale.US, "%.1f%%", spent - 100))
                    .append(". Immediate corrective action required.\n");
        } else if (spent > 95) {
            rec.append("• CRITICAL: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
        } else if (spent > 85) {
            rec.append("• CAUTION: Budget utilization above 85%. Monitor spending closely.\n");
        } else if (spent < 40) {
            rec.append("• LOW UTILIZATION: Budget utilization below 40%. Consider accelerating planned activities.\n");
        } else {
            rec.append("• Budget utilization is within acceptable range (40–85%).\n");
        }

        double available = safe(dept.getAvailableBudget()).doubleValue();
        if (available < 0) {
            rec.append("• CRITICAL: Available budget is negative. Budget reallocation required.\n");
        } else if (available < 50_000_000) {
            rec.append("• WARNING: Available budget below UGX 50M threshold. Plan remaining expenditures carefully.\n");
        }

        if (safe(dept.getTotalCommitted()).doubleValue() > available) {
            rec.append("• ALERT: Committed amounts exceed available budget. Review commitments.\n");
        }

        if (!dept.isBudgetCheckEnabled() && spent > 80) {
            rec.append("• RECOMMENDATION: Enable Budget Check control for better spending monitoring.\n");
        }

        if (!dept.isBudgetStopEnabled() && spent > 90) {
            rec.append("• RECOMMENDATION: Consider enabling Budget Stop control to prevent overspending.\n");
        }

        if (dept.isPostingProhibited()) {
            rec.append("• ACTION REQUIRED: Posting is currently prohibited. Review and lift restriction if appropriate.\n");
        }

        if (rec.length() == 0) {
            rec.append("• Department budget performance is satisfactory. Continue monitoring and maintain current practices.\n");
        }

        return rec.toString();
    }

}
