package com.methaltech.application.views.budget;

import com.methaltech.application.data.entity.bgtool.BudgetSummary;
import com.vaadin.flow.component.html.Div;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetVisualCards extends Div {

    private final GraphCard utilizationCard;
    private final GraphCard revenuePerformanceCard;
    private final GraphCard opexCapexCard;
    private final GraphCard quarterlyActualsCard;

    public BudgetVisualCards(BudgetSummary summary) {
        addClassName("budget-visual-cards");
        setWidthFull();

        utilizationCard = new GraphCard("", "", "radialBar", "{}");
        revenuePerformanceCard = new GraphCard("", "", "radialBar", "{}");
        opexCapexCard = new GraphCard("", "", "donut", "{}");
        quarterlyActualsCard = new GraphCard("", "", "bar", "{}");

        add(utilizationCard, revenuePerformanceCard, opexCapexCard, quarterlyActualsCard);

        refresh(summary);
    }

public void refresh(BudgetSummary summary) {
    utilizationCard.updateChart(
            "Budget Utilization",
            formatPercent(percentage(summary.getTotalSpent(), summary.getTotalBudget())) + " used",
            "radialBar",
            createRadialConfig(
                    percentage(summary.getTotalSpent(), summary.getTotalBudget()).doubleValue(),
                    "Used",
                    "#ef4444"
            )
    );

    revenuePerformanceCard.updateChart(
            "Revenue Performance",
            formatPercent(percentage(summary.getRevenueActual(), summary.getProjectedRevenue())) + " achieved",
            "radialBar",
            createRadialConfig(
                    percentage(summary.getRevenueActual(), summary.getProjectedRevenue()).doubleValue(),
                    "Revenue",
                    "#10b981"
            )
    );

    opexCapexCard.updateChart(
            "Opex vs Capex",
            "Actual expenditure split",
            "donut",
            createDonutConfig(
                    new double[]{
                            n(summary.getOpexActual()),
                            n(summary.getCapexActual())
                    },
                    new String[]{"Opex", "Capex"},
                    new String[]{"#3b82f6", "#8b5cf6"}
            )
    );

    quarterlyActualsCard.updateChart(
            "Quarterly Actuals",
            "Actual expenditure by quarter",
            "bar",
            createBarConfig(
                    new String[]{"Q1", "Q2", "Q3", "Q4"},
                    new double[]{
                            n(summary.getCumQtr1Actual()),
                            n(summary.getCumQtr2Actual()),
                            n(summary.getCumQtr3Actual()),
                            n(summary.getCumQtr4Actual())
                    },
                    "Actuals"
            )
    );
}

private String createRadialConfig(double value, String label, String color) {
    double safeValue = Math.max(0, Math.min(100, value));

    return """
    {
      "series": [%s],
      "labels": ["%s"],
      "colors": ["%s"],
      "height": 220,
      "plotOptions": {
        "radialBar": {
          "hollow": { "size": "62%%" },
          "track": { "background": "#eef2f7" },
          "dataLabels": {
            "name": { "fontSize": "14px" },
            "value": {
              "fontSize": "22px",
              "fontWeight": 700
            }
          }
        }
      }
    }
    """.formatted(safeValue, escape(label), escape(color));
}

    private String createDonutConfig(double[] values, String[] labels, String[] colors) {
        return """
        {
          "series": [%s],
          "labels": [%s],
          "colors": [%s],
          "height": 220,
          "legend": { "position": "bottom" },
          "plotOptions": {
            "pie": {
              "donut": { "size": "62%%" }
            }
          }
        }
        """.formatted(
                joinNumbers(values),
                joinQuoted(labels),
                joinQuoted(colors)
        );
    }

    private String createBarConfig(String[] categories, double[] values, String seriesName) {
        return """
        {
          "series": [{
            "name": "%s",
            "data": [%s]
          }],
          "xaxis": {
            "categories": [%s]
          },
          "height": 220,
          "plotOptions": {
            "bar": {
              "borderRadius": 6,
              "columnWidth": "45%%"
            }
          }
        }
        """.formatted(
                escape(seriesName),
                joinNumbers(values),
                joinQuoted(categories)
        );
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

    private double n(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private String formatPercent(BigDecimal value) {
        return value == null ? "0.0%" : value.setScale(1, RoundingMode.HALF_UP) + "%";
    }

    private String joinNumbers(double[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }

    private String joinQuoted(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("\"").append(escape(values[i])).append("\"");
        }
        return sb.toString();
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
