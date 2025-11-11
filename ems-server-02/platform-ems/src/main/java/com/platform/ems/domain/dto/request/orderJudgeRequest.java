package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 判断订单未核销状态
 *
 */
@Data
@ApiModel
public class orderJudgeRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "销售订单号")
    private Long salesOrderSid;

    @ApiModelProperty(value = "采购订单")
    private Long purchaseOrderSid;

    @ApiModelProperty(value = "行号")
    private Long[] itemNumList;
}
