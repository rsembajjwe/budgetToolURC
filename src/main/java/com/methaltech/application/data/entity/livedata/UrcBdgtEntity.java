
package com.methaltech.application.data.entity.livedata;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "URC_BDGT")
public class UrcBdgtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BDGT_ID")
    private int bdgtId;

    @Column(name = "UPDATE_COUNT", nullable = false)
    private int updateCount;

    @Column(name = "LAST_CHANGE_USER_ID", nullable = false)
    private int lastChangeUserId;

    @Column(name = "LAST_CHANGE_DATETIME", nullable = false)
    @CreatedDate
    private Date lastChangeDatetime;

    @Column(name = "STATUS", nullable = false)
    private int status;

    @Column(name = "ACNT_CODE")
    private String acntCode;

    @Column(name = "ANL_CODE_1")
    private String anlCode1;

    @Column(name = "ANL_CODE_2")
    private String anlCode2;

    @Column(name = "ANL_CODE_3")
    private String anlCode3;

    @Column(name = "ANL_CODE_4")
    private String anlCode4;

    @Column(name = "ANL_CODE_5")
    private String anlCode5;

    @Column(name = "PERD_FROM", nullable = false)
    private Date perdFrom;

    @Column(name = "TOTAL_BDGT", nullable = false)
    private BigDecimal totalBdgt;

    @Column(name = "COMMIT_AMT", nullable = false)
    private BigDecimal commitAmt;

    @Column(name = "SPENT_AMT", nullable = false)
    private BigDecimal spentAmt;

    @Column(name = "BDGT_DR_CR", nullable = false)
    private int bdgtDrCr;

    @Column(name = "COMMIT_DR_CR", nullable = false)
    private int commitDrCr;

    @Column(name = "SPENT_DR_CR", nullable = false)
    private int spentDrCr;

    @Column(name = "SOFT_COMMIT_AMT", nullable = false)
    private BigDecimal softCommitAmt;

    @Column(name = "SOFT_COMMIT_DR_CR", nullable = false)
    private int softCommitDrCr;

    @ManyToOne
    @JoinColumn(name = "ACNT_CODE", referencedColumnName = "ACNT_CODE", insertable = false, updatable = false)
    private URC_ACNT urcAcnt;
}
