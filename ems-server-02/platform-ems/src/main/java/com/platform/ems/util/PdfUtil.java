package com.platform.ems.util;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * @Author qhq
 * @create 2021/11/11 17:02
 */
public class PdfUtil {

	public static Font titlefont;
	public static Font headfont;
	public static Font keyfont;
	public static Font textfont;

	public static int maxWidth = 520;

	static {
		try {
			// 不同字体（这里定义为同一种字体：包含不同字号、不同style）
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			titlefont = new Font(bfChinese, 20, Font.BOLD);
			headfont = new Font(bfChinese, 14, Font.BOLD);
			keyfont = new Font(bfChinese, 10, Font.BOLD);
			textfont = new Font(bfChinese, 8, Font.NORMAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void instance (Font title, Font head, Font key, Font text) {
		title = textfont;
		head = headfont;
		key = keyfont;
		text = textfont;
	}

	/**------------------------创建表格单元格的方法start----------------------------*/
	/**
	 * 创建单元格(指定字体)
	 * 无边框
	 *
	 * @param value
	 * @param font
	 * @return
	 */
	public static PdfPCell createCell (String value, Font font) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPhrase(new Phrase(value, font));
		cell.setBorderWidth(0);
		return cell;
	}

	/**
	 * 创建单元格（指定字体、水平..）
	 *
	 * @param value
	 * @param font
	 * @param align
	 * @return
	 */
	public static PdfPCell createCell (String value, Font font, int align) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(align);
		cell.setPhrase(new Phrase(value, font));
		return cell;
	}

	/**
	 * 创建单元格(指定字体)
	 * 边框值自定义
	 *
	 * @param value
	 * @param font
	 * @param borderWidth
	 * @return
	 */
	public static PdfPCell createCellSetBorderWidth (String value, Font font, int borderWidth) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPhrase(new Phrase(value, font));
		cell.setBorderWidth(borderWidth);
		return cell;
	}

	/**
	 * 创建单元格（指定字体、水平居..、单元格跨x列合并）
	 *
	 * @param value
	 * @param font
	 * @param align
	 * @param colspan
	 * @return
	 */
	public static PdfPCell createCell (String value, Font font, int align, int colspan) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(align);
		cell.setColspan(colspan);
		cell.setPhrase(new Phrase(value, font));
		return cell;
	}

	/**
	 * 创建单元格（指定字体、水平居..、单元格跨x列合并、设置单元格内边距）
	 *
	 * @param value
	 * @param font
	 * @param align
	 * @param colspan
	 * @param boderFlag
	 * @return
	 */
	public static PdfPCell createCell (String value, Font font, int align, int colspan, boolean boderFlag) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(align);
		cell.setColspan(colspan);
		cell.setPhrase(new Phrase(value, font));
		cell.setPadding(3.0f);
		if (!boderFlag) {
			cell.setBorder(0);
			cell.setPaddingTop(15.0f);
			cell.setPaddingBottom(8.0f);
		} else if (boderFlag) {
			cell.setBorder(0);
			cell.setPaddingTop(0.0f);
			cell.setPaddingBottom(15.0f);
		}
		return cell;
	}

	/**
	 * 创建单元格（指定字体、水平..、边框宽度：0表示无边框、内边距）
	 *
	 * @param value
	 * @param font
	 * @param align
	 * @param borderWidth
	 * @param paddingSize
	 * @param flag
	 * @return
	 */
	public static PdfPCell createCell (String value, Font font, int align, float[] borderWidth, float[] paddingSize, boolean flag) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(align);
		cell.setPhrase(new Phrase(value, font));
		cell.setBorderWidthLeft(borderWidth[0]);
		cell.setBorderWidthRight(borderWidth[1]);
		cell.setBorderWidthTop(borderWidth[2]);
		cell.setBorderWidthBottom(borderWidth[3]);
		cell.setPaddingTop(paddingSize[0]);
		cell.setPaddingBottom(paddingSize[1]);
		if (flag) {
			cell.setColspan(2);
		}
		return cell;
	}
	/**------------------------创建表格单元格的方法end----------------------------*/

	/**--------------------------创建表格的方法start------------------- ---------*/
	/**
	 * 创建默认列宽，指定列数、水平(居中、右、左)的表格
	 *
	 * @param colNumber
	 * @param align
	 * @return
	 */
	public PdfPTable createTable (int colNumber, int align) {
		PdfPTable table = new PdfPTable(colNumber);
		try {
			table.setTotalWidth(520);
			table.setLockedWidth(true);
			table.setHorizontalAlignment(align);
			table.getDefaultCell().setBorder(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return table;
	}

	/**
	 * 创建指定列宽、列数的表格
	 *
	 * @param widths
	 * @return
	 */
	public PdfPTable createTable (float[] widths) {
		PdfPTable table = new PdfPTable(widths);
		try {
			table.setTotalWidth(520);
			table.setLockedWidth(true);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setBorder(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return table;
	}

	/**
	 * 创建空白的表格
	 *
	 * @return
	 */
	public PdfPTable createBlankTable () {
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(0);
		table.addCell(createCell("", keyfont));
		table.setSpacingAfter(20.0f);
		table.setSpacingBefore(20.0f);
		return table;
	}

	/**
	 * 指定列數，寬度
	 * @param column
	 * @param tableWidth
	 * @return
	 * @throws Exception
	 */
	public static PdfPTable getPdfPTable (int column, int[] tableWidth) throws Exception {
		PdfPTable table = new PdfPTable(column);
		table.setWidths(tableWidth);
		table.getDefaultCell().setBorder(0);
		table.setWidthPercentage(100);
		return table;
	}
/**--------------------------创建表格的方法end------------------- ---------*/


}
