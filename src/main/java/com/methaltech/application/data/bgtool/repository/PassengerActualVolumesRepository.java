
package com.methaltech.application.data.bgtool.repository;
import com.methaltech.application.data.Display;
import com.methaltech.application.data.entity.bgtool.PassengerActualVolumes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassengerActualVolumesRepository extends JpaRepository<PassengerActualVolumes, Long> {
    List<PassengerActualVolumes> findByBudgetIdAndCoacodeDisplay(Long budgetId, Display display);
    Optional<PassengerActualVolumes> findByBudgetIdAndCoacodeId(Long budgetId, Long coaId);
}

