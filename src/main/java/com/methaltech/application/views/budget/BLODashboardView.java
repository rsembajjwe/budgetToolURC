package com.methaltech.application.views.budget;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
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
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.SectionBudget;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.actual.utilityActuals;
import com.methaltech.application.views.approvals.ApprovalDashboardCards;
import com.methaltech.application.views.approvals.ApprovalRequestForm;
import com.methaltech.application.views.approvals.ApprovalWorkflowGrid;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "blo-dashboard", layout = MainLayout.class)
@PageTitle("Budget Liaison Officer Dashboard")
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
            BudgetItemsService budgetItemsService, CoaService sampleCoaService,UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
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
        this.sampleSALFLDGService=sampleSALFLDGService;

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

        Button exportButton = new Button("Export", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addClassName("blo-export-button");
        exportButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportButton.addClickListener(e -> {
            generateBLOPDFReport();
            //showNotification("Export functionality coming soon", NotificationVariant.LUMO_PRIMARY);
        });

        Button alertsButton = new Button("Alerts", new Icon(VaadinIcon.BELL));
        alertsButton.addClassName("blo-alerts-button");
        alertsButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        alertsButton.addClickListener(e -> {
            showNotification("Budget alerts panel coming soon", NotificationVariant.LUMO_CONTRAST);
        });

        rightSide.add(newRequestButton, refreshButton, exportButton, alertsButton);

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

            // Department Overview Header
            createDepartmentOverview();

            // Main dashboard grid
            HorizontalLayout dashboardGrid = new HorizontalLayout();
            dashboardGrid.setWidthFull();
            dashboardGrid.setSpacing(true);
            dashboardGrid.setPadding(false);
            dashboardGrid.addClassName("blo-dashboard-grid");

            // Left column - Section cards and controls
            VerticalLayout leftColumn = new VerticalLayout();
            leftColumn.setSpacing(true);
            leftColumn.setPadding(false);
            leftColumn.setFlexGrow(2);
            leftColumn.addClassName("blo-left-column");

            // Section Budget Cards
            DepartmentSectionCards sectionCards = new DepartmentSectionCards(selectedDepartment, budgetService, selectedBudget,sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService);
            sectionCards.addClassName("blo-section-cards");

            // Budget Control Panel
            BudgetControlPanel controlPanel = new BudgetControlPanel(selectedDepartment, controlService, selectedBudget, user);
            controlPanel.addClassName("blo-control-panel");

            leftColumn.add(sectionCards, controlPanel);

            // Right column - Charts and alerts
            VerticalLayout rightColumn = new VerticalLayout();
            rightColumn.setSpacing(true);
            rightColumn.setPadding(false);
            rightColumn.setFlexGrow(1);
            rightColumn.addClassName("blo-right-column");

            // Section Budget Chart
            SectionBudgetChart budgetChart = new SectionBudgetChart(selectedDepartment);
            budgetChart.addClassName("blo-budget-chart");

            // Budget Alerts
            BudgetAlerts budgetAlerts = new BudgetAlerts(selectedDepartment);
            budgetAlerts.addClassName("blo-budget-alerts");

            //rightColumn.add(budgetAlerts);
            rightColumn.add(budgetChart, budgetAlerts);

            dashboardGrid.add(leftColumn, rightColumn);
            contentContainer.add(dashboardGrid);

        } catch (Exception e) {
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
            
            utils = new utilityActuals(selectedBudget, sampleCoaService,sampleUrcDeptSectionAnlDimbgtService,sampleSALFLDGService,sectionCodes);
            utils.createTransactionsDialog2(overviewContent);
        });

        budgetSummary.add(totalSpan, totalSpentSpan, totalCommittedSpan, availableSpan);

        overviewContent.add(deptInfo, budgetSummary);
        overviewCard.add(overviewContent);
        contentContainer.add(overviewCard);
    }

    private VerticalLayout createBudgetSummaryItem(String label, double amount, String className) {
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
            if (amount < 0) {
                amountSpan.addClassName("amount-negative");
                Icon warningIcon = new Icon(VaadinIcon.WARNING);
                warningIcon.addClassName("amount-warning-icon");
                item.add(labelSpan, amountSpan, warningIcon);
                return item;
            } else if (amount < 50000000) { // Less than 50M UGX
                amountSpan.addClassName("amount-low");
            }
        }

        item.add(labelSpan, amountSpan);
        return item;
    }

    private String formatCurrency(double amount) {
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

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);

            // Create fonts
            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Currency formatter
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

            // Title Section
            Paragraph title = new Paragraph("BUDGET LIAISON OFFICER DASHBOARD REPORT")
                    .setFont(titleFont)
                    .setFontSize(15)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            // Subtitle with fiscal year and department
            Paragraph subtitle = new Paragraph("Fiscal Year: " + selectedBudget.getFinancialYear()
                    + " | Department: " + selectedDepartment.getDepartmentName())
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitle);

            // Report generation info
            Paragraph reportInfo = new Paragraph("Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"))
                    + " | Generated by: " + currentUserName + " (" + currentUserRole + ")")
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20);
            document.add(reportInfo);

            // Department Executive Summary Section
            Paragraph summaryHeader = new Paragraph("DEPARTMENT EXECUTIVE SUMMARY")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginBottom(10);
            document.add(summaryHeader);

            // Department Summary Table
            Table deptSummaryTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            // Department summary headers
            deptSummaryTable.addHeaderCell(new Cell().add(new Paragraph("Metric")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deptSummaryTable.addHeaderCell(new Cell().add(new Paragraph("Amount/Value")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Department summary data
            deptSummaryTable.addCell("Department Name").addCell(selectedDepartment.getDepartmentName());
            deptSummaryTable.addCell("Department Code").addCell(selectedDepartment.getDepartmentCode());
            deptSummaryTable.addCell("Category ID").addCell(selectedDepartment.getCategoryId());
            deptSummaryTable.addCell("Total Budget").addCell(formatCurrency(selectedDepartment.getTotalBudget()));
            deptSummaryTable.addCell("Total Spent").addCell(formatCurrency(selectedDepartment.getTotalSpent()));
            deptSummaryTable.addCell("Total Committed").addCell(formatCurrency(selectedDepartment.getTotalCommitted()));
            deptSummaryTable.addCell("Available Budget").addCell(formatCurrency(selectedDepartment.getAvailableBudget()));
            deptSummaryTable.addCell("Budget Utilization").addCell(String.format("%.1f%%", selectedDepartment.getSpentPercentage()));
            deptSummaryTable.addCell("Department Status").addCell(selectedDepartment.getStatusText());
            deptSummaryTable.addCell("Number of Sections").addCell(String.valueOf(selectedDepartment.getSectionCount()));

            document.add(deptSummaryTable);

            // Section Budget Analysis
            document.add(new Paragraph("SECTION BUDGET ANALYSIS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Get section data for the department
            List<SectionBudget> sections = budgetService.getDepartmentSections(selectedDepartment.getDepartmentCode(), selectedBudget);

            if (!sections.isEmpty()) {
                // Section Table
                Table sectionTable = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 1, 1}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginBottom(20);

                // Section table headers
                sectionTable.addHeaderCell(new Cell().add(new Paragraph("Section Name")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                sectionTable.addHeaderCell(new Cell().add(new Paragraph("Section Code")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                sectionTable.addHeaderCell(new Cell().add(new Paragraph("Allocated Budget")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                sectionTable.addHeaderCell(new Cell().add(new Paragraph("Spent Amount")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                sectionTable.addHeaderCell(new Cell().add(new Paragraph("Utilization")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                sectionTable.addHeaderCell(new Cell().add(new Paragraph("Status")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

                // Section data
                for (SectionBudget section : sections) {
                    sectionTable.addCell(section.getSectionName());
                    sectionTable.addCell(section.getSectionCode());
                    sectionTable.addCell(formatCurrency(section.getAllocatedBudget()));
                    sectionTable.addCell(formatCurrency(section.getSpentAmount()));
                    sectionTable.addCell(String.format("%.1f%%", section.getSpentPercentage()));

                    Cell statusCell = new Cell().add(new Paragraph(section.getStatus()));
                    if (section.isOverBudget()) {
                        statusCell.setBackgroundColor(ColorConstants.PINK);
                    } else if (section.isCritical()) {
                        statusCell.setBackgroundColor(ColorConstants.YELLOW);
                    } else if (section.isNearLimit()) {
                        statusCell.setBackgroundColor(ColorConstants.MAGENTA);
                    } else {
                        statusCell.setBackgroundColor(ColorConstants.GREEN);
                    }
                    sectionTable.addCell(statusCell);
                }

                document.add(sectionTable);
            } else {
                document.add(new Paragraph("No section data available for this department.")
                        .setFont(normalFont)
                        .setFontSize(12)
                        .setMarginBottom(20));
            }

            // Budget Controls Analysis Section
            document.add(new Paragraph("BUDGET CONTROLS ANALYSIS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Budget Controls Table
            Table controlsTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            // Controls table headers
            controlsTable.addHeaderCell(new Cell().add(new Paragraph("Control Type")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            controlsTable.addHeaderCell(new Cell().add(new Paragraph("Status")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Controls data
            controlsTable.addCell("Budget Check Control");
            controlsTable.addCell(selectedDepartment.isBudgetCheckEnabled() ? "ENABLED" : "DISABLED");

            controlsTable.addCell("Budget Stop Control");
            controlsTable.addCell(selectedDepartment.isBudgetStopEnabled() ? "ENABLED" : "DISABLED");

            controlsTable.addCell("Posting Prohibited");
            controlsTable.addCell(selectedDepartment.isPostingProhibited() ? "ENABLED" : "DISABLED");

            document.add(controlsTable);

            // Key Performance Indicators Section
            document.add(new Paragraph("DEPARTMENT KEY PERFORMANCE INDICATORS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // KPI Table
            Table kpiTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            kpiTable.addHeaderCell(new Cell().add(new Paragraph("Indicator")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            kpiTable.addHeaderCell(new Cell().add(new Paragraph("Value")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            kpiTable.addHeaderCell(new Cell().add(new Paragraph("Status")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Calculate KPIs
            double spentPercentage = selectedDepartment.getSpentPercentage();
            double availablePercentage = (selectedDepartment.getAvailableBudget() / selectedDepartment.getTotalBudget()) * 100;
            double commitmentRatio = (selectedDepartment.getTotalCommitted() / selectedDepartment.getTotalBudget()) * 100;
            double efficiency = spentPercentage > 0 ? (selectedDepartment.getTotalBudget() / selectedDepartment.getTotalSpent()) * 100 : 0;

            kpiTable.addCell("Budget Utilization Rate");
            kpiTable.addCell(String.format("%.1f%%", spentPercentage));
            kpiTable.addCell(spentPercentage <= 85 ? "Good" : spentPercentage <= 95 ? "Caution" : "Critical");

            kpiTable.addCell("Available Budget Ratio");
            kpiTable.addCell(String.format("%.1f%%", availablePercentage));
            kpiTable.addCell(availablePercentage >= 15 ? "Healthy" : availablePercentage >= 5 ? "Low" : "Critical");

            kpiTable.addCell("Commitment Ratio");
            kpiTable.addCell(String.format("%.1f%%", commitmentRatio));
            kpiTable.addCell(commitmentRatio <= 20 ? "Good" : commitmentRatio <= 30 ? "Moderate" : "High");

            kpiTable.addCell("Budget Efficiency");
            kpiTable.addCell(String.format("%.1f%%", Math.min(efficiency, 200)));
            kpiTable.addCell(efficiency >= 100 ? "Excellent" : efficiency >= 80 ? "Good" : "Below Target");

            kpiTable.addCell("Department Status");
            kpiTable.addCell(selectedDepartment.getStatusText());
            Cell statusCell = new Cell().add(new Paragraph(getStatusDescription(selectedDepartment.getStatusText())));
            if (selectedDepartment.getStatusText().equals("Over Budget")) {
                statusCell.setBackgroundColor(ColorConstants.PINK);
            } else if (selectedDepartment.getStatusText().equals("Critical")) {
                statusCell.setBackgroundColor(ColorConstants.YELLOW);
            } else if (selectedDepartment.getStatusText().equals("Near Limit")) {
                statusCell.setBackgroundColor(ColorConstants.YELLOW);
            } else {
                statusCell.setBackgroundColor(ColorConstants.GREEN);
            }
            kpiTable.addCell(statusCell);

            document.add(kpiTable);

            // Budget Control Status Section
            document.add(new Paragraph("BUDGET CONTROL STATUS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Control Status Table
            Table controlStatusTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 3}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            controlStatusTable.addHeaderCell(new Cell().add(new Paragraph("Control Type")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            controlStatusTable.addHeaderCell(new Cell().add(new Paragraph("Status")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            controlStatusTable.addHeaderCell(new Cell().add(new Paragraph("Description")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Budget Check Control
            controlStatusTable.addCell("Budget Check");
            Cell budgetCheckCell = new Cell().add(new Paragraph(selectedDepartment.isBudgetCheckEnabled() ? "ENABLED" : "DISABLED"));
            if (selectedDepartment.isBudgetCheckEnabled()) {
                budgetCheckCell.setBackgroundColor(ColorConstants.GREEN);
            } else {
                budgetCheckCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            }
            controlStatusTable.addCell(budgetCheckCell);
            controlStatusTable.addCell("Validates transactions against budget limits before processing");

            // Budget Stop Control
            controlStatusTable.addCell("Budget Stop");
            Cell budgetStopCell = new Cell().add(new Paragraph(selectedDepartment.isBudgetStopEnabled() ? "ENABLED" : "DISABLED"));
            if (selectedDepartment.isBudgetStopEnabled()) {
                budgetStopCell.setBackgroundColor(ColorConstants.PINK);
            } else {
                budgetStopCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            }
            controlStatusTable.addCell(budgetStopCell);
            controlStatusTable.addCell("Automatically prevents transactions when budget limits are exceeded");

            // Posting Prohibited Control
            controlStatusTable.addCell("Posting Prohibited");
            Cell postingCell = new Cell().add(new Paragraph(selectedDepartment.isPostingProhibited() ? "ENABLED" : "DISABLED"));
            if (selectedDepartment.isPostingProhibited()) {
                postingCell.setBackgroundColor(ColorConstants.YELLOW);
            } else {
                postingCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            }
            controlStatusTable.addCell(postingCell);
            controlStatusTable.addCell("Completely blocks all financial postings and transactions");

            document.add(controlStatusTable);

            // Recommendations & Action Items Section
            document.add(new Paragraph("RECOMMENDATIONS & ACTION ITEMS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Generate recommendations based on department data
            StringBuilder recommendations = new StringBuilder();

            if (spentPercentage > 100) {
                recommendations.append("• URGENT: Department has exceeded budget allocation by ")
                        .append(String.format("%.1f%%", spentPercentage - 100))
                        .append(". Immediate corrective action required.\n");
            } else if (spentPercentage > 95) {
                recommendations.append("• CRITICAL: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
            } else if (spentPercentage > 85) {
                recommendations.append("• CAUTION: Budget utilization above 85%. Monitor spending closely.\n");
            } else if (spentPercentage < 40) {
                recommendations.append("• LOW UTILIZATION: Budget utilization below 40%. Consider accelerating planned activities.\n");
            } else {
                recommendations.append("• Budget utilization is within acceptable range (40-85%).\n");
            }

            if (selectedDepartment.getAvailableBudget() < 0) {
                recommendations.append("• CRITICAL: Available budget is negative. Budget reallocation required.\n");
            } else if (selectedDepartment.getAvailableBudget() < 50_000_000) {
                recommendations.append("• WARNING: Available budget below UGX 50M threshold. Plan remaining expenditures carefully.\n");
            }

            if (selectedDepartment.getTotalCommitted() > selectedDepartment.getAvailableBudget()) {
                recommendations.append("• ALERT: Committed amounts exceed available budget. Review commitments.\n");
            }

            // Budget controls recommendations
            if (!selectedDepartment.isBudgetCheckEnabled() && spentPercentage > 80) {
                recommendations.append("• RECOMMENDATION: Enable Budget Check control for better spending monitoring.\n");
            }

            if (!selectedDepartment.isBudgetStopEnabled() && spentPercentage > 90) {
                recommendations.append("• RECOMMENDATION: Consider enabling Budget Stop control to prevent overspending.\n");
            }

            if (selectedDepartment.isPostingProhibited()) {
                recommendations.append("• ACTION REQUIRED: Posting is currently prohibited. Review and lift restriction if appropriate.\n");
            }

            if (recommendations.length() == 0) {
                recommendations.append("• Department budget performance is satisfactory. Continue monitoring and maintain current practices.");
            }

            Paragraph recommendationsText = new Paragraph(recommendations.toString())
                    .setFont(normalFont)
                    .setFontSize(11)
                    .setMarginBottom(20);
            document.add(recommendationsText);

            // BLO Specific Information Section
            document.add(new Paragraph("BLO DASHBOARD INFORMATION")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // BLO Info Table
            Table bloTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            bloTable.addHeaderCell(new Cell().add(new Paragraph("Information")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            bloTable.addHeaderCell(new Cell().add(new Paragraph("Details")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            bloTable.addCell("Report Generated By").addCell(currentUserName + " (" + currentUserRole + ")");
            bloTable.addCell("Dashboard Access Level").addCell("Budget Liaison Officer");
            bloTable.addCell("Fiscal Year").addCell(selectedBudget.getFinancialYear());
            bloTable.addCell("Department Focus").addCell(selectedDepartment.getDepartmentName());
            bloTable.addCell("Report Type").addCell("Departmental Budget Analysis");
            bloTable.addCell("Data Currency").addCell("Uganda Shillings (UGX)");
            bloTable.addCell("Report Scope").addCell("Department-level budget performance and controls");

            document.add(bloTable);

            // Approval Workflow Status (if available)
            try {
                List<BudgetApprovals> pendingApprovals = approvalsService.getPendingApprovalsByRole(currentUserRole)
                        .stream()
                        .filter(approval -> approval.getBudget().getId().equals(selectedBudget.getId()))
                        .toList();

                if (!pendingApprovals.isEmpty()) {
                    document.add(new Paragraph("PENDING APPROVAL REQUESTS")
                            .setFont(headerFont)
                            .setFontSize(13)
                            .setMarginTop(20)
                            .setMarginBottom(10));

                    // Approvals Table
                    Table approvalsTable = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 1, 1}))
                            .setWidth(UnitValue.createPercentValue(100))
                            .setMarginBottom(20);

                    approvalsTable.addHeaderCell(new Cell().add(new Paragraph("Request ID")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                    approvalsTable.addHeaderCell(new Cell().add(new Paragraph("Department")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                    approvalsTable.addHeaderCell(new Cell().add(new Paragraph("Request Type")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                    approvalsTable.addHeaderCell(new Cell().add(new Paragraph("Amount")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
                    approvalsTable.addHeaderCell(new Cell().add(new Paragraph("Priority")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

                    for (BudgetApprovals approval : pendingApprovals.stream().limit(10).toList()) {
                        approvalsTable.addCell(approval.getRequestId());
                        approvalsTable.addCell(approval.getDepartmentName());
                        approvalsTable.addCell(approval.getRequestType().getDisplayName());
                        approvalsTable.addCell(formatCurrency(approval.getRequestedAmount()));

                        Cell priorityCell = new Cell().add(new Paragraph(approval.getPriorityLevel().getDisplayName()));
                        if (approval.getPriorityLevel().name().equals("HIGH") || approval.getPriorityLevel().name().equals("URGENT")) {
                            priorityCell.setBackgroundColor(ColorConstants.PINK);
                        } else if (approval.getPriorityLevel().name().equals("MEDIUM")) {
                            priorityCell.setBackgroundColor(ColorConstants.YELLOW);
                        } else {
                            priorityCell.setBackgroundColor(ColorConstants.GREEN);
                        }
                        approvalsTable.addCell(priorityCell);
                    }

                    document.add(approvalsTable);
                }
            } catch (Exception e) {
                // If approvals service is not available, skip this section
                document.add(new Paragraph("Approval workflow information not available.")
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setMarginBottom(10));
            }

            // Footer
            Paragraph footer = new Paragraph("This report was generated automatically by the Budget Liaison Officer Dashboard. "
                    + "For questions or clarifications, please contact the Budget Management Team.")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);

            document.close();

            // Create download
            String fileName = "BLO_Dashboard_Report_" + selectedDepartment.getDepartmentCode() + "_"
                    + selectedBudget.getFinancialYear().replace("/", "-")
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(baos.toByteArray()));
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);

            // Trigger download
            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration registration = ui.getSession()
                        .getResourceRegistry()
                        .registerResource(resource);

                String url = registration.getResourceUri().toString();
                ui.getPage().open(url, "_blank");   // pass URL, not the StreamResource
                showNotification("BLO Dashboard PDF report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                // If you're on a background thread, get a reference to UI and use ui.access(...)
                // uiRef.access(() -> { ... open url ... });
            }

        } catch (Exception e) {
            showNotification("Error generating PDF report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
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

}
