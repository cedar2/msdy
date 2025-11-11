package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购议价单明细报表导出实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
public class PurQuoteBargainResponse implements Serializable {

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @Excel(name = "物料/商品 编码")
    @ApiModelProperty(value = "物料/商品 编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "物料/商品 名称")
    @ApiModelProperty(value = "物料/商品 名称")
    @TableField(exist = false)
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;


    @Excel(name = "价格维度",dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    private String priceDimension;


    @Excel(name = "甲供料方式",dictType = "s_raw_material_mode")
    @ApiModelProperty(value = "甲供料方式（数据字典的键值）")
    private String rawMaterialMode;


    @Excel(name = "采购模式",dictType = "s_price_type")
    @ApiModelProperty(value = "采购模式（数据字典的键值）")
    private String purchaseMode;


    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;


    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;


    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;


    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价更新时间")
    private Date quoteUpdateDate;


    /** 核定价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核定价更新时间")
    private Date checkUpdateDate;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "采购价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购价更新时间")
    private Date confirmDate;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRateName;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;


    @Excel(name = "采购价计量单位")
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值）")
    private String unitPriceName;

    @Excel(name = "单位换算比例")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;


    @Excel(name = "价格录入方式",dictType = "s_price_enter_mode")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    private String priceEnterMode;


    @Excel(name = "递增减计量单位")
    @ApiModelProperty(value = "递增减计量单位（数据字典的键值）")
    private String unitRecursionName;


    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity   ;

    @Excel(name = "价格最小起算量")
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;


    /** 递增报价(含税) */
    @Excel(name = "递增报价(含税)")
    @ApiModelProperty(value = "递增报价(含税)")
    private BigDecimal increQuoPriceTax;


    /** 递减报价(含税) */
    @Excel(name = "递减报价(含税)")
    @ApiModelProperty(value = "递减报价(含税)")
    private BigDecimal decreQuoPriceTax;


    @Excel(name = "递增核定价(含税)")
    @ApiModelProperty(value = "递增核定价(含税)")
    private BigDecimal increChePriceTax;


    @Excel(name = "递减核定价(含税)")
    @ApiModelProperty(value = "递减核定价(含税)")
    private BigDecimal decreChePriceTax;


    @Excel(name = "递增采购价(含税)")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;


    @Excel(name = "递减采购价(含税)")
    @ApiModelProperty(value = "递减采购价(含税)")
    private BigDecimal decrePurPriceTax;


    @Excel(name = "取整方式(递增减)",dictType = "s_rounding_type")
    @ApiModelProperty(value = "取整方式(递增减)（数据字典的键值）")
    private String roundingType;

    @Excel(name = "是否递增减价",dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否递增减价")
    private String isRecursionPrice;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料采购价格记录")
    private Long inquirySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料采购价格记录编码")
    private Long inquiryCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料采购价格记录明细")
    private Long inquiryItemSid;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checker;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String[] checkerList;

    @ApiModelProperty(value = "核价员（用户账号）")
    private String checkerName;

    @ApiModelProperty(value = "询价备注")
    private String remarkRequest;

    @ApiModelProperty(value = "报价备注")
    private String remarkQuote;

    @ApiModelProperty(value = "核价备注")
    private String remarkCheck;

    @ApiModelProperty(value = "议价备注")
    private String remarkConfirm;

    @ApiModelProperty(value = "备注")
    private String remark;

}
