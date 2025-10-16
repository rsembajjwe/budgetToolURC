package com.methaltech.application.views.requisition;

import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.RequisitionDataService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COABudgetInfo;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.RequisitionData.ProcMethods;
import com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionStatus;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.requisition.ActivitySelectionDialog.AccountSummary;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "blo-requisitions", layout = MainLayout.class)
@PageTitle("Requisition Management")
@SpringComponent
@UIScope
@RolesAllowed({"BLO", "ADMIN", "HOD", "CFO"})
public class BLORequisitionView extends VerticalLayout {

    private final RequisitionDataService requisitionService;
    private final BudgetService budgetService;
    private final OrganisationService organisationService;
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private final CoaService coaService;
    private final StockUnitMeasureService sampleStockUnitMeasureService;
    private final Urc_ActivitiesService activitiesService;
    private final BudgetItemsService budgetItemsService;
    private final UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService;
    private DeptSectionMergerService deptSectionMergerService;
    private final SALFLDGService sALFLDGService;
    private final OrganisationService fundsourceService;
    private final NumberFormat currencyFormat;

    private Tabs navigationTabs;
    private VerticalLayout contentContainer;
    private TextField searchField;
    private ComboBox<RequisitionData.RequisitionStatus> statusFilter;
    private ComboBox<RequisitionData.PriorityLevel> priorityFilter;
    private ComboBox<Budget> budgetSelector;
    private Button newRequisitionButton;
    private Button refreshButton;
    private Button exportButton;
    private Button bulkApproveButton;
    private Button bulkRejectButton;

    private Grid<RequisitionData> requisitionGrid;
    private PPDARequisitionDialog requisitionDialog;
    private RequisitionApprovalDialog approvalDialog;
    private ActivitySelectionDialog activityDialog;

    // Dashboard components
    private Div dashboardOverview;
    private HorizontalLayout dashboardCards;
    private VerticalLayout statisticsSection;
    private VerticalLayout trendsSection;
    private VerticalLayout alertsSection;

    // Filter and search state
    private String currentSearchTerm = "";
    private RequisitionData.RequisitionStatus currentStatusFilter;
    private RequisitionData.PriorityLevel currentPriorityFilter;
    private ComboBox<UrcDeptSectionAnlDimbgt> sectionSelector;

    private String currentUserRole = "BLO";
    private String currentUserName = "BLO User";
    private Budget selectedBudget;
    private User user;
    private UrcDeptSectionAnlDimbgt selectedSection;

    // Tab references
    private Tab dashboardTab;
    private Tab myRequisitionsTab;
    private Tab pendingTab;
    private Tab allRequisitionsTab;
    private Tab analyticsTab;
    private Tab approvalWorkflowTab;

    RequisitionUtility req;

    @Autowired
    public BLORequisitionView(RequisitionDataService requisitionService, BudgetService budgetService,
            Urc_ActivitiesService activitiesService, BudgetItemsService budgetItemsService,
            StockUnitMeasureService sampleStockUnitMeasureService, RequisitionDataService acquisitionService,
            OrganisationService organisationService, AuthenticatedUser authenticatedUser, CoaService coaService, UserService userService,
            UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService, SALFLDGService sALFLDGService, DeptSectionMergerService deptSectionMergerService,
            OrganisationService fundsourceService) {
        this.requisitionService = requisitionService;
        this.budgetService = budgetService;
        this.organisationService = organisationService;
        this.authenticatedUser = authenticatedUser;
        this.coaService = coaService;
        this.userService = userService;
        this.sALFLDGService = sALFLDGService;
        this.urcDeptSectionAnlDimbgtService = urcDeptSectionAnlDimbgtService;
        this.sampleStockUnitMeasureService = sampleStockUnitMeasureService;
        this.activitiesService = activitiesService;
        this.budgetItemsService = budgetItemsService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.deptSectionMergerService = deptSectionMergerService;
        this.fundsourceService = fundsourceService;
        req = new RequisitionUtility(deptSectionMergerService);

        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        addClassName("blo-requisition-view");

        setUser();

        createHeader();

        createNavigationTabs();
        createMainContent();
        loadBudgetData();
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
        header.addClassName("requisition-header");
        header.setWidthFull();

        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        headerContent.setPadding(false);
        headerContent.setSpacing(false);

        // Left side - Title and budget selector
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSpacing(false);
        leftSide.setPadding(false);
        leftSide.setFlexGrow(1);

        H1 title = new H1("Requisition Management");
        title.addClassName("requisition-title");

        Span subtitle = new Span("Manage PPDA requisitions and procurement requests");
        subtitle.addClassName("requisition-subtitle");

        // Budget selector
        HorizontalLayout budgetLayout = new HorizontalLayout();
        budgetLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        budgetLayout.setSpacing(true);
        budgetLayout.setPadding(false);

        Span budgetLabel = new Span("Budget:");
        budgetLabel.addClassName("budget-label");

        budgetSelector = new ComboBox<>();
        budgetSelector.setPlaceholder("Select budget...");
        budgetSelector.setItemLabelGenerator(Budget::getFinancialYear);
        budgetSelector.addClassName("budget-selector");
        budgetSelector.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedBudget = e.getValue();
                refreshDashboardAndTrends();
                loadInitialData();
                setNewRequisitionDisplay();
            }
        });

        // Section selector
        sectionSelector = new ComboBox<>("Section");
        sectionSelector.setPlaceholder("Select section...");
        sectionSelector.addClassName("section-selector");
        sectionSelector.addValueChangeListener(e -> {
            selectedSection = e.getValue();
            refreshDashboardAndTrends();
            loadInitialData();
            setNewRequisitionDisplay();
        });

        budgetLayout.add(budgetLabel, budgetSelector, sectionSelector);
        leftSide.add(title, subtitle, budgetLayout);

        // Right side - Search and actions
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);

        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Search requisitions...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("requisition-search");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> {
            currentSearchTerm = e.getValue();
            filterRequisitions();
        });

        // Status filter
        statusFilter = new ComboBox<>("Status");
        statusFilter.setItems(RequisitionData.RequisitionStatus.values());
        statusFilter.setItemLabelGenerator(RequisitionData.RequisitionStatus::getDisplayName);
        statusFilter.addClassName("status-filter");
        statusFilter.addValueChangeListener(e -> {
            currentStatusFilter = e.getValue();
            filterRequisitions();
        });

        // Priority filter
        priorityFilter = new ComboBox<>("Priority");
        priorityFilter.setItems(RequisitionData.PriorityLevel.values());
        priorityFilter.setItemLabelGenerator(RequisitionData.PriorityLevel::getDisplayName);
        priorityFilter.addClassName("priority-filter");
        priorityFilter.addValueChangeListener(e -> {
            currentPriorityFilter = e.getValue();
            filterRequisitions();
        });

        // New requisition button
        newRequisitionButton = new Button("New Requisition", new Icon(VaadinIcon.PLUS));
        newRequisitionButton.addClassName("new-requisition-button");
        newRequisitionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newRequisitionButton.addClickListener(e -> showNewRequisitionDialog());

        // Refresh button
        refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClassName("refresh-button");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        refreshButton.addClickListener(e -> {
            refreshCurrentView();
            showNotification("Data refreshed successfully", NotificationVariant.LUMO_SUCCESS);
        });

        // Export button
        exportButton = new Button("Export", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addClassName("export-button");
        exportButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportButton.addClickListener(e -> {
            exportRequisitions();
        });

        rightSide.add(searchField, statusFilter, priorityFilter, newRequisitionButton, refreshButton, exportButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void setNewRequisitionDisplay() {
        if (selectedSection == null || selectedBudget == null) {
            newRequisitionButton.setEnabled(false);
        } else {
            newRequisitionButton.setEnabled(true);
        }
    }

    private void createNavigationTabs() {
        navigationTabs = new Tabs();
        navigationTabs.addClassName("requisition-navigation");
        navigationTabs.setWidthFull();

        dashboardTab = new Tab(new Icon(VaadinIcon.DASHBOARD), new Span("Dashboard"));
        myRequisitionsTab = new Tab(new Icon(VaadinIcon.FILE_TEXT), new Span("My Requisitions"));
        pendingTab = new Tab(new Icon(VaadinIcon.CLOCK), new Span("Pending Approval"));
        allRequisitionsTab = new Tab(new Icon(VaadinIcon.LIST), new Span("All Requisitions"));
        analyticsTab = new Tab(new Icon(VaadinIcon.CHART), new Span("Analytics"));
        approvalWorkflowTab = new Tab(new Icon(VaadinIcon.FORM), new Span("Approval Workflow"));

        dashboardTab.addClassName("nav-tab");
        myRequisitionsTab.addClassName("nav-tab");
        pendingTab.addClassName("nav-tab");
        allRequisitionsTab.addClassName("nav-tab");
        analyticsTab.addClassName("nav-tab");
        approvalWorkflowTab.addClassName("nav-tab");

        navigationTabs.add(dashboardTab, myRequisitionsTab, pendingTab, allRequisitionsTab, analyticsTab, approvalWorkflowTab);
        navigationTabs.setSelectedTab(dashboardTab);

        navigationTabs.addSelectedChangeListener(e -> {
            Tab selectedTab = e.getSelectedTab();
            if (selectedTab == dashboardTab) {
                showDashboard();
            } else if (selectedTab == myRequisitionsTab) {
                showMyRequisitions();
            } else if (selectedTab == pendingTab) {
                showPendingApprovals();
            } else if (selectedTab == allRequisitionsTab) {
                showAllRequisitions();
            } else if (selectedTab == analyticsTab) {
                showAnalytics();
            } else if (selectedTab == approvalWorkflowTab) {
                showApprovalWorkflow();
            }
        });

        add(navigationTabs);
    }

    private void createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("requisition-main-content");

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.addClassName("requisition-content-container");

        mainContent.add(contentContainer);
        add(mainContent);
    }

    private void loadBudgetData() {
        try {
            List<Budget> budgets = budgetService.getBudgets();
            budgetSelector.setItems(budgets);

            Optional<Budget> recentBudget = budgetService.getMostRecurrentBudget();
            if (recentBudget.isPresent()) {
                budgetSelector.setValue(recentBudget.get());
                selectedBudget = recentBudget.get();
            }
            loadSections();
        } catch (Exception e) {
            showNotification("Error loading budget data: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadInitialData() {
        showDashboard();
    }

    private void showDashboard() {
        contentContainer.removeAll();

        // Create dashboard overview
        createDashboardOverview();

        // Dashboard cards
        dashboardCards = createDashboardCards();
        dashboardCards.addClassName("requisition-dashboard-cards");

        // Statistics section
        //createStatisticsSection();
        // Trends section
        createTrendsSection();

        // Alerts section
        createAlertsSection();

        // Recent activity section
        VerticalLayout recentActivity = new VerticalLayout();
        recentActivity.setSpacing(true);
        recentActivity.setPadding(false);
        recentActivity.addClassName("recent-activity-section");

        H2 activityTitle = new H2("Recent Requisitions");
        activityTitle.addClassName("section-title");

        /*        List<RequisitionData> recentRequisitions = selectedBudget != null
        ? requisitionService.getRequisitionsByBudget(selectedBudget).stream().limit(10).toList()
        : List.of();*/
        List<RequisitionData> recentRequisitions = null;

        if ((user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) && selectedSection == null) {
            recentRequisitions = selectedBudget != null
                    ? requisitionService.getByBudgetAndStatusOrderByCreatedDateDesc(selectedBudget, RequisitionData.RequisitionStatus.APPROVED).stream().limit(10).toList()
                    : List.of();

        } else if ((user.getRoles().contains(Role.BLO))) {
            recentRequisitions = (selectedBudget != null && selectedSection != null) ? requisitionService
                    .getByBudgetStatuseAndDeptSectionOrderByCreatedDateDesc(selectedBudget, RequisitionData.RequisitionStatus.APPROVED, selectedSection)
                    .stream()
                    .limit(10)
                    .toList()
                    : List.of();
        } else if ((user.getRoles().contains(Role.HOD))) {
            Set<RequisitionData.RequisitionStatus> statuses = Set.of(RequisitionStatus.APPROVED, RequisitionStatus.SUBMITTED, RequisitionStatus.REJECTED, RequisitionStatus.UNDER_REVIEW);
            recentRequisitions = (selectedBudget != null && selectedSection != null)
                            ? requisitionService
                                    .getByBudgetStatusesAndDeptSectionsOrderByCreatedDateDesc(selectedBudget, statuses, user.getDeptsection())
                                    .stream()
                                    .limit(10)
                                    .toList()
                            : List.of();
        }
        Grid<RequisitionData> recentGrid = createRequisitionGrid(recentRequisitions);

        recentActivity.add(activityTitle, recentGrid);

        contentContainer.add(dashboardOverview, dashboardCards,
                trendsSection, alertsSection, recentActivity);
    }

    private void loadSections() {
        Set<UrcDeptSectionAnlDimbgt> deptsection = null;
        sectionSelector.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);

        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) {
            deptsection = urcDeptSectionAnlDimbgtService.getAllUrcSectionsAsSet();
            sectionSelector.setItems(deptsection);
        } else {
            deptsection = user.getDeptsection();
            sectionSelector.setItems(deptsection);
            //sectionSelector.setValue(deptsection.iterator().next());
            if (deptsection != null || !deptsection.isEmpty()) {
                sectionSelector.setValue(deptsection.iterator().next()); // or stream().findFirst().get()
            } else {
                sectionSelector.clear(); // or set a default value / disable selector
            }
        }
        sectionSelector.addValueChangeListener(e -> {
            selectedSection = e.getValue();
        });
        //sectionSelector.clear();
    }

    private void createDashboardOverview() {
        dashboardOverview = new Div();
        dashboardOverview.addClassName("dashboard-overview");

        VerticalLayout overviewContent = new VerticalLayout();
        overviewContent.setSpacing(true);
        overviewContent.setPadding(false);
        overviewContent.addClassName("overview-content");

        H2 overviewTitle = new H2("Requisition Management Overview");
        overviewTitle.addClassName("overview-title");

        Span overviewDescription = new Span("Comprehensive PPDA requisition management system for "
                + (selectedBudget != null ? selectedBudget.getFinancialYear() : "current budget"));
        overviewDescription.addClassName("overview-description");

        // Quick stats
        HorizontalLayout quickStats = createQuickStats();
        quickStats.addClassName("quick-stats");

        overviewContent.add(overviewTitle);
        dashboardOverview.add(overviewContent);
    }

    private HorizontalLayout createQuickStats() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.setSpacing(true);
        stats.setPadding(false);

        if (selectedBudget != null) {
            Long totalCount = requisitionService.getRequisitionsCountByBudget(selectedBudget);
            Double totalAmount = requisitionService.getTotalAmountByBudget(selectedBudget);
            Long pendingCount = requisitionService.getRequisitionsCountByStatus(RequisitionData.RequisitionStatus.SUBMITTED);

            stats.add(
                    createQuickStatItem("Total Requisitions", totalCount.toString(), VaadinIcon.FILE_TEXT, "#3b82f6"),
                    createQuickStatItem("Total Amount", formatCurrency(totalAmount), VaadinIcon.MONEY, "#10b981"),
                    createQuickStatItem("Pending Approval", pendingCount.toString(), VaadinIcon.CLOCK, "#f59e0b"),
                    createQuickStatItem("This Month", "12", VaadinIcon.CALENDAR, "#8b5cf6")
            );
        }

        return stats;
    }

    private Div createQuickStatItem(String label, String value, VaadinIcon iconType, String color) {
        Div item = new Div();
        item.addClassName("quick-stat-item");

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);
        content.setPadding(false);

        Div iconContainer = new Div();
        iconContainer.addClassName("stat-icon-container");
        iconContainer.getStyle().set("background-color", color + "20");
        iconContainer.getStyle().set("border", "1px solid " + color + "40");

        Icon icon = new Icon(iconType);
        icon.addClassName("stat-icon");
        icon.getStyle().set("color", color);
        iconContainer.add(icon);

        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);

        Span valueSpan = new Span(value);
        valueSpan.addClassName("stat-value");
        valueSpan.getStyle().set("color", color);

        Span labelSpan = new Span(label);
        labelSpan.addClassName("stat-label");

        textContent.add(valueSpan, labelSpan);
        content.add(iconContainer, textContent);
        item.add(content);

        return item;
    }

    private void createStatisticsSection() {
        statisticsSection = new VerticalLayout();
        statisticsSection.setSpacing(true);
        statisticsSection.setPadding(false);
        statisticsSection.addClassName("statistics-section");

        H3 statsTitle = new H3("Requisition Statistics");
        statsTitle.addClassName("section-title");

        // Create statistics grid
        HorizontalLayout statsGrid = new HorizontalLayout();
        statsGrid.setWidthFull();
        statsGrid.setSpacing(true);
        statsGrid.setPadding(false);

        // Monthly statistics
        VerticalLayout monthlyStats = createMonthlyStatistics();
        monthlyStats.addClassName("monthly-stats");

        // Type distribution
        VerticalLayout typeStats = createTypeDistribution();
        typeStats.addClassName("type-stats");

        // Status distribution
        VerticalLayout statusStats = createStatusDistribution();
        statusStats.addClassName("status-stats");

        statsGrid.add(monthlyStats, typeStats, statusStats);
        statisticsSection.add(statsTitle, statsGrid);
    }

    private VerticalLayout createMonthlyStatistics() {
        VerticalLayout monthly = new VerticalLayout();
        monthly.setSpacing(true);
        monthly.setPadding(true);
        monthly.addClassName("monthly-statistics");

        H4 title = new H4("Monthly Trends");
        title.addClassName("stats-card-title");

        // Mock monthly data - replace with actual service calls
        List<Object[]> monthlyData = requisitionService.getRequisitionsByMonth();

        for (Object[] data : monthlyData.stream().limit(6).toList()) {
            HorizontalLayout monthRow = new HorizontalLayout();
            monthRow.setWidthFull();
            monthRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            monthRow.setPadding(false);
            monthRow.setSpacing(false);

            Span monthLabel = new Span(data[0] + "/" + data[1]);
            monthLabel.addClassName("month-label");

            Span monthValue = new Span(data[2].toString());
            monthValue.addClassName("month-value");

            monthRow.add(monthLabel, monthValue);
            monthly.add(monthRow);
        }

        monthly.add(title);
        return monthly;
    }

    private VerticalLayout createTypeDistribution() {
        VerticalLayout types = new VerticalLayout();
        types.setSpacing(true);
        types.setPadding(true);
        types.addClassName("type-distribution");

        H4 title = new H4("Requisition Types");
        title.addClassName("stats-card-title");

        List<Object[]> typeData = requisitionService.getRequisitionsByType();

        for (Object[] data : typeData) {
            HorizontalLayout typeRow = new HorizontalLayout();
            typeRow.setWidthFull();
            typeRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            typeRow.setPadding(false);
            typeRow.setSpacing(false);

            Span typeLabel = new Span(data[0].toString());
            typeLabel.addClassName("type-label");

            Span typeValue = new Span(data[1].toString());
            typeValue.addClassName("type-value");

            typeRow.add(typeLabel, typeValue);
            types.add(typeRow);
        }

        types.add(title);
        return types;
    }

    private VerticalLayout createStatusDistribution() {
        VerticalLayout status = new VerticalLayout();
        status.setSpacing(true);
        status.setPadding(true);
        status.addClassName("status-distribution");

        H4 title = new H4("Status Distribution");
        title.addClassName("stats-card-title");

        // Get status counts
        for (RequisitionData.RequisitionStatus statusValue : RequisitionData.RequisitionStatus.values()) {
            Long count = requisitionService.getRequisitionsCountByStatus(statusValue);

            HorizontalLayout statusRow = new HorizontalLayout();
            statusRow.setWidthFull();
            statusRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            statusRow.setPadding(false);
            statusRow.setSpacing(false);

            Span statusLabel = new Span(statusValue.getDisplayName());
            statusLabel.addClassName("status-label");

            Span statusValue2 = new Span(count.toString());
            statusValue2.addClassName("status-value");
            //statusValue.getStyle().set("color", getStatusColor(statusValue));

            statusRow.add(statusLabel, statusValue2);
            status.add(statusRow);
        }

        status.add(title);
        return status;
    }

    private VerticalLayout createTrendsSection() {
        trendsSection = new VerticalLayout();
        trendsSection.setSpacing(true);
        trendsSection.setPadding(false);
        trendsSection.addClassName("trends-section");

        H3 trendsTitle = new H3("Procurement Trends & Insights");
        trendsTitle.addClassName("section-title");

        HorizontalLayout trendsGrid = new HorizontalLayout();
        trendsGrid.setWidthFull();
        trendsGrid.setSpacing(true);
        trendsGrid.setPadding(false);

        // Procurement method trends
        VerticalLayout methodTrends = createProcurementMethodTrends();
        methodTrends.addClassName("method-trends");

        // Amount trends
        VerticalLayout amountTrends = createAmountTrends();
        amountTrends.addClassName("amount-trends");

        // Performance metrics
        VerticalLayout performanceMetrics = createPerformanceMetrics();
        performanceMetrics.addClassName("performance-metrics");

        trendsGrid.add(methodTrends, amountTrends, performanceMetrics);
        trendsSection.add(trendsTitle, trendsGrid);
        return trendsSection;
    }

    private VerticalLayout createProcurementMethodTrends() {
        VerticalLayout trends = new VerticalLayout();
        trends.setSpacing(true);
        trends.setPadding(true);
        trends.addClassName("procurement-method-trends");

        H4 title = new H4("Procurement Methods");
        title.addClassName("trends-card-title");

        // Mock data for procurement methods
        //String[] methods = {"Micro Procurement", "Request for Quotations (RFQ)", "Restricted Bidding", "Open Bidding"};
        RequisitionData.ProcMethods[] methods = {ProcMethods.MICRO_PROCUREMENT, ProcMethods.RFQ, ProcMethods.RESTRICTED_BIDDING, ProcMethods.OPEN_BIDDING};
        String[] colors = {"#10b981", "#3b82f6", "#f59e0b", "#ef4444"};

        for (int i = 0; i < methods.length; i++) {
            //  if (selectedSection != null) {
            if ((user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) && sectionSelector.isEmpty()) {
                long count = requisitionService.countRequisitionsByBudgetAndProcMethod(selectedBudget, methods[i]);
                HorizontalLayout methodRow = createTrendRow(methods[i].getDisplayName(), String.valueOf(count), colors[i]);
                trends.add(methodRow);
            } else if ((user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) && !sectionSelector.isEmpty()) {
                long count = requisitionService.countRequisitionsByBudgetDeptSectionAndProcMethod(selectedBudget, selectedSection, methods[i]);
                HorizontalLayout methodRow = createTrendRow(methods[i].getDisplayName(), String.valueOf(count), colors[i]);
                trends.add(methodRow);
            } else if ((!user.getRoles().contains(Role.ADMIN) || !user.getRoles().contains(Role.PROCUREMENT)) && sectionSelector.isEmpty()) {

                long count = requisitionService.countRequisitionsByBudgetDeptSectionAndProcMethod(selectedBudget, user.getDeptsection().stream().toList().get(0), methods[i]);
                HorizontalLayout methodRow = createTrendRow(methods[i].getDisplayName(), String.valueOf(count), colors[i]);
                trends.add(methodRow);
            } else if ((!user.getRoles().contains(Role.ADMIN) || !user.getRoles().contains(Role.PROCUREMENT)) && !sectionSelector.isEmpty()) {
                long count = requisitionService.countRequisitionsByBudgetDeptSectionAndProcMethod(selectedBudget, selectedSection, methods[i]);
                HorizontalLayout methodRow = createTrendRow(methods[i].getDisplayName(), String.valueOf(count), colors[i]);
                trends.add(methodRow);
            }
            // }

        }

        trends.add(title);
        return trends;
    }

    private VerticalLayout createAmountTrends() {
        VerticalLayout trends = new VerticalLayout();
        trends.setSpacing(true);
        trends.setPadding(true);
        trends.addClassName("amount-trends");

        H4 title = new H4("Amount Ranges");
        title.addClassName("trends-card-title");

        // Mock data for amount ranges
        String[] ranges = {"< UGX 10M", "UGX 10M - 200M", "UGX 200M - 500M", "> UGX 500M"};
        RequisitionData.ProcMethods[] methods = {ProcMethods.MICRO_PROCUREMENT, ProcMethods.RFQ, ProcMethods.RESTRICTED_BIDDING, ProcMethods.OPEN_BIDDING};
        int[] counts = {32, 28, 15, 8};
        String[] colors = {"#10b981", "#3b82f6", "#f59e0b", "#ef4444"};

        for (int i = 0; i < methods.length; i++) {

            if ((user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) && sectionSelector.isEmpty()) {
                long count = requisitionService.countRequisitionsByBudgetAndProcMethod(selectedBudget, methods[i]);
                HorizontalLayout methodRow = createTrendRow(ranges[i], String.valueOf(count), colors[i]);
                trends.add(methodRow);
            } else if ((user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) && !sectionSelector.isEmpty()) {
                long count = requisitionService.countRequisitionsByBudgetDeptSectionAndProcMethod(selectedBudget, selectedSection, methods[i]);
                HorizontalLayout methodRow = createTrendRow(ranges[i], String.valueOf(count), colors[i]);
                trends.add(methodRow);
            } else if ((!user.getRoles().contains(Role.ADMIN) || !user.getRoles().contains(Role.PROCUREMENT)) && sectionSelector.isEmpty()) {

                long count = requisitionService.countRequisitionsByBudgetDeptSectionAndProcMethod(selectedBudget, user.getDeptsection().stream().toList().get(0), methods[i]);
                HorizontalLayout methodRow = createTrendRow(ranges[i], String.valueOf(count), colors[i]);
                trends.add(methodRow);
            } else if ((!user.getRoles().contains(Role.ADMIN) || !user.getRoles().contains(Role.PROCUREMENT)) && !sectionSelector.isEmpty()) {
                long count = requisitionService.countRequisitionsByBudgetDeptSectionAndProcMethod(selectedBudget, selectedSection, methods[i]);
                HorizontalLayout methodRow = createTrendRow(ranges[i], String.valueOf(count), colors[i]);
                trends.add(methodRow);
            }
        }

        trends.add(title);
        return trends;
    }

    private VerticalLayout createPerformanceMetrics() {
        VerticalLayout metrics = new VerticalLayout();
        metrics.setSpacing(true);
        metrics.setPadding(true);
        metrics.addClassName("performance-metrics");

        H4 title = new H4("Performance Metrics");
        title.addClassName("metrics-card-title");

        // Performance indicators
        metrics.add(
                createMetricRow("Avg. Processing Time", "3.2 days", "#3b82f6"),
                createMetricRow("Approval Rate", "94.5%", "#10b981"),
                createMetricRow("Budget Compliance", "98.1%", "#10b981"),
                createMetricRow("Vendor Satisfaction", "4.7/5", "#8b5cf6")
        );

        metrics.add(title);
        return metrics;
    }

    private HorizontalLayout createTrendRow(String label, String value, String color) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setPadding(false);
        row.setSpacing(false);
        row.addClassName("trend-row");

        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setSpacing(false);
        leftSide.setPadding(false);

        Div colorDot = new Div();
        colorDot.addClassName("trend-color-dot");
        colorDot.getStyle().set("background-color", color);

        Span labelSpan = new Span(label);
        labelSpan.addClassName("trend-label");

        leftSide.add(colorDot, labelSpan);

        Span valueSpan = new Span(value);
        valueSpan.addClassName("trend-value");
        valueSpan.getStyle().set("color", color);

        row.add(leftSide, valueSpan);
        return row;
    }

    private HorizontalLayout createMetricRow(String label, String value, String color) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setPadding(false);
        row.setSpacing(false);
        row.addClassName("metric-row");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("metric-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("metric-value");
        valueSpan.getStyle().set("color", color);

        row.add(labelSpan, valueSpan);
        return row;
    }

    private void createAlertsSection() {
        alertsSection = new VerticalLayout();
        alertsSection.setSpacing(true);
        alertsSection.setPadding(false);
        alertsSection.addClassName("alerts-section");

        H3 alertsTitle = new H3("System Alerts & Notifications");
        alertsTitle.addClassName("section-title");

        VerticalLayout alertsContent = new VerticalLayout();
        alertsContent.setSpacing(true);
        alertsContent.setPadding(false);

        // Create sample alerts
        alertsContent.add(
                createAlertItem("High Priority", "5 urgent requisitions require immediate attention",
                        VaadinIcon.EXCLAMATION_CIRCLE, "#dc2626", "high"),
                createAlertItem("Budget Alert", "Department XYZ approaching budget limit",
                        VaadinIcon.WARNING, "#f59e0b", "medium"),
                createAlertItem("System Update", "New PPDA guidelines implemented",
                        VaadinIcon.INFO_CIRCLE, "#3b82f6", "low")
        );

        alertsSection.add(alertsTitle, alertsContent);
    }

    private Div createAlertItem(String title, String message, VaadinIcon iconType, String color, String priority) {
        Div alert = new Div();
        alert.addClassName("alert-item");
        alert.addClassName("alert-" + priority);

        HorizontalLayout alertContent = new HorizontalLayout();
        alertContent.setWidthFull();
        alertContent.setAlignItems(FlexComponent.Alignment.START);
        alertContent.setSpacing(true);
        alertContent.setPadding(false);

        Div iconContainer = new Div();
        iconContainer.addClassName("alert-icon-container");
        iconContainer.getStyle().set("background-color", color + "20");
        iconContainer.getStyle().set("border", "1px solid " + color + "40");

        Icon icon = new Icon(iconType);
        icon.addClassName("alert-icon");
        icon.getStyle().set("color", color);
        iconContainer.add(icon);

        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);
        textContent.setFlexGrow(1);

        Span titleSpan = new Span(title);
        titleSpan.addClassName("alert-title");

        Span messageSpan = new Span(message);
        messageSpan.addClassName("alert-message");

        textContent.add(titleSpan, messageSpan);

        Button dismissButton = new Button(new Icon(VaadinIcon.CLOSE));
        dismissButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        dismissButton.addClassName("alert-dismiss");
        dismissButton.addClickListener(e -> {
            alert.setVisible(false);
            showNotification("Alert dismissed", NotificationVariant.LUMO_CONTRAST);
        });

        alertContent.add(iconContainer, textContent, dismissButton);
        alert.add(alertContent);

        return alert;
    }

    private void showMyRequisitions() {
        contentContainer.removeAll();
        contentContainer.setWidthFull();

        H2 title = new H2("My Requisitions");
        title.addClassName("section-title");

        // Add bulk actions for my requisitions
        HorizontalLayout bulkActions = createBulkActions();
        bulkActions.addClassName("bulk-actions");

        List<RequisitionData> myRequisitions = null;

        if ((user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) && selectedSection == null) {
            myRequisitions = selectedBudget != null
                    ? requisitionService.getByBudgetAndStatusOrderByCreatedDateDesc(selectedBudget, RequisitionData.RequisitionStatus.APPROVED).stream().toList()
                    : List.of();
        } else if ((user.getRoles().contains(Role.BLO))) {
            myRequisitions = (selectedBudget != null && selectedSection != null) ? requisitionService
                    .getByBudgetStatuseAndDeptSectionOrderByCreatedDateDesc(selectedBudget, RequisitionData.RequisitionStatus.APPROVED, selectedSection)
                    .stream()
                    .toList()
                    : List.of();
        } else if ((user.getRoles().contains(Role.HOD))) {
            Set<RequisitionData.RequisitionStatus> statuses = Set.of(RequisitionStatus.APPROVED, RequisitionStatus.SUBMITTED, RequisitionStatus.REJECTED, RequisitionStatus.UNDER_REVIEW);
            myRequisitions
                    = (selectedBudget != null && selectedSection != null)
                            ? requisitionService
                                    .getByBudgetStatusesAndDeptSectionsOrderByCreatedDateDesc(selectedBudget, statuses, user.getDeptsection())
                                    .stream()
                                    .toList()
                            : List.of();
        }
        Grid<RequisitionData> myGrid = createRequisitionGrid(myRequisitions);

        // Add selection mode for bulk operations
        myGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        contentContainer.add(title, bulkActions, myGrid);
    }

    private void showPendingApprovals() {
        contentContainer.removeAll();

        H2 title = new H2("Pending Approvals");
        title.addClassName("section-title");

        // Add approval actions
        HorizontalLayout approvalActions = createApprovalActions();
        approvalActions.addClassName("approval-actions");

        List<RequisitionData> pendingRequisitions = requisitionService.getPendingApprovals();
        Grid<RequisitionData> pendingGrid = createRequisitionGrid(pendingRequisitions);

        // Add selection mode for bulk approvals
        pendingGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        contentContainer.add(title, approvalActions, pendingGrid);
    }

    private void showAllRequisitions() {
        contentContainer.removeAll();

        H2 title = new H2("All Requisitions");
        title.addClassName("section-title");

        // Add advanced filters
        HorizontalLayout advancedFilters = createAdvancedFilters();
        advancedFilters.addClassName("advanced-filters");

        List<RequisitionData> allRequisitions = selectedBudget != null
                ? requisitionService.getRequisitionsByBudget(selectedBudget)
                : List.of();

        Grid<RequisitionData> allGrid = createRequisitionGrid(allRequisitions);

        contentContainer.add(title, advancedFilters, allGrid);
    }

    private void showAnalytics() {
        contentContainer.removeAll();

        H2 title = new H2("Requisition Analytics");
        title.addClassName("section-title");

        // Analytics dashboard
        HorizontalLayout analyticsGrid = new HorizontalLayout();
        analyticsGrid.setWidthFull();
        analyticsGrid.setSpacing(true);
        analyticsGrid.setPadding(false);

        // Charts section
        VerticalLayout chartsSection = createChartsSection();
        chartsSection.addClassName("charts-section");

        // Insights section
        VerticalLayout insightsSection = createInsightsSection();
        insightsSection.addClassName("insights-section");

        analyticsGrid.add(chartsSection, insightsSection);
        analyticsGrid.setFlexGrow(2, chartsSection);
        analyticsGrid.setFlexGrow(1, insightsSection);

        contentContainer.add(title, analyticsGrid);
    }

    private void showApprovalWorkflow() {
        contentContainer.removeAll();

        H2 title = new H2("Approval Workflow");
        title.addClassName("section-title");

        // Workflow visualization
        VerticalLayout workflowSection = createWorkflowVisualization();
        workflowSection.addClassName("workflow-section");

        // Workflow statistics
        HorizontalLayout workflowStats = createWorkflowStatistics();
        workflowStats.addClassName("workflow-stats");

        contentContainer.add(title, workflowSection, workflowStats);
    }

    private VerticalLayout createChartsSection() {
        VerticalLayout charts = new VerticalLayout();
        charts.setSpacing(true);
        charts.setPadding(true);
        charts.addClassName("charts-section");

        H3 chartsTitle = new H3("Procurement Analytics");
        chartsTitle.addClassName("charts-title");

        // Mock chart placeholders
        Div chartPlaceholder1 = createChartPlaceholder("Monthly Requisition Volume", "Line Chart");
        Div chartPlaceholder2 = createChartPlaceholder("Budget Utilization by Department", "Bar Chart");
        Div chartPlaceholder3 = createChartPlaceholder("Procurement Method Distribution", "Pie Chart");

        charts.add(chartsTitle, chartPlaceholder1, chartPlaceholder2, chartPlaceholder3);
        return charts;
    }

    private Div createChartPlaceholder(String title, String type) {
        Div placeholder = new Div();
        placeholder.addClassName("chart-placeholder");

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);

        Icon chartIcon = new Icon(VaadinIcon.CHART);
        chartIcon.addClassName("chart-icon");

        Span chartTitle = new Span(title);
        chartTitle.addClassName("chart-title");

        Span chartType = new Span(type);
        chartType.addClassName("chart-type");

        content.add(chartIcon, chartTitle, chartType);
        placeholder.add(content);

        return placeholder;
    }

    private VerticalLayout createInsightsSection() {
        VerticalLayout insights = new VerticalLayout();
        insights.setSpacing(true);
        insights.setPadding(true);
        insights.addClassName("insights-section");

        H3 insightsTitle = new H3("AI-Powered Insights");
        insightsTitle.addClassName("insights-title");

        // Create insight cards
        insights.add(
                createInsightCard("Efficiency Opportunity",
                        "Consider consolidating similar requisitions to reduce processing overhead",
                        VaadinIcon.LIGHTBULB, "#f59e0b"),
                createInsightCard("Budget Optimization",
                        "Department ABC has 15% unused budget allocation",
                        VaadinIcon.TRENDING_UP, "#10b981"),
                createInsightCard("Compliance Alert",
                        "3 requisitions require additional documentation",
                        VaadinIcon.SHIELD, "#ef4444")
        );

        insights.add(insightsTitle);
        return insights;
    }

    private Div createInsightCard(String title, String description, VaadinIcon iconType, String color) {
        Div card = new Div();
        card.addClassName("insight-card");

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.START);
        content.setSpacing(true);
        content.setPadding(false);

        Div iconContainer = new Div();
        iconContainer.addClassName("insight-icon-container");
        iconContainer.getStyle().set("background-color", color + "20");
        iconContainer.getStyle().set("border", "1px solid " + color + "40");

        Icon icon = new Icon(iconType);
        icon.addClassName("insight-icon");
        icon.getStyle().set("color", color);
        iconContainer.add(icon);

        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);
        textContent.setFlexGrow(1);

        Span titleSpan = new Span(title);
        titleSpan.addClassName("insight-title");

        Span descSpan = new Span(description);
        descSpan.addClassName("insight-description");

        textContent.add(titleSpan, descSpan);
        content.add(iconContainer, textContent);
        card.add(content);

        return card;
    }

    private VerticalLayout createWorkflowVisualization() {
        VerticalLayout workflow = new VerticalLayout();
        workflow.setSpacing(true);
        workflow.setPadding(true);
        workflow.addClassName("workflow-visualization");

        H3 workflowTitle = new H3("Approval Workflow Stages");
        workflowTitle.addClassName("workflow-title");

        // Workflow stages
        HorizontalLayout stagesFlow = new HorizontalLayout();
        stagesFlow.setWidthFull();
        stagesFlow.setSpacing(true);
        stagesFlow.setPadding(false);
        stagesFlow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        String[] stages = {"Draft", "Submitted", "Under Review", "Approved", "Completed"};
        String[] colors = {"#6b7280", "#3b82f6", "#f59e0b", "#10b981", "#059669"};
        int[] counts = {12, 8, 5, 23, 45};

        for (int i = 0; i < stages.length; i++) {
            Div stageCard = createWorkflowStage(stages[i], String.valueOf(counts[i]), colors[i], i == 0);
            stagesFlow.add(stageCard);
        }

        workflow.add(workflowTitle, stagesFlow);
        return workflow;
    }

    private Div createWorkflowStage(String stage, String count, String color, boolean isFirst) {
        Div stageCard = new Div();
        stageCard.addClassName("workflow-stage");
        if (isFirst) {
            stageCard.addClassName("stage-active");
        }

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(false);
        content.setPadding(false);

        Div stageIcon = new Div();
        stageIcon.addClassName("stage-icon");
        stageIcon.getStyle().set("background-color", color);
        stageIcon.setText(count);

        Span stageName = new Span(stage);
        stageName.addClassName("stage-name");

        content.add(stageIcon, stageName);
        stageCard.add(content);

        return stageCard;
    }

    private HorizontalLayout createWorkflowStatistics() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.setSpacing(true);
        stats.setPadding(false);

        // Average processing time
        VerticalLayout avgTime = createWorkflowStat("Average Processing Time", "3.2 days",
                VaadinIcon.CLOCK, "#3b82f6");

        // Approval rate
        VerticalLayout approvalRate = createWorkflowStat("Approval Rate", "94.5%",
                VaadinIcon.CHECK_CIRCLE, "#10b981");

        // Bottleneck stage
        VerticalLayout bottleneck = createWorkflowStat("Current Bottleneck", "Under Review",
                VaadinIcon.WARNING, "#f59e0b");

        stats.add(avgTime, approvalRate, bottleneck);
        return stats;
    }

    private VerticalLayout createWorkflowStat(String label, String value, VaadinIcon iconType, String color) {
        VerticalLayout stat = new VerticalLayout();
        stat.setSpacing(true);
        stat.setPadding(true);
        stat.addClassName("workflow-stat");
        stat.setAlignItems(FlexComponent.Alignment.CENTER);

        Div iconContainer = new Div();
        iconContainer.addClassName("workflow-stat-icon");
        iconContainer.getStyle().set("background-color", color + "20");
        iconContainer.getStyle().set("border", "1px solid " + color + "40");

        Icon icon = new Icon(iconType);
        icon.getStyle().set("color", color);
        iconContainer.add(icon);

        Span valueSpan = new Span(value);
        valueSpan.addClassName("workflow-stat-value");
        valueSpan.getStyle().set("color", color);

        Span labelSpan = new Span(label);
        labelSpan.addClassName("workflow-stat-label");

        stat.add(iconContainer, valueSpan, labelSpan);
        return stat;
    }

    private HorizontalLayout createBulkActions() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.setPadding(false);
        actions.addClassName("bulk-actions");

        bulkApproveButton = new Button("Bulk Approve", new Icon(VaadinIcon.CHECK));
        bulkApproveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        bulkApproveButton.addClassName("bulk-approve-button");
        bulkApproveButton.setEnabled(false);
        bulkApproveButton.addClickListener(e -> performBulkApproval());

        bulkRejectButton = new Button("Bulk Reject", new Icon(VaadinIcon.CLOSE));
        bulkRejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        bulkRejectButton.addClassName("bulk-reject-button");
        bulkRejectButton.setEnabled(false);
        bulkRejectButton.addClickListener(e -> performBulkRejection());

        Button exportSelectedButton = new Button("Export Selected", new Icon(VaadinIcon.DOWNLOAD));
        exportSelectedButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportSelectedButton.addClassName("export-selected-button");
        exportSelectedButton.addClickListener(e -> exportSelectedRequisitions());

        actions.add(bulkApproveButton, bulkRejectButton, exportSelectedButton);
        return actions;
    }

    private HorizontalLayout createApprovalActions() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.setPadding(false);
        actions.addClassName("approval-actions");

        Button approveAllButton = new Button("Approve All", new Icon(VaadinIcon.CHECK_CIRCLE));
        approveAllButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        approveAllButton.addClassName("approve-all-button");
        approveAllButton.addClickListener(e -> approveAllPending());

        Button priorityFirstButton = new Button("Priority First", new Icon(VaadinIcon.ARROW_UP));
        priorityFirstButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        priorityFirstButton.addClassName("priority-first-button");
        priorityFirstButton.addClickListener(e -> sortByPriority());

        Button generateReportButton = new Button("Generate Report", new Icon(VaadinIcon.FILE_TEXT));
        generateReportButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        generateReportButton.addClassName("generate-report-button");
        generateReportButton.addClickListener(e -> generateApprovalReport());

        actions.add(approveAllButton, priorityFirstButton, generateReportButton);
        return actions;
    }

    private HorizontalLayout createAdvancedFilters() {
        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(true);
        filters.setPadding(false);
        filters.addClassName("advanced-filters");

        ComboBox<String> departmentFilter = new ComboBox<>("Department");
        departmentFilter.setItems("All", "HR", "IT", "Finance", "Operations");
        departmentFilter.setValue("All");
        departmentFilter.addClassName("department-filter");

        ComboBox<String> amountRangeFilter = new ComboBox<>("Amount Range");
        amountRangeFilter.setItems("All", "< UGX 1M", "UGX 1M - 10M", "UGX 10M - 100M", "> UGX 100M");
        amountRangeFilter.setValue("All");
        amountRangeFilter.addClassName("amount-range-filter");

        ComboBox<String> dateRangeFilter = new ComboBox<>("Date Range");
        dateRangeFilter.setItems("All", "Today", "This Week", "This Month", "Last 3 Months");
        dateRangeFilter.setValue("All");
        dateRangeFilter.addClassName("date-range-filter");

        Button clearFiltersButton = new Button("Clear Filters", new Icon(VaadinIcon.ERASER));
        clearFiltersButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearFiltersButton.addClassName("clear-filters-button");
        clearFiltersButton.addClickListener(e -> clearAllFilters());

        filters.add(departmentFilter, amountRangeFilter, dateRangeFilter, clearFiltersButton);
        return filters;
    }

    private HorizontalLayout createDashboardCards() {
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setSpacing(true);
        cards.setPadding(false);

        Long totalRequisitions = Long.valueOf(0);
        Long draftCount = Long.valueOf(0);
        Long submittedCount = Long.valueOf(0);
        Long approvedCount = Long.valueOf(0);
        Double totalDraftAmount = Long.valueOf(0).doubleValue();
        Double totalsumittedlAmount = Long.valueOf(0).doubleValue();
        Double totalRejectedAmount = Long.valueOf(0).doubleValue();
        Double totalCompletedAmount = Long.valueOf(0).doubleValue();
        Double totalAmount = requisitionService.getTotalAmountByStatus(RequisitionData.RequisitionStatus.APPROVED);

        if ((user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) && sectionSelector.isEmpty()) {
            totalRequisitions = requisitionService.getRequisitionsCountByBudget(selectedBudget);
            draftCount = requisitionService.getRequisitionsCountByBudgetAndStatus(selectedBudget, RequisitionData.RequisitionStatus.DRAFT);
            submittedCount = requisitionService.getRequisitionsCountByBudgetAndStatus(selectedBudget, RequisitionData.RequisitionStatus.SUBMITTED);
            approvedCount = requisitionService.getRequisitionsCountByBudgetAndStatus(selectedBudget, RequisitionData.RequisitionStatus.APPROVED);
            totalDraftAmount = requisitionService.getTotalRequestedByBudgetAndStatus(selectedBudget, RequisitionData.RequisitionStatus.DRAFT).doubleValue();
            totalsumittedlAmount = requisitionService.getTotalRequestedByBudgetAndStatus(selectedBudget, RequisitionData.RequisitionStatus.SUBMITTED).doubleValue();
            totalRejectedAmount = requisitionService.getTotalRequestedByBudgetAndStatus(selectedBudget, RequisitionData.RequisitionStatus.REJECTED).doubleValue();
            totalCompletedAmount = requisitionService.getTotalRequestedByBudgetAndStatus(selectedBudget, RequisitionData.RequisitionStatus.COMPLETED).doubleValue();
        } else {
            totalRequisitions = requisitionService.getRequisitionsCountByBudgetAndDeptSection(selectedBudget, selectedSection);
            draftCount = requisitionService.getRequisitionsCountByBudgetAndStatusAndDeptSection(selectedBudget, RequisitionData.RequisitionStatus.DRAFT, selectedSection);
            submittedCount = requisitionService.getRequisitionsCountByBudgetAndStatusAndDeptSection(selectedBudget, RequisitionData.RequisitionStatus.SUBMITTED, selectedSection);
            approvedCount = requisitionService.getRequisitionsCountByBudgetAndStatusAndDeptSection(selectedBudget, RequisitionData.RequisitionStatus.APPROVED, selectedSection);
            totalDraftAmount = requisitionService.getTotalRequestedByBudgetDeptSectionAndStatus(selectedBudget, selectedSection, RequisitionData.RequisitionStatus.DRAFT).doubleValue();
            totalsumittedlAmount = requisitionService.getTotalRequestedByBudgetDeptSectionAndStatus(selectedBudget, selectedSection, RequisitionData.RequisitionStatus.SUBMITTED).doubleValue();
            totalRejectedAmount = requisitionService.getTotalRequestedByBudgetDeptSectionAndStatus(selectedBudget, selectedSection, RequisitionData.RequisitionStatus.REJECTED).doubleValue();
            totalCompletedAmount = requisitionService.getTotalRequestedByBudgetDeptSectionAndStatus(selectedBudget, selectedSection, RequisitionData.RequisitionStatus.COMPLETED).doubleValue();
        }

        cards.add(
                createDashboardCard("Total Requisitions", totalRequisitions.toString(), VaadinIcon.FILE_TEXT, "card-total"),
                createDashboardCard("Draft", draftCount.toString(), VaadinIcon.EDIT, "card-draft"),
                createDashboardCard("Submitted", submittedCount.toString(), VaadinIcon.UPLOAD, "card-submitted"),
                createDashboardCard("Approved", approvedCount.toString(), VaadinIcon.CHECK_CIRCLE, "card-approved"),
                createDashboardCard("Approved Amount", formatCurrency(totalAmount), VaadinIcon.MONEY, "card-amount"),
                createDashboardCard("Draft Amount", formatCurrency(totalDraftAmount), VaadinIcon.MONEY, "card-amount"),
                createDashboardCard("Submitted Amount", formatCurrency(totalsumittedlAmount), VaadinIcon.MONEY, "card-amount"),
                createDashboardCard("Rejected Amount", formatCurrency(totalRejectedAmount), VaadinIcon.MONEY, "card-amount"),
                createDashboardCard("Completed Amount", formatCurrency(totalCompletedAmount), VaadinIcon.MONEY, "card-amount")
        );

        return cards;
    }

    private Div createDashboardCard(String title, String value, VaadinIcon iconType, String cardClass) {
        Div card = new Div();
        card.addClassName("quick-stat-item");
        card.addClassName(cardClass);

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setPadding(false);
        content.setSpacing(false);

        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);
        textContent.setFlexGrow(1);

        Span titleSpan = new Span(title);
        titleSpan.addClassName("card-title");

        H3 valueH3 = new H3(value);
        valueH3.addClassName("card-value");

        textContent.add(titleSpan, valueH3);

        Div iconContainer = new Div();
        iconContainer.addClassName("card-icon");
        Icon icon = new Icon(iconType);
        icon.setSize("1.5rem");
        iconContainer.add(icon);

        content.add(textContent, iconContainer);
        card.add(content);

        return card;
    }

    private Grid<RequisitionData> createRequisitionGrid(List<RequisitionData> requisitions) {
        Grid<RequisitionData> grid = new Grid<>(RequisitionData.class, false);
        //grid.addClassName("requisition-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setWidth("1200px");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        // grid.setHeight("600px");

        // Requisition number column
        grid.addColumn(RequisitionData::getRequisitionNumber)
                .setHeader("Requisition #")
                .setWidth("180px")
                .setSortable(true).setFlexGrow(0);

        // Activity column
        grid.addColumn(req -> req.getActivity() != null ? req.getActivity().getActivityCode() : "N/A")
                .setHeader("Activity")
                .setWidth("130px")
                .setFlexGrow(0);
        //grid.addColumn(RequisitionData::getSubjectOfProcurement).setHeader("Subject of Procurement");
        grid.addColumn(req -> req.getSubjectOfProcurement() != null && req.getSubjectOfProcurement().length() > 50
                ? req.getSubjectOfProcurement().substring(0, 50) + "..." : req.getSubjectOfProcurement())
                .setHeader("Subject of Procurement");

        // Type and amount
        grid.addColumn(new ComponentRenderer<>(this::createTypeAndAmountInfo))
                .setHeader("Type & Amount");

        grid.addColumn(req -> req.getCreatedDate() != null ? req.getCreatedDate().format(formatter) : "")
                .setHeader("Created Date")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(req -> req.getApprovedDate() != null ? req.getApprovedDate().format(formatter) : "")
                .setHeader("Approved Date")
                .setSortable(true)
                .setAutoWidth(true);
        // Status
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("120px")
                .setFlexGrow(0);

        // Priority
        /*        grid.addColumn(new ComponentRenderer<>(this::createPriorityBadge))
        .setHeader("Priority")
        .setWidth("100px")
        .setFlexGrow(0);*/
        // Actions column
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setHeader("Actions")
                .setWidth("200px")
                .setFlexGrow(0);

        // Selection handling for bulk operations
        grid.addSelectionListener(selection -> {
            boolean hasSelection = !selection.getAllSelectedItems().isEmpty();
            if (bulkApproveButton != null) {
                bulkApproveButton.setEnabled(hasSelection);
            }
            if (bulkRejectButton != null) {
                bulkRejectButton.setEnabled(hasSelection);
            }
        });

        //grid.setItems(requisitions);
        grid.setItems(requisitions != null ? requisitions : Collections.emptyList());
        return grid;
    }

    private Div createTypeAndAmountInfo(RequisitionData requisition) {
        Div container = new Div();
        container.addClassName("type-amount-info");

        Span typeSpan = new Span(requisition.getRequisitionType().getDisplayName());
        typeSpan.addClassName("requisition-type");

        Span amountSpan = new Span(" " + formatCurrency(requisition.getRequestedAmount() != null ? requisition.getRequestedAmount() : 0.0));
        amountSpan.addClassName("requisition-amount");

        // Add budget compliance indicator
        if (requisition.isOverBudget()) {
            Icon warningIcon = new Icon(VaadinIcon.WARNING);
            warningIcon.addClassName("budget-warning-icon");
            warningIcon.getStyle().set("color", "#ef4444");
            container.add(typeSpan, amountSpan, warningIcon);
        } else {
            container.add(typeSpan, amountSpan);
        }

        return container;
    }

    private Div createStatusBadge(RequisitionData requisition) {
        Div badge = new Div();
        badge.addClassName("status-badge");
        badge.addClassName("status-" + requisition.getStatus().name().toLowerCase());
        badge.getStyle().set("background-color", requisition.getStatusColor() + "20");
        badge.getStyle().set("color", requisition.getStatusColor());
        badge.getStyle().set("border", "1px solid " + requisition.getStatusColor() + "40");

        Span statusText = new Span(requisition.getStatus().getDisplayName());
        badge.add(statusText);

        return badge;
    }

    private Div createPriorityBadge(RequisitionData requisition) {
        Div badge = new Div();
        badge.addClassName("priority-badge");

// Safely handle null priority level
        if (requisition.getPriorityLevel() != null) {
            badge.addClassName("priority-" + requisition.getPriorityLevel().name().toLowerCase());
        } else {
            badge.addClassName("priority-default"); // fallback class
        }

// Safely handle null priority color
        String color = requisition.getPriorityColor() != null ? requisition.getPriorityColor() : "#999"; // fallback color

        badge.getStyle().set("background-color", color + "20"); // transparent background
        badge.getStyle().set("color", color);                 // text color
        badge.getStyle().set("border", "1px solid " + color + "40"); // border with transparency

        String displayName = requisition.getPriorityLevel() != null
                ? requisition.getPriorityLevel().getDisplayName()
                : "N/A"; // fallback text for null

        Span priorityText = new Span(displayName);

        badge.add(priorityText);

        if (requisition.getPriorityLevel() == RequisitionData.PriorityLevel.URGENT
                || requisition.getPriorityLevel() == RequisitionData.PriorityLevel.EMERGENCY) {
            badge.addClassName("priority-pulse");
        }

        return badge;
    }

    private HorizontalLayout createActionButtons(RequisitionData requisition) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.setPadding(false);
        actions.addClassName("action-buttons");

        // View button
        Button viewButton = new Button(new Icon(VaadinIcon.EYE));
        viewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        viewButton.addClassName("view-button");
        viewButton.setTooltipText("View Details");
        viewButton.addClickListener(e -> viewRequisition(requisition));

        actions.add(viewButton);

        // Edit button (only for draft)
        if (requisition.canBeEdited() && user.getRoles().contains(Role.BLO)) {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            editButton.addClassName("edit-button");
            editButton.setTooltipText("Edit Requisition");
            if (requisition.getStatus() == RequisitionStatus.APPROVED) {
                editButton.setEnabled(false);
            }
            if (requisition.getStatus() == RequisitionStatus.SUBMITTED) {
                editButton.setEnabled(false);
            }            
            editButton.addClickListener(e -> editRequisition(requisition));
            actions.add(editButton);
        }

        // Submit button (for draft requisitions)
        if (requisition.canBeSubmitted() && user.getRoles().contains(Role.BLO)) {
            Button submitButton = new Button(new Icon(VaadinIcon.UPLOAD));
            submitButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            submitButton.addClassName("submit-button");
            submitButton.setTooltipText("Submit for Approval");
            if (requisition.getStatus() == RequisitionStatus.APPROVED) {
                submitButton.setEnabled(false);
            }
            if (requisition.getStatus() == RequisitionStatus.SUBMITTED) {
                submitButton.setEnabled(false);
            }             
            submitButton.addClickListener(e -> submitRequisition(requisition));
            actions.add(submitButton);
        }

        // Approve/Reject buttons (for submitted requisitions)
        if (requisition.canBeApproved() && user.getRoles().contains(Role.HOD)) {
            Button approveButton = new Button(new Icon(VaadinIcon.CHECK));
            approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            approveButton.addClassName("approve-button");
            approveButton.setTooltipText("Approve Requisition");
            if (requisition.getStatus() == RequisitionStatus.APPROVED) {
                approveButton.setEnabled(false);
            }
            approveButton.addClickListener(e -> showApprovalDialog(requisition, true));

            Button rejectButton = new Button(new Icon(VaadinIcon.CLOSE));
            rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            rejectButton.addClassName("reject-button");
            rejectButton.setTooltipText("Reject Requisition");
            if (requisition.getStatus() == RequisitionStatus.APPROVED) {
                rejectButton.setEnabled(false);
            }
            if (requisition.getStatus() == RequisitionStatus.REJECTED) {
                rejectButton.setEnabled(false);
            }             
            rejectButton.addClickListener(e -> showApprovalDialog(requisition, false));

            actions.add(approveButton, rejectButton);
        }

        // Cancel button (for submitted requisitions)
        if (requisition.canBeCancelled() && user.getRoles().contains(Role.HOD)) {
            Button cancelButton = new Button(new Icon(VaadinIcon.BAN));
            cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            cancelButton.addClassName("cancel-button");
            cancelButton.setTooltipText("Cancel Requisition");
            if (requisition.getStatus() == RequisitionStatus.APPROVED) {
                cancelButton.setEnabled(false);
            }
            if (requisition.getStatus() == RequisitionStatus.REJECTED) {
                cancelButton.setEnabled(false);
            }            
            cancelButton.addClickListener(e -> cancelRequisition(requisition));
            actions.add(cancelButton);
        }

        // Print button
        if (requisition.canBePrinted()) {
            Button printButton = new Button(new Icon(VaadinIcon.PRINT));
            printButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            printButton.addClassName("print-button");
            printButton.setTooltipText("Print Requisition");
            printButton.addClickListener(e -> printRequisition(requisition));
            actions.add(printButton);
        }

        return actions;
    }

    private void showNewRequisitionDialog() {
        if (selectedBudget == null || selectedSection == null) {
            showNotification("Please select a budget first & Section", NotificationVariant.LUMO_ERROR);
            return;
        }

        showActivitySelectionDialog(null, selectedBudget, selectedSection);
    }

    private void showActivitySelectionDialog(RequisitionData existingRequisition, Budget selectedBudget, UrcDeptSectionAnlDimbgt selectedSection) {
        try {
            List<Urc_Activities> activities = activitiesService.getActivitiesBySectionAndBudget(selectedSection, selectedBudget);

            activityDialog = new ActivitySelectionDialog(
                    activities,
                    existingRequisition != null ? existingRequisition.getActivity() : null,
                    budgetItemsService,
                    selectedBudget, sampleStockUnitMeasureService,
                    requisitionService, authenticatedUser,
                    userService, selectedSection, organisationService, coaService, sALFLDGService, deptSectionMergerService
            );

            activityDialog.addSelectionListener(e -> {
                Urc_Activities selectedActivity = e.getSelectedActivity();
                showRequisitionDialog(selectedActivity, existingRequisition);
            });

            activityDialog.open();
        } catch (Exception e) {
            showNotification("Error loading activities: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showRequisitionDialog(Urc_Activities activity, RequisitionData existingRequisition) {
        try {
            // Create mock COA budget info for the activity
            COABudgetInfo budgetInfo = createMockBudgetInfo(activity);
            AccountSummary summa = new AccountSummary();
            summa.setAccountCode(existingRequisition.getCoa().getCode());
            summa.setAccountName(existingRequisition.getCoa().getName());
            summa.setTotalBalance(existingRequisition.getAvailableBalanceByActivity());

            requisitionDialog = new PPDARequisitionDialog(
                    summa, existingRequisition.getActivity(), selectedBudget, budgetItemsService, sampleStockUnitMeasureService,
                    requisitionService, authenticatedUser, userService, selectedSection, fundsourceService, coaService, deptSectionMergerService);

            /*            if (existingRequisition != null) {
                        requisitionDialog.setRequisition(existingRequisition);
                        }*/
            requisitionDialog.addSaveListener(e -> {
                showNotification("Requisition saved successfully", NotificationVariant.LUMO_SUCCESS);
                refreshCurrentView();
            });
            requisitionDialog.setRequisitionData(existingRequisition);
            requisitionDialog.setFormData();
            requisitionDialog.setupEventHandlers();
            requisitionDialog.open();

        } catch (Exception e) {
            showNotification("Error opening requisition dialog: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private COABudgetInfo createMockBudgetInfo(Urc_Activities activity) {
        // Create mock budget info based on activity
        double budgetTotal = activity.getTotalBudgetAmount();
        double committedAmount = budgetTotal * 0.15; // 15% committed
        double actualSpent = budgetTotal * 0.25; // 25% spent

        COABudgetInfo budgetInfo = new COABudgetInfo();
        budgetInfo.setCoaCode(activity.getActivityCode());
        budgetInfo.setCoaName(activity.getName());
        budgetInfo.setBudgetTotal(budgetTotal);
        budgetInfo.setCommittedAmount(committedAmount);
        budgetInfo.setActualSpent(actualSpent);
        budgetInfo.setBalanceAmount(budgetTotal - committedAmount - actualSpent);
        budgetInfo.setStatus("Available");
        budgetInfo.setActive(true);

        return budgetInfo;
    }

    private void viewRequisition(RequisitionData requisition) {
        showRequisitionDialog(requisition.getActivity(), requisition);
    }

    private void editRequisition(RequisitionData requisition) {
        showRequisitionDialog(requisition.getActivity(), requisition);
    }

    private void submitRequisition(RequisitionData requisition) {
        try {
            requisitionService.submitRequisition(requisition.getId(), user);
            showNotification("Requisition submitted for approval", NotificationVariant.LUMO_SUCCESS);
            refreshCurrentView();
        } catch (Exception e) {
            showNotification("Error submitting requisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showApprovalDialog(RequisitionData requisition, boolean isApproval) {
        System.out.println("Approve dialog to open");
        try {
            approvalDialog = new RequisitionApprovalDialog(requisition, requisitionService, authenticatedUser,userService,isApproval);
            approvalDialog.addApprovalListener(e -> {
                String action = e.isApproved() ? "approved" : "rejected";
                showNotification("Requisition " + action + " successfully",
                        e.isApproved() ? NotificationVariant.LUMO_SUCCESS : NotificationVariant.LUMO_ERROR);
                refreshCurrentView();
            });

            approvalDialog.open();
        } catch (Exception e) {
            System.out.println("Approve dialog to open failed "+  e.getMessage());
            showNotification("Error opening approval dialog: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void cancelRequisition(RequisitionData requisition) {
        try {
            // Update status to cancelled
            requisition.setHodStatus(RequisitionData.RequisitionStatus.REJECTED);
            requisition.setRejectionReason("Cancelled by user");
            requisitionService.updateRequisition(requisition);

            showNotification("Requisition cancelled", NotificationVariant.LUMO_CONTRAST);
            refreshCurrentView();
        } catch (Exception e) {
            showNotification("Error cancelling requisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void printRequisition(RequisitionData requisition) {
        // Generate PDF or open print dialog
        //showNotification("Print functionality coming soon", NotificationVariant.LUMO_PRIMARY);
        req.generatePDFForm(requisition, this);

    }

    private void performBulkApproval() {
        // Get selected items and approve them
        showNotification("Bulk approval functionality coming soon", NotificationVariant.LUMO_PRIMARY);
    }

    private void performBulkRejection() {
        // Get selected items and reject them
        showNotification("Bulk rejection functionality coming soon", NotificationVariant.LUMO_PRIMARY);
    }

    private void exportSelectedRequisitions() {
        showNotification("Export selected functionality coming soon", NotificationVariant.LUMO_PRIMARY);
    }

    private void approveAllPending() {
        showNotification("Approve all functionality coming soon", NotificationVariant.LUMO_PRIMARY);
    }

    private void sortByPriority() {
        showNotification("Priority sorting applied", NotificationVariant.LUMO_SUCCESS);
        refreshCurrentView();
    }

    private void generateApprovalReport() {
        showNotification("Approval report generation coming soon", NotificationVariant.LUMO_PRIMARY);
    }

    private void clearAllFilters() {
        searchField.clear();
        statusFilter.clear();
        priorityFilter.clear();
        currentSearchTerm = "";
        currentStatusFilter = null;
        currentPriorityFilter = null;
        refreshCurrentView();
        showNotification("All filters cleared", NotificationVariant.LUMO_CONTRAST);
    }

    private void exportRequisitions() {
        showNotification("Export functionality coming soon", NotificationVariant.LUMO_PRIMARY);
    }

    private void filterRequisitions() {
        // Implement filtering logic based on search field and filters
        refreshCurrentView();
    }

    private void refreshCurrentView() {
        Tab selectedTab = navigationTabs.getSelectedTab();
        if (selectedTab != null) {
            navigationTabs.setSelectedTab(selectedTab);
        }
    }

    private void refreshDashboardAndTrends() {
        try {
            // Update dashboard cards with new data
            updateDashboardCards();

            // Update trends section with new data
            updateTrendsSection();

        } catch (Exception e) {
            showNotification("Error refreshing dashboard: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateDashboardCards() {
        if (dashboardCards != null && contentContainer.indexOf(dashboardCards) != -1) {
            // Remove existing dashboard cards
            contentContainer.remove(dashboardCards);

            // Create new dashboard cards with updated data
            dashboardCards = createDashboardCards();
            dashboardCards.addClassName("requisition-dashboard-cards");

            // Insert at the beginning (after header if exists)
            contentContainer.addComponentAsFirst(dashboardCards);
        }
    }

    private void updateTrendsSection() {
        // Find and update trends section if it exists in current view
        for (int i = 0; i < contentContainer.getComponentCount(); i++) {
            Component component = contentContainer.getComponentAt(i);
            if (component.getClass().getSimpleName().contains("trends")
                    || (component instanceof VerticalLayout
                    && component.getElement().getAttribute("class") != null
                    && component.getElement().getAttribute("class").contains("trends"))) {

                // Remove existing trends section
                contentContainer.remove(component);

                // Create new trends section
                VerticalLayout newTrendsSection = createTrendsSection();
                newTrendsSection.addClassName("requisition-trends-section");

                // Insert at the same position
                contentContainer.addComponentAtIndex(i, newTrendsSection);
                break;
            }
        }
    }

    private String getStatusColor(RequisitionData.RequisitionStatus status) {
        switch (status) {
            case DRAFT:
                return "#6b7280";
            case SUBMITTED:
                return "#3b82f6";
            case APPROVED:
                return "#10b981";
            case REJECTED:
                return "#ef4444";
            case COMPLETED:
                return "#059669";
            case UNDER_REVIEW:
                return "#f59e0b";
            default:
                return "#6b7280";
        }
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

}
