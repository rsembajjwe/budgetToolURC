
package com.methaltech.application.data.entity.bgtool;



import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "URC_DEPARTMENTS_ANL_DIM")
public @Data class UrDepartmentsAnlDim2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ANL_CODE")
    private String anlCode;

    @Column(name = "ANL_CAT_ID")
    private String anlCatId;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT5")
    private String anlCodeExtsnExtsnText5;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_DATE2")
    private Date anlCodeExtsnExtsnDate2;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT4")
    private String anlCodeExtsnExtsnText4;

    @Column(name = "DAG_CODE")
    private String dagCode;

    @Column(name = "EXT_FIXED_1")
    private String extFixed1;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT7")
    private String anlCodeExtsnExtsnText7;

    @Column(name = "EXT_FIXED_5")
    private String extFixed5;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_NUMBER2")
    private Float anlCodeExtsnExtsnNumber2;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT2")
    private String anlCodeExtsnExtsnText2;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_DATE1")
    private Date anlCodeExtsnExtsnDate1;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_NUMBER5")
    private Float anlCodeExtsnExtsnNumber5;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT10")
    private String anlCodeExtsnExtsnText10;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_NUMBER1")
    private Float anlCodeExtsnExtsnNumber1;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT13")
    private String anlCodeExtsnExtsnText13;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT12")
    private String anlCodeExtsnExtsnText12;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT6")
    private String anlCodeExtsnExtsnText6;

    @Column(name = "EXT_FIXED_4")
    private String extFixed4;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT3")
    private String anlCodeExtsnExtsnText3;

    @Column(name = "EXT_FIXED_6")
    private String extFixed6;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT9")
    private String anlCodeExtsnExtsnText9;

    @Column(name = "EXT_FIXED_7")
    private String extFixed7;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT1")
    private String anlCodeExtsnExtsnText1;

    @Column(name = "EXT_FIXED_3")
    private String extFixed3;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_NUMBER4")
    private Float anlCodeExtsnExtsnNumber4;

    @Column(name = "EXT_FIXED_2")
    private String extFixed2;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT8")
    private String anlCodeExtsnExtsnText8;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT14")
    private String anlCodeExtsnExtsnText14;

    @Column(name = "EXT_FIXED_8")
    private String extFixed8;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_DATE5")
    private Date anlCodeExtsnExtsnDate5;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_DATE3")
    private Date anlCodeExtsnExtsnDate3;

    @Column(name = "EXT_FIXED_10")
    private String extFixed10;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT15")
    private String anlCodeExtsnExtsnText15;

    @Column(name = "EXT_FIXED_9")
    private String extFixed9;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_TEXT11")
    private String anlCodeExtsnExtsnText11;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_NUMBER3")
    private Float anlCodeExtsnExtsnNumber3;

    @Column(name = "ANL_CODE_EXTSN_EXTSN_DATE4")
    private Date anlCodeExtsnExtsnDate4;

    @Column(name = "LOOKUP")
    private String lookup;

    @Column(name = "UPDATE_COUNT")
    private Integer updateCount;

    @Column(name = "LAST_CHANGE_USER_ID")
    private String lastChangeUserId;

    @Column(name = "LAST_CHANGE_DATETIME")
    private Date lastChangeDatetime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "BDGT_CHECK")
    private String bdgtCheck;

    @Column(name = "BDGT_STOP")
    private String bdgtStop;

    @Column(name = "PROHIBIT_POSTING")
    private String prohibitPosting;

    @Column(name = "NAVIGATION_OPTION")
    private String navigationOption;

    @Column(name = "COMBINED_BDGT_CHCK")
    private String combinedBdgtChck;
}

