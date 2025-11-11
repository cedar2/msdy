package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 请求
 */
@Data
@ApiModel
@Accessors(chain = true)
public class EstimateLineReportRequest {

    @ApiModelProperty(value = "查询条件：物料编码")
    private String materialCode;

    @ApiModelProperty(value = "商品条码sid")
    private Long materialSid;

    @ApiModelProperty(value = "商品颜色")
    private Long sku1Sid;

    @ApiModelProperty(value = "商品颜色")
    private Long sku2Sid;

    @NotNull(message = "订单数量")
    @ApiModelProperty(value = "订单数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "供应商")
    private Long vendorSid;

    @ApiModelProperty(value = "采购类型")
    private String purchaseType;

    @ApiModelProperty(value = "物料分类")
    private String materialCategory;

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

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "款号sid")
    private Long saleMaterialSid;

    @ApiModelProperty(value = "款sku1sid")
    @TableField(exist = false)
    private Long saleSku1Sid;

    @ApiModelProperty(value = "款sku2sid")
    @TableField(exist = false)
    private Long saleSku2Sid;

    @TableField(exist = false)
    @ApiModelProperty(value = "汇总维度类型")
    private String sumDimension;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属生产环节（数据字典的键值或配置档案的编码），如：车间、后道、包装")
    private String touseProduceStage;

    @Excel(name = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    @TableField(exist = false)
    private Long manufactureOrderCode;

    @ApiModelProperty(value = "采购订单号")
    @TableField(exist = false)
    private Long purchaseOrderCode;

    @Excel(name = "销售订单号")
    @ApiModelProperty(value = "销售订单号")
    @TableField(exist = false)
    private Long salesOrderCode;

    @Excel(name = "通用sid")
    @ApiModelProperty(value = "通用sid")
    @TableField(exist = false)
    private Long commonSid;

    @Excel(name = "通用明细行sid")
    @ApiModelProperty(value = "通用明细行sid")
    @TableField(exist = false)
    private Long commonItemSid;

    @Excel(name = "通用行号")
    @ApiModelProperty(value = "通用行号")
    @TableField(exist = false)
    private Long commonItemNum;

    @TableField(exist = false)
    private String handleStatus;
}
