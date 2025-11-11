package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 权限对象-字段明细对象 s_sys_authority_object_field
 *
 * @author chenkw
 * @date 2021-12-28
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_authority_object_field")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAuthorityObjectField extends BaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-权限对象明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-权限对象明细")
    private Long authorityObjectFieldSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] authorityObjectFieldSidList;

    /**
     * 系统SID-权限对象
     */
    @Excel(name = "系统SID-权限对象")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-权限对象")
    private Long authorityObjectSid;

    /**
     * 系统SID-权限字段
     */
    @Excel(name = "系统SID-权限字段")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-权限字段")
    private Long authorityFieldSid;

    @ApiModelProperty(value = "权限对象对应权限字段的参数名称")
    private String authorityFieldParam;

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
     * 权限字段编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "权限字段编码")
    private String fieldCode;

    /**
     * 权限字段编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "权限字段名称")
    private String fieldName;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
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


    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
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
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
