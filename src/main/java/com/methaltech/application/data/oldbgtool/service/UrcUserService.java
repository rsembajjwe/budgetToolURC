
package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.UrcUser;
import com.methaltech.application.data.oldbgtool.repository.UrcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UrcUserService {

    private final UrcUserRepository userRepository;

    @Autowired
    public UrcUserService(UrcUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UrcUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UrcUser> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public UrcUser saveUser(UrcUser user) {
        return userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }


}

