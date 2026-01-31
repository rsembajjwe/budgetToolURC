package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.QtrReleaseCumulativeDto;
import com.methaltech.application.data.entity.bgtool.QtrReleases;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QtrReleasesRepository extends JpaRepository<QtrReleases, Long> {

    Optional<QtrReleases> findByOrganisationAndBudgetAndDeptSection(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    );

    List<QtrReleases> findByOrganisation(Organisation organisation);

    List<QtrReleases> findByBudget(Budget budget);

    List<QtrReleases> findByOrganisationAndBudget(Organisation organisation, Budget budget);

    List<QtrReleases> findByBudgetAndDeptSection(Budget budget, UrcDeptSectionAnlDimbgt deptSection);

    boolean existsByOrganisationAndBudgetAndDeptSection(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    );

    @Query("""
    SELECT
        COALESCE(SUM(q.qtr1Release), 0) AS q1Total,
        COALESCE(SUM(q.qtr1Release + q.qtr2Release), 0) AS q2Total,
        COALESCE(SUM(q.qtr1Release + q.qtr2Release + q.qtr3Release), 0) AS q3Total,
        COALESCE(SUM(q.qtr1Release + q.qtr2Release + q.qtr3Release + q.qtr4Release), 0) AS q4Total
    FROM QtrReleases q
    WHERE q.budget.id = :budgetId
      AND q.deptSection IN :deptSections
""")
    Tuple findCumulativeQuarterTotalsByBudget(
            @Param("budgetId") Long budgetId,
            @Param("deptSections") Set<UrcDeptSectionAnlDimbgt> deptSections
    );

    @Query("""
    SELECT
        COALESCE(SUM(q.qtr1Release), 0) AS q1Total,
        COALESCE(SUM(q.qtr1Release + q.qtr2Release), 0)  AS q2Total,
        COALESCE(SUM(q.qtr1Release + q.qtr2Release + q.qtr3Release), 0) AS q3Total,
        COALESCE(SUM(q.qtr1Release + q.qtr2Release + q.qtr3Release + q.qtr4Release), 0) AS q4Total
    FROM QtrReleases q
    WHERE q.budget.id = :budgetId
      AND q.deptSection = :deptSection
""")
    Tuple findCumulativeQuarterTotalsByBudget(
            @Param("budgetId") Long budgetId,
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection
    );

    @Query("""
        SELECT COALESCE(SUM(q.amount), 0)
        FROM QuarterlyActuals q
        WHERE q.period IN :periods
          AND q.activity = :activity
    """)
    BigDecimal sumAmountByPeriodsAndActivity(
            @Param("periods") Set<Integer> periods,
            @Param("activity") Urc_Activities activity
    );

}
