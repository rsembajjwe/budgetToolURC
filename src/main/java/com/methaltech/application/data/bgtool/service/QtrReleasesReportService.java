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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Text;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.QtrReleases;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.views.budgetReport.BudgetReportsView.QtrReleaseRow;

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

    public QtrReleasesReportService(UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, QtrReleasesService qtrReleasesService) {
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.qtrReleasesService = qtrReleasesService;
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
                    .setFontSize(14));

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

    private List<QtrReleaseRow> loadRows(Organisation organisation, Budget budget) {
        List<UrcDeptSectionAnlDimbgt> sections = sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims();

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

    public byte[] buildPdfItext7ByFundSourceByBudget(Budget budget, Set<Organisation> org) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<QtrReleaseRow> rows = null;

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            for (Organisation organ : org) {
                document.add(new Paragraph(organ.getName() + " Quarter Releases Report " + budget.getFinancialYear())
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
                rows = loadRows(organ, budget);

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

                table.addCell(cellLeft("TOTAL").setFont(boldFont));
                table.addCell(cellRight(fmt(q1Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q2Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q3Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(q4Sum)).setFont(boldFont));
                table.addCell(cellRight(fmt(grandTotal)).setFont(boldFont));
                document.add(table).setFontSize(10);

            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private void addHeader(Table table, String text) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(text).setFont(boldFont))
                .setTextAlignment(TextAlignment.CENTER));
    }

    private Cell cellLeft(String text) {
        return new Cell()
                .add(new Paragraph(text == null ? "" : text))
                .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell cellRight(String text) {
        return new Cell()
                .add(new Paragraph(text == null ? "" : text))
                .setTextAlignment(TextAlignment.RIGHT);
    }
}
