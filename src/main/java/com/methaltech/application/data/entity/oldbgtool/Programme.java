
package com.methaltech.application.data.entity.oldbgtool;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PROGRAMME")
public @Data class Programme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "DEPTID")
    private Integer deptId;

    @Column(name = "FY")
    private String fy;

    @Column(name = "PROGRAMME", columnDefinition = "nvarchar(max)")
    private String programme;

}
