package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.DepartmentGeneralPhysicalPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentGeneralPhysicalPerformanceRepository
        extends JpaRepository<DepartmentGeneralPhysicalPerformance, Long> {

    Optional<DepartmentGeneralPhysicalPerformance> findByCodeAndBudget(String code, Budget budget);
}
