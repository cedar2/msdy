package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售状况-销售趋势
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class RepSalesStatusOrderTotalTrend {

    @ApiModelProperty(value = "数据")
    private List<RepSalesStatusOrderItemTrendResponse> dataList;

    @ApiModelProperty(value = "x轴数据")
    private List<String> xList;

    @ApiModelProperty(value = "y轴数据")
    private List<String> yList;
}
