package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.SALFLDG;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SALFLDGRepository extends JpaRepository<SALFLDG, String> {

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period = :period")
    BigDecimal findSumOfAmountByAccntCodeAndPeriod(@Param("accntCode") String accntCode, @Param("period") int period);

    @Query("SELECT COALESCE(SUM(s.amount), 0) "
            + "FROM SALFLDG s "
            + "WHERE s.accntCode = :accntCode "
            + "AND FUNCTION('MONTH', s.transDatetime) = :month "
            + "AND FUNCTION('YEAR', s.transDatetime) = :year")
    BigDecimal findSumOfAmountByAccntCodeAndMonthYear(
            @Param("accntCode") String accntCode,
            @Param("month") int month,
            @Param("year") int year);

    /*    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period = :period")
    BigDecimal findNetAmountByAccntCodeAndPeriod(@Param("accntCode") String accntCode, @Param("period") int period);*/
 /*    @Query("SELECT COALESCE(SUM(CASE WHEN s.dC = 'C' THEN s.amount ELSE -s.amount END), 0) "
    + "FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period = :period")
    BigDecimal findSignedSumByAccntCodeAndPeriod(@Param("accntCode") String accntCode, @Param("period") int period);*/
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

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period = :period AND s.analT1 IN :analT1Set")
    BigDecimal findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(@Param("accntCode") String accntCode,
            @Param("period") int period,
            @Param("analT1Set") Set<String> analT1Set);

    @Query("SELECT COALESCE(SUM(s.amount), 0) "
            + "FROM SALFLDG s "
            + "WHERE s.accntCode = :accntCode "
            + "AND FUNCTION('MONTH', s.transDatetime) = :month "
            + "AND FUNCTION('YEAR', s.transDatetime) = :year "
            + "AND s.analT1 IN :analT1Set")
    BigDecimal findSumOfAmountByAccntCodeAndMonthYearBySections(
            @Param("accntCode") String accntCode,
            @Param("month") int month,
            @Param("year") int year,
            @Param("analT1Set") Set<String> analT1Set);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode = :accntCode AND s.period IN :periods")
    BigDecimal findSumOfAmountByAccntCodeAndPeriods(@Param("accntCode") String accntCode, @Param("periods") List<Integer> periods);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.accntCode IN :accntCode AND s.period IN :periods")
    BigDecimal findSumOfAmountByAccntCodeAndPeriods(@Param("accntCode") List<String> accntCode, @Param("periods") List<Integer> periods);

    List<String> findDistinctAccntCodeByAnalT1AndPeriod(String analT1, int period);

    @Query("SELECT DISTINCT s.accntCode FROM SALFLDG s WHERE s.analT1 IN :analT1List AND s.period IN :periodList")
    List<String> findDistinctAccntCodeByAnalT1InAndPeriodIn(List<String> analT1List, List<Integer> periodList);

    @Query("SELECT COALESCE(SUM(ABS(s.amount)), 0) FROM SALFLDG s WHERE s.analT1 = :analT1List AND s.period IN :periodList")
    BigDecimal findSumOfAmountByAnalT1AndPeriodIn(String analT1List, List<Integer> periodList);

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

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, AMOUNT AS amount, DESCRIPTN AS descriptn, "
            + "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1 "
            + "FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD = :period "
            + "AND ACCNT_CODE = :accntCode "
            + "AND ANAL_T1 IN (:analT1)", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndAccntCodeAndAnalT1InS(
            @Param("period") int period,
            @Param("accntCode") String accntCode,
            @Param("analT1") List<String> analT1);

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, AMOUNT AS amount, DESCRIPTN AS descriptn, "
            + "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1, PERIOD AS period "
            + "FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD IN :period "
            + "AND ACCNT_CODE = :accntCode "
            + "AND ANAL_T1 IN (:analT1)", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndAccntCodeAndAnalT1InS2(
            @Param("period") List<Integer> period,
            @Param("accntCode") String accntCode,
            @Param("analT1") List<String> analT1);

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, AMOUNT AS amount, DESCRIPTN AS descriptn, "
            + "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1 "
            + "FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD = :period "
            + "AND ACCNT_CODE = :accntCode ", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndAccntCode(
            @Param("period") int period,
            @Param("accntCode") String accntCode);

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, AMOUNT AS amount, DESCRIPTN AS descriptn, "
            + "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1 "
            + "FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD IN :period "
            + "AND ACCNT_CODE = :accntCode ", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndAccntCode2(
            @Param("period") List<Integer> period,
            @Param("accntCode") String accntCode);

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, AMOUNT AS amount, DESCRIPTN AS descriptn, "
            + "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1 "
            + "FROM URC_A_SALFLDG_View "
            + "WHERE  PERIOD IN :period AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%') "
            + "AND ACCNT_CODE NOT LIKE '321%' "
            + "AND ACCNT_CODE NOT LIKE '314%' "
            + "AND LEN(ACCNT_CODE) <= 6 ", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndTotalExpenditures(
            @Param("period") List<Integer> period);

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, AMOUNT AS amount, DESCRIPTN AS descriptn, "
            + "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1 "
            + "FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD IN :period AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%') "
            + "AND ACCNT_CODE NOT LIKE '321%' "
            + "AND ACCNT_CODE NOT LIKE '314%' "
            + "AND LEN(ACCNT_CODE) <= 6 "
            + "AND ANAL_T1 IN (:analT1Values) ", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndDepartmentExpenditures(
            @Param("period") Set<Integer> period, @Param("analT1Values") Set<String> analT1Values);

    @Query(value = "SELECT ACCNT_CODE AS accntCode, JRNAL_NO AS jrnalNo, JRNAL_LINE AS jrnalLine, PERIOD AS period, AMOUNT AS amount, DESCRIPTN AS descriptn, "
            + "TRANS_DATETIME AS transDatetime, ANAL_T1 AS analT1 "
            + "FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD IN :period AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%') "
            + "AND ACCNT_CODE NOT LIKE '321%' "
            + "AND ACCNT_CODE NOT LIKE '314%' "
            + "AND LEN(ACCNT_CODE) <= 6 "
            + "AND ANAL_T1 = (:analT1Values) ", nativeQuery = true)
    List<SALFLDGProjection> findByPeriodAndDepartmentExpenditures(
            @Param("period") Set<Integer> period, @Param("analT1Values") String analT1Values);

    @Query(value = "SELECT * FROM URC_A_SALFLDG_View "
            + "WHERE PERIOD IN :period "
            + "AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%') "
            + "AND ACCNT_CODE NOT LIKE '321%' "
            + "AND ACCNT_CODE NOT LIKE '314%' "
            + "AND LEN(ACCNT_CODE) <= 6 "
            + "AND ANAL_T1 = :analT1Values",
            nativeQuery = true)
    List<SALFLDG> findByPeriodAndSectionExpenditures(
            @Param("period") Set<Integer> period,
            @Param("analT1Values") String analT1Values);

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_A_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 IN (:analT1Values)
        AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%')
        AND ACCNT_CODE NOT LIKE '321%'
        AND ACCNT_CODE NOT LIKE '314%'
        AND LEN(ACCNT_CODE) <= 6
        AND (
            (ANAL_T0 IS NOT NULL AND ANAL_T0 != '') 
            OR 
            (ANAL_T1 IS NOT NULL AND ANAL_T1 != '')
        )
""", nativeQuery = true)
    BigDecimal findTotalAmountByPeriodsAndAnalT1Set(
            @Param("periods") Set<Integer> periods,
            @Param("analT1Values") Set<String> analT1Values
    );

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_A_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 IN (:analT1Values)
        AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%')
        AND ACCNT_CODE NOT LIKE '321%'
        AND ACCNT_CODE NOT LIKE '314%'
        AND LEN(ACCNT_CODE) <= 6
        AND (
            (ANAL_T0 IS NOT NULL OR ANAL_T0 != '') 
            OR 
            (ANAL_T1 IS NOT NULL OR ANAL_T1 != '')
        )
""", nativeQuery = true)
    BigDecimal findTotalAmountByPeriodsAndAnalT1Set2(
            @Param("periods") Set<Integer> periods,
            @Param("analT1Values") Set<String> analT1Values
    );

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_A_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 = (:analT1Values)
        AND (ACCNT_CODE LIKE '2%' OR ACCNT_CODE LIKE '3%')
        AND ACCNT_CODE NOT LIKE '321%'
        AND ACCNT_CODE NOT LIKE '314%'
        AND LEN(ACCNT_CODE) <= 6
        AND (
            (ANAL_T0 IS NOT NULL OR ANAL_T0 != '') 
            OR 
            (ANAL_T1 IS NOT NULL OR ANAL_T1 != '')
        )
""", nativeQuery = true)
    BigDecimal findTotalAmountByPeriodsAndAnalT1Set(
            @Param("periods") Set<Integer> periods,
            @Param("analT1Values") String analT1Values
    );

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_A_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 = :analT1Values
        AND ACCNT_CODE = :accntCode
        AND ANAL_T8 = :analT8Values
    """, nativeQuery = true)
    BigDecimal calculateTotalByBudgetAndCoaAndActivityAndSectionActuals(
            @Param("periods") Set<Integer> periods,
            @Param("accntCode") String accCode,
            @Param("analT8Values") String analT8Values,
            @Param("analT1Values") String analT1Values
    );

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_A_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ANAL_T1 = :analT1Values
        AND ACCNT_CODE = :accntCode
    """, nativeQuery = true)
    BigDecimal calculateTotalByBudgetAndCoaAndSectionActuals(
            @Param("periods") Set<Integer> periods,
            @Param("accntCode") String accCode,
            @Param("analT1Values") String analT1Values
    );

    @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_A_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ACCNT_CODE IN (:accntCodes)         
        AND LEN(ACCNT_CODE) <= 6
""", nativeQuery = true)
    BigDecimal findTotalAmountByPeriodsAndAccntCodes(
            @Param("periods") Set<Integer> periods,
            @Param("accntCodes") Set<String> accntCodes
    );
    
     @Query(value = """
    SELECT COALESCE(SUM(AMOUNT), 0)
    FROM URC_A_SALFLDG_View
    WHERE 
        PERIOD IN (:periods)
        AND ACCNT_CODE = (:accntCodes)
""", nativeQuery = true)
    BigDecimal findTotalAmountByPeriodsAndAccntCode(
            @Param("periods") Set<Integer> periods,
            @Param("accntCodes") String accntCodes
    );   

    @Query("SELECT COALESCE(SUM(s.amount), 0) "
            + "FROM SALFLDG s "
            + "WHERE s.analT8 = :analT8 AND s.accntCode = :accntCode")
    Double sumAmountByAnalT8AndAccntCode(@Param("analT8") String analT8,
            @Param("accntCode") String accntCode);

    @Query("SELECT COALESCE(SUM(s.amount), 0) "
            + "FROM SALFLDG s "
            + "WHERE s.analT8 = :analT8")
    Double sumAmountByAnalT8(@Param("analT8") String analT8);

    Optional<SALFLDG> findByAccntCodeAndPeriodAndTransDatetimeAndJrnalNoAndJrnalLine(
            String accntCode,
            Integer period,
            LocalDateTime transDatetime,
            Integer jrnalNo,
            Integer jrnalLine
    );

}
