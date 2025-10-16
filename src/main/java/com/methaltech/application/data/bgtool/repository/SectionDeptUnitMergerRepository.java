package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.SectionDeptUnitMerger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionDeptUnitMergerRepository extends JpaRepository<SectionDeptUnitMerger, Long> {

    Optional<SectionDeptUnitMerger> findBySectioncode(String sectioncode);

    @Query("SELECT DISTINCT s.sectioncode FROM SectionDeptUnitMerger s JOIN s.deptUnitcodes d WHERE d = :deptUnitcode")
    List<String> findDistinctSectionCodesByDeptUnitcode(@Param("deptUnitcode") String deptUnitcode);

    @Query("SELECT s.sectioncode FROM SectionDeptUnitMerger s WHERE :deptUnitcode IN (s.deptUnitcodes)")
    String findSectionCodeByDeptUnitCode(@Param("deptUnitcode") String deptUnitcode);

    @Query("SELECT s.sectioncode FROM SectionDeptUnitMerger s WHERE :deptUnitcode IN (s.deptUnitcodes)")
    List<String> findSectionCodesByDeptUnitCode(@Param("deptUnitcode") String deptUnitcode);

    @Query("SELECT s FROM SectionDeptUnitMerger s WHERE :deptUnitcode IN (s.deptUnitcodes)")
    List<SectionDeptUnitMerger> findSectionCodesByDeptUnitCode2(@Param("deptUnitcode") String deptUnitcode);
}
