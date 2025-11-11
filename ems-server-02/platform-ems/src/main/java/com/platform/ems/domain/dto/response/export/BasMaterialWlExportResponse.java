package com.platform.ems.domain.dto.response.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * 物料导出 BasMaterialWlExportResponse
 *
 * @author chenkaiwen
 * @date 2021-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialWlExportResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "物料编码")
    @ApiModelProperty(value = "外采样编码")
    private String materialCode;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "外采样名称")
    private String materialName;

    @Excel(name = "供应商简称")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    @Excel(name = "启用/停用状态", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @Excel(name = "幅宽（厘米）")
    @ApiModelProperty(value = "幅宽（厘米）")
    private String width;

    @Excel(name = "克重")
    @ApiModelProperty(value = "克重")
    private String gramWeight;

    @Excel(name = "成分")
    @ApiModelProperty(value = "主面料成分编码")
    private String mainFabricType;

    @Excel(name = "纱支")
    @ApiModelProperty(value = "纱支")
    private String yarnCount;

    @Excel(name = "密度")
    @ApiModelProperty(value = "密度")
    private String density;

    @Excel(name = "规格尺寸")
    @ApiModelProperty(value = "规格")
    private String specificationSize;

    @Excel(name = "材质")
    @ApiModelProperty(value = "材质")
    private String materialComposition;

    @Excel(name = "季节", dictType = "s_season")
    @ApiModelProperty(value = "季节编码")
    private String season;

    @Excel(name = "采购类型")
    @ApiModelProperty(value = "采购类型名称（默认）")
    private String purchaseTypeName;

    @Excel(name = "物料类型")
    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    private String materialTypeName;

    @Excel(name = "物料分类")
    @ApiModelProperty(value = "物料类型名称（物料/商品/服务）")
    private String materialClassName;

    @Excel(name = "是否复核面料", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否复核面料")
    private String isCompositeMaterial;

    @Excel(name = "供方编码")
    @ApiModelProperty(value = "供方编码（物料/商品/服务）")
    private String supplierProductCode;

    @Excel(name = "拉链标识", dictType = "s_zipper_flag")
    @ApiModelProperty(value = "拉链标识")
    private String zipperFlag;

    @Excel(name = "口型")
    @ApiModelProperty(value = "口型")
    private String zipperMonth;

    @Excel(name = "号型")
    @ApiModelProperty(value = "号型")
    private String zipperSize;

    @Excel(name = "是否SKU物料", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否SKU物料")
    private String isSkuMaterial;

    @Excel(name = "SKU维度1类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU1类型编码")
    private String sku1Type;

    @Excel(name = "SKU维度2类型", dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU1类型编码")
    private String sku2Type;

    @Excel(name = "所属生产环节", dictType = "s_touse_produce_stage")
    @ApiModelProperty(value = "所属生产环节")
    private String touseProduceStage;

    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "我方跟单员")
    @ApiModelProperty(value = "我方跟单员")
    private String buOperatorName;

    @Excel(name = "供方业务员")
    @ApiModelProperty(value = "供方业务员")
    private String buOperatorVendor;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人账号")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @Excel(name = "更新人")
    @ApiModelProperty(value = "更新人")
    private String updaterAccountName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @Excel(name = "确认人")
    @ApiModelProperty(value = "确认人")
    private String confirmerAccountName;

    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

}
