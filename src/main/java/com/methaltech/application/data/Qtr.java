package com.methaltech.application.data;

import java.math.BigDecimal;
import lombok.Data;

public @Data
class Qtr {

    BigDecimal qtr1;
    BigDecimal qtr2;
    BigDecimal qtr3;
    BigDecimal qtr4;

    public Qtr(BigDecimal qtr1, BigDecimal qtr2, BigDecimal qtr3, BigDecimal qtr4) {
        this.qtr1 = qtr1;
        this.qtr2 = qtr2;
        this.qtr3 = qtr3;
        this.qtr4 = qtr4;
    }

}
