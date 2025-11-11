package com.platform.ems.controller.feign.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.ems.domain.SalSalesOrderItem;
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
public class SalSalesOrderJixianyun {

    @ApiModelProperty(value = "货运单号")
    private String carrierNoteCode;

    @NotBlank(message = "客户编码不能为空")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @NotBlank(message = "单据类型编码不能为空")
    @ApiModelProperty(value = "单据类型编码")
    private String documentType;

    @NotBlank(message = "业务类型编码不能为空")
    @ApiModelProperty(value = "业务类型编码")
    private String businessType;

    @ApiModelProperty(value = "产品季名称")
    private String productSeasonName;

    @ApiModelProperty(value = "库位编码")
    private String locationCode;

    @ApiModelProperty(value = "收货人地址-省(名称)")
    private String consigneeAddrProvince;

    @ApiModelProperty(value = "收货人地址-市(名称)")
    private String consigneeAddrCity;

    @ApiModelProperty(value = "收货人联系电话")
    private String consigneePhone;

    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @NotBlank(message = "销售员不能为空")
    @ApiModelProperty(value = "销售员")
    private String salePersonName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private Date documentDate;

    @NotBlank(message = "客供料方式不能为空")
    @ApiModelProperty(value = "供料方式")
    private String rawMaterialMode;

    @NotBlank(message = "销售模式不能为空")
    @ApiModelProperty(value = "销售模式")
    private String saleMode;

    @NotBlank(message = "销售渠道不能为空")
    @ApiModelProperty(value = "业务渠道/销售渠道")
    private String businessChannel;

    @NotBlank(message = "销售合同号不能为空")
    @ApiModelProperty(value = "销售合同号")
    private String saleContractCode;

    @ApiModelProperty(value = "外围系统销售订单号(极限云)")
    private String otherSystemSaleOrder;

    @ApiModelProperty(value = "销售订单-明细对象")
    private List<SalSalesOrderItem> salSalesOrderItemList;

}
