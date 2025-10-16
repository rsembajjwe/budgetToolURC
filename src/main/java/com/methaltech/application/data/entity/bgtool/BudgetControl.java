
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "BUDGET_CONTROLS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetControl  implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUDGET_ID", nullable = false)
    private Budget budget;
    
    // Equals and hashCode to avoid lazy loading issues
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BudgetControl)) return false;
        BudgetControl that = (BudgetControl) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Column(name = "CONTROL_LEVEL", nullable = false)
    @Enumerated(EnumType.STRING)
    private ControlLevel controlLevel;
    
    @Column(name = "DEPARTMENT_CODE")
    private String departmentCode;
    
    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
    
    @Column(name = "SECTION_CODE")
    private String sectionCode;
    
    @Column(name = "SECTION_NAME")
    private String sectionName;
    
    @Column(name = "BUDGET_CHECK_ENABLED", nullable = false)
    private Boolean budgetCheckEnabled = false;
    
    @Column(name = "BUDGET_STOP_ENABLED", nullable = false)
    private Boolean budgetStopEnabled = false;
    
    @Column(name = "POSTING_PROHIBITED", nullable = false)
    private Boolean postingProhibited = false;
    
    @Column(name = "CONTROL_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ControlStatus controlStatus;
    
    @Column(name = "EFFECTIVE_DATE", nullable = false)
    private LocalDateTime effectiveDate;
    
    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;
    
    @Column(name = "REASON")
    private String reason;
    
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
    
    public enum ControlLevel {
        BUDGET("Budget Level"),
        DEPARTMENT("Department Level"),
        SECTION("Section Level");
        
        private final String displayName;
        
        ControlLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ControlStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUSPENDED("Suspended"),
        EXPIRED("Expired");
        
        private final String displayName;
        
        ControlStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Helper methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return controlStatus == ControlStatus.ACTIVE &&
               effectiveDate.isBefore(now) &&
               (expiryDate == null || expiryDate.isAfter(now));
    }
    
    public String getControlSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (budgetCheckEnabled) {
            summary.append("Budget Check");
        }
        
        if (budgetStopEnabled) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Budget Stop");
        }
        
        if (postingProhibited) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Posting Prohibited");
        }
        
        return summary.length() > 0 ? summary.toString() : "No Controls";
    }
    
    public String getHierarchyPath() {
        StringBuilder path = new StringBuilder();
        
        if (departmentName != null) {
            path.append(departmentName);
        }
        
        if (sectionName != null) {
            if (path.length() > 0) path.append(" > ");
            path.append(sectionName);
        }
        
        return path.toString();
    }
    
    public boolean hasAnyControlEnabled() {
        return budgetCheckEnabled || budgetStopEnabled || postingProhibited;
    }
    
    public int getActiveControlsCount() {
        int count = 0;
        if (budgetCheckEnabled) count++;
        if (budgetStopEnabled) count++;
        if (postingProhibited) count++;
        return count;
    }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        if (effectiveDate == null) {
            effectiveDate = LocalDateTime.now();
        }
        if (controlStatus == null) {
            controlStatus = ControlStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
