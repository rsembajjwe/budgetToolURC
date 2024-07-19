
package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.OldDeptUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldDeptUnitRepository extends JpaRepository<OldDeptUnit, Integer> {
    
}
