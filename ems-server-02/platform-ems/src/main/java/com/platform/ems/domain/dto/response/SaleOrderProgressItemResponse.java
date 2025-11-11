package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 销售订单统计报表 明细
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SaleOrderProgressItemResponse {

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "sku1名称")
    private String sku1Name;

    @ApiModelProperty(value = "sku2名称")
    private String sku2Name;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @ApiModelProperty(value = "销售订单号")
    private String salesOrderCode;

    @ApiModelProperty(value = "订单量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "已出库")
    private BigDecimal sumQuantityYck;

    @ApiModelProperty(value = "已开票量")
    private BigDecimal invoiceQuantity;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal priceTax;

    @ApiModelProperty(value = "已出库金额")
    private BigDecimal sumPriceTaxYck;

    @ApiModelProperty(value = "已开票金额")
    private BigDecimal invoiceCurrencyAmountTax;

    @ApiModelProperty(value = "已排产量")
    private BigDecimal alreadyQuantity;

    @ApiModelProperty(value = "待排产量")
    private BigDecimal notQuantity;

    @ApiModelProperty(value = "已生产完工量")
    private BigDecimal completeQuantity;

    @ApiModelProperty(value = "销售单位名称")
    private String unitPriceName;

    @ApiModelProperty(value = "基本计量单位")
    private String unitBaseName;

    @ApiModelProperty(value = "行号")
    private Integer itemNum;

    @ApiModelProperty(value = "原材料_需购状态（数据字典的键值或配置档案的编码）")
    private String yclXugouStatus;

    @ApiModelProperty(value = "原材料_备料状态（数据字典的键值或配置档案的编码）")
    private String yclBeiliaoStatus;

    @ApiModelProperty(value = "原材料_采购下单状态（数据字典的键值或配置档案的编码）")
    private String yclCaigouxiadanStatus;

    @ApiModelProperty(value = "原材料_齐套状态（数据字典的键值或配置档案的编码）")
    private String yclQitaoStatus;

    @ApiModelProperty(value = "即将到期提醒天数")
    private Integer toexpireDays;
}
