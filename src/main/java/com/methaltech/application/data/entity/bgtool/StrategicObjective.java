
package com.methaltech.application.data.entity.bgtool;

// package com.example.ndp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "strategic_objective")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StrategicObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** short title, e.g. "Enhance human capital development" */
    @Column(nullable = false, length = 300)
    private String title;

    /** longer explanation */
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ndp_plan_id", nullable = false)
    private NdpPlan ndpPlan;
}

