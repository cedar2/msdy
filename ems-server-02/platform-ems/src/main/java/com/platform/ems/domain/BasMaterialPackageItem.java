package com.platform.ems.domain;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.validation.constraints.NotNull;

/**
 * 常规辅料包-明细对象 s_bas_material_package_item
 *
 * @author linhongwei
 * @date 2021-03-14
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_material_package_item")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialPackageItem extends EmsBaseEntity {

    /** 客户端口号 */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /** 系统ID-常规辅料包明细 */
    @Excel(name = "系统ID-常规辅料包明细")
    @ApiModelProperty(value = "系统ID-常规辅料包明细")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long materialPackItemSid;

    @TableField(exist = false)
    private Long[] materialPackItemSidList;

    /** 系统ID-常规辅料包档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-常规辅料包档案")
    private Long materialPackageSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-常规辅料包档案")
    private Long[] materialPackageSidList;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码")
    private String barcode;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-商品条码")
    private Long barcodeSid;

    /** 系统ID-物料档案 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料档案")
    private Long materialSid;

    /** SKU1编码 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU1编码")
    private Long sku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU1名称")
    private String sku1Name;

    /** SKU2编码 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "SKU2编码")
    private Long sku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "SKU2名称")
    private String sku2Name;

    @TableField(exist = false)
    @Excel(name = "用量单位编码")
    @ApiModelProperty(value = "用量单位编码")
    private String unitQuantity;

    @TableField(exist = false)
    @Excel(name = "用量单位名称")
    @ApiModelProperty(value = "用量单位名称")
    private String unitQuantityName;

    @TableField(exist = false)
    @Excel(name = "计量单位编码")
    @ApiModelProperty(value = "计量单位编码")
    private String unitPriceName;

    @TableField(exist = false)
    @Excel(name = "基本计量单位编码")
    @ApiModelProperty(value = "基本计量单位编码")
    private String unitBaseName;

    @TableField(exist = false)
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人名称")
    private String creatorAccountName;

    @TableField(exist = false)
    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人名称")
    private String updaterAccountName;

    @TableField(exist = false)
    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人名称")
    private String confirmerAccountName;

    /** 用量 */
    @Excel(name = "用量")
    @ApiModelProperty(value = "用量")
    @NotNull(message = "物料明细用量不能为空")
    @Digits(integer=5,fraction = 4,message = "明细用量整数位上限为5位，小数位上限为4位")
    private BigDecimal quantity;

    /** 计量单位编码 */
    @Excel(name = "计量单位编码")
    @ApiModelProperty(value = "计量单位编码")
    private String unit;

    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位")
    private String unitName;

    @TableField(exist = false)
    @ApiModelProperty(value = "计量单位编码")
    private String unitBase;

    @Excel(name = "序号")
    @ApiModelProperty(value = "序号")
    @Digits(integer=5,fraction = 3,message = "明细序号整数位上限为5位，小数位上限为3位")
    private BigDecimal sort;

    @Excel(name = "物料名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Excel(name = "物料编码")
    @TableField(exist = false)
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;

    @TableField(exist = false)
    @ApiModelProperty(value = "供方编码")
    private String supplierProductCode;

    @TableField(exist = false)
    private String width;

    @TableField(exist = false)
    private String gramWeight;

    @TableField(exist = false)
    private String yarnCount;

    @TableField(exist = false)
    private String density;

    @TableField(exist = false)
    private String composition;

    @TableField(exist = false)
    private String specificationSize;

    @TableField(exist = false)
    private String materialComposition;

    @TableField(exist = false)
    private String purchaseType;

    @TableField(exist = false)
    private String vendorName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSkuSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类名称")
    private String materialClassName;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料类型名称")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型名称")
    private String purchaseTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片路径")
    private String picturePath;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料分类")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialClassSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "辅料包编码")
    private String packageCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "辅料包名称")
    private String packageName;

    @TableField(exist = false)
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品条码启用/停用状态")
    private String barcodeStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料启用/停用状态")
    private String materialStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料处理状态")
    private String materialHandleStatus;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认人日期")
    private String confirmDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态")
    private String[] handleStatusList;

    @TableField(exist = false)
    @ApiModelProperty(value = "启用/停用状态")
    private String[] statusList;

    @TableField(exist = false)
    private String unitPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "BOM用量取整方式 / BOM用量取整方式")
    private String roundingType;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStageName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private Long vendorSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String vendorShortName;

}
