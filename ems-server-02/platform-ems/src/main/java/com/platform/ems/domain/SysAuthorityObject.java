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
import com.platform.ems.domain.base.SysAuthorityEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 权限对象对象 s_sys_authority_object
 *
 * @author straw
 * @date 2023-01-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_authority_object")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAuthorityObject extends EmsBaseEntity implements SysAuthorityEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    String clientId;

    /**
     * 系统SID-权限对象
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-权限对象")
    Long authorityObjectSid;

    @ApiModelProperty(value = "权限对象sid数组")
    @TableField(exist = false)
    Long[] authorityObjectSidList;

    /**
     * 权限字段编码
     */
    @Excel(name = "权限字段编码")
    @ApiModelProperty(value = "权限字段编码")
    String objectCode;

    /**
     * 权限对象名称
     */
    @NotBlank(message = "权限对象名称不能为空")
    @Excel(name = "权限对象名称")
    @ApiModelProperty(value = "权限对象名称")
    String objectName;

    /**
     * 权限类别
     */
    @Excel(name = "权限对象类别",
           dictType = "s_authority_category")
    @ApiModelProperty(value = "权限类别")
    @NotBlank(message = "权限类别不能为空")
    String category;

    /**
     * 权限对象来源
     */
    @Excel(name = "权限对象来源",
           dictType = "s_authority_source")
    @ApiModelProperty(value = "权限对象来源")
    @NotBlank(message = "权限对象来源不能为空")
    String createSource;

    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用",
           dictType = "s_valid_flag")
    @NotBlank(message = "启停状态不能为空")
    @ApiModelProperty(value = "启用/停用")
    String status;

    /**
     * 处理状态
     */
    @NotBlank(message = "处理状态不能为空")
    @Excel(name = "处理状态",
           dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    String handleStatus;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    String remark;

    /* -------------------------------- */
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    String creatorAccountName;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    String updaterAccountName;

    /**
     * 更改日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "更改日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改日期")
    Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人昵称")
    @TableField(exist = false)
    String confirmerAccountName;

    /**
     * 确认日期
     */
    @JsonFormat(timezone = "GMT+8",
                pattern = "yyyy-MM-dd")
    @Excel(name = "确认日期",
           width = 30,
           dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认日期")
    Date confirmDate;

    /* --------------------------- */

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    String creatorAccount;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    String updaterAccount;

    /**
     * 确认人
     */
    @ApiModelProperty(value = "确认人")
    String confirmerAccount;


    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "权限字段sid数组，表示对象关联的字段的所有sid")
    Long[] authorityFieldSidList;

    @ApiModelProperty(value = "处理状态-多选")
    @TableField(exist = false)
    String[] handleStatusList;

    @ApiModelProperty(value = "权限类别-多选")
    @TableField(exist = false)
    String[] categoryList;

    @ApiModelProperty(value = "下属权限字段列表")
    @TableField(exist = false)
    List<SysAuthorityObjectField> sysAuthorityFieldList;
}
