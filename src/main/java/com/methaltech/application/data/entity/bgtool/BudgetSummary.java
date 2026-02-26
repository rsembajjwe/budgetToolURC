package com.methaltech.application.data.entity.bgtool;

import java.util.List;

public class BudgetSummary {

    private double totalBudget;
    private double totalSpent;
    private double totalCommitted;
    private double totalRevenue;
    private double projectedRevenue;
    private double cumQtr1Budget;
    private double cumQtr2Budget;
    private double cumQtr3Budget;
    private double cumQtr4Budget;

    private double cumQtr1Actual;
    private double cumQtr2Actual;
    private double cumQtr3Actual;
    private double cumQtr4Actual;
    private List<DepartmentBudget> departmentBudgets;
    private List<RevenueSource> revenueSources;

    public BudgetSummary() {
    }

    public BudgetSummary(double totalBudget, double totalCommitted, double totalSpent, double totalRevenue,
            double projectedRevenue, List<DepartmentBudget> departmentBudgets,
            List<RevenueSource> revenueSources) {
        this.totalBudget = totalBudget;
        this.totalCommitted = totalCommitted;
        this.totalSpent = totalSpent;
        this.totalRevenue = totalRevenue;
        this.projectedRevenue = projectedRevenue;
        this.departmentBudgets = departmentBudgets;
        this.revenueSources = revenueSources;
    }

    public BudgetSummary(double totalBudget, double totalSpent, double totalCommitted, double totalRevenue, double projectedRevenue, double cumQtr1Budget, double cumQtr2Budget, double cumQtr3Budget, double cumQtr4Budget, double cumQtr1Actual, double cumQtr2Actual, double cumQtr3Actual, double cumQtr4Actual, List<DepartmentBudget> departmentBudgets,List<RevenueSource> revenueSources) {
        this.totalBudget = totalBudget;
        this.totalSpent = totalSpent;
        this.totalCommitted = totalCommitted;
        this.totalRevenue = totalRevenue;
        this.projectedRevenue = projectedRevenue;
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

    // Getters and setters
    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public double getTotalCommitted() {
        return totalCommitted;
    }

    public void setTotalCommitted(double totalCommitted) {
        this.totalCommitted = totalCommitted;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getProjectedRevenue() {
        return projectedRevenue;
    }

    public void setProjectedRevenue(double projectedRevenue) {
        this.projectedRevenue = projectedRevenue;
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

    public double getSpentPercentage() {
        return totalBudget > 0 ? ((totalSpent) / totalBudget) * 100 : 0;
    }

    public double getCommittedPercentage() {
        return totalBudget > 0 ? ((totalCommitted) / totalBudget) * 100 : 0;
    }

    public double getRevenuePercentage() {
        return projectedRevenue > 0 ? (totalRevenue / projectedRevenue) * 100 : 0;
    }

    public double getRemainingBudget() {
        return totalBudget - (totalSpent + totalCommitted);
    }

    public double getCumQtr1Budget() {
        return cumQtr1Budget;
    }

    public void setCumQtr1Budget(double cumQtr1Budget) {
        this.cumQtr1Budget = cumQtr1Budget;
    }

    public double getCumQtr2Budget() {
        return cumQtr2Budget;
    }

    public void setCumQtr2Budget(double cumQtr2Budget) {
        this.cumQtr2Budget = cumQtr2Budget;
    }

    public double getCumQtr3Budget() {
        return cumQtr3Budget;
    }

    public void setCumQtr3Budget(double cumQtr3Budget) {
        this.cumQtr3Budget = cumQtr3Budget;
    }

    public double getCumQtr4Budget() {
        return cumQtr4Budget;
    }

    public void setCumQtr4Budget(double cumQtr4Budget) {
        this.cumQtr4Budget = cumQtr4Budget;
    }

    public double getCumQtr1Actual() {
        return cumQtr1Actual;
    }

    public void setCumQtr1Actual(double cumQtr1Actual) {
        this.cumQtr1Actual = cumQtr1Actual;
    }

    public double getCumQtr2Actual() {
        return cumQtr2Actual;
    }

    public void setCumQtr2Actual(double cumQtr2Actual) {
        this.cumQtr2Actual = cumQtr2Actual;
    }

    public double getCumQtr3Actual() {
        return cumQtr3Actual;
    }

    public void setCumQtr3Actual(double cumQtr3Actual) {
        this.cumQtr3Actual = cumQtr3Actual;
    }

    public double getCumQtr4Actual() {
        return cumQtr4Actual;
    }

    public void setCumQtr4Actual(double cumQtr4Actual) {
        this.cumQtr4Actual = cumQtr4Actual;
    }
    

    // Legacy methods for backward compatibility
    @Deprecated
    public List<Department> getDepartments() {
        // This method is kept for backward compatibility but should not be used
        return null;
    }
}
