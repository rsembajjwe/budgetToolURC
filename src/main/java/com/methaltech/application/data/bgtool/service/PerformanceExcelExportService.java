package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.CurrencyFormatter;
import com.methaltech.application.data.GetPeriods;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReport;
import com.methaltech.application.data.entity.bgtool.PerformanceReportContext;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import jakarta.persistence.Tuple;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class PerformanceExcelExportService {

    // ================= DEPENDENCIES =================
    private final Urc_ActivitiesService activitiesService;
    private final BudgetItemsService budgetItemsService;
    private final QtrReleasesServiceImpl qtrReleasesService;
    private final SALFLDGService salfldgService;
    private final SectionBudgetPerformanceService sectionBudgetPerformanceService;

    private final CurrencyFormatter formatter = new CurrencyFormatter();
    private final GetPeriods periods = new GetPeriods();
    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    List<PriorityArea> priorityAreas = new ArrayList<>();

    public PerformanceExcelExportService(
            Urc_ActivitiesService activitiesService,
            BudgetItemsService budgetItemsService,
            QtrReleasesServiceImpl qtrReleasesService,
            SALFLDGService salfldgService,
            SectionBudgetPerformanceService sectionBudgetPerformanceService,
            URC_Priority_AreasService sampleURC_Priority_AreasService
    ) {
        this.activitiesService = activitiesService;
        this.budgetItemsService = budgetItemsService;
        this.qtrReleasesService = qtrReleasesService;
        this.salfldgService = salfldgService;
        this.sectionBudgetPerformanceService = sectionBudgetPerformanceService;
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
    }

    // =================================================
    // EXPORT ENTRY POINT
    // =================================================
    public byte[] export(
            PerformanceReportContext ctx,
            List<CustomDetailedBudgetReport> reports
    ) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle bodyStyle = createBodyStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            for (CustomDetailedBudgetReport report : reports) {

                Sheet sheet = workbook.createSheet(
                        sanitizeSheetName(report.getSheetname())
                );
                setupA4Print(sheet);

                int row = 0;

                // ===== REPORT HEADER =====
                row = addReportHeader(sheet, ctx, row);

                // ===== FINANCIAL SECTION =====
                row = addSectionTitle(sheet, "1. FINANCIAL PERFORMANCE", row);
                row = buildFinancialTable(
                        sheet, ctx, report,
                        row, headerStyle, bodyStyle, moneyStyle
                );

                row += 2;

                // ===== PHYSICAL SECTION =====
                row = addSectionTitle(sheet, "2. PHYSICAL PERFORMANCE", row);

                List<Urc_Activities> activities
                        = activitiesService.findByDeptSectionAndBudget(
                                report.getDeptsection(), ctx.getBudget()
                        );

                buildPhysicalTable(
                        sheet, activities, ctx,
                        row, headerStyle, bodyStyle
                );

                applyColumnWidths(sheet);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // =================================================
    // REPORT HEADER (CORPORATE)
    // =================================================
    private int addReportHeader(Sheet sheet, PerformanceReportContext ctx, int row) {

        row = mergedTitle(sheet, row,
                "UGANDA RAILWAYS CORPORATION", true, 12);

        row = mergedTitle(sheet, row,
                "FINANCIAL & PHYSICAL PERFORMANCE REPORT", true, 11);

        row = mergedTitle(sheet, row,
                ctx.getFinancialYear() + " – Quarter " + ctx.getQuarter(),
                false, 10);

        return row + 1;
    }

    private int mergedTitle(
            Sheet sheet,
            int rowIdx,
            String text,
            boolean bold,
            int fontSize
    ) {
        Workbook wb = sheet.getWorkbook();

        Font font = wb.createFont();
        font.setBold(bold);
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName("Arial");

        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        Row row = sheet.createRow(rowIdx);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);

        sheet.addMergedRegion(
                new CellRangeAddress(rowIdx, rowIdx, 0, 6)
        );

        return rowIdx + 1;
    }

    private int addSectionTitle(Sheet sheet, String title, int row) {

        Workbook wb = sheet.getWorkbook();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);

        CellStyle style = wb.createCellStyle();
        style.setFont(font);

        Row r = sheet.createRow(row);
        Cell c = r.createCell(0);
        c.setCellValue(title);
        c.setCellStyle(style);

        sheet.addMergedRegion(
                new CellRangeAddress(row, row, 0, 6)
        );

        return row + 1;
    }

    // =================================================
    // FINANCIAL TABLE
    // =================================================
    private int buildFinancialTable(
            Sheet sheet,
            PerformanceReportContext ctx,
            CustomDetailedBudgetReport report,
            int row,
            CellStyle header,
            CellStyle body,
            CellStyle money
    ) {
        priorityAreas = sampleURC_Priority_AreasService.getDistinctPriorityAreasByBudget(ctx.getBudget().getStartDate());

        String[] headers = {
            "NDP Programme",
            "Planned Budget",
            "Approved Budget",
            "Cumulative Release",
            "Actual Spent",
            "% Release Spent",
            "Reasons"
        };

        Row headerRow = sheet.createRow(row++);
        for (int i = 0; i < headers.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        BigDecimal planned
                = budgetItemsService
                        .computeGrandExpenditureTotals(
                                ctx.getBudget(), report.getDeptsection()
                        )[0];

        Tuple totals
                = qtrReleasesService.getCumulativeQuarterReleases(
                        ctx.getBudget().getId(),
                        report.getDeptsection()
                );

        BigDecimal release = getQuarterRelease(totals, ctx.getQuarter());
        BigDecimal actual = getActualSpent(ctx, report);
        String reasons = buildReasons(ctx, report);
        for (PriorityArea p : priorityAreas) {
            Row r = sheet.createRow(row++);
            writeCell(r, 0, p.getName(), body);
            writeMoney(r, 1, planned, money);
            writeMoney(r, 2, planned, money);
            writeMoney(r, 3, release, money);
            writeMoney(r, 4, actual, money);
            writeCell(r, 5, percentage(actual, release), body);
            writeCell(r, 6, reasons, body);
        }

        return row;
    }

    // =================================================
    // PHYSICAL TABLE
    // =================================================
    private void buildPhysicalTable(
            Sheet sheet,
            List<Urc_Activities> activities,
            PerformanceReportContext ctx,
            int row,
            CellStyle header,
            CellStyle body
    ) {

        String[] headers = {
            "URC Programme",
            "Activity",
            "Indicator",
            "Annual Target",
            "Cumulative Achievement",
            "Actual",
            "Explanation"
        };

        Row headerRow = sheet.createRow(row++);
        for (int i = 0; i < headers.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        for (Urc_Activities act : activities) {

            Row r = sheet.createRow(row++);

            writeCell(r, 0,
                    act.getUrcPriorityAreas() != null
                    ? act.getUrcPriorityAreas().getName() : "—", body);
            writeCell(r, 1, act.getName(), body);
            writeCell(r, 2, act.getPerformanceIndicator(), body);
            writeCell(r, 3, act.getAnnualTarget(), body);
            writeCell(r, 4, getCumulativeAchievement(act, ctx.getQuarter()), body);
            writeCell(r, 5, getActualAchievement(act, ctx), body);
            writeCell(r, 6, getVariation(act, ctx.getQuarter()), body);
        }
    }

    // =================================================
    // BUSINESS HELPERS
    // =================================================
    private BigDecimal getQuarterRelease(Tuple totals, int qtr) {
        return switch (qtr) {
            case 1 ->
                totals.get("q1Total", BigDecimal.class);
            case 2 ->
                totals.get("q2Total", BigDecimal.class);
            case 3 ->
                totals.get("q3Total", BigDecimal.class);
            case 4 ->
                totals.get("q4Total", BigDecimal.class);
            default ->
                BigDecimal.ZERO;
        };
    }

    private BigDecimal getActualSpent(
            PerformanceReportContext ctx,
            CustomDetailedBudgetReport report
    ) {
        Set<Integer> p = periods.getFinancialYearPeriods(
                ctx.getBudget(), ctx.getQuarter()
        );

        return salfldgService.getTotalAmountByPeriods(
                p,
                salfldgService.extractTrimmedAnlCodes(report.getDeptsection())
        );
    }

    private String buildReasons(
            PerformanceReportContext ctx,
            CustomDetailedBudgetReport report
    ) {

        Map<String, SectionBudgetPerformance> map
                = sectionBudgetPerformanceService.findByBudget(ctx.getBudget())
                        .stream()
                        .collect(Collectors.toMap(
                                p -> p.getDeptSection().getANL_CODE(),
                                Function.identity(),
                                (a, b) -> a
                        ));

        return report.getDeptsection().stream()
                .map(s -> map.get(s.getANL_CODE()))
                .filter(Objects::nonNull)
                .map(p -> getReasonByQuarter(p, ctx.getQuarter()))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private String getCumulativeAchievement(Urc_Activities a, int qtr) {
        return switch (qtr) {
            case 1 ->
                a.getCum_achievements_qtr1();
            case 2 ->
                a.getCum_achievements_qtr2();
            case 3 ->
                a.getCum_achievements_qtr3();
            case 4 ->
                a.getCum_achievements_qtr4();
            default ->
                "-";
        };
    }

    private String getActualAchievement(Urc_Activities a, PerformanceReportContext ctx) {
        Set<Integer> p = periods.getFinancialYearPeriods(
                a.getBudget(), ctx.getQuarter()
        );
        return formatter.format(
                qtrReleasesService.getTotalAmountByPeriodsAndActivity(p, a)
        );
    }

    private String getVariation(Urc_Activities a, int qtr) {
        return switch (qtr) {
            case 1 ->
                a.getExpl_of_variations_qtr1();
            case 2 ->
                a.getExpl_of_variations_qtr2();
            case 3 ->
                a.getExpl_of_variations_qtr3();
            case 4 ->
                a.getExpl_of_variations_qtr4();
            default ->
                "-";
        };
    }

    // =================================================
    // STYLES & PRINT
    // =================================================
    private void setupA4Print(Sheet sheet) {
        PrintSetup ps = sheet.getPrintSetup();
        ps.setPaperSize(PrintSetup.A4_PAPERSIZE);
        ps.setLandscape(true);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);
        sheet.setHorizontallyCenter(true);
        sheet.setVerticallyCenter(false);
        sheet.setAutobreaks(true);
    }

    private void applyColumnWidths(Sheet s) {
        s.setColumnWidth(0, 7000);
        s.setColumnWidth(1, 5000);
        s.setColumnWidth(2, 5000);
        s.setColumnWidth(3, 5000);
        s.setColumnWidth(4, 5000);
        s.setColumnWidth(5, 4000);
        s.setColumnWidth(6, 9000);
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 9);

        CellStyle s = wb.createCellStyle();
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setWrapText(true);
        return s;
    }

    private CellStyle createBodyStyle(Workbook wb) {
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 9);

        CellStyle s = wb.createCellStyle();
        s.setFont(f);
        s.setBorderBottom(BorderStyle.THIN);
        s.setWrapText(true);
        return s;
    }

    private CellStyle createMoneyStyle(Workbook wb) {
        CellStyle s = createBodyStyle(wb);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        return s;
    }

    // =================================================
    // UTIL
    // =================================================
    private void writeCell(Row r, int c, String v, CellStyle s) {
        Cell cell = r.createCell(c);
        cell.setCellValue(v != null ? v : "—");
        cell.setCellStyle(s);
    }

    private void writeMoney(Row r, int c, BigDecimal v, CellStyle s) {
        Cell cell = r.createCell(c);
        cell.setCellValue(v != null ? v.doubleValue() : 0);
        cell.setCellStyle(s);
    }

    private String percentage(BigDecimal v, BigDecimal t) {
        if (v == null || t == null || t.signum() == 0) {
            return "0%";
        }
        return v.multiply(BigDecimal.valueOf(100))
                .divide(t, 2, RoundingMode.HALF_UP) + "%";
    }

    private String sanitizeSheetName(String n) {
        return n.replaceAll("[\\\\/?*\\[\\]]", "_");
    }

    private String getReasonByQuarter(SectionBudgetPerformance p, int q) {
        return switch (q) {
            case 1 ->
                p.getReasonsForUnderOver1();
            case 2 ->
                p.getReasonsForUnderOver2();
            case 3 ->
                p.getReasonsForUnderOver3();
            case 4 ->
                p.getReasonsForUnderOver4();
            default ->
                null;
        };
    }
}
