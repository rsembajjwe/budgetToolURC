
package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.livedata.UrcBdgtEntity;
import com.methaltech.application.data.livedata.repository.UrcBdgtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UrcBdgtService {

    private final UrcBdgtRepository urcBdgtRepository;

    @Autowired
    public UrcBdgtService(UrcBdgtRepository urcBdgtRepository) {
        this.urcBdgtRepository = urcBdgtRepository;
    }

    public List<UrcBdgtEntity> getAllBudgets() {
        return urcBdgtRepository.findAll();
    }

    public Optional<UrcBdgtEntity> getBudgetById(int bdgtId) {
        return urcBdgtRepository.findById(bdgtId);
    }

    public UrcBdgtEntity saveBudget(UrcBdgtEntity bdgt) {
        return urcBdgtRepository.save(bdgt);
    }

    public void deleteBudget(int bdgtId) {
        urcBdgtRepository.deleteById(bdgtId);
    }
}

