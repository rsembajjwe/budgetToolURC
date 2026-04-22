package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.springframework.stereotype.Service;

@Service
public class DepartmentWorkplanWordExportService {

    private final BudgetItemsService budgetItemsService;

    public DepartmentWorkplanWordExportService(BudgetItemsService budgetItemsService) {
        this.budgetItemsService = budgetItemsService;
    }

    public byte[] exportDepartmentWorkplanDocx(
            Budget budget,
            List<DepartmentBudget> departments,
            Set<Organisation> selectedOrganisations
    ) throws IOException, InvalidFormatException {

        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            setupPage(doc);

            addFirstPageHeader(
                    doc,
                    "/META-INF/resources/images/urclogo.png",
                    "DEPARTMENT WORKPLAN-BUDGET REPORT",
                     safe(budget != null ? budget.getFinancialYear() : "")
            );

            addRightMeta(
                    doc,
                    "Generated on: "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"))
            );

            if (departments == null || departments.isEmpty()) {
                addSectionHeader(doc, "NO DEPARTMENT WORKPLANS AVAILABLE");
                addFooterNote(doc);
                doc.write(baos);
                return baos.toByteArray();
            }

            for (DepartmentBudget department : departments) {
                if (department == null || department.getSections() == null || department.getSections().isEmpty()) {
                    continue;
                }

                addDepartmentSection(doc, budget, department, selectedOrganisations);
                addSpacer(doc, 2);
            }

            addFooterNote(doc);

            doc.write(baos);
            return baos.toByteArray();
        }
    }

    private void addDepartmentSection(
            XWPFDocument doc,
            Budget budget,
            DepartmentBudget department,
            Set<Organisation> selectedOrganisations
    ) {
        addSectionHeader(doc, safe(department.getDepartmentName()).toUpperCase());

        /*        XWPFTable deptSummary = doc.createTable(1, 2);
        styleTable(deptSummary);
        setHeaderRow(deptSummary.getRow(0), "Department Information", "Value");
        addRow(deptSummary, "Department Name", safe(department.getDepartmentName()));
        addRow(deptSummary, "Total Budget", formatUGX(nz(department.getTotalBudget())));
        addRow(deptSummary, "Total Spent", formatUGX(nz(department.getTotalSpent())));
        
        addSpacer(doc, 1);*/

        Map<Long, ActivityRenderData> activityDataMap = preloadActivityRenderData(
                budget,
                selectedOrganisations,
                department.getSections()
        );

        Map<String, List<ActivityRenderData>> byProgramme = activityDataMap.values().stream()
                .sorted((a, b) -> safe(a.activity() != null ? a.activity().getName() : "")
                        .compareToIgnoreCase(safe(b.activity() != null ? b.activity().getName() : "")))
                .collect(Collectors.groupingBy(
                        a -> resolveProgrammeName(a.activity()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        if (byProgramme.isEmpty()) {
            addSubSectionHeader(doc, "No expenditure activities found");
            return;
        }

        for (Map.Entry<String, List<ActivityRenderData>> entry : byProgramme.entrySet()) {
            addSubSectionHeader(doc, "Programme: " + entry.getKey());

            for (ActivityRenderData act : entry.getValue()) {
                addActivityBlock(doc, act);
            }
        }
    }

private void addActivityBlock(XWPFDocument doc, ActivityRenderData act) {
    Urc_Activities activity = act.activity();

    // ===== ACTIVITY TITLE =====
    XWPFParagraph activityP = doc.createParagraph();
    activityP.setSpacingBefore(140);
    activityP.setSpacingAfter(40);

    XWPFRun ar = activityP.createRun();
    ar.setBold(true);
    ar.setFontFamily("Calibri");
    ar.setFontSize(11);
    ar.setText("Activity: " + safe(activity != null ? activity.getName() : ""));

    // ===== ACTIVITY DETAILS TABLE (single horizontal row) =====
    XWPFTable details = doc.createTable(2, 6);
    styleTable(details);
    setHeaderRow(
            details.getRow(0),
            "Outcome",
            "Output",
            "KPI / Performance Indicator",
            "Objective",
            "Accounts",
            "Funding"
    );

    XWPFTableRow detailRow = details.getRow(1);
    setCell(detailRow.getCell(0), safe(activity != null ? activity.getOutcome() : ""), false, ParagraphAlignment.LEFT);
    setCell(detailRow.getCell(1), safe(activity != null ? activity.getOutput() : ""), false, ParagraphAlignment.LEFT);
    setCell(detailRow.getCell(2), safe(activity != null ? activity.getPerformanceIndicator() : ""), false, ParagraphAlignment.LEFT);
    setCell(detailRow.getCell(3), safe(activity != null ? activity.getObjective() : ""), false, ParagraphAlignment.LEFT);
    setCell(detailRow.getCell(4), safe(act.accounts()), false, ParagraphAlignment.LEFT);
    setCell(detailRow.getCell(5), safe(act.funding()), false, ParagraphAlignment.LEFT);

    //addSpacer(doc, 1);

    // ===== TOTALS TABLE =====
    XWPFTable totals = doc.createTable(2, 5);
    styleTable(totals);
    setActivityTotalsHeaderRow(
            totals.getRow(0),
            "Total Budget",
            "Q1",
            "Q2",
            "Q3",
            "Q4"
    );

    XWPFTableRow totalRow = totals.getRow(1);
    setCell(totalRow.getCell(0), formatUGXNoPrefix(act.total()), false, ParagraphAlignment.RIGHT);
    setCell(totalRow.getCell(1), formatUGXNoPrefix(act.q1()), false, ParagraphAlignment.RIGHT);
    setCell(totalRow.getCell(2), formatUGXNoPrefix(act.q2()), false, ParagraphAlignment.RIGHT);
    setCell(totalRow.getCell(3), formatUGXNoPrefix(act.q3()), false, ParagraphAlignment.RIGHT);
    setCell(totalRow.getCell(4), formatUGXNoPrefix(act.q4()), false, ParagraphAlignment.RIGHT);

    //addSpacer(doc, 1);

    // ===== BUDGET ITEM BREAKDOWN =====
    XWPFParagraph detailTitle = doc.createParagraph();
    detailTitle.setSpacingBefore(40);
    detailTitle.setSpacingAfter(30);

    XWPFRun dtr = detailTitle.createRun();
    dtr.setBold(true);
    dtr.setFontFamily("Calibri");
    dtr.setFontSize(10);
    dtr.setText("Budget Item Breakdown");

    if (act.items() == null || act.items().isEmpty()) {
        XWPFParagraph none = doc.createParagraph();
        XWPFRun nr = none.createRun();
        nr.setFontFamily("Calibri");
        nr.setFontSize(9);
        nr.setItalic(true);
        nr.setColor("666666");
        nr.setText("No budget items available.");
        addSpacer(doc, 1);
        return;
    }

    XWPFTable itemTable = doc.createTable(1, 8);
    styleTable(itemTable);
    setHeaderRow(
            itemTable.getRow(0),
            "Code",
            "Code Description",
            "Item",
            "Amount",
            "Q1",
            "Q2",
            "Q3",
            "Q4"
    );

    for (BudgetItems item : act.items()) {
        XWPFTableRow r = itemTable.createRow();

        setCell(r.getCell(0),
                safe(item.getCoacode() != null ? item.getCoacode().getCode().trim() : ""),
                false,
                ParagraphAlignment.LEFT);

        setCell(r.getCell(1),
                safe(item.getCoacode() != null ? item.getCoacode().getName() : ""),
                false,
                ParagraphAlignment.LEFT);

        setCell(r.getCell(2),
                safe(item.getItem()),
                false,
                ParagraphAlignment.LEFT);

        setCell(r.getCell(3),
                formatUGXNoPrefix(nz(item.getYearTotalFromQuarters())),
                false,
                ParagraphAlignment.RIGHT);

        setCell(r.getCell(4),
                formatUGXNoPrefix(nz(item.getQ1Total())),
                false,
                ParagraphAlignment.RIGHT);

        setCell(r.getCell(5),
                formatUGXNoPrefix(nz(item.getQ2Total())),
                false,
                ParagraphAlignment.RIGHT);

        setCell(r.getCell(6),
                formatUGXNoPrefix(nz(item.getQ3Total())),
                false,
                ParagraphAlignment.RIGHT);

        setCell(r.getCell(7),
                formatUGXNoPrefix(nz(item.getQ4Total())),
                false,
                ParagraphAlignment.RIGHT);
    }

    addSpacer(doc, 1);
}

    private Map<Long, ActivityRenderData> preloadActivityRenderData(
            Budget budget,
            Set<Organisation> selectedOrganisations,
            Set<UrcDeptSectionAnlDimbgt> selectedSections
    ) {
        List<BudgetItems> allItems = budgetItemsService.findExpenseItemsByBudgetAndBudgetTypesAndDeptUnits(
                budget,
                selectedOrganisations,
                selectedSections
        );

        Map<Long, List<BudgetItems>> itemsByActivity = allItems.stream()
                .filter(item -> item.getActivity() != null && item.getActivity().getId() != null)
                .collect(Collectors.groupingBy(item -> item.getActivity().getId()));

        Map<Long, ActivityRenderData> result = new HashMap<>();

        for (Map.Entry<Long, List<BudgetItems>> entry : itemsByActivity.entrySet()) {
            List<BudgetItems> items = entry.getValue();
            Urc_Activities activity = items.get(0).getActivity();

            BigDecimal total = items.stream()
                    .map(item -> nz(item.getYearTotalFromQuarters()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q1 = items.stream()
                    .map(item -> nz(item.getQ1Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q2 = items.stream()
                    .map(item -> nz(item.getQ2Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q3 = items.stream()
                    .map(item -> nz(item.getQ3Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal q4 = items.stream()
                    .map(item -> nz(item.getQ4Total()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String accounts = items.stream()
                    .map(item -> item.getCoacode() != null ? safe(item.getCoacode().getCode().trim()) : "")
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            String funding = items.stream()
                    .map(item -> item.getBudgetType() != null ? safe(item.getBudgetType().getName()) : "")
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            result.put(entry.getKey(), new ActivityRenderData(
                    activity,
                    items,
                    total,
                    q1,
                    q2,
                    q3,
                    q4,
                    accounts,
                    funding
            ));
        }

        return result;
    }

    private String resolveProgrammeName(Urc_Activities activity) {
        if (activity == null || activity.getUrcPriorityAreas() == null) {
            return "GENERAL";
        }

        try {
            URC_Priority_Areas urc = activity.getUrcPriorityAreas();

            if (urc.getPriorityArea() != null && urc.getPriorityArea().getName() != null) {
                return urc.getPriorityArea().getName();
            }

            if (urc.getName() != null) {
                return urc.getName();
            }
        } catch (Exception ignored) {
        }

        return "GENERAL";
    }

    private void setupPage(XWPFDocument doc) {
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();

        if (!sectPr.isSetPgSz()) {
            sectPr.addNewPgSz();
        }

        sectPr.getPgSz().setW(BigInteger.valueOf(16838));
        sectPr.getPgSz().setH(BigInteger.valueOf(11906));
        sectPr.getPgSz().setOrient(STPageOrientation.LANDSCAPE);

        CTPageMar mar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
        mar.setTop(900);
        mar.setBottom(900);
        mar.setLeft(900);
        mar.setRight(900);
    }

    private void addFirstPageHeader(
            XWPFDocument doc,
            String logoClasspath,
            String title,
            String subtitle
    ) throws IOException, InvalidFormatException {

        CTBody body = doc.getDocument().getBody();
        CTSectPr sectPr = body.isSetSectPr() ? body.getSectPr() : body.addNewSectPr();

        if (!sectPr.isSetTitlePg()) {
            sectPr.addNewTitlePg();
        }

        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc, sectPr);

        XWPFHeader firstHeader = policy.createHeader(XWPFHeaderFooterPolicy.FIRST);
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

        XWPFFooter defaultFooter = policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFFooter firstFooter = policy.createFooter(XWPFHeaderFooterPolicy.FIRST);

        XWPFParagraph logoP = firstHeader.createParagraph();
        logoP.setAlignment(ParagraphAlignment.LEFT);

        try (InputStream is = getClass().getResourceAsStream(logoClasspath)) {
            if (is != null) {
                XWPFRun lr = logoP.createRun();
                lr.addPicture(
                        is,
                        pictureTypeFromPath(logoClasspath),
                        "logo",
                        Units.toEMU(60),
                        Units.toEMU(60)
                );
            }
        }

        XWPFParagraph titleP = firstHeader.createParagraph();
        titleP.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun tr = titleP.createRun();
        tr.setBold(true);
        tr.setFontFamily("Calibri");
        tr.setFontSize(14);
        tr.setText(title);

        XWPFParagraph subtitleP = firstHeader.createParagraph();
        subtitleP.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun sr = subtitleP.createRun();
        sr.setFontFamily("Calibri");
        sr.setFontSize(10);
        sr.setColor("666666");
        sr.setText(subtitle);

        addFooterWithPageNumbers(defaultFooter);
        addFooterWithPageNumbers(firstFooter);
    }

    private void addFooterWithPageNumbers(XWPFFooter footer) {
        XWPFParagraph fp = footer.createParagraph();
        fp.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun text1 = fp.createRun();
        text1.setFontFamily("Calibri");
        text1.setFontSize(9);
        text1.setColor("666666");
        text1.setText("Uganda Railways Corporation  |  Page ");

        addField(fp, "PAGE");

        XWPFRun text2 = fp.createRun();
        text2.setFontFamily("Calibri");
        text2.setFontSize(9);
        text2.setColor("666666");
        text2.setText(" of ");

        addField(fp, "NUMPAGES");
    }

    private void addField(XWPFParagraph paragraph, String fieldName) {
        CTSimpleField field = paragraph.getCTP().addNewFldSimple();
        field.setInstr(" " + fieldName + " ");
    }

    private int pictureTypeFromPath(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".png")) {
            return XWPFDocument.PICTURE_TYPE_PNG;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return XWPFDocument.PICTURE_TYPE_JPEG;
        }
        return XWPFDocument.PICTURE_TYPE_PNG;
    }

    private void addRightMeta(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(9);
        r.setColor("666666");
        r.setText(text);
    }

    private void addSectionHeader(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(180);
        p.setSpacingAfter(60);

        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setFontFamily("Calibri");
        r.setFontSize(13);
        r.setText(text);
    }

    private void addSubSectionHeader(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(120);
        p.setSpacingAfter(40);

        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setFontFamily("Calibri");
        r.setFontSize(11);
        r.setText(text);
    }

    private void addSpacer(XWPFDocument doc, int lines) {
        for (int i = 0; i < lines; i++) {
            doc.createParagraph();
        }
    }

    private void styleTable(XWPFTable table) {
        table.setWidth("100%");
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "D9D9D9");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "D9D9D9");
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 0, "BFBFBF");
        table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 0, "BFBFBF");
        table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 0, "BFBFBF");
        table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 0, "BFBFBF");
    }

    private void setHeaderRow(XWPFTableRow row, String... values) {
        row.setRepeatHeader(true);

        for (int i = 0; i < values.length; i++) {
            XWPFTableCell cell = i < row.getTableCells().size() ? row.getCell(i) : row.addNewTableCell();
            setCell(cell, values[i], true, ParagraphAlignment.CENTER);
        }
    }

    private void setActivityTotalsHeaderRow(XWPFTableRow row, String... values) {
        row.setRepeatHeader(true);

        for (int i = 0; i < values.length; i++) {
            XWPFTableCell cell = i < row.getTableCells().size() ? row.getCell(i) : row.addNewTableCell();
            setActivityTotalsHeaderCell(cell, values[i], ParagraphAlignment.CENTER);
        }
    }

    private void addRow(XWPFTable table, String... values) {
        XWPFTableRow row = table.createRow();

        for (int i = 0; i < values.length; i++) {
            setCell(
                    row.getCell(i),
                    values[i],
                    false,
                    i == 0 ? ParagraphAlignment.LEFT : ParagraphAlignment.LEFT
            );
        }
    }

    private void setCell(XWPFTableCell cell, String text, boolean header, ParagraphAlignment alignment) {
        if (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }

        XWPFParagraph p = cell.addParagraph();
        p.setAlignment(alignment);

        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(header ? 10 : 9);
        r.setBold(header);
        r.setText(safe(text));

        if (header) {
            cell.setColor("D9EAF7");
        }
    }

    private void setActivityTotalsHeaderCell(XWPFTableCell cell, String text, ParagraphAlignment alignment) {
        if (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }

        XWPFParagraph p = cell.addParagraph();
        p.setAlignment(alignment);

        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(9);
        r.setBold(true);
        r.setColor("FFFFFF");
        r.setText(safe(text));

        cell.setColor("5B9BD5");
    }

    private void addFooterNote(XWPFDocument doc) {
        XWPFParagraph foot = doc.createParagraph();
        foot.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun fr = foot.createRun();
        fr.setFontFamily("Calibri");
        fr.setFontSize(9);
        fr.setColor("666666");
        fr.setText("This report was generated automatically from the department workplan export.");
    }

    private String formatUGX(BigDecimal value) {
        BigDecimal v = nz(value).setScale(2, RoundingMode.HALF_UP);
        return "UGX " + String.format("%,.2f", v);
    }

    private String formatUGXNoPrefix(BigDecimal value) {
        BigDecimal v = nz(value).setScale(2, RoundingMode.HALF_UP);
        return String.format("%,.2f", v);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private record ActivityRenderData(
            Urc_Activities activity,
            List<BudgetItems> items,
            BigDecimal total,
            BigDecimal q1,
            BigDecimal q2,
            BigDecimal q3,
            BigDecimal q4,
            String accounts,
            String funding
    ) {}
}
