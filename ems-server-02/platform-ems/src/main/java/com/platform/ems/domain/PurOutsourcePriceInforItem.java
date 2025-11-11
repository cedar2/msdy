package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 加工采购价格记录明细(报价/核价/议价)对象 s_pur_outsource_price_infor_item
 *
 * @author c
 * @date 2022-04-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_outsource_price_infor_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurOutsourcePriceInforItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-加工采购价格记录明细信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价格记录明细信息")
    private Long outsourcePriceInforItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] outsourcePriceInforItemSidList;
    /**
     * 系统SID-加工采购价格记录
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-加工采购价格记录")
    private Long outsourcePriceInforSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "加工采购价格记录主表编号")
    private Long outsourcePriceInforCode;

    /**
     * 阶梯类型（数据字典的键值）
     */
    @Excel(name = "阶梯类型")
    @ApiModelProperty(value = "阶梯类型（数据字典的键值）")
    private String cascadeType;

    /**
     * 价格录入方式（数据字典的键值）
     */
    @Excel(name = "价格录入方式")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    private String priceEnterMode;

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
     * 采购价(含税)
     */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /**
     * 采购价(不含税)
     */
    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /**
     * 币种（数据字典的键值）
     */
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    /**
     * 货币单位（数据字典的键值）
     */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    @Excel(name = "基本计量单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位名称")
    private String unitBaseName;

    /**
     * 采购价计量单位（数据字典的键值）
     */
    @ApiModelProperty(value = "采购价计量单位编码")
    private String unitPrice;

    @Excel(name = "采购价计量单位")
    @TableField(exist = false)
    @ApiModelProperty(value = "采购价计量单位名称")
    private String unitPriceName;

    /**
     * 单位换算比例（采购价单位/基本单位）
     */
    @Excel(name = "单位换算比例（采购价单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /**
     * 是否递增减价（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否递增减价")
    @ApiModelProperty(value = "是否递增减价（数据字典的键值或配置档案的编码）")
    private String isRecursionPrice;

    /**
     * 报价更新人账号（用户名称）
     */
    @Excel(name = "报价更新人账号（用户名称）")
    @ApiModelProperty(value = "报价更新人账号（用户名称）")
    private String quoteUpdaterAccount;

    /**
     * 报价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价更新时间")
    private Date quoteUpdateDate;

    /**
     * 核定价更新人账号（用户名称）
     */
    @Excel(name = "核定价更新人账号（用户名称）")
    @ApiModelProperty(value = "核定价更新人账号（用户名称）")
    private String checkUpdaterAccount;

    /**
     * 核定价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核定价更新时间")
    private Date checkUpdateDate;

    /**
     * 确认价更新人账号（用户名称）
     */
    @Excel(name = "确认价更新人账号（用户名称）")
    @ApiModelProperty(value = "确认价更新人账号（用户名称）")
    private String confirmUpdaterAccount;

    /**
     * 确认价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认价更新时间")
    private Date confirmUpdateDate;

    /**
     * 采购价更新人账号（用户名称）
     */
    @Excel(name = "采购价更新人账号（用户名称）")
    @ApiModelProperty(value = "采购价更新人账号（用户名称）")
    private String purchaseUpdaterAccount;

    /**
     * 采购价更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "采购价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购价更新时间")
    private Date purchaseUpdateDate;

    /**
     * 备注(报价)
     */
    @Excel(name = "备注(报价)")
    @ApiModelProperty(value = "备注(报价)")
    private String remarkQuote;

    /**
     * 备注(核价)
     */
    @Excel(name = "备注(核价)")
    @ApiModelProperty(value = "备注(核价)")
    private String remarkCheck;

    /**
     * 备注(确认价)
     */
    @Excel(name = "备注(确认价)")
    @ApiModelProperty(value = "备注(确认价)")
    private String remarkConfirm;

    /**
     * 备注(采购价)
     */
    @Excel(name = "备注(采购价)")
    @ApiModelProperty(value = "备注(采购价)")
    private String remarkPurchase;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人")
	@TableField(exist = false)
	@ApiModelProperty(value = "创建人昵称（用户昵称）")
	private String creatorAccountName;

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
    @Excel(name = "更新人")
	@TableField(exist = false)
	@ApiModelProperty(value = "更新人昵称（用户昵称）")
	private String updaterAccountName;

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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人")
	@TableField(exist = false)
	@ApiModelProperty(value = "确认人昵称（用户昵称）")
	private String confirmerAccountName;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
