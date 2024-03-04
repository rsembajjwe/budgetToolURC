package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.bgtool.repository.ProcurementPlanRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<ProcurementPlan> findByBudgetAndProcClass2(Budget budget, ProcClass procClass, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        if (budget == null || procClass == null || deptUnits == null) {
            // Handle the case when the budget is null (e.g., throw an exception or return an empty list)
            return Collections.emptyList();  // Return an empty list as an example
        }

        // Assuming you have a 'Budget' entity associated with 'ProcurementPlan'
        return procurementPlanRepository.findByBudgetAndProcClassAndProcPlanBudgetItemsDeptUnitIn(budget, procClass, deptUnits);
    }

    public List<ProcurementPlan> findByBudgetAndProcClassAndDeptUnits(Budget budget, ProcClass procClass, Collection<UrcDeptSectionAnlDimbgt> deptUnits) {
        if (budget == null || procClass == null || deptUnits == null) {
            // Handle the case when the budget is null (e.g., throw an exception or return an empty list)
            return Collections.emptyList();  // Return an empty list as an example
        }        
        List<ProcurementPlan> procurementPlans = procurementPlanRepository.findByBudgetAndProcClassAndProcPlanBudgetItemsDeptUnitIn(budget, procClass, deptUnits);

        // Filter and sort procPlanBudgetItems set for each ProcurementPlan entity
        for (ProcurementPlan procurementPlan : procurementPlans) {
            Set<BudgetItems> filteredBudgetItems = procurementPlan.getProcPlanBudgetItems().stream()
                    .filter(budgetItem -> deptUnits.contains(budgetItem.getDeptUnit()))
                    .collect(Collectors.toSet());
            procurementPlan.setProcPlanBudgetItems(filteredBudgetItems);
            procurementPlan.setCost(generatesumofMonthsFromList(filteredBudgetItems.stream().toList()));
            
        }
        
        return procurementPlans;
    }    
    
    @Transactional
    public void deleteByBudget_COA(Budget budget, COA coa) {
        procurementPlanRepository.deleteByBudgetAndCoa(budget, coa);
    }

    private BigDecimal generatesumofMonthsFromList(List<BudgetItems> budget) {
        BigDecimal sumofMonths = BigDecimal.ZERO;
        for (BudgetItems lis : budget) {
            
            sumofMonths = sumofMonths.add(generatesumofMonths(lis));
        }
        
        return sumofMonths;
    }

    private BigDecimal generatesumofMonths(BudgetItems budget) {
        BigDecimal sumofMonths = BigDecimal.ZERO;
        
        if (budget.getJan() != null) {
            sumofMonths = sumofMonths.add(budget.getJan());
        }
        if (budget.getFeb() != null) {
            sumofMonths = sumofMonths.add(budget.getFeb());
        }
        if (budget.getMar() != null) {
            sumofMonths = sumofMonths.add(budget.getMar());
        }
        if (budget.getApr() != null) {
            sumofMonths = sumofMonths.add(budget.getApr());
        }
        if (budget.getMay() != null) {
            sumofMonths = sumofMonths.add(budget.getMay());
        }
        if (budget.getJun() != null) {
            sumofMonths = sumofMonths.add(budget.getJun());
        }
        if (budget.getJul() != null) {
            sumofMonths = sumofMonths.add(budget.getJul());
        }
        if (budget.getAug() != null) {
            sumofMonths = sumofMonths.add(budget.getAug());
        }
        if (budget.getSep() != null) {
            sumofMonths = sumofMonths.add(budget.getSep());
        }
        if (budget.getOct() != null) {
            sumofMonths = sumofMonths.add(budget.getOct());
        }
        if (budget.getNov() != null) {
            sumofMonths = sumofMonths.add(budget.getNov());
        }
        if (budget.getDec() != null) {
            sumofMonths = sumofMonths.add(budget.getDec());
        }
        return sumofMonths;
    }    
}
