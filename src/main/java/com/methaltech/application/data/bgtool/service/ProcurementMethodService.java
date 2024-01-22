
package com.methaltech.application.data.bgtool.service;


import com.methaltech.application.data.bgtool.repository.ProcurementMethodRepository;
import com.methaltech.application.data.entity.bgtool.ProcurementMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProcurementMethodService {

    private final ProcurementMethodRepository procurementMethodRepository;

    @Autowired
    public ProcurementMethodService(ProcurementMethodRepository procurementMethodRepository) {
        this.procurementMethodRepository = procurementMethodRepository;
    }

    // Save a new procurement method
    public ProcurementMethod save(ProcurementMethod procurementMethod) {
        return procurementMethodRepository.save(procurementMethod);
    }

    // Get all procurement methods
    public List<ProcurementMethod> getAllProcurementMethods() {
        return procurementMethodRepository.findAll();
    }

    // Get a procurement method by ID
    public Optional<ProcurementMethod> getProcurementMethodById(Long id) {
        return procurementMethodRepository.findById(id);
    }

    // Delete a procurement method by ID
    public void deleteProcurementMethod(Long id) {
        procurementMethodRepository.deleteById(id);
    }
    // Delete a procurement method by object
    public void deleteProcurementMethod(ProcurementMethod procurementMethod) {
        procurementMethodRepository.delete(procurementMethod);
    }
}

