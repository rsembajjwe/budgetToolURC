package com.methaltech.application.data;

import java.math.BigDecimal;
import lombok.Data;

public @Data class MonthlySumResponseFreight {

    private BigDecimal jul;
    private BigDecimal aug;
    private BigDecimal sep;
    private BigDecimal oct;
    private BigDecimal nov;
    private BigDecimal dec;
    private BigDecimal jan;
    private BigDecimal feb;
    private BigDecimal mar;
    private BigDecimal apr;
    private BigDecimal may;
    private BigDecimal jun;
    private BigDecimal total;
    private BigDecimal qtr1;
    private BigDecimal qtr2;
    private BigDecimal qtr3;
    private BigDecimal qtr4;

    // Constructor
    public MonthlySumResponseFreight(BigDecimal jul, BigDecimal aug, BigDecimal sep, BigDecimal oct, BigDecimal nov,
            BigDecimal dec, BigDecimal jan, BigDecimal feb, BigDecimal mar, BigDecimal apr,
            BigDecimal may, BigDecimal jun, BigDecimal total) {
        this.jul = jul;
        this.aug = aug;
        this.sep = sep;
        this.oct = oct;
        this.nov = nov;
        this.dec = dec;
        this.jan = jan;
        this.feb = feb;
        this.mar = mar;
        this.apr = apr;
        this.may = may;
        this.jun = jun;
        this.total = total;
        this.qtr1 = jul.add(aug).add(sep);
        this.qtr2 = oct.add(nov).add(dec);
        this.qtr3 = jan.add(feb).add(mar);
        this.qtr4 = apr.add(may).add(jun);
    }
    
}
