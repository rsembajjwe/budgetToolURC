package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.SalaryCeilingEnforcementMode;
import com.methaltech.application.data.SalaryGradeCeilingType;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.SalaryGradeCeiling;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.salaryScale;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffSalaryBudgetService {

    private static final String SALARY_WAGES_CODE = "211101";
    private static final String NSSF_CODE = "212101";
    private static final String GRATUITY_CODE = "213004";
    private static final String WORKMAN_COMPENSATION_CODE = "213005";
    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");
    private static final BigDecimal NSSF_RATE = new BigDecimal("0.10");
    private static final BigDecimal GRATUITY_RATE = new BigDecimal("0.25");
    private static final BigDecimal WORKMAN_COMPENSATION_RATE = new BigDecimal("0.03");

    private final CoaService coaService;
    private final CurrencyService currencyService;
    private final Coalevel1Service coalevel1Service;
    private final BudgetItemsService budgetItemsService;
    private final StaffSalaryService staffSalaryService;
    private final SalaryGradeCeilingService salaryGradeCeilingService;

    public StaffSalaryBudgetService(CoaService coaService, CurrencyService currencyService,
            Coalevel1Service coalevel1Service, BudgetItemsService budgetItemsService,
            StaffSalaryService staffSalaryService, SalaryGradeCeilingService salaryGradeCeilingService) {
        this.coaService = coaService;
        this.currencyService = currencyService;
        this.coalevel1Service = coalevel1Service;
        this.budgetItemsService = budgetItemsService;
        this.staffSalaryService = staffSalaryService;
        this.salaryGradeCeilingService = salaryGradeCeilingService;
    }

    @Transactional
    public SalaryBudgetRegenerationResult regenerateSelectedContext(Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType, Urc_Activities activity,
            Fundsource fundsource) {
        return regenerateSelectedContext(budget, deptUnit, budgetType, activity, fundsource,
                SalaryCeilingEnforcementMode.SOFT);
    }

    @Transactional
    public SalaryBudgetRegenerationResult regenerateSelectedContext(Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType, Urc_Activities activity,
            Fundsource fundsource, SalaryCeilingEnforcementMode enforcementMode) {
        validateContext(budget, deptUnit, budgetType, activity, fundsource);

        SalaryBudgetPreview preview = previewSelectedContext(budget, deptUnit, budgetType, activity, fundsource);
        int overCeilingRows = preview.overCeilingRows();
        if (enforcementMode == SalaryCeilingEnforcementMode.HARD && overCeilingRows > 0) {
            throw new IllegalStateException("Salary budget exceeds grade ceiling for " + overCeilingRows
                    + " grade(s). Switch to Soft enforcement or adjust salaries/ceilings before generating.");
        }

        SalaryBudgetSetup setup = loadSalaryBudgetSetup(budget);
        deleteSelectedSalaryBudgetItems(budget, deptUnit, budgetType, activity, fundsource, setup.salaryCoas());

        List<StaffSalary> salaries = staffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, deptUnit, budgetType, activity);
        List<StaffSalary> aggregateSalaryByGrade = staffSalaryService.aggregateSalaryByGrade(salaries);

        BigDecimal monthlyTotal = BigDecimal.ZERO;
        for (StaffSalary aggregate : aggregateSalaryByGrade) {
            aggregate.setBudget(budget);
            BudgetItems salaryItem = createSalaryBudgetItem(aggregate, setup.salaryWages(), setup.currency(),
                    setup.coaLevel1(), budget, deptUnit, budgetType, activity, fundsource);
            budgetItemsService.update(salaryItem);
            monthlyTotal = monthlyTotal.add(aggregate.getSalary());
        }

        if (monthlyTotal.compareTo(BigDecimal.ZERO) > 0) {
            budgetItemsService.update(createBenefitBudgetItem(setup.nssf(), setup.currency(), setup.coaLevel1(),
                    monthlyTotal, NSSF_RATE, budget, deptUnit, budgetType, activity, fundsource));
            budgetItemsService.update(createBenefitBudgetItem(setup.gratuity(), setup.currency(), setup.coaLevel1(),
                    monthlyTotal, GRATUITY_RATE, budget, deptUnit, budgetType, activity, fundsource));
            budgetItemsService.update(createBenefitBudgetItem(setup.workmanCompensation(), setup.currency(),
                    setup.coaLevel1(), monthlyTotal, WORKMAN_COMPENSATION_RATE, budget, deptUnit, budgetType,
                    activity, fundsource));
        }

        return new SalaryBudgetRegenerationResult(aggregateSalaryByGrade.size(), monthlyTotal, overCeilingRows);
    }

    @Transactional(readOnly = true)
    public SalaryBudgetPreview previewSelectedContext(Budget budget,
            UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType, Urc_Activities activity,
            Fundsource fundsource) {
        validateContext(budget, deptUnit, budgetType, activity, fundsource);

        SalaryBudgetSetup setup = loadSalaryBudgetSetup(budget);
        List<StaffSalary> salaries = staffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, deptUnit, budgetType, activity);
        List<StaffSalary> aggregateSalaryByGrade = staffSalaryService.aggregateSalaryByGrade(salaries);
        List<SalaryGradeCeiling> ceilings = salaryGradeCeilingService.findByContext(
                budget, deptUnit, budgetType, activity, fundsource);
        if (ceilings == null) {
            ceilings = Collections.emptyList();
        }

        List<SalaryBudgetPreviewRow> rows = new ArrayList<>();
        BigDecimal monthlyTotal = BigDecimal.ZERO;
        for (StaffSalary aggregate : aggregateSalaryByGrade) {
            BigDecimal monthlyAmount = aggregate.getSalary();
            GradeSalaryStats stats = gradeSalaryStats(salaries, aggregate.getGrade());
            CeilingComparison ceilingComparison = compareCeiling(ceilings, aggregate.getGrade(), monthlyAmount, stats.staffCount());
            rows.add(new SalaryBudgetPreviewRow("Salary", setup.salaryWages().getCode(),
                    aggregate.getFname() + " Salary", aggregate.getGrade(), monthlyAmount,
                    annualAmount(monthlyAmount), stats.staffCount(), ceilingComparison.ceilingType(),
                    ceilingComparison.monthlyCeiling(), ceilingComparison.monthlyVariance(),
                    ceilingComparison.status()));
            monthlyTotal = monthlyTotal.add(monthlyAmount);
        }

        if (monthlyTotal.compareTo(BigDecimal.ZERO) > 0) {
            addBenefitPreview(rows, setup.nssf(), "NSSF", monthlyTotal, NSSF_RATE);
            addBenefitPreview(rows, setup.gratuity(), "Gratuity", monthlyTotal, GRATUITY_RATE);
            addBenefitPreview(rows, setup.workmanCompensation(), "Workman Compensation", monthlyTotal,
                    WORKMAN_COMPENSATION_RATE);
        }

        return new SalaryBudgetPreview(rows, monthlyTotal, annualAmount(monthlyTotal), countOverCeilingRows(rows));
    }

    @Transactional
    public void deleteAllSalaryBudgetData(Budget budget) {
        if (budget == null) {
            return;
        }
        budgetItemsService.deleteByBudgetAndCoas(budget, loadSalaryBudgetSetup(budget).salaryCoas());
        staffSalaryService.deleteByBudget(budget);
    }

    private void deleteSelectedSalaryBudgetItems(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource, List<COA> salaryCoas) {
        budgetItemsService.deleteByBudgetAndCoasAndSalaryContext(
                budget, salaryCoas, deptUnit, budgetType, activity, fundsource);
    }

    private SalaryBudgetSetup loadSalaryBudgetSetup(Budget budget) {
        COA salaryWages = coaService.findByCodeAndBudget(SALARY_WAGES_CODE, budget);
        COA nssf = coaService.findByCodeAndBudget(NSSF_CODE, budget);
        COA gratuity = coaService.findByCodeAndBudget(GRATUITY_CODE, budget);
        COA workmanCompensation = coaService.findByCodeAndBudget(WORKMAN_COMPENSATION_CODE, budget);
        Currency currency = currencyService.findCurrenciesByCurrencyShortAndBudget("UGX", budget);
        Coalevel1 coaLevel1 = coalevel1Service.findByCode(2);

        if (salaryWages == null || nssf == null || gratuity == null || workmanCompensation == null
                || currency == null || coaLevel1 == null) {
            throw new IllegalStateException("Salary COA setup is incomplete for this budget.");
        }

        List<COA> salaryCoas = new ArrayList<>();
        salaryCoas.add(salaryWages);
        salaryCoas.add(nssf);
        salaryCoas.add(gratuity);
        salaryCoas.add(workmanCompensation);
        return new SalaryBudgetSetup(salaryWages, nssf, gratuity, workmanCompensation, currency, coaLevel1, salaryCoas);
    }

    private void validateContext(Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType,
            Urc_Activities activity, Fundsource fundsource) {
        if (budget == null || deptUnit == null || budgetType == null || activity == null || fundsource == null) {
            throw new IllegalArgumentException(
                    "Select Budget, Cost Centre, Budget Type, Activity and Fund Source before generating salary budget items.");
        }
    }

    private BudgetItems createSalaryBudgetItem(StaffSalary salary, COA salaryWages, Currency cur,
            Coalevel1 coaLevel1, Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType,
            Urc_Activities activity, Fundsource fundsource) {
        BudgetItems item = createBaseSalaryBudgetItem(salary.getFname() + " Salary", salaryWages, cur, coaLevel1,
                budget, deptUnit, budgetType, activity, fundsource);
        item.setCost(salary.getSalary());
        item.setQty(MONTHS_IN_YEAR);
        setMonthlyAmounts(item, salary.getSalary());
        item.setGrade(salary.getGrade());
        return item;
    }

    private BudgetItems createBenefitBudgetItem(COA coa, Currency cur, Coalevel1 coaLevel1,
            BigDecimal monthlySalaryTotal, BigDecimal rate, Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource) {
        BigDecimal monthlyAmount = monthlySalaryTotal.multiply(rate);
        BudgetItems item = createBaseSalaryBudgetItem(coa.getName(), coa, cur, coaLevel1,
                budget, deptUnit, budgetType, activity, fundsource);
        item.setCost(monthlyAmount);
        item.setQty(MONTHS_IN_YEAR);
        setMonthlyAmounts(item, monthlyAmount);
        return item;
    }

    private BudgetItems createBaseSalaryBudgetItem(String itemName, COA coa, Currency cur, Coalevel1 coaLevel1,
            Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType, Urc_Activities activity,
            Fundsource fundsource) {
        BudgetItems item = new BudgetItems();
        item.setItem(itemName);
        item.setUnitMeasure("MONTH");
        item.setCurrency(cur);
        item.setBudget(budget);
        item.setBudgetType(budgetType);
        item.setCoacode(coa);
        item.setDeptUnit(deptUnit);
        item.setFundsource(fundsource);
        item.setActivity(activity);
        item.setBcategory(coa.getCode());
        item.setCoalevel1(coaLevel1);
        return item;
    }

    private void setMonthlyAmounts(BudgetItems item, BigDecimal monthlyAmount) {
        item.setJan(monthlyAmount);
        item.setFeb(monthlyAmount);
        item.setMar(monthlyAmount);
        item.setApr(monthlyAmount);
        item.setMay(monthlyAmount);
        item.setJun(monthlyAmount);
        item.setJul(monthlyAmount);
        item.setAug(monthlyAmount);
        item.setSep(monthlyAmount);
        item.setOct(monthlyAmount);
        item.setNov(monthlyAmount);
        item.setDec(monthlyAmount);
    }

    private void addBenefitPreview(List<SalaryBudgetPreviewRow> rows, COA coa, String category,
            BigDecimal monthlySalaryTotal, BigDecimal rate) {
        BigDecimal monthlyAmount = monthlySalaryTotal.multiply(rate);
        rows.add(new SalaryBudgetPreviewRow(category, coa.getCode(), coa.getName(), null, monthlyAmount,
                annualAmount(monthlyAmount), null, null, null, null, ""));
    }

    private BigDecimal annualAmount(BigDecimal monthlyAmount) {
        return monthlyAmount.multiply(MONTHS_IN_YEAR);
    }

    private int countOverCeilingRows(List<SalaryBudgetPreviewRow> rows) {
        return (int) rows.stream()
                .filter(row -> "Over".equals(row.ceilingStatus()))
                .count();
    }

    private GradeSalaryStats gradeSalaryStats(List<StaffSalary> salaries, salaryScale grade) {
        List<StaffSalary> gradeSalaries = salaries.stream()
                .filter(salary -> grade != null && grade.equals(salary.getGrade()))
                .toList();
        BigDecimal maxMonthlySalary = gradeSalaries.stream()
                .map(StaffSalary::getSalary)
                .filter(salary -> salary != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        return new GradeSalaryStats(gradeSalaries.size(), maxMonthlySalary);
    }

    private CeilingComparison compareCeiling(List<SalaryGradeCeiling> ceilings, salaryScale grade,
            BigDecimal monthlyAmount, int staffCount) {
        SalaryGradeCeiling ceiling = ceilings.stream()
                .filter(item -> grade != null && grade.equals(item.getGrade()))
                .filter(item -> item.getCeilingType() == SalaryGradeCeilingType.GRADE_TOTAL)
                .findFirst()
                .orElseGet(() -> ceilings.stream()
                .filter(item -> grade != null && grade.equals(item.getGrade()))
                .filter(item -> item.getCeilingType() == SalaryGradeCeilingType.PER_STAFF)
                .findFirst()
                .orElse(null));

        if (ceiling == null || ceiling.getMonthlyCeiling() == null) {
            return new CeilingComparison(null, null, null, "No Ceiling");
        }

        BigDecimal effectiveCeiling = ceiling.getCeilingType() == SalaryGradeCeilingType.PER_STAFF
                ? ceiling.getMonthlyCeiling().multiply(new BigDecimal(staffCount))
                : ceiling.getMonthlyCeiling();
        BigDecimal variance = monthlyAmount.subtract(effectiveCeiling);
        return new CeilingComparison(ceiling.getCeilingType().name(), effectiveCeiling, variance,
                variance.compareTo(BigDecimal.ZERO) > 0 ? "Over" : "OK");
    }

    public record SalaryBudgetRegenerationResult(int salaryGradeRows, BigDecimal monthlyTotal, int overCeilingRows) {
    }

    public record SalaryBudgetPreview(List<SalaryBudgetPreviewRow> rows, BigDecimal monthlyTotal,
            BigDecimal annualTotal, int overCeilingRows) {
    }

    public record SalaryBudgetPreviewRow(String category, String accountCode, String item, salaryScale grade,
            BigDecimal monthlyAmount, BigDecimal annualAmount, Integer staffCount, String ceilingType,
            BigDecimal monthlyCeiling, BigDecimal monthlyVariance, String ceilingStatus) {
    }

    private record GradeSalaryStats(int staffCount, BigDecimal maxMonthlySalary) {
    }

    private record CeilingComparison(String ceilingType, BigDecimal monthlyCeiling, BigDecimal monthlyVariance,
            String status) {
    }

    private record SalaryBudgetSetup(COA salaryWages, COA nssf, COA gratuity, COA workmanCompensation,
            Currency currency, Coalevel1 coaLevel1, List<COA> salaryCoas) {
    }
}
