package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 导入报错信息
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class CommonErrMsgResponse {

    @ApiModelProperty(value = "行号")
    private  int itemNum;

    @ApiModelProperty(value = "序号")
    private BigDecimal sort;

    @ApiModelProperty(value = "单号")
    private  Long  code;

    @ApiModelProperty(value = "报错信息")
    private  String  msg;

    @ApiModelProperty(value = "数据")
    private  Object date;
}
