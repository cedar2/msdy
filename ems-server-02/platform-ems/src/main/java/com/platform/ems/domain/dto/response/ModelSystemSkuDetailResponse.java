package com.platform.ems.domain.dto.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 版型档案尺码详情响应
 * @author cwp
 * @date 2021-03-16
 */
@Data
@ApiModel
@Accessors( chain = true)
public class ModelSystemSkuDetailResponse {

    @ApiModelProperty(value = "尺码sid")
    private String skuSid;

    @ApiModelProperty(value = "尺码名称")
    private String skuName;

    @ApiModelProperty(value = "尺码值")
    private String sizeValue;

}
