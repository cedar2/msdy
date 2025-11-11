package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 订单是否设置首批
 *
 */
@Data
@ApiModel
public class OrderItemShouPiRequest {

    @ApiModelProperty(value = "所选明细行")
    private List<Long> sids;

    @ApiModelProperty(value = "是否设置首批")
    private String isMakeShoupi;

}
