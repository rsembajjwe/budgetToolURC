package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.bgtool.repository.FreightActualVolumesRepository;
import com.methaltech.application.data.bgtool.repository.FreightVolumesRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.FreightActualBudgetDTO;
import com.methaltech.application.data.entity.bgtool.FreightActualVolumes;
import com.methaltech.application.data.entity.bgtool.FreightQuarterTotalsDTO;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreightActualVolumesService {

    private final FreightActualVolumesRepository repository;
    private final FreightVolumesRepository freightVolumesRepository;

    public List<FreightActualBudgetDTO> getActualVsBudget(Long budgetId, Display display) {

        // 1) aggregated budget by COA
        List<Tuple> budgetRows = freightVolumesRepository.sumBudgetByCoa(budgetId, display);

        // 2) actuals keyed by COA id
        Map<Long, FreightActualVolumes> actualByCoaId = repository.findByBudgetIdAndCoacodeDisplay(budgetId, display)
                .stream()
                .filter(a -> a.getCoacode() != null && a.getCoacode().getId() != null)
                .collect(Collectors.toMap(a -> a.getCoacode().getId(), a -> a, (a, b) -> a));

        // 3) build DTOs
        List<FreightActualBudgetDTO> out = new ArrayList<>(budgetRows.size());

        for (Tuple t : budgetRows) {
            COA coa = t.get("coacode", COA.class);
            Budget budget = t.get("budget", Budget.class);

            FreightActualVolumes actual = (coa != null && coa.getId() != null)
                    ? actualByCoaId.get(coa.getId())
                    : null;

            if (actual == null) {
                actual = new FreightActualVolumes();
                actual.setBudget(budget);
                actual.setCoacode(coa);
            }

            FreightActualBudgetDTO dto = new FreightActualBudgetDTO();
            dto.setCoacode(coa);
            dto.setBudget(budget);
            dto.setActual(actual);

            dto.setBudgetJul(nz(t.get("budgetJul", BigDecimal.class)));
            dto.setBudgetAug(nz(t.get("budgetAug", BigDecimal.class)));
            dto.setBudgetSep(nz(t.get("budgetSep", BigDecimal.class)));
            dto.setBudgetOct(nz(t.get("budgetOct", BigDecimal.class)));
            dto.setBudgetNov(nz(t.get("budgetNov", BigDecimal.class)));
            dto.setBudgetDec(nz(t.get("budgetDec", BigDecimal.class)));

            dto.setBudgetJan(nz(t.get("budgetJan", BigDecimal.class)));
            dto.setBudgetFeb(nz(t.get("budgetFeb", BigDecimal.class)));
            dto.setBudgetMar(nz(t.get("budgetMar", BigDecimal.class)));
            dto.setBudgetApr(nz(t.get("budgetApr", BigDecimal.class)));
            dto.setBudgetMay(nz(t.get("budgetMay", BigDecimal.class)));
            dto.setBudgetJun(nz(t.get("budgetJun", BigDecimal.class)));

            dto.setBudgetTotal(nz(t.get("budgetTotal", BigDecimal.class)));

            out.add(dto);
        }

        return out;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    /**
     * Save or update actual volumes.
     */
    @Transactional
    public FreightActualVolumes save(FreightActualVolumes actual) {
        return repository.save(actual);
    }

    /**
     * Bulk save (useful for grid batch commits).
     */
    @Transactional
    public List<FreightActualVolumes> saveAll(
            List<FreightActualVolumes> actuals
    ) {
        return repository.saveAll(actuals);
    }

    public FreightQuarterTotalsDTO getTotals(Long budgetId, Long coaId) {
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

        return new FreightQuarterTotalsDTO(budgetId, coaId, q1, q2, q3, q4, year);
    }

    public FreightQuarterTotalsDTO getTotalsForBudgetAndCoaSet(Long budgetId, Set<Long> coaIds) {

        if (budgetId == null) {
            throw new IllegalArgumentException("budgetId is required");
        }

        if (coaIds == null || coaIds.isEmpty()) {
            return new FreightQuarterTotalsDTO(
                    budgetId, budgetId,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        Object[] r = repository.sumQuartersAndYearForBudgetAndCoaSet(budgetId, coaIds);
        if (r != null && r.length == 1 && r[0] instanceof Object[]) {
            r = (Object[]) r[0];   // ✅ unwrap
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

        return new FreightQuarterTotalsDTO(budgetId, budgetId, q1, q2, q3, q4, year);
    }

    public FreightQuarterTotalsDTO getTotals(Budget budget, COA coa) {

        if (budget != null && coa != null) {
            return getTotals(budget.getId(), coa.getId());
        } else {
            return getTotals(0L, 0L);
        }

    }

    public FreightQuarterTotalsDTO getTotals(Budget budget, Set<COA> coa) {

        if (budget == null || budget.getId() == null) {
            return new FreightQuarterTotalsDTO(
                    0L, null,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        if (coa == null || coa.isEmpty()) {
            return new FreightQuarterTotalsDTO(
                    budget.getId(), null,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        Set<Long> coaIds = coa.stream()
                .map(COA::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (coaIds.isEmpty()) {
            return new FreightQuarterTotalsDTO(
                    budget.getId(), null,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        return getTotalsForBudgetAndCoaSet(budget.getId(), coaIds);
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
