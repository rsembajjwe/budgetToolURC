package com.methaltech.application.views.requisition;

import com.itextpdf.io.font.constants.StandardFonts;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.views.requisition.ActivitySelectionDialog.AccountSummary;

import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.UI;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.RequisitionDataService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.RequisitionData.CatOfProc;
import com.methaltech.application.data.entity.bgtool.RequisitionData.IsMultiYearContract;
import com.methaltech.application.data.entity.bgtool.RequisitionData.ProcMethods;
import com.methaltech.application.data.entity.bgtool.RequisitionData.ProcType;
import com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionType;
import com.methaltech.application.data.entity.bgtool.RequisitionItem;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.security.AuthenticatedUser;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class PPDARequisitionDialog extends Dialog {

    private final AccountSummary accountSummary;
    private final Urc_Activities selectedActivity;
    private final Budget selectedBudget;
    private final BudgetItemsService budgetItemsService;
    private final NumberFormat currencyFormat;

    private Binder<RequisitionData> binder;
    private RequisitionData currentRequisition;

    // Form fields
    private ComboBox<RequisitionType> requisitionTypeCombo;
    private ComboBox<ProcType> procTypeCombo;
    private ComboBox<CatOfProc> catOfProcCombo;
    private ComboBox<Organisation> fundSourceCombo;
    private TextField requisitionNumberField;
    private NumberField requestedAmountField;
    private TextArea subjectProc;
    private TextField justificationField;
    private TextField procPlanRef;
    private TextField locationOfDeliveryField;
    private DatePicker requiredDatePicker;
    private Checkbox isItAMuiltYearContract;

    private TextField projectCodeField;
    private TextField projectTitleField;

    // Dynamic items list
    private VerticalLayout itemsContainer;
    //private List<RequisitionItem> requisitionItems;
    private Button addItemButton;
    private Button removeItemButton;
    private Grid<RequisitionItem> itemsGrid;

    // Items list
    private List<RequisitionItem> requisitionItems = new ArrayList<>();
    private Span totalAmountDisplay;
    FormLayout tableLayout = new FormLayout();

    // Buttons
    private Button submitButton;
    private Button saveButton;
    private Button cancelButton;
    Button addItemBtn = new Button("Add Item", new Icon(VaadinIcon.PLUS));
    private final StockUnitMeasureService sampleStockUnitMeasureService;
    private final RequisitionDataService requisitionDataService;
    private final UserService userService;
    private AuthenticatedUser authenticatedUser;
    private UrcDeptSectionAnlDimbgt selectedSection;
    private DeptSectionMergerService deptSectionMergerService;
    private final OrganisationService fundsourceService;
    private User user;
    private final CoaService coaService;
    RadioButtonGroup<RequisitionData.IsMultiYearContract> isItAMuiltYearContractRadio = new RadioButtonGroup<>();
    H4 multYearLegend;
    H4 catOfProcLegend;
    H4 particularsLegend;
    RequisitionData requisitionData;

    public PPDARequisitionDialog(AccountSummary accountSummary, Urc_Activities selectedActivity,
            Budget selectedBudget, BudgetItemsService budgetItemsService, StockUnitMeasureService sampleStockUnitMeasureService,
            RequisitionDataService requisitionDataService, AuthenticatedUser authenticatedUser, UserService userService,
            UrcDeptSectionAnlDimbgt selectedSection, OrganisationService fundsourceService, CoaService coaService, DeptSectionMergerService deptSectionMergerService) {
        this.accountSummary = accountSummary;
        this.selectedActivity = selectedActivity;
        this.selectedBudget = selectedBudget;
        this.budgetItemsService = budgetItemsService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.sampleStockUnitMeasureService = sampleStockUnitMeasureService;
        this.requisitionDataService = requisitionDataService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.fundsourceService = fundsourceService;
        this.deptSectionMergerService = deptSectionMergerService;
        this.coaService = coaService;
        this.selectedSection = selectedSection;
        this.requisitionItems = new ArrayList<>();
        this.setWidth("1000px");   // 90% of viewport width
        this.setHeight("90vh");  // 70% of viewport height

        setModal(true);
        this.setCloseOnOutsideClick(false);
        setDraggable(true);
        setResizable(true);
        addClassName("requisition-approval-dialog");
        setUser();
        createDialogContent();
        setupValidation();
        setupEventHandlers();
    }

    public void setUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }
    }

    private void createDialogContent() {
        VerticalLayout layout = new VerticalLayout();
        // layout.setSpacing(true);
        layout.setPadding(false);
        //layout.addClassName("approval-dialog-layout");

        // Header
        createHeader(layout);

        // Account information card
        createAccountInfoCard(layout);

        // Form
        createMainForm(layout);

        createActionButtons(layout);

        add(layout);
    }

    private void createHeader(VerticalLayout layout) {
        Div header = new Div();
        header.setWidthFull();
        header.addClassName("approval-dialog-header");

        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setSpacing(false);
        titleSection.setPadding(false);

        H2 title = new H2("PPDA Uganda Requisition Form");
        title.addClassName("dialog-title");

        Span subtitle = new Span("Public Procurement and Disposal of Public Assets");
        subtitle.addClassName("dialog-subtitle");

        titleSection.add(title, subtitle);

        Div iconContainer = new Div();
        iconContainer.addClassName("ppda-dialog-icon-container");

        Icon requisitionIcon = new Icon(VaadinIcon.FILE_TEXT);
        requisitionIcon.addClassName("dialog-icon");
        iconContainer.add(requisitionIcon);

        headerContent.add(titleSection, iconContainer);
        header.add(headerContent);
        layout.add(header);
    }

    private void createAccountInfoCard(VerticalLayout layout) {
        Div accountCard = new Div();
        accountCard.addClassName("purpose-section");
        accountCard.setVisible(false);

        H3 accountTitle = new H3("Account Information");
        accountTitle.addClassName("ppda-account-title");

        HorizontalLayout accountGrid = new HorizontalLayout();
        accountGrid.setWidthFull();
        accountGrid.setSpacing(true);
        accountGrid.addClassName("ppda-account-grid");

        accountGrid.add(
                createAccountInfoItem("Account Code", accountSummary.getAccountCode()),
                createAccountInfoItem("Account Name", accountSummary.getAccountName()),
                createAccountInfoItem("Activity Code", selectedActivity.getActivityCode()),
                createAccountInfoItem("Activity Name", selectedActivity.getName())
        );
        record AccountSummary(String account, Double budget, Double actual, Double committed, Double balance) {

        }
        COA coa = coaService.findByCodeAndBudget(accountSummary.getAccountCode(), selectedBudget);
        double budgetByCoa = budgetItemsService.calculateTotalByBudgetAndCoaAndSection(selectedBudget, coa, selectedActivity.getDeptSection()).doubleValue();
        double budgetByCoaActivity = budgetItemsService.calculateTotalByBudgetAndCoaAndActivityAndSection(selectedBudget, coa, selectedActivity, selectedActivity.getDeptSection()).doubleValue();

        double actualByCoaActivity = requisitionDataService.calculateTotalByBudgetAndCoaAndActivityAndSectionActuals(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getActivityCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();
        double actualByCoa = requisitionDataService.calculateTotalBalanceBudgetAndCoaAndSectionBalance(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();

        double committedByCoaActivity = requisitionDataService.calculateTotalCommittedByCoaAndSectionActivity(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getActivityCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();
        double committedByCoa = requisitionDataService.calculateTotalCommittedByCoaAndSectionBalance(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();

        double balanceCoa = budgetByCoa - committedByCoa - actualByCoa;
        double balanceCoaActivity = budgetByCoaActivity - committedByCoaActivity - actualByCoaActivity;
        // Sample data
        List<AccountSummary> items = List.of(
                new AccountSummary("Acc Code", budgetByCoa, actualByCoa, committedByCoa, balanceCoa),
                new AccountSummary("Acc Code & Activity", budgetByCoaActivity, actualByCoaActivity, committedByCoaActivity, balanceCoaActivity)
        );

        Grid<AccountSummary> grid = new Grid<>(AccountSummary.class, false);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        // First column: Account (bold)
        grid.addComponentColumn(item -> {
            Span span = new Span(item.account());
            span.getStyle().set("font-weight", "bold");
            return span;
        }).setHeader("Account");

        // Other numeric columns
        grid.addColumn(item -> formatNumber(item.budget())).setHeader("Budget");
        grid.addColumn(item -> formatNumber(item.actual())).setHeader("Actual");
        grid.addColumn(item -> formatNumber(item.committed())).setHeader("Committed");
        grid.addColumn(item -> formatNumber(item.balance())).setHeader("Balance");

        // Bold entire first row ("Acc Code")
        grid.setClassNameGenerator(item
                -> "Acc Code".equals(item.account()) ? "first-row-bold" : null
        );

        grid.setItems(items);
        grid.setWidthFull();
        grid.setHeight("150px");
        grid.addClassName("ppda-account-grid");
        // add(grid);

        // CSS injection
        getElement().executeJs("""
            const style = document.createElement('style');
            style.textContent = '.first-row-bold td { font-weight: bold; }';
            document.head.appendChild(style);
        """);
        accountCard.add(accountTitle, accountGrid, grid);
        Button[] showHideBtn = new Button[1];

        showHideBtn[0] = new Button("Show Account Info", e -> {
            accountCard.setVisible(!accountCard.isVisible());
            showHideBtn[0].setText(accountCard.isVisible() ? "Hide Account Info" : "Show Account Info");
        });

        layout.setWidthFull();
        layout.add(showHideBtn[0], accountCard);

    }

    private String formatNumber(Double value) {
        return String.format("%,.0f", value); // e.g. 120,000,000
    }

    private VerticalLayout createAccountInfoItem(String label, String value) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName("ppda-account-info-item");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("ppda-info-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("ppda-info-value");

        item.add(labelSpan, valueSpan);
        return item;
    }

    private void createMainForm(VerticalLayout layout) {
        Div formCard = new Div();
        formCard.addClassName("ppda-form-card");

        /*        H3 formTitle = new H3("Requisition Details");
        formTitle.addClassName("ppda-form-title");*/
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("ppda-form-layout");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 3)
        );

        // === Section 1: Basic Requisition Info ===
        /*        H4 requisitionInfoLegend = new H4("Procurement Reference Number");
        requisitionInfoLegend.addClassName("ppda-form-legend");
        formLayout.add(requisitionInfoLegend);
        formLayout.setColspan(requisitionInfoLegend, 3);*/
        // Requisition type
        requisitionTypeCombo = new ComboBox<>("Requisition Type");
        requisitionTypeCombo.setItems(RequisitionType.values());
        requisitionTypeCombo.setItemLabelGenerator(RequisitionType::getDisplayName);
        requisitionTypeCombo.setRequired(true);
        requisitionTypeCombo.addClassName("ppda-requisition-type");

        // Procurement type
        procTypeCombo = new ComboBox<>("Procurement Type");
        procTypeCombo.setItems(ProcType.values());
        procTypeCombo.setItemLabelGenerator(ProcType::getDisplayName);
        procTypeCombo.addClassName("ppda-proc-type");

        // Category of procurement
        catOfProcCombo = new ComboBox<>("Category of Procurement");
        catOfProcCombo.setItems(CatOfProc.values());
        catOfProcCombo.setItemLabelGenerator(CatOfProc::getDisplayName);
        catOfProcCombo.addClassName("ppda-cat-proc");

        fundSourceCombo = new ComboBox<>("Fund Source");
        if (selectedBudget != null) {
            fundSourceCombo.setItems(fundsourceService.getOrganisationsByBudget(selectedBudget));
        }

        fundSourceCombo.setItemLabelGenerator(Organisation::getName);
        fundSourceCombo.addClassName("ppda-cat-proc");

        // Requisition number (auto-generated)
        requisitionNumberField = new TextField("Requisition Number");
        requisitionNumberField.setReadOnly(true);

        requisitionNumberField.addClassName("ppda-requisition-number");

        // Requested amount
        requestedAmountField = new NumberField("Requested Amount (UGX)");
        requestedAmountField.setRequired(true);
        requestedAmountField.setMin(1);
        requestedAmountField.setMax(accountSummary.getTotalBalance());
        requestedAmountField.addClassName("ppda-requested-amount");
        requestedAmountField.setEnabled(false);

        requisitionTypeCombo.addValueChangeListener(e -> {
            updateFormFields(e.getValue());
            requisitionNumberField.setValue(generateRequisitionNumber());
            if (e.getValue().equals(RequisitionType.CASH_REQUISITION)) {
                particularsLegend.setText("Particulars of Requistion");
            } else {
                particularsLegend.setText("Particulars of Procurement");
            }

        });
        procTypeCombo.addValueChangeListener(e -> {
            requisitionNumberField.setValue(generateRequisitionNumber());

        });
        // Project fields
        projectCodeField = new TextField("Project Code");
        projectCodeField.addClassName("ppda-project-code");

        projectTitleField = new TextField("Project Title");
        projectTitleField.addClassName("ppda-project-title");

        formLayout.add(requisitionTypeCombo, procTypeCombo, requisitionNumberField, requestedAmountField);

        // === Section 2: Basic Requisition Info ===
        catOfProcLegend = new H4("Category Of Procurement");
        catOfProcLegend.addClassName("ppda-form-legend");
        formLayout.add(catOfProcLegend);
        formLayout.setColspan(catOfProcLegend, 3);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 3)
        );

        formLayout.add(catOfProcCombo, projectCodeField, projectTitleField);

        // === Section 3: Basic Requisition Info ===
        multYearLegend = new H4("Is procurement going to result into multiyear contracting?");
        multYearLegend.addClassName("ppda-form-legend");
        formLayout.add(multYearLegend);
        formLayout.setColspan(multYearLegend, 3);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 3)
        );

        isItAMuiltYearContractRadio.setItems(IsMultiYearContract.YEAR_ONE, IsMultiYearContract.YEAR_TWO, IsMultiYearContract.YEAR_THREE, IsMultiYearContract.YEAR_FOUR);
        HorizontalLayout isItAMuiltYearContractGroup = new HorizontalLayout(isItAMuiltYearContractRadio);
        formLayout.add(isItAMuiltYearContractGroup);

        // === Section 3: Basic Requisition Info ===
        particularsLegend = new H4("Particulars of Procurement");
        particularsLegend.addClassName("ppda-form-legend");
        formLayout.add(particularsLegend);
        formLayout.setColspan(particularsLegend, 3);
        // Purpose
        subjectProc = new TextArea("Subject of Procurement");
        subjectProc.setRequired(true);
        subjectProc.setMinLength(10);
        subjectProc.setMaxLength(50);
        subjectProc.setPlaceholder("Describe the Subject of Procurement...");
        subjectProc.addClassName("ppda-purpose");

        justificationField = new TextField("Justification");
        justificationField.setMinLength(10);
        justificationField.setMaxLength(50);
        justificationField.setPlaceholder("Justify the Requisition...");
        justificationField.addClassName("ppda-purpose");

        formLayout.add(subjectProc, justificationField);
        formLayout.setColspan(subjectProc, 3);
        // Delivery fields
        locationOfDeliveryField = new TextField("Location for Delivery");
        locationOfDeliveryField.setPlaceholder("Enter Location for Delivery");
        locationOfDeliveryField.addClassName("ppda-delivery-location");

        requiredDatePicker = new DatePicker("Date Required");
        requiredDatePicker.setMin(LocalDate.now());
        requiredDatePicker.addClassName("ppda-required-date");

        formLayout.add(locationOfDeliveryField, requiredDatePicker);

        // Procurement plan reference
        procPlanRef = new TextField("Procurement Plan Reference");
        procPlanRef.setMaxLength(50);
        procPlanRef.setPlaceholder("Provide the Procurement Plan Reference...");
        procPlanRef.addClassName("ppda-proc-plan-ref");

        formLayout.add(procPlanRef);

        //contractCard.add(contractLayout);
        formCard.add(formLayout);
        layout.add(formCard);

        createTableLayout(formLayout);
    }

    private void createTableLayout(FormLayout formLayout) {

        H4 detailsRelatingToProcLegend = new H4("Details Relating to the Procurement");
        detailsRelatingToProcLegend.addClassName("ppda-form-legend");
        formLayout.add(detailsRelatingToProcLegend);
        formLayout.setColspan(detailsRelatingToProcLegend, 3);

        // Create a sub-layout with responsive columns
        tableLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("900px", 4)
        );

        // Create items grid first (above the form)
        createItemsGrid();
        formLayout.setColspan(itemsGrid, 4);
        formLayout.add(itemsGrid);
        itemsGrid.asSingleSelect().addValueChangeListener(e -> {

        });

        // Item description field (spans 4 columns)
        TextField itemDescriptionField = new TextField("Item Description");
        itemDescriptionField.setPlaceholder("Enter detailed item descriptions...");
        itemDescriptionField.setRequired(true);
        itemDescriptionField.addClassName("item-description-field");
        itemDescriptionField.setMaxLength(50);
        //formLayout.setColspan(itemDescriptionField, 3);

        // Other fields in one line
        NumberField itemQuantityField = new NumberField("Quantity");
        itemQuantityField.setPlaceholder("0.00");
        itemQuantityField.setRequired(true);
        itemQuantityField.setMin(0.01);
        itemQuantityField.addClassName("item-quantity-field");

        ComboBox<String> itemUnitField = new ComboBox<>("Unit of Measure");
        itemUnitField.setPlaceholder("e.g., pieces, kg, liters");
        itemUnitField.setRequired(true);
        itemUnitField.addClassName("item-unit-field");
        itemUnitField.setItems(sampleStockUnitMeasureService.findAllUnitValues());

        NumberField itemUnitCostField = new NumberField("Unit Cost (UGX)");
        itemUnitCostField.setPlaceholder("0.00");
        itemUnitCostField.setRequired(true);
        itemUnitCostField.setMin(0.01);
        itemUnitCostField.addClassName("item-unit-cost-field");

        NumberField itemMarketPriceField = new NumberField("Market Price (UGX)");
        itemMarketPriceField.setPlaceholder("Optional market reference");
        itemMarketPriceField.addClassName("item-market-price-field");
        /*        tableLayout.setResponsiveSteps(
        new FormLayout.ResponsiveStep("600px", 4)
        );*/

        tableLayout.add(itemDescriptionField, itemQuantityField, itemUnitField, itemUnitCostField, itemMarketPriceField);
        tableLayout.setColspan(itemDescriptionField, 4);
        itemsGrid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                itemDescriptionField.setValue(e.getValue().getDescription() != null ? e.getValue().getDescription() : "");

                itemQuantityField.setValue(e.getValue().getQuantity() != null ? e.getValue().getQuantity() : 0);

                itemUnitField.setValue(e.getValue().getUnitOfMeasure() != null ? e.getValue().getUnitOfMeasure() : "");

                itemUnitCostField.setValue(e.getValue().getEstimatedUnitCost() != null ? e.getValue().getEstimatedUnitCost() : 0.0);

                itemMarketPriceField.setValue(e.getValue().getMarketPrice() != null ? e.getValue().getMarketPrice() : 0.0);

            } else {
                itemDescriptionField.clear();
                itemQuantityField.setValue(null);
                itemUnitField.clear();
                itemUnitCostField.setValue(null);
                itemMarketPriceField.setValue(null);

            }
        });
        // Add item button

        addItemBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addItemBtn.addClassName("add-item-button");
        addItemBtn.addClickListener(e -> {
            addItemToTable(itemDescriptionField, itemQuantityField, itemUnitField, itemUnitCostField, itemMarketPriceField);
        });

        tableLayout.setColspan(addItemBtn, 1);
        tableLayout.add(addItemBtn);
        tableLayout.setVisible(false);
        Button[] showHideBtn = new Button[1];

        showHideBtn[0] = new Button("Show Add Budget Line Panel", e -> {
            tableLayout.setVisible(!tableLayout.isVisible());
            showHideBtn[0].setText(tableLayout.isVisible() ? "Hide  Add Budget Line Panel" : "Show Add Budget Line Panel");
        });
        showHideBtn[0].setWidth("300px");
        showHideBtn[0].getStyle().set("white-space", "normal");
        showHideBtn[0].getStyle().set("text-align", "center");

        formLayout.add(showHideBtn[0], tableLayout);
        formLayout.setColspan(tableLayout, 3);
    }

    private void createItemsGrid() {
        itemsGrid = new Grid<>(RequisitionItem.class, false);
        itemsGrid.addClassName("elegant-items-grid");
        itemsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        itemsGrid.setWidthFull();
        itemsGrid.setHeight("250px");

        // Item number column
        itemsGrid.addColumn(RequisitionItem::getItemNumber)
                .setHeader("Item")
                .setWidth("80px")
                .setFlexGrow(0);

        // Description column
        itemsGrid.addColumn(RequisitionItem::getDescription)
                .setHeader("Description")
                .setWidth("300px")
                .setFlexGrow(1);

        // Quantity column
        itemsGrid.addColumn(item -> String.format("%.2f", item.getQuantity()))
                .setHeader("Quantity")
                .setWidth("100px")
                .setFlexGrow(0);

        // Unit of measure column
        itemsGrid.addColumn(RequisitionItem::getUnitOfMeasure)
                .setHeader("UOM")
                .setWidth("80px")
                .setFlexGrow(0);

        // Unit cost column
        itemsGrid.addColumn(item -> formatCurrency(item.getEstimatedUnitCost()))
                .setHeader("Unit Cost")
                .setWidth("120px")
                .setFlexGrow(0);

        // Total cost column
        itemsGrid.addColumn(item -> formatCurrency(item.getTotalCost()))
                .setHeader("Total")
                .setWidth("120px")
                .setFlexGrow(0);

        itemsGrid.addColumn(new ComponentRenderer<>(this::createEditButton))
                .setHeader("Edit")
                .setWidth("80px")
                .setFlexGrow(0);
        // Delete button column
        itemsGrid.addColumn(new ComponentRenderer<>(this::createDeleteButton))
                .setHeader("Del")
                .setWidth("80px")
                .setFlexGrow(0);

        itemsGrid.setItems(requisitionItems);
    }

    private Button createDeleteButton(RequisitionItem item) {
        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.addClassName("delete-item-button");
        deleteButton.addClickListener(e -> removeItemFromTable(item));
        return deleteButton;
    }

    private Button createEditButton(RequisitionItem item) {
        Button deleteButton = new Button(new Icon(VaadinIcon.EDIT));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.addClassName("delete-item-button");
        deleteButton.addClickListener(e -> openEditDialog(item));
        return deleteButton;
    }

    private void addItemToTable(TextField descField, NumberField qtyField, ComboBox<String> unitField,
            NumberField costField, NumberField marketField) {
        // Validate item form
        if (descField.getValue() == null || descField.getValue().trim().isEmpty()) {
            showNotification("Please enter item description", NotificationVariant.LUMO_ERROR);
            return;
        }

        if (qtyField.getValue() == null || qtyField.getValue() <= 0) {
            showNotification("Please enter valid quantity", NotificationVariant.LUMO_ERROR);
            return;
        }

        if (unitField.getValue() == null || unitField.getValue().trim().isEmpty()) {
            showNotification("Please enter unit of measure", NotificationVariant.LUMO_ERROR);
            return;
        }

        if (costField.getValue() == null || costField.getValue() <= 0) {
            showNotification("Please enter valid unit cost", NotificationVariant.LUMO_ERROR);
            return;
        }
        ListDataProvider<RequisitionItem> dataProvider = (ListDataProvider<RequisitionItem>) itemsGrid.getDataProvider();

        // Create new item
        RequisitionItem newItem = new RequisitionItem();
        newItem.setItemNumber(dataProvider.getItems().size() + 1);
        newItem.setDescription(descField.getValue());
        newItem.setQuantity(qtyField.getValue());
        newItem.setUnitOfMeasure(unitField.getValue());
        newItem.setEstimatedUnitCost(costField.getValue());
        newItem.setMarketPrice(marketField.getValue());
        newItem.calculateTotalCost();

        requisitionItems = new ArrayList<>(dataProvider.getItems());
        // Add to list
        requisitionItems.add(newItem);

        // Refresh grid
        itemsGrid.setItems(requisitionItems);

        // Clear form fields
        descField.clear();
        qtyField.clear();
        unitField.clear();
        costField.clear();
        marketField.clear();

        // Update total amount
        updateTotalAmount();

        showNotification("Item added successfully", NotificationVariant.LUMO_SUCCESS);
    }

    private void updateTotalAmount() {
        double total = requisitionItems.stream()
                .mapToDouble(RequisitionItem::getTotalCost)
                .sum();
        requestedAmountField.setValue(total);
    }

    private void updateFormFields(RequisitionType type) {
        boolean showAdditionalFields = type == RequisitionType.FORM_5 || type == RequisitionType.FORM_48;

        procTypeCombo.setVisible(showAdditionalFields);
        catOfProcCombo.setVisible(showAdditionalFields);
        projectCodeField.setVisible(showAdditionalFields);
        projectTitleField.setVisible(showAdditionalFields);
        locationOfDeliveryField.setVisible(showAdditionalFields);
        requiredDatePicker.setVisible(showAdditionalFields);
        procPlanRef.setVisible(showAdditionalFields);

        if (showAdditionalFields) {
            procTypeCombo.setRequired(true);
            catOfProcCombo.setRequired(true);
            locationOfDeliveryField.setRequired(true);
            requiredDatePicker.setRequired(true);

            catOfProcCombo.setVisible(true);
            catOfProcLegend.setVisible(true);
            multYearLegend.setVisible(true);
            isItAMuiltYearContractRadio.setVisible(true);
        } else {
            procTypeCombo.setRequired(false);
            catOfProcCombo.setRequired(false);
            locationOfDeliveryField.setRequired(false);
            requiredDatePicker.setRequired(false);

            catOfProcCombo.setVisible(false);
            multYearLegend.setVisible(false);
            isItAMuiltYearContractRadio.setVisible(false);
            catOfProcLegend.setVisible(false);
        }
    }

    private void createActionButtons(VerticalLayout layout) {
        Div buttonFooter = new Div();
        buttonFooter.setWidthFull();
        buttonFooter.addClassName("ppda-button-footer");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName("ppda-form-buttons");

        cancelButton = new Button("CANCEL");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("ppda-cancel-button");
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("SAVE");
        saveButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        saveButton.addClassName("ppda-save-button");
        saveButton.addClickListener(e -> saveDraftRequisition(false));

        submitButton = new Button("SUBMIT & DOWNLOAD");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("ppda-submit-button");
        submitButton.addClickListener(e -> saveSubmitRequisition(true));

        buttonLayout.add(cancelButton, saveButton, submitButton);
        buttonFooter.add(buttonLayout);
        layout.add(buttonFooter);
    }

    private void setupValidation() {
        binder = new Binder<>(RequisitionData.class);

        // Requisition type validation
        binder.forField(requisitionTypeCombo)
                .asRequired("Requisition type is required")
                .bind(RequisitionData::getRequisitionType, RequisitionData::setRequisitionType);

        // Procurement type validation
        binder.forField(procTypeCombo)
                .bind(RequisitionData::getProcType, RequisitionData::setProcType);
        binder.forField(catOfProcCombo)
                .bind(RequisitionData::getCatOfProc, RequisitionData::setCatOfProc);

        binder.forField(fundSourceCombo)
                .bind(RequisitionData::getOrganisation, RequisitionData::setOrganisation);

        // Requested amount validation
        binder.forField(requestedAmountField)
                .asRequired("Requested amount is required")
                .withValidator(new DoubleRangeValidator("Amount must be greater than 0", 1.0, Double.MAX_VALUE))
                .withValidator(amount -> amount <= accountSummary.getTotalBalance(),
                        "Amount exceeds available balance of " + formatCurrency(accountSummary.getTotalBalance()))
                //  .withValidator(amount -> amount <= 5_000_000,"For Cash Requistions, Amount must not be less than 5,000,000")
                //.withValidator(this::validateReqAmount)
                .bind(RequisitionData::getRequestedAmount, RequisitionData::setRequestedAmount);

        // Purpose validation
        binder.forField(subjectProc)
                .asRequired("Purpose is required")
                .withValidator(new StringLengthValidator("Purpose must be between 10 and 500 characters", 10, 500))
                .bind(RequisitionData::getSubjectOfProcurement, RequisitionData::setSubjectOfProcurement);

        // Other field bindings
        binder.forField(procPlanRef)
                .bind(RequisitionData::getProcurementPlanRef, RequisitionData::setProcurementPlanRef);

        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);

        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);
        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);
        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);
        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);
        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);
        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);
        binder.forField(locationOfDeliveryField)
                .bind(RequisitionData::getDeliveryLocation, RequisitionData::setDeliveryLocation);

    }

    public void setupEventHandlers() {
        // Real-time validation feedback
        binder.addStatusChangeListener(e -> {
            boolean isValid = binder.isValid();
            saveButton.setEnabled(isValid);
            submitButton.setEnabled(isValid);
            //addItemBtn.setEnabled(isValid);
        });

        if (this.getRequisitionData() != null) {

            if (this.getRequisitionData().canBeSubmitted()) {
                submitButton.setEnabled(true);
            } else {
                submitButton.setEnabled(false);
            }

            if (this.getRequisitionData().getStatus() == RequisitionData.RequisitionStatus.SUBMITTED || this.getRequisitionData().getStatus() == RequisitionData.RequisitionStatus.COMPLETED
                    || this.getRequisitionData().getStatus() == RequisitionData.RequisitionStatus.REJECTED || this.getRequisitionData().getStatus() == RequisitionData.RequisitionStatus.UNDER_REVIEW
                    || this.getRequisitionData().getStatus() == RequisitionData.RequisitionStatus.APPROVED) {
                saveButton.setEnabled(false);
            } else {
                saveButton.setEnabled(true);
            }
        } else {
            System.out.println("No Requisition found");
        }
    }

    private ValidationResult validateReqAmount(Double amount, ValueContext ctx) {
        if (amount == null) {
            return ValidationResult.error("For Cash Requistions, Amount must not be less than 5,000,000");
        }
        if (requisitionTypeCombo.getValue() == RequisitionType.CASH_REQUISITION && amount <= 5_000_000) {
            return ValidationResult.error("For Cash Requistions, Amount must not be less than 5,000,000");
        }
        return ValidationResult.ok();
    }

    private void saveSubmitRequisition(boolean submitAndDownload) {
        generatePDFForm(currentRequisition);
        /*        if (this.getRequisitionData() != null) {
        if (this.getRequisitionData().canBeSubmitted()) {
        currentRequisition = this.getRequisitionData();
        currentRequisition.setHodStatus(RequisitionData.RequisitionStatus.SUBMITTED);
        currentRequisition.setStatus(RequisitionData.RequisitionStatus.SUBMITTED);
        currentRequisition.setLastUpdatedBy(user);
        currentRequisition = requisitionDataService.saveOrUpdateRequisition(currentRequisition);
        }
        
        generatePDFForm(currentRequisition);
        }*/

    }

    private void saveDraftRequisition(boolean status) {
        Optional<RequisitionData> getRequisitionByNumber = requisitionDataService.getRequisitionByNumber(requisitionNumberField.getValue());
        COA coa = coaService.findByCodeAndBudget(accountSummary.getAccountCode(), selectedBudget);
        BigDecimal balanceByCOA = BigDecimal.ZERO;
        BigDecimal balanceByActivity = BigDecimal.ZERO;
        BigDecimal actualBalanceByCOA = BigDecimal.ZERO;
        BigDecimal actualBalanceByActivity = BigDecimal.ZERO;
        Long requisitionId = 0L;

        if (getRequisitionByNumber.isPresent()) {
            currentRequisition = getRequisitionByNumber.get();
            //balanceByCOA = balanceByCOA.subtract(new BigDecimal(currentRequisition.getAvailableBalanceByCOA()));
            //balanceByActivity = balanceByActivity.subtract(new BigDecimal(currentRequisition.getAvailableBalanceByActivity()));
            requisitionId = currentRequisition.getId();

        } else {
            currentRequisition = new RequisitionData();
            currentRequisition.setCreatedBy(user);
            currentRequisition.setCreatedDate(LocalDateTime.now());

            // Generate requisition number only for brand-new requisitions
            currentRequisition.setRequisitionNumber(requisitionNumberField.getValue());
        }
        //  balanceByCOA = budgetItemsService.calculateTotalBalanceBudgetAndCoaAndSectionBalance(selectedBudget, coa, selectedSection, requisitionId);
        // balanceByActivity = budgetItemsService.calculateTotalBalanceBudgetAndCoaAndActivityAndSectionBalance(selectedBudget, coa, selectedActivity, selectedSection, requisitionId);

        //  actualBalanceByCOA = budgetItemsService.calculateTotalBalanceBudgetAndCoaAndSectionBalance(selectedBudget, coa, selectedSection, requisitionId);
        // actualBalanceByActivity = budgetItemsService.calculateTotalBalanceBudgetAndCoaAndActivityAndSectionBalance(selectedBudget, coa, selectedActivity, selectedSection, requisitionId);
        double budgetByCoa = budgetItemsService.calculateTotalByBudgetAndCoaAndSection(selectedBudget, coa, selectedActivity.getDeptSection()).doubleValue();
        double budgetByCoaActivity = budgetItemsService.calculateTotalByBudgetAndCoaAndActivityAndSection(selectedBudget, coa, selectedActivity, selectedActivity.getDeptSection()).doubleValue();

        double actualByCoaActivity = requisitionDataService.calculateTotalByBudgetAndCoaAndActivityAndSectionActuals(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getActivityCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();
        double actualByCoa = requisitionDataService.calculateTotalBalanceBudgetAndCoaAndSectionBalance(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();

        double committedByCoaActivity = requisitionDataService.calculateTotalCommittedByCoaAndSectionActivity(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getActivityCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();
        double committedByCoa = requisitionDataService.calculateTotalCommittedByCoaAndSectionBalance(selectedBudget, accountSummary.getAccountCode(), selectedActivity.getDeptSection().getANL_CODE()).doubleValue();

        double balanceCoa = budgetByCoa - committedByCoa - actualByCoa;
        double balanceCoaActivity = budgetByCoaActivity - committedByCoaActivity - actualByCoaActivity;

        // Always update mutable fields (whether draft or submit)
        currentRequisition.setPdeCode("URC");
        currentRequisition.setBudget(selectedBudget);
        currentRequisition.setActivity(selectedActivity);
        currentRequisition.setProcType(procTypeCombo.getValue());
        currentRequisition.setRequisitionType(requisitionTypeCombo.getValue());
        if (!currentRequisition.getRequisitionType().equals(RequisitionType.CASH_REQUISITION)) {
            currentRequisition.setDateRequired(requiredDatePicker.getValue().atStartOfDay());
            currentRequisition.setIsMultiYearContract(isItAMuiltYearContractRadio.getValue());
            currentRequisition.setProjectCode(projectCodeField.getValue());
            currentRequisition.setProjectTitle(projectTitleField.getValue());
        } else {
            currentRequisition.setJustification(justificationField.getValue());

        }

        currentRequisition.setCurrency(currencyFormat.getCurrency().getCurrencyCode());
        currentRequisition.setDeptSection(selectedSection);
        //currentRequisition.setAvailableBalanceByActivity(requestedAmountField.getValue());
        //currentRequisition.setAvailableBalanceByCOA(requestedAmountField.getValue());
        currentRequisition.setProcMethods(setProcMethods(requestedAmountField.getValue()));
        currentRequisition.setCatOfProc(catOfProcCombo.getValue());
        currentRequisition.setOrganisation(fundSourceCombo.getValue());
        currentRequisition.setCoa(coaService.findByCodeAndBudget(accountSummary.getAccountCode(), selectedBudget));
        currentRequisition.setSubjectOfProcurement(subjectProc.getValue());
        currentRequisition.setRequestedAmount(requestedAmountField.getValue());
        currentRequisition.setTotalActualByCode(actualByCoa);
        currentRequisition.setTotalActualByCodeActivity(actualByCoaActivity);
        currentRequisition.setTotalCommittedByCode(committedByCoa);
        currentRequisition.setTotalCommittedByCodeActivity(committedByCoaActivity);

        currentRequisition.setAvailableBalanceByCOA(balanceCoa);
        currentRequisition.setAvailableBalanceByActivity(balanceCoaActivity);

        // Replace requisition items
        ListDataProvider<RequisitionItem> dataProvider = (ListDataProvider<RequisitionItem>) itemsGrid.getDataProvider();
        List<RequisitionItem> requisitionItems2 = new ArrayList<>(dataProvider.getItems());
        currentRequisition.getItems().clear();
        for (RequisitionItem item : requisitionItems2) {
            currentRequisition.addItem(item);  // ensures item.setRequisitionData(currentRequisition)
        }
        currentRequisition.setTotalAmount(requestedAmountField.getValue());
        if (status == false) {
            currentRequisition.setHodStatus(RequisitionData.RequisitionStatus.DRAFT);
        } else {
            currentRequisition.setHodStatus(RequisitionData.RequisitionStatus.SUBMITTED);
        }

        try {

            // Save or update depending on ID
            currentRequisition = requisitionDataService.saveOrUpdateRequisition(currentRequisition);
            showNotification("Draft saved successfully", NotificationVariant.LUMO_PRIMARY);
            // Show different messages for draft vs submit
            /*            if (submitAndDownload) {
            showNotification("Requisition submitted successfully", NotificationVariant.LUMO_SUCCESS);
            // TODO: trigger your PDF download logic here
            } else {
            showNotification("Draft saved successfully", NotificationVariant.LUMO_PRIMARY);
            }*/
            if (status == true) {
                generatePDFForm(currentRequisition);
            }
            currentRequisition = null;
            this.close();

        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Error saving requisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    public RequisitionData.ProcMethods setProcurementMethodByAmount(Double requestedAmount) {
        if (requestedAmount == null) {
            return RequisitionData.ProcMethods.NOT_DETERMINED;
        }

        if (requestedAmount < 10_000_000) {
            return RequisitionData.ProcMethods.MICRO_PROCUREMENT;
        } else if (requestedAmount >= 10_000_000 && requestedAmount < 200_000_000) {
            return RequisitionData.ProcMethods.RFQ;
        } else if (requestedAmount >= 200_000_000 && requestedAmount <= 500_000_000) {
            return RequisitionData.ProcMethods.RESTRICTED_BIDDING;
        } else {
            return RequisitionData.ProcMethods.OPEN_BIDDING;
        }
    }

    private ProcMethods setProcMethods(double requestedAmount) {
        return setProcurementMethodByAmount(requestedAmount);
    }

    public void generatePDFForm(RequisitionData requisition) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);

            // Create fonts
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

            switch (requisition.getRequisitionType()) {
                case CASH_REQUISITION:
                    generateCashRequisitionForm(document, requisition, boldFont, itallicFont, normalFont);
                    break;
                case FORM_5:
                    generateForm5(document, requisition, boldFont, itallicFont, normalFont);
                    break;
                case FORM_48:
                    generateForm48(document, requisition, boldFont, itallicFont, normalFont);
                    break;
            }

            document.close();

            // Create download
            String fileName = requisition.getRequisitionType().name() + "_"
                    + requisition.getRequisitionNumber().replace("/", "-") + ".pdf";

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
                ui.getPage().open(url, "_blank");
                showNotification("Report generated successfully", NotificationVariant.LUMO_SUCCESS);
            }

        } catch (Exception e) {
            showNotification("Error generating PDF: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void generateCashRequisitionForm(Document document, RequisitionData requisition,
            PdfFont boldFont, PdfFont itallicFont, PdfFont normalFont) throws Exception {

        // Official PPDA header
        document.add(new Paragraph("UGANDA RAILWAYS CORPORATION")
                .setFont(boldFont).setFontSize(13).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("CASH REQUISITION FORM")
                .setFont(normalFont).setFontSize(13).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("REQUEST FOR APPROVAL OF REQUISITION")
                .setFont(boldFont).setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        // --- PART I: REQUEST BY USER DEPARTMENT ---
        document.add(new Paragraph("PART I: REQUEST BY USER DEPARTMENT FOR APPROVAL OF REQUISITION")
                .setFont(boldFont).setMarginTop(0)).setTextAlignment(TextAlignment.CENTER);

        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- First row: Procurement Reference Number spans all 4 columns ---
        Cell refCell = new Cell(1, 4) // row span 1, column span 4
                .add(new Paragraph("Requisition Reference Number: "
                        + safe(requisition.getRequisitionNumber()))
                        .setFont(boldFont)).setFontSize(10);
        detailsTable.addCell(refCell);

// --- Second row: headers ---
        detailsTable.addCell(new Cell().add(new Paragraph("Code of Procuring and Disposing Entity").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Supplies/Works/Non-Consultancy Services").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Financial Year").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Sequence Number").setFont(normalFont)));

// --- Third row: values ---
        detailsTable.addCell(safe(requisition.getPdeCode())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getProcType())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getBudget().getFinancialYear())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getRequisitionNumber())).setFontSize(10);

        document.add(detailsTable).setFont(normalFont).setFontSize(10);

        // --- PARTICULARS OF PROCUREMENT ---
        Table particulars = new Table(UnitValue.createPercentArray(new float[]{3, 5}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(25);

// --- First row: Section title spans 2 columns ---
        Cell partHeader = new Cell(1, 2)
                .add(new Paragraph("Particulars of Requisition").setFont(boldFont));
        particulars.addCell(partHeader);

// --- Subject of Procurement ---
        particulars.addCell(new Paragraph("Subject of Requisition").setFont(normalFont));
        particulars.addCell(new Paragraph(safe(requisition.getSubjectOfProcurement())));

        particulars.addCell(new Paragraph("Justification:").setFont(boldFont));
        particulars.addCell(new Paragraph(safe(requisition.getJustification())));

        document.add(particulars).setBottomMargin(25);

        // --- DETAILS TABLE ---
        Table details = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

// --- First row: Section title spans all 6 columns ---
        Cell header = new Cell(1, 6)
                .add(new Paragraph("Details Relating to the Requisition").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER);
        details.addCell(header);

// --- Second row: Column headers ---
        Paragraph descHeader = new Paragraph()
                .add(new Text("Description ").setFont(normalFont));  // <- italic font
        details.addCell(new Paragraph("Item No.").setFont(normalFont));
        details.addCell(descHeader);
        details.addCell(new Paragraph("Quantity").setFont(normalFont));
        details.addCell(new Paragraph("Unit of Measure").setFont(normalFont));
        details.addCell(new Paragraph("Estimated Unit Cost").setFont(normalFont));
        details.addCell(new Paragraph("Market Price of the Procurement").setFont(normalFont));

// --- Next 4 rows: placeholders for items ---
        int maxItems = 4;
        if (requisition.getItems().size() > 4) {
            maxItems = requisition.getItems().size();
            for (int i = 1; i <= maxItems; i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));       // Market Price

            }
        } else {
            for (int i = 1; i <= requisition.getItems().size(); i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));       // Market Price
            }
            int size = 5 - requisition.getItems().size();

        }

// --- Last row: Currency + Estimated Total Cost ---
        Cell currencyCell = new Cell(1, 5)
                .add(new Paragraph("Currency: " + safe(requisition.getCurrency())).setFont(boldFont));
        details.addCell(currencyCell);

        details.addCell(new Paragraph("Estimated Total Cost: "
                + formatCurrency(requisition.getRequestedAmount())).setFont(boldFont));

        document.add(details);

        // --- SIGNATURES SECTION ---
        Table signatures = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10);

// Remove all borders
        signatures.setBorder(Border.NO_BORDER);

// --- First row: headers ---
        Paragraph reqHeader = new Paragraph()
                .add(new Text("(1) Request for Requisition ").setFont(boldFont))
                .add(new Text("(Member of user department)").setFont(itallicFont));

        Paragraph confHeader = new Paragraph()
                .add(new Text("(2) Confirmation of Request ").setFont(boldFont))
                .add(new Text("(Head of user department)").setFont(itallicFont));

        signatures.addCell(new Cell(1, 2).add(reqHeader).setBorder(Border.NO_BORDER));
        signatures.addCell(new Cell(1, 2).add(confHeader).setBorder(Border.NO_BORDER));

// --- Helper for dashed line ---
// --- Signature Row ---
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Name Row ---
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Title Row ---
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Date Row ---
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

        document.add(signatures);
        // --- FUNDS AVAILABILITY ---
        document.add(new Paragraph("Availability of funds to be confirmed prior to approval by Accounting Officer:")
                .setFont(itallicFont).setMarginTop(10));

        Table funds = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        funds.addHeaderCell("Department/Section")
                .addHeaderCell("Activity")
                .addHeaderCell("Chart Of Account")
                .addHeaderCell("Balance Remaining By Account Code")
                .addHeaderCell("Balance Remaining By Activity").setFont(boldFont);

        // Placeholder row
        String sectioncode = "(" + requisition.getDeptSection().getANL_CODE() + ")";
        UrcDepartmentAnlDim findDepartmentDetail = deptSectionMergerService.getDepartmentBySectionCode(requisition.getDeptSection().getANL_CODE().trim());
        String dept = findDepartmentDetail.getNAME() + " (" + findDepartmentDetail.getANL_CODE() + ") / ";
        String activities = requisition.getActivity().getName() + " (" + requisition.getActivity().getActivityCode() + ")";
        String coa = requisition.getCoa().getName() + " (" + requisition.getCoa().getCode().trim() + ")";
        funds.addCell(dept + requisition.getDeptSection().getNAME() + " " + sectioncode).setFont(normalFont)
                .addCell(activities).setFont(normalFont)
                .addCell(coa).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByCOA().doubleValue())).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByActivity().doubleValue())).setFont(normalFont);

        document.add(funds);
        Table fundingApproval = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(20);

// Remove all borders
        fundingApproval.setBorder(Border.NO_BORDER);

// --- Header row ---
        Paragraph headercon = new Paragraph()
                .add(new Text("(3) Confirmation of Funding and Approval to Procure ").setFont(boldFont))
                .add(new Text("(Accounting Officer)").setFont(normalFont));

        fundingApproval.addCell(new Cell(1, 2).add(headercon).setBorder(Border.NO_BORDER));

// --- Helper line ---
// --- Signature Row ---
        fundingApproval.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Name Row ---
        fundingApproval.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Title Row ---
        fundingApproval.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Date Row ---
        fundingApproval.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

        document.add(fundingApproval);

    }

    private String catOfProc(CatOfProc catp, RequisitionData requisition) {
        String category = "";
        if (requisition != null) {

            if (catp == requisition.getCatOfProc()) {
                category = "Yes";
            } else {
                category = "No";
            }
        }

        return category;
    }

    private String multiYear(IsMultiYearContract catp, RequisitionData requisition) {
        String category = "";
        if (requisition != null) {

            if (catp == requisition.getIsMultiYearContract()) {
                category = "Yes";
            } else {
                category = "No";
            }
        }

        return category;
    }

    private void generateForm5(Document document, RequisitionData requisition,
            PdfFont boldFont, PdfFont itallicFont, PdfFont normalFont) throws Exception {

        // Official PPDA header
        document.add(new Paragraph("SCHEDULES")
                .setFont(boldFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("SCHEDULES 1")
                .setFont(normalFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Forms")
                .setFont(normalFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("FORM 5")
                .setFont(boldFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Regulation 3(1), 13(3), 15(3), 17(3) 24(2), 53(6), 54(5)")
                .setFont(itallicFont).setFontSize(11).setTextAlignment(TextAlignment.RIGHT));

        document.add(new Paragraph("THE PUBLIC PROCUREMENT AND DISPOSAL OF PUBLIC ASSETS ACT, 2003")
                .setFont(normalFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("REQUEST FOR APPROVAL OF PROCUREMENT")
                .setFont(boldFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        // --- PART I: REQUEST BY USER DEPARTMENT ---
        document.add(new Paragraph("PART I: REQUEST BY USER DEPARTMENT FOR APPROVAL OF PROCUREMENT")
                .setFont(boldFont).setMarginTop(0)).setTextAlignment(TextAlignment.CENTER);

        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- First row: Procurement Reference Number spans all 4 columns ---
        Cell refCell = new Cell(1, 4) // row span 1, column span 4
                .add(new Paragraph("Procurement Reference Number: "
                        + safe(requisition.getRequisitionNumber()))
                        .setFont(boldFont)).setFontSize(10);
        detailsTable.addCell(refCell);

// --- Second row: headers ---
        detailsTable.addCell(new Cell().add(new Paragraph("Code of Procuring and Disposing Entity").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Supplies/Works/Non-Consultancy Services").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Financial Year").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Sequence Number").setFont(normalFont)));

// --- Third row: values ---
        detailsTable.addCell(safe(requisition.getPdeCode())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getProcType())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getBudget().getFinancialYear())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getRequisitionNumber())).setFontSize(10);

        document.add(detailsTable).setFont(normalFont).setFontSize(10);

        document.add(new Paragraph("Category of procurement and budget")
                .setFont(normalFont).setFontSize(10).setTextAlignment(TextAlignment.LEFT).setMarginBottom(0));

        Table procCatTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- Second row: headers ---
        procCatTable.addCell(new Cell().add(new Paragraph("Recurrent Budget").setFont(normalFont)));
        procCatTable.addCell(new Cell().add(new Paragraph("Development Budget").setFont(normalFont)));
        procCatTable.addCell(new Cell().add(new Paragraph("Project Code").setFont(normalFont)));
        procCatTable.addCell(new Cell().add(new Paragraph("Project Title").setFont(normalFont)));

// --- Third row: values ---
        procCatTable.addCell(safe(catOfProc(CatOfProc.RECURRENT_BUDGET, requisition)));
        procCatTable.addCell(safe(catOfProc(CatOfProc.DEVELOPMENT_BUDGET, requisition)));
        procCatTable.addCell(safe(requisition.getProjectCode()));
        procCatTable.addCell(safe(requisition.getProjectTitle()));

        document.add(procCatTable).setFontSize(10);

        // --- Multi-year contracting info ---
        Table multiYearTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- Second row: headers ---
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year One").setFont(normalFont)));
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year Two").setFont(normalFont)));
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year Three").setFont(normalFont)));
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year Four").setFont(normalFont)));

// --- Third row: values ---
        multiYearTable.addCell(safe(multiYear(IsMultiYearContract.YEAR_ONE, requisition)));
        multiYearTable.addCell(safe(multiYear(IsMultiYearContract.YEAR_TWO, requisition)));
        multiYearTable.addCell(safe(multiYear(IsMultiYearContract.YEAR_THREE, requisition)));
        multiYearTable.addCell(safe(multiYear(IsMultiYearContract.YEAR_FOUR, requisition)));

        document.add(new Paragraph("Is procurement going to result into multiyear contracting?").setTextAlignment(TextAlignment.LEFT)
                .setFont(normalFont)).setFontSize(10).setTextAlignment(TextAlignment.LEFT);
        document.add(multiYearTable).setBottomMargin(25);
        document.add(new Paragraph(""));

        // --- PARTICULARS OF PROCUREMENT ---
        Table particulars = new Table(UnitValue.createPercentArray(new float[]{3, 5}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(25);

// --- First row: Section title spans 2 columns ---
        Cell partHeader = new Cell(1, 2)
                .add(new Paragraph("Particulars of Procurement").setFont(boldFont));
        particulars.addCell(partHeader);

// --- Subject of Procurement ---
        particulars.addCell(new Paragraph("Subject of Procurement").setFont(normalFont));
        particulars.addCell(new Paragraph(safe(requisition.getSubjectOfProcurement())));

// --- Procurement Plan Reference ---
        particulars.addCell(new Paragraph("Procurement Plan Reference").setFont(normalFont));
        particulars.addCell(new Paragraph(safe(requisition.getProcurementPlanRef())));

// --- Location for Delivery ---
        particulars.addCell(new Paragraph("Location for Delivery").setFont(normalFont));
        particulars.addCell(new Paragraph(safe(requisition.getDeliveryLocation())));

// --- Date Required ---
        particulars.addCell(new Paragraph("Date Required").setFont(normalFont));
        particulars.addCell(new Paragraph(safe(requisition.getDateRequired())));

        document.add(particulars).setBottomMargin(25);

        // --- DETAILS TABLE ---
        Table details = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

// --- First row: Section title spans all 6 columns ---
        Cell header = new Cell(1, 6)
                .add(new Paragraph("Details Relating to the Procurement").setFont(boldFont)).setTextAlignment(TextAlignment.CENTER);
        details.addCell(header);

// --- Second row: Column headers ---
        Paragraph descHeader = new Paragraph()
                .add(new Text("Description ").setFont(normalFont))
                .add(new Text("(Attach specifications, terms of reference or scope of works)")
                        .setFont(itallicFont));  // <- italic font
        details.addCell(new Paragraph("Item No.").setFont(normalFont));
        details.addCell(descHeader);
        details.addCell(new Paragraph("Quantity").setFont(normalFont));
        details.addCell(new Paragraph("Unit of Measure").setFont(normalFont));
        details.addCell(new Paragraph("Estimated Unit Cost").setFont(normalFont));
        details.addCell(new Paragraph("Market Price of the Procurement").setFont(normalFont));

// --- Next 4 rows: placeholders for items ---
        int maxItems = 4;
        if (requisition.getItems().size() > 4) {
            maxItems = requisition.getItems().size();
            for (int i = 1; i <= maxItems; i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));       // Market Price

            }
        } else {
            for (int i = 1; i <= requisition.getItems().size(); i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));       // Market Price
            }
            int size = 5 - requisition.getItems().size();

        }

// --- Last row: Currency + Estimated Total Cost ---
        Cell currencyCell = new Cell(1, 5)
                .add(new Paragraph("Currency: " + safe(requisition.getCurrency())).setFont(boldFont));
        details.addCell(currencyCell);

        details.addCell(new Paragraph("Estimated Total Cost: "
                + formatCurrency(requisition.getRequestedAmount())).setFont(boldFont));

        document.add(details);

        // --- SIGNATURES SECTION ---
        Table signatures = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10);

// Remove all borders
        signatures.setBorder(Border.NO_BORDER);

// --- First row: headers ---
        Paragraph reqHeader = new Paragraph()
                .add(new Text("(1) Request for Procurement ").setFont(boldFont))
                .add(new Text("(Member of user department)").setFont(itallicFont));

        Paragraph confHeader = new Paragraph()
                .add(new Text("(2) Confirmation of Request ").setFont(boldFont))
                .add(new Text("(Head of user department)").setFont(itallicFont));

        signatures.addCell(new Cell(1, 2).add(reqHeader).setBorder(Border.NO_BORDER));
        signatures.addCell(new Cell(1, 2).add(confHeader).setBorder(Border.NO_BORDER));

// --- Helper for dashed line ---
// --- Signature Row ---
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Name Row ---
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Title Row ---
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Date Row ---
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

        document.add(signatures);
        // --- FUNDS AVAILABILITY ---
        document.add(new Paragraph("Availability of funds to be confirmed prior to approval by Accounting Officer:")
                .setFont(itallicFont).setMarginTop(10));

        // Placeholder row
        String sectioncode = "(" + requisition.getDeptSection().getANL_CODE() + ")";
        UrcDepartmentAnlDim findDepartmentDetail = deptSectionMergerService.getDepartmentBySectionCode(requisition.getDeptSection().getANL_CODE().trim());
        String dept = findDepartmentDetail.getNAME() + " (" + findDepartmentDetail.getANL_CODE() + ") / ";
        String activities = requisition.getActivity().getName() + " (" + requisition.getActivity().getActivityCode() + ")";
        String coa = requisition.getCoa().getName() + " (" + requisition.getCoa().getCode().trim() + ")";
        Table funds = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        funds.addHeaderCell("Vote/Head No")
                .addHeaderCell("Programme")
                .addHeaderCell("Sub-programme")
                .addHeaderCell("Item")
                .addHeaderCell("Balance Remaining").setFont(boldFont);

        // Placeholder row
        funds.addCell("1").addCell("").addCell("").addCell("").addCell("");

        document.add(funds);
        document.add(new Paragraph(""));

        Table funds2 = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        funds2.addHeaderCell(new Paragraph("Department/Section").setFont(boldFont))
                .addHeaderCell(new Paragraph("Activity").setFont(boldFont))
                .addHeaderCell(new Paragraph("Chart Of Account").setFont(boldFont))
                .addHeaderCell(new Paragraph("Balance Remaining By Account Code").setFont(boldFont))
                .addHeaderCell(new Paragraph("Balance Remaining By Activity").setFont(boldFont));

        funds2.addCell(dept + requisition.getDeptSection().getNAME() + " " + sectioncode).setFont(normalFont)
                .addCell(activities).setFont(normalFont).setFont(normalFont)
                .addCell(coa).setFont(normalFont).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByCOA().doubleValue())).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByActivity().doubleValue())).setFont(normalFont);

        document.add(funds2);
        Table fundingApproval = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(20);

// Remove all borders
        fundingApproval.setBorder(Border.NO_BORDER);

// --- Header row ---
        Paragraph headercon = new Paragraph()
                .add(new Text("(3) Confirmation of Funding and Approval to Procure ").setFont(boldFont))
                .add(new Text("(Accounting Officer)").setFont(normalFont));

        fundingApproval.addCell(new Cell(1, 2).add(headercon).setBorder(Border.NO_BORDER));

// --- Helper line ---
// --- Signature Row ---
        fundingApproval.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Name Row ---
        fundingApproval.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Title Row ---
        fundingApproval.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Date Row ---
        fundingApproval.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

        document.add(fundingApproval);

    }

    private void generateForm48(Document document, RequisitionData requisition,
            PdfFont titleFont, PdfFont headerFont, PdfFont normalFont) throws Exception {
        // Official PPDA header
        document.add(new Paragraph("REPUBLIC OF UGANDA")
                .setFont(titleFont).setFontSize(16).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("PUBLIC PROCUREMENT AND DISPOSAL OF PUBLIC ASSETS AUTHORITY")
                .setFont(headerFont).setFontSize(12).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("FORM 48")
                .setFont(titleFont).setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        document.add(new Paragraph("LOCAL PURCHASE ORDER")
                .setFont(headerFont).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        // LPO details
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(0);

        detailsTable.addCell("LPO Number:").addCell(requisition.getRequisitionNumber());
        detailsTable.addCell("Date:").addCell(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        detailsTable.addCell("Account Code:").addCell(requisition.getActivity().getActivityCode());
        detailsTable.addCell("Activity:").addCell(requisition.getActivity().getActivityCode() + " - " + requisition.getActivity().getName());

        document.add(detailsTable);

        // Order items
        document.add(new Paragraph("ORDER DETAILS").setFont(headerFont).setMarginTop(20));

        Table orderTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(20);

        orderTable.addHeaderCell("Item No.").addHeaderCell("Description").addHeaderCell("Quantity")
                .addHeaderCell("Unit Price").addHeaderCell("Total Amount");

        orderTable.addCell("1").addCell(requisition.getSubjectOfProcurement())
                .addCell("1").addCell(formatCurrency(requisition.getRequestedAmount()))
                .addCell(formatCurrency(requisition.getRequestedAmount()));

        document.add(orderTable);

        // Delivery terms
        // LPO signature section
        document.add(new Paragraph("AUTHORIZATION").setFont(headerFont).setMarginTop(30));

        Table lpoSignatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginTop(20);

        lpoSignatureTable.addCell("Issued By:\n\n\n_________________\nName & Signature\nDate: ___________");
        lpoSignatureTable.addCell("Supplier Acceptance:\n\n\n_________________\nName & Signature\nDate: ___________");

        document.add(lpoSignatureTable);

        // Footer
        document.add(new Paragraph("This Local Purchase Order is issued in accordance with the PPDA Act 2003 and Regulations 2014.")
                .setFont(normalFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(30));
    }

    private void addSignatureSection(Document document, PdfFont headerFont) {
        try {
            document.add(new Paragraph("AUTHORIZATION").setFont(headerFont).setMarginTop(30));

            Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100)).setMarginTop(20);

            signatureTable.addCell("Prepared By:\n\n\n_________________\nName & Signature\nDate: ___________");
            signatureTable.addCell("Reviewed By:\n\n\n_________________\nName & Signature\nDate: ___________");
            signatureTable.addCell("Approved By:\n\n\n_________________\nName & Signature\nDate: ___________");

            document.add(signatureTable);

            // Footer
            document.add(new Paragraph("This form is issued in accordance with the PPDA Regulations 2014.")
                    .setFont(PdfFontFactory.createFont()).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(20));
        } catch (IOException ex) {
            Logger.getLogger(PPDARequisitionDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String generateRequisitionNumber() {
        String agency = "URC";
        String procTy = "";
        String startY = "";
        String endY = "";
        String num = "";
        PeriodExtractor getPeriod = new PeriodExtractor();
        if (requisitionTypeCombo.getValue().equals(RequisitionType.CASH_REQUISITION)) {
            procTy = "UCash";
        }
        if (!requisitionTypeCombo.isEmpty() && !procTypeCombo.isEmpty() && !requisitionTypeCombo.getValue().equals(RequisitionType.CASH_REQUISITION)) {

            switch (procTypeCombo.getValue()) {
                case CONSULTANCY:
                    procTy = "Consu";
                    break;
                case NON_CONSULTANCY:
                    procTy = "NCons";
                    break;
                case SUPPLIES:
                    procTy = "Suppl";
                    break;
                case WORKS:
                    procTy = "Works";
                    break;
                default:
                    break;
            }

        }
        startY = getLastTwoChars(getPeriod.extractYears(selectedBudget.getFinancialYear())[0] + "");
        endY = getLastTwoChars(getPeriod.extractYears(selectedBudget.getFinancialYear())[1] + "");

        Pageable topOne = PageRequest.of(0, 1);
        Optional<RequisitionData> lastRequisition = requisitionDataService.getLastRequisitionForFiscal(selectedBudget);
        int nextNumber = 1; // default if no previous requisition
        if (lastRequisition.isPresent()) {
            String lastNumberStr = lastRequisition.get().getRequisitionNumber();
            String[] parts = lastNumberStr.split("/");
            String lastSeq = parts[parts.length - 1];
            nextNumber = Integer.parseInt(lastSeq) + 1;
        }
        String formattedSeq = String.format("%05d", nextNumber);
        return agency + "/" + procTy + "/" + startY + "/" + endY + "/" + formattedSeq;
    }

    public String getLastTwoChars(String input) {
        if (input == null || input.length() < 2) {
            return input; // return as is if shorter than 2 chars
        }
        return input.substring(input.length() - 2);
    }

    private String formatCurrency(Double amount) {
        if (amount == null) {
            return "0.0";
        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return formatter.format(amount);
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    public void setRequisitionData(RequisitionData requisition) {
        requisitionData = requisition;
    }

    public RequisitionData getRequisitionData() {
        return requisitionData;
    }

    public void setFormData() {
        if (requisitionData != null) {
            requisitionTypeCombo.setValue(requisitionData.getRequisitionType() != null ? requisitionData.getRequisitionType() : null);

            procTypeCombo.setValue(requisitionData.getProcType() != null ? requisitionData.getProcType() : null);

            catOfProcCombo.setValue(requisitionData.getCatOfProc() != null ? requisitionData.getCatOfProc() : null);

            requisitionNumberField.setValue(requisitionData.getRequisitionNumber() != null ? requisitionData.getRequisitionNumber() : "");

            requestedAmountField.setValue(requisitionData.getRequestedAmount() != null ? requisitionData.getRequestedAmount() : 0.0);

            projectCodeField.setValue(requisitionData.getProjectCode() != null ? requisitionData.getProjectCode() : "");

            projectTitleField.setValue(requisitionData.getProjectTitle() != null ? requisitionData.getProjectTitle() : "");

            isItAMuiltYearContractRadio.setValue(requisitionData.getIsMultiYearContract() != null ? requisitionData.getIsMultiYearContract() : IsMultiYearContract.YEAR_ONE); // default enum value

            subjectProc.setValue(requisitionData.getSubjectOfProcurement() != null ? requisitionData.getSubjectOfProcurement() : "");

            justificationField.setValue(requisitionData.getJustification() != null ? requisitionData.getJustification() : "");

            locationOfDeliveryField.setValue(requisitionData.getDeliveryLocation() != null ? requisitionData.getDeliveryLocation() : "");

            requiredDatePicker.setValue(requisitionData.getDateRequired() != null ? requisitionData.getDateRequired().toLocalDate() : null);

            procPlanRef.setValue(requisitionData.getProcurementPlanRef() != null ? requisitionData.getProcurementPlanRef() : "");
            itemsGrid.setItems(requisitionData.getItems());
        }
    }

    // Event handling
    public static class SaveEvent extends ComponentEvent<PPDARequisitionDialog> {

        private final RequisitionData requisition;

        public SaveEvent(PPDARequisitionDialog source, RequisitionData requisition) {
            super(source, false);
            this.requisition = requisition;
        }

        public RequisitionData getRequisition() {
            return requisition;
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    // Helper class for requisition item data
    public class RequisitionItemData {

        private int itemNumber;
        private String description;
        private Double quantity;
        private String unitOfMeasure;
        private Double estimatedUnitCost;
        private Double marketPrice;
        private Double totalCost;

        public void calculateTotalCost() {
            if (quantity != null && estimatedUnitCost != null) {
                totalCost = quantity * estimatedUnitCost;
            }
        }

        // Getters and setters
        public int getItemNumber() {
            return itemNumber;
        }

        public void setItemNumber(int itemNumber) {
            this.itemNumber = itemNumber;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public String getUnitOfMeasure() {
            return unitOfMeasure;
        }

        public void setUnitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
        }

        public Double getEstimatedUnitCost() {
            return estimatedUnitCost;
        }

        public void setEstimatedUnitCost(Double estimatedUnitCost) {
            this.estimatedUnitCost = estimatedUnitCost;
        }

        public Double getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(Double marketPrice) {
            this.marketPrice = marketPrice;
        }

        public Double getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(Double totalCost) {
            this.totalCost = totalCost;
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 6000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }

    private void openEditDialog(RequisitionItem item) {

        Dialog dialog = new Dialog();
        dialog.addClassName("requisition-dialog");

        H3 title = new H3("Edit Requisition Item");
        title.addClassName("dialog-title");

        // Description field
        TextField description = new TextField("Description");
        description.setWidthFull();
        description.setValue(item.getDescription() != null ? item.getDescription() : "");

        // Quantity field
        NumberField quantity = new NumberField("Quantity");
        quantity.setMin(1);
        quantity.setWidth("150px");
        quantity.setValue(item.getQuantity() != null ? item.getQuantity() : 1);

        // Unit cost field
        NumberField unitCost = new NumberField("Unit Cost");
        unitCost.setWidth("200px");
        unitCost.setValue(item.getEstimatedUnitCost() != null ? item.getEstimatedUnitCost() : 0.0);

        // Total cost field (read-only)
        NumberField totalCost = new NumberField("Total");
        totalCost.setWidth("200px");
        totalCost.setReadOnly(true);
        totalCost.setValue(quantity.getValue() * unitCost.getValue());

        // Update total dynamically
        quantity.addValueChangeListener(e -> totalCost.setValue(quantity.getValue() * unitCost.getValue()));
        unitCost.addValueChangeListener(e -> totalCost.setValue(quantity.getValue() * unitCost.getValue()));

        HorizontalLayout fields = new HorizontalLayout(description, quantity, unitCost, totalCost);
        fields.addClassName("dialog-form-row");

        currentRequisition = item.getRequisitionData();

        // Save button
        Button save = new Button("Save", e -> {
            // 1️⃣ Update item fields
            item.setDescription(description.getValue());
            item.setQuantity(quantity.getValue());
            item.setEstimatedUnitCost(unitCost.getValue()); // store unit cost, not total
            item.setTotalCost(totalCost.getValue());

            // 2️⃣ Refresh grid row
            ListDataProvider<RequisitionItem> dataProvider = (ListDataProvider<RequisitionItem>) itemsGrid.getDataProvider();
            dataProvider.refreshItem(item); // refresh single item
            itemsGrid.setItems(dataProvider);

            // 3️⃣ Recalculate requestedAmount
            double requestedAmount = currentRequisition.getItems().stream()
                    .mapToDouble(i -> (i.getEstimatedUnitCost() != null ? i.getEstimatedUnitCost() : 0.0)
                    * (i.getQuantity() != null ? i.getQuantity() : 0.0))
                    .sum();
            currentRequisition.setRequestedAmount(requestedAmount);
            currentRequisition.setTotalAmount(requestedAmount);
            // 5️⃣ Update bound UI fields
            requestedAmountField.setValue(requestedAmount);
            requisitionDataService.saveRequisition(currentRequisition);
            currentRequisition = null;

            dialog.close();
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Cancel button
        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.addClassName("dialog-actions");

        VerticalLayout content = new VerticalLayout(title, fields, actions);
        content.setPadding(false);
        content.setSpacing(true);
        dialog.add(content);
        dialog.open();
    }

    private void removeItemFromTable(RequisitionItem item) {

        if (item == null) {
            return;
        }

        // 1️⃣ Remove item from database
        if (item.getId() != null) { // only remove if persisted
            requisitionDataService.deleteRequisitionItem(item);
        }
        currentRequisition = item.getRequisitionData();
        // 2️⃣ Remove from in-memory list
        requisitionItems = currentRequisition.getItems();
        requisitionItems.remove(item);

        // 3️⃣ Renumber items
        for (int i = 0; i < requisitionItems.size(); i++) {
            requisitionItems.get(i).setItemNumber(i + 1);
        }

        // 4️⃣ Refresh grid
        itemsGrid.setItems(requisitionItems);

        // 5️⃣ Recalculate total requested amount
        double requestedAmount = requisitionItems.stream()
                .mapToDouble(i -> (i.getEstimatedUnitCost() != null ? i.getEstimatedUnitCost() : 0.0)
                * (i.getQuantity() != null ? i.getQuantity() : 0.0))
                .sum();
        currentRequisition.setRequestedAmount(requestedAmount);
        currentRequisition.setTotalAmount(requestedAmount);
        // 8️⃣ Update bound UI fields
        requestedAmountField.setValue(requestedAmount);

        // 7️⃣ Persist the updated RequisitionData
        requisitionDataService.saveRequisition(currentRequisition);
        currentRequisition = null;

        // 9️⃣ Show notification
        showNotification("Item removed", NotificationVariant.LUMO_CONTRAST);
    }

}
