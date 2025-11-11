package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 销售价导入报错信息
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class salSalePriceErrMsgResponse {

    @ApiModelProperty(value = "行号")
    private  int itemNum;

    @ApiModelProperty(value = "报错信息")
    private  String  msg;
}
