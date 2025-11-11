package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentCodeRequest {

    @ApiModelProperty(value = "单据类别编码")
    private String referDocCategory;

    @ApiModelProperty(value = "业务单号")
    private String referDocumentCode;


}
