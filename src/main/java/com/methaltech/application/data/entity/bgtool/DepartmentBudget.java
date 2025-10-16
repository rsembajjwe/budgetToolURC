package com.methaltech.application.data.entity.bgtool;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentBudget {

    private String departmentCode;
    private String departmentName;
    private String categoryId;
    private double totalBudget;
    private double totalSpent;
    private double totalCommitted;
    private String color;
    private boolean budgetCheckEnabled;
    private boolean budgetStopEnabled;
    private boolean postingProhibited;
    private String status;
    private int sectionCount;

    public double getSpentPercentage() {
        return totalBudget > 0 ? (totalSpent / totalBudget) * 100 : 0;
    }

    public double getRemainingBudget() {
        return totalBudget - totalSpent;
    }

    public double getAvailableBudget() {
        return totalBudget - totalSpent - totalCommitted;
    }

    public String getStatusText() {
        double spentPercentage = getSpentPercentage();
        if (spentPercentage > 100) {
            return "Over Budget";
        } else if (spentPercentage > 90) {
            return "Critical";
        } else if (spentPercentage > 75) {
            return "Near Limit";
        } else {
            return "On Track";
        }
    }

    public String getStatusClass() {
        double spentPercentage = getSpentPercentage();
        if (spentPercentage > 100) {
            return "status-over";
        } else if (spentPercentage > 90) {
            return "status-critical";
        } else if (spentPercentage > 75) {
            return "status-near";
        } else {
            return "status-good";
        }
    }
}
