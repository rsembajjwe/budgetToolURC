package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.OrganisationRepository;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private CoaService coaService;

    public OrganisationService(OrganisationRepository organisationRepository,CoaService coaService) {
        this.organisationRepository = organisationRepository;
        this.coaService=coaService;
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

    public List<Organisation> findByBudget(Budget budget) {
        return organisationRepository.findByBudgetWithCoaAccounts(budget);
    }

    public List<Organisation> getOrganisationsByBudget(Budget budget) {
        return organisationRepository.findByBudgetWithCoaAccounts(budget);
        // Or use findByBudgetWithCoaAccounts(budget) if you need the COA list too
    }

    /*    public List<Organisation> findByBudgetList(Budget budget) {
    return organisationRepository.findByBudget(budget);
    }*/

    public BigDecimal getSumOfAnnualMonthsByBudgetAndOrganisation(Budget budget, Organisation organisation) {

        return organisationRepository.sumAnnualByBudgetAndOrganisation(budget, organisation);
    }
    
 public Organisation createOrganisation(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public Organisation saveOrganisation(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public Organisation updateOrganisation(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public void deleteOrganisation(Long organisationId) {
        organisationRepository.deleteById(organisationId);
    }

    public Optional<Organisation> getOrganisationById(Long id) {
        return organisationRepository.findById(id);
    }

    public List<Organisation> getAllOrganisations() {
        return organisationRepository.findAll();
    }

    /*    public List<Organisation> getOrganisationsByBudget(Budget budget) {
    return organisationRepository.findByBudgetOrderByNameAsc(budget);
    }*/

    public List<Organisation> getOrganisationsByName(String name) {
        return organisationRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }

    public List<Organisation> getOrganisationsByCode(String code) {
        return organisationRepository.findByCodeContainingIgnoreCaseOrderByCodeAsc(code);
    }

    public Page<Organisation> searchOrganisations(String searchTerm, Pageable pageable) {
        return organisationRepository.searchOrganisations(searchTerm, pageable);
    }

    /*    public List<Budget> getAllBudgets() {
    return budgetService.getAllBudgets();
    }*/

    // COA Assignment Methods
    public Organisation assignCOAToOrganisation(Long organisationId, Set<COA> coaAccounts) {
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(organisationId);
        if (!optionalOrganisation.isPresent()) {
            throw new RuntimeException("Organisation not found");
        }
        
        Organisation organisation = optionalOrganisation.get();
        
        // Clear existing COA assignments
        organisation.getCoaAccounts().clear();
        
        // Add new COA assignments
        for (COA coa : coaAccounts) {
            organisation.addCOA(coa);
        }
        
        return organisationRepository.save(organisation);
    }

    public Organisation addCOAToOrganisation(Long organisationId, COA coa) {
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(organisationId);
        if (!optionalOrganisation.isPresent()) {
            throw new RuntimeException("Organisation not found");
        }
        
        Organisation organisation = optionalOrganisation.get();
        organisation.addCOA(coa);
        
        return organisationRepository.save(organisation);
    }

    public Organisation removeCOAFromOrganisation(Long organisationId, COA coa) {
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(organisationId);
        if (!optionalOrganisation.isPresent()) {
            throw new RuntimeException("Organisation not found");
        }
        
        Organisation organisation = optionalOrganisation.get();
        organisation.removeCOA(coa);
        
        return organisationRepository.save(organisation);
    }

    public List<COA> getUnassignedCOAForBudget(Budget budget) {
        return coaService.getUnassignedCOA(budget);
    }

    public List<COA> getAssignedCOAForOrganisation(Long organisationId) {
        return coaService.getCOAByOrganisation(getOrganisationById(organisationId).orElse(null));
    }

    // Dashboard statistics
    public Long getTotalOrganisationsCount() {
        return organisationRepository.count();
    }

    public Long getOrganisationsWithCOACount() {
        return organisationRepository.countOrganisationsWithCOA();
    }

    public Long getOrganisationsWithoutCOACount() {
        return organisationRepository.countOrganisationsWithoutCOA();
    }

    public Long getTotalCOAAssignmentsCount() {
        return organisationRepository.countTotalCOAAssignments();
    }

    public List<Object[]> getCOAAssignmentsByOrganisation() {
        return organisationRepository.countCOAAssignmentsByOrganisation();
    }

    public List<Organisation> getOrganisationsWithMostCOA() {
        return organisationRepository.findOrganisationsWithMostCOA();
    }

    public List<Organisation> getOrganisationsWithoutCOA() {
        return organisationRepository.findOrganisationsWithoutCOA();
    }    
}
