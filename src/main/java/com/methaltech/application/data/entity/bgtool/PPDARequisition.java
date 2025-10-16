
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "PPDA_REQUISITIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PPDARequisition  implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "REQUISITION_NUMBER", unique = true, nullable = false)
    private String requisitionNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID", nullable = false)
    private Budget budget;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Urc_Activities activity;
    
    @Column(name = "ACCOUNT_CODE", nullable = false)
    private String accountCode;
    
    @Column(name = "ACCOUNT_NAME", nullable = false)
    private String accountName;
    
    @Column(name = "REQUISITION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequisitionType requisitionType;
    
    @Column(name = "REQUESTED_AMOUNT", nullable = false)
    private Double requestedAmount;
    
    @Column(name = "AVAILABLE_BALANCE")
    private Double availableBalance;
    
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
    
    @Column(name = "DELIVERY_DATE")
    private LocalDate deliveryDate;
    
    @Column(name = "DELIVERY_LOCATION", columnDefinition = "TEXT")
    private String deliveryLocation;
    
    @Column(name = "PAYMENT_TERMS")
    private String paymentTerms;
    
    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;
    
    @Column(name = "CREATED_BY", nullable = false)
    private String createdBy;
    
    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;
    
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "SUBMITTED_BY")
    private String submittedBy;
    
    @Column(name = "SUBMITTED_DATE")
    private LocalDateTime submittedDate;
    
    @Column(name = "APPROVED_BY")
    private String approvedBy;
    
    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;
    
    @Column(name = "REJECTION_REASON")
    private String rejectionReason;
    
    @Column(name = "PDF_FILE_PATH")
    private String pdfFilePath;
    
    @Column(name = "IS_MULTI_YEAR_CONTRACT")
    private Boolean isMultiYearContract = false;
    
    @Column(name = "CONTRACT_DURATION")
    private String contractDuration;
    
    public enum RequisitionType {
        CASH_REQUISITION("Cash Requisition"),
        FORM_5("PPDA Form 5 - Request for Quotations"),
        FORM_48("PPDA Form 48 - Local Purchase Order");

        private final String displayName;

        RequisitionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum RequisitionStatus {
        DRAFT("Draft"),
        SUBMITTED("Submitted"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        COMPLETED("Completed");
        
        private final String displayName;
        
        RequisitionStatus(String displayName) {
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
            case APPROVED: return "#10b981";
            case REJECTED: return "#ef4444";
            case COMPLETED: return "#059669";
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
    
    public boolean isVendorRequired() {
        return requisitionType == RequisitionType.FORM_5 || 
               requisitionType == RequisitionType.FORM_48;
    }
    
    public String getFormattedAmount() {
        if (requestedAmount == null) return "UGX 0";
        return String.format("UGX %,.2f", requestedAmount);
    }
    
    public String getFormattedBalance() {
        if (availableBalance == null) return "UGX 0";
        return String.format("UGX %,.2f", availableBalance);
    }
    
    public double getUtilizationPercentage() {
        if (availableBalance == null || availableBalance <= 0) return 0;
        return (requestedAmount / availableBalance) * 100;
    }
    
    public boolean isOverBudget() {
        return availableBalance != null && requestedAmount != null && 
               requestedAmount > availableBalance;
    }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        if (status == null) {
            status = RequisitionStatus.DRAFT;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
