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


/**
 * 流程节点属性配置对象 s_sys_process_task_properties_config
 *
 * @author qhq
 * @date 2021-10-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_process_task_properties_config")
public class SysProcessTaskPropertiesConfig extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 参数ID */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "参数ID")
    private Long id;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] idList;

    /** 参数名称 */
    @Excel(name = "参数名称")
    @ApiModelProperty(value = "参数名称")
    private String name;

    @Excel(name = "参数值")
    @ApiModelProperty(value = "参数值")
    private String value;

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
}
