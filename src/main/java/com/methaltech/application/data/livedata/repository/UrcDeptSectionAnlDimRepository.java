
package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UrcDeptSectionAnlDim;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcDeptSectionAnlDimRepository extends JpaRepository<UrcDeptSectionAnlDim, String> {

    @Query("SELECT u FROM UrcDeptSectionAnlDim u WHERE u.ANL_CODE LIKE 'S%'")
    List<UrcDeptSectionAnlDim> findByANL_CODEStartingWithD();
    
    @Query("SELECT u FROM UrcDeptSectionAnlDim u WHERE u.ANL_CODE LIKE 'S%'")
    Page<UrcDeptSectionAnlDim> findByANL_CODEStartingWithD2(Pageable page);    
    
    @Query("SELECT u FROM UrcDeptSectionAnlDim u WHERE u.ANL_CODE = :anlCode")
    UrcDeptSectionAnlDim findByCustomANL_CODE(@Param("anlCode") String anlCode);
    
}
