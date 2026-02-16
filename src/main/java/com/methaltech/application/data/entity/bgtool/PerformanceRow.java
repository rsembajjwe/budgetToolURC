
package com.methaltech.application.data.entity.bgtool;

import lombok.Data;

import java.util.UUID;

@Data
public class PerformanceRow {

    private final UUID id = UUID.randomUUID(); // technical identity

    private String label;
    private String previousBudgetApproved;
    private String previousRevised;
    private String previousBudgeActual;
    private String previousProjected;
    private String chosenBudget;
    private boolean section;

    public PerformanceRow(String label,boolean section) {
        this.label = label;
        this.section=section;
    }

    public PerformanceRow(String label,
                          String approved,
                          String actual,
                          String budget) {
        this.label = label;
        this.previousBudgetApproved = approved;
        this.previousBudgeActual = actual;
        this.chosenBudget = budget;
    }
    
    public PerformanceRow(String label,
                          String approved,
                          String previousRevised,
                          String actual,
                          String previousProjected,
                          String budget) {
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
        if (!(o instanceof PerformanceRow)) return false;
        PerformanceRow that = (PerformanceRow) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

