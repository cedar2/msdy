package com.platform.ems.domain.dto.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: bom尺码拉链长度sku行
 * @author: yang
 * @date: 2021-07-20
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomSizeSkuRequest implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统ID-物料BOM组件明细")
    private Long bomItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "bom商品的尺码")
    private Long skuBomSid;

    @ApiModelProperty(value = "bom商品的尺码的名称")
    private String skuBomName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "上级商品SKU档案")
    private Long skuSid;

    @ApiModelProperty(value = "上级商品SKU类型名称")
    private String skuName;

    @ApiModelProperty(value = "上级商品SKU类型编码")
    private String skuType;

}
