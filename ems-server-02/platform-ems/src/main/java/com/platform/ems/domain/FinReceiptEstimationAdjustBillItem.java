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
 * 应收暂估调价量单-明细对象 s_fin_receipt_estimation_adjust_bill_item
 *
 * @author chenkw
 * @date 2022-01-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_receipt_estimation_adjust_bill_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinReceiptEstimationAdjustBillItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-应收暂估调价量单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-应收暂估调价量单明细")
    private Long receiptEstimationAdjustBillItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] receiptEstimationAdjustBillItemSidList;
    /**
     * 系统SID-应收暂估调价量单
     */
    @Excel(name = "系统SID-应收暂估调价量单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-应收暂估调价量单")
    private Long receiptEstimationAdjustBillSid;

    /**
     * 系统SID-物料&商品&服务
     */
    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品&服务编码")
    private String materialCode;

    /**
     * 系统SID-物料&商品sku1
     */
    @Excel(name = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品sku1编码")
    private String sku1Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品sku1名称")
    private String sku1Name;

    /**
     * 系统SID-物料&商品sku2
     */
    @Excel(name = "系统SID-物料&商品sku2")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品sku2编码")
    private String sku2Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品sku2名称")
    private String sku2Name;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @Excel(name = "系统SID-商品条码（物料&商品&服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /**
     * 开票货物或服务名称
     */
    @Excel(name = "开票货物或服务名称")
    @ApiModelProperty(value = "开票货物或服务名称")
    private String materialName;

    /**
     * 规格型号
     */
    @Excel(name = "规格型号")
    @ApiModelProperty(value = "规格型号")
    private String specification;

    /**
     * 数量（新）
     */
    @Excel(name = "数量（新）")
    @ApiModelProperty(value = "数量（新）")
    private BigDecimal quantityNew;

    /**
     * 价格(不含税)（新）
     */
    @Excel(name = "价格(不含税)（新）")
    @ApiModelProperty(value = "价格(不含税)（新）")
    private BigDecimal priceNew;

    /**
     * 价格(含税)（新）
     */
    @Excel(name = "价格(含税)（新）")
    @ApiModelProperty(value = "价格(含税)（新）")
    private BigDecimal priceTaxNew;

    /**
     * 数量（旧）
     */
    @Excel(name = "数量（旧）")
    @ApiModelProperty(value = "数量（旧）")
    private BigDecimal quantityOld;

    /**
     * 价格(不含税)（旧）
     */
    @Excel(name = "价格(不含税)（旧）")
    @ApiModelProperty(value = "价格(不含税)（旧）")
    private BigDecimal priceOld;

    /**
     * 价格(含税)（旧）
     */
    @Excel(name = "价格(含税)（旧）")
    @ApiModelProperty(value = "价格(含税)（旧）")
    private BigDecimal priceTaxOld;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

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
     * 流水类型编码code
     */
    @Excel(name = "流水类型编码code")
    @ApiModelProperty(value = "流水类型编码code")
    private String bookType;

    /**
     * 流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 财务凭证流水行sid
     */
    @Excel(name = "财务凭证流水行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    /**
     * 财务凭证流水行号
     */
    @Excel(name = "财务凭证流水行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行号")
    private Long accountItemCode;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

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

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应收暂估）")
    private Long bookReceiptEstimationSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水账（应收暂估）")
    private Long bookReceiptEstimationCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应收暂估）")
    private Long bookReceiptEstimationItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价（含税）")
    private BigDecimal priceTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价（不含税）")
    private BigDecimal price;

    @TableField(exist = false)
    @ApiModelProperty(value = "数量（出入库）")
    private BigDecimal quantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "待核销数量（出入库）")
    private BigDecimal quantityLeft;

    @TableField(exist = false)
    @ApiModelProperty(value = "金额")
    private BigDecimal currencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "已核销金额")
    private BigDecimal currencyAmountTaxYhx;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销中金额")
    private BigDecimal currencyAmountTaxHxz;

    @ApiModelProperty(value = "财务流水类型编码code")
    @TableField(exist = false)
    private String bookTypeName;

    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String bookSourceCategoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价单位（数据字典的键值或配置档案的编码）")
    private String unitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价单位（数据字典的键值或配置档案的编码）")
    private String unitPriceName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private Long deliveryNoteCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "收付款方式组合")
    private String accountsMethodGroupName;
}
