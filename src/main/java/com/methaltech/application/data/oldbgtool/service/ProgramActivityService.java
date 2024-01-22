package com.methaltech.application.data.oldbgtool.service;


import com.methaltech.application.data.entity.oldbgtool.ProgramActivity;
import com.methaltech.application.data.oldbgtool.repository.ProgramActivityRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProgramActivityService {

    private final ProgramActivityRepository repository;

    public ProgramActivityService(ProgramActivityRepository repository) {
        this.repository = repository;
    }

    public List<ProgramActivity> getProgramActivityByProgramme(Integer programme) {
        return repository.findByProg(programme);
    }
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
