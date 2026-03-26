
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "overall_general_physical_performance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_overall_general_perf_code_budget",
                        columnNames = {"code", "budget_id"}
                )
        }
)
@Data
@NoArgsConstructor
public class OverallGeneralPhysicalPerformance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", length = 255)
    private String name;

    @Lob
    @Column(name = "qtr1", columnDefinition = "nvarchar(max)")
    private String qtr1;

    @Lob
    @Column(name = "qtr2", columnDefinition = "nvarchar(max)")
    private String qtr2;

    @Lob
    @Column(name = "qtr3", columnDefinition = "nvarchar(max)")
    private String qtr3;

    @Lob
    @Column(name = "qtr4", columnDefinition = "nvarchar(max)")
    private String qtr4;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_id")
    private Budget budget;
}
