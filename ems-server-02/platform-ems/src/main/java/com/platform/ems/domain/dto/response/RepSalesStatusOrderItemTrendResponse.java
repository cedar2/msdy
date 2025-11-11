package com.platform.ems.domain.dto.response;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售状况-销售趋势-各渠道数据
 *
 * @author yangqz
 * @date
 */
@Data
@ApiModel
public class RepSalesStatusOrderItemTrendResponse {

    @ApiModelProperty(value = "销售渠道code")
    private String businessChannelCode;

    @ApiModelProperty(value = "销售渠道名称")
    private String businessChannelName;

    @ApiModelProperty(value = "数据")
    private List<BigDecimal> dataList;

}
