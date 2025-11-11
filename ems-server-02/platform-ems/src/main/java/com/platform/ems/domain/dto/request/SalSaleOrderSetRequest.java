package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 录入采购合同参数
 *
 * @author yangqz
 * @date 2021-7-16
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSaleOrderSetRequest implements Serializable {

    @ApiModelProperty(value = "销售合同")
    private Long saleContractSid;

    @ApiModelProperty(value = "销售合同")
    private String saleContractCode;

    @ApiModelProperty(value = "销售订单sid")
    private Long salesOrderSid;

    @ApiModelProperty(value = "销售订单sids")
    private Long[] salesOrderSids;
}
