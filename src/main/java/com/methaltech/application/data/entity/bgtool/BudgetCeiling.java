package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "BUDGET_CEILINGS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCeiling  implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID", nullable = false)
    private Budget budget;

    @Column(name = "CEILING_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private CeilingType ceilingType;

    @Column(name = "DEPARTMENT_CODE")
    private String departmentCode;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;

    @Column(name = "SECTION_CODE")
    private String sectionCode;

    @Column(name = "SECTION_NAME")
    private String sectionName;

    @Column(name = "REVENUE_SOURCE_CODE")
    private String revenueSourceCode;

    @Column(name = "REVENUE_SOURCE_NAME")
    private String revenueSourceName;

    @Column(name = "ACCOUNT_CODE")
    private String accountCode;

    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    @Column(name = "CEILING_AMOUNT", nullable = false)
    private Double ceilingAmount;

    @Column(name = "ALLOCATED_AMOUNT")
    private Double allocatedAmount = 0.0;

    @Column(name = "SPENT_AMOUNT")
    private Double spentAmount = 0.0;

    @Column(name = "COMMITTED_AMOUNT")
    private Double committedAmount = 0.0;

    @Column(name = "CEILING_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private CeilingStatus ceilingStatus;

    @Column(name = "EFFECTIVE_DATE", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED_BY", nullable = false)
    private String createdBy;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "APPROVAL_STATUS")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "APPROVED_BY")
    private String approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "NOTES")
    private String notes;

    public enum CeilingType {
        SECTION("Section Level"),
        REVENUE_SOURCE("Revenue Source Level"),
        ACCOUNT_CODE("Account Code Level");

        private final String displayName;

        CeilingType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum CeilingStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUSPENDED("Suspended"),
        EXPIRED("Expired");

        private final String displayName;

        CeilingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ApprovalStatus {
        PENDING("Pending Approval"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        DRAFT("Draft");

        private final String displayName;

        ApprovalStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public double getAvailableAmount() {
        return ceilingAmount - allocatedAmount;
    }

    public double getRemainingAmount() {
        return ceilingAmount - spentAmount - committedAmount;
    }

    public double getUtilizationPercentage() {
        return ceilingAmount > 0 ? ((spentAmount + committedAmount) / ceilingAmount) * 100 : 0;
    }

    public double getSpentPercentage() {
        return ceilingAmount > 0 ? (spentAmount / ceilingAmount) * 100 : 0;
    }

    public double getAllocationPercentage() {
        return ceilingAmount > 0 ? (allocatedAmount / ceilingAmount) * 100 : 0;
    }

    public boolean isOverCeiling() {
        return (spentAmount + committedAmount) > ceilingAmount;
    }

    public boolean isNearCeiling() {
        double utilization = getUtilizationPercentage();
        return utilization > 80 && utilization <= 95;
    }

    public boolean isCritical() {
        return getUtilizationPercentage() > 95;
    }

    public String getStatusColor() {
        if (isOverCeiling()) {
            return "#dc2626"; // Red
        } else if (isCritical()) {
            return "#f59e0b"; // Orange
        } else if (isNearCeiling()) {
            return "#eab308"; // Yellow
        } else {
            return "#10b981"; // Green
        }
    }

    public String getStatusText() {
        if (isOverCeiling()) {
            return "Over Ceiling";
        } else if (isCritical()) {
            return "Critical";
        } else if (isNearCeiling()) {
            return "Near Ceiling";
        } else {
            return "Within Ceiling";
        }
    }

    public String getHierarchyPath() {
        StringBuilder path = new StringBuilder();

        if (departmentName != null) {
            path.append(departmentName);
        }

        if (sectionName != null) {
            if (path.length() > 0) {
                path.append(" > ");
            }
            path.append(sectionName);
        }

        if (revenueSourceName != null) {
            if (path.length() > 0) {
                path.append(" > ");
            }
            path.append(revenueSourceName);
        }

        if (accountName != null) {
            if (path.length() > 0) {
                path.append(" > ");
            }
            path.append(accountName);
        }

        return path.toString();
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return ceilingStatus == CeilingStatus.ACTIVE
                && approvalStatus == ApprovalStatus.APPROVED
                && effectiveDate.isBefore(now)
                && (expiryDate == null || expiryDate.isAfter(now));
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        if (effectiveDate == null) {
            effectiveDate = LocalDateTime.now();
        }
        if (approvalStatus == null) {
            approvalStatus = ApprovalStatus.DRAFT;
        }
        if (ceilingStatus == null) {
            ceilingStatus = CeilingStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
