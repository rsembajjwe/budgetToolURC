
package com.methaltech.application.views.procurementplan;

import com.methaltech.application.data.entity.bgtool.BudgetItems;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class BudgetItemExcelExporter {

    public ByteArrayInputStream exportBudgetItemsToExcel(List<BudgetItems> items) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Budget Items Report");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle boldStyle = createBoldStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"COA Code", "Item", "QTR1", "QTR2", "QTR3", "QTR4", "Budget Type", "Total", "Cost Centre"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Track totals
            BigDecimal totalQ1 = BigDecimal.ZERO;
            BigDecimal totalQ2 = BigDecimal.ZERO;
            BigDecimal totalQ3 = BigDecimal.ZERO;
            BigDecimal totalQ4 = BigDecimal.ZERO;
            BigDecimal totalGrand = BigDecimal.ZERO;

            int rowIndex = 1;

            for (BudgetItems item : items) {
                Row row = sheet.createRow(rowIndex++);

                BigDecimal q1 = sum(safe(item.getJul()), safe(item.getAug()), safe(item.getSep()));
                BigDecimal q2 = sum(safe(item.getOct()), safe(item.getNov()), safe(item.getDec()));
                BigDecimal q3 = sum(safe(item.getJan()), safe(item.getFeb()), safe(item.getMar()));
                BigDecimal q4 = sum(safe(item.getApr()), safe(item.getMay()), safe(item.getJun()));
                BigDecimal total = q1.add(q2).add(q3).add(q4);

                totalQ1 = totalQ1.add(q1);
                totalQ2 = totalQ2.add(q2);
                totalQ3 = totalQ3.add(q3);
                totalQ4 = totalQ4.add(q4);
                totalGrand = totalGrand.add(total);

                int col = 0;

                row.createCell(col++).setCellValue(item.getCoacode() != null ? item.getCoacode().getCode() : "");
                //row.createCell(col++).setCellValue(trim(item.getItem(), 50));
                row.createCell(col++).setCellValue(item.getItem());

                Cell cellQ1 = row.createCell(col++);
                cellQ1.setCellValue(q1.doubleValue());
                cellQ1.setCellStyle(numberStyle);

                Cell cellQ2 = row.createCell(col++);
                cellQ2.setCellValue(q2.doubleValue());
                cellQ2.setCellStyle(numberStyle);

                Cell cellQ3 = row.createCell(col++);
                cellQ3.setCellValue(q3.doubleValue());
                cellQ3.setCellStyle(numberStyle);

                Cell cellQ4 = row.createCell(col++);
                cellQ4.setCellValue(q4.doubleValue());
                cellQ4.setCellStyle(numberStyle);

                row.createCell(col++).setCellValue(item.getBudgetType() != null ? item.getBudgetType().getName() : "");

                Cell totalCell = row.createCell(col++);
                totalCell.setCellValue(total.doubleValue());
                totalCell.setCellStyle(numberStyle);
                
                row.createCell(col++).setCellValue(item.getDeptUnit() != null ? item.getDeptUnit().getNAME() : "");
            }

            // Add totals row
            Row totalsRow = sheet.createRow(rowIndex);
            totalsRow.createCell(1).setCellValue("Totals");
            totalsRow.getCell(1).setCellStyle(boldStyle);

            writeNumberCell(totalsRow, 2, totalQ1, numberStyle);
            writeNumberCell(totalsRow, 3, totalQ2, numberStyle);
            writeNumberCell(totalsRow, 4, totalQ3, numberStyle);
            writeNumberCell(totalsRow, 5, totalQ4, numberStyle);
            writeNumberCell(totalsRow, 7, totalGrand, numberStyle);

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Export to stream
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ByteArrayInputStream(new byte[0]); // fallback
        }
    }

    private BigDecimal safe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private BigDecimal sum(BigDecimal a, BigDecimal b, BigDecimal c) {
        return a.add(b).add(c);
    }

    private String trim(String input, int maxLen) {
        if (input == null) return "";
        return input.length() <= maxLen ? input : input.substring(0, maxLen);
    }

    private void writeNumberCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value.doubleValue());
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createBoldStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }
}

