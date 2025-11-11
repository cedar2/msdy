package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 物料清单（BOM）组件清单对象 s_tec_bom_item
 *
 * @author qhq
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
//@TableName(value = "s_tec_bom_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class TecBomItemReportResponse extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-物料BOM组件明细 */
    @Excel(name = "系统ID-物料BOM组件明细")
    @ApiModelProperty(value = "系统ID-物料BOM组件明细")
    @TableId
    private Long bomItemSid;

    /** 系统ID-物料BOM档案 */
    @ApiModelProperty(value = "系统ID-物料BOM档案")
    private Long bomSid;

    /** BOM组件物料编码 */
    @ApiModelProperty(value = "BOM组件物料编码")
    private Long bomMaterialSid;

//    /** 是否主面料 */
//    @Excel(name = "是否主面料")
//    @ApiModelProperty(value = "是否主面料")
//    private String isMainFabric;

    /** BOM组件物料SKU1档案 */
    @Excel(name = "BOM组件物料SKU1档案")
    @ApiModelProperty(value = "BOM组件物料SKU1档案")
    private Long bomMaterialSku1Sid;

    /** BOM组件物料SKU2档案 */
    @Excel(name = "BOM组件物料SKU2档案")
    @ApiModelProperty(value = "BOM组件物料SKU2档案")
    private Long bomMaterialSku2Sid;

//    /** 部位编码 */
//    @Excel(name = "部位编码")
//    @ApiModelProperty(value = "部位编码")
//    private String positionCode;

//    /** 部位名称 */
//    @Excel(name = "部位名称")
//    @ApiModelProperty(value = "部位名称")
//    private String positionName;
//
//    /** 内部用量(不含损耗) */
//    @Excel(name = "内部用量")
//    @ApiModelProperty(value = "内部用量(不含损耗)")
//    private BigDecimal innerQuantity;
//
//    /** 内部用量 */
//    @TableField(exist = false)
//    @Excel(name = "内部用量(含损耗)")
//    @ApiModelProperty(value = "内部用量(含损耗)")
//    private BigDecimal lossInnerQuantity;
//
//    /** 内部损耗率（%） */
//    @Excel(name = "内部损耗率（%）")
//    @ApiModelProperty(value = "内部损耗率（%）")
//    private BigDecimal innerLossRate;
//
//    /** 报价用量 */
//    @Excel(name = "报价用量")
//    @ApiModelProperty(value = "报价用量")
//    private BigDecimal quoteQuantity;
//
//    /** 报价损耗率（%） */
//    @Excel(name = "报价损耗率（%）")
//    @ApiModelProperty(value = "报价损耗率（%）")
//    private BigDecimal quoteLossRate;
//
//    /** 核价用量 */
//    @Excel(name = "核价用量")
//    @ApiModelProperty(value = "核价用量")
//    private BigDecimal checkQuantity;

//    /** 核价损耗率（%） */
//    @Excel(name = "核价损耗率（%）")
//    @ApiModelProperty(value = "核价损耗率（%）")
//    private BigDecimal checkLossRate;

    /** 确认用量 */
    @Excel(name = "确认用量（不含损耗）")
    @ApiModelProperty(value = "确认用量（不含损耗）")
    private BigDecimal confirmQuantity;

    /** 确认用量 */
    @TableField(exist = false)
    @Excel(name = "用量（含损耗）")
    @ApiModelProperty(value = "用量（含损耗）")
    private BigDecimal lossConfirmQuantity;

    /** 确认损耗率（%） */
    @Excel(name = "确认损耗率（%）")
    @ApiModelProperty(value = "确认损耗率（%）")
    private BigDecimal confirmLossRate;

    /** 基本计量单位编码 */
    @Excel(name = "基本计量单位编码")
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBase;
//
//    /** 取整方式（损耗） */
//    @Excel(name = "取整方式（损耗）")
//    @ApiModelProperty(value = "取整方式（损耗）")
//    private String roundingType;
//
//    /** 计价量 */
//    @Excel(name = "计价量")
//    @ApiModelProperty(value = "计价量")
//    @Digits(integer=10,fraction = 3,message = "计价量上限为10位，小数位上限为3位")
//    private BigDecimal priceQuantity;
//
//    /** 计价量计量单位编码 */
//    @Excel(name = "计价量计量单位编码")
//    @ApiModelProperty(value = "计价量计量单位编码")
//    private String unitPrice;
//
//    /** 内部量备注 */
//    @Excel(name = "内部量备注")
//    @ApiModelProperty(value = "内部量备注")
//    private String remarkInnerQuantity;
//
//    /** 报价量备注 */
//    @Excel(name = "报价量备注")
//    @ApiModelProperty(value = "报价量备注")
//    private String remarkQuoteQuantity;
//
//    /** 核价量备注 */
//    @Excel(name = "核价量备注")
//    @ApiModelProperty(value = "核价量备注")
//    private String remarkCheckQuantity;
//
//    /** 确认价量备注 */
//    @Excel(name = "确认价量备注")
//    @ApiModelProperty(value = "确认价量备注")
//    private String remarkConfirmQuantity;
//
//    /** 创建人账号 */
//    @Excel(name = "创建人账号")
//    @TableField(fill = FieldFill.INSERT)
//    @ApiModelProperty(value = "创建人账号")
//    private String creatorAccount;
//
//    /** 创建时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
//    @TableField(fill = FieldFill.INSERT)
//    @ApiModelProperty(value = "创建时间")
//    private Date createDate;
//
//    /** 更新人账号 */
//    @Excel(name = "更新人账号")
//    @TableField(fill = FieldFill.UPDATE)
//    @ApiModelProperty(value = "更新人账号")
//    private String updaterAccount;
//
//    /** 更新时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
//    @TableField(fill = FieldFill.UPDATE)
//    @ApiModelProperty(value = "更新时间")
//    private Date updateDate;
//
//    /** 数据源系统 */
//    @Excel(name = "数据源系统")
//    @TableField(fill = FieldFill.INSERT)
//    @ApiModelProperty(value = "数据源系统")
//    private String dataSourceSys;

//    /** 物料BOM组件具体尺码用量list */
//    @TableField(exist = false)
//    @ApiModelProperty(value = "物料BOM组件具体尺码用量list")
//    private List<TecBomSizeQuantity> sizeQuantityList;

//    /**
//     * 接收存为 position_name
//     */
//    @TableField(exist = false)
//    private String position;

//    /** 物料详情 */
//    @TableField(exist = false)
//    @ApiModelProperty(value = "物料详情")
//    private BasMaterial material;

    private String remark;

    /**
     * 需求量(含损耗)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(含损耗)")
    private BigDecimal lossRequireQuantity;

    /**
     * 需求量(不含损耗)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "需求量(不含损耗)")
    private BigDecimal requireQuantity;

    /** 采购类型编码（默认） */
    @TableField(exist = false)
    @Excel(name = "采购类型编码（默认）", dictType = "s_purchase_type")
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    /** 供应商编码（默认） */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码（默认）")
    private Long vendorSid;

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

    /** 物料（商品/服务）分类编码 */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料（商品/服务）分类编码")
    private Long materialClassSid;
//
//    /** 物料分类名称 */
//    @TableField(exist = false)
//    @Excel(name = "物料分类名称")
//    @ApiModelProperty(value = "物料分类名称")
//    private String nodeName;

    /** 物料sid */
    @TableField(exist = false)
    @ApiModelProperty(value = "物料sid")
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

//    /** SKU1sid */
//    @TableField(exist = false)
//    @ApiModelProperty(value = "SKU1sid")
//    private Long sku1Sid;
//
//    /** SKU2sid */
//    @TableField(exist = false)
//    @ApiModelProperty(value = "SKU2sid")
//    private Long sku2Sid;

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

//    @TableField(exist = false)
//    @ApiModelProperty(value = "备注（内部成本价）")
//    private String remarkInner;
//
//    @TableField(exist = false)
//    @ApiModelProperty(value = "备注（报价）")
//    private String remarkQuote;
//
//    @TableField(exist = false)
//    @ApiModelProperty(value = "备注（核定价）")
//    private String remarkCheck;
//
//    @TableField(exist = false)
//    @ApiModelProperty(value = "备注（确认价）")
//    private String remarkConfirm;

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
}
