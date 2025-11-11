package com.platform.ems.domain;

import java.io.Serializable;
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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * 采购发票-折扣对象 s_fin_purchase_invoice_discount
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_purchase_invoice_discount")
public class FinPurchaseInvoiceDiscount extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-采购发票折扣
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票折扣")
    private Long purchaseInvoiceDiscountSid;

    /**
     * 系统SID-采购发票
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceSid;

    /**
     * 财务凭证流水sid（台帐流水号/财务账流水号）
     */
    @Excel(name = "财务凭证流水sid（台帐流水号/财务账流水号）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    /**
     * 财务凭证流水code（台帐流水号/财务账流水号）
     */
    @Excel(name = "财务凭证流水code（台帐流水号/财务账流水号）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    /**
     * 财务凭证流水行sid
     */
    @Excel(name = "财务凭证流水行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    /**
     * 财务凭证流水行code
     */
    @Excel(name = "财务凭证流水行code")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行code")
    private Long accountItemCode;

    /**
     * 财务流水类型编码code
     */
    @Excel(name = "财务流水类型编码code")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 本次抵折扣金额(含税)
     */
    @Excel(name = "本次抵折扣金额(含税)")
    @NotNull
    @Digits(integer=11,fraction = 4,message = "本次抵折扣金额整数位上限为11位，小数位上限为4位")
    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTaxDiscount;

    @TableField(exist = false)
    @ApiModelProperty(value = "本次抵折扣金额(含税) 的差值")
    private BigDecimal currencyAmountTaxDifference;

    /**
     * 本次抵折扣金额(不含税)
     */
    @Excel(name = "本次抵折扣金额(不含税)")
    @ApiModelProperty(value = "本次抵折扣金额(不含税)")
    private BigDecimal currencyAmount;

    /**
     * 本次抵折扣税额
     */
    @Excel(name = "本次抵折扣税额")
    @ApiModelProperty(value = "本次抵折扣税额")
    private BigDecimal taxAmount;

    /**
     * 系统SID-产品季
     */
    @Excel(name = "系统SID-产品季")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private int itemNum;

    /**
     * 创建人账号（用户名称）
     */
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
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "应抵扣金额（扣款金额/调账金额..）")
    private BigDecimal currencyAmountTaxYingD;

    @TableField(exist = false)
    @ApiModelProperty(value = "待抵扣金额（应抵-已核销-核销中）")
    private BigDecimal currencyAmountTaxDaiD;

    @TableField(exist = false)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "业务单号")
    private Long billCode;

    @TableField(exist = false)
    @Excel(name = "系统SID-采购发票")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @Excel(name = "月账单所属期间")
    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    @TableField(exist = false)
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    @TableField(exist = false)
    @Excel(name = "币种单位")
    @ApiModelProperty(value = "币种单位")
    private String currencyUnit;

    @TableField(exist = false)
    @Excel(name = "创建人账号（用户名称）")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "发票类别")
    @TableField(exist = false)
    private String[] invoiceCategoryList;

    @ApiModelProperty(value = "发票类型")
    @TableField(exist = false)
    private String[] invoiceTypeList;

    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String[] materialTypeList;

    @ApiModelProperty(value = "采购组织")
    @TableField(exist = false)
    private String[] purchaseOrgList;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @ApiModelProperty(value = "发票类别")
    @TableField(exist = false)
    private String invoiceCategory;

    @Excel(name = "发票类别")
    @ApiModelProperty(value = "发票类别")
    @TableField(exist = false)
    private String invoiceCategoryName;

    @ApiModelProperty(value = "发票类型")
    @TableField(exist = false)
    private String invoiceType;

    @Excel(name = "发票类型")
    @ApiModelProperty(value = "发票类型")
    @TableField(exist = false)
    private String invoiceTypeName;

    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String materialType;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String materialTypeName;

    @ApiModelProperty(value = "采购组织")
    @TableField(exist = false)
    private String purchaseOrg;

    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织")
    @TableField(exist = false)
    private String purchaseOrgName;

    @Excel(name = "开票日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开票日期")
    @TableField(exist = false)
    private Date invoiceDate;

    @Excel(name = "总账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "总账日期")
    @TableField(exist = false)
    private Date generalLedgerDate;

    @Excel(name = "发票代码")
    @ApiModelProperty(value = "发票代码")
    @TableField(exist = false)
    private String invoiceCode;

    @Excel(name = "发票号")
    @ApiModelProperty(value = "发票号")
    @TableField(exist = false)
    private String invoiceNum;

}
