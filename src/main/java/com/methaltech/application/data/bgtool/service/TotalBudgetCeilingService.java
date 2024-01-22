
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.TotalBudgetCeiling;
import com.methaltech.application.data.bgtool.repository.TotalBudgetCeilingRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class TotalBudgetCeilingService {
    private final TotalBudgetCeilingRepository repository;

    @Autowired
    public TotalBudgetCeilingService(TotalBudgetCeilingRepository repository) {
        this.repository = repository;
    }

    public Optional<TotalBudgetCeiling> get(Long id) {
        return repository.findById(id);
    }

    public TotalBudgetCeiling update(TotalBudgetCeiling entity) {
        return repository.save(entity);
    }

    public Page<TotalBudgetCeiling> list(Pageable pageable) {
        return repository.findAll(pageable);
    }    
}
