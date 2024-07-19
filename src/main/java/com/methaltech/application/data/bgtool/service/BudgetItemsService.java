package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.bgtool.repository.BudgetItemsRepository;
import com.methaltech.application.data.bgtool.repository.CoaRepository;
import com.methaltech.application.data.bgtool.repository.FundsourceRepository;
import com.methaltech.application.data.bgtool.repository.UrcDeptSectionAnlDimbgtRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BudgetItemsService {

    private final BudgetItemsRepository repository;
    private final CoaRepository caoRepository;
    private final FundsourceRepository fundsourceRepository;
    private final SALFLDGRepository salfldgRepository;
    private final UrcDeptSectionAnlDimbgtRepository urcDeptSectionAnlDimRepository;
    PeriodExtractor extActuals = new PeriodExtractor();
    private final SALFLDGService sALFLDGService;

    @Autowired
    public BudgetItemsService(BudgetItemsRepository repository, CoaRepository caoRepository,
            FundsourceRepository fundsourceRepository, SALFLDGRepository salfldgRepository,
            UrcDeptSectionAnlDimbgtRepository urcDeptSectionAnlDimRepository,
            SALFLDGService sALFLDGService) {
        this.repository = repository;
        this.caoRepository = caoRepository;
        this.fundsourceRepository = fundsourceRepository;
        this.salfldgRepository = salfldgRepository;
        this.urcDeptSectionAnlDimRepository = urcDeptSectionAnlDimRepository;
        this.sALFLDGService = sALFLDGService;
    }

    public Optional<BudgetItems> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public BudgetItems update(BudgetItems entity) {
        // Fetch the Fundsource entity from the database within the same transactional context
        if (!entity.getCoacode().getCode().startsWith("1")) {
            if (entity.getFundsource() != null) { // Check if fundsource is not null
                Optional<Fundsource> optionalFundsource = fundsourceRepository.findById(entity.getFundsource().getId());

                // Extract the Fundsource entity from the Optional
                Fundsource fundsource = optionalFundsource.orElseThrow(() -> new IllegalArgumentException("Fundsource not found"));

                // Set the fetched Fundsource entity to the BudgetItems entity
                entity.setFundsource(fundsource);
            }
        }
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByAnalcode(Long analcode) {
        repository.deleteByAnalcode(analcode);
    }

    public Page<BudgetItems> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<BudgetItems> findBudgetItemsByCriteria(
            String start,
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {
        return repository.findBudgetItemsByCriteria(start, coalevel1, budgetType, budget, activity, deptUnit);
    }

    public List<BudgetItems> findBudgetItemsByCriteria2(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit, COA coacode
    ) {
        return repository.findBudgetItemsByCriteria2(coalevel1, budgetType, budget, activity, deptUnit, coacode);
    }

    public List<BudgetItems> findByCriteria(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit, COA coacode
    ) {

        return repository.findByCriteria(coalevel1, budgetType, budget, activity, deptUnit, coacode);
    }

    public List<BudgetItems> findByCriteria2(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit, COA coacode
    ) {
        return repository.findByCriteria2(coalevel1, budgetType, budget, deptUnit, coacode);
    }

    public List<BudgetItems> findBudgetItemsByCriteria3(
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {
        return repository.findBudgetItemsByCriteria3(budgetType, budget, activity, deptUnit);
    }

    public BigDecimal calculateSumOfAllMonths(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {
        return repository.calculateSumOfAllMonths(coalevel1, budgetType, budget, activity, deptUnit);
    }

    public BigDecimal sumMonthsByParameters(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit, COA coa
    ) {
        return repository.sumMonthsByParameters(coalevel1, budgetType, budget, activity, deptUnit, coa);
    }

    public BigDecimal sumMonthsByParameters2(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit, COA coa
    ) {
        return repository.sumMonthsByParameters2(coalevel1, budgetType, budget, deptUnit, coa);
    }

    public BigDecimal sumMonthsBudgetAndCode(Budget budget, COA coa) {
        return repository.sumMonthsBudgetAndCode(budget, coa);
    }

    public BigDecimal sumMonthsByActivityBySection(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {
        return repository.sumMonthsByActivityBySection(coalevel1, budgetType, budget, activity, deptUnit);
    }

    public BigDecimal sumMonthsByActivityBySection3(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {
        return repository.sumMonthsByActivityBySection3(coalevel1, budgetType, budget, activity, deptUnit);
    }

    public BigDecimal sumMonthsByActivityBySection4(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            Urc_Activities activity,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {

        return repository.sumMonthsByActivityBySection4(coalevel1, budgetType, budget, activity, deptUnit);
    }

    public BigDecimal sumMonthsBySection4(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {

        return repository.sumMonthsBySection4(coalevel1, budgetType, budget, deptUnit);
    }

    public BigDecimal sumMonthsBySectionTotalBudget(
            Organisation budgetType,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {

        return repository.sumMonthsBySectionTotalBudget(budgetType, budget, deptUnit);
    }

    public BigDecimal sumMonthsByActivityBySection2(
            Coalevel1 coalevel1,
            Organisation budgetType,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit
    ) {
        return repository.sumMonthsByActivityBySection2(coalevel1, budgetType, budget, deptUnit);
    }

    @Transactional
    public void deleteBudgetItem(BudgetItems budgetItem) {
        repository.deleteBudgetItem(budgetItem);
    }

    public List<COA> findDistinctCoacodesByBudgetTypeAndBudgetAndDeptUnit(
            Organisation budgetType,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit,
            Coalevel1 coalevel1,
            Urc_Activities activity
    ) {
        return repository.findDistinctCoacodesByCriteria(budgetType, budget, deptUnit, coalevel1, activity);
    }

    public List<COA> findDistinctCoacodesByBudgetTypeAndBudgetAndDeptUnit(
            Set<Organisation> budgetType,
            Budget budget,
            Set<UrcDeptSectionAnlDimbgt> deptUnit,
            Coalevel1 coalevel1
    ) {
        return repository.findDistinctCoacodesByCriteria(budgetType, budget, deptUnit, coalevel1);
    }

    public List<COA> findDistinctCoacodesByBudgetTypeAndBudgetAndDeptUnit2(
            Organisation budgetType,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit,
            Coalevel1 coalevel1
    ) {
        return repository.findDistinctCoacodesByCriteria2(budgetType, budget, deptUnit, coalevel1);
    }

    public List<BudgetItems> findDistictCodeAndNames(Organisation budgetType, Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Coalevel1 coalevel1, Urc_Activities activity) {
        List<BudgetItems> bgt = new ArrayList();
        List<COA> coaList = findDistinctCoacodesByBudgetTypeAndBudgetAndDeptUnit(budgetType, budget, deptUnit, coalevel1, activity);
        for (COA coa : coaList) {
            BudgetItems bgtItem = new BudgetItems();
            bgtItem.setCoacode(coa);
            bgtItem.setItem(coa.getName());
            bgtItem.setTotal(repository.sumMonthsByOrgAndBudgetAndSection(coalevel1, budgetType, budget, activity, deptUnit, coa));
            bgt.add(bgtItem);
        }
        return bgt;
    }

    public List<BudgetItems> findDistictCodeAndNames(Set<Organisation> budgetType, Budget budget, Set<UrcDeptSectionAnlDimbgt> deptUnit, Coalevel1 coalevel1) {
        List<BudgetItems> bgt = new ArrayList();
        List<COA> coaList = findDistinctCoacodesByBudgetTypeAndBudgetAndDeptUnit(budgetType, budget, deptUnit, coalevel1);
        for (COA coa : coaList) {
            BudgetItems bgtItem = new BudgetItems();
            bgtItem.setCoacode(coa);
            bgtItem.setItem(coa.getName());
            bgtItem.setTotal(repository.sumMonthsByOrgAndBudgetAndSection(coalevel1, budgetType, budget, deptUnit, coa));
            bgt.add(bgtItem);
        }
        return bgt;
    }

    // Method to calculate the sum of total from a list of BudgetItems
    public BigDecimal calculateTotalSum(List<BudgetItems> budgetItemsList) {
        BigDecimal totalSum = BigDecimal.ZERO;
        for (BudgetItems budgetItem : budgetItemsList) {
            totalSum = totalSum.add(budgetItem.getTotal());
        }
        return totalSum;
    }

    public List<BudgetItems> findDistictCodeAndNames2(Organisation budgetType, Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Coalevel1 coalevel1) {
        List<BudgetItems> bgt = new ArrayList();
        List<COA> coaList = findDistinctCoacodesByBudgetTypeAndBudgetAndDeptUnit2(budgetType, budget, deptUnit, coalevel1);
        for (COA coa : coaList) {
            BudgetItems bgtItem = new BudgetItems();
            bgtItem.setCoacode(coa);
            bgtItem.setItem(coa.getName());
            bgtItem.setTotal(repository.sumMonthsByOrgAndBudgetAndSection2(coalevel1, budgetType, budget, deptUnit, coa));
            bgt.add(bgtItem);
        }
        return bgt;
    }

    public List<BudgetItems> findByOrgAndBudgetAndSection3(Set<Organisation> budgetType, Budget budget, Set<UrcDeptSectionAnlDimbgt> deptUnit, Coalevel1 coalevel1, COA coa) {

        return repository.findByOrgAndBudgetAndSection3(coalevel1, budgetType, budget, deptUnit, coa);
    }

    public BudgetItems getBudgetItemsByAnalcode(Long analcode) {
        return repository.findByAnalcode(analcode);
    }

    public BudgetItems findBudgetAndCodeAndAnalcode(Budget budget, COA coacode, Long analcode) {
        return repository.findBudgetAndCodeAndAnalcode(budget, coacode, analcode);
    }

    public BudgetItems findBudgetAndCode(Budget budget, COA coacode) {
        return repository.findBudgetAndCode(budget, coacode);
    }

    public List<BudgetItems> findByBudgetAndCoacode(Budget budget, COA coacode) {
        return repository.findByBudgetAndCoacode(budget, coacode);
    }

    public BudgetItems sumIndividualMonthsBudgetAndCode(Budget budget, COA coacode) {
        List<Object[]> monthSums = repository.sumIndividualMonthsBudgetAndCode(budget, coacode);
        BudgetItems bdgt = new BudgetItems();
        for (Object[] row : monthSums) {
            bdgt.setJan((BigDecimal) row[0]);
            bdgt.setFeb((BigDecimal) row[1]);
            bdgt.setMar((BigDecimal) row[2]);
            bdgt.setApr((BigDecimal) row[3]);
            bdgt.setMay((BigDecimal) row[4]);
            bdgt.setJun((BigDecimal) row[5]);
            bdgt.setJul((BigDecimal) row[6]);
            bdgt.setAug((BigDecimal) row[7]);
            bdgt.setSep((BigDecimal) row[8]);
            bdgt.setOct((BigDecimal) row[9]);
            bdgt.setNov((BigDecimal) row[10]);
            bdgt.setDec((BigDecimal) row[11]);
        }
        return bdgt;
    }

    public BigDecimal calculateTotalByBudgetAndActivityAndDeptUnits(
            Budget budget,
            Urc_Activities activity,
            List<UrcDeptSectionAnlDimbgt> deptUnits
    ) {
        return repository.calculateTotalByBudgetAndActivityAndDeptUnits(budget, activity, deptUnits);
    }

    public boolean isItemWithCoacodeExists(COA coacode, Budget budget) {
        return repository.existsByCoacodeAndBudget(coacode, budget);
    }

    public void deleteByCoacode(COA coacode, Budget budget) {
        if (repository.existsByCoacodeAndBudget(coacode, budget)) {
            repository.deleteByCoacodeAndBudget(coacode, budget);
        } else {
            // Handle the case where the item with the specified coacode doesn't exist
            // You can throw an exception or handle it based on your application logic
        }
    }

    public List<BudgetItems> findFirst1ByCoacodeAndBudget(COA coacode, Budget budget) {
        Page<BudgetItems> resultPage = repository.findFirst1ByCoacodeAndBudget(coacode, budget, PageRequest.of(0, 1));
        return resultPage.getContent();
    }

    public BudgetItems findFirst1ByCoacodeAndBudgetBudgetItems(COA coacode, Budget budget) {
        Page<BudgetItems> resultPage = repository.findFirst1ByCoacodeAndBudget(coacode, budget, PageRequest.of(0, 1));
        //BudgetItems bud=resultPage.getContent().get(0);
        //return resultPage.getContent().get(0);
        if (!resultPage.isEmpty()) {
            return resultPage.getContent().get(0);
        } else {
            // Handle the case where the list is empty (no matching items)
            // You can throw an exception or handle it based on your application logic
            return null;  // Or another appropriate response
        }
    }

    // Method to find BudgetItems by deptUnit and budget
    public List<BudgetItems> findByDeptUnitAndBudget(UrcDeptSectionAnlDimbgt deptUnit, Budget budget) {
        return repository.findByDeptUnitAndBudget(deptUnit, budget);
    }

    public List<BudgetItems> findByAll() {
        return repository.findAll();
    }

    public BigDecimal findSumByBudgetCoalevel1AndDeptUnits(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Set<Organisation> budgetType) {
        BigDecimal sum = repository.findSumByBudgetCoalevel1AndDeptUnits(budget, coalevel1, deptUnits, budgetType);
        return sum;
    }

    public BigDecimal findSumByBudgetCoalevel1AndDeptUnitsTotal(Budget budget, List<Coalevel1> coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Set<Organisation> budgetType) {
        BigDecimal sum = repository.findSumByBudgetCoalevel1AndDeptUnitsTotal(budget, coalevel1, deptUnits, budgetType);
        return sum;
    }

    public BigDecimal findSumByBudgetCOA(Budget budget, COA coa) {
        BigDecimal sum = repository.findSumByBudgetCOA(budget, coa);
        return sum;
    }

    public BigDecimal findSumByBudgetCoalevel1AndDeptUnitsAndActivity(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Urc_Activities activity, Set<Organisation> budgetType) {
        BigDecimal sum = repository.findSumByBudgetCoalevel1AndDeptUnitsAndActivity(budget, coalevel1, deptUnits, activity, budgetType);
        return sum;
    }

    public boolean isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Set<Organisation> budgetType) {
        return repository.isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(budget, coalevel1, deptUnits, budgetType);
    }

    public List<BudgetItems> findByBudgetCoalevel1AndDeptUnits(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Set<Organisation> budgetType) {
        return repository.findByBudgetCoalevel1AndDeptUnits(budget, coalevel1, deptUnits, budgetType);
    }

    public List<BudgetItems> findByBudgetCoalevel1AndDeptUnitsAndActivities(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, List<Urc_Activities> activities, Set<Organisation> budgetType) {
        return repository.findByBudgetCoalevel1AndDeptUnitsAndActivities(budget, coalevel1, deptUnits, activities, budgetType);
    }

    public boolean isSumBudgetCoalevel1AndDeptUnitsAndActivitiesGreaterThanZero(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, List<Urc_Activities> activities, Set<Organisation> budgetType) {
        return repository.isSumBudgetCoalevel1AndDeptUnitsAndActivitiesGreaterThanZero(budget, coalevel1, deptUnits, activities, budgetType);
    }

    public boolean isSumBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Urc_Activities activities, Set<Organisation> budgetType) {
        return repository.isSumBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(budget, coalevel1, deptUnits, activities, budgetType);
    }

    public List<BudgetItems> findBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Urc_Activities activities, Set<Organisation> budgetType) {
        return repository.findBudgetCoalevel1AndDeptUnitsAndActivityGreaterThanZero(budget, coalevel1, deptUnits, activities, budgetType);
    }

    public BigDecimal findSumOfMonthByBudgetCoalevel1AndDeptUnitsAndActivity(
            Budget budget,
            Coalevel1 coalevel1,
            List<UrcDeptSectionAnlDimbgt> deptUnits,
            Urc_Activities activity,
            Set<Organisation> budgetTypes,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetCoalevel1AndDeptUnitsAndActivity(
                        budget, coalevel1, deptUnits, activity, budgetTypes);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;

                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    public BigDecimal findSumOfIndividualMonthsByBudgetAndCoacodeFreight(
            Budget budget,
            Display display,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetAndCoacodeFreight(
                        budget, display);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;
                case "qtr1":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum())
                            .add(getSafeValue(monthlySumResults.get(0).getAugSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getSepSum()));
                    break;
                case "qtr2":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum())
                            .add(getSafeValue(monthlySumResults.get(0).getNovSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getDecSum()));
                    break;
                case "qtr3":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum())
                            .add(getSafeValue(monthlySumResults.get(0).getFebSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getMarSum()));
                    break;
                case "qtr4":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum())
                            .add(getSafeValue(monthlySumResults.get(0).getMaySum()))
                            .add(getSafeValue(monthlySumResults.get(0).getJunSum()));
                    break;
                case "total":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum())
                            .add(getSafeValue(monthlySumResults.get(0).getAugSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getSepSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getOctSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getNovSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getDecSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getJanSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getFebSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getMarSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getAprSum()))
                            .add(getSafeValue(monthlySumResults.get(0).getMaySum()))
                            .add(getSafeValue(monthlySumResults.get(0).getJunSum()));
                    break;
                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    public BigDecimal findSumOfMonthByBudgetCoalevel1AndDeptUnits(
            Budget budget,
            Coalevel1 coalevel1,
            List<UrcDeptSectionAnlDimbgt> deptUnits,
            Set<Organisation> budgetTypes,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetCoalevel1AndDeptUnits(
                        budget, coalevel1, deptUnits, budgetTypes);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;

                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    public BigDecimal findSumOfMonthByBudgetCoalevel1AndDeptUnitsTotal(
            Budget budget,
            List<Coalevel1> coalevel1,
            List<UrcDeptSectionAnlDimbgt> deptUnits,
            Set<Organisation> budgetTypes,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetCoalevel1AndDeptUnitsTotal(
                        budget, coalevel1, deptUnits, budgetTypes);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;

                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    public BigDecimal findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
            Budget budget,
            COA coalevel1,
            List<UrcDeptSectionAnlDimbgt> deptUnits,
            Set<Organisation> budgetTypes,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                        budget, coalevel1, deptUnits, budgetTypes);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;

                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    private BigDecimal getSafeValue(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public List<COA> findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits, Set<Organisation> budgetType) {
        return repository.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(budget, coalevel1, deptUnits, budgetType);
    }

    public List<BudgetItems> findByBudgetCoacodeAndDeptUnits(Budget budget, COA coacode, List<UrcDeptSectionAnlDimbgt> deptUnits, Set<Organisation> budgetType) {
        return repository.findByBudgetCoacodeAndDeptUnits(budget, coacode, deptUnits, budgetType);
    }

    public BigDecimal findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(Budget budget, COA coa, List<UrcDeptSectionAnlDimbgt> deptUnits, Set<Organisation> budgetType) {
        BigDecimal sum = repository.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(budget, coa, deptUnits, budgetType);
        return sum;
    }

    public BigDecimal sumActvitySummation(Set<Organisation> budgetTypes,
            Budget budget,
            Urc_Activities activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits,
            Integer coalevel1Code) {
        BigDecimal sum = repository.sumActvitySummation(budgetTypes, budget, activities, deptUnits, coalevel1Code);
        return sum;
    }

    public boolean isSumProgrammeGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            List<Urc_Activities> activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits,
            Integer coalevel1Code
    ) {
        return repository.isSumProgrammeGreaterThanZero(budgetTypes, budget, activities, deptUnits, coalevel1Code);
    }

    public boolean isSumActvityGreaterThanZero(
            Set<Organisation> budgetTypes,
            Budget budget,
            Urc_Activities activities,
            Set<UrcDeptSectionAnlDimbgt> deptUnits,
            Integer coalevel1Code
    ) {
        return repository.isSumActvityGreaterThanZero(budgetTypes, budget, activities, deptUnits, coalevel1Code);
    }

    public BigDecimal findSumOfIndividualMonthsByBudgetCoalevel1Activity(
            Budget budget,
            Integer coalevel1,
            Set<UrcDeptSectionAnlDimbgt> deptUnits,
            Urc_Activities activity,
            Set<Organisation> budgetTypes,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetCoalevel1Activity(
                        budget, coalevel1, deptUnits, activity, budgetTypes);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;

                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    public BigDecimal findSumOfIndividualMonthsByBudgetCoa(
            Budget budget,
            List<COA> coa,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetCoa(
                        budget, coa);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;

                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    public BigDecimal totalMonthsByProcClass(Budget budget, ProcClass p) {
        List<COA> coacode = caoRepository.findByBudgetAndProcclass(budget, p);
        BigDecimal sumOfTotalMonths = repository.findSumOfTotalMonthsByBudgetCoa(budget, coacode);
        return sumOfTotalMonths != null ? sumOfTotalMonths : BigDecimal.ZERO;
    }

    public List<BudgetItems> findBudgetItemsByUrc_Activities(Set<Organisation> budgetType, Budget budget, Urc_Activities activities, Set<UrcDeptSectionAnlDimbgt> deptUnits, Integer coalevel1Code) {
        List<BudgetItems> result = repository.findBudgetItemsByUrc_Activities(budgetType, budget, activities, deptUnits, coalevel1Code);
        return result != null ? result : Collections.emptyList();
    }
    // Method to fetch BudgetItems by Budget and Set of COA codes

    public List<BudgetItems> getBudgetItemsByBudgetAndCoacodes(Budget budget, Set<COA> coacodes) {
        List<BudgetItems> result = repository.findByBudgetAndCoacodeIn(budget, coacodes);
        return result != null ? result : Collections.emptyList();
    }

    public List<BudgetItems> getBudgetItemsByBudgetAndCoacode(Budget budget, COA coacode) {
        List<BudgetItems> result = repository.findByBudgetAndCoacode(budget, coacode);
        return result != null ? result : Collections.emptyList();
    }

    public List<BudgetItems> findByCoacodeCodeStartingWith2Or3() {
        return repository.findByCoacodeCodeStartingWith2Or3();
    }

    public BigDecimal sumOfAllMonthsByBudgetAndProcClassAndCoa(Budget budget, ProcClass p, COA coacode) {
        return repository.sumOfAllMonthsByBudgetAndProcClassAndCoa(budget, p, coacode);
    }

    public BigDecimal sumOfAllMonthsByBudgetAndCoa(Budget budget, COA coacode, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        return repository.sumOfAllMonthsByBudgetAndCoa(budget, coacode, deptUnits);
    }

    public BigDecimal sumOfAllMonthsByBudgetAndCoa(Budget budget, COA coacode) {
        return repository.sumOfAllMonthsByBudgetAndCoa(budget, coacode);
    }

    public BigDecimal sumOfAllMonthsByBudgetAndProcClassAndCoa(Budget budget, ProcClass p, COA coacode, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        return repository.sumOfAllMonthsByBudgetAndProcClassAndCoa(budget, p, coacode, deptUnits);
    }

    public List<BudgetItems> findByBudgetAndProcClassAndCoa(Budget budget, ProcClass p, COA coacode) {
        List<BudgetItems> result = repository.findByBudgetAndProcClassAndCoa(budget, p, coacode);
        return result != null ? result : Collections.emptyList();
    }

    public List<BudgetItems> findByBudgetAndProcClassAndCoaAndDeptUnitIn(Budget budget, ProcClass p, COA coacode, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        List<BudgetItems> result = repository.findByBudgetAndProcClassAndCoaAndDeptUnitIn(budget, p, coacode, deptUnits);
        return result != null ? result : Collections.emptyList();
    }

    public List<BudgetItems> findByBudgetAndProcClassAndCoaAndDeptUnitIn(Budget budget, ProcClass p, Set<COA> coacode, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        List<BudgetItems> result = repository.findByBudgetAndProcClassAndCoaAndDeptUnitIn(budget, p, coacode, deptUnits);
        return result != null ? result : Collections.emptyList();
    }

    public Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacode(Budget budget, ProcClass p, COA coacode) {
        return repository.findDistinctFundSourcesByBudgetAndProcClassAndCoacode(budget, p, coacode);
    }

    public Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacodeF(Budget budget, ProcClass p, COA coacode, Set<Fundsource> fundsource) {
        return repository.findDistinctFundSourcesByBudgetAndProcClassAndCoacodeAndFundsourceIn(budget, p, coacode, fundsource);
    }

    public Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacode(Budget budget, ProcClass p, COA coacode, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        return repository.findDistinctFundSourcesByBudgetAndProcClassAndCoacodeAndDeptIn(budget, p, coacode, deptUnits);
    }

    public Set<String> findDistinctFundSourcesByBudgetAndProcClassAndCoacode(Budget budget, ProcClass p, COA coacode, Set<UrcDeptSectionAnlDimbgt> deptUnits, Set<Fundsource> fundsource) {
        return repository.findDistinctFundSourcesByBudgetAndProcClassAndCoacodeAndDeptInAndFundsourceIn(budget, p, coacode, deptUnits, fundsource);
    }

    public Set<Fundsource> findDistinctFundSources2ByBudgetAndProcClassAndCoacode(Budget budget, ProcClass p, COA coacode) {
        return repository.findDistinctFundSources2ByBudgetAndProcClassAndCoacode(budget, p, coacode);
    }

    public List<BudgetItems> findByBudgetAndProcClassAndCoaAndFundsourceIn(Budget budget, ProcClass p, COA coacode, Set<Fundsource> fundsource) {
        List<BudgetItems> result = repository.findByBudgetAndProcClassAndCoaAndFundsourceIn(budget, p, coacode, fundsource);
        return result != null ? result : Collections.emptyList();
    }

    public List<BudgetItems> findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(Budget budget, ProcClass p, COA coacode, Set<UrcDeptSectionAnlDimbgt> deptUnits, Set<Fundsource> fundsource) {
        List<BudgetItems> result = repository.findByBudgetAndProcClassAndCoaAndDeptUnitInAndFundsSourceIn(budget, p, coacode, deptUnits, fundsource);

        return result != null ? result : Collections.emptyList();
    }

    public List<BudgetItemsActuals> findDistinctBudgetItemses(Budget budget, Set<UrcDeptSectionAnlDimbgt> deptUnits) {

        Set<String> sctions = new HashSet<>();
        for (UrcDeptSectionAnlDimbgt sects : deptUnits) {
            sctions.add(sects.getANL_CODE());

        }

        List<COA> coaList = repository.findDistinctCoacodeByBudgetAndDeptUnitIn(budget, deptUnits);

        List<COA> coaList2 = sALFLDGService.listOFCoa(sctions.stream().toList(), listAllPeriod(budget), budget);

        // Combine the two lists into a single list
        List<COA> combinedList = Stream.concat(coaList.stream(), coaList2.stream())
                .collect(Collectors.toList());

        // Convert the list to a set to remove duplicates
        Set<COA> combinedSet = combinedList.stream().collect(Collectors.toSet());

        // Sort the set in ascending order
        //List<COA> finalListWithoutDuplicates = combinedSet.stream().sorted(Comparator.comparing(COA::getCode)).collect(Collectors.toList());
        List<COA> finalListWithoutDuplicates = combinedSet.stream()
                .filter(Objects::nonNull) // Filter out null values
                .sorted(Comparator.comparing(
                        coa -> Optional.ofNullable(coa.getCode()).orElse("")) // Handle null codes
                ).collect(Collectors.toList());
        UrcDeptSectionAnlDimbgt freightAnlDimbgt = urcDeptSectionAnlDimRepository.findByCustomANL_CODE("S020");
        UrcDeptSectionAnlDimbgt propertymgt = urcDeptSectionAnlDimRepository.findByCustomANL_CODE("S004");
        List<BudgetItemsActuals> budgetItemses = new ArrayList<>();
        PeriodExtractor extActuals = new PeriodExtractor();
        for (COA c : finalListWithoutDuplicates) {
            BudgetItemsActuals b = new BudgetItemsActuals();

            b.setBudget(budget);
            b.setItem(c.getName());
            b.setCoacode(c);
            b.setDeptUnit(deptUnits);
            if (deptUnits.contains(freightAnlDimbgt) && (c.getDisplay() == Display.FREIGHT || c.getCode().contains("111109") || c.getCode().contains("111110"))) {
                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jul")));
                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Aug")));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Sep")));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Oct")));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Nov")));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Dec")));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jan")));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Feb")));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Mar")));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Apr")));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "May")));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jun")));
            } else if (deptUnits.contains(propertymgt) && (c.getCode().contains("111401") || c.getCode().contains("111402") || c.getCode().contains("111403") || c.getCode().contains("111404") || c.getCode().contains("111406") || c.getCode().contains("111407"))) {

                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jul")));
                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Aug")));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Sep")));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Oct")));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Nov")));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Dec")));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jan")));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Feb")));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Mar")));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Apr")));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "May")));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jun")));
            } else {
                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jul"), sctions));
                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Aug"), sctions));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Sep"), sctions));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Oct"), sctions));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Nov"), sctions));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Dec"), sctions));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jan"), sctions));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Feb"), sctions));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Mar"), sctions));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Apr"), sctions));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "May"), sctions));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generateCurrentPeriod(budget.getFinancialYear(), "Jun"), sctions));
            }
            b.setTotal(repository.sumOfAllMonthsByBudgetAndCoa(budget, c, deptUnits));
            b.setJul(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jul"));
            b.setAug(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "aug"));
            b.setSep(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "sep"));
            b.setOct(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "oct"));
            b.setNov(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "nov"));
            b.setDec(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "dec"));
            b.setJan(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jan"));
            b.setFeb(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "feb"));
            b.setMar(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "mar"));
            b.setApr(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "apr"));
            b.setMay(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "may"));
            b.setJun(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jun"));
            b.setQtr1(b.getJul().add(b.getAug().add(b.getSep())));
            b.setQtr1A(b.getJulA().add(b.getAugA().add(b.getSepA())));
            b.setQtr2(b.getOct().add(b.getNov().add(b.getDec())));
            b.setQtr2A(b.getOctA().add(b.getNovA().add(b.getDecA())));
            b.setQtr3(b.getJan().add(b.getFeb().add(b.getMar())));
            b.setQtr3A(b.getJanA().add(b.getFebA().add(b.getMarA())));
            b.setQtr4(b.getApr().add(b.getMay().add(b.getJun())));
            b.setQtr4A(b.getAprA().add(b.getMayA().add(b.getJunA())));
            b.setTotalA(b.getQtr1A().add(b.getQtr2A().add(b.getQtr3A().add(b.getQtr4A()))));

            budgetItemses.add(b);
        }
        return budgetItemses;
    }

    public List<BudgetItemsActuals> findDistinctBudgetItemses2(Budget budget, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        Set<String> sctions = new HashSet<>();
        for (UrcDeptSectionAnlDimbgt sects : deptUnits) {
            sctions.add(sects.getANL_CODE());

        }
        List<COA> coaList = repository.findDistinctCoacodeByBudgetAndDeptUnitIn(budget, deptUnits);
        UrcDeptSectionAnlDimbgt freightAnlDimbgt = urcDeptSectionAnlDimRepository.findByCustomANL_CODE("S020");
        UrcDeptSectionAnlDimbgt propertymgt = urcDeptSectionAnlDimRepository.findByCustomANL_CODE("S004");
        List<BudgetItemsActuals> budgetItemses = new ArrayList<>();
        for (COA c : coaList) {
            BudgetItemsActuals b = new BudgetItemsActuals();

            b.setBudget(budget);
            b.setItem(c.getName());
            b.setCoacode(c);
            b.setDeptUnit(deptUnits);

            if (deptUnits.contains(freightAnlDimbgt) && (c.getDisplay() == Display.FREIGHT || c.getCode().contains("111109") || c.getCode().contains("111110"))) {
                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jul")));
                // b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndMonthAndYearAndPeriod(c.getCode(),Month.JULY.getValue(),budget.getStartDate().getYear()-1,extActuals.generateCode2(budget.getFinancialYear(), "Jul")));
                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Aug")));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Sep")));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Oct")));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Nov")));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Dec")));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jan")));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Feb")));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Mar")));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Apr")));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "May")));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jun")));
            } else if (deptUnits.contains(propertymgt) && (c.getCode().contains("111401") || c.getCode().contains("111402") || c.getCode().contains("111403") || c.getCode().contains("111404") || c.getCode().contains("111406") || c.getCode().contains("111407"))) {

                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jul")));
                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Aug")));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Sep")));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Oct")));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Nov")));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Dec")));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jan")));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Feb")));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Mar")));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Apr")));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "May")));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jun")));
            } else {
                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jul"), sctions));

                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Aug"), sctions));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Sep"), sctions));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Oct"), sctions));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Nov"), sctions));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Dec"), sctions));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jan"), sctions));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Feb"), sctions));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Mar"), sctions));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Apr"), sctions));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "May"), sctions));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jun"), sctions));
            }
            b.setTotal(repository.sumOfAllMonthsByBudgetAndCoa(budget, c, deptUnits));
            b.setJul(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jul"));
            b.setAug(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "aug"));
            b.setSep(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "sep"));
            b.setOct(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "oct"));
            b.setNov(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "nov"));
            b.setDec(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "dec"));
            b.setJan(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jan"));
            b.setFeb(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "feb"));
            b.setMar(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "mar"));
            b.setApr(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "apr"));
            b.setMay(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "may"));
            b.setJun(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jun"));
            b.setQtr1(b.getJul().add(b.getAug().add(b.getSep())));
            b.setQtr1A(b.getJulA().add(b.getAugA().add(b.getSepA())));
            b.setQtr2(b.getOct().add(b.getNov().add(b.getDec())));
            b.setQtr2A(b.getOctA().add(b.getNovA().add(b.getDecA())));
            b.setQtr3(b.getJan().add(b.getFeb().add(b.getMar())));
            b.setQtr3A(b.getJanA().add(b.getFebA().add(b.getMarA())));
            b.setQtr4(b.getApr().add(b.getMay().add(b.getJun())));
            b.setQtr4A(b.getAprA().add(b.getMayA().add(b.getJunA())));
            b.setTotalA(b.getQtr1A().add(b.getQtr2A().add(b.getQtr3A().add(b.getQtr4A()))));

            budgetItemses.add(b);
        }
        return budgetItemses;
    }

    public List<BudgetItemsActuals> findDistinctBudgetItemses22(Budget budget, Set<UrcDeptSectionAnlDimbgt> deptUnits) {
        Set<String> sctions = new HashSet<>();
        for (UrcDeptSectionAnlDimbgt sects : deptUnits) {
            sctions.add(sects.getANL_CODE());

        }
        List<COA> coaList = repository.findDistinctCoacodeByBudgetAndDeptUnitIn(budget, deptUnits);

        List<COA> coaList2 = sALFLDGService.listOFCoa(sctions.stream().toList(), listAllPeriod(budget), budget);

        // Combine the two lists into a single list
        List<COA> combinedList = Stream.concat(coaList.stream(), coaList2.stream())
                .collect(Collectors.toList());

        // Convert the list to a set to remove duplicates
        Set<COA> combinedSet = combinedList.stream().collect(Collectors.toSet());

        // Sort the set in ascending order
        List<COA> finalListWithoutDuplicates = combinedSet.stream()
                .filter(coa -> coa != null && coa.getCode() != null) // Filter out null COA objects and null codes
                .sorted(Comparator.comparing(COA::getCode))
                .collect(Collectors.toList());

        UrcDeptSectionAnlDimbgt freightAnlDimbgt = urcDeptSectionAnlDimRepository.findByCustomANL_CODE("S020");
        UrcDeptSectionAnlDimbgt propertymgt = urcDeptSectionAnlDimRepository.findByCustomANL_CODE("S004");
        List<BudgetItemsActuals> budgetItemses = new ArrayList<>();

        for (COA c : finalListWithoutDuplicates) {
            BudgetItemsActuals b = new BudgetItemsActuals();

            b.setBudget(budget);
            b.setItem(c.getName());
            b.setCoacode(c);
            b.setDeptUnit(deptUnits);

            if (deptUnits.contains(freightAnlDimbgt) && (c.getDisplay() == Display.FREIGHT || c.getCode().contains("111109") || c.getCode().contains("111110"))) {
                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jul")));
                // b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndMonthAndYearAndPeriod(c.getCode(),Month.JULY.getValue(),budget.getStartDate().getYear()-1,extActuals.generateCode2(budget.getFinancialYear(), "Jul")));
                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Aug")));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Sep")));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Oct")));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Nov")));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Dec")));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jan")));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Feb")));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Mar")));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Apr")));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "May")));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jun")));
            } else if (deptUnits.contains(propertymgt) && (c.getCode().contains("111401") || c.getCode().contains("111402") || c.getCode().contains("111403") || c.getCode().contains("111404") || c.getCode().contains("111406") || c.getCode().contains("111407"))) {

                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jul")));
                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Aug")));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Sep")));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Oct")));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Nov")));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Dec")));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jan")));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Feb")));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Mar")));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Apr")));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "May")));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriod(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jun")));
            } else {
                b.setJulA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jul"), sctions));

                b.setAugA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Aug"), sctions));
                b.setSepA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Sep"), sctions));
                b.setOctA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Oct"), sctions));
                b.setNovA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Nov"), sctions));
                b.setDecA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Dec"), sctions));
                b.setJanA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jan"), sctions));
                b.setFebA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Feb"), sctions));
                b.setMarA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Mar"), sctions));
                b.setAprA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Apr"), sctions));
                b.setMayA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "May"), sctions));
                b.setJunA(salfldgRepository.findSumOfAmountByAccntCodeAndPeriodAndAnalT1In(c.getCode(), extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jun"), sctions));
            }
            b.setTotal(repository.sumOfAllMonthsByBudgetAndCoa(budget, c, deptUnits));
            b.setJul(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jul"));
            b.setAug(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "aug"));
            b.setSep(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "sep"));
            b.setOct(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "oct"));
            b.setNov(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "nov"));
            b.setDec(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "dec"));
            b.setJan(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jan"));
            b.setFeb(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "feb"));
            b.setMar(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "mar"));
            b.setApr(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "apr"));
            b.setMay(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "may"));
            b.setJun(findSumOfIndividualMonthsByBudgetCoaDepts(budget, c, deptUnits, "jun"));
            b.setQtr1(b.getJul().add(b.getAug().add(b.getSep())));
            b.setQtr1A(b.getJulA().add(b.getAugA().add(b.getSepA())));
            b.setQtr2(b.getOct().add(b.getNov().add(b.getDec())));
            b.setQtr2A(b.getOctA().add(b.getNovA().add(b.getDecA())));
            b.setQtr3(b.getJan().add(b.getFeb().add(b.getMar())));
            b.setQtr3A(b.getJanA().add(b.getFebA().add(b.getMarA())));
            b.setQtr4(b.getApr().add(b.getMay().add(b.getJun())));
            b.setQtr4A(b.getAprA().add(b.getMayA().add(b.getJunA())));
            b.setTotalA(b.getQtr1A().add(b.getQtr2A().add(b.getQtr3A().add(b.getQtr4A()))));

            budgetItemses.add(b);
        }
        return budgetItemses;
    }

    public BigDecimal findSumOfIndividualMonthsByBudgetCoaDepts(
            Budget budget,
            COA coacode,
            Set<UrcDeptSectionAnlDimbgt> deptUnits,
            String month) {

        List<BudgetItemsRepository.MonthlySumResult> monthlySumResults
                = repository.findSumOfIndividualMonthsByBudgetCoaDept(
                        budget, coacode, deptUnits);

        // Extract sum of the specified month from the first result (assuming there's only one result)
        BigDecimal sumOfMonth = BigDecimal.ZERO;
        if (!monthlySumResults.isEmpty()) {
            switch (month.toLowerCase()) {
                case "jul":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJulSum());
                    break;
                case "aug":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAugSum());
                    break;
                case "sep":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getSepSum());
                    break;
                case "oct":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getOctSum());
                    break;
                case "nov":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getNovSum());
                    break;
                case "dec":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getDecSum());
                    break;
                case "jan":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJanSum());
                    break;
                case "feb":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getFebSum());
                    break;
                case "mar":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMarSum());
                    break;
                case "apr":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getAprSum());
                    break;
                case "may":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getMaySum());
                    break;
                case "jun":
                    sumOfMonth = getSafeValue(monthlySumResults.get(0).getJunSum());
                    break;

                // Default to zero if the specified month is not found
                default:
                    sumOfMonth = BigDecimal.ZERO;
            }
        }

        return sumOfMonth;
    }

    public MonthlySumResponseFreight getTotals(Budget budget, COA coacode) {
        return repository.getMonthlySumsByBudgetAndCoacode(budget, coacode);
    }

    public MonthlySumResponseFreight getTotals(Budget budget, List<COA> coacode) {
        return repository.getMonthlySumsByBudgetAndCoacodes(budget, coacode);
    }

    public List<BudgetItems> findByBudgetAndCoalevel1(Budget budget, Coalevel1 coalevel1) {
        List<BudgetItems> result = repository.findByBudgetAndCoalevel1(budget, coalevel1);
        return result != null ? result : Collections.emptyList();
    }

    public List<Integer> listAllPeriod(Budget budget) {
        List<Integer> list = new ArrayList<>();
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jul"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Aug"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Sep"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Oct"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Nov"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Dec"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jan"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Feb"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Mar"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Apr"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "May"));
        list.add(extActuals.generatePreviousPeriod(budget.getFinancialYear(), "Jun"));

        return list;
    }

    public List<BudgetItems> findBudgetItemsByBudgetAndCoaAndSectios(Budget budget, COA coa, Set<UrcDeptSectionAnlDimbgt> deptUnit, String month) {
        List<BudgetItems> result = repository.findBudgetItemsByBudgetAndCoaAndSectios(budget, coa, deptUnit);
        List<BudgetItems> resultList = new ArrayList<>();
        for (BudgetItems projection : result) {
            switch (month) {
                case "Jul":
                    if (projection.getJul().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }
                    break;
                case "Aug":
                    if (projection.getAug().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }
                    break;
                case "Sep":
                    if (projection.getSep().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Oct":
                    if (projection.getOct().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Nov":
                    if (projection.getNov().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Dec":
                    if (projection.getDec().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Jan":
                    if (projection.getJan().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Feb":
                    if (projection.getFeb().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Mar":
                    if (projection.getMar().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Apr":
                    if (projection.getApr().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "May":
                    if (projection.getMay().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }

                    break;
                case "Jun":
                    if (projection.getJun().compareTo(BigDecimal.ZERO) > 0) {
                        resultList.add(projection);
                    }
                    break;
                case "Total":

                    resultList.add(projection);
                    break;

                default:
                    break;
            }
        }
        return result != null ? resultList : Collections.emptyList();
    }
}
