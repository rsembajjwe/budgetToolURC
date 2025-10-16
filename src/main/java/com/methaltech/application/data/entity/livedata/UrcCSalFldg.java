
package com.methaltech.application.data.entity.livedata;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "URC_C_SALFLDG_View")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrcCSalFldg {

    @EmbeddedId
    private UrcCSalFldgId id;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount; 

    @Column(name = "D_C", nullable = false, length = 1)
    private String dC;

    @Column(name = "ALLOCATION", nullable = false, length = 1)
    private String allocation;

    @Column(name = "JRNAL_TYPE")
    private String journalType;

    @Column(name = "JRNAL_SRCE", nullable = false)
    private String journalSource;

    @Column(name = "TREFERENCE")
    private String transactionReference;

    @Column(name = "DESCRIPTN")
    private String description;

    @Column(name = "ENTRY_DATETIME")
    private LocalDateTime entryDatetime;

    @Column(name = "ENTRY_PRD", nullable = false)
    private Integer entryPeriod;

    @Column(name = "DUE_DATETIME")
    private LocalDateTime dueDatetime;

    @Column(name = "ALLOC_REF", nullable = false)
    private String allocRef;

    @Column(name = "ALLOC_DATETIME")
    private LocalDateTime allocDatetime;

    @Column(name = "ALLOC_PERIOD", nullable = false)
    private Integer allocPeriod;

    @Column(name = "ASSET_IND", nullable = false)
    private String assetInd;

    @Column(name = "ASSET_CODE", nullable = false)
    private String assetCode;

    @Column(name = "ASSET_SUB", nullable = false)
    private String assetSub;

    @Column(name = "CONV_CODE", nullable = false)
    private String convCode;

    @Column(name = "CONV_RATE", nullable = false)
    private BigDecimal convRate;

    @Column(name = "OTHER_AMT", nullable = false)
    private BigDecimal otherAmt;

    @Column(name = "OTHER_DP", nullable = false)
    private String otherDp;

    @Column(name = "CLEARDOWN", nullable = false)
    private Integer cleardown;

    @Column(name = "REVERSAL", nullable = false)
    private String reversal;

    @Column(name = "LOSS_GAIN", nullable = false)
    private String lossGain;

    @Column(name = "ROUGH_FLAG", nullable = false)
    private String roughFlag;

    @Column(name = "IN_USE_FLAG", nullable = false)
    private String inUseFlag;

    @Column(name = "ANAL_T0", nullable = false)
    private String analT0;

    @Column(name = "ANAL_T1", nullable = false)
    private String analT1;

    @Column(name = "ANAL_T2", nullable = false)
    private String analT2;

    @Column(name = "ANAL_T3", nullable = false)
    private String analT3;

    @Column(name = "ANAL_T4", nullable = false)
    private String analT4;

    @Column(name = "ANAL_T5", nullable = false)
    private String analT5;

    @Column(name = "ANAL_T6", nullable = false)
    private String analT6;

    @Column(name = "ANAL_T7", nullable = false)
    private String analT7;

    @Column(name = "ANAL_T8", nullable = false)
    private String analT8;

    @Column(name = "ANAL_T9", nullable = false)
    private String analT9;

    @Column(name = "POSTING_DATETIME")
    private LocalDateTime postingDatetime;

    @Column(name = "ALLOC_IN_PROGRESS", nullable = false)
    private String allocInProgress;

    @Column(name = "HOLD_REF", nullable = false)
    private String holdRef;

    @Column(name = "HOLD_OP_ID", nullable = false)
    private String holdOpId;

    @Column(name = "BASE_RATE", nullable = false)
    private BigDecimal baseRate;

    @Column(name = "BASE_OPERATOR", nullable = false)
    private String baseOperator;

    @Column(name = "CONV_OPERATOR", nullable = false)
    private String convOperator;

    @Column(name = "REPORT_RATE", nullable = false)
    private BigDecimal reportRate;

    @Column(name = "REPORT_OPERATOR", nullable = false)
    private String reportOperator;

    @Column(name = "REPORT_AMT", nullable = false)
    private BigDecimal reportAmt;

    @Column(name = "MEMO_AMT", nullable = false)
    private BigDecimal memoAmt;

    @Column(name = "EXCLUDE_BAL", nullable = false)
    private String excludeBal;

    @Column(name = "LE_DETAILS_IND", nullable = false)
    private String leDetailsInd;

    @Column(name = "CONSUMED_BDGT_ID", nullable = false)
    private Integer consumedBdgtId;

    @Column(name = "CV4_CONV_CODE", nullable = false)
    private String cv4ConvCode;

    @Column(name = "CV4_AMT", nullable = false)
    private BigDecimal cv4Amt;

    @Column(name = "CV4_CONV_RATE", nullable = false)
    private BigDecimal cv4ConvRate;

    @Column(name = "CV4_OPERATOR", nullable = false)
    private String cv4Operator;

    @Column(name = "CV4_DP", nullable = false)
    private String cv4Dp;

    @Column(name = "CV5_CONV_CODE", nullable = false)
    private String cv5ConvCode;

    @Column(name = "CV5_AMT", nullable = false)
    private BigDecimal cv5Amt;

    @Column(name = "CV5_CONV_RATE", nullable = false)
    private BigDecimal cv5ConvRate;

    @Column(name = "CV5_OPERATOR", nullable = false)
    private String cv5Operator;

    @Column(name = "CV5_DP", nullable = false)
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
    private Integer allocnStmnts;

    @Column(name = "OPR_CODE")
    private String oprCode;

    @Column(name = "SPLIT_ORIG_LINE", nullable = false)
    private Integer splitOrigLine;

    @Column(name = "VAL_DATETIME")
    private LocalDateTime valDatetime;

    @Column(name = "SIGNING_DETAILS")
    private String signingDetails;

    @Column(name = "INSTLMT_DATETIME")
    private LocalDateTime instlmtDatetime;

    @Column(name = "PRINCIPAL_REQD", nullable = false)
    private Integer principalReqd;

    @Column(name = "BINDER_STATUS", nullable = false)
    private String binderStatus;

    @Column(name = "AGREED_STATUS", nullable = false)
    private Integer agreedStatus;

    @Column(name = "SPLIT_LINK_REF")
    private String splitLinkRef;

    @Column(name = "PSTG_REF")
    private String pstgRef;

    @Column(name = "TRUE_RATED", nullable = false)
    private Integer trueRated;

    @Column(name = "HOLD_DATETIME")
    private LocalDateTime holdDatetime;

    @Column(name = "HOLD_TEXT")
    private String holdText;

    @Column(name = "INSTLMT_NUM")
    private Integer instlmtNum;

    @Column(name = "SUPPLMNTRY_EXTSN", nullable = false)
    private Integer supplmntryExtsn;

    @Column(name = "APRVLS_EXTSN", nullable = false)
    private Integer aprvlsExtsn;

    @Column(name = "REVAL_LINK_REF")
    private Integer revalLinkRef;

    @Column(name = "SAVED_SET_NUM")
    private Long savedSetNum;

    @Column(name = "AUTHORISTN_SET_REF")
    private Integer authoristnSetRef;

    @Column(name = "PYMT_AUTHORISTN_SET_REF")
    private Integer pymtAuthoristnSetRef;

    @Column(name = "MAN_PAY_OVER", nullable = false)
    private Integer manPayOver;

    @Column(name = "PYMT_STAMP")
    private String pymtStamp;

    @Column(name = "AUTHORISTN_IN_PROGRESS", nullable = false)
    private Integer authoristnInProgress;

    @Column(name = "SPLIT_IN_PROGRESS", nullable = false)
    private Integer splitInProgress;

    @Column(name = "VCHR_NUM")
    private String vchrNum;

    @Column(name = "JNL_CLASS_CODE")
    private String jnlClassCode;

    @Column(name = "ORIGINATOR_ID")
    private String originatorId;

    @Column(name = "ORIGINATED_DATETIME")
    private LocalDateTime originatedDatetime;

    @Column(name = "LAST_CHANGE_USER_ID")
    private String lastChangeUserId;

    @Column(name = "LAST_CHANGE_DATETIME")
    private LocalDateTime lastChangeDatetime;

    @Column(name = "AFTER_PSTG_ID")
    private String afterPstgId;

    @Column(name = "AFTER_PSTG_DATETIME")
    private LocalDateTime afterPstgDatetime;

    @Column(name = "POSTER_ID")
    private String posterId;

    @Column(name = "ALLOC_ID")
    private String allocId;

    @Column(name = "JNL_REVERSAL_TYPE", nullable = false)
    private Integer jnlReversalType;
}
