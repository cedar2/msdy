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

import javax.validation.constraints.NotEmpty;


/**
 * 生产订单-组件对象 s_man_manufacture_order_component
 *
 * @author qhq
 * @date 2021-04-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_man_manufacture_order_component")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManManufactureOrderComponent extends EmsBaseEntity {
    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统自增长ID-生产订单-组件 */
    @TableId
    @ApiModelProperty(value = "系统自增长ID-生产订单-组件")
    private String manufactureOrderComponentSid;

    /** 系统自增长ID-生产订单 */
    @Excel(name = "系统自增长ID-生产订单")
    @ApiModelProperty(value = "系统自增长ID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderSid;

    /** 系统自增长ID-商品&物料&服务 */
    @Excel(name = "系统自增长ID-商品&物料&服务")
    @ApiModelProperty(value = "系统自增长ID-商品&物料&服务")
    private String materialSid;

    /** 系统自增长ID-商品sku1 */
    @Excel(name = "系统自增长ID-商品sku1")
    @ApiModelProperty(value = "系统自增长ID-商品sku1")
    private String sku1Sid;

    /** 系统自增长ID-商品sku2 */
    @Excel(name = "系统自增长ID-商品sku2")
    @ApiModelProperty(value = "系统自增长ID-商品sku2")
    private String sku2Sid;

    /** 系统自增长ID-商品条码 */
    @Excel(name = "系统自增长ID-商品条码")
    @ApiModelProperty(value = "系统自增长ID-商品条码")
    private String barcodeSid;

    /** 部位 */
    @Excel(name = "部位")
    @ApiModelProperty(value = "部位")
    private String position;

    /** 物料名称 */
    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    /** 用量 */
    @Excel(name = "用量")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用量")
    private Long quantity;

    /** 损耗率(%) */
    @Excel(name = "损耗率(%)")
    @ApiModelProperty(value = "损耗率(%)")
    private BigDecimal lossRate;

    /** 计量单位 */
    @Excel(name = "计量单位")
    @ApiModelProperty(value = "计量单位")
    private String unitBase;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @Excel(name = "物料编码")
    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;


}
