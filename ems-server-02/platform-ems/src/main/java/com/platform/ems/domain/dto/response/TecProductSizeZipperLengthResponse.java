package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商品所用拉链对象 尺码组
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecProductSizeZipperLengthResponse  implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品编码的SKU档案sid")
    private Long productSkuSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料的SKU档案sid")
    private Long materialSkuSid;
}
