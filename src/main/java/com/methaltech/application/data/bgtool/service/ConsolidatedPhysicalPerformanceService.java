
package com.methaltech.application.data.bgtool.service;
import com.methaltech.application.data.bgtool.repository.URCConsolidatedPhysicalPerformanceViewRepository;
import com.methaltech.application.data.entity.bgtool.URC_Consolidated_Physical_Performance_V;
import com.methaltech.application.data.entity.bgtool.dto.ConsolidatedPhysicalPerformanceRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsolidatedPhysicalPerformanceService {

    private final URCConsolidatedPhysicalPerformanceViewRepository repository;

    public List<ConsolidatedPhysicalPerformanceRow> getReportByBudget(Long budgetId) {
        return repository.findByBudgetId(budgetId)
                .stream()
                .map(this::map)
                .toList();
    }

private ConsolidatedPhysicalPerformanceRow map(URC_Consolidated_Physical_Performance_V v) {
    return ConsolidatedPhysicalPerformanceRow.builder()
            .budgetId(v.getBudgetId())
            .budgetName(v.getBudgetName())
            .programmeId(v.getProgrammeId())
            .programmeName(v.getProgrammeName())
            .departmentCode(v.getDepartmentCode())
            .departmentName(v.getDepartmentName())
            .sectionCode(v.getSectionCode())
            .sectionName(v.getSectionName())

            .activityId(v.getActivityId())
            .activityCode(v.getActivityCode())
            .activityName(v.getActivityName())

            .logframeType(v.getLogframeType())
            .logframeDescription(v.getLogframeDescription())
            .kpiName(v.getKpiName())
            .unit(v.getUnit())
            .reportingPeriod(v.getReportingPeriod())

            .targetValue(v.getTargetValue())
            .actualValue(v.getActualValue())
            .varianceValue(v.getVarianceValue())
            .performancePercent(v.getPerformancePercent())

            .status(v.getStatus())
            .physicalPerformance(v.getPhysicalPerformance())
            .keyAchievements(v.getKeyAchievements())
            .challenges(v.getChallenges())
            .correctiveActions(v.getCorrectiveActions())
            .meansOfVerification(v.getMeansOfVerification())
            .build();
}
}
