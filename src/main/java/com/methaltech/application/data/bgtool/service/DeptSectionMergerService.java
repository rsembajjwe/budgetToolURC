package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.DepartmentRepository;
import com.methaltech.application.data.bgtool.repository.DeptSectionMergerRepository;
import com.methaltech.application.data.entity.bgtool.Department;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.data.livedata.repository.UrcDepartmentAnlDimRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeptSectionMergerService {

    private final DeptSectionMergerRepository repository;
    private final UrcDepartmentAnlDimRepository deptrepository;

    @Autowired
    public DeptSectionMergerService(DeptSectionMergerRepository repository, UrcDepartmentAnlDimRepository deptrepository) {
        this.repository = repository;
        this.deptrepository = deptrepository;
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

    public String findDepartment(String sectcode) {
        String deptString = repository.findDeptcodeBySectioncode(sectcode);
        UrcDepartmentAnlDim dept = deptrepository.findByANL_CODE(deptString);
        return dept.getNAME();
    }
}
