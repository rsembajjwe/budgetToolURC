
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "REQUISITION_ITEMS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionItem  implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUISITION_DATA_ID", nullable = false)
    private RequisitionData requisitionData;
    
    @Column(name = "ITEM_NUMBER", nullable = false)
    private Integer itemNumber;
    
    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "QUANTITY", nullable = false)
    private Double quantity;
    
    @Column(name = "UNIT_OF_MEASURE", nullable = false)
    private String unitOfMeasure;
    
    @Column(name = "ESTIMATED_UNIT_COST", nullable = false)
    private Double estimatedUnitCost;
    
    @Column(name = "MARKET_PRICE")
    private Double marketPrice;
    
    @Column(name = "TOTAL_COST", nullable = true)
    private Double totalCost;
    
    @Column(name = "NOTES")
    private String notes;
    
    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;
    
    // Setter method for item number
    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }
    
    // Getter method for item number  
    public Integer getItemNumber() {
        return this.itemNumber;
    }
    
    // Helper methods
    public String getFormattedUnitCost() {
        if (estimatedUnitCost == null) return "UGX 0";
        return String.format("UGX %,.2f", estimatedUnitCost);
    }
    
    public String getFormattedTotalCost() {
        if (totalCost == null) return "UGX 0";
        return String.format("UGX %,.2f", totalCost);
    }
    
    public String getFormattedMarketPrice() {
        if (marketPrice == null) return "N/A";
        return String.format("UGX %,.2f", marketPrice);
    }
    
    public void calculateTotalCost() {
        if (quantity != null && estimatedUnitCost != null) {
            this.totalCost = quantity * estimatedUnitCost;
        } else {
            this.totalCost = 0.0;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
        calculateTotalCost();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        calculateTotalCost();
    }
    
    // Relationship management methods
    public void setRequisitionData(RequisitionData requisitionData) {
        this.requisitionData = requisitionData;
    }
    
    public RequisitionData getRequisitionData() {
        return this.requisitionData;
    }
}
