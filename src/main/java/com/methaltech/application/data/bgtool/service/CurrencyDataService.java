package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.CurrencyData;
import com.methaltech.application.data.bgtool.repository.CurrencyDataRepository;
import com.methaltech.application.data.bgtool.repository.CurrencyRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.livedata.URCCurrency;
import com.methaltech.application.data.livedata.repository.URCCurrencyRepository;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CurrencyDataService {

    private final CurrencyDataRepository repository;
    private final URCCurrencyRepository urcCurrencyViewRepository;
    private final CurrencyRepository currencyEntityRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyDataService(CurrencyDataRepository repository, URCCurrencyRepository urcCurrencyViewRepository,
            CurrencyRepository currencyEntityRepository,CurrencyRepository currencyRepository) {
        this.repository = repository;
        this.urcCurrencyViewRepository = urcCurrencyViewRepository;
        this.currencyEntityRepository = currencyEntityRepository;
        this.currencyRepository=currencyRepository;
    }

    public Optional<CurrencyData> get(Long id) {
        return repository.findById(id);
    }

    public boolean getCurrencyData(String currency) {
        return repository.findByCurrency(currency) != null;

    }

    public List<CurrencyData> getCurrencyDataById(Iterable<Long> id) {
        return repository.findAllById(id);

    }

    public CurrencyData update(CurrencyData entity) {
        return repository.save(entity);
    }

    public CurrencyData updateCurrencyData(CurrencyData entity) {
        return repository.save(entity);
    }

    public int setUserInfoById(CurrencyData entity) {
        return repository.setUserInfoById(entity.getCurrency(), entity.getCurrencyShort(), entity.getId());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<CurrencyData> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<CurrencyData> listAllCurrencyData() {
        return repository.findAll();
    }

    public int count() {
        return (int) repository.count();
    }

    public Optional<CurrencyData> getCurrencyByName(String currency) {
        CurrencyData currencys = repository.findByCurrency(currency);

        return Optional.ofNullable(currencys);
    }

    public CurrencyData saveCustomer(CurrencyData savedCustomer) {
        CurrencyData currencydata = repository.findByCurrency(savedCustomer.getCurrency());

        if (currencydata != null) {
            currencydata.setCurrency(savedCustomer.getCurrency());
            currencydata.setCurrencyShort(savedCustomer.getCurrencyShort());
        } else {
            currencydata = new CurrencyData();
            currencydata.setCurrency(savedCustomer.getCurrency());
            currencydata.setCurrencyShort(savedCustomer.getCurrencyShort());
        }
        return repository.save(currencydata);
    }

    public void sunCurr() {
        List<URCCurrency> data = urcCurrencyViewRepository.findAll();
        for (URCCurrency dat : data) {
            CurrencyData td = new CurrencyData();
            td.setCurrency(dat.getDescr());
            td.setCurrencyShort(dat.getCurrCode());
            repository.save(td);

        }
    }

    public void fetchFromBdgt() {
        List<Currency> data = currencyEntityRepository.findAll();
        List<CurrencyData> availed = listAllCurrencyData();
        for (Currency currencyEntity : data) {
            String currencyShort = currencyEntity.getData().getCurrencyShort();
            CurrencyData currencyData;
            List<CurrencyData> mm = repository.findByCurrencyShort(currencyEntity.getData().getCurrencyShort());
            if (mm.isEmpty() && !currencyShort.equals("")) {

                CurrencyData newCurrencyData = new CurrencyData();
                newCurrencyData.setCurrency(currencyEntity.getData().getCurrency());
                newCurrencyData.setCurrencyShort(currencyShort);
                repository.save(newCurrencyData);

            }

        }
    }

    public Optional<CurrencyData> findLastSavedItem() {
        return repository.findLastSavedItem(PageRequest.of(0, 1)).stream().findFirst();
    }

    public void fetchFromCurrencyfromBdgt(Budget budget) {
        List<Currency> data = currencyEntityRepository.findByBudget(budget);
        for (Currency currencyEntity : data) {
            String currencyShort = currencyEntity.getData().getCurrencyShort();
            List<CurrencyData> mm = repository.findByCurrencyShort(currencyEntity.getData().getCurrencyShort());
            if (mm.isEmpty() && !currencyShort.equals("")) {

                CurrencyData newCurrencyData = new CurrencyData();
                newCurrencyData.setCurrency(currencyEntity.getData().getCurrency());
                newCurrencyData.setCurrencyShort(currencyShort);
                repository.save(newCurrencyData);

                Optional<CurrencyData> findLastSavedItem = findLastSavedItem();
                if (findLastSavedItem.isPresent()) {
                    Currency cur = new Currency();
                    cur.setBudget(budget);
                    cur.setData(findLastSavedItem.get());
                    cur.setEnabled(true);
                    int precision = 10; // The number of decimal places you want to keep
                    RoundingMode roundingMode = RoundingMode.HALF_UP; // Choose the rounding mode that suits your needs
                    cur.setRate(currencyEntity.getRate());
                    currencyRepository.save(cur);
                    
                }

            }

        }
    }
}
