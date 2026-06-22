package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.SalaryGradeCeilingType;
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
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salaryGradeCeiling")
@NoArgsConstructor
public @Data class SalaryGradeCeiling implements Serializable {

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
    private salaryScale grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SalaryGradeCeilingType ceilingType;

    @Column(precision = 25, scale = 6)
    private BigDecimal monthlyCeiling;
}
