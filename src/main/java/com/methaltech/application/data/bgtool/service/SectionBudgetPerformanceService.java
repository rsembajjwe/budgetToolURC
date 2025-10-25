
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.SectionBudgetPerformanceRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SectionBudgetPerformanceService {

    private final SectionBudgetPerformanceRepository repository;

    public SectionBudgetPerformanceService(SectionBudgetPerformanceRepository repository) {
        this.repository = repository;
    }

    public SectionBudgetPerformance save(SectionBudgetPerformance performance) {
        return repository.save(performance);
    }

    public Optional<SectionBudgetPerformance> findByBudgetAndDeptSection(Budget budget, UrcDeptSectionAnlDimbgt deptSection) {
        return repository.findByBudgetAndDeptSection(budget, deptSection);
    }

    public List<SectionBudgetPerformance> findByBudget(Budget budget) {
        return repository.findByBudget(budget);
    }

    public List<SectionBudgetPerformance> findByDeptSection(UrcDeptSectionAnlDimbgt deptSection) {
        return repository.findByDeptSection(deptSection);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
