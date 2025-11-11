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
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 生产产品缺陷登记对象 s_man_product_defect
 *
 * @author zhuangyz
 * @date 2022-08-04
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_product_defect")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManProductDefect extends EmsBaseEntity {

    /**
     * 商品编码
     */
    @Excel(name = "商品编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @TableField(exist = false)
    @Excel(name = "商品名称")
    private String productName;

    /**
     * 排产批次号
     */
    @Excel(name = "排产批次号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    /**
     * 问题简述
     */
    @Excel(name = "问题简述")
    @ApiModelProperty(value = "问题简述")
    private String defectShortDescription;

    /**
     * 解决状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "解决状态")
    @ApiModelProperty(value = "解决状态（数据字典的键值或配置档案的编码）")
    private String resolveStatus;

    @Excel(name = "负责解决人")
    @TableField(exist = false)
    private String defectResolverName;

    /**
     * 计划解决日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划解决日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划解决日期")
    private Date planResolveDate;

    /**
     * 优先级（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "紧急度" , dictType = "s_urgency_type")
    @ApiModelProperty(value = "紧急度/优先级")
    private String defectPriority;

    @Excel(name = "提报人")
    @TableField(exist = false)
    private String defectReporterName;

    /**
     * 提报日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "提报日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提报日期")
    private Date defectReportDate;

    @Excel(name = "问题分类")
    @TableField(exist = false)
    private Long defectClassName;

    @Excel(name = "工厂")
    @TableField(exist = false)
    private String plantShortName;

    @Excel(name = "操作部门")
    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    @Excel(name = "班组")
    private String workCenterName;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改日期")
    private Date updateDate;


    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    private String confirmerAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    private Date confirmDate;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 生产产品缺陷编号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "生产产品缺陷登记编号")
    @ApiModelProperty(value = "生产产品缺陷编号")
    private Long productDefectCode;


//-----------------------------------------------------------------------------

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产产品缺陷
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产产品缺陷")
    private Long productDefectSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] productDefectSidList;


    /**
     * 工厂sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 操作部门编码
     */
    @ApiModelProperty(value = "操作部门编码")
    private String department;

    /**
     * 工作中心(班组)sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心(班组)sid")
    private Long workCenterSid;

    /**
     * 工作中心(班组)编码
     */
    @ApiModelProperty(value = "工作中心(班组)编码")
    private String workCenterCode;

    /**
     * 问题来源（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "问题来源（数据字典的键值或配置档案的编码）")
    private String defectSource;

    /**
     * 生产订单sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单sid")
    private Long manufactureOrderSid;



    /**
     * 商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long productSid;




    /**
     * 问题提报人sid（员工档案sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "问题提报人sid（员工档案sid）")
    private Long defectReporterSid;

    /**
     * 问题提报人编号（员工档案编号）
     */
    @ApiModelProperty(value = "问题提报人编号（员工档案编号）")
    private String defectReporterCode;






    /**
     * 负责解决人sid（员工档案sid）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "负责解决人sid（员工档案sid）")
    private Long defectResolverSid;

    /**
     * 负责解决人编号（员工档案编号）
     */
    @Excel(name = "负责解决人编号（员工档案编号）")
    @ApiModelProperty(value = "负责解决人编号（员工档案编号）")
    private String defectResolverCode;



    /**
     * 问题分类sid
     */
    @Excel(name = "问题分类sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "问题分类sid")
    private Long defectClassSid;

    /**
     * 问题分类编码
     */
    @Excel(name = "问题分类编码")
    @ApiModelProperty(value = "问题分类编码")
    private String defectClassCode;


    /**
     * 解决方案
     */
    @Excel(name = "解决方案")
    @ApiModelProperty(value = "解决方案")
    private String solutionDescription;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;


    /**
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    /**
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


    @TableField(exist = false)
    private List<ManProductDefectAttach> manProductDefectAttachList;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：工厂")
    @TableField(exist = false)
    private Long[] plantSidList;


    @ApiModelProperty(value = "查询：操作部门")
    @TableField(exist = false)
    private String[] departmentList;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询: 班组")
    @TableField(exist = false)
    private Long[] workCenterSidList;


    @ApiModelProperty(value = "查询：创建人")
    @TableField(exist = false)
    private String[] creatorAccountList;


    @ApiModelProperty(value = "查询：问题来源")
    @TableField(exist = false)
    private String[] defectSourceList;


    @ApiModelProperty(value = "查询：解决状态")
    @TableField(exist = false)
    private String[] resolveStatusList;


    @ApiModelProperty(value = "计划解决日期起")
    @TableField(exist = false)
    private String planResolveDateBegin;

    @ApiModelProperty(value = "计划解决日期至")
    @TableField(exist = false)
    private String planResolveDateEnd;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "查询：问题提报人")
    @TableField(exist = false)
    private Long[] defectReporterSidList;


    @ApiModelProperty(value = "查询：负责解决人")
    @TableField(exist = false)
    private Long[] defectResolverSidList;


    @ApiModelProperty(value = "查询：优先级")
    @TableField(exist = false)
    private String[] defectPriorityList;

    @ApiModelProperty(value = "查询：提报日期起")
    @TableField(exist = false)
    private String defectReportDateBegin;

    @ApiModelProperty(value = "查询：提报日期至")
    @TableField(exist = false)
    private String defectReportDateEnd;
}
