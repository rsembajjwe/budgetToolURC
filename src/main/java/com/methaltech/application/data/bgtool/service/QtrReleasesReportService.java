package com.methaltech.application.data.bgtool.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.QtrReleases;
import com.methaltech.application.data.entity.bgtool.QuarterBudgetSum;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.views.budgetReport.BudgetReportsView.QtrReleaseRow;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QtrReleasesReportService {

    private static final DecimalFormat MONEY_FMT = new DecimalFormat("#,##0.00");
    PdfFont boldFont = null;
    PdfFont normalFont = null;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final QtrReleasesService qtrReleasesService;
    private final BudgetItemsService sampleBudgetItemsService;

    public QtrReleasesReportService(UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, QtrReleasesService qtrReleasesService,
            BudgetItemsService sampleBudgetItemsService) {
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.qtrReleasesService = qtrReleasesService;
        this.sampleBudgetItemsService = sampleBudgetItemsService;
    }

    private String fmt(BigDecimal v) {
        return MONEY_FMT.format(v == null ? BigDecimal.ZERO : v);
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public byte[] buildPdfItext7(List<QtrReleaseRow> rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

            document.add(new Paragraph("Quarter Releases Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(boldFont)
                    .setFontSize(12));

            document.add(new Paragraph(" "));

            // 6 columns: Section + Q1 + Q2 + Q3 + Q4 + Total
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 14, 14, 14, 14, 14}))
                    .useAllAvailableWidth();

            // Header row
            addHeader(table, "Dept/Section");
            addHeader(table, "Q1");
            addHeader(table, "Q2");
            addHeader(table, "Q3");
            addHeader(table, "Q4");
            addHeader(table, "Total");

            BigDecimal q1Sum = BigDecimal.ZERO;
            BigDecimal q2Sum = BigDecimal.ZERO;
            BigDecimal q3Sum = BigDecimal.ZERO;
            BigDecimal q4Sum = BigDecimal.ZERO;

            for (QtrReleaseRow r : rows) {
                BigDecimal q1 = nvl(r.getQtr1Release());
                BigDecimal q2 = nvl(r.getQtr2Release());
                BigDecimal q3 = nvl(r.getQtr3Release());
                BigDecimal q4 = nvl(r.getQtr4Release());
                BigDecimal total = q1.add(q2).add(q3).add(q4);

                q1Sum = q1Sum.add(q1);
                q2Sum = q2Sum.add(q2);
                q3Sum = q3Sum.add(q3);
                q4Sum = q4Sum.add(q4);

                table.addCell(cellLeft(r.getDeptSectionName()));
                table.addCell(cellRight(fmt(q1)));
                table.addCell(cellRight(fmt(q2)));
                table.addCell(cellRight(fmt(q3)));
                table.addCell(cellRight(fmt(q4)));
                table.addCell(cellRight(fmt(total)).setFont(boldFont));
            }

            // Totals row
            BigDecimal grandTotal = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);

            table.addCell(cellLeft("TOTAL").setFont(boldFont));
            table.addCell(cellRight(fmt(q1Sum)).setFont(boldFont));
            table.addCell(cellRight(fmt(q2Sum)).setFont(boldFont));
            table.addCell(cellRight(fmt(q3Sum)).setFont(boldFont));
            table.addCell(cellRight(fmt(q4Sum)).setFont(boldFont));
            table.addCell(cellRight(fmt(grandTotal)).setFont(boldFont));

            document.add(table);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private List<QtrReleaseRow> loadRows(Organisation organisation, Budget budget, User user) {
        List<UrcDeptSectionAnlDimbgt> sections = new ArrayList();
        if (user.getRoles().contains(Role.ADMIN)) {
            sections = sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims();
        } else {
            sections = user.getDeptsection().stream().toList();
        }

        // Load existing QtrReleases for this organisation+budget (for all deptSections)
        List<QtrReleases> existing = qtrReleasesService.findByOrganisationAndBudget(organisation, budget);

        Map<String, QtrReleases> bySectionId = existing.stream()
                .filter(x -> x.getDeptSection() != null && x.getDeptSection().getANL_CODE() != null)
                .collect(Collectors.toMap(
                        x -> x.getDeptSection().getANL_CODE(),
                        x -> x,
                        (a, b) -> a // keep first if duplicates exist
                ));

        List<QtrReleaseRow> rows = new ArrayList<>();

        for (UrcDeptSectionAnlDimbgt section : sections) {
            QtrReleases qtr = bySectionId.get(section.getANL_CODE());

            QtrReleaseRow row = new QtrReleaseRow(section);

            if (qtr != null) {
                row.setId(qtr.getId());
                row.setQtr1Release(nvl(qtr.getQtr1Release()));
                row.setQtr2Release(nvl(qtr.getQtr2Release()));
                row.setQtr3Release(nvl(qtr.getQtr3Release()));
                row.setQtr4Release(nvl(qtr.getQtr4Release()));
            }

            rows.add(row);
        }

        return rows;
    }

    public byte[] buildPdfItext7ByFundSourceByBudget(Budget budget, Set<Organisation> org, User user) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<QtrReleaseRow> rows = null;

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            // store organisation totals for final summary
            class OrgTotal {

                String name;
                BigDecimal q1, q2, q3, q4, total;

                OrgTotal(String name, BigDecimal q1, BigDecimal q2, BigDecimal q3, BigDecimal q4) {
                    this.name = name;
                    this.q1 = q1;
                    this.q2 = q2;
                    this.q3 = q3;
                    this.q4 = q4;
                    this.total = q1.add(q2).add(q3).add(q4);
                }
            }

            List<OrgTotal> orgTotals = new ArrayList<>();

            // grand totals across all organisations
            BigDecimal allQ1 = BigDecimal.ZERO;
            BigDecimal allQ2 = BigDecimal.ZERO;
            BigDecimal allQ3 = BigDecimal.ZERO;
            BigDecimal allQ4 = BigDecimal.ZERO;
            for (Organisation organ : org) {
                document.add(new Paragraph((organ.getName() + " Quarter Releases Report " + budget.getFinancialYear()).toUpperCase())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(boldFont)
                        .setFontSize(12));

                document.add(new Paragraph(" "));
                // 6 columns: Section + Q1 + Q2 + Q3 + Q4 + Total
                Table table = new Table(UnitValue.createPercentArray(new float[]{30, 14, 14, 14, 14, 14}))
                        .useAllAvailableWidth();

                // Header row
                addHeader(table, "SECTION");
                addHeader(table, "Q1");
                addHeader(table, "Q2");
                addHeader(table, "Q3");
                addHeader(table, "Q4");
                addHeader(table, "TOTAL");

                BigDecimal q1Sum = BigDecimal.ZERO;
                BigDecimal q2Sum = BigDecimal.ZERO;
                BigDecimal q3Sum = BigDecimal.ZERO;
                BigDecimal q4Sum = BigDecimal.ZERO;
                rows = loadRows(organ, budget, user);

                for (QtrReleaseRow r : rows) {
                    BigDecimal q1 = nvl(r.getQtr1Release());
                    BigDecimal q2 = nvl(r.getQtr2Release());
                    BigDecimal q3 = nvl(r.getQtr3Release());
                    BigDecimal q4 = nvl(r.getQtr4Release());
                    BigDecimal total = q1.add(q2).add(q3).add(q4);

                    q1Sum = q1Sum.add(q1);
                    q2Sum = q2Sum.add(q2);
                    q3Sum = q3Sum.add(q3);
                    q4Sum = q4Sum.add(q4);

                    table.addCell(cellLeft(r.getDeptSectionName()));
                    table.addCell(cellRight(fmt(q1)));
                    table.addCell(cellRight(fmt(q2)));
                    table.addCell(cellRight(fmt(q3)));
                    table.addCell(cellRight(fmt(q4)));
                    table.addCell(cellRight(fmt(total)).setFont(boldFont));

                    // Totals row
                }
                BigDecimal grandTotal = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);

                table.addCell(cellLeft("TOTAL " + organ.getName().toUpperCase()).setFont(boldFont));
                table.addCell(cellRight(fmt(q1Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q2Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q3Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q4Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(grandTotal)).setFont(boldFont));
                document.add(table).setFontSize(10);

                // collect org totals for final summary
                orgTotals.add(new OrgTotal(organ.getName(), q1Sum, q2Sum, q3Sum, q4Sum));

                // add into overall totals
                allQ1 = allQ1.add(q1Sum);
                allQ2 = allQ2.add(q2Sum);
                allQ3 = allQ3.add(q3Sum);
                allQ4 = allQ4.add(q4Sum);

                document.add(new Paragraph(" "));

            }
            // ================================
            // FINAL SUMMARY BY ORGANISATION
            // ================================
            document.add(new Paragraph("SUMMARY BY ORGANISATION - " + budget.getFinancialYear())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(boldFont)
                    .setFontSize(12));

            document.add(new Paragraph(" "));

            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{30, 14, 14, 14, 14, 14}))
                    .useAllAvailableWidth();
            summaryTable.setFontSize(10);

            addHeader(summaryTable, "Fund Source");
            addHeader(summaryTable, "Q1");
            addHeader(summaryTable, "Q2");
            addHeader(summaryTable, "Q3");
            addHeader(summaryTable, "Q4");
            addHeader(summaryTable, "Total");

            for (OrgTotal t : orgTotals) {
                summaryTable.addCell(cellLeft(t.name));
                summaryTable.addCell(cellRight(fmt(t.q1)));
                summaryTable.addCell(cellRight(fmt(t.q2)));
                summaryTable.addCell(cellRight(fmt(t.q3)));
                summaryTable.addCell(cellRight(fmt(t.q4)));
                summaryTable.addCell(cellRight(fmt(t.total)).setFont(boldFont));
            }

            BigDecimal allGrand = allQ1.add(allQ2).add(allQ3).add(allQ4);

            summaryTable.addCell(cellLeft("GRAND TOTAL").setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ1)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ2)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ3)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ4)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allGrand)).setFont(boldFont));

            document.add(summaryTable);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    public byte[] buildPdfItext7ByFundSourceByBudgetwithBgt(Budget budget, Set<Organisation> org, User user) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<QtrReleaseRow> rows = null;

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            // store organisation totals for final summary
            class OrgTotal {

                String name;
                BigDecimal q1, q2, q3, q4, total, q1Bgt, q2Bgt, q3Bgt, q4Bgt, totalBgt, variance;

                OrgTotal(String name, BigDecimal q1Bgt, BigDecimal q2Bgt, BigDecimal q3Bgt, BigDecimal q4Bgt,
                        BigDecimal q1, BigDecimal q2, BigDecimal q3, BigDecimal q4) {
                    this.name = name;
                    this.q1 = q1;
                    this.q2 = q2;
                    this.q3 = q3;
                    this.q4 = q4;
                    this.q1Bgt = q1Bgt;
                    this.q2Bgt = q2Bgt;
                    this.q3Bgt = q3Bgt;
                    this.q4Bgt = q4Bgt;

                    this.total = q1.add(q2).add(q3).add(q4);
                    this.totalBgt = q1Bgt.add(q2Bgt).add(q3Bgt).add(q4Bgt);
                    this.variance = total.subtract(totalBgt);
                }
            }

            List<OrgTotal> orgTotals = new ArrayList<>();

            // grand totals across all organisations
            BigDecimal allQ1 = BigDecimal.ZERO;
            BigDecimal allQ2 = BigDecimal.ZERO;
            BigDecimal allQ3 = BigDecimal.ZERO;
            BigDecimal allQ4 = BigDecimal.ZERO;

            BigDecimal allQ1Bgt = BigDecimal.ZERO;
            BigDecimal allQ2Bgt = BigDecimal.ZERO;
            BigDecimal allQ3Bgt = BigDecimal.ZERO;
            BigDecimal allQ4Bgt = BigDecimal.ZERO;
            for (Organisation organ : org) {
                document.add(new Paragraph((organ.getName() + " Quarterly Performance Financial Report " + budget.getFinancialYear()).toUpperCase())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFont(boldFont)
                        .setFontSize(10));

                document.add(new Paragraph(" "));
                // 6 columns: Section + Q1 + Q2 + Q3 + Q4 + Total
                Table table = new Table(UnitValue.createPercentArray(new float[]{18, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 7}))
                        .useAllAvailableWidth();

                // Header row
                addHeader(table, "SECTION");
                addHeader(table, "Q1 BUDGET");
                addHeader(table, "Q2 BUDGET");
                addHeader(table, "Q3 BUDGET");
                addHeader(table, "Q4 BUDGET");

                addHeader(table, "Q1 RELEASE");
                addHeader(table, "Q2 RELEASE");
                addHeader(table, "Q3 RELEASE");
                addHeader(table, "Q4 RELEASE");
                addHeader(table, "TOTAL BUDGET");
                addHeader(table, "TOTAL RELEASE");

                addHeader(table, "VARIANCE");

                BigDecimal q1Sum = BigDecimal.ZERO;
                BigDecimal q2Sum = BigDecimal.ZERO;
                BigDecimal q3Sum = BigDecimal.ZERO;
                BigDecimal q4Sum = BigDecimal.ZERO;

                BigDecimal q1BudSum = BigDecimal.ZERO;
                BigDecimal q2BudSum = BigDecimal.ZERO;
                BigDecimal q3BudSum = BigDecimal.ZERO;
                BigDecimal q4BudSum = BigDecimal.ZERO;
                rows = loadRows(organ, budget, user);
                for (QtrReleaseRow row : rows) {
                    fillQuarterBudgets(row, organ, budget); // sets q1Budget..q4Budget on each row
                }

                for (QtrReleaseRow r : rows) {
                    BigDecimal q1 = nvl(r.getQtr1Release());
                    BigDecimal q2 = nvl(r.getQtr2Release());
                    BigDecimal q3 = nvl(r.getQtr3Release());
                    BigDecimal q4 = nvl(r.getQtr4Release());
                    BigDecimal total = q1.add(q2).add(q3).add(q4);

                    BigDecimal q1Bgt = nvl(r.getQ1Budget());
                    BigDecimal q2Bgt = nvl(r.getQ2Budget());
                    BigDecimal q3Bgt = nvl(r.getQ3Budget());
                    BigDecimal q4Bgt = nvl(r.getQ4Budget());
                    BigDecimal totalBgt = q1Bgt.add(q2Bgt).add(q3Bgt).add(q4Bgt);

                    q1Sum = q1Sum.add(q1);
                    q2Sum = q2Sum.add(q2);
                    q3Sum = q3Sum.add(q3);
                    q4Sum = q4Sum.add(q4);

                    q1BudSum = q1BudSum.add(nvl(r.getQ1Budget()));
                    q2BudSum = q2BudSum.add(nvl(r.getQ2Budget()));
                    q3BudSum = q3BudSum.add(nvl(r.getQ3Budget()));
                    q4BudSum = q4BudSum.add(nvl(r.getQ4Budget()));

                    table.addCell(cellLeft(r.getDeptSectionName()));
                    table.addCell(cellRight(fmt(q1Bgt)));
                    table.addCell(cellRight(fmt(q2Bgt)));
                    table.addCell(cellRight(fmt(q3Bgt)));
                    table.addCell(cellRight(fmt(q4Bgt)));
                    table.addCell(cellRight(fmt(totalBgt)).setFont(boldFont));

                    table.addCell(cellRight(fmt(q1)));
                    table.addCell(cellRight(fmt(q2)));
                    table.addCell(cellRight(fmt(q3)));
                    table.addCell(cellRight(fmt(q4)));
                    table.addCell(cellRight(fmt(total)).setFont(boldFont));
                    table.addCell(cellRight(fmt(total.subtract(totalBgt))).setFont(boldFont));

                    // Totals row
                }
                BigDecimal grandTotal = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);
                BigDecimal grandTotalBgt = q1BudSum.add(q2BudSum).add(q3BudSum).add(q4BudSum);

                table.addCell(cellLeft("TOTAL " + organ.getName().toUpperCase()).setFont(boldFont));
                table.addCell(cellRight(fmt(q1BudSum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q1BudSum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q1BudSum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q1BudSum)).setFont(boldFont));

                table.addCell(cellRight(fmt(q1Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q2Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q3Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q4Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(grandTotalBgt)).setFont(boldFont));
                table.addCell(cellRight(fmt(grandTotal)).setFont(boldFont));

                table.addCell(cellRight(fmt(grandTotal.subtract(grandTotalBgt))).setFont(boldFont));
                document.add(table).setFontSize(8);

                // collect org totals for final summary
                orgTotals.add(new OrgTotal(organ.getName(), q1BudSum, q2BudSum, q3BudSum, q4BudSum, q1Sum, q2Sum, q3Sum, q4Sum));

                // add into overall totals
                allQ1 = allQ1.add(q1Sum);
                allQ2 = allQ2.add(q2Sum);
                allQ3 = allQ3.add(q3Sum);
                allQ4 = allQ4.add(q4Sum);

                allQ1Bgt = allQ1Bgt.add(q1BudSum);
                allQ2Bgt = allQ2Bgt.add(q2BudSum);
                allQ3Bgt = allQ3Bgt.add(q3BudSum);
                allQ4Bgt = allQ4Bgt.add(q4BudSum);

                document.add(new Paragraph(" "));

            }
            // ================================
            // FINAL SUMMARY BY ORGANISATION
            // ================================
            document.add(new Paragraph("SUMMARY BY ORGANISATION - " + budget.getFinancialYear())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(boldFont)
                    .setFontSize(10));

            document.add(new Paragraph(" "));

            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{18, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 7}))
                    .useAllAvailableWidth();
            summaryTable.setFontSize(10);

            addHeader(summaryTable, "FUND SOURCE");

            addHeader(summaryTable, "Q1 BUDGET");
            addHeader(summaryTable, "Q2 BUDGET");
            addHeader(summaryTable, "Q3 BUDGET");
            addHeader(summaryTable, "Q4 BUDGET");

            addHeader(summaryTable, "Q1 RELEASE");
            addHeader(summaryTable, "Q2 RELEASE");
            addHeader(summaryTable, "Q3 RELEASE");
            addHeader(summaryTable, "Q4 RELEASE");
            addHeader(summaryTable, "TOTAL BUDGET");
            addHeader(summaryTable, "TOTAL RELEASE");

            addHeader(summaryTable, "VARIANCE");

            for (OrgTotal t : orgTotals) {
                summaryTable.addCell(cellLeft(t.name));
                summaryTable.addCell(cellRight(fmt(t.q1Bgt)));
                summaryTable.addCell(cellRight(fmt(t.q2Bgt)));
                summaryTable.addCell(cellRight(fmt(t.q3Bgt)));
                summaryTable.addCell(cellRight(fmt(t.q4Bgt)));

                summaryTable.addCell(cellRight(fmt(t.q1)));
                summaryTable.addCell(cellRight(fmt(t.q2)));
                summaryTable.addCell(cellRight(fmt(t.q3)));
                summaryTable.addCell(cellRight(fmt(t.q4)));
                summaryTable.addCell(cellRight(fmt(t.totalBgt)).setFont(boldFont));
                summaryTable.addCell(cellRight(fmt(t.total)).setFont(boldFont));

                summaryTable.addCell(cellRight(fmt(t.variance)).setFont(boldFont));
            }

            BigDecimal allGrand = allQ1.add(allQ2).add(allQ3).add(allQ4);
            BigDecimal allGrandBgt = allQ1Bgt.add(allQ2Bgt).add(allQ3Bgt).add(allQ4Bgt);

            summaryTable.addCell(cellLeft("GRAND TOTAL").setFont(boldFont));

            summaryTable.addCell(cellRight(fmt(allQ1Bgt)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ2Bgt)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ3Bgt)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ4Bgt)).setFont(boldFont));

            summaryTable.addCell(cellRight(fmt(allQ1)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ2)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ3)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allQ4)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allGrandBgt)).setFont(boldFont));
            summaryTable.addCell(cellRight(fmt(allGrand)).setFont(boldFont));

            summaryTable.addCell(cellRight(fmt(allGrand.subtract(allGrandBgt))).setFont(boldFont));

            document.add(summaryTable);
            document.setMargins(5, 5, 5, 5);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    public byte[] buildExcelByFundSourceByBudgetwithBgt(Budget budget, Set<Organisation> orgs, User user) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // -------------------------
            // Styles
            // -------------------------
            DataFormat df = wb.createDataFormat();

            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 12);

            Font headerFont = wb.createFont();
            headerFont.setBold(true);

            Font boldFont = wb.createFont();
            boldFont.setBold(true);

            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(headerStyle);

            CellStyle textLeftStyle = wb.createCellStyle();
            textLeftStyle.setAlignment(HorizontalAlignment.LEFT);
            textLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(textLeftStyle);

            CellStyle numberStyle = wb.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            numberStyle.setDataFormat(df.getFormat("#,##0.00"));
            setBorders(numberStyle);

            CellStyle numberBoldStyle = wb.createCellStyle();
            numberBoldStyle.cloneStyleFrom(numberStyle);
            numberBoldStyle.setFont(boldFont);

            CellStyle totalRowStyle = wb.createCellStyle();
            totalRowStyle.cloneStyleFrom(textLeftStyle);
            totalRowStyle.setFont(boldFont);
            totalRowStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            totalRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle totalNumberStyle = wb.createCellStyle();
            totalNumberStyle.cloneStyleFrom(numberBoldStyle);
            totalNumberStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            totalNumberStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle summaryTitleStyle = wb.createCellStyle();
            summaryTitleStyle.cloneStyleFrom(titleStyle);
            summaryTitleStyle.setFont(titleFont);

            // -------------------------
            // Helper class to store org totals
            // -------------------------
            class OrgTotal {

                String name;
                BigDecimal q1, q2, q3, q4, total, q1Bgt, q2Bgt, q3Bgt, q4Bgt, totalBgt, variance;

                OrgTotal(String name, BigDecimal q1Bgt, BigDecimal q2Bgt, BigDecimal q3Bgt, BigDecimal q4Bgt,
                        BigDecimal q1, BigDecimal q2, BigDecimal q3, BigDecimal q4) {
                    this.name = name;
                    this.q1 = q1;
                    this.q2 = q2;
                    this.q3 = q3;
                    this.q4 = q4;
                    this.q1Bgt = q1Bgt;
                    this.q2Bgt = q2Bgt;
                    this.q3Bgt = q3Bgt;
                    this.q4Bgt = q4Bgt;

                    this.total = q1.add(q2).add(q3).add(q4);
                    this.totalBgt = q1Bgt.add(q2Bgt).add(q3Bgt).add(q4Bgt);
                    this.variance = total.subtract(totalBgt);
                }
            }

            List<OrgTotal> orgTotals = new ArrayList<>();

            BigDecimal allQ1 = BigDecimal.ZERO;
            BigDecimal allQ2 = BigDecimal.ZERO;
            BigDecimal allQ3 = BigDecimal.ZERO;
            BigDecimal allQ4 = BigDecimal.ZERO;

            BigDecimal allQ1Bgt = BigDecimal.ZERO;
            BigDecimal allQ2Bgt = BigDecimal.ZERO;
            BigDecimal allQ3Bgt = BigDecimal.ZERO;
            BigDecimal allQ4Bgt = BigDecimal.ZERO;

            

            // -------------------------
            // Organisation Sections
            // -------------------------
            for (Organisation organ : orgs) {
                int rowIdx = 0;
                Sheet sheet = wb.createSheet(organ.getName());
                // Column widths (nice readable layout)
// 12 columns: 0..11
                sheet.setColumnWidth(0, 35 * 256); // SECTION / FUND SOURCE (wide)

                sheet.setColumnWidth(1, 14 * 256); // Q1 Budget
                sheet.setColumnWidth(2, 14 * 256); // Q2 Budget
                sheet.setColumnWidth(3, 14 * 256); // Q3 Budget
                sheet.setColumnWidth(4, 14 * 256); // Q4 Budget
                sheet.setColumnWidth(5, 14 * 256); // TOTAL Budget (slightly wider)

                sheet.setColumnWidth(6, 14 * 256); // Q1 Release
                sheet.setColumnWidth(7, 14 * 256); // Q2 Release
                sheet.setColumnWidth(8, 14 * 256); // Q3 Release
                sheet.setColumnWidth(9, 16 * 256); // Q4 Release
                sheet.setColumnWidth(10, 16 * 256); // TOTAL Release (slightly wider)

                sheet.setColumnWidth(11, 16 * 256); // VARIANCE                

                // Title row (merged across 6 columns)
                Row titleRow = sheet.createRow(rowIdx++);
                org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue((organ.getName() + " Quarter Releases Report " + budget.getFinancialYear()).toUpperCase());
                titleCell.setCellStyle(titleStyle);

                sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, 11));

                rowIdx++; // blank row

                // Header row
                Row headerRow = sheet.createRow(rowIdx++);
                createCell(headerRow, 0, "SECTION", headerStyle);
                createCell(headerRow, 1, "Q1 BUDGET", headerStyle);
                createCell(headerRow, 2, "Q2 BUDGET", headerStyle);
                createCell(headerRow, 3, "Q3 BUDGET", headerStyle);
                createCell(headerRow, 4, "Q4 BUDGET", headerStyle);

                createCell(headerRow, 5, "Q1 RELEASE", headerStyle);
                createCell(headerRow, 6, "Q2 RELEASE", headerStyle);
                createCell(headerRow, 7, "Q3 RELEASE", headerStyle);
                createCell(headerRow, 8, "Q4 RELEASE", headerStyle);
                createCell(headerRow, 9, "TOTAL BUDGET", headerStyle);
                createCell(headerRow, 10, "TOTAL RELEASE", headerStyle);
                createCell(headerRow, 11, "VARIANCE", headerStyle);

                BigDecimal q1Sum = BigDecimal.ZERO;
                BigDecimal q2Sum = BigDecimal.ZERO;
                BigDecimal q3Sum = BigDecimal.ZERO;
                BigDecimal q4Sum = BigDecimal.ZERO;

                BigDecimal q1BudSum = BigDecimal.ZERO;
                BigDecimal q2BudSum = BigDecimal.ZERO;
                BigDecimal q3BudSum = BigDecimal.ZERO;
                BigDecimal q4BudSum = BigDecimal.ZERO;
                List<QtrReleaseRow> rows = loadRows(organ, budget, user);
                for (QtrReleaseRow row : rows) {
                    fillQuarterBudgets(row, organ, budget); // sets q1Budget..q4Budget on each row
                }

                for (QtrReleaseRow r : rows) {
                    BigDecimal q1 = nvl(r.getQtr1Release());
                    BigDecimal q2 = nvl(r.getQtr2Release());
                    BigDecimal q3 = nvl(r.getQtr3Release());
                    BigDecimal q4 = nvl(r.getQtr4Release());
                    BigDecimal total = q1.add(q2).add(q3).add(q4);

                    BigDecimal q1Bgt = nvl(r.getQ1Budget());
                    BigDecimal q2Bgt = nvl(r.getQ2Budget());
                    BigDecimal q3Bgt = nvl(r.getQ3Budget());
                    BigDecimal q4Bgt = nvl(r.getQ4Budget());
                    BigDecimal totalBgt = q1Bgt.add(q2Bgt).add(q3Bgt).add(q4Bgt);

                    q1Sum = q1Sum.add(q1);
                    q2Sum = q2Sum.add(q2);
                    q3Sum = q3Sum.add(q3);
                    q4Sum = q4Sum.add(q4);

                    q1BudSum = q1BudSum.add(nvl(r.getQ1Budget()));
                    q2BudSum = q2BudSum.add(nvl(r.getQ2Budget()));
                    q3BudSum = q3BudSum.add(nvl(r.getQ3Budget()));
                    q4BudSum = q4BudSum.add(nvl(r.getQ4Budget()));

                    Row dataRow = sheet.createRow(rowIdx++);
                    createCell(dataRow, 0, safe(r.getDeptSectionName()), textLeftStyle);
                    createNumberCell(dataRow, 1, q1Bgt, numberStyle);
                    createNumberCell(dataRow, 2, q2Bgt, numberStyle);
                    createNumberCell(dataRow, 3, q3Bgt, numberStyle);
                    createNumberCell(dataRow, 4, q4Bgt, numberStyle);

                    createNumberCell(dataRow, 5, q1, numberStyle);
                    createNumberCell(dataRow, 6, q2, numberStyle);
                    createNumberCell(dataRow, 7, q3, numberStyle);
                    createNumberCell(dataRow, 8, q4, numberStyle);
                    createNumberCell(dataRow, 9, totalBgt, numberBoldStyle);
                    createNumberCell(dataRow, 10, total, numberBoldStyle);
                    createNumberCell(dataRow, 11, total.subtract(totalBgt), numberBoldStyle);
                }
                BigDecimal grandTotal = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);
                BigDecimal grandTotalBgt = q1BudSum.add(q2BudSum).add(q3BudSum).add(q4BudSum);
                // BigDecimal orgGrand = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);

                // TOTAL row for org
                Row totalRow = sheet.createRow(rowIdx++);
                createCell(totalRow, 0, "TOTAL " + organ.getName().toUpperCase(), totalRowStyle);
                createNumberCell(totalRow, 1, q1BudSum, totalNumberStyle);
                createNumberCell(totalRow, 2, q1BudSum, totalNumberStyle);
                createNumberCell(totalRow, 3, q1BudSum, totalNumberStyle);
                createNumberCell(totalRow, 4, q1BudSum, totalNumberStyle);
                createNumberCell(totalRow, 5, q1Sum, totalNumberStyle);
                createNumberCell(totalRow, 6, q2Sum, totalNumberStyle);
                createNumberCell(totalRow, 7, q3Sum, totalNumberStyle);
                createNumberCell(totalRow, 8, q4Sum, totalNumberStyle);
                createNumberCell(totalRow, 9, grandTotalBgt, totalNumberStyle);
                createNumberCell(totalRow, 10, grandTotal, totalNumberStyle);
                createNumberCell(totalRow, 11, grandTotal.subtract(grandTotalBgt), totalNumberStyle);

                rowIdx += 2; // spacing after each organisation

                // store for summary
                orgTotals.add(new OrgTotal(organ.getName(), q1BudSum, q2BudSum, q3BudSum, q4BudSum, q1Sum, q2Sum, q3Sum, q4Sum));

                // add into overall totals
                allQ1 = allQ1.add(q1Sum);
                allQ2 = allQ2.add(q2Sum);
                allQ3 = allQ3.add(q3Sum);
                allQ4 = allQ4.add(q4Sum);

                allQ1Bgt = allQ1Bgt.add(q1BudSum);
                allQ2Bgt = allQ2Bgt.add(q2BudSum);
                allQ3Bgt = allQ3Bgt.add(q3BudSum);
                allQ4Bgt = allQ4Bgt.add(q4BudSum);

            }

            // -------------------------
            // SUMMARY BY ORGANISATION
            // -------------------------
            Sheet sheet2 = wb.createSheet("SUMMARY");
            int rowIdx = 0;
            // Column widths (nice readable layout)
// 12 columns: 0..11
            sheet2.setColumnWidth(0, 35 * 256); // SECTION / FUND SOURCE (wide)

            sheet2.setColumnWidth(1, 14 * 256); // Q1 Budget
            sheet2.setColumnWidth(2, 14 * 256); // Q2 Budget
            sheet2.setColumnWidth(3, 14 * 256); // Q3 Budget
            sheet2.setColumnWidth(4, 14 * 256); // Q4 Budget
            sheet2.setColumnWidth(5, 16 * 256); // TOTAL Budget (slightly wider)

            sheet2.setColumnWidth(6, 14 * 256); // Q1 Release
            sheet2.setColumnWidth(7, 14 * 256); // Q2 Release
            sheet2.setColumnWidth(8, 14 * 256); // Q3 Release
            sheet2.setColumnWidth(9, 14 * 256); // Q4 Release
            sheet2.setColumnWidth(10, 16 * 256); // TOTAL Release (slightly wider)

            sheet2.setColumnWidth(11, 16 * 256); // VARIANCE              
            Row summaryTitleRow = sheet2.createRow(rowIdx++);
            org.apache.poi.ss.usermodel.Cell summaryTitleCell = summaryTitleRow.createCell(0);
            summaryTitleCell.setCellValue(("SUMMARY BY ORGANISATION - " + budget.getFinancialYear()).toUpperCase());
            summaryTitleCell.setCellStyle(summaryTitleStyle);
            sheet2.addMergedRegion(new CellRangeAddress(summaryTitleRow.getRowNum(), summaryTitleRow.getRowNum(), 0, 11));

            rowIdx++;

            Row summaryHeaderRow = sheet2.createRow(rowIdx++);
            createCell(summaryHeaderRow, 0, "FUND SOURCE", headerStyle);
            createCell(summaryHeaderRow, 1, "Q1 BUDGET", headerStyle);
            createCell(summaryHeaderRow, 2, "Q2 BUDGET", headerStyle);
            createCell(summaryHeaderRow, 3, "Q3 BUDGET", headerStyle);
            createCell(summaryHeaderRow, 4, "Q4 BUDGET", headerStyle);
            createCell(summaryHeaderRow, 5, "Q1 RELEASE", headerStyle);
            createCell(summaryHeaderRow, 6, "Q2 RELEASE", headerStyle);
            createCell(summaryHeaderRow, 7, "Q3 RELEASE", headerStyle);
            createCell(summaryHeaderRow, 8, "Q4 RELEASE", headerStyle);
            createCell(summaryHeaderRow, 9, "TOTAL BUDGET", headerStyle);
            createCell(summaryHeaderRow, 10, "TOTAL RELEASE", headerStyle);
            createCell(summaryHeaderRow, 11, "VARIANCE", headerStyle);

            for (OrgTotal t : orgTotals) {
                Row r = sheet2.createRow(rowIdx++);
                createCell(r, 0, safe(t.name), textLeftStyle);
                createNumberCell(r, 1, t.q1Bgt, numberStyle);
                createNumberCell(r, 2, t.q2Bgt, numberStyle);
                createNumberCell(r, 3, t.q3Bgt, numberStyle);
                createNumberCell(r, 4, t.q4Bgt, numberStyle);

                createNumberCell(r, 5, t.q1, numberStyle);
                createNumberCell(r, 6, t.q2, numberStyle);
                createNumberCell(r, 7, t.q3, numberStyle);
                createNumberCell(r, 8, t.q4, numberStyle);
                createNumberCell(r, 9, t.totalBgt, numberBoldStyle);
                createNumberCell(r, 10, t.total, numberBoldStyle);
                createNumberCell(r, 11, t.variance, numberBoldStyle);
            }

            BigDecimal allGrand = allQ1.add(allQ2).add(allQ3).add(allQ4);
            BigDecimal allGrandBgt = allQ1Bgt.add(allQ2Bgt).add(allQ3Bgt).add(allQ4Bgt);

            Row grandRow = sheet2.createRow(rowIdx++);
            createCell(grandRow, 0, "GRAND TOTAL", totalRowStyle);
            createNumberCell(grandRow, 1, allQ1Bgt, totalNumberStyle);
            createNumberCell(grandRow, 2, allQ2Bgt, totalNumberStyle);
            createNumberCell(grandRow, 3, allQ3Bgt, totalNumberStyle);
            createNumberCell(grandRow, 4, allQ4Bgt, totalNumberStyle);            
            createNumberCell(grandRow, 5, allQ1, totalNumberStyle);
            createNumberCell(grandRow, 6, allQ2, totalNumberStyle);
            createNumberCell(grandRow, 7, allQ3, totalNumberStyle);
            createNumberCell(grandRow, 8, allQ4, totalNumberStyle);
            createNumberCell(grandRow, 9, allGrandBgt, totalNumberStyle);
            createNumberCell(grandRow, 10, allGrand, totalNumberStyle);
            createNumberCell(grandRow, 11, allGrand.subtract(allGrandBgt), totalNumberStyle);

            // Freeze header area (optional, nice UX)
            sheet2.createFreezePane(0, 0);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed: " + e.getMessage(), e);
        }
    }

    public byte[] buildExcelByFundSourceByBudget(Budget budget, Set<Organisation> orgs, User user) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Quarterly Releases");

            // -------------------------
            // Styles
            // -------------------------
            DataFormat df = wb.createDataFormat();

            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 12);

            Font headerFont = wb.createFont();
            headerFont.setBold(true);

            Font boldFont = wb.createFont();
            boldFont.setBold(true);

            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(headerStyle);

            CellStyle textLeftStyle = wb.createCellStyle();
            textLeftStyle.setAlignment(HorizontalAlignment.LEFT);
            textLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(textLeftStyle);

            CellStyle numberStyle = wb.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            numberStyle.setDataFormat(df.getFormat("#,##0.00"));
            setBorders(numberStyle);

            CellStyle numberBoldStyle = wb.createCellStyle();
            numberBoldStyle.cloneStyleFrom(numberStyle);
            numberBoldStyle.setFont(boldFont);

            CellStyle totalRowStyle = wb.createCellStyle();
            totalRowStyle.cloneStyleFrom(textLeftStyle);
            totalRowStyle.setFont(boldFont);
            totalRowStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            totalRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle totalNumberStyle = wb.createCellStyle();
            totalNumberStyle.cloneStyleFrom(numberBoldStyle);
            totalNumberStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            totalNumberStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle summaryTitleStyle = wb.createCellStyle();
            summaryTitleStyle.cloneStyleFrom(titleStyle);
            summaryTitleStyle.setFont(titleFont);

            // -------------------------
            // Helper class to store org totals
            // -------------------------
            class OrgTotal {

                String name;
                BigDecimal q1, q2, q3, q4, total;

                OrgTotal(String name, BigDecimal q1, BigDecimal q2, BigDecimal q3, BigDecimal q4) {
                    this.name = name;
                    this.q1 = q1;
                    this.q2 = q2;
                    this.q3 = q3;
                    this.q4 = q4;
                    this.total = q1.add(q2).add(q3).add(q4);
                }
            }

            List<OrgTotal> orgTotals = new ArrayList<>();

            BigDecimal allQ1 = BigDecimal.ZERO;
            BigDecimal allQ2 = BigDecimal.ZERO;
            BigDecimal allQ3 = BigDecimal.ZERO;
            BigDecimal allQ4 = BigDecimal.ZERO;

            int rowIdx = 0;

            // Column widths (nice readable layout)
            sheet.setColumnWidth(0, 35 * 256); // Section / Organisation name
            sheet.setColumnWidth(1, 15 * 256);
            sheet.setColumnWidth(2, 15 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 15 * 256);
            sheet.setColumnWidth(5, 18 * 256);

            // -------------------------
            // Organisation Sections
            // -------------------------
            for (Organisation organ : orgs) {

                // Title row (merged across 6 columns)
                Row titleRow = sheet.createRow(rowIdx++);
                org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue((organ.getName() + " Quarter Releases Report " + budget.getFinancialYear()).toUpperCase());
                titleCell.setCellStyle(titleStyle);

                sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, 5));

                rowIdx++; // blank row

                // Header row
                Row headerRow = sheet.createRow(rowIdx++);
                createCell(headerRow, 0, "SECTION", headerStyle);
                createCell(headerRow, 1, "Q1", headerStyle);
                createCell(headerRow, 2, "Q2", headerStyle);
                createCell(headerRow, 3, "Q3", headerStyle);
                createCell(headerRow, 4, "Q4", headerStyle);
                createCell(headerRow, 5, "TOTAL", headerStyle);

                BigDecimal q1Sum = BigDecimal.ZERO;
                BigDecimal q2Sum = BigDecimal.ZERO;
                BigDecimal q3Sum = BigDecimal.ZERO;
                BigDecimal q4Sum = BigDecimal.ZERO;

                List<QtrReleaseRow> rows = loadRows(organ, budget, user);

                for (QtrReleaseRow r : rows) {
                    BigDecimal q1 = nvl(r.getQtr1Release());
                    BigDecimal q2 = nvl(r.getQtr2Release());
                    BigDecimal q3 = nvl(r.getQtr3Release());
                    BigDecimal q4 = nvl(r.getQtr4Release());
                    BigDecimal total = q1.add(q2).add(q3).add(q4);

                    q1Sum = q1Sum.add(q1);
                    q2Sum = q2Sum.add(q2);
                    q3Sum = q3Sum.add(q3);
                    q4Sum = q4Sum.add(q4);

                    Row dataRow = sheet.createRow(rowIdx++);
                    createCell(dataRow, 0, safe(r.getDeptSectionName()), textLeftStyle);
                    createNumberCell(dataRow, 1, q1, numberStyle);
                    createNumberCell(dataRow, 2, q2, numberStyle);
                    createNumberCell(dataRow, 3, q3, numberStyle);
                    createNumberCell(dataRow, 4, q4, numberStyle);
                    createNumberCell(dataRow, 5, total, numberBoldStyle);
                }

                BigDecimal orgGrand = q1Sum.add(q2Sum).add(q3Sum).add(q4Sum);

                // TOTAL row for org
                Row totalRow = sheet.createRow(rowIdx++);
                createCell(totalRow, 0, "TOTAL " + organ.getName().toUpperCase(), totalRowStyle);
                createNumberCell(totalRow, 1, q1Sum, totalNumberStyle);
                createNumberCell(totalRow, 2, q2Sum, totalNumberStyle);
                createNumberCell(totalRow, 3, q3Sum, totalNumberStyle);
                createNumberCell(totalRow, 4, q4Sum, totalNumberStyle);
                createNumberCell(totalRow, 5, orgGrand, totalNumberStyle);

                rowIdx += 2; // spacing after each organisation

                // store for summary
                orgTotals.add(new OrgTotal(organ.getName(), q1Sum, q2Sum, q3Sum, q4Sum));

                // accumulate global totals
                allQ1 = allQ1.add(q1Sum);
                allQ2 = allQ2.add(q2Sum);
                allQ3 = allQ3.add(q3Sum);
                allQ4 = allQ4.add(q4Sum);
            }

            // -------------------------
            // SUMMARY BY ORGANISATION
            // -------------------------
            Row summaryTitleRow = sheet.createRow(rowIdx++);
            org.apache.poi.ss.usermodel.Cell summaryTitleCell = summaryTitleRow.createCell(0);
            summaryTitleCell.setCellValue(("SUMMARY BY ORGANISATION - " + budget.getFinancialYear()).toUpperCase());
            summaryTitleCell.setCellStyle(summaryTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(summaryTitleRow.getRowNum(), summaryTitleRow.getRowNum(), 0, 5));

            rowIdx++;

            Row summaryHeaderRow = sheet.createRow(rowIdx++);
            createCell(summaryHeaderRow, 0, "FUND SOURCE", headerStyle);
            createCell(summaryHeaderRow, 1, "Q1", headerStyle);
            createCell(summaryHeaderRow, 2, "Q2", headerStyle);
            createCell(summaryHeaderRow, 3, "Q3", headerStyle);
            createCell(summaryHeaderRow, 4, "Q4", headerStyle);
            createCell(summaryHeaderRow, 5, "TOTAL", headerStyle);

            for (OrgTotal t : orgTotals) {
                Row r = sheet.createRow(rowIdx++);
                createCell(r, 0, safe(t.name), textLeftStyle);
                createNumberCell(r, 1, t.q1, numberStyle);
                createNumberCell(r, 2, t.q2, numberStyle);
                createNumberCell(r, 3, t.q3, numberStyle);
                createNumberCell(r, 4, t.q4, numberStyle);
                createNumberCell(r, 5, t.total, numberBoldStyle);
            }

            BigDecimal allGrand = allQ1.add(allQ2).add(allQ3).add(allQ4);

            Row grandRow = sheet.createRow(rowIdx++);
            createCell(grandRow, 0, "GRAND TOTAL", totalRowStyle);
            createNumberCell(grandRow, 1, allQ1, totalNumberStyle);
            createNumberCell(grandRow, 2, allQ2, totalNumberStyle);
            createNumberCell(grandRow, 3, allQ3, totalNumberStyle);
            createNumberCell(grandRow, 4, allQ4, totalNumberStyle);
            createNumberCell(grandRow, 5, allGrand, totalNumberStyle);

            // Freeze header area (optional, nice UX)
            sheet.createFreezePane(0, 0);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed: " + e.getMessage(), e);
        }
    }

    private void addHeader(Table table, String text) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(text).setFont(boldFont))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
    }

    private Cell cellLeft(String text) {
        return new Cell()
                .add(new Paragraph(text == null ? "" : text))
                .setFont(normalFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell cellRight(String text) {
        return new Cell()
                .add(new Paragraph(text == null ? "" : text))
                .setFont(normalFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createNumberCell(Row row, int col, BigDecimal value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(value == null ? 0.0 : value.doubleValue());
        cell.setCellStyle(style);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private void fillQuarterBudgets(QtrReleaseRow row, Organisation org, Budget budget) {

        if (row == null || row.getDeptSection() == null) {
            return;
        }

        List<QuarterBudgetSum> sum
                = sampleBudgetItemsService.sumQuarterBudgetsByDept(
                        budget,
                        org,
                        row.getDeptSection()
                );
        for (QuarterBudgetSum val : sum) {
            row.setQ1Budget(val.q1());
            row.setQ2Budget(val.q2());
            row.setQ3Budget(val.q3());
            row.setQ4Budget(val.q4());
        }

    }
}
