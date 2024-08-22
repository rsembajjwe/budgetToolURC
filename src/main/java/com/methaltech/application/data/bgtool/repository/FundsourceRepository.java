
package com.methaltech.application.data.bgtool.repository;


import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundsourceRepository extends JpaRepository<Fundsource, Long> {
    List<Fundsource> findByBudget(Budget budget);
    Fundsource findByFundsourceAndBudget(String fundsource, Budget budget);
    Fundsource findTopByBudgetOrderByIdDesc(Budget budget);
    Fundsource findTopByOrderByIdDesc();

}

