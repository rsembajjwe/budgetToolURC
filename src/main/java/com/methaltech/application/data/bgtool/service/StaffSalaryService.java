package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.StaffSalaryRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffSalaryService {

    private final StaffSalaryRepository staffSalaryRepository;

    @Autowired
    public StaffSalaryService(StaffSalaryRepository staffSalaryRepository) {
        this.staffSalaryRepository = staffSalaryRepository;
    }

    public List<StaffSalary> findByBudget(Budget budget) {
        return staffSalaryRepository.findByBudget(budget);
    }

    public List<StaffSalary> findByBudgetAndDeptUnitAndBudgetTypeAndActivity(Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType, Urc_Activities activity) {
        return staffSalaryRepository.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(budget, deptUnit, budgetType, activity);
    }

    public BigDecimal getSumOfSalariesByBudget(Budget budget) {
        return staffSalaryRepository.getSumOfSalariesByBudget(budget);
    }

    public BigDecimal getSumOfSalariesByBudget(Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType, Urc_Activities activity) {
        return staffSalaryRepository.getSumOfSalariesByCriteria(budget, deptUnit, budgetType, activity);
    }

    public BigDecimal getSumOfSalariesByCriteria2(Budget budget) {
        return staffSalaryRepository.getSumOfSalariesByCriteria2(budget);
    }

public StaffSalary saveStaffSalary(StaffSalary staffSalary) {
    try {
        StaffSalary savedSalary = staffSalaryRepository.save(staffSalary);
        System.out.println("Salary Saved");
        return savedSalary;
    } catch (Exception e) {
        System.out.println("Error saving salary: " + e.getMessage());
        // You might want to log the exception or rethrow it depending on your use case
        throw new RuntimeException("Error saving salary", e);
    }
}


    public void deleteBystaff(StaffSalary staffSalary) {
        staffSalaryRepository.deleteStaffSalary(staffSalary);
    }

    public BigDecimal getTotalSalaryByFinancialYear(Budget budget) {
        return staffSalaryRepository.calculateTotalSalaryByFy(budget);
    }

    public BigDecimal getTotalNssfByFinancialYear(Budget budget) {
        return staffSalaryRepository.calculateTotalNssfSalaryByFy(budget);
    }

    public BigDecimal getTotalSalaryGratuityByFinancialYear(Budget budget) {
        return staffSalaryRepository.calculateTotalGratuitySalaryByFy(budget);
    }

    public BigDecimal getTotalSalaryMonthGratuityByFinancialYear(Budget budget) {
        return staffSalaryRepository.calculateTotalMonthGratuitySalaryByFy(budget);
    }

    public BigDecimal getTotalMonthSalaryByFinancialYear(Budget budget) {
        return staffSalaryRepository.calculateTotalMonthSalaryByFy(budget);
    }

    public BigDecimal calculateTotalMonthWKMSalaryByFy(Budget budget) {
        return staffSalaryRepository.calculateTotalMonthWKMSalaryByFy(budget);
    }

    public StaffSalary getLastSavedItem() {
        return staffSalaryRepository.findFirstByOrderByIdDesc();
    }
}
