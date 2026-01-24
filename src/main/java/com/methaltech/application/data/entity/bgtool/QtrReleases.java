package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "qtr_releases",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organisation_id", "budget_id", "dsection_ids"})
        },
        indexes = {
            @Index(name = "idx_qtr_org", columnList = "organisation_id"),
            @Index(name = "idx_qtr_budget", columnList = "budget_id"),
            @Index(name = "idx_qtr_dept_section", columnList = "dsection_ids")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class QtrReleases implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    // === Relationships ===
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    /**
     * If quarter releases are also tracked per section/department. If not
     * needed, you can remove this.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dsection_ids")
    private UrcDeptSectionAnlDimbgt deptSection;

    // === Quarter Releases ===
    @Column(name = "qtr1_release", precision = 18, scale = 2, nullable = false)
    private BigDecimal qtr1Release = BigDecimal.ZERO;

    @Column(name = "qtr2_release", precision = 18, scale = 2, nullable = false)
    private BigDecimal qtr2Release = BigDecimal.ZERO;

    @Column(name = "qtr3_release", precision = 18, scale = 2, nullable = false)
    private BigDecimal qtr3Release = BigDecimal.ZERO;

    @Column(name = "qtr4_release", precision = 18, scale = 2, nullable = false)
    private BigDecimal qtr4Release = BigDecimal.ZERO;

    @Column(name = "reasons_for_under_over1", length = 500)
    private String reasonsForUnderOver1;
    @Column(name = "reasons_for_under_over2", length = 500)
    private String reasonsForUnderOver2;
    @Column(name = "reasons_for_under_over3", length = 500)
    private String reasonsForUnderOver3;
    @Column(name = "reasons_for_under_over4", length = 500)
    private String reasonsForUnderOver4;
  

    // === Audit Fields (optional but useful) ===
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        sanitizeNumbers();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        sanitizeNumbers();
    }

    private void sanitizeNumbers() {
        qtr1Release = nvl(qtr1Release);
        qtr2Release = nvl(qtr2Release);
        qtr3Release = nvl(qtr3Release);
        qtr4Release = nvl(qtr4Release);
       
    }

    // === Convenience totals ===
    @Transient
    public BigDecimal getTotalReleased() {
        return nvl(qtr1Release)
                .add(nvl(qtr2Release))
                .add(nvl(qtr3Release))
                .add(nvl(qtr4Release));
    }
   

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
