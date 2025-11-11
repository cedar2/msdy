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
 * 生产周进度汇总对象 s_man_produce_week_progress_total
 *
 * @author chenkw
 * @date 2022-08-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_produce_week_progress_total")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManProduceWeekProgressTotal extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-生产周进度汇总记录
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产周进度汇总记录")
    private Long weekProgressTotalSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] weekProgressTotalSidList;

    /**
     * 周计划日期(起)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期(起)")
    private Date dateStart;

    /**
     * 周计划日期(至)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "周计划日期(至)")
    private Date dateEnd;

    @TableField(exist = false)
    @ApiModelProperty(value = "近几周")
    private Integer recentWeeks;

    /**
     * 工厂sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂sid")
    private Long[] plantSidList;

    /**
     * 工厂编码
     */
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 操作部门sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "操作部门sid")
    private Long departmentSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门sid")
    private Long[] departmentSidList;

    /**
     * 操作部门code
     */
    @ApiModelProperty(value = "操作部门code")
    private String departmentCode;

    @Excel(name = "操作部门")
    @TableField(exist = false)
    @ApiModelProperty(value = "操作部门名称")
    private String departmentName;

    @Excel(name = "周日期段")
    @TableField(exist = false)
    @ApiModelProperty(value = "周计划日期段")
    private String dateRange;

    /**
     * 计划完成数/目标数(本周)
     */
    @ApiModelProperty(value = "计划完成数/目标数(本周)")
    private BigDecimal quantityJih;

    @Excel(name = "目标数(本周)", cellType = Excel.ColumnType.NUMERIC)
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成数/目标数(本周)")
    private String quantityJihString;

    /**
     * 完成数(本周)
     */
    @ApiModelProperty(value = "完成数(本周)")
    private BigDecimal quantityWanc;

    @Excel(name = "完成数(本周)", cellType = Excel.ColumnType.NUMERIC)
    @TableField(exist = false)
    @ApiModelProperty(value = "完成数(本周)")
    private String quantityWancString;

    /**
     * 欠数(本周)
     */
    @ApiModelProperty(value = "欠数(本周)")
    private BigDecimal quantityQian;

    @Excel(name = "欠数(本周)", cellType = Excel.ColumnType.NUMERIC)
    @TableField(exist = false)
    @ApiModelProperty(value = "欠数(本周)")
    private String quantityQianString;

    /**
     * 本周完成率(%)
     */
    @ApiModelProperty(value = "本周完成率(%)")
    private BigDecimal rateWanc;

    @Excel(name = "本周完成率(%)")
    @TableField(exist = false)
    @ApiModelProperty(value = "本周完成率(%)")
    private String rateWancPercent;

    /**
     * 本周欠率(%)
     */
    @ApiModelProperty(value = "本周欠率(%)")
    private BigDecimal rateQian;

    @Excel(name = "本周欠率(%)")
    @TableField(exist = false)
    @ApiModelProperty(value = "本周欠率(%)")
    private String rateQianPercent;

    /**
     * 创建人账号（用户账号）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
    private String creatorAccount;

    @ApiModelProperty(value = "创建人昵称")
    @TableField(exist = false)
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户账号）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
    private String updaterAccount;

    @ApiModelProperty(value = "更改人昵称")
    @TableField(exist = false)
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;
}
