package com.methaltech.application.views.staff;

import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.StaffService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Staff;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;

@PageTitle("Staff Master")
@Route(value = "staff-master", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "HR"})
@Uses(Icon.class)
public class StaffMasterView extends Div {

    private static final String[] GRADE_OPTIONS = {
        "EXEC 1", "EXEC 2", "RG 1", "RG 2", "RG 3", "RG 4",
        "RG 5", "RG 6", "RG 7", "RG 8", "RG 9"
    };
    private static final List<String> STAFF_EXPORT_HEADERS = List.of(
            "Financial Year", "Staff Code", "Last Name", "First Name", "Position", "Grade",
            "Monthly Salary", "Email", "Telephone", "Mobile", "Contract", "Appointment Date",
            "Departure Date", "Primary Address", "Address 2", "Next of Kin"
    );

    private final BudgetService budgetService;
    private final StaffService staffService;
    private final BeanValidationBinder<Staff> binder = new BeanValidationBinder<>(Staff.class);
    private final Grid<Staff> grid = new Grid<>(Staff.class, false);
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.US));

    private final ComboBox<Budget> budget = new ComboBox<>("Financial Year");
    private final TextField searchField = new TextField();
    private final TextField code = new TextField("Staff Code");
    private final TextField fname = new TextField("First Name");
    private final TextField lname = new TextField("Last Name");
    private final TextField email = new TextField("Email");
    private final TextField tel = new TextField("Telephone");
    private final TextField mob = new TextField("Mobile");
    private final TextField position = new TextField("Position");
    private final ComboBox<String> grade = new ComboBox<>("Level");
    private final BigDecimalField salary = new BigDecimalField("Monthly Salary");
    private final TextField contract = new TextField("Contract");
    private final DatePicker appointment = new DatePicker("Appointment Date");
    private final DatePicker departure = new DatePicker("Departure Date");
    private final TextField address = new TextField("Primary Address");
    private final TextField address2 = new TextField("Address 2");
    private final TextField nextOfKin = new TextField("Next of Kin");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button newStaff = new Button("New Staff");
    private final Button downloadStaff = new Button("Download Excel", VaadinIcon.DOWNLOAD.create());
    private final MemoryBuffer uploadBuffer = new MemoryBuffer();
    private final Upload upload = new Upload(uploadBuffer);
    private final Footer footer = new Footer();

    private Staff selectedStaff;

    public StaffMasterView(BudgetService budgetService, StaffService staffService) {
        this.budgetService = budgetService;
        this.staffService = staffService;

        addClassNames("user-view");
        setHeightFull();

        Image image = new Image("images/ugflagstrip.png", "Strip");
        image.setWidthFull();
        image.getStyle().set("margin", "0").set("padding", "0");
        add(image);

        configureBudgetSelector();
        configureGrid();
        configureForm();
        configureActions();

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setHeightFull();
        splitLayout.setSplitterPosition(68);
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(createFilterLayout(), splitLayout);
        refreshGrid();
    }

    private void configureBudgetSelector() {
        budget.setItems(query -> budgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        budget.setItemLabelGenerator(Budget::getFinancialYear);
        budget.setClearButtonVisible(true);
        budget.setRequiredIndicatorVisible(true);
        budget.addValueChangeListener(event -> {
            clearForm();
            refreshGrid();
        });
    }

    private void configureGrid() {
        grid.addColumn(Staff::getCode)
                .setHeader("Staff Code")
                .setAutoWidth(true)
                .setSortable(true);
        grid.addColumn(Staff::getFname)
                .setHeader("First Name")
                .setAutoWidth(true)
                .setSortable(true);
        grid.addColumn(Staff::getLname)
                .setHeader("Last Name")
                .setAutoWidth(true)
                .setSortable(true);
        grid.addColumn(Staff::getPosition)
                .setHeader("Position")
                .setAutoWidth(true);
        grid.addColumn(Staff::getGrade)
                .setHeader("Level")
                .setAutoWidth(true);
        grid.addColumn(staff -> formatAmount(staff.getSalary()))
                .setHeader("Monthly Salary")
                .setAutoWidth(true);
        grid.addColumn(Staff::getEmail)
                .setHeader("Email")
                .setAutoWidth(true);
        grid.addColumn(Staff::getTel)
                .setHeader("Telephone")
                .setAutoWidth(true);

        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.asSingleSelect().addValueChangeListener(event -> {
            Staff staff = event.getValue();
            if (staff == null) {
                clearForm();
                return;
            }
            selectedStaff = staff;
            binder.readBean(staff);
            delete.setEnabled(true);
        });
    }

    private void configureForm() {
        setRequired(code, fname, lname);
        grade.setItems(GRADE_OPTIONS);
        grade.setClearButtonVisible(true);
        salary.setPrefixComponent(new Text("UGX"));
        salary.setClearButtonVisible(true);

        binder.forField(code)
                .asRequired("Staff code is required")
                .bind(Staff::getCode, Staff::setCode);
        binder.forField(fname)
                .asRequired("First name is required")
                .bind(Staff::getFname, Staff::setFname);
        binder.forField(lname)
                .asRequired("Last name is required")
                .bind(Staff::getLname, Staff::setLname);
        binder.forField(email).bind(Staff::getEmail, Staff::setEmail);
        binder.forField(tel).bind(Staff::getTel, Staff::setTel);
        binder.forField(mob).bind(Staff::getMob, Staff::setMob);
        binder.forField(position).bind(Staff::getPosition, Staff::setPosition);
        binder.forField(grade).bind(Staff::getGrade, Staff::setGrade);
        binder.forField(salary)
                .asRequired("Monthly salary is required")
                .bind(Staff::getSalary, Staff::setSalary);
        binder.forField(contract).bind(Staff::getContract, Staff::setContract);
        binder.forField(appointment)
                .bind(staff -> toLocalDate(staff.getAppointment()),
                        (staff, value) -> staff.setAppointment(toDate(value)));
        binder.forField(departure)
                .bind(staff -> toLocalDate(staff.getDeparture()),
                        (staff, value) -> staff.setDeparture(toDate(value)));
        binder.forField(address).bind(Staff::getAddress, Staff::setAddress);
        binder.forField(address2).bind(Staff::getAddress2, Staff::setAddress2);
        binder.forField(nextOfKin).bind(Staff::getNextOfKin, Staff::setNextOfKin);
    }

    private void configureActions() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        newStaff.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        downloadStaff.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        delete.setEnabled(false);

        upload.setAcceptedFileTypes(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                ".xlsx");
        upload.setMaxFiles(1);
        upload.setDropAllowed(false);
        upload.setUploadButton(new Button("Upload Excel", VaadinIcon.UPLOAD.create()));
        upload.addSucceededListener(event -> importStaffWorkbook(uploadBuffer.getInputStream()));

        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setPlaceholder("Search staff code, name, position, or email");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> refreshGrid());

        newStaff.addClickListener(event -> {
            grid.deselectAll();
            clearForm();
        });
        cancel.addClickListener(event -> {
            grid.deselectAll();
            clearForm();
        });
        save.addClickListener(event -> saveStaff());
        delete.addClickListener(event -> confirmDelete());
        downloadStaff.addClickListener(event -> downloadStaffWorkbook());
    }

    private HorizontalLayout createFilterLayout() {
        budget.setWidth("260px");
        searchField.setWidthFull();
        HorizontalLayout filters = new HorizontalLayout(budget, searchField, newStaff, upload, downloadStaff);
        filters.setWidthFull();
        filters.setAlignItems(FlexComponent.Alignment.END);
        filters.expand(searchField);
        return filters;
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div(grid, footer);
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();
        footer.getElement().getThemeList().add("badge success");
        footer.getStyle().set("margin-left", "auto");
        splitLayout.addToPrimary(wrapper);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("user-editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("user-editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        formLayout.add(code, fname, lname, email, tel, mob, position, grade, salary,
                contract, appointment, departure, address, address2, nextOfKin);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        HorizontalLayout buttonLayout = new HorizontalLayout(save, delete, cancel);
        buttonLayout.setClassName("button-layout");
        buttonLayout.setWidthFull();

        editorDiv.add(formLayout, buttonLayout);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void saveStaff() {
        if (budget.isEmpty()) {
            warningNotification("Select a financial year before saving staff.");
            return;
        }

        Staff staff = selectedStaff == null ? new Staff() : selectedStaff;
        try {
            binder.writeBean(staff);
            staff.setFy(budget.getValue().getFinancialYear());
            staffService.saveStaff(staff);
            Notification.show("Staff details saved.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            grid.deselectAll();
            clearForm();
            refreshGrid();
        } catch (ValidationException validationException) {
            warningNotification("Fill in the required staff details.");
        }
    }

    private void confirmDelete() {
        if (selectedStaff == null || selectedStaff.getId() == 0) {
            warningNotification("Select staff to delete.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Delete staff \"" + staffName(selectedStaff) + "\"?");
        dialog.add("This removes the staff member from the selected financial-year roster.");

        Button deleteButton = new Button("Delete", event -> {
            staffService.deleteStaff(selectedStaff.getId());
            dialog.close();
            grid.deselectAll();
            clearForm();
            refreshGrid();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(deleteButton, cancelButton);
        dialog.open();
    }

    private void importStaffWorkbook(InputStream inputStream) {
        if (budget.isEmpty()) {
            warningNotification("Select a financial year before uploading Staff Master.");
            return;
        }

        String fy = selectedFinancialYear();
        List<String> errors = new ArrayList<>();
        List<Staff> staffToSave = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();
        DataFormatter formatter = new DataFormatter(Locale.US);

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isBlankRow(row, formatter)) {
                    continue;
                }

                Staff staff = parseStaffRow(row, formatter, fy, rowIndex + 1, errors, seenCodes);
                if (staff != null) {
                    staffToSave.add(staff);
                }
            }
        } catch (IOException ex) {
            warningNotification("Unable to read Staff Master spreadsheet: " + ex.getMessage());
            return;
        }

        if (!errors.isEmpty()) {
            warningNotification(formatImportErrors(errors));
            return;
        }

        if (staffToSave.isEmpty()) {
            warningNotification("No staff rows found in the spreadsheet.");
            return;
        }

        staffService.saveStaff(staffToSave);
        Notification.show("Imported " + staffToSave.size() + " staff records for " + fy + ".")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        grid.deselectAll();
        clearForm();
        refreshGrid();
    }

    private Staff parseStaffRow(Row row, DataFormatter formatter, String selectedFy, int displayRow,
            List<String> errors, Set<String> seenCodes) {
        String rowFy = clean(cellText(row, 0, formatter));
        String staffCode = clean(cellText(row, 1, formatter));
        String lastName = clean(cellText(row, 2, formatter));
        String firstName = clean(cellText(row, 3, formatter));
        String staffGrade = normalizeGrade(cellText(row, 5, formatter));
        BigDecimal monthlySalary = parseAmount(cellText(row, 6, formatter));

        if (rowFy != null && !rowFy.equals(selectedFy)) {
            errors.add("Row " + displayRow + ": financial year must match selected year " + selectedFy + ".");
        }
        if (staffCode == null) {
            errors.add("Row " + displayRow + ": staff code is required.");
        } else if (!seenCodes.add(staffCode.toUpperCase(Locale.ROOT))) {
            errors.add("Row " + displayRow + ": duplicate staff code " + staffCode + " in spreadsheet.");
        }
        if (lastName == null) {
            errors.add("Row " + displayRow + ": last name is required.");
        }
        if (firstName == null) {
            errors.add("Row " + displayRow + ": first name is required.");
        }
        if (staffGrade == null) {
            errors.add("Row " + displayRow + ": grade must be one of " + String.join(", ", GRADE_OPTIONS) + ".");
        }
        if (monthlySalary == null) {
            errors.add("Row " + displayRow + ": monthly salary is required and must be numeric.");
        }

        Date appointmentDate = null;
        Date departureDate = null;
        try {
            appointmentDate = parseDate(row.getCell(11), formatter);
            departureDate = parseDate(row.getCell(12), formatter);
        } catch (IllegalArgumentException ex) {
            errors.add("Row " + displayRow + ": " + ex.getMessage());
        }

        if (!errorsForRow(errors, displayRow).isEmpty()) {
            return null;
        }

        Optional<Staff> existing = staffService.findByFinancialYearAndCode(selectedFy, staffCode);
        Staff staff = existing.orElseGet(Staff::new);
        staff.setFy(selectedFy);
        staff.setCode(staffCode);
        staff.setLname(lastName);
        staff.setFname(firstName);
        staff.setPosition(clean(cellText(row, 4, formatter)));
        staff.setGrade(staffGrade);
        staff.setSalary(monthlySalary);
        staff.setEmail(clean(cellText(row, 7, formatter)));
        staff.setTel(clean(cellText(row, 8, formatter)));
        staff.setMob(clean(cellText(row, 9, formatter)));
        staff.setContract(clean(cellText(row, 10, formatter)));
        staff.setAppointment(appointmentDate);
        staff.setDeparture(departureDate);
        staff.setAddress(clean(cellText(row, 13, formatter)));
        staff.setAddress2(clean(cellText(row, 14, formatter)));
        staff.setNextOfKin(clean(cellText(row, 15, formatter)));
        return staff;
    }

    private void downloadStaffWorkbook() {
        if (budget.isEmpty()) {
            warningNotification("Select a financial year before downloading Staff Master.");
            return;
        }

        String fy = selectedFinancialYear();
        try {
            byte[] data = generateStaffWorkbook(fy, staffService.listByFinancialYear(fy));
            triggerDownload("staff-master-" + safeFileName(fy) + ".xlsx", data);
        } catch (IOException ex) {
            warningNotification("Unable to generate Staff Master spreadsheet: " + ex.getMessage());
        }
    }

    private byte[] generateStaffWorkbook(String fy, List<Staff> staffRows) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Staff Master");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CreationHelper creationHelper = workbook.getCreationHelper();
            DataFormat dataFormat = creationHelper.createDataFormat();
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd"));

            Row header = sheet.createRow(0);
            for (int i = 0; i < STAFF_EXPORT_HEADERS.size(); i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(STAFF_EXPORT_HEADERS.get(i));
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (Staff staff : staffRows) {
                Row row = sheet.createRow(rowIndex++);
                writeStaffRow(row, staff, fy, dateStyle);
            }

            for (int i = 0; i < STAFF_EXPORT_HEADERS.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void writeStaffRow(Row row, Staff staff, String fy, CellStyle dateStyle) {
        row.createCell(0).setCellValue(fy);
        row.createCell(1).setCellValue(valueOrEmpty(staff.getCode()));
        row.createCell(2).setCellValue(valueOrEmpty(staff.getLname()));
        row.createCell(3).setCellValue(valueOrEmpty(staff.getFname()));
        row.createCell(4).setCellValue(valueOrEmpty(staff.getPosition()));
        row.createCell(5).setCellValue(valueOrEmpty(staff.getGrade()));
        if (staff.getSalary() != null) {
            row.createCell(6).setCellValue(staff.getSalary().doubleValue());
        }
        row.createCell(7).setCellValue(valueOrEmpty(staff.getEmail()));
        row.createCell(8).setCellValue(valueOrEmpty(staff.getTel()));
        row.createCell(9).setCellValue(valueOrEmpty(staff.getMob()));
        row.createCell(10).setCellValue(valueOrEmpty(staff.getContract()));
        writeDateCell(row, 11, staff.getAppointment(), dateStyle);
        writeDateCell(row, 12, staff.getDeparture(), dateStyle);
        row.createCell(13).setCellValue(valueOrEmpty(staff.getAddress()));
        row.createCell(14).setCellValue(valueOrEmpty(staff.getAddress2()));
        row.createCell(15).setCellValue(valueOrEmpty(staff.getNextOfKin()));
    }

    private void writeDateCell(Row row, int column, Date date, CellStyle dateStyle) {
        if (date == null) {
            return;
        }
        Cell cell = row.createCell(column);
        cell.setCellValue(date);
        cell.setCellStyle(dateStyle);
    }

    private void triggerDownload(String fileName, byte[] data) {
        StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(data));
        resource.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resource.setCacheTime(0);

        StreamRegistration registration = VaadinSession.getCurrent()
                .getResourceRegistry()
                .registerResource(resource);
        UI.getCurrent().getPage().executeJs("window.location.href = $0;", registration.getResourceUri().toString());
    }

    private void refreshGrid() {
        String fy = selectedFinancialYear();
        List<Staff> staff = staffService.searchByFinancialYear(fy, searchField.getValue());
        grid.setItems(staff);
        updateFooter(fy, staff);
    }

    private void updateFooter(String fy, List<Staff> staff) {
        footer.removeAll();
        if (fy == null) {
            footer.add(new Text("Select a financial year to view staff."));
            return;
        }

        BigDecimal monthly = staff.stream()
                .map(Staff::getSalary)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal annual = monthly.multiply(new BigDecimal("12"));
        footer.add(new Text("Monthly Salary: " + formatAmount(monthly)
                + " | Annual Salary: " + formatAmount(annual)));
    }

    private String selectedFinancialYear() {
        return budget.isEmpty() ? null : budget.getValue().getFinancialYear();
    }

    private void clearForm() {
        selectedStaff = null;
        binder.readBean(new Staff());
        delete.setEnabled(false);
    }

    private void setRequired(TextField... fields) {
        for (TextField field : fields) {
            field.setRequired(true);
            field.setRequiredIndicatorVisible(true);
            field.setClearButtonVisible(true);
        }
    }

    private String staffName(Staff staff) {
        String first = staff.getFname() == null ? "" : staff.getFname();
        String last = staff.getLname() == null ? "" : staff.getLname();
        String fullName = (first + " " + last).trim();
        return fullName.isEmpty() ? staff.getCode() : fullName;
    }

    private boolean isBlankRow(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < STAFF_EXPORT_HEADERS.size(); i++) {
            if (clean(cellText(row, i, formatter)) != null) {
                return false;
            }
        }
        return true;
    }

    private String cellText(Row row, int column, DataFormatter formatter) {
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(column);
        return cell == null ? "" : formatter.formatCellValue(cell).trim();
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeGrade(String value) {
        String cleaned = clean(value);
        if (cleaned == null) {
            return null;
        }
        String normalized = cleaned.toUpperCase(Locale.ROOT)
                .replace("_", " ")
                .replaceAll("\\s+", " ");
        return Arrays.stream(GRADE_OPTIONS)
                .filter(option -> option.equals(normalized))
                .findFirst()
                .orElse(null);
    }

    private BigDecimal parseAmount(String value) {
        String cleaned = clean(value);
        if (cleaned == null) {
            return null;
        }
        try {
            return new BigDecimal(cleaned.replace(",", "").replace("UGX", "").trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Date parseDate(Cell cell, DataFormatter formatter) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        String value = clean(formatter.formatCellValue(cell));
        if (value == null) {
            return null;
        }
        try {
            return toDate(LocalDate.parse(value));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("dates must use yyyy-MM-dd format.");
        }
    }

    private List<String> errorsForRow(List<String> errors, int displayRow) {
        String prefix = "Row " + displayRow + ":";
        return errors.stream()
                .filter(error -> error.startsWith(prefix))
                .toList();
    }

    private String formatImportErrors(List<String> errors) {
        int maxErrors = Math.min(errors.size(), 8);
        String message = String.join("; ", errors.subList(0, maxErrors));
        if (errors.size() > maxErrors) {
            message += "; and " + (errors.size() - maxErrors) + " more error(s).";
        }
        return message;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String safeFileName(String value) {
        return value == null ? "staff" : value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String formatAmount(BigDecimal amount) {
        return decimalFormat.format(amount == null ? BigDecimal.ZERO : amount);
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Notification warningNotification(String error) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(new Text(error), new HtmlComponent("br"), new Text("Close this warning to continue working."));
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> notification.close());

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }
}
