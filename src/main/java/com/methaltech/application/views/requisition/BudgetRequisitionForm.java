
package com.methaltech.application.views.requisition;

import com.methaltech.application.data.bgtool.service.BudgetRequisitionService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetRequisition;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.SectionBudget;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class BudgetRequisitionForm extends Dialog {
    
    private final BudgetRequisitionService requisitionService;
    private final BudgetService budgetService;
    private final String currentUser;
    private final Budget selectedBudget;
    
    private Binder<BudgetRequisition> binder;
    private BudgetRequisition currentRequisition;
    
    // Form fields
    private ComboBox<DepartmentBudget> departmentCombo;
    private ComboBox<String> sectionCombo;
    private ComboBox<String> accountCodeCombo;
    private TextField accountNameField;
    private ComboBox<String> revenueSourceCombo;
    private TextField revenueSourceNameField;
    private ComboBox<BudgetRequisition.RequisitionType> requisitionTypeCombo;
    private NumberField requestedAmountField;
    private NumberField availableBudgetField;
    private TextArea purposeArea;
    private TextArea justificationArea;
    private DatePicker expectedDeliveryPicker;
    private TextField vendorNameField;
    private TextField vendorContactField;
    private ComboBox<BudgetRequisition.ProcurementMethod> procurementMethodCombo;
    private ComboBox<BudgetRequisition.PriorityLevel> priorityCombo;
    private TextArea notesArea;
    private Upload documentUpload;
    
    // Budget info display
    private Div budgetInfoCard;
    private Span availableBudgetDisplay;
    private Span utilizationDisplay;
    
    // Buttons
    private Button saveButton;
    private Button submitButton;
    private Button cancelButton;

    public BudgetRequisitionForm(List<DepartmentBudget> departments, String currentUser, 
                                BudgetRequisitionService requisitionService, BudgetService budgetService,
                                Budget selectedBudget) {
        this.requisitionService = requisitionService;
        this.budgetService = budgetService;
        this.currentUser = currentUser;
        this.selectedBudget = selectedBudget;
        
        setWidth("1000px");
        setHeight("800px");
        setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("budget-requisition-form-dialog");
        
        createForm(departments);
        setupValidation();
        setupEventHandlers();
    }

    private void createForm(List<DepartmentBudget> departments) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("requisition-form-layout");

        // Header
        H2 title = new H2("Create Budget Requisition");
        title.addClassName("form-title");
        
        Span subtitle = new Span("Submit a new budget requisition for " + 
                                (selectedBudget != null ? selectedBudget.getFinancialYear() : "current budget"));
        subtitle.addClassName("form-subtitle");

        // Budget info card
        budgetInfoCard = createBudgetInfoCard();

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("requisition-form");
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 2),
            new FormLayout.ResponsiveStep("900px", 3)
        );

        // Department selection
        departmentCombo = new ComboBox<>("Department");
        departmentCombo.setItems(departments);
        departmentCombo.setItemLabelGenerator(dept -> dept.getDepartmentName() + " (" + dept.getDepartmentCode() + ")");
        departmentCombo.setRequired(true);
        departmentCombo.addClassName("department-combo");
        departmentCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadSections(e.getValue().getDepartmentCode(),selectedBudget);
                updateBudgetInfo();
            }
        });

        // Section selection
        sectionCombo = new ComboBox<>("Section");
        sectionCombo.setRequired(true);
        sectionCombo.addClassName("section-combo");
        sectionCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadAccountCodes(e.getValue());
                loadRevenueSources(e.getValue());
                updateBudgetInfo();
            }
        });

        // Account code selection
        accountCodeCombo = new ComboBox<>("Account Code");
        accountCodeCombo.setRequired(true);
        accountCodeCombo.addClassName("account-code-combo");
        accountCodeCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadAccountName(e.getValue());
                updateBudgetInfo();
            }
        });

        // Account name (auto-filled)
        accountNameField = new TextField("Account Name");
        accountNameField.setReadOnly(true);
        accountNameField.addClassName("account-name-field");

        // Revenue source (optional)
        revenueSourceCombo = new ComboBox<>("Revenue Source");
        revenueSourceCombo.addClassName("revenue-source-combo");
        revenueSourceCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadRevenueSourceName(e.getValue());
            }
        });

        // Revenue source name (auto-filled)
        revenueSourceNameField = new TextField("Revenue Source Name");
        revenueSourceNameField.setReadOnly(true);
        revenueSourceNameField.addClassName("revenue-source-name-field");

        // Requisition type
        requisitionTypeCombo = new ComboBox<>("Requisition Type");
        requisitionTypeCombo.setItems(BudgetRequisition.RequisitionType.values());
        requisitionTypeCombo.setItemLabelGenerator(BudgetRequisition.RequisitionType::getDisplayName);
        requisitionTypeCombo.setRequired(true);
        requisitionTypeCombo.addClassName("requisition-type-combo");

        // Available budget (read-only)
        availableBudgetField = new NumberField("Available Budget (UGX)");
        availableBudgetField.setReadOnly(true);
        availableBudgetField.addClassName("available-budget-field");

        // Requested amount
        requestedAmountField = new NumberField("Requested Amount (UGX)");
        requestedAmountField.setRequired(true);
        requestedAmountField.setMin(1);
        requestedAmountField.setStep(1000);
        requestedAmountField.addClassName("requested-amount-field");
        requestedAmountField.addValueChangeListener(e -> updateBudgetInfo());

        // Purpose
        purposeArea = new TextArea("Purpose");
        purposeArea.setRequired(true);
        purposeArea.setMinLength(10);
        purposeArea.setMaxLength(1000);
        purposeArea.setPlaceholder("Describe the purpose of this requisition...");
        purposeArea.addClassName("purpose-area");

        // Justification
        justificationArea = new TextArea("Justification");
        justificationArea.setMaxLength(2000);
        justificationArea.setPlaceholder("Provide detailed justification for this requisition...");
        justificationArea.addClassName("justification-area");

        // Expected delivery date
        expectedDeliveryPicker = new DatePicker("Expected Delivery Date");
        expectedDeliveryPicker.setMin(LocalDate.now());
        expectedDeliveryPicker.addClassName("expected-delivery-picker");

        // Vendor information
        vendorNameField = new TextField("Vendor Name");
        vendorNameField.setPlaceholder("Enter vendor/supplier name");
        vendorNameField.addClassName("vendor-name-field");

        vendorContactField = new TextField("Vendor Contact");
        vendorContactField.setPlaceholder("Enter vendor contact information");
        vendorContactField.addClassName("vendor-contact-field");

        // Procurement method
        procurementMethodCombo = new ComboBox<>("Procurement Method");
        procurementMethodCombo.setItems(BudgetRequisition.ProcurementMethod.values());
        procurementMethodCombo.setItemLabelGenerator(BudgetRequisition.ProcurementMethod::getDisplayName);
        procurementMethodCombo.addClassName("procurement-method-combo");

        // Priority level
        priorityCombo = new ComboBox<>("Priority Level");
        priorityCombo.setItems(BudgetRequisition.PriorityLevel.values());
        priorityCombo.setItemLabelGenerator(BudgetRequisition.PriorityLevel::getDisplayName);
        priorityCombo.setValue(BudgetRequisition.PriorityLevel.MEDIUM);
        priorityCombo.setRequired(true);
        priorityCombo.addClassName("priority-combo");

        // Notes
        notesArea = new TextArea("Additional Notes");
        notesArea.setMaxLength(1000);
        notesArea.setPlaceholder("Any additional notes or comments...");
        notesArea.addClassName("notes-area");

        // Document upload
        MemoryBuffer buffer = new MemoryBuffer();
        documentUpload = new Upload(buffer);
        documentUpload.setAcceptedFileTypes(".pdf", ".doc", ".docx", ".xls", ".xlsx", ".jpg", ".png");
        documentUpload.setMaxFiles(5);
        documentUpload.setMaxFileSize(10 * 1024 * 1024); // 10MB
        documentUpload.addClassName("document-upload");

        // Add fields to form layout
        formLayout.add(departmentCombo, sectionCombo, accountCodeCombo);
        formLayout.add(accountNameField, revenueSourceCombo, revenueSourceNameField);
        formLayout.add(requisitionTypeCombo, availableBudgetField, requestedAmountField);
        formLayout.add(vendorNameField, vendorContactField, procurementMethodCombo);
        formLayout.add(expectedDeliveryPicker, priorityCombo);
        formLayout.setColspan(purposeArea, 3);
        formLayout.add(purposeArea);
        formLayout.setColspan(justificationArea, 3);
        formLayout.add(justificationArea);
        formLayout.setColspan(notesArea, 3);
        formLayout.add(notesArea);
        formLayout.setColspan(documentUpload, 3);
        formLayout.add(documentUpload);

        // Buttons
        HorizontalLayout buttonLayout = createButtonLayout();

        layout.add(title, subtitle, budgetInfoCard, formLayout, buttonLayout);
        add(layout);
    }

    private Div createBudgetInfoCard() {
        Div card = new Div();
        card.addClassName("budget-info-card");
        
        H3 title = new H3("Budget Information");
        title.addClassName("budget-info-title");
        
        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setWidthFull();
        infoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        
        availableBudgetDisplay = new Span("Available: UGX 0");
        availableBudgetDisplay.addClassName("available-budget-display");
        
        utilizationDisplay = new Span("Utilization: 0%");
        utilizationDisplay.addClassName("utilization-display");
        
        infoLayout.add(availableBudgetDisplay, utilizationDisplay);
        card.add(title, infoLayout);
        
        return card;
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

        saveButton = new Button("Save Draft");
        saveButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        saveButton.addClassName("save-button");
        saveButton.addClickListener(e -> saveRequisition(false));

        submitButton = new Button("Submit Requisition");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("submit-button");
        submitButton.addClickListener(e -> saveRequisition(true));

        buttonLayout.add(cancelButton, saveButton, submitButton);
        return buttonLayout;
    }

    private void setupValidation() {
        binder = new Binder<>(BudgetRequisition.class);

        // Department validation
        binder.forField(departmentCombo)
            .asRequired("Department is required")
            .bind(
                requisition -> null, // We'll handle this manually
                (requisition, dept) -> {
                    if (dept != null) {
                        requisition.setDepartmentCode(dept.getDepartmentCode());
                        requisition.setDepartmentName(dept.getDepartmentName());
                    }
                }
            );

        // Section validation
        binder.forField(sectionCombo)
            .asRequired("Section is required")
            .bind(
                BudgetRequisition::getSectionCode,
                BudgetRequisition::setSectionCode
            );

        // Account code validation
        binder.forField(accountCodeCombo)
            .asRequired("Account code is required")
            .bind(
                BudgetRequisition::getAccountCode,
                BudgetRequisition::setAccountCode
            );

        // Requisition type validation
        binder.forField(requisitionTypeCombo)
            .asRequired("Requisition type is required")
            .bind(BudgetRequisition::getRequisitionType, BudgetRequisition::setRequisitionType);

        // Requested amount validation
        binder.forField(requestedAmountField)
            .asRequired("Requested amount is required")
            .withValidator(new DoubleRangeValidator("Amount must be greater than 0", 1.0, Double.MAX_VALUE))
            .bind(BudgetRequisition::getRequestedAmount, BudgetRequisition::setRequestedAmount);

        // Purpose validation
        binder.forField(purposeArea)
            .asRequired("Purpose is required")
            .withValidator(new StringLengthValidator("Purpose must be between 10 and 1000 characters", 10, 1000))
            .bind(BudgetRequisition::getPurpose, BudgetRequisition::setPurpose);

        // Other field bindings
        binder.forField(justificationArea)
            .bind(BudgetRequisition::getJustification, BudgetRequisition::setJustification);

        binder.forField(vendorNameField)
            .bind(BudgetRequisition::getVendorName, BudgetRequisition::setVendorName);

        binder.forField(vendorContactField)
            .bind(BudgetRequisition::getVendorContact, BudgetRequisition::setVendorContact);

        binder.forField(procurementMethodCombo)
            .bind(BudgetRequisition::getProcurementMethod, BudgetRequisition::setProcurementMethod);

        binder.forField(priorityCombo)
            .asRequired("Priority level is required")
            .bind(BudgetRequisition::getPriorityLevel, BudgetRequisition::setPriorityLevel);

        binder.forField(notesArea)
            .bind(BudgetRequisition::getNotes, BudgetRequisition::setNotes);

        binder.forField(expectedDeliveryPicker)
            .bind(
                requisition -> requisition.getExpectedDeliveryDate() != null ? 
                    requisition.getExpectedDeliveryDate().toLocalDate() : null,
                (requisition, date) -> requisition.setExpectedDeliveryDate(
                    date != null ? date.atStartOfDay() : null)
            );
    }

    private void setupEventHandlers() {
        // Real-time validation feedback
        binder.addStatusChangeListener(e -> {
            boolean isValid = binder.isValid();
            saveButton.setEnabled(isValid);
            submitButton.setEnabled(isValid);
        });

        // Document upload handlers
        documentUpload.addSucceededListener(e -> {
            showNotification("Document uploaded successfully: " + e.getFileName(), NotificationVariant.LUMO_SUCCESS);
        });

        documentUpload.addFailedListener(e -> {
            showNotification("Failed to upload document: " + e.getReason().getMessage(), NotificationVariant.LUMO_ERROR);
        });
    }

    private void loadSections(String departmentCode,Budget budget) {
        try {
            List<SectionBudget> sections = budgetService.getDepartmentSections(departmentCode,budget);
            List<String> sectionCodes = sections.stream()
                .map(SectionBudget::getSectionCode)
                .toList();
            sectionCombo.setItems(sectionCodes);
            sectionCombo.clear();
            accountCodeCombo.clear();
            revenueSourceCombo.clear();
        } catch (Exception e) {
            showNotification("Error loading sections: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadAccountCodes(String sectionCode) {
        // Mock account codes - replace with actual service call
        List<String> accountCodes = Arrays.asList(
            "5211001", "5211002", "5211003", "5212001", "5212002",
            "5213001", "5213002", "5214001", "5214002", "5215001"
        );
        accountCodeCombo.setItems(accountCodes);
        accountCodeCombo.clear();
    }

    private void loadRevenueSources(String sectionCode) {
        // Mock revenue sources - replace with actual service call
        List<String> revenueSources = Arrays.asList(
            "GOV-GRANT", "DONOR-FUND", "IGF-REVENUE", "LOAN-FUND", "OTHER-FUND"
        );
        revenueSourceCombo.setItems(revenueSources);
        revenueSourceCombo.clear();
    }

    private void loadAccountName(String accountCode) {
        // Mock account names - replace with actual service call
        String accountName = switch (accountCode) {
            case "5211001" -> "Office Supplies";
            case "5211002" -> "Stationery";
            case "5211003" -> "Printing Materials";
            case "5212001" -> "Fuel and Lubricants";
            case "5212002" -> "Vehicle Maintenance";
            case "5213001" -> "Training Expenses";
            case "5213002" -> "Workshop Costs";
            case "5214001" -> "Consultancy Services";
            case "5214002" -> "Professional Services";
            case "5215001" -> "Utilities";
            default -> "Unknown Account";
        };
        accountNameField.setValue(accountName);
    }

    private void loadRevenueSourceName(String revenueSourceCode) {
        // Mock revenue source names - replace with actual service call
        String revenueSourceName = switch (revenueSourceCode) {
            case "GOV-GRANT" -> "Government Grant";
            case "DONOR-FUND" -> "Donor Funding";
            case "IGF-REVENUE" -> "Internally Generated Funds";
            case "LOAN-FUND" -> "Loan Financing";
            case "OTHER-FUND" -> "Other Funding Sources";
            default -> "Unknown Revenue Source";
        };
        revenueSourceNameField.setValue(revenueSourceName);
    }

    private void updateBudgetInfo() {
        if (departmentCombo.getValue() != null && sectionCombo.getValue() != null && accountCodeCombo.getValue() != null) {
            // Mock available budget calculation - replace with actual service call
            double availableBudget = 50_000_000 + (accountCodeCombo.getValue().hashCode() % 100_000_000);
            availableBudgetField.setValue(availableBudget);
            
            String formattedBudget = formatCurrency(availableBudget);
            availableBudgetDisplay.setText("Available: " + formattedBudget);
            
            if (requestedAmountField.getValue() != null) {
                double utilization = (requestedAmountField.getValue() / availableBudget) * 100;
                utilizationDisplay.setText(String.format("Utilization: %.1f%%", utilization));
                
                if (utilization > 100) {
                    utilizationDisplay.addClassName("over-budget");
                    availableBudgetDisplay.addClassName("over-budget");
                } else if (utilization > 80) {
                    utilizationDisplay.addClassName("near-budget");
                    availableBudgetDisplay.addClassName("near-budget");
                } else {
                    utilizationDisplay.removeClassName("over-budget");
                    utilizationDisplay.removeClassName("near-budget");
                    availableBudgetDisplay.removeClassName("over-budget");
                    availableBudgetDisplay.removeClassName("near-budget");
                }
            }
        }
    }

    private void saveRequisition(boolean submit) {
        try {
            if (currentRequisition == null) {
                currentRequisition = new BudgetRequisition();
            }

            // Validate and bind form data
            binder.writeBean(currentRequisition);

            // Set additional fields
            currentRequisition.setBudget(selectedBudget);
            currentRequisition.setCreatedBy(currentUser);
            currentRequisition.setAccountName(accountNameField.getValue());
            currentRequisition.setRevenueSourceCode(revenueSourceCombo.getValue());
            currentRequisition.setRevenueSourceName(revenueSourceNameField.getValue());
            currentRequisition.setAvailableBudget(availableBudgetField.getValue());

            // Save the requisition
            BudgetRequisition savedRequisition = requisitionService.createRequisition(currentRequisition);

            if (submit) {
                savedRequisition = requisitionService.submitRequisition(savedRequisition.getId(), currentUser);
                showNotification("Requisition submitted successfully. Number: " + savedRequisition.getRequisitionNumber(), 
                               NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Requisition saved as draft. Number: " + savedRequisition.getRequisitionNumber(), 
                               NotificationVariant.LUMO_PRIMARY);
            }

            // Fire save event
            fireEvent(new SaveEvent(this, savedRequisition));
            close();

        } catch (ValidationException e) {
            showNotification("Please correct the validation errors", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Error saving requisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private String formatCurrency(double amount) {
        if (Math.abs(amount) >= 1_000_000_000) {
            return String.format("UGX %.1fB", amount / 1_000_000_000);
        } else if (Math.abs(amount) >= 1_000_000) {
            return String.format("UGX %.1fM", amount / 1_000_000);
        } else if (Math.abs(amount) >= 1_000) {
            return String.format("UGX %.0fK", amount / 1_000);
        } else {
            return String.format("UGX %.0f", amount);
        }
    }

    public void setRequisition(BudgetRequisition requisition) {
        this.currentRequisition = requisition;
        binder.readBean(requisition);
    }

    // Event handling
    public static class SaveEvent extends ComponentEvent<BudgetRequisitionForm> {
        private final BudgetRequisition requisition;

        public SaveEvent(BudgetRequisitionForm source, BudgetRequisition requisition) {
            super(source, false);
            this.requisition = requisition;
        }

        public BudgetRequisition getRequisition() {
            return requisition;
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
