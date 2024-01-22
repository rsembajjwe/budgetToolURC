package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "Budget")
public @Data class OldBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Budget_Id")
    private int budgetId;

    @Column(name = "Financial_Year")
    private String financialYear;

    @Column(name = "Open_Date")
    private LocalDate openDate;

    @Column(name = "Close_Date")
    private LocalDate closeDate;

    @Column(name = "rate")
    private float rate;

    @Column(name = "Status")
    private String status;

    @Column(name = "Savehash")
    private String savehash;
}
