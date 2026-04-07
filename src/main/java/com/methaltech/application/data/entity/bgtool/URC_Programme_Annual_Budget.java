
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "urc_programme_annual_budgets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"programme_id", "budget_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class URC_Programme_Annual_Budget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private URC_Priority_Areas programme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(name = "allocated_amount", precision = 18, scale = 2)
    private BigDecimal allocatedAmount;

    @Column(name = "released_amount", precision = 18, scale = 2)
    private BigDecimal releasedAmount;

    @Column(name = "spent_amount", precision = 18, scale = 2)
    private BigDecimal spentAmount;

    @Column(name = "committed_amount", precision = 18, scale = 2)
    private BigDecimal committedAmount;

    @Column(name = "notes", length = 3000)
    private String notes;

    @Transient
    public LocalDate getBudgetStartDate() {
        return budget != null ? budget.getStartDate() : null;
    }

    @Transient
    public LocalDate getBudgetEndDate() {
        return budget != null ? budget.getCloseDate() : null;
    }
}
