package com.methaltech.application.data.entity.bgtool;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "URC_CONSOLIDATED_PHYSICAL_PERFORMANCE_V")
@Getter
@Setter
@NoArgsConstructor
public class URC_Consolidated_Physical_Performance_V implements Serializable {

    @Id
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "budget_name")
    private String budgetName;

    @Column(name = "programme_id")
    private Long programmeId;

    @Column(name = "programme_name")
    private String programmeName;

    @Column(name = "department_code")
    private String departmentCode;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "section_name")
    private String sectionName;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "activity_code")
    private String activityCode;

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "logframe_type")
    private String logframeType;

    @Column(name = "logframe_description")
    private String logframeDescription;

    @Column(name = "kpi_name")
    private String kpiName;

    @Column(name = "unit")
    private String unit;

    @Column(name = "reporting_period")
    private String reportingPeriod;

    @Column(name = "target_value")
    private BigDecimal targetValue;

    @Column(name = "actual_value")
    private BigDecimal actualValue;

    @Column(name = "variance_value")
    private BigDecimal varianceValue;

    @Column(name = "performance_percent")
    private BigDecimal performancePercent;

    @Column(name = "status")
    private String status;

    @Column(name = "physical_performance")
    private String physicalPerformance;

    @Column(name = "key_achievements")
    private String keyAchievements;

    @Column(name = "challenges")
    private String challenges;

    @Column(name = "corrective_actions")
    private String correctiveActions;

    @Column(name = "means_of_verification")
    private String meansOfVerification;
}
