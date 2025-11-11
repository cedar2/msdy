package com.platform.ems.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 角色信息-权限字段值对象 s_sys_role_authority_field_value
 *
 * @author chenkw
 * @date 2021-12-28
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_role_authority_field_value")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysRoleAuthorityFieldValue extends BaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-角色信息-权限字段值
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-角色信息-权限字段值")
    private Long roleAuthorityFieldValueSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] roleAuthorityFieldValueSidList;
    /**
     * 系统SID-角色信息-权限对象
     */
    @Excel(name = "系统SID-角色信息-权限对象")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-角色信息-权限对象")
    private Long roleAuthorityObjectSid;

    /**
     * 系统SID-权限字段
     */
    @Excel(name = "系统SID-权限字段")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-权限字段")
    private Long authorityFieldSid;

    @Excel(name = "权限字段编码")
    @ApiModelProperty(value = "权限字段编码")
    private String authorityFieldCode;

    /**
     * 权限字段值
     */
    @Excel(name = "权限字段值")
    @ApiModelProperty(value = "权限字段值")
    private String authorityFieldValue;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

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
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

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
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据对象类别sid")
    private Long dataobjectCategorySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别编码")
    private String dataobjectCategoryCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别名称")
    private String dataobjectCategoryName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "权限对象sid")
    private Long authorityObjectSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "权限对象编码")
    private String authorityObjectCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "权限对象名称")
    private String authorityObjectName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色id（多选）")
    private List<Long> roleIdList;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @TableField(exist = false)
    @ApiModelProperty(value = "权限字段名称")
    private String authorityFieldName;

    @TableField(exist = false)
    @ApiModelProperty(value = "对应的单据数据库表字段（人工编码）")
    private String databaseFieldname;

    @TableField(exist = false)
    @ApiModelProperty(value = "权限字段类别")
    private String authorityFieldCategory;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
    private String updaterAccountName;

}
