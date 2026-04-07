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

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "urcPriorityAreas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class URC_Priority_Areas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @Column(length = 1000, nullable = false)
    private String name;

    @Column(name = "programme_code", length = 50, unique = true)
    private String programmeCode;

    @Column(name = "programme_description", length = 3000)
    private String programmeDescription;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_area_id")
    private PriorityArea priorityArea;

    @OneToMany(mappedBy = "programme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<URC_Programme_Annual_Budget> annualBudgets = new ArrayList<>();

    @OneToMany(mappedBy = "programme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<URC_Programme_Department> programmeDepartments = new ArrayList<>();

    @OneToMany(mappedBy = "programme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<URC_Programme_Logframe_Item> logframeItems = new ArrayList<>();

    @OneToMany(mappedBy = "programme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<URC_Programme_KPI> kpis = new ArrayList<>();

    @Transient
    public LocalDate getStartDate() {
        return priorityArea != null && priorityArea.getNdpPlan() != null
                ? priorityArea.getNdpPlan().getStartDate()
                : null;
    }

    @Transient
    public LocalDate getEndDate() {
        return priorityArea != null && priorityArea.getNdpPlan() != null
                ? priorityArea.getNdpPlan().getEndDate()
                : null;
    }

    @Transient
    public Integer getStartYear() {
        return getStartDate() != null ? getStartDate().getYear() : null;
    }

    @Transient
    public Integer getEndYear() {
        return getEndDate() != null ? getEndDate().getYear() : null;
    }

    public void addAnnualBudget(URC_Programme_Annual_Budget annualBudget) {
        annualBudgets.add(annualBudget);
        annualBudget.setProgramme(this);
    }

    public void removeAnnualBudget(URC_Programme_Annual_Budget annualBudget) {
        annualBudgets.remove(annualBudget);
        annualBudget.setProgramme(null);
    }

    public void addProgrammeDepartment(URC_Programme_Department pd) {
        programmeDepartments.add(pd);
        pd.setProgramme(this);
    }

    public void removeProgrammeDepartment(URC_Programme_Department pd) {
        programmeDepartments.remove(pd);
        pd.setProgramme(null);
    }

    public void addLogframeItem(URC_Programme_Logframe_Item item) {
        logframeItems.add(item);
        item.setProgramme(this);
    }

    public void removeLogframeItem(URC_Programme_Logframe_Item item) {
        logframeItems.remove(item);
        item.setProgramme(null);
    }

    public void addKpi(URC_Programme_KPI kpi) {
        kpis.add(kpi);
        kpi.setProgramme(this);
    }

    public void removeKpi(URC_Programme_KPI kpi) {
        kpis.remove(kpi);
        kpi.setProgramme(null);
    }
}
