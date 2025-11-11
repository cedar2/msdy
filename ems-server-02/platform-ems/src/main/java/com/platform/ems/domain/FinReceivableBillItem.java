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

import javax.validation.constraints.Digits;

/**
 * 收款单-明细对象 s_fin_receivable_bill_item
 *
 * @author linhongwei
 * @date 2021-04-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_receivable_bill_item")
public class FinReceivableBillItem extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-收款单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-收款单明细")
    private Long receivableBillItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] receivableBillItemSidList;

    /**
     * 系统SID-收款单
     */
    @Excel(name = "系统SID-收款单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-收款单")
    private Long receivableBillSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-收款单")
    private Long[] receivableBillSidList;

    /**
     * 财务凭证流水sid（台帐流水号/财务账流水号）
     */
    @Excel(name = "财务凭证流水sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水sid")
    private Long accountDocumentSid;

    /**
     * 财务凭证流水code（台帐流水号/财务账流水号）
     */
    @Excel(name = "财务凭证流水code")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "财务凭证流水code")
    private Long accountDocumentCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-产品季/下单季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季/下单季sid数组")
    private Long[] productSeasonSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季/下单季")
    private String productSeasonName;

    /**
     * 财务流水类型编码code
     */
    @Excel(name = "财务流水类型编码code")
    @ApiModelProperty(value = "财务流水类型编码code")
    private String bookType;

    /**
     * 财务流水来源类别编码code（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "财务流水来源类别编码code")
    @ApiModelProperty(value = "财务流水来源类别编码code")
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
     * 本次收款金额(含税)
     */
    @Digits(integer = 8, fraction = 4, message = "明细金额整数位上限为8位，小数位上限为4位")
    @Excel(name = "本次收款金额(含税)")
    @ApiModelProperty(value = "本次收款金额(含税)")
    private BigDecimal currencyAmountTax;

    /**
     * 收款方式（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "收款方式")
    private String paymentMethod;

    @TableField(exist = false)
    @ApiModelProperty(value = "收款方式")
    private String[] paymentMethodList;

    @TableField(exist = false)
    @Excel(name = "收款方式")
    @ApiModelProperty(value = "收款方式")
    private String paymentMethodName;

    @TableField(exist = false)
    @ApiModelProperty(value = "收款单号")
    private String receivableBillCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型（配置档案 code 多选框）")
    private String[] businessTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型（配置档案 code 多选框）")
    private String[] documentTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "币种（数据字典）")
    private String currency;

    @TableField(exist = false)
    @ApiModelProperty(value = "币种单位（数据字典）")
    private String currencyUnit;

    @ApiModelProperty(value = "款项类别（数据字典的键值或配置档案的编码）")
    private String accountCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别（配置档案 code 多选框）")
    private String[] accountCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "款项类别（数据字典的键值或配置档案的编码）")
    private String accountCategoryName;

    /**
     * 销售渠道（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "销售渠道")
    private String businessChannel;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道")
    private String[] businessChannelList;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售渠道")
    private String businessChannelName;

    @TableField(exist = false)
    @ApiModelProperty(value ="处理状态（数据字典）")
    private String handleStatus;

    /**
     * 纸质合同号
     */
    @Excel(name = "纸质合同号")
    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    /**
     * 税率（存值，即：不含百分号，如20%，就存0.2）
     */
    @Excel(name = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 销售发票sid
     */
    @Excel(name = "销售发票sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票sid")
    private Long saleInvoiceSid;

    /**
     * 销售发票记录号
     */
    @Excel(name = "销售发票记录号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售发票记录号")
    private Long saleInvoiceCode;

    /**
     * 销售订单sid
     */
    @Excel(name = "销售订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    /**
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

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
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "创建人账号")
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
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @Excel(name = "更新人账号")
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
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "收款状态")
    private String receiptPaymentStatus;

    @TableField(exist = false)
    @ApiModelProperty(value ="收款状态（数据字典 多选框)")
    private String[] receiptPaymentStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典 多选框)")
    private String[] handleStatusList;
}
