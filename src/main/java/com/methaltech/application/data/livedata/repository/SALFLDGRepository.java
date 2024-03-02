
package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.SALFLDG;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SALFLDGRepository extends JpaRepository<SALFLDG, String> {
    // You can add custom query methods here if needed
}

