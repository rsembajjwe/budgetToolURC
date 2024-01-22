
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReport;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReportImp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomDetailedBudgetReportRepository extends JpaRepository<CustomDetailedBudgetReport, Long> {
    // You can add custom query methods here if needed
    List<CustomDetailedBudgetReport> findByBudgetreport(CustomDetailedBudgetReportImp budgetreport);
}

