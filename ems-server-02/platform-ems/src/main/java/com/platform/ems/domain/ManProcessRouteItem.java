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

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工艺路线-工序对象 s_man_process_route_item
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_process_route_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManProcessRouteItem extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统自增长ID-工艺路线-工序
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工艺路线-工序")
    private Long processRouteProcessSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] processRouteProcessSidList;
    /**
     * 系统自增长ID-工艺路线
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工艺路线")
    private Long processRouteSid;

    /**
     * 系统自增长ID-工序
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-工序")
    private Long processSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门ID")
    private String departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门")
    private String department;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    /**
     * 单位作业量
     */
    @ApiModelProperty(value = "单位作业量")
    private BigDecimal outputPerUnit;

    /**
     * 生产方式（数据字典的键值或配置档案的编码），自产、外发
     */
    @ApiModelProperty(value = "生产方式（数据字典的键值或配置档案的编码），自产、外发")
    private String productionMode;

    /**
     * 作业计量单位编码
     */
    @ApiModelProperty(value = "作业计量单位编码")
    private String worktimeUnit;

    /**
     * 单位用时
     */
    @ApiModelProperty(value = "单位用时")
    private Integer timePerUnit;

    /**
     * 用时计量单位编码
     */
    @ApiModelProperty(value = "用时计量单位编码")
    private String timeMeasureUnit;

    /**
     * 是否标志阶段完成的工序（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否标志阶段完成的工序（数据字典的键值或配置档案的编码）")
    private String isStageComplete;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完成量校验参考工序sid")
    private Long quantityReferProcessSid;

    @ApiModelProperty(value = "完成量校验参考工序编码")
    private String quantityReferProcessCode;

    @ApiModelProperty(value = "参考工序所引用数量类型（数据字典的键值：s_quantity_type_refer_process）")
    private String quantityTypeReferProcess;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 是否标志成品完工的工序（数据字典的键值或配置档案的编码）
     */
    @ApiModelProperty(value = "是否标志成品完工的工序（数据字典的键值或配置档案的编码）")
    private String isProduceComplete;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工厂")
    private Long[] plantSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @Excel(name = "工厂")
    @ApiModelProperty(value = "工厂编码")
    private String plantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantName;

    @TableField(exist = false)
    @Excel(name = "工艺路线编码")
    @ApiModelProperty(value = "工艺路线编码")
    private String processRouteCode;

    @ApiModelProperty(value = "特殊工序标识（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String specialFlag;

    @TableField(exist = false)
    @Excel(name = "工艺路线名称")
    @ApiModelProperty(value = "工艺路线名称")
    private String processRouteName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-公司档案")
    private Long companySid;

    @Excel(name = "客户")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户")
    private String customerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "类别", dictType = "s_process_route_category")
    @ApiModelProperty(value = "工艺路线类别")
    private String processRouteCategory;

    @TableField(exist = false)
    @Excel(name = "工序编码")
    @ApiModelProperty(value = "工序编码")
    private String processCode;

    @Excel(name = "工序名称")
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-负责人(员工档案)")
    private Long directorSid;

    @ApiModelProperty(value = "负责人编码(员工档案)")
    private String directorCode;

    @TableField(exist = false)
    @Excel(name = "负责人")
    @ApiModelProperty(value = "负责人姓名(员工档案)")
    private String directorName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工作中心")
    private Long workCenterSid;

    @ApiModelProperty(value = "工作中心编码")
    private String workCenterCode;

    @TableField(exist = false)
    @Excel(name = "班组")
    @ApiModelProperty(value = "工作中心")
    private String workCenterName;

    @Excel(name = "里程碑", dictType = "s_manufacture_milestone")
    @ApiModelProperty(value = "里程碑（数据字典的键值或配置档案的编码）")
    private String milestone;

    @Excel(name = "序号")
    @Digits(integer = 3, fraction = 2, message = "序号整数位上限为3位，小数位上限为2位")
    @ApiModelProperty(value = "序号")
    private BigDecimal serialNum;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    @TableField(exist = false)
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "类别list")
    private String[] processRouteCategoryList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统自增长ID-公司list")
    private Long[] companySidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否第一个工序（数据字典的键值或配置档案的编码）")
    private String isFirstProcess;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:客户")
    private String[] customerSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询:创建人")
    private String[] creatorAccountList;

}
