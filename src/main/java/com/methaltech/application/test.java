
package com.methaltech.application;

import com.methaltech.application.data.GenerateQtrFromFy;
import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    private static List<Integer> years = new ArrayList<>();
    BudgetItemsService samplBudgetItemsService;
    SALFLDGService sALFLDGService;

    public test(SALFLDGService sALFLDGService) {
        this.sALFLDGService=sALFLDGService;

    }


    public static void main(String[] args) {
        String input = "FY2024-2025";
        String month = "Jan"; // Example month from the list

        // int year = extractYear(input);
        //int result = generateCode(year, month);
        int result = generateCode2(input, "Jul");
        /*        System.out.println("Result: " + result);
        //strings();
        
        System.out.println("Result: " + getYears().get(0));
        System.out.println("Result: " + getYears().get(1));
        
        System.out.println("Result3: " + generateCode4(input, "Jun"));
        System.out.println("Result31: " + generateCode5(input, "Jun"));*/
        for (int i = 1; i <= 99; i++) {
            String result2 = generateString(i);
            System.out.println(result2);
        }   

    }

    private static int extractYear(String input) {
        Pattern pattern = Pattern.compile("\\d{4}");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return -1; // Handle error case
    }

    private static int generateCode(int year, String month) {
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
        if (monthValue == 12) {
            return year * 1000 + 1; // Special case for "Jun"
        } else if (monthValue <= 6) {
            return year * 1000 + monthValue + 1;
        } else {
            return (year + 1) * 1000 + monthValue + 1;
        }
    }

    private static int generateCode2(String yearS, String month) {
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

    private static int generateCode3(String yearS, String month) {
        strings(yearS); // Extract years from the input string
        int year = getYears().get(0); // Get the first year extracted

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
        if (monthValue == 1 || monthValue == 2 || monthValue == 3 || monthValue == 4 || monthValue == 5 || monthValue == 6) {
            return year * 1000 + monthValue; // Special case for "Jun"
        } else if (monthValue >= 7 && monthValue <= 12) {
            year = getYears().get(1); // Get the second year extracted
            return year * 1000 + monthValue;
        } else {
            return 0;
        }
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

    private static String generateCode4(String yearS, String month) {
        strings(yearS); // Extract years from the input string
        int year = getYears().get(0); // Get the first year extracted

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
        if (monthValue >= 1 && monthValue <= 6) {
            return year + "/" + String.format("%03d", monthValue); // Format for "Jul" to "Jun"
        } else if (monthValue >= 7 && monthValue <= 12) {
            year = getYears().get(1); // Get the second year extracted
            return year + "/" + String.format("%03d", monthValue); // Format for other months
        } else {
            return ""; // Return empty string for invalid input
        }
    }
    private static String generateCode5(String yearS, String month) {
        strings(yearS); // Extract years from the input string
        int year = getYears().get(0); // Get the first year extracted

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
        if (monthValue >= 1 && monthValue <= 6) {
            return month + "-" + Integer.toString(year).substring(2); // Format for "Jul" to "Jun"
        } else if (monthValue >= 7 && monthValue <= 12) {
            year = getYears().get(1); // Get the second year extracted
            return month + "-" + Integer.toString(year).substring(2); // Format for other months
        } else {
            return ""; // Return empty string for invalid input
        }
    } 
    
    public void showThat(){
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


}
