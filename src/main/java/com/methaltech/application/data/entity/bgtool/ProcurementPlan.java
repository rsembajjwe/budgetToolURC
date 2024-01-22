package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "procurementPlan")
@NoArgsConstructor
public @Data
class ProcurementPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String subject;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Column(precision = 25, scale = 6)
    private BigDecimal cost;

    @ManyToOne
    @JoinColumn(name = "fundsource_id") // Adjust the column name as needed
    private Fundsource fundsource;

    @ManyToOne
    @JoinColumn(name = "procurementmethod_id")
    private ProcurementMethod procurementmethod;

    @ManyToOne
    @JoinColumn(name = "procurementtype_id")
    private ProcurementType procurementtype;

    private boolean prequal;
    private boolean reserve;
    private Date invite;
    private Date close;
    private Date evaluation;
    private Date notification;
    private Date signing;
    private Date completion;
}
