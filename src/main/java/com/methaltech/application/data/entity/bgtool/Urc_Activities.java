package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "urcActivities")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Important!
public @Data
class Urc_Activities implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String activityCode;

    @Column(length = 500)
    private String name;

    @Column(length = 500)
    private String fundsource;

    @Column(length = 500)
    private String output;

    @Column(length = 500)
    private String performanceIndicator;

    @Column(length = 500)
    private String outcome;

    @Column(length = 500)
    private String objective;

    @Column(precision = 19, scale = 2)
    private BigDecimal bdgt;

    @Column(precision = 19, scale = 2)
    private BigDecimal Total;

    @Column(precision = 19, scale = 2)
    private BigDecimal month;

    @Column(precision = 19, scale = 2)
    private BigDecimal activity_budget;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "urcPriorityAreas_id")
    private URC_Priority_Areas urcPriorityAreas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "objectives_id")
    private UrcStrategicObjectives objectives;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dsection_id")
    private UrcDeptSectionAnlDimbgt deptSection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ElementCollection
    @CollectionTable(name = "activity_deliverable_outputs", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "deliverable_output")
    private Set<String> deliverable_outputs = new HashSet<>();

    @Column(precision = 19, scale = 2)
    private BigDecimal qtr1;
    @Column(precision = 19, scale = 2)
    private BigDecimal qtr2;
    @Column(precision = 19, scale = 2)
    private BigDecimal qtr3;
    @Column(precision = 19, scale = 2)
    private BigDecimal qtr4;

    @Column(precision = 19, scale = 2)
    private BigDecimal qtr1A;
    @Column(precision = 19, scale = 2)
    private BigDecimal qtr2A;
    @Column(precision = 19, scale = 2)
    private BigDecimal qtr3A;
    @Column(precision = 19, scale = 2)
    private BigDecimal qtr4A;

    @Column(precision = 19, scale = 2)
    private BigDecimal TotalA;

    private Long origid;
    @Column(length = 500)
    private String annualTarget;
    @Column(length = 500)
    private String cum_achievements_qtr1;
    @Column(length = 500)
    private String cum_achievements_qtr2;
    @Column(length = 500)
    private String cum_achievements_qtr3;
    @Column(length = 500)
    private String cum_achievements_qtr4;
    @Column(length = 500)
    private String perc_of_TargetAchieved_qtr1;
    @Column(length = 500)
    private String perc_of_TargetAchieved_qtr2;
    @Column(length = 500)
    private String perc_of_TargetAchieved_qtr3;
    @Column(length = 500)
    private String perc_of_TargetAchieved_qtr4;
    @Column(length = 500)
    private String expl_of_variations_qtr1;
    @Column(length = 500)
    private String expl_of_variations_qtr2;
    @Column(length = 500)
    private String expl_of_variations_qtr3;
    @Column(length = 500)
    private String expl_of_variations_qtr4;

    @Column(name = "perc_of_TargetAchieved_qtr11")
    private Double perc_of_TargetAchieved_qtr11;

    @Column(name = "perc_of_TargetAchieved_qtr21")
    private Double perc_of_TargetAchieved_qtr21;

    @Column(name = "perc_of_TargetAchieved_qtr31")
    private Double perc_of_TargetAchieved_qtr31;

    @Column(name = "perc_of_TargetAchieved_qtr41")
    private Double perc_of_TargetAchieved_qtr41;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_annual_budget_id")
    private URC_Programme_Annual_Budget programmeAnnualBudget;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuarterlyActuals> quarterlyActuals = new HashSet<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<URC_Activity_Output_Link> outputLinks = new HashSet<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<URC_Activity_KPI_Link> kpiLinks = new HashSet<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<URC_Activity_Outcome_Link> outcomeLinks = new HashSet<>();

    // Helper methods
    public String getDisplayName() {
        return activityCode + " - " + name;
    }

    public double getTotalBudgetAmount() {
        return activity_budget != null ? activity_budget.doubleValue() : 0.0;
    }

    public String getFundingSource() {
        return fundsource != null ? fundsource : "Not Specified";
    }

    public boolean hasValidBudget() {
        return activity_budget != null && activity_budget.compareTo(BigDecimal.ZERO) > 0;
    }


}
