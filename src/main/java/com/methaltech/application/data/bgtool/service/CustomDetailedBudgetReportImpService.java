
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.CustomDetailedBudgetReportImpRepository;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReportImp;
import com.methaltech.application.data.entity.bgtool.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomDetailedBudgetReportImpService {

    private final CustomDetailedBudgetReportImpRepository repository;

    @Autowired
    public CustomDetailedBudgetReportImpService(CustomDetailedBudgetReportImpRepository repository) {
        this.repository = repository;
    }

    public List<CustomDetailedBudgetReportImp> getAllReportsImp() {
        return repository.findAll();
    }

    public Optional<CustomDetailedBudgetReportImp> getReportImpById(Long id) {
        return repository.findById(id);
    }

    public CustomDetailedBudgetReportImp saveReportImp(CustomDetailedBudgetReportImp reportImp) {
        return repository.save(reportImp);
    }

    public void deleteReportImp(Long id) {
        repository.deleteById(id);
    }
    public List<CustomDetailedBudgetReportImp> getAllReportsImpByUser(User user) {
        return repository.findByUser(user);
    }
}

