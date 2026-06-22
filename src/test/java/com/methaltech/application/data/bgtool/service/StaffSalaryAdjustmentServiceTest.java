package com.methaltech.application.data.bgtool.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.methaltech.application.data.SalaryAdjustmentScope;
import com.methaltech.application.data.SalaryAdjustmentType;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
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
class StaffSalaryAdjustmentServiceTest {

    @Mock
    private StaffSalaryService staffSalaryService;

    private StaffSalaryAdjustmentService service;
    private Budget budget;
    private UrcDeptSectionAnlDimbgt deptUnit;
    private Organisation budgetType;
    private Urc_Activities activity;
    private Fundsource fundsource;

    @BeforeEach
    void setUp() {
        service = new StaffSalaryAdjustmentService(staffSalaryService);
        budget = new Budget();
        deptUnit = new UrcDeptSectionAnlDimbgt();
        budgetType = new Organisation();
        activity = new Urc_Activities();
        fundsource = new Fundsource();
    }

    @Test
    void previewByGradePercentageAdjustsOnlySelectedGrade() {
        StaffSalary rg1 = staffSalary(1L, salaryScale.RG_1, "1000");
        StaffSalary rg2 = staffSalary(2L, salaryScale.RG_2, "2000");
        when(staffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, deptUnit, budgetType, activity)).thenReturn(List.of(rg1, rg2));

        StaffSalaryAdjustmentService.SalaryAdjustmentPreview preview = service.preview(
                budget, deptUnit, budgetType, activity, fundsource,
                SalaryAdjustmentScope.BY_GRADE, SalaryAdjustmentType.PERCENTAGE,
                salaryScale.RG_1, null, new BigDecimal("10"));

        assertThat(preview.rows()).hasSize(1);
        assertThat(preview.rows().get(0).oldMonthlySalary()).isEqualByComparingTo("1000");
        assertThat(preview.rows().get(0).newMonthlySalary()).isEqualByComparingTo("1100");
        assertThat(preview.monthlyDifference()).isEqualByComparingTo("100");
        assertThat(preview.annualDifference()).isEqualByComparingTo("1200");
    }

    @Test
    void applySelectedStaffFixedAmountSavesOnlySelectedStaff() {
        StaffSalary selected = staffSalary(10L, salaryScale.EXEC_1, "5000");
        StaffSalary other = staffSalary(11L, salaryScale.EXEC_1, "7000");
        when(staffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, deptUnit, budgetType, activity)).thenReturn(List.of(selected, other));

        service.apply(budget, deptUnit, budgetType, activity, fundsource,
                SalaryAdjustmentScope.SELECTED_STAFF, SalaryAdjustmentType.FIXED_AMOUNT,
                null, 10L, new BigDecimal("250"));

        ArgumentCaptor<List<StaffSalary>> captor = ArgumentCaptor.forClass(List.class);
        verify(staffSalaryService).saveStaffSalary(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getId()).isEqualTo(10L);
        assertThat(captor.getValue().get(0).getSalary()).isEqualByComparingTo("5250");
        assertThat(other.getSalary()).isEqualByComparingTo("7000");
    }

    private StaffSalary staffSalary(Long id, salaryScale grade, String salary) {
        StaffSalary staffSalary = new StaffSalary();
        staffSalary.setId(id);
        staffSalary.setCode("S" + id);
        staffSalary.setFname("Staff");
        staffSalary.setLname(String.valueOf(id));
        staffSalary.setGrade(grade);
        staffSalary.setSalary(new BigDecimal(salary));
        return staffSalary;
    }
}
