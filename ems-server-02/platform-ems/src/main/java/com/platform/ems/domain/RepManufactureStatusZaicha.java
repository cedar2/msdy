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
 * 生产状况-在产对象
 *
 * @author c
 * @date 2022-03-17
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_manufacture_status_zaicha")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepManufactureStatusZaicha extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 数据记录sid
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "数据记录sid")
    private Long dataRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dataRecordSidList;
    /**
     * 生产订单sid
     */
    @Excel(name = "生产订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单sid")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

    /**
     * 工厂sid
     */
    @Excel(name = "工厂sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "工厂sid")
    private Long plantSid;

    /**
     * 工厂code
     */
    @Excel(name = "工厂code")
    @ApiModelProperty(value = "工厂code")
    private String plantCode;

    /**
     * 工厂简称
     */
    @Excel(name = "工厂简称")
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    /**
     * 生产订单的业务类型
     */
    @Excel(name = "生产订单的业务类型")
    @ApiModelProperty(value = "生产订单的业务类型")
    private String moBusinessType;

    /**
     * 生产订单商品行sid
     */
    @Excel(name = "生产订单商品行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单商品行sid")
    private Long manufactureOrderProductSid;

    /**
     * 物料/商品sid
     */
    @Excel(name = "物料/商品sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品sid")
    private Long materialSid;

    /**
     * 物料/商品编码
     */
    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    /**
     * SKU1 sid
     */
    @Excel(name = "SKU1 sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1 sid")
    private Long sku1Sid;

    /**
     * SKU1 code
     */
    @Excel(name = "SKU1 code")
    @ApiModelProperty(value = "SKU1 code")
    private String sku1Code;

    /**
     * SKU2 sid
     */
    @Excel(name = "SKU2 sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2 sid")
    private Long sku2Sid;

    /**
     * SKU2 code
     */
    @Excel(name = "SKU2 code")
    @ApiModelProperty(value = "SKU2 code")
    private String sku2Code;

    /**
     * 计划完成日期_生产订单
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期_生产订单", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期_生产订单")
    private Date planEndDateMo;

    /**
     * 计划完成日期_生产订单商品明细
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期_生产订单商品明细", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期_生产订单商品明细")
    private Date planEndDateProduct;

    /**
     * 计划产量
     */
    @Excel(name = "计划产量")
    @ApiModelProperty(value = "计划产量")
    private BigDecimal quantityJih;

    /**
     * 已入库量
     */
    @Excel(name = "已入库量")
    @ApiModelProperty(value = "已入库量")
    private BigDecimal quantityYiwc;

    /**
     * 未入库量
     */
    @Excel(name = "未入库量")
    @ApiModelProperty(value = "未入库量")
    private BigDecimal quantityWeiwc;

    /**
     * 客户sid
     */
    @Excel(name = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    /**
     * 客户编码
     */
    @Excel(name = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户简称
     */
    @Excel(name = "客户简称")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    /**
     * 产品季code
     */
    @Excel(name = "产品季code")
    @ApiModelProperty(value = "产品季code")
    private String productSeasonCode;

    /**
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderSid;

    /**
     * 销售订单sid
     */
    @Excel(name = "销售订单sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderCode;

    /**
     * 销售订单行sid
     */
    @Excel(name = "销售订单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单行sid")
    private Long salesOrderItemSid;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @ApiModelProperty(value = "处理状态")
    @TableField(exist = false)
    private String handleStatus;

}
