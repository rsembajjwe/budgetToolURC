package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "urc_programme_logframe_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class URC_Programme_Logframe_Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private LogframeItemType itemType;

    @ToString.Include
    @Column(name = "description", nullable = false, length = 3000)
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "output_type", length = 50)
    private String outputType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private URC_Priority_Areas programme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_code", referencedColumnName = "department_code")
    private Custom_URC_Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_code", referencedColumnName = "ANL_CODE")
    private UrcDeptSectionAnlDimbgt section;

    @OneToMany(mappedBy = "logframeItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<URC_Programme_KPI> kpis = new ArrayList<>();

    @OneToMany(mappedBy = "output", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<URC_Activity_Output_Link> activityOutputLinks = new HashSet<>();

    @OneToMany(mappedBy = "outcome", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<URC_Activity_Outcome_Link> activityOutcomeLinks = new HashSet<>();
}
