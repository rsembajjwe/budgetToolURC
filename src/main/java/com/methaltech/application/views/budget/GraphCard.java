package com.methaltech.application.views.budget;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsModule("./src/budget-graphs.js")
public class GraphCard extends Div {

    private final Div chartHost;
    private final H4 titleLabel;
    private final Span subtitleLabel;

    private String chartType;
    private String jsonConfig;

    public GraphCard(String title, String subtitle, String chartType, String jsonConfig) {
        addClassName("graph-card");
        setWidthFull();

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("graph-card-header");
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(false);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        VerticalLayout titleWrap = new VerticalLayout();
        titleWrap.setPadding(false);
        titleWrap.setSpacing(false);

        titleLabel = new H4();
        titleLabel.addClassName("graph-card-title");

        subtitleLabel = new Span();
        subtitleLabel.addClassName("graph-card-subtitle");

        titleWrap.add(titleLabel, subtitleLabel);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("graph-toolbar");
        toolbar.setPadding(false);
        toolbar.setSpacing(false);
        toolbar.setAlignItems(Alignment.CENTER);

        Button expandButton = iconButton(VaadinIcon.EXPAND_SQUARE, "Expand");
        expandButton.addClickListener(e -> openExpandedDialog());

        Button fullscreenButton = iconButton(VaadinIcon.EXPAND, "Fullscreen");
        fullscreenButton.addClickListener(e -> openFullscreenDialog());

        Button pngButton = iconButton(VaadinIcon.PICTURE, "Export PNG");
        pngButton.addClickListener(e -> exportPng());

        Anchor excelAnchor = buildExcelDownloadLink();
        excelAnchor.addClassName("graph-toolbar-link");

        Button imageButton = iconButton(VaadinIcon.DOWNLOAD_ALT, "Download Chart");
        imageButton.addClickListener(e -> downloadChartImage());

        toolbar.add(expandButton, fullscreenButton, pngButton, excelAnchor, imageButton);

        header.add(titleWrap, toolbar);

        chartHost = new Div();
        chartHost.setId("chart-" + UUID.randomUUID());
        chartHost.addClassName("graph-card-chart");
        chartHost.setWidthFull();

        add(header, chartHost);

        addDoubleClickListener(e -> openExpandedDialog());

        updateChart(title, subtitle, chartType, jsonConfig);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        renderChart(chartHost, chartType, jsonConfig);
    }

    public void updateChart(String title, String subtitle, String chartType, String jsonConfig) {
        this.chartType = chartType;
        this.jsonConfig = jsonConfig;

        titleLabel.setText(title == null ? "" : title);
        subtitleLabel.setText(subtitle == null ? "" : subtitle);
        subtitleLabel.setVisible(subtitle != null && !subtitle.isBlank());

        if (isAttached()) {
            renderChart(chartHost, chartType, jsonConfig);
        }
    }

    private Button iconButton(VaadinIcon iconType, String tooltip) {
        Button button = new Button(new Icon(iconType));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.addClassName("graph-toolbar-button");
        button.getElement().setProperty("title", tooltip);
        return button;
    }

    private Anchor buildExcelDownloadLink() {
        StreamResource resource = new StreamResource(
                safeFileName(titleLabel.getText()) + ".csv",
                () -> new ByteArrayInputStream(buildCsv().getBytes(StandardCharsets.UTF_8))
        );

        Anchor anchor = new Anchor(resource, "");
        Button excelButton = iconButton(VaadinIcon.TABLE, "Export Excel");
        excelButton.getElement().setProperty("title", "Export Excel/CSV");
        anchor.add(excelButton);
        anchor.getElement().setAttribute("download", true);
        return anchor;
    }

    private String buildCsv() {
        return "Title,Chart Type,Config\n"
                + escapeCsv(titleLabel.getText()) + ","
                + escapeCsv(chartType) + ","
                + escapeCsv(jsonConfig);
    }

    private String escapeCsv(String value) {
        String v = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }

    private String safeFileName(String value) {
        if (value == null || value.isBlank()) {
            return "chart";
        }
        return value.replaceAll("[^a-zA-Z0-9-_]+", "_").toLowerCase();
    }

    private void renderChart(Div host, String chartType, String jsonConfig) {
        if (chartType == null || jsonConfig == null || jsonConfig.isBlank()) {
            return;
        }

        host.getElement().executeJs("""
            if (window.renderBudgetChart) {
                window.renderBudgetChart(this, $0, JSON.parse($1));
            }
        """, chartType, jsonConfig);
    }

    private void openExpandedDialog() {
        openDialogWithHeight("1000px", "720px", 560);
    }

    private void openFullscreenDialog() {
        openDialogWithHeight("96vw", "92vh", 700);
    }

    private void openDialogWithHeight(String width, String height, int chartHeight) {
        if (chartType == null || jsonConfig == null || jsonConfig.isBlank()) {
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth(width);
        dialog.setHeight(height);
        dialog.addClassName("graph-zoom-dialog");

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(true);

        H4 dialogTitle = new H4(titleLabel.getText());
        dialogTitle.addClassName("graph-dialog-title");

        Span dialogSubtitle = new Span(subtitleLabel.getText());
        dialogSubtitle.addClassName("graph-dialog-subtitle");
        dialogSubtitle.setVisible(subtitleLabel.isVisible());

        Div enlargedChart = new Div();
        enlargedChart.setWidthFull();
        enlargedChart.setHeight(chartHeight + "px");
        enlargedChart.addClassName("graph-dialog-chart");

        HorizontalLayout dialogActions = new HorizontalLayout();
        dialogActions.setPadding(false);
        dialogActions.setSpacing(true);

        Button pngButton = new Button("PNG", e -> exportPng());
        Button imageButton = new Button("Image", e -> downloadChartImage());
        Button closeButton = new Button("Close", e -> dialog.close());

        dialogActions.add(pngButton, imageButton, closeButton);

        content.add(dialogTitle, dialogSubtitle, enlargedChart, dialogActions);
        content.expand(enlargedChart);

        dialog.add(content);

        dialog.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                String enlargedConfig = injectChartHeight(jsonConfig, chartHeight);
                renderChart(enlargedChart, chartType, enlargedConfig);
            }
        });

        dialog.open();
    }

    private void exportPng() {
        chartHost.getElement().executeJs("""
            if (window.exportBudgetChartPng) {
                window.exportBudgetChartPng(this, $0);
            }
        """, safeFileName(titleLabel.getText()));
    }

    private void downloadChartImage() {
        chartHost.getElement().executeJs("""
            if (window.downloadBudgetChartImage) {
                window.downloadBudgetChartImage(this, $0);
            }
        """, safeFileName(titleLabel.getText()));
    }

    private String injectChartHeight(String originalJson, int newHeight) {
        if (originalJson.contains("\"height\":")) {
            return originalJson.replaceAll("\"height\"\\s*:\\s*\\d+", "\"height\": " + newHeight);
        }
        int lastBrace = originalJson.lastIndexOf('}');
        if (lastBrace > 0) {
            return originalJson.substring(0, lastBrace)
                    + ", \"height\": " + newHeight
                    + originalJson.substring(lastBrace);
        }
        return originalJson;
    }
}
