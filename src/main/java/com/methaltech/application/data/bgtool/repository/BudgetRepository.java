package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findByFinancialYear(String fy);

    List<Budget> findAllByFinancialYearNot(String financialYear);

    /*    @Query(value = "SELECT TOP 1 * FROM budget ORDER BY closeDate DESC", nativeQuery = true)
    Budget findLastSavedBudget();*/
    @Query("SELECT b FROM Budget b WHERE b.financialYear = :financialYear")
    Optional<Budget> findByFY(@Param("financialYear") String financialYear);

    @Query("SELECT b FROM Budget b ORDER BY b.id DESC")
    List<Budget> findLastSavedBudget();

    /*    @Query("SELECT b FROM Budget b ORDER BY b.id DESC")
    Optional<Budget> findTopByOrderByIdDesc();*/
    @Query(value = "SELECT TOP 1 * FROM budget ORDER BY id DESC", nativeQuery = true)
    Optional<Budget> findTopByOrderByIdDesc();
    
    // Step 1: Get the most frequent financialYear
    @Query("SELECT b FROM Budget b GROUP BY b.financialYear ORDER BY COUNT(b) DESC")
    Optional<Budget> findTopFinancialYearByCount();

    // Step 2: Get any one Budget with that financialYear
    Optional<Budget> findFirstByFinancialYear(String financialYear);  
}
