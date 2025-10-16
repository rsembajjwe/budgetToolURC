package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "ndp_plan",
        indexes = {
            @Index(name = "idx_ndp_name", columnList = "name")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NdpPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL Server IDENTITY
    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(length = 500)
    private String theme;

    @Column(length = 1000)
    private String ultimateGoal;

    @OneToMany(mappedBy = "ndpPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PriorityArea> priorityAreas = new HashSet<>();

    @OneToMany(mappedBy = "ndpPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StrategicObjective> strategicObjectives = new HashSet<>();

    // Helper methods to maintain bi-directional relationships
    public void addPriorityArea(PriorityArea area) {
        area.setNdpPlan(this);
        this.priorityAreas.add(area);
    }

    public void removePriorityArea(PriorityArea area) {
        this.priorityAreas.remove(area);
        area.setNdpPlan(null);
    }

    public void addStrategicObjective(StrategicObjective obj) {
        obj.setNdpPlan(this);
        this.strategicObjectives.add(obj);
    }

    public void removeStrategicObjective(StrategicObjective obj) {
        this.strategicObjectives.remove(obj);
        obj.setNdpPlan(null);
    }
}
