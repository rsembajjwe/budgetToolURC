package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.ReportingPeriodType;
import com.methaltech.application.data.entity.bgtool.URC_KPI_Performance_Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface URCKPIPerformanceRecordRepository extends JpaRepository<URC_KPI_Performance_Record, Long> {

    List<URC_KPI_Performance_Record> findByProgrammeAnnualBudgetId(Long programmeAnnualBudgetId);

    List<URC_KPI_Performance_Record> findByProgrammeAnnualBudgetIdAndReportingPeriod(
            Long programmeAnnualBudgetId,
            ReportingPeriodType reportingPeriod
    );

    @Query("""
        select r
        from URC_KPI_Performance_Record r
        join fetch r.kpi k
        join fetch k.programme p
        join fetch r.programmeAnnualBudget pab
        join fetch pab.budget b
        where b.id = :budgetId
        order by p.name, k.kpiName, r.reportingPeriod
    """)
    List<URC_KPI_Performance_Record> findByBudgetId(Long budgetId);
}
