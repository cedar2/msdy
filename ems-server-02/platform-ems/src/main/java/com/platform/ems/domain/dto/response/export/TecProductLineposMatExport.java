package com.platform.ems.domain.dto.response.export;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 商品导出线用量
 *
 * @author chenkw
 * @date 2023-06-05
 */
@Data
@Accessors(chain = true)
@ApiModel
public class TecProductLineposMatExport {

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码(款号)")
    private String productCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String productName;

    @Excel(name = "款颜色")
    @ApiModelProperty(value = "款颜色名称")
    private String productSkuName;

    @Excel(name = "基本计量单位(商品)")
    @ApiModelProperty(value = "基本计量单位(商品)")
    private String productUnitBaseName;

    @Excel(name = "线部位名称")
    @ApiModelProperty(value = "线部位名称")
    private String linePositionName;

    @Excel(name = "线部位类别", dictType = "s_line_position_category")
    @ApiModelProperty(value = "线部位类别")
    private String linePositionCategory;

    @Excel(name = "用量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    @Excel(name = "BOM用量单位")
    @ApiModelProperty(value = "BOM用量单位")
    private String quantityUnitName;

    @Excel(name = "物料编码")
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Excel(name = "料颜色")
    @ApiModelProperty(value = "料颜色名称")
    private String materialSkuName;

    @Excel(name = "基本计量单位(物料)")
    @ApiModelProperty(value = "基本计量单位(物料)")
    private String unitBaseName;

    @Excel(name = "度量方法说明")
    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;
}
