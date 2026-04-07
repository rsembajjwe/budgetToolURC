package com.methaltech.application.data.entity.bgtool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsolidatedPhysicalPerformanceRow {

    private Long budgetId;
    private String budgetName;

    private Long programmeId;
    private String programmeName;

    private String departmentCode;
    private String departmentName;

    private String sectionCode;
    private String sectionName;

    // 🔥 ADD THESE
    private Long activityId;
    private String activityCode;
    private String activityName;

    private String logframeType;
    private String logframeDescription;

    private String kpiName;
    private String unit;
    private String reportingPeriod;

    private BigDecimal targetValue;
    private BigDecimal actualValue;
    private BigDecimal varianceValue;
    private BigDecimal performancePercent;

    private String status;
    private String physicalPerformance;
    private String keyAchievements;
    private String challenges;
    private String correctiveActions;
    private String meansOfVerification;
}
