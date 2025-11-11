package com.platform.common.core.domain.entity;

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

/**
 * 用户-数据角色对象 s_sys_user_data_role
 *
 * @author chenkw
 * @date 2023-05-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_user_data_role")
public class SysUserDataRole extends EmsBaseEntity {

    /**
     * 租户id
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户id")
    private String clientId;

    /**
     * 用户-数据角色数据sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户-数据角色数据sid")
    private Long userDataRoleSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] userDataRoleSidList;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户id")
    private Long[] userIdList;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 系统SID-数据角色
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-数据角色")
    private Long roleDataSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-数据角色")
    private Long[] roleDataSidList;

    /**
     * 数据角色编码
     */
    @ApiModelProperty(value = "数据角色编码")
    private String roleDataCode;

    /**
     * 数据角色名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色名称")
    private String roleDataName;

    /**
     * 数据角色启停状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色启停状态")
    private String status;

    /**
     * 数据角色处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司")
    private String companyShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门")
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "岗位")
    private String positionName;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

}
