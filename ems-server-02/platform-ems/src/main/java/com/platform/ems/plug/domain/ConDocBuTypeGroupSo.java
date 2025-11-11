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

import lombok.experimental.Accessors;

/**
 * 销售订单单据类型与业务类型组合关系对象 s_con_doc_bu_type_group_so
 *
 * @author chenkw
 * @date 2021-12-24
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_con_doc_bu_type_group_so")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConDocBuTypeGroupSo extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-销售订单单据类型与业务类型组合关系信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单单据类型与业务类型组合关系信息")
    private Long sid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] sidList;

    @Excel(name = "单据类型编码")
    @NotBlank(message = "请选择单据类型")
    @ApiModelProperty(value = "单据类型(销售订单)编码（数据字典的键值或配置档案的编码）")
    private String docTypeCode;

    @Excel(name = "单据类型名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型(销售订单)名称")
    private String docTypeName;

    @Excel(name = "业务类型编码")
    @NotBlank(message = "请选择业务类型")
    @ApiModelProperty(value = "业务类型(销售订单)编码（数据字典的键值或配置档案的编码）")
    private String buTypeCode;

    @Excel(name = "业务类型名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型(销售订单)名称")
    private String buTypeName;

    @Excel(name = "是否无需审批", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否无需审批（数据字典的键值）")
    private String isNonApproval;

    /**
     * 序号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "序号")
    private Long sort;

    /**
     * 启用/停用状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "启停状态不能为空")
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态（数据字典的键值或配置档案的编码）")
    private String status;

    /**
     * 处理状态（数据字典的键值或配置档案的编码）
     */
    @NotEmpty(message = "处理状态不能为空")
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态（数据字典的键值或配置档案的编码）")
    private String handleStatus;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号（昵称）")
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
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型(销售订单)多选查询")
    private String[] docTypeCodeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "业务类型(销售订单)多选查询")
    private String[] buTypeCodeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "库存管理方式（数据字典的键值或配置档案的编码）")
    private String inventoryControlMode;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否退货（数据字典的键值或配置档案的编码）")
    private String isReturnGoods;

    @TableField(exist = false)
    @ApiModelProperty(value = " 是否寄售结算（数据字典的键值或配置档案的编码）")
    private String isConsignmentSettle;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否允许编辑价格（数据字典的键值或配置档案的编码）")
    private String isEditPrice;
}
