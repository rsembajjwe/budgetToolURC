package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.bgtool.repository.BudgetRepository;
import com.methaltech.application.data.bgtool.repository.CoaRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.livedata.SALFLDG;
import com.methaltech.application.data.livedata.repository.SALFLDGProjection;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import com.methaltech.application.data.livedata.repository.UrcCSalFldgRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SALFLDGService {

    private final SALFLDGRepository salfldgRepository;
    private final UrcCSalFldgRepository urcCSalFldgRepository;
    private final CoaRepository coaRepository;
    private final BudgetRepository repository;

    @Autowired
    public SALFLDGService(SALFLDGRepository salfldgRepository, CoaRepository coaRepository,
            BudgetRepository repository, UrcCSalFldgRepository urcCSalFldgRepository) {
        this.salfldgRepository = salfldgRepository;
        this.coaRepository = coaRepository;
        this.repository = repository;
        this.urcCSalFldgRepository = urcCSalFldgRepository;
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriod(String accntCode, int period) {
        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(accntCode, period);
    }

    public BigDecimal findSumOfAmountByAnalT1AndPeriodIn(String sectionCode, List<Integer> period) {
        return salfldgRepository.findSumOfAmountByAnalT1AndPeriodIn(sectionCode, period);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(String accntCode, int period, Set<String> analT1Set) {
        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(accntCode, period, analT1Set);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriods(String accntCode, Quarters qtrFromFy, String fy) {
        int i = 0;
        List<String> per = periods(fy, qtrFromFy);
        // Convert the list of String periods to a list of Integer periods
        List<Integer> intPer = per.stream().map(Integer::parseInt).collect(Collectors.toList());

        return salfldgRepository.findSumOfAmountByAccntCodeAndPeriods(accntCode, intPer);
    }

    public BigDecimal findSumOfAmountByAccntCodeAndPeriods(List<String> accntCode, Quarters qtrFromFy, String fy) {
        int i = 0;
        List<String> per = previousPeriods(fy, qtrFromFy);
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

    public List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1In(int period, String accntCode, List<String> analT1List) {
        List<SALFLDG> nn = salfldgRepository.findByPeriodAndAccntCodeAndAnalT1In(period, accntCode, analT1List);
        for (SALFLDG item : nn) {
        }
        return nn;
    }

    public List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1InZ(int period, String accntCode, List<String> analT1List) {
        List<SALFLDG> nn = salfldgRepository.findByPeriodAndAccntCodeAndAnalT1InZ(period, accntCode, analT1List);

        return nn;
    }

    public List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1In() {
        List<SALFLDG> nn = salfldgRepository.findFixedEntries();
        return nn;
    }

    public List<SALFLDG> findByPeriodAndAccntCodeAndAnalT1InAll() {
        List<SALFLDG> nn = salfldgRepository.findAll();

        return nn;
    }

    public List<SALFLDGProjection> findByPeriodAndAccntCodeAndAnalT1InAllS(int period, String accntCode, List<String> analT1List) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndAccntCodeAndAnalT1InS(period, accntCode, analT1List);

        return nn;
    }

    public List<SALFLDGProjection> findByPeriodAndAccntCode(int period, String accntCode) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndAccntCode(period, accntCode);

        return nn;
    }

    public List<SALFLDGProjection> findByPeriodAndAccntCodeAndAnalT1InAllS2(List<Integer> period, String accntCode, List<String> analT1List) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndAccntCodeAndAnalT1InS2(period, accntCode, analT1List);

        return nn;
    }

    public List<SALFLDGProjection> findByPeriodAndAccntCode2(List<Integer> period, String accntCode) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndAccntCode2(period, accntCode);

        return nn;
    }

    public List<SALFLDGProjection> findExpendituresByPeriodAndSections(Set<Integer> period, Set<String> sections) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndDepartmentExpenditures(period, sections);

        return nn;
    }

    public List<SALFLDGProjection> findByPeriodAndDepartmentExpendituresWithNullSections(Set<Integer> period, Set<String> sections) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndDepartmentExpendituresWithNullSections(period, sections);

        return nn;
    }

    public List<SALFLDGProjection> findExpendituresByPeriodAndSections(Set<Integer> period, String sections) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndDepartmentExpenditures(period, sections);

        return nn;
    }

    public List<SALFLDG> findExpendituresByPeriodAndSectionsExpenditure(Set<Integer> period, String sections) {
        List<SALFLDG> nn = salfldgRepository.findByPeriodAndSectionExpenditures(period, sections);

        return nn;
    }

    public List<SALFLDGProjection> findByPeriodAndExpenditures(List<Integer> period) {
        List<SALFLDGProjection> nn = salfldgRepository.findByPeriodAndTotalExpenditures(period);

        return nn;
    }

    public List<String> periods(String fy, Quarters qtr) {
        int[] years = extractYears(fy);
        List<String> periods = new ArrayList<>();

        if (qtr.equals(Quarters.Qtr1)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d007", y), String.format("%d008", y), String.format("%d009", y)));
        } else if (qtr.equals(Quarters.Qtr2)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)));
        } else if (qtr.equals(Quarters.Qtr3)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d001", y), String.format("%d002", y), String.format("%d003", y)));
        } else if (qtr.equals(Quarters.Qtr4)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d004", y), String.format("%d005", y), String.format("%d006", y)));
        } else if (qtr.equals(Quarters.Total)) {
            for (int y : years) {
                periods.addAll(Arrays.asList(
                        String.format("%d001", y), String.format("%d002", y), String.format("%d003", y),
                        String.format("%d004", y), String.format("%d005", y), String.format("%d006", y),
                        String.format("%d007", y), String.format("%d008", y), String.format("%d009", y),
                        String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)
                ));
            }
        }

        return periods;
    }

    public List<String> previousPeriods(String fy, Quarters qtr) {
        int[] years = extractYears(fy);
        List<String> periods = new ArrayList<>();
        int y = years[1];
        if (qtr.equals(Quarters.Qtr1)) {

            periods.addAll(Arrays.asList(String.format("%d001", y), String.format("%d002", y), String.format("%d003", y)));

        } else if (qtr.equals(Quarters.Qtr2)) {

            periods.addAll(Arrays.asList(String.format("%d004", y), String.format("%d005", y), String.format("%d006", y)));
        } else if (qtr.equals(Quarters.Qtr3)) {

            periods.addAll(Arrays.asList(String.format("%d007", y), String.format("%d008", y), String.format("%d009", y)));
        } else if (qtr.equals(Quarters.Qtr4)) {

            periods.addAll(Arrays.asList(String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)));
        } else if (qtr.equals(Quarters.Total)) {
            for (int x : years) {
                periods.addAll(Arrays.asList(
                        String.format("%d001", y), String.format("%d002", y), String.format("%d003", y),
                        String.format("%d004", y), String.format("%d005", y), String.format("%d006", y),
                        String.format("%d007", y), String.format("%d008", y), String.format("%d009", y),
                        String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)
                ));
            }
        }

        return periods;
    }

    public String getPreviousFy(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        return String.format("FY%d-%d", newfy1, fy1);
    }

    public Budget getPreviousBudget(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        // return String.format("FY%d-%d", newfy1, fy1);
        Optional<Budget> bug = repository.findByFY(String.format("FY%d-%d", newfy1, fy1));
        if (bug.isPresent()) {
            return bug.get();
        } else {
            return null;
        }
    }

    public int[] extractYears(String input) {
        int[] years = new int[2];
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        int i = 0;
        while (matcher.find() && i < 2) {
            years[i++] = Integer.parseInt(matcher.group());
        }

        return years;
    }

    public BigDecimal getTotalAmountByPeriods(Set<Integer> periods, Set<String> analT1Values) {
        BigDecimal amount = salfldgRepository.findTotalAmountByPeriodsAndAnalT1Set(periods, analT1Values);
        return amount.abs(); // always non-null
    }

    public BigDecimal getTotalAmountByPeriods(Set<Integer> periods) {
        BigDecimal amount = salfldgRepository.findTotalAmountByPeriods(periods);
        System.out.println(amount);
        return amount.abs(); // always non-null
    }

    public BigDecimal findTotalAmountByPeriodsAndUnAnalyzed(Set<Integer> periods) {
        BigDecimal amount = salfldgRepository.findTotalAmountByPeriodsAndUnAnalyzed(periods);
        return amount.abs(); // always non-null
    }

    public BigDecimal getTotalCommittedAmountByPeriods(Set<Integer> periods, Set<String> analT1Values) {
        BigDecimal amount = urcCSalFldgRepository.findTotalCommittedAmountByPeriodsAndAnalT1Set(periods, analT1Values);
        return amount; // always non-null
    }

    public Double getTotalCommittedAmountByPeriodsCodeAndSection(Set<Integer> periods, String analT1Values, String accCode) {
        BigDecimal amount = urcCSalFldgRepository.findTotalCommittedAmountByPeriodsCodeAndSection(periods, analT1Values, accCode);
        return amount.doubleValue(); // always non-null
    }

    public Double getTotalCommittedAmountByPeriodsCodeAndSectionAndActivity(Set<Integer> periods, String analT1Values, String accCode, String activityCode) {
        BigDecimal amount = urcCSalFldgRepository.findTotalCommittedAmountByPeriodsCodeAndSectionAndActivity(periods, analT1Values, accCode, activityCode);
        return amount.doubleValue(); // always non-null
    }

    public BigDecimal getTotalAmountByPeriods2(Set<Integer> periods, String analT1Values) {
        BigDecimal amount = salfldgRepository.findTotalAmountByPeriodsAndAnalT1Set(periods, analT1Values);
        return amount.abs(); // always non-null
    }

    public BigDecimal findTotalAmountByPeriodsAndAccntCodes(Set<Integer> periods, Set<String> accno) {
        BigDecimal amount = salfldgRepository.findTotalAmountByPeriodsAndAccntCodes(periods, accno);
        return amount; // always non-null
    }

    public Double getTotalAmountByActivityAndCode(String analT8, String accntCode) {
        return salfldgRepository.sumAmountByAnalT8AndAccntCode(analT8, accntCode);
    }

    public Double getTotalAmountByActivity(String analT8) {
        return salfldgRepository.sumAmountByAnalT8(analT8);
    }

    public BigDecimal calculateTotalByBudgetAndCoaAndActivityAndSectionActuals(Set<Integer> periods, String accCode, String activity, String section) {
        BigDecimal amount = salfldgRepository.calculateTotalByBudgetAndCoaAndActivityAndSectionActuals(periods, accCode, activity, section);
        return amount; // always non-null
    }

    public BigDecimal calculateTotalByBudgetAndCoaAndSectionActuals(Set<Integer> periods, String accCode, String section) {
        BigDecimal amount = salfldgRepository.calculateTotalByBudgetAndCoaAndSectionActuals(periods, accCode, section);
        return amount; // always non-null
    }

    public BigDecimal findTotalAmountByPeriodsAndAccntCode(Budget budget, String accCode) {
        Set<Integer> periods = getFinancialYearPeriods(budget);
        BigDecimal amount = salfldgRepository.findTotalAmountByPeriodsAndAccntCode(periods, accCode);
        return amount; // always non-null
    }

    public BigDecimal findTotalAmountByPeriodsAndPassengerActuals(Budget budget) {
        Set<Integer> periods = getFinancialYearPeriods(budget);
        String accCode = "111601         ";

        BigDecimal total = salfldgRepository.findTotalAmountByPeriodsAndAccntCode(periods, accCode);
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return total.divide(BigDecimal.valueOf(2000), 2, BigDecimal.ROUND_HALF_UP);
    }

    public Set<Integer> getFinancialYearPeriods(Budget budget) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        for (int i = 1; i <= 12; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

    public Set<Integer> getFinancialYearPeriods(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        int start = 0;
        switch (quarter) {
            case 1:
                start = 1;
                break;
            case 2:
                start = 4;
                break;
            case 3:
                start = 7;
                break;
            case 4:
                start = 10;
                break;
            default:
                break;
        }
        for (int i = start; i <= 12; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

    public Set<Integer> getFinancialYearPeriodsCummulative(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        int stop = 0;
        switch (quarter) {
            case 1:
                stop = 3;
                break;
            case 2:
                stop = 6;
                break;
            case 3:
                stop = 9;
                break;
            case 4:
                stop = 12;
                break;
            default:
                break;
        }
        for (int i = 1; i <= stop; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

    public Optional<SALFLDG> findByActualByID(
            String accountCode,
            Integer period,
            LocalDateTime transactionDateTime,
            Integer journalNo,
            Integer journalLine
    ) {
        return salfldgRepository
                .findByAccntCodeAndPeriodAndTransDatetimeAndJrnalNoAndJrnalLine(
                        accountCode, period, transactionDateTime, journalNo, journalLine
                );
    }

    public Set<String> extractTrimmedAnlCodes(Set<UrcDeptSectionAnlDimbgt> records) {
        if (records == null) {
            return Set.of();
        }

        return records.stream()
                .map(UrcDeptSectionAnlDimbgt::getANL_CODE)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toSet());
    }
}
