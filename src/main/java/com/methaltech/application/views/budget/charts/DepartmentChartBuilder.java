package com.methaltech.application.views.budget.charts;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DepartmentChartBuilder {

    public static Image buildDepartmentBarChart(List<DepartmentBudget> departments) throws IOException {

        int width = 1100;
        int height = 550;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            int left = 100;
            int right = 40;
            int top = 90;
            int bottom = 140;

            int chartHeight = height - top - bottom;
            int chartWidth = width - left - right;

            int baseY = top + chartHeight;

            // ================= HEADER =================
            g.setColor(new Color(30, 30, 30));

            // g.setFont(new Font("SansSerif", Font.BOLD, 20));
            //g.drawString("UGANDA RAILWAYS CORPORATION", left, 30);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Cost Centre Budget Analysis", left, 55);

            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new Color(90, 90, 90));
            g.drawString("Budget vs Actual Expenditure Bar Chart", left, 75);

            // ================= DATA =================
            BigDecimal max = BigDecimal.ZERO;
            for (DepartmentBudget d : departments) {
                max = max.max(nz(d.getTotalBudget()));
                max = max.max(nz(d.getTotalSpent()));
            }

            if (max.compareTo(BigDecimal.ZERO) == 0) {
                max = BigDecimal.ONE;
            }

            int n = Math.max(departments.size(), 1);
            int groupWidth = chartWidth / n;
            int barWidth = Math.max(12, groupWidth / 3);

            Color budgetColor = new Color(66, 133, 244);
            Color spentColor = new Color(219, 68, 55);

            // ================= AXIS =================
            g.setColor(new Color(200, 200, 200));
            g.drawLine(left, baseY, left + chartWidth, baseY);

            // ================= BARS =================
            for (int i = 0; i < departments.size(); i++) {

                DepartmentBudget d = departments.get(i);

                BigDecimal budget = nz(d.getTotalBudget());
                BigDecimal spent = nz(d.getTotalSpent());

                int x = left + i * groupWidth + groupWidth / 2;

                int budgetHeight = budget.multiply(BigDecimal.valueOf(chartHeight))
                        .divide(max, 2, RoundingMode.HALF_UP)
                        .intValue();

                int spentHeight = spent.multiply(BigDecimal.valueOf(chartHeight))
                        .divide(max, 2, RoundingMode.HALF_UP)
                        .intValue();

                int budgetBarX = x - barWidth - 3;
                int spentBarX = x + 3;

                int budgetBarY = baseY - budgetHeight;
                int spentBarY = baseY - spentHeight;

                // Budget bar
                g.setColor(budgetColor);
                g.fillRoundRect(budgetBarX, budgetBarY, barWidth, budgetHeight, 8, 8);

                // Actual bar
                g.setColor(spentColor);
                g.fillRoundRect(spentBarX, spentBarY, barWidth, spentHeight, 8, 8);

                // ===== Amount labels on bars =====
                g.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g.setColor(new Color(80, 80, 80));

                String budgetValueLabel = shortUGX(budget);
                String spentValueLabel = shortUGX(spent);

                FontMetrics valueFm = g.getFontMetrics();

                int budgetLabelWidth = valueFm.stringWidth(budgetValueLabel);
                int spentLabelWidth = valueFm.stringWidth(spentValueLabel);

                int budgetLabelX = budgetBarX + (barWidth / 2) - (budgetLabelWidth / 2);
                int spentLabelX = spentBarX + (barWidth / 2) - (spentLabelWidth / 2);

                int budgetLabelY = Math.max(top + 28, budgetBarY - 8);
                int spentLabelY = Math.max(top + 28, spentBarY - 22);

                g.drawString(budgetValueLabel, budgetLabelX, budgetLabelY);
                g.drawString(spentValueLabel, spentLabelX, spentLabelY);

                // ===== Utilization % above actual bar =====
                BigDecimal utilization = BigDecimal.ZERO;
                if (budget.compareTo(BigDecimal.ZERO) > 0) {
                    utilization = spent.multiply(BigDecimal.valueOf(100))
                            .divide(budget, 1, RoundingMode.HALF_UP);
                }

                String utilizationLabel = utilization.stripTrailingZeros().toPlainString() + "%";

                g.setFont(new Font("SansSerif", Font.BOLD, 10));

                if (utilization.compareTo(BigDecimal.valueOf(100)) > 0) {
                    g.setColor(new Color(200, 0, 0));       // over budget
                } else if (utilization.compareTo(BigDecimal.valueOf(90)) >= 0) {
                    g.setColor(new Color(245, 158, 11));    // near limit
                } else {
                    g.setColor(new Color(34, 139, 34));     // healthy
                }

                FontMetrics utilFm = g.getFontMetrics();
                int utilWidth = utilFm.stringWidth(utilizationLabel);
                int utilX = spentBarX + (barWidth / 2) - (utilWidth / 2);
                int utilY = Math.max(top + 15, spentBarY - 34);

                g.drawString(utilizationLabel, utilX, utilY);

                // ===== Department label =====
                String label = formatDeptName(d.getDepartmentName());

                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.setColor(new Color(70, 70, 70));

                AffineTransform original = g.getTransform();
                g.rotate(-Math.PI / 4, x, baseY + 30);
                g.drawString(label, x - 20, baseY + 30);
                g.setTransform(original);
            }

            // ================= LEGEND =================
            int legendY = height - 40;

            g.setFont(new Font("SansSerif", Font.PLAIN, 12));

            g.setColor(budgetColor);
            g.fillRect(left, legendY - 10, 14, 10);
            g.setColor(Color.BLACK);
            g.drawString("Budget", left + 20, legendY);

            g.setColor(spentColor);
            g.fillRect(left + 100, legendY - 10, 14, 10);
            g.setColor(Color.BLACK);
            g.drawString("Actual", left + 120, legendY);

        } finally {
            g.dispose();
        }

        return new Image(ImageDataFactory.create(toBytes(image)));
    }

    private static String shortUGX(BigDecimal value) {
        if (value == null) {
            return "0";
        }

        BigDecimal billion = new BigDecimal("1000000000");
        BigDecimal million = new BigDecimal("1000000");
        BigDecimal thousand = new BigDecimal("1000");

        if (value.compareTo(billion) >= 0) {
            return value.divide(billion, 1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "B";
        } else if (value.compareTo(million) >= 0) {
            return value.divide(million, 1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "M";
        } else if (value.compareTo(thousand) >= 0) {
            return value.divide(thousand, 1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "K";
        }

        return value.setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    private static String formatDeptName(String name) {
        if (name == null || name.isBlank()) {
            return "N/A";
        }

        // Remove common long prefixes
        name = name.replace("DEPARTMENT OF ", "")
                .replace("DIVISION OF ", "")
                .replace("SECTION OF ", "");

        // Truncate safely
        if (name.length() > 14) {
            return name.substring(0, 14) + "...";
        }

        return name;
    }

    private static byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private static BigDecimal nz(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private static String shortName(String name) {
        if (name == null) {
            return "N/A";
        }
        return name.length() > 10 ? name.substring(0, 10) + "..." : name;
    }
}
