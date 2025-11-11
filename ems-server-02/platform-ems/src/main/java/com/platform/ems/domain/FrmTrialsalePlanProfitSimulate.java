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
 * 新品试销计划单-利润模拟对象 s_frm_trialsale_plan_profit_simulate
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_trialsale_plan_profit_simulate")
public class FrmTrialsalePlanProfitSimulate extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-新品试销计划单利润模拟
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单利润模拟")
    private Long trialsalePlanProfitSimulationSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] trialsalePlanProfitSimulationSidList;

    /**
     * 系统SID-新品试销计划单
     */
    @Excel(name = "系统SID-新品试销计划单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-新品试销计划单")
    private Long newproductTrialsalePlanSid;

    /**
     * 方案
     */
    @Excel(name = "方案")
    @ApiModelProperty(value = "方案")
    private String scheme;

    /**
     * 定价（元）
     */
    @Excel(name = "定价（元）")
    @ApiModelProperty(value = "定价（元）")
    private BigDecimal fixedPrice;

    /**
     * 折扣&VAT
     */
    @Excel(name = "折扣&VAT")
    @ApiModelProperty(value = "折扣&VAT")
    private BigDecimal discountVat;

    /**
     * 售价（元）
     */
    @Excel(name = "售价（元）")
    @ApiModelProperty(value = "售价（元）")
    private BigDecimal salePrice;

    /**
     * 产品成本（元）
     */
    @Excel(name = "产品成本（元）")
    @ApiModelProperty(value = "产品成本（元）")
    private BigDecimal productCost;

    /**
     * 物流成本（元）
     */
    @Excel(name = "物流成本（元）")
    @ApiModelProperty(value = "物流成本（元）")
    private BigDecimal logisticsCost;

    /**
     * 销售成本（元）
     */
    @Excel(name = "销售成本（元）")
    @ApiModelProperty(value = "销售成本（元）")
    private BigDecimal saleCost;

    /**
     * 佣金（元）
     */
    @Excel(name = "佣金（元）")
    @ApiModelProperty(value = "佣金（元）")
    private BigDecimal rakeoff;

    /**
     * 操作费（元）
     */
    @Excel(name = "操作费（元）")
    @ApiModelProperty(value = "操作费（元）")
    private BigDecimal operateCost;

    /**
     * 广告费占比，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
     */
    @Digits(integer = 1, fraction = 4, message = "广告费占比(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "广告费占比，如是2%，则存储的值为：0.02")
    private BigDecimal advertiseCostRate;

    /**
     * 广告费占比
     */
    @TableField(exist = false)
    @Excel(name = "广告费占比(%)")
    @ApiModelProperty(value = "广告费占比")
    private String advertiseCostRateString;

    /**
     * 广告费（元）
     */
    @Excel(name = "广告费（元）")
    @ApiModelProperty(value = "广告费（元）")
    private BigDecimal advertiseCost;

    /**
     * 其它
     */
    @Excel(name = "其它")
    @ApiModelProperty(value = "其它")
    private String otherComment;

    /**
     * 利润（元）
     */
    @Excel(name = "利润（元）")
    @ApiModelProperty(value = "利润（元）")
    private BigDecimal profit;

    /**
     * 利润率，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
     */
    @Digits(integer = 1, fraction = 4, message = "利润率(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "利润率，如是2%，则存储的值为：0.02")
    private BigDecimal profitRate;

    /**
     * 利润率
     */
    @TableField(exist = false)
    @Excel(name = "利润率(%)")
    @ApiModelProperty(value = "利润率")
    private String profitRateString;

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
