package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.NdpPlan;
import com.methaltech.application.data.entity.bgtool.UrcStrategicPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcStrategicPlanRepository extends JpaRepository<UrcStrategicPlan, Long> {

    /*    @Query("SELECT DISTINCT p FROM UrcStrategicPlan p LEFT JOIN FETCH p.strategicObjectives")
    List<UrcStrategicPlan> findAllWithObjectives();*/
}
