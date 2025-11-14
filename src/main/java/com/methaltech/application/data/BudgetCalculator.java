
package com.methaltech.application.data;


import com.methaltech.application.data.entity.bgtool.BudgetItems;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetCalculator {

    public Map<String, BigDecimal> calculateQuarterlyAndTotalBudget(List<BudgetItems> items) {
        BigDecimal q1 = BigDecimal.ZERO; // Jul, Aug, Sep
        BigDecimal q2 = BigDecimal.ZERO; // Oct, Nov, Dec
        BigDecimal q3 = BigDecimal.ZERO; // Jan, Feb, Mar
        BigDecimal q4 = BigDecimal.ZERO; // Apr, May, Jun

        for (BudgetItems item : items) {
            q1 = q1.add(getSafe(item.getJul()))
                   .add(getSafe(item.getAug()))
                   .add(getSafe(item.getSep()));

            q2 = q2.add(getSafe(item.getOct()))
                   .add(getSafe(item.getNov()))
                   .add(getSafe(item.getDec()));

            q3 = q3.add(getSafe(item.getJan()))
                   .add(getSafe(item.getFeb()))
                   .add(getSafe(item.getMar()));

            q4 = q4.add(getSafe(item.getApr()))
                   .add(getSafe(item.getMay()))
                   .add(getSafe(item.getJun()));
        }

        BigDecimal total = q1.add(q2).add(q3).add(q4);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("Q1", q1);
        result.put("Q2", q2);
        result.put("Q3", q3);
        result.put("Q4", q4);
        result.put("TOTAL", total);

        return result;
    }

    private BigDecimal getSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}

