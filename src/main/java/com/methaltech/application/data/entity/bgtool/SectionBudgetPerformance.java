package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table( name = "section_budget_performance")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SectionBudgetPerformance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    // === Relationships ===
    /**
     * The department/section related to this performance
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dsection_ids")
    private UrcDeptSectionAnlDimbgt deptSection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_ids")
    private Budget budget;

    // === Financial Data Fields ===
    @Column(name = "plannedBudget", precision = 18, scale = 2)
    private BigDecimal plannedBudget = BigDecimal.ZERO;

    @Column(name = "approvedBudget", precision = 18, scale = 2)
    private BigDecimal approvedBudget = BigDecimal.ZERO;

    @Column(name = "cumulative_funds_released1", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsReleased1 = BigDecimal.ZERO;

    @Column(name = "cumulative_funds_released2", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsReleased2 = BigDecimal.ZERO;

    @Column(name = "cumulative_funds_released3", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsReleased3 = BigDecimal.ZERO;

    @Column(name = "cumulative_funds_released4", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsReleased4 = BigDecimal.ZERO;

    @Column(name = "cumulative_funds_spent1", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsSpent1 = BigDecimal.ZERO;
    @Column(name = "cumulative_funds_spent2", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsSpent2 = BigDecimal.ZERO;
    @Column(name = "cumulative_funds_spent3", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsSpent3 = BigDecimal.ZERO;
    @Column(name = "cumulative_funds_spent4", precision = 18, scale = 2)
    private BigDecimal cumulativeFundsSpent4 = BigDecimal.ZERO;

    @Column(name = "percentage_spent1", length = 500)
    private String percentageSpent1;
    @Column(name = "percentage_spent2", length = 500)
    private String percentageSpent2;
    @Column(name = "percentage_spent3", length = 500)
    private String percentageSpent3;
    @Column(name = "percentage_spent4", length = 500)
    private String percentageSpent4;
 
    @Column(name = "reasons_for_under_over1", length = 500)
    private String reasonsForUnderOver1;
    @Column(name = "reasons_for_under_over2", length = 500)
    private String reasonsForUnderOver2;
    @Column(name = "reasons_for_under_over3", length = 500)
    private String reasonsForUnderOver3;
    @Column(name = "reasons_for_under_over4", length = 500)
    private String reasonsForUnderOver4;
    @Column(name = "submit_qtr1")
    private Boolean  submitQtr1;

    @Column(name = "submit_qtr2")
    private Boolean submitQtr2;

    @Column(name = "submit_qtr3")
    private Boolean submitQtr3;

    @Column(name = "submit_qtr4")
    private Boolean submitQtr4;

}
