package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "urcPriorityAreas")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public @Data
class URC_Priority_Areas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(length = 1000)
    private String name;

    /*    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "urcStrategicPlan_id")
    private URC_Strategic_Plan urcStrategicPlan;*/

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
    
    
    /*    @ManyToOne
    @JoinColumn(name = "ndpPlan_id")
    private NdpPlan ndpPlan; */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_area_id", nullable = true)
    private PriorityArea priorityArea;
}
