package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ApiModel
@Accessors(chain = true)
public class BasBomColorRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomSid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long sku1Sid;

    @JsonSerialize(using = ToStringSerializer.class)
    private String sku1Name;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSku1Sid;

    private String bomMaterialSku1Name;
}
