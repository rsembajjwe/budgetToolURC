
package com.methaltech.application.data.entity.livedata;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "URC_CURRENCY_V")
public @Data class URCCurrency {

    @Id
    @Column(name = "CURR_CODE")
    private String currCode;


    @Column(name = "S_HEAD")
    private String sHead;

    @Column(name = "LOOKUP")
    private String lookup;



    @Column(name = "DESCR")
    private String descr;




}

