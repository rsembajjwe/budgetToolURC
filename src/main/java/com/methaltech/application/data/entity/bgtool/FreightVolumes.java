package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "freightVolumes")
@NoArgsConstructor
public @Data class FreightVolumes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    String name;

    String cargotype;

    String cargo_desc;

    BigDecimal rate;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    BigDecimal jul;

    BigDecimal aug;

    BigDecimal sep;

    BigDecimal oct;

    BigDecimal nov;

    BigDecimal dec;

    BigDecimal jan;

    BigDecimal feb;

    BigDecimal mar;

    BigDecimal apr;

    BigDecimal may;

    BigDecimal jun;

    BigDecimal total;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private COA coacode;
   
}
