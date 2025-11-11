package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.experimental.Accessors;

/**
 * 采购订单-数据来源明细对象 s_pur_purchase_order_data_source
 *
 * @author chenkw
 * @date 2023-05-09
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_pur_purchase_order_data_source")
public class PurPurchaseOrderDataSource extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-采购订单-数据来源明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单-数据来源明细")
    private Long purchaseOrderDataSourceSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] purchaseOrderDataSourceSidList;

    /**
     * 系统SID-采购订单
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 系统SID-采购订单 多选
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-采购订单 多选")
    private Long[] purchaseOrderSidList;

    /**
     * 采购订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    /**
     * 系统SID-采购订单明细
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单明细")
    private Long purchaseOrderItemSid;

    /**
     * 系统SID-采购订单明细 (多选)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-采购订单明细 (多选)")
    private Long[] purchaseOrderItemSidList;

    /**
     * 采购订单明细行号
     */
    @ApiModelProperty(value = "采购订单明细行号")
    private Integer purchaseOrderItemNum;

    /**
     * 来源单据sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据sid")
    private Long referDocSid;

    /**
     * 来源单据单号code
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据单号code")
    private Long referDocCode;

    /**
     * 来源单据行sid
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "来源单据行sid")
    private Long referDocItemSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "来源单据行sid")
    private Long[] referDocItemSidList;

    /**
     * 来源单据行号
     */
    @ApiModelProperty(value = "来源单据行号")
    private Integer referDocItemNum;

    /**
     * 计量单位编码
     */
    @ApiModelProperty(value = "计量单位编码")
    private String unitBase;

    /**
     * 计量单位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位")
    private String unitBaseName;

    /**
     * 本次下单量
     */
    @ApiModelProperty(value = "本次下单量")
    private BigDecimal quantity;

    /**
     * 本次下单量(变更中)
     */
    @ApiModelProperty(value = "本次下单量(变更中)")
    private BigDecimal newQuantity;

    /**
     * 已下单量（不含本单） refer单据的和-该行数量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "已下单量")
    private BigDecimal quantityReferOtherSum;

    /**
     * 创建人账号（用户名称）
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人昵称")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号（用户名称）
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    @TableField(exist = false)
    @ApiModelProperty(value = "更改人昵称")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    /**
     * 采购申请单明细信息
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购申请单明细信息")
    private ReqPurchaseRequireItem purchaseRequireItem;

    /**
     * 采购订单的处理状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单的处理状态")
    private String handleStatus;

    /**
     * 采购订单明细的数据来源类别编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单明细的数据来源类别编码")
    private String referDocCategory;

    /**
     * 采购订单明细的数据来源类别
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "采购订单明细的数据来源类别")
    private String referDocCategoryName;

}
