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
 * 系统SID-采购订单明细的交货计划明细对象 s_pur_purchase_order_delivery_plan
 *
 * @author linhongwei
 * @date 2021-11-11
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_purchase_order_delivery_plan")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurPurchaseOrderDeliveryPlan extends EmsBaseEntity{

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-销售订单明细的发货计划明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单明细的发货计划明细")
    private Long deliveryPlanSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] deliveryPlanSidList;
    /**
     * 系统SID-采购订单明细
     */
    @Excel(name = "系统SID-采购订单明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单明细")
    private Long purchaseOrderItemSid;

    /**
     * 系统SID-采购订单
     */
    @Excel(name = "系统SID-采购订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 交货日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "交货日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "交货日期")
    private Date deliveryDate;

    /**
     * 计划发货量
     */
    @Excel(name = "计划发货量")
    @ApiModelProperty(value = "计划发货量")
    private BigDecimal planQuantity;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 销售计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "销售计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "销售计量单位（数据字典的键值或配置档案的编码）")
    private String unitSale;

    /**
     * 收货方类型（数据字典的键值或配置档案的编码）客户/供应商
     */
    @Excel(name = "收货方类型（数据字典的键值或配置档案的编码）客户/供应商")
    @ApiModelProperty(value = "收货方类型（数据字典的键值或配置档案的编码）客户/供应商")
    private String receiverOrgType;

    /**
     * 收货方sid
     */
    @Excel(name = "收货方sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方sid")
    private Long receiverOrg;

    /**
     * 收货方（供应商sid）
     */
    @Excel(name = "收货方（供应商sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（供应商sid）")
    private Long vendorSid;

    /**
     * 收货方（客户sid）
     */
    @Excel(name = "收货方（客户sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（客户sid）")
    private Long customerSid;

    /**
     * 收货方（仓库sid）
     */
    @Excel(name = "收货方（仓库sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（仓库sid）")
    private Long storehouseSid;

    /**
     * 收货方（店铺sid）
     */
    @Excel(name = "收货方（店铺sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（店铺sid）")
    private Long shopSid;

    /**
     * 收货人
     */
    @Excel(name = "收货人")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    /**
     * 收货人联系电话
     */
    @Excel(name = "收货人联系电话")
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    /**
     * 收货地址
     */
    @Excel(name = "收货地址")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Long itemNum;

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

}
