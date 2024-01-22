package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.UnitsBudget;
import com.methaltech.application.data.entity.bgtool.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnitsBudgetRepository extends JpaRepository<UnitsBudget, Long> {

    //List<UnitsBudget> findByBudgetAndUser(Budget budget, User user);
    
    UnitsBudget findByBudgetAndUser(Budget budget, User user);

    @Query("SELECT ub.units FROM UnitsBudget ub WHERE ub.budget = :budget AND ub.user = :user")
    List<D_Unit> findUnitsByBudgetAndUser(@Param("budget") Budget budget, @Param("user") User user);
}
