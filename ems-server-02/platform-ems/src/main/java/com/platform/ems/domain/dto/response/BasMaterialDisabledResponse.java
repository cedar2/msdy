package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品停用报错提示
 *
 */
@Data
@ApiModel
@Accessors(chain = true)
public class BasMaterialDisabledResponse {


    @ApiModelProperty(value = "商品编码")
    private String materialCode;

    @ApiModelProperty(value = "我司样衣号")
    private String sampleCodeSelf;

    @ApiModelProperty(value = "报错信息")
    private String msg;
}
