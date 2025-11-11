package com.platform.ems.plug.domain;

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

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 出入库作业类型&单据作业类型对照对象 s_con_inout_document_movement_type_relation
 *
 * @author c
 * @date 2022-03-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_inout_document_movement_type_relation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConInoutDocumentMovementTypeRelation extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-款项类别sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-款项类别sid")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;

    /**
     * 出入库库存凭证类别
     */
    @NotEmpty(message = "出入库库存凭证类别不能为空")
    @ApiModelProperty(value = "出入库库存凭证类别")
    private String invDocCategoryCode;

    @TableField(exist = false)
    private String[] invDocCategoryCodeList;

    @TableField(exist = false)
    @Excel(name = "出入库库存凭证类别")
    @ApiModelProperty(value = "出入库库存凭证类别名称")
    private String invDocCategoryCodeName;

    /**
     * 出入库作业类型(移动类型)编码
     */
    @NotEmpty(message = "出入库作业类型名称不能为空")
    @ApiModelProperty(value = "出入库作业类型(移动类型)编码")
    private String inOutMovementTypeCode;

    @TableField(exist = false)
    private String[] inOutMovementTypeCodeList;

    @TableField(exist = false)
    @Excel(name = "出入库作业类型名称")
    @ApiModelProperty(value = "出入库作业类型名称")
    private String inOutMovementTypeName;

    /**
     * 单据作业类型(移动类型)编码
     */
    @NotEmpty(message = "单据作业类型名称不能为空")
    @ApiModelProperty(value = "单据作业类型(移动类型)编码")
    private String documentMovementTypeCode;

    @TableField(exist = false)
    private String[] documentMovementTypeCodeList;

    @TableField(exist = false)
    @Excel(name = "单据作业类型名称")
    @ApiModelProperty(value = "单据作业类型名称")
    private String documentMovementTypeName;

    /**
     * 启用/停用状态（数据字典的键值）
     */
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值）")
    private String status;

    /**
     * 处理状态（数据字典的键值）
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @Excel(name = "创建人")
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
     * 确认人账号（用户名称）
     */
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

}
