package com.methaltech.application.views.budget;

import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OrganisationCOADialog extends Dialog {

    private final OrganisationService organisationService;
    private final CoaService coaService;
    private Organisation currentOrganisation;
    private final BudgetService budgetService;

    // UI Components
    private ComboBox<Organisation> organisationCombo;
    private ComboBox<Budget> budgetCombo;
    private TextField searchField;
    private Grid<COA> availableCoaGrid;
    private Grid<COA> assignedCoaGrid;
    private Button addSelectedButton;
    private Button removeSelectedButton;
    private Button addAllButton;
    private Button removeAllButton;
    private Button saveButton;
    private Button cancelButton;

    // Data providers
    private ListDataProvider<COA> availableCoaProvider;
    private ListDataProvider<COA> assignedCoaProvider;

    // Selected items
    private Set<COA> selectedAvailableCoa = new HashSet<>();
    private Set<COA> selectedAssignedCoa = new HashSet<>();

    // Data lists
    private List<COA> allAvailableCoa = new ArrayList<>();
    private List<COA> currentAssignedCoa = new ArrayList<>();

    // Count badge references
    private Span availableCountBadge;
    private Span assignedCountBadge;

    public OrganisationCOADialog(OrganisationService organisationService, CoaService coaService, BudgetService budgetService) {
        this.organisationService = organisationService;
        this.coaService = coaService;
        this.budgetService = budgetService;

        setWidth("1200px");
        setHeight("800px");
        setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("organisation-coa-dialog");

        createDialogContent();
        setupEventHandlers();
        loadInitialData();
    }

    private void createDialogContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("org-coa-dialog-layout");

        // Header
        createHeader(layout);

        // Selection controls
        createSelectionControls(layout);

        // Main content area with grids
        createMainContent(layout);

        // Action buttons
        createActionButtons(layout);

        add(layout);
    }

    private void createHeader(VerticalLayout layout) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.addClassName("dialog-header");

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setSpacing(false);
        titleSection.setPadding(false);

        H2 title = new H2("Organisation COA Management");
        title.addClassName("dialog-title");

        Span subtitle = new Span("Assign Chart of Accounts to Organisations");
        subtitle.addClassName("dialog-subtitle");

        titleSection.add(title, subtitle);

        Icon orgIcon = new Icon(VaadinIcon.BUILDING);
        orgIcon.addClassName("dialog-icon");

        header.add(titleSection, orgIcon);
        layout.add(header);
    }

    private void createSelectionControls(VerticalLayout layout) {
        Div controlsCard = new Div();
        controlsCard.addClassName("selection-controls-card");

        VerticalLayout controlsLayout = new VerticalLayout();
        controlsLayout.setSpacing(true);
        controlsLayout.setPadding(false);

        H3 controlsTitle = new H3("Selection Controls");
        controlsTitle.addClassName("controls-title");

        HorizontalLayout selectorsRow = new HorizontalLayout();
        selectorsRow.setWidthFull();
        selectorsRow.setSpacing(true);
        selectorsRow.addClassName("selectors-row");

        // Budget selector
        budgetCombo = new ComboBox<>("Budget");
        budgetCombo.setPlaceholder("Select budget...");
        budgetCombo.setItemLabelGenerator(Budget::getFinancialYear);
        budgetCombo.addClassName("budget-selector");
        budgetCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadOrganisations(e.getValue());
                loadAvailableCOA(e.getValue());
            }
        });

        // Organisation selector
        organisationCombo = new ComboBox<>("Organisation");
        organisationCombo.setPlaceholder("Select organisation...");
        organisationCombo.setItemLabelGenerator(org -> org.getName() + " (" + org.getCode() + ")");
        organisationCombo.addClassName("organisation-selector");
        organisationCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                currentOrganisation = e.getValue();
                loadAssignedCOA();
                updateGrids();
            }
        });

        // Search field
        searchField = new TextField("Search COA");
        searchField.setPlaceholder("Search by code or name...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("coa-search");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterAvailableCOA());

        selectorsRow.add(budgetCombo, organisationCombo, searchField);
        selectorsRow.setFlexGrow(1, budgetCombo);
        selectorsRow.setFlexGrow(1, organisationCombo);
        selectorsRow.setFlexGrow(1, searchField);

        controlsLayout.add(controlsTitle, selectorsRow);
        controlsCard.add(controlsLayout);
        layout.add(controlsCard);
    }

    private void createMainContent(VerticalLayout layout) {
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setSpacing(true);
        mainContent.addClassName("main-content-area");
        mainContent.setHeight("400px");

        // Available COA section
        VerticalLayout availableSection = createAvailableCoaSection();

        // Transfer buttons section
        VerticalLayout transferSection = createTransferButtonsSection();

        // Assigned COA section
        VerticalLayout assignedSection = createAssignedCoaSection();

        mainContent.add(availableSection, transferSection, assignedSection);
        mainContent.setFlexGrow(2, availableSection);
        mainContent.setFlexGrow(0, transferSection);
        mainContent.setFlexGrow(2, assignedSection);

        layout.add(mainContent);
    }

    private VerticalLayout createAvailableCoaSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(false);
        section.addClassName("available-coa-section");

        HorizontalLayout sectionHeader = new HorizontalLayout();
        sectionHeader.setWidthFull();
        sectionHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 title = new H3("Available COA Accounts");
        title.addClassName("section-title");

        availableCountBadge = new Span("0");
        availableCountBadge.addClassName("count-badge");
        availableCountBadge.addClassName("available-count");

        sectionHeader.add(title, availableCountBadge);

        // Available COA Grid
        availableCoaGrid = new Grid<>(COA.class, false);
        setupAvailableCoaGrid();

        section.add(sectionHeader, availableCoaGrid);
        return section;
    }

    private VerticalLayout createAssignedCoaSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(false);
        section.addClassName("assigned-coa-section");

        HorizontalLayout sectionHeader = new HorizontalLayout();
        sectionHeader.setWidthFull();
        sectionHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 title = new H3("Assigned COA Accounts");
        title.addClassName("section-title");

        assignedCountBadge = new Span("0");
        assignedCountBadge.addClassName("count-badge");
        assignedCountBadge.addClassName("assigned-count");

        sectionHeader.add(title, assignedCountBadge);

        // Assigned COA Grid
        assignedCoaGrid = new Grid<>(COA.class, false);
        setupAssignedCoaGrid();

        section.add(sectionHeader, assignedCoaGrid);
        return section;
    }

    private VerticalLayout createTransferButtonsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(true);
        section.addClassName("transfer-buttons-section");
        section.setAlignItems(FlexComponent.Alignment.CENTER);
        section.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Add selected button
        addSelectedButton = new Button("Add Selected", new Icon(VaadinIcon.ARROW_RIGHT));
        addSelectedButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        addSelectedButton.addClassName("transfer-button");
        addSelectedButton.setEnabled(false);
        addSelectedButton.addClickListener(e -> addSelectedCoa());

        // Remove selected button
        removeSelectedButton = new Button("Remove Selected", new Icon(VaadinIcon.ARROW_LEFT));
        removeSelectedButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        removeSelectedButton.addClassName("transfer-button");
        removeSelectedButton.setEnabled(false);
        removeSelectedButton.addClickListener(e -> removeSelectedCoa());

        // Add all button
        addAllButton = new Button("Add All", new Icon(VaadinIcon.ARROW_FORWARD));
        addAllButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        addAllButton.addClassName("transfer-button");
        addAllButton.addClickListener(e -> addAllCoa());

        // Remove all button
        removeAllButton = new Button("Remove All", new Icon(VaadinIcon.ARROW_BACKWARD));
        removeAllButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);
        removeAllButton.addClassName("transfer-button");
        removeAllButton.addClickListener(e -> removeAllCoa());

        section.add(addSelectedButton, removeSelectedButton, addAllButton, removeAllButton);
        return section;
    }

    private void setupAvailableCoaGrid() {
        availableCoaGrid.addClassName("available-coa-grid");
        availableCoaGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        availableCoaGrid.setWidthFull();
        availableCoaGrid.setHeight("350px");
        availableCoaGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        // COA Code column
        availableCoaGrid.addColumn(COA::getCode)
                .setHeader("COA Code")
                .setWidth("120px")
                .setFlexGrow(0)
                .setSortable(true);

        // COA Name column
        availableCoaGrid.addColumn(COA::getName)
                .setHeader("Account Name")
                .setWidth("250px")
                .setFlexGrow(1)
                .setSortable(true);

        // Hierarchy column
        availableCoaGrid.addColumn(new ComponentRenderer<>(this::createHierarchyInfo))
                .setHeader("Hierarchy")
                .setWidth("200px")
                .setFlexGrow(0);

        // Status column
        availableCoaGrid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("100px")
                .setFlexGrow(0);

        // Selection handling
        availableCoaGrid.addSelectionListener(selection -> {
            selectedAvailableCoa = selection.getAllSelectedItems();
            updateTransferButtonStates();
        });

        // Data provider
        availableCoaProvider = new ListDataProvider<>(allAvailableCoa);
        availableCoaGrid.setDataProvider(availableCoaProvider);
    }

    private void setupAssignedCoaGrid() {
        assignedCoaGrid.addClassName("assigned-coa-grid");
        assignedCoaGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        assignedCoaGrid.setWidthFull();
        assignedCoaGrid.setHeight("350px");
        assignedCoaGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        // COA Code column
        assignedCoaGrid.addColumn(COA::getCode)
                .setHeader("COA Code")
                .setWidth("120px")
                .setFlexGrow(0)
                .setSortable(true);

        // COA Name column
        assignedCoaGrid.addColumn(COA::getName)
                .setHeader("Account Name")
                .setWidth("250px")
                .setFlexGrow(1)
                .setSortable(true);

        // Hierarchy column
        assignedCoaGrid.addColumn(new ComponentRenderer<>(this::createHierarchyInfo))
                .setHeader("Hierarchy")
                .setWidth("200px")
                .setFlexGrow(0);

        // Status column
        assignedCoaGrid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("100px")
                .setFlexGrow(0);

        // Selection handling
        assignedCoaGrid.addSelectionListener(selection -> {
            selectedAssignedCoa = selection.getAllSelectedItems();
            updateTransferButtonStates();
        });

        // Data provider
        assignedCoaProvider = new ListDataProvider<>(currentAssignedCoa);
        assignedCoaGrid.setDataProvider(assignedCoaProvider);
    }

    private Div createHierarchyInfo(COA coa) {
        Div container = new Div();
        container.addClassName("hierarchy-info");

        StringBuilder hierarchy = new StringBuilder();

        if (coa.getCoalevel1() != null) {
            hierarchy.append(coa.getCoalevel1().getName());
        }

        if (coa.getCoalevel11() != null) {
            if (hierarchy.length() > 0) {
                hierarchy.append(" > ");
            }
            hierarchy.append(coa.getCoalevel11().getName());
        }

        if (coa.getCoalevel12() != null) {
            if (hierarchy.length() > 0) {
                hierarchy.append(" > ");
            }
            hierarchy.append(coa.getCoalevel12().getName());
        }

        if (coa.getCoalevel13() != null) {
            if (hierarchy.length() > 0) {
                hierarchy.append(" > ");
            }
            hierarchy.append(coa.getCoalevel13().getName());
        }

        Span hierarchyText = new Span(hierarchy.toString());
        hierarchyText.addClassName("hierarchy-text");

        container.add(hierarchyText);
        return container;
    }

    private Div createStatusBadge(COA coa) {
        Div badge = new Div();
        badge.addClassName("status-badge");

        if (coa.isStateOpen()) {
            badge.addClassName("status-active");
            badge.setText("Active");
        } else {
            badge.addClassName("status-inactive");
            badge.setText("Inactive");
        }

        return badge;
    }

    private void createActionButtons(VerticalLayout layout) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName("dialog-buttons");

        cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("cancel-button");
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Save Changes");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClassName("save-button");
        saveButton.addClickListener(e -> saveChanges());
        saveButton.setEnabled(false);

        buttonLayout.add(cancelButton, saveButton);
        layout.add(buttonLayout);
    }

    private void setupEventHandlers() {
        // Enable save button when there are changes
        organisationCombo.addValueChangeListener(e -> {
            updateSaveButtonState();
            // Clear selections when organisation changes
            selectedAvailableCoa.clear();
            selectedAssignedCoa.clear();
            availableCoaGrid.deselectAll();
            assignedCoaGrid.deselectAll();
            updateTransferButtonStates();
        });

        // Update button states when budget changes
        budgetCombo.addValueChangeListener(e -> {
            selectedAvailableCoa.clear();
            selectedAssignedCoa.clear();
            if (availableCoaGrid != null) {
                availableCoaGrid.deselectAll();
            }
            if (assignedCoaGrid != null) {
                assignedCoaGrid.deselectAll();
            }
            updateTransferButtonStates();
        });
    }

    private void loadInitialData() {
        try {
            // Load budgets
            List<Budget> budgets = budgetService.getBudgets();
            budgetCombo.setItems(budgets);

            // Auto-select most recent budget
            if (!budgets.isEmpty()) {
                budgetCombo.setValue(budgets.get(0));
            }
        } catch (Exception e) {
            showNotification("Error loading initial data: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadOrganisations(Budget budget) {
        try {
            List<Organisation> organisations = organisationService.getOrganisationsByBudget(budget);
            organisationCombo.setItems(organisations);
            organisationCombo.clear();
            currentOrganisation = null;
            updateGrids();
        } catch (Exception e) {
            showNotification("Error loading organisations: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadAvailableCOA(Budget budget) {
        try {
            allAvailableCoa = coaService.getCOAByBudgetAndActive(budget)
                    .stream()
                    .filter(coa -> coa.getCode() != null && coa.getCode().startsWith("1"))
                    .collect(Collectors.toList());
            updateGrids();
            updateCountBadges();
        } catch (Exception e) {
            showNotification("Error loading COA accounts: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadAssignedCOA() {
        if (currentOrganisation != null) {
            try {
                // Load COA accounts using service to avoid lazy loading issues
                List<COA> orgCoaAccounts = coaService.getCOAByOrganisation(currentOrganisation)
                        .stream()
                        .filter(coa -> coa.getCode() != null && coa.getCode().startsWith("1"))
                        .collect(Collectors.toList());
                currentAssignedCoa = new ArrayList<>(orgCoaAccounts);
                updateGrids();
                updateCountBadges();
                updateSaveButtonState();
            } catch (Exception e) {
                showNotification("Error loading assigned COA: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private void updateGrids() {
        if (currentOrganisation != null) {
            // Filter available COA to exclude already assigned ones
            List<COA> filteredAvailable = allAvailableCoa.stream()
                    .filter(coa -> !currentAssignedCoa.contains(coa))
                    .collect(Collectors.toList());

            // Update data providers with new data
            availableCoaProvider = new ListDataProvider<>(filteredAvailable);
            availableCoaGrid.setDataProvider(availableCoaProvider);

            assignedCoaProvider = new ListDataProvider<>(new ArrayList<>(currentAssignedCoa));
            assignedCoaGrid.setDataProvider(assignedCoaProvider);
        } else {
            // Clear both grids if no organisation selected
            availableCoaProvider = new ListDataProvider<>(new ArrayList<>());
            availableCoaGrid.setDataProvider(availableCoaProvider);

            assignedCoaProvider = new ListDataProvider<>(new ArrayList<>());
            assignedCoaGrid.setDataProvider(assignedCoaProvider);
        }

        updateCountBadges();
        updateTransferButtonStates();
    }

    private void filterAvailableCOA() {
        String searchTerm = searchField.getValue();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            updateGrids();
            return;
        }

        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        List<COA> filteredCoa = allAvailableCoa.stream()
                .filter(coa -> !currentAssignedCoa.contains(coa))
                .filter(coa
                        -> (coa.getCode() != null && coa.getCode().toLowerCase().contains(lowerSearchTerm))
                || (coa.getName() != null && coa.getName().toLowerCase().contains(lowerSearchTerm))
                )
                .collect(Collectors.toList());

        availableCoaProvider = new ListDataProvider<>(filteredCoa);
        availableCoaGrid.setDataProvider(availableCoaProvider);
        updateCountBadges();
    }

    private void addSelectedCoa() {
        if (!selectedAvailableCoa.isEmpty()) {
            try {
                // Store count before clearing selection
                int selectedCount = selectedAvailableCoa.size();

                // Ensure we have a modifiable list
                if (currentAssignedCoa == null) {
                    currentAssignedCoa = new ArrayList<>();
                }
                // Create new modifiable list if current one is unmodifiable
                if (!(currentAssignedCoa instanceof ArrayList)) {
                    currentAssignedCoa = new ArrayList<>(currentAssignedCoa);
                }

                // Add selected items to assigned list
                currentAssignedCoa.addAll(selectedAvailableCoa);

                // Clear selections and refresh grids
                //selectedAvailableCoa.clear();
                availableCoaGrid.deselectAll();

                // Force refresh both data providers
                updateGrids();
                updateSaveButtonState();

                showNotification("COA accounts added successfully (" + selectedCount + " items)",
                        NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                showNotification("Error adding COA accounts: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private void removeSelectedCoa() {
        if (!selectedAssignedCoa.isEmpty()) {
            try {
                // Store count before clearing selection
                int selectedCount = selectedAssignedCoa.size();

                // Ensure we have a modifiable list
                if (currentAssignedCoa == null) {
                    currentAssignedCoa = new ArrayList<>();
                }
                // Create new modifiable list if current one is unmodifiable
                if (!(currentAssignedCoa instanceof ArrayList)) {
                    currentAssignedCoa = new ArrayList<>(currentAssignedCoa);
                }

                currentAssignedCoa.removeAll(selectedAssignedCoa);

                // Clear selections and refresh grids
                //selectedAssignedCoa.clear();
                assignedCoaGrid.deselectAll();

                // Force refresh both data providers
                updateGrids();
                updateSaveButtonState();

                showNotification("COA accounts removed successfully (" + selectedCount + " items)",
                        NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                showNotification("Error removing COA accounts: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private void addAllCoa() {
        try {
            // Get current available items from the data provider
            List<COA> availableItems = new ArrayList<>();
            availableCoaProvider.getItems().forEach(availableItems::add);

            if (availableItems.isEmpty()) {
                showNotification("No COA accounts available to add", NotificationVariant.LUMO_WARNING);
                return;
            }

            int addedCount = availableItems.size();

            // Ensure we have a modifiable list
            if (currentAssignedCoa == null) {
                currentAssignedCoa = new ArrayList<>();
            }
            // Create new modifiable list if current one is unmodifiable
            if (!(currentAssignedCoa instanceof ArrayList)) {
                currentAssignedCoa = new ArrayList<>(currentAssignedCoa);
            }

            currentAssignedCoa.addAll(availableItems);

            // Clear selections and refresh grids
            //selectedAvailableCoa.clear();
            availableCoaGrid.deselectAll();

            // Force refresh both data providers
            updateGrids();
            updateSaveButtonState();

            showNotification("All available COA accounts added (" + addedCount + " items)",
                    NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            showNotification("Error adding all COA accounts: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void removeAllCoa() {
        try {
            if (currentAssignedCoa == null || currentAssignedCoa.isEmpty()) {
                showNotification("No COA accounts to remove", NotificationVariant.LUMO_WARNING);
                return;
            }

            int removedCount = currentAssignedCoa.size();

            // Clear the assigned list
            currentAssignedCoa.clear();

            // Clear selections and refresh grids
           // selectedAssignedCoa.clear();
            assignedCoaGrid.deselectAll();

            // Force refresh both data providers
            updateGrids();
            updateSaveButtonState();

            showNotification("All COA accounts removed (" + removedCount + " items)",
                    NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            showNotification("Error removing all COA accounts: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateCountBadges() {
        if (availableCountBadge != null && availableCoaProvider != null) {
            long count = 0;
            for (COA coa : availableCoaProvider.getItems()) {
                count++;
            }
            availableCountBadge.setText(String.valueOf(count));
        }

        if (assignedCountBadge != null) {
            assignedCountBadge.setText(String.valueOf(currentAssignedCoa.size()));
        }
    }

    private void updateTransferButtonStates() {
        addSelectedButton.setEnabled(!selectedAvailableCoa.isEmpty() && currentOrganisation != null);
        removeSelectedButton.setEnabled(!selectedAssignedCoa.isEmpty() && currentOrganisation != null);

        // Check if there are available items
        boolean hasAvailableItems = false;
        if (availableCoaProvider != null) {
            hasAvailableItems = availableCoaProvider.getItems()
                    .stream()
                    .findAny()
                    .isPresent();
        }
        addAllButton.setEnabled(hasAvailableItems && currentOrganisation != null);
        removeAllButton.setEnabled(!currentAssignedCoa.isEmpty() && currentOrganisation != null);
    }

    private void updateSaveButtonState() {
        if (currentOrganisation != null) {
            try {
                // Load original COA using service to avoid lazy loading
                List<COA> originalCoa = coaService.getCOAByOrganisation(currentOrganisation)
                        .stream()
                        .filter(coa -> coa.getCode() != null && coa.getCode().startsWith("1"))
                        .collect(Collectors.toList());
                Set<COA> originalCoaSet = new HashSet<>(originalCoa);
                Set<COA> currentCoaSet = new HashSet<>(currentAssignedCoa);
                boolean hasChanges = !originalCoaSet.equals(currentCoaSet);
                saveButton.setEnabled(hasChanges);
            } catch (Exception e) {
                // If we can't check for changes, enable save button
                saveButton.setEnabled(true);
            }
        } else {
            saveButton.setEnabled(false);
        }
    }

    private void saveChanges() {
        if (currentOrganisation == null) {
            showNotification("Please select an organisation first", NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // First, remove all existing COA assignments for this organisation
            List<COA> existingCoa = coaService.getCOAByOrganisation(currentOrganisation)
                    .stream()
                    .filter(coa -> coa.getCode() != null && coa.getCode().startsWith("1"))
                    .collect(Collectors.toList());

            // Unassign existing COA
            for (COA coa : existingCoa) {
                coaService.unassignFromOrganisation(coa.getId());
            }

            // Assign new COA
            for (COA coa : currentAssignedCoa) {
                coaService.assignToOrganisation(coa.getId(), currentOrganisation);
            }

            showNotification("COA assignments saved successfully (" + currentAssignedCoa.size() + " accounts assigned)",
                    NotificationVariant.LUMO_SUCCESS);

            // Fire save event
            fireEvent(new SaveEvent(this, currentOrganisation));
            close();

        } catch (Exception e) {
            showNotification("Error saving COA assignments: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    public void setOrganisation(Organisation organisation) {
        this.currentOrganisation = organisation;
        if (organisation != null) {
            organisationCombo.setValue(organisation);
            if (organisation.getBudget() != null) {
                budgetCombo.setValue(organisation.getBudget());
            }
        }
    }

    // Event handling
    public static class SaveEvent extends ComponentEvent<OrganisationCOADialog> {

        private final Organisation organisation;

        public SaveEvent(OrganisationCOADialog source, Organisation organisation) {
            super(source, false);
            this.organisation = organisation;
        }

        public Organisation getOrganisation() {
            return organisation;
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
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
