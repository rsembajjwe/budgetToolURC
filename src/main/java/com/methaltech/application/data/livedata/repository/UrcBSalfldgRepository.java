package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UrcBSalfldg;
import com.methaltech.application.data.entity.livedata.UrcBSalfldgId;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcBSalfldgRepository extends JpaRepository<UrcBSalfldg, UrcBSalfldgId> {

    /*    List<UrcBSalfldg> findByAccntCodeAndPeriod(String accntCode, Integer period);
    
    List<UrcBSalfldg> findByAccntCodeAndPeriodIn(String accntCode, List<Integer> periods);*/
    List<UrcBSalfldg> findByIdAccntCodeAndIdPeriod(String accntCode, Integer period);

    /*    @Query("SELECT SUM(u.amount) FROM UrcBSalfldg u WHERE u.accntCode = :accntCode AND u.period IN :periods")
    BigDecimal findTotalAmountByAccntCodeAndPeriodIn(@Param("accntCode") String accntCode, @Param("periods") List<Integer> periods);
    
    @Query("SELECT SUM(u.amount) FROM UrcBSalfldg u WHERE u.accntCode = :accntCode AND u.period = :period")
    BigDecimal findTotalAmountByAccntCodeAndPeriod(@Param("accntCode") String accntCode, @Param("period") Integer period);*/
 /*    @Query("SELECT SUM(u.amount) FROM UrcBSalfldg u WHERE u.id.accntCode = :accntCode")
    BigDecimal findTotalAmountByAccntCode(@Param("accntCode") String accntCode);*/
    @Query("SELECT CASE WHEN SUM(u.amount) >= 0 THEN SUM(u.amount) ELSE SUM(u.amount) * -1 END "
            + "FROM UrcBSalfldg u WHERE u.id.accntCode = :accntCode")
    BigDecimal findTotalAmountByAccntCode(@Param("accntCode") String accntCode);
}
