package com.platform.ems.domain.dto.response.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 物料/商品合格证洗唛-商品SKU实测成分对象 BasMaterialSkuComponentExternal
 *
 * @author chenkaiwen
 * @date 2022-02-23
 */
@Data
@Accessors(chain = true)
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasMaterialSkuComponentExternal {

    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "商品档案sid")
    private Long productSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "颜色sid")
    private Long skuColorSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "颜色编码")
    private String skuColorCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "颜色名称")
    private String skuColorName;

    @ApiModelProperty(value = "实测成分")
    private String actualComponent;

    @ApiModelProperty(value = "实测成分（面料）")
    private String componentOutFabric;

    @ApiModelProperty(value = "实测成分（里料）")
    private String componentInFabric;

    @ApiModelProperty(value = "实测成分（填充物）")
    private String componentPadding;

    @ApiModelProperty(value = "下装实测成分（面料）")
    private String bottomsComponentOutFabric;

    @ApiModelProperty(value = "实测成分（里料）")
    private String bottomsComponentInFabric;

    @ApiModelProperty(value = "下装实测成分（填充物）")
    private String bottomsComponentPadding;

    @ApiModelProperty(value = "下装实测成分")
    private String bottomsActualComponent;

}
