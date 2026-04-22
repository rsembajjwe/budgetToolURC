package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.views.actual.utilityActuals;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
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
    private final String selectedQuarter;

    public BudgetSummaryCards(BudgetSummary budgetSummary,
            CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            SALFLDGService sampleSALFLDGService,
            DeptSectionMergerService sampleDeptSectionMergerService,
            Budget budget,
            String selectedQuarter) {

        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budget = budget;
        this.selectedQuarter = selectedQuarter;

        setWidthFull();
        setSpacing(false);
        setPadding(false);
        addClassName("budget-summary-cards");
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);

        getStyle().set("display", "flex");
        getStyle().set("flex-wrap", "wrap");
        getStyle().set("justify-content", "center");
        getStyle().set("align-items", "stretch");
        getStyle().set("gap", "1rem");
        getStyle().set("box-sizing", "border-box");

        BigDecimal selectedSpent = getSelectedSpent(budgetSummary);
        BigDecimal selectedSpentPct = percentage(selectedSpent, budgetSummary.getTotalBudget());
        BigDecimal selectedRemaining = nz(budgetSummary.getTotalBudget()).subtract(selectedSpent);
        double percentageRemaining = BigDecimal.valueOf(100)
                .subtract(selectedSpentPct)
                .max(BigDecimal.ZERO)
                .doubleValue();

        Div spent = createQuarterlySummaryCardActual(
                "1.4 Total Spent",
                selectedSpent,
                nz(budgetSummary.getCumQtr1Actual()),
                nz(budgetSummary.getCumQtr2Actual()),
                nz(budgetSummary.getCumQtr3Actual()),
                nz(budgetSummary.getCumQtr4Actual()),
                budgetSummary.getOpexActual().abs(),
                budgetSummary.getCapexActual().abs(),
                selectedRemaining,
                percentageRemaining,
                VaadinIcon.TRENDING_DOWN,
                "card-red",
                String.format("%.1f%% of budget", selectedSpentPct.doubleValue()),
                "Actual Exp."
        );

        ContextMenu contextMenu = new ContextMenu(spent);
        contextMenu.setOpenOnClick(false);

        contextMenu.addItem("Download Qtr 1 Expenses", event -> {
            Set<String> sections = collectSections(budgetSummary);
            utils = new utilityActuals(
                    budget, sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService, sections
            );
            utils.exportAndDownloadExcelTransactionDetails(
                    budget, utils.refreshgridTransactions2(1), this, 1
            );
        });

        contextMenu.addItem("Download Qtr 2 Expenses", event -> {
            Set<String> sections = collectSections(budgetSummary);
            utils = new utilityActuals(
                    budget, sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService, sections
            );
            utils.exportAndDownloadExcelTransactionDetails(
                    budget, utils.refreshgridTransactions2(2), this, 2
            );
        });

        contextMenu.addItem("Download Qtr 3 Expenses", event -> {
            Set<String> sections = collectSections(budgetSummary);
            utils = new utilityActuals(
                    budget, sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService, sections
            );
            utils.exportAndDownloadExcelTransactionDetails(
                    budget, utils.refreshgridTransactions2(3), this, 3
            );
        });

        contextMenu.addItem("Download Qtr 4 Expenses", event -> {
            Set<String> sections = collectSections(budgetSummary);
            utils = new utilityActuals(
                    budget, sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService, sections
            );
            utils.exportAndDownloadExcelTransactionDetails(
                    budget, utils.refreshgridTransactions2(), this, 4
            );
        });

        contextMenu.addItem("Download All Expenses", event -> {
            Set<String> sections = collectSections(budgetSummary);
            utils = new utilityActuals(
                    budget, sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService, sections
            );
            utils.exportAndDownloadExcelTransactionDetails(
                    budget, utils.refreshgridTransactions2(), this, 4
            );
        });

        spent.addDoubleClickListener(e -> {
            Set<String> sections = collectSections(budgetSummary);
            utils = new utilityActuals(
                    budget, sampleCoaService,
                    sampleUrcDeptSectionAnlDimbgtService,
                    sampleSALFLDGService, sections
            );
            utils.createTransactionsDialog2(this);
        });

        Div financialHighlights = createFinancialHighlightsCard(
                budgetSummary.getTotalBudget(),
                budgetSummary.getProjectedRevenue(),
                budgetSummary.getRevenueActual(),
                selectedSpent,
                budgetSummary.getBudgetPerformancePercentage(),
                selectedSpentPct
        );

        Div totalBudget = createQuarterlySummaryCard(
                "1.2 Total Budget",
                budgetSummary.getTotalBudget(),
                budgetSummary.getCumQtr1Budget(),
                budgetSummary.getCumQtr2Budget(),
                budgetSummary.getCumQtr3Budget(),
                budgetSummary.getCumQtr4Budget(),
                budgetSummary.getOpexBudget(),
                budgetSummary.getCapexBudget(),
                VaadinIcon.DIAMOND,
                "card-blue",
                "Approved budget",
                "Budget"
        );

        Div revenueCollected = createQuarterlySummaryCardRevenue(
                "1.3 Revenue Collected",
                budgetSummary.getRevenueActual(),
                budgetSummary.getIgrTotalActualRevenue(),
                budgetSummary.getGouTotalActualRevenue(),
                budgetSummary.getIgrTotalRevenueBudget(),
                budgetSummary.getGouTotalRevenueBudget(),
                budgetSummary.getProjectedRevenue(),
                VaadinIcon.TRENDING_UP,
                "card-emerald",
                String.format("%.1f%% of target",
                        percentage(budgetSummary.getRevenueActual(), budgetSummary.getProjectedRevenue()).doubleValue())
        );

        add(financialHighlights, totalBudget, revenueCollected, spent);
    }

    private BigDecimal getSelectedSpent(BudgetSummary budgetSummary) {
        if (budgetSummary == null) {
            return BigDecimal.ZERO;
        }

        if (selectedQuarter == null || selectedQuarter.isBlank()) {
            return nz(budgetSummary.getTotalSpent());
        }

        return switch (selectedQuarter.toLowerCase()) {
            case "qtr1" ->
                nz(budgetSummary.getCumQtr1Actual());
            case "qtr2" ->
                nz(budgetSummary.getCumQtr2Actual()); // cumulative = q1 + q2
            case "qtr3" ->
                nz(budgetSummary.getCumQtr3Actual()); // cumulative = q1 + q2 + q3
            case "qtr4" ->
                nz(budgetSummary.getCumQtr4Actual()); // cumulative full-year
            default ->
                nz(budgetSummary.getTotalSpent());
        };
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Set<String> collectSections(BudgetSummary budgetSummary) {
        Set<String> sections = new HashSet<>();
        for (DepartmentBudget f : budgetSummary.getDepartmentBudgets()) {
            String deptcode = f.getDepartmentCode();
            Set<String> sectionCodes = sampleDeptSectionMergerService.extractSectionAnlCodes(deptcode);
            sections.addAll(sectionCodes);
        }
        return sections;
    }

    private void styleCard(Div card) {
        card.addClassName("summary-card");
        card.setWidthFull();
        card.getStyle().set("box-sizing", "border-box");
        card.getStyle().set("flex", "1 1 320px");
        card.getStyle().set("min-width", "280px");
        card.getStyle().set("max-width", "100%");
    }

    private HorizontalLayout buildCardHeader(String title, VaadinIcon iconType) {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("summary-card-header");
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(false);
        header.setAlignItems(FlexComponent.Alignment.START);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout titleArea = new VerticalLayout();
        titleArea.setPadding(false);
        titleArea.setSpacing(false);
        titleArea.addClassName("summary-card-header-text");

        Span titleSpan = new Span(title);
        titleSpan.addClassName("card-title");

        titleArea.add(titleSpan);

        Div iconContainer = new Div();
        iconContainer.addClassName("card-icon");
        Icon icon = new Icon(iconType);
        icon.setSize("1rem");
        iconContainer.add(icon);

        header.add(titleArea, iconContainer);
        return header;
    }

    private Div buildMainValue(String amount, String subtitle) {
        Div section = new Div();
        section.addClassName("card-main-value");

        H4 amountText = new H4(amount);
        amountText.addClassName("card-amount");

        section.add(amountText);

        if (subtitle != null && !subtitle.isBlank()) {
            Span badge = new Span(subtitle);
            badge.addClassName("card-status-badge");
            section.add(badge);
        }

        return section;
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
            String cardClass,
            String subtitle,
            String head
    ) {
        Div card = new Div();
        styleCard(card);
        card.addClassName(cardClass);

        HorizontalLayout header = buildCardHeader(title, iconType);
        Div mainValue = buildMainValue(formatAmount(total), subtitle);

        Div breakdown = new Div();
        breakdown.addClassName("card-breakdown");
        breakdown.add(
                compactMetricRow("Q1", formatAmount(q1)),
                compactMetricRow("Q2", formatAmount(q2)),
                compactMetricRow("Q3", formatAmount(q3)),
                compactMetricRow("Q4", formatAmount(q4)),
                compactMetricRow("Opex", formatAmount(opexB), percentage(opexB, total) + "% of " + head),
                compactMetricRow("Capex", formatAmount(capexB), percentage(capexB, total) + "% of " + head)
        );

        card.add(header, mainValue, breakdown);
        return card;
    }

    private Div createQuarterlySummaryCardRevenue(
            String title,
            BigDecimal actualRevenue,
            BigDecimal igr,
            BigDecimal gouExt,
            BigDecimal projectedIgr,
            BigDecimal projectedGouExt,
            BigDecimal budgetRevenue,
            VaadinIcon iconType,
            String cardClass,
            String subtitle
    ) {
        Div card = new Div();
        styleCard(card);
        card.addClassName(cardClass);

        HorizontalLayout header = buildCardHeader(title, iconType);
        Div mainValue = buildMainValue(formatAmount(actualRevenue), subtitle);

        Div breakdown = new Div();
        breakdown.addClassName("card-breakdown");
        breakdown.add(
                compactMetricRow("IGR", formatAmount(igr)),
                compactMetricRow("IGR vs Budget", percentage(igr, projectedIgr) + "%"),
                compactMetricRow("IGR Share", percentage(igr, actualRevenue) + "% of actual"),
                compactMetricRow("GOU/External", formatAmount(gouExt)),
                compactMetricRow("GOU vs Budget", percentage(gouExt, projectedGouExt) + "%"),
                compactMetricRow("GOU Share", percentage(gouExt, actualRevenue) + "% of actual"),
                compactMetricRow("Vs Total Budget Revenue", percentage(actualRevenue, budgetRevenue) + "%")
        );

        card.add(header, mainValue, breakdown);
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
            String cardClass,
            String subtitle,
            String head
    ) {
        Div card = new Div();
        styleCard(card);
        card.addClassName(cardClass);

        HorizontalLayout header = buildCardHeader(title, iconType);
        Div mainValue = buildMainValue(formatAmount(nz(total)), subtitle);

        Div breakdown = new Div();
        breakdown.addClassName("card-breakdown");
        breakdown.add(
                compactMetricRow("Q1", formatAmount(nz(q1))),
                compactMetricRow("Q2", formatAmount(nz(q2)), "Cumulative"),
                compactMetricRow("Q3", formatAmount(nz(q3)), "Cumulative"),
                compactMetricRow("Q4", formatAmount(nz(q4)), "Cumulative"),
                compactMetricRow("Opex", formatAmount(nz(opexB)), percentage(opexB, total) + "% of actual"),
                compactMetricRow("Capex", formatAmount(nz(capexB)), percentage(capexB, total) + "% of actual"),
                compactMetricRow("Remaining", formatAmount(nz(remainingB)), String.format("%.2f%% of budget", percentageRemaining))
        );

        card.add(header, mainValue, breakdown);
        return card;
    }

    private Div createFinancialHighlightsCard(
            BigDecimal approvedBudget,
            BigDecimal projectedRevenue,
            BigDecimal actualRevenue,
            BigDecimal actualExpenditure,
            BigDecimal revenuePerformancePercent,
            BigDecimal absorptionRatePercent
    ) {
        BigDecimal netPosition = nz(actualRevenue).subtract(nz(actualExpenditure));
        boolean deficit = netPosition.compareTo(BigDecimal.ZERO) < 0;

        Div card = new Div();
        styleCard(card);
        card.addClassName("financial-card");

        HorizontalLayout header = buildCardHeader("1.1 Financial Highlights", VaadinIcon.TRENDING_UP);
        Div mainValue = buildMainValue(
                formatAmount(netPosition),
                deficit ? "Deficit position" : "Surplus position"
        );

        Div breakdown = new Div();
        breakdown.addClassName("card-breakdown");
        breakdown.add(
                compactMetricRow("Approved Budget", formatAmount(approvedBudget)),
                compactMetricRow("Income Projection", formatAmount(projectedRevenue)),
                compactMetricRow("Actual Revenue", formatAmount(actualRevenue), revenuePerformancePercent + "% performance"),
                compactMetricRow("Actual Expenditure", formatAmount(actualExpenditure), absorptionRatePercent + "% absorption"),
                compactMetricRow(
                        deficit ? "Net Deficit" : "Net Surplus",
                        formatAmount(netPosition),
                        deficit ? "Below balance" : "Above balance",
                        deficit ? "negative-metric" : "positive-metric"
                )
        );

        card.add(header, mainValue, breakdown);
        return card;
    }

    private Div compactMetricRow(String label, String value) {
        return compactMetricRow(label, value, null, null);
    }

    private Div compactMetricRow(String label, String value, String meta) {
        return compactMetricRow(label, value, meta, null);
    }

    private Div compactMetricRow(String label, String value, String meta, String extraClass) {
        Div row = new Div();
        row.addClassName("breakdown-row");
        if (extraClass != null && !extraClass.isBlank()) {
            row.addClassName(extraClass);
        }

        Div left = new Div();
        left.addClassName("breakdown-left");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("breakdown-label");
        left.add(labelSpan);

        if (meta != null && !meta.isBlank()) {
            Span metaSpan = new Span(meta);
            metaSpan.addClassName("breakdown-meta");
            left.add(metaSpan);
        }

        Span valueSpan = new Span(value);
        valueSpan.addClassName("breakdown-value");

        row.add(left, valueSpan);
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

    private BigDecimal percentage(BigDecimal part, BigDecimal total) {
        if (part == null) {
            part = BigDecimal.ZERO;
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return part.divide(total, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
