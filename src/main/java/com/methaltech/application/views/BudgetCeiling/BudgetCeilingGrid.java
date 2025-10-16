package com.methaltech.application.views.BudgetCeiling;

import com.methaltech.application.data.bgtool.service.BudgetCeilingService;
import com.methaltech.application.data.entity.bgtool.BudgetCeiling;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BudgetCeilingGrid extends VerticalLayout {

    private final Grid<BudgetCeiling> grid;
    private final BudgetCeilingService ceilingService;
    private final String currentUserRole;
    private final NumberFormat currencyFormat;

    public BudgetCeilingGrid(List<BudgetCeiling> ceilings, String currentUserRole, BudgetCeilingService ceilingService) {
        this.ceilingService = ceilingService;
        this.currentUserRole = currentUserRole;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("budget-ceiling-grid");

        grid = new Grid<>(BudgetCeiling.class, false);
        setupGrid();
        grid.setItems(ceilings);

        add(grid);
    }

    private void setupGrid() {
        grid.addClassName("ceiling-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setWidthFull();
        grid.setHeight("600px");

        // Ceiling type column
        grid.addColumn(new ComponentRenderer<>(this::createCeilingTypeInfo))
                .setHeader("Ceiling Type")
                .setWidth("150px")
                .setFlexGrow(0);

        // Hierarchy path column
        grid.addColumn(new ComponentRenderer<>(this::createHierarchyPath))
                .setHeader("Hierarchy Path")
                .setWidth("300px")
                .setFlexGrow(1);

        // Ceiling amount column
        grid.addColumn(new ComponentRenderer<>(this::createAmountInfo))
                .setHeader("Ceiling Amount")
                .setWidth("200px")
                .setFlexGrow(0);

        // Utilization column
        grid.addColumn(new ComponentRenderer<>(this::createUtilizationInfo))
                .setHeader("Utilization")
                .setWidth("200px")
                .setFlexGrow(0);

        // Status column
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setWidth("120px")
                .setFlexGrow(0);

        // Approval status column
        grid.addColumn(new ComponentRenderer<>(this::createApprovalStatusBadge))
                .setHeader("Approval")
                .setWidth("120px")
                .setFlexGrow(0);

        // Effective date column
        /*        grid.addColumn(new LocalDateTimeRenderer<BudgetCeiling>(
        BudgetCeiling::getEffectiveDate,
        DateTimeFormatter.ofPattern("MMM dd, yyyy"),
        "N/A"
        ))
        .setHeader("Effective Date")
        .setWidth("120px")
        .setFlexGrow(0)
        .setSortable(true);*/
        // Actions column
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setHeader("Actions")
                .setWidth("150px")
                .setFlexGrow(0);
    }

    private Div createCeilingTypeInfo(BudgetCeiling ceiling) {
        Div container = new Div();
        container.addClassName("ceiling-type-info");

        Icon typeIcon = getCeilingTypeIcon(ceiling.getCeilingType());
        typeIcon.addClassName("type-icon");

        Span typeName = new Span(ceiling.getCeilingType().getDisplayName());
        typeName.addClassName("type-name");

        container.add(typeIcon, typeName);
        return container;
    }

    private Icon getCeilingTypeIcon(BudgetCeiling.CeilingType type) {
        switch (type) {
            case SECTION:
                return new Icon(VaadinIcon.BUILDING);
            case REVENUE_SOURCE:
                return new Icon(VaadinIcon.MONEY_DEPOSIT);
            case ACCOUNT_CODE:
                return new Icon(VaadinIcon.BOOK);
            default:
                return new Icon(VaadinIcon.INFO);
        }
    }

    private Div createHierarchyPath(BudgetCeiling ceiling) {
        Div container = new Div();
        container.addClassName("hierarchy-path");

        Span path = new Span(ceiling.getHierarchyPath());
        path.addClassName("path-text");

        container.add(path);
        return container;
    }

    private Div createAmountInfo(BudgetCeiling ceiling) {
        Div container = new Div();
        container.addClassName("amount-info");

        Span ceilingAmount = new Span(formatCurrency(ceiling.getCeilingAmount()));
        ceilingAmount.addClassName("ceiling-amount");

        Span allocatedAmount = new Span("Allocated: " + formatCurrency(ceiling.getAllocatedAmount()));
        allocatedAmount.addClassName("allocated-amount");

        Span availableAmount = new Span("Available: " + formatCurrency(ceiling.getAvailableAmount()));
        availableAmount.addClassName("available-amount");

        if (ceiling.getAvailableAmount() < 0) {
            availableAmount.addClassName("amount-negative");
        }

        container.add(ceilingAmount, allocatedAmount, availableAmount);
        return container;
    }

    private Div createUtilizationInfo(BudgetCeiling ceiling) {
        Div container = new Div();
        container.addClassName("utilization-info");

        double utilizationPercentage = ceiling.getUtilizationPercentage();

        Span utilizationText = new Span(String.format("%.1f%% utilized", utilizationPercentage));
        utilizationText.addClassName("utilization-text");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setValue(Math.min(utilizationPercentage / 100.0, 1.0));
        progressBar.addClassName(getUtilizationClass(utilizationPercentage));

        Span amounts = new Span(String.format("Spent: %s | Committed: %s",
                formatCurrency(ceiling.getSpentAmount()),
                formatCurrency(ceiling.getCommittedAmount())));
        amounts.addClassName("utilization-amounts");

        container.add(utilizationText, progressBar, amounts);
        return container;
    }

    private String getUtilizationClass(double percentage) {
        if (percentage > 100) {
            return "utilization-over";
        } else if (percentage > 95) {
            return "utilization-critical";
        } else if (percentage > 80) {
            return "utilization-near";
        } else {
            return "utilization-good";
        }
    }

    private Div createStatusBadge(BudgetCeiling ceiling) {
        Div badge = new Div();
        badge.addClassName("status-badge");
        badge.addClassName("status-" + ceiling.getCeilingStatus().name().toLowerCase());
        badge.getStyle().set("background-color", ceiling.getStatusColor() + "20");
        badge.getStyle().set("color", ceiling.getStatusColor());
        badge.getStyle().set("border", "1px solid " + ceiling.getStatusColor() + "40");

        Span statusText = new Span(ceiling.getStatusText());
        badge.add(statusText);

        return badge;
    }

    private Div createApprovalStatusBadge(BudgetCeiling ceiling) {
        Div badge = new Div();
        badge.addClassName("approval-badge");
        badge.addClassName("approval-" + ceiling.getApprovalStatus().name().toLowerCase());

        String color = getApprovalStatusColor(ceiling.getApprovalStatus());
        badge.getStyle().set("background-color", color + "20");
        badge.getStyle().set("color", color);
        badge.getStyle().set("border", "1px solid " + color + "40");

        Span statusText = new Span(ceiling.getApprovalStatus().getDisplayName());
        badge.add(statusText);

        return badge;
    }

    private String getApprovalStatusColor(BudgetCeiling.ApprovalStatus status) {
        switch (status) {
            case PENDING:
                return "#f59e0b";
            case APPROVED:
                return "#10b981";
            case REJECTED:
                return "#ef4444";
            case DRAFT:
                return "#6b7280";
            default:
                return "#6b7280";
        }
    }

    private HorizontalLayout createActionButtons(BudgetCeiling ceiling) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.setPadding(false);
        actions.addClassName("action-buttons");

        // Edit button
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editButton.addClassName("edit-button");
        editButton.addClickListener(e -> editCeiling(ceiling));

        // Approve/Reject buttons for pending items
        if (ceiling.getApprovalStatus() == BudgetCeiling.ApprovalStatus.PENDING
                && ("ADMIN".equals(currentUserRole) || "CFO".equals(currentUserRole))) {

            Button approveButton = new Button(new Icon(VaadinIcon.CHECK));
            approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
            approveButton.addClassName("approve-button");
            approveButton.addClickListener(e -> approveCeiling(ceiling));

            Button rejectButton = new Button(new Icon(VaadinIcon.CLOSE));
            rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            rejectButton.addClassName("reject-button");
            rejectButton.addClickListener(e -> rejectCeiling(ceiling));

            actions.add(editButton, approveButton, rejectButton);
        } else {
            actions.add(editButton);
        }

        return actions;
    }

    private void editCeiling(BudgetCeiling ceiling) {
        // Fire edit event or open edit dialog
        showNotification("Edit ceiling functionality coming soon", NotificationVariant.LUMO_PRIMARY);
    }

    private void approveCeiling(BudgetCeiling ceiling) {
        try {
            ceilingService.approveCeiling(ceiling.getId(), "Current User");
            showNotification("Budget ceiling approved successfully", NotificationVariant.LUMO_SUCCESS);
            refreshGrid();
        } catch (Exception e) {
            showNotification("Error approving ceiling: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void rejectCeiling(BudgetCeiling ceiling) {
        try {
            ceilingService.rejectCeiling(ceiling.getId(), "Current User", "Rejected by user");
            showNotification("Budget ceiling rejected", NotificationVariant.LUMO_ERROR);
            refreshGrid();
        } catch (Exception e) {
            showNotification("Error rejecting ceiling: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
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
