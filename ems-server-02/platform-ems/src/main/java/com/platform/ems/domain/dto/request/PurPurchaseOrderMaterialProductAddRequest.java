package com.platform.ems.domain.dto.request;

import com.platform.ems.domain.PurPurchaseOrderMaterialProduct;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 采购订单-所用商品信息
 *
 * @author yangqz
 * @date 2021-04-20
 */
@Data
@ApiModel
public class PurPurchaseOrderMaterialProductAddRequest {

    @ApiModelProperty(value = "系统自增长ID-采购订单")
    private Long purchaseOrderSid;

    @ApiModelProperty(value = "系统自增长ID-采购订单明细行")
    private Long purchaseOrderItemSid;

    @ApiModelProperty(value = "所用商品信息子表")
    List<PurPurchaseOrderMaterialProduct>  itemList;
}
