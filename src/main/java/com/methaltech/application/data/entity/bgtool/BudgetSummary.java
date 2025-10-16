
package com.methaltech.application.data.entity.bgtool;

import java.util.List;

public class BudgetSummary {
    private double totalBudget;
    private double totalSpent;
    private double totalCommitted;
    private double totalRevenue;
    private double projectedRevenue;
    private List<DepartmentBudget> departmentBudgets;
    private List<RevenueSource> revenueSources;

    public BudgetSummary() {}

    public BudgetSummary(double totalBudget, double totalCommitted,double totalSpent, double totalRevenue, 
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

    // Getters and setters
    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
    
    public double getTotalCommitted() { return totalCommitted; }
    public void setTotalCommitted(double totalCommitted) { this.totalCommitted = totalCommitted; }    

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getProjectedRevenue() { return projectedRevenue; }
    public void setProjectedRevenue(double projectedRevenue) { this.projectedRevenue = projectedRevenue; }

    public List<DepartmentBudget> getDepartmentBudgets() { return departmentBudgets; }
    public void setDepartmentBudgets(List<DepartmentBudget> departmentBudgets) { this.departmentBudgets = departmentBudgets; }

    public List<RevenueSource> getRevenueSources() { return revenueSources; }
    public void setRevenueSources(List<RevenueSource> revenueSources) { this.revenueSources = revenueSources; }

    public double getSpentPercentage() {
        return totalBudget > 0 ? ((totalSpent )/ totalBudget) * 100 : 0;
    }
    
    public double getCommittedPercentage() {
        return totalBudget > 0 ? ((totalCommitted )/ totalBudget) * 100 : 0;
    }    

    public double getRevenuePercentage() {
        return projectedRevenue > 0 ? (totalRevenue / projectedRevenue) * 100 : 0;
    }

    public double getRemainingBudget() {
        return totalBudget - (totalSpent+totalCommitted);
    }

    // Legacy methods for backward compatibility
    @Deprecated
    public List<Department> getDepartments() {
        // This method is kept for backward compatibility but should not be used
        return null;
    }
}
