package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * 供应商的人员信息对象 s_bas_vendor_team
 *
 * @author chenkw
 * @date 2022-01-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_vendor_team")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasVendorTeam extends EmsBaseEntity {

    /**
     * 租户id
     */
    @Excel(name = "租户id")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户id")
    private String clientId;

    /**
     * 系统SID-供应商的人员信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商的人员信息")
    private Long vendorTeamSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] vendorTeamSidList;
    /**
     * 系统SID-供应商档案sid
     */
    @Excel(name = "系统SID-供应商档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商档案sid")
    private Long vendorSid;

    /**
     * 总人数
     */
    @Excel(name = "总人数")
    @Digits(integer = 5, fraction = 0, message = "总人数长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "总人数")
    private Long headcount;

    /**
     * 开发部人数
     */
    @Excel(name = "开发部人数")
    @Digits(integer = 5, fraction = 0, message = "开发部人数长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发部人数")
    private Long numberDevelop;

    /**
     * 品管部人数
     */
    @Excel(name = "品管部人数")
    @Digits(integer = 5, fraction = 0, message = "品管部人数长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品管部人数")
    private Long numberQuality;

    /**
     * 生产人数
     */
    @Excel(name = "生产人数")
    @Digits(integer = 5, fraction = 0, message = "生产人数长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产人数")
    private Long numberProduce;

    /**
     * 直接工人数
     */
    @Excel(name = "直接工人数")
    @Digits(integer = 5, fraction = 0, message = "直接工人数长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "直接工人数")
    private Long numberWorkerDirect;

    /**
     * 管理人员数
     */
    @Excel(name = "管理人员数")
    @Digits(integer = 5, fraction = 0, message = "管理人员数长度不能超过5位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "管理人员数")
    private Long numberManager;

    /**
     * 月平均工时(小时)
     */
    @Excel(name = "月平均工时(小时)")
    @Digits(integer = 6, fraction = 1, message = "月平均工时(小时)的整数位上限为6位，小数位上限为1位")
    @ApiModelProperty(value = "月平均工时(小时)")
    private BigDecimal monthManHour;

    /**
     * 月平均工资(元)
     */
    @Excel(name = "月平均工资(元)")
    @Digits(integer = 6, fraction = 1, message = "月平均工资(元)的整数位上限为6位，小数位上限为1位")
    @ApiModelProperty(value = "月平均工资(元)")
    private BigDecimal monthWage;

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
     * 是否安排住宿
     */
    @Excel(name = "是否安排住宿")
    @ApiModelProperty(value = "是否安排住宿")
    private String isProvideDormitory;

    /**
     * 是否有餐补
     */
    @Excel(name = "是否有餐补")
    @ApiModelProperty(value = "是否有餐补")
    private String isProvideDining;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人账号")
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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;


}
