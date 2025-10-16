
package com.methaltech.application.data.entity.livedata;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "URC_ACNT")
public class UrcAcnt implements Serializable {

    @Id
    @Column(name = "ACNT_CODE")
    private String acntCode;

    @Column(name = "UPDATE_COUNT")
    private int updateCount;

    @Column(name = "LAST_CHANGE_USER_ID")
    private String lastChangeUserId;

    @Column(name = "LAST_CHANGE_DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChangeDatetime;

    @Column(name = "DESCR")
    private String description;

    @Column(name = "S_HEAD")
    private String shortHeading;

    @Column(name = "LOOKUP")
    private String lookup;

    @Column(name = "DAG_CODE")
    private String dataAccessGroup;

    @Column(name = "ACNT_TYPE")
    private int accountType;

    @Column(name = "BAL_TYPE")
    private int balanceType;

    @Column(name = "STATUS")
    private int status;

    @Column(name = "SUPPRESS_REVAL")
    private int suppressRevaluation;

    @Column(name = "LONG_DESCR")
    private String longDescription;

    @Column(name = "CONV_CODE_CTRL")
    private int conversionCodeControl;

    @Column(name = "DFLT_CURR_CODE")
    private String defaultCurrencyCode;

    @Column(name = "ALLOCN_IN_PROGRESS")
    private int allocationInProgress;

    @Column(name = "LINK_ACNT")
    private String linkAccountCode;

    @Column(name = "RPT_CONV_CTRL")
    private int reportConversionControl;

    @Column(name = "USER_AREA")
    private String userArea;

    @Column(name = "CR_LIMIT")
    private BigDecimal creditLimit;

    @Column(name = "ENTER_ANL_1")
    private int enterAnalysis1;

    @Column(name = "ENTER_ANL_2")
    private int enterAnalysis2;

    @Column(name = "ENTER_ANL_3")
    private int enterAnalysis3;

    @Column(name = "ENTER_ANL_4")
    private int enterAnalysis4;

    @Column(name = "ENTER_ANL_5")
    private int enterAnalysis5;

    @Column(name = "ENTER_ANL_6")
    private int enterAnalysis6;

    @Column(name = "ENTER_ANL_7")
    private int enterAnalysis7;

    @Column(name = "ENTER_ANL_8")
    private int enterAnalysis8;

    @Column(name = "ENTER_ANL_9")
    private int enterAnalysis9;

    @Column(name = "ENTER_ANL_10")
    private int enterAnalysis10;

    @Column(name = "OIL")
    private BigDecimal oil;

    @Column(name = "CV4_DFLT_CURR_CODE")
    private String cv4DefaultCurrencyCode;

    @Column(name = "CV4_CONV_CODE_CTRL")
    private int cv4ConversionCodeControl;

    @Column(name = "CV5_DFLT_CURR_CODE")
    private String cv5DefaultCurrencyCode;

    @Column(name = "CV5_CONV_CODE_CTRL")
    private int cv5ConversionCodeControl;

    @Column(name = "BANK_CURR_REQD")
    private int bankCurrencyRequired;

    @Column(name = "ACNT_LINKS_ALLOWED")
    private int accountLinksAllowed;

    @Column(name = "PASP_ACNT_TYPE")
    private int paspAccountType;

    @Column(name = "DR_CR")
    private int drCr;

    @Column(name = "ACNT_SUB_TYPE")
    private Integer accountSubType;

    @Column(name = "WT_CALC")
    private int weightCalculation;

    @Column(name = "WT_ACNT_TAX_CLASS")
    private int weightAccountTaxClass;

    @Column(name = "WT_NORMAL_SIGN")
    private int weightNormalSign;

}

