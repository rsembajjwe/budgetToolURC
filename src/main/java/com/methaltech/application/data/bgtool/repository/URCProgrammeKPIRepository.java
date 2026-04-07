package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.URC_Programme_KPI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface URCProgrammeKPIRepository extends JpaRepository<URC_Programme_KPI, Long> {

    List<URC_Programme_KPI> findByProgrammeId(Long programmeId);

    List<URC_Programme_KPI> findByDepartmentDepartmentCode(String departmentCode);
}
