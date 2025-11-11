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
 * 销售发票对象 s_fin_sale_invoice
 *
 * @author qhq
 * @date 2021-06-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_sale_invoice")
public class FinSaleInvoice extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-销售发票
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售发票")
    private Long saleInvoiceSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] saleInvoiceSidList;

    /**
     * 销售发票单号
     */
    @Excel(name = "销售发票记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票单号")
    private Long saleInvoiceCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Excel(name = "客户")
    @ApiModelProperty(value = "客户名称")
    private String customerShortName;

    @Excel(name = "发票类型")
    @ApiModelProperty(value = "发票类型（数据字典的键值）")
    @TableField(exist = false)
    private String invoiceTypeName;

    @Excel(name = "发票类别")
    @ApiModelProperty(value = "发票类别（数据字典的键值）")
    @TableField(exist = false)
    private String invoiceCategoryName;

    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @Excel(name = "发票寄出状态", dictType = "s_invoice_send_status")
    @ApiModelProperty(value = "发票寄出状态（数据字典的键值）")
    private String sendFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    @TableField(exist = false)
    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String materialTypeName;

    @Excel(name = "销售组织")
    @ApiModelProperty(value = "销售组织（数据字典的键值）")
    @TableField(exist = false)
    private String saleOrgName;

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

    @Excel(name = "开票维度")
    @ApiModelProperty(value = "发票维度（数据字典的键值）")
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

    @Excel(name = "创建人")
    @TableField(exist = false)
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

    /**
     * 单据日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    /**
     * 发票类别（数据字典的键值）
     */
    @ApiModelProperty(value = "发票类别（数据字典的键值）")
    private String invoiceCategory;

    @ApiModelProperty(value = "财务流水类型编码code")
    @TableField(exist = false)
    private String bookType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号/协议号")
    @TableField(exist = false)
    private Long saleContractCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售合同号/协议号")
    @TableField(exist = false)
    private Long saleContractSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    @TableField(exist = false)
    private Long salesOrderCode;

    /**
     * 发票类型（数据字典的键值）
     */
    @ApiModelProperty(value = "发票类型（数据字典的键值）")
    private String invoiceType;

    @ApiModelProperty(value = "发票维度（数据字典的键值）")
    private String invoiceDimension;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司品牌sid")
    private Long companyBrandSid;

    @ApiModelProperty(value = "销售组织（数据字典的键值）")
    private String saleOrg;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季")
    private Long productSeasonSid;

    @ApiModelProperty(value = "业务渠道（数据字典的键值或配置档案的编码）")
    private String businessChannel;

    @ApiModelProperty(value = "物料类型（数据字典的键值或配置档案的编码）")
    private String materialType;

    @ApiModelProperty(value = "月账单所属期间")
    private String monthAccountPeriod;

    /**
     * 参考发票单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "参考发票单号")
    private Long referenceInvoice;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "票面总税额")
    private BigDecimal totalTaxAmount;

    @ApiModelProperty(value = "税率（存值，即：不含百分号，如20%，就存0.2）")
    private BigDecimal taxRate;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "票面总金额(含税)")
    private BigDecimal totalCurrencyAmountTax;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "票面总金额(不含税)")
    private BigDecimal totalCurrencyAmount;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收款主体sid")
    private Long payeeCompanySid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收票方sid")
    private Long invoiceCustomerSid;

    @ApiModelProperty(value = "机器编号（发票）")
    private String identificationNumber;

    /**
     * 校验码（发票）
     */
    @ApiModelProperty(value = "校验码（发票）")
    private String checkCode;

    /**
     * 收付款方式组合sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收付款方式组合sid")
    private Long accountsMethodGroup;

    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    /**
     * 产品季编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 系统SID-销售发票sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-销售发票sids")
    private Long[] saleInvoiceSids;

    /**
     * 销售发票-明细对象list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售发票-明细对象")
    private List<FinSaleInvoiceItem> finSaleInvoiceItemList;

    /**
     * 销售发票-附件对象list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售发票-附件对象")
    private List<FinSaleInvoiceAttachment> attachmentList;

    /**
     * 销售发票-附件对象list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售发票-折扣对象")
    private List<FinSaleInvoiceDiscount> finSaleInvoiceDiscountList;

    /**
     * 折扣-调账流水
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "折扣-调账流水")
    private List<FinBookCustomerAccountAdjust> bookCustomerAccountAdjustList;

    /**
     * 折扣-扣款流水
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "折扣-扣款流水")
    private List<FinBookCustomerDeduction> bookCustomerDeductionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-客户")
    private Long[] customerSidList;

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
     * 配置档案 s_con_sale_org
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "销售组织（配置档案）")
    private String[] saleOrgList;

    /**
     * 配置档案 s_con_invoice_dimension
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开票维度（配置档案）")
    private String[] invoiceDimensionList;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票签收状态（数据字典的键值）")
    private String[] sendFlagList;

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
