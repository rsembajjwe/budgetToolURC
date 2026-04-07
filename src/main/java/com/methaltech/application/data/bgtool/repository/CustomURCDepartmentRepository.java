package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Custom_URC_Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomURCDepartmentRepository extends JpaRepository<Custom_URC_Department, String> {

    List<Custom_URC_Department> findByActiveTrueOrderByDisplayOrderAscDepartmentNameAsc();

    Optional<Custom_URC_Department> findBySourceAnlCode(String sourceAnlCode);
}
