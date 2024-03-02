package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budgetItem")
@NoArgsConstructor
public @Data
class BudgetItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String item;
    private String product;
    private String category;
    private Long analcode;
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
    /*    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "activity_id")
    private Urc_Activities progactivity;*/

 /*    @ManyToOne
    @JoinColumn(name = "unitMeasure_id")*/
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
}
