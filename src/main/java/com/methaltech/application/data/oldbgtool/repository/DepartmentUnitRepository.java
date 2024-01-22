package com.methaltech.application.data.oldbgtool.repository;


import com.methaltech.application.data.entity.oldbgtool.DepartmentUnit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentUnitRepository extends JpaRepository<DepartmentUnit, Integer> {

    List<DepartmentUnit> findBySectionId(Integer sectionId);
}
