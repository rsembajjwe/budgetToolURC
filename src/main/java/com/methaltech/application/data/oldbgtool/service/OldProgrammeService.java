
package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.OldProgramme;
import com.methaltech.application.data.oldbgtool.repository.OldProgrammeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OldProgrammeService {
    private final OldProgrammeRepository programmeRepository;

    @Autowired
    public OldProgrammeService(OldProgrammeRepository programmeRepository) {
        this.programmeRepository = programmeRepository;
    }

    public OldProgramme saveProgramme(OldProgramme programme) {
        return programmeRepository.save(programme);
    }

    public List<OldProgramme> getAllProgrammes() {
        return programmeRepository.findAll();
    }

    public OldProgramme getProgrammeById(int id) {
        return programmeRepository.findById(id).orElse(null);
    }

    public void deleteProgramme(int id) {
        programmeRepository.deleteById(id);
    } 
    public List<OldProgramme> findProgrammesByFy(String fy) {
        return programmeRepository.findByFy(fy);
    }    
}
