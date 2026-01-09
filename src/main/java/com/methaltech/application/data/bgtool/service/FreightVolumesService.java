package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.bgtool.repository.FreightVolumesRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.FreightVolumes;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FreightVolumesService {

    private final FreightVolumesRepository freightVolumesRepository;

    @Autowired
    public FreightVolumesService(FreightVolumesRepository freightVolumesRepository) {
        this.freightVolumesRepository = freightVolumesRepository;
    }

    public List<FreightVolumes> getAllFreightVolumes() {
        return freightVolumesRepository.findAll();
    }

    public List<FreightVolumes> getAllFreightVolumesByBudgetAndCode(Budget budget, COA coacode) {
        return freightVolumesRepository.findByBudgetAndCoacode(budget, coacode);
    }

    public List<FreightVolumes> getAllFreightVolumesByBudget(Budget budget) {
        return freightVolumesRepository.findByBudget(budget);
    }

    public Optional<FreightVolumes> getFreightVolumeById(Long id) {
        return freightVolumesRepository.findById(id);
    }

    public FreightVolumes createOrUpdateFreightVolume(FreightVolumes freightVolume) {
        return freightVolumesRepository.save(freightVolume);
    }

    public void deleteFreightVolume(Long id) {
        freightVolumesRepository.deleteById(id);
    }

    public BigDecimal calculateSumOfAllMonthsByBudgetAndCoacode(
            Budget budget,
            COA coa
    ) {
        return freightVolumesRepository.sumMonthsByBudgetAndCoacode(budget, coa);
    }

    public BigDecimal calculateSumOfAllMonthsByBudgetAndCoacodes(
            Budget budget,
            List<COA> coa
    ) {
        return freightVolumesRepository.sumMonthsByBudgetAndCoacodes(budget, coa);
    }

    public long countByBudget(Budget budget) {
        return freightVolumesRepository.countByBudget(budget);
    }

    public MonthlySumResponseFreight getTotals(Budget budget, COA coacode) {
        if (budget == null) {
            // Handle the case where budget is null
            // For example, you can return null or throw an exception
            return new MonthlySumResponseFreight(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        } else {
            return freightVolumesRepository.getMonthlySumsByBudgetAndCoacode(budget, coacode);
        }

    }

    public MonthlySumResponseFreight getTotals(Budget budget, List<COA> coacode) {
        return freightVolumesRepository.getMonthlySumsByBudgetAndCoacodes(budget, coacode);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteByBudget(Budget budget) {
        return freightVolumesRepository.deleteByBudget(budget);
    }

    @Transactional(readOnly = true)
    public List<COA> getDistinctCoacodesByBudget(Budget budget) {
        Objects.requireNonNull(budget, "Budget must not be null");
        return freightVolumesRepository.findDistinctCoacodeByBudget(budget);
    }
}
