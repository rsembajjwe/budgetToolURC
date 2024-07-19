package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.OldFreightRate;
import com.methaltech.application.data.oldbgtool.repository.OldFreightRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OldFreightRateService {

    private final OldFreightRateRepository freightRateRepository;

    @Autowired
    public OldFreightRateService(OldFreightRateRepository freightRateRepository) {
        this.freightRateRepository = freightRateRepository;
    }

    public OldFreightRate saveFreightRate(OldFreightRate freightRate) {
        return freightRateRepository.save(freightRate);
    }

    public List<OldFreightRate> getAllFreightRates() {
        return freightRateRepository.findAll();
    }

    public List<OldFreightRate> getFreightRatesByFiscalYear(String fiscalYear) {
        return freightRateRepository.findByFiscalYear(fiscalYear);
    }

    public OldFreightRate getFreightRateById(Integer id) {
        return freightRateRepository.findById(id).orElse(null);
    }

    public void deleteFreightRate(Integer id) {
        freightRateRepository.deleteById(id);
    }
}
