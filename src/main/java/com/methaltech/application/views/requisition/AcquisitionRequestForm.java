package com.methaltech.application.views.requisition;

import com.methaltech.application.data.bgtool.service.AcquisitionRequestService;
import com.methaltech.application.data.bgtool.service.RequisitionDataService;
import com.methaltech.application.data.entity.bgtool.AcquisitionRequest;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.COABudgetInfo;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionType;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;

public class AcquisitionRequestForm extends Dialog {

    private final RequisitionDataService acquisitionService;
    private final String currentUser;
    private final Budget selectedBudget;
    private final Organisation selectedOrganisation;
    private final COA selectedCOA;
    private final UrcDeptSectionAnlDimbgt selectedSection;
    private final COABudgetInfo coaBudgetInfo;

    private Binder<AcquisitionRequest> binder;
    private RequisitionData currentAcquisition;

    // Form fields
    private NumberField requestedAmountField;
    private TextArea purposeArea;
    private TextArea justificationArea;
    private TextField vendorNameField;
    private TextArea vendorAddressArea;
    private TextField vendorContactField;
    private TextField vendorTinField;
    private TextArea deliveryLocationArea;
    private DatePicker deliveryDatePicker;
    private TextField paymentTermsField;
    private TextField warrantyPeriodField;
    private ComboBox<RequisitionType> procurementMethodCombo;
    private ComboBox<RequisitionType> priorityCombo;
    private TextArea technicalSpecsArea;
    private TextArea notesArea;

    // Budget info display
    private Div budgetInfoCard;

    // Buttons
    private Button saveButton;
    private Button submitButton;
    private Button cancelButton;

    public AcquisitionRequestForm( Budget selectedBudget,
            Organisation selectedOrganisation, COA selectedCOA,
            UrcDeptSectionAnlDimbgt selectedSection, COABudgetInfo coaBudgetInfo,
            String currentUser, RequisitionDataService acquisitionService) {
        this.selectedBudget = selectedBudget;
        this.selectedOrganisation = selectedOrganisation;
        this.selectedCOA = selectedCOA;
        this.selectedSection = selectedSection;
        this.coaBudgetInfo = coaBudgetInfo;
        this.currentUser = currentUser;
        this.acquisitionService = acquisitionService;

        setWidth("1000px");
        setHeight("800px");
        setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("acquisition-request-form-dialog");

        createForm();
        setupValidation();
        setupEventHandlers();
    }

    private void createForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("acquisition-form-layout");

        // Header
        H2 title = new H2("Create " );
        title.addClassName("form-title");

        Span subtitle = new Span("PPDA Uganda Compliant Acquisition Request");
        subtitle.addClassName("form-subtitle");

        // Budget info card
        budgetInfoCard = createBudgetInfoCard();

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("acquisition-form");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3)
        );

        // Create form fields
        createFormFields(formLayout);

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

        HorizontalLayout infoGrid = new HorizontalLayout();
        infoGrid.setWidthFull();
        infoGrid.setSpacing(true);

        infoGrid.add(
                createBudgetInfoItem("Account", selectedCOA.getCode() + " - " + selectedCOA.getName()),
                createBudgetInfoItem("Budget Total", formatCurrency(coaBudgetInfo.getBudgetTotal())),
                createBudgetInfoItem("Available Balance", formatCurrency(coaBudgetInfo.getBalanceAmount())),
                createBudgetInfoItem("Status", coaBudgetInfo.getStatus())
        );

        card.add(title, infoGrid);
        return card;
    }

    private VerticalLayout createBudgetInfoItem(String label, String value) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName("budget-info-item");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("info-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("info-value");

        item.add(labelSpan, valueSpan);
        return item;
    }

    private void createFormFields(FormLayout formLayout) {
        // Requested amount
        requestedAmountField = new NumberField("Requested Amount (UGX)");
        requestedAmountField.setRequired(true);
        requestedAmountField.setMin(1);
        requestedAmountField.setStep(1000);
        requestedAmountField.addClassName("requested-amount-field");

        // Purpose
        purposeArea = new TextArea("Purpose");
        purposeArea.setRequired(true);
        purposeArea.setMinLength(10);
        purposeArea.setMaxLength(1000);
        purposeArea.setPlaceholder("Describe the purpose of this acquisition...");
        purposeArea.addClassName("purpose-area");

        // Justification
        justificationArea = new TextArea("Justification");
        justificationArea.setMaxLength(2000);
        justificationArea.setPlaceholder("Provide detailed justification...");
        justificationArea.addClassName("justification-area");

        // Vendor information
        vendorNameField = new TextField("Vendor Name");
        vendorNameField.setPlaceholder("Enter vendor/supplier name");
        vendorNameField.addClassName("vendor-name-field");

        vendorAddressArea = new TextArea("Vendor Address");
        vendorAddressArea.setMaxLength(500);
        vendorAddressArea.setPlaceholder("Enter vendor address");
        vendorAddressArea.addClassName("vendor-address-area");

        vendorContactField = new TextField("Vendor Contact");
        vendorContactField.setPlaceholder("Phone/Email");
        vendorContactField.addClassName("vendor-contact-field");

        vendorTinField = new TextField("Vendor TIN");
        vendorTinField.setPlaceholder("Tax Identification Number");
        vendorTinField.addClassName("vendor-tin-field");

        // Delivery information
        deliveryLocationArea = new TextArea("Delivery Location");
        deliveryLocationArea.setMaxLength(500);
        deliveryLocationArea.setPlaceholder("Enter delivery location");
        deliveryLocationArea.addClassName("delivery-location-area");

        deliveryDatePicker = new DatePicker("Expected Delivery Date");
        deliveryDatePicker.setMin(LocalDate.now());
        deliveryDatePicker.addClassName("delivery-date-picker");

        paymentTermsField = new TextField("Payment Terms");
        paymentTermsField.setPlaceholder("e.g., 30 days after delivery");
        paymentTermsField.addClassName("payment-terms-field");

        warrantyPeriodField = new TextField("Warranty Period");
        warrantyPeriodField.setPlaceholder("e.g., 12 months");
        warrantyPeriodField.addClassName("warranty-period-field");

        // Procurement method
        /*        procurementMethodCombo = new ComboBox<>("Procurement Method");
        procurementMethodCombo.setItems(RequisitionType.values());
        procurementMethodCombo.setItemLabelGenerator(RequisitionData::getDisplayName);
        procurementMethodCombo.addClassName("procurement-method-combo");
        
        // Priority level
        priorityCombo = new ComboBox<>("Priority Level");
        priorityCombo.setItems(AcquisitionRequest.PriorityLevel.values());
        priorityCombo.setItemLabelGenerator(AcquisitionRequest.PriorityLevel::getDisplayName);
        priorityCombo.setValue(AcquisitionRequest.PriorityLevel.MEDIUM);
        priorityCombo.setRequired(true);
        priorityCombo.addClassName("priority-combo");*/
        // Technical specifications (for LPO)
        /*        if (acquisitionType == RequisitionData.AcquisitionType.LOCAL_PURCHASE_ORDER) {
        technicalSpecsArea = new TextArea("Technical Specifications");
        technicalSpecsArea.setMaxLength(2000);
        technicalSpecsArea.setPlaceholder("Enter detailed technical specifications...");
        technicalSpecsArea.addClassName("technical-specs-area");
        }*/

        // Notes
        notesArea = new TextArea("Additional Notes");
        notesArea.setMaxLength(1000);
        notesArea.setPlaceholder("Any additional notes...");
        notesArea.addClassName("notes-area");

        // Add fields to form layout
        formLayout.add(requestedAmountField, priorityCombo, procurementMethodCombo);
        formLayout.setColspan(purposeArea, 3);
        formLayout.add(purposeArea);
        formLayout.setColspan(justificationArea, 3);
        formLayout.add(justificationArea);

        formLayout.add(vendorNameField, vendorContactField, vendorTinField);
        formLayout.setColspan(vendorAddressArea, 3);
        formLayout.add(vendorAddressArea);

        formLayout.add(paymentTermsField, warrantyPeriodField, deliveryDatePicker);
        formLayout.setColspan(deliveryLocationArea, 3);
        formLayout.add(deliveryLocationArea);

        if (technicalSpecsArea != null) {
            formLayout.setColspan(technicalSpecsArea, 3);
            formLayout.add(technicalSpecsArea);
        }

        formLayout.setColspan(notesArea, 3);
        formLayout.add(notesArea);
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
        saveButton.addClickListener(e -> saveAcquisition(false));

        submitButton = new Button("Submit Request");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("submit-button");
        submitButton.addClickListener(e -> saveAcquisition(true));

        buttonLayout.add(cancelButton, saveButton, submitButton);
        return buttonLayout;
    }

    private void setupValidation() {
        binder = new Binder<>(AcquisitionRequest.class);

        // Requested amount validation
        binder.forField(requestedAmountField)
                .asRequired("Requested amount is required")
                .withValidator(new DoubleRangeValidator("Amount must be greater than 0", 1.0, Double.MAX_VALUE))
                .withValidator(amount -> amount <= coaBudgetInfo.getBalanceAmount(),
                        "Amount exceeds available balance of " + formatCurrency(coaBudgetInfo.getBalanceAmount()))
                .bind(AcquisitionRequest::getRequestedAmount, AcquisitionRequest::setRequestedAmount);

        // Purpose validation
        binder.forField(purposeArea)
                .asRequired("Purpose is required")
                .withValidator(new StringLengthValidator("Purpose must be between 10 and 1000 characters", 10, 1000))
                .bind(AcquisitionRequest::getPurpose, AcquisitionRequest::setPurpose);

        // Other field bindings
        binder.forField(justificationArea)
                .bind(AcquisitionRequest::getJustification, AcquisitionRequest::setJustification);

        binder.forField(vendorNameField)
                .bind(AcquisitionRequest::getVendorName, AcquisitionRequest::setVendorName);

        binder.forField(vendorAddressArea)
                .bind(AcquisitionRequest::getVendorAddress, AcquisitionRequest::setVendorAddress);

        binder.forField(vendorContactField)
                .bind(AcquisitionRequest::getVendorContact, AcquisitionRequest::setVendorContact);

        binder.forField(vendorTinField)
                .bind(AcquisitionRequest::getVendorTin, AcquisitionRequest::setVendorTin);

        binder.forField(deliveryLocationArea)
                .bind(AcquisitionRequest::getDeliveryLocation, AcquisitionRequest::setDeliveryLocation);

        binder.forField(deliveryDatePicker)
                .bind(AcquisitionRequest::getDeliveryDate, AcquisitionRequest::setDeliveryDate);

        binder.forField(paymentTermsField)
                .bind(AcquisitionRequest::getPaymentTerms, AcquisitionRequest::setPaymentTerms);

        binder.forField(warrantyPeriodField)
                .bind(AcquisitionRequest::getWarrantyPeriod, AcquisitionRequest::setWarrantyPeriod);
        /*
        binder.forField(procurementMethodCombo)
        .bind(AcquisitionRequest::getProcurementMethod, AcquisitionRequest::setProcurementMethod);
        
        binder.forField(priorityCombo)
        .asRequired("Priority level is required")
        .bind(AcquisitionRequest::getPriorityLevel, AcquisitionRequest::setPriorityLevel);*/

        if (technicalSpecsArea != null) {
            binder.forField(technicalSpecsArea)
                    .bind(AcquisitionRequest::getTechnicalSpecifications, AcquisitionRequest::setTechnicalSpecifications);
        }

        binder.forField(notesArea)
                .bind(AcquisitionRequest::getNotes, AcquisitionRequest::setNotes);
    }

    private void setupEventHandlers() {
        // Real-time validation feedback
        binder.addStatusChangeListener(e -> {
            boolean isValid = binder.isValid();
            saveButton.setEnabled(isValid);
            submitButton.setEnabled(isValid);
        });
    }

    private void saveAcquisition(boolean submit) {
        /*        try {
        if (currentAcquisition == null) {
        currentAcquisition = new RequisitionData();
        }
        
        // Validate and bind form data
        binder.writeBean(currentAcquisition);
        
        // Set additional fields
        //currentAcquisition.setAcquisitionType(acquisitionType);
        currentAcquisition.setBudget(selectedBudget);
        currentAcquisition.setOrganisation(selectedOrganisation);
        currentAcquisition.setCoa(selectedCOA);
        currentAcquisition.setSectionCode(selectedSection != null ? selectedSection.getANL_CODE() : null);
        currentAcquisition.setSectionName(selectedSection != null ? selectedSection.getNAME() : null);
        currentAcquisition.setCreatedBy(currentUser);
        
        // Set budget info
        currentAcquisition.setAvailableBudget(coaBudgetInfo.getBudgetTotal());
        currentAcquisition.setCommittedAmount(coaBudgetInfo.getCommittedAmount());
        currentAcquisition.setActualSpent(coaBudgetInfo.getActualSpent());
        currentAcquisition.setBalanceAmount(coaBudgetInfo.getBalanceAmount());
        
        // Save the acquisition
        RequisitionData savedAcquisition = acquisitionService.createAcquisition(currentAcquisition);
        
        if (submit) {
        savedAcquisition = acquisitionService.submitAcquisition(savedAcquisition.getId(), currentUser);
        showNotification("Acquisition submitted successfully. Number: " + savedAcquisition.getAcquisitionNumber(),
        NotificationVariant.LUMO_SUCCESS);
        } else {
        showNotification("Acquisition saved as draft. Number: " + savedAcquisition.getAcquisitionNumber(),
        NotificationVariant.LUMO_PRIMARY);
        }
        
        // Fire save event
        fireEvent(new SaveEvent(this, savedAcquisition));
        close();
        
        } catch (ValidationException e) {
        showNotification("Please correct the validation errors", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
        showNotification("Error saving acquisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }*/
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

    // Event handling
    public static class SaveEvent extends ComponentEvent<AcquisitionRequestForm> {

        private final AcquisitionRequest acquisition;

        public SaveEvent(AcquisitionRequestForm source, AcquisitionRequest acquisition) {
            super(source, false);
            this.acquisition = acquisition;
        }

        public AcquisitionRequest getAcquisition() {
            return acquisition;
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
