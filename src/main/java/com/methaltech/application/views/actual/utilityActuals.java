package com.methaltech.application.views.actual;

import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.Budget;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataFormat;
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
    PeriodExtractor per = new PeriodExtractor();
    List<SALFLDGProjection> items = new ArrayList<>();

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
        if (comboBoxD_Section.contains("#              ")) {
            items = sampleSALFLDGService.findByPeriodAndDepartmentExpendituresWithNullSections(getFinancialYearPeriods(budget), comboBoxD_Section);
        } else {
            items = sampleSALFLDGService.findExpendituresByPeriodAndSections(getFinancialYearPeriods(budget), comboBoxD_Section);
        }
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
        //gridTransactions.addColumn(SALFLDGProjection::getPeriod).setHeader("Period");

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.setItems(items);

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
            List<SALFLDGProjection> items2 = refreshgridTransactions2();
            exportAndDownloadExcelTransactionDetails(budget, items, overviewContent, 4);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }

    public Dialog createTransactionsDialog3(HorizontalLayout overviewContent, int qtr) {
        Dialog dialog = new Dialog();
        dialog.getElement().getStyle().set("padding-top", "50px");
        dialog.setHeaderTitle("Transactions " + budget.getFinancialYear());

        Grid<SALFLDGProjection> gridTransactions = new Grid<>(SALFLDGProjection.class, false);
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

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
        if (comboBoxD_Section.contains("#              ")) {
            items = sampleSALFLDGService.findByPeriodAndDepartmentExpendituresWithNullSections(getFinancialYearPeriods(budget), comboBoxD_Section);
        } else {
            items = sampleSALFLDGService.findExpendituresByPeriodAndSections(getFinancialYearPeriods(budget), comboBoxD_Section);
        }

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
        //gridTransactions.addColumn(SALFLDGProjection::getPeriod).setHeader("Period");

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);

        Set<Integer> periods = extra.getFinancialYearPeriodsByCumQuarter(budget, qtr);
        List<SALFLDGProjection> items3 = filterByPeriods(items, periods);
        gridTransactions.setItems(items3);

        footerTotal.setText("Total: " + sumAmounts(items3));
        footerTotal.getStyle().set("text-align", "right").set("font-weight", "bold");

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
            //List<SALFLDGProjection> items2 = refreshgridTransactions2();

            exportAndDownloadExcelTransactionDetails(budget, items3, overviewContent, 4);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }
    
    public Dialog createTransactionsDialog4(HorizontalLayout overviewContent, int qtr,String accCode) {
        Dialog dialog = new Dialog();
        dialog.getElement().getStyle().set("padding-top", "50px");
        dialog.setHeaderTitle("Transactions " + budget.getFinancialYear());

        Grid<SALFLDGProjection> gridTransactions = new Grid<>(SALFLDGProjection.class, false);
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

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
        if (comboBoxD_Section.contains("#              ")) {
            items = sampleSALFLDGService.findByPeriodAndDepartmentExpendituresWithNullSections(getFinancialYearPeriods(budget), comboBoxD_Section);
        } else {
            items = sampleSALFLDGService.findExpendituresByPeriodAndSections(getFinancialYearPeriods(budget), comboBoxD_Section);
        }

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
        //gridTransactions.addColumn(SALFLDGProjection::getPeriod).setHeader("Period");

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);

        Set<Integer> periods = extra.getFinancialYearPeriodsByCumQuarter(budget, qtr);
        List<SALFLDGProjection> items3 = filterByPeriods(items, periods,accCode);
        gridTransactions.setItems(items3);

        footerTotal.setText("Total: " + sumAmounts(items3));
        footerTotal.getStyle().set("text-align", "right").set("font-weight", "bold");

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
            //List<SALFLDGProjection> items2 = refreshgridTransactions2();

            exportAndDownloadExcelTransactionDetails(budget, items3, overviewContent, 4);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }    

    private List<SALFLDGProjection> filterByPeriods(
            List<SALFLDGProjection> items,
            Set<Integer> periods
    ) {
        if (items == null || periods == null || periods.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .filter(item -> item.getPeriod() != null && periods.contains(item.getPeriod()))
                .toList();
    }
    
    private List<SALFLDGProjection> filterByPeriods(
            List<SALFLDGProjection> items,
            Set<Integer> periods,String code
    ) {
        if (items == null || periods == null || periods.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .filter(item -> item.getPeriod() != null && periods.contains(item.getPeriod()))
                .filter(item -> item.getAccntCode() != null && code.equals(item.getAccntCode()))
                .toList();
    }    

    public void exportAndDownloadExcelTransactionDetails(Budget budget, List<SALFLDGProjection> list, HorizontalLayout overviewContent, int qtr) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(" Transactions " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createTransactionDetails(workbook, sheet, list, qtr);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            String filename = "QTR " + qtr + " Transactios";
            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(filename + " " + budget.getFinancialYear() + ".xlsx", ()
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

    private enum TxSKey {
        TITLE, SUBTITLE, HEADER,
        CODE, TEXT, TEXT_ZEBRA,
        MONEY, MONEY_ZEBRA,
        DATE, DATE_ZEBRA,
        TOTAL_LABEL, TOTAL_MONEY
    }

    /**
     * Transaction Details export (executive format) Columns now: Code,
     * Description, Journal No., Amount, Date, Section - Journal Line removed -
     * Period removed - Amount sign normalized: if AccntCode starts with 2 or 3
     * -> always positive else -> always negative
     */
    private void createTransactionDetails(Workbook workbook, Sheet sheet, List<SALFLDGProjection> list, int qtr) {
        Map<TxSKey, CellStyle> st = buildTransactionStyles(workbook);

        // Columns: 0..5
        final int COL_CODE = 0, COL_DESC = 1, COL_JNO = 2, COL_AMT = 3, COL_DATE = 4, COL_SEC = 5;
        final int LAST_COL = 5;

        int r = 0;

        // ===== Row 0: Logo + Title =====
        Row titleRow = sheet.createRow(r);
        titleRow.setHeightInPoints(26);

        try {
            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");
        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        titleCell.setCellStyle(st.get(TxSKey.TITLE));
        sheet.addMergedRegion(new CellRangeAddress(r, r, 1, LAST_COL));
        r++;

        // ===== Row 1: Subtitle band =====
        String fy = (budget != null && budget.getFinancialYear() != null) ? budget.getFinancialYear() : "";

        Row subRow = sheet.createRow(r);
        subRow.setHeightInPoints(18);

        Cell subCell = subRow.createCell(0);
        subCell.setCellValue("QUARTER " + qtr + " TRANSACTIONS " + fy);
        subCell.setCellStyle(st.get(TxSKey.SUBTITLE));
        sheet.addMergedRegion(new CellRangeAddress(r, r, 0, LAST_COL));

        for (int c = 1; c <= LAST_COL; c++) {
            Cell cc = subRow.createCell(c);
            cc.setCellStyle(st.get(TxSKey.SUBTITLE));
        }
        r++;

        // ===== Row 2: Header =====
        Row hdr = sheet.createRow(r);
        hdr.setHeightInPoints(34);

        createTextCell(hdr, COL_CODE, "Code", st.get(TxSKey.HEADER));
        createTextCell(hdr, COL_DESC, "Description", st.get(TxSKey.HEADER));
        createTextCell(hdr, COL_JNO, "Journal No.", st.get(TxSKey.HEADER));
        createTextCell(hdr, COL_AMT, "Amount", st.get(TxSKey.HEADER));
        createTextCell(hdr, COL_DATE, "Date", st.get(TxSKey.HEADER));
        createTextCell(hdr, COL_SEC, "Section", st.get(TxSKey.HEADER));
        r++;

        // Freeze below header; keep Code+Description fixed
        sheet.createFreezePane(2, 3);

        // ===== Data rows =====
        int firstDataRowIdx = r;

        if (list != null) {
            // Null-safe ordering
            list.sort(
                    Comparator.comparing(
                            SALFLDGProjection::getTransDatetime,
                            Comparator.nullsLast(Date::compareTo)
                    )
                            .thenComparing(
                                    SALFLDGProjection::getJrnalNo,
                                    Comparator.nullsLast(Integer::compareTo)
                            )
                            .thenComparing(
                                    SALFLDGProjection::getAccntCode,
                                    Comparator.nullsLast(String::compareTo)
                            )
            );
        }

        int idx = 0;
        for (SALFLDGProjection t : safeList(list)) {
            Row row = sheet.createRow(r++);
            boolean zebra = (idx++ % 2 == 1);

            CellStyle textStyle = zebra ? st.get(TxSKey.TEXT_ZEBRA) : st.get(TxSKey.TEXT);
            CellStyle codeStyle = st.get(TxSKey.CODE);
            CellStyle moneyStyle = zebra ? st.get(TxSKey.MONEY_ZEBRA) : st.get(TxSKey.MONEY);
            CellStyle dateStyle = zebra ? st.get(TxSKey.DATE_ZEBRA) : st.get(TxSKey.DATE);

            String code = safeStr(t.getAccntCode());

            createTextCell(row, COL_CODE, code, codeStyle);
            createTextCell(row, COL_DESC, safeStr(t.getDescriptn()), textStyle);

            // journal no as text to preserve formatting
            createTextCell(row, COL_JNO, t.getJrnalNo() == null ? "" : String.valueOf(t.getJrnalNo()), textStyle);

            // Amount sign normalization:
            // 2/3 -> positive, else -> negative
            BigDecimal amt = t.getAmount() == null ? BigDecimal.ZERO : t.getAmount();
            if (code.startsWith("2") || code.startsWith("3")) {
                amt = amt.abs();
            } else {
                amt = amt.abs().negate();
            }
            //createMoneyCell(row, COL_AMT, amt.doubleValue(), moneyStyle);
            createMoneyCell(row, COL_AMT, t.getAmount().doubleValue(), moneyStyle);

            if (t.getTransDatetime() != null) {
                Cell dc = row.createCell(COL_DATE);
                dc.setCellValue(t.getTransDatetime());
                dc.setCellStyle(dateStyle);
            } else {
                createTextCell(row, COL_DATE, "", dateStyle);
            }

            createTextCell(row, COL_SEC, safeStr(t.getAnalT1()), textStyle);
        }

        int lastDataRowIdx = r - 1;

        // ===== Total row =====
        Row totalRow = sheet.createRow(r++);
        totalRow.setHeightInPoints(18);

        Cell t0 = totalRow.createCell(COL_CODE);
        t0.setCellValue("Total");
        t0.setCellStyle(st.get(TxSKey.TOTAL_LABEL));

        for (int c = 1; c <= LAST_COL; c++) {
            Cell cell = totalRow.createCell(c);
            cell.setCellStyle(c == COL_AMT ? st.get(TxSKey.TOTAL_MONEY) : st.get(TxSKey.TOTAL_LABEL));
        }

        if (lastDataRowIdx >= firstDataRowIdx) {
            int firstExcel = firstDataRowIdx + 1;
            int lastExcel = lastDataRowIdx + 1;
            String amtColLetter = org.apache.poi.ss.util.CellReference.convertNumToColString(COL_AMT); // "D"
            totalRow.getCell(COL_AMT).setCellFormula(
                    String.format("SUM(%s%d:%s%d)", amtColLetter, firstExcel, amtColLetter, lastExcel)
            );
        } else {
            totalRow.getCell(COL_AMT).setCellValue(0d);
        }

        // ===== Executive sizing =====
        applyTransactionColumnSizing(sheet);

        // ===== Print/visual polish =====
        sheet.setDisplayGridlines(false);
        sheet.setDefaultRowHeightInPoints(18);

        PrintSetup ps = sheet.getPrintSetup();
        ps.setLandscape(true);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        sheet.setRepeatingRows(CellRangeAddress.valueOf("1:3"));
    }

// widths tuned for compact, professional layout
    private void applyTransactionColumnSizing(Sheet sheet) {
        sheet.setColumnWidth(0, 16 * 256); // Code
        sheet.setColumnWidth(1, 75 * 256); // Description (wide => less wrapping => shorter rows)
        sheet.setColumnWidth(2, 14 * 256); // Journal No
        sheet.setColumnWidth(3, 16 * 256); // Amount
        sheet.setColumnWidth(4, 14 * 256); // Date
        sheet.setColumnWidth(5, 22 * 256); // Section
    }

// ------------------- styles + helpers -------------------
    private Map<TxSKey, CellStyle> buildTransactionStyles(Workbook wb) {
        Map<TxSKey, CellStyle> s = new EnumMap<>(TxSKey.class);
        DataFormat df = wb.createDataFormat();

        Font title = wb.createFont();
        title.setFontName("Calibri");
        title.setFontHeightInPoints((short) 16);
        title.setBold(true);

        Font hdr = wb.createFont();
        hdr.setFontName("Calibri");
        hdr.setFontHeightInPoints((short) 10);
        hdr.setBold(true);
        hdr.setColor(IndexedColors.WHITE.getIndex());

        Font normal = wb.createFont();
        normal.setFontName("Calibri");
        normal.setFontHeightInPoints((short) 10);

        Font bold = wb.createFont();
        bold.setFontName("Calibri");
        bold.setFontHeightInPoints((short) 10);
        bold.setBold(true);

        java.util.function.Consumer<CellStyle> borders = st -> {
            st.setBorderTop(BorderStyle.THIN);
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderLeft(BorderStyle.THIN);
            st.setBorderRight(BorderStyle.THIN);
            st.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        };

        CellStyle titleSt = wb.createCellStyle();
        titleSt.setFont(title);
        titleSt.setAlignment(HorizontalAlignment.CENTER);
        titleSt.setVerticalAlignment(VerticalAlignment.CENTER);
        s.put(TxSKey.TITLE, titleSt);

        CellStyle sub = wb.createCellStyle();
        sub.setFont(bold);
        sub.setAlignment(HorizontalAlignment.LEFT);
        sub.setVerticalAlignment(VerticalAlignment.CENTER);
        sub.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        sub.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(sub);
        s.put(TxSKey.SUBTITLE, sub);

        CellStyle header = wb.createCellStyle();
        header.setFont(hdr);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);
        header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(header);
        s.put(TxSKey.HEADER, header);

        CellStyle code = wb.createCellStyle();
        code.setFont(normal);
        code.setAlignment(HorizontalAlignment.CENTER);
        code.setVerticalAlignment(VerticalAlignment.TOP);
        borders.accept(code);
        s.put(TxSKey.CODE, code);

        CellStyle text = wb.createCellStyle();
        text.setFont(normal);
        text.setAlignment(HorizontalAlignment.LEFT);
        text.setVerticalAlignment(VerticalAlignment.TOP);
        text.setWrapText(true);
        borders.accept(text);
        s.put(TxSKey.TEXT, text);

        CellStyle textZ = wb.createCellStyle();
        textZ.cloneStyleFrom(text);
        textZ.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        textZ.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(TxSKey.TEXT_ZEBRA, textZ);

        CellStyle money = wb.createCellStyle();
        money.cloneStyleFrom(text);
        money.setAlignment(HorizontalAlignment.RIGHT);
        money.setDataFormat(df.getFormat("#,##0.00"));
        s.put(TxSKey.MONEY, money);

        CellStyle moneyZ = wb.createCellStyle();
        moneyZ.cloneStyleFrom(money);
        moneyZ.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        moneyZ.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(TxSKey.MONEY_ZEBRA, moneyZ);

        CellStyle date = wb.createCellStyle();
        date.cloneStyleFrom(text);
        date.setAlignment(HorizontalAlignment.CENTER);
        date.setDataFormat(df.getFormat("yyyy-MM-dd"));
        s.put(TxSKey.DATE, date);

        CellStyle dateZ = wb.createCellStyle();
        dateZ.cloneStyleFrom(date);
        dateZ.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        dateZ.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(TxSKey.DATE_ZEBRA, dateZ);

        CellStyle totalLabel = wb.createCellStyle();
        totalLabel.cloneStyleFrom(sub);
        totalLabel.setAlignment(HorizontalAlignment.LEFT);
        s.put(TxSKey.TOTAL_LABEL, totalLabel);

        CellStyle totalMoney = wb.createCellStyle();
        totalMoney.cloneStyleFrom(sub);
        totalMoney.setAlignment(HorizontalAlignment.RIGHT);
        totalMoney.setDataFormat(df.getFormat("#,##0.00"));
        s.put(TxSKey.TOTAL_MONEY, totalMoney);

        return s;
    }

    private void createTextCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private void createMoneyCell(Row row, int colIndex, double value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String safeStr(String s) {
        return s == null ? "" : s;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
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

    public List<SALFLDGProjection> refreshgridTransactions2(int qtr) {
        List<SALFLDGProjection> lis = new ArrayList<>();
        lis = sampleSALFLDGService.findExpendituresByPeriodAndSections(per.getFinancialYearPeriodsByQuarter(budget, qtr), comboBoxD_Section);

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
