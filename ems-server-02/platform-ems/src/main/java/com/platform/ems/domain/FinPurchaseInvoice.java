package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;

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

import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 采购发票对象 s_fin_purchase_invoice
 *
 * @author qhq
 * @date 2021-04-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_purchase_invoice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinPurchaseInvoice extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-采购发票
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购发票")
    private Long purchaseInvoiceSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] purchaseInvoiceSidList;

    /**
     * 采购发票号
     */
    @Excel(name = "发票记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购发票号")
    private Long purchaseInvoiceCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @Excel(name = "发票类型")
    @ApiModelProperty(value = "发票类型名称（数据字典的键值）")
    @TableField(exist = false)
    private String invoiceTypeName;

    @Excel(name = "发票类别")
    @ApiModelProperty(value = "发票类别名称（数据字典的键值）")
    @TableField(exist = false)
    private String invoiceCategoryName;

    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @Excel(name = "发票签收状态", dictType = "s_sign_status")
    @ApiModelProperty(value = "发票签收状态（数据字典的键值）")
    private String signFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称（数据字典的键值）")
    @TableField(exist = false)
    private String materialTypeName;

    @Excel(name = "采购组织")
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    @TableField(exist = false)
    private String purchaseOrgName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "开票日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "开票日期")
    private Date invoiceDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "总账日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "总账日期")
    private Date generalLedgerDate;

    @Excel(name = "发票号")
    @ApiModelProperty(value = "发票号码")
    private String invoiceNum;

    @Excel(name = "发票代码")
    @ApiModelProperty(value = "发票代码")
    private String invoiceCode;

    @Excel(name = "是否已财务对账", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已财务对账（数据字典的键值）")
    private String isFinanceVerify;

    @Excel(name = "异常确认状态", dictType = "s_invoice_exception_confirm")
    @ApiModelProperty(value = "异常确认状态（数据字典的键值）")
    private String exceptionConfirmFlag;

    @Excel(name = "发票维度")
    @ApiModelProperty(value = "发票维度名称（数据字典的键值）")
    @TableField(exist = false)
    private String invoiceDimensionName;

    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "当前审批节点名称")
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    @Excel(name = "当前审批人")
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户昵称）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号（用户昵称）")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 发票类别（数据字典的键值）
     */
    @ApiModelProperty(value = "发票类别（数据字典的键值）")
    private String invoiceCategory;

    /**
     * 发票类型（数据字典的键值）
     */
    @ApiModelProperty(value = "发票类型（数据字典的键值）")
    private String invoiceType;

    @ApiModelProperty(value = "发票维度（数据字典的键值）")
    private String invoiceDimension;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    /**
     * 采购组织（数据字典的键值）
     */
    @ApiModelProperty(value = "采购组织（数据字典的键值）")
    private String purchaseOrg;

    /**
     * 系统SID-产品季
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    /**
     * 业务渠道（数据字典的键值）
     */
    @ApiModelProperty(value = "业务渠道（数据字典的键值）")
    private String businessChannel;

    @ApiModelProperty(value = "物料类型（数据字典的键值）")
    private String materialType;

    @ApiModelProperty(value = "财务流水类型编码code")
    @TableField(exist = false)
    private String bookType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同号/协议号")
    @TableField(exist = false)
    private Long purchaseContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同号/协议号")
    @TableField(exist = false)
    private Long purchaseContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    @TableField(exist = false)
    private Long purchaseOrderCode;

    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    /**
     * 参考发票号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "参考发票号")
    private Long referenceInvoice;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "票面总金额(含税)")
    private BigDecimal totalCurrencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "票面总金额(不含税)")
    private BigDecimal totalCurrencyAmount;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "票面总税额")
    private BigDecimal totalTaxAmount;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款主体sid")
    private Long payCompanySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开票方sid")
    private Long invoiceVendorSid;

    @ApiModelProperty(value = "机器编号（发票）")
    private String identificationNumber;

    @ApiModelProperty(value = "校验码（发票）")
    private String checkCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合sid")
    private Long accountsMethodGroup;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;


    /**
     * 系统SID-采购发票sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-采购发票sids")
    private Long[] purchaseInvoiceSids;

    /**
     * 采购发票-明细对象list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购发票-明细对象")
    private List<FinPurchaseInvoiceItem> finPurchaseInvoiceItemList;

    /**
     * 采购发票-附件对象list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购发票-附件对象")
    private List<FinPurchaseInvoiceAttachment> attachmentList;

    /**
     * 采购发票-附件对象list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购发票-折扣对象")
    private List<FinPurchaseInvoiceDiscount> finPurchaseInvoiceDiscountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点名称")
    private String node;

    @TableField(exist = false)
    @ApiModelProperty(value = "审批人")
    private String approval;

    @TableField(exist = false)
    @ApiModelProperty(value = "扣款单流水账list，开票折扣用")
    private List<FinBookVendorDeduction> finBookVendorDedcutionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "调账单流水账list，开票折扣用")
    private List<FinBookVendorAccountAdjust> finBookVendorAccountAdjustList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long[] productSeasonSidList;

    /**
     * 配置档案 s_con_invoice_type
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票类型（配置档案）")
    private String[] invoiceTypeList;

    /**
     * 配置档案 s_con_invoice_category
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票类别（配置档案）")
    private String[] invoiceCategoryList;

    /**
     * 配置档案 s_con_material_type
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型（配置档案）")
    private String[] materialTypeList;

    /**
     * 配置档案 s_con_purchase_org
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购组织（配置档案）")
    private String[] purchaseOrgList;

    /**
     * 配置档案 s_con_invoice_dimension
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开票维度（配置档案）")
    private String[] invoiceDimensionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票签收状态（数据字典的键值）")
    private String[] signFlagList;

    @TableField(exist = false)
    @ApiModelProperty(value = "异常确认状态（数据字典的键值）")
    private String[] exceptionConfirmFlagList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRateName;

}
