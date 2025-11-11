package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 交期状况报表
 *
 * @author yangqz
 * @date
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderProgressResponse {

    @ApiModelProperty(value = "供应商")
    private String vendorName;


    private String vendorsid;

    private String customerSid;

    private String  productSeasonSid;

    @ApiModelProperty(value = "客户")
    private String customerName;


    @ApiModelProperty(value = "下单季")
    private String  productSeasonName;

    @ApiModelProperty(value = "即将到期数量")
    private BigDecimal jjdqQuantity;

    @ApiModelProperty(value = "即将到期款数")
    private BigDecimal jjdqCount;

    @ApiModelProperty(value = "即将到期款数")
    private BigDecimal jjdqPriceTax;

    @ApiModelProperty(value = "已逾期数量")
    private BigDecimal yyqQuantity;

    @ApiModelProperty(value = "已逾期款数")
    private BigDecimal yyqCount;

    @ApiModelProperty(value = "即将到期款数")
    private BigDecimal yyqPriceTax;
}
