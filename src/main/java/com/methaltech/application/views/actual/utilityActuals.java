package com.methaltech.application.views.actual;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.livedata.repository.SALFLDGProjection;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.views.budgetReport.BudgetReportsView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class utilityActuals {

    Budget budget;
    private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    PeriodExtractor extra = new PeriodExtractor();
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    private Span footerTotal = new Span("");
    Set<String> comboBoxD_Section;

    public utilityActuals(Budget budget, CoaService sampleCoaService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, SALFLDGService sampleSALFLDGService, Set<String> comboBoxD_Section) {
        this.budget = budget;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.comboBoxD_Section = comboBoxD_Section;
    }
    public Dialog createTransactionsDialog2(HorizontalLayout overviewContent) {
        Dialog dialog = new Dialog();
        dialog.getElement().getStyle().set("padding-top", "50px");
        dialog.setHeaderTitle("Transactions " + budget.getFinancialYear());

        Grid<SALFLDGProjection> gridTransactions = new Grid<>(SALFLDGProjection.class, false);
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // Columns
        /*                gridTransactions.addColumn(SALFLDGProjection::getAccntCode)
        .setHeader("Code")
        .setWidth("80px")
        .setFlexGrow(0)
        .setSortable(true).setTooltipGenerator(trans -> {
        COA coa = sampleCoaService.findByCodeAndBudget(trans.getAccntCode(), budget);
        return coa.getName();
        });*/
        gridTransactions.addColumn(SALFLDGProjection::getAccntCode)
                .setHeader("Code")
                .setWidth("80px")
                .setFlexGrow(0)
                .setSortable(true);

        gridTransactions.addColumn(SALFLDGProjection::getDescriptn).setHeader("Name").setFrozenToEnd(true);

        gridTransactions.addColumn(SALFLDGProjection::getJrnalNo)
                .setHeader("Journal No.")
                .setWidth("80px")
                .setFlexGrow(0);

        // List<SALFLDGProjection> items = refreshgridTransactions2(budget.getFinancialYear(), budget.getCoacode().getCode());
        List<SALFLDGProjection> items = sampleSALFLDGService.findExpendituresByPeriodAndSections(getFinancialYearPeriods(budget), comboBoxD_Section);
        footerTotal.setText("Total: " + sumAmounts(items));
        footerTotal.getStyle().set("text-align", "right").set("font-weight", "bold");

        gridTransactions.addColumn(salfldg -> {
            BigDecimal amount = salfldg.getAmount();
            return amount != null ? formatAmount(amount) : "";
        }).setHeader("Amount").setAutoWidth(true).setFooter(footerTotal);
        gridTransactions.addColumn(SALFLDGProjection::getTransDatetime).setHeader("Trans Date");
        gridTransactions.addColumn(SALFLDGProjection::getAnalT1).setHeader("Section").setTooltipGenerator(trans -> {
            String sectcode = trans.getAnalT1();
            if (sectcode == null || sectcode.trim() == "") {
                return "_";
            } else {
                // return sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE(trans.getAnalT1()).getNAME();

                return sectcode;
            }

        });
        gridTransactions.addColumn(SALFLDGProjection::getPeriod).setHeader("Period");

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.setItems(items);

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
             List<SALFLDGProjection>  items2 = refreshgridTransactions2();
             exportAndDownloadExcelTransactionDetails(budget, items, overviewContent);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }
    
    private void exportAndDownloadExcelTransactionDetails(Budget budget, List<SALFLDGProjection> list,HorizontalLayout overviewContent) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(" Transactions " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createTransactionDetails(workbook, sheet, list);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(" Transactions " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
               overviewContent.add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTransactionDetails(Workbook workbook, Sheet sheet, List<SALFLDGProjection> list) {
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

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.LEFT);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);
        stylec.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec2 = workbook.createCellStyle();
        stylec2.setAlignment(HorizontalAlignment.CENTER);
        stylec2.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec2.setWrapText(true);
        stylec2.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        stylec2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylec2.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

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
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 6);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("TRANSACTION DETAILS " + budget.getFinancialYear());
        CellRangeAddress cellRange4 = new CellRangeAddress(tr, tr, 0, 6);
        sheet.addMergedRegion(cellRange4);
        rowBoldcount.add((int) 0);
        tr++;

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("Code");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Description");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Journal No.");
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Amount");
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Date");

        Cell cell6 = row.createCell((short) 5);
        row.getCell(5).setCellStyle(styleq31);
        cell6.setCellValue("Section");

        Cell cell7 = row.createCell((short) 6);
        row.getCell(6).setCellStyle(styleq31);
        cell7.setCellValue("period");

        rowBoldcount.add((int) 1);
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

// Format for dates, e.g., "yyyy-MM-dd" or any other format you need
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (SALFLDGProjection h : list) {
            tr++;
            short tc = 0;
            Row rowx1 = sheet.createRow(tr);
            rowx1.createCell((short) tc).setCellValue(h.getAccntCode());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getDescriptn());

            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJrnalNo() + " ");
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAmount().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;

            if (h.getTransDatetime() != null) {
                rowx1.createCell(tc).setCellValue(h.getTransDatetime());
                rowx1.getCell(tc).setCellStyle(dateCellStyle);
            } else {
                rowx1.createCell(tc).setCellValue("");
                rowx1.getCell(tc).setCellStyle(stylec);
            }
            /*            rowx1.createCell((short) tc).setCellValue(h.getTransDatetime());
                rowx1.getCell(tc).setCellStyle(stylec);*/
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAnalT1() + " ");
            rowx1.getCell(tc).setCellStyle(stylec);

            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getPeriod() + " ");
            rowx1.getCell(tc).setCellStyle(stylec);

        }
        // Add a row for the total amount using a formula
        Row totalRow = sheet.createRow(++tr);
        short tc = 0;
        totalRow.createCell(tc).setCellValue("Total");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;

        // Set the formula for summing up the amount column
        String amountColumnRange = String.format("D2:D%d", tr); // Assuming the Amount column is the 4th column (index 3)
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);

        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
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
    }

    public String sumAmounts(List<SALFLDGProjection> projections) {
        BigDecimal sum = BigDecimal.ZERO;
        for (SALFLDGProjection projection : projections) {
            sum = sum.add(projection.getAmount());
        }
        String sums = decimalFormat.format(sum);
        if (sum == null) {
            return "";
        }
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            sums = decimalFormat.format(sum.abs());
            return "(" + sums + ")";
        } else {
            sums = decimalFormat.format(sum);
            return "(" + sums + ")";
        }

    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return ""; // Return an empty string if amount is null
        }
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount.doubleValue());
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

    public List<SALFLDGProjection> refreshgridTransactions2() {
        List<SALFLDGProjection> lis = new ArrayList<>();
        lis = sampleSALFLDGService.findExpendituresByPeriodAndSections(getFinancialYearPeriods(budget), comboBoxD_Section);

        return lis;
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
}
