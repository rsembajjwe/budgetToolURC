package com.methaltech.application.data.bgtool.repository;

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
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
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
            + "AND b.coalevel1.code = :coalevel1Code")
    BigDecimal sumProgrammeSummation(
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("budget") Budget budget,
            @Param("activities") List<Urc_Activities> activities,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("coalevel1Code") Integer coalevel1Code
    );

    default boolean isSumProgrammeGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            List<Urc_Activities> activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits,
            Integer coalevel1Code
    ) {
        BigDecimal sum = sumProgrammeSummation(budgetTypes, budget, activities, deptUnits, coalevel1Code);
        return sum.compareTo(BigDecimal.ZERO) > 0;
    }

    @Query("SELECT COALESCE(SUM(b.jan + b.feb + b.mar + b.apr + b.may + b.jun + "
            + "b.jul + b.aug + b.sep + b.oct + b.nov + b.dec), 0) "
            + "FROM BudgetItems b "
            + "WHERE b.budgetType IN (:budgetTypes) "
            + "AND b.budget = :budget "
            + "AND b.activity = :activities "
            + "AND b.deptUnit IN (:deptUnits) "
            + "AND b.coalevel1.code = :coalevel1Code")
    BigDecimal sumActvitySummation(
            @Param("budgetTypes") Set<Organisation> budgetTypes,
            @Param("budget") Budget budget,
            @Param("activities") Urc_Activities activities,
            @Param("deptUnits") Set<UrcDeptSectionAnlDimbgt> deptUnits,
            @Param("coalevel1Code") Integer coalevel1Code
    );

    default boolean isSumActvityGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            Urc_Activities activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits,
            Integer coalevel1Code
    ) {
        BigDecimal sum = sumActvitySummation(budgetTypes, budget, activities, deptUnits, coalevel1Code);
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

    @Query("SELECT SUM(b.jul) + SUM(b.aug) + SUM(b.sep) + SUM(b.oct) + SUM(b.nov) + SUM(b.dec) + "
            + "SUM(b.jan) + SUM(b.feb) + SUM(b.mar) + SUM(b.apr) + SUM(b.may) + SUM(b.jun) as totalSum "
            + "FROM BudgetItems b "
            + "WHERE b.budget = :budget "
            + "AND b.coacode IN :coacode")
    BigDecimal findSumOfTotalMonthsByBudgetCoa(
            @Param("budget") Budget budget,
            @Param("coacode") List<COA> coacode);

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


}
