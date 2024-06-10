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

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) "
            + "FROM SALFLDG s "
            + "WHERE s.accntCode = :accntCode "
            + "AND YEAR(s.transDatetime) = :year "
            + "AND MONTH(s.transDatetime) = :month "
            + "AND s.period = :period")
    BigDecimal findSumOfAmountByAccntCodeAndMonthAndYearAndPeriod(
            @Param("accntCode") String accntCode,
            @Param("month") int month,
            @Param("year") int year,
            @Param("period") int period);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period = :period AND s.analT1 IN :analT1Set")
    BigDecimal findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(@Param("accntCode") String accntCode,
            @Param("period") int period,
            @Param("analT1Set") Set<String> analT1Set);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period IN :periods")
    BigDecimal findSumOfAmountByAccntCodeAndPeriods(@Param("accntCode") String accntCode, @Param("periods") List<Integer> periods);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode IN :accntCode AND s.period IN :periods")
    BigDecimal findSumOfAmountByAccntCodeAndPeriods(@Param("accntCode") List<String> accntCode, @Param("periods") List<Integer> periods);

    List<String> findDistinctAccntCodeByAnalT1AndPeriod(String analT1, int period);

    @Query("SELECT DISTINCT s.accntCode FROM SALFLDG s WHERE s.analT1 IN :analT1List AND s.period IN :periodList")
    List<String> findDistinctAccntCodeByAnalT1InAndPeriodIn(List<String> analT1List, List<Integer> periodList);

    /*    @Query("SELECT s FROM SALFLDG s WHERE s.period = :period AND s.accntCode = :accntCode AND s.analT1 IN :analT1List")
    List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1In(@Param("period") int period, @Param("accntCode") String accntCode, @Param("analT1List") List<String> analT1List);*/

 /*    @Query("SELECT s FROM SALFLDG s WHERE s.period = :period AND s.accntCode = :accntCode AND s.analT1 IN :analT1List")
    List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1In(@Param("period") int period, @Param("accntCode") String accntCode, @Param("analT1List") List<String> analT1List);*/
    List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1In(int period, String accntCode, List<String> analT1List);

    @Query(value = "SELECT * FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD = 2024001 "
            + "AND ACCNT_CODE = '221001' "
            + "AND ANAL_T1 IN ('S001')", nativeQuery = true)
    List<SALFLDG> findFixedEntries();

    @Query(value = "SELECT * FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD = :period "
            + "AND ACCNT_CODE = :accntCode "
            + "AND ANAL_T1 IN (:analT1)", nativeQuery = true)
    List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1InZ(
            @Param("period") int period,
            @Param("accntCode") String accntCode,
            @Param("analT1") List<String> analT1);

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, AMOUNT AS amount, DESCRIPTN AS descriptn, " +
                   "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1 " +
                   "FROM URC_A_SALFLDG_View " +
                   "WHERE PERIOD = :period " +
                   "AND ACCNT_CODE = :accntCode " +
                   "AND ANAL_T1 IN (:analT1)", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndAccntCodeAndAnalT1InS(
            @Param("period") int period, 
            @Param("accntCode") String accntCode, 
            @Param("analT1") List<String> analT1);

}
