
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.PerformanceReportContext;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PerformanceContextBuilder {

    public PerformanceReportContext build(
            Budget budget,
            int quarter,
            Map<String, SectionBudgetPerformance> performanceMap,
            List<PriorityArea> priorityAreas
    ) {
        return PerformanceReportContext.builder()
                .budget(budget)
                .financialYear(budget.getFinancialYear())
                .quarter(quarter)
                .performanceMap(performanceMap)
                .priorityAreas(priorityAreas)
                .generatedOn(LocalDateTime.now())
                .build();
    }
}
