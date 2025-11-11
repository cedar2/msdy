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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 任务节点对象 s_prj_task
 *
 * @author chenkw
 * @date 2022-12-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_prj_task")
public class PrjTask extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-任务节点
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long taskSid;

    /**
     * 多选导出所选sid
     */
    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] taskSidList;

    /**
     * 任务节点名称
     */
    @NotBlank(message = "任务节点名称不能为空")
    @Excel(name = "任务节点名称")
    @ApiModelProperty(value = "任务节点名称")
    private String taskName;

    /**
     * 发起岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发起岗位sid")
    private Long startPositionSid;

    /**
     * 发起岗位sid多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发起岗位sid多选")
    private Long[] startPositionSidList;

    /**
     * 发起岗位code
     */
    @ApiModelProperty(value = "发起岗位code")
    private String startPositionCode;

    /**
     * 发起岗位code 数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发起岗位code 数组")
    private String[] startPositionCodeList;

    /**
     * 发起岗位
     */
    @TableField(exist = false)
    @Excel(name = "发起岗位")
    @ApiModelProperty(value = "发起岗位")
    private String startPositionName;

    /**
     * 负责岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责岗位sid")
    private Long chargePositionSid;

    /**
     * 负责岗位sid多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "负责岗位sid多选")
    private Long[] chargePositionSidList;

    /**
     * 负责岗位code
     */
    @ApiModelProperty(value = "负责岗位code")
    private String chargePositionCode;

    /**
     * 负责岗位code 数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "负责岗位code 数组")
    private String[] chargePositionCodeList;

    /**
     * 负责岗位
     */
    @TableField(exist = false)
    @Excel(name = "负责岗位")
    @ApiModelProperty(value = "负责岗位")
    private String chargePositionName;

    /**
     * 告知岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "告知岗位sid")
    private Long noticePositionSid;

    /**
     * 告知岗位sid多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "告知岗位sid多选")
    private Long[] noticePositionSidList;

    /**
     * 告知岗位code
     */
    @ApiModelProperty(value = "告知岗位code")
    private String noticePositionCode;

    /**
     * 告知岗位code 数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "告知岗位code 数组")
    private String[] noticePositionCodeList;

    /**
     * 告知岗位
     */
    @TableField(exist = false)
    @Excel(name = "告知岗位")
    @ApiModelProperty(value = "告知岗位")
    private String noticePositionName;

    /**
     * 关联业务单据sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联业务单据sid")
    private Long relateBusinessFormSid;

    /**
     * 关联业务单据sid (多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联业务单据sid")
    private Long[] relateBusinessFormSidList;

    /**
     * 关联业务单据code
     */
    @Excel(name = "关联业务单据", dictType = "s_relate_business_form")
    @ApiModelProperty(value = "关联业务单据code")
    private String relateBusinessFormCode;

    /**
     * 关联业务单据code 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联业务单据code")
    private String[] relateBusinessFormCodeList;

    /**
     * 结束标志（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "结束标志（数据字典的键值或配置档案的编码）")
    private String endFlag;

    /**
     * 标准用时（天）
     */
    @Excel(name = "标准用时(天)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "标准用时（天）")
    private Long standardTime;

    /**
     * 日历类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "日历类型", dictType = "s_day_type")
    @ApiModelProperty(value = "日历类型（数据字典的键值或配置档案的编码）")
    private String calendarType;

    /**
     * 是否监控（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否监控", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否监控（数据字典的键值或配置档案的编码）")
    private String isMonitor;

    /**
     * 所属任务阶段（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "所属任务阶段", dictType = "s_task_phase")
    @ApiModelProperty(value = "所属任务阶段（数据字典的键值或配置档案的编码）")
    private String taskPhase;

    /**
     * 所属任务阶段（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属任务阶段（多选）")
    private String[] taskPhaseList;

    /**
     * 任务节点编码
     */
    @Excel(name = "任务节点编码")
    @ApiModelProperty(value = "任务节点编码")
    private String taskCode;

    /**
     * 任务节点编码 (多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "任务节点编码（多选）")
    private String[] taskCodeList;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 所属业务板块（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "所属业务板块（数据字典的键值或配置档案的编码）")
    private String businessSection;

    /**
     * 节点级别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "节点级别（数据字典的键值或配置档案的编码）")
    private String nodeLevel;

    /**
     * 任务节点简称
     */
    @ApiModelProperty(value = "任务节点简称")
    private String taskShortName;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
