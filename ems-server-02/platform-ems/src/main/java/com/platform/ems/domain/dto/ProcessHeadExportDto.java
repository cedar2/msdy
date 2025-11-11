package com.platform.ems.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
@Accessors(chain = true)
public class ProcessHeadExportDto {

    @Excel(name = "预警")
    @ApiModelProperty(value = "预警 R:红色  Y:黄色 G:绿色")
    private String warning;

    @Excel(name = "合同交期" , dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @Excel(name = "商品编码")
    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @Excel(name = "商品名称")
    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @Excel(name = "订单量")
    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    @Excel(name = "已出库量")
    @ApiModelProperty(value = "已出库")
    private BigDecimal sumQuantityYck;

    @Excel(name = "已开票量")
    @ApiModelProperty(value = "已开票量")
    private BigDecimal invoiceQuantity;

    @Excel(name = "订单金额(万)")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal priceTax;

    @Excel(name = "已出库金额(万)")
    @ApiModelProperty(value = "已出库金额")
    private BigDecimal sumPriceTaxYck;

    @Excel(name = "已开票金额(万)")
    @ApiModelProperty(value = "已开票金额")
    private BigDecimal invoiceCurrencyAmountTax;

    @Excel(name = "待排产量")
    @ApiModelProperty(value = "待排产量")
    private BigDecimal notQuantity;

    @Excel(name = "已排产量")
    @ApiModelProperty(value = "已排产量")
    private BigDecimal alreadyQuantity;

    @Excel(name = "已完工量")
    @ApiModelProperty(value = "已生产完工量")
    private BigDecimal completeQuantity;

    @Excel(name = "基本单位")
    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

//    ----------------------------------------------------------------
















    @ApiModelProperty(value = "即将到期提醒天数")
    private Integer toexpireDays;




}
