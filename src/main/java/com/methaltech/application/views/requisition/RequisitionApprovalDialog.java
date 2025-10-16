package com.methaltech.application.views.requisition;

import com.methaltech.application.data.bgtool.service.RequisitionDataService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.RequisitionItem;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.shared.Registration;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RequisitionApprovalDialog extends Dialog {

    private final RequisitionData requisition;
    private final RequisitionDataService requisitionService;
    private final boolean isApproval;
    private final NumberFormat currencyFormat;

    private TextArea commentsArea;
    private Button approveButton;
    private Button rejectButton;
    private Button cancelButton;
    private User user;
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private String approvalMessage = "";

    public RequisitionApprovalDialog(RequisitionData requisition,
            RequisitionDataService requisitionService, AuthenticatedUser authenticatedUser, UserService userService, boolean isApproval) {
        this.requisition = requisition;
        this.requisitionService = requisitionService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.isApproval = isApproval;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        this.setWidth("700px");
        this.setHeight("90vh");
        setModal(true);
        setDraggable(true);
        this.setCloseOnOutsideClick(false);
        addClassName("requisition-approval-dialog");
        setUser();
        createDialogContent();
        setButtonVisibility();
    }

    private void setUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }
    }

    private void createDialogContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("approval-dialog-layout");

        // Header
        createHeader(layout);

        // Requisition details
        createRequisitionDetails(layout);

        // Comments section
        createCommentsSection(layout);

        // Action buttons
        createActionButtons(layout);

        add(layout);
    }

    private void createHeader(VerticalLayout layout) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.addClassName("approval-dialog-header");

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setSpacing(false);
        titleSection.setPadding(false);

        H2 title = new H2(isApproval ? "Approve Requisition" : "Reject Requisition");
        title.addClassName("dialog-title");

        Span subtitle = new Span("Review and " + (isApproval ? "approve" : "reject") + " requisition request");
        subtitle.addClassName("dialog-subtitle");

        titleSection.add(title, subtitle);

        Icon approvalIcon = new Icon(isApproval ? VaadinIcon.CHECK_CIRCLE : VaadinIcon.CLOSE_CIRCLE);
        approvalIcon.addClassName("dialog-icon");
        approvalIcon.getStyle().set("color", isApproval ? "#10b981" : "#ef4444");

        header.add(titleSection, approvalIcon);
        layout.add(header);
    }

    private void createRequisitionDetails(VerticalLayout layout) {
        Div detailsCard = new Div();
        detailsCard.addClassName("requisition-details-card");

        H3 detailsTitle = new H3("Requisition Details");
        detailsTitle.addClassName("details-title");

        VerticalLayout detailsContent = new VerticalLayout();
        detailsContent.setSpacing(true);
        detailsContent.setPadding(false);

        // --- Basic Information Section ---
        H4 basicTitle = new H4("Basic Information");
        basicTitle.addClassName("section-title");

        FormLayout basicForm = new FormLayout();
        basicForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1), // mobile: 1 column
                new FormLayout.ResponsiveStep("600px", 3) // desktop: 3 columns
        );

        Component reqNumber = createDetailItem(VaadinIcon.CLIPBOARD_TEXT, "Requisition Number",
                requisition.getRequisitionNumber());

        Component type = createDetailItem(VaadinIcon.FILE_TEXT, "Type",
                requisition.getRequisitionType().getDisplayName());

        Component priority = createDetailItem(VaadinIcon.FLAG, "Priority",
                Optional.ofNullable(requisition.getPriorityLevel())
                        .map(RequisitionData.PriorityLevel::getDisplayName)
                        .orElse("Not set"));

        Component subject = createDetailItem(VaadinIcon.BRIEFCASE, "Subject of Procurement",
                requisition.getSubjectOfProcurement());

        Component justification = createDetailItem(VaadinIcon.BRIEFCASE, "Justification",
                requisition.getJustification());

        basicForm.add(reqNumber, type, priority, subject, justification);
        basicForm.setColspan(subject, 3); // subject spans 3 columns
        basicForm.setColspan(justification, 3);

        // --- Financial Information Section ---
        H4 financialTitle = new H4("Financial Information");
        financialTitle.addClassName("section-title");

        FormLayout financialForm = new FormLayout();
        financialForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 3)
        );

        Component totalAmount = createDetailItem(VaadinIcon.MONEY, "Total Amount",
                formatCurrency(requisition.getTotalAmount() != null ? requisition.getRequestedAmount() : 0.0));

        Component budget = createDetailItem(VaadinIcon.CREDIT_CARD, "Available Budget",
                formatCurrency(requisition.getAvailableBalanceByActivity() != null ? requisition.getAvailableBalanceByActivity() : 0.0));

        Component activity = createDetailItem(
                VaadinIcon.TRENDING_UP,
                "Activity",
                requisition.getActivity() != null
                ? requisition.getActivity().getActivityCode() + " - " + requisition.getActivity().getName()
                : "N/A"
        );

        financialForm.add(totalAmount, budget, activity);

        // --- Requisition Items Grid ---
        H4 itemsTitle = new H4("Requisition Items");
        itemsTitle.addClassName("section-title");

        Grid<RequisitionItem> itemsGrid = new Grid<>(RequisitionItem.class, false);
        itemsGrid.setWidthFull();
        itemsGrid.addClassName("items-grid");

        /*        itemsGrid.addColumn(RequisitionItem::getItemNumber)
        .setHeader("Item No.")
        .setAutoWidth(true);*/
        itemsGrid.addColumn(RequisitionItem::getDescription)
                .setHeader("Description")
                .setFlexGrow(2);

        itemsGrid.addColumn(RequisitionItem::getQuantity)
                .setHeader("Quantity")
                .setAutoWidth(true);

        itemsGrid.addColumn(RequisitionItem::getUnitOfMeasure)
                .setHeader("Unit")
                .setAutoWidth(true);

        itemsGrid.addColumn(RequisitionItem::getFormattedUnitCost)
                .setHeader("Unit Cost")
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true);

        Grid.Column<RequisitionItem> totalCostCol = itemsGrid
                .addColumn(RequisitionItem::getTotalCost)
                .setHeader("Total Cost")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        itemsGrid.setAllRowsVisible(true);

        FooterRow footerRow = itemsGrid.appendFooterRow();

        footerRow.getCell(totalCostCol).setText("UGX 0");

// Method to update footer total
        Runnable updateFooter = () -> {
            double sum = requisition.getItems().stream()
                    .mapToDouble(item -> item.getTotalCost() != null ? item.getTotalCost() : 0.0)
                    .sum();
            footerRow.getCell(totalCostCol).setText(String.format("TOTAL UGX %,.2f", sum));
        };
        // load requisition items
        if (requisition.getItems() != null) {
            itemsGrid.setItems(requisition.getItems());
        }
        updateFooter.run();
        itemsGrid.setHeight("220px");

        // --- Assemble content ---
        detailsContent.add(basicTitle, basicForm, financialTitle, financialForm, itemsTitle, itemsGrid);
        detailsCard.add(detailsTitle, detailsContent);
        layout.add(detailsCard);
    }

    private VerticalLayout createDetailItem(VaadinIcon icon, String label, String value) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName("detail-item");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("detail-label");

        // Split code and name for styling
        Span valueSpan = new Span();
        if (value.contains(" - ")) {
            String[] parts = value.split(" - ", 2);
            Span code = new Span(parts[0]);
            code.getStyle().set("font-weight", "600");
            Span name = new Span(" - " + parts[1]);
            name.getStyle().set("font-weight", "400");
            valueSpan.add(code, name);
        } else {
            valueSpan.setText(value);
            valueSpan.getStyle().set("font-weight", "600");
        }

        valueSpan.addClassName("detail-value");
        item.add(labelSpan, valueSpan);
        return item;
    }

    private void createCommentsSection(VerticalLayout layout) {
        // Card container for comments
        Div commentsCard = new Div();
        commentsCard.addClassName("comments-card");
        commentsCard.setWidthFull(); // Make the card full width

        // Title
        H3 commentsTitle = new H3(isApproval ? "Approval Comments" : "Rejection Reason");
        commentsTitle.addClassName("comments-title");

        // Text area for input
        commentsArea = new TextArea();
        commentsArea.setPlaceholder(isApproval ? "Enter your approval comments..." : "Enter rejection reason...");
        commentsArea.setWidthFull(); // Make text area full width
        commentsArea.setHeight("60px");
        commentsArea.addClassName("comments-area");
        commentsArea.setValue(Optional.ofNullable(requisition.getComment()).orElse(""));

        // Add components to the card
        commentsCard.add(commentsTitle, commentsArea);

        // Wrap in a VerticalLayout to ensure full width in parent
        VerticalLayout wrapper = new VerticalLayout(commentsCard);
        wrapper.setWidthFull(); // Make sure wrapper is full width
        wrapper.setPadding(false);
        wrapper.setSpacing(false);

        layout.add(wrapper);
    }

    private void setButtonVisibility() {
        if (requisition.getStatus().equals(RequisitionData.RequisitionStatus.APPROVED)) {
            approveButton.setEnabled(false);
        }
    }

    private void createActionButtons(VerticalLayout layout) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull(); // Make container full width
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setPadding(true);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName("approval-buttons");

        cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("cancel-button");
        cancelButton.setWidth("150px"); // fixed width
        cancelButton.addClickListener(e -> close());

        if (isApproval) {
            approveButton = new Button("Approve", new Icon(VaadinIcon.CHECK));
            approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            approveButton.addClassName("approve-button");
            approveButton.setWidth("150px"); // ensure enough space for text + icon
            approveButton.setIconAfterText(true);
            approveButton.addClickListener(e -> {
                if (!commentsArea.isEmpty() || commentsArea.getValue() != null) {
                    approvalMessage = commentsArea.getValue();
                    approveRequisition(approvalMessage);
                    showNotification("Approved Successifully", NotificationVariant.LUMO_SUCCESS);
                    this.close();
                } else {
                    showNotification("Add a message", NotificationVariant.LUMO_WARNING);
                    this.close();
                }

            });
            buttonLayout.add(cancelButton, approveButton);
        } else {
            rejectButton = new Button("Reject", new Icon(VaadinIcon.CLOSE));
            rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            rejectButton.addClassName("reject-button");
            rejectButton.setWidth("150px"); // ensure text is visible
            rejectButton.setIconAfterText(true);
            rejectButton.addClickListener(e -> rejectRequisition());
            buttonLayout.add(cancelButton, rejectButton);
        }

        layout.add(buttonLayout);
    }

    private void approveRequisition(String message) {
        try {
            String comments = commentsArea.getValue();
            if (comments == null || comments.trim().isEmpty()) {
                showNotification("Please provide approval comments", NotificationVariant.LUMO_WARNING);
                return;
            }else{
            requisitionService.approveRequisition(requisition.getId(), user, message);
            showNotification("Requisition approved successfully", NotificationVariant.LUMO_SUCCESS);                
            }
            //fireEvent(new ApprovalEvent(this, requisition, true));
            close();

        } catch (Exception e) {
            showNotification("Error approving requisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void rejectRequisition() {
        try {
            String reason = commentsArea.getValue();
            if (reason == null || reason.trim().isEmpty()) {
                showNotification("Please provide rejection reason", NotificationVariant.LUMO_WARNING);
                return;
            }

            //  requisitionService.rejectRequisition(requisition.getId(), currentUser, reason);
            showNotification("Requisition rejected", NotificationVariant.LUMO_ERROR);

            fireEvent(new ApprovalEvent(this, requisition, false));
            close();

        } catch (Exception e) {
            showNotification("Error rejecting requisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    /*    private String formatCurrency(double amount) {
    if (Math.abs(amount) >= 1_000_000_000) {
    return String.format("UGX %.1fB", amount / 1_000_000_000);
    } else if (Math.abs(amount) >= 1_000_000) {
    return String.format("UGX %.1fM", amount / 1_000_000);
    } else if (Math.abs(amount) >= 1_000) {
    return String.format("UGX %.0fK", amount / 1_000);
    } else {
    return String.format("UGX %.0f", amount);
    }
    }*/
    private String formatCurrency(Double amount) {
        if (amount == null) {
            return "0.0";
        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return formatter.format(amount);
    }

    // Event handling
    public static class ApprovalEvent extends ComponentEvent<RequisitionApprovalDialog> {

        private final RequisitionData requisition;
        private final boolean approved;

        public ApprovalEvent(RequisitionApprovalDialog source, RequisitionData requisition, boolean approved) {
            super(source, false);
            this.requisition = requisition;
            this.approved = approved;
        }

        public RequisitionData getRequisition() {
            return requisition;
        }

        public boolean isApproved() {
            return approved;
        }
    }

    public Registration addApprovalListener(ComponentEventListener<ApprovalEvent> listener) {
        return addListener(ApprovalEvent.class, listener);
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }
}
