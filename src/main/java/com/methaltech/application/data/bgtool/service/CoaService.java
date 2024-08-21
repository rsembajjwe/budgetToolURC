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

    public List<COA> findByCode(String code) {
        return coaRepository.findByCode(code);
    }

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

    public boolean existsByCode(String code) {
        return coaRepository.existsByCode(code);
    }

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
}
