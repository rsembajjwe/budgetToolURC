
package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.OldDeptSection;
import com.methaltech.application.data.oldbgtool.repository.OldDeptSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OldDeptSectionService {
    private final OldDeptSectionRepository deptSectionRepository;

    @Autowired
    public OldDeptSectionService(OldDeptSectionRepository deptSectionRepository) {
        this.deptSectionRepository = deptSectionRepository;
    }
    
    public List<OldDeptSection> getAllDeptSections() {
        return deptSectionRepository.findAll();
    }

    public OldDeptSection getDeptSectionById(Integer id) {
        return deptSectionRepository.findById(id).orElse(null);
    }

    public OldDeptSection saveDeptSection(OldDeptSection deptSection) {
        return deptSectionRepository.save(deptSection);
    }

    public void deleteDeptSection(Integer id) {
        deptSectionRepository.deleteById(id);
    }
}

