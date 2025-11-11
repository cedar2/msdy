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
public class RepSalesStatusOrderTotalResponse {

    @ApiModelProperty(value = "客户销售占比情况")
    private List<RepSalesStatusOrderTotalDivResponse> itemList;

    @ApiModelProperty(value = "产品季code")
    private String productSeasonCode;

    @ApiModelProperty(value = "销售总额")
    private BigDecimal sumMoneyAmount;
}
