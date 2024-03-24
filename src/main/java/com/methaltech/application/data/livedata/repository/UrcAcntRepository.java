package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.URC_ACNT;
import com.methaltech.application.data.entity.livedata.UrcAcnt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcAcntRepository extends JpaRepository<UrcAcnt, Long> {

    @Query("SELECT u FROM URC_ACNT u WHERE u.acntCode LIKE CONCAT(:startsWith, '%')")
    List<URC_ACNT> findByAcntCodeStartingWith(String startsWith);
    
    @Query("SELECT u FROM URC_ACNT u WHERE TRIM(u.acntCode) LIKE CONCAT(:startsWith, '%') AND SUBSTRING(u.acntCode, 6, 1) IN (:nextDigits)")
    List<URC_ACNT> findByAcntCodeStartingWith11110AndNextDigitIn(String startsWith, List<String> nextDigits);    
}
