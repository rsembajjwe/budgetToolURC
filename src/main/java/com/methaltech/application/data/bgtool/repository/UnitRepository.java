
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UnitRepository extends JpaRepository<D_Unit, Long>{
    @Query("SELECT u FROM D_Unit u WHERE u.section = :section")
    List<D_Unit> findBySectionId(@Param("section") Section section); 
    List<D_Unit> findBySection(Section section);
    List<D_Unit> findByDepartmentBudget(Budget budget);
    //List<D_Unit> findByBudget(Budget budget);
    //D_Unit findByBudgetAndUnit(Budget budget,String name);
   
}
