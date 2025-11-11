package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * bom更改采购类型
 *
 * @author
 * @date 2021-03-15
 */
@Data
@ApiModel
@Accessors(chain = true)
public class TecBomHeadReportPurchaseRequest {

    @ApiModelProperty(value = "所选择行的sid")
    private List<TecBomHeadReportSidRequest> sidList;

    @ApiModelProperty(value = "采购类型")
    private String purchaseType;
}
