package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 销售、采购 统计报表
 *
 * @author yangqz
 * @date
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderTotalResponse {


    private String vendorsid;

    private String customerSid;

    private String  productSeasonSid;

    private String documentType;

    private String businessType;

    @ApiModelProperty(value = "供应商")
    private String vendorName;

    @ApiModelProperty(value = "下单季")
    @Excel(name = "下单季")
    private String  productSeasonName;

    @ApiModelProperty(value = "客户")
    @Excel(name = "客户")
    private String customerName;

    @ApiModelProperty(value = "单据类型名称")
    @Excel(name = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "订单总数")
    @Excel(name = "订单数（单）")
    private BigDecimal orderCountQuantity;

    @ApiModelProperty(value = "订单总数款数")
    @Excel(name = "订单总款数")
    private BigDecimal orderTotalFunQuantity;

    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "订单总量")
    @Excel(name = "订单总量")
    private BigDecimal orderTotalQuantity;


    @ApiModelProperty(value = "订单总金额")
    @Excel(name = "订单总金额（万）")
    private BigDecimal orderTotalPrice;

    @Excel(name = "已出库/退货总量")
    @ApiModelProperty(value = "已出库/退货总量")
    private BigDecimal invTotalQuantity;

    @Excel(name = "已出库/退货总金额（万）")
    @ApiModelProperty(value = "已出库/退货总金额（万）")
    private BigDecimal invTotalPrice;


    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    @ApiModelProperty(value = "业务渠道/销售渠道名称")
    private String businessChannelName;

    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    @ApiModelProperty(value = "计量单位")
    private String unitBaseName;
}
