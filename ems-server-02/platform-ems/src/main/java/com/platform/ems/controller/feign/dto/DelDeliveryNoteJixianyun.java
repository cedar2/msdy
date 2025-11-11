package com.platform.ems.controller.feign.dto;

import com.platform.ems.domain.DelDeliveryNoteItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel
public class DelDeliveryNoteJixianyun {

    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @NotBlank(message = "销售订单号不能为空")
    @ApiModelProperty(value = "销售订单号code（仅单订单时，存值）")
    private String salesOrderCode;

    @NotBlank(message = "业务类型不能为空")
    @ApiModelProperty(value = "业务类型（数据字典的键值）")
    private String businessType;

    @NotBlank(message = "单据日期不能为空")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @ApiModelProperty(value = "仓库名称")
    private String storehouseName;

    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @NotBlank(message = "配送方式不能为空")
    @ApiModelProperty(value = "配送方式名称")
    private String shipmentTypeName;

    @NotBlank(message = "收货省份不能为空")
    @ApiModelProperty(value = "收货人地址-省(名称)")
    private String consigneeAddrProvince;

    @NotBlank(message = "收货城市不能为空")
    @ApiModelProperty(value = "收货人地址-市(名称)")
    private String consigneeAddrCity;

    @NotBlank(message = "收货人联系方式不能为空")
    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    @NotBlank(message = "收货地址不能为空")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @NotBlank(message = "收货人不能为空")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "外围系统销售发货单号(极限云)")
    private String otherSystemDeliveryNoteCode;

    @ApiModelProperty(value = "交货单-明细对象")
    private List<DelDeliveryNoteItem> delDeliveryNoteItemList;


}
