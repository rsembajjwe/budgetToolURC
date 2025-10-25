package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.CascadeType;
import java.math.BigDecimal;
import jakarta.persistence.Column;
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

    @Column(length = Integer.MAX_VALUE)
    private String name;

    @Column(length = 255)
    private String fundsource;

    @Column(length = Integer.MAX_VALUE)
    private String output;

    @Column(length = Integer.MAX_VALUE)
    private String performanceIndicator;

    @Column(length = Integer.MAX_VALUE)
    private String outcome;

    @Column(length = Integer.MAX_VALUE)
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

    @Column(length = 255)
    private Set<String> deliverable_outputs;

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
    private String annualTarget;
    private String cum_achievements_qtr1;
    private String cum_achievements_qtr2;
    private String cum_achievements_qtr3;
    private String cum_achievements_qtr4;
    private String perc_of_TargetAchieved_qtr1;
    private String perc_of_TargetAchieved_qtr2;
    private String perc_of_TargetAchieved_qtr3;
    private String perc_of_TargetAchieved_qtr4;
    private String expl_of_variations_qtr1;
    private String expl_of_variations_qtr2;
    private String expl_of_variations_qtr3;
    private String expl_of_variations_qtr4;


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

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuarterlyActuals> quarterlyActuals = new HashSet<>();

}
