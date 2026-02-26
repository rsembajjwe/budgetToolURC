package com.methaltech.application;

import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;

public class test {

    private static List<Integer> years = new ArrayList<>();
    BudgetItemsService samplBudgetItemsService;
    SALFLDGService sALFLDGService;

    public test(SALFLDGService sALFLDGService) {
        this.sALFLDGService = sALFLDGService;

    }

    public static void main(String[] args) {
Budget bug =new Budget();
bug.setStartDate(LocalDate.of(2025, Month.JULY, 1));
bug.setCloseDate(LocalDate.of(2026, Month.JUNE, 30));
System.out.println(getFinancialYearPeriodsByQuarter(bug,1));

    }
    public static void strings(String input) {
        // String input = "FY2024-2025";

        // Define a pattern to match the years
        Pattern pattern = Pattern.compile("\\d{4}");

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(input);

        // Find and print all matches (years) in the input string
        while (matcher.find()) {
            years.add(Integer.parseInt(matcher.group()));
            // System.out.println("Year: " + matcher.group());
        }
    }

    public static List<Integer> getYears() {

        return years;
    }

    public void showThat() {
        DeptSectionMerger deptSectionMerger = new DeptSectionMerger();
        deptSectionMerger.setDeptcode("D001");
        Set<String> sectionCodes = new HashSet<>();
        sectionCodes.add("S001");
        sectionCodes.add("S002");
        sectionCodes.add("S003");
        deptSectionMerger.setSectioncodes(sectionCodes);

        DeptSectionMerger deptSectionMerger2 = new DeptSectionMerger();
        deptSectionMerger2.setDeptcode("D002");
        Set<String> sectionCodes2 = new HashSet<>();
        sectionCodes2.add("S004");
        sectionCodes2.add("S005");
        sectionCodes2.add("S006");
        deptSectionMerger2.setSectioncodes(sectionCodes2);

        DeptSectionMerger deptSectionMerger3 = new DeptSectionMerger();
        deptSectionMerger3.setDeptcode("D003");
        Set<String> sectionCodes3 = new HashSet<>();
        sectionCodes3.add("S007");
        sectionCodes3.add("S008");
        sectionCodes3.add("S009");
        deptSectionMerger3.setSectioncodes(sectionCodes3);
    }

    public static String generateString(int index) {
        // Convert index to string with leading zeros
        String indexString = String.format("%02d", index);
        // Generate the string
        String generatedString = "ZBT" + indexString;
        return generatedString;
    }

    public static Set<String> getFinancialYearPeriods(Budget budget) {
        Set<String> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        for (int i = 1; i <= 12; i++) {
            String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }
    public static Set<Integer> getFinancialYearPeriodByMonth(Budget budget, Month month) {
        Set<Integer> periods = new LinkedHashSet<>();
        if (budget == null || month == null || budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods;
        }

        LocalDate start = budget.getStartDate().withDayOfMonth(1);
        LocalDate end   = budget.getCloseDate().withDayOfMonth(1);

        int fyEndYear = budget.getCloseDate().getYear();

        // July=1 ... June=12
        int periodIndex = ((month.getValue() - Month.JULY.getValue() + 12) % 12) + 1;
        int periodCode = fyEndYear * 1000 + periodIndex;

        // Map month to the correct calendar year in this FY window
        int monthYear = (month.getValue() >= Month.JULY.getValue())
                ? budget.getStartDate().getYear()
                : budget.getCloseDate().getYear();

        LocalDate requestedMonth = LocalDate.of(monthYear, month, 1);

        if (!requestedMonth.isBefore(start) && !requestedMonth.isAfter(end)) {
            periods.add(periodCode);
        }

        return periods;
    }

    public static Set<Integer> getFinancialYearPeriodsByQuarter(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();
        if (budget == null || budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods;
        }
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter must be 1..4");
        }

        LocalDate start = budget.getStartDate().withDayOfMonth(1);
        LocalDate end   = budget.getCloseDate().withDayOfMonth(1);

        int fyEndYear = budget.getCloseDate().getYear();

        Month[] quarterMonths = switch (quarter) {
            case 1 -> new Month[]{ Month.JULY, Month.AUGUST, Month.SEPTEMBER };
            case 2 -> new Month[]{ Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER };
            case 3 -> new Month[]{ Month.JANUARY, Month.FEBRUARY, Month.MARCH };
            case 4 -> new Month[]{ Month.APRIL, Month.MAY, Month.JUNE };
            default -> throw new IllegalArgumentException("Quarter must be 1..4");
        };

        for (Month m : quarterMonths) {
            // July=1 ... June=12
            int periodIndex = ((m.getValue() - Month.JULY.getValue() + 12) % 12) + 1;
            int periodCode = fyEndYear * 1000 + periodIndex;

            int monthYear = (m.getValue() >= Month.JULY.getValue())
                    ? budget.getStartDate().getYear()
                    : budget.getCloseDate().getYear();

            LocalDate requestedMonth = LocalDate.of(monthYear, m, 1);

            if (!requestedMonth.isBefore(start) && !requestedMonth.isAfter(end)) {
                periods.add(periodCode);
            }
        }

        return periods;
    }

}
