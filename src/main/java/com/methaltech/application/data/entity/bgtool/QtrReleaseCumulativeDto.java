
package com.methaltech.application.data.entity.bgtool;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QtrReleaseCumulativeDto {

    private Long budgetId;
    private String deptSectionId;

    private BigDecimal q1Total;
    private BigDecimal q2Total;
    private BigDecimal q3Total;
    private BigDecimal q4Total;
}

