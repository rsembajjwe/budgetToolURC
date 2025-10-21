package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "QuarterlyActuals")
public @Data
class QuarterlyActuals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCNT_CODE", nullable = false, length = 50)
    private String accountCode;

    @Column(name = "PERIOD", nullable = false)
    private Integer period;

    @Column(name = "TRANS_DATETIME", nullable = false)
    private LocalDateTime transactionDateTime;

    @Column(name = "JRNAL_NO", nullable = false)
    private Integer journalNo;

    @Column(name = "JRNAL_LINE", nullable = false)
    private Integer journalLine;

    @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "DESCRIPTN", nullable = false, length = 50)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Urc_Activities activity;

}
