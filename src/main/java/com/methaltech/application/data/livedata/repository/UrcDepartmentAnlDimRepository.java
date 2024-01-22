package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcDepartmentAnlDimRepository extends JpaRepository<UrcDepartmentAnlDim, String> {

    @Query("SELECT u FROM UrcDepartmentAnlDim u WHERE u.ANL_CODE LIKE 'D%'")
    List<UrcDepartmentAnlDim> findByANL_CODEStartingWithD();
}
