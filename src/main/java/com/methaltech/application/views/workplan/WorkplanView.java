package com.methaltech.application.views.workplan;

import com.methaltech.application.data.bgtool.repository.SamplePersonService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.COAReconcileService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.COAReconcile;
import com.methaltech.application.data.entity.bgtool.SamplePerson;
import com.methaltech.application.data.entity.oldbgtool.BudgetSubItem;
import com.methaltech.application.data.entity.oldbgtool.DepartmentSection;
import com.methaltech.application.data.entity.oldbgtool.DepartmentUnit;
import com.methaltech.application.data.entity.oldbgtool.OldBudget;
import com.methaltech.application.data.entity.oldbgtool.OldBudgetSubItem;
import com.methaltech.application.data.entity.oldbgtool.OldItem;
import com.methaltech.application.data.entity.oldbgtool.ProgramActivity;
import com.methaltech.application.data.entity.oldbgtool.Programme;
import com.methaltech.application.data.entity.oldbgtool.RowsWorkplan;
import com.methaltech.application.data.oldbgtool.service.ItemService2;
import com.methaltech.application.data.oldbgtool.service.BudgetSubItemService;
import com.methaltech.application.data.oldbgtool.service.DepartmentSectionService;
import com.methaltech.application.data.oldbgtool.service.DepartmentUnitService;
import com.methaltech.application.data.oldbgtool.service.OldBudgetService;
import com.methaltech.application.data.oldbgtool.service.OldItemService;
import com.methaltech.application.data.oldbgtool.service.ProgramActivityService;
import com.methaltech.application.data.oldbgtool.service.ProgrammeService;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@PageTitle("Work Plan Extractor")
@Route(value = "workplan", layout = MainLayout.class)
//@RolesAllowed("USER")
@RolesAllowed({"ADMIN", "BLO", "USER", "HOD"})
@Uses(Icon.class)
public class WorkplanView extends VerticalLayout {

    private final ComboBox<OldBudget> comboBox = new ComboBox<>("Select Budget");
    private final UserService userService;
    private final OldBudgetService oldbudgetService;
    private final BudgetService budgetService;
    private final DepartmentSectionService departmentsectionService;
    private final DepartmentUnitService departmentUnitService;
    private final SamplePersonService samplePersonService;
    private final ProgrammeService programmeService;
    private final ProgramActivityService programmeActivityService;
    private final BudgetSubItemService budgetSubItemService;
    private final ItemService2 itemService;
    private Grid<DepartmentUnit> select = new Grid<>(DepartmentUnit.class, false);
    private final List<DepartmentUnit> selected = new ArrayList<>();
    private final List<SamplePerson> person = new ArrayList<>();
    private List<DepartmentSection> selectedSections = new ArrayList<>();
    private List<Programme> programmes = new ArrayList<>();
    private List<ProgramActivity> programmesActivities = new ArrayList<>();
    private List<BudgetSubItem> budgetList = new ArrayList<>();
    private final COAReconcileService coaReconcileService;
    private final OldItemService oldItemService;
    private final CoaService coaService;
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public WorkplanView(UserService userService, OldBudgetService oldbudgetService,
            DepartmentSectionService departmentsectionService, DepartmentUnitService departmentUnitService,
            SamplePersonService samplePersonService, ProgrammeService programmeService,
            ProgramActivityService programmeActivityService, BudgetSubItemService budgetSubItemService,
            ItemService2 itemService, COAReconcileService coaReconcileService, OldItemService oldItemService,
            CoaService coaService, BudgetService budgetService) {
        this.userService = userService;
        this.oldbudgetService = oldbudgetService;
        this.budgetService = budgetService;
        this.departmentsectionService = departmentsectionService;
        this.departmentUnitService = departmentUnitService;
        this.samplePersonService = samplePersonService;
        this.programmeService = programmeService;
        this.programmeActivityService = programmeActivityService;
        this.budgetSubItemService = budgetSubItemService;
        this.itemService = itemService;
        this.coaReconcileService = coaReconcileService;
        this.oldItemService = oldItemService;
        this.coaService = coaService;
        setSpacing(false);

        //H2 header = new H2("This place intentionally left empty");
        //header.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);
        // add(header);
        comboBox.setItems(oldbudgetService.getAllBudgets());
        comboBox.setItemLabelGenerator(OldBudget::getFinancialYear);

        select.setItems(departmentUnitService.getDepartmentUnits());
        select.setSelectionMode(Grid.SelectionMode.MULTI);
        select.addColumn(DepartmentUnit::getUnit).setHeader("DEPARTMENT UNIT")
                .setAutoWidth(true);

        //add(hlay);
        HorizontalLayout hlay = new HorizontalLayout();
        Button but = new Button("Submit to Extract Work plan");
        but.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button details = new Button("Submit Budget with Activities");
        Button details2 = new Button("Dowmload Old Budget");
        Button details3 = new Button("Dowmload Old Budget new code");
        Button organogram = new Button("Organogram");
        details.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        details2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        details3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        organogram.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        but.addClickListener(e -> {
            if (select.asMultiSelect().getSelectedItems().isEmpty() || comboBox.isEmpty()) {
                warningNotification("Select altleast one Unit & Budget");
            } else {
                exportAndDownloadExcel();
            }

        });
        details.addClickListener(xy -> {
            if (select.asMultiSelect().getSelectedItems().isEmpty() || comboBox.isEmpty()) {
                warningNotification("Select altleast one Unit & Budget");
            } else {
                exportAndDownloadActvityBudgetItems(comboBox.getValue().getFinancialYear());
            }
        });
        details2.addClickListener(xy -> {
            if (select.asMultiSelect().getSelectedItems().isEmpty() || comboBox.isEmpty()) {
                warningNotification("Select altleast one Unit & Budget");
            } else {
                exportAndDownloadActvityBudgetItems2(comboBox.getValue().getFinancialYear());
            }
        });
        details3.addClickListener(xy -> {
            if (select.asMultiSelect().getSelectedItems().isEmpty() || comboBox.isEmpty()) {
                warningNotification("Select altleast one Unit & Budget");
            } else {
                exportAndDownloadActvityBudgetItems3(comboBox.getValue().getFinancialYear());
            }
        });
        organogram.addClickListener(ev -> {

        });

        hlay.add(comboBox, but, details, details2, details3);
        hlay.setVerticalComponentAlignment(FlexComponent.Alignment.END, but);
        hlay.setAlignItems(FlexComponent.Alignment.BASELINE);
        add(hlay);
        add(select);
        setSizeFull();
        //setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        //setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        //getStyle().set("text-align", "center");
    }

    private void exportAndDownloadActvityBudgetItems(String fy) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget By Activities " + fy);
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderAndBodyActivitiesByBudgetRow(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Budget By Activities " + fy + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadActvityBudgetItems2(String fy) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Old Budget By " + fy);
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderAndBodyActivitiesByBudgetRow2(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Old Budget " + fy + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadActvityBudgetItems3(String fy) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Old Budget By " + fy);
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderAndBodyActivitiesByBudgetRow3(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Old Budget " + fy + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Work plan");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderRow(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Workplan.xlsx", ()
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

    private void exportAndDownloadExcelSectionCOA() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Setion Chart Of Accounts");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(false);
            createHeaderRow(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("Setion Chart Of Accounts.xlsx", ()
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

    private void createHeaderAndBodySectionCOA(Workbook workbook, Sheet sheet) {
        short tr = 0;
        Row rowhead01 = sheet.createRow((short) tr);
        rowhead01.createCell((short) tr).setCellValue("UGANDA RAILWAYS CORPORATION BUDGET BY ACTIVITY");
        tr++;
        Row rowhead0a = sheet.createRow((short) tr);
    }

    private void createHeaderAndBodyActivitiesByBudgetRow(Workbook workbook, Sheet sheet) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.BLUE_GREY.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //style.setFillPattern ( PatternFormatting.SPARSE_DOTS );
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 15);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        CellStyle style1 = workbook.createCellStyle();
        style1.setFillForegroundColor(IndexedColors.BLUE_GREY.index);
        style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style1.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        //style.setFillPattern ( PatternFormatting.SPARSE_DOTS );
        Font font1 = workbook.createFont();
        font1.setColor(IndexedColors.WHITE.getIndex());
        font1.setFontHeightInPoints((short) 10);
        font1.setBold(true);
        style1.setFont(font1);
        style1.setAlignment(HorizontalAlignment.LEFT);

        CellStyle style11 = workbook.createCellStyle();
        style11.setFillForegroundColor(IndexedColors.BLUE_GREY.index);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        //style.setFillPattern ( PatternFormatting.SPARSE_DOTS );
        Font font11 = workbook.createFont();
        font11.setColor(IndexedColors.WHITE.getIndex());
        font11.setFontHeightInPoints((short) 10);
        font11.setBold(true);
        style11.setFont(font11);
        style11.setAlignment(HorizontalAlignment.LEFT);

        //Style 2
        CellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.index);
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style2.setAlignment(HorizontalAlignment.CENTER);

        //style.setFillPattern ( PatternFormatting.SPARSE_DOTS );
        Font font2 = workbook.createFont();
        font2.setColor(IndexedColors.WHITE.getIndex());
        font2.setFontHeightInPoints((short) 10);
        font2.setBold(true);
        style2.setFont(font2);

        CellStyle style3 = workbook.createCellStyle();
        style3.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.index);
        style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style3.setAlignment(HorizontalAlignment.LEFT);

        //style.setFillPattern ( PatternFormatting.SPARSE_DOTS );
        Font font3 = workbook.createFont();
        // font2.setColor(IndexedColors.WHITE.getIndex());
        font3.setFontHeightInPoints((short) 10);
        font3.setBold(true);
        style3.setFont(font3);
        short tr = 0;

        Row rowhead01 = sheet.createRow((short) tr);
        rowhead01.createCell((short) tr).setCellValue("UGANDA RAILWAYS CORPORATION BUDGET BY ACTIVITY");

        rowhead01.getCell(0).setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
        tr++;
        Row rowhead0a = sheet.createRow((short) tr);

        rowhead0a.createCell(0).setCellValue(listOfUnitsNameSelected().toUpperCase() + " " + comboBox.getValue().getFinancialYear());

        sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
        rowhead0a.getCell(0).setCellStyle(style2);
        tr++;
        if (checkIfTotalByExpenditure_Fy_UnitString("2", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {
            Row rowhead0b = sheet.createRow((short) tr);
            rowhead0b.createCell(0).setCellValue(("REVENUE EXPENDITURE BUDGET").toUpperCase());
            sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            rowhead0b.getCell(0).setCellStyle(style2);
            sheet.createFreezePane(2, 3);
            programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
            for (Programme prog : programmes) {
                programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
                if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {
                    tr++;
                    Row rowin = sheet.createRow((short) tr);
                    rowin.createCell(0).setCellValue((prog.getProgramme()).toUpperCase());
                    sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                    rowin.getCell(0).setCellStyle(style3);
                    for (ProgramActivity d : programmesActivities) {
                        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {
                            tr++;
                            Row rowin2 = sheet.createRow((short) tr);
                            rowin2.createCell(0).setCellValue((d.getActivities()));
                            sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                            rowin2.getCell(0).setCellStyle(style2);
                            budgetList = budgetSubItemService.BudgetByActivitiesByUnitByCategoryByFy(d.getId(), "2", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

                            tr++;
                            int place = budgetList.size() + tr;

                            Row Q2 = sheet.createRow((short) tr);
                            Q2.createCell((short) 0).setCellValue("CODE");
                            Q2.createCell((short) 1).setCellValue("ITEM");
                            Q2.createCell((short) 2).setCellValue("TOTAL/UGX");
                            Q2.createCell((short) 3).setCellValue("JUL/UGX");
                            Q2.createCell((short) 4).setCellValue("AUG/UGX");
                            Q2.createCell((short) 5).setCellValue("SEP/UGX");
                            Q2.createCell((short) 6).setCellValue("OCT/UGX");
                            Q2.createCell((short) 7).setCellValue("NOV/UGX");
                            Q2.createCell((short) 8).setCellValue("DEC/UGX");
                            Q2.createCell((short) 9).setCellValue("JAN/UGX");
                            Q2.createCell((short) 10).setCellValue("FEB/UGX");
                            Q2.createCell((short) 11).setCellValue("MAR/UGX");
                            Q2.createCell((short) 12).setCellValue("APR/UGX");
                            Q2.createCell((short) 13).setCellValue("MAY/UGX");
                            Q2.createCell((short) 14).setCellValue("JUN/UGX");
                            Q2.createCell((short) 15).setCellValue("COST");
                            Q2.createCell((short) 16).setCellValue("QTY");
                            Q2.createCell((short) 17).setCellValue("CUR");
                            Q2.createCell((short) 18).setCellValue("Number of Days");
                            Q2.createCell((short) 19).setCellValue("Target Group");
                            Q2.createCell((short) 20).setCellValue("Trainer");
                            Q2.createCell((short) 21).setCellValue("Notes");
                            Q2.createCell((short) 22).setCellValue("Unit");

                            for (BudgetSubItem tt : budgetList) {
                                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("2")) {
                                    tr++;
                                    Row mm = sheet.createRow((short) tr);
                                    mm.createCell((short) 0).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                                    mm.createCell((short) 1).setCellValue(tt.getItem());
                                    mm.createCell((short) 2).setCellValue(decimalFormat.format(tt.getTotal()));
                                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getJul()));
                                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getAug()));
                                    mm.createCell((short) 5).setCellValue(decimalFormat.format(tt.getSep()));
                                    mm.createCell((short) 6).setCellValue(decimalFormat.format(tt.getOct()));
                                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getNov()));
                                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getDec()));
                                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getJan()));
                                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getFeb()));
                                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getMar()));
                                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getApr()));
                                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getMay()));
                                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getJun()));
                                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getCost()));
                                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getQuantity()));
                                    mm.createCell((short) 17).setCellValue(tt.getCurrency());
                                    mm.createCell((short) 18).setCellValue(tt.getNumberOfDays());
                                    mm.createCell((short) 19).setCellValue(tt.getTargetGroup());
                                    mm.createCell((short) 20).setCellValue(tt.getExpectedTrainer());
                                    mm.createCell((short) 21).setCellValue(tt.getNotes());

                                    mm.createCell((short) 22).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                                }

                            }
                        }
                    }
                }
            }
        }

        tr++;
        if (checkIfTotalByExpenditure_Fy_UnitString("3", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {
            Row rowhead0b = sheet.createRow((short) tr);
            rowhead0b.createCell(0).setCellValue(("CAPITAL EXPENDITURE BUDGET").toUpperCase());
            sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
            rowhead0b.getCell(0).setCellStyle(style2);
            //sheet.createFreezePane(2, 3);
            programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
            for (Programme prog : programmes) {
                programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
                if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {
                    tr++;
                    Row rowin = sheet.createRow((short) tr);
                    rowin.createCell(0).setCellValue((prog.getProgramme()).toUpperCase());
                    sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                    rowin.getCell(0).setCellStyle(style3);
                    for (ProgramActivity d : programmesActivities) {
                        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {
                            tr++;
                            Row rowin2 = sheet.createRow((short) tr);
                            rowin2.createCell(0).setCellValue((d.getActivities()));
                            sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                            rowin2.getCell(0).setCellStyle(style2);
                            budgetList = budgetSubItemService.BudgetByActivitiesByUnitByCategoryByFy(d.getId(), "3", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

                            tr++;
                            int place = budgetList.size() + tr;

                            Row Q2 = sheet.createRow((short) tr);
                            Q2.createCell((short) 0).setCellValue("CODE");
                            Q2.createCell((short) 1).setCellValue("ITEM");
                            Q2.createCell((short) 2).setCellValue("TOTAL/UGX");
                            Q2.createCell((short) 3).setCellValue("JUL/UGX");
                            Q2.createCell((short) 4).setCellValue("AUG/UGX");
                            Q2.createCell((short) 5).setCellValue("SEP/UGX");
                            Q2.createCell((short) 6).setCellValue("OCT/UGX");
                            Q2.createCell((short) 7).setCellValue("NOV/UGX");
                            Q2.createCell((short) 8).setCellValue("DEC/UGX");
                            Q2.createCell((short) 9).setCellValue("JAN/UGX");
                            Q2.createCell((short) 10).setCellValue("FEB/UGX");
                            Q2.createCell((short) 11).setCellValue("MAR/UGX");
                            Q2.createCell((short) 12).setCellValue("APR/UGX");
                            Q2.createCell((short) 13).setCellValue("MAY/UGX");
                            Q2.createCell((short) 14).setCellValue("JUN/UGX");
                            Q2.createCell((short) 15).setCellValue("COST");
                            Q2.createCell((short) 16).setCellValue("QTY");
                            Q2.createCell((short) 17).setCellValue("CUR");
                            Q2.createCell((short) 18).setCellValue("Number of Days");
                            Q2.createCell((short) 19).setCellValue("Target Group");
                            Q2.createCell((short) 20).setCellValue("Trainer");
                            Q2.createCell((short) 21).setCellValue("Notes");
                            Q2.createCell((short) 22).setCellValue("Unit");
                            for (BudgetSubItem tt : budgetList) {
                                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("3")) {
                                    tr++;
                                    Row mm = sheet.createRow((short) tr);
                                    mm.createCell((short) 0).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                                    mm.createCell((short) 1).setCellValue(tt.getItem());
                                    mm.createCell((short) 2).setCellValue(decimalFormat.format(tt.getTotal()));
                                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getJul()));
                                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getAug()));
                                    mm.createCell((short) 5).setCellValue(decimalFormat.format(tt.getSep()));
                                    mm.createCell((short) 6).setCellValue(decimalFormat.format(tt.getOct()));
                                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getNov()));
                                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getDec()));
                                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getJan()));
                                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getFeb()));
                                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getMar()));
                                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getApr()));
                                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getMay()));
                                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getJun()));
                                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getCost()));
                                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getQuantity()));
                                    mm.createCell((short) 17).setCellValue(tt.getCurrency());
                                    mm.createCell((short) 18).setCellValue(tt.getNumberOfDays());
                                    mm.createCell((short) 19).setCellValue(tt.getTargetGroup());
                                    mm.createCell((short) 20).setCellValue(tt.getExpectedTrainer());
                                    mm.createCell((short) 21).setCellValue(tt.getNotes());
                                    mm.createCell((short) 15).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private void createHeaderAndBodyActivitiesByBudgetRow2(Workbook workbook, Sheet sheet) {

        short tr = 0;
        Row Q2 = sheet.createRow((short) tr);
        Q2.createCell((short) 0).setCellValue("ACTIVITY CODE");
        Q2.createCell((short) 1).setCellValue("COA CODE");
        Q2.createCell((short) 2).setCellValue("ITEM");
        Q2.createCell((short) 3).setCellValue("COST");
        Q2.createCell((short) 4).setCellValue("QTY");
        Q2.createCell((short) 5).setCellValue("Unit Measure");
        Q2.createCell((short) 6).setCellValue("CUR");
        Q2.createCell((short) 7).setCellValue("JUL/UGX");
        Q2.createCell((short) 8).setCellValue("AUG/UGX");
        Q2.createCell((short) 9).setCellValue("SEP/UGX");
        Q2.createCell((short) 10).setCellValue("OCT/UGX");
        Q2.createCell((short) 11).setCellValue("NOV/UGX");
        Q2.createCell((short) 12).setCellValue("DEC/UGX");
        Q2.createCell((short) 13).setCellValue("JAN/UGX");
        Q2.createCell((short) 14).setCellValue("FEB/UGX");
        Q2.createCell((short) 15).setCellValue("MAR/UGX");
        Q2.createCell((short) 16).setCellValue("APR/UGX");
        Q2.createCell((short) 17).setCellValue("MAY/UGX");
        Q2.createCell((short) 18).setCellValue("JUN/UGX");
        Q2.createCell((short) 19).setCellValue("Number of Days");
        Q2.createCell((short) 20).setCellValue("Target Group");
        Q2.createCell((short) 21).setCellValue("Trainer");
        Q2.createCell((short) 22).setCellValue("Notes");
        Q2.createCell((short) 23).setCellValue("Unit");
        if (checkIfTotalByExpenditure_Fy_UnitString("1", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {

            budgetList = budgetSubItemService.BudgetByUnitByCategoryByFy("1", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

            for (BudgetSubItem tt : budgetList) {
                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("1") && isSumOfMonthsGreaterThanZero(tt) == true) {
                    tr++;
                    Row mm = sheet.createRow((short) tr);
                    mm.createCell((short) 0).setCellValue("");
                    mm.createCell((short) 1).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                    mm.createCell((short) 2).setCellValue(tt.getItem());
                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQuantity()));
                    mm.createCell((short) 5).setCellValue(tt.getItemUnit());
                    mm.createCell((short) 6).setCellValue(tt.getCurrency());
                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getJul()));
                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getAug()));
                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getSep()));
                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getOct()));
                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getNov()));
                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getDec()));
                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getJan()));
                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getFeb()));
                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getMar()));
                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getApr()));
                    mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getMay()));
                    mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getJun()));
                    mm.createCell((short) 19).setCellValue(tt.getNumberOfDays());
                    mm.createCell((short) 20).setCellValue(tt.getTargetGroup());
                    mm.createCell((short) 21).setCellValue(tt.getExpectedTrainer());
                    mm.createCell((short) 22).setCellValue(tt.getNotes());
                    mm.createCell((short) 23).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                }

            }

        }
        if (checkIfTotalByExpenditure_Fy_UnitString("2", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {

            programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
            for (Programme prog : programmes) {

                programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
                if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {

                    for (ProgramActivity d : programmesActivities) {
                        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {

                            tr++;
                            Row rowin = sheet.createRow((short) tr);
                            rowin.createCell(0).setCellValue(("(" + prog.getProgramme() + ") " + d.getActivities()).toUpperCase());
                            sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                            budgetList = budgetSubItemService.BudgetByActivitiesByUnitByCategoryByFy(d.getId(), "2", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

                            for (BudgetSubItem tt : budgetList) {
                                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("2") && isSumOfMonthsGreaterThanZero(tt) == true) {
                                    tr++;
                                    Row mm = sheet.createRow((short) tr);
                                    mm.createCell((short) 0).setCellValue("");
                                    mm.createCell((short) 1).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                                    mm.createCell((short) 2).setCellValue(tt.getItem());
                                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQuantity()));
                                    mm.createCell((short) 5).setCellValue(tt.getItemUnit());
                                    mm.createCell((short) 6).setCellValue(tt.getCurrency());
                                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getJul()));
                                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getAug()));
                                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getSep()));
                                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getOct()));
                                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getNov()));
                                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getDec()));
                                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getJan()));
                                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getFeb()));
                                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getMar()));
                                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getApr()));
                                    mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getMay()));
                                    mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getJun()));
                                    mm.createCell((short) 19).setCellValue(tt.getNumberOfDays());
                                    mm.createCell((short) 20).setCellValue(tt.getTargetGroup());
                                    mm.createCell((short) 21).setCellValue(tt.getExpectedTrainer());
                                    mm.createCell((short) 22).setCellValue(tt.getNotes());
                                    mm.createCell((short) 23).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                                }

                            }
                        }
                    }
                }
            }
        }

        if (checkIfTotalByExpenditure_Fy_UnitString("3", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {

            programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
            for (Programme prog : programmes) {
                programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
                if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {

                    for (ProgramActivity d : programmesActivities) {
                        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {
                            tr++;
                            Row rowin = sheet.createRow((short) tr);
                            rowin.createCell(0).setCellValue(("(" + prog.getProgramme() + ") " + d.getActivities()).toUpperCase());
                            sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                            budgetList = budgetSubItemService.BudgetByActivitiesByUnitByCategoryByFy(d.getId(), "3", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

                            for (BudgetSubItem tt : budgetList) {
                                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("3") && isSumOfMonthsGreaterThanZero(tt) == true) {
                                    tr++;
                                    Row mm = sheet.createRow((short) tr);
                                    mm.createCell((short) 0).setCellValue("");
                                    mm.createCell((short) 1).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                                    mm.createCell((short) 2).setCellValue(tt.getItem());
                                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQuantity()));
                                    mm.createCell((short) 5).setCellValue(tt.getItemUnit());
                                    mm.createCell((short) 6).setCellValue(tt.getCurrency());
                                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getJul()));
                                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getAug()));
                                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getSep()));
                                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getOct()));
                                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getNov()));
                                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getDec()));
                                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getJan()));
                                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getFeb()));
                                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getMar()));
                                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getApr()));
                                    mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getMay()));
                                    mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getJun()));
                                    mm.createCell((short) 19).setCellValue(tt.getNumberOfDays());
                                    mm.createCell((short) 20).setCellValue(tt.getTargetGroup());
                                    mm.createCell((short) 21).setCellValue(tt.getExpectedTrainer());
                                    mm.createCell((short) 22).setCellValue(tt.getNotes());
                                    mm.createCell((short) 23).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private void createHeaderAndBodyActivitiesByBudgetRow3(Workbook workbook, Sheet sheet) {

        short tr = 0;
        Row Q2 = sheet.createRow((short) tr);
        Q2.createCell((short) 0).setCellValue("ACTIVITY CODE");
        Q2.createCell((short) 1).setCellValue("COA CODE");
        Q2.createCell((short) 2).setCellValue("ITEM");
        Q2.createCell((short) 3).setCellValue("COST");
        Q2.createCell((short) 4).setCellValue("QTY");
        Q2.createCell((short) 5).setCellValue("Unit Measure");
        Q2.createCell((short) 6).setCellValue("CUR");
        Q2.createCell((short) 7).setCellValue("JUL/UGX");
        Q2.createCell((short) 8).setCellValue("AUG/UGX");
        Q2.createCell((short) 9).setCellValue("SEP/UGX");
        Q2.createCell((short) 10).setCellValue("OCT/UGX");
        Q2.createCell((short) 11).setCellValue("NOV/UGX");
        Q2.createCell((short) 12).setCellValue("DEC/UGX");
        Q2.createCell((short) 13).setCellValue("JAN/UGX");
        Q2.createCell((short) 14).setCellValue("FEB/UGX");
        Q2.createCell((short) 15).setCellValue("MAR/UGX");
        Q2.createCell((short) 16).setCellValue("APR/UGX");
        Q2.createCell((short) 17).setCellValue("MAY/UGX");
        Q2.createCell((short) 18).setCellValue("JUN/UGX");
        Q2.createCell((short) 19).setCellValue("Number of Days");
        Q2.createCell((short) 20).setCellValue("Target Group");
        Q2.createCell((short) 21).setCellValue("Trainer");
        Q2.createCell((short) 22).setCellValue("Notes");
        Q2.createCell((short) 23).setCellValue("Unit");
        if (checkIfTotalByExpenditure_Fy_UnitString("1", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {

            budgetList = budgetSubItemService.BudgetByUnitByCategoryByFy("1", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

            for (BudgetSubItem tt : budgetList) {
                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("1") && isSumOfMonthsGreaterThanZero(tt) == true) {
                    tr++;
                    Row mm = sheet.createRow((short) tr);
                    mm.createCell((short) 0).setCellValue("");

                    if (!itemService.getItemById(tt.getCodeId()).getCode().isEmpty() || itemService.getItemById(tt.getCodeId()).getCode() != null) {
                        if (budgetService.getBudget(comboBox.getValue().getFinancialYear())) {
                            //String fy=budgetService.getBudget(comboBox.getValue().getFinancialYear());
                            Budget newBudget = budgetService.getByFY(comboBox.getValue().getFinancialYear()).get();
                            //  mm.createCell((short) 1).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                            System.out.println(itemService.getItemById(tt.getCodeId()).getCode());
                            COA c = setCOA(itemService.getItemById(tt.getCodeId()).getCode(), newBudget);
                            mm.createCell((short) 1).setCellValue(c.getCode());

                        }
                    }

                    mm.createCell((short) 2).setCellValue(tt.getItem());
                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQuantity()));
                    mm.createCell((short) 5).setCellValue(tt.getItemUnit());
                    mm.createCell((short) 6).setCellValue(tt.getCurrency());
                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getJul()));
                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getAug()));
                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getSep()));
                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getOct()));
                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getNov()));
                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getDec()));
                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getJan()));
                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getFeb()));
                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getMar()));
                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getApr()));
                    mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getMay()));
                    mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getJun()));
                    mm.createCell((short) 19).setCellValue(tt.getNumberOfDays());
                    mm.createCell((short) 20).setCellValue(tt.getTargetGroup());
                    mm.createCell((short) 21).setCellValue(tt.getExpectedTrainer());
                    mm.createCell((short) 22).setCellValue(tt.getNotes());
                    mm.createCell((short) 23).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                }

            }

        }
        if (checkIfTotalByExpenditure_Fy_UnitString("2", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {

            programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
            for (Programme prog : programmes) {

                programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
                if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {

                    for (ProgramActivity d : programmesActivities) {
                        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {

                            tr++;
                            Row rowin = sheet.createRow((short) tr);
                            rowin.createCell(0).setCellValue(("(" + prog.getProgramme() + ") " + d.getActivities()).toUpperCase());
                            sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                            budgetList = budgetSubItemService.BudgetByActivitiesByUnitByCategoryByFy(d.getId(), "2", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

                            for (BudgetSubItem tt : budgetList) {
                                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("2") && isSumOfMonthsGreaterThanZero(tt) == true) {
                                    tr++;
                                    Row mm = sheet.createRow((short) tr);
                                    mm.createCell((short) 0).setCellValue("");
                                    mm.createCell((short) 1).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                                    mm.createCell((short) 2).setCellValue(tt.getItem());
                                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQuantity()));
                                    mm.createCell((short) 5).setCellValue(tt.getItemUnit());
                                    mm.createCell((short) 6).setCellValue(tt.getCurrency());
                                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getJul()));
                                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getAug()));
                                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getSep()));
                                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getOct()));
                                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getNov()));
                                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getDec()));
                                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getJan()));
                                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getFeb()));
                                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getMar()));
                                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getApr()));
                                    mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getMay()));
                                    mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getJun()));
                                    mm.createCell((short) 19).setCellValue(tt.getNumberOfDays());
                                    mm.createCell((short) 20).setCellValue(tt.getTargetGroup());
                                    mm.createCell((short) 21).setCellValue(tt.getExpectedTrainer());
                                    mm.createCell((short) 22).setCellValue(tt.getNotes());
                                    mm.createCell((short) 23).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                                }

                            }
                        }
                    }
                }
            }
        }

        if (checkIfTotalByExpenditure_Fy_UnitString("3", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {

            programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
            for (Programme prog : programmes) {
                programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
                if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {

                    for (ProgramActivity d : programmesActivities) {
                        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {
                            tr++;
                            Row rowin = sheet.createRow((short) tr);
                            rowin.createCell(0).setCellValue(("(" + prog.getProgramme() + ") " + d.getActivities()).toUpperCase());
                            //sheet.addMergedRegion(new CellRangeAddress(tr, tr, 0, 23));
                            CellRangeAddress newMergedRegion = new CellRangeAddress(tr, tr, 0, 23);
                            if (!isOverlappingWithExistingRegions(sheet, newMergedRegion)) {
                                addMergedRegion(sheet, newMergedRegion);
                            } else {
                                // Handle overlapping regions (e.g., adjust the region or skip it)
                            }

                            budgetList = budgetSubItemService.BudgetByActivitiesByUnitByCategoryByFy(d.getId(), "3", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected());

                            for (BudgetSubItem tt : budgetList) {
                                if (itemService.getItemById(tt.getCodeId()).getCode().substring(0, 1).equals("3") && isSumOfMonthsGreaterThanZero(tt) == true) {
                                    tr++;
                                    Row mm = sheet.createRow((short) tr);
                                    mm.createCell((short) 0).setCellValue("");
                                    mm.createCell((short) 1).setCellValue(itemService.getItemById(tt.getCodeId()).getCode());
                                    mm.createCell((short) 2).setCellValue(tt.getItem());
                                    mm.createCell((short) 3).setCellValue(decimalFormat.format(tt.getCost()));
                                    mm.createCell((short) 4).setCellValue(decimalFormat.format(tt.getQuantity()));
                                    mm.createCell((short) 5).setCellValue(tt.getItemUnit());
                                    mm.createCell((short) 6).setCellValue(tt.getCurrency());
                                    mm.createCell((short) 7).setCellValue(decimalFormat.format(tt.getJul()));
                                    mm.createCell((short) 8).setCellValue(decimalFormat.format(tt.getAug()));
                                    mm.createCell((short) 9).setCellValue(decimalFormat.format(tt.getSep()));
                                    mm.createCell((short) 10).setCellValue(decimalFormat.format(tt.getOct()));
                                    mm.createCell((short) 11).setCellValue(decimalFormat.format(tt.getNov()));
                                    mm.createCell((short) 12).setCellValue(decimalFormat.format(tt.getDec()));
                                    mm.createCell((short) 13).setCellValue(decimalFormat.format(tt.getJan()));
                                    mm.createCell((short) 14).setCellValue(decimalFormat.format(tt.getFeb()));
                                    mm.createCell((short) 15).setCellValue(decimalFormat.format(tt.getMar()));
                                    mm.createCell((short) 16).setCellValue(decimalFormat.format(tt.getApr()));
                                    mm.createCell((short) 17).setCellValue(decimalFormat.format(tt.getMay()));
                                    mm.createCell((short) 18).setCellValue(decimalFormat.format(tt.getJun()));
                                    mm.createCell((short) 19).setCellValue(tt.getNumberOfDays());
                                    mm.createCell((short) 20).setCellValue(tt.getTargetGroup());
                                    mm.createCell((short) 21).setCellValue(tt.getExpectedTrainer());
                                    mm.createCell((short) 22).setCellValue(tt.getNotes());
                                    mm.createCell((short) 23).setCellValue(departmentUnitService.getUnitById(tt.getDeptUnit()).getUnit());
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private void createOrganogram(Workbook workbook, Sheet sheet) {

    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.CENTER);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        Row row0 = sheet.createRow(0);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(stylec);
        cellq.setCellValue("UNIT WORKPLAN");

        Row row = sheet.createRow(1);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(stylec);
        cell.setCellValue("Programme");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(stylec);
        cell2.setCellValue("Activities");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(stylec);
        cell3.setCellValue("Budget");
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(stylec);
        cell4.setCellValue("Funding");
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(stylec);
        cell5.setCellValue("Time Line Quarterly");

        Cell cell6 = row.createCell((short) 16);
        row.getCell(16).setCellStyle(stylec);
        cell6.setCellValue("Output");
        Cell cell7 = row.createCell((short) 17);
        row.getCell(17).setCellStyle(stylec);
        cell7.setCellValue("Performance Indicator");
        Cell cell8 = row.createCell((short) 18);
        row.getCell(18).setCellStyle(stylec);
        cell8.setCellValue("Outcome");
        Cell cell9 = row.createCell((short) 19);
        row.getCell(19).setCellStyle(stylec);
        cell9.setCellValue("Strategic Objective");
        Cell cell10 = row.createCell((short) 20);
        row.getCell(20).setCellStyle(stylec);
        cell10.setCellValue("Remarks");

        Row row1 = sheet.createRow(2);
        Cell cella = row1.createCell((short) 4);
        row1.getCell(4).setCellStyle(style);
        cella.setCellValue("Q1");
        Cell cellb = row1.createCell((short) 7);
        row1.getCell(7).setCellStyle(style);
        cellb.setCellValue("Q2");
        Cell cellc = row1.createCell((short) 10);
        row1.getCell(10).setCellStyle(style);
        cellc.setCellValue("Q3");
        Cell celld = row1.createCell((short) 13);
        row1.getCell(13).setCellStyle(style);
        celld.setCellValue("Q4");

        Row row2 = sheet.createRow(3);
        Cell cella1 = row2.createCell((short) 4);
        row2.getCell(4).setCellStyle(styleq);
        cella1.setCellValue("Jul");
        Cell cellb1 = row2.createCell((short) 5);
        row2.getCell(5).setCellStyle(styleq);
        cellb1.setCellValue("Aug");
        Cell cellc1 = row2.createCell((short) 6);
        row2.getCell(6).setCellStyle(styleq);
        cellc1.setCellValue("Sep");
        Cell celld1 = row2.createCell((short) 7);
        row2.getCell(7).setCellStyle(styleq2);
        celld1.setCellValue("Oct");

        Cell celle1 = row2.createCell((short) 8);
        row2.getCell(8).setCellStyle(styleq2);
        celle1.setCellValue("Nov");
        Cell cellf1 = row2.createCell((short) 9);
        row2.getCell(9).setCellStyle(styleq2);
        cellf1.setCellValue("Dec");
        Cell cellg1 = row2.createCell((short) 10);
        row2.getCell(10).setCellStyle(styleq3);
        cellg1.setCellValue("Jan");
        Cell cellh1 = row2.createCell((short) 11);
        row2.getCell(11).setCellStyle(styleq3);
        cellh1.setCellValue("Feb");
        Cell celli1 = row2.createCell((short) 12);
        row2.getCell(12).setCellStyle(styleq3);
        celli1.setCellValue("Mar");
        Cell cellj1 = row2.createCell((short) 13);
        row2.getCell(13).setCellStyle(styleq4);
        cellj1.setCellValue("Apr");
        Cell cellk1 = row2.createCell((short) 14);
        row2.getCell(14).setCellStyle(styleq4);
        cellk1.setCellValue("May");
        Cell celll1 = row2.createCell((short) 15);
        row2.getCell(15).setCellStyle(styleq4);
        celll1.setCellValue("Jun");
                sheet.addMergedRegion(new CellRangeAddress( 1,3,0,0 ));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 15));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 16, 16));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 17, 17));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 18, 18));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 19, 19));
        sheet.addMergedRegion(new CellRangeAddress(1, 3, 20, 20));
        
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 6));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 9));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 10, 12));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 13, 15));
        short rownum = 3;
        int rowstart = 4;
        int rowend = 0;
        int rowstart2 = 0;
                if (checkIfTotalByExpenditure_Fy_UnitString("2", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {
        rownum++;
        Row rowx = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 20));
        Cell cellx = rowx.createCell((short) 0);
        rowx.getCell(0).setCellStyle(stylegreen);
        cellx.setCellValue("REVENUE EXPENDITURE");
        
        programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
        List<RowsWorkplan> rowcount = new ArrayList();
        
        for (Programme prog : programmes) {
        
        programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
        if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {
        for (ProgramActivity d : programmesActivities) {
        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear()) == true) {
        
        rowend++;
        short tc = 0;
        BigDecimal er = null;
        rownum++;
        Row rowx1 = sheet.createRow(rownum);
        rowx1.createCell((short) tc).setCellValue(prog.getProgramme());
        
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getActivities());
        
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(budgetSubItemService.sumTotalByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear())));
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getFund());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        BigDecimal jul = budgetSubItemService.sumJulByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(jul));
        if (jul.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal aug = budgetSubItemService.sumAugByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(aug));
        if (aug.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal sep = budgetSubItemService.sumSepByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(sep));
        if (sep.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal oct = budgetSubItemService.sumOctByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(oct));
        if (oct.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal nov = budgetSubItemService.sumNovByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(nov));
        if (nov.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal dec = budgetSubItemService.sumDecByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(dec));
        if (dec.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal jan = budgetSubItemService.sumJanByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(jan));
        if (jan.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal feb = budgetSubItemService.sumFebByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(feb));
        if (feb.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal mar = budgetSubItemService.sumMarByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(mar));
        if (mar.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal apr = budgetSubItemService.sumAprByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(apr));
        if (apr.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal may = budgetSubItemService.sumMayByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(may));
        if (may.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal jun = budgetSubItemService.sumJunByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "2", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(jun));
        if (jun.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getOutput());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getPerformanceIndicator());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getOutcome());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getObjective());
        rowx1.getCell(tc).setCellStyle(stylec);
        
        }
        
        }
        if (rowstart == 4) {
        rowstart = rowstart + 1;
        rowend = rowstart + rowend;
        rowcount.add(new RowsWorkplan(rowstart, rowend - 1));
        rowstart = rowend - 1;
        } else {
        rowcount.add(new RowsWorkplan(rowstart + 1, rowend - 1));
        rowstart = rowend - 1;
        }
        }
        }
        
        for (RowsWorkplan w : rowcount) {
        if ((w.getRowEnd() - w.getRowStart()) > 0) {
        sheet.addMergedRegion(new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0));
        }
        
        }
        
        }
        rowstart++;
        if (checkIfTotalByExpenditure_Fy_UnitString("3", comboBox.getValue().getFinancialYear(), listOfUnitsIdSelected()) == true) {
        rownum++;
        Row rowx = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 20));
        Cell cellx = rowx.createCell((short) 0);
        rowx.getCell(0).setCellStyle(stylegreen);
        cellx.setCellValue("CAPITAL EXPENDITURE");
        
        programmes = programmeService.getProgrammeByFinancialYear(comboBox.getValue().getFinancialYear());
        List<RowsWorkplan> rowcount = new ArrayList();
        
        for (Programme prog : programmes) {
        
        programmesActivities = programmeActivityService.getProgramActivityByProgramme(prog.getId());
        if (doesprogrammeHaveBudget(listOfActivitiesInAProgramme(prog.getId()), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {
        for (ProgramActivity d : programmesActivities) {
        if (doesactivityHaveBudget(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear()) == true) {
        
        rowend++;
        short tc = 0;
        BigDecimal er = null;
        rownum++;
        Row rowx1 = sheet.createRow(rownum);
        rowx1.createCell((short) tc).setCellValue(prog.getProgramme());
        
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getActivities());
        
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(budgetSubItemService.sumTotalByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear())));
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getFund());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        BigDecimal jul = budgetSubItemService.sumJulByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(jul));
        if (jul.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal aug = budgetSubItemService.sumAugByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(aug));
        if (aug.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal sep = budgetSubItemService.sumSepByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(sep));
        if (sep.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal oct = budgetSubItemService.sumOctByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(oct));
        if (oct.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal nov = budgetSubItemService.sumNovByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(nov));
        if (nov.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal dec = budgetSubItemService.sumDecByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(dec));
        if (dec.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal jan = budgetSubItemService.sumJanByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(jan));
        if (jan.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal feb = budgetSubItemService.sumFebByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(feb));
        if (feb.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal mar = budgetSubItemService.sumMarByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(mar));
        if (mar.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal apr = budgetSubItemService.sumAprByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(apr));
        if (apr.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal may = budgetSubItemService.sumMayByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(may));
        if (may.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        BigDecimal jun = budgetSubItemService.sumJunByActivityByUnitByCategoryByFy(d.getId(), listOfUnitsIdSelected(), "3", comboBox.getValue().getFinancialYear());
        rowx1.createCell((short) tc).setCellValue(decimalFormat.format(jun));
        if (jun.doubleValue() > 0) {
        rowx1.getCell(tc).setCellStyle(styley);
        
        }
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getOutput());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getPerformanceIndicator());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getOutcome());
        rowx1.getCell(tc).setCellStyle(stylec);
        tc++;
        rowx1.createCell((short) tc).setCellValue(d.getObjective());
        rowx1.getCell(tc).setCellStyle(stylec);
        
        }
        
        }
        if (rowstart == 4) {
        rowstart = rowstart + 1;
        rowend = rowstart + rowend;
        rowcount.add(new RowsWorkplan(rowstart, rowend - 1));
        rowstart = rowend - 1;
        } else {
        rowcount.add(new RowsWorkplan(rowstart + 1, rowend - 1));
        rowstart = rowend - 1;
        }
        }
        }
        System.out.println(rowcount.toString());
        
        for (RowsWorkplan w : rowcount) {
        if ((w.getRowEnd() - w.getRowStart()) > 0) {
        sheet.addMergedRegion(new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0));
        }
        
        }
        
        }

    }

    private void createDataRows(Sheet sheet, List<SamplePerson> people) {
        int rowIndex = 1;
        for (SamplePerson person : people) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(person.getFirstName());
            row.createCell(1).setCellValue(person.getLastName());
            row.createCell(2).setCellValue(person.getEmail());
            row.createCell(3).setCellValue(person.getPhone());
            row.createCell(4).setCellValue(person.getDateOfBirth().toString());
            row.createCell(5).setCellValue(person.getOccupation());
        }
    }

    public Notification warningNotification(String warning) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                new Text(warning),
                new HtmlComponent("br"),
                new Text("Close this warning to continue working.")
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
        return notification;
    }

    public BigDecimal getTotalByExpenditure_Fy_Unit(String category, String fiscalYear, List<Integer> deptUnits) {
        BigDecimal sum = budgetSubItemService.getTotalByCategoryAndFiscalYearAndDeptUnits(category, fiscalYear, deptUnits);
        return sum;
    }

    public boolean checkIfTotalByExpenditure_Fy_UnitString(String category, String fiscalYear, List<Integer> deptUnits) {
        BigDecimal sums = getTotalByExpenditure_Fy_Unit(category, fiscalYear, deptUnits);
        return sums != null;
        //return getTotalByExpenditure_Fy_Unit(category, fiscalYear, deptUnits).doubleValue() > 0;
    }

    public List<Integer> listOfUnitsIdSelected() {
        List<Integer> inte = new ArrayList<>();

        for (DepartmentUnit u : select.asMultiSelect().getSelectedItems()) {
            inte.add(u.getId());
        }
        return inte;
    }

    public String listOfUnitsNameSelected() {
        List<String> inte = new ArrayList<>();
        StringBuilder buildd = new StringBuilder();

        for (DepartmentUnit u : select.asMultiSelect().getSelectedItems()) {
            buildd.append(u.getUnit() + ", ");
        }
        return buildd.toString().substring(0, buildd.toString().length() - 2);
    }

    //List of Activities in a programme
    public List<Integer> listOfActivitiesInAProgramme(Integer programmeId) {
        programmesActivities = programmeActivityService.getProgramActivityByProgramme(programmeId);
        List<Integer> inte = new ArrayList<>();
        for (ProgramActivity act : programmesActivities) {
            inte.add(act.getId());
        }
        return inte;
    }

    //Check if something has ever been budgeted under that programme
    //Under those units and those activities
    public boolean doesprogrammeHaveBudget(List<Integer> activities, List<Integer> units, String expenseCategory, String fy) {
        BigDecimal prog = budgetSubItemService.sumTotalByActivitiesByUnitByCategoryByFy(activities, units, expenseCategory, fy);
        if (prog != null) {
            if (prog.doubleValue() > 0) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    public boolean doesactivityHaveBudget(Integer activities, List<Integer> units, String expenseCategory, String fy) {
        BigDecimal prog = budgetSubItemService.sumTotalByActivityByUnitByCategoryByFy(activities, units, expenseCategory, fy);
        if (prog != null) {
            if (prog.doubleValue() > 0) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    public boolean isSumOfMonthsGreaterThanZero(BudgetSubItem budgetSubItem) {
        BigDecimal jul = budgetSubItem.getJul() != null ? budgetSubItem.getJul() : BigDecimal.ZERO;
        BigDecimal aug = budgetSubItem.getAug() != null ? budgetSubItem.getAug() : BigDecimal.ZERO;
        BigDecimal sep = budgetSubItem.getSep() != null ? budgetSubItem.getSep() : BigDecimal.ZERO;
        BigDecimal oct = budgetSubItem.getOct() != null ? budgetSubItem.getOct() : BigDecimal.ZERO;
        BigDecimal nov = budgetSubItem.getNov() != null ? budgetSubItem.getNov() : BigDecimal.ZERO;
        BigDecimal dec = budgetSubItem.getDec() != null ? budgetSubItem.getDec() : BigDecimal.ZERO;
        BigDecimal jan = budgetSubItem.getJan() != null ? budgetSubItem.getJan() : BigDecimal.ZERO;
        BigDecimal feb = budgetSubItem.getFeb() != null ? budgetSubItem.getFeb() : BigDecimal.ZERO;
        BigDecimal mar = budgetSubItem.getMar() != null ? budgetSubItem.getMar() : BigDecimal.ZERO;
        BigDecimal apr = budgetSubItem.getApr() != null ? budgetSubItem.getApr() : BigDecimal.ZERO;
        BigDecimal may = budgetSubItem.getMay() != null ? budgetSubItem.getMay() : BigDecimal.ZERO;
        BigDecimal jun = budgetSubItem.getJun() != null ? budgetSubItem.getJun() : BigDecimal.ZERO;

        BigDecimal sumOfMonths = jul.add(aug).add(sep).add(oct)
                .add(nov).add(dec).add(jan).add(feb)
                .add(mar).add(apr).add(may).add(jun);

        return sumOfMonths.compareTo(BigDecimal.ZERO) > 0;
    }

    public COA setCOA(String oldcode, Budget budget) {
        COA coa = null;

        List<COAReconcile> code = coaReconcileService.findCOAReconcileByOldcoa(oldcode);
        for (COAReconcile b : code) {
            //System.out.println(b.getNewcoa() + ": Item new " + b.getOldcoa() + ": Old Item");
            coa = coaService.findByCodeAndBudget(b.getNewcoa(), budget);
            // System.out.println(coa.getCode() + " Item new");
        }

        return coa;
    }
// Method to check if the new region overlaps with existing regions

private static boolean isOverlappingWithExistingRegions(Sheet sheet, CellRangeAddress newRegion) {
    for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
        CellRangeAddress existingRegion = sheet.getMergedRegion(i);

        // Check if the new region is completely within an existing region
        if (existingRegion.isInRange(newRegion.getFirstRow(), newRegion.getFirstColumn())
                && existingRegion.isInRange(newRegion.getLastRow(), newRegion.getLastColumn())) {
            return true; // Overlapping
        }

        // Check if the new region partially overlaps with an existing region
        if (newRegion.isInRange(existingRegion.getFirstRow(), existingRegion.getFirstColumn())
                || newRegion.isInRange(existingRegion.getLastRow(), existingRegion.getLastColumn())) {
            return true; // Partially overlapping
        }
    }

    return false; // Not overlapping
}

// Method to add a merged region based on the sheet type
    private static void addMergedRegion(Sheet sheet, CellRangeAddress region) {
        if (sheet instanceof XSSFSheet) {
            ((XSSFSheet) sheet).addMergedRegion(region);
        } else if (sheet instanceof HSSFSheet) {
            ((HSSFSheet) sheet).addMergedRegion(region);
        }
    }
}
