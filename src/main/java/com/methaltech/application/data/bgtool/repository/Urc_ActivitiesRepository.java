package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface Urc_ActivitiesRepository extends JpaRepository<Urc_Activities, Long> {

    Page<Urc_Activities> findByUrcPriorityAreas(URC_Priority_Areas ndp111Objective, Pageable pageable);

    /*
    @Query("SELECT ua FROM Urc_Activities ua WHERE ua.urcPriorityAreas.urcStrategicPlan.nationalBudgetFocusArea.nationalTransportMasterPlan.ndp111Objective.budget = :budget")
    Page<Urc_Activities> findByBudget(Budget budget, Pageable pageable);*/

 /*    @Query("SELECT ua FROM Urc_Activities ua WHERE ua.urcPriorityAreas.urcStrategicPlan.nationalBudgetFocusArea.nationalTransportMasterPlan.ndp111Objective.budget = :budget")
    List<Urc_Activities> findByBudget2(Budget budget);*/
    List<Urc_Activities> findByBudget(Budget budget);

    @Modifying
    @Query("DELETE FROM Urc_Activities b WHERE b = :activity")
    void deleteActivity(@Param("activity") Urc_Activities activity);

    /*    @Query("""
    SELECT DISTINCT a FROM Urc_Activities a
    JOIN FETCH a.urcPriorityAreas pa
    JOIN FETCH pa.priorityArea pr
    JOIN FETCH a.deptSection ds
    JOIN FETCH a.budget b
    LEFT JOIN FETCH a.quarterlyActuals q
    WHERE a.budget = :budget
    AND a.urcPriorityAreas = :urcPriorityAreas
    AND a.deptSection IN :deptSections
    """)
    List<Urc_Activities> findWithAllJoins(
    @Param("budget") Budget budget,
    @Param("urcPriorityAreas") URC_Priority_Areas urcPriorityAreas,
    @Param("deptSections") List<UrcDeptSectionAnlDimbgt> deptSections);*/
    @Query("""
    SELECT DISTINCT a
    FROM Urc_Activities a
    JOIN FETCH a.urcPriorityAreas pa
    JOIN FETCH pa.priorityArea pr
    JOIN FETCH a.deptSection ds
    JOIN FETCH a.budget b
    LEFT JOIN FETCH a.quarterlyActuals q
    LEFT JOIN FETCH a.deliverable_outputs d
    WHERE a.budget = :budget
      AND a.urcPriorityAreas = :urcPriorityAreas
      AND a.deptSection IN :deptSections
""")
    List<Urc_Activities> findWithAllJoins(
            @Param("budget") Budget budget,
            @Param("urcPriorityAreas") URC_Priority_Areas urcPriorityAreas,
            @Param("deptSections") List<UrcDeptSectionAnlDimbgt> deptSections);

    @Query("""
    SELECT DISTINCT a FROM Urc_Activities a
    JOIN FETCH a.urcPriorityAreas pa
    JOIN FETCH pa.priorityArea pr 
    JOIN FETCH a.deptSection ds
    JOIN FETCH a.budget b
    LEFT JOIN FETCH a.quarterlyActuals q       
    WHERE a.budget = :budget
      AND a.deptSection IN :deptSections
""")
    List<Urc_Activities> findWithAllJoinsByBudgetAndSectionSet(
            @Param("budget") Budget budget,
            @Param("deptSections") Set<UrcDeptSectionAnlDimbgt> deptSections);

    List<Urc_Activities> findByBudgetAndUrcPriorityAreasAndDeptSectionIn(Budget budget, URC_Priority_Areas urcPriorityAreas, List<UrcDeptSectionAnlDimbgt> deptSections);

    @Query("SELECT ua FROM Urc_Activities ua WHERE ua.budget = :budget "
            + "AND ua.urcPriorityAreas = :urcPriorityAreas "
            + "AND (ua.activityCode LIKE %:keyword% "
            + "OR ua.name LIKE %:keyword% "
            + "OR ua.fundsource LIKE %:keyword% "
            + "OR ua.output LIKE %:keyword% "
            + "OR ua.outcome LIKE %:keyword% "
            + "OR ua.objective LIKE %:keyword%) "
            + "AND ua.deptSection IN :deptSections")
    List<Urc_Activities> customSearchByFields(
            @Param("budget") Budget budget,
            @Param("urcPriorityAreas") URC_Priority_Areas urcPriorityAreas,
            @Param("deptSections") List<UrcDeptSectionAnlDimbgt> deptSections,
            @Param("keyword") String keyword);

    List<Urc_Activities> findByUrcPriorityAreas(URC_Priority_Areas urcPriorityAreas);

    List<Urc_Activities> findByUrcPriorityAreasAndBudgetAndDeptSectionIn(
            URC_Priority_Areas urcPriorityAreas, Budget budget, List<UrcDeptSectionAnlDimbgt> deptSections);

    /*    @Query("SELECT ua FROM Urc_Activities ua "
    + "WHERE ua.urcPriorityAreas.urcStrategicPlan.nationalBudgetFocusArea.nationalTransportMasterPlan.ndp111Objective.budget = :budget "
    + "AND (:search IS NULL OR "
    + "ua.name LIKE %:search% OR "
    + "ua.output LIKE %:search% OR "
    + "ua.performanceIndicator LIKE %:search% OR "
    + "ua.outcome LIKE %:search% OR "
    + "ua.objective LIKE %:search%)")
    List<Urc_Activities> findByBudgetAndSearch(
    @Param("budget") Budget budget,
    @Param("search") String search
    );*/
    @Query("SELECT ua FROM Urc_Activities ua WHERE ua.deptSection IN :deptSections")
    List<Urc_Activities> findByDeptSections(@Param("deptSections") List<UrcDeptSectionAnlDimbgt> deptSections);

    List<Urc_Activities> findByDeptSectionAndBudgetAndNameContaining(
            UrcDeptSectionAnlDimbgt deptSection, Budget budget, String filter);

    //List<Urc_Activities> findByDeptSectionAndBudget(UrcDeptSectionAnlDimbgt deptSection, Budget budget);
    @Query("""
       SELECT a 
       FROM Urc_Activities a 
       LEFT JOIN FETCH a.quarterlyActuals 
       WHERE a.deptSection = :deptSection 
         AND a.budget = :budget
       """)
    List<Urc_Activities> findByDeptSectionAndBudget(
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("budget") Budget budget);

    @Query("""
       SELECT a 
       FROM Urc_Activities a 
       LEFT JOIN FETCH a.quarterlyActuals 
       WHERE a.deptSection IN :deptSection 
         AND a.budget = :budget
       """)
    List<Urc_Activities> findByDeptSectionAndBudget(
            @Param("deptSection") Set<UrcDeptSectionAnlDimbgt> deptSection,
            @Param("budget") Budget budget);

    @Query("SELECT ua FROM Urc_Activities ua WHERE ua.deptSection = :deptSection "
            + "AND ua.budget = :budget "
            + "AND ua.name LIKE %:filter%")
    List<Urc_Activities> findByDeptSectionAndBudgetAndSearch(
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("budget") Budget budget,
            @Param("filter") String filter);

    Urc_Activities save(Urc_Activities activity); // Standard save method

    @Query("SELECT MAX(a.activityCode) FROM Urc_Activities a WHERE a.deptSection.ANL_CODE = :anlCode")
    String findMaxActivityCodeByAnlCode(@Param("anlCode") String anlCode);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO urcActivities (activityCode, name, fundsource, output, performanceIndicator, outcome, objective, bdgt, Jul, Nov, Mar, Aug, Dec, Apr, Sep, Jan, May, Oct, Feb, Jun, Total, month, activity_budget, urcPriorityAreas, deptSection, budget) "
            + "VALUES (:activityCode, :name, :fundsource, :output, :performanceIndicator, :outcome, :objective, :bdgt, :Jul, :Nov, :Mar, :Aug, :Dec, :Apr, :Sep, :Jan, :May, :Oct, :Feb, :Jun, :Total, :month, :activity_budget, :urcPriorityAreas, :deptSection, :budget)",
            nativeQuery = true)
    void insertActivity(@Param("activityCode") String activityCode, @Param("name") String name, @Param("fundsource") String fundsource, @Param("output") String output,
            @Param("performanceIndicator") String performanceIndicator, @Param("outcome") String outcome,
            @Param("objective") String objective, @Param("bdgt") BigDecimal bdgt, @Param("Jul") BigDecimal Jul,
            @Param("Nov") BigDecimal Nov, @Param("Mar") BigDecimal Mar, @Param("Aug") BigDecimal Aug,
            @Param("Dec") BigDecimal Dec, @Param("Apr") BigDecimal Apr, @Param("Sep") BigDecimal Sep,
            @Param("Jan") BigDecimal Jan, @Param("May") BigDecimal May, @Param("Oct") BigDecimal Oct,
            @Param("Feb") BigDecimal Feb, @Param("Jun") BigDecimal Jun, @Param("Total") BigDecimal Total,
            @Param("month") BigDecimal month, @Param("activity_budget") BigDecimal activity_budget,
            @Param("urcPriorityAreas") URC_Priority_Areas urcPriorityAreas,
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("budget") Budget budget);

    Urc_Activities findTopByOrderByIdDesc();

    List<Urc_Activities> findByOrigid(Long origid);

    List<Urc_Activities> findByBudgetAndDeptSectionIn(Budget budget, List<UrcDeptSectionAnlDimbgt> deptSections);

    //List<Urc_Activities> findByBudgetAndUrcPriorityAreasAndDeptSectionIn(Budget budget, URC_Priority_Areas urcPriorityAreas, List<UrcDeptSectionAnlDimbgt> deptSections);
    @Query("SELECT ua FROM Urc_Activities ua WHERE ua.activityCode = :activityCode AND ua.budget = :budget")
    Urc_Activities findByActivityCodeAndBudget(@Param("activityCode") String activityCode, @Param("budget") Budget budget);

    // Find by department section
    List<Urc_Activities> findByDeptSectionOrderByActivityCodeAsc(UrcDeptSectionAnlDimbgt deptSection);

    // Find by budget
    List<Urc_Activities> findByBudgetOrderByActivityCodeAsc(Budget budget);

    // Find by section and budget
    List<Urc_Activities> findByDeptSectionAndBudgetOrderByActivityCodeAsc(UrcDeptSectionAnlDimbgt deptSection, Budget budget);

    // Find by activity code
    List<Urc_Activities> findByActivityCodeContainingIgnoreCaseOrderByActivityCodeAsc(String activityCode);

    // Find by name
    List<Urc_Activities> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    // Find by fund source
    List<Urc_Activities> findByFundsourceContainingIgnoreCaseOrderByActivityCodeAsc(String fundSource);

    // Search activities
    @Query("SELECT a FROM Urc_Activities a WHERE "
            + "LOWER(a.activityCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(a.fundsource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "ORDER BY a.activityCode ASC")
    Page<Urc_Activities> searchActivities(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find activities with valid budget
    @Query("SELECT a FROM Urc_Activities a WHERE a.activity_budget IS NOT NULL AND a.activity_budget > 0 ORDER BY a.activityCode ASC")
    List<Urc_Activities> findActivitiesWithValidBudget();

    // Count by section
    Long countByDeptSection(UrcDeptSectionAnlDimbgt deptSection);

    // Count by budget
    Long countByBudget(Budget budget);

    // Sum activity budget by budget
    @Query("SELECT SUM(a.activity_budget) FROM Urc_Activities a WHERE a.budget = :budget")
    Double sumActivityBudgetByBudget(@Param("budget") Budget budget);

    // Sum activity budget by section and budget
    @Query("SELECT SUM(a.activity_budget) FROM Urc_Activities a WHERE a.deptSection = :section AND a.budget = :budget")
    Double sumActivityBudgetBySectionAndBudget(@Param("section") UrcDeptSectionAnlDimbgt section, @Param("budget") Budget budget);

    // Option 1: Using JPQL (returns concatenated string)
    @Query("SELECT GROUP_CONCAT(DISTINCT ua.fundsource) "
            + "FROM Urc_Activities ua "
            + "WHERE ua.budget.id = :budgetId")
    String findDistinctFundSourcesByBudget(@Param("budgetId") Long budgetId);

    // Option 2: If GROUP_CONCAT is not supported in JPQL, use native query
    @Query(
            value = "SELECT STRING_AGG(fundsource, ', ') "
            + "FROM (SELECT DISTINCT fundsource FROM urcActivities WHERE budget_id = :budgetId) AS distinct_sources",
            nativeQuery = true
    )
    String findDistinctFundSourcesByBudgetNative(@Param("budgetId") Long budgetId);

}
