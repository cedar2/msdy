package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料导出实体
 *
 * @author yangqz
 * @date 2021-7-14
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialMResponse implements Serializable{

    @Excel(name = "物料编码")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "规格")
    private String specificationSize;

    @Excel(name = "型号")
    @ApiModelProperty(value = "型号")
    private String modelSize;

    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @Excel(name = "成分")
    @ApiModelProperty(value = "成分")
    private String composition;

    @Excel(name = "供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @ApiModelProperty(value = "幅宽（厘米）")
    private String width;

    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @ApiModelProperty(value = "纱支")
    private String yarnCount;

    @ApiModelProperty(value = "密度")
    private String density;

    @ApiModelProperty(value = "启用/停用状态")
    @Excel(name = "启用/停用" ,dictType = "s_valid_flag")
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

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

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

    @Excel(name = "是否SKU物料", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否SKU物料")
    private String isSkuMaterial;

    @Excel(name = "SKU维度数", dictType = "s_sku_dimension")
    @ApiModelProperty(value = "SKU维度数")
    private Integer skuDimension;

    @Excel(name = "SKU维度1类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU1类型编码")
    private String sku1Type;

    /** SKU2类型编码 */
    @Excel(name = "SKU维度2类型" , dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU2类型编码")
    private String sku2Type;

    @Excel(name = "是否存在SKU1", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在SKU1（数据字典的键值或配置档案的编码）")
    private String isHasCreatedSku1;

    @Excel(name = "是否存在SKU2", dictType = "s_yesno_flag")
    @ApiModelProperty(value = "是否存在SKU2（数据字典的键值或配置档案的编码）")
    private String isHasCreatedSku2;

    @ApiModelProperty(value = "是否复核面料")
    private String isCompositeMaterial;

    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    @ApiModelProperty(value = "拉链标识")
    private String zipperFlag;

    @ApiModelProperty(value = "口型")
    private String zipperMonth;

    @ApiModelProperty(value = "号型")
    private String zipperSize;

    @ApiModelProperty(value = "计价/递增减计量单位")
    private String unitRecursionName;

    @ApiModelProperty(value = "库存价核算方式（数据字典的键值）")
    private String inventoryPriceMethod;

    @ApiModelProperty(value = "库存固定价")
    private BigDecimal inventoryStandardPrice;

    @ApiModelProperty(value = "所属生产环节")
    private String touseProduceStage;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "最低起订量", scale = 2)
    @ApiModelProperty(value = "最低起订量")
    private BigDecimal minOrderQuantity;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更改人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;
}
