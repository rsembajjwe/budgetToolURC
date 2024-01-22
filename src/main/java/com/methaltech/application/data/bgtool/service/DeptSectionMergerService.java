package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.DeptSectionMergerRepository;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeptSectionMergerService {

    private final DeptSectionMergerRepository repository;

    @Autowired
    public DeptSectionMergerService(DeptSectionMergerRepository repository) {
        this.repository = repository;
    }

    public List<DeptSectionMerger> getAllUrcDeptAndSections() {
        return repository.findAll();
    }

    public Optional<DeptSectionMerger> findByDeptcodeCustom(String deptcode) {
        return repository.findByDeptcodeCustom(deptcode);
    }

    public DeptSectionMerger update(DeptSectionMerger entity) {
        return repository.save(entity);
    }
    

    public Optional<DeptSectionMerger> findByDeptcode(String deptcode) {
        return repository.findByDeptcode(deptcode);
    }
}
