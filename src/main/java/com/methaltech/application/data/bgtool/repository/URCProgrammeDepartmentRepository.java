
package com.methaltech.application.data.bgtool.repository;
import com.methaltech.application.data.entity.bgtool.URC_Programme_Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface URCProgrammeDepartmentRepository extends JpaRepository<URC_Programme_Department, Long> {
    List<URC_Programme_Department> findByProgrammeId(Long programmeId);
    List<URC_Programme_Department> findByDepartmentDepartmentCode(String departmentCode);
}
