package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.entity.bgtool.PassengerActualVolumes;
import com.methaltech.application.data.entity.bgtool.dto.PassengerQuarterAgg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PassengerActualVolumesRepository extends JpaRepository<PassengerActualVolumes, Long> {

    List<PassengerActualVolumes> findByBudgetIdAndCoacodeDisplay(Long budgetId, Display display);

    Optional<PassengerActualVolumes> findByBudgetIdAndCoacodeId(Long budgetId, Long coaId);

    @Query("""
    SELECT
      SUM(COALESCE(pa.jul,0) + COALESCE(pa.aug,0) + COALESCE(pa.sep,0)),
      SUM(COALESCE(pa.oct,0) + COALESCE(pa.nov,0) + COALESCE(pa.dec,0)),
      SUM(COALESCE(pa.jan,0) + COALESCE(pa.feb,0) + COALESCE(pa.mar,0)),
      SUM(COALESCE(pa.apr,0) + COALESCE(pa.may,0) + COALESCE(pa.jun,0)),
      SUM(
          COALESCE(pa.jul,0) + COALESCE(pa.aug,0) + COALESCE(pa.sep,0) +
          COALESCE(pa.oct,0) + COALESCE(pa.nov,0) + COALESCE(pa.dec,0) +
          COALESCE(pa.jan,0) + COALESCE(pa.feb,0) + COALESCE(pa.mar,0) +
          COALESCE(pa.apr,0) + COALESCE(pa.may,0) + COALESCE(pa.jun,0)
      )
    FROM PassengerActualVolumes pa
    WHERE pa.budget.id = :budgetId
      AND pa.coacode.id = :coaId
""")
    Object[] sumQuartersAndYearForBudgetAndCoa(@Param("budgetId") Long budgetId,
            @Param("coaId") Long coaId);

}
