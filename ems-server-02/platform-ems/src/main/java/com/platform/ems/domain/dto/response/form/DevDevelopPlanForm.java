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
 * 开发计划报表返回
 *
 * @author chenkw
 * @date 2023-01-31
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevDevelopPlanForm {

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    @Excel(name = "所属年月(计划)")
    @ApiModelProperty(value = "所属年月")
    private String yearmonth;

    @ApiModelProperty(value = "所属年月(计划)起")
    private String yearmonthDevelopBegin;

    @ApiModelProperty(value = "所属年月(计划)止")
    private String yearmonthDevelopEnd;

    @Excel(name = "大类")
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类 sid")
    private Long bigClassSid;

    @ApiModelProperty(value = "大类 sid 多选")
    private Long[] bigClassSidList;

    @Excel(name = "中类")
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类 sid")
    private Long middleClassSid;

    @ApiModelProperty(value = "中类 sid 多选")
    private Long[] middleClassSidList;

    @Excel(name = "小类")
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类 sid")
    private Long smallClassSid;

    @ApiModelProperty(value = "小类 sid 多选")
    private Long[] smallClassSidList;

    @Excel(name = "项目名称")
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @Excel(name = "项目状态", dictType = "s_project_status")
    @ApiModelProperty(value = "项目状态（数据字典的键值或配置档案的编码）")
    private String projectStatus;

    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String projectType;

    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegion;

    @Excel(name = "市场区域")
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegionName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目负责人sid")
    private Long projectLeaderSid;

    @ApiModelProperty(value = "项目负责人编码")
    private String projectLeaderCode;

    @Excel(name = "项目负责人")
    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期(项目)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期(项目)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期(项目)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

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

    @Excel(name = "样品号")
    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    /**
     * 商品款号（变体前）sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品款号（变体前）sid")
    private Long productSidInitial;

    /**
     * 商品款号（变体前）code
     */
    @ApiModelProperty(value = "商品款号（变体前）code")
    private String productCodeInitial;

    @Excel(name = "开发类型", dictType = "s_develop_type")
    @ApiModelProperty(value = "开发类型（数据字典的键值或配置档案的编码）")
    private String developType;

    @Excel(name = "开发级别", dictType = "s_develop_level")
    @ApiModelProperty(value = "开发级别（数据字典的键值或配置档案的编码）")
    private String developLevel;

    /**
     * 根据对应项目，显示数据库表“项目任务明细表”中“任务状态”为“进行中”的任务名称
     * 若查到多个任务，不同任务名称间用；隔开
     */
    @Excel(name = "当前进展任务")
    @ApiModelProperty(value = "当前进展任务")
    private String projectTaskName;

    /**
     * 根据对应项目，显示数据库表“样品评审单表”的处理状态的名称（评审阶段review_phase为终审）
     */
    @Excel(name = "终审状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "终审状态")
    private String handleStatusReviewZs;

    /**
     * 若终审状态为“已确认”，根据对应项目，显示数据库表“样品评审单表”的确认时间（年月日）数据
     * （评审阶段review_phase为终审）
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "终审评审日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "终审评审日期")
    private Date confirmDateReviewZs;

    /**
     * 若终审状态为“已确认”，根据对应项目，显示数据库表“样品终审单表”的评审结果名称
     */
    @Excel(name = "终审评审结果", dictType = "s_review_result")
    @ApiModelProperty(value = "终审评审结果")
    private String reviewResult;

    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private Long projectCode;

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

    @Excel(name = "计划类型", dictType = "s_plan_type")
    @ApiModelProperty(value = "计划类型（数据字典的键值或配置档案的编码）")
    private String planType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品类规划sid")
    private Long categoryPlanSid;

    @Excel(name = "品类规划编号")
    @ApiModelProperty(value = "品类规划编号")
    private String categoryPlanCode;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @Excel(name = "年度")
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String year;

    @Excel(name = "产品季")
    @ApiModelProperty(value = "产品季")
    private String productSeasonName;

    @ApiModelProperty(value = "年度（多选）")
    private String[] yearList;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季Sid")
    private Long productSeasonSid;

    @ApiModelProperty(value = "产品季（多选）")
    private Long[] productSeasonSidList;

    @ApiModelProperty(value = "计划类型（多选）")
    private String[] planTypeList;

    @ApiModelProperty(value = "项目状态（多选）")
    private String[] projectStatusList;

    @ApiModelProperty(value = "开发类型（多选）")
    private String[] developTypeList;

    @ApiModelProperty(value = "市场区域编码（多选）")
    private String[] marketRegionList;

    @ApiModelProperty(value = "项目负责人sid（多选）")
    private Long[] projectLeaderSidList;

    @ApiModelProperty(value = "项目类型（多选）")
    private String[] projectTypeList;

    @ApiModelProperty(value = "计划开始日期起")
    private String planStartDateBegin;

    @ApiModelProperty(value = "计划开始日期止")
    private String planStartDateEnd;

    @ApiModelProperty(value = "计划完成日期起")
    private String planEndDateBegin;

    @ApiModelProperty(value = "计划完成日期止")
    private String planEndDateEnd;

    @ApiModelProperty(value = "实际完成日期起")
    private String actualEndDateBegin;

    @ApiModelProperty(value = "实际完成日期止")
    private String actualEndDateEnd;

    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    @ApiModelProperty(value = "开发级别（多选）")
    private String[] developLevelList;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前用户对应的员工sid")
    private Long currentUserIsLeaderSid;

    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划负责人SID")
    private Long leaderSid;

    @ApiModelProperty(value = "开发计划负责人SID")
    private Long[] leaderSidList;

    @ApiModelProperty(value = "开发计划负责人CODE")
    private String leaderCode;

    @ApiModelProperty(value = "开发计划负责人")
    private String leaderName;

}
