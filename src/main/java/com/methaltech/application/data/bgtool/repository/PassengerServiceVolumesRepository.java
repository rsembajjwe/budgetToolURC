
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.entity.bgtool.PassengerServiceVolumes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassengerServiceVolumesRepository extends JpaRepository<PassengerServiceVolumes, Long> {
    List<PassengerServiceVolumes> findByBudgetIdAndCoacodeDisplay(Long budgetId, Display display);
    Optional<PassengerServiceVolumes> findByBudgetIdAndCoacodeId(Long budgetId, Long coaId);
}

