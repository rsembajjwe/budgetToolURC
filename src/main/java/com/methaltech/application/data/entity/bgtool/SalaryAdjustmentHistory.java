package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.SalaryAdjustmentScope;
import com.methaltech.application.data.SalaryAdjustmentType;
import com.methaltech.application.data.salaryScale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salaryAdjustmentHistory")
@NoArgsConstructor
public @Data class SalaryAdjustmentHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "dsection_id")
    private UrcDeptSectionAnlDimbgt deptUnit;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation budgetType;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Urc_Activities activity;

    @ManyToOne
    @JoinColumn(name = "fundsource_id")
    private Fundsource fundsource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SalaryAdjustmentScope scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SalaryAdjustmentType adjustmentType;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private salaryScale grade;

    private Long selectedStaffSalaryId;
    private String selectedStaffCode;
    private String selectedStaffName;

    @Column(precision = 25, scale = 6)
    private BigDecimal adjustmentValue;

    private int affectedStaffCount;

    @Column(precision = 25, scale = 6)
    private BigDecimal oldMonthlyTotal;

    @Column(precision = 25, scale = 6)
    private BigDecimal newMonthlyTotal;

    @Column(precision = 25, scale = 6)
    private BigDecimal monthlyDifference;

    @Column(precision = 25, scale = 6)
    private BigDecimal annualDifference;

    private String appliedBy;
    private LocalDateTime appliedAt;

    @PrePersist
    public void prePersist() {
        if (appliedAt == null) {
            appliedAt = LocalDateTime.now();
        }
    }
}
