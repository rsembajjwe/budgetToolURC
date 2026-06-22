package com.methaltech.application.data.bgtool.repository;


import com.methaltech.application.data.entity.bgtool.Staff;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    List<Staff> findByFy(String fy);

    List<Staff> findByFyOrderByLnameAscFnameAsc(String fy);

    Optional<Staff> findFirstByFyAndCode(String fy, String code);

    @Query("""
        SELECT s FROM Staff s
        WHERE s.fy = :fy
          AND (
              LOWER(COALESCE(s.fname, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(COALESCE(s.lname, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(COALESCE(s.code, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(COALESCE(s.position, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(COALESCE(s.email, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
          )
        ORDER BY s.lname ASC, s.fname ASC
    """)
    List<Staff> searchByFy(@Param("fy") String fy, @Param("searchTerm") String searchTerm);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 12 FROM Staff s WHERE s.fy = :fy")
    BigDecimal calculateTotalSalaryByFy(String fy);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 12 * 0.1 FROM Staff s WHERE s.fy = :fy")
    BigDecimal calculateTotalNssfSalaryByFy(String fy);

    @Query("SELECT COALESCE(SUM(s.salary), 0) * 12 * 0.25 FROM Staff s WHERE s.fy = :fy")
    BigDecimal calculateTotalGratuitySalaryByFy(String fy);
}
