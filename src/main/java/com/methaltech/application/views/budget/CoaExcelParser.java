package com.methaltech.application.views.budget;

import com.methaltech.application.data.entity.bgtool.dto.CoaClassificationRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.*;

public final class CoaExcelParser {

    private CoaExcelParser() {
    }

    public static List<CoaClassificationRow> parse(InputStream in) throws Exception {
        try (Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                return List.of();
            }

            Map<String, Integer> header = readHeader(sheet.getRow(0));

            int codeCol = require(header, "code");
            int nameCol = require(header, "name");
            int c1Col = require(header, "class1");
            int c2Col = require(header, "class2");
            int c3Col = header.getOrDefault("class3", -1); // optional

            DataFormatter fmt = new DataFormatter();

            List<CoaClassificationRow> out = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }

                String code = fmt.formatCellValue(row.getCell(codeCol)).trim();
                if (code.isBlank()) {
                    continue;
                }

                String name = fmt.formatCellValue(row.getCell(nameCol)).trim();
                String class1 = fmt.formatCellValue(row.getCell(c1Col)).trim();
                String class2 = fmt.formatCellValue(row.getCell(c2Col)).trim();
                String class3 = (c3Col >= 0) ? fmt.formatCellValue(row.getCell(c3Col)).trim() : null;

                out.add(new CoaClassificationRow(code, name, class1, class2, class3));
            }
            return out;
        }
    }

    private static Map<String, Integer> readHeader(Row row) {
        Map<String, Integer> m = new HashMap<>();
        if (row == null) {
            return m;
        }

        DataFormatter fmt = new DataFormatter();
        for (Cell c : row) {
            String key = fmt.formatCellValue(c).trim().toLowerCase(Locale.ROOT);
            if (!key.isBlank()) {
                m.put(key, c.getColumnIndex());
            }
        }
        return m;
    }

    private static int require(Map<String, Integer> header, String col) {
        Integer idx = header.get(col);
        if (idx == null) {
            throw new IllegalArgumentException("Missing header column: " + col);
        }
        return idx;
    }
}
