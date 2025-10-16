
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.NDPIII_Objective;
import com.methaltech.application.data.bgtool.repository.NDPIII_ObjectiveRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NDPIII_ObjectiveService {

    private final NDPIII_ObjectiveRepository repository;

    @Autowired
    public NDPIII_ObjectiveService(NDPIII_ObjectiveRepository repository) {
        this.repository = repository;
    }

    public Optional<NDPIII_Objective> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public NDPIII_Objective update(NDPIII_Objective entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<NDPIII_Objective> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public Page<NDPIII_Objective> listByBudget(Budget budget,Pageable pageable) {
        return repository.findByBudget(budget, pageable);
    }
    public int count() {
        return (int) repository.count();
    }    
}
