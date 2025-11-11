package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * bom物料替换-校验
 *
 * @author
 * @date
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadExChangeJudgeRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    private List<Long> materialSidList;

    @ApiModelProperty(value = "物料编码sid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomMaterialSid;

    @ApiModelProperty(value = "bomSID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bomSid;

    @ApiModelProperty(value = "物料编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private String bomMaterialCode;
}
