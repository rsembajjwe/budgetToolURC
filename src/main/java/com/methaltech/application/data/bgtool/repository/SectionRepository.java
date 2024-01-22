package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.Department;
import com.methaltech.application.data.entity.bgtool.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("SELECT s FROM Section s WHERE s.department = :department")
    List<Section> findByDepartmentId(@Param("department") Department department);

    List<Section> findByDepartment(Department department);

    @Query("SELECT s FROM Section s WHERE s.department.budget = :budget")
    List<Section> findAllSectionsByBudget(@Param("budget") Budget budget);
    
    List<Section> findByDepartmentIdIn(List<Long> departmentIds);
}
