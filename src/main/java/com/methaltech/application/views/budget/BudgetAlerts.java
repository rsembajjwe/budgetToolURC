
package com.methaltech.application.views.budget;

import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BudgetAlerts extends VerticalLayout {
    
    private final NumberFormat currencyFormat;
    private final DepartmentBudget department;

    public BudgetAlerts(DepartmentBudget department) {
        this.department = department;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        
        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("budget-alerts");
        
        createAlertsOverview();
        createHeader();
        createAlertsSection();
        createAlertActions();
    }

    private void createAlertsOverview() {
        Div overviewCard = new Div();
        overviewCard.addClassName("alerts-overview-card");
        
        HorizontalLayout overviewGrid = new HorizontalLayout();
        overviewGrid.setWidthFull();
        overviewGrid.setSpacing(true);
        overviewGrid.setPadding(false);
        overviewGrid.addClassName("alerts-overview-grid");
        
        List<AlertItem> allAlerts = generateAlerts();
        long criticalCount = allAlerts.stream().filter(a -> "critical".equals(a.getType())).count();
        long warningCount = allAlerts.stream().filter(a -> "warning".equals(a.getType())).count();
        long infoCount = allAlerts.stream().filter(a -> "info".equals(a.getType())).count();
        
        overviewGrid.add(
            createOverviewMetric("Critical Alerts", String.valueOf(criticalCount), VaadinIcon.EXCLAMATION_CIRCLE, "#dc2626"),
            createOverviewMetric("Warnings", String.valueOf(warningCount), VaadinIcon.WARNING, "#f59e0b"),
            createOverviewMetric("Information", String.valueOf(infoCount), VaadinIcon.INFO_CIRCLE, "#3b82f6"),
            createOverviewMetric("Total Alerts", String.valueOf(allAlerts.size()), VaadinIcon.BELL, "#7c3aed")
        );
        
        overviewCard.add(overviewGrid);
        add(overviewCard);
    }

    private VerticalLayout createOverviewMetric(String label, String value, VaadinIcon iconType, String color) {
        VerticalLayout metric = new VerticalLayout();
        metric.setSpacing(false);
        metric.setPadding(false);
        metric.addClassName("alert-overview-metric");
        metric.setAlignItems(FlexComponent.Alignment.CENTER);
        metric.setWidthFull();
        
        Div iconContainer = new Div();
        iconContainer.addClassName("alert-metric-icon-container");
        iconContainer.getStyle().set("background-color", color + "20");
        iconContainer.getStyle().set("border", "1px solid " + color + "40");
        
        Icon icon = new Icon(iconType);
        icon.addClassName("alert-metric-icon");
        icon.getStyle().set("color", color);
        iconContainer.add(icon);
        
        Span valueSpan = new Span(value);
        valueSpan.addClassName("alert-metric-value");
        valueSpan.getStyle().set("color", color);
        
        Span labelSpan = new Span(label);
        labelSpan.addClassName("alert-metric-label");
        
        metric.add(iconContainer, valueSpan, labelSpan);
        return metric;
    }
    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);
        header.addClassName("alerts-main-header");
        
        VerticalLayout headerText = new VerticalLayout();
        headerText.setSpacing(false);
        headerText.setPadding(false);
        
        H3 title = new H3("Budget Alert Center");
        title.addClassName("alerts-title");
        
        Span subtitle = new Span("Real-time budget monitoring and intelligent notifications");
        subtitle.addClassName("alerts-subtitle");
        
        headerText.add(title, subtitle);
        
        HorizontalLayout headerActions = new HorizontalLayout();
        headerActions.setAlignItems(FlexComponent.Alignment.CENTER);
        headerActions.setSpacing(true);
        headerActions.setPadding(false);
        headerActions.addClassName("alerts-header-actions");
        
        Button refreshAlertsButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshAlertsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        refreshAlertsButton.addClassName("alerts-refresh-button");
        refreshAlertsButton.addClickListener(e -> {
            showNotification("Alerts refreshed", NotificationVariant.LUMO_SUCCESS);
        });
        
        Button settingsButton = new Button(new Icon(VaadinIcon.COG));
        settingsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        settingsButton.addClassName("alerts-settings-button");
        settingsButton.addClickListener(e -> {
            showNotification("Alert settings coming soon", NotificationVariant.LUMO_PRIMARY);
        });
        
        Icon alertIcon = new Icon(VaadinIcon.BELL);
        alertIcon.addClassName("alerts-main-icon");
        
        headerActions.add(refreshAlertsButton, settingsButton, alertIcon);
        
        header.add(headerText, headerActions);
        add(header);
    }

    private void createAlertsSection() {
        Div alertsCard = new Div();
        alertsCard.addClassName("alerts-card");
        
        VerticalLayout alertsContent = new VerticalLayout();
        alertsContent.setSpacing(true);
        alertsContent.setPadding(false);
        alertsContent.setWidthFull();
        alertsContent.addClassName("alerts-content");
        
        List<AlertItem> alerts = generateAlerts();
        
        if (alerts.isEmpty()) {
            alertsContent.add(createNoAlertsState());
        } else {
            // Group alerts by priority
            List<AlertItem> highPriorityAlerts = alerts.stream()
                .filter(a -> "high".equals(a.getPriority()))
                .collect(Collectors.toList());
            List<AlertItem> mediumPriorityAlerts = alerts.stream()
                .filter(a -> "medium".equals(a.getPriority()))
                .collect(Collectors.toList());
            List<AlertItem> lowPriorityAlerts = alerts.stream()
                .filter(a -> "low".equals(a.getPriority()))
                .collect(Collectors.toList());
            
            if (!highPriorityAlerts.isEmpty()) {
                alertsContent.add(createAlertSection("High Priority", highPriorityAlerts, "#dc2626"));
            }
            if (!mediumPriorityAlerts.isEmpty()) {
                alertsContent.add(createAlertSection("Medium Priority", mediumPriorityAlerts, "#f59e0b"));
            }
            if (!lowPriorityAlerts.isEmpty()) {
                alertsContent.add(createAlertSection("Low Priority", lowPriorityAlerts, "#10b981"));
            }
        }
        
        alertsCard.add(alertsContent);
        add(alertsCard);
    }

    private VerticalLayout createNoAlertsState() {
        VerticalLayout noAlertsState = new VerticalLayout();
        noAlertsState.setAlignItems(FlexComponent.Alignment.CENTER);
        noAlertsState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        noAlertsState.addClassName("no-alerts-state");
        
        Div successContainer = new Div();
        successContainer.addClassName("success-container");
        
        Icon checkIcon = new Icon(VaadinIcon.CHECK_CIRCLE);
        checkIcon.addClassName("no-alerts-icon");
        
        H4 noAlertsTitle = new H4("All Systems Operational");
        noAlertsTitle.addClassName("no-alerts-title");
        
        Span noAlertsMessage = new Span("No budget alerts detected. Your department is operating within optimal parameters.");
        noAlertsMessage.addClassName("no-alerts-message");
        
        Span lastChecked = new Span("Last checked: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        lastChecked.addClassName("last-checked");
        
        successContainer.add(checkIcon);
        noAlertsState.add(successContainer, noAlertsTitle, noAlertsMessage, lastChecked);
        
        return noAlertsState;
    }

    private VerticalLayout createAlertSection(String sectionTitle, List<AlertItem> alerts, String color) {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);
        section.setPadding(false);
        section.setWidthFull();
        section.addClassName("alert-priority-section");
        
        // Section header
        HorizontalLayout sectionHeader = new HorizontalLayout();
        sectionHeader.setWidthFull();
        sectionHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionHeader.setPadding(false);
        sectionHeader.setSpacing(false);
        sectionHeader.addClassName("alert-section-header");
        
        H4 title = new H4(sectionTitle);
        title.addClassName("alert-section-title");
        title.getStyle().set("color", color);
        
        Span count = new Span(alerts.size() + " alerts");
        count.addClassName("alert-section-count");
        count.getStyle().set("background-color", color + "20");
        count.getStyle().set("color", color);
        count.getStyle().set("border", "1px solid " + color + "40");
        
        sectionHeader.add(title, count);
        section.add(sectionHeader);
        
        // Alerts in this section
        for (AlertItem alert : alerts) {
            section.add(createEnhancedAlertCard(alert));
        }
        
        return section;
    }

    private void createAlertActions() {
        Div actionsCard = new Div();
        actionsCard.addClassName("alert-actions-card");
        
        VerticalLayout actionsContent = new VerticalLayout();
        actionsContent.setSpacing(true);
        actionsContent.setPadding(false);
        actionsContent.setWidthFull();
        
        H4 actionsTitle = new H4("Alert Management");
        actionsTitle.addClassName("actions-title");
        
        HorizontalLayout actionsGrid = new HorizontalLayout();
        actionsGrid.setWidthFull();
        actionsGrid.setSpacing(true);
        actionsGrid.setPadding(false);
        actionsGrid.addClassName("alert-actions-grid");
        
        Button markAllReadButton = new Button("Mark All Read", new Icon(VaadinIcon.CHECK));
        markAllReadButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        markAllReadButton.addClassName("mark-read-button");
        markAllReadButton.addClickListener(e -> {
            showNotification("All alerts marked as read", NotificationVariant.LUMO_SUCCESS);
        });
        
        Button exportAlertsButton = new Button("Export Report", new Icon(VaadinIcon.DOWNLOAD));
        exportAlertsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        exportAlertsButton.addClassName("export-alerts-button");
        exportAlertsButton.addClickListener(e -> {
            showNotification("Alert report export coming soon", NotificationVariant.LUMO_PRIMARY);
        });
        
        Button configureButton = new Button("Configure", new Icon(VaadinIcon.COG));
        configureButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);
        configureButton.addClassName("configure-alerts-button");
        configureButton.addClickListener(e -> {
            showNotification("Alert configuration coming soon", NotificationVariant.LUMO_CONTRAST);
        });
        
        actionsGrid.add(markAllReadButton, exportAlertsButton, configureButton);
        actionsContent.add(actionsTitle, actionsGrid);
        actionsCard.add(actionsContent);
        add(actionsCard);
    }

    private Div createEnhancedAlertCard(AlertItem alert) {
        Div alertCard = new Div();
        alertCard.addClassName("enhanced-alert-card");
        alertCard.addClassName("alert-" + alert.getType());
        alertCard.addClassName("priority-" + alert.getPriority());
        
        HorizontalLayout alertContent = new HorizontalLayout();
        alertContent.setWidthFull();
        alertContent.setAlignItems(FlexComponent.Alignment.START);
        alertContent.setSpacing(true);
        alertContent.setPadding(false);
        alertContent.addClassName("enhanced-alert-content");
        
        // Priority indicator strip
        Div priorityStrip = new Div();
        priorityStrip.addClassName("alert-priority-strip");
        priorityStrip.addClassName("priority-" + alert.getPriority());
        
        // Alert icon
        Div iconContainer = new Div();
        iconContainer.addClassName("enhanced-alert-icon-container");
        iconContainer.addClassName("icon-" + alert.getType());
        
        Icon alertIcon = getEnhancedAlertIcon(alert.getType());
        alertIcon.addClassName("enhanced-alert-icon");
        iconContainer.add(alertIcon);
        
        // Alert content
        VerticalLayout alertText = new VerticalLayout();
        alertText.setSpacing(false);
        alertText.setPadding(false);
        alertText.setFlexGrow(1);
        alertText.addClassName("enhanced-alert-text");
        
        // Alert header with priority badge
        HorizontalLayout alertHeader = new HorizontalLayout();
        alertHeader.setWidthFull();
        alertHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        alertHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        alertHeader.setPadding(false);
        alertHeader.setSpacing(false);
        
        H5 alertTitle = new H5(alert.getTitle());
        alertTitle.addClassName("enhanced-alert-title");
        
        Div priorityBadge = new Div();
        priorityBadge.addClassName("alert-priority-badge");
        priorityBadge.addClassName("priority-" + alert.getPriority());
        priorityBadge.setText(alert.getPriority().toUpperCase());
        
        alertHeader.add(alertTitle, priorityBadge);
        
        Span alertMessage = new Span(alert.getMessage());
        alertMessage.addClassName("enhanced-alert-message");
        
        // Alert footer with timestamp and actions
        HorizontalLayout alertFooter = new HorizontalLayout();
        alertFooter.setWidthFull();
        alertFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        alertFooter.setAlignItems(FlexComponent.Alignment.CENTER);
        alertFooter.setPadding(false);
        alertFooter.setSpacing(false);
        alertFooter.addClassName("enhanced-alert-footer");
        
        HorizontalLayout timeInfo = new HorizontalLayout();
        timeInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        timeInfo.setSpacing(false);
        timeInfo.setPadding(false);
        
        Icon timeIcon = new Icon(VaadinIcon.CLOCK);
        timeIcon.addClassName("alert-time-icon");
        
        Span alertTime = new Span(getRelativeTime(alert.getTimestamp()));
        alertTime.addClassName("enhanced-alert-time");
        
        timeInfo.add(timeIcon, alertTime);
        
        HorizontalLayout alertActions = new HorizontalLayout();
        alertActions.setAlignItems(FlexComponent.Alignment.CENTER);
        alertActions.setSpacing(false);
        alertActions.setPadding(false);
        alertActions.addClassName("alert-item-actions");
        
        Button dismissButton = new Button(new Icon(VaadinIcon.CLOSE));
        dismissButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        dismissButton.addClassName("alert-dismiss-button");
        dismissButton.addClickListener(e -> {
            showNotification("Alert dismissed", NotificationVariant.LUMO_CONTRAST);
        });
        
        Button detailsButton = new Button(new Icon(VaadinIcon.INFO));
        detailsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        detailsButton.addClassName("alert-details-button");
        detailsButton.addClickListener(e -> {
            showNotification("Alert details: " + alert.getTitle(), NotificationVariant.LUMO_PRIMARY);
        });
        
        alertActions.add(detailsButton, dismissButton);
        alertFooter.add(timeInfo, alertActions);
        
        alertText.add(alertHeader, alertMessage, alertFooter);
        
        // Status indicator
        Div statusIndicator = new Div();
        statusIndicator.addClassName("alert-status-indicator");
        statusIndicator.addClassName("status-" + alert.getType());
        
        alertContent.add(priorityStrip, iconContainer, alertText, statusIndicator);
        alertCard.add(alertContent);
        
        return alertCard;
    }

    private Icon getEnhancedAlertIcon(String type) {
        switch (type) {
            case "critical": return new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O);
            case "warning": return new Icon(VaadinIcon.WARNING);
            case "info": return new Icon(VaadinIcon.INFO_CIRCLE_O);
            case "success": return new Icon(VaadinIcon.CHECK_CIRCLE_O);
            default: return new Icon(VaadinIcon.INFO);
        }
    }

    private String getRelativeTime(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(timestamp, now).toHours();
        long days = java.time.Duration.between(timestamp, now).toDays();
        
        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            return "Just now";
        }
    }
    private List<AlertItem> generateAlerts() {
        List<AlertItem> alerts = new ArrayList<>();
        double spentPercentage = department.getSpentPercentage();
        double availableBudget = department.getAvailableBudget();
        
        // Critical alerts
        if (spentPercentage > 100) {
            alerts.add(new AlertItem(
                "critical",
                "high",
                "🚨 Budget Exceeded - Immediate Action Required",
                String.format("Department has exceeded allocated budget by %s (%.1f%% utilization). This requires immediate attention and corrective measures.", 
                    formatCurrency(Math.abs(availableBudget)), spentPercentage),
                LocalDateTime.now().minusHours(2)
            ));
        }
        
        // Warning alerts
        if (spentPercentage > 90 && spentPercentage <= 100) {
            alerts.add(new AlertItem(
                "warning",
                "high",
                "⚠️ Budget Approaching Critical Level",
                String.format("Department has utilized %.1f%% of allocated budget. Only %s remaining before exceeding limits. Consider implementing spending controls.", 
                    spentPercentage, formatCurrency(availableBudget)),
                LocalDateTime.now().minusHours(6)
            ));
        }
        
        if (availableBudget < 50_000_000 && availableBudget > 0) {
            alerts.add(new AlertItem(
                "warning",
                "medium",
                "💰 Low Available Budget Alert",
                String.format("Available budget has fallen below the UGX 50M threshold. Current available: %s. Plan remaining expenditures carefully.", 
                    formatCurrency(availableBudget)),
                LocalDateTime.now().minusHours(12)
            ));
        }
        
        // Info alerts
        if (department.isBudgetStopEnabled()) {
            alerts.add(new AlertItem(
                "info",
                "medium",
                "🛡️ Budget Protection Active",
                "Budget stop control is currently enabled. This will automatically block new transactions if budget limits are exceeded, providing financial protection.",
                LocalDateTime.now().minusDays(1)
            ));
        }
        
        if (department.isPostingProhibited()) {
            alerts.add(new AlertItem(
                "critical",
                "high",
                "🚫 Financial Posting Blocked",
                "All financial postings are currently prohibited for this department. Contact system administrator to resolve this restriction.",
                LocalDateTime.now().minusHours(4)
            ));
        }
        
        // Success alerts
        if (spentPercentage < 75 && spentPercentage > 25) {
            alerts.add(new AlertItem(
                "success",
                "low",
                "✅ Budget Performance Excellent",
                String.format("Department budget utilization is optimal at %.1f%%. Financial management is on track with healthy spending patterns.", 
                    spentPercentage),
                LocalDateTime.now().minusDays(2)
            ));
        }
        
        // Add efficiency alert
        if (spentPercentage < 40) {
            alerts.add(new AlertItem(
                "info",
                "medium",
                "📊 Budget Utilization Below Target",
                String.format("Current utilization at %.1f%% is below optimal range. Consider accelerating planned activities or reallocating unused funds.", 
                    spentPercentage),
                LocalDateTime.now().minusHours(8)
            ));
        }
        
        return alerts;
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
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

    private static class AlertItem {
        private final String type;
        private final String priority;
        private final String title;
        private final String message;
        private final LocalDateTime timestamp;

        public AlertItem(String type, String priority, String title, String message, LocalDateTime timestamp) {
            this.type = type;
            this.priority = priority;
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getType() { return type; }
        public String getPriority() { return priority; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
