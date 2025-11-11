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
 * 预留库存对象 s_inv_reserve_inventory
 *
 * @author linhongwei
 * @date 2022-04-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_reserve_inventory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvReserveInventory extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-预留库存信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-预留库存信息")
    private Long reserveStockSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] reserveStockSidList;
    /**
     * 系统SID-物料&商品&服务
     */
    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    /**
     * 系统SID-物料&商品sku1
     */
    @Excel(name = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;

    /**
     * 系统SID-物料&商品sku2
     */
    @Excel(name = "系统SID-物料&商品sku2")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    /**
     * 系统SID-商品条码（物料&商品&服务）
     */
    @Excel(name = "系统SID-商品条码（物料&商品&服务）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-商品条码（物料&商品&服务）")
    private Long barcodeSid;

    /**
     * 系统SID-仓库档案
     */
    @Excel(name = "系统SID-仓库档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    /**
     * 系统SID-库位
     */
    @Excel(name = "系统SID-库位")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    /**
     * 预留类型（数据字典的键值或配置档案的编码）：销售订单、销售发货单、采购退货交货单、采购退货订单、领料单、调拨单、发货单、生产订单原材料
     */
    @Excel(name = "预留类型（数据字典的键值或配置档案的编码）：销售订单、销售发货单、采购退货交货单、采购退货订单、领料单、调拨单、发货单、生产订单原材料")
    @ApiModelProperty(value = "预留类型（数据字典的键值或配置档案的编码）：销售订单、销售发货单、采购退货交货单、采购退货订单、领料单、调拨单、发货单、生产订单原材料")
    private String reserveType;

    /**
     * 预留库存量
     */
    @Excel(name = "预留库存量")
    @ApiModelProperty(value = "预留库存量")
    private BigDecimal quantity;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 系统SID-对应业务单
     */
    @Excel(name = "系统SID-对应业务单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-对应业务单")
    private Long businessOrderSid;

    /**
     * 对应业务单号
     */
    @Excel(name = "对应业务单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "对应业务单号")
    private Long businessOrderCode;

    /**
     * 对应业务单明细行sid
     */
    @Excel(name = "对应业务单明细行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "对应业务单明细行sid")
    private Long businessOrderItemSid;

    /**
     * 对应业务单明细行号
     */
    @Excel(name = "对应业务单明细行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "对应业务单明细行号")
    private Long businessOrderItemNum;

    /**
     * 系统SID-领退料单
     */
    @Excel(name = "系统SID-领退料单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-领退料单")
    private Long materialRequisitionSid;

    /**
     * 领退料单号
     */
    @Excel(name = "领退料单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "领退料单号")
    private Long materialRequisitionCode;

    /**
     * 领料单明细行sid
     */
    @Excel(name = "领料单明细行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "领料单明细行sid")
    private Long materialRequisitionItemSid;

    /**
     * 领料单明细行号
     */
    @Excel(name = "领料单明细行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "领料单明细行号")
    private Long materialRequisitionItemNum;

    /**
     * 系统SID-采购交货单/销售发货单
     */
    @Excel(name = "系统SID-采购交货单/销售发货单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购交货单/销售发货单")
    private Long deliveryNoteSid;

    /**
     * 采购交货单/销售发货单号
     */
    @Excel(name = "采购交货单/销售发货单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单号")
    private Long deliveryNoteCode;

    /**
     * 采购交货单/销售发货单行sid
     */
    @Excel(name = "采购交货单/销售发货单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单行sid")
    private Long deliveryNoteItemSid;

    /**
     * 采购交货单/销售发货单行号
     */
    @Excel(name = "采购交货单/销售发货单行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购交货单/销售发货单行号")
    private Long deliveryNoteItemNum;

    /**
     * 系统SID-销售订单
     */
    @Excel(name = "系统SID-销售订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-销售订单")
    private Long salesOrderSid;

    /**
     * 销售订单号
     */
    @Excel(name = "销售订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderCode;

    /**
     * 销售订单行sid
     */
    @Excel(name = "销售订单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单行sid")
    private Long salesOrderItemSid;

    /**
     * 销售订单行号
     */
    @Excel(name = "销售订单行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单行号")
    private Long salesOrderItemNum;

    /**
     * 系统SID-采购退货订单
     */
    @Excel(name = "系统SID-采购退货订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购退货订单")
    private Long purchaseOrderSid;

    /**
     * 采购退货订单号
     */
    @Excel(name = "采购退货订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购退货订单号")
    private Long purchaseOrderCode;

    /**
     * 采购退货订单行sid
     */
    @Excel(name = "采购退货订单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购退货订单行sid")
    private Long purchaseOrderItemSid;

    /**
     * 采购退货订单行号
     */
    @Excel(name = "采购退货订单行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购退货订单行号")
    private Long purchaseOrderItemNum;

    /**
     * 系统SID-调拨单
     */
    @Excel(name = "系统SID-调拨单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-调拨单")
    private Long inventoryTransferSid;

    /**
     * 调拨单号
     */
    @Excel(name = "调拨单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单号")
    private Long inventoryTransferCode;

    /**
     * 调拨单行sid
     */
    @Excel(name = "调拨单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单行sid")
    private Long inventoryTransferItemSid;

    /**
     * 调拨单行号
     */
    @Excel(name = "调拨单行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单行号")
    private Long inventoryTransferItemNum;

    /**
     * 系统SID-发货单
     */
    @Excel(name = "系统SID-发货单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-发货单")
    private Long goodIssueNoteSid;

    /**
     * 发货单号
     */
    @Excel(name = "发货单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发货单号")
    private Long goodIssueNoteCode;

    /**
     * 发货单行sid
     */
    @Excel(name = "发货单行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发货单行sid")
    private Long goodIssueNoteItemSid;

    /**
     * 发货单行号
     */
    @Excel(name = "发货单行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发货单行号")
    private Long goodIssueNoteItemNum;

    /**
     * 系统SID-生产订单
     */
    @Excel(name = "系统SID-生产订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-生产订单")
    private Long manufactureOrderSid;

    /**
     * 生产订单号
     */
    @Excel(name = "生产订单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "生产订单号")
    private Long manufactureOrderCode;

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
     * 确认人账号（用户账号）
     */
    @Excel(name = "确认人账号（用户账号）")
    @ApiModelProperty(value = "确认人账号（用户账号）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "数据源系统（数据字典的键值或配置档案的编码）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值或配置档案的编码）")
    private String dataSourceSys;


    @TableField(exist = false)
    private String creatorAccountName;

    @TableField(exist = false)
    private String updaterAccountName;

    @TableField(exist = false)
    private String confirmerAccountName;

    @Excel(name = "特殊库存（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    private String specialStock;

    /**
     * 特殊库存供应商sid
     */
    @Excel(name = "特殊库存供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存供应商sid")
    private Long vendorSid;

    /**
     * 特殊库存客户sid
     */
    @Excel(name = "特殊库存客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "特殊库存客户sid")
    private Long customerSid;

}
