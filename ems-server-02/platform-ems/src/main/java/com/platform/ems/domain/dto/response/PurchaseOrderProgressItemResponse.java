package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 采购订单统计报表 明细
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurchaseOrderProgressItemResponse {

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @ApiModelProperty(value = "物料类型")
    private String materialTypeName;

    @ApiModelProperty(value = "采购员")
    private String buyerName;

    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "已入库")
    private BigDecimal sumQuantityYrk;

    @ApiModelProperty(value = "已开票量")
    private BigDecimal invoiceQuantity;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "已入库金额")
    private BigDecimal sumPriceTaxYrk;

    @ApiModelProperty(value = "已开票金额")
    private BigDecimal invoiceCurrencyAmountTax;

    @ApiModelProperty(value = "采购单位名称")
    private String unitPriceName;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @ApiModelProperty(value = "即将到期提醒天数")
    private Integer toexpireDays;

    @ApiModelProperty(value = "行号")
    private Integer itemNum;

}
