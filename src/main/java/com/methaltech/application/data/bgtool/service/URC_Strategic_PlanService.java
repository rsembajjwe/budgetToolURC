package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.National_Budget_Focus_Areas;
import com.methaltech.application.data.entity.bgtool.URC_Strategic_Plan;
import com.methaltech.application.data.bgtool.repository.URC_Strategic_PlanRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class URC_Strategic_PlanService {

    private final URC_Strategic_PlanRepository repository;

    @Autowired
    public URC_Strategic_PlanService(URC_Strategic_PlanRepository repository) {
        this.repository = repository;
    }

    public Optional<URC_Strategic_Plan> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public URC_Strategic_Plan update(URC_Strategic_Plan entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<URC_Strategic_Plan> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public Page listByNational_Budget_Focus_Areas(National_Budget_Focus_Areas value, Pageable pageable) {
        return repository.findByNationalBudgetFocusArea(value, pageable);
    }
}
