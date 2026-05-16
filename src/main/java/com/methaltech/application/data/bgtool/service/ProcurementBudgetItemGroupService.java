package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.bgtool.repository.ProcurementBudgetItemGroupRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.ProcurementBudgetItemGroup;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcurementBudgetItemGroupService {

    private final ProcurementBudgetItemGroupRepository repository;

    public ProcurementBudgetItemGroup save(ProcurementBudgetItemGroup group) {
        return repository.save(group);
    }

    public void delete(ProcurementBudgetItemGroup group) {
        repository.delete(group);
    }

    @Transactional(readOnly = true)
    public List<ProcurementBudgetItemGroup> findByBudgetAndProcClassWithItems(
            Budget budget,
            ProcClass procClass
    ) {
        if (budget == null || procClass == null) {
            return Collections.emptyList();
        }

        return repository.findByBudgetAndProcClassWithItems(
                budget,
                procClass
        );
    }

    @Transactional(readOnly = true)
    public Optional<ProcurementBudgetItemGroup> findByIdWithItems(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return repository.findByIdWithItems(id);
    }

    public List<ProcurementBudgetItemGroup> findByBudgetAndProcClassAndCoaWithItems(
            Budget budget,
            ProcClass procClass,
            COA coa
    ) {
        if (budget == null || procClass == null || coa == null) {
            return Collections.emptyList();
        }

        return repository.findByBudgetAndProcClassAndCoaWithItems(
                budget,
                procClass,
                coa
        );
    }

    public List<ProcurementBudgetItemGroup> findByBudgetWithItems(
            Budget budget
    ) {
        if (budget == null) {
            return Collections.emptyList();
        }

        return repository.findByBudgetWithItems(budget);
    }

    public Optional<ProcurementBudgetItemGroup> findById(Long id) {
        return repository.findById(id);
    }
}
