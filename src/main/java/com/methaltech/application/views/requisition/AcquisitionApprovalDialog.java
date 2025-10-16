package com.methaltech.application.views.requisition;

import com.methaltech.application.data.bgtool.service.RequisitionDataService;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.User;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;

import java.text.NumberFormat;
import java.util.Locale;

public class AcquisitionApprovalDialog extends Dialog {

    private final RequisitionData acquisition;
    private final User currentUser;
    private final RequisitionDataService acquisitionService;
    private final NumberFormat currencyFormat;

    private TextArea commentsArea;
    private Button approveButton;
    private Button rejectButton;
    private Button cancelButton;

    public AcquisitionApprovalDialog(RequisitionData acquisition, User currentUser,
            RequisitionDataService acquisitionService) {
        this.acquisition = acquisition;
        this.currentUser = currentUser;
        this.acquisitionService = acquisitionService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        setWidth("700px");
        setHeight("600px");
        
        setModal(true);
        setDraggable(true);
        this.setCloseOnOutsideClick(false);
        addClassName("acquisition-approval-dialog");

        createDialogContent();
    }

    private void createDialogContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.addClassName("approval-dialog-layout");

        // Header
        createHeader(layout);

        // Acquisition details
        createAcquisitionDetails(layout);

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

        H2 title = new H2("Acquisition Approval");
        title.addClassName("dialog-title");

        Span subtitle = new Span("Review and approve acquisition request");
        subtitle.addClassName("dialog-subtitle");

        titleSection.add(title, subtitle);

        Icon approvalIcon = new Icon(VaadinIcon.CHECK_CIRCLE);
        approvalIcon.addClassName("dialog-icon");

        header.add(titleSection, approvalIcon);
        layout.add(header);
    }

    private void createAcquisitionDetails(VerticalLayout layout) {
        Div detailsCard = new Div();
        detailsCard.addClassName("acquisition-details-card");

        H3 detailsTitle = new H3("Acquisition Details");
        detailsTitle.addClassName("details-title");

        VerticalLayout detailsContent = new VerticalLayout();
        detailsContent.setSpacing(true);
        detailsContent.setPadding(false);

        // Basic information
        HorizontalLayout basicInfo = new HorizontalLayout();
        basicInfo.setWidthFull();
        basicInfo.setSpacing(true);

        basicInfo.add(
                createDetailItem("Acquisition Number", acquisition.getRequisitionNumber()),
                createDetailItem("Type", acquisition.getProcType().getDisplayName()),
                createDetailItem("Priority", acquisition.getPriorityLevel().getDisplayName())
        );

        // Financial information
        HorizontalLayout financialInfo = new HorizontalLayout();
        financialInfo.setWidthFull();
        financialInfo.setSpacing(true);

        financialInfo.add(
                createDetailItem("Requested Amount", formatCurrency(acquisition.getRequestedAmount())),
                createDetailItem("Available Budget", formatCurrency(acquisition.getAvailableBalanceByActivity())),
                createDetailItem("Balance After", formatCurrency(acquisition.getAvailableBalanceByActivity() - acquisition.getRequestedAmount()))
        );

        // Organisation and COA
        HorizontalLayout orgCoaInfo = new HorizontalLayout();
        orgCoaInfo.setWidthFull();
        orgCoaInfo.setSpacing(true);

        orgCoaInfo.add(
                createDetailItem("Organisation", acquisition.getOrganisation().getName()),
                createDetailItem("Account Code", acquisition.getCoa().getCode()),
                createDetailItem("Account Name", acquisition.getCoa().getName())
        );

        // Purpose
        Div purposeSection = new Div();
        purposeSection.addClassName("purpose-section");

        Span purposeLabel = new Span("Purpose:");
        purposeLabel.addClassName("detail-label");

        Span purposeText = new Span(acquisition.getPurpose());
        purposeText.addClassName("purpose-text");

        purposeSection.add(purposeLabel, purposeText);

        detailsContent.add(basicInfo, financialInfo, orgCoaInfo, purposeSection);
        detailsCard.add(detailsTitle, detailsContent);
        layout.add(detailsCard);
    }

    private VerticalLayout createDetailItem(String label, String value) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.addClassName("detail-item");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("detail-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("detail-value");

        item.add(labelSpan, valueSpan);
        return item;
    }

    private void createCommentsSection(VerticalLayout layout) {
        Div commentsCard = new Div();
        commentsCard.addClassName("comments-card");

        H3 commentsTitle = new H3("Approval Comments");
        commentsTitle.addClassName("comments-title");

        commentsArea = new TextArea();
        commentsArea.setPlaceholder("Enter your approval comments...");
        commentsArea.setWidthFull();
        commentsArea.setHeight("100px");
        commentsArea.addClassName("comments-area");

        commentsCard.add(commentsTitle, commentsArea);
        layout.add(commentsCard);
    }

    private void createActionButtons(VerticalLayout layout) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName("approval-buttons");

        cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClassName("cancel-button");
        cancelButton.addClickListener(e -> close());

        rejectButton = new Button("Reject", new Icon(VaadinIcon.CLOSE));
        rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        rejectButton.addClassName("reject-button");
        rejectButton.addClickListener(e -> rejectAcquisition());

        approveButton = new Button("Approve", new Icon(VaadinIcon.CHECK));
        approveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        approveButton.addClassName("approve-button");
        approveButton.addClickListener(e -> approveAcquisition());

        buttonLayout.add(cancelButton, rejectButton, approveButton);
        layout.add(buttonLayout);
    }

    private void approveAcquisition() {
        try {
            String comments = commentsArea.getValue();
            if (comments == null || comments.trim().isEmpty()) {
                showNotification("Please provide approval comments", NotificationVariant.LUMO_WARNING);
                return;
            }

            acquisitionService.approveRequisition(acquisition.getId(), currentUser, comments);
            showNotification("Acquisition approved successfully", NotificationVariant.LUMO_SUCCESS);

            fireEvent(new ApprovalEvent(this, acquisition, true));
            close();

        } catch (Exception e) {
            showNotification("Error approving acquisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void rejectAcquisition() {
        try {
            String reason = commentsArea.getValue();
            if (reason == null || reason.trim().isEmpty()) {
                showNotification("Please provide rejection reason", NotificationVariant.LUMO_WARNING);
                return;
            }

            acquisitionService.rejectRequisition(acquisition.getId(), currentUser, reason);
            showNotification("Acquisition rejected", NotificationVariant.LUMO_ERROR);

            fireEvent(new ApprovalEvent(this, acquisition, false));
            close();

        } catch (Exception e) {
            showNotification("Error rejecting acquisition: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
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

    // Event handling
    public static class ApprovalEvent extends ComponentEvent<AcquisitionApprovalDialog> {

        private final RequisitionData acquisition;
        private final boolean approved;

        public ApprovalEvent(AcquisitionApprovalDialog source, RequisitionData acquisition, boolean approved) {
            super(source, false);
            this.acquisition = acquisition;
            this.approved = approved;
        }

        public RequisitionData getAcquisition() {
            return acquisition;
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
