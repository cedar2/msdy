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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Digits;

import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 试销结果单-计划项对象 s_frm_trialsale_result_plan_item
 *
 * @author linhongwei
 * @date 2022-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_frm_trialsale_result_plan_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FrmTrialsaleResultPlanItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-试销结果单计划项
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-试销结果单计划项")
    private Long trialsaleResultPlanItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] trialsaleResultPlanItemSidList;
    /**
     * 系统SID-试销结果单
     */
    @Excel(name = "系统SID-试销结果单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-试销结果单")
    private Long trialsaleResultSid;

    /**
     * 项目
     */
    @Excel(name = "项目")
    @Length(max = 30, message = "项目最大长度不能超过30位")
    @ApiModelProperty(value = "项目")
    private String itemName;

    /**
     * 流量转化率，如是2%，则存储的值为：0.0， 提示用 百分比的规则提示
     */
    @Digits(integer = 1, fraction = 4, message = "流量转化率(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "流量转化率，如是2%，则存储的值为：0.02")
    private BigDecimal liulConversionRate;

    /**
     * 流量转化率，如是2%
     */
    @TableField(exist = false)
    @Excel(name = "流量转化率(%)")
    @ApiModelProperty(value = "流量转化率，如是2%")
    private String liulConversionRateString;

    /**
     * 销售额（元）
     */
    @Excel(name = "销售额（元）")
    @ApiModelProperty(value = "销售额（元）")
    private String saleAmount;

    /**
     * 展现量
     */
    @Excel(name = "展现量")
    @ApiModelProperty(value = "展现量")
    private String displayQuantity;

    /**
     * 流量
     */
    @Excel(name = "流量")
    @ApiModelProperty(value = "流量")
    private String visitorQuantity;

    /**
     * 销量
     */
    @Excel(name = "销量")
    @ApiModelProperty(value = "销量")
    private String saleQuantity;

    /**
     * 点击量
     */
    @Excel(name = "点击量")
    @ApiModelProperty(value = "点击量")
    private String clickNum;

    /**
     * 点击率，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
     */
    @Digits(integer = 1, fraction = 4, message = "点击率(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "点击率，如是2%，则存储的值为：0.02")
    private BigDecimal clickRate;

    /**
     * 点击率（%）
     */
    @TableField(exist = false)
    @Excel(name = "点击率(%)")
    @ApiModelProperty(value = "点击率（%）")
    private String clickRateString;

    /**
     * 点击转化率，如是2%，则存储的值为：0.02   提示用 百分比的规则提示
     */
    @Digits(integer = 1, fraction = 4, message = "点击转化率(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "点击转化率，如是2%，则存储的值为：0.02")
    private BigDecimal dianjConversionRate;

    /**
     * 点击转化率，如是2%
     */
    @TableField(exist = false)
    @Excel(name = "点击转化率(%)")
    @ApiModelProperty(value = "点击转化率，如是2%")
    private String dianjConversionRateString;

    /**
     * 花费（元）
     */
    @Excel(name = "花费（元）")
    @ApiModelProperty(value = "花费（元）")
    private BigDecimal cost;

    /**
     * 成交单量
     */
    @Excel(name = "成交单量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "成交单量")
    private Long orderNum;

    /**
     * 成交金额（元）
     */
    @Excel(name = "成交金额（元）")
    @ApiModelProperty(value = "成交金额（元）")
    private BigDecimal orderAmount;

    /**
     * 费用比（%），如是2%，则存储的值为：0.02   提示用 百分比的规则提示
     */
    @Digits(integer = 1, fraction = 4, message = "费用比(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "费用比（%），如是2%，则存储的值为：0.02")
    private BigDecimal costRate;

    /**
     * 费用比（%）
     */
    @TableField(exist = false)
    @Excel(name = "费用比(%)")
    @ApiModelProperty(value = "费用比（%）")
    private String costRateString;

    /**
     * ACOS（%），如是2%，则存储的值为：0.02
     */
    @Digits(integer = 1, fraction = 4, message = "ACOS(%)整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "ACOS（%），如是2%，则存储的值为：0.02")
    private BigDecimal acos;

    /**
     * ACOS（%）
     */
    @TableField(exist = false)
    @Excel(name = "ACOS(%)")
    @ApiModelProperty(value = "ACOS（%）")
    private String acosString;

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
