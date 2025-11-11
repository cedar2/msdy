package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售状况-客户销售占比
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
public class RepSalesStatusOrderTotalDivResponse {

    @ApiModelProperty(value = "占比")
    private BigDecimal proportion;

    @ApiModelProperty(value = "客户")
    private String customerName;

    @ApiModelProperty(value = "总金额")
    private BigDecimal sumMoneyAmount;
}
