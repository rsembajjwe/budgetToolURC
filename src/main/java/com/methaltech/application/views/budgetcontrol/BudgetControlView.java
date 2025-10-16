package com.methaltech.application.views.budgetcontrol;

import com.methaltech.application.data.bgtool.service.BudgetControlService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetControl;
import com.methaltech.application.data.entity.bgtool.BudgetSummary;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.security.AuthenticatedUser;
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
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "budget-controls", layout = MainLayout.class)
@PageTitle("Budget Control Management")
@SpringComponent
@UIScope
@RolesAllowed({"BLO", "HOD", "ADMIN", "CFO"})
public class BudgetControlView extends VerticalLayout {

    private final BudgetControlService controlService;
    private final BudgetService budgetService;
    private AuthenticatedUser authenticatedUser;
    private final UserService userService;
    
    private Tabs navigationTabs;
    private VerticalLayout contentContainer;
    private TextField searchField;
    private ComboBox<BudgetControl.ControlLevel> levelFilter;
    private ComboBox<BudgetControl.ControlStatus> statusFilter;
    private ComboBox<Budget> budgetSelector;
    private Button newControlButton;
    
    private BudgetControlManagementDialog controlDialog;
    
    private String currentUserRole = "BLO";
    private String currentUserName = "BLO User";
    private Budget selectedBudget;
    private User user;

    @Autowired
    public BudgetControlView(BudgetControlService controlService, BudgetService budgetService,AuthenticatedUser authenticatedUser,
            UserService userService) {
        this.controlService = controlService;
        this.budgetService = budgetService;
        this.authenticatedUser=authenticatedUser;
        this.userService=userService;
        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        addClassName("budget-control-view");

        setUser();
        createHeader();
        createNavigationTabs();
        createMainContent();
        loadBudgetData();
        loadInitialData();
    }
    private void setUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);        
                if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
            currentUserName=user.getLastName()+" "+user.getFirstName();
            currentUserRole=user.getRoles().toString();
        }        
    }

    private void createHeader() {
        Div header = new Div();
        header.addClassName("control-header");
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

        H1 title = new H1("Budget Control Management");
        title.addClassName("control-title");

        Span subtitle = new Span("Configure and monitor budget controls at all organizational levels");
        subtitle.addClassName("control-subtitle");

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
                loadInitialData();
            }
        });

        budgetLayout.add(budgetLabel, budgetSelector);
        budgetLayout.setAlignItems(Alignment.BASELINE);
        leftSide.add(title, subtitle, budgetLayout);

        // Right side - Search and filters
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);

        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Search controls...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("control-search");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterControls());

        // Level filter
        levelFilter = new ComboBox<>("Level");
        levelFilter.setItems(BudgetControl.ControlLevel.values());
        levelFilter.setItemLabelGenerator(BudgetControl.ControlLevel::getDisplayName);
        levelFilter.addClassName("level-filter");
        levelFilter.addValueChangeListener(e -> filterControls());

        // Status filter
        statusFilter = new ComboBox<>("Status");
        statusFilter.setItems(BudgetControl.ControlStatus.values());
        statusFilter.setItemLabelGenerator(BudgetControl.ControlStatus::getDisplayName);
        statusFilter.addClassName("status-filter");
        statusFilter.addValueChangeListener(e -> filterControls());

        // New control button
        newControlButton = new Button("New Control", new Icon(VaadinIcon.PLUS));
        newControlButton.addClassName("new-control-button");
        newControlButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newControlButton.addClickListener(e -> showNewControlDialog());

        rightSide.add(searchField, levelFilter, statusFilter, newControlButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void createNavigationTabs() {
        navigationTabs = new Tabs();
        navigationTabs.addClassName("control-navigation");
        navigationTabs.setWidthFull();

        Tab dashboardTab = new Tab(new Icon(VaadinIcon.DASHBOARD), new Span("Dashboard"));
        Tab budgetTab = new Tab(new Icon(VaadinIcon.WALLET), new Span("Budget Level"));
        Tab departmentTab = new Tab(new Icon(VaadinIcon.BUILDING), new Span("Department Level"));
        Tab sectionTab = new Tab(new Icon(VaadinIcon.GRID_SMALL), new Span("Section Level"));
        Tab allTab = new Tab(new Icon(VaadinIcon.LIST), new Span("All Controls"));

        dashboardTab.addClassName("nav-tab");
        budgetTab.addClassName("nav-tab");
        departmentTab.addClassName("nav-tab");
        sectionTab.addClassName("nav-tab");
        allTab.addClassName("nav-tab");

        navigationTabs.add(dashboardTab, budgetTab, departmentTab, sectionTab, allTab);
        navigationTabs.setSelectedTab(dashboardTab);

        navigationTabs.addSelectedChangeListener(e -> {
            Tab selectedTab = e.getSelectedTab();
            if (selectedTab == dashboardTab) {
                showDashboard();
            } else if (selectedTab == budgetTab) {
                showBudgetLevelControls();
            } else if (selectedTab == departmentTab) {
                showDepartmentLevelControls();
            } else if (selectedTab == sectionTab) {
                showSectionLevelControls();
            } else if (selectedTab == allTab) {
                showAllControls();
            }
        });

        add(navigationTabs);
    }

    private void createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("control-main-content");

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.addClassName("control-content-container");

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
        } catch (Exception e) {
            showNotification("Error loading budget data: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadInitialData() {
        showDashboard();
    }

    private void showDashboard() {
        if (selectedBudget == null) {
            showEmptyState("Please select a budget to view control dashboard");
            return;
        }

        contentContainer.removeAll();

        // Dashboard cards
        HorizontalLayout dashboardCards = createDashboardCards();
        dashboardCards.addClassName("control-dashboard-cards");

        // Recent activity section
        VerticalLayout recentActivity = new VerticalLayout();
        recentActivity.setSpacing(true);
        recentActivity.setPadding(false);
        recentActivity.addClassName("recent-activity-section");

        H2 activityTitle = new H2("Active Budget Controls");
        activityTitle.addClassName("section-title");

        List<BudgetControl> activeControls = controlService.getActiveControls(selectedBudget);
        Grid<BudgetControl> activeGrid = createControlGrid(activeControls);
        activeGrid.addClassName("active-controls-grid");

        recentActivity.add(activityTitle, activeGrid);
        contentContainer.add(dashboardCards, recentActivity);
    }

    private HorizontalLayout createDashboardCards() {
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setSpacing(true);
        cards.setPadding(false);

        Long totalControls = controlService.getTotalActiveControls(selectedBudget);
        Long budgetCheckCount = controlService.getBudgetCheckControlsCount(selectedBudget);
        Long budgetStopCount = controlService.getBudgetStopControlsCount(selectedBudget);
        Long postingProhibitedCount = controlService.getPostingProhibitedControlsCount(selectedBudget);

        cards.add(
            createDashboardCard("Total Controls", totalControls.toString(), VaadinIcon.SHIELD, "card-total"),
            createDashboardCard("Budget Check", budgetCheckCount.toString(), VaadinIcon.CHECK_CIRCLE, "card-check"),
            createDashboardCard("Budget Stop", budgetStopCount.toString(), VaadinIcon.STOP, "card-stop"),
            createDashboardCard("Posting Prohibited", postingProhibitedCount.toString(), VaadinIcon.BAN, "card-prohibited")
        );

        return cards;
    }

    private Div createDashboardCard(String title, String value, VaadinIcon iconType, String cardClass) {
        Div card = new Div();
        card.addClassName("dashboard-card");
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

    private void showBudgetLevelControls() {
        contentContainer.removeAll();

        H2 title = new H2("Budget Level Controls");
        title.addClassName("section-title");

        Optional<BudgetControl> budgetControl = controlService.getBudgetLevelControl(selectedBudget);
        
        if (budgetControl.isPresent()) {
            Grid<BudgetControl> budgetGrid = createControlGrid(List.of(budgetControl.get()));
            budgetGrid.addClassName("budget-controls-grid");
            contentContainer.add(title, budgetGrid);
        } else {
            contentContainer.add(title, createEmptyControlsState("No budget level controls configured"));
        }
    }

    private void showDepartmentLevelControls() {
        contentContainer.removeAll();

        H2 title = new H2("Department Level Controls");
        title.addClassName("section-title");

        List<BudgetControl> departmentControls = controlService.getDepartmentLevelControls(selectedBudget);
        Grid<BudgetControl> departmentGrid = createControlGrid(departmentControls);
        departmentGrid.addClassName("department-controls-grid");

        contentContainer.add(title, departmentGrid);
    }

    private void showSectionLevelControls() {
        contentContainer.removeAll();

        H2 title = new H2("Section Level Controls");
        title.addClassName("section-title");

        List<BudgetControl> sectionControls = controlService.getControlsByBudget(selectedBudget).stream()
            .filter(control -> control.getControlLevel() == BudgetControl.ControlLevel.SECTION)
            .toList();
        
        Grid<BudgetControl> sectionGrid = createControlGrid(sectionControls);
        sectionGrid.addClassName("section-controls-grid");

        contentContainer.add(title, sectionGrid);
    }

    private void showAllControls() {
        contentContainer.removeAll();

        H2 title = new H2("All Budget Controls");
        title.addClassName("section-title");

        List<BudgetControl> allControls = controlService.getControlsByBudget(selectedBudget);
        Grid<BudgetControl> allGrid = createControlGrid(allControls);
        allGrid.addClassName("all-controls-grid");

        contentContainer.add(title, allGrid);
    }

    private Grid<BudgetControl> createControlGrid(List<BudgetControl> controls) {
        Grid<BudgetControl> grid = new Grid<>(BudgetControl.class, false);
        grid.addClassName("control-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setWidthFull();
        grid.setHeight("500px");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        // Control level column
        grid.addColumn(new ComponentRenderer<>(this::createControlLevelInfo))
            .setHeader("Control Level")
            .setWidth("150px")
            .setFlexGrow(0);

        // Hierarchy path column
        grid.addColumn(BudgetControl::getHierarchyPath)
            .setHeader("Hierarchy")
            .setWidth("250px")
            .setFlexGrow(1);

        // Control summary column
        grid.addColumn(new ComponentRenderer<>(this::createControlSummary))
            .setHeader("Active Controls")
            .setWidth("200px")
            .setFlexGrow(0);

        // Status column
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
            .setHeader("Status")
            .setWidth("100px")
            .setFlexGrow(0);

        // Effective date column
        grid.addColumn(new TextRenderer<>(item -> {
            LocalDateTime date = item.getEffectiveDate();
            return date != null ? date.format(formatter) : "";
        }))
                .setHeader("Effective Date")
                .setWidth("120px")
                .setFlexGrow(0)
                .setSortable(true);

        // Created by column
        grid.addColumn(BudgetControl::getCreatedBy)
            .setHeader("Created By")
            .setWidth("120px")
            .setFlexGrow(0);

        // Actions column
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
            .setHeader("Actions")
            .setWidth("150px")
            .setFlexGrow(0);

        grid.setItems(controls);
        return grid;
    }

    private Div createControlLevelInfo(BudgetControl control) {
        Div container = new Div();
        container.addClassName("control-level-info");

        Icon levelIcon = getControlLevelIcon(control.getControlLevel());
        levelIcon.addClassName("level-icon");

        Span levelName = new Span(control.getControlLevel().getDisplayName());
        levelName.addClassName("level-name");

        container.add(levelIcon, levelName);
        return container;
    }

    private Icon getControlLevelIcon(BudgetControl.ControlLevel level) {
        switch (level) {
            case BUDGET:
                return new Icon(VaadinIcon.WALLET);
            case DEPARTMENT:
                return new Icon(VaadinIcon.BUILDING);
            case SECTION:
                return new Icon(VaadinIcon.GRID_SMALL);
            default:
                return new Icon(VaadinIcon.INFO);
        }
    }

    private Div createControlSummary(BudgetControl control) {
        Div container = new Div();
        container.addClassName("control-summary");

        HorizontalLayout controlsRow = new HorizontalLayout();
        controlsRow.setSpacing(false);
        controlsRow.setPadding(false);
        controlsRow.addClassName("controls-row");

        if (control.getBudgetCheckEnabled()) {
            Span checkBadge = new Span("Check");
            checkBadge.addClassName("control-badge");
            checkBadge.addClassName("badge-check");
            controlsRow.add(checkBadge);
        }

        if (control.getBudgetStopEnabled()) {
            Span stopBadge = new Span("Stop");
            stopBadge.addClassName("control-badge");
            stopBadge.addClassName("badge-stop");
            controlsRow.add(stopBadge);
        }

        if (control.getPostingProhibited()) {
            Span prohibitedBadge = new Span("Prohibited");
            prohibitedBadge.addClassName("control-badge");
            prohibitedBadge.addClassName("badge-prohibited");
            controlsRow.add(prohibitedBadge);
        }

        if (!control.hasAnyControlEnabled()) {
            Span noBadge = new Span("No Controls");
            noBadge.addClassName("control-badge");
            noBadge.addClassName("badge-none");
            controlsRow.add(noBadge);
        }

        container.add(controlsRow);
        return container;
    }

    private Div createStatusBadge(BudgetControl control) {
        Div badge = new Div();
        badge.addClassName("status-badge");
        badge.addClassName("status-" + control.getControlStatus().name().toLowerCase());

        String color = getStatusColor(control.getControlStatus());
        badge.getStyle().set("background-color", color + "20");
        badge.getStyle().set("color", color);
        badge.getStyle().set("border", "1px solid " + color + "40");

        Span statusText = new Span(control.getControlStatus().getDisplayName());
        badge.add(statusText);

        return badge;
    }

    private String getStatusColor(BudgetControl.ControlStatus status) {
        switch (status) {
            case ACTIVE:
                return "#10b981";
            case INACTIVE:
                return "#6b7280";
            case SUSPENDED:
                return "#f59e0b";
            case EXPIRED:
                return "#ef4444";
            default:
                return "#6b7280";
        }
    }

    private HorizontalLayout createActionButtons(BudgetControl control) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.setPadding(false);
        actions.addClassName("action-buttons");

        // Edit button
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);
        editButton.addClassName("edit-button");
        editButton.addClickListener(e -> editControl(control));

        // Activate/Deactivate button
        if (control.getControlStatus() == BudgetControl.ControlStatus.ACTIVE) {
            Button deactivateButton = new Button(new Icon(VaadinIcon.PAUSE));
            deactivateButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            deactivateButton.addClassName("deactivate-button");
            deactivateButton.addClickListener(e -> deactivateControl(control));
            actions.add(editButton, deactivateButton);
        } else {
            Button activateButton = new Button(new Icon(VaadinIcon.PLAY));
            activateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            activateButton.addClassName("activate-button");
            activateButton.addClickListener(e -> activateControl(control));
            actions.add(editButton, activateButton);
        }

        return actions;
    }

    private void showNewControlDialog() {
        if (selectedBudget == null) {
            showNotification("Please select a budget first", NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            BudgetSummary budgetSummary = budgetService.getBudgetSummary(selectedBudget);
            List<DepartmentBudget> departments = budgetSummary.getDepartmentBudgets();
            
            controlDialog = new BudgetControlManagementDialog(controlService, selectedBudget, departments, currentUserName);
            controlDialog.addClassName("control-management-dialog");
            
            controlDialog.addSaveListener(e -> {
                showNotification("Budget control saved successfully", NotificationVariant.LUMO_SUCCESS);
                refreshCurrentView();
            });
            
            controlDialog.open();
        } catch (Exception e) {
            showNotification("Error loading form: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void editControl(BudgetControl control) {
        try {
            BudgetSummary budgetSummary = budgetService.getBudgetSummary(selectedBudget);
            List<DepartmentBudget> departments = budgetSummary.getDepartmentBudgets();
            
            controlDialog = new BudgetControlManagementDialog(controlService, selectedBudget, departments, currentUserName);
            controlDialog.setControl(control);
            controlDialog.addClassName("control-management-dialog");
            
            controlDialog.addSaveListener(e -> {
                showNotification("Budget control updated successfully", NotificationVariant.LUMO_SUCCESS);
                refreshCurrentView();
            });
            
            controlDialog.open();
        } catch (Exception e) {
            showNotification("Error loading edit form: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void activateControl(BudgetControl control) {
        try {
            controlService.activateControl(control.getId(), currentUserName);
            showNotification("Control activated successfully", NotificationVariant.LUMO_SUCCESS);
            refreshCurrentView();
        } catch (Exception e) {
            showNotification("Error activating control: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void deactivateControl(BudgetControl control) {
        try {
            controlService.deactivateControl(control.getId(), currentUserName);
            showNotification("Control deactivated successfully", NotificationVariant.LUMO_SUCCESS);
            refreshCurrentView();
        } catch (Exception e) {
            showNotification("Error deactivating control: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void filterControls() {
        // Implement filtering logic based on search field and filters
        refreshCurrentView();
    }

    private void refreshCurrentView() {
        Tab selectedTab = navigationTabs.getSelectedTab();
        if (selectedTab != null) {
            navigationTabs.setSelectedTab(selectedTab);
        }
    }

    private Div createEmptyControlsState(String message) {
        Div emptyState = new Div();
        emptyState.addClassName("empty-controls-state");

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);

        Icon emptyIcon = new Icon(VaadinIcon.SHIELD);
        emptyIcon.addClassName("empty-icon");

        H3 emptyTitle = new H3("No Controls Configured");
        emptyTitle.addClassName("empty-title");

        Span emptyMessage = new Span(message);
        emptyMessage.addClassName("empty-message");

        content.add(emptyIcon, emptyTitle, emptyMessage);
        emptyState.add(content);

        return emptyState;
    }

    private void showEmptyState(String message) {
        contentContainer.removeAll();
        
        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyState.addClassName("control-empty-state");
        emptyState.setHeightFull();

        Icon controlIcon = new Icon(VaadinIcon.SHIELD);
        controlIcon.addClassName("empty-state-icon");

        H2 emptyTitle = new H2("No Budget Selected");
        emptyTitle.addClassName("empty-state-title");

        Span emptyMessage = new Span(message);
        emptyMessage.addClassName("empty-state-message");

        emptyState.add(controlIcon, emptyTitle, emptyMessage);
        contentContainer.add(emptyState);
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
