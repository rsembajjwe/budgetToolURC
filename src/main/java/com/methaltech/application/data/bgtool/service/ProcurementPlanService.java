package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.bgtool.repository.ProcurementPlanRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import jakarta.transaction.Transactional;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProcurementPlanService {

    private final ProcurementPlanRepository procurementPlanRepository;

    @Autowired
    public ProcurementPlanService(ProcurementPlanRepository procurementPlanRepository) {
        this.procurementPlanRepository = procurementPlanRepository;
    }

    // Save a new procurement plan
    public ProcurementPlan save(ProcurementPlan procurementPlan) {
        return procurementPlanRepository.save(procurementPlan);
    }

    // Get all procurement plans
    public List<ProcurementPlan> getAllProcurementPlans() {
        return procurementPlanRepository.findAll();
    }

    // Get a procurement plan by ID
    public Optional<ProcurementPlan> getProcurementPlanById(Integer id) {
        return procurementPlanRepository.findById(id);
    }

    // Delete a procurement plan by ID
    public void deleteProcurementPlanById(Integer id) {
        procurementPlanRepository.deleteById(id);
    }

    // Delete a procurement plan by ID
    public void deleteProcurementPlanByBudget(Budget budget) {
        procurementPlanRepository.deleteByBudget(budget);
    }

    // Delete a procurement plan by object
    public void deleteProcurementPlan(ProcurementPlan procurementPlan) {
        procurementPlanRepository.delete(procurementPlan);
    }

// Find procurement plans by budget
    public List<ProcurementPlan> findProcurementPlansByBudget(Budget budget) {
        if (budget == null) {
            // Handle the case when the budget is null (e.g., throw an exception or return an empty list)
            return Collections.emptyList();  // Return an empty list as an example
        }

        // Assuming you have a 'Budget' entity associated with 'ProcurementPlan'
        return procurementPlanRepository.findByBudget(budget);
    }

    public List<ProcurementPlan> findByBudgetAndProcClass(Budget budget, ProcClass procClass) {
        if (budget == null || procClass == null) {
            // Handle the case when the budget is null (e.g., throw an exception or return an empty list)
            return Collections.emptyList();  // Return an empty list as an example
        }

        // Assuming you have a 'Budget' entity associated with 'ProcurementPlan'
        return procurementPlanRepository.findByBudgetAndProcClass(budget, procClass);
    }

    @Transactional
    public void deleteByBudget_COA(Budget budget, COA coa) {
        procurementPlanRepository.deleteByBudgetAndCoa(budget, coa);
    }
}
