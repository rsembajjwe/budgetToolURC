package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Coalevel1Repository extends JpaRepository<Coalevel1, Long> {
    
     //List<Coalevel1> findByBudget(Budget budget);

    /*        @Query("SELECT s FROM Coalevel1 s WHERE s.budget = :budget")
    List<Coalevel1> findCoalevel1ByBudgetId(@Param("budget") Budget budget);*/
    
    @Query("SELECT c FROM Coalevel1 c WHERE c.name = :name")
    Coalevel1 findByNameAndBudget(@Param("name") String name);
    boolean existsByName(String name);
    Coalevel1 findByCode(Integer code);

}
