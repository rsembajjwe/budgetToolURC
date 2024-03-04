package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, Integer> {

    // You can add custom query methods if needed
    List<ProcurementPlan> findByBudget(Budget budget);

    List<ProcurementPlan> findByBudgetAndProcClass(Budget budget, ProcClass procClass);

    @Transactional
    void deleteByBudget(Budget budget);

    void deleteByBudgetAndCoa(Budget budget, COA coa);

    List<ProcurementPlan> findByBudgetAndProcClassAndProcPlanBudgetItemsDeptUnitIn(
            Budget budget, ProcClass procClass, Collection<UrcDeptSectionAnlDimbgt> deptUnits);
    
}
