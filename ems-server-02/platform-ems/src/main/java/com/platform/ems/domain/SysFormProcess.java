package com.platform.ems.domain;

import java.util.Date;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 单据关联流程实例对象 s_sys_form_process
 *
 * @author qhq
 * @date 2021-09-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_form_process")
public class SysFormProcess extends EmsBaseEntity {

    /**  */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] idList;

    /** 流程实例ID */
    @Excel(name = "流程实例ID")
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /** 单据ID */
    @Excel(name = "单据ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "单据ID")
    private Long formId;

    /** 单据类型 */
    @Excel(name = "单据类型")
    @ApiModelProperty(value = "单据类型")
    private String formType;

    /** 流程状态 */
    @Excel(name = "流程状态")
    @ApiModelProperty(value = "流程状态")
    private String formStatus;

    /** 当前审批节点 */
    @Excel(name = "当前审批节点")
    @ApiModelProperty(value = "当前审批节点")
    private String approvalNode;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 当前审批人id */
    @Excel(name = "当前审批人id")
    @ApiModelProperty(value = "当前审批人id")
    private String approvalUserId;

    /** 当前审批人name */
    @Excel(name = "当前审批人name")
    @ApiModelProperty(value = "当前审批人name")
    private String approvalUserName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date modifyDate;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    @ApiModelProperty(value = "创建人ID")
    private String createById;

    /** 更新人ID */
    @Excel(name = "更新人ID")
    @ApiModelProperty(value = "更新人ID")
    private String modifyById;

    /** 状态 */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "状态")
    private String status;

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;
}
