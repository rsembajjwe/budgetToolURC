package com.methaltech.application.data.entity.bgtool;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class FreightActualBudgetDTO {

    private FreightActualVolumes actual;
    private COA coacode;
    private Budget budget;

    private BigDecimal budgetJul;
    private BigDecimal budgetAug;
    private BigDecimal budgetSep;
    private BigDecimal budgetOct;
    private BigDecimal budgetNov;
    private BigDecimal budgetDec;
    private BigDecimal budgetJan;
    private BigDecimal budgetFeb;
    private BigDecimal budgetMar;
    private BigDecimal budgetApr;
    private BigDecimal budgetMay;
    private BigDecimal budgetJun;
    private BigDecimal budgetTotal;
    public FreightActualBudgetDTO(){
        
    }
    public FreightActualBudgetDTO(
            FreightActualVolumes actual,
            COA coacode,
            Budget budget,
            BigDecimal jul, BigDecimal aug, BigDecimal sep,
            BigDecimal oct, BigDecimal nov, BigDecimal dec,
            BigDecimal jan, BigDecimal feb, BigDecimal mar,
            BigDecimal apr, BigDecimal may, BigDecimal jun,
            BigDecimal total
    ) {
        this.actual = actual;
        this.coacode = coacode;
        this.budget = budget;
        this.budgetJul = nz(jul);
        this.budgetAug = nz(aug);
        this.budgetSep = nz(sep);
        this.budgetOct = nz(oct);
        this.budgetNov = nz(nov);
        this.budgetDec = nz(dec);
        this.budgetJan = nz(jan);
        this.budgetFeb = nz(feb);
        this.budgetMar = nz(mar);
        this.budgetApr = nz(apr);
        this.budgetMay = nz(may);
        this.budgetJun = nz(jun);
        this.budgetTotal = nz(total);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

}
