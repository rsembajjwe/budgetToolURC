package com.methaltech.application.views.approvals;

import com.methaltech.application.data.bgtool.service.BudgetApprovalsService;
import com.methaltech.application.data.entity.bgtool.BudgetApprovals;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ApprovalWorkflowGrid extends VerticalLayout {

    private final Grid<BudgetApprovals> grid;
    private final BudgetApprovalsService approvalsService;
    private final String currentUserRole;
    private final NumberFormat currencyFormat;

    public ApprovalWorkflowGrid(List<BudgetApprovals> approvals, String currentUserRole, BudgetApprovalsService approvalsService) {
        this.approvalsService = approvalsService;
        this.currentUserRole = currentUserRole;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("approval-workflow-grid");

        grid = new Grid<>(BudgetApprovals.class, false);
        setupGrid();
        grid.setItems(approvals);

        add(grid);
    }

    private void setupGrid() {
        grid.addClassName("approval-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setWidthFull();
        grid.setHeight("600px");

        // Request ID column
        grid.addColumn(BudgetApprovals::getRequestId)
                .setHeader("Request ID")
                .setWidth("150px")
                .setFlexGrow(0)
                .setSortable(true);

        // Department column
        grid.addColumn(new ComponentRenderer<>(this::createDepartmentInfo))
                .setHeader("Department")
                .setWidth("200px")
                .setFlexGrow(0);

        // Request type and amount
        grid.addColumn(new ComponentRenderer<>(this::createRequestInfo))
                .setHeader("Request Details")
                .setWidth("250px")
                .setFlexGrow(1);

        // Current stage
        grid.addColumn(new ComponentRenderer<>(this::createStageInfo))
                .setHeader("Current Stage")
                .setWidth("180px")
                .setFlexGrow(0);

        // Status
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("120px")
                .setFlexGrow(0);

        // Priority
        grid.addColumn(new ComponentRenderer<>(this::createPriorityBadge))
                .setHeader("Priority")
                .setWidth("100px")
                .setFlexGrow(0);

        // Requested date
        grid.addColumn(new ComponentRenderer<>(approval -> {
            Span dateSpan = new Span(approval.getRequestedDate()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            return dateSpan;
        }));

        // Days in stage
        grid.addColumn(new ComponentRenderer<>(this::createDaysInStage))
                .setHeader("Days")
                .setWidth("80px")
                .setFlexGrow(0);

        // Actions column
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setHeader("Actions")
                .setWidth("200px")
                .setFlexGrow(0);

        // Row click handler for details
        grid.addItemClickListener(e -> showApprovalDetails(e.getItem()));
    }

    private Div createDepartmentInfo(BudgetApprovals approval) {
        Div container = new Div();
        container.addClassName("department-info");

        Span deptName = new Span(approval.getDepartmentName());
        deptName.addClassName("dept-name");

        Span deptCode = new Span(approval.getDepartmentCode());
        deptCode.addClassName("dept-code");

        if (approval.getSectionName() != null && !approval.getSectionName().isEmpty()) {
            Span sectionName = new Span(approval.getSectionName());
            sectionName.addClassName("section-name");
            container.add(deptName, deptCode, sectionName);
        } else {
            container.add(deptName, deptCode);
        }

        return container;
    }

    private Div createRequestInfo(BudgetApprovals approval) {
        Div container = new Div();
        container.addClassName("request-info");

        Span requestType = new Span(approval.getRequestType().getDisplayName());
        requestType.addClassName("request-type");

        Span amount = new Span(formatCurrency(approval.getRequestedAmount()));
        amount.addClassName("request-amount");

        container.add(requestType, amount);
        return container;
    }

    private Div createStageInfo(BudgetApprovals approval) {
        Div container = new Div();
        container.addClassName("stage-info");

        HorizontalLayout stageLayout = new HorizontalLayout();
        stageLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        stageLayout.setSpacing(false);
        stageLayout.setPadding(false);

        Div stageIndicator = new Div();
        stageIndicator.addClassName("stage-indicator");
        stageIndicator.getStyle().set("background-color", approval.getStageColor());

        Span stageName = new Span(approval.getCurrentStage().getDisplayName());
        stageName.addClassName("stage-name");

        stageLayout.add(stageIndicator, stageName);
        container.add(stageLayout);

        return container;
    }

    private Div createStatusBadge(BudgetApprovals approval) {
        Div badge = new Div();
        badge.addClassName("status-badge");
        badge.addClassName("status-" + approval.getOverallStatus().name().toLowerCase());
        badge.getStyle().set("background-color", approval.getStatusColor() + "20");
        badge.getStyle().set("color", approval.getStatusColor());
        badge.getStyle().set("border", "1px solid " + approval.getStatusColor() + "40");

        Span statusText = new Span(approval.getOverallStatus().getDisplayName());
        badge.add(statusText);

        return badge;
    }

    private Div createPriorityBadge(BudgetApprovals approval) {
        Div badge = new Div();
        badge.addClassName("priority-badge");
        badge.addClassName("priority-" + approval.getPriorityLevel().name().toLowerCase());
        badge.getStyle().set("background-color", approval.getPriorityColor() + "20");
        badge.getStyle().set("color", approval.getPriorityColor());
        badge.getStyle().set("border", "1px solid " + approval.getPriorityColor() + "40");

        Span priorityText = new Span(approval.getPriorityLevel().getDisplayName());
        badge.add(priorityText);

        return badge;
    }

    private Div createDaysInStage(BudgetApprovals approval) {
        Div container = new Div();
        container.addClassName("days-in-stage");

        int days = approval.getDaysInCurrentStage();
        Span daysText = new Span(String.valueOf(days));
        daysText.addClassName("days-number");

        if (days > 7) {
            daysText.addClassName("overdue");
        } else if (days > 5) {
            daysText.addClassName("warning");
        }

        container.add(daysText);
        return container;
    }

    private HorizontalLayout createActionButtons(BudgetApprovals approval) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.setPadding(false);
        actions.addClassName("action-buttons");

        // View details button
        Button viewButton = new Button(new Icon(VaadinIcon.EYE));
        viewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        viewButton.addClassName("view-button");
        viewButton.addClickListener(e -> showApprovalDetails(approval));

        actions.add(viewButton);

        // Show approve/reject buttons only if user can act on this approval
        if (approval.canBeApprovedBy(currentUserRole)
                && approval.getOverallStatus() == BudgetApprovals.ApprovalStatus.IN_PROGRESS) {

            Button approveButton = new Button(new Icon(VaadinIcon.CHECK));
            approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            approveButton.addClassName("approve-button");
            approveButton.addClickListener(e -> showApprovalDialog(approval, true));

            Button rejectButton = new Button(new Icon(VaadinIcon.CLOSE));
            rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            rejectButton.addClassName("reject-button");
            rejectButton.addClickListener(e -> showApprovalDialog(approval, false));

            actions.add(approveButton, rejectButton);
        }

        return actions;
    }

    private void showApprovalDetails(BudgetApprovals approval) {
        Dialog detailsDialog = new Dialog();
        detailsDialog.setWidth("800px");
        detailsDialog.setHeight("600px");
        detailsDialog.setModal(true);
        detailsDialog.addClassName("approval-details-dialog");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        H3 title = new H3("Approval Request Details");
        title.addClassName("details-title");

        // Create detailed view of the approval
        Div detailsContent = createApprovalDetailsContent(approval);

        Button closeButton = new Button("Close");
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.addClickListener(e -> detailsDialog.close());

        content.add(title, detailsContent, closeButton);
        detailsDialog.add(content);
        detailsDialog.open();
    }

    private Div createApprovalDetailsContent(BudgetApprovals approval) {
        Div content = new Div();
        content.addClassName("approval-details-content");

        // Add detailed information about the approval
        // This would include all fields, approval history, comments, etc.
        return content;
    }

    private void showApprovalDialog(BudgetApprovals approval, boolean isApproval) {
        Dialog actionDialog = new Dialog();
        actionDialog.setWidth("500px");
        actionDialog.setModal(true);
        actionDialog.addClassName("approval-action-dialog");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);

        String action = isApproval ? "Approve" : "Reject";
        H3 title = new H3(action + " Request");
        title.addClassName("action-title");

        Span message = new Span("Are you sure you want to " + action.toLowerCase() + " this budget request?");
        message.addClassName("action-message");

        TextArea commentsArea = new TextArea("Comments");
        commentsArea.setPlaceholder("Add your comments...");
        commentsArea.setWidthFull();
        commentsArea.addClassName("comments-area");

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setSpacing(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> actionDialog.close());

        Button confirmButton = new Button(action);
        confirmButton.addThemeVariants(isApproval ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_ERROR);
        confirmButton.addClickListener(e -> {
            try {
                String comments = commentsArea.getValue();
                if (isApproval) {
                    approvalsService.approveRequest(approval.getId(), currentUserRole, "Current User", comments);
                    showNotification("Request approved successfully", NotificationVariant.LUMO_SUCCESS);
                } else {
                    approvalsService.rejectRequest(approval.getId(), currentUserRole, "Current User", comments);
                    showNotification("Request rejected", NotificationVariant.LUMO_ERROR);
                }
                actionDialog.close();
                refreshGrid();
            } catch (Exception ex) {
                showNotification("Error processing request: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        buttons.add(cancelButton, confirmButton);
        content.add(title, message, commentsArea, buttons);
        actionDialog.add(content);
        actionDialog.open();
    }

    private void refreshGrid() {
        // Refresh the grid data
        // This would typically reload the data from the service
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

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }
}
