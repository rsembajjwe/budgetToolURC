
package com.methaltech.application.data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.EnumMap;
import java.util.Map;

public class Styles {

    public enum Key {
        TITLE, SUBTITLE, HEADER, TEXT, TEXT_WRAP,
        MONEY, MONEY_ALT, CODE, ZEBRA_TEXT, ZEBRA_MONEY
    }

    public static Map<Key, CellStyle> build(Workbook wb) {
        Map<Key, CellStyle> s = new EnumMap<>(Key.class);

        DataFormat df = wb.createDataFormat();

        Font title = wb.createFont();
        title.setFontName("Calibri");
        title.setFontHeightInPoints((short) 16);
        title.setBold(true);

        Font subtitle = wb.createFont();
        subtitle.setFontName("Calibri");
        subtitle.setFontHeightInPoints((short) 11);
        subtitle.setBold(true);

        Font header = wb.createFont();
        header.setFontName("Calibri");
        header.setFontHeightInPoints((short) 10);
        header.setBold(true);
        header.setColor(IndexedColors.WHITE.getIndex());

        Font normal = wb.createFont();
        normal.setFontName("Calibri");
        normal.setFontHeightInPoints((short) 10);

        // Helper to apply consistent borders
        java.util.function.Consumer<CellStyle> borders = st -> {
            st.setBorderTop(BorderStyle.THIN);
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderLeft(BorderStyle.THIN);
            st.setBorderRight(BorderStyle.THIN);
            st.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        };

        // Title (merged)
        CellStyle titleSt = wb.createCellStyle();
        titleSt.setFont(title);
        titleSt.setAlignment(HorizontalAlignment.CENTER);
        titleSt.setVerticalAlignment(VerticalAlignment.CENTER);
        s.put(Key.TITLE, titleSt);

        // Subtitle band
        CellStyle subSt = wb.createCellStyle();
        subSt.setFont(subtitle);
        subSt.setAlignment(HorizontalAlignment.CENTER);
        subSt.setVerticalAlignment(VerticalAlignment.CENTER);
        subSt.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        subSt.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(subSt);
        s.put(Key.SUBTITLE, subSt);

        // Column header band (dark)
        CellStyle headSt = wb.createCellStyle();
        headSt.setFont(header);
        headSt.setAlignment(HorizontalAlignment.CENTER);
        headSt.setVerticalAlignment(VerticalAlignment.CENTER);
        headSt.setWrapText(true);
        headSt.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headSt.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(headSt);
        s.put(Key.HEADER, headSt);

        // Base text
        CellStyle text = wb.createCellStyle();
        text.setFont(normal);
        text.setAlignment(HorizontalAlignment.LEFT);
        text.setVerticalAlignment(VerticalAlignment.CENTER);
        borders.accept(text);
        s.put(Key.TEXT, text);

        CellStyle textWrap = wb.createCellStyle();
        textWrap.cloneStyleFrom(text);
        textWrap.setWrapText(true);
        s.put(Key.TEXT_WRAP, textWrap);

        // Code centered
        CellStyle code = wb.createCellStyle();
        code.cloneStyleFrom(text);
        code.setAlignment(HorizontalAlignment.CENTER);
        s.put(Key.CODE, code);

        // Money
        CellStyle money = wb.createCellStyle();
        money.cloneStyleFrom(text);
        money.setAlignment(HorizontalAlignment.RIGHT);
        money.setDataFormat(df.getFormat("#,##0.00"));
        s.put(Key.MONEY, money);

        // Alternate fill for zebra rows
        CellStyle zebraText = wb.createCellStyle();
        zebraText.cloneStyleFrom(textWrap);
        zebraText.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        zebraText.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(Key.ZEBRA_TEXT, zebraText);

        CellStyle zebraMoney = wb.createCellStyle();
        zebraMoney.cloneStyleFrom(money);
        zebraMoney.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        zebraMoney.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(Key.ZEBRA_MONEY, zebraMoney);

        return s;
    }
}
