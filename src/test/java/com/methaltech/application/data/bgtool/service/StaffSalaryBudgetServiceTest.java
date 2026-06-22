package com.methaltech.application.data.bgtool.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.methaltech.application.data.SalaryGradeCeilingType;
import com.methaltech.application.data.salaryScale;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaffSalaryBudgetServiceTest {

    private static final String SALARY_WAGES_CODE = "211101";
    private static final String NSSF_CODE = "212101";
    private static final String GRATUITY_CODE = "213004";
    private static final String WORKMAN_COMPENSATION_CODE = "213005";

    @Mock
    private CoaService coaService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private Coalevel1Service coalevel1Service;
    @Mock
    private BudgetItemsService budgetItemsService;
    @Mock
    private StaffSalaryService staffSalaryService;
    @Mock
    private SalaryGradeCeilingService salaryGradeCeilingService;

    private StaffSalaryBudgetService service;
    private Budget budget;
    private UrcDeptSectionAnlDimbgt deptUnit;
    private Organisation budgetType;
    private Urc_Activities activity;
    private Fundsource fundsource;
    private Currency currency;
    private Coalevel1 coaLevel1;
    private COA salaryWages;
    private COA nssf;
    private COA gratuity;
    private COA workmanCompensation;

    @BeforeEach
    void setUp() {
        service = new StaffSalaryBudgetService(coaService, currencyService, coalevel1Service,
                budgetItemsService, staffSalaryService, salaryGradeCeilingService);
        budget = new Budget();
        deptUnit = new UrcDeptSectionAnlDimbgt();
        budgetType = new Organisation();
        activity = new Urc_Activities();
        fundsource = new Fundsource();
        currency = new Currency();
        coaLevel1 = new Coalevel1();
        salaryWages = coa(SALARY_WAGES_CODE, "Salary and Wages");
        nssf = coa(NSSF_CODE, "NSSF");
        gratuity = coa(GRATUITY_CODE, "Gratuity");
        workmanCompensation = coa(WORKMAN_COMPENSATION_CODE, "Workman Compensation");
    }

    @Test
    void regenerateSelectedContextCreatesScopedSalaryAndBenefitItems() {
        stubSalarySetup();
        List<StaffSalary> salaries = List.of(
                staffSalary(salaryScale.RG_1, "1000"),
                staffSalary(salaryScale.RG_1, "2000"),
                staffSalary(salaryScale.EXEC_1, "5000")
        );
        when(staffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, deptUnit, budgetType, activity)).thenReturn(salaries);
        when(staffSalaryService.aggregateSalaryByGrade(salaries)).thenCallRealMethod();
        when(budgetItemsService.update(any(BudgetItems.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StaffSalaryBudgetService.SalaryBudgetRegenerationResult result = service.regenerateSelectedContext(
                budget, deptUnit, budgetType, activity, fundsource);

        assertThat(result.salaryGradeRows()).isEqualTo(2);
        assertThat(result.monthlyTotal()).isEqualByComparingTo("8000");
        verify(budgetItemsService).deleteByBudgetAndCoasAndSalaryContext(
                eq(budget), eq(List.of(salaryWages, nssf, gratuity, workmanCompensation)),
                eq(deptUnit), eq(budgetType), eq(activity), eq(fundsource));

        ArgumentCaptor<BudgetItems> captor = ArgumentCaptor.forClass(BudgetItems.class);
        verify(budgetItemsService, org.mockito.Mockito.times(5)).update(captor.capture());
        List<BudgetItems> generated = captor.getAllValues();

        assertThat(generated).allSatisfy(item -> {
            assertThat(item.getBudget()).isSameAs(budget);
            assertThat(item.getDeptUnit()).isSameAs(deptUnit);
            assertThat(item.getBudgetType()).isSameAs(budgetType);
            assertThat(item.getActivity()).isSameAs(activity);
            assertThat(item.getFundsource()).isSameAs(fundsource);
            assertThat(item.getCurrency()).isSameAs(currency);
            assertThat(item.getCoalevel1()).isSameAs(coaLevel1);
            assertThat(item.getQty()).isEqualByComparingTo("12");
            assertThat(item.getJan()).isEqualByComparingTo(item.getCost());
            assertThat(item.getDec()).isEqualByComparingTo(item.getCost());
        });

        assertThat(generated)
                .filteredOn(item -> item.getCoacode() == salaryWages)
                .extracting(BudgetItems::getGrade)
                .containsExactlyInAnyOrder(salaryScale.RG_1, salaryScale.EXEC_1);
        assertThat(generated)
                .filteredOn(item -> item.getCoacode() == salaryWages)
                .extracting(BudgetItems::getCost)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactlyInAnyOrder(new BigDecimal("3000"), new BigDecimal("5000"));
        assertThat(findByCoa(generated, nssf).getCost()).isEqualByComparingTo("800.00");
        assertThat(findByCoa(generated, gratuity).getCost()).isEqualByComparingTo("2000.00");
        assertThat(findByCoa(generated, workmanCompensation).getCost()).isEqualByComparingTo("240.00");
    }

    @Test
    void regenerateSelectedContextFailsClearlyWhenSetupIsMissing() {
        when(coaService.findByCodeAndBudget(SALARY_WAGES_CODE, budget)).thenReturn(salaryWages);

        assertThatThrownBy(() -> service.regenerateSelectedContext(
                budget, deptUnit, budgetType, activity, fundsource))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Salary COA setup is incomplete");

        verify(budgetItemsService, never()).deleteByBudgetAndCoasAndSalaryContext(
                any(), any(), any(), any(), any(), any());
        verify(budgetItemsService, never()).update(any(BudgetItems.class));
    }

    @Test
    void previewSelectedContextReturnsSalaryAndBenefitRowsWithoutWritingBudgetItems() {
        stubSalarySetup();
        List<StaffSalary> salaries = List.of(
                staffSalary(salaryScale.RG_2, "1500"),
                staffSalary(salaryScale.EXEC_2, "4500")
        );
        when(staffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, deptUnit, budgetType, activity)).thenReturn(salaries);
        when(staffSalaryService.aggregateSalaryByGrade(salaries)).thenCallRealMethod();
        when(salaryGradeCeilingService.findByContext(budget, deptUnit, budgetType, activity, fundsource))
                .thenReturn(List.of(ceiling(salaryScale.RG_2, SalaryGradeCeilingType.GRADE_TOTAL, "1000")));

        StaffSalaryBudgetService.SalaryBudgetPreview preview = service.previewSelectedContext(
                budget, deptUnit, budgetType, activity, fundsource);

        assertThat(preview.monthlyTotal()).isEqualByComparingTo("6000");
        assertThat(preview.annualTotal()).isEqualByComparingTo("72000");
        assertThat(preview.rows()).hasSize(5);
        assertThat(preview.rows())
                .filteredOn(row -> "Salary".equals(row.category()))
                .extracting(StaffSalaryBudgetService.SalaryBudgetPreviewRow::grade)
                .containsExactlyInAnyOrder(salaryScale.RG_2, salaryScale.EXEC_2);
        assertThat(preview.rows())
                .filteredOn(row -> salaryScale.RG_2.equals(row.grade()))
                .singleElement()
                .satisfies(row -> {
                    assertThat(row.staffCount()).isEqualTo(1);
                    assertThat(row.ceilingType()).isEqualTo(SalaryGradeCeilingType.GRADE_TOTAL.name());
                    assertThat(row.monthlyCeiling()).isEqualByComparingTo("1000");
                    assertThat(row.monthlyVariance()).isEqualByComparingTo("500");
                    assertThat(row.ceilingStatus()).isEqualTo("Over");
                });
        assertThat(preview.rows())
                .filteredOn(row -> NSSF_CODE.equals(row.accountCode()))
                .singleElement()
                .satisfies(row -> {
                    assertThat(row.monthlyAmount()).isEqualByComparingTo("600.00");
                    assertThat(row.annualAmount()).isEqualByComparingTo("7200.00");
                });
        assertThat(preview.rows())
                .filteredOn(row -> GRATUITY_CODE.equals(row.accountCode()))
                .singleElement()
                .satisfies(row -> assertThat(row.monthlyAmount()).isEqualByComparingTo("1500.00"));
        assertThat(preview.rows())
                .filteredOn(row -> WORKMAN_COMPENSATION_CODE.equals(row.accountCode()))
                .singleElement()
                .satisfies(row -> assertThat(row.monthlyAmount()).isEqualByComparingTo("180.00"));

        verify(budgetItemsService, never()).deleteByBudgetAndCoasAndSalaryContext(
                any(), any(), any(), any(), any(), any());
        verify(budgetItemsService, never()).update(any(BudgetItems.class));
    }

    @Test
    void deleteAllSalaryBudgetDataDeletesSalaryCoasAndStaffSalaries() {
        stubSalarySetup();

        service.deleteAllSalaryBudgetData(budget);

        verify(budgetItemsService).deleteByBudgetAndCoas(
                budget, List.of(salaryWages, nssf, gratuity, workmanCompensation));
        verify(staffSalaryService).deleteByBudget(budget);
    }

    private void stubSalarySetup() {
        when(coaService.findByCodeAndBudget(SALARY_WAGES_CODE, budget)).thenReturn(salaryWages);
        when(coaService.findByCodeAndBudget(NSSF_CODE, budget)).thenReturn(nssf);
        when(coaService.findByCodeAndBudget(GRATUITY_CODE, budget)).thenReturn(gratuity);
        when(coaService.findByCodeAndBudget(WORKMAN_COMPENSATION_CODE, budget)).thenReturn(workmanCompensation);
        when(currencyService.findCurrenciesByCurrencyShortAndBudget("UGX", budget)).thenReturn(currency);
        when(coalevel1Service.findByCode(2)).thenReturn(coaLevel1);
    }

    private COA coa(String code, String name) {
        COA coa = new COA();
        coa.setCode(code);
        coa.setName(name);
        return coa;
    }

    private StaffSalary staffSalary(salaryScale grade, String salary) {
        StaffSalary staffSalary = new StaffSalary();
        staffSalary.setGrade(grade);
        staffSalary.setSalary(new BigDecimal(salary));
        return staffSalary;
    }

    private SalaryGradeCeiling ceiling(salaryScale grade, SalaryGradeCeilingType type, String monthlyCeiling) {
        SalaryGradeCeiling ceiling = new SalaryGradeCeiling();
        ceiling.setGrade(grade);
        ceiling.setCeilingType(type);
        ceiling.setMonthlyCeiling(new BigDecimal(monthlyCeiling));
        return ceiling;
    }

    private BudgetItems findByCoa(List<BudgetItems> items, COA coa) {
        return items.stream()
                .filter(item -> item.getCoacode() == coa)
                .findFirst()
                .orElseThrow();
    }
}
