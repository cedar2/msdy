package com.platform.ems.plug.domain;

import java.math.BigDecimal;
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
 * 生产里程碑清单-明细对象 s_con_manufacture_milestone_list_item
 *
 * @author platform
 * @date 2024-03-14
 */
@Data
@Accessors(chain = true)
@TableName(value = "s_con_manufacture_milestone_list_item")
public class ConManufactureMilestoneListItem extends EmsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产里程碑清单-明细表
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产里程碑清单-明细表")
    private Long manufactureMilestoneListItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "sid数组")
    private Long[] manufactureMilestoneListItemSidList;

    /**
     * 系统SID-生产里程碑清单表
     */
    @Excel(name = "系统SID-生产里程碑清单表")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产里程碑清单表")
    private Long manufactureMilestoneListSid;

    /**
     * 序号
     */
    @NotNull(message = "序号不能为空")
    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    /**
     * 里程碑
     */
    @NotBlank(message = "里程碑不能为空")
    @Excel(name = "里程碑")
    @ApiModelProperty(value = "里程碑")
    private String milestone;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "创建人账号")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

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
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @Excel(name = "更新人账号")
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /**
     * 更改人
     */
    @TableField(exist = false)
    @Excel(name = "更改人")
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "数据源系统")
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


}
