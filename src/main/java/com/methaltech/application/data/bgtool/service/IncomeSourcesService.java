package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.IncomeSourcesRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.IncomeSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncomeSourcesService {

    private final IncomeSourcesRepository incomeSourcesRepository;

    @Autowired
    public IncomeSourcesService(IncomeSourcesRepository incomeSourcesRepository) {
        this.incomeSourcesRepository = incomeSourcesRepository;
    }

    public List<IncomeSources> getAllIncomeSources() {
        return incomeSourcesRepository.findAll();
    }

    public Optional<IncomeSources> getIncomeSourceById(Long id) {
        return incomeSourcesRepository.findById(id);
    }

    public List<IncomeSources> getIncomeSourcesByBudget(Budget budget) {
        return incomeSourcesRepository.findByBudget(budget);
    }

    public IncomeSources saveIncomeSource(IncomeSources incomeSource) {
        return incomeSourcesRepository.save(incomeSource);
    }

    public void deleteIncomeSource(Long id) {
        incomeSourcesRepository.deleteById(id);
    }

    public void deleteIncomeSourceByIncomeSource(String incomeSource) {
        incomeSourcesRepository.deleteByIncomeSource(incomeSource);
    }
    
    public void deleteIncomeSource(IncomeSources incomeSource) {
        incomeSourcesRepository.delete(incomeSource);
    }    

}
