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
import lombok.experimental.Accessors;

/**
 * 财务流水账-明细-应收暂估对象 s_fin_book_receipt_estimation_item
 *
 * @author qhq
 * @date 2021-06-08
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_book_receipt_estimation_item")
public class FinBookReceiptEstimationItem extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-流水账明细（应收暂估）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应收暂估）")
    private Long bookReceiptEstimationItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] bookReceiptEstimationItemSidList;
    /**
     * 系统SID-流水账（应收暂估）
     */
    @Excel(name = "系统SID-流水账（应收暂估）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应收暂估）")
    private Long bookReceiptEstimationSid;

    /**
     * 系统SID-流水账（应付暂估）
     */
    @Excel(name = "系统SID-流水账（应收暂估）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务流水号")
    @TableField(exist = false)
    private Long bookReceiptEstimationCode;

    /**
     * 财务流水类型编码code
     */
    @Excel(name = "财务流水类型编码code")
    @ApiModelProperty(value = "财务流水类型编码code")
    @TableField(exist = false)
    private String bookType;

    /**
     * 财务流水类型编码code
     */
    @Excel(name = "财务流水类型编码code")
    @ApiModelProperty(value = "财务流水类型编码code")
    @TableField(exist = false)
    private String bookTypeName;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String bookSourceCategory;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String bookSourceCategoryName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合")
    private Long accountsMethodGroup;

    @TableField(exist = false)
    @ApiModelProperty(value = "收付款方式组合")
    private String accountsMethodGroupName;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 系统SID-物料&商品&服务
     */
    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    /**
     * 系统SID-物料&商品sku1
     */
    @Excel(name = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    /**
     * 系统SID-物料&商品sku2
     */
    @Excel(name = "系统SID-物料&商品sku2")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @Excel(name = "系统SID-商品条码（物料&商品&服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位名称（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位名称（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String unitBaseName;

    /**
     * 数量（出入库）
     */
    @Excel(name = "数量（出入库）")
    @ApiModelProperty(value = "数量（出入库）")
    private BigDecimal quantity;

    /**
     * 销售价（含税）
     */
    @Excel(name = "销售价（含税）")
    @ApiModelProperty(value = "销售价（含税）")
    private BigDecimal priceTax;

    /**
     * 销售价（不含税）
     */
    @Excel(name = "销售价（不含税）")
    @ApiModelProperty(value = "销售价（不含税）")
    private BigDecimal price;

    //新增字段
    /**
     * 采购金额（含税）
     */
    @Excel(name = "采购金额（含税）")
    @ApiModelProperty(value = "采购金额（含税）")
    private BigDecimal currencyAmountTax;

    /**
     * 已核销金额（含税）
     */
    @Excel(name = "已核销金额（含税）")
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    /**
     * 核销中金额（含税）
     */
    @Excel(name = "核销中金额（含税）")
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    /**
     * 已核销数量
     */
    @Excel(name = "已核销数量")
    @ApiModelProperty(value = "已核销数量")
    private BigDecimal quantityYhx;

    /**
     * 核销中数量
     */
    @Excel(name = "核销中数量")
    @ApiModelProperty(value = "核销中数量")
    private BigDecimal quantityHxz;

    @Excel(name = "已核销金额（含税）收款")
    @ApiModelProperty(value = "已核销金额（含税）收款")
    private BigDecimal currencyAmountTaxSkYhx;

    @Excel(name = "核销中金额（含税）收款")
    @ApiModelProperty(value = "核销中金额（含税）收款")
    private BigDecimal currencyAmountTaxSkHxz;

    @Excel(name = "已核销金额（含税）到票")
    @ApiModelProperty(value = "已核销金额（含税）到票")
    private BigDecimal currencyAmountTaxDpYhx;

    @Excel(name = "核销中金额（含税）到票")
    @ApiModelProperty(value = "核销中金额（含税）到票")
    private BigDecimal currencyAmountTaxDpHxz;

    /**
     * 销售合同sid
     */
    @Excel(name = "销售合同sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同sid")
    private Long saleContractSid;

    /**
     * 销售合同号
     */
    @Excel(name = "销售合同号")
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    /**
     * 采购交货单/销售发货单sid
     */
    @Excel(name = "采购交货单/销售发货单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单sid")
    private Long deliveryNoteSid;

    /**
     * 采购交货单/销售发货单号
     */
    @Excel(name = "采购交货单/销售发货单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private Long deliveryNoteCode;

    /**
     * 关联单据类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "关联单据类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "关联单据类别（数据字典的键值或配置档案的编码）")
    private String referDocCategory;

    /**
     * 关联单据sid
     */
    @Excel(name = "关联单据sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据sid")
    private Long referDocSid;

    /**
     * 关联单据号
     */
    @Excel(name = "关联单据号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据号")
    private Long referDocCode;

    /**
     * 关联单据行sid
     */
    @Excel(name = "关联单据行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据行sid")
    private Long referDocItemSid;

    /**
     * 关联单据行号
     */
    @Excel(name = "关联单据行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联单据行号")
    private Long referDocItemCode;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率（存值，即：不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    /**
     * 核销状态
     */
    @Excel(name = "核销状态", dictType = "s_account_clear")
    @ApiModelProperty(value = "核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "用来过滤全核销")
    private String clearStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "用来过滤作废单据")
    private String[] handleStatusNotList;

    @TableField(exist = false)
    @ApiModelProperty(value = "用来过滤红蓝票正反向金额")
    private String invoiceType;

    /**
     * 核销状态（含税金额）
     */
    @Excel(name = "核销状态（含税金额）")
    @ApiModelProperty(value = "核销状态（含税金额）")
    private String clearStatusMoney;

    /**
     * 核销状态（数量）
     */
    @Excel(name = "核销状态（数量）")
    @ApiModelProperty(value = "核销状态（数量）")
    private String clearStatusQuantity;

    /**
     * 账期(天)
     */
    @Excel(name = "账期(天)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账期(天)")
    private Long accountValidDays;

    /**
     * 账期天类型编码（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "账期天类型编码（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "账期天类型编码（数据字典的键值或配置档案的编码）")
    private String dayType;

    /**
     * 账期有效期（起）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "账期有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "账期有效期（起）")
    private Date accountValidDate;

    @Excel(name = "是否已业务对账", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已业务对账（数据字典的键值或配置档案的编码）")
    private String isBusinessVerify;

    @ApiModelProperty(value = "业务对账所属期间（所属年月）")
    private String businessVerifyPeriod;

    @Excel(name = "是否已财务对账", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值或配置档案的编码）")
    private String isFinanceVerify;

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

    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    private String materialName;

    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "商品编码")
    @TableField(exist = false)
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isTuihuo;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderSid;

    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司档案")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "待开票量")
    private BigDecimal quantityLeft;

    @TableField(exist = false)
    @ApiModelProperty(value = "待开票金额(含税)")
    private BigDecimal currencyAmountTaxLeft;

    @TableField(exist = false)
    @ApiModelProperty(value = "税额")
    private BigDecimal taxAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前销售价（含税）")
    private BigDecimal currentPriceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前销售价（不含税）")
    private BigDecimal currentPrice;

    @ApiModelProperty(value = "销售价单位")
    private String unitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价单位")
    private String unitPriceName;

    /**
     * 配置档案
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private String taxRateName;

    @TableField(exist = false)
    @ApiModelProperty(value = "甲供料方式")
    private String rawMaterialMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

}
