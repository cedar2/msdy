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

import java.util.Date;

/**
 * 线部位组明细对象 s_tec_line_position_group_item
 *
 * @author linhongwei
 * @date 2021-08-19
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_tec_line_position_group_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecLinePositionGroupItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-线部位组明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线部位组明细")
    private Long groupItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] groupItemSidList;
    /**
     * 系统SID-线部位组档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线部位组档案")
    private Long groupSid;

    /**
     * 系统SID-线部位档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-线部位档案")
    private Long linePositionSid;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

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

    @TableField(exist = false)
    @Excel(name = "线部位组编码")
    @ApiModelProperty(value = "线部位组编码")
    private String groupCode;

    @TableField(exist = false)
    @Excel(name = "线部位组名称")
    @ApiModelProperty(value = "线部位组名称")
    private String groupName;

    @TableField(exist = false)
    @Excel(name = "线部位编码")
    @ApiModelProperty(value = "线部位编码")
    private String linePositionCode;

    @TableField(exist = false)
    @Excel(name = "线部位名称")
    @ApiModelProperty(value = "线部位名称")
    private String linePositionName;

    @TableField(exist = false)
    @ApiModelProperty(value = "度量方法说明")
    private String measureDescription;

    @TableField(exist = false)
    @Excel(name = "上下装/套装", dictType = "s_up_down_suit")
    @ApiModelProperty(value = "上下装/套装")
    private String upDownSuit;

    @TableField(exist = false)
    @Excel(name = "线部位类别", dictType = "s_line_position_category")
    @ApiModelProperty(value = "线部位类别")
    private String linePositionCategory;

    @TableField(exist = false)
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @TableField(exist = false)
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "上下装/套装list")
    private String[] upDownSuitList;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;


}
