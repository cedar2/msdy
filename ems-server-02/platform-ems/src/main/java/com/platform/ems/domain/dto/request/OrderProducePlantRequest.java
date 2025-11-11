package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设置负责生产工厂
 *
 */
@Data
@ApiModel
public class OrderProducePlantRequest {

    @ApiModelProperty(value = "所选行sid")
    private Long[] SidList;

    @ApiModelProperty(value = "负责工厂sid")
    private Long producePlantSid;
}
