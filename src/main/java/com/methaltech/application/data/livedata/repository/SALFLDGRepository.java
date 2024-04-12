package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.SALFLDG;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SALFLDGRepository extends JpaRepository<SALFLDG, String> {

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period = :period")
    BigDecimal findSumOfAmountByAccntCodeAndPeriod(@Param("accntCode") String accntCode, @Param("period") int period);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period = :period AND s.analT1 IN :analT1Set")
    BigDecimal findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(@Param("accntCode") String accntCode,
            @Param("period") int period,
            @Param("analT1Set") Set<String> analT1Set);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period IN :periods")
    BigDecimal findSumOfAmountByAccntCodeAndPeriods(@Param("accntCode") String accntCode, @Param("periods") List<Integer> periods);
    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode IN :accntCode AND s.period IN :periods")
    BigDecimal findSumOfAmountByAccntCodeAndPeriods(@Param("accntCode") List<String> accntCode, @Param("periods") List<Integer> periods);    

}
