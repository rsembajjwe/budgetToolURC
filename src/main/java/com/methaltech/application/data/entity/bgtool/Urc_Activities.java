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
@Table(name = "urcActivities")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)

public @Data
class Urc_Activities implements Serializable {

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
    @JoinColumn(name = "dsection_id")
    private UrcDeptSectionAnlDimbgt deptSection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    private Long origid;

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
