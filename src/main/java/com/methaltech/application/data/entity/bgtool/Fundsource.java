package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fundsource")
@NoArgsConstructor
public @Data class Fundsource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String fundsource;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget; 
    /*    @ManyToMany
    @JoinTable(name = "procPlanFundsource", joinColumns = @JoinColumn(name = "fundsource_id"), inverseJoinColumns = @JoinColumn(name = "procurementPlan_id"))
    private Set<ProcurementPlan> procurementPlan; */   
}
