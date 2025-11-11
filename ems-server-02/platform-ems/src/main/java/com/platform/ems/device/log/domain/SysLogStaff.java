package com.platform.ems.device.log.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 员工档案接口日志对象 s_sys_log_staff
 *
 * @author Straw
 * @date 2023-03-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_log_staff")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysLogStaff {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    String clientId;

    /**
     * 员工档案日志SID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "员工档案日志SID")
    Long logStaffSid;

    /**
     * 系统SID-员工档案
     */
    @Excel(name = "系统SID-员工档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-员工档案")
    Long staffSid;

    /**
     * 员工编号（人工编码）
     */
    @Excel(name = "员工编号（人工编码）")
    @ApiModelProperty(value = "员工编号（人工编码）")
    String staffCode;

    /**
     * 员工姓名
     */
    @Excel(name = "员工姓名")
    @ApiModelProperty(value = "员工姓名")
    String staffName;

    /**
     * 主属公司sid
     */
    @Excel(name = "主属公司sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属公司sid")
    Long defaultCompanySid;

    /**
     * 主属公司编码
     */
    @Excel(name = "主属公司编码")
    @ApiModelProperty(value = "主属公司编码")
    String defaultCompanyCode;

    /**
     * 主属工厂sid
     */
    @Excel(name = "主属工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主属工厂sid")
    Long defaultPlantSid;

    /**
     * 主属工厂编码
     */
    @Excel(name = "主属工厂编码")
    @ApiModelProperty(value = "主属工厂编码")
    String defaultPlantCode;

    /**
     * 系统SID-工作中心(班组)
     */
    @Excel(name = "系统SID-工作中心(班组)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    Long workCenterSid;

    /**
     * 工作中心(班组)编码
     */
    @Excel(name = "工作中心(班组)编码")
    @ApiModelProperty(value = "工作中心(班组)编码")
    String workCenterCode;

    /**
     * 推送人账号（用户账号）
     */
    @Excel(name = "推送人账号（用户账号）")
    @ApiModelProperty(value = "推送人账号（用户账号）")
    String sendAccount = ApiThreadLocalUtil.getLoginUserUserName();

    /**
     * 推送时间
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "推送时间",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "推送时间")
    Date sendDate = new Date();

    /**
     * 目标系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "目标系统（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "目标系统（数据字典的键值或配置档案的编码）")
    String dataTargetSys;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    String dataSourceSys;

}
