
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.UrcDeptSectionAnlDimbgtRepository;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UrcDeptSectionAnlDimbgtService {

    private final UrcDeptSectionAnlDimbgtRepository repository;

    @Autowired
    public UrcDeptSectionAnlDimbgtService(UrcDeptSectionAnlDimbgtRepository repository) {
        this.repository = repository;
    }

    public List<UrcDeptSectionAnlDimbgt> getAllUrcSectionsAnlDims() {
        return repository.findByANL_CODEStartingWithS();
    }
    public UrcDeptSectionAnlDimbgt findByANL_CODE(String anlCode) {
        return repository.findByCustomANL_CODE(anlCode);
    }
    public Page<UrcDeptSectionAnlDimbgt> list(Pageable pageable) {
        return repository.findAll(pageable);
    } 
    public Page<UrcDeptSectionAnlDimbgt> findByANL_CODEStartingWithD2(Pageable pageable) {
        return repository.findByANL_CODEStartingWithS(pageable);
    } 
    public List<UrcDeptSectionAnlDimbgt> getAllUrcSectionsAnlDims2() {
        return repository.findAll();
    }  
    public Set<UrcDeptSectionAnlDimbgt> getAllUrcSectionsAsSet() {
        List<UrcDeptSectionAnlDimbgt> allSections = repository.findAll();
        return new HashSet<>(allSections);
    }    
}
