
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "BUDGET_REQUISITIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetRequisition  implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "REQUISITION_NUMBER", unique = true, nullable = false)
    private String requisitionNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID", nullable = false)
    private Budget budget;
    
    @Column(name = "DEPARTMENT_CODE", nullable = false)
    private String departmentCode;
    
    @Column(name = "DEPARTMENT_NAME", nullable = false)
    private String departmentName;
    
    @Column(name = "SECTION_CODE", nullable = false)
    private String sectionCode;
    
    @Column(name = "SECTION_NAME", nullable = false)
    private String sectionName;
    
    @Column(name = "ACCOUNT_CODE", nullable = false)
    private String accountCode;
    
    @Column(name = "ACCOUNT_NAME", nullable = false)
    private String accountName;
    
    @Column(name = "REVENUE_SOURCE_CODE")
    private String revenueSourceCode;
    
    @Column(name = "REVENUE_SOURCE_NAME")
    private String revenueSourceName;
    
    @Column(name = "REQUISITION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequisitionType requisitionType;
    
    @Column(name = "REQUESTED_AMOUNT", nullable = false)
    private Double requestedAmount;
    
    @Column(name = "AVAILABLE_BUDGET")
    private Double availableBudget;
    
    @Column(name = "PURPOSE", nullable = false, columnDefinition = "TEXT")
    private String purpose;
    
    @Column(name = "JUSTIFICATION", columnDefinition = "TEXT")
    private String justification;
    
    @Column(name = "EXPECTED_DELIVERY_DATE")
    private LocalDateTime expectedDeliveryDate;
    
    @Column(name = "VENDOR_NAME")
    private String vendorName;
    
    @Column(name = "VENDOR_CONTACT")
    private String vendorContact;
    
    @Column(name = "PROCUREMENT_METHOD")
    @Enumerated(EnumType.STRING)
    private ProcurementMethod procurementMethod;
    
    @Column(name = "PRIORITY_LEVEL", nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;
    
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;
    
    @Column(name = "APPROVAL_STATUS")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    
    @Column(name = "CREATED_BY", nullable = false)
    private String createdBy;
    
    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;
    
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "APPROVED_BY")
    private String approvedBy;
    
    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;
    
    @Column(name = "REJECTED_BY")
    private String rejectedBy;
    
    @Column(name = "REJECTED_DATE")
    private LocalDateTime rejectedDate;
    
    @Column(name = "REJECTION_REASON")
    private String rejectionReason;
    
    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "SUPPORTING_DOCUMENTS")
    private String supportingDocuments;
    
    public enum RequisitionType {
        CONSULTANCY("Consultancy Services"),
        NON_CONSULTANCY("Non Consultancy Services"),
        SUPPLIES("Supplies"),
        WORKS("Works"),
        OTHER("Other");
        
        private final String displayName;
        
        RequisitionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ProcurementMethod {
        OPEN_TENDER("Open Tender"),
        RESTRICTED_TENDER("Restricted Tender"),
        DIRECT_PROCUREMENT("Direct Procurement"),
        QUOTATION("Quotation"),
        FRAMEWORK_AGREEMENT("Framework Agreement"),
        EMERGENCY_PROCUREMENT("Emergency Procurement");
        
        private final String displayName;
        
        ProcurementMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PriorityLevel {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        URGENT("Urgent"),
        EMERGENCY("Emergency");
        
        private final String displayName;
        
        PriorityLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum RequisitionStatus {
        DRAFT("Draft"),
        SUBMITTED("Submitted"),
        UNDER_REVIEW("Under Review"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed");
        
        private final String displayName;
        
        RequisitionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ApprovalStatus {
        PENDING("Pending Approval"),
        BLO_APPROVED("BLO Approved"),
        HOD_APPROVED("HOD Approved"),
        PROCUREMENT_APPROVED("Procurement Approved"),
        FINANCE_APPROVED("Finance Approved"),
        FINAL_APPROVED("Final Approved"),
        REJECTED("Rejected");
        
        private final String displayName;
        
        ApprovalStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Helper methods
    public String getStatusColor() {
        switch (status) {
            case DRAFT: return "#6b7280";
            case SUBMITTED: return "#3b82f6";
            case UNDER_REVIEW: return "#f59e0b";
            case APPROVED: return "#10b981";
            case REJECTED: return "#ef4444";
            case CANCELLED: return "#9ca3af";
            case COMPLETED: return "#059669";
            default: return "#6b7280";
        }
    }
    
    public String getPriorityColor() {
        switch (priorityLevel) {
            case LOW: return "#10b981";
            case MEDIUM: return "#f59e0b";
            case HIGH: return "#ef4444";
            case URGENT: return "#dc2626";
            case EMERGENCY: return "#991b1b";
            default: return "#6b7280";
        }
    }
    
    public boolean canBeEdited() {
        return status == RequisitionStatus.DRAFT;
    }
    
    public boolean canBeSubmitted() {
        return status == RequisitionStatus.DRAFT && 
               requestedAmount != null && requestedAmount > 0 &&
               purpose != null && !purpose.trim().isEmpty();
    }
    
    public boolean canBeCancelled() {
        return status == RequisitionStatus.SUBMITTED || 
               status == RequisitionStatus.UNDER_REVIEW;
    }
    
    public double getBudgetUtilizationPercentage() {
        if (availableBudget == null || availableBudget <= 0) return 0;
        return (requestedAmount / availableBudget) * 100;
    }
    
    public boolean isOverBudget() {
        return availableBudget != null && requestedAmount > availableBudget;
    }
    
    public boolean isHighValue() {
        return requestedAmount != null && requestedAmount > 100_000_000; // 100M UGX
    }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        if (status == null) {
            status = RequisitionStatus.DRAFT;
        }
        if (approvalStatus == null) {
            approvalStatus = ApprovalStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
