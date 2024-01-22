package com.methaltech.application.data.bgtool.service;


import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Department;
import com.methaltech.application.data.entity.bgtool.Section;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel12;
import com.methaltech.application.data.entity.bgtool.Coalevel13;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.bgtool.repository.BudgetRepository;
import com.methaltech.application.data.bgtool.repository.CoaRepository;
import com.methaltech.application.data.bgtool.repository.Coalevel11Repository;
import com.methaltech.application.data.bgtool.repository.Coalevel12Repository;
import com.methaltech.application.data.bgtool.repository.Coalevel13Repository;
import com.methaltech.application.data.bgtool.repository.Coalevel1Repository;
import com.methaltech.application.data.bgtool.repository.CurrencyRepository;
import com.methaltech.application.data.bgtool.repository.DepartmentRepository;
import com.methaltech.application.data.bgtool.repository.OrganisationRepository;
import com.methaltech.application.data.bgtool.repository.SectionRepository;
import com.methaltech.application.data.bgtool.repository.UnitRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository repository;
    private final Coalevel1Repository coalevel1Repository;
    private final Coalevel11Repository coalevel11Repository;
    private final Coalevel12Repository coalevel12Repository;
    private final Coalevel13Repository coalevel13Repository;
    private final CurrencyRepository currencyRepository;
    private final OrganisationRepository organisationRepository;
    private final DepartmentRepository departmentRepository;
    private final SectionRepository sectionRepository;
    private final UnitRepository unitRepository;
    private final CoaRepository coaRepository;

    @Autowired
    public BudgetService(BudgetRepository repository, Coalevel1Repository coalevel1Repository,
            Coalevel11Repository coalevel11Repository, Coalevel12Repository coalevel12Repository,
            Coalevel13Repository coalevel13Repository, CurrencyRepository currencyRepository,
            OrganisationRepository organisationRepository, DepartmentRepository departmentRepository,
            SectionRepository sectionRepository, UnitRepository unitRepository,
            CoaRepository coaRepository) {
        this.repository = repository;
        this.coalevel1Repository = coalevel1Repository;
        this.coalevel11Repository = coalevel11Repository;
        this.coalevel12Repository = coalevel12Repository;
        this.coalevel13Repository = coalevel13Repository;
        this.currencyRepository = currencyRepository;
        this.organisationRepository = organisationRepository;
        this.departmentRepository = departmentRepository;
        this.sectionRepository = sectionRepository;
        this.unitRepository = unitRepository;
        this.coaRepository = coaRepository;

    }

    public Optional<Budget> get(Integer id) {
        //UUID myUUID = intToUUID(id);
        return repository.findById(id);
    }
    public Optional<Budget> getByFY(String fy) {
        //UUID myUUID = intToUUID(id);
        return repository.findByFY(fy);
    }    
    public static UUID intToUUID(int value) {
        // Convert the integer to a long to prevent data loss for negative integers
        long longValue = value & 0xFFFFFFFFL;
        
        // Create a UUID from the long value (treat it as the most significant bits)
        return new UUID(longValue << 32, 0L);
    }    

    public boolean getBudget(String fy) {
        return repository.findByFinancialYear(fy) != null;

    }

    public Budget update(Budget entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<Budget> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public List<Budget> getBudgets() {
        return repository.findAll();
    }    
    public List<Budget> getLastSavedBudget() {
        return repository.findLastSavedBudget();
    }
    public int count() {
        return (int) repository.count();
    }

    public List<Budget> findAllExcept(String financialYear) {
        return repository.findAllByFinancialYearNot(financialYear);
    }

    /*    public Budget getLastSavedBudget() {
    return repository.findLastSavedBudget();
    }*/

    public void savenewBudget(Budget sourceBudget, Budget targetBudget) {
        repository.save(targetBudget);
        copyDataToNewFinancialYear(sourceBudget, targetBudget);

    }

    public void copyDataToNewFinancialYear(Budget sourceBudget, Budget targetBudget) {
        // Retrieve all Organization entities for the source financial year
        List<Organisation> organizationList = organisationRepository.findByBudget(sourceBudget);

        // Loop through each Organization entity and copy its data to the target financial year
        for (Organisation sourceOrganization : organizationList) {
            // Create a new Organization entity for the target financial year and copy the common attributes
            Organisation targetOrganization = new Organisation();
            targetOrganization.setName(sourceOrganization.getName());
            targetOrganization.setBudget(targetBudget);

            // Save the new Organization entity to the target database
            organisationRepository.save(targetOrganization);
        }
        // Retrieve all Department entities for the source Organization entity
        List<Department> departmentList = departmentRepository.findAll();

        // Loop through each Department entity and copy its data to the target Organization entity
        for (Department sourceDepartment : departmentList) {
            // Create a new Department entity for the target Organization entity and copy the common attributes
            Department targetDepartment = new Department();
            targetDepartment.setDepartment(sourceDepartment.getDepartment());
            // targetDepartment.setOrganisation(targetOrganization);

            // Save the new Department entity to the target database
            departmentRepository.save(targetDepartment);

            // Retrieve all Section entities for the source Department entity
            List<Section> sectionList = sectionRepository.findByDepartment(sourceDepartment);

            // Loop through each Section entity and copy its data to the target Department entity
            for (Section sourceSection : sectionList) {
                // Create a new Section entity for the target Department entity and copy the common attributes
                Section targetSection = new Section();
                targetSection.setSection(sourceSection.getSection());
                targetSection.setDepartment(targetDepartment);

                // Save the new Section entity to the target database
                sectionRepository.save(targetSection);

                // Retrieve all D_Unit entities for the source Section entity
                List<D_Unit> dUnitList = unitRepository.findBySection(sourceSection);

                // Loop through each D_Unit entity and copy its data to the target Section entity
                for (D_Unit sourceDUnit : dUnitList) {
                    // Create a new D_Unit entity for the target Section entity and copy the common attributes
                    D_Unit targetDUnit = new D_Unit();
                    targetDUnit.setUnit(sourceDUnit.getUnit());
                    targetDUnit.setSection(targetSection);

                    // Save the new D_Unit entity to the target database
                    unitRepository.save(targetDUnit);
                }
            }

        }

        List<Currency> findCurrencyByBudget = currencyRepository.findByBudget(sourceBudget);
        for (Currency currency : findCurrencyByBudget) {
            Currency targetCurrency = new Currency();
            targetCurrency.setBudget(targetBudget);
            targetCurrency.setEnabled(currency.isEnabled());
            targetCurrency.setBudget(targetBudget);
            targetCurrency.setRate(currency.getRate());
            currencyRepository.save(targetCurrency);

        }
        // Retrieve all Coalevel1 entities for the source financial year
        List<Coalevel1> coalevel1List = coalevel1Repository.findAll();
        // Loop through each Coalevel1 entity and copy its data to the target financial year
        for (Coalevel1 sourceCoalevel1 : coalevel1List) {
            // Create a new Coalevel1 entity for the target financial year and copy the common attributes
            Coalevel1 targetCoalevel1 = new Coalevel1();
            targetCoalevel1.setName(sourceCoalevel1.getName());
            //targetCoalevel1.setBudget(targetBudget);

            // Save the new Coalevel1 entity to the target database
            coalevel1Repository.save(targetCoalevel1);

            // Retrieve all Coalevel12 entities for the source Coalevel1 entity
            List<Coalevel12> coalevel12List = coalevel12Repository.findByCoalevel1(sourceCoalevel1);
            for (Coalevel12 sourceCoalevel12 : coalevel12List) {
                Coalevel12 targetCoalevel12 = new Coalevel12();
                targetCoalevel12.setName(sourceCoalevel12.getName());
                targetCoalevel12.setCoalevel1(targetCoalevel1);

                // Save the new Coalevel11 entity to the target database
                coalevel12Repository.save(targetCoalevel12);
            }

            // Retrieve all Coalevel11 entities for the source Coalevel1 entity
            List<Coalevel11> coalevel11List = coalevel11Repository.findByCoalevel1(sourceCoalevel1);

            // Loop through each Coalevel11 entity and copy its data to the target Coalevel1 entity
            for (Coalevel11 sourceCoalevel11 : coalevel11List) {
                // Create a new Coalevel11 entity for the target Coalevel1 entity and copy the common attributes
                Coalevel11 targetCoalevel11 = new Coalevel11();
                targetCoalevel11.setName(sourceCoalevel11.getName());
                targetCoalevel11.setCoalevel1(targetCoalevel1);

                // Save the new Coalevel11 entity to the target database
                coalevel11Repository.save(targetCoalevel11);

                // Retrieve all Coalevel13 entities for the source Coalevel11 entity
                List<Coalevel13> coalevel13List = coalevel13Repository.findByCoalevel11(sourceCoalevel11);

                // Loop through each Coalevel13 entity and copy its data to the target Coalevel11 entity
                for (Coalevel13 sourceCoalevel13 : coalevel13List) {
                    // Create a new Coalevel13 entity for the target Coalevel11 entity and copy the common attributes
                    Coalevel13 targetCoalevel13 = new Coalevel13();
                    targetCoalevel13.setName(sourceCoalevel13.getName());
                    targetCoalevel13.setCoalevel11(targetCoalevel11);

                    // Save the new Coalevel13 entity to the target database
                    coalevel13Repository.save(targetCoalevel13);
                }
            }
        }
        saveFromPreviousBudget(sourceBudget, targetBudget);
    }

    public int saveFromPreviousBudget(Budget oldBudget, Budget newBudget) {
        List<COA> coaList = coaRepository.findByBudget(oldBudget);
        int num = coaList.size();

        // Loop through each COA entity and copy its data to the target Coalevel12 entity
        for (COA sourceCOA : coaList) {
            // Create a new COA entity for the target Coalevel12 entity and copy the common attributes
            COA targetCOA = new COA();
            targetCOA.setCode(sourceCOA.getCode());
            targetCOA.setName(sourceCOA.getName());
            targetCOA.setBudget(newBudget);
            Coalevel1 targetCoalevell = coalevel1Repository.findByNameAndBudget(sourceCOA.getCoalevel1().getName());
            targetCOA.setCoalevel1(targetCoalevell);
            targetCOA.setCoalevel11(
                    Optional.ofNullable(sourceCOA.getCoalevel11())
                            .map(coa11 -> coalevel11Repository.findByCoalevel1AndName(targetCoalevell, coa11.getName()))
                            .orElse(null));
            targetCOA.setCoalevel12(
                    Optional.ofNullable(sourceCOA.getCoalevel12())
                            .map(coa12 -> coalevel12Repository.findByCoalevel1AndName(targetCoalevell, coa12.getName()))
                            .orElse(null));
            if (sourceCOA.getCoalevel13() != null) {
                Coalevel13 coalevel13 = coalevel13Repository.findByCoalevel11AndName(targetCOA.getCoalevel11(), sourceCOA.getCoalevel13().getName());
                targetCOA.setCoalevel13(coalevel13);
            }
            Set<Section> unitnew = new HashSet<>();
            for (Section units : sourceCOA.getDsections()) {
                //D_Unit unit = unitRepository.findByBudgetAndUnit(newBudget, units.getUnit());
               // unitnew.add(unit);

            }
            targetCOA.setDsections(unitnew);
            num++;

            // Save the new COA entity to the target database
            coaRepository.save(targetCOA);
        }
        return num;
    }

}
