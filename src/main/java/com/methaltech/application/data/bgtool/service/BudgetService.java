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
import com.methaltech.application.data.bgtool.repository.FundsourceRepository;
import com.methaltech.application.data.bgtool.repository.OrganisationRepository;
import com.methaltech.application.data.bgtool.repository.SectionRepository;
import com.methaltech.application.data.bgtool.repository.UnitRepository;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
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
    private final FundsourceRepository fundsourceRepository;

    @Autowired
    public BudgetService(BudgetRepository repository, Coalevel1Repository coalevel1Repository,
            Coalevel11Repository coalevel11Repository, Coalevel12Repository coalevel12Repository,
            Coalevel13Repository coalevel13Repository, CurrencyRepository currencyRepository,
            OrganisationRepository organisationRepository, DepartmentRepository departmentRepository,
            SectionRepository sectionRepository, UnitRepository unitRepository,
            CoaRepository coaRepository, FundsourceRepository fundsourceRepository) {
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
        this.fundsourceRepository = fundsourceRepository;

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
            targetOrganization.setCode(sourceOrganization.getCode());

            // Save the new Organization entity to the target database
            organisationRepository.save(targetOrganization);
        }

        List<Currency> findCurrencyByBudget = currencyRepository.findByBudget(sourceBudget);
        for (Currency currency : findCurrencyByBudget) {
            Currency targetCurrency = new Currency();
            targetCurrency.setBudget(targetBudget);
            targetCurrency.setEnabled(currency.isEnabled());
            targetCurrency.setBudget(targetBudget);
            targetCurrency.setRate(currency.getRate());
            targetCurrency.setData(currency.getData());
            currencyRepository.save(targetCurrency);

        }

        List<Fundsource> fs = fundsourceRepository.findByBudget(sourceBudget);

        for (Fundsource f : fs) {
            Fundsource nfs = new Fundsource();
            nfs.setBudget(targetBudget);
            nfs.setFundsource(f.getFundsource());
            nfs.setCode(generateFundSourceCode(targetBudget));
            fundsourceRepository.save(nfs);
        }

        saveFromPreviousBudget(sourceBudget, targetBudget);
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

    public Optional<Budget> getLastSavedBudget2() {
        return repository.findTopByOrderByIdDesc();
    }

    private String generateFundSourceCode(Budget budget) {
        // Assuming you have a way to retrieve the last code used in your database
        // For demonstration purposes, let's assume it's stored in a variable called lastCode
        // You can replace it with your actual logic to fetch the last used code
        String lastCode = ""; // Replace this with actaul logic to retrieve last used code

        Fundsource org = fundsourceRepository.findTopByBudgetOrderByIdDesc(budget);
        // If lastCode is null or empty, start with ZBT01
        if (org == null) {
            return "ZBFS01";
        }
        lastCode = org.getCode();
        // Extract the numeric part of the last code
        String numericPart = lastCode.substring(4); // Assuming "ZBT" is always the prefix
        int index = Integer.parseInt(numericPart);

        // Increment index and format it with leading zeros
        index++;
        String newIndex = String.format("%02d", index);

        // Construct the new code
        return "ZBFS" + newIndex;
    }
}
