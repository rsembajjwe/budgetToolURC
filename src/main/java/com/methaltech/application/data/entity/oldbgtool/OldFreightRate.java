
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "FREIGHTRATES")
public @Data class OldFreightRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(name = "NAME", length = 150)
    private String name;

    @Column(name = "CARGO_TYPE", length = 150)
    private String cargoType;

    @Column(name = "CARGO_DESC", length = 200)
    private String cargoDescription;

    @Column(name = "RATE")
    private BigDecimal rate;

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

    @Column(name = "CODE", length = 50)
    private String code;

    @Column(name = "FY", length = 50)
    private String fiscalYear;

    @Column(name = "ROUTE", length = 50)
    private String route;

    @Column(name = "CURRENCY", length = 50)
    private String currency; 
}
