package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel
@Accessors(chain = true)
public class PurChaseOrderSetRequest {

    @ApiModelProperty(value = "系统自增长ID-采购合同")
    private Long purchaseContractSid;

    @ApiModelProperty(value = "采购合同")
    private String purchaseContractCode;

    @ApiModelProperty(value = "系统自增长ID-采购合同")
    private Long purchaseOrderSid;

    @ApiModelProperty(value = "采购订单sids")
    private Long[] purchaseOrderSids;
}
