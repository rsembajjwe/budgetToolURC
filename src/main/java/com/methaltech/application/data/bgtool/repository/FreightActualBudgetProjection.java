
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.FreightActualVolumes;
import java.math.BigDecimal;

public interface FreightActualBudgetProjection {

    FreightActualVolumes getActual();
    COA getCoacode();
    Budget getBudget();

    BigDecimal getBudgetJul();
    BigDecimal getBudgetAug();
    BigDecimal getBudgetSep();
    BigDecimal getBudgetOct();
    BigDecimal getBudgetNov();
    BigDecimal getBudgetDec();

    BigDecimal getBudgetJan();
    BigDecimal getBudgetFeb();
    BigDecimal getBudgetMar();
    BigDecimal getBudgetApr();
    BigDecimal getBudgetMay();
    BigDecimal getBudgetJun();

    BigDecimal getBudgetTotal();
}

