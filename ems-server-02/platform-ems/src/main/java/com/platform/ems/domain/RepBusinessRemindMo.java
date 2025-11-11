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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 已逾期/即将到期-生产订单对象 s_rep_business_remind_mo
 *
 * @author chenkw
 * @date 2022-04-26
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_rep_business_remind_mo")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepBusinessRemindMo extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /** 数据记录sid */
    @TableId
    @ApiModelProperty(value = "数据记录sid")
    private Long dataRecordSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] dataRecordSidList;

    /**
     * 预警类型：已逾期、即将到期
     */
    @ApiModelProperty(value = "预警类型：已逾期、即将到期")
    private String remindType;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "计划完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期_生产订单")
    private Date planEndDateMo;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期_生产订单(起)")
    private String planEndDateMoBegin;

    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成日期_生产订单(止)")
    private String planEndDateMoEnd;

    /**
     * 物料/商品sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料/商品sid")
    private Long materialSid;

    /**
     * 物料/商品编码
     */
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "物料/商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料/商品名称")
    private String materialName;

    /**
     * SKU1 sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1 sid")
    private Long sku1Sid;

    /**
     * SKU1 code
     */
    @ApiModelProperty(value = "SKU1 code")
    private String sku1Code;

    @Excel(name = "颜色")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1 name")
    private String sku1Name;

    /**
     * SKU2 sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2 sid")
    private Long sku2Sid;

    /**
     * SKU2 code
     */
    @ApiModelProperty(value = "SKU2 code")
    private String sku2Code;

    @Excel(name = "尺码")
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2 name")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku1的序号")
    private BigDecimal sort1;

    @TableField(exist = false)
    @ApiModelProperty(value = "sku2的序号")
    private BigDecimal sort2;

    @Excel(name = "工厂")
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂")
    private String plantName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @Excel(name = "客户")
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    @Excel(name = "计划产量")
    @ApiModelProperty(value = "计划产量")
    private BigDecimal quantityJih;

    @Excel(name = "待完工量")
    @ApiModelProperty(value = "待完工量")
    private BigDecimal quantityWeiwc;

    @Excel(name = "已完工量")
    @ApiModelProperty(value = "已完工量")
    private BigDecimal quantityYiwc;

    @Excel(name = "合同交期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "下单季")
    @TableField(exist = false)
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

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

    @Excel(name = "行号")
    @TableField(exist = false)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

    /**
     * 公司编码
     */
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    /**
     * 公司简称
     */
    @ApiModelProperty(value = "公司简称")
    private String companyName;

    /**
     * 生产订单商品行sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单商品行sid")
    private Long manufactureOrderProductSid;

    /**
     * 计划完成日期_生产订单商品明细
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计划完成日期_生产订单商品明细")
    private Date planEndDateProduct;

    /**
     * 销售订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    /**
     * 销售订单sid
     */
    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    /**
     * 销售订单行sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单行sid")
    private Long salesOrderItemSid;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建日期")
    private Date createDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "实裁量")
    private BigDecimal isCaichuangQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "排产批次号")
    private String paichanBatch;

}
