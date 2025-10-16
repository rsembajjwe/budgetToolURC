
package com.methaltech.application.data.entity.bgtool;

import java.io.Serializable;

public class RevenueSource  implements Serializable{
    private String id;
    private String name;
    private String category;
    private double amount;
    private double projected;
    private String color;

    public RevenueSource() {}

    public RevenueSource(String id, String name, String category, 
                        double amount, double projected, String color) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.projected = projected;
        this.color = color;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getProjected() { return projected; }
    public void setProjected(double projected) { this.projected = projected; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getProjectedPercentage() {
        return projected > 0 ? (amount / projected) * 100 : 0;
    }

    public String getCategoryIcon() {
        switch (category) {
            case "grants": return "🎯";
            case "fees": return "💳";
            case "donations": return "❤️";
            case "sales": return "🛒";
            case "investments": return "📈";
            default: return "💰";
        }
    }
}
