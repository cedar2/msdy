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
 * 客户账互抵单-明细对象 s_fin_customer_account_balance_bill_item
 *
 * @author linhongwei
 * @date 2021-05-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_customer_account_balance_bill_item")
public class FinCustomerAccountBalanceBillItem extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户账互抵单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户账互抵单明细")
    private Long accountBalanceBillItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] accountBalanceBillItemSidList;

    /**
     * 系统SID-客户账互抵单
     */
    @Excel(name = "系统SID-客户账互抵单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户账互抵单")
    private Long accountBalanceBillSid;

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
     * 财务流水类型编码code
     */
    @Excel(name = "财务流水类型编码code")
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "财务流水来源类别编码code（数据字典的键值或配置档案的编码）")
    private String bookSourceCategory;

    /**
     * 财务凭证流水行sid
     */
    @Excel(name = "财务凭证流水行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水行sid")
    private Long accountItemSid;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @ApiModelProperty(value = "行号")
    private Long itemNum;

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
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人账号（用户名称）")
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


    @TableField(exist = false)
    @Excel(name = "客户账互抵单号")
    @ApiModelProperty(value = "客户账互抵单号")
    private Long accountBalanceBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @TableField(exist = false)
    @Excel(name = "客户名称")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季sid")
    private String productSeasonSid;

    @TableField(exist = false)
    @Excel(name = "产品季名称")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @TableField(exist = false)
    @Excel(name = "产品季编码")
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    @TableField(exist = false)
    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @TableField(exist = false)
    @Excel(name = "销售员")
    @ApiModelProperty(value = "销售员")
    private String salePerson;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司sid")
    private Long companySid;

    @TableField(exist = false)
    @Excel(name = "公司名称")
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    @TableField(exist = false)
    @Excel(name = "币种单位")
    @ApiModelProperty(value = "币种单位")
    private String currencyUnit;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员（user_name 多选框）")
    private String[] salePersonList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（配置档案 code 多选框）")
    private String[] materialTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态（数据字典 多选框）")
    private String[] clearStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典 多选框)")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @ApiModelProperty(value = "互抵金额(含税)")
    private BigDecimal currencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水来源类别")
    private String bookSourceCategoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态（数据字典 单选）")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销状态（用来过滤）")
    private String clearStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（用来过滤）")
    private String handleStatusNot;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商扣款）")
    private Long bookDeductionSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商扣款）")
    private Long bookDeductionCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商扣款）")
    private Long bookDeductionItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商调账）")
    private Long bookAccountAdjustSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商调账）")
    private Long bookAccountAdjustCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（供应商调账）")
    private Long bookAccountAdjustItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（应收）")
    private Long bookAccountReceivableSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应收）")
    private Long bookAccountReceivableCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细（应收）")
    private Long bookAccountReceivableItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应收暂估）")
    private Long bookReceiptEstimationSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应收暂估）")
    private Long bookReceiptEstimationCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号（应收暂估）")
    private Long bookReceiptEstimationItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商预收台账流水")
    private Long recordAdvanceReceiptSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商预收台账流水号")
    private Long recordAdvanceReceiptCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商预收台账流水明细")
    private Long recordAdvanceReceiptItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "应互抵金额（含税）")
    private BigDecimal currencyAmountTaxYhd;

    @TableField(exist = false)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

    @TableField(exist = false)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @TableField(exist = false)
    @ApiModelProperty(value = "核销中金额（含税）")
    private BigDecimal currencyAmountTaxHxz;

    @TableField(exist = false)
    @ApiModelProperty(value = "收款状态")
    private String receiptStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "财务流水来源类别编码code")
    private String[] bookSourceCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售价（含税）")
    private BigDecimal priceTax;
}
