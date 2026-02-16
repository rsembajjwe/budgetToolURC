package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.PassengerActualVolumesRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.dto.PassengerQuarterAgg;
import com.methaltech.application.data.entity.bgtool.dto.PassengerQuarterTotalsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerActualVolumesService {

    private final PassengerActualVolumesRepository repository;

    public PassengerQuarterTotalsDTO getTotals(Long budgetId, Long coaId) {
        Object[] r = repository.sumQuartersAndYearForBudgetAndCoa(budgetId, coaId);

        if (r != null && r.length == 1 && r[0] instanceof Object[]) {
            r = (Object[]) r[0];
        }

        BigDecimal q1 = BigDecimal.ZERO, q2 = BigDecimal.ZERO, q3 = BigDecimal.ZERO,
                q4 = BigDecimal.ZERO, year = BigDecimal.ZERO;

        if (r != null) {
            q1 = toBigDecimal(r[0]);
            q2 = toBigDecimal(r[1]);
            q3 = toBigDecimal(r[2]);
            q4 = toBigDecimal(r[3]);
            year = toBigDecimal(r[4]);
        }

        return new PassengerQuarterTotalsDTO(budgetId, null, q1, q2, q3, q4, year);
    }

    public PassengerQuarterTotalsDTO getTotals(Budget budget, COA coa) {

        if (budget != null && coa != null) {
            return getTotals(budget.getId(), coa.getId());
        } else {
            return getTotals(0L, 0L);
        }

    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal toBigDecimal(Object v) {
        if (v == null) {
            return BigDecimal.ZERO;
        }
        if (v instanceof BigDecimal) {
            return (BigDecimal) v;
        }
        if (v instanceof Number) {
            return new BigDecimal(v.toString());
        }
        if (v instanceof String) {
            String s = ((String) v).trim();
            return s.isEmpty() ? BigDecimal.ZERO : new BigDecimal(s);
        }
        throw new IllegalStateException("Cannot convert to BigDecimal: " + v.getClass() + " value=" + v);
    }
}
