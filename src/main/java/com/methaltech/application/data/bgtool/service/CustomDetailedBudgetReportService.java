package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.CustomDetailedBudgetReportRepository;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReport;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReportImp;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomDetailedBudgetReportService {

    private final CustomDetailedBudgetReportRepository repository;

    @Autowired
    public CustomDetailedBudgetReportService(CustomDetailedBudgetReportRepository repository) {
        this.repository = repository;
    }

    public List<CustomDetailedBudgetReport> getAllReports() {
        return repository.findAll();
    }

    public Optional<CustomDetailedBudgetReport> getReportById(Long id) {
        return repository.findById(id);
    }

    public CustomDetailedBudgetReport saveReport(CustomDetailedBudgetReport report) {
        return repository.save(report);
    }

    public List<CustomDetailedBudgetReport> findByBudgetreport(CustomDetailedBudgetReportImp budgetreport) {
        return repository.findByBudgetreport(budgetreport);
    }

    public void deleteReport(Long id) {
        repository.deleteById(id);
    }

public Set<UrcDeptSectionAnlDimbgt> getCombinedDeptSections(
        List<CustomDetailedBudgetReport> reports) {

    return reports.stream()
            .filter(Objects::nonNull)
            .map(CustomDetailedBudgetReport::getDeptsection)
            .filter(Objects::nonNull)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
}

    // Add other business logic or custom methods if needed
}
