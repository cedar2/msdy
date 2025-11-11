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

import javax.validation.constraints.Digits;
/**
 * 付款单-核销预付明细表对象 s_fin_pay_bill_item_yufu
 *
 * @author platform
 * @date 2024-03-12
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_pay_bill_item_yufu")
public class FinPayBillItemYufu extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-付款单核销预付明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单核销预付明细")
    private Long payBillItemYufuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] payBillItemYufuSidList;

    /**
     * 系统SID-付款单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long[] payBillSidList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "引用流水关联的收款单")
    private Long referDocSid;

    @TableField(exist = false)
    @Excel(name = "已预付款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "引用流水关联的收款单")
    private Long referDocCode;

    @TableField(exist = false)
    @Excel(name = "核销状态", dictType = "s_account_clear")
    @ApiModelProperty(value = "流水核销状态")
    private String clearStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水核销状态")
    private String[] clearStatusList;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long[] vendorSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-公司")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司简称")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水关联单据经办人")
    private String referAgent;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水关联单据经办人")
    private String[] referAgentList;

    @TableField(exist = false)
    @Excel(name = "经办人(已预付款)")
    @ApiModelProperty(value = "流水关联单据经办人")
    private String referAgentName;

    @TableField(exist = false)
    @Excel(name = "付款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款单")
    private Long payBillCode;

    /**
     * 本次申请金额(含税)
     */
    @Digits(integer = 8, fraction = 4, message = "明细金额整数位上限为8位，小数位上限为4位")
    @Excel(name = "本次核销金额", scale = 4, cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "本次申请金额(含税)")
    private BigDecimal currencyAmountTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据经办人")
    private String agent;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据经办人")
    private String[] agentList;

    @TableField(exist = false)
    @Excel(name = "经办人(付款单)")
    @ApiModelProperty(value = "单据经办人")
    private String agentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @TableField(exist = false)
    @Excel(name = "处理状态(付款单)", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账（预付款）")
    private Long bookPaymentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-流水账（预付款）")
    private Long[] bookPaymentSidList;

    @Excel(name = "已预付款财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水账编号（预付款）")
    private Long bookPaymentCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "下单季")
    private Long productSeasonSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "流水已预付金额（含税）")
    private BigDecimal currencyAmountTaxFk;

    @TableField(exist = false)
    @ApiModelProperty(value = "已核销金额（含税）")
    private BigDecimal currencyAmountTaxYhx;

    @TableField(exist = false)
    @ApiModelProperty(value = "待核销金额（含税）")
    private BigDecimal currencyAmountTaxDhx;

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
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

}
