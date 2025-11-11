package com.platform.ems.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 多订单出入库点选择明细行添加时调用的接口，模仿普通入库的下一步接口参数
 */
@Data
@ApiModel
@Accessors(chain = true)
public class InvInventoryDocumentOrders {

    @ApiModelProperty(value = "订单号")
    private String[] codeList;

    @ApiModelProperty(value = "订单明细sid")
    private Long[] itemSidList;

    @ApiModelProperty(value = "关联单据类别编码")
    private String referDocCategory;

    @ApiModelProperty(value = "出入库类型")
    private String type;

    @ApiModelProperty(value = "作业类型")
    private String movementType;

    @ApiModelProperty(value = "库存凭证单据类别编码")
    private String documentCategory;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "款备注")
    private String productCodes;

}
