package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * bom物料替换 所选sid
 *
 * @author
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadReportExSidRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomItemSid;

    private Integer itemNum;

}
