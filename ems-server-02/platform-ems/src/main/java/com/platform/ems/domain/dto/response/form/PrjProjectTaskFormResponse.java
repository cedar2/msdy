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

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目任务明细报表返回体
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PrjProjectTaskFormResponse {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "指示灯：灰色-1红色0绿色1橙黄2蓝色3")
    private String light;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案-任务明细")
    private Long projectTaskSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划sid")
    private Long developPlanSid;

    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;

    @Excel(name = "商品款号/SPU号")
    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @Excel(name = "商品SKU编码(ERP)")
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品号Sid")
    private Long sampleSid;

    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegion;

    @Excel(name = "市场区域")
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegionName;

    @Excel(name = "任务状态", dictType = "s_project_task_status")
    @ApiModelProperty(value = "任务状态")
    private String taskStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long taskSid;

    @ApiModelProperty(value = "任务节点编码")
    private String taskCode;

    @Excel(name = "任务节点")
    @ApiModelProperty(value = "任务节点名称")
    private String taskName;

    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    @Excel(name = "试销类型", dictType = "s_trialsale_type")
    @ApiModelProperty(value = "试销类型（数据字典的键值或配置档案的编码）")
    private String trialsaleType;

    @Excel(name = "所属阶段", dictType = "s_project_phase")
    @ApiModelProperty(value = "所属阶段（数据字典的键值或配置档案的编码）")
    private String projectPhase;

    @Excel(name = "销售站点/网店")
    @ApiModelProperty(value = "销售站点name")
    private String saleStationName;

    @Excel(name = "所属年月(项目)")
    @ApiModelProperty(value = "所属年月(项目)")
    private String yearmonthProject;

    @ApiModelProperty(value = "处理人(任务)（用户账号）")
    private String handlerTask;

    @ApiModelProperty(value = "处理人(任务)（用户账号）")
    private String[] handlerTaskList;

    @Excel(name = "处理人(任务)")
    @ApiModelProperty(value = "处理人(任务)")
    private String handlerTaskName;

    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @ApiModelProperty(value = "图片路径数组")
    private String[] picturePathList;

    @Excel(name = "前置任务节点")
    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号;隔开")
    private String preTaskName;

    @Excel(name = "优先级(任务)", dictType = "s_urgency_type")
    @ApiModelProperty(value = "优先级(任务)")
    private String priorityTask;

    @ApiModelProperty(value = "处理序号(任务)")
    private String handleSortTask;

    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期(任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期(任务)")
    private Date planStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期(任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期(任务)")
    private Date planEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期(任务)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期(任务)")
    private Date actualEndDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-发起岗位")
    private Long startPositionSid;

    @ApiModelProperty(value = "发起岗位")
    private String startPositionCode;

    @ApiModelProperty(value = "发起岗位")
    private String[] startPositionCodeList;

    @Excel(name = "发起岗位")
    @ApiModelProperty(value = "发起岗位")
    private String startPositionName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-负责岗位")
    private Long chargePositionSid;

    @Excel(name = "负责岗位")
    @ApiModelProperty(value = "负责岗位")
    private String chargePositionName;

    @ApiModelProperty(value = "负责岗位")
    private String chargePositionCode;

    @ApiModelProperty(value = "负责岗位")
    private String[] chargePositionCodeList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-告知岗位")
    private Long noticePositionSid;

    @Excel(name = "告知岗位")
    @ApiModelProperty(value = "告知岗位")
    private String noticePositionName;

    @ApiModelProperty(value = "告知岗位")
    private String noticePositionCode;

    @ApiModelProperty(value = "告知岗位")
    private String[] noticePositionCodeList;

    @Excel(name = "任务执行提醒天数")
    @ApiModelProperty(value = "项目任务执行提醒天数")
    private Integer toexecuteNoticeDaysPrjTask;

    @Excel(name = "到期提醒天数(任务)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "任务到期提醒天数")
    private Long toexpireDaysTask;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联业务单据sid")
    private Long relateBusinessFormSid;

    @ApiModelProperty(value = "前置任务节点，如包含多个，则用英文分号;隔开")
    private String preTask;

    @ApiModelProperty(value = "前置任务节点 (数组)")
    private String[] preTaskList;

    @ApiModelProperty(value = "前置序号，如包含多个，则用英文分号;隔开")
    private String preSort;

    @ApiModelProperty(value = "是否监控（数据字典的键值或配置档案的编码）")
    private String isMonitor;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "任务用时（天）")
    private Long templateTime;

    @ApiModelProperty(value = "日历类型")
    private String calendarType;

    @ApiModelProperty(value = "完成标准")
    private String completeStandard;

    @Excel(name = "进度说明(任务)")
    @ApiModelProperty(value = "进度说明")
    private String progressDescription;

    @Excel(name = "关联业务单据", dictType = "s_relate_business_form")
    @ApiModelProperty(value = "关联业务单据单号")
    private String relateBusinessFormCode;

    @Excel(name = "所属任务阶段", dictType = "s_task_phase")
    @ApiModelProperty(value = "所属任务阶段")
    private String taskPhase;

    @ApiModelProperty(value = "所属业务板块")
    private String businessSection;

    @Excel(name = "年度", dictType = "s_year")
    @ApiModelProperty(value = "年度")
    private String year;

    @Excel(name = "项目状态", dictType = "s_project_status")
    @ApiModelProperty(value = "项目状态")
    private String projectStatus;

    @Excel(name = "项目负责人")
    @ApiModelProperty(value = "项目负责人名称(员工档案)")
    private String projectLeaderName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期(项目)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date projectPlanStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期(项目)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date projectPlanEndDate;

    @Excel(name = "样品号")
    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    @Excel(name = "计划类型", dictType = "s_plan_type")
    @ApiModelProperty(value = "计划类型")
    private String planType;

    @Excel(name = "项目名称")
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品类规划sid")
    private Long categoryPlanSid;

    @Excel(name = "品类规划编号")
    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @ApiModelProperty(value = "即将到期预警天数比例，如是2%，则存储的值为：0.02")
    private BigDecimal overdueWarnRate;

    @ApiModelProperty(value = "开发类型")
    private String developType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类")
    private Long smallClassSid;

    @ApiModelProperty(value = "小类")
    private String smallClassCode;

    @ApiModelProperty(value = "小类")
    private String smallClassName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类")
    private Long middleClassSid;

    @ApiModelProperty(value = "中类")
    private String middleClassCode;

    @ApiModelProperty(value = "中类")
    private String middleClassName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类")
    private Long bigClassSid;

    @ApiModelProperty(value = "大类")
    private String bigClassCode;

    @ApiModelProperty(value = "大类")
    private String bigClassName;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

}
