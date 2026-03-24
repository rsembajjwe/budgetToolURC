package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.DepartmentGeneralPhysicalPerformanceRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.DepartmentGeneralPhysicalPerformance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentGeneralPhysicalPerformanceService {
    
    private final DepartmentGeneralPhysicalPerformanceRepository repository;
    
    @Transactional(readOnly = true)
    public DepartmentGeneralPhysicalPerformance getOrCreate(String code, String name, Budget budget) {
        return repository.findByCodeAndBudget(code, budget)
                .orElseGet(() -> {
                    DepartmentGeneralPhysicalPerformance entity = new DepartmentGeneralPhysicalPerformance();
                    entity.setCode(code);
                    entity.setName(name);
                    return entity;
                });
    }
    
    @Transactional
    public DepartmentGeneralPhysicalPerformance saveQuarter(
            String code,
            String name,
            String quarter,
            String html, Budget budget
    ) {
        DepartmentGeneralPhysicalPerformance entity = repository.findByCodeAndBudget(code, budget)
                .orElseGet(() -> {
                    DepartmentGeneralPhysicalPerformance x = new DepartmentGeneralPhysicalPerformance();
                    x.setCode(code);
                    x.setBudget(budget);
                    x.setName(name);
                    return x;
                });
        
        entity.setName(name);
        
        switch (quarter.toLowerCase()) {
            case "qtr1" ->
                entity.setQtr1(html);
            case "qtr2" ->
                entity.setQtr2(html);
            case "qtr3" ->
                entity.setQtr3(html);
            case "qtr4" ->
                entity.setQtr4(html);
            default ->
                throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }
        System.out.println(html+"Saved");
        return repository.save(entity);
    }
    
    @Transactional(readOnly = true)
    public String getQuarterHtml(String code, String quarter, Budget budget) {
        return repository.findByCodeAndBudget(code, budget)
                .map(entity -> switch (quarter.toLowerCase()) {
            case "qtr1" ->
                entity.getQtr1();
            case "qtr2" ->
                entity.getQtr2();
            case "qtr3" ->
                entity.getQtr3();
            case "qtr4" ->
                entity.getQtr4();
            default ->
                "";
        })
                .orElse("");
    }
}
