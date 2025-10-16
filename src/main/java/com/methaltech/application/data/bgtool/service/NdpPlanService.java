
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.NdpPlanRepository;
import com.methaltech.application.data.entity.bgtool.NdpPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NdpPlanService {

    private final NdpPlanRepository repository;

    public NdpPlanService(NdpPlanRepository repository) {
        this.repository = repository;
    }

    public List<NdpPlan> findAll() {
        return repository.findAll();
    }
    public List<NdpPlan> findAllWithDetails() {
        return repository.findAllWithDetails();
    }    

    public Optional<NdpPlan> findById(Long id) {
        return repository.findById(id);
    }

    public NdpPlan save(NdpPlan ndpPlan) {
        return repository.save(ndpPlan);
    }

    public void delete(NdpPlan ndpPlan) {
        repository.delete(ndpPlan);
    }
}
