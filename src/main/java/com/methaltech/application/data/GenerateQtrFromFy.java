package com.methaltech.application.data;

import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.entity.bgtool.Budget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
@Component
public class GenerateQtrFromFy {

    private final BudgetService budgetService;

    public GenerateQtrFromFy(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public List<String> periods(String fy, Quarters qtr) {
        int[] years = extractYears(fy);
        List<String> periods = new ArrayList<>();

        if (qtr.equals(Quarters.Qtr1)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d007", y), String.format("%d008", y), String.format("%d009", y)));
        } else if (qtr.equals(Quarters.Qtr2)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)));
        } else if (qtr.equals(Quarters.Qtr3)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d001", y), String.format("%d002", y), String.format("%d003", y)));
        } else if (qtr.equals(Quarters.Qtr4)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d004", y), String.format("%d005", y), String.format("%d006", y)));
        } else if (qtr.equals(Quarters.Total)) {
            for (int y : years) {
                periods.addAll(Arrays.asList(
                        String.format("%d001", y), String.format("%d002", y), String.format("%d003", y),
                        String.format("%d004", y), String.format("%d005", y), String.format("%d006", y),
                        String.format("%d007", y), String.format("%d008", y), String.format("%d009", y),
                        String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)
                ));
            }
        }

        return periods;
    }

    public String getPreviousFy(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        return String.format("FY%d-%d", newfy1, fy1);
    }

    public Budget getPreviousBudget(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        // return String.format("FY%d-%d", newfy1, fy1);
        Optional<Budget> bug = budgetService.getByFY(String.format("FY%d-%d", newfy1, fy1));
        if (bug.isPresent()) {
            return bug.get();
        } else {
            return null;
        }
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
}
