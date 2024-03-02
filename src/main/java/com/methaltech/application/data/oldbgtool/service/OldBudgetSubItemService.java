package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.bgtool.repository.SectionDeptUnitMergerRepository;
import com.methaltech.application.data.entity.bgtool.SectionDeptUnitMerger;
import com.methaltech.application.data.entity.oldbgtool.OldBudgetSubItem;
import com.methaltech.application.data.entity.oldbgtool.OldDeptUnit;
import com.methaltech.application.data.oldbgtool.repository.OldBudgetSubItemRepository;
import com.methaltech.application.data.oldbgtool.repository.OldDeptUnitRepository;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OldBudgetSubItemService {

    private final OldBudgetSubItemRepository budgetSubItemRepository;
    private final OldDeptUnitRepository deptUnitRepository;
    private final SectionDeptUnitMergerRepository sectionDeptUnitMergerRepository;

    @Autowired
    public OldBudgetSubItemService(OldBudgetSubItemRepository budgetSubItemRepository, OldDeptUnitRepository deptUnitRepository,
            SectionDeptUnitMergerRepository sectionDeptUnitMergerRepository) {
        this.budgetSubItemRepository = budgetSubItemRepository;
        this.deptUnitRepository = deptUnitRepository;
        this.sectionDeptUnitMergerRepository = sectionDeptUnitMergerRepository;
    }



    public List<OldBudgetSubItem> getAllBudgetSubItems() {
        return budgetSubItemRepository.findAll();
    }

    public OldBudgetSubItem getBudgetSubItemById(Long id) {
        return budgetSubItemRepository.findById(id).orElse(null);
    }

    public List<OldBudgetSubItem> findBySectAndFy(Integer sect, String fy) {
        return budgetSubItemRepository.findBySectAndFy(sect, fy);
    }

    public List<OldBudgetSubItem> findItemsByFiscalYear(String fiscalYear) {
        return budgetSubItemRepository.findByFy(fiscalYear);
    }

    public List<OldBudgetSubItem> findAllByFyAndProgactivity(String fy, Integer progactivity) {
        return budgetSubItemRepository.findAllByFyAndProgactivity(fy, progactivity);
    }

    public List<Integer> findUniqueProgactivitiesByFy(String fy, Integer progactivity) {
        List<OldBudgetSubItem> budgetSubItems = findAllByFyAndProgactivity(fy, progactivity);

        List<Integer> uniqueProgactivities = budgetSubItems.stream()
                .map(OldBudgetSubItem::getDeptunit)
                .distinct()
                .collect(Collectors.toList());

        return removeDuplicates(uniqueProgactivities);
    }

    public List<Integer> findSectByFyAndProgactivity(String fy, Integer progactivity) {
        return budgetSubItemRepository.findDeptunitByFyAndProgactivity(fy, progactivity);
    }

    public List<Integer> removeDuplicates(List<Integer> inputList) {
        // Create a HashSet to store unique values
        HashSet<Integer> uniqueValues = new HashSet<>();

        // Create a new list to store the result without duplicates
        List<Integer> result = new ArrayList<>();

        // Iterate through the input list
        for (Integer item : inputList) {
            // If the item is not in the HashSet, add it to both the HashSet and the result list
            if (uniqueValues.add(item)) {
                result.add(item);
            }
        }

        return result;
    }

    public List<String> findSectionsInBudget(Integer progactivity) {
        List<String> sectioncodes = new ArrayList<>();
        List<Integer> dunits = removeDuplicates(budgetSubItemRepository.findDeptunitsByProgactivity(progactivity));
        List<String> unitcodes = new ArrayList<>();
        for (Integer i : dunits) {
            Optional<OldDeptUnit> oldunit = deptUnitRepository.findById(i);

            if (oldunit.isPresent()) {
                String sunCode = oldunit.get().getSunCode();
                if (sunCode != null && !sunCode.trim().isEmpty()) {
                    unitcodes.add(sunCode);
                    if (!unitcodes.isEmpty()) {
                        for (String b : unitcodes) {
                            List<SectionDeptUnitMerger> list = sectionDeptUnitMergerRepository.findAll();
                            for (SectionDeptUnitMerger e : list) {
                                for (String f : e.getDeptUnitcodes()) {
                                    //System.out.println(f + "    " + b);
                                    if (f.trim().equals(b.trim())) {

                                        sectioncodes.add(e.getSectioncode());

                                    }
                                }
                            }

                        }
                    }
                }
            }

        }
        return sectioncodes;
    }

}
