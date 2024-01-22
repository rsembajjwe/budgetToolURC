package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.DepartmentUnit;
import com.methaltech.application.data.oldbgtool.repository.DepartmentUnitRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DepartmentUnitService {

    private final DepartmentUnitRepository repository;

    public DepartmentUnitService(DepartmentUnitRepository repository) {
        this.repository = repository;
    }

    public List<DepartmentUnit> getDepartmentUnitBySection(Integer sectionId) {
        return repository.findBySectionId(sectionId);
    }

    public List<DepartmentUnit> getDepartmentUnits() {
        return repository.findAll();
    }

    public Optional<DepartmentUnit> getUnitById2(Integer id) {
        return repository.findById(id);
    }
    public DepartmentUnit getUnitById(Integer id) {
        return repository.findById(id).get();
    }    
}
