package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.SalaryGradeCeilingType;
import com.methaltech.application.data.bgtool.repository.SalaryGradeCeilingRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.SalaryGradeCeiling;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.salaryScale;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SalaryGradeCeilingService {

    private final SalaryGradeCeilingRepository repository;

    public SalaryGradeCeilingService(SalaryGradeCeilingRepository repository) {
        this.repository = repository;
    }

    public List<SalaryGradeCeiling> findByContext(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource) {
        if (budget == null || deptUnit == null || budgetType == null || activity == null || fundsource == null) {
            return Collections.emptyList();
        }
        return repository.findByBudgetAndDeptUnitAndBudgetTypeAndActivityAndFundsource(
                budget, deptUnit, budgetType, activity, fundsource);
    }

    public SalaryGradeCeiling saveOrUpdate(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource,
            salaryScale grade, SalaryGradeCeilingType ceilingType, BigDecimal monthlyCeiling) {
        if (budget == null || deptUnit == null || budgetType == null || activity == null || fundsource == null
                || grade == null || ceilingType == null || monthlyCeiling == null) {
            throw new IllegalArgumentException("Select salary context, grade, ceiling type and monthly ceiling.");
        }

        SalaryGradeCeiling ceiling = repository
                .findFirstByBudgetAndDeptUnitAndBudgetTypeAndActivityAndFundsourceAndGradeAndCeilingType(
                        budget, deptUnit, budgetType, activity, fundsource, grade, ceilingType)
                .orElseGet(SalaryGradeCeiling::new);
        ceiling.setBudget(budget);
        ceiling.setDeptUnit(deptUnit);
        ceiling.setBudgetType(budgetType);
        ceiling.setActivity(activity);
        ceiling.setFundsource(fundsource);
        ceiling.setGrade(grade);
        ceiling.setCeilingType(ceilingType);
        ceiling.setMonthlyCeiling(monthlyCeiling);
        return repository.save(ceiling);
    }
}
