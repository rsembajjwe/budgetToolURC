
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.UrDepartmentsAnlDim2;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrDepartmentsAnlDimRepository2 extends JpaRepository<UrDepartmentsAnlDim2, Long> {
   Optional<UrDepartmentsAnlDim2> findByAnlCode(String anlCode);
}
