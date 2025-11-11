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
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 生产进度日报-完工明细对象 s_man_day_manufacture_progress_detail
 *
 * @author chenkw
 * @date 2022-06-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_day_manufacture_progress_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManDayManufactureProgressDetail extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产进度日报单完工明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单完工明细")
    private Long dayManufactureProgressDetailSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dayManufactureProgressDetailSidList;

    /**
     * 系统SID-生产进度日报单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产进度日报单明细")
    private Long dayManufactureProgressItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-生产进度日报单明细")
    private Long[] dayManufactureProgressItemSidList;

    /**
     * 完工明细的SKU1sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完工明细的SKU1sid")
    private Long sku1Sid;

    /**
     * 完工明细的SKU1类型
     */
    @Excel(name = "完工明细的SKU1类型")
    @ApiModelProperty(value = "完工明细的SKU1类型")
    private String sku1Type;

    @Excel(name = "完工明细的SKU1名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "完工明细的SKU1名称")
    private String sku1Name;

    /**
     * 完工明细的SKU2sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "完工明细的SKU2sid")
    private Long sku2Sid;

    /**
     * 完工明细的SKU2类型
     */
    @Excel(name = "完工明细的SKU2类型")
    @ApiModelProperty(value = "完工明细的SKU2类型")
    private String sku2Type;

    @Excel(name = "完工明细的SKU2名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "完工明细的SKU2名称")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    /**
     * 当天实际完成量
     */
    @Excel(name = "当天实际完成量")
    @ApiModelProperty(value = "当天实际完成量")
    private BigDecimal quantity;

    /**
     * 当天计划完成量
     */
    @Excel(name = "当天计划完成量")
    @ApiModelProperty(value = "当天计划完成量")
    private BigDecimal planQuantity;

    /**
     * 当天外发料量
     */
    @Excel(name = "当天外发料量")
    @ApiModelProperty(value = "当天外发料量")
    private BigDecimal issueQuantity;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期/汇报日期")
    private Date documentDate;

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
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

}
