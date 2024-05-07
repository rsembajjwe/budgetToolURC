package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.GenerateQtrFromFy;
import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.bgtool.repository.BudgetItemsRepository;
import com.methaltech.application.data.bgtool.repository.CoaRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SALFLDGService {

    private final SALFLDGRepository salfldgRepository;
    private final GenerateQtrFromFy generateQtrFromFy;
    private final BudgetItemsRepository repository;
    private final CoaRepository coaRepository;

    @Autowired
    public SALFLDGService(SALFLDGRepository salfldgRepository, GenerateQtrFromFy generateQtrFromFy, BudgetItemsRepository repository, CoaRepository coaRepository) {
        this.salfldgRepository = salfldgRepository;
        this.generateQtrFromFy = generateQtrFromFy;
        this.repository = repository;
        this.coaRepository = coaRepository;
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriod(String accntCode, int period) {
        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(accntCode, period);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(String accntCode, int period, Set<String> analT1Set) {
        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(accntCode, period, analT1Set);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriods(String accntCode, Quarters qtrFromFy, String fy) {
        int i = 0;
        List<String> per = generateQtrFromFy.periods(fy, qtrFromFy);
        // Convert the list of String periods to a list of Integer periods
        List<Integer> intPer = per.stream().map(Integer::parseInt).collect(Collectors.toList());

        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriods(accntCode, intPer);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriods(List<String> accntCode, Quarters qtrFromFy, String fy) {
        int i = 0;
        List<String> per = generateQtrFromFy.previousPeriods(fy, qtrFromFy);
        // Convert the list of String periods to a list of Integer periods
        List<Integer> intPer = per.stream().map(Integer::parseInt).collect(Collectors.toList());

        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriods(accntCode, intPer);
    }

    /*    public List<COA> listOFCoa( String analT1Set,int period,Budget budget) {
    List<COA> listcoaList=new ArrayList<>();
    List<String> per = salfldgRepository.findDistinctAccntCodeByAnalT1AndPeriod(analT1Set, period);
    for(String c:per){
    COA coa=  coaRepository.findByCodeAndBudget(c, budget);
    listcoaList.add(coa);
    }
    return listcoaList;
    }*/
    public List<COA> listOFCoa(List<String> analT1Set, List<Integer> period, Budget budget) {
        List<COA> listcoaList = new ArrayList<>();
        List<String> per = salfldgRepository.findDistinctAccntCodeByAnalT1InAndPeriodIn(analT1Set, period);

        for (String c : per) {
            COA coa = coaRepository.findByCodeAndBudget(c, budget);
            listcoaList.add(coa);
        }
        return listcoaList;
    }
}
