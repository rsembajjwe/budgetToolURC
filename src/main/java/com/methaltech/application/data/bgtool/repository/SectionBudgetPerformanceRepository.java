
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionBudgetPerformanceRepository extends JpaRepository<SectionBudgetPerformance, Long> {

    Optional<SectionBudgetPerformance> findByBudgetAndDeptSection(Budget budget, UrcDeptSectionAnlDimbgt deptSection);

    List<SectionBudgetPerformance> findByBudget(Budget budget);

    List<SectionBudgetPerformance> findByDeptSection(UrcDeptSectionAnlDimbgt deptSection);
}
