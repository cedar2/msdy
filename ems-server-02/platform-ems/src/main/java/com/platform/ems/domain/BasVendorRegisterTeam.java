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
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * 供应商注册-人员信息对象 s_bas_vendor_register_team
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_register_team")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorRegisterTeam extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-供应商注册人员信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册人员信息")
    private Long vendorRegisterTeamSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorRegisterTeamSidList;

    /**
     * 系统SID-供应商注册基本信息
     */
    @Excel(name = "系统SID-供应商注册基本信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商注册基本信息")
    private Long vendorRegisterSid;

    /**
     * 总人数
     */
    @Digits(integer = 5, fraction = 0, message = "总人数最大只支持输入5位")
    @Excel(name = "总人数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "总人数")
    private Long headcount;

    /**
     * 开发部人数
     */
    @Digits(integer = 5, fraction = 0, message = "开发部人数最大只支持输入5位")
    @Excel(name = "开发部人数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发部人数")
    private Long numberDevelop;

    /**
     * 品管部人数
     */
    @Digits(integer = 5, fraction = 0, message = "品管部人数最大只支持输入5位")
    @Excel(name = "品管部人数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品管部人数")
    private Long numberQuality;

    /**
     * 生产人数
     */
    @Digits(integer = 5, fraction = 0, message = "生产人数最大只支持输入5位")
    @Excel(name = "生产人数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产人数")
    private Long numberProduce;

    /**
     * 直接工人数
     */
    @Digits(integer = 5, fraction = 0, message = "直接工人数最大只支持输入5位")
    @Excel(name = "直接工人数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "直接工人数")
    private Long numberWorkerDirect;

    /**
     * 管理人员数
     */
    @Digits(integer = 5, fraction = 0, message = "管理人员数最大只支持输入5位")
    @Excel(name = "管理人员数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "管理人员数")
    private Long numberManager;

    /**
     * 月平均工时(小时)
     */
    @Digits(integer = 6, fraction = 1, message = "月平均工时(小时)整数位上限为6位，小数位上限为1位")
    @Excel(name = "月平均工时(小时)")
    @ApiModelProperty(value = "月平均工时(小时)")
    private BigDecimal monthManHour;

    /**
     * 月平均工资(元)
     */
    @Digits(integer = 6, fraction = 1, message = "月平均工资(元)整数位上限为6位，小数位上限为1位")
    @Excel(name = "月平均工资(元)")
    @ApiModelProperty(value = "月平均工资(元)")
    private BigDecimal monthWage;

    /**
     * 币种
     */
    @Excel(name = "币种", dictType = "s_currency")
    @ApiModelProperty(value = "币种（数据字典的键值）")
    private String currency;

    /**
     * 货币单位
     */
    @Excel(name = "货币单位", dictType = "s_currency_unit")
    @ApiModelProperty(value = "货币单位（数据字典的键值）")
    private String currencyUnit;

    /**
     * 是否安排住宿
     */
    @Excel(name = "是否安排住宿", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否安排住宿（数据字典的键值）")
    private String isProvideDormitory;

    /**
     * 是否有餐补
     */
    @Excel(name = "是否有餐补", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否有餐补（数据字典的键值）")
    private String isProvideDining;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
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

    @Excel(name = "更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
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

}
