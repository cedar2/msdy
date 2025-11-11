package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 作业类型_租户级对象 s_sys_client_movement_type
 *
 * @author chenkw
 * @date 2022-06-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_client_movement_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysClientMovementType extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-作业类型_租户级
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-作业类型_租户级")
    private Long clientMovementTypeSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] clientMovementTypeSidList;

    /**
     * 作业类型SID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "作业类型SID")
    private Long movementTypeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "作业类型SID")
    private Long[] movementTypeSidList;

    /**
     * 作业类型code
     */
    @Excel(name = "作业类型编码")
    @ApiModelProperty(value = "作业类型编码")
    private String movementTypeCode;

    @Excel(name = "作业类型名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "作业类型名称")
    private String movementTypeName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "处理状态不能为空")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
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

}
