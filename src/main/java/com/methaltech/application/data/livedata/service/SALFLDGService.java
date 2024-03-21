
package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class SALFLDGService {

    private final SALFLDGRepository salfldgRepository;

    @Autowired
    public SALFLDGService(SALFLDGRepository salfldgRepository) {
        this.salfldgRepository = salfldgRepository;
    }
    public BigDecimal findSumOfAmountByAccntCodeAndPeriod(String accntCode,int period){
      return   salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(accntCode, period);
    }
    
    public BigDecimal findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(String accntCode,int period,Set<String> analT1Set){
      return   salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(accntCode, period,analT1Set);
    }    
}

