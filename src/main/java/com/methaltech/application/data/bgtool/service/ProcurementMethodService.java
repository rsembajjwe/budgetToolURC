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

    public List<ProcurementMethod> getAllProcurementMethods() {
        return procurementMethodRepository.findAll();
    }

    public Optional<ProcurementMethod> getProcurementMethodById(Long id) {
        return procurementMethodRepository.findById(id);
    }

    public ProcurementMethod saveProcurementMethod(ProcurementMethod procurementMethod) {
        return procurementMethodRepository.save(procurementMethod);
    }

    public void deleteProcurementMethod(Long id) {
        procurementMethodRepository.deleteById(id);
    }

    public ProcurementMethod findByNum(Integer num) {
        return procurementMethodRepository.findByNum(num);
    }
}
