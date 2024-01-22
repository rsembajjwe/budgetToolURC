
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "BudgetSubItems")
public @Data class BudgetSubItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ITEM", columnDefinition = "varchar(max)")
    private String item;

    @Column(name = "PRODUCT", length = 100)
    private String product;

    @Column(name = "CATEGORY", length = 100)
    private String category;

    @Column(name = "COST")
    private BigDecimal cost;

    @Column(name = "CURRENCY", length = 50)
    private String currency;

    @Column(name = "UNITS")
    private Integer units;

    @Column(name = "QTY")
    private BigDecimal quantity;

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
    private Integer deptUnit;

    @Column(name = "FY", length = 100)
    private String fiscalYear;

    @Column(name = "TOTAL")
    private BigDecimal total;

    @Column(name = "CODEID")
    private Integer codeId;

    @Column(name = "NOTES", columnDefinition = "varchar(max)")
    private String notes;

    @Column(name = "ActivityID")
    private Integer activityId;

    @Column(name = "BCATEGORY", length = 50)
    private String bCategory;

    @Column(name = "SECT")
    private Integer sect;

    @Column(name = "DEPT")
    private Integer dept;

    @Column(name = "ITEMUNIT", length = 50)
    private String itemUnit;

    @Column(name = "PROGACTIVITY")
    private Integer progActivity;

    @Column(name = "Target_group", length = 50)
    private String targetGroup;

    @Column(name = "Expected_trainer", length = 50)
    private String expectedTrainer;

    @Column(name = "No_of_days", length = 50)
    private String numberOfDays;

    @Column(name = "Type", length = 50)
    private String type;

    @Column(name = "sourceofFund", length = 250)
    private String sourceOfFund;

    @Column(name = "proMethod", length = 250)
    private String proMethod;

    @Column(name = "proType", length = 250)
    private String proType;

    @Column(name = "preQua", length = 250)
    private String preQualification;

    @Column(name = "AppliofReservSch", length = 250)
    private String applicationOfReserveSchedule;

    @Column(name = "bidinvdate")
    @Temporal(TemporalType.DATE)
    private Date bidInvitationDate;

    @Column(name = "bidclosdate")
    @Temporal(TemporalType.DATE)
    private Date bidClosureDate;

    @Column(name = "appEvalDate")
    @Temporal(TemporalType.DATE)
    private Date applicationEvaluationDate;

    @Column(name = "awardNotiDate")
    @Temporal(TemporalType.DATE)
    private Date awardNotificationDate;

    @Column(name = "contractSignDate")
    @Temporal(TemporalType.DATE)
    private Date contractSigningDate;

    @Column(name = "compliDate")
    @Temporal(TemporalType.DATE)
    private Date complianceDate;
}

