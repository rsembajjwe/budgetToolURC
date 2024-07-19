
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incomesources")
@NoArgsConstructor
public @Data class IncomeSources {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "incomesource") 
    private String incomeSource;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;    
}
