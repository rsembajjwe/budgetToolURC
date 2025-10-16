package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.UrcStrategicPlanRepository;
import com.methaltech.application.data.entity.bgtool.UrcStrategicPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UrcStrategicPlanService {

    private final UrcStrategicPlanRepository repository;

    public UrcStrategicPlanService(UrcStrategicPlanRepository repository) {
        this.repository = repository;
    }

    public UrcStrategicPlan save(UrcStrategicPlan plan) {
        return repository.save(plan);
    }

    @Transactional(readOnly = true)
    public List<UrcStrategicPlan> findAll() {
        return repository.findAll();
    }


    public Optional<UrcStrategicPlan> findById(Long id) {
        return repository.findById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
