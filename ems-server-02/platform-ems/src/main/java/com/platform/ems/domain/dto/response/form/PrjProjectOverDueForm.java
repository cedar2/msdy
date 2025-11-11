package com.platform.ems.domain.dto.response.form;

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

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 项目档案对象 s_prj_project
 *
 * @author chenkw
 * @date 2022-12-08
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PrjProjectOverDueForm extends EmsBaseEntity {

    /**
     * 计划完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期")
    private Date planEndDate;

    /**
     * 项目编号
     */
    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    /**
     * 项目编号
     */
    @Excel(name = "项目名称")
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String projectType;

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
     * 项目状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "项目状态", dictType = "s_project_status")
    @ApiModelProperty(value = "项目状态（数据字典的键值或配置档案的编码）")
    private String projectStatus;

    /**
     * 项目负责人
     */
    @Excel(name = "项目负责人")
    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    /**
     * 年度（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "年度", dictType = "s_year")
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String year;

    @Excel(name = "市场区域")
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegionName;

    /**
     * 计划开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划开始日期")
    private Date planStartDate;

    /**
     * 品类规划编码
     */
    @Excel(name = "品类规划号")
    @ApiModelProperty(value = "品类规划编码")
    private String categoryPlanCode;

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

    @Excel(name = "优先级(项目)", dictType = "s_urgency_type")
    @ApiModelProperty(value = "优先级(项目)")
    private String priorityProject;

    /**
     * 样品号
     */
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
     * 系统SID-项目档案
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目档案")
    private Long projectSid;

    @ApiModelProperty(value = "sid数组")
    private Long[] projectSidList;


    /**
     * 项目类型（多选）
     */
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String[] projectTypeList;

    /**
     * 开发计划sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划sid")
    private Long developPlanSid;

    /**
     * 开发计划类型
     */
    @ApiModelProperty(value = "开发计划类型")
    private String developType;

    /**
     * 大类sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类sid")
    private Long bigClassSid;

    /**
     * 大类code
     */
    @ApiModelProperty(value = "大类code")
    private String bigClassCode;

    /**
     * 大类sid
     */
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    /**
     * 中类sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类sid")
    private Long middleClassSid;

    /**
     * 中类code
     */
    @ApiModelProperty(value = "中类code")
    private String middleClassCode;

    /**
     * 中类sid
     */
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    /**
     * 小类sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类sid")
    private Long smallClassSid;

    /**
     * 小类code
     */
    @ApiModelProperty(value = "小类code")
    private String smallClassCode;

    /**
     * 小类sid
     */
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    /**
     * 样品号sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品号Sid")
    private Long sampleSid;

    /**
     * 物料/商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品号对应我司样衣号的商品sid")
    private Long materialSid;

    /**
     * 物料/商品款号
     */
    @ApiModelProperty(value = "样品号对应我司样衣号的商品编码/款号")
    private String materialCode;

    /**
     * 物料/商品名称
     */
    @ApiModelProperty(value = "样品号对应我司样衣号的商品名称")
    private String materialName;

    /**
     * 项目状态（多选）
     */
    @ApiModelProperty(value = "项目状态（数据字典的键值或配置档案的编码）")
    private String[] projectStatusList;

    /**
     * 年度（多选）
     */
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String[] yearList;

    /**
     * 销售站点sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售站点sid")
    private Long saleStationSid;

    @ApiModelProperty(value = "销售站点sid 多选")
    private Long[] saleStationSidList;

    /**
     * 销售站点code
     */
    @ApiModelProperty(value = "销售站点code")
    private Long saleStationCode;

    /**
     * 销售站点name
     */
    @ApiModelProperty(value = "销售站点name")
    private String saleStationName;

    /**
     * 市场区域编码，如存在多值，则用英文分号“;”隔开
     */
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegion;

    /**
     * 市场区域编码，多选
     */
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String[] marketRegionList;

    /**
     * 计划类型（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "计划类型（数据字典的键值或配置档案的编码）")
    private String planType;

    /**
     * 项目负责人sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目负责人sid")
    private Long projectLeaderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目负责人对应员工档案对应的用户ID")
    private Long projectLeaderId;

    /**
     * 项目负责人sid(多选)
     */
    @ApiModelProperty(value = "项目负责人sid")
    private Long[] projectLeaderSidList;

    /**
     * 项目负责人编码
     */
    @ApiModelProperty(value = "项目负责人编码")
    private String projectLeaderCode;

    /**
     * 计划开始日期(起)
     */
    @ApiModelProperty(value = "计划开始日期起")
    private String planStartDateBegin;

    /**
     * 计划开始日期(止)
     */
    @ApiModelProperty(value = "计划开始日期止")
    private String planStartDateEnd;

    /**
     * 计划完成日期(起)
     */
    @ApiModelProperty(value = "计划完成日期起")
    private String planEndDateBegin;

    /**
     * 计划完成日期(止)
     */
    @ApiModelProperty(value = "计划完成日期止")
    private String planEndDateEnd;

    /**
     * 实际完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    /**
     * 实际完成日期(起)
     */
    @ApiModelProperty(value = "实际完成日期起")
    private String actualEndDateBegin;

    /**
     * 实际完成日期(止)
     */
    @ApiModelProperty(value = "实际完成日期止")
    private String actualEndDateEnd;

    /**
     * 产品季sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 产品季sid 多选
     */
    @ApiModelProperty(value = "产品季sid 多选")
    private Long[] productSeasonSidList;

    /**
     * 产品季编码
     */
    @ApiModelProperty(value = "产品季编码")
    private String productSeasonCode;

    /**
     * 品类规划sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "品类规划sid")
    private Long categoryPlanSid;

    /**
     * 项目说明
     */
    @ApiModelProperty(value = "项目说明")
    private String projectDescription;

    /**
     * 即将到期提醒天数
     */
    @ApiModelProperty(value = "即将到期提醒天数")
    private Integer toexpireDaysProj;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态（多选）
     */
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "创建人用户ID")
    private Long creatorAccountId;

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
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    @ApiModelProperty(value = "确认人昵称")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 系统SID-项目任务模板
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板")
    private Long taskTemplateSid;

    /**
     * 关联业务单据单号
     */
    @ApiModelProperty(value = "关联业务单据单号")
    private String relateBusinessFormCode;

    /**
     * 总任务数
     */
    @ApiModelProperty(value = "总任务数")
    private int itemCount;

    /**
     * 已完成任务数
     */
    @ApiModelProperty(value = "已完成任务数")
    private int itemCountYwc;

    /**
     * 进行中任务数
     */
    @ApiModelProperty(value = "进行中任务数")
    private int itemCountJxz;

    /**
     * 预警任务数
     */
    @ApiModelProperty(value = "预警中任务数")
    private int itemCountYj;

    /**
     * 逾期任务数
     */
    @ApiModelProperty(value = "逾期中任务数")
    private int itemCountYq;

    /**
     * 跳转用的 projectSid被单据引用的单据sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "跳转单据返回的单据sid")
    private Long referSid;

    /**
     * 跳转单据返回的提示信息
     */
    @ApiModelProperty(value = "跳转单据返回的提示信息")
    private String referMsg;
}
