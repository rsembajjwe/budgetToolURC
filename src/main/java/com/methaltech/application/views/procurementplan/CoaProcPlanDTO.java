
package com.methaltech.application.views.procurementplan;

import java.math.BigDecimal;

public class CoaProcPlanDTO {

    private Long budgetId;
    private Long coaId;
    private String coaName;
    private BigDecimal totalCost;

    public CoaProcPlanDTO(Long budgetId, Long coaId, String coaName, BigDecimal totalCost) {
        this.budgetId = budgetId;
        this.coaId = coaId;
        this.coaName = coaName;
        this.totalCost = totalCost;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getCoaId() {
        return coaId;
    }

    public void setCoaId(Long coaId) {
        this.coaId = coaId;
    }

    public String getCoaName() {
        return coaName;
    }

    public void setCoaName(String coaName) {
        this.coaName = coaName;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
