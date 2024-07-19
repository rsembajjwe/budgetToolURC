
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "DEPT_UNITS")
public @Data class DepartmentUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "UNIT", length = 50)
    private String unit;

    @Column(name = "DESCP", length = 50)
    private String description;

    @Column(name = "SECTIONID")
    private Integer sectionId;

    @Column(name = "SUNCODE")
    private String sunCode;

    @Column(name = "SUNSECTCODE")
    private String sunSectCode;
}

