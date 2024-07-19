
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.National_Budget_Focus_Areas;
import com.methaltech.application.data.entity.bgtool.National_Transport_Master_Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface National_Budget_Focus_AreasRepository  extends JpaRepository<National_Budget_Focus_Areas, Long>{
Page<National_Budget_Focus_Areas> findByNationalTransportMasterPlan(National_Transport_Master_Plan nationalTransportMasterPlan,Pageable pageable);    
}
