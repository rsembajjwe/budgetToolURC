package com.methaltech.application.views.budgetReport;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.methaltech.application.data.BudgetCalculator;
import com.methaltech.application.data.BudgetItemsSummaryProjection;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.CustomDetailedBudgetReportService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReport;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReportImp;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BudgetReportGeneratorPDF {

    private final Urc_ActivitiesService ActivityService;
    private final CustomDetailedBudgetReportService ReportService;
    private final BudgetItemsService sampleBudgetItemsService;
    private Set<Organisation> budgetTypes;

    public BudgetReportGeneratorPDF(Urc_ActivitiesService ActivityService,
            CustomDetailedBudgetReportService ReportService, BudgetItemsService sampleBudgetItemsService, Set<Organisation> budgetTypes) {
        this.ActivityService = ActivityService;
        this.ReportService = ReportService;
        this.sampleBudgetItemsService = sampleBudgetItemsService;
        this.budgetTypes = budgetTypes;
    }

    public String getOrganisationNames(Set<Organisation> budgetTypes) {
        if (budgetTypes == null || budgetTypes.isEmpty()) {
            return "";
        }

        return budgetTypes.stream()
                .map(Organisation::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private Cell wrap(Cell cell) {
        cell.setPaddingTop(2);
        cell.setPaddingBottom(2);
        cell.setPaddingLeft(2);
        cell.setPaddingRight(2);
        cell.setKeepTogether(false);
        cell.setKeepWithNext(false);
        cell.setFontSize(9);
        return cell;
    }

    // ✅ Optimized: stream directly to OutputStream
    public void streamCustomBudgetSummaryPdfReport(Budget budget,
            CustomDetailedBudgetReportImp reports,
            OutputStream outputStream) throws IOException {

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        // ✅ Landscape A4
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.setMargins(30, 30, 30, 30); // optional, adjust as needed
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLDITALIC);

        document.add(new Paragraph("UGANDA RAILWAYS CORPORATION").setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("BUDGET REPORT " + budget.getFinancialYear().toUpperCase()).setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(reports.getReportname().toUpperCase()).setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10f));
        document.add(new Paragraph("BUDGET TYPE(S): " + getOrganisationNames(budgetTypes)).setFont(itallicFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10f));

        //float[] columnWidths = {120, 150, 80, 130, 80, 80, 80, 60, 60, 60, 60};
        float[] columnWidths = {
            12.5f, // PROGRAMME
            15.6f, // ACTIVITY
            8.3f, // OUTPUT
            13.5f, // OUTCOME
            8.3f, // KPI
            8.3f, // ANNUAL TARGET
            8.3f, // BUDGET
            6.3f, // Q1
            6.3f, // Q2
            6.3f, // Q3
            6.3f // Q4
        };

        List<CustomDetailedBudgetReport> budgetreport = ReportService.findByBudgetreport(reports);

        if (!budgetreport.isEmpty()) {
            for (CustomDetailedBudgetReport rep : budgetreport) {
                Table mainTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
                mainTable.setWidth(UnitValue.createPercentValue(100));
                mainTable.setFixedLayout();
                Cell title = new Cell(1, 11) // rowSpan = 1, colSpan = 5
                        .add(new Paragraph(rep.getSheetname().toUpperCase())
                                .setFont(boldFont)
                                .setTextAlignment(TextAlignment.LEFT))
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
                mainTable.addCell(wrap(title));
                String[] headers = {"URC PROGRAMME", "ACTIVITY", "OUTPUT", "OUTCOME", "KPI", "ANNUAL TARGET",
                    "BUDGET", "QTR1", "QTR2", "QTR3", "QTR4"};
                for (String h : headers) {
                    mainTable.addHeaderCell(wrap(new Cell().add(new Paragraph(h).setFont(boldFont).setTextAlignment(TextAlignment.LEFT).setMarginBottom(10f))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER)));
                }

                List<Urc_Activities> activityList
                        = ActivityService.findWithAllJoinsByBudgetAndSectionSet(budget, rep.getDeptsection());

                // mainTable.addCell(new Cell().add(new Paragraph("")));
                //mainTable.addCell(new Cell().add(new Paragraph(rep.getSheetname().toUpperCase())));
                Map<String, List<Urc_Activities>> grouped
                        = activityList.stream()
                                .sorted(Comparator.comparing(a
                                        -> Optional.ofNullable(a.getUrcPriorityAreas())
                                        .map(p -> p.getName())
                                        .orElse("Unspecified")))
                                .collect(Collectors.groupingBy(a
                                        -> Optional.ofNullable(a.getUrcPriorityAreas())
                                        .map(p -> p.getName())
                                        .orElse("Unspecified"),
                                        LinkedHashMap::new, Collectors.toList()));
                List<BudgetItems> budgetListTotal = new ArrayList<>();

                for (Map.Entry<String, List<Urc_Activities>> entry : grouped.entrySet()) {
                    String programmeName = entry.getKey();
                    List<Urc_Activities> groupList = entry.getValue();

                    Cell mergedCell = new Cell(groupList.size(), 1)
                            .add(new Paragraph(programmeName)
                                    .setFont(normalFont).setFontSize(9)
                                    .setTextAlignment(TextAlignment.CENTER))
                            .setVerticalAlignment(VerticalAlignment.MIDDLE);
                    mainTable.addCell(wrap(mergedCell));

                    for (Urc_Activities act : groupList) {
                        mainTable.addCell(wrap(new Cell().add(new Paragraph(Optional.ofNullable(act.getName()).orElse("")).setFont(normalFont).setFontSize(9))));
                        mainTable.addCell(wrap(new Cell().add(new Paragraph(Optional.ofNullable(act.getOutput()).orElse("")).setFont(normalFont).setFontSize(9))));
                        mainTable.addCell(wrap(new Cell().add(new Paragraph(Optional.ofNullable(act.getOutcome()).orElse("")).setFont(normalFont).setFontSize(9))));
                        mainTable.addCell(wrap(new Cell().add(new Paragraph(Optional.ofNullable(act.getPerformanceIndicator()).orElse("")).setFont(normalFont).setFontSize(9))));
                        mainTable.addCell(wrap(new Cell().add(new Paragraph(Optional.ofNullable(act.getAnnualTarget()).orElse("")).setFont(normalFont).setFontSize(9))));

                        List<BudgetItems> budgetList = sampleBudgetItemsService.findDeptItemsForActivityWith2or3Coa(act.getBudget(), act, budgetTypes);
                        budgetListTotal.addAll(budgetList);
                        BudgetCalculator calculateBudget = new BudgetCalculator();
                        Map<String, BigDecimal> totals = calculateBudget.calculateQuarterlyAndTotalBudget(budgetList);

                        mainTable.addCell(new Paragraph(formatAmount(totals.get("TOTAL"))).setFont(normalFont).setFontSize(9));
                        mainTable.addCell(new Paragraph(formatAmount(totals.get("Q1"))).setFont(normalFont).setFontSize(9));
                        mainTable.addCell(new Paragraph(formatAmount(totals.get("Q2"))).setFont(normalFont).setFontSize(9));
                        mainTable.addCell(new Paragraph(formatAmount(totals.get("Q3"))).setFont(normalFont).setFontSize(9));
                        mainTable.addCell(new Paragraph(formatAmount(totals.get("Q4"))).setFont(normalFont).setFontSize(9));
                    }

                }
                Cell totalCell = new Cell(1, 6) // rowSpan = 1, colSpan = 5
                        .add(new Paragraph(rep.getSheetname().toUpperCase() + " BUDGET")
                                .setFont(boldFont)
                                .setTextAlignment(TextAlignment.LEFT))
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
                mainTable.addCell(wrap((totalCell)));
                BudgetCalculator calculateBudget = new BudgetCalculator();
                Map<String, BigDecimal> totals = calculateBudget.calculateQuarterlyAndTotalBudget(budgetListTotal);

                mainTable.addCell(new Paragraph(formatAmount(totals.get("TOTAL"))).setFont(boldFont).setFontSize(9));
                mainTable.addCell(new Paragraph(formatAmount(totals.get("Q1"))).setFont(boldFont).setFontSize(9));
                mainTable.addCell(new Paragraph(formatAmount(totals.get("Q2"))).setFont(boldFont).setFontSize(9));
                mainTable.addCell(new Paragraph(formatAmount(totals.get("Q3"))).setFont(boldFont).setFontSize(9));
                mainTable.addCell(new Paragraph(formatAmount(totals.get("Q4"))).setFont(boldFont).setFontSize(9));
                document.add(mainTable);
            }

        }

        document.close();
    }

    // ✅ Optimized: stream directly to OutputStream
    public void streamCustomBudgetByActivityByCOAPdfReport(
            Budget budget,
            CustomDetailedBudgetReportImp reports,
            OutputStream outputStream) throws IOException {

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(30, 30, 30, 30);

        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLDITALIC);

        // ─────────────────────────────────────────────────────
        // HEADER
        // ─────────────────────────────────────────────────────
        document.add(new Paragraph("UGANDA RAILWAYS CORPORATION")
                .setFont(boldFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("BUDGET REPORT " + budget.getFinancialYear().toUpperCase())
                .setFont(boldFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(reports.getReportname().toUpperCase())
                .setFont(boldFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("BUDGET TYPE(S): " + getOrganisationNames(budgetTypes)).setFont(itallicFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10f));

        document.add(new Paragraph("\n"));

        // Main table column widths matching Excel layout
        float[] mainCols = {80f, 150f, 110f, 110f, 100f, 90f, 90f, 70f, 70f, 70f, 70f};

        List<CustomDetailedBudgetReport> sheets = ReportService.findByBudgetreport(reports);

        for (CustomDetailedBudgetReport rep : sheets) {

            // Section Title Row
            Table sheetTitleTable = new Table(mainCols).useAllAvailableWidth();
            Cell titleCell = new Cell(1, 11)
                    .add(new Paragraph(rep.getSheetname().toUpperCase()).setFont(boldFont));
            sheetTitleTable.addCell(titleCell);
            document.add(sheetTitleTable);

            // Header Row
            Table headerTable = new Table(mainCols).useAllAvailableWidth();
            String[] headers = {
                "URC PROGRAMME", "ACTIVITY", "OUTPUT", "OUTCOME", "KPI",
                "ANNUAL TARGET", "BUDGET", "QTR1", "QTR2", "QTR3", "QTR4"
            };
            for (String h : headers) {
                headerTable.addCell(
                        new Cell().add(new Paragraph(h).setFont(boldFont).setFontSize(9))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setTextAlignment(TextAlignment.CENTER)
                );
            }
            document.add(headerTable);

            // Retrieve activities
            List<Urc_Activities> activityList
                    = ActivityService.findWithAllJoinsByBudgetAndSectionSet(budget, rep.getDeptsection());

            // Group by Programme (Priority Area)
            Map<String, List<Urc_Activities>> grouped
                    = activityList.stream()
                            .sorted(Comparator.comparing(a
                                    -> Optional.ofNullable(a.getUrcPriorityAreas())
                                    .map(p -> p.getName())
                                    .orElse("Unspecified")))
                            .collect(Collectors.groupingBy(
                                    a -> Optional.ofNullable(a.getUrcPriorityAreas())
                                            .map(p -> p.getName())
                                            .orElse("Unspecified"),
                                    LinkedHashMap::new, Collectors.toList()
                            ));

            List<BudgetItems> allItemsForTotals = new ArrayList<>();

            for (Map.Entry<String, List<Urc_Activities>> entry : grouped.entrySet()) {

                String programmeName = entry.getKey();
                List<Urc_Activities> acts = entry.getValue();

                for (Urc_Activities act : acts) {

                    // ──────────────────────────────────────────────────
                    // 1. ACTIVITY ROW (full width)
                    // ──────────────────────────────────────────────────
                    Table actRow = new Table(mainCols).useAllAvailableWidth();

                    actRow.addCell(text(programmeName, boldFont));
                    actRow.addCell(text(act.getName(), boldFont));
                    actRow.addCell(text(act.getOutput(), boldFont));
                    actRow.addCell(text(act.getOutcome(), boldFont));
                    actRow.addCell(text(act.getPerformanceIndicator(), boldFont));
                    actRow.addCell(text(act.getAnnualTarget(), boldFont));

                    List<BudgetItems> items
                            = sampleBudgetItemsService.findDeptItemsForActivityWith2or3Coa(
                                    act.getBudget(), act, budgetTypes);

                    allItemsForTotals.addAll(items);

                    BudgetCalculator calc = new BudgetCalculator();
                    Map<String, BigDecimal> totals = calc.calculateQuarterlyAndTotalBudget(items);

                    actRow.addCell(money(totals.get("TOTAL")).setFont(boldFont));
                    actRow.addCell(money(totals.get("Q1")).setFont(boldFont));
                    actRow.addCell(money(totals.get("Q2")).setFont(boldFont));
                    actRow.addCell(money(totals.get("Q3")).setFont(boldFont));
                    actRow.addCell(money(totals.get("Q4")).setFont(boldFont));

                    document.add(actRow);

                    // ──────────────────────────────────────────────────
                    // 2. “ACTIVITY BUDGET BY CHART OF ACCOUNTS” ROW
                    //    (START FROM 2nd COLUMN)
                    // ──────────────────────────────────────────────────
                    Table coaTitleRow = new Table(mainCols).useAllAvailableWidth();
                    coaTitleRow.addCell(blank()); // 1st column

                    Cell coaTitleCell = new Cell(1, 10)
                            .add(new Paragraph("BUDGET BY ACCOUNT CODE")
                                    .setFont(boldFont).setFontSize(9));

                    coaTitleRow.addCell(coaTitleCell);
                    document.add(coaTitleRow);

                    // ──────────────────────────────────────────────────
                    // 3. COA TABLE (ALSO STARTS FROM COLUMN 2)
                    // ──────────────────────────────────────────────────
                    float[] coaCols = {80f, 150f, 90f, 60f, 60f, 60f, 60f, 60f};
                    Table coaTable = new Table(coaCols).useAllAvailableWidth();

                    List<BudgetItemsSummaryProjection> coaSummary
                            = sampleBudgetItemsService.getDistinctBudgetItemsByCoa(
                                    act.getBudget(), act, budgetTypes);

                    // Dummy left indent column
                    coaTable.addCell(blank());

                    coaTable.addCell(headerSmall("CODE"));
                    coaTable.addCell(headerSmall("DESCRIPTION"));
                    coaTable.addCell(headerSmall("TOTAL"));
                    coaTable.addCell(headerSmall("QTR1"));
                    coaTable.addCell(headerSmall("QTR2"));
                    coaTable.addCell(headerSmall("QTR3"));
                    coaTable.addCell(headerSmall("QTR4"));

                    for (BudgetItemsSummaryProjection item : coaSummary) {
                        coaTable.addCell(blank());
                        coaTable.addCell(textSmall(item.getCoacode().getCode()));
                        coaTable.addCell(textSmall(item.getCoacode().getName()));
                        coaTable.addCell(moneySmall(item.getTotal()));
                        coaTable.addCell(moneySmall(item.getQtr1()));
                        coaTable.addCell(moneySmall(item.getQtr2()));
                        coaTable.addCell(moneySmall(item.getQtr3()));
                        coaTable.addCell(moneySmall(item.getQtr4()));
                    }

                    document.add(coaTable);

                    // Spacing
                    document.add(new Paragraph("\n"));
                }
            }

            // ──────────────────────────────────────────────────
            // 4. TOTAL ROW (START FROM COLUMN 2)
            // ──────────────────────────────────────────────────
            BudgetCalculator calc = new BudgetCalculator();
            Map<String, BigDecimal> totals = calc.calculateQuarterlyAndTotalBudget(allItemsForTotals);

            Table totalRow = new Table(mainCols).useAllAvailableWidth();
            totalRow.addCell(blank()); // indent

            Cell totalLabel = new Cell(1, 5)
                    .add(new Paragraph(rep.getSheetname() + " TOTAL").setFont(boldFont))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);

            totalRow.addCell(totalLabel);

            totalRow.addCell(money(totals.get("TOTAL")) .setBackgroundColor(ColorConstants.LIGHT_GRAY).setFont(boldFont));
            totalRow.addCell(money(totals.get("Q1")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setFont(boldFont));
            totalRow.addCell(money(totals.get("Q2")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setFont(boldFont));
            totalRow.addCell(money(totals.get("Q3")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setFont(boldFont));
            totalRow.addCell(money(totals.get("Q4")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setFont(boldFont));

            document.add(totalRow);
            document.add(new Paragraph("\n"));
        }

        document.close();
    }

    private String formatAmount(BigDecimal value) {
        return value == null ? "-" : String.format("%,.0f", value);
    }

    private Cell cell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9));
    }

    private Cell subCell(String text, PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(8));
    }

    private Cell amountCell(BigDecimal amount, PdfFont font) {
        return new Cell().add(new Paragraph(formatAmount(amount)).setFont(font).setFontSize(9));
    }

    private String n(String value) {
        return value == null ? "" : value;
    }

    private Cell text(String value, PdfFont font) {
        return new Cell().add(new Paragraph(Optional.ofNullable(value).orElse(""))
                .setFont(font).setFontSize(9));
    }

    private Cell money(BigDecimal val) {
        String v = val == null ? "-" : String.format("%,d", val.longValue());
        return new Cell().add(new Paragraph(v).setFontSize(9));
    }

    private Cell blank() {
        return new Cell().add(new Paragraph(""));
    }

    private Cell headerSmall(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(7))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell textSmall(String value) {
        return new Cell().add(new Paragraph(value).setFontSize(7));
    }

    private Cell moneySmall(BigDecimal val) {
        String v = val == null ? "-" : String.format("%,d", val.longValue());
        return new Cell().add(new Paragraph(v).setFontSize(7));
    }

}
