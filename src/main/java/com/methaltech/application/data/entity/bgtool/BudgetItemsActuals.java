package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

public @Data
class BudgetItemsActuals {

    private String item;

    private Budget budget;

    private Coalevel1 coalevel1;

    private Set<UrcDeptSectionAnlDimbgt> deptUnit;

    private BigDecimal jul;

    private BigDecimal nov;

    private BigDecimal mar;

    private BigDecimal aug;

    private BigDecimal dec;

    private BigDecimal apr;

    private BigDecimal sep;

    private BigDecimal jan;

    private BigDecimal may;

    private BigDecimal oct;

    private BigDecimal feb;

    private BigDecimal jun;

    private BigDecimal julA;

    private BigDecimal novA;

    private BigDecimal marA;

    private BigDecimal augA;

    private BigDecimal decA;

    private BigDecimal aprA;

    private BigDecimal sepA;

    private BigDecimal janA;

    private BigDecimal mayA;

    private BigDecimal octA;

    private BigDecimal febA;

    private BigDecimal junA;

    private BigDecimal total;
    private BigDecimal qtr1;
    private BigDecimal qtr2;
    private BigDecimal qtr3;
    private BigDecimal qtr4;
    private BigDecimal qtr1A;
    private BigDecimal qtr2A;
    private BigDecimal qtr3A;
    private BigDecimal qtr4A;
    private BigDecimal totalA;

    private COA coacode;

    /*    public void setTotal(BigDecimal total) {
    this.total = sumNullable(jul, aug, sep, oct, nov, dec, jan, feb, mar, apr, may, jun);
    }*/

    public void setTotalA(BigDecimal total) {
        this.totalA = sumNullable(julA, augA, sepA, octA, novA, decA, janA, febA, marA, aprA, mayA, junA).abs();
    }

    public void setQtr1(BigDecimal qtr1) {
        this.qtr1 = sumNullable(jul, aug, sep);
    }

    public void setQtr2(BigDecimal qtr2) {
        this.qtr2 = sumNullable(oct, nov, dec);
    }

    public void setQtr3(BigDecimal qtr3) {
        this.qtr3 = sumNullable(jan, feb, mar);
    }

    public void setQtr4(BigDecimal qtr4) {
        this.qtr4 = sumNullable(apr, may, jun);
    }

    public void setQtr1A(BigDecimal qtr1A) {
        this.qtr1A = sumNullable(julA, augA, sepA);
    }

    public void setQtr2A(BigDecimal qtr2A) {
        this.qtr2A = sumNullable(octA, novA, decA);
    }

    public void setQtr3A(BigDecimal qtr3A) {
        this.qtr3A = sumNullable(janA, febA, marA);
    }

    public void setQtr4A(BigDecimal qtr4A) {
        this.qtr4A = sumNullable(aprA, mayA, junA);
    }

    private BigDecimal sumNullable(BigDecimal... values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                sum = sum.add(value);
            }
        }
        return sum;
    }

}
