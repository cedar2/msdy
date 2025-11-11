package com.platform.ems.domain.dto.request;

import com.platform.common.annotation.Excel;
import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.SalSalesOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author c
 */
@ApiModel
@Data
public class BasSaleOrderRequest {

    @NotNull(message = "商品条码不能为空")
    @ApiModelProperty(value ="商品条码sid数组")
    private Long[] materialBarcodeSidList;

    @ApiModelProperty(value ="物料包sids 数组")
    private Long[] materialPackageSids;

    @NotNull(message = "甲供料方式不能为空")
    @ApiModelProperty(value ="甲供料方式")
    private String rawMaterialMode;

    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value ="类型 1采购 2销售")
    private String type;

    private Long customerSid;

    @NotNull(message = "公司sid不能为空")
    private Long companySid;

    private Long vendorSid;

    List<SalSalesOrderItem> salesOrderItemsList;

    List<PurPurchaseOrderItem> purPurchaseOrderItemList;

    @NotNull(message = "模式不能为空")
    @ApiModelProperty(value ="销售/采购模式")
    private String mode;

    @ApiModelProperty(value ="是否跳过单位换算比例校验-Y：跳过")
    private String isSkipUitConversionRate;

}
