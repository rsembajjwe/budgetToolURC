
package com.methaltech.application.views.budgetcontrol;

import com.methaltech.application.data.bgtool.service.BudgetControlService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetControl;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.security.AuthenticatedUser;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Optional;

public class BudgetControlManagementDialog extends Dialog {
    
    private final BudgetControlService controlService;
    private final Budget selectedBudget;
    private final String currentUser;
    
    // UI Components
    private ComboBox<BudgetControl.ControlLevel> controlLevelCombo;
    private ComboBox<DepartmentBudget> departmentCombo;
    private TextField sectionCodeField;
    private TextField sectionNameField;
    private Checkbox budgetCheckBox;
    private Checkbox budgetStopBox;
    private Checkbox postingProhibitedBox;
    private TextArea reasonArea;
    private Button saveButton;
    private Button cancelButton;
    private Div previewCard;
    
    // Current control being edited
    private BudgetControl currentControl;
    private List<DepartmentBudget> departments;

    public BudgetControlManagementDialog(BudgetControlService controlService, Budget selectedBudget, 
                                        List<DepartmentBudget> departments, String currentUser) {
        this.controlService = controlService;
        this.selectedBudget = selectedBudget;
        this.departments = departments;
        this.currentUser = currentUser;
        
        setWidth("900px");
        setHeight("700px");
        setModal(true);
        setDraggable(true);
        setResizable(true);
        addClassName("budget-control-dialog");
        
        createDialogContent();
        setupEventHandlers();
    }

    private void createDialogContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("control-dialog-layout");

        // Header
        createHeader(layout);
        
        // Form
        createForm(layout);
        
        // Control preview
        createControlPreview(layout);
        
        // Action buttons
        createActionButtons(layout);

        add(layout);
    }

    private void createHeader(VerticalLayout layout) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.addClassName("control-dialog-header");

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setSpacing(false);
        titleSection.setPadding(false);

        H2 title = new H2("Budget Control Configuration");
        title.addClassName("dialog-title");

        Span subtitle = new Span("Configure advanced budget controls for " + selectedBudget.getFinancialYear());
        subtitle.addClassName("dialog-subtitle");

        titleSection.add(title, subtitle);

        Div iconContainer = new Div();
        iconContainer.addClassName("dialog-icon-container");
        
        Icon controlIcon = new Icon(VaadinIcon.SHIELD);
        controlIcon.addClassName("dialog-icon");
        iconContainer.add(controlIcon);

        header.add(titleSection, iconContainer);
        layout.add(header);
    }

    private void createForm(VerticalLayout layout) {
        Div formCard = new Div();
        formCard.addClassName("control-form-card");

        H3 formTitle = new H3("Control Settings");
        formTitle.addClassName("form-section-title");

        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("control-form");
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 2)
        );

        // Control level selection
        controlLevelCombo = new ComboBox<>("Control Level");
        controlLevelCombo.setItems(BudgetControl.ControlLevel.values());
        controlLevelCombo.setItemLabelGenerator(BudgetControl.ControlLevel::getDisplayName);
        controlLevelCombo.setRequired(true);
        controlLevelCombo.addClassName("control-level-combo");
        controlLevelCombo.addValueChangeListener(e -> {
            updateFormFields(e.getValue());
            updatePreview();
        });

        // Department selection
        departmentCombo = new ComboBox<>("Department");
        departmentCombo.setItems(departments);
        departmentCombo.setItemLabelGenerator(dept -> dept.getDepartmentName() + " (" + dept.getDepartmentCode() + ")");
        departmentCombo.addClassName("department-combo");
        departmentCombo.addValueChangeListener(e -> {
            loadExistingControl();
            updatePreview();
        });

        // Section fields
        sectionCodeField = new TextField("Section Code");
        sectionCodeField.setPlaceholder("Enter section code");
        sectionCodeField.addClassName("section-code-field");
        sectionCodeField.addValueChangeListener(e -> {
            loadExistingControl();
            updatePreview();
        });

        sectionNameField = new TextField("Section Name");
        sectionNameField.setPlaceholder("Enter section name");
        sectionNameField.addClassName("section-name-field");

        // Control type checkboxes with enhanced styling
        VerticalLayout controlTypesSection = new VerticalLayout();
        controlTypesSection.setSpacing(true);
        controlTypesSection.setPadding(false);
        controlTypesSection.addClassName("control-types-section");

        H4 controlTypesTitle = new H4("Control Types");
        controlTypesTitle.addClassName("control-types-title");

        budgetCheckBox = new Checkbox("Budget Check");
        budgetCheckBox.addClassName("control-checkbox");
        budgetCheckBox.addClassName("budget-check-checkbox");
        budgetCheckBox.addValueChangeListener(e -> updatePreview());

        budgetStopBox = new Checkbox("Budget Stop");
        budgetStopBox.addClassName("control-checkbox");
        budgetStopBox.addClassName("budget-stop-checkbox");
        budgetStopBox.addValueChangeListener(e -> updatePreview());

        postingProhibitedBox = new Checkbox("Posting Prohibited");
        postingProhibitedBox.addClassName("control-checkbox");
        postingProhibitedBox.addClassName("posting-prohibited-checkbox");
        postingProhibitedBox.addValueChangeListener(e -> updatePreview());

        controlTypesSection.add(controlTypesTitle, budgetCheckBox, budgetStopBox, postingProhibitedBox);

        // Reason field
        reasonArea = new TextArea("Reason");
        reasonArea.setPlaceholder("Enter reason for these control settings...");
        reasonArea.setMaxLength(500);
        reasonArea.addClassName("reason-area");

        // Add fields to form
        formLayout.add(controlLevelCombo, departmentCombo);
        formLayout.add(sectionCodeField, sectionNameField);
        formLayout.setColspan(controlTypesSection, 2);
        formLayout.add(controlTypesSection);
        formLayout.setColspan(reasonArea, 2);
        formLayout.add(reasonArea);

        // Initially hide optional fields
        updateFormFields(null);

        formCard.add(formTitle, formLayout);
        layout.add(formCard);
    }

    private void createControlPreview(VerticalLayout layout) {
        previewCard = new Div();
        previewCard.addClassName("control-preview-card");

        H3 previewTitle = new H3("Control Preview");
        previewTitle.addClassName("preview-title");

        VerticalLayout previewContent = new VerticalLayout();
        previewContent.setSpacing(true);
        previewContent.setPadding(false);
        previewContent.addClassName("preview-content");

        Span previewText = new Span("Select control options to see preview");
        previewText.addClassName("preview-text");

        previewContent.add(previewText);
        previewCard.add(previewTitle, previewContent);
        layout.add(previewCard);
    }

    private void updateFormFields(BudgetControl.ControlLevel controlLevel) {
        // Hide all optional fields first
        departmentCombo.setVisible(false);
        sectionCodeField.setVisible(false);
        sectionNameField.setVisible(false);

        if (controlLevel != null) {
            switch (controlLevel) {
                case BUDGET:
                    // No additional fields needed for budget level
                    break;
                case DEPARTMENT:
                    departmentCombo.setVisible(true);
                    departmentCombo.setRequired(true);
                    break;
                case SECTION:
                    departmentCombo.setVisible(true);
                    sectionCodeField.setVisible(true);
                    sectionNameField.setVisible(true);
                    departmentCombo.setRequired(true);
                    sectionCodeField.setRequired(true);
                    sectionNameField.setRequired(true);
                    break;
            }
        }
    }

    private void loadExistingControl() {
        BudgetControl.ControlLevel level = controlLevelCombo.getValue();
        DepartmentBudget department = departmentCombo.getValue();
        
        if (level == null) return;
        
        try {
            Optional<BudgetControl> existingControl = Optional.empty();
            
            switch (level) {
                case BUDGET:
                    existingControl = controlService.getBudgetLevelControl(selectedBudget);
                    break;
                case DEPARTMENT:
                    if (department != null) {
                        existingControl = controlService.getDepartmentControl(selectedBudget, department.getDepartmentCode());
                    }
                    break;
                case SECTION:
                    if (department != null && sectionCodeField.getValue() != null && !sectionCodeField.getValue().trim().isEmpty()) {
                        existingControl = controlService.getSectionControl(selectedBudget, 
                            department.getDepartmentCode(), sectionCodeField.getValue());
                    }
                    break;
            }
            
            if (existingControl.isPresent()) {
                BudgetControl control = existingControl.get();
                budgetCheckBox.setValue(control.getBudgetCheckEnabled());
                budgetStopBox.setValue(control.getBudgetStopEnabled());
                postingProhibitedBox.setValue(control.getPostingProhibited());
                reasonArea.setValue(control.getReason() != null ? control.getReason() : "");
                currentControl = control;
            } else {
                // Clear form for new control
                budgetCheckBox.setValue(false);
                budgetStopBox.setValue(false);
                postingProhibitedBox.setValue(false);
                reasonArea.setValue("");
                currentControl = null;
            }
        } catch (Exception e) {
            showNotification("Error loading existing control: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void updatePreview() {
        if (previewCard == null) return;
        
        // Clear existing preview content
        previewCard.removeAll();
        
        H3 previewTitle = new H3("Control Preview");
        previewTitle.addClassName("preview-title");
        
        VerticalLayout previewContent = new VerticalLayout();
        previewContent.setSpacing(true);
        previewContent.setPadding(false);
        previewContent.addClassName("preview-content");
        
        BudgetControl.ControlLevel level = controlLevelCombo.getValue();
        DepartmentBudget dept = departmentCombo.getValue();
        String sectionCode = sectionCodeField.getValue();
        
        if (level != null) {
            // Show scope
            Div scopeInfo = new Div();
            scopeInfo.addClassName("preview-scope");
            
            String scopeText = "Scope: ";
            switch (level) {
                case BUDGET:
                    scopeText += "Entire Budget (" + selectedBudget.getFinancialYear() + ")";
                    break;
                case DEPARTMENT:
                    scopeText += dept != null ? "Department: " + dept.getDepartmentName() : "Department Level";
                    break;
                case SECTION:
                    scopeText += dept != null && sectionCode != null ? 
                        "Section: " + sectionCode + " in " + dept.getDepartmentName() : "Section Level";
                    break;
            }
            
            Span scopeSpan = new Span(scopeText);
            scopeSpan.addClassName("preview-scope-text");
            scopeInfo.add(scopeSpan);
            
            // Show active controls
            VerticalLayout activeControls = new VerticalLayout();
            activeControls.setSpacing(false);
            activeControls.setPadding(false);
            activeControls.addClassName("preview-controls");
            
            if (budgetCheckBox.getValue()) {
                HorizontalLayout checkControl = createPreviewControlItem("Budget Check", 
                    "Validates transactions against budget limits", VaadinIcon.CHECK_CIRCLE, "#3b82f6");
                activeControls.add(checkControl);
            }
            
            if (budgetStopBox.getValue()) {
                HorizontalLayout stopControl = createPreviewControlItem("Budget Stop", 
                    "Prevents transactions exceeding budget", VaadinIcon.STOP, "#ef4444");
                activeControls.add(stopControl);
            }
            
            if (postingProhibitedBox.getValue()) {
                HorizontalLayout prohibitedControl = createPreviewControlItem("Posting Prohibited", 
                    "Blocks all financial postings", VaadinIcon.BAN, "#f59e0b");
                activeControls.add(prohibitedControl);
            }
            
            if (!budgetCheckBox.getValue() && !budgetStopBox.getValue() && !postingProhibitedBox.getValue()) {
                Span noControlsText = new Span("No controls will be active");
                noControlsText.addClassName("preview-no-controls");
                activeControls.add(noControlsText);
            }
            
            previewContent.add(scopeInfo, activeControls);
        } else {
            Span selectLevelText = new Span("Select a control level to see preview");
            selectLevelText.addClassName("preview-placeholder");
            previewContent.add(selectLevelText);
        }
        
        previewCard.add(previewTitle, previewContent);
    }

    private HorizontalLayout createPreviewControlItem(String name, String description, VaadinIcon iconType, String color) {
        HorizontalLayout item = new HorizontalLayout();
        item.setAlignItems(FlexComponent.Alignment.CENTER);
        item.setSpacing(true);
        item.setPadding(false);
        item.addClassName("preview-control-item");
        
        Div iconContainer = new Div();
        iconContainer.addClassName("preview-control-icon");
        iconContainer.getStyle().set("background-color", color + "20");
        iconContainer.getStyle().set("border", "1px solid " + color + "40");
        
        Icon icon = new Icon(iconType);
        icon.getStyle().set("color", color);
        iconContainer.add(icon);
        
        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);
        
        Span nameSpan = new Span(name);
        nameSpan.addClassName("preview-control-name");
        nameSpan.getStyle().set("color", color);
        
        Span descSpan = new Span(description);
        descSpan.addClassName("preview-control-desc");
        
        textContent.add(nameSpan, descSpan);
        item.add(iconContainer, textContent);
        
        return item;
    }

    private void setupEventHandlers() {
        // Update preview when any field changes
        budgetCheckBox.addValueChangeListener(e -> updatePreview());
        budgetStopBox.addValueChangeListener(e -> updatePreview());
        postingProhibitedBox.addValueChangeListener(e -> updatePreview());
        departmentCombo.addValueChangeListener(e -> {
            loadExistingControl();
            updatePreview();
        });
        sectionCodeField.addValueChangeListener(e -> {
            loadExistingControl();
            updatePreview();
        });
    }

    private void createActionButtons(VerticalLayout layout) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName("control-dialog-buttons");

        cancelButton = new Button("Cancel", new Icon(VaadinIcon.CLOSE));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("cancel-button");
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("Save Controls", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClassName("save-button");
        saveButton.addClickListener(e -> saveControls());

        buttonLayout.add(cancelButton, saveButton);
        layout.add(buttonLayout);
    }

    private void saveControls() {
        try {
            BudgetControl.ControlLevel level = controlLevelCombo.getValue();
            if (level == null) {
                showNotification("Please select a control level", NotificationVariant.LUMO_ERROR);
                return;
            }

            boolean budgetCheck = budgetCheckBox.getValue();
            boolean budgetStop = budgetStopBox.getValue();
            boolean postingProhibited = postingProhibitedBox.getValue();
            String reason = reasonArea.getValue();

            BudgetControl savedControl = null;

            switch (level) {
                case BUDGET:
                    savedControl = controlService.setBudgetLevelControl(selectedBudget, budgetCheck, 
                        budgetStop, postingProhibited, reason, currentUser);
                    break;
                case DEPARTMENT:
                    DepartmentBudget department = departmentCombo.getValue();
                    if (department == null) {
                        showNotification("Please select a department", NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    savedControl = controlService.setDepartmentLevelControl(selectedBudget, 
                        department.getDepartmentCode(), department.getDepartmentName(),
                        budgetCheck, budgetStop, postingProhibited, reason, currentUser);
                    break;
                case SECTION:
                    DepartmentBudget deptForSection = departmentCombo.getValue();
                    String sectionCode = sectionCodeField.getValue();
                    String sectionName = sectionNameField.getValue();
                    
                    if (deptForSection == null || sectionCode == null || sectionCode.trim().isEmpty()) {
                        showNotification("Please fill in all required section fields", NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    
                    savedControl = controlService.setSectionLevelControl(selectedBudget,
                        deptForSection.getDepartmentCode(), deptForSection.getDepartmentName(),
                        sectionCode, sectionName, budgetCheck, budgetStop, postingProhibited, reason, currentUser);
                    break;
            }

            if (savedControl != null) {
                showNotification("Budget controls configured successfully", NotificationVariant.LUMO_SUCCESS);
                fireEvent(new SaveEvent(this, savedControl));
                close();
            }

        } catch (Exception e) {
            showNotification("Error saving controls: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    public void setControl(BudgetControl control) {
        this.currentControl = control;
        if (control != null) {
            controlLevelCombo.setValue(control.getControlLevel());
            
            if (control.getDepartmentCode() != null) {
                DepartmentBudget dept = departments.stream()
                    .filter(d -> d.getDepartmentCode().equals(control.getDepartmentCode()))
                    .findFirst()
                    .orElse(null);
                departmentCombo.setValue(dept);
            }
            
            if (control.getSectionCode() != null) {
                sectionCodeField.setValue(control.getSectionCode());
            }
            
            if (control.getSectionName() != null) {
                sectionNameField.setValue(control.getSectionName());
            }
            
            budgetCheckBox.setValue(control.getBudgetCheckEnabled());
            budgetStopBox.setValue(control.getBudgetStopEnabled());
            postingProhibitedBox.setValue(control.getPostingProhibited());
            
            if (control.getReason() != null) {
                reasonArea.setValue(control.getReason());
            }
            
            updateFormFields(control.getControlLevel());
            updatePreview();
        }
    }

    // Event handling
    public static class SaveEvent extends ComponentEvent<BudgetControlManagementDialog> {
        private final BudgetControl control;

        public SaveEvent(BudgetControlManagementDialog source, BudgetControl control) {
            super(source, false);
            this.control = control;
        }

        public BudgetControl getControl() {
            return control;
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
