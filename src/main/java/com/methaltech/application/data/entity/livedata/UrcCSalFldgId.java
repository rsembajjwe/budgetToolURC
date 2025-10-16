
package com.methaltech.application.data.entity.livedata;
import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class UrcCSalFldgId implements Serializable {
    @Column(name = "ACCNT_CODE")
    private String accntCode;

    @Column(name = "PERIOD")
    private Integer period;

    @Column(name = "TRANS_DATETIME")
    private LocalDateTime transDatetime;

    @Column(name = "JRNAL_NO")
    private Integer journalNo;

    @Column(name = "JRNAL_LINE")
    private Integer journalLine;
}
