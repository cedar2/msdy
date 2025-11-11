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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.experimental.Accessors;

/**
 * 项目任务模板对象 s_prj_task_template
 *
 * @author chenkw
 * @date 2022-12-07
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_prj_task_template")
public class PrjTaskTemplate extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-系统SID-项目任务模板
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-系统SID-项目任务模板")
    private Long taskTemplateSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] taskTemplateSidList;

    /**
     * 项目任务模板名称
     */
    @NotBlank(message = "任务模板名称不能为空")
    @Excel(name = "任务模板名称")
    @ApiModelProperty(value = "项目任务模板名称")
    private String taskTemplateName;

    /**
     * 项目任务模板编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目任务模板编码")
    private Long taskTemplateCode;

    /**
     * 项目类型（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "项目类型不能为空")
    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型（数据字典的键值或配置档案的编码）")
    private String projectType;

    /**
     * 项目类型（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目类型（多选）")
    private String[] projectTypeList;

    /**
     * 总任务数
     */
    @TableField(exist = false)
    @Excel(name = "总任务数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "总任务数")
    private String itemCount;

    /**
     * 模板周期（天）
     */
    @Excel(name = "模板周期(天)")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "模板周期（天）")
    private Long templateTime;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 处理状态（多选）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（多选）")
    private String[] handleStatusList;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotBlank(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

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

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

	@Excel(name = "更新人")
	@ApiModelProperty(value = "更改人昵称")
	@TableField(exist = false)
	private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号（用户账号）
     */
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

	@Excel(name = "确认人")
	@ApiModelProperty(value = "确认人昵称")
	@TableField(exist = false)
	private String confirmerAccountName;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    /**
     * 明细列表
     */
    @Valid
    @TableField(exist = false)
    @ApiModelProperty(value = "明细列表")
    private List<PrjTaskTemplateItem> taskTemplateItemList;

}
