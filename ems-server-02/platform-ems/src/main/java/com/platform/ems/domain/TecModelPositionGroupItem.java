package com.platform.ems.domain;

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
 * 版型部位组明细对象 s_tec_model_position_group_item
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_model_position_group_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecModelPositionGroupItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-版型部位组明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型部位组明细")
    private Long groupItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] groupItemSidList;
    /**
     * 系统SID-版型部位组档案
     */
    @Excel(name = "系统SID-版型部位组档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型部位组档案")
    private Long groupSid;

    /**
     * 系统SID-版型部位档案
     */
    @Excel(name = "系统SID-版型部位档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-版型部位档案")
    private Long modelPositionSid;

    /**
     * 版型部位档案编码
     */
    @TableField(exist = false)
    @Excel(name = "版型部位档案编码")
    @ApiModelProperty(value = "版型部位档案编码")
    private String modelPositionCode;

    /**
     * 版型部位档案名称
     */
    @TableField(exist = false)
    @Excel(name = "版型部位档案名称")
    @ApiModelProperty(value = "版型部位档案名称")
    private String modelPositionName;

    @Excel(name = "上下装标识")
    @TableField(exist = false)
    @ApiModelProperty(value = "上下装标识")
    private String upDownSuit;

    @Excel(name = "客户")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户")
    private String customerSid;

    @Excel(name = "客户名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @Excel(name = "客方版型部位编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "客方版型部位编码")
    private String customerPositionCode;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /**
     * 度量方法说明
     */
    @TableField(exist = false)
    @Excel(name = "度量方法说明")
    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

    /**
     * 序号
     */
    @Excel(name = "序号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

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
    @ApiModelProperty(value = "版型部位组编码（人工编码）")
    private String groupCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "版型部位组名称")
    private String groupName;

    @TableField(exist = false)
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认人日期")
    private String confirmDate;

    @ApiModelProperty(value = "上下装套装")
    @TableField(exist = false)
    private String[] upDownSuitList;

    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String[] handleStatusList;

    @ApiModelProperty(value = "启停状态")
    @TableField(exist = false)
    private String[] statusList;

    @ApiModelProperty(value = "客户")
    @TableField(exist = false)
    private String[] customerSidList;

}
