package com.methaltech.application.data;

import static com.methaltech.application.data.Quarters.Qtr1;
import static com.methaltech.application.data.Quarters.Qtr2;
import static com.methaltech.application.data.Quarters.Qtr3;
import static com.methaltech.application.data.Quarters.Qtr4;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
