
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.NDPIII_Objective;
import com.methaltech.application.data.entity.bgtool.National_Transport_Master_Plan;
import com.methaltech.application.data.bgtool.repository.National_Transport_Master_PlanRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class National_Transport_Master_PlanService {

    private final National_Transport_Master_PlanRepository repository;

    @Autowired
    public National_Transport_Master_PlanService(National_Transport_Master_PlanRepository repository) {
        this.repository = repository;
    }

    public Optional<National_Transport_Master_Plan> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public National_Transport_Master_Plan update(National_Transport_Master_Plan entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<National_Transport_Master_Plan> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public Page<National_Transport_Master_Plan> listByNDP3Objective(NDPIII_Objective entity,Pageable pageable) {
        return repository.findByNdp111Objective(entity, pageable);
    }
    public int count() {
        return (int) repository.count();
    }        
}
