package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByEmail(String username);

    /*    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.email = :email")
    User findByEmailWithDepartments(@Param("email") String email);*/
}
