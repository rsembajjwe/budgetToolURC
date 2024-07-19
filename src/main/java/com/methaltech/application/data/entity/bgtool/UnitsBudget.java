package com.methaltech.application.data.entity.bgtool;

import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UnitBudget")
@NoArgsConstructor
@Data
public class UnitsBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")    
    private User user;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
    
     @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_unit_budget",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "unit_id")
    )
    private Set<D_Unit> units;    
}
