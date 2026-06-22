package com.methaltech.application.data.bgtool.service;

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
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffSalaryAdjustmentService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    private final StaffSalaryService staffSalaryService;
    private final SalaryAdjustmentHistoryService salaryAdjustmentHistoryService;

    public StaffSalaryAdjustmentService(StaffSalaryService staffSalaryService,
            SalaryAdjustmentHistoryService salaryAdjustmentHistoryService) {
        this.staffSalaryService = staffSalaryService;
        this.salaryAdjustmentHistoryService = salaryAdjustmentHistoryService;
    }

    @Transactional(readOnly = true)
    public SalaryAdjustmentPreview preview(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource,
            SalaryAdjustmentScope scope, SalaryAdjustmentType type, salaryScale grade,
            Long selectedStaffSalaryId, BigDecimal value) {
        List<StaffSalary> salaries = matchingSalaries(budget, deptUnit, budgetType, activity, fundsource,
                scope, grade, selectedStaffSalaryId);
        List<SalaryAdjustmentPreviewRow> rows = salaries.stream()
                .map(salary -> {
                    BigDecimal oldSalary = zeroIfNull(salary.getSalary());
                    BigDecimal newSalary = adjustedSalary(oldSalary, type, value);
                    return new SalaryAdjustmentPreviewRow(salary.getId(), salary.getCode(),
                            staffName(salary), salary.getGrade(), oldSalary, newSalary,
                            newSalary.subtract(oldSalary), newSalary.subtract(oldSalary).multiply(MONTHS_IN_YEAR));
                })
                .toList();
        BigDecimal oldMonthlyTotal = rows.stream()
                .map(SalaryAdjustmentPreviewRow::oldMonthlySalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal newMonthlyTotal = rows.stream()
                .map(SalaryAdjustmentPreviewRow::newMonthlySalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SalaryAdjustmentPreview(rows, oldMonthlyTotal, newMonthlyTotal,
                newMonthlyTotal.subtract(oldMonthlyTotal),
                newMonthlyTotal.subtract(oldMonthlyTotal).multiply(MONTHS_IN_YEAR));
    }

    @Transactional
    public SalaryAdjustmentPreview apply(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource,
            SalaryAdjustmentScope scope, SalaryAdjustmentType type, salaryScale grade,
            Long selectedStaffSalaryId, BigDecimal value) {
        return apply(budget, deptUnit, budgetType, activity, fundsource, scope, type, grade,
                selectedStaffSalaryId, value, "SYSTEM");
    }

    @Transactional
    public SalaryAdjustmentPreview apply(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource,
            SalaryAdjustmentScope scope, SalaryAdjustmentType type, salaryScale grade,
            Long selectedStaffSalaryId, BigDecimal value, String appliedBy) {
        SalaryAdjustmentPreview preview = preview(budget, deptUnit, budgetType, activity, fundsource,
                scope, type, grade, selectedStaffSalaryId, value);
        List<StaffSalary> salaries = matchingSalaries(budget, deptUnit, budgetType, activity, fundsource,
                scope, grade, selectedStaffSalaryId);
        for (StaffSalary salary : salaries) {
            salary.setSalary(adjustedSalary(zeroIfNull(salary.getSalary()), type, value));
        }
        staffSalaryService.saveStaffSalary(salaries);
        recordHistory(budget, deptUnit, budgetType, activity, fundsource, scope, type, grade,
                selectedStaffSalaryId, value, appliedBy, preview);
        return preview;
    }

    private void recordHistory(Budget budget, UrcDeptSectionAnlDimbgt deptUnit, Organisation budgetType,
            Urc_Activities activity, Fundsource fundsource, SalaryAdjustmentScope scope, SalaryAdjustmentType type,
            salaryScale grade, Long selectedStaffSalaryId, BigDecimal value, String appliedBy,
            SalaryAdjustmentPreview preview) {
        SalaryAdjustmentPreviewRow selectedStaff = preview.rows().size() == 1
                ? preview.rows().get(0)
                : null;
        salaryAdjustmentHistoryService.record(budget, deptUnit, budgetType, activity, fundsource,
                scope, type, grade, selectedStaffSalaryId,
                selectedStaff == null ? null : selectedStaff.staffCode(),
                selectedStaff == null ? null : selectedStaff.staffName(),
                value, preview.rows().size(), preview.oldMonthlyTotal(), preview.newMonthlyTotal(),
                preview.monthlyDifference(), preview.annualDifference(), appliedBy);
    }

    private List<StaffSalary> matchingSalaries(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource,
            SalaryAdjustmentScope scope, salaryScale grade, Long selectedStaffSalaryId) {
        validateContext(budget, deptUnit, budgetType, activity, fundsource);
        if (scope == null) {
            throw new IllegalArgumentException("Select salary adjustment scope.");
        }
        List<StaffSalary> salaries = staffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, deptUnit, budgetType, activity);
        return salaries.stream()
                .filter(salary -> switch (scope) {
                    case ALL_STAFF -> true;
                    case BY_GRADE -> {
                        if (grade == null) {
                            throw new IllegalArgumentException("Select grade for By Grade adjustment.");
                        }
                        yield grade.equals(salary.getGrade());
                    }
                    case SELECTED_STAFF -> {
                        if (selectedStaffSalaryId == null) {
                            throw new IllegalArgumentException("Select a staff salary row first.");
                        }
                        yield selectedStaffSalaryId.equals(salary.getId());
                    }
                })
                .toList();
    }

    private BigDecimal adjustedSalary(BigDecimal oldSalary, SalaryAdjustmentType type, BigDecimal value) {
        if (type == null) {
            throw new IllegalArgumentException("Select salary adjustment type.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Enter adjustment value.");
        }
        BigDecimal adjusted = switch (type) {
            case PERCENTAGE -> oldSalary.add(oldSalary.multiply(value).divide(ONE_HUNDRED, 6, RoundingMode.HALF_UP));
            case FIXED_AMOUNT -> oldSalary.add(value);
            case SET_SALARY -> value;
        };
        if (adjusted.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Adjusted salary cannot be negative.");
        }
        return adjusted;
    }

    private void validateContext(Budget budget, UrcDeptSectionAnlDimbgt deptUnit,
            Organisation budgetType, Urc_Activities activity, Fundsource fundsource) {
        if (budget == null || deptUnit == null || budgetType == null || activity == null || fundsource == null) {
            throw new IllegalArgumentException(
                    "Select Budget, Cost Centre, Budget Type, Activity and Fund Source before adjusting salaries.");
        }
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String staffName(StaffSalary salary) {
        String first = salary.getFname() == null ? "" : salary.getFname();
        String last = salary.getLname() == null ? "" : salary.getLname();
        String fullName = (first + " " + last).trim();
        return fullName.isEmpty() ? salary.getCode() : fullName;
    }

    public record SalaryAdjustmentPreview(List<SalaryAdjustmentPreviewRow> rows, BigDecimal oldMonthlyTotal,
            BigDecimal newMonthlyTotal, BigDecimal monthlyDifference, BigDecimal annualDifference) {
    }

    public record SalaryAdjustmentPreviewRow(Long staffSalaryId, String staffCode, String staffName,
            salaryScale grade, BigDecimal oldMonthlySalary, BigDecimal newMonthlySalary,
            BigDecimal monthlyDifference, BigDecimal annualDifference) {
    }
}
