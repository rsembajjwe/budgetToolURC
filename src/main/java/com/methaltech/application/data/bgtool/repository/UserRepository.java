package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

        User findByEmail(String username);
}
