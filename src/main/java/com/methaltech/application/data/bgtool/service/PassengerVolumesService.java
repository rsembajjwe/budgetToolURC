package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.bgtool.repository.CoaRepository;
import com.methaltech.application.data.entity.bgtool.PassengerActualVolumes;
import com.methaltech.application.data.entity.bgtool.PassengerBudgetActualDTO;
import com.methaltech.application.data.entity.bgtool.PassengerServiceVolumes;
import com.methaltech.application.data.bgtool.repository.PassengerActualVolumesRepository;
import com.methaltech.application.data.bgtool.repository.PassengerServiceVolumesRepository;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.PassengerRowDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerVolumesService {

    private final PassengerServiceVolumesRepository budgetRepo;
    private final PassengerActualVolumesRepository actualRepo;
    private final CoaRepository coaRepository;

    public List<PassengerBudgetActualDTO> getPassengerBudgetActualRows(Long budgetId, Display display) {

        // Driver list: COA by budget + display
        List<COA> coas = coaRepository.findByBudgetIdAndDisplayOrderByCodeAsc(budgetId, display);

        // Existing budget rows keyed by COA id
        Map<Long, PassengerServiceVolumes> budgetByCoaId
                = budgetRepo.findByBudgetIdAndCoacodeDisplay(budgetId, display).stream()
                        .filter(v -> v.getCoacode() != null && v.getCoacode().getId() != null)
                        .collect(Collectors.toMap(v -> v.getCoacode().getId(), v -> v, (a, b) -> a));

        // Existing actual rows keyed by COA id
        Map<Long, PassengerActualVolumes> actualByCoaId
                = actualRepo.findByBudgetIdAndCoacodeDisplay(budgetId, display).stream()
                        .filter(v -> v.getCoacode() != null && v.getCoacode().getId() != null)
                        .collect(Collectors.toMap(v -> v.getCoacode().getId(), v -> v, (a, b) -> a));

        List<PassengerBudgetActualDTO> out = new ArrayList<>(coas.size());

        for (COA coa : coas) {
            PassengerBudgetActualDTO dto = new PassengerBudgetActualDTO();
            dto.setCoacode(coa);
            dto.setBudget(coa.getBudget()); // COA has budget in your entity

            PassengerServiceVolumes b = budgetByCoaId.get(coa.getId());
            if (b == null) {
                b = new PassengerServiceVolumes();
                b.setBudget(coa.getBudget());
                b.setCoacode(coa);
            }
            dto.setBudgetRow(b);

            PassengerActualVolumes a = actualByCoaId.get(coa.getId());
            if (a == null) {
                a = new PassengerActualVolumes();
                a.setBudget(coa.getBudget());
                a.setCoacode(coa);
            }
            dto.setActualRow(a);

            out.add(dto);
        }

        return out;
    }

    @Transactional
    public void saveBoth(PassengerServiceVolumes budgetRow, PassengerActualVolumes actualRow) {
        budgetRepo.save(budgetRow);
        actualRepo.save(actualRow);
    }

}
