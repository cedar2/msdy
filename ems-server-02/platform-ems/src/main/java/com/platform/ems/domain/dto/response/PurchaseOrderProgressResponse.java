package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购订单统计报表
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurchaseOrderProgressResponse {

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

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

    @ApiModelProperty(value = "即将到期提醒天数")
    private Integer toexpireDays;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @ApiModelProperty(value = "预警 R:红色  Y:黄色 G:绿色")
    private String warning;
}
