package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.URC_Strategic_Plan;
import com.methaltech.application.data.bgtool.repository.URC_Priority_AreasRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class URC_Priority_AreasService {

    private final URC_Priority_AreasRepository repository;

    @Autowired
    public URC_Priority_AreasService(URC_Priority_AreasRepository repository) {
        this.repository = repository;
    }

    public Optional<URC_Priority_Areas> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public URC_Priority_Areas update(URC_Priority_Areas entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<URC_Priority_Areas> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<URC_Priority_Areas> findByUrcStrategicPlan(URC_Strategic_Plan ndp111Objective, Pageable pageable) {
        return repository.findByUrcStrategicPlan(ndp111Objective, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<URC_Priority_Areas> findByNDPIIIObjectiveBudget(Budget budget) {
        return repository.findByNDPIIIObjectiveBudget(budget);
    }

    public List<URC_Priority_Areas> findPriorityAreasByBudget(Budget budget) {
        return repository.findByBudget(budget);
    }

    public List<URC_Priority_Areas> findByNameAndBudget(String name, Budget budget) {
        return repository.customSearchByNameAndBudget(name, budget);
    }

    public List<URC_Priority_Areas> findByBudget(Budget budget) {
        return repository.findByBudget(budget);
    }

    public URC_Priority_Areas getLastSavedItem() {
        Optional<URC_Priority_Areas> lastItemOptional = repository.findFirstByOrderByIdDesc();
        return lastItemOptional.orElse(null);
    }

    public URC_Priority_Areas getById(Long id) {
        // URC_Priority_Areas urcPriorityArea = repository.findById(id).orElse(null);
        return repository.findById(id).orElse(null);
    }
}
