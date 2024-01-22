
package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UrcAcnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcAcntRepository extends JpaRepository<UrcAcnt, Long> {
    
}
