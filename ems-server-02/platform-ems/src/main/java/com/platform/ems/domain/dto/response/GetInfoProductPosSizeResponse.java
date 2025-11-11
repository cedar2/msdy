package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author olive
 */
@Data
@ApiModel
public class GetInfoProductPosSizeResponse  {
    @ApiModelProperty(value ="部位尺码尺寸信息id")
    private String materialPosSizeSid;
    @ApiModelProperty(value ="sku档案id")
    private String skuSid;
    @ApiModelProperty(value ="sku编码")
    private String skuCode;
    @ApiModelProperty(value ="sku名称")
    private String skuName;
    @ApiModelProperty(value ="尺寸值")
    private BigDecimal sizeValue;
    @ApiModelProperty(value ="备注")
    private String remark;
}
