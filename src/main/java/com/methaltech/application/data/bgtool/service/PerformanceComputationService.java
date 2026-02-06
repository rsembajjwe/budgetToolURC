package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.GetPeriods;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PerformanceComputationService {

    GetPeriods periods = new GetPeriods();
    private final SALFLDGService samopleSALFLDGService;

    public PerformanceComputationService(
            SALFLDGService samopleSALFLDGService
    ) {
        this.samopleSALFLDGService = samopleSALFLDGService;
    }

    public BigDecimal getActualForQuarter(
            Budget budget,
            int quarter,
            Set<UrcDeptSectionAnlDimbgt> deptSections
    ) {
        if (budget == null || quarter < 1 || quarter > 4 || deptSections == null || deptSections.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Set<Integer> periodSet = new HashSet<>();

        for (int q = 1; q <= quarter; q++) {
            periodSet.addAll(periods.getFinancialYearPeriods(budget, q));
        }

        return samopleSALFLDGService
                .getTotalAmountByPeriods(
                        periodSet,
                        samopleSALFLDGService.extractTrimmedAnlCodes(deptSections)
                )
                .abs();
    }
}
