package com.methaltech.application.data;

import static com.methaltech.application.data.Quarters.Qtr1;
import static com.methaltech.application.data.Quarters.Qtr2;
import static com.methaltech.application.data.Quarters.Qtr3;
import static com.methaltech.application.data.Quarters.Qtr4;
import com.methaltech.application.data.entity.bgtool.Budget;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeriodExtractor {

    private static List<Integer> years = new ArrayList<>();

    private int generateCode(int year, String month) {
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);
        monthMap.put("Qr1", 1);
        monthMap.put("Qr2", 1);
        monthMap.put("Qr3", 7);
        monthMap.put("Qr4", 7);

        int monthValue = monthMap.get(month);
        if (monthValue == 12) {
            return year * 1000 + 1; // Special case for "Jun"
        } else if (monthValue <= 6) {
            return year * 1000 + monthValue + 1;
        } else {
            return (year + 1) * 1000 + monthValue + 1;
        }
    }

    public List<Integer> getListOfPeriodByFY(String fy, Quarters qtr) {
        List<Integer> listPeriods = new ArrayList<>();
        strings(fy);
        int year = getYears().get(1);
        switch (qtr) {
            case Qtr1:
                listPeriods.add(year * 1000 + 1);
                listPeriods.add(year * 1000 + 2);
                listPeriods.add(year * 1000 + 3);
                break;
            case Qtr2:
                listPeriods.add(year * 1000 + 4);
                listPeriods.add(year * 1000 + 5);
                listPeriods.add(year * 1000 + 6);
                break;
            case Qtr3:
                listPeriods.add(year * 1000 + 7);
                listPeriods.add(year * 1000 + 8);
                listPeriods.add(year * 1000 + 9);
                break;
            case Qtr4:
                listPeriods.add(year * 1000 + 10);
                listPeriods.add(year * 1000 + 11);
                listPeriods.add(year * 1000 + 12);
                break;
            default:
                break;
        }
        return listPeriods;
    }

    public List<Integer> getListOfCurrentPeriodByFY(String fy) {
        List<Integer> listPeriods = new ArrayList<>();
        strings(fy);
        int year = getYears().get(1);

        listPeriods.add(year * 1000 + 1);
        listPeriods.add(year * 1000 + 2);
        listPeriods.add(year * 1000 + 3);

        listPeriods.add(year * 1000 + 4);
        listPeriods.add(year * 1000 + 5);
        listPeriods.add(year * 1000 + 6);

        listPeriods.add(year * 1000 + 7);
        listPeriods.add(year * 1000 + 8);
        listPeriods.add(year * 1000 + 9);

        listPeriods.add(year * 1000 + 10);
        listPeriods.add(year * 1000 + 11);
        listPeriods.add(year * 1000 + 12);

        return listPeriods;
    }

    public List<Integer> getListOfPreviousPeriodByFY(String fy) {
        List<Integer> listPeriods = new ArrayList<>();
        strings(fy);
        int year = getYears().get(0);

        listPeriods.add(year * 1000 + 1);
        listPeriods.add(year * 1000 + 2);
        listPeriods.add(year * 1000 + 3);

        listPeriods.add(year * 1000 + 4);
        listPeriods.add(year * 1000 + 5);
        listPeriods.add(year * 1000 + 6);

        listPeriods.add(year * 1000 + 7);
        listPeriods.add(year * 1000 + 8);
        listPeriods.add(year * 1000 + 9);

        listPeriods.add(year * 1000 + 10);
        listPeriods.add(year * 1000 + 11);
        listPeriods.add(year * 1000 + 12);

        return listPeriods;
    }

    public int generatePreviousPeriod(String yearS, String month) {
        strings(yearS);
        int year = getYears().get(0);
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);

        int monthValue = monthMap.get(month);
        if (monthValue == 1) {
            return year * 1000 + 1; // Special case for "Jun"
        } else if (monthValue == 2) {
            return year * 1000 + 2; // Special case for "Jun"
        } else if (monthValue == 3) {
            return year * 1000 + 3; // Special case for "Jun"
        } else if (monthValue == 4) {
            return year * 1000 + 4; // Special case for "Jun"
        } else if (monthValue == 5) {
            return year * 1000 + 5; // Special case for "Jun"
        } else if (monthValue == 6) {
            return year * 1000 + 6; // Special case for "Jun"
        } else if (monthValue == 7) {
            return year * 1000 + 7; // Special case for "Jun"
        } else if (monthValue == 8) {
            return year * 1000 + 8; // Special case for "Jun"
        } else if (monthValue == 9) {
            return year * 1000 + 9; // Special case for "Jun"
        } else if (monthValue == 10) {
            return year * 1000 + 10; // Special case for "Jun"
        } else if (monthValue == 11) {
            return year * 1000 + 11; // Special case for "Jun"
        } else if (monthValue == 12) {
            return year * 1000 + 12; // Special case for "Jun"
        } else {
            return 0;
        }
    }

    public int generateCurrentPeriod(String yearS, String month) {
        strings(yearS);
        int year = getYears().get(1);
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);

        int monthValue = monthMap.get(month);
        if (monthValue == 1) {
            return year * 1000 + 1; // Special case for "Jun"
        } else if (monthValue == 2) {
            return year * 1000 + 2; // Special case for "Jun"
        } else if (monthValue == 3) {
            return year * 1000 + 3; // Special case for "Jun"
        } else if (monthValue == 4) {
            return year * 1000 + 4; // Special case for "Jun"
        } else if (monthValue == 5) {
            return year * 1000 + 5; // Special case for "Jun"
        } else if (monthValue == 6) {
            return year * 1000 + 6; // Special case for "Jun"
        } else if (monthValue == 7) {
            return year * 1000 + 7; // Special case for "Jun"
        } else if (monthValue == 8) {
            return year * 1000 + 8; // Special case for "Jun"
        } else if (monthValue == 9) {
            return year * 1000 + 9; // Special case for "Jun"
        } else if (monthValue == 10) {
            return year * 1000 + 10; // Special case for "Jun"
        } else if (monthValue == 11) {
            return year * 1000 + 11; // Special case for "Jun"
        } else if (monthValue == 12) {
            return year * 1000 + 12; // Special case for "Jun"
        } else {
            return 0;
        }
    }

    public int generateCode2(String yearS, String month) {
        strings(yearS);
        int year = getYears().get(0);
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);
        monthMap.put("Qr1", 1);
        monthMap.put("Qr2", 1);
        monthMap.put("Qr3", 7);
        monthMap.put("Qr4", 7);

        int monthValue = monthMap.get(month);
        if (monthValue == 1) {
            return year * 1000 + 1; // Special case for "Jun"
        } else if (monthValue == 2) {
            return year * 1000 + 2; // Special case for "Jun"
        } else if (monthValue == 3) {
            return year * 1000 + 3; // Special case for "Jun"
        } else if (monthValue == 4) {
            return year * 1000 + 4; // Special case for "Jun"
        } else if (monthValue == 5) {
            return year * 1000 + 5; // Special case for "Jun"
        } else if (monthValue == 6) {
            return year * 1000 + 6; // Special case for "Jun"
        } else if (monthValue == 7) {
            year = getYears().get(1);
            return year * 1000 + 7; // Special case for "Jun"
        } else if (monthValue == 8) {
            year = getYears().get(1);
            return year * 1000 + 8; // Special case for "Jun"
        } else if (monthValue == 9) {
            year = getYears().get(1);
            return year * 1000 + 9; // Special case for "Jun"
        } else if (monthValue == 10) {
            year = getYears().get(1);
            return year * 1000 + 10; // Special case for "Jun"
        } else if (monthValue == 11) {
            year = getYears().get(1);
            return year * 1000 + 11; // Special case for "Jun"
        } else if (monthValue == 12) {
            year = getYears().get(1);
            return year * 1000 + 12; // Special case for "Jun"
        } else {
            return 0;
        }
    }

    public int generateCode22(String yearS, String month) {
        strings(yearS);
        int year = getYears().get(0) - 1;
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);
        monthMap.put("Qr1", 1);
        monthMap.put("Qr2", 1);
        monthMap.put("Qr3", 7);
        monthMap.put("Qr4", 7);

        int monthValue = monthMap.get(month);
        if (monthValue == 1) {
            return year * 1000 + 1; // Special case for "Jun"
        } else if (monthValue == 2) {
            return year * 1000 + 2; // Special case for "Jun"
        } else if (monthValue == 3) {
            return year * 1000 + 3; // Special case for "Jun"
        } else if (monthValue == 4) {
            return year * 1000 + 4; // Special case for "Jun"
        } else if (monthValue == 5) {
            return year * 1000 + 5; // Special case for "Jun"
        } else if (monthValue == 6) {
            return year * 1000 + 6; // Special case for "Jun"
        } else if (monthValue == 7) {
            year = getYears().get(1) - 1;
            return year * 1000 + 7; // Special case for "Jun"
        } else if (monthValue == 8) {
            year = getYears().get(1) - 1;
            return year * 1000 + 8; // Special case for "Jun"
        } else if (monthValue == 9) {
            year = getYears().get(1) - 1;
            return year * 1000 + 9; // Special case for "Jun"
        } else if (monthValue == 10) {
            year = getYears().get(1) - 1;
            return year * 1000 + 10; // Special case for "Jun"
        } else if (monthValue == 11) {
            year = getYears().get(1) - 1;
            return year * 1000 + 11; // Special case for "Jun"
        } else if (monthValue == 12) {
            year = getYears().get(1) - 1;
            return year * 1000 + 12; // Special case for "Jun"
        } else {
            return 0;
        }
    }

    public int generateYear(String yearS, String month) {
        strings(yearS);
        int year = getYears().get(0);
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("Jul", 1);
        monthMap.put("Aug", 2);
        monthMap.put("Sep", 3);
        monthMap.put("Oct", 4);
        monthMap.put("Nov", 5);
        monthMap.put("Dec", 6);
        monthMap.put("Jan", 7);
        monthMap.put("Feb", 8);
        monthMap.put("Mar", 9);
        monthMap.put("Apr", 10);
        monthMap.put("May", 11);
        monthMap.put("Jun", 12);
        monthMap.put("Qr1", 1);
        monthMap.put("Qr2", 1);
        monthMap.put("Qr3", 7);
        monthMap.put("Qr4", 7);

        int monthValue = monthMap.get(month);
        if (monthValue == 1) {
            return year; // Special case for "Jun"
        } else if (monthValue == 2) {
            return year; // Special case for "Jun"
        } else if (monthValue == 3) {
            return year; // Special case for "Jun"
        } else if (monthValue == 4) {
            return year; // Special case for "Jun"
        } else if (monthValue == 5) {
            return year; // Special case for "Jun"
        } else if (monthValue == 6) {
            return year; // Special case for "Jun"
        } else if (monthValue == 7) {
            year = getYears().get(1);
            return year; // Special case for "Jun"
        } else if (monthValue == 8) {
            year = getYears().get(1);
            return year; // Special case for "Jun"
        } else if (monthValue == 9) {
            year = getYears().get(1);
            return year; // Special case for "Jun"
        } else if (monthValue == 10) {
            year = getYears().get(1);
            return year; // Special case for "Jun"
        } else if (monthValue == 11) {
            year = getYears().get(1);
            return year; // Special case for "Jun"
        } else if (monthValue == 12) {
            year = getYears().get(1);
            return year; // Special case for "Jun"
        } else {
            return 0;
        }
    }

    public void strings(String input) {
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

    public List<Integer> getYears() {
        return years;
    }

    public List<Integer> extYears(String fy) {
        Pattern pattern = Pattern.compile("\\d{4}");

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(fy);

        // Find and print all matches (years) in the input string
        while (matcher.find()) {
            years.add(Integer.parseInt(matcher.group()));
            // System.out.println("Year: " + matcher.group());
        }
        return years;
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

    public String getPreviousFy(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        return String.format("FY%d-%d", newfy1, fy1);
    }

    public String getCurrentFy(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[1];
        int newfy1 = fy1 - 1;
        return String.format("FY%d-%d", newfy1, fy1);
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

    /*    public Set<Integer> getFinancialYearPeriodByMonth(Budget budget, Month month) {
    Set<Integer> periods = new LinkedHashSet<>();
    
    if (budget == null || month == null
    || budget.getStartDate() == null || budget.getCloseDate() == null) {
    return periods;
    }
    
    // Normalize to month boundaries
    LocalDate start = budget.getStartDate().withDayOfMonth(1);
    LocalDate end = budget.getCloseDate().withDayOfMonth(1);
    
    // Find the financial-year "period index" for the given month: July=1 ... June=12
    int periodIndex = ((month.getValue() - Month.JULY.getValue() + 12) % 12) + 1;
    
    // Financial year suffix = close year (e.g., FY 2025/07 -> 2026/06 => 2026)
    int fyEndYear = budget.getCloseDate().getYear();
    int periodCode = fyEndYear * 1000 + periodIndex;
    
    // Validate that the requested month actually lies within the budget range
    // We need to determine which calendar year that month falls in within this FY window.
    // Months Jul-Dec belong to start year, Jan-Jun belong to close year.
    int monthYear = (month.getValue() >= Month.JULY.getValue())
    ? budget.getStartDate().getYear()
    : budget.getCloseDate().getYear();
    
    LocalDate requestedMonth = LocalDate.of(monthYear, month, 1);
    
    if (!requestedMonth.isBefore(start) && !requestedMonth.isAfter(end)) {
    periods.add(periodCode);
    }
    
    return periods;
    }*/

    public int getFinancialYearPeriodByMonth(Budget budget, Month month) {

        if (budget == null || month == null
                || budget.getStartDate() == null || budget.getCloseDate() == null) {
            throw new IllegalArgumentException("Budget dates and month must not be null");
        }

        LocalDate start = budget.getStartDate().withDayOfMonth(1);
        LocalDate end = budget.getCloseDate().withDayOfMonth(1);

        int fyEndYear = budget.getCloseDate().getYear();

        // July = 1 ... June = 12
        int periodIndex = ((month.getValue() - Month.JULY.getValue() + 12) % 12) + 1;

        // Determine which calendar year this month belongs to
        int monthYear = (month.getValue() >= Month.JULY.getValue())
                ? budget.getStartDate().getYear()
                : budget.getCloseDate().getYear();

        LocalDate requestedMonth = LocalDate.of(monthYear, month, 1);

        if (requestedMonth.isBefore(start) || requestedMonth.isAfter(end)) {
            periodIndex=Month.JULY.getValue();
           // throw new IllegalArgumentException("Month is outside the budget financial year");
        }

        return fyEndYear * 1000 + periodIndex;
    }

    public Set<Integer> getFinancialYearPeriodsByQuarter(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();
        if (budget == null || budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods;
        }
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter must be 1..4");
        }

        LocalDate start = budget.getStartDate().withDayOfMonth(1);
        LocalDate end = budget.getCloseDate().withDayOfMonth(1);

        int fyEndYear = budget.getCloseDate().getYear();

        Month[] quarterMonths = switch (quarter) {
            case 1 ->
                new Month[]{Month.JULY, Month.AUGUST, Month.SEPTEMBER};
            case 2 ->
                new Month[]{Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER};
            case 3 ->
                new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH};
            case 4 ->
                new Month[]{Month.APRIL, Month.MAY, Month.JUNE};
            default ->
                throw new IllegalArgumentException("Quarter must be 1..4");
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
