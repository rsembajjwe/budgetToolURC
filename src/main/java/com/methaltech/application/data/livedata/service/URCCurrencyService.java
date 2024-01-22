
package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.livedata.URCCurrency;
import com.methaltech.application.data.livedata.repository.URCCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class URCCurrencyService {

    private final URCCurrencyRepository urcCurrencyViewRepository;

    @Autowired
    public URCCurrencyService(URCCurrencyRepository urcCurrencyViewRepository) {
        this.urcCurrencyViewRepository = urcCurrencyViewRepository;
    }

    public List<URCCurrency> getAllURCCurrencyViews() {
        return urcCurrencyViewRepository.findAll();
    }

}

