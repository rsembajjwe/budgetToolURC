
package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UrcBdgtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcBdgtRepository extends JpaRepository<UrcBdgtEntity, Integer> {
    // You can add custom query methods if needed
}

