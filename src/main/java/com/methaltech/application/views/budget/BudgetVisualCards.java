package com.methaltech.application.views.budget;

import com.methaltech.application.data.entity.bgtool.BudgetSummary;
import com.vaadin.flow.component.html.Div;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetVisualCards extends Div {

    private final GraphCard utilizationCard;
    private final GraphCard revenuePerformanceCard;
    private final GraphCard netPositionCard;
    private final GraphCard expenditureMixCard;
    private final GraphCard revenueCompositionCard;
    private final GraphCard quarterlyActualsCard;

    public BudgetVisualCards(BudgetSummary summary) {
        addClassName("budget-visual-cards");
        setWidthFull();

        utilizationCard = new GraphCard("", "", "radialBar", "{}");
        revenuePerformanceCard = new GraphCard("", "", "radialBar", "{}");
        netPositionCard = new GraphCard("", "", "bar", "{}");
        expenditureMixCard = new GraphCard("", "", "donut", "{}");
        revenueCompositionCard = new GraphCard("", "", "donut", "{}");
        quarterlyActualsCard = new GraphCard("", "", "bar", "{}");

        add(
                utilizationCard,
                revenuePerformanceCard,
                netPositionCard,
                expenditureMixCard,
                revenueCompositionCard,
                quarterlyActualsCard
        );

        refresh(summary);
    }

    public void refresh(BudgetSummary summary) {
        BigDecimal totalSpent = nz(summary.getTotalSpent()).abs();
        BigDecimal totalBudget = nz(summary.getTotalBudget()).abs();
        BigDecimal remainingBudget = nz(summary.getRemainingBudget()).abs();

        BigDecimal revenueActual = nz(summary.getRevenueActual()).abs();
        BigDecimal projectedRevenue = nz(summary.getProjectedRevenue()).abs();

        BigDecimal opexActual = nz(summary.getOpexActual()).abs();
        BigDecimal capexActual = nz(summary.getCapexActual()).abs();

        BigDecimal igrActual = nz(summary.getIgrTotalActualRevenue()).abs();
        BigDecimal gouActual = nz(summary.getGouTotalActualRevenue()).abs();

        BigDecimal q1 = nz(summary.getCumQtr1Actual()).abs();
        BigDecimal q2 = nz(summary.getCumQtr2Actual()).abs();
        BigDecimal q3 = nz(summary.getCumQtr3Actual()).abs();
        BigDecimal q4 = nz(summary.getCumQtr4Actual()).abs();

        BigDecimal netPosition = revenueActual.subtract(totalSpent);

        utilizationCard.updateChart(
                "Budget Utilization",
                formatPercent(percentage(totalSpent, totalBudget)) + " used",
                "radialBar",
                createRadialConfig(
                        percentage(totalSpent, totalBudget).doubleValue(),
                        "Used",
                        "#ef4444"
                )
        );

        revenuePerformanceCard.updateChart(
                "Revenue Performance",
                formatPercent(percentage(revenueActual, projectedRevenue)) + " achieved",
                "radialBar",
                createRadialConfig(
                        percentage(revenueActual, projectedRevenue).doubleValue(),
                        "Revenue",
                        "#10b981"
                )
        );

        netPositionCard.updateChart(
                "Net Position",
                netPosition.signum() < 0 ? "Deficit" : "Surplus",
                "bar",
                createComparisonBarConfig(
                        new String[]{"Revenue", "Expenditure"},
                        new double[]{toMillions(revenueActual), toMillions(totalSpent)},
                        new double[]{revenueActual.doubleValue(), totalSpent.doubleValue()},
                        "UGX (M)"
                )
        );

        expenditureMixCard.updateChart(
                "Expenditure Mix",
                "Opex vs Capex",
                "donut",
                createDonutConfigSafe(
                        new double[]{opexActual.doubleValue(), capexActual.doubleValue()},
                        new String[]{"Opex", "Capex"},
                        new String[]{"#3b82f6", "#8b5cf6"}
                )
        );

        revenueCompositionCard.updateChart(
                "Revenue Composition",
                "IGR vs GOU/External",
                "donut",
                createDonutConfigSafe(
                        new double[]{igrActual.doubleValue(), gouActual.doubleValue()},
                        new String[]{"IGR", "GOU/External"},
                        new String[]{"#10b981", "#f59e0b"}
                )
        );

        quarterlyActualsCard.updateChart(
                "Quarterly Expenditure",
                "Actual expenditure by quarter",
                "bar",
                createBarConfig(
                        new String[]{"Q1", "Q2", "Q3", "Q4"},
                        new double[]{toMillions(q1), toMillions(q2), toMillions(q3), toMillions(q4)},
                        new double[]{q1.doubleValue(), q2.doubleValue(), q3.doubleValue(), q4.doubleValue()},
                        "Actuals (M)"
                )
        );
    }

    private double toMillions(BigDecimal value) {
        return nz(value)
                .divide(BigDecimal.valueOf(1_000_000), 2, RoundingMode.HALF_UP)
                .doubleValue();
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
                "value": { "fontSize": "22px", "fontWeight": 700 }
              }
            }
          }
        }
        """.formatted(safeValue, escape(label), escape(color));
    }

    private String createDonutConfigSafe(double[] values, String[] labels, String[] colors) {
        boolean allZero = true;
        for (double v : values) {
            if (v > 0) {
                allZero = false;
                break;
            }
        }

        double[] safeValues = allZero ? new double[]{1, 1} : values;
        String[] safeColors = allZero ? new String[]{"#cbd5e1", "#e2e8f0"} : colors;

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
                joinNumbers(safeValues),
                joinQuoted(labels),
                joinQuoted(safeColors)
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

    private String createBarConfig(String[] categories, double[] displayValues, double[] fullValues, String seriesName) {
        return """
    {
      "series": [{
        "name": "%s",
        "data": [%s]
      }],
      "xaxis": {
        "categories": [%s]
      },
      "yaxis": {
        "title": {
          "text": "UGX (Millions)"
        }
      },
      "fullValues": [%s],
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
                joinNumbers(displayValues),
                joinQuoted(categories),
                joinNumbers(fullValues)
        );
    }

    private String createComparisonBarConfig(String[] categories, double[] values, String seriesName) {
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
              "horizontal": true,
              "borderRadius": 6,
              "barHeight": "45%%"
            }
          }
        }
        """.formatted(
                escape(seriesName),
                joinNumbers(values),
                joinQuoted(categories)
        );
    }

    private String createComparisonBarConfig(String[] categories, double[] displayValues, double[] fullValues, String seriesName) {
        return """
    {
      "series": [{
        "name": "%s",
        "data": [%s]
      }],
      "xaxis": {
        "categories": [%s],
        "title": {
          "text": "UGX (Millions)"
        }
      },
      "chartUnit": "money",
      "fullValues": [%s],
      "height": 220,
      "plotOptions": {
        "bar": {
          "horizontal": true,
          "borderRadius": 6,
          "barHeight": "45%%"
        }
      }
    }
    """.formatted(
                escape(seriesName),
                joinNumbers(displayValues),
                joinQuoted(categories),
                joinNumbers(fullValues)
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

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
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
