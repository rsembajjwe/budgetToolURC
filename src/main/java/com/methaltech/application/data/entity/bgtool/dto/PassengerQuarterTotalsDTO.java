
package com.methaltech.application.data.entity.bgtool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
public class PassengerQuarterTotalsDTO {

    private Long budgetId;
    private Set<Long> coaIds;   // for "set" totals (can also store a single id as Set.of(coaId))

    private BigDecimal q1;
    private BigDecimal q2;
    private BigDecimal q3;
    private BigDecimal q4;
    private BigDecimal year;
}
