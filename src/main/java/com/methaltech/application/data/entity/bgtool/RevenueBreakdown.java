package com.methaltech.application.data.entity.bgtool;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RevenueBreakdown extends VerticalLayout {

    private final NumberFormat currencyFormat;

    public RevenueBreakdown(List<RevenueSource> revenueSources) {
        // Create UGX currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

        setWidthFull();
        setPadding(false);
        setSpacing(true);
        addClassName("revenue-breakdown");

        createHeader();
        createContent(revenueSources);
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);

        VerticalLayout headerText = new VerticalLayout();
        headerText.setSpacing(false);
        headerText.setPadding(false);

        H2 title = new H2("Revenue Sources");
        title.addClassName("section-title");

        Span subtitle = new Span("Income breakdown by source category");
        subtitle.addClassName("section-subtitle");

        headerText.add(title, subtitle);

        Icon pieIcon = new Icon(VaadinIcon.PIE_CHART);
        pieIcon.addClassName("section-icon");

        header.add(headerText, pieIcon);
        add(header);
    }

    private void createContent(List<RevenueSource> revenueSources) {
        FlexLayout contentGrid2 = new FlexLayout();
        contentGrid2.setWidthFull();
        contentGrid2.setFlexWrap(FlexLayout.FlexWrap.WRAP); // Ensure it wraps on smaller screens

        contentGrid2.setAlignItems(FlexComponent.Alignment.START);
        contentGrid2.getStyle().set("gap", "2rem");

        // Revenue sources list
        VerticalLayout sourcesList = new VerticalLayout();
        sourcesList.setSpacing(true);
        sourcesList.setPadding(false);
        sourcesList.addClassName("sources-list");
        sourcesList.getStyle().set("margin-right", "1rem");

        for (RevenueSource source : revenueSources) {
            sourcesList.add(createRevenueSourceCard(source, revenueSources));
        }

        // Summary panel
        Div summaryPanel = createSummaryPanel(revenueSources);
        summaryPanel.addClassName("summary-panel");
        summaryPanel.getStyle().set("margin-left", "1rem");

        sourcesList.setWidth("48%");
        summaryPanel.setWidth("48%");

        contentGrid2.add(sourcesList, summaryPanel);

        // Control flex grow
        contentGrid2.setFlexGrow(2, sourcesList);
        contentGrid2.setFlexGrow(1, summaryPanel);

        add(contentGrid2);
    }

    private Div createRevenueSourceCard(RevenueSource source, List<RevenueSource> allSources) {
        Div card = new Div();
        card.addClassName("revenue-source-card");
        card.setWidthFull();

        // Card header with icon and trend
        HorizontalLayout cardHeader = new HorizontalLayout();
        cardHeader.setWidthFull();
        cardHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        cardHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        cardHeader.setPadding(false);
        cardHeader.setSpacing(false);

        HorizontalLayout sourceInfo = new HorizontalLayout();
        sourceInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        sourceInfo.setPadding(false);
        sourceInfo.setSpacing(false);

        Span iconSpan = new Span(source.getCategoryIcon());
        iconSpan.addClassName("revenue-icon");

        VerticalLayout sourceDetails = new VerticalLayout();
        sourceDetails.setSpacing(false);
        sourceDetails.setPadding(false);

        H3 sourceName = new H3(source.getName());
        sourceName.addClassName("source-name");

        Span sourceCategory = new Span(capitalizeFirst(source.getCategory()));
        sourceCategory.addClassName("source-category");

        sourceDetails.add(sourceName, sourceCategory);
        sourceInfo.add(iconSpan, sourceDetails);

        // Percentage indicator
        HorizontalLayout percentageInfo = new HorizontalLayout();
        percentageInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        percentageInfo.setPadding(false);
        percentageInfo.setSpacing(false);

        Icon trendIcon = new Icon(VaadinIcon.ARROW_UP);
        trendIcon.addClassName("trend-icon");

        Span percentage = new Span(String.format("%.1f%%", source.getProjectedPercentage()));
        percentage.addClassName("percentage-text");

        percentageInfo.add(trendIcon, percentage);
        cardHeader.add(sourceInfo, percentageInfo);

        // Amount details
        VerticalLayout amountDetails = new VerticalLayout();
        amountDetails.setSpacing(false);
        amountDetails.setPadding(false);
        amountDetails.setWidthFull();

        HorizontalLayout collectedRow = new HorizontalLayout();
        collectedRow.setWidthFull();
        collectedRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        collectedRow.setPadding(false);
        collectedRow.setSpacing(false);

        Span collectedLabel = new Span("Collected");
        collectedLabel.getStyle().set("color", "var(--gray-600)").set("font-size", "0.875rem");

        Span collectedAmount = new Span(formatCurrency(source.getAmount()));
        collectedAmount.getStyle().set("font-weight", "600");

        collectedRow.add(collectedLabel, collectedAmount);

        HorizontalLayout projectedRow = new HorizontalLayout();
        projectedRow.setWidthFull();
        projectedRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        projectedRow.setPadding(false);
        projectedRow.setSpacing(false);

        Span projectedLabel = new Span("Projected");
        projectedLabel.getStyle().set("color", "var(--gray-600)").set("font-size", "0.875rem");

        Span projectedAmount = new Span(formatCurrency(source.getProjected()));
        projectedAmount.getStyle().set("font-weight", "600");

        projectedRow.add(projectedLabel, projectedAmount);

        // Enhanced progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setValue(Math.min(source.getProjectedPercentage().doubleValue() / 100.0, 1.0));
        progressBar.getStyle()
                .set("--vaadin-progress-value-color", source.getColor())
                .set("height", "8px")
                .set("border-radius", "var(--radius-full)");

        amountDetails.add(collectedRow, projectedRow, progressBar);

        card.add(cardHeader, amountDetails);

        // Add hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getElement().getStyle().set("transform", "translateY(-2px)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getElement().getStyle().set("transform", "translateY(0)");
        });

        return card;
    }

    private Div createSummaryPanel(List<RevenueSource> revenueSources) {
        Div panel = new Div();
        panel.addClassName("summary-panel");

        H3 title = new H3("Revenue Summary");
        title.addClassName("summary-title");

        BigDecimal totalRevenue = revenueSources.stream()
                .map(RevenueSource::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProjected = revenueSources.stream()
                .map(RevenueSource::getProjected)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        VerticalLayout summaryContent = new VerticalLayout();
        summaryContent.setSpacing(true);
        summaryContent.setPadding(false);
        //summaryContent.setWidthFull();

        // Summary rows with enhanced styling
        summaryContent.add(
                createSummaryRow("Total Collected", totalRevenue, true, false),
                createSummaryRow("Total Projected", totalProjected, true, false)
        );

        // Collection rate with special styling
        BigDecimal collectionRate = percentage(totalRevenue, totalProjected);

        HorizontalLayout collectionRateRow
                = createSummaryRow("Collection Rate", collectionRate, false, true);
        collectionRateRow.getStyle()
                .set("border-top", "1px solid var(--gray-300)")
                .set("padding-top", "var(--space-4)")
                .set("margin-top", "var(--space-4)");

        summaryContent.add(collectionRateRow);

        // Distribution section
        H4 distributionTitle = new H4("Distribution");
        distributionTitle.addClassName("distribution-title");

        VerticalLayout distributionContent = new VerticalLayout();
        distributionContent.setSpacing(false);
        distributionContent.setPadding(false);
        distributionContent.setWidthFull();

        for (RevenueSource source : revenueSources) {
            double percentage = (source.getAmount().doubleValue() / totalRevenue.doubleValue()) * 100;
            distributionContent.add(createDistributionRow(source, percentage));
        }

        panel.add(title, summaryContent, distributionTitle, distributionContent);
        return panel;
    }

    private BigDecimal percentage(BigDecimal part, BigDecimal total) {

        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return part
                .divide(total, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private HorizontalLayout createSummaryRow(String label, BigDecimal value, boolean isCurrency, boolean isPercentage) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setPadding(false);
        row.setSpacing(false);

        Span labelSpan = new Span(label);
        labelSpan.addClassName("summary-label");

        String valueText;
        if (isPercentage) {
            valueText = String.format("%.1f%%", value);
        } else if (isCurrency) {
            valueText = formatCurrency(value);
        } else {
            valueText = String.valueOf(value);
        }

        Span valueSpan = new Span(valueText);
        valueSpan.addClassName("summary-value");

        if (isPercentage) {
            valueSpan.getStyle().set("color", "var(--secondary-600)");
        }

        row.add(labelSpan, valueSpan);
        return row;
    }

    private HorizontalLayout createDistributionRow(RevenueSource source, double percentage) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setPadding(false);
        row.setSpacing(false);

        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setPadding(false);
        leftSide.setSpacing(false);

        Div colorDot = new Div();
        colorDot.addClassName("color-dot");
        colorDot.getStyle().set("background-color", source.getColor());

        Span nameSpan = new Span(source.getName());
        nameSpan.addClassName("distribution-name");

        leftSide.add(colorDot, nameSpan);

        Span percentageSpan = new Span(String.format("%.1f%%", percentage));
        percentageSpan.addClassName("distribution-percentage");

        row.add(leftSide, percentageSpan);
        return row;
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        return "UGX " + formatter.format(amount);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
