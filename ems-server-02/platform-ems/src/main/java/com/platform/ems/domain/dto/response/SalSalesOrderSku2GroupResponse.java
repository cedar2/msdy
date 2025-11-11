package com.platform.ems.domain.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 销售订单明细汇总-尺码（表头）
 *
 * @author yangqz
 * @date 2021-7-12
 */
@Data
@ApiModel
@Accessors(chain = true)
public class SalSalesOrderSku2GroupResponse {

    @ApiModelProperty(value = "尺码组名称")
    private String sku2GroupName;

    @ApiModelProperty(value = "尺码组名称")
    private List<String> sku2NameList;
}
