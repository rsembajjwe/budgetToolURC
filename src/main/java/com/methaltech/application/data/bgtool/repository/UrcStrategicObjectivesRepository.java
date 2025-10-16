
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.UrcStrategicObjectives;
import com.methaltech.application.data.entity.bgtool.UrcStrategicPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UrcStrategicObjectivesRepository extends JpaRepository<UrcStrategicObjectives, Long> {

    // 🔍 Find all objectives belonging to a specific strategic plan
    //List<UrcStrategicObjectives> findByUrcStrategicPlan(UrcStrategicPlan urcStrategicPlan);

    // Optional: Find objectives by keyword search (useful for UI filtering)
   // List<UrcStrategicObjectives> findByObjectiveContainingIgnoreCase(String keyword);
}
