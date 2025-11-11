package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 外部系统接口-采购订单状态值修改
 *
 */
@Data
@ApiModel
public class PurPurchaseOrderHandleRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单编码")
    private Long purchaseOrderCode;

    @ApiModelProperty(value = "处理状态")
    private String handleStatus;
}
