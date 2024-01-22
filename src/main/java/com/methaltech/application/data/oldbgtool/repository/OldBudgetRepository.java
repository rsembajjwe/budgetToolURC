package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.OldBudget;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OldBudgetRepository extends JpaRepository<OldBudget, Integer> {

    OldBudget findByFinancialYear(String financialYear);
    Optional<OldBudget> findFirstByFinancialYear(String financialYear);
}
