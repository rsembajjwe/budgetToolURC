package com.methaltech.application.views.budget;

import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetControlService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetControl;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
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
import com.vaadin.flow.component.progressbar.ProgressBar;
import java.math.BigDecimal;
import java.util.Optional;

public class BudgetControlPanel extends VerticalLayout {

    private final DepartmentBudget department;
    private final BudgetControlService controlService;
    private final Budget selectedBudget;
    private final User currentUser;

    public BudgetControlPanel(DepartmentBudget department, BudgetControlService controlService,
            Budget selectedBudget, User currentUser) {
        this.department = department;
        this.controlService = controlService;
        this.selectedBudget = selectedBudget;
        this.currentUser = currentUser;

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("budget-control-panel");

        createHeader();
        createControlsSection();
        createStatusSection();
        createQuickActions();
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);
        header.addClassName("control-panel-header");

        VerticalLayout headerText = new VerticalLayout();
        headerText.setSpacing(false);
        headerText.setPadding(false);

        H3 title = new H3("Budget Controls");
        title.addClassName("control-panel-title");

        Span subtitle = new Span("Advanced budget protection & monitoring");
        subtitle.addClassName("control-panel-subtitle");

        headerText.add(title, subtitle);

        Icon controlIcon = new Icon(VaadinIcon.SHIELD);
        controlIcon.addClassName("control-panel-icon");

        header.add(headerText, controlIcon);
        add(header);
    }

    private void createControlsSection() {
        Div controlsCard = new Div();
        controlsCard.addClassName("controls-card");

        VerticalLayout controlsContent = new VerticalLayout();
        controlsContent.setSpacing(true);
        controlsContent.setPadding(false);
        controlsContent.setWidthFull();
        controlsContent.addClassName("controls-content");

        // Budget Check Control
        HorizontalLayout budgetCheckControl = createEnhancedControlRow(
                "Budget Check",
                "Validates transactions against budget limits before processing",
                getCurrentBudgetCheckStatus(),
                VaadinIcon.CHECK_CIRCLE,
                "budget-check",
                "#3b82f6"
        );

        // Budget Stop Control
        HorizontalLayout budgetStopControl = createEnhancedControlRow(
                "Budget Stop",
                "Automatically prevents transactions when budget limits are exceeded",
                getCurrentBudgetStopStatus(),
                VaadinIcon.STOP,
                "budget-stop",
                "#ef4444"
        );

        // Posting Prohibition Control
        HorizontalLayout postingControl = createEnhancedControlRow(
                "Posting Prohibited",
                "Completely blocks all financial postings and transactions",
                getCurrentPostingProhibitedStatus(),
                VaadinIcon.BAN,
                "posting-prohibited",
                "#f59e0b"
        );

        controlsContent.add(budgetCheckControl, budgetStopControl, postingControl);
        controlsCard.add(controlsContent);
        add(controlsCard);
    }

    private HorizontalLayout createEnhancedControlRow(String title, String description, boolean enabled,
            VaadinIcon icon, String controlType, String accentColor) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setPadding(false);
        row.setSpacing(false);
        row.addClassName("control-row");

        // Left side - Icon and text
        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setSpacing(true);
        leftSide.setPadding(false);
        leftSide.setFlexGrow(1);

        // Enhanced icon container
        Div iconContainer = new Div();
        iconContainer.addClassName("control-icon");
        if (enabled) {
            iconContainer.addClassName("control-enabled");
        } else {
            iconContainer.addClassName("control-disabled");
        }
        iconContainer.getStyle().set("border-color", accentColor + "40");

        Icon controlIcon = new Icon(icon);
        controlIcon.getStyle().set("color", enabled ? accentColor : "#94a3b8");
        iconContainer.add(controlIcon);

        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);
        textContent.setFlexGrow(1);

        Span titleSpan = new Span(title);
        titleSpan.addClassName("control-title");

        Span descSpan = new Span(description);
        descSpan.addClassName("control-description");

        textContent.add(titleSpan, descSpan);
        leftSide.add(iconContainer, textContent);

        // Right side - Status and action
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);
        rightSide.addClassName("control-right-side");

        // Enhanced status indicator
        Div statusIndicator = new Div();
        statusIndicator.addClassName("control-status");
        if (enabled) {
            statusIndicator.addClassName("status-enabled");
            statusIndicator.setText("ACTIVE");
            statusIndicator.getStyle().set("border-color", accentColor + "40");
            statusIndicator.getStyle().set("color", accentColor);
        } else {
            statusIndicator.addClassName("status-disabled");
            statusIndicator.setText("INACTIVE");
        }

        // Enhanced toggle button
        Button toggleButton = new Button();
        toggleButton.addClassName("control-toggle");
        if (enabled) {
            toggleButton.setText("Disable");
            toggleButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            toggleButton.getElement().setAttribute("theme", "error small");
        } else {
            toggleButton.setText("Enable");
            toggleButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            toggleButton.getElement().setAttribute("theme", "success small");
        }
        if(!currentUser.getRoles().contains(Role.ADMIN)){
            toggleButton.setEnabled(false);
        }

        toggleButton.addClickListener(e -> {
            showEnhancedControlDialog(title, description, controlType, !enabled, accentColor);
        });

        rightSide.add(statusIndicator, toggleButton);
        row.add(leftSide, rightSide);

        return row;
    }

    private void createStatusSection() {
        Div statusCard = new Div();
        statusCard.addClassName("status-card");

        VerticalLayout statusContent = new VerticalLayout();
        statusContent.setSpacing(true);
        statusContent.setPadding(false);
        statusContent.setWidthFull();
        statusContent.addClassName("status-content");

        H4 statusTitle = new H4("Department Status Overview");
        statusTitle.addClassName("status-title");

        // Enhanced overall status
        HorizontalLayout overallStatus = new HorizontalLayout();
        overallStatus.setWidthFull();
        overallStatus.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        overallStatus.setAlignItems(FlexComponent.Alignment.CENTER);
        overallStatus.setPadding(false);
        overallStatus.setSpacing(false);
        overallStatus.addClassName("overall-status");

        HorizontalLayout statusLeft = new HorizontalLayout();
        statusLeft.setAlignItems(FlexComponent.Alignment.CENTER);
        statusLeft.setSpacing(true);
        statusLeft.setPadding(false);

        Icon statusIcon = getStatusIcon(department.getStatusText());
        statusIcon.addClassName("status-icon");
        statusIcon.getStyle().set("color", getStatusColor(department.getStatusText()));

        Span statusLabel = new Span("Department Status:");
        statusLabel.addClassName("status-label");

        statusLeft.add(statusIcon, statusLabel);

        Div statusBadge = new Div();
        statusBadge.addClassName("status-badge");
        statusBadge.addClassName(department.getStatusClass());
        statusBadge.setText(department.getStatusText());

        overallStatus.add(statusLeft, statusBadge);

        // Budget utilization info
        HorizontalLayout utilizationInfo = new HorizontalLayout();
        utilizationInfo.setWidthFull();
        utilizationInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        utilizationInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        utilizationInfo.setPadding(false);
        utilizationInfo.setSpacing(false);
        utilizationInfo.addClassName("utilization-info");

        Span utilizationLabel = new Span("Budget Utilization:");
        utilizationLabel.addClassName("status-label");

        Span utilizationValue = new Span(String.format("%.1f%%", department.getSpentPercentage()));
        utilizationValue.addClassName("utilization-value");
        utilizationValue.getStyle().set("color", getUtilizationColor(department.getSpentPercentage()));

        utilizationInfo.add(utilizationLabel, utilizationValue);

        statusContent.add(statusTitle, overallStatus, utilizationInfo);
        statusCard.add(statusContent);
        add(statusCard);
    }

    private void createQuickActions() {
        Div actionsCard = new Div();
        actionsCard.addClassName("quick-actions-card");

        VerticalLayout actionsContent = new VerticalLayout();
        actionsContent.setSpacing(true);
        actionsContent.setPadding(false);
        actionsContent.setWidthFull();

        H4 actionsTitle = new H4("Quick Actions");
        actionsTitle.addClassName("actions-title");

        HorizontalLayout actionsGrid = new HorizontalLayout();
        actionsGrid.setWidthFull();
        actionsGrid.setSpacing(true);
        actionsGrid.setPadding(false);
        actionsGrid.addClassName("quick-actions");

        Button viewTransactionsBtn = new Button("View Transactions", new Icon(VaadinIcon.LIST));
        viewTransactionsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);
        viewTransactionsBtn.addClassName("quick-action-button");
        viewTransactionsBtn.addClickListener(e -> {
            showNotification("Transaction view coming soon", NotificationVariant.LUMO_PRIMARY);
        });

        Button generateReportBtn = new Button("Generate Report", new Icon(VaadinIcon.FILE_TEXT));
        generateReportBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        generateReportBtn.addClassName("quick-action-button");
        generateReportBtn.addClickListener(e -> {
            showNotification("Report generation coming soon", NotificationVariant.LUMO_PRIMARY);
        });

        Button requestBudgetBtn = new Button("Request Budget", new Icon(VaadinIcon.PLUS));
        requestBudgetBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        requestBudgetBtn.addClassName("quick-action-button");
        requestBudgetBtn.addClickListener(e -> {
            showNotification("Budget request form coming soon", NotificationVariant.LUMO_PRIMARY);
        });

        actionsGrid.add(viewTransactionsBtn, generateReportBtn, requestBudgetBtn);
        actionsContent.add(actionsTitle, actionsGrid);
        actionsCard.add(actionsContent);
        add(actionsCard);
    }

    private Icon getStatusIcon(String status) {
        switch (status) {
            case "Over Budget":
                return new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
            case "Critical":
                return new Icon(VaadinIcon.WARNING);
            case "Near Limit":
                return new Icon(VaadinIcon.INFO_CIRCLE);
            default:
                return new Icon(VaadinIcon.CHECK_CIRCLE);
        }
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "Over Budget":
                return "#dc2626";
            case "Critical":
                return "#f97316";
            case "Near Limit":
                return "#f59e0b";
            default:
                return "#10b981";
        }
    }

    private String getUtilizationColor(BigDecimal percentage) {
        if (percentage.doubleValue() > 100) {
            return "#dc2626";
        }
        if (percentage.doubleValue() > 90) {
            return "#f97316";
        }
        if (percentage.doubleValue() > 75) {
            return "#f59e0b";
        }
        return "#10b981";
    }

    private boolean getCurrentBudgetCheckStatus() {
        if (controlService != null && selectedBudget != null) {
            return controlService.isBudgetCheckEnabled(selectedBudget, department.getDepartmentCode(), null);
        }
        return department.isBudgetCheckEnabled();
    }

    private boolean getCurrentBudgetStopStatus() {
        if (controlService != null && selectedBudget != null) {
            return controlService.isBudgetStopEnabled(selectedBudget, department.getDepartmentCode(), null);
        }
        return department.isBudgetStopEnabled();
    }

    private boolean getCurrentPostingProhibitedStatus() {
        if (controlService != null && selectedBudget != null) {
            return controlService.isPostingProhibited(selectedBudget, department.getDepartmentCode(), null);
        }
        return department.isPostingProhibited();
    }

    private void showEnhancedControlDialog(String title, String description, String controlType,
            boolean newValue, String accentColor) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("600px");
        confirmDialog.setModal(true);
        confirmDialog.addClassName("control-confirmation-dialog");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);
        content.addClassName("confirmation-content");

        // Enhanced header
        HorizontalLayout dialogHeader = new HorizontalLayout();
        dialogHeader.setWidthFull();
        dialogHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        dialogHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogHeader.addClassName("confirmation-header");

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setSpacing(false);
        titleSection.setPadding(false);

        H3 dialogTitle = new H3((newValue ? "Enable" : "Disable") + " " + title);
        dialogTitle.addClassName("confirmation-title");

        Span dialogSubtitle = new Span("Department: " + department.getDepartmentName());
        dialogSubtitle.addClassName("confirmation-subtitle");

        titleSection.add(dialogTitle, dialogSubtitle);

        Icon dialogIcon = new Icon(getControlIcon(controlType));
        dialogIcon.addClassName("confirmation-icon");
        dialogIcon.getStyle().set("color", accentColor);

        dialogHeader.add(titleSection, dialogIcon);

        // Enhanced message section
        Div messageCard = new Div();
        messageCard.addClassName("confirmation-message-card");

        Span message = new Span(description);
        message.addClassName("confirmation-message");

        Span actionMessage = new Span("Are you sure you want to " + (newValue ? "enable" : "disable")
                + " this control? This will affect all transactions for this department.");
        actionMessage.addClassName("confirmation-action-message");

        Div warningCard = new Div();
        warningCard.addClassName("confirmation-warning");

        HorizontalLayout warningContent = new HorizontalLayout();
        warningContent.setAlignItems(FlexComponent.Alignment.CENTER);
        warningContent.setSpacing(true);
        warningContent.setPadding(false);

        Icon warningIcon = new Icon(VaadinIcon.WARNING);
        warningIcon.addClassName("warning-icon");

        Span warningText = new Span("This action will take effect immediately and impact all future transactions.");
        warningText.addClassName("warning-text");

        warningContent.add(warningIcon, warningText);
        warningCard.add(warningContent);

        messageCard.add(message, actionMessage, warningCard);

        // Enhanced buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setSpacing(true);
        buttons.addClassName("confirmation-buttons");

        Button cancelButton = new Button("Cancel", new Icon(VaadinIcon.CLOSE));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("cancel-button");
        cancelButton.addClickListener(e -> confirmDialog.close());

        Button confirmButton = new Button((newValue ? "Enable" : "Disable"),
                new Icon(newValue ? VaadinIcon.CHECK : VaadinIcon.CLOSE));
        confirmButton.addThemeVariants(newValue ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_ERROR);
        confirmButton.addClassName("confirm-button");
        confirmButton.addClickListener(e -> {
            try {
                updateDepartmentControl(controlType, newValue);
                confirmDialog.close();
                String action = newValue ? "enabled" : "disabled";
                showNotification(title + " has been " + action + " for " + department.getDepartmentName(),
                        newValue ? NotificationVariant.LUMO_SUCCESS : NotificationVariant.LUMO_CONTRAST);
                refreshControlsSection();
            } catch (Exception ex) {
                showNotification("Error updating control: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        buttons.add(cancelButton, confirmButton);
        content.add(dialogHeader, messageCard, buttons);
        confirmDialog.add(content);
        confirmDialog.open();
    }

    private VaadinIcon getControlIcon(String controlType) {
        switch (controlType) {
            case "budget-check":
                return VaadinIcon.CHECK_CIRCLE;
            case "budget-stop":
                return VaadinIcon.STOP;
            case "posting-prohibited":
                return VaadinIcon.BAN;
            default:
                return VaadinIcon.COG;
        }
    }

    private void updateDepartmentControl(String controlType, boolean enabled) {
        if (controlService == null || selectedBudget == null) {
            throw new RuntimeException("Control service not available");
        }

        // Get current control or create new one
        Optional<BudgetControl> existingControl = controlService.getDepartmentControl(
                selectedBudget, department.getDepartmentCode());

        boolean currentBudgetCheck = existingControl.map(BudgetControl::getBudgetCheckEnabled).orElse(false);
        boolean currentBudgetStop = existingControl.map(BudgetControl::getBudgetStopEnabled).orElse(false);
        boolean currentPostingProhibited = existingControl.map(BudgetControl::getPostingProhibited).orElse(false);

        // Update the specific control
        switch (controlType) {
            case "budget-check":
                currentBudgetCheck = enabled;
                break;
            case "budget-stop":
                currentBudgetStop = enabled;
                break;
            case "posting-prohibited":
                currentPostingProhibited = enabled;
                break;
        }

        String reason = (enabled ? "Enabled" : "Disabled") + " " + getControlDisplayName(controlType)
                + " for department " + department.getDepartmentName() + " via BLO Dashboard";

        controlService.setDepartmentLevelControl(selectedBudget, department.getDepartmentCode(),
                department.getDepartmentName(), currentBudgetCheck, currentBudgetStop,
                currentPostingProhibited, reason, currentUser.getLastName()+" "+currentUser.getFirstName());
    }

    private String getControlDisplayName(String controlType) {
        switch (controlType) {
            case "budget-check":
                return "Budget Check";
            case "budget-stop":
                return "Budget Stop";
            case "posting-prohibited":
                return "Posting Prohibition";
            default:
                return "Budget Control";
        }
    }

    private void refreshControlsSection() {
        // Remove and recreate the controls section
        removeAll();
        createHeader();
        createControlsSection();
        createStatusSection();
        createQuickActions();
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
