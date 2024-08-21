package com.methaltech.application.views;

import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.FundsourceService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.errorMessages;
import com.methaltech.application.security.AuthenticatedUser;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("URC Programmes")
@Route(value = "programmes", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO", "USER", "HOD"})
@Uses(Icon.class)
public class UrcProgrammesView extends Div {

    private TextField searchCoa = new TextField();
    private TextField searchAct = new TextField();
    private TextArea name = new TextArea("URC Priority Area");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Upload uploadProgrammeTemplate;
    private Upload uploadActivityTemplate;
    private final BudgetService chosenBudgetService;
    private final UserService userService;
    private User user;
    private AuthenticatedUser authenticatedUser;
    private URC_Priority_AreasService uRC_Priority_AreasService;
    private Grid<URC_Priority_Areas> gridView = new Grid<>(URC_Priority_Areas.class, false);
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    private final BudgetItemsService budgetItemsService;
    private Span status;
    private SplitLayout layouts = new SplitLayout();
    private SplitLayout layouts2 = new SplitLayout();
    private VerticalLayout vlay = new VerticalLayout();
    private VerticalLayout vlay2 = new VerticalLayout();
    private Grid<Urc_Activities> gridUrc_Activities = new Grid<>(Urc_Activities.class, false);
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section;
    private FormLayout form = new FormLayout();
    private VerticalLayout activityvlay2 = new VerticalLayout();
    private Urc_ActivitiesService sampleUrc_ActivitiesService;

    private TextArea activity = new TextArea("Urc Activity");
    private TextArea fundsource = new TextArea("Fund Source");
    private TextArea performanceIndicator = new TextArea("Performance Indicator");
    private TextArea outcome = new TextArea("Outcome");
    private TextArea output = new TextArea("Output");
    private TextArea objective = new TextArea("Objective");
    private FormLayout formActivity = new FormLayout();
    private BeanValidationBinder<Urc_Activities> binder = new BeanValidationBinder<>(Urc_Activities.class);
    private MenuBar menuBar = new MenuBar();
    private UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService;
    private Urc_ActivitiesService urc_ActivitiesService;
    private final OrganisationService sampleOrganisationService;
    private final FundsourceService fundsourceService;
    Button saveA = new Button("Save");
    Button deleteA = new Button("Delete");
    Button rectifyA = new Button("Rectify");
    Button saveButton = new Button("Change Programme");
    Button uploadProgrammeButton = new Button("Upload Programmes...");
    Button importProgrammeButton = new Button("Import Programmes...");
    ComboBox<Budget> budgetComboBox = new ComboBox<>("Select Budget");
    Button importActivityButton = new Button("Import Activities");

    public UrcProgrammesView(BudgetService chosenBudgetService, AuthenticatedUser authenticatedUser, UserService userService, URC_Priority_AreasService uRC_Priority_AreasService,
            BudgetItemsService budgetItemsService, Urc_ActivitiesService sampleUrc_ActivitiesService, UrcDeptSectionAnlDimbgtService urcDeptSectionAnlDimbgtService,
            Urc_ActivitiesService urc_ActivitiesService, OrganisationService sampleOrganisationService, FundsourceService fundsourceService) {
        this.chosenBudgetService = chosenBudgetService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.uRC_Priority_AreasService = uRC_Priority_AreasService;
        this.budgetItemsService = budgetItemsService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.urcDeptSectionAnlDimbgtService = urcDeptSectionAnlDimbgtService;
        this.urc_ActivitiesService = urc_ActivitiesService;
        this.sampleOrganisationService = sampleOrganisationService;
        this.fundsourceService = fundsourceService;
        gridUrc_Activities.setHeight("100%");
        Urc_ActivitiesGridContextMenu contextMenu = new Urc_ActivitiesGridContextMenu(gridUrc_Activities);
        // menuBar.setOpenOnHover(true);
        MenuItem move = menuBar.addItem("Download Templates");
        SubMenu moveSubMenu = move.getSubMenu();
        moveSubMenu.addItem("Download Programe File Template").addClickListener(e -> {
            exportAndDownloadUploadProgrammeFileExcel();
        });
        moveSubMenu.addItem("Download Activity File Template").addClickListener(e -> {
            exportAndDownloadUploadActivityFileExcel();
        });
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        add(menuBar, new Hr());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        setHeightFull();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);

        budgetComboBox.setItems(/* Add your list of Budget entities here */);
        budgetComboBox.setItemLabelGenerator(Budget::getFinancialYear);
        gridView.addColumn(URC_Priority_Areas::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        gridView.addColumn(URC_Priority_Areas::getName).setHeader("Programme");
        gridView.asSingleSelect().addValueChangeListener(e -> {
            if (!gridView.asSingleSelect().isEmpty() && !budgetComboBox.isEmpty()) {
                name.setValue(e.getValue().getName());
                if (!comboBoxD_Section.isEmpty()) {
                    refreshActGrid(budgetComboBox.getValue(), e.getValue(), comboBoxD_Section.getSelectedItems().stream().toList());
                }

            }

        });

        comboBoxD_Section = new MultiSelectComboBox<>("Cost Centres");
        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        comboBoxD_Section.setWidthFull();
        comboBoxD_Section.setItems(user.getDeptsection());
        comboBoxD_Section.addValueChangeListener(e -> {
            if (!comboBoxD_Section.isEmpty() && !gridView.asSingleSelect().isEmpty() && !budgetComboBox.isEmpty()) {
                refreshActGrid(budgetComboBox.getValue(), gridView.asSingleSelect().getValue(), comboBoxD_Section.getSelectedItems().stream().toList());
            }
        });
        if (user.getRoles().contains(Role.ADMIN)) {
            uploadProgrammeButton.setEnabled(true);
            uploadProgrammeButton.setVisible(true);
            importProgrammeButton.setEnabled(true);
            importProgrammeButton.setVisible(true);
            save.setEnabled(true);
            delete.setEnabled(true);
        } else {
            uploadProgrammeButton.setEnabled(false);
            uploadProgrammeButton.setVisible(false);
            importProgrammeButton.setEnabled(false);
            importProgrammeButton.setVisible(false);
            save.setEnabled(false);
            delete.setEnabled(false);
        }

        budgetComboBox.setItems(query -> chosenBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        budgetComboBox.setItemLabelGenerator(Budget::getFinancialYear);
        FormLayout lay = new FormLayout();
        searchCoa.setPlaceholder("Search");
        searchCoa.setClearButtonVisible(true);
        searchCoa.setValueChangeMode(ValueChangeMode.EAGER);

        searchAct.setPlaceholder("Search");
        searchAct.setClearButtonVisible(true);
        searchAct.setValueChangeMode(ValueChangeMode.EAGER);

        name.setPlaceholder("URC Priority Area");
        name.setClearButtonVisible(true);
        name.setWidth("300px");

        delete.addClickListener(e -> {
            URC_Priority_Areas entity = gridView.asSingleSelect().getValue();
            if (entity != null && !budgetComboBox.isEmpty()) {
                ConfirmDialogBasic(entity, budgetComboBox.getValue());

            }
        });
        HorizontalLayout layout = new HorizontalLayout();
        layout.add(name, save, delete);
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        save.addClickListener(e -> {
            if (!budgetComboBox.isEmpty()) {
                Notification.show("Budget not empty");
                URC_Priority_Areas entity = gridView.asSingleSelect().getValue();
                if (entity == null) {
                    entity = new URC_Priority_Areas();
                    entity.setName(name.getValue());
                    entity.setBudget(budgetComboBox.getValue());
                } else {
                    entity.setName(name.getValue());
                    entity.setBudget(entity.getBudget());
                }
                if (isTextAreaEmptyOrBlank(name) == false) {
                    uRC_Priority_AreasService.update(entity);
                }

                refreshGrid(budgetComboBox.getValue());
                name.clear();
            }

        });
        budgetComboBox.addValueChangeListener(event -> {
            Budget selectedBudget = event.getValue();
            refreshGrid(selectedBudget);
            if (!event.getValue().isActive()) {
                save.setEnabled(false);
                delete.setEnabled(false);
                saveA.setEnabled(false);
                deleteA.setEnabled(false);
                uploadProgrammeTemplate.setVisible(false);
                importProgrammeButton.setVisible(false);
                uploadActivityTemplate.setVisible(false);
                importActivityButton.setVisible(false);
                saveButton.setEnabled(false);
            } else {
                save.setEnabled(true);
                delete.setEnabled(true);
                saveA.setEnabled(true);
                deleteA.setEnabled(true);
                uploadProgrammeTemplate.setVisible(true);
                importProgrammeButton.setVisible(true);
                uploadActivityTemplate.setVisible(true);
                saveButton.setEnabled(true);
                importActivityButton.setVisible(true);
            }
        });
        searchCoa.addValueChangeListener(e -> {
            refreshGrid2(e.getValue(), budgetComboBox.getValue());

        });
        searchAct.addValueChangeListener(e -> {
            if (!comboBoxD_Section.isEmpty() && !gridView.asSingleSelect().isEmpty() && !budgetComboBox.isEmpty()) {
                refreshActGrid2(budgetComboBox.getValue(), gridView.asSingleSelect().getValue(), comboBoxD_Section.getSelectedItems().stream().toList(), e.getValue());
            }

        });
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        uploadProgrammeTemplate = new Upload(buffer);

        uploadProgrammeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        importProgrammeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        uploadProgrammeTemplate.setUploadButton(uploadProgrammeButton);

        MultiFileMemoryBuffer buffer2 = new MultiFileMemoryBuffer();
        uploadActivityTemplate = new Upload(buffer2);
        Button uploadActivityButton = new Button("Upload Activities...");
        uploadActivityButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        uploadActivityTemplate.setUploadButton(uploadActivityButton);

        lay.add(budgetComboBox, searchCoa);
        if (isUserAdmin() == true) {
            lay.add(importProgrammeButton, uploadProgrammeTemplate, layout);
        }
        lay.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new FormLayout.ResponsiveStep("320px", 2),
                // Use three columns, if the layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3),
                new FormLayout.ResponsiveStep("700px", 4));

        uploadProgrammeTemplate.addSucceededListener(event -> {
            if (!budgetComboBox.isEmpty()) {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream(fileName);

                try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                    Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

                    for (Row row : sheet) {
                        URC_Priority_Areas urcPriorityArea = new URC_Priority_Areas();

                        // Check if the row is null
                        if (row != null) {
                            // Assuming the name is in the first cell of each row (you can change it accordingly)
                            Cell cell = row.getCell(0); // 0 represents the first cell

                            // Check if the cell is not null and is of type STRING
                            if (cell != null && cell.getCellType() == CellType.STRING) {
                                String name = cell.getStringCellValue();
                                urcPriorityArea.setName(name);
                                urcPriorityArea.setBudget(budgetComboBox.getValue());

                                uRC_Priority_AreasService.update(urcPriorityArea);
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                refreshGrid(budgetComboBox.getValue());
            }

        });
        importProgrammeButton.addSingleClickListener(e -> {
            if (!budgetComboBox.isEmpty()) {
                openProgramDialog().open();
            } else {
                Notificationwarning("Select a financial year");
            }

        });

        uploadActivityTemplate.addSucceededListener(event -> {
            List<errorMessages> mes = new ArrayList<>();
            errorMessages m = new errorMessages();
            if (!budgetComboBox.isEmpty()) {
                String fileName = event.getFileName();
                InputStream inputStream = buffer2.getInputStream(fileName);

                try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                    Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
                    int i = 0;
                    StringBuilder error = new StringBuilder();

                    for (Row row : sheet) {
                        i++;

                        if (i > 1) {
                            // Skip the first row

                            if (row != null) {
                                Cell cell = row.getCell(0);

                                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                                    // Assuming the cell contains a date and time as a numeric value
                                    int dateValue = (int) cell.getNumericCellValue();
                                    URC_Priority_Areas urcPriorityArea = uRC_Priority_AreasService.getById((long) dateValue);
                                    if (urcPriorityArea == null) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i);
                                        m.setMessage("Programe ID Invalid");
                                        mes.add(m);
                                    }

                                } else if (cell != null && cell.getCellType() == CellType.STRING) {
                                    String stringValue = cell.getStringCellValue();
                                    //System.out.println("Text: " + stringValue);
                                    int name = 0;
                                    try {
                                        name = Integer.parseInt(stringValue);
                                        URC_Priority_Areas urcPriorityArea = uRC_Priority_AreasService.getById((long) name);
                                        if (urcPriorityArea == null) {
                                            m = new errorMessages();
                                            m.setRow("Row: " + i);
                                            m.setMessage("Programe ID Invalid");
                                            mes.add(m);
                                        }
                                    } catch (NumberFormatException e) {
                                        // Handle other cell types or data types as needed
                                        m = new errorMessages();
                                        m.setRow("Row: " + i);
                                        m.setMessage("Programe ID Should be an Integer");
                                        mes.add(m);
                                    }

                                } else {
                                    m = new errorMessages();
                                    m.setRow("Row: " + i);
                                    m.setMessage("Programe ID Invalid");
                                    mes.add(m);
                                }
                                Cell cell2 = row.getCell(1);
                                if (cell2 != null && cell2.getCellType() == CellType.STRING) {
                                    String stringValue = cell2.getStringCellValue();

                                    UrcDeptSectionAnlDimbgt sectioncode = urcDeptSectionAnlDimbgtService.findByANL_CODE(stringValue);
                                    if (sectioncode == null) {
                                        m = new errorMessages();
                                        m.setRow("Row: " + i);
                                        m.setMessage("Section Code Invalid");
                                        mes.add(m);
                                    } else {
                                        if (!user.getDeptsection().contains(sectioncode)) {
                                            m = new errorMessages();
                                            m.setRow("Row: " + i);
                                            m.setMessage("You have no access to that Section");
                                            mes.add(m);
                                        }
                                    }

                                }
                                if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null || row.getCell(3) == null || row.getCell(4) == null || row.getCell(5) == null || row.getCell(6) == null || row.getCell(7) == null) {
                                    m = new errorMessages();
                                    m.setRow("Row: " + i);
                                    m.setMessage("Empty columns an not accepted");
                                    mes.add(m);
                                }
                            }
                        }
                    }

                    if (!mes.isEmpty()) {
                        warningNotification(mes);
                        System.out.println(error.toString());
                    } else {
                        //System.out.println("Processed " + (i - 1) + " rows."); // Subtract 1 to exclude the header row
                        sheetData(sheet, budgetComboBox.getValue());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                m = new errorMessages();
                m.setRow("Error:");
                m.setMessage("No Budget Selected");
                mes.add(m);

                warningNotification(mes);
            }
        });

        Grid.Column<Urc_Activities> aname = gridUrc_Activities.addColumn("name").setHeader("URC Activities").setAutoWidth(true);
        gridUrc_Activities.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(urcActivity.getDeptSection().getNAME());
            /*
            if (isUserCreator(urcActivity.getDeptSection())) {
            span.getElement().getThemeList().add("badge success");
            } else {
            span.getElement().getThemeList().add("badge error");
            }*/
            return span;
        })).setHeader("Section Creator").setFlexGrow(0).setWidth("150px").setFooter("");
        gridUrc_Activities.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span(urcActivity.getActivityCode());
            span.getElement().getThemeList().add("badge success");

            return span;
        })).setHeader("Code").setFlexGrow(0).setWidth("150px").setFooter("");
        //gridUrc_Activities.addColumn("budget").setHeader("Budget").setAutoWidth(true).setFlexGrow(0).setWidth("150px");
        gridUrc_Activities.addColumn(new ComponentRenderer<>(urcActivity -> {
            BigDecimal dec = BigDecimal.ZERO;
            if (!budgetComboBox.isEmpty() && !comboBoxD_Section.isEmpty()) {
                dec = budgetItemsService.calculateTotalByBudgetAndActivityAndDeptUnits(budgetComboBox.getValue(), urcActivity, comboBoxD_Section.getSelectedItems().stream().toList());
            }

            Span span = new Span("" + decimalFormat.format(dec));
            return span;
        })).setHeader("Budget").setFlexGrow(0).setWidth("150px");
        gridUrc_Activities.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        binder.forField(activity).asRequired("Urc Activity is Required").bind("name");
        binder.forField(fundsource).asRequired("Fund Source is Required").bind("fundsource");
        binder.forField(performanceIndicator).asRequired("Performance Indicator is Required").bind("performanceIndicator");
        binder.forField(outcome).asRequired("outcome is Required").bind("outcome");
        binder.forField(objective).asRequired("objective is Required").bind("objective");
        binder.forField(output).asRequired("output is Required").bind("output");
        gridUrc_Activities.asSingleSelect().addValueChangeListener(e -> {
            if (!gridUrc_Activities.asSingleSelect().isEmpty()) {
                populateForm(e.getValue());
            }

        });

        vlay.add(lay, gridView);
        if (isUserAdmin() == true) {
            vlay.add(layout);
        }
        importActivityButton.addSingleClickListener(e -> {
            if (!gridView.asSingleSelect().isEmpty()) {
                openActivityDialog().open();
            }

        });
        form.add(comboBoxD_Section, searchAct, importActivityButton, uploadActivityTemplate);
        form.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new FormLayout.ResponsiveStep("320px", 2),
                // Use three columns, if the layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3),
                new FormLayout.ResponsiveStep("700px", 4));
        activityvlay2.add(form, gridUrc_Activities);
        layouts2 = new SplitLayout(vlay, activityvlay2);
        layouts2.setSplitterPosition(50);
        layouts2.setHeight("100%");
        layouts2.setOrientation(SplitLayout.Orientation.VERTICAL);

        formActivity.add(activity, fundsource, performanceIndicator, outcome, output, objective);
        vlay2.add(formActivity);
        HorizontalLayout lays = new HorizontalLayout();

        lays.add(saveA, deleteA, rectifyA);
        vlay2.add(lays);

        rectifyA.addClickListener(e -> {
            List<Urc_Activities> list = sampleUrc_ActivitiesService.listByBudget2(budgetComboBox.getValue());
            for (Urc_Activities a : list) {
                a.setActivityCode("ZBA" + a.getActivityCode());
                if (!budgetComboBox.isEmpty()) {
                    sampleUrc_ActivitiesService.update(a);
                }

            }
            List<Organisation> listorg = sampleOrganisationService.findByBudgetList(budgetComboBox.getValue());
            int i = 0;
            for (Organisation a : listorg) {
                i++;
                a.setCode(generateBudgetTypeCode(i));
                if (!budgetComboBox.isEmpty()) {
                    sampleOrganisationService.update(a);
                }

            }

            List<Fundsource> listfunds = fundsourceService.findFundsourcesByBudget(budgetComboBox.getValue());
            int i2 = 0;
            for (Fundsource a : listfunds) {
                i2++;
                a.setCode(generateFundSourceCode(i2));
                if (!budgetComboBox.isEmpty()) {
                    fundsourceService.save(a);
                }

            }
        });
        saveA.addClickListener(e -> {
            if (!activity.isEmpty() && !fundsource.isEmpty() && !performanceIndicator.isEmpty() && !outcome.isEmpty() && !output.isEmpty() && !objective.isEmpty() && !comboBoxD_Section.isEmpty() && !budgetComboBox.isEmpty()) {
                Urc_Activities act = gridUrc_Activities.asSingleSelect().getValue();
                if (act != null) {
                    act.setName(activity.getValue());
                    act.setFundsource(fundsource.getValue());
                    act.setPerformanceIndicator(performanceIndicator.getValue());
                    act.setOutcome(outcome.getValue());
                    act.setOutput(output.getValue());
                    act.setObjective(objective.getValue());
                    sampleUrc_ActivitiesService.update(act);
                    refreshActGrid(budgetComboBox.getValue(), act.getUrcPriorityAreas(), comboBoxD_Section.getSelectedItems().stream().toList());

                } else {
                    if (!gridView.asSingleSelect().isEmpty()) {
                        act = new Urc_Activities();
                        act.setName(activity.getValue());
                        act.setFundsource(fundsource.getValue());
                        act.setPerformanceIndicator(performanceIndicator.getValue());
                        act.setOutcome(outcome.getValue());
                        act.setOutput(output.getValue());
                        act.setObjective(objective.getValue());
                        act.setBudget(budgetComboBox.getValue());
                        act.setUrcPriorityAreas(gridView.asSingleSelect().getValue());
                        if (comboBoxD_Section.getSelectedItems().size() > 1) {
                            Notification.show("Select only one Section");
                        } else {
                            act.setDeptSection(comboBoxD_Section.getSelectedItems().parallelStream().toList().get(0));
                            String maxActivityCode = urc_ActivitiesService.maxActivityCode(act.getDeptSection().getANL_CODE());
                            String nextActivityCode = "";
                            if (maxActivityCode != null) {
                                //int nextActivityCodeNumber = Integer.parseInt(maxActivityCode.substring(4)) + 1;
                                BigInteger nextActivityCodeNumber = new BigInteger(maxActivityCode.substring(7)).add(BigInteger.ONE);
                                nextActivityCode = "ZBA" + act.getDeptSection().getANL_CODE().trim() + String.format("%07d", nextActivityCodeNumber);
                            } else {
                                nextActivityCode = "ZBA" + act.getDeptSection().getANL_CODE().trim() + "0000001";
                            }

                            act.setActivityCode(nextActivityCode);
                            sampleUrc_ActivitiesService.update(act);
                            refreshActGrid(budgetComboBox.getValue(), act.getUrcPriorityAreas(), comboBoxD_Section.getSelectedItems().stream().toList());
                        }
                    } else {
                        Notification.show("Select a Programme");
                    }

                }
            } else {
                Notification.show("Ensure that no value is left empty");
            }

            clearActivity();
        });
        deleteA.addClickListener(e -> {
            Urc_Activities act = gridUrc_Activities.asSingleSelect().getValue();
            if (act != null) {
                try {
                    sampleUrc_ActivitiesService.deleteByActivity(act);
                } catch (Exception ex) {
                    // Handle the exception here, you can log it or show a user-friendly message
                    ex.printStackTrace(); // Print the stack trace for debugging purposes
                    // Add your custom exception handling logic here
                    Notification.show("Can`t no delete this activity. It may lead to loss of data");
                }
                refreshActGrid(act.getBudget(), act.getUrcPriorityAreas(), comboBoxD_Section.getSelectedItems().stream().toList());
            }
        });

        layouts = new SplitLayout(layouts2, vlay2);
        layouts.setSplitterPosition(50);
        layouts.setHeight("100%");

        add(layouts);
    }

    private void clearActivity() {
        activity.clear();
        fundsource.clear();
        performanceIndicator.clear();
        outcome.clear();
        output.clear();
        objective.clear();
    }

    private boolean isUserAdmin() {
        Optional<User> maybeUser = authenticatedUser.get();

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            return user.getRoles().contains(Role.ADMIN);
        }

        return false;
    }

    public void refreshGrid(Budget budget) {
        gridUrc_Activities.deselectAll();
        gridUrc_Activities.setItems(new ArrayList());
        gridView.deselectAll();
        gridView.setItems(uRC_Priority_AreasService.findByBudget(budget));

    }

    public void refreshGrid2(String name, Budget budget) {
        gridUrc_Activities.deselectAll();
        gridUrc_Activities.setItems(new ArrayList());
        gridView.deselectAll();
        gridView.setItems(uRC_Priority_AreasService.findByNameAndBudget(name, budget));

    }

    private void exportAndDownloadUploadProgrammeFileExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Programme Upload Template");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            Row headerRow = sheet.createRow(0);

            // Add currency and unit measure options to the header row
            Cell currencyCell = headerRow.createCell(0); // Column 26 (Position AA)
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true); // Set wrap text property
            currencyCell.setCellStyle(cellStyle);
            currencyCell.setCellValue("Put all programmes in this first Column");

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Programme Upload Template.xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadUploadActivityFileExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Activity Upload Template");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            Row headerRow = sheet.createRow(0);
            // Create the headers
            String[] headers = {"URC PRIORITY ID", "SECTION CODE", "ACTIVITY", "FUND SOURCE", "PERFORMANCE INDICATOR", "OUTPUT", "OUTCOME", "OBJECTIVE"};
            //Row headerRow = sheet.createRow(0);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true); // Set wrap text property
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(cellStyle);
            }

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Activity Upload Template.xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStatus(String value) {
        status.setText("Status: " + value);
        status.setVisible(true);
    }

    public ConfirmDialog ConfirmDialogBasic(URC_Priority_Areas entity, Budget budget) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        status = new Span();
        status.setVisible(false);

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Selected Data");
        dialog.setText(
                "Do you want to discard the Selected Data?");

        dialog.setCancelable(true);
        dialog.addCancelListener(event -> {
            dialog.close();
        });

        dialog.setRejectable(true);
        dialog.setRejectText("Discard");
        dialog.addRejectListener(event -> {
            dialog.close();
        });

        dialog.setConfirmText("Delete");
        dialog.addConfirmListener(event -> {
            try {
                uRC_Priority_AreasService.delete(entity.getId());
                status.setVisible(true);
                refreshGrid(budget);
                setStatus("Saved");
                dialog.close();
            } catch (Exception e) {
                // Handle the exception, you can log it or show an error message
                e.printStackTrace(); // This will print the exception trace to the console, replace it with your desired handling logic
                // Optionally, you can also show an error message to the user
                errorNotification("An error occurred while deleting the data.");
            }
        });

        dialog.open();

        // layout.add(button, status);
        // add(layout);
        // Center the button within the example
        /*        getStyle().set("position", "fixed").set("top", "0").set("right", "0")
        .set("bottom", "0").set("left", "0").set("display", "flex")
        .set("align-items", "center").set("justify-content", "center");*/
        return dialog;
    }

    public boolean isTextAreaEmptyOrBlank(TextArea textArea) {
        if (textArea == null) {
            return true; // Null text area is considered empty
        }

        String textAreaValue = textArea.getValue().trim();
        return textAreaValue.isEmpty();
    }

    public void refreshActGrid(Budget budget, URC_Priority_Areas urcPriorityAreas, List<UrcDeptSectionAnlDimbgt> deptSections) {
        gridUrc_Activities.setItems(sampleUrc_ActivitiesService.findActivitiesByBudgetAndPriorityAndDeptUnits(budget, urcPriorityAreas, deptSections));
    }

    public void refreshActGrid2(Budget budget, URC_Priority_Areas urcPriorityAreas, List<UrcDeptSectionAnlDimbgt> deptSections, String keyword) {
        gridUrc_Activities.setItems(sampleUrc_ActivitiesService.customSearchByFields(budget, urcPriorityAreas, deptSections, keyword));
    }

    private void populateForm(Urc_Activities object) {
        binder.readBean(object);

    }

    public void sheetData(Sheet sheet, Budget budget) {
        int i = 0;
        for (Row row : sheet) {
            i++;
            if (i > 1) {
                Urc_Activities activ = new Urc_Activities();

                // Check if the row is null
                if (row != null) {
                    // Assuming the name is in the first cell of each row (you can change it accordingly)
                    Cell cell = row.getCell(0); // 0 represents the first cell

                    // Check if the cell is not null and is of type STRING
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        int dateValue = (int) cell.getNumericCellValue();
                        URC_Priority_Areas urcPriorityArea = uRC_Priority_AreasService.getById((long) dateValue);
                        activ.setUrcPriorityAreas(urcPriorityArea);
                        activ.setBudget(budget);

                    } else if (cell != null && cell.getCellType() == CellType.STRING) {
                        String stringValue = cell.getStringCellValue();
                        //System.out.println("Text: " + stringValue);
                        int name = 0;
                        try {
                            name = Integer.parseInt(stringValue);
                            URC_Priority_Areas urcPriorityArea = uRC_Priority_AreasService.getById((long) name);
                            activ.setUrcPriorityAreas(urcPriorityArea);
                            activ.setBudget(budget);
                        } catch (NumberFormatException e) {

                        }

                    }

                    Cell cell1 = row.getCell(1); // 0 represents the first cell

                    // Check if the cell is not null and is of type STRING
                    if (cell1 != null && cell1.getCellType() == CellType.STRING) {
                        String names = cell1.getStringCellValue();
                        activ.setDeptSection(urcDeptSectionAnlDimbgtService.findByANL_CODE(names));

                    } else if (cell1 != null && cell1.getCellType() == CellType.NUMERIC) {
                        int n = (int) cell1.getNumericCellValue();
                        String names = Integer.toString(n);
                        activ.setDeptSection(urcDeptSectionAnlDimbgtService.findByANL_CODE(names));
                    }
                    Cell cell2 = row.getCell(2);
                    if (cell2 != null && cell2.getCellType() == CellType.STRING) {
                        String names = cell2.getStringCellValue();
                        activ.setName(names);
                    } else if (cell2 != null && cell2.getCellType() == CellType.NUMERIC) {
                        int n = (int) cell2.getNumericCellValue();
                        String names = Integer.toString(n);
                        activ.setName(names);
                    }
                    Cell cell3 = row.getCell(3);
                    if (cell3 != null && cell3.getCellType() == CellType.STRING) {
                        String names = cell3.getStringCellValue();
                        activ.setFundsource(names);
                    } else if (cell3 != null && cell3.getCellType() == CellType.NUMERIC) {
                        int n = (int) cell3.getNumericCellValue();
                        String names = Integer.toString(n);
                        activ.setFundsource(names);
                    }
                    Cell cell4 = row.getCell(4);
                    if (cell4 != null && cell4.getCellType() == CellType.STRING) {
                        String names = cell4.getStringCellValue();
                        activ.setPerformanceIndicator(names);
                    } else if (cell4 != null && cell4.getCellType() == CellType.NUMERIC) {
                        int n = (int) cell4.getNumericCellValue();
                        String names = Integer.toString(n);
                        activ.setPerformanceIndicator(names);
                    }
                    Cell cell5 = row.getCell(5);
                    if (cell5 != null && cell5.getCellType() == CellType.STRING) {
                        String names = cell5.getStringCellValue();
                        activ.setOutput(names);
                    } else if (cell5 != null && cell5.getCellType() == CellType.NUMERIC) {
                        int n = (int) cell5.getNumericCellValue();
                        String names = Integer.toString(n);
                        activ.setOutput(names);
                    }
                    Cell cell6 = row.getCell(6);
                    if (cell6 != null && cell6.getCellType() == CellType.STRING) {
                        String names = cell6.getStringCellValue();
                        activ.setOutcome(names);
                    } else if (cell6 != null && cell6.getCellType() == CellType.NUMERIC) {
                        int n = (int) cell6.getNumericCellValue();
                        String names = Integer.toString(n);
                        activ.setOutcome(names);
                    }
                    Cell cell7 = row.getCell(7);
                    if (cell7 != null && cell7.getCellType() == CellType.STRING) {
                        String names = cell7.getStringCellValue();
                        activ.setObjective(names);
                    } else if (cell7 != null && cell7.getCellType() == CellType.NUMERIC) {
                        int n = (int) cell7.getNumericCellValue();
                        String names = Integer.toString(n);
                        activ.setObjective(names);
                    }

                    String maxActivityCode = urc_ActivitiesService.maxActivityCode(activ.getDeptSection().getANL_CODE());
                    String nextActivityCode = "";
                    if (maxActivityCode != null) {
                        //int nextActivityCodeNumber = Integer.parseInt(maxActivityCode.substring(4)) + 1;
                        BigInteger nextActivityCodeNumber = new BigInteger(maxActivityCode.substring(7)).add(BigInteger.ONE);
                        nextActivityCode = "ZBA" + activ.getDeptSection().getANL_CODE().trim() + String.format("%07d", nextActivityCodeNumber);
                    } else {
                        nextActivityCode = "ZBA" + activ.getDeptSection().getANL_CODE().trim() + "0000001";
                    }

                    activ.setActivityCode(nextActivityCode);
                    sampleUrc_ActivitiesService.update(activ);
                }
            }

        }
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

    public Notification errorNotification(String b) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(
                new Text(b),
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

    private class Urc_ActivitiesGridContextMenu extends GridContextMenu<Urc_Activities> {

        public Urc_ActivitiesGridContextMenu(Grid<Urc_Activities> target) {
            super(target);
            addItem("Change Activity Programme", e -> e.getItem().ifPresent(person -> {
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle("Change Activity Programme");
                Span span = new Span();
                span.setText("Change Programme: " + person.getUrcPriorityAreas().getName() + " To:");
                span.getElement().getThemeList().add("badge success");
                VerticalLayout dialogLayout = new VerticalLayout();

                Grid<URC_Priority_Areas> gridCOA2 = new Grid<>(URC_Priority_Areas.class, false);
                gridCOA2.addColumn("name").setAutoWidth(true);
                gridCOA2.setItems(uRC_Priority_AreasService.findByBudget(person.getBudget()));
                gridCOA2.asSingleSelect().addValueChangeListener(vl -> {
                    span.setText("Change Programme: " + person.getUrcPriorityAreas().getName() + " To:" + vl.getValue().getName());
                });

                dialogLayout.add(span, gridCOA2);

                saveButton.addClickListener(ev -> {
                    if (!gridCOA2.asSingleSelect().isEmpty()) {
                        person.setUrcPriorityAreas(gridCOA2.asSingleSelect().getValue());
                        sampleUrc_ActivitiesService.update(person);
                        refreshActGrid(person.getBudget(), person.getUrcPriorityAreas(), comboBoxD_Section.getSelectedItems().stream().toList());
                        dialog.close();
                    } else {
                        Notificationwarning("Select an Account");
                    }
                });
                Button cancelButton = new Button("Cancel", ev -> dialog.close());
                dialog.getFooter().add(cancelButton);
                dialog.getFooter().add(saveButton);
                dialog.add(dialogLayout);
                dialog.open();
            }));
        }
    }

    public Notification Notificationwarning(String warning) {

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                // new Text("Your session will expire in 5 minutes due to inactivity."),
                // new HtmlComponent("br"),
                new Text(warning)
        );

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();

        notification.setPosition(Notification.Position.TOP_CENTER);

        return notification;
    }

    private String generateBudgetTypeCode(int index) {
        // Convert index to string with leading zeros
        String indexString = String.format("%02d", index);
        // Generate the string
        String generatedString = "ZBT" + indexString;
        return generatedString;
    }

    private String generateFundSourceCode(int index) {
        // Convert index to string with leading zeros
        String indexString = String.format("%02d", index);
        // Generate the string
        String generatedString = "ZBFS" + indexString;
        return generatedString;
    }

    private Dialog openProgramDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Import Budget Programmes");

        ComboBox<Budget> comboBox = new ComboBox<>("Source Budget");
        List<Budget> lists = chosenBudgetService.findAllExcept(budgetComboBox.getValue().getFinancialYear());
        comboBox.setItems(lists);
        comboBox.setItemLabelGenerator(Budget::getFinancialYear);

        Grid<URC_Priority_Areas> gridProgs = new Grid<>(URC_Priority_Areas.class, false);
        gridProgs.addColumn(URC_Priority_Areas::getName).setHeader("Programme");
        gridProgs.setSelectionMode(Grid.SelectionMode.MULTI);

        comboBox.addValueChangeListener(e -> {
            gridProgs.setItems(uRC_Priority_AreasService.findByBudget(e.getValue()));
        });

        VerticalLayout dialogLayout = new VerticalLayout(comboBox, gridProgs);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        dialog.add(dialogLayout);

        Button saveButton = new Button("Import Programmes", e -> {
            if (!gridProgs.asMultiSelect().isEmpty()) {
                Set<URC_Priority_Areas> lis = gridProgs.asMultiSelect().getValue();
                for (URC_Priority_Areas p : lis) {
                    URC_Priority_Areas ar = new URC_Priority_Areas();
                    ar.setBudget(budgetComboBox.getValue());
                    ar.setName(p.getName());
                    ar.setUrcStrategicPlan(p.getUrcStrategicPlan());
                    uRC_Priority_AreasService.update(ar);
                }
            }
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        return dialog;
    }

    private Dialog openActivityDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Import Budget Activities");

        ComboBox<Budget> comboBox = new ComboBox<>("Source Budget");
        List<Budget> lists = chosenBudgetService.findAllExcept(budgetComboBox.getValue().getFinancialYear());
        comboBox.setItems(lists);
        comboBox.setItemLabelGenerator(Budget::getFinancialYear);

        Grid<Urc_Activities> gridProgs = new Grid<>(Urc_Activities.class, false);
        gridProgs.addColumn(Urc_Activities::getName).setHeader("Activity");
        gridProgs.setSelectionMode(Grid.SelectionMode.MULTI);

        comboBox.addValueChangeListener(e -> {
            if (!comboBoxD_Section.isEmpty()) {
                int i = comboBoxD_Section.getSelectedItems().size();
                if (i > 0 && i < 2) {
                    Set<UrcDeptSectionAnlDimbgt> sel = comboBoxD_Section.getSelectedItems();
                    UrcDeptSectionAnlDimbgt sellected = sel.stream().findFirst().get();
                    gridProgs.setItems(sampleUrc_ActivitiesService.findByDeptSectionAndBudget(sellected, e.getValue()));
                } else {
                    Notification.show("Select cost centre");
                }
            }

        });

        VerticalLayout dialogLayout = new VerticalLayout(comboBox, gridProgs);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        dialog.add(dialogLayout);

        Button saveButton = new Button("Import Programmes", e -> {
            if (!gridProgs.asMultiSelect().isEmpty()) {
                Set<Urc_Activities> lis = gridProgs.asMultiSelect().getValue();
                for (Urc_Activities p : lis) {
                    Urc_Activities ar = new Urc_Activities();
                    ar.setBudget(budgetComboBox.getValue());
                    ar.setName(p.getName());
                    ar.setFundsource(p.getFundsource());
                    ar.setPerformanceIndicator(p.getPerformanceIndicator());
                    ar.setOutcome(p.getOutcome());
                    ar.setOutput(p.getOutput());
                    ar.setObjective(p.getObjective());
                    ar.setUrcPriorityAreas(gridView.asSingleSelect().getValue());
                    ar.setDeptSection(p.getDeptSection());
                    String maxActivityCode = urc_ActivitiesService.maxActivityCode(ar.getDeptSection().getANL_CODE());
                    System.out.println(maxActivityCode+"");
                    String nextActivityCode = "";
                    if (maxActivityCode != null) {
                        // int nextActivityCodeNumber = Integer.parseInt(maxActivityCode.substring(4)) + 1;
                        BigInteger nextActivityCodeNumber = new BigInteger(maxActivityCode.substring(7)).add(BigInteger.ONE);
                        nextActivityCode = "ZBA" + ar.getDeptSection().getANL_CODE().trim() + String.format("%07d", nextActivityCodeNumber);
                    } else {
                        nextActivityCode = "ZBA" + ar.getDeptSection().getANL_CODE().trim() + "0000001";
                    }
                    ar.setActivityCode(nextActivityCode);
                    sampleUrc_ActivitiesService.update(ar);
                    comboBoxD_Section.deselectAll();
                }
            }
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        return dialog;
    }
}
