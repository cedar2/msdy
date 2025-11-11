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
 * 加工议价单明细报表导出实体
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
public class PurOutsourceQuoteBargainResponse implements Serializable {

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

    @ApiModelProperty(value = "加工项")
    @Excel(name = "加工项")
    private String processName;

    @Excel(name = "价格维度",dictType = "s_price_dimension")
    @ApiModelProperty(value = "价格维度（数据字典的键值）")
    private String priceDimension;

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

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单")
    private Long outsourceInquirySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工询价单明细")
    private Long outsourceInquiryItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工询价单编码")
    private Long outsourceInquiryCode;

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
