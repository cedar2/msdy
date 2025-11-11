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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_process_task_config")
public class SysProcessTaskConfig extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**  */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] idList;

    /** 流程key */
    @Excel(name = "流程key")
    @ApiModelProperty(value = "流程key")
    private String processKey;

    /** 任务节点id */
    @Excel(name = "任务节点id")
    @ApiModelProperty(value = "任务节点id")
    private String taskId;

    /** 任务节点name */
    @Excel(name = "任务节点name")
    @ApiModelProperty(value = "任务节点name")
    private String taskName;

    /** 参数id：task_properties_config表 */
    @Excel(name = "参数id：task_properties_config表")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "参数id：task_properties_config表")
    private Long propertiesId;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "子表List")
    private List<SysProcessTaskPropertiesConfig> propertiesConfigList;

}
