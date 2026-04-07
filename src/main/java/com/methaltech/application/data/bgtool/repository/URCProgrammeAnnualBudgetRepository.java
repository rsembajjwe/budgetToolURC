package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.URC_Programme_Annual_Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface URCProgrammeAnnualBudgetRepository extends JpaRepository<URC_Programme_Annual_Budget, Long> {

    List<URC_Programme_Annual_Budget> findByProgrammeId(Long programmeId);

    Optional<URC_Programme_Annual_Budget> findByProgrammeIdAndBudgetId(Long programmeId, Long budgetId);

    List<URC_Programme_Annual_Budget> findByBudgetId(Long budgetId);
}
