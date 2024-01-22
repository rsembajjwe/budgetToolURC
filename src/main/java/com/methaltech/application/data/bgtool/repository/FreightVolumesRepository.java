
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.FreightVolumes;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FreightVolumesRepository extends JpaRepository<FreightVolumes, Long> {
   List<FreightVolumes> findByBudgetAndCoacode(Budget budget, COA coacode);
@Query("SELECT COALESCE(SUM(COALESCE(fv.jan, 0) + COALESCE(fv.feb, 0) + COALESCE(fv.mar, 0) + COALESCE(fv.apr, 0) + " +
       "COALESCE(fv.may, 0) + COALESCE(fv.jun, 0) + COALESCE(fv.jul, 0) + COALESCE(fv.aug, 0) + " +
       "COALESCE(fv.sep, 0) + COALESCE(fv.oct, 0) + COALESCE(fv.nov, 0) + COALESCE(fv.dec, 0)), 0) " +
       "FROM FreightVolumes fv WHERE fv.budget = :budget AND fv.coacode = :coacode")
BigDecimal sumMonthsByBudgetAndCoacode(@Param("budget") Budget budget, @Param("coacode") COA coacode);
Long countByBudget(Budget budget);


    @Query("SELECT NEW com.methaltech.application.data.MonthlySumResponseFreight(" +
            "COALESCE(SUM(fv.jul), 0), COALESCE(SUM(fv.aug), 0), COALESCE(SUM(fv.sep), 0), " +
            "COALESCE(SUM(fv.oct), 0), COALESCE(SUM(fv.nov), 0), COALESCE(SUM(fv.dec), 0), " +
            "COALESCE(SUM(fv.jan), 0), COALESCE(SUM(fv.feb), 0), COALESCE(SUM(fv.mar), 0), " +
            "COALESCE(SUM(fv.apr), 0), COALESCE(SUM(fv.may), 0), COALESCE(SUM(fv.jun), 0), " +
            "COALESCE(SUM(fv.total), 0)) " +
            "FROM FreightVolumes fv " +
            "WHERE fv.budget = :budget AND fv.coacode = :coacode")
    MonthlySumResponseFreight getMonthlySumsByBudgetAndCoacode(@Param("budget") Budget budget, @Param("coacode") COA coacode);

   
}

