package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 发货单明细报表对象请求实体
 *
 * @author yangqz
 * @date 2021-7-13
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasBomCopyRequest implements Serializable {

    @ApiModelProperty(value = "SKU的Sid")
    private Long sku1Sid;

    @ApiModelProperty(value = "SKU的name")
    private String sku1Name;

    /** 物料编码 */
    @ApiModelProperty(value = "商品编码")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "商品编码")
    private String materialCode;
}
