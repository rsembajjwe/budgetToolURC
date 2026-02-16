package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.entity.bgtool.FreightActualBudgetDTO;
import com.methaltech.application.data.entity.bgtool.FreightActualVolumes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface FreightActualVolumesRepository
        extends JpaRepository<FreightActualVolumes, Long> {

    @Query("""
        SELECT new com.methaltech.application.data.entity.bgtool.FreightActualBudgetDTO(
            fa,
            fb.jul, fb.aug, fb.sep, fb.oct, fb.nov, fb.dec,
            fb.jan, fb.feb, fb.mar, fb.apr, fb.may, fb.jun,
            fb.total
        )
        FROM FreightActualVolumes fa
        JOIN FreightVolumes fb
            ON fb.budget = fa.budget
           AND fb.coacode = fa.coacode
        WHERE fa.budget.id = :budgetId
          AND fa.coacode.display = :display
    """)
    List<FreightActualBudgetDTO> findActualWithBudgetByDisplay(
            @Param("budgetId") Long budgetId,
            @Param("display") Display display
    );

    @Query("""
    SELECT new com.methaltech.application.data.entity.bgtool.FreightActualBudgetDTO(
        fa, bi
    )
    FROM BudgetItems bi
    LEFT JOIN FreightActualVolumes fa
        ON fa.coacode = bi.coacode
       AND fa.budget = bi.budget
    WHERE bi.budget.id = :budgetId
      AND bi.coacode.display = :display
""")
    List<FreightActualBudgetDTO> findBudgetWithActuals(
            @Param("budgetId") Long budgetId,
            @Param("display") Display display
    );

    List<FreightActualVolumes> findByBudgetIdAndCoacodeDisplay(Long budgetId, Display display);

    @Query("""
        SELECT
          SUM(COALESCE(fa.jul,0) + COALESCE(fa.aug,0) + COALESCE(fa.sep,0)),
          SUM(COALESCE(fa.oct,0) + COALESCE(fa.nov,0) + COALESCE(fa.dec,0)),
          SUM(COALESCE(fa.jan,0) + COALESCE(fa.feb,0) + COALESCE(fa.mar,0)),
          SUM(COALESCE(fa.apr,0) + COALESCE(fa.may,0) + COALESCE(fa.jun,0)),
          SUM(
              COALESCE(fa.jul,0) + COALESCE(fa.aug,0) + COALESCE(fa.sep,0) +
              COALESCE(fa.oct,0) + COALESCE(fa.nov,0) + COALESCE(fa.dec,0) +
              COALESCE(fa.jan,0) + COALESCE(fa.feb,0) + COALESCE(fa.mar,0) +
              COALESCE(fa.apr,0) + COALESCE(fa.may,0) + COALESCE(fa.jun,0)
          )
        FROM FreightActualVolumes fa
        WHERE fa.budget.id = :budgetId
          AND fa.coacode.id = :coaId
    """)
    Object[] sumQuartersAndYearForBudgetAndCoa(@Param("budgetId") Long budgetId,
            @Param("coaId") Long coaId);

    @Query("""
        SELECT
          SUM(COALESCE(fa.jul,0) + COALESCE(fa.aug,0) + COALESCE(fa.sep,0)),
          SUM(COALESCE(fa.oct,0) + COALESCE(fa.nov,0) + COALESCE(fa.dec,0)),
          SUM(COALESCE(fa.jan,0) + COALESCE(fa.feb,0) + COALESCE(fa.mar,0)),
          SUM(COALESCE(fa.apr,0) + COALESCE(fa.may,0) + COALESCE(fa.jun,0)),
          SUM(
              COALESCE(fa.jul,0) + COALESCE(fa.aug,0) + COALESCE(fa.sep,0) +
              COALESCE(fa.oct,0) + COALESCE(fa.nov,0) + COALESCE(fa.dec,0) +
              COALESCE(fa.jan,0) + COALESCE(fa.feb,0) + COALESCE(fa.mar,0) +
              COALESCE(fa.apr,0) + COALESCE(fa.may,0) + COALESCE(fa.jun,0)
          )
        FROM FreightActualVolumes fa
        WHERE fa.budget.id = :budgetId
          AND fa.coacode.id IN :coaIds
    """)
    Object[] sumQuartersAndYearForBudgetAndCoaSet(@Param("budgetId") Long budgetId,
                                                  @Param("coaIds") Set<Long> coaIds);

}
