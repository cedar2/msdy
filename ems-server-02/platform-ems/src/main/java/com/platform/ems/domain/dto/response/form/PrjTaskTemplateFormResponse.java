package com.platform.ems.domain.dto.response.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Straw
 * @date 2022/12/20
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrjTaskTemplateFormResponse {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板")
    private Long taskTemplateSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板-任务明细")
    private Long taskTemplateItemSid;

    /**
     * 项目任务模板名称
     */
    @Excel(name = "任务模板名称")
    @ApiModelProperty(value = "项目任务模板名称")
    String taskTemplateName;

    /**
     * 项目类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    String projectType;

    /**
     * 总任务数
     */
    @Excel(name = "总任务数")
    @ApiModelProperty(value = "总任务数")
    String itemCount;

    /**
     * 模版周期（天）
     */
    @Excel(name = "模版周期（天）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "模版周期（天）")
    Long taskTemplateTime;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态",
           dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    String handleStatus;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @Excel(name = "启用/停用",
           dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    String status;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    BigDecimal sort;

    /**
     * 任务节点名称
     */
    @Excel(name = " 任务节点")
    @ApiModelProperty(value = "任务节点编码")
    String taskName;

    @Excel(name = " 处理人(任务)")
    @ApiModelProperty(value = "处理人(任务)（用户昵称）")
    private String handlerTaskName;

    /**
     * 所属任务阶段（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "所属任务阶段", dictType = "s_task_phase")
    @ApiModelProperty(value = "所属任务阶段（数据字典的键值或配置档案的编码）")
    String taskPhase;

    /**
     * 任务用时（天）
     */
    @Excel(name = "任务用时（天）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "任务用时（天）")
    Long itemTemplateTime;

    /**
     * 计划完成日期设置T-
     */
    @Excel(name = "计划完成日期设置T- ")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计划完成日期设置T- ")
    Long planEndDateConfig;

    /**
     * 发起岗位
     */
    @Excel(name = "发起岗位")
    @ApiModelProperty(value = "发起岗位")
    String startPositionName;

    @ApiModelProperty(value = "发起岗位")
    String startPositionCode;

    /**
     * 负责岗位
     */
    @Excel(name = "负责岗位")
    @ApiModelProperty(value = "负责岗位")
    String chargePositionName;

    @ApiModelProperty(value = "负责岗位")
    String chargePositionCode;

    /**
     * 告知岗位
     */
    @Excel(name = "告知岗位")
    @ApiModelProperty(value = "告知岗位")
    String noticePositionName;

    @ApiModelProperty(value = "告知岗位")
    String noticePositionCode;

    /**
     * 即将到期预警天数比例，如是2%，则存储的值为：0.02
     */
    @Excel(name = "即将到期预警天数比例")
    @ApiModelProperty(value = "即将到期预警天数比例，如是2%，则存储的值为：0.02")
    BigDecimal overdueWarnRate;


    /**
     * 日历类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "日历类型",
           dictType = "s_day_type")
    @ApiModelProperty(value = "日历类型（数据字典的键值或配置档案的编码）")
    String calendarType;

    /**
     * 关联业务单据单号
     */
    @Excel(name = "关联业务单据单", dictType = "s_relate_business_form")
    @ApiModelProperty(value = "关联业务单据单号")
    String relateBusinessFormCode;

    /**
     * 前置任务节点，如包含多个，则用英文分号隔开
     */
    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号隔开")
    String preTaskName;

    @Excel(name = "前置任务节点")
    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号隔开")
    String preTask;

    /**
     * 是否监控（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否监控", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否监控（数据字典的键值或配置档案的编码）")
    String isMonitor;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    String remark;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    String creatorAccountName;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    Date createDate;

}
