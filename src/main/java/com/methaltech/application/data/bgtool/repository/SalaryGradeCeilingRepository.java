package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.SalaryGradeCeilingType;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.SalaryGradeCeiling;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.salaryScale;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryGradeCeilingRepository extends JpaRepository<SalaryGradeCeiling, Long> {

    List<SalaryGradeCeiling> findByBudgetAndDeptUnitAndBudgetTypeAndActivityAndFundsource(
            Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType,
            Urc_Activities activity, Fundsource fundsource);

    Optional<SalaryGradeCeiling> findFirstByBudgetAndDeptUnitAndBudgetTypeAndActivityAndFundsourceAndGradeAndCeilingType(
            Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType,
            Urc_Activities activity, Fundsource fundsource, salaryScale grade,
            SalaryGradeCeilingType ceilingType);
}
