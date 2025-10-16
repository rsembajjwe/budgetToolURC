package com.methaltech.application.views.requisition;

import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.RequisitionDataService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivitySelectionDialog extends Dialog {

    private final List<Urc_Activities> activities;
    private final BudgetItemsService budgetItemsService;
    private final Budget selectedBudget;
    private final NumberFormat currencyFormat;

    private Grid<Urc_Activities> activitiesGrid;
    private Grid<BudgetItems> budgetItemsGrid;
    private Grid<AccountSummary> accountSummaryGrid;
    private TextField searchField;
    private Button selectButton;
    private Button cancelButton;

    private Urc_Activities selectedActivity;
    private UrcDeptSectionAnlDimbgt selectedSection;
    private ListDataProvider<Urc_Activities> activitiesProvider;
    List<BudgetItems> budgetItemsPerActivity = new ArrayList();
    private Span calculateSumOfAllMonthstotalText = new Span();
    private final StockUnitMeasureService sampleStockUnitMeasureService;
    private final RequisitionDataService requisitionDataService;
    private final UserService userService;
    private AuthenticatedUser authenticatedUser;
    private final OrganisationService fundsourceService;
    private final CoaService coaService;
    private final SALFLDGService sALFLDGService;
    private DeptSectionMergerService deptSectionMergerService;

    public ActivitySelectionDialog(List<Urc_Activities> activities, Urc_Activities currentSelection,
            BudgetItemsService budgetItemsService, Budget selectedBudget, StockUnitMeasureService sampleStockUnitMeasureService,
            RequisitionDataService requisitionDataService, AuthenticatedUser authenticatedUser, UserService userService,
            UrcDeptSectionAnlDimbgt selectedSection, OrganisationService fundsourceService, CoaService coaService,
            SALFLDGService sALFLDGService, DeptSectionMergerService deptSectionMergerService) {
        this.activities = activities;
        this.selectedActivity = currentSelection;
        this.budgetItemsService = budgetItemsService;
        this.selectedBudget = selectedBudget;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.sampleStockUnitMeasureService = sampleStockUnitMeasureService;
        this.requisitionDataService = requisitionDataService;
        this.selectedSection = selectedSection;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.fundsourceService = fundsourceService;
        this.coaService = coaService;
        this.sALFLDGService = sALFLDGService;
        this.deptSectionMergerService = deptSectionMergerService;

        setWidth("1400px");
        setHeight("800px");
        this.setCloseOnOutsideClick(false);
        //setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("activity-selection-dialog");

        createDialogContent();
    }

    private void createDialogContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("activity-dialog-layout");

        // Header
        createHeader(layout);

        // Search field
        createSearchSection(layout);

        // Main content area
        createMainContentArea(layout);

        // Action buttons
        // createActionButtons(layout);
        add(layout);
    }

    private void createHeader(VerticalLayout layout) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.addClassName("activity-dialog-header");

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setSpacing(false);
        titleSection.setPadding(false);

        H2 title = new H2(selectedSection.getNAME() + " Activities " + selectedBudget.getFinancialYear());
        title.addClassName("dialog-title");

        Span subtitle = new Span("Choose an activity to view budget items and account summaries");
        subtitle.addClassName("dialog-subtitle");

        titleSection.add(title, subtitle);

        Div iconContainer = new Div();
        iconContainer.addClassName("act-dialog-icon-container");

        Icon activityIcon = new Icon(VaadinIcon.TASKS);
        activityIcon.addClassName("act-dialog-icon");
        iconContainer.add(activityIcon);

        cancelButton = new Button("", new Icon(VaadinIcon.CLOSE));
        //cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("act-dialog-icon");
        cancelButton.addClickListener(e -> close());

        header.add(titleSection, iconContainer, cancelButton);
        layout.add(header);
    }

    private void createSearchSection(VerticalLayout layout) {
        HorizontalLayout searchSection = new HorizontalLayout();
        searchSection.setWidthFull();
        searchSection.setAlignItems(FlexComponent.Alignment.CENTER);
        searchSection.addClassName("search-section");

        searchField = new TextField();
        searchField.setPlaceholder("Search activities by code or name...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("activity-search");
        searchField.setWidthFull();
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterActivities());

        Span resultCount = new Span(activities.size() + " activities found");
        resultCount.addClassName("result-count");

        searchSection.add(searchField, resultCount);
        layout.add(searchSection);
    }

    private void createMainContentArea(VerticalLayout layout) {
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setHeightFull();
        mainContent.setSpacing(true);
        mainContent.addClassName("activity-main-content");

        // Left side - Activities grid
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSpacing(false);
        leftSide.setPadding(false);
        leftSide.setWidthFull();
        leftSide.addClassName("activities-section");

        H3 activitiesTitle = new H3("Activities");
        activitiesTitle.addClassName("section-title");

        activitiesGrid = createActivitiesGrid();
        activitiesGrid.addClassName("activities-grid");

        leftSide.add(activitiesTitle, activitiesGrid);

        // Right side - Budget items and account summary
        VerticalLayout rightSide = new VerticalLayout();
        rightSide.setSpacing(true);
        rightSide.setPadding(false);
        rightSide.setWidthFull();
        rightSide.addClassName("budget-details-section");

        // Budget items section (top right)
        VerticalLayout budgetItemsSection = new VerticalLayout();
        budgetItemsSection.setSpacing(false);
        budgetItemsSection.setPadding(false);
        budgetItemsSection.addClassName("budget-items-section");

        H3 budgetItemsTitle = new H3("Budget Items");
        budgetItemsTitle.addClassName("section-title");

        budgetItemsGrid = createBudgetItemsGrid();
        budgetItemsGrid.addClassName("budget-items-grid");

        Footer footer = new Footer();
        footer.getElement().getStyle().set("margin-left", "auto");
        footer.addClassName("total-footer");
        calculateSumOfAllMonthstotalText.setWidthFull();
        calculateSumOfAllMonthstotalText.addClassName("total-text");
        footer.add(calculateSumOfAllMonthstotalText);

        budgetItemsSection.add(budgetItemsTitle, budgetItemsGrid, footer);

        // Account summary section (bottom right)
        VerticalLayout accountSummarySection = new VerticalLayout();
        accountSummarySection.setSpacing(false);
        accountSummarySection.setPadding(false);
        accountSummarySection.addClassName("account-summary-section");

        H3 accountSummaryTitle = new H3("Account Code Summary");
        accountSummaryTitle.addClassName("section-title");

        accountSummaryGrid = createAccountSummaryGrid();
        accountSummaryGrid.addClassName("account-summary-grid");

        accountSummarySection.add(accountSummaryTitle, accountSummaryGrid);

        rightSide.add(budgetItemsSection, accountSummarySection);

        // Set flex ratios
        mainContent.add(leftSide, rightSide);
        mainContent.setFlexGrow(1, leftSide);
        mainContent.setFlexGrow(1, rightSide);

        layout.add(mainContent);
    }

    private Grid<Urc_Activities> createActivitiesGrid() {
        Grid<Urc_Activities> grid = new Grid<>(Urc_Activities.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setWidthFull();
        grid.setHeight("600px");

        // Activity code
        grid.addColumn(Urc_Activities::getActivityCode)
                .setHeader("Activity Code")
                .setWidth("150px")
                .setFlexGrow(0)
                .setSortable(true);

        // Activity name
        grid.addColumn(Urc_Activities::getName)
                .setHeader("Activity Name")
                .setWidth("300px")
                .setFlexGrow(1);

        // Activity budget
        grid.addColumn(new ComponentRenderer<>(this::createActivityBudgetInfo))
                .setHeader("Activity Budget")
                .setWidth("150px")
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(this::createCommittedInfo))
                .setHeader("Committed")
                .setWidth("150px")
                .setFlexGrow(0);

        // Activity budget
        grid.addColumn(new ComponentRenderer<>(this::createActivityActualInfo))
                .setHeader("Actual Exp")
                .setWidth("150px")
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(this::createActivityVarianceInfo))
                .setHeader("Balance")
                .setWidth("150px")
                .setFlexGrow(0);

        // Output
        grid.addColumn(activity -> activity.getOutput() != null
                ? (activity.getOutput().length() > 50 ? activity.getOutput().substring(0, 50) + "..." : activity.getOutput()) : "")
                .setHeader("Output")
                .setWidth("200px")
                .setFlexGrow(0);

        activitiesProvider = new ListDataProvider<>(activities);
        grid.setDataProvider(activitiesProvider);

        // Selection handling
        grid.addSelectionListener(selection -> {
            selectedActivity = selection.getFirstSelectedItem().orElse(null);
            // selectButton.setEnabled(selectedActivity != null);
            budgetItemsPerActivity = budgetItemsService.findBudgetItemsExpensesByActvity(selectedActivity);
            calculateSumOfAllMonthstotalText.setText("Total:   " + String.format("UGX %,.2f", getTotalFromItems(budgetItemsPerActivity)));

            if (selectedActivity != null) {
                loadBudgetItems();
                loadAccountSummary();
            } else {
                clearRightSideGrids();
            }
        });

        // Pre-select current activity if exists
        if (selectedActivity != null) {
            grid.select(selectedActivity);
        }

        return grid;
    }

    private Grid<BudgetItems> createBudgetItemsGrid() {
        Grid<BudgetItems> grid = new Grid<>(BudgetItems.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.setWidthFull();
        grid.setHeight("250px");

        // Item name
        Grid.Column<BudgetItems> codeColumn = grid
                .addColumn(budgetItem -> {
                    COA coacode = budgetItem.getCoacode();
                    Text label = new Text(coacode != null ? coacode.getCode() : "");
                    return label.getText(); // Get the text content
                })
                .setHeader("Code").setWidth("100px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getCoacode() != null ? budgetItem1.getCoacode().getName() : "";
                    String name2 = budgetItem2.getCoacode() != null ? budgetItem2.getCoacode().getName() : "";
                    return name1.compareTo(name2);
                });
        // Item name
        grid.addColumn(BudgetItems::getItem)
                .setHeader("Item")
                .setWidth("200px")
                .setFlexGrow(1);

        // Total cost
        grid.addColumn(new ComponentRenderer<>(this::createTotalCostInfo))
                .setHeader("Total Cost")
                .setWidth("120px")
                .setFlexGrow(0);

        return grid;
    }

    private Grid<AccountSummary> createAccountSummaryGrid() {
        Grid<AccountSummary> grid = new Grid<>(AccountSummary.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.setWidthFull();
        grid.setHeight("250px");

        // Account code
        grid.addColumn(AccountSummary::getAccountCode)
                .setHeader("Account Code")
                .setWidth("120px")
                .setFlexGrow(0);

        // Account name
        grid.addColumn(AccountSummary::getAccountName)
                .setHeader("Account Name")
                .setWidth("180px")
                .setFlexGrow(1);

        // Total budget
        grid.addColumn(new ComponentRenderer<>(this::createTotalBudgetInfo))
                .setHeader("Total Budget")
                .setWidth("120px")
                .setFlexGrow(0);

        // Total committed
        grid.addColumn(new ComponentRenderer<>(this::createTotalCommittedInfo))
                .setHeader("Committed")
                .setWidth("120px")
                .setFlexGrow(0);

        // Total requisitions
        grid.addColumn(new ComponentRenderer<>(this::createTotalRequisitionsInfo))
                .setHeader("Requisitions")
                .setWidth("120px")
                .setFlexGrow(0);

        // Total actual
        grid.addColumn(new ComponentRenderer<>(this::createTotalActualInfo))
                .setHeader("Actual")
                .setWidth("120px")
                .setFlexGrow(0);

        // Total balance
        grid.addColumn(new ComponentRenderer<>(this::createTotalBalanceInfo))
                .setHeader("Balance")
                .setWidth("120px")
                .setFlexGrow(0);

        // Item count
        grid.addColumn(AccountSummary::getItemCount)
                .setHeader("Items")
                .setWidth("80px")
                .setFlexGrow(0);

        // Actions
        grid.addColumn(new ComponentRenderer<>(this::createAccountActionButtons))
                .setHeader("Actions")
                .setWidth("100px")
                .setFlexGrow(0).setFrozenToEnd(true);

        return grid;
    }

    private Div createActivityBudgetInfo(Urc_Activities activity) {
        Div container = new Div();
        container.addClassName("activity-budget-info");
        budgetItemsPerActivity = budgetItemsService.findBudgetItemsExpensesByActvity(activity);

        Span budgetSpan = new Span(formatCurrency(getTotalFromItems(budgetItemsPerActivity)));
        budgetSpan.addClassName("activity-budget-amount");

        if (!activity.hasValidBudget()) {
            budgetSpan.addClassName("budget-zero");
        }

        container.add(budgetSpan);
        return container;
    }

    private Div createCommittedInfo(Urc_Activities activity) {
        Div container = new Div();
        container.addClassName("activity-budget-info");
        double sum = requisitionDataService.getRequestedAmountForActivityExcludingRejected(activity.getId());

        Span budgetSpan = new Span(formatCurrency(sum));
        budgetSpan.addClassName("activity-budget-amount");

        if (!activity.hasValidBudget()) {
            budgetSpan.addClassName("budget-zero");
        }

        container.add(budgetSpan);
        return container;
    }

    private Div createActivityActualInfo(Urc_Activities activity) {
        Div container = new Div();
        container.addClassName("activity-budget-info");
        double actuals = sALFLDGService.getTotalAmountByActivity(activity.getActivityCode());
        if (actuals < 0) {
            actuals = Math.abs(actuals);
        }
        Span budgetSpan = new Span(formatCurrency(actuals));
        budgetSpan.addClassName("activity-budget-amount");

        if (!activity.hasValidBudget()) {
            budgetSpan.addClassName("budget-zero");
        }

        container.add(budgetSpan);
        return container;
    }

    private Div createActivityVarianceInfo(Urc_Activities activity) {
        Div container = new Div();
        container.addClassName("activity-budget-info");
        double actuals = sALFLDGService.getTotalAmountByActivity(activity.getActivityCode());
        if (actuals < 0) {
            actuals = Math.abs(actuals);
        }
        double actualss = getTotalFromItems(budgetItemsService.findBudgetItemsExpensesByActvity(activity));
        double vari = actualss - actuals;
        Span budgetSpan = new Span(formatCurrency(vari));
        budgetSpan.addClassName("activity-budget-amount");

        if (!activity.hasValidBudget()) {
            budgetSpan.addClassName("budget-zero");
        }

        container.add(budgetSpan);
        return container;
    }

    private Div createUnitCostInfo(BudgetItems item) {
        Div container = new Div();
        container.addClassName("unit-cost-info");

        Span costSpan = new Span(item.getFormattedCost());
        costSpan.addClassName("unit-cost");

        container.add(costSpan);
        return container;
    }

    private Div createTotalCostInfo(BudgetItems item) {
        Div container = new Div();
        container.addClassName("total-cost-info");

        Span costSpan = new Span(item.getFormattedTotal());
        costSpan.addClassName("total-cost");

        container.add(costSpan);
        return container;
    }

    private Div createTotalBudgetInfo(AccountSummary summary) {
        Div container = new Div();
        container.addClassName("total-budget-info");

        Span budgetSpan = new Span(String.format("UGX %,.2f", summary.getTotalBudget()));
        budgetSpan.getElement().setProperty("title", "Total budget: " + budgetSpan.getText());
        budgetSpan.addClassName("total-budget");

        container.add(budgetSpan);
        return container;
    }

    private Div createTotalCommittedInfo(AccountSummary summary) {
        Div container = new Div();
        container.addClassName("total-committed-info");

        Span committedSpan = new Span(String.format("UGX %,.2f", summary.getTotalCommitted()));
        committedSpan.getElement().setProperty("title", "Total Commited: " + committedSpan.getText());
        committedSpan.addClassName("total-committed");

        container.add(committedSpan);
        return container;
    }

    private Div createTotalRequisitionsInfo(AccountSummary summary) {
        Div container = new Div();
        container.addClassName("total-requisitions-info");

        Span requisitionsSpan = new Span(String.format("UGX %,.2f", summary.getTotalRequisitions()));
        requisitionsSpan.getElement().setProperty("title", "Total Requistions: " + requisitionsSpan.getText());
        requisitionsSpan.addClassName("total-requisitions");

        container.add(requisitionsSpan);
        return container;
    }

    private Div createTotalActualInfo(AccountSummary summary) {
        Div container = new Div();
        container.addClassName("total-actual-info");

        Span actualSpan = new Span(String.format("UGX %,.2f", summary.getTotalActual()));
        actualSpan.getElement().setProperty("title", "Actual Total Spent: " + actualSpan.getText());
        actualSpan.addClassName("total-actual");

        container.add(actualSpan);
        return container;
    }

    private Div createTotalBalanceInfo(AccountSummary summary) {
        Div container = new Div();
        container.addClassName("total-balance-info");

        Span balanceSpan = new Span(String.format("UGX %,.2f", summary.getTotalBalance()));
        balanceSpan.getElement().setProperty("title", "Total Balance: " + balanceSpan.getText());
        balanceSpan.addClassName("total-balance");

        if (summary.getTotalBalance() < 0) {
            balanceSpan.addClassName("balance-negative");
        } else if (summary.getTotalBalance() < 10_000_000) {
            balanceSpan.addClassName("balance-low");
        } else {
            balanceSpan.addClassName("balance-good");
        }

        container.add(balanceSpan);
        return container;
    }

    private HorizontalLayout createAccountActionButtons(AccountSummary summary) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.setPadding(false);
        actions.addClassName("account-action-buttons");

        Button requisitionButton = new Button(new Icon(VaadinIcon.PLUS));
        requisitionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        requisitionButton.addClassName("requisition-button");
        requisitionButton.setTooltipText("Create Requisition");
        requisitionButton.setEnabled(summary.hasBalance());
        requisitionButton.addClickListener(e -> showRequisitionDialog(summary));

        actions.add(requisitionButton);
        return actions;
    }

    private void showRequisitionDialog(AccountSummary summary) {
        PPDARequisitionDialog requisitionDialog = new PPDARequisitionDialog(
                summary, selectedActivity, selectedBudget, budgetItemsService, sampleStockUnitMeasureService,
                requisitionDataService, authenticatedUser, userService, selectedSection, fundsourceService, coaService, deptSectionMergerService);

        requisitionDialog.addSaveListener(e -> {
            showNotification("Requisition submitted successfully", NotificationVariant.LUMO_SUCCESS);
            // Refresh the account summary grid
            loadAccountSummary();
        });
        requisitionDialog.addDialogCloseActionListener(e -> {
            loadAccountSummary();
        });

        requisitionDialog.open();
    }

    private void createActionButtons(VerticalLayout layout) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName("activity-dialog-buttons");

        selectButton = new Button("Select Activity", new Icon(VaadinIcon.CHECK));
        selectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        selectButton.addClassName("select-button");
        selectButton.setEnabled(selectedActivity != null);
        selectButton.addClickListener(e -> selectActivity());

        buttonLayout.add(selectButton);
        layout.add(buttonLayout);
    }

    private void filterActivities() {
        String searchTerm = searchField.getValue();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            activitiesProvider.clearFilters();
            return;
        }

        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        activitiesProvider.setFilter(activity
                -> (activity.getActivityCode() != null && activity.getActivityCode().toLowerCase().contains(lowerSearchTerm))
                || (activity.getName() != null && activity.getName().toLowerCase().contains(lowerSearchTerm))
        );
    }

    private void loadBudgetItems() {
        if (selectedActivity == null) {
            budgetItemsGrid.setItems();
            return;
        }

        try {
            List<BudgetItems> budgetItems = budgetItemsService.findBudgetItemsExpensesByActvity(selectedActivity);
            budgetItemsGrid.setItems(budgetItems);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showNotification("Error loading budget items: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadAccountSummary() {
        if (selectedActivity == null) {
            accountSummaryGrid.setItems();
            return;
        }

        try {
            List<BudgetItems> budgetItems = budgetItemsService.findBudgetItemsExpensesByActvity(selectedActivity);

            // Group by account code and summarize
            Map<String, List<BudgetItems>> groupedByAccount = budgetItems.stream()
                    .filter(item -> item.getCoacode() != null)
                    .collect(Collectors.groupingBy(BudgetItems::getAccountCode));

            List<AccountSummary> accountSummaries = groupedByAccount.entrySet().stream()
                    .map(entry -> {
                        String accountCode = entry.getKey();
                        List<BudgetItems> items = entry.getValue();

                        String accountName = items.get(0).getAccountName();
                        double totalBudget = items.stream().mapToDouble(BudgetItems::getCalculatedTotal).sum();

                        // Mock additional financial data - replace with actual service calls
                        //double totalCommitted = requisitionDataService.getRequestedTotalByActivityAndCoa(accountCode, selectedActivity.getId());
                        double totalCommitted = sALFLDGService.getTotalCommittedAmountByPeriodsCodeAndSection(sALFLDGService.getFinancialYearPeriods(selectedBudget), selectedActivity.getDeptSection().getANL_CODE(), accountCode);
                        double totalCommittedA = sALFLDGService.getTotalCommittedAmountByPeriodsCodeAndSectionAndActivity(sALFLDGService.getFinancialYearPeriods(selectedBudget), selectedActivity.getDeptSection().getANL_CODE(), accountCode, selectedActivity.getActivityCode());
                        double totalRequisitions = requisitionDataService.getRequestedTotalByActivityAndCoa(accountCode, selectedActivity.getId());
                        double totalActual = Math.abs(sALFLDGService.getTotalAmountByActivityAndCode(selectedActivity.getActivityCode(), accountCode));
                        double totalBalance = totalBudget - totalCommitted - totalActual;
                        double totalBalanceA = totalBudget - totalCommittedA - totalActual;
                        int itemCount = items.size();

                        return new AccountSummary(accountCode, accountName, totalBudget,
                                totalCommitted, totalRequisitions, totalActual,
                                totalBalance, itemCount,totalCommittedA,totalBalanceA);
                    })
                    .sorted((a, b) -> a.getAccountCode().compareTo(b.getAccountCode()))
                    .collect(Collectors.toList());

            accountSummaryGrid.setItems(accountSummaries);
        } catch (Exception e) {
            showNotification("Error loading account summary: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void clearRightSideGrids() {
        budgetItemsGrid.setItems();
        accountSummaryGrid.setItems();
    }

    private void selectActivity() {
        if (selectedActivity != null) {
            fireEvent(new ActivitySelectionEvent(this, selectedActivity));
            close();
        }
    }

    /*    private String formatCurrency(double amount) {
    if (Math.abs(amount) >= 1_000_000_000) {
    return String.format("UGX %.1fB", amount / 1_000_000_000);
    } else if (Math.abs(amount) >= 1_000_000) {
    return String.format("UGX %.1fM", amount / 1_000_000);
    } else if (Math.abs(amount) >= 1_000) {
    return String.format("UGX %.0fK", amount / 1_000);
    } else {
    return String.format("UGX %.0f", amount);
    }
    }*/
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return formatter.format(amount);
    }

    // Event handling
    public static class ActivitySelectionEvent extends ComponentEvent<ActivitySelectionDialog> {

        private final Urc_Activities selectedActivity;

        public ActivitySelectionEvent(ActivitySelectionDialog source, Urc_Activities selectedActivity) {
            super(source, false);
            this.selectedActivity = selectedActivity;
        }

        public Urc_Activities getSelectedActivity() {
            return selectedActivity;
        }
    }

    public Registration addSelectionListener(ComponentEventListener<ActivitySelectionEvent> listener) {
        return addListener(ActivitySelectionEvent.class, listener);
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }

    // Helper class for account summary
    public static class AccountSummary {

        private String accountCode;
        private String accountName;
        private double totalBudget;
        private double totalCommitted;
        private double totalRequisitions;
        private double totalActual;
        private double totalBalance;
        private int itemCount;
        private double totalCommittedA;
        private double totalBalanceA;

        public AccountSummary(String accountCode, String accountName, double totalBudget,
                double totalCommitted, double totalRequisitions, double totalActual,
                double totalBalance, int itemCount,double totalCommittedA,double totalBalanceA) {
            this.accountCode = accountCode;
            this.accountName = accountName;
            this.totalBudget = totalBudget;
            this.totalCommitted = totalCommitted;
            this.totalRequisitions = totalRequisitions;
            this.totalActual = totalActual;
            this.totalBalance = totalBalance;
            this.itemCount = itemCount;
            this.totalCommittedA = totalCommittedA;
            this.totalBalanceA = totalBalanceA;
        }

        public AccountSummary() {
        }

        public void setAccountCode(String accountCode) {
            this.accountCode = accountCode;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public void setTotalBudget(double totalBudget) {
            this.totalBudget = totalBudget;
        }

        public void setTotalCommitted(double totalCommitted) {
            this.totalCommitted = totalCommitted;
        }

        public void setTotalRequisitions(double totalRequisitions) {
            this.totalRequisitions = totalRequisitions;
        }

        public void setTotalActual(double totalActual) {
            this.totalActual = totalActual;
        }

        public void setTotalBalance(double totalBalance) {
            this.totalBalance = totalBalance;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }

        public String getAccountCode() {
            return accountCode;
        }

        public String getAccountName() {
            return accountName;
        }

        public double getTotalBudget() {
            return totalBudget;
        }

        public double getTotalCommitted() {
            if(totalCommitted<0)
               totalCommitted=Math.abs(totalCommitted);               
            return totalCommitted;
        }

        public double getTotalRequisitions() {
            return totalRequisitions;
        }

        public double getTotalActual() {
            return totalActual;
        }

        public double getTotalBalance() {
            if(totalBalance<0)
               totalBalance=Math.abs(totalBalance);
            return totalBalance;
        }

        public int getItemCount() {
            return itemCount;
        }

        public boolean hasBalance() {
            return totalBalance > 0;
        }


        public double getTotalCommittedA() {
            if(totalCommittedA<0)
               totalCommittedA=Math.abs(totalCommittedA);            
            return totalCommittedA;
        }

        public void setTotalCommittedA(double totalCommittedA) {
            this.totalCommittedA = totalCommittedA;
        }

            public double getTotalBalanceA() {
            if(totalBalanceA<0)
               totalBalanceA=Math.abs(totalBalanceA);                 
            return totalBalanceA;
        }

        public void setTotalBalanceA(double totalBalanceA) {
            this.totalBalanceA = totalBalanceA;
        }

    }

    public double getTotalFromItems(List<BudgetItems> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(BudgetItems::getCalculatedTotal)
                .sum();
    }
}
