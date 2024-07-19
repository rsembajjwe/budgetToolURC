
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "DEPT_SECTION")
public @Data class OldDeptSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "SECTION")
    private String section;

    @Column(name = "DESCP")
    private String description;

    @Column(name = "DEPT_ID")
    private Integer deptId;

    @Column(name = "SUNCODE")
    private String sunCode;    
}
