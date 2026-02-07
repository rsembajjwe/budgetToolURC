package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.FreightVolumes;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FreightVolumesRepository extends JpaRepository<FreightVolumes, Long> {

    List<FreightVolumes> findByBudgetAndCoacode(Budget budget, COA coacode);

    List<FreightVolumes> findByBudget(Budget budget);

    @Query("SELECT COALESCE(SUM(COALESCE(fv.jan, 0) + COALESCE(fv.feb, 0) + COALESCE(fv.mar, 0) + COALESCE(fv.apr, 0) + "
            + "COALESCE(fv.may, 0) + COALESCE(fv.jun, 0) + COALESCE(fv.jul, 0) + COALESCE(fv.aug, 0) + "
            + "COALESCE(fv.sep, 0) + COALESCE(fv.oct, 0) + COALESCE(fv.nov, 0) + COALESCE(fv.dec, 0)), 0) "
            + "FROM FreightVolumes fv WHERE fv.budget = :budget AND fv.coacode = :coacode")
    BigDecimal sumMonthsByBudgetAndCoacode(@Param("budget") Budget budget, @Param("coacode") COA coacode);

    @Query("SELECT COALESCE(SUM(COALESCE(fv.jan, 0) + COALESCE(fv.feb, 0) + COALESCE(fv.mar, 0) + COALESCE(fv.apr, 0) + "
            + "COALESCE(fv.may, 0) + COALESCE(fv.jun, 0) + COALESCE(fv.jul, 0) + COALESCE(fv.aug, 0) + "
            + "COALESCE(fv.sep, 0) + COALESCE(fv.oct, 0) + COALESCE(fv.nov, 0) + COALESCE(fv.dec, 0)), 0) "
            + "FROM FreightVolumes fv WHERE fv.budget = :budget AND fv.coacode IN :coacodes")
    BigDecimal sumMonthsByBudgetAndCoacodes(@Param("budget") Budget budget, @Param("coacodes") List<COA> coacodes);

    Long countByBudget(Budget budget);

    @Query("SELECT NEW com.methaltech.application.data.MonthlySumResponseFreight("
            + "COALESCE(SUM(fv.jul), 0), COALESCE(SUM(fv.aug), 0), COALESCE(SUM(fv.sep), 0), "
            + "COALESCE(SUM(fv.oct), 0), COALESCE(SUM(fv.nov), 0), COALESCE(SUM(fv.dec), 0), "
            + "COALESCE(SUM(fv.jan), 0), COALESCE(SUM(fv.feb), 0), COALESCE(SUM(fv.mar), 0), "
            + "COALESCE(SUM(fv.apr), 0), COALESCE(SUM(fv.may), 0), COALESCE(SUM(fv.jun), 0), "
            + "COALESCE(SUM(fv.total), 0)) "
            + "FROM FreightVolumes fv "
            + "WHERE fv.budget = :budget AND fv.coacode = :coacode")
    MonthlySumResponseFreight getMonthlySumsByBudgetAndCoacode(@Param("budget") Budget budget, @Param("coacode") COA coacode);

    @Query("SELECT NEW com.methaltech.application.data.MonthlySumResponseFreight("
            + "COALESCE(SUM(fv.jul), 0), COALESCE(SUM(fv.aug), 0), COALESCE(SUM(fv.sep), 0), "
            + "COALESCE(SUM(fv.oct), 0), COALESCE(SUM(fv.nov), 0), COALESCE(SUM(fv.dec), 0), "
            + "COALESCE(SUM(fv.jan), 0), COALESCE(SUM(fv.feb), 0), COALESCE(SUM(fv.mar), 0), "
            + "COALESCE(SUM(fv.apr), 0), COALESCE(SUM(fv.may), 0), COALESCE(SUM(fv.jun), 0), "
            + "COALESCE(SUM(fv.total), 0)) "
            + "FROM FreightVolumes fv "
            + "WHERE fv.budget = :budget AND fv.coacode IN :coacodes")
    MonthlySumResponseFreight getMonthlySumsByBudgetAndCoacodes(@Param("budget") Budget budget, @Param("coacodes") List<COA> coacodes);

    @Modifying
    @Query("DELETE FROM FreightVolumes f WHERE f.budget = :budget")
    int deleteByBudget(@Param("budget") Budget budget);

    /**
     * Return distinct COA codes for a given budget
     */
    @Query("""
        SELECT DISTINCT f.coacode
        FROM FreightVolumes f
        WHERE f.budget = :budget
    """)
    List<COA> findDistinctCoacodeByBudget(@Param("budget") Budget budget);
    
     @Query("""
        SELECT
            fb.coacode as coacode,
            fb.budget  as budget,

            SUM(fb.jul) as budgetJul,
            SUM(fb.aug) as budgetAug,
            SUM(fb.sep) as budgetSep,
            SUM(fb.oct) as budgetOct,
            SUM(fb.nov) as budgetNov,
            SUM(fb.dec) as budgetDec,

            SUM(fb.jan) as budgetJan,
            SUM(fb.feb) as budgetFeb,
            SUM(fb.mar) as budgetMar,
            SUM(fb.apr) as budgetApr,
            SUM(fb.may) as budgetMay,
            SUM(fb.jun) as budgetJun,

            SUM(fb.total) as budgetTotal
        FROM FreightVolumes fb
        WHERE fb.budget.id = :budgetId
          AND fb.coacode.display = :display
        GROUP BY fb.coacode, fb.budget
    """)
    List<Tuple> sumBudgetByCoa(
            @Param("budgetId") Long budgetId,
            @Param("display") Display display
    );
}
