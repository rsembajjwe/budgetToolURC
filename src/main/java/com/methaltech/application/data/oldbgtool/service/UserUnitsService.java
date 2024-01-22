
package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.UserUnits;
import com.methaltech.application.data.oldbgtool.repository.UserUnitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUnitsService {

    private final UserUnitsRepository userUnitsRepository;

    @Autowired
    public UserUnitsService(UserUnitsRepository userUnitsRepository) {
        this.userUnitsRepository = userUnitsRepository;
    }

    public List<UserUnits> findByUserId(Integer userId) {
        return userUnitsRepository.findByUserId(userId);
    }

    // You can add more methods here for custom business logic or additional operations.
}

