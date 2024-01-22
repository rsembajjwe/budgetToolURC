
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.ProcurementTypeRepository;
import com.methaltech.application.data.entity.bgtool.ProcurementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProcurementTypeService {

    private final ProcurementTypeRepository procurementTypeRepository;

    @Autowired
    public ProcurementTypeService(ProcurementTypeRepository procurementTypeRepository) {
        this.procurementTypeRepository = procurementTypeRepository;
    }

    // Save a new procurement type
    public ProcurementType save(ProcurementType procurementType) {
        return procurementTypeRepository.save(procurementType);
    }

    // Get all procurement types
    public List<ProcurementType> getAllProcurementTypes() {
        return procurementTypeRepository.findAll();
    }

    // Get a procurement type by ID
    public Optional<ProcurementType> getProcurementTypeById(Long id) {
        return procurementTypeRepository.findById(id);
    }

    // Delete a procurement type by ID
    public void deleteProcurementTypeById(Long id) {
        procurementTypeRepository.deleteById(id);
    }

    // Delete a procurement type by object
    public void deleteProcurementType(ProcurementType procurementType) {
        procurementTypeRepository.delete(procurementType);
    }
}

