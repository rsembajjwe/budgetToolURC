
package com.methaltech.application.data.oldbgtool.service;


import com.methaltech.application.data.entity.oldbgtool.Programme;
import com.methaltech.application.data.oldbgtool.repository.ProgrammeRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProgrammeService {
     private final ProgrammeRepository repository;

    public ProgrammeService(ProgrammeRepository repository) {
        this.repository = repository;
    }

    public List<Programme> getProgrammeByFinancialYear(String financialYear) {
        return repository.findByFy(financialYear);
    }
    public List<Programme> getAllProgrammes() {
        return repository.findAll();
    }    

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
