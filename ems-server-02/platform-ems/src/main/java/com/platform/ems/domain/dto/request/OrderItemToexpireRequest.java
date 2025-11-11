package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单 设置即将到期提醒天数
 *
 */
@Data
@ApiModel
public class OrderItemToexpireRequest {

    @ApiModelProperty(value = "所选行sid")
    private Long[] sidList;

    @ApiModelProperty(value = "即将到期提醒天数")
    private Long toexpireDays;
}
