
package com.methaltech.application.data.entity.livedata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "URC_DEPARTMENTS_ANL_DIM_V")
public @Data class UrcDepartmentAnlDim {

    @Id
    private String ANL_CODE;
    private String ANL_CAT_ID;
    private String ANL_CODE_EXTSN_EXTSN_TEXT5;
    private String ANL_CODE_EXTSN_EXTSN_DATE2;
    private String ANL_CODE_EXTSN_EXTSN_TEXT4;
    private String DAG_CODE;
    private String EXT_FIXED_1;
    private String NAME;
    private String ANL_CODE_EXTSN_EXTSN_TEXT7;
    private String EXT_FIXED_5;
    private String ANL_CODE_EXTSN_EXTSN_NUMBER2;
    private String ANL_CODE_EXTSN_EXTSN_TEXT2;
    private String ANL_CODE_EXTSN_EXTSN_DATE1;
    private String ANL_CODE_EXTSN_EXTSN_NUMBER5;
    private String ANL_CODE_EXTSN_EXTSN_TEXT10;
    private String ANL_CODE_EXTSN_EXTSN_NUMBER1;
    private String ANL_CODE_EXTSN_EXTSN_TEXT13;
    private String ANL_CODE_EXTSN_EXTSN_TEXT12;
    private String ANL_CODE_EXTSN_EXTSN_TEXT6;
    private String EXT_FIXED_4;
    private String ANL_CODE_EXTSN_EXTSN_TEXT3;
    private String EXT_FIXED_6;
    private String ANL_CODE_EXTSN_EXTSN_TEXT9;
    private String EXT_FIXED_7;
    private String ANL_CODE_EXTSN_EXTSN_TEXT1;
    private String EXT_FIXED_3;
    private String ANL_CODE_EXTSN_EXTSN_NUMBER4;
    private String EXT_FIXED_2;
    private String ANL_CODE_EXTSN_EXTSN_TEXT8;
    private String ANL_CODE_EXTSN_EXTSN_TEXT14;
    private String EXT_FIXED_8;
    private String ANL_CODE_EXTSN_EXTSN_DATE5;
    private String ANL_CODE_EXTSN_EXTSN_DATE3;
    private String EXT_FIXED_10;
    private String ANL_CODE_EXTSN_EXTSN_TEXT15;
    private String EXT_FIXED_9;
    private String ANL_CODE_EXTSN_EXTSN_TEXT11;
    private String ANL_CODE_EXTSN_EXTSN_NUMBER3;
    private String ANL_CODE_EXTSN_EXTSN_DATE4;
    private String LOOKUP;
    private String UPDATE_COUNT;
    private String LAST_CHANGE_USER_ID;
    private String LAST_CHANGE_DATETIME;
    private String STATUS;
    private String BDGT_CHECK;
    private String BDGT_STOP;
    private String PROHIBIT_POSTING;
    private String NAVIGATION_OPTION;
    private String COMBINED_BDGT_CHCK;

}
