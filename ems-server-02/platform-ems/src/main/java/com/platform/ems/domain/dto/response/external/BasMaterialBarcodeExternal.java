package com.platform.ems.domain.dto.response.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 面辅料档案 BasMaterialExternal
 *
 * @author chenkaiwen
 * @date 2022-02-28
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialBarcodeExternal {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @ApiModelProperty(value = "面辅料档案 sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "面辅料档案编码")
    private String materialCode;

    @ApiModelProperty(value = "面辅料档案名称")
    private String materialName;

    @ApiModelProperty(value = "计量单位名称")
    private String unitBase;

    @ApiModelProperty(value = "启用/停用")
    private String status;

    @ApiModelProperty(value = "SKU1 编码")
    private String sku1Code;

    @ApiModelProperty(value = "SKU1 名称")
    private String sku1Name;

    @ApiModelProperty(value = "SKU2 编码")
    private String sku2Code;

    @ApiModelProperty(value = "SKU2 名称")
    private String sku2Name;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "是否SKU物料")
    private String isSkuMaterial;

    @ApiModelProperty(value = "SKU维度数")
    private String skuDimension;

    @ApiModelProperty(value = "SKU1 类型")
    private String sku1Type;

    @ApiModelProperty(value = "SKU2 类型")
    private String sku2Type;

    @ApiModelProperty(value = "幅宽（厘米）")
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

    @ApiModelProperty(value = "型号")
    private String modelSize;

    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @ApiModelProperty(value = "口型")
    private String zipperMonth;

    @ApiModelProperty(value = "号型")
    private String zipperSize;

    @ApiModelProperty(value = "操作类型")
    private String operateType;

}
