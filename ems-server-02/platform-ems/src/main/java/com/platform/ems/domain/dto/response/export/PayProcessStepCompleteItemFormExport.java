package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 导出道序计薪量统计报表 PayProcessStepCompleteItemFormExport
 *
 * @author chenkaiwen
 * @date 2022-09-05
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PayProcessStepCompleteItemFormExport {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂")
    private Long plantSid;

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    @Excel(name = "商品编码(款号)")
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @Excel(name = "道序序号", scale = 2)
    @ApiModelProperty(value = "序号(商品道序)")
    private BigDecimal sort;

    @Excel(name = "道序名称")
    @ApiModelProperty(value = "商品道序名称")
    private String processStepName;

    @ApiModelProperty(value = "道序工价(元)")
    private BigDecimal price;

    @ApiModelProperty(value = "倍率(道序)")
    private BigDecimal priceRate;

    @ApiModelProperty(value = "完工调价倍率(道序)")
    private BigDecimal wangongPriceRate;

    @ApiModelProperty(value = "当天计薪量")
    private BigDecimal completeQuantity;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "金额(元)")
    private BigDecimal money;

    @Excel(name = "道序工价(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "道序工价(元)")
    private String priceString;

    @Excel(name = "工价倍率", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "倍率(道序)")
    private String priceRateString;

    @Excel(name = "调价率", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "完工调价倍率(道序)")
    private String wangongPriceRateString;

    @Excel(name = "累计计薪量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "当天计薪量")
    private String completeQuantityString;

    @ApiModelProperty(value = "实裁量")
    private BigDecimal shicaiQuantity;

    @Excel(name = "实裁量")
    @ApiModelProperty(value = "实裁量")
    private String shicaiQuantityString;

    @Excel(name = "金额(元)", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "金额(元)")
    private String moneyString;

    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门（数据字典的键值或配置档案的编码）")
    private String departmentName;

    @Excel(name = "商品工价类型", dictType = "s_product_price_type")
    @ApiModelProperty(value = "商品工价类型（数据字典的键值或配置档案的编码）")
    private String productPriceType;

    @Excel(name = "计薪完工类型", dictType = "s_jixin_wangong_type")
    @ApiModelProperty(value = "计薪完工类型（数据字典的键值或配置档案的编码）")
    private String jixinWangongType;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String productName;

    @Excel(name = "道序编码")
    @ApiModelProperty(value = "商品道序编码")
    private String processStepCode;
}
