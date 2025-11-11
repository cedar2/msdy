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

import lombok.experimental.Accessors;

/**
 * 采购到货台账-明细对象 s_inv_record_goods_arrival_item
 *
 * @author linhongwei
 * @date 2022-06-27
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_record_goods_arrival_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvRecordGoodsArrivalItem extends EmsBaseEntity {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-采购到货台账明细
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购到货台账明细")
    private Long goodsArrivalItemSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] goodsArrivalItemSidList;
    /**
     * 系统SID-采购到货台账
     */
    @Excel(name = "系统SID-采购到货台账")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购到货台账")
    private Long goodsArrivalSid;

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
     * 本次到货量（价格单位对应的数量）
     */
    @Excel(name = "本次到货量（价格单位对应的数量）")
    @ApiModelProperty(value = "本次到货量（价格单位对应的数量）")
    private BigDecimal arrivalQuantity;

    /**
     * 采购价单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "采购价单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "采购价单位（数据字典的键值或配置档案的编码）")
    private String unitPrice;

    @ApiModelProperty(value = "采购价单位")
    @TableField(exist = false)
    private String unitPriceName;

    /**
     * 基本计量单位（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "基本计量单位（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值或配置档案的编码）")
    private String unitBase;

    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    /**
     * 单位换算比例（价格单位/基本单位）
     */
    @Excel(name = "单位换算比例（价格单位/基本单位）")
    @ApiModelProperty(value = "单位换算比例（价格单位/基本单位）")
    private BigDecimal unitConversionRate;

    /**
     * 检测状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "检测状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "检测状态（数据字典的键值或配置档案的编码）")
    private String checkStatus;

    /**
     * 检测单号
     */
    @Excel(name = "检测单号")
    @ApiModelProperty(value = "检测单号")
    private String checkDocumentCode;

    /**
     * 检测结果（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "检测结果（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "检测结果（数据字典的键值或配置档案的编码）")
    private String checkResult;

    /**
     * 供应商退货的联系状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "供应商退货的联系状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "供应商退货的联系状态（数据字典的键值或配置档案的编码）")
    private String vendorContactStatus;

    /**
     * 供应商退货的取货状态（数据字典的键值或配置档案的编码）
     */
    @Excel(name = "供应商退货的取货状态（数据字典的键值或配置档案的编码）")
    @ApiModelProperty(value = "供应商退货的取货状态（数据字典的键值或配置档案的编码）")
    private String vendorPickupStatus;

    /**
     * 行号
     */
    @Excel(name = "行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    /**
     * 系统SID-采购订单
     */
    @Excel(name = "系统SID-采购订单")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单")
    private Long purchaseOrderSid;

    /**
     * 采购订单号code
     */
    @Excel(name = "采购订单号code")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号code")
    private Long purchaseOrderCode;

    /**
     * 系统SID-公司档案
     */
    @Excel(name = "系统SID-公司档案")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-公司档案")
    private Long companySid;

    /**
     * 系统SID-采购订单明细
     */
    @Excel(name = "系统SID-采购订单明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-采购订单明细")
    private Long purchaseOrderItemSid;

    /**
     * 采购订单明细行号
     */
    @Excel(name = "采购订单明细行号")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细行号")
    private Long purchaseOrderItemNum;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "采购到货台账-缸号明细对象")
    private List<InvRecordGoodsArrivalDetail> itemDetailList;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @ApiModelProperty(value = "物料（商品/服务）名称")
    @TableField(exist = false)
    private String materialName;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Name;

    @Excel(name = "sku1名称")
    @ApiModelProperty(value = "sku1名称")
    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku2Code;

    @Excel(name = "sku2名称")
    @ApiModelProperty(value = "sku2名称")
    @TableField(exist = false)
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "下单季")
    private String productSeasonName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

}
