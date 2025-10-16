package com.methaltech.application.data.entity.bgtool;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class COABudgetInfo {

    private String coaCode;
    private String coaName;
    private String hierarchyPath;
    private Double budgetTotal;
    private Double committedAmount;
    private Double actualSpent;
    private Double balanceAmount;
    private String status;
    private boolean isActive;
    private String organisationCode;
    private String organisationName;

    // Constructor from COA entity
    public COABudgetInfo(COA coa, Double budgetTotal, Double committedAmount, Double actualSpent) {
        this.coaCode = coa.getCode();
        this.coaName = coa.getName();
        this.hierarchyPath = coa.getFullHierarchy();
        this.budgetTotal = budgetTotal != null ? budgetTotal : 0.0;
        this.committedAmount = committedAmount != null ? committedAmount : 0.0;
        this.actualSpent = actualSpent != null ? actualSpent : 0.0;
        this.balanceAmount = this.budgetTotal - this.committedAmount - this.actualSpent;
        this.isActive = coa.isStateOpen();
        this.status = calculateStatus();

        if (coa.getOrganisation() != null) {
            this.organisationCode = coa.getOrganisation().getCode();
            this.organisationName = coa.getOrganisation().getName();
        }
    }

    public double getUtilizationPercentage() {
        return budgetTotal > 0 ? ((committedAmount + actualSpent) / budgetTotal) * 100 : 0;
    }

    public boolean isOverBudget() {
        return (committedAmount + actualSpent) > budgetTotal;
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
            return "Available";
        }
    }

    public String getStatusClass() {
        switch (status) {
            case "Over Budget":
                return "status-over";
            case "Critical":
                return "status-critical";
            case "Near Limit":
                return "status-near";
            default:
                return "status-available";
        }
    }

    public String getStatusColor() {
        switch (status) {
            case "Over Budget":
                return "#dc2626";
            case "Critical":
                return "#f97316";
            case "Near Limit":
                return "#f59e0b";
            default:
                return "#10b981";
        }
    }

    public boolean canCreateRequisition() {
        return isActive && balanceAmount > 0 && !isOverBudget();
    }

    public String getAvailabilityText() {
        if (!isActive) {
            return "Inactive Account";
        } else if (isOverBudget()) {
            return "Over Budget";
        } else if (balanceAmount <= 0) {
            return "No Balance";
        } else {
            return "Available";
        }
    }
}
