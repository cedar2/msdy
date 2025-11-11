package com.platform.ems.domain.dto.response;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasBomMaterialSkuResponse {


    @Excel(name = "系统ID-物料SKU信息")
    @ApiModelProperty(value = "系统ID-物料SKU信息")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSkuSid;

    @ApiModelProperty(value = "系统ID-物料档案")
    private String materialSid;

    @ApiModelProperty(value = "系统ID-SKU档案")
    private String skuSid;

    @ApiModelProperty(value = "sku类型")
    private String skuType;

    @ApiModelProperty(value = "SKU名称")
    private String skuName;
}
