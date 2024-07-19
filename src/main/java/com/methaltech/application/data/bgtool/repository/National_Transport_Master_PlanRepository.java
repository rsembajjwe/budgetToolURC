
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.NDPIII_Objective;
import com.methaltech.application.data.entity.bgtool.National_Transport_Master_Plan;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface National_Transport_Master_PlanRepository  extends JpaRepository<National_Transport_Master_Plan, Long> {
     // Add custom query method to fetch by NDPIII_Objective
    Page<National_Transport_Master_Plan> findByNdp111Objective(NDPIII_Objective ndp111Objective,Pageable pageable);   
}
