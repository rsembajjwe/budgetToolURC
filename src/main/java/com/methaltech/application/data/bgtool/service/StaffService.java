package com.methaltech.application.data.bgtool.service;


import com.methaltech.application.data.bgtool.repository.StaffRepository;
import com.methaltech.application.data.entity.bgtool.Staff;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public List<Staff> getStaffByFinancialYear(String fy) {
        return staffRepository.findByFy(fy);
    }

    public Staff getStaffById(int id) {
        return staffRepository.findById(id).orElse(null);
    }

    public Staff saveStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public void deleteStaff(int id) {
        staffRepository.deleteById(id);
    }

    public BigDecimal getTotalSalaryByFinancialYear(String fy) {
        return staffRepository.calculateTotalSalaryByFy(fy);
    }

    public BigDecimal getTotalNssfByFinancialYear(String fy) {
        return staffRepository.calculateTotalNssfSalaryByFy(fy);
    }

    public BigDecimal getTotalSalaryGratuityByFinancialYear(String fy) {
        return staffRepository.calculateTotalGratuitySalaryByFy(fy);
    }
}
