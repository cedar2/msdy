package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

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

import lombok.experimental.Accessors;

/**
 * 生产日计划-明细对象 s_man_day_manufacture_plan_item
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_day_manufacture_plan_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayManufacturePlanItem extends EmsBaseEntity {

    /** 租户ID */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 系统SID-生产日计划单明细 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产日计划单明细")
    private Long dayManufacturePlanItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long [] dayManufacturePlanItemSidList;
    /** 系统SID-生产计划单明细 */
    @Excel(name = "系统SID-生产计划单明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产计划单明细")
    private Long manufacturePlanItemSid;

    /** 当天计划完成量 */
    @Excel(name = "当天计划完成量")
    @ApiModelProperty(value = "当天计划完成量")
    private BigDecimal planQuantity;

    /** 行号 */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /** 计划日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划日期")
    private Date planDate;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人（昵称）")
    private String creatorAccountName;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人（昵称）")
    private String updaterAccountName;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统（数据字典的键值或配置档案的编码） */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工作中心(班组)")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workCenterSid ;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品sid")
    private Long materialSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "排产批次号")
    private Long paichanBatch;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "sku1Sid")
    private Long sku1Sid;
}
