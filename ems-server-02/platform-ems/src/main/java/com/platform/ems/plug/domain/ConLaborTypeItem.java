package com.platform.ems.plug.domain;

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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 工价类型/工价费用项对照对象 s_con_labor_type_item
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_labor_type_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConLaborTypeItem extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-节点ID（工价类型/工价费用项对照）
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-节点ID（工价类型/工价费用项对照）")
    private Long laborTypeItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] laborTypeItemSidList;

    @Excel(name = "工价类型")
    @TableField(exist = false)
    @ApiModelProperty(value = "工价名称")
    private String laborTypeName;

    /**
     * 工价项编码（人工编码）
     */
    @NotBlank(message = "工价项编码不能为空")
    @Length(max = 8, message = "工价项编码不能超过8个字符")
    @Excel(name = "工价项编码")
    @ApiModelProperty(value = "工价项编码（人工编码）")
    private String itemCode;

    /**
     * 工价费用项名称
     */
    @Length(max = 300, message = "工价项名称不能超过300个字符")
    @NotBlank(message = "工价项名称不能为空")
    @Excel(name = "工价项名称")
    @ApiModelProperty(value = "工价费用项名称")
    private String itemName;

    @Excel(name = "工序")
    @TableField(exist = false)
    @ApiModelProperty(value = "工序名称")
    private String processName;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 启用/停用状态
     */
    @NotEmpty(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    /**
     * 处理状态
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 创建人账号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 系统SID-工序（加工项）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工序（加工项）")
    private Long processSid;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更新人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 系统SID-工价类型sid
     */
    @NotNull(message = "工价类型不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-工价类型sid")
    private Long laborTypeSid;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer itemSort;

    @ApiModelProperty(value = "工价编码")
    private String laborTypeCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序编码")
    private String processCode;

    /**
     * 系统SID-工价类型sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-工价类型sid")
    private Long[] laborTypeSidList;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

}
