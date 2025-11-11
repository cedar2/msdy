package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import sun.awt.image.BufferedImageGraphicsConfig;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存报表响应实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryLocationResponse implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value ="sku1名称")
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value ="sku2名称")
    private String sku2Name;

    @Excel(name = "库存量")
    @ApiModelProperty(value = "非限制库存量（非限制使用的库存）")
    private BigDecimal unlimitedQuantity;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @Excel(name = "仓库")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @Excel(name = "库位")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @Excel(name = "预留库存量")
    @ApiModelProperty(value = "预留库存")
    private BigDecimal obligateQuantity;

    @Excel(name = "可用库存量")
    @ApiModelProperty(value = "可用库存")
    private BigDecimal ableQuantity;

    @Excel(name = "客供量")
    @ApiModelProperty(value = "客户来料库存量（客户提供的来料库存）")
    private BigDecimal customerSubcontractQuantity;

    @Excel(name = "客户寄售库存量")
    @ApiModelProperty(value = "客户寄售库存量（放在客户方的用于寄售的库存）")
    private BigDecimal customerConsignQuantity;

    @Excel(name = "甲供量")
    @ApiModelProperty(value = "供应商委外库存量（给供应商用于委外加工的库存）")
    private BigDecimal vendorSubcontractQuantity;

    @Excel(name = "供应商寄售量")
    @ApiModelProperty(value = "供应商寄售库存量（供应商提供的寄售库存）")
    private BigDecimal vendorConsignQuantity;

    @Excel(name = "成本价(含税)")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "成本价(含税)")
    private BigDecimal priceCostTax;

    @Excel(name = "成本金额(元)", scale = 2)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "成本金额(元)")
    private BigDecimal amountPriceCostTax;

    @ApiModelProperty(value = "物料类型")
    @Excel(name = "物料类型")
    private String materialTypeName;

    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

    @Excel(name = "使用频率标识", dictType = "usage_frequency_flag")
    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlag;

    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlagName;

    @Excel(name = "加权平均价")
    @ApiModelProperty(value ="价格")
    private BigDecimal price;

    @Excel(name = "仓库编码")
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @Excel(name = "库位编码")
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "首次更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "首次更新库存时间")
    private Date firstUpdateStockDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "最近更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近更新库存时间")
    private Date latestUpdateStockDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "最近盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近盘点时间")
    private Date latestCountDate;

}
