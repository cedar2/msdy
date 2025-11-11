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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;

/**
 * 新品试销计划单-目标预定对象 s_frm_trialsale_plan_target
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_trialsale_plan_target")
public class FrmTrialsalePlanTarget extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-新品试销计划单目标预定
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单目标预定")
    private Long trialsalePlanTargetSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] trialsalePlanTargetSidList;

    /**
     * 系统SID-新品试销计划单
     */
    @Excel(name = "系统SID-新品试销计划单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单")
    private Long newproductTrialsalePlanSid;

    /**
     * 目标排名
     */
    @Excel(name = "目标排名")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "目标排名")
    private Long targetRank;

    /**
     * 目标日均量
     */
    @Excel(name = "目标日均量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "目标日均量")
    private Long targetDailyNum;

    /**
     * 流量
     */
    @Excel(name = "流量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流量")
    private Long visitorQuantity;

    /**
     * 转化率，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
     */
    @Digits(integer = 1, fraction = 4, message = "转化率(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "转化率，如是2%，则存储的值为：0.02")
    private BigDecimal conversionRate;

    /**
     * 转化率，如是2%
     */
    @TableField(exist = false)
    @Excel(name = "转化率(%)")
    @ApiModelProperty(value = "转化率，如是2%")
    private String conversionRateString;

    /**
     * 营业额（元）
     */
    @Excel(name = "营业额（元）")
    @ApiModelProperty(value = "营业额（元）")
    private BigDecimal saleAmount;

    /**
     * 展现量
     */
    @Excel(name = "展现量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "展现量")
    private Long displayQuantity;

    /**
     * 币种
     */
    @Excel(name = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位")
    @ApiModelProperty(value = "货币单位")
    private String currencyUnit;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
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
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
