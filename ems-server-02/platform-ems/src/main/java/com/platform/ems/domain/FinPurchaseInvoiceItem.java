package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 采购发票-明细对象 s_fin_purchase_invoice_item
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_purchase_invoice_item")
public class FinPurchaseInvoiceItem extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-采购发票明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票明细")
    private Long purchaseInvoiceItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票明细")
    private Long purchaseInvoiceDiscountSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] purchaseInvoiceItemSidList;
    /**
     * 系统SID-采购发票
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceSid;

    /**
     * 系统SID-物料&商品&服务
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    /**
     * 系统SID-物料&商品sku1
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    /**
     * 系统SID-物料&商品sku2
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    /**
     * 系统SID-物料&商品条码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    @Excel(name = "采购发票记录号")
    @ApiModelProperty(value = "采购发票记录号")
    @TableField(exist = false)
    private Long purchaseInvoiceCode;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名称")
    @TableField(exist = false)
    private String vendorName;

    @Excel(name = "发票类型")
    @ApiModelProperty(value = "发票类型")
    @TableField(exist = false)
    private String invoiceTypeName;

    @Excel(name = "发票类别")
    @ApiModelProperty(value = "发票类别")
    @TableField(exist = false)
    private String invoiceCategoryName;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String handleStatus;

    @Excel(name = "财务流水来源类别")
    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @Excel(name = "商品/物料编码")
    @ApiModelProperty(value = "商品编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "开票货物或服务名称")
    private String materialName;

    @Excel(name = "开票量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    @Excel(name = "价格(含税)")
    @ApiModelProperty(value = "价格(含税)")
    private BigDecimal priceTax;

    @Excel(name = "价格(不含税)")
    @ApiModelProperty(value = "价格(不含税)")
    private BigDecimal price;

    @Excel(name = "本次开票金额(含税)")
    @ApiModelProperty(value = "本次开票金额(含税)")
    private BigDecimal currencyAmountTax;

    @Excel(name = "本次开票金额(不含税)")
    @ApiModelProperty(value = "本次开票金额(不含税)")
    private BigDecimal currencyAmount;

    @Excel(name = "税额")
    @ApiModelProperty(value = "税额")
    private BigDecimal taxAmount;

    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @Excel(name = "折扣金额")
    @TableField(exist = false)
    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTaxDiscount;

    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    @TableField(exist = false)
    private String productSeasonName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String materialTypeName;

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

    @Excel(name = "发票号")
    @ApiModelProperty(value = "发票号")
    @TableField(exist = false)
    private String invoiceNum;

    @Excel(name = "发票代码")
    @ApiModelProperty(value = "发票代码")
    @TableField(exist = false)
    private String invoiceCode;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种")
    @TableField(exist = false)
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "币种单位")
    @TableField(exist = false)
    private String currencyUnit;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    private String specification;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    /**
     * 采购计量单位（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "采购计量单位（数据字典的键值或配置档案的编码）")
    private String unitPrice;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合")
    private Long accountsMethodGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "收付款方式组合")
    private String accountsMethodGroupName;

    /**
     * 采购合同号/协议sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同号/协议sid")
    private Long purchaseContractSid;

    /**
     * 采购订单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    /**
     * 采购合同号/协议号
     */
    @ApiModelProperty(value = "采购合同号/协议号")
    private String purchaseContractCode;

    /**
     * 系统SID-交货单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-交货单sid")
    private Long deliveryNoteSid;

    /**
     * 采购订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    /**
     * 系统SID-流水账明细（应付暂估）
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应付暂估）")
    private Long bookPaymentEstimationItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应付暂估）")
    private Long bookPaymentEstimationCode;

    /**
     * 已核销金额（含税）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应付暂估的已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    /**
     * 核销中金额（含税）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应付暂估的核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    /**
     * 已核销数量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应付暂估的已核销数量")
    private BigDecimal quantityYhx;

    /**
     * 核销中数量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应付暂估的核销中数量")
    private BigDecimal quantityHxz;

    /**
     * 行号
     */
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 采购交货单/销售发货单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private Long deliveryNoteCode;

    /**
     * 财务凭证流水sid（台帐流水号/财务账流水号）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    /**
     * 财务凭证流水code（台帐流水号/财务账流水号）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code（台帐流水号/财务账流水号）")
    private Long accountDocumentCode;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 财务流水类型编码code
     */
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 财务凭证流水行sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    /**
     * 财务凭证流水行号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行号")
    private Long accountItemCode;

    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "sku1编码")
    @TableField(exist = false)
    private String sku1Code;

    @ApiModelProperty(value = "sku2编码")
    @TableField(exist = false)
    private String sku2Code;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商sid")
    @TableField(exist = false)
    private Long vendorSid;

    @ApiModelProperty(value = "供应商编码")
    @TableField(exist = false)
    private String vendorCode;

    @ApiModelProperty(value = "发票类别")
    @TableField(exist = false)
    private String invoiceCategory;

    @ApiModelProperty(value = "发票类型")
    @TableField(exist = false)
    private String invoiceType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    @TableField(exist = false)
    private Long companySid;

    @ApiModelProperty(value = "公司编码")
    @TableField(exist = false)
    private String companyCode;

    @ApiModelProperty(value = "产品季sid")
    @TableField(exist = false)
    private String productSeasonSid;

    @ApiModelProperty(value = "产品季编码")
    @TableField(exist = false)
    private String productSeasonCode;

    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String materialType;

    @ApiModelProperty(value = "采购组织")
    @TableField(exist = false)
    private String purchaseOrg;

    @ApiModelProperty(value = "发票寄出状态")
    @TableField(exist = false)
    private String signFlag;

    @ApiModelProperty(value = "异常确认状态")
    @TableField(exist = false)
    private String exceptionConfirmFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购计量单位（数据字典的键值或配置档案的编码）")
    private String unitPriceName;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水号")
    private Long bookPaymentEstimationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "待开票量")
    private BigDecimal quantityLeft;

    @TableField(exist = false)
    @ApiModelProperty(value = "待开票金额(含税)")
    private BigDecimal currencyAmountTaxLeft;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式")
    private String rawMaterialMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购模式")
    private String purchaseMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前采购价（含税）")
    private BigDecimal currentPriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前采购价（不含税）")
    private BigDecimal currentPrice;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;
}
