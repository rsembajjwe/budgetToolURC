
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.UnitsBudget;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.bgtool.repository.UnitsBudgetRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitsBudgetService {

    private final UnitsBudgetRepository repository;

    @Autowired
    public UnitsBudgetService(UnitsBudgetRepository repository) {
        this.repository = repository;
    }

    public Optional<UnitsBudget> get(Long id) {
        return repository.findById(id);
    }


    @Transactional
    public UnitsBudget update(UnitsBudget entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<UnitsBudget> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public List<UnitsBudget> list() {
        return repository.findAll();
    }    
    public UnitsBudget listbyBudget(Budget budget, User user) {
        return repository.findByBudgetAndUser(budget, user);
    } 
    public List<D_Unit> listUnitsbyBudget(Budget budget, User user) {
        return repository.findUnitsByBudgetAndUser(budget, user);
    }    
    public int count() {
        return (int) repository.count();
    }    
}
