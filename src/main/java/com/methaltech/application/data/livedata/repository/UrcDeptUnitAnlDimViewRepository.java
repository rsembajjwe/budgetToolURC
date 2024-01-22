
package com.methaltech.application.data.livedata.repository;
import com.methaltech.application.data.entity.livedata.UrcDeptUnitAnlDimView;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcDeptUnitAnlDimViewRepository extends JpaRepository<UrcDeptUnitAnlDimView, String> {
   Optional<UrcDeptUnitAnlDimView> findByANLCODE(String ANL_CODE);
}

