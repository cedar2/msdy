package com.platform.ems.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 任务模板对照关系Controller对象 s_con_task_template_compare
 *
 * @author platform
 * @date 2023-11-03
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_con_task_template_compare")
public class ConTaskTemplateCompare extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-任务模板对照关系
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务模板对照关系")
    private Long taskTemplateCompareSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] taskTemplateCompareSidList;

    /**
     * 系统SID-任务模板
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-任务模板")
    @NotNull(message = "任务模板sid不能为空")
    private Long taskTemplateSid;

    /**
     * 任务模板名称
     */
    @TableField(exist = false)
    @Excel(name = "任务模板名称")
    @ApiModelProperty(value = "任务模板名称")
    private String taskTemplateName;

    /**
     * 项目类型（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "项目类型", dictType = "s_project_type")
    @ApiModelProperty(value = "项目类型")
    private String projectType;

    /**
     * 品牌（数据字典的键值或配置档案的编码）
     */
//    @Excel(name = "品牌", dictType = "s_brand")
    @ApiModelProperty(value = "品牌")
    private String brand;


    /**
     * 经营模式（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "经营模式", dictType = "s_operate_mode")
    @ApiModelProperty(value = "经营模式")
    private String operateMode;

    /**
     * 系统SID-后续事项模板
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-后续事项模板")
    private Long afterTaskTemplateSid;

    /**
     * 后续事项模板编码
     */
    @ApiModelProperty(value = "后续事项模板编码")
    private String afterTaskTemplateCode;

    /**
     * 后续事项模板名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "后续事项模板名称")
    @Excel(name = "后续事项模板")
    private String afterTaskTemplateName;

    /**
     * 加盟模式（数据字典的键值或配置档案的编码）
     */
//    @Excel(name = "加盟模式", dictType = "s_join_mode")
    @ApiModelProperty(value = "加盟模式")
    private String joinMode;

    /**
     * 门店定位类型（数据字典的键值或配置档案的编码）
     */
//    @Excel(name = "线下门店定位类别", dictType = "s_store_category_xx")
    @ApiModelProperty(value = "线下门店定位类别")
    private String storeCategory;

    /**
     * 任务模板编码
     */
    @Excel(name = "任务模板编码")
    @ApiModelProperty(value = "任务模板编码")
    @NotBlank(message = "任务模板编码不能为空")
    private String taskTemplateCode;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /**
     * 创建人账号数组
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号数组")
    private String[] creatorAccountList;

    /**
     * 创建人
     */
    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

}

