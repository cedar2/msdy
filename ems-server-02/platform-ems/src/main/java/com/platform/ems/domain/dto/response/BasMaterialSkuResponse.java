package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors( chain = true)
public class BasMaterialSkuResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku1Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuSid;
}
