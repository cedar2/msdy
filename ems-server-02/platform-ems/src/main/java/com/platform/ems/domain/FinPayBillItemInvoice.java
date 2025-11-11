package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 付款单-发票台账明细表对象 s_fin_pay_bill_item_invoice
 *
 * @author platform
 * @date 2024-03-12
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_pay_bill_item_invoice")
public class FinPayBillItemInvoice extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-付款单发票台账明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单发票台账明细")
    private Long payBillItemInvoiceSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] payBillItemInvoiceSidList;

    /**
     * 系统SID-付款单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    /**
     * 系统SID-供应商发票台账
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商发票台账")
    private Long vendorInvoiceRecordSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商发票台账")
    private Long[] vendorInvoiceRecordSidList;

    /**
     * 供应商发票台账号
     */
    @Excel(name = "供应商发票台账号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商发票台账号")
    private Long vendorInvoiceRecordCode;

    // // // // // // // // // //

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商")
    private String vendorShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司编码")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司编码")
    private String companyShortName;

    // // // // // 付款单 // // // // //

    @TableField(exist = false)
    @Excel(name = "付款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long[] payBillSidList;

    @TableField(exist = false)
    @Excel(name = "付款状态", dictType = "s_fukuan_status")
    @ApiModelProperty(value = "收款状态")
    private String fukuanStatus;

    // // // // // // // // // //

    /**
     * 本次申请金额(含税)
     */
    @Excel(name = "本次核销金额")
    @ApiModelProperty(value = "本次申请金额(含税)")
    private BigDecimal currencyAmountTax;

    // // // // // 收付款单 // // // // //

    @TableField(exist = false)
    @ApiModelProperty(value = "经办人")
    private String agent;

    @TableField(exist = false)
    @Excel(name = "经办人")
    @ApiModelProperty(value = "经办人")
    private String agentName;

    @TableField(exist = false)
    @Excel(name = "收款单处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String billHandleStatus;

    // // // // // 发票台账 // // // // //

    @TableField(exist = false)
    @ApiModelProperty(value = "发票号")
    private String invoiceNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "待收款金额")
    private BigDecimal currencyAmountTaxDai;

    @TableField(exist = false)
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "票面总税额")
    private BigDecimal totalTaxAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "票面总金额(含税)")
    private BigDecimal totalCurrencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "票面总金额(不含税)")
    private BigDecimal totalCurrencyAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票代码")
    private String inoviceCode;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发票日期")
    private Date invoiceDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类别")
    private String invoiceCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类别")
    private String invoiceCategoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类型")
    private String invoiceType;

    @TableField(exist = false)
    @ApiModelProperty(value = "发票类型")
    private String invoiceTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    // // // // // // // // // //

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


}
