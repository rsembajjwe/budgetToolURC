package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.CurrencyData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author GG-TECH SYSTEM
 */
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Page<Currency> findByBudget(Budget budget, Pageable pageable);

    List<Currency> findByBudget(Budget budget);

    @Query("SELECT c FROM Currency c WHERE c.data.currencyShort = :currencyShort AND c.budget = :budget")
    Currency findCurrenciesByCurrencyShortAndBudget(
            @Param("currencyShort") String currencyShort,
            @Param("budget") Budget budget
    );

    Currency findByBudgetAndData_CurrencyShort(Budget budget, String currencyShort);

    @Query("SELECT c FROM Currency c WHERE c.data.currency = :currency AND c.budget = :budget")
    Currency findByDataCurrencyAndBudget(@Param("currency") String currency, @Param("budget") Budget budget);
    
    Optional<Currency> findByDataAndBudget(CurrencyData data, Budget budget);
}
