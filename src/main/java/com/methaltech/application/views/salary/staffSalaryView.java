package com.methaltech.application.views.salary;

import com.methaltech.application.data.UploadExamplesI18N;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.FreightVolumesService;
import com.methaltech.application.data.bgtool.service.FundsourceService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.StaffService;
import com.methaltech.application.data.bgtool.service.StaffSalaryService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.Staff;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.errorMessages;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
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

    private record SalaryBudgetRegenerationResult(int salaryGradeRows, BigDecimal monthlyTotal) {
    }

    private final FreightVolumesService sampleFreightVolumesService;
    private final BudgetService sampleBudgetService;
    private final CoaService sampleCoaService;
    private ComboBox<Budget> comboBoxBudget = new ComboBox<>("Budget");
    private ComboBox<COA> comboBoxCOA = new ComboBox<>("Freight Route");
    private ComboBox<Urc_Activities> comboBoxUrc_Activities = new ComboBox("Activities");
    private Grid<StaffSalary> gridStaffSalary = new Grid<>(StaffSalary.class, false);
    private final CurrencyService sampleCurrencyService;
    private final BudgetItemsService budgetItemsService;
    private final OrganisationService sampleOrganisationService;
    private final BudgetItemsService sampleBudgetItemsService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final StaffSalaryService sampleStaffSalaryService;
    private final StaffService staffService;

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
    private Button deleteAll = new Button("Delete All");
    private Button cancel = new Button("Cancel");
    private Button loadStaffMaster = new Button("Load Staff Master");
    Span span = new Span();

    SplitLayout splitLayout = new SplitLayout();
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US); // Use US locale for consistent formatting

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
    private AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private User user;
    private final Coalevel1Service coalevel1Service;

    private ComboBox<Fundsource> budgetItemfundSource = new ComboBox<>("Fund Source");
    private final FundsourceService sampleFundsourceService;
    private Upload upload;

    public staffSalaryView(AuthenticatedUser authenticatedUser, FreightVolumesService sampleFreightVolumesService, BudgetService sampleBudgetService, CoaService sampleCoaService,
            CurrencyService sampleCurrencyService, BudgetItemsService budgetItemsService,
            OrganisationService sampleOrganisationService, UserService userService, BudgetItemsService sampleBudgetItemsService,
            Urc_ActivitiesService sampleUrc_ActivitiesService, StaffSalaryService sampleStaffSalaryService,
            StaffService staffService, Coalevel1Service coalevel1Service, FundsourceService sampleFundsourceService) {
        this.sampleFreightVolumesService = sampleFreightVolumesService;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleCoaService = sampleCoaService;
        this.sampleCurrencyService = sampleCurrencyService;
        this.budgetItemsService = budgetItemsService;
        this.sampleOrganisationService = sampleOrganisationService;
        this.userService = userService;
        this.sampleBudgetItemsService = sampleBudgetItemsService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.sampleStaffSalaryService = sampleStaffSalaryService;
        this.staffService = staffService;
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
            if (e.getValue() == null) {
                save.setEnabled(false);
                cancel.setEnabled(false);
                delete.setEnabled(false);
                deleteAll.setEnabled(false);
                loadStaffMaster.setEnabled(false);
                if (upload != null) {
                    upload.setVisible(false);
                }
                return;
            }
            if (!e.getValue().isActive()) {
                save.setEnabled(false);
                cancel.setEnabled(false);
                delete.setEnabled(false);
                deleteAll.setEnabled(false);
                loadStaffMaster.setEnabled(false);
                upload.setVisible(false);
            } else {
                save.setEnabled(true);
                cancel.setEnabled(true);
                delete.setEnabled(true);
                deleteAll.setEnabled(true);
                loadStaffMaster.setEnabled(true);
                upload.setVisible(true);
            }
            budgetItemfundSource.setItems(sampleFundsourceService.findFundsourcesByBudget(e.getValue()));
            setSalaryGrid2();
            if (!comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                comboBoxUrc_Activities.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudget(comboBoxD_Section.getValue(), comboBoxBudget.getValue()));
                // setSalaryGrid2();
            }

            comboBoxOrganisation.setItems(sampleOrganisationService.getOrganisationsByBudget(comboBoxBudget.getValue()));
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
                    deleteStaffAndRegenerateSelectedSalaryBudgetItems(comboBoxBudget.getValue(), salary);
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
        deleteAll.addClickListener(e -> {
            if (!comboBoxBudget.isEmpty()) {
                Dialog dialog = new Dialog();

                dialog.setHeaderTitle("Permanently Delete this staff data and budget");
                dialog.add("Are you sure you want to delete this staff data permanently?");

                Button deleteButton = new Button("Delete");
                deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                deleteButton.getStyle().set("margin-right", "auto");
                dialog.getFooter().add(deleteButton);

                deleteButton.addClickListener(es -> {
                    deleteAllItemsalaryBudget();
                    setSalaryGrid2();
                    clearForm();
                    dialog.close();
                });

                Button cancelButton = new Button("Cancel", (ez) -> dialog.close());
                cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                dialog.getFooter().add(cancelButton);
                dialog.open();

            } else {
                Notification.show("Select a Financial Year");
            }
        });

        save.addClickListener(event -> {

            if (hasSelectedSalaryContext() && !code.isEmpty() && !fname.isEmpty()
                    && !lname.isEmpty() && !grade.isEmpty() && !salaryz.isEmpty()) {

                StaffSalary salary = gridStaffSalary.asSingleSelect().getValue();
                if (salary == null) {
                    salary = new StaffSalary();
                }
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
                SalaryBudgetRegenerationResult regenerationResult = regenerateSelectedSalaryBudgetItems(comboBoxBudget.getValue());

                //setCOACombo(comboBoxBudget.getValue());
                setSalaryGrid2();

                clearForm();
                showSalaryBudgetRegenerationNotification(regenerationResult);
            } else {
                warningNotification("Select budget context and fill Staff Code, First Name, Last Name, Level and Salary.");
            }

        });
        cancel.addClickListener(event -> cancel());
        loadStaffMaster.addClickListener(event -> importStaffFromMaster());
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
        ho.add(save, delete, deleteAll, loadStaffMaster, cancel);
        form.add(code, fname, lname, email, tel, mob, position, grade, Address,
                Address2, nextofkin,
                salaryz, ho
        );

        // binder.bindInstanceFields(this);
        return form;
    }

    public void deleteAllItemsalaryBudget() {
        COA salary_wages = sampleCoaService.findByCodeAndBudget("211101", comboBoxBudget.getValue());
        COA nssf = sampleCoaService.findByCodeAndBudget("212101", comboBoxBudget.getValue());
        COA gratuity = sampleCoaService.findByCodeAndBudget("213004", comboBoxBudget.getValue());
        COA workmancompesation = sampleCoaService.findByCodeAndBudget("213005", comboBoxBudget.getValue());

        List<COA> coaList = new ArrayList();
        coaList.add(salary_wages);
        coaList.add(nssf);
        coaList.add(gratuity);
        coaList.add(workmancompesation);
        int deleted = budgetItemsService.deleteByBudgetAndCoas(comboBoxBudget.getValue(), coaList);
        sampleStaffSalaryService.deleteByBudget(comboBoxBudget.getValue());

    }

    private void deleteSelectedSalaryBudgetItems() {
        COA salary_wages = sampleCoaService.findByCodeAndBudget("211101", comboBoxBudget.getValue());
        COA nssf = sampleCoaService.findByCodeAndBudget("212101", comboBoxBudget.getValue());
        COA gratuity = sampleCoaService.findByCodeAndBudget("213004", comboBoxBudget.getValue());
        COA workmancompesation = sampleCoaService.findByCodeAndBudget("213005", comboBoxBudget.getValue());

        List<COA> coaList = new ArrayList();
        coaList.add(salary_wages);
        coaList.add(nssf);
        coaList.add(gratuity);
        coaList.add(workmancompesation);
        int deleted = budgetItemsService.deleteByBudgetAndCoasAndSalaryContext(
                comboBoxBudget.getValue(), coaList, comboBoxD_Section.getValue(),
                comboBoxOrganisation.getValue(), comboBoxUrc_Activities.getValue(),
                budgetItemfundSource.getValue());

    }

    private void importStaffFromMaster() {
        if (!hasSelectedSalaryContext()) {
            warningNotification("Select Budget, Cost Centre, Budget Type, Activity and Fund Source before loading Staff Master.");
            return;
        }

        Budget budget = comboBoxBudget.getValue();
        List<Staff> masterStaff = staffService.listByFinancialYear(budget.getFinancialYear());
        if (masterStaff.isEmpty()) {
            warningNotification("No Staff Master records found for " + budget.getFinancialYear() + ".");
            return;
        }

        List<StaffSalary> salariesToSave = new ArrayList<>();
        Set<String> importedCodes = new HashSet<>();
        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (Staff staff : masterStaff) {
            String staffCode = clean(staff.getCode());
            salaryScale staffGrade = GetScale(staff.getGrade());
            if (staffCode == null || staff.getSalary() == null || staffGrade == null || !importedCodes.add(staffCode)) {
                skipped++;
                continue;
            }

            Optional<StaffSalary> existingSalary = sampleStaffSalaryService.findByBudgetAndCode(budget, staffCode);
            StaffSalary salary = existingSalary.orElseGet(StaffSalary::new);
            if (existingSalary.isPresent()) {
                updated++;
            } else {
                created++;
            }

            copyStaffToSalary(staff, salary, staffGrade);
            salariesToSave.add(salary);
        }

        if (salariesToSave.isEmpty()) {
            warningNotification("No valid Staff Master rows were loaded. Check staff code, grade and salary values.");
            return;
        }

        sampleStaffSalaryService.saveStaffSalary(salariesToSave);
        SalaryBudgetRegenerationResult regenerationResult = regenerateSelectedSalaryBudgetItems(budget);
        setSalaryGrid2();
        clearForm();

        Notification notification = Notification.show("Loaded Staff Master: " + created
                + " created, " + updated + " updated, " + skipped + " skipped. "
                + formatSalaryBudgetRegeneration(regenerationResult));
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private boolean hasSelectedSalaryContext() {
        return !comboBoxBudget.isEmpty()
                && !comboBoxD_Section.isEmpty()
                && !comboBoxOrganisation.isEmpty()
                && !comboBoxUrc_Activities.isEmpty()
                && !budgetItemfundSource.isEmpty();
    }

    private void copyStaffToSalary(Staff staff, StaffSalary salary, salaryScale staffGrade) {
        salary.setCode(clean(staff.getCode()));
        salary.setFname(clean(staff.getFname()));
        salary.setLname(clean(staff.getLname()));
        salary.setTel(clean(staff.getTel()));
        salary.setMob(clean(staff.getMob()));
        salary.setAddress(clean(staff.getAddress()));
        salary.setAddress2(clean(staff.getAddress2()));
        salary.setNextofkin(clean(staff.getNextOfKin()));
        salary.setEmail(clean(staff.getEmail()));
        salary.setPosition(clean(staff.getPosition()));
        salary.setContract(clean(staff.getContract()));
        salary.setGrade(staffGrade);
        salary.setSalary(staff.getSalary());
        applySelectedSalaryContext(salary);
    }

    private void applySelectedSalaryContext(StaffSalary salary) {
        salary.setBudget(comboBoxBudget.getValue());
        salary.setDeptUnit(comboBoxD_Section.getValue());
        salary.setBudgetType(comboBoxOrganisation.getValue());
        salary.setActivity(comboBoxUrc_Activities.getValue());
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
        if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxUrc_Activities.isEmpty()) {

        } else {
            upload.setVisible(true);
        }
        upload.addSucceededListener(event -> {
            if (hasSelectedSalaryContext()) {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream(fileName);
                //System.out.println("Uploaded");
                extractStaffSalaryFromCell2(inputStream);
            } else {
                Notification.show("Select Budget, Cost Centre, Budget Type, Activity and Fund Source");
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
        if (s == null) {
            return null;
        }

        String normalized = s.trim()
                .toUpperCase(Locale.ROOT)
                .replace("_", " ")
                .replaceAll("\\s+", " ");

        if (normalized.equals("RG 1")) {
            return salaryScale.RG_1;
        } else if (normalized.equals("RG 2")) {
            return salaryScale.RG_2;
        } else if (normalized.equals("RG 3")) {
            return salaryScale.RG_3;
        } else if (normalized.equals("RG 4")) {
            return salaryScale.RG_4;
        } else if (normalized.equals("RG 5")) {
            return salaryScale.RG_5;
        } else if (normalized.equals("RG 6")) {
            return salaryScale.RG_6;
        } else if (normalized.equals("RG 7")) {
            return salaryScale.RG_7;
        } else if (normalized.equals("RG 8")) {
            return salaryScale.RG_8;
        } else if (normalized.equals("RG 9")) {
            return salaryScale.RG_9;
        } else if (normalized.equals("EXEC 1")) {
            return salaryScale.EXEC_1;
        } else if (normalized.equals("EXEC 2")) {
            return salaryScale.EXEC_2;
        }

        return null;
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

                            try {
                                info.setSalary(new BigDecimal(salary.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            // info.setRate(test);
                        }

                    });

                    applySelectedSalaryContext(info);
                    listStaffSalary.add(info);
                }
            }
            if (messages.isEmpty()) {
                sampleStaffSalaryService.saveStaffSalary(listStaffSalary);
                SalaryBudgetRegenerationResult regenerationResult = regenerateSelectedSalaryBudgetItems(comboBoxBudget.getValue());
                if (!comboBoxBudget.isEmpty()) {
                    setSalaryGrid2();
                }
                showSalaryBudgetRegenerationNotification(regenerationResult);
            } else {
                warningNotification(messages);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteStaffAndRegenerateSelectedSalaryBudgetItems(Budget budget, StaffSalary staff) {
        sampleStaffSalaryService.deleteBystaff(staff);
        SalaryBudgetRegenerationResult regenerationResult = regenerateSelectedSalaryBudgetItems(budget);
        showSalaryBudgetRegenerationNotification(regenerationResult);
    }

    private SalaryBudgetRegenerationResult regenerateSelectedSalaryBudgetItems(Budget budget) {
        if (budget == null || !hasSelectedSalaryContext()) {
            return new SalaryBudgetRegenerationResult(0, BigDecimal.ZERO);
        }

        COA salaryWages = sampleCoaService.findByCodeAndBudget("211101", budget);
        COA nssf = sampleCoaService.findByCodeAndBudget("212101", budget);
        COA gratuity = sampleCoaService.findByCodeAndBudget("213004", budget);
        COA workmanCompensation = sampleCoaService.findByCodeAndBudget("213005", budget);
        Currency cur = sampleCurrencyService.findCurrenciesByCurrencyShortAndBudget("UGX", budget);
        Coalevel1 coaLevel1 = coalevel1Service.findByCode(2);

        if (salaryWages == null || nssf == null || gratuity == null || workmanCompensation == null || cur == null || coaLevel1 == null) {
            warningNotification("Salary COA setup is incomplete for this budget.");
            return new SalaryBudgetRegenerationResult(0, BigDecimal.ZERO);
        }

        deleteSelectedSalaryBudgetItems();

        List<StaffSalary> salaries = sampleStaffSalaryService.findByBudgetAndDeptUnitAndBudgetTypeAndActivity(
                budget, comboBoxD_Section.getValue(), comboBoxOrganisation.getValue(), comboBoxUrc_Activities.getValue());
        List<StaffSalary> aggregateSalaryByGrade = sampleStaffSalaryService.aggregateSalaryByGrade(salaries);
        BigDecimal monthlyTotal = BigDecimal.ZERO;
        for (StaffSalary a : aggregateSalaryByGrade) {
            a.setBudget(budget);
            BudgetItems salaryItem = createSalaryBudgetItem(a, salaryWages, cur, coaLevel1);
            sampleBudgetItemsService.update(salaryItem);
            monthlyTotal = monthlyTotal.add(a.getSalary());
        }

        if (monthlyTotal.compareTo(BigDecimal.ZERO) > 0) {
            sampleBudgetItemsService.update(createBenefitBudgetItem(nssf, cur, coaLevel1, monthlyTotal, new BigDecimal("0.10")));
            sampleBudgetItemsService.update(createBenefitBudgetItem(gratuity, cur, coaLevel1, monthlyTotal, new BigDecimal("0.25")));
            sampleBudgetItemsService.update(createBenefitBudgetItem(workmanCompensation, cur, coaLevel1, monthlyTotal, new BigDecimal("0.03")));
        }

        return new SalaryBudgetRegenerationResult(aggregateSalaryByGrade.size(), monthlyTotal);
    }

    private void showSalaryBudgetRegenerationNotification(SalaryBudgetRegenerationResult result) {
        Notification notification = Notification.show(formatSalaryBudgetRegeneration(result));
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private String formatSalaryBudgetRegeneration(SalaryBudgetRegenerationResult result) {
        return "Generated " + result.salaryGradeRows() + " salary grade item(s); monthly salary total "
                + decimalFormat.format(result.monthlyTotal()) + ".";
    }

    private BudgetItems createSalaryBudgetItem(StaffSalary salary, COA salaryWages, Currency cur, Coalevel1 coaLevel1) {
        BudgetItems item = createBaseSalaryBudgetItem(salary.getFname() + " Salary", salaryWages, cur, coaLevel1);
        item.setCost(salary.getSalary());
        item.setQty(new BigDecimal("12"));
        setMonthlyAmounts(item, salary.getSalary());
        item.setGrade(salary.getGrade());
        return item;
    }

    private BudgetItems createBenefitBudgetItem(COA coa, Currency cur, Coalevel1 coaLevel1,
            BigDecimal monthlySalaryTotal, BigDecimal rate) {
        BigDecimal monthlyAmount = monthlySalaryTotal.multiply(rate);
        BudgetItems item = createBaseSalaryBudgetItem(coa.getName(), coa, cur, coaLevel1);
        item.setCost(monthlyAmount);
        item.setQty(new BigDecimal("12"));
        setMonthlyAmounts(item, monthlyAmount);
        return item;
    }

    private BudgetItems createBaseSalaryBudgetItem(String itemName, COA coa, Currency cur, Coalevel1 coaLevel1) {
        BudgetItems item = new BudgetItems();
        item.setItem(itemName);
        item.setUnitMeasure("MONTH");
        item.setCurrency(cur);
        item.setBudget(comboBoxBudget.getValue());
        item.setBudgetType(comboBoxOrganisation.getValue());
        item.setCoacode(coa);
        item.setDeptUnit(comboBoxD_Section.getValue());
        item.setFundsource(budgetItemfundSource.getValue());
        item.setActivity(comboBoxUrc_Activities.getValue());
        item.setBcategory(coa.getCode());
        item.setCoalevel1(coaLevel1);
        return item;
    }

    private void setMonthlyAmounts(BudgetItems item, BigDecimal monthlyAmount) {
        item.setJan(monthlyAmount);
        item.setFeb(monthlyAmount);
        item.setMar(monthlyAmount);
        item.setApr(monthlyAmount);
        item.setMay(monthlyAmount);
        item.setJun(monthlyAmount);
        item.setJul(monthlyAmount);
        item.setAug(monthlyAmount);
        item.setSep(monthlyAmount);
        item.setOct(monthlyAmount);
        item.setNov(monthlyAmount);
        item.setDec(monthlyAmount);
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
            handleNumericError(messages, rowIndex, columnIndex, errorMessage);
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
