
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "DEPT_UNITS")
public @Data class OldDeptUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "DESCP")
    private String description;

    @Column(name = "SECTIONID")
    private Long sectionId; 
    
    @Column(name = "SUNCODE")
    private String sunCode; 
    
    @Column(name = "SUNSECTCODE")
    private String sunSectCode;     
    
}
