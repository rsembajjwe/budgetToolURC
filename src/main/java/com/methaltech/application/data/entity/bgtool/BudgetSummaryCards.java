package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
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
import java.math.RoundingMode;

import java.text.NumberFormat;
import java.util.HashSet;
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

        // Div spent = createSummaryCard("Total Spent", budgetSummary.getTotalSpent(), VaadinIcon.TRENDING_DOWN, "card-red", String.format("%.1f%% of budget", budgetSummary.getSpentPercentage()));
        Div spent = createQuarterlySummaryCardActual("1.4 Total Spent", budgetSummary.getTotalSpent(),
                budgetSummary.getCumQtr1Actual(), budgetSummary.getCumQtr2Actual(),
                budgetSummary.getCumQtr3Actual(), budgetSummary.getCumQtr4Actual(), budgetSummary.getOpexActual().abs(), budgetSummary.getCapexActual().abs(), budgetSummary.getRemainingBudget(), 100 - budgetSummary.getSpentPercentage().doubleValue(),
                VaadinIcon.TRENDING_DOWN, "card-red", String.format("%.1f%% of budget",
                        budgetSummary.getSpentPercentage()), "Actual Exp.");
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
        /*            BigDecimal actualRevenue,//Actual Revenue
        BigDecimal igr,//IGR Revenue
        BigDecimal gou_Ext,//GOU&EXTERNAL
        BigDecimal projectedIgr,
        BigDecimal projectedgou_Ext,*/
        Div financialHighlights = createFinancialHighlightsCard(
                budgetSummary.getTotalBudget(), // Approved Budget
                budgetSummary.getProjectedRevenue(), // projectedRevenue
                budgetSummary.getRevenueActual(), // actualRevenue // 
                budgetSummary.getTotalSpent(), // actualExpenditure
                budgetSummary.getBudgetPerformancePercentage(), // Revenue Performance %
                budgetSummary.getAbsorptionRatePercentage() // Absorption %
        );
        add(financialHighlights);
        add(
                createQuarterlySummaryCard("1.2 Total Budget", budgetSummary.getTotalBudget(), budgetSummary.getCumQtr1Budget(), budgetSummary.getCumQtr2Budget(), budgetSummary.getCumQtr3Budget(), budgetSummary.getCumQtr4Budget(), budgetSummary.getOpexBudget(), budgetSummary.getCapexBudget(), VaadinIcon.DIAMOND, "card-blue", null, "Budget"),
                createQuarterlySummaryCardRevenue("1.3 Revenue Collected", budgetSummary.getRevenueActual(),budgetSummary.getIgrTotalActualRevenue(),budgetSummary.getGouTotalActualRevenue(),budgetSummary.getIgrTotalRevenueBudget(),budgetSummary.getGouTotalRevenueBudget(), budgetSummary.getProjectedRevenue(),VaadinIcon.TRENDING_UP, "card-emerald", String.format("%.1f%% of target", percentage(budgetSummary.getRevenueActual(), budgetSummary.getProjectedRevenue()))),
                spent
        );
    }

    private Div createSummaryCard(String title, BigDecimal amount, VaadinIcon iconType,
            String cardClass) {
        return createSummaryCard(title, amount, iconType, cardClass, null);
    }

    private Div createSummaryCard(String title, BigDecimal amount, VaadinIcon iconType,
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
        content.setAlignItems(FlexComponent.Alignment.START);

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
            BigDecimal total,
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4,
            BigDecimal opexB,
            BigDecimal capexB,
            VaadinIcon iconType,
            String cardClass, String subtitle, String head
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

        if (subtitle != null) {
            Span subtitleSpan = new Span(subtitle);
            subtitleSpan.addClassName("card-subtitle");
            textContent.add(subtitleSpan);
        }

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
                breakdownRow("Q1.", q1),
                breakdownRow("Q2.", q2),
                breakdownRow("Q3.", q3),
                breakdownRow("Q4.", q4),
                metricRow("Opex Exp.", opexB, percentage(opexB, total) + "% of " + head),
                metricRow("Capex Exp.", capexB, percentage(capexB, total) + "% of " + head)
        );

        card.add(header, breakdown);

        // IMPORTANT: remove Java hover listeners; CSS already handles it elegantly
        return card;
    }
    
        private Div createQuarterlySummaryCardRevenue(
            String title,
            BigDecimal actualRevenue,//Actual Revenue
            BigDecimal igr,//IGR Revenue
            BigDecimal gou_Ext,//GOU&EXTERNAL
            BigDecimal projectedIgr,
            BigDecimal projectedgou_Ext,
            BigDecimal budgetRevenue,
            VaadinIcon iconType,
            String cardClass, String subtitle
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

        H6 totalAmount = new H6(formatAmount(actualRevenue));
        totalAmount.addClassName("card-amount");
        totalAmount.getStyle().set("word-break", "break-word");

        textContent.add(titleSpan, totalAmount);

        if (subtitle != null) {
            Span subtitleSpan = new Span(subtitle);
            subtitleSpan.addClassName("card-subtitle");
            textContent.add(subtitleSpan);
        }

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
                metricRow("IGR.", igr, percentage(igr, projectedIgr) + "% of IGR Revenue Budget | " + percentage(igr, actualRevenue)+ "% of Total Actual Revenue | "+ percentage(igr, budgetRevenue)+ "% of Total Budgeted Revenue"),
                metricRow("GOU & EXTERNAL.", gou_Ext, percentage(gou_Ext, projectedgou_Ext) + "% of GOU & External Funding Budget | " + percentage(gou_Ext, actualRevenue)+ "% of Total Actual Revenue | "+ percentage(gou_Ext, budgetRevenue)+ "% of Total Budgeted Revenue")
        );

        card.add(header, breakdown);

        // IMPORTANT: remove Java hover listeners; CSS already handles it elegantly
        return card;
    }

    private Div createQuarterlySummaryCardActual(
            String title,
            BigDecimal total,
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4,
            BigDecimal opexB,
            BigDecimal capexB,
            BigDecimal remainingB,
            double percentageRemaining,
            VaadinIcon iconType,
            String cardClass, String subtitle, String head
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

        if (subtitle != null) {
            Span subtitleSpan = new Span(subtitle);
            subtitleSpan.addClassName("card-subtitle");
            textContent.add(subtitleSpan);
        }

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
                breakdownRow("Q1.", q1),
                breakdownRow("Q2.", q2),
                breakdownRow("Q3.", q3),
                breakdownRow("Q4.", q4),
                metricRow("Opex Exp.", opexB, percentage(opexB, total) + "% of Actaul Budget"),
                metricRow("Capex Exp.", capexB, percentage(capexB, total) + "% of Actaul Budget"),
                metricRow("Remaining Budget.", remainingB, percentageRemaining + "% of Budget")
        );

        card.add(header, breakdown);

        // IMPORTANT: remove Java hover listeners; CSS already handles it elegantly
        return card;
    }

    private Div breakdownRow(String label, BigDecimal value) {
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
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
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

    private Div createFinancialHighlightsCard(
            BigDecimal approvedBudget,
            BigDecimal projectedRevenue,
            BigDecimal actualRevenue,
            BigDecimal actualExpenditure,
            BigDecimal revenuePerformancePercent,
            BigDecimal absorptionRatePercent
    ) {

        // BigDecimal totalRevenue = igr.add(gou).add(external);
        BigDecimal netPosition = actualRevenue.subtract(actualExpenditure);

        boolean deficit = netPosition.compareTo(BigDecimal.ZERO) < 0;

        Div card = new Div();
        card.addClassName("summary-card");
        card.addClassName("financial-card");
        this.setFlexGrow(1, card);

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);

        VerticalLayout titleLayout = new VerticalLayout();
        titleLayout.setSpacing(false);
        titleLayout.setPadding(false);

        Span title = new Span("1.1 Financial Highlights");
        title.addClassName("card-title");

        titleLayout.add(title);

        Div iconContainer = new Div();
        iconContainer.addClassName("card-icon");
        iconContainer.add(new Icon(VaadinIcon.TRENDING_UP));

        header.add(titleLayout, iconContainer);

        // Body Metrics
        Div body = new Div();
        body.addClassName("card-breakdown");

        body.add(
                metricRow("Approved Expenditure Budget", approvedBudget),
                metricRow("Approved Income Projection", projectedRevenue),
                metricRow("Total Actual Revenue", actualRevenue,
                        revenuePerformancePercent + "% performance"),
                metricRow("Total Actual Expenditure", actualExpenditure,
                        absorptionRatePercent + "% absorption"),
                netPositionRow(deficit ? "Net Position (Deficit)" : "Net Position (Surplus)",
                        netPosition, deficit)
        );

        card.add(header, body);
        return card;
    }

    private Div metricRow(String label, BigDecimal value) {
        return metricRow(label, value, null);
    }

    private Div metricRow(String label, BigDecimal value, String subtitle) {
        Div row = new Div();
        row.addClassName("breakdown-row");

        VerticalLayout text = new VerticalLayout();
        text.setPadding(false);
        text.setSpacing(false);

        Span l = new Span(label);
        l.addClassName("breakdown-label");

        Span v = new Span(formatAmount(value));
        v.addClassName("breakdown-value");

        text.add(l, v);

        if (subtitle != null) {
            Span sub = new Span(subtitle);
            sub.addClassName("card-subtitle");
            text.add(sub);
        }

        row.add(text);
        return row;
    }

    private Div netPositionRow(String label, BigDecimal value, boolean deficit) {
        Div row = metricRow(label, value);
        row.addClassName(deficit ? "negative-metric" : "positive-metric");
        return row;
    }

    private BigDecimal percentage(BigDecimal part, BigDecimal total) {

        if (part == null) {
            part = BigDecimal.ZERO;
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return part
                .divide(total, 6, RoundingMode.HALF_UP) // intermediate precision
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);       // final display scale
    }

}
