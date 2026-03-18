package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.salaryScale;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budgetItem")
@NoArgsConstructor
public @Data
class BudgetItems implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String item;
    private String product;
    private String category;
    private Long analcode;
    private salaryScale grade;
    @Column(precision = 25, scale = 6)
    private BigDecimal cost;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "fundsource_id")
    @JsonIgnore
    private Fundsource fundsource;

    @ManyToOne
    @JoinColumn(name = "coalevel1_id")
    private Coalevel1 coalevel1;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "activity_id2")
    private Urc_Activities activity;

    @ManyToOne
    @JoinColumn(name = "dsection_id")
    private UrcDeptSectionAnlDimbgt deptUnit;

    private int units;
    @Column(precision = 25, scale = 6)
    private BigDecimal qty;
    @Column(precision = 25, scale = 6)
    private BigDecimal jul;
    @Column(precision = 25, scale = 6)
    private BigDecimal nov;
    @Column(precision = 25, scale = 6)
    private BigDecimal mar;
    @Column(precision = 25, scale = 6)
    private BigDecimal aug;
    @Column(precision = 25, scale = 6)
    private BigDecimal dec;
    @Column(precision = 25, scale = 6)
    private BigDecimal apr;
    @Column(precision = 25, scale = 6)
    private BigDecimal sep;
    @Column(precision = 25, scale = 6)
    private BigDecimal jan;
    @Column(precision = 25, scale = 6)
    private BigDecimal may;
    @Column(precision = 25, scale = 6)
    private BigDecimal oct;
    @Column(precision = 25, scale = 6)
    private BigDecimal feb;
    @Column(precision = 25, scale = 6)
    private BigDecimal jun;
    private String type;
    @Column(precision = 25, scale = 6)
    private BigDecimal total;
    @ManyToOne
    @JoinColumn(name = "coa_id")
    private COA coacode;
    @Column(length = 255)
    private String notes;
    private String bcategory;

    private String unitMeasure;
    private String target_group;
    private String expected_trainer;
    private String no_of_days;
    private String procMethod;
    private String procType;
    private String preQ;
    private String appofResv;
    private LocalDate bidInv;
    private LocalDate bidclos;
    private LocalDate apprevadate;
    private LocalDate awardnotidate;
    private LocalDate contractsigndate;
    private LocalDate completiondate;

    @ManyToOne
    @JoinColumn(name = "Organisation_id")
    private Organisation budgetType;
    @Enumerated(EnumType.STRING)
    @Column(name = "procureClass_id")
    private ProcClass procClass;

    // Helper methods
    public String getDisplayName() {
        return item;
    }

    public double getTotalCost() {
        return total != null ? total.doubleValue() : 0.0;
    }

    public double getUnitCost() {
        return cost != null ? cost.doubleValue() : 0.0;
    }

    public String getAccountCode() {
        return coacode != null ? coacode.getCode() : "N/A";
    }

    public String getAccountName() {
        return coacode != null ? coacode.getName() : "N/A";
    }

    public String getProcurementClass() {
        return procClass != null ? procClass.name() : "Not Specified";
    }

    public boolean hasValidCost() {
        return cost != null && cost.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getFormattedCost() {
        if (cost == null) {
            return "UGX 0";
        }
        return String.format("UGX %,.2f", cost.doubleValue());
    }

    public String getFormattedTotal() {
        BigDecimal tot = BigDecimal.ZERO;
        if (jul != null) {
            tot = tot.add(jul);
        }
        if (aug != null) {
            tot = tot.add(aug);
        }
        if (sep != null) {
            tot = tot.add(sep);
        }
        if (oct != null) {
            tot = tot.add(oct);
        }
        if (nov != null) {
            tot = tot.add(nov);
        }
        if (dec != null) {
            tot = tot.add(dec);
        }
        if (jan != null) {
            tot = tot.add(jan);
        }
        if (feb != null) {
            tot = tot.add(feb);
        }
        if (mar != null) {
            tot = tot.add(mar);
        }
        if (apr != null) {
            tot = tot.add(apr);
        }
        if (may != null) {
            tot = tot.add(may);
        }
        if (jun != null) {
            tot = tot.add(jun);
        }

        return String.format("UGX %,.2f", tot.doubleValue());
    }

    public double getCalculatedTotal() {
        BigDecimal tot = BigDecimal.ZERO;
        if (jul != null) {
            tot = tot.add(jul);
        }
        if (aug != null) {
            tot = tot.add(aug);
        }
        if (sep != null) {
            tot = tot.add(sep);
        }
        if (oct != null) {
            tot = tot.add(oct);
        }
        if (nov != null) {
            tot = tot.add(nov);
        }
        if (dec != null) {
            tot = tot.add(dec);
        }
        if (jan != null) {
            tot = tot.add(jan);
        }
        if (feb != null) {
            tot = tot.add(feb);
        }
        if (mar != null) {
            tot = tot.add(mar);
        }
        if (apr != null) {
            tot = tot.add(apr);
        }
        if (may != null) {
            tot = tot.add(may);
        }
        if (jun != null) {
            tot = tot.add(jun);
        }

        return tot.doubleValue();
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal getQ1Total() { // Jul-Sep
        return nvl(jul).add(nvl(aug)).add(nvl(sep));
    }

    public BigDecimal getQ2Total() { // Oct-Dec
        return nvl(oct).add(nvl(nov)).add(nvl(dec));
    }

    public BigDecimal getQ3Total() { // Jan-Mar
        return nvl(jan).add(nvl(feb)).add(nvl(mar));
    }

    public BigDecimal getQ4Total() { // Apr-Jun
        return nvl(apr).add(nvl(may)).add(nvl(jun));
    }

    public BigDecimal getYearTotalFromQuarters() {
        return getQ1Total().add(getQ2Total()).add(getQ3Total()).add(getQ4Total());
    }
}
