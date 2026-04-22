package com.methaltech.application.data.entity.bgtool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionBudget implements Serializable {

    private String sectionCode;
    private String sectionName;
    private String categoryId;
    private String departmentCode;
    private BigDecimal allocatedBudget;
    private BigDecimal spentAmount;
    private BigDecimal availableAmount;
    private String status;
    private String description;

    // quarter cumulative actuals
    private BigDecimal cumQtr1Actual;
    private BigDecimal cumQtr2Actual;
    private BigDecimal cumQtr3Actual;
    private BigDecimal cumQtr4Actual;

    private static final BigDecimal NEAR_LIMIT_LOW = BigDecimal.valueOf(75);
    private static final BigDecimal NEAR_LIMIT_HIGH = BigDecimal.valueOf(90);
    private static final BigDecimal CRITICAL_LIMIT = BigDecimal.valueOf(90);

    public SectionBudget(UrcDeptSectionAnlDimbgt section,
                         BigDecimal allocatedBudget,
                         BigDecimal spentAmount,
                         BigDecimal cumQtr1Actual,
                         BigDecimal cumQtr2Actual,
                         BigDecimal cumQtr3Actual,
                         BigDecimal cumQtr4Actual) {
        this.sectionCode = section.getANL_CODE();
        this.sectionName = section.getNAME();
        this.categoryId = section.getANL_CAT_ID();
        this.allocatedBudget = safe(allocatedBudget);
        this.spentAmount = safe(spentAmount);
        this.cumQtr1Actual = safe(cumQtr1Actual);
        this.cumQtr2Actual = safe(cumQtr2Actual);
        this.cumQtr3Actual = safe(cumQtr3Actual);
        this.cumQtr4Actual = safe(cumQtr4Actual);
        this.availableAmount = this.allocatedBudget.subtract(this.spentAmount.abs());
        this.status = calculateStatus();
    }

    public BigDecimal getSpentPercentage() {
        return percentage(spentAmount.abs(), allocatedBudget);
    }

    public BigDecimal getUtilizationPercentage() {
        return percentage(spentAmount.abs(), allocatedBudget);
    }

    public BigDecimal getRemainingBudget() {
        return safe(allocatedBudget)
                .subtract(safe(spentAmount.abs()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isOverBudget() {
        return safe(spentAmount.abs()).compareTo(safe(allocatedBudget)) > 0;
    }

    public boolean isNearLimit() {
        BigDecimal utilization = getUtilizationPercentage();
        return utilization.compareTo(NEAR_LIMIT_LOW) > 0
                && utilization.compareTo(NEAR_LIMIT_HIGH) <= 0;
    }

    public boolean isCritical() {
        BigDecimal utilization = getUtilizationPercentage();
        return utilization.compareTo(CRITICAL_LIMIT) > 0;
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
        return switch (status) {
            case "Over Budget" -> "status-over";
            case "Critical" -> "status-critical";
            case "Near Limit" -> "status-near";
            default -> "status-good";
        };
    }

    public String getProgressBarClass() {
        return switch (status) {
            case "Over Budget" -> "progress-over";
            case "Critical" -> "progress-critical";
            case "Near Limit" -> "progress-near";
            default -> "progress-good";
        };
    }

    public BigDecimal getAvailablePercentage() {
        return percentage(availableAmount, allocatedBudget);
    }

    public BigDecimal getTotalConsumedAmount() {
        return safe(spentAmount.abs());
    }

    public BigDecimal getTotalConsumedPercentage() {
        return percentage(getTotalConsumedAmount(), allocatedBudget);
    }

    public boolean isHealthy() {
        return "On Track".equals(status);
    }

    public boolean hasAvailableBudget() {
        return safe(availableAmount).compareTo(BigDecimal.ZERO) > 0;
    }

    // -----------------------------
    // Quarter-aware helpers
    // -----------------------------
    public BigDecimal getSpentAmountByQuarter(String quarterKey) {
        if (quarterKey == null || quarterKey.isBlank()) {
            return safe(spentAmount).abs();
        }

        return switch (quarterKey.toLowerCase()) {
            case "qtr1" -> safe(cumQtr1Actual).abs();
            case "qtr2" -> safe(cumQtr2Actual).abs();
            case "qtr3" -> safe(cumQtr3Actual).abs();
            case "qtr4" -> safe(cumQtr4Actual).abs();
            default -> safe(spentAmount).abs();
        };
    }

    public BigDecimal getAvailableAmountByQuarter(String quarterKey) {
        return safe(allocatedBudget).subtract(getSpentAmountByQuarter(quarterKey));
    }

    public BigDecimal getSpentPercentageByQuarter(String quarterKey) {
        return percentage(getSpentAmountByQuarter(quarterKey), allocatedBudget);
    }

    public boolean isOverBudget(String quarterKey) {
        return getSpentAmountByQuarter(quarterKey).compareTo(safe(allocatedBudget)) > 0;
    }

    public boolean isNearLimit(String quarterKey) {
        BigDecimal utilization = getSpentPercentageByQuarter(quarterKey);
        return utilization.compareTo(NEAR_LIMIT_LOW) > 0
                && utilization.compareTo(NEAR_LIMIT_HIGH) <= 0;
    }

    public boolean isCritical(String quarterKey) {
        BigDecimal utilization = getSpentPercentageByQuarter(quarterKey);
        return utilization.compareTo(CRITICAL_LIMIT) > 0;
    }

    public String getStatusByQuarter(String quarterKey) {
        if (isOverBudget(quarterKey)) {
            return "Over Budget";
        } else if (isCritical(quarterKey)) {
            return "Critical";
        } else if (isNearLimit(quarterKey)) {
            return "Near Limit";
        } else {
            return "On Track";
        }
    }

    public String getStatusClassByQuarter(String quarterKey) {
        return switch (getStatusByQuarter(quarterKey)) {
            case "Over Budget" -> "status-over";
            case "Critical" -> "status-critical";
            case "Near Limit" -> "status-near";
            default -> "status-good";
        };
    }

    public String getProgressBarClassByQuarter(String quarterKey) {
        return switch (getStatusByQuarter(quarterKey)) {
            case "Over Budget" -> "progress-over";
            case "Critical" -> "progress-critical";
            case "Near Limit" -> "progress-near";
            default -> "progress-good";
        };
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal percentage(BigDecimal part, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return safe(part)
                .divide(total, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
