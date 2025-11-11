package com.platform.ems.domain.dto.response;

import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.SalSalesOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 订单获取价格
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class OrderUpdatePriceResponse {

    @ApiModelProperty(value = "所选择的采购订单明细")
    List<PurPurchaseOrderItem> purPurchaseOrderItemList;

    @ApiModelProperty(value = "所选择的销售订单明细")
    List<SalSalesOrderItem> salesOrderItemsList;

    List<CommonErrMsgResponse> msgList;

    @ApiModelProperty(value = "提示是否包含税率不一致")
    String isIncludeTax;
}
