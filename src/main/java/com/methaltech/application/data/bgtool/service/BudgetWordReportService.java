package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.service.report.BudgetReportRequest;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.stereotype.Service;

@Service
public class BudgetWordReportService {

    private final BudgetItemsService sampleBudgetItemsService;
    private final Coalevel1Service sampleCoalevel1Service;

    public BudgetWordReportService(BudgetItemsService sampleBudgetItemsService,
            Coalevel1Service sampleCoalevel1Service) {
        this.sampleBudgetItemsService = sampleBudgetItemsService;
        this.sampleCoalevel1Service = sampleCoalevel1Service;
    }

    public byte[] generateExecutiveBudgetReport(BudgetReportRequest request) {
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            configurePage(document);

            Coalevel1 income = sampleCoalevel1Service.findByCode(1);
            Coalevel1 revenueExpenditure = sampleCoalevel1Service.findByCode(2);
            Coalevel1 capitalExpenditure = sampleCoalevel1Service.findByCode(3);

            addReportHeader(document, "UGANDA RAILWAYS CORPORATION", "Executive Budget Report");
            addMetaBlock(document, request);
            addExecutiveSummary(document, request, income, revenueExpenditure, capitalExpenditure);

            addBudgetSection(document, "INCOME", income, request);
            addBudgetSection(document, "REVENUE EXPENDITURE", revenueExpenditure, request);
            addBudgetSection(document, "CAPITAL EXPENDITURE", capitalExpenditure, request);

            addGrandTotalSection(document, request, List.of(revenueExpenditure, capitalExpenditure));

            document.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate executive Word budget report", e);
        }
    }

    private void configurePage(XWPFDocument document) {
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSize = sectPr.addNewPgSz();
        pageSize.setW(java.math.BigInteger.valueOf(11906));
        pageSize.setH(java.math.BigInteger.valueOf(16838));

        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setTop(java.math.BigInteger.valueOf(900));
        pageMar.setRight(java.math.BigInteger.valueOf(700));
        pageMar.setBottom(java.math.BigInteger.valueOf(900));
        pageMar.setLeft(java.math.BigInteger.valueOf(700));
    }

    private void addReportHeader(XWPFDocument document, String organisation, String reportTitle) {
        XWPFParagraph p = document.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.setSpacingAfter(100);

        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontFamily("Times New Roman");
        run.setFontSize(16);
        run.setText(organisation);

        XWPFParagraph p2 = document.createParagraph();
        p2.setAlignment(ParagraphAlignment.CENTER);
        p2.setSpacingAfter(220);

        XWPFRun run2 = p2.createRun();
        run2.setBold(true);
        run2.setFontFamily("Times New Roman");
        run2.setFontSize(13);
        run2.setText(reportTitle);
    }

    private void addMetaBlock(XWPFDocument document, BudgetReportRequest request) {
        XWPFTable table = document.createTable(4, 2);
        table.setWidth("100%");
        table.setTableAlignment(TableRowAlign.CENTER);

        formatLabelValueRow(table.getRow(0), "Budget:", safeText(request.getBudget()));
        formatLabelValueRow(table.getRow(1), "Departments / Units:",
                request.getSelectedSections().stream()
                        .map(UrcDeptSectionAnlDimbgt::getNAME)
                        .collect(Collectors.joining(", ")));
        formatLabelValueRow(table.getRow(2), "Budget Type(s):",
                request.getSelectedBudgetTypes().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));
        formatLabelValueRow(table.getRow(3), "Report Format:", "Executive Budget Narrative");

        XWPFParagraph spacer = document.createParagraph();
        spacer.setSpacingAfter(180);
    }

    private void addExecutiveSummary(XWPFDocument document,
            BudgetReportRequest request,
            Coalevel1 income,
            Coalevel1 revenueExpenditure,
            Coalevel1 capitalExpenditure) {

        BigDecimal totalIncome = safeAmount(
                sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                        request.getBudget(), income, request.getSelectedSections(), request.getSelectedBudgetTypes()));

        BigDecimal totalRevenue = safeAmount(
                sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                        request.getBudget(), revenueExpenditure, request.getSelectedSections(), request.getSelectedBudgetTypes()));

        BigDecimal totalCapital = safeAmount(
                sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                        request.getBudget(), capitalExpenditure, request.getSelectedSections(), request.getSelectedBudgetTypes()));

        BigDecimal totalExpenditure = totalRevenue.add(totalCapital);
        BigDecimal budgetPosition = totalIncome.subtract(totalExpenditure);

        addSectionTitle(document, "Executive Summary");

        XWPFTable table = document.createTable(4, 2);
        table.setWidth("100%");

        formatSummaryRow(table.getRow(0), "Total Income", formatCurrency(totalIncome));
        formatSummaryRow(table.getRow(1), "Total Revenue Expenditure", formatCurrency(totalRevenue));
        formatSummaryRow(table.getRow(2), "Total Capital Expenditure", formatCurrency(totalCapital));
        formatSummaryRow(table.getRow(3), "Budget Position", formatCurrency(budgetPosition));

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(120);
        paragraph.setSpacingAfter(240);
        paragraph.setAlignment(ParagraphAlignment.BOTH);

        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Times New Roman");
        run.setFontSize(11);
        run.setText("This report presents a consolidated executive view of the selected budget, "
                + "summarising projected income, recurrent commitments, and capital investments for management review.");
    }

    private void addBudgetSection(XWPFDocument document,
            String sectionTitle,
            Coalevel1 coaLevel1,
            BudgetReportRequest request) {

        if (!hasData(request.getBudget(), coaLevel1, request.getSelectedSections(), request.getSelectedBudgetTypes())) {
            return;
        }

        addSectionTitle(document, sectionTitle);

        List<COA> coaItems = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(
                request.getBudget(), coaLevel1, request.getSelectedSections(), request.getSelectedBudgetTypes());

        for (COA coa : coaItems) {
            addCoaSubheading(document, coa.getCode() + " - " + coa.getName());

            BigDecimal coaTotal = safeAmount(
                    sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(
                            request.getBudget(), coa, request.getSelectedSections(), request.getSelectedBudgetTypes()));

            XWPFParagraph totalParagraph = document.createParagraph();
            totalParagraph.setSpacingAfter(80);

            XWPFRun totalRun = totalParagraph.createRun();
            totalRun.setBold(true);
            totalRun.setFontFamily("Times New Roman");
            totalRun.setFontSize(11);
            totalRun.setText("COA Total: " + formatCurrency(coaTotal));

            List<BudgetItems> items = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(
                    request.getBudget(), coa, request.getSelectedSections(), request.getSelectedBudgetTypes());

            XWPFTable table = document.createTable();
            table.setWidth("100%");
            createBudgetTableHeader(table);

            for (BudgetItems item : items) {
                QuarterValues q = calculateQuarterValues(item);

                XWPFTableRow row = table.createRow();

                setPlainCell(row.getCell(0), "", ParagraphAlignment.LEFT);

                setPlainCell(row.getCell(1),
                        safeText(item.getCoacode().getName() + " (" + item.getCoacode().getCode().trim() + ")"),
                        ParagraphAlignment.LEFT);

                setPlainCell(row.getCell(2), safeText(item.getItem()), ParagraphAlignment.LEFT);

                setPlainCell(row.getCell(3), formatCurrency(q.total()), ParagraphAlignment.RIGHT);

                setPlainCell(row.getCell(4), formatCurrency(q.q1()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(5), formatCurrency(q.q2()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(6), formatCurrency(q.q3()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(7), formatCurrency(q.q4()), ParagraphAlignment.RIGHT);

                // 🔥 REPLACED NOTES WITH ORGANISATION
                setPlainCell(row.getCell(8),
                        item.getBudgetType() != null
                        ? safeText(item.getBudgetType().getName())
                        : "",
                        ParagraphAlignment.LEFT);

                setPlainCell(row.getCell(9),
                        item.getDeptUnit() != null
                        ? safeText(item.getDeptUnit().getNAME())
                        : "",
                        ParagraphAlignment.LEFT);
            }

            XWPFParagraph spacer = document.createParagraph();
            spacer.setSpacingAfter(220);
        }

        BigDecimal sectionTotal = safeAmount(
                sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                        request.getBudget(), coaLevel1, request.getSelectedSections(), request.getSelectedBudgetTypes()));

        XWPFTable totalTable = document.createTable(1, 2);
        totalTable.setWidth("100%");
        setCellText(totalTable.getRow(0).getCell(0), "Total " + sectionTitle, true, ParagraphAlignment.LEFT, "D9D9D9");
        setCellText(totalTable.getRow(0).getCell(1), formatCurrency(sectionTotal), true, ParagraphAlignment.RIGHT, "D9D9D9");

        XWPFParagraph spacer = document.createParagraph();
        spacer.setSpacingAfter(250);
    }

    private void addGrandTotalSection(XWPFDocument document,
            BudgetReportRequest request,
            List<Coalevel1> expenditureCoaLevels) {

        BigDecimal totalExpenditure = safeAmount(
                sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnitsTotal(
                        request.getBudget(), expenditureCoaLevels,
                        request.getSelectedSections(), request.getSelectedBudgetTypes()));

        addSectionTitle(document, "Overall Expenditure Position");

        XWPFTable table = document.createTable(1, 2);
        table.setWidth("100%");
        setCellText(table.getRow(0).getCell(0), "Total Expenditure", true, ParagraphAlignment.LEFT, "C9DAF8");
        setCellText(table.getRow(0).getCell(1), formatCurrency(totalExpenditure), true, ParagraphAlignment.RIGHT, "C9DAF8");
    }

    private void createBudgetTableHeader(XWPFTable table) {
        XWPFTableRow row = table.getRow(0);
        setCellText(row.getCell(0), "Item", true, ParagraphAlignment.LEFT, "EAEAEA");
        setCellText(row.addNewTableCell(), "Unit Cost", true, ParagraphAlignment.RIGHT, "EAEAEA");
        setCellText(row.addNewTableCell(), "Qty", true, ParagraphAlignment.CENTER, "EAEAEA");
        setCellText(row.addNewTableCell(), "Unit Measure", true, ParagraphAlignment.LEFT, "EAEAEA");
        setCellText(row.addNewTableCell(), "Currency", true, ParagraphAlignment.CENTER, "EAEAEA");
        setCellText(row.addNewTableCell(), "Annual Total", true, ParagraphAlignment.RIGHT, "EAEAEA");
        setCellText(row.addNewTableCell(), "Department / Unit", true, ParagraphAlignment.LEFT, "EAEAEA");
        setCellText(row.addNewTableCell(), "Notes", true, ParagraphAlignment.LEFT, "EAEAEA");
    }

    private void formatLabelValueRow(XWPFTableRow row, String label, String value) {
        setCellText(row.getCell(0), label, true, ParagraphAlignment.LEFT, "D9E2F3");
        setCellText(row.getCell(1), value, false, ParagraphAlignment.LEFT, "FFFFFF");
    }

    private void formatSummaryRow(XWPFTableRow row, String label, String value) {
        setCellText(row.getCell(0), label, true, ParagraphAlignment.LEFT, "EAEAEA");
        setCellText(row.getCell(1), value, true, ParagraphAlignment.RIGHT, "FFFFFF");
    }

    private void addSectionTitle(XWPFDocument document, String title) {
        XWPFParagraph p = document.createParagraph();
        p.setSpacingBefore(160);
        p.setSpacingAfter(100);
        p.setBorderBottom(Borders.SINGLE);

        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
        run.setText(title);
    }

    private void addCoaSubheading(XWPFDocument document, String title) {
        XWPFParagraph p = document.createParagraph();
        p.setSpacingBefore(100);
        p.setSpacingAfter(60);

        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontFamily("Times New Roman");
        run.setFontSize(11);
        run.setText(title);
    }

    private void setCellText(XWPFTableCell cell,
            String text,
            boolean bold,
            ParagraphAlignment alignment,
            String hexBgColor) {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(alignment);

        XWPFRun run = paragraph.createRun();
        run.setBold(bold);
        run.setFontFamily("Times New Roman");
        run.setFontSize(10);
        run.setText(text != null ? text : "");

        if (hexBgColor != null) {
            cell.setColor(hexBgColor);
        }

        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTTblWidth width = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
        width.setType(STTblWidth.DXA);
    }

    private void setPlainCell(XWPFTableCell cell, String text, ParagraphAlignment alignment) {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(alignment);

        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Times New Roman");
        run.setFontSize(10);
        run.setText(text != null ? text : "");
    }

    private boolean hasData(Budget budget,
            Coalevel1 coaLevel1,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            Set<Organisation> budgetTypes) {
        BigDecimal amount = sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                budget, coaLevel1, selectedSections, budgetTypes);
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private BigDecimal calculateSumOfMonths(BudgetItems item) {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(safeAmount(item.getJul()));
        total = total.add(safeAmount(item.getAug()));
        total = total.add(safeAmount(item.getSep()));
        total = total.add(safeAmount(item.getOct()));
        total = total.add(safeAmount(item.getNov()));
        total = total.add(safeAmount(item.getDec()));
        total = total.add(safeAmount(item.getJan()));
        total = total.add(safeAmount(item.getFeb()));
        total = total.add(safeAmount(item.getMar()));
        total = total.add(safeAmount(item.getApr()));
        total = total.add(safeAmount(item.getMay()));
        total = total.add(safeAmount(item.getJun()));
        return total;
    }

    private void configureLandscapePage(XWPFDocument document) {
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();

        CTPageSz pageSize = sectPr.addNewPgSz();
        pageSize.setOrient(STPageOrientation.LANDSCAPE);
        pageSize.setW(BigInteger.valueOf(16838));
        pageSize.setH(BigInteger.valueOf(11906));

        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setTop(BigInteger.valueOf(500));
        pageMar.setBottom(BigInteger.valueOf(500));
        pageMar.setLeft(BigInteger.valueOf(400));
        pageMar.setRight(BigInteger.valueOf(400));
    }

    private void addDetailedReportHeader(XWPFDocument document,
            String title,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            Set<?> selectedBudgetTypes) {

        XWPFParagraph p1 = document.createParagraph();
        p1.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(15);
        r1.setText("UGANDA RAILWAYS CORPORATION");

        XWPFParagraph p2 = document.createParagraph();
        p2.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(12);
        r2.setText(title != null ? title.toUpperCase() : "ACCOUNT CODE DETAIL REPORT");

        XWPFParagraph p3 = document.createParagraph();
        p3.setAlignment(ParagraphAlignment.LEFT);
        p3.setSpacingAfter(150);

        XWPFRun r3 = p3.createRun();
        r3.setFontFamily("Times New Roman");
        r3.setFontSize(10);
        r3.setText("Departments/Units: " + selectedSections.stream()
                .map(UrcDeptSectionAnlDimbgt::getNAME)
                .collect(Collectors.joining(", ")));

        XWPFParagraph p4 = document.createParagraph();
        p4.setAlignment(ParagraphAlignment.LEFT);
        p4.setSpacingAfter(200);

        XWPFRun r4 = p4.createRun();
        r4.setFontFamily("Times New Roman");
        r4.setFontSize(10);
        r4.setText("Budget Type(s): " + selectedBudgetTypes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }

    private void addSectionHeadingWord(XWPFDocument document, String sectionTitle) {
        XWPFParagraph p = document.createParagraph();
        p.setSpacingBefore(180);
        p.setSpacingAfter(100);

        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
        run.setText(sectionTitle);
    }

    private void addCoaHeaderWord(XWPFDocument document,
            String coaCode,
            String coaName,
            QuarterValues q) {

        XWPFTable table = document.createTable(1, 7);
        table.setWidth("100%");

        setCellText(table.getRow(0).getCell(0), "COA", true, ParagraphAlignment.LEFT, "FCE4D6");
        setCellText(table.getRow(0).getCell(1), safeText(coaCode), true, ParagraphAlignment.LEFT, "FCE4D6");
        setCellText(table.getRow(0).getCell(2), safeText(coaName), true, ParagraphAlignment.LEFT, "FCE4D6");
        setCellText(table.getRow(0).getCell(3), "Q1: " + formatCurrency(q.q1()), true, ParagraphAlignment.RIGHT, "FCE4D6");
        setCellText(table.getRow(0).getCell(4), "Q2: " + formatCurrency(q.q2()), true, ParagraphAlignment.RIGHT, "FCE4D6");
        setCellText(table.getRow(0).getCell(5), "Q3: " + formatCurrency(q.q3()), true, ParagraphAlignment.RIGHT, "FCE4D6");
        setCellText(table.getRow(0).getCell(6), "Q4: " + formatCurrency(q.q4()), true, ParagraphAlignment.RIGHT, "FCE4D6");
    }

    private void addSectionTotalWord(XWPFDocument document, String sectionTitle, QuarterValues totals) {
        XWPFTable table = document.createTable(1, 6);
        table.setWidth("100%");

        setCellText(table.getRow(0).getCell(0), "TOTAL " + sectionTitle, true, ParagraphAlignment.LEFT, "D9D9D9");
        setCellText(table.getRow(0).getCell(1), "Q1: " + formatCurrency(totals.q1()), true, ParagraphAlignment.RIGHT, "D9D9D9");
        setCellText(table.getRow(0).getCell(2), "Q2: " + formatCurrency(totals.q2()), true, ParagraphAlignment.RIGHT, "D9D9D9");
        setCellText(table.getRow(0).getCell(3), "Q3: " + formatCurrency(totals.q3()), true, ParagraphAlignment.RIGHT, "D9D9D9");
        setCellText(table.getRow(0).getCell(4), "Q4: " + formatCurrency(totals.q4()), true, ParagraphAlignment.RIGHT, "D9D9D9");
        setCellText(table.getRow(0).getCell(5), "Total: " + formatCurrency(totals.total()), true, ParagraphAlignment.RIGHT, "D9D9D9");

        XWPFParagraph spacer = document.createParagraph();
        spacer.setSpacingAfter(200);
    }

    private void addGrandExpenditureTotalWord(XWPFDocument document,
            QuarterValues revenueTotals,
            QuarterValues capitalTotals) {

        QuarterValues grand = revenueTotals.add(capitalTotals);

        XWPFTable table = document.createTable(1, 6);
        table.setWidth("100%");

        setCellText(table.getRow(0).getCell(0), "TOTAL EXPENDITURE", true, ParagraphAlignment.LEFT, "C9DAF8");
        setCellText(table.getRow(0).getCell(1), "Q1: " + formatCurrency(grand.q1()), true, ParagraphAlignment.RIGHT, "C9DAF8");
        setCellText(table.getRow(0).getCell(2), "Q2: " + formatCurrency(grand.q2()), true, ParagraphAlignment.RIGHT, "C9DAF8");
        setCellText(table.getRow(0).getCell(3), "Q3: " + formatCurrency(grand.q3()), true, ParagraphAlignment.RIGHT, "C9DAF8");
        setCellText(table.getRow(0).getCell(4), "Q4: " + formatCurrency(grand.q4()), true, ParagraphAlignment.RIGHT, "C9DAF8");
        setCellText(table.getRow(0).getCell(5), "Total: " + formatCurrency(grand.total()), true, ParagraphAlignment.RIGHT, "C9DAF8");
    }

    private QuarterValues loadQuarterValuesForCoa(
            Budget budget,
            COA coa,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            Set<Organisation> budgetTypes
    ) {
        BigDecimal jul = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "jul"));
        BigDecimal aug = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "aug"));
        BigDecimal sep = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "sep"));
        BigDecimal oct = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "oct"));
        BigDecimal nov = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "nov"));
        BigDecimal dec = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "dec"));
        BigDecimal jan = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "jan"));
        BigDecimal feb = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "feb"));
        BigDecimal mar = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "mar"));
        BigDecimal apr = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "apr"));
        BigDecimal may = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "may"));
        BigDecimal jun = safeAmount(sampleBudgetItemsService.findSumOfIndividualMonthsByBudgetCoacodeAndDeptUnits(
                budget, coa, selectedSections, budgetTypes, "jun"));

        BigDecimal q1 = jul.add(aug).add(sep);
        BigDecimal q2 = oct.add(nov).add(dec);
        BigDecimal q3 = jan.add(feb).add(mar);
        BigDecimal q4 = apr.add(may).add(jun);

        return new QuarterValues(q1, q2, q3, q4, q1.add(q2).add(q3).add(q4));
    }

    public record QuarterValues(
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4,
            BigDecimal total) {

        public static QuarterValues zero() {
            return new QuarterValues(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        public QuarterValues add(QuarterValues other) {
            return new QuarterValues(
                    this.q1.add(other.q1),
                    this.q2.add(other.q2),
                    this.q3.add(other.q3),
                    this.q4.add(other.q4),
                    this.total.add(other.total)
            );
        }
    }

    private QuarterValues calculateQuarterValues(BudgetItems item) {

        BigDecimal jul = safeAmount(item.getJul());
        BigDecimal aug = safeAmount(item.getAug());
        BigDecimal sep = safeAmount(item.getSep());

        BigDecimal oct = safeAmount(item.getOct());
        BigDecimal nov = safeAmount(item.getNov());
        BigDecimal dec = safeAmount(item.getDec());

        BigDecimal jan = safeAmount(item.getJan());
        BigDecimal feb = safeAmount(item.getFeb());
        BigDecimal mar = safeAmount(item.getMar());

        BigDecimal apr = safeAmount(item.getApr());
        BigDecimal may = safeAmount(item.getMay());
        BigDecimal jun = safeAmount(item.getJun());

        BigDecimal q1 = jul.add(aug).add(sep);
        BigDecimal q2 = oct.add(nov).add(dec);
        BigDecimal q3 = jan.add(feb).add(mar);
        BigDecimal q4 = apr.add(may).add(jun);

        BigDecimal total = q1.add(q2).add(q3).add(q4);

        return new QuarterValues(q1, q2, q3, q4, total);
    }

    private void createDetailedQuarterTableHeader(XWPFTable table) {
        XWPFTableRow row = table.getRow(0);

        while (row.getTableCells().size() < 8) {
            row.addNewTableCell();
        }

        setCellText(row.getCell(0), "ITEM", true, ParagraphAlignment.CENTER, "D9E2F3");
        setCellText(row.getCell(1), "TOTAL", true, ParagraphAlignment.CENTER, "D9E2F3");
        setCellText(row.getCell(2), "Q1 / UGX", true, ParagraphAlignment.CENTER, "D9E2F3");
        setCellText(row.getCell(3), "Q2 / UGX", true, ParagraphAlignment.CENTER, "D9E2F3");
        setCellText(row.getCell(4), "Q3 / UGX", true, ParagraphAlignment.CENTER, "D9E2F3");
        setCellText(row.getCell(5), "Q4 / UGX", true, ParagraphAlignment.CENTER, "D9E2F3");
        setCellText(row.getCell(6), "Fund Source", true, ParagraphAlignment.CENTER, "D9E2F3");
        setCellText(row.getCell(7), "Section", true, ParagraphAlignment.CENTER, "D9E2F3");
    }

    public byte[] generateDetailedAccountCodeQuarterWordReport(
            Budget budget,
            Set<UrcDeptSectionAnlDimbgt> sect,
            Set<Organisation> budgetTypes,
            String title
    ) {
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            configureLandscapePage(document);

            List<UrcDeptSectionAnlDimbgt> selectedSections = new ArrayList<>(sect);

            Coalevel1 incomeCoal = sampleCoalevel1Service.findByCode(1);
            Coalevel1 revenueCoal = sampleCoalevel1Service.findByCode(2);
            Coalevel1 capitalCoal = sampleCoalevel1Service.findByCode(3);

            addDetailedReportHeader(document, title, selectedSections, budgetTypes);

            QuarterValues incomeTotals = buildDetailedQuarterCoaLevelSectionWord(
                    document, incomeCoal, "INCOME", budget, selectedSections, budgetTypes
            );

            QuarterValues revenueTotals = buildDetailedQuarterCoaLevelSectionWord(
                    document, revenueCoal, "REVENUE EXPENDITURE", budget, selectedSections, budgetTypes
            );

            QuarterValues capitalTotals = buildDetailedQuarterCoaLevelSectionWord(
                    document, capitalCoal, "CAPITAL EXPENDITURE", budget, selectedSections, budgetTypes
            );

            if (revenueTotals.total().compareTo(BigDecimal.ZERO) > 0
                    || capitalTotals.total().compareTo(BigDecimal.ZERO) > 0) {
                addGrandExpenditureTotalWord(document, revenueTotals, capitalTotals);
            }

            document.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate quarter-based account code Word report", e);
        }
    }

    private QuarterValues buildDetailedQuarterCoaLevelSectionWord(
            XWPFDocument document,
            Coalevel1 coaLevel,
            String sectionTitle,
            Budget budget,
            List<UrcDeptSectionAnlDimbgt> selectedSections,
            Set<Organisation> budgetTypes
    ) {
        BigDecimal sectionTotal = safeAmount(
                sampleBudgetItemsService.findSumByBudgetCoalevel1AndDeptUnits(
                        budget, coaLevel, selectedSections, budgetTypes
                )
        );

        if (sectionTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return QuarterValues.zero();
        }

        addSectionHeadingWord(document, sectionTitle);

        List<COA> coaList = sampleBudgetItemsService.findDistinctCoacodeByBudgetCoalevel1AndDeptUnits(
                budget, coaLevel, selectedSections, budgetTypes
        );

        QuarterValues sectionAccumulated = QuarterValues.zero();

        for (COA coa : coaList) {
            BigDecimal coaTotal = safeAmount(
                    sampleBudgetItemsService.findSumOfAllMonthsByBudgetCoacodeAndDeptUnits(
                            budget, coa, selectedSections, budgetTypes
                    )
            );

            if (coaTotal.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            QuarterValues coaQuarterValues = loadQuarterValuesForCoa(
                    budget, coa, selectedSections, budgetTypes
            );
            sectionAccumulated = sectionAccumulated.add(coaQuarterValues);

            addCoaHeaderWord(document, coa.getCode(), coa.getName(), coaQuarterValues);

            List<BudgetItems> items = sampleBudgetItemsService.findByBudgetCoacodeAndDeptUnits(
                    budget, coa, selectedSections, budgetTypes
            );

            XWPFTable table = document.createTable(1, 8);
            table.setWidth("100%");
            createDetailedQuarterTableHeader(table);

            for (BudgetItems item : items) {
                QuarterValues q = calculateQuarterValues(item);

                XWPFTableRow row = table.createRow();
                while (row.getTableCells().size() < 8) {
                    row.addNewTableCell();
                }

                setPlainCell(row.getCell(0), safeText(item.getItem()), ParagraphAlignment.LEFT);
                setPlainCell(row.getCell(1), formatCurrency(q.total()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(2), formatCurrency(q.q1()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(3), formatCurrency(q.q2()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(4), formatCurrency(q.q3()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(5), formatCurrency(q.q4()), ParagraphAlignment.RIGHT);
                setPlainCell(row.getCell(6),
                        item.getBudgetType() != null ? safeText(item.getBudgetType().getName()) : "",
                        ParagraphAlignment.LEFT);
                setPlainCell(row.getCell(7),
                        item.getDeptUnit() != null ? safeText(item.getDeptUnit().getNAME()) : "",
                        ParagraphAlignment.LEFT);
            }

            XWPFParagraph spacer = document.createParagraph();
            spacer.setSpacingAfter(160);
        }

        addSectionTotalWord(document, sectionTitle, sectionAccumulated);
        return sectionAccumulated;
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String safeText(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String safeCurrency(BudgetItems item) {
        if (item == null || item.getCurrency() == null || item.getCurrency().getData() == null) {
            return "";
        }
        return safeText(item.getCurrency().getData().getCurrencyShort());
    }

    private String formatCurrency(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(safeAmount(amount));
    }
}
