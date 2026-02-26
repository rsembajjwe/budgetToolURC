package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.BudgetItemsSummaryProjection;
import com.methaltech.application.data.Classification1;
import com.methaltech.application.data.Classification2;
import com.methaltech.application.data.Display;
import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.QuarterBudgetSum;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.views.procurementplan.CoaProcPlanDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetItemsRepository extends JpaRepository<BudgetItems, Long> {

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.coacode.code LIKE :start% "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit")
    List<BudgetItems> findBudgetItemsByCriteria(
            @Param("start") String start,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE (b.coacode.code LIKE '2%' OR b.coacode.code LIKE '3%') "
            + "AND b.activity = :activity ")
    List<BudgetItems> findBudgetItemsExpensesByActvity(
            @Param("activity") Urc_Activities activity
    );

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit "
            + "AND b.coacode = :coacode")
    List<BudgetItems> findBudgetItemsByCriteria2(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit")
    List<BudgetItems> findBudgetItemsByCriteria3(
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode "
            + "AND b.deptUnit IN :deptUnit")
    List<BudgetItems> findBudgetItemsByBudgetAndCoaAndSectios(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("deptUnit") Set<UrcDeptSectionAnlDimbgt> deptUnit
    );

    @Query("SELECT SUM(COALESCE(b.jul, 0) + COALESCE(b.nov, 0) + COALESCE(b.mar, 0) + COALESCE(b.aug, 0) + COALESCE(b.dec, 0) + COALESCE(b.apr, 0) + COALESCE(b.sep, 0) + COALESCE(b.jan, 0) + COALESCE(b.may, 0) + COALESCE(b.oct, 0) + COALESCE(b.feb, 0) + COALESCE(b.jun, 0)) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit")
    BigDecimal calculateSumOfAllMonths(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit "
            + "AND b.coacode = :coacode")
    BigDecimal sumMonthsByParameters(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit = :deptUnit "
            + "AND b.coacode = :coacode")
    BigDecimal sumMonthsByParameters2(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit")
    BigDecimal sumMonthsByActivityBySection(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budgetType IN (:budgetTypes) "
            + "AND b.budget = :budget "
            + "AND b.activity IN (:activities) "
            + "AND b.deptUnit IN (:deptUnits) "
            + "AND (b.coacode.code LIKE '2%' OR b.coacode.code LIKE '3%')")
    BigDecimal sumProgrammeSummation(
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("budget") Budget budget,
            @Param("activities") List<Urc_Activities> activities,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits
    );

    default boolean isSumProgrammeGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            List<Urc_Activities> activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits
    ) {
        BigDecimal sum = sumProgrammeSummation(budgetTypes, budget, activities, deptUnits);
        return sum.compareTo(BigDecimal.ZERO) > 0;
    }

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budgetType IN (:budgetTypes) "
            + "AND b.budget = :budget "
            + "AND b.activity IN (:activities) "
            + "AND b.deptUnit IN (:deptUnits) "
            + "AND b.coacode.code LIKE CONCAT(:sCode, '%')")
    BigDecimal sumProgrammeSummation(
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("budget") Budget budget,
            @Param("activities") List<Urc_Activities> activities,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("sCode") String sCode
    );

    default boolean isSumProgrammeGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            List<Urc_Activities> activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits, String sCode
    ) {
        BigDecimal sum = sumProgrammeSummation(budgetTypes, budget, activities, deptUnits, sCode);
        return sum.compareTo(BigDecimal.ZERO) > 0;
    }

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budgetType IN (:budgetTypes) "
            + "AND b.budget = :budget "
            + "AND b.activity = :activities "
            + "AND b.deptUnit IN (:deptUnits) "
            + "AND (b.coacode.code LIKE '2%' OR b.coacode.code LIKE '3%')")
    BigDecimal sumActvitySummation(
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("budget") Budget budget,
            @Param("activities") Urc_Activities activities,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits
    );

    default boolean isSumActvityGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            Urc_Activities activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits
    ) {
        BigDecimal sum = sumActvitySummation(budgetTypes, budget, activities, deptUnits);
        return sum.compareTo(BigDecimal.ZERO) > 0;
    }

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budgetType IN (:budgetTypes) "
            + "AND b.budget = :budget "
            + "AND b.activity = :activities "
            + "AND b.deptUnit IN (:deptUnits) "
            + "AND b.coacode.code LIKE CONCAT(:sCode, '%')")
    BigDecimal sumActvitySummation(
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("budget") Budget budget,
            @Param("activities") Urc_Activities activities,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("sCode") String sCode
    );

    default boolean isSumActvityGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            Urc_Activities activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits, String sCode
    ) {
        BigDecimal sum = sumActvitySummation(budgetTypes, budget, activities, deptUnits, sCode);
        return sum.compareTo(BigDecimal.ZERO) > 0;
    }

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit = :deptUnit")
    BigDecimal sumMonthsByActivityBySection2(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE (b.coalevel1 = :coalevel1 AND (b.coalevel1.code = 2 OR b.coalevel1.code = 3)) "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit")
    BigDecimal sumMonthsByActivityBySection3(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnit")
    BigDecimal sumMonthsByActivityBySection4(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit = :deptUnit")
    BigDecimal sumMonthsBySection4(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE (b.coalevel1.code = 2 OR b.coalevel1.code = 3) "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit = :deptUnit")
    BigDecimal sumMonthsBySectionTotalBudget(
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit
    );

    @Query("""
select new com.methaltech.application.data.entity.bgtool.QuarterBudgetSum(
       bi.deptUnit.id,
       coalesce(sum(coalesce(bi.jul,0) + coalesce(bi.aug,0) + coalesce(bi.sep,0)),0),
       coalesce(sum(coalesce(bi.oct,0) + coalesce(bi.nov,0) + coalesce(bi.dec,0)),0),
       coalesce(sum(coalesce(bi.jan,0) + coalesce(bi.feb,0) + coalesce(bi.mar,0)),0),
       coalesce(sum(coalesce(bi.apr,0) + coalesce(bi.may,0) + coalesce(bi.jun,0)),0)
)
from BudgetItems bi
where bi.budget = :budget
  and (bi.coalevel1.code = 2 OR bi.coalevel1.code = 3)
  and bi.budgetType = :org
  and bi.deptUnit = :deptUnit
group by bi.deptUnit.id
""")
    List<QuarterBudgetSum> sumQuarterBudgetsByDept(@Param("budget") Budget budget,
            @Param("org") Organisation org,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit);

    @Modifying
    @Query("DELETE FROM BudgetItems b WHERE b = :budgetItem")
    void deleteBudgetItem(@Param("budgetItem") BudgetItems budgetItem);

    // Define a custom query to select distinct coacode values by budgetType, Budget, UrcDeptSectionAnlDimbgt, and Coalevel1
    // Define a custom query to select distinct coacode values by budgetType, Budget, UrcDeptSectionAnlDimbgt, Coalevel1, and activity
    @Query("SELECT DISTINCT b.coacode FROM BudgetItems b "
            + "WHERE b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit = :deptUnit "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.activity = :activity")
    List<COA> findDistinctCoacodesByCriteria(
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("activity") Urc_Activities activity
    );

    @Query("SELECT DISTINCT b.coacode FROM BudgetItems b "
            + "WHERE b.budgetType IN :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit IN :deptUnit "
            + "AND b.coalevel1 = :coalevel1 ")
    List<COA> findDistinctCoacodesByCriteria(
            @Param("budgetType") Set<Organisation> budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") Set<UrcDeptSectionAnlDimbgt> deptUnit,
            @Param("coalevel1") Coalevel1 coalevel1
    );

    @Query("SELECT DISTINCT b.coacode FROM BudgetItems b "
            + "WHERE b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit = :deptUnit "
            + "AND b.coalevel1 = :coalevel1 ")
    List<COA> findDistinctCoacodesByCriteria2(
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coalevel1") Coalevel1 coalevel1
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.activity = :activity " // Add a space before AND
            + "AND b.deptUnit = :deptUnit "
            + "AND b.coacode = :coacode")
    BigDecimal sumMonthsByOrgAndBudgetAndSection(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType IN :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit IN :deptUnit "
            + "AND b.coacode = :coacode")
    BigDecimal sumMonthsByOrgAndBudgetAndSection(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Set<Organisation> budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") Set<UrcDeptSectionAnlDimbgt> deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT b "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType IN :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit IN :deptUnit "
            + "AND b.coacode = :coacode")
    List<BudgetItems> findByOrgAndBudgetAndSection3(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Set<Organisation> budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") Set<UrcDeptSectionAnlDimbgt> deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.coalevel1 = :coalevel1 "
            + "AND b.budgetType = :budgetType "
            + "AND b.budget = :budget "
            + "AND b.deptUnit = :deptUnit "
            + "AND b.coacode = :coacode")
    BigDecimal sumMonthsByOrgAndBudgetAndSection2(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE  b.budget = :budget "
            + "AND b.coacode = :coacode")
    BigDecimal sumMonthsBudgetAndCode(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode
    );

    BudgetItems findByAnalcode(Long analcode);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode")
    BudgetItems findBudgetAndCode(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode
    );

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode "
            + "AND b.analcode = :analcode")
    BudgetItems findBudgetAndCodeAndAnalcode(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("analcode") Long analcode
    );

    @Query("SELECT "
            + "SUM(b.jan), "
            + "SUM(b.feb), "
            + "SUM(b.mar), "
            + "SUM(b.apr), "
            + "SUM(b.may), "
            + "SUM(b.jun), "
            + "SUM(b.jul), "
            + "SUM(b.aug), "
            + "SUM(b.sep), "
            + "SUM(b.oct), "
            + "SUM(b.nov), "
            + "SUM(b.dec) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode")
    List<Object[]> sumIndividualMonthsBudgetAndCode(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode
    );

    @Query("SELECT bi FROM BudgetItems bi "
            + "WHERE (:coalevel1 IS NULL OR bi.coalevel1 = :coalevel1) "
            + "AND (:budgetType IS NULL OR bi.budgetType = :budgetType) "
            + "AND (:budget IS NULL OR bi.budget = :budget) "
            + "AND (:activity IS NULL OR bi.activity = :activity) "
            + "AND (:deptUnit IS NULL OR bi.deptUnit = :deptUnit) "
            + "AND (:coacode IS NULL OR bi.coacode = :coacode)")
    List<BudgetItems> findByCriteria(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT bi FROM BudgetItems bi "
            + "WHERE bi.coalevel1 = :coalevel1 "
            + "AND bi.budgetType = :budgetType "
            + "AND bi.budget = :budget "
            + "AND bi.deptUnit = :deptUnit "
            + "AND bi.coacode = :coacode")
    List<BudgetItems> findByCriteria2(
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budgetType") Organisation budgetType,
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("coacode") COA coacode
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnits(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.deptUnit = :deptUnits")
    BigDecimal calculateTotalByBudgetAndDeptUnits(
            @Param("budget") Budget budget,
            @Param("deptUnits") UrcDeptSectionAnlDimbgt deptUnits
    );

    @Query("SELECT COALESCE(SUM(b.jul + b.aug + b.sep ), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsQtr1(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits
    );

    @Query("SELECT COALESCE(SUM( b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsQtr2(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsQtr3(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits
    );

    @Query("SELECT COALESCE(SUM(b.apr + b.may + b.jun), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsQtr4(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.budgetType IN :budgetType "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsAndBudgetTypes(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetType") Set<Organisation> budgetType
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.budgetType = :budgetType "
            + "AND b.coacode.code LIKE '1%' "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByIncome(
            @Param("budget") Budget budget,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetType") Organisation budgetType
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.budgetType = :budgetType "
            + "AND (b.coacode.code LIKE '2%' OR b.coacode.code LIKE '3%') "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndDeptUnitsAndBudgetTypesByExp(
            @Param("budget") Budget budget,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetType") Organisation budgetType
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode.code LIKE '1%' "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndDeptUnitsByIncome(
            @Param("budget") Budget budget,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits
    );

    @Query("SELECT COALESCE(SUM(b.jul + b.aug + b.sep ), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.budgetType IN :budgetType "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsAndBudgetTypesQtr1(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetType") Set<Organisation> budgetType
    );

    @Query("SELECT COALESCE(SUM( b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.budgetType IN :budgetType "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsAndBudgetTypesQtr2(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetType") Set<Organisation> budgetType
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar ), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.budgetType IN :budgetType "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsAndBudgetTypesQtr3(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetType") Set<Organisation> budgetType
    );

    @Query("SELECT COALESCE(SUM(b.apr + b.may + b.jun), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.budgetType IN :budgetType "
            + "AND b.activity = :activity "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal calculateTotalByBudgetAndActivityAndDeptUnitsAndBudgetTypesQtr4(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetType") Set<Organisation> budgetType
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode "
            + "AND b.activity = :activity "
            + "AND b.deptUnit = :deptUnits")
    BigDecimal calculateTotalByBudgetAndCoaAndActivityAndSection(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("activity") Urc_Activities activity,
            @Param("deptUnits") UrcDeptSectionAnlDimbgt deptUnits
    );

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode "
            + "AND b.deptUnit = :deptUnits")
    BigDecimal calculateTotalByBudgetAndCoaAndSection(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("deptUnits") UrcDeptSectionAnlDimbgt deptUnits
    );

    @Query("""
    SELECT COALESCE(SUM(
        b.jan + b.feb + b.mar + b.apr + b.may + b.jun +
        b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0)
    FROM BudgetItems b
    WHERE b.budget = :budget
      AND b.coalevel1.id IN (2, 3)
           
""")
    BigDecimal calculateTotalExpenditure(
            @Param("budget") Budget budget
    );

    @Query("""
    SELECT COALESCE(SUM(
        b.jan + b.feb + b.mar + b.apr + b.may + b.jun +
        b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0)
    FROM BudgetItems b
    WHERE b.budget = :budget
      AND b.coalevel1.id IN (2, 3)
           AND b.deptUnit IN :deptUnits
""")
    BigDecimal calculateTotalDeptExpenditure(
            @Param("budget") Budget budget,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits
    );

    @Query("""
    SELECT COALESCE(SUM(
        b.jan + b.feb + b.mar + b.apr + b.may + b.jun +
        b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0)
    FROM BudgetItems b
    WHERE b.budget = :budget
      AND b.coalevel1.id IN (2, 3)
           AND b.deptUnit = :deptUnits
""")
    BigDecimal calculateTotalDeptExpenditure(
            @Param("budget") Budget budget,
            @Param("deptUnits") UrcDeptSectionAnlDimbgt deptUnits
    );

    @Query("""
    SELECT COALESCE(SUM(
        b.jan + b.feb + b.mar + b.apr + b.may + b.jun +
        b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0)
    FROM BudgetItems b
    WHERE b.budget = :budget
      AND b.coalevel1.id = 1
""")
    BigDecimal calculateTotalIncome(
            @Param("budget") Budget budget
    );

    boolean existsByCoacodeAndBudget(COA coacode, Budget budget);

    @Transactional
    void deleteByCoacodeAndBudget(COA coacode, Budget budget);

    @Query("SELECT b FROM BudgetItems b WHERE b.coacode = :coacode AND b.budget = :budget")
    Page<BudgetItems> findFirst1ByCoacodeAndBudget(@Param("coacode") COA coacode, @Param("budget") Budget budget, Pageable pageable);

    // Find BudgetItems by deptUnit and budget
    List<BudgetItems> findByDeptUnitAndBudget(UrcDeptSectionAnlDimbgt deptUnit, Budget budget);

    @Query("SELECT COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    BigDecimal findSumByBudgetCoalevel1AndDeptUnits(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 IN :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    BigDecimal findSumByBudgetCoalevel1AndDeptUnitsTotal(
            @Param("budget") Budget budget,
            @Param("coalevel1") List<Coalevel1> coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode ")
    BigDecimal findSumByBudgetCOA(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode);

    @Query("SELECT CASE WHEN COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) > 0 "
            + "THEN true ELSE false END "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    boolean isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT CASE WHEN COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) > 0 "
            + "THEN true ELSE false END "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    boolean isSumBudgetDeptUnitsGreaterThanZero(
            @Param("budget") Budget budget,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    List<BudgetItems> findByBudgetCoalevel1AndDeptUnits(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    List<BudgetItems> findByBudgetCoacodeAndDeptUnits(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + "
            + "b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    BigDecimal findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.activity IN :activities "
            + "AND b.budgetType IN :budgetTypes")
    List<BudgetItems> findByBudgetCoalevel1AndDeptUnitsAndActivities(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("activities") List<Urc_Activities> activities,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT CASE WHEN COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) > 0 "
            + "THEN true ELSE false END "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.activity IN :activities "
            + "AND b.budgetType IN :budgetTypes")
    boolean isSumBudgetCoalevel1AndDeptUnitsAndActivitiesGreaterThanZero(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("activities") List<Urc_Activities> activities,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT CASE WHEN COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) > 0 "
            + "THEN true ELSE false END "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.activity = :activity "
            + "AND b.budgetType IN :budgetTypes")
    boolean isSumBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("activity") Urc_Activities activity,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.activity = :activity "
            + "AND b.budgetType IN :budgetTypes")
    List<BudgetItems> findBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("activity") Urc_Activities activity,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT COALESCE(SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + "
            + "b.jan + b.feb + b.mar + b.apr + b.may + b.jun), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.activity = :activity "
            + "AND b.budgetType IN :budgetTypes")
    BigDecimal findSumByBudgetCoalevel1AndDeptUnitsAndActivity(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("activity") Urc_Activities activity,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT SUM(b.jul) as julSum, "
            + "SUM(b.aug) as augSum, "
            + "SUM(b.sep) as sepSum, "
            + "SUM(b.oct) as octSum, "
            + "SUM(b.nov) as novSum, "
            + "SUM(b.dec) as decSum, "
            + "SUM(b.jan) as janSum, "
            + "SUM(b.feb) as febSum, "
            + "SUM(b.mar) as marSum, "
            + "SUM(b.apr) as aprSum, "
            + "SUM(b.may) as maySum, "
            + "SUM(b.jun) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.activity = :activity "
            + "AND b.budgetType IN :budgetTypes")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetCoalevel1AndDeptUnitsAndActivity(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("activity") Urc_Activities activity,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT SUM(b.jul) as julSum, "
            + "SUM(b.aug) as augSum, "
            + "SUM(b.sep) as sepSum, "
            + "SUM(b.oct) as octSum, "
            + "SUM(b.nov) as novSum, "
            + "SUM(b.dec) as decSum, "
            + "SUM(b.jan) as janSum, "
            + "SUM(b.feb) as febSum, "
            + "SUM(b.mar) as marSum, "
            + "SUM(b.apr) as aprSum, "
            + "SUM(b.may) as maySum, "
            + "SUM(b.jun) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetCoalevel1AndDeptUnits(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT SUM(b.jul) as julSum, "
            + "SUM(b.aug) as augSum, "
            + "SUM(b.sep) as sepSum, "
            + "SUM(b.oct) as octSum, "
            + "SUM(b.nov) as novSum, "
            + "SUM(b.dec) as decSum, "
            + "SUM(b.jan) as janSum, "
            + "SUM(b.feb) as febSum, "
            + "SUM(b.mar) as marSum, "
            + "SUM(b.apr) as aprSum, "
            + "SUM(b.may) as maySum, "
            + "SUM(b.jun) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 IN :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetCoalevel1AndDeptUnitsTotal(
            @Param("budget") Budget budget,
            @Param("coalevel1") List<Coalevel1> coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT SUM(b.jul) as julSum, "
            + "SUM(b.aug) as augSum, "
            + "SUM(b.sep) as sepSum, "
            + "SUM(b.oct) as octSum, "
            + "SUM(b.nov) as novSum, "
            + "SUM(b.dec) as decSum, "
            + "SUM(b.jan) as janSum, "
            + "SUM(b.feb) as febSum, "
            + "SUM(b.mar) as marSum, "
            + "SUM(b.apr) as aprSum, "
            + "SUM(b.may) as maySum, "
            + "SUM(b.jun) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode = :coacode "
            + // Change is here
            "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode, // Change is here
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    public static interface MonthlySumResult {

        BigDecimal getJulSum();

        BigDecimal getAugSum();

        BigDecimal getSepSum();

        BigDecimal getOctSum();

        BigDecimal getNovSum();

        BigDecimal getDecSum();

        BigDecimal getJanSum();

        BigDecimal getFebSum();

        BigDecimal getMarSum();

        BigDecimal getAprSum();

        BigDecimal getMaySum();

        BigDecimal getJunSum();
    }

    @Query("SELECT DISTINCT b.coacode "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1 = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.budgetType IN :budgetTypes")
    List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(
            @Param("budget") Budget budget,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("deptUnits") List<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    void deleteByAnalcode(Long analcode);

    @Query("SELECT SUM(b.jul) as julSum, "
            + "SUM(b.aug) as augSum, "
            + "SUM(b.sep) as sepSum, "
            + "SUM(b.oct) as octSum, "
            + "SUM(b.nov) as novSum, "
            + "SUM(b.dec) as decSum, "
            + "SUM(b.jan) as janSum, "
            + "SUM(b.feb) as febSum, "
            + "SUM(b.mar) as marSum, "
            + "SUM(b.apr) as aprSum, "
            + "SUM(b.may) as maySum, "
            + "SUM(b.jun) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coalevel1.code = :coalevel1 "
            + "AND b.deptUnit IN :deptUnits "
            + "AND b.activity = :activity "
            + "AND b.budgetType IN :budgetTypes")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetCoalevel1Activity(
            @Param("budget") Budget budget,
            @Param("coalevel1") Integer coalevel1,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("activity") Urc_Activities activity,
            @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("SELECT SUM(b.jul) as julSum, "
            + "SUM(b.aug) as augSum, "
            + "SUM(b.sep) as sepSum, "
            + "SUM(b.oct) as octSum, "
            + "SUM(b.nov) as novSum, "
            + "SUM(b.dec) as decSum, "
            + "SUM(b.jan) as janSum, "
            + "SUM(b.feb) as febSum, "
            + "SUM(b.mar) as marSum, "
            + "SUM(b.apr) as aprSum, "
            + "SUM(b.may) as maySum, "
            + "SUM(b.jun) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode IN :coacode")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetCoa(
            @Param("budget") Budget budget,
            @Param("coacode") List<COA> coacode);

    @Query("""
    SELECT COALESCE(
        SUM(
            COALESCE(b.jul,0) + COALESCE(b.aug,0) + COALESCE(b.sep,0) +
            COALESCE(b.oct,0) + COALESCE(b.nov,0) + COALESCE(b.dec,0) +
            COALESCE(b.jan,0) + COALESCE(b.feb,0) + COALESCE(b.mar,0) +
            COALESCE(b.apr,0) + COALESCE(b.may,0) + COALESCE(b.jun,0)
        ), 0
    )
    FROM BudgetItems b
    WHERE b.budget = :budget
    AND b.coacode IN :coacode
""")
    BigDecimal findSumOfTotalMonthsByBudgetCoa(
            @Param("budget") Budget budget,
            @Param("coacode") List<COA> coacode);

    @Query("""
    SELECT COALESCE(
        SUM(
            COALESCE(b.jul,0) + COALESCE(b.aug,0) + COALESCE(b.sep,0) +
            COALESCE(b.oct,0) + COALESCE(b.nov,0) + COALESCE(b.dec,0) +
            COALESCE(b.jan,0) + COALESCE(b.feb,0) + COALESCE(b.mar,0) +
            COALESCE(b.apr,0) + COALESCE(b.may,0) + COALESCE(b.jun,0)
        ), 0
    )
    FROM BudgetItems b
    WHERE b.budget = :budget
    AND b.coacode = :coacode
""")
    BigDecimal findSumOfTotalMonthsByBudgetCoa(
            @Param("budget") Budget budget,
            @Param("coacode") COA coacode);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budgetType IN (:budgetTypes) "
            + "AND b.budget = :budget "
            + "AND b.activity = :activities "
            + "AND b.deptUnit IN (:deptUnits) "
            + "AND b.coalevel1.code = :coalevel1Code")
    List<BudgetItems> findBudgetItemsByUrc_Activities(
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("budget") Budget budget,
            @Param("activities") Urc_Activities activities,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("coalevel1Code") Integer coalevel1Code
    );

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.activity = :activities ")
    List<BudgetItems> findBudgetItemsByUrc_Activities(
            @Param("activities") Urc_Activities activities
    );

    List<BudgetItems> findByBudgetAndCoacode(Budget budget, COA coacode);

    // Custom method to find BudgetItems by Budget and Set of COA codes
    List<BudgetItems> findByBudgetAndCoacodeIn(Budget budget, Set<COA> coacodes);

    @Query("SELECT bi FROM BudgetItems bi WHERE bi.coacode.code LIKE '2%' OR bi.coacode.code LIKE '3%'")
    List<BudgetItems> findByCoacodeCodeStartingWith2Or3();

    @Query("SELECT COALESCE(SUM(b.jan), 0) + COALESCE(SUM(b.feb), 0) + COALESCE(SUM(b.mar), 0) + "
            + "COALESCE(SUM(b.apr), 0) + COALESCE(SUM(b.may), 0) + COALESCE(SUM(b.jun), 0) + "
            + "COALESCE(SUM(b.jul), 0) + COALESCE(SUM(b.aug), 0) + COALESCE(SUM(b.sep), 0) + "
            + "COALESCE(SUM(b.oct), 0) + COALESCE(SUM(b.nov), 0) + COALESCE(SUM(b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode = :coacode")
    BigDecimal sumOfAllMonthsByBudgetAndProcClassAndCoa(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode);

    @Query("SELECT COALESCE(SUM(b.jan), 0) + COALESCE(SUM(b.feb), 0) + COALESCE(SUM(b.mar), 0) + "
            + "COALESCE(SUM(b.apr), 0) + COALESCE(SUM(b.may), 0) + COALESCE(SUM(b.jun), 0) + "
            + "COALESCE(SUM(b.jul), 0) + COALESCE(SUM(b.aug), 0) + COALESCE(SUM(b.sep), 0) + "
            + "COALESCE(SUM(b.oct), 0) + COALESCE(SUM(b.nov), 0) + COALESCE(SUM(b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode = :coacode "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal sumOfAllMonthsByBudgetAndProcClassAndCoa(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT COALESCE(SUM(b.jan), 0) + COALESCE(SUM(b.feb), 0) + COALESCE(SUM(b.mar), 0) + "
            + "COALESCE(SUM(b.apr), 0) + COALESCE(SUM(b.may), 0) + COALESCE(SUM(b.jun), 0) + "
            + "COALESCE(SUM(b.jul), 0) + COALESCE(SUM(b.aug), 0) + COALESCE(SUM(b.sep), 0) + "
            + "COALESCE(SUM(b.oct), 0) + COALESCE(SUM(b.nov), 0) + COALESCE(SUM(b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.coacode = :coacode "
            + "AND b.deptUnit IN :deptUnits")
    BigDecimal sumOfAllMonthsByBudgetAndCoa(@Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT COALESCE(SUM(b.jan), 0) + COALESCE(SUM(b.feb), 0) + COALESCE(SUM(b.mar), 0) + "
            + "COALESCE(SUM(b.apr), 0) + COALESCE(SUM(b.may), 0) + COALESCE(SUM(b.jun), 0) + "
            + "COALESCE(SUM(b.jul), 0) + COALESCE(SUM(b.aug), 0) + COALESCE(SUM(b.sep), 0) + "
            + "COALESCE(SUM(b.oct), 0) + COALESCE(SUM(b.nov), 0) + COALESCE(SUM(b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.coacode = :coacode ")
    BigDecimal sumOfAllMonthsByBudgetAndCoa(@Param("budget") Budget budget, @Param("coacode") COA coacode);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode = :coacode")
    List<BudgetItems> findByBudgetAndProcClassAndCoa(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode = :coacode AND b.deptUnit IN :deptUnits")
    List<BudgetItems> findByBudgetAndProcClassAndCoaAndDeptUnitIn(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode = :coacode AND b.deptUnit IN :deptUnits")
    List<BudgetItems> findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsource(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode IN :coacode AND b.deptUnit IN :deptUnits")
    List<BudgetItems> findByBudgetAndProcClassAndCoaAndDeptUnitIn(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") Set<COA> coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode IN :coacode")
    List<BudgetItems> findByBudgetAndProcClassAndCoa(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") Set<COA> coacode);

    @Query("SELECT DISTINCT f.fundsource FROM BudgetItems bi "
            + "JOIN bi.fundsource f "
            + "WHERE bi.budget = :budget "
            + "AND bi.procClass = :procClass "
            + "AND bi.coacode = :coacode")
    Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacode(Budget budget, ProcClass procClass, COA coacode);

    @Query("SELECT DISTINCT f.fundsource FROM BudgetItems bi "
            + "JOIN bi.fundsource f "
            + "WHERE bi.budget = :budget "
            + "AND bi.procClass = :procClass "
            + "AND bi.coacode = :coacode  AND bi.deptUnit IN :deptUnits")
    Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacodeAndDeptIn(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT DISTINCT f.fundsource FROM BudgetItems bi "
            + "JOIN bi.fundsource f "
            + "WHERE bi.budget = :budget "
            + "AND bi.procClass = :procClass "
            + "AND bi.coacode = :coacode  AND bi.deptUnit IN :deptUnits AND f IN :fundsource")
    Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacodeAndDeptInAndFundsourceIn(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("fundsource") Set<Fundsource> fundsource);

    @Query("SELECT DISTINCT f.fundsource FROM BudgetItems bi "
            + "JOIN bi.fundsource f "
            + "WHERE bi.budget = :budget "
            + "AND bi.procClass = :procClass "
            + "AND bi.coacode = :coacode AND f IN :fundsource")
    Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacodeAndFundsourceIn(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("fundsource") Set<Fundsource> fundsource);

    @Query("SELECT DISTINCT f FROM BudgetItems bi "
            + "JOIN bi.fundsource f "
            + "WHERE bi.budget = :budget "
            + "AND bi.procClass = :procClass "
            + "AND bi.coacode = :coacode")
    Set<Fundsource> findDistinctFundSources2ByBudgetAndProcClassAndCoacode(Budget budget, ProcClass procClass, COA coacode);

    @Query("SELECT bi FROM BudgetItems bi "
            + "JOIN bi.fundsource fs "
            + "WHERE bi.budget = :budget "
            + "AND bi.procClass = :procClass "
            + "AND bi.coacode = :coacode "
            + "AND fs IN :fundsource")
    List<BudgetItems> findByBudgetAndProcClassAndCoaAndFundsourceIn(@Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("fundsource") Set<Fundsource> fundsource);

    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.procClass = :procClass AND b.coacode = :coacode AND b.deptUnit IN :deptUnits AND b.fundsource IN :fundsource")
    List<BudgetItems> findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(
            @Param("budget") Budget budget,
            @Param("procClass") ProcClass procClass,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("fundsource") Set<Fundsource> fundsource);

    @Query("SELECT DISTINCT b.coacode FROM BudgetItems b WHERE b.budget = :budget")
    List<COA> findDistinctCoacodeByBudget(@Param("budget") Budget budget);

    @Query("SELECT DISTINCT b.coacode FROM BudgetItems b WHERE b.budget = :budget AND b.deptUnit IN :deptUnits")
    List<COA> findDistinctCoacodeByBudgetAndDeptUnitIn(@Param("budget") Budget budget, @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT COALESCE(SUM(b.jul), 0) as julSum, "
            + "COALESCE(SUM(b.aug), 0) as augSum, "
            + "COALESCE(SUM(b.sep), 0) as sepSum, "
            + "COALESCE(SUM(b.oct), 0) as octSum, "
            + "COALESCE(SUM(b.nov), 0) as novSum, "
            + "COALESCE(SUM(b.dec), 0) as decSum, "
            + "COALESCE(SUM(b.jan), 0) as janSum, "
            + "COALESCE(SUM(b.feb), 0) as febSum, "
            + "COALESCE(SUM(b.mar), 0) as marSum, "
            + "COALESCE(SUM(b.apr), 0) as aprSum, "
            + "COALESCE(SUM(b.may), 0) as maySum, "
            + "COALESCE(SUM(b.jun), 0) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.coacode = :coacode "
            + "AND b.deptUnit IN :deptUnits")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetCoaDept(@Param("budget") Budget budget,
            @Param("coacode") COA coacode,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits);

    @Query("SELECT NEW com.methaltech.application.data.MonthlySumResponseFreight("
            + "COALESCE(SUM(fv.jul), 0), COALESCE(SUM(fv.aug), 0), COALESCE(SUM(fv.sep), 0), "
            + "COALESCE(SUM(fv.oct), 0), COALESCE(SUM(fv.nov), 0), COALESCE(SUM(fv.dec), 0), "
            + "COALESCE(SUM(fv.jan), 0), COALESCE(SUM(fv.feb), 0), COALESCE(SUM(fv.mar), 0), "
            + "COALESCE(SUM(fv.apr), 0), COALESCE(SUM(fv.may), 0), COALESCE(SUM(fv.jun), 0), "
            + "COALESCE((SUM(fv.jul)+SUM(fv.aug)+SUM(fv.sep)+SUM(fv.oct)+SUM(fv.nov)+SUM(fv.dec)+SUM(fv.jan)+SUM(fv.feb)+SUM(fv.mar)+SUM(fv.apr)+SUM(fv.may)+SUM(fv.jun)), 0)) "
            + "FROM BudgetItems fv "
            + "WHERE fv.budget = :budget AND fv.coacode = :coacode")
    MonthlySumResponseFreight getMonthlySumsByBudgetAndCoacode(@Param("budget") Budget budget, @Param("coacode") COA coacode);

    @Query("SELECT NEW com.methaltech.application.data.MonthlySumResponseFreight("
            + "COALESCE(SUM(fv.jul), 0), COALESCE(SUM(fv.aug), 0), COALESCE(SUM(fv.sep), 0), "
            + "COALESCE(SUM(fv.oct), 0), COALESCE(SUM(fv.nov), 0), COALESCE(SUM(fv.dec), 0), "
            + "COALESCE(SUM(fv.jan), 0), COALESCE(SUM(fv.feb), 0), COALESCE(SUM(fv.mar), 0), "
            + "COALESCE(SUM(fv.apr), 0), COALESCE(SUM(fv.may), 0), COALESCE(SUM(fv.jun), 0), "
            + "COALESCE((SUM(fv.jul)+SUM(fv.aug)+SUM(fv.sep)+SUM(fv.oct)+SUM(fv.nov)+SUM(fv.dec)+SUM(fv.jan)+SUM(fv.feb)+SUM(fv.mar)+SUM(fv.apr)+SUM(fv.may)+SUM(fv.jun)), 0)) "
            + "FROM BudgetItems fv "
            + "WHERE fv.budget = :budget AND fv.coacode IN :coacode")
    MonthlySumResponseFreight getMonthlySumsByBudgetAndCoacode(@Param("budget") Budget budget, @Param("coacode") List<COA> coacode);

    @Query("SELECT NEW com.methaltech.application.data.MonthlySumResponseFreight("
            + "COALESCE(SUM(fv.jul), 0), COALESCE(SUM(fv.aug), 0), COALESCE(SUM(fv.sep), 0), "
            + "COALESCE(SUM(fv.oct), 0), COALESCE(SUM(fv.nov), 0), COALESCE(SUM(fv.dec), 0), "
            + "COALESCE(SUM(fv.jan), 0), COALESCE(SUM(fv.feb), 0), COALESCE(SUM(fv.mar), 0), "
            + "COALESCE(SUM(fv.apr), 0), COALESCE(SUM(fv.may), 0), COALESCE(SUM(fv.jun), 0), "
            + "COALESCE((SUM(fv.jul)+SUM(fv.aug)+SUM(fv.sep)+SUM(fv.oct)+SUM(fv.nov)+SUM(fv.dec)+SUM(fv.jan)+SUM(fv.feb)+SUM(fv.mar)+SUM(fv.apr)+SUM(fv.may)+SUM(fv.jun)), 0)) "
            + "FROM BudgetItems fv "
            + "WHERE fv.budget = :budget AND fv.coacode IN :coacodes")
    MonthlySumResponseFreight getMonthlySumsByBudgetAndCoacodes(@Param("budget") Budget budget, @Param("coacodes") List<COA> coacodes);

    @Query("SELECT "
            + "COALESCE(SUM(b.jul), 0) as julSum, "
            + "COALESCE(SUM(b.aug), 0) as augSum, "
            + "COALESCE(SUM(b.sep), 0) as sepSum, "
            + "COALESCE(SUM(b.oct), 0) as octSum, "
            + "COALESCE(SUM(b.nov), 0) as novSum, "
            + "COALESCE(SUM(b.dec), 0) as decSum, "
            + "COALESCE(SUM(b.jan), 0) as janSum, "
            + "COALESCE(SUM(b.feb), 0) as febSum, "
            + "COALESCE(SUM(b.mar), 0) as marSum, "
            + "COALESCE(SUM(b.apr), 0) as aprSum, "
            + "COALESCE(SUM(b.may), 0) as maySum, "
            + "COALESCE(SUM(b.jun), 0) as junSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode.display = :display")
    List<MonthlySumResult> findSumOfIndividualMonthsByBudgetAndCoacodeFreight(@Param("budget") Budget budget,
            @Param("display") Display display);

    List<BudgetItems> findByBudgetAndCoalevel1(Budget budget, Coalevel1 coalevel1);

    @Query("SELECT DISTINCT b.coacode FROM BudgetItems b WHERE b.budget = :budget AND b.coacode IS NOT NULL AND b.coacode.code NOT LIKE '1%'")
    List<COA> findDistinctCoacodesByBudget(Budget budget);

    @Query("SELECT DISTINCT b.fundsource FROM BudgetItems b WHERE b.budget = :budget AND b.coacode = :coa AND b.fundsource IS NOT NULL")
    List<Fundsource> findDistinctFundsourceByBudgetAndCoacode(Budget budget, COA coa);

    @Query("""
    SELECT COALESCE(SUM(b.cost), 0)
    FROM BudgetItems b
    WHERE b.budget = :budget AND b.coacode = :coa
""")
    BigDecimal sumCostByBudgetAndCoacode(Budget budget, COA coa);

    @Query("""
    SELECT new com.methaltech.application.views.procurementplan.CoaProcPlanDTO(
        b.budget.id,
        c.id,
        c.name,
        SUM(
            COALESCE(b.jan, 0) + COALESCE(b.feb, 0) + COALESCE(b.mar, 0) +
            COALESCE(b.apr, 0) + COALESCE(b.may, 0) + COALESCE(b.jun, 0) +
            COALESCE(b.jul, 0) + COALESCE(b.aug, 0) + COALESCE(b.sep, 0) +
            COALESCE(b.oct, 0) + COALESCE(b.nov, 0) + COALESCE(b.dec, 0)
        )
    )
    FROM BudgetItems b
    JOIN b.coacode c
    WHERE b.budget.id = :budgetId
      AND c.code NOT LIKE '1%'
    GROUP BY  b.budget.id, c.id, c.name
    HAVING SUM(
        COALESCE(b.jan, 0) + COALESCE(b.feb, 0) + COALESCE(b.mar, 0) +
        COALESCE(b.apr, 0) + COALESCE(b.may, 0) + COALESCE(b.jun, 0) +
        COALESCE(b.jul, 0) + COALESCE(b.aug, 0) + COALESCE(b.sep, 0) +
        COALESCE(b.oct, 0) + COALESCE(b.nov, 0) + COALESCE(b.dec, 0)
    ) > 0
""")
    List<CoaProcPlanDTO> fetchGroupedBudgetItemsByBudget(@Param("budgetId") Long budgetId);

    // Find by activity
    //List<BudgetItems> findByActivityOrderByItemAsc(Urc_Activities activity);
    @Query("SELECT b FROM BudgetItems b "
            + "WHERE b.activity = :activity "
            + "ORDER BY b.item ASC")
    List<BudgetItems> findItemsByActivity(@Param("activity") Urc_Activities activity);

    // Find by activity and COA
    List<BudgetItems> findByActivityAndCoacodeOrderByItemAsc(Urc_Activities activity, COA coa);

    // Find by COA
    List<BudgetItems> findByCoacodeOrderByItemAsc(COA coa);

    // Find by budget
    List<BudgetItems> findByBudgetOrderByItemAsc(Budget budget);

    // Find by category
    List<BudgetItems> findByCategoryContainingIgnoreCaseOrderByItemAsc(String category);

    // Search budget items
    @Query("SELECT bi FROM BudgetItems bi WHERE "
            + "LOWER(bi.item) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(bi.product) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(bi.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "ORDER BY bi.item ASC")
    Page<BudgetItems> searchBudgetItems(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count by activity
    Long countByActivity(Urc_Activities activity);

    // Sum total cost by activity
    @Query("SELECT SUM(bi.total) FROM BudgetItems bi WHERE bi.activity = :activity")
    BigDecimal sumTotalCostByActivity(@Param("activity") Urc_Activities activity);

    // Sum total cost by COA
    @Query("SELECT SUM(bi.total) FROM BudgetItems bi WHERE bi.coacode = :coa")
    BigDecimal sumTotalCostByCOA(@Param("coa") COA coa);

    // Get budget items summary by account code
    @Query("SELECT bi.coacode.code, bi.coacode.name, SUM(bi.total), COUNT(bi) "
            + "FROM BudgetItems bi WHERE bi.activity = :activity AND bi.coacode IS NOT NULL "
            + "GROUP BY bi.coacode.id, bi.coacode.code, bi.coacode.name "
            + "ORDER BY bi.coacode.code ASC")
    List<Object[]> getBudgetItemsSummaryByAccountCode(@Param("activity") Urc_Activities activity);

    // Get budget items summary by category
    @Query("SELECT bi.category, SUM(bi.total), COUNT(bi) "
            + "FROM BudgetItems bi WHERE bi.activity = :activity "
            + "GROUP BY bi.category "
            + "ORDER BY bi.category ASC")
    List<Object[]> getBudgetItemsSummaryByCategory(@Param("activity") Urc_Activities activity);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE budgetItem
        SET procureClass_id = coa.PROC_CLASS
        FROM coa
        WHERE budgetItem.coa_id = coa.id
        """, nativeQuery = true)
    int updateProcureClassFromCoa();

    @Query(value = """
        SELECT
            STUFF((
                SELECT DISTINCT
                       ', ' + f.fundsource
                FROM budgetItem bi
                JOIN fundsource f ON bi.fundsource_id = f.id
                WHERE bi.budget_id = :budgetId
                  AND bi.activity_id2 = :activityId
                FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)'), 1, 2, '')
        """, nativeQuery = true)
    String findDistinctFundSourcesByBudgetAndActivity(
            @Param("budgetId") Long budgetId,
            @Param("activityId") Long activityId
    );

    @Query(value = """
        SELECT
            STUFF((
                SELECT DISTINCT
                       ', ' + f.fundsource
                FROM budgetItem bi
                JOIN fundsource f ON bi.fundsource_id = f.id
                WHERE bi.budget_id = :budgetId
                  AND bi.activity_id2 = :activityId
                  AND bi.Organisation_id = :budgetTypeId
                FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)'), 1, 2, '')
        """, nativeQuery = true)
    String findDistinctFundSourcesByBudgetActivityAndType(
            @Param("budgetId") Long budgetId,
            @Param("activityId") Long activityId,
            @Param("budgetTypeId") Long budgetTypeId
    );

    @Query(value = """
        SELECT
            STUFF((
                SELECT DISTINCT
                       ', ' + f.fundsource
                FROM budgetItem bi
                JOIN fundsource f ON bi.fundsource_id = f.id
                WHERE bi.budget_id = :budgetId
                  AND bi.activity_id2 = :activityId
                  AND bi.Organisation_id IN (:budgetTypeIds)
                FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)'), 1, 2, '')
        """, nativeQuery = true)
    String findDistinctFundSourcesByBudgetActivityAndTypes(
            @Param("budgetId") Long budgetId,
            @Param("activityId") Long activityId,
            @Param("budgetTypeIds") Set<Long> budgetTypeIds
    );

    List<BudgetItems> findByActivity(Urc_Activities activity);

    @Query("""
    SELECT b
    FROM BudgetItems b
    WHERE b.budget = :budget
      AND (b.coacode.code LIKE '2%' OR b.coacode.code LIKE '3%')
      AND b.activity = :activity
      AND b.budgetType IN (:budgetTypes)
""")
    List<BudgetItems> findDeptItemsForActivityWith2or3Coa(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity, @Param("budgetTypes") Set<Organisation> budgetTypes);

    @Query("""
        SELECT b.coacode AS coacode,
               SUM(b.jan + b.feb + b.mar+ b.apr + b.may + b.jun + b.jul + b.aug + b.sep + b.oct + b.nov + b.dec) AS total,
               SUM(b.jan + b.feb + b.mar) AS qtr1,
               SUM(b.apr + b.may + b.jun) AS qtr2,
               SUM(b.jul + b.aug + b.sep) AS qtr3,
               SUM(b.oct + b.nov + b.dec) AS qtr4
        FROM BudgetItems b
        WHERE b.budget = :budget
      AND (b.coacode.code LIKE '2%' OR b.coacode.code LIKE '3%')
      AND b.activity = :activity
      AND b.budgetType IN (:budgetTypes)
        GROUP BY b.coacode
        ORDER BY b.coacode.code
    """)
    List<BudgetItemsSummaryProjection> findDistinctByCoacodeWithSums(
            @Param("budget") Budget budget,
            @Param("activity") Urc_Activities activity, @Param("budgetTypes") Set<Organisation> budgetTypes
    );

    @Query("""
    SELECT b.coacode AS coacode,
           SUM(b.jan + b.feb + b.mar +
               b.apr + b.may + b.jun +
               b.jul + b.aug + b.sep +
               b.oct + b.nov + b.dec) AS total,
           SUM(b.jan + b.feb + b.mar) AS qtr1,
           SUM(b.apr + b.may + b.jun) AS qtr2,
           SUM(b.jul + b.aug + b.sep) AS qtr3,
           SUM(b.oct + b.nov + b.dec) AS qtr4
      FROM BudgetItems b
     WHERE b.budget = :budget
       AND b.deptUnit IN (:deptUnits)
       AND b.budgetType IN (:budgetTypes)
       AND b.coacode.code LIKE :prefix
  GROUP BY b.coacode
  ORDER BY b.coacode.code
""")
    List<BudgetItemsSummaryProjection> findCoaSummaryByPrefix(
            @Param("budget") Budget budget,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("prefix") String prefix
    );

    /**
     * Fetch BudgetItem IDs by budget and COA list
     */
    @Query("""
        SELECT b.id
        FROM BudgetItems b
        WHERE b.budget = :budget
          AND b.coacode IN :coas
    """)
    List<Long> findIdsByBudgetAndCoacodeIn(
            @Param("budget") Budget budget,
            @Param("coas") List<COA> coas
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM BudgetItems b
        WHERE b.id IN :ids
    """)
    int deleteByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT SUM(b.jul + b.aug + b.sep + b.oct + b.nov + b.dec + b.jan + b.feb + b.mar + b.apr + b.may + b.jun) "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget AND b.coacode = :coa")
    BigDecimal getTotalByBudgetAndCoa(@Param("budget") Budget budget,
            @Param("coa") COA coa);

    @Query("""
        select
            coalesce(sum(
                coalesce(b.jul, 0) +
                coalesce(b.aug, 0) +
                coalesce(b.sep, 0) +
                coalesce(b.oct, 0) +
                coalesce(b.nov, 0) +
                coalesce(b.dec, 0) +
                coalesce(b.jan, 0) +
                coalesce(b.feb, 0) +
                coalesce(b.mar, 0) +
                coalesce(b.apr, 0) +
                coalesce(b.may, 0) +
                coalesce(b.jun, 0)
            ), 0)
        from BudgetItems b
        join b.coacode c
        where b.budget.id = :budgetId
          and c.class2 = :class2
    """)
    BigDecimal getTotalByBudgetAndClassification2(
            @Param("budgetId") Long budgetId,
            @Param("class2") Classification2 class2
    );

    @Query("""
        select
            coalesce(sum(
                coalesce(b.jul, 0) +
                coalesce(b.aug, 0) +
                coalesce(b.sep, 0) +
                coalesce(b.oct, 0) +
                coalesce(b.nov, 0) +
                coalesce(b.dec, 0) +
                coalesce(b.jan, 0) +
                coalesce(b.feb, 0) +
                coalesce(b.mar, 0) +
                coalesce(b.apr, 0) +
                coalesce(b.may, 0) +
                coalesce(b.jun, 0)
            ), 0)
        from BudgetItems b
        join b.coacode c
        where b.budget.id = :budgetId
          and c.class1 = :class1
    """)
    BigDecimal getTotalByBudgetAndClassification1(
            @Param("budgetId") Long budgetId,
            @Param("class1") Classification1 class1
    );

    @Query("""
    select coalesce(sum(
        coalesce(b.jul, 0) + coalesce(b.aug, 0) + coalesce(b.sep, 0) +
        coalesce(b.oct, 0) + coalesce(b.nov, 0) + coalesce(b.dec, 0) +
        coalesce(b.jan, 0) + coalesce(b.feb, 0) + coalesce(b.mar, 0) +
        coalesce(b.apr, 0) + coalesce(b.may, 0) + coalesce(b.jun, 0)
    ), 0)
    from BudgetItems b
    join b.coacode c
    where b.budget.id = :budgetId
      and c.code like '1%'
""")
    BigDecimal getTotalIncomeByBudget(@Param("budgetId") Long budgetId);

}
