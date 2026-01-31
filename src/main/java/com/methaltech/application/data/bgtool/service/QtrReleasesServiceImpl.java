package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.QtrReleasesRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.QtrReleaseCumulativeDto;
import com.methaltech.application.data.entity.bgtool.QtrReleases;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class QtrReleasesServiceImpl implements QtrReleasesService {

    private final QtrReleasesRepository qtrReleasesRepository;

    @Override
    public QtrReleases save(QtrReleases qtrReleases) {
        sanitize(qtrReleases);
        return qtrReleasesRepository.save(qtrReleases);
    }

    @Override
    public QtrReleases update(QtrReleases qtrReleases) {
        if (qtrReleases.getId() == null) {
            throw new IllegalArgumentException("Cannot update QtrReleases without ID");
        }
        sanitize(qtrReleases);
        return qtrReleasesRepository.save(qtrReleases);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QtrReleases> findById(Long id) {
        return qtrReleasesRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QtrReleases> findAll() {
        return qtrReleasesRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        qtrReleasesRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QtrReleases> findByOrganisationBudgetSection(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    ) {
        return qtrReleasesRepository.findByOrganisationAndBudgetAndDeptSection(
                organisation, budget, deptSection
        );
    }

    /**
     * Ensures there is always exactly one row per
     * Organisation+Budget+DeptSection. Works nicely with your unique
     * constraint.
     */
    @Override
    public QtrReleases getOrCreate(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    ) {
        return qtrReleasesRepository
                .findByOrganisationAndBudgetAndDeptSection(organisation, budget, deptSection)
                .orElseGet(() -> {
                    QtrReleases qtr = new QtrReleases();
                    qtr.setOrganisation(organisation);
                    qtr.setBudget(budget);
                    qtr.setDeptSection(deptSection);

                    qtr.setQtr1Release(BigDecimal.ZERO);
                    qtr.setQtr2Release(BigDecimal.ZERO);
                    qtr.setQtr3Release(BigDecimal.ZERO);
                    qtr.setQtr4Release(BigDecimal.ZERO);

                    return qtrReleasesRepository.save(qtr);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<QtrReleases> findByOrganisationAndBudget(Organisation organisation, Budget budget) {
        return qtrReleasesRepository.findByOrganisationAndBudget(organisation, budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QtrReleases> findByBudget(Budget budget) {
        return qtrReleasesRepository.findByBudget(budget);
    }

    private void sanitize(QtrReleases qtr) {
        if (qtr == null) {
            return;
        }

        if (qtr.getQtr1Release() == null) {
            qtr.setQtr1Release(BigDecimal.ZERO);
        }
        if (qtr.getQtr2Release() == null) {
            qtr.setQtr2Release(BigDecimal.ZERO);
        }
        if (qtr.getQtr3Release() == null) {
            qtr.setQtr3Release(BigDecimal.ZERO);
        }
        if (qtr.getQtr4Release() == null) {
            qtr.setQtr4Release(BigDecimal.ZERO);
        }
    }

    public Tuple getCumulativeQuarterReleases(
            Long budgetId,
            UrcDeptSectionAnlDimbgt deptSections
    ) {
        return qtrReleasesRepository
                .findCumulativeQuarterTotalsByBudget(budgetId, deptSections);
    }

    public Tuple getCumulativeQuarterReleases(
            Long budgetId,
            Set<UrcDeptSectionAnlDimbgt> deptSections
    ) {
        return qtrReleasesRepository
                .findCumulativeQuarterTotalsByBudget(budgetId, deptSections);
    }
    
        public BigDecimal getTotalAmountByPeriodsAndActivity(
            Set<Integer> periods,
            Urc_Activities activity) {

        if (periods == null || periods.isEmpty() || activity == null) {
            return BigDecimal.ZERO;
        }

        return qtrReleasesRepository.sumAmountByPeriodsAndActivity(periods, activity);
    }

}
