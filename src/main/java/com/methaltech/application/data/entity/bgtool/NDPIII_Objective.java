package com.methaltech.application.data.entity.bgtool;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Ndp111")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public @Data
class NDPIII_Objective implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(length = 1000)
    private String objective;
    @ToString.Include
    private BigDecimal obj_budget;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
}
