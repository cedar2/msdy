package com.platform.ems.domain.dto.response;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasBomMaterialResponse {

    @ApiModelProperty(value = "系统ID-物料档案")
    private String bomItemSid;

    @ApiModelProperty(value = "系统ID-物料档案")
    private String bomSid;

    @ApiModelProperty(value = "SKU的Sid")
    private Long skuSid;

    @ApiModelProperty(value = "SKU的name")
    private String skuName;

    private int num;
}
