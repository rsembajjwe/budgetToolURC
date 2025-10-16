
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.NdpPlan;
import com.methaltech.application.data.entity.bgtool.StrategicObjective;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategicObjectiveRepository extends JpaRepository<StrategicObjective, Long> {
    List<StrategicObjective> findByNdpPlan(NdpPlan ndpPlan);
}
