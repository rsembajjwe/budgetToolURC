
package com.methaltech.application.data.oldbgtool.service;
import com.methaltech.application.data.bgtool.repository.CurrencyRepository;
import com.methaltech.application.data.entity.oldbgtool.CurrencyEntity;
import com.methaltech.application.data.oldbgtool.repository.CurrencyEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

@Service
public class oldCurrencyService {
    private final CurrencyEntityRepository currencyRepository;

    @Autowired
    public oldCurrencyService(CurrencyEntityRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<CurrencyEntity> getAllCurrencies() {
        return currencyRepository.findAll();
    }
    public List<CurrencyEntity> findByFy(String fy) {
        return currencyRepository.findByFy(fy);
    }
   
}

