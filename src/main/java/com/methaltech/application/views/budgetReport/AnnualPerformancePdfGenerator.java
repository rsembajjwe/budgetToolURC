package com.methaltech.application.views.budgetReport;


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.methaltech.application.data.GetPeriods;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.SectionBudgetPerformanceService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.Urc_ActivitiesService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import com.methaltech.application.data.entity.bgtool.QuarterlyActuals;
import com.methaltech.application.data.entity.bgtool.SectionBudgetPerformance;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import java.io.OutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnualPerformancePdfGenerator {

    private final URC_Priority_AreasService sampleURC_Priority_AreasService;
    private final Urc_ActivitiesService sampleUrc_ActivitiesService;
    private final SALFLDGService SALFLDGService;
    private final BudgetItemsService budgetItemsService;
    private final SectionBudgetPerformanceService sectionBudgetPerformanceService;
    private final UrcDeptSectionAnlDimbgt departmentName;
    private final String quarter;
    Budget budget;
    int qtr = 0;
    GetPeriods periods = new GetPeriods();
    Set<Integer> period = new HashSet<>();
    BigDecimal cumRealiseSpent = BigDecimal.ZERO;
    BigDecimal totalBudget = BigDecimal.ZERO;
    BigDecimal totalActualExpenditure = BigDecimal.ZERO;
    List<Urc_Activities> acts = new ArrayList();
    SectionBudgetPerformance budgetPer = null;

    public AnnualPerformancePdfGenerator(URC_Priority_AreasService sampleURC_Priority_AreasService, Urc_ActivitiesService sampleUrc_ActivitiesService,
            UrcDeptSectionAnlDimbgt departmentName, SALFLDGService SALFLDGService,
            BudgetItemsService budgetItemsService, SectionBudgetPerformanceService sectionBudgetPerformanceService, String quarter, Budget budget) {
        this.sampleURC_Priority_AreasService = sampleURC_Priority_AreasService;
        this.sampleUrc_ActivitiesService = sampleUrc_ActivitiesService;
        this.SALFLDGService = SALFLDGService;
        this.budgetItemsService = budgetItemsService;
        this.sectionBudgetPerformanceService = sectionBudgetPerformanceService;
        this.departmentName = departmentName;
        this.quarter = quarter;
        this.budget = budget;

        configureQuarter();
        setData();
    }

    /**
     * Generates the Annual Performance PDF Report for a given budget.
     *
     * @param budget The budget containing financial performance
     * @param physicalPerformances List of physical performance entries
     * @param outputStream Output stream where the PDF will be written
     * @throws IOException
     */
    /*    public void generateAnnualReportPdf(OutputStream outputStream) throws IOException {
    PdfWriter writer = new PdfWriter(outputStream);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf, PageSize.A4.rotate());
    document.setMargins(40, 40, 40, 40);
    PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
    PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
    PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
    
    // === Title ===
    Paragraph title;
    title = new Paragraph()
    .add("UGANDA RAILWAYS CORPORATION\n").setFont(boldFont)
    .add(departmentName.getNAME().toUpperCase() + "\n").setFont(boldFont)
    .add("FINANCIAL & PHYSICAL PERFORMANCE REPORT " + budget.getFinancialYear() + " " + quarter.toUpperCase()).setFont(boldFont)
    .setFontSize(12)
    .setTextAlignment(TextAlignment.CENTER);
    
    document.add(title);
    document.add(new Paragraph("\n"));
    
    try {
    // === Financial Performance Table ===
    addFinancialPerformanceTable(document);
    
    document.add(new Paragraph("\n")); // spacing
    // === Physical Performance Table ===
    addPhysicalPerformanceTable(document, acts);
    
    } catch (Exception e) {
    e.printStackTrace();
    } finally {
    // === Footer ===
    // === Footer ===
    // === Footer ===
    int numberOfPages = pdf.getNumberOfPages();
    String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
    for (int i = 1; i <= numberOfPages; i++) {
    String footerText = String.format(
    "Generated from Budget Tool | %s | Page %d of %d",
    timeStamp, i, numberOfPages
    );
    
    Paragraph footer = new Paragraph(footerText)
    .setFontSize(8)
    .setTextAlignment(TextAlignment.CENTER);
    
    float x = pdf.getPage(i).getPageSize().getWidth() / 2;
    float y = 20; // 20 pts from bottom
    
    // ✅ Use page rectangle here
    Rectangle pageSize = pdf.getPage(i).getPageSize();
    new Canvas(new PdfCanvas(pdf.getPage(i)), pageSize)
    .showTextAligned(footer, x, y, TextAlignment.CENTER);
    }
    
    document.close();
    }
    }*/
    public void generateAnnualReportPdf(OutputStream outputStream) throws IOException {
        try {
            System.out.println("Step 1: Creating PDF writer");
            PdfWriter writer = new PdfWriter(outputStream);
            System.out.println("Step 2: Creating PDF document");
            PdfDocument pdf = new PdfDocument(writer);
            System.out.println("Step 3: Creating document");
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(40, 40, 40, 40);

            System.out.println("Step 4: Creating fonts");
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

            System.out.println("Step 5: Creating title");
            Paragraph title = new Paragraph()
                    .add("UGANDA RAILWAYS CORPORATION\n").setFont(boldFont)
                    .add((departmentName != null ? departmentName.getNAME() : "N/A").toUpperCase() + "\n").setFont(boldFont)
                    .add("FINANCIAL & PHYSICAL PERFORMANCE REPORT "
                            + (budget != null ? budget.getFinancialYear() : "N/A") + " "
                            + (quarter != null ? quarter.toUpperCase() : "N/A"))
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);

            System.out.println("Step 6: Adding title to document");
            document.add(title);
            document.add(new Paragraph("\n"));

            System.out.println("Step 7: Adding financial table");
            addFinancialPerformanceTable(document);
            document.add(new Paragraph("\n"));

            System.out.println("Step 8: Adding physical table");
            if (acts != null) {
                addPhysicalPerformanceTable(document, acts);
                document.add(new Paragraph("\n"));
            }

            System.out.println("Step 9: Adding footers");
            if (normalFont == null) {
                System.err.println("ERROR: normalFont is null!");
                normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            }

            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            int numberOfPages = pdf.getNumberOfPages();
            System.out.println("Number of pages: " + numberOfPages);

            pdf.close();
            System.out.println("PDF created successfully with footers!");

            System.out.println("Step 10: Closing document");
            document.close();
            System.out.println("PDF generation completed successfully");
        } catch (NullPointerException e) {
            System.err.println("NPE in PDF generation");
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("Error in PDF generation: " + e.getMessage());
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    // ------------------------ Financial Table ------------------------
    private void addFinancialPerformanceTable(Document doc) {
        try {
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            //float[] colWidths = {150f, 120f, 120f, 120f, 120f, 120f, 150f};
            float[] colWidths = {2f, 3f, 3f, 2f, 2f, 2f, 3f};
            Table table = new Table(colWidths);

            addHeaderCell(table, "NDP Programme");
            addHeaderCell(table, "Planned Budget (UGX)");
            addHeaderCell(table, "Approved Budget (UGX)");
            addHeaderCell(table, "Cumulative Funds Released (UGX)");
            addHeaderCell(table, "Cumulative Funds Spent (UGX)");
            addHeaderCell(table, "% Spent");
            addHeaderCell(table, "Reasons for Under/Over Absorption");

            List<PriorityArea> priorityAreas = sampleURC_Priority_AreasService.getDistinctPriorityAreasByBudget(budget.getStartDate());
            for (PriorityArea area : priorityAreas) {
                Optional<SectionBudgetPerformance> isbudgetPerExist = sectionBudgetPerformanceService.findByBudgetAndDeptSection(budget, departmentName);
                if (isbudgetPerExist.isPresent()) {
                    budgetPer = isbudgetPerExist.get();

                } else {
                    budgetPer = new SectionBudgetPerformance();

                }
                addCell(table, area.getName());
                addCell(table, formatCurrency(budgetPer.getPlannedBudget()));
                addCell(table, formatCurrency(budgetPer.getApprovedBudget()));

                switch (qtr) {
                    case 1 -> {
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsReleased1()));
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsSpent1()));
                        addCell(table, (budgetPer.getPercentageSpent1()));
                        String reasons = budgetPer.getReasonsForUnderOver1() != null ? budgetPer.getReasonsForUnderOver1() : "";
                        addCell(table, reasons);
                    }
                    case 2 -> {
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsReleased2()));
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsSpent2()));
                        addCell(table, (budgetPer.getPercentageSpent2()));
                        String reasons = budgetPer.getReasonsForUnderOver2() != null ? budgetPer.getReasonsForUnderOver2() : "";
                        addCell(table, reasons);
                    }
                    case 3 -> {
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsReleased3()));
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsSpent3()));
                        addCell(table, (budgetPer.getPercentageSpent3()));
                        String reasons = budgetPer.getReasonsForUnderOver3() != null ? budgetPer.getReasonsForUnderOver3() : "";
                        addCell(table, reasons);
                    }
                    case 4 -> {
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsReleased4()));
                        addCell(table, formatCurrency(budgetPer.getCumulativeFundsSpent4()));
                        addCell(table, (budgetPer.getPercentageSpent4()));
                        String reasons = budgetPer.getReasonsForUnderOver4() != null ? budgetPer.getReasonsForUnderOver4() : "";
                        addCell(table, reasons);
                    }
                    default -> {
                        addCell(table, "");
                        addCell(table, "");
                        addCell(table, "");
                        addCell(table, "");
                    }

                }

            }

            doc.add(new Paragraph("1. FINANCIAL PERFORMANCE").setFont(boldFont).setFontSize(12));
            doc.add(table);
        } catch (IOException ex) {
            Logger.getLogger(AnnualPerformancePdfGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ------------------------ Physical Table ------------------------
    private void addPhysicalPerformanceTable(Document doc, List<Urc_Activities> performances) {
        try {
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            //float[] colWidths = {120f, 150f, 120f, 80f, 80f, 80f, 150f};
            float[] colWidths = {2f, 3f, 3f, 2f, 2f, 2f, 3f};
            Table table = new Table(colWidths);

            addHeaderCell(table, "URC Programme");
            addHeaderCell(table, "Approved Annual Work Plan");
            addHeaderCell(table, "Key Performance Indicator (KPI)");
            addHeaderCell(table, "Annual Target");
            addHeaderCell(table, "Cumulative Achievements");
            addHeaderCell(table, "% Target Achieved");
            addHeaderCell(table, "Explanation for Variations");

            for (Urc_Activities pp : performances) {
                addCell(table, safe(pp.getUrcPriorityAreas().getName()));
                addCell(table, safe(pp.getName()));
                addCell(table, safe(pp.getPerformanceIndicator()));
                addCell(table, safe(pp.getAnnualTarget()));

                switch (qtr) {
                    case 1 -> {
                        addCell(table, safe(pp.getCum_achievements_qtr1()));
                        addCell(table, safe(pp.getPerc_of_TargetAchieved_qtr1()));
                        addCell(table, safe(pp.getExpl_of_variations_qtr1()));
                    }
                    case 2 -> {
                        addCell(table, safe(pp.getCum_achievements_qtr2()));
                        addCell(table, safe(pp.getPerc_of_TargetAchieved_qtr2()));
                        addCell(table, safe(pp.getExpl_of_variations_qtr2()));
                    }
                    case 3 -> {
                        addCell(table, safe(pp.getCum_achievements_qtr3()));
                        addCell(table, safe(pp.getPerc_of_TargetAchieved_qtr3()));
                        addCell(table, safe(pp.getExpl_of_variations_qtr3()));
                    }
                    case 4 -> {
                        addCell(table, safe(pp.getCum_achievements_qtr4()));
                        addCell(table, safe(pp.getPerc_of_TargetAchieved_qtr4()));
                        addCell(table, safe(pp.getExpl_of_variations_qtr4()));
                    }
                    default -> {
                        addCell(table, "");
                        addCell(table, "");
                        addCell(table, "");
                    }

                }

            }

            doc.add(new Paragraph("2. PHYSICAL PERFORMANCE").setFont(boldFont).setFontSize(12));
            doc.add(table);
        } catch (IOException ex) {
            Logger.getLogger(AnnualPerformancePdfGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ------------------------ Helpers ------------------------
    private void addHeaderCell(Table table, String text) {
        try {
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            Cell cell = new Cell().add(new Paragraph(text).setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(cell);
        } catch (IOException ex) {
            Logger.getLogger(AnnualPerformancePdfGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addCell(Table table, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFontSize(10)));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    private String formatPercentage(BigDecimal value) {
        if (value == null) {
            return "0%";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toString() + "%";
    }

    private void setData() {
        switch (qtr) {
            case 1:
                period = periods.getFinancialYearPeriods(budget, qtr);
                break;
            case 2:
                period = Stream.concat(periods.getFinancialYearPeriods(budget, qtr).stream(), periods.getFinancialYearPeriods(budget, 2).stream()).collect(Collectors.toSet());
                break;
            case 3:
                period = Stream.concat(periods.getFinancialYearPeriods(budget, qtr).stream(), periods.getFinancialYearPeriods(budget, 2).stream()).collect(Collectors.toSet());
                period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(budget, 3).stream()).collect(Collectors.toSet());
                break;
            case 4:
                period = Stream.concat(periods.getFinancialYearPeriods(budget, qtr).stream(), periods.getFinancialYearPeriods(budget, 2).stream()).collect(Collectors.toSet());
                period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(budget, 3).stream()).collect(Collectors.toSet());
                period = Stream.concat(period.stream(), periods.getFinancialYearPeriods(budget, 4).stream()).collect(Collectors.toSet());
                break;
            default:
                break;
        }
        acts = sampleUrc_ActivitiesService.findByDeptSectionAndBudget(departmentName, budget);
        totalActualExpenditure = getTotalActualsForActivities(acts, period);
        // Recalculate key values
        cumRealiseSpent = SALFLDGService.getTotalAmountByPeriods2(period, departmentName.getANL_CODE());
        totalBudget = budgetItemsService.calculateTotalDeptExpenditure2(budget, departmentName);

    }

    private void configureQuarter() {
        String qt = quarter;
        if (null != qt) {
            switch (qt) {
                case "Qtr 1":
                    qtr = 1;
                    break;
                case "Qtr 2":
                    qtr = 2;
                    break;
                case "Qtr 3":
                    qtr = 3;
                    break;
                case "Qtr 4":
                    qtr = 4;
                    break;
                default:
                    break;
            }
        }

    }

    public BigDecimal calculateTotalActivityActuals(Urc_Activities activity) {
        if (activity.getQuarterlyActuals() == null || activity.getQuarterlyActuals().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return activity.getQuarterlyActuals().stream()
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalActivityActuals(Urc_Activities activity, Set<Integer> periods) {
        if (activity.getQuarterlyActuals() == null || activity.getQuarterlyActuals().isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (periods == null || periods.isEmpty()) {
            // No period filter provided — return total of all periods
            return activity.getQuarterlyActuals().stream()
                    .map(QuarterlyActuals::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return activity.getQuarterlyActuals().stream()
                .filter(q -> q.getPeriod() != null && periods.contains(q.getPeriod())) // filter by allowed periods
                .map(QuarterlyActuals::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalActualsForActivities(List<Urc_Activities> activities, Set<Integer> periods) {
        if (activities == null || activities.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return activities.stream()
                .map(a -> calculateTotalActivityActuals(a, periods))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
