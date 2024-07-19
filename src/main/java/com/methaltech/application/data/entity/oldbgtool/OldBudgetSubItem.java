
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import java.sql.Date;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "BudgetSubItems")
public @Data class OldBudgetSubItem {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ITEM")
    private String item;

    @Column(name = "PRODUCT")
    private String product;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "COST")
    private BigDecimal cost;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "UNITS")
    private Integer units;

    @Column(name = "QTY")
    private BigDecimal qty;

    @Column(name = "JUL")
    private BigDecimal jul;

    @Column(name = "AUG")
    private BigDecimal aug;

    @Column(name = "SEP")
    private BigDecimal sep;

    @Column(name = "OCT")
    private BigDecimal oct;

    @Column(name = "NOV")
    private BigDecimal nov;

    @Column(name = "DEC")
    private BigDecimal dec;

    @Column(name = "JAN")
    private BigDecimal jan;

    @Column(name = "FEB")
    private BigDecimal feb;

    @Column(name = "MAR")
    private BigDecimal mar;

    @Column(name = "APR")
    private BigDecimal apr;

    @Column(name = "MAY")
    private BigDecimal may;

    @Column(name = "JUN")
    private BigDecimal jun;

    @Column(name = "DEPTUNIT")
    private Integer deptunit;

    @Column(name = "FY")
    private String fy;

    @Column(name = "TOTAL")
    private BigDecimal total;

    @Column(name = "CODEID")
    private Integer codeid;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "ActivityID")
    private Integer activityID;

    @Column(name = "BCATEGORY")
    private String bcategory;

    @Column(name = "SECT")
    private Integer sect;

    @Column(name = "DEPT")
    private Integer dept;

    @Column(name = "ITEMUNIT")
    private String itemunit;

    @Column(name = "PROGACTIVITY")
    private Integer progactivity;

    @Column(name = "Target_group")
    private String targetGroup;

    @Column(name = "Expected_trainer")
    private String expectedTrainer;

    @Column(name = "No_of_days")
    private String noOfDays;

    @Column(name = "Type")
    private String type;

    @Column(name = "sourceofFund")
    private String sourceOfFund;

    @Column(name = "proMethod")
    private String proMethod;

    @Column(name = "proType")
    private String proType;

    @Column(name = "preQua")
    private String preQua;

    @Column(name = "AppliofReservSch")
    private String appliOfReservSch;

    @Column(name = "bidinvdate")
    private LocalDate bidInvDate;

    @Column(name = "bidclosdate")
    private LocalDate bidClosDate;

    @Column(name = "appEvalDate")
    private LocalDate appEvalDate;

    @Column(name = "awardNotiDate")
    private LocalDate awardNotiDate;

    @Column(name = "contractSignDate")
    private LocalDate contractSignDate;

    @Column(name = "compliDate")
    private LocalDate compliDate;   
}
