package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "BUDGET_APPROVALS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetApprovals  implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "REQUEST_ID", unique = true, nullable = false)
    private String requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID", nullable = false)
    private Budget budget;
    
    @Column(name = "DEPARTMENT_CODE", nullable = false)
    private String departmentCode;
    
    @Column(name = "DEPARTMENT_NAME", nullable = false)
    private String departmentName;
    
    @Column(name = "SECTION_CODE")
    private String sectionCode;
    
    @Column(name = "SECTION_NAME")
    private String sectionName;
    
    @Column(name = "REQUEST_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestType requestType;
    
    @Column(name = "REQUESTED_AMOUNT", nullable = false)
    private BigDecimal requestedAmount;
    
    @Column(name = "CURRENT_BUDGET")
    private BigDecimal currentBudget;
    
    @Column(name = "JUSTIFICATION", columnDefinition = "TEXT")
    private String justification;
    
    @Column(name = "SUPPORTING_DOCUMENTS")
    private String supportingDocuments;
    
    @Column(name = "CURRENT_STAGE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStage currentStage;
    
    @Column(name = "OVERALL_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus overallStatus;
    
    @Column(name = "PRIORITY_LEVEL", nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;
    
    @Column(name = "REQUESTED_BY", nullable = false)
    private String requestedBy;
    
    @Column(name = "REQUESTED_DATE", nullable = false)
    private LocalDateTime requestedDate;
    
    @Column(name = "BLO_APPROVED_BY")
    private String bloApprovedBy;
    
    @Column(name = "BLO_APPROVED_DATE")
    private LocalDateTime bloApprovedDate;
    
    @Column(name = "BLO_COMMENTS")
    private String bloComments;
    
    @Column(name = "HOD_APPROVED_BY")
    private String hodApprovedBy;
    
    @Column(name = "HOD_APPROVED_DATE")
    private LocalDateTime hodApprovedDate;
    
    @Column(name = "HOD_COMMENTS")
    private String hodComments;
    
    @Column(name = "ADMIN_APPROVED_BY")
    private String adminApprovedBy;
    
    @Column(name = "ADMIN_APPROVED_DATE")
    private LocalDateTime adminApprovedDate;
    
    @Column(name = "ADMIN_COMMENTS")
    private String adminComments;
    
    @Column(name = "CFO_APPROVED_BY")
    private String cfoApprovedBy;
    
    @Column(name = "CFO_APPROVED_DATE")
    private LocalDateTime cfoApprovedDate;
    
    @Column(name = "CFO_COMMENTS")
    private String cfoComments;
    
    @Column(name = "FINAL_APPROVED_AMOUNT")
    private BigDecimal finalApprovedAmount;
    
    @Column(name = "REJECTION_REASON")
    private String rejectionReason;
    
    @Column(name = "REJECTED_BY")
    private String rejectedBy;
    
    @Column(name = "REJECTED_DATE")
    private LocalDateTime rejectedDate;
    
    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;
    
    // Getters and Setters
    public LocalDateTime getRequestedDate() { 
        return requestedDate; 
    }
    
    public void setRequestedDate(LocalDateTime requestedDate) { 
        this.requestedDate = requestedDate; 
    }
    
    public enum RequestType {
        BUDGET_INCREASE("Budget Increase"),
        BUDGET_REALLOCATION("Budget Reallocation"),
        NEW_BUDGET_LINE("New Budget Line"),
        EMERGENCY_FUNDING("Emergency Funding"),
        BUDGET_TRANSFER("Budget Transfer");
        
        private final String displayName;
        
        RequestType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ApprovalStage {
        BLO_REVIEW("BLO Review"),
        HOD_REVIEW("HOD Review"),
        ADMIN_REVIEW("Admin Review"),
        CFO_REVIEW("CFO Review"),
        COMPLETED("Completed");
        
        private final String displayName;
        
        ApprovalStage(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ApprovalStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        ApprovalStatus(String displayName) {
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
        URGENT("Urgent");
        
        private final String displayName;
        
        PriorityLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Helper methods
    public boolean canBeApprovedBy(String role) {
        switch (currentStage) {
            case BLO_REVIEW:
                return "BLO".equals(role) || "ADMIN".equals(role);
            case HOD_REVIEW:
                return "HOD".equals(role) || "ADMIN".equals(role);
            case ADMIN_REVIEW:
                return "ADMIN".equals(role);
            case CFO_REVIEW:
                return "CFO".equals(role) || "ADMIN".equals(role);
            default:
                return false;
        }
    }
    
    public ApprovalStage getNextStage() {
        switch (currentStage) {
            case BLO_REVIEW:
                return ApprovalStage.HOD_REVIEW;
            case HOD_REVIEW:
                return ApprovalStage.ADMIN_REVIEW;
            case ADMIN_REVIEW:
                return ApprovalStage.CFO_REVIEW;
            case CFO_REVIEW:
                return ApprovalStage.COMPLETED;
            default:
                return currentStage;
        }
    }
    
    public String getStageColor() {
        switch (currentStage) {
            case BLO_REVIEW:
                return "#3B82F6";
            case HOD_REVIEW:
                return "#F59E0B";
            case ADMIN_REVIEW:
                return "#8B5CF6";
            case CFO_REVIEW:
                return "#EF4444";
            case COMPLETED:
                return "#10B981";
            default:
                return "#6B7280";
        }
    }
    
    public String getStatusColor() {
        switch (overallStatus) {
            case PENDING:
                return "#6B7280";
            case IN_PROGRESS:
                return "#3B82F6";
            case APPROVED:
                return "#10B981";
            case REJECTED:
                return "#EF4444";
            case CANCELLED:
                return "#F59E0B";
            default:
                return "#6B7280";
        }
    }
    
    public String getPriorityColor() {
        switch (priorityLevel) {
            case LOW:
                return "#10B981";
            case MEDIUM:
                return "#F59E0B";
            case HIGH:
                return "#EF4444";
            case URGENT:
                return "#DC2626";
            default:
                return "#6B7280";
        }
    }
    
    public int getDaysInCurrentStage() {
        LocalDateTime stageStartDate = getStageStartDate();
        if (stageStartDate != null) {
            return (int) java.time.Duration.between(stageStartDate, LocalDateTime.now()).toDays();
        }
        return 0;
    }
    
    private LocalDateTime getStageStartDate() {
        switch (currentStage) {
            case BLO_REVIEW:
                return requestedDate;
            case HOD_REVIEW:
                return bloApprovedDate;
            case ADMIN_REVIEW:
                return hodApprovedDate;
            case CFO_REVIEW:
                return adminApprovedDate;
            default:
                return requestedDate;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        if (requestedDate == null) {
            requestedDate = LocalDateTime.now();
        }
        if (requestedDate == null) {
            requestedDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
