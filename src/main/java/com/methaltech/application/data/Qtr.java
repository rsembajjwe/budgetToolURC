package com.methaltech.application.data;

import java.math.BigDecimal;
import lombok.Data;

public @Data
class Qtr {

    BigDecimal qtr1;
    BigDecimal qtr2;
    BigDecimal qtr3;
    BigDecimal qtr4;
    BigDecimal bqtr1;
    BigDecimal bqtr2;
    BigDecimal bqtr3;
    BigDecimal bqtr4;
    BigDecimal pbudget;
    BigDecimal cbudget;

    public Qtr(BigDecimal qtr1, BigDecimal qtr2, BigDecimal qtr3, BigDecimal qtr4, BigDecimal bqtr1,
            BigDecimal bqtr2,
            BigDecimal bqtr3,
            BigDecimal bqtr4,
            BigDecimal pbudget,
            BigDecimal cbudget) {
        this.qtr1 = qtr1;
        this.qtr2 = qtr2;
        this.qtr3 = qtr3;
        this.qtr4 = qtr4;
        this.bqtr1 = bqtr1;
        this.bqtr2 = bqtr2;
        this.bqtr3 = bqtr3;
        this.bqtr4 = bqtr4;
        this.pbudget = pbudget;
        this.cbudget = cbudget;

    }

}
