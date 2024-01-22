
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
import java.util.Optional;

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
    
    public long countByBudget(Budget budget){
        return freightVolumesRepository.countByBudget(budget);
    }
    public MonthlySumResponseFreight getTotals(Budget budget,COA coacode){
        return freightVolumesRepository.getMonthlySumsByBudgetAndCoacode(budget, coacode);
    }
}

