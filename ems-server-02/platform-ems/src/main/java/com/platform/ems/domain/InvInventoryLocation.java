package com.platform.ems.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;


import com.platform.common.core.domain.EmsBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import lombok.experimental.Accessors;

/**
 * 仓库库位库存对象 s_inv_inventory_location
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_inv_inventory_location")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvInventoryLocation extends EmsBaseEntity implements Serializable {

    /**
     * 租户ID
     */
    @Excel(name = "租户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "租户ID")
    private String clientId;

    /**
     * 系统SID-仓库库位库存信息
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-仓库库位库存信息")
    private Long locationStockSid;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] locationStockSidList;
    /**
     * 系统SID-物料&商品&服务
     */
    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long[] materialSidList;

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
     * 系统SID-物料&商品条码
     */
    @Excel(name = "系统SID-物料&商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcode;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private String barcode2;

    /**
     * 基本计量单位（数据字典的键值）
     */
    @Excel(name = "基本计量单位（数据字典的键值）")
    @ApiModelProperty(value = "基本计量单位（数据字典的键值）")
    private String unitBase;

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
     * 价格（加权平均价）
     */
    @Excel(name = "价格（加权平均价）")
    @ApiModelProperty(value = "价格（加权平均价）")
    private BigDecimal price;

    /**
     * 总库存量
     */
    @Excel(name = "总库存量")
    @ApiModelProperty(value = "总库存量")
    private BigDecimal totalQuantity;

    /**
     * 非限制库存量（非限制使用的库存）
     */
    @Excel(name = "非限制库存量（非限制使用的库存）")
    @ApiModelProperty(value = "非限制库存量（非限制使用的库存）")
    private BigDecimal unlimitedQuantity;

    /**
     * 在检库存量（处于质检的库存）
     */
    @Excel(name = "在检库存量（处于质检的库存）")
    @ApiModelProperty(value = "在检库存量（处于质检的库存）")
    private BigDecimal qualityTestedQuantity;

    /**
     * 冻结库存量（被冻结的库存）
     */
    @Excel(name = "冻结库存量（被冻结的库存）")
    @ApiModelProperty(value = "冻结库存量（被冻结的库存）")
    private BigDecimal freezedQuantity;

    /**
     * 供应商寄售库存量（供应商提供的寄售库存）
     */
    @Excel(name = "供应商寄售库存量（供应商提供的寄售库存）")
    @ApiModelProperty(value = "供应商寄售库存量（供应商提供的寄售库存）")
    private BigDecimal vendorConsignQuantity;

    /**
     * 供应商委外库存量（给供应商用于委外加工的库存）
     */
    @Excel(name = "供应商委外库存量（给供应商用于委外加工的库存）")
    @ApiModelProperty(value = "供应商委外库存量（给供应商用于委外加工的库存）")
    private BigDecimal vendorSubcontractQuantity;

    /**
     * 客户寄售库存量（放在客户方的用于寄售的库存）
     */
    @Excel(name = "客户寄售库存量（放在客户方的用于寄售的库存）")
    @ApiModelProperty(value = "客户寄售库存量（放在客户方的用于寄售的库存）")
    private BigDecimal customerConsignQuantity;

    /**
     * 客户来料库存量（客户提供的来料库存）
     */
    @Excel(name = "客户来料库存量（客户提供的来料库存）")
    @ApiModelProperty(value = "客户来料库存量（客户提供的来料库存）")
    private BigDecimal customerSubcontractQuantity;

    /**
     * 销售订单的库存量
     */
    @Excel(name = "销售订单的库存量")
    @ApiModelProperty(value = "销售订单的库存量")
    private BigDecimal saleStockQuantity;

    /**
     * 项目的库存量
     */
    @Excel(name = "项目的库存量")
    @ApiModelProperty(value = "项目的库存量")
    private BigDecimal projectStockQuantity;

    /**
     * 当前在途的库存量
     */
    @Excel(name = "当前在途的库存量")
    @ApiModelProperty(value = "当前在途的库存量")
    private BigDecimal transitStockQuantity;

    /**
     * 当前预留的库存量
     */
    @Excel(name = "当前预留的库存量")
    @ApiModelProperty(value = "当前预留的库存量")
    private BigDecimal reserveStockQuantity;

    /**
     * 首次更新库存时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "首次更新库存时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "首次更新库存时间")
    private Date firstUpdateStockDate;

    /**
     * 最近更新库存时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近更新库存时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近更新库存时间")
    private Date latestUpdateStockDate;

    /**
     * 首次生产入库时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "首次生产入库时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "首次生产入库时间")
    private Date firstManufactEntryDate;

    /**
     * 最近生产入库时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近生产入库时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近生产入库时间")
    private Date latestManufactEntryDate;

    /**
     * 首次采购入库时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "首次采购入库时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "首次采购入库时间")
    private Date firstPurchaseEntryDate;

    /**
     * 最近采购入库时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近采购入库时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近采购入库时间")
    private Date latestPurchaseEntryDate;

    /**
     * 首次调拨入库时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "首次调拨入库时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "首次调拨入库时间")
    private Date firstTransferEntryDate;

    /**
     * 最近调拨入库时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近调拨入库时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近调拨入库时间")
    private Date latestTransferEntryDate;

    /**
     * 最近盘点时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最近盘点时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最近盘点时间")
    private Date latestCountDate;

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
     * 确认人账号（用户名称）
     */
    @Excel(name = "确认人账号（用户名称）")
    @ApiModelProperty(value = "确认人账号（用户名称）")
    private String confirmerAccount;

    /**
     * 确认时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统（数据字典的键值）
     */
    @Excel(name = "数据源系统（数据字典的键值）")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统（数据字典的键值）")
    private String dataSourceSys;

    @TableField(exist = false)
    @Excel(name = "仓库名称")
    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @TableField(exist = false)
    @Excel(name = "仓库编码")
    @ApiModelProperty(value = "仓库编码")
    private String storehouseCode;

    @TableField(exist = false)
    @Excel(name = "库位名称")
    @ApiModelProperty(value = "库位名称")
    private String locationName;

    @TableField(exist = false)
    @Excel(name = "库位编码")
    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "库位编码")
    private String storehouseLocationCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料商品图片")
    private String picturePath;

    @TableField(exist = false)
    private String sku1Code;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Code;

    @TableField(exist = false)
    private String sku2Name;

    @Excel(name = "基本计量单位")
    @ApiModelProperty(value = "基本计量单位")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "查询：库位")
    @TableField(exist = false)
    private Long[] storehouseLocationSidList;

    @ApiModelProperty(value = "查询：物料分类")
    @TableField(exist = false)
    private Long[] materialClassSidList;

    @ApiModelProperty(value = "查询：物料类型")
    @TableField(exist = false)
    private String[] materialTypeList;

    @ApiModelProperty(value = "物料类型")
    @TableField(exist = false)
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlag;

    @TableField(exist = false)
    @ApiModelProperty(value = "使用频率标识（数据字典的键值或配置档案的编码）")
    private String usageFrequencyFlagName;

    @TableField(exist = false)
    @ApiModelProperty(value = "成本价(含税)")
    private BigDecimal priceCostTax;

    @TableField(exist = false)
    @ApiModelProperty(value = "成本金额(元)")
    private BigDecimal amountPriceCostTax;

    @ApiModelProperty(value = "查询：是否显示0库存")
    @TableField(exist = false)
    private String whether;

    @TableField(exist = false)
    @ApiModelProperty(value = "可用库存")
    private BigDecimal ableQuantity;

    @TableField(exist = false)
    @ApiModelProperty(value = "预留库存")
    private BigDecimal obligateQuantity;


    @TableField(exist = false)
    @ApiModelProperty(value = "供应商sid")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户sid")
    private Long customerSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户sid")
    private String specialStock;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-供应商特殊库存信息")
    @TableField(exist = false)
    private Long vendorSpecialStockSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-客户特殊库存信息")
    @TableField(exist = false)
    private Long customerSpecialStockSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "库存预留-对应业务单明细行sid")
    @TableField(exist = false)
    private Long businessOrderSid;

    @ApiModelProperty(value = "查询：仓库(多选)")
    @TableField(exist = false)
    private Long[] storehouseSidList;

    @ApiModelProperty(value = "库存可用类型（数据字典键值）")
    @TableField(exist = false)
    private String usableType;

    @TableField(exist = false)
    private Long bomMaterialSid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku1Sid;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;

    /**
     * 移动端库存报表
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "按款按款色按SKU维度")
    private String progressDimension;

}
