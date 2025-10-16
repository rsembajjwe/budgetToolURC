package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.NdpPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NdpPlanRepository extends JpaRepository<NdpPlan, Long> {

    @Query("SELECT n FROM NdpPlan n LEFT JOIN FETCH n.priorityAreas LEFT JOIN FETCH n.strategicObjectives")
    List<NdpPlan> findAllWithDetails();
}
