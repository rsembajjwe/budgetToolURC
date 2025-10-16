package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "StockUnitMeasure")
@NoArgsConstructor
public @Data
class StockUnitPojo  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "UserID")
    private Integer  userId;

    @Column(name = "LastModified")
    private Timestamp lastModified;

    /*    @Column(name = "Changed")
    private boolean changed=false;*/
}
