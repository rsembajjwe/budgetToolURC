
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(
        name = "urc_kpi_performance_records",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"kpi_id", "programme_annual_budget_id", "reporting_period"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class URC_KPI_Performance_Record implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", nullable = false)
    private URC_Programme_KPI kpi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_annual_budget_id", nullable = false)
    private URC_Programme_Annual_Budget programmeAnnualBudget;

    @Enumerated(EnumType.STRING)
    @Column(name = "reporting_period", nullable = false, length = 20)
    private ReportingPeriodType reportingPeriod;

    @Column(name = "target_value", precision = 18, scale = 2, nullable = false)
    private BigDecimal targetValue;

    @Column(name = "actual_value", precision = 18, scale = 2)
    private BigDecimal actualValue;

    @Column(name = "variance_value", precision = 18, scale = 2)
    private BigDecimal varianceValue;

    @Column(name = "performance_percent", precision = 18, scale = 2)
    private BigDecimal performancePercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private MonitoringStatus status = MonitoringStatus.NOT_STARTED;

    @Column(name = "physical_performance", length = 3000)
    private String physicalPerformance;

    @Column(name = "key_achievements", length = 3000)
    private String keyAchievements;

    @Column(name = "challenges", length = 3000)
    private String challenges;

    @Column(name = "corrective_actions", length = 3000)
    private String correctiveActions;

    @Column(name = "means_of_verification", length = 2000)
    private String meansOfVerification;

    @Column(name = "assumptions", length = 2000)
    private String assumptions;

    @Column(name = "reporting_date")
    private LocalDate reportingDate;

    @Transient
    public Budget getBudget() {
        return programmeAnnualBudget != null ? programmeAnnualBudget.getBudget() : null;
    }

    @Transient
    public Integer getBudgetYear() {
        LocalDate start = programmeAnnualBudget != null ? programmeAnnualBudget.getBudgetStartDate() : null;
        return start != null ? start.getYear() : null;
    }

    @PrePersist
    @PreUpdate
    public void calculateMetrics() {
        BigDecimal target = targetValue != null ? targetValue : BigDecimal.ZERO;
        BigDecimal actual = actualValue != null ? actualValue : BigDecimal.ZERO;

        varianceValue = actual.subtract(target);

        if (target.compareTo(BigDecimal.ZERO) > 0) {
            performancePercent = actual
                    .divide(target, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            performancePercent = BigDecimal.ZERO;
        }
    }
}
