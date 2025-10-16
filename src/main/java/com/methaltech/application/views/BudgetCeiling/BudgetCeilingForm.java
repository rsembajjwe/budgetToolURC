
package com.methaltech.application.views.BudgetCeiling;

import com.methaltech.application.data.bgtool.service.BudgetCeilingService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetCeiling;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class BudgetCeilingForm extends Dialog {
    
    private final BudgetCeilingService ceilingService;
    private final String currentUser;
    private final Budget selectedBudget;
    
    private Binder<BudgetCeiling> binder;
    private BudgetCeiling currentCeiling;
    
    // Form fields
    private ComboBox<BudgetCeiling.CeilingType> ceilingTypeCombo;
    private ComboBox<DepartmentBudget> departmentCombo;
    private TextField sectionCodeField;
    private TextField sectionNameField;
    private TextField revenueSourceCodeField;
    private TextField revenueSourceNameField;
    private TextField accountCodeField;
    private TextField accountNameField;
    private NumberField ceilingAmountField;
    private NumberField allocatedAmountField;
    private DatePicker effectiveDatePicker;
    private DatePicker expiryDatePicker;
    private ComboBox<BudgetCeiling.CeilingStatus> statusCombo;
    private TextArea descriptionArea;
    
    // Buttons
    private Button saveButton;
    private Button cancelButton;

    public BudgetCeilingForm(List<DepartmentBudget> departments, String currentUser, 
                            BudgetCeilingService ceilingService, Budget selectedBudget) {
        this.ceilingService = ceilingService;
        this.currentUser = currentUser;
        this.selectedBudget = selectedBudget;
        
        setWidth("900px");
        setHeight("800px");
        setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("budget-ceiling-form-dialog");
        
        createForm(departments);
        setupValidation();
        setupEventHandlers();
    }

    private void createForm(List<DepartmentBudget> departments) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("ceiling-form-layout");

        // Header
        H2 title = new H2("Budget Ceiling Configuration");
        title.addClassName("form-title");
        
        Span subtitle = new Span("Set budget ceilings at different organizational levels");
        subtitle.addClassName("form-subtitle");

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("ceiling-form");
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 2)
        );

        // Ceiling type selection
        ceilingTypeCombo = new ComboBox<>("Ceiling Type");
        ceilingTypeCombo.setItems(BudgetCeiling.CeilingType.values());
        ceilingTypeCombo.setItemLabelGenerator(BudgetCeiling.CeilingType::getDisplayName);
        ceilingTypeCombo.setRequired(true);
        ceilingTypeCombo.addClassName("ceiling-type-combo");
        ceilingTypeCombo.addValueChangeListener(e -> updateFormFields(e.getValue()));

        // Department selection
        departmentCombo = new ComboBox<>("Department");
        departmentCombo.setItems(departments);
        departmentCombo.setItemLabelGenerator(dept -> dept.getDepartmentName() + " (" + dept.getDepartmentCode() + ")");
        departmentCombo.setRequired(true);
        departmentCombo.addClassName("department-combo");

        // Section fields
        sectionCodeField = new TextField("Section Code");
        sectionCodeField.setPlaceholder("Enter section code");
        sectionCodeField.addClassName("section-code-field");

        sectionNameField = new TextField("Section Name");
        sectionNameField.setPlaceholder("Enter section name");
        sectionNameField.addClassName("section-name-field");

        // Revenue source fields
        revenueSourceCodeField = new TextField("Revenue Source Code");
        revenueSourceCodeField.setPlaceholder("Enter revenue source code");
        revenueSourceCodeField.addClassName("revenue-source-code-field");

        revenueSourceNameField = new TextField("Revenue Source Name");
        revenueSourceNameField.setPlaceholder("Enter revenue source name");
        revenueSourceNameField.addClassName("revenue-source-name-field");

        // Account code fields
        accountCodeField = new TextField("Account Code");
        accountCodeField.setPlaceholder("Enter account code");
        accountCodeField.addClassName("account-code-field");

        accountNameField = new TextField("Account Name");
        accountNameField.setPlaceholder("Enter account name");
        accountNameField.addClassName("account-name-field");

        // Amount fields
        ceilingAmountField = new NumberField("Ceiling Amount (UGX)");
        ceilingAmountField.setRequired(true);
        ceilingAmountField.setMin(1);
        ceilingAmountField.setStep(1000);
        ceilingAmountField.addClassName("ceiling-amount-field");

        allocatedAmountField = new NumberField("Initial Allocated Amount (UGX)");
        allocatedAmountField.setMin(0);
        allocatedAmountField.setStep(1000);
        allocatedAmountField.setValue(0.0);
        allocatedAmountField.addClassName("allocated-amount-field");

        // Date fields
        effectiveDatePicker = new DatePicker("Effective Date");
        effectiveDatePicker.setRequired(true);
        effectiveDatePicker.setValue(LocalDate.now());
        effectiveDatePicker.addClassName("effective-date-picker");

        expiryDatePicker = new DatePicker("Expiry Date");
        expiryDatePicker.setPlaceholder("Optional - leave blank for no expiry");
        expiryDatePicker.addClassName("expiry-date-picker");

        // Status
        statusCombo = new ComboBox<>("Status");
        statusCombo.setItems(BudgetCeiling.CeilingStatus.values());
        statusCombo.setItemLabelGenerator(BudgetCeiling.CeilingStatus::getDisplayName);
        statusCombo.setValue(BudgetCeiling.CeilingStatus.ACTIVE);
        statusCombo.setRequired(true);
        statusCombo.addClassName("status-combo");

        // Description
        descriptionArea = new TextArea("Description");
        descriptionArea.setMaxLength(1000);
        descriptionArea.setPlaceholder("Enter description for this budget ceiling...");
        descriptionArea.addClassName("description-area");

        // Add fields to form layout
        formLayout.add(ceilingTypeCombo, departmentCombo);
        formLayout.add(sectionCodeField, sectionNameField);
        formLayout.add(revenueSourceCodeField, revenueSourceNameField);
        formLayout.add(accountCodeField, accountNameField);
        formLayout.add(ceilingAmountField, allocatedAmountField);
        formLayout.add(effectiveDatePicker, expiryDatePicker);
        formLayout.add(statusCombo);
        formLayout.setColspan(descriptionArea, 2);
        formLayout.add(descriptionArea);

        // Initially hide all optional fields
        updateFormFields(null);

        // Buttons
        HorizontalLayout buttonLayout = createButtonLayout();

        layout.add(title, subtitle, formLayout, buttonLayout);
        add(layout);
    }

    private void updateFormFields(BudgetCeiling.CeilingType ceilingType) {
        // Hide all optional fields first
        sectionCodeField.setVisible(false);
        sectionNameField.setVisible(false);
        revenueSourceCodeField.setVisible(false);
        revenueSourceNameField.setVisible(false);
        accountCodeField.setVisible(false);
        accountNameField.setVisible(false);

        if (ceilingType != null) {
            switch (ceilingType) {
                case SECTION:
                    sectionCodeField.setVisible(true);
                    sectionNameField.setVisible(true);
                    sectionCodeField.setRequired(true);
                    sectionNameField.setRequired(true);
                    break;
                case REVENUE_SOURCE:
                    sectionCodeField.setVisible(true);
                    sectionNameField.setVisible(true);
                    revenueSourceCodeField.setVisible(true);
                    revenueSourceNameField.setVisible(true);
                    sectionCodeField.setRequired(true);
                    sectionNameField.setRequired(true);
                    revenueSourceCodeField.setRequired(true);
                    revenueSourceNameField.setRequired(true);
                    break;
                case ACCOUNT_CODE:
                    sectionCodeField.setVisible(true);
                    sectionNameField.setVisible(true);
                    revenueSourceCodeField.setVisible(true);
                    revenueSourceNameField.setVisible(true);
                    accountCodeField.setVisible(true);
                    accountNameField.setVisible(true);
                    sectionCodeField.setRequired(true);
                    sectionNameField.setRequired(true);
                    revenueSourceCodeField.setRequired(true);
                    revenueSourceNameField.setRequired(true);
                    accountCodeField.setRequired(true);
                    accountNameField.setRequired(true);
                    break;
            }
        }
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName("form-buttons");

        cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("cancel-button");
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Save Ceiling");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClassName("save-button");
        saveButton.addClickListener(e -> saveCeiling());

        buttonLayout.add(cancelButton, saveButton);
        return buttonLayout;
    }

    private void setupValidation() {
        binder = new Binder<>(BudgetCeiling.class);

        // Ceiling type validation
        binder.forField(ceilingTypeCombo)
            .asRequired("Ceiling type is required")
            .bind(BudgetCeiling::getCeilingType, BudgetCeiling::setCeilingType);

        // Department validation
        binder.forField(departmentCombo)
            .asRequired("Department is required")
            .bind(
                ceiling -> null, // We'll handle this manually
                (ceiling, dept) -> {
                    if (dept != null) {
                        ceiling.setDepartmentCode(dept.getDepartmentCode());
                        ceiling.setDepartmentName(dept.getDepartmentName());
                    }
                }
            );

        // Section validation
        binder.forField(sectionCodeField)
            .withValidator(new StringLengthValidator("Section code must be between 2 and 50 characters", 2, 50))
            .bind(BudgetCeiling::getSectionCode, BudgetCeiling::setSectionCode);

        binder.forField(sectionNameField)
            .withValidator(new StringLengthValidator("Section name must be between 2 and 200 characters", 2, 200))
            .bind(BudgetCeiling::getSectionName, BudgetCeiling::setSectionName);

        // Revenue source validation
        binder.forField(revenueSourceCodeField)
            .withValidator(new StringLengthValidator("Revenue source code must be between 2 and 50 characters", 2, 50))
            .bind(BudgetCeiling::getRevenueSourceCode, BudgetCeiling::setRevenueSourceCode);

        binder.forField(revenueSourceNameField)
            .withValidator(new StringLengthValidator("Revenue source name must be between 2 and 200 characters", 2, 200))
            .bind(BudgetCeiling::getRevenueSourceName, BudgetCeiling::setRevenueSourceName);

        // Account code validation
        binder.forField(accountCodeField)
            .withValidator(new StringLengthValidator("Account code must be between 2 and 50 characters", 2, 50))
            .bind(BudgetCeiling::getAccountCode, BudgetCeiling::setAccountCode);

        binder.forField(accountNameField)
            .withValidator(new StringLengthValidator("Account name must be between 2 and 200 characters", 2, 200))
            .bind(BudgetCeiling::getAccountName, BudgetCeiling::setAccountName);

        // Amount validation
        binder.forField(ceilingAmountField)
            .asRequired("Ceiling amount is required")
            .withValidator(new DoubleRangeValidator("Amount must be greater than 0", 1.0, Double.MAX_VALUE))
            .bind(BudgetCeiling::getCeilingAmount, BudgetCeiling::setCeilingAmount);

        binder.forField(allocatedAmountField)
            .withValidator(new DoubleRangeValidator("Allocated amount must be 0 or greater", 0.0, Double.MAX_VALUE))
            .bind(BudgetCeiling::getAllocatedAmount, BudgetCeiling::setAllocatedAmount);

        // Date validation
        binder.forField(effectiveDatePicker)
            .asRequired("Effective date is required")
            .bind(
                ceiling -> ceiling.getEffectiveDate() != null ? ceiling.getEffectiveDate().toLocalDate() : null,
                (ceiling, date) -> ceiling.setEffectiveDate(date != null ? date.atStartOfDay() : null)
            );

        binder.forField(expiryDatePicker)
            .bind(
                ceiling -> ceiling.getExpiryDate() != null ? ceiling.getExpiryDate().toLocalDate() : null,
                (ceiling, date) -> ceiling.setExpiryDate(date != null ? date.atTime(23, 59, 59) : null)
            );

        // Status validation
        binder.forField(statusCombo)
            .asRequired("Status is required")
            .bind(BudgetCeiling::getCeilingStatus, BudgetCeiling::setCeilingStatus);

        // Description validation
        binder.forField(descriptionArea)
            .withValidator(new StringLengthValidator("Description must be less than 1000 characters", 0, 1000))
            .bind(BudgetCeiling::getDescription, BudgetCeiling::setDescription);
    }

    private void setupEventHandlers() {
        // Real-time validation feedback
        binder.addStatusChangeListener(e -> {
            saveButton.setEnabled(binder.isValid());
        });
    }

    private void saveCeiling() {
        try {
            if (currentCeiling == null) {
                currentCeiling = new BudgetCeiling();
            }

            // Validate and bind form data
            binder.writeBean(currentCeiling);

            // Set additional fields
            currentCeiling.setBudget(selectedBudget);
            currentCeiling.setCreatedBy(currentUser);
            currentCeiling.setApprovalStatus(BudgetCeiling.ApprovalStatus.DRAFT);

            // Save the ceiling
            BudgetCeiling savedCeiling = ceilingService.createCeiling(currentCeiling);

            showNotification("Budget ceiling created successfully", NotificationVariant.LUMO_SUCCESS);

            // Fire save event
            fireEvent(new SaveEvent(this, savedCeiling));
            close();

        } catch (ValidationException e) {
            showNotification("Please correct the validation errors", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Error saving ceiling: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    public void setCeiling(BudgetCeiling ceiling) {
        this.currentCeiling = ceiling;
        binder.readBean(ceiling);
        updateFormFields(ceiling.getCeilingType());
    }

    // Event handling
    public static class SaveEvent extends ComponentEvent<BudgetCeilingForm> {
        private final BudgetCeiling ceiling;

        public SaveEvent(BudgetCeilingForm source, BudgetCeiling ceiling) {
            super(source, false);
            this.ceiling = ceiling;
        }

        public BudgetCeiling getCeiling() {
            return ceiling;
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }
}
