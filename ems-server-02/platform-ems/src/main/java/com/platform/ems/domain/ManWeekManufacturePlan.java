package com.platform.ems.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.util.Date;
import java.util.List;

/**
 * 生产周计划对象 s_man_week_manufacture_plan
 *
 * @author hjj
 * @date 2021-07-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_week_manufacture_plan")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManWeekManufacturePlan extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产周计划单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产周计划单")
    private Long weekManufacturePlanSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] weekManufacturePlanSidList;
    /**
     * 生产周计划单号
     */
    @Excel(name = "生产周计划编号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产周计划单号")
    private Long weekManufacturePlanCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产周计划单号")
    private String weekManufacturePlanCodeQuery;

    /**
     * 工厂sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @ApiModelProperty(value = "操作部门编码")
    private String department;

    @Excel(name = "操作部门")
    @ApiModelProperty(value = "操作部门")
    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：操作部门")
    private String[] departmentList;
    /**
     * 工作中心/班组sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心/班组sid")
    private Long workCenterSid;

    @Excel(name = "工作中心/班组")
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心/班组名称")
    private String workCenterName;

    /**
     * 周计划日期(起)
     */
//    @NotEmpty(message = "周计划日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "周计划日期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期(起)")
    private Date dateStart;

    /**
     * 周计划日期(至)
     */
//    @NotEmpty(message = "周计划日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "周计划日期(至)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期(至)")
    private Date dateEnd;

    /**
     * 单据类型编码code
     */
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

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

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    private String[] handleStatusList;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @TableField(exist = false)
    private String[] creatorAccountList;
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

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更改人")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

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
    @ApiModelProperty(value = "生产所属阶段名称")
    private String produceStageName;
    /**
     * 生产周计划sids
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产周计划sids")
    private List<Long> weekManufacturePlanSids;

    /**
     * 生产周计划-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产周计划-明细对象")
    private List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList;


    @TableField(exist = false)
    @ApiModelProperty(value = "生产周计划-近5周进度")
    private List<ManProduceWeekProgressTotal> ManProduceWeekProgressTotalList;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产周计划-附件对象")
    private List<ManWeekManufacturePlanAttach> manWeekManufacturePlanAttachList;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产周计划-生产周计划-班组总结")
    List<ManWeekManufacturePlanBanzuRemark> planBanzuRemarkList;


    @TableField(exist = false)
    @ApiModelProperty(value = "生产周计划-生产周计划-关注事项表")
    List<ManManufactureOrderConcernTask>   manufactureOrderConcernTaskList;

    @TableField(exist = false)
    private String confirmerAccountName;

    @TableField(exist = false)
    private String materialCode;

    @ApiModelProperty(value = "查询：生产所属阶段")
    @TableField(exist = false)
    private String[] produceStageList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：工厂工序")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：工序")
    private Long[] processSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询：班组")
    private Long[] workCenterSidList;

    @ApiModelProperty(value = "周计划总结")
    private String  weekRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "存在明细行的数量值都为空，是否继续")
    private String continueIsNull;
}
