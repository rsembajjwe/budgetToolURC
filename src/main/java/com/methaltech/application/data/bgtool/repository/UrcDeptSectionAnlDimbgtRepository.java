package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcDeptSectionAnlDimbgtRepository extends JpaRepository<UrcDeptSectionAnlDimbgt, String> {

    @Query("SELECT u FROM UrcDeptSectionAnlDimbgt u WHERE u.ANL_CODE LIKE 'S%'")
    List<UrcDeptSectionAnlDimbgt> findByANL_CODEStartingWithS();

    @Query("SELECT u FROM UrcDeptSectionAnlDimbgt u WHERE u.ANL_CODE LIKE 'S%'")
    Page<UrcDeptSectionAnlDimbgt> findByANL_CODEStartingWithS(Pageable page);

    @Query("SELECT u FROM UrcDeptSectionAnlDimbgt u WHERE u.ANL_CODE = :anlCode")
    UrcDeptSectionAnlDimbgt findByCustomANL_CODE(@Param("anlCode") String anlCode);

    @Query("SELECT u FROM UrcDeptSectionAnlDimbgt u WHERE u.ANL_CODE IN :anlCode")
    Set<UrcDeptSectionAnlDimbgt> findByCustomANL_CODE2(@Param("anlCode") List<String> anlCode);
}
