package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.IncomeSources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeSourcesRepository extends JpaRepository<IncomeSources, Long> {

    List<IncomeSources> findByBudget(Budget budget);

    void deleteByIncomeSource(String incomeSource);

    void delete(IncomeSources incomeSource);
}
