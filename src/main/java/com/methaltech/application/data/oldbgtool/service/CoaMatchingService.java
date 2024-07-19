package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.CoaMatching;
import com.methaltech.application.data.entity.oldbgtool.OldBudgetSubItem;
import com.methaltech.application.data.oldbgtool.repository.CoaMatchingRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoaMatchingService {

    private final CoaMatchingRepository coaMatchingRepository;

    @Autowired
    public CoaMatchingService(CoaMatchingRepository coaMatchingRepository) {
        this.coaMatchingRepository = coaMatchingRepository;
    }

    public List<CoaMatching> getAllCoaMatching() {
        return coaMatchingRepository.findAll();
    }

    public CoaMatching createBudget(CoaMatching budget) {
        return coaMatchingRepository.save(budget);
    }
}
