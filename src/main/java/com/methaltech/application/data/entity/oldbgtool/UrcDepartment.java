
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "URC_DEPTS")
public @Data class UrcDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPT_ID")
    private Integer deptId;

    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

    @Column(name = "I_ID")
    private Integer iId;

    // Constructors, getters, and setters (omitted for brevity)
}

