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
import com.platform.ems.util.data.KeepTwoDecimalsSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 客户押金-明细对象 s_fin_customer_cash_pledge_bill_item
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_fin_customer_cash_pledge_bill_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinCustomerCashPledgeBillItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-客户押金(退回)单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户押金(退回)单明细")
    private Long cashPledgeBillItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] cashPledgeBillItemSidList;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] cashPledgeBillSidList;

    /**
     * 系统SID-客户押金(退回)单
     */
    @Excel(name = "系统SID-客户押金(退回)单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户押金(退回)单")
    private Long cashPledgeBillSid;

    /**
     * 押金类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "押金类型（数据字典的键值或配置档案的编码）")
    @NotBlank(message = "明细中押金类型不能为空！")
    @ApiModelProperty(value = "押金类型（数据字典的键值或配置档案的编码）")
    private String pledgeType;

    /**
     * 押金金额/本次退回金额
     */
    @Excel(name = "押金金额/本次退回金额")
    @NotNull(message = "明细的金额不能为空！")
    @Digits(integer = 11, fraction = 4, message = "明细中押金金额/本次退回金额整数位上限为11位，小数位上限为4位")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "押金金额/本次退回金额")
    private BigDecimal currencyAmount;

    /**
     * 已退回金额
     */
    @Excel(name = "已退回金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "已退回金额")
    private BigDecimal currencyAmountYth;

    /**
     * 退回中金额
     */
    @Excel(name = "退回中金额")
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "退回中金额")
    private BigDecimal currencyAmountThz;

    /**
     * 退回状态
     */
    @Excel(name = "退回状态")
    @ApiModelProperty(value = "退回状态")
    private String returnStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "退回状态,用来默认过滤：全退回")
    private String returnStatusNot;

    @TableField(exist = false)
    @ApiModelProperty(value = "退回状态")
    private String preReturnStatus;

    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "已退回金额")
    private BigDecimal preCurrencyAmountYth;

    /**
     * 退回中金额
     */
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "退回中金额")
    private BigDecimal preCurrencyAmountThz;

    /**
     * 待退回金额
     */
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @TableField(exist = false)
    @ApiModelProperty(value = "待退回金额")
    private BigDecimal preCurrencyAmountDth;


    /**
     * 退回单对应的押金单sid
     */
    @Excel(name = "退回单对应的押金单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "退回单对应的押金单sid")
    private Long preCashPledgeBillSid;

    /**
     * 退回单对应的押金单号
     */
    @Excel(name = "退回单对应的押金单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "退回单对应的押金单号")
    private Long preCashPledgeBillCode;

    /**
     * 退回单对应的押金单明细sid
     */
    @Excel(name = "退回单对应的押金单明细sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "退回单对应的押金单明细sid")
    private Long preCashPledgeBillItemSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "引用的行号")
    private Long preItemNum;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 采购合同sid
     */
    @Excel(name = "采购合同sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购合同sid")
    private Long saleContractSid;

    /**
     * 采购合同号
     */
    @Excel(name = "采购合同号")
    @ApiModelProperty(value = "采购合同号")
    private String saleContractCode;

    /**
     * 采购订单sid
     */
    @Excel(name = "采购订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long salesOrderSid;

    /**
     * 采购订单号
     */
    @Excel(name = "采购订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long salesOrderCode;

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

    @ApiModelProperty(value = "纸质合同号")
    private String paperContractCode;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
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

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "修改人")
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

    /**
     * 客户押金(退回)单号
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户押金(退回)单号")
    private Long cashPledgeBillCode;

    /**
     * 系统SID-客户
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户")
    private Long customerSid;

    /**
     * 系统SID-公司档案
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentType;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date preDocumentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String preDocumentType;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String preDocumentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String[] documentTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "押金类型")
    private String pledgeTypeName;

    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "押金金额")
    private BigDecimal cashAmount;

    @TableField(exist = false)
    @JsonSerialize(using = KeepTwoDecimalsSerialize.class)
    @ApiModelProperty(value = "待退回金额")
    private BigDecimal currencyAmountDth;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（下拉框 多选")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "退回状态（下拉框 多选")
    private String[] returnStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private Long[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private Long[] companySidList;
}
