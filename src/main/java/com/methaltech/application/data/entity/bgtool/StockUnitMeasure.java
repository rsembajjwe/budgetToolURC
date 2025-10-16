package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import java.sql.Timestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "StockUnitMeasure")
@NoArgsConstructor
public @Data class StockUnitMeasure  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "unit")
    private String unit;

    @Column(name = "lastModified")
    private Date lastModified;

    @Column(name = "changed")
    private boolean changed;
}
