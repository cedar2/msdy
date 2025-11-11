package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 计薪量申报-按款显示的返回实体 - 商品道序明细
 *
 * @author chenkw
 * @date 2022-07-28
 */
@Data
@Accessors(chain = true)
@ApiModel
public class PayProcessStepCompleteTableStepResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "道序sid")
    private Long processStepSid;

    @ApiModelProperty(value = "道序编码")
    private String processStepCode;

    @ApiModelProperty(value = "道序名称")
    private String processStepName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "道序行sid")
    private Long processStepItemSid;

    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

}
