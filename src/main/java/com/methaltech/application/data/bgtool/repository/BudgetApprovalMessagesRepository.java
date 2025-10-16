
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApprovalMessages;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetApprovalMessagesRepository extends JpaRepository<BudgetApprovalMessages, Integer> {
    @Query("SELECT bam FROM BudgetApprovalMessages bam " +
           "JOIN bam.budgetApproval ba " +
           "WHERE ba.budget = :budget " +
           "AND ba.section IN :sections")
    List<BudgetApprovalMessages> findByBudgetAndSections(@Param("budget") Budget budget, @Param("sections") Set<UrcDeptSectionAnlDimbgt> sections);
}
