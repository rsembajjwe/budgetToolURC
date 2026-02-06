
package com.methaltech.application.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public  class CurrencyFormatter {

    public  final Locale UG_LOCALE = new Locale("en", "UG");

    public CurrencyFormatter() {}

    public  String format(BigDecimal value) {
        if (value == null) {
            return "—";
        }

        NumberFormat nf = NumberFormat.getNumberInstance(UG_LOCALE);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        return nf.format(value.setScale(2, RoundingMode.HALF_UP));
    }
}

