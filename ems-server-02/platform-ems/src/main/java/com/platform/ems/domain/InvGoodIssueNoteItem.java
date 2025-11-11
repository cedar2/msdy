package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.platform.common.core.domain.document.UserOperLog;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.experimental.Accessors;

/**
 * 发货单-明细对象 s_inv_good_issue_note_item
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_good_issue_note_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvGoodIssueNoteItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-发货单明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-发货单明细")
    private Long noteItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] goodIssueNoteItemSidList;
    /**
     * 系统SID-发货单
     */
    @Excel(name = "系统SID-发货单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-发货单")
    private Long noteSid;

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
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    /**
     * 数量
     */
    @Excel(name = "数量")
    @ApiModelProperty(value = "数量")
    private BigDecimal quantity;

    /** 系统SID-仓库档案 */
    @NotNull(message = "明细仓库不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库档案")
    private Long storehouseSid;

    @ApiModelProperty(value = "系统SID-仓库档案")
    private String storehouseCode;

    /** 系统SID-库位 */
    @NotNull(message = "明细库位不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-库位")
    private Long storehouseLocationSid;

    @ApiModelProperty(value = "系统SID-库位")
    private String storehouseLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-库位")
    private String locationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "仓库档案")
    private String storehouseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位档案")
    private String storehouseLocationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位档案")
    private String locationName;

    /**
     * 需求日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "需求日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "需求日期")
    private Date demandDate;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /**
     * 创建人账号（用户名称）
     */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
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
     * 更新人账号（用户名称）
     */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
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

    @TableField(exist = false)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Code;

    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "创建人账号（用户名称）")
    @TableField(exist = false)
    private String creatorAccountName;

    @ApiModelProperty(value = "商品条码")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "启停状态")
    private String status;

    @ApiModelProperty(value = "出入库状态")
    private String inOutStockStatus;

    @ApiModelProperty(value = "款号名称")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "款号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSid;

    @ApiModelProperty(value = "商品条码sid(适用于物料对应的商品)；只能保存单个")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productBarcodeSid;

    @ApiModelProperty(value = "款号code")
    private String productCode;

    @ApiModelProperty(value = "款颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款颜色")
    private String productSku1Name;

    @ApiModelProperty(value = "款颜色code")
    private String productSku1Code;

    @ApiModelProperty(value = "款尺码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款尺码")
    private String productSku2Name;

    @ApiModelProperty(value = "款尺码")
    private String productSku2Code;

    @ApiModelProperty(value = "款备注")
    private String productCodes;

    @ApiModelProperty(value = "款颜色备注")
    private String productSku1Names;

    @ApiModelProperty(value = "款尺码备注")
    private String productSku2Names;

    @ApiModelProperty(value = "采购订单号备注")
    private String productPoCodes;

    /** 采购订单sid（如是原材料采购，此处存放对应的成品采购订单） */
    @Excel(name = "采购订单sid（如是原材料采购，此处存放对应的成品采购订单）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid（如是原材料采购，此处存放对应的成品采购订单）")
    private Long referPurchaseOrderSid;

    /** 采购订单明细行sid */
    @Excel(name = "采购订单明细行sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细行sid")
    private Long referPurchaseOrderItemSid;

    /** 采购订单号code（如是原材料采购，此处存放对应的成品采购订单） */
    @Excel(name = "采购订单号code（如是原材料采购，此处存放对应的成品采购订单）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号code（如是原材料采购，此处存放对应的成品采购订单）")
    private Long referPurchaseOrderCode;

    /** 采购订单明细行号 */
    @Excel(name = "采购订单明细行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细行号")
    private Long referPurchaseOrderItemNum;

    @ApiModelProperty(value = "款采购订单号")
    @TableField(exist = false)
    private String purchaseOrderCode;

    @ApiModelProperty(value = "款数量")
    private BigDecimal productQuantity;

    @ApiModelProperty(value = "库存预留状态")
    private String reserveStatus;

    @ApiModelProperty(value = "供应商sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long vendorSid;

    @ApiModelProperty(value = "客户sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long customerSid;

    @Excel(name = "特殊库存（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "特殊库存（数据字典的键值或配置档案的编码）")
    @TableField(exist = false)
    private String specialStock;

    @ApiModelProperty(value = "系统SID-发货单号")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private String noteCode;

    @TableField(exist = false)
    private String firstSort;

    @TableField(exist = false)
    private String secondSort;

    @TableField(exist = false)
    private String thirdSort;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;
}
