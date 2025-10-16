
package com.methaltech.application.data.entity.bgtool;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCeilingHierarchy {
    private String departmentCode;
    private String departmentName;
    private Double departmentCeiling;
    private Double departmentAllocated;
    private Double departmentSpent;
    private Double departmentCommitted;
    private List<SectionCeiling> sections = new ArrayList<>();
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionCeiling {
        private String sectionCode;
        private String sectionName;
        private Double sectionCeiling;
        private Double sectionAllocated;
        private Double sectionSpent;
        private Double sectionCommitted;
        private List<RevenueSourceCeiling> revenueSources = new ArrayList<>();
        
        public double getUtilizationPercentage() {
            return sectionCeiling > 0 ? ((sectionSpent + sectionCommitted) / sectionCeiling) * 100 : 0;
        }
        
        public double getAvailableAmount() {
            return sectionCeiling - sectionAllocated;
        }
        
        public String getStatusText() {
            double utilization = getUtilizationPercentage();
            if (utilization > 100) return "Over Ceiling";
            if (utilization > 95) return "Critical";
            if (utilization > 80) return "Near Ceiling";
            return "Within Ceiling";
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueSourceCeiling {
        private String revenueSourceCode;
        private String revenueSourceName;
        private Double revenueSourceCeiling;
        private Double revenueSourceAllocated;
        private Double revenueSourceSpent;
        private Double revenueSourceCommitted;
        private List<AccountCodeCeiling> accountCodes = new ArrayList<>();
        
        public double getUtilizationPercentage() {
            return revenueSourceCeiling > 0 ? ((revenueSourceSpent + revenueSourceCommitted) / revenueSourceCeiling) * 100 : 0;
        }
        
        public double getAvailableAmount() {
            return revenueSourceCeiling - revenueSourceAllocated;
        }
        
        public String getStatusText() {
            double utilization = getUtilizationPercentage();
            if (utilization > 100) return "Over Ceiling";
            if (utilization > 95) return "Critical";
            if (utilization > 80) return "Near Ceiling";
            return "Within Ceiling";
        }
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountCodeCeiling {
        private String accountCode;
        private String accountName;
        private Double accountCeiling;
        private Double accountAllocated;
        private Double accountSpent;
        private Double accountCommitted;
        
        public double getUtilizationPercentage() {
            return accountCeiling > 0 ? ((accountSpent + accountCommitted) / accountCeiling) * 100 : 0;
        }
        
        public double getAvailableAmount() {
            return accountCeiling - accountAllocated;
        }
        
        public String getStatusText() {
            double utilization = getUtilizationPercentage();
            if (utilization > 100) return "Over Ceiling";
            if (utilization > 95) return "Critical";
            if (utilization > 80) return "Near Ceiling";
            return "Within Ceiling";
        }
    }
    
    public double getDepartmentUtilizationPercentage() {
        return departmentCeiling > 0 ? ((departmentSpent + departmentCommitted) / departmentCeiling) * 100 : 0;
    }
    
    public double getDepartmentAvailableAmount() {
        return departmentCeiling - departmentAllocated;
    }
    
    public String getDepartmentStatusText() {
        double utilization = getDepartmentUtilizationPercentage();
        if (utilization > 100) return "Over Ceiling";
        if (utilization > 95) return "Critical";
        if (utilization > 80) return "Near Ceiling";
        return "Within Ceiling";
    }
}
