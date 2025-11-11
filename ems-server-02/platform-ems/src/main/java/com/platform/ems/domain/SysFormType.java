package com.platform.ems.domain;

import java.util.Date;

import javax.validation.constraints.NotEmpty;

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


/**
 * 系统单据定义对象 s_sys_form_type
 *
 * @author qhq
 * @date 2021-09-06
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_form_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysFormType extends EmsBaseEntity {
    /**  */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "")
    private Long id;
    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] idList;

    /** 单据定义类型 */
    @Excel(name = "单据定义类型")
    @ApiModelProperty(value = "单据定义类型")
    private String formType;

    /** 单据定义名称 */
    @Excel(name = "单据定义名称")
    @ApiModelProperty(value = "单据定义名称")
    private String formName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date modifyDate;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    @ApiModelProperty(value = "创建人ID")
    private String createById;

    /** 更新人ID */
    @Excel(name = "更新人ID")
    @ApiModelProperty(value = "更新人ID")
    private String modifyById;

    /** 状态 */
    @Excel(name = "状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "状态")
    private String status;

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;
}
