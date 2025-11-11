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

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 版型-线部位对象 s_tec_model_line_pos
 *
 * @author linhongwei
 * @date 2021-10-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_model_line_pos")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModelLinePos extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-版型线部位
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型线部位")
    private Long modelLinePosSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] modelLinePosSidList;
    /**
     * 系统SID-版型线信息
     */
    @Excel(name = "系统SID-版型线信息")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型线信息")
    private Long modelLineSid;

    /**
     * 系统SID-版型档案
     */
    @Excel(name = "系统SID-版型档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型档案")
    private Long modelSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型编码")
    private String modelCode;

    /**
     * 系统SID-线部位档案
     */
    @Excel(name = "系统SID-线部位档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线部位档案")
    private Long linePositionSid;

    /**
     * 用量
     */
    @Digits(integer = 3, fraction = 1, message = "用量整数位上限为3位，小数位上限为1位")
    @Excel(name = "用量")
    @ApiModelProperty(value = "用量")
    private BigDecimal quantity;

    /**
     * 线部位类别（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "线部位类别（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "线部位类别（数据字典的键值或配置档案的编码）")
    private String linePositionCategory;

    /**
     * 计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "计量单位（数据字典的键值或配置档案的编码）")
    private String unit;

    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位名称")
    private String unitName;

    /**
     * 线部位名称
     */
    @Excel(name = "线部位名称")
    @ApiModelProperty(value = "线部位名称")
    private String linePositionName;

    @TableField(exist = false)
    @ApiModelProperty(value = "线部位编码")
    private String linePositionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "线部位档案名称")
    private String newestLinePositionName;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long serialNum;

    /**
     * 度量方法说明
     */
    @Excel(name = "度量方法说明")
    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "线部位sids")
    private List<Long> linePositionSids;


}
