package com.methaltech.application.data;

import com.methaltech.application.data.entity.bgtool.Budget;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.Set;

public class GetPeriods {

    public GetPeriods() {
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

    public Set<Integer> getFinancialYearPeriods2(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget == null || budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // Return empty if dates not set
        }

        int startYear = budget.getStartDate().getYear();
        int endYear = budget.getCloseDate().getYear();

        // Financial year runs July → June
        // Quarter mappings:
        // Q1: Jul–Sep, Q2: Oct–Dec, Q3: Jan–Mar, Q4: Apr–Jun
        Month startMonth;
        Month endMonth;

        switch (quarter) {
            case 1 -> {
                startMonth = Month.JULY;
                endMonth = Month.SEPTEMBER;
            }
            case 2 -> {
                startMonth = Month.OCTOBER;
                endMonth = Month.DECEMBER;
            }
            case 3 -> {
                startMonth = Month.JANUARY;
                endMonth = Month.MARCH;
            }
            case 4 -> {
                startMonth = Month.APRIL;
                endMonth = Month.JUNE;
            }
            default -> {
                return periods; // Invalid quarter
            }
        }

        // Determine the correct year based on quarter
        // int periodYear = (quarter == 1 || quarter == 2) ? startYear : endYear;
        int periodYear = budget.getCloseDate().getYear();

        // Generate integer codes for each month in the quarter
        LocalDate current = LocalDate.of(periodYear, startMonth, 1);
        while (!current.isAfter(LocalDate.of(periodYear, endMonth, endMonth.length(current.isLeapYear())))) {
            int monthValue = current.getMonthValue();
            int periodCode = periodYear * 1000 + monthValue; // e.g. 2025007 for July 2025
            periods.add(periodCode);
            current = current.plusMonths(1);
        }
        System.out.println(periods.toString());
        return periods;
    }

    
    public Set<Integer> getFinancialYearPeriods(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget == null || budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // Return empty if dates not set
        }

        // Financial year runs July → June
        // Quarter mappings:
        // Q1: Jul–Sep, Q2: Oct–Dec, Q3: Jan–Mar, Q4: Apr–Jun
        Month startMonth;
        Month endMonth;

        switch (quarter) {
            case 1 -> {
                startMonth = Month.JULY;
                endMonth = Month.SEPTEMBER;
            }
            case 2 -> {
                startMonth = Month.OCTOBER;
                endMonth = Month.DECEMBER;
            }
            case 3 -> {
                startMonth = Month.JANUARY;
                endMonth = Month.MARCH;
            }
            case 4 -> {
                startMonth = Month.APRIL;
                endMonth = Month.JUNE;
            }
            default -> {
                return periods;
            }
        }

        int periodYear = budget.getCloseDate().getYear();
        // Generate integer codes relative to financial year (July = 1, ..., June = 12)
        LocalDate current = LocalDate.of(periodYear, startMonth, 1);

        while (!current.isAfter(LocalDate.of(periodYear, endMonth, endMonth.length(current.isLeapYear())))) {
            int monthOffset = ((current.getMonthValue() + 12 - 7) % 12) + 1; // July = 1, June = 12
            int periodCode = periodYear * 1000 + monthOffset;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }
        return periods;
    }

}
