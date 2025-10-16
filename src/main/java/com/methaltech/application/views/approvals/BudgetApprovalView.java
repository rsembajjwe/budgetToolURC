
package com.methaltech.application.views.approvals;

import com.methaltech.application.data.bgtool.service.BudgetApprovalsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApprovals;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

@Route(value = "budget-approvals", layout = MainLayout.class)
@PageTitle("Budget Approval Workflow")
@SpringComponent
@UIScope
@RolesAllowed({"BLO", "HOD", "ADMIN", "CFO"})
public class BudgetApprovalView extends VerticalLayout {

    private final BudgetApprovalsService approvalsService;
    private final BudgetService budgetService;
    private final AuthenticationContext authContext;
    
    private Tabs navigationTabs;
    private VerticalLayout contentContainer;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> priorityFilter;
    private Button newRequestButton;
    
    private ApprovalDashboardCards dashboardCards;
    private ApprovalWorkflowGrid approvalGrid;
    private ApprovalRequestForm requestForm;
    
    private String currentUserRole;
    private String currentUserName;
    private ComboBox<Budget> fiscalYear = new ComboBox<>("Fiscal Year");

    @Autowired
    public BudgetApprovalView(BudgetApprovalsService approvalsService, 
                             BudgetService budgetService,
                             AuthenticationContext authContext) {
        this.approvalsService = approvalsService;
        this.budgetService = budgetService;
        this.authContext = authContext;
        
        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        addClassName("budget-approval-view");
        
        loadFiscalYearData();
        
        initializeUserContext();
        createHeader();
        createNavigationTabs();
        createMainContent();
        loadInitialData();
    }

    private void initializeUserContext() {
        authContext.getAuthenticatedUser(UserDetails.class).ifPresent(user -> {
            currentUserName = user.getUsername();
            // Extract role from authorities - simplified for demo
            currentUserRole = user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        });
        
        // Fallback for demo
        if (currentUserRole == null) {
            currentUserRole = "BLO";
            currentUserName = "Demo User";
        }
    }

    private void createHeader() {
        Div header = new Div();
        header.addClassName("approval-header");
        header.setWidthFull();

        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        headerContent.setPadding(false);
        headerContent.setSpacing(false);

        // Left side - Title and subtitle
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSpacing(false);
        leftSide.setPadding(false);
        leftSide.setFlexGrow(1);

        H1 title = new H1("Budget Approval Workflow");
        title.addClassName("approval-title");

        Span subtitle = new Span("Manage budget requests through the approval process");
        subtitle.addClassName("approval-subtitle");

        // User role badge
        HorizontalLayout roleLayout = new HorizontalLayout();
        roleLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        roleLayout.addClassName("user-role-badge");
        roleLayout.setSpacing(false);
        roleLayout.setPadding(false);

        Icon userIcon = new Icon(VaadinIcon.USER);
        userIcon.addClassName("role-icon");

        Span roleText = new Span(currentUserRole + " Dashboard");
        roleText.addClassName("role-text");

        roleLayout.add(userIcon, roleText);

        leftSide.add(title, subtitle, fiscalYear,roleLayout);

        // Right side - Search and filters
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);

        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Search approvals...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("approval-search");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterApprovals());

        // Status filter
        statusFilter = new ComboBox<>("Status");
        statusFilter.setItems("All", "Pending", "In Progress", "Approved", "Rejected");
        statusFilter.setValue("All");
        statusFilter.addClassName("status-filter");
        statusFilter.addValueChangeListener(e -> filterApprovals());

        // Priority filter
        priorityFilter = new ComboBox<>("Priority");
        priorityFilter.setItems("All", "Low", "Medium", "High", "Urgent");
        priorityFilter.setValue("All");
        priorityFilter.addClassName("priority-filter");
        priorityFilter.addValueChangeListener(e -> filterApprovals());

        // New request button
        newRequestButton = new Button("New Request", new Icon(VaadinIcon.PLUS));
        newRequestButton.addClassName("new-request-button");
        newRequestButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newRequestButton.addClickListener(e -> showNewRequestForm());

        rightSide.add(searchField, statusFilter, priorityFilter, newRequestButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }
    private void loadFiscalYearData() {
        fiscalYear.setItems(query -> budgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        fiscalYear.setItemLabelGenerator(Budget::getFinancialYear);
        Optional<Budget> recentBudget = budgetService.getMostRecurrentBudget();
        if (recentBudget.isPresent()) {
            fiscalYear.setValue(recentBudget.get());
        }
    }
    private void createNavigationTabs() {
        navigationTabs = new Tabs();
        navigationTabs.addClassName("approval-navigation");
        navigationTabs.setWidthFull();

        Tab dashboardTab = new Tab(new Icon(VaadinIcon.DASHBOARD), new Span("Dashboard"));
        Tab myRequestsTab = new Tab(new Icon(VaadinIcon.FILE_TEXT), new Span("My Requests"));
        Tab pendingTab = new Tab(new Icon(VaadinIcon.CLOCK), new Span("Pending Approvals"));
        Tab allRequestsTab = new Tab(new Icon(VaadinIcon.LIST), new Span("All Requests"));

        dashboardTab.addClassName("nav-tab");
        myRequestsTab.addClassName("nav-tab");
        pendingTab.addClassName("nav-tab");
        allRequestsTab.addClassName("nav-tab");

        navigationTabs.add(dashboardTab, myRequestsTab, pendingTab, allRequestsTab);
        navigationTabs.setSelectedTab(dashboardTab);

        navigationTabs.addSelectedChangeListener(e -> {
            Tab selectedTab = e.getSelectedTab();
            if (selectedTab == dashboardTab) {
                showDashboard();
            } else if (selectedTab == myRequestsTab) {
                showMyRequests();
            } else if (selectedTab == pendingTab) {
                showPendingApprovals();
            } else if (selectedTab == allRequestsTab) {
                showAllRequests();
            }
        });

        add(navigationTabs);
    }

    private void createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("approval-main-content");

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.addClassName("approval-content-container");

        mainContent.add(contentContainer);
        add(mainContent);
    }

    private void loadInitialData() {
        showDashboard();
    }

    private void showDashboard() {
        contentContainer.removeAll();

        // Dashboard cards
        dashboardCards = new ApprovalDashboardCards(approvalsService, currentUserRole);
        dashboardCards.addClassName("approval-dashboard-cards");

        // Recent activity section
        VerticalLayout recentActivity = new VerticalLayout();
        recentActivity.setSpacing(true);
        recentActivity.setPadding(false);
        recentActivity.addClassName("recent-activity-section");

        H2 activityTitle = new H2("Recent Activity");
        activityTitle.addClassName("section-title");

        // Get recent approvals based on user role
        List<BudgetApprovals> recentApprovals = getRecentApprovals();
        ApprovalWorkflowGrid recentGrid = new ApprovalWorkflowGrid(recentApprovals, currentUserRole, approvalsService);
        recentGrid.addClassName("recent-approvals-grid");

        recentActivity.add(activityTitle, recentGrid);

        contentContainer.add(dashboardCards, recentActivity);
    }

    private void showMyRequests() {
        contentContainer.removeAll();

        H2 title = new H2("My Requests");
        title.addClassName("section-title");

        List<BudgetApprovals> myRequests = approvalsService.getApprovalsByRequester(currentUserName);
        ApprovalWorkflowGrid myRequestsGrid = new ApprovalWorkflowGrid(myRequests, currentUserRole, approvalsService);
        myRequestsGrid.addClassName("my-requests-grid");

        contentContainer.add(title, myRequestsGrid);
    }

    private void showPendingApprovals() {
        contentContainer.removeAll();

        H2 title = new H2("Pending Approvals");
        title.addClassName("section-title");

        List<BudgetApprovals> pendingApprovals = approvalsService.getPendingApprovalsByRole(currentUserRole);
        ApprovalWorkflowGrid pendingGrid = new ApprovalWorkflowGrid(pendingApprovals, currentUserRole, approvalsService);
        pendingGrid.addClassName("pending-approvals-grid");

        contentContainer.add(title, pendingGrid);
    }

    private void showAllRequests() {
        contentContainer.removeAll();

        H2 title = new H2("All Requests");
        title.addClassName("section-title");

        // For demo, show all approvals - in production, filter by user permissions
        List<BudgetApprovals> allApprovals = approvalsService.getPendingApprovalsByRole("ADMIN");
        ApprovalWorkflowGrid allGrid = new ApprovalWorkflowGrid(allApprovals, currentUserRole, approvalsService);
        allGrid.addClassName("all-requests-grid");

        contentContainer.add(title, allGrid);
    }

    private void showNewRequestForm() {
        // Create and show the request form dialog
        List<DepartmentBudget> departments = budgetService.getBudgetSummary(fiscalYear.getValue()).getDepartmentBudgets();
        requestForm = new ApprovalRequestForm(departments, currentUserName, approvalsService);
        requestForm.addClassName("approval-request-form");
        
        requestForm.addSaveListener(e -> {
            showNotification("Budget request submitted successfully", NotificationVariant.LUMO_SUCCESS);
            refreshCurrentView();
        });
        
        requestForm.open();
    }

    private void filterApprovals() {
        // Implement filtering logic based on search field and filters
        String searchTerm = searchField.getValue();
        String status = statusFilter.getValue();
        String priority = priorityFilter.getValue();
        
        // For now, just refresh the current view
        refreshCurrentView();
    }

    private void refreshCurrentView() {
        Tab selectedTab = navigationTabs.getSelectedTab();
        if (selectedTab != null) {
            // Trigger the selected change event to refresh the view
            navigationTabs.setSelectedTab(selectedTab);
        }
    }

    private List<BudgetApprovals> getRecentApprovals() {
        // Get recent approvals based on user role
        switch (currentUserRole) {
            case "BLO":
                return approvalsService.getApprovalsByRequester(currentUserName);
            case "HOD":
            case "ADMIN":
            case "CFO":
                return approvalsService.getPendingApprovalsByRole(currentUserRole);
            default:
                return List.of();
        }
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
