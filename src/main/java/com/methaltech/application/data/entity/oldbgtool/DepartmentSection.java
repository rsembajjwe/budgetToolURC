
package com.methaltech.application.data.entity.oldbgtool;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "DEPT_SECTION")
public @Data class DepartmentSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "SECTION", length = 50)
    private String section;

    @Column(name = "DESCP", length = 50)
    private String description;

    @Column(name = "DEPT_ID")
    private Integer deptId;

    // Constructors, getters, and setters (omitted for brevity)
}

