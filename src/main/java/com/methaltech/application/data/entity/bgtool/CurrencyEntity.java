package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Currency")
public @Data class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Currency")
    private String currency;

    @Column(name = "CurrencyShort")
    private String currencyShort;

    @Column(name = "Rate")
    private Double rate;

    @Column(name = "Fy")
    private String fy;
}
