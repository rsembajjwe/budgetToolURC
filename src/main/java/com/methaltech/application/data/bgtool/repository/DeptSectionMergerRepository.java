package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Query(value = "SELECT deptcode FROM DeptSectionMerger WHERE ?1 IN sectioncodes", nativeQuery = true)
    String findDeptcodeBySectioncode(String sectioncode);

    @Query("SELECT d.deptcode FROM DeptSectionMerger d JOIN d.sectioncodes s WHERE s = :codeSection")
    Optional<String> findDeptcodeBySectioncode2(Set<String> codeSection);

    @Query("SELECT d.deptcode FROM DeptSectionMerger d WHERE EXISTS (SELECT 1 FROM d.sectioncodes s WHERE s = :sectioncode)")
    String findDeptcodeBySectioncode3(@Param("sectioncode") String sectioncode);
   // List<DeptSectionMerger> findBySectioncodesContaining(String sectionCode);
    //List<DeptSectionMerger> findBySectioncodesIn(String sectionCode);
    List<DeptSectionMerger> findBySectioncodesIn(List<String> sectionCodes);

}
