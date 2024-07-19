package com.methaltech.application.views.procurementplan;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.UploadExamplesI18N;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel11Service;
import com.methaltech.application.data.bgtool.service.Coalevel12Service;
import com.methaltech.application.data.bgtool.service.Coalevel13Service;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.Coalevel12;
import com.methaltech.application.data.entity.bgtool.Coalevel13;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.CurrencyData;
import com.methaltech.application.data.entity.bgtool.Department;
import com.methaltech.application.data.entity.bgtool.IncomeSources;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.ProcurementType;
import com.methaltech.application.data.entity.bgtool.Section;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.livedata.UR5_ACNT;
import com.methaltech.application.data.errorMessages;
import com.methaltech.application.data.livedata.service.UR5_ACNTService;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.views.salary.staffSalaryView;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Procurement Chart Of Accounts Settings")
@Route(value = "procurementplancoasetting", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "PROCUREMENT"})
@Uses(Icon.class)
public class ProcurementCOASettingView extends Div {

    private Grid<Coalevel1> gridCoalevel1 = new Grid<>(Coalevel1.class, false);
    private Grid<Coalevel11> gridCoalevel11 = new Grid<>(Coalevel11.class, false);
    private Grid<Coalevel12> gridCoalevel12 = new Grid<>(Coalevel12.class, false);
    private Grid<Coalevel13> gridCoalevel13 = new Grid<>(Coalevel13.class, false);

    private Grid<IncomeSources> gridFundsource = new Grid<>(IncomeSources.class, false);

    private Grid<COA> gridCOA = new Grid<>(COA.class, false);
    private Grid<UR5_ACNT> gridCOASetting = new Grid<>(UR5_ACNT.class, false);

    private DatePicker startDate;
    private DatePicker closeDate;
    private TextField financialYear;
    private TextArea description;
    private Checkbox important;

    private  Button cancel = new Button("Cancel 22");
    private  Button save = new Button("Save ee");

    private final Button cancelCurrencyData = new Button("Cancel");
    private final Button saveCurrencyData = new Button("Save");

    private final Button cancelCurrency = new Button("Cancel");
    private final Button saveCurrency = new Button("Save");

    private final Button cancelLevel1COA = new Button("Cancel");
    private final Button saveLevel1COA = new Button("Save");

    private final Button cancelLevel2COA = new Button("Cancel");
    private final Button saveLevel2COA = new Button("Save");

    private final Button cancelLevel3COA = new Button("Cancel");
    private final Button saveLevel3COA = new Button("Save");

    private final Button cancelUnit = new Button("Cancel");
    private final Button saveUnit = new Button("Save");

    private final Button cancelSection = new Button("Cancel");
    private final Button saveSection = new Button("Save");

    private final Button cancelDepartment = new Button("Cancel");
    private final Button saveDepartment = new Button("Save");

    private final Button cancelCoalevel1 = new Button("Cancel");
    private final Button saveCoalevel1 = new Button("Save");

    private final Button cancelCoalevel11 = new Button("Cancel");
    private final Button saveCoalevel11 = new Button("Save");

    private final Button cancelCoalevel12 = new Button("Cancel");
    private final Button saveCoalevel12 = new Button("Save");

    private final Button cancelCoalevel13 = new Button("Cancel");
    private final Button saveCoalevel13 = new Button("Save");

    private final Button cancelCOA = new Button("Cancel");
    private final Button saveCOA = new Button("Save");

    private Button autogen = new Button("Set");

    private MenuBar menuBar = new MenuBar();

    private MenuItem currency;
    private SubMenu edit_currency;
    private SubMenu currencyFy;

    private MenuItem chart_of_accounts;
    private SubMenu level1COA;
    private SubMenu level2COA;
    private SubMenu level3COA;
    private SubMenu level4COA;

    private MenuItem structure;
    private SubMenu department;
    private SubMenu section;
    private SubMenu unit;
    private SubMenu budgetStructure;

    private MenuItem workplan;
    private SubMenu workplanView;

    private MenuItem procurementMethods;
    private SubMenu procurementMethodsView;

    private MenuItem procurementType;
    private SubMenu procurementTypeView;

    private MenuItem Fundsource;
    private SubMenu fundsourceView;

    private BeanValidationBinder<Budget> binder = new BeanValidationBinder<>(Budget.class);

    private BeanValidationBinder<COA> binderCOA = new BeanValidationBinder<>(COA.class);

    private Budget sampleBudget;

    private CurrencyData sampleCurrencyData;
    private Currency sampleCurrency;

    private Unit sampleUnitDept;
    private Section sampleSection;
    private Department sampleDepartment;
    private Coalevel1 sampleCoalevel1;
    private Coalevel11 sampleCoalevel11;
    private Coalevel12 sampleCoalevel12;
    private Coalevel13 sampleCoalevel13;

    private Coalevel1 sampleCoalevel1COA = new Coalevel1();

    private Organisation sampleOrganisation;

    private ProcurementType sampleProcurementType;
    private IncomeSources sampleFundsource;

    private COA sampleCOA;
    private final Coalevel1Service sampleCoalevel1Service;
    private final Coalevel11Service sampleCoalevel11Service;
    private final Coalevel12Service sampleCoalevel12Service;
    private final CoaService sampleCoaService;
    private final Coalevel13Service sampleCoalevel13Service;
    private final UR5_ACNTService sampleUR5_ACNTService;

    private TextField currencyDataNameField = new TextField("Currency");
    private TextField currencyAbrField = new TextField("Currency Abr");

    private TextField UnitNameField = new TextField("Unit");

    private TextField SectionNameField = new TextField("Section");

    private ComboBox<CurrencyData> currencyName = new ComboBox<>("Currency");
    private BigDecimalField currencyRateField = new BigDecimalField("Rate");
    private Checkbox currencyEnabledField = new Checkbox("Enable");
    private String currentfy;
    private Long level1ID = null;
    Long level1ID2 = null;
    private int deptselect;
    private int sectselect;

    private TextField BudgetTypeNameField = new TextField("BudgetName");
    private TextField ProcurementMethodNameField = new TextField("Procurement Method");
    private TextField ProcurementTypeNameField = new TextField("Procurement Type");
    private TextField FundsourceNameField = new TextField("Fund source");

    private TextField DepartmentNameField = new TextField("Department");

    private TextField Coalevel1NameField = new TextField("Name");
    private TextField Coalevel11NameField = new TextField("Name");
    private TextField Coalevel12NameField = new TextField("Name");
    private TextField Coalevel13NameField = new TextField("Name");

    private TextField COALevel11NameField = new TextField("Name");

    private ComboBox<Coalevel1> Coalevel1Box = new ComboBox<>("Class 1");
    private ComboBox<Display> displayBox = new ComboBox<>("Display");
    private ComboBox<Coalevel1> Coalevel1Box1 = new ComboBox<>("Class 1");
    private TextField COANameField = new TextField("Name");
    private TextField CodeField = new TextField("Code");
    //private ComboBox<Coalevel11> Coalevel11Box = new ComboBox<>("COA Sub Category 1");
    private MultiSelectComboBox<Section> coaunits = new MultiSelectComboBox<>();
    private ComboBox<ProcClass> procclass = new ComboBox<>("Procurement Category");
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sections = new MultiSelectComboBox("Attach Sections");

    //private ComboBox<Coalevel12> Coalevel12Box = new ComboBox<>("Class 2");
    // private ComboBox<Coalevel13> Coalevel13Box = new ComboBox<>("COA Sub Category 2");
    private Span status;
    private TextField COASearchField = new TextField("Search");
    private TextField COASearchField1 = new TextField("Search");

    private int currentPage;
    private int pageSize;
    private String filter;
    private COA coaSAVE;
    private Budget sourceBudget;
    private ComboBox<Budget> comboBoxBudget;
    private final BudgetService chosenBudgetService;

    Checkbox checkbox = new Checkbox("Active");
    Upload upload=null;

    @Autowired
    public ProcurementCOASettingView(Coalevel1Service sampleCoalevel1Service,
            Coalevel11Service sampleCoalevel11Service, Coalevel12Service sampleCoalevel12Service,
            CoaService sampleCoaService, Coalevel13Service sampleCoalevel13Service,
            UR5_ACNTService sampleUR5_ACNTService, BudgetService chosenBudgetService) {
        this.sampleCoalevel1Service = sampleCoalevel1Service;
        this.sampleCoalevel11Service = sampleCoalevel11Service;
        this.sampleCoalevel12Service = sampleCoalevel12Service;
        this.sampleCoaService = sampleCoaService;
        this.sampleCoalevel13Service = sampleCoalevel13Service;
        this.sampleUR5_ACNTService = sampleUR5_ACNTService;
        this.chosenBudgetService = chosenBudgetService;

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSizeFull();
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        comboBoxBudget = new ComboBox<>("Budget");

        comboBoxBudget.setItems(query -> chosenBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        comboBoxBudget.setItemLabelGenerator(Budget::getFinancialYear);

        COASearchField1.setClearButtonVisible(true);
        COASearchField1.setPlaceholder("Filter by name or code");
        COASearchField1.setPrefixComponent(VaadinIcon.SEARCH.create());
        COASearchField1.setValueChangeMode(ValueChangeMode.EAGER);

        Coalevel1Box1.addValueChangeListener(er -> {
            if (!Coalevel1Box1.isEmpty() && !comboBoxBudget.isEmpty()) {
                gridCOA.setItems(sampleCoaService.getCOAList(comboBoxBudget.getValue(), Coalevel1Box1.getValue(), ""));
            }
        });
        COASearchField1.addValueChangeListener(er -> {
            //updateGridCoaSetting(er.getValue().trim());
            if (!Coalevel1Box1.isEmpty() && !comboBoxBudget.isEmpty()) {
                gridCOA.setItems(sampleCoaService.getCOAList(comboBoxBudget.getValue(), Coalevel1Box1.getValue(), er.getValue()));
            }

        });
        comboBoxBudget.addValueChangeListener(e -> {

            if (!e.getValue().isActive()) {
                saveCOA.setEnabled(false);
                cancelCOA.setEnabled(false);
                upload.setVisible(false);
            }
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSplitterPosition(65);
        splitLayout.setHeightFull();
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(createCOAASettingGridDialogLayout());
        splitLayout.addToSecondary(createCOADialogLayout());

        //dialogLayout.add(splitLayout);
        add(splitLayout);
        this.setSizeFull();

    }

    private VerticalLayout createCOAASettingGridDialogLayout() {

        FormLayout lay = new FormLayout();
        lay.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("800px", 3),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("1000px", 4));
        // layContainer.setAlignItems(FlexComponent.Alignment.BASELINE);
        gridCOA = new Grid<>(COA.class, false);
        gridCOA.addColumn(COA::getCode)
                .setHeader("code")
                .setAutoWidth(true);
        gridCOA.addColumn("name").setAutoWidth(true);

        Coalevel1Box1.setItems(sampleCoalevel1Service.findCoalevel1ByBudgetId());
        Coalevel1Box1.setItemLabelGenerator(Coalevel1::getName);

        gridCOA.asSingleSelect().addValueChangeListener(event -> {
            clearFormCOA();

            //sampleCOA = sampleCoaService.findByCodeAndBudgetWithDSections(event.getValue().getAcntCode(), sampleBudget);
            if (event.getValue() != null) {

                COANameField.setValue(event.getValue().getName());
                CodeField.setValue(event.getValue().getCode());
                procclass.setValue(event.getValue().getProcclass());

            }

        });

        lay.add(comboBoxBudget, Coalevel1Box1, COASearchField1);
        VerticalLayout dialogLayout = new VerticalLayout(lay, gridCOA);

        return dialogLayout;
    }

    private VerticalLayout createCOADialogLayout() {
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

        upload.addSucceededListener(event -> {
            if (!comboBoxBudget.isEmpty()) {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream(fileName);
                //System.out.println("Uploaded");
                extractStaffSalaryFromCell2(inputStream);
            } else {
                Notification.show("Select all the required parameters");
            }

        });
        Text label = new Text("CODE DETAILS");
        CodeField = new TextField("Code");
        CodeField.setPlaceholder("Account Code");
        COANameField = new TextField("Name");
        COANameField.setPlaceholder("Account Name");
        Button refreshCurrencyTable = new Button("Refresh");

        Button selectallUnits = new Button("Select all");

        HorizontalLayout layContainer = new HorizontalLayout();
        layContainer.setPadding(true);
        layContainer.setSpacing(false);
        layContainer.setAlignItems(FlexComponent.Alignment.BASELINE);
        layContainer.setWidthFull();
        layContainer.getStyle().set("width", "18rem").set("max-width", "100%");

        COANameField = new TextField("Name");
        COANameField.setPlaceholder("Enter Class Name ");
        // Coalevel1Box = new ComboBox("Class 1");
        Coalevel1Box.setPlaceholder("Select COA Category");
        CodeField.setReadOnly(true);
        COANameField.setReadOnly(true);
        CodeField.setEnabled(false);
        COANameField.setEnabled(false);

        VerticalLayout dialogLayout = new VerticalLayout(upload, label, CodeField, COANameField, procclass);
        procclass.setItems(ProcClass.Works, ProcClass.Supplies, ProcClass.Consultancy, ProcClass.Non_Consultancy, ProcClass.Disposal, ProcClass.Other);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        dialogLayout.setSizeFull();
        dialogLayout.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        HorizontalLayout buttonLayout2 = new HorizontalLayout();
        cancelCOA.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCOA.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshCurrencyTable.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelCOA.addClickListener(e -> {
            CodeField.clear();
            COANameField.clear();

            coaSAVE = new COA();
            gridCOA.deselectAll();
        });
        saveCOA.addClickListener(e -> {
            if (!gridCOA.asSingleSelect().isEmpty()) {
                COA item = gridCOA.asSingleSelect().getValue();
                item.setProcclass(procclass.getValue());
                if (item.getProcclass() != null) {
                    sampleCoaService.save(item);
                    CodeField.clear();
                    COANameField.clear();
                    procclass.clear();

                    coaSAVE = new COA();
                    gridCOA.deselectAll();
                }
            }

        });

        Div editorLayoutDiv = new Div();
        buttonLayout.add(saveCOA, cancelCOA);
        buttonLayout2.add(refreshCurrencyTable);
        editorLayoutDiv.add(buttonLayout);
        dialogLayout.add(editorLayoutDiv);

        return dialogLayout;
    }

    private void clearFormCOA() {
        CodeField.clear();
        COANameField.clear();
        sections.clear();
        procclass.clear();
        displayBox.clear();
        checkbox.clear();
    }

    public void extractStaffSalaryFromCell2(InputStream inputStream) {

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int i = 0;
            List<errorMessages> messages = new ArrayList<>();
            List<StaffSalary> listStaffSalary = new ArrayList();
            List<COA> listCOA = new ArrayList();
            for (Row row : sheet) {
                i++;
                if (i > 1) {
                    BudgetItems budget = new BudgetItems();
                    handleCodeCell(row, messages, i, 0, (code) -> {
                        code.setCellType(CellType.STRING);
                        COA coa = sampleCoaService.findByCodeAndBudget(code.getStringCellValue().trim(), comboBoxBudget.getValue());
                        if (coa != null) {
                            Cell nok = row.getCell(2);
                            if (nok != null) {
                                nok.setCellType(CellType.STRING);
                                coa.setProcclass(GetProcClass(nok.getStringCellValue()));
                            }
                            listCOA.add(coa);
                        }

                    });

                }
            }
            if (messages.isEmpty()) {
                int x = 0;
                for (COA a : listCOA) {
                    x++;
                    sampleCoaService.save(a);
                }

            } else {
                warningNotification(messages);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCell(Row row, List<errorMessages> messages, StaffSalary info, int rowIndex, int columnIndex, String errorMessage, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            handler.handle(cell);
        } else {
            handleNullCell(messages, rowIndex, columnIndex);
        }
    }

    private void handleCodeCell(Row row, List<errorMessages> messages, int rowIndex, int columnIndex, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            handler.handle(cell);
            COA coa = sampleCoaService.findByCodeAndBudget(cell.getStringCellValue().trim(), comboBoxBudget.getValue());
            if (coa == null) {
                handleWrongCodeCell(messages, rowIndex, columnIndex);
            }
        } else {
            handleNullCell(messages, rowIndex, columnIndex);
        }
    }

    private void handleProClassCell(Row row, List<errorMessages> messages, int rowIndex, int columnIndex, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            handler.handle(cell);
            ProcClass pp = GetProcClass(cell.getStringCellValue());
            COA coa = sampleCoaService.findByCodeAndBudget(cell.getStringCellValue().trim(), comboBoxBudget.getValue());
            if (pp == null) {
                handleWrongCodeCell(messages, rowIndex, columnIndex);
            }
        } else {
            handleNullCell(messages, rowIndex, columnIndex);
        }
    }

    private void handleNullCell(List<errorMessages> messages, int rowIndex, int columnIndex) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     Account Code is null");
        messages.add(error);
    }

    private void handleWrongCodeCell(List<errorMessages> messages, int rowIndex, int columnIndex) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     Account Code is Invalid");
        messages.add(error);
    }

    private void handleWrongProClassCell(List<errorMessages> messages, int rowIndex, int columnIndex) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     Procurement Class is Invalid");
        messages.add(error);
    }

    @FunctionalInterface
    private interface CellHandler {

        void handle(Cell cell);
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

    public ProcClass GetProcClass(String s) {
        ProcClass ba = null;

        if (s.equals("Other")) {
            ba = ProcClass.Other;
        } else if (s.equals("Non Consultancy")) {
            ba = ProcClass.Non_Consultancy;
        } else if (s.equals("Consultancy")) {
            ba = ProcClass.Consultancy;
        } else if (s.equals("Supplies")) {
            ba = ProcClass.Supplies;
        } else if (s.equals("Works")) {
            ba = ProcClass.Works;
        } else if (s.equals("Disposal")) {
            ba = ProcClass.Disposal;
        } else {
            ba = ProcClass.Other;
        }

        return ba;
    }
}
