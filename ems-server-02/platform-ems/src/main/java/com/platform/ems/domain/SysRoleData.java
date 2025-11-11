package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.entity.SysUserDataRole;
import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 数据角色对象 sys_role_data
 *
 * @author chenkw
 * @date 2023-05-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "sys_role_data")
public class SysRoleData extends EmsBaseEntity {

    /**
     * 租户id
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户id")
    private String clientId;

    /**
     * 系统SID-数据角色
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-数据角色")
    private Long roleDataSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] roleDataSidList;

    /**
     * 数据角色编号
     */
    @ApiModelProperty(value = "数据角色编号")
    private String roleDataCode;

    /**
     * 数据角色名称
     */
    @ApiModelProperty(value = "数据角色名称")
    private String roleDataName;

    /**
     * 角色级别
     */
    @ApiModelProperty(value = "角色级别")
    private String roleLevel;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色级别")
    private String[] roleLevelList;

    /**
     * 角色类别
     */
    @ApiModelProperty(value = "角色类别")
    private String roleCategory;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色类别")
    private String[] roleCategoryList;

    /**
     * 角色来源
     */
    @ApiModelProperty(value = "角色来源")
    private String roleSource;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色来源")
    private String[] roleSourceList;

    /**
     * 父角色
     */
    @ApiModelProperty(value = "父角色")
    private String parentRole;

    /**
     * 权限字符
     */
    @ApiModelProperty(value = "权限字符")
    private String accessText;

    /**
     * 处理状态（数据字典的键值）
     */
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;

    /**
     * 启用停用（数据字典的键值）
     */
    @ApiModelProperty(value = "启用停用（数据字典的键值）")
    private String status;

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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色信息-权限对象对象")
    private List<SysDataRoleAuthorityObject> objectList;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色信息-用户对象列表")
    private List<SysUserDataRole> userDataRoleList;
}
