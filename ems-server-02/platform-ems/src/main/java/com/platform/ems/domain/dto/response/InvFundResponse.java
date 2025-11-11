package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 按款选料 响应实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvFundResponse {

    @Excel(name = "系统SID-物料&商品&服务")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品&服务")
    private Long materialSid;


    @Excel(name = "系统SID-物料&商品sku1")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku1")
    private Long sku1Sid;


    @Excel(name = "系统SID-物料&商品sku2")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品sku2")
    private Long sku2Sid;

    @Excel(name = "系统SID-物料&商品条码")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private Long barcodeSid;

    @TableField(exist = false)
    @ApiModelProperty(value = "系统SID-物料&商品条码")
    private String barcode;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料名称")
    private String materialName;

    @TableField(exist = false)
    @ApiModelProperty(value ="物料编码")
    private String materialCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="商品编码（款号）")
    private String materialCodeK;

    @ApiModelProperty(value ="款颜色")
    private String sku1NameK;

    @ApiModelProperty(value ="款尺码")
    private String sku2NameK;

    @ApiModelProperty(value = "款颜色")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku1Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "款颜色")
    private String productSku1Name;

    @ApiModelProperty(value = "款颜色code")
    private String productSku1Code;

    @ApiModelProperty(value = "款尺码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long productSku2Sid;

    @TableField(exist = false)
    private String sku1Name;

    @TableField(exist = false)
    private String sku2Name;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBase;

    @ApiModelProperty(value = "基本计量单位名称")
    @TableField(exist = false)
    private String unitBaseName;

    @ApiModelProperty(value = "采购类型编码（默认）")
    private String purchaseType;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "采购类型")
    private String purchaseTypeName;

    @Excel(name = "出入库量")
    @ApiModelProperty(value = "出入库量")
    private BigDecimal quantity;

    @Excel(name = "bom用量")
    @ApiModelProperty(value = "bom用量")
    private BigDecimal bomQuantity;

    @Excel(name = "bom用量（含损耗率）")
    @ApiModelProperty(value = "bom用量（含损耗率）")
    private BigDecimal lossBomQuantity;

    @ApiModelProperty(value ="备注")
    private String remark;

    @ApiModelProperty(value = "款备注")
    private String productCodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "物料档案规格尺寸")
    private String specificationSize;
}
