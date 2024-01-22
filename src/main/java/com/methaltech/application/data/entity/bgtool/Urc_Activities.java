package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "urcActivities")
@NoArgsConstructor
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
public @Data
class Urc_Activities {

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
    private BigDecimal Jul;

    @Column(precision = 19, scale = 2)
    private BigDecimal Nov;

    @Column(precision = 19, scale = 2)
    private BigDecimal Mar;

    @Column(precision = 19, scale = 2)
    private BigDecimal Aug;

    @Column(precision = 19, scale = 2)
    private BigDecimal Dec;

    @Column(precision = 19, scale = 2)
    private BigDecimal Apr;

    @Column(precision = 19, scale = 2)
    private BigDecimal Sep;

    @Column(precision = 19, scale = 2)
    private BigDecimal Jan;

    @Column(precision = 19, scale = 2)
    private BigDecimal May;

    @Column(precision = 19, scale = 2)
    private BigDecimal Oct;

    @Column(precision = 19, scale = 2)
    private BigDecimal Feb;

    @Column(precision = 19, scale = 2)
    private BigDecimal Jun;

    @Column(precision = 19, scale = 2)
    private BigDecimal Total;

    @Column(precision = 19, scale = 2)
    private BigDecimal month;

    @Column(precision = 19, scale = 2)
    private BigDecimal activity_budget;

    @ManyToOne
    @JoinColumn(name = "urcPriorityAreas_id")
    private URC_Priority_Areas urcPriorityAreas;

    @ManyToOne
    @JoinColumn(name = "dsection_id")
    private UrcDeptSectionAnlDimbgt deptSection;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    private Long origid;
}

