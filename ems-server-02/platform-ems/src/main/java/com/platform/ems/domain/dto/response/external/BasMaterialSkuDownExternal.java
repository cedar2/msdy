package com.platform.ems.domain.dto.response.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 物料/商品合格证洗唛-商品SKU羽绒充绒量对象 BasMaterialSkuDownExternal
 *
 * @author chenkaiwen
 * @date 2022-02-23
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialSkuDownExternal {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品档案sid")
    private Long productSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "尺码sid")
    private Long skuSizeSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "尺码编码")
    private String skuSizeCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "尺码名称")
    private String skuSizeName;

    @ApiModelProperty(value = "克重")
    private String gramWeight;
}
