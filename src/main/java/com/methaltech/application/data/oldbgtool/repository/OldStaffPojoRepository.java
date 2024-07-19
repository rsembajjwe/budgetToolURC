
package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.OldStaffPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OldStaffPojoRepository extends JpaRepository<OldStaffPojo, Integer> {
    // Custom query method to find records by fiscal year (FY)
    List<OldStaffPojo> findByFiscalYear(String fiscalYear);
}