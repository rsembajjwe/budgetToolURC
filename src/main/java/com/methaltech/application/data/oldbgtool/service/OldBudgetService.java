package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.OldBudget;
import com.methaltech.application.data.oldbgtool.repository.OldBudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OldBudgetService {

    private final OldBudgetRepository budgetRepository;

    @Autowired
    public OldBudgetService(OldBudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<OldBudget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Optional<OldBudget> getBudgetById(int budgetId) {
        return budgetRepository.findById(budgetId);
    }

    public OldBudget createBudget(OldBudget budget) {
        return budgetRepository.save(budget);
    }

    public OldBudget updateBudget(OldBudget budget) {
        return budgetRepository.save(budget);
    }

    public void deleteBudgetById(int budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    public OldBudget getBudgetByFY(String fy) {
        return budgetRepository.findByFinancialYear(fy);
    }
    public Optional<OldBudget> getBudgetByFinancialYear(String financialYear) {
        return budgetRepository.findFirstByFinancialYear(financialYear);
    }    
}
