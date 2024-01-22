
package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.DepartmentSection;
import com.methaltech.application.data.oldbgtool.repository.DepartmentSectionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DepartmentSectionService {
    private final DepartmentSectionRepository repository;

    public DepartmentSectionService(DepartmentSectionRepository repository) {
        this.repository = repository;
    }
    public List<DepartmentSection> getAllDepartmentSections() {
        return repository.findAll();
    }     
}
