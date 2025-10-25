package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.Quarters;
import com.methaltech.application.data.bgtool.repository.BudgetItemsRepository;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel13;
import com.methaltech.application.data.entity.bgtool.Currency;
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
import com.methaltech.application.data.entity.bgtool.BudgetSummary;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.RevenueSource;
import com.methaltech.application.data.entity.bgtool.SectionBudget;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.data.entity.livedata.UrcDeptSectionAnlDim;
import com.methaltech.application.data.livedata.repository.UrcDepartmentAnlDimRepository;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.data.livedata.service.UrcDeptSectionAnlDimService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
    private final UrcDepartmentAnlDimRepository urcDepartmentAnlDimRepository;
    private final BudgetItemsService budgetItemsService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final UrcDeptSectionAnlDimService sampleSectionService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final Random random = new Random();

    private final String[] colors = {
        "#3B82F6", "#10B981", "#F59E0B", "#EF4444", "#8B5CF6",
        "#06B6D4", "#84CC16", "#F97316", "#EC4899", "#6366F1"
    };
    private final DeptSectionMergerService deptSectionMergerService;

    @Autowired
    public BudgetService(BudgetRepository repository, Coalevel1Repository coalevel1Repository,
            Coalevel11Repository coalevel11Repository, Coalevel12Repository coalevel12Repository,
            Coalevel13Repository coalevel13Repository, CurrencyRepository currencyRepository,
            OrganisationRepository organisationRepository, DepartmentRepository departmentRepository,
            SectionRepository sectionRepository, UnitRepository unitRepository,
            CoaRepository coaRepository, FundsourceRepository fundsourceRepository,
            UrcDepartmentAnlDimRepository urcDepartmentAnlDimRepository,
            BudgetItemsService budgetItemsService, DeptSectionMergerService deptSectionMergerService,
            DeptSectionMergerService sampleDeptSectionMergerService, UrcDeptSectionAnlDimService sampleSectionService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, SALFLDGService sampleSALFLDGService
    ) {
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
        this.urcDepartmentAnlDimRepository = urcDepartmentAnlDimRepository;
        this.budgetItemsService = budgetItemsService;
        this.deptSectionMergerService = deptSectionMergerService;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleSectionService = sampleSectionService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
    }

    public Optional<Budget> get(Long id) {
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

    public void delete(Long id) {
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
        List<Organisation> organizationList = organisationRepository.findByBudgetWithCoaAccounts(sourceBudget);

        // Loop through each Organization entity and copy its data to the target financial year
        for (Organisation sourceOrganization : organizationList) {
            // Create a new Organization entity for the target financial year and copy the common attributes
            Organisation targetOrganization = new Organisation();
            targetOrganization.setName(sourceOrganization.getName());
            targetOrganization.setBudget(targetBudget);
            targetOrganization.setCode(generateBudgetTypeCode());

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
            //nfs.setCode(generateFundSourceCode(targetBudget));
            nfs.setCode(generateFundSourceCode());
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

    private String generateBudgetTypeCode() {
        // Assuming you have a way to retrieve the last code used in your database
        // For demonstration purposes, let's assume it's stored in a variable called lastCode
        // You can replace it with your actual logic to fetch the last used code
        String lastCode = ""; // Replace this with actaul logic to retrieve last used code

        //Organisation org = sampleOrganisationService.getLastSavedOrganisationByBudget(sampleBudget);
        Organisation org = organisationRepository.findTopByOrderByIdDesc();

        // If lastCode is null or empty, start with ZBT01
        if (org == null) {
            return "ZBT01";
        }
        lastCode = org.getCode();
        // Extract the numeric part of the last code
        String numericPart = lastCode.substring(3); // Assuming "ZBT" is always the prefix
        int index = Integer.parseInt(numericPart);

        // Increment index and format it with leading zeros
        index++;
        String newIndex = String.format("%02d", index);

        // Construct the new code
        return "ZBT" + newIndex;
    }

    private String generateFundSourceCode() {
        // Assuming you have a way to retrieve the last code used in your database
        // For demonstration purposes, let's assume it's stored in a variable called lastCode
        // You can replace it with your actual logic to fetch the last used code
        String lastCode = ""; // Replace this with actaul logic to retrieve last used code

        //Fundsource org = fundsourceService.getLastSavedFundsourceByBudget(sampleBudget);
        Fundsource org = fundsourceRepository.findTopByOrderByIdDesc();

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

    public BudgetSummary getBudgetSummary(Budget budget) {
        List<DepartmentBudget> departmentBudgets = getDepartmentBudgets(budget);
        List<RevenueSource> revenueSources = createMockRevenueSources(budget);

        double totalBudget = departmentBudgets.stream().mapToDouble(DepartmentBudget::getTotalBudget).sum();
        double totalCommitted = departmentBudgets.stream().mapToDouble(DepartmentBudget::getTotalCommitted).sum();
        double totalSpent = departmentBudgets.stream().mapToDouble(DepartmentBudget::getTotalSpent).sum();
        double totalRevenue = revenueSources.stream().mapToDouble(RevenueSource::getAmount).sum();
        double projectedRevenue = revenueSources.stream().mapToDouble(RevenueSource::getProjected).sum();

        return new BudgetSummary(
                totalBudget,
                totalCommitted,
                totalSpent,
                totalRevenue,
                projectedRevenue,
                departmentBudgets,
                revenueSources
        );
    }

    public List<DepartmentBudget> getDepartmentBudgets(Budget budget) {
        List<UrcDepartmentAnlDim> departments = findActiveDepartments();
        Random random = new Random();

        return departments.stream().map(dept -> {
            // Generate mock budget data based on department
            Set<UrcDeptSectionAnlDimbgt> sections = deptSectionMergerService.getSectionsByDeptCode(dept.getANL_CODE());

            double baseBudget = 50_000_000 + random.nextDouble() * 450_000_000; // 50M to 500M UGX
            baseBudget = budgetItemsService.calculateTotalDeptExpenditure(budget, sections.stream().toList()).doubleValue();

            double spentPercentage = 0.3 + random.nextDouble() * 0.6; // 30% to 90%
            double totalSpent = baseBudget * spentPercentage;
            double totalCommitted = baseBudget * (0.05 + random.nextDouble() * 0.15); // 5% to 20%
            totalCommitted = 0.0;
            BigDecimal[] totals = budgetItemsService.computeGrandExpenditureTotals(budget, sections);

            Set<String> sects = new HashSet<>();

            if (dept.getANL_CODE() != null && !dept.getANL_CODE().isEmpty()) {
                Optional<DeptSectionMerger> merger = sampleDeptSectionMergerService.findByDeptcodeCustom(dept.getANL_CODE());
                if (merger.isPresent()) {
                    DeptSectionMerger deptSectionMerger = merger.get();
                    sects = deptSectionMerger.getSectioncodes();

                }
            }
            String color = colors[Math.abs(dept.getANL_CODE().hashCode()) % colors.length];
            int sectionCount = sects.size();// 1 + random.nextInt(5); // 1 to 5 sections
            totalSpent = sampleSALFLDGService.getTotalAmountByPeriods(getFinancialYearPeriods(budget), sects).negate().doubleValue();
            totalCommitted = sampleSALFLDGService.getTotalCommittedAmountByPeriods(getFinancialYearPeriods(budget), sects).negate().doubleValue();

            return new DepartmentBudget(
                    dept.getANL_CODE(),
                    dept.getNAME(),
                    dept.getANL_CAT_ID(),
                    baseBudget,
                    totalSpent,
                    totalCommitted,
                    color,
                    false,
                    false,
                    false,
                    dept.getSTATUS(),
                    sectionCount
            );
        }).collect(Collectors.toList());
    }

    public List<SectionBudget> getDepartmentSections(String departmentCode,Budget budget) {
        // Find the department first to get its category
        UrcDepartmentAnlDim department = urcDepartmentAnlDimRepository.findByANL_CODE(departmentCode);
        List<UrcDeptSectionAnlDimbgt> sections = new ArrayList<>();
        if (department == null) {
            return new ArrayList<>();
        }

        if (department != null) {
            String anlCode = department.getANL_CODE();
            if (anlCode != null && !anlCode.isEmpty()) {
                Optional<DeptSectionMerger> merger = sampleDeptSectionMergerService.findByDeptcodeCustom(anlCode);
                if (merger.isPresent()) {
                    DeptSectionMerger deptSectionMerger = merger.get();
                    Set<String> sects = deptSectionMerger.getSectioncodes();
                    for (String result : sects) {
                        UrcDeptSectionAnlDimbgt section = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE(result);
                        if (section != null) {
                            sections.add(section);
                        }
                    }
                }
            }
        }

        Random random = new Random();

        return sections.stream().map(section -> {
            // Generate mock budget data for each section
            double baseBudget = 20_000_000 + random.nextDouble() * 180_000_000; // 20M to 200M UGX
            double spentPercentage = 0.2 + random.nextDouble() * 0.7; // 20% to 90%
            double spentAmount = baseBudget * spentPercentage;
            double committedPercentage = 0.0; // 5% to 20%
            double committedAmount = 0.0;
            baseBudget = budgetItemsService.calculateTotalDeptExpenditure2(budget, section).doubleValue();  
            spentAmount = sampleSALFLDGService.getTotalAmountByPeriods2(getFinancialYearPeriods(budget), section.getANL_CODE()).negate().doubleValue();
            SectionBudget sectionBudget = new SectionBudget(section, baseBudget, spentAmount, committedAmount);
            sectionBudget.setDepartmentCode(departmentCode);
            sectionBudget.setDescription(generateSectionDescription(section.getNAME()));

            return sectionBudget;
        }).collect(Collectors.toList());
    }

    public SectionBudget getSectionBudget(String sectionCode) {
        UrcDeptSectionAnlDimbgt section = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE(sectionCode);
        if (section == null) {
            return null;
        }

        Random random = new Random();
        double baseBudget = 50_000_000 + random.nextDouble() * 150_000_000;
        double spentAmount = baseBudget * (0.3 + random.nextDouble() * 0.5);
        double committedAmount = baseBudget * (0.05 + random.nextDouble() * 0.15);

        SectionBudget sectionBudget = new SectionBudget(section, baseBudget, spentAmount, committedAmount);
        sectionBudget.setDescription(generateSectionDescription(section.getNAME()));

        return sectionBudget;
    }

    /*    public List<UrcDeptSectionAnlDimbgt> getSectionsByCategory(String categoryId) {
    return sectionRepository.findByCategoryId(categoryId);
    }
    
    public Long getSectionCountByCategory(String categoryId) {
    return sectionRepository.countByCategoryId(categoryId);
    }*/
    private String generateSectionDescription(String sectionName) {
        // Generate appropriate descriptions based on section name
        String name = sectionName.toLowerCase();

        if (name.contains("admin") || name.contains("administration")) {
            return "Administrative functions and support services";
        } else if (name.contains("operation") || name.contains("ops")) {
            return "Day-to-day operational activities and processes";
        } else if (name.contains("project") || name.contains("development")) {
            return "Special projects and development initiatives";
        } else if (name.contains("maintenance") || name.contains("support")) {
            return "Maintenance and technical support services";
        } else if (name.contains("training") || name.contains("capacity")) {
            return "Training and capacity building programs";
        } else if (name.contains("procurement") || name.contains("supply")) {
            return "Procurement and supply chain management";
        } else if (name.contains("research") || name.contains("innovation")) {
            return "Research and innovation activities";
        } else if (name.contains("quality") || name.contains("assurance")) {
            return "Quality assurance and control processes";
        } else {
            return "Specialized departmental functions and activities";
        }
    }

    /*    public List<UrcDepartmentAnlDim> getActiveDepartments() {
    return departmentRepository.findActiveDepartments();
    }
    
    public List<UrcDepartmentAnlDim> getDepartmentsByCategory(String categoryId) {
    return departmentRepository.findByCategoryId(categoryId);
    }
    
    public List<String> getDepartmentCategories() {
    return departmentRepository.findDistinctCategories();
    }
    
    public List<UrcDepartmentAnlDim> getBudgetControlledDepartments() {
    return departmentRepository.findBudgetControlledDepartments();
    }*/
    private List<RevenueSource> createMockRevenueSources(Budget budget) {

        List<Organisation> revenuesources = new ArrayList();
        List<RevenueSource> revenuesourcesfinal = new ArrayList<>();
        revenuesources = organisationRepository.findByBudgetWithCoaAccounts(budget);
        for (Organisation sour : revenuesources) {
            double totrev = organisationRepository.sumAnnualByBudgetAndOrganisation(budget, sour).doubleValue();
            double totcol = sampleSALFLDGService.findTotalAmountByPeriodsAndAccntCodes(getFinancialYearPeriods(budget), extractCodes(sour.getCoaAccounts())).doubleValue();
            revenuesourcesfinal.add(new RevenueSource(sour.getCode(), sour.getName(), sour.getCode(), totcol, totrev, colors[random.nextInt(colors.length)]));

        }
        return revenuesourcesfinal;
        /*        return Arrays.asList(
        new RevenueSource("rev-1", "Government Grants", "grants", 425_000_000, 450_000_000, "#3B82F6"),
        new RevenueSource("rev-2", "Service Fees", "fees", 340_000_000, 360_000_000, "#10B981"),
        new RevenueSource("rev-3", "Product Sales", "sales", 260_000_000, 290_000_000, "#F59E0B"),
        new RevenueSource("rev-4", "Private Donations", "donations", 190_000_000, 200_000_000, "#EF4444"),
        new RevenueSource("rev-5", "Investment Returns", "investments", 90_000_000, 100_000_000, "#8B5CF6"),
        new RevenueSource("rev-6", "Consulting Services", "other", 35_000_000, 25_000_000, "#06B6D4")
        );*/
    }

    public List<UrcDepartmentAnlDim> findActiveDepartments() {
        //return urcDepartmentAnlDimRepository.findByANL_CODEStartingWithD();
        return urcDepartmentAnlDimRepository.findAll();
    }

    public Optional<Budget> getMostRecurrentBudget() {
        return repository.findTopByOrderByIdDesc();
    }

    public List<String> periods(String fy, Quarters qtr) {
        int[] years = extractYears(fy);
        List<String> periods = new ArrayList<>();

        if (qtr.equals(Quarters.Qtr1)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d007", y), String.format("%d008", y), String.format("%d009", y)));
        } else if (qtr.equals(Quarters.Qtr2)) {
            int y = years[0];
            periods.addAll(Arrays.asList(String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)));
        } else if (qtr.equals(Quarters.Qtr3)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d001", y), String.format("%d002", y), String.format("%d003", y)));
        } else if (qtr.equals(Quarters.Qtr4)) {
            int y = years[1];
            periods.addAll(Arrays.asList(String.format("%d004", y), String.format("%d005", y), String.format("%d006", y)));
        } else if (qtr.equals(Quarters.Total)) {
            for (int y : years) {
                periods.addAll(Arrays.asList(
                        String.format("%d001", y), String.format("%d002", y), String.format("%d003", y),
                        String.format("%d004", y), String.format("%d005", y), String.format("%d006", y),
                        String.format("%d007", y), String.format("%d008", y), String.format("%d009", y),
                        String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)
                ));
            }
        }

        return periods;
    }

    public List<String> previousPeriods(String fy, Quarters qtr) {
        int[] years = extractYears(fy);
        List<String> periods = new ArrayList<>();
        int y = years[1];
        if (qtr.equals(Quarters.Qtr1)) {

            periods.addAll(Arrays.asList(String.format("%d001", y), String.format("%d002", y), String.format("%d003", y)));

        } else if (qtr.equals(Quarters.Qtr2)) {

            periods.addAll(Arrays.asList(String.format("%d004", y), String.format("%d005", y), String.format("%d006", y)));
        } else if (qtr.equals(Quarters.Qtr3)) {

            periods.addAll(Arrays.asList(String.format("%d007", y), String.format("%d008", y), String.format("%d009", y)));
        } else if (qtr.equals(Quarters.Qtr4)) {

            periods.addAll(Arrays.asList(String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)));
        } else if (qtr.equals(Quarters.Total)) {
            for (int x : years) {
                periods.addAll(Arrays.asList(
                        String.format("%d001", y), String.format("%d002", y), String.format("%d003", y),
                        String.format("%d004", y), String.format("%d005", y), String.format("%d006", y),
                        String.format("%d007", y), String.format("%d008", y), String.format("%d009", y),
                        String.format("%d010", y), String.format("%d011", y), String.format("%d012", y)
                ));
            }
        }

        return periods;
    }

    public String getPreviousFy(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        return String.format("FY%d-%d", newfy1, fy1);
    }

    public Budget getPreviousBudget(String fy) {
        int[] years = extractYears(fy);
        int fy1 = years[0];
        int newfy1 = fy1 - 1;
        // return String.format("FY%d-%d", newfy1, fy1);
        Optional<Budget> bug = getByFY(String.format("FY%d-%d", newfy1, fy1));
        if (bug.isPresent()) {
            return bug.get();
        } else {
            return null;
        }
    }

    public int[] extractYears(String input) {
        int[] years = new int[2];
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        int i = 0;
        while (matcher.find() && i < 2) {
            years[i++] = Integer.parseInt(matcher.group());
        }

        return years;
    }

    public UrcDeptSectionAnlDimbgt toDimbgt(UrcDeptSectionAnlDim source) {
        if (source == null) {
            return null;
        }

        UrcDeptSectionAnlDimbgt target = new UrcDeptSectionAnlDimbgt();

        // Truncate or validate as per DB column sizes
        target.setANL_CODE(truncate(source.getANL_CODE(), 15));
        target.setANL_CAT_ID(truncate(source.getANL_CAT_ID(), 5));
        target.setNAME(source.getNAME());

        return target;
    }

    private static String truncate(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        return input.length() <= maxLength ? input : input.substring(0, maxLength);
    }

    public Set<Integer> getFinancialYearPeriods(Budget budget) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        for (int i = 1; i <= 12; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
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
