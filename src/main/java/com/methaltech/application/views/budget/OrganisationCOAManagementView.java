package com.methaltech.application.views.budget;

import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route(value = "organisation-coa", layout = MainLayout.class)
@PageTitle("Organisation COA Management")
@SpringComponent
@UIScope
@RolesAllowed({"ADMIN"})
public class OrganisationCOAManagementView extends VerticalLayout {

    private final OrganisationService organisationService;
    private final CoaService coaService;
    private final BudgetService budgetService;

    private Tabs navigationTabs;
    private VerticalLayout contentContainer;
    private TextField searchField;
    private ComboBox<Budget> budgetFilter;
    private Button manageCoaButton;
    private Button refreshButton;

    private Grid<Organisation> organisationGrid;
    private OrganisationCOADialog coaDialog;
    private Organisation organ = new Organisation();

    private String currentUserRole = "BLO";
    private String currentUserName = "BLO User";
    private Budget selectedBudget;

    @Autowired
    public OrganisationCOAManagementView(OrganisationService organisationService, CoaService coaService, BudgetService budgetService) {
        this.organisationService = organisationService;
        this.coaService = coaService;
        this.budgetService = budgetService;

        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        addClassName("organisation-coa-view");

        createHeader();
        createNavigationTabs();
        createMainContent();
        loadInitialData();
    }

    private void createHeader() {
        Div header = new Div();
        header.addClassName("org-coa-header");
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

        H1 title = new H1("Organisation COA Management");
        title.addClassName("org-coa-title");

        Span subtitle = new Span("Assign Chart of Accounts to Organisations for budget control");
        subtitle.addClassName("org-coa-subtitle");

        // Budget selector
        HorizontalLayout budgetLayout = new HorizontalLayout();
        budgetLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        budgetLayout.setSpacing(true);
        budgetLayout.setPadding(false);

        Span budgetLabel = new Span("Budget:");
        budgetLabel.addClassName("budget-label");

        budgetFilter = new ComboBox<>();
        budgetFilter.setPlaceholder("Select budget...");
        budgetFilter.setItemLabelGenerator(Budget::getFinancialYear);
        budgetFilter.addClassName("budget-filter");
        budgetFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedBudget = e.getValue();
                loadOrganisations();
            }
        });

        budgetLayout.add(budgetLabel, budgetFilter);
        leftSide.add(title, subtitle, budgetLayout);

        // Right side - Search and actions
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);

        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Search organisations...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("org-search");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterOrganisations());

        // Manage COA button
        manageCoaButton = new Button("Manage COA", new Icon(VaadinIcon.COG));
        manageCoaButton.addClassName("manage-coa-button");
        manageCoaButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        manageCoaButton.setEnabled(false);
        manageCoaButton.addClickListener(e -> showCOAManagementDialog(organ));

        // Refresh button
        refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClassName("refresh-button");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        refreshButton.addClickListener(e -> {
            loadOrganisations();
            showNotification("Data refreshed successfully", NotificationVariant.LUMO_SUCCESS);
        });

        rightSide.add(searchField, manageCoaButton, refreshButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void createNavigationTabs() {
        navigationTabs = new Tabs();
        navigationTabs.addClassName("org-coa-navigation");
        navigationTabs.setWidthFull();

        Tab overviewTab = new Tab(new Icon(VaadinIcon.DASHBOARD), new Span("Overview"));
        Tab organisationsTab = new Tab(new Icon(VaadinIcon.BUILDING), new Span("Organisations"));
        Tab assignmentsTab = new Tab(new Icon(VaadinIcon.CONNECT), new Span("COA Assignments"));
        Tab statisticsTab = new Tab(new Icon(VaadinIcon.CHART), new Span("Statistics"));

        overviewTab.addClassName("nav-tab");
        organisationsTab.addClassName("nav-tab");
        assignmentsTab.addClassName("nav-tab");
        statisticsTab.addClassName("nav-tab");

        navigationTabs.add(overviewTab, organisationsTab, assignmentsTab, statisticsTab);
        navigationTabs.setSelectedTab(overviewTab);

        navigationTabs.addSelectedChangeListener(e -> {
            Tab selectedTab = e.getSelectedTab();
            if (selectedTab == overviewTab) {
                showOverview();
            } else if (selectedTab == organisationsTab) {
                showOrganisations();
            } else if (selectedTab == assignmentsTab) {
                showAssignments();
            } else if (selectedTab == statisticsTab) {
                showStatistics();
            }
        });

        add(navigationTabs);
    }

    private void createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("org-coa-main-content");
        mainContent.setWidthFull();

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.addClassName("org-coa-content-container");

        mainContent.add(contentContainer);
        add(mainContent);
    }

    private void loadInitialData() {
        try {
            List<Budget> budgets = budgetService.getBudgets();
            budgetFilter.setItems(budgets);

            // Auto-select most recent budget
            if (!budgets.isEmpty()) {
                budgetFilter.setValue(budgets.get(0));
            }
        } catch (Exception e) {
            showNotification("Error loading initial data: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadOrganisations() {
        if (selectedBudget == null) {
            return;
        }

        try {
            showOrganisations();
        } catch (Exception e) {
            showNotification("Error loading organisations: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showOverview() {
        contentContainer.removeAll();

        // Dashboard cards
        HorizontalLayout dashboardCards = createDashboardCards();
        dashboardCards.addClassName("org-coa-dashboard-cards");
        dashboardCards.setWidthFull();

        // Recent activity section
        VerticalLayout recentActivity = new VerticalLayout();
        recentActivity.setSpacing(true);
        recentActivity.setPadding(false);
        recentActivity.addClassName("recent-activity-section");
        recentActivity.setWidthFull();

        H2 activityTitle = new H2("Recent Organisations");
        activityTitle.addClassName("section-title");

        List<Organisation> recentOrganisations = selectedBudget != null
                ? organisationService.getOrganisationsByBudget(selectedBudget) : List.of();
        Grid<Organisation> recentGrid = createOrganisationGrid(recentOrganisations.stream().limit(10).toList());
        recentGrid.addClassName("recent-organisations-grid");
        recentGrid.setWidthFull();
        recentGrid.asSingleSelect().addValueChangeListener(e->{organ=e.getValue();});

        recentActivity.add(activityTitle, recentGrid);
        contentContainer.add(dashboardCards, recentActivity);
        contentContainer.setWidthFull();

    }

    private HorizontalLayout createDashboardCards() {
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setSpacing(true);
        cards.setPadding(false);

        /*        Long totalOrgs = organisationService.getTotalOrganisationsCount();
        Long orgsWithCOA = organisationService.getOrganisationsWithCOACount();
        Long orgsWithoutCOA = organisationService.getOrganisationsWithoutCOACount();
        Long totalAssignments = organisationService.getTotalCOAAssignmentsCount();*/

 /*        cards.add(
        createDashboardCard("Total Organisations", totalOrgs.toString(), VaadinIcon.BUILDING, "card-total"),
        createDashboardCard("With COA", orgsWithCOA.toString(), VaadinIcon.CHECK_CIRCLE, "card-with-coa"),
        createDashboardCard("Without COA", orgsWithoutCOA.toString(), VaadinIcon.CLOSE_CIRCLE, "card-without-coa"),
        createDashboardCard("Total Assignments", totalAssignments.toString(), VaadinIcon.CONNECT, "card-assignments")
        );*/
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

    private void showOrganisations() {
        contentContainer.removeAll();

        H2 title = new H2("Organisations");
        title.addClassName("section-title");

        List<Organisation> organisations = selectedBudget != null
                ? organisationService.getOrganisationsByBudget(selectedBudget) : List.of();
        organisationGrid = createOrganisationGrid(organisations);

        organisationGrid.addClassName("organisations-grid");
        organisationGrid.setWidthFull();
        organisationGrid.asSingleSelect().addValueChangeListener(e->{organ=e.getValue();});

        contentContainer.add(title, organisationGrid);
    }

    private void showAssignments() {
        contentContainer.removeAll();

        H2 title = new H2("COA Assignments");
        title.addClassName("section-title");

        // Create assignments overview
        VerticalLayout assignmentsContent = new VerticalLayout();
        assignmentsContent.setSpacing(true);
        assignmentsContent.setPadding(false);
        assignmentsContent.setWidthFull();

        if (selectedBudget != null) {
            List<Organisation> organisations = organisationService.getOrganisationsByBudget(selectedBudget);

            for (Organisation org : organisations) {
                if (org.hasCoaAccounts()) {
                    assignmentsContent.add(createAssignmentCard(org));
                }
            }

            if (assignmentsContent.getComponentCount() == 0) {
                assignmentsContent.add(createEmptyAssignmentsState());
            }
        } else {
            assignmentsContent.add(createEmptyAssignmentsState());
        }

        contentContainer.add(title, assignmentsContent);
    }

    private void showStatistics() {
        contentContainer.removeAll();

        H2 title = new H2("Assignment Statistics");
        title.addClassName("section-title");

        // Statistics content
        HorizontalLayout statsGrid = new HorizontalLayout();
        statsGrid.setWidthFull();
        statsGrid.setSpacing(true);
        statsGrid.setPadding(false);

        // COA statistics
        VerticalLayout coaStats = createCOAStatistics();
        coaStats.setWidthFull();

        // Organisation statistics
        VerticalLayout orgStats = createOrganisationStatistics();
        orgStats.setWidthFull();

        statsGrid.add(coaStats, orgStats);
        statsGrid.setFlexGrow(1, coaStats);
        statsGrid.setFlexGrow(1, orgStats);

        contentContainer.add(title, statsGrid);
    }

    private Grid<Organisation> createOrganisationGrid(List<Organisation> organisations) {
        Grid<Organisation> grid = new Grid<>(Organisation.class, false);
        grid.addClassName("organisation-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setWidthFull();
        grid.setHeight("600px");

        // Organisation code column
        grid.addColumn(Organisation::getCode)
                .setHeader("Code")
                .setWidth("120px")
                .setFlexGrow(0)
                .setSortable(true);

        // Organisation name column
        grid.addColumn(Organisation::getName)
                .setHeader("Organisation Name")
                .setWidth("300px")
                .setFlexGrow(1)
                .setSortable(true);

        // Budget column
        grid.addColumn(org -> org.getBudget() != null ? org.getBudget().getFinancialYear() : "No Budget")
                .setHeader("Budget")
                .setWidth("150px")
                .setFlexGrow(0);

        // COA count column
        grid.addColumn(new ComponentRenderer<>(this::createCoaCountInfo))
                .setHeader("COA Accounts")
                .setWidth("150px")
                .setFlexGrow(0);

        // Status column
        grid.addColumn(new ComponentRenderer<>(this::createAssignmentStatus))
                .setHeader("Assignment Status")
                .setWidth("150px")
                .setFlexGrow(0);

        // Actions column
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setHeader("Actions")
                .setWidth("150px")
                .setFlexGrow(0);

        // Selection handling
        grid.addSelectionListener(selection -> {
            Optional<Organisation> selectedOrg = selection.getFirstSelectedItem();
            if (selectedOrg.isPresent()) {
                organ = selectedOrg.get();
            } else {
                organ = null;
            }
            manageCoaButton.setEnabled(selectedOrg.isPresent());
        });

        grid.setItems(organisations);
        return grid;
    }

    private Div createCoaCountInfo(Organisation organisation) {
        Div container = new Div();
        container.addClassName("coa-count-info");

        int coaCount = organisation.getCoaCount();

        HorizontalLayout countLayout = new HorizontalLayout();
        countLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        countLayout.setSpacing(false);
        countLayout.setPadding(false);

        Icon coaIcon = new Icon(VaadinIcon.BOOK);
        coaIcon.addClassName("coa-icon");

        Span countText = new Span(String.valueOf(coaCount));
        countText.addClassName("coa-count");

        if (coaCount == 0) {
            countText.addClassName("count-zero");
            coaIcon.addClassName("icon-inactive");
        } else if (coaCount < 5) {
            countText.addClassName("count-low");
            coaIcon.addClassName("icon-warning");
        } else {
            countText.addClassName("count-good");
            coaIcon.addClassName("icon-active");
        }

        countLayout.add(coaIcon, countText);
        container.add(countLayout);

        return container;
    }

    private Div createAssignmentStatus(Organisation organisation) {
        Div badge = new Div();
        badge.addClassName("assignment-status-badge");

        if (organisation.hasCoaAccounts()) {
            badge.addClassName("status-assigned");
            badge.setText("Assigned");
        } else {
            badge.addClassName("status-unassigned");
            badge.setText("Unassigned");
        }

        return badge;
    }

    private HorizontalLayout createActionButtons(Organisation organisation) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.setPadding(false);
        actions.addClassName("action-buttons");

        // Manage COA button
        Button manageCoa = new Button(new Icon(VaadinIcon.COG));
        manageCoa.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        manageCoa.addClassName("manage-coa-action");
        manageCoa.addClickListener(e -> showCOAManagementDialog(organisation));

        // View COA button
        Button viewCoa = new Button(new Icon(VaadinIcon.EYE));
        viewCoa.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        viewCoa.addClassName("view-coa-action");
        viewCoa.addClickListener(e -> viewOrganisationCOA(organisation));

        actions.add(manageCoa, viewCoa);
        return actions;
    }

    private Div createAssignmentCard(Organisation organisation) {
        Div card = new Div();
        card.addClassName("assignment-card");

        HorizontalLayout cardHeader = new HorizontalLayout();
        cardHeader.setWidthFull();
        cardHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        cardHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout orgInfo = new VerticalLayout();
        orgInfo.setSpacing(false);
        orgInfo.setPadding(false);

        H3 orgName = new H3(organisation.getName());
        orgName.addClassName("org-name");

        Span orgCode = new Span("Code: " + organisation.getCode());
        orgCode.addClassName("org-code");

        orgInfo.add(orgName, orgCode);

        Span coaCount = new Span(organisation.getCoaCount() + " COA Accounts");
        coaCount.addClassName("coa-count-badge");

        cardHeader.add(orgInfo, coaCount);

        // COA list preview
        VerticalLayout coaPreview = new VerticalLayout();
        coaPreview.setSpacing(false);
        coaPreview.setPadding(false);
        coaPreview.addClassName("coa-preview");

        organisation.getCoaAccounts().stream()
                .limit(5)
                .forEach(coa -> {
                    HorizontalLayout coaItem = new HorizontalLayout();
                    coaItem.setAlignItems(FlexComponent.Alignment.CENTER);
                    coaItem.setSpacing(true);

                    Span coaCode = new Span(coa.getCode());
                    coaCode.addClassName("coa-code");

                    Span coaName = new Span(coa.getName());
                    coaName.addClassName("coa-name");

                    coaItem.add(coaCode, coaName);
                    coaPreview.add(coaItem);
                });

        if (organisation.getCoaCount() > 5) {
            Span moreText = new Span("... and " + (organisation.getCoaCount() - 5) + " more");
            moreText.addClassName("more-text");
            coaPreview.add(moreText);
        }

        card.add(cardHeader, coaPreview);
        return card;
    }

    private Div createEmptyAssignmentsState() {
        Div emptyState = new Div();
        emptyState.addClassName("empty-assignments-state");

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);

        Icon emptyIcon = new Icon(VaadinIcon.INFO_CIRCLE);
        emptyIcon.addClassName("empty-icon");

        H3 emptyTitle = new H3("No COA Assignments");
        emptyTitle.addClassName("empty-title");

        Span emptyMessage = new Span("No organisations have COA accounts assigned yet.");
        emptyMessage.addClassName("empty-message");

        content.add(emptyIcon, emptyTitle, emptyMessage);
        emptyState.add(content);

        return emptyState;
    }

    private VerticalLayout createCOAStatistics() {
        VerticalLayout stats = new VerticalLayout();
        stats.addClassName("coa-statistics");
        stats.setSpacing(true);
        stats.setPadding(true);

        H3 title = new H3("COA Statistics");
        title.addClassName("stats-title");

        /*        Long totalCOA = coaService.getTotalCOACount();
        Long activeCOA = coaService.getActiveCOACount();
        Long assignedCOA = coaService.getAssignedCOACount();
        Long unassignedCOA = coaService.getUnassignedCOACount();
        
        stats.add(
        title,
        createStatItem("Total COA Accounts", totalCOA.toString()),
        createStatItem("Active COA", activeCOA.toString()),
        createStatItem("Assigned COA", assignedCOA.toString()),
        createStatItem("Unassigned COA", unassignedCOA.toString())
        );*/
        return stats;
    }

    private VerticalLayout createOrganisationStatistics() {
        VerticalLayout stats = new VerticalLayout();
        stats.addClassName("org-statistics");
        stats.setSpacing(true);
        stats.setPadding(true);

        H3 title = new H3("Organisation Statistics");
        title.addClassName("stats-title");

        /*        Long totalOrgs = organisationService.getTotalOrganisationsCount();
        Long orgsWithCOA = organisationService.getOrganisationsWithCOACount();
        Long orgsWithoutCOA = organisationService.getOrganisationsWithoutCOACount();
        
        stats.add(
        title,
        createStatItem("Total Organisations", totalOrgs.toString()),
        createStatItem("With COA Assignments", orgsWithCOA.toString()),
        createStatItem("Without COA Assignments", orgsWithoutCOA.toString())
        );*/
        return stats;
    }

    private HorizontalLayout createStatItem(String label, String value) {
        HorizontalLayout item = new HorizontalLayout();
        item.setWidthFull();
        item.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        item.addClassName("stat-item");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("stat-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("stat-value");

        item.add(labelSpan, valueSpan);
        return item;
    }

    private void showCOAManagementDialog() {
        if (organ != null) {
            showCOAManagementDialog(organ);
        } else {
            showNotification("Please select an organisation first", NotificationVariant.LUMO_WARNING);
        }
    }

    private void showCOAManagementDialog(Organisation organisation) {
        coaDialog = new OrganisationCOADialog(organisationService, coaService, budgetService);
        coaDialog.setOrganisation(organisation);
        coaDialog.addClassName("coa-management-dialog");
       // showNotification(organisation.getCode() + ":" + organisation.getName(), NotificationVariant.LUMO_WARNING);
        coaDialog.addSaveListener(e -> {
            showNotification("COA assignments updated successfully", NotificationVariant.LUMO_SUCCESS);
            refreshCurrentView();
        });

        coaDialog.open();
    }

    private void viewOrganisationCOA(Organisation organisation) {
        showNotification("View COA for: " + organisation.getName(), NotificationVariant.LUMO_PRIMARY);
        // Future: Open detailed view of organisation's COA accounts
    }

    private void filterOrganisations() {
        // Implementation for filtering organisations
        refreshCurrentView();
    }

    private void refreshCurrentView() {
        Tab selectedTab = navigationTabs.getSelectedTab();
        if (selectedTab != null) {
            navigationTabs.setSelectedTab(selectedTab);
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
