package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.BasMaterialSku;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class TecBomMaterialResponse {

    /** 物料（商品/服务）编码 */
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialSid;

    /** 物料（商品/服务）名称 */
    @Excel(name = "物料（商品/服务）名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    /** 供方编码（物料/商品/服务） */
    @Excel(name = "供方编码（物料/商品/服务）")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    /** 供应商编码（默认） */
    @Excel(name = "供应商编码（默认）")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "供应商编码（默认）")
    private Long vendorSid;

    /** 供应商名称 */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    /** 克重 */
    @Excel(name = "克重")
    @ApiModelProperty(value = "克重")
    @Length(max = 180,message = "克重不能超过180个字符")
    private String gramWeight;


    /** 纱支 */
    @Excel(name = "纱支")
    @ApiModelProperty(value = "纱支")
    @Length(max = 180,message = "纱支不能超过180个字符")
    private String yarnCount;

    /** 密度 */
    @Excel(name = "密度")
    @ApiModelProperty(value = "密度")
    @Length(max = 180,message = "密度不能超过180个字符")
    private String density;

    /** 成分 */
    @Excel(name = "成分")
    @ApiModelProperty(value = "成分")
    @Length(max = 180,message = "成分不能超过180个字符")
    private String composition;

    /** 采购类型编码（默认） */
    @Excel(name = "采购类型编码（默认）", dictType = "s_purchase_type")
    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    /** 采购类型名称（默认） */
    @Excel(name = "采购类型名称（默认）")
    @ApiModelProperty(value = "采购类型名称（默认）")
    @TableField(exist = false)
    private String purchaseTypeName;

    /** 材质 */
    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    @Length(max = 180,message = "材质不能超过180个字符")
    private String materialComposition;

    /** 规格 */
    @Excel(name = "规格")
    @ApiModelProperty(value = "规格")
    @Length(max = 180,message = "规格尺寸不能超过180个字符")
    private String specificationSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料&商品-SKU明细对象")
    private List<BasBomMaterialSkuResponse> basMaterialSkuList;


}
