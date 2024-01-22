package com.methaltech.application.data.bgtool.service;


import com.methaltech.application.data.entity.bgtool.UnitBudgetCeiling;
import com.methaltech.application.data.bgtool.repository.UnitBudgetCeilingRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class UnitBudgetCeilingService {

    private final UnitBudgetCeilingRepository repository;

    @Autowired
    public UnitBudgetCeilingService(UnitBudgetCeilingRepository repository) {
        this.repository = repository;
    }

    public Optional<UnitBudgetCeiling> get(Long id) {
        return repository.findById(id);
    }

    public UnitBudgetCeiling update(UnitBudgetCeiling entity) {
        return repository.save(entity);
    }

    public Page<UnitBudgetCeiling> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
