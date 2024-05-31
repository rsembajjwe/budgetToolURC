package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApproval;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BudgetApprovalRepository extends JpaRepository<BudgetApproval, Integer> {

    @Query("SELECT ba FROM BudgetApproval ba WHERE ba.budget = :budget AND ba.section IN :sections")
    List<BudgetApproval> findByBudgetAndSections(@Param("budget") Budget budget, @Param("sections") Set<UrcDeptSectionAnlDimbgt> sections);

    @Query("SELECT ba FROM BudgetApproval ba WHERE ba.budget = :budget AND ba.section IN :sections")
    Page<BudgetApproval> findByBudgetAndSections(@Param("budget") Budget budget, @Param("sections") Set<UrcDeptSectionAnlDimbgt> sections, Pageable pageable);

    @Query("SELECT ba FROM BudgetApproval ba WHERE ba.budget = :budget")
    List<BudgetApproval> findByBudget(@Param("budget") Budget budget);

    // Method to find the top BudgetApproval by budget and section
    @Query("SELECT b FROM BudgetApproval b WHERE b.budget = :budget AND b.section = :section ORDER BY b.bloSubmissionDate DESC")
    BudgetApproval findTopByBudgetAndSectionOrderByBloSubmissionDateDesc(
            @Param("budget") Budget budget,
            @Param("section") UrcDeptSectionAnlDimbgt section
    );

}
