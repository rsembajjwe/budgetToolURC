
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, Integer> {
    // You can add custom query methods if needed
    List<ProcurementPlan> findByBudget(Budget budget);
}

