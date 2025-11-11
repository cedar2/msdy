package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 按款添加对应的明细
 */
@Data
@Accessors(chain = true)
@ApiModel
public class MaterialAddItemResponse {

    @ApiModelProperty(value = "颜色名称")
    private String skuName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "颜色sid")
    private Long skuSid;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "合同交期")
    private Date contractDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "物料sid")
    private Long materialSid;

    @ApiModelProperty(value = "尺码")
    List<MaterialAddSkuResponse> listSku2;

    @ApiModelProperty(value = "颜色")
    List<MaterialAddSkuResponse> listSku1;
}
