
package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.livedata.UrcDeptUnitAnlDimView;
import com.methaltech.application.data.livedata.repository.UrcDeptUnitAnlDimViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UrcDeptUnitAnlDimViewService {
    private final UrcDeptUnitAnlDimViewRepository repository;

    @Autowired
    public UrcDeptUnitAnlDimViewService(UrcDeptUnitAnlDimViewRepository repository) {
        this.repository = repository;
    }

    public List<UrcDeptUnitAnlDimView> getAllViews() {
        return repository.findAll();
    }
     public Optional<UrcDeptUnitAnlDimView> findByANL_CODE(String ANL_CODE) {
        return repository.findByANLCODE(ANL_CODE);
    }   
    
    /*    public List<UrcDeptUnitAnlDimView> getAllViewsgreaterthan2() {
    return repository.findByANL_CODEGreaterThan2Chars();
    }*/
}

