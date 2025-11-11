package com.platform.ems.domain.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 外部接口 交货单主表
 *
 * @author yangqz
 * @date 2022-2-21
 */
@Data
@ApiModel
@Accessors(chain = true)
public class DelDeliveryNoteOutResponse implements Serializable {

    @ApiModelProperty(value = "租户 ID")
    private String clientId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "系统自增长ID-交货单")
    private Long deliveryNoteSid;

    @ApiModelProperty(value = "租户名称")
    private String clientName;

    @ApiModelProperty(value = "供方送货单号")
    private String supplierDeliveryCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "单据日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss:sss")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "配送类型编码")
    private String shipmentType;

    @ApiModelProperty(value = "配送方式名称")
    private String shipmentTypeName;

    @ApiModelProperty(value = "收货方类型")
    private String receiverOrgType;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "预计到货日期")
    private Date expectedArrivalDate;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单sid")
    private Long purchaseOrderSid;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "采购订单号")
    private Long purchaseOrderCode;

    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @ApiModelProperty(value = "货运方名称")
    private String carrierName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "收货方sid")
    private Long receiverOrg;

    @ApiModelProperty(value = "收货方名称")
    private String receiverOrgName;

    @ApiModelProperty(value = "收货方编码")
    private String receiverOrgCode;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @Excel(name = "收货人联系电话")
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @ApiModelProperty(value = "交货单明细")
    List<DelDeliveryNoteItemOutResponse> itemList;
}
