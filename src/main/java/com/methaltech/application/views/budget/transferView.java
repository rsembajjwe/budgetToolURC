package com.methaltech.application.views.budget;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.COAReconcileService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CurrencyDataService;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.DataDuplicationService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.SectionDeptUnitMergerService;
import com.methaltech.application.data.bgtool.service.StaffSalaryService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.COAReconcile;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.SectionDeptUnitMerger;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.StockUnitMeasure;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.oldbgtool.OldBudget;
import com.methaltech.application.data.entity.oldbgtool.OldBudgetSubItem;
import com.methaltech.application.data.entity.oldbgtool.OldDeptSection;
import com.methaltech.application.data.entity.oldbgtool.OldDeptUnit;
import com.methaltech.application.data.entity.oldbgtool.OldItem;
import com.methaltech.application.data.entity.oldbgtool.OldProgActivity;
import com.methaltech.application.data.entity.oldbgtool.OldProgramme;
import com.methaltech.application.data.entity.livedata.UR5_ACNT;
import com.methaltech.application.data.entity.oldbgtool.Staff;
import com.methaltech.application.data.oldbgtool.service.OldBudgetService;
import com.methaltech.application.data.oldbgtool.service.OldBudgetSubItemService;
import com.methaltech.application.data.oldbgtool.service.OldDeptSectionService;
import com.methaltech.application.data.oldbgtool.service.OldDeptUnitService;
import com.methaltech.application.data.oldbgtool.service.OldItemService;
import com.methaltech.application.data.oldbgtool.service.OldProgActivityService;
import com.methaltech.application.data.oldbgtool.service.OldProgrammeService;
import com.methaltech.application.data.livedata.service.UR5_ACNTService;
import com.methaltech.application.data.oldbgtool.service.StaffService;
import com.methaltech.application.data.salaryScale;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.RolesAllowed;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Route;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@PageTitle("Transfer Budget")
@Route(value = "transfer", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class transferView extends Div {
    
    private ComboBox<OldBudget> oldBudgetBox = new ComboBox<>("Budget");
    private final OldBudgetService oldbudgetRepository;
    private final StaffService staffService;
    private final StaffSalaryService staffSalaryService;
    private final OldProgrammeService oldProgrammeService;
    private final URC_Priority_AreasService uRC_Priority_AreasService;
    private final OldProgActivityService oldProgActivityService;
    private final OldDeptSectionService oldDeptSectionService;
    private final UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService;
    private final Urc_ActivitiesService urc_ActivitiesService;
    private final BudgetService sampleBudgetService;
    private final OldBudgetSubItemService oldBudgetSubItemService;
    private final OldDeptUnitService oldDeptUnitService;
    private final SectionDeptUnitMergerService sectionDeptUnitMergerService;
    private final COAReconcileService coaReconcileService;
    private final OrganisationService organisationService;
    private final CurrencyService currencyService;
    private final CurrencyDataService currencyDataService;
    private final StockUnitMeasureService stockUnitMeasureService;
    private final OldItemService oldItemService;
    private final CoaService coaService;
    private final BudgetItemsService budgetItemsService;
    private final Coalevel1Service sampleCoalevel1Service;
    private final UR5_ACNTService sampleUR5_ACNTService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final DataDuplicationService sampleDataDuplicationService;
    Button importBgt = new Button("Import Budget Settings");
    Button check = new Button("Check");
    Button check2 = new Button("Import Budget Items");
    Button check3 = new Button("Sample");
    Button staff = new Button("Import staff");
    NumberField gfiled = new NumberField();
    
    public transferView(OldBudgetService oldbudgetRepository, BudgetService sampleBudgetService,
            OldProgrammeService oldProgrammeService, URC_Priority_AreasService uRC_Priority_AreasService,
            OldProgActivityService oldProgActivityService, OldDeptSectionService oldDeptSectionService,
            UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService, Urc_ActivitiesService urc_ActivitiesService,
            OldBudgetSubItemService oldBudgetSubItemService, OldDeptUnitService oldDeptUnitService,
            SectionDeptUnitMergerService sectionDeptUnitMergerService, COAReconcileService coaReconcileService,
            OrganisationService organisationService, CurrencyService currencyService,
            StockUnitMeasureService stockUnitMeasureService, OldItemService oldItemService,
            CoaService coaService, BudgetItemsService budgetItemsService, CurrencyDataService currencyDataService,
            Coalevel1Service sampleCoalevel1Service, UR5_ACNTService sampleUR5_ACNTService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, DataDuplicationService sampleDataDuplicationService,
            StaffService staffService, StaffSalaryService staffSalaryService) {
        this.oldbudgetRepository = oldbudgetRepository;
        this.sampleBudgetService = sampleBudgetService;
        this.oldProgrammeService = oldProgrammeService;
        this.uRC_Priority_AreasService = uRC_Priority_AreasService;
        this.oldProgActivityService = oldProgActivityService;
        this.oldDeptSectionService = oldDeptSectionService;
        this.urcDeptSectionAnlDimbgtService = urcDeptSectionAnlDimbgtService;
        this.urc_ActivitiesService = urc_ActivitiesService;
        this.oldBudgetSubItemService = oldBudgetSubItemService;
        this.oldDeptUnitService = oldDeptUnitService;
        this.sectionDeptUnitMergerService = sectionDeptUnitMergerService;
        this.coaReconcileService = coaReconcileService;
        this.organisationService = organisationService;
        this.currencyService = currencyService;
        this.stockUnitMeasureService = stockUnitMeasureService;
        this.oldItemService = oldItemService;
        this.coaService = coaService;
        this.budgetItemsService = budgetItemsService;
        this.currencyDataService = currencyDataService;
        this.sampleCoalevel1Service = sampleCoalevel1Service;
        this.sampleUR5_ACNTService = sampleUR5_ACNTService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleDataDuplicationService = sampleDataDuplicationService;
        this.staffService = staffService;
        this.staffSalaryService = staffSalaryService;
        
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        upload.setMaxFileSize(10 * 1024 * 1024); // 10 MB   
        upload.addSucceededListener(event -> {
            try {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream(fileName);
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

                for (Row row : sheet) {
                    Cell cell1 = row.getCell(0);
                    Cell cell2 = row.getCell(1);
                    
                    if (cell1 != null && cell2 != null) {
                        String oldcoa;
                        if (cell1.getCellType() == CellType.STRING) {
                            oldcoa = cell1.getStringCellValue();
                        } else if (cell1.getCellType() == CellType.NUMERIC) {
                            oldcoa = String.valueOf((int) cell1.getNumericCellValue());
                        } else {
                            // Handle other cell types if needed
                            oldcoa = ""; // Or set it to an appropriate default value
                        }
                        
                        String newcoa;
                        if (cell2.getCellType() == CellType.STRING) {
                            newcoa = cell2.getStringCellValue();
                        } else if (cell2.getCellType() == CellType.NUMERIC) {
                            newcoa = String.valueOf((int) cell2.getNumericCellValue());
                        } else {
                            // Handle other cell types if needed
                            newcoa = ""; // Or set it to an appropriate default value
                        }
                        
                        COAReconcile coaReconcile = new COAReconcile();
                        coaReconcile.setOldcoa(oldcoa);
                        coaReconcile.setNewcoa(newcoa);
                        
                        coaReconcileService.createCOAReconcile(coaReconcile);
                    }
                }
                
                Notification.show("Data uploaded successfully!", 3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Error uploading data: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        
        oldBudgetBox.setItems(oldbudgetRepository.getAllBudgets());
        oldBudgetBox.setItemLabelGenerator(OldBudget::getFinancialYear);
        add(oldBudgetBox, importBgt, upload, check, check2, check3, staff);
        
        staff.addClickListener(e -> {
            if (!oldBudgetBox.isEmpty()) {
                OldBudget bugt = oldBudgetBox.getValue();
                List<Staff> staffs = staffService.getStaffByFinancialYear(bugt.getFinancialYear());
                
                if (!staffs.isEmpty()) {
                    Budget bgt = sampleBudgetService.getByFY(bugt.getFinancialYear()).get();
                    COA salary = coaService.findByCodeAndBudget("211101", sampleBudgetService.getByFY(bugt.getFinancialYear()).get());
                    
                    COA nssf = coaService.findByCodeAndBudget("212101", sampleBudgetService.getByFY(bugt.getFinancialYear()).get());
                    
                    COA gratuity = coaService.findByCodeAndBudget("213004", sampleBudgetService.getByFY(bugt.getFinancialYear()).get());
                    
                    COA workmanscompensation = coaService.findByCodeAndBudget("213005", sampleBudgetService.getByFY(bugt.getFinancialYear()).get());
                    Currency cur = currencyService.findCurrenciesByCurrencyShortAndBudget("UGX", bgt);
                    
                    BudgetItems bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    
                    if (budgetItemsService.isItemWithCoacodeExists(nssf, bgt) == true) {
                        
                        budgetItemsService.deleteByCoacode(nssf, bgt);
                        
                    }
                    if (budgetItemsService.isItemWithCoacodeExists(gratuity, bgt) == true) {
                        budgetItemsService.deleteByCoacode(gratuity, bgt);
                    }
                    if (budgetItemsService.isItemWithCoacodeExists(workmanscompensation, bgt) == true) {
                        
                        budgetItemsService.deleteByCoacode(workmanscompensation, bgt);
                        
                    }
                    if (budgetItemsService.isItemWithCoacodeExists(salary, bgt) == true) {
                        
                        budgetItemsService.deleteByCoacode(salary, bgt);
                        
                    }
                    for (Staff st : staffs) {
                        StaffSalary staf = new StaffSalary();
                        staf.setFname(st.getFname());
                        staf.setLname(st.getLname());
                        staf.setAddress(st.getAddress());
                        staf.setAddress2(st.getAddress2());
                        staf.setBudget(sampleBudgetService.getByFY(bugt.getFinancialYear()).get());
                        staf.setCode(st.getCode());
                        staf.setEmail(st.getEmail());
                        staf.setTel(st.getTel());
                        staf.setPosition(st.getPosition());
                        staf.setGrade(GetScale(st.getGrade()));
                        staf.setSalary(st.getSalary());
                        staffSalaryService.saveStaffSalary(staf);
                        
                        BudgetItems salaryItem = new BudgetItems();
                        salaryItem.setItem(staf.getFname() + " " + staf.getLname());
                        salaryItem.setCost(staf.getSalary());
                        salaryItem.setQty(new BigDecimal("12"));
                        
                        salaryItem.setUnitMeasure("MONTH");
                        
                        salaryItem.setCurrency(cur);
                        salaryItem.setBudget(staf.getBudget());
                        salaryItem.setBudgetType(organisationService.findByBudgetList(bgt).get(0));
                        salaryItem.setCoacode(salary);
                        salaryItem.setDeptUnit(sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE("S016"));
                        salaryItem.setAnalcode(staffSalaryService.getLastSavedItem().getId());
                        
                        salaryItem.setJan(staf.getSalary());
                        salaryItem.setFeb(staf.getSalary());
                        salaryItem.setMar(staf.getSalary());
                        salaryItem.setApr(staf.getSalary());
                        salaryItem.setMay(staf.getSalary());
                        salaryItem.setJun(staf.getSalary());
                        salaryItem.setJul(staf.getSalary());
                        salaryItem.setAug(staf.getSalary());
                        salaryItem.setSep(staf.getSalary());
                        salaryItem.setOct(staf.getSalary());
                        salaryItem.setNov(staf.getSalary());
                        salaryItem.setDec(staf.getSalary());                        
                        budgetItemsService.update(salaryItem);
                    }

                    //bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    BudgetItems nssfBudget = new BudgetItems();
                    nssfBudget.setActivity(bug.getActivity());
                    nssfBudget.setCoacode(nssf);
                    nssfBudget.setItem(nssf.getName());
                    nssfBudget.setBudget(bgt);
                    nssfBudget.setBudgetType(bug.getBudgetType());
                    nssfBudget.setCoalevel1(bug.getCoalevel1());
                    nssfBudget.setCurrency(bug.getCurrency());
                    nssfBudget.setDeptUnit(bug.getDeptUnit());
                    nssfBudget.setBcategory(nssf.getCode());
                    nssfBudget.setCost(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setQty(new BigDecimal("12"));
                    nssfBudget.setJul(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setAug(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setSep(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setOct(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setNov(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setDec(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setJan(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setFeb(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setMar(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setApr(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setMay(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    nssfBudget.setJun(staffSalaryService.getTotalMonthSalaryByFinancialYear(bgt));
                    budgetItemsService.update(nssfBudget);

                    //                   bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    BudgetItems gratuityBudget = new BudgetItems();
                    gratuityBudget.setActivity(bug.getActivity());
                    gratuityBudget.setCoacode(gratuity);
                    gratuityBudget.setItem(gratuity.getName());
                    gratuityBudget.setBudget(bgt);
                    gratuityBudget.setBudgetType(bug.getBudgetType());
                    gratuityBudget.setCoalevel1(bug.getCoalevel1());
                    gratuityBudget.setCurrency(bug.getCurrency());
                    gratuityBudget.setDeptUnit(bug.getDeptUnit());
                    gratuityBudget.setBcategory(gratuity.getCode());
                    System.out.println(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt) + " Summation");
                    gratuityBudget.setCost(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setQty(new BigDecimal("12"));
                    gratuityBudget.setJul(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setAug(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setSep(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setOct(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setNov(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setDec(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setJan(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setFeb(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setMar(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setApr(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setMay(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    gratuityBudget.setJun(staffSalaryService.getTotalSalaryMonthGratuityByFinancialYear(bgt));
                    budgetItemsService.update(gratuityBudget);

                    //                    bug = budgetItemsService.findFirst1ByCoacodeAndBudgetBudgetItems(salary, bgt);
                    BudgetItems workmanscompensationBudget = new BudgetItems();
                    workmanscompensationBudget.setActivity(bug.getActivity());
                    workmanscompensationBudget.setCoacode(workmanscompensation);
                    workmanscompensationBudget.setItem(workmanscompensation.getName());
                    workmanscompensationBudget.setBudget(bgt);
                    workmanscompensationBudget.setBudgetType(bug.getBudgetType());
                    workmanscompensationBudget.setCoalevel1(bug.getCoalevel1());
                    workmanscompensationBudget.setCurrency(bug.getCurrency());
                    workmanscompensationBudget.setDeptUnit(bug.getDeptUnit());
                    workmanscompensationBudget.setBcategory(workmanscompensation.getCode());
                    workmanscompensationBudget.setCost(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setQty(new BigDecimal("12"));
                    workmanscompensationBudget.setJul(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setAug(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setSep(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setOct(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setNov(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setDec(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setJan(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setFeb(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setMar(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setApr(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setMay(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    workmanscompensationBudget.setJun(staffSalaryService.calculateTotalMonthWKMSalaryByFy(bgt));
                    budgetItemsService.update(workmanscompensationBudget);
                }
            }
            
        });
        importBgt.addSingleClickListener(e -> {
            if (!oldBudgetBox.isEmpty()) {
                importFromOldSystem(oldBudgetBox.getValue());
                //Optional<Budget> findByFinancialYear = sampleBudgetService.getByFY(oldBudgetBox.getValue().getFinancialYear());
                //setSections(findByFinancialYear.get());
            }
            
        });
        check.addSingleClickListener(e -> {
            if (!oldBudgetBox.isEmpty()) {
                Optional<Budget> findByFinancialYear = sampleBudgetService.getByFY(oldBudgetBox.getValue().getFinancialYear());
                /*if (findByFinancialYear.isPresent()) {
                Notification.show(currencyService.findCurrenciesByCurrencyShortAndBudget("UGX", findByFinancialYear.get()).getData().getCurrency());
                }*/
                //setBudgetItems(oldBudgetBox.getValue());
                //setTest(oldBudgetBox.getValue());
            }
            
        });
        check2.addSingleClickListener(e -> {
            if (!oldBudgetBox.isEmpty()) {

                // budgetOnly(oldBudgetBox.getValue());
            }
            
        });
        check3.addClickListener(e -> {
            if (!oldBudgetBox.isEmpty()) {
                
                Optional<Budget> existsBudget = sampleBudgetService.getByFY(oldBudgetBox.getValue().getFinancialYear());
                if (existsBudget.isPresent()) {

                    //Notification.show(existsBudget.get().getFinancialYear());
                    //Notification.show(setCOA(oldBudgetSubItemService.getBudgetSubItemById((long) 203), existsBudget.get()).getCode());
                }
            }
            
        });
    }
    
    public void importFromOldSystem(OldBudget oldbudget) {
        Optional<OldBudget> budgetOptional = oldbudgetRepository.getBudgetByFinancialYear(oldbudget.getFinancialYear());
        
        if (budgetOptional.isPresent()) {
            //OldBudget oldbudget = budgetOptional.get();
            Budget budget = new Budget();
            budget.setStartDate(oldbudget.getOpenDate());
            budget.setCloseDate(oldbudget.getCloseDate());
            budget.setFinancialYear(oldbudget.getFinancialYear());
            if (oldbudget.getStatus().equals("Closed")) {
                budget.setActive(false);
            } else if (oldbudget.getStatus().equals("Open")) {
                budget.setActive(true);
            }
            
            Optional<Budget> existsBudget = sampleBudgetService.getByFY(budget.getFinancialYear());
            if (!existsBudget.isPresent()) {
                sampleBudgetService.update(budget);
                List<Budget> createdBudgetList = sampleBudgetService.getLastSavedBudget();
                //System.out.println(createdBudgetList.get(0).getFinancialYear() + ".................");
                Budget createdBudget = createdBudgetList.get(0);
                if (createdBudget != null) {
                    //Fetch programmes by Budget
                    Organisation org = new Organisation();
                    org.setBudget(createdBudget);
                    org.setName("General");
                    organisationService.create(org);
                    currencyDataService.fetchFromCurrencyfromBdgt(createdBudget);
                    duplicateData();
                    
                    saveCoalevel1(createdBudget);
                    //importUnitsMeasure();
                    List<OldProgramme> programmes = oldProgrammeService.findProgrammesByFy(oldbudget.getFinancialYear());
                    // System.out.println(programmes.size() + ".................Programme size");
                    int z = 0;
                    for (OldProgramme oldprogramme : programmes) {
                        URC_Priority_Areas priorityArea = new URC_Priority_Areas();
                        priorityArea.setBudget(createdBudget);
                        priorityArea.setName(oldprogramme.getProgramme());
                        //System.out.println(priorityArea.toString() + ".................Programme 1");
                        //uRC_Priority_AreasService.update(priorityArea);
                        priorityArea = uRC_Priority_AreasService.getLastSavedItem();
                        //System.out.println(priorityArea.toString() + ".................Programme 2");
                        // System.out.println(oldprogramme.getId() + ".................Programme id");
                        //get Activities by programme
                        List<OldProgActivity> list = oldProgActivityService.getAllProgActivitiesbyProg(oldprogramme.getId());
                        //Notification.show(listAllCurrencyData.size()+"");
                        // System.out.println(oldprogramme.getId()+" Programmme size");
                        int y = 0;
                        for (OldProgActivity pp : list) {
                            
                            Urc_Activities act = new Urc_Activities();
                            act.setOrigid(pp.getId().longValue());
                            act.setFundsource(pp.getFund());
                            act.setBudget(createdBudget);
                            act.setObjective(pp.getObjective());
                            act.setOutput(pp.getOutput());
                            act.setOutcome(pp.getOutcome());
                            act.setName(pp.getActivities());
                            act.setPerformanceIndicator(pp.getPerfInd());
                            act.setUrcPriorityAreas(priorityArea);
                            act.setDeptSection(null);
                            act.setActivityCode(null);
                            //urc_ActivitiesService.update(act);

                        }
                        
                    }
                    // setSections(createdBudget);
                    // setBudgetItems(oldbudget);

                } else {
                    Notification note = Notification.show("Budget Doesn`t Exist");
                    note.addThemeVariants(NotificationVariant.LUMO_WARNING);
                }
            } else {
                Notification note = Notification.show("Budget Exists");
                note.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
            
        } else {
            Notification note = Notification.show("Budget Not Found");
            note.addThemeVariants(NotificationVariant.LUMO_WARNING);
        }
    }
    
    public void budgetOnly(OldBudget oldbudget) {
        Optional<OldBudget> budgetOptional = oldbudgetRepository.getBudgetByFinancialYear(oldbudget.getFinancialYear());
        
        if (budgetOptional.isPresent()) {
            //OldBudget oldbudget = budgetOptional.get();
            /*            Budget budget = new Budget();
            budget.setStartDate(oldbudget.getOpenDate());
            budget.setCloseDate(oldbudget.getCloseDate());
            budget.setFinancialYear(oldbudget.getFinancialYear());
            if (oldbudget.getStatus().equals("Closed")) {
            budget.setActive(false);
            } else if (oldbudget.getStatus().equals("Open")) {
            budget.setActive(true);
            }*/
            
            Optional<Budget> existsBudget = sampleBudgetService.getByFY(oldbudget.getFinancialYear());
            if (existsBudget.isPresent()) {
                // sampleBudgetService.update(budget);
                Optional<Budget> createdBudget = sampleBudgetService.getByFY(oldbudget.getFinancialYear());
                //System.out.println(createdBudgetList.get(0).getFinancialYear() + ".................");
                //Budget createdBudget = createdBudgetList.get(0);
                if (createdBudget.isPresent()) {
                    //Fetch programmes by Budget
                    // Organisation org = new Organisation();
                    // org.setBudget(createdBudget);
                    //org.setName("General");
                    //organisationService.create(org);
                    // currencyDataService.fetchFromCurrencyfromBdgt(createdBudget);
                    // duplicateData();

                    // saveCoalevel1(createdBudget);
                    // importUnitsMeasure();
                    List<OldProgramme> programmes = oldProgrammeService.findProgrammesByFy(oldbudget.getFinancialYear());
                    // System.out.println(programmes.size() + ".................Programme size");
                    int z = 0;
                    for (OldProgramme oldprogramme : programmes) {
                        URC_Priority_Areas priorityArea = new URC_Priority_Areas();
                        priorityArea.setBudget(createdBudget.get());
                        priorityArea.setName(oldprogramme.getProgramme());
                        //System.out.println(priorityArea.toString() + ".................Programme 1");
                        uRC_Priority_AreasService.update(priorityArea);
                        priorityArea = uRC_Priority_AreasService.getLastSavedItem();
                        //System.out.println(priorityArea.toString() + ".................Programme 2");
                        // System.out.println(oldprogramme.getId() + ".................Programme id");
                        //get Activities by programme
                        List<OldProgActivity> list = oldProgActivityService.getAllProgActivitiesbyProg(oldprogramme.getId());
                        //Notification.show(listAllCurrencyData.size()+"");
                        // System.out.println(oldprogramme.getId()+" Programmme size");
                        int y = 0;
                        for (OldProgActivity pp : list) {
                            
                            Urc_Activities act = new Urc_Activities();
                            act.setOrigid(pp.getId().longValue());
                            act.setFundsource(pp.getFund());
                            act.setBudget(createdBudget.get());
                            act.setObjective(pp.getObjective().trim());
                            act.setOutput(pp.getOutput().trim());
                            act.setOutcome(pp.getOutcome().trim());
                            act.setName(pp.getActivities().trim());
                            act.setPerformanceIndicator(pp.getPerfInd().trim());
                            act.setUrcPriorityAreas(priorityArea);
                            act.setDeptSection(null);
                            act.setActivityCode(null);
                            urc_ActivitiesService.update(act);
                            
                        }
                        
                    }
                    setSections(createdBudget.get());
                    setBudgetItems(oldbudget);
                    
                } else {
                    Notification note = Notification.show("Budget Doesn`t Exist");
                    note.addThemeVariants(NotificationVariant.LUMO_WARNING);
                }
            } else {
                Notification note = Notification.show("Budget Exists");
                note.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
            
        } else {
            Notification note = Notification.show("Budget Not Found");
            note.addThemeVariants(NotificationVariant.LUMO_WARNING);
        }
    }
    
    public void setSections(Budget budget) {
        // System.out.println(budget.getFinancialYear() + " Budget");
        List<Urc_Activities> listByBudget = urc_ActivitiesService.listByBudget2(budget);
        //System.out.println(listByBudget.size());
        for (Urc_Activities pp : listByBudget) {
            List<String> findSectionsInBudget = oldBudgetSubItemService.findSectionsInBudget(pp.getOrigid().intValue());
            System.out.println(findSectionsInBudget.size() + " Size");
            int r = 0;
            for (String c : findSectionsInBudget) {
                r++;
                if (r == 1) {
                    // System.out.println(c+" Exists");
                    pp.setDeptSection(urcDeptSectionAnlDimbgtService.findByANL_CODE(c));
                    try {
                        urc_ActivitiesService.update(pp);
                    } catch (Exception e) {
                        // Handle the exception here, you can log it or take appropriate action
                        e.printStackTrace(); // Print the exception stack trace for debugging
                    }
                } else {
                    Urc_Activities act = new Urc_Activities();
                    System.out.println(c + "  " + pp.getName() + " New Created");
                    act.setOrigid(pp.getId());
                    act.setFundsource(pp.getFundsource().trim());
                    act.setBudget(budget);
                    act.setObjective(pp.getObjective().trim());
                    act.setOutput(pp.getOutput().trim());
                    act.setOutcome(pp.getOutcome().trim());
                    act.setName(pp.getName().trim());
                    act.setPerformanceIndicator(pp.getPerformanceIndicator().trim());
                    act.setUrcPriorityAreas(pp.getUrcPriorityAreas());
                    act.setDeptSection(urcDeptSectionAnlDimbgtService.findByANL_CODE(c));
                    act.setActivityCode(null);
                    try {
                        urc_ActivitiesService.update(act);
                        System.out.println(act.getName() + " Saved Successifully");
                    } catch (Exception e) {
                        // Handle the exception here, you can log it or take appropriate action
                        e.printStackTrace(); // Print the exception stack trace for debugging
                    }
                }
                
            }
            
        }
        setActivityCode(budget);
    }
    
    public void setActivityCode(Budget budget) {
        List<Urc_Activities> listByBudget = urc_ActivitiesService.listByBudget2(budget);
        //System.out.println(listByBudget.size());
        for (Urc_Activities pp : listByBudget) {
            UrcDeptSectionAnlDimbgt deptSection = pp.getDeptSection();
            if (deptSection != null && deptSection.getANL_CODE() != null) {
                String maxActivityCode = urc_ActivitiesService.maxActivityCode(deptSection.getANL_CODE());
                String nextActivityCode = "";
                
                if (maxActivityCode != null) {
                    int nextActivityCodeNumber = Integer.parseInt(maxActivityCode.substring(4)) + 1;
                    nextActivityCode = deptSection.getANL_CODE().trim() + String.format("%07d", nextActivityCodeNumber);
                } else {
                    nextActivityCode = deptSection.getANL_CODE().trim() + "0000001";
                }
                
                pp.setActivityCode(nextActivityCode);
                //System.out.println(nextActivityCode);
                try {
                    urc_ActivitiesService.update(pp);
                } catch (Exception e) {
                    // Handle the exception here, you can log it or take appropriate action
                    e.printStackTrace(); // Print the exception stack trace for debugging
                }
            }
        }
    }
    
    public void setBudgetItems(OldBudget oldbudget) {
        List<OldBudgetSubItem> bug = oldBudgetSubItemService.findItemsByFiscalYear(oldbudget.getFinancialYear());
        
        for (OldBudgetSubItem oldbud : bug) {
            
            Optional<Budget> existsBudget = sampleBudgetService.getByFY(oldbudget.getFinancialYear());
            if (existsBudget.isPresent()) {
                BudgetItems bugItem = new BudgetItems();
                bugItem.setItem(oldbud.getItem());
                //System.out.println(oldbud.getItem());
                bugItem.setProduct(oldbud.getProduct());
                bugItem.setCategory(oldbud.getCategory());
                bugItem.setBudget(existsBudget.get());
                //System.out.println(existsBudget.get());
                bugItem.setNotes(oldbud.getNotes());
                bugItem.setBudgetType(organisationService.findByBudgetList(existsBudget.get()).get(0));
                //System.out.println(organisationService.findByBudgetList(existsBudget.get()).get(0).getName());
                bugItem.setCurrency(currencyService.findCurrenciesByCurrencyShortAndBudget(oldbud.getCurrency(), existsBudget.get()));
                /*                List<StockUnitMeasure> stock = stockUnitMeasureService.getStockUnitMeasureByCode(oldbud.getItemunit());
                if (!stock.isEmpty()) {
                bugItem.setUnitMeasure(stockUnitMeasureService.getStockUnitMeasureByUnit(oldbud.getItemunit()).get(0));
                // System.out.println(bugItem.getUnitMeasure());
                }*/
                
                bugItem.setUnitMeasure(oldbud.getItemunit());
                bugItem.setAwardnotidate(oldbud.getAwardNotiDate());
                bugItem.setAppofResv(oldbud.getAppliOfReservSch());
                bugItem.setCompletiondate(oldbud.getCompliDate());
                bugItem.setContractsigndate(oldbud.getContractSignDate());
                bugItem.setBidInv(oldbud.getBidInvDate());
                bugItem.setBidclos(oldbud.getBidClosDate());
                Integer progactivity = oldbud.getProgactivity();
                if (progactivity != null) {
                    // Now it's safe to call longValue() on progactivity
                    long value = progactivity.longValue();
                    List<Urc_Activities> findUrcActivitiesByOrigid = urc_ActivitiesService.findUrcActivitiesByOrigid(oldbud.getProgactivity().longValue());
                    if (!findUrcActivitiesByOrigid.isEmpty()) {
                        bugItem.setActivity(urc_ActivitiesService.findUrcActivitiesByOrigid(oldbud.getProgactivity().longValue()).get(0));

                        //System.out.println(urc_ActivitiesService.findUrcActivitiesByOrigid(oldbud.getProgactivity().longValue()).get(0).toString());
                    }
                } else {
                    bugItem.setActivity(null);
                }
                /*                List<Urc_Activities> findUrcActivitiesByOrigid = urc_ActivitiesService.findUrcActivitiesByOrigid(oldbud.getProgactivity().longValue());
                if (!findUrcActivitiesByOrigid.isEmpty()) {
                bugItem.setProgactivity(urc_ActivitiesService.findUrcActivitiesByOrigid(oldbud.getProgactivity().longValue()).get(0));
                }*/
                bugItem.setTarget_group(oldbud.getTargetGroup());
                bugItem.setExpected_trainer(oldbud.getExpectedTrainer());
                bugItem.setNo_of_days(oldbud.getNoOfDays());
                bugItem.setProcMethod(oldbud.getProMethod());
                bugItem.setProcType(oldbud.getProType());
                bugItem.setPreQ(oldbud.getPreQua());
                bugItem.setAppofResv(oldbud.getAppliOfReservSch());
                bugItem.setDeptUnit(getSectionOfBudgetItem(oldbud));
                // System.out.println(oldbud.getDeptunit()+"  "+getSectionOfBudgetItem(oldbud).getANL_CODE());

                //System.out.println(oldbud.getDeptunit());
                bugItem.setBidInv(oldbud.getBidInvDate());
                bugItem.setCoacode(setCOA(oldbud, existsBudget.get()));
                //System.out.println(bugItem.getCoacode().getCode());
                //bugItem.setCoalevel1(findCoalevel1(bugItem.getCoacode().getCode(), existsBudget.get()));
                COA coacode = bugItem.getCoacode(); // Get the COA object
                if (coacode != null) {
                    bugItem.setCoalevel1(findCoalevel1(coacode.getCode(), existsBudget.get()));
                    // You can also access other properties of coacode here
                } else {
                    // Handle the case where coacode is null, perhaps by logging an error or taking appropriate action.
                }
                //String co = setCOA(oldbud, existsBudget.get()).getCode();
                // System.out.println(co);

                bugItem.setQty(oldbud.getQty());
                bugItem.setCost(oldbud.getCost());
                bugItem.setTotal(oldbud.getTotal());
                bugItem.setJan(oldbud.getJan());
                bugItem.setFeb(oldbud.getFeb());
                bugItem.setMar(oldbud.getMar());
                bugItem.setApr(oldbud.getApr());
                bugItem.setMay(oldbud.getMay());
                bugItem.setJun(oldbud.getJun());
                bugItem.setJul(oldbud.getJul());
                bugItem.setAug(oldbud.getAug());
                bugItem.setSep(oldbud.getSep());
                bugItem.setOct(oldbud.getOct());
                bugItem.setNov(oldbud.getNov());
                bugItem.setDec(oldbud.getDec());
                budgetItemsService.update(bugItem);
            }
            
        }
        
    }
    
    public COA setCOA(OldBudgetSubItem oldbud, Budget budget) {
        COA coa = null;
        OldItem item = oldItemService.findItemById(oldbud.getCodeid());
        // System.out.println(item.getCode() + " Item old");
        List<COAReconcile> code = coaReconcileService.findCOAReconcileByOldcoa(item.getCode());
        for (COAReconcile b : code) {
            //System.out.println(b.getNewcoa() + ": Item new " + b.getOldcoa() + ": Old Item");
            coa = coaService.findByCodeAndBudget(b.getNewcoa(), budget);
            // System.out.println(coa.getCode() + " Item new");
        }
        
        return coa;
    }
    
    public UrcDeptSectionAnlDimbgt getSectionOfBudgetItem(OldBudgetSubItem oldbud) {
        UrcDeptSectionAnlDimbgt sect = null;
        String bb = "";
        Optional<OldDeptUnit> deptunit = oldDeptUnitService.getDeptUnitById(oldbud.getDeptunit());
        if (deptunit.isPresent()) {
            //System.out.println(deptunit.get().getSunCode() + " oLD SECTION");
            List<SectionDeptUnitMerger> getAllMergers = sectionDeptUnitMergerService.getAllMergers();
            //System.out.println(getAllMergers.size() + " oLD SECTION Size");
            for (SectionDeptUnitMerger a : getAllMergers) {
                for (String b : a.getDeptUnitcodes()) {
                    bb = b;
                    
                    if (b.trim().equals(deptunit.get().getSunCode().trim())) {
                        //System.out.println(b.trim() + "_     _"+deptunit.get().getSunCode().trim());
                        sect = urcDeptSectionAnlDimbgtService.findByANL_CODE(a.getSectioncode());
                        if (sect != null) {
                            // System.out.println(sect.getANL_CODE() + "     New Section");
                        } else {
                            //System.out.println(bb+" No matching section found.");
                        }
                        break;
                    }
                }
            }
        }
        
        return sect;
    }
    
    public void setTest(OldBudget oldbudget) {
        List<OldBudgetSubItem> bug = oldBudgetSubItemService.findItemsByFiscalYear(oldbudget.getFinancialYear());
        
        for (OldBudgetSubItem oldbud : bug) {
            System.out.println(getSectionOfBudgetItem(oldbud));
            
        }
    }
    
    private void saveCoalevel1(Budget budget) {
        Coalevel1 coa1 = new Coalevel1();
        coa1.setName("Income");
        if (sampleCoalevel1Service.existsByBudgetAndName(coa1.getName()) == false) {
            //coa1.setBudget(budget);
            coa1.setCode(1);
            sampleCoalevel1Service.save(coa1);
        }
        
        Coalevel1 coa2 = new Coalevel1();
        coa2.setName("Operation Expenditure");
        if (sampleCoalevel1Service.existsByBudgetAndName(coa2.getName()) == false) {
            // coa2.setBudget(budget);
            coa2.setCode(2);
            sampleCoalevel1Service.save(coa2);
        }
        
        Coalevel1 coa3 = new Coalevel1();
        coa3.setName("Capital Expenditure");
        if (sampleCoalevel1Service.existsByBudgetAndName(coa3.getName()) == false) {
            //  coa3.setBudget(budget);
            coa3.setCode(3);
            sampleCoalevel1Service.save(coa3);
        }
        //if (coaService.count() < 1) {
        List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan = sampleUR5_ACNTService.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();
        
        for (UR5_ACNT b : findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan) {
            COA coa = new COA();
            coa.setBudget(budget);
            coa.setCode(b.getAcntCode());
            coa.setName(b.getDescr());
            coa.setStateOpen(true);
            coa.setDisplay(Display.GENERAL);
            String acntCode = "";
            int parsedInt = 0;
            try {
                acntCode = b.getAcntCode().trim();
                String firstCharacter = getFirstCharacter(acntCode);
                parsedInt = Integer.parseInt(firstCharacter);
                System.out.println("The first character of the string as an integer is: " + parsedInt);
            } catch (NumberFormatException e) {
                System.err.println("Error: The first character could not be converted to an integer.");
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
            }
            Coalevel1 findByCode = sampleCoalevel1Service.findByCode(parsedInt);
            coa.setCoalevel1(findByCode);
            coa.setDeptsection(urcDeptSectionAnlDimbgtService.getAllUrcSectionsAsSet());
            coaService.save(coa);
        }
        // }

    }
    
    public void duplicateData() {
        sampleDataDuplicationService.duplicateData();
    }
    
    public void importUnitsMeasure() {
        if (stockUnitMeasureService.count() < 1) {
            stockUnitMeasureService.updateTransfer();
        }
    }
    
    public Coalevel1 findCoalevel1(String code, Budget budget) {
        String income = "Income";
        String opex = "Operation Expenditure";
        String capex = "Capital Expenditure";
        String cats = "";
        
        List<Coalevel1> findByBudget = sampleCoalevel1Service.findByBudget();
        //System.out.println(findByBudget.size());
        Coalevel1 coalnew = null;
        for (Coalevel1 coal : findByBudget) {
            //System.out.println(code+"       "+coal.getName().trim()+"       "+budget.getFinancialYear());
            if (code.startsWith("1") && coal.getName().trim().equalsIgnoreCase(income)) {
                
                coalnew = coal;
                
            } else if (code.startsWith("2") && coal.getName().trim().equalsIgnoreCase(opex)) {
                
                coalnew = coal;
            } else if (code.startsWith("3") && coal.getName().trim().equalsIgnoreCase(capex)) {
                
                coalnew = coal;
            }
        }
        // System.out.println(code+"  "+coalnew.getName());
        return coalnew;
    }
    
    public String getFirstCharacter(String inputString) {
        if (inputString != null && !inputString.isEmpty()) {
            return Character.toString(inputString.charAt(0));
        } else {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }
    }
    
    public salaryScale GetScale(String s) {
        salaryScale ba = null;
        
        if (s.equals("RG 1")) {
            ba = salaryScale.RG_1;
        } else if (s.equals("RG 2")) {
            ba = salaryScale.RG_2;
        } else if (s.equals("RG 3")) {
            ba = salaryScale.RG_3;
        } else if (s.equals("RG 4")) {
            ba = salaryScale.RG_4;
        } else if (s.equals("RG 5")) {
            ba = salaryScale.RG_5;
        } else if (s.equals("RG 6")) {
            ba = salaryScale.RG_6;
        } else if (s.equals("RG 7")) {
            ba = salaryScale.RG_7;
        } else if (s.equals("RG 8")) {
            ba = salaryScale.RG_8;
        } else if (s.equals("RG 9")) {
            ba = salaryScale.RG_9;
        } else if (s.equals("EXEC 1")) {
            ba = salaryScale.EXEC_1;
        } else if (s.equals("EXEC 2")) {
            ba = salaryScale.EXEC_2;
        }
        
        return ba;
    }
}
