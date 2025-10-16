package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.livedata.UrcBSalfldg;
import com.methaltech.application.data.entity.livedata.UrcBSalfldgId;
import com.methaltech.application.data.livedata.repository.UrcBSalfldgRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UrcBSalfldgService {

    @Autowired
    private UrcBSalfldgRepository repository;

    public List<UrcBSalfldg> findAll() {
        return repository.findAll();
    }

    public Optional<UrcBSalfldg> findById(UrcBSalfldgId id) {
        return repository.findById(id);
    }

    public UrcBSalfldg save(UrcBSalfldg entity) {
        return repository.save(entity);
    }

    public void deleteById(UrcBSalfldgId id) {
        repository.deleteById(id);
    }
    /*
    public List<UrcBSalfldg> findByAccntCodeAndPeriod(String accntCode, Integer period) {
    return repository.findByAccntCodeAndPeriod(accntCode, period);
    }
    
    public List<UrcBSalfldg> findByAccntCodeAndPeriodIn(String accntCode, List<Integer> periods) {
    return repository.findByAccntCodeAndPeriodIn(accntCode, periods);
    }*/

    /*    public BigDecimal findTotalAmountByAccntCodeAndPeriodIn(String accntCode, List<Integer> periods) {
    BigDecimal totalAmount = repository.findTotalAmountByAccntCodeAndPeriodIn(accntCode, periods);
    return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }
    
    public BigDecimal findTotalAmountByAccntCodeAndPeriod(String accntCode, Integer period) {
    BigDecimal totalAmount = repository.findTotalAmountByAccntCodeAndPeriod(accntCode, period);
    return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }*/

    public BigDecimal findTotalAmountByAccntCode(String accntCode) {
        BigDecimal totalAmount = repository.findTotalAmountByAccntCode(accntCode);
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }
}
