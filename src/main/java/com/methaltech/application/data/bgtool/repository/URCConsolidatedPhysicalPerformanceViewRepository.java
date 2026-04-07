package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.URC_Consolidated_Physical_Performance_V;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface URCConsolidatedPhysicalPerformanceViewRepository
        extends JpaRepository<URC_Consolidated_Physical_Performance_V, Long> {

    List<URC_Consolidated_Physical_Performance_V> findByBudgetId(Long budgetId);

    List<URC_Consolidated_Physical_Performance_V> findByBudgetIdAndProgrammeId(
            Long budgetId,
            Long programmeId
    );

    List<URC_Consolidated_Physical_Performance_V> findByBudgetIdAndDepartmentCode(
            Long budgetId,
            String departmentCode
    );
}
