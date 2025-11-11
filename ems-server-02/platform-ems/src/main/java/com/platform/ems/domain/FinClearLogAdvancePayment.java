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
 * 核销供应商已预付款日志对象 s_fin_clear_log_advance_payment
 *
 * @author platform
 * @date 2024-03-28
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_clear_log_advance_payment")
public class FinClearLogAdvancePayment extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 供应商已预付款单号-流水账明细
     */
    @TableField(exist = false)
    @Excel(name = "供应商已预付款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商已预付款单号")
    private Long bookPayBillCode;

    /**
     * 系统SID-核销供应商已预付款日志表
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-核销供应商已预付款日志表")
    private Long clearLogAdvancePaymentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] clearLogAdvancePaymentSidList;

    /**
     * 系统SID-供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商列表")
    private Long[] vendorSidList;

    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商名")
    private String vendorName;

    /**
     * 系统SID-公司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司")
    private Long companySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司列表")
    private Long[] companySidList;

    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    @TableField(exist = false)
    @Excel(name = "公司")
    @ApiModelProperty(value = "公司名")
    private String companyName;

    /**
     * 系统SID-付款单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    /**
     * 付款单号
     */
    @Excel(name = "付款单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "付款单号")
    private Long payBillCode;

    /**
     * 生效状态
     */
    @Excel(name = "生效状态", dictType = "s_shengxiao_status")
    @ApiModelProperty(value = "生效状态")
    private String shengxiaoStatus;

    /**
     * 本次核销金额
     */
    @Excel(name = "本次核销金额")
    @ApiModelProperty(value = "核销金额(含税)")
    private BigDecimal currencyAmountTax;

    /**
     * 系统SID-流水账（付款）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账")
    private Long bookPaymentSid;

    /**
     * 供应商已预付款财务流水号
     */
    @Excel(name = "供应商已预付款财务流水号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流水号")
    private Long bookPaymentCode;

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
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 系统SID-流水账明细（付款）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-流水账明细")
    private Long bookPaymentItemSid;


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
