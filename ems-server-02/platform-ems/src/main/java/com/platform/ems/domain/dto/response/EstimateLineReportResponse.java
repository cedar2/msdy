package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 物料需求测算线用量
 */
@Data
@ApiModel
@Accessors(chain = true)
public class EstimateLineReportResponse implements Serializable {

    @Excel(name = "单件用量(不含损耗率)")
    @ApiModelProperty(value = "单件用量(不含损耗率)")
    private BigDecimal quantity;

    @Excel(name = "损耗率（存值，不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "损耗率（存值，不含百分号，如20%，就存0.2）")
    private BigDecimal lossRate;

    @Excel(name = "基本计量单位编码")
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;

    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(含损耗)")
    private BigDecimal lossRequireQuantity;

    @ApiModelProperty(value = "单间用量含损耗率")
    private BigDecimal quantityLossRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(不含损耗)")
    private String requireQuantityView;


    @TableField(exist = false)
    @Excel(name = "采购类型编码（默认）", dictType = "s_purchase_type")
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;


    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码（默认）")
    private Long vendorSid;

    @Excel(name = "供应商名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @ApiModelProperty(value = "是否是整数型计量单位")
    @TableField(exist = false)
    private String isInteger;

    @TableField(exist = false)
    @ApiModelProperty(value = "拉链标识")
    private String  zipperFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    @TableField(exist = false)
    @Excel(name = "供方编码（物料/商品/服务）")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;


    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassSid;


    @TableField(exist = false)
    @ApiModelProperty(value = "物料sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "物料编码")
    @TableField(exist = false)
    private String materialCode;

    @TableField(exist = false)
    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;


    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1sid")
    private Long sku1Sid;


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
    private int unlimitedQuantity;

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

    @ApiModelProperty(value = "物料颜色sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSku1Sid;

    @ApiModelProperty(value = "物料颜色名称")
    @JsonSerialize(using = ToStringSerializer.class)
    private String materialSku1Name;

    @ApiModelProperty(value = "物料颜色编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private String materialSku1Code;


    @ApiModelProperty(value = "物料sku2sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSku2Sid;

    @ApiModelProperty(value = "物料sku2名称")
    @JsonSerialize(using = ToStringSerializer.class)
    private String materialSku2Name;

    @ApiModelProperty(value = "BOM组件物料编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSid;

    @ApiModelProperty(value = "BOM组件物料SKU1档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku1Sid;

    @ApiModelProperty(value = "BOM组件物料SKU2档案")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku2Sid;

    @TableField(exist = false)
    private String handleStatus;
}
