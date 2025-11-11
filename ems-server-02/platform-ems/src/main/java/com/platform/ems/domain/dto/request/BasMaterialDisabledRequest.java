package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品停用传参实体
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialDisabledRequest {

    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "商品sid")
    private Long materialSid;

    @ApiModelProperty(value = "商品类别")
    private String materialCategory;


}
