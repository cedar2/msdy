package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import lombok.experimental.Accessors;

/**
 * 采购价格记录明细(报价/核价/议价)对象 s_pur_price_infor_item
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_price_infor_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurPriceInforItem extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-采购价格记录明细信息 */
    @TableId
    @ApiModelProperty(value = "系统SID-采购价格记录明细信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long priceInforItemSid;

    /** 系统SID-采购价格记录 */
    @Excel(name = "系统SID-采购价格记录")
    @ApiModelProperty(value = "系统SID-采购价格记录")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long priceInforSid;

    /** 阶梯类型（数据字典的键值） */
    @Excel(name = "阶梯类型（数据字典的键值）")
    @ApiModelProperty(value = "阶梯类型（数据字典的键值）")
    private String cascadeType;

    /** 价格录入方式（数据字典的键值） */
    @Excel(name = "价格录入方式（数据字典的键值）")
    @ApiModelProperty(value = "价格录入方式（数据字典的键值）")
    private String priceEnterMode;

    /** 报价(含税) */
    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    /** 报价(不含税) */
    @Excel(name = "报价(不含税)")
    @ApiModelProperty(value = "报价(不含税)")
    private BigDecimal quotePrice;

    /** 核定价(含税) */
    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    /** 核定价(不含税) */
    @Excel(name = "核定价(不含税)")
    @ApiModelProperty(value = "核定价(不含税)")
    private BigDecimal checkPrice;

    /** 确认价(含税) */
    @Excel(name = "确认价(含税)")
    @ApiModelProperty(value = "确认价(含税)")
    private BigDecimal confirmPriceTax;

    /** 确认价(不含税) */
    @Excel(name = "确认价(不含税)")
    @ApiModelProperty(value = "确认价(不含税)")
    private BigDecimal confirmPrice;

    /** 采购价(含税) */
    @Excel(name = "采购价(含税)")
    @ApiModelProperty(value = "采购价(含税)")
    private BigDecimal purchasePriceTax;

    /** 采购价(不含税) */
    @Excel(name = "采购价(不含税)")
    @ApiModelProperty(value = "采购价(不含税)")
    private BigDecimal purchasePrice;

    /** 税率（存值，即：不含百分号，如20%，就存0.2） */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private String taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRateValue;

    /** 基准量 */
    @Excel(name = "基准量")
    @ApiModelProperty(value = "基准量")
    private BigDecimal referQuantity;

    /** 递增量 */
    @Excel(name = "递增量")
    @ApiModelProperty(value = "递增量")
    private BigDecimal increQuantity;

    /** 递减量 */
    @Excel(name = "递减量")
    @ApiModelProperty(value = "递减量")
    private BigDecimal decreQuantity;

    /** 价格最小起算量 */
    @Excel(name = "价格最小起算量")
    @ApiModelProperty(value = "价格最小起算量")
    private BigDecimal priceMinQuantity;

    /** 递增减计量单位（数据字典的键值） */
    @Excel(name = "递增减计量单位（数据字典的键值）")
    @ApiModelProperty(value = "递增减计量单位（数据字典的键值）")
    private String unitRecursion;

    /** 取整方式(递增减)（数据字典的键值） */
    @Excel(name = "取整方式(递增减)（数据字典的键值）")
    @ApiModelProperty(value = "取整方式(递增减)（数据字典的键值）")
    private String roundingType;

    /** 递增报价(含税) */
    @Excel(name = "递增报价(含税)")
    @ApiModelProperty(value = "递增报价(含税)")
    private BigDecimal increQuoPriceTax;

    /** 递增报价(不含税) */
    @Excel(name = "递增报价(不含税)")
    @ApiModelProperty(value = "递增报价(不含税)")
    private BigDecimal increQuoPrice;

    /** 递增核定价(含税) */
    @Excel(name = "递增核定价(含税)")
    @ApiModelProperty(value = "递增核定价(含税)")
    private BigDecimal increChePriceTax;

    /** 递增核定价(不含税) */
    @Excel(name = "递增核定价(不含税)")
    @ApiModelProperty(value = "递增核定价(不含税)")
    private BigDecimal increChePrice;

    /** 递增确认价(含税) */
    @Excel(name = "递增确认价(含税)")
    @ApiModelProperty(value = "递增确认价(含税)")
    private BigDecimal increConfPriceTax;

    /** 递增确认价(不含税) */
    @Excel(name = "递增确认价(不含税)")
    @ApiModelProperty(value = "递增确认价(不含税)")
    private BigDecimal increConfPrice;

    /** 递增采购价(含税) */
    @Excel(name = "递增采购价(含税)")
    @ApiModelProperty(value = "递增采购价(含税)")
    private BigDecimal increPurPriceTax;

    /** 递增采购价(不含税) */
    @Excel(name = "递增采购价(不含税)")
    @ApiModelProperty(value = "递增采购价(不含税)")
    private BigDecimal increPurPrice;

    @ApiModelProperty(value = "是否递增减价")
    private String isRecursionPrice;


    /** 递减报价(含税) */
    @Excel(name = "递减报价(含税)")
    @ApiModelProperty(value = "递减报价(含税)")
    private BigDecimal decreQuoPriceTax;

    /** 递减报价(不含税) */
    @Excel(name = "递减报价(不含税)")
    @ApiModelProperty(value = "递减报价(不含税)")
    private BigDecimal decreQuoPrice;

    /** 递减核定价(含税) */
    @Excel(name = "递减核定价(含税)")
    @ApiModelProperty(value = "递减核定价(含税)")
    private BigDecimal decreChePriceTax;

    /** 递减核定价(不含税) */
    @Excel(name = "递减核定价(不含税)")
    @ApiModelProperty(value = "递减核定价(不含税)")
    private BigDecimal decreChePrice;

    /** 递减确认价(含税) */
    @Excel(name = "递减确认价(含税)")
    @ApiModelProperty(value = "递减确认价(含税)")
    private BigDecimal decreConfPriceTax;

    /** 递减确认价(不含税) */
    @Excel(name = "递减确认价(不含税)")
    @ApiModelProperty(value = "递减确认价(不含税)")
    private BigDecimal decreConfPrice;

    /** 递减采购价(含税) */
    @Excel(name = "递减采购价(含税)")
    @ApiModelProperty(value = "递减采购价(含税)")
    private BigDecimal decrePurPriceTax;

    /** 递减采购价(不含税) */
    @Excel(name = "递减采购价(不含税)")
    @ApiModelProperty(value = "递减采购价(不含税)")
    private BigDecimal decrePurPrice;

    /** 币种（数据字典的键值） */
    @Excel(name = "币种（数据字典的键值）")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    /** 货币单位（数据字典的键值） */
    @Excel(name = "货币单位（数据字典的键值）")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    /** 基本计量单位（数据字典的键值） */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /** 采购价计量单位（数据字典的键值） */
    @Excel(name = "采购价计量单位（数据字典的键值）")
    @ApiModelProperty(value = "采购价计量单位（数据字典的键值）")
    private String unitPrice;

    /** 单位换算比例（采购价单位/基本单位） */
    @Excel(name = "单位换算比例（采购价单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRate;

    /** 报价更新人账号（用户名称） */
    @Excel(name = "报价更新人账号（用户名称）")
    @ApiModelProperty(value = "报价更新人账号（用户名称）")
    private String quoteUpdaterAccount;

    /** 报价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "报价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "报价更新时间")
    private Date quoteUpdateDate;

    /** 核定价更新人账号（用户名称） */
    @Excel(name = "核定价更新人账号（用户名称）")
    @ApiModelProperty(value = "核定价更新人账号（用户名称）")
    private String checkUpdaterAccount;

    /** 核定价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "核定价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "核定价更新时间")
    private Date checkUpdateDate;

    /** 确认价更新人账号（用户名称） */
    @Excel(name = "确认价更新人账号（用户名称）")
    @ApiModelProperty(value = "确认价更新人账号（用户名称）")
    private String confirmUpdaterAccount;

    /** 确认价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认价更新时间")
    private Date confirmUpdateDate;

    /** 采购价更新人账号（用户名称） */
    @Excel(name = "采购价更新人账号（用户名称）")
    @ApiModelProperty(value = "采购价更新人账号（用户名称）")
    private String purchaseUpdaterAccount;

    /** 采购价更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "采购价更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "采购价更新时间")
    private Date purchaseUpdateDate;

    /** 有效期（起） */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    private Date startDate;

    /** 有效期（止） */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（止）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    private Date endDate;

    /** 备注(报价) */
    @Excel(name = "备注(报价)")
    @ApiModelProperty(value = "备注(报价)")
    private String remarkQuote;

    /** 备注(核价) */
    @Excel(name = "备注(核价)")
    @ApiModelProperty(value = "备注(核价)")
    private String remarkCheck;

    /** 备注(确认价) */
    @Excel(name = "备注(确认价)")
    @ApiModelProperty(value = "备注(确认价)")
    private String remarkConfirm;

    /** 备注(采购价) */
    @Excel(name = "备注(采购价)")
    @ApiModelProperty(value = "备注(采购价)")
    private String remarkPurchase;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号（用户名称） */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统（数据字典的键值） */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;



}
