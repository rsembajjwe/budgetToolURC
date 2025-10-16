
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "ACQUISITION_REQUESTS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquisitionRequest  implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ACQUISITION_NUMBER", unique = true, nullable = false)
    private String acquisitionNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID", nullable = false)
    private Budget budget;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANISATION_ID", nullable = false)
    private Organisation organisation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COA_ID", nullable = false)
    private COA coa;
    
    @Column(name = "DEPARTMENT_CODE")
    private String departmentCode;
    
    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
    
    @Column(name = "SECTION_CODE")
    private String sectionCode;
    
    @Column(name = "SECTION_NAME")
    private String sectionName;
    
    @Column(name = "ACQUISITION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private AcquisitionType acquisitionType;
    
    @Column(name = "REQUESTED_AMOUNT", nullable = false)
    private Double requestedAmount;
    
    @Column(name = "AVAILABLE_BUDGET")
    private Double availableBudget;
    
    @Column(name = "COMMITTED_AMOUNT")
    private Double committedAmount;
    
    @Column(name = "ACTUAL_SPENT")
    private Double actualSpent;
    
    @Column(name = "BALANCE_AMOUNT")
    private Double balanceAmount;
    
    @Column(name = "PURPOSE", nullable = false, columnDefinition = "TEXT")
    private String purpose;
    
    @Column(name = "JUSTIFICATION", columnDefinition = "TEXT")
    private String justification;
    
    @Column(name = "VENDOR_NAME")
    private String vendorName;
    
    @Column(name = "VENDOR_ADDRESS", columnDefinition = "TEXT")
    private String vendorAddress;
    
    @Column(name = "VENDOR_CONTACT")
    private String vendorContact;
    
    @Column(name = "VENDOR_TIN")
    private String vendorTin;
    
    @Column(name = "DELIVERY_LOCATION", columnDefinition = "TEXT")
    private String deliveryLocation;
    
    @Column(name = "DELIVERY_DATE")
    private LocalDate deliveryDate;
    
    @Column(name = "PAYMENT_TERMS")
    private String paymentTerms;
    
    @Column(name = "WARRANTY_PERIOD")
    private String warrantyPeriod;
    
    @Column(name = "PROCUREMENT_METHOD")
    @Enumerated(EnumType.STRING)
    private ProcurementMethod procurementMethod;
    
    @Column(name = "PRIORITY_LEVEL", nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;
    
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AcquisitionStatus status;
    
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
    
    // PPDA Uganda specific fields
    @Column(name = "PPDA_REFERENCE")
    private String ppdaReference;
    
    @Column(name = "PROCUREMENT_PLAN_REF")
    private String procurementPlanRef;
    
    @Column(name = "BUDGET_PROVISION_REF")
    private String budgetProvisionRef;
    
    @Column(name = "TECHNICAL_SPECIFICATIONS", columnDefinition = "TEXT")
    private String technicalSpecifications;
    
    @Column(name = "EVALUATION_CRITERIA", columnDefinition = "TEXT")
    private String evaluationCriteria;
    
    public enum AcquisitionType {
        CASH_REQUISITION("Cash Requisition"),
        LOCAL_PURCHASE_ORDER("Local Purchase Order");
        
        private final String displayName;
        
        AcquisitionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ProcurementMethod {
        MICRO_PROCUREMENT("Micro Procurement (Below UGX 1M)"),
        QUOTATIONS("Quotations (UGX 1M - 10M)"),
        SIMPLIFIED_PROCUREMENT("Simplified Procurement (UGX 10M - 200M)"),
        OPEN_DOMESTIC_BIDDING("Open Domestic Bidding (Above UGX 200M)"),
        RESTRICTED_DOMESTIC_BIDDING("Restricted Domestic Bidding"),
        DIRECT_PROCUREMENT("Direct Procurement"),
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
    
    public enum AcquisitionStatus {
        DRAFT("Draft"),
        SUBMITTED("Submitted"),
        UNDER_REVIEW("Under Review"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed");
        
        private final String displayName;
        
        AcquisitionStatus(String displayName) {
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
        return status == AcquisitionStatus.DRAFT;
    }
    
    public boolean canBeSubmitted() {
        return status == AcquisitionStatus.DRAFT && 
               requestedAmount != null && requestedAmount > 0 &&
               purpose != null && !purpose.trim().isEmpty();
    }
    
    public boolean canBeCancelled() {
        return status == AcquisitionStatus.SUBMITTED || 
               status == AcquisitionStatus.UNDER_REVIEW;
    }
    
    public boolean canBeApproved() {
        return status == AcquisitionStatus.SUBMITTED || 
               status == AcquisitionStatus.UNDER_REVIEW;
    }
    
    public double getBudgetUtilizationPercentage() {
        if (availableBudget == null || availableBudget <= 0) return 0;
        return (requestedAmount / availableBudget) * 100;
    }
    
    public boolean isOverBudget() {
        return availableBudget != null && requestedAmount > availableBudget;
    }
    
    public boolean isHighValue() {
        return requestedAmount != null && requestedAmount > 200_000_000; // 200M UGX - PPDA threshold
    }
    
    public String getProcurementMethodByAmount() {
        if (requestedAmount == null) return "Not Determined";
        
        if (requestedAmount < 1_000_000) {
            return "Micro Procurement";
        } else if (requestedAmount <= 10_000_000) {
            return "Quotations";
        } else if (requestedAmount <= 200_000_000) {
            return "Simplified Procurement";
        } else {
            return "Open Domestic Bidding";
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        if (status == null) {
            status = AcquisitionStatus.DRAFT;
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
