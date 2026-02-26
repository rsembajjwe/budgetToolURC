
package com.methaltech.application.data.entity.bgtool.dto;

import java.math.BigDecimal;
import lombok.Data;

import java.util.UUID;

@Data
public class PerformanceData {

    private final UUID id = UUID.randomUUID(); // technical identity

    private String label;
    private BigDecimal previousBudgetApproved;
    private BigDecimal previousRevised;
    private BigDecimal previousBudgeActual;
    private BigDecimal previousProjected;
    private BigDecimal chosenBudget;
    private boolean section;

    public PerformanceData(String label,boolean section) {
        this.label = label;
        this.section=section;
    }

    public PerformanceData(String label,
                          BigDecimal approved,
                          BigDecimal actual,
                          BigDecimal budget) {
        this.label = label;
        this.previousBudgetApproved = approved;
        this.previousBudgeActual = actual;
        this.chosenBudget = budget;
    }
    
    public PerformanceData(String label,
                          BigDecimal approved,
                          BigDecimal previousRevised,
                          BigDecimal actual,
                          BigDecimal previousProjected,
                          BigDecimal budget) {
        this.label = label;
        this.previousBudgetApproved = approved;
        this.previousBudgeActual = actual;
        this.chosenBudget = budget;
        this.previousRevised=previousRevised;
        this.chosenBudget=previousProjected;
    }
    // Equality strictly by ID (not by label or values)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerformanceData)) return false;
        PerformanceData that = (PerformanceData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


