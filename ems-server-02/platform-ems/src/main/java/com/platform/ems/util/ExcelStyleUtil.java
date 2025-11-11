package com.platform.ems.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelStyleUtil {
    /**
     * color 单元格填充色
     * 水平居中、垂直居中
     * 字体：宋体
     * 字体大小：16号
     * 加粗
     * @param workbook
     * @return
     */
    public static XSSFCellStyle getXSSFCellStyle(XSSFWorkbook workbook, XSSFColor color) {
        XSSFCellStyle cellstyle;
        cellstyle = workbook.createCellStyle();
        cellstyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        Font font=workbook.createFont();//字体
        font.setFontName("宋体");//字体
        font.setFontHeightInPoints((short)10);//字号
        font.setBold(true);//加粗
        cellstyle.setFont(font);
        if (color != null){
            cellstyle.setFillForegroundColor(color);
            cellstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        setBorderStyle(cellstyle);
        return cellstyle;
    }
    /**
     * 水平居中、垂直居中
     * 字体：宋体
     * 字体大小：16号
     * 加粗
     * @param workbook
     * @return
     */
    public static CellStyle getStyle(XSSFWorkbook workbook) {
        CellStyle cellstyle=workbook.createCellStyle();
        cellstyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        Font font=workbook.createFont();//字体
        font.setFontName("宋体");//字体
        font.setFontHeightInPoints((short)10);//字号
        font.setBold(true);//加粗
        cellstyle.setFont(font);
        setBorderStyle(cellstyle);
        return cellstyle;
    }
    public static CellStyle getStyleX(HSSFWorkbook workbook) {
        CellStyle cellstyle=workbook.createCellStyle();
        cellstyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        Font font=workbook.createFont();//字体
        font.setFontName("宋体");//字体
        font.setFontHeightInPoints((short)10);//字号
        font.setBold(true);//加粗
        cellstyle.setFont(font);
        setBorderStyle(cellstyle);
        return cellstyle;
    }

    /**
     * 获取默认的cell表格样式，加边框，水平居中，垂直居中
     * @param workbook
     * @return
     */
    public static CellStyle getDefaultCellStyle(XSSFWorkbook workbook) {
        CellStyle style=workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        setBorderStyle(style);
        Font font=workbook.createFont();//字体
        font.setFontName("宋体");//字体
        font.setFontHeightInPoints((short)10);//字号
//        font.setBold(true);//加粗
        style.setFont(font);
        return style;
    }

    public static CellStyle getDefaultCellStyleDigital(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        setBorderStyle(style);
        DataFormat df = workbook.createDataFormat(); // 此处设置数据格式
        style.setDataFormat(df.getFormat("0.00_ "));// 关键是'_ '，空格不要忘记  数值型单元格
        return style;
    }

    public static CellStyle getDefaultCellStyleX(HSSFWorkbook workbook) {
        CellStyle style=workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        setBorderStyle(style);
        return style;
    }

    /**
     * 边框样式
     * @param style
     */
    public static void setBorderStyle(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN); //下边框
        style.setBorderLeft(BorderStyle.THIN);//左边框
        style.setBorderTop(BorderStyle.THIN);//上边框
        style.setBorderRight(BorderStyle.THIN);//右边框
    }

    /**
     * 奇数行
     * 背景颜色为黄色
     * @param style
     */
    public static void setCellStyleYellow(CellStyle style) {
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }
    /**
     * 偶数行
     * 背景颜色为LIME
     * @param style
     */
    public static void setCellStyleLime(CellStyle style) {
        style.setFillForegroundColor(IndexedColors.LIME.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    public static void setCellStyleGrey(CellStyle style) {
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }
    /**
     * 字体设置红色
     * @param workbook
     * @param style
     */
    public static void setFontRedColor(XSSFWorkbook workbook, CellStyle style) {
        Font font=workbook.createFont();//字体
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
    }
}
