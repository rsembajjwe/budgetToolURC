
package com.methaltech.application.data.entity.bgtool;

// package com.example.ndp.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "priority_area")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PriorityArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** e.g. "Value Addition & Industrialization" */
    @Column(nullable = true, length = 200)
    private String name;

    /** longer description / details */
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ndp_plan_id", nullable = true)
    private NdpPlan ndpPlan;

    @OneToMany(mappedBy = "priorityArea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<URC_Priority_Areas> programmes = new ArrayList<>();

    public void addProgramme(URC_Priority_Areas p) {
        p.setPriorityArea(this);
        this.programmes.add(p);
    }

    public void removeProgramme(URC_Priority_Areas p) {
        this.programmes.remove(p);
        p.setPriorityArea(null);
    }
}

