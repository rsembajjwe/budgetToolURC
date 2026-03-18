package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StaffSalaryRepository extends JpaRepository<StaffSalary, Long> {

    List<StaffSalary> findByBudget(Budget budget);

    @Modifying
    @Transactional
    @Query("DELETE FROM StaffSalary s WHERE s = :staffSalary")
    void deleteStaffSalary(@Param("staffSalary") StaffSalary staffSalary);

    List<StaffSalary> findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType,
            Urc_Activities activity
    );

    @Query("SELECT SUM(s.salary) FROM StaffSalary s "
            + "WHERE s.budget = :budget "
            + "AND s.deptUnit = :deptUnit "
            + "AND s.budgetType = :budgetType "
            + "AND s.activity = :activity")
    BigDecimal getSumOfSalariesByCriteria(
            @Param("budget") Budget budget,
            @Param("deptUnit") UrcDeptSectionAnlDimbgt deptUnit,
            @Param("budgetType") Organisation budgetType,
            @Param("activity") Urc_Activities activity
    );

    @Query("SELECT COALESCE(SUM(s.salary),0) FROM StaffSalary s "
            + "WHERE s.budget = :budget ")
    BigDecimal getSumOfSalariesByCriteria2(
            @Param("budget") Budget budget
    );

    @Query("SELECT COALESCE(SUM(s.salary),0) FROM StaffSalary s WHERE s.budget = :budget")
    BigDecimal getSumOfSalariesByBudget(@Param("budget") Budget budget);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 12 FROM StaffSalary s WHERE s.budget = :budget")
    BigDecimal calculateTotalSalaryByFy(Budget budget);

    @Query("SELECT COALESCE(SUM(s.salary), 0)*(0.1) FROM StaffSalary s WHERE s.budget = :budget")
    BigDecimal calculateTotalMonthSalaryByFy(Budget budget);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 0.1 FROM StaffSalary s WHERE s.budget = :budget")
    BigDecimal calculateTotalNssfSalaryByFy(Budget budget);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 0.25 FROM StaffSalary s WHERE s.budget = :budget")
    BigDecimal calculateTotalGratuitySalaryByFy(Budget budget);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 0.03 FROM StaffSalary s WHERE s.budget = :budget")
    BigDecimal calculateTotalMonthWKMSalaryByFy(Budget budget);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 0.25 FROM StaffSalary s WHERE s.budget = :budget")
    BigDecimal calculateTotalMonthGratuitySalaryByFy(Budget budget);

    // Method to retrieve the last saved item
    StaffSalary findFirstByOrderByIdDesc();

    @Transactional
    long deleteByBudget(Budget budget);

}
