package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.SalaryAdjustmentScope;
import com.methaltech.application.data.SalaryAdjustmentType;
import com.methaltech.application.data.bgtool.repository.SalaryAdjustmentHistoryRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.SalaryAdjustmentHistory;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.salaryScale;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SalaryAdjustmentHistoryService {

    private final SalaryAdjustmentHistoryRepository repository;

    public SalaryAdjustmentHistoryService(SalaryAdjustmentHistoryRepository repository) {
        this.repository = repository;
    }

    public List<SalaryAdjustmentHistory> findByContext(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource) {
        if (budget == null || deptUnit == null || budgetType == null || activity == null || fundsource == null) {
            return Collections.emptyList();
        }
        return repository.findByBudgetAndDeptUnitAndBudgetTypeAndActivityAndFundsourceOrderByAppliedAtDesc(
                budget, deptUnit, budgetType, activity, fundsource);
    }

    public SalaryAdjustmentHistory record(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource,
            SalaryAdjustmentScope scope, SalaryAdjustmentType adjustmentType, salaryScale grade,
            Long selectedStaffSalaryId, String selectedStaffCode, String selectedStaffName,
            BigDecimal adjustmentValue, int affectedStaffCount, BigDecimal oldMonthlyTotal,
            BigDecimal newMonthlyTotal, BigDecimal monthlyDifference, BigDecimal annualDifference,
            String appliedBy) {
        SalaryAdjustmentHistory history = new SalaryAdjustmentHistory();
        history.setBudget(budget);
        history.setDeptUnit(deptUnit);
        history.setBudgetType(budgetType);
        history.setActivity(activity);
        history.setFundsource(fundsource);
        history.setScope(scope);
        history.setAdjustmentType(adjustmentType);
        history.setGrade(grade);
        history.setSelectedStaffSalaryId(selectedStaffSalaryId);
        history.setSelectedStaffCode(selectedStaffCode);
        history.setSelectedStaffName(selectedStaffName);
        history.setAdjustmentValue(adjustmentValue);
        history.setAffectedStaffCount(affectedStaffCount);
        history.setOldMonthlyTotal(oldMonthlyTotal);
        history.setNewMonthlyTotal(newMonthlyTotal);
        history.setMonthlyDifference(monthlyDifference);
        history.setAnnualDifference(annualDifference);
        history.setAppliedBy(appliedBy);
        return repository.save(history);
    }
}
