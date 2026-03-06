package com.methaltech.application.data.entity.bgtool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RevenueSource implements Serializable {

    private String id;
    private String name;
    private String category;
    private BigDecimal amount;
    private BigDecimal projected;
    private String color;

    public RevenueSource() {
    }

    public RevenueSource(String id, String name, String category,
            BigDecimal amount, BigDecimal projected, String color) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.projected = projected;
        this.color = color;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getProjected() {
        return projected;
    }

    public void setProjected(BigDecimal projected) {
        this.projected = projected;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getProjectedPercentage() {
        if (projected == null || projected.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return amount
                .divide(projected, 6, RoundingMode.HALF_UP) // high precision intermediate
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP); // final percentage scale
    }

    public String getCategoryIcon() {
        switch (category) {
            case "grants":
                return "🎯";
            case "fees":
                return "💳";
            case "donations":
                return "❤️";
            case "sales":
                return "🛒";
            case "investments":
                return "📈";
            default:
                return "💰";
        }
    }
}
