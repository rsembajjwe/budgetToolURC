
package com.methaltech.application.data.bgtool.repository;
import com.methaltech.application.data.entity.bgtool.COAReconcile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface COAReconcileRepository extends JpaRepository<COAReconcile, Long> {
    List<COAReconcile> findByOldcoa(String oldcoa);
}

