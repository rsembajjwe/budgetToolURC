
package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.URCCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface URCCurrencyRepository extends JpaRepository<URCCurrency, String> {
    // Define custom query methods if needed
}

