package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单设置委托人
 *
 */
@Data
@ApiModel
public class OrderTrustorAccountRequest {

    @ApiModelProperty(value = "所选订单sid")
    private List<Long> orderSidList;

    @ApiModelProperty(value = "委托人账号")
    private String trustorAccount;
}
