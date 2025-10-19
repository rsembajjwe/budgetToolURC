package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.NdpPlan;
import com.methaltech.application.data.entity.bgtool.UrcStrategicPlan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcStrategicPlanRepository extends JpaRepository<UrcStrategicPlan, Long> {

    /*    @Query("SELECT DISTINCT p FROM UrcStrategicPlan p LEFT JOIN FETCH p.strategicObjectives")
    List<UrcStrategicPlan> findAllWithObjectives();*/
    
        @Query("""
           SELECT n 
           FROM UrcStrategicPlan n 
           WHERE :date BETWEEN n.startDate AND n.endDate
           """)
    Optional<UrcStrategicPlan> findByDateBetween(@Param("date") LocalDate date);
}
