
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "URC_DEPTSECTION_ANL_DIM_V")
public @Data class UrcDeptSectionAnlDimbgt {
    @Id
    @Column(name = "ANL_CODE", columnDefinition = "VARCHAR(15)")
    private String ANL_CODE;
    @Column(name = "ANL_CAT_ID", columnDefinition = "VARCHAR(5)")
    private String ANL_CAT_ID;
    @Column(name = "NAME")
    private String NAME;    
}
