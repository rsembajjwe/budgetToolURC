package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.OldDeptUnit;
import com.methaltech.application.data.oldbgtool.repository.OldDeptUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OldDeptUnitService {

    private final OldDeptUnitRepository deptUnitRepository;

    @Autowired
    public OldDeptUnitService(OldDeptUnitRepository deptUnitRepository) {
        this.deptUnitRepository = deptUnitRepository;
    }

    public List<OldDeptUnit> getAllDeptUnits() {
        return deptUnitRepository.findAll();
    }

    public Optional<OldDeptUnit> getDeptUnitById(int id) {
        return deptUnitRepository.findById(id);
    }

    public OldDeptUnit saveDeptUnit(OldDeptUnit deptUnit) {
        return deptUnitRepository.save(deptUnit);
    }

    public void deleteDeptUnit(int id) {
        deptUnitRepository.deleteById(id);
    }
}
