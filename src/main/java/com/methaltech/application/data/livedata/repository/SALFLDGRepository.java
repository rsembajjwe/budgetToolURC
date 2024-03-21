package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.SALFLDG;
import java.math.BigDecimal;
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

}
