package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物料清单（BOM）组件清单对象-拉链 请求信息
 *
 */
@ApiModel
@Data
public class MaterialReportZipperFormsRequest implements Serializable {

    @NotNull(message = "商品编码")
    @ApiModelProperty(value = "商品条码sid")
    private Long materialSid;

    @NotNull(message = "商品颜色")
    @ApiModelProperty(value = "商品颜色")
    private Long sku1Sid;

    @ApiModelProperty(value = "商品颜色")
    private Long sku2Sid;

    @NotNull(message = "订单数量")
    @ApiModelProperty(value = "订单数量")
    private BigDecimal quantity;

    @Excel(name = "商品")
    @ApiModelProperty(value = "款名称")
    private String saleMaterialName;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "款编码")
    private String saleMaterialCode;

    @Excel(name = "商品sku1")
    @ApiModelProperty(value = "款sku1名称")
    private String saleSku1Name;

    @Excel(name = "商品sku2")
    @ApiModelProperty(value = "款sku2名称")
    private String saleSku2Name;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "物料分类")
    private String materialCategory;

    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "采购类型")
    private String purchaseType;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String manufactureOrderCode;

    @ApiModelProperty(value = "采购订单号")
    private String purchaseOrderCode;

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;
}
