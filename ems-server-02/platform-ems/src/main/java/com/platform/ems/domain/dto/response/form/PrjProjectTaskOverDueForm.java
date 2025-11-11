package com.platform.ems.domain.dto.response.form;

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
import lombok.Data;

import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;

/**
 * 项目档案-任务对象 s_prj_project_task
 *
 * @author chenkw
 * @date 2023-02-02
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PrjProjectTaskOverDueForm extends EmsBaseEntity {

    /**
     * 计划完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    /**
     * 项目档案编码
     */
    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目档案编码")
    private Long projectCode;

    /**
     * 项目档案名称
     */
    @Excel(name = "项目名称")
    @ApiModelProperty(value = "项目档案名称")
    private String projectName;

    @Excel(name = "任务节点")
    @ApiModelProperty(value = "任务节点名称")
    private String taskName;

    /**
     * 任务状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "任务状态", dictType = "s_project_task_status")
    @ApiModelProperty(value = "任务状态（数据字典的键值或配置档案的编码）")
    private String taskStatus;

    /**
     * 开发计划号
     */
    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    /**
     * 开发计划名称
     */
    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    /**
     * 发起岗位
     */
    @Excel(name = "发起岗位")
    @ApiModelProperty(value = "发起岗位")
    private String startPositionName;

    /**
     * 负责岗位
     */
    @Excel(name = "发起岗位")
    @ApiModelProperty(value = "负责岗位")
    private String chargePositionName;

    /**
     * 告知岗位
     */
    @Excel(name = "告知岗位")
    @ApiModelProperty(value = "告知岗位")
    private String noticePositionName;

    /**
     * 计划开始日期
     */
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    /**
     * 商品款号code
     */
    @Excel(name = "商品款号/SPU号")
    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    /**
     * 商品SKU条码code
     */
    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @Excel(name = "商品SKU编码(ERP)")
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    @Excel(name = "商品MSKU编码(ERP)")
    @ApiModelProperty(value = "商品MSKU编码(ERP)")
    private String erpMaterialMskuCode;

    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    @Excel(name = "所属年月(项目)")
    @ApiModelProperty(value = "所属年月(项目)")
    private String yearmonthProject;

    @Excel(name = "处理人(任务)")
    @ApiModelProperty(value = "处理人(任务)")
    private String handlerTaskName;

    @Excel(name = "优先级(任务)", dictType = "s_urgency_type")
    @ApiModelProperty(value = "优先级(任务)")
    private String priorityTask;

    @Excel(name = "样品号")
    @ApiModelProperty(value = "样品号")
    private String sampleCode;

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
    private Long[] projectTaskSidList;

    /**
     * 系统SID-项目档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    /**
     * 系统SID-项目档案 多选
     */
    @ApiModelProperty(value = "系统SID-项目档案 多选")
    private Long[] projectSidList;

    /**
     * 系统SID-任务节点
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long taskSid;

    @ApiModelProperty(value = "系统SID-任务节点")
    private Long[] taskSidList;

    /**
     * 任务节点编码
     */
    @ApiModelProperty(value = "任务节点编码")
    private String taskCode;

    /**
     * 序号
     */
    @Digits(integer = 3, fraction = 2, message = "序号整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    /**
     * 明细报表是否修改任务状态的字段
     */
    @ApiModelProperty(value = "明细报表是否修改任务状态的字段")
    private String taskStatusIsUpdate;

    @ApiModelProperty(value = "任务状态（数据字典的键值或配置档案的编码）")
    private String[] taskStatusList;

    @ApiModelProperty(value = "是否修改计划开始日期")
    private String planStartDateIsUpdate;

    @ApiModelProperty(value = "是否修改计划完成日期")
    private String planEndDateIsUpdate;

    /**
     * 明细报表是否修改实际完成日期的字段
     */
    @ApiModelProperty(value = "明细报表是否修改实际完成日期的字段")
    private String actualEndDateIsUpdate;

    /**
     * 实际完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    /**
     * 到期提醒天数
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "到期提醒天数")
    private Long toexpireDaysTask;

    /**
     * 发起岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发起岗位sid")
    private Long startPositionSid;

    @ApiModelProperty(value = "发起岗位")
    private String startPositionCode;

    @ApiModelProperty(value = "发起岗位")
    private String[] startPositionCodeList;

    /**
     * 负责岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责岗位sid")
    private Long chargePositionSid;

    @ApiModelProperty(value = "负责岗位")
    private String chargePositionCode;

    @ApiModelProperty(value = "负责岗位")
    private String[] chargePositionCodeList;

    /**
     * 告知岗位sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "告知岗位sid")
    private Long noticePositionSid;

    @ApiModelProperty(value = "告知岗位")
    private String noticePositionCode;

    @ApiModelProperty(value = "告知岗位")
    private String[] noticePositionCodeList;

    /**
     * 所属任务阶段（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "所属任务阶段（数据字典的键值或配置档案的编码）")
    private String taskPhase;

    /**
     * 所属业务板块（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "所属业务板块（数据字典的键值或配置档案的编码）")
    private String businessSection;

    /**
     * 即将到期预警天数比例，如是2%，则存储的值为：0.02
     */
    @ApiModelProperty(value = "即将到期预警天数比例，如是2%，则存储的值为：0.02")
    private BigDecimal overdueWarnRate;

    /**
     * 日历类型（数据字典的键值或配置档案的编码）
     */
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
    @ApiModelProperty(value = "关联业务单据单号")
    private String relateBusinessFormCode;

    /**
     * 前置任务节点，如包含多个，则用英文分号";"隔开
     */
    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号;隔开")
    private String preTask;

    /**
     * 前置任务节点 (数组)
     */
    @ApiModelProperty(value = "前置任务节点 (数组)")
    private String[] preTaskList;

    /**
     * 前置序号，如包含多个，则用英文分号";"隔开
     */
    @ApiModelProperty(value = "前置序号，如包含多个，则用英文分号;隔开")
    private String preSort;

    /**
     * 是否监控（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否监控（数据字典的键值或配置档案的编码）")
    private String isMonitor;

    /**
     * 任务用时（天）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "任务用时（天）")
    private Long templateTime;

    /**
     * 完成标准
     */
    @ApiModelProperty(value = "完成标准")
    private String completeStandard;

    /**
     * 进度说明
     */
    @ApiModelProperty(value = "进度说明")
    private String progressDescription;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
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
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "计划完成日期设置T- ")
    private Long planEndDateConfig;

    /**
     * 主表的处理状态
     */
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 开发计划类型
     */
    @ApiModelProperty(value = "开发计划类型")
    private String developType;
}
