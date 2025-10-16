package com.methaltech.application.data.entity.livedata;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "URC_DEPTUNIT_ANL_DIM_V")
public @Data class UrcDeptUnitAnlDimView {

    @Id
    @Column(name = "ANL_CODE")
    private String ANLCODE;

    @Column(name = "ANL_CAT_ID")
    private String ANL_CAT_ID;

    @Column(name = "NAME")
    private String NAME;

    @Column(name = "LOOKUP")
    private String LOOKUP;
}
