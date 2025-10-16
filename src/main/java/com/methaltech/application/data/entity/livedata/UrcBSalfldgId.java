
package com.methaltech.application.data.entity.livedata;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Embeddable
public @Data class UrcBSalfldgId implements Serializable {
    
    @Column(name = "ACCNT_CODE", nullable = false)
    private String accntCode;

    @Column(name = "PERIOD", nullable = false)
    private Integer period;

    @Column(name = "TRANS_DATETIME", nullable = false)
    private LocalDateTime transDatetime;

    @Column(name = "JRNAL_NO", nullable = false)
    private String jrnNo;

    @Column(name = "JRNAL_LINE", nullable = false)
    private Integer jrnLine;

    // Getters and Setters
}
