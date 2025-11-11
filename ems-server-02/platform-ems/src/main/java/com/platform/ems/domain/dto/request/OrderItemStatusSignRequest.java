package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单 签收状态请求参数
 *
 */
@Data
@ApiModel
public class OrderItemStatusSignRequest {

    @ApiModelProperty(value = "所选行sid")
    private Long[] sidList;

    @ApiModelProperty(value = "签收状态")
    private String signInStatus;

}
