
package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.UserUnits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserUnitsRepository extends JpaRepository<UserUnits, Integer> {
    List<UserUnits> findByUserId(Integer userId);
}

