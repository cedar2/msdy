package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BasMaterialDropDown {

    /**
     * 客户端口号
     */
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-物料档案
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    /**
     * 物料（商品/服务）名称
     */
    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

}
