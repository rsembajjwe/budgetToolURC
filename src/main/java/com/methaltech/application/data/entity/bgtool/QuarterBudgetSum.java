
package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;

public record QuarterBudgetSum(
        String deptCode,
        BigDecimal q1,
        BigDecimal q2,
        BigDecimal q3,
        BigDecimal q4
) {}
