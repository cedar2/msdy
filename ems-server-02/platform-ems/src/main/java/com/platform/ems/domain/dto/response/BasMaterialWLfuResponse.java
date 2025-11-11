package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料辅料导出实体
 *
 * @author chenkw
 * @date 2022-10-24
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialWLfuResponse {

    /** 物料（商品/服务）编码 */
    @Excel(name = "物料编码")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /** 物料（商品/服务）名称 */
    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /** 规格 */
    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "规格")
    private String specificationSize;

    /** 型号 */
    @Excel(name = "型号")
    @ApiModelProperty(value = "型号")
    private String modelSize;

    /** 材质 */
    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用状态" ,dictType = "s_valid_flag")
    private String status;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态" , dictType = "s_handle_status")
    private String handleStatus;

    @Excel(name = "采购类型")
    @ApiModelProperty(value = "采购类型名称")
    private String purchaseTypeName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    private String materialTypeName;

    @Excel(name = "物料分类")
    @ApiModelProperty(value ="物料分类名称")
    private String materialClassName;

    @Excel(name = "我方跟单员")
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @Excel(name = "供方业务员")
    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @Excel(name = "拉链标识", dictType = "s_zipper_flag")
    @ApiModelProperty(value = "拉链标识")
    private String zipperFlag;

    /** 口型 */
    @Excel(name = "口型")
    @ApiModelProperty(value = "口型")
    private String zipperMonth;

    /** 号型 */
    @Excel(name = "号型")
    @ApiModelProperty(value = "号型")
    private String zipperSize;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本单位")
    private String unitBaseName;

    @Excel(name = "BOM用量单位")
    @ApiModelProperty(value = "BOM用量单位")
    private String unitQuantityName;

    @Excel(name = "换算比例(基本单位/BOM用量单位)")
    @ApiModelProperty(value = "换算比例(基本单位/BOM用量单位)")
    private BigDecimal unitConversionRate;

    @Excel(name = "报价单位")
    @ApiModelProperty(value = "报价单位")
    private String unitPriceName;

    @Excel(name = "换算比例(报价单位/基本计量单位)")
    @ApiModelProperty(value = "单位换算比例（报价单位/基本计量单位）")
    private BigDecimal unitConversionRatePrice;

    @Excel(name = "库存价核算方式", dictType = "s_inventory_price_method")
    @ApiModelProperty(value = "库存价核算方式（数据字典的键值）")
    private String inventoryPriceMethod;

    @Excel(name = "固定价（人民币/元）")
    @ApiModelProperty(value = "库存固定价")
    private BigDecimal inventoryStandardPrice;

    @Excel(name = "是否SKU物料", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否SKU物料")
    private String isSkuMaterial;

    @Excel(name = "SKU维度数", dictType = "s_sku_dimension")
    @ApiModelProperty(value = "SKU维度数")
    private Integer skuDimension;

    /** SKU1类型编码 */
    @Excel(name = "SKU1类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU1类型编码")
    private String sku1Type;

    /** SKU2类型编码 */
    @Excel(name = "SKU2类型" , dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU2类型编码")
    private String sku2Type;

    @Excel(name = "是否存在SKU1", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在SKU1（数据字典的键值或配置档案的编码）")
    private String isHasCreatedSku1;

    @Excel(name = "是否存在SKU2", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在SKU2（数据字典的键值或配置档案的编码）")
    private String isHasCreatedSku2;

    @Excel(name = "所属生产环节",dictType = "s_touse_produce_stage")
    @ApiModelProperty(value = "所属生产环节")
    private String touseProduceStage;

    @Excel(name = "最低起订量", scale = 2)
    @ApiModelProperty(value = "最低起订量")
    private BigDecimal minOrderQuantity;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;
}
