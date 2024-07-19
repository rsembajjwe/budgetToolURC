
package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "totalbudget_ceiling")
@NoArgsConstructor
public @Data class TotalBudgetCeiling {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    @OneToOne
    @JoinColumn(name = "budget_id")
    private Budget budget; 
    private BigDecimal budget_ceiling;
}
