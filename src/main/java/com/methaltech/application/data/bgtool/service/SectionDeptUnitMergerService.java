package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.SectionDeptUnitMergerRepository;
import com.methaltech.application.data.entity.bgtool.SectionDeptUnitMerger;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SectionDeptUnitMergerService {

    private final SectionDeptUnitMergerRepository mergerRepository;

    @Autowired
    public SectionDeptUnitMergerService(SectionDeptUnitMergerRepository mergerRepository) {
        this.mergerRepository = mergerRepository;
    }

    public List<SectionDeptUnitMerger> getAllMergers() {
        return mergerRepository.findAll();
    }

    public Optional<SectionDeptUnitMerger> getMergerById(Long id) {
        return mergerRepository.findById(id);
    }

    public Optional<SectionDeptUnitMerger> getMergerBySectioncode(String sectioncode) {
        return mergerRepository.findBySectioncode(sectioncode);
    }

    public SectionDeptUnitMerger createMerger(SectionDeptUnitMerger merger) {
        return mergerRepository.save(merger);
    }

    public SectionDeptUnitMerger updateMerger(Long id, SectionDeptUnitMerger updatedMerger) {
        Optional<SectionDeptUnitMerger> existingMergerOptional = mergerRepository.findById(id);
        if (existingMergerOptional.isPresent()) {
            SectionDeptUnitMerger existingMerger = existingMergerOptional.get();
            existingMerger.setSectioncode(updatedMerger.getSectioncode());
            existingMerger.setDeptUnitcodes(updatedMerger.getDeptUnitcodes());
            return mergerRepository.save(existingMerger);
        } else {
            throw new RuntimeException("Merger with ID " + id + " not found");
        }
    }

    public void deleteMerger(Long id) {
        mergerRepository.deleteById(id);
    }

    public List<String> findSectionDeptUnitMergersByDeptUnitcodesContaining(String searchString) {
        List<String> sectionCodes = mergerRepository.findDistinctSectionCodesByDeptUnitcode(searchString);

// Remove duplicates by converting the list to a stream and using distinct()
        List<String> distinctSectionCodes = sectionCodes.stream().distinct().collect(Collectors.toList());
        return distinctSectionCodes;
    }

    public String findSectionCodeByDeptUnitCode(String deptUnitcode) {
        return mergerRepository.findSectionCodeByDeptUnitCode(deptUnitcode);
    }

    public List<String> findSectionCodesByDeptUnitCode(String deptUnitcode) {
        return mergerRepository.findSectionCodesByDeptUnitCode(deptUnitcode);
    }

    public List<SectionDeptUnitMerger> findSectionCodesByDeptUnitCode2(String deptUnitcode) {
        return mergerRepository.findSectionCodesByDeptUnitCode2(deptUnitcode);
    }

    public String findSectionCodesByDeptUnitCode3(String deptUnitcode) {
        List<SectionDeptUnitMerger> findAll = mergerRepository.findAll();
        String code = "";
        for (SectionDeptUnitMerger a : findAll) {
            for (String b : a.getDeptUnitcodes()) {
                if (b.equals(deptUnitcode)) {
                    code = a.getSectioncode();
                    break;
                }
            }
        }
        return code;
    }
    public  List<Integer> removeDuplicates(List<Integer> inputList) {
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
}
