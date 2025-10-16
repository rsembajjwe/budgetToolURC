package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.bgtool.repository.CoaRepository;
import com.methaltech.application.data.bgtool.repository.Coalevel11Repository;
import com.methaltech.application.data.bgtool.repository.Coalevel12Repository;
import com.methaltech.application.data.bgtool.repository.Coalevel13Repository;
import com.methaltech.application.data.bgtool.repository.Coalevel1Repository;
import com.methaltech.application.data.bgtool.repository.SectionRepository;
import com.methaltech.application.data.bgtool.repository.UnitRepository;
import com.methaltech.application.data.livedata.repository.UR5_ACNTRepository;
import com.methaltech.application.data.entity.bgtool.*;
import com.methaltech.application.data.entity.livedata.UR5_ACNT;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CoaService {

    private final CoaRepository coaRepository;
    private final UnitRepository dUnitRepository;
    private final SectionRepository sectionRepository;
    private final UR5_ACNTRepository urc5acntRepository;
    private final Coalevel1Repository coalevel1Repository;
    private final Coalevel11Repository coalevel11Repository;
    private final Coalevel12Repository coalevel12Repository;
    private final Coalevel13Repository coalevel13Repository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CoaService(CoaRepository coaRepository, UnitRepository dUnitRepository,
            UR5_ACNTRepository urc5acntRepository, Coalevel1Repository coalevel1Repository,
            Coalevel11Repository coalevel11Repository, Coalevel12Repository coalevel12Repository,
            SectionRepository sectionRepository, Coalevel13Repository coalevel13Repository) {
        this.coaRepository = coaRepository;
        this.dUnitRepository = dUnitRepository;
        this.urc5acntRepository = urc5acntRepository;
        this.coalevel1Repository = coalevel1Repository;
        this.coalevel11Repository = coalevel11Repository;
        this.coalevel12Repository = coalevel12Repository;
        this.sectionRepository = sectionRepository;
        this.coalevel13Repository = coalevel13Repository;
    }

    public List<COA> findAll() {
        return coaRepository.findAll();
    }

    public COA findById(Long id) {
        return coaRepository.findById(id).orElse(null);
    }

    public COA findByCodeAndBudget(String code, Budget budget) {
        return coaRepository.findByCodeAndBudget(code, budget);
    }

    public List<COA> findByCodeAndBudget2(String code, Budget budget) {
        return coaRepository.findByCodeAndBudget2(code, budget);
    }

    public List<COA> findByBudget(Budget budget) {
        return coaRepository.findByBudget(budget);
    }

    /*    public List<COA> findByCode(String code) {
    return coaRepository.findByCode(code);
    }*/
    public COA findByCodeAndBudgetWithDSections(String code, Budget budget) {
        return coaRepository.findByCodeAndBudgetWithDSections(code, budget);
    }

    @Transactional
    public COA save(COA coa) {
        try {
            for (Section section : coa.getDsections()) {
                entityManager.merge(section);
            }
            return coaRepository.save(coa);
        } catch (Exception e) {
            // Log detailed information about the exception, including the data causing the conflict.
            e.printStackTrace();
            throw e; // Rethrow the exception to propagate it.
        }
    }

    public COA saveCOA(COA coa) {
        return coaRepository.save(coa);
    }

    /*    public boolean existsByCode(String code) {
    return coaRepository.existsByCode(code);
    }*/
    public boolean existsByCode(String code, Budget budget) {
        return coaRepository.existsByCodeAndBudget(code, budget);
    }

    public void deleteById(Long id) {
        coaRepository.deleteById(id);
    }

    public Page<COA> list(Pageable pageable) {
        return coaRepository.findAll(pageable);
    }

    public List<COA> list() {
        return coaRepository.findAll();
    }

    public List<COA> findByBudgetAndCoalevel1(Budget budget, Coalevel1 coalevel1) {
        return coaRepository.findByBudgetAndCoalevel1(budget, coalevel1);
    }

    public List<COA> getCOAList(Budget budget, Coalevel1 coalevel1, String nameOrCode) {
        if (nameOrCode == null || nameOrCode.isEmpty()) {
            return coaRepository.findByBudgetAndCoalevel1(budget, coalevel1);
        } else {
            return coaRepository.findByBudgetAndCoalevel1AndSubstring(budget, coalevel1, nameOrCode);
        }
    }

    public Page<COA> findByCoalevel1AndDUnitsAndBudget(Coalevel1 coalevel1, D_Unit dUnit, Budget budget, Pageable page) {
        //return coaRepository.findByCoalevel1AndBudgetAndDUnit(coalevel1, dUnit, budget, page);
        return null;
    }

    public void saveFromSunDb(Budget budget) {
        List<UR5_ACNT> thesuncodes = urc5acntRepository.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();
        for (UR5_ACNT acountcode : thesuncodes) {
            COA coa = new COA();
            if (acountcode.getAcntCode().startsWith("1")) {
                coa.setCoalevel1(coalevel1Repository.findByNameAndBudget("Income"));
            } else if (acountcode.getAcntCode().startsWith("2")) {
                coa.setCoalevel1(coalevel1Repository.findByNameAndBudget("Operation Expenditure"));
            } else if (acountcode.getAcntCode().startsWith("3")) {
                coa.setCoalevel1(coalevel1Repository.findByNameAndBudget("Capital Expenditure"));
            }

            coa.setBudget(budget);
            coa.setCode(acountcode.getAcntCode());
            coa.setName(acountcode.getDescr());
            coaRepository.save(coa);

        }

    }

    public boolean isCoaTableEmpty(Budget budget) {
        return !coaRepository.existsByBudget(budget);
    }

    public int saveFromPreviousBudget(Budget oldBudget, Budget newBudget) {
        List<COA> coaList = coaRepository.findByBudget(oldBudget);
        int num = coaList.size();

        for (COA sourceCOA : coaList) {
            // Create a new COA entity for the target Coalevel12 entity and copy the common attributes
            COA targetCOA = new COA();
            targetCOA.setCode(sourceCOA.getCode());
            targetCOA.setName(sourceCOA.getName());
            targetCOA.setBudget(newBudget);
            targetCOA.setDisplay(sourceCOA.getDisplay());
            targetCOA.setProcclass(sourceCOA.getProcclass());
            targetCOA.setDeptsection(sourceCOA.getDeptsection());
            targetCOA.setStatCode(sourceCOA.getStatCode());
            targetCOA.setStateOpen(sourceCOA.isStateOpen());

            Coalevel1 targetCoalevell = coalevel1Repository.findByNameAndBudget(sourceCOA.getCoalevel1().getName());
            targetCOA.setCoalevel1(targetCoalevell);

            targetCOA.setCoalevel11(
                    Optional.ofNullable(sourceCOA.getCoalevel11())
                            .map(coa11 -> coalevel11Repository.findByCoalevel1AndName(targetCoalevell, coa11.getName()))
                            .orElse(null)
            );

            targetCOA.setCoalevel12(
                    Optional.ofNullable(sourceCOA.getCoalevel12())
                            .map(coa12 -> coalevel12Repository.findByCoalevel1AndName(targetCoalevell, coa12.getName()))
                            .orElse(null)
            );

            if (sourceCOA.getCoalevel13() != null) {
                Coalevel13 coalevel13 = coalevel13Repository.findByCoalevel11AndName(targetCOA.getCoalevel11(), sourceCOA.getCoalevel13().getName());
                targetCOA.setCoalevel13(coalevel13);
            }

            // Create a new Set for Dsections to avoid shared references
            Set<UrcDeptSectionAnlDimbgt> unitnew = new HashSet<>();
            for (UrcDeptSectionAnlDimbgt units : sourceCOA.getDeptsection()) {
                if (units != null) {
                    unitnew.add(units);
                }
            }
            targetCOA.setDeptsection(unitnew);

            // Save the new COA entity to the target database
            coaRepository.save(targetCOA);
            num++;
        }

        return num;
    }

    public void updateCoa(Budget budget) {
        List<UR5_ACNT> caosourceList = urc5acntRepository.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();
        COA coa;
        for (UR5_ACNT coasun : caosourceList) {
            if (coaRepository.existsByCodeAndBudget(coasun.getAcntCode(), budget) == true) {
                coa = coaRepository.findByCodeAndBudget(coasun.getAcntCode(), budget);
                coa.setName(coasun.getDescr());
            } else {
                coa = new COA();
                coa.setName(coasun.getDescr());
                coa.setCode(coasun.getAcntCode());
                if (coasun.getAcntCode().startsWith("1")) {
                    coa.setCoalevel1(coalevel1Repository.findByNameAndBudget("Income"));
                } else if (coasun.getAcntCode().startsWith("2")) {
                    coa.setCoalevel1(coalevel1Repository.findByNameAndBudget("Operation Expenditure"));
                } else if (coasun.getAcntCode().startsWith("3")) {
                    coa.setCoalevel1(coalevel1Repository.findByNameAndBudget("Capital Expenditure"));
                }
                coa.setBudget(budget);
            }
            coaRepository.save(coa);
        }
    }

    public List<COA> findByBudgetAndD_sections(Budget budget) {
        return coaRepository.findByBudgetAndDSections(budget);
    }

    public List<COA> findByDeptsectionAndCodePrefix(Set<UrcDeptSectionAnlDimbgt> section, String code, Budget budget) {
        return coaRepository.findByDeptsectionAndCodePrefix(section, code, budget);
    }

    /*    public List<COA> findBySectionsAndCodePrefixSearch(Set<Section> section, String code, String category) {
    return coaRepository.findBySectionsAndCodePrefixSearch(section, code,category);
    }*/
    public List<COA> findBySectionsAndCodePrefixSearch(Set<UrcDeptSectionAnlDimbgt> section, String code, String category, Budget budget) {
        return coaRepository.findByDeptsectionAndCodePrefixSearch(section, code, category, budget);
    }

    public List<COA> findByDeptsectionAndCoalevel1AndBudgetAndSearchTerm(Set<UrcDeptSectionAnlDimbgt> section, Coalevel1 coal1, Budget budget, String term) {
        return coaRepository.findByDeptsectionAndCoalevel1AndBudgetAndSearchTerm(section, coal1, budget, term);
    }

    public List<COA> findBySectionsAndCodePrefixSearch2(Set<Section> section, String code, String category, Budget budget) {
        return coaRepository.findBySectionsAndCodePrefixSearch2(section, code, category, budget);
    }

    public List<COA> findByDeptSectionAndCodeStartingWith(UrcDeptSectionAnlDimbgt section, String code, Budget budget) {
        return coaRepository.findByDeptSectionAndCodeStartingWithAndStateOpen(section, code, budget);
    }
    
    public List<COA> findByDeptSectionAndCodeStartingWith2Or3AndStateOpen(UrcDeptSectionAnlDimbgt section, Budget budget) {
        return coaRepository.findByDeptSectionAndCodeStartingWith2Or3AndStateOpen(section,  budget);
    }    

    public List<COA> findByBudgetAndDisplay(Budget budget, Display display) {
        return coaRepository.findByBudgetAndDisplay(budget, display);
    }

    public List<COA> findByBudgetAndDisplayAndStateOpen(Budget budget, Display display, boolean stateOpen) {
        return coaRepository.findByBudgetAndDisplayAndStateOpen(budget, display, stateOpen);
    }

    public int count() {
        return (int) coaRepository.count();
    }

    public List<COA> findByBudgetAndCoalevel11(Budget budget, Coalevel11 coalevel11) {
        return coaRepository.findByBudgetAndCoalevel11(budget, coalevel11);
    }

    public List<COA> findByBudgetAndProcclassIn(Budget budget, List<ProcClass> procclasses) {
        return coaRepository.findByBudgetAndProcclassIn(budget, procclasses);
    }

    public List<COA> findByBudgetAndProcclass(Budget budget, ProcClass procclasses) {
        return coaRepository.findByBudgetAndProcclass(budget, procclasses);
    }

    public List<COA> getCoaByBudgetAndCodePrefix(Budget budget, String prefix) {
        return coaRepository.findByBudgetAndCodeStartingWith(budget, prefix);
    }

// ===================================================================
    // BASIC CRUD OPERATIONS
    // ===================================================================
    public COA createCOA(COA coa) {
        validateCOA(coa);
        return coaRepository.save(coa);
    }

    /*    public COA saveCOA(COA coa) {
    return coaRepository.save(coa);
    }*/
    public COA updateCOA(COA coa) {
        validateCOA(coa);
        return coaRepository.save(coa);
    }

    public void deleteCOA(Long coaId) {
        Optional<COA> coa = coaRepository.findById(coaId);
        if (coa.isPresent() && coa.get().getOrganisation() != null) {
            throw new RuntimeException("Cannot delete COA that is assigned to an organisation");
        }
        coaRepository.deleteById(coaId);
    }

    public Optional<COA> getCOAById(Long id) {
        return coaRepository.findById(id);
    }

    public Optional<COA> getCOAByCode(String code) {
        return coaRepository.findByCode(code);
    }

    public List<COA> getAllCOA() {
        return coaRepository.findAll();
    }

    // ===================================================================
    // BUDGET-RELATED OPERATIONS
    // ===================================================================
    public List<COA> getCOAByBudget(Budget budget) {
        return coaRepository.findByBudgetOrderByCodeAsc(budget);
    }

    public List<COA> getActiveCOA() {
        return coaRepository.findByStateOpenTrueOrderByCodeAsc();
    }

    public List<COA> getCOAByBudgetAndActive(Budget budget) {
        return coaRepository.findByBudgetAndStateOpenTrueOrderByCodeAsc(budget);
    }

    public List<COA> getInactiveCOA() {
        return coaRepository.findByStateOpenFalseOrderByCodeAsc();
    }

    public List<COA> getCOAByBudgetAndInactive(Budget budget) {
        return coaRepository.findByBudgetAndStateOpenFalseOrderByCodeAsc(budget);
    }

    // ===================================================================
    // ORGANISATION-RELATED OPERATIONS
    // ===================================================================
    public List<COA> getCOAByOrganisation(Organisation organisation) {
        if (organisation == null) {
            return List.of();
        }
        return coaRepository.findByOrganisationOrderByCodeAsc(organisation);
    }

    public List<COA> getUnassignedCOA(Budget budget) {
        return coaRepository.findByBudgetAndOrganisationIsNullOrderByCodeAsc(budget);
    }

    public List<COA> getAssignedCOA(Budget budget) {
        return coaRepository.findByBudgetAndOrganisationIsNotNullOrderByCodeAsc(budget);
    }

    public List<COA> getAvailableForAssignment(Budget budget) {
        return coaRepository.findAvailableForAssignment(budget);
    }

    // ===================================================================
    // HIERARCHY-RELATED OPERATIONS
    // ===================================================================
    public List<COA> getCOAByCoalevel1(Long coalevel1Id) {
        return coaRepository.findByCoalevel1_IdOrderByCodeAsc(coalevel1Id);
    }

    public List<COA> getCOAByCoalevel11(Long coalevel11Id) {
        return coaRepository.findByCoalevel11_IdOrderByCodeAsc(coalevel11Id);
    }

    public List<COA> getCOAByCoalevel12(Long coalevel12Id) {
        return coaRepository.findByCoalevel12_IdOrderByCodeAsc(coalevel12Id);
    }

    public List<COA> getCOAByCoalevel13(Long coalevel13Id) {
        return coaRepository.findByCoalevel13_IdOrderByCodeAsc(coalevel13Id);
    }

    public List<COA> getCOAByHierarchy(Long coalevel1Id, Long coalevel11Id, Long coalevel12Id, Long coalevel13Id) {
        return coaRepository.findByHierarchy(coalevel1Id, coalevel11Id, coalevel12Id, coalevel13Id);
    }

    // ===================================================================
    // SEARCH OPERATIONS
    // ===================================================================
    public Page<COA> searchCOA(String searchTerm, Pageable pageable) {
        return coaRepository.searchCOA(searchTerm, pageable);
    }

    public Page<COA> searchCOAByBudget(Budget budget, String searchTerm, Pageable pageable) {
        return coaRepository.searchCOAByBudget(budget, searchTerm, pageable);
    }

    public Page<COA> searchCOAByOrganisation(Organisation organisation, String searchTerm, Pageable pageable) {
        return coaRepository.searchCOAByOrganisation(organisation, searchTerm, pageable);
    }

    public List<COA> getCOAByNameContaining(String name) {
        return coaRepository.findByNameContainingIgnoreCaseOrderByCodeAsc(name);
    }

    public List<COA> getCOAByCodeContaining(String code) {
        return coaRepository.findByCodeContainingIgnoreCaseOrderByCodeAsc(code);
    }

    public List<COA> findByCriteria(Budget budget, Organisation organisation, Boolean stateOpen, String searchTerm) {
        return coaRepository.findByCriteria(budget, organisation, stateOpen, searchTerm);
    }

    // ===================================================================
    // ASSIGNMENT OPERATIONS
    // ===================================================================
    @Transactional
    public COA assignToOrganisation(Long coaId, Organisation organisation) {
        Optional<COA> optionalCoa = coaRepository.findById(coaId);
        if (!optionalCoa.isPresent()) {
            throw new RuntimeException("COA not found with ID: " + coaId);
        }

        COA coa = optionalCoa.get();

        if (!coa.isStateOpen()) {
            throw new RuntimeException("Cannot assign inactive COA to organisation");
        }

        coa.setOrganisation(organisation);
        return coaRepository.save(coa);
    }

    @Transactional
    public COA unassignFromOrganisation(Long coaId) {
        Optional<COA> optionalCoa = coaRepository.findById(coaId);
        if (!optionalCoa.isPresent()) {
            throw new RuntimeException("COA not found with ID: " + coaId);
        }

        COA coa = optionalCoa.get();
        coa.setOrganisation(null);
        return coaRepository.save(coa);
    }

    @Transactional
    public int bulkAssignToOrganisation(Organisation organisation, List<Long> coaIds) {
        // Validate all COA are available for assignment
        List<COA> coaList = coaRepository.findAllById(coaIds);
        for (COA coa : coaList) {
            if (!coa.isStateOpen()) {
                throw new RuntimeException("COA " + coa.getCode() + " is inactive and cannot be assigned");
            }
            if (coa.getOrganisation() != null) {
                throw new RuntimeException("COA " + coa.getCode() + " is already assigned to another organisation");
            }
        }

        return coaRepository.bulkAssignToOrganisation(organisation, coaIds);
    }

    @Transactional
    public int bulkUnassignFromOrganisation(Organisation organisation) {
        return coaRepository.bulkUnassignFromOrganisation(organisation);
    }

    // ===================================================================
    // STATE MANAGEMENT OPERATIONS
    // ===================================================================
    @Transactional
    public COA activateCOA(Long coaId) {
        Optional<COA> optionalCoa = coaRepository.findById(coaId);
        if (!optionalCoa.isPresent()) {
            throw new RuntimeException("COA not found with ID: " + coaId);
        }

        COA coa = optionalCoa.get();
        coa.setStateOpen(true);
        return coaRepository.save(coa);
    }

    @Transactional
    public COA deactivateCOA(Long coaId) {
        Optional<COA> optionalCoa = coaRepository.findById(coaId);
        if (!optionalCoa.isPresent()) {
            throw new RuntimeException("COA not found with ID: " + coaId);
        }

        COA coa = optionalCoa.get();

        // Check if COA is assigned to an organisation
        if (coa.getOrganisation() != null) {
            throw new RuntimeException("Cannot deactivate COA that is assigned to an organisation. Please unassign first.");
        }

        coa.setStateOpen(false);
        return coaRepository.save(coa);
    }

    @Transactional
    public int bulkActivateCOAs(List<Long> coaIds) {
        return coaRepository.activateCOAs(coaIds);
    }

    @Transactional
    public int bulkDeactivateCOAs(List<Long> coaIds) {
        // Check if any COA is assigned
        List<COA> assignedCOAs = coaRepository.findAllById(coaIds).stream()
                .filter(coa -> coa.getOrganisation() != null)
                .toList();

        if (!assignedCOAs.isEmpty()) {
            String assignedCodes = assignedCOAs.stream()
                    .map(COA::getCode)
                    .collect(java.util.stream.Collectors.joining(", "));
            throw new RuntimeException("Cannot deactivate assigned COAs: " + assignedCodes);
        }

        return coaRepository.deactivateCOAs(coaIds);
    }

    // ===================================================================
    // DASHBOARD STATISTICS
    // ===================================================================
    public Long getTotalCOACount() {
        return coaRepository.count();
    }

    public Long getActiveCOACount() {
        return coaRepository.countByStateOpenTrue();
    }

    public Long getInactiveCOACount() {
        return coaRepository.countByStateOpenFalse();
    }

    public Long getAssignedCOACount() {
        return coaRepository.countByOrganisationIsNotNull();
    }

    public Long getUnassignedCOACount() {
        return coaRepository.countByOrganisationIsNull();
    }

    public Long getCOACountByBudget(Budget budget) {
        return coaRepository.countByBudget(budget);
    }

    public Long getActiveCOACountByBudget(Budget budget) {
        return coaRepository.countByBudgetAndStateOpenTrue(budget);
    }

    public Long getAssignedCOACountByBudget(Budget budget) {
        return coaRepository.countByBudgetAndOrganisationIsNotNull(budget);
    }

    public Object[] getCOAStatistics(Budget budget) {
        return coaRepository.getCOAStatistics(budget);
    }

    public Object[] getOrganisationAssignmentStatistics(Budget budget) {
        return coaRepository.getOrganisationAssignmentStatistics(budget);
    }

    // ===================================================================
    // REPORTING OPERATIONS
    // ===================================================================
    public List<Object[]> getCOACountByOrganisation() {
        return coaRepository.countCOAByOrganisation();
    }

    public List<Object[]> getCOACountByCoalevel1() {
        return coaRepository.countCOAByCoalevel1();
    }

    public List<Object[]> getCOACountByCoalevel11() {
        return coaRepository.countCOAByCoalevel11();
    }

    public List<Object[]> getCOAUtilizationReport() {
        return coaRepository.getCOAUtilizationReport();
    }

    public List<Object[]> getOrganisationAssignmentSummary(Budget budget) {
        return coaRepository.getOrganisationAssignmentSummary(budget);
    }

    public List<Object[]> getHierarchyDistributionReport(Budget budget) {
        return coaRepository.getHierarchyDistributionReport(budget);
    }

    // ===================================================================
    // MAINTENANCE OPERATIONS
    // ===================================================================
    public List<COA> getOrphanedCOA() {
        return coaRepository.findOrphanedCOA();
    }

    public List<COA> getCOAWithoutLevel1() {
        return coaRepository.findCOAWithoutLevel1();
    }

    public List<COA> getInactiveCOAWithAssignments() {
        return coaRepository.findInactiveCOAWithAssignments();
    }

    public List<COA> getCOAWithCircularHierarchy() {
        return coaRepository.findCOAWithCircularHierarchy();
    }

    public List<COA> getInconsistentCOAState() {
        return coaRepository.findInconsistentCOAState();
    }

    public List<Object[]> getDuplicateCodes() {
        return coaRepository.findDuplicateCodes();
    }

    // ===================================================================
    // EXPORT OPERATIONS
    // ===================================================================
    public List<COA> getCOAForExport(Budget budget) {
        return coaRepository.findForExport(budget);
    }

    public List<COA> getAssignedCOAForExport(Budget budget) {
        return coaRepository.findAssignedForExport(budget);
    }

    public List<COA> getUnassignedCOAForExport(Budget budget) {
        return coaRepository.findUnassignedForExport(budget);
    }

    // ===================================================================
    // PERFORMANCE OPTIMIZED OPERATIONS
    // ===================================================================
    public List<COA> getMinimalCOAForDropdown(Budget budget) {
        return coaRepository.findMinimalCOAForDropdown(budget);
    }

    public List<String> getCOACodesByBudget(Budget budget) {
        return coaRepository.findCOACodesByBudget(budget);
    }

    public List<String> getCOANamesByBudget(Budget budget) {
        return coaRepository.findCOANamesByBudget(budget);
    }

    public List<COA> getRecentlyModified(Budget budget, Pageable pageable) {
        return coaRepository.findRecentlyModified(budget, pageable);
    }

    // ===================================================================
    // ADVANCED FILTERING
    // ===================================================================
    public List<COA> getAvailableForAssignmentWithFilters(Budget budget, Long coalevel1Id, String searchTerm) {
        return coaRepository.findAvailableForAssignmentWithFilters(budget, coalevel1Id, searchTerm);
    }

    public Page<COA> findByMultipleCriteria(Budget budget, Organisation organisation, Boolean stateOpen,
            Long coalevel1Id, Long coalevel11Id, String searchTerm, Pageable pageable) {
        return coaRepository.findByMultipleCriteria(budget, organisation, stateOpen, coalevel1Id, coalevel11Id, searchTerm, pageable);
    }

    // ===================================================================
    // VALIDATION OPERATIONS
    // ===================================================================
    public boolean isCodeUnique(String code) {
        return !coaRepository.existsByCode(code);
    }

    public boolean isCodeUniqueForUpdate(String code, Long coaId) {
        return !coaRepository.existsByCodeAndIdNot(code, coaId);
    }

    private void validateCOA(COA coa) {
        if (coa.getCode() == null || coa.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("COA code is required");
        }

        if (coa.getName() == null || coa.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("COA name is required");
        }

        if (coa.getBudget() == null) {
            throw new IllegalArgumentException("Budget is required for COA");
        }

        // Check for duplicate code
        if (coa.getId() == null) {
            // New COA
            if (!isCodeUnique(coa.getCode())) {
                throw new IllegalArgumentException("COA code '" + coa.getCode() + "' already exists");
            }
        } else {
            // Existing COA
            if (!isCodeUniqueForUpdate(coa.getCode(), coa.getId())) {
                throw new IllegalArgumentException("COA code '" + coa.getCode() + "' already exists");
            }
        }
    }

    // ===================================================================
    // BUSINESS LOGIC OPERATIONS
    // ===================================================================
    @Transactional
    public void transferCOABetweenOrganisations(List<Long> coaIds, Organisation fromOrganisation, Organisation toOrganisation) {
        List<COA> coaList = coaRepository.findAllById(coaIds);

        // Validate all COA belong to the source organisation
        for (COA coa : coaList) {
            if (!fromOrganisation.equals(coa.getOrganisation())) {
                throw new RuntimeException("COA " + coa.getCode() + " does not belong to the source organisation");
            }
            if (!coa.isStateOpen()) {
                throw new RuntimeException("COA " + coa.getCode() + " is inactive and cannot be transferred");
            }
        }

        // Perform the transfer
        coaRepository.bulkAssignToOrganisation(toOrganisation, coaIds);
    }

    @Transactional
    public void massUnassignCOA(List<Long> coaIds) {
        coaRepository.removeOrganisationForCOAs(coaIds);
    }

    @Transactional
    public void syncCOAWithBudget(Budget budget) {
        // Business logic to sync COA with budget changes
        List<COA> budgetCOAs = getCOAByBudget(budget);

        // Example: Deactivate COA if budget is closed
        if (!budget.isActive()) {
            List<Long> coaIds = budgetCOAs.stream()
                    .filter(COA::isStateOpen)
                    .map(COA::getId)
                    .toList();

            if (!coaIds.isEmpty()) {
                bulkDeactivateCOAs(coaIds);
            }
        }
    }

    // ===================================================================
    // UTILITY OPERATIONS
    // ===================================================================
    public boolean canDeleteCOA(Long coaId) {
        Optional<COA> coa = getCOAById(coaId);
        return coa.isPresent() && coa.get().getOrganisation() == null;
    }

    public boolean canDeactivateCOA(Long coaId) {
        Optional<COA> coa = getCOAById(coaId);
        return coa.isPresent() && coa.get().getOrganisation() == null && coa.get().isStateOpen();
    }

    public String getValidationMessage(COA coa) {
        if (coa.getCode() == null || coa.getCode().trim().isEmpty()) {
            return "COA code is required";
        }
        if (coa.getName() == null || coa.getName().trim().isEmpty()) {
            return "COA name is required";
        }
        if (coa.getBudget() == null) {
            return "Budget assignment is required";
        }

        // Check for duplicate code
        if (coa.getId() == null) {
            if (!isCodeUnique(coa.getCode())) {
                return "COA code '" + coa.getCode() + "' already exists";
            }
        } else {
            if (!isCodeUniqueForUpdate(coa.getCode(), coa.getId())) {
                return "COA code '" + coa.getCode() + "' already exists";
            }
        }

        return null; // No validation errors
    }

    // ===================================================================
    // IMPORT/EXPORT HELPERS
    // ===================================================================
    @Transactional
    public List<COA> importCOAList(List<COA> coaList) {
        List<COA> savedCOAs = new ArrayList<>();

        for (COA coa : coaList) {
            try {
                validateCOA(coa);
                savedCOAs.add(coaRepository.save(coa));
            } catch (Exception e) {
                // Log error but continue with other COAs
                System.err.println("Failed to import COA " + coa.getCode() + ": " + e.getMessage());
            }
        }

        return savedCOAs;
    }

    public List<COA> getCOAForBulkOperations(Budget budget, List<String> codes) {
        return codes.stream()
                .map(code -> coaRepository.findByCode(code))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(coa -> budget.equals(coa.getBudget()))
                .toList();
    }

    // ===================================================================
    // ORGANIZATION SPECIFIC OPERATIONS
    // ===================================================================
    @Transactional
    public void reassignAllCOA(Organisation fromOrganisation, Organisation toOrganisation) {
        List<COA> organisationCOAs = getCOAByOrganisation(fromOrganisation);
        List<Long> coaIds = organisationCOAs.stream()
                .map(COA::getId)
                .toList();

        if (!coaIds.isEmpty()) {
            bulkAssignToOrganisation(toOrganisation, coaIds);
        }
    }

    public boolean hasAssignedCOA(Organisation organisation) {
        List<COA> assignedCOAs = getCOAByOrganisation(organisation);
        return !assignedCOAs.isEmpty();
    }

    public int getAssignedCOACount(Organisation organisation) {
        return getCOAByOrganisation(organisation).size();
    }

    // ===================================================================
    // HIERARCHY VALIDATION
    // ===================================================================
    public boolean isValidHierarchy(COA coa) {
        // Basic hierarchy validation
        if (coa.getCoalevel11() != null && coa.getCoalevel1() == null) {
            return false; // Level 11 requires Level 1
        }
        if (coa.getCoalevel12() != null && coa.getCoalevel11() == null) {
            return false; // Level 12 requires Level 11
        }
        if (coa.getCoalevel13() != null && coa.getCoalevel12() == null) {
            return false; // Level 13 requires Level 12
        }

        return true;
    }

    public List<COA> getCOAWithInvalidHierarchy() {
        return getAllCOA().stream()
                .filter(coa -> !isValidHierarchy(coa))
                .toList();
    }

    public Set<String> extractCodes(Set<COA> coaSet) {
        if (coaSet == null || coaSet.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return coaSet.stream()
                .map(COA::getCode) // Extract code
                .filter(Objects::nonNull) // Remove null codes
                .map(String::trim) // Trim whitespace
                .collect(Collectors.toSet());
    }

}
