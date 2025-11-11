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

import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 物料清单（BOM）组件清单对象 s_tec_bom_item
 *
 * @author qhq
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomItemReport implements Serializable {

    @Excel(name = "系统ID-物料BOM组件明细")
    @ApiModelProperty(value = "系统ID-物料BOM组件明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long bomItemSid;

    /**
     * 系统ID-物料BOM档案
     */
    @ApiModelProperty(value = "系统ID-物料BOM档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomSid;


    @Excel(name = "单件用量(不含损耗率)")
    @ApiModelProperty(value = "单件用量(不含损耗率)")
    private BigDecimal quantity;

    @Excel(name = "损耗率（存值，不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "损耗率（存值，不含百分号，如20%，就存0.2）")
    private BigDecimal lossRate;

    @Excel(name = "单件用量(含损耗率)")
    @ApiModelProperty(value = "单件用量(含损耗率)")
    private BigDecimal quantityLossRate;
    /**
     * BOM组件物料编码
     */
    @ApiModelProperty(value = "BOM组件物料编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSid;

    /**
     * BOM组件物料SKU1档案
     */
    @Excel(name = "BOM组件物料SKU1档案")
    @ApiModelProperty(value = "BOM组件物料SKU1档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku1Sid;

    /**
     * BOM组件物料SKU2档案
     */
    @Excel(name = "BOM组件物料SKU2档案")
    @ApiModelProperty(value = "BOM组件物料SKU2档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku2Sid;

    /** 内部用量(不含损耗) */
    @Excel(name = "内部用量")
    @ApiModelProperty(value = "内部用量(不含损耗)")
    private BigDecimal innerQuantity;

    /** 内部用量 */
    @TableField(exist = false)
    @Excel(name = "内部用量(含损耗)")
    @ApiModelProperty(value = "内部用量(含损耗)")
    private BigDecimal lossInnerQuantity;

    /**
     * 确认用量
     */
    @Excel(name = "确认用量（不含损耗）")
    @ApiModelProperty(value = "确认用量（不含损耗）")
    private BigDecimal confirmQuantity;

    /**
     * 确认用量
     */
    @TableField(exist = false)
    @Excel(name = "用量（含损耗）")
    @ApiModelProperty(value = "用量（含损耗）")
    private BigDecimal lossConfirmQuantity;

    /**
     * 确认损耗率（%）
     */
    @Excel(name = "确认损耗率（%）")
    @ApiModelProperty(value = "确认损耗率（%）")
    private BigDecimal confirmLossRate;

    /**
     * 基本计量单位编码
     */
    @Excel(name = "基本计量单位编码")
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    private String remark;

    /**
     * 需求量(含损耗)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(含损耗)")
    private String lossRequireQuantity;

    /**
     * 需求量(不含损耗)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(不含损耗)")
    private String requireQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(含损耗)")
    private String lossRequireQuantityView;

    /**
     * 需求量(不含损耗)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(不含损耗)")
    private String requireQuantityView;

    /**
     * 采购类型编码（默认）
     */
    @TableField(exist = false)
    @Excel(name = "采购类型编码（默认）", dictType = "s_purchase_type")
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    /**
     * 供应商编码（默认）
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码（默认）")
    private Long vendorSid;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供方编码（物料/商品/服务）
     */
    @TableField(exist = false)
    @Excel(name = "供方编码（物料/商品/服务）")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    /**
     * 物料（商品/服务）分类编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassSid;

    /**
     * 物料sid
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    /**
     * 物料（商品/服务）名称
     */
    @TableField(exist = false)
    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /** SKU1sid */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1sid")
    private Long sku1Sid;

    /** SKU2sid */
    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2sid")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的code")
    private String sku1Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的name")
    private String sku1Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的code")
    private String sku2Code;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2的name")
    private String sku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "单位换算比例(基本计量单位/用量)")
    private String unitConversionRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "幅宽")
    private String width;

    @TableField(exist = false)
    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @TableField(exist = false)
    @ApiModelProperty(value = "成分")
    private String composition;

    @TableField(exist = false)
    @ApiModelProperty(value = "纱支")
    private String yarnCount;

    @TableField(exist = false)
    @ApiModelProperty(value = "密度")
    private String density;

    @TableField(exist = false)
    @ApiModelProperty(value = "规格尺寸")
    private String specificationSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "可用库存量")
    private BigDecimal unlimitedQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "bom用量计量单位")
    private String unitQuantityName;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;

    @Excel(name = "商品")
    @ApiModelProperty(value = "销售订单商品名称")
    private String saleMaterialName;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "销售订单商品编码")
    private String saleMaterialCode;

    @Excel(name = "商品sku1")
    @ApiModelProperty(value = "销售订单商品sku1名称")
    private String saleSku1Name;

    @ApiModelProperty(value = "款号sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleMaterialSid;

    @ApiModelProperty(value = "款sku1sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleSku1Sid;

    @ApiModelProperty(value = "款sku2sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleSku2Sid;

    @Excel(name = "商品sku2")
    @ApiModelProperty(value = "销售订单商品sku2名称")
    private String saleSku2Name;

    @TableField(exist = false)
    @ApiModelProperty(value = "款备注")
    private String materialCodeRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "款颜色")
    private String materialSkuRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "款尺码")
    private String materialSku2Remark;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号备注")
    private String manufactureOrderCodeRemark;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号备注")
    private String purchaseOrderCodeRemark;

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号备注")
    private String salesOrderCodeRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    @Excel(name = "通用code")
    @ApiModelProperty(value = "通用code")
    @TableField(exist = false)
    private String commonCode;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long manufactureOrderCode;

    @ApiModelProperty(value = "采购订单号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long purchaseOrderCode;

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long salesOrderCode;

    @Excel(name = "通用sid")
    @ApiModelProperty(value = "通用sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commonSid;

    @Excel(name = "通用明细行sid")
    @ApiModelProperty(value = "通用明细行sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commonItemSid;

    @Excel(name = "通用行号")
    @ApiModelProperty(value = "通用行号")
    @TableField(exist = false)
    private Long commonItemNum;

    @ApiModelProperty(value = "款数量")
    @TableField(exist = false)
    private BigDecimal productQuantity;

    @ApiModelProperty(value = "行号sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private String commonItemSidRemark;

    @ApiModelProperty(value = "订单明细行的需求量")
    @TableField(exist = false)
    private HashMap<String,BigDecimal> quantityMap=new HashMap<>();
}
