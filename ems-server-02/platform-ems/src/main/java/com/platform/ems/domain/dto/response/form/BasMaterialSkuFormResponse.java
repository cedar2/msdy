package com.platform.ems.domain.dto.response.form;

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料/商品SKU明细报表 BasMaterialSkuFormResponse
 *
 * @author chenkaiwen
 * @date 2021-12-16
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialSkuFormResponse extends EmsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "租户ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料档案")
    private Long materialSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料SKU信息")
    private Long materialSkuSid;

    @Excel(name = "物料/商品编码")
    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @Excel(name = "物料/商品名称")
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @Excel(name = "类别",dictType = "s_material_category")
    @ApiModelProperty(value = "物料（商品/服务）类别")
    private String materialCategory;

    @Excel(name = "物料/商品类型")
    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @Excel(name = "SKU属性类型",dictType = "s_sku_type")
    @ApiModelProperty(value = "SKU类型编码")
    private String skuType;

    @Excel(name = "SKU属性编码")
    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    @Excel(name = "SKU属性名称")
    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    @Excel(name = "SKU属性名称2")
    @ApiModelProperty(value = "SKU名称2")
    private String skuName2;

    @Excel(name = "SKU属性数值")
    @ApiModelProperty(value = "sku数值")
    private BigDecimal skuNumeralValue;

    @Excel(name = "供方SKU属性编码")
    @ApiModelProperty(value = "供方sku编码")
    private String supplierSkuCode;

    @Excel(name = "供方SKU属性名称")
    @ApiModelProperty(value = "供方sku名称")
    private String supplierSkuName;

    @Excel(name = "客方SKU属性编码")
    @ApiModelProperty(value =" 客方SKU编码")
    private String customerSkuCode;

    @Excel(name = "客方SKU属性名称")
    @ApiModelProperty(value ="客方SKU名称")
    private String customerSkuName;

    /** 物料sku明细中的状态 */
    @Excel(name = "启用/停用状态",dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @Excel(name = "处理状态",dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @Excel(name = "是否已建BOM(按色)",dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否已创建BOM")
    private String isCreateBom;

    @Excel(name = "BOM处理状态(按色)",dictType = "s_handle_status")
    @ApiModelProperty(value = "bom处理状态")
    private String bomHandleStatus;

    @Excel(name = "BOM启用/停用(按色)",dictType = "s_valid_flag")
    @ApiModelProperty(value = "BOM启用/停用(按色)")
    private String bomStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;
}
