package com.platform.ems.domain.dto.response;

import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.SalSalesOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 按款添加对应的相应实体
 */
@Data
@Accessors(chain = true)
@ApiModel
public class MaterialAddResponse {

    @ApiModelProperty(value = "主表尺码")
    List<String> headList;

    @ApiModelProperty(value = "明细")
    List<MaterialAddItemResponse> itemList;

    @ApiModelProperty(value ="商品条码sid数组")
    private Long[] materialBarcodeSidList;

    @ApiModelProperty(value ="甲供料方式")
    private String rawMaterialMode;

    @ApiModelProperty(value ="类型 1采购 2销售")
    private String type;

    @ApiModelProperty(value ="客户")
    private Long customerSid;

    @ApiModelProperty(value ="公司sid")
    private Long companySid;

    @ApiModelProperty(value ="供应商")
    private Long vendorSid;

    @ApiModelProperty(value ="销售/采购模式")
    private String mode;

    private List<OrderItemFunResponse> orderItemList;

    @ApiModelProperty(value = "订单号")
    private String code;
}
