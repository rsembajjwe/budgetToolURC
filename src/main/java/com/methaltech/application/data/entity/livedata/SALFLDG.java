package com.methaltech.application.data.entity.livedata;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "URC_A_SALFLDG_View")
public @Data
class SALFLDG {

    @Id
    @Column(name = "ACCNT_CODE")
    private String accntCode;

    @Column(name = "PERIOD")
    private int period;

    @Column(name = "TRANS_DATETIME")
    private Date transDatetime;

    @Column(name = "JRNAL_NO")
    private int jrnalNo;

    @Column(name = "JRNAL_LINE")
    private int jrnalLine;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "D_C")
    private String dC;

    @Column(name = "ALLOCATION")
    private String allocation;

    @Column(name = "JRNAL_TYPE")
    private String jrnalType;

    @Column(name = "JRNAL_SRCE")
    private String jrnalSrce;

    @Column(name = "TREFERENCE")
    private String tReference;

    @Column(name = "DESCRIPTN")
    private String descriptn;

    @Column(name = "ENTRY_DATETIME")
    private Date entryDatetime;

    @Column(name = "ENTRY_PRD")
    private int entryPrd;

    @Column(name = "DUE_DATETIME")
    private Date dueDatetime;

    @Column(name = "ALLOC_REF")
    private String allocRef;

    @Column(name = "ALLOC_DATETIME")
    private Date allocDatetime;

    @Column(name = "ALLOC_PERIOD")
    private int allocPeriod;

    @Column(name = "ASSET_IND")
    private String assetInd;

    @Column(name = "ASSET_CODE")
    private String assetCode;

    @Column(name = "ASSET_SUB")
    private String assetSub;

    @Column(name = "CONV_CODE")
    private String convCode;

    @Column(name = "CONV_RATE")
    private double convRate;

    @Column(name = "OTHER_AMT")
    private double otherAmt;

    @Column(name = "OTHER_DP")
    private String otherDp;

    @Column(name = "CLEARDOWN")
    private int cleardown;

    @Column(name = "REVERSAL")
    private String reversal;

    @Column(name = "LOSS_GAIN")
    private String lossGain;

    @Column(name = "ROUGH_FLAG")
    private String roughFlag;

    @Column(name = "IN_USE_FLAG")
    private String inUseFlag;

    @Column(name = "ANAL_T0")
    private String analT0;

    @Column(name = "ANAL_T1")
    private String analT1;

    @Column(name = "ANAL_T2")
    private String analT2;

    @Column(name = "ANAL_T3")
    private String analT3;

    @Column(name = "ANAL_T4")
    private String analT4;

    @Column(name = "ANAL_T5")
    private String analT5;

    @Column(name = "ANAL_T6")
    private String analT6;

    @Column(name = "ANAL_T7")
    private String analT7;

    @Column(name = "ANAL_T8")
    private String analT8;

    @Column(name = "ANAL_T9")
    private String analT9;

    @Column(name = "POSTING_DATETIME")
    private Date postingDatetime;

    @Column(name = "ALLOC_IN_PROGRESS")
    private String allocInProgress;

    @Column(name = "HOLD_REF")
    private String holdRef;

    @Column(name = "HOLD_OP_ID")
    private int holdOpId;

    @Column(name = "BASE_RATE")
    private double baseRate;

    @Column(name = "BASE_OPERATOR")
    private String baseOperator;

    @Column(name = "CONV_OPERATOR")
    private String convOperator;

    @Column(name = "REPORT_RATE")
    private double reportRate;

    @Column(name = "REPORT_OPERATOR")
    private String reportOperator;

    @Column(name = "REPORT_AMT")
    private double reportAmt;

    @Column(name = "MEMO_AMT")
    private double memoAmt;

    @Column(name = "EXCLUDE_BAL")
    private String excludeBal;

    @Column(name = "LE_DETAILS_IND")
    private String leDetailsInd;

    @Column(name = "CONSUMED_BDGT_ID")
    private int consumedBdgtId;

    @Column(name = "CV4_CONV_CODE")
    private String cv4ConvCode;

    @Column(name = "CV4_AMT")
    private double cv4Amt;

    @Column(name = "CV4_CONV_RATE")
    private double cv4ConvRate;

    @Column(name = "CV4_OPERATOR")
    private String cv4Operator;

    @Column(name = "CV4_DP")
    private String cv4Dp;

    @Column(name = "CV5_CONV_CODE")
    private String cv5ConvCode;

    @Column(name = "CV5_AMT")
    private double cv5Amt;

    @Column(name = "CV5_CONV_RATE")
    private double cv5ConvRate;

    @Column(name = "CV5_OPERATOR")
    private String cv5Operator;

    @Column(name = "CV5_DP")
    private String cv5Dp;

    @Column(name = "LINK_REF_1")
    private String linkRef1;

    @Column(name = "LINK_REF_2")
    private String linkRef2;

    @Column(name = "LINK_REF_3")
    private String linkRef3;

    @Column(name = "ALLOCN_CODE")
    private String allocnCode;

    @Column(name = "ALLOCN_STMNTS")
    private short allocnStmnts;

    @Column(name = "OPR_CODE")
    private String oprCode;

    @Column(name = "SPLIT_ORIG_LINE")
    private int splitOrigLine;

    @Column(name = "VAL_DATETIME")
    private Date valDatetime;

    @Column(name = "SIGNING_DETAILS")
    private String signingDetails;

    @Column(name = "INSTLMT_DATETIME")
    private Date instlmtDatetime;

    @Column(name = "PRINCIPAL_REQD")
    private String principalReqd;

    @Column(name = "BINDER_STATUS")
    private String binderStatus;

    @Column(name = "AGREED_STATUS")
    private String agreedStatus;

    @Column(name = "SPLIT_LINK_REF")
    private String splitLinkRef;

    @Column(name = "PSTG_REF")
    private String pstgRef;

    @Column(name = "TRUE_RATED")
    private String trueRated;

    @Column(name = "HOLD_DATETIME")
    private Date holdDatetime;

    @Column(name = "HOLD_TEXT")
    private String holdText;

    @Column(name = "INSTLMT_NUM")
    private Short instlmtNum;

    @Column(name = "SUPPLMNTRY_EXTSN")
    private String supplmntyExtsn;

    @Column(name = "APRVLS_EXTSN")
    private String aprvlsExtsn;

    @Column(name = "REVAL_LINK_REF")
    private Integer revalLinkRef;

    @Column(name = "SAVED_SET_NUM")
    private Long savedSetNum;

    @Column(name = "AUTHORISTN_SET_REF")
    private Integer authoristnSetRef;

    @Column(name = "PYMT_AUTHORISTN_SET_REF")
    private Integer pymtAuthoristnSetRef;

    @Column(name = "MAN_PAY_OVER")
    private String manPayOver;

    @Column(name = "PYMT_STAMP")
    private String pymtStamp;

    @Column(name = "AUTHORISTN_IN_PROGRESS")
    private String authoristnInProgress;

    @Column(name = "SPLIT_IN_PROGRESS")
    private String splitInProgress;

    @Column(name = "VCHR_NUM")
    private String vchrNum;

    @Column(name = "JNL_CLASS_CODE")
    private String jnlClassCode;

    @Column(name = "ORIGINATOR_ID")
    private String originatorId;

    @Column(name = "ORIGINATED_DATETIME")
    private Date originatedDatetime;

    @Column(name = "LAST_CHANGE_USER_ID")
    private String lastChangeUserId;

    @Column(name = "LAST_CHANGE_DATETIME")
    private Date lastChangeDatetime;

    @Column(name = "AFTER_PSTG_ID")
    private String afterPstgId;

    @Column(name = "AFTER_PSTG_DATETIME")
    private Date afterPstgDatetime;

    @Column(name = "POSTER_ID")
    private String posterId;

    @Column(name = "ALLOC_ID")
    private String allocId;

    @Column(name = "JNL_REVERSAL_TYPE")
    private String jnlReversalType;

}
