
package com.methaltech.application.data.entity.bgtool;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PerformanceRow {

    private final UUID id = UUID.randomUUID(); // technical identity

    private String label;
    private String previousBudgetApproved;
    private String previousBudgeActual;
    private String chosenBudget;

    public PerformanceRow(String label) {
        this.label = label;
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

