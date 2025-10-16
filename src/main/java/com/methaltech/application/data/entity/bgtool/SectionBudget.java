
package com.methaltech.application.data.entity.bgtool;


import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionBudget  implements Serializable{
    private String sectionCode;
    private String sectionName;
    private String categoryId;
    private String departmentCode;
    private double allocatedBudget;
    private double spentAmount;
    private double committedAmount;
    private double availableAmount;
    private String status;
    private String description;

    // Constructor from entity
    public SectionBudget(UrcDeptSectionAnlDimbgt section, double allocatedBudget, double spentAmount, double committedAmount) {
        this.sectionCode = section.getANL_CODE();
        this.sectionName = section.getNAME();
        this.categoryId = section.getANL_CAT_ID();
        this.allocatedBudget = allocatedBudget;
        this.spentAmount = spentAmount;
        this.committedAmount = committedAmount;
        this.availableAmount = allocatedBudget - spentAmount - committedAmount;
        this.status = calculateStatus();
    }

    public double getSpentPercentage() {
        return allocatedBudget > 0 ? (spentAmount / allocatedBudget) * 100 : 0;
    }

    public double getUtilizationPercentage() {
        return allocatedBudget > 0 ? ((spentAmount + committedAmount) / allocatedBudget) * 100 : 0;
    }

    public double getRemainingBudget() {
        return allocatedBudget - spentAmount;
    }

    public boolean isOverBudget() {
        return spentAmount > allocatedBudget;
    }

    public boolean isNearLimit() {
        double utilization = getUtilizationPercentage();
        return utilization > 75 && utilization <= 90;
    }

    public boolean isCritical() {
        return getUtilizationPercentage() > 90;
    }

    private String calculateStatus() {
        if (isOverBudget()) {
            return "Over Budget";
        } else if (isCritical()) {
            return "Critical";
        } else if (isNearLimit()) {
            return "Near Limit";
        } else {
            return "On Track";
        }
    }

    public String getStatusClass() {
        switch (status) {
            case "Over Budget": return "status-over";
            case "Critical": return "status-critical";
            case "Near Limit": return "status-near";
            default: return "status-good";
        }
    }

    public String getProgressBarClass() {
        switch (status) {
            case "Over Budget": return "progress-over";
            case "Critical": return "progress-critical";
            case "Near Limit": return "progress-near";
            default: return "progress-good";
        }
    }
}
