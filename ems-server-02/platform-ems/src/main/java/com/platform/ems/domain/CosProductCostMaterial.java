package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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


/**
 * 商品成本核算-物料成本明细对象 s_cos_product_cost_material
 *
 * @author qhq
 * @date 2021-04-25
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_cos_product_cost_material")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CosProductCostMaterial extends EmsBaseEntity {
    /** 客户端口号 */
    @Excel(name = "客户端口号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统SID-成品/半成品成本核算物料明细 */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-成品/半成品成本核算物料明细")
    private Long productCostMaterialSid;

    /** 商品成本BOM sid */
    @Excel(name = "商品成本BOM sid")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品成本BOM sid")
    private Long productCostBomSid;

    /** 系统SID-物料BOM组件明细 */
    @Excel(name = "系统SID-物料BOM组件明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料BOM组件明细")
    private Long bomItemSid;

    /** BOM组件物料编码（物料档案sid） */
    @Excel(name = "BOM组件物料编码（物料档案sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "BOM组件物料编码（物料档案sid）")
    private Long bomMaterialSid;

    /** 是否主面料 */
    @Excel(name = "是否主面料")
    @ApiModelProperty(value = "是否主面料")
    private String isMainFabric;

    /** BOM组件物料SKU1档案（SKU sid） */
    @Excel(name = "BOM组件物料SKU1档案（SKU sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "BOM组件物料SKU1档案（SKU sid）")
    private Long bomMaterialSku1Sid;

    /** BOM组件物料SKU2档案（SKU sid） */
    @Excel(name = "BOM组件物料SKU2档案（SKU sid）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "BOM组件物料SKU2档案（SKU sid）")
    private Long bomMaterialSku2Sid;

    /** 部位编码 */
    @Excel(name = "部位编码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "部位编码")
    private Long positionSid;

    /** 部位名称 */
    @Excel(name = "部位名称")
    @ApiModelProperty(value = "部位名称")
    private String positionName;

    /** 内部用量 */
    @Excel(name = "内部用量")
    @ApiModelProperty(value = "内部用量")
    private BigDecimal innerQuantity;

    /** 内部损耗率（%） */
    @Excel(name = "内部损耗率（%）")
    @ApiModelProperty(value = "内部损耗率（%）")
    private BigDecimal innerLossRate;

    /** 报价用量 */
    @Excel(name = "报价用量")
    @ApiModelProperty(value = "报价用量")
    private BigDecimal quoteQuantity;

    /** 报价损耗率（%） */
    @Excel(name = "报价损耗率（%）")
    @ApiModelProperty(value = "报价损耗率（%）")
    private BigDecimal quoteLossRate;

    /** 核价用量 */
    @Excel(name = "核价用量")
    @ApiModelProperty(value = "核价用量")
    private BigDecimal checkQuantity;

    /** 核价损耗率（%） */
    @Excel(name = "核价损耗率（%）")
    @ApiModelProperty(value = "核价损耗率（%）")
    private BigDecimal checkLossRate;

    /** 确认用量 */
    @Excel(name = "确认用量")
    @ApiModelProperty(value = "确认用量")
    private BigDecimal confirmQuantity;

    /** 确认损耗率（%） */
    @Excel(name = "确认损耗率（%）")
    @ApiModelProperty(value = "确认损耗率（%）")
    private BigDecimal confirmLossRate;

    /** 基本计量单位（BOM组件物料）（数据字典的键值） */
    @Excel(name = "基本计量单位（BOM组件物料）（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（BOM组件物料）（数据字典的键值）")
    private String unitBase;

    /** 取整方式（损耗） */
    @Excel(name = "取整方式（损耗）")
    @ApiModelProperty(value = "取整方式（损耗）")
    private String roundingType;

    /** 计价量 */
    @Excel(name = "计价量")
    @ApiModelProperty(value = "计价量")
    private BigDecimal priceQuantity;

    /** 计价量计量单位编码 */
    @Excel(name = "计价量计量单位编码")
    @ApiModelProperty(value = "计价量计量单位编码")
    private String unitPrice;

    /** 内部量备注 */
    @Excel(name = "内部量备注")
    @ApiModelProperty(value = "内部量备注")
    private String remarkInnerQuantity;

    /** 用量计量单位（数据字典的键值） */
    @Excel(name = "用量计量单位（数据字典的键值）")
    @ApiModelProperty(value = "用量计量单位（数据字典的键值）")
    private String unitQuantity;

    /** 报价量备注 */
    @Excel(name = "报价量备注")
    @ApiModelProperty(value = "报价量备注")
    private String remarkQuoteQuantity;

    /** 核价量备注 */
    @Excel(name = "核价量备注")
    @ApiModelProperty(value = "核价量备注")
    private String remarkCheckQuantity;

    /** 确认价量备注 */
    @Excel(name = "确认价量备注")
    @ApiModelProperty(value = "确认价量备注")
    private String remarkConfirmQuantity;

    /** BOM组件物料名称 */
    @Excel(name = "BOM组件物料名称")
    @ApiModelProperty(value = "BOM组件物料名称")
    private String bomMaterialName;

    /** 采购类型（默认）（数据字典的键值） */
    @Excel(name = "采购类型（默认）（数据字典的键值）")
    @ApiModelProperty(value = "采购类型（默认）（数据字典的键值）")
    private String purchaseType;

    /** 内部价(含税) */
    @Excel(name = "内部价(含税)")
    @ApiModelProperty(value = "内部价(含税)")
    private BigDecimal innerPriceTax;

    /** 内部价(不含税) */
    @Excel(name = "内部价(不含税)")
    @ApiModelProperty(value = "内部价(不含税)")
    private BigDecimal innerPrice;

    /** 报价(含税) */
    @Excel(name = "报价(含税)")
    @ApiModelProperty(value = "报价(含税)")
    private BigDecimal quotePriceTax;

    /** 报价(不含税) */
    @Excel(name = "报价(不含税)")
    @ApiModelProperty(value = "报价(不含税)")
    private BigDecimal quotePrice;

    /** 核定价(含税) */
    @Excel(name = "核定价(含税)")
    @ApiModelProperty(value = "核定价(含税)")
    private BigDecimal checkPriceTax;

    /** 核定价(不含税) */
    @Excel(name = "核定价(不含税)")
    @ApiModelProperty(value = "核定价(不含税)")
    private BigDecimal checkPrice;

    /** 确认价(含税) */
    @Excel(name = "确认价(含税)")
    @ApiModelProperty(value = "确认价(含税)")
    private BigDecimal confirmPriceTax;

    /** 确认价(不含税) */
    @Excel(name = "确认价(不含税)")
    @ApiModelProperty(value = "确认价(不含税)")
    private BigDecimal confirmPrice;

    /** 备注（内部价） */
    @Excel(name = "备注（内部价）")
    @ApiModelProperty(value = "备注（内部价）")
    private String remarkInner;

    /** 备注（报价） */
    @Excel(name = "备注（报价）")
    @ApiModelProperty(value = "备注（报价）")
    private String remarkQuote;

    /** 备注（核定价） */
    @Excel(name = "备注（核定价）")
    @ApiModelProperty(value = "备注（核定价）")
    private String remarkCheck;

    /** 备注（确认价） */
    @Excel(name = "备注（确认价）")
    @ApiModelProperty(value = "备注（确认价）")
    private String remarkConfirm;

    /** 创建人账号（用户名称） */
    @Excel(name = "创建人账号（用户名称）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号（用户名称）")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号（用户名称） */
    @Excel(name = "更新人账号（用户名称）")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号（用户名称）")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统（数据字典的键值） */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建日期开始时间")
    @TableField(exist = false)
    private String beginTime;

    @ApiModelProperty(value = "创建日期结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "页数")
    @TableField(exist = false)
    private Integer pageNum;

    @ApiModelProperty(value = "每页个数")
    @TableField(exist = false)
    private Integer pageSize;

    @ApiModelProperty(value = "物料档案")
    @TableField(exist = false)
    private BasMaterial material;

    @TableField(exist = false)
    @ApiModelProperty(value = "拉链标识")
    private String  zipperFlag;

    @Excel(name = "用量")
    @ApiModelProperty(value = "用量/订单数量（报表）")
    private BigDecimal quantity;

    @Excel(name = "损耗率（存值，不含百分号，如20%，就存0.2）")
    @ApiModelProperty(value = "损耗率（存值，不含百分号，如20%，就存0.2）")
    private BigDecimal lossRate;

    @Excel(name = "价格")
    @ApiModelProperty(value = "价格")
    private BigDecimal priceTax;

    @Excel(name = "价格(不含税)")
    @ApiModelProperty(value = "价格")
    private BigDecimal price ;

    @ApiModelProperty(value = "单位换算比例(基本计量单位/用量)")
    private String unitConversionRate;

    @ApiModelProperty(value = "单位换算比例（采购价单位/基本单位）")
    private BigDecimal unitConversionRatePrice;

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
    @ApiModelProperty(value = "启停用状态")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "bom用量计量单位")
    private String unitQuantityName;

    @TableField(exist = false)
    @ApiModelProperty(value = "计价单位")
    private String unitPriceName;

    @TableField(exist = false)
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;


    /** 供应商编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /** 供方编码（物料/商品/服务） */
    @TableField(exist = false)
    @Excel(name = "供方编码（物料/商品/服务）")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1的name")
    private String sku1Name;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @ApiModelProperty(value = "递增减计量单位")
    private String unitRecursion;

    @ApiModelProperty(value = "递增减计量单位")
    @TableField(exist = false)
    private String unitRecursionName;

}
