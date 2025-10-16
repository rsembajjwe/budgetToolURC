
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.BudgetCeilingRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetCeiling;
import com.methaltech.application.data.entity.bgtool.BudgetCeilingHierarchy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetCeilingService {

    @Autowired
    private BudgetCeilingRepository ceilingRepository;

    public BudgetCeiling createCeiling(BudgetCeiling ceiling) {
        // Validate ceiling hierarchy
        validateCeilingHierarchy(ceiling);
        
        // Set default values
        if (ceiling.getAllocatedAmount() == null) {
            ceiling.setAllocatedAmount(0.0);
        }
        if (ceiling.getSpentAmount() == null) {
            ceiling.setSpentAmount(0.0);
        }
        if (ceiling.getCommittedAmount() == null) {
            ceiling.setCommittedAmount(0.0);
        }
        
        return ceilingRepository.save(ceiling);
    }

    public BudgetCeiling updateCeiling(BudgetCeiling ceiling) {
        validateCeilingHierarchy(ceiling);
        return ceilingRepository.save(ceiling);
    }

    public void deleteCeiling(Long ceilingId) {
        ceilingRepository.deleteById(ceilingId);
    }

    public Optional<BudgetCeiling> getCeilingById(Long id) {
        return ceilingRepository.findById(id);
    }

    public List<BudgetCeiling> getCeilingsByBudget(Budget budget) {
        return ceilingRepository.findByBudgetOrderByCreatedDateDesc(budget);
    }

    public List<BudgetCeiling> getCeilingsByType(Budget budget, BudgetCeiling.CeilingType type) {
        return ceilingRepository.findByBudgetAndCeilingTypeOrderByCreatedDateDesc(budget, type);
    }

    public List<BudgetCeiling> getSectionCeilings(Budget budget) {
        return getCeilingsByType(budget, BudgetCeiling.CeilingType.SECTION);
    }

    public List<BudgetCeiling> getRevenueSourceCeilings(Budget budget) {
        return getCeilingsByType(budget, BudgetCeiling.CeilingType.REVENUE_SOURCE);
    }

    public List<BudgetCeiling> getAccountCodeCeilings(Budget budget) {
        return getCeilingsByType(budget, BudgetCeiling.CeilingType.ACCOUNT_CODE);
    }

    public List<BudgetCeiling> getCeilingsByDepartment(Budget budget, String departmentCode) {
        return ceilingRepository.findByBudgetAndDepartmentCodeOrderByCreatedDateDesc(budget, departmentCode);
    }

    public List<BudgetCeiling> getCeilingsBySection(Budget budget, String sectionCode) {
        return ceilingRepository.findByBudgetAndSectionCodeOrderByCreatedDateDesc(budget, sectionCode);
    }

    public List<BudgetCeiling> getCeilingsByRevenueSource(Budget budget, String revenueSourceCode) {
        return ceilingRepository.findByBudgetAndRevenueSourceCodeOrderByCreatedDateDesc(budget, revenueSourceCode);
    }

    public BudgetCeilingHierarchy getCeilingHierarchy(Budget budget, String departmentCode) {
        List<BudgetCeiling> allCeilings = getCeilingsByDepartment(budget, departmentCode);
        
        BudgetCeilingHierarchy hierarchy = new BudgetCeilingHierarchy();
        hierarchy.setDepartmentCode(departmentCode);
        
        // Group by section
        Map<String, List<BudgetCeiling>> sectionGroups = allCeilings.stream()
            .filter(c -> c.getSectionCode() != null)
            .collect(Collectors.groupingBy(BudgetCeiling::getSectionCode));
        
        for (Map.Entry<String, List<BudgetCeiling>> sectionEntry : sectionGroups.entrySet()) {
            String sectionCode = sectionEntry.getKey();
            List<BudgetCeiling> sectionCeilings = sectionEntry.getValue();
            
            BudgetCeilingHierarchy.SectionCeiling section = new BudgetCeilingHierarchy.SectionCeiling();
            section.setSectionCode(sectionCode);
            
            // Find section-level ceiling
            Optional<BudgetCeiling> sectionCeiling = sectionCeilings.stream()
                .filter(c -> c.getCeilingType() == BudgetCeiling.CeilingType.SECTION)
                .findFirst();
            
            if (sectionCeiling.isPresent()) {
                BudgetCeiling sc = sectionCeiling.get();
                section.setSectionName(sc.getSectionName());
                section.setSectionCeiling(sc.getCeilingAmount());
                section.setSectionAllocated(sc.getAllocatedAmount());
                section.setSectionSpent(sc.getSpentAmount());
                section.setSectionCommitted(sc.getCommittedAmount());
            }
            
            // Group by revenue source
            Map<String, List<BudgetCeiling>> revenueGroups = sectionCeilings.stream()
                .filter(c -> c.getRevenueSourceCode() != null)
                .collect(Collectors.groupingBy(BudgetCeiling::getRevenueSourceCode));
            
            for (Map.Entry<String, List<BudgetCeiling>> revenueEntry : revenueGroups.entrySet()) {
                String revenueCode = revenueEntry.getKey();
                List<BudgetCeiling> revenueCeilings = revenueEntry.getValue();
                
                BudgetCeilingHierarchy.RevenueSourceCeiling revenue = new BudgetCeilingHierarchy.RevenueSourceCeiling();
                revenue.setRevenueSourceCode(revenueCode);
                
                // Find revenue source-level ceiling
                Optional<BudgetCeiling> revenueCeiling = revenueCeilings.stream()
                    .filter(c -> c.getCeilingType() == BudgetCeiling.CeilingType.REVENUE_SOURCE)
                    .findFirst();
                
                if (revenueCeiling.isPresent()) {
                    BudgetCeiling rc = revenueCeiling.get();
                    revenue.setRevenueSourceName(rc.getRevenueSourceName());
                    revenue.setRevenueSourceCeiling(rc.getCeilingAmount());
                    revenue.setRevenueSourceAllocated(rc.getAllocatedAmount());
                    revenue.setRevenueSourceSpent(rc.getSpentAmount());
                    revenue.setRevenueSourceCommitted(rc.getCommittedAmount());
                }
                
                // Add account code ceilings
                List<BudgetCeilingHierarchy.AccountCodeCeiling> accountCeilings = revenueCeilings.stream()
                    .filter(c -> c.getCeilingType() == BudgetCeiling.CeilingType.ACCOUNT_CODE)
                    .map(ac -> new BudgetCeilingHierarchy.AccountCodeCeiling(
                        ac.getAccountCode(),
                        ac.getAccountName(),
                        ac.getCeilingAmount(),
                        ac.getAllocatedAmount(),
                        ac.getSpentAmount(),
                        ac.getCommittedAmount()
                    ))
                    .collect(Collectors.toList());
                
                revenue.setAccountCodes(accountCeilings);
                section.getRevenueSources().add(revenue);
            }
            
            hierarchy.getSections().add(section);
        }
        
        return hierarchy;
    }

    public List<BudgetCeiling> getOverCeilingItems(Budget budget) {
        return ceilingRepository.findOverCeilingItems(budget);
    }

    public List<BudgetCeiling> getNearCeilingItems(Budget budget) {
        return ceilingRepository.findNearCeilingItems(budget);
    }

    public List<BudgetCeiling> getCriticalCeilingItems(Budget budget) {
        return ceilingRepository.findCriticalCeilingItems(budget);
    }

    public Page<BudgetCeiling> searchCeilings(String searchTerm, Pageable pageable) {
        return ceilingRepository.searchCeilings(searchTerm, pageable);
    }

    public BudgetCeiling approveCeiling(Long ceilingId, String approvedBy) {
        Optional<BudgetCeiling> optionalCeiling = ceilingRepository.findById(ceilingId);
        if (!optionalCeiling.isPresent()) {
            throw new RuntimeException("Budget ceiling not found");
        }
        
        BudgetCeiling ceiling = optionalCeiling.get();
        ceiling.setApprovalStatus(BudgetCeiling.ApprovalStatus.APPROVED);
        ceiling.setApprovedBy(approvedBy);
        ceiling.setApprovedDate(LocalDateTime.now());
        ceiling.setCeilingStatus(BudgetCeiling.CeilingStatus.ACTIVE);
        
        return ceilingRepository.save(ceiling);
    }

    public BudgetCeiling rejectCeiling(Long ceilingId, String rejectedBy, String reason) {
        Optional<BudgetCeiling> optionalCeiling = ceilingRepository.findById(ceilingId);
        if (!optionalCeiling.isPresent()) {
            throw new RuntimeException("Budget ceiling not found");
        }
        
        BudgetCeiling ceiling = optionalCeiling.get();
        ceiling.setApprovalStatus(BudgetCeiling.ApprovalStatus.REJECTED);
        ceiling.setLastUpdatedBy(rejectedBy);
        ceiling.setNotes(reason);
        ceiling.setCeilingStatus(BudgetCeiling.CeilingStatus.INACTIVE);
        
        return ceilingRepository.save(ceiling);
    }

    public void updateCeilingAmounts(Long ceilingId, Double allocatedAmount, Double spentAmount, Double committedAmount) {
        Optional<BudgetCeiling> optionalCeiling = ceilingRepository.findById(ceilingId);
        if (!optionalCeiling.isPresent()) {
            throw new RuntimeException("Budget ceiling not found");
        }
        
        BudgetCeiling ceiling = optionalCeiling.get();
        if (allocatedAmount != null) ceiling.setAllocatedAmount(allocatedAmount);
        if (spentAmount != null) ceiling.setSpentAmount(spentAmount);
        if (committedAmount != null) ceiling.setCommittedAmount(committedAmount);
        
        ceilingRepository.save(ceiling);
    }

    // Dashboard statistics
    public Long getActiveCeilingsCount(Budget budget) {
        return ceilingRepository.countActiveCeilings(budget);
    }

    public Long getOverCeilingCount(Budget budget) {
        return ceilingRepository.countOverCeilingItems(budget);
    }

    public Long getNearCeilingCount(Budget budget) {
        return ceilingRepository.countNearCeilingItems(budget);
    }

    public Double getTotalCeilingAmount(Budget budget) {
        Double total = ceilingRepository.sumTotalCeilingAmount(budget);
        return total != null ? total : 0.0;
    }

    public Double getTotalAllocatedAmount(Budget budget) {
        Double total = ceilingRepository.sumTotalAllocatedAmount(budget);
        return total != null ? total : 0.0;
    }

    private void validateCeilingHierarchy(BudgetCeiling ceiling) {
        // Validate that the ceiling hierarchy is consistent
        switch (ceiling.getCeilingType()) {
            case SECTION:
                if (ceiling.getSectionCode() == null || ceiling.getSectionCode().isEmpty()) {
                    throw new IllegalArgumentException("Section code is required for section-level ceiling");
                }
                break;
            case REVENUE_SOURCE:
                if (ceiling.getSectionCode() == null || ceiling.getRevenueSourceCode() == null) {
                    throw new IllegalArgumentException("Section and revenue source codes are required for revenue source-level ceiling");
                }
                break;
            case ACCOUNT_CODE:
                if (ceiling.getSectionCode() == null || ceiling.getRevenueSourceCode() == null || ceiling.getAccountCode() == null) {
                    throw new IllegalArgumentException("Section, revenue source, and account codes are required for account code-level ceiling");
                }
                break;
        }
    }
}
