package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "urc_strategic_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrcStrategicPlan {

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

    /*    @OneToMany(mappedBy = "strategicPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<URC_Priority_Areas> priorityAreas = new HashSet<>();
    
    @OneToMany(mappedBy = "strategicPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UrcStrategicObjectives> strategicObjectives = new HashSet<>();*/

}
