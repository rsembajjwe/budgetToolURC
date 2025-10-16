package com.methaltech.application.data.bgtool.repository;


import com.methaltech.application.data.entity.bgtool.Staff;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    List<Staff> findByFy(String fy);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 12 FROM Staff s WHERE s.fy = :fy")
    BigDecimal calculateTotalSalaryByFy(String fy);

    @Query("SELECT COALESCE((SUM(s.salary), 0) * 12*0.1) FROM Staff s WHERE s.fy = :fy")
    BigDecimal calculateTotalNssfSalaryByFy(String fy);

    @Query("SELECT COALESCE((SUM(s.salary), 0) * 12*0.25) FROM Staff s WHERE s.fy = :fy")
    BigDecimal calculateTotalGratuitySalaryByFy(String fy);
}
