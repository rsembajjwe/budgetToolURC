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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private Tab approvalsTab;
    private Tab requestsTab;
    private Tab analyticsTab;

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

    @Autowired
    public BLODashboardView(BudgetService budgetService, BudgetApprovalsService approvalsService, BudgetControlService controlService,
            AuthenticatedUser authenticatedUser, UserService userService, DeptSectionMergerService sampleDeptSectionMergerService,
            BudgetItemsService budgetItemsService, CoaService sampleCoaService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            SALFLDGService sampleSALFLDGService) {
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

        // Left side - Title and Department Selector
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSpacing(false);
        leftSide.setPadding(false);
        leftSide.setFlexGrow(1);

        H1 title = new H1("Budget Liaison Officer Dashboard");
        title.addClassName("blo-title");

        Span subtitle = new Span("Comprehensive Budget, Approval & Performance Management");
        subtitle.addClassName("blo-subtitle");

        // Selectors layout
        HorizontalLayout selectorsLayout = new HorizontalLayout();
        selectorsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        selectorsLayout.setSpacing(true);
        selectorsLayout.setPadding(false);
        selectorsLayout.addClassName("blo-selectors");

        // Fiscal year selector
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

        // Department Selector
        Span selectorLabel = new Span("Department:");
        selectorLabel.addClassName("selector-label");

        departmentSelector = new ComboBox<>();
        departmentSelector.setPlaceholder("Select your department...");
        departmentSelector.setItemLabelGenerator(dept -> dept.getDepartmentName());
        departmentSelector.addClassName("department-selector");
        departmentSelector.setWidthFull();
        departmentSelector.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedDepartment = e.getValue();
                refreshCurrentTab();
            }
        });

        selectorsLayout.add(selectorYearLabel, fiscalYear, selectorLabel, departmentSelector);
        selectorsLayout.setFlexGrow(1, departmentSelector);

        leftSide.add(title, subtitle, selectorsLayout);

        // Right side - Action buttons
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);
        rightSide.addClassName("blo-actions");

        Button newRequestButton = new Button("New Request", new Icon(VaadinIcon.PLUS));
        newRequestButton.addClassName("blo-new-request-button");
        newRequestButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newRequestButton.addClickListener(e -> showNewRequestForm());

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
        exportButton.addClickListener(e -> {
            generateBLOPDFReport();
            //showNotification("Export functionality coming soon", NotificationVariant.LUMO_PRIMARY);
        });

        Button exportWordButton = new Button("Export Word", new Icon(VaadinIcon.DOWNLOAD));
        exportWordButton.addClassName("blo-export-button");
        exportWordButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportWordButton.addClickListener(e -> {
            generateBLOWordReport();
            //showNotification("Export functionality coming soon", NotificationVariant.LUMO_PRIMARY);
        });

        Button alertsButton = new Button("Alerts", new Icon(VaadinIcon.BELL));
        alertsButton.addClassName("blo-alerts-button");
        alertsButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        alertsButton.addClickListener(e -> {
            showNotification("Budget alerts panel coming soon", NotificationVariant.LUMO_CONTRAST);
        });

        rightSide.add(newRequestButton, refreshButton, exportButton, exportWordButton, alertsButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void createNavigationTabs() {
        navigationTabs = new Tabs();
        navigationTabs.addClassName("blo-navigation-tabs");
        navigationTabs.setWidthFull();

        budgetTab = new Tab(new Icon(VaadinIcon.DASHBOARD), new Span("Budget Overview"));
        approvalsTab = new Tab(new Icon(VaadinIcon.FILE_PROCESS), new Span("Approvals"));
        requestsTab = new Tab(new Icon(VaadinIcon.FILE_TEXT), new Span("My Requests"));
        analyticsTab = new Tab(new Icon(VaadinIcon.CHART), new Span("Analytics"));

        budgetTab.addClassName("nav-tab");
        approvalsTab.addClassName("nav-tab");
        requestsTab.addClassName("nav-tab");
        analyticsTab.addClassName("nav-tab");

        navigationTabs.add(budgetTab, approvalsTab, requestsTab, analyticsTab);
        navigationTabs.setSelectedTab(budgetTab);

        navigationTabs.addSelectedChangeListener(e -> {
            Tab selectedTab = e.getSelectedTab();
            if (selectedTab == budgetTab) {
                showBudgetOverview();
            } else if (selectedTab == approvalsTab) {
                showApprovalsView();
            } else if (selectedTab == requestsTab) {
                showMyRequestsView();
            } else if (selectedTab == analyticsTab) {
                showAnalyticsView();
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
            if (user.getRoles().contains(Role.ADMIN)) {

            } else {
                Set<UrcDeptSectionAnlDimbgt> deptsection = user.getDeptsection();
                List<String> deptcodes = new ArrayList();
                for (UrcDeptSectionAnlDimbgt dep : deptsection) {
                    String getDeptCodeBySectionCode = sampleDeptSectionMergerService.getDeptCodeBySectionCode(dep.getANL_CODE());
                    deptcodes.add(getDeptCodeBySectionCode);
                }
                deptcodes = deptcodes.stream().distinct().collect(Collectors.toList());

                departments = filterBudgets(departments, deptcodes);
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
            createDepartmentOverview();

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
                    = new DepartmentSectionCards(selectedDepartment, budgetService, selectedBudget,
                            sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService);
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

    private void showApprovalsView() {
        if (selectedBudget == null) {
            showNotification("Please select a fiscal year first", NotificationVariant.LUMO_WARNING);
            return;
        }

        contentContainer.removeAll();

        // Approval dashboard cards
        approvalCards = new ApprovalDashboardCards(approvalsService, currentUserRole);
        approvalCards.addClassName("blo-approval-cards");

        // Pending approvals section
        VerticalLayout approvalsSection = new VerticalLayout();
        approvalsSection.setSpacing(true);
        approvalsSection.setPadding(false);
        approvalsSection.addClassName("blo-approvals-section");

        H3 approvalsTitle = new H3("Pending Approvals - BLO Review");
        approvalsTitle.addClassName("section-title");

        List<BudgetApprovals> pendingApprovals = approvalsService.getPendingApprovalsByRole(currentUserRole)
                .stream()
                .filter(approval -> approval.getBudget().getId().equals(selectedBudget.getId()))
                .toList();

        approvalGrid = new ApprovalWorkflowGrid(pendingApprovals, currentUserRole, approvalsService);
        approvalGrid.addClassName("blo-approval-grid");

        approvalsSection.add(approvalsTitle, approvalGrid);
        contentContainer.add(approvalCards, approvalsSection);
    }

    private void showMyRequestsView() {
        if (selectedBudget == null) {
            showNotification("Please select a fiscal year first", NotificationVariant.LUMO_WARNING);
            return;
        }

        contentContainer.removeAll();

        H3 requestsTitle = new H3("My Budget Requests");
        requestsTitle.addClassName("section-title");

        List<BudgetApprovals> myRequests = approvalsService.getApprovalsByRequester(currentUserName)
                .stream()
                .filter(approval -> approval.getBudget().getId().equals(selectedBudget.getId()))
                .toList();

        ApprovalWorkflowGrid myRequestsGrid = new ApprovalWorkflowGrid(myRequests, currentUserRole, approvalsService);
        myRequestsGrid.addClassName("blo-requests-grid");

        contentContainer.add(requestsTitle, myRequestsGrid);
    }

    private void showAnalyticsView() {
        if (selectedBudget == null || selectedDepartment == null) {
            createEmptyState();
            return;
        }

        contentContainer.removeAll();

        H3 analyticsTitle = new H3("Budget Analytics & Insights");
        analyticsTitle.addClassName("section-title");

        // Analytics grid
        HorizontalLayout analyticsGrid = new HorizontalLayout();
        analyticsGrid.setWidthFull();
        analyticsGrid.setSpacing(true);
        analyticsGrid.setPadding(false);
        analyticsGrid.addClassName("blo-analytics-grid");

        // Left side - Charts
        VerticalLayout chartsColumn = new VerticalLayout();
        chartsColumn.setSpacing(true);
        chartsColumn.setPadding(false);
        chartsColumn.setFlexGrow(2);

        SectionBudgetChart budgetChart = new SectionBudgetChart(selectedDepartment);
        budgetChart.addClassName("analytics-chart");

        chartsColumn.add(budgetChart);

        // Right side - Insights and alerts
        VerticalLayout insightsColumn = new VerticalLayout();
        insightsColumn.setSpacing(true);
        insightsColumn.setPadding(false);
        insightsColumn.setFlexGrow(1);

        BudgetAlerts budgetAlerts = new BudgetAlerts(selectedDepartment);
        budgetAlerts.addClassName("analytics-alerts");

        // Approval statistics
        ApprovalDashboardCards analyticsCards = new ApprovalDashboardCards(approvalsService, currentUserRole);
        analyticsCards.addClassName("analytics-approval-cards");

        insightsColumn.add(analyticsCards, budgetAlerts);

        analyticsGrid.add(chartsColumn, insightsColumn);
        contentContainer.add(analyticsTitle, analyticsGrid);
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
        } else if (selectedTab == approvalsTab) {
            showApprovalsView();
        } else if (selectedTab == requestsTab) {
            showMyRequestsView();
        } else if (selectedTab == analyticsTab) {
            showAnalyticsView();
        }
    }

    private void createDepartmentOverview() {
        Div overviewCard = new Div();
        overviewCard.addClassName("blo-department-overview");

        HorizontalLayout overviewContent = new HorizontalLayout();
        overviewContent.setWidthFull();
        overviewContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        overviewContent.setAlignItems(FlexComponent.Alignment.CENTER);
        overviewContent.setPadding(false);
        overviewContent.setSpacing(false);

        // Department Info
        HorizontalLayout deptInfo = new HorizontalLayout();
        deptInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        deptInfo.setSpacing(true);
        deptInfo.setPadding(false);

        Div colorIndicator = new Div();
        colorIndicator.addClassName("dept-color-indicator");
        colorIndicator.getStyle().set("background-color", selectedDepartment.getColor());

        VerticalLayout deptDetails = new VerticalLayout();
        deptDetails.setSpacing(false);
        deptDetails.setPadding(false);

        H2 deptName = new H2(selectedDepartment.getDepartmentName());
        deptName.addClassName("dept-overview-name");

        Span deptCode = new Span("Code: " + selectedDepartment.getDepartmentCode()
                + " | Category: " + selectedDepartment.getCategoryId()
                + " | Sections: " + selectedDepartment.getSectionCount());
        deptCode.addClassName("dept-overview-details");

        deptDetails.add(deptName, deptCode);
        deptInfo.add(colorIndicator, deptDetails);

        // Budget Summary
        HorizontalLayout budgetSummary = new HorizontalLayout();
        budgetSummary.setSpacing(true);
        budgetSummary.setPadding(false);
        budgetSummary.addClassName("dept-budget-summary");
        VerticalLayout totalSpan = createBudgetSummaryItem("Total Budget", selectedDepartment.getTotalBudget(), "summary-budget");
        VerticalLayout totalSpentSpan = createBudgetSummaryItem("Total Spent", selectedDepartment.getTotalSpent(), "summary-spent");
        VerticalLayout totalCommittedSpan = createBudgetSummaryItem("Committed", selectedDepartment.getTotalCommitted(), "summary-committed");
        VerticalLayout availableSpan = createBudgetSummaryItem("Available", selectedDepartment.getAvailableBudget(), "summary-available");
        totalSpentSpan.addDoubleClickListener(e -> {
            String deptcode = selectedDepartment.getDepartmentCode();
            Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);

            utils = new utilityActuals(selectedBudget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sectionCodes);
            utils.createTransactionsDialog2(overviewContent);
        });

        budgetSummary.add(totalSpan, totalSpentSpan, totalCommittedSpan, availableSpan);

        overviewContent.add(deptInfo, budgetSummary);
        overviewCard.add(overviewContent);
        contentContainer.add(overviewCard);
    }

    private VerticalLayout createBudgetSummaryItem(String label, BigDecimal amount, String className) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName(className);
        item.setAlignItems(FlexComponent.Alignment.CENTER);

        Span labelSpan = new Span(label);
        labelSpan.addClassName("summary-label");

        Span amountSpan = new Span(formatCurrency(amount));
        amountSpan.addClassName("summary-amount");

        // Add status indicators
        if ("Available".equals(label)) {
            if (amount.doubleValue() < 0) {
                amountSpan.addClassName("amount-negative");
                Icon warningIcon = new Icon(VaadinIcon.WARNING);
                warningIcon.addClassName("amount-warning-icon");
                item.add(labelSpan, amountSpan, warningIcon);
                return item;
            } else if (amount.doubleValue() < 50000000) { // Less than 50M UGX
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

            PdfFormXObject totalPagesPlaceholder = new PdfFormXObject(new com.itextpdf.kernel.geom.Rectangle(0, 0, 50, 12));

            String title = "BUDGET LIAISON OFFICER DASHBOARD REPORT";
            String subtitle = "Fiscal Year: " + safe(budget.getFinancialYear()) + "  |  Department: " + safe(dept.getDepartmentName());

            // Header (only page 1) + Footer (all pages)
            ImageData logo = loadLogo("/META-INF/resources/images/urclogo.png");

            pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
                    new BloHeaderFooterHandler(title, subtitle, fontRegular, fontBold, logo, totalPagesPlaceholder));

            try (Document doc = new Document(pdf)) {
                //doc.setMargins(MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);
                doc.setMargins(100f, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);

                // Meta
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
                addRow(summary, fontRegular, false, "Category ID", safe(dept.getCategoryId()), false);
                addRow(summary, fontRegular, true, "Total Budget", formatUGX(dept.getTotalBudget()), true);
                addRow(summary, fontRegular, false, "Total Spent", formatUGX(dept.getTotalSpent()), true);
                addRow(summary, fontRegular, true, "Total Committed", formatUGX(dept.getTotalCommitted()), true);
                addRow(summary, fontRegular, false, "Available Budget", formatUGX(dept.getAvailableBudget()), true);
                addRow(summary, fontRegular, true, "Budget Utilization", pct(dept.getSpentPercentage()), true);
                addRow(summary, fontRegular, false, "Department Status", safe(dept.getStatusText()), false);
                addRow(summary, fontRegular, true, "Number of Sections", String.valueOf(dept.getSectionCount()), false);

                doc.add(summary);

                // After first section, reduce top margin for subsequent pages
                doc.setMargins(MARGIN_OTHER_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);

                // SECTION ANALYSIS
                doc.add(sectionTitle("Section Budget Analysis", fontBold));
                List<SectionBudget> sections = safeList(budgetService.getDepartmentSections(dept.getDepartmentCode(), budget));

                if (!sections.isEmpty()) {
                    Table sectionTable = baseTable(new float[]{2.6f, 1.2f, 1.6f, 1.6f, 1.0f, 1.0f});
                    addHeader(sectionTable, fontBold,
                            "Section Name", "Code", "Allocated", "Spent", "Utilization", "Status");

                    boolean alt = false;
                    for (SectionBudget s : sections) {
                        Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
                        alt = !alt;

                        addBodyCell(sectionTable, fontRegular, safe(s.getSectionName()), bg, TextAlignment.LEFT);
                        addBodyCell(sectionTable, fontRegular, safe(s.getSectionCode()), bg, TextAlignment.CENTER);
                        addBodyCell(sectionTable, fontRegular, formatUGX(s.getAllocatedBudget()), bg, TextAlignment.RIGHT);
                        addBodyCell(sectionTable, fontRegular, formatUGX(s.getSpentAmount()), bg, TextAlignment.RIGHT);
                        addBodyCell(sectionTable, fontRegular, pct(s.getSpentPercentage()), bg, TextAlignment.RIGHT);

                        sectionTable.addCell(statusCell(fontRegular, safe(s.getStatus()), sectionStatusColor(s)));
                    }

                    doc.add(sectionTable);
                } else {
                    doc.add(new Paragraph("No section data available for this department.")
                            .setFont(fontRegular)
                            .setFontSize(10)
                            .setFontColor(ColorConstants.DARK_GRAY)
                            .setMarginBottom(10));
                }

                // CONTROLS
                doc.add(sectionTitle("Budget Controls Analysis", fontBold));

                Table controls = baseTable(new float[]{3, 2});
                addHeader(controls, fontBold, "Control Type", "Status");

                addRow(controls, fontRegular, false, "Budget Check Control", dept.isBudgetCheckEnabled() ? "ENABLED" : "DISABLED", false);
                addRow(controls, fontRegular, true, "Budget Stop Control", dept.isBudgetStopEnabled() ? "ENABLED" : "DISABLED", false);
                addRow(controls, fontRegular, false, "Posting Prohibited", dept.isPostingProhibited() ? "ENABLED" : "DISABLED", false);

                doc.add(controls);

                // KPI
                doc.add(sectionTitle("Department Key Performance Indicators", fontBold));

                BigDecimal spentPct = percentage(dept.getTotalSpent(), dept.getTotalBudget());
                BigDecimal availablePct = percentage(dept.getAvailableBudget(), dept.getTotalBudget());
                BigDecimal commitmentPct = percentage(dept.getTotalCommitted(), dept.getTotalBudget());
                BigDecimal efficiency = safe(dept.getTotalSpent()).compareTo(BigDecimal.ZERO) > 0
                        ? percentage(dept.getTotalBudget(), dept.getTotalSpent())
                        : BigDecimal.ZERO;

                Table kpi = baseTable(new float[]{3, 1.6f, 1.4f});
                addHeader(kpi, fontBold, "Indicator", "Value", "Status");

                addKpiRow(kpi, fontRegular, "Budget Utilization Rate", pct(spentPct),
                        spentPct.doubleValue() <= 85 ? "Good" : spentPct.doubleValue() <= 95 ? "Caution" : "Critical");

                addKpiRow(kpi, fontRegular, "Available Budget Ratio", pct(availablePct),
                        availablePct.doubleValue() >= 15 ? "Healthy" : availablePct.doubleValue() >= 5 ? "Low" : "Critical");

                addKpiRow(kpi, fontRegular, "Commitment Ratio", pct(commitmentPct),
                        commitmentPct.doubleValue() <= 20 ? "Good" : commitmentPct.doubleValue() <= 30 ? "Moderate" : "High");

                addKpiRow(kpi, fontRegular, "Budget Efficiency",
                        String.format(Locale.US, "%.1f%%", Math.min(efficiency.doubleValue(), 200)),
                        efficiency.doubleValue() >= 100 ? "Excellent" : efficiency.doubleValue() >= 80 ? "Good" : "Below Target");

                // Department status with shaded cell
                addBodyCell(kpi, fontRegular, "Department Status", ColorConstants.WHITE, TextAlignment.LEFT);
                addBodyCell(kpi, fontRegular, safe(dept.getStatusText()), ColorConstants.WHITE, TextAlignment.LEFT);
                kpi.addCell(statusCell(fontRegular, safe(getStatusDescription(dept.getStatusText())), deptStatusColor(dept)));

                doc.add(kpi);

                // CONTROL STATUS
                doc.add(sectionTitle("Budget Control Status", fontBold));

                Table controlStatus = baseTable(new float[]{2.2f, 1.1f, 2.7f});
                addHeader(controlStatus, fontBold, "Control Type", "Status", "Description");

                addControlStatusRow(controlStatus, fontRegular, "Budget Check",
                        dept.isBudgetCheckEnabled(), "Validates transactions against budget limits before processing",
                        dept.isBudgetCheckEnabled() ? softGreen() : COLOR_HEADER_BG);

                addControlStatusRow(controlStatus, fontRegular, "Budget Stop",
                        dept.isBudgetStopEnabled(), "Automatically prevents transactions when budget limits are exceeded",
                        dept.isBudgetStopEnabled() ? softPink() : COLOR_HEADER_BG);

                addControlStatusRow(controlStatus, fontRegular, "Posting Prohibited",
                        dept.isPostingProhibited(), "Completely blocks all financial postings and transactions",
                        dept.isPostingProhibited() ? softYellow() : COLOR_HEADER_BG);

                doc.add(controlStatus);

                // RECOMMENDATIONS
                doc.add(sectionTitle("Recommendations & Action Items", fontBold));
                doc.add(recommendationsBlock(dept, spentPct, fontRegular));

                // BLO INFO
                doc.add(sectionTitle("BLO Dashboard Information", fontBold));

                Table bloInfo = baseTable(new float[]{3, 2});
                addHeader(bloInfo, fontBold, "Information", "Details");

                addRow(bloInfo, fontRegular, false, "Report Generated By", safe(currentUserName), false);
                addRow(bloInfo, fontRegular, true, "Dashboard Access Level", "Budget Liaison Officer", false);
                addRow(bloInfo, fontRegular, false, "Fiscal Year", safe(budget.getFinancialYear()), false);
                addRow(bloInfo, fontRegular, true, "Department Focus", safe(dept.getDepartmentName()), false);
                addRow(bloInfo, fontRegular, false, "Report Type", "Departmental Budget Analysis", false);
                addRow(bloInfo, fontRegular, true, "Data Currency", "Uganda Shillings (UGX)", false);
                addRow(bloInfo, fontRegular, false, "Report Scope", "Department-level budget performance and controls", false);

                doc.add(bloInfo);

                // PENDING APPROVALS (best-effort)
                addPendingApprovalsSection(doc, pdf, fontBold, fontRegular, budget);

                // Footer note
                doc.add(new Paragraph("This report was generated automatically by the Budget Liaison Officer Dashboard. "
                        + "For questions or clarifications, please contact the Budget Management Team.")
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(14));

                // fill total pages
                writeTotalPages(pdf, totalPagesPlaceholder, fontRegular);
            }

            return baos.toByteArray();
        }
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
        return "UGX " + nf.format(a);
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
                        -> budget.getDepartmentCode().equalsIgnoreCase(filter)
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

            byte[] docxBytes = buildBloDashboardDocx(selectedBudget, selectedDepartment);

            String fileName = "BLO_Dashboard_Report_"
                    + safe(selectedDepartment.getDepartmentCode()) + "_"
                    + safe(selectedBudget.getFinancialYear()).replace("/", "-")
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                    + ".docx";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(docxBytes));
            resource.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            resource.setCacheTime(0);

            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration reg = ui.getSession().getResourceRegistry().registerResource(resource);
                ui.getPage().open(reg.getResourceUri().toString(), "_blank");
                showNotification("BLO Dashboard Word report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Unable to open Word report (no UI context).", NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Error generating Word report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private byte[] buildBloDashboardDocx(Budget budget, DepartmentBudget dept)
            throws IOException, InvalidFormatException {

        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // ---- Page setup / margins (twips: 1440 = 1 inch)
            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar mar = sectPr.addNewPgMar();
            mar.setTop(1100);
            mar.setBottom(900);
            mar.setLeft(900);
            mar.setRight(900);

            // ---- First-page header with logo + title + subtitle
            String title = "BUDGET LIAISON OFFICER DASHBOARD REPORT";
            String subtitle = "Fiscal Year: " + safe(budget.getFinancialYear())
                    + "  |  Department: " + safe(dept.getDepartmentName());

            addFirstPageHeader(doc,
                    "/META-INF/resources/images/urclogo.png",
                    "BUDGET LIAISON OFFICER DASHBOARD REPORT",
                    "Fiscal Year: " + safe(budget.getFinancialYear()) + "  |  Department: " + safe(dept.getDepartmentName()));

            // ---- Meta line
            addRightMeta(doc, "Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"))
                    + "  |  Generated by: " + safe(currentUserName));

            // ---- 1) Department Executive Summary
            addSectionHeader(doc, "DEPARTMENT EXECUTIVE SUMMARY");
            XWPFTable summary = doc.createTable(1, 2);
            styleTable(summary);
            setHeaderRow(summary.getRow(0), "Metric", "Amount / Value");

            addRow(summary, "Department Name", safe(dept.getDepartmentName()));
            addRow(summary, "Department Code", safe(dept.getDepartmentCode()));
            addRow(summary, "Category ID", safe(dept.getCategoryId()));
            addRow(summary, "Total Budget", formatUGX(dept.getTotalBudget()));
            addRow(summary, "Total Spent", formatUGX(dept.getTotalSpent()));
            addRow(summary, "Total Committed", formatUGX(dept.getTotalCommitted()));
            addRow(summary, "Available Budget", formatUGX(dept.getAvailableBudget()));
            addRow(summary, "Budget Utilization", pct(dept.getSpentPercentage()));
            addRow(summary, "Department Status", safe(dept.getStatusText()));
            addRow(summary, "Number of Sections", String.valueOf(dept.getSectionCount()));

            addSpacer(doc, 1);

            // ---- 2) Section Budget Analysis
            addSectionHeader(doc, "SECTION BUDGET ANALYSIS");
            List<SectionBudget> sections = safeList(budgetService.getDepartmentSections(dept.getDepartmentCode(), budget));

            if (!sections.isEmpty()) {
                XWPFTable secTable = doc.createTable(1, 6);
                styleTable(secTable);
                setHeaderRow(secTable.getRow(0),
                        "Section Name", "Code", "Allocated", "Spent", "Utilization", "Status");

                for (SectionBudget s : sections) {
                    XWPFTableRow r = secTable.createRow();
                    setCell(r.getCell(0), safe(s.getSectionName()), false, ParagraphAlignment.LEFT);
                    setCell(r.getCell(1), safe(s.getSectionCode()), false, ParagraphAlignment.CENTER);
                    setCell(r.getCell(2), formatUGX(s.getAllocatedBudget()), false, ParagraphAlignment.RIGHT);
                    setCell(r.getCell(3), formatUGX(s.getSpentAmount()), false, ParagraphAlignment.RIGHT);
                    setCell(r.getCell(4), pct(s.getSpentPercentage()), false, ParagraphAlignment.RIGHT);

                    String status = safe(s.getStatus());
                    setCell(r.getCell(5), status, false, ParagraphAlignment.CENTER);
                    shadeCell(r.getCell(5), sectionStatusHex(s));
                }
            } else {
                addMutedParagraph(doc, "No section data available for this department.");
            }

            addSpacer(doc, 1);

            // ---- 3) Budget Controls Analysis
            addSectionHeader(doc, "BUDGET CONTROLS ANALYSIS");
            XWPFTable controls = doc.createTable(1, 2);
            styleTable(controls);
            setHeaderRow(controls.getRow(0), "Control Type", "Status");

            addRow(controls, "Budget Check Control", dept.isBudgetCheckEnabled() ? "ENABLED" : "DISABLED");
            addRow(controls, "Budget Stop Control", dept.isBudgetStopEnabled() ? "ENABLED" : "DISABLED");
            addRow(controls, "Posting Prohibited", dept.isPostingProhibited() ? "ENABLED" : "DISABLED");

            addSpacer(doc, 1);

            // ---- 4) KPIs
            addSectionHeader(doc, "DEPARTMENT KEY PERFORMANCE INDICATORS");

            BigDecimal spentPct = percentage(dept.getTotalSpent(), dept.getTotalBudget());
            BigDecimal availablePct = percentage(dept.getAvailableBudget(), dept.getTotalBudget());
            BigDecimal commitmentPct = percentage(dept.getTotalCommitted(), dept.getTotalBudget());
            BigDecimal efficiency = safe(dept.getTotalSpent()).compareTo(BigDecimal.ZERO) > 0
                    ? percentage(dept.getTotalBudget(), dept.getTotalSpent())
                    : BigDecimal.ZERO;

            XWPFTable kpi = doc.createTable(1, 3);
            styleTable(kpi);
            setHeaderRow(kpi.getRow(0), "Indicator", "Value", "Status");

            addKpiRow(kpi, "Budget Utilization Rate", pct(spentPct),
                    spentPct.doubleValue() <= 85 ? "Good" : spentPct.doubleValue() <= 95 ? "Caution" : "Critical");

            addKpiRow(kpi, "Available Budget Ratio", pct(availablePct),
                    availablePct.doubleValue() >= 15 ? "Healthy" : availablePct.doubleValue() >= 5 ? "Low" : "Critical");

            addKpiRow(kpi, "Commitment Ratio", pct(commitmentPct),
                    commitmentPct.doubleValue() <= 20 ? "Good" : commitmentPct.doubleValue() <= 30 ? "Moderate" : "High");

            addKpiRow(kpi, "Budget Efficiency",
                    String.format(Locale.US, "%.1f%%", Math.min(efficiency.doubleValue(), 200)),
                    efficiency.doubleValue() >= 100 ? "Excellent" : efficiency.doubleValue() >= 80 ? "Good" : "Below Target");

            // Department status row with shaded status cell
            XWPFTableRow st = kpi.createRow();
            setCell(st.getCell(0), "Department Status", false, ParagraphAlignment.LEFT);
            setCell(st.getCell(1), safe(dept.getStatusText()), false, ParagraphAlignment.LEFT);
            setCell(st.getCell(2), safe(getStatusDescription(dept.getStatusText())), false, ParagraphAlignment.LEFT);
            shadeCell(st.getCell(2), deptStatusHex(dept));

            addSpacer(doc, 1);

            // ---- 5) Budget Control Status
            addSectionHeader(doc, "BUDGET CONTROL STATUS");
            XWPFTable cs = doc.createTable(1, 3);
            styleTable(cs);
            setHeaderRow(cs.getRow(0), "Control Type", "Status", "Description");

            addControlStatusRow(cs, "Budget Check", dept.isBudgetCheckEnabled(),
                    "Validates transactions against budget limits before processing",
                    dept.isBudgetCheckEnabled() ? "DCF5E6" : "F2F2F2");

            addControlStatusRow(cs, "Budget Stop", dept.isBudgetStopEnabled(),
                    "Automatically prevents transactions when budget limits are exceeded",
                    dept.isBudgetStopEnabled() ? "FFDCE6" : "F2F2F2");

            addControlStatusRow(cs, "Posting Prohibited", dept.isPostingProhibited(),
                    "Completely blocks all financial postings and transactions",
                    dept.isPostingProhibited() ? "FFF5D2" : "F2F2F2");

            addSpacer(doc, 1);

            // ---- 6) Recommendations
            addSectionHeader(doc, "RECOMMENDATIONS & ACTION ITEMS");
            addRecommendationsBox(doc, buildDeptRecommendations(dept, spentPct));

            addSpacer(doc, 1);

            // ---- 7) BLO Info
            addSectionHeader(doc, "BLO DASHBOARD INFORMATION");
            XWPFTable info = doc.createTable(1, 2);
            styleTable(info);
            setHeaderRow(info.getRow(0), "Information", "Details");

            addRow(info, "Report Generated By", safe(currentUserName) + " (" + safe(currentUserRole) + ")");
            addRow(info, "Dashboard Access Level", "Budget Liaison Officer");
            addRow(info, "Fiscal Year", safe(budget.getFinancialYear()));
            addRow(info, "Department Focus", safe(dept.getDepartmentName()));
            addRow(info, "Report Type", "Departmental Budget Analysis");
            addRow(info, "Data Currency", "Uganda Shillings (UGX)");
            addRow(info, "Report Scope", "Department-level budget performance and controls");

            addSpacer(doc, 1);

            // ---- 8) Pending Approvals (best-effort)
            addPendingApprovalsDocx(doc, budget);

            // Footer note
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

// ---------- Table helpers ----------
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
