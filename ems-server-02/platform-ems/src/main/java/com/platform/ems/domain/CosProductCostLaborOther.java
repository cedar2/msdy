package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品成本核算-工价成本明细对象 s_cos_product_cost_labor_other
 *
 * @author c
 * @date 2021-07-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_product_cost_labor_other")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CosProductCostLaborOther extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-成品/半成品成本核算工价成本明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-成品/半成品成本核算工价成本明细")
    private Long productCostLaborOtherSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] productCostLaborOtherSidList;


    /**
     * 系统SID-成品/半成品成本核算工价成本明细
     */
    @Excel(name = "系统SID-成品/半成品成本核算工价成本明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-成品/半成品成本核算工价成本明细")
    private Long productCostLaborSid;

    /**
     * 成本工价其它项的名称
     */
    @Excel(name = "成本工价其它项的名称")
    @ApiModelProperty(value = "成本工价其它项的名称")
    private String otherItemName;

    /**
     * 成本价(含税)
     */
    @Excel(name = "成本价(含税)")
    @ApiModelProperty(value = "成本价(含税)")
    private BigDecimal innerPriceTax;

    /**
     * 成本价(不含税)
     */
    @Excel(name = "成本价(不含税)")
    @ApiModelProperty(value = "成本价(不含税)")
    private BigDecimal innerPrice;

    /**
     * 报价(含税)
     */
    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    /**
     * 报价(不含税)
     */
    @Excel(name = "报价(不含税)")
    @ApiModelProperty(value = "报价(不含税)")
    private BigDecimal quotePrice;

    /**
     * 核定价(含税)
     */
    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    /**
     * 核定价(不含税)
     */
    @Excel(name = "核定价(不含税)")
    @ApiModelProperty(value = "核定价(不含税)")
    private BigDecimal checkPrice;

    /**
     * 确认价(含税)
     */
    @Excel(name = "确认价(含税)")
    @ApiModelProperty(value = "确认价(含税)")
    private BigDecimal confirmPriceTax;

    /**
     * 确认价(不含税)
     */
    @Excel(name = "确认价(不含税)")
    @ApiModelProperty(value = "确认价(不含税)")
    private BigDecimal confirmPrice;

    /**
     * 特殊工艺分类sid
     */
    @Excel(name = "特殊工艺分类sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊工艺分类sid")
    private Long specialCraftType;

    /**
     * 部位
     */
    @Excel(name = "部位")
    @ApiModelProperty(value = "部位")
    private String position;

    /**
     * 规格
     */
    @Excel(name = "规格")
    @ApiModelProperty(value = "规格")
    private String specification;

    /**
     * 工艺加工商
     */
    @Excel(name = "工艺加工商")
    @ApiModelProperty(value = "工艺加工商")
    private String processVendor;

    /**
     * 工艺说明
     */
    @Excel(name = "工艺说明")
    @ApiModelProperty(value = "工艺说明")
    private String processDesc;

    /**
     * 系统SID-工序（加工项）
     */
    @Excel(name = "系统SID-工序（加工项）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序（加工项）")
    private Long processSid;

    /**
     * 备注（内部成本价）
     */
    @Excel(name = "备注（内部成本价）")
    @ApiModelProperty(value = "备注（内部成本价）")
    private String remarkInner;

    /**
     * 备注（报价）
     */
    @Excel(name = "备注（报价）")
    @ApiModelProperty(value = "备注（报价）")
    private String remarkQuote;

    /**
     * 备注（核定价）
     */
    @Excel(name = "备注（核定价）")
    @ApiModelProperty(value = "备注（核定价）")
    private String remarkCheck;

    /**
     * 备注（确认价）
     */
    @Excel(name = "备注（确认价）")
    @ApiModelProperty(value = "备注（确认价）")
    private String remarkConfirm;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private int serialNum;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "系统SID-成品/半成品成本核算")
    private Long productCostSid;

    @TableField(exist = false)
    private String laborTypeItemName;

    @TableField(exist = false)
    private String laborTypeName;

    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @ApiModelProperty(value = "生产方式（数据字典的键值或配置档案的编码），自产、外发")
    @TableField(exist = false)
    private String productionMode;

    /**
     * 风险系数（存值，即：不含百分号，如20%，就存0.2）
     */

    @Excel(name = "确认风险系数（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "风险系数（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal confirmFengxianRate;


    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */

    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;


    /**
     * 报价服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）
     */

    @Excel(name = "报价服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    @ApiModelProperty(value = "报价服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    private BigDecimal quoteFuwuRate;


    /**
     * 核定服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）
     */

    @Excel(name = "核定服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    @ApiModelProperty(value = "核定服务费率（存值，即：不含百分号，如20%，就存0.2）（冗余）")
    private BigDecimal checkFuwuRate;


    /**
     * 确认服务费率（存值，即：不含百分号，如20%，就存0.2）
     */

    @Excel(name = "确认服务费率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "确认服务费率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal confirmFuwuRate;


    /**
     * 风险金额
     */

    @Excel(name = "风险金额")
    @ApiModelProperty(value = "风险金额")
    private BigDecimal fengxianCostTax;


    /**
     * 报价服务费（冗余）
     */

    @Excel(name = "报价服务费（冗余）")
    @ApiModelProperty(value = "报价服务费（冗余）")
    private BigDecimal quoteFuwuCostTax;


    /**
     * 核定服务费（冗余）
     */

    @Excel(name = "核定服务费（冗余）")
    @ApiModelProperty(value = "核定服务费（冗余）")
    private BigDecimal checkFuwuCostTax;


    /**
     * 确认服务费
     */

    @Excel(name = "确认服务费")
    @ApiModelProperty(value = "确认服务费")
    private BigDecimal confirmFuwuCostTax;

    @ApiModelProperty(value = "风险金额(报价)")
    private String detailQuoteFengxianCostTax;

    @ApiModelProperty(value = "风险金额(成本价)")
    private String detailInnerFengxianCostTax;

    @ApiModelProperty(value = " 风险金额(核定价)")
    private String detailCheckFengxianCostTax ;

    @ApiModelProperty(value = " 风险金额(确认价)")
    private String detailConfirmFengxianCostTax ;

    @ApiModelProperty(value = " 服务费(报价)")
    private String detailQuoteFuwuCostTax ;

    @ApiModelProperty(value = "服务费(成本价)")
    private String detailInnerFuwuCostTax ;

    @ApiModelProperty(value = "服务费(成本价)")
    private String detailCheckFuwuCostTax;

    @ApiModelProperty(value = "服务费(确认价)")
    private String detailConfirmFuwuCostTax ;

}
