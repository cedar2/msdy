package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 销售订单-设置是否首缸
 *
 * @author linhongwei
 * @date 2021-03-04
 */
@Data
@ApiModel
public class SalSalesOrderItemSetRequest {

    @ApiModelProperty(value = "设置是否首缸")
    private String isMakeShougang;

    @ApiModelProperty(value = "所选明细行sid")
    private List<Long> salSalesOrderItemSidList;
}
