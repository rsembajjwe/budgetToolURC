package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.UserRepository;
import com.methaltech.application.data.entity.bgtool.User;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> get(Integer id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    @Transactional
    public User getUserByEmail(String email) {
        User user = repository.findByEmail(email);
        Hibernate.initialize(user.getDepartment());  // Initialize the collection
        return user;
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }


    public boolean getUsername(String username) {
        return repository.findByEmail(username) != null;

    }
}
