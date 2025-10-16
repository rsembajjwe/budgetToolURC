package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.livedata.UrcDeptSectionAnlDim;
import com.methaltech.application.data.livedata.repository.UrcDeptSectionAnlDimRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataDuplicationService {

    @Autowired
    private UrcDeptSectionAnlDimRepository deptSectionRepository; // Repository for UrcDeptSectionAnlDim entity in THESUN
    @Autowired
    private EntityManager entityManager; // EntityManager for the bgtool database*/

    @Transactional
    public void duplicateData() {
        List<UrcDeptSectionAnlDim> deptSections = deptSectionRepository.findAll();

        for (UrcDeptSectionAnlDim deptSection : deptSections) {
            // Create or update the corresponding entity in the bgtool database
            UrcDeptSectionAnlDimbgt bgtoolDeptSection = entityManager.find(UrcDeptSectionAnlDimbgt.class, deptSection.getANL_CODE());

            if (bgtoolDeptSection == null) {
                // If the entity doesn't exist in bgtool, create it
                bgtoolDeptSection = new UrcDeptSectionAnlDimbgt();
                bgtoolDeptSection.setANL_CAT_ID(deptSection.getANL_CAT_ID());
                bgtoolDeptSection.setANL_CODE(deptSection.getANL_CODE());
                bgtoolDeptSection.setNAME(deptSection.getNAME());
                // Copy relevant data from deptSection to bgtoolDeptSection
                // Set any additional properties as needed
            } else {
                bgtoolDeptSection.setANL_CAT_ID(deptSection.getANL_CAT_ID());
                bgtoolDeptSection.setANL_CODE(deptSection.getANL_CODE());
                bgtoolDeptSection.setNAME(deptSection.getNAME());
            }

            // Persist the entity in the bgtool database
            entityManager.persist(bgtoolDeptSection);
        }
    }
}
