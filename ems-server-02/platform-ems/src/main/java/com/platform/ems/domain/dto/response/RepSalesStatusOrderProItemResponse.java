package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 销售状况-销售同比-各渠道数据
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class RepSalesStatusOrderProItemResponse {

    @ApiModelProperty(value = "销售渠道名称")
    private String businessChannelName;

    @ApiModelProperty(value = "数据")
    private List<RepSalesStatusOrderProItemCodeResponse>  dateItemList;

}
