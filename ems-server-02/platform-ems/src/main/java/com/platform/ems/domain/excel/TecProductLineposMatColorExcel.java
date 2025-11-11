package com.platform.ems.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.math.BigDecimal;

/**
 * 商品线部位-线小计 bom 详情查看线用量导出 的线小计 sheet页
 *
 * @author chenkw
 * @date 2023-03-23
 */
@Data
@Accessors(chain = true)
@ApiModel
public class TecProductLineposMatColorExcel {

    @ExcelProperty("款号")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "款号")
    private String productCode;

    @ExcelProperty("款颜色")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "款颜色")
    private String productSkuName;

    @ExcelProperty("物料编码")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @ExcelProperty("物料名称")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ExcelProperty("料颜色")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "料颜色")
    private String materialSkuName;

    @ExcelProperty("用量")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    @ExcelProperty("BOM用量单位")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "用量计量单位名称")
    private String quantityUnitName;

    @ExcelProperty("基本计量单位")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    @ExcelProperty("款名称")
    @HeadFontStyle(fontHeightInPoints = 12)
    @HeadStyle(fillBackgroundColor = 23, shrinkToFit = true,
            leftBorderColor = 23, rightBorderColor = 23, bottomBorderColor = 23)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
            borderRight = BorderStyle.THIN, rightBorderColor = 23,
            borderBottom = BorderStyle.THIN, bottomBorderColor = 23)
    @ApiModelProperty(value = "款名称")
    private String productName;

}
