package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 付款单-核销货款明细表 s_fin_pay_bill_item_huokuan
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_fin_pay_bill_item_huokuan")
public class FinPayBillItemHuokuan extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-核销货款明细表
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-核销货款明细表")
    private Long payBillItemHuokuanSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] payBillItemHuokuanSidList;

    /**
     * 系统SID-付款单
     */
    @Excel(name = "系统SID-付款单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long payBillSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-付款单")
    private Long[] payBillSidList;

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
     * 本次申请金额(含税)
     */
    @Digits(integer = 8, fraction = 4, message = "明细金额整数位上限为8位，小数位上限为4位")
    @Excel(name = "本次申请金额(含税)")
    @ApiModelProperty(value = "本次申请金额(含税)")
    private BigDecimal currencyAmountTax;

    /**
     * 创建人账号（用户账号）
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
     * 更新人账号（用户账号）
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
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


}
