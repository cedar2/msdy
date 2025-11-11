package com.platform.ems.domain;

import java.math.BigDecimal;
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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 项目任务模板-明细对象 s_prj_task_template_item
 *
 * @author chenkw
 * @date 2022-12-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_prj_task_template_item")
public class PrjTaskTemplateItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-项目任务模板-任务明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板-任务明细")
    private Long taskTemplateItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] taskTemplateItemSidList;

    /**
     * 系统SID-项目任务模板
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板")
    private Long taskTemplateSid;

    /**
     * 系统SID-项目任务模板(多选)
     */
    @ApiModelProperty(value = "主表sid数组")
    @TableField(exist = false)
    private Long[] taskTemplateSidList;

    /**
     * 项目任务模板编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目任务模板编码")
    private Long taskTemplateCode;

    /**
     * 系统SID-任务节点
     */
    @NotNull(message = "任务明细中任务节点不能为空")
    @Excel(name = "系统SID-任务节点")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long taskSid;

    /**
     * 任务节点编码
     */
    @Excel(name = "任务节点编码")
    @ApiModelProperty(value = "任务节点编码")
    private String taskCode;

    /**
     * 任务节点名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "任务节点编码")
    private String taskName;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @Digits(integer = 3, fraction = 2, message = "序号整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    /**
     * 所属任务阶段（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "所属任务阶段")
    @ApiModelProperty(value = "所属任务阶段（数据字典的键值或配置档案的编码）")
    private String taskPhase;

    /**
     * 所属业务板块（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "所属业务板块")
    @ApiModelProperty(value = "所属业务板块（数据字典的键值或配置档案的编码）")
    private String businessSection;

    /**
     * 计划完成日期设置T-
     */
    @Excel(name = "计划完成日期设置T- ")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计划完成日期设置T- ")
    private Long planEndDateConfig;

    /**
     * 发起岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发起岗位sid")
    private Long startPositionSid;

    @ApiModelProperty(value = "发起岗位")
    private String startPositionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "发起岗位")
    private String[] startPositionCodeList;

    /**
     * 发起岗位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发起岗位")
    private String startPositionName;

    /**
     * 负责岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责岗位sid")
    private Long chargePositionSid;

    /**
     * 负责岗位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "负责岗位")
    private String chargePositionName;

    @ApiModelProperty(value = "负责岗位")
    private String chargePositionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责岗位")
    private String[] chargePositionCodeList;

    /**
     * 告知岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "告知岗位sid")
    private Long noticePositionSid;

    /**
     * 告知岗位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "告知岗位")
    private String noticePositionName;

    @ApiModelProperty(value = "告知岗位")
    private String noticePositionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "告知岗位")
    private String[] noticePositionCodeList;

    @ApiModelProperty(value = "处理人(任务)（用户账号）")
    private String handlerTask;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理人(任务)（用户账号）")
    private String handlerTaskId;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理人(任务)（用户昵称）")
    private String handlerTaskName;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理人(任务)（用户账号多选）")
    private String[] handlerTaskList;

    /**
     * 即将到期预警天数比例，如是2%，则存储的值为：0.02
     */
    @Excel(name = "即将到期预警天数比例，如是2%，则存储的值为：0.02")
    @ApiModelProperty(value = "即将到期预警天数比例，如是2%，则存储的值为：0.02")
    private BigDecimal overdueWarnRate;

    /**
     * 日历类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "日历类型")
    @ApiModelProperty(value = "日历类型（数据字典的键值或配置档案的编码）")
    private String calendarType;

    /**
     * 关联业务单据sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联业务单据sid")
    private Long relateBusinessFormSid;

    /**
     * 关联业务单据单号
     */
    @Excel(name = "关联业务单据单号")
    @ApiModelProperty(value = "关联业务单据单号")
    private String relateBusinessFormCode;

    /**
     * 前置任务节点，如包含多个，则用英文分号隔开
     */
    @Excel(name = "前置任务节点，如包含多个，则用英文分号隔开")
    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号隔开")
    private String preTask;

    /**
     * 前置任务节点数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "前置任务节点 数组")
    private String[] preTaskList;

    /**
     * 前置任务节点名称分号隔开
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "前置任务节点名称分号隔开")
    private String preTaskName;

    /**
     * 前置序号，如包含多个，则用英文分号隔开
     */
    @Excel(name = "前置序号，如包含多个，则用英文分号隔开")
    @ApiModelProperty(value = "前置序号，如包含多个，则用英文分号隔开")
    private String preSort;

    /**
     * 是否监控（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否监控（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否监控（数据字典的键值或配置档案的编码）")
    private String isMonitor;

    /**
     * 任务用时（天）
     */
    @Excel(name = "任务用时（天）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "任务用时（天）")
    private Long templateTime;

    /**
     * 完成标准
     */
    @Excel(name = "完成标准")
    @ApiModelProperty(value = "完成标准")
    private String completeStandard;

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

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
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
