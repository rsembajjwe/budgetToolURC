package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BudgetSummary {

    private BigDecimal totalBudget;
    private BigDecimal totalSpent;
    private BigDecimal totalCommitted;
    private BigDecimal totalActualRevenue;
    private BigDecimal budgetedRevenue;
    private BigDecimal cumQtr1Budget;
    private BigDecimal cumQtr2Budget;
    private BigDecimal cumQtr3Budget;
    private BigDecimal cumQtr4Budget;

    private BigDecimal cumQtr1Actual;
    private BigDecimal cumQtr2Actual;
    private BigDecimal cumQtr3Actual;
    private BigDecimal cumQtr4Actual;
    private List<DepartmentBudget> departmentBudgets;
    private List<RevenueSource> revenueSources;
    private BigDecimal igrTotalRevenueBudget;
    private BigDecimal igrRevenueQtr1Budget;
    private BigDecimal igrRevenueQtr2Budget;
    private BigDecimal igrRevenueQtr3Budget;
    private BigDecimal igrRevenueQtr4Budget;

    private BigDecimal gouTotalRevenueBudget;
    private BigDecimal gouRevenueQtr1Budget;
    private BigDecimal gouRevenueQtr2Budget;
    private BigDecimal gouRevenueQtr3Budget;
    private BigDecimal gouRevenueQtr4Budget;

    private BigDecimal extTotalRevenueBudget;
    private BigDecimal extRevenueQtr1Budget;
    private BigDecimal extRevenueQtr2Budget;
    private BigDecimal extRevenueQtr3Budget;
    private BigDecimal extRevenueQtr4Budget;
    
    private BigDecimal projectedRevenueQtr1Budget;
    private BigDecimal projectedRevenueQtr2Budget;
    private BigDecimal projectedRevenueQtr3Budget;
    private BigDecimal projectedRevenueQtr4Budget;
    
        private BigDecimal revenueQtr1Actual;
    private BigDecimal revenueQtr2Actual;
    private BigDecimal revenueQtr3Actual;
    private BigDecimal revenueQtr4Actual;
    private BigDecimal revenueActual;
    
    private BigDecimal opexBudget;
    private BigDecimal capexBudget;
    private BigDecimal opexActual;
    private BigDecimal capexActual;
    private BigDecimal igrTotalActualRevenue;
    private BigDecimal gouTotalActualRevenue;

    public BudgetSummary() {
    }

    public BudgetSummary(BigDecimal totalBudget, BigDecimal totalCommitted, BigDecimal totalSpent, BigDecimal totalRevenue,
            BigDecimal projectedRevenue, List<DepartmentBudget> departmentBudgets,
            List<RevenueSource> revenueSources) {
        this.totalBudget = totalBudget;
        this.totalCommitted = totalCommitted;
        this.totalSpent = totalSpent;
        this.totalActualRevenue = totalRevenue;
        this.budgetedRevenue = projectedRevenue;
        this.departmentBudgets = departmentBudgets;
        this.revenueSources = revenueSources;
    }

    public BudgetSummary(BigDecimal totalBudget, BigDecimal totalSpent, BigDecimal totalCommitted, BigDecimal totalRevenue, BigDecimal projectedRevenue, BigDecimal cumQtr1Budget,
            BigDecimal cumQtr2Budget, BigDecimal cumQtr3Budget, BigDecimal cumQtr4Budget, BigDecimal cumQtr1Actual, BigDecimal cumQtr2Actual, BigDecimal cumQtr3Actual, BigDecimal cumQtr4Actual, List<DepartmentBudget> departmentBudgets, List<RevenueSource> revenueSources) {
        this.totalBudget = totalBudget;
        this.totalSpent = totalSpent;
        this.totalCommitted = totalCommitted;
        this.totalActualRevenue = totalRevenue;
        this.budgetedRevenue = projectedRevenue;
        this.cumQtr1Budget = cumQtr1Budget;
        this.cumQtr2Budget = cumQtr2Budget;
        this.cumQtr3Budget = cumQtr3Budget;
        this.cumQtr4Budget = cumQtr4Budget;
        this.cumQtr1Actual = cumQtr1Actual;
        this.cumQtr2Actual = cumQtr2Actual;
        this.cumQtr3Actual = cumQtr3Actual;
        this.cumQtr4Actual = cumQtr4Actual;
        this.departmentBudgets = departmentBudgets;
        this.revenueSources = revenueSources;
    }

    public BudgetSummary(BigDecimal totalBudget, BigDecimal totalSpent, BigDecimal totalCommitted, BigDecimal totalRevenue, BigDecimal projectedRevenue, BigDecimal cumQtr1Budget, BigDecimal cumQtr2Budget, BigDecimal cumQtr3Budget, BigDecimal cumQtr4Budget, BigDecimal cumQtr1Actual, BigDecimal cumQtr2Actual, BigDecimal cumQtr3Actual, BigDecimal cumQtr4Actual, List<DepartmentBudget> departmentBudgets, List<RevenueSource> revenueSources, BigDecimal igrTotalRevenueBudget, BigDecimal igrRevenueQtr1Budget, BigDecimal igrRevenueQtr2Budget, BigDecimal igrRevenueQtr3Budget, BigDecimal igrRevenueQtr4Budget, BigDecimal gouTotalRevenueBudget, BigDecimal gouRevenueQtr1Budget, BigDecimal gouRevenueQtr2Budget, BigDecimal gouRevenueQtr3Budget, BigDecimal gouRevenueQtr4Budget, BigDecimal extTotalRevenueBudget, BigDecimal extRevenueQtr1Budget, BigDecimal extRevenueQtr2Budget, BigDecimal extRevenueQtr3Budget, BigDecimal extRevenueQtr4Budget) {
        this.totalBudget = totalBudget;
        this.totalSpent = totalSpent;
        this.totalCommitted = totalCommitted;
        this.totalActualRevenue = totalRevenue;
        this.budgetedRevenue = projectedRevenue;
        this.cumQtr1Budget = cumQtr1Budget;
        this.cumQtr2Budget = cumQtr2Budget;
        this.cumQtr3Budget = cumQtr3Budget;
        this.cumQtr4Budget = cumQtr4Budget;
        this.cumQtr1Actual = cumQtr1Actual;
        this.cumQtr2Actual = cumQtr2Actual;
        this.cumQtr3Actual = cumQtr3Actual;
        this.cumQtr4Actual = cumQtr4Actual;
        this.departmentBudgets = departmentBudgets;
        this.revenueSources = revenueSources;
        this.igrTotalRevenueBudget = igrTotalRevenueBudget;
        this.igrRevenueQtr1Budget = igrRevenueQtr1Budget;
        this.igrRevenueQtr2Budget = igrRevenueQtr2Budget;
        this.igrRevenueQtr3Budget = igrRevenueQtr3Budget;
        this.igrRevenueQtr4Budget = igrRevenueQtr4Budget;
        this.gouTotalRevenueBudget = gouTotalRevenueBudget;
        this.gouRevenueQtr1Budget = gouRevenueQtr1Budget;
        this.gouRevenueQtr2Budget = gouRevenueQtr2Budget;
        this.gouRevenueQtr3Budget = gouRevenueQtr3Budget;
        this.gouRevenueQtr4Budget = gouRevenueQtr4Budget;
        this.extTotalRevenueBudget = extTotalRevenueBudget;
        this.extRevenueQtr1Budget = extRevenueQtr1Budget;
        this.extRevenueQtr2Budget = extRevenueQtr2Budget;
        this.extRevenueQtr3Budget = extRevenueQtr3Budget;
        this.extRevenueQtr4Budget = extRevenueQtr4Budget;
    }

    public BigDecimal getIgrTotalRevenueBudget() {
        return igrTotalRevenueBudget;
    }

    public void setIgrTotalRevenueBudget(BigDecimal igrTotalRevenueBudget) {
        this.igrTotalRevenueBudget = igrTotalRevenueBudget;
    }

    public BigDecimal getIgrRevenueQtr1Budget() {
        return igrRevenueQtr1Budget;
    }

    public void setIgrRevenueQtr1Budget(BigDecimal igrRevenueQtr1Budget) {
        this.igrRevenueQtr1Budget = igrRevenueQtr1Budget;
    }

    public BigDecimal getIgrRevenueQtr2Budget() {
        return igrRevenueQtr2Budget;
    }

    public void setIgrRevenueQtr2Budget(BigDecimal igrRevenueQtr2Budget) {
        this.igrRevenueQtr2Budget = igrRevenueQtr2Budget;
    }

    public BigDecimal getIgrRevenueQtr3Budget() {
        return igrRevenueQtr3Budget;
    }

    public void setIgrRevenueQtr3Budget(BigDecimal igrRevenueQtr3Budget) {
        this.igrRevenueQtr3Budget = igrRevenueQtr3Budget;
    }

    public BigDecimal getIgrRevenueQtr4Budget() {
        return igrRevenueQtr4Budget;
    }

    public void setIgrRevenueQtr4Budget(BigDecimal igrRevenueQtr4Budget) {
        this.igrRevenueQtr4Budget = igrRevenueQtr4Budget;
    }

    public BigDecimal getGouTotalRevenueBudget() {
        return gouTotalRevenueBudget;
    }

    public void setGouTotalRevenueBudget(BigDecimal gouTotalRevenueBudget) {
        this.gouTotalRevenueBudget = gouTotalRevenueBudget;
    }

    public BigDecimal getGouRevenueQtr1Budget() {
        return gouRevenueQtr1Budget;
    }

    public void setGouRevenueQtr1Budget(BigDecimal gouRevenueQtr1Budget) {
        this.gouRevenueQtr1Budget = gouRevenueQtr1Budget;
    }

    public BigDecimal getGouRevenueQtr2Budget() {
        return gouRevenueQtr2Budget;
    }

    public void setGouRevenueQtr2Budget(BigDecimal gouRevenueQtr2Budget) {
        this.gouRevenueQtr2Budget = gouRevenueQtr2Budget;
    }

    public BigDecimal getGouRevenueQtr3Budget() {
        return gouRevenueQtr3Budget;
    }

    public void setGouRevenueQtr3Budget(BigDecimal gouRevenueQtr3Budget) {
        this.gouRevenueQtr3Budget = gouRevenueQtr3Budget;
    }

    public BigDecimal getGouRevenueQtr4Budget() {
        return gouRevenueQtr4Budget;
    }

    public void setGouRevenueQtr4Budget(BigDecimal gouRevenueQtr4Budget) {
        this.gouRevenueQtr4Budget = gouRevenueQtr4Budget;
    }

    public BigDecimal getExtTotalRevenueBudget() {
        return extTotalRevenueBudget;
    }

    public void setExtTotalRevenueBudget(BigDecimal extTotalRevenueBudget) {
        this.extTotalRevenueBudget = extTotalRevenueBudget;
    }

    public BigDecimal getExtRevenueQtr1Budget() {
        return extRevenueQtr1Budget;
    }

    public void setExtRevenueQtr1Budget(BigDecimal extRevenueQtr1Budget) {
        this.extRevenueQtr1Budget = extRevenueQtr1Budget;
    }

    public BigDecimal getExtRevenueQtr2Budget() {
        return extRevenueQtr2Budget;
    }

    public void setExtRevenueQtr2Budget(BigDecimal extRevenueQtr2Budget) {
        this.extRevenueQtr2Budget = extRevenueQtr2Budget;
    }

    public BigDecimal getExtRevenueQtr3Budget() {
        return extRevenueQtr3Budget;
    }

    public void setExtRevenueQtr3Budget(BigDecimal extRevenueQtr3Budget) {
        this.extRevenueQtr3Budget = extRevenueQtr3Budget;
    }

    public BigDecimal getExtRevenueQtr4Budget() {
        return extRevenueQtr4Budget;
    }

    public void setExtRevenueQtr4Budget(BigDecimal extRevenueQtr4Budget) {
        this.extRevenueQtr4Budget = extRevenueQtr4Budget;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getTotalCommitted() {
        return totalCommitted;
    }

    public void setTotalCommitted(BigDecimal totalCommitted) {
        this.totalCommitted = totalCommitted;
    }

    public BigDecimal getTotalRevenue() {
        return totalActualRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalActualRevenue = totalRevenue;
    }

    public BigDecimal getProjectedRevenue() {
        return budgetedRevenue;
    }

    public void setProjectedRevenue(BigDecimal projectedRevenue) {
        this.budgetedRevenue = projectedRevenue;
    }

    public BigDecimal getCumQtr1Budget() {
        return cumQtr1Budget;
    }

    public void setCumQtr1Budget(BigDecimal cumQtr1Budget) {
        this.cumQtr1Budget = cumQtr1Budget;
    }

    public BigDecimal getCumQtr2Budget() {
        return cumQtr2Budget;
    }

    public void setCumQtr2Budget(BigDecimal cumQtr2Budget) {
        this.cumQtr2Budget = cumQtr2Budget;
    }

    public BigDecimal getCumQtr3Budget() {
        return cumQtr3Budget;
    }

    public void setCumQtr3Budget(BigDecimal cumQtr3Budget) {
        this.cumQtr3Budget = cumQtr3Budget;
    }

    public BigDecimal getCumQtr4Budget() {
        return cumQtr4Budget;
    }

    public void setCumQtr4Budget(BigDecimal cumQtr4Budget) {
        this.cumQtr4Budget = cumQtr4Budget;
    }

    public BigDecimal getCumQtr1Actual() {
        return cumQtr1Actual;
    }

    public void setCumQtr1Actual(BigDecimal cumQtr1Actual) {
        this.cumQtr1Actual = cumQtr1Actual;
    }

    public BigDecimal getCumQtr2Actual() {
        return cumQtr2Actual;
    }

    public void setCumQtr2Actual(BigDecimal cumQtr2Actual) {
        this.cumQtr2Actual = cumQtr2Actual;
    }

    public BigDecimal getCumQtr3Actual() {
        return cumQtr3Actual;
    }

    public void setCumQtr3Actual(BigDecimal cumQtr3Actual) {
        this.cumQtr3Actual = cumQtr3Actual;
    }

    public BigDecimal getCumQtr4Actual() {
        return cumQtr4Actual;
    }

    public void setCumQtr4Actual(BigDecimal cumQtr4Actual) {
        this.cumQtr4Actual = cumQtr4Actual;
    }

    public List<DepartmentBudget> getDepartmentBudgets() {
        return departmentBudgets;
    }

    public void setDepartmentBudgets(List<DepartmentBudget> departmentBudgets) {
        this.departmentBudgets = departmentBudgets;
    }

    public List<RevenueSource> getRevenueSources() {
        return revenueSources;
    }

    public void setRevenueSources(List<RevenueSource> revenueSources) {
        this.revenueSources = revenueSources;
    }

    public BigDecimal getRevenueQtr1Actual() {
        return revenueQtr1Actual;
    }

    public void setRevenueQtr1Actual(BigDecimal revenueQtr1Actual) {
        this.revenueQtr1Actual = revenueQtr1Actual;
    }

    public BigDecimal getRevenueQtr2Actual() {
        return revenueQtr2Actual;
    }

    public void setRevenueQtr2Actual(BigDecimal revenueQtr2Actual) {
        this.revenueQtr2Actual = revenueQtr2Actual;
    }

    public BigDecimal getRevenueQtr3Actual() {
        return revenueQtr3Actual;
    }

    public void setRevenueQtr3Actual(BigDecimal revenueQtr3Actual) {
        this.revenueQtr3Actual = revenueQtr3Actual;
    }

    public BigDecimal getRevenueQtr4Actual() {
        return revenueQtr4Actual;
    }

    public void setRevenueQtr4Actual(BigDecimal revenueQtr4Actual) {
        this.revenueQtr4Actual = revenueQtr4Actual;
    }

    public BigDecimal getRevenueActual() {
        return revenueActual;
    }

    public void setRevenueActual(BigDecimal revenueActual) {
        this.revenueActual = revenueActual;
    }

    public BigDecimal getRemainingBudget() {
        BigDecimal spent = totalSpent != null ? totalSpent : BigDecimal.ZERO;
        BigDecimal committed = totalCommitted != null ? totalCommitted : BigDecimal.ZERO;
        BigDecimal budget = totalBudget != null ? totalBudget : BigDecimal.ZERO;

        return budget
                .subtract(spent.add(committed))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSpentPercentage() {
        return percentage(totalSpent, totalBudget);
    }

    public BigDecimal getCommittedPercentage() {
        return percentage(totalCommitted, totalBudget);
    }

    public BigDecimal getRevenuePercentage() {
        return percentage(totalActualRevenue, budgetedRevenue);
    }

    public BigDecimal getRevenuePerformancePercentage() {
        System.out.println(getTotalRevenueSafe()+": Actual Revenue "+getProjectedRevenueSafe()+" Budget Revenue");
        // Revenue Performance (%) = Actual Revenue Collected / Planned (Projected) Revenue for the period * 100
        return percentage(getTotalRevenueSafe(), getProjectedRevenueSafe());
    }

        public BigDecimal getBudgetPerformancePercentage() {
        System.out.println(getTotalRevenueSafe()+": Actual Revenue "+getProjectedRevenueSafe()+" Budget Revenue");
        // Revenue Performance (%) = Actual Revenue Collected / Planned (Projected) Revenue for the period * 100
        return percentage(getTotalSpentSafe(), getTotalBudgetSafe());
    }
        
    public BigDecimal getAbsorptionRatePercentage() {
        // Absorption Rate (%) = Total Expenditure / Total Funds Available (Revenue collected for the period) * 100
        // If you consider "available funding" to be releases only, swap denominator accordingly.
        return percentage(getTotalSpentSafe(), getActualRevenueSafe());
    }

    /**
     * Optional: if you want absorption based on (Spent + Committed)
     */
    public BigDecimal getAbsorptionRateIncludingCommitmentsPercentage() {
        BigDecimal spentPlusCommitted = getTotalSpentSafe().add(getTotalCommittedSafe());
        return percentage(spentPlusCommitted, getTotalRevenueSafe());
    }

// ---- small null-safe helpers ----
        private BigDecimal getTotalBudgetSafe() {
        return totalBudget != null ? totalBudget : BigDecimal.ZERO;
    }
    private BigDecimal getTotalRevenueSafe() {
        return totalActualRevenue != null ? totalActualRevenue : BigDecimal.ZERO;
    }
    private BigDecimal getActualRevenueSafe() {
        return revenueActual != null ? revenueActual : BigDecimal.ZERO;
    }
    private BigDecimal getProjectedRevenueSafe() {
        return budgetedRevenue != null ? budgetedRevenue : BigDecimal.ZERO;
    }

    private BigDecimal getTotalSpentSafe() {
        return totalSpent != null ? totalSpent : BigDecimal.ZERO;
    }

    private BigDecimal getTotalCommittedSafe() {
        return totalCommitted != null ? totalCommitted : BigDecimal.ZERO;
    }

    public BigDecimal getProjectedRevenueQtr1Budget() {
        return projectedRevenueQtr1Budget;
    }

    public void setProjectedRevenueQtr1Budget(BigDecimal projectedRevenueQtr1Budget) {
        this.projectedRevenueQtr1Budget = projectedRevenueQtr1Budget;
    }

    public BigDecimal getProjectedRevenueQtr2Budget() {
        return projectedRevenueQtr2Budget;
    }

    public void setProjectedRevenueQtr2Budget(BigDecimal projectedRevenueQtr2Budget) {
        this.projectedRevenueQtr2Budget = projectedRevenueQtr2Budget;
    }

    public BigDecimal getProjectedRevenueQtr3Budget() {
        return projectedRevenueQtr3Budget;
    }

    public void setProjectedRevenueQtr3Budget(BigDecimal projectedRevenueQtr3Budget) {
        this.projectedRevenueQtr3Budget = projectedRevenueQtr3Budget;
    }

    public BigDecimal getProjectedRevenueQtr4Budget() {
        return projectedRevenueQtr4Budget;
    }

    public void setProjectedRevenueQtr4Budget(BigDecimal projectedRevenueQtr4Budget) {
        this.projectedRevenueQtr4Budget = projectedRevenueQtr4Budget;
    }

    public BigDecimal getProjectedRevenueQtr1Actual() {
        return revenueQtr1Actual;
    }

    public void setProjectedRevenueQtr1Actual(BigDecimal projectedRevenueQtr1Actual) {
        this.revenueQtr1Actual = projectedRevenueQtr1Actual;
    }

    public BigDecimal getProjectedRevenueQtr2Actual() {
        return revenueQtr2Actual;
    }

    public void setProjectedRevenueQtr2Actual(BigDecimal projectedRevenueQtr2Actual) {
        this.revenueQtr2Actual = projectedRevenueQtr2Actual;
    }

    public BigDecimal getProjectedRevenueQtr3Actual() {
        return revenueQtr3Actual;
    }

    public void setProjectedRevenueQtr3Actual(BigDecimal projectedRevenueQtr3Actual) {
        this.revenueQtr3Actual = projectedRevenueQtr3Actual;
    }

    public BigDecimal getProjectedRevenueQtr4Actual() {
        return revenueQtr4Actual;
    }

    public void setProjectedRevenueQtr4Actual(BigDecimal projectedRevenueQtr4Actual) {
        this.revenueQtr4Actual = projectedRevenueQtr4Actual;
    }

    public BigDecimal getOpexBudget() {
        return opexBudget;
    }

    public void setOpexBudget(BigDecimal opexBudget) {
        this.opexBudget = opexBudget;
    }

    public BigDecimal getCapexBudget() {
        return capexBudget;
    }

    public void setCapexBudget(BigDecimal capexBudget) {
        this.capexBudget = capexBudget;
    }

    public BigDecimal getOpexActual() {
        return opexActual;
    }

    public void setOpexActual(BigDecimal opexActual) {
        this.opexActual = opexActual;
    }

    public BigDecimal getCapexActual() {
        return capexActual;
    }

    public void setCapexActual(BigDecimal capexActual) {
        this.capexActual = capexActual;
    }
    
    

    // Legacy methods for backward compatibility
    @Deprecated
    public List<Department> getDepartments() {
        // This method is kept for backward compatibility but should not be used
        return null;
    }

    private BigDecimal percentage(BigDecimal part, BigDecimal total) {

        if (part == null) {
            part = BigDecimal.ZERO;
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return part
                .divide(total, 6, RoundingMode.HALF_UP) // intermediate precision
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);       // final display scale
    }

    public BigDecimal getIgrTotalActualRevenue() {
        return igrTotalActualRevenue;
    }

    public void setIgrTotalActualRevenue(BigDecimal igrTotalActualRevenue) {
        this.igrTotalActualRevenue = igrTotalActualRevenue;
    }

    public BigDecimal getGouTotalActualRevenue() {
        return gouTotalActualRevenue;
    }

    public void setGouTotalActualRevenue(BigDecimal gouTotalActualRevenue) {
        this.gouTotalActualRevenue = gouTotalActualRevenue;
    }

    public BudgetSummary(BigDecimal totalBudget, BigDecimal totalSpent, BigDecimal totalCommitted, BigDecimal totalActualRevenue, BigDecimal budgetedRevenue, BigDecimal cumQtr1Budget, BigDecimal cumQtr2Budget, BigDecimal cumQtr3Budget, BigDecimal cumQtr4Budget, BigDecimal cumQtr1Actual, BigDecimal cumQtr2Actual, BigDecimal cumQtr3Actual, BigDecimal cumQtr4Actual, List<DepartmentBudget> departmentBudgets, List<RevenueSource> revenueSources, BigDecimal igrTotalRevenueBudget, BigDecimal igrRevenueQtr1Budget, BigDecimal igrRevenueQtr2Budget, BigDecimal igrRevenueQtr3Budget, BigDecimal igrRevenueQtr4Budget, BigDecimal gouTotalRevenueBudget, BigDecimal gouRevenueQtr1Budget, BigDecimal gouRevenueQtr2Budget, BigDecimal gouRevenueQtr3Budget, BigDecimal gouRevenueQtr4Budget, BigDecimal extTotalRevenueBudget, BigDecimal extRevenueQtr1Budget, BigDecimal extRevenueQtr2Budget, BigDecimal extRevenueQtr3Budget, BigDecimal extRevenueQtr4Budget, BigDecimal projectedRevenueQtr1Budget, BigDecimal projectedRevenueQtr2Budget, BigDecimal projectedRevenueQtr3Budget, BigDecimal projectedRevenueQtr4Budget, BigDecimal revenueQtr1Actual, BigDecimal revenueQtr2Actual, BigDecimal revenueQtr3Actual, BigDecimal revenueQtr4Actual, BigDecimal revenueActual, BigDecimal opexBudget, BigDecimal capexBudget, BigDecimal opexActual, BigDecimal capexActual, BigDecimal igrTotalActualRevenue, BigDecimal gouTotalActualRevenue) {
        this.totalBudget = totalBudget;
        this.totalSpent = totalSpent;
        this.totalCommitted = totalCommitted;
        this.totalActualRevenue = totalActualRevenue;
        this.budgetedRevenue = budgetedRevenue;
        this.cumQtr1Budget = cumQtr1Budget;
        this.cumQtr2Budget = cumQtr2Budget;
        this.cumQtr3Budget = cumQtr3Budget;
        this.cumQtr4Budget = cumQtr4Budget;
        this.cumQtr1Actual = cumQtr1Actual;
        this.cumQtr2Actual = cumQtr2Actual;
        this.cumQtr3Actual = cumQtr3Actual;
        this.cumQtr4Actual = cumQtr4Actual;
        this.departmentBudgets = departmentBudgets;
        this.revenueSources = revenueSources;
        this.igrTotalRevenueBudget = igrTotalRevenueBudget;
        this.igrRevenueQtr1Budget = igrRevenueQtr1Budget;
        this.igrRevenueQtr2Budget = igrRevenueQtr2Budget;
        this.igrRevenueQtr3Budget = igrRevenueQtr3Budget;
        this.igrRevenueQtr4Budget = igrRevenueQtr4Budget;
        this.gouTotalRevenueBudget = gouTotalRevenueBudget;
        this.gouRevenueQtr1Budget = gouRevenueQtr1Budget;
        this.gouRevenueQtr2Budget = gouRevenueQtr2Budget;
        this.gouRevenueQtr3Budget = gouRevenueQtr3Budget;
        this.gouRevenueQtr4Budget = gouRevenueQtr4Budget;
        this.extTotalRevenueBudget = extTotalRevenueBudget;
        this.extRevenueQtr1Budget = extRevenueQtr1Budget;
        this.extRevenueQtr2Budget = extRevenueQtr2Budget;
        this.extRevenueQtr3Budget = extRevenueQtr3Budget;
        this.extRevenueQtr4Budget = extRevenueQtr4Budget;
        this.projectedRevenueQtr1Budget = projectedRevenueQtr1Budget;
        this.projectedRevenueQtr2Budget = projectedRevenueQtr2Budget;
        this.projectedRevenueQtr3Budget = projectedRevenueQtr3Budget;
        this.projectedRevenueQtr4Budget = projectedRevenueQtr4Budget;
        this.revenueQtr1Actual = revenueQtr1Actual;
        this.revenueQtr2Actual = revenueQtr2Actual;
        this.revenueQtr3Actual = revenueQtr3Actual;
        this.revenueQtr4Actual = revenueQtr4Actual;
        this.revenueActual = revenueActual;
        this.opexBudget = opexBudget;
        this.capexBudget = capexBudget;
        this.opexActual = opexActual;
        this.capexActual = capexActual;
        this.igrTotalActualRevenue = igrTotalActualRevenue;
        this.gouTotalActualRevenue = gouTotalActualRevenue;
    }
    




    
}
