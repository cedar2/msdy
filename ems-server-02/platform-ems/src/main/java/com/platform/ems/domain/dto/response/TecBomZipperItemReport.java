package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物料清单（BOM）组件清单对象-拉链 s_tec_bom_item
 *
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecBomZipperItemReport implements Serializable{

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
    @Excel(name = "采购类型编码（默认）", dictType = "s_purchase_type")
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    /**
     * 供应商编码（默认）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码（默认）")
    private Long vendorSid;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 供方编码（物料/商品/服务）
     */
    @Excel(name = "供方编码（物料/商品/服务）")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    /**
     * 物料（商品/服务）分类编码
     */
    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassSid;

    /**
     * 物料sid
     */
    @ApiModelProperty(value = "物料sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "物料名称")
    private String BomMaterialName;

    @Excel(name = "商品sku1")
    @ApiModelProperty(value = "商品sku1")
    private String BomSku1Name;

    @Excel(name = "商品sku2")
    @ApiModelProperty(value = "商品sku2")
    private String BomSku2Name;

    /**
     * 物料（商品/服务）名称
     */
    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @ApiModelProperty(value = "SKU1的code")
    private String sku1Code;

    @ApiModelProperty(value = "SKU1的name")
    private String sku1Name;

    @ApiModelProperty(value = "SKU2的code")
    private String sku2Code;

    @ApiModelProperty(value = "SKU2的name")
    private String sku2Name;

    @ApiModelProperty(value = "单位换算比例(基本计量单位/用量)")
    private String unitConversionRate;

    @ApiModelProperty(value = "幅宽")
    private String width;

    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @ApiModelProperty(value = "成分")
    private String composition;

    @ApiModelProperty(value = "纱支")
    private String yarnCount;

    @ApiModelProperty(value = "密度")
    private String density;

    @ApiModelProperty(value = "规格尺寸")
    private String specificationSize;

    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @ApiModelProperty(value = "可用库存量")
    private int unlimitedQuantity;

    @ApiModelProperty(value = "bom用量计量单位")
    private String unitQuantityName;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;

    @TableField(exist = false)
    @Excel(name = "商品")
    @ApiModelProperty(value = "销售订单商品名称")
    private String saleMaterialName;

    @TableField(exist = false)
    @Excel(name = "商品sku1")
    @ApiModelProperty(value = "销售订单商品sku1名称")
    private String saleSku1Name;

    @TableField(exist = false)
    @Excel(name = "商品sku2")
    @ApiModelProperty(value = "销售订单商品sku2名称")
    private String saleSku2Name;

    @TableField(exist = false)
    @Excel(name = "商品编码")
    @ApiModelProperty(value = "销售订单商品编码")
    private String saleMaterialCode;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号备注")
    private String manufactureOrderCodeRemark;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号备注")
    private String purchaseOrderCodeRemark;

    @ApiModelProperty(value = "销售订单号备注")
    private String salesOrderCodeRemark;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "款号sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private String saleMaterialSid;

    @ApiModelProperty(value = "款sku1sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleSku1Sid;

    @ApiModelProperty(value = "款sku2sid")
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleSku2Sid;

    @Excel(name = "通用code")
    @ApiModelProperty(value = "通用code")
    @TableField(exist = false)
    private String commonCode;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    @TableField(exist = false)
    private String manufactureOrderCode;

    @ApiModelProperty(value = "采购订单号")
    @TableField(exist = false)
    private String purchaseOrderCode;

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    @TableField(exist = false)
    private String salesOrderCode;
}
