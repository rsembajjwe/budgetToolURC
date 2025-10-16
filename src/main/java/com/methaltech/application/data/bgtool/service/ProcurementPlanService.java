package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.ProcClass;
import static com.methaltech.application.data.ProcClass.Consultancy;
import static com.methaltech.application.data.ProcClass.Non_Consultancy;
import static com.methaltech.application.data.ProcClass.Supplies;
import static com.methaltech.application.data.ProcClass.Works;
import com.methaltech.application.data.bgtool.repository.BudgetItemsRepository;
import com.methaltech.application.data.bgtool.repository.FundsourceRepository;
import com.methaltech.application.data.bgtool.repository.ProcurementMethodRepository;
import com.methaltech.application.data.bgtool.repository.ProcurementPlanRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.ProcurementMethod;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ProcurementPlanService {

    private final ProcurementPlanRepository procurementPlanRepository;
    private final ProcurementMethodRepository procurementMethodRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;
    private final FundsourceRepository fundsourceRepository;
    private final BudgetItemsRepository repository;

    @Autowired
    public ProcurementPlanService(ProcurementPlanRepository procurementPlanRepository, PlatformTransactionManager transactionManager,
            ProcurementMethodRepository procurementMethodRepository, FundsourceRepository fundsourceRepository,
            BudgetItemsRepository repository) {
        this.procurementPlanRepository = procurementPlanRepository;
        this.transactionManager = transactionManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.procurementMethodRepository = procurementMethodRepository;
        this.transactionTemplate.setReadOnly(true);
        this.fundsourceRepository = fundsourceRepository;
        this.repository = repository;
    }

    /*    @Transactional
    public ProcurementPlan save(ProcurementPlan procurementPlan) {
    return procurementPlanRepository.save(procurementPlan);
    }*/
    @Transactional
    public ProcurementPlan save(ProcurementPlan procurementPlan) {
        // Make sure associated Fundsource entities are managed
        /*        if (procurementPlan.getFundsource() != null) {
        Set<Fundsource> managedFundsources = procurementPlan.getFundsource().stream()
        .peek(fundsource -> {
        if (fundsource.getId() == null) {
        throw new IllegalArgumentException("Fundsource entity must have an ID");
        }
        })
        .map(fundsource -> fundsourceRepository.findById(fundsource.getId())
        .orElseThrow(() -> new EntityNotFoundException("Fundsource not found with id: " + fundsource.getId())))
        .collect(Collectors.toSet());
        procurementPlan.setFundsource(managedFundsources);
        }*/

        // Make sure associated ProcPlanBudgetItem entities are managed
        /*        if (procurementPlan.getProcPlanBudgetItems() != null) {
        Set<BudgetItems> managedProcPlanBudgetItems = procurementPlan.getProcPlanBudgetItems().stream()
        .peek(procPlanBudgetItem -> {
        if (procPlanBudgetItem.getId() == null) {
        throw new IllegalArgumentException("ProcPlanBudgetItem entity must have an ID");
        }
        })
        .map(procPlanBudgetItem -> repository.findById(procPlanBudgetItem.getId())
        .orElseThrow(() -> new EntityNotFoundException("ProcPlanBudgetItem not found with id: " + procPlanBudgetItem.getId())))
        .collect(Collectors.toSet());
        procurementPlan.setProcPlanBudgetItems(managedProcPlanBudgetItems);
        }*/
        // Save the ProcurementPlan entity
        return procurementPlanRepository.save(procurementPlan);
    }

    @Transactional
    public List<ProcurementPlan> saveAll(List<ProcurementPlan> procurementPlans) {
        // Batch insert using saveAll method
        System.out.println("Saved Procurement Plan with ID: ");
        return procurementPlanRepository.saveAll(procurementPlans);
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

    @Transactional
    public void deleteProcurementPlanByBudget(Budget budget) {
        procurementPlanRepository.deleteByBudget(budget);
    }

    @Transactional
    public void deleteProcurementPlan(ProcurementPlan procurementPlan) {
        procurementPlanRepository.delete(procurementPlan);
    }

    @Transactional
    public List<ProcurementPlan> findProcurementPlansByBudget(Budget budget) {
        if (budget == null) {
            // Handle the case when the budget is null (e.g., throw an exception or return an empty list)
            return Collections.emptyList();  // Return an empty list as an example
        }

        // Assuming you have a 'Budget' entity associated with 'ProcurementPlan'
        return procurementPlanRepository.findByBudget(budget);
    }

    @Transactional
    public List<ProcurementPlan> findByBudgetAndProcClass(Budget budget, ProcClass procClass) {
        if (budget == null || procClass == null) {
            // Handle the case when the budget is null (e.g., throw an exception or return an empty list)
            return Collections.emptyList();  // Return an empty list as an example
        }
        return procurementPlanRepository.findByBudgetAndProcClass(budget, procClass);
    }

    @Transactional
    public List<ProcurementPlan> findByBudgetAndProcClassAndDeptUnits(Budget budget, ProcClass procClass, Collection<UrcDeptSectionAnlDimbgt> deptUnits) {
        if (budget == null || procClass == null || deptUnits == null) {

            return Collections.emptyList();
        }
        //List<ProcurementPlan> procurementPlans = procurementPlanRepository.findByBudgetAndProcClassAndProcPlanBudgetItemsDeptUnitIn(budget, procClass, deptUnits);
        List<ProcurementPlan> procurementPlans = procurementPlanRepository.findByBudgetAndProcClass(budget, procClass);

        List<ProcurementPlan> procurementPlanss = new ArrayList<>();
        for (ProcurementPlan procurementPlan : procurementPlans) {
            List<BudgetItems> items = repository.findByBudgetAndProcClassAndCoaAndDeptUnitIn(procurementPlan.getBudget(), procurementPlan.getProcClass(), procurementPlan.getCoa(), (Set<UrcDeptSectionAnlDimbgt>) deptUnits);
            //procurementPlan.setProcPlanBudgetItems(new HashSet<>(items));
            procurementPlan.setCost(generatesumofMonthsFromList(items));

            procurementPlanss.add(procurementPlan);

        }
        List<ProcurementPlan> filteredProcurementPlans = procurementPlanss.stream()
                .filter(plan -> plan.getCost() != null && plan.getCost().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
        return filteredProcurementPlans;
    }

    @Transactional
    public List<ProcurementPlan> findByBudgetAndProcClassAndFundsource(Budget budget, ProcClass procClass, List<ProcurementPlan> procurementPlans, Set<Fundsource> fundsource) {
        /*        if (budget == null || procClass == null) {
        
        return Collections.emptyList();
        }*/

        List<ProcurementPlan> procurementPlanss = new ArrayList<>();
        for (ProcurementPlan procurementPlan : procurementPlans) {
            List<BudgetItems> items = repository.findByBudgetAndProcClassAndCoaAndFundsourceIn(procurementPlan.getBudget(), procurementPlan.getProcClass(), procurementPlan.getCoa(), fundsource);
            //procurementPlan.setProcPlanBudgetItems(new HashSet<>(items));
            procurementPlan.setCost(generatesumofMonthsFromList(items));

            procurementPlanss.add(procurementPlan);

        }
        List<ProcurementPlan> filteredProcurementPlans = procurementPlanss.stream()
                .filter(plan -> plan.getCost() != null && plan.getCost().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
        System.out.println("Used this");
        return filteredProcurementPlans;
    }

    @Transactional
    public List<ProcurementPlan> findByBudgetAndProcClassAndSectsAndFundsource(Budget budget, ProcClass procClass, List<ProcurementPlan> procurementPlans, Set<Fundsource> fundsource, Set<UrcDeptSectionAnlDimbgt> deptUnits) {

        List<ProcurementPlan> procurementPlanss = new ArrayList<>();
        for (ProcurementPlan procurementPlan : procurementPlans) {
            List<BudgetItems> items = repository.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(procurementPlan.getBudget(), procurementPlan.getProcClass(), procurementPlan.getCoa(), deptUnits, fundsource);
            //procurementPlan.setProcPlanBudgetItems(new HashSet<>(items));
            procurementPlan.setCost(generatesumofMonthsFromList(items));

            procurementPlanss.add(procurementPlan);

        }
        List<ProcurementPlan> filteredProcurementPlans = procurementPlanss.stream()
                .filter(plan -> plan.getCost() != null && plan.getCost().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());
        System.out.println("Used this");
        return filteredProcurementPlans;
    }

    @Transactional
    public BigDecimal findByBudgetAndProcClassAndSectionIn(Budget budget, ProcClass procClass, List<ProcurementPlan> procurementPlans, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        /*        if (budget == null || procClass == null) {
        
        return Collections.emptyList();
        }*/
        BigDecimal TOTADecimal = new BigDecimal(BigInteger.ZERO);
        for (ProcurementPlan procurementPlan : procurementPlans) {
            List<BudgetItems> items = repository.findByBudgetAndProcClassAndCoaAndDeptUnitIn(procurementPlan.getBudget(), procurementPlan.getProcClass(), procurementPlan.getCoa(), deptUnits);

            TOTADecimal = TOTADecimal.add(generatesumofMonthsFromList(items));
        }

        return TOTADecimal;
    }

    @Transactional
    public BigDecimal findByBudgetAndProcClassAndSectionIn(Budget budget, ProcClass procClass, ProcurementPlan procurementPlan, Set<UrcDeptSectionAnlDimbgt> deptUnits) {

        BigDecimal TOTADecimal = new BigDecimal(BigInteger.ZERO);

        List<BudgetItems> items = repository.findByBudgetAndProcClassAndCoaAndDeptUnitIn(procurementPlan.getBudget(), procurementPlan.getProcClass(), procurementPlan.getCoa(), deptUnits);

        TOTADecimal = TOTADecimal.add(generatesumofMonthsFromList(items));

        return TOTADecimal;
    }

    @Transactional
    public BigDecimal findByBudgetAndProcClassAndFundsIn(Budget budget, ProcClass procClass, ProcurementPlan procurementPlan, Set<Fundsource> fundsource) {

        BigDecimal TOTADecimal = new BigDecimal(BigInteger.ZERO);
        List<BudgetItems> items = repository.findByBudgetAndProcClassAndCoaAndFundsourceIn(procurementPlan.getBudget(), procurementPlan.getProcClass(), procurementPlan.getCoa(), fundsource);

        TOTADecimal = TOTADecimal.add(generatesumofMonthsFromList(items));

        return TOTADecimal;
    }

    @Transactional
    public BigDecimal findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(Budget budget, ProcClass procClass, ProcurementPlan procurementPlan, Set<UrcDeptSectionAnlDimbgt> deptUnits, Set<Fundsource> fundsource) {

        BigDecimal TOTADecimal = new BigDecimal(BigInteger.ZERO);
        List<BudgetItems> items = repository.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(procurementPlan.getBudget(), procurementPlan.getProcClass(), procurementPlan.getCoa(), deptUnits, fundsource);

        TOTADecimal = TOTADecimal.add(generatesumofMonthsFromList(items));

        return TOTADecimal;
    }

    @Transactional
    public List<ProcurementPlan> findByBudgetAndProcClassAndCoa(Budget budget, ProcClass procClass, COA coa) {
        if (budget == null || procClass == null || coa == null) {
            // Handle the case when the budget is null (e.g., throw an exception or return an empty list)
            return Collections.emptyList(); // Return an empty list as an example
        }
        return procurementPlanRepository.findByBudgetAndProcClassAndCoa(budget, procClass, coa);
    }

    @Transactional
    public ProcurementPlan findFirstByBudgetAndProcClassAndCoa(Budget budget, ProcClass procClass, COA coa) {
        if (budget == null || procClass == null || coa == null) {
            // Handle the case when any parameter is null (e.g., throw an exception or return null)
            return null; // Return null as an example
        }
        return procurementPlanRepository.findFirstByBudgetAndProcClassAndCoa(budget, procClass, coa);
    }

    @Transactional
    public ProcurementPlan updateProcPlanBudgetItems(Integer procurementPlanId, Set<BudgetItems> newBudgetItems, BigDecimal cost) {
        // Retrieve the ProcurementPlan entity from the database
        ProcurementPlan procurementPlan = procurementPlanRepository.findById(procurementPlanId)
                .orElseThrow(() -> new EntityNotFoundException("ProcurementPlan not found with id: " + procurementPlanId));

        // Update the procPlanBudgetItems association
        //procurementPlan.setProcPlanBudgetItems(newBudgetItems);
        procurementPlan.setCost(cost);

        // Save the modified entity back to the database
        return procurementPlanRepository.save(procurementPlan);
    }

    @Transactional
    public void deleteProcurementPlan(Integer procurementPlanId) {
        procurementPlanRepository.deleteById(procurementPlanId);
        System.out.println("2 Deleted Procurement Plan with ID: Done");
    }

    @Transactional
    public ProcurementPlan saveOrUpdateProcurementPlan(ProcurementPlan procurementPlan) {
        return procurementPlanRepository.save(procurementPlan);
    }

    @Transactional
    public void updateProcurementPlans(List<ProcurementPlan> procurementPlans) {
        procurementPlanRepository.saveAll(procurementPlans);
    }

    @Transactional
    public void deleteProcurementPlans(List<Integer> procurementPlanIds) {
        procurementPlanIds.forEach(id -> {
            System.out.println("1 Start Deleting Procurement Plan with ID: " + id + "  " + procurementPlanIds.size());
            deleteProcurementPlan(id);
        });
        System.out.println("3 Deleting Procurement Plan with ID: Done");
    }

    @Transactional
    public void updateProcPlanBudgetItems(List<ProcurementPlan> procurementPlans) {
        List<ProcurementPlan> updatedPlans = new ArrayList<>();

        for (ProcurementPlan procurementPlan : procurementPlans) {
            Integer procurementPlanId = procurementPlan.getId();
            // Set<BudgetItems> newBudgetItems = procurementPlan.getProcPlanBudgetItems();
            BigDecimal cost = procurementPlan.getCost();

            // Retrieve the ProcurementPlan entity from the database
            ProcurementPlan existingProcurementPlan = procurementPlanRepository.findById(procurementPlanId)
                    .orElseThrow(() -> new EntityNotFoundException("ProcurementPlan not found with id: " + procurementPlanId));

            // Update the procPlanBudgetItems association
            //existingProcurementPlan.setProcPlanBudgetItems(newBudgetItems);
            existingProcurementPlan.setCost(cost);

            updatedPlans.add(existingProcurementPlan);
            System.out.println("Updating Procurement Plan with ID: " + existingProcurementPlan.getId());
        }

        // Save all modified entities back to the database
        procurementPlanRepository.saveAll(updatedPlans);
    }

    @Transactional
    public void deleteByBudget_COA(Budget budget, COA coa) {
        procurementPlanRepository.deleteByBudgetAndCoa(budget, coa);
    }

    @Transactional
    public void deleteProcurementPlansByBudget(Budget budget) {
        List<ProcurementPlan> plans = procurementPlanRepository.findByBudget(budget);

        for (ProcurementPlan plan : plans) {
            plan.getProcPlanBudgetItems().clear(); // clears the join table entries
            procurementPlanRepository.save(plan); // persist the change
        }

        procurementPlanRepository.deleteByBudget(budget);
    }

    @Transactional
    public void deleteAll(List<ProcurementPlan> procurementPlans) {
        // Batch delete using deleteAll method
        procurementPlanRepository.deleteAll(procurementPlans);
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

    public ProcurementPlan findById(Integer procurementPlanId) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setReadOnly(true);

        return transactionTemplate.execute(status -> {
            return procurementPlanRepository.findById(procurementPlanId)
                    .orElseThrow(() -> new EntityNotFoundException("ProcurementPlan not found with id: " + procurementPlanId));
        });
    }

    public List<ProcurementPlan> findByIds(List<Integer> procurementPlanIds) {
        return transactionTemplate.execute(new TransactionCallback<List<ProcurementPlan>>() {
            @Override
            public List<ProcurementPlan> doInTransaction(TransactionStatus status) {
                return procurementPlanRepository.findAllById(procurementPlanIds);
            }
        });
    }

    public ProcurementMethod getProcurementMethodList2(ProcClass p, BigDecimal threshold) {
        ProcurementMethod m = null;

        switch (p) {
            case Supplies:
            case Non_Consultancy:
                if (threshold.doubleValue() > 500000000) {
                    //Open_Bidding
                    m = procurementMethodRepository.findByNum(2);
                } else if (threshold.doubleValue() >= 200000000 && threshold.doubleValue() <= 500000000) {
                    //Restricted_Bidding
                    m = procurementMethodRepository.findByNum(4);
                } else if (threshold.doubleValue() >= 10000000 && threshold.doubleValue() < 200000000) {
                    //Request_for_Quotations
                    m = procurementMethodRepository.findByNum(3);
                } else if (threshold.doubleValue() < 10000000) {
                    //Micro_Procurement
                    m = procurementMethodRepository.findByNum(1);
                }
                break;
            case Works:
                if (threshold.doubleValue() > 800000000) {
                    //Open_Bidding
                    m = procurementMethodRepository.findByNum(2);
                } else if (threshold.doubleValue() >= 400000000 && threshold.doubleValue() <= 800000000) {
                    //Restricted_Bidding
                    m = procurementMethodRepository.findByNum(4);
                } else if (threshold.doubleValue() >= 10000000 && threshold.doubleValue() < 400000000) {
                    //Request_for_Quotations
                    m = procurementMethodRepository.findByNum(3);
                } else if (threshold.doubleValue() < 10000000) {
                    //Micro_Procurement
                    m = procurementMethodRepository.findByNum(1);
                }
                break;
            case Consultancy:
                if (threshold.doubleValue() > 500000000) {
                    //Open_Bidding
                    m = procurementMethodRepository.findByNum(2);
                } else if (threshold.doubleValue() >= 200000000 && threshold.doubleValue() <= 500000000) {
                    //Restricted_Bidding
                    m = procurementMethodRepository.findByNum(4);
                } else if (threshold.doubleValue() >= 10000000 && threshold.doubleValue() < 200000000) {
                    //Request_for_Quotations
                    m = procurementMethodRepository.findByNum(3);
                } else if (threshold.doubleValue() < 10000000) {
                    //Micro_Procurement
                    m = procurementMethodRepository.findByNum(1);
                }
                break;
            default:
                break;
        }

        return m;
    }

    public ProcurementPlan findFirstByProcPlanBudgetItems(BudgetItems budgetItem) {

        return procurementPlanRepository.findFirstByProcPlanBudgetItems(budgetItem);

    }
}
