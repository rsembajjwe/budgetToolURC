package com.methaltech.application.data.entity.livedata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "URC_ACNT")
@NoArgsConstructor
public @Data
class UR5_ACNT {

    @Id
    @Column(name = "ACNT_CODE", nullable = false)
    private String acntCode;

    @Column(name = "UPDATE_COUNT")
    private Integer updateCount;

    @Column(name = "LAST_CHANGE_USER_ID", length = 255)
    private String lastChangeUserId;

    @Column(name = "LAST_CHANGE_DATETIME", length = 255)
    private String lastChangeDatetime;

    @Column(name = "DESCR", nullable = false)
    private String descr;

    @Column(name = "S_HEAD", length = 255)
    private String sHead;

    @Column(name = "LOOKUP", nullable = false)
    private String lookup;

    @Column(name = "DAG_CODE")
    private String dagCode;

    @Column(name = "ACNT_TYPE", nullable = false)
    private Integer acntType;

    @Column(name = "BAL_TYPE", nullable = false)
    private Integer balType;

    @Column(name = "STATUS", nullable = false)
    private Integer status;

    @Column(name = "SUPPRESS_REVAL", nullable = false)
    private Integer suppressReval;

    @Column(name = "LONG_DESCR")
    private String longDescr;

    @Column(name = "CONV_CODE_CTRL", nullable = false)
    private Integer convCodeCtrl;

    @Column(name = "DFLT_CURR_CODE")
    private String dfltCurrCode;

    @Column(name = "ALLOCN_IN_PROGRESS", nullable = false)
    private Integer allocnInProgress;

    @Column(name = "LINK_ACNT", length = 255)
    private String linkAcnt;

    @Column(name = "RPT_CONV_CTRL", nullable = false)
    private Integer rptConvCtrl;

    @Column(name = "USER_AREA", length = 255)
    private String userArea;

    @Column(name = "CR_LIMIT")
    private Long crLimit;

    @Column(name = "ENTER_ANL_1", nullable = false)
    private Integer enterAnl1;

    @Column(name = "ENTER_ANL_2", nullable = false)
    private Integer enterAnl2;

    @Column(name = "ENTER_ANL_3", nullable = false)
    private Integer enterAnl3;

    @Column(name = "ENTER_ANL_4", nullable = false)
    private Integer enterAnl4;

    @Column(name = "ENTER_ANL_5", nullable = false)
    private Integer enterAnl5;

    @Column(name = "ENTER_ANL_6", nullable = false)
    private Integer enterAnl6;

    @Column(name = "ENTER_ANL_7", nullable = false)
    private Integer enterAnl7;

    @Column(name = "ENTER_ANL_8", nullable = false)
    private Integer enterAnl8;

    @Column(name = "ENTER_ANL_9", nullable = false)
    private Integer enterAnl9;

    @Column(name = "ENTER_ANL_10", nullable = false)
    private Integer enterAnl10;

    @Column(name = "OIL")
    private Long oil;

    @Column(name = "CV4_DFLT_CURR_CODE")
    private String cv4DfltCurrCode;

    @Column(name = "CV4_CONV_CODE_CTRL", nullable = false)
    private Integer cv4ConvCodeCtrl;

    @Column(name = "CV5_DFLT_CURR_CODE")
    private String cv5DfltCurrCode;

    @Column(name = "CV5_CONV_CODE_CTRL", nullable = false)
    private Integer cv5ConvCodeCtrl;

    @Column(name = "BANK_CURR_REQD", nullable = false)
    private Integer bankCurrReqd;

    @Column(name = "ACNT_LINKS_ALLOWED", nullable = false)
    private Integer acntLinksAllowed;

    @Column(name = "PASP_ACNT_TYPE", nullable = false)
    private Integer paspAcntType;

    @Column(name = "DR_CR")
    private Integer drCr;

    @Column(name = "ACNT_SUB_TYPE")
    private Short acntSubType;

    @Column(name = "WT_CALC", nullable = false)
    private Integer wtCalc;

    @Column(name = "WT_ACNT_TAX_CLASS", nullable = false)
    private Integer wtAcntTaxClass;

    @Column(name = "WT_NORMAL_SIGN", nullable = false)
    private Integer wtNormalSign;
}
