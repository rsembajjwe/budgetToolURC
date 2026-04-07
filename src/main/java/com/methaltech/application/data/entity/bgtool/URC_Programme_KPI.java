package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "urc_programme_kpis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class URC_Programme_KPI implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @Column(name = "kpi_name", nullable = false, length = 1000)
    private String kpiName;

    @Column(name = "unit", nullable = false, length = 100)
    private String unit;

    @Column(name = "description", length = 3000)
    private String description;

    @Column(name = "baseline_value", precision = 18, scale = 2)
    private BigDecimal baselineValue;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private URC_Priority_Areas programme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logframe_item_id")
    private URC_Programme_Logframe_Item logframeItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_code", referencedColumnName = "department_code")
    private Custom_URC_Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_code", referencedColumnName = "ANL_CODE")
    private UrcDeptSectionAnlDimbgt section;

    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("reportingPeriod ASC")
    private List<URC_KPI_Performance_Record> performanceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<URC_Activity_KPI_Link> activityLinks = new HashSet<>();

    public void addPerformanceRecord(URC_KPI_Performance_Record record) {
        performanceRecords.add(record);
        record.setKpi(this);
    }

    public void removePerformanceRecord(URC_KPI_Performance_Record record) {
        performanceRecords.remove(record);
        record.setKpi(null);
    }
}
