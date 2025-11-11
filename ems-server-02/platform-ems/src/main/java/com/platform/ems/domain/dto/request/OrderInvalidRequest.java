package com.platform.ems.domain.dto.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单作废
 *
 */
@Data
@ApiModel
public class OrderInvalidRequest {

    @ApiModelProperty(value = "所选明细行")
    private List<Long> sids;

    @ApiModelProperty(value = "说明")
    private String explain;

}
