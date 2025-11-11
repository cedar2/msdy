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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 面辅料检测单-检测项目对象 s_qua_rawmat_check_item
 *
 * @author linhongwei
 * @date 2022-04-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_qua_rawmat_check_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuaRawmatCheckItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-面辅料检测单-检测项目
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-面辅料检测单-检测项目")
    private Long rawmatCheckItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] rawmatCheckItemSidList;
    /**
     * 系统SID-面辅料检测单
     */
    @Excel(name = "系统SID-面辅料检测单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-面辅料检测单")
    private Long rawmatCheckSid;

    /**
     * 项目类别sid
     */
    @Excel(name = "项目类别sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目类别sid")
    private Long itemCategory;

    /**
     * 项目sid
     */
    @Excel(name = "项目sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "项目sid")
    private Long itemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目编码")
    private String itemCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称")
    private String itemName;

    /**
     * 项目值
     */
    @Excel(name = "项目值")
    @ApiModelProperty(value = "项目值")
    private BigDecimal itemValue;

    /**
     * 结果（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "结果（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "结果（数据字典的键值或配置档案的编码）")
    private String itemResult;

    /**
     * 原项目值
     */
    @Excel(name = "原项目值")
    @ApiModelProperty(value = "原项目值")
    private BigDecimal preItemValue;

    /**
     * 原结果（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "原结果（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "原结果（数据字典的键值或配置档案的编码）")
    private String preItemResult;

    /**
     * 创建人账号（用户账号）
     */
    @Excel(name = "创建人账号（用户账号）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户账号）")
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
     * 更新人账号（用户账号）
     */
    @Excel(name = "更新人账号（用户账号）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户账号）")
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
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

}
