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
    private static final BigDecimal NEAR_LIMIT_LOW = BigDecimal.valueOf(75);
    private static final BigDecimal NEAR_LIMIT_HIGH = BigDecimal.valueOf(90);

    // Constructor from entity
    public SectionBudget(UrcDeptSectionAnlDimbgt section, BigDecimal allocatedBudget, BigDecimal spentAmount) {
        this.sectionCode = section.getANL_CODE();
        this.sectionName = section.getNAME();
        this.categoryId = section.getANL_CAT_ID();
        this.allocatedBudget = allocatedBudget;
        this.spentAmount = spentAmount;
        this.availableAmount = allocatedBudget.subtract(spentAmount.abs());
        this.status = calculateStatus();
    }

    public BigDecimal getSpentPercentage() {
        return percentage(spentAmount.abs(), allocatedBudget);
    }

    public BigDecimal getUtilizationPercentage() {
        return percentage(
                safe(spentAmount.abs()),
                allocatedBudget
        );
    }

    public BigDecimal getRemainingBudget() {
        return safe(allocatedBudget)
                .subtract(safe(spentAmount.abs()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isOverBudget() {
        return safe(spentAmount.abs())
                .compareTo(safe(allocatedBudget)) > 0;
    }

    public boolean isNearLimit() {

        BigDecimal utilization = getUtilizationPercentage();

        return utilization.compareTo(NEAR_LIMIT_LOW) > 0
                && utilization.compareTo(NEAR_LIMIT_HIGH) <= 0;
    }

    private static final BigDecimal CRITICAL_LIMIT = BigDecimal.valueOf(90);

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
        switch (status) {
            case "Over Budget":
                return "status-over";
            case "Critical":
                return "status-critical";
            case "Near Limit":
                return "status-near";
            default:
                return "status-good";
        }
    }

    public String getProgressBarClass() {
        switch (status) {
            case "Over Budget":
                return "progress-over";
            case "Critical":
                return "progress-critical";
            case "Near Limit":
                return "progress-near";
            default:
                return "progress-good";
        }
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
}
