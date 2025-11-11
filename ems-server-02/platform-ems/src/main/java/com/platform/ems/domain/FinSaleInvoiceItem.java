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

import lombok.experimental.Accessors;

/**
 * 销售发票-明细对象 s_fin_sale_invoice_item
 *
 * @author linhongwei
 * @date 2021-06-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_sale_invoice_item")
public class FinSaleInvoiceItem extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-销售发票明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票明细")
    private Long saleInvoiceItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票明细")
    private Long saleInvoiceDiscountSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] saleInvoiceItemSidList;
    /**
     * 系统SID-销售发票
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票")
    private Long saleInvoiceSid;

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

    /**
     * 系统SID-物料&商品条码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    @Excel(name = "销售发票记录号")
    @TableField(exist = false)
    @ApiModelProperty(value = "销售发票记录号")
    private Long saleInvoiceCode;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "发票类型")
    @ApiModelProperty(value = "发票类型")
    private String invoiceTypeName;

    @TableField(exist = false)
    @Excel(name = "发票类别")
    @ApiModelProperty(value = "发票类别")
    private String invoiceCategoryName;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @Excel(name = "财务流水来源类别")
    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @Excel(name = "商品/物料编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品/物料名称")
    @ApiModelProperty(value = "开票货物或服务名称")
    private String materialName;


    /**
     * 数量
     */
    @Excel(name = "开票量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    /**
     * 价格(含税)
     */
    @Excel(name = "价格(含税)")
    @ApiModelProperty(value = "价格(含税)")
    private BigDecimal priceTax;

    /**
     * 价格(不含税)
     */
    @Excel(name = "价格(不含税)")
    @ApiModelProperty(value = "价格(不含税)")
    private BigDecimal price;

    /**
     * 本次开票金额(含税)
     */
    @Excel(name = "本次开票金额(含税)")
    @ApiModelProperty(value = "本次开票金额(含税)")
    private BigDecimal currencyAmountTax;

    /**
     * 本次开票金额(不含税)
     */
    @Excel(name = "本次开票金额(不含税)")
    @ApiModelProperty(value = "本次开票金额(不含税)")
    private BigDecimal currencyAmount;

    /**
     * 税额
     */
    @Excel(name = "税额")
    @ApiModelProperty(value = "税额")
    private BigDecimal taxAmount;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @Excel(name = "折扣金额")
    @TableField(exist = false)
    @ApiModelProperty(value = "本次抵折扣金额(含税)")
    private BigDecimal currencyAmountTaxDiscount;

    @Excel(name = "公司")
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @Excel(name = "产品季")
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialTypeName;

    @TableField(exist = false)
    @Excel(name = "销售组织")
    @ApiModelProperty(value = "销售组织（数据字典的键值）")
    private String saleOrgName;

    @Excel(name = "开票日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "开票日期")
    private Date invoiceDate;

    @Excel(name = "总账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @ApiModelProperty(value = "总账日期")
    private Date generalLedgerDate;

    @Excel(name = "发票号")
    @TableField(exist = false)
    @ApiModelProperty(value = "发票号")
    private String invoiceNum;

    @Excel(name = "发票代码")
    @TableField(exist = false)
    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @Excel(name = "币种", dictType = "s_currency")
    @TableField(exist = false)
    @ApiModelProperty(value = "币种")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @TableField(exist = false)
    @ApiModelProperty(value = "币种单位")
    private String currencyUnit;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private String taxRateName;

    /**
     * 规格型号
     */
    @ApiModelProperty(value = "规格型号")
    private String specification;

    /**
     * 系统SID-流水账明细（应付暂估）
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应收暂估）")
    private Long bookReceiptEstimationItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应付暂估）")
    private Long bookReceiptEstimationCode;

    /**
     * 已核销金额（含税）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应收暂估的已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    /**
     * 核销中金额（含税）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应收暂估的核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    /**
     * 已核销数量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应收暂估的已核销数量")
    private BigDecimal quantityYhx;

    /**
     * 核销中数量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "应收暂估的核销中数量")
    private BigDecimal quantityHxz;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

    @ApiModelProperty(value = "销售价单位（数据字典的键值）")
    private String unitPrice;

    /**
     * 销售合同号/协议sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号/协议sid")
    private Long saleContractSid;

    /**
     * 销售合同号/协议号
     */
    @ApiModelProperty(value = "销售合同号/协议号")
    private String saleContractCode;

    /**
     * 销售订单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    /**
     * 系统SID-交货单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-交货单")
    private Long deliveryNoteSid;

    /**
     * 行号
     */
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 销售订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    /**
     * 采购交货单/销售发货单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private Long deliveryNoteCode;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 财务凭证流水sid（台帐流水号/财务账流水号）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid（台帐流水号/财务账流水号）")
    private Long accountDocumentSid;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类型")
    private String invoiceType;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类别")
    private String invoiceCategory;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyCode;


    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid")
    private String productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织")
    private String saleOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票寄出状态")
    private String sendFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "异常确认状态")
    private String exceptionConfirmFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价单位")
    private String unitPriceName;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水类型")
    private String bookTypeName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合")
    private Long accountsMethodGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "收付款方式组合")
    private String accountsMethodGroupName;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水号")
    private Long bookReceiptEstimationSid;

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
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前销售价（含税）")
    private BigDecimal currentPriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前销售价（不含税）")
    private BigDecimal currentPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

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

    @ApiModelProperty(value = "销售组织")
    @TableField(exist = false)
    private String[] saleOrgList;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

}
