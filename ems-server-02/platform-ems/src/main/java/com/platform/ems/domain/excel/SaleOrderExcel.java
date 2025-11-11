package com.platform.ems.domain.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author c
 */
@Data
public class SaleOrderExcel {

    @ApiModelProperty(value ="销售订单号")
    private String saleOrderCode;
    @ApiModelProperty(value ="销售发货单号")
    private String deliveryNoteCode;
    @ApiModelProperty(value ="出库日期")
    private String documentDate;
    @ApiModelProperty(value ="销售员")
    private String salePerson;
    @ApiModelProperty(value ="仓管员")
    private String storehouseAdmin;
    @ApiModelProperty(value ="备注")
    private String remark;


}
