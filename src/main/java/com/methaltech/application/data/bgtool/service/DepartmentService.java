package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Department;
import com.methaltech.application.data.entity.bgtool.Section;
import com.methaltech.application.data.bgtool.repository.DepartmentRepository;
import com.methaltech.application.data.bgtool.repository.SectionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository repository;
    private final SectionRepository sectionRepository;

    @Autowired
    public DepartmentService(DepartmentRepository repository, SectionRepository sectionRepository) {
        this.repository = repository;
        this.sectionRepository = sectionRepository;
    }

    public Optional<Department> get(Long id) {
        return repository.findById(id);
    }

    public Optional<Department> getSectionById(Long id) {
        return repository.findById(id);

    }

    @Transactional
    public Department update(Department entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Department> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<Section> findSectionsByDepartmentId(Long departmentId) {
        Department department = repository.findById(departmentId).orElse(null);
        if (department == null) {
            throw new IllegalArgumentException("Invalid department ID provided");
        }
        return sectionRepository.findByDepartmentId(department);
    }
    public List<Department> findDepartmentByBudget(Budget budget){
     return   repository.findByBudget(budget);
    }
}
