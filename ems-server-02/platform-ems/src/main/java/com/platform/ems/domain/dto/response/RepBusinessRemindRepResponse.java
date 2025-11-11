package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售订单/采购订单 已逾期看板报表
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
public class RepBusinessRemindRepResponse {

    @ApiModelProperty(value = "预警类型：已逾期、即将到期")
    private String remindType;


    @ApiModelProperty(value = "款数")
    private int quantity;

    @ApiModelProperty(value = "待发货量")
    private BigDecimal quantityDaiFh;

}
