package com.methaltech.application.data.bgtool.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReport;
import com.methaltech.application.data.entity.bgtool.PerformanceReportContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.SolidBorder;
import com.methaltech.application.data.CurrencyFormatter;
import com.methaltech.application.data.GetPeriods;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class PerformancePdfExportService {

    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final BudgetItemsService sampleBudgetItemsService;

    CurrencyFormatter f = new CurrencyFormatter();
    private final QtrReleasesServiceImpl qtrReleasesServiceImpl;
    private final SALFLDGService samopleSALFLDGService;
    GetPeriods periods = new GetPeriods();
    Set<Integer> period = new HashSet<>();
    Map<String, SectionBudgetPerformance> performanceMap = new HashMap<>();
    private final SectionBudgetPerformanceService sectionBudgetPerformanceService;
    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    List<PriorityArea> priorityAreas = new ArrayList<>();
    // ===== Corporate color palette =====
    private static final DeviceRgb PRIMARY = new DeviceRgb(0, 51, 102);   // Navy
    private static final DeviceRgb HEADER_BG = new DeviceRgb(235, 240, 245);
    private static final DeviceRgb SECTION_BG = new DeviceRgb(220, 226, 233); // slightly darker than header
    private static final DeviceRgb BORDER = new DeviceRgb(200, 200, 200);
    private static final DeviceRgb TEXT = new DeviceRgb(33, 37, 41);

    public PerformancePdfExportService(Urc_ActivitiesService sampleUrc_ActivitiesService, BudgetItemsService sampleBudgetItemsService,
            QtrReleasesServiceImpl qtrReleasesServiceImpl, SALFLDGService samopleSALFLDGService, SectionBudgetPerformanceService sectionBudgetPerformanceService,
            URC_Priority_AreasService sampleURC_Priority_AreasService) {
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.sampleBudgetItemsService = sampleBudgetItemsService;
        this.qtrReleasesServiceImpl = qtrReleasesServiceImpl;
        this.samopleSALFLDGService = samopleSALFLDGService;
        this.sectionBudgetPerformanceService = sectionBudgetPerformanceService;
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
    }

    public byte[] export(
            PerformanceReportContext ctx,
            List<CustomDetailedBudgetReport> reports
    ) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(50, 36, 50, 36);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        for (int i = 0; i < reports.size(); i++) {
            CustomDetailedBudgetReport report = reports.get(i);

            if (i > 0) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // --- Report Header ---
            addReportHeader(document, ctx);
            document.add(new Paragraph(report.getSheetname())
                    .setFont(fontBold)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER));

// --- Financial Performance Section ---
            addSectionHeader(document, "1. FINANCIAL PERFORMANCE");  // 👈 Use here
            document.add(buildFinancialTable(ctx, report));

// --- Physical Performance Section ---
            addSectionHeader(document, "2. PHYSICAL PERFORMANCE");  // 👈 Use here
            List<Urc_Activities> acts = sampleUrc_ActivitiesService.findByDeptSectionAndBudget(report.getDeptsection(), ctx.getBudget());
            document.add(buildPhysicalTable(ctx, acts));

// --- Next page for next CustomDetailedBudgetReport if needed ---
            //  document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        }

        document.close();
        return out.toByteArray();
    }

    private void addReportHeader(Document document, PerformanceReportContext ctx) throws IOException {
        PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        document.add(new Paragraph("UGANDA RAILWAYS CORPORATION")
                .setFont(fontBold)
                .setFontSize(14)
                .setFontColor(PRIMARY)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("FINANCIAL & PHYSICAL PERFORMANCE REPORT")
                .setFont(fontBold)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(
                ctx.getFinancialYear() + " – Quarter " + ctx.getQuarter()
        )
                .setFont(fontRegular)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    }

    private void addSectionHeader(Document document, String title) throws IOException {
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        document.add(new Paragraph(title)
                .setFont(fontBold)
                .setFontSize(10)
                .setFontColor(PRIMARY)
                .setMarginTop(15)
                .setMarginBottom(8));

        document.add(new LineSeparator(new SolidLine(1))
                .setStrokeColor(BORDER)
                .setMarginBottom(10));
    }

    private void addHeader(Table table, String text) throws IOException {
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        table.addHeaderCell(
                new Cell()
                        .add(new Paragraph(text)
                                .setFont(fontBold)
                                .setFontSize(9)
                                .setFontColor(TEXT))
                        .setBackgroundColor(HEADER_BG)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(BORDER, 0.5f))
                        .setPadding(6)
        );
    }
    
        private void addHeaderLast(Table table, String text) throws IOException {
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        table.addHeaderCell(
                new Cell(1, 2)
                        .add(new Paragraph(text)
                                .setFont(fontBold)
                                .setFontSize(9)
                                .setFontColor(TEXT))
                        .setBackgroundColor(HEADER_BG)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(BORDER, 0.5f))
                        .setPadding(6)
        );
    }

    private Cell bodyCell(String value, TextAlignment align) throws IOException {
        PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        return new Cell()
                .add(new Paragraph(value != null ? value : "—")
                        .setFont(fontRegular)
                        .setFontSize(9)
                        .setFontColor(TEXT))
                .setTextAlignment(align)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(BORDER, 0.5f))
                .setPadding(5);
    }

    private String formatCurrency(BigDecimal value) {
        return value != null ? String.format("%,.2f", value) : "0.00";
    }

    private Table buildFinancialTable(PerformanceReportContext ctx, CustomDetailedBudgetReport report) throws IOException {
        float[] columnWidths = {
            4, 3, 3, 3, 2, 2, 2
        };

        priorityAreas = sampleURC_Priority_AreasService
                .getDistinctPriorityAreasByBudget(ctx.getBudget().getStartDate());

        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth()
                .setMarginBottom(15)
                .setBorder(new SolidBorder(BORDER, 0.8f));

        addHeader(table, "NDP Programme");
        addHeader(table, "Planned Budget (UGX)");
        addHeader(table, "Approved Budget (UGX)");
        addHeader(table, "Actual Spent (UGX)");
        addHeader(table, "% Release Spent");
        addHeaderLast(table, "Reasons");


        BigDecimal[] computeGrandExpenditureTotals
                = sampleBudgetItemsService.computeGrandExpenditureTotals(ctx.getBudget(), report.getDeptsection());
        BigDecimal planned = computeGrandExpenditureTotals[0];

        Tuple totals = qtrReleasesServiceImpl.getCumulativeQuarterReleases(
                ctx.getBudget().getId(), report.getDeptsection());

        BigDecimal cumulativeRelease = switch (ctx.getQuarter()) {
            case 1 ->
                nvl(totals.get("q1Total", BigDecimal.class));
            case 2 ->
                nvl(totals.get("q2Total", BigDecimal.class));
            case 3 ->
                nvl(totals.get("q3Total", BigDecimal.class));
            case 4 ->
                nvl(totals.get("q4Total", BigDecimal.class));
            default ->
                BigDecimal.ZERO;
        };

        BigDecimal actualSpent = BigDecimal.ZERO;

        switch (ctx.getQuarter()) {
            case 1:
                actualSpent = samopleSALFLDGService.getTotalAmountByPeriods(
                        periods.getFinancialYearPeriods(ctx.getBudget(), 1),
                        samopleSALFLDGService.extractTrimmedAnlCodes(report.getDeptsection()));
                break;
            case 2:
                Set<Integer> period1 = periods.getFinancialYearPeriods(ctx.getBudget(), 1);
                Set<Integer> period2 = periods.getFinancialYearPeriods(ctx.getBudget(), 2);
                period1.addAll(period2);
                period = period1;
                actualSpent = samopleSALFLDGService.getTotalAmountByPeriods(
                        period,
                        samopleSALFLDGService.extractTrimmedAnlCodes(report.getDeptsection()));
                break;
            case 3:
                period = Stream.concat(
                        periods.getFinancialYearPeriods(ctx.getBudget(), 1).stream(),
                        periods.getFinancialYearPeriods(ctx.getBudget(), 2).stream()
                ).collect(Collectors.toSet());
                period = Stream.concat(
                        period.stream(),
                        periods.getFinancialYearPeriods(ctx.getBudget(), 3).stream()
                ).collect(Collectors.toSet());
                actualSpent = samopleSALFLDGService.getTotalAmountByPeriods(
                        period,
                        samopleSALFLDGService.extractTrimmedAnlCodes(report.getDeptsection()));
                break;
            case 4:
                period = Stream.concat(
                        periods.getFinancialYearPeriods(ctx.getBudget(), 1).stream(),
                        periods.getFinancialYearPeriods(ctx.getBudget(), 2).stream()
                ).collect(Collectors.toSet());
                period = Stream.concat(
                        period.stream(),
                        periods.getFinancialYearPeriods(ctx.getBudget(), 3).stream()
                ).collect(Collectors.toSet());
                period = Stream.concat(
                        period.stream(),
                        periods.getFinancialYearPeriods(ctx.getBudget(), 4).stream()
                ).collect(Collectors.toSet());
                actualSpent = samopleSALFLDGService.getTotalAmountByPeriods(
                        period,
                        samopleSALFLDGService.extractTrimmedAnlCodes(report.getDeptsection()));
                break;
            default:
                actualSpent = BigDecimal.ZERO;
                break;
        }

        String percentage = calculatePercentage(actualSpent.abs(), planned);

        List<SectionBudgetPerformance> performances = sectionBudgetPerformanceService.findByBudget(ctx.getBudget());

        String reasons = "";

        performanceMap = performances.stream()
                .collect(Collectors.toMap(
                        p -> p.getDeptSection().getANL_CODE(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        if (performanceMap.isEmpty()) {
            reasons = "—";
        } else {
            reasons = report.getDeptsection().stream()
                    .map(sect -> performanceMap.get(sect.getANL_CODE()))
                    .filter(Objects::nonNull)
                    .map(p -> getReasonByQuarter(p, ctx.getQuarter()))
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining(", "));
        }

        for (PriorityArea p : priorityAreas) {
            table.addCell(bodyCell(p.getName(), TextAlignment.LEFT));
            table.addCell(money(planned));
            table.addCell(money(planned));
            table.addCell(money(actualSpent.abs()));
            table.addCell(bodyCell(percentage, TextAlignment.LEFT));

            Cell reasonsCell = new Cell(1, 2)
                    .add(new Paragraph(reasons))
                    .setTextAlignment(TextAlignment.LEFT);

            table.addCell(reasonsCell);
        }

        return table;
    }

    private String getReasonByQuarter(SectionBudgetPerformance p, int quarter) {
        return switch (quarter) {
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

    public Cell money(BigDecimal value) {
        return body(f.format(value))
                .setTextAlignment(TextAlignment.RIGHT);
    }

    public String calculatePercentage(
            BigDecimal value,
            BigDecimal total) {
        DecimalFormat PERCENT_FORMAT
                = new DecimalFormat("#,##0.00'%'");
        if (value == null || total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00%";
        }

        BigDecimal percentage = value
                .multiply(BigDecimal.valueOf(100))
                .divide(total, 4, RoundingMode.HALF_UP);

        return PERCENT_FORMAT.format(percentage.abs());
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public Cell body(String text) {
        return new Cell()
                .add(new Paragraph(text == null ? "—" : text))
                .setFontSize(9)
                .setPadding(4);
    }

    private Table buildPhysicalTable(
            PerformanceReportContext ctx,
            List<Urc_Activities> activities
    ) throws IOException {

        float[] columnWidths = {3, 4, 4, 2, 3, 2, 4};

        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth()
                .setMarginBottom(15)
                .setBorder(new SolidBorder(BORDER, 0.8f));

        // ================= HEADERS =================
        addHeader(table, "URC Programme");
        addHeader(table, "Activity");
        addHeader(table, "Performance Indicator");
        addHeader(table, "Annual Target");
        addHeader(table, "Cumulative Achievement");
        addHeader(table, "% Target");
        addHeader(table, "Explanation");

        // ================= GROUP ACTIVITIES =================
        Map<URC_Priority_Areas, List<Urc_Activities>> grouped
                = activities.stream()
                        .collect(Collectors.groupingBy(
                                Urc_Activities::getUrcPriorityAreas,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));

        // ================= ROWS =================
        for (Map.Entry<URC_Priority_Areas, List<Urc_Activities>> entry : grouped.entrySet()) {

            URC_Priority_Areas priorityArea = entry.getKey();
            List<Urc_Activities> acts = entry.getValue();

            int rowSpan = acts.size();
            boolean firstRow = true;

            for (Urc_Activities act : acts) {

                // -------- MERGED PRIORITY AREA CELL --------
                if (firstRow) {
                    table.addCell(new Cell(rowSpan, 1)
                            .add(new Paragraph(
                                    priorityArea != null
                                            ? priorityArea.getName()
                                            : "—"
                            ))
                            .setFontSize(9)
                            .setFontColor(TEXT)
                            .setBackgroundColor(HEADER_BG)
                            .setTextAlignment(TextAlignment.LEFT)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPadding(6)
                            .setBorder(new SolidBorder(BORDER, 0.5f))
                    );
                    firstRow = false;
                }

                // -------- ACTIVITY DETAILS --------
                table.addCell(body(act.getName()));
                table.addCell(body(act.getPerformanceIndicator()));
                table.addCell(body(act.getAnnualTarget()));

                // -------- CUMULATIVE ACHIEVEMENT --------
                table.addCell(body(getCumulativeAchievement(act, ctx.getQuarter())));

                // -------- % TARGET / ACTUAL ACHIEVEMENT --------
                table.addCell(body(getPerAchievement(act, ctx.getQuarter())));

                // -------- EXPLANATION --------
                table.addCell(body(getVariation(act, ctx.getQuarter())));
            }
        }

        return table;
    }

    private String getCumulativeAchievement(Urc_Activities act, int qtr) {
        return switch (qtr) {
            case 1 ->
                act.getCum_achievements_qtr1();
            case 2 ->
                act.getCum_achievements_qtr2();
            case 3 ->
                act.getCum_achievements_qtr3();
            case 4 ->
                act.getCum_achievements_qtr4();
            default ->
                "-";
        };
    }

    private String getPerAchievement(Urc_Activities act, int qtr) {
        return switch (qtr) {
            case 1 ->
                act.getPerc_of_TargetAchieved_qtr1();
            case 2 ->
                act.getPerc_of_TargetAchieved_qtr2();
            case 3 ->
                act.getPerc_of_TargetAchieved_qtr3();
            case 4 ->
                act.getPerc_of_TargetAchieved_qtr4();
            default ->
                "-";
        };
    }

    private String getActualAchievement(Urc_Activities act, PerformanceReportContext ctx) {

        Set<Integer> periodsSet
                = periods.getFinancialYearPeriods(act.getBudget(), ctx.getQuarter());

        return formatCurrency(
                qtrReleasesServiceImpl
                        .getTotalAmountByPeriodsAndActivity(periodsSet, act)
                        .abs()
        );
    }

    private String getVariation(Urc_Activities act, int qtr) {
        return switch (qtr) {
            case 1 ->
                act.getExpl_of_variations_qtr1();
            case 2 ->
                act.getExpl_of_variations_qtr2();
            case 3 ->
                act.getExpl_of_variations_qtr3();
            case 4 ->
                act.getExpl_of_variations_qtr4();
            default ->
                "-";
        };
    }

}
