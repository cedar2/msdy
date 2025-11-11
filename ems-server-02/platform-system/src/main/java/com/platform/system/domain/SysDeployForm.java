package com.platform.system.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 流程实例关联表单对象 sys_instance_form
 *
 * @author c
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_sys_deploy_form")
public class SysDeployForm extends EmsBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统SID-单据与工作流程实例关系信息 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long formProcessRelatSid;

    @TableField(exist = false)
    private Long[] formProcessRelatSidList;

    /** 流程定义key */
    @TableField(value = "`key`")
    private String key;

    /** 流程名称 */
    @TableField(value = "`name`")
    private String name;

    /** 流程定义ID */
    private String processDefintionId;

    /** 流程定义类型 */
    private String processDefintionType;

    /** 状态（ 0：未启用，1：启用，2：停用） */
    private String status;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date modifyDate;

    /** 创建人账号（用户名称） */
    private String creatorAccount;

    /** 更新人账号（用户名称） */
    private String updaterAccount;

    /** 创建人ID */
    private String createById;

    /** 更新人ID */
    private String modifyById;

	@TableField(exist = false)
	private List<SysProcessTaskConfig> taskConfigList;

}
