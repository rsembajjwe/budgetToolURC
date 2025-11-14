
package com.methaltech.application.data;

import com.methaltech.application.data.entity.bgtool.COA;
import java.math.BigDecimal;

public interface BudgetItemsSummaryProjection {
    COA getCoacode(); // or CoaCodeDTO if you have a DTO
    BigDecimal getTotal();
    BigDecimal getQtr1();
    BigDecimal getQtr2();
    BigDecimal getQtr3();
    BigDecimal getQtr4();
}

