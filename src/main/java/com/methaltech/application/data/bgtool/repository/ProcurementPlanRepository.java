package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, Integer> {

    // You can add custom query methods if needed
    List<ProcurementPlan> findByBudget(Budget budget);

    @Query("""
    SELECT DISTINCT p
    FROM ProcurementPlan p
    LEFT JOIN FETCH p.procPlanBudgetItems bi
    LEFT JOIN FETCH bi.procurementMethod
    LEFT JOIN FETCH bi.procurementType
    LEFT JOIN FETCH bi.fundsource
    LEFT JOIN FETCH bi.coacode
    LEFT JOIN FETCH bi.deptUnit
    WHERE p.budget = :budget
""")
    List<ProcurementPlan> findProcurementPlansForExport(@Param("budget") Budget budget);

    List<ProcurementPlan> findByBudgetAndProcClass(Budget budget, ProcClass procClass);

    @Query("""
    SELECT DISTINCT p
    FROM ProcurementPlan p
    LEFT JOIN FETCH p.procPlanBudgetItems bi
    LEFT JOIN FETCH bi.procurementMethod
    LEFT JOIN FETCH bi.procurementType
    LEFT JOIN FETCH bi.fundsource
    LEFT JOIN FETCH bi.coacode
    LEFT JOIN FETCH bi.deptUnit
    WHERE p.budget = :budget
      AND p.procClass = :procClass
""")
    List<ProcurementPlan> findByBudgetAndProcClassWithItems(
            @Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM ProcurementPlan p WHERE p.budget = :budget")
    void deleteByBudget(@Param("budget") Budget budget);

    void deleteByBudgetAndCoa(Budget budget, COA coa);

    //List<ProcurementPlan> findByBudgetAndProcClassAndProcPlanBudgetItemsDeptUnitIn( Budget budget, ProcClass procClass, Collection<UrcDeptSectionAnlDimbgt> deptUnits);
    //Optional<ProcurementPlan> findByBudgetAndProcClassAndCoa(Budget budget, ProcClass procClass, COA coa);
    List<ProcurementPlan> findByBudgetAndProcClassAndCoa(Budget budget, ProcClass procClass, COA coa);

    ProcurementPlan findFirstByBudgetAndProcClassAndCoa(Budget budget, ProcClass procClass, COA coa);

    ProcurementPlan findFirstByProcPlanBudgetItems(BudgetItems budgetItem);

    @Modifying
    @Query(value = """
        DELETE FROM proc_plan_budget_items
        WHERE budget_item_id IN (:ids)
    """, nativeQuery = true)
    void detachBudgetItems(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = """
        DELETE FROM proc_plan_budget_items
        WHERE budget_item_id = (:ids)
    """, nativeQuery = true)
    void detachBudgetItems(@Param("ids") Long ids);

}
