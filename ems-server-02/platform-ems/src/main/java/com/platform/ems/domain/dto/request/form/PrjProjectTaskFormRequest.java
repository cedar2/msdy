package com.platform.ems.domain.dto.request.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 项目任务明细报表查询请求
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrjProjectTaskFormRequest {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    @ApiModelProperty(value = "项目档案sid数组")
    private Long[] projectSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案-任务明细")
    private Long projectTaskSid;

    @ApiModelProperty(value = "项目档案任务明细sid数组")
    private Long[] projectTaskSidList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务节点")
    private Long taskSid;

    @ApiModelProperty(value = "系统SID-任务节点多选数组")
    private Long[] taskSidList;

    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目类型（单选）")
    private String projectType;

    @ApiModelProperty(value = "项目类型（多选）")
    private String[] projectTypeList;

    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    @ApiModelProperty(value = "年度（单选）")
    private String year;

    @ApiModelProperty(value = "年度（多选）")
    private String[] yearList;

    @ApiModelProperty(value = "计划类型（单选）")
    private String planType;

    @ApiModelProperty(value = "计划类型（多选）")
    private String[] planTypeList;

    @ApiModelProperty(value = "项目状态（单选）")
    private String projectStatus;

    @ApiModelProperty(value = "项目状态（多选）")
    private String[] projectStatusList;

    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目负责人sid（单选）")
    private Long projectLeaderSid;

    @ApiModelProperty(value = "项目负责人sid（多选）")
    private Long[] projectLeaderSidList;

    @ApiModelProperty(value = "负责岗位")
    private String chargePositionCode;

    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    @ApiModelProperty(value = "市场区域编码（单选）")
    private String marketRegion;

    @ApiModelProperty(value = "市场区域编码（多选）")
    private String[] marketRegionList;

    @ApiModelProperty(value = "计划开始日期起(任务)")
    private String planStartDateBegin;

    @ApiModelProperty(value = "计划开始日期止(任务)")
    private String planStartDateEnd;

    @ApiModelProperty(value = "计划完成日期起(任务)")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完成日期止(任务)")
    private String planEndDateEnd;

    @ApiModelProperty(value = "项目任务状态（单选）")
    private String taskStatus;

    @ApiModelProperty(value = "项目任务状态（多选）")
    private String[] taskStatusList;

    @ApiModelProperty(value = "组别")
    private String groupType;

    @ApiModelProperty(value = "组别（数据字典的键值或配置档案的编码）")
    private String[] groupTypeList;

    @ApiModelProperty(value = "项目所属阶段（数据字典的键值或配置档案的编码）")
    private String projectPhase;

    @ApiModelProperty(value = "项目所属阶段（数据字典的键值或配置档案的编码）")
    private String[] projectPhaseList;

    @ApiModelProperty(value = "试销类型（数据字典的键值或配置档案的编码）")
    private String trialsaleType;

    @ApiModelProperty(value = "试销类型（数据字典的键值或配置档案的编码）")
    private String[] trialsaleTypeList;

    @ApiModelProperty(value = "销售站点code")
    private String saleStationCode;

    @ApiModelProperty(value = "销售站点code 数组")
    private String[] saleStationCodeList;

    @ApiModelProperty(value = "销售站点name")
    private String saleStationName;


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

    @ApiModelProperty(value = "任务处理人是否为空")
    private String handlerTaskIsNull;

    @ApiModelProperty(value = "处理状态（单选）")
    private String handleStatus;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "创建人账号（下拉框）")
    private String creatorAccount;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前用户对应的员工sid")
    private Long currentUserIsLeaderSid;

    @ApiModelProperty(value = "创建人昵称（输入框）")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

}
