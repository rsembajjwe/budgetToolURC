package com.methaltech.application.views.workplan;

import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.FreightVolumesService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.StaffSalaryService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.entity.oldbgtool.RowsWorkplan;
import com.methaltech.application.data.oldbgtool.service.OldStaffPojoService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.views.budgetReport.BudgetReportsView;
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
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
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
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Work Plan Extractor")
@Route(value = "workplans", layout = MainLayout.class)
//@RolesAllowed("USER")
@RolesAllowed({"ADMIN", "BLO", "USER", "HOD"})
@Uses(Icon.class)
public class budgetWorkplanView extends Div {

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
    private final URC_Priority_AreasService sampleURC_Priority_Areas;
    private final StaffSalaryService sampleStaffSalaryService;

    private final Binder<StaffSalary> binder = new BeanValidationBinder<>(StaffSalary.class);
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section = new MultiSelectComboBox<>("Cost Centres");
    private ComboBox<Currency> currencyComboBox = new ComboBox("Currency");
    private MultiSelectComboBox<Organisation> comboBoxOrganisation = new MultiSelectComboBox<>("Budget Type");
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US); // Use US locale for consistent formatting

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
    private AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private User user;
    private final OldStaffPojoService oldStaffPojoService;
    private final Coalevel1Service coalevel1Service;
    Button downloadWorkplan = new Button(new Icon(VaadinIcon.DOWNLOAD));
    Button downloadWorkplan2 = new Button("Download Qtr");
    private List<URC_Priority_Areas> programmes = new ArrayList<>();
    private List<Urc_Activities> programmesActivities = new ArrayList<>();

    public budgetWorkplanView(AuthenticatedUser authenticatedUser, FreightVolumesService sampleFreightVolumesService, BudgetService sampleBudgetService, CoaService sampleCoaService,
            CurrencyService sampleCurrencyService, BudgetItemsService budgetItemsService, StockUnitMeasureService sampleStockUnitMeasureService,
            OrganisationService sampleOrganisationService, UserService userService, BudgetItemsService sampleBudgetItemsService,
            Urc_ActivitiesService sampleUrc_ActivitiesService, StaffSalaryService sampleStaffSalaryService, OldStaffPojoService oldStaffPojoService,
            Coalevel1Service coalevel1Service, URC_Priority_AreasService sampleURC_Priority_Areas) {
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
        this.sampleURC_Priority_Areas = sampleURC_Priority_Areas;
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

        TabSheet tabSheet = new TabSheet();
        tabSheet.add("General Workplan Extractor", workplanPanel());
        tabSheet.add("Custom Workplan Extractor", customworkplanPanel());
        tabSheet.setHeight("100%");
        tabSheet.setWidth("100%");
        add(tabSheet);
    }

    private VerticalLayout workplanPanel() {
        VerticalLayout mainLay = new VerticalLayout();
        mainLay.setSizeFull();
        HorizontalLayout hmainLay = new HorizontalLayout();
        hmainLay.setSizeFull();
        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        comboBoxD_Section.setItems(user.getDeptsection());
        comboBoxD_Section.setWidthFull();

        comboBoxOrganisation.setItemLabelGenerator(Organisation::getName);
        comboBoxBudget.setItems(query -> sampleBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        comboBoxBudget.setItemLabelGenerator(Budget::getFinancialYear);
        comboBoxBudget.addValueChangeListener(e -> {
            comboBoxOrganisation.setItems(sampleOrganisationService.findByBudgetList(comboBoxBudget.getValue()));
            if (!comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                downloadWorkplan.setEnabled(true);
                downloadWorkplan2.setEnabled(true);
            } else {
                downloadWorkplan.setEnabled(false);
                downloadWorkplan2.setEnabled(true);
            }
        });
        comboBoxD_Section.addValueChangeListener(e -> {
            if (!comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                downloadWorkplan.setEnabled(true);
                downloadWorkplan2.setEnabled(true);
            } else {
                downloadWorkplan.setEnabled(false);
                downloadWorkplan2.setEnabled(true);
            }
        });
        comboBoxOrganisation.addValueChangeListener(e -> {
            if (!comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
                downloadWorkplan.setEnabled(true);
                downloadWorkplan2.setEnabled(true);
            } else {
                downloadWorkplan.setEnabled(false);
                downloadWorkplan2.setEnabled(true);
            }
        });
        downloadWorkplan.addThemeVariants(ButtonVariant.LUMO_ICON);
        downloadWorkplan.setAriaLabel("Download");
        downloadWorkplan.setEnabled(false);

        downloadWorkplan2.addThemeVariants(ButtonVariant.LUMO_ICON);
        downloadWorkplan2.setAriaLabel("Download");
        downloadWorkplan2.setEnabled(false);

        downloadWorkplan.addClickListener(e -> {
            if (comboBoxD_Section.isEmpty() || comboBoxOrganisation.isEmpty() || comboBoxBudget.isEmpty()) {
                warningNotification("Ensure that You have filled the form well");
            } else {
                exportAndDownloadExcelWorkplan();
            }

        });

        downloadWorkplan2.addClickListener(e -> {
            if (comboBoxD_Section.isEmpty() || comboBoxOrganisation.isEmpty() || comboBoxBudget.isEmpty()) {
                warningNotification("Ensure that You have filled the form well");
            } else {
                exportAndDownloadExcelWorkplanQtr();
            }

        });
        hmainLay.add(comboBoxBudget, comboBoxOrganisation, comboBoxD_Section, downloadWorkplan, downloadWorkplan2);
        //hmainLay.setAlignSelf(FlexComponent.Alignment.BASELINE, hmainLay);
        hmainLay.setAlignItems(FlexComponent.Alignment.BASELINE);
        //hmainLay.setAlignItems(Alignment.BASELINE);

        mainLay.add(hmainLay);
        return mainLay;
    }

    private VerticalLayout customworkplanPanel() {
        VerticalLayout main2Lay = new VerticalLayout();
        return main2Lay;
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

    private void exportAndDownloadExcelWorkplan() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Work plan");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderRowWorkplan(workbook, sheet);
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

    private void exportAndDownloadExcelWorkplanQtr() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Work plan");
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderRowWorkplanQtr(workbook, sheet);
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

    private void createHeaderRowWorkplan(Workbook workbook, Sheet sheet) {
        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq.setWrapText(true);
        styleq.setFont(fontBold);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);
        styleq31.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);
        styley.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.CENTER);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);
        stylec.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowBoldcount = new ArrayList();
        Row headerRow = sheet.createRow(tr);

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 20);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("WORKPLAN");
        rowBoldcount.add((int) 0);
        tr++;

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("Programme");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Activities");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Budget");
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Funding");
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Time Line Quarterly");

        Cell cell6 = row.createCell((short) 16);
        row.getCell(16).setCellStyle(styleq31);
        cell6.setCellValue("Output");
        Cell cell7 = row.createCell((short) 17);
        row.getCell(17).setCellStyle(styleq31);
        cell7.setCellValue("Performance Indicator");
        Cell cell8 = row.createCell((short) 18);
        row.getCell(18).setCellStyle(styleq31);
        cell8.setCellValue("Outcome");
        Cell cell9 = row.createCell((short) 19);
        row.getCell(19).setCellStyle(styleq31);
        cell9.setCellValue("Strategic Objective");
        Cell cell10 = row.createCell((short) 20);
        row.getCell(20).setCellStyle(styleq31);
        cell10.setCellValue("Remarks");
        tr++;
        Row row1 = sheet.createRow(tr);
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
        tr++;
        Row row2 = sheet.createRow(tr);
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
        rowBoldcount.add((int) 1);
        //tr++;

        short rownum = tr;
        int rowstart = tr + 1;
        System.out.println(rownum + "......" + rowstart);
        int rowend = 0;
        int rowstart2 = 0;
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 20));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 15));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 16, 16));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 17, 17));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 18, 18));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 19, 19));
        sheet.addMergedRegion(new CellRangeAddress(2, 4, 20, 20));

        sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 6));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 7, 9));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 10, 12));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 13, 15));
        BigDecimal totalexpense = BigDecimal.ZERO;
        BigDecimal totaljul = BigDecimal.ZERO;
        BigDecimal totalaug = BigDecimal.ZERO;
        BigDecimal totalsep = BigDecimal.ZERO;
        BigDecimal totaloct = BigDecimal.ZERO;
        BigDecimal totalnov = BigDecimal.ZERO;
        BigDecimal totaldec = BigDecimal.ZERO;
        BigDecimal totaljan = BigDecimal.ZERO;
        BigDecimal totalfeb = BigDecimal.ZERO;
        BigDecimal totalmar = BigDecimal.ZERO;
        BigDecimal totalapr = BigDecimal.ZERO;
        BigDecimal totalmay = BigDecimal.ZERO;
        BigDecimal totaljun = BigDecimal.ZERO;

        // Creating merged regions
        Coalevel1 coal = coalevel1Service.findByCode(2);
        List<UrcDeptSectionAnlDimbgt> selectedSections = comboBoxD_Section.getSelectedItems().stream().toList();
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBoxBudget.getValue(), coal, selectedSections) == true) {
            rownum++;
            Row rowx = sheet.createRow(rownum);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 20));
            Cell cellx = rowx.createCell((short) 0);
            rowx.getCell(0).setCellStyle(stylegreen);
            cellx.setCellValue("REVENUE EXPENDITURE");
            rowBoldcount.add((int) rownum);

            programmes = sampleURC_Priority_Areas.findByBudget(comboBoxBudget.getValue());
            List<RowsWorkplan> rowcount = new ArrayList();

            for (URC_Priority_Areas prog : programmes) {

                programmesActivities = sampleUrc_ActivitiesService.findActivitiesByBudgetAndPriorityAndDeptUnits(comboBoxBudget.getValue(), prog, selectedSections);
                if (programmesActivities != null) {
                    if (budgetItemsService.isSumProgrammeGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), programmesActivities, comboBoxD_Section.getSelectedItems(), 2) == true) {
                        for (Urc_Activities d : programmesActivities) {
                            if (budgetItemsService.isSumActvityGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 2) == true) {

                                rowend++;
                                short tc = 0;
                                BigDecimal er = null;
                                rownum++;
                                Row rowx1 = sheet.createRow(rownum);
                                rowx1.createCell((short) tc).setCellValue(prog.getName());

                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx1.createCell((short) tc).setCellValue(d.getName());

                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal adds = budgetItemsService.sumActvitySummation(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 2);
                                totalexpense = totalexpense.add(adds);
                                rowx1.createCell((short) tc).setCellValue(adds.doubleValue());
                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx1.createCell((short) tc).setCellValue(d.getFundsource());
                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal jul = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jul");
                                totaljul = totaljul.add(jul);
                                rowx1.createCell((short) tc).setCellValue(jul.doubleValue());
                                if (jul.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal aug = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "aug");
                                totalaug = totalaug.add(aug);
                                rowx1.createCell((short) tc).setCellValue(aug.doubleValue());
                                if (aug.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal sep = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "sep");
                                totalsep = totalsep.add(sep);
                                rowx1.createCell((short) tc).setCellValue(sep.doubleValue());
                                if (sep.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal oct = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "oct");
                                totaloct = totaloct.add(oct);
                                rowx1.createCell((short) tc).setCellValue(oct.doubleValue());
                                if (oct.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal nov = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "nov");
                                totalnov = totalnov.add(nov);
                                rowx1.createCell((short) tc).setCellValue(nov.doubleValue());
                                if (nov.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal dec = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "dec");
                                totaldec = totaldec.add(dec);
                                rowx1.createCell((short) tc).setCellValue(dec.doubleValue());
                                if (dec.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal jan = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jan");
                                totaljan = totaljan.add(jan);
                                rowx1.createCell((short) tc).setCellValue(jan.doubleValue());
                                if (jan.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal feb = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "feb");
                                totalfeb = totalfeb.add(feb);
                                rowx1.createCell((short) tc).setCellValue(feb.doubleValue());
                                if (feb.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal mar = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "mar");
                                totalmar = totalmar.add(mar);
                                rowx1.createCell((short) tc).setCellValue(mar.doubleValue());
                                if (mar.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal apr = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "apr");
                                totalapr = totalapr.add(apr);
                                rowx1.createCell((short) tc).setCellValue(apr.doubleValue());
                                if (apr.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal may = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "may");
                                totalmay = totalmay.add(may);
                                rowx1.createCell((short) tc).setCellValue(may.doubleValue());
                                if (may.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal jun = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jun");
                                totaljun = totaljun.add(jun);
                                rowx1.createCell((short) tc).setCellValue(jun.doubleValue());
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
                                tc++;
                                rowx1.createCell((short) tc).setCellValue("");
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

            }

            for (RowsWorkplan w : rowcount) {

                if ((w.getRowEnd() - w.getRowStart()) > 0) {
                    //sheet.addMergedRegion(new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0));
                    CellRangeAddress newMergedRegion = new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0);
                    if (!isOverlappingWithExistingRegions(sheet, newMergedRegion)) {
                        addMergedRegion(sheet, newMergedRegion);
                    } else {
                        // Handle overlapping regions (e.g., adjust the region or skip it)
                    }
                }

            }

        }
        rownum++;
        Row rowx1 = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
        Cell cellx1 = rowx1.createCell((short) 0);
        rowx1.getCell(0).setCellStyle(styleq31);
        cellx1.setCellValue("TOTAL");
        Cell cellx2 = rowx1.createCell((short) 2);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 3));
        rowx1.getCell(2).setCellStyle(styleq31);
        cellx2.setCellValue(totalexpense.doubleValue());
        Cell cellx4 = rowx1.createCell((short) 4);
        rowx1.getCell(4).setCellStyle(styleq31);
        cellx4.setCellValue(totaljul.doubleValue());
        Cell cellx5 = rowx1.createCell((short) 5);
        rowx1.getCell(5).setCellStyle(styleq31);
        cellx5.setCellValue(totalaug.doubleValue());
        Cell cellx6 = rowx1.createCell((short) 6);
        rowx1.getCell(6).setCellStyle(styleq31);
        cellx6.setCellValue(totalsep.doubleValue());
        Cell cellx7 = rowx1.createCell((short) 7);
        rowx1.getCell(7).setCellStyle(styleq31);
        cellx7.setCellValue(totaloct.doubleValue());
        Cell cellx8 = rowx1.createCell((short) 8);
        rowx1.getCell(8).setCellStyle(styleq31);
        cellx8.setCellValue(totalnov.doubleValue());
        Cell cellx9 = rowx1.createCell((short) 9);
        rowx1.getCell(9).setCellStyle(styleq31);
        cellx9.setCellValue(totaldec.doubleValue());
        Cell cellx10 = rowx1.createCell((short) 10);
        rowx1.getCell(10).setCellStyle(styleq31);
        cellx10.setCellValue(totaljan.doubleValue());
        Cell cellx11 = rowx1.createCell((short) 11);
        rowx1.getCell(11).setCellStyle(styleq31);
        cellx11.setCellValue(totalfeb.doubleValue());
        Cell cellx12 = rowx1.createCell((short) 12);
        rowx1.getCell(12).setCellStyle(styleq31);
        cellx12.setCellValue(totalmar.doubleValue());
        Cell cellx13 = rowx1.createCell((short) 13);
        rowx1.getCell(13).setCellStyle(styleq31);
        cellx13.setCellValue(totalapr.doubleValue());
        Cell cellx14 = rowx1.createCell((short) 14);
        rowx1.getCell(14).setCellStyle(styleq31);
        cellx14.setCellValue(totalmay.doubleValue());
        Cell cellx15 = rowx1.createCell((short) 15);
        rowx1.getCell(15).setCellStyle(styleq31);
        cellx15.setCellValue(totaljun.doubleValue());

        rowstart++;
        totalexpense = BigDecimal.ZERO;
        totaljul = BigDecimal.ZERO;
        totalaug = BigDecimal.ZERO;
        totalsep = BigDecimal.ZERO;
        totaloct = BigDecimal.ZERO;
        totalnov = BigDecimal.ZERO;
        totaldec = BigDecimal.ZERO;
        totaljan = BigDecimal.ZERO;
        totalfeb = BigDecimal.ZERO;
        totalmar = BigDecimal.ZERO;
        totalapr = BigDecimal.ZERO;
        totalmay = BigDecimal.ZERO;
        totaljun = BigDecimal.ZERO;
        coal = coalevel1Service.findByCode(3);
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBoxBudget.getValue(), coal, selectedSections) == true) {
            rownum++;
            Row rowx = sheet.createRow(rownum);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 20));
            Cell cellx = rowx.createCell((short) 0);
            rowx.getCell(0).setCellStyle(stylegreen);
            cellx.setCellValue("CAPITAL EXPENDITURE");
            rowBoldcount.add((int) rownum);

            programmes = sampleURC_Priority_Areas.findByBudget(comboBoxBudget.getValue());
            List<RowsWorkplan> rowcount = new ArrayList();

            for (URC_Priority_Areas prog : programmes) {

                programmesActivities = sampleUrc_ActivitiesService.findActivitiesByBudgetAndPriorityAndDeptUnits(comboBoxBudget.getValue(), prog, selectedSections);
                if (programmesActivities != null) {
                    if (budgetItemsService.isSumProgrammeGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), programmesActivities, comboBoxD_Section.getSelectedItems(), 3) == true) {
                        for (Urc_Activities d : programmesActivities) {
                            if (budgetItemsService.isSumActvityGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 3) == true) {

                                rowend++;
                                short tc = 0;
                                BigDecimal er = null;
                                rownum++;
                                Row rowx12 = sheet.createRow(rownum);
                                rowx12.createCell((short) tc).setCellValue(prog.getName());

                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx12.createCell((short) tc).setCellValue(d.getName());

                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal adds = budgetItemsService.sumActvitySummation(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 3);
                                totalexpense = totalexpense.add(adds);
                                rowx12.createCell((short) tc).setCellValue(adds.doubleValue());
                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx12.createCell((short) tc).setCellValue(d.getFundsource());
                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal jul = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jul");
                                totaljul = totaljul.add(jul);
                                rowx12.createCell((short) tc).setCellValue(jul.doubleValue());
                                if (jul.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal aug = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "aug");
                                totalaug = totalaug.add(aug);
                                rowx12.createCell((short) tc).setCellValue(aug.doubleValue());
                                if (aug.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal sep = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "sep");
                                totalsep = totalsep.add(sep);
                                rowx12.createCell((short) tc).setCellValue(sep.doubleValue());
                                if (sep.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal oct = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "oct");
                                totaloct = totaloct.add(oct);
                                rowx12.createCell((short) tc).setCellValue(oct.doubleValue());
                                if (oct.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal nov = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "nov");
                                totalnov = totalnov.add(nov);
                                rowx12.createCell((short) tc).setCellValue(nov.doubleValue());
                                if (nov.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal dec = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "dec");
                                totaldec = totaldec.add(dec);
                                rowx12.createCell((short) tc).setCellValue(dec.doubleValue());
                                if (dec.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal jan = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jan");
                                totaljan = totaljan.add(jan);
                                rowx12.createCell((short) tc).setCellValue(jan.doubleValue());
                                if (jan.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal feb = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "feb");
                                totalfeb = totalfeb.add(feb);
                                rowx12.createCell((short) tc).setCellValue(feb.doubleValue());
                                if (feb.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal mar = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "mar");
                                totalmar = totalmar.add(mar);
                                rowx12.createCell((short) tc).setCellValue(mar.doubleValue());
                                if (mar.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal apr = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "apr");
                                totalapr = totalapr.add(apr);
                                rowx12.createCell((short) tc).setCellValue(apr.doubleValue());
                                if (apr.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal may = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "may");
                                totalmay = totalmay.add(may);
                                rowx12.createCell((short) tc).setCellValue(may.doubleValue());
                                if (may.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                BigDecimal jun = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jun");
                                totaljun = totaljun.add(jun);
                                rowx12.createCell((short) tc).setCellValue(jun.doubleValue());
                                if (jun.doubleValue() > 0) {
                                    rowx12.getCell(tc).setCellStyle(styley);

                                }
                                tc++;
                                rowx12.createCell((short) tc).setCellValue(d.getOutput());
                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx12.createCell((short) tc).setCellValue(d.getPerformanceIndicator());
                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx12.createCell((short) tc).setCellValue(d.getOutcome());
                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx12.createCell((short) tc).setCellValue(d.getObjective());
                                rowx12.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx12.createCell((short) tc).setCellValue("");
                                rowx12.getCell(tc).setCellStyle(stylec);
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

            }

            for (RowsWorkplan w : rowcount) {
                if ((w.getRowEnd() - w.getRowStart()) > 0) {
                    //sheet.addMergedRegion(new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0));
                    CellRangeAddress newMergedRegion = new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0);
                    if (!isOverlappingWithExistingRegions(sheet, newMergedRegion)) {
                        addMergedRegion(sheet, newMergedRegion);
                    } else {
                        // Handle overlapping regions (e.g., adjust the region or skip it)
                    }
                }

            }

// Iterate through each row and cell and apply the border
        }
        rownum++;

        Row rowx3 = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
        Cell cellx3 = rowx3.createCell((short) 0);
        rowx3.getCell(0).setCellStyle(styleq31);
        cellx3.setCellValue("TOTAL");
        Cell cellx3Cell = rowx3.createCell((short) 2);
        rowx3.getCell(2).setCellStyle(styleq31);
        cellx3Cell.setCellValue(totalexpense.doubleValue());

        Cell cellx41 = rowx3.createCell((short) 4);
        rowx3.getCell(4).setCellStyle(styleq31);
        cellx41.setCellValue(totaljul.doubleValue());
        Cell cellx51 = rowx3.createCell((short) 5);
        rowx3.getCell(5).setCellStyle(styleq31);
        cellx51.setCellValue(totalaug.doubleValue());
        Cell cellx61 = rowx3.createCell((short) 6);
        rowx3.getCell(6).setCellStyle(styleq31);
        cellx61.setCellValue(totalsep.doubleValue());
        Cell cellx71 = rowx3.createCell((short) 7);
        rowx3.getCell(7).setCellStyle(styleq31);
        cellx71.setCellValue(totaloct.doubleValue());
        Cell cellx81 = rowx3.createCell((short) 8);
        rowx3.getCell(8).setCellStyle(styleq31);
        cellx81.setCellValue(totalnov.doubleValue());
        Cell cellx91 = rowx3.createCell((short) 9);
        rowx3.getCell(9).setCellStyle(styleq31);
        cellx91.setCellValue(totaldec.doubleValue());
        Cell cellx101 = rowx3.createCell((short) 10);
        rowx3.getCell(10).setCellStyle(styleq31);
        cellx101.setCellValue(totaljan.doubleValue());
        Cell cellx111 = rowx3.createCell((short) 11);
        rowx3.getCell(11).setCellStyle(styleq31);
        cellx111.setCellValue(totalfeb.doubleValue());
        Cell cellx121 = rowx3.createCell((short) 12);
        rowx3.getCell(12).setCellStyle(styleq31);
        cellx121.setCellValue(totalmar.doubleValue());
        Cell cellx131 = rowx3.createCell((short) 13);
        rowx3.getCell(13).setCellStyle(styleq31);
        cellx131.setCellValue(totalapr.doubleValue());
        Cell cellx141 = rowx3.createCell((short) 14);
        rowx3.getCell(14).setCellStyle(styleq31);
        cellx141.setCellValue(totalmay.doubleValue());
        Cell cellx151 = rowx3.createCell((short) 15);
        rowx3.getCell(15).setCellStyle(styleq31);
        cellx151.setCellValue(totaljun.doubleValue());
        // Iterate through each merged region and apply the border
        for (int i = 1; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            for (int rowz = mergedRegion.getFirstRow(); rowz <= mergedRegion.getLastRow(); rowz++) {
                Row currentRow = sheet.getRow(rowz);
                if (currentRow == null) {
                    currentRow = sheet.createRow(rowz);
                }

                for (int col = mergedRegion.getFirstColumn(); col <= mergedRegion.getLastColumn(); col++) {
                    Cell currentCell = currentRow.getCell(col);
                    if (currentCell == null) {
                        currentCell = currentRow.createCell(col);
                    }
                    CellStyle existingStyle = currentCell.getCellStyle();

                    // Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);
                    // Set the new style to the cell
                    currentCell.setCellStyle(newStyle);

                }
            }
        }
        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);

                    currentCell.setCellStyle(newStyle);
                }

            }
        }
        /*        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
        sheet.autoSizeColumn(i);
        }*/
    }

    private void createHeaderRowWorkplanQtr(Workbook workbook, Sheet sheet) {
        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq.setWrapText(true);
        styleq.setFont(fontBold);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);
        styleq31.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);
        styley.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.CENTER);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);
        stylec.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowBoldcount = new ArrayList();
        Row headerRow = sheet.createRow(tr);

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 12);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("WORKPLAN");
        rowBoldcount.add((int) 0);
        tr++;

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("Programme");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Activities");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Budget");
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Funding");
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Time Line Quarterly");

        Cell cell6 = row.createCell((short) 8);
        row.getCell(8).setCellStyle(styleq31);
        cell6.setCellValue("Output");
        Cell cell7 = row.createCell((short) 9);
        row.getCell(9).setCellStyle(styleq31);
        cell7.setCellValue("Performance Indicator");
        Cell cell8 = row.createCell((short) 10);
        row.getCell(10).setCellStyle(styleq31);
        cell8.setCellValue("Outcome");
        Cell cell9 = row.createCell((short) 11);
        row.getCell(11).setCellStyle(styleq31);
        cell9.setCellValue("Strategic Objective");
        Cell cell10 = row.createCell((short) 12);
        row.getCell(12).setCellStyle(styleq31);
        cell10.setCellValue("Remarks");

        tr++;
        Row row1 = sheet.createRow(tr);
        Cell cella = row1.createCell((short) 4);
        row1.getCell(4).setCellStyle(style);
        cella.setCellValue("Q1");
        Cell cellb = row1.createCell((short) 5);
        row1.getCell(5).setCellStyle(style);
        cellb.setCellValue("Q2");
        Cell cellc = row1.createCell((short) 6);
        row1.getCell(6).setCellStyle(style);
        cellc.setCellValue("Q3");
        Cell celld = row1.createCell((short) 7);
        row1.getCell(7).setCellStyle(style);
        celld.setCellValue("Q4");

        rowBoldcount.add((int) 1);
        //tr++;

        short rownum = tr;
        int rowstart = tr + 1;
        System.out.println(rownum + "......" + rowstart);
        int rowend = 0;
        int rowstart2 = 0;
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 7));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 8, 8));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 9, 9));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 10, 10));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 11, 11));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 12, 12));
        BigDecimal totalexpense = BigDecimal.ZERO;
        BigDecimal totalqtr1 = BigDecimal.ZERO;
        BigDecimal totalqtr2 = BigDecimal.ZERO;
        BigDecimal totalqtr3 = BigDecimal.ZERO;
        BigDecimal totalqtr4 = BigDecimal.ZERO;

        Coalevel1 coal = coalevel1Service.findByCode(2);
        List<UrcDeptSectionAnlDimbgt> selectedSections = comboBoxD_Section.getSelectedItems().stream().toList();
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBoxBudget.getValue(), coal, selectedSections) == true) {
            rownum++;
            Row rowx = sheet.createRow(rownum);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 12));
            Cell cellx = rowx.createCell((short) 0);
            rowx.getCell(0).setCellStyle(stylegreen);
            cellx.setCellValue("REVENUE EXPENDITURE");
            rowBoldcount.add((int) rownum);

            programmes = sampleURC_Priority_Areas.findByBudget(comboBoxBudget.getValue());
            List<RowsWorkplan> rowcount = new ArrayList();

            for (URC_Priority_Areas prog : programmes) {

                programmesActivities = sampleUrc_ActivitiesService.findActivitiesByBudgetAndPriorityAndDeptUnits(comboBoxBudget.getValue(), prog, selectedSections);
                if (programmesActivities != null) {
                    if (budgetItemsService.isSumProgrammeGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), programmesActivities, comboBoxD_Section.getSelectedItems(), 2) == true) {
                        for (Urc_Activities d : programmesActivities) {
                            if (budgetItemsService.isSumActvityGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 2) == true) {

                                rowend++;
                                short tc = 0;
                                BigDecimal er = null;
                                rownum++;
                                Row rowx1 = sheet.createRow(rownum);
                                rowx1.createCell((short) tc).setCellValue(prog.getName());

                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx1.createCell((short) tc).setCellValue(d.getName());

                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal tBigDecimal = budgetItemsService.sumActvitySummation(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 2);
                                totalexpense = totalexpense.add(tBigDecimal);
                                rowx1.createCell((short) tc).setCellValue(tBigDecimal.doubleValue());
                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx1.createCell((short) tc).setCellValue(d.getFundsource());
                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal jul = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jul");
                                BigDecimal aug = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "aug");
                                BigDecimal sep = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "sep");
                                BigDecimal qtr1 = jul.add(aug).add(sep);
                                totalqtr1 = totalqtr1.add(qtr1);
                                rowx1.createCell((short) tc).setCellValue(qtr1.doubleValue());
                                if (qtr1.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }

                                tc++;
                                BigDecimal oct = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "oct");
                                BigDecimal nov = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "nov");
                                BigDecimal dec = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "dec");
                                BigDecimal qtr2 = oct.add(nov).add(dec);
                                totalqtr2 = totalqtr2.add(qtr2);
                                rowx1.createCell((short) tc).setCellValue(qtr2.doubleValue());
                                if (qtr2.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }

                                tc++;
                                BigDecimal jan = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jan");
                                BigDecimal feb = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "feb");
                                BigDecimal mar = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "mar");
                                BigDecimal qtr3 = jan.add(feb).add(mar);
                                totalqtr3 = totalqtr3.add(qtr3);
                                rowx1.createCell((short) tc).setCellValue(qtr3.doubleValue());
                                if (qtr3.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }

                                tc++;
                                BigDecimal apr = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "apr");
                                BigDecimal may = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "may");
                                BigDecimal jun = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 2, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jun");
                                BigDecimal qtr4 = apr.add(may).add(jun);
                                totalqtr4 = totalqtr4.add(qtr4);
                                rowx1.createCell((short) tc).setCellValue(qtr4.doubleValue());
                                if (qtr4.doubleValue() > 0) {
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
                                tc++;
                                rowx1.createCell((short) tc).setCellValue("");
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

            }

            for (RowsWorkplan w : rowcount) {

                if ((w.getRowEnd() - w.getRowStart()) > 0) {
                    //sheet.addMergedRegion(new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0));
                    CellRangeAddress newMergedRegion = new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0);
                    if (!isOverlappingWithExistingRegions(sheet, newMergedRegion)) {
                        addMergedRegion(sheet, newMergedRegion);
                    } else {
                        // Handle overlapping regions (e.g., adjust the region or skip it)
                    }
                }

            }

        }

        rownum++;
        Row rowx12 = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
        Cell cellx1 = rowx12.createCell((short) 0);
        rowx12.getCell(0).setCellStyle(styleq31);
        cellx1.setCellValue("TOTAL");
        Cell cellx2 = rowx12.createCell((short) 2);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 3));
        rowx12.getCell(2).setCellStyle(styleq31);
        cellx2.setCellValue(totalexpense.doubleValue());
        Cell cellx4 = rowx12.createCell((short) 4);
        rowx12.getCell(4).setCellStyle(styleq31);
        cellx4.setCellValue(totalqtr1.doubleValue());
        Cell cellx5 = rowx12.createCell((short) 5);
        rowx12.getCell(5).setCellStyle(styleq31);
        cellx5.setCellValue(totalqtr2.doubleValue());
        Cell cellx6 = rowx12.createCell((short) 6);
        rowx12.getCell(6).setCellStyle(styleq31);
        cellx6.setCellValue(totalqtr3.doubleValue());
        Cell cellx7 = rowx12.createCell((short) 7);
        rowx12.getCell(7).setCellStyle(styleq31);
        cellx7.setCellValue(totalqtr4.doubleValue());

        totalexpense = BigDecimal.ZERO;
        totalqtr1 = BigDecimal.ZERO;
        totalqtr2 = BigDecimal.ZERO;
        totalqtr3 = BigDecimal.ZERO;
        totalqtr4 = BigDecimal.ZERO;
        rowstart++;

        coal = coalevel1Service.findByCode(3);
        if (isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(comboBoxBudget.getValue(), coal, selectedSections) == true) {
            rownum++;
            Row rowx = sheet.createRow(rownum);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 12));
            Cell cellx = rowx.createCell((short) 0);
            rowx.getCell(0).setCellStyle(stylegreen);
            cellx.setCellValue("CAPITAL EXPENDITURE");
            rowBoldcount.add((int) rownum);

            programmes = sampleURC_Priority_Areas.findByBudget(comboBoxBudget.getValue());
            List<RowsWorkplan> rowcount = new ArrayList();

            for (URC_Priority_Areas prog : programmes) {

                programmesActivities = sampleUrc_ActivitiesService.findActivitiesByBudgetAndPriorityAndDeptUnits(comboBoxBudget.getValue(), prog, selectedSections);
                if (programmesActivities != null) {
                    if (budgetItemsService.isSumProgrammeGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), programmesActivities, comboBoxD_Section.getSelectedItems(), 3) == true) {
                        for (Urc_Activities d : programmesActivities) {
                            if (budgetItemsService.isSumActvityGreaterThanZero(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 3) == true) {

                                rowend++;
                                short tc = 0;
                                BigDecimal er = null;
                                rownum++;
                                Row rowx1 = sheet.createRow(rownum);
                                rowx1.createCell((short) tc).setCellValue(prog.getName());

                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx1.createCell((short) tc).setCellValue(d.getName());

                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal tBigDecimal = budgetItemsService.sumActvitySummation(comboBoxOrganisation.getSelectedItems(), comboBoxBudget.getValue(), d, comboBoxD_Section.getSelectedItems(), 3);
                                totalexpense = totalexpense.add(tBigDecimal);
                                rowx1.createCell((short) tc).setCellValue(tBigDecimal.doubleValue());
                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                rowx1.createCell((short) tc).setCellValue(d.getFundsource());
                                rowx1.getCell(tc).setCellStyle(stylec);
                                tc++;
                                BigDecimal jul = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jul");
                                BigDecimal aug = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "aug");
                                BigDecimal sep = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "sep");
                                BigDecimal qtr1 = jul.add(aug).add(sep);
                                totalqtr1 = totalqtr1.add(qtr1);
                                rowx1.createCell((short) tc).setCellValue(qtr1.doubleValue());
                                if (qtr1.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }

                                tc++;
                                BigDecimal oct = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "oct");
                                BigDecimal nov = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "nov");
                                BigDecimal dec = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "dec");
                                BigDecimal qtr2 = oct.add(nov).add(dec);
                                totalqtr2 = totalqtr2.add(qtr2);
                                rowx1.createCell((short) tc).setCellValue(qtr2.doubleValue());
                                if (qtr2.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }

                                tc++;
                                BigDecimal jan = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jan");
                                BigDecimal feb = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "feb");
                                BigDecimal mar = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "mar");
                                BigDecimal qtr3 = jan.add(feb).add(mar);
                                totalqtr3 = totalqtr3.add(qtr3);
                                rowx1.createCell((short) tc).setCellValue(qtr3.doubleValue());
                                if (qtr3.doubleValue() > 0) {
                                    rowx1.getCell(tc).setCellStyle(styley);

                                }

                                tc++;
                                BigDecimal apr = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "apr");
                                BigDecimal may = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "may");
                                BigDecimal jun = budgetItemsService.findSumOfIndividualMonthsByBudgetCoalevel1Activity(comboBoxBudget.getValue(), 3, comboBoxD_Section.getSelectedItems(), d, comboBoxOrganisation.getSelectedItems(), "jun");
                                BigDecimal qtr4 = apr.add(may).add(jun);
                                totalqtr4 = totalqtr4.add(qtr4);
                                rowx1.createCell((short) tc).setCellValue(qtr4.doubleValue());
                                if (qtr4.doubleValue() > 0) {
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
                                tc++;
                                rowx1.createCell((short) tc).setCellValue("");
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

            }

            for (RowsWorkplan w : rowcount) {
                if ((w.getRowEnd() - w.getRowStart()) > 0) {
                    //sheet.addMergedRegion(new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0));
                    CellRangeAddress newMergedRegion = new CellRangeAddress(w.getRowStart(), w.getRowEnd(), 0, 0);
                    if (!isOverlappingWithExistingRegions(sheet, newMergedRegion)) {
                        addMergedRegion(sheet, newMergedRegion);
                    } else {
                        // Handle overlapping regions (e.g., adjust the region or skip it)
                    }
                }

            }

        }
        rownum++;
        Row rowx121 = sheet.createRow(rownum);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
        Cell cellx11 = rowx121.createCell((short) 0);
        rowx121.getCell(0).setCellStyle(styleq31);
        cellx11.setCellValue("TOTAL");
        Cell cellx21 = rowx121.createCell((short) 2);
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 3));
        rowx121.getCell(2).setCellStyle(styleq31);
        cellx21.setCellValue(totalexpense.doubleValue());
        Cell cellx41 = rowx121.createCell((short) 4);
        rowx121.getCell(4).setCellStyle(styleq31);
        cellx41.setCellValue(totalqtr1.doubleValue());
        Cell cellx51 = rowx121.createCell((short) 5);
        rowx121.getCell(5).setCellStyle(styleq31);
        cellx51.setCellValue(totalqtr2.doubleValue());
        Cell cellx61 = rowx121.createCell((short) 6);
        rowx121.getCell(6).setCellStyle(styleq31);
        cellx61.setCellValue(totalqtr3.doubleValue());
        Cell cellx71 = rowx121.createCell((short) 7);
        rowx121.getCell(7).setCellStyle(styleq31);
        cellx71.setCellValue(totalqtr4.doubleValue());

        // Iterate through each merged region and apply the border
        for (int i = 1; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            for (int rowz = mergedRegion.getFirstRow(); rowz <= mergedRegion.getLastRow(); rowz++) {
                Row currentRow = sheet.getRow(rowz);
                if (currentRow == null) {
                    currentRow = sheet.createRow(rowz);
                }

                for (int col = mergedRegion.getFirstColumn(); col <= mergedRegion.getLastColumn(); col++) {
                    Cell currentCell = currentRow.getCell(col);
                    if (currentCell == null) {
                        currentCell = currentRow.createCell(col);
                    }
                    CellStyle existingStyle = currentCell.getCellStyle();

                    // Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);
                    // Set the new style to the cell
                    currentCell.setCellStyle(newStyle);

                }
            }
        }
// Iterate through each row and cell and apply the border
        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);

                    currentCell.setCellStyle(newStyle);
                }

            }
            /*            for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
            }*/
        }
    }

    public boolean isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(Budget budget, Coalevel1 coalevel1, List<UrcDeptSectionAnlDimbgt> deptUnits) {

        return sampleBudgetItemsService.isSumBudgetCoalevel1AndDeptUnitsGreaterThanZero(budget, coalevel1, deptUnits, comboBoxOrganisation.getSelectedItems());
    }

    public BigDecimal calculateSumOfMonths(BudgetItems e) {
        BigDecimal sum = BigDecimal.ZERO;

        // Add all the months, handling null values
        sum = sum.add(e.getJul() != null ? e.getJul() : BigDecimal.ZERO)
                .add(e.getAug() != null ? e.getAug() : BigDecimal.ZERO)
                .add(e.getSep() != null ? e.getSep() : BigDecimal.ZERO)
                .add(e.getOct() != null ? e.getOct() : BigDecimal.ZERO)
                .add(e.getNov() != null ? e.getNov() : BigDecimal.ZERO)
                .add(e.getDec() != null ? e.getDec() : BigDecimal.ZERO)
                .add(e.getJan() != null ? e.getJan() : BigDecimal.ZERO)
                .add(e.getFeb() != null ? e.getFeb() : BigDecimal.ZERO)
                .add(e.getMar() != null ? e.getMar() : BigDecimal.ZERO)
                .add(e.getApr() != null ? e.getApr() : BigDecimal.ZERO)
                .add(e.getMay() != null ? e.getMay() : BigDecimal.ZERO)
                .add(e.getJun() != null ? e.getJun() : BigDecimal.ZERO);

        return sum;
    }

    private static void addImageToHeader(Sheet sheet, String imagePath) throws IOException {
        // Load the image
        BufferedImage bufferedImage = ImageIO.read(BudgetReportsView.class.getResourceAsStream(imagePath));

        // Convert the image to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Add the image to the header
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 0, 1, 1);

        int pictureIndex = sheet.getWorkbook().addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        drawing.createPicture(anchor, pictureIndex);
    }

    private void setBottomBorderForRegion(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
    }

    private static void createMergedRegion(Sheet sheet, int startRow, int endRow, int startColumn, int endColumn) {
        CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow, endRow, startColumn, endColumn);
        sheet.addMergedRegion(cellRangeAddress);
    }

    private static boolean isOverlappingWithExistingRegions(Sheet sheet, CellRangeAddress newRegion) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress existingRegion = sheet.getMergedRegion(i);
            if (newRegion.isInRange(existingRegion.getFirstRow(), existingRegion.getFirstColumn())
                    || newRegion.isInRange(existingRegion.getLastRow(), existingRegion.getLastColumn())) {
                return true; // Overlapping
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
