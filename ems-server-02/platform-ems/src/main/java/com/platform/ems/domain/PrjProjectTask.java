package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;

/**
 * 项目档案-任务对象 s_prj_project_task
 *
 * @author chenkw
 * @date 2022-12-15
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_prj_project_task")
public class PrjProjectTask extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-项目档案-任务明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案-任务明细")
    private Long projectTaskSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] projectTaskSidList;

    /**
     * 系统SID-项目档案
     */
    @Excel(name = "系统SID-项目档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    /**
     * 系统SID-项目档案 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-项目档案 多选")
    private Long[] projectSidList;

    /**
     * 项目档案编码
     */
    @Excel(name = "项目档案编码")
    @ApiModelProperty(value = "项目档案编码")
    private Long projectCode;

    /**
     * 项目档案名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目档案名称")
    private String projectName;

    /**
     * 系统SID-任务节点
     */
    @Excel(name = "系统SID-任务节点")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long taskSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long[] taskSidList;

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
    @Excel(name = "任务节点名称")
    @ApiModelProperty(value = "任务节点名称")
    private String taskName;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @Digits(integer = 3, fraction = 2, message = "序号整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    /**
     * 明细报表是否修改任务状态的字段
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "明细报表是否修改任务状态的字段")
    private String taskStatusIsUpdate;

    /**
     * 任务状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "任务状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "任务状态（数据字典的键值或配置档案的编码）")
    private String taskStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "任务状态（数据字典的键值或配置档案的编码）")
    private String[] taskStatusList;

    /**
     * 计划开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改计划开始日期")
    private String planStartDateIsUpdate;

    /**
     * 计划完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改计划完成日期")
    private String planEndDateIsUpdate;

    /**
     * 明细报表是否修改实际完成日期的字段
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "明细报表是否修改实际完成日期的字段")
    private String actualEndDateIsUpdate;

    /**
     * 实际完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    /**
     * 到期提醒天数
     */
    @Excel(name = "到期提醒天数")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "到期提醒天数")
    private Long toexpireDaysTask;

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
     * 发起岗位sid
     */
    @Excel(name = "发起岗位sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发起岗位sid")
    private Long startPositionSid;

    /**
     * 发起岗位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发起岗位")
    private String startPositionName;

    @ApiModelProperty(value = "发起岗位")
    private String startPositionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "发起岗位")
    private String[] startPositionCodeList;

    /**
     * 负责岗位sid
     */
    @Excel(name = "负责岗位sid")
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
    @Excel(name = "告知岗位sid")
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

    @TableField(exist = false)
    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    /**
     * 所属任务阶段（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "所属任务阶段（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "所属任务阶段（数据字典的键值或配置档案的编码）")
    private String taskPhase;

    /**
     * 所属业务板块（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "所属业务板块（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "所属业务板块（数据字典的键值或配置档案的编码）")
    private String businessSection;

    /**
     * 即将到期预警天数比例，如是2%，则存储的值为：0.02
     */
    @Excel(name = "即将到期预警天数比例，如是2%，则存储的值为：0.02")
    @ApiModelProperty(value = "即将到期预警天数比例，如是2%，则存储的值为：0.02")
    private BigDecimal overdueWarnRate;

    /**
     * 日历类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "日历类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "日历类型（数据字典的键值或配置档案的编码）")
    private String calendarType;

    /**
     * 关联业务单据sid
     */
    @Excel(name = "关联业务单据sid")
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
     * 前置任务节点，如包含多个，则用英文分号";"隔开
     */
    @Excel(name = "前置任务节点，如包含多个，则用英文分号;隔开")
    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号;隔开")
    private String preTask;

    @TableField(exist = false)
    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号;隔开")
    private String preTaskName;

    /**
     * 前置任务节点 (数组)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "前置任务节点 (数组)")
    private String[] preTaskList;

    @ApiModelProperty(value = "优先级(任务)")
    private String priorityTask;

    @TableField(exist = false)
    @ApiModelProperty(value = "优先级(任务)是否修改")
    private String priorityTaskIsUpd;

    @ApiModelProperty(value = "处理序号(任务)")
    private String handleSortTask;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理序号(任务)是否修改")
    private String handleSortTaskIsUpd;

    /**
     * 前置序号，如包含多个，则用英文分号";"隔开
     */
    @Excel(name = "前置序号，如包含多个，则用英文分号;隔开")
    @ApiModelProperty(value = "前置序号，如包含多个，则用英文分号;隔开")
    private String preSort;

    /**
     * 是否监控（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "是否监控（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "是否监控（数据字典的键值或配置档案的编码）")
    private String isMonitor;

    /**
     * 项目任务执行提醒天数
     */
    @ApiModelProperty(value = "项目任务执行提醒天数")
    private Integer toexecuteNoticeDaysPrjTask;

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
     * 进度说明
     */
    @Excel(name = "进度说明")
    @ApiModelProperty(value = "进度说明")
    private String progressDescription;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径数组")
    private String[] picturePathList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前用户对应的员工sid")
    private Long currentUserIsLeaderSid;

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
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 计划完成日期设置T- 项目模板明细
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计划完成日期设置T- ")
    private Long planEndDateConfig;

    /**
     * 主表的处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 开发计划号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    /**
     * 开发计划名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    /**
     * 开发计划类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发计划类型")
    private String developType;

    @TableField(exist = false)
    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    /**
     * 计划完成日期(起)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期起")
    private String planEndDateBegin;

    /**
     * 计划完成日期(止)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期止")
    private String planEndDateEnd;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目负责人sid")
    private Long projectLeaderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String projectType;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目所属阶段（数据字典的键值或配置档案的编码）")
    private String projectPhase;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目状态（数据字典的键值或配置档案的编码）")
    private String projectStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售站点name")
    private String saleStationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "一次采购标识")
    private String firstPurchaseFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "二次采购标识")
    private String secondPurchaseFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "一次采购到货通知标识")
    private String arrivalNoticeFlagFirstPurchase;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)")
    private String yearmonthProject;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)起")
    private String yearmonthProjectBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)止")
    private String yearmonthProjectEnd;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别")
    private String groupType;

    @TableField(exist = false)
    @ApiModelProperty(value = "组别(多选)")
    private String[] groupTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品MSKU编码(ERP)")
    private String erpMaterialMskuCode;

    /**
     * 附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件对象")
    private List<PrjProjectTaskAttach> attachmentList;

}
