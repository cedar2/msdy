package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * SCM工作流确认配置对象 s_sys_scm_approve_setting_confirm
 *
 * @author chenkw
 * @date 2023-05-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_scm_approve_setting_confirm")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysScmApproveSettingConfirm {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * SCM工作流确认配置数据sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SCM工作流确认配置数据sid")
    private Long scmApproverSettingConfirmSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] scmApproverSettingConfirmSidList;

    /**
     * 数据对象类别code
     */
    @ApiModelProperty(value = "数据对象类别code")
    private String dataObjectCode;

    /**
     * 流程KEY
     */
    @ApiModelProperty(value = "流程KEY")
    private String approveKey;

    /**
     * 工厂编码，多值间用；隔开
     */
    @ApiModelProperty(value = "工厂编码，多值间用；隔开")
    private String plantCode;

    @ApiModelProperty(value ="启用停用")
    private String status;

	@ApiModelProperty(value ="备注")
	private String remark;

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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
