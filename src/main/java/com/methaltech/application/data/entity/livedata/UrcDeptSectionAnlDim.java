package com.methaltech.application.data.entity.livedata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "URC_DEPTSECTION_ANL_DIM_V")
public @Data
class UrcDeptSectionAnlDim {

    @Id
    @Column(name = "ANL_CODE", columnDefinition = "VARCHAR(255)")
    private String ANL_CODE;
    @Column(name = "ANL_CAT_ID", columnDefinition = "VARCHAR(255)")
    @JdbcTypeCode(SqlTypes.JSON)
    private String ANL_CAT_ID;
    @Column(name = "NAME")
    private String NAME;

}
