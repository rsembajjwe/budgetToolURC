
package com.methaltech.application.views.approvals;

import com.methaltech.application.data.bgtool.service.BudgetApprovalsService;
import com.methaltech.application.data.entity.bgtool.BudgetApprovals;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class ApprovalRequestForm extends Dialog {
    
    private final BudgetApprovalsService approvalsService;
    private final String currentUser;
    
    private Binder<BudgetApprovals> binder;
    private BudgetApprovals currentApproval;
    
    // Form fields
    private ComboBox<DepartmentBudget> departmentCombo;
    private TextField sectionField;
    private ComboBox<BudgetApprovals.RequestType> requestTypeCombo;
    private NumberField requestedAmountField;
    private NumberField currentBudgetField;
    private ComboBox<BudgetApprovals.PriorityLevel> priorityCombo;
    private TextArea justificationArea;
    private Upload documentUpload;
    
    // Buttons
    private Button saveButton;
    private Button cancelButton;

    public ApprovalRequestForm(List<DepartmentBudget> departments, String currentUser, BudgetApprovalsService approvalsService) {
        this.approvalsService = approvalsService;
        this.currentUser = currentUser;
        
        setWidth("800px");
        setHeight("700px");
        setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("approval-request-form-dialog");
        
        createForm(departments);
        setupValidation();
        setupEventHandlers();
    }

    private void createForm(List<DepartmentBudget> departments) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("approval-form-layout");

        // Header
        H2 title = new H2("New Budget Request");
        title.addClassName("form-title");
        
        Span subtitle = new Span("Submit a new budget approval request");
        subtitle.addClassName("form-subtitle");

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("approval-form");
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 2)
        );

        // Department selection
        departmentCombo = new ComboBox<>("Department");
        departmentCombo.setItems(departments);
        departmentCombo.setItemLabelGenerator(dept -> dept.getDepartmentName() + " (" + dept.getDepartmentCode() + ")");
        departmentCombo.setRequired(true);
        departmentCombo.addClassName("department-combo");
        departmentCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                currentBudgetField.setValue(e.getValue().getTotalBudget());
            }
        });

        // Section field
        sectionField = new TextField("Section");
        sectionField.setPlaceholder("Enter section name or code");
        sectionField.addClassName("section-field");

        // Request type
        requestTypeCombo = new ComboBox<>("Request Type");
        requestTypeCombo.setItems(BudgetApprovals.RequestType.values());
        requestTypeCombo.setItemLabelGenerator(BudgetApprovals.RequestType::getDisplayName);
        requestTypeCombo.setRequired(true);
        requestTypeCombo.addClassName("request-type-combo");

        // Current budget (read-only)
        currentBudgetField = new NumberField("Current Budget (UGX)");
        currentBudgetField.setReadOnly(true);
        currentBudgetField.addClassName("current-budget-field");

        // Requested amount
        requestedAmountField = new NumberField("Requested Amount (UGX)");
        requestedAmountField.setRequired(true);
        requestedAmountField.setMin(1);
        requestedAmountField.setStep(1000);
        requestedAmountField.addClassName("requested-amount-field");

        // Priority level
        priorityCombo = new ComboBox<>("Priority Level");
        priorityCombo.setItems(BudgetApprovals.PriorityLevel.values());
        priorityCombo.setItemLabelGenerator(BudgetApprovals.PriorityLevel::getDisplayName);
        priorityCombo.setValue(BudgetApprovals.PriorityLevel.MEDIUM);
        priorityCombo.setRequired(true);
        priorityCombo.addClassName("priority-combo");

        // Justification
        justificationArea = new TextArea("Justification");
        justificationArea.setRequired(true);
        justificationArea.setMinLength(50);
        justificationArea.setMaxLength(2000);
        justificationArea.setPlaceholder("Provide detailed justification for this budget request...");
        justificationArea.addClassName("justification-area");

        // Document upload
        MemoryBuffer buffer = new MemoryBuffer();
        documentUpload = new Upload(buffer);
        documentUpload.setAcceptedFileTypes(".pdf", ".doc", ".docx", ".xls", ".xlsx");
        documentUpload.setMaxFiles(5);
        documentUpload.setMaxFileSize(10 * 1024 * 1024); // 10MB
        documentUpload.addClassName("document-upload");

        // Add fields to form layout
        formLayout.add(departmentCombo, sectionField);
        formLayout.add(requestTypeCombo, priorityCombo);
        formLayout.add(currentBudgetField, requestedAmountField);
        formLayout.setColspan(justificationArea, 2);
        formLayout.add(justificationArea);
        formLayout.setColspan(documentUpload, 2);
        formLayout.add(documentUpload);

        // Buttons
        HorizontalLayout buttonLayout = createButtonLayout();

        layout.add(title, subtitle, formLayout, buttonLayout);
        add(layout);
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

        saveButton = new Button("Submit Request");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClassName("save-button");
        saveButton.addClickListener(e -> saveRequest());

        buttonLayout.add(cancelButton, saveButton);
        return buttonLayout;
    }

    private void setupValidation() {
        binder = new Binder<>(BudgetApprovals.class);

        // Department validation
        binder.forField(departmentCombo)
            .asRequired("Department is required")
            .bind(
                approval -> null, // We'll handle this manually
                (approval, dept) -> {
                    if (dept != null) {
                        approval.setDepartmentCode(dept.getDepartmentCode());
                        approval.setDepartmentName(dept.getDepartmentName());
                    }
                }
            );

        // Section validation
        binder.forField(sectionField)
            .withValidator(new StringLengthValidator("Section name must be between 2 and 100 characters", 2, 100))
            .bind(BudgetApprovals::getSectionName, BudgetApprovals::setSectionName);

        // Request type validation
        binder.forField(requestTypeCombo)
            .asRequired("Request type is required")
            .bind(BudgetApprovals::getRequestType, BudgetApprovals::setRequestType);

        // Requested amount validation
        binder.forField(requestedAmountField)
            .asRequired("Requested amount is required")
            .withValidator(new DoubleRangeValidator("Amount must be greater than 0", 1.0, Double.MAX_VALUE))
            .bind(BudgetApprovals::getRequestedAmount, BudgetApprovals::setRequestedAmount);

        // Current budget
        binder.forField(currentBudgetField)
            .bind(BudgetApprovals::getCurrentBudget, BudgetApprovals::setCurrentBudget);

        // Priority validation
        binder.forField(priorityCombo)
            .asRequired("Priority level is required")
            .bind(BudgetApprovals::getPriorityLevel, BudgetApprovals::setPriorityLevel);

        // Justification validation
        binder.forField(justificationArea)
            .asRequired("Justification is required")
            .withValidator(new StringLengthValidator("Justification must be between 50 and 2000 characters", 50, 2000))
            .bind(BudgetApprovals::getJustification, BudgetApprovals::setJustification);
    }

    private void setupEventHandlers() {
        // Real-time validation feedback
        binder.addStatusChangeListener(e -> {
            saveButton.setEnabled(binder.isValid());
        });

        // Document upload handlers
        documentUpload.addSucceededListener(e -> {
            showNotification("Document uploaded successfully: " + e.getFileName(), NotificationVariant.LUMO_SUCCESS);
        });

        documentUpload.addFailedListener(e -> {
            showNotification("Failed to upload document: " + e.getReason().getMessage(), NotificationVariant.LUMO_ERROR);
        });
    }

    private void saveRequest() {
        try {
            if (currentApproval == null) {
                currentApproval = new BudgetApprovals();
            }

            // Validate and bind form data
            binder.writeBean(currentApproval);

            // Set additional fields
            currentApproval.setRequestedBy(currentUser);
            currentApproval.setSectionCode(sectionField.getValue());

            // Save the approval request
            BudgetApprovals savedApproval = approvalsService.createApprovalRequest(currentApproval);

            showNotification("Budget request submitted successfully. Request ID: " + savedApproval.getRequestId(), 
                           NotificationVariant.LUMO_SUCCESS);

            // Fire save event
            fireEvent(new SaveEvent(this, savedApproval));
            close();

        } catch (ValidationException e) {
            showNotification("Please correct the validation errors", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            showNotification("Error saving request: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    public void setApproval(BudgetApprovals approval) {
        this.currentApproval = approval;
        binder.readBean(approval);
    }

    // Event handling
    public static class SaveEvent extends ComponentEvent<ApprovalRequestForm> {
        private final BudgetApprovals approval;

        public SaveEvent(ApprovalRequestForm source, BudgetApprovals approval) {
            super(source, false);
            this.approval = approval;
        }

        public BudgetApprovals getApproval() {
            return approval;
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
