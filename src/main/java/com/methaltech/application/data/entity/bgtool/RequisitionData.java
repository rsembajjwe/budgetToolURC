package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "REQUISITION_DATA")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // 👈 Important
public class RequisitionData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // 👈 Use only ID
    private Long id;

    @Column(name = "REQUISITION_NUMBER", unique = true, nullable = false, length = 50)
    private String requisitionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID", nullable = true)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_SECTION", nullable = true)
    private UrcDeptSectionAnlDimbgt deptSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation", nullable = true)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coa", nullable = true)
    private COA coa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Urc_Activities activity;

    @Column(name = "REQUISITION_TYPE", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private RequisitionType requisitionType;

    @Column(name = "PROC_METHODS", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private ProcMethods procMethods;

    @Column(name = "PRIORITY_LEVEL", nullable = true)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @Column(name = "TOTAL_AMOUNT", nullable = true)
    private Double totalAmount;

    @Column(name = "TOTAL_ACTUAL_CODE", nullable = true)
    private Double totalActualByCode;
    
    @Column(name = "TOTAL_ACTUAL_CODE_ACTIVITY", nullable = true)
    private Double totalActualByCodeActivity;  
    
    @Column(name = "TOTAL_COMMITTED_CODE", nullable = true)
    private Double totalCommittedByCode;
    
    @Column(name = "TOTAL_COMMITTED_CODE_ACTIVITY", nullable = true)
    private Double totalCommittedByCodeActivity;    
    
    @Column(name = "AVAILABLE_BALANCE_BYCOA")
    private Double availableBalanceByCOA;

    @Column(name = "AVAILABLE_BALANCE_ACTIVITY")
    private Double availableBalanceByActivity;

    @Column(name = "PURPOSE", nullable = true, columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "JUSTIFICATION", columnDefinition = "TEXT")
    private String justification;

    @Column(name = "IS_MULTI_YEAR_CONTRACT")
    private IsMultiYearContract isMultiYearContract;

    @Column(name = "CONTRACT_DURATION", length = 100)
    private String contractDuration;

    @Column(name = "HODSTATUS", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private RequisitionStatus hodStatus;
    
    @Column(name = "HODCREATIONCREATED_DATE", nullable = true)
    private LocalDateTime hodStatusCreatedDate;    

    @Column(name = "CFOSTATUS", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private RequisitionStatus cfoStatus;
    
    @Column(name = "CFOCREATIONCREATED_DATE", nullable = true)
    private LocalDateTime cfoStatusCreatedDate;    

    @Column(name = "MDSTATUS", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private RequisitionStatus mdStatus;
    
    @Column(name = "MDCREATIONCREATED_DATE", nullable = true)
    private LocalDateTime mdStatusCreatedDate;     

    @Column(name = "STATUS", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;
    
    @Column(name = "STATUSCREATED_DATE", nullable = true)
    private LocalDateTime statusCreatedDate;     

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = true)
    private User createdBy;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;


    @Column(name = "LAST_UPDATED", nullable = true)
    private LocalDateTime lastUpdated;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_CREATED_BY", nullable = true)
    private User lastUpdatedBy;    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMITTED_BY", nullable = true)
    private User submittedBy;

    @Column(name = "SUBMITTED_DATE", nullable = true)
    private LocalDateTime submittedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVED_BY", nullable = true)
    private User approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "REJECTION_REASON", length = 500)
    private String rejectionReason;

    @Column(name = "PDF_FILE_PATH", length = 255)
    private String pdfFilePath;

    @Column(name = "CAT_OF_PROC", length = 30)
    @Enumerated(EnumType.STRING)
    private CatOfProc catOfProc;

    @Column(name = "REQUESTED_AMOUNT")
    private Double requestedAmount;

    @Column(name = "DELIVERY_LOCATION", length = 255)
    private String deliveryLocation;

    @Column(name = "PROC_TYPE", length = 30)
    @Enumerated(EnumType.STRING)
    private ProcType procType;

    // --- New fields for official PPDA Form 5 ---
    @Column(name = "PDE_CODE", length = 50)
    private String pdeCode;

    @Column(name = "PROCUREMENT_CATEGORY", length = 50)
    private String procurementCategory;

    @Column(name = "SEQUENCE_NUMBER", length = 50)
    private String sequenceNumber;

    @Column(name = "PROJECT_CODE", length = 50)
    private String projectCode;

    @Column(name = "PROJECT_TITLE", length = 255)
    private String projectTitle;

    // Particulars of procurement
    @Column(name = "SUBJECT_OF_PROCUREMENT", length = 255)
    private String subjectOfProcurement;

    @Column(name = "PROCUREMENT_PLAN_REF", length = 100)
    private String procurementPlanRef;

    @Column(name = "DATE_REQUIRED", nullable = true)
    private LocalDateTime dateRequired; // Could be LocalDate
    // Item details
    @Column(name = "UNIT_OF_MEASURE", length = 50)
    private String unitOfMeasure;

    private double marketPrice;

    @Column(name = "currency", length = 10)
    private String currency;
    @Column(name = "comment", length = 20)
    private String comment;

    @OneToMany(mappedBy = "requisitionData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionItem> items = new ArrayList<>();

    // ==================== ENUMS ====================
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
        APPROVED("Approved"),
        REJECTED("Rejected"),
        COMPLETED("Completed"),
        UNDER_REVIEW("Under Review");

        private final String displayName;

        RequisitionStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RequisitionType {
        CASH_REQUISITION("Cash Requisition"),
        FORM_5("Form 5"),
        FORM_48("Form 48");

        private final String displayName;

        RequisitionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ProcType {
        SUPPLIES("Supplies"),
        WORKS("Works"),
        CONSULTANCY("Consultancy"),
        NON_CONSULTANCY("Non Consultancy");

        private final String displayName;

        ProcType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ProcMethods {
        MICRO_PROCUREMENT("Micro Procurement"),
        RFQ("Request for Quotations (RFQ)"),
        RESTRICTED_BIDDING("Restricted Bidding"),
        OPEN_BIDDING("Open Bidding"),
        NOT_DETERMINED("Not Determined");

        private final String displayName;

        ProcMethods(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum CatOfProc {
        RECURRENT_BUDGET("Recurrent Budget"),
        DEVELOPMENT_BUDGET("Development Budget");

        private final String displayName;

        CatOfProc(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum IsMultiYearContract {
        YEAR_ONE("Required Resources (UGX Bn) Year One"),
        YEAR_TWO("Required Resources (UGX Bn) Year Two"),
        YEAR_THREE("Required Resources (UGX Bn) Year Three"),
        YEAR_FOUR("Required Resources (UGX Bn) Year Four"),;

        private final String displayName;

        IsMultiYearContract(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ==================== HELPERS ====================
    public ProcMethods setProcurementMethodByAmount(Double requestedAmount) {
        if (requestedAmount == null) {
            return ProcMethods.NOT_DETERMINED;
        }

        if (requestedAmount < 10_000_000) {
            return ProcMethods.MICRO_PROCUREMENT;
        } else if (requestedAmount >= 10_000_000 && requestedAmount < 200_000_000) {
            return ProcMethods.RFQ;
        } else if (requestedAmount >= 200_000_000 && requestedAmount <= 500_000_000) {
            return ProcMethods.RESTRICTED_BIDDING;
        } else {
            return ProcMethods.OPEN_BIDDING;
        }
    }

    public String getStatusColor() {
        switch (status) {
            case DRAFT:
                return "#6b7280";
            case SUBMITTED:
                return "#3b82f6";
            case APPROVED:
                return "#10b981";
            case REJECTED:
                return "#ef4444";
            case COMPLETED:
                return "#059669";
            case UNDER_REVIEW:
                return "#059669";
            default:
                return "#6b7280";
        }
    }

    public String getPriorityColor() {
        if (priorityLevel == null) {
            return "#6b7280"; // fallback color for null
        }

        switch (priorityLevel) {
            case LOW:
                return "#10b981";
            case MEDIUM:
                return "#f59e0b";
            case HIGH:
                return "#ef4444";
            case URGENT:
                return "#dc2626";
            case EMERGENCY:
                return "#991b1b";
            default:
                return "#6b7280";
        }
    }

    public boolean canBeEdited() {
        return status == RequisitionStatus.DRAFT;
    }

    public boolean canBeSubmitted() {
        return status == RequisitionStatus.DRAFT
                && totalAmount != null && totalAmount > 0
                && subjectOfProcurement != null && !subjectOfProcurement.trim().isEmpty()
                && !items.isEmpty();
    }

    public boolean canBeCancelled() {
        return status == RequisitionStatus.SUBMITTED
                || status == RequisitionStatus.UNDER_REVIEW;
    }

    public boolean canBeApproved() {
        return status == RequisitionStatus.SUBMITTED
                || status == RequisitionStatus.UNDER_REVIEW;
    }
    
    public boolean canBePrinted() {
        return status == RequisitionStatus.APPROVED;
    }    

    public boolean isVendorRequired() {
        return requisitionType == RequisitionType.FORM_5
                || requisitionType == RequisitionType.FORM_48;
    }

    public String getFormattedAmount() {
        if (totalAmount == null) {
            return "UGX 0";
        }
        return String.format("UGX %,.2f", totalAmount);
    }

    public String getFormattedBalance() {
        if (availableBalanceByActivity == null) {
            return "UGX 0";
        }
        return String.format("UGX %,.2f", availableBalanceByActivity);
    }

    public double getUtilizationPercentage() {
        if (availableBalanceByActivity == null || availableBalanceByActivity <= 0) {
            return 0;
        }
        return (totalAmount / availableBalanceByActivity) * 100;
    }

    public boolean isOverBudget() {
        return availableBalanceByActivity != null && totalAmount != null
                && totalAmount > availableBalanceByActivity;
    }

    public void addItem(RequisitionItem item) {
        items.add(item);
        item.setRequisitionData(this);
        updateTotalAmount();
    }

    public void removeItem(RequisitionItem item) {
        items.remove(item);
        item.setRequisitionData(null);
        updateTotalAmount();
    }

    public void updateTotalAmount() {
        this.totalAmount = items.stream()
                .mapToDouble(RequisitionItem::getTotalCost)
                .sum();
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        if (status == null) {
            status = RequisitionStatus.DRAFT;
            statusCreatedDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
