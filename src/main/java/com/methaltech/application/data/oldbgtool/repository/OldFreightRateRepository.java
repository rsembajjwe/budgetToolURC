
package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.OldFreightRate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldFreightRateRepository extends JpaRepository<OldFreightRate, Integer> {
    // You can add custom query methods here if needed
    List<OldFreightRate> findByFiscalYear(String fiscalYear);
}
