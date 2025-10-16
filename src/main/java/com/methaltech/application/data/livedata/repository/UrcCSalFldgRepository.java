package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UrcCSalFldg;
import com.methaltech.application.data.entity.livedata.UrcCSalFldgId;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcCSalFldgRepository extends JpaRepository<UrcCSalFldg, UrcCSalFldgId> {

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_C_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 IN (:analT1Values)
        AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%')
        AND ACCNT_CODE NOT LIKE '321%'
        AND ACCNT_CODE NOT LIKE '314%'
        AND LEN(ACCNT_CODE) <= 6
        AND (
            (ANAL_T0 IS NOT NULL AND ANAL_T0 != '') 
            OR 
            (ANAL_T1 IS NOT NULL AND ANAL_T1 != '')
        )
""", nativeQuery = true)
    BigDecimal findTotalCommittedAmountByPeriodsAndAnalT1Set(
            @Param("periods") Set<Integer> periods,
            @Param("analT1Values") Set<String> analT1Values
    );

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_C_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 = :analT1Values
        AND ACCNT_CODE = :accCode
""", nativeQuery = true)
    BigDecimal findTotalCommittedAmountByPeriodsCodeAndSection(
            @Param("periods") Set<Integer> periods,
            @Param("analT1Values") String analT1Values,
            @Param("accCode") String accCode
    );
    
    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_C_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 = :analT1Values
        AND ACCNT_CODE = :accCode 
        AND ANAL_T8 = :activity
""", nativeQuery = true)
    BigDecimal findTotalCommittedAmountByPeriodsCodeAndSectionAndActivity(
            @Param("periods") Set<Integer> periods,
            @Param("analT1Values") String analT1Values,
            @Param("accCode") String accCode,
            @Param("activity") String activity
    );
}
