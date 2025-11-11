package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 **采购价修改状态请求信息
 * @author yang qi ze
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel
public class PurPurchasePriceActionRequest {
    @ApiModelProperty(value = "采购价id")
    private Long[] purchasePriceSids;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    @ApiModelProperty(value = "启用/停用")
    private String status;

}
