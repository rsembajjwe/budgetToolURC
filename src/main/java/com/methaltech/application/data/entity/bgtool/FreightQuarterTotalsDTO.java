
package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FreightQuarterTotalsDTO {
    private Long budgetId;
    private Long coaId;

    private BigDecimal q1;   // Jul-Sep
    private BigDecimal q2;   // Oct-Dec
    private BigDecimal q3;   // Jan-Mar
    private BigDecimal q4;   // Apr-Jun
    private BigDecimal year; // total of all months
}

