package com.methaltech.application.data.livedata.repository;

import java.math.BigDecimal;
import java.util.Date;

public interface SALFLDGProjection {

    String getAccntCode();

    Integer  getJrnalNo();
    Integer  getJrnalLine();

    BigDecimal getAmount();

    String getDescriptn();

    Date getTransDatetime();

    String getAnalT1();
    Integer getPeriod();
}
