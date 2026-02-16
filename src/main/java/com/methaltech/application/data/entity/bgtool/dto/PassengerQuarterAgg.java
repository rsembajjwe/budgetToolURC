
package com.methaltech.application.data.entity.bgtool.dto;

import java.math.BigDecimal;

public class PassengerQuarterAgg {
    private final BigDecimal q1, q2, q3, q4, year;

    public PassengerQuarterAgg(BigDecimal q1, BigDecimal q2, BigDecimal q3, BigDecimal q4, BigDecimal year) {
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.year = year;
    }

    public BigDecimal getQ1() { return q1; }
    public BigDecimal getQ2() { return q2; }
    public BigDecimal getQ3() { return q3; }
    public BigDecimal getQ4() { return q4; }
    public BigDecimal getYear() { return year; }
}


