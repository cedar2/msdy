package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 商品成本物料明细报表
 */
@Data
@Accessors( chain = true)
public class CosProductCostMaterialResponse {

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String materialCode;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Excel(name = "是否主面料", readConverterExp = "1=是")
    @ApiModelProperty(value = "是否主面料")
    private String isMainFabric;

    @ApiModelProperty(value = "款颜色1")
    @Excel(name = "款颜色1")
    private String sku1Name;


    @Excel(name = "部位")
    @ApiModelProperty(value = "部位")
    private String positionName;


    @Excel(name = "内部用量")
    @ApiModelProperty(value = "内部用量")
    private BigDecimal innerQuantity;


    /** 内部损耗率（%） */
    @Excel(name = "内部损耗率（%）")
    @ApiModelProperty(value = "内部损耗率（%）")
    private BigDecimal innerLossRate;

    /** 报价用量 */
    @Excel(name = "报价用量")
    @ApiModelProperty(value = "报价用量")
    private BigDecimal quoteQuantity;

    /** 报价损耗率（%） */
    @Excel(name = "报价损耗率（%）")
    @ApiModelProperty(value = "报价损耗率（%）")
    private BigDecimal quoteLossRate;

    /** 确认用量 */
    @Excel(name = "客方确认用量")
    @ApiModelProperty(value = "客方确认用量")
    private BigDecimal confirmQuantity;

    /** 确认损耗率（%） */
    @Excel(name = "客方确认损耗率（%）")
    @ApiModelProperty(value = "客方确认损耗率（%）")
    private BigDecimal confirmLossRate;


    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal priceTax;


    @Excel(name = "报价金额(含税)")
    @ApiModelProperty(value = "报价金额(含税)")
    private BigDecimal quoteAmount;

    @Excel(name = "确认金额(含税)")
    @ApiModelProperty(value = "确认金额(含税)")
    private BigDecimal confirmAmount;

    @Excel(name = "内部成本金额(含税)")
    @ApiModelProperty(value = "内部成本金额(含税)")
    private BigDecimal innerPrice;


    @Excel(name = "损耗取整方式")
    @ApiModelProperty(value = "损耗取整方式")
    private String roundingType;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本单位")
    private String unitBaseName;

    @Excel(name = "计价量")
    @ApiModelProperty(value = "计价量")
    private BigDecimal priceQuantity;

    @Excel(name = "BOM用量单位")
    @ApiModelProperty(value = "BOM用量单位")
    private String unitQuantityName;

    @Excel(name = "计价单位")
    @ApiModelProperty(value = "计价单位")
    private String unitPriceName;

    @Excel(name = "供应商(默认)")
    @ApiModelProperty(value = "供应商(默认)")
    private String vendorName;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码")
    private String supplierProductCode;

    @Excel(name = "采购类型")
    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;


    @Excel(name = "幅宽(厘米)")
    @ApiModelProperty(value = "幅宽")
    private String width;

    @Excel(name = "克重")
    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @Excel(name = "成分")
    @ApiModelProperty(value = "成分")
    private String composition;

    @Excel(name = "纱支")
    @ApiModelProperty(value = "纱支")
    private String yarnCount;

    @Excel(name = "密度")
    @ApiModelProperty(value = "密度")
    private String density;

    @Excel(name = "规格")
    @ApiModelProperty(value = "规格")
    private String specificationSize;


    @Excel(name = "型号")
    @ApiModelProperty(value = "型号")
    private String modelSize;

    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

}
