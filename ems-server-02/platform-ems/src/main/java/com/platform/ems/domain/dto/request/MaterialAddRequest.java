package com.platform.ems.domain.dto.request;

import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.dto.response.OrderItemFunResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangqz
 */
@Data
@ApiModel
public class MaterialAddRequest {

    @ApiModelProperty(value = "订单号")
    private String code;

    private List<OrderItemFunResponse> orderItemList;
}
