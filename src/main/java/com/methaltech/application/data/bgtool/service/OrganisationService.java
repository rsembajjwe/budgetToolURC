package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.OrganisationRepository;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.Budget;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public Organisation create(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public Organisation update(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public List<Organisation> findAll() {
        return organisationRepository.findAll();
    }

    public Organisation findById(Long id) {
        return organisationRepository.findById(id).orElse(null);
    }

    public Organisation getLastSavedOrganisationByBudget(Budget budget) {
        return organisationRepository.findTopByBudgetOrderByIdDesc(budget);
    }

    public Organisation getLastSavedOrganisationByBudget() {
        return organisationRepository.findTopByOrderByIdDesc();
    }

    public void delete(Long id) {
        organisationRepository.deleteById(id);
    }

    public Page<Organisation> list(Pageable pageable) {
        return organisationRepository.findAll(pageable);
    }

    public Page<Organisation> findByBudget(Budget budget, Pageable pageable) {
        return organisationRepository.findByBudget(budget, pageable);
    }

    public List<Organisation> findByBudgetList(Budget budget) {
        return organisationRepository.findByBudget(budget);
    }
}
