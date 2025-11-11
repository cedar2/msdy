package com.platform.ems.plug.domain;

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

import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 数据对象类别与处理状态对象 s_con_dataobject_handle_status
 *
 * @author chenkw
 * @date 2022-06-23
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_dataobject_handle_status")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConDataobjectHandleStatus extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-数据对象类别与处理状态关联信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-数据对象类别与处理状态关联信息")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;

    /**
     * 数据对象类别sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据对象类别sid")
    private Long dataobjectCategorySid;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别sid")
    private Long[] dataobjectCategorySidList;

    /**
     * 数据对象类别code
     */
    @Excel(name = "数据对象类别编码")
    @ApiModelProperty(value = "数据对象类别code")
    private String dataobjectCategoryCode;

    @TableField(exist = false)
    @Excel(name = "数据对象类别名称")
    @ApiModelProperty(value = "数据对象类别名称")
    private String dataobjectCategoryName;

    /**
     * 数据对象类别所用到的处理状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "对应处理状态")
    @ApiModelProperty(value = "数据对象类别所用到的处理状态（数据字典的键值或配置档案的编码）")
    //@NotBlank(message = "对应处理状态不能为空")
    private String dataobjectHandleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据对象类别所用到的处理状态（数据字典的键值或配置档案的编码）")
    private String[] dataobjectHandleStatusList;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "树形子列表")
    List<ConDataobjectHandleStatus> children;

}
