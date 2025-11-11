package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 数据角色信息-权限对象对象 s_sys_data_role_authority_object
 *
 * @author chenkw
 * @date 2023-05-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_data_role_authority_object")
public class SysDataRoleAuthorityObject extends EmsBaseEntity {

    /**
     * 租户id
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户id")
    private String clientId;

    /**
     * 系统SID-数据角色信息-权限对象
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-数据角色信息-权限对象")
    private Long dataRoleAuthorityObjectSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dataRoleAuthorityObjectSidList;

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
    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色编码")
    private String roleDataCode;

    /**
     * 数据角色名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色名称")
    private String roleDataName;

    /**
     * 系统SID-权限对象
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-权限对象")
    private Long authorityObjectSid;

    /**
     * 权限对象编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "权限对象编码")
    private String objectCode;

    /**
     * 权限对象名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "权限对象名称")
    private String objectName;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "权限字段-权限字段值组合的字符串")
    private String fieldValueString;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色信息-权限字段值对象")
    private List<SysDataRoleAuthorityFieldValue> fieldValueList;
}
