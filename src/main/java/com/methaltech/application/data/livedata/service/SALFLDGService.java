package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.GenerateQtrFromFy;
import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SALFLDGService {

    private final SALFLDGRepository salfldgRepository;
     private final GenerateQtrFromFy generateQtrFromFy;

    @Autowired
    public SALFLDGService(SALFLDGRepository salfldgRepository,GenerateQtrFromFy generateQtrFromFy) {
        this.salfldgRepository = salfldgRepository;
        this.generateQtrFromFy=generateQtrFromFy;
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriod(String accntCode, int period) {
        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(accntCode, period);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(String accntCode, int period, Set<String> analT1Set) {
        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(accntCode, period, analT1Set);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriods(String accntCode, Quarters qtrFromFy, String fy) {
int i=0;
        List<String> per = generateQtrFromFy.periods(fy, qtrFromFy);
        // Convert the list of String periods to a list of Integer periods
        List<Integer> intPer = per.stream().map(Integer::parseInt).collect(Collectors.toList());
        System.out.println(i+++" | "+intPer+" | "+qtrFromFy+" | "+fy);
        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriods(accntCode, intPer);
    }
}
