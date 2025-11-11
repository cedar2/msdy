package com.platform.ems.domain;

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
@TableName(value = "s_prj_project")
public class PrjProject extends EmsBaseEntity {

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
    @TableField(exist = false)
    private Long[] projectSidList;

    /**
     * 项目编号
     */
    @Excel(name = "项目编号")
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 项目类型（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "项目类型不能为空")
    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String projectType;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String projectTypeName;

    /**
     * 项目类型（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String[] projectTypeList;

    /**
     * 项目所属阶段（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "项目所属阶段", dictType = "s_project_phase")
    @ApiModelProperty(value = "项目所属阶段（数据字典的键值或配置档案的编码）")
    private String projectPhase;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 是否已创建后续项目
     */
    @TableField(exist = false)
    @Excel(name = "是否已创建后续项目", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否已创建后续项目")
    private String isHaveShixiao;

    @TableField(exist = false)
    @Excel(name = "市场区域")
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegionName;

    /**
     * 项目负责人
     */
    @TableField(exist = false)
    @Excel(name = "项目负责人")
    @ApiModelProperty(value = "项目负责人")
    private String projectLeaderName;

    /**
     * 开发计划sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "开发计划sid")
    private Long developPlanSid;

    /**
     * 开发计划号
     */
    @Excel(name = "开发计划号")
    @ApiModelProperty(value = "开发计划号")
    private String developPlanCode;

    /**
     * 开发计划名称
     */
    @TableField(exist = false)
    @Excel(name = "开发计划名称")
    @ApiModelProperty(value = "开发计划名称")
    private String developPlanName;

    /**
     * 商品款号code
     */
    @Excel(name = "商品款号/SPU号")
    @ApiModelProperty(value = "商品款号code")
    private String productCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品款号code是否为空")
    private String productCodeIsNull;

    /**
     * 商品SKU条码code
     */
    @ApiModelProperty(value = "商品SKU条码code")
    private String materialBarcodeCode;

    @Excel(name = "商品SKU编码(ERP)")
    @ApiModelProperty(value = "ERP系统SKU条码编码")
    private String erpMaterialSkuBarcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "ERP系统SKU条码编码是否为空")
    private String erpMaterialSkuBarcodeIsNull;

    @Excel(name = "商品MSKU编码(ERP)")
    @ApiModelProperty(value = "商品MSKU编码(ERP)")
    private String erpMaterialMskuCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品MSKU编码(ERP)是否为空")
    private String erpMaterialMskuCodeIsNull;

    @Excel(name = "所属年月(项目)")
    @ApiModelProperty(value = "所属年月(项目)")
    private String yearmonthProject;

    /**
     * 计划开始日期
     */
    @TableField(exist = false)
    @Excel(name = "所属年月(开发计划)")
    @ApiModelProperty(value = "所属年月(开发计划)")
    private String yearmonthDevelop;

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
     * 计划开始日期(起)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划开始日期起")
    private String planStartDateBegin;

    /**
     * 计划开始日期(止)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计划开始日期止")
    private String planStartDateEnd;

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

    /**
     * 实际完成日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "实际完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    private Date actualEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目所属阶段（数据字典的键值或配置档案的编码）")
    private String[] projectPhaseList;

    /**
     * 实际完成日期(起)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "实际完成日期起")
    private String actualEndDateBegin;

    /**
     * 实际完成日期(止)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "实际完成日期止")
    private String actualEndDateEnd;

    /**
     * 项目状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "项目状态", dictType = "s_project_status")
    @ApiModelProperty(value = "项目状态（数据字典的键值或配置档案的编码）")
    private String projectStatus;

    /**
     * 优先级(项目)（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "优先级(项目)", dictType = "s_urgency_type")
    @ApiModelProperty(value = "优先级(项目)（数据字典的键值或配置档案的编码）")
    private String priorityProject;

    /**
     * 开发计划类型
     */
    @TableField(exist = false)
    @Excel(name = "开发类型", dictType = "s_develop_type")
    @ApiModelProperty(value = "开发计划类型")
    private String developType;

    /**
     * 试销类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "试销类型", dictType = "s_trialsale_type")
    @ApiModelProperty(value = "试销类型（数据字典的键值或配置档案的编码）")
    private String trialsaleType;

    @TableField(exist = false)
    @ApiModelProperty(value = "试销类型（数据字典的键值或配置档案的编码）")
    private String trialsaleTypeName;

    /**
     * 年度（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "年度", dictType = "s_year")
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String year;

    /**
     * 销售站点name
     */
    @TableField(exist = false)
    @Excel(name = "销售站点/网店")
    @ApiModelProperty(value = "销售站点name")
    private String saleStationName;

    @Excel(name = "前置项目编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "前置项目档案编码")
    private Long preProjectCode;

    /**
     * 样品号
     */
    @ApiModelProperty(value = "样品号")
    private String sampleCode;

    /**
     * 样品号sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "样品号Sid")
    private Long sampleSid;

    /**
     * 商品款号code
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品款号code")
    private Long productSid;

    /**
     * 商品SKU条码sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品SKU条码sid")
    private Long materialBarcodeSid;

    /**
     * 物料/商品名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "样品号对应我司样衣号的商品名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目状态（查询条件=不是该状态）")
    private String projectStatusNot;

    /**
     * 项目状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目状态（数据字典的键值或配置档案的编码）")
    private String[] projectStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "优先级(项目)（查询条件=不是该状态）")
    private String priorityProjectNot;

    /**
     * 优先级(项目)（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "优先级(项目)（数据字典的键值或配置档案的编码）")
    private String[] priorityProjectList;

    /**
     * 处理序号(项目)
     */
    @ApiModelProperty(value = "处理序号(项目)")
    private String handleSortProject;

    /**
     * 开发计划类型 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "开发计划类型 多选")
    private String[] developTypeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "试销类型（数据字典的键值或配置档案的编码）")
    private String[] trialsaleTypeList;

    /**
     * 年度（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "年度（数据字典的键值或配置档案的编码）")
    private String[] yearList;

    /**
     * 市场区域编码，如存在多值，则用英文分号“;”隔开
     */
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String marketRegion;

    /**
     * 市场区域编码，多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "市场区域编码，如存在多值，则用英文分号“;”隔开")
    private String[] marketRegionList;

    /**
     * 销售站点sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售站点sid")
    private Long saleStationSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售站点sid 多选")
    private Long[] saleStationSidList;

    /**
     * 销售站点code
     */
    @ApiModelProperty(value = "销售站点code")
    private String saleStationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "销售站点code 数组")
    private String[] saleStationCodeList;

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

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目负责人对应员工档案对应的用户ID")
    private Long projectLeaderId;

    /**
     * 项目负责人sid(多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目负责人sid")
    private Long[] projectLeaderSidList;

    /**
     * 项目负责人编码
     */
    @ApiModelProperty(value = "项目负责人编码")
    private String projectLeaderCode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前用户对应的员工sid")
    private Long currentUserIsLeaderSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)起")
    private String yearmonthProjectBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属年月(项目)止")
    private String yearmonthProjectEnd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-前置项目档案")
    private Long preProjectSid;

    /**
     * 产品季sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "产品季sid")
    private Long productSeasonSid;

    /**
     * 产品季sid 多选
     */
    @TableField(exist = false)
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
     * 品类规划编码
     */
    @Excel(name = "品类规划编号")
    @ApiModelProperty(value = "品类规划编码")
    private String categoryPlanCode;


    /**
     * 大类sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "大类sid")
    private Long bigClassSid;

    /**
     * 大类code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "大类code")
    private String bigClassCode;

    /**
     * 大类sid
     */
    @TableField(exist = false)
    @Excel(name = "大类")
    @ApiModelProperty(value = "大类")
    private String bigClassName;

    /**
     * 中类sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "中类sid")
    private Long middleClassSid;

    /**
     * 中类code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "中类code")
    private String middleClassCode;

    /**
     * 中类sid
     */
    @TableField(exist = false)
    @Excel(name = "中类")
    @ApiModelProperty(value = "中类")
    private String middleClassName;

    /**
     * 小类sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "小类sid")
    private Long smallClassSid;

    /**
     * 小类code
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "小类code")
    private String smallClassCode;

    /**
     * 小类sid
     */
    @TableField(exist = false)
    @Excel(name = "小类")
    @ApiModelProperty(value = "小类")
    private String smallClassName;

    /**
     * 组别
     */
    @TableField(exist = false)
    @Excel(name = "组别", dictType = "s_product_group")
    @ApiModelProperty(value = "组别")
    private String groupType;

    /**
     * 组别 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "组别 多选")
    private String[] groupTypeList;

    /**
     * 项目说明
     */
    @ApiModelProperty(value = "项目说明")
    private String projectDescription;

    /**
     * 项目名称
     */
    @Excel(name = "项目名称")
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * 即将到期提醒天数
     */
    @Excel(name = "到期提醒天数")
    @ApiModelProperty(value = "即将到期提醒天数")
    private Integer toexpireDaysProj;

    /**
     * 一次采购标识
     */
    @Excel(name = "一次采购", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "一次采购标识")
    private String firstPurchaseFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改一次采购标识")
    private String firstPurchaseFlagIsUpd;

    /**
     * 一次采购更新日期（后台）
     */
    @Excel(name = "一次采购更新日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "一次采购更新日期（后台）")
    private Date firstPurchaseFlagUpdateDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "一次采购更新日期（后台）起")
    private String fstPurFlagUpdDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "一次采购更新日期（后台）止")
    private String fstPurFlagUpdDateEnd;

    /**
     * 二次采购标识
     */
    @Excel(name = "二次采购", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "二次采购标识")
    private String secondPurchaseFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改二次采购标识")
    private String secondPurchaseFlagIsUpd;

    /**
     * 二次采购标识更新日期（后台）
     */
    @Excel(name = "二次采购标识更新日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "二次采购标识更新日期（后台）")
    private Date secondPurchaseFlagUpdateDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "二次采购标识更新日期（后台）起")
    private String sndPurFlagUpdDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "二次采购标识更新日期（后台）止")
    private String sndPurFlagUpdDateEnd;

    /**
     * 一次采购到货通知标识
     */
    @Excel(name = "一次采购到货通知", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "一次采购到货通知标识")
    private String arrivalNoticeFlagFirstPurchase;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否修改一次采购到货通知标识")
    private String arrivalNoticeFlagFirstPurchaseIsUpd;

    /**
     * 一次采购到货通知更新日期（后台）
     */
    @Excel(name = "一次采购到货通知更新日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "一次采购到货通知更新日期（后台）")
    private Date arrivalNoticeFlagFirstPurchaseUpdateDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "一次采购到货通知更新日期（后台）起")
    private String arrNtcFlagFstPurUpdDateBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "一次采购到货通知更新日期（后台）止")
    private String arrNtcFlagFstPurUpdDateEnd;

    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "创建人用户ID")
    private Long creatorAccountId;

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

    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
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
    @TableField(exist = false)
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
     * 项目档案任务列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目档案任务列表")
    private List<PrjProjectTask> taskList;

    /**
     * 附件对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件对象")
    private List<PrjProjectAttach> attachmentList;

    /**
     * 前置项目档案列表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "前置项目档案列表")
    private List<PrjProject> preProjectList;

    /**
     * 系统SID-项目任务模板
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-项目任务模板")
    private Long taskTemplateSid;

    /**
     * 关联业务单据单号
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联业务单据单号")
    private String relateBusinessFormCode;

    /**
     * 总任务数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总任务数")
    private int itemCount;

    /**
     * 已完成任务数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "已完成任务数")
    private int itemCountYwc;

    /**
     * 进行中任务数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "进行中任务数")
    private int itemCountJxz;

    /**
     * 预警任务数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预警中任务数")
    private int itemCountYj;

    /**
     * 逾期任务数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "逾期中任务数")
    private int itemCountYq;

    /**
     * 跳转用的 projectSid被单据引用的单据sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "跳转单据返回的单据sid")
    private Long referSid;

    /**
     * 跳转单据返回的提示信息
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "跳转单据返回的提示信息")
    private String referMsg;

    /**
     * 导入标识： DR
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "导入标识： DR")
    private String importType;

    /**
     * 试销结果单的是否翻单
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否翻单（数据字典的键值或配置档案的编码）")
    private String isRepeatOrder;

    /**
     * 经营模式（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "经营模式")
    private String operateMode;
}
