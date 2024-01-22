
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PROGRAMME")
public @Data class OldProgramme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "DEPTID")
    private Integer deptId;

    @Column(name = "FY")
    private String fy;

    @Lob
    @Column(name = "PROGRAMME", columnDefinition = "nvarchar(max)")
    private String programme;    
}
