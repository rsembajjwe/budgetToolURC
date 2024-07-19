
package com.methaltech.application.data.bgtool.service;
import com.methaltech.application.data.bgtool.repository.COAReconcileRepository;
import com.methaltech.application.data.entity.bgtool.COAReconcile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class COAReconcileService {

    private final COAReconcileRepository coaReconcileRepository;

    @Autowired
    public COAReconcileService(COAReconcileRepository coaReconcileRepository) {
        this.coaReconcileRepository = coaReconcileRepository;
    }

    public List<COAReconcile> getAllCOAReconciles() {
        return coaReconcileRepository.findAll();
    }

    public Optional<COAReconcile> getCOAReconcileById(Long id) {
        return coaReconcileRepository.findById(id);
    }

    public COAReconcile createCOAReconcile(COAReconcile coaReconcile) {
        return coaReconcileRepository.save(coaReconcile);
    }

    public void deleteCOAReconcile(Long id) {
        coaReconcileRepository.deleteById(id);
    }

    public List<COAReconcile> findCOAReconcileByOldcoa(String oldcoa) {
        return coaReconcileRepository.findByOldcoa(oldcoa);
    }
}

