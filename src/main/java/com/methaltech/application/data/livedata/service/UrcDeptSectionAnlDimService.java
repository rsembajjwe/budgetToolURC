package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.livedata.UrcDeptSectionAnlDim;
import com.methaltech.application.data.livedata.repository.UrcDeptSectionAnlDimRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UrcDeptSectionAnlDimService {

    private final UrcDeptSectionAnlDimRepository repository;

    @Autowired
    public UrcDeptSectionAnlDimService(UrcDeptSectionAnlDimRepository repository) {
        this.repository = repository;
    }

    public List<UrcDeptSectionAnlDim> getAllUrcSectionsAnlDims() {
        return repository.findByANL_CODEStartingWithD();
    }
    public List<UrcDeptSectionAnlDim> getAllUrcSections() {
        return repository.findAll();
    }    
    public UrcDeptSectionAnlDim findByANL_CODE(String anlCode) {
        return repository.findByCustomANL_CODE(anlCode);
    }
    public Page<UrcDeptSectionAnlDim> list(Pageable pageable) {
        return repository.findAll(pageable);
    } 
    public Page<UrcDeptSectionAnlDim> findByANL_CODEStartingWithD2(Pageable pageable) {
        return repository.findByANL_CODEStartingWithD2(pageable);
    }    
}
