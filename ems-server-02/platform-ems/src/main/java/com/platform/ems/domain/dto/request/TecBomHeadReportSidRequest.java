package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadReportSidRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomSid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSid;
}
