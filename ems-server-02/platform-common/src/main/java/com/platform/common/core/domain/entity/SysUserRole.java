package com.platform.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 用户操作角色 sys_user_role
 *
 * @author chenkw
 * @date 2023-05-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "sys_user_role")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserRole {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户ID")
    private Long[] userIdList;

    /**
     * 用户账号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色ID")
    private Long[] roleIdList;

    /**
     * 角色名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 角色key
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "角色key")
    private String roleKey;

    /**
     * 角色状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "角色状态")
    private String status;

    /**
     * 备注
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String createBy;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;
}
