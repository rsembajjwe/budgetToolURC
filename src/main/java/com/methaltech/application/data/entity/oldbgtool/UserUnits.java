
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "USERUNITS")
public @Data class UserUnits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "USERID")
    private Integer userId;

    @Column(name = "UNITID")
    private Integer unitId;
}

