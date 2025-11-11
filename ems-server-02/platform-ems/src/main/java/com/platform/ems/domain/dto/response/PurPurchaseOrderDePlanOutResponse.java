package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 外部接口 交货计划
 *
 * @author yangqz
 * @date 2022-2-21
 */
@Data
@ApiModel
@Accessors(chain = true)
public class PurPurchaseOrderDePlanOutResponse implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细的交货计划明细 sid")
    private Long deliveryPlanSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单明细sid")
    private Long purchaseOrderItemSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "交货日期")
    private Date deliveryDate;

    @ApiModelProperty(value = "计划发货量")
    private BigDecimal planQuantity;

    @ApiModelProperty(value = "收货方类型")
    private String receiverOrgType;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方sid")
    private Long receiverOrg;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（供应商sid）")
    private Long vendorSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（客户sid）")
    private Long customerSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（仓库sid）")
    private Long storehouseSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方（店铺sid）")
    private Long shopSid;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;
}
