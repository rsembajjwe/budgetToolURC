package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
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
    private BigDecimal totalBudget;
    private BigDecimal totalSpent;
    private BigDecimal totalCommitted;
    private String color;
    private boolean budgetCheckEnabled;
    private boolean budgetStopEnabled;
    private boolean postingProhibited;
    private String status;
    private int sectionCount;
    private BigDecimal cumQtr1Budget;
    private BigDecimal cumQtr2Budget;
    private BigDecimal cumQtr3Budget;
    private BigDecimal cumQtr4Budget;

    private BigDecimal cumQtr1Actual;
    private BigDecimal cumQtr2Actual;
    private BigDecimal cumQtr3Actual;
    private BigDecimal cumQtr4Actual;
    Set<UrcDeptSectionAnlDimbgt> sections;

public BigDecimal getSpentPercentage() {

    if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) <= 0) {
        return BigDecimal.ZERO;
    }

    return totalSpent
            .divide(totalBudget, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);
}

 public BigDecimal getRemainingBudget() {

    BigDecimal budget = totalBudget != null ? totalBudget : BigDecimal.ZERO;
    BigDecimal spent = totalSpent != null ? totalSpent : BigDecimal.ZERO;

    return budget
            .subtract(spent)
            .setScale(2, RoundingMode.HALF_UP);
}

public BigDecimal getAvailableBudget() {

    BigDecimal budget = totalBudget != null ? totalBudget : BigDecimal.ZERO;
    BigDecimal spent = totalSpent != null ? totalSpent : BigDecimal.ZERO;
    BigDecimal committed = totalCommitted != null ? totalCommitted : BigDecimal.ZERO;

    return budget
            .subtract(spent.add(committed))
            .setScale(2, RoundingMode.HALF_UP);
}

    public String getStatusText() {
        double spentPercentage = getSpentPercentage().doubleValue();
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
        double spentPercentage = getSpentPercentage().doubleValue();
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
