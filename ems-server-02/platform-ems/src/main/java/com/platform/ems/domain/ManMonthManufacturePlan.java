package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 生产月计划对象 s_man_month_manufacture_plan
 *
 * @author linhongwei
 * @date 2021-07-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_month_manufacture_plan")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManMonthManufacturePlan extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @ApiModelProperty(value = "操作部门编码")
    private String department;

    /**
     * 系统SID-生产月计划单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产月计划单")
    private Long monthManufacturePlanSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] monthManufacturePlanSidList;
    /**
     * 生产月计划单号
     */
    @Excel(name = "生产月计划编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产月计划单号")
    private Long monthManufacturePlanCode;

    @ApiModelProperty(value = "计划年月")
    @Excel(name = "计划年月", width = 30)
    private String yearmonth;

    @ApiModelProperty(value ="计划年月开始时间")
    @TableField(exist = false)
    private String yearMonthBeginTime;

    @ApiModelProperty(value ="计划年月结束时间")
    @TableField(exist = false)
    private String yearMonthEndTime;

    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心名称")
    private String workCenterName;
    /**
     * 工厂sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid数组")
    private Long[] plantSidList;


    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组sid数组")
    private Long[] workCenterSidList;

    @TableField(exist = false)
    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;



    /**
     * 业务类型编码code
     */
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    /**
     * 系统SID-公司档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 数据来源类别（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "数据来源类别（数据字典的键值或配置档案的编码）")
    private String referDocCategory;

    /**
     * 当前审批节点名称
     */
    @ApiModelProperty(value = "当前审批节点名称")
    @TableField(exist = false)
    private String approvalNode;

    /**
     * 当前审批人
     */
    @ApiModelProperty(value = "当前审批人")
    @TableField(exist = false)
    private String approvalUserName;

    /**
     * 提交人
     */
    @ApiModelProperty(value = "提交人")
    @TableField(exist = false)
    private String submitUserName;

    /**
     * 提交日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    @TableField(exist = false)
    private Date submitDate;

    /**
     * 当前审批人id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人id")
    private String approvalUserId;


    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人名称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @TableField(exist = false)
    private String[] handleStatusList;
    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    @TableField(exist = false)
    private String[] creatorAccountList;
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

    @TableField(exist = false)
    @ApiModelProperty(value = "最大行号")
    private Long maxItemNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "部门查询")
    private String[] departmentList;

    /**
     * 生产月计划sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产月计划sids")
    private List<Long> monthManufacturePlanSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产月计划-明细对象")
    private List<ManMonthManufacturePlanItem> manMonthManufacturePlanItemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产月计划-附件对象")
    private List<ManMonthManufacturePlanAttach> manMonthManufacturePlanAttachList;


    @TableField(exist = false)
    @ApiModelProperty(value = "生产月计划-班组总结")
    List<ManMonthManufacturePlanBanzuRemark> planBanzuRemarkList;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产月计划-关注事项表")
    List<ManManufactureOrderConcernTask>   manufactureOrderConcernTaskList;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人名称")
    private String confirmerAccountName;

    /** 工厂编码 */

    @ApiModelProperty(value = "工厂编码")
    private String plantCode;


    /** 工艺路线sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工艺路线sid")
    @NotNull(message = "工艺路线不能为空")
    private Long processRouteSid;

    /** 工艺路线code */
    @ApiModelProperty(value = "工艺路线code")
    private String processRouteCode;

    /** 关注事项组sid */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关注事项组sid")
    private Long concernTaskGroupSid;

    /** 关注事项组code */
    @ApiModelProperty(value = "关注事项组code")
    private String concernTaskGroupCode;

    /** 月总结 */
    @ApiModelProperty(value = "月总结")
    private String monthRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "明细清单页签中相同班组+款号+排产批次号+颜色允许存在多行")
    private String continueIsUnique;
}
