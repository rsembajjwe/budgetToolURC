
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.StrategicObjectiveRepository;
import com.methaltech.application.data.entity.bgtool.NdpPlan;
import com.methaltech.application.data.entity.bgtool.StrategicObjective;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StrategicObjectiveService {

    private final StrategicObjectiveRepository repository;

    public StrategicObjectiveService(StrategicObjectiveRepository repository) {
        this.repository = repository;
    }

    public List<StrategicObjective> findAll() {
        return repository.findAll();
    }

    public List<StrategicObjective> findByNdpPlan(NdpPlan ndpPlan) {
        return repository.findByNdpPlan(ndpPlan);
    }

    public Optional<StrategicObjective> findById(Long id) {
        return repository.findById(id);
    }

    public StrategicObjective save(StrategicObjective obj) {
        return repository.save(obj);
    }

    public void delete(StrategicObjective obj) {
        repository.delete(obj);
    }
}
