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

import javax.validation.constraints.NotEmpty;

import lombok.experimental.Accessors;

/**
 * 物料/商品/服务编码号码段配置对象 s_con_material_code_range_config
 *
 * @author linhongwei
 * @date 2021-06-03
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_material_code_range_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConMaterialCodeRangeConfig extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-物料/商品/服务编码号码段配置
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料/商品/服务编码号码段配置")
    private Long rangeConfigSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] rangeConfigSidList;
    /**
     * 物料类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "物料类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "物料类别（数据字典的键值或配置档案的编码）")
    private String materialCategory;

    /**
     * 号码起
     */
    @Excel(name = "号码起")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "号码起")
    private Long rangeCodeStart;

    /**
     * 号码至
     */
    @Excel(name = "号码至")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "号码至")
    private Long rangeCodeEnd;

    /**
     * 当前号码
     */
    @Excel(name = "当前号码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "当前号码")
    private Long rangeCodePresent;

    /**
     * 公司档案sid
     */
    @Excel(name = "公司档案sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司档案sid")
    private Long companySid;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "启用/停用状态（数据字典的键值或配置档案的编码）", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

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


}
