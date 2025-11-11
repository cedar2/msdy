package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 生产日计划对象 s_man_day_manufacture_plan
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_day_manufacture_plan")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayManufacturePlan extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产日计划单
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产日计划单")
    private Long dayManufacturePlanSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dayManufacturePlanSidList;
    /**
     * 生产日计划单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产日计划单号")
    private Long dayManufacturePlanCode;

    /**
     * 工厂sid
     */
    @NotNull(message = "工厂不能为空")
    @Excel(name = "工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工作中心sid
     */
    @NotNull(message = "工作中心不能为空")
    @Excel(name = "工作中心sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心sid")
    private Long workCenterSid;

    /**
     * 日计划日期
     */
    @NotNull(message = "日计划日期不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "日计划日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "日计划日期")
    private Date documentDate;

    /**
     * 单据类型编码code
     */
    @Excel(name = "单据类型编码code")
    @ApiModelProperty(value = "单据类型编码code")
    private String documentType;

    /**
     * 业务类型编码code
     */
    @Excel(name = "业务类型编码code")
    @ApiModelProperty(value = "业务类型编码code")
    private String businessType;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @ApiModelProperty(value = "系统SID-公司档案")
    private String companySid;

    /**
     * 数据来源类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据来源类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "数据来源类别（数据字典的键值或配置档案的编码）")
    private String referDocCategory;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /** 工厂编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /** 工厂名称 */
    @TableField(exist = false)
    @Excel(name = "工厂名称")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /** 工厂简称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    /** 工作中心编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "工作中心编码")
    private String workCenterCode;

    /** 工作中心名称 */
    @TableField(exist = false)
    @Excel(name = "工作中心名称")
    @ApiModelProperty(value = "工作中心名称")
    private String workCenterName;

    /**
     * 工厂sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    /**
     * 工作中心sid
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工作中心sid")
    private Long[] workCenterSidList;

    /**
     * 处理状态s
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态s")
    private String[] handleStatusList;

    /**
     * 日计划日期开始
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "日计划日期结束")
    private String documentBeginDate;

    /**
     * 日计划日期开始
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "日计划日期结束")
    private String documentEndDate;

    /** 生产日计划sids */
    @TableField(exist = false)
    @ApiModelProperty(value = "生产日计划sids")
    private List<Long> dayManufacturePlanSids;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private List<String> creatorAccountList;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    /**
     * 生产日计划-明细对象
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "生产日计划-明细对象")
    private List<ManDayManufacturePlanItem> manDayManufacturePlanItemList;
}
