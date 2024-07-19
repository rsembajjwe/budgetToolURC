package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currency")
@NoArgsConstructor
public @Data
class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long currencyid;

    private BigDecimal rate;
    private boolean enabled;
    @ManyToOne
    @JoinColumn(name = "currencydata_id")
    private CurrencyData data;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

}
