
package com.methaltech.application.data.entity.bgtool;

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
@Table(name = "nationalTransportMasterPlan")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public @Data class National_Transport_Master_Plan {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(length = 1000)
    private String name;   
    @ManyToOne
    @JoinColumn(name = "ndp111Objective_id")
    private NDPIII_Objective ndp111Objective;     
}
