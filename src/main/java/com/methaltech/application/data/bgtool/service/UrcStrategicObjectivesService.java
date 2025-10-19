package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.UrcStrategicObjectivesRepository;
import com.methaltech.application.data.entity.bgtool.UrcStrategicObjectives;
import com.methaltech.application.data.entity.bgtool.UrcStrategicPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UrcStrategicObjectivesService {

    private final UrcStrategicObjectivesRepository repository;

    public UrcStrategicObjectivesService(UrcStrategicObjectivesRepository repository) {
        this.repository = repository;
    }

    public UrcStrategicObjectives save(UrcStrategicObjectives objective) {
        return repository.save(objective);
    }

    public List<UrcStrategicObjectives> findAll() {
        return repository.findAll();
    }

    public Optional<UrcStrategicObjectives> findById(Long id) {
        return repository.findById(id);
    }

    /*
    public List<UrcStrategicObjectives> findByStrategicPlan(UrcStrategicPlan plan) {
    return repository.findByUrcStrategicPlan(plan);
    }
    
    
    public List<UrcStrategicObjectives> searchByObjective(String keyword) {
    return repository.findByObjectiveContainingIgnoreCase(keyword);
    }*/
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<UrcStrategicObjectives> findByStrategicPlan(UrcStrategicPlan strategicPlan) {
        return repository.findByStrategicPlan(strategicPlan);
    }
}
