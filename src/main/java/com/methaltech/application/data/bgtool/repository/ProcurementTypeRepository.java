
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.ProcurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementTypeRepository extends JpaRepository<ProcurementType, Long> {
    // You can add custom query methods if needed
}

