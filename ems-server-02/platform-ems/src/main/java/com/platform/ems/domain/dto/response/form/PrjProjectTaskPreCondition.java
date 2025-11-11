package com.platform.ems.domain.dto.response.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 查询项目前置任务完成状况报表
 *
 * @author chenkw
 * @date 2023-02-15
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrjProjectTaskPreCondition {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案-任务明细")
    private Long projectTaskSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long taskSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划sid")
    private Long developPlanSid;

    @ApiModelProperty(value = "预警 0 为红灯")
    private String light;

    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @ApiModelProperty(value = "任务节点编码")
    private String taskCode;

    @ApiModelProperty(value = "任务节点编码")
    private String[] taskCodeList;

    @Excel(name = "任务节点")
    @ApiModelProperty(value = "任务节点名称")
    private String taskName;

    @Excel(name = "计划开始日期(任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期(任务)")
    private Date planStartDate;

    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号;隔开")
    private String preTask;

    @ApiModelProperty(value = "前置任务节点编码")
    private String preTaskCode;

    @Excel(name = "前置任务节点")
    @ApiModelProperty(value = "前置任务节点")
    private String preTaskName;

    @Excel(name = "前置任务状态", dictType = "s_project_task_status")
    @ApiModelProperty(value = "前置任务状态")
    private String preTaskStatus;

    @Excel(name = "计划开始日期(前置任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期(前置任务)")
    private Date prePlanStartDate;

    @Excel(name = "计划完成日期(前置任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期(前置任务)")
    private Date prePlanEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @Excel(name = "实际完成日期(前置任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期(前置任务)")
    private Date preActualEndDate;

    @ApiModelProperty(value = "发起岗位编码")
    private String startPositionCode;

    @ApiModelProperty(value = "发起岗位编码")
    private String chargePositionCode;

    @ApiModelProperty(value = "发起岗位编码")
    private String noticePositionCode;

    @ApiModelProperty(value = "发起岗位")
    private String startPositionName;

    @ApiModelProperty(value = "发起岗位")
    private String chargePositionName;

    @ApiModelProperty(value = "发起岗位")
    private String noticePositionName;

    @ApiModelProperty(value = "发起岗位编码(前置任务)")
    private String preStartPositionCode;

    @ApiModelProperty(value = "发起岗位编码(前置任务)")
    private String preChargePositionCode;

    @ApiModelProperty(value = "发起岗位编码(前置任务)")
    private String preNoticePositionCode;

    @Excel(name = "发起岗位(前置任务)")
    @ApiModelProperty(value = "发起岗位(前置任务)")
    private String preStartPositionName;

    @Excel(name = "负责岗位(前置任务)")
    @ApiModelProperty(value = "发起岗位(前置任务)")
    private String preChargePositionName;

    @Excel(name = "告知岗位(前置任务)")
    @ApiModelProperty(value = "发起岗位(前置任务)")
    private String preNoticePositionName;

    @Excel(name = "任务状态", dictType = "s_project_task_status")
    @ApiModelProperty(value = "任务状态")
    private String taskStatus;

    @Excel(name = "计划完成日期(任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期(任务)")
    private Date planEndDate;

    @ApiModelProperty(value = "计划完成日期起")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完成日期止")
    private String planEndDateEnd;

    @Excel(name = "项目名称")
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @Excel(name = "项目状态", dictType = "s_project_status")
    @ApiModelProperty(value = "项目状态")
    private String projectStatus;

    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String[] groupTypeList;

    @Excel(name = "所属年月(项目)")
    @ApiModelProperty(value = "所属年月(项目)")
    private String yearmonthProject;

    @ApiModelProperty(value = "所属年月(项目)起")
    private String yearmonthProjectBegin;

    @ApiModelProperty(value = "所属年月(项目)止")
    private String yearmonthProjectEnd;

    @ApiModelProperty(value = "处理人(任务)")
    private String handlerTask;

    @ApiModelProperty(value = "处理人(任务 多选)")
    private String[] handlerTaskList;

    @Excel(name = "处理人(任务)")
    @ApiModelProperty(value = "处理人(任务)")
    private String handlerTaskName;

    @Excel(name = "优先级(任务)", dictType = "s_urgency_type")
    @ApiModelProperty(value = "优先级(任务)")
    private String priorityTask;

    @ApiModelProperty(value = "系统SID-任务节点")
    private Long[] taskSidList;

    @ApiModelProperty(value = "计划开始是否超时(任务)")
    private String isTaskTimeout;

    @ApiModelProperty(value = "计划开始日期起(任务)")
    private String planStartDateBegin;

    @ApiModelProperty(value = "计划开始日期止(任务)")
    private String planStartDateEnd;

    @ApiModelProperty(value = "任务状态(多选)")
    private String[] taskStatusList;

    @ApiModelProperty(value = "项目状态(多选)")
    private String[] projectStatusList;

    @ApiModelProperty(value = "创建人账号（下拉框）")
    private String creatorAccount;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前用户对应的员工sid")
    private Long currentUserIsLeaderSid;

    @ApiModelProperty(value = "创建人昵称（输入框）")
    private String creatorAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @TableField(exist = false)
    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
