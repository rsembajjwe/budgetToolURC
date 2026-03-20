package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(
        name = "department_general_physical_performance",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_dept_general_perf_code", columnNames = {"code"})
        }
)
@NoArgsConstructor
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DepartmentGeneralPhysicalPerformance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "code", nullable = false, length = 100)
    @ToString.Include
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
}
