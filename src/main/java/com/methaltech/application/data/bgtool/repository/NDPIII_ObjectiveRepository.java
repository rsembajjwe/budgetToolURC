
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.NDPIII_Objective;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NDPIII_ObjectiveRepository extends JpaRepository<NDPIII_Objective, Long> {
Page<NDPIII_Objective> findByBudget(Budget budget,Pageable pageable);    
}
