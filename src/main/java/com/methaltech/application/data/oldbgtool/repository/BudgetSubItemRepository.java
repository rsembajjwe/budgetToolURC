package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.BudgetSubItem;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetSubItemRepository extends JpaRepository<BudgetSubItem, Integer> {

    @Query("SELECT SUM(bs.total) FROM BudgetSubItem bs WHERE bs.bCategory = :category AND bs.fiscalYear = :fiscalYear AND bs.deptUnit IN :deptUnits")
    BigDecimal getTotalByCategoryAndFiscalYearAndDeptUnits(@Param("category") String category, @Param("fiscalYear") String fiscalYear, @Param("deptUnits") List<Integer> deptUnits);

    @Query("SELECT SUM(bs.total) FROM BudgetSubItem bs WHERE bs.progActivity IN :progActivityList AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumTotalByActivitiesByUnitByCategoryByFy(@Param("progActivityList") List<Integer> progActivityList, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.total) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumTotalByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.jul) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumJulByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.aug) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumAugByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.sep) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumSepByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.oct) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumOctByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.nov) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumNovByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.dec) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumDecByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.jan) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumJanByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.feb) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumFebByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.mar) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumMarByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.apr) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumAprByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.may) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumMayByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.jun) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumJunByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.jul + bs.aug + bs.sep + bs.oct + bs.nov + bs.dec + bs.jan + bs.feb + bs.mar + bs.apr + bs.may + bs.jun) FROM BudgetSubItem bs WHERE bs.progActivity = :activityId AND bs.deptUnit IN :deptUnitList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear")
    BigDecimal sumMonthsByActivityByUnitByCategoryByFy(@Param("activityId") Integer activityId, @Param("deptUnitList") List<Integer> deptUnitList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear);

    @Query("SELECT SUM(bs.total) FROM BudgetSubItem bs WHERE bs.progActivity = :progActivityList")
    BigDecimal sumTotalByActivities(@Param("progActivityList") Integer progActivityList);

    @Query("SELECT SUM(bs.total) FROM BudgetSubItem bs WHERE bs.progActivity IN :progActivityList")
    BigDecimal sumTotalByActivitiesList(@Param("progActivityList") List<Integer> progActivityList);

    @Query("SELECT bs FROM BudgetSubItem bs WHERE bs.progActivity = :progActivityList AND bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear AND bs.deptUnit IN :deptUnitList")
    List<BudgetSubItem> BudgetByActivitiesByUnitByCategoryByFy(@Param("progActivityList") Integer progActivityList, @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear, @Param("deptUnitList") List<Integer> deptUnitList);

    @Query("SELECT bs FROM BudgetSubItem bs WHERE bs.bCategory = :bCategory AND bs.fiscalYear = :fiscalYear AND bs.deptUnit IN :deptUnitList")
    List<BudgetSubItem> BudgetByUnitByCategoryByFy( @Param("bCategory") String bCategory, @Param("fiscalYear") String fiscalYear, @Param("deptUnitList") List<Integer> deptUnitList);
}
