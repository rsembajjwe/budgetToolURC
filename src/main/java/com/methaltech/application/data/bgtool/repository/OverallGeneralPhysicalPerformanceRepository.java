package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.OverallGeneralPhysicalPerformance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OverallGeneralPhysicalPerformanceRepository
        extends JpaRepository<OverallGeneralPhysicalPerformance, Long> {

    Optional<OverallGeneralPhysicalPerformance> findByCodeAndBudget(String code, Budget budget);
}
