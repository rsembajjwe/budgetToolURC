package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.bgtool.repository.CurrencyRepository;
import com.methaltech.application.data.entity.bgtool.CurrencyData;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CurrencyService {

    private final CurrencyRepository repository;

    @Autowired
    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    public Optional<Currency> get(Long id) {
        return repository.findById(id);
    }

    public Page<Currency> getBudget(Budget budget, Pageable pageable) {
        return repository.findByBudget(budget, pageable);

    }

    public Optional<Currency> getCurrencyById(Long id) {
        return repository.findById(id);

    }

    public Currency update(Currency entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Currency> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<Currency> findCurrencyByBudget(Budget budget) {
        return repository.findByBudget(budget);
    }

    public Currency findCurrenciesByCurrencyShortAndBudget(String currencyShort, Budget budget) {
        return repository.findCurrenciesByCurrencyShortAndBudget(currencyShort, budget);
    }

    public Currency getCurrencyByBudgetAndCurrencyShort(Budget budget, String currencyShort) {
        return repository.findByBudgetAndData_CurrencyShort(budget, currencyShort);
    }

    public Currency findByDataCurrencyAndBudget(String currency, Budget budget) {
        return repository.findByDataCurrencyAndBudget(currency, budget);
    }

    public Optional<Currency> findCurrency(CurrencyData data, Budget budget) {
        /*        return repository.findByDataAndBudget(data, budget)
        .orElseThrow(() -> new EntityNotFoundException("Currency not found"));*/
                return repository.findByDataAndBudget(data, budget);
    }
}
