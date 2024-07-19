package com.methaltech.application.views.salary;

import com.flowingcode.vaadin.addons.gridexporter.GridExporter;
import com.methaltech.application.data.UploadExamplesI18N;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.FreightVolumesService;
import com.methaltech.application.data.bgtool.service.FundsourceService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.StaffSalaryService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.FreightVolumes;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.StockUnitMeasure;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.entity.oldbgtool.OldStaffPojo;
import com.methaltech.application.data.errorMessages;
import com.methaltech.application.data.oldbgtool.service.OldStaffPojoService;
import com.methaltech.application.data.salaryScale;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.NumberRenderer;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("URC Staff")
@Route(value = "salary", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "HR", "USER"})
@Uses(Icon.class)
public class staffSalaryView extends Div {

    private final FreightVolumesService sampleFreightVolumesService;
    private final BudgetService sampleBudgetService;
    private final CoaService sampleCoaService;
    private ComboBox<Budget> comboBoxBudget = new ComboBox<>("Budget");
    private ComboBox<COA> comboBoxCOA = new ComboBox<>("Freight Route");
    private ComboBox<Urc_Activities> comboBoxUrc_Activities = new ComboBox("Activities");
    private Grid<StaffSalary> gridStaffSalary = new Grid<>(StaffSalary.class, false);
    private final CurrencyService sampleCurrencyService;
    private final BudgetItemsService budgetItemsService;
    private final StockUnitMeasureService sampleStockUnitMeasureService;
    private final OrganisationService sampleOrganisationService;
    private final BudgetItemsService sampleBudgetItemsService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final StaffSalaryService sampleStaffSalaryService;

    private final Binder<StaffSalary> binder = new BeanValidationBinder<>(StaffSalary.class);
    private ComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section = new ComboBox<>("Cost Centre");
    private ComboBox<Currency> currencyComboBox = new ComboBox("Currency");
    private ComboBox<Organisation> comboBoxOrganisation = new ComboBox<>("Budget Type");
    private StaffSalary salaries;

    private TextField fname = new TextField("First Name");
    private TextField lname = new TextField("Last Name");
    private TextField tel = new TextField("Tel");
    private TextField mob = new TextField("Mobile");

    private TextField Address = new TextField("Primary Address");
    private TextField Address2 = new TextField("Address 2");
    private TextField nextofkin = new TextField("Next of Kin");
    private TextField email = new TextField("Email");
    private TextField position = new TextField("Position");
    private ComboBox<salaryScale> grade = new ComboBox<>("Level");
    private TextField code = new TextField("Staff Code");
    private TextField contract = new TextField("C");
    private BigDecimalField salaryz = new BigDecimalField("Monthly Salary");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button cancel = new Button("Cancel");
    Span span = new Span();

    SplitLayout splitLayout = new SplitLayout();
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US); // Use US locale for consistent formatting

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
    private AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private User user;
    private final OldStaffPojoService oldStaffPojoService;
    private final Coalevel1Service coalevel1Service;

    private ComboBox<Fundsource> budgetItemfundSource = new ComboBox<>("Fund Source");
    private final FundsourceService sampleFundsourceService;
    private Upload upload;

    public staffSalaryView(AuthenticatedUser authenticatedUser, FreightVolumesService sampleFreightVolumesService, BudgetService sampleBudgetService, CoaService sampleCoaService,
            CurrencyService sampleCurrencyService, BudgetItemsService budgetItemsService, StockUnitMeasureService sampleStockUnitMeasureService,
            OrganisationService sampleOrganisationService, UserService userService, BudgetItemsService sampleBudgetItemsService,
            Urc_ActivitiesService sampleUrc_ActivitiesService, StaffSalaryService sampleStaffSalaryService, OldStaffPojoService oldStaffPojoService,
            Coalevel1Service coalevel1Service, FundsourceService sampleFundsourceService) {
        this.sampleFreightVolumesService = sampleFreightVolumesService;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleCoaService = sampleCoaService;
        this.sampleCurrencyService = sampleCurrencyService;
        this.budgetItemsService = budgetItemsService;
        this.sampleStockUnitMeasureService = sampleStockUnitMeasureService;
        this.sampleOrganisationService = sampleOrganisationService;
        this.userService = userService;
        this.sampleBudgetItemsService = sampleBudgetItemsService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.sampleStaffSalaryService = sampleStaffSalaryService;
        this.oldStaffPojoService = oldStaffPojoService;
        this.coalevel1Service = coalevel1Service;
        this.sampleFundsourceService = sampleFundsourceService;
        setHeight("100%");
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }

        budgetItemfundSource.setItemLabelGenerator(Fundsource::getFundsource);

        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        comboBoxD_Section.setItems(user.getDeptsection());

        comboBoxUrc_Activities.setItemLabelGenerator(Urc_Activities::getName);

        comboBoxOrganisation.setItemLabelGenerator(Organisation::getName);
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Staff Salary", detailsPanel());
        tabSheet.add("Staff Salary Reports", secondPanel());
        tabSheet.setHeight("100%");

        binder.forField(fname).bind(StaffSalary::getFname, StaffSalary::setFname);
        binder.forField(lname).bind(StaffSalary::getLname, StaffSalary::setLname);
        binder.forField(tel).bind(StaffSalary::getTel, StaffSalary::setTel);
        binder.forField(mob).bind(StaffSalary::getMob, StaffSalary::setMob);
        binder.forField(Address).bind(StaffSalary::getAddress, StaffSalary::setAddress);
        binder.forField(Address2).bind(StaffSalary::getAddress2, StaffSalary::setAddress2);
        binder.forField(nextofkin).bind(StaffSalary::getNextofkin, StaffSalary::setNextofkin);
        binder.forField(email).bind(StaffSalary::getEmail, StaffSalary::setEmail);
        binder.forField(position).bind(StaffSalary::getPosition, StaffSalary::setPosition);
        binder.forField(grade).bind(StaffSalary::getGrade, StaffSalary::setGrade);
        binder.forField(code).bind(StaffSalary::getCode, StaffSalary::setCode);
        binder.forField(comboBoxD_Section).bind(StaffSalary::getDeptUnit, StaffSalary::setDeptUnit);
        binder.forField(comboBoxOrganisation).bind(StaffSalary::getBudgetType, StaffSalary::setBudgetType);
        binder.forField(comboBoxBudget).bind(StaffSalary::getBudget, StaffSalary::setBudget);
        binder.forField(comboBoxUrc_Activities).bind(StaffSalary::getActivity, StaffSalary::setActivity);
        // binder.forField(comboBoxBudget).bind(FreightVolumes::getBudget, FreightVolumes::setBudget);
        binder.forField(salaryz).bind(StaffSalary::getSalary, StaffSalary::setSalary);
        // binder.forField(comboBoxCOA).bind(FreightVolumes::getCoacode, FreightVolumes::setCoacode);
        //salaryz.setEnabled(false);
        symbols.setGroupingSeparator(','); // Set the grouping separator to a comma
        grade.setItems(salaryScale.EXEC_1, salaryScale.EXEC_2, salaryScale.RG_1, salaryScale.RG_2, salaryScale.RG_3, salaryScale.RG_4, salaryScale.RG_5, salaryScale.RG_6, salaryScale.RG_7, salaryScale.RG_8, salaryScale.RG_9);
        // volumesDetails();

        setBudgetCombo();

        setFreightVolumeGridDetails();
        add(tabSheet);

    }

    public void setBudgetCombo() {
        comboBoxBudget.setItems(query -> sampleBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        comboBoxBudget.setItemLabelGenerator(Budget::getFinancialYear);
        comboBoxBudget.addValueChangeListener(e -> {
            if (!e.getValue().isActive()) {
                save.setEnabled(false);
                cancel.setEnabled(false);
                delete.setEnabled(false);
                upload.setVisible(false);
            }
            budgetItemfundSource.setItems(sampleFundsourceService.findFundsourcesByBudget(e.getValue()));
            setSalaryGrid2();
            if (!comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                comboBoxUrc_Activities.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudget(comboBoxD_Section.getValue(), comboBoxBudget.getValue()));
                // setSalaryGrid2();
            }

            comboBoxOrganisation.setItems(sampleOrganisationService.findByBudgetList(comboBoxBudget.getValue()));
        });
        comboBoxD_Section.addValueChangeListener(e -> {
            try {
                if (!comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                    comboBoxUrc_Activities.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudget(comboBoxD_Section.getValue(), comboBoxBudget.getValue()));
                    //setSalaryGrid();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification.show(ex.getMessage());
                // Handle the exception or log it for debugging.
            }
        });

        comboBoxUrc_Activities.addValueChangeListener(e -> {
            // setSalaryGrid();
        });
        comboBoxOrganisation.addValueChangeListener(e -> {
            // setSalaryGrid();
        });
        delete.addClickListener(e -> {
            if (!gridStaffSalary.asSingleSelect().isEmpty()) {
                Dialog dialog = new Dialog();

                dialog.setHeaderTitle(
                        String.format("Delete staff \"%s\"?", gridStaffSalary.asSingleSelect().getValue().getFname() + " " + gridStaffSalary.asSingleSelect().getValue().getLname()));
                dialog.add("Are you sure you want to delete this staff permanently?");

                Button deleteButton = new Button("Delete");
                deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                        ButtonVariant.LUMO_ERROR);
                deleteButton.getStyle().set("margin-right", "auto");
                dialog.getFooter().add(deleteButton);

                deleteButton.addClickListener(es -> {
                    StaffSalary salary = gridStaffSalary.asSingleSelect().getValue();
                    sampleBudgetItemsService.deleteByAnalcode(salary.getId());
                    sampleStaffSalaryService.deleteBystaff(salary);
                    deleteItemsalaryBudget();
                    setSalaryGrid2();
                    clearForm();
                    dialog.close();
                });

                Button cancelButton = new Button("Cancel", (ez) -> dialog.close());
                cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                dialog.getFooter().add(cancelButton);
                dialog.open();

            } else {
                Notification.show("Select a Staff");
            }
        });
        save.addClickListener(event -> {

            if (!comboBoxBudget.isEmpty() && !fname.isEmpty() && !salaryz.isEmpty()) {

                StaffSalary salary = gridStaffSalary.asSingleSelect().getValue();
                BudgetItems item;
                if (salary == null) {
                    salary = new StaffSalary();
                }
// Update the fields of the freightVolume object with the form data
                binder.writeBeanIfValid(salary);

                salary.setFname(fname.getValue());
                salary.setLname(lname.getValue());
                salary.setTel(tel.getValue());
                salary.setMob(mob.getValue());
                salary.setAddress(Address.getValue());
                salary.setAddress2(Address2.getValue());
                salary.setPosition(position.getValue());
                salary.setCode(code.getValue());
                salary.setGrade(grade.getValue());
                salary.setBudget(comboBoxBudget.getValue());
                salary.setSalary(salaryz.getValue());
                salary.setBudgetType(comboBoxOrganisation.getValue());
                salary.setActivity(comboBoxUrc_Activities.getValue());
                salary.setDeptUnit(comboBoxD_Section.getValue());
                sampleStaffSalaryService.saveStaffSalary(salary);
                //setSalaryGrid();
                itemsalaryBudget(salary, comboBoxOrganisation.getValue());

                //setCOACombo(comboBoxBudget.getValue());
                setSalaryGrid2();

                clearForm();
            } else {
                warningNotification("Empty Values");
            }

        });
        cancel.addClickListener(event -> cancel());
    }

    public void setSalaryGrid() {
        if (!comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxUrc_Activities.isEmpty()) {
            gridStaffSalary.setItems(sampleStaffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(comboBoxBudget.getValue(), comboBoxD_Section.getValue(), comboBoxOrganisation.getValue(), comboBoxUrc_Activities.getValue()));
            span.setText("Total Monthly Salary " + decimalFormat.format(sampleStaffSalaryService.getSumOfSalariesByCriteria2(comboBoxBudget.getValue())));
        }

    }

    public void setSalaryGrid2() {
        if (!comboBoxBudget.isEmpty()) {
            gridStaffSalary.setItems(sampleStaffSalaryService.findByBudget(comboBoxBudget.getValue()));
            span.setText("Total Monthly Salary " + decimalFormat.format(sampleStaffSalaryService.getSumOfSalariesByCriteria2(comboBoxBudget.getValue())));
        }

    }

    public void setFreightVolumeGridDetails() {
        currencyComboBox.setItems(query -> sampleCurrencyService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        currencyComboBox.setItemLabelGenerator(item -> item.getData().getCurrencyShort());
        Column<StaffSalary> fname1 = gridStaffSalary.addColumn("fname").setHeader("First Name").setAutoWidth(true);
        Column<StaffSalary> lname1 = gridStaffSalary.addColumn("lname").setHeader("Last Name").setAutoWidth(true);
        Column<StaffSalary> position1 = gridStaffSalary.addColumn("position").setHeader("Position").setAutoWidth(true);
        Column<StaffSalary> rateColumn1 = gridStaffSalary.addColumn("salary").setHeader("Salary").setAutoWidth(true);
        rateColumn1.setRenderer(new NumberRenderer<>(StaffSalary::getSalary, decimalFormat));

        /*    GridExporter<StaffSalary> exporter =GridExporter.createFor(gridStaffSalary, "/custom-template.xlsx", "/custom-template.docx");
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("${date}", new SimpleDateFormat().format(Calendar.getInstance().getTime()));
        exporter.setExportColumn(fname1, false);
        exporter.setExportColumn(lname1, true);
        exporter.setCustomHeader(rateColumn1, "URC Staff");
        exporter.setAdditionalPlaceHolders(placeholders);
        exporter.setSheetNumber(1);
        exporter.setCsvExportEnabled(false);
        exporter.setTitle("URC Staff");
        exporter.setFileName(
        "GridExport" + new SimpleDateFormat("yyyyddMM").format(Calendar.getInstance().getTime())); */
        gridStaffSalary.asSingleSelect().addValueChangeListener(e -> {
            StaffSalary selectedSalary = e.getValue();

            if (selectedSalary != null) {
                setDetails(selectedSalary);
                fname.setValue(selectedSalary.getFname() != null ? selectedSalary.getFname() : "");
                lname.setValue(selectedSalary.getLname() != null ? selectedSalary.getLname() : "");
                tel.setValue(selectedSalary.getTel() != null ? selectedSalary.getTel() : "");
                mob.setValue(selectedSalary.getMob() != null ? selectedSalary.getMob() : "");
                Address.setValue(selectedSalary.getAddress() != null ? selectedSalary.getAddress() : "");
                Address2.setValue(selectedSalary.getAddress2() != null ? selectedSalary.getAddress2() : "");
                position.setValue(selectedSalary.getPosition() != null ? selectedSalary.getPosition() : "");
                code.setValue(selectedSalary.getCode() != null ? selectedSalary.getCode() : "");
                email.setValue(selectedSalary.getEmail() != null ? selectedSalary.getEmail() : "");
                nextofkin.setValue(selectedSalary.getNextofkin() != null ? selectedSalary.getNextofkin() : "");
                // budgetItemfundSource.setValue(selectedSalary. != null ? selectedSalary.getNextofkin() : "");

            } else {
                fname.clear();
                lname.clear();
                tel.clear();
                mob.clear();
                Address.clear();
                Address2.clear();
                position.clear();
                code.clear();
                grade.clear();
                salaryz.clear();
                email.clear();
                nextofkin.clear();

            }
        });

    }

    public FormLayout volumesDetails() {
        FormLayout form = new FormLayout();
        HorizontalLayout ho = new HorizontalLayout();
        ho.add(save, delete, cancel);
        form.add(code, fname, lname, email, tel, mob, position, grade, Address,
                Address2, nextofkin,
                salaryz, ho
        );

        // binder.bindInstanceFields(this);
        return form;
    }

    public void itemsalaryBudget(StaffSalary salarywage, Organisation org) {
        StockUnitMeasure measure = sampleStockUnitMeasureService.getStockUnitMeasureByUnit("MONTH").get(0);
        Currency cur = sampleCurrencyService.findCurrenciesByCurrencyShortAndBudget("UGX", comboBoxBudget.getValue());
        COA salary_wages = sampleCoaService.findByCodeAndBudget("211101", comboBoxBudget.getValue());
        COA nssf = sampleCoaService.findByCodeAndBudget("212101", comboBoxBudget.getValue());
        COA gratuity = sampleCoaService.findByCodeAndBudget("213004", comboBoxBudget.getValue());
        COA workmancompesation = sampleCoaService.findByCodeAndBudget("213005", comboBoxBudget.getValue());
        BudgetItems item = sampleBudgetItemsService.getBudgetItemsByAnalcode(salarywage.getId());
        Coalevel1 coa = coalevel1Service.findByCode(2);
        if (item != null) {

            item.setItem(salarywage.getFname() + " " + salarywage.getLname());
            item.setCost(salarywage.getSalary());
            item.setQty(new BigDecimal("12"));

            item.setUnitMeasure("MONTH");

            item.setCurrency(cur);
            item.setBudget(salarywage.getBudget());
            item.setBudgetType(org);
            item.setCoacode(salary_wages);
            item.setDeptUnit(comboBoxD_Section.getValue());
            item.setAnalcode(salarywage.getId());
            item.setActivity(comboBoxUrc_Activities.getValue());
            item.setBudgetType(comboBoxOrganisation.getValue());
            item.setBcategory(salary_wages.getCode());
            item.setFundsource(budgetItemfundSource.getValue());
            item.setCoalevel1(coa);
            item.setJan(salarywage.getSalary());
            item.setFeb(salarywage.getSalary());
            item.setMar(salarywage.getSalary());
            item.setApr(salarywage.getSalary());
            item.setMay(salarywage.getSalary());
            item.setJun(salarywage.getSalary());
            item.setJul(salarywage.getSalary());
            item.setAug(salarywage.getSalary());
            item.setSep(salarywage.getSalary());
            item.setOct(salarywage.getSalary());
            item.setNov(salarywage.getSalary());
            item.setDec(salarywage.getSalary());
        } else {
            item = new BudgetItems();
            item.setItem(salarywage.getFname() + " " + salarywage.getLname());
            item.setCost(salarywage.getSalary());
            item.setQty(new BigDecimal("12"));

            item.setUnitMeasure("MONTH");

            item.setCurrency(cur);
            item.setBudget(salarywage.getBudget());
            item.setBudgetType(org);
            item.setCoacode(salary_wages);
            item.setDeptUnit(comboBoxD_Section.getValue());
            item.setFundsource(budgetItemfundSource.getValue());
            item.setAnalcode(salarywage.getId());
            item.setActivity(comboBoxUrc_Activities.getValue());
            item.setBudgetType(comboBoxOrganisation.getValue());
            item.setBcategory(salary_wages.getCode());
            item.setCoalevel1(coa);
            item.setJan(salarywage.getSalary());
            item.setFeb(salarywage.getSalary());
            item.setMar(salarywage.getSalary());
            item.setApr(salarywage.getSalary());
            item.setMay(salarywage.getSalary());
            item.setJun(salarywage.getSalary());
            item.setJul(salarywage.getSalary());
            item.setAug(salarywage.getSalary());
            item.setSep(salarywage.getSalary());
            item.setOct(salarywage.getSalary());
            item.setNov(salarywage.getSalary());
            item.setDec(salarywage.getSalary());
        }
        if (item.getCost() != null || item.getCost() != BigDecimal.ZERO || item.getQty() != null || item.getQty() != BigDecimal.ZERO) {
            sampleBudgetItemsService.update(item);
            BudgetItems nssfB = sampleBudgetItemsService.findBudgetAndCode(comboBoxBudget.getValue(), nssf);

            if (nssfB != null) {
                BudgetItems nssfs = sampleBudgetItemsService.sumIndividualMonthsBudgetAndCode(comboBoxBudget.getValue(), salary_wages);
                nssfB.setItem(nssf.getName());
                nssfB.setCost(calculateMonthSum(nssfs).multiply(new BigDecimal("0.1")));
                nssfB.setQty(new BigDecimal("1"));
                nssfB.setCurrency(cur);
                nssfB.setBudget(salarywage.getBudget());
                nssfB.setBudgetType(org);
                nssfB.setCoacode(nssf);
                nssfB.setDeptUnit(comboBoxD_Section.getValue());
                nssfB.setUnitMeasure("MONTH");
                //nssfB.setAnalcode(salarywage.getId());
                nssfB.setActivity(comboBoxUrc_Activities.getValue());
                nssfB.setBudgetType(comboBoxOrganisation.getValue());
                nssfB.setFundsource(budgetItemfundSource.getValue());
                nssfB.setBcategory(nssf.getCode());
                nssfB.setCoalevel1(coa);
                nssfB.setJan(nssfs.getJan().multiply(new BigDecimal("0.1")));
                nssfB.setFeb(nssfs.getFeb().multiply(new BigDecimal("0.1")));
                nssfB.setMar(nssfs.getMar().multiply(new BigDecimal("0.1")));
                nssfB.setApr(nssfs.getApr().multiply(new BigDecimal("0.1")));
                nssfB.setMay(nssfs.getMay().multiply(new BigDecimal("0.1")));
                nssfB.setJun(nssfs.getJun().multiply(new BigDecimal("0.1")));
                nssfB.setJul(nssfs.getJul().multiply(new BigDecimal("0.1")));
                nssfB.setAug(nssfs.getAug().multiply(new BigDecimal("0.1")));
                nssfB.setSep(nssfs.getSep().multiply(new BigDecimal("0.1")));
                nssfB.setOct(nssfs.getOct().multiply(new BigDecimal("0.1")));
                nssfB.setNov(nssfs.getNov().multiply(new BigDecimal("0.1")));
                nssfB.setDec(nssfs.getDec().multiply(new BigDecimal("0.1")));
            } else {
                nssfB = new BudgetItems();
                nssfB.setItem(nssf.getName());
                nssfB.setCost(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setQty(new BigDecimal("12"));
                nssfB.setCurrency(cur);
                nssfB.setBudget(salarywage.getBudget());
                nssfB.setBudgetType(org);
                nssfB.setCoacode(nssf);
                nssfB.setDeptUnit(comboBoxD_Section.getValue());
                nssfB.setUnitMeasure("MONTH");
                //nssfB.setAnalcode(salarywage.getId());
                nssfB.setActivity(comboBoxUrc_Activities.getValue());
                nssfB.setFundsource(budgetItemfundSource.getValue());
                nssfB.setBudgetType(comboBoxOrganisation.getValue());
                nssfB.setBcategory(nssf.getCode());
                nssfB.setCoalevel1(coa);
                nssfB.setJan(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setFeb(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setMar(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setApr(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setMay(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setJun(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setJul(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setAug(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setSep(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setOct(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setNov(salarywage.getSalary().multiply(new BigDecimal("0.1")));
                nssfB.setDec(salarywage.getSalary().multiply(new BigDecimal("0.1")));
            }
            sampleBudgetItemsService.update(nssfB);
            BudgetItems nssfG = sampleBudgetItemsService.findBudgetAndCode(comboBoxBudget.getValue(), gratuity);

            if (nssfG != null) {
                BudgetItems grauityAmount = sampleBudgetItemsService.sumIndividualMonthsBudgetAndCode(comboBoxBudget.getValue(), salary_wages);
                nssfG.setItem(gratuity.getName());
                nssfG.setCost(calculateMonthSum(grauityAmount).multiply(new BigDecimal("0.25")));
                nssfG.setQty(new BigDecimal("1"));
                nssfG.setUnitMeasure("MONTH");
                nssfG.setCurrency(cur);
                nssfG.setBudget(salarywage.getBudget());
                nssfG.setBudgetType(org);
                nssfG.setCoacode(gratuity);
                nssfG.setDeptUnit(comboBoxD_Section.getValue());
                nssfG.setDeptUnit(comboBoxD_Section.getValue());
                //nssfG.setAnalcode(salarywage.getId());
                nssfG.setActivity(comboBoxUrc_Activities.getValue());
                nssfG.setBudgetType(comboBoxOrganisation.getValue());
                nssfG.setFundsource(budgetItemfundSource.getValue());
                nssfG.setBcategory(gratuity.getCode());
                nssfG.setCoalevel1(coa);
                nssfG.setJan(grauityAmount.getJan().multiply(new BigDecimal("0.25")));
                nssfG.setFeb(grauityAmount.getFeb().multiply(new BigDecimal("0.25")));
                nssfG.setMar(grauityAmount.getMar().multiply(new BigDecimal("0.25")));
                nssfG.setApr(grauityAmount.getApr().multiply(new BigDecimal("0.25")));
                nssfG.setMay(grauityAmount.getMay().multiply(new BigDecimal("0.25")));
                nssfG.setJun(grauityAmount.getJun().multiply(new BigDecimal("0.25")));
                nssfG.setJul(grauityAmount.getJul().multiply(new BigDecimal("0.25")));
                nssfG.setAug(grauityAmount.getAug().multiply(new BigDecimal("0.25")));
                nssfG.setSep(grauityAmount.getSep().multiply(new BigDecimal("0.25")));
                nssfG.setOct(grauityAmount.getOct().multiply(new BigDecimal("0.25")));
                nssfG.setNov(grauityAmount.getNov().multiply(new BigDecimal("0.25")));
                nssfG.setDec(grauityAmount.getDec().multiply(new BigDecimal("0.25")));
            } else {
                nssfG = new BudgetItems();
                nssfG.setItem(gratuity.getName());
                nssfG.setCost(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setQty(new BigDecimal("12"));
                nssfG.setUnitMeasure("MONTH");
                nssfG.setCurrency(cur);
                nssfG.setBudget(salarywage.getBudget());
                nssfG.setBudgetType(org);
                nssfG.setCoacode(gratuity);
                nssfG.setDeptUnit(comboBoxD_Section.getValue());
                nssfG.setDeptUnit(comboBoxD_Section.getValue());
                // nssfG.setAnalcode(salarywage.getId());
                nssfG.setActivity(comboBoxUrc_Activities.getValue());
                nssfG.setBudgetType(comboBoxOrganisation.getValue());
                nssfG.setFundsource(budgetItemfundSource.getValue());
                nssfG.setBcategory(gratuity.getCode());
                nssfG.setCoalevel1(coa);
                nssfG.setJan(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setFeb(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setMar(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setApr(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setMay(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setJun(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setJul(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setAug(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setSep(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setOct(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setNov(salarywage.getSalary().multiply(new BigDecimal("0.25")));
                nssfG.setDec(salarywage.getSalary().multiply(new BigDecimal("0.25")));
            }
            sampleBudgetItemsService.update(nssfG);
            BudgetItems nssfW = sampleBudgetItemsService.findBudgetAndCode(comboBoxBudget.getValue(), workmancompesation);

            if (nssfW != null) {
                BudgetItems grauityAmount = sampleBudgetItemsService.sumIndividualMonthsBudgetAndCode(comboBoxBudget.getValue(), salary_wages);
                nssfW.setItem(workmancompesation.getName());
                nssfW.setCost(calculateMonthSum(grauityAmount).multiply(new BigDecimal("0.03")));
                nssfW.setQty(new BigDecimal("1"));
                nssfW.setUnitMeasure("MONTH");
                nssfW.setCurrency(cur);
                nssfW.setBudget(salarywage.getBudget());
                nssfW.setBudgetType(org);
                nssfW.setCoalevel1(coa);
                nssfW.setCoacode(workmancompesation);
                nssfW.setDeptUnit(comboBoxD_Section.getValue());
                nssfW.setDeptUnit(comboBoxD_Section.getValue());
                // nssfW.setAnalcode(salarywage.getId());
                nssfW.setActivity(comboBoxUrc_Activities.getValue());
                nssfW.setBudgetType(comboBoxOrganisation.getValue());
                nssfW.setFundsource(budgetItemfundSource.getValue());
                nssfW.setBcategory(workmancompesation.getCode());
                nssfW.setJan(grauityAmount.getJan().multiply(new BigDecimal("0.03")));
                nssfW.setFeb(grauityAmount.getFeb().multiply(new BigDecimal("0.03")));
                nssfW.setMar(grauityAmount.getMar().multiply(new BigDecimal("0.03")));
                nssfW.setApr(grauityAmount.getApr().multiply(new BigDecimal("0.03")));
                nssfW.setMay(grauityAmount.getMay().multiply(new BigDecimal("0.03")));
                nssfW.setJun(grauityAmount.getJun().multiply(new BigDecimal("0.03")));
                nssfW.setJul(grauityAmount.getJul().multiply(new BigDecimal("0.03")));
                nssfW.setAug(grauityAmount.getAug().multiply(new BigDecimal("0.03")));
                nssfW.setSep(grauityAmount.getSep().multiply(new BigDecimal("0.03")));
                nssfW.setOct(grauityAmount.getOct().multiply(new BigDecimal("0.03")));
                nssfW.setNov(grauityAmount.getNov().multiply(new BigDecimal("0.03")));
                nssfW.setDec(grauityAmount.getDec().multiply(new BigDecimal("0.03")));
            } else {
                nssfW = new BudgetItems();
                nssfW.setItem(workmancompesation.getName());
                nssfW.setCost(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setQty(new BigDecimal("12"));
                nssfW.setCoalevel1(coa);
                nssfW.setUnitMeasure("MONTH");
                nssfW.setCurrency(cur);
                nssfW.setBudget(salarywage.getBudget());
                nssfW.setBudgetType(org);
                nssfW.setCoacode(workmancompesation);
                nssfW.setDeptUnit(comboBoxD_Section.getValue());
                nssfW.setDeptUnit(comboBoxD_Section.getValue());
                // nssfW.setAnalcode(salarywage.getId());
                nssfW.setActivity(comboBoxUrc_Activities.getValue());
                nssfW.setBudgetType(comboBoxOrganisation.getValue());
                nssfW.setFundsource(budgetItemfundSource.getValue());
                nssfW.setBcategory(workmancompesation.getCode());
                nssfW.setJan(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setFeb(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setMar(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setApr(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setMay(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setJun(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setJul(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setAug(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setSep(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setOct(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setNov(salarywage.getSalary().multiply(new BigDecimal("0.03")));
                nssfW.setDec(salarywage.getSalary().multiply(new BigDecimal("0.03")));
            }
            sampleBudgetItemsService.update(nssfW);
        }

    }

    public void deleteItemsalaryBudget() {
        StockUnitMeasure measure = sampleStockUnitMeasureService.getStockUnitMeasureByUnit("MONTH").get(0);
        Currency cur = sampleCurrencyService.findCurrenciesByCurrencyShortAndBudget("UGX", comboBoxBudget.getValue());
        COA salary_wages = sampleCoaService.findByCodeAndBudget("211101", comboBoxBudget.getValue());
        COA nssf = sampleCoaService.findByCodeAndBudget("212101", comboBoxBudget.getValue());
        COA gratuity = sampleCoaService.findByCodeAndBudget("213004", comboBoxBudget.getValue());
        COA workmancompesation = sampleCoaService.findByCodeAndBudget("213005", comboBoxBudget.getValue());
        Coalevel1 coa = coalevel1Service.findByCode(2);

        BudgetItems nssfB = sampleBudgetItemsService.findBudgetAndCode(comboBoxBudget.getValue(), nssf);

        if (nssfB != null) {
            BudgetItems nssfs = sampleBudgetItemsService.sumIndividualMonthsBudgetAndCode(comboBoxBudget.getValue(), salary_wages);
            nssfB.setItem(nssf.getName());
            nssfB.setCost(calculateMonthSum(nssfs).multiply(new BigDecimal("0.1")));
            nssfB.setQty(new BigDecimal("1"));
            nssfB.setCurrency(cur);
            nssfB.setBudget(comboBoxBudget.getValue());
            nssfB.setCoacode(nssf);
            nssfB.setDeptUnit(comboBoxD_Section.getValue());
            nssfB.setUnitMeasure("MONTH");
            //nssfB.setAnalcode(salarywage.getId());
            nssfB.setActivity(comboBoxUrc_Activities.getValue());
            nssfB.setBudgetType(comboBoxOrganisation.getValue());
            nssfB.setBcategory(nssf.getCode());
            nssfB.setCoalevel1(coa);
            nssfB.setJan(nssfs.getJan().multiply(new BigDecimal("0.1")));
            nssfB.setFeb(nssfs.getFeb().multiply(new BigDecimal("0.1")));
            nssfB.setMar(nssfs.getMar().multiply(new BigDecimal("0.1")));
            nssfB.setApr(nssfs.getApr().multiply(new BigDecimal("0.1")));
            nssfB.setMay(nssfs.getMay().multiply(new BigDecimal("0.1")));
            nssfB.setJun(nssfs.getJun().multiply(new BigDecimal("0.1")));
            nssfB.setJul(nssfs.getJul().multiply(new BigDecimal("0.1")));
            nssfB.setAug(nssfs.getAug().multiply(new BigDecimal("0.1")));
            nssfB.setSep(nssfs.getSep().multiply(new BigDecimal("0.1")));
            nssfB.setOct(nssfs.getOct().multiply(new BigDecimal("0.1")));
            nssfB.setNov(nssfs.getNov().multiply(new BigDecimal("0.1")));
            nssfB.setDec(nssfs.getDec().multiply(new BigDecimal("0.1")));
        }
        sampleBudgetItemsService.update(nssfB);
        BudgetItems nssfG = sampleBudgetItemsService.findBudgetAndCode(comboBoxBudget.getValue(), gratuity);

        if (nssfG != null) {
            BudgetItems grauityAmount = sampleBudgetItemsService.sumIndividualMonthsBudgetAndCode(comboBoxBudget.getValue(), salary_wages);
            nssfG.setItem(gratuity.getName());
            nssfG.setCost(calculateMonthSum(grauityAmount).multiply(new BigDecimal("0.25")));
            nssfG.setQty(new BigDecimal("1"));
            nssfG.setUnitMeasure("MONTH");
            nssfG.setCurrency(cur);
            nssfG.setBudget(comboBoxBudget.getValue());
            nssfG.setCoacode(gratuity);
            nssfG.setDeptUnit(comboBoxD_Section.getValue());
            nssfG.setDeptUnit(comboBoxD_Section.getValue());
            //nssfG.setAnalcode(salarywage.getId());
            nssfG.setActivity(comboBoxUrc_Activities.getValue());
            nssfG.setBudgetType(comboBoxOrganisation.getValue());
            nssfG.setBcategory(gratuity.getCode());
            nssfG.setCoalevel1(coa);
            nssfG.setJan(grauityAmount.getJan().multiply(new BigDecimal("0.25")));
            nssfG.setFeb(grauityAmount.getFeb().multiply(new BigDecimal("0.25")));
            nssfG.setMar(grauityAmount.getMar().multiply(new BigDecimal("0.25")));
            nssfG.setApr(grauityAmount.getApr().multiply(new BigDecimal("0.25")));
            nssfG.setMay(grauityAmount.getMay().multiply(new BigDecimal("0.25")));
            nssfG.setJun(grauityAmount.getJun().multiply(new BigDecimal("0.25")));
            nssfG.setJul(grauityAmount.getJul().multiply(new BigDecimal("0.25")));
            nssfG.setAug(grauityAmount.getAug().multiply(new BigDecimal("0.25")));
            nssfG.setSep(grauityAmount.getSep().multiply(new BigDecimal("0.25")));
            nssfG.setOct(grauityAmount.getOct().multiply(new BigDecimal("0.25")));
            nssfG.setNov(grauityAmount.getNov().multiply(new BigDecimal("0.25")));
            nssfG.setDec(grauityAmount.getDec().multiply(new BigDecimal("0.25")));
        }
        sampleBudgetItemsService.update(nssfG);
        BudgetItems nssfW = sampleBudgetItemsService.findBudgetAndCode(comboBoxBudget.getValue(), workmancompesation);

        if (nssfW != null) {
            BudgetItems grauityAmount = sampleBudgetItemsService.sumIndividualMonthsBudgetAndCode(comboBoxBudget.getValue(), salary_wages);
            nssfW.setItem(workmancompesation.getName());
            nssfW.setCost(calculateMonthSum(grauityAmount).multiply(new BigDecimal("0.03")));
            nssfW.setQty(new BigDecimal("1"));
            nssfW.setUnitMeasure("MONTH");
            nssfW.setCurrency(cur);
            nssfW.setBudget(comboBoxBudget.getValue());
            nssfW.setCoalevel1(coa);
            nssfW.setCoacode(workmancompesation);
            nssfW.setDeptUnit(comboBoxD_Section.getValue());
            nssfW.setDeptUnit(comboBoxD_Section.getValue());
            // nssfW.setAnalcode(salarywage.getId());
            nssfW.setActivity(comboBoxUrc_Activities.getValue());
            nssfW.setBudgetType(comboBoxOrganisation.getValue());
            nssfW.setBcategory(workmancompesation.getCode());
            nssfW.setJan(grauityAmount.getJan().multiply(new BigDecimal("0.03")));
            nssfW.setFeb(grauityAmount.getFeb().multiply(new BigDecimal("0.03")));
            nssfW.setMar(grauityAmount.getMar().multiply(new BigDecimal("0.03")));
            nssfW.setApr(grauityAmount.getApr().multiply(new BigDecimal("0.03")));
            nssfW.setMay(grauityAmount.getMay().multiply(new BigDecimal("0.03")));
            nssfW.setJun(grauityAmount.getJun().multiply(new BigDecimal("0.03")));
            nssfW.setJul(grauityAmount.getJul().multiply(new BigDecimal("0.03")));
            nssfW.setAug(grauityAmount.getAug().multiply(new BigDecimal("0.03")));
            nssfW.setSep(grauityAmount.getSep().multiply(new BigDecimal("0.03")));
            nssfW.setOct(grauityAmount.getOct().multiply(new BigDecimal("0.03")));
            nssfW.setNov(grauityAmount.getNov().multiply(new BigDecimal("0.03")));
            nssfW.setDec(grauityAmount.getDec().multiply(new BigDecimal("0.03")));
        }
        sampleBudgetItemsService.update(nssfW);

    }

    public void setStaffData(StaffSalary slaries) {
        this.salaries = slaries;
        binder.setBean(slaries);
    }

    private void cancel() {
        Notification.show("Operation canceled.");
    }

    private VerticalLayout detailsPanel() {
        VerticalLayout lay = new VerticalLayout();
        HorizontalLayout hor = new HorizontalLayout();
        MenuBar menuBar = new MenuBar();
        hor.add(menuBar);
        menuBar.setOpenOnHover(true);
        MenuItem share = menuBar.addItem("Share");
        SubMenu shareSubMenu = share.getSubMenu();
        shareSubMenu.addItem("Upload Staff Data");
        shareSubMenu.addItem("Download Staff Data");
        /*        shareSubMenu.addItem("Import Staff Data").addClickListener(e -> {
        if (!comboBoxBudget.isEmpty()||!comboBoxBudget.getValue().isActive()) {
        if (sampleStaffSalaryService.findByBudget(comboBoxBudget.getValue()).size() == 0) {
        List<OldStaffPojo> getStaffByFiscalYear = oldStaffPojoService.getStaffByFiscalYear(comboBoxBudget.getValue().getFinancialYear());
        for (OldStaffPojo s : getStaffByFiscalYear) {
        StaffSalary salary = new StaffSalary();
        salary.setFname(s.getFirstName());
        salary.setLname(s.getLastName());
        salary.setTel(s.getTelephone());
        salary.setMob(s.getMobile());
        salary.setAddress(s.getAddress());
        salary.setAddress2(s.getAddress2());
        salary.setPosition(s.getPosition());
        salary.setCode(s.getCode());
        salary.setGrade(GetScale(s.getGrade()));
        salary.setBudget(comboBoxBudget.getValue());
        salary.setSalary(s.getSalary());
        sampleStaffSalaryService.saveStaffSalary(salary);
        }
        } else {
        warningNotification("Items Found: " + sampleStaffSalaryService.findByBudget(comboBoxBudget.getValue()).size());
        }
        setSalaryGrid2();
        } else {
        warningNotification("Select Budget");
        }
        });*/
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        upload = new Upload(buffer);
        upload.setAcceptedFileTypes(
                // Microsoft Excel (OpenXML, .xlsx)
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                ".xlsx");

        UploadExamplesI18N i18n = new UploadExamplesI18N();
        i18n.getAddFiles().setOne("Upload Spreadsheet...");
        i18n.getDropFiles().setOne("Drop spreadsheet here");
        i18n.getError().setIncorrectFileType(
                "Provide the file in one of the supported formats (.xls, .xlsx, .csv).");
        upload.setI18n(i18n);
        upload.setVisible(true);
        if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty()) {

        } else {
            upload.setVisible(true);
        }
        upload.addSucceededListener(event -> {
            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty()) {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream(fileName);
                //System.out.println("Uploaded");
                extractStaffSalaryFromCell2(inputStream);
            } else {
                Notification.show("Select all the required parameters");
            }

        });
        FormLayout formLayout = new FormLayout();
        formLayout.add(comboBoxBudget, comboBoxD_Section, comboBoxOrganisation, comboBoxUrc_Activities, budgetItemfundSource, upload);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new ResponsiveStep("320px", 3),
                // Use three columns, if the layout's width exceeds 500px
                new ResponsiveStep("500px", 4));
        splitLayout.setSplitterPosition(50);
        lay.setHeight("100%");
        splitLayout.setHeight("100%");
        gridStaffSalary.setHeight("100%");
        Footer footer = new Footer();
        footer.getElement().getStyle().set("margin-left", "auto");
        span.getElement().getStyle().set("margin-left", "auto");
        span.setWidthFull();
        footer.getElement().getThemeList().add("badge success");
        footer.add(span);

        // Make the SplitLayout components take up the remaining space
        splitLayout.addToPrimary(gridStaffSalary, footer);
        splitLayout.addToSecondary(volumesDetails());
        lay.add(hor, formLayout, splitLayout);

        return lay;
    }

    private VerticalLayout secondPanel() {
        VerticalLayout lay = new VerticalLayout();
        //lay.add(new H1("hOW ARE YOU?"));
        return lay;
    }

    private void populateForm(StaffSalary value) {
        this.salaries = value;
        binder.readBean(this.salaries);

    }

    private void clearForm() {

        fname.clear();
        lname.clear();
        tel.clear();
        mob.clear();
        Address.clear();
        Address2.clear();
        position.clear();
        code.clear();
        grade.clear();
        salaryz.clear();
        email.clear();
        nextofkin.clear();
    }

    public BigDecimal calculateTotalTons() {
        BigDecimal total = BigDecimal.ZERO;

        return total;
    }

    public BigDecimal calculateMonthSum(BudgetItems budget) {
        BigDecimal sum = BigDecimal.ZERO; // Initialize sum to zero

        // Add up the values of all the month fields
        sum = sum.add(budget.getJan());
        sum = sum.add(budget.getFeb());
        sum = sum.add(budget.getMar());
        sum = sum.add(budget.getApr());
        sum = sum.add(budget.getMay());
        sum = sum.add(budget.getJun());
        sum = sum.add(budget.getJul());
        sum = sum.add(budget.getAug());
        sum = sum.add(budget.getSep());
        sum = sum.add(budget.getOct());
        sum = sum.add(budget.getNov());
        sum = sum.add(budget.getDec());

        return sum;
    }

    public Notification warningNotification(String error) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(new Text(error), new HtmlComponent("br"), new Text("Close this warning to continue working."));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
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

    public void setDetails(StaffSalary s) {
        if (s != null) {
            if (s.getFname() != null) {
                fname.setValue(s.getFname());
            }
            if (s.getLname() != null) {
                lname.setValue(s.getLname());
            }
            if (s.getTel() != null) {
                tel.setValue(s.getTel());
            }
            if (s.getMob() != null) {
                mob.setValue(s.getMob());
            }
            if (s.getAddress() != null) {
                Address.setValue(s.getAddress());
            }
            if (s.getAddress2() != null) {
                Address2.setValue(s.getAddress2());
            }
            if (s.getNextofkin() != null) {
                nextofkin.setValue(s.getNextofkin());
            }
            if (s.getEmail() != null) {
                email.setValue(s.getEmail());
            }
            if (s.getPosition() != null) {
                position.setValue(s.getPosition());
            }
            if (s.getGrade() != null) {
                grade.setValue(s.getGrade());
            }
            if (s.getCode() != null) {
                code.setValue(s.getCode());
            }
            if (s.getDeptUnit() != null) {
                comboBoxD_Section.setValue(s.getDeptUnit());
            }
            if (s.getBudgetType() != null) {
                comboBoxOrganisation.setValue(s.getBudgetType());
            }
            if (s.getBudget() != null) {
                comboBoxBudget.setValue(s.getBudget());
            }
            if (s.getActivity() != null) {
                comboBoxUrc_Activities.setValue(s.getActivity());
            }
            if (s.getSalary() != null) {
                salaryz.setValue(s.getSalary());
            }
        } else {
            // Handle the case when the input object s is null
            // For example, you might want to clear all fields or show an error message.
        }
    }

    public void extractStaffSalaryFromCell2(InputStream inputStream) {

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int i = 0;
            List<errorMessages> messages = new ArrayList<>();
            List<StaffSalary> listStaffSalary = new ArrayList();
            for (Row row : sheet) {
                i++;
                if (i > 1) {
                    StaffSalary info = new StaffSalary();
                    BudgetItems budget = new BudgetItems();

                    handleCell(row, messages, info, i, 0, "Null Staff Code Value", (code) -> {
                        code.setCellType(CellType.STRING);
                        info.setCode(code.getStringCellValue());
                        info.setBudget(comboBoxBudget.getValue());
                    });

                    handleCell(row, messages, info, i, 1, "Null Last Name Value", (lname) -> {
                        info.setLname(lname.getStringCellValue());
                    });

                    handleCell(row, messages, info, i, 2, "Null First Name Value", (fname) -> {
                        info.setFname(fname.getStringCellValue());
                    });
                    //handle null rate cell
                    handleCell(row, messages, info, i, 3, "Null Position Value", (position) -> {
                        info.setPosition(position.getStringCellValue());
                    });
                    handleGradeCell(row, messages, info, i, 4, "Null Position Value", (grade) -> {
                        info.setGrade(GetScale(grade.getStringCellValue()));
                    });
                    Cell tel = row.getCell(5);
                    if (tel != null) {
                        tel.setCellType(CellType.STRING);
                        info.setTel(tel.getStringCellValue());
                    } else {
                        info.setTel(null);
                    }
                    Cell mob = row.getCell(6);
                    if (mob != null) {
                        mob.setCellType(CellType.STRING);
                        info.setMob(mob.getStringCellValue());
                    } else {
                        info.setMob(null);
                    }
                    Cell address = row.getCell(7);
                    if (address != null) {
                        address.setCellType(CellType.STRING);
                        info.setAddress(address.getStringCellValue());
                    } else {
                        info.setAddress(null);
                    }
                    Cell nok = row.getCell(8);
                    if (nok != null) {
                        nok.setCellType(CellType.STRING);
                        info.setNextofkin(nok.getStringCellValue());
                    } else {
                        info.setNextofkin(null);
                    }
                    Cell email = row.getCell(9);
                    if (email != null) {
                        email.setCellType(CellType.STRING);
                        info.setEmail(email.getStringCellValue());
                    } else {
                        info.setEmail(null);
                    }
                    handleNumericCell(row, messages, info, i, 10, "Use Numeric Value", (salary) -> {
                        Cell cell = row.getCell(10);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);
                            System.out.println(cell.getRichStringCellValue());

                            try {
                                info.setSalary(new BigDecimal(salary.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            // info.setRate(test);
                        }

                    });

                    listStaffSalary.add(info);
                }
            }
            if (messages.isEmpty()) {
                int x = 0;
                for (StaffSalary a : listStaffSalary) {
                    x++;
                    System.out.println(x + ": " + a.getSalary());
                    BudgetItems budget = new BudgetItems();
                    System.out.println(a);
                    if (sampleStaffSalaryService.saveStaffSalary(a) != null) {
                        itemsalaryBudget(a, comboBoxOrganisation.getValue());
                    }
                }
                if (!comboBoxBudget.isEmpty()) {
                    setSalaryGrid2();
                }
            } else {
                warningNotification(messages);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleNumericCell(Row row, List<errorMessages> messages, StaffSalary info, int rowIndex, int columnIndex, String errorMessage, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        BigDecimal test = BigDecimal.ZERO;
        if (cell != null) {
            cell.setCellType(CellType.STRING);

            try {
                test = new BigDecimal(cell.getStringCellValue());
                if (columnIndex == 10) {
                    info.setSalary(test);
                }

            } catch (NumberFormatException ex) {
                handleNumericError(messages, rowIndex, columnIndex, errorMessage);
            }
        } else {

        }
    }

    private void handleGradeCell(Row row, List<errorMessages> messages, StaffSalary info, int rowIndex, int columnIndex, String errorMessage, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            info.setGrade(GetScale(cell.getStringCellValue()));
            if (info.getGrade() == null) {
                handleNullCell(messages, rowIndex, columnIndex, "Wrong Salary Grade");
            }
        } else {
            handleNullCell(messages, rowIndex, columnIndex, errorMessage);
        }
    }

    private void handleCell(Row row, List<errorMessages> messages, StaffSalary info, int rowIndex, int columnIndex, String errorMessage, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            handler.handle(cell);
        } else {
            handleNullCell(messages, rowIndex, columnIndex, errorMessage);
        }
    }

    private void handleNullCell(List<errorMessages> messages, int rowIndex, int columnIndex, String errorMessage) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     " + errorMessage);
        messages.add(error);
    }

    private void handleNumericError(List<errorMessages> messages, int rowIndex, int columnIndex, String errorMessage) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     " + errorMessage);
        messages.add(error);
    }

    private void handleNullGradeError(List<errorMessages> messages, int rowIndex, int columnIndex, String errorMessage) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     " + errorMessage);
        messages.add(error);
    }

    public Notification warningNotification(List<errorMessages> messages) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        Grid<errorMessages> errors = new Grid<>(errorMessages.class, false);
        errors.addColumn(errorMessages::getRow).setHeader("Row");
        errors.addColumn(errorMessages::getMessage).setHeader("Warning");
        errors.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        errors.getStyle().set("background-color", "#ffcc00");

        errors.setItems(messages);
        Div text = new Div(
                errors,
                new HtmlComponent("br"),
                new Text("Close this warning to continue working.")
        );

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }

    @FunctionalInterface
    private interface CellHandler {

        void handle(Cell cell);
    }
}
