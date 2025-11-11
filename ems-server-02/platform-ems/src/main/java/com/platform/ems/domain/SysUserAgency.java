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

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 账号代办设置对象 s_sys_user_agency
 *
 * @author qhq
 * @date 2021-10-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_sys_user_agency")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserAgency extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-账号代办设置信息 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-账号代办设置信息")
    private Long userAgencySid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] userAgencySidList;
    /** 原用户账号（用户账号） */
    @Excel(name = "原用户账号（用户账号）")
    @ApiModelProperty(value = "原用户账号（用户账号）")
    private String userId;

    /** 代办人账号（用户账号） */
    @Excel(name = "代办人账号（用户账号）")
    @ApiModelProperty(value = "代办人账号（用户账号）")
    private String agencyUserId;

    /** 代办类型（数据字典的键值或配置档案的编码） */
    @Excel(name = "代办类型（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "代办类型（数据字典的键值或配置档案的编码）")
    private String category;

    /** 有效期（起） */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（起）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（起）")
    private Date startDate;

    /** 有效期（止） */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "有效期（止）", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "有效期（止）")
    private Date endDate;

    /** 数据类型（流程key） */
    @Excel(name = "数据类型（流程key）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据类型（流程key）")
    private Long dataobjectCategoryCode;

    /** 处理状态（数据字典的键值或配置档案的编码） */
    @NotEmpty(message = "状态不能为空")
    @Excel(name = "处理状态（数据字典的键值或配置档案的编码）",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
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


}
