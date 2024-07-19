
package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.UrcUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcUserRepository extends JpaRepository<UrcUser, Integer> {
    // You can add custom query methods here if needed.
}
