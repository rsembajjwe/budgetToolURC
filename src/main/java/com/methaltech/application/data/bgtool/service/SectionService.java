package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.Department;
import com.methaltech.application.data.entity.bgtool.Section;
import com.methaltech.application.data.bgtool.repository.SectionRepository;
import com.methaltech.application.data.bgtool.repository.UnitRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final UnitRepository unitRepository;

    public SectionService(SectionRepository sectionRepository, UnitRepository unitRepository) {
        this.sectionRepository = sectionRepository;
        this.unitRepository = unitRepository;
    }

    public Section create(Section section) {
        return sectionRepository.save(section);
    }

    public Section update(Section section) {
        return sectionRepository.save(section);
    }

    public List<Section> findAll() {
        return sectionRepository.findAll();
    }

    public Page<Section> list(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    public Section findById(Long id) {
        return sectionRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        sectionRepository.deleteById(id);
    }

    public List<D_Unit> findUnitsBySectionId(Long sectionId) {
        Section section = sectionRepository.findById(sectionId).orElse(null);
        if (section == null) {
            throw new IllegalArgumentException("Invalid section ID provided");
        }
        return unitRepository.findBySectionId(section);
    }
    public List<Section> findByDepartment(Department department) {
        return sectionRepository.findByDepartment(department);
    }   
    
    public List<Section> findAllSectionsByBudget(Budget budget) {
        return sectionRepository.findAllSectionsByBudget(budget);
    } 
    public List<Section> findSectionsBelongingToDUnits(List<D_Unit> dunitList) {
        // Extract department IDs from the list of D_Unit objects
        List<Long> departmentIds = dunitList.stream()
                .map(D_Unit::getDepartment)
                .map(Department::getId)
                .collect(Collectors.toList());

        // Use the extracted department IDs to find associated sections
        return sectionRepository.findByDepartmentIdIn(departmentIds);
    }    
}
