
package com.methaltech.application.data.bgtool.service;
import com.methaltech.application.data.bgtool.repository.PriorityAreaRepository;
import com.methaltech.application.data.entity.bgtool.NdpPlan;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PriorityAreaService {

    private final PriorityAreaRepository repository;

    public PriorityAreaService(PriorityAreaRepository repository) {
        this.repository = repository;
    }

    public List<PriorityArea> findAll() {
        return repository.findAll();
    }
    /*
    public List<PriorityArea> findByNdpPlan(NdpPlan ndpPlan) {
    return repository.findByNdpPlan(ndpPlan);
    }*/

    public Optional<PriorityArea> findById(Long id) {
        return repository.findById(id);
    }

    public PriorityArea save(PriorityArea priorityArea) {
        return repository.save(priorityArea);
    }

    public void delete(PriorityArea priorityArea) {
        repository.delete(priorityArea);
    }
    
    
    /**
     * Returns PriorityAreas whose NDP Plan is active on the given date.
     */
    public List<PriorityArea> getPriorityAreasActiveOn(LocalDate date) {
        return repository.findByNdpPlanActiveOn(date);
    }
}
