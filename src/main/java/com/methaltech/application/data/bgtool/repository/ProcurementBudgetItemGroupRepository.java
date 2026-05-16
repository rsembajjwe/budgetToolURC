package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.ProcurementBudgetItemGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcurementBudgetItemGroupRepository
        extends JpaRepository<ProcurementBudgetItemGroup, Long> {

    @Query("""
    SELECT DISTINCT g
    FROM ProcurementBudgetItemGroup g
    LEFT JOIN FETCH g.items i
    LEFT JOIN FETCH g.coa
    LEFT JOIN FETCH g.deptUnit
    WHERE g.budget = :budget
      AND g.procClass = :procClass
""")
    List<ProcurementBudgetItemGroup> findByBudgetAndProcClassWithItems(
            @Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass
    );

    @Query("""
    SELECT DISTINCT g
    FROM ProcurementBudgetItemGroup g
    LEFT JOIN FETCH g.items i
    LEFT JOIN FETCH g.coa
    LEFT JOIN FETCH g.deptUnit
    WHERE g.budget = :budget
      AND g.procClass = :procClass
      AND g.coa = :coa
""")
    List<ProcurementBudgetItemGroup> findByBudgetAndProcClassAndCoaWithItems(
            @Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coa") COA coa
    );

    @Query("""
    SELECT DISTINCT g
    FROM ProcurementBudgetItemGroup g
    LEFT JOIN FETCH g.items i
    LEFT JOIN FETCH i.coacode
    LEFT JOIN FETCH i.procurementMethod
    LEFT JOIN FETCH i.procurementType
    LEFT JOIN FETCH i.fundsource
    WHERE g.id = :id
""")
    Optional<ProcurementBudgetItemGroup> findByIdWithItems(
            @Param("id") Long id
    );

    @Query("""
    SELECT DISTINCT g
    FROM ProcurementBudgetItemGroup g
    LEFT JOIN FETCH g.items i
    LEFT JOIN FETCH i.coacode
    LEFT JOIN FETCH g.coa
    LEFT JOIN FETCH g.deptUnit
    LEFT JOIN FETCH g.procurementMethod
    LEFT JOIN FETCH g.procurementType
    WHERE g.budget = :budget
""")
    List<ProcurementBudgetItemGroup> findByBudgetWithItems(@Param("budget") Budget budget);
}
