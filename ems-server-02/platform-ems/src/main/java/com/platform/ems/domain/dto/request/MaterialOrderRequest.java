package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 销售订单-设置下单状态
 *
 */
@Data
@ApiModel
public class MaterialOrderRequest {

    @ApiModelProperty(value = "所选行sid")
    private Long[] sidList;

    @ApiModelProperty(value = "辅料_采购下单状态")
    private String flCaigouxiadanStatus;

    @ApiModelProperty(value = "面料_采购下单状态")
    private String mlCaigouxiadanStatus;

    @ApiModelProperty(value = "type:FL 表示辅料  ML 表示面料")
    private String type;
}
