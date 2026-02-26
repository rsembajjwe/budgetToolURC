package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.livedata.repository.SALFLDGProjection;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.views.actual.utilityActuals;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.math.BigDecimal;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BudgetSummaryCards extends HorizontalLayout {

    private final NumberFormat currencyFormat;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    utilityActuals utils;
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final Budget budget;
    PeriodExtractor extract = new PeriodExtractor();

    public BudgetSummaryCards(BudgetSummary budgetSummary, CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, SALFLDGService sampleSALFLDGService, DeptSectionMergerService sampleDeptSectionMergerService, Budget budget) {
        // Create UGX currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budget = budget;

        setWidthFull();
        setSpacing(true);
        setPadding(false);
        addClassName("budget-summary-cards-professional");
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Enable flex wrap using CSS
        getStyle().set("flex-wrap", "wrap");
        getStyle().set("gap", "var(--lumo-space-m)");

        Div spent = createSummaryCard("Total Spent", budgetSummary.getTotalSpent(), VaadinIcon.TRENDING_DOWN, "card-red", String.format("%.1f%% of budget", budgetSummary.getSpentPercentage()));
// Create context menu and attach to component
        ContextMenu contextMenu = new ContextMenu(spent);
        contextMenu.setOpenOnClick(false); // default = right-click

// Add menu items
        contextMenu.addItem("Download Qtr 1 Expenses", event -> {
            Set<String> sections = new HashSet<>();
            for (DepartmentBudget f : budgetSummary.getDepartmentBudgets()) {
                String deptcode = f.getDepartmentCode();
                Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
                sections.addAll(sectionCodes);
            }
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sections);
            utils.exportAndDownloadExcelTransactionDetails(budget, utils.refreshgridTransactions2(1), this, 1);
        });

        contextMenu.addItem("Download Qtr 2 Expenses", event -> {
            Set<String> sections = new HashSet<>();
            for (DepartmentBudget f : budgetSummary.getDepartmentBudgets()) {
                String deptcode = f.getDepartmentCode();
                Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
                sections.addAll(sectionCodes);
            }
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sections);
            utils.exportAndDownloadExcelTransactionDetails(budget, utils.refreshgridTransactions2(2), this, 2);
        });
        contextMenu.addItem("Download Qtr 3 Expenses", event -> {
            Set<String> sections = new HashSet<>();
            for (DepartmentBudget f : budgetSummary.getDepartmentBudgets()) {
                String deptcode = f.getDepartmentCode();
                Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
                sections.addAll(sectionCodes);
            }
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sections);
            utils.exportAndDownloadExcelTransactionDetails(budget, utils.refreshgridTransactions2(3), this, 3);
        });
        contextMenu.addItem("Download Qtr 4 Expenses", event -> {
            Set<String> sections = new HashSet<>();
            for (DepartmentBudget f : budgetSummary.getDepartmentBudgets()) {
                String deptcode = f.getDepartmentCode();
                Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
                sections.addAll(sectionCodes);
            }
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sections);
            utils.exportAndDownloadExcelTransactionDetails(budget, utils.refreshgridTransactions2(), this, 4);
        });
        contextMenu.addItem("Download All Expenses", event -> {
            Set<String> sections = new HashSet<>();
            for (DepartmentBudget f : budgetSummary.getDepartmentBudgets()) {
                String deptcode = f.getDepartmentCode();
                Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
                sections.addAll(sectionCodes);
            }
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sections);
            utils.exportAndDownloadExcelTransactionDetails(budget, utils.refreshgridTransactions2(), this, 4);

        });
        spent.addDoubleClickListener(e -> {
            Set<String> sections = new HashSet<>();
            for (DepartmentBudget f : budgetSummary.getDepartmentBudgets()) {
                String deptcode = f.getDepartmentCode();
                Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
                sections.addAll(sectionCodes);
            }
            utils = new utilityActuals(budget, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sections);
            utils.createTransactionsDialog2(this);
        });
        add(
                //createSummaryCard("Total Budget", budgetSummary.getTotalBudget(), VaadinIcon.DIAMOND, "card-blue"),
                createQuarterlySummaryCard("Total Budget", budgetSummary.getTotalBudget(),budgetSummary.getCumQtr1Budget() ,budgetSummary.getCumQtr2Budget() ,budgetSummary.getCumQtr3Budget() ,budgetSummary.getCumQtr4Budget() ,VaadinIcon.DIAMOND, "card-blue"),
                createSummaryCard("Total Committed", budgetSummary.getTotalCommitted(), VaadinIcon.COIN_PILES, "card-red", String.format("%.1f%% of budget", budgetSummary.getCommittedPercentage())), 
                spent, 
                createSummaryCard("Remaining Budget", budgetSummary.getRemainingBudget(), VaadinIcon.MONEY_DEPOSIT, "card-green",                String.format("%.1f%% available", 100 - budgetSummary.getSpentPercentage())), 
                createSummaryCard("Revenue Collected", budgetSummary.getTotalRevenue(), VaadinIcon.TRENDING_UP, "card-emerald",                String.format("%.1f%% of target", budgetSummary.getRevenuePercentage()))
        );
    }

    private Div createSummaryCard(String title, double amount, VaadinIcon iconType,
            String cardClass) {
        return createSummaryCard(title, amount, iconType, cardClass, null);
    }

    private Div createSummaryCard(String title, double amount, VaadinIcon iconType,
            String cardClass, String subtitle) {
        Div card = new Div();
        card.addClassName("summary-card");
        card.addClassName(cardClass);
        card.setMinWidth("150px");
        card.setMaxWidth("170px");

        // Set flex grow on the parent layout instead
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

        H6 amountH3 = new H6(formatAmount(amount));
        amountH3.addClassName("card-amount");
        amountH3.getStyle().set("word-break", "break-word");

        textContent.add(titleSpan, amountH3);

        if (subtitle != null) {
            Span subtitleSpan = new Span(subtitle);
            subtitleSpan.addClassName("card-subtitle");
            textContent.add(subtitleSpan);
        }

        Div iconContainer = new Div();
        iconContainer.addClassName("card-icon");
        iconContainer.getStyle().set("flex-shrink", "0");
        Icon icon = new Icon(iconType);
        icon.setSize("1rem");
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
    
    private Div createQuarterlySummaryCard(
        String title,
        double total,
        double q1,
        double q2,
        double q3,
        double q4,
        VaadinIcon iconType,
        String cardClass
) {
    Div card = new Div();
    card.addClassName("summary-card");
    card.addClassName(cardClass);

    // Let CSS control sizing; avoid conflicting min/max unless you must
    // card.setMinWidth("200px");
    // card.setMaxWidth("280px");
    this.setFlexGrow(1, card);

    HorizontalLayout header = new HorizontalLayout();
    header.setWidthFull();
    header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    header.setAlignItems(FlexComponent.Alignment.CENTER);
    header.setPadding(false);
    header.setSpacing(false);

    VerticalLayout textContent = new VerticalLayout();
    textContent.setSpacing(false);
    textContent.setPadding(false);

    Span titleSpan = new Span(title);
    titleSpan.addClassName("card-title");

    H6 totalAmount = new H6(formatAmount(total));
    totalAmount.addClassName("card-amount");
    totalAmount.getStyle().set("word-break", "break-word");

    textContent.add(titleSpan, totalAmount);

    Div iconContainer = new Div();
    iconContainer.addClassName("card-icon");
    Icon icon = new Icon(iconType);
    icon.setSize("1rem");
    iconContainer.add(icon);

    header.add(textContent, iconContainer);

    // Breakdown (Q1-Q4)
    Div breakdown = new Div();
    breakdown.addClassName("card-breakdown");
    breakdown.add(
            breakdownRow("Q1 Budget", q1),
            breakdownRow("Q2 Budget", q2),
            breakdownRow("Q3 Budget", q3),
            breakdownRow("Q4 Budget", q4)
    );

    card.add(header, breakdown);

    // IMPORTANT: remove Java hover listeners; CSS already handles it elegantly
    return card;
}

private Div breakdownRow(String label, double value) {
    Div row = new Div();
    row.addClassName("breakdown-row");

    Span l = new Span(label);
    l.addClassName("breakdown-label");

    Span v = new Span(formatAmount(value));
    v.addClassName("breakdown-value");

    row.add(l, v);
    return row;
}

private String formatAmount(BigDecimal amount) {
    if (amount == null) amount = BigDecimal.ZERO;
    NumberFormat formatter = NumberFormat.getInstance(Locale.US);
    formatter.setMaximumFractionDigits(1);
    formatter.setMinimumFractionDigits(0);
    return "UGX " + formatter.format(amount);
}

    private String formatAmount(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return "UGX " + formatter.format(amount);
    }

}
