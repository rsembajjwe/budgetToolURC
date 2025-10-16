
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.NdpPlan;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriorityAreaRepository extends JpaRepository<PriorityArea, Long> {
    //List<PriorityArea> findByNdpPlan(NdpPlan ndpPlan);
        @Query("""
           SELECT p FROM PriorityArea p
           JOIN p.ndpPlan n
           WHERE :targetDate BETWEEN n.startDate AND n.endDate
           """)
    List<PriorityArea> findByNdpPlanActiveOn(@Param("targetDate") LocalDate targetDate);
}
