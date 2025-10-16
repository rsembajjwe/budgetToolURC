package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.URC_Strategic_Plan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface URC_Priority_AreasRepository extends JpaRepository<URC_Priority_Areas, Long> {
    /*
    Page<URC_Priority_Areas> findByUrcStrategicPlan(URC_Strategic_Plan ndp111Objective, Pageable pageable);
    
    @Query("SELECT urcPriority FROM URC_Priority_Areas urcPriority "
    + "JOIN urcPriority.urcStrategicPlan.nationalBudgetFocusArea.nationalTransportMasterPlan.ndp111Objective ndpObjective "
    + "WHERE ndpObjective.budget = :budget")
    List<URC_Priority_Areas> findByNDPIIIObjectiveBudget(@Param("budget") Budget budget);*/

    List<URC_Priority_Areas> findByBudget(Budget budget);
    List<URC_Priority_Areas> findByNameLikeAndBudget(String name, Budget budget);
    
    @Query("SELECT u FROM URC_Priority_Areas u WHERE u.name LIKE %:name% AND u.budget = :budget")
    List<URC_Priority_Areas> customSearchByNameAndBudget(
        @Param("name") String name,
        @Param("budget") Budget budget
    );    
    Optional<URC_Priority_Areas> findFirstByOrderByIdDesc();
    
        /** ✅ New: safely fetches all with PriorityArea and NdpPlan initialized */
    @Query("""
        SELECT u FROM URC_Priority_Areas u
        JOIN FETCH u.priorityArea pa
        JOIN FETCH pa.ndpPlan np
        WHERE u.budget = :budget
    """)
    List<URC_Priority_Areas> findByBudgetWithPriorityAndPlan(@Param("budget") Budget budget);
    
        @Query("""
        SELECT u FROM URC_Priority_Areas u
        LEFT JOIN FETCH u.priorityArea pa
        LEFT JOIN FETCH pa.ndpPlan np
        WHERE u.budget = :budget
    """)
    List<URC_Priority_Areas> findByBudgetWithPriority(@Param("budget") Budget budget);

    @Query("""
        SELECT u FROM URC_Priority_Areas u
        LEFT JOIN FETCH u.priorityArea pa
        LEFT JOIN FETCH pa.ndpPlan np
        WHERE u.name LIKE %:name%
        AND u.budget = :budget
    """)
    List<URC_Priority_Areas> findByNameAndBudgetWithPriority(@Param("name") String name, @Param("budget") Budget budget);
    

}
