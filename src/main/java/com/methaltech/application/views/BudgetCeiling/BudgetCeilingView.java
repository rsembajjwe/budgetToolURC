package com.methaltech.application.views.BudgetCeiling;

import com.methaltech.application.data.bgtool.service.BudgetCeilingService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetCeiling;
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
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

@Route(value = "budget-ceilings", layout = MainLayout.class)
@PageTitle("Budget Ceiling Management")
@SpringComponent
@UIScope
@RolesAllowed({"BLO", "HOD", "ADMIN", "CFO"})
public class BudgetCeilingView extends VerticalLayout {

    private final BudgetCeilingService ceilingService;
    private final BudgetService budgetService;

    private Tabs navigationTabs;
    private VerticalLayout contentContainer;
    private TextField searchField;
    private ComboBox<BudgetCeiling.CeilingType> typeFilter;
    private ComboBox<BudgetCeiling.CeilingStatus> statusFilter;
    private ComboBox<Budget> budgetSelector= new ComboBox<>();
    private Button newCeilingButton;

    private BudgetCeilingDashboard ceilingDashboard;
    private BudgetCeilingGrid ceilingGrid;
    private BudgetCeilingForm ceilingForm;

    private String currentUserRole = "BLO";
    private String currentUserName = "BLO User";
    private Budget selectedBudget;

    @Autowired
    public BudgetCeilingView(BudgetCeilingService ceilingService, BudgetService budgetService) {
        this.ceilingService = ceilingService;
        this.budgetService = budgetService;

        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        addClassName("budget-ceiling-view");
        
        loadFiscalYearData();

        createHeader();
        createNavigationTabs();
        createMainContent();
        loadBudgetData();
        loadInitialData();
    }

    private void createHeader() {
        Div header = new Div();
        header.addClassName("ceiling-header");
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

        H1 title = new H1("Budget Ceiling Management");
        title.addClassName("ceiling-title");

        Span subtitle = new Span("Configure and monitor budget ceilings at different organizational levels");
        subtitle.addClassName("ceiling-subtitle");

        // Budget selector
        HorizontalLayout budgetLayout = new HorizontalLayout();
        budgetLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        budgetLayout.setSpacing(true);
        budgetLayout.setPadding(false);

        Span budgetLabel = new Span("Budget:");
        budgetLabel.addClassName("budget-label");

       // budgetSelector = new ComboBox<>();
        budgetSelector.setPlaceholder("Select budget...");
        budgetSelector.addClassName("budget-selector");
        budgetSelector.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedBudget = e.getValue();
                loadInitialData();
            }
        });

        budgetLayout.add(budgetLabel, budgetSelector);
        leftSide.add(title, subtitle, budgetLayout);

        // Right side - Search and filters
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);

        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Search ceilings...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("ceiling-search");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterCeilings());

        // Type filter
        typeFilter = new ComboBox<>("Type");
        typeFilter.setItems(BudgetCeiling.CeilingType.values());
        typeFilter.setItemLabelGenerator(BudgetCeiling.CeilingType::getDisplayName);
        typeFilter.addClassName("type-filter");
        typeFilter.addValueChangeListener(e -> filterCeilings());

        // Status filter
        statusFilter = new ComboBox<>("Status");
        statusFilter.setItems(BudgetCeiling.CeilingStatus.values());
        statusFilter.setItemLabelGenerator(BudgetCeiling.CeilingStatus::getDisplayName);
        statusFilter.addClassName("status-filter");
        statusFilter.addValueChangeListener(e -> filterCeilings());

        // New ceiling button
        newCeilingButton = new Button("New Ceiling", new Icon(VaadinIcon.PLUS));
        newCeilingButton.addClassName("new-ceiling-button");
        newCeilingButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newCeilingButton.addClickListener(e -> showNewCeilingForm());

        rightSide.add(searchField, typeFilter, statusFilter, newCeilingButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void loadFiscalYearData() {
        budgetSelector.setItems(query -> budgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        budgetSelector.setItemLabelGenerator(Budget::getFinancialYear);
        Optional<Budget> recentBudget = budgetService.getMostRecurrentBudget();
        if (recentBudget.isPresent()) {
            budgetSelector.setValue(recentBudget.get());
            selectedBudget = recentBudget.get();
        }
    }

    private void createNavigationTabs() {
        navigationTabs = new Tabs();
        navigationTabs.addClassName("ceiling-navigation");
        navigationTabs.setWidthFull();

        Tab dashboardTab = new Tab(new Icon(VaadinIcon.DASHBOARD), new Span("Dashboard"));
        Tab sectionTab = new Tab(new Icon(VaadinIcon.BUILDING), new Span("Section Ceilings"));
        Tab revenueTab = new Tab(new Icon(VaadinIcon.MONEY_DEPOSIT), new Span("Revenue Source Ceilings"));
        Tab accountTab = new Tab(new Icon(VaadinIcon.BOOK), new Span("Account Code Ceilings"));
        Tab allTab = new Tab(new Icon(VaadinIcon.LIST), new Span("All Ceilings"));

        dashboardTab.addClassName("nav-tab");
        sectionTab.addClassName("nav-tab");
        revenueTab.addClassName("nav-tab");
        accountTab.addClassName("nav-tab");
        allTab.addClassName("nav-tab");

        navigationTabs.add(dashboardTab, sectionTab, revenueTab, accountTab, allTab);
        navigationTabs.setSelectedTab(dashboardTab);

        navigationTabs.addSelectedChangeListener(e -> {
            Tab selectedTab = e.getSelectedTab();
            if (selectedTab == dashboardTab) {
                showDashboard();
            } else if (selectedTab == sectionTab) {
                showSectionCeilings();
            } else if (selectedTab == revenueTab) {
                showRevenueSourceCeilings();
            } else if (selectedTab == accountTab) {
                showAccountCodeCeilings();
            } else if (selectedTab == allTab) {
                showAllCeilings();
            }
        });

        add(navigationTabs);
    }

    private void createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("ceiling-main-content");

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.addClassName("ceiling-content-container");

        mainContent.add(contentContainer);
        add(mainContent);
    }

    private void loadBudgetData() {
        // Load available budgets
        try {
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
            showEmptyState("Please select a budget to view ceiling dashboard");
            return;
        }

        contentContainer.removeAll();

        // Dashboard cards
        ceilingDashboard = new BudgetCeilingDashboard(ceilingService, selectedBudget);
        ceilingDashboard.addClassName("ceiling-dashboard-cards");

        // Recent activity section
        VerticalLayout recentActivity = new VerticalLayout();
        recentActivity.setSpacing(true);
        recentActivity.setPadding(false);
        recentActivity.addClassName("recent-activity-section");

        H2 activityTitle = new H2("Recent Ceiling Activity");
        activityTitle.addClassName("section-title");

        List<BudgetCeiling> recentCeilings = ceilingService.getCeilingsByBudget(selectedBudget);
        BudgetCeilingGrid recentGrid = new BudgetCeilingGrid(recentCeilings.stream().limit(10).toList(), currentUserRole, ceilingService);
        recentGrid.addClassName("recent-ceilings-grid");

        recentActivity.add(activityTitle, recentGrid);
        contentContainer.add(ceilingDashboard, recentActivity);
    }

    private void showSectionCeilings() {
        if (selectedBudget == null) {
            showEmptyState("Please select a budget to view section ceilings");
            return;
        }

        contentContainer.removeAll();

        H2 title = new H2("Section Level Ceilings");
        title.addClassName("section-title");

        List<BudgetCeiling> sectionCeilings = ceilingService.getSectionCeilings(selectedBudget);
        BudgetCeilingGrid sectionGrid = new BudgetCeilingGrid(sectionCeilings, currentUserRole, ceilingService);
        sectionGrid.addClassName("section-ceilings-grid");

        contentContainer.add(title, sectionGrid);
    }

    private void showRevenueSourceCeilings() {
        if (selectedBudget == null) {
            showEmptyState("Please select a budget to view revenue source ceilings");
            return;
        }

        contentContainer.removeAll();

        H2 title = new H2("Revenue Source Level Ceilings");
        title.addClassName("section-title");

        List<BudgetCeiling> revenueCeilings = ceilingService.getRevenueSourceCeilings(selectedBudget);
        BudgetCeilingGrid revenueGrid = new BudgetCeilingGrid(revenueCeilings, currentUserRole, ceilingService);
        revenueGrid.addClassName("revenue-ceilings-grid");

        contentContainer.add(title, revenueGrid);
    }

    private void showAccountCodeCeilings() {
        if (selectedBudget == null) {
            showEmptyState("Please select a budget to view account code ceilings");
            return;
        }

        contentContainer.removeAll();

        H2 title = new H2("Account Code Level Ceilings");
        title.addClassName("section-title");

        List<BudgetCeiling> accountCeilings = ceilingService.getAccountCodeCeilings(selectedBudget);
        BudgetCeilingGrid accountGrid = new BudgetCeilingGrid(accountCeilings, currentUserRole, ceilingService);
        accountGrid.addClassName("account-ceilings-grid");

        contentContainer.add(title, accountGrid);
    }

    private void showAllCeilings() {
        if (selectedBudget == null) {
            showEmptyState("Please select a budget to view all ceilings");
            return;
        }

        contentContainer.removeAll();

        H2 title = new H2("All Budget Ceilings");
        title.addClassName("section-title");

        List<BudgetCeiling> allCeilings = ceilingService.getCeilingsByBudget(selectedBudget);
        BudgetCeilingGrid allGrid = new BudgetCeilingGrid(allCeilings, currentUserRole, ceilingService);
        allGrid.addClassName("all-ceilings-grid");

        contentContainer.add(title, allGrid);
    }

    private void showNewCeilingForm() {
        if (selectedBudget == null) {
            showNotification("Please select a budget first", NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // Get departments for the form
            List<DepartmentBudget> departments = budgetService.getBudgetSummary(selectedBudget).getDepartmentBudgets();

            ceilingForm = new BudgetCeilingForm(departments, currentUserName, ceilingService, selectedBudget);
            ceilingForm.addClassName("ceiling-form");

            ceilingForm.addSaveListener(e -> {
                showNotification("Budget ceiling created successfully", NotificationVariant.LUMO_SUCCESS);
                refreshCurrentView();
            });

            ceilingForm.open();
        } catch (Exception e) {
            showNotification("Error loading form: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void filterCeilings() {
        // Implement filtering logic based on search field and filters
        String searchTerm = searchField.getValue();
        BudgetCeiling.CeilingType type = typeFilter.getValue();
        BudgetCeiling.CeilingStatus status = statusFilter.getValue();

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

    private void showEmptyState(String message) {
        contentContainer.removeAll();

        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyState.addClassName("ceiling-empty-state");
        emptyState.setHeightFull();

        Icon ceilingIcon = new Icon(VaadinIcon.SHIELD);
        ceilingIcon.addClassName("empty-state-icon");

        H2 emptyTitle = new H2("No Budget Ceilings");
        emptyTitle.addClassName("empty-state-title");

        Span emptyMessage = new Span(message);
        emptyMessage.addClassName("empty-state-message");

        emptyState.add(ceilingIcon, emptyTitle, emptyMessage);
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
