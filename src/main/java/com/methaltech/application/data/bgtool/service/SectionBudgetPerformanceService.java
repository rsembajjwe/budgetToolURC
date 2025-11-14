package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.SectionBudgetPerformanceRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /*
    public Optional<SectionBudgetPerformance> findByBudgetAndDeptSection(Budget budget, UrcDeptSectionAnlDimbgt deptSection) {
    return repository.findByBudgetAndDeptSection(budget, deptSection);
    }*/
    public Optional<SectionBudgetPerformance> findByBudgetAndDeptSection(Budget budget, UrcDeptSectionAnlDimbgt deptSection) {
        List<SectionBudgetPerformance> performances = repository.findByBudgetAndDeptSection(budget, deptSection);

        if (performances.size() == 1) {
            return Optional.of(performances.get(0));
        } else {
            return Optional.empty(); // 0 or more than 1
        }
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

    /**
     * Finds all SectionBudgetPerformance records matching Budget and submitQtr1
     * flag.
     */
    public List<SectionBudgetPerformance> findAllByBudgetAndSubmitQtr1(Budget budget, Boolean submitQtr1) {
        return repository.findAllByBudgetAndSubmitQtr1(budget, submitQtr1);
    }

    public List<SectionBudgetPerformance> findAllByBudgetAndSubmitQtr2(Budget budget, Boolean submitQtr2) {
        return repository.findAllByBudgetAndSubmitQtr2(budget, submitQtr2);
    }

    public List<SectionBudgetPerformance> findAllByBudgetAndSubmitQtr3(Budget budget, Boolean submitQtr3) {
        return repository.findAllByBudgetAndSubmitQtr3(budget, submitQtr3);
    }

    public List<SectionBudgetPerformance> findAllByBudgetAndSubmitQtr4(Budget budget, Boolean submitQtr4) {
        return repository.findAllByBudgetAndSubmitQtr4(budget, submitQtr4);
    }

    public List<UrcDeptSectionAnlDimbgt> getSubmittedDeptSectionsQtr1(Budget chosenBudget) {
        return repository.findAllByBudgetAndSubmitQtr1(chosenBudget, true)
                .stream().map(SectionBudgetPerformance::getDeptSection).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    public List<UrcDeptSectionAnlDimbgt> getSubmittedDeptSectionsQtr2(Budget chosenBudget) {
        return repository.findAllByBudgetAndSubmitQtr2(chosenBudget, true)
                .stream().map(SectionBudgetPerformance::getDeptSection).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    public List<UrcDeptSectionAnlDimbgt> getSubmittedDeptSectionsQtr3(Budget chosenBudget) {
        return repository.findAllByBudgetAndSubmitQtr3(chosenBudget, true)
                .stream().map(SectionBudgetPerformance::getDeptSection).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    public List<UrcDeptSectionAnlDimbgt> getSubmittedDeptSectionsQtr4(Budget chosenBudget) {
        return repository.findAllByBudgetAndSubmitQtr4(chosenBudget, true)
                .stream().map(SectionBudgetPerformance::getDeptSection).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }
}
