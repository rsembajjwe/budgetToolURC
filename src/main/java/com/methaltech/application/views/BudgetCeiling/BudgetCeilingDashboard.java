
package com.methaltech.application.views.BudgetCeiling;

import com.methaltech.application.data.bgtool.service.BudgetCeilingService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetCeilingDashboard extends HorizontalLayout {
    
    private final BudgetCeilingService ceilingService;
    private final NumberFormat currencyFormat;

    public BudgetCeilingDashboard(BudgetCeilingService ceilingService, Budget selectedBudget) {
        this.ceilingService = ceilingService;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        
        setWidthFull();
        setSpacing(true);
        setPadding(false);
        addClassName("budget-ceiling-dashboard");
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        
        createDashboardCards(selectedBudget);
    }

    private void createDashboardCards(Budget selectedBudget) {
        // Get statistics from service
        Long activeCeilings = ceilingService.getActiveCeilingsCount(selectedBudget);
        Long overCeiling = ceilingService.getOverCeilingCount(selectedBudget);
        Long nearCeiling = ceilingService.getNearCeilingCount(selectedBudget);
        Double totalCeilingAmount = ceilingService.getTotalCeilingAmount(selectedBudget);
        Double totalAllocatedAmount = ceilingService.getTotalAllocatedAmount(selectedBudget);

        add(
            createDashboardCard("Active Ceilings", activeCeilings.toString(), 
                VaadinIcon.SHIELD, "card-active"),
            createDashboardCard("Over Ceiling", overCeiling.toString(), 
                VaadinIcon.EXCLAMATION_CIRCLE, "card-over"),
            createDashboardCard("Near Ceiling", nearCeiling.toString(), 
                VaadinIcon.WARNING, "card-near"),
            createDashboardCard("Total Ceiling", formatCurrency(totalCeilingAmount), 
                VaadinIcon.MONEY_DEPOSIT, "card-total"),
            createDashboardCard("Total Allocated", formatCurrency(totalAllocatedAmount), 
                VaadinIcon.CHART, "card-allocated")
        );
    }

    private Div createDashboardCard(String title, String value, VaadinIcon iconType, String cardClass) {
        Div card = new Div();
        card.addClassName("ceiling-dashboard-card");
        card.addClassName(cardClass);
        card.setMinWidth("180px");
        card.setMaxWidth("220px");
        
        this.setFlexGrow(1, card);
        
        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setPadding(false);
        content.setSpacing(false);
        
        VerticalLayout textContent = new VerticalLayout();
        textContent.setSpacing(false);
        textContent.setPadding(false);
        textContent.setFlexGrow(1);
        
        Span titleSpan = new Span(title);
        titleSpan.addClassName("card-title");
        
        H3 valueH3 = new H3(value);
        valueH3.addClassName("card-value");
        valueH3.getStyle().set("word-break", "break-word");
        
        textContent.add(titleSpan, valueH3);
        
        Div iconContainer = new Div();
        iconContainer.addClassName("card-icon");
        iconContainer.getStyle().set("flex-shrink", "0");
        Icon icon = new Icon(iconType);
        icon.setSize("1.5rem");
        iconContainer.add(icon);
        
        content.add(textContent, iconContainer);
        card.add(content);
        
        // Add hover effect
        card.getElement().addEventListener("mouseenter", e -> {
            card.getElement().getStyle().set("transform", "translateY(-4px)");
        });
        
        card.getElement().addEventListener("mouseleave", e -> {
            card.getElement().getStyle().set("transform", "translateY(0)");
        });
        
        return card;
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
}
