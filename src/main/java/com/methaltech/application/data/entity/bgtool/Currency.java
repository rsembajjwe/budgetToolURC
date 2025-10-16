package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currency")
@NoArgsConstructor
public @Data
class Currency   implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long currencyid;
   @Column(name = "[rate]", columnDefinition = "numeric(38,2)")
    private BigDecimal rate;
    private boolean enabled;
    @ManyToOne
    @JoinColumn(name = "currencydata_id")
    private CurrencyData data;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

}
