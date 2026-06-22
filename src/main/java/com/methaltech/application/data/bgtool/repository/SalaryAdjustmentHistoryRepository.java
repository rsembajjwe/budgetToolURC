package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.SalaryAdjustmentHistory;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryAdjustmentHistoryRepository extends JpaRepository<SalaryAdjustmentHistory, Long> {

    List<SalaryAdjustmentHistory> findByBudgetAndDeptUnitAndBudgetTypeAndActivityAndFundsourceOrderByAppliedAtDesc(
            Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType,
            Urc_Activities activity, Fundsource fundsource);
}
