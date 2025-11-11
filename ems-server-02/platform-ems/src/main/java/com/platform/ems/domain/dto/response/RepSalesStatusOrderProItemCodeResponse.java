package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 销售状况-销售同比-各渠道数据-
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class RepSalesStatusOrderProItemCodeResponse {

    @ApiModelProperty(value = "产品季节名称")
    private String productSeasonName;

    @ApiModelProperty(value = "产品季节code")
    private String productSeasonCode;

    @ApiModelProperty(value = "销售额")
    private BigDecimal totalItem;
}
