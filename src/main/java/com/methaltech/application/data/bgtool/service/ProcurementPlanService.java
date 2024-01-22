
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.ProcurementPlanRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.ProcurementMethod;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
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

    // Delete a procurement plan by object
    public void deleteProcurementPlan(ProcurementPlan procurementPlan) {
        procurementPlanRepository.delete(procurementPlan);
    }

    // Find procurement plans by budget
    public List<ProcurementPlan> findProcurementPlansByBudget(Budget budget) {
        // Assuming you have a 'Budget' entity associated with 'ProcurementPlan'
        return procurementPlanRepository.findByBudget(budget);
    }

    /*   // Find procurement plans by procurement method
    public List<ProcurementPlan> findProcurementPlansByProcurementMethod(ProcurementMethod procurementMethod) {
    return procurementPlanRepository.findByProcurementmethod(procurementMethod);
    }
    
    // Find procurement plans by procurement type
    public List<ProcurementPlan> findProcurementPlansByProcurementType(ProcurementType procurementType) {
    return procurementPlanRepository.findByProcurementtype(procurementType);
    }*/

    // Add more methods as needed based on your requirements
}

