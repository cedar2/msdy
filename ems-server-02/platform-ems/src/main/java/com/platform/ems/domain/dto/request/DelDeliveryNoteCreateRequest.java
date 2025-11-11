package com.platform.ems.domain.dto.request;

import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 交货单创建销售订单
 *
 * @author yangqz
 * @date 2021-7-22
 */
@Data
@ApiModel
@Accessors(chain = true)
public class DelDeliveryNoteCreateRequest {

    @ApiModelProperty(value = "选择行sid")
    private  List<Long> deliveryNoteSidList;

    @ApiModelProperty(value = "物料类别编码")
    private String materialCategory;
}
