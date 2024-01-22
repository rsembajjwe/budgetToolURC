
package com.methaltech.application.data.entity.oldbgtool;


import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "PROGACTIVITIES")
public @Data class ProgramActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "PROG")
    private Integer prog;

    @Column(name = "ACTIVITIES", columnDefinition = "nvarchar(max)")
    private String activities;

    @Column(name = "FUND", columnDefinition = "varchar(max)")
    private String fund;

    @Column(name = "OUTPUT", columnDefinition = "varchar(max)")
    private String output;

    @Column(name = "PERF_IND", columnDefinition = "varchar(max)")
    private String performanceIndicator;

    @Column(name = "OUTCOME", columnDefinition = "varchar(max)")
    private String outcome;

    @Column(name = "OBJECTIVE", columnDefinition = "varchar(max)")
    private String objective;

    @Column(name = "BUDGET")
    private BigDecimal budget;

}

