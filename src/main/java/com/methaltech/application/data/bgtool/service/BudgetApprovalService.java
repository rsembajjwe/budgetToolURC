package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.BudgetApprovalRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApproval;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class BudgetApprovalService {

    @Autowired
    private BudgetApprovalRepository budgetApprovalRepository;

    public List<BudgetApproval> getBudgetApprovalsByBudgetAndSections(Budget budget, Set<UrcDeptSectionAnlDimbgt> sections) {
        return budgetApprovalRepository.findByBudgetAndSections(budget, sections);
    }

    public BudgetApproval saveBudgetApproval(BudgetApproval budgetApproval) {
        return budgetApprovalRepository.save(budgetApproval);
    }

    public List<BudgetApproval> getBudgetApprovalsByBudget(Budget budget) {
        return budgetApprovalRepository.findByBudget(budget);
    }
    
    public BudgetApproval findTopByBudgetAndSectionOrderByBloSubmissionDateDesc(Budget budget,UrcDeptSectionAnlDimbgt section) {
        return budgetApprovalRepository.findTopByBudgetAndSectionOrderByBloSubmissionDateDesc(budget,section);
    }    
}
