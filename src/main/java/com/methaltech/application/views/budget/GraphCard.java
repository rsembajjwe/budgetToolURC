package com.methaltech.application.views.budget;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;

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

        Div header = new Div();
        header.addClassName("graph-card-header");

        titleLabel = new H4();
        titleLabel.addClassName("graph-card-title");

        subtitleLabel = new Span();
        subtitleLabel.addClassName("graph-card-subtitle");

        header.add(titleLabel, subtitleLabel);

        chartHost = new Div();
        chartHost.setId("chart-" + UUID.randomUUID());
        chartHost.addClassName("graph-card-chart");
        chartHost.setWidthFull();

        add(header, chartHost);

        updateChart(title, subtitle, chartType, jsonConfig);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        renderChart();
    }

    public void updateChart(String title, String subtitle, String chartType, String jsonConfig) {
        this.chartType = chartType;
        this.jsonConfig = jsonConfig;

        titleLabel.setText(title == null ? "" : title);
        subtitleLabel.setText(subtitle == null ? "" : subtitle);
        subtitleLabel.setVisible(subtitle != null && !subtitle.isBlank());

        if (isAttached()) {
            renderChart();
        }
    }

    private void renderChart() {
        if (chartType == null || jsonConfig == null || jsonConfig.isBlank()) {
            return;
        }

        chartHost.getElement().executeJs("""
            if (window.renderBudgetChart) {
                window.renderBudgetChart(this, $0, JSON.parse($1));
            } else {
                console.error("renderBudgetChart is not available");
            }
        """, chartType, jsonConfig);
    }
}
