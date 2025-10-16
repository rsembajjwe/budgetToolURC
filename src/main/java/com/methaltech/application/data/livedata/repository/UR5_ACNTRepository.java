package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UR5_ACNT;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UR5_ACNTRepository extends JpaRepository<UR5_ACNT, String> {

    // List<UR5_ACNT> findByAcntCodeStartingWithAndAcntSubTypeGreaterThanAndAcntSubTypeLessThan(String prefix, Integer greaterThan, Integer lessThan);
    @Query("SELECT ua FROM UR5_ACNT ua WHERE (ua.acntCode LIKE '1%' OR ua.acntCode LIKE '2%' OR ua.acntCode LIKE '3%') AND LEN(RTRIM(ua.acntCode)) > 5 AND LEN(RTRIM(ua.acntCode)) < 7")
    List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();

@Query("SELECT ua FROM UR5_ACNT ua WHERE (UPPER(ua.acntCode) LIKE CONCAT('%', UPPER(:searchString), '%') OR UPPER(ua.descr) LIKE CONCAT('%', UPPER(:searchString), '%')) AND ua.acntCode LIKE (:codeString%)  AND LENGTH(RTRIM(ua.acntCode)) > 5 AND LENGTH(RTRIM(ua.acntCode)) < 7")
List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeLengthRange(@Param("searchString") String searchString,@Param("codeString") String codeString);
UR5_ACNT findByAcntCode(String acntCode);

}
