package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptSectionMergerRepository extends JpaRepository<DeptSectionMerger, Long> {

    Optional<DeptSectionMerger> findByDeptcode(String deptcode);

    /*
    @Query("SELECT d FROM DeptSectionMerger d WHERE d.Deptcode = :deptcode")
    List<DeptSectionMerger> findByDeptcodeCustom(@Param("deptcode") String deptcode);*/

    @Query("SELECT d FROM DeptSectionMerger d WHERE d.deptcode = :deptcode")
    Optional<DeptSectionMerger> findByDeptcodeCustom(@Param("deptcode") String deptcode);
}
