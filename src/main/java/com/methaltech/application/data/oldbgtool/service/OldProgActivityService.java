
package com.methaltech.application.data.oldbgtool.service;
import com.methaltech.application.data.entity.oldbgtool.OldProgActivity;
import com.methaltech.application.data.oldbgtool.repository.OldProgActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OldProgActivityService {

    private final OldProgActivityRepository progActivityRepository;

    @Autowired
    public OldProgActivityService(OldProgActivityRepository progActivityRepository) {
        this.progActivityRepository = progActivityRepository;
    }

    public List<OldProgActivity> getAllProgActivities() {
        return progActivityRepository.findAll();
    }
    
    public List<OldProgActivity> getAllProgActivitiesbyProg(int prog) {
        return progActivityRepository.findByProg(prog);
    }    

    public Optional<OldProgActivity> getProgActivityById(Integer id) {
        return progActivityRepository.findById(id);
    }

    public OldProgActivity saveProgActivity(OldProgActivity progActivity) {
        return progActivityRepository.save(progActivity);
    }

    public void deleteProgActivity(Integer id) {
        progActivityRepository.deleteById(id);
    }
}

